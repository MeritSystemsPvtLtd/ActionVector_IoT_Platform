package com.meritsystems.core.log;

import java.io.Serializable;

/**
 * @author shashi Logging interface
 */
public interface Logger extends Serializable {

    /**
     * While entering the method
     *
     * @param methodName
     */
    public void entering(String methodName);

    /**
     * While entering the method
     *
     * @param methodName
     * @param params
     */
    public void entering(String methodName, Object[] params);

    /**
     * While entering the method
     *
     * @param methodName
     * @param param
     */
    public void entering(String methodName, Object param);

    /**
     * While exiting the method
     *
     * @param methodName
     */
    public void exiting(String methodName);

    /**
     * While exiting the method
     *
     * @param methodName
     * @param returnParam
     */
    public void exiting(String methodName, Object returnParam);

    /**
     * Logging of info
     *
     * @param message
     */
    public void info(String message);

    /**
     * Debug information
     *
     * @param message
     */
    public void debug(String message);

    /**
     * Log the warning
     *
     * @param message
     */
    public void warn(String message);

    /**
     * Fatal messages
     *
     * @param methodName
     * @param message
     */
    public void error(String methodName, String message);

    /**
     * Severe messages
     *
     * @param exception
     * @param methodName
     * @param message
     */
    public void severe(Throwable exception, String methodName, String message);
}
