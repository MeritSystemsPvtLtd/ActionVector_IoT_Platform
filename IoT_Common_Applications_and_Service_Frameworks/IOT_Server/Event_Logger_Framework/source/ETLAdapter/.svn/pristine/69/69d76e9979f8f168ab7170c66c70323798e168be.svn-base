/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.adapter.etl.ETLAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import org.apache.log4j.Logger;

public class ReadOpsView {

    private static final Logger log = Logger.getLogger(ReadOpsView.class.getName());
    private static Connection con = null;
    private static Statement stmt = null;
    private static ResultSet rs = null;
    static boolean mapInitialized = false;
    Properties properties = null;
    static Enumeration enum1 = null;
    String metricValue = null;

    public ReadOpsView() {
    }

    public void initialize(String opsViewEvent, String customerID) {

        String strLine = null;
        String strLastCheckTime = null;
        String strHostState = null;
        String strLastHostState = null;
        String strHostgroupnames = null;
        String strHostName = null;
        String eventPipePath = null;

        try {
            log.debug("------IN READOPSVIEW--------");
            String line = opsViewEvent;
            if (line == null) {
                log.debug("File iS empty");
            } else {
                if (line.length() > 0) {

                    log.debug("Event Data:::::" + line);

                    strLine = line;

                    String[] st = strLine.toString().split("==");

                    strLastCheckTime = st[0];
                    String st1[] = strLastCheckTime.split("=");
                    String varName1 = st1[0];
                    String lastCheckTime = st1[1];

                    strHostState = st[1];
                    String st2[] = strHostState.split("=");
                    String hostState = st2[1];

                    strLastHostState = st[2];
                    String st5[] = strLastHostState.split("=");
                    String LastHostStateVal = st5[1];

                    strHostgroupnames = st[8];
                    String st3[] = strHostgroupnames.split("=");
                    String hostGroupNames = st3[1];

                    strHostName = st[9];
                    String st4[] = strHostName.split("=");
                    String hostName = st4[1];

                    SendToHostState(lastCheckTime, hostState, hostName, customerID);
                }
                //end of while
            }//end of inner if
        } catch (Exception e1) {
            log.error("Exception:" + e1);
            e1.printStackTrace();
        } finally {
            try {
                strLine = null;
                strLastCheckTime = null;
                strHostState = null;
                strLastHostState = null;
                strHostgroupnames = null;
                strHostName = null;
                eventPipePath = null;
            } catch (Exception e2) {
                log.error("Exception:" + e2.getMessage());
                e2.printStackTrace();
            }
        }
    }

    public void SendToHostState(String timestamp, String hostState, String hostName, String customerID) {
        log.debug("Inside SendToHostState()...");
        try {
            if (hostState.equalsIgnoreCase("UP")) {
                SendToUPDataBaseAvail(timestamp, hostState, hostName, customerID);
            } else if (hostState.equalsIgnoreCase("DOWN")) {
                SendToDownDataBaseAvail(timestamp, hostState, hostName, customerID);
            }
        } catch (Exception e) {
            log.error("Exception:" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
            } catch (Exception e2) {
                log.error("Exception:" + e2.getMessage());
                e2.printStackTrace();
            }
        }

    }

