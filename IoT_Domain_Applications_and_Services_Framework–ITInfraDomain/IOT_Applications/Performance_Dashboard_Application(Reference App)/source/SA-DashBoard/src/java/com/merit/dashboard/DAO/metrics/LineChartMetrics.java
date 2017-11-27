/***********************************************************************
 Software Developed by
 Merit Systems Pvt. Ltd.,
No. 42/1, 55/c, Nandi Mansion, 40th Cross, Jayanagar 8th Block
Bangalore - 560 070, India
 Work Created for Merit Systems Private Limited
All rights reserved

THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES
 NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED,
 DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED,
 EXPANDED, COLLECTED, COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED
WITHOUT THE PRIOR WRITTEN CONSENT
 ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT
 THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
***********************************************************************/


package com.merit.dashboard.DAO.metrics;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.merit.connection.ConnectionDAO;
import com.merit.dashboard.DBUtil.DBUtilHelper;
import com.merit.dashboard.util.PropertyUtil;
import javax.servlet.ServletContextListener;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * *******************************************************************************************************
 * This class is giving the information from servicemetrics tables
 * to generate TimeLine Chart.
 *
 * *******************************************************************************************************
 */
public class LineChartMetrics {
	static Logger log = Logger.getLogger(LineChartMetrics.class);



