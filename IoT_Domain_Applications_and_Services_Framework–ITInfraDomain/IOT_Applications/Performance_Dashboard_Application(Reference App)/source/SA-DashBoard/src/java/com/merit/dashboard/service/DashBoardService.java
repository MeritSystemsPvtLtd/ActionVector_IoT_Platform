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
package com.merit.dashboard.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import com.merit.dashboard.Availability.GetAvailability;
import com.merit.dashboard.DAO.metrics.LineChartMetrics;
import com.merit.dashboard.ServletContextListener;
import com.merit.dashboard.bizlogic.BizLogic;
import com.merit.dashboard.file.SendFileToJson;
import com.merit.dashboard.queryexecuter.QueryExecuter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

public class DashBoardService {


    QueryExecuter queryExecuter = null;
    BizLogic bizlogic = null;
    LineChartMetrics lineChartMetrics = null;
    long smilli = 0;
    long emilli = 0;
    private static  boolean metricflag=true;
    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * ************************************************************************************************
     * This method is calling queryExecuter.collectMetricdata() for getting all
     * Metric Info, queryExecuter.collectTicketdata() for getting all Tiket Info
     * and GetAvailability.getAvailabilityByPeriod() for getting Availability
     * Information and then returning all these JSON String are combined and
     * creating JSON Files name MetricTypes.json
     *
     * @param customer Selected customerID
     * @param resourceType Different category as
     * Desktop,Server,DataBase,Network,JVM
     * @param startDate from date as subtract(endDate-timestampselection) in
     * Date Format
     * @param endDate Current date in Date Format
     * @return timestampselection Selected Time Period means (hour, days, week,
     * month, year)
     *
     * *************************************************************************************************
     */
    public void generateMetricTypeJson(String resourceType, String selection, String startDate, String endDate, String timestampselection, String customer) {
        try {
            queryExecuter = new QueryExecuter();
            bizlogic = new BizLogic();

            Date dateStart = formatter1.parse(startDate);
            Date dateEnd = formatter1.parse(endDate);
            String date = formatter1.format(dateStart);
            String date1 = formatter1.format(dateEnd);
            smilli = dateStart.getTime() / 1000;
            emilli = dateEnd.getTime() / 1000;

            String szMetricJson = "";
            if (selection.contains("Availability")) {
                //availability hence no change
                String szAvailJson = GetAvailability.getAvailabilityByPeriod(emilli, smilli, resourceType, customer, timestampselection);
                //System.out.println(">>>>>>>>>>>>>>><<<<<<<<<<<<<< szAvailJson = " + szAvailJson);
                szMetricJson = szAvailJson;
                //szMetricJson = bizlogic.generateJsonFromCombinedTable(new LinkedHashMap<String, String>(), new LinkedHashMap<String, String>(), szAvailJson);
            } else if (selection.contains("Alert")) {
                //alerts only and no change for selection
                LinkedHashMap<String, String> mapTicketInfo = queryExecuter.collectTicketdata(customer, resourceType, date, date1,selection);
                szMetricJson = ServletContextListener.modifyMapToJSONArray(mapTicketInfo);//bizlogic.generateJsonFromCombinedTable(new LinkedHashMap<String, String>(), mapTicketInfo, "[{}]");
               // System.out.println("*********** Alerts szMetricJson = " + szMetricJson);
            } else {
                //metrictype except avalability and alerts
                String metricTypeIn = ServletContextListener.getJSONMetricGroupSet(resourceType, selection);
                LinkedHashMap<String, String> mapMetricInfo = queryExecuter.collectMetricdata(customer, resourceType, metricTypeIn, smilli, emilli);
                szMetricJson = ServletContextListener.modifyMapToJSONArray(mapMetricInfo);//bizlogic.generateJsonFromCombinedTable(mapMetricInfo, new LinkedHashMap<String, String>(), "[{}]");
               // System.out.println("*********** other metricType szMetricJson = " + szMetricJson);
            }


            new SendFileToJson(customer, selection, timestampselection, resourceType, "MetricTypes", szMetricJson);
            QueryExecuter.pojoObject = null;
            //mapTicketInfo = null;
            queryExecuter = null;
            //mapMetricInfo = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ************************************************************************************************
     * This method is calling lineChartMetrics.generateLineChart() and then
     * returning JSON String for creating JSON Files for generating Chart
     *
     * @param customer Selected customerID
     * @param resourceType Different category as
     * Desktop,Server,DataBase,Network,JVM
     * @param startDate from date as subtract(endDate-timestampselection) in
     * Date Format
     * @param endDate Current date in Date Format
     * @return timestampselection Selected Time Period means (hour, days, week,
     * month, year)
     *
     * *************************************************************************************************
     */
    public void generateLineChartJson(String resourceType, String selection, String startDate, String endDate, String timestampselection, String customer) {
        lineChartMetrics = new LineChartMetrics();
        try{
        Date dateStart = formatter1.parse(startDate);
        Date dateEnd = formatter1.parse(endDate);
        smilli = dateStart.getTime() / 1000;
        emilli = dateEnd.getTime() / 1000;
        String szMetricTypeValueJson = null;
        HashMap<String, String>map = ServletContextListener.getJSONLocationMap(resourceType);
        if(selection.startsWith("TimeLine")){
            szMetricTypeValueJson = lineChartMetrics.generateLineChart(resourceType, smilli, emilli, customer, map.get(selection));
            new SendFileToJson(customer, selection, timestampselection, resourceType, "LineChartByTime", szMetricTypeValueJson);
        }
        lineChartMetrics = null;
        }
        catch (Exception e) {
		e.printStackTrace();
		}
    }

    /**
     * ************************************************************************************************
     * This method is calling queryExecuter.generateDefaultServiceJson() for
     * generating default service and
     * queryExecuter.generateDefaultResourceTypeJson() for generating default
     * ResourceType JSON for Chart and then returning JSON String for creating
     * JSON Files
     *
     * @param customer Selected customerID
     * @param resourceType Different category as
     * Desktop,Server,DataBase,Network,JVM
     * @param startDate from date as subtract(endDate-timestampselection) in
     * Date Format
     * @param endDate Current date in Date Format
     * @return timestampselection Selected Time Period means (hour, days, week,
     * month, year)
     *
     * *************************************************************************************************
     */
    public void generateDefaultServiceAndResourceTypeJson(String resourceType, String startDate, String endDate, String timestampselection, String customer) {
        try {
            queryExecuter = new QueryExecuter();
            resourceType = "";
            String szServiceJson = queryExecuter.generateDefaultServiceJson(resourceType, startDate, endDate, timestampselection, customer);
            new SendFileToJson(customer, "", timestampselection, resourceType, "Defaultservice", szServiceJson);
            String szResourceTypeJson = queryExecuter.generateDefaultResourceTypeJson(resourceType, startDate, endDate, timestampselection, customer);
            new SendFileToJson(customer, "", timestampselection, resourceType, "DefaultResourcetype", szResourceTypeJson);
            String watchDogAlertJson = queryExecuter.getWatchDogAlertJson(customer);
            new SendFileToJson(customer, "", "Hour", resourceType, "WatchDogAlert", watchDogAlertJson);
           if(metricflag){
            	 String metricResourceJson = queryExecuter.getMetricResourceTypeMapping(customer);
            	 new SendFileToJson(customer, "", "Hour", "", "MetricResourceMappingJson", metricResourceJson);
            	 metricflag=false;
            }
            queryExecuter = null;
        } catch (Exception e) {
            e.printStackTrace();

        }
        //new SendFileToJson(customer, timestampselection, resourceType, szJsonName, szMetricTypeValueJson);
    }

    public void generateJSONForLeftGrid(String resourceType, String startDate, String endDate, String timestampselection, String customer) {
        try {
        	 bizlogic = new BizLogic();
            queryExecuter = new QueryExecuter();
            Date dateStart = formatter1.parse(startDate);
			Date dateEnd = formatter1.parse(endDate);
			String date=formatter1.format(dateStart);
			String date1=formatter1.format(dateEnd);
			smilli = dateStart.getTime() / 1000;
			emilli = dateEnd.getTime() / 1000;
			LinkedHashMap<String,String> mapMetricInfo=	queryExecuter.collectMetricdata1(customer,resourceType, smilli, emilli);
			LinkedHashMap<String,String> mapTicketInfo=queryExecuter.collectTicketdata1(customer,resourceType,date, date1);
			String szAvailJson= GetAvailability.getAvailabilityByPeriod(emilli, smilli, resourceType,customer,timestampselection);
			String szMetricJson=bizlogic.generateJsonFromCombinedTable(mapMetricInfo,mapTicketInfo,szAvailJson);
			new SendFileToJson(customer,"", timestampselection, resourceType, "LeftGrid", szMetricJson);
        } catch (Exception e) {
            e.printStackTrace();

        }
        //new SendFileToJson(customer, timestampselection, resourceType, szJsonName, szMetricTypeValueJson);
    }

}
