/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.modbusadapter.configuration;

import com.prokosha.emailsmsutility.EMailSMSUtility;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author rekhas
 */
public class ModBusRequests {

    private static Logger logger = Logger.getLogger(ModBusRequests.class.getName());
    private static String xmlConfigFile = null;
    //private static Request[] szRequests;
    private static HashMap<String, HashMap> modbusRequestsMap;

    public static boolean initializeRequests(String file) {
        xmlConfigFile = file;
        return readXMLRequests();
    }

    public static boolean readXMLRequests() {
        SAXBuilder builder = null;
        File xmlFile = null;
        String request = null;
        String scope = null;
        String[] deviceIDs = null;
        String dataCollection = null;
        String froEvent = null;
        String timePeriod = null;

        try {
            builder = new SAXBuilder();
            xmlFile = new File(xmlConfigFile);
            Document document = (Document) builder.build(xmlFile);
            Element rootNode = document.getRootElement();
            List list = rootNode.getChildren("Request");
            logger.info("No. of requests configured==>>" + list.size());
            //szRequests = new Request[list.size()];
            modbusRequestsMap = new HashMap();

            for (int i = 0; i < list.size(); i++) {
                Element node = (Element) list.get(i);
                request = node.getAttributeValue("request");
                scope = node.getAttributeValue("scope");
                timePeriod = node.getAttributeValue("timePeriod");
                froEvent = node.getAttributeValue("froEvent");
                if (!(modbusRequestsMap.containsKey(froEvent))) {
                    modbusRequestsMap.put(froEvent, new HashMap());
                }
                dataCollection = node.getAttributeValue("dataCollection");
                HashMap deviceMap = modbusRequestsMap.get(froEvent);
                if (scope.equalsIgnoreCase("device")) {
                    deviceIDs = node.getAttributeValue("deviceIDs").split(",");
                    String szTemp = dataCollection + ":" + request + ":" + timePeriod;
                    for (int j = 0; j < deviceIDs.length; j++) {
                        if (!(deviceMap.containsKey(deviceIDs[j]))) {
                            ArrayList szList = new ArrayList();
                            szList.add(szTemp);
                            deviceMap.put(deviceIDs[j], szList);
                        } else {
                            ((ArrayList) deviceMap.get(deviceIDs[j])).add(szTemp);
                        }
                    }
                } else if (scope.equalsIgnoreCase("system")) {
                    logger.info("request scope is system");
                    ArrayList szList = new ArrayList();
                    szList.add(dataCollection + ":" + request + ":" + timePeriod);
                    deviceMap.put("System", szList);
                }
            }
            builder = null;
            xmlFile = null;
            document = null;
            rootNode = null;
            request = null;
            scope = null;
            deviceIDs = null;
            dataCollection = null;
            froEvent = null;
            timePeriod = null;
            return true;
        } catch (Exception e) {
            logger.error("Error in reading " + xmlConfigFile);
            logger.error("Send msg and e-mail" + e.toString());
            if (AdapterProperties.getSendErrorSMS()) {
                EMailSMSUtility.sendSMS("Error in reading " + xmlConfigFile + "==>>" + e.getMessage());
            }
            String szSubject = "ModBusAdapterError";
            if (AdapterProperties.getSendErrorMail()) {

                String szBody = "Error in reading " + xmlConfigFile + "==>>" + e.getMessage();
                EMailSMSUtility.sendMail(szSubject, szBody);
            }
            e.printStackTrace();
        } finally {
            builder = null;
            xmlFile = null;
            xmlConfigFile = null;
            request = null;
            scope = null;
            deviceIDs = null;
            dataCollection = null;
            froEvent = null;
            timePeriod = null;
        }
        return false;
    }

    public static HashMap getRequestsMap() {
        return modbusRequestsMap;
    }
}
