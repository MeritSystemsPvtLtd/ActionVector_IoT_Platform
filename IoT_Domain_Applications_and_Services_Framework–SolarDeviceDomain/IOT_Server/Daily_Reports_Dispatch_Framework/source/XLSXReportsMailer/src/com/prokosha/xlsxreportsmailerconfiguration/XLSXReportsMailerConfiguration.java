/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.xlsxreportsmailerconfiguration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author yedukondalu
 */
public class XLSXReportsMailerConfiguration {
    
    private static Logger logger = Logger.getLogger(XLSXReportsMailerConfiguration.class.getName());
    private static String smtpServer = null;
    private static String smtpUser = null;
    private static String smtpPassword = null;
    private static String xlsxFilesPath = null;
    private static String bodyPart = null;
    private static String subject = null;
    private static String receipentMailIDPath = null;
    private static String periods = null;
    private static String smtpPort = null;
    
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


        //smtpServer is reading from property file
        temp = props.getProperty("SMTPServer");
        if (temp != null) {
            smtpServer = temp;
            logger.info("smtpServer null :" + smtpServer);
            
        }else{
            logger.info("smtpServer :" + smtpServer);
            return false;
        }
        
               //smtpUser is reading from property file
        temp = props.getProperty("SMTPUser");
        if (temp != null) {
            smtpUser = temp;
            logger.info("smtpUser null :" + smtpUser);
        } else{
            logger.info("smtpUser :" + smtpUser);
            return false;
        }
        
               //smtpPassword is reading from property file
        temp = props.getProperty("SMTPPassword");
        if (temp != null) {
            smtpPassword = temp;
            logger.info("smtpPassword null :" + smtpPassword);
        } else{
            logger.info("smtpPassword :" + smtpPassword);
            return false;
        }
         //Periods is reading from property file
        temp = props.getProperty("periods");
        if (temp != null) {
            periods = temp;
            logger.info("periods not null :" + periods);
        } else {
            logger.info("periods null :" + periods);
            return false;
        }
       
         //xlsxFilesPath is reading from property file
        temp = props.getProperty("xlsxFilesPath");
        if (temp != null) {
            xlsxFilesPath = temp;
            logger.info("xlsxFilesPath null :" + xlsxFilesPath);
        } else{
            logger.info("xlsxFilesPath :" + xlsxFilesPath);
            return false;
        }
        
        
        //bodyPart is reading from property file
        temp = props.getProperty("bodyPart");
        if (temp != null) {
            bodyPart = temp;
            logger.info("bodyPart null :" + bodyPart);
            
        }else{
            logger.info("bodyPart :" + bodyPart);
            return false;
        }
        
               //cid is reading from property file
        temp = props.getProperty("subject");
        if (temp != null) {
            subject = temp;
            logger.info("subject null :" + subject);
        } else{
            logger.info("subject :" + subject);
            return false;
        }
        
               //receipentMailIDPath is reading from property file
        temp = props.getProperty("receipentMailIdsPath");
        if (temp != null) {
            receipentMailIDPath = temp;
            logger.info("receipentMailIDPath null :" + receipentMailIDPath);
        } else{
            logger.info("receipentMailIDPath :" + receipentMailIDPath);
            return false;
        }
        
         //smtpPort is reading from property file
        temp = props.getProperty("SMTPPort");
        if (temp != null) {
            smtpPort = temp;
            logger.info("smtpPort null :" + smtpPort);
        } else{
            logger.info("smtpPort :" + smtpPort);
            return false;
        }
        }catch(Exception e){
            e.printStackTrace();
            logger.error(e);
            }
        return true;
    }

    public static String getSmtpServer() {
        logger.info("in get method smtpServer :" + smtpServer);
         return smtpServer;
        }
     public static String getSmtpUser() {
        logger.info("in get method smtpUser :" + smtpUser);
         return smtpUser;
        }
     public static String getXlsxFilesPath() {
        logger.info("in get method xlsxFilesPath :" + xlsxFilesPath);
         return xlsxFilesPath;
        }
      
     public static String getSmtpPassword() {
        logger.info("in get method smtpPassword :" + smtpPassword);
         return smtpPassword;
        }
      public static String getBodyPart() {
        logger.info("in get method bodyPart :" + bodyPart);
         return bodyPart;
        }
     public static String getSubject() {
        logger.info("in get method subject :" + subject);
         return subject;
        }
     public static String getReceipentMailIDPath() {
        logger.info("in get method receipentMailIDPath :" + receipentMailIDPath);
         return receipentMailIDPath;
        }
      
     public static String getSmtpPort() {
        logger.info("in get method smtpPort :" + smtpPort);
         return smtpPort;
        }
      public static String getPeriods() {
        logger.info("in get method periods :" + periods);
         return periods;
        }
 }

