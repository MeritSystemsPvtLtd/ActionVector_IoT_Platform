/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.merit.dashboard.util;

import com.merit.dashboard.DBUtil.DBUtilHelper;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author rekhas
 */
public class PerformanceJsonsConfig {
    
    static Logger log = Logger.getLogger(PerformanceJsonsConfig.class);
    private String resourceType;
    private String metricType;
    private String configuredMetricType;
    private String jsonGenerator;
    
    public PerformanceJsonsConfig(String resourceType, String metricType, String derivedMetricType, String jsonGenerator) {
        this.resourceType = resourceType;
        this.metricType = metricType;
        this.configuredMetricType = derivedMetricType;
        this.jsonGenerator = jsonGenerator;
    }
    
    public static HashMap initialize() {
        FileInputStream finptStrm = null;
        Properties jsonProp = new Properties();
        String resType;
        String metType;
        String derMetType;
        String entryKey;
        String jsonGen;
        HashMap<String, PerformanceJsonsConfig> confMap = new HashMap();
        try {
            String szPropFile = DBUtilHelper.getPerformanceJsonConfigFile();
            log.info("PerformanceJsonsConfig initialize()");
            finptStrm = new FileInputStream(szPropFile);
            jsonProp.load(finptStrm);
            Iterator entryItr = jsonProp.keySet().iterator();
            log.info("No of performance jsons configured==>>" + jsonProp.keySet().size());
            String[] entryValue;//
            while (entryItr.hasNext()) {
                entryKey = entryItr.next().toString();
                entryValue = jsonProp.getProperty(entryKey).split(",");
                resType = entryValue[0];
                metType = entryValue[1];
                derMetType = entryValue[2];
                jsonGen = entryValue[3];
                log.info("Entry key==>>" + entryKey);
                PerformanceJsonsConfig szConfig = new PerformanceJsonsConfig(resType, metType, derMetType, jsonGen);
                confMap.put(entryKey, szConfig);
            }
            return confMap;
        } catch (Exception e) {
            log.error("Error in initialize()==>>" + e.toString());
            e.printStackTrace();
        } finally {
            try {
                if (finptStrm != null) {
                    finptStrm.close();
                }
                finptStrm = null;
                jsonProp = null;
            } catch (Exception e) {
                log.error("Error in finally==>>" + e.toString());
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public String getResourceType() {
        return resourceType;
    }
    
    public String getMetricType() {
        return metricType;
    }
    
    public String getJsonGenerator(String reqResType) {
        if (reqResType.equals(this.resourceType) || reqResType.equals("")) {
            return jsonGenerator;
        } else {
            return null;
        }
    }
    
    public String getConfiguredMetricType() {
        return configuredMetricType;
    }
}
