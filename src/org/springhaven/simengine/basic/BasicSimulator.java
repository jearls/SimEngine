/**
 * 
 */
package org.springhaven.simengine.basic;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springhaven.simengine.Worker;
import org.springhaven.simengine.exceptions.BadSimulatorStateException;
import org.springhaven.simengine.exceptions.DuplicateWorkerException;
import org.springhaven.simengine.exceptions.NoSuchWorkerException;

/**
 * A basic simulator engine.
 * 
 * @see BasicSimulator
 * @author Johnson Earls
 * @version 0.0.0
 */
public class BasicSimulator implements org.springhaven.simengine.Simulator, Runnable {
    public static final long                    serialVersionUID = 1L;

    protected ConcurrentHashMap<String, Worker> workers;
    protected ConcurrentLinkedQueue<String>     inQ, outQ, newQ, goneQ;
    protected AtomicInteger                     state;
    protected BasicClock                             clock;
    protected Log                               log              = LogFactory.getLog(BasicSimulator.class);
    protected Phaser                            frameStart, frameEnd;
    protected Phaser                            phaseStart, phaseEnd;
    protected int                               maxTaskCount;
    protected BasicTask[]                            tasks;

    /**
     * Create a new simulator with a specified number of worker tasks.
     * 
     * @param maxTaskCount
     *            the number of worker tasks to use
     */
    public BasicSimulator(int maxTaskCount) {
        this.maxTaskCount = maxTaskCount;
        workers = new ConcurrentHashMap<String, Worker>();
        inQ = new ConcurrentLinkedQueue<String>();
        outQ = new ConcurrentLinkedQueue<String>();
        newQ = new ConcurrentLinkedQueue<String>();
        state = new AtomicInteger(STOPPED);
        clock = new BasicClock();
    }

    /**
     * Create a new simulator with a single worker task.
     */
    public BasicSimulator() {
        this(1);
    }

    /**
     * @return the current state of the simulator
     * @see org.springhaven.simengine.Simulator#getState()
     */
    @Override
    public int getState() {
        return this.state.get();
    }

    /**
     * @return <b>true</b> if the simulator has any workers - this result is
     *         <b>undefined</b> from any state other than <b>STOPPED</b>
     * @see org.springhaven.simengine.Simulator#hasWorkers()
     */
    @Override
    public boolean hasWorkers() {
        return !this.workers.isEmpty();
    }

    /**
     * @return an enumeration of the names of all current workers
     * @see org.springhaven.simengine.Simulator#getWorkerNames()
     */
    @Override
    public Enumeration<String> getWorkerNames() {
        return this.workers.keys();
    }

    /**
     * @param name
     *            the name of the worker to return
     * @return the named worker
     * @throws NoSuchWorkerException
     *             if the named worker does not exist
     * @see org.springhaven.simengine.Simulator#getWorker(java.lang.String)
     */
    @Override
    public Worker getWorker(String name) throws NoSuchWorkerException {
        Worker worker = this.workers.get(name);
        if (worker == null) {
            throw new NoSuchWorkerException(name);
        }
        return worker;
    }

    /**
     * @param name
     *            the name of the new worker
     * @param worker
     *            the worker to add to the simulation
     * @throws DuplicateWorkerException
     *             if the named worker already exists
     * @throws BadSimulatorStateException
     *             if the simulator's current state does not allow workers to be
     *             added.
     * @see org.springhaven.simengine.Simulator#addWorker(java.lang.String,
     *      org.springhaven.simengine.Worker)
     */
    @Override
    public void addWorker(String name, Worker worker) throws DuplicateWorkerException, BadSimulatorStateException {
        if (this.state.get() != STOPPED && this.state.get() != RUNNING) {
            throw new BadSimulatorStateException("Can only add workers in STOPPED or RUNNING states");
        }
        if (this.workers.containsKey(name)) {
            throw new DuplicateWorkerException(name);
        }
        this.workers.put(name, worker);
        // add the new worker to the `new` queue for simulation processing
        this.newQ.add(name);
    }

    /**
     * @param name
     *            the name of the worker to remove
     * @return the worker who was removed
     * @throws NoSuchWorkerException
     *             if the named worker does not exist
     * @throws BadSimulatorStateException
     *             if the simulator's current state does not allow workers to be
     *             removed.
     * @see org.springhaven.simengine.Simulator#removeWorker(java.lang.String)
     */
    @Override
    public Worker removeWorker(String name) throws NoSuchWorkerException, BadSimulatorStateException {
        if (this.state.get() != STOPPED && this.state.get() != RUNNING) {
            throw new BadSimulatorStateException("Can only remove workers in STOPPED or RUNNING states");
        }
        if (this.workers.containsKey(name)) {
            // Remove the worker from any of the queues it might be in
            this.inQ.remove(name);
            this.outQ.remove(name);
            this.newQ.remove(name);
            this.goneQ.remove(name);
            // Remove the worker from the workers set
            return this.workers.remove(name);
        } else {
            throw new NoSuchWorkerException(name);
        }
    }

