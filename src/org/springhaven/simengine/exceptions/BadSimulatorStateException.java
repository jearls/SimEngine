/**
 * 
 */
package org.springhaven.simengine.exceptions;

public class BadSimulatorStateException extends SimulatorException {
    public static final long serialVersionUID = 1L;
    public BadSimulatorStateException() {
        super();
    }
    public BadSimulatorStateException(String msg) {
        super(msg);
    }
    public BadSimulatorStateException(Throwable t) {
        super(t);
    }
    public BadSimulatorStateException(String msg, Throwable t) {
        super(msg, t);
    }
}
