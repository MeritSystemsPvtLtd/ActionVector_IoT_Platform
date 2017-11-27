/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.timelineconsumedgenerator;
import com.prokosha.dbconnection.ConnectionDAO;
import com.prokosha.timelineconsumedgenerator.ts.StartAndEndTS;
import com.prokosha.timelineconsumedgeneratorconfiguration.TimeLineConsumedGeneratorConfiguration;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class TimeLineConsumedGenerator {
     private static Logger logger = Logger.getLogger(TimeLineConsumedGenerator.class.getName());
    public static String propertyFile = null;
    public static String cid = null;
    public static boolean curDayflag=false;
    public static HashMap deltaGen = new LinkedHashMap();
    public static HashMap deltaCons = new LinkedHashMap();
    public static HashMap cummGenMap = new LinkedHashMap();
    public static HashMap cummConsMap = new LinkedHashMap();

    public TimeLineConsumedGenerator(String propertyFileName) {

        logger.info("Loading the property file" + propertyFileName);

        this.propertyFile = propertyFileName;
    }

    public static void initialize() throws IOException {

        if (!TimeLineConsumedGeneratorConfiguration.loadProperties(propertyFile)) {
            logger.info("Error in loading property file");
            System.exit(0);
        }

    }

    public static void generateAggregateValues(long periodTS) throws IOException {

        String query = "select distinct resourceid,service,resourcetype,customerid from hostinfo";
        cid = TimeLineConsumedGeneratorConfiguration.getCustomerId();
        ResultSet rset = ConnectionDAO.executerQuery(query, cid);
        String metricType = TimeLineConsumedGeneratorConfiguration.getMetricType();
        String period = TimeLineConsumedGeneratorConfiguration.getPeriod();
            
        //long periodTS=Long.getLong("periodTS");

        String resourceId = null;
        String service = null;
        String resourceType = null;
        int customerId;
        logger.info("in periodTS " + periodTS);

        try {
            while (rset.next()) {
                resourceId = rset.getString(1);
                service = rset.getString(2);
                resourceType = rset.getString(3);
                customerId = Integer.parseInt(rset.getString(4));
                logger.info("sevice " + service);
                logger.info("resourceid " + resourceId);
                logger.info("resourcetype " + resourceType);
                logger.info("customerid " + customerId);
                logger.info("metricType " + metricType);
                    generateConsumedGeneratedValues(periodTS, period, resourceType, resourceId, metricType, customerId, service);
            }

            logger.info("generation is over");

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
        logger.info("epochtime"+epochtime);
        
        List startEndTS=null;
        startEndTS= StartAndEndTS.getPeriodEndTS(epochtime, period);
        logger.info("startEndTS :" + startEndTS.get(1));
        logger.info("startEndTS :" + startEndTS.get(3));
        logger.info("startEndTS :" + startEndTS);
        String timestamp1 = (String) startEndTS.get(0);
        int week = (int) startEndTS.get(4);
        String[] splite1 = timestamp1.split("/");
        int startDay = Integer.parseInt(splite1[0]);
        logger.info("startday" + startDay);
        String timestamp2 = (String) startEndTS.get(2);
        String[] splite2 = timestamp2.split("/");
        int lastDay = Integer.parseInt(splite2[0]);
        logger.info("lastDay" + lastDay);
        logger.info("mon" + splite1[1]);
        logger.info("year" + splite1[2].substring(0, 4));
        HashMap map = new LinkedHashMap();
        ResultSet rs = null, rs1 = null;
        String query = null, query1 = null;
        try {

            long metricvalue = 0;
            long maxNEMValue = 0;
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
            long maxNETS = 0L;
             long maxECTS = 0L;
            long maxDETS=0;
             long maxDECumValue = 0;
             long maxECCumValue = 0;
                    
                    query1 = "select t.timestamp1,t.metricvalue from(select customerid,service,resourcetype,resourceid,metrictype, max(timestamp1) "
                            + "as MaxTime from servicemetrics t1 where t1.resourcetype='" + resourcetype + "' and t1.metrictype ='DayEnergy' and t1.customerid=" + customerid
                            + " and t1.service = '" + service + "' and t1.resourceid = '" + resourceid + "' group by t1.customerid, t1.service, t1.metrictype, t1.resourcetype, t1.resourceid) r inner join servicemetrics t on t.service=r.service and t.resourcetype=r.resourcetype and t.timestamp1 = r.MaxTime and t.metrictype=r.metrictype and t.customerid=r.customerid and t.resourceid =r.resourceid "
                            + "group by t.customerid,t.service,t.resourcetype,t.timestamp1,t.metrictype,t.metricvalue,t.resourceid";
                    logger.info("query1 " + query1);

                    rs1 = ConnectionDAO.executerQuery(query1, cid);
                    while (rs1.next()) {
                        maxDETS = Long.parseLong(rs1.getString(1));
                        logger.info("maxDETS " + maxDETS);
                        maxDECumValue = Long.parseLong(rs1.getString(2));
                        logger.info("maxDECumValue cumm day energy value " + maxDECumValue);

                    }
                    query1 = "select t.timestamp1,t.metricvalue from(select customerid,service,resourcetype,resourceid,metrictype, max(timestamp1) "
                            + "as MaxTime from servicemetrics t1 where t1.resourcetype='" + resourcetype + "' and t1.metrictype ='EnergyConsumed' and t1.customerid=" + customerid
                            + " and t1.service = '" + service + "' and t1.resourceid = '" + resourceid + "'and t1.timestamp1 ="+maxDETS+" group by t1.customerid, t1.service, t1.metrictype, t1.resourcetype, t1.resourceid) r inner join servicemetrics t on t.service=r.service and t.resourcetype=r.resourcetype and t.timestamp1 = r.MaxTime and t.metrictype=r.metrictype and t.customerid=r.customerid and t.resourceid =r.resourceid "
                            + "group by t.customerid,t.service,t.resourcetype,t.timestamp1,t.metrictype,t.metricvalue,t.resourceid";
                    logger.info("query1 " + query1);

                    rs1 = ConnectionDAO.executerQuery(query1, cid);
                    while (rs1.next()) {
                        maxECTS = Long.parseLong(rs1.getString(1));
                        logger.info("energyconsumed lastts " + maxECTS);
                        maxECCumValue = Long.parseLong(rs1.getString(2));
                        logger.info("maxECCumValue cumm day energy value " + maxECCumValue);

                    }
                    query1 = "select timestamp1,metricvalue from servicemetrics t1 where t1.resourcetype='" + resourcetype + "' and t1.metrictype ='"+metrictype+"' and t1.customerid=" + customerid
                            + " and t1.service = '" + service + "' and t1.timestamp1 = " + maxDETS +" and t1.resourceid = '" + resourceid + "' group by t1.customerid,t1.service,t1.resourcetype,t1.timestamp1,t1.metrictype,t1.metricvalue,t1.resourceid";
                    logger.info("query1 " + query1);
                    rs1 = ConnectionDAO.executerQuery(query1, cid);
                    while (rs1.next()) {
                        maxNETS = Long.parseLong(rs1.getString(1));
                        logger.info("maxNETS " + maxNETS);
                        maxNEMValue = Long.parseLong(rs1.getString(2));
                        logger.info("maxNEMValue " + maxNEMValue);

                    }
                    
        
            query = "select t.metricvalue,t.category,t.host,t.hostgroup,t.subservice,t.eventid,t.resourcesubtype,t.customerid,t.sla,t.timestamp1,t.timestamp2 from(select customerid,service,resourcetype,resourceid,metrictype,timestamp1 from servicemetrics t1 "
                    + "where t1.resourcetype='" + resourcetype + "' and t1.metrictype ='" + metrictype + "' and t1.customerid=" + customerid + " and t1.service = '" + service + "' and t1.resourceid = '" + resourceid + "' and t1.timestamp1 >=" + maxDETS + " "
                    + "group by t1.customerid, t1.service, t1.metrictype, t1.resourcetype, t1.resourceid,t1.timestamp1) r inner join servicemetrics t on t.service=r.service "
                    + "and t.resourcetype=r.resourcetype and t.metrictype=r.metrictype and t.timestamp1=r.timestamp1 and t.customerid=r.customerid and t.resourceid =r.resourceid group by t.customerid,"
                    + "t.service,t.resourcetype,t.metrictype,t.metricvalue,t.category,t.resourceid,t.host,t.hostgroup,t.subservice,t.eventid,t.resourcesubtype,t.customerid,t.sla,t.timestamp1,t.timestamp2 order by timestamp1";

            logger.info(query);
            
            rs = ConnectionDAO.executerQuery(query, cid);
            int i = 0;
            long firstMValue = 0;
            long cummGen = 0;
            long cummCons = 0;
            long gen = 0;
            boolean flag=false;
            //int ii = 0;
            // int deltaCons;
            
            while (rs.next()) {
                logger.info("generate mewwwwwwwwwwwwthod");
                status = 1;
                metricvalue = Long.parseLong(rs.getString(1));
                timeStamp = Long.parseLong(rs.getString(10));
                logger.info("metricvalue " + metricvalue);
                logger.info("timestamp=" + timeStamp);
                logger.info("starting DayTS :" + startEndTS.get(1));
                logger.info("Ending DayTS :" + startEndTS.get(3));
                if(timeStamp>(Long)startEndTS.get(1) && timeStamp<(Long)startEndTS.get(3)){
                logger.info("timestamp is between current day start and end timestamps ");
                    flag=false;
               
                }else{
                startEndTS = StartAndEndTS.getPeriodEndTS(timeStamp*1000, period);
                logger.info("startTS :" + startEndTS.get(1));
                logger.info("EndTS :" + startEndTS.get(3));
                logger.info("timestamp is not between maxNETS "+maxNETS);
                //if(flag==false){
                maxNEMValue=0;
                i=0;
                maxNETS=0;
                logger.info("flag :" + flag);
              /*  }else{
                   if (maxNETS == 0L) {
                    query1 = "select t.timestamp1,t.metricvalue from(select customerid,service,resourcetype,resourceid,metrictype, max(timestamp1) "
                            + "as MaxTime from servicemetrics t1 where t1.resourcetype='" + resourcetype + "' and t1.metrictype ='NetEnergy' and t1.customerid=" + customerid
                            + " and t1.service = '" + service + "' and t1.resourceid = '" + resourceid + "' and t1.timestamp1 between " + startEndTS.get(1) + " and " + startEndTS.get(3) + " group by t1.customerid, t1.service, t1.metrictype, t1.resourcetype, t1.resourceid) r inner join servicemetrics t on t.service=r.service and t.resourcetype=r.resourcetype and t.timestamp1 = r.MaxTime and t.metrictype=r.metrictype and t.customerid=r.customerid and t.resourceid =r.resourceid "
                            + "group by t.customerid,t.service,t.resourcetype,t.timestamp1,t.metrictype,t.metricvalue,t.resourceid";
                    logger.info("query1 " + query1);

                    rs1 = ConnectionDAO.executerQuery(query1, cid);
                    while (rs1.next()) {
                        maxNETS = Long.parseLong(rs1.getString(1));
                        logger.info("ts " + maxNETS);
                        maxNEMValue = Long.parseLong(rs1.getString(2));
                        logger.info("maxNEMValue " + maxNEMValue);
                        
                    }
                } 
                
                }*/
                }
                
                logger.info("last TS " + maxDETS);

                if (timeStamp > maxDETS) {  //modify 5times

                    category = rs.getString(2);
                    host = rs.getString(3);
                    hostgroup = rs.getString(4);
                    subservice = rs.getString(5);
                    eventid = rs.getString(6);
                    rsubtype = rs.getString(7);
                    customerid = Integer.parseInt(rs.getString(8));
                    sla = Double.parseDouble(rs.getString(9));
                    timeStamp2 = Long.parseLong(rs.getString(11));
                    /*logger.info("category " + rs.getString(2));
                    logger.info("host " + rs.getString(3));
                    logger.info("hostgroup " + rs.getString(4));
                    logger.info("subservice " + rs.getString(5));
                    logger.info("eventid " + rs.getString(6));
                    logger.info("resourcesubtype " + rs.getString(7));
                    logger.info("customerid " + rs.getString(8));
                    logger.info("sla" + sla);
                    logger.info("timestamp2=" + timeStamp2);
                    */
                    if (i == 0 && maxNETS == 0L) {
                        firstMValue = metricvalue;
                        i++;
                        //maxDECumValue = metricvalue;
                        logger.info("firsttime" + metricvalue);
                        logger.info("i==" + i);
                        logger.info(maxDECumValue);
                        cummGen=0;
                        maxECCumValue=0;
                        flag=true;
                            metrictype = "EnergyConsumed";
                            query = "update servicemetrics SET metricvalue=0 where service='" + service + "' and customerid=" + customerid + " and timestamp1=" + timeStamp + " and timestamp2=" + timeStamp2 + " and metrictype='" + metrictype + "' and resourceid='" + resourceid + "' and resourcetype='" + resourcetype + "';";
                            status = 0;
                            status = ConnectionDAO.inserterUpdate(query, cid);
                            if (status > 0) {
                                logger.info(status + "EnergyConsumed updated values sucefully");

                            } else {
                                metrictype = "EnergyConsumed";
                                query = "insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue,eventid,resourcetype,resourcesubtype,resourceid,sla,customerid) "
                                        + "values('" + host + "','" + hostgroup + "','" + service + "','" + subservice + "'," + timeStamp + "," + timeStamp2 + ",'" + category + "','" + metrictype + "',0,'" + eventid + "','" 
                                        + resourcetype + "','" + rsubtype + "','" + resourceid + "','" + sla + "'," + customerid + ")";

                                status = ConnectionDAO.inserterUpdate(query, cid);
                                logger.info(query);
                                logger.info("EnergyConsumed inserted values sucefully");

                            }
                            
                            metrictype = "DayEnergy";
                            query = "update servicemetrics SET metricvalue="+firstMValue+" where service='" + service + "' and customerid=" + customerid + " and timestamp1=" + timeStamp + " and timestamp2=" + timeStamp2 + " and metrictype='" + metrictype + "' and resourceid='" + resourceid + "' and resourcetype='" + resourcetype + "';";
                            status = 0;
                            status = ConnectionDAO.inserterUpdate(query, cid);
                            if (status > 0) {
                                logger.info(status + "DayEnergy updated values sucefully");

                            } else {
                                metrictype = "DayEnergy";
                                query = "insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue,eventid,resourcetype,resourcesubtype,resourceid,sla,customerid) "
                                        + "values('" + host + "','" + hostgroup + "','" + service + "','" + subservice + "'," + timeStamp + "," + timeStamp2 + ",'" + category + "','" + metrictype + "',"+firstMValue+",'" + eventid + "','" 
                                        + resourcetype + "','" + rsubtype + "','" + resourceid + "','" + sla + "'," + customerid + ")";

                                status = ConnectionDAO.inserterUpdate(query, cid);
                                logger.info(query);
                                logger.info(" DayEnergy inserted values sucefully");

                            }
                         maxDECumValue=firstMValue;  
                        
                    } else {

                        if (i == 0) {
                            firstMValue = maxNEMValue;
                            logger.info("maxNEMValue" + firstMValue);
                            logger.info("i==" + i);
                        }
                        i++;
                        logger.info(i + "pre" + maxDECumValue + "conn" + cummGen);

                        logger.info("metricvalue" + metricvalue);
                        logger.info("metricvalue" + firstMValue);
                        gen = metricvalue - firstMValue;
                        if (gen >=0) {
                            logger.info("gained" + gen);
                            
                            cummGen = maxDECumValue + gen;
                            logger.info(maxDECumValue);
                            logger.info(gen);
                            
                            metrictype = "DayEnergy";
                            query = "update servicemetrics SET metricvalue=" + cummGen + " where service='" + service + "' and customerid=" + customerid + " and timestamp1=" + timeStamp + " and timestamp2=" + timeStamp2 + " and metrictype='" + metrictype + "' and resourceid='" + resourceid + "' and resourcetype='" + resourcetype + "';";
                            logger.info(query);
                            status = 0;
                            status = ConnectionDAO.inserterUpdate(query, cid);
                            if (status > 0) {
                                logger.info(status + "DayEnergy updated values sucefully");
                                logger.info(cummGen + "updated values sucefully" + timeStamp);
                            
                            } else {
                                metrictype = "DayEnergy";
                                query = "insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue,eventid,resourcetype,resourcesubtype,resourceid,sla,customerid) "
                                        + "values('" + host + "','" + hostgroup + "','" + service + "','" + subservice + "'," + timeStamp + "," + timeStamp2 + ",'" + category + "','" + metrictype + "',"+cummGen +",'" + eventid + "','" 
                                        + resourcetype + "','" + rsubtype + "','" + resourceid + "','" + sla + "'," + customerid + ")";

                                status = ConnectionDAO.inserterUpdate(query, cid);
                                logger.info(query);
                                logger.info(" DayEnergy inserted values sucefully");

                            }
                            maxDECumValue = cummGen;
                            cummCons = maxECCumValue;
                           
                            metrictype = "EnergyConsumed";
                            query = "update servicemetrics SET metricvalue=" + cummCons + " where service='" + service + "' and customerid=" + customerid + " and timestamp1=" + timeStamp + " and timestamp2=" + timeStamp2 + " and metrictype='" + metrictype + "' and resourceid='" + resourceid + "' and resourcetype='" + resourcetype + "';";
                            status = 0;
                            status = ConnectionDAO.inserterUpdate(query, cid);
                            if (status > 0) {
                                logger.info(status + "updated values sucefully");

                            } else {
                                metrictype = "EnergyConsumed";
                                query = "insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue,eventid,resourcetype,resourcesubtype,resourceid,sla,customerid) "
                                        + "values('" + host + "','" + hostgroup + "','" + service + "','" + subservice + "'," + timeStamp + "," + timeStamp2 + ",'" + category + "','" + metrictype + "','"
                                        + cummCons + "','" + eventid + "','" + resourcetype + "','" + rsubtype + "','" + resourceid + "','" + sla + "'," + customerid + ")";

                                status = ConnectionDAO.inserterUpdate(query, cid);
                                logger.info(query);
                                logger.info("inserted values sucefully");

                            }
                         } else {
                            logger.info("Lossed" + gen);
                            gen=firstMValue-metricvalue;
                            
                            cummCons = maxECCumValue + gen;
                            logger.info("Lossed" + cummCons);
                            metrictype = "EnergyConsumed";
                            query = "update servicemetrics SET metricvalue=" + cummCons + " where service='" + service + "' and customerid=" + customerid + " and timestamp1=" + timeStamp + " and timestamp2=" + timeStamp2 + " and metrictype='" + metrictype + "' and resourceid='" + resourceid + "' and resourcetype='" + resourcetype + "';";
                                logger.info(query);
                            status = 0;
                            status = ConnectionDAO.inserterUpdate(query, cid);
                            if (status > 0) {
                                logger.info(status + "updated values sucefully");

                            } else {
                                metrictype = "EnergyConsumed";
                                query = "insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue,eventid,resourcetype,resourcesubtype,resourceid,sla,customerid) "
                                        + "values('" + host + "','" + hostgroup + "','" + service + "','" + subservice + "'," + timeStamp + "," + timeStamp2 + ",'" + category + "','" + metrictype + "','"
                                        + cummCons + "','" + eventid + "','" + resourcetype + "','" + rsubtype + "','" + resourceid + "','" + sla + "'," + customerid + ")";

                                status = ConnectionDAO.inserterUpdate(query, cid);
                                logger.info(query);
                                logger.info("inserted values sucefully");

                            }

                            maxECCumValue = cummCons;
                            cummGen = maxDECumValue;
                            logger.info(maxDECumValue);
                            logger.info(maxECCumValue);

                            metrictype = "DayEnergy";
                            query = "update servicemetrics SET metricvalue=" + cummGen + " where service='" + service + "' and customerid=" + customerid + " and timestamp1=" + timeStamp + " and timestamp2=" + timeStamp2 + " and metrictype='" + metrictype + "' and resourceid='" + resourceid + "' and resourcetype='" + resourcetype + "';";
                            logger.info(query);
                            status = 0;
                            status = ConnectionDAO.inserterUpdate(query, cid);
                            
                            if (status > 0) {
                                logger.info(status + "DayEnergy updated values sucefully");
                                logger.info(cummGen + "updated values sucefully" + timeStamp);
                            
                            } else {
                                metrictype = "DayEnergy";
                                query = "insert into servicemetrics(host,hostgroup,service,subservice,timestamp1,timestamp2,category,metrictype,metricvalue,eventid,resourcetype,resourcesubtype,resourceid,sla,customerid) "
                                        + "values('" + host + "','" + hostgroup + "','" + service + "','" + subservice + "'," + timeStamp + "," + timeStamp2 + ",'" + category + "','" + metrictype + "',"+cummGen +",'" + eventid + "','" 
                                        + resourcetype + "','" + rsubtype + "','" + resourceid + "','" + sla + "'," + customerid + ")";

                                status = ConnectionDAO.inserterUpdate(query, cid);
                                logger.info(query);
                                logger.info(" DayEnergy inserted values sucefully");

                            }
                          
                        }
                        firstMValue = metricvalue;
                    }
                    logger.info("i==>" + i);
                    logger.info("maxNETS==>" + maxNETS);
                    } else {

                    maxNEMValue = metricvalue;
                    logger.info("else maxNEMValue" + maxNEMValue);
                }
                
                
            } 
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
        try {
             PropertyConfigurator.configure("log4j.properties");
             
            TimeLineConsumedGenerator tl = new TimeLineConsumedGenerator("config" + File.separator + "MetricTypes.properties");
            initialize();
            logger.info("taking System current Time ");
            generateAggregateValues(System.currentTimeMillis());

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
