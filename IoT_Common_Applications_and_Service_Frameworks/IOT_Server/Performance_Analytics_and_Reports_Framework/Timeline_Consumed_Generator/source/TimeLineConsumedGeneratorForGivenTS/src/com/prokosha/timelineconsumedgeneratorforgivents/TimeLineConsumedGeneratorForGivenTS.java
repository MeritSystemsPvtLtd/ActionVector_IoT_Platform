/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.timelineconsumedgeneratorforgivents;
import com.prokosha.dbconnection.ConnectionDAO;
import com.prokosha.timelineconsumedgeneratorforgiventsconfiguration.TimeLineConsumedGeneratorConfiguration;
import com.prokosha.timelineconsumedgeneratorforgiventsts.StartAndEndTS;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.postgresql.util.PSQLException;

/**
 *
 * @author yedukondalu
 */
public class TimeLineConsumedGeneratorForGivenTS {
     

    private static Logger logger = Logger.getLogger(TimeLineConsumedGeneratorForGivenTS.class.getName());
    public static String propertyFile = null;
    public static String cid = null;
    public static boolean curDayflag=false;
    public static HashMap deltaGen = new LinkedHashMap();
    public static HashMap deltaCons = new LinkedHashMap();
    public static HashMap cummGenMap = new LinkedHashMap();
    public static HashMap cummConsMap = new LinkedHashMap();

    public TimeLineConsumedGeneratorForGivenTS(String propertyFileName) {

        System.out.println("Loading the property file" + propertyFileName);

        this.propertyFile = propertyFileName;
    }

    public static void initialize() throws IOException {

        if (!TimeLineConsumedGeneratorConfiguration.loadProperties(propertyFile)) {
            System.out.println("Error in loading property file");
            System.exit(0);
        }

    }

    public static void generateAggregateValues(long periodTS) throws IOException {

        String query = "select distinct resourceid,service,resourcetype,customerid from hostinfo";
        cid = TimeLineConsumedGeneratorConfiguration.getCustomerId();
        ResultSet rset = ConnectionDAO.executerQuery(query, cid);
        String metricType = TimeLineConsumedGeneratorConfiguration.getMetricType();
        String period = TimeLineConsumedGeneratorConfiguration.getPeriod();    
        String resourceId = null;
        String service = null;
        String resourceType = null;
        int customerId;
        System.out.println("in method " + periodTS);
        
        
        try {
            while (rset.next()) {
                resourceId = rset.getString(1);
                service = rset.getString(2);
                resourceType = rset.getString(3);
                customerId = Integer.parseInt(rset.getString(4));
                System.out.println("sevice " + service);
                System.out.println("resourceid " + resourceId);
                System.out.println("resourcetype " + resourceType);
                System.out.println("customerid " + customerId);
                System.out.println("metricType " + metricType);
                    generateConsumedGeneratedValues(periodTS, period, resourceType, resourceId, metricType, customerId, service);
            }

            System.out.println("generation is over");

        } catch (SQLException ex) {
            ex.printStackTrace();
            logger.error(ex);
        } finally {
            try {
                if (rset != null) {
                    rset.close();
                    rset = null;
                } else if (query != null) {
                    query = null;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                logger.error(ex);

            }

        }

    }

    public static void generateConsumedGeneratedValues(Long epochtime, String period, String resourcetype, String resourceid, String metrictype, int customerid, String service) {

        //DateFormat form = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
        System.out.println("epochtime"+epochtime);
        
        List startEndTS=null;
        startEndTS= StartAndEndTS.getPeriodEndTS(epochtime*1000, period);
        //System.out.println("startEndTS :" + startEndTS.get(1));
        //System.out.println("startEndTS :" + startEndTS.get(3));
        //System.out.println("startEndTS :" + startEndTS);
        String timestamp1 = (String) startEndTS.get(0);
        int week = (int) startEndTS.get(4);
        String[] splite1 = timestamp1.split("/");
        int startDay = Integer.parseInt(splite1[0]);
        System.out.println("startday" + startDay);
        String timestamp2 = (String) startEndTS.get(2);
        String[] splite2 = timestamp2.split("/");
        int lastDay = Integer.parseInt(splite2[0]);
        System.out.println("lastDay" + lastDay);
        System.out.println("mon" + splite1[1]);
        System.out.println("year" + splite1[2].substring(0, 4));
        HashMap map = new LinkedHashMap();
        ResultSet rs = null, rs1 = null;
        String query = null, query1 = null;
        try {

            long metricvalue = 0;
            long lastNEMValue = 0;
            String host = null;
            String hostgroup = null;
            String subservice = null;
            String category = null;
            String eventid = null;
            String rsubtype = null;
            Double sla = 0.0;
            int status = 0;
            long timeStamp = 0;
            long timeStamp2 = 0;
            long lastNETS = 0L;
             long lastECTS = 0L;
            long lastDETS=0;
             long lastDECumValue = 0;
             long lastECCumValue = 0;
             int i = 0;
             System.out.println(curDayflag);
             
                         query1 = "select timestamp1,metricvalue from servicemetrics where resourcetype='" + resourcetype + "' and metrictype ='DayEnergy' and customerid=" + customerid
                            + " and service = '" + service + "' and resourceid = '" + resourceid + "' and timestamp1 <="+epochtime
                                 +" and timestamp1 between "+startEndTS.get(1)+" and "+startEndTS.get(3)+" order by timestamp1 desc limit 1";
                         System.out.println("query1 " + query1);
                    rs1 = ConnectionDAO.executerQuery(query1, cid);
                    while (rs1.next()) {
                        lastDETS = Long.parseLong(rs1.getString(1));
                        System.out.println("energy lastts " + lastDETS);
                        lastDECumValue = Long.parseLong(rs1.getString(2));
                        System.out.println("lastDECumValue cumm day energy value " + lastDECumValue);

                    }
                    if(lastDETS!=0){
                    query1 = "select timestamp1,metricvalue from servicemetrics where resourcetype='" + resourcetype + "' and metrictype ='EnergyConsumed' and customerid=" + customerid
                            + " and service = '" + service + "' and resourceid = '" + resourceid + "' and timestamp1 ="+lastDETS
                                 +" order by timestamp1 desc limit 1";
                         System.out.println("query1 " + query1);

                    rs1 = ConnectionDAO.executerQuery(query1, cid);
                    while (rs1.next()) {
                        lastECTS = Long.parseLong(rs1.getString(1));
                        System.out.println("energyconsumed lastts " + lastECTS);
                        lastECCumValue = Long.parseLong(rs1.getString(2));
                        System.out.println("lastECCumValue cumm day energy value " + lastECCumValue);

                    }
                    query1 = "select timestamp1,metricvalue from servicemetrics where resourcetype='" + resourcetype + "' and metrictype ='"+metrictype+"' and customerid=" + customerid
                            + " and service = '" + service + "' and resourceid = '" + resourceid + "' and timestamp1 ="+lastDETS
                                 +" order by timestamp1 desc limit 1";
                     System.out.println("query1 " + query1);

                    rs1 = ConnectionDAO.executerQuery(query1, cid);
                    while (rs1.next()) {
                        lastNETS = Long.parseLong(rs1.getString(1));
                        System.out.println("lastNETS " + lastNETS);
                        lastNEMValue = Long.parseLong(rs1.getString(2));
                        System.out.println("lastNEMValue " + lastNEMValue);

                    }
                    
            
            /*query = "select t.metricvalue,t.category,t.host,t.hostgroup,t.subservice,t.eventid,t.resourcesubtype,t.customerid,t.sla,t.timestamp1,t.timestamp2 from(select customerid,service,resourcetype,resourceid,metrictype,timestamp1 from servicemetrics t1 "
                    + "where t1.resourcetype='" + resourcetype + "' and t1.metrictype ='" + metrictype + "' and t1.customerid=" + customerid + " and t1.service = '" + service + "' and t1.resourceid = '" + resourceid + "' and t1.timestamp1 between " + startEndTS.get(1) + " and " + startEndTS.get(3) + " "
                    + "group by t1.customerid, t1.service, t1.metrictype, t1.resourcetype, t1.resourceid,t1.timestamp1) r inner join servicemetrics t on t.service=r.service "
                    + "and t.resourcetype=r.resourcetype and t.metrictype=r.metrictype and t.timestamp1=r.timestamp1 and t.customerid=r.customerid and t.resourceid =r.resourceid group by t.customerid,"
                    + "t.service,t.resourcetype,t.metrictype,t.metricvalue,t.category,t.resourceid,t.host,t.hostgroup,t.subservice,t.eventid,t.resourcesubtype,t.customerid,t.sla,t.timestamp1,t.timestamp2 order by timestamp1";
        */
                query = "select metricvalue,category,host,hostgroup,subservice,eventid,resourcesubtype,customerid,sla,timestamp1,timestamp2 from servicemetrics where resourcetype='" + resourcetype + "' and metrictype ='"+metrictype+"' and customerid=" + customerid
                            + " and service = '" + service + "' and resourceid = '" + resourceid + "' and timestamp1 >="+lastDETS+" and timestamp1 between "+startEndTS.get(1)+" and "+startEndTS.get(3)+" order by timestamp1";
                    }else{
                            System.out.println("taking day start timestamp ");
                            
                 query = "select metricvalue,category,host,hostgroup,subservice,eventid,resourcesubtype,customerid,sla,timestamp1,timestamp2 from servicemetrics where resourcetype='" + resourcetype + "' and metrictype ='"+metrictype+"' and customerid=" + customerid
                            + " and service = '" + service + "' and resourceid = '" + resourceid +"' and timestamp1 between "+startEndTS.get(1)+" and "+startEndTS.get(3)+" order by timestamp1";
                    }      
            System.out.println(query);
          rs = ConnectionDAO.executerQuery(query, cid);
            
            long firstMValue = 0;
            long cummGen = 0;
            long cummCons = 0;
            long gen = 0;
            
            while (rs.next()) {
                System.out.println("generate mewwwwwwwwwwwwthod");
                status = 1;

                metricvalue = Long.parseLong(rs.getString(1));
                timeStamp = Long.parseLong(rs.getString(10));

                System.out.println("metricvalue " + metricvalue);
                System.out.println("timestamp=" + timeStamp);
                System.out.println("startTS :" + startEndTS.get(1));
                System.out.println("EndTS :" + startEndTS.get(3));
                
                if(timeStamp>(Long)startEndTS.get(1) && timeStamp<(Long)startEndTS.get(3)){
                System.out.println("timestamp is between current day start and end timestamps ");
                }else{
                
                startEndTS = StartAndEndTS.getPeriodEndTS(timeStamp*1000, period);
                System.out.println("startTS :" + startEndTS.get(1));
                System.out.println("EndTS :" + startEndTS.get(3));
                System.out.println("timestamp is not between lastNETS "+lastNETS);
                 System.exit(0);
                lastNEMValue=0;
                i=0;
                lastNETS=0;
                }
                System.out.println("last TS " + lastNETS);

                if (timeStamp > lastNETS) {  //modify 5times

                    category = rs.getString(2);
                    host = rs.getString(3);
                    hostgroup = rs.getString(4);
                    subservice = rs.getString(5);
                    eventid = rs.getString(6);
                    rsubtype = rs.getString(7);
                    customerid = Integer.parseInt(rs.getString(8));
                    sla = Double.parseDouble(rs.getString(9));
                    timeStamp2 = Long.parseLong(rs.getString(11));
                    /*System.out.println("category " + rs.getString(2));
                    System.out.println("host " + rs.getString(3));
                    System.out.println("hostgroup " + rs.getString(4));
                    System.out.println("subservice " + rs.getString(5));
                    System.out.println("eventid " + rs.getString(6));
                    System.out.println("resourcesubtype " + rs.getString(7));
                    System.out.println("customerid " + rs.getString(8));
                    System.out.println("sla" + sla);
                    System.out.println("timestamp2=" + timeStamp2);
                    */
                    if (i == 0 && lastNETS == 0L) {
                        firstMValue = metricvalue;
                        i++;
                        System.out.println("firsttime" + metricvalue);
                        System.out.println("i==" + i);
                        System.out.println(lastDECumValue);
                        cummGen=0;
                        lastECCumValue=0;
                            metrictype = "EnergyConsumed";
                            query = "update servicemetrics SET metricvalue=0 where service='" + service + "' and customerid=" + customerid + " and timestamp1=" + timeStamp + " and timestamp2=" + timeStamp2 + " and metrictype='" + metrictype + "' and resourceid='" + resourceid + "' and resourcetype='" + resourcetype + "';";
                              System.out.println(query);
                            status = 0;
                            status = ConnectionDAO.inserterUpdate(query, cid);
                            if (status > 0) {
                                System.out.println(status + "EnergyConsumed updated values sucefully");

                            } else {
                                metrictype = "EnergyConsumed";
                                query = "insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue,eventid,resourcetype,resourcesubtype,resourceid,sla,customerid) "
                                        + "values('" + host + "','" + hostgroup + "','" + service + "','" + subservice + "'," + timeStamp + "," + timeStamp2 + ",'" + category + "','" + metrictype + "',0,'" + eventid + "','" 
                                        + resourcetype + "','" + rsubtype + "','" + resourceid + "','" + sla + "'," + customerid + ")";

                                status = ConnectionDAO.inserterUpdate(query, cid);
                                System.out.println(query);
                                System.out.println("EnergyConsumed inserted values sucefully");

                            }
                            
                            metrictype = "DayEnergy";
                            query = "update servicemetrics SET metricvalue="+firstMValue+" where service='" + service + "' and customerid=" + customerid + " and timestamp1=" + timeStamp + " and timestamp2=" + timeStamp2 + " and metrictype='" + metrictype + "' and resourceid='" + resourceid + "' and resourcetype='" + resourcetype + "';";
                              System.out.println(query);
                              status = 0;
                            status = ConnectionDAO.inserterUpdate(query, cid);
                            if (status > 0) {
                                System.out.println(status + "DayEnergy updated values sucefully");

                            } else {
                                metrictype = "DayEnergy";
                                query = "insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue,eventid,resourcetype,resourcesubtype,resourceid,sla,customerid) "
                                        + "values('" + host + "','" + hostgroup + "','" + service + "','" + subservice + "'," + timeStamp + "," + timeStamp2 + ",'" + category + "','" + metrictype + "',"+firstMValue+",'" + eventid + "','" 
                                        + resourcetype + "','" + rsubtype + "','" + resourceid + "','" + sla + "'," + customerid + ")";

                                status = ConnectionDAO.inserterUpdate(query, cid);
                                System.out.println(query);
                                System.out.println(" DayEnergy inserted values sucefully");

                            }
                            lastDECumValue = firstMValue;
                    } else {

                        if (i == 0) {
                            firstMValue = lastNEMValue;
                            System.out.println("lastNEMValue" + firstMValue);
                            System.out.println("i==" + i);
                        }
                        i++;
                        System.out.println(i + "pre" + lastDECumValue + "conn" + cummGen);

                        System.out.println("metricvalue" + metricvalue);
                        System.out.println("metricvalue" + firstMValue);

                        gen = metricvalue - firstMValue;
                        if (gen >=0) {
                            System.out.println("gained" + gen);
                            cummGen = lastDECumValue + gen;
                            System.out.println(lastDECumValue);
                            System.out.println(gen);
                           metrictype = "DayEnergy";
                            query = "update servicemetrics SET metricvalue=" + cummGen + " where service='" + service + "' and customerid=" + customerid + " and timestamp1=" + timeStamp + " and timestamp2=" + timeStamp2 + " and metrictype='" + metrictype + "' and resourceid='" + resourceid + "' and resourcetype='" + resourcetype + "';";
                            System.out.println(query);
                            status = 0;
                            status = ConnectionDAO.inserterUpdate(query, cid);
                            if (status > 0) {
                                System.out.println(status + "DayEnergy updated values sucefully");
                                System.out.println(cummGen + "updated values sucefully" + timeStamp);
                            
                            } else {
                                metrictype = "DayEnergy";
                                query = "insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue,eventid,resourcetype,resourcesubtype,resourceid,sla,customerid) "
                                        + "values('" + host + "','" + hostgroup + "','" + service + "','" + subservice + "'," + timeStamp + "," + timeStamp2 + ",'" + category + "','" + metrictype + "',"+cummGen +",'" + eventid + "','" 
                                        + resourcetype + "','" + rsubtype + "','" + resourceid + "','" + sla + "'," + customerid + ")";

                                status = ConnectionDAO.inserterUpdate(query, cid);
                                System.out.println(query);
                                System.out.println(" DayEnergy inserted values sucefully");

                            }
                            lastDECumValue = cummGen;
                            cummCons = lastECCumValue;
                            //cummConsMap.put(timeStamp, cummCons);

                            metrictype = "EnergyConsumed";
                            query = "update servicemetrics SET metricvalue=" + cummCons + " where service='" + service + "' and customerid=" + customerid + " and timestamp1=" + timeStamp + " and timestamp2=" + timeStamp2 + " and metrictype='" + metrictype + "' and resourceid='" + resourceid + "' and resourcetype='" + resourcetype + "';";
                                System.out.println(query);
                            status = 0;
                            status = ConnectionDAO.inserterUpdate(query, cid);
                            if (status > 0) {
                                System.out.println(status + "updated values sucefully");

                            } else {
                                metrictype = "EnergyConsumed";
                                query = "insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue,eventid,resourcetype,resourcesubtype,resourceid,sla,customerid) "
                                        + "values('" + host + "','" + hostgroup + "','" + service + "','" + subservice + "'," + timeStamp + "," + timeStamp2 + ",'" + category + "','" + metrictype + "','"
                                        + cummCons + "','" + eventid + "','" + resourcetype + "','" + rsubtype + "','" + resourceid + "','" + sla + "'," + customerid + ")";

                                status = ConnectionDAO.inserterUpdate(query, cid);
                                System.out.println(query);
                                System.out.println("inserted values sucefully");

                            }
                            
                        } else {
                            System.out.println("Lossed" + gen);
                            gen=firstMValue-metricvalue;
                            cummCons = lastECCumValue + gen;
                            System.out.println("Lossed" + cummCons);
                            metrictype = "EnergyConsumed";
                            query = "update servicemetrics SET metricvalue=" + cummCons + " where service='" + service + "' and customerid=" + customerid + " and timestamp1=" + timeStamp + " and timestamp2=" + timeStamp2 + " and metrictype='" + metrictype + "' and resourceid='" + resourceid + "' and resourcetype='" + resourcetype + "';";
                             System.out.println(query);
                            status = 0;
                            status = ConnectionDAO.inserterUpdate(query, cid);
                            if (status > 0) {
                                System.out.println(status + "updated values sucefully");

                            } else {
                                metrictype = "EnergyConsumed";
                                query = "insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue,eventid,resourcetype,resourcesubtype,resourceid,sla,customerid) "
                                        + "values('" + host + "','" + hostgroup + "','" + service + "','" + subservice + "'," + timeStamp + "," + timeStamp2 + ",'" + category + "','" + metrictype + "','"
                                        + cummCons + "','" + eventid + "','" + resourcetype + "','" + rsubtype + "','" + resourceid + "','" + sla + "'," + customerid + ")";

                                status = ConnectionDAO.inserterUpdate(query, cid);
                                System.out.println(query);
                                System.out.println("inserted values sucefully");

                            }

                            lastECCumValue = cummCons;
                            cummGen = lastDECumValue;
                            System.out.println(lastDECumValue);
                            System.out.println(lastECCumValue);
                            metrictype = "DayEnergy";
                            query = "update servicemetrics SET metricvalue=" + cummGen + " where service='" + service + "' and customerid=" + customerid + " and timestamp1=" + timeStamp + " and timestamp2=" + timeStamp2 + " and metrictype='" + metrictype + "' and resourceid='" + resourceid + "' and resourcetype='" + resourcetype + "';";
                            System.out.println(query);
                            status = 0;
                            status = ConnectionDAO.inserterUpdate(query, cid);
                            
                            if (status > 0) {
                                System.out.println(status + "DayEnergy updated values sucefully");
                                System.out.println(cummGen + "updated values sucefully" + timeStamp);
                            
                            } else {
                                metrictype = "DayEnergy";
                                query = "insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue,eventid,resourcetype,resourcesubtype,resourceid,sla,customerid) "
                                        + "values('" + host + "','" + hostgroup + "','" + service + "','" + subservice + "'," + timeStamp + "," + timeStamp2 + ",'" + category + "','" + metrictype + "',"+cummGen +",'" + eventid + "','" 
                                        + resourcetype + "','" + rsubtype + "','" + resourceid + "','" + sla + "'," + customerid + ")";

                                status = ConnectionDAO.inserterUpdate(query, cid);
                                System.out.println(query);
                                System.out.println(" DayEnergy inserted values sucefully");

                            }
                         
                        }
                        firstMValue = metricvalue;
                    }
                    System.out.println("i==>" + i);
                    System.out.println("lastNETS==>" + lastNETS);
                    
                  } else {
                        System.out.println("Same timestamp1" +timeStamp);
                }
                
                
            }   System.out.println("Installation"+service);
                System.out.println("Resourceid"+resourceid);
        } catch (PSQLException ex) {
            ex.printStackTrace();
            logger.error(ex);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e);
        } finally {

            try {

                if (rs != null) {
                    rs.close();
                    rs = null;
                }if (rs1 != null) {
                    rs1.close();
                    rs1 = null;
                }
                    query1 = null;
                
                    query = null;
                    query1 = null;
                   // splite1 = null;
                //} //else if (splite2 != null) {
                    //splite2 = null;
               // }
                if (startEndTS != null) {
                    startEndTS.clear();
                    startEndTS = null;
                    propertyFile = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e);
            }

        }

    }

