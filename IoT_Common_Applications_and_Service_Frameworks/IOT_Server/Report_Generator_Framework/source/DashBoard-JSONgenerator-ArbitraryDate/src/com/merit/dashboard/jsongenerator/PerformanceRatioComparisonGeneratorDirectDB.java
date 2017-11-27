/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.merit.dashboard.jsongenerator;

import com.prokosha.dbconnection.ConnectionDAO;
import com.merit.dashboard.util.ResourceConfiguration;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import org.apache.log4j.Logger;

/**
 *
 * @author rekhas
 */
public class PerformanceRatioComparisonGeneratorDirectDB {

    static Logger log = Logger.getLogger(PerformanceRatioComparisonGeneratorDirectDB.class);

    public static String generateJSON(String resType, String cCustomer, String service, String resType2, String metricType, String configuredMetricType,
            long sMilli, long eMilli, String timeStampSelection, String customer) {
        String resultJson = "[]";
        try {
            metricType = metricType.replaceFirst("XXX", timeStampSelection);
            //configuredMetricType = configuredMetricType.replaceFirst("XXX", timeStampSelection);
            log.info("metricType==>>" + metricType);
            log.info("configuredMetricType==>>" + configuredMetricType);
            log.info("resType==>>" + resType);
            log.info("resType2==>>" + resType2);
            log.debug("In PerformanceRatioComparisonGenerator.generateJSON==>>smilli:" + sMilli + "::emilli:" + eMilli);
            String[] headingNamesMetric1 = null;
            String szQuery;
            if (service == null) {//ccustomer level json
                resultJson = "[]";
            } else if (resType2.equals("")) {//service level json
                szQuery = "select w1.energy as Yield, w1.energy as PerformanceRatio,w1.energy as EnergyPerRatedPower, w1.ServiceName as ServiceName,"
                        + "w1.resourcetype as ResourceType, w1.resourceid as ResourceID from (select t.resourceid, t.service as ServiceName, t.resourcetype as ResourceType,t.metricvalue as energy,"
                        + " t.metrictype from(select customerid,service,resourcetype,resourceid,metrictype, max(timestamp1) as MaxTime from servicemetrics t1 "
                        + "where t1.resourcetype='" + resType + "' and t1.metrictype = '" + metricType + "' and t1.customerid=(select id from customerinfo "
                        + "where customername='" + cCustomer + "') and t1.service = '" + service + "' and t1.timestamp1 between " + sMilli + " and " + eMilli
                        + " group by t1.customerid,t1.service,t1.metrictype,t1.resourcetype,t1.resourceid) r inner join servicemetrics t on t.service=r.service and "
                        + "t.resourcetype=r.resourcetype and t.timestamp1 = r.MaxTime and t.metrictype=r.metrictype and t.customerid=r.customerid and t.resourceid=r.resourceid "
                        + "group by t.customerid,t.service,t.resourcetype,t.metrictype,t.metricvalue,t.resourceid) w1";
                headingNamesMetric1 = new String[]{"ServiceName", "ResourceType", "ResourceID", "Yield", "PerformanceRatio", "EnergyPerRatedPower"};
                log.info("PerformanceRatioGenerator query==>>" + szQuery);
                resultJson = generateDerivedJsonFromGivenQueryForCustomer(szQuery, headingNamesMetric1, customer, cCustomer, resType2, configuredMetricType,
                        timeStampSelection);
                log.debug("resultJson at service level==>>" + resultJson);
            } else if (resType2.equals(resType)) {//device level json
                resultJson = "[]";
            }
            if (service != null) {//dont modify json for cCustomer level
                //log.debug("resultjson before modifyJson is==>>"+resultJson);
                if (!(resultJson.equals("[]"))) {
                    //resultJson = modifyJson(resultJson);
                }
            }

            return resultJson;
        } catch (Exception e) {
            log.error("Error in generateJSON==>>" + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public static String generateDerivedJsonFromGivenQueryForCustomer(String szQuery, String[] headingNames, String customer, String cCustomer,
            String resType, String configuredMetricType, String timeStampSelection) throws SQLException {
        String perfJSON = "";
        ResultSet rs4 = null;
        try {
            String szMetricTypeValueJson = "";
            perfJSON = "[";
            rs4 = ConnectionDAO.executerQuery(szQuery, customer);
            String szConcatColumn;//
            String szResID;//
            String service;//
            String derivedVal;//
            String szDCEnergyMetricName;//
            String DCXXXEnergy;//
            String InstalledCapacity;//
            String szEnergy;//
            Double val;//
            DecimalFormat df;//
            while (rs4.next()) {
                szConcatColumn = "";
                szMetricTypeValueJson = "";
                szResID = null;

                service = null;
                log.info("generateDerivedJsonFromGivenQuery resType==>>" + resType);
                service = rs4.getString("ServiceName");
                szResID = rs4.getString("ResourceID");
                if (resType.equals("")) {
                } else {
                    if (szResID.equals(service)) {
                        continue;
                    }
                }
                log.info("szResID==>>" + szResID);
                derivedVal = null;
                szDCEnergyMetricName = "DCXXXEnergy";
                DCXXXEnergy = ResourceConfiguration.getConfValue(customer, cCustomer, service, "Default",
                        szResID, szDCEnergyMetricName.replaceFirst("XXX", timeStampSelection));
                InstalledCapacity = ResourceConfiguration.getConfValue(customer, cCustomer, service, "Default",
                        szResID, "Pmax");
                log.debug("Pmax for " + customer + ":" + cCustomer + ":" + service + ":" + szResID + " is " + InstalledCapacity);

                for (int i = 0; i < headingNames.length - 1; i++) {
                    if (rs4.getString(headingNames[i]) != null) {
                        if (headingNames[i].equals("PerformanceRatio")) {
                            if (DCXXXEnergy != null) {
                                szEnergy = rs4.getString(headingNames[i]);
                                log.debug("cust level::szEnergy==>>" + szEnergy);
                                val = (Double.parseDouble(szEnergy)) / (Double.parseDouble(DCXXXEnergy));
                                log.debug("cust level::val==>>" + val);
                                df = new DecimalFormat("#.##");
                                derivedVal = df.format(val);
                            } else {
                                derivedVal = "";
                            }
                            szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":\"" + derivedVal + "\"";
                        } else if (headingNames[i].equals("EnergyPerRatedPower")) {
                            if (InstalledCapacity != null) {
                                szEnergy = rs4.getString(headingNames[i]);
                                val = (Double.parseDouble(szEnergy)) / (Double.parseDouble(InstalledCapacity));
                                df = new DecimalFormat("#.##");
                                derivedVal = df.format(val);
                            } else {
                                derivedVal = "";
                            }
                            szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":\"" + derivedVal + "\"";
                        } else if (headingNames[i].equals("TimeStamps")) {
                            szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":[[" + rs4.getString(headingNames[i]) + "],["
                                    + rs4.getString(headingNames[i]) + "]]";
                        } else if (headingNames[i].equals("ServiceName")) {
                            szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":\"" + rs4.getString("ResourceID") + "\"";
                        } else {
                            szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":\"" + rs4.getString(headingNames[i]) + "\"";
                        }
                    }
                    szMetricTypeValueJson = szMetricTypeValueJson + "," + rs4.getString(headingNames[i]);
                }
                if (headingNames[headingNames.length - 1].equals("PerformanceRatio")) {
                    if (DCXXXEnergy != null) {
                        szEnergy = rs4.getString(headingNames[headingNames.length - 1]);
                        val = (Double.parseDouble(szEnergy)) / (Double.parseDouble(DCXXXEnergy));
                        df = new DecimalFormat("#.##");
                        derivedVal = df.format(val);
                    } else {
                        derivedVal = "";
                    }
                } else if (headingNames[headingNames.length - 1].equals("EnergyPerRatedPower")) {
                    derivedVal = "";
                    if (InstalledCapacity != null) {
                        szEnergy = rs4.getString(headingNames[headingNames.length - 1]);
                        val = (Double.parseDouble(szEnergy)) / (Double.parseDouble(InstalledCapacity));
                        df = new DecimalFormat("#.##");
                        derivedVal = df.format(val);
                    } else {
                        derivedVal = "";
                    }
                } else if (headingNames[headingNames.length - 1].equals("ServiceName")) {
                    derivedVal = rs4.getString("ResourceID");
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
            return perfJSON;
        } catch (Exception e) {
            log.error("Error in generateDerivedJsonFromGivenQuery" + e.toString());
            e.printStackTrace();
        } finally {
            ConnectionDAO.closeStatement();
            if (rs4 != null) {
                rs4.close();
                rs4 = null;
            }
        }
        return null;
    }

    /*private static String modifyJson(String input) {
     String output = "";
     boolean altered = false;
     try {
     String[] arrayEles = input.split("},");
     log.info("arrayEles length==>>" + arrayEles.length);
     String arrayE;//
     int startIndex;//
     int endIndex;//
     String perfRatioStr;//
     String energyPerRatedPowerStr;//
     String actualMTypes;//
     String resNames;//
     String acVals;//
     String exVals;//
     String resValues;//
     for (int j = 0; j < arrayEles.length; j++) {
     arrayE = arrayEles[j];
     altered = true;
     startIndex = arrayE.indexOf("\"PerformanceRatio\":\"");
     endIndex = arrayE.indexOf("\"", startIndex + new String("\"PerformanceRatio\":\"").length());
     perfRatioStr = arrayE.substring(startIndex, endIndex);
     arrayE = arrayE.replace(perfRatioStr + "\",", "");
     startIndex = arrayE.indexOf("\"EnergyPerRatedPower\":\"");
     endIndex = arrayE.indexOf("\"", startIndex + new String("\"EnergyPerRatedPower\":\"").length());
     energyPerRatedPowerStr = arrayE.substring(startIndex, endIndex);
     //arrayE = arrayE.replace(energyPerRatedPowerStr + "\"", "");
     actualMTypes = "\"ActualMetricTypes\":[\"PerformanceRatio\",\"EnergyPerRatedPower\"]";
     //arrayE += actualMTypes;
     resNames = ",\"ResourceNames\":[\"PerformanceRatio\",\"EnergyPerRatedPower\"]";
     //arrayE += resNames;
     //arrayE = arrayE.replace(energyPerRatedPowerStr + "\"", actualMTypes + resNames);
     acVals = perfRatioStr.replace("\"PerformanceRatio\":\"", "");
     exVals = energyPerRatedPowerStr.replace("\"EnergyPerRatedPower\":\"", "");
     resValues = ",\"ResourceValues\":[[" + acVals + "],[" + exVals + "]]";
     arrayE = arrayE.replace(energyPerRatedPowerStr + "\"", actualMTypes + resNames + resValues);
     if (j == (arrayEles.length - 1)) {
     arrayE = arrayE.substring(0, arrayE.length() - 2);
     }
     arrayE += ",\"SlaValues\":[0.0,0.0]";
     arrayE += ",\"Health\":[\"OK\",\"OK\"]},";
     output += arrayE;
     }
     if (altered) {
     output = output.substring(0, output.length() - 1);
     output += "]";
     }
     return output;
     } catch (Exception e) {
     log.error("Error in modifyJson" + e.getMessage());
     e.printStackTrace();
     }
     return null;
     }*/
    public static void main(String args[]) {
        String derivedVal = "";
        try {
            DecimalFormat df = new DecimalFormat("#.##");
            //df.format(val);
            derivedVal = derivedVal + df.format(0.067814278) + ",";
            System.out.println("derivedVal1==>>" + derivedVal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
