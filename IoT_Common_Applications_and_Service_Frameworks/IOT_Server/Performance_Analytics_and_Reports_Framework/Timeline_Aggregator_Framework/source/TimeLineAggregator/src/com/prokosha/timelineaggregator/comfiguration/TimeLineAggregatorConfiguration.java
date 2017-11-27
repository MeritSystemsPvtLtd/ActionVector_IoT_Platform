/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.timelineaggregator.comfiguration;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;
/**
 *
 * @author yedukondalu
 */
public class TimeLineAggregatorConfiguration {
  private static Logger logger = Logger.getLogger(TimeLineAggregatorConfiguration.class.getName());
    private static String metricType = null;
    private static String customerId = null;
    private static String JSONResourceConfig = null;
    private static String period = null;
    
    public static boolean loadProperties(String fileName) throws IOException {
        String temp;
        logger.info("in loadproperties method");
         try{
        Properties props = new Properties();
        //loading File Stream
        FileInputStream fps = new FileInputStream(fileName);
        logger.info("fps obj" + fps);
        props.load(fps);
        fps.close();


        //metricType is reading from property file
        temp = props.getProperty("metricType");
        if (temp != null) {
            metricType = temp;
            logger.info("metricType null :" + metricType);
            
        }else{
            logger.info("metricType :" + metricType);
            return false;
        }
        
               //cid is reading from property file
        temp = props.getProperty("customerId");
        if (temp != null) {
            customerId = temp;
            logger.info("customerId null :" + customerId);
        } else{
            logger.info("customerId :" + customerId);
            return false;
        }
        
               //JSONResourceConfig is reading from property file
        temp = props.getProperty("JSONResourceConfig");
        if (temp != null) {
            JSONResourceConfig = temp;
            logger.info("JSONResourceConfig null :" + JSONResourceConfig);
        } else{
            logger.info("JSONResourceConfig :" + JSONResourceConfig);
            return false;
        }
        
         //period is reading from property file
        temp = props.getProperty("period");
        if (temp != null) {
            period = temp;
            logger.info("period null :" + period);
        } else{
            logger.info("period :" + period);
            return false;
        }
        
        }catch(Exception e){
            e.printStackTrace();
            logger.error(e);
            }
        return true;
    }

    public static String getMetricType() {
        logger.info("in get method metricType :" + metricType);
         return metricType;
        }
     public static String getCustomerId() {
        logger.info("in get method customerId :" + customerId);
         return customerId;
        }
     public static String getPeriod() {
        logger.info("in get method period :" + period);
         return period;
        }
      
     public static String getJSONResourceConfig() {
        logger.info("in get method JSONResourceConfig :" + JSONResourceConfig);
         return JSONResourceConfig;
        }   
}
