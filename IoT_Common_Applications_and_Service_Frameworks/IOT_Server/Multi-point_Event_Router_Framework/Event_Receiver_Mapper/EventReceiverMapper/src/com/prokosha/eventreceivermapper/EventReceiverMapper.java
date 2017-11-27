/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.eventreceivermapper;

import com.prokosha.eventreceivermapper.assoc.AddressMap;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author anand kumar verma
 * ********************************************************************
 * Copyright message Software Developed by Merit Systems Pvt. Ltd., No. 42/1,
 * 55/c, Nandi Mansion, 40th Cross, Jayanagar 8th Block Bangalore - 560 070,
 * India Work Created for Merit Systems Private Limited All rights reserved
 *
 * THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES
 * NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED, DISTRIBUTED,
 * REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED,
 * COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD
 * SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 * *********************************************************************
 */
public class EventReceiverMapper {

    private static final Logger log = Logger.getLogger(EventReceiverMapper.class.getName());
    public static Properties pro = null;
    private static String mutliClient = null;
    /**
     * All enabled Senders in form of String Array
     */
    public static String[] gszArrProcessorGroup;
    /**
     * All enabled Event Type in form of String Array
     */
    public String[] gszArrEventTypeGroup;
    /**
     * All enabled EventProcessors Object Group in form of HashMap
     */
    public HashMap<String, EventProcessor> processorObjectGroup;
    /**
     * AddressMap Class object to store all the mapped Host with their name and
     * processors group
     */
    public static AddressMap addressMap;

    /**
     * initialize the Properties class Object and load the properties from the
     * configuration file.
     */
    public void ini() throws Exception {
        pro = new Properties();
        String szPropFile = "AddressConfig.properties";
        String szUserHome_dir = System.getProperty("user.home");
        System.out.println("user home dir   :" + szUserHome_dir);
        String FileName = szUserHome_dir + System.getProperty("file.separator") + szPropFile;
        System.out.println("user home dir szFilePath" + FileName);
        FileInputStream flInptStrm = new FileInputStream(FileName);
        pro.load(flInptStrm);
        flInptStrm.close();

        //  setClientsLifeSpan();
        mutliClient = pro.getProperty("enable.MultipleClients");
        if (mutliClient == null) {
            throw (new Exception("configure 'enable.MultipleClients' to one of the value ['true', 'false', 'enabled', 'disabled'] in 'AddressConfig.properties' file."));
        }

        String value = pro.getProperty("enabledEventProcessors").trim();
        gszArrProcessorGroup = value.split(",");

        System.out.println("EventProcessors = " + value);
        value = pro.getProperty("enabledEventTypes").trim();
        gszArrEventTypeGroup = value.split(",");
        System.out.println("eventTypes = " + value);
        flInptStrm = null;
    }

    public static boolean isForMultipleClient() {
        mutliClient = mutliClient.trim();
        if (mutliClient.equalsIgnoreCase("true") || mutliClient.equalsIgnoreCase("enabled")) {
            return true;
        } else if (mutliClient.equalsIgnoreCase("false") || mutliClient.equalsIgnoreCase("disabled")) {
            return false;
        }
        return true;
    }

    /**
     * initialize EventProcessors ObjectGroup of HashMap class and maps them
     * with their name
     */
    public void initProcessorObjectGroup() {
        try {
            processorObjectGroup = new HashMap<String, EventProcessor>();
            EventProcessor processorObject;
            String processorAddress;
            String addrvals[];
            for (String processor : gszArrProcessorGroup) {
                processorAddress = pro.getProperty(processor);
                if (processorAddress == null) {
                    System.out.println("Exception : address for processor '" + processor + "' is not configured in property file.");
                    System.out.println("Exception : The processor '" + processor + "' is not included in processors list.");
                } else {
                    processorAddress = processorAddress.trim();
                    addrvals = getHost_Port(processorAddress);
                    processorObject = new EventProcessor(processor, addrvals[0], addrvals[1]);
                    processorObject.init();
                    processorObjectGroup.put(processor, processorObject);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //addressMap = new AddressMap(gszArrEventTypeGroup, processorObjectGroup, pro);
    }

    /**
     * initialize AddressMap Object
     */
    public void initAddressMap() {
        addressMap = new AddressMap(gszArrEventTypeGroup, processorObjectGroup, pro);
    }

    /**
     * split the given address {Host@n1.n2.n3.n4::Port@nnnn} into IP address and
     * port
     *
     * @param address as String value that must be {Host@n1.n2.n3.n4::Port@nnnn}
     * @return An String Array Object that contains IP Address as first value
     * and port as sencond value of the server specified in input
     */
    public String[] getHost_Port(String address) {

        String hostPattern = " Host@";
        String portPattern = " Port@";

        String host;
        String port;

        if (address.contains("Host@")) {
            hostPattern = "Host@";
        } else if (address.contains("host@")) {
            hostPattern = "host@";
        }

        if (address.contains("Port@")) {
            portPattern = "Port@";
        } else if (address.contains("port@")) {
            portPattern = "port@";
        }

        host = (address.split(hostPattern)[1]).split("::")[0];
        port = (address.split(portPattern)[1]).split("::")[0];
        return new String[]{host, port};
    }

    /**
     * closes all connections to all mapped hosts
     */
    public void closeProcessorGroup() {
        Collection<EventProcessor> list = processorObjectGroup.values();

        for (EventProcessor processor : list) {
            processor.closeDataProcessor();
        }
    }

    /**
     * checks weather a sender is included in Enabled Processors List
     *
     * @param processor name
     * @return boolean value <table> <tr><td>true - </td><td> if sender is
     * present in Enabled Senders List</td></tr> <tr><td>false - </td><td> if
     * sender is present in Enabled Senders List</td></tr></table>
     */
    public static boolean isSenderEnabled(String processor) {
        for (String senderName : gszArrProcessorGroup) {
            if (processor.equalsIgnoreCase(senderName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param args the command line arguments
     */
    public static void initialize() {
        // TODO code application logic here
        try {
            //PropertyConfigurator.configure("logger.properties");
            EventReceiverMapper obj = new EventReceiverMapper();
            obj.ini();
            //  obj.initReceiver();
            System.out.println("Calling initProcessorObjectGroup");
            obj.initProcessorObjectGroup();
            System.out.println("Calling initAddressMap");
            obj.initAddressMap();
            // obj.eventReceiver.startListener();
            //System.out.println("Calling closeProcessorGroup");
            //obj.closeProcessorGroup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
