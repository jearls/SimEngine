/**
 * 
 */
package org.springhaven.simengine;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import org.springhaven.simengine.exceptions.NoSuchStatisticException;

/**
 * @author Johnson
 *
 */
public abstract class AbstractClock implements Clock {
    public static final long                           serialVersionUID = 1L;

    protected ConcurrentHashMap<String, Long>          statsLong        = new ConcurrentHashMap<String, Long>();
    protected ConcurrentHashMap<String, Double>        statsDouble      = new ConcurrentHashMap<String, Double>();

    // I really only want a ConcurrentSet...
    protected ConcurrentHashMap<ClockObserver, Object> observers        =
            new ConcurrentHashMap<ClockObserver, Object>();

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
        this.notifyLongStatisticChanged(statistic, value);
    }
    
    protected void setDoubleStatistic(String statistic, double value) {
        statsDouble.put(statistic, new Double(value));
        this.notifyDoubleStatisticChanged(statistic, value);
    }

    @Override
    public void finish() {
        this.notifyClockFinished();
    }
    
    @Override
    public void initialize(Simulator sim) {
        this.notifyClockInitialized();
    }

    @Override
    public boolean tick() {
        this.notifyClockTicked();
        return true;
    }
    
    protected void notifyLongStatisticChanged(String statistic, long value) {
        Enumeration<ClockObserver> e = observers.keys();
        while (e.hasMoreElements()) {
            ClockObserver o = e.nextElement();
            o.longStatisticChanged(this, statistic, value);
        }
    }

    protected void notifyDoubleStatisticChanged(String statistic, double value) {
        Enumeration<ClockObserver> e = observers.keys();
        while (e.hasMoreElements()) {
            ClockObserver o = e.nextElement();
            o.doubleStatisticChanged(this,  statistic, value);
        }
    }

    protected void notifyClockInitialized() {
        Enumeration<ClockObserver> e = observers.keys();
        while (e.hasMoreElements()) {
            ClockObserver o = e.nextElement();
            o.clockInitialized(this);
        }
    }

    protected void notifyClockTicked() {
        Enumeration<ClockObserver> e = observers.keys();
        while (e.hasMoreElements()) {
            ClockObserver o = e.nextElement();
            o.clockTicked(this);
        }
    }

    protected void notifyClockFinished() {
        Enumeration<ClockObserver> e = observers.keys();
        while (e.hasMoreElements()) {
            ClockObserver o = e.nextElement();
            o.clockFinished(this);
        }
    }

    @Override
    public void addObserver(ClockObserver o) {
        this.observers.put(o,  o);
    }

    @Override
    public int countObservers() {
        return this.observers.size();
    }
    
    @Override
    public void deleteObserver(ClockObserver o) {
        this.observers.remove(o);
    }
    
    @Override
    public void deleteObservers() {
        this.observers.clear();
    }
}
