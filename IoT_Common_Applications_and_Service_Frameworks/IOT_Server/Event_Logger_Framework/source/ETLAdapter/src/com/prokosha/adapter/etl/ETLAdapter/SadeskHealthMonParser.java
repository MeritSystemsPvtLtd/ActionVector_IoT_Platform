/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.prokosha.adapter.etl.ETLAdapter;

import contextSetters.SadeskHealthMonContext;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author vijayasri
 */
public class SadeskHealthMonParser {
    HashMap<String, ReportData> metricValueMap = new HashMap<String, ReportData>();
    private static Logger log = Logger.getLogger(ETLAdapter.class.getName());
    ETLAdapter etl = null;
    PersistReportData dataInsert = null;
    int insertStatus = 0;

    public SadeskHealthMonParser(String CEPEvent, String metrics, Properties properties, String eventID){
        initialize(CEPEvent,metrics,properties, eventID);
    }

    public void initialize(String CEPEvent, String metrics,Properties properties, String eventID) {
        String metricsToken[] = null;
        double diskTotal = 0.0;
        double diskFree = 0.0;
        String timestamp = null;
        String startTIme = null;
        String endTime = null;
        String MetricType = null;
        String hostName = null;
        String customerID = null;
        double memFree = 0.0;
        double memTotal = 0.0;
        Double mem_usg = 0.0;
        Double disk_boot = 0.0;
        String status = null;
        String resourceId = null;
        etl = new ETLAdapter();
        String logmetricType=null;

        try {
            String parseToken[] = CEPEvent.split(",");
            metricsToken = metrics.split(" ");

            dataInsert = new PersistReportData();

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

            for (int j = 0; j < parseToken.length; j++){
                String token[] = parseToken[j].split("=");
                if (token[0].trim().equalsIgnoreCase("HostName")) {
                    hostName = token[1];
                    //log.debug("HostName===" + hostName);
                    break;
                }
            }

            for (int j = 0; j < parseToken.length; j++){
                String token[] = parseToken[j].split("=");
                if (token[0].trim().equalsIgnoreCase("TimeStamp")) {
                    timestamp = token[1];
                    //log.debug("Timestamp===" + timestamp);
                    break;
                }
            }
            for (int j = 0; j < parseToken.length; j++) {
                String token[] = parseToken[j].split("=");
                if (token[0].trim().equalsIgnoreCase("MetricType")) {
                    MetricType = token[1];
                    //log.debug("Size===" + size);
                    break;
                }
            }
            for (int j = 0; j < parseToken.length; j++) {
                String token[] = parseToken[j].split("=");
                if (token[0].trim().equalsIgnoreCase("resourceID")) {
                    resourceId = token[1];
                    //log.debug("resourceId===" + resourceId);
                    break;
                }
            }

            for (int i = 0; i < metricsToken.length; i++){
                String mValue = null;
                String mName = null;
                String metricType = null;
                String metricToken[] = null;
                ReportData reportData = null;
                boolean vfound = false;

                for (int j = 0; j < parseToken.length && (vfound == false); j++){
                    String token[] = parseToken[j].trim().split("=");

                    if (metricsToken[i].equalsIgnoreCase(token[0])){
                        reportData = new ReportData();
                        vfound = true;
                        mValue = token[1];
                        mName = token[0];

                        if(!(mValue.equalsIgnoreCase("-1"))){
                            metricType = properties.getProperty(mName);
                            log.debug("MetricType,Category===="+metricType);
                            metricToken = metricType.split(",");
                            reportData.setCategory(metricToken[1]);
                            reportData.setHost(hostName);
                             if(MetricType.equalsIgnoreCase("TOBEPROCESSED")){
                                 reportData.setMetricType("tobeprocessed"+mName);
                             }else if(MetricType.equalsIgnoreCase("QUEUESIZE")){
                                  reportData.setMetricType("queuesize");
                                  log.debug("QueueSize==="+reportData.getMetricType());
                             }else{
                                  reportData.setMetricType(metricToken[0]);
                             }
                            reportData.setTimestamp1(timestamp);
                            reportData.setValue(mValue);
                            reportData.setResourceId(resourceId);
                            reportData.setEventID(eventID);
                            log.debug("Setting The Report Data Sucessfull");
                            metricValueMap.put(metricsToken[i], reportData);
                        }
                    }
                }
            }
            SadeskHealthMonContext contextSetter = new SadeskHealthMonContext( );
            metricValueMap = contextSetter.setContextsInMap(metricValueMap);
            this.insertStatus = dataInsert.sendToDatabse(metricValueMap,customerID);
            contextSetter = null;
        } catch (Exception e) {
            log.error("Exception :"+e.getMessage());
            e.printStackTrace();
        }finally{
            metricsToken = null;
            timestamp = null;
            startTIme = null;
            endTime = null;
            resourceId = null;
            hostName = null;
            status = null;
            etl = null;
            logmetricType=null;
            metricValueMap=null;
            dataInsert=null;
        }
    }

    public double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");// like 2.35 for// 2.356789..
        return Double.valueOf(twoDForm.format(d));
    }


    public int getInsertStatus(){
        return this.insertStatus;
    }

}
