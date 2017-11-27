/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.adapter.etl.ETLAdapter;


import com.prokosha.dbconnection.ConnectionDAO;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author vijayasri
 */
public class ReadPerfData {

    private static final Logger log = Logger.getLogger(ReadPerfData.class.getName());
    private static Connection con = null;
    private static Statement stmt = null;
    private static ResultSet rs = null;
    static boolean mapInitialized = false;
    Properties properties = null;
    Properties properties1 = null;
    static Enumeration enum1 = null;
    String metricValue = null;

    public ReadPerfData() {
        super();

    }

    public void initilize(String perfEvent) {








        String resourceType = null;
        String strLine = null;
        String hostName = null;
        int numTotalLine = 0;
        int numCpuLine = 0;
        int numMemoryLine = 0;
        int numDiskLine = 0;
        long reportTime = 0L;
        double bootUsg = 0.0;
        double memUsg = 0.0;
        double loadOne = 0.0;
        double loadFive = 0.0;
        double loadFifteen = 0.0;
        double varUsg = 0.0;
        double usrUsg = 0.0;
        double rootUsg = 0.0;
        double homeUsg = 0.0;
        Class msgClass;
        Object msgObj;


        String server = null;
        File file1 = null;






        try {

            log.debug("------IN READPERFDATA--------");

            try {

                String line = perfEvent;

                if (line.length() > 0) {

                    //  log.debug("IN REadPIPe 1st REad " + line);
                    ReportOVMsg msg = new ReportOVMsg(line);
                    String type = msg.getEventtype();
                    //log.debug("token[0]=========" + type);
                    String className = PerfMsgToClass.getClassForMsg(type);
                    // log.debug("ClassName"+className);
                    if (className != null) {
                        log.debug("Constructing class object for OvMsgXXX class::" + className);
                        msgClass = Class.forName(className);
                        log.debug("Constructing ctor method for OvMsgXXX class::" + className);
                        Constructor msgObjConstructor = msgClass.getConstructor(Class.forName("java.lang.String"));
                        log.debug("Constructing object instance for OvMsgXXX class::" + className);
                        msgObj = msgObjConstructor.newInstance(line);
                    } else {
                        log.error("*** ERROR *** Cannot decipher handler for msg::" + type + " in messageToClass registry!!");

                    }



                } else {
                    log.debug("*****Perf Stream is Null  ******");
                }

            } catch (Exception iex) {
                log.error("Exception:" + iex.getMessage());
                iex.printStackTrace();
            }


        } catch (Exception e1) {
            log.error("Exception :" + e1.getMessage());
            e1.printStackTrace();
        } finally {
            try {
                // fis.close();
                //fstream.close();
                // din.close();
            } catch (Exception e2) {
                log.error("Exception :" + e2.getMessage());
                e2.printStackTrace();
            } finally {





                resourceType = null;
                strLine = null;
                hostName = null;


                msgClass = null;
                msgObj = null;


                server = null;
                file1 = null;
            }
        }
    }

