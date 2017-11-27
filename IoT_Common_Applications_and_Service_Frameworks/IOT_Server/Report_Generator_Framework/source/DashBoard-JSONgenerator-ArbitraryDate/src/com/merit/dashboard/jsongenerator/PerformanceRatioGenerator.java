/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.merit.dashboard.jsongenerator;

import com.prokosha.dbconnection.ConnectionDAO;
import com.merit.dashboard.DBUtil.DBUtilHelper;
import com.merit.dashboard.DateGenerator;
import com.merit.dashboard.util.ResourceConfiguration;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author rekhas
 */
public class PerformanceRatioGenerator {

    static Logger log = Logger.getLogger(PerformanceRatioGenerator.class);

    public static String generateJSON(String resType, String cCustomer, String service, String resType2, String metricType, String configuredMetricType,
            long sMilli, long eMilli, String timeStampSelection, String customer) {
        String resultJson = "[]";
        String[] headingNamesMetric1 = null;
        String szQuery = "";
        try {
            //configuredMetricType = configuredMetricType.replaceFirst("XXX", timeStampSelection);
            System.out.println("metricType==>>" + metricType);
            System.out.println("configuredMetricType==>>" + configuredMetricType);
            System.out.println("resType==>>" + resType);
            System.out.println("resType2==>>" + resType2);
            System.out.println("service==>>" + service);
            System.out.println("timeStampSelection==>>" + timeStampSelection);
            System.out.println("customer==>>" + customer);

            System.out.println("In PerformanceRatioGenerator.generateJSON==>>smilli:" + sMilli + "::emilli:" + eMilli);
            if (service == null) {//ccustomer level json
               /* szQuery = "select w1.energy as ActualEnergy, w1.energy as PerformanceRatio,w1.energy as EnergyPerRatedPower, w1.ServiceName as ServiceName,"
                 + "w1.resourcetype as ResourceType  from (select t.service as ServiceName, t.resourcetype as ResourceType,t.metricvalue as energy,"
                 + " t.metrictype from(select customerid,service,resourcetype,metrictype, max(timestamp1) as MaxTime from servicemetrics t1 "
                 + "where t1.resourcetype=service and t1.metrictype = '" + metricType + "' and t1.customerid=(select id from customerinfo "
                 + "where customername='" + cCustomer + "') and t1.timestamp1 between " + sMilli + " and " + eMilli
                 + " group by t1.customerid,t1.service,t1.metrictype,t1.resourcetype) r inner join servicemetrics t on t.service=r.service and "
                 + "t.resourcetype=r.resourcetype and t.timestamp1 = r.MaxTime and t.metrictype=r.metrictype and t.customerid=r.customerid "
                 + "group by t.customerid,t.service,t.resourcetype,t.metrictype,t.metricvalue,t.resourceid) w1";
                 headingNamesMetric1 = new String[]{"ServiceName", "ResourceType", "PerformanceRatio", "EnergyPerRatedPower", "ActualEnergy", "TargetEnergy"};
                 System.out.println("PerformanceRatioGenerator query==>>" + szQuery);
                 */
                // resultJson=customerJSON("Inverter", "SriGroup", null, "", "Energy", " ",
                // 1421173800000L, 1421260199000L, "year", "merit.actionvector.com");
                System.out.println("resultJson at customer level==>>");
                resultJson = getJSONFromAggregatedMetrics(resType, cCustomer, service, resType2, metricType, configuredMetricType, sMilli, eMilli, timeStampSelection, customer);
                System.out.println("resultJson at customer level==>>" + resultJson);

            } else if (resType2.equals("")) {//service level json
                //if (timeStampSelection.equalsIgnoreCase("Hour") || timeStampSelection.equalsIgnoreCase("Day")) {
                //  if (timeStampSelection.equalsIgnoreCase("Day")) {
                //    timeStampSelection = "Hour";
                // }
                   /* metricType = metricType.replaceFirst("XXX", timeStampSelection);
                 szQuery = "select ServiceName,ResourceID,Yield,PerformanceRatio,EnergyPerRatedPower,ResourceType,TimeStamps from "
                 + "(SELECT service as ServiceName, resourceid as ResourceID, array_to_string(array_agg(metricvalue order by timestamp1),',') as Yield"
                 + ", array_to_string(array_agg(metricvalue order by timestamp1),',') as PerformanceRatio, array_to_string(array_agg(metricvalue order by timestamp1),',') as EnergyPerRatedPower, resourcetype as ResourceType, "
                 + "array_to_string(array_agg(concat('\"',to_char(to_timestamp(timestamp1), 'yyyy/MM/dd HH24:mi:SS'),'\"') order by timestamp1),',') as TimeStamps "
                 + "from servicemetrics where resourcetype='" + service + "' and customerid=(select id from customerinfo where "
                 + "customername='" + cCustomer + "') and service='" + service + "' and metrictype='" + metricType + "' and timestamp1 between "
                 + sMilli + " and " + eMilli + " and resourceid='" + service + "' group by service,resourcetype,resourceid) w1";
                 headingNamesMetric1 = new String[]{"ServiceName", "ResourceType", "ResourceID", "TimeStamps", "PerformanceRatio", "EnergyPerRatedPower", "Yield"};
                 System.out.println("PerformanceRatioGeneratorDirectDB query==>>" + szQuery);
                 resultJson = generateDerivedJsonFromGivenQueryForService(szQuery, headingNamesMetric1, customer, cCustomer, resType2, configuredMetricType,
                 timeStampSelection, 3);
                 System.out.println("resultjson before modifyJson is==>>" + resultJson);
                 if (!(resultJson.equals("[]"))) {
                 resultJson = modifyJson(resultJson);
                 System.out.println("resultjson after modifyJson is==>>" + resultJson);
                 }
                 */
                //} else {
                System.out.println("resultJson at PRservice level==>>");
                resultJson = getJSONFromAggregatedMetrics(resType, cCustomer, service, resType2, metricType, configuredMetricType, sMilli, eMilli, timeStampSelection, customer);
                System.out.println("resultJson at service level==>>" + resultJson);

                //}

            } else if (resType2.equals(resType)) {//device level json
                //if (timeStampSelection.equalsIgnoreCase("Hour") || timeStampSelection.equalsIgnoreCase("Day")) {
                   /* if (timeStampSelection.equalsIgnoreCase("Day")) {
                 timeStampSelection = "Hour";
                 }
                 metricType = metricType.replaceFirst("XXX", timeStampSelection);
                 szQuery = "SELECT service as ServiceName, resourceid as ResourceID, array_to_string(array_agg(metricvalue  order by timestamp1),',') as PerformanceRatio, "
                 + "array_to_string(array_agg(metricvalue  order by timestamp1),',') as EnergyPerRatedPower, resourcetype as ResourceType, "
                 + "array_to_string(array_agg(concat('\"',to_char(to_timestamp(timestamp1), 'yyyy/MM/dd HH24:mi:SS'),'\"') order by timestamp1),',') as TimeStamps from "
                 + "servicemetrics "
                 + "where resourcetype='" + resType + "' and customerid=(select id from customerinfo where customername='"
                 + cCustomer + "') and service='" + service + "' and metrictype='" + metricType + "' and timestamp1 between "
                 + sMilli + " and " + eMilli + " group by service,resourcetype,resourceid";
                 headingNamesMetric1 = new String[]{"ServiceName", "ResourceType", "ResourceID", "TimeStamps", "PerformanceRatio", "EnergyPerRatedPower"};
                 System.out.println("PerformanceRatioGeneratorDirectDB query==>>" + szQuery);
                 resultJson = generateDerivedJsonFromGivenQueryForService(szQuery, headingNamesMetric1, customer, cCustomer, resType2, configuredMetricType,
                 timeStampSelection, 2);
                 System.out.println("resultjson before modifyJson is==>>" + resultJson);
                 if (!(resultJson.equals("[]"))) {
                 resultJson = modifyJson(resultJson);
                 System.out.println("resultjson after modifyJson is==>>" + resultJson);
                 }

                 // } else {
                 */ System.out.println("resultJson at PRdevice level==>>");
                resultJson = getJSONFromAggregatedMetrics(resType, cCustomer, service, resType2, metricType, configuredMetricType, sMilli, eMilli, timeStampSelection, customer);
                System.out.println("resultJson at device level==>>" + resultJson);

                // }
            }
            /*if (service != null) {//dont modify json for cCustomer level
             //System.out.println("resultjson before modifyJson is==>>"+resultJson);
             if (!(resultJson.equals("[]"))) {
             resultJson = modifyJson(resultJson);
             }
             }*/

            return resultJson;
        } catch (Exception e) {
            log.error("Error in generateJSON==>>" + e.toString());
            e.printStackTrace();
        } finally {
            szQuery = null;
            headingNamesMetric1 = null;

        }
        return null;
    }

