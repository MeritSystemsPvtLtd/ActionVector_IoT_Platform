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
**********************************************************************
 */
package com.merit.dashboard.DBUtil;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * ************************************************************************************************
 * This class for getting different properties for establishing database
 * connection with different databases
 * ************************************************************************************************
	 *
 */
public class DBUtilHelper {

    static Logger log = Logger.getLogger(DBUtilHelper.class);
    private static Properties properties = null;
    private static Properties metrics_mapping_properties = null;
    private static boolean flag = false;
    String home = System.getProperty("user.home");
    String sz_fileseparator = System.getProperty("file.separator");
    //private String home = "/opt/sadesk";
    private String configPath = home;
    private String szPath = configPath + sz_fileseparator + "Masterconfig" + sz_fileseparator + "TaskSummary.properties";
    private String sz_metrics_mapping_path = configPath + sz_fileseparator + "Masterconfig" + sz_fileseparator + "Metrics_mapping.properties";

    public DBUtilHelper() {
        System.out.println("sz_metrics_mapping_path" + sz_metrics_mapping_path);
        if (!flag) {
            try {
            	FileInputStream fstream= new FileInputStream(szPath);
                properties = new Properties();
                properties.load(fstream);
                fstream.close();
                fstream=null;
                fstream=new FileInputStream(sz_metrics_mapping_path);
                metrics_mapping_properties = new Properties();
                metrics_mapping_properties.load(fstream);
                fstream.close();
                fstream=null;
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
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

    public static Properties getMetrics_mapping_properties() {
        return metrics_mapping_properties;
    }
}