    public static void main(String[] args) throws PSQLException, InterruptedException {
        String[] forDate = null;
         Calendar toDayDate=null;
         String[] zDateSplite=null,spliteTime=null,spliteDate=null;
            toDayDate = Calendar.getInstance();
        try {
             PropertyConfigurator.configure("log4j.properties");
            String curTime =null;
            System.out.println("curTime " + curTime);
            TimeLineConsumedGeneratorForGivenTS tl = new TimeLineConsumedGeneratorForGivenTS("config" + File.separator + "MetricTypes.properties");
            for (String arg : args) {
                if (arg.contains("--forDate")) {
                    forDate = arg.split("=");
                    curTime =forDate[1];
                }
            }
            initialize();
            if (curTime!=null) {
                curDayflag=true;
              zDateSplite = curTime.split("_");
             System.out.println(zDateSplite[0]);
             System.out.println(zDateSplite[1]);
             spliteDate=zDateSplite[0].split("-");
             spliteTime=zDateSplite[1].split(":");
             toDayDate.set(Integer.parseInt(spliteDate[2]),(Integer.parseInt(spliteDate[1])-1),Integer.parseInt(spliteDate[0]));
             toDayDate.set(Calendar.HOUR_OF_DAY,Integer.parseInt(spliteTime[0]));
             toDayDate.set(Calendar.MINUTE,Integer.parseInt(spliteTime[1]));
             toDayDate.set(Calendar.SECOND,Integer.parseInt(spliteTime[2]));
             System.out.println(toDayDate.getTime());
             System.out.println((toDayDate.getTimeInMillis()/1000));
             System.out.println("taking curDayflag " + curDayflag);
            generateAggregateValues((toDayDate.getTimeInMillis()/1000));
            }else{
             System.out.println("Plz enter Date in controllerscript file");
            
            }
        } catch (IOException io) {
            io.printStackTrace();
            logger.error(io);
        } finally {
            if (forDate != null) {
                forDate = null;
            }

        }
    }
}
