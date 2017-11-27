package com.meritsystems.report;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.meritsystems.core.ApplicationException;
import com.meritsystems.core.configuration.ConfigurationReader;
import com.meritsystems.core.log.Logger;
import com.meritsystems.core.log.LoggerFactory;
import com.meritsystems.monitor.JMXContext;

/**
 * @author shashi
 *
 */
public abstract class Report {

    static Logger logger = LoggerFactory.getLogger(Report.class);
    private Map<String, String> arguments = new HashMap<String, String>();
    public static String ARRAYSEPARATOR = "$$";
    public static String COMMA = ",";
    public static String ESCAPE_COMMASEPARATOR = "##";

    /**
     * @param jmx
     * @throws ApplicationException
     */
    public abstract void report(List<JMXContext> jmx, OutputStream outputstream) throws ApplicationException;

    /**
     * @param jmx
     * @throws ApplicationException
     */
    public void report(List<JMXContext> jmx) throws ApplicationException {
        logger.entering("Report JMX");
        try {
            OutputStream stream = getDefaultOutputStream();
            logger.info("calling report");
            report(jmx, stream);
            logger.info("calling close");
            close(stream);
        } catch (IOException ex) {
            logger.severe(ex, "report JMX", ex.getMessage());
            throw new ApplicationException(ex);
        }
        logger.exiting("Report JMX");
    }

    /**
     * @return
     */
    protected abstract OutputStream getDefaultOutputStream() throws IOException;

    /**
     * @param stram
     * @throws IOException
     */
    protected void close(OutputStream stream) throws IOException {
        logger.entering("close");
        if (stream != null) {
            stream.close();
        }
        logger.exiting("close");
    }

    /**
     * @return
     */
    public Map<String, String> getArguments() {
        return arguments;
    }

    /**
     * @param arguments
     */
    public void processArguments() {

        logger.entering("processArguments");

        String configAttribute = this.getClass().getName() + ".args";
        logger.info(" configAttribute " + configAttribute);

        String val = ConfigurationReader.getRawconfig().getString(configAttribute);
        logger.info(" val " + val);

        String[] tokens = null;

        if (val != null) {
            tokens = StringUtils.split(val, ConfigurationReader.SEPARATOR);
        }

        int count = 0;
        if (tokens != null) {
            while (count < tokens.length) {
                String arg = tokens[count];
                count++;
                logger.info("Arg " + arg);
                arguments.put(StringUtils.split(arg, "=")[0], StringUtils.split(arg, "=")[1]);
            }
        }

        logger.info(" arguments " + arguments);
        logger.exiting("processArguments");

    }

    /**
     * Converts givenobject to string representation
     *
     * @param obj
     * @return
     */
    protected String getStringRepresentation(Object obj) {
        String returnval = null;
        logger.info("String representation of " + obj);

        if (obj == null) {
            returnval = "";
        } else if (obj != null && obj.getClass().isArray() && obj instanceof String[]) {
            String[] array = (String[]) obj;
            for (int i = 0; i < array.length; i++) {
                returnval = returnval + StringUtils.replace(array[i], COMMA, ESCAPE_COMMASEPARATOR);
                if (i != array.length) {
                    returnval = returnval + ARRAYSEPARATOR;
                }
            }
        } else if (obj != null && obj.getClass().isArray() && obj instanceof long[]) {
            long[] array = (long[]) obj;
            for (int i = 0; i < array.length; i++) {
                returnval = returnval + StringUtils.replace(Long.toString(array[i]), COMMA, ESCAPE_COMMASEPARATOR);
                if (i != array.length) {
                    returnval = returnval + ARRAYSEPARATOR;
                }
            }
        } else if (obj instanceof Date) {
            returnval = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format((Date) obj);
        } else {
            returnval = obj.toString();
        }

        returnval = StringUtils.replace(returnval, COMMA, ESCAPE_COMMASEPARATOR);

        logger.info("String representation is " + returnval);
        return returnval;
    }
}