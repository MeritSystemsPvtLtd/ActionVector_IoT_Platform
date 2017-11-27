/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.adapter.etl.ETLAdapter;

import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 *
 * @author Anand
 */
public class CEPEvent {

    Logger log = Logger.getLogger(CEPEvent.class.getName());
    private Vector stateVector = new Vector(3);
    HashMap<String, ReportData> metricValueMap = new HashMap<String, ReportData>();
    HashMap<String, ReportData> metricValueMap1 = new HashMap<String, ReportData>();
    ETLAdapter etl = null;
    PersistReportData dataInsert = null;
    static boolean mapInitialized = false;
    static Properties properties = null;
    static Enumeration enum1 = null;
    String metricValue = null;

    public CEPEvent(String CEPEvent, String metrics, Properties properties) {
        initialize(CEPEvent, metrics, properties);
    }

    public void initialize(String CEPEvent, String metrics, Properties properties) {
        String metricsToken[] = null;
        double diskTotal = 0.0;
        double diskFree = 0.0;
        String timestamp = null;
        String startTIme = null;
        String endTime = null;
        String size = null;
        String hostName = null;
        String customerID = null;
        double memFree = 0.0;
        double memTotal = 0.0;
        Double mem_usg = 0.0;
        Double disk_boot = 0.0;
        String status = null;
        double tmax = 0;
        double tn = 0;
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

            for (int j = 0; j < parseToken.length; j++) {
                String token[] = parseToken[j].split("=");
                if (token[0].trim().equalsIgnoreCase("HostName")) {
                    hostName = token[1];
                    //log.debug("HostName===" + hostName);
                    break;
                }
            }

            for (int j = 0; j < parseToken.length; j++) {
                String token[] = parseToken[j].split("=");
                if (token[0].trim().equalsIgnoreCase("TimeStamp")) {
                    timestamp = token[1];
                    //log.debug("Timestamp===" + timestamp);
                    break;
                }
            }
            for (int j = 0; j < parseToken.length; j++) {
                String token[] = parseToken[j].split("=");
                if (token[0].trim().equalsIgnoreCase("TMAX")) {
                    tmax = Double.parseDouble(token[1]);
                    //log.debug("Timestamp===" + timestamp);
                    break;
                }
            }
            for (int j = 0; j < parseToken.length; j++) {
                String token[] = parseToken[j].split("=");
                if (token[0].trim().equalsIgnoreCase("TN")) {
                    tn = Double.parseDouble(token[1]);
                    //log.debug("Timestamp===" + timestamp);
                    break;
                }
            }

            if (tn != -1.1 && tmax != -1.1) {
                ReportData reportData = null;
                String metricType = null;
                String metricToken[] = null;

                if (tn > 4 * tmax) { //server is down
                    reportData = new ReportData();
                    metricType = properties.getProperty("Availability");
                    metricToken = metricType.split(",");
                    reportData.setCategory(metricToken[1]);
                    reportData.setHost(hostName);
                    reportData.setMetricType(metricToken[0]);
                    reportData.setTimestamp1(timestamp);
                    reportData.setValue("DOWN");
                    metricValueMap1.put("Avail", reportData);
                    dataInsert.sendAvailToDatabse(metricValueMap1, customerID);

                } else {//server is up
                    reportData = new ReportData();
                    metricType = properties.getProperty("Availability");
                    metricToken = metricType.split(",");
                    reportData.setCategory(metricToken[1]);
                    reportData.setHost(hostName);
                    reportData.setMetricType(metricToken[0]);
                    reportData.setTimestamp1(timestamp);
                    reportData.setValue("UP");
                    metricValueMap1.put("Avail", reportData);
                    dataInsert.sendAvailToDatabse(metricValueMap1, customerID);
                }
            }

            String mValue = null;
            String mName = null;
            String metricType = null;
            String metricToken[] = null;
            ReportData reportData = null;
            
            for (int i = 0; i < metricsToken.length; i++) {
                mValue = null;
                mName = null;
                metricType = null;
                metricToken = null;
                reportData = null;
                boolean vfound = false;
                for (int j = 0; j < parseToken.length && (vfound == false); j++) {
                    String token[] = parseToken[j].trim().split("=");

                    if (metricsToken[i].equalsIgnoreCase(token[0])) {
                        reportData = new ReportData();
                        vfound = true;
                        mValue = token[1];
                        mName = token[0];

                        metricType = properties.getProperty(mName);
                        // log.debug("MetricType,Category====" + metricType);
                        if (metricType != null) {
                            metricToken = metricType.split(",");
                            reportData.setCategory(metricToken[1]);
                            reportData.setHost(hostName);
                            reportData.setStatus(status);
                            reportData.setEndTime(endTime);
                            reportData.setStartTime(startTIme);
                            reportData.setSize(size);
                            reportData.setMetricType(metricToken[0]);
                            reportData.setTimestamp1(timestamp);
                            reportData.setValue(mValue);
                            // log.debug("Setting The Report Data Sucessfull");
                            metricValueMap.put(metricsToken[i], reportData);
                        } else {
                            log.debug("***Message*****Metric Type & category is not cofigures for  Mertic==" + mName);
                        }


                    } else if ("DiskTotal".equalsIgnoreCase(token[0])) {
                        mValue = token[1];
                        mName = token[0];
                        diskTotal = Double.parseDouble(mValue);

                    } else if ("MemTotal".equalsIgnoreCase(token[0])) {
                        mValue = token[1];
                        mName = token[0];
                        memTotal = Double.parseDouble(mValue);

                    } else if ("DiskFree".equalsIgnoreCase(token[0])) {
                        //log.debug("Disk Metric Found");
                        mValue = token[1];
                        mName = token[0];
                        diskFree = Double.parseDouble(mValue);
                        
                        if (diskTotal != -1.1 && diskFree != -1.1) {
                            reportData = new ReportData();
                            //log.debug("Diskfree=="+diskFree+"Disktotal=="+diskTotal);
                            disk_boot = roundTwoDecimals((diskFree * 100) / diskTotal);
                            log.debug("DiskUsage" + disk_boot);

                            metricType = properties.getProperty("disk");
                            metricToken = metricType.split(",");
                            reportData.setCategory(metricToken[1]);
                            reportData.setHost(hostName);
                            reportData.setMetricType(metricToken[0]);
                            reportData.setTimestamp1(timestamp);
                            reportData.setValue(disk_boot.toString());
                            metricValueMap.put("disk", reportData);

                        } else {
                            log.debug("***Message**** Disk Values Are Null");
                        }

                    } else if ("MemFree".equalsIgnoreCase(token[0])) {
                        log.debug("Memory Metric Found");
                        mValue = token[1];
                        mName = token[0];
                        memFree = Double.parseDouble(mValue);
                        
                        if (memTotal != -1.1 && memFree != -1.1) {
                            reportData = new ReportData();
                            //    log.debug("memFree=="+memFree+"memTotal=="+memTotal);
                            mem_usg = roundTwoDecimals((memFree * 100) / memTotal);
                            log.debug("MEm_usg" + mem_usg);

                            metricType = properties.getProperty("memory");
                            metricToken = metricType.split(",");
                            reportData.setCategory(metricToken[1]);
                            reportData.setHost(hostName);
                            reportData.setMetricType(metricToken[0]);
                            reportData.setTimestamp1(timestamp);
                            reportData.setValue(mem_usg.toString());
                            metricValueMap.put("memory", reportData);

                        } else {
                            log.debug("***Message**** Memory Values Are -1.1");
                        }
                    }
                }
            }
            dataInsert.sendToDatabse(metricValueMap, customerID);
            //}// else end here
        } catch (Exception e) {
            log.error("Exception :" + e.getMessage());
            e.printStackTrace();
        } finally {
            metricsToken = null;
            timestamp = null;
            startTIme = null;
            endTime = null;
            size = null;
            hostName = null;
            status = null;
            etl = null;
            metricValueMap = null;
            dataInsert = null;
        }

    }

    public double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");// like 2.35 for
        // 2.356789..
        return Double.valueOf(twoDForm.format(d));
    }
}
