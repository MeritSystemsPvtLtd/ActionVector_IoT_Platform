/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.prokosha.sadeskCeP;

import com.espertech.esper.client.*;
import org.apache.log4j.Logger;

/**
 *
 * @author Abraham
 */
public class DefaultEventListener implements UpdateListener {
    static int count=1;
    private static final Logger log = Logger.getLogger(DefaultEventListener.class.getName());
    public void update(EventBean[] newData, EventBean[] oldData) {
        if (newData == null) return;
        log.info("Event received --> "+count++ +"-->" + newData[0].getUnderlying());
    }
}
