/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.modbusadapter.configuration;

import com.prokosha.emailsmsutility.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author rekhas
 */
public class ModBusEventMapper {

    private static Logger logger = Logger.getLogger(ModBusEventMapper.class.getName());
    private static String xmlEventMapper = null;
    private static String eventStaticDataFile = null;
    private static String parserMappingFile = null;
    private static HashMap<String, HashMap> modbusEventSchema;
    private static HashMap<String, String> eventStaticData;
    private static HashMap<String, String> parserMapping;
    private static Object szParamList;

    public static boolean initializeModBusEvents(String file) {
        xmlEventMapper = file;
        return readXMLModBusEvents();
    }

    public static boolean readXMLModBusEvents() {
        SAXBuilder builder = null;
        File xmlFile = null;
        String evtName = null;
        String dataCollection = null;
        String szEvtField = null;
        String szFroName = null;
        String szFroValue = null;
        String szFroUnit = null;
        String szEvtType = null;
        try {
            builder = new SAXBuilder();
            xmlFile = new File(xmlEventMapper);
            Document document = (Document) builder.build(xmlFile);
            Element rootNode = document.getRootElement();
            List list = rootNode.getChildren("EventDefinition");
            HashMap<String, ArrayList> dataCollMap;
            for (int i = 0; i < list.size(); i++) {
                Element node = (Element) list.get(i);
                List cepEvtNodes = node.getChildren("CepEvent");
                modbusEventSchema = new HashMap(cepEvtNodes.size());
                logger.info("No. of events configured==>>" + cepEvtNodes.size());
                for (int k = 0; k < cepEvtNodes.size(); k++) {
                    Element cepEvtNode = (Element) cepEvtNodes.get(k);
                    evtName = cepEvtNode.getAttributeValue("evName");
                    logger.info("creating evet map for event==>>" + evtName);
                    dataCollMap = new HashMap();
                    List paramList = cepEvtNode.getChildren("Parameter");
                    logger.info("No of params configured==>>" + paramList.size());
                    for (int j = 0; j < paramList.size(); j++) {
                        Element paramNode = (Element) paramList.get(j);
                        logger.info("param attributes==>>" + paramNode.getAttributes().toString());
                        dataCollection = paramNode.getAttributeValue("dataCollection");
                        szEvtField = paramNode.getAttributeValue("evField");
                        szFroName = paramNode.getAttributeValue("fName");
                        szFroValue = paramNode.getAttributeValue("fVal");
                        szFroUnit = paramNode.getAttributeValue("fUnit");
                        szEvtType = paramNode.getAttributeValue("evType");
                        ModBusEventParam paramObj = new ModBusEventParam(szEvtField, szFroName, szFroValue, szFroUnit, szEvtType);
                        //logger.info("created ModBusEventParam==>>" + paramObj.toString());
                        if (dataCollMap.containsKey(dataCollection)) {
                            ((ArrayList) dataCollMap.get(dataCollection)).add(paramObj);
                            //logger.info("Data collection " + dataCollection + " found.");
                        } else {
                            ArrayList szAryLst = new ArrayList();
                            szAryLst.add(paramObj);
                            dataCollMap.put(dataCollection, szAryLst);
                            //logger.info("Data collection " + dataCollection + " not found. Created new one");
                        }
                        dataCollection = null;
                        szEvtField = null;
                        szFroName = null;
                        szFroValue = null;
                        szFroUnit = null;
                        szEvtType = null;
                    }
                    modbusEventSchema.put(evtName, dataCollMap);
                    logger.info("EventSchema");
                    logger.info("EventName==>>" + evtName);
                    Set dataCollSet = modbusEventSchema.get(evtName).keySet();
                    Iterator szDataCollItr = dataCollSet.iterator();
                    while (szDataCollItr.hasNext()) {
                        String dataColln = szDataCollItr.next().toString();
                        logger.info("datCollection==>>" + dataColln);
                        ArrayList szParamList = (ArrayList) modbusEventSchema.get(evtName).get(dataColln);
                        logger.info("ParamList==>>" + szParamList.toString());
                    }
                }
            }
            builder = null;
            xmlFile = null;
            document = null;
            rootNode = null;
            xmlEventMapper = null;
            evtName = null;
            dataCollection = null;
            szEvtField = null;
            szFroName = null;
            szFroValue = null;
            szFroUnit = null;
            szEvtType = null;
            return true;
        } catch (Exception e) {
            logger.error("Error in reading " + xmlEventMapper);
            logger.error("Send msg and e-mail" + e.toString());
            if (AdapterProperties.getSendErrorSMS()) {
                EMailSMSUtility.sendSMS("Error in reading " + xmlEventMapper + "==>>" + e.toString());
            }
            String szSubject = "ModBusAdapterError";
            if (AdapterProperties.getSendErrorMail()) {

                String szBody = "Error in reading " + xmlEventMapper + "==>>" + e.toString();
                EMailSMSUtility.sendMail(szSubject, szBody);
            }
            e.printStackTrace();
        } finally {
            builder = null;
            xmlFile = null;
            xmlEventMapper = null;
            evtName = null;
            dataCollection = null;
            szEvtField = null;
            szFroName = null;
            szFroValue = null;
            szFroUnit = null;
            szEvtType = null;

        }
        return false;
    }

