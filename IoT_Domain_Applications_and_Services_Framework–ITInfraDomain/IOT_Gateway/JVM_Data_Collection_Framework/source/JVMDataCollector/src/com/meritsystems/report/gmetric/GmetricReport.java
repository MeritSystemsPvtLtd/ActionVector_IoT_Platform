package com.meritsystems.report.gmetric;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.meritsystems.core.ApplicationException;
import com.meritsystems.core.log.Logger;
import com.meritsystems.core.log.LoggerFactory;
import com.meritsystems.monitor.JMXContext;
import com.meritsystems.monitor.MX;
import com.meritsystems.monitor.SubMetrics;
import com.meritsystems.report.Report;

/**
 * @author shashi
 *
 */
public class GmetricReport extends Report {

    static Logger logger = LoggerFactory.getLogger(GmetricReport.class);
    public static String COMMAND = "command";
    Runtime rt = Runtime.getRuntime();

    /**
     *
     */
    public GmetricReport() {
        super();
    }

    @Override
    protected OutputStream getDefaultOutputStream() throws IOException {
        return null;
    }

    @Override
    public void report(List<JMXContext> jmx, OutputStream outputstream) throws ApplicationException {
        logger.entering("report");
        String command = "gmetric";
        Map<String, String> args = getArguments();

        if (args != null || !args.isEmpty()) {
            command = getArguments().get(COMMAND);
        }

        for (JMXContext jmxContext : jmx) {
            for (MX mx : jmxContext.getMx()) {
                for (SubMetrics submetrics : mx.getSubmetricsList()) {
                    execute(command, submetrics.getTitle(), getStringRepresentation(submetrics.getValue()), submetrics.getDescription(), submetrics.getType(), submetrics.getUnit(), rt);
                }
            }
        }
        logger.exiting("report");
    }

    /**
     * @param command
     * @param title
     * @param value
     * @param description
     * @param units
     */
    private void execute(String command, String title, String value, String description, String type, String units, Runtime rt) {
        if (command == null) {
            System.out.println("");
        }

        String executecommd = command + " --name " + StringUtils.replace(title, " ", "_") + " --value " + value + " --type " + type;

        if (StringUtils.isNotBlank(units)) {
            executecommd = executecommd + " --units " + units;
        }

        logger.info("Executing command " + executecommd);

        try {
            Process pr = rt.exec(executecommd);
            pr.waitFor();
            /*File file = new File("test.log");
             logger.info("Wriing to "+file.getAbsolutePath());
             FileOutputStream os = new FileOutputStream(file , true);
             os.write((executecommd+"\n").getBytes());
             os.flush();
             os.close();*/
        } catch (Exception e) {
            logger.severe(e, "execute", "Error while executing command");
            e.printStackTrace();
        }
    }
}
