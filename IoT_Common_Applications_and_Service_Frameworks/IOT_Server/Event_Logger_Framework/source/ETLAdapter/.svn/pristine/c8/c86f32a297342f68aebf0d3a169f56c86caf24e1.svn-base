package com.prokosha.adapter.etl.ETLAdapter;
import java.io.FileReader;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rekhas
 */
public class ETLProperties {

    private static final Logger log = Logger.getLogger(ETLProperties.class.getName());
    private static int ETLPort;
    private static String DriverName;
    private static String UrlPath;
    private static String UserID;
    private static String Password;
    private static String customerDBConfigFileFormat;
    private static String ResourceMapping;

    public static boolean initialize() {
        String home = System.getProperty("user.home");
        String szFile_Seperator = System.getProperty("file.separator");
        String propertyFilePath = home + szFile_Seperator + "ETLConfig" + szFile_Seperator + "reportadapter.properties";
        Properties pro = new Properties();
        try {
            FileReader fr = new FileReader(propertyFilePath);
            pro.load(fr);
            ETLPort = Integer.parseInt(pro.getProperty("ETLPort"));
            DriverName = pro.getProperty("DriverName");
            UrlPath = pro.getProperty("UrlPath");
            UserID = pro.getProperty("UserID");
            Password = pro.getProperty("Password");
            customerDBConfigFileFormat = pro.getProperty("customerDBConfigFileFormat");
            ResourceMapping = pro.getProperty("ResourceMapping"); 
            try{
                fr.close();
                fr = null;
            }catch(Exception e){
                e.printStackTrace();
            }
            return true;
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ETLProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static int getETLPort() {
        return ETLPort;
    }

    public static String getDriverName() {
        return DriverName;
    }

    public static String getUrlPath() {
        return UrlPath;
    }

    public static String getUserID() {
        return UserID;
    }

    public static String getPassword() {
        return Password;
    }

    public static String getCustomerDBConfigFileFormat() {
        return customerDBConfigFileFormat;
    }

    public static String getResourceMapping() {
        return ResourceMapping;
    }
}
