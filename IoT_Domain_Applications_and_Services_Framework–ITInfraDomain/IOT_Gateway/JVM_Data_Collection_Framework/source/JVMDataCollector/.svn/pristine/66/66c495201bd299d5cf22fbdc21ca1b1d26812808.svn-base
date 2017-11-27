package com.meritsystems.core.log;

/**
 * @author shashi Implementation for log4j
 */
public class Log4jLogger implements Logger {

    private org.apache.log4j.Logger logger = null;

    /**
     * @param classInstance
     */
    public Log4jLogger(Class classInstance) {
        logger = org.apache.log4j.Logger.getLogger(classInstance);
    }

    /* (non-Javadoc)
     * @see com.meritsystems.core.Logger#debug(java.lang.String)
     */
    public void debug(String message) {
        logger.debug(message);

    }

    /* (non-Javadoc)
     * @see com.meritsystems.core.Logger#entering(java.lang.String)
     */
    public void entering(String methodName) {
        logger.info("Entering method " + methodName);

    }


    /* (non-Javadoc)
     * @see com.meritsystems.core.Logger#entering(java.lang.String, java.lang.Object[])
     */
    public void entering(String methodName, Object[] params) {
        String paramsStr = "";

        if (params != null || params.length != 0) {
            for (int i = 0; i < params.length; i++) {
                paramsStr = paramsStr + params[i] + "\n";
            }
        }
        entering(methodName + " " + paramsStr);

    }

    /* (non-Javadoc)
     * @see com.meritsystems.core.Logger#exiting(java.lang.String)
     */
    public void exiting(String methodName) {
        logger.info("Exiting method " + methodName);

    }

    /* (non-Javadoc)
     * @see com.meritsystems.core.Logger#exiting(java.lang.String, java.lang.Object)
     */
    public void exiting(String methodName, Object returnParam) {
        exiting(methodName + " " + returnParam);

    }

    /* (non-Javadoc)
     * @see com.meritsystems.core.Logger#error(java.lang.String, java.lang.String)
     */
    public void error(String methodName, String message) {
        logger.error("Method: " + methodName + " " + message);

    }

    /* (non-Javadoc)
     * @see com.meritsystems.core.Logger#info(java.lang.String)
     */
    public void info(String message) {
        logger.info(message);

    }

    /* (non-Javadoc)
     * @see com.meritsystems.core.Logger#severe(java.lang.Throwable, java.lang.String, java.lang.String)
     */
    public void severe(Throwable exception, String methodName, String message) {
        logger.fatal("Method: " + methodName + " " + message, exception);

    }

    /* (non-Javadoc)
     * @see com.meritsystems.core.Logger#warn(java.lang.String)
     */
    public void warn(String message) {
        logger.warn(message);

    }

    /* (non-Javadoc)
     * @see com.meritsystems.core.Logger#entering(java.lang.String, java.lang.Object)
     */
    public void entering(String methodName, Object param) {
        this.entering(methodName, new Object[]{param});
    }
}
