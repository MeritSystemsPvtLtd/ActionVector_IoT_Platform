/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.timelineconsumedgeneratorforgiventsconfiguration;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;
/**
 *
 * @author yedukondalu
 */
public class TimeLineConsumedGeneratorConfiguration {
    
    
    private static Logger logger = Logger.getLogger(TimeLineConsumedGeneratorConfiguration.class.getName());
    private static String metricType = null;
    private static String customerId = null;
    private static String period = null;
    
    public static boolean loadProperties(String fileName) throws IOException {
        String temp;
        System.out.println("in loadproperties method");
         try{
        Properties props = new Properties();
        //loading File Stream
        FileInputStream fps = new FileInputStream(fileName);
        System.out.println("fps obj" + fps);
        props.load(fps);
        fps.close();


        //metricType is reading from property file
        temp = props.getProperty("metricType");
        if (temp != null) {
            metricType = temp;
            System.out.println("tempnot null :" + metricType);
            
        }else{
            System.out.println("tempnull :" + metricType);
            return false;
        }
        
               //cid is reading from property file
        temp = props.getProperty("customerId");
        if (temp != null) {
            customerId = temp;
            System.out.println("tempnot null :" + customerId);
        } else{
            System.out.println("tempnull :" + customerId);
            return false;
        }
        
         //period is reading from property file
        temp = props.getProperty("period");
        if (temp != null) {
            period = temp;
            System.out.println("tempnot null :" + period);
        } else{
            System.out.println("tempnull :" + period);
            return false;
        }
        
        }catch(Exception e){
            e.printStackTrace();
            logger.error(e);
            }
        return true;
    }

    public static String getMetricType() {
        System.out.println("in get method metricType :" + metricType);
         return metricType;
        }
     public static String getCustomerId() {
        System.out.println("in get method customerId :" + customerId);
         return customerId;
        }
     public static String getPeriod() {
        System.out.println("in get method period :" + period);
         return period;
        }
}
