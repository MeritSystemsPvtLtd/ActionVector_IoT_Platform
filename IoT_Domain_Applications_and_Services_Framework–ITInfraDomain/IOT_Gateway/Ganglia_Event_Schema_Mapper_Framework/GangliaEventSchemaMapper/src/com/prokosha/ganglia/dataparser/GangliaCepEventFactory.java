/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.ganglia.dataparser;

import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author Abraham
 */
public class GangliaCepEventFactory {

    private static final Logger log = Logger.getLogger(GangliaCepEventFactory.class.getName());
    private static GangliaCepEvent cepEvent;
    private static boolean evStarted = false;

    public static boolean nodeStart(String nodeName, HashMap<String,String> hm) {
        log.debug("nodeStart:: nodeName: " + nodeName + " attribute list : " + hm.toString());

        //Look for start of a new event -- <CepEvent> node
        //In this node, attribute containerNode is encompassing node and evName is the CEP event stream name
        if (nodeName.equals("CepEvent")) {
            //we do not support nested event mapping sections for now!!
            if (evStarted) {
                log.error("*** ERROR **** Nested <CepEvent> sections in event mapper xml... not supported!!");
                return false;
            } else {
                cepEvent = new GangliaCepEvent();
                if (cepEvent.initialize( hm.get("containerNode"), hm.get("evName"))) {
                    evStarted = true;
                    return true;
                } else {
                    log.error("*** ERROR **** Format error in <CepEvent> sections in event mapper xml");
                    return false;
                }
            }
        }

        //Look for <Parameter> nodes - this is valid only within <event> nodes
        //Add the parameter attributes to the event
        if (nodeName.equals("Parameter")) {
            if (!evStarted) {
                log.error("*** ERROR **** <Parameter> sections not nested within <event> sections in event mapper xml... not supported!!");
                return false;
            } else {
                if (cepEvent.setEventParameter(hm.get("evField"),hm.get("evType"),hm.get("gNode"),hm.get("gName"),hm.get("gVal"))) {
                    return true;
                } else {
                    log.error("*** ERROR **** Format error in <Parameter> definition sections in event mapper xml");
                    return false;
                }
            }
        }

        //we do not care about the other sections of the event mapper for now!!
        return true;
    }

    public static boolean nodeEnd(String nodeName) {
        log.debug("nodeEnd:: nodeName: " + nodeName);

        //if <event> node has ended finalise the GangliaCep event as completed.
        //we are now on the look out for the next <event> defintion
        if (nodeName.equals("CepEvent")) {
            if (evStarted) {
                cepEvent.setDefined();
                evStarted = false;
                if (log.isDebugEnabled()) {
                    //pretty print the defined event for debugging
                    cepEvent.prettyPrintDefinition();
                }
                return true;
            }
            else {
                log.error("*** ERROR **** Improperly nested <CepEvent> definition sections in event mapper xml... not supported!!");
                return false;
            }
        }
        return true;
    }
}
