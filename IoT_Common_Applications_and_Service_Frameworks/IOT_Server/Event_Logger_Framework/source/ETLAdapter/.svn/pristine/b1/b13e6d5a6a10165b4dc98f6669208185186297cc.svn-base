/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.JIDFRD64GRT
 */
package com.prokosha.adapter.etl.ETLAdapter;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author Rekha
 */
public class PerfMsgToClass {

    private static final Logger log = Logger.getLogger(PerfMsgToClass.class.getName());
    private static Set mappings;
    private static boolean initialized = false;

    public PerfMsgToClass() {
    }

    public static boolean initialize() {
        try {
             String home="/opt/sadesk";
            log.info("HOME DIRECTORY=="+home);
            log.debug("Building messageToClass mapping");
            Properties props = new Properties();
            log.debug("Loading properties from file: messageToClass.properties");
            props.load(new FileInputStream(home+"/"+"ETLConfig/MessageToClass.properties"));
            mappings = props.entrySet();
            initialized=true;
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public static String getClassForMsg(String msg) {

        try {
            if (!initialized) {

            initialize();
            }
            Iterator mapIter = mappings.iterator();
            while (mapIter.hasNext()) {
                Entry entrymap = (Entry) mapIter.next();
                if (msg.contains((String) entrymap.getKey())) {
                    String clsName = (String) entrymap.getValue();
                    return clsName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {

        String samples[] = {
            "304", "101", "999", "108", "106", "110", "151", "301", "304", "/", "/swap"
        };

        PerfMsgToClass.initialize();
        for (int i = 0; i < samples.length; i++) {
            log.info("----- Test Case " + i + " ----------");
            log.info("Input Msg Type: " + samples[i]);
            log.info("Msg Handler: " + PerfMsgToClass.getClassForMsg(samples[i]));
            log.info("----- End Test Case ----------\n");
        }
    }

}
