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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author rekhas
 */
public class PerformanceRatioComparisonGenerator {

    static Logger log = Logger.getLogger(PerformanceRatioComparisonGenerator.class);

    public static String generateJSON(String resType, String cCustomer, String service, String resType2, String metricType, String configuredMetricType,
            long sMilli, long eMilli, String timeStampSelection, String customer) {
        String resultJson = "[]";
        try {
            metricType = metricType.replaceFirst("XXX", timeStampSelection);
            //configuredMetricType = configuredMetricType.replaceFirst("XXX", timeStampSelection);
            System.out.println("metricType==>>" + metricType);
            System.out.println("configuredMetricType==>>" + configuredMetricType);
            System.out.println("resType==>>" + resType);
            System.out.println("resType2==>>" + resType2);
            System.out.println("In PerformanceRatioComparisonGenerator.generateJSON==>>smilli:" + sMilli + "::emilli:" + eMilli);
            String[] headingNamesMetric1 = null;
            String szQuery;
            if (service == null) {//ccustomer level json
                resultJson = "[]";
            } else if (resType2.equals("")) {//service level json

                /*szQuery = "select w1.energy as Yield, w1.energy as PerformanceRatio,w1.energy as EnergyPerRatedPower, w1.ServiceName as ServiceName,"
                 + "w1.resourcetype as ResourceType, w1.resourceid as ResourceID from (select t.resourceid, t.service as ServiceName, t.resourcetype as ResourceType,t.metricvalue as energy,"
                 + " t.metrictype from(select customerid,service,resourcetype,resourceid,metrictype, max(timestamp1) as MaxTime from servicemetrics t1 "
                 + "where t1.resourcetype='" + resType + "' and t1.metrictype = '" + metricType + "' and t1.customerid=(select id from customerinfo "
                 + "where customername='" + cCustomer + "') and t1.service = '" + service + "' and t1.timestamp1 between " + sMilli + " and " + eMilli
                 + " group by t1.customerid,t1.service,t1.metrictype,t1.resourcetype,t1.resourceid) r inner join servicemetrics t on t.service=r.service and "
                 + "t.resourcetype=r.resourcetype and t.timestamp1 = r.MaxTime and t.metrictype=r.metrictype and t.customerid=r.customerid and t.resourceid=r.resourceid "
                 + "group by t.customerid,t.service,t.resourcetype,t.metrictype,t.metricvalue,t.resourceid) w1";
                 headingNamesMetric1 = new String[]{"ServiceName", "ResourceType", "ResourceID", "Yield", "PerformanceRatio", "EnergyPerRatedPower"};
                 System.out.println("PerformanceRatioGenerator query==>>" + szQuery);*/

                // resultJson = generateDerivedJsonFromGivenQueryForCustomer(szQuery, headingNamesMetric1, customer, cCustomer, resType2, configuredMetricType,
                //      timeStampSelection);
               // if (timeStampSelection.equalsIgnoreCase("Hour")) {
               //     timeStampSelection = "Day";
               // }

                resultJson = getJSONFromAggregatedMetrics(resType, cCustomer, service, resType2, metricType, configuredMetricType, sMilli, eMilli, timeStampSelection, customer);
                System.out.println("resultJson at service level==>>" + resultJson);
            } else if (resType2.equals(resType)) {//device level json
                resultJson = "[]";
            }
            if (service != null) {//dont modify json for cCustomer level
                //System.out.println("resultjson before modifyJson is==>>"+resultJson);
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
            String szTargetEnergyMetricName;//
            String TargetEnergyMintue;//
            String InstalledCapacity;//
            String szEnergy;//
            Double val;//
            DecimalFormat df;//
            while (rs4.next()) {
                szConcatColumn = "";
                szMetricTypeValueJson = "";
                szResID = null;

                service = null;
                System.out.println("generateDerivedJsonFromGivenQuery resType==>>" + resType);
                service = rs4.getString("ServiceName");
                szResID = rs4.getString("ResourceID");
                if (resType.equals("")) {
                } else {
                    if (szResID.equals(service)) {
                        continue;
                    }
                }
                System.out.println("szResID==>>" + szResID);
                derivedVal = null;
                szTargetEnergyMetricName = "TargetEnergyMinute";
                TargetEnergyMintue = ResourceConfiguration.getConfValue(customer, cCustomer, service, "Default",
                        szResID, szTargetEnergyMetricName);
                InstalledCapacity = ResourceConfiguration.getConfValue(customer, cCustomer, service, "Default",
                        szResID, "Pmax");
                System.out.println("Pmax for " + customer + ":" + cCustomer + ":" + service + ":" + szResID + " is " + InstalledCapacity);

                for (int i = 0; i < headingNames.length - 1; i++) {
                    if (rs4.getString(headingNames[i]) != null) {
                        if (headingNames[i].equals("PerformanceRatio")) {
                            if (TargetEnergyMintue != null) {
                                szEnergy = rs4.getString(headingNames[i]);
                                System.out.println("cust level::szEnergy==>>" + szEnergy);
                               /* val = (Double.parseDouble(szEnergy)) / (Double.parseDouble(TargetEnergyMintue)
                                        * DateGenerator.getElapsedMinutes(timeStampSelection, null, customer, cCustomer, service));
                                */
                                 val = (Double.parseDouble(szEnergy)) / (Double.parseDouble(TargetEnergyMintue)
                                        * DateGenerator.getIrradiationOfMinutes(timeStampSelection, null,customer,cCustomer,service));
                               
                                System.out.println("cust level::val==>>" + val);
                                df = new DecimalFormat("#.#");
                                derivedVal = df.format(val);
                            } else {
                                derivedVal = "";
                            }
                            szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":\"" + derivedVal + "\"";
                        } else if (headingNames[i].equals("EnergyPerRatedPower")) {
                            if (InstalledCapacity != null) {
                                szEnergy = rs4.getString(headingNames[i]);
                                val = (Double.parseDouble(szEnergy)) / (Double.parseDouble(InstalledCapacity));
                                df = new DecimalFormat("#.#");
                                derivedVal = df.format(val);
                            } else {
                                derivedVal = "";
                            }
                            szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":\"" + derivedVal + "\"";
                        } else if (headingNames[i].equals("TimeStamps")) {
                            szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":[[" + rs4.getString(headingNames[i]) + "],["
                                    + rs4.getString(headingNames[i]) + "],[" + rs4.getString(headingNames[i]) + "]]";
                        } else if (headingNames[i].equals("ServiceName")) {
                            szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":\"" + rs4.getString("ResourceID") + "\"";
                        } else {
                            szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":\"" + rs4.getString(headingNames[i]) + "\"";
                        }
                    }
                    szMetricTypeValueJson = szMetricTypeValueJson + "," + rs4.getString(headingNames[i]);
                }
                if (headingNames[headingNames.length - 1].equals("PerformanceRatio")) {
                    if (TargetEnergyMintue != null) {
                        szEnergy = rs4.getString(headingNames[headingNames.length - 1]);
                        /*val = (Double.parseDouble(szEnergy)) / (Double.parseDouble(TargetEnergyMintue)
                                * DateGenerator.getElapsedMinutes(timeStampSelection, null, customer, cCustomer, service));
                        */
                        val = (Double.parseDouble(szEnergy)) / (Double.parseDouble(TargetEnergyMintue)
                                * DateGenerator.getIrradiationOfMinutes(timeStampSelection, null,customer,cCustomer,service));
                        
                        
                        
                        df = new DecimalFormat("#.#");
                        derivedVal = df.format(val);
                    } else {
                        derivedVal = "";
                    }
                } else if (headingNames[headingNames.length - 1].equals("EnergyPerRatedPower")) {
                    derivedVal = "";
                    if (InstalledCapacity != null) {
                        szEnergy = rs4.getString(headingNames[headingNames.length - 1]);
                        val = (Double.parseDouble(szEnergy)) / (Double.parseDouble(InstalledCapacity));
                        df = new DecimalFormat("#.#");
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

    public static String getJSONFromAggregatedMetrics(String resType, String cCustomer, String service, String resType2, String metricType, String configuredMetricType,
            long sMilli, long eMilli, String timeStampSelection, String customer) {

        String resultJson = "[";
        String szQuery = null;
        //Calendar cal1 = Calendar.getInstance();
        Calendar cal1 = DateGenerator.getCurrentTime();
        DecimalFormat df = new DecimalFormat("#.#");
        String response = null;
        String[] splite = null;
        String[] dSplite = null;
        URL url = null;
        HttpURLConnection conn = null;
        ResultSet rs = null,rs5=null;
        BufferedReader br = null;
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        metricType = "Energy";
        String[] spliteca;
        String[] spliteco;
        Date date = null;
        ArrayList szServiceList = null;
        InputStreamReader isReader = null;
        double jsonIrradianValue=0;
         String panelArea;
        String mEfficiency;
        String pvpa="PVPanelArea";
        String meff="ModuleEfficiency";
         double totalNA=0;
         String subServiceQuery=null;   
           
        try {
            if (timeStampSelection.equalsIgnoreCase("Hour")) {
                return "[]";
            }
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
            System.out.println("resType2==>>" + resType2);
            System.out.println("In PerformanceRatioGenerator.generateJSON==>>smilli:" + sMilli + "::emilli:" + eMilli);

            String szEnergy = "";
            String prVal = "", eprpVal = "", aeVal = "";
            String InstalledCapacity;

            String rsValuesPR = null, rsValuesEPRP = null, rsValuesAE = null;
            szServiceList = new ArrayList();
            String szService;

            szQuery = "select distinct resourceid from hostinfo where customerid=(select id from customerinfo where customername='" + cCustomer + "') and "
                    + "service='" + service + "' and resourceid not like '%Adapter%' and resourceid !='" + service + "'";
            rs = ConnectionDAO.executerQuery(szQuery, customer);
            while (rs.next()) {
                szServiceList.add(rs.getString(1));
            }
            rs = null;
            System.out.println("PRCservicemap==> " + szServiceList);
            System.out.println("metricType==>>" + metricType);
            System.out.println("configuredMetricType==>>" + configuredMetricType);
            System.out.println("resType==>>" + resType);
            System.out.println("resType2==>>" + resType2);
            System.out.println("resType2==>>" + timeStampSelection);

            for (int k = 0; k < szServiceList.size(); k++) {
                szService = (String) szServiceList.get(k);
                System.out.println("for loop==>  " + szService);
                System.out.println("for length==>  " + szServiceList.size() + " " + k);
                System.out.println(szServiceList);

                url = new URL(DBUtilHelper.MetricValueURL + "?customer=" + cCustomer
                        + "&metrictype=" + metricType + "&service=" + service + "&timeperiod=" + timeStampSelection
                        + "&resourcetype=" + resType + "&slot=" + timeStampSelection + "&resourceid=" + szService + "&date=" + day + "&week=" + week + "&month=" + months[month]
                        + "&year=" + year);

                System.out.println("PRCurl>>> " + url);

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                isReader = new InputStreamReader(conn.getInputStream());
                br = new BufferedReader(isReader);
                // resultJson = "[]";
                
                    subServiceQuery="select distinct service,subservice,resourceid from hostinfo where customerid=(select id from customerinfo where customername='" + cCustomer + "') and service='"+service+"' and resourceid='"+szService+"';";
                System.out.println("subServiceQuery>>> " + subServiceQuery);
                System.out.println("subServiceQuery>>> " + subServiceQuery);
                rs5 = ConnectionDAO.executerQuery(subServiceQuery, customer);
                                 totalNA=0;
                    while(rs5.next()){
                          panelArea = ResourceConfiguration.getConfValue(customer, cCustomer, rs5.getString("service"), rs5.getString("subservice"),
                                             rs5.getString("resourceid"), pvpa);
                                System.out.println("panel area "+panelArea);
                                 mEfficiency=ResourceConfiguration.getConfValue(customer, cCustomer, rs5.getString("service"),rs5.getString("subservice"),
                                             rs5.getString("resourceid"), meff);
                                 System.out.println("mEfficiency "+mEfficiency);
                                 totalNA=totalNA+(Double.parseDouble(panelArea)*Double.parseDouble(mEfficiency));

                                 System.out.println("TotalNA "+totalNA);
                          }
                jsonIrradianValue=DateGenerator.getIrradiationOfMinutes(timeStampSelection,null,customer,cCustomer,service);
                 

                InstalledCapacity = ResourceConfiguration.getConfValue(customer, cCustomer, service, "Default",
                        szService, "Pmax");
                System.out.println("jsonIrradianValue " + jsonIrradianValue);
                System.out.println("InstalledCapacity " + InstalledCapacity);
                while ((response = br.readLine()) != null) {
                    System.out.println("Response from getMetricValue API::" + response);

                    if (response.contains(":")) {
                        splite = response.split("}");

                        rsValuesPR = "[]";
                        rsValuesEPRP = "[]";
                        rsValuesAE = "[]";

                        for (int i = 0; i < splite.length; i++) {

                            System.out.println("sp[i]==>" + i + splite[i]);
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

                                System.out.println("eval " + spliteco[1].substring(1, spliteco[1].length() - 1));
                                szEnergy = spliteco[1].substring(1, spliteco[1].length() - 1);
                            } else if (splite[i].contains(":")) {
                                System.out.println("else bb");
                                spliteco = splite[i].split(":");
                                szEnergy = spliteco[1].substring(1, spliteco[1].length() - 1);
                            }
                            if (splite[i].contains(":")) {
                                System.out.println("length " + splite.length);

                                System.out.println("energyval " + szEnergy);
                                prVal = "";
                                eprpVal = "";
                                aeVal = "";
                                if (jsonIrradianValue >0) {

                                    System.out.println("cust level::szEnergy to get performanceratio==>>" + szEnergy);
                                  /*  prVal = df.format((Double.parseDouble((String) szEnergy)) / (Double.parseDouble(TargetEnergyMinute)
                                            * DateGenerator.getElapsedMinutes(timeStampSelection, null, customer, cCustomer, service)));
                                    */
                                    
                                    prVal = df.format((Double.parseDouble((String) szEnergy)) / (jsonIrradianValue*totalNA));                              
                                    System.out.println("cust level::performanceratio val==>>" + prVal);

                                } else {
                                    prVal = "";
                                }
                                if (InstalledCapacity != null) {
                                    System.out.println("cust level::szEnergy to get EnergyPerRatedPower==>>" + szEnergy);
                                    System.out.println("df==>" + df.format((Double.parseDouble((String) szEnergy)) / (Double.parseDouble(InstalledCapacity))));
                                    eprpVal = df.format((Double.parseDouble((String) szEnergy)) / (Double.parseDouble(InstalledCapacity)));
                                    System.out.println("cust level:: EnergyPerRatedPower val==>>" + eprpVal);

                                } else {
                                    eprpVal = "";
                                }
                                if (szEnergy != null) {
                                    System.out.println("cust level::szEnergy to get actual energy ==>>" + szEnergy);
                                    System.out.println("df==>" + df.format((Double.parseDouble((String) szEnergy))));
                                    aeVal = df.format((Double.parseDouble((String) szEnergy)));
                                    System.out.println("cust level::actual energy val==>>" + aeVal);

                                } else {
                                    aeVal = "";
                                }
                                if (rsValuesPR.length() == 2) {

                                    rsValuesPR = "\"" + prVal + "\"";
                                    rsValuesEPRP = "\"" + eprpVal + "\"";
                                    rsValuesAE = "\"" + aeVal + "\"";

                                } else {

                                    System.out.println(rsValuesPR);
                                    rsValuesPR = "[" + rsValuesPR + ",\"" + prVal + "\"]";
                                    System.out.println("rsValuesPR " + rsValuesPR);

                                    rsValuesEPRP = "[" + rsValuesEPRP + ",\"" + eprpVal + "\"]";
                                    System.out.println("rsValuesEPRP " + rsValuesEPRP);
                                    rsValuesAE = "[" + rsValuesAE + ",\"" + aeVal + "\"]";
                                    System.out.println("rsValuesPR " + rsValuesAE);
                                }
                            }
                        }
                    } else {
                        rsValuesPR = "\"\"";
                        rsValuesEPRP = "\"\"";
                        rsValuesAE = "\"\"";
                    }
                    System.out.println(resultJson.length());
                    if (resultJson.length() == 1) {
                        resultJson = "[{\"Yield\":" + rsValuesAE + ",\"ResourceType\":\"" + resType + "\",\"ServiceName\":\"" + service + "\",\"EnergyPerRatedPower\":" + rsValuesEPRP + ",\"ResourceID\":\"" + szService + "\",\"PerformanceRatio\":" + rsValuesPR + "}";
                        System.out.println(resultJson.length());
                    } else {
                        resultJson += ",{\"Yield\":" + rsValuesAE + ",\"ResourceType\":\"" + resType + "\",\"ServiceName\":\"" + service + "\",\"EnergyPerRatedPower\":" + rsValuesEPRP + ",\"ResourceID\":\"" + szService + "\",\"PerformanceRatio\":" + rsValuesPR + "}";
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
                } else if (response != null) {
                    response = null;
                } else if (splite != null) {
                    splite = null;
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

    /*private static String modifyJson(String input) {
     String output = "";
     boolean altered = false;
     try {
     String[] arrayEles = input.split("},");
     System.out.println("arrayEles length==>>" + arrayEles.length);
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
