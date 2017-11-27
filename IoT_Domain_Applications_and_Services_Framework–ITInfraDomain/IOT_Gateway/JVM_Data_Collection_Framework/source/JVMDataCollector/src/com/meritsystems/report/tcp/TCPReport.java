package com.meritsystems.report.tcp;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.meritsystems.core.ApplicationException;
import com.meritsystems.core.log.Logger;
import com.meritsystems.core.log.LoggerFactory;
import com.meritsystems.monitor.JMXContext;
import com.meritsystems.monitor.MX;
import com.meritsystems.monitor.JVMMonitor;
import com.meritsystems.monitor.SubMetrics;
import com.meritsystems.report.Report;
import com.meritsystems.core.configuration.ConfigurationReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLSocket;
import org.apache.log4j.*;

/**
 * @author shashi Generates an XML Report
 */
public class TCPReport extends Report {

    static com.meritsystems.core.log.Logger logger = LoggerFactory.getLogger(TCPReport.class);
    private static final org.apache.log4j.Logger evtLogger = org.apache.log4j.Logger.getLogger("eventsLogger");
    public static String ERRORFILEPATH = "errorfilepath";
    public static String HOST = "host";
    public static String PORT = "port";
    public static String RETRYVAL = "retryinterval";
    public static final int RETRY_COUNT = 1;
    public static final int DEFAULT_RETRY_INTERVAL = 30;
    private static SSLSocket clientSocket = null;
    private static boolean bflag = false;
    private static PrintWriter outToServer = null;
    //private static ETLAdapter etlAdapter = new ETLAdapter();
    //watchdog
    public static String szHostPort = null;
    public static String szHostIp = null;

    public TCPReport() {
        super();
    }

