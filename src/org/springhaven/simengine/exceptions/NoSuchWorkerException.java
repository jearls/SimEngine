/**
 * 
 */
package org.springhaven.simengine.exceptions;

/**
 * @author Johnson
 *
 */
public class NoSuchWorkerException extends SimulatorException {
    public static final long serialVersionUID = 1L;

    /**
     * 
     */
    public NoSuchWorkerException() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msg
     */
    public NoSuchWorkerException(String msg) {
        super(msg);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param t
     */
    public NoSuchWorkerException(Throwable t) {
        super(t);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msg
     * @param t
     */
    public NoSuchWorkerException(String msg, Throwable t) {
        super(msg, t);
        // TODO Auto-generated constructor stub
    }
}
