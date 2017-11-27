/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsers;

import com.prokosha.adapter.etl.ETLAdapter.*;
import contextSetters.FroniusContext;
import com.prokosha.dbconnection.ConnectionDAO;
import independentDatabaseQuery.QueryObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author mahesh
 */

/*
 Make objects of reportdata in Fronius on basis of
 sthreads, BACKENDS, UPTIME, OPENTABLES,
 THREADS,BACKENDS,UPTIME,OPENTABLES
 */
public class FroniusParser {

    HashMap<String, ReportData> metricValueMap = new HashMap<String, ReportData>();
    HashMap<String, ReportData> availMetricValueMap = new HashMap<String, ReportData>();
    private static Logger log = Logger.getLogger(FroniusParser.class.getName());
    ETLAdapter etl = null;
    PersistReportData dataInsert = null;
    int insertStatus = 0;

    public FroniusParser(String CEPEvent, String metrics, Properties properties, String eventID) {
        initialize(CEPEvent, metrics, properties, eventID);
    }

    public void initialize(String CEPEvent, String metrics, Properties properties, String eventID) {
        String metricsToken[] = null;

        String hostName = null;
        String timestamp1 = null;
        String resourceType = null;
        String resourceSubType = null;
        String resourceId = null;
        String service = null;
        String subService = null;
        String hostGroup = null;
        String customerID = null;
        String availability = "1";// default avilability should be 1
        int cCustID = -1;

        ReportData reportData = null;
        String metricType = null;
        String metricToken[] = null;
        boolean flag1 = true;
        boolean flag2 = true;
        boolean gotoContextFlag = false;


        try {
            String defaultKeyConstants[] = CEPEventMetricsMapping.getSzdefaultConstants();
            String parseToken[] = CEPEvent.split(",");
            metricsToken = metrics.split(" ");

            dataInsert = new PersistReportData();
            Map<String, String> mp_string_to_map = convertStringToMap(CEPEvent);

            customerID = mp_string_to_map.get(defaultKeyConstants[3].trim());
            hostName = mp_string_to_map.get(defaultKeyConstants[10].trim());
            timestamp1 = mp_string_to_map.get(defaultKeyConstants[0]);
            resourceType = mp_string_to_map.get(defaultKeyConstants[4]);
            resourceSubType = mp_string_to_map.get(defaultKeyConstants[1]);
            resourceId = mp_string_to_map.get(defaultKeyConstants[2]);
            availability = mp_string_to_map.get(defaultKeyConstants[14]);
            //cCustID = Integer.parseInt(mp_string_to_map.get(defaultKeyConstants[19]));
            String cname = mp_string_to_map.get(defaultKeyConstants[19]);
            System.out.println("><><>< mp_string_to_map = " + mp_string_to_map);
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
                    /*ResultSet rs = stmt.executeQuery("Select service,subservice,hostgroup,resourceType,resourceSubType,resourceId "
                     + "from hostinfo where lower(resourceId)=lower('" + resourceId + "') and customerid='" + cCustID + "'");*/
                    ResultSet rs = stmt.executeQuery("Select service,subservice,hostgroup,resourceType,resourceSubType,resourceId,customerid "
                            + "from hostinfo where lower(resourceId)=lower('" + resourceId + "') and customerid="
                            + "(select id from customerinfo where customername='" + cname + "')");
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
                        cCustID = rs.getInt("customerid");
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
                reportData.setHostGroup(hostGroup);

                availMetricValueMap.put("Avail", reportData);
                log.debug("Down state of fronius occured.");

            } else if (availability.equalsIgnoreCase("1")) { //server is up
                reportData = new ReportData();
                log.debug("Up state of fronius again occuppied.");

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
                reportData.setHostGroup(hostGroup);

                availMetricValueMap.put("Avail", reportData);
            }

            if (gotoContextFlag) {
                FroniusContext contextSetter = new FroniusContext(properties);
                availMetricValueMap = contextSetter.setContextsInMap(availMetricValueMap);
                contextSetter = null;
            }
            //log.debug("Inside Fronius parser availMetricValueMap .size..." + availMetricValueMap.size());
            //System.out.println("In Fronius reportData .getSuper() = " + reportData.getSuper());
            dataInsert.sendAvailToDatabse(availMetricValueMap, customerID);

            System.out.println("continuing ");
            int metricLen = metricsToken.length;
            int parseLen = parseToken.length;
            //log.debug("metricLen = metricsToken.length  (" + metricLen + ")");

            if (availability.equalsIgnoreCase("1")) {
                String mName = null;
                String mValue = null;
                String token[] = null;
                boolean vfound = false;
                for (int i = 0; i < metricLen; i++) {
                    mName = null;
                    mValue = null;
                    metricType = null;
                    metricToken = null;
                    reportData = null;
                    token = null;
                    vfound = false;
                    //for (int j = 0; j < parseLen && (vfound == false); j++) {
                    //token = parseToken[j].trim().split("=");
                    if (mp_string_to_map.containsKey(metricsToken[i])) {
                        vfound = true;
                        mName = metricsToken[i];
                        mValue = mp_string_to_map.get(metricsToken[i]);
                        //log.debug("FroniusParser initialize mName = " + mName + ", mValue = " + mValue);
                        mValue = mValue.split(";")[0];// in case of mssql when the metiic valuse is coming in the format of
                        //   CPU_BUSY=0.19;80;90
                        //   IO_BUSY=0.06;80;90
                        //   LATCH_WAITS_PER_SEC=0.06;10;50,
                        reportData = new ReportData();

                        Double dmValue = Double.parseDouble(mValue);
                        //if (!(dmValue < 0)) {
                            metricType = properties.getProperty(mName);
                            //log.debug("MetricType,Category====" + metricType);
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

                            //log.debug("Setting The Report Data Sucessfull");
                            metricValueMap.put(metricsToken[i], reportData);
                            //log.debug("Inside Fronius parser  metricValueMap .size..." + availMetricValueMap.size());
                        //}
                    }
                    //}
                    mName = null;
                    mValue = null;
                    metricType = null;
                    metricToken = null;
                    reportData = null;
                    token = null;
                }
                if (gotoContextFlag) {
                    FroniusContext contextSetter = new FroniusContext(properties);
                    metricValueMap = contextSetter.setContextsInMap(metricValueMap);
                    //log.debug("metricValueMap.size()  = " + metricValueMap.size());
                    contextSetter = null;
                }
                //log.debug("metricValueMap.size()  = " + metricValueMap.size());
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
            availMetricValueMap = null;
            metricValueMap = null;
            dataInsert = null;
            availability = null;
            reportData = null;
            metricType = null;
            metricToken = null;

        }
    }

    public Map<String, String> convertStringToMap(String szCepEvent) {
        Map<String, String> metrics_map = new HashMap<String, String>();
        String splitByComma[] = szCepEvent.split(",");

        for (int i = 0; i < splitByComma.length; i++) {
            String keyValueSplit[] = splitByComma[i].split("=");
            metrics_map.put(keyValueSplit[0].trim(), keyValueSplit[1].trim());
        }

        //System.out.println("After Converted==" + metrics_map);
        return metrics_map;
    }

    public int getInsertStatus() {
        return this.insertStatus;
    }
}
