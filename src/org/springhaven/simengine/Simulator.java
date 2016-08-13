/**
 * 
 */
package org.springhaven.simengine;

import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.springhaven.simengine.exceptions.BadSimulatorStateException;
import org.springhaven.simengine.exceptions.DuplicateWorkerException;
import org.springhaven.simengine.exceptions.NoSuchWorkerException;

/**
 * <p>
 * The public interface to a simulation engine.
 * </p>
 * 
 * <p>
 * A simulation engine consists of:
 * </p>
 * <ul>
 * <li>A dynamic set of {@link Worker}s that perform the simulated tasks;</li>
 * <li>A state:</li>
 * <dl>
 * <dt><b>STOPPED</b></dt>
 * <dd>when the simulator is not active,</dd>
 * <dt><b>STARTING</b></dt>
 * <dd>when the simulator is starting up for a simulation run,</dd>
 * <dt><b>RUNNING</b></dt>
 * <dd>when the simulator is actively running,</dd>
 * <dt><b>STOPPING</b></dt>
 * <dd>when the simulator is shutting down;</dd>
 * </dl>
 * <li>And a clock that tracks simulation statistics.</li>
 * </ul>
 * 
 * <p>
 * In the <b>STOPPED</b> state, no work is done and the clock is not active.
 * Workers can be freely added or removed from the simulator. When the simulator
 * is started (via the {@link #start()} method), the simulator transitions to
 * the <b>STARTING</b> state.
 * </p>
 * 
 * <p>
 * In the <b>STARTING</b> state, the clock and all workers are initialized. In
 * this state, no modifications can be done to the set of workers. When the
 * clock and all workers are initialized, the simulator transitions to the
 * <b>RUNNING</b> state.
 * </p>
 * 
 * <p>
 * In the <b>RUNNING</b> state, time is divided into <b>frames</b>. One frame
 * consists of the simulator calling on each worker to do its required work;
 * when all workers have done their work for a frame, the frame ends, the clock
 * is updated, and (if there are any workers left and the simulator has not been
 * stopped) a new frame begins.
 * </p>
 * 
 * <p>
 * In this state, workers can be added, but will not be initialized until the
 * end of the current frame.
 * </p>
 * 
 * <p>
 * When there are no remaining workers, or when the simulator is stopped
 * externally (via the {@link #stop()} method), the simulator transitions to the
 * <b>STOPPING</b> state.
 * </p>
 * 
 * <p>
 * In the <b>STOPPING</b> state, all workers get cleaned up and removed from the
 * simulator and the clock gets reset; then the simulator transitions back to
 * the <b>STOPPED</b> state.
 * </p>
 * 
 * @author Johnson Earls
 * @version 0.0.0
 */
public interface Simulator {
    /**
     * Returned by {@link #getState()} to indicate the simulator is in the
     * <b>STOPPED</b> state.
     */
    public final int STOPPED  = 0;
    /**
     * Returned by {@link #getState()} to indicate the simulator is in the
     * <b>STARTING</b> state.
     */
    public final int STARTING = 1;
    /**
     * Returned by {@link #getState()} to indicate the simulator is in the
     * <b>RUNNING</b> state.
     */
    public final int RUNNING  = 2;
    /**
     * Returned by {@link #getState()} to indicate the simulator is in the
     * <b>STOPPING</b> state.
     */
    public final int STOPPING = 3;

    /**
     * @return the current state of the simulator
     */
    public int getState();

    /**
     * @return <b>true</b> if the simulator has any workers
     */
    public boolean hasWorkers();

    /**
     * @return an enumeration of the names of all current workers
     */
    public Enumeration<String> getWorkerNames();

    /**
     * @param name
     *            the name of the worker to return
     * @return the named worker
     * @throws NoSuchWorkerException
     *             if the named worker does not exist
     */
    public Worker getWorker(String name) throws NoSuchWorkerException;

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
     */
    public void addWorker(String name, Worker worker) throws DuplicateWorkerException, BadSimulatorStateException;

    /**
     * @param name
     *            the name of the worker to remove
     * @return the worker who was removed
     * @throws NoSuchWorkerException
     *             if the named worker does not exist
     * @throws BadSimulatorStateException
     *             if the simulator's current state does not allow workers to be
     *             removed.
     */
    public Worker removeWorker(String name) throws NoSuchWorkerException, BadSimulatorStateException;

    /**
     * Transitions the simulator to the <b>STARTING</b> state
     */
    public void start();

    /**
     * Transitions the simulator to the <b>STOPPING</b> state
     */
    public void stop();

    /**
     * @return <b>true</b> if the simulator is in the <b>STOPPING</b> state
     */
    public boolean isStopping();

    /**
     * @return <b>true</b> if the simulator is in the <b>STOPPED</b> state
     */
    public boolean isStopped();

    /**
     * @return the simulator clock
     */
    public Clock getClock();

    /**
     * Tell the simulator to use a different clock. Generally, this should only
     * be done when the simulator is <b>STOPPED</b>.
     * 
     * @param clock
     *            the new clock to use.
     * @throws IllegalArgumentException
     *             if the clock is not valid for this simulator.
     * @throws BadSimulatorStateException
     *             if the simulation state does not allow the clock to be
     *             changed.
     */
    public void setClock(Clock clock) throws BadSimulatorStateException, IllegalArgumentException;

    /**
     * @return the simulator log
     */
    public Log getLog();
}
