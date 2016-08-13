/**
 * 
 */
package org.springhaven.simengine.exceptions;

public class NoSuchStatisticException extends SimulatorException {
    public static final long serialVersionUID = 1L;
    public NoSuchStatisticException() {
        super();
    }
    public NoSuchStatisticException(String msg) {
        super(msg);
    }
    public NoSuchStatisticException(Throwable t) {
        super(t);
    }
    public NoSuchStatisticException(String msg, Throwable t) {
        super(msg, t);
    }
}