    public static String generateDerivedJsonFromGivenQueryForService(String szQuery, String[] headingNames, String customer, String cCustomer,
            String resType, String configuredMetricType, String timeStampSelection, int noOfMetrics) {
        String perfJSON = "";
        ResultSet rs4 = null;
        String InstalledCapacity;
        String szConcatColumn;//
        String subServiceQuery = null;

        try {
            String szMetricTypeValueJson = "";
            perfJSON = "[";
            rs4 = ConnectionDAO.executerQuery(szQuery, customer);
            String szPmaxName;
            if (resType.equals("")) {
                szPmaxName = "InstalledCapacity";
            } else {
                szPmaxName = "Pmax";
            }
            System.out.println("generateDerivedJsonFromGivenQuery resType==>>" + resType);
            System.out.println("szPmaxName==>>" + szPmaxName);
            String szResID;//
            String service;//
            String derivedVal;//
            String[] szEnergyArray;//
            String[] szTimestmpArray;
            Double val;//
            double totalNA = 0;
            DecimalFormat df = new DecimalFormat("#.#");
            while (rs4.next()) {
                szConcatColumn = "";
                szMetricTypeValueJson = "";
                szResID = null;

                service = null;
                service = rs4.getString("ServiceName");
                if (resType.equals("")) {
                    System.out.println("in service");
                    /*TargetEnergyMinute = ResourceConfiguration.getConfValue(customer, cCustomer, service,
                     null, szTargetEnergyMetricName.replaceFirst("XXX", timeStampSelection));*/
                    InstalledCapacity = ResourceConfiguration.getConfValue(customer, cCustomer, service, "Default",
                            null, szPmaxName);
                    subServiceQuery = "select distinct service,subservice from hostinfo where customerid=(select id from customerinfo where customername='" + cCustomer + "') and service='" + rs4.getString("ServiceName") + "'";

                } else {
                    System.out.println("in device");
                    szResID = rs4.getString("ResourceID");
                    /*TargetEnergyMinute = ResourceConfiguration.getConfValue(customer, cCustomer, rs4.getString("ServiceName"),
                     szResID, szTargetEnergyMetricName.replaceFirst("XXX", timeStampSelection));*/
                    subServiceQuery = "select distinct service,subservice,resourceid from hostinfo where customerid=(select id from customerinfo where customername='" + cCustomer + "') and service='" + rs4.getString("ServiceName") + "' and resourceid='" + rs4.getString("ResourceID") + "'";
                    InstalledCapacity = ResourceConfiguration.getConfValue(customer, cCustomer, rs4.getString("ServiceName"), "Default",
                            szResID, szPmaxName);
                    if (szResID.equals(service)) {
                        continue;
                    }
                }


                System.out.println("szResID==>>" + szResID);
                derivedVal = "";
                double jsonIrradianValue = 0;
                totalNA = PerformanceRatioGenerator.totalAreaWithModuleEfficiency(resType, cCustomer, customer, service, service, subServiceQuery);
                System.out.println("totalNA in per" + totalNA);
                for (int i = 0; i < headingNames.length - 1; i++) {
                    if (rs4.getString(headingNames[i]) != null) {
                        derivedVal = "";
                        if (headingNames[i].equals("PerformanceRatio")) {
                            if (totalNA > 0) {
                                System.out.println("not nullddd dc");

                                szEnergyArray = rs4.getString(headingNames[i]).split(",");
                                System.out.println("No. of energy data points==>>" + szEnergyArray.length);
                                szTimestmpArray = rs4.getString("TimeStamps").split(",");
                                for (int count = 0; count < szEnergyArray.length; count++) {
                                    /* val = (Double.parseDouble(szEnergyArray[count])) / (Double.parseDouble(TargetEnergyMinute)
                                     * DateGenerator.getElapsedMinutes(timeStampSelection, szTimestmpArray[count].replaceAll("\"", ""),
                                     customer, cCustomer, service));*/

                                    jsonIrradianValue = DateGenerator.getIrradiationOfMinutes(timeStampSelection, szTimestmpArray[count].replaceAll("\"", ""), customer, cCustomer, rs4.getString("ServiceName"));
                                    if (jsonIrradianValue > 0) {
                                        val = (Double.parseDouble(szEnergyArray[count])) / (jsonIrradianValue * totalNA);
                                        System.out.println("totalNA in per" + totalNA);
                                        System.out.println("totalNA in per" + jsonIrradianValue);
                                        System.out.println("totalNA in per" + szEnergyArray[count]);
                                        System.out.println("totalNA in per" + val);
                                        derivedVal = derivedVal + df.format(val) + ",";
                                        System.out.println("val in per " + val);
                                    } else {
                                        derivedVal = derivedVal + df.format(0) + ",";
                                        System.out.println("derivedVal in per val=0 :" + derivedVal);
                                    }

                                }
                                if (derivedVal.length() > 1) {
                                    derivedVal = derivedVal.substring(0, derivedVal.length() - 1);
                                    System.out.println("val in per " + derivedVal);
                                }
                            } else {
                                derivedVal = "";
                            }
                            szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":\"" + derivedVal + "\"";
                        } else if (headingNames[i].equals("EnergyPerRatedPower")) {
                            if (InstalledCapacity != null) {
                                derivedVal = "";
                                System.out.println("not nullddd IC");
                                szEnergyArray = rs4.getString(headingNames[i]).split(",");
                                for (int count = 0; count < szEnergyArray.length; count++) {
                                    val = (Double.parseDouble(szEnergyArray[count])) / (Double.parseDouble(InstalledCapacity));
                                    derivedVal = derivedVal + df.format(val) + ",";
                                    System.out.println("val" + derivedVal);
                                }
                                if (derivedVal.length() > 1) {
                                    derivedVal = derivedVal.substring(0, derivedVal.length() - 1);
                                }
                            } else {
                                derivedVal = "";
                            }
                            szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":\"" + derivedVal + "\"";
                        } else if (headingNames[i].equals("TimeStamps")) {
                            /*szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":[[" + rs4.getString(headingNames[i]) + "],["
                             + rs4.getString(headingNames[i]) + "]]";*/
                            szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":[";
                            for (int p = 0; p < noOfMetrics; p++) {
                                szConcatColumn += "[" + rs4.getString(headingNames[i]) + "],";
                            }
                            szConcatColumn = szConcatColumn.substring(0, szConcatColumn.length() - 1);
                            szConcatColumn += "]";
                        } else {
                            szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":\"" + rs4.getString(headingNames[i]) + "\"";
                        }
                    }
                    szMetricTypeValueJson = szMetricTypeValueJson + "," + rs4.getString(headingNames[i]);
                }
                if (headingNames[headingNames.length - 1].equals("PerformanceRatio")) {
                    derivedVal = "";
                    if (totalNA > 0) {
                        System.out.println("not nullddd dcp");
                        szEnergyArray = rs4.getString(headingNames[headingNames.length - 1]).split(",");
                        szTimestmpArray = rs4.getString("TimeStamps").split(",");
                        for (int count = 0; count < szEnergyArray.length; count++) {
                            /* val = (Double.parseDouble(szEnergyArray[count])) / (Double.parseDouble(TargetEnergyMinute)
                             * DateGenerator.getElapsedMinutes(timeStampSelection, szTimestmpArray[count].replaceAll("\"", ""), customer,
                             cCustomer, service));*/
                            jsonIrradianValue = DateGenerator.getIrradiationOfMinutes(timeStampSelection, szTimestmpArray[count].replaceAll("\"", ""), customer, cCustomer, service);

                            val = (Double.parseDouble(szEnergyArray[count])) / (totalNA * jsonIrradianValue);

                            derivedVal = derivedVal + df.format(val) + ",";
                            System.out.println("val" + derivedVal);
                        }
                        if (derivedVal.length() > 1) {
                            derivedVal = derivedVal.substring(0, derivedVal.length() - 1);
                        }
                    } else {
                        derivedVal = "";
                    }
                } else if (headingNames[headingNames.length - 1].equals("EnergyPerRatedPower")) {
                    derivedVal = "";
                    System.out.println("not nullddd Ep");
                    if (InstalledCapacity != null) {
                        szEnergyArray = rs4.getString(headingNames[headingNames.length - 1]).split(",");
                        for (int count = 0; count < szEnergyArray.length; count++) {
                            val = (Double.parseDouble(szEnergyArray[count])) / (Double.parseDouble(InstalledCapacity));
                            derivedVal = derivedVal + df.format(val) + ",";
                            System.out.println("val" + derivedVal);
                        }
                        if (derivedVal.length() > 1) {
                            derivedVal = derivedVal.substring(0, derivedVal.length() - 1);
                        }
                    } else {
                        derivedVal = "";
                    }
                } else {
                    derivedVal = rs4.getString(headingNames[headingNames.length - 1]);
                }
                szConcatColumn = szConcatColumn + ",\"" + headingNames[headingNames.length - 1] + "\":\"" + derivedVal + "\"";
                perfJSON += "{" + szConcatColumn.substring(1) + "},";
                szConcatColumn = null;
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
            szConcatColumn = null;
            if (rs4 != null) {
                try {
                    rs4.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rs4 = null;
            } else if (subServiceQuery != null) {
                subServiceQuery = null;
            }
        }
        return null;
    }

    private static String modifyJson(String input) {
        String output = "";
        boolean altered = false;
        try {
            String[] arrayEles = input.split("},");
            System.out.println("arrayEles length==>>" + arrayEles.length);
            int startIndex;//
            int endIndex;//
            String perfRatioStr;//
            String energyPerRatedPowerStr;//
            String yieldStr;//
            String actualMTypes;//
            String resNames;//
            String yields;//
            String resValues;//
            String acVals;//
            String exVals;//
            for (String arrayE : arrayEles) {
                altered = true;
                startIndex = arrayE.indexOf("\"PerformanceRatio\":\"");
                endIndex = arrayE.indexOf("\"", startIndex + new String("\"PerformanceRatio\":\"").length());
                perfRatioStr = arrayE.substring(startIndex, endIndex);
                arrayE = arrayE.replace(perfRatioStr + "\",", "");
                startIndex = arrayE.indexOf("\"EnergyPerRatedPower\":\"");
                endIndex = arrayE.indexOf("\"", startIndex + new String("\"EnergyPerRatedPower\":\"").length());
                energyPerRatedPowerStr = arrayE.substring(startIndex, endIndex);
                startIndex = arrayE.indexOf("\"Yield\":\"");

                //arrayE += actualMTypes;

                acVals = perfRatioStr.replace("\"PerformanceRatio\":\"", "");
                exVals = energyPerRatedPowerStr.replace("\"EnergyPerRatedPower\":\"", "");
                if (startIndex != -1) {
                    endIndex = arrayE.indexOf("\"", startIndex + new String("\"Yield\":\"").length());
                    yieldStr = arrayE.substring(startIndex, endIndex);
                    actualMTypes = "\"ActualMetricTypes\":[\"PerformanceRatio\",\"EnergyPerRatedPower\",\"Yield\"]";
                    //arrayE += actualMTypes;
                    resNames = ",\"ResourceNames\":[\"PerformanceRatio\",\"EnergyPerRatedPower\",\"Yield\"]";
                    arrayE = arrayE.replace(energyPerRatedPowerStr + "\"", "");
                    yields = yieldStr.replace("\"Yield\":\"", "");
                    resValues = ",\"ResourceValues\":[[" + acVals + "],[" + exVals + "],[" + yields + "]]";
                    arrayE = arrayE.replace(yieldStr + "\"", actualMTypes + resNames + resValues);
                } else {
                    actualMTypes = "\"ActualMetricTypes\":[\"PerformanceRatio\",\"EnergyPerRatedPower\"]";
                    //arrayE += actualMTypes;
                    resNames = ",\"ResourceNames\":[\"PerformanceRatio\",\"EnergyPerRatedPower\"]";
                    resValues = ",\"ResourceValues\":[[" + acVals + "],[" + exVals + "]]";
                    arrayE = arrayE.replace(energyPerRatedPowerStr + "\"", actualMTypes + resNames + resValues);
                }
                //arrayE += resNames;
                //arrayE = arrayE.replace(energyPerRatedPowerStr + "\"", actualMTypes + resNames);
                output += arrayE + "},";
            }
            if (altered) {
                output = output.substring(0, output.length() - 2);
                //output += "]";
            }
            return output;
        } catch (Exception e) {
            log.error("Error in modifyJson" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static String getJSONFromAggregatedMetrics(String resType, String cCustomer, String service, String resType2, String metricType, String configuredMetricType,
            long sMilli, long eMilli, String timeStampSelection, String customer) {

        String resultJson = "[";
        String szQuery = null;
        //Calendar cal1 = Calendar.getInstance();
        Calendar cal1 = DateGenerator.getCurrentTime();
        DateFormat formate = new SimpleDateFormat("yyyy\\/MM\\/dd HH:mm:ss");
        DecimalFormat df = new DecimalFormat("#.#");
        String response = null;
        String[] splite = null;
        String sztimestamp = null;
        String[] dSplite = null;
        URL url = null;
        HttpURLConnection conn = null;
        ResultSet rs = null, rs5 = null;
        BufferedReader br = null;
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        metricType = "Energy";
        String[] spliteca = null;
        String[] spliteco = null;
        Date date = null;
        ArrayList szServiceList = null;
        InputStreamReader isReader = null;
        // String panelArea;
        // String mEfficiency;
        // String pvpa="PVPanelArea";
        // String meff="ModuleEfficiency";
        double totalNA = 0;
        String subServiceQuery = null;
        try {

            date = new Date(eMilli * 1000);
            cal1.setTime(date);
            System.out.println("Date>> " + date);
            System.out.println(cal1.getTime());
            int day = cal1.get(Calendar.DAY_OF_MONTH);
            int year = cal1.get(Calendar.YEAR);
            int month = cal1.get(Calendar.MONTH);
            int week = cal1.get(Calendar.WEEK_OF_MONTH);
            System.out.println("Date>> " + day + month + year + week);
            //configuredMetricType = configuredMetricType.replaceFirst("XXX", timeStampSelection);
            System.out.println("metricType==>>" + metricType);
            System.out.println("configuredMetricType==>>" + configuredMetricType);
            System.out.println("resType==>>" + resType);
            System.out.println("resType2==>>" + resType2 + service);
            System.out.println("In PerformanceRatioGenerator.generateJSON==>>smilli:" + sMilli + "::emilli:" + eMilli);
            double jsonIrradianValue = 0;
            String szEnergy = "";
            String timestamp = null;
            String prVal = "", eprpVal = "", teVal = "", aeVal = "";
            String InstalledCapacity;
            String slot = null;
            if (service == null) {
                if (timeStampSelection.equalsIgnoreCase("year")) {
                    slot = "Year";
                } else if (timeStampSelection.equalsIgnoreCase("month")) {
                    slot = "Month";
                } else if (timeStampSelection.equalsIgnoreCase("Week")) {
                    slot = "Week";
                } else if (timeStampSelection.equalsIgnoreCase("Day")) {
                    slot = "Day";
                } else if (timeStampSelection.equalsIgnoreCase("Hour")) {
                    slot = "Hour";
                }

            } else {
                if (timeStampSelection.equalsIgnoreCase("year")) {
                    slot = "Month";
                } else if (timeStampSelection.equalsIgnoreCase("month")) {
                    slot = "Day";
                } else if (timeStampSelection.equalsIgnoreCase("Week")) {
                    slot = "Day";
                } else if (timeStampSelection.equalsIgnoreCase("Day")) {
                    slot = "Hour";
                } else if (timeStampSelection.equalsIgnoreCase("Hour")) {
                    slot = "Hour";
                }
            }
            String rsValuesPR = null, rsValuesEPRP = null, rsValuesTE = null, rsValuesAE = null;
            String ts = null;
            String szPmaxName;
            if (resType.equals("")) {
                szPmaxName = "InstalledCapacity";
            } else {
                szPmaxName = "Pmax";
            }
            szServiceList = new ArrayList();
            String szService;
            if (service == null) {
                szQuery = "select distinct service from hostinfo where customerid=(select id from customerinfo where customername='" + cCustomer + "')";
                rs = ConnectionDAO.executerQuery(szQuery, customer);
                System.out.println("query==> " + szQuery);
                System.out.println("cCustomer==> " + cCustomer);

                while (rs.next()) {
                    szServiceList.add(rs.getString(1));
                }
                System.out.println("customermap==> " + szServiceList.size());
                System.out.println("metricType==>>" + metricType);
                System.out.println("configuredMetricType==>>" + configuredMetricType);
                System.out.println("resType==>>" + resType);
                System.out.println("resType2==>>" + resType2 + service);
                System.out.println("resType2==>>" + timeStampSelection);
            } else if (resType2.equals("")) {
                szServiceList.add(service);
                System.out.println("servicemap==> " + szServiceList);
                System.out.println("metricType==>>" + metricType);
                System.out.println("configuredMetricType==>>" + configuredMetricType);
                System.out.println("resType==>>" + resType);
                System.out.println("resType2==>>" + resType2);
                System.out.println("resType2==>>" + timeStampSelection);
            } else {
                szQuery = "select distinct resourceid from hostinfo where customerid=(select id from customerinfo where customername='" + cCustomer + "') and "
                        + "service='" + service + "' and resourceid not like 'FroniusAdapter%'and resourceid !='" + service + "'";
                rs = ConnectionDAO.executerQuery(szQuery, customer);
                while (rs.next()) {
                    szServiceList.add(rs.getString(1));
                }
                rs = null;
                System.out.println("devicemap==> " + szServiceList);
                System.out.println("metricType==>>" + metricType);
                System.out.println("configuredMetricType==>>" + configuredMetricType);
                System.out.println("resType==>>" + resType);
                System.out.println("resType2==>>" + resType2);
                System.out.println("resType2==>>" + timeStampSelection);
            }

            for (int k = 0; k < szServiceList.size(); k++) {
                szService = (String) szServiceList.get(k);
                System.out.println("for loop==>  " + szService);
                System.out.println("for length==>  " + szServiceList.size() + " " + k);
                System.out.println(szServiceList);

                if (resType2.equals(resType)) {

                    url = new URL(DBUtilHelper.MetricValueURL + "?customer=" + cCustomer
                            + "&metrictype=" + metricType + "&service=" + service + "&timeperiod=" + timeStampSelection
                            + "&resourcetype=" + resType + "&slot=" + slot + "&resourceid=" + szService + "&date=" + day + "&week=" + week + "&month=" + months[month]
                            + "&year=" + year + "&hour=23");

                } else {
                    //customer level 
                    url = new URL(DBUtilHelper.MetricValueURL + "?customer=" + cCustomer
                            + "&metrictype=" + metricType + "&service=" + szService + "&timeperiod=" + timeStampSelection
                            + "&resourcetype=" + szService + "&slot=" + slot + "&date=" + day + "&week=" + week + "&month=" + months[month]
                            + "&year=" + year + "&hour=23");

                }
                System.out.println("url>>> " + url);

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                isReader = new InputStreamReader(conn.getInputStream());
                br = new BufferedReader(isReader);
                // resultJson = "[]";

                if (service == null || resType2.equals("")) {

                    //customer and service level
                    /*TargetEnergyMinute = ResourceConfiguration.getConfValue(customer, cCustomer, szService,
                     null, szTagretEnergyMetricName.replaceFirst("XXX", timeStampSelection));*/
                    //      TargetEnergyMinute = ResourceConfiguration.getConfValue(customer, cCustomer, szService, "Default",
                    //            null, szTagretEnergyMetricName);
                    InstalledCapacity = ResourceConfiguration.getConfValue(customer, cCustomer, szService, "Default",
                            null, "InstalledCapacity");
                    subServiceQuery = "select distinct service,subservice from hostinfo where customerid=(select id from customerinfo where customername='" + cCustomer + "') and service='" + szService + "'";

                } else {
                    //device level 
                    /*TargetEnergyMinute = ResourceConfiguration.getConfValue(customer, cCustomer, service,
                     szService, szTagretEnergyMetricName.replaceFirst("XXX", timeStampSelection));*/
                    //  TargetEnergyMinute = ResourceConfiguration.getConfValue(customer, cCustomer, service, "Default",
                    //        szService, szTagretEnergyMetricName);
                    InstalledCapacity = ResourceConfiguration.getConfValue(customer, cCustomer, service, "Default",
                            szService, szPmaxName);
                    subServiceQuery = "select distinct service,subservice,resourceid from hostinfo where customerid=(select id from customerinfo where customername='" + cCustomer + "') and service='" + service + "' and resourceid='" + szService + "'";

                }



                System.out.println("InstalledCapacity " + InstalledCapacity);
                while ((response = br.readLine()) != null) {
                    System.out.println("Response from getMetricValue API::" + response);

                    /*if (response.length() > 35) {

                     splite = response.split(",");

                     } else {
                     response.concat(",)");
                     splite = response.split(",");
                     System.out.println(splite[0]);
                     System.out.println(splite.length);

                     }*/
                    if (response.contains(":")) {
                        splite = response.split("}");

                        rsValuesPR = "[]";
                        rsValuesEPRP = "[]";
                        rsValuesTE = "[]";
                        rsValuesAE = "[]";
                        ts = "[]";
                        for (int i = 0; i < splite.length; i++) {

                            System.out.println("sp[i]==>" + i + splite[i]);
                            if (slot.equalsIgnoreCase("Hour")) {

                                if (splite[i].contains(",") && splite[i].contains(":")) {
                                    spliteca = splite[i].split(",");
                                    if (spliteca[2].contains(":")) {
                                        spliteco = spliteca[2].split(":");
                                    } else if (spliteca[3].contains(":")) {
                                        spliteco = spliteca[3].split(":");
                                    }
                                    System.out.println("spca0==>" + spliteca[0]);
                                    System.out.println("spca2==>" + spliteca[2]);
                                    System.out.println("spco==>" + spliteco[0]);
                                    System.out.println("spco==>" + spliteco[1]);
                                    System.out.println("ts " + spliteco[0].substring(0, spliteco[0].length() - 1));
                                    sztimestamp = spliteco[0].substring(0, spliteco[0].length() - 1);
                                    System.out.println("eval " + spliteco[1].substring(1, spliteco[1].length() - 1));
                                    szEnergy = spliteco[1].substring(1, spliteco[1].length() - 1);
                                } else if (splite[i].contains(":")) {
                                    System.out.println("else bb");
                                    spliteco = splite[i].split(":");
                                    if (spliteco[0].contains("'")) {
                                        String[] splitess = spliteco[0].split("'");
                                        System.out.println("spss==>" + splitess[0]);
                                        System.out.println("spss==>" + splitess[1]);

                                        sztimestamp = splitess[1].substring(3, splitess[1].length() - 1);
                                        System.out.println("final==>" + sztimestamp);

                                        /* if (timeStampSelection.equalsIgnoreCase("month") && slot.equalsIgnoreCase("Day")) {
                                         sztimestamp = splitess[1].substring(3, splitess[1].length() - 1);
                                         } else if (timeStampSelection.equalsIgnoreCase("Year") && slot.equalsIgnoreCase("Month")) {
                                         sztimestamp = splitess[1].substring(3, splitess[1].length() - 1);
                                         } else if (timeStampSelection.equalsIgnoreCase("Month") && slot.equalsIgnoreCase("Month")) {
                                         sztimestamp = splitess[1].substring(3, splitess[1].length() - 1);
                                         System.out.println("final==>" + sztimestamp);
                                         }*/
                                    } else {
                                        sztimestamp = spliteco[0].substring(0, spliteco[0].length() - 1);
                                        if (timeStampSelection.equalsIgnoreCase("month") && slot.equalsIgnoreCase("Week")) {
                                            sztimestamp = spliteco[0].substring(2, spliteco[0].length() - 1);
                                        } else if (timeStampSelection.equalsIgnoreCase("Year") && slot.equalsIgnoreCase("Month")) {
                                            sztimestamp = spliteco[0].substring(2, spliteco[0].length() - 1);
                                        }
                                    }
                                    szEnergy = spliteco[1].substring(1, spliteco[1].length() - 1);
                                }
                            } else {
                                if (splite[i].contains(",") && splite[i].contains(":")) {
                                    spliteca = splite[i].split(",");
                                    if (spliteca[1].contains(":")) {
                                        spliteco = spliteca[1].split(":");
                                    } else {
                                        spliteco = spliteca[2].split(":");
                                    }
                                    System.out.println("spca==>" + spliteca[1]);
                                    System.out.println("spco==>" + spliteco[0]);
                                    System.out.println("spco==>" + spliteco[1]);
                                    System.out.println("ts " + spliteco[0].substring(0, spliteco[0].length() - 1));
                                    sztimestamp = spliteco[0].substring(0, spliteco[0].length() - 1);
                                    if (timeStampSelection.equalsIgnoreCase("month") && slot.equalsIgnoreCase("Week")) {
                                        sztimestamp = spliteco[0].substring(2, spliteco[0].length() - 1);
                                    } else if (timeStampSelection.equalsIgnoreCase("Year") && slot.equalsIgnoreCase("Month")) {
                                        sztimestamp = spliteco[0].substring(2, spliteco[0].length() - 1);
                                    }
                                    System.out.println("eval " + spliteco[1].substring(1, spliteco[1].length() - 1));
                                    szEnergy = spliteco[1].substring(1, spliteco[1].length() - 1);
                                } else if (splite[i].contains(":")) {
                                    System.out.println("else bb");
                                    spliteco = splite[i].split(":");
                                    if (spliteco[0].contains("'")) {
                                        String[] splitess = spliteco[0].split("'");
                                        System.out.println("spss==>" + splitess[0]);
                                        System.out.println("spss==>" + splitess[1]);

                                        sztimestamp = splitess[1].substring(3, splitess[1].length() - 1);
                                        System.out.println("final==>" + sztimestamp);

                                        /* if (timeStampSelection.equalsIgnoreCase("month") && slot.equalsIgnoreCase("Day")) {
                                         sztimestamp = splitess[1].substring(2, splitess[1].length() - 1);
                                         } else if (timeStampSelection.equalsIgnoreCase("Year") && slot.equalsIgnoreCase("Month")) {
                                         sztimestamp = splitess[1].substring(2, splitess[1].length() - 1);
                                         } else if (timeStampSelection.equalsIgnoreCase("Month") && slot.equalsIgnoreCase("Month")) {
                                         sztimestamp = splitess[1].substring(2, splitess[1].length() - 1);
                                         System.out.println("final==>" + sztimestamp);
                                         }*/
                                    } else {
                                        sztimestamp = spliteco[0].substring(0, spliteco[0].length() - 1);
                                        if (timeStampSelection.equalsIgnoreCase("month") && slot.equalsIgnoreCase("Week")) {
                                            sztimestamp = spliteco[0].substring(2, spliteco[0].length() - 1);
                                        } else if (timeStampSelection.equalsIgnoreCase("Year") && slot.equalsIgnoreCase("Month")) {
                                            sztimestamp = spliteco[0].substring(2, spliteco[0].length() - 1);
                                        }
                                    }
                                    szEnergy = spliteco[1].substring(1, spliteco[1].length() - 1);
                                }
                            }
                            /*if (slot.equalsIgnoreCase("Day")) {
                             sztimestamp = splite[i].substring(4, splite[i].indexOf(":") - 1);
                             System.out.println("sztimestamp "+sztimestamp);

                             } else {
                             sztimestamp = splite[i].substring(splite[i].indexOf("{") + 2, splite[i].indexOf(":") - 1);
                             System.out.println("sztimestamp "+sztimestamp);
                             }

                             szEnergy = splite[i].substring(splite[i].indexOf(":") + 2, splite[i].indexOf("}") - 1);
                             */
                            if (splite[i].contains(":")) {
                                System.out.println("length " + splite.length);
                                System.out.println("timestampval " + sztimestamp);
                                System.out.println("timestampval " + sztimestamp.length());

                                System.out.println("energyval " + szEnergy);

                                dSplite = sztimestamp.split("/");
                                System.out.println("timestampval " + dSplite.length);

                                if ((slot.equalsIgnoreCase("Month") || slot.equalsIgnoreCase("Week")) || slot.equalsIgnoreCase("Day")) {
                                    if (dSplite.length > 2) {
                                        if (slot.equalsIgnoreCase("Week")) {
                                            timestamp = dSplite[2] + "/" + dSplite[1] + "/" + day;
                                        } else {
                                            timestamp = dSplite[2] + "/" + dSplite[1] + "/" + dSplite[0];
                                        }
                                        System.out.println("Full date=  " + dSplite[2] + "/" + dSplite[1] + "/" + dSplite[0]);
                                    } else if (dSplite.length > 1) {
                                        timestamp = dSplite[1] + "/" + dSplite[0];
                                        System.out.println("only month=  " + dSplite[1] + "/" + dSplite[0]);

                                    } else {
                                        timestamp = "";
                                        System.out.println("only Date=  " + dSplite[0]);
                                    }

                                } else {
                                    if (slot.equalsIgnoreCase("Hour")) {
                                        System.out.println("spliteca[0] " + spliteca[0]);
                                        System.out.println("spliteca[1] " + spliteca[1]);
                                        if (spliteca[0].length() > 9) {
                                            timestamp = sztimestamp + " " + spliteca[0].substring(9) + ":00:00";
                                        } else {

                                            timestamp = sztimestamp + " " + spliteca[1].substring(2) + ":00:00";
                                        }
                                        System.out.println("only hours=  " + timestamp);
                                    } else {
                                        timestamp = sztimestamp;
                                    }
                                }
                                prVal = "";
                                eprpVal = "";
                                teVal = "";
                                aeVal = "";


                                /*   System.out.println("subquery "+subServiceQuery);
                                 rs5 = ConnectionDAO.executerQuery(subServiceQuery, customer);
                                 totalNA=0;
                                 while(rs5.next()){
                                 System.out.println("subservice "+rs5.getString("subservice"));
                                 if (service == null || resType2.equals("")) {
                                 panelArea = ResourceConfiguration.getConfValue(customer, cCustomer, rs5.getString("service"), rs5.getString("subservice"),
                                 null, pvpa);
                                 System.out.println("panel area "+panelArea);
                                 mEfficiency=ResourceConfiguration.getConfValue(customer, cCustomer, rs5.getString("service"),rs5.getString("subservice"),
                                 null, meff);
                                 System.out.println("mEfficiency "+mEfficiency);
                                 totalNA=totalNA+(Double.parseDouble(panelArea)*Double.parseDouble(mEfficiency));

                                 System.out.println("TotalNA "+totalNA);
                               
                                 }else{
                               
                               
                                 panelArea = ResourceConfiguration.getConfValue(customer, cCustomer, rs5.getString("service"), rs5.getString("subservice"),
                                 rs5.getString("resourceid"), pvpa);
                                 System.out.println("panel area "+panelArea);
                                 mEfficiency=ResourceConfiguration.getConfValue(customer, cCustomer, rs5.getString("service"),rs5.getString("subservice"),
                                 rs5.getString("resourceid"), meff);
                                 System.out.println("mEfficiency "+mEfficiency);
                                 totalNA=totalNA+(Double.parseDouble(panelArea)*Double.parseDouble(mEfficiency));

                                 System.out.println("TotalNA "+totalNA);
                                 }
                                 }
                                 */
                                totalNA = PerformanceRatioGenerator.totalAreaWithModuleEfficiency(resType2, cCustomer, customer, service, szService, subServiceQuery);
                                System.out.println("slot " + slot);
                                System.out.println("timestamp " + timestamp);
                                System.out.println("timeStampSelection " + timeStampSelection);

                                if (service == null || resType2.equals("")) {
                                    jsonIrradianValue = DateGenerator.getIrradiationOfMinutes(slot, timestamp, customer, cCustomer, szService);
                                } else {
                                    jsonIrradianValue = DateGenerator.getIrradiationOfMinutes(slot, timestamp, customer, cCustomer, service);

                                }
                                if (jsonIrradianValue > 0) {
                                    System.out.println("cust level::szEnergy to get performanceratio==>>" + szEnergy);
                                    if (service == null || resType2.equals("")) {
                                        /*     prVal = df.format((Double.parseDouble((String) szEnergy)) / (Double.parseDouble(TargetEnergyMinute)
                                         * DateGenerator.getElapsedMinutes(slot, timestamp, customer, cCustomer, szService)));
                                         */
                                        prVal = df.format((Double.parseDouble((String) szEnergy)) / (jsonIrradianValue * totalNA));

                                    } else {
                                        /*      prVal = df.format((Double.parseDouble((String) szEnergy)) / (Double.parseDouble(TargetEnergyMinute)
                                         * DateGenerator.getElapsedMinutes(slot, timestamp, customer, cCustomer, service)));
                                         */
                                        prVal = df.format((Double.parseDouble((String) szEnergy)) / (jsonIrradianValue * totalNA));

                                    }
                                    System.out.println("cust level::performanceratio val==>>" + prVal);

                                } else {
                                    prVal = "";
                                }
                                if (InstalledCapacity != null) {
                                    System.out.println("cust level::szEnergy to get EnergyPerRatedPower==>>" + szEnergy);
                                    eprpVal = df.format((Double.parseDouble((String) szEnergy)) / (Double.parseDouble(InstalledCapacity)));
                                    System.out.println("cust level:: EnergyPerRatedPower val==>>" + eprpVal);

                                } else {
                                    eprpVal = "";
                                }


                                if (jsonIrradianValue > 0) {
                                    System.out.println("Irval " + jsonIrradianValue);
                                    System.out.println("na" + totalNA);
                                    teVal = df.format((jsonIrradianValue * totalNA));
                                } else {
                                    teVal = "";
                                }
                                /*  
                                 if (TargetEnergyMinute != null) {
                                 System.out.println("cust level::szEnergy to get target energy ==>>" + szEnergy);
                                 if (service == null || resType2.equals("")) {
                                 teVal = df.format((Double.parseDouble(TargetEnergyMinute))
                                 * DateGenerator.getElapsedMinutes(slot, timestamp, customer, cCustomer, szService));
                                        
                                 } else {
                                 teVal = df.format((Double.parseDouble(TargetEnergyMinute))
                                 * DateGenerator.getElapsedMinutes(slot, timestamp, customer, cCustomer, service));
                                    
                                    
                                 }
                                 System.out.println("cust level::target energy val==>>" + teVal);

                                 } else {
                                 teVal = "";
                                 }
                                 */



                                if (szEnergy != null) {
                                    System.out.println("cust level::szEnergy to get actual energy ==>>" + szEnergy);
                                    aeVal = df.format((Double.parseDouble((String) szEnergy)));
                                    System.out.println("cust level::actual energy val==>>" + aeVal);

                                } else {
                                    aeVal = "";
                                }



                                System.out.println("ts :" + timestamp);
                                if (slot.equalsIgnoreCase("hour") && service != null) {
                                    String[] hrsplite = timestamp.split(" ");
                                    String[] secsplite = hrsplite[1].split(":");
                                    timestamp = secsplite[0] + ":" + secsplite[1];
                                    System.out.println(timestamp + " tselse " + timestamp.length());
                                }
                                System.out.println(timestamp);
                                System.out.println("rsValuesPR " + rsValuesPR.length());

                                if (rsValuesPR.length() == 2) {
                                    if (service == null) {
                                        rsValuesPR = prVal;
                                        rsValuesEPRP = eprpVal;
                                        rsValuesTE = teVal;
                                        rsValuesAE = aeVal;

                                    } else {
                                        rsValuesPR = "[\"" + prVal + "\"]";
                                        rsValuesEPRP = "[\"" + eprpVal + "\"]";
                                        rsValuesTE = "[\"" + teVal + "\"]";
                                        rsValuesAE = "[\"" + aeVal + "\"]";
                                        ts = "[\"" + timestamp + "\"]";

                                        System.out.println(rsValuesPR + rsValuesEPRP + rsValuesTE + rsValuesAE);
                                        System.out.println(ts);
                                    }
                                } else {

                                    System.out.println(rsValuesPR);
                                    if (rsValuesPR.length() > 0) {
                                        rsValuesPR = rsValuesPR.substring(0, rsValuesPR.length() - 1) + ",\"" + prVal + "\"]";
                                        System.out.println("rsValuesPR " + rsValuesPR);
                                    } else {
                                        rsValuesPR = "";
                                    }
                                    rsValuesEPRP = rsValuesEPRP.substring(0, rsValuesEPRP.length() - 1) + ",\"" + eprpVal + "\"]";
                                    System.out.println("rsValuesEPRP " + rsValuesEPRP);
                                    if (rsValuesTE.length() > 0) {
                                        rsValuesTE = rsValuesTE.substring(0, rsValuesTE.length() - 1) + ",\"" + teVal + "\"]";
                                        System.out.println("rsValuesTE " + rsValuesTE);
                                    } else {
                                        rsValuesTE = "";
                                    }
                                    rsValuesAE = rsValuesAE.substring(0, rsValuesAE.length() - 1) + ",\"" + aeVal + "\"]";
                                    System.out.println("rsValuesPR " + rsValuesAE);

                                    ts = ts.substring(0, ts.length() - 1) + ",\"" + timestamp + "\"]";
                                    System.out.println(ts);

                                }
                            }
                        }
                    } else {
                        if (service == null) {
                            rsValuesPR = "";
                            rsValuesEPRP = "";
                            rsValuesTE = "";
                            rsValuesAE = "";
                            ts = "";
                        } else {
                            rsValuesPR = "[]";
                            rsValuesEPRP = "[]";
                            rsValuesTE = "[]";
                            rsValuesAE = "[]";
                            ts = "[]";
                        }


                    }
                    System.out.println("[" + rsValuesPR + "," + rsValuesEPRP + "," + rsValuesAE + "," + rsValuesTE + "]");
                    System.out.println(resultJson.length());
                    if (resultJson.length() == 1) {
                        if (service == null) {

                            resultJson = "[{\"ResourceType \":\"" + szService + "\",\"ServiceName\":\"" + szService + "\",\"EnergyPerRatedPower\":\"" + rsValuesEPRP + "\",\"ActualEnergy\":\"" + rsValuesAE + "\",\"PerformanceRatio\":\"" + rsValuesPR + "\",\"TargetEnergy\":\"" + rsValuesTE + "\"}";
                        } else {
                            resultJson = "[{\"ActualMetricTypes\":[\"PerformanceRatio\",\"EnergyPerRatedPower\",\"Yield\",\"TargetEnergy\"],\"ResourceType \":\""
                                    + resType + "\",\"ServiceName\":\"" + service + "\","
                                    + "\"ResourceID\":\"" + szService + "\",\"ResourceValues\":[" + rsValuesPR + "," + rsValuesEPRP + "," + rsValuesAE
                                    + "," + rsValuesTE + "]"
                                    + ",\"ResourceNames\":[\"PerformanceRatio\",\"EnergyPerRatedPower\",\"Yield\",\"TargetEnergy\"],\"TimeStamps\":[" + ts + "," + ts + "," + ts + "," + ts + "]}";
                        }
                        System.out.println(resultJson.length());
                        System.out.println(resultJson);
                        System.out.println(resultJson.length());
                    } else {
                        if (service == null) {

                            resultJson += ",{\"ResourceType \":\"" + szService + "\",\"ServiceName\":\"" + szService + "\",\"EnergyPerRatedPower\":\"" + rsValuesEPRP + "\",\"ActualEnergy\":\"" + rsValuesAE + "\",\"PerformanceRatio\":\"" + rsValuesPR + "\",\"TargetEnergy\":\"" + rsValuesTE + "\"}";
                        } else {
                            resultJson += ",{\"ActualMetricTypes\":[\"PerformanceRatio\","
                                    + "\"EnergyPerRatedPower\",\"Yield\",\"TargetEnergy\"],\"ResourceType \":\"" + resType + "\",\"ServiceName\":\""
                                    + service + "\","
                                    + "\"ResourceID\":\"" + szService + "\",\"ResourceValues\":[" + rsValuesPR + "," + rsValuesEPRP + ","
                                    + rsValuesAE + "," + rsValuesTE + "]"
                                    + ",\"ResourceNames\":[\"PerformanceRatio\",\"EnergyPerRatedPower\",\"Yield\",\"TargetEnergy\"],"
                                    + "\"TimeStamps\":[" + ts + "," + ts + "," + ts + "," + ts + "]}";


                        }
                        System.out.println("else  " + resultJson);

                    }
                }
                br.close();
                System.out.println("final json " + resultJson + "]");
            }

        } catch (Exception e) {
            log.error("Error in generateJSON==>>" + e.toString());
            e.printStackTrace();
        } finally {
            try {
                if (szQuery != null) {
                    szQuery = null;
                } else if (cal1 != null) {
                    cal1.clear();
                    cal1 = null;
                } else if (formate != null) {
                    formate = null;
                } else if (response != null) {
                    response = null;
                } else if (splite != null) {
                    splite = null;
                } else if (sztimestamp != null) {
                    sztimestamp = null;
                } else if (dSplite != null) {
                    dSplite = null;
                } else if (url != null) {
                    url = null;
                } else if (conn != null) {
                    conn.disconnect();
                    conn = null;
                } else if (rs != null) {
                    rs.close();
                    rs = null;
                } else if (br != null) {
                    br.close();
                    br = null;
                } else if (date != null) {
                    date = null;
                } else if (szServiceList != null) {
                    szServiceList.clear();
                    szServiceList = null;
                } else if (isReader != null) {
                    isReader.close();
                    isReader = null;
                } else if (df != null) {
                    df = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultJson + "]";
    }

    public static double totalAreaWithModuleEfficiency(String resType2, String cCustomer, String customer, String service, String szService, String subServiceQuery) throws SQLException {
        String panelArea;
        ResultSet rs = null;
        String mEfficiency;
        String pvpa = "PVPanelArea";
        String meff = "ModuleEfficiency";
        double totalNA = 0;
        System.out.println("subquery " + subServiceQuery);
        rs = ConnectionDAO.executerQuery(subServiceQuery, customer);
        totalNA = 0;
        try {
            while (rs.next()) {
                System.out.println("subservice " + rs.getString("subservice"));
                if (service == null || resType2.equals("")) {
                    panelArea = ResourceConfiguration.getConfValue(customer, cCustomer, rs.getString("service"), rs.getString("subservice"),
                            null, pvpa);
                    System.out.println("panel area " + panelArea);
                    mEfficiency = ResourceConfiguration.getConfValue(customer, cCustomer, rs.getString("service"), rs.getString("subservice"),
                            null, meff);
                    System.out.println("mEfficiency " + mEfficiency);
                    totalNA = totalNA + (Double.parseDouble(panelArea) * Double.parseDouble(mEfficiency));

                    System.out.println("TotalNA " + totalNA);

                } else {


                    panelArea = ResourceConfiguration.getConfValue(customer, cCustomer, rs.getString("service"), rs.getString("subservice"),
                            rs.getString("resourceid"), pvpa);
                    System.out.println("panel area " + panelArea);
                    mEfficiency = ResourceConfiguration.getConfValue(customer, cCustomer, rs.getString("service"), rs.getString("subservice"),
                            rs.getString("resourceid"), meff);
                    System.out.println("mEfficiency " + mEfficiency);
                    totalNA = totalNA + (Double.parseDouble(panelArea) * Double.parseDouble(mEfficiency));

                    System.out.println("TotalNA " + totalNA);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            ConnectionDAO.closeStatement();
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rs = null;
            } else if (subServiceQuery != null) {
                subServiceQuery = null;
            }
        }
        return totalNA;
    }

    public static void main(String args[]) throws ParseException {
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