    /*  public void CacheInsert(ReportOVMsg msg)
    {
    String Elapsed_Time = "-1.1";
    if (!msg.getServiceperfdata().equals("(null)"))
    {
    log.debug("Service perfdata(memCache):: " + msg.getServiceperfdata());
    log.debug("host=="+msg.getHostname()+"Time===="+msg.getServicetime());
    String spd[] = msg.getServiceperfdata().split(",");

    String tok[];
    if (TokenParser.isValidToken(spd, 1))
    {
    tok = spd[1].split("=");
    Elapsed_Time = TokenParser.trimmedToken(tok, 1, "-1.1");
    log.debug("ElapsedTime"+Elapsed_Time);
    }else
    {
    log.debug("*** ERROR *** invalid messages received... cannot parse and make CEP msg...");
    }

    memCacheInsert(msg.getHostname(),Long.parseLong(msg.getServicetime()),Double.parseDouble(Elapsed_Time));


    }
    else
    {
    log.debug("***Error****Invalid message Received (Serviceperfdata is Null)");
    }

    }*/
    public void memCacheInsert(String hostName, Long reportTime, Double elapsedTime, String customerID) {
        String hostGroupName = null;
        String service1 = null;
        String subService1 = null;

        try {

            con = ConnectionDAO.getConnection(customerID);
            stmt = con.createStatement();
            /*
            rs1 = stmt.executeQuery("Select count(*) from memory_usage where host='" + hostName + "'");

            if (rs1.next())
            {
            if (rs1.getInt(1) == 0)
            {
            log.debug("Result Set is null:Insert");
             *
             */
            rs = stmt.executeQuery("Select service,subservice,hostgroup from hostinfo where lower(host)=lower('" + hostName + "')");
            if (rs == null) {
                log.debug("HostName does not exist in hostinfo:mem");
            }
            while (rs.next()) {
                service1 = rs.getString("service");
                subService1 = rs.getString("subservice");
                hostGroupName = rs.getString("hostgroup");
            }
            rs.close();
            // log.debug("From hostinfo of mem: " + service1 + " " + subService1 + " " + hostGroupName);

            rs = stmt.executeQuery("Select count(*) from servicemetrics where host='" + hostName + "' and timestamp1=" + reportTime);
            long timeInMillis = System.currentTimeMillis() / 1000;
            if (rs.next()) {
                if (rs.getInt(1) == 0) {
                    if (elapsedTime != -1.1) {
                        int insVal = stmt.executeUpdate("Insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue) values('" + hostName + "','" + hostGroupName + "','" + service1 + "','" + subService1 + "'," + reportTime + "," + timeInMillis + ",'memcache'" + ",'" + "latency" + "'," + elapsedTime + ")");
                        log.debug("Inserted memCache: " + insVal);
                        stmt.close();
                    }
                } else {
                    log.debug("Duplicate host with same timestamp found inside memory_usage.");
                }
            }

            if (rs == null) {
                log.debug("memory_usage has no record.");
            }
            /*    } else
            {
            log.debug("Result Set is not null:Update");
            int updVal = stmt.executeUpdate("Update memory_usage set mem_usg='" + memUsg + "' where host='" + hostName + "'");
            log.debug("Updated mem: " + updVal);
            stmt.close();
            }

            }

             */
            rs.close();
            //rs1.close();


        } catch (Exception e) {
            log.error("Exception :" + e.getMessage());
            e.printStackTrace();
            if(e.getMessage().contains("java.net.SocketException: Broken pipe")){
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
                /*if (con != null) {
                con.close();
                con = null;
                //log.debug("Connection in ganglia"+con);

                }*/
                hostGroupName = null;
                service1 = null;
                subService1 = null;

            } catch (SQLException se) {
                log.error("Exception :" + se.getMessage());
                se.printStackTrace();
            }


        }
    }
    /* public void cpuInsert(ReportOVMsg msg) {

    String Load1 = "-1.1";
    String Load5 = "-1.1";
    String Load15 = "-1.1";
    if (!msg.getServiceperfdata().equals("(null)")) {
    log.debug("Service perfdata:: " + msg.getServiceperfdata());
    String spd[] = msg.getServiceperfdata().split(";");

    //Load1
    String tok[];
    if (TokenParser.isValidToken(spd, 0)) {
    tok = spd[0].split("=");
    Load1 = TokenParser.trimmedToken(tok, 1, "-1.1");
    log.debug("Perfdata:: Load1:: " + Load1);
    }
    if (TokenParser.isValidToken(spd, 4)) {
    tok = spd[4].split("=");
    Load5 = TokenParser.trimmedToken(tok, 1, "-1.1");
    log.debug("Perfdata:: Load5:: " + Load5);
    }
    if (TokenParser.isValidToken(spd, 8)) {
    tok = spd[8].split("=");
    Load15 = TokenParser.trimmedToken(tok, 1, "-1.1");
    log.debug("Perfdata:: Load15:: " + Load15);
    }
    cpuInsert(msg.getHostname(),Long.parseLong(msg.getServicetime()), Double.parseDouble(Load1), Double.parseDouble(Load5), Double.parseDouble(Load15));
    } else {
    log.debug("*** ERROR *** invalid messages received... cannot parse and make CEP msg...");
    }





    log.debug("cpu" + msg.getHostname() + "," + msg.getServicetime() + ","
    + Load1 + "," + Load5 + "," + Load15);

    }

    public void diskInsert(ReportOVMsg msg) {

    String bootUsg = "-1.1";
    String varUsg = "-1.1";
    String homeUsg = "-1.1";
    String usrUsg = "-1.1";
    String rootUsg = "-1.1";
    if (!msg.getServiceoutput().equals("(null)")) {
    log.debug("MSG" + msg);
    String Usage = "-1.1";
    String sop[] = msg.getServiceoutput().split(" ");

    if (TokenParser.isValidToken(sop, 8)) {
    sop[8] = TokenParser.replaceTrimToken(sop, 8, '(', ' ', "-1.1");
    String subsop[] = sop[8].split("%");
    if (!(subsop[0].equals(Usage))) {
    Usage = "" + (100 - Integer.parseInt(subsop[0]));
    }
    }

    if (!msg.getServicename().equals("(null)")) {
    String[] token = msg.getServicename().replace("\n", " ").trim().split("([|]{2}+)");
    if (token[0].contains("100")) {
    varUsg = Usage;
    } else if (token[0].contains("102")) {
    homeUsg = Usage;
    } else if (token[0].contains("103")) {
    usrUsg = Usage;
    } else if (token[0].contains("101")) {
    bootUsg = Usage;
    } else if (token[0].contains("114")) {
    rootUsg = Usage;
    }
    }
    diskInsert(msg.getHostname(),Long.parseLong(msg.getServicetime()), Double.parseDouble(bootUsg), Double.parseDouble(varUsg), Double.parseDouble(homeUsg),Double.parseDouble(usrUsg),Double.parseDouble(rootUsg));
    } else {
    log.debug("*** ERROR *** invalid messages received... cannot parse and make CEP msg...");
    //log CRITICAL error to the logfile
    }



    log.debug("disk=========," + msg.getHostname() + "," + msg.getServicetime()
    + "," + bootUsg + "," + varUsg + "," + homeUsg + "," + usrUsg + "," + rootUsg);

    }

    public void memoryInsert(ReportOVMsg msg) {

    String Real_Usage = "-1.1";
    String Memory_Used = "-1.1";
    String Memory_Max = "-1.1";
    String Buffer = "-1.1";
    String Cache = "-1.1";
    String Swap = "-1.1";
    String Swap_Used = "-1.1";
    String Swap_Max = "-1.1";
    String Utilization = "-1.1";

    String sopToken = msg.getServiceoutput();

    if ((!sopToken.equals("(null)"))
    && (sopToken.contains("real") && sopToken.contains("buffer") && sopToken.contains("cache") && sopToken.contains("swap"))) {

    log.debug("Service ouput: " + sopToken);
    String sop[] = sopToken.trim().split(" ");
    Real_Usage = TokenParser.replaceTrimToken(sop, 2, '%', ' ', "-1.1");
    log.debug("Real memory usage: " + Real_Usage);

    if (TokenParser.isValidToken(sop, 3)) {
    String memory1[] = sop[3].split("/"); //(663/947
    Memory_Used = TokenParser.replaceTrimToken(memory1, 0, '(', ' ', "-1.1");
    Memory_Max = TokenParser.trimmedToken(memory1, 1, "-1.1");
    log.debug("Memory usage: " + Memory_Used + " Memory max available: " + Memory_Max);
    }


    Buffer = TokenParser.trimmedToken(sop, 6, "-1.1");
    log.debug("Buffer memory: " + Buffer);

    Cache = TokenParser.trimmedToken(sop, 9, "-1.1");
    log.debug("Cache memory: " + Cache);

    Swap = TokenParser.replaceTrimToken(sop, 12, '%', ' ', "-1.1");
    log.debug("Swap memory utilisation: " + Swap);


    if (TokenParser.isValidToken(sop, 13)) {
    String memory1[] = sop[13].split("/"); //(80/2047
    Swap_Used = TokenParser.replaceTrimToken(memory1, 0, '(', ' ', "-1.1");
    Swap_Max = TokenParser.trimmedToken(memory1, 1, "-1.1");
    log.debug("Swap memory used: " + Swap_Used + " Swap maximum: " + Swap_Max);
    }
    memInsert(msg.getHostname(),  Long.parseLong(msg.getServicetime()), Double.parseDouble(Real_Usage));
    } else {
    log.debug("*** ERROR *** invalid message received... cannot parse and make CEP msg...");
    }

    if (!msg.getServiceperfdata().equals("(null)")) {
    //Format: utilisation=70
    String spd[] = msg.getServiceperfdata().split("=");

    Utilization = TokenParser.trimmedToken(spd, 1, "-1.1");
    log.debug("Utilisation: " + Utilization);
    } else {
    log.debug("*** ERROR *** invalid message received... cannot parse and make CEP msg...");
    }





    log.debug("memory=========," + msg.getHostname() + "," + msg.getServicetime() + "," + Real_Usage);

    }*/

