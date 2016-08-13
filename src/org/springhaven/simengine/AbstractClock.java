/**
 * 
 */
package org.springhaven.simengine;

import java.util.concurrent.ConcurrentHashMap;

import org.springhaven.simengine.exceptions.NoSuchStatisticException;

/**
 * @author Johnson
 *
 */
public abstract class AbstractClock implements Clock {
    public static final long serialVersionUID = 1L;

    ConcurrentHashMap<String, Long> statsLong = new ConcurrentHashMap<String, Long>();
    ConcurrentHashMap<String, Double> statsDouble = new ConcurrentHashMap<String, Double>();

    /**
     * @param statistic
     *            the name of the <b>long</b> statistic to query
     * @return the <b>long</b> value of the named statistic
     * @throws NoSuchStatisticException
     *             if the named statistic does not exist (or is not a
     *             <b>long</b> statistic)
     * @see org.springhaven.simengine.Clock#getLongStatistic(java.lang.String)
     */
    @Override
    public long getLongStatistic(String statistic) throws NoSuchStatisticException {
        if (statsLong.contains(statistic)) {
            return statsLong.get(statistic).longValue();
        } else {
            throw new NoSuchStatisticException(statistic);
        }
    }

    /**
     * @param statistic
     *            the name of the <b>double</b> statistic to query
     * @return the <b>double</b> value of the named statistic
     * @throws NoSuchStatisticException
     *             if the named statistic does not exist (or is not a
     *             <b>double</b> statistic)
     * @see org.springhaven.simengine.Clock#getDoubleStatistic(java.lang.String)
     */
    @Override
    public double getDoubleStatistic(String statistic) throws NoSuchStatisticException {
        if (statsDouble.contains(statistic)) {
            return statsDouble.get(statistic).doubleValue();
        } else {
            throw new NoSuchStatisticException(statistic);
        }
    }

    protected void setLongStatistic(String statistic, long value) {
        statsLong.put(statistic, new Long(value));
    }

    protected void setDoubleStatistic(String statistic, double value) {
        statsDouble.put(statistic, new Double(value));
    }
}