    /**
     * Transitions the simulator to the <b>STARTING</b> state
     * 
     * @see org.springhaven.simengine.Simulator#start()
     */
    @Override
    public void start() {
        this.state.set(STARTING);
        initializeSimulator();
        this.state.set(RUNNING);
        startSimulator();
    }

    /**
     * Transitions the simulator to the <b>STOPPING</b> state
     * 
     * @see org.springhaven.simengine.Simulator#stop()
     */
    @Override
    public void stop() {
        this.state.set(STOPPING);
    }

    /**
     * @return <b>true</b> if the simulator is in the <b>STOPPING</b> state
     * @see org.springhaven.simengine.Simulator#isStopping()
     */
    @Override
    public boolean isStopping() {
        return this.state.get() == STOPPING;
    }

    /**
     * @return <b>true</b> if the simulator is in the <b>STOPPED</b> state
     * @see org.springhaven.simengine.Simulator#isStopped()
     */
    @Override
    public boolean isStopped() {
        return this.state.get() == STOPPED;
    }

    /**
     * @return the simulator clock
     * @see org.springhaven.simengine.Simulator#getClock()
     */
    @Override
    public BasicClock getClock() {
        return this.clock;
    }

    /**
     * @return the simulator log
     * @see org.springhaven.simengine.Simulator#getLog()
     */
    @Override
    public Log getLog() {
        return this.log;
    }

    /**
     * Initialize the simulator and all simulation objects.
     */
    void initializeSimulator() {
        this.clock.initialize(this);
        Enumeration<String> e = this.workers.keys();
        while (e.hasMoreElements()) {
            this.newQ.add(e.nextElement());
        }
    }

    ExecutorService threadPool;

    void startSimulator() {
        this.threadPool = Executors.newCachedThreadPool();
        this.frameStart = new Phaser(1 + this.maxTaskCount);
        this.frameEnd = new Phaser(1 + this.maxTaskCount);
        this.phaseStart = new Phaser(this.maxTaskCount);
        this.phaseEnd = new Phaser(this.maxTaskCount);
        this.tasks = new BasicTask[this.maxTaskCount];
        for (int i = 0; i < this.maxTaskCount; i += 1) {
            tasks[i] = new BasicTask(this, this.frameStart, this.frameEnd, this.phaseStart, this.phaseEnd);
            this.threadPool.execute(tasks[i]);
        }
        this.threadPool.execute(this);
    }

    @Override
    public void run() {
        // generic variable used to loop through the queues
        String workerName = null;
        // initialize the clock
        this.clock.initialize(this);
        // loop as lon gas we're in the RUNNING state and there are workers
        while ((this.state.get() == RUNNING) && (!this.workers.isEmpty())) {
            // any workers in the `new` queue need to be initialized and
            // added to the `in` queue
            while ((workerName = this.newQ.poll()) != null) {
                Worker worker = this.workers.get(workerName);
                worker.initialize(this);
                this.inQ.add(workerName);
            }
            // start the frame
            this.frameStart.arriveAndAwaitAdvance();
            // wait for the frame to finish
            this.frameEnd.arriveAndAwaitAdvance();
            // Finish any workers in the `gone` queue
            while ((workerName = this.goneQ.poll()) != null) {
                Worker worker = this.workers.get(workerName);
                worker.finish();
                this.workers.remove(workerName);
            }
            // Move the `out` queue to the `in` queue (by swaping)
            ConcurrentLinkedQueue<String> tempQ = this.outQ;
            this.outQ = this.inQ;
            this.inQ = tempQ;
            // run the Clock
            if (!this.clock.tick()) {
                // the clock told us to stop
                this.stop();
            }
        }
        // we're done:
        // initialize, finish, and remove all workers in the `new` queue
        while ((workerName = this.newQ.poll()) != null) {
            Worker worker = this.workers.get(workerName);
            worker.initialize(this);
            worker.finish();
            this.workers.remove(workerName);
        }
        // finish and remove all workers in the `in` queue
        while ((workerName = this.inQ.poll()) != null) {
            Worker worker = this.workers.get(workerName);
            worker.finish();
            this.workers.remove(workerName);
        }
        // deregister from the frameStart and frameEnd phasers
        this.frameStart.arriveAndDeregister();
        this.frameEnd.arriveAndDeregister();
        // finish the Clock
        this.clock.finish();
        // mark the state as STOPPED
        this.state.set(STOPPED);
        // shut down the thread pool
        this.threadPool.shutdownNow();
    }
}
