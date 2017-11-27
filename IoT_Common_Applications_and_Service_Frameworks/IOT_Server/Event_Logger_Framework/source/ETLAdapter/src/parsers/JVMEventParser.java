/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsers;

import com.prokosha.adapter.etl.ETLAdapter.*;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import contextSetters.JVMEventContext;
import com.prokosha.dbconnection.ConnectionDAO;
import independentDatabaseQuery.QueryObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

public class JVMEventParser {

    HashMap<String, ReportData> metricValueMap = new HashMap<String, ReportData>();
    HashMap<String, ReportData> availMetricValueMap = new HashMap<String, ReportData>();
    private static Logger log = Logger.getLogger(JVMEventParser.class.getName());
    ETLAdapter etl = null;
    PersistReportData dataInsert = null;
    int insertStatus = 0;

    public JVMEventParser(String CEPEvent, String metrics, Properties properties, String eventID) {
        initialize(CEPEvent, metrics, properties, eventID);
    }

    public void initialize(String CEPEvent, String metrics, Properties properties, String eventID) {
        String metricsToken[] = null;

        String hostName = null;
        String timestamp1 = null;
        String resourceType = null;
        String resourceSubType = null;
        String resourceId = null;//Host+jmxurl:port
        String jmxurl = null;
        String service = null;
        String subService = null;
        String hostGroup = null;
        String customerID = null;// default avilability should be 1
        String availability = "1";// default avilability should be 1
        int cCustID = -1;

        String metricType = null;
        String metricToken[] = null;
        ReportData reportData = null;
        boolean flag1 = true;
        boolean flag2 = true;
        boolean gotoContextFlag = false;

        try {
            String parseToken[] = CEPEvent.split(",");
            metricsToken = metrics.split(" ");

            dataInsert = new PersistReportData();
            String defaultKeyConstants[] = CEPEventMetricsMapping.getSzdefaultConstants();
            Map<String, String> mp_string_to_map = convertStringToMap(CEPEvent);

            customerID = mp_string_to_map.get(defaultKeyConstants[3].trim());

            hostName = mp_string_to_map.get(defaultKeyConstants[15].trim());

            timestamp1 = mp_string_to_map.get(defaultKeyConstants[16]);
            /*String df = "yyyy/MM/dd HH:mm:ss";
            SimpleDateFormat smdf = new SimpleDateFormat(df);
            Date d = smdf.parse(timestamp1);
            timestamp1 = ((Long) d.getTime()).toString();
            timestamp1 = timestamp1.substring(0, 10);*/


            resourceType = mp_string_to_map.get(defaultKeyConstants[4]);


            resourceSubType = mp_string_to_map.get(defaultKeyConstants[1]);



            resourceId = mp_string_to_map.get(defaultKeyConstants[2]);



            availability = mp_string_to_map.get(defaultKeyConstants[14]);
            
            cCustID = Integer.parseInt(mp_string_to_map.get(defaultKeyConstants[19]));





            flag1 = resourceType == null || resourceSubType == null || resourceId == null || resourceType.equalsIgnoreCase("null") || resourceSubType.equalsIgnoreCase("null") || resourceId.equalsIgnoreCase("null");
            flag2 = service == null || subService == null || hostGroup == null || service.equalsIgnoreCase("null") || subService.equalsIgnoreCase("null") || hostGroup.equalsIgnoreCase("null");
            if (flag1 || flag2) {
                if (resourceId != null) {

                    //query with database to find
                    //resourceType, resourceSubType, resourceId,
                    //services, Subservice, hostgroup
                    //QueryObject qObj = new QueryObject();
                    Connection connection = ConnectionDAO.getConnection(customerID);

                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery("Select service,subservice,hostgroup,resourceType,resourceSubType,resourceId "
                            + "from hostinfo where lower(resourceId)=lower('" + resourceId + "') and customerid='" + cCustID + "'");
                    while (rs.next()) {
                        if (service == null || service.equalsIgnoreCase("null")) {
                            service = rs.getString("service");
                        }
                        if (subService == null || subService.equalsIgnoreCase("null")) {
                            subService = rs.getString("subservice");
                        }
                        if (hostGroup == null || hostGroup.equalsIgnoreCase("null")) {
                            hostGroup = rs.getString("hostgroup");
                        }

                        if (resourceType == null || resourceType.equalsIgnoreCase("null")) {
                            resourceType = rs.getString("resourceType");
                        }
                        if (resourceSubType == null || resourceSubType.equalsIgnoreCase("null")) {
                            resourceSubType = rs.getString("resourceSubType");
                        }
                    }

                    //qObj = null;
                    //connection = null;

                    flag1 = resourceType == null || resourceSubType == null || resourceType.equalsIgnoreCase("null") || resourceSubType.equalsIgnoreCase("null");
                    flag2 = service == null || subService == null || hostGroup == null || service.equalsIgnoreCase("null") || subService.equalsIgnoreCase("null") || hostGroup.equalsIgnoreCase("null");
                    if (flag1 || flag2) {
                        gotoContextFlag = true;
                    }

                } else {
                    gotoContextFlag = true;
                }
            }

            if (availability.equalsIgnoreCase("0")) { //server is down
                reportData = new ReportData();

                metricType = properties.getProperty("Availability");
                metricToken = metricType.split(",");

                reportData.setCategory(metricToken[1]);
                reportData.setHost(hostName);
                reportData.setService(service);
                reportData.setSubService(subService);
                reportData.setMetricType(metricToken[0]);
                reportData.setTimestamp1(timestamp1);
                reportData.setValue("DOWN");
                reportData.setResourceType(resourceType);
                reportData.setResourceSubType(resourceSubType);
                reportData.setResourceId(resourceId);
                reportData.setEventID(eventID);
                reportData.setAvailabilty(availability);
                reportData.setCCustomerID(cCustID);

                availMetricValueMap.put("Avail", reportData);
                log.debug("Down state of JVM occured.");

            } else if (availability.equalsIgnoreCase("1")) { //server is up
                reportData = new ReportData();
                log.debug("Up state of JVM again occuppied.");

                metricType = properties.getProperty("Availability");
                metricToken = metricType.split(",");

                reportData.setCategory(metricToken[1]);
                reportData.setHost(hostName);
                reportData.setService(service);
                reportData.setSubService(subService);
                reportData.setMetricType(metricToken[0]);
                reportData.setTimestamp1(timestamp1);
                reportData.setValue("UP");
                reportData.setResourceType(resourceType);
                reportData.setResourceSubType(resourceSubType);
                reportData.setResourceId(resourceId);
                reportData.setEventID(eventID);
                reportData.setAvailabilty(availability);
                reportData.setCCustomerID(cCustID);

                availMetricValueMap.put("Avail", reportData);
            }

            if (gotoContextFlag) {
                JVMEventContext contextSetter = new JVMEventContext(properties);
                availMetricValueMap = contextSetter.setContextsInMap(availMetricValueMap);
                contextSetter = null;
            }
            log.debug("Inside JVMEvent parser availMetricValueMap .size..." + availMetricValueMap.size());
            dataInsert.sendAvailToDatabse(availMetricValueMap, customerID);

            int metricLen = metricsToken.length;//no of object based on required metrics
            int parseLen = parseToken.length;//total no of metrics in the event stream

            if (availability.equalsIgnoreCase("1")) { //server is up
                for (int i = 0; i < metricLen; i++) {
                    String mName = null;
                    String mValue = null;
                    String token[] = null;
                    metricType = null;
                    metricToken = null;
                    reportData = null;
                    boolean vfound = false;
                    //for (int j = 0; j < parseLen && (vfound == false); j++) {

                    if (mp_string_to_map.containsKey(metricsToken[i])) {
                        vfound = true;
                        mName = metricsToken[i];
                        mValue = mp_string_to_map.get(metricsToken[i]);
                        reportData = new ReportData();
                        Double dmValue = Double.parseDouble(mValue);
                        if (!(dmValue < 0)) {

                            metricType = properties.getProperty(mName);
                            log.debug("MetricType,Category====" + metricType);


                            metricToken = metricType.split(",");

                            reportData.setHost(hostName);
                            reportData.setService(service);
                            reportData.setSubService(subService);
                            reportData.setHostGroup(hostGroup);
                            reportData.setTimestamp1(timestamp1);

                            reportData.setMetricType(metricToken[0]);
                            reportData.setValue(mValue);
                            reportData.setCategory(metricToken[1]);
                            reportData.setEventID(eventID);

                            reportData.setResourceType(resourceType);
                            reportData.setResourceSubType(resourceSubType);
                            reportData.setResourceId(resourceId);
                            reportData.setAvailabilty(availability);
                            reportData.setCCustomerID(cCustID);

                            log.debug("Setting The Report Data Sucessfull");
                            metricValueMap.put(metricsToken[i], reportData);
                        }
                    }
                    //  }
                    mName = null;
                    mValue = null;
                    metricType = null;
                    metricToken = null;
                    reportData = null;
                    token = null;
                }

                if (gotoContextFlag) {
                    JVMEventContext contextSetter = new JVMEventContext(properties);
                    metricValueMap = contextSetter.setContextsInMap(metricValueMap);
                    contextSetter = null;
                }
                this.insertStatus = dataInsert.sendToDatabse(metricValueMap, customerID);

            }
        } catch (Exception e) {
            log.error("Exception :" + e.getMessage());
            e.printStackTrace();
            if(e.getMessage().contains("java.net.SocketException: Broken pipe")){
                            System.out.println("SokectException :Broken pipe");
                        System.out.println("calling ConDAO.closeConn()");
                ConnectionDAO.closeConnection(customerID);
                System.out.println("exited from ConDAO.closeCon");
                        }
        } finally {
            metricsToken = null;
            hostName = null;
            timestamp1 = null;
            resourceType = null;
            resourceSubType = null;
            resourceId = null;
            service = null;
            subService = null;
            hostGroup = null;
            availability = null;
            availability = null;
            metricValueMap = null;
            availMetricValueMap = null;
            dataInsert = null;
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