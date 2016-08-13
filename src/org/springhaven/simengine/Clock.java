/**
 * 
 */
package org.springhaven.simengine;

import org.springhaven.simengine.exceptions.NoSuchStatisticException;

/**
 * <p>
 * The `Clock` manages, or helps to manage, the simulation over time. It
 * maintains numeric statistics that can be queried by external callers. Some
 * Clock implementations may offer varying statistics, but the following are
 * guaranteed:
 * </p>
 * 
 * <table border="1">
 * <tr>
 * <th>Statistic Name</th>
 * <th>Type</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <th>frames</th>
 * <td>Long</td>
 * <td>The number of frames that have been executed so far.</td>
 * </tr>
 * <tr>
 * <th>framesPerSecond</th>
 * <td>Long</td>
 * <td>The number of frames that were executed in the last second.</td>
 * </tr>
 * </table>
 * 
 * @author Johnson Earls
 * @version 0.0.0
 */
public interface Clock {

    public final static String STAT_FRAMES = "frames";
    public final static String STAT_FPS    = "framesPerSecond";

    /**
     * @param statistic
     *            the name of the <b>long</b> statistic to query
     * @return the <b>long</b> value of the named statistic
     * @throws NoSuchStatisticException
     *             if the named statistic does not exist (or is not a
     *             <b>long</b> statistic)
     */
    public long getLongStatistic(String statistic) throws NoSuchStatisticException;

    /**
     * @param statistic
     *            the name of the <b>double</b> statistic to query
     * @return the <b>double</b> value of the named statistic
     * @throws NoSuchStatisticException
     *             if the named statistic does not exist (or is not a
     *             <b>double</b> statistic)
     */
    public double getDoubleStatistic(String statistic) throws NoSuchStatisticException;

    /**
     * Initialize the clock.
     * 
     * @param sim
     *            the Simulation Engine that is controlling the simulation
     */
    public void initialize(Simulator sim);

    /**
     * Update the clock after completion of a frame.
     * 
     * @return <b>true</b> if the Simulation should continue, or <b>false</b> if
     *         the Simulation should be terminated.
     */
    public boolean tick();

    /**
     * Called when the simulation ends. Use this method do any required
     * clean-up.
     */
    public void finish();
}
