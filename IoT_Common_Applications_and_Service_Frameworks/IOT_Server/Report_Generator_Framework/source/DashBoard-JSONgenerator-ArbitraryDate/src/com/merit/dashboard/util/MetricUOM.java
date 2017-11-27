/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.merit.dashboard.util;

import com.merit.dashboard.DBUtil.DBUtilHelper;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author niteshc
 */
public class MetricUOM {

    static Logger log = Logger.getLogger(MetricUOM.class);
    static HashMap<String, ArrayList> metricUOMMap;

    public static boolean initialize(List<String> customerList) {
        ArrayList metricUOMConfList = null;

        InputStreamReader isr;
        InputStream is;

        try {
            metricUOMMap = new HashMap();
            String APIURL;
            URL obj;
            HttpURLConnection con;
            int responseCode;
            BufferedReader in;
            String inputLine;
            StringBuffer response;
            String szAllMetircConfigJSON;
            for (String customer : customerList) {
                //call clouduser api to get resourceconfig from DB as Json                
                APIURL = DBUtilHelper.AllMetricUOMURL;//String APIURL = "http://192.168.1.2:8084/CloudUserAPI/GetAllMetricUOM";
                obj = new URL(APIURL);
                con = (HttpURLConnection) obj.openConnection();
                responseCode = con.getResponseCode();
                System.out.println("GetAllMetricUOM Response Code : " + responseCode);
                is = con.getInputStream();
                isr = new InputStreamReader(is);
                in = new BufferedReader(isr);

                response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                in = null;
                isr.close();
                isr = null;
                is.close();
                is = null;
                szAllMetircConfigJSON = response.toString(); //convert json to string
                metricUOMConfList = getListFromJSON(szAllMetircConfigJSON);
                metricUOMMap.put(customer, metricUOMConfList);
            }
            return true;
        } catch (Exception e) {
            log.error("Error in initialize()==>>" + e.toString());
            e.printStackTrace();
        }
        return false;
    }

    private static ArrayList getListFromJSON(String szJSON) {

        ArrayList UOMList = new ArrayList();
        int i = 0;
        Configuration szConf = null;
        String customer;
        String service;
        String subservice;
        String resourceType;
        String metricType;
        String metricUOM;
        Iterator cusItr = null, serItr = null, subSerItr = null, resItr = null, metricItr = null;
        JSONObject cusObj = null, serObj = null, serSubObj = null, resObj = null, metric = null;
        JSONArray cusArray = null, serArray = null, subSerArray = null, resArray = null, UOMArray = null;
        try {

            JSONParser parser = new JSONParser();
            szJSON = szJSON.replace("null('[", "");
            szJSON = szJSON.replace("]')", "");
            log.info(szJSON);
            JSONObject jsonObject = (JSONObject) parser.parse(szJSON);
            cusArray = (JSONArray) jsonObject.get("UOMMap");
            if(cusArray!=null){
            cusItr = cusArray.iterator();
            

            while (cusItr.hasNext()) {
                cusObj = (JSONObject) cusItr.next();
                customer = cusObj.get("customer").toString();

                serArray = (JSONArray) cusObj.get("UOMMap");
                serItr = serArray.iterator();

                while (serItr.hasNext()) {
                    serObj = (JSONObject) serItr.next();
                    service = serObj.get("service").toString();

                    subSerArray = (JSONArray) serObj.get("UOMMap");
                    subSerItr = subSerArray.iterator();

                    while (subSerItr.hasNext()) {
                        serSubObj = (JSONObject) subSerItr.next();
                        subservice = (String) serSubObj.get("subservice");

                        resArray = (JSONArray) serSubObj.get("UOMMap");
                        resItr = resArray.iterator();

                        while (resItr.hasNext()) {
                            resObj = (JSONObject) resItr.next();
                            resourceType = (String) resObj.get("resourcetype");

                            UOMArray = (JSONArray) resObj.get("UOMMap");
                            metricItr = UOMArray.iterator();

                            while (metricItr.hasNext()) {
                                metric = (JSONObject) metricItr.next();
                                metricType = (String) metric.get("metrictype");
                                metricUOM = (String) metric.get("metricUOM");
                                szConf = new Configuration(customer, service, subservice, resourceType, metricType, metricUOM);
                                UOMList.add(szConf);
                            }
                            metricItr = null;
                            // metricUOMMap.clear();
                        }
                        resItr = null;
                    }
                    subSerItr = null;
                }
                serItr = null;
            }
            }
            cusItr = null;
            return UOMList;
        } catch (Exception e) {
            log.error("Error in getListFromJSON()==>>" + e.toString());
            e.printStackTrace();
        } finally {
        }
        return null;
    }

    public static String getConfValue(String customer, String ccustomer, String service, String subservice, String resourcetype, String metrictype) {
        try {
            log.info("Get configured value for==>>" + customer + ":" + ccustomer + ":" + service + ":" + subservice + ":" + resourcetype + ":" + metrictype);
            ArrayList ccUOMList = (ArrayList) metricUOMMap.get(customer);
            if (ccUOMList != null) {
                log.info("ccResConfList==>>" + ccUOMList.size());
                Configuration szConf;
                for (Object szConfObj : ccUOMList) {
                    szConf = (Configuration) szConfObj;
                    if (szConf.compare(ccustomer, service, subservice, resourcetype, metrictype)) {
                        log.debug("MetricUOM::" + szConf.confVal);
                        return szConf.confVal;
                    } else {
                        log.debug("MetricUOM::null");
                    }
                }
            } else {
                log.error("Configuration for==>>" + customer + ":" + ccustomer + ":" + service + ":" + subservice + ":" + resourcetype + ":" + metrictype
                        + "not found.");
            }
        } catch (Exception e) {
            log.error("Error in getConfValue==>>" + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    private static class Configuration {

        private String confKey;
        private String confVal;

        Configuration(String szCust, String szServ, String szSubService, String szResType, String mtype, String mUnit) {
            this.confKey = szCust + ":" + szServ + ":" + szSubService + ":" + szResType + ":" + mtype;
            this.confVal = mUnit;
        }

        private boolean compare(String szCust, String szServ, String szSubService, String szResType, String mType) {
            try {
                String reqKey = szCust + ":" + szServ + ":" + szSubService + ":" + szResType + ":" + mType;
                if (confKey.equals(reqKey)) {
                    return true;
                }
            } catch (Exception e) {
                log.error("Error in compare()==>>" + e.toString());
                e.printStackTrace();
            }
            return false;
        }
    }
}