    @Override
    public void report(List<JMXContext> jmx, OutputStream outputstream) throws ApplicationException {

        logger.entering("report");
        StringBuffer sb = new StringBuffer(100);
        String HostIp = "null";
        String jmxPort = "null";
        String availability = "0";

        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;

        for (JMXContext jmxContext : jmx) {
            sb.append("stream=JVMEvent,");
            sb.append("CustomerID=" + JVMMonitor.customerID + ",");
            sb.append("resourceType=JVM,");
            sb.append("resourceSubType=JVM,");

            String metric = "";
            String metricValue = "";
            String timestamp1 = "";
            String df = "yyyy/MM/dd HH:mm:ss";/*set format*/
            SimpleDateFormat smdf = new SimpleDateFormat(df);
            Date date = null;

            for (MX mx : jmxContext.getMx()) {
                //mx.getTitle();
                for (SubMetrics submetrics : mx.getSubmetricsList()) {
                    metric = submetrics.getBeanproperty();
                    metricValue = getStringRepresentation(submetrics.getValue());
                    if (metric.equals("hostname")) {
                        metric = "HostName";
                    }

                    if (metric.equalsIgnoreCase("TIMESTAMP") || metric.equalsIgnoreCase("time")) {
                        timestamp1 = metricValue;
                        try {
                            date = smdf.parse(timestamp1);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        timestamp1 = ((Long) date.getTime()).toString();
                        timestamp1 = timestamp1.substring(0, 10);
                        metricValue = timestamp1;
                    }

                    sb.append(metric + "=" + metricValue + ",");
                    //System.out.println(submetrics.getBeanproperty() + "="+getStringRepresentation(submetrics.getValue()));
                    if (metric.equalsIgnoreCase("hostipaddress") && HostIp.equalsIgnoreCase("null")) {
                        HostIp = metricValue;
                        flag1 = true;
                    }

                    if (metric.equalsIgnoreCase("jmxurl") && jmxPort.equalsIgnoreCase("null")) {
                        jmxPort = getJMXPort(metricValue);
                        flag2 = true;
                    }

                    if (metric.equalsIgnoreCase("alive") && availability.equalsIgnoreCase("0")) {
                        if (metricValue.equalsIgnoreCase("true")) {
                            availability = "1";
                        }
                        flag3 = true;
                    }
                }
                if (flag1 && flag2) {
                    sb.append("resourceId=" +/* HostIp + ":" +*/ jmxPort + ",");
                    flag1 = false;
                    flag2 = false;
                }

                if (flag3) {
                    //sb.append("availability=" + availability + ",");
                    sb.append("availability=" + availability);
                    flag3 = false;
                }
            }
            sb.append("");
        }

        szHostPort = jmxPort;//watchdog
        szHostIp = HostIp;//watchdog

        HostIp = null;
        jmxPort = null;
        availability = null;
        //String statusId = null;
        //String eventID = null;
        /*if (JVMMonitor.sendToETLOption) {
         logger.info(" Sending JVM Stream to :ETLAdapter :: " + sb.toString());
         ETLAdapter etlAdapter = new ETLAdapter();
         statusId = etlAdapter.dumpCEPEvent(sb.toString());
         logger.info(" the status id for the event is " + statusId);
         etlAdapter = null;
         String id[] = statusId.split(":");
         eventID = id[0];
         statusId = id[1];
         }

         if (JVMMonitor.sendToETLOption) {
         sb.append(",eventID=" + eventID);
         } else {
         sb.append(",eventID=0");
         statusId = "1";
         }*/
        logger.info("Sending data to CEP Engine : " + sb.toString());
        System.out.println("Sending data to CEP Engine : " + sb.toString());
        sendData(getArguments(), sb.toString());
        logger.exiting("report");

        //sending watchdog event 23-04-2013
        if (ConfigurationReader.getConfiguration().getWatchdogSend()) {
            JVMMonitor.WatchDogEventConnector.initialize(ConfigurationReader.getConfiguration().getWatchdogHost(), Integer.parseInt(ConfigurationReader.getConfiguration().getWatchdogPort()), "\n");
            logger.debug("Connecting to WatchDog....");
            if (!JVMMonitor.WatchDogEventConnector.isServerReady()) {
                logger.debug("Some error occurred while connecting to WatchDog. Will retry later...");
                System.out.println("Some error occurred while connecting to WatchDog. Will retry later...");
            } else {
                JVMMonitor.WatchDogEventConnector.sendMessage("type=jvmlocal:" + szHostIp + ":" + szHostPort + ",status=Alive");

                logger.info("dogwatch event sent successfully");
                System.out.println("dogwatch event sent successfully");

            }
            // watchdog event sent
        }

        //eventID = null;
        //statusId = null;
        sb = null;
    }

    @Override
    protected OutputStream getDefaultOutputStream() throws IOException {
        return null;
    }

    /**
     * @param args
     * @param data
     */
    private void sendData(Map<String, String> args, String data) {
        logger.entering("SendData");
        int retryCount = 0;
        boolean dataSentSuccess = false;
        String host = args.get(HOST);
        String port = args.get(PORT);
        String retryInterval = args.get(RETRYVAL);
        String errorFilepath = args.get(ERRORFILEPATH);
        int retryIntervalVal = DEFAULT_RETRY_INTERVAL;

        if (StringUtils.isNumeric(retryInterval)) {
            retryIntervalVal = Integer.parseInt(retryInterval);
        }

        logger.info("host " + host + " port " + port + " retryIntervalVal " + retryIntervalVal + " errorFilepath " + errorFilepath);

        /*try {
            resendFile(errorFilepath, host, Integer.parseInt(port));
        } catch (NumberFormatException e1) {
            logger.severe(e1, "sendData", "Exception while resending data");
        } catch (IOException e1) {
            logger.severe(e1, "sendData", "Exception while resending data");
        }*/

        while (retryCount != RETRY_COUNT && !dataSentSuccess) {
            try {
                logger.info("Retry count " + retryCount);
                dataSentSuccess = sendData(data, host, Integer.parseInt(port));
                logger.info("Data sent to " + host);
            } catch (Exception ex) {
                logger.severe(ex, "sendData", "Exception while sending data");
                retryCount++;
                logger.info("Sleeping " + retryIntervalVal);
                try {
                    Thread.sleep(retryIntervalVal * 1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        try {
            if (!dataSentSuccess) {
                logger.info("Data to be written to file");
                //writeToFile(data, errorFilepath);
                evtLogger.info(data);
                logger.info("Data written to file");
            }
        } catch (Exception e) {
            logger.severe(e, "sendData", "Exception while writing file");
        }
        logger.exiting("SendData");
    }

    /**
     * @param data
     * @param filepath
     * @throws IOException
     */
    private void writeToFile(String data, String filepath) throws IOException {
        logger.entering("writeToFile " + filepath);
        logger.info("Writing to file " + filepath);
        File file = new File(filepath);
        FileUtils.writeStringToFile(file, data);
        logger.info("Writing to file successsful");
        logger.exiting("writeToFile " + filepath);
    }

    /**
     * @param data
     * @param filepath
     * @param hostname
     * @param port
     * @throws IOException
     */
    private void resendFile(String filepath, String hostname, int port) throws IOException {

        logger.entering("resendFile " + filepath);
        File file = new File(filepath);
        if (file.exists()) {
            logger.info("File exists, trying resening");
            sendData(FileUtils.readFileToString(file), hostname, port);
            logger.info("Resending success");
            logger.info("Deleteing file");
            file.delete();
            logger.info("Deleted");
        } else {
            logger.info("No backup file exists");
        }
        file = null;
        logger.exiting("resendFile " + filepath);
    }

    /**
     * @param data
     * @param hostname
     * @param port
     */
    /*private void sendData(String data, String hostname, int port) throws IOException {

     logger.entering("sendData ");
     logger.info("Creating socket connection to " + hostname + " " + port);
     SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
     if (!bflag) {
     clientSocket = (SSLSocket) sslsocketfactory.createSocket(hostname, port);
     logger.info("Socket===" + clientSocket);
     // BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
     outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
     bflag = true;
     }
     logger.info("Socket===" + clientSocket);
     //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
     logger.info("Sending data " + data);
     if (outToServer.checkError()) {
     try {
     outToServer.close();
     outToServer = null;
     } catch (Exception ex) {
     outToServer = null;
     }
     outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
     }
     outToServer.println(data);
     logger.info("Sending data successful");
     //outToServer.close();
     //clientSocket.close();
     logger.exiting("sendData ");
     }*/
    /**
     * @param data
     * @param hostname
     * @param port
     */
    private boolean sendData(String data, String hostname, int port) throws IOException {
        logger.entering("sendData ");
        try {
            if (JVMMonitor.SSLClient.isServerReady()) {
                return JVMMonitor.SSLClient.sendMessage(data);
            } else {
                JVMMonitor.SSLClient.initialize(hostname, port, "\n");
                return JVMMonitor.SSLClient.sendMessage(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getJMXPort(String JMXUrl) {
        Pattern p = Pattern.compile(":[0-9]{4}/");
        Matcher m = p.matcher(JMXUrl);
        String JMXPort = "";
        while (m.find()) {
            JMXUrl = m.group();
        }
        int len = JMXUrl.length();
        JMXPort = JMXUrl.substring(1, len - 1);
        p = null;
        m = null;
        JMXUrl = null;
        return JMXPort;
    }
}
