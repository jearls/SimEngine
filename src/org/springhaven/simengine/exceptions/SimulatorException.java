/**
 * 
 */
package org.springhaven.simengine.exceptions;

/**
 * @author Johnson
 *
 */
public class SimulatorException extends Exception {
    public static final long serialVersionUID = 1L;
    
    public SimulatorException() {
        super();
    }
    public SimulatorException(String msg) {
        super(msg);
    }
    public SimulatorException(Throwable t) {
        super(t);
    }
    public SimulatorException(String msg, Throwable t) {
        super(msg, t);
    }
}