    public void cpuInsert(String hostName, long reportTime, double loadOne, double loadFive, double loadFifteen, String customerID) {
        String hostGroupName = null;
        String service1 = null;
        String subService1 = null;



        try {

            con = ConnectionDAO.getConnection(customerID);
            stmt = con.createStatement();

            rs = stmt.executeQuery("Select service,subservice,hostgroup from hostinfo where lower(host)=lower('" + hostName + "')");
            if (rs == null) {
                log.debug("HostName does not exist in hostinfo:cpu");
            }
            while (rs.next()) {
                service1 = rs.getString("service");
                subService1 = rs.getString("subservice");
                hostGroupName = rs.getString("hostgroup");
            }
            rs.close();
            //   log.debug("From hostinfo of cpu: " + service1 + " " + subService1 + " " + hostGroupName);

            rs = stmt.executeQuery("Select count(*) from servicemetrics where host='" + hostName + "' and timestamp1=" + reportTime + " and metrictype='load1'");
            long timeInMillis = System.currentTimeMillis() / 1000;
            if (rs.next()) {
                if (rs.getInt(1) == 0) {
                    if (loadOne != -1.1) {

                        int insVal = stmt.executeUpdate("Insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue) values('" + hostName + "','" + hostGroupName + "','" + service1 + "','" + subService1 + "'," + reportTime + "," + timeInMillis + ",'cpu'" + ",'load1'," + loadOne + ")");
                        log.debug("Inserted cpu(load1): " + insVal);
                    } else {
                        log.debug("Record Not Inserted(loadOne=-1.1)");
                    }

                } else {
                    log.debug("Duplicate host with same timestamp found inside cpu_utilisation(load1).");
                }
            }
            rs = stmt.executeQuery("Select count(*) from servicemetrics where host='" + hostName + "' and timestamp1=" + reportTime + " and metrictype='load5'");

            if (rs.next()) {
                if (rs.getInt(1) == 0) {
                    if (loadFive != -1.1) {
                        int insVal2 = stmt.executeUpdate("Insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue) values('" + hostName + "','" + hostGroupName + "','" + service1 + "','" + subService1 + "'," + reportTime + "," + timeInMillis + ",'cpu'" + ",'load5'," + loadFive + ")");
                        log.debug("Inserted cpu(load5): " + insVal2);
                    } else {
                        log.debug("Record Not Inserted(loadFive=-1.1)");
                    }


                } else {
                    log.debug("Duplicate host with same timestamp found inside cpu_utilisation(load5).");
                }

            }
            rs = stmt.executeQuery("Select count(*) from servicemetrics where host='" + hostName + "' and timestamp1=" + reportTime + " and metrictype='load15'");

            if (rs.next()) {
                if (rs.getInt(1) == 0) {
                    if (loadFifteen != -1.1) {
                        int insVal3 = stmt.executeUpdate("Insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue) values('" + hostName + "','" + hostGroupName + "','" + service1 + "','" + subService1 + "'," + reportTime + "," + timeInMillis + ",'cpu'" + ",'load15'," + loadFifteen + ")");
                        log.debug("Inserted cpu(load15): " + insVal3);
                    } else {
                        log.debug("Record Not Inserted(loadFifteen=-1.1)");
                    }


                } else {
                    log.debug("Duplicate host with same timestamp found inside cpu_utilisation(load15).");
                }
            }

            if (rs == null) {
                log.debug("cpu_utilisation has no record.");
            }
            rs.close();



        } catch (Exception e) {
            log.error("Exception :" + e.getMessage());
            e.printStackTrace();
            if(e.getMessage().contains("java.net.SocketException: Broken pipe")){
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
                /*if (con != null) {
                con.close();
                con = null;
                //log.debug("Connection in ganglia"+con);

                }*/
                hostGroupName = null;
                service1 = null;
                subService1 = null;


            } catch (SQLException se) {
                log.error("Exception :" + se.getMessage());
                se.printStackTrace();
            }
        }
    }

