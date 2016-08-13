/**
 * 
 */
package org.springhaven.simengine.basic;

/**
 * This is a variant of the basic Clock that allows the simulation to run with a
 * limited FPS and a limited frame count.
 * 
 * @author Johnson
 *
 */
public class LimitClock extends BasicClock {
    public static final long serialVersionUID = 1L;
    public static final long UNLIMITED        = -1L;
    protected long           fpsLimit;
    protected long           frameLimit;

    /**
     * Instantiate a clock with the specified limits.
     * 
     * @param fpsLimit
     *            The maximum number of frames to run each second
     * @param frameLimit
     *            The maximum number of frames to run, period
     */
    public LimitClock(long fpsLimit, long frameLimit) {
        super();
        this.fpsLimit = fpsLimit;
        this.frameLimit = frameLimit;
    }

    /**
     * Instantiate a clock with no limits defined.
     */
    public LimitClock() {
        this(UNLIMITED, UNLIMITED);
    }

    /**
     * @return the current limit on frames-per-second, or <b>UNLIMITED</b>
     */
    public long getFpsLimit() {
        return fpsLimit;
    }

    /**
     * Set a new frames-per-second limit.  Pass in <b>UNLIMITED</b> to not have a frames-per-second limit.
     * 
     * @param fpsLimit the new frames-per-second limit
     */
    public void setFpsLimit(long fpsLimit) {
        this.fpsLimit = fpsLimit;
    }

    /**
     * Clear the frames-per-second limit.
     */
    public void clearFpsLimit() {
        setFpsLimit(UNLIMITED);
    }

    /**
     * @return the current limit on simulation frames, or <b>UNLIMITED</b>
     */
    public long getFrameLimit() {
        return frameLimit;
    }

    /**
     * Set a new frame limit.  Pass in <b>UNLIMITED</b> to not have a frame limit.
     * 
     * @param fpsLimit the new frame limit
     */
    public void setFrameLimit(long frameLimit) {
        this.frameLimit = frameLimit;
    }

    /**
     * Clear the frame limit.
     */
    public void clearFrameLimit() {
        setFrameLimit(UNLIMITED);
    }

    /**
     * <p>Call {@link BasicClock#tick()} but enforce the limits:</p>
     * <ul><li>Before calling, if we've met or exceeded
     * the frames-per-second limit, sleep until we move to a new second.</li>
     * <li>After calling, if we've met or exceeded the frames limit, return <b>false</b></li>.
     * <li>Otherwise, return the value returned by Clock's <b>tick</b> method.</li></ul>
     * 
     * @return <b>true</b> if the Simulation should continue, or <b>false</b> if
     *         the Simulation should be terminated.
     * @see org.springhaven.simengine.basic.BasicClock#tick()
     */
    @Override
    public boolean tick() {
        if ((this.fpsLimit != UNLIMITED) && (this.framesPerSecond_accum >= this.fpsLimit)) {
            long partialFrame = (System.currentTimeMillis() - startFrame - lastFrame * 1000L);
            while (partialFrame < 990L) {
                try { Thread.sleep(partialFrame / 2); } catch (InterruptedException e) {}
                partialFrame = (System.currentTimeMillis() - startFrame - lastFrame * 1000L);
            }
        }

        boolean keepRunning = super.tick();

        if ((this.frameLimit != UNLIMITED) && (this.frames_accum >= this.frameLimit)) {
            return false;
        }
        return keepRunning;
    }
}
