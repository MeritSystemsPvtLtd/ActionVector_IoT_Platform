/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qtart;

import com.prokosha.adapter.etl.ETLAdapter.CEPEvent;
import com.prokosha.adapter.etl.ETLAdapter.ETLAdapter;
import com.prokosha.adapter.etl.ETLAdapter.PersistReportData;
import com.prokosha.adapter.etl.ETLAdapter.ReportData;
import contextSetters.QTartContext;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author anand kumar verma
 */
public class QTartParser {

    Logger log = Logger.getLogger(QTartParser.class.getName());
    HashMap<String, ReportData> metricValueMap = new HashMap<String, ReportData>();
    ETLAdapter etl = null;
    PersistReportData dataInsert = null;
    static Properties properties = null;
    static Enumeration enum1 = null;
    String metricValue = null;
    String eventID = null;
    static boolean mapInitialized = false;
    int insertStatus = 0;

    public QTartParser(String CEPEvent, String metrics, Properties properties, String eventID) {

        String metricToken[] = null;
        String metricsToken[] = null;

        String customerID = null;
        String metricType = null;
        String timestamp = null;
        String hostName = null;
        String alertName = null;
        String resourceId = null;


        ReportData reportData = null;

        this.eventID = eventID;

        try {
            dataInsert = new PersistReportData();

            String parseToken[] = CEPEvent.split(",");
            metricsToken = metrics.split(" ");
            int nocol = 0;
            for (int j = 0; j < parseToken.length; j++) {
                String token[] = parseToken[j].split("=");
                if (token[0].trim().equalsIgnoreCase("customerID")) {
                    customerID = token[1];
                    //log.debug("HostName===" + hostName);
                    token = null;
                    break;
                }
                token = null;
            }

            for (int j = 0; j < parseToken.length; j++) {
                String token[] = parseToken[j].split("=");
                if (token[0].trim().equalsIgnoreCase("eventSource")) {
                    hostName = token[1];
                    //log.debug("HostName===" + hostName);
                    token = null;
                    break;
                }
                token = null;
            }

            for (int j = 0; j < parseToken.length; j++) {
                String token[] = parseToken[j].split("=");
                if (token[0].trim().equalsIgnoreCase("TimeStamp")) {
                    timestamp = token[1];
                    //log.debug("Timestamp===" + timestamp);
                    token = null;
                    break;
                }
                token = null;
            }

            for (int j = 0; j < parseToken.length; j++) {
                String token[] = parseToken[j].split("=");
                if (token[0].trim().equalsIgnoreCase("Alert")) {
                    alertName = token[1];
                    //log.debug("Timestamp===" + timestamp);
                    token = null;
                    break;
                }
                token = null;
            }

            for (int j = 0; j < parseToken.length; j++) {
                String token[] = parseToken[j].split("=");
                if (token[0].trim().equalsIgnoreCase("resourceID")) {
                    resourceId = token[1];
                    //log.debug("resourceId===" + resourceId);
                    token = null;
                    break;
                }
                token = null;
            }

            if (timestamp == null) {
                Date d = new Date();
                timestamp = ((Long) (d.getTime() / 1000)).toString();
            }
            for (int i = 0; i < metricsToken.length; i++) {//Messages, Delay, Duration

                String mValue = null;
                String mName = null;

                boolean vfound = false;

                for (int j = 0; j < parseToken.length && (vfound == false); j++) {
                    String token[] = parseToken[j].trim().split("=");
                    if (metricsToken[i].equalsIgnoreCase(token[0])) {
                        reportData = new ReportData();
                        mValue = token[1];
                        mName = token[0];
                        metricType = properties.getProperty(mName);
                        if (metricType != null) {
                            metricToken = metricType.split(",");

                            reportData.setCategory(metricToken[1]);
                            reportData.setHost(hostName);
                            reportData.setMetricType(metricToken[0]);
                            //reportData.setTimestamp1(timestamp);
                            reportData.setValue(mValue);
                            reportData.setEventID(eventID);
                            reportData.setAlertName(alertName);

                            reportData.setResourceId(resourceId);
                            // log.debug("Setting The Report Data Sucessfull");
                            metricValueMap.put(metricsToken[i], reportData);
                        } else {
                            log.debug("***Message*****Metric Type & category is not cofigures for  Mertic==" + mName);
                        }
                    }
                    token = null;
                }
            }

            QTartContext contextSetter = new QTartContext(properties);
            metricValueMap = contextSetter.setContextsInMap(metricValueMap);

            this.insertStatus = dataInsert.sendToDatabse(metricValueMap,customerID);


        } catch (Exception e) {
            log.error("Exception :" + e.getMessage());
            e.printStackTrace();
        } finally {

            metricType = null;
            metricToken = null;
            metricsToken = null;
            hostName = null;
            timestamp = null;
            resourceId = null;
            reportData = null;
            metricValueMap = null;
            dataInsert = null;
            log = null;
        }
    }

    public int getInsertStatus() {
        return this.insertStatus;
    }
}