    public void diskInsert(String hostName, long reportTime, double bootUsg, double varUsg, double usrUsg, double homeUsg, double rootUsg, String customerID) {
        String hostGroupName = null;
        String service1 = null;
        String subService1 = null;
        log.debug("boot==" + bootUsg + "var==" + varUsg + "home==" + homeUsg + "usr==" + usrUsg + "root==" + rootUsg);

        Timestamp reportDate = new Timestamp(new Date().getTime());
        log.debug("Report Date disk: " + reportDate);

        try {

            con = ConnectionDAO.getConnection(customerID);
            stmt = con.createStatement();


            rs = stmt.executeQuery("Select service,subservice,hostgroup from hostinfo where lower(host)=lower('" + hostName + "')");
            if (rs == null) {
                log.debug("HostName does not exist in hostinfo:disk");
            }
            while (rs.next()) {
                service1 = rs.getString("service");
                subService1 = rs.getString("subservice");
                hostGroupName = rs.getString("hostgroup");
            }
            rs.close();
            log.debug("From hostinfo of disk: " + service1 + " " + subService1 + " " + hostGroupName);

            rs = stmt.executeQuery("Select count(*) from servicemetrics where host='" + hostName + "' and timestamp1=" + reportTime + " and metrictype='diskusage_boot'");
            long timeInMillis = System.currentTimeMillis() / 1000;
            if (rs.next()) {
                if (rs.getInt(1) == 0) {
                    if (bootUsg != -1.1) {
                        int insVal = stmt.executeUpdate("Insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue) values('" + hostName + "','" + hostGroupName + "','" + service1 + "','" + subService1 + "'," + reportTime + "," + timeInMillis + ",'disk'" + ",'" + "diskusage_boot" + "'," + bootUsg + ")");
                        log.debug("Inserted disk : " + insVal);
                    } else {
                        log.debug("Record Not Inserted(bootUsg=-1.1)");
                    }
                } else {
                    log.debug("Duplicate host with same timestamp found inside disk_usage.");
                }

            }
            rs = stmt.executeQuery("Select count(*) from servicemetrics where host='" + hostName + "' and timestamp1=" + reportTime + " and metrictype='diskusage_var'");

            if (rs.next()) {
                if (rs.getInt(1) == 0) {
                    if (varUsg != -1.1) {
                        int insVa2 = stmt.executeUpdate("Insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue) values('" + hostName + "','" + hostGroupName + "','" + service1 + "','" + subService1 + "'," + reportTime + "," + timeInMillis + ",'disk'" + ",'" + "diskusage_var" + "'," + varUsg + ")");
                        log.debug("Inserted disk: " + insVa2);
                    } else {
                        log.debug("Record Not Inserted(varUsg=-1.1)");
                    }
                } else {
                    log.debug("Duplicate host with same timestamp found inside disk_usage.");
                }

            }
            rs = stmt.executeQuery("Select count(*) from servicemetrics where host='" + hostName + "' and timestamp1=" + reportTime + " and metrictype='diskusage_home'");

            if (rs.next()) {
                if (rs.getInt(1) == 0) {
                    if (homeUsg != -1.1) {
                        int insVa3 = stmt.executeUpdate("Insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue) values('" + hostName + "','" + hostGroupName + "','" + service1 + "','" + subService1 + "'," + reportTime + "," + timeInMillis + ",'disk'" + ",'" + "diskusage_home" + "'," + homeUsg + ")");
                        log.debug("Inserted disk: " + insVa3);
                    } else {
                        log.debug("Record Not Inserted(homeUsg=-1.1)");
                    }
                } else {
                    log.debug("Duplicate host with same timestamp found inside disk_usage.");
                }

            }
            rs = stmt.executeQuery("Select count(*) from servicemetrics where host='" + hostName + "' and timestamp1=" + reportTime + " and metrictype='diskusage_usr'");

            if (rs.next()) {
                if (rs.getInt(1) == 0) {
                    if (usrUsg != -1.1) {
                        int insVa4 = stmt.executeUpdate("Insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue) values('" + hostName + "','" + hostGroupName + "','" + service1 + "','" + subService1 + "'," + reportTime + "," + timeInMillis + ",'disk'" + ",'" + "diskusage_user" + "'," + usrUsg + ")");
                        log.debug("Inserted disk: " + insVa4);
                    } else {
                        log.debug("Record Not Inserted(usrUsg=-1.1)");
                    }
                } else {
                    log.debug("Duplicate host with same timestamp found inside disk_usage.");
                }

            }
            rs = stmt.executeQuery("Select count(*) from servicemetrics where host='" + hostName + "' and timestamp1=" + reportTime + " and metrictype='diskusage_root'");

            if (rs.next()) {
                if (rs.getInt(1) == 0) {
                    if (rootUsg != -1.1) {
                        int insVa5 = stmt.executeUpdate("Insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue) values('" + hostName + "','" + hostGroupName + "','" + service1 + "','" + subService1 + "'," + reportTime + "," + timeInMillis + ",'disk'" + ",'" + "diskusage_root" + "'," + rootUsg + ")");
                        log.debug("Inserted disk: " + insVa5);
                    } else {
                        log.debug("Record Not Inserted(rootUsg=-1.1)");
                    }
                } else {
                    log.debug("Duplicate host with same timestamp found inside disk_usage.");
                }

            }

            stmt.close();



            if (rs == null) {
                log.debug("disk_usage has no record.");
            }
            rs.close();



        } catch (Exception e) {
            log.error("Exception :" + e.getMessage());
            e.printStackTrace();
            if(e.getMessage().contains("java.net.SocketException: Broken pipe")){
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
                /*if (con != null) {
                con.close();
                con = null;
                //log.debug("Connection in ganglia"+con);

                }*/
                hostGroupName = null;
                service1 = null;
                subService1 = null;

            } catch (SQLException se) {
                log.error("Exception :" + se.getMessage());
                se.printStackTrace();
            }
        }
    }

