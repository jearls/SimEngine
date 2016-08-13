/**
 * 
 */
package org.springhaven.simengine.basic;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springhaven.simengine.Worker;
import org.springhaven.simengine.exceptions.DuplicateWorkerException;
import org.springhaven.simengine.exceptions.NoSuchWorkerException;

/**
 * A basic simulator engine.
 * 
 * @see Simulator
 * @author Johnson Earls
 * @version 0.0.0
 */
public class Simulator implements org.springhaven.simengine.Simulator {
    public static final long                    serialVersionUID = 1L;

    protected ConcurrentHashMap<String, Worker> workers;
    protected ConcurrentLinkedQueue<String>     in, out, pending;
    protected AtomicInteger                     state;
    protected Clock                             clock;
    protected Log                               log              = LogFactory.getLog(Simulator.class);
    protected Phaser                            simulatorSync;
    protected Phaser                            phaseSync;
    protected int                               maxTaskCount;
    protected Task[]                            tasks;

    /**
     * Create a new simulator with a specified number of worker tasks.
     * 
     * @param maxTaskCount
     *            the number of worker tasks to use
     */
    public Simulator(int maxTaskCount) {
        this.maxTaskCount = maxTaskCount;
        workers = new ConcurrentHashMap<String, Worker>();
        in = new ConcurrentLinkedQueue<String>();
        out = new ConcurrentLinkedQueue<String>();
        pending = new ConcurrentLinkedQueue<String>();
        state = new AtomicInteger(STOPPED);
        clock = new Clock();
    }

    /**
     * Create a new simulator with a single worker task.
     */
    public Simulator() {
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
        if (this.workers.containsKey(name)) {
            return this.workers.get(name);
        } else {
            throw new NoSuchWorkerException(name);
        }
    }

    /**
     * @param name
     *            the name of the new worker
     * @param worker
     *            the worker to add to the simulation
     * @throws DuplicateWorkerException
     *             if the named worker already exists
     * @see org.springhaven.simengine.Simulator#addWorker(java.lang.String,
     *      org.springhaven.simengine.Worker)
     */
    @Override
    public void addWorker(String name, Worker worker) throws DuplicateWorkerException {
        if (this.workers.containsKey(name)) {
            throw new DuplicateWorkerException(name);
        }
        this.workers.put(name, worker);
    }

    /**
     * @param name
     *            the name of the worker to remove
     * @return the worker who was removed
     * @throws NoSuchWorkerException
     *             if the named worker does not exist
     * @see org.springhaven.simengine.Simulator#removeWorker(java.lang.String)
     */
    @Override
    public Worker removeWorker(String name) throws NoSuchWorkerException {
        if (this.workers.containsKey(name)) {
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
    public Clock getClock() {
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
            this.pending.add(e.nextElement());
        }
    }

    void startSimulator() {
        simulatorSync = new Phaser(1 + this.maxTaskCount);
        phaseSync = new Phaser(this.maxTaskCount);
        tasks = new Task[this.maxTaskCount];
        for (int i = 0; i < this.maxTaskCount; i += 1) {
            tasks[i] = new Task(this, this.simulatorSync, this.phaseSync);
        }
    }
}
