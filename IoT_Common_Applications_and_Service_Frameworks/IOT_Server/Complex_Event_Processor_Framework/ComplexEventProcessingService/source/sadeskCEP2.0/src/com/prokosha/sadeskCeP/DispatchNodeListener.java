/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.prokosha.sadeskCeP;

import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import com.espertech.esper.client.*;
import org.apache.log4j.Logger;

/**
 * @author Abraham
 * Standard parameters for every event that is to be dispatched to SADesk are:
 * EventType = "Correlated Event"
 * EventDesc = some thing that described the event such as "Memcache Performance Degradation"
 * EventSymptoms = what are the symptoms of this condition - e.g: "Latency too high on memcached server"
 * The rest of the event information is contextual data to help the operator handle the event condition.
 */
public class DispatchNodeListener implements UpdateListener {

    private static final Logger log = Logger.getLogger(DispatchNodeListener.class.getName());
    private CepSadeskDispatcher evDispatcher; /** SADesk event dispatcher */

    public DispatchNodeListener() {
       evDispatcher = new CepSadeskDispatcher(); /** create the event dispatcher */
    }

    public void update(EventBean[] corrEvent, EventBean[] oldData) {
        try{
        if (corrEvent == null) return;

        for (int i=0; i<corrEvent.length; i++ ) {
            log.debug("[EventCount:" + (i+1) +"]\nEventDump==> " + corrEvent[i].getUnderlying());
            // pretty print the correlated event to the logs
            prettyprint(corrEvent[i]);
            evDispatcher.dispatchEvents((HashMap)corrEvent[i].getUnderlying()); /** dipatch the event */
        }
        }catch(Exception e){e.printStackTrace();}
    }

    private void prettyprint(EventBean corrEvent) {

        StringBuffer sbuf = new StringBuffer();

        HashMap ev = (HashMap)corrEvent.getUnderlying();
        Set keys = ev.keySet();
        Iterator index = keys.iterator();

        String key;
        while (index.hasNext()) {
            key = (String)index.next();
            sbuf.append("   ").append(key).append(" = ").append(ev.get(key)).append("   \n");
        }
        log.warn("\n\n-------- Correlated Event ---------\n"+sbuf+"\n-------------------------------------\n\n");
    }

}