    public static HashMap getModBusEvents() {
        return modbusEventSchema;
    }

    public static boolean initializeEventStaticData(String fileName) {
        eventStaticDataFile = fileName;
        String evtName = "";
        Properties props = null;
        FileInputStream fps = null;
        try {
            logger.debug("Loading properties file: " + eventStaticDataFile);
            props = new Properties();
            fps = new FileInputStream(eventStaticDataFile);
            props.load(fps);
            fps.close();
            fps = null;
            Set eventNames = props.keySet();
            Iterator propsItr = eventNames.iterator();
            eventStaticData = new HashMap(props.size());
            while (propsItr.hasNext()) {
                evtName = propsItr.next().toString();
                eventStaticData.put(evtName, props.getProperty(evtName));
            }
            props.clear();
            props = null;
            fps = null;
            evtName = null;
            return true;
        } catch (Exception e) {
            logger.error("Error in setting static data for event " + evtName + "==>>" + e.getMessage());
            logger.error("Send msg and e-mail" + e.toString());
            if (AdapterProperties.getSendErrorSMS()) {

                EMailSMSUtility.sendSMS("Error in setting static data for event " + evtName + "==>>" + e.getMessage());
            }
            String szSubject = "ModBusAdapterError";
            if (AdapterProperties.getSendErrorMail()) {

                String szBody = "Error in setting static data for event " + evtName + "==>>" + e.getMessage();
                EMailSMSUtility.sendMail(szSubject, szBody);
            }
            e.printStackTrace();
        } finally {
            try {
                if (fps != null) {
                    fps.close();
                    fps = null;
                }
                if (props != null) {
                    props.clear();
                    props = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static HashMap getEventStaticData() {
        return eventStaticData;
    }

    public static boolean initializeParserMapping(String fileName) {
        parserMappingFile = fileName;
        String evtName = "";
        Properties props = null;
        FileInputStream fps = null;
        try {
            logger.debug("Loading properties file: " + parserMappingFile);

            props = new Properties();
            fps = new FileInputStream(parserMappingFile);
            props.load(fps);
            fps.close();
            fps = null;
            Set eventNames = props.keySet();
            Iterator propsItr = eventNames.iterator();
            parserMapping = new HashMap(props.size());
            while (propsItr.hasNext()) {
                evtName = propsItr.next().toString();
                parserMapping.put(evtName, props.getProperty(evtName));
            }
            evtName = null;
            props.clear();
            props = null;
            fps = null;
            return true;
        } catch (Exception e) {
            logger.error("Error in setting static data for event " + evtName + "==>>" + e.getMessage());
            logger.error("Send msg and e-mail" + e.toString());
            if (AdapterProperties.getSendErrorSMS()) {

                EMailSMSUtility.sendSMS("Error in setting static data for event " + evtName + "==>>" + e.getMessage());
            }
            String szSubject = "ModBusAdapterError";
            if (AdapterProperties.getSendErrorMail()) {

                String szBody = "Error in setting static data for event " + evtName + "==>>" + e.getMessage();
                EMailSMSUtility.sendMail(szSubject, szBody);
            }
            e.printStackTrace();
        } finally {
            try {
                if (fps != null) {
                    fps.close();
                    fps = null;
                }
                if (props != null) {
                    props.clear();
                    props = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static String getParser(String evt, String dataColl) {
        try {
            return parserMapping.get(evt + "." + dataColl).toString();
        } catch (Exception e) {
            logger.error("Send msg and e-mail" + e.toString());
            if (AdapterProperties.getSendErrorSMS()) {

                EMailSMSUtility.sendSMS("getParser failed for " + evt + "==>>" + e.getMessage());
            }
            String szSubject = "ModBusAdapterError";
            if (AdapterProperties.getSendErrorMail()) {

                String szBody = "getParser failed for " + evt + "==>>" + e.getMessage();
                EMailSMSUtility.sendMail(szSubject, szBody);
            }
            e.printStackTrace();
        }
        return null;
    }
}
