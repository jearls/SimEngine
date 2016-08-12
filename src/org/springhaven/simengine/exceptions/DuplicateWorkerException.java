/**
 * 
 */
package org.springhaven.simengine.exceptions;

/**
 * @author Johnson
 *
 */
public class DuplicateWorkerException extends Exception {
    public static final long serialVersionUID = 1L;

    /**
     * 
     */
    public DuplicateWorkerException() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public DuplicateWorkerException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public DuplicateWorkerException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public DuplicateWorkerException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public DuplicateWorkerException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }
}
