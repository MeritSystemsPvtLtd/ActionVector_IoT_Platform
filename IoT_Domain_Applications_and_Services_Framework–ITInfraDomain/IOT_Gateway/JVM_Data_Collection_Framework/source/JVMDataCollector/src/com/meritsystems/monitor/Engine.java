package com.meritsystems.monitor;

import com.meritsystems.core.ApplicationException;
import com.meritsystems.core.MetricsEnum;
import com.meritsystems.core.configuration.ConfigurationReader;
import com.meritsystems.core.configuration.HostProperties;
import com.meritsystems.core.log.Logger;
import com.meritsystems.core.log.LoggerFactory;
import com.meritsystems.monitor.mx.AppMX;
import com.meritsystems.monitor.mx.MXFactory;
import com.meritsystems.report.ReportDispatcher;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.commons.lang.StringUtils;

/**
 * @author shashi
 *
 */
public class Engine extends TimerTask {

    static Logger logger = LoggerFactory.getLogger(Engine.class);
    JMXConnector jmxc = null;

    /**
     *
     */
    public Engine() {
        super();
    }

    /**
     * @throws ApplicationException
     */
    public void init() throws ApplicationException {
        ConfigurationReader.getConfiguration();
    }

    /**
     * (non-Javadoc)
     *
     * @see java.util.TimerTask#run()
     */
    public List<JMXContext> process() {
        logger.entering("process");
        List<JMXContext> jmxcList = new ArrayList<JMXContext>();
        MBeanServerConnection msbc = null;

        try {
            logger.info("getting all configured urls");

            List<HostProperties> urls = ConfigurationReader.getConfiguration().getURL();

            logger.info("got  all configured urls " + urls);

            for (HostProperties url : urls) {
                JMXContext context = new JMXContext(url, ConfigurationReader.getConfiguration().getAppname());
                jmxcList.add(context);

                logger.info("Loading URL " + url);
                //if fails continue
                try {
                    logger.info("Creating connection " + url);
                    msbc = getMBeanServerConnection(url.getJmxurl());
                    logger.info("Creating connection succcess" + url);
                    List<MX> mxbeans = getAllConfiguredMetrics(msbc);
                    logger.info("Got list of mxbeans ");

                    for (MX mx : mxbeans) {
                        logger.info("Processing for " + mx.getMxConstant());
                        mx.process();
                    }

                    context.setMx(mxbeans);
                    context.setConnected(true);
                } catch (Exception ex) {
                    logger.severe(ex, "getMBeanServprocesserConnection", "Exception while connecting to URL " + url);
                    context.setConnected(false);
                }
                logger.info("Jmx List SIze===" + jmxcList.size());

                for (JMXContext contextSetter : jmxcList) {
                    AppMX mx = new AppMX();
                    mx.setAlive(contextSetter.isConnected());
                    mx.setClustername(contextSetter.getHostproHostProperties().getClustername());
                    mx.setHostname(contextSetter.getHostproHostProperties().getHostname());
                    mx.setTime(contextSetter.getDateOfCreation());
                    mx.setJmxurl(contextSetter.getHostproHostProperties().getJmxurl());
                    logger.info("JMX URL" + mx.getJmxurl());
                    mx.setHostipaddress(contextSetter.getHostproHostProperties().getHostIPaddress());
                    mx.setMxBean(mx);
                    mx.process();
                    contextSetter.getMx().add(mx);
                }

                try {
                    logger.info("Before Sending to CEP");
                    ReportDispatcher.generateReports(jmxcList);
                    logger.info("After Sending to CEP");
                } catch (Exception e) {
                    e.printStackTrace();
                }


                jmxcList.clear();
            }
        } catch (Exception ex) {
            logger.severe(ex, "process", ex.getMessage());
        } finally {

            try {
                msbc = null;
                jmxc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        logger.exiting("process");
        return jmxcList;
    }

    /**
     * @return
     */
    private MBeanServerConnection getMBeanServerConnection(String url) throws MalformedURLException, IOException {

        logger.entering("getMBeanServerConnection");
        logger.info("Loading for " + url);

        JMXServiceURL jmxurl = new JMXServiceURL(url);
        logger.info("Created JMXServiceURL");

        String username = ConfigurationReader.getConfiguration().getUserName();
        logger.info("Username " + username);

        String password = ConfigurationReader.getConfiguration().getPassword();
        logger.info("password " + password);


        if (StringUtils.isNotBlank(username)) {
            Map<String, String[]> env = new HashMap<String, String[]>();
            String[] creds = {
                username, password
            };
            env.put(JMXConnector.CREDENTIALS, creds);
            logger.info("Created credentials");
            jmxc = JMXConnectorFactory.connect(jmxurl, env);
        } else {
            jmxc = JMXConnectorFactory.connect(jmxurl, null);
        }
        logger.info("Created jmxc");
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
        logger.info("Created mbsc");

        logger.exiting("getMBeanServerConnection");
        //jmxc.close();
        return mbsc;
    }

    /**
     * Creates MX objects from server for configured MXbeans
     *
     * @param msbc
     * @return
     * @throws ApplicationException
     */
    private List<MX> getAllConfiguredMetrics(MBeanServerConnection msbc) throws Exception {
        List<MX> mxList = new ArrayList<MX>();

        logger.entering(" getAllConfiguredMetrics");

        logger.info("Loading all configured metrics");

        List<MetricsEnum> metrics = ConfigurationReader.getConfiguration().getMetricsEnum();

        for (MetricsEnum metricsenum : metrics) {
            logger.info("Got metrics " + metricsenum.getName());
            List<Object> mxObjects = getMx(msbc, metricsenum);

            logger.info("Got MXObjects");

            if ((MX.metricsClass.get(metricsenum) != null)) {
                for (Object object : mxObjects) {
                    MX mx = MXFactory.getMX(metricsenum);
                    mx.setMxBean(object);
                    mxList.add(mx);
                }
            } else {
                logger.error("getAllConfiguredMetrics", "Classnot found");
            }

        }

        logger.exiting(" getAllConfiguredMetrics");
        return mxList;
    }

    /**
     * @param mxobject
     * @return
     * @throws Exception
     */
    public List<SubMetrics> getSubMetrics(MX mxobject) throws Exception {
        List<SubMetrics> submetricslist = new ArrayList<SubMetrics>();
        logger.entering(" getSubMetrics " + mxobject);

        logger.info("Loading submetrics for " + mxobject.getMxConstant());

        logger.exiting(" getSubMetrics " + mxobject);
        return submetricslist;
    }

    /**
     * @param mbsc
     * @param mx
     * @param classval
     * @return
     * @throws ApplicationException
     */
    private List<Object> getMx(MBeanServerConnection msbc, MetricsEnum metricsenum) throws ApplicationException {
        logger.entering("getMx " + metricsenum);
        List<Object> objList = new ArrayList<Object>();
        try {
            ObjectName gcName = new ObjectName(MX.metricsClass.get(metricsenum).getMx() + ",*");
            for (ObjectName name : msbc.queryNames(gcName, null)) {
                objList.add(ManagementFactory.newPlatformMXBeanProxy(msbc, name.getCanonicalName(), MX.metricsClass.get(metricsenum).getManagementClass()));
            }
        } catch (Exception ex) {
            logger.severe(ex, "getMx", ex.getMessage());
            throw new ApplicationException(ex);
        }
        logger.exiting("getMx");

        return objList;
    }

    public static void main(String args[]) {
        try {
            Engine engine = new Engine();
            engine.init();
            ReportDispatcher.generateReports(engine.process());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {

        logger.entering("run");

        List<JMXContext> contexts = process();

        logger.exiting("run");

    }
}
