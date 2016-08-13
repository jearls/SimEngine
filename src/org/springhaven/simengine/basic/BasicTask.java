/**
 * 
 */
package org.springhaven.simengine.basic;

import java.util.LinkedList;
import java.util.concurrent.Phaser;
import org.springhaven.simengine.Worker;

/**
 * @author Johnson
 *
 */
public class BasicTask implements Runnable {
    public static final long serialVersionUID = 1L;

    BasicSimulator                sim;
    Phaser                   frameStart, frameEnd;
    Phaser                   phaseStart, phaseEnd;
    boolean                  stopping;
    boolean                  stopped;

    /**
     * @param sim
     *            the simulator controlling this task
     * @param frameStart
     *            the phaser synchronizing this task to the simulator
     * @param phaseStart
     *            the phaser synchronizing the tasks between work phases
     */
    public BasicTask(BasicSimulator sim, Phaser frameStart, Phaser frameEnd, Phaser phaseStart, Phaser phaseEnd) {
        this.sim = sim;
        this.frameStart = frameStart;
        this.frameEnd = frameEnd;
        this.phaseStart = phaseStart;
        this.phaseEnd = phaseEnd;
        this.stopping = false;
        this.stopped = false;
    }

    public void stop() {
        this.stopping = true;
    }

    public boolean getStopped() {
        return this.stopped;
    }

    /**
     * Run the task: The frame loop is one clock tick. Synchronize to the
     * simulator at the beginning and end of the frame. After synchronizing,
     * check if the `in` queue is empty. If so, stop the task. Within the frame
     * loop, start another loop on the phase number, continuing as long as the
     * `in` queue is not empty. Synchronize to the other tasks on the phase
     * synchronizer at the beginning and end of the phase. Within the phase
     * loop, pull a worker out of the simulator's `in` queue and process it for
     * the current frame. If it returns `true`, add it to a local `next` queue
     * or the simulator `out` queue, depending on whether or not it has reached
     * its requested number of phases. After synchronizing the end of the phase,
     * loop through the workers (if any) in the `next` queue and move them back
     * to the `in` queue.
     */
    @Override
    public void run() {
        // frame loop
        LinkedList<String> nextPhase = new LinkedList<String>();
        while (!this.stopping) {
            // synchronize at start of frame loop
            this.frameStart.arriveAndAwaitAdvance();
            if (this.sim.inQ.isEmpty()) {
                break;
            }
            // phase loop
            for (int phase = 1; !(this.sim.inQ.isEmpty()); phase += 1) {
                // synchronize worker tasks at start of phase loop
                this.phaseStart.arriveAndAwaitAdvance();
                // process workers loop
                String workerName;
                while ((workerName = this.sim.inQ.poll()) != null) {
                    Worker worker = this.sim.workers.get(workerName);
                    // run the worker. If it returns `true` (meaning it wants to
                    // keep running), add it to either the `nextPhase` queue or
                    // the simulator's `out` queue, depending on whether or not
                    // it has wants to run additional phases.
                    boolean keepWorking;
                    try {
                        keepWorking = worker.tick(phase);
                    } catch (Exception e) {
                        keepWorking = false;
                        this.sim.log.fatal("Worker " + workerName + " caught exception; terminating worker!", e);
                    }
                    if (keepWorking) {
                        if (worker.getPhaseCount() > phase) {
                            nextPhase.add(workerName);
                        } else {
                            this.sim.outQ.add(workerName);
                        }
                    } else {
                        // the worker returned false, so move it to the `gone`
                        // queue
                        this.sim.goneQ.add(workerName);
                    }
                }
                // synchronize worker tasks at end of phase loop
                this.phaseEnd.arriveAndAwaitAdvance();
                // move workers from `nextPhase` queue back to `in` queue
                while ((workerName = nextPhase.poll()) != null) {
                    this.sim.inQ.add(workerName);
                }
            }
            // synchronize at end of frame loop
            this.frameEnd.arriveAndAwaitAdvance();
            // the simulator will do between-frame processing here...
        }
    }
}
