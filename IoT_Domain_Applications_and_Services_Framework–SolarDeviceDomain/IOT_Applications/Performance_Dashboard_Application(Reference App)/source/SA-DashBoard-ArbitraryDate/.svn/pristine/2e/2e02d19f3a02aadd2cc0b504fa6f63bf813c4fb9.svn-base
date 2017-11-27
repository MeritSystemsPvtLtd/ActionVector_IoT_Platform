
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


package com.merit.dashboard.Availability;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.merit.connection.ConnectionDAO;
import com.merit.dashboard.DBUtil.DBUtilHelper;

/**
 *
 * Case ::1 Get all the host Availability for a selected period time
 * Case ::2 If the availability is not found in a selected period of time it check for last
 *        occurence of  downtime Then calculate percent of availability
 * Case ::3 If downtime not found then it checks for the last occurence of the uptime.
 *
 * */

public class GetAvailability {
	static Logger log = Logger.getLogger(GetAvailability.class);
	static StringBuffer sz_avail_json = null;
	public static String getAvailabilityByPeriod(long emilli, long smilli,
			String resourceType,
			String customer, String timestampselection) {
		 sz_avail_json = new StringBuffer();
		sz_avail_json.append("[");
		Statement stat = null;
		ResultSet rs = null;
		String host = null;
		String service = null;
		//String subservice = null;
		String sz_resourceType = null;
		String category = null;
		String metricType = null;
		String metricvalue = null;
		float metricthresholdvalue = 0.0f;
		String resourceId = null;
		String szAvailJson="[]";

		try {
			Connection uniqueconnection = ConnectionDAO.getConnection(customer);
			stat = uniqueconnection.createStatement();

			if(resourceType.split(",").length==1)
				resourceType="'"+resourceType+"'";

			if(resourceType.equals("'Desktop'")){

				String szQuery = "select service,subservice,"
				+ "host,resourceId,resourceType  "
				+ "from servicemetrics where "
				+ "service is not null  and service!='null' "
				+ "and resourcetype in ("+resourceType+") group by service,subservice,resourcetype,host,resourceid";
				rs = stat.executeQuery(szQuery);
				while (rs.next()) {
				host = rs.getString("host");
				service = rs.getString("service");
				resourceId = rs.getString("resourceId");
				sz_resourceType = rs.getString("resourceType");
				sz_avail_json.append("{\"ServerName\":\"" + host
								+ "\",\"ResourceType\":\"" + sz_resourceType
								+ "\",\"ServiceName\":\"" + service
								+ "\",\"ResourceNames\":\"\""
								+ ",\"ResourceID\":\"" + resourceId
								+ "\",\"Health\":\"Default"
								+ "\"},");


			}
				szAvailJson=sz_avail_json.substring(0,sz_avail_json.length()-1).toString()+"]";
				return szAvailJson;
			}
			else{


			String szQuery = "select sum(case when timestamp1<"+smilli+" and timestamp2<"+smilli+" then "+(emilli-smilli)+" when timestamp1<"+smilli+" and timestamp2>"+smilli+" then timestamp2-"+smilli+" else timestamp2-timestamp1 end) as availmetricvalue,service,subservice,"
				+ "host,metrictype,category,resourceId,sla as SLA,resourceType  "
				+ "from servicemetrics where timestamp1 between "
				+ smilli
				+ " and "
				+ emilli
				+ " and service is not null  and service!='null' and timestamp2 is not null "
				+ "and metrictype='downtime' and resourcetype in ("+resourceType+") group by metrictype,host,service,subservice,category,resourceid,resourcetype,"
				+ "sla order by service,subservice,metrictype,host";
			log.debug("DashBoard Availability With in Time period /"+timestampselection+"/"+resourceType+"/ \n"+szQuery);
			rs = stat.executeQuery(szQuery);
			 String szHostList="";
			while (rs.next()) {
				host = rs.getString("host");
				category = rs.getString("category");
				service = rs.getString("service");
				metricType = rs.getString("category");
				metricvalue = rs.getString("availmetricvalue");
				resourceId = rs.getString("resourceId");
				metricthresholdvalue = rs.getFloat("SLA");
				sz_resourceType = rs.getString("resourceType");

				String downTimestamp=getDownTimeStamp(smilli,  emilli,  resourceType,  host, resourceId, uniqueconnection);
				String availStatus=getAvailSatus(resourceType,  host, resourceId, uniqueconnection);
				long SDate = smilli;
				long EDate = emilli;
				float downtime = Float.parseFloat(metricvalue) + Float.parseFloat(downTimestamp);
				float first = (float) (((EDate - SDate) - downtime) / (EDate - SDate)) * 100;
				if(first<1){
					first=1f;
				}
				float deveation = first - 99.99f;
				float sla_deviation = first;
				String szHealth="CRITICAL";
				if (deveation > 0 && availStatus.equals("OK")) {
					szHealth = "OK";
				}
				szHostList = szHostList+","+service+"/"+host+"/"+sz_resourceType+"/"+resourceId;
				sz_avail_json.append("{\"ServerName\":\"" + host
								+ "\",\"ResourceType\":\"" + sz_resourceType
								+ "\",\"ResourceNames\":[\"Availability\"],\"ServiceName\":\"" + service
								+ "\",\"ResourceID\":\"" + resourceId
								+ "\",\"ResourceValues\":[" + sla_deviation
								+ "],\"SlaValues\":[99.99],\"Health\":\"" + szHealth
								+ "\"},");


			}
			if(!szHostList.equals("")){
				szHostList=szHostList.substring(1);
			}
			else{
				szHostList="";
			}
			szAvailJson=missedHostAvailability(uniqueconnection, emilli, smilli,
					customer, resourceType,
					timestampselection, sz_avail_json,szHostList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("DashBoard Availability in specified Period: /"+timestampselection+"/"+resourceType+"/\n"+e.getMessage());
		}
return szAvailJson;
	}

	private static String missedHostAvailability(Connection con, long emilli,
			long smilli, String customer,
			String resourceType, String timestampselection,
			StringBuffer sz_avail_json,String szHostList) {
		Statement st = null;
		//ResultSet rs = null;
		String host = null;
		String service = null;
		//String subservice = null;
		String sz_resourceType = null;
		String category = null;
		String metricType = null;
		String metricvalue = null;
		float metricthresholdvalue = 0.0f;
		String resourceId = null;
		float sla_deviation;
		String szHealth = null;
		String finalJson = "[]";

		try {
			String szAvailUpTimeQuery = "select timestamp2,service,host,metrictype,subservice,category,resourceId,SLA,resourceType from servicemetrics where category='availabilityME' and resourcetype in ("+resourceType+") and  timestamp1=1";
			log.debug("DashBoard Availability With watchDog Event in Time period:"+resourceType+"\n"+szAvailUpTimeQuery);
			st = con.createStatement();
			ResultSet rs3 = st.executeQuery(szAvailUpTimeQuery);
			while (rs3.next()) {
				long timestamp2 = rs3.getLong("timestamp2");
				host = rs3.getString("host");
				service = rs3.getString("service");
				resourceId = rs3.getString("resourceId");
				sz_resourceType = rs3.getString("resourceType");

			//boolean adapterDownFlag=getDownTimeWhenAdapterStop(smilli,emilli,resourceType,host,service,resourceId,timestamp2, con);
				if(!szHostList.contains(host+"/"+sz_resourceType+"/"+resourceId)){
				szHostList = szHostList+","+host+"/"+sz_resourceType+"/"+resourceId;
				if(!szHostList.equals("") && szHostList.startsWith(","))
					szHostList=szHostList.substring(1);

				//if(!adapterDownFlag){
						sz_avail_json.append("{\"ServerName\":\"" + host
								+ "\",\"ResourceType\":\"" + sz_resourceType
								+ "\",\"ResourceNames\":[\"Availability\"],\"ServiceName\":\"" + service
								+ "\",\"ResourceID\":\"" + resourceId
								+ "\",\"ResourceValues\":[100],\"SlaValues\":[99.99],\"Health\":\"OK\"},");
						//missed_host_list.remove(host + "/" + sz_resourceType);

			//}

				}
			}
			// check for the host having downtime
			{
				String szAvailDownTimeQuery = "select timestamp1,service,host,metrictype,subservice,category,resourceId,SLA,resourceType from servicemetrics where timestamp2 is NULL and category='availabilityME' and resourcetype in ("+resourceType+") and  metricvalue is NULL";
				log.debug("DashBoard szAvailDownTimeQuery:"+resourceType+"\n"+szAvailDownTimeQuery);
				ResultSet rs4 = st.executeQuery(szAvailDownTimeQuery);
				while (rs4.next()) {

					Long szDownTime_timestamp = rs4.getLong("timestamp1");
					long szOrginalDownTime = emilli - szDownTime_timestamp;
					long szTimeRequested = (long) (emilli - smilli);
					host = rs4.getString("host");
					service = rs4.getString("service");
					resourceId = rs4.getString("resourceId");
					metricType = rs4.getString("category");
					sz_resourceType = rs4.getString("resourceType");
					if(!szHostList.contains(host+"/"+sz_resourceType+"/"+resourceId)){
					szHostList = szHostList+",'"+host+"/"+sz_resourceType+"/"+resourceId;
					if(!szHostList.equals("") && szHostList.startsWith(","))
						szHostList=szHostList.substring(1);

						if (szOrginalDownTime < szTimeRequested) {
							long SDate = smilli;
							long EDate = emilli;
							float downtime = szOrginalDownTime;
							Float first = (float) (((EDate - SDate) - downtime) / (EDate - SDate)) * 100;
							if(first<1){
								first=1f;
							}
							float deveation = first - 99.9f;
							sla_deviation = first;

							szHealth = "CRITICAL";
							if (deveation > 0) {
								szHealth = "OK";
							}


							sz_avail_json.append("{\"ServerName\":\"" + host
									+ "\",\"ResourceType\":\""
									+ sz_resourceType
									+ "\",\"ResourceNames\":[\"Availability\"],\"ServiceName\":\"" + service
									+ "\",\"ResourceID\":\"" + resourceId
									+ "\",\"ResourceValues\":["+ sla_deviation
									+ "],\"SlaValues\":[99.99],\"Health\":\""
									+ szHealth + "\"},");
							}


						else {
							sla_deviation = -2f;
							metricthresholdvalue = rs4.getFloat("SLA");
							metricvalue = "1";
							szHealth = "CRITICAL";
							sz_avail_json.append("{\"ServerName\":\"" + host
									+ "\",\"ResourceType\":\""
									+ sz_resourceType
									+ "\",\"ResourceNames\":[\"Availability\"],\"ServiceName\":\"" + service
									+ "\",\"ResourceID\":\"" + resourceId
									+ "\",\"ResourceValues\":[" + metricvalue
									+ "],\"SlaValues\":[99.99],\"Health\":\""
									+ szHealth + "\"},");

						}
					}
				}


				{

					String szAvailUp = "select service,host,subservice,resourceId,resourceType from servicemetrics where  category = 'availability' and resourcetype in ("+resourceType+") group by host,service,subservice,resourceId,resourceType";
					log.debug("DashBoard Availability With watchDog Event in Time period:"+resourceType+"\n"+szAvailUpTimeQuery);
					st = con.createStatement();
					ResultSet rs5 = st.executeQuery(szAvailUp);
					while (rs5.next()) {
						host = rs5.getString("host");
						service = rs5.getString("service");
						resourceId = rs5.getString("resourceId");
						sz_resourceType = rs5.getString("resourceType");
						if(!szHostList.contains(host+"/"+sz_resourceType+"/"+resourceId)){
						sz_avail_json.append("{\"ServerName\":\"" + host
									+ "\",\"ResourceType\":\"" + sz_resourceType
									+ "\",\"ResourceNames\":[\"Availability\"],\"ServiceName\":\"" + service
									+ "\",\"ResourceID\":\"" + resourceId
									+ "\",\"ResourceValues\":[100],\"SlaValues\":[99.99],\"Health\":\"OK\"},");

						}

					}

				}
			}


			if (sz_avail_json.lastIndexOf(",") > 0) {
				finalJson = sz_avail_json.substring(0,sz_avail_json.lastIndexOf(","));
				finalJson = finalJson + "]";
			}
			sz_avail_json = null;

		} catch (Exception e) {
			e.printStackTrace();
			log.error("DashBoard  missedHostAvailability Query: "+timestampselection+"/"+resourceType+"\n"+e.getMessage());
		} finally {

		}
		return finalJson;
	}

	private static String getDownTimeStamp(long smilli, long emilli, String resourceType, String host,String resourceId,Connection uniqueconnection) throws IOException, SQLException {
		String downTimestamp="0";
		try {
			String szAvailDownTimeQuery = "select timestamp1 from servicemetrics where timestamp1 between "
				+ smilli
				+ " and "
				+ emilli
				+ " and timestamp2 is NULL and category='availabilityME' and resourcetype in ("+resourceType+") and  metricvalue is NULL  and host='"+host+"'";

			Statement st = uniqueconnection.createStatement();
			ResultSet rs1 = st.executeQuery(szAvailDownTimeQuery);

			while (rs1.next()) {
				Long szDownTime_timestamp = rs1.getLong("timestamp1");

			long szOrginalDownTime = emilli - szDownTime_timestamp;
			downTimestamp=szOrginalDownTime+"";
			}

		}
		catch(Exception e){
			e.printStackTrace();
			log.error("DashBoard Ticket Assignee-Status Query \n"+e.getMessage());
		}
		return downTimestamp;
}


	private static String getAvailSatus(String resourceType, String host,String resourceId,Connection uniqueconnection) throws IOException, SQLException {
		String downTimestamp="";
		try {
			String szAvailDownTimeQuery = "select case when max(timestamp2)>max(timestamp1) then 'OK' else 'CRITICAL' end  as timestampStatus from servicemetrics where  resourcetype in ("+resourceType+")   and host='"+host+"' and metrictype='downtime'";

			Statement st = uniqueconnection.createStatement();
			ResultSet rs1 = st.executeQuery(szAvailDownTimeQuery);

			while (rs1.next()) {
				 downTimestamp = rs1.getString("timestampStatus");
			}
		}
		catch(Exception e){
			e.printStackTrace();
			log.error("DashBoard Ticket Assignee-Status Query \n"+e.getMessage());
		}
		return downTimestamp;
}

	/**
	 * ******************************************************************************
	 * This method is returning the String of all Default values like Status,Assignee
	 * which having no data but we have to show while generating chart
	 * @param resourceType Different category as Desktop,Server,DataBase,Network,JVM
	 * @param smilli date in second as long Timestamp(Current Time-selected Time Period)
	 * @param emilli date in second as long Timestamp(Current Time)
	 * @param uniqueconnection Providing unique connection
	 * @param host for which host we are checking adapter Status
	 * @param service for which service we are checking adapter Status
	 * @param resourceId for which resourceId we are checking adapter Status
	 * @param timestamp this will give upTime to compare with adapter downTime
	 * @return adapterStatus Query executed and gives result as boolean
	 *
	 * *******************************************************************************
	 */

	private static boolean getDownTimeWhenAdapterStop(long smilli, long emilli, String resourceType, String host,String service,String resourceId,long timestamp, Connection uniqueconnection) throws IOException, SQLException {
			boolean adapterStatus=false;
			try {


				String szQuer = "select min(timestamp1)  as availmetricvalue,service,subservice,"
					+ "resourceId,resourceType  "
					+ "from servicemetrics where timestamp1 between "
					+ smilli
					+ " and "
					+ emilli
					+  " and timestamp1>"+timestamp+" and resourcetype in ("+resourceType+") and metrictype in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeNotUsedInChart")+") group by metrictype,host,service,subservice,category,resourceid,resourcetype,"
					+ "sla order by service,subservice,metrictype,host";
				Statement st = uniqueconnection.createStatement();
				ResultSet rs1 = st.executeQuery(szQuer);

				while (rs1.next()) {
					String metricvalue = rs1.getString("availmetricvalue");

					adapterStatus=true;

					long SDate = smilli;
					long EDate = emilli;
					float downtime = emilli - Long.valueOf(metricvalue).longValue();
					float first = (float) (((EDate - SDate) - downtime) / (EDate - SDate)) * 100;
					if(first<1){
						first=1f;
					}
					float sla_deviation = first;
					sz_avail_json.append("{\"ServerName\":\"" + host
									+ "\",\"ResourceType\":\"" + resourceType
									+ "\",\"ResourceNames\":\"Availability\",\"ServiceName\":\"" + service
									+ "\",\"ResourceID\":\"" + resourceId
									+ "\",\"ResourceValues\":" + sla_deviation
									+ ",\"SlaValues\":99.99,\"Health\":\"CRITICAL\"},");


				}

			}
			catch(Exception e){
				e.printStackTrace();
				log.error("DashBoard Ticket Assignee-Status Query \n"+e.getMessage());
			}
			return adapterStatus;
	}

}