/**
 * 
 */
package org.springhaven.simengine;

/**
 * A simulation worker.
 * 
 * A worker goes through three stages: Initialization, Work, Finished. In the
 * Initialization stage, the {@link #initialize} method is called to set up the
 * worker.
 * 
 * In the Work stage, the Worker is called to do its work during each `tick` of
 * the simulation clock.
 * 
 * Work can be done in 1 or more phases, as determined by the return value from
 * {@link #getPhaseCount}.
 * 
 * For each `tick` of the clock, the {@link #tick} method will be called for
 * each required phase, starting at phase 1. If, for any phase, the tick method
 * returns <b>false</b>, the Worker will be immediately removed from the
 * simulation.
 *
 * When the tick method returns false, or when the simulator is stopped
 * externally, the {@link #finish} method will be called so the worker can do
 * any required cleanup.
 * 
 * @author Johnson Earls
 * @version 0.0.0
 */
public interface Worker {
    /**
     * Initialize the worker.
     * 
     * @param sim
     *            the Simulation Engine that is controlling the simulation
     */
    public void initialize(Simulator sim);

    /**
     * @return the number of phases required for the worker to do its work
     *         (<b>Warning</b>: results are undefined if this value changes
     *         between the initial and final call to tick during a clock tick)
     */
    public int getPhaseCount();

    /**
     * Perform the work required.
     * 
     * @param phase
     *            the work phase being called
     * @return <b>true</b> if the Worker should continue working, or
     *         <b>false</b> if the Worker should be removed from the simulation
     */
    public boolean tick(int phase);

    /**
     * Called when the Worker is removed from the simulation. Use this method to
     * do any required clean-up.
     */
    public void finish();
}