	/**
	 * ***************************************************************************************************
	 * This method is Query to Generate TimeLine Chart for using user Defined
	 *  array_agg()function for concatenating the column of a query Result
	 *  array_agg function in postgresql  below 8 version we have to create by following command:
	 *
		create aggregate array_agg ( sfunc = array_append,  basetype = anyelement,  stype = anyarray,  initcond = '{}'  );

	 *  Then sending to executeQueryForLineChart() to execute query and
	 *  generate HashMap for Generating Chart.
	 *  @param resourceType Different category as Desktop,Server,DataBase,Network,JVM
	 *  @param smilli date in second as long Timestamp(Current Time-selected Time Period)
	 *  @param emilli date in second as long Timestamp(Current Time)
	 *  @param customer Selected customerID
	 * ***************************************************************************************************
	 */
	String restype = "";
	String mettype = "";
	TreeMap<String, String> mapAvailabilityLineChart = new TreeMap<String, String>();
	public String generateLineChart(String resourceType,long smilli, long emilli,String customer, String metrictype) {
		log.debug("==============================Endof LineChartMetricsSummary linechart===========================================");
		restype = resourceType;
		mettype = metrictype;
		Statement stat = null;
		ResultSet rs = null;
		String szLineChartJson="[]";

//        System.out.println(">>>>>>>>>>>>>>>>>>>>> metrictype ==== " + metrictype);
        String szQueryDataPointsForBattery="";
        if(metrictype== null ||  metrictype.equalsIgnoreCase("")){
            szQueryDataPointsForBattery="" +
                                        "select "
                                          + "host as ServerName, "
                                          + "resourcetype as ResourceType,"
                                          + "resourceId as ResourceID,"
                                          + "service as ServiceName,"
                                          + "metrictype as ResourceNames, "
                                          + "to_char(to_timestamp(timestamp1), 'yyyy/MM/dd HH24:mi:SS') as TimeStamps,"
                                          + "metricvalue as ResourceValues,"
                                          + "sla as SlaValues "
                                      + "from servicemetrics  "
                                      + "where "
                                          + "timestamp1 between " + smilli + " and " + emilli +  " and "
                                          + "metricvalue is not null and "
                                          + "resourceType='"+resourceType+"' "
                                      + "ORDER By "
                                          + "metrictype,TimeStamps";
            return "[]";

        }else{
        	if(metrictype.equals("'downtime'")){
        		 szQueryDataPointsForBattery=""+
                 "select "
                   + "host as ServerName, "
                   + "resourcetype as ResourceType,"
                   + "resourceId as ResourceID,"
                   + "service as ServiceName,"
                   + "metrictype as ResourceNames, "
                   + "to_char(to_timestamp(timestamp1), 'yyyy/MM/dd HH24:mi:SS') as TimeStamps,"
                   + "0 as ResourceValues, "
                   + "0.99 as SlaValues "
               + "from servicemetrics  "
               + "where "
                   + "timestamp1 between " + smilli + " and " + emilli +  " and "
                   + "timestamp1 <> 1 and "
                   + "resourceType='"+resourceType+"' and "
                   + "metrictype in ('downtime')";

        		String szQueryDataPointsForBattery1=""+
                 "select "
                   + "host as ServerName, "
                   + "resourcetype as ResourceType,"
                   + "resourceId as ResourceID,"
                   + "service as ServiceName,"
                   + "metrictype as ResourceNames, "
                   + "to_char(to_timestamp(timestamp2), 'yyyy/MM/dd HH24:mi:SS') as TimeStamps,"
                   + "1 as ResourceValues, "
                   + "0.99 as SlaValues "
               + "from servicemetrics  "
               + "where "
                   + "timestamp2 between " + smilli + " and " + emilli +  " and "
                   + "timestamp2 is not null and "
                   + "resourceType='"+resourceType+"' and "
                   + "metrictype in ('downtime')";
        		String Json_data=createAvailabilityJson(szQueryDataPointsForBattery,szQueryDataPointsForBattery1,customer);
        		mapAvailabilityLineChart=null;
        		return Json_data;
        	}
        	else
            szQueryDataPointsForBattery=""+
                                        "select "
                                          + "host as ServerName, "
                                          + "resourcetype as ResourceType,"
                                          + "resourceId as ResourceID,"
                                          + "service as ServiceName,"
                                          + "metrictype as ResourceNames, "
                                          + "to_char(to_timestamp(timestamp1), 'yyyy/MM/dd HH24:mi:SS') as TimeStamps,"
                                          + "metricvalue as ResourceValues,"
                                          + "sla as SlaValues "
                                      + "from servicemetrics  "
                                      + "where "
                                          + "timestamp1 between " + smilli + " and " + emilli +  " and "
                                          + "metricvalue is not null and "
                                          + "resourceType='"+resourceType+"' and "
                                          + "metrictype in ( " +metrictype + ") "
                                      + "ORDER By "
                                          + "metrictype,TimeStamps";
           }
        try {
            log.debug("linechart query: /"+resourceType+"/ \n"+szQueryDataPointsForBattery);
            szLineChartJson= executeQueryForLineChart_1(szQueryDataPointsForBattery,customer);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("DashBoard LineCharMetrics  generateLineChart()  :"+ e.getMessage());

		} finally {
			try {
				if (stat != null) {
					stat.close();
					stat = null;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("DashBoard LineCharMetrics  generateLineChart()  :\n"+ e.getMessage());
			}

		}
		return szLineChartJson;
	}

	/**
	 * ***************************************************************************************************
	 *  Executing Query and making HashMap of SLA,MetricValues,MetricNames,Timestamp
	 *  then sending to generateJsonForMultipleMetricType() to generate JSON for
	 *  TimeLine LineChart
	 *  @param szQuery Query to Be Executed for Line Chart
	 *  @param customer Customer Id
	 *
	 * ***************************************************************************************************
	 */

	public String executeQueryForLineChart_1(String szQuery,String customer){
    	Connection uniqueconnection=null;
    	Statement stat=null;
    	ResultSet rs=null;
    	String szLineChartJson="[]";
    	HashMap<String,String> map_metricName      = new HashMap<String, String>();
    	HashMap<String,String> map_metricTimeStamp = new HashMap<String, String>();
    	HashMap<String,String> map_metricValue     = new HashMap<String, String>();
        HashMap<String,String> map_metricSla       = new HashMap<String, String>();
        HashMap<String, JSONObject> jsonObjMap     = new HashMap<String, JSONObject>();
    	try{

             /**
                ServerName
                ResourceType
                ResourceID
                ServiceName
                arr_    ResourceNames
                arr_    TimeStamps
                arr_    ResourceValues
                arr_    SlaValues
                * key = "'ServerName':'"+serverName+"','ResourceType':'"+resourceType+"','ResourceID':'"+resourceID+"','ServiceName':'"+serviceName+"',"
             */
             uniqueconnection=ConnectionDAO.getConnection(customer);
             stat = uniqueconnection.createStatement();
             rs = stat.executeQuery(szQuery);
             String key = null;
             JSONObject tjsonObj = null;
             System.out.println("manual lineChart logic start:"+new Date());
             while (rs.next()) {
                key =   "'ServerName':'"     + rs.getString("ServerName") +
                      "','ResourceType':'"   + rs.getString("ResourceType") +
                      "','ResourceID':'"     + rs.getString("ResourceID") +
                      "','ServiceName':'"    + rs.getString("ServiceName") +
                      "','ActualMetricNames':['" + rs.getString("ResourceNames")+"']" +
                      " ,'ResourceNames':['" + DBUtilHelper.getMetrics_mapping_properties().getProperty(rs.getString("ResourceNames"))+"'],";

                //updateMap(map_metricName,      rs, key, "ResourceNames");
                updateMap(map_metricTimeStamp, rs, key, "TimeStamps");
                updateMap(map_metricValue,     rs, key, "ResourceValues");
                updateMap(map_metricSla,       rs, key, "SlaValues");
             }
             Set <String> keySet = map_metricTimeStamp.keySet();
             for(String keyStr : keySet){
                 String jsonSTR = keyStr
                                    + "'TimeStamps':     [[" + map_metricTimeStamp.get(keyStr) + " ]],"
                                    + "'ResourceValues': [[" + map_metricValue.get(keyStr).replaceAll("\"", "").replaceAll("'", "")     + " ]],"
                                    + "'SlaValues':      [[" + map_metricSla.get(keyStr).replaceAll("\"", "").replaceAll("'", "")      + " ]]";
                 JSONObject jsonObj = new JSONObject("{" +jsonSTR +"}");
                 key = keyStr.split(",'ResourceNames'")[0];
                 tjsonObj = jsonObjMap.get(key);
                 if(tjsonObj == null){
                     tjsonObj = jsonObj;
                 } else{

                    /*tjsonObj = CommonMongoChangerMethods.accumulateJSONObject(
                            tjsonObj,
                            new String[] {"ServerName","ResourceType", "ResourceID", "ServiceName", "ResourceNames", "TimeStamps", "ResourceValues", "SlaValues"},
                            jsonObj,
                            new String[] {"ServerName","ResourceType", "ResourceID", "ServiceName", "ResourceNames", "TimeStamps", "ResourceValues", "SlaValues"},
                            new String[] {"ServerName","ResourceType", "ResourceID", "ServiceName"});*/
                    tjsonObj = accumulateJSONSAllArray(
                            tjsonObj,
                            jsonObj,
                            new String[] {"ActualMetricNames", "ResourceNames", "TimeStamps", "ResourceValues", "SlaValues"}
                    );
                 }
                 jsonObjMap.put(key, tjsonObj);
             }
             System.out.println("manual lineChart logic End:"+new Date());
            /**
                ServerName
                ResourceType
                ResourceID
                ServiceName
                arr_    ResourceNames
                arr_    TimeStamps
                arr_    ResourceValues
                arr_    SlaValues
                *

                * key = "'ServerName':'"+serverName+"','ResourceType':'"+resourceType+"','ResourceID':'"+resourceID+"','ServiceName':'"+serviceName+"',"
             */
             //System.out.println("\n\n\n jsonObjMap = " + jsonObjMap);
             szLineChartJson = getJSONArrayFromObjectMap(jsonObjMap).toString();
             map_metricName=null;
             map_metricValue=null;
             map_metricTimeStamp=null;
             map_metricSla=null;
        } catch (Exception e) {
    		e.printStackTrace();
    	}
    	finally {
    		try {
    			if (stat != null) {
    				stat.close();
    				stat = null;
    			}
				if (rs != null) {
					rs.close();
					rs = null;
				}
    		}
    		catch (Exception e) {
				e.printStackTrace();
				log.error("DashBoard LineCharMetrics  executeQueryForLineChart()  :\n"+ e.getMessage());
			}
		}
    	return szLineChartJson;
	}

    private void updateMap(HashMap<String,String> map, ResultSet rs, String key, String column){
        try {
            String value = null;
            value = map.get(key);
            if (value == null) {
                value = "'" + rs.getString(column) + "'";
            } else {
                value += ",'" + rs.getString(column) + "'";
            }
            map.put(key, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private JSONObject accumulateJSONSAllArray(JSONObject obj1, JSONObject obj2, String[] arrFields){
        try {
            JSONArray t2 = null;
            for (String arrField : arrFields) {
                try{
                    t2 = obj2.getJSONArray(arrField);
                    obj1.accumulate(arrField, t2.get(0));
                }catch(Exception ex){}
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return obj1;
    }

    private JSONObject accumulateJSONS(JSONObject obj1, JSONObject obj2, String[] arrFields){
        try {
            Object field1 = null;
            Object field2 = null;
            JSONArray t = null;
            JSONArray temp = null;
            for (String arrField : arrFields) {
                try{
                    field1 = obj1.getJSONArray(arrField);
                    field2 = obj2.getJSONArray(arrField);
                }catch(Exception ex){}
                if(field2 == null){
                    field2 = obj2.getString(arrField);
                    obj1.accumulate(arrField, field2);
                } else {
                    t = new JSONArray();
                    temp = null;
                    try{
                        temp = ((JSONArray)field1).getJSONArray(0);
                    }catch(Exception ex){}

                    if(temp != null){
                        int length = ((JSONArray)field1).length();
                        ((JSONArray)field1).put(length, field2);
                    }else{
                        t.put(0, field1);
                        t.put(1, field2);
                        field1 = t;
                    }
                    obj1.remove(arrField);
                    obj1.put(arrField, field1);
                }
                field1 = null;
                field2 = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return obj1;
    }

	/**
	 * ***************************************************************************************************
	 *  Executing Query and making HashMap of SLA,MetricValues,MetricNames,Timestamp
	 *  then sending to generateJsonForMultipleMetricType() to generate JSON for
	 *  TimeLine LineChart
	 *  @param szQuery Query to Be Executed for Line Chart
	 *  @param customer Customer Id
	 *
	 * ***************************************************************************************************
	 */

	/*public String executeQueryForLineChart(String szQuery,String customer){
    	Connection uniqueconnection=null;
    	Statement stat=null;
    	ResultSet rs=null;
    	String szLineChartJson="[]";
    	HashMap<String,String> map_metricName=new HashMap<String, String>();
    	HashMap<String,String> map_metricValue=new HashMap<String, String>();
    	HashMap<String,String> map_metricTimeStamp=new HashMap<String, String>();
        HashMap<String,String> map_metricSla=new HashMap<String, String>();
    	try{
    	uniqueconnection=ConnectionDAO.getConnection(customer);
    	stat = uniqueconnection.createStatement();

    	rs = stat.executeQuery(szQuery);

         while (rs.next())
         {
        	 if(map_metricSla.containsKey(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"))){
        		 String metricsla=map_metricSla.get(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"));
        		 map_metricSla.remove(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"));
        		 map_metricSla.put(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"), metricsla+","+rs.getString("slavalues").replace("{","[").replace("}", "]"));
        	 }
        	 else{
        		 map_metricSla.put(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"),"["+rs.getString("slavalues").replace("{","[").replace("}", "]"));
        	 }
        	 if(map_metricName.containsKey(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"))){
        		 String metricname=map_metricName.get(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"));
        		 map_metricName.remove(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"));
        		 map_metricName.put(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"), metricname+",\""+DBUtilHelper.getMetrics_mapping_properties().getProperty(rs.getString("metrictype"))+"\"");
        	 }
        	 else{
        		 map_metricName.put(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"),"["+"\""+DBUtilHelper.getMetrics_mapping_properties().getProperty(rs.getString("metrictype"))+"\"");
        	 }
        	 if(map_metricValue.containsKey(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"))){
        		 String metricname=map_metricValue.get(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"));
        		 map_metricValue.remove(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"));
        		 map_metricValue.put(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"), metricname+","+rs.getString("metricvalue").replace("{","[").replace("}", "]"));
        	 }
        	 else{
        		 map_metricValue.put(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"),"["+rs.getString("metricvalue").replace("{","[").replace("}", "]"));
        	 }
        	 if(map_metricTimeStamp.containsKey(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"))){
        		 String sztimestamp=map_metricTimeStamp.get(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"));
        		 map_metricTimeStamp.remove(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"));
        		 map_metricTimeStamp.put(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"), sztimestamp+","+rs.getString("timestamp").replace("{\"","[\"").replace("\"}", "\"]"));
        	 }
        	 else{
        		 map_metricTimeStamp.put(rs.getString("service")+","+rs.getString("resourcetype")+","+rs.getString("host"),rs.getString("timestamp").replace("{\"","[\"").replace("\"}", "\"]"));
        	 }
         }
         if(map_metricName.size()!=0)
        	 szLineChartJson=generateJsonForMultipleMetricType(map_metricSla,map_metricName, map_metricValue, map_metricTimeStamp);
        map_metricName=null;
     	map_metricValue=null;
     	map_metricTimeStamp=null;
        map_metricSla=null;
    	}

    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	finally {
    		try {
    			if (stat != null) {
    				stat.close();
    				stat = null;
    			}
				if (rs != null) {
					rs.close();
					rs = null;
				}
    		}
    		catch (Exception e) {
				e.printStackTrace();
				log.error("DashBoard LineCharMetrics  executeQueryForLineChart()  :\n"+ e.getMessage());
			}
		}
    	return szLineChartJson;
	}
*/
    /**
	 * ***************************************************************************************************
	 * creating JSON For Line Chart and Sending generated JSON to SendFileToJson class
	 * @param Metricsla  HashMap having SLAValues
	 * @param MetricName HashMap having MetricNames
	 * @param MetricValue HashMap having MetricValues
	 * @param MetricTimeStamp HashMap having Timestamp

	 * ***************************************************************************************************
	 */
	@SuppressWarnings("rawtypes")
  /*  public String generateJsonForMultipleMetricType(HashMap<String, String> Metricsla,HashMap<String, String> MetricName, HashMap<String, String> MetricValue,HashMap<String, String> MetricTimeStamp){
		String Json_data = "[";
		Set set = MetricName.entrySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {

			Map.Entry entry = (Map.Entry) it.next();
			String host_key = (String) entry.getKey();
			String[] serviceResourcetypehost=host_key.split(",");
			String host_MetricName = (String) entry.getValue() + "]";
			String host_MetricValue = (String) MetricValue.get(host_key) + "]";
			String host_timestamp = (String) MetricTimeStamp.get(host_key);
			Json_data=Json_data+"{ \"ServiceName\":\"" + serviceResourcetypehost[0] +"\",\"ResourceType\":\"" + serviceResourcetypehost[1] +"\",\"ServerName\":\"" + serviceResourcetypehost[2] +"\",\"TimeStamps\":[" + host_timestamp+ "],\"ResourceNames\":" + host_MetricName + ",\"ResourceValues\":" +host_MetricValue+ ",\"SlaValues\":"+Metricsla.get(host_key)+"],\"ResourceID\":\"" + host_key + "\"},";
		}
		Json_data = Json_data.substring(0, Json_data.lastIndexOf(",")) + "]";

		return Json_data;
		//SendFileToJson file_send_jvmheap = new SendFileToJson(customer,timestampselection, resourcetype, json_Filename, Json_data);

	}*/
	public void  createJson(String szQuery){
		  Connection uniqueconnection=null;
    	ResultSet rs=null;
    	String Json_data="";
    	String metricvalue="";
    	String timestamp="";

		   try {

			try {
				uniqueconnection = ConnectionDAO.getConnection("192.168.1.2");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				 Statement stat = uniqueconnection.createStatement();
			rs = stat.executeQuery(szQuery);
			  while (rs.next()) {
				  metricvalue=rs.getString("metricvalue");
				  timestamp="\\/Date("+rs.getString("timestamp1")+"+000)\\/";
				  Json_data=Json_data+",{ \"value\":\"" + metricvalue +"\",\"slice\":\"" + timestamp +"\"}";
			  }

		} catch (SQLException e) {

			e.printStackTrace();
		}



	}

	public String createAvailabilityJson(String Query1,String Query2,String customer){
		Connection uniqueconnection=null;
    	Statement stat=null;
    	ResultSet rs=null;
    	String timeLineAvailJson="";
    	String serviceName="";
    	String resourceType="";
    	String serverName="";
    	String resourceId="";
    	String timestamp1="";
    	String resourceValue="";
		String sla="";
		String timestamp="";
		String uniqueString="";
		int c=0;
		int count=0;


		try{
	    	uniqueconnection=ConnectionDAO.getConnection(customer);
	    	stat = uniqueconnection.createStatement();
	    	String[] QueryArray={Query1,Query2};
	    	for(int i=0;i<2;i++){
	    	rs = stat.executeQuery(QueryArray[i]);


	         while (rs.next())
	         {
	        	  serviceName=rs.getString("ServiceName");
	        	 resourceType=rs.getString("ResourceType");
	        	 serverName=rs.getString("ServerName");
	        	 resourceId=rs.getString("ResourceID");
	        	 mapAvailabilityLineChart.put(rs.getString("ServiceName")+","+rs.getString("ResourceType")+","+rs.getString("ServerName")+","+rs.getString("ResourceID")+",\""+rs.getString("Timestamps")+"\"",rs.getString("ResourceValues"));
	         }
	    	}

	    	Set set = mapAvailabilityLineChart.entrySet();
			Iterator it = set.iterator();
			while (it.hasNext()) {


				Map.Entry entry = (Map.Entry) it.next();
				String host_key = (String) entry.getKey();
				String[] serviceResourcetypehost=host_key.split(",");
				serviceName=serviceResourcetypehost[0];
		    	 resourceType=serviceResourcetypehost[1];
		    	 serverName=serviceResourcetypehost[2];
		    	 resourceId=serviceResourcetypehost[3];
		    	 if(!(serviceName+","+serverName+","+resourceId).equals(uniqueString)){

					  if(c==1){
						  String[] splitUniqueString=uniqueString.split(",");
						  timeLineAvailJson= timeLineAvailJson+",{\"ServiceName\":\"" + splitUniqueString[0] +"\",\"ResourceType\":\"" + resourceType +"\",\"ServerName\":\"" + splitUniqueString[1] +"\",\"TimeStamps\":[" + timestamp.substring(1)+ "],\"ActualMetricNames\":[\"Availability\"],\"ResourceNames\":[\"Availability\"],\"ResourceValues\":[" +resourceValue.substring(1)+ "],\"SlaValues\":["+sla.substring(1)+"],\"ResourceID\":\"" + splitUniqueString[2]+ "\"}";
						  resourceValue="";
						  sla="";
						  timestamp="";
					  }
		    		  uniqueString=serviceName+","+serverName+","+resourceId;
		    		  c=1;
		    		   count=0;
		    	  }
		    	 timestamp1=serviceResourcetypehost[4];
		    	  timestamp=timestamp+","+serviceResourcetypehost[4];
		    	  String rvalue=mapAvailabilityLineChart.get(serviceName+","+resourceType+","+serverName+","+resourceId+","+timestamp1);
		    	  resourceValue=resourceValue+",["+count+","+rvalue+"]";
		    	  if(rvalue.equals("0")){
		    	  sla=sla+",["+count+",0]";
		    	  }
		    	  count++;
			}

		}
		catch(Exception e){
			e.printStackTrace();
		}
		if(count==0)
			timeLineAvailJson=",";
		else
			timeLineAvailJson=timeLineAvailJson+","+"{\"ServiceName\":\"" + serviceName+"\",\"ResourceType\":\"" + resourceType +"\",\"ServerName\":\"" + serverName +"\",\"TimeStamps\":[" + timestamp.substring(1)+ "],\"ActualMetricNames\":[\"Availability\"],\"ResourceNames\":[\"Availability\"],\"ResourceValues\":[" +resourceValue.substring(1)+ "],\"SlaValues\":["+sla.substring(1)+"],\"ResourceID\":\"" + resourceId+ "\"}";
		return "["+timeLineAvailJson.substring(1)+"]";

	}

    public static JSONArray getJSONArrayFromObjectMap(HashMap<String,JSONObject> map){
        Collection<JSONObject> jsonSet = map.values();
        JSONArray arr = new JSONArray(jsonSet);
        return arr;
    }

    public static void main(String[] args) {
    	String query="select timestamp1,metricvalue from servicemetrics where host='192.168.1.7' and resourcetype='server' and metrictype='cpuutilization' order by timestamp1";
    	new LineChartMetrics().createJson(query);
	}

}