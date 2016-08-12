/**
 * 
 */
package org.springhaven.simengine.exceptions;

/**
 * @author Johnson
 *
 */
public class NoSuchStatisticException extends SimulatorException {
    public static final long serialVersionUID = 1L;

    /**
     * 
     */
    public NoSuchStatisticException() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msg
     */
    public NoSuchStatisticException(String msg) {
        super(msg);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param t
     */
    public NoSuchStatisticException(Throwable t) {
        super(t);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msg
     * @param t
     */
    public NoSuchStatisticException(String msg, Throwable t) {
        super(msg, t);
        // TODO Auto-generated constructor stub
    }
}
