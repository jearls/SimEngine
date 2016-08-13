/**
 * 
 */
package org.springhaven.simengine;

/**
 * @author Johnson
 *
 */
public interface ClockObserver {
    public void clockInitialized(Clock clock);
    public void clockTicked(Clock clock);
    public void clockFinished(Clock clock);
    public void longStatisticChanged(Clock clock, String statistic, long newValue);
    public void doubleStatisticChanged(Clock clock, String statistic, double newValue);
}
