package com.meritsystems.report;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.meritsystems.core.ApplicationException;
import com.meritsystems.core.log.Logger;
import com.meritsystems.core.log.LoggerFactory;
import com.meritsystems.monitor.JMXContext;
import com.meritsystems.monitor.MX;
import com.meritsystems.monitor.SubMetrics;

/**
 * @author shashi
 *
 */
public class ConsoleReport extends Report {

    static Logger logger = LoggerFactory.getLogger(ConsoleReport.class);

    /**
     *
     */
    public ConsoleReport() {
        super();
    }

    @Override
    public void report(List<JMXContext> jmx, OutputStream stream) throws ApplicationException {

        logger.entering("report");

        for (JMXContext jmxContext : jmx) {
            print(jmxContext.getAppname());
            print(jmxContext.getHostproHostProperties().getJmxurl());

            for (MX mx : jmxContext.getMx()) {
                print(mx.getTitle());

                for (SubMetrics submetrics : mx.getSubmetricsList()) {
                    print(submetrics.getTitle() + " " + submetrics.getValue());
                }
            }
        }

        logger.exiting("report");

    }

    private void print(String message) {
        System.out.println(message);
    }

    @Override
    protected OutputStream getDefaultOutputStream() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
}
