/**
 * 
 */
package org.springhaven.simengine.exceptions;

public class NoSuchWorkerException extends SimulatorException {
    public static final long serialVersionUID = 1L;

    public NoSuchWorkerException() {
        super();
    }
    public NoSuchWorkerException(String msg) {
        super(msg);
    }
    public NoSuchWorkerException(Throwable t) {
        super(t);
    }
    public NoSuchWorkerException(String msg, Throwable t) {
        super(msg, t);
    }
}
