/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.watchdog;

//import com.prokosha.dogwatchevent.CepEngineConnector;
import custmDBCon.ConnectionDAO;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 *
 * @author gopal
 */
public class SendWatchEvent {

    private static final Logger log = Logger.getLogger(SendWatchEvent.class.getName());
    protected static String szCepMsg = null;
    protected static boolean msgStatus = false;

    public static boolean sendWatchEvent(String szResourceId, String szResourceType, String szResourceName, String szInstanceId, String szInstancePort,
            Long szEphoctime, int iAvailability, String customerId) {

        String szQuery = "";
        Statement stmt;
        String resID, cCID, host;
        int insStatus;
        Connection connection = null;
        try {
            //connection = DatabaseConnection.getAVSAConnection(new HttpServletRequest());
            connection = ConnectionDAO.getConnection(customerId);
            stmt = connection.createStatement();
            if (szResourceType.equalsIgnoreCase("Server")) {
                resID = szInstanceId;
                cCID = szInstanceId;
                host = szInstanceId;
            } else {
                resID = szInstancePort;
                cCID = szResourceId;
                host = szResourceId;
            }
            szQuery = "Select * from servicemetrics where metrictype='" + szResourceName + "' and host='"
                    + host + "' and resourceId='" + resID + "' and customerid=(select id from customerinfo where customername='"
                    + cCID + "')";
            System.out.println("szQuery=" + szQuery);
            ResultSet rs1 = stmt.executeQuery(szQuery);
            System.out.println("checking records in database for agent -> " + szQuery);
            boolean downRecord = false;// no record for agent down is present in database
            if (rs1.next()) {
                downRecord = true;// means agent down is present
                System.out.println(szResourceName + " on " + host + " down record found");
            } else if (iAvailability == 0) {
                System.out.println(szResourceName + " on " + host + " down record not found, inserting event");
                szQuery = "Insert into  servicemetrics(host,timestamp1,category,metrictype,"
                        + "resourceType, resourceSubType,resourceId,customerid) values('" + host + "','" + szEphoctime
                        + "','MonitoringAgent'" + ",'" + szResourceName + "','" + szResourceType + "','"
                        + szResourceType + "','" + resID + "',(select id from customerinfo where customername='" + cCID + "'))";
                System.out.println("execute query::" + szQuery);
                insStatus = stmt.executeUpdate(szQuery);
                return Boolean.parseBoolean("" + insStatus);
            }
            if (downRecord && (iAvailability == 1)) {
                System.out.println(szResourceName + " on " + host + " down record found, deleting from DB");
                szQuery = "delete from  servicemetrics where host='" + host + "' and category='MonitoringAgent'" + " and metrictype='" + szResourceName
                        + "' and resourcetype='"
                        + szResourceType + "' and resourcesubtype='" + szResourceType + "' and resourceid='"
                        + resID + "' and customerid=(select id from customerinfo where customername='" + cCID + "')";
                System.out.println("execute query::" + szQuery);
                insStatus = stmt.executeUpdate(szQuery);
                return Boolean.parseBoolean("" + insStatus);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }
}
