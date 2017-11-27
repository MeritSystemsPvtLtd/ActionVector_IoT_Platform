package com.meritsystems.report;

import java.util.List;

import com.meritsystems.core.ApplicationException;
import com.meritsystems.core.configuration.ConfigurationReader;
import com.meritsystems.core.log.Logger;
import com.meritsystems.core.log.LoggerFactory;
import com.meritsystems.monitor.JMXContext;

/**
 * @author shashi Generates reports of all configured reports
 */
public class ReportDispatcher {

    static Logger logger = LoggerFactory.getLogger(ReportDispatcher.class);

    /**
     *
     */
    private ReportDispatcher() {
        super();
    }

    /**
     * @param contexts
     * @throws ApplicationException
     */
    public static void generateReports(List<JMXContext> contexts) throws ApplicationException {
        logger.entering("generateReports");

        List<String> configuredReports = ConfigurationReader.getConfiguration().getReporters();

        for (String report : configuredReports) {
            logger.info("Creating report for " + report);
            //ignore exceptions
            try {
                report(report, contexts);
            } catch (Exception ex) {
                logger.severe(ex, "generateReports", "Report " + report);
            }
        }
        logger.exiting("generateReports");
    }

    /**
     * @param classname
     * @param contexts
     * @throws ApplicationException
     */
    private static void report(String classname, List<JMXContext> contexts) throws ApplicationException {
        logger.entering("report");
        Report report = null;

        try {
            report = (Report) Class.forName(classname).newInstance();
        } catch (InstantiationException e) {
            logger.severe(e, "report", "Loading class failed " + classname);
            throw new ApplicationException(e);
        } catch (IllegalAccessException e) {
            logger.severe(e, "report", "Loading class failed " + classname);
            throw new ApplicationException(e);
        } catch (ClassNotFoundException e) {
            logger.severe(e, "report", "Loading class failed " + classname);
            throw new ApplicationException(e);
        }

        report.processArguments();
        report.report(contexts, null);
        report = null;
        logger.exiting("report");
    }
}