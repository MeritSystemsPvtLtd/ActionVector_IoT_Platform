/**
 * *********************************************************************
 * Software Developed by Merit Systems Pvt. Ltd., No. 42/1, 55/c, Nandi Mansion,
 * 40th Cross, Jayanagar 8th Block Bangalore - 560 070, India Work Created for
 * Merit Systems Private Limited All rights reserved
 *
 * THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES
 * NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED, DISTRIBUTED,
 * REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED,
 * COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD
 * SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 * *********************************************************************
 */
package com.merit.dashboard.DBUtil;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Properties;
import main.ServiceThreadListener;
import org.apache.log4j.Logger;

/**
 * ************************************************************************************************
 * This class for getting different properties for establishing database
 * connection with different databases
 * ************************************************************************************************
 *
 */
public class DBUtilHelperWithLoop {

    static Logger log = Logger.getLogger(DBUtilHelperWithLoop.class);
    private static Properties properties = null;
    private static Properties metrics_mapping_properties = null;
    private static boolean flag = false;
    static String  home = System.getProperty("user.home");
    static String sz_fileseparator = System.getProperty("file.separator");
    private static String configPath = home;
    private String szPath = configPath + sz_fileseparator + "Masterconfig" + sz_fileseparator + "TaskSummary.properties";
    private String sz_metrics_mapping_path = configPath + sz_fileseparator + "Masterconfig" + sz_fileseparator + "Metrics_mapping.properties";
    private static String sz_perf_prop_path = configPath + sz_fileseparator + "Masterconfig" + sz_fileseparator + "PerformanceJsons.properties";
    public static String dashBoardJSONPATH  = null;
    public static String XMLResConfigURL = null;
    public static String MetricUOMURL = null;
    public static String MetricValueURL = null;
    public static String hourSleep  = "60";//in sec
    public static String daySleep   = "910";//in sec
    public static String weekSleep  = "18000";//in sec
    public static String monthSleep = "43200";//in sec
    public static String yearSleep  = "432000";//in sec
    public static String generateAllJSONForced = null;

    public static String getCatalinaHome(){
        String path = System.getProperty("catalina.base");
        System.out.println("System.getProperty(\"catalina.base\")==>>"+path);
        if(path!=null){
            if(!path.trim().equalsIgnoreCase("") && (new File(path)).exists()){
                return path.trim() + File.separator + "webapps" ; // + metrics_mapping_properties.getProperty("projectName");
            }
        }
        return null;
    }
    
    public DBUtilHelperWithLoop() {
        System.out.println("sz_metrics_mapping_path" + sz_metrics_mapping_path);
        if (!flag) {
            properties = new Properties();
            metrics_mapping_properties = new Properties();
            try {
                FileInputStream finptStrm1 = new FileInputStream(szPath);
                FileInputStream finptStrm2 = new FileInputStream(sz_metrics_mapping_path);
                File f ;//modified
                properties.load(finptStrm1);
                metrics_mapping_properties.load(finptStrm2);
                flag = true;
                String path = metrics_mapping_properties.getProperty("DashBoardJSONPATH");
                dashBoardJSONPATH  = getCatalinaHome();
                dashBoardJSONPATH = dashBoardJSONPATH.replaceAll(File.separator+File.separator, File.separator);
                if(path != null){
                    f = new File(path);//modified
                     if(!path.trim().equalsIgnoreCase("") && (f).exists())//modified
                        dashBoardJSONPATH = path;
                }
                XMLResConfigURL = metrics_mapping_properties.getProperty("GetXMLResConfigURL");
                MetricUOMURL = metrics_mapping_properties.getProperty("GetMetricUOMURL");
                MetricValueURL = metrics_mapping_properties.getProperty("GetMetricValueURL");
                
                //setRefreshTimeInterval();
                finptStrm1.close();
                finptStrm2.close();
                f = null;
                finptStrm1 = null;
                finptStrm2 = null;
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error in DBUtilHelper" + e.toString());
            }
        }
    }

    /**
     * ************************************************************************************************
     * This class for getting postgreSQL DataBase Connection
     *
     * @return con Connection Object
     * ************************************************************************************************
     *
     */
    public Connection getConnectionPostgres() {
        Connection con = null;
        try {

            Class.forName(properties.getProperty("driverName"));
            con = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("username"), properties.getProperty("password"));
            flag = true;
        } catch (Exception e) {
            log.error("IN DBUtilHelper   some ERROR is here :" + e.getMessage());
            e.printStackTrace();
        }
        return con;
    }

    /**
     * ************************************************************************************************
     * This class for getting MySQL DataBase Connection
     *
     * @return con Connection Object
     * ************************************************************************************************
     *
     */
    public Connection getConnectionMySql() {
        Connection con = null;
        try {
            Class.forName(properties.getProperty("MysqlDriverName"));
            con = DriverManager.getConnection(properties.getProperty("MysqlUrlPath"), properties.getProperty("MysqlUserID"), properties.getProperty("MysqlPassword"));
            flag = true;

        } catch (Exception e) {
            log.error("IN DBUtilHelper   some ERROR is here :" + e.getMessage());
            e.printStackTrace();
        }
        return con;
    }

    public static Properties getProperties() {
        return properties;
    }

    public static synchronized Properties getMetrics_mapping_properties() {
        return metrics_mapping_properties;
    }

    public void setRefreshTimeInterval() {
        String dashBoardReFreshSeconds = null;
        String exceptionMessage = "";
        try {
            dashBoardReFreshSeconds = metrics_mapping_properties.getProperty("DashBoardReFreshSeconds");
            generateAllJSONForced = metrics_mapping_properties.getProperty("generateAllJSONForced_inFirstExecution");
        } catch (Exception e) {
            exceptionMessage += "Warning : ";
            exceptionMessage += "\n\t'DashBoardReFreshSeconds' is not configured in file \"" + szPath + "\" ";
            exceptionMessage += "\n___using default value of sleep time interval";
            //System.out.println(exceptionMessage);
            log.error("Error in setRefreshTimeInterval" + e.toString());
            return;
        }
        try {
            dashBoardReFreshSeconds = dashBoardReFreshSeconds.replaceAll("['(']", "");
            dashBoardReFreshSeconds = dashBoardReFreshSeconds.replaceAll("[')']", "");
            dashBoardReFreshSeconds = dashBoardReFreshSeconds.replaceAll("sec", "");
            dashBoardReFreshSeconds = dashBoardReFreshSeconds.replaceAll("-", "");
            if (dashBoardReFreshSeconds.contains(")_(")) {
                throw (new Exception());
            }
        } catch (Exception e) {
            exceptionMessage += "Warning : ";
            exceptionMessage += "\n\t'DashBoardReFreshSeconds' is mulconfigured in file \"" + szPath + "\" ";
            exceptionMessage += "\n\t'DashBoardReFreshSeconds' should be configured in format (HOUR-VALUEsec)_(DAY-VALUEsec)_(WEEK-VALUEsec)_(MONTH-VALUEsec)_(YEAR-VALUEsec)";
            exceptionMessage += "\n\tfor any negative value 'DashBoardReFreshSeconds' should be configured in this format (HOUR-<neg>VALUEsec)_(DAY-<neg>VALUEsec)_(WEEK-<neg>VALUEsec)_(MONTH-<neg>VALUEsec)_(YEAR-<neg>VALUEsec)";
            exceptionMessage += "\n___ any negative value in 'DashBoardReFreshSeconds' refers that json file will not be generated for that timestamp";
            exceptionMessage += "\n___ continuing with default value of sleep time interval";
            //System.out.println(exceptionMessage);
            log.error("Error in setRefreshTimeInterval" + e.toString());
            return;
        }

         hourSleep = getVlueFromStrin(dashBoardReFreshSeconds, "HOUR");
          daySleep = getVlueFromStrin(dashBoardReFreshSeconds, "DAY");
         weekSleep = getVlueFromStrin(dashBoardReFreshSeconds, "WEEK");
        monthSleep = getVlueFromStrin(dashBoardReFreshSeconds, "MONTH");
         yearSleep = getVlueFromStrin(dashBoardReFreshSeconds, "YEAR");
    }
    
    private String getVlueFromStrin(String timeStrip, String timeStamp){
        boolean error = false;
        String timeVal = "";
        String exceptionMessage = "";
        String exceptionMessage1 = "";
        String exceptionMessage2 = "";
        exceptionMessage  += "Warning : ";
        exceptionMessage1 += "\n\t'" + timeStamp + "' is not configured in 'DashBoardReFreshSeconds' in file \"" + szPath + "\" ";
        exceptionMessage1 += "\n\t'DashBoardReFreshSeconds' should be configured in format (HOUR-VALUEsec)_(DAY-VALUEsec)_(WEEK-VALUEsec)_(MONTH-VALUE-sec)_(YEAR-VALUE-sec)";
        exceptionMessage2 += "\n___ continuing with default value of sleep time interval for '" + timeStamp + "'";
        if (timeStrip.contains(timeStamp)) {
            timeVal = timeStrip.split(timeStamp)[1].split("_")[0];
            try {
                System.out.println("timeVal = '" + timeVal + "'");
                System.out.println("timeVal.replace = '" + timeVal.replaceAll("<neg>", "-").replaceAll(" ", "") + "'");
                Long.parseLong(timeVal.replaceAll("<neg>", "-").replaceAll(" ", ""));
            } catch (Exception e) {
                exceptionMessage1 = "\n\t'" + timeStamp + "' is configured with non-numeric value in 'DashBoardReFreshSeconds' in file \"" + szPath + "\" ";
                exceptionMessage += exceptionMessage1 + exceptionMessage2;
                error = true;
                log.error("Error in getVlueFromStrin" + e.toString());
            }
        } else {
            exceptionMessage += exceptionMessage1 + exceptionMessage2;
            error = true;
        }
        if(error) {
            System.out.println(exceptionMessage);
            if (timeStamp.equals("HOUR")) {
                return hourSleep.replaceAll("<neg>", "-").replaceAll(" ", "");
            } else if (timeStamp.equals("DAY")) {
                return daySleep.replaceAll("<neg>", "-").replaceAll(" ", "");
            } else if (timeStamp.equals("WEEK")) {
                return weekSleep.replaceAll("<neg>", "-").replaceAll(" ", "");
            } else if (timeStamp.equals("MONTH")) {
                return monthSleep.replaceAll("<neg>", "-").replaceAll(" ", "");
            } else if (timeStamp.equals("YEAR")) {
                return yearSleep.replaceAll("<neg>", "-").replaceAll(" ", "");
            }
        }
        return timeVal.replaceAll("<neg>", "-").replaceAll(" ", "");
    }
    
    public static String getPerformanceJsonConfigFile(){
        return sz_perf_prop_path;
    }

}
