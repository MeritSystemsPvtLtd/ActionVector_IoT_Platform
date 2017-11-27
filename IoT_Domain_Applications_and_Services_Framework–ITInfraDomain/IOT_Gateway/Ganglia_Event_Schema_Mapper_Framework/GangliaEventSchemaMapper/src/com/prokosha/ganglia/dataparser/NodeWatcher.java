/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.ganglia.dataparser;

import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author Abraham
 */
public class NodeWatcher {

    private static final Logger log = Logger.getLogger(NodeWatcher.class.getName());
    private static HashMap nodeStartListeners = new HashMap<String, ArrayList>();
    private static HashMap nodeEndListeners = new HashMap<String, ArrayList>();

    public NodeWatcher() {
    }

    public static boolean nodeStart(String nodeType, HashMap avList) {

        if (log.isDebugEnabled()) {
            log.debug("\n---------- Ganglia XML " + nodeType + " node found by NodeWatcher------------\n");
            StringBuffer sbuf = new StringBuffer();
            Set keys = avList.keySet();
            Iterator index = keys.iterator();
            String key;

            while (index.hasNext()) {
                key = (String) index.next();
                sbuf.append("   ").append(key).append(" = ").append(avList.get(key)).append("   \n");
            }
            log.debug("\n-------- Ganglia XML node (" + nodeType + ") started ---------\n" + sbuf + "\n");
        }


        //invoke the listeners - call the wildcard listeners first and then the specific listeners
        invokeNodeStartListeners(nodeType, "*", avList); //wildcard

        String nodeName = (String) avList.get("NAME");
        invokeNodeStartListeners(nodeType, nodeName, avList); // specific

        return true;
    }

    public static boolean nodeEnd(String nodeType) {

        log.debug("\n-------- Ganglia XML node (" + nodeType + ") ended ---------\n");
        //invoke the listeners - call the wildcard listeners first and then the specific listeners
        invokeNodeEndListeners(nodeType);
        return true;
    }

    private static void invokeNodeEndListeners(String nodeType) {
        String key = nodeType;
        ArrayList gnList = (ArrayList) nodeEndListeners.get(key);
        if (gnList != null) {
            for (int i = 0; i < gnList.size(); i++) {
                GangliaNodeListener gnListener = (GangliaNodeListener) gnList.get(i);
                gnListener.nodeEnd(nodeType);
            }
        }
    }

    private static void invokeNodeStartListeners(String nodeType, String nodeName, HashMap avList) {
        String key = nodeType + ":" + nodeName;
        ArrayList gnList = (ArrayList) nodeStartListeners.get(key);
        if (gnList != null) {
            for (int i = 0; i < gnList.size(); i++) {
                GangliaNodeListener gnListener = (GangliaNodeListener) gnList.get(i);
                gnListener.nodeStart(nodeType, nodeName, avList);
            }
        }
    }

    private static void addNodeStartListener(String nodeType, String nodeName, GangliaNodeListener listener) {
        // Add node start listeners:
        // There can be several listeners for the same (nodeType,nodeName) pairs. So get the current
        // list of registered listeners and then append to that list.
        // Check to ensure we add only one instance of the listener for the same (nodeType, nodeName) pair.
        String key = nodeType + ":" + nodeName;
        ArrayList gnList = (ArrayList) nodeStartListeners.get(key);
        if (gnList != null) {
            //check if the listener is already registered for this key
            boolean isRegistered = false;
            for (int i = 0; (i < gnList.size() && !isRegistered); i++) {
                if (gnList.get(i).equals(listener)) {
                    isRegistered = true;
                }
            }
            if (!isRegistered) {
                gnList.add(listener);
            }
        } else { //first listener being added now
            gnList = new ArrayList();
            gnList.add(listener);
        }
        nodeStartListeners.put(key, gnList);
    }

    private static void addNodeEndListener(String nodeType, GangliaNodeListener listener) {
        // Add node end listerners - node end listener is invoked on nodeType
        // There can be several listeners for the same nodeType. So get the current
        // list of registered listeners and then append to that list.
        // Check to ensure we add only one instance of the listener for the same nodeType.
        String key = nodeType;
        ArrayList gnList = (ArrayList) nodeEndListeners.get(key);

        if (gnList != null) {
            //check if the listener is already registered for this key
            boolean isRegistered = false;
            for (int i = 0; (i < gnList.size() && !isRegistered); i++) {
                if (gnList.get(i).equals(listener)) {
                    isRegistered = true;
                }
            }
            if (!isRegistered) {
                gnList.add(listener);
            }
        } else { //first listener being added now
            gnList = new ArrayList();
            gnList.add(listener);
        }
        nodeEndListeners.put(key, gnList);
    }

    public static void addListener(String nodeType, String nodeName, GangliaNodeListener listener) {
        log.debug("Adding Ganglia XML node start listener:: (nodeType=" + nodeType + ") (nodeName=" + nodeName + ") (listener=" + listener + ")");
        addNodeStartListener(nodeType, nodeName, listener);
        log.debug("Adding Ganglia XML node end listener:: (nodeType=" + nodeType + ") (listener=" + listener + ")");
        addNodeEndListener(nodeType, listener);
    }

    public static void printNodeListeners() {

        StringBuffer sbuf = new StringBuffer();
        Iterator index;
        String key;

        sbuf.append("\n\n----------- NODE START LISTENERS ---------------\n");
        index = nodeStartListeners.keySet().iterator();
        for (int i = 0; i < nodeStartListeners.size(); i++) {
            key = (String) index.next();
            sbuf.append("\n   ::Node " + i + "::");
            sbuf.append("     nodeType:nodeName=" + key + "\n");

            ArrayList ar = (ArrayList) nodeStartListeners.get(key);
            for (int j = 0; j < ar.size(); j++) {
                sbuf.append("           [" + j + "]  " + ar.get(j) + "\n");
            }
        }
        log.debug(sbuf);
        //private static HashMap nodeEndListeners = new HashMap<String, ArrayList>();
    }
}
