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
import java.util.List;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

/**
 *
 * @author rekhas
 */
public class ResourceConfiguration {

    static Logger log = Logger.getLogger(ResourceConfiguration.class);
    static HashMap<String, ArrayList> resConfMap;

    public static boolean initialize(List<String> customerList) {
        ArrayList ccResConfList = null;

        InputStreamReader isr;//modified
        InputStream is;
        try {
            resConfMap = new HashMap();
            String APIURL;//
            URL obj;//
            HttpURLConnection con;//
            int responseCode;//
            BufferedReader in;//
            String inputLine;//
            StringBuffer response;//
            String szResourceConfigXML;//
            for (String customer : customerList) {
                //call clouduser api to get resourceconfig from DB as XML
                //String APIURL = "http://192.168.1.2:8084/CloudUserAPI/GetXMLResourceConfig";
                APIURL = DBUtilHelper.XMLResConfigURL;
                obj = new URL(APIURL);
                con = (HttpURLConnection) obj.openConnection();
                responseCode = con.getResponseCode();
                System.out.println("GetResourceConfig Response Code : " + responseCode);
                is = con.getInputStream();
                isr = new InputStreamReader(is);//modified
                in = new BufferedReader(isr);//modified

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
                szResourceConfigXML = response.toString();
                //convert xml to string
                ccResConfList = getListFromXML(szResourceConfigXML);
                resConfMap.put(customer, ccResConfList);
            }
            return true;
        } catch (Exception e) {
            log.error("Error in initialize()==>>" + e.toString());
            e.printStackTrace();
        }
        return false;
    }

    private static ArrayList getListFromXML(String szXML) {
        ArrayList resMap = new ArrayList();
        try {
            //SAXReader reader = new SAXReader();
            //Document document = reader.read(szXML);
            Document document = DocumentHelper.parseText(szXML);
            System.out.println("Total Configuration==>> " + document.selectNodes("//resourceconfig/customer").size());
            List nodeList = document.selectNodes("//resourceconfig/customer");
            Iterator nodeItr = nodeList.iterator();
            String ccustomer;
            String service;
            String subservice;
            String resourceid;
            String paramName;
            String paramValue;
            Configuration szConf = null;
            Node custNode;//
            while (nodeItr.hasNext()) {
                custNode = (Node) nodeItr.next();
                ccustomer = custNode.selectSingleNode("name").getText();
                service = custNode.selectSingleNode("service").getText();
                subservice = custNode.selectSingleNode("subservice").getText();
                resourceid = custNode.selectSingleNode("resourceid").getText();
                paramName = custNode.selectSingleNode("paramname").getText();
                paramValue = custNode.selectSingleNode("paramvalue").getText();
                szConf = new Configuration(ccustomer, service, subservice, resourceid, paramName, paramValue);
                resMap.add(szConf);
            }
            return resMap;
        } catch (Exception e) {
            log.error("Error in getMapFromXML==>>" + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public static String getConfValue(String customer, String ccustomer, String service, String subservice, String resourceid, String paramName) {
        try {
            log.info("Get configured value for==>>" + customer + ":" + ccustomer + ":" + service + ":" + subservice + ":" + resourceid + ":" + paramName);
            ArrayList ccResConfList = (ArrayList) resConfMap.get(customer);
            if (ccResConfList != null) {
                log.info("ccResConfList==>>" + ccResConfList.size());
                Configuration szConf;
                for (Object szConfObj : ccResConfList) {
                    szConf = (Configuration) szConfObj;
                    if (szConf.compare(ccustomer, service, subservice, resourceid, paramName)) {
                        return szConf.confVal;
                    }
                }
            } else {
                log.error("Configuration for==>>" + customer + ":" + ccustomer + ":" + service + ":" + subservice + ":" + resourceid + ":" + paramName
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

        Configuration(String szCust, String szServ, String szSubser, String szRes, String pName, String pVal) {
            this.confKey = szCust + ":" + szServ + ":" + szSubser + ":" + szRes + ":" + pName;
            this.confVal = pVal;
        }

        private boolean compare(String szCust, String szServ, String szSubser, String szRes, String pName) {
            try {
                String reqKey = szCust + ":" + szServ + ":" + szSubser + ":" + szRes + ":" + pName;
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
