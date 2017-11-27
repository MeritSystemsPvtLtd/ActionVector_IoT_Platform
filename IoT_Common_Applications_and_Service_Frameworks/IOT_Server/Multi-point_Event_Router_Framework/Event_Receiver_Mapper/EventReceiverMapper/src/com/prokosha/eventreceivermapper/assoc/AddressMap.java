/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.eventreceivermapper.assoc;

import com.prokosha.eventreceivermapper.EventProcessor;
import com.prokosha.eventreceivermapper.EventReceiverMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author anand kumar verma
 *********************************************************************
  Copyright message
  Software Developed by
  Merit Systems Pvt. Ltd.,
  No. 42/1, 55/c, Nandi Mansion, 40th Cross, Jayanagar 8th Block
  Bangalore - 560 070, India
  Work Created for Merit Systems Private Limited
  All rights reserved

  THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES
  NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED,
  DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED,
  EXPANDED, COLLECTED, COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED
  WITHOUT THE PRIOR WRITTEN CONSENT
  ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT
  THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 **********************************************************************
 */

/**
 * AddressMap class creates a map as per the configuration file.
 */
public class AddressMap {
    
    private static final Logger log = Logger.getLogger(AddressMap.class.getName());

    HashMap<String, List<EventProcessor>> map;
    String[] eventTypeGroup;

    /**
     * @param eventTypeGroup <table><tr><td>   </td><td>array of names for those events that are included in list of enabled eventType.</td></tr></table>
     * @see  Main
     * @param senderObjectGroup <table><tr><td>   </td><td>A map object containing objects of EventProcessor class with their name as given in configuration file.</td></tr></table>
     * @see EventProcessor
     * @param property <table><tr><td>   </td><td>A object of Properties class that loads properties configuration file.</td></tr></table>
     */
    public AddressMap(String[] eventTypeGroup, HashMap<String, EventProcessor> senderObjectGroup, Properties property) {
        this.eventTypeGroup = eventTypeGroup;
        map = new HashMap<String, List<EventProcessor>>();

        String processors[];
        String processor;
        EventProcessor senderObject;
        List<EventProcessor> processorList;
        for (String eventType : eventTypeGroup) {
            processorList = new ArrayList<EventProcessor>();
            processor = property.getProperty(eventType);
            if (processor == null) {
                System.out.println("Exception : processors configuration list for eventType '" + eventType + "' is empty.");
                processorList.add(null);
            } else {
                processor = processor.trim();
                processors = processor.split(",");
                for (String processorName : processors) {
                    if(EventReceiverMapper.isSenderEnabled(processorName)){
                        senderObject = senderObjectGroup.get(processorName);
                        if(senderObject == null){
                            System.out.println("Exception : processorObject for name '" + processorName + "' is either not configured in property file or the values or incorrect or the machine at that sender is shut down.");
                        }else{
                            processorList.add(senderObject);
                        }
                    }else{
                        System.out.println("Message : processorObject for name '" + processorName + "' is not enabled.");
                    }
                }
            }
            map.put(eventType, processorList);
        }
    }

    public AddressMap() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return map object that contains mapping of eventType and senders
     */
    public HashMap<String, List<EventProcessor>> getAddHashMap() {
        return map;
    }

    /**
     * sends events to corresponding hosts according to configuration
     * @param eventType parameter as name of event
     * @param event the String value of event
     */
    public void sendEvent(String eventType, String event) {
        List<EventProcessor> group = (List<EventProcessor>) map.get(eventType);
        if (group != null) {
            //log.info("processor group length for eventType '" + eventType + "' : " + group.size());
            for (EventProcessor processor : group) {
                if (processor != null) {
                    processor.sendEvent(eventType, event);
                }
            }
        } else {
            System.out.println("processor group is null for eventType '" + eventType + "'");
        }
    }
}
