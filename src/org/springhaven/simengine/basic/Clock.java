/**
 * 
 */
package org.springhaven.simengine.basic;

import org.apache.commons.logging.Log;
import org.springhaven.simengine.Simulator;

/**
 * @author Johnson
 *
 */
public class Clock extends org.springhaven.simengine.AbstractClock {
    public static final long serialVersionUID = 1L;

    Simulator sim;
    Log log;
    
    long frames_accum ;
    long framesPerSecond_accum ;
    long startFrame ;
    long lastFrame ;
    
    /**
     * 
     */
    public Clock() {
    }

    /**
     * Initialize the clock.
     * 
     * @param sim
     *            the Simulation Engine that is controlling the simulation
     * @see org.springhaven.simengine.Clock#initialize(org.springhaven.simengine.Simulator)
     */
    @Override
    public void initialize(Simulator sim) {
        this.sim = sim;
        this.log = sim.getLog();
        frames_accum = 0L ;
        framesPerSecond_accum = 0L ;
        startFrame = System.currentTimeMillis() ;
        lastFrame = 0L ;
    }

    /**
     * Update the clock after completion of a frame.
     * 
     * @return <b>true</b> if the Simulation should continue, or <b>false</b> if
     *         the Simulation should be terminated.
     * @see org.springhaven.simengine.Clock#tick()
     */
    @Override
    public boolean tick() {
        long thisFrame = (System.currentTimeMillis() - startFrame) / 1000L;
        if (thisFrame != lastFrame) {
            lastFrame = thisFrame;
            this.setLongStatistic(STAT_FPS, this.framesPerSecond_accum);
            this.framesPerSecond_accum = 1;
        } else {
            this.framesPerSecond_accum += 1;
        }
        this.frames_accum += 1;
        this.setLongStatistic(STAT_FRAMES, this.frames_accum);
        return true;
    }

    /**
     * Called when the simulation ends. Use this method do any required
     * clean-up.
     * @see org.springhaven.simengine.Clock#finish()
     */
    @Override
    public void finish() {
    }
}
