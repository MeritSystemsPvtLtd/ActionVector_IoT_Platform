/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.merit.dashboard.jsongenerator;

import com.prokosha.dbconnection.ConnectionDAO;
import com.merit.dashboard.queryexecuter.QueryExecuter;
import com.merit.dashboard.util.ResourceConfiguration;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author rekhas
 */
public class EnergyPerformanceGenerator {

    static Logger log = Logger.getLogger(EnergyPerformanceGenerator.class);

    public static String generateJSON(String resType, String cCustomer, String service, String resType2, String metricType, String configuredMetricType,
            long sMilli, long eMilli, String timeStampSelection, String customer) {
        String resultJson = "";
        String szQuery = "";
        try {
            metricType = metricType.replaceFirst("XXX", timeStampSelection);
            //configuredMetricType = configuredMetricType.replaceFirst("XXX", timeStampSelection);
            log.info("metricType==>>" + metricType);
            log.info("configuredMetricType==>>" + configuredMetricType);
            log.info("resType==>>" + resType);
            log.info("resType2==>>" + resType2);
            String[] headingNamesMetric1 = null;
            if (service == null) {//ccustomer level json
                szQuery = "SELECT t.service as ServiceName, sum(t.metricvalue) as Efficiency, sum(t.metricvalue) as ActualEnergy, t.resourcetype "
                        + "as ResourceType, t.metrictype, max(t.timestamp1) FROM (SELECT service, resourcetype, metrictype, MAX(timestamp1) as MaxTime FROM servicemetrics "
                        + "where resourcetype=service and customerid=(select id from customerinfo where "
                        + "customername='" + cCustomer + "') and metrictype='" + metricType + "' and timestamp1 between " + sMilli
                        + " and " + eMilli + " and resourceid=service "
                        + " GROUP BY service,resourcetype,metrictype) r "
                        + "INNER JOIN servicemetrics t ON t.service = r.service AND t.resourcetype = r.resourcetype AND "
                        + "t.timestamp1 = r.MaxTime AND t.metrictype = r.metrictype and t.service=t.resourceid GROUP BY t.service, t.resourcetype,t.metrictype";
                headingNamesMetric1 = new String[]{"ServiceName", "ResourceType", "Efficiency", "ActualEnergy", "ExpectedEnergy"};
            } else if (resType2.equals("")) {//service level json
                szQuery = "SELECT service as ServiceName, resourceid as ResourceID, array_to_string(array_agg(metricvalue),',') as ActualEnergy, resourcetype as ResourceType, "
                        + "array_to_string(array_agg(concat('\"',to_char(to_timestamp(timestamp1), 'yyyy/MM/dd HH24:mi:SS'),'\"')),',') as TimeStamps from servicemetrics "
                        + "where resourcetype='" + service + "' and customerid=(select id from customerinfo where customername='"
                        + cCustomer + "') and service='" + service + "' and metrictype='" + metricType + "' and timestamp1 between "
                        + sMilli + " and " + eMilli + " and resourceid='" + service + "' group by service,resourcetype,resourceid";
                headingNamesMetric1 = new String[]{"ServiceName", "ResourceType", "ResourceID", "TimeStamps", "ActualEnergy", "ExpectedEnergy"};
            } else if (resType2.equals(resType)) {//device level json
                szQuery = "SELECT service as ServiceName, resourceid as ResourceID, array_to_string(array_agg(metricvalue),',') as ActualEnergy, resourcetype as ResourceType, "
                        + "array_to_string(array_agg(concat('\"',to_char(to_timestamp(timestamp1), 'yyyy/MM/dd HH24:mi:SS'),'\"')),',') as TimeStamps from servicemetrics "
                        + "where resourcetype='" + resType + "' and customerid=(select id from customerinfo where customername='"
                        + cCustomer + "') and service='" + service + "' and metrictype='" + metricType + "' and timestamp1 between "
                        + sMilli + " and " + eMilli + " group by service,resourcetype,resourceid";
                headingNamesMetric1 = new String[]{"ServiceName", "ResourceType", "ResourceID", "TimeStamps", "ActualEnergy", "ExpectedEnergy"};
            }
            //derivedMetricType = "Efficiency";
            log.info("EfficiencyGenerator query==>>" + szQuery);
            resultJson = generateDerivedJsonFromGivenQuery(szQuery, headingNamesMetric1, customer, cCustomer, resType2, configuredMetricType,
                    timeStampSelection);
            if (service != null) {//dont modify json for cCustomer level
                if (!(resultJson.equals("[]"))) {
                    resultJson = modifyJson(resultJson);
                }
            }
            return resultJson;
        } catch (Exception e) {
            log.error("Error in generateJSON==>>" + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public static String generateDerivedJsonFromGivenQuery(String szQuery, String[] headingNames, String customer, String cCustomer,
            String resType, String configuredMetricType, String timeStampSelection) throws SQLException {
        String perfJSON = "";
        ResultSet rs4 = null;
        int valsCounter = 0;
        try {
            String szMetricTypeValueJson = "";
            perfJSON = "[";
            rs4 = ConnectionDAO.executerQuery(szQuery, customer);
            String szConcatColumn;//
            HashMap confValsMap;//
            String szResID;//
            String service;//
            String[] configuredMTypes;//
            String derivedVal;//
            String confVal;//
            String confVal2;//
            String colValue;//
            while (rs4.next()) {
                szConcatColumn = "";
                szMetricTypeValueJson = "";
                confValsMap = new HashMap();
                szResID = null;
                log.info("generateDerivedJsonFromGivenQuery resType==>>" + resType);
                if (resType.equals("")) {
                } else {
                    szResID = rs4.getString("ResourceID");
                    service = rs4.getString("ServiceName");
                    if (szResID.equals(service)) {
                        continue;
                    }
                }
                configuredMTypes = configuredMetricType.split(":");
                log.info("configuredMTypes.length==>>" + configuredMTypes.length);
                log.info("szResID==>>" + szResID);
                for (int i = 0; i < configuredMTypes.length; i++) {
                    String val = ResourceConfiguration.getConfValue(customer, cCustomer, rs4.getString("ServiceName"),"Default",
                            szResID, configuredMTypes[i]);
                    log.info("val==>>" + val);
                    confValsMap.put(configuredMTypes[i], val);
                }
                derivedVal = null;
                confVal = null;
                for (int i = 0; i < headingNames.length - 1; i++) {
                    if (rs4.getString(headingNames[i]) != null) {
                        if (headingNames[i].equals("Efficiency")) {
                            confVal = confValsMap.get("kiloWattPeak").toString();
                            log.info("confVal1==>>" + confVal);
                            if (confVal != null) {
                                derivedVal = "" + Double.parseDouble(rs4.getString(headingNames[i])) / Double.parseDouble(confVal);
                                log.info("derivedVal1==>>" + derivedVal);
                                szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":\"" + derivedVal + "\"";
                            }
                        } else if (headingNames[i].equals("ExpectedEnergy")) {
                            confVal = confValsMap.get("InstalledkiloWatt").toString();
                            confVal2 = null;
                            confVal2 = confValsMap.get("OperatingHours").toString();
                            Float confVal2Float = Float.parseFloat(confVal2);
                            if (timeStampSelection.equalsIgnoreCase("Week")) {
                                confVal2Float = confVal2Float * 7;
                            } else if (timeStampSelection.equalsIgnoreCase("Month")) {
                                confVal2Float = confVal2Float * 30;
                            } else if (timeStampSelection.equalsIgnoreCase("Year")) {
                                confVal2Float = confVal2Float * 365;
                            }
                            log.info("confVal1==>>" + confVal);
                            log.info("confVal2==>>" + confVal);
                            if ((confVal != null) && (confVal2 != null)) {
                                 colValue = "";
                                //szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":\""
                                //      + (Float.parseFloat(confVal) * confVal2Float) + "\"";
                                //code change for json to match with spaneos requirement
                                derivedVal = "" + (Float.parseFloat(confVal) * confVal2Float);
                                log.info("valsCounter==>>" + valsCounter);
                                for (int k = 0; k < valsCounter; k++) {
                                    colValue += derivedVal + ",";
                                }
                                if (colValue.length() > 1) {
                                    derivedVal = colValue.substring(0, colValue.length() - 1);
                                }
                                colValue = null;
                                szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":\"" + derivedVal + "\"";
                            }
                        } else if (headingNames[i].equals("TimeStamps")) {
                            szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":[[" + rs4.getString(headingNames[i]) + "],[" 
                                    + rs4.getString(headingNames[i]) + "]]";
                            valsCounter = rs4.getString(headingNames[i]).split(",").length;
                        } else {
                            szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":\"" + rs4.getString(headingNames[i]) + "\"";
                        }
                    }
                    szMetricTypeValueJson = szMetricTypeValueJson + "," + rs4.getString(headingNames[i]);
                }
                if (headingNames[headingNames.length - 1].equals("Efficiency")) {
                    confVal = confValsMap.get("kiloWattPeak").toString();
                    log.info("confVal2==>>" + confVal);
                    if (confVal != null) {
                        derivedVal = "" + Double.parseDouble(rs4.getString(headingNames[headingNames.length - 1]))
                                / Double.parseDouble(confVal);
                        log.info("derivedVal2==>>" + derivedVal);
                    }
                } else if (headingNames[headingNames.length - 1].equals("ExpectedEnergy")) {
                    //derivedVal = ResourceConfiguration.getConfValue(customer, cCustomer, rs4.getString("ServiceName"),
                    //null, configuredMetricType);
                    confVal = confValsMap.get("InstalledkiloWatt").toString();
                    confVal2 = null;
                    confVal2 = confValsMap.get("OperatingHours").toString();
                    log.info("confVal1==>>" + confVal);
                    log.info("confVal2==>>" + confVal);
                    if ((confVal != null) && (confVal2 != null)) {
                        //derivedVal = "" + (Float.parseFloat(confVal) * Float.parseFloat(confVal2));
                        //code change for json to match with spaneos requirement
                        colValue = "";
                        derivedVal = "" + (Float.parseFloat(confVal) * Float.parseFloat(confVal2));
                        log.info("valsCounter==>>" + valsCounter);
                        for (int k = 0; k < valsCounter; k++) {
                            colValue += derivedVal + ",";
                        }
                        if (colValue.length() > 1) {
                            derivedVal = colValue.substring(0, colValue.length() - 1);
                        }
                    }
                    log.info("confVal2==>>" + derivedVal);
                } else {
                    derivedVal = rs4.getString(headingNames[headingNames.length - 1]);
                }
                szConcatColumn = szConcatColumn + ",\"" + headingNames[headingNames.length - 1] + "\":\"" + derivedVal + "\"";
                perfJSON += "{" + szConcatColumn.substring(1) + "},";
            }
            if (perfJSON.length() > 1) {
                perfJSON = perfJSON.substring(0, perfJSON.length() - 1);
            }
            perfJSON += "]";
            log.info("performance json==>>" + perfJSON);
            confValsMap = null;//modified
            return perfJSON;
        } catch (Exception e) {
            log.error("Error in generateDerivedJsonFromGivenQuery" + e.toString());
            e.printStackTrace();
        } finally {
            ConnectionDAO.closeStatement();
            if(rs4 != null){
                rs4.close();
                rs4 = null;
            }
            
            
        }
        return null;
    }

    private static String modifyJson(String input) {
        String output = "";
        try {
            String[] arrayEles = input.split("},");
            log.info("arrayEles length==>>"+arrayEles.length);
            int startIndex;//
            int endIndex;//
            String actualEnergyStr;//
            String expectedEnergyStr;//
            String actualMTypes;//
            String resNames;//
            String acVals;//
            String exVals;//
            String resValues;//
            for (String arrayE : arrayEles) {
                log.info("arrayE before modification==>>" + arrayE);
                startIndex = arrayE.indexOf("\"ActualEnergy\":\"");
                endIndex = arrayE.indexOf("\"", startIndex + new String("\"ActualEnergy\":\"").length());
                actualEnergyStr = arrayE.substring(startIndex, endIndex);
                log.info("actualEnergyStr==>>" + actualEnergyStr);
                arrayE = arrayE.replace(actualEnergyStr + "\",", "");
                startIndex = arrayE.indexOf("\"ExpectedEnergy\":\"");
                endIndex = arrayE.indexOf("\"", startIndex + new String("\"ExpectedEnergy\":\"").length());
                expectedEnergyStr = arrayE.substring(startIndex, endIndex);
                log.info("expectedEnergyStr==>>" + expectedEnergyStr);
                //arrayE = arrayE.replace(expectedEnergyStr + "\"", "");
                actualMTypes = "\"ActualMetricTypes\":[\"ActualEnergy\",\"ExpectedEnergy\"]";
                //arrayE += actualMTypes;
                resNames = ",\"ResourceNames\":[\"ActualEnergy\",\"ExpectedEnergy\"]";
                //arrayE += resNames;
                //arrayE = arrayE.replace(expectedEnergyStr + "\"", actualMTypes + resNames);
                acVals = actualEnergyStr.replace("\"ActualEnergy\":\"","");
                exVals = expectedEnergyStr.replace("\"ExpectedEnergy\":\"","");
                resValues = ",\"ResourceValues\":[[" + acVals + "],[" + exVals + "]]";
                arrayE = arrayE.replace(expectedEnergyStr + "\"", actualMTypes + resNames + resValues);
                log.info("arrayE after modification==>>" + arrayE);
                output += arrayE;
            }
            return output;
        } catch (Exception e) {
            log.error("Error in modifyJson" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
