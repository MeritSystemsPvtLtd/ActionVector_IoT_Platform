/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.JIDFRD64GRT
 */
package com.prokosha.adapter.etl.ETLAdapter;

import com.prokosha.adapter.etl.Listener.Filelistener.CofigureFile;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author Rekha
 */
public class CEPEventMetricsMapping {

    private static final Logger log = Logger.getLogger(CEPEventMetricsMapping.class.getName());
    private static Set mappings;
    private static boolean initialized = false;
    CofigureFile ex=null;
    String szFile_Seperator=System.getProperty("file.separator");
    String home=System.getProperty("user.home");
    String path=home+szFile_Seperator+"ETLConfig"+szFile_Seperator+"CEPEventMetricsMapping.properties";
    private static  Properties properties = null;
    private static String szdefaultConstants[]={"timestamp1","resourceSubType","resourceId","CustomerID","resourceType","IPAddress","TMAX","TN"
                                                 ,"CpuUser","CpuSystem","HostName","TimeStamp","Message","TimeGenerated","availability","hostipaddress"
                                                 ,"time","IPADDRESS","TIMESTAMP","cCustomer"};

    public CEPEventMetricsMapping() {
    }
        
    public static void  setProperty(Properties props) {
        properties=props;
        initialize();
    }

    public static  boolean initialize() {
        try {
            mappings = properties.entrySet();
            initialized = true;
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        } finally{
            //properties = null;
        }
    }

    public  String getMetricsForEvent(String msg) {
        try {
            if (!initialized) {
                ex = new CofigureFile(false,path,"CEPMETRICS");
                initialize();
            }

            Iterator mapIter = mappings.iterator();

            while (mapIter.hasNext()) {
                Entry entrymap = (Entry) mapIter.next();
                if (msg.equals((String) entrymap.getKey())) {
                    String clsName = (String) entrymap.getValue();
                    return clsName;
                }
            }
        } catch (Exception e) {
            log.error("Exception :"+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static String[] getSzdefaultConstants() {
        return szdefaultConstants;
    }
    
}
