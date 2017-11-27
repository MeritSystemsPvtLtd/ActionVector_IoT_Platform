/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.modbusadapter.configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author naveen
 */
public class SensorMapping {

    public static HashMap channelMapping = null;
    private static final Logger log = Logger.getLogger(SensorMapping.class.getName());

    public static boolean loadProperties(String fileName) {
        Properties props = null;
        FileInputStream fps = null;

        try {
            //creating hashmap object
            channelMapping = new HashMap();
            log.debug("Loading properties file: " + fileName);
            props = new Properties();
            fps = new FileInputStream(fileName);
            props.load(fps);
            fps.close();
            String value = null;
            //reading property file keys and values and putting in hashmap
            for (Object key : props.keySet()) {
                value = props.getProperty((String) key);
                System.out.println(key + ": " + value);
                channelMapping.put(key, value);
            }
            System.out.println("hashmap;" + channelMapping);
            props.clear();
            props = null;
            fps = null;
            value = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (props != null) {
                    props.clear();
                }
                props = null;
                if (fps != null) {
                    fps.close();
                }
                fps = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;

    }

    public static HashMap getChannelMapping() {

        return channelMapping;
    }
}
