package com.meritsystems.core.log;

/**
 * @author shashi Gets logger instance
 */
public class LoggerFactory {

    /**
     * @param classname
     * @return
     */
    public static Logger getLogger(Class classname) {
        return new Log4jLogger(classname);
    }
}
