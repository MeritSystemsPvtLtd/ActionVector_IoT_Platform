/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.adapter.etl.ETLAdapter;


import com.prokosha.dbconnection.ConnectionDAO;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author Anand
 */
public class PersistReportData {
    
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    Logger log = Logger.getLogger(PersistReportData.class.getName());
    
    public int sendToDatabse(HashMap<String, ReportData> metricMap, String customerID) {
        
        int insertStatus = 0;
        
        
        try {
            if (metricMap.size() > 0) {
                con = ConnectionDAO.getConnection(customerID);
                stmt = con.createStatement();
                Set<String> keySet = metricMap.keySet();
                for (String key : keySet) {
                    System.out.println("---------------------Inserting Record---------------------------");
                    try {
                        String query = generateQuery(metricMap, metricMap.get(key));
                        System.out.println("query1111 " + query);
                        if (query != null) {
                            insertStatus = stmt.executeUpdate(query);
                            if (insertStatus != 0) {
                                System.out.println("--------------------- Record Insert Sucessfully---------------------------");
                                
                            } else {
                                log.debug("---Found Duplicate Recodrs Event Ignored----");
                            }
                        }
                        
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        if (!(ex.getMessage().contains("duplicate key value"))) {
                            System.out.println("Exception other than duplicate key, event need to be backed up to reprocess again later");
                            return -1;
                        }
                        if (ex.getMessage().contains("java.net.SocketException: Broken pipe")) {
                            System.out.println("SokectException :Broken pipe");
                            ConnectionDAO.closeConnection(customerID);
                        }
                        
                    } finally {
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Exception could not insert data to database :" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (rs != null) {
                    stmt.close();
                }
                /*if (con != null) {
                 //con.close();
                 //con = null;
                 //log.debug("Connection in ganglia"+con);
                 }*/
            } catch (Exception e) {
                log.error("Exception :" + e.getMessage());
                e.printStackTrace();
            }
        }
        return insertStatus;
    }
    
    public void sendLogwatchToDatabse(HashMap<String, ReportData> metricMap, String customerID) {
        String hostGroupName = null;
        String service1 = null;
        String subService1 = null;
        log.debug("HashMapLength" + metricMap.size());
        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date startdate1 = null;
        Date enddate1 = null;
        long startdate_milli = 0;
        long enddate_milli = 0;
        Set<String> set1 = null;
        
        try {
            if (metricMap.size() > 0) {
                con = ConnectionDAO.getConnection(customerID);
                stmt = con.createStatement();
                
                set1 = metricMap.keySet();
                Iterator iter = set1.iterator();
                
                for (int i = 0; i < set1.size(); i++) {
                    
                    ReportData rData = (ReportData) metricMap.get(iter.next());
                    log.debug("End Time===" + rData.getEndTime());
                    log.debug("StartTime" + rData.getStartTime());
                    rs = stmt.executeQuery("Select service,subservice,hostgroup from hostinfo where lower(host)=lower('"
                            + rData.getHost() + "') and customerid=" + rData.getCCustomerID());
                    startdate1 = format1.parse(rData.getStartTime());
                    startdate_milli = startdate1.getTime() / 1000;
                    enddate1 = format1.parse(rData.getEndTime());
                    enddate_milli = enddate1.getTime() / 1000;
                    
                    if (rs == null) {
                        log.debug("HostName does not exist in hostinfo:cpu");
                    }
                    
                    while (rs.next()) {
                        service1 = rs.getString("service");
                        subService1 = rs.getString("subservice");
                        hostGroupName = rs.getString("hostgroup");
                    }
                    
                    rs.close();
                    log.debug("From hostinfo of cpu: " + service1 + " " + subService1 + " " + hostGroupName);
                    
                    rs = stmt.executeQuery("Select count(*) from servicemetrics where host='" + rData.getHost() + "' and timestamp1="
                            + startdate_milli + " and metrictype='" + rData.getMetricType() + "' and customerid='" + rData.getCCustomerID() + "'");
                    log.debug("QueryCount==" + "Select count(*) from servicemetrics where host='" + rData.getHost() + "' and timestamp1="
                            + startdate_milli + " and metrictype='" + rData.getMetricType() + "' and customerid='" + rData.getCCustomerID() + "'");
                    
                    if (rs.next()) {
                        log.debug("Count===" + rs.getInt(1));
                        if (rs.getInt(1) == 0) {
                            if (Double.parseDouble(rData.getValue()) != -1.1) {
                                int insVal = stmt.executeUpdate("Insert into "
                                        + "servicemetrics"
                                        + "(host,hostgroup,service,subservice,timestamp1,timestamp2,"
                                        + "category,"
                                        + "metrictype,"
                                        + "rsourcetype,"
                                        + "rsourceId,"
                                        + "metricvalue,eventid,customerid) "
                                        + "values("
                                        + "'" + rData.getHost() + "',"
                                        + "'" + hostGroupName + "',"
                                        + "'" + service1 + "',"
                                        + "'" + subService1 + "',"
                                        + " " + startdate_milli + ","
                                        + " " + enddate_milli + ","
                                        + "'" + rData.getCategory() + "',"
                                        + "'" + rData.getMetricType() + "',"
                                        + "'" + rData.getResourceType() + "',"
                                        + "'" + rData.getResourceId() + "',"
                                        + " " + rData.getValue() + "',"
                                        + " " + rData.getEventID() + ""
                                        + " " + rData.getCCustomerID() + ""
                                        + ")");
                                log.debug("Inserted LogWatchData  Sucessfull: " + insVal);
                            } else {
                                log.debug("Record Not inserted mvalue is -1.1");
                            }
                        } else {
                            log.debug("Duplicate host with same timestamp found .");
                        }
                    }
                }
            } else {
                log.debug("******************HashMap Is Empty*****************");
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            if (e.getMessage().contains("java.net.SocketException: Broken pipe")) {
                System.out.println("SokectException :Broken pipe");
                System.out.println("calling ConDAO.closeConn()");
                ConnectionDAO.closeConnection(customerID);
                System.out.println("exited from ConDAO.closeCon");
            }
        } finally {
            try {
                
                if (rs != null) {
                    rs.close();
                }
                
                if (stmt != null) {
                    stmt.close();
                }

                /*if (con != null) {
                 //con.close();
                 //con = null;
                 //log.debug("Connection in ganglia"+con);
                 }*/
                
                hostGroupName = null;
                service1 = null;
                subService1 = null;
                startdate1 = null;
                enddate1 = null;
                format1 = null;
                set1 = null;
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void sendAvailToDatabse(HashMap<String, ReportData> metricMap, String customerID) {
        System.out.println("Inside SendToDataBaseAvail HashMapLength======" + metricMap.size());
        
        String service2 = null;
        String subService2 = null;
        String hostGroup2 = null;
        String downTimestamp = null;
        Long downtime = null;
        Set<String> set1 = null;
        
        try {
            set1 = metricMap.keySet();
            Iterator iter = set1.iterator();
            con = ConnectionDAO.getConnection(customerID);
            stmt = con.createStatement();
            
            for (int i = 0; i < set1.size(); i++) {
                try {
                    ReportData rData = (ReportData) metricMap.get(iter.next().toString());
                    ResultSet rs1 = stmt.executeQuery(
                            "Select "
                            + "* "
                            + "from "
                            + "servicemetrics "
                            + "where "
                            + "metrictype='downtime' and "
                            + "category='availabilityME' and "
                            + "host='" + rData.getHost() + "' and "
                            + "resourceId='" + rData.getResourceId() + "' and "
                            + "customerid='" + rData.getCCustomerID() + "' and "
                            + "timestamp1 !=1");//
                    System.out.println("checking no. of records in database with down availability  -> "
                            + "Select * from servicemetrics "
                            + "where "
                            + "metrictype='downtime' and "
                            + "category='availabilityME' and "
                            + "host='" + rData.getHost() + "' and "
                            + "customerid='" + rData.getCCustomerID() + "' and "
                            + "resourceId='" + rData.getResourceId() + "'");
                    boolean downReord = false;// no record for downtime is present in database
                    if (rs1.next()) {
                        downReord = true;// means down availability is present
                    }
                    if (rData.getValue().equalsIgnoreCase("DOWN") && !downReord) {
                        String szDeleteUpQuery = "delete from "
                                + "servicemetrics "
                                + "where "
                                + "host='" + rData.getHost() + "' and "
                                + "timestamp1=1 and "
                                + "customerid='" + rData.getCCustomerID() + "' and "
                                + "resourcetype='" + rData.getResourceType() + "'";
                        int i_delete_res = stmt.executeUpdate(szDeleteUpQuery);
                        if (i_delete_res > 0) {
                            System.out.println("SucessFully Deleted UpTIme Record");
                        } else {
                            System.out.println("UP Time Record Deleted Failed");
                        }
                        int insVal = stmt.executeUpdate(
                                "Insert into  "
                                + "servicemetrics "
                                + "( "
                                + "host,"
                                + "hostgroup,"
                                + "service,"
                                + "subservice,"
                                + "timestamp1,"
                                + "category,"
                                + "metrictype,"
                                + "resourceType,"
                                + "resourceSubType,"
                                + "resourceId,"
                                + "eventid,"
                                + "customerid"
                                + ") "
                                + "values ("
                                + "'" + rData.getHost() + "',"
                                + "'" + rData.getHostGroup() + "',"
                                + "'" + rData.getService() + "',"
                                + "'" + rData.getSubService() + "',"
                                + " " + rData.getTimestamp1() + ","
                                + "'" + rData.getCategory() + "ME'" + ","
                                + "'" + "downtime" + "',"
                                + "'" + rData.getResourceType() + "',"
                                + "'" + rData.getResourceSubType() + "',"
                                + "'" + rData.getResourceId() + "',"
                                + "'" + rData.getEventID() + "',"
                                + "'" + rData.getCCustomerID() + "'"
                                + ")");
                        stmt.close();
                        
                        System.out.println("Down Query====" + "Insert into  servicemetrics(host,hostgroup,service,subservice,timestamp1,"
                                + "category,metrictype,resourceType, resourceSubType,resourceId,eventid,customerid) values('"
                                + rData.getHost() + "','" + rData.getHostGroup() + "','" + rData.getService() + "','"
                                + rData.getSubService() + "'," + rData.getTimestamp1() + ",'" + rData.getCategory()
                                + "ME'" + ",'" + "downtime" + "','" + rData.getResourceType() + "','" + rData.getResourceSubType()
                                + "','" + rData.getResourceId() + "','" + rData.getEventID() + "','" + rData.getCCustomerID() + "')");
                        log.debug("Inserted DownTime Availability: " + insVal);
                        
                        if (insVal > 0) {
                            System.out.println("Inserted DownTime Availability: " + insVal);
                        } else {
                            System.out.println("Inserted DownTime Availability: " + insVal);
                            System.out.println("Duplicate host with same downtimestamp found inside availability.");
                        }
                        
                    } else if (rData.getValue().equalsIgnoreCase("UP")) {
                        log.debug("Updating the AvalabilityMe");
                        updateSLA(rData);
                        stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        // to select the first record from the objects having metrictype "availabilityME" and metricValue "DOWN"
                        rs1 = null;
                        rs1 = stmt.executeQuery(
                                "Select "
                                + "timestamp1 "
                                + "from "
                                + "servicemetrics "
                                + "where "
                                + "host='" + rData.getHost() + "' and "
                                + "service='" + rData.getService() + "' and "
                                + "subservice='" + rData.getSubService() + "' and "
                                + "customerid='" + rData.getCCustomerID() + "' and "
                                + "timestamp2 IS NULL and "
                                + "metricvalue IS NULL" + " and "
                                + "timestamp1<" + rData.getTimestamp1() + " " + " "
                                + "order by "
                                + "timestamp1");
                        System.out.println(
                                "UP Event Query=="
                                + "Select "
                                + "timestamp1 "
                                + "from "
                                + "servicemetrics "
                                + "where "
                                + "host='" + rData.getHost() + "' and "
                                + "service='" + rData.getService() + "' and "
                                + "subservice='" + rData.getSubService() + "' and "
                                + "customerid='" + rData.getCCustomerID() + "' and "
                                + "timestamp2 IS NULL and "
                                + "metricvalue IS NULL" + " and "
                                + "timestamp1<" + rData.getTimestamp1() + " " + " "
                                + "order by "
                                + "timestamp1");
                        if (rs1.last()) {
                            downTimestamp = rs1.getString("timestamp1");
                            downtime = Long.parseLong(rData.getTimestamp1()) - Long.parseLong(downTimestamp);
                            System.out.println("Downtime===" + downtime);
                            
                            int res = stmt.executeUpdate(
                                    "update "
                                    + "servicemetrics "
                                    + "set "
                                    + "category='availability', "
                                    + "timestamp2='" + rData.getTimestamp1() + "', "
                                    + "service='" + rData.getService() + "', "
                                    + "subservice='" + rData.getSubService() + "', "
                                    + "metricvalue='" + downtime + "',"
                                    + "SLA='" + rData.getSLAvalue() + "'  "
                                    + "where "
                                    + "host='" + rData.getHost() + "' and "
                                    + "service='" + rData.getService() + "' and "
                                    + "subservice='" + rData.getSubService() + "' and "
                                    + "customerid='" + rData.getCCustomerID() + "' and "
                                    + "timestamp1=" + downTimestamp + " and "
                                    + "metrictype='downtime'");
                            System.out.println(res + " records are updated with statement -> "
                                    + "update "
                                    + "servicemetrics "
                                    + "set "
                                    + "category='availability', "
                                    + "timestamp2='" + rData.getTimestamp1() + "', "
                                    + "service='" + rData.getService() + "', "
                                    + "subservice='" + rData.getSubService() + "', "
                                    + "metricvalue='" + downtime + "',"
                                    + "SLA='" + rData.getSLAvalue() + "'  "
                                    + "where "
                                    + "host='" + rData.getHost() + "' and "
                                    + "service='" + rData.getService() + "' and "
                                    + "subservice='" + rData.getSubService() + "' and "
                                    + "customerid='" + rData.getCCustomerID() + "' and "
                                    + "timestamp1=" + downTimestamp + " and "
                                    + "metrictype='downtime'");
                        } else {
                            
                            String szUptimeQuery = "Select "
                                    + "count(*) "
                                    + "from "
                                    + "servicemetrics "
                                    + "where "
                                    + "host='" + rData.getHost() + "' and "
                                    + "customerid='" + rData.getCCustomerID() + "' and "
                                    + "metrictype='downtime' and "
                                    + "timestamp1 IS NULL and "
                                    + "metricvalue IS NULL";
                            
                            ResultSet rs2 = stmt.executeQuery(szUptimeQuery);
                            if (rs2.next()) {
                                log.debug("count" + rs2.getInt(1));
                                if (rs2.getInt(1) == 0) {
                                    
                                    String sz_Checkfor_Duplicate_query = "select "
                                            + "count(*) "
                                            + "from "
                                            + "servicemetrics "
                                            + "where "
                                            + "host='" + rData.getHost() + "' and "
                                            + "customerid='" + rData.getCCustomerID() + "' and "
                                            + "timestamp1=1 and "
                                            + "resourcetype='" + rData.getResourceType() + "'";
                                    
                                    System.out.println("Duplicate Check For UpTIme Query==" + sz_Checkfor_Duplicate_query);
                                    ResultSet rs3 = stmt.executeQuery(sz_Checkfor_Duplicate_query);
                                    if (rs3.next()) {
                                        if (rs3.getInt(1) == 0) {
                                            log.debug("No Record Exits for the Host" + rData.getHost());
                                            String szUpTimeUpdateQuery = "Insert into  "
                                                    + "servicemetrics"
                                                    + "("
                                                    + "host,"
                                                    + "hostgroup,"
                                                    + "service,"
                                                    + "subservice,"
                                                    + "timestamp2,"
                                                    + "timestamp1,"
                                                    + "category,"
                                                    + "metrictype,"
                                                    + "resourcetype,"
                                                    + "resourceid,"
                                                    + "customerid"
                                                    + ") "
                                                    + "values("
                                                    + "'" + rData.getHost() + "',"
                                                    + "'" + hostGroup2 + "',"
                                                    + "'" + rData.getService() + "',"
                                                    + "'" + rData.getSubService() + "',"
                                                    + "" + Long.parseLong(rData.getTimestamp1()) + ","
                                                    + "1,"
                                                    + "'" + rData.getCategory() + "ME',"
                                                    + "'" + rData.getMetricType() + "',"
                                                    + "'" + rData.getResourceType() + "',"
                                                    + "'" + rData.getResourceId() + "',"
                                                    + "'" + rData.getCCustomerID() + "'"
                                                    + ")";
                                            System.out.println("Up Time Update Query=" + szUpTimeUpdateQuery);
                                            int insVal = stmt.executeUpdate(szUpTimeUpdateQuery);
                                            System.out.println("Inserted UPTime Availability: " + insVal);
                                            
                                        } else {
                                            System.out.println("Duplicate Uptime Record Found ");
                                        }
                                    }
                                } else {
                                    System.out.println("Up TIme Not Inserted Already Existed");
                                }
                            }
                            System.out.println("Record Not Inserted DownTimestamp Not Found");
                        }
                    }//else end here
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (ex.getMessage().contains("java.net.SocketException: Broken pipe")) {
                        System.out.println("SokectException :Broken pipe");
                        System.out.println("calling ConDAO.closeConn()");
                ConnectionDAO.closeConnection(customerID);
                System.out.println("exited from ConDAO.closeCon");
                    }
                } finally {
                    continue;
                }
                
            }
            set1 = null;
            iter = null;
            
        } catch (Exception e) {
            log.error("Exception:" + e.getMessage());
            e.printStackTrace();
            
        } finally {
            
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                /*if (con != null) {
                 //con.close();
                 //con = null;
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
    
    public void sendAvailToDatabse(HashMap<String, ReportData> metricMap, String customerID, int on) {
        System.out.println("Inside SendToDataBaseAvail HashMapLength======" + metricMap.size());
        
        String metrictype = null;
        ReportData rData = null;
        String category = "availability";
        Set<String> set1 = null;
        String strQuery = "";
        String resource = "";
        try {
            set1 = metricMap.keySet();
            Iterator iter = set1.iterator();
            con = ConnectionDAO.getConnection(customerID);
            stmt = con.createStatement();
            for (int i = 0; i < set1.size(); i++) {
                try {
                    rData = (ReportData) metricMap.get(iter.next().toString());
                    if (rData.getValue().equalsIgnoreCase("DOWN")) {
                        metrictype = "downtime";
                    } else if (rData.getValue().equalsIgnoreCase("UP")) {
                        metrictype = "uptime";
                    }//else end here
                    updateSLA(rData);
                    System.out.println(">>>>>>>>>>>>>>>>>>getSuperValue = " + rData.getSuper());
                    strQuery = "Insert into "
                            + "servicemetrics "
                            + "("
                            + "host, "
                            + "hostgroup, "
                            + "service, "
                            + "subservice, "
                            + "timestamp1, "
                            + "category, "
                            + "metrictype, "
                            + "SLA, "
                            + "resourceType, "
                            + "resourceSubType, "
                            + "resourceId, "
                            + "eventid, "
                            + "customerid"
                            + ") "
                            + "select "
                            + "'" + rData.getHost() + "' as host, "
                            + "'" + rData.getHostGroup() + "' as hostgroup, "
                            + "'" + rData.getService() + "' as service, "
                            + "'" + rData.getSubService() + "' as subservice, "
                            + " " + rData.getTimestamp1() + "  as timestamp1, "
                            + "'" + category + "' as category, "
                            + "'" + metrictype + "' as metrictype, "
                            + " " + rData.getSLAvalue() + "  as SLA, "
                            + "'" + rData.getResourceType() + "' as resourceType, "
                            + "'" + rData.getResourceSubType() + "' as resourceSubType, "
                            + "'" + rData.getResourceId() + "' as resourceId, "
                            + "'" + rData.getEventID() + "' as eventid, "
                            + "'" + rData.getCCustomerID() + "' as eventid, "
                            + "where "
                            + rData.getTimestamp1() + " > ( "
                            + "select case when (max(timestamp1) is null) then 0 else  max(timestamp1) end as timestamp1 "
                            + "from servicemetrics "
                            + "where "
                            + "host         ='" + rData.getHost() + "' and "
                            + "resourcetype ='" + rData.getResourceType() + "' and "
                            + "resourceId   ='" + rData.getResourceId() + "' and "
                            + "customerid         ='" + rData.getCCustomerID() + "' and "
                            + "metrictype in ('uptime', 'downtime') "
                            + ") "
                            + "and "
                            + "'" + metrictype + "' not in ( "
                            + "select case when (metrictype is null) then '$$' else  metrictype end as metrictype "
                            + "from servicemetrics "
                            + "where "
                            + "timestamp1 in ( "
                            + "select max(timestamp1) "
                            + "from servicemetrics "
                            + "where "
                            + "metrictype in ('uptime', 'downtime') and "
                            + "host         ='" + rData.getHost() + "' and "
                            + "resourcetype ='" + rData.getResourceType() + "' and "
                            + "customerid         ='" + rData.getCCustomerID() + "' and "
                            + "resourceId   ='" + rData.getResourceId() + "'"
                            + ") and "
                            + "host         ='" + rData.getHost() + "' and "
                            + "resourcetype ='" + rData.getResourceType() + "' and "
                            + "customerid         ='" + rData.getCCustomerID() + "' and "
                            + "resourceId   ='" + rData.getResourceId() + "'"
                            + ") ";
                    System.out.println("Executing query for '" + metrictype + "': >>strQuery = " + strQuery);
                    int insVal = stmt.executeUpdate(strQuery);
                    System.out.println("result for Executing query for '" + metrictype + "': >>strQuery = " + insVal);
                    resource = "Resource on "
                            + "Host            : '" + rData.getHost() + "', "
                            + "resourceType    : '" + rData.getResourceType() + "', "
                            + "resourceSubType : '" + rData.getResourceSubType() + "', "
                            + "customerid : '" + rData.getCCustomerID() + "', "
                            + "resourceID      : '" + rData.getResourceId() + "' ";
                    
                    if (insVal == 0) {
                        if (metrictype.equalsIgnoreCase("uptime")) {
                            System.out.println("the " + resource + " is still up. ");
                        } else if (metrictype.equalsIgnoreCase("uptime")) {
                            System.out.println("the " + resource + " is still down. ");
                        }
                    } else {
                        if (metrictype.equalsIgnoreCase("uptime")) {
                            System.out.println("the " + resource + " is got up. ");
                        } else if (metrictype.equalsIgnoreCase("uptime")) {
                            System.out.println("the " + resource + " is got down. ");
                        }
                    }
                    stmt.close();
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (ex.getMessage().contains("java.net.SocketException: Broken pipe")) {
                        System.out.println("SokectException :Broken pipe");
                        System.out.println("calling ConDAO.closeConn()");
                ConnectionDAO.closeConnection(customerID);
                System.out.println("exited from ConDAO.closeCon");
                    }
                } finally {
                    continue;
                }
                
            }
            set1 = null;
            iter = null;
        } catch (Exception e) {
            log.error("Exception:" + e.getMessage());
            if (e.getMessage().contains("java.net.SocketException: Broken pipe")) {
                System.out.println("SokectException :Broken pipe");
               System.out.println("calling ConDAO.closeConn()");
                ConnectionDAO.closeConnection(customerID);
                System.out.println("exited from ConDAO.closeCon");
            }
            e.printStackTrace();
        } finally {
            
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se) {
                log.error("Exception:" + se.getMessage());
                se.printStackTrace();
            }
        }
    }
    
    public String generateQuery(HashMap metricMap, ReportData rData) {
        //Insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue,resourceType, resourceSubType, resourceId,eventid) values('" + rData.getHost() + "','" + hostGroupName + "','" + service1 + "','" + subService1 + "'," +  startdate_milli+","+enddate_milli+ ","+"'"+rData.getCategory()+"'"+",'"+rData.getMetricType()+"',"+rData.getValue()+"',"+rData.getResourceType()+"',"+rData.getResourceSubType()+"',"+rData.getResourceId()+"',"+rData.getEventID()+")
        String szQuery = "insert into servicemetrics "
                + "(host,hostgroup,service,subservice,timestamp1,timestamp2,category,"
                + "metrictype,metricvalue,resourceType, resourceSubType, resourceId,eventID,SLA,customerid) values";
        int initialLen = szQuery.length();
        try {
            if (metricMap.size() > 0) {
                //Set<String> set1 = null;
                updateSLA(rData);
                if (rData.getValue().contains(":") || rData.getValue().contains("OK")) {
                    long timeInMillis = System.currentTimeMillis() / 1000;
                    szQuery = szQuery + "('" + rData.getHost() + "','" + rData.getHostGroup() + "','" + rData.getService() + "','"
                            + rData.getSubService() + "'," + rData.getTimestamp1() + ",'" + timeInMillis + "','" + rData.getCategory() + "'"
                            + ",'" + rData.getMetricType() + "','" + rData.getValue() + "','" + rData.getResourceType() + "','"
                            + rData.getResourceSubType() + "','" + rData.getResourceId() + "','" + rData.getEventID() + "','"
                            + rData.getSLAvalue() + "','" + rData.getCCustomerID() + "')";
                    // System.out.println("Query String==="+szQuery);
                } else if (Double.parseDouble(rData.getValue()) != -1.1) {
                    long timeInMillis = System.currentTimeMillis() / 1000;
                    szQuery = szQuery + "('" + rData.getHost() + "','" + rData.getHostGroup() + "','" + rData.getService() + "','"
                            + rData.getSubService() + "'," + rData.getTimestamp1() + ",'" + timeInMillis + "','" + rData.getCategory() + "'"
                            + ",'" + rData.getMetricType() + "','" + rData.getValue() + "','" + rData.getResourceType() + "','"
                            + rData.getResourceSubType() + "','" + rData.getResourceId() + "','" + rData.getEventID() + "','"
                            + rData.getSLAvalue() + "','" + rData.getCCustomerID() + "')";
                    // System.out.println("Query String==="+szQuery);
                } else {
                    log.error("Record not inserted mvalue is -1.1");
                }
                log.debug("Query====" + szQuery);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int lengthNow = szQuery.length();
        if (initialLen == lengthNow) {
            return null;
        }
        return szQuery;
    }
    
    public String generateQuery(HashMap metricMap) {
        //Insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue,resourceType, resourceSubType, resourceId,eventid) values('" + rData.getHost() + "','" + hostGroupName + "','" + service1 + "','" + subService1 + "'," +  startdate_milli+","+enddate_milli+ ","+"'"+rData.getCategory()+"'"+",'"+rData.getMetricType()+"',"+rData.getValue()+"',"+rData.getResourceType()+"',"+rData.getResourceSubType()+"',"+rData.getResourceId()+"',"+rData.getEventID()+")
        String szQuery = "insert ignore into servicemetrics (host,hostgroup,service,subservice,timestamp1,timestamp2,category"
                + ",metrictype,metricvalue,resourceType, resourceSubType, resourceId,eventID,SLA,customerid) values";
        int initialLen = szQuery.length();
        try {
            if (metricMap.size() > 0) {
                Set<String> set1 = null;
                set1 = metricMap.keySet();
                Iterator iter = set1.iterator();
                for (int i = 0; i < set1.size(); i++) {
                    ReportData rData = (ReportData) metricMap.get(iter.next());
                    updateSLA(rData);
                    if (rData.getValue().contains(":") || rData.getValue().contains("OK")) {
                        long timeInMillis = System.currentTimeMillis() / 1000;
                        if (i < (set1.size() - 1)) {
                            szQuery = szQuery + "('" + rData.getHost() + "','" + rData.getHostGroup() + "','" + rData.getService() + "','"
                                    + rData.getSubService() + "'," + rData.getTimestamp1() + ",'" + timeInMillis + "','" + rData.getCategory()
                                    + "'" + ",'" + rData.getMetricType() + "','" + rData.getValue() + "','" + rData.getResourceType() + "','"
                                    + rData.getResourceSubType() + "','" + rData.getResourceId() + "','" + rData.getEventID() + "','"
                                    + rData.getSLAvalue() + "','" + rData.getCCustomerID() + "')";
                        } else {
                            szQuery = szQuery + "('" + rData.getHost() + "','" + rData.getHostGroup() + "','" + rData.getService() + "','"
                                    + rData.getSubService() + "'," + rData.getTimestamp1() + ",'" + timeInMillis + "','" + rData.getCategory()
                                    + "'" + ",'" + rData.getMetricType() + "','" + rData.getValue() + "','" + rData.getResourceType() + "','"
                                    + rData.getResourceSubType() + "','" + rData.getResourceId() + "','" + rData.getEventID() + "','"
                                    + rData.getSLAvalue() + "','" + rData.getCCustomerID() + "')";
                        }
                        
                    } else if (Double.parseDouble(rData.getValue()) != -1.1) {
                        long timeInMillis = System.currentTimeMillis() / 1000;
                        if (i < (set1.size() - 1)) {
                            szQuery = szQuery + "('" + rData.getHost() + "','" + rData.getHostGroup() + "','" + rData.getService() + "','"
                                    + rData.getSubService() + "'," + rData.getTimestamp1() + ",'" + timeInMillis + "','" + rData.getCategory()
                                    + "'" + ",'" + rData.getMetricType() + "','" + rData.getValue() + "','" + rData.getResourceType() + "','"
                                    + rData.getResourceSubType() + "','" + rData.getResourceId() + "','" + rData.getEventID() + "','"
                                    + rData.getSLAvalue() + "','" + rData.getCCustomerID() + "'),";
                        } else {
                            szQuery = szQuery + "('" + rData.getHost() + "','" + rData.getHostGroup() + "','" + rData.getService() + "','"
                                    + rData.getSubService() + "'," + rData.getTimestamp1() + ",'" + timeInMillis + "','" + rData.getCategory()
                                    + "'" + ",'" + rData.getMetricType() + "','" + rData.getValue() + "','" + rData.getResourceType() + "','"
                                    + rData.getResourceSubType() + "','" + rData.getResourceId() + "','" + rData.getEventID() + "','"
                                    + rData.getSLAvalue() + "','" + rData.getCCustomerID() + "')";
                        }
                    } else {
                        log.error("Record not inserted mvalue is -1.1");
                    }
                }
                log.debug("Query====" + szQuery);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int lengthNow = szQuery.length();
        if (initialLen == lengthNow) {
            return null;
        }
        return szQuery;
    }
    
    void updateSLA(ReportData rData) {
        String slaValue = null;
        try {
            System.out.println("Query=="
                    + "select "
                    + "metricthresholdvalue "
                    + "from "
                    + "smetricslathresholds "
                    + "where "
                    + "service='" + rData.getService().trim() + "' and "
                    + "metrictype='" + rData.getMetricType().trim() + "' and "
                    + "customerid='" + rData.getCCustomerID() + "' and "
                    + "host='" + rData.getHost() + "'");
            
            ResultSet rs1 = stmt.executeQuery(
                    "select "
                    + "metricthresholdvalue "
                    + "from "
                    + "smetricslathresholds "
                    + "where "
                    + "service='" + rData.getService().trim() + "' and "
                    + "metrictype='" + rData.getMetricType().trim() + "' and "
                    + "customerid='" + rData.getCCustomerID() + "' and "
                    + "host='" + rData.getHost() + "'");
            
            if (rs1.next()) {
                slaValue = Double.toString(rs1.getDouble("metricthresholdvalue"));
            } else {
                slaValue = "0";
            }
            System.out.println("sla = " + slaValue + ".......");
            rData.setSLAvalue(slaValue);
        } catch (Exception e) {
            System.out.println("SLA could not be updated....");
            rData.setSLAvalue("0");
        }
    }
    
    public int sendAgentToDatabse(HashMap<String, ReportData> metricMap, String customerID) {
        int status = 0;
        System.out.println("Inside sendAgentToDatabse HashMapLength======" + metricMap.size());
        
        Set<String> RDset = null;
        
        try {
            RDset = metricMap.keySet();
            Iterator iter = RDset.iterator();
            con = ConnectionDAO.getConnection(customerID);
            stmt = con.createStatement();
            
            for (int i = 0; i < RDset.size(); i++) {
                try {
                    ReportData rData = (ReportData) metricMap.get(iter.next().toString());
                    int availability = Integer.parseInt(rData.getValue());
                    System.out.println("availability::" + rData.getValue());
                    String szQuery = "Select * from servicemetrics where metrictype='" + rData.getMetricType() + "' and host='"
                            + rData.getHost() + "' and resourceId='" + rData.getResourceId() + "' and customerid='" + rData.getCCustomerID() + "'";
                    System.out.println("szQuery=" + szQuery);
                    ResultSet rs1 = stmt.executeQuery(szQuery);
                    System.out.println("checking records in database for agent -> " + szQuery);
                    boolean downRecord = false;// no record for agent down is present in database
                    if (rs1.next()) {
                        downRecord = true;// means agent down is present
                        System.out.println(rData.getMetricType() + " on " + rData.getHost() + " down record found");
                    } else if (availability == 0) {
                        System.out.println(rData.getMetricType() + " on " + rData.getHost() + " down record not found, inserting event");
                        szQuery = "Insert into  servicemetrics(host,timestamp1,category,metrictype,"
                                + "resourceType, resourceSubType,resourceId,customerid) values('" + rData.getHost() + "','" + rData.getTimestamp1()
                                + "','" + rData.getCategory() + "'" + ",'" + rData.getMetricType() + "','" + rData.getResourceType() + "','"
                                + rData.getResourceSubType() + "','" + rData.getResourceId() + "','" + rData.getCCustomerID() + "')";
                        System.out.println("execute query::" + szQuery);
                        int insStatus = stmt.executeUpdate(szQuery);
                        return insStatus;
                    }
                    if (downRecord && (availability == 1)) {
                        System.out.println(rData.getMetricType() + " on " + rData.getHost() + " down record found, deleting from DB");
                        szQuery = "delete from  servicemetrics where host='" + rData.getHost() + "' and category='"
                                + rData.getCategory() + "'" + " and metrictype='" + rData.getMetricType() + "' and resourcetype='"
                                + rData.getResourceType() + "' and resourcesubtype='" + rData.getResourceSubType() + "' and resourceid='"
                                + rData.getResourceId() + "' and customerid='" + rData.getCCustomerID() + "'";
                        System.out.println("execute query::" + szQuery);
                        int insStatus = stmt.executeUpdate(szQuery);
                        return insStatus;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (ex.getMessage().contains("java.net.SocketException: Broken pipe")) {
                        System.out.println("SokectException :Broken pipe");
                        System.out.println("calling ConDAO.closeConn()");
                ConnectionDAO.closeConnection(customerID);
                System.out.println("exited from ConDAO.closeCon");
                    }
                } finally {
                    continue;
                }
                
            }
            RDset = null;
            iter = null;
            
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
            e.printStackTrace();
            
        } finally {
            
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                /*if (con != null) {
                 //con.close();
                 //con = null;
                 // System.out.println("Connection in Opsview"+con);
                 }*/
                
            } catch (SQLException se) {
                System.out.println("Exception:" + se.getMessage());
                se.printStackTrace();
            }
        }
        return status;
    }
}
