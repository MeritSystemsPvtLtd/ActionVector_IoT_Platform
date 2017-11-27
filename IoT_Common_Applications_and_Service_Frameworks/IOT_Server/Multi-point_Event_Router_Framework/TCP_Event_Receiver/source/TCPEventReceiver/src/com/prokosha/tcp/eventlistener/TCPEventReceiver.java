/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.prokosha.tcp.eventlistener;

import com.prokosha.eventreceivermapper.EventReceiverMapper;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
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

public class TCPEventReceiver {
    
    private static final Logger log = Logger.getLogger(TCPEventReceiver.class.getName());

    //public Properties pro = null;

    /**
     * EventReceiver Class Object 
     */
    public EventReceiver eventReceiver;
    
    /**
     * Set the min life span for inactive clients
     */
    private void setClientsLifeSpan() {
        String clientMinLife = EventReceiverMapper.pro.getProperty("ClientsMinLifeSpan");
        if (clientMinLife == null) {
            log.info("Warning : 'ClientsMinLifeSpan' has not been set.");
            log.info("Warning : working with default value(360) of 'ClientsMinLifeSpan'.");
            EchoClient.clientMinLife = 360;
        } else {
            try {
                long spanTime = Long.parseLong(clientMinLife.trim());
                if (spanTime < 360) {
                    log.info("Warning : 'ClientsMinLifeSpan' should be greater value 360.");
                    log.info("Warning : working with default value of 'ClientsMinLifeSpan'.");
                    EchoClient.clientMinLife = 360;
                } else {
                    EchoClient.clientMinLife = spanTime;
                }
                
            } catch (NumberFormatException numberFormatException) {
                log.info("Warning : 'ClientsMinLifeSpan' should be a numeric value greater value 360.");
                log.info("Warning : working with default value of 'ClientsMinLifeSpan'.");
                EchoClient.clientMinLife = 360;
            }
        }
    }

    /**
     * initialize Receiver class Object
     */
    public void initReceiver(){
        String receiverVal = EventReceiverMapper.pro.getProperty("FrontFaceAddressPort").trim();
        log.debug("receiverVal = " + receiverVal);
        eventReceiver = new EventReceiver(receiverVal);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            PropertyConfigurator.configure("logger.properties");
            TCPEventReceiver obj = new TCPEventReceiver();
            //obj.ini();
            EventReceiverMapper.initialize();
            obj.setClientsLifeSpan();
            obj.initReceiver();
            //obj.initProcessorObjectGroup();
            //obj.initAddressMap();
            obj.eventReceiver.startListener();
            //obj.closeProcessorGroup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
