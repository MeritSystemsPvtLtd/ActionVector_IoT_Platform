/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.modbusadapter;

import com.prokosha.modbusadapter.configuration.AdapterProperties;
import org.apache.log4j.Logger;

/**
 *
 * @author rekhas
 */
public class ModBusError {

    private static Logger logger = Logger.getLogger(ModBusError.class.getName());
    private static final String installation = AdapterProperties.getInstallationID();
    private static final String customerID = AdapterProperties.getCustomerID();
    private static final String cCustomerID = AdapterProperties.getcCustomerID();

    public static boolean sendErrorEvent(String error, String cause) {
        String szErrorEvt = null;
        try {
            szErrorEvt = "stream=ModBusErrorEvent";
            szErrorEvt += ",CustomerID=" + customerID;
            szErrorEvt += ",HostName=ModBusAdapter-"+installation;
            szErrorEvt += ",HostAddress=ModBusAdapter-"+installation;
            szErrorEvt += ",resourceId=ModBusAdapter-"+installation;
            szErrorEvt += ",Error=" + error;
            szErrorEvt += ",Cause=" + cause;
            szErrorEvt += ",resourceType=ModBusAdapter";
            szErrorEvt += ",resourceSubType=ModBusAdapter";
            szErrorEvt += ",cCustomer=" + AdapterProperties.getcCustomerID();
            
            ModBusAdapter.FrontControllerSSLClient.sendMessage(szErrorEvt);
            szErrorEvt = null;
            return true;
        } catch (Exception e) {
            logger.error("Error while sending error event to CEP==>>" + e.toString());
            e.printStackTrace();
        } finally {
                szErrorEvt = null;
        }
        return false;
    }
}
