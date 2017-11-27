/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsers;

import com.prokosha.adapter.etl.ETLAdapter.CEPEventMetricsMapping;
import com.prokosha.adapter.etl.ETLAdapter.ETLAdapter;
import com.prokosha.adapter.etl.ETLAdapter.PersistReportData;
import com.prokosha.adapter.etl.ETLAdapter.ReportData;
import java.text.DecimalFormat;
import java.util.*;
import org.apache.log4j.Logger;

/**
 *
 * @author anandkv
 */
public class WatchDogParser {
    Logger log = Logger.getLogger(WatchDogParser.class.getName());
    HashMap<String, ReportData> metricValueMap = new HashMap<String, ReportData>();
    ETLAdapter etl = null;
    int insertStatus = 0;
    PersistReportData dataInsert = null;
    static Properties properties = null;
    static Enumeration enum1 = null;
    String metricValue = null;
    String eventID = null;
    static boolean mapInitialized = false;

    public double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");// like 2.35 for     // 2.356789..
        return Double.valueOf(twoDForm.format(d));
    }

    public WatchDogParser(String CEPEvent, String metrics, Properties properties, String eventID) {
        initialize(CEPEvent, metrics, properties, eventID);
    }

    public void initialize(String CEPEvent, String metrics, Properties properties, String eventID) {
        String timestamp = null;
        String resourceSubType = null;
        String hostName = null;
        String resourceId = null;
        String customerID = null;
        String resourceType = null;
        String resourceName = null;
        String availability = ""+1;// by default 
        ReportData reportData = null;
        this.eventID = eventID;

        try {
            Map<String, String> mp_string_to_map = convertStringToMap(CEPEvent);
            String parseToken[] = CEPEvent.split(",");
            dataInsert = new PersistReportData();
            String defaultKeyConstants[] = CEPEventMetricsMapping.getSzdefaultConstants();

            customerID = mp_string_to_map.get(defaultKeyConstants[3].trim());

            hostName = mp_string_to_map.get(defaultKeyConstants[10]);

            timestamp = mp_string_to_map.get(defaultKeyConstants[0]);

            resourceType = mp_string_to_map.get(defaultKeyConstants[4]);

            resourceSubType = mp_string_to_map.get(defaultKeyConstants[1]);

            resourceId = mp_string_to_map.get(defaultKeyConstants[2]);
            
            resourceName = mp_string_to_map.get("resourceName");

            availability = mp_string_to_map.get("availability");


            //if (!availabilility) { //Monitoring agent is down
                System.out.println("availability-->"+availability);
                reportData = new ReportData();
                reportData.setCategory("MonitoringAgent");
                reportData.setMetricType(resourceName);
                reportData.setHost(hostName);
                reportData.setTimestamp1(timestamp);
                reportData.setValue(availability);
                reportData.setResourceId(resourceId);
                reportData.setEventID(eventID);
                reportData.setResourceType(resourceType);
                reportData.setResourceSubType(resourceSubType);
                metricValueMap.put("agent", reportData);
            //}else{
                //System.out.println("*******Monitoring Agent Up Event Found*******");
            //}
                this.insertStatus = dataInsert.sendAgentToDatabse(metricValueMap, customerID);
        } catch (Exception e) {
            System.out.println("Exception :" + e.getMessage());
            e.printStackTrace();
        } finally {
            resourceSubType = null;
            timestamp = null;
            hostName = null;
            reportData = null;
            resourceId = null;
            metricValueMap = null;
            dataInsert = null;
            log = null;
            //contextSetter = null;
        }
    }

    public Map<String, String> convertStringToMap(String szCepEvent) {
        Map<String, String> metrics_map = new HashMap<String, String>();
        String splitByComma[] = szCepEvent.split(",");
        for (int i = 0; i < splitByComma.length; i++) {
            String keyValueSplit[] = splitByComma[i].split("=");
            metrics_map.put(keyValueSplit[0].trim(), keyValueSplit[1].trim());
        }
        System.out.println("After Converted==" + metrics_map);
        return metrics_map;
    }

    public int getInsertStatus() {
        return this.insertStatus;
    }
}