    public void SendToUPDataBaseAvail(String upTimestamp, String hostState, String hostName, String customerID) {

        log.debug("Inside SendToDataBaseAvail()...");
        String service2 = null;
        String subService2 = null;
        String hostGroup2 = null;
        String downTimestamp = null;
        long downtime = 0;
        Timestamp downDate = new Timestamp(new Date().getTime());

        try {

            con = ConnectionDAO.getConnection(customerID);
            stmt = con.createStatement();

            rs = stmt.executeQuery("Select service,subservice,hostgroup from hostinfo where lower(host)='" + hostName.toLowerCase() + "'");
            log.debug("Service &subservice Query" + "Select service,subservice,hostgroup from hostinfo where lower(host)='" + hostName.toLowerCase() + "'");

            while (rs.next()) {
                service2 = rs.getString("service");
                subService2 = rs.getString("subservice");
                hostGroup2 = rs.getString("hostgroup");
            }
            rs.close();
            log.debug("HostInfo: " + hostGroup2 + " " + service2 + " " + subService2);


            rs = stmt.executeQuery("Select timestamp1 from servicemetrics where host='" + hostName + "' and service='" + service2 + "' and subservice='" + subService2 + "' and timestamp2 IS NULL and metricvalue IS NULL" + " and timestamp1<" + upTimestamp + " " + " order by timestamp1");
            log.debug("UP Event Query==" + "Select timestamp1 from servicemetrics where host='" + hostName + "' and service='" + service2 + "' and subservice='" + subService2 + "' and timestamp2 IS NULL and metricvalue IS NULL" + " and timestamp1<" + upTimestamp + " " + " order by timestamp1");

            if (rs.last()) {
                downTimestamp = rs.getString("timestamp1");
                downtime = Long.parseLong(upTimestamp) - Long.parseLong(downTimestamp);
                log.debug("Downtime===" + downtime);
                int res = stmt.executeUpdate("update servicemetrics set category='availability', timestamp2='" + upTimestamp + "', metricvalue='" + downtime + "' where host='" + hostName + "' and service='" + service2 + "' and subservice='" + subService2 + "' and timestamp1=" + downTimestamp);

                log.debug("update servicemetrics set category='availability', timestamp2='" + upTimestamp + "', metricvalue='" + downtime + "' where host='" + hostName + "' and service='" + service2 + "' and subservice='" + subService2 + "' and timestamp1=" + downTimestamp);
            } else {
                rs = stmt.executeQuery("Select count(*) from servicemetrics where host='" + hostName + "' and timestamp2=" + upTimestamp + " and metrictype='downtime'");

                if (rs.next()) {
                    log.debug("count" + rs.getInt(1));

                    if (rs.getInt(1) == 0) {
                        log.debug("No Record Exits for the Host" + hostName);
                        int insVal = stmt.executeUpdate("Insert into  servicemetrics(host,hostgroup,service,subservice,timestamp2,category,metrictype) values('" + hostName + "','" + hostGroup2 + "','" + service2 + "','" + subService2 + "'," + Long.parseLong(upTimestamp) + ",'availabilityME'" + ",'downtime'" + ")");
                        log.debug("Inserted UPTime Availability: " + insVal);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Exception:" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {

                if (rs != null) {
                    rs.close();
                }

                /*if (con != null) {
                con.close();
                con = null;
                // log.debug("Connection in Opsview"+con);
                }*/
                service2 = null;
                subService2 = null;
                hostGroup2 = null;
                downTimestamp = null;
            } catch (SQLException se) {
                log.error("Exception:" + se.getMessage());
                se.printStackTrace();
            }
        }
    }

    public void SendToDownDataBaseAvail(String downtimeStamp, String hostState, String hostName, String customerID) {
        log.debug("Inside SendToDownDataBaseAvail()...");
        String service2 = null;
        String subService2 = null;
        String hostGroup2 = null;



        Timestamp downDate = new Timestamp(new Date().getTime());


        try {

            con = ConnectionDAO.getConnection(customerID);
            stmt = con.createStatement();

            rs = stmt.executeQuery("Select service,subservice,hostgroup from hostinfo where lower(host)='" + hostName.toLowerCase() + "'");
            log.debug("Service &subservice Query" + "Select service,subservice,hostgroup from hostinfo where lower(host)='" + hostName.toLowerCase() + "'");
            while (rs.next()) {
                service2 = rs.getString("service");
                subService2 = rs.getString("subservice");
                hostGroup2 = rs.getString("hostgroup");
            }
            rs.close();
            log.debug("HostInfo: " + hostGroup2 + " " + service2 + " " + subService2);

            rs = stmt.executeQuery("Select count(*) from servicemetrics where host='" + hostName + "' and timestamp1=" + downtimeStamp + " and metrictype='downtime'");

            if (rs.next()) {
                log.debug("count" + rs.getInt(1));
                if (rs.getInt(1) == 0) {

                    int insVal = stmt.executeUpdate("Insert into  servicemetrics(host,hostgroup,service,subservice,timestamp1,category,metrictype) values('" + hostName + "','" + hostGroup2 + "','" + service2 + "','" + subService2 + "'," + Long.parseLong(downtimeStamp) + ",'availabilityME'" + ",'downtime'" + ")");
                    log.debug("Down Query====" + "Insert into  servicemetrics(host,hostgroup,service,subservice,timestamp1,category,metrictype) values('" + hostName + "','" + hostGroup2 + "','" + service2 + "','" + subService2 + "'," + downtimeStamp + ",'availabilityME'" + ",'downtime'" + ")");
                    log.debug("Inserted DownTime Availability: " + insVal);
                    stmt.close();

                } else {
                    log.debug("Duplicate host with same downtimestamp found inside availability.");
                }

            }
            if (rs == null) {
                log.debug("availability has no record.");
            }

            rs.close();

        } catch (Exception e) {
            log.error("Exception:" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {

                    rs.close();
                }
                /*if (con != null) {
                con.close();
                con = null;
                // log.debug("Connection in Opsview"+con);
                }*/
                service2 = null;
                subService2 = null;
                hostGroup2 = null;

            } catch (SQLException se) {
                log.error("Exception:" + se.getMessage());
                se.printStackTrace();
            }
        }
    }
}
