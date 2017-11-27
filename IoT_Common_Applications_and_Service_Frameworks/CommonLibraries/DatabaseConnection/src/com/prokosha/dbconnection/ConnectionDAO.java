package com.prokosha.dbconnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

/**
 *
 * @author vijayasri
 */
public class ConnectionDAO {

    private static ConnectionMap conmap = null;
    private static boolean initialized = false;
    private static final Logger log = Logger.getLogger(ConnectionDAO.class.getName());
    private static boolean initCalled=false;
    private static Statement stat = null;
    private static ResultSet rs = null;
    private static Connection uniqueconnection = null;
    private static int trial = 0;
    private static int rs1 = 0;
    /*private static Connection con = null;
     static Properties properties = null;
     static FileInputStream fis = null;
     static String driverName = null;
     static String urlPath = null;
     static String userID = null;
     static String pwd = null;*/
    
    public static void initialize(String moduleName){
        initCalled=true;
    conmap=new ConnectionMap(moduleName);
    
    }
    public static Connection getConnection(String CustomerID) throws IOException, SQLException {

        try {
            if(!initCalled){
            initialize("General");
            }
            if (!initialized) {
                Connection con = conmap.getConntionWithID(CustomerID);
                if (con == null) {
                    log.error("DB connection is null");
                    return null;
                }
                con.commit();
                return con;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Statement getConnectionStatement(String CustomerID) throws IOException, SQLException {

        try {
            if(!initCalled){
            initialize("General");
            }
            if (!initialized) {
                return conmap.getConntionStatementWithID(CustomerID);

            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in getConnectionStatement" + e.toString()); //newly added
        }
        return null;
    }

    public static synchronized ResultSet executerQuery(String strQuery, String customerID) {
        if (trial < 3) {
            try {
                uniqueconnection = getConnection(customerID);
                stat = uniqueconnection.createStatement();
                rs = stat.executeQuery(strQuery);
                log.debug(" in trial : " + trial + " Got success while executing the query ..");
                //System.out.println(" in trial : " + trial + " Got success while executing the query [" + strQuery + "]");
                trial = 0;
                return rs;
            } catch (Exception ex) {
                ex.printStackTrace();
                log.debug(" in trial : " + trial + " Error Occured while executing the query ..");
                //System.out.println(" in trial : " + trial + " Error Occured while executing the query [" + strQuery + "]");
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.toString()); //newly added
                }
                trial++;
                conmap.updateMapforConntionWithID(customerID, null);
                return executerQuery(strQuery, customerID);
            }
        }
        return null;
    }
    
    public static synchronized int inserterUpdate(String strQuery, String customerID){
        if (trial < 3) {
            try {
                uniqueconnection = getConnection(customerID);
                stat = uniqueconnection.createStatement();
                rs1 = stat.executeUpdate(strQuery);
                log.info(" in trial : " + trial + " Got success while executing the query ..");
                //log.info(" in trial : " + trial + " Got success while executing the query [" + strQuery + "]");
                trial = 0;
                return rs1;
            } catch (Exception ex) {
                ex.printStackTrace();
                log.info(" in trial : " + trial + " Error Occured while executing the query ..");
                //log.info(" in trial : " + trial + " Error Occured while executing the query [" + strQuery + "]");
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.toString()); //newly added
                }
                trial++;
                conmap.updateMapforConntionWithID(customerID, null);
                return inserterUpdate(strQuery, customerID);
            }
        }
        return rs1;
    }
    public static void closeConnection(String CustomerID) {
        try {
            System.out.println("Im calling ConnectionMap.CloseConeectionWithID");
            conmap.CloseConeectionWithID(CustomerID);
            System.out.println("Exited from ConnectionMap.CloseConeectionWithID");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeStatement() {
        try {
            if (stat != null) {
                stat.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Error in closeStatement" + ex.toString()); //newly added
        }
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Error in closeStatement" + ex.toString()); //newly added
        } finally {
            stat = null;
            rs = null;
        }
    }
}
