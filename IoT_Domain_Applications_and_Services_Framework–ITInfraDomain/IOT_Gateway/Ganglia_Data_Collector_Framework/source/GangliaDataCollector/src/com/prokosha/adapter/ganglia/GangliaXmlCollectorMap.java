/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.adapter.ganglia;

import java.util.HashMap;
import com.prokosha.ganglia.datacollector.GangliaProtocolHandler;

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
public class GangliaXmlCollectorMap {

    String aRgmondHost[];
    String aRgmondPort[];

    HashMap<String, GangliaProtocolHandler> gcollectorMap = null;

    public GangliaXmlCollectorMap(String aRgmondHost[], String aRgmondPort[]) {
        this.aRgmondHost = aRgmondHost;
        this.aRgmondPort = aRgmondPort;
        gcollectorMap = new HashMap();
        int len = aRgmondHost.length;
        GangliaProtocolHandler gcollector = null;
        String host;
        String port;
        int iport=0;
        for (int i = 0; i < len; i++) {
            try{
                host = aRgmondHost[i].trim();
                port = aRgmondPort[i].trim();
                iport = Integer.parseInt(port);
                gcollector = new GangliaProtocolHandler(host, iport);
                gcollectorMap.put(host+":"+port, gcollector);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public HashMap<String, GangliaProtocolHandler> getListGangliaXmlCollector() {
        return gcollectorMap;
    }
}
