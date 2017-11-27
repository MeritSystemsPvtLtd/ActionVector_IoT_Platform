package com.meritsystems.core;

/**
 * @author shashi Wrapper for our exception
 */
public class ApplicationException extends Exception {

    private static final long serialVersionUID = 5176629630519613139L;

    public ApplicationException() {
        super();
    }

    /**
     * @param message
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * @param message
     */
    public ApplicationException(MetricsEnum message) {
        super(message.getName());
    }

    /**
     * @param exception
     */
    public ApplicationException(Throwable exception) {
        super(exception);
    }

    /**
     * @param exception
     */
    public ApplicationException(Exception exception) {
        super(exception);
    }
}