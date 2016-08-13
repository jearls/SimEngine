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
public class Task implements Runnable {
    public static final long serialVersionUID = 1L;

    Simulator                sim;
    Phaser                   simSync;
    Phaser                   phaseSync;
    boolean                  stopping;
    boolean                  stopped;

    /**
     * @param sim
     *            the simulator controlling this task
     * @param simSync
     *            the phaser synchronizing this task to the simulator
     * @param phaseSync
     *            the phaser synchronizing the tasks between work phases
     */
    public Task(Simulator sim, Phaser simSync, Phaser phaseSync) {
        this.sim = sim;
        this.simSync = simSync;
        this.phaseSync = phaseSync;
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
            this.simSync.arriveAndAwaitAdvance();
            if (this.sim.in.isEmpty()) {
                break;
            }
            // phase loop
            for (int phase = 1; !(this.sim.in.isEmpty()); phase += 1) {
                // synchronize worker tasks at start of phase loop
                this.phaseSync.arriveAndAwaitAdvance();
                // process workers loop
                // loop infinitely, but break in the middle if we cannot get
                // another worker from the `in` queue.
                while (true) {
                    // fetch the next worker, if any, from the `in` queue
                    String workerName = this.sim.in.poll();
                    if (workerName == null) {
                        break;
                    }
                    Worker worker = this.sim.workers.get(workerName);
                    // run the worker. If it returns `true` (meaning it wants to
                    // keep running), add it to either the `nextPhase` queue or
                    // the simulator's `out` queue, depending on whether or not
                    // it has wants to run additional phases.
                    if (worker.tick(phase)) {
                        if (worker.getPhaseCount() > phase) {
                            nextPhase.add(workerName);
                        } else {
                            this.sim.out.add(workerName);
                        }
                    }
                }
                // synchronize worker tasks at end of phase loop
                this.phaseSync.arriveAndAwaitAdvance();
                // move workers from `nextPhase` queue back to `in` queue
                while (!nextPhase.isEmpty()) {
                    this.sim.in.add(nextPhase.remove());
                }
            }
            // synchronize at end of frame loop
            this.simSync.arriveAndAwaitAdvance();
            // the simulator will do between-frame processing here...
        }
    }
}