    public void memInsert(String hostName, long reportTime, double memUsg, double maxMemory, String customerID) {
        String hostGroupName = null;
        String service1 = null;
        String subService1 = null;
        double mem_Usg = (memUsg / maxMemory) * 100;
        Timestamp reportDate = new Timestamp(new Date().getTime());
        log.debug("Report Date mem: " + reportDate);
        log.debug("Memory Usage " + mem_Usg);
        try {

            con = ConnectionDAO.getConnection(customerID);
            stmt = con.createStatement();
            /*
            rs1 = stmt.executeQuery("Select count(*) from memory_usage where host='" + hostName + "'");

            if (rs1.next())
            {
            if (rs1.getInt(1) == 0)
            {
            log.debug("Result Set is null:Insert");
             *
             */
            rs = stmt.executeQuery("Select service,subservice,hostgroup from hostinfo where lower(host)=lower('" + hostName + "')");
            if (rs == null) {
                log.debug("HostName does not exist in hostinfo:mem");
            }
            while (rs.next()) {
                service1 = rs.getString("service");
                subService1 = rs.getString("subservice");
                hostGroupName = rs.getString("hostgroup");
            }
            rs.close();
            log.debug("From hostinfo of mem: " + service1 + " " + subService1 + " " + hostGroupName);

            rs = stmt.executeQuery("Select count(*) from servicemetrics where host='" + hostName + "' and timestamp1=" + reportTime);
            long timeInMillis = System.currentTimeMillis() / 1000;
            if (rs.next()) {
                if (rs.getInt(1) == 0) {
                    if (mem_Usg != -1.1) {
                        int insVal = stmt.executeUpdate("Insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue) values('" + hostName + "','" + hostGroupName + "','" + service1 + "','" + subService1 + "'," + reportTime + "," + timeInMillis + ",'memory'" + ",'" + "memusg" + "'," + mem_Usg + ")");
                        log.debug("Inserted mem: " + insVal);
                        stmt.close();
                    } else {
                        log.debug("Record Not Inserted(memUsg=-1.1)");
                    }
                } else {
                    log.debug("Duplicate host with same timestamp found inside memory_usage.");
                }
            }

            if (rs == null) {
                log.debug("memory_usage has no record.");
            }
            /*    } else
            {
            log.debug("Result Set is not null:Update");
            int updVal = stmt.executeUpdate("Update memory_usage set mem_usg='" + memUsg + "' where host='" + hostName + "'");
            log.debug("Updated mem: " + updVal);
            stmt.close();
            }

            }

             */
            rs.close();
            //rs1.close();


        } catch (Exception e) {
            log.error("Exception :" + e.getMessage());
            e.printStackTrace();
            if(e.getMessage().contains("java.net.SocketException: Broken pipe")){
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
                /*if (con != null) {
                con.close();
                con = null;
                //log.debug("Connection in ganglia"+con);

                }*/
                hostGroupName = null;
                service1 = null;
                subService1 = null;

            } catch (SQLException se) {
                log.error("Exception :" + se.getMessage());
                se.printStackTrace();
            }
        }
    }
}


