/**
 * 
 */
package org.springhaven.simengine.exceptions;

public class DuplicateWorkerException extends SimulatorException {
    public static final long serialVersionUID = 1L;
    public DuplicateWorkerException() {
        super();
    }
    public DuplicateWorkerException(String message) {
        super(message);
    }
    public DuplicateWorkerException(Throwable cause) {
        super(cause);
    }
    public DuplicateWorkerException(String message, Throwable cause) {
        super(message, cause);
    }
}
