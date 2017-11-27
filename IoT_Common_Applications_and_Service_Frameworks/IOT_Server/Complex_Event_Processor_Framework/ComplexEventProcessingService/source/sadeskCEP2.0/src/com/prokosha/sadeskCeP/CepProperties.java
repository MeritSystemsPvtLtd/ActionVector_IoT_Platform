/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.prokosha.sadeskCeP;

import org.apache.log4j.Logger;
import java.io.*;
import java.util.*;
/**
 *
 * @author Abraham
 */
public class CepProperties {

    private static Properties props;
    private static Boolean propsLoaded = false;
    
    private static final Logger log = Logger.getLogger(CepProperties.class.getName());

    public static Boolean configure(String fileName) throws IOException {
         props = new Properties();
         log.debug ("Loading properties from file: " + fileName);
         props.load(new FileInputStream(fileName));
         propsLoaded = true;
         log.debug ("Properties loaded successfully...");
         return true;
    }

    public static String getProperty(String key) {
        if (propsLoaded) {
            String val = props.getProperty(key);
            log.debug ("Properties value for <"+key+"> : " + val);
            return val;
        }
        else {
            log.error ("Attempt to get properties without loading the properties from file...");
            return null;
        }
    }

    public static void reset() {
         props = null;
         propsLoaded = false;
         log.debug ("Properties object reset...");
    }

}
