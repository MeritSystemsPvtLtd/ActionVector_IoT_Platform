/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.timelineaggregator;

import com.prokosha.dbconnection.ConnectionDAO;
import com.prokosha.timelineaggregator.comfiguration.TimeLineAggregatorConfiguration;
import com.prokosha.timelineaggregator.hourts.StartAndEndHourTS;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.postgresql.util.PSQLException;

/**
 *
 * @author yedukondalu
 */
public class TimeLineAggregator {
    
    private static Logger logger = Logger.getLogger(TimeLineAggregator.class.getName());
    public static String propertyFile = null;
    public static String cid = null;
    public static String JSONResourceConfig = null;
    
    public TimeLineAggregator(String propertyFileName) {

        logger.info("Loading the property file : " + propertyFileName);

        this.propertyFile = propertyFileName;
    }

    public static void initialize() throws IOException {

        if (!TimeLineAggregatorConfiguration.loadProperties(propertyFile)) {
            logger.info("Error in loading property file");
            System.exit(0);
        }

    }

    public static void generateAggregateValues(long periodTS) throws IOException {

        String query = "select distinct resourceid,service,resourcetype,customerid from hostinfo";
        cid = TimeLineAggregatorConfiguration.getCustomerId();
        JSONResourceConfig=TimeLineAggregatorConfiguration.getJSONResourceConfig();
        ResultSet rset = ConnectionDAO.executerQuery(query, cid);
        String metricType = TimeLineAggregatorConfiguration.getMetricType();
        String period = TimeLineAggregatorConfiguration.getPeriod();
           String resourceId=null;
        String service=null;
        String resourceType=null;
        int customerId;
       logger.info("in generateAggregateValues method " + periodTS);

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
                getAggregateValue(periodTS, period, resourceType, resourceId, metricType, customerId, service);
               
            }
} catch (SQLException ex) {
            ex.printStackTrace();
            logger.error(ex);
        } finally {
            try {
                if (rset != null) {
                    rset.close();
                    rset = null;
                }
                query = null;
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                logger.error(ex);

            }

        }

    }

    
    public static void getAggregateValue(Long epochtime, String period, String resourcetype, String resourceid, String metrictype, int customerid, String service) throws IOException {

        logger.info("in getAggregateValue method");
        List startEndTS = StartAndEndHourTS.getPeriodEndTS(epochtime, period);
        logger.info("startEndTS :" + startEndTS);
        String timestamp1 = (String) startEndTS.get(2);
        
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
            String[] hour1=timestamp1.split(" ");
           String hr=hour1[1].substring(0,hour1[1].length()-6);
            int HH=Integer.parseInt(hr);
            logger.info("hour "+HH);
            
       
        ResultSet rs = null;
        String query = null;
        String url=null;
        Date epochDate=new Date(epochtime);
        Calendar sunHrStart = Calendar.getInstance();
        sunHrStart.setTime(epochDate);
        Calendar sunHrEnd = Calendar.getInstance();
        sunHrEnd.setTime(epochDate);
       String[] sunHoursStart1=null;
       String[] sunHoursEnd1=null;
       String sunHoursStart=null;
       String sunHoursEnd=null;
       long sunHrStartMillis=0;
       long sunHrEndMillis=0;
        try {

            double metricvalue = 0;
            String host = null;
            String hostgroup = null;
            String subservice = null;
            String category = null;
            String eventid = null;
            String rsubtype = null;
            Double sla = 0.0;
            int status = 0;
            String cCustomer=null;
             query="select customername from customerinfo where id="+customerid;
             
              rs = ConnectionDAO.executerQuery(query, cid);
            logger.info(query);

            while (rs.next()) {
                cCustomer = rs.getString("customername");
                 logger.info("customername "+cCustomer);
            
            }
            try{
         url=JSONResourceConfig+"customer="+cCustomer+"&service="+service+"&subservice=Default&paramname=SunLightStart";
        logger.info("url "+url);
         sunHoursStart =TimeLineAggregator.urlResopnes(url);
        sunHoursStart1 = sunHoursStart.split(":");

        sunHrStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sunHoursStart1[0]));
        sunHrStart.set(Calendar.MINUTE, Integer.parseInt(sunHoursStart1[1]));
        sunHrStart.set(Calendar.SECOND,00);
        logger.info("sunHrStart time" + sunHrStart.getTime());
        sunHrStartMillis = sunHrStart.getTimeInMillis();
        logger.info("sunHrStartMillis" + (sunHrStartMillis));

          url=JSONResourceConfig+"customer="+cCustomer+"&service="+service+"&subservice=Default&paramname=SunLightEnd";
        logger.info("url "+url);
         sunHoursEnd = TimeLineAggregator.urlResopnes(url);
        sunHoursEnd1 = sunHoursEnd.split(":");

        sunHrEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sunHoursEnd1[0]));
        sunHrEnd.set(Calendar.MINUTE, Integer.parseInt(sunHoursEnd1[1]));
        sunHrEnd.set(Calendar.SECOND,00);
        logger.info("sunHrEnd time" + sunHrEnd.getTime());
        sunHrEndMillis = sunHrEnd.getTimeInMillis();
        logger.info("sunHrEndMillis" + sunHrEndMillis);
        logger.info("ts0" +(Long)startEndTS.get(1)*1000  +" >= "+sunHrStartMillis);
        logger.info("ts1" +((Long)startEndTS.get(3)*1000)  +" <= "+sunHrEndMillis) ;
            }catch(Exception e){
            e.printStackTrace();
            logger.error(e);
            }
            if(sunHrStartMillis>0 && sunHrEndMillis>0){
        if(((Long)startEndTS.get(1)*1000)>=sunHrStartMillis && ((Long)startEndTS.get(3)*1000)<=sunHrEndMillis){
        logger.info("in between sun hours");
        
          query = "select t.metricvalue,t.category,t.host,t.hostgroup,t.subservice,t.eventid,t.resourcesubtype,t.customerid,t.sla from(select customerid,service,resourcetype,resourceid,metrictype,Max(timestamp1) as MaxTime from servicemetrics t1 "
                    + "where t1.resourcetype='" + resourcetype + "' and t1.metrictype ='" + metrictype + "' and t1.customerid=" + customerid + " and t1.service = '" + service + "' and t1.resourceid = '" + resourceid + "' and t1.timestamp1 between " + startEndTS.get(1) + " and " + startEndTS.get(3) + " "
                    + "group by t1.customerid, t1.service, t1.metrictype, t1.resourcetype, t1.resourceid) r inner join servicemetrics t on t.service=r.service "
                    + "and t.resourcetype=r.resourcetype and t.timestamp1 = r.MaxTime and t.metrictype=r.metrictype and t.customerid=r.customerid and t.resourceid =r.resourceid group by t.customerid,"
                    + "t.service,t.resourcetype,t.metrictype,t.metricvalue,t.category,t.resourceid,t.host,t.hostgroup,t.subservice,t.eventid,t.resourcesubtype,t.customerid,t.sla";
        status = 1;
                }
        
        /*else if(((Long)startEndTS.get(1)*1000)>=sunHrStartMillis){
        logger.info("not in between sun hours");
        metrictype = "Energy";
        
            query=null;
            query="select metricvalue,category,host,hostgroup,subservice,eventid,resourcesubtype,customerid,sla,hour from timewisederivedmetrics "
                    + "where resourcetype='" + resourcetype + "' and metrictype='" + metrictype + "' and customerid=" + customerid + " and service ='" + service + "' and resourceid ='" + resourceid + "' and day="+lastDay
                    +"and month='"+splite1[1]+"' and year="+splite1[2].substring(0, 4)+"and hour=(select max(hour) from timewisederivedmetrics where hour < "+ HH +" and service ='" + service + "' and resourceid ='" + resourceid + "' and day="+lastDay
                    +"and month='"+splite1[1]+"' and year="+splite1[2].substring(0, 4)+" ) group by customerid,service,resourcetype,metrictype,metricvalue,category,resourceid,host,hostgroup,subservice,eventid,resourcesubtype,customerid,sla,hour order by hour DESC limit 1";
        status = 1;
                
        }*/
            rs = ConnectionDAO.executerQuery(query, cid);
            logger.info(query);
            if (status > 0) {
            while (rs.next()) {
                metricvalue = Double.parseDouble(rs.getString(1));
                category = rs.getString(2);
                host = rs.getString(3);
                hostgroup = rs.getString(4);
                subservice = rs.getString(5);
                eventid = rs.getString(6);
                rsubtype = rs.getString(7);
                customerid = Integer.parseInt(rs.getString(8));
                sla = Double.parseDouble(rs.getString(9));
                logger.info("metricvalue " + metricvalue);
                logger.info("category " + rs.getString(2));
                logger.info("host " + rs.getString(3));
                logger.info("hostgroup " + rs.getString(4));
                logger.info("subservice " + rs.getString(5));
                logger.info("eventid " + rs.getString(6));
                logger.info("resourcesubtype " + rs.getString(7));
                logger.info("customerid " + rs.getString(8));
                logger.info("sla" + sla);
                

            }
                
                if (metrictype.equalsIgnoreCase("DayEnergy")) {
                    metrictype = "Energy";
                }
                if(host!=null){
                query = "update timewisederivedmetrics SET metricvalue=" + metricvalue + " where host='" + host + "' and day=" + splite1[0] + " and month='" + splite1[1] + "' and year=" + splite1[2].substring(0, 4) + " and metrictype='" + metrictype + "' and resourceid='" + resourceid + "' and resourcetype='" + resourcetype + "'and week=" + week+" and hour="+HH;
                logger.info(query);
                try {
                    status = ConnectionDAO.inserterUpdate(query, cid);
                    logger.info("status" + status);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("error in updation" + e);
                    status = 0;
                }
                if (status > 0) {
                    logger.info("updated values sucefully");
                } else {
                    logger.info("adding column into table");
                    if (metrictype.equalsIgnoreCase("DayEnergy")) {
                        metrictype = "Energy";
                    }
                    query = "insert into timewisederivedmetrics(host,hostgroup,service,subservice,day,month,year,category,metrictype,metricvalue,eventid,resourcetype,resourcesubtype,resourceid,sla,customerid,week,hour) "
                            + "values('" + host + "','" + hostgroup + "','" + service + "','" + subservice + "','" + splite1[0] + "','" + splite1[1] + "','" + splite1[2].substring(0, 4) + "','" + category + "','" + metrictype + "','"
                            + metricvalue + "','" + eventid + "','" + resourcetype + "','" + rsubtype + "','" + resourceid + "','" + sla + "','" + customerid + "'," + week +","+HH+ ")";

                    status = ConnectionDAO.inserterUpdate(query, cid);
                    logger.info("inserted values sucefully");
                }
           
           // ConnectionDAO.closeStatement();
                }else{
                    logger.info("Data is not avalable for that timestamp");
                
                }
        }
    }else{
                    logger.info("Start and end sun hours as zero");
            
            
            }
          
        } catch (PSQLException ex) {
            ex.printStackTrace();
            logger.error(ex);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e);
        } finally {

            try {   sunHoursStart1=null;
                    sunHoursEnd1=null;
                    propertyFile = null;
                    splite1 = null;
                    splite2 = null;
                    query = null;
                    url=null;
                    hour1=null;
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (startEndTS != null) {
                    startEndTS.clear();
                    startEndTS = null;
                    propertyFile = null;
                }
                epochDate=null;
                if(sunHrStart!=null){
                sunHrStart.clear();
                sunHrStart=null;
                }
                if(sunHrEnd!=null){
                sunHrEnd.clear();
                 sunHrEnd=null;
               }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e);
            }

        }

    }

    
     public static String urlResopnes(String url){
        URL jUrl = null;
        HttpURLConnection conn = null;
        InputStream resopnse = null;
        InputStreamReader inputStrem = null;
        BufferedReader jsonInputStream = null;
        String output;
        String jsonName=null;
        try {
            
            jUrl = new URL(url);
            conn = (HttpURLConnection) jUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.connect();
            resopnse = conn.getInputStream();
            inputStrem = new InputStreamReader(resopnse);
            jsonInputStream = new BufferedReader(inputStrem);
            while ((output = jsonInputStream.readLine()) != null) {
            logger.info(output);
             jsonName = output;
           }
            if(jsonName.contains("\"")){
            logger.info("jsonresponece "+jsonName);
            String[] jsonSplite=jsonName.split("\"");
            logger.info("jsonresponece "+jsonSplite[7]);
            jsonName=jsonSplite[7];
            //String 
            }
        } catch (MalformedURLException ex) {
        ex.printStackTrace();
        
        }catch(IOException e){
       
        }finally{
        
            try{
                    jUrl = null;
                if (inputStrem != null) {
                    inputStrem.close();
                    inputStrem = null;
                }
                if (jsonInputStream != null) {
                    jsonInputStream.close();
                    jsonInputStream = null;
                }
                if (conn != null) {
                    conn.disconnect();
                    conn = null;
                }
                if (resopnse != null) {
                    resopnse.close();
                    resopnse = null;
                }
            }catch(Exception e){
            e.printStackTrace();
            }
        }
         return jsonName;
      }
    public static void main(String[] args) throws PSQLException, InterruptedException {
         String[] forDate = null;
         Calendar toDayDate = Calendar.getInstance();
         String[] zDateSplite=null,spliteDate=null;
         List startEndTS=null;
           
         try {
           PropertyConfigurator.configure("log4j.properties");
            long curTime =0;
            String curDate=null;
              long i=0;
            
            logger.info("curTime " + curDate);
            //TimeLineAggregator tl = new TimeLineAggregator("/home/naveen/yedukondalu/NetBeansProjects/TimeWiseAggregator/config"+File.separator
            //+"MetricTypes.properties");
            TimeLineAggregator tl = new TimeLineAggregator("config"+File.separator+"MetricTypes.properties");
            
            for(String arg:args){ 
                if(arg.contains("--forDate")){
                 forDate=arg.split("=");
                curDate=forDate[1];
                }
            }
            initialize();
            if (curDate.contains("-1")) {
            startEndTS = StartAndEndHourTS.getPeriodEndTS(toDayDate.getTimeInMillis(), "Day");
            curTime=Calendar.getInstance().getTimeInMillis();
            logger.info("taking currentDate time " + curTime);
            i=(Long)startEndTS.get(1)*1000;
            }else{
              zDateSplite = curDate.split("_");
             logger.info(zDateSplite[0]);
             spliteDate=zDateSplite[0].split("-");
             toDayDate.set(Integer.parseInt(spliteDate[2]),(Integer.parseInt(spliteDate[1])-1),Integer.parseInt(spliteDate[0]));
             logger.info(toDayDate.getTime());
             startEndTS = StartAndEndHourTS.getPeriodEndTS(toDayDate.getTimeInMillis(), "Day");
            logger.info("taking GivenDate time " + startEndTS);
            i=(Long)startEndTS.get(1)*1000;
            curTime=(Long) startEndTS.get(3)*1000;
            logger.info("taking GivenDate time " + i);
            logger.info("taking GivenDate time " + curTime);
            }
            while(i<curTime){
            logger.info("timestamp from main" + i);
            generateAggregateValues(i);
                i=i+3690000;
            }
            logger.info("generation is over");
         } catch (Exception io) {
            io.printStackTrace();
            logger.error(io);
        }finally{
        forDate=null;
          toDayDate=null;
         zDateSplite=null;
         spliteDate=null;
        startEndTS.clear();
         startEndTS=null;
        
        }
    }
 
}
