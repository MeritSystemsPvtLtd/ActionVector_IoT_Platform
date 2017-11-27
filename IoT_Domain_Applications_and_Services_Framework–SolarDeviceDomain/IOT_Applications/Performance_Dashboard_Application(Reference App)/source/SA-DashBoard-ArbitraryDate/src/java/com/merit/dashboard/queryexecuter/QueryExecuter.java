
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

package com.merit.dashboard.queryexecuter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.merit.connection.ConnectionDAO;
import com.merit.dashboard.ServletContextListener;
import com.merit.dashboard.Availability.GetAvailability;
import com.merit.dashboard.DBUtil.DBUtilHelper;
import com.merit.dashboard.bizlogic.BizLogic;
import com.merit.dashboard.model.PojoObject;

/**
 * *****************************************************************************************************
 * All Status name with Count of each status,All Assignee Name with Count of each Assignee,
 * All Task-Summay with Count of each task-Summay in a single row with distinct IP(ServerName or Host).
 * Default Assignee, Status, task-summary
 *
 * *****************************************************************************************************
 */


public class QueryExecuter {
		static Logger log = Logger.getLogger(QueryExecuter.class);
		Statement stat = null;
		Connection uniqueconnection=null;
		BizLogic bizlogic=new BizLogic();
		public static PojoObject pojoObject=null;
		LinkedHashMap<String,String> ticketInfo=null;
		LinkedHashMap<String,String> serviceMetricInfo=null;

	/**
	 * **************************************************************************************************
	 * This method is creating Query to get necessay information to generate chart
	 * from these tables like gatasktypeassignee and gatask.
	 *
	 * Query is generated to keep in mind like:
	 * 1.One IP in one Row, All information related to this IP should be present in that row
	 * 2.For generating Alert information in DashBoard these count is required:
	 * Task-Summary count, Status count,Assignee Count
	 *
	 * These all above count's query, Joining with the help of left outer join and we are using
	 * user_defined/InBuilt function like array_agg() for concatenating a column and gives array for generating JSON.
	 * array_agg function in postgresql below 8 version we have to create by following command:

		create aggregate array_agg ( sfunc = array_append,  basetype = anyelement,  stype = anyarray,  initcond = '{}'  );

	 * @param customer Selected customerId
	 * @param resourceType Different category as Desktop,Server,DataBase,Network,JVM
	 * @param date from date as subtract(endDate-timestampselection) in Date Format
	 * @param date1 Current date in Date Format
	 * @return ticketInfo Ticket generated Information

	 *  **************************************************************************************************
	 */

	public LinkedHashMap<String,String> collectTicketdata(String customer,String resourceType,String date, String date1,String selection) throws IOException, SQLException {
		 ticketInfo=new LinkedHashMap<String, String>();
		try {
			uniqueconnection = ConnectionDAO.getConnection(customer+"_177");
			stat = uniqueconnection.createStatement();
			String szQuery="";

			if(selection.equals("CriticalAlerts"))
			szQuery="select servicename as ServiceName,resourcetype as ResourceType,host as ServerName,array_to_string(array_agg(summaryname),',') as ResourceNames,array_to_string(array_agg(summarycount),',') as ResourceValues,case when sum(criticalcheck)>0 then 'CRITICAL' else 'OK' end as Health,resourceid as ResourceID from (select count(*) as summarycount,attributes6 as host,attributes10 as resourcetype,attributes4 as ServiceName,attributes12 as resourceID,substring(task_summary from position('[' in task_summary) for 25) as summaryname,CASE WHEN substring(task_summary from position('[' in task_summary) for 25) like '%HSTDWN%' THEN 1 ELSE 0 END as criticalcheck from gatask where created_date between  '"+date+"' and '"+date1+"' and status!='Closed' and task_type!='SOX' and attributes10='"+resourceType+"' group by attributes6,summaryname,attributes4,attributes12,attributes10) as cAlert group by servicename, resourcetype, host,resourceid";
			else if(selection.equals("AlertsByAssignee"))
			szQuery="select servicename as ServiceName,resourcetype as ResourceType,host as ServerName,array_to_string(array_agg(assigneenames),',') as ResourceNames,array_to_string(array_agg(ticketcount),',') as ResourceValues,case when host is not null then 'OK' end as Health,resourceid as ResourceID from (select count(*)as ticketcount,g2.attributes6 as host ,g2.attributes10 as resourcetype ,g2.attributes4 as ServiceName,g1.assignee as assigneenames,g2.attributes12 as ResourceID from gatask g2 ,gatasktypeassignee g1 where created_date between  '"+date+"' and '"+date1+"' and g1.taskid=g2.task_id and action_assignee=1 and g2.attributes10='"+resourceType+"'  group by g2.attributes6,g1.assignee,attributes4,g2.attributes10,g2.attributes12) as assig group by servicename, resourcetype, host,resourceid";
			else
			szQuery="select servicename as ServiceName,resourcetype as ResourceType,host as ServerName,array_to_string(array_agg(statusnames),',') as ResourceNames,array_to_string(array_agg(statuscount),',') as ResourceValues,case when host is not null then 'OK' end as Health,resourceid as ResourceID from (select count(*) as statuscount,attributes6 as host,attributes10 as resourcetype,attributes4 as ServiceName,status as statusnames,attributes12 as resourceID from gatask where created_date between  '"+date+"' and '"+date1+"' and attributes6!='none' and attributes10='"+resourceType+"' and attributes10!='none' and attributes6 is not null and attributes12 is not null group by attributes6,attributes4,status,attributes10,attributes12) as sAlert group by servicename, resourcetype, host,resourceid";

	/**
	 * ***************************************************************************************************
	 * This Query will give Modified result from all necessary tables like gatasktypeassignee, gatask table ..
	 * here concatenating the result came from  GAtask for Summary Count and checking hostdown status
	 * array_agg function in postgresql  below 8 version we have to create by following command:
		create aggregate array_agg ( sfunc = array_append,  basetype = anyelement,  stype = anyarray,  initcond = '{}'  );
	 * ***************************************************************************************************
	 */
			log.debug("Ticket Query /"+resourceType+"/\n"+szQuery);

			ResultSet rs = stat.executeQuery(szQuery);

			while (rs.next()) {
				String alertCount="[]";
				String alertName="";
				String alertHealth="[]";
				String nameOfAlert="";
				String serverName=rs.getString("ServerName");
				String resourceID=rs.getString("ResourceID");
				String serviceName=rs.getString("ServiceName");
				 resourceType=rs.getString("ResourceType");

	/**
	 * ************************************************************************************************
	 * Default Assignee that having no Ticket Assigned for specific host
	 * ************************************************************************************************
	 * */

		 String sz_query_assignee_list = "select distinct(user_id) as status from gaoperator where user_id !='Admin' and user_id!='None' and  user_id not in (select g1.assignee from gatask g2 ,gatasktypeassignee g1 where  created_date between '"+date+"' and '"+date1+"'  and g1.taskid=g2.task_id and action_assignee=1 and g2.attributes10='"+resourceType+"'  and attributes6='"+serverName+"') order by user_id";
		 String assigneeList= getDefaultAssigneeStatus(sz_query_assignee_list,uniqueconnection);

	 /** Default Status for specific host */

		 String sz_query_status_list = " select distinct(status) as status from gastatus where status not in (select status from gatask where created_date between '"+date+"' and '"+date1+"'  and attributes6!='none' and attributes10='"+resourceType+"'  and attributes10!='none' and  attributes6 is not null and  attributes12 is not null   and attributes6='"+serverName+"') order by status";
         String statusList= getDefaultAssigneeStatus(sz_query_status_list,uniqueconnection);

         	if(rs.getString("ResourceValues")!=null)
         		alertCount="["+rs.getString("ResourceValues")+"]";
			if(rs.getString("ResourceNames")!=null)
				alertName="\'"+rs.getString("ResourceNames").replace(",","\',\'")+"\'";
			if(rs.getString("Health")!=null)
				alertHealth="[\""+rs.getString("Health")+"\"]";
			if(selection.equals("AlertsByAssignee")){
			if(alertName.equals("[]"))
				nameOfAlert="["+assigneeList.substring(1)+"]";
			else
				nameOfAlert="["+alertName+assigneeList+"]";

			}
			else if(selection.equals("AlertsByStatus")){
			if(alertName.equals("[]"))
				nameOfAlert="["+statusList.substring(1)+"]";
			else
				nameOfAlert="["+alertName+statusList+"]";

			}
			String summaryname1="";
			String summarynameconcat="";

	/**
	 * **************************************************************************************************
	 * Default Task Summary accepted from properties: File name is TaskSummay.Properties
	 * For specific resourceType and Checking Task-Summary is present or not.
	 * if present then changing from the properties(MetricMapping.prperties) file
	 * **************************************************************************************************
	 */
			if(selection.equals("CriticalAlerts")){

		String[] splitsummaryname=alertName.split(",");
		if(splitsummaryname.length>0 && !splitsummaryname[0].equalsIgnoreCase("")){
		for(int b=0;b<splitsummaryname.length;b++){
			if(resourceType.equals("server")){
				if(!splitsummaryname[b].toLowerCase().contains("fnsp") && !splitsummaryname[b].toLowerCase().contains("tem"))
					summarynameconcat=summarynameconcat+",\"" + DBUtilHelper.getMetrics_mapping_properties().getProperty(splitsummaryname[b]).trim() + "\"";
				}
				else{
					summarynameconcat=summarynameconcat+",\"" + DBUtilHelper.getMetrics_mapping_properties().getProperty(splitsummaryname[b]).trim() + "\"";
				}
			}

		summarynameconcat=summarynameconcat.substring(1);
		summaryname1=summarynameconcat;
		}

	/**
	 * **************************************************************************************************
	 * Changing Task-Summary Name Present in Properties(TaskSummary.properties) with some useful meaning
	 * from other properties(MetricMapping.properties) files
	 * **************************************************************************************************
	 * */

		String[] szArrayCriticalNames=DBUtilHelper.getProperties().getProperty(resourceType).split(",");
		for(int c=0;c<szArrayCriticalNames.length;c++){
			  if (!summaryname1.contains(DBUtilHelper.getMetrics_mapping_properties().getProperty(szArrayCriticalNames[c]).trim())) {
				  summaryname1 =summaryname1 + ",\"" + DBUtilHelper.getMetrics_mapping_properties().getProperty(szArrayCriticalNames[c]).trim() + "\"";
              }
		}
			}
		if(selection.equals("CriticalAlerts"))
		nameOfAlert="["+summaryname1+"]";

	/** Generating Map for Chart Generation  */

			ticketInfo.put(serverName+","+resourceID,"\"ServiceName\":\""+serviceName+"\",\"ResourceType\":\""+resourceType+"\",\"ServerName\":\""+serverName+"\",\"ResourceNames\":"+nameOfAlert+",\"ResourceValues\":"+alertCount+",\"SlaValues\":[10000],\"Health\":"+alertHealth+",\"ResourceID\":\""+resourceID+"\"");
		//ticketInfo.put(host+","+resourceId,"\"ServerName\":\""+host+"\",\"ServiceName\":\""+serviceName+"\",\"ResourceID\":\""+resourceId+"\",\"ResourceType\":\""+resourceType+"\",\"summarycount\":"+summarycount+",\"summaryname\":"+criticalSummaryName+",\"hstdwnstatus\":"+hstdwnstatus+",\"ticketcount\":"+ticketcount+",\"assigneenames\":"+assigneenames+",\"statusnames\":"+statusnames+",\"statuscount\":"+statuscount);
	}
	rs=null;
	stat=null;
	if(ticketInfo.isEmpty())
	ticketInfo.put("DefaultMapValue","\"summarycount\":[],\"summaryname\":[],\"hstdwnstatus\":[],\"ticketcount\":[],\"assigneenames\":[],\"statusnames\":[],\"statuscount\":[]");
	} catch (Exception e) {
		e.printStackTrace();
		log.error("DashBoard Ticket Query /"+resourceType+"/\n"+e.getMessage());
	}
	finally{

	}
	return ticketInfo;
}

	/**
	 * ***************************************************************************************************
	 * This method is returning the String of all Default values like Status,Assignee
	 * that having no data but we have to show while generating chart
	 * @param szExecuteQuery Query to be Executed
	 * @param uniqueconnection Providing unique connection
	 * @return szExecutedString gives output as string
	 *
	 * ***************************************************************************************************
	 */

	public String getDefaultAssigneeStatus(String szExecuteQuery,Connection uniqueconnection) throws IOException, SQLException {
			String szExecutedString="";
			try {
				stat = uniqueconnection.createStatement();
				ResultSet rs2 = stat.executeQuery(szExecuteQuery);
				while (rs2.next()) {
					szExecutedString=szExecutedString+",\""+rs2.getString("status")+"\"";
				}
				//uniqueconnection=null;
				stat=null;
				rs2=null;
			}
			catch(Exception e){
				e.printStackTrace();
				log.error("DashBoard Ticket Assignee-Status Query \n"+e.getMessage());
			}
			return szExecutedString;
	}

	/**
	 * ************************************************************************************************
	 * This method is giving All metricType,Avg(Metricvalues) related to each metricType,
	 * SLAValues related to each metricType,services,ResourceType,ResourceId,Servername
	 * @param customer Selected customerID
	 * @param resourceType Different category as Desktop,Server,DataBase,Network,JVM
	 * @param smilli date in second as long Timestamp(Current Time-selected Time Period)
	 * @param emilli date in second as long Timestamp(Current Time)
	 * @return serviceMetricInfo Metric information as string Json
	 *
	 * *************************************************************************************************
	 */



	public LinkedHashMap<String,String> collectMetricdata(String customer,String resourceType, String metrictypeIn, long smilli , long emilli ) throws IOException, SQLException {
		try {
			pojoObject= new PojoObject();
			serviceMetricInfo=new LinkedHashMap<String, String>();
			ArrayList<String> host_resourcetype_list=new ArrayList<String>();
			serviceMetricInfo.clear();

	/**
	 * *************************************************************************************************
	 * Here we are checking the condition of showing critical according to
	 * avg (means avg of actual value compare with avg of actual sla) or
	 * Count (means actual value compare with actual sla)
	 * *************************************************************************************************
	 * */

			String metricHealthCheckLogic="case when sum(criticalflag)>0 then 'CRITICAL' else 'OK' end";
			if(DBUtilHelper.getMetrics_mapping_properties().getProperty("HealthCheck").equals("AVG"))
				metricHealthCheckLogic="case when (avg(metricvalue)>avg(sla) and metrictype not in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+")) or (avg(metricvalue)<avg(sla) and metrictype in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+")) then  'CRITICAL' else 'OK' end";

			uniqueconnection = ConnectionDAO.getConnection(customer);
			stat = uniqueconnection.createStatement();

	/**
	 * ***************************************************************************************************
	 * Here Query is Separated according to resource type because server and Desktop taking more time
	 * because of more data , We can use the same query by removing  if condition
	 * array_agg function in postgresql  below 8 version we have to create by following command:
		create aggregate array_agg ( sfunc = array_append,  basetype = anyelement,  stype = anyarray,  initcond = '{}'  );
	 * ***************************************************************************************************
	 * */

				String szQuery="select host,service,resourcetype,resourceid,array_to_string(array_agg(metricvalue),',') as metricvalues,array_to_string(array_agg(sla),',') as slavalues ,array_to_string(array_agg(metrictype),',') as metricname,array_to_string(array_agg(criticalsum),',') as metrictypehealth,resourceSubType from "
                        + "( select def.resourcesubtype as resourceSubType,f1.host,case when  f1.host=def.host and f1.resourceid=def.resourceid then def.service end as service,resourcetype,f1.resourceid,metricvalue,metrictype,sla,criticalsum from "
                        + "( select host,service,resourcetype,resourceid,case when metrictype in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeCountAsValue")+") then count(metrictype) else   round(cast(avg(metricvalue) as numeric),2) end as metricvalue,metrictype,round(cast(avg(sla) as numeric),2) as sla, " +metricHealthCheckLogic+" as criticalsum from "
                        + "( select host,service,resourcetype,resourceid, metricvalue,metrictype,sla,case when metricvalue>sla and metrictype not in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+") or metricvalue<sla and metrictype  in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+") then 1 else 0  end as criticalflag from "
                        + "  servicemetrics "
                        + "where timestamp1 between "+ smilli+ " and "+ emilli+ " and  "
                        +       "resourcetype='"+resourceType+"' and "
                        +       "metrictype in ("+ metrictypeIn + ") and "
                        +       "metricvalue is not null and "
                        +        "metrictype not in ('downtime',"+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeNotUsedInChart")+")) as f "
                        + "group by host,service,resourcetype,resourceid,metrictype "
                        + "order by criticalsum) as f1 "
                            + "left outer join "
                        + "(select distinct host,resourceid,service,resourcesubtype from "
                        + "servicemetrics "
                        + "where resourcetype='"+resourceType+"' and "
                        +       "metrictype in ("+ metrictypeIn + ") and "
                        +       "service <>'null' and "
                        +       "resourcesubtype !='') as def using(host,resourceid)) as s1 "
                        + "group by host,service,resourcetype,resourceid,resourcesubtype order by metrictypehealth";
				if(resourceType.equals("Desktop") || resourceType.equals("server"))
				szQuery="select host,service,resourcetype,resourceid,array_to_string(array_agg(metricvalue),',') as metricvalues,array_to_string(array_agg(sla),',') as slavalues ,array_to_string(array_agg(metrictype),',') as metricname,array_to_string(array_agg(criticalsum),',') as metrictypehealth from "
                        + "( select host,resourcetype, service,al.resourceid,metricvalue,metrictype,sla,criticalsum from "
                        + "( select service,resourceid,case when metrictype in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeCountAsValue")+") then count(metrictype) else   round(cast(avg(metricvalue) as numeric),2) end as metricvalue,metrictype,round(cast(avg(sla) as numeric),2) as sla, "+metricHealthCheckLogic+" as criticalsum from "
                        + "( select service,resourceid, metricvalue,metrictype,sla,case when metricvalue>sla and metrictype not in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+") or metricvalue<sla and metrictype in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+") then 1 else 0  end as criticalflag from "
                        + "servicemetrics "
                        + "where timestamp1 between "+ smilli+ " and "+ emilli+ " and  "
                        +       "resourcetype='"+resourceType+"' and "
                        +       "metrictype in ("+ metrictypeIn + ") and "
                        +       "metricvalue is not null and "
                        +       "metrictype not in ('downtime',"+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeNotUsedInChart")+")) as f "
                        + "group by resourceid,service,metrictype ) as al "
                        + "left outer join "
                        + "(select distinct resourceid,host,resourcetype from "
                        + "servicemetrics "
                        + "where timestamp1 between "+ smilli+ " and "+ emilli+ " and "
                        +       "metrictype in ("+ metrictypeIn + ") and "
                        +       "resourcetype='"+resourceType+"') as hr "
                        + "using(resourceid) "
                        + "order by criticalsum) as fullservicetable "
                        + "group by host,service,resourcetype,resourceid order by metrictypehealth";


				log.debug("Report Metric Query /"+resourceType+"/ \n"+szQuery);
				ResultSet rs = stat.executeQuery(szQuery);

				while (rs.next()) {

					String metricvalues="[]";
					String metricname="[]";
					String slavalues="[]";
					String metrictypehealth="[]";

					String host=rs.getString("host");
					if(rs.getString("metricvalues")!=null)
					metricvalues="["+rs.getString("metricvalues")+"]";
					if(rs.getString("metricname")!=null){
						String metricnameTemp="\""+rs.getString("metricname").replace(",","\",\"")+"\"";
						metricname=bizlogic.ChangeMetricNameAsPropertyname(metricnameTemp);
					}
					if(rs.getString("slavalues")!=null)
                        slavalues="["+rs.getString("slavalues")+"]";
					if(rs.getString("metrictypehealth")!=null)
                        metrictypehealth="[\""+rs.getString("metrictypehealth").replace(",","\",\"")+"\"]";
					String serviceName=rs.getString("service");
					String resourcetype=rs.getString("resourcetype");
					String resourceId=rs.getString("resourceId");
					String resourceSubType="";
					if(resourceType.equals("Desktop") || resourceType.equals("server")){

					} else
						try {
							resourceSubType=rs.getString("resourceSubType");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


					if(serviceMetricInfo.containsKey(host+","+resourceId))
						serviceMetricInfo.remove(host+","+resourceId);
					if(!host.equals("null"))
						serviceMetricInfo.put(host+","+resourceId, "\"ServiceName\":\""+serviceName+"\",\"ResourceType\":\""+resourcetype+"\",\"ServerName\":\""+host+"\",\"ResourceNames\":"+metricname+",\"ResourceValues\":"+metricvalues+",\"SlaValues\":"+slavalues+",\"Health\":"+metrictypehealth+",\"ResourceSubType\":\"" + resourceSubType + "\",\"ResourceID\":\"" + resourceId + "\"");
					host_resourcetype_list.add(host.trim()+"/"+resourcetype.trim());
				}

	/**
	 * ***************************************************************************************************
	 * Here we are setting All Host list Available in that Period According
	 *  to this we are showing Availability
	 * ***************************************************************************************************
	 */
				pojoObject.setHostListAvailable(host_resourcetype_list);
				rs=null;
				stat=null;
				host_resourcetype_list=null;
			} catch (Exception e) {
				e.printStackTrace();
				log.error("DashBoard Report Metric Query /"+resourceType+"/ \n"+e.getMessage());
			}
			return serviceMetricInfo;
		}

	/**
	 * ***************************************************************************************************
	 * This method is giving default services
	 * with Health and Alert status for Specified period and Generating JSON
	 * with this method generateDefaultServiceResourcetypeJson()
	 * @param resourceType Different category as Desktop,Server,DataBase,Network,JVM
	 * @param startDate sub(endDate-timestampselection) in Date Format
	 * @param endDate Current date in Date Format
	 * @param timestampselection  Selected Time Period
	 * @param customer Selected customerID
	 * @return szResourceTypeJson Json having service,health and Alert
	 *
	 * ***************************************************************************************************
	 */


		public String generateDefaultServiceJson(String resourceType,
				String startDate, String endDate, String timestampselection,
				String customer) throws IOException, SQLException {
			String szServiceJson="";
			try {
				serviceMetricInfo=new LinkedHashMap<String, String>();
				ticketInfo=new LinkedHashMap<String, String>();
				SimpleDateFormat formatter1 = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date dateStart = formatter1.parse(startDate);
				Date dateEnd = formatter1.parse(endDate);
				String date = formatter1.format(dateStart);
				String date1 = formatter1.format(dateEnd);
				long smilli = dateStart.getTime() / 1000;
				long emilli = dateEnd.getTime() / 1000;
				resourceType = "";
				String szQueryMetric="select dService.service as ServiceName, case when sum(health)>0 then 'CRITICAL' when sum(health)=0 then 'OK' else 'Default' end as  Health from (select distinct service from servicemetrics where resourcetype in ("+"\'"+DBUtilHelper.getMetrics_mapping_properties().getProperty("ResourceName").replaceAll(",", "\',\'")+"\'"+") and service <> 'null') as dService left outer join (select service,CASE WHEN (metrictype in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+") and avg(metricvalue) < avg(sla)) or (metrictype not in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+") and avg(metricvalue) > avg(sla)) THEN 1 WHEN (metrictype not in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+") and avg(metricvalue) < avg(sla)) or (metrictype in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+") and avg(metricvalue) > avg(sla)) THEN 0 END as health from servicemetrics where service <> 'null' and timestamp1 between "+ smilli+ " and "+ emilli+ "  and metricvalue is not null and metrictype not in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeNotUsedInChart")+") group by service,resourcetype,metrictype) as hService using(service) group by ServiceName order by health";
				Connection dataBaseConnectionMetric=ConnectionDAO.getConnection(customer);
				String[] headingNamesMetric = { "ServiceName", "Health"};
				serviceMetricInfo=generateJsonFromGivenQuery(szQueryMetric,headingNamesMetric,dataBaseConnectionMetric);
				log.debug("Default Service Metric Query /"+timestampselection+"/ \n"+szQueryMetric);

				String szAvailJson= GetAvailability.getAvailabilityByPeriod(emilli, smilli, "'Desktop','server','DataBase','Network','JVM','Inverter'",customer,timestampselection);
				JSONArray arrJobj=null;
				JSONObject temp = null;
				String tempKey = "";
					try {
						 arrJobj = new JSONArray(szAvailJson);
						 for (int p = 0; p < arrJobj.length(); p++) {
								temp = arrJobj.getJSONObject(p);
								tempKey = temp.getString("ServiceName");
								if(serviceMetricInfo.get(tempKey)==null || temp.getString("Health").equals("CRITICAL")){
									String availValue="\"ServiceName\":\""+temp.getString("ServiceName")+"\",\"Health\":\""+temp.getString("Health")+"\"";
									serviceMetricInfo.put(tempKey,availValue);
								}
						 }
					}
					catch (Exception e) {
						e.printStackTrace();
					}


				String szQueryTicket="select service as ServiceName,case when sum(summary)>0 then 'CRITICAL' when sum(summary)=0 then 'OK' else 'Default' end as Alert from  (select attributes4 as service,CASE WHEN substring(task_summary from position('[' in task_summary) for 25) like '%HSTDWN%' THEN 1 ELSE 0 END as summary from gatask where created_date between '"+date+"' and '"+date1+"' and status!='Closed' and task_type!='SOX' and attributes4!='null' group by summary,attributes4,attributes10) as dTicket group by ServiceName";
				Connection dataBaseConnectionTicket=ConnectionDAO.getConnection(customer+"_177");
				String[] headingNamesTicket = { "ServiceName", "Alert"};
				ticketInfo=generateJsonFromGivenQuery(szQueryTicket,headingNamesTicket,dataBaseConnectionTicket);
				log.debug("Default Service Metric Query /"+timestampselection+"/ \n"+szQueryTicket);

				szServiceJson=bizlogic.generateDefaultServiceResourcetypeJson(serviceMetricInfo,ticketInfo);
				dataBaseConnectionMetric=null;
				dataBaseConnectionTicket=null;
				ticketInfo=null;
				serviceMetricInfo=null;
				formatter1=null;
			}
			catch(Exception e){
				e.printStackTrace();
				log.error("DashBoard Generate Default Service \n"+e.getMessage());
			}
			return szServiceJson;
			}

	/**
	 * ***************************************************************************************************
	 * This method is giving default Service ,ResourceType
	 * with Health and Alert status for Specified period and Generating JSON
	 * with this method generateDefaultServiceResourcetypeJson()
	 * @param resourceType Different category as Desktop,Server,DataBase,Network,JVM
	 * @param startDate sub(endDate-timestampselection) in Date Format
	 * @param endDate Current date in Date Format
	 * @param timestampselection  Selected Time Period
	 * @param customer Selected customerID
	 * @return szResourceTypeJson Json having service,Resourcetype,health and Alert
	 *
	 * ***************************************************************************************************
	 */
		public String generateDefaultResourceTypeJson(String resourceType,
				String startDate, String endDate, String timestampselection,
				String customer) throws IOException, SQLException {
			String szResourceTypeJson="";
			try {
				ticketInfo=new LinkedHashMap<String, String>();
				serviceMetricInfo=new LinkedHashMap<String, String>();
				SimpleDateFormat formatter1 = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
				Date dateStart = formatter1.parse(startDate);
				Date dateEnd = formatter1.parse(endDate);
				String date = formatter1.format(dateStart);
				String date1 = formatter1.format(dateEnd);
				long smilli = dateStart.getTime() / 1000;
				long emilli = dateEnd.getTime() / 1000;
				Connection dataBaseConnectionMetric=ConnectionDAO.getConnection(customer);
				Connection dataBaseConnectionTicket=ConnectionDAO.getConnection(customer+"_177");
				ticketInfo.clear();
				serviceMetricInfo.clear();

				String szQueryMetric="select dService.service as ServiceName, resourcetype as ResourceType ,case when sum(health)>0 then 'CRITICAL' when sum(health)=0 then 'OK' else 'Default' end as  Health from (select distinct service from servicemetrics where resourcetype in ("+"\'"+DBUtilHelper.getMetrics_mapping_properties().getProperty("ResourceName").replaceAll(",", "\',\'")+"\'"+") and service <> 'null') as dService left outer join (select service,resourcetype,CASE WHEN (metrictype in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+") and avg(metricvalue) < avg(sla)) or (metrictype not in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+") and avg(metricvalue) > avg(sla)) THEN 1 WHEN (metrictype not in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+") and avg(metricvalue) < avg(sla)) or (metrictype in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+") and avg(metricvalue) > avg(sla)) THEN 0 END as health from servicemetrics where service <> 'null' and timestamp1 between "+ smilli+ " and "+ emilli+ "  and metricvalue is not null and metrictype not in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeNotUsedInChart")+") group by service,resourcetype,metrictype) as hService using(service) group by ServiceName,ResourceType";
				String[] headingNamesMetric1 = { "ServiceName","ResourceType", "Health"};
				log.debug("Default ResourceType Metric Query /"+timestampselection+"/ \n"+szQueryMetric);
				serviceMetricInfo=generateJsonFromGivenQuery(szQueryMetric,headingNamesMetric1,dataBaseConnectionMetric);
				String szAvailJson= GetAvailability.getAvailabilityByPeriod(emilli, smilli, "'Desktop','server','DataBase','Network','JVM','Inverter'",customer,timestampselection);
				JSONArray arrJobj=null;
				JSONObject temp = null;
				String tempKey = "";
					try {
						 arrJobj = new JSONArray(szAvailJson);
						 for (int p = 0; p < arrJobj.length(); p++) {
								temp = arrJobj.getJSONObject(p);
								tempKey = temp.getString("ServiceName") + ","+ temp.getString("ResourceType");
								if(serviceMetricInfo.get(tempKey)==null || temp.getString("Health").equals("CRITICAL")){
									serviceMetricInfo.remove(temp.getString("ServiceName") + ",null");
									String availValue="\"ServiceName\":\""+temp.getString("ServiceName")+"\",\"ResourceType\":\""+temp.getString("ResourceType")+"\",\"Health\":\""+temp.getString("Health")+"\"";
									serviceMetricInfo.put(tempKey,availValue);
								}
						 }
					}
					catch (Exception e) {
						e.printStackTrace();
					}

				String szQueryTicket="select service as ServiceName,ResourceType,case when sum(summary)>0 then 'CRITICAL' when sum(summary)=0 then 'OK' else 'Default' end as Alert from  (select attributes4 as service,attributes10 as ResourceType,CASE WHEN substring(task_summary from position('[' in task_summary) for 25) like '%HSTDWN%' THEN 1 ELSE 0 END as summary from gatask where created_date between '"+date+"' and '"+date1+"' and status!='Closed' and task_type!='SOX' and attributes4!='null' group by summary,attributes4,attributes10) as dTicket group by ServiceName,ResourceType";

				String[] headingNamesTicket1 = { "ServiceName","ResourceType", "Alert"};
				log.debug("Default ResourceType Ticket Query /"+timestampselection+"/ \n"+szQueryTicket);

				ticketInfo=generateJsonFromGivenQuery(szQueryTicket,headingNamesTicket1,dataBaseConnectionTicket);

				szResourceTypeJson=bizlogic.generateDefaultServiceResourcetypeJson(serviceMetricInfo,ticketInfo);
				dataBaseConnectionMetric=null;
				dataBaseConnectionTicket=null;
				ticketInfo=null;
				serviceMetricInfo=null;
				formatter1=null;
			} catch (Exception e) {
				e.printStackTrace();
				log.error("DashBoard Generate DefaultResourceType \n"+e.getMessage());
			}
				return szResourceTypeJson;
		}

	/**
	 * ***************************************************************************************************
	 * Here we are Executing both Ticket and Metric Query  one by one because both having
	 * different dataBase Connection and sending as LinkedHashMap
	 * @param szQueryString Query to be Executed
	 * @param headingNames Name of Columns in Executed Query
	 * @param dataBaseConnection Specific query specific Connection
	 * @return szMetricTypemap  return LinkedHashMap
	 * ***************************************************************************************************
	 */


		 public LinkedHashMap<String, String> generateJsonFromGivenQuery(String szQueryString,String[] headingNames,Connection dataBaseConnection){
				LinkedHashMap<String, String> szMetricTypemap=new LinkedHashMap<String, String>();

				try{
					 Statement stat2 = dataBaseConnection.createStatement();
					 String szMetricTypeValueJson="";
					 ResultSet rs4 = stat2.executeQuery(szQueryString);
					 while (rs4.next()) {
						String szConcatColumn="";
						szMetricTypeValueJson="";
						for(int i=0;i<headingNames.length-1;i++){
							if(!headingNames[headingNames.length-1].equals("Alert"))
								if(rs4.getString(headingNames[i])!=null)
								szConcatColumn=szConcatColumn+",\""+headingNames[i]+"\":\""+rs4.getString(headingNames[i])+"\"";
							szMetricTypeValueJson=szMetricTypeValueJson+","+rs4.getString(headingNames[i]);
						}
							szConcatColumn=szConcatColumn+",\""+headingNames[headingNames.length-1]+"\":\""+rs4.getString(headingNames[headingNames.length-1])+"\"";
							 szMetricTypemap.put(szMetricTypeValueJson.substring(1), szConcatColumn.substring(1));
						}
					dataBaseConnection=null;
					stat2=null;
				 }
				 catch (Exception e) {
					e.printStackTrace();
					log.error("DashBoard Generate Default Service and ResourceType \n"+e.getMessage());
				}
				 return szMetricTypemap;
			}


		 public LinkedHashMap<String,String> collectTicketdata1(String customer,String resourceType,String date, String date1) throws IOException, SQLException {
			 ticketInfo=new LinkedHashMap<String, String>();
			try {
				uniqueconnection = ConnectionDAO.getConnection(customer+"_177");
				stat = uniqueconnection.createStatement();
				String szQuery="select host,resourcetype,ServiceName,resourceID,case when sum(criticalcheck)>0 then 'CRITICAL' else 'OK' end as health from (select attributes6 as host,attributes10 as resourcetype,attributes4 as ServiceName,attributes12 as resourceID,CASE WHEN substring(task_summary from position('[' in task_summary) for 25) like '%HSTDWN%' THEN 1 ELSE 0 END as criticalcheck from gatask where created_date between  '"+date+"' and '"+date1+"' and status!='Closed' and task_type!='SOX' and attributes10='"+resourceType+"' group by attributes4,attributes12,attributes6,attributes10,criticalcheck) as alert group by host,resourcetype,ServiceName,resourceID";
		/**
		 * ***************************************************************************************************
		 * This Query will give Modified result from all necessary tables like gatasktypeassignee, gatask table ..
		 * here concatenating the result came from  GAtask for Summary Count and checking hostdown status
		 * array_agg function in postgresql  below 8 version we have to create by following command:
			create aggregate array_agg ( sfunc = array_append,  basetype = anyelement,  stype = anyarray,  initcond = '{}'  );
		 * ***************************************************************************************************
		 */
				log.debug("Ticket Query /"+resourceType+"/\n"+szQuery);

				ResultSet rs = stat.executeQuery(szQuery);

				while (rs.next()) {
					String serviceName=rs.getString("ServiceName");
					resourceType=rs.getString("resourcetype");
					String host=rs.getString("host");
					String resourceID=rs.getString("resourceID");
					String health=rs.getString("health");


		/** Generating Map for Chart Generation  */

			ticketInfo.put(serviceName+","+host+","+resourceID,"\"hstdwnstatus\":[\""+health+"\"]");
		}
		rs=null;
		stat=null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("DashBoard Ticket Query /"+resourceType+"/\n"+e.getMessage());
		}
		finally{

		}
		return ticketInfo;
	}


		/**
		 * ************************************************************************************************
		 * This method is giving All metricType,Avg(Metricvalues) related to each metricType,
		 * SLAValues related to each metricType,services,ResourceType,ResourceId,Servername
		 * @param customer Selected customerID
		 * @param resourceType Different category as Desktop,Server,DataBase,Network,JVM
		 * @param smilli date in second as long Timestamp(Current Time-selected Time Period)
		 * @param emilli date in second as long Timestamp(Current Time)
		 * @return serviceMetricInfo Metric information as string Json
		 *
		 * *************************************************************************************************
		 */



		public LinkedHashMap<String,String> collectMetricdata1(String customer,String resourceType,long smilli , long emilli ) throws IOException, SQLException {
			try {
				serviceMetricInfo=new LinkedHashMap<String, String>();

		/**
		 * *************************************************************************************************
		 * Here we are checking the condition of showing critical according to
		 * avg (means avg of actual value compare with avg of actual sla) or
		 * Count (means actual value compare with actual sla)
		 * *************************************************************************************************
		 * */

				String metricHealthCheckLogic="case when sum(criticalflag)>0 then 'CRITICAL' else 'OK' end";
				if(DBUtilHelper.getMetrics_mapping_properties().getProperty("HealthCheck").equals("AVG"))
					metricHealthCheckLogic="case when (avg(metricvalue)>avg(sla) and metrictype not in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+")) or (avg(metricvalue)<avg(sla) and metrictype in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+")) then  'CRITICAL' else 'OK' end";


				String szQuery="select host,service,resourcetype,resourceid,array_to_string(array_agg(metrictype),',') as metricname,array_to_string(array_agg(criticalsum),',') as metrictypehealth,case when resourceSubType is null then resourcetype else resourceSubType end as resourceSubType from "
                    + "( select def.resourcesubtype as resourceSubType,def.host,def.service as service,def.resourcetype,def.resourceid as resourceid,case when metrictype is null then 'default' else metrictype end as metrictype,case when criticalsum is null then 'GREY' else criticalsum end as criticalsum from "
                    + "( select host,resourcetype,resourceid,case when metrictype in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeCountAsValue")+") then count(metrictype) else   round(cast(avg(metricvalue) as numeric),2) end as metricvalue,metrictype,round(cast(avg(sla) as numeric),2) as sla, " +metricHealthCheckLogic+" as criticalsum from "
                    + "( select host,resourcetype,resourceid, metricvalue,metrictype,sla,case when metricvalue>sla and metrictype not in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+") or metricvalue<sla and metrictype  in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+") then 1 else 0  end as criticalflag from "
                    + "  servicemetrics "
                    + "where timestamp1 between "+ smilli+ " and "+ emilli+ " and  "
                    +       "resourcetype='"+resourceType+"' and "

                    +       "metricvalue is not null and "
                    +        "metrictype not in ('downtime',"+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeNotUsedInChart")+")) as f "
                    + "group by host,resourcetype,resourceid,metrictype "
                    + "order by criticalsum) as f1 "
                        + "right outer join "
                    + "(select distinct host,resourceid,service,resourcetype,resourcesubtype from "
                    + "hostinfo "
                    + "where resourcetype='"+resourceType+"' and "
                    +       "service <>'null') as def using(host,resourceid)) as s1 "
                    + "group by host,service,resourcetype,resourceid,resourcesubtype order by metrictypehealth";


				if(resourceType.equals("Desktop") || resourceType.equals("server"))
					szQuery="select host,service,resourcetype,resourceid,array_to_string(array_agg(metrictype),',') as metricname,array_to_string(array_agg(criticalsum),',') as metrictypehealth from "
	                        + "( select host,resourcetype, hr.service,hr.resourceid as resourceid,case when metrictype is null then 'default' else metrictype end as metrictype,case when criticalsum is null then 'GREY' else criticalsum end as criticalsum from "
	                        + "( select resourceid,case when metrictype in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeCountAsValue")+") then count(metrictype) else   round(cast(avg(metricvalue) as numeric),2) end as metricvalue,metrictype,round(cast(avg(sla) as numeric),2) as sla, "+metricHealthCheckLogic+" as criticalsum from "
	                        + "( select resourceid, metricvalue,metrictype,sla,case when metricvalue>sla and metrictype not in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+") or metricvalue<sla and metrictype in ("+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")+") then 1 else 0  end as criticalflag from "
	                        + "servicemetrics "
	                        + "where timestamp1 between "+ smilli+ " and "+ emilli+ " and  "
	                        +       "resourcetype='"+resourceType+"' and "

	                        +       "metricvalue is not null and "
	                        +       "metrictype not in ('downtime',"+DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeNotUsedInChart")+")) as f "
	                        + "group by resourceid,metrictype ) as al "
	                        + "right outer join "
	                        + "(select distinct resourceid,host,service,resourcetype from "
	                        + "hostinfo "
	                        + "where resourcetype='"+resourceType+"') as hr "
	                        + "using(resourceid) "
	                        + "order by criticalsum) as fullservicetable "
	                        + "group by host,service,resourcetype,resourceid order by metrictypehealth";



				uniqueconnection = ConnectionDAO.getConnection(customer);
				stat = uniqueconnection.createStatement();

		/**
		 * ***************************************************************************************************
		 * Here Query is Separated according to resource type because server and Desktop taking more time
		 * because of more data , We can use the same query by removing  if condition
		 * array_agg function in postgresql  below 8 version we have to create by following command:
			create aggregate array_agg ( sfunc = array_append,  basetype = anyelement,  stype = anyarray,  initcond = '{}'  );
		 * ***************************************************************************************************
		 * */


					log.debug("Report Metric Query /"+resourceType+"/ \n"+szQuery);
					ResultSet rs = stat.executeQuery(szQuery);

					while (rs.next()) {
						String metricname="[]";
						String chartType="[\"0\",\"1\"]";
						String metrictypehealth="[]";

						String host=rs.getString("host");
						if(rs.getString("metrictypehealth")!=null)
							metrictypehealth="[\""+rs.getString("metrictypehealth").replace(",","\",\"")+"\"]";
						if(rs.getString("metricname")!=null){
							String metricnameTemp="\""+rs.getString("metricname").replace(",","\",\"")+"\"";
							metricname=bizlogic.ChangeMetricNameAsPropertyname(metricnameTemp);
							if(!metricnameTemp.equals("Default"))
							chartType=bizlogic.getChartType(resourceType,metricnameTemp,metrictypehealth);
						}

						String serviceName=rs.getString("service");
						String resourcetype=rs.getString("resourcetype");
						String resourceId=rs.getString("resourceId");
						String resourceSubType="resourceSubType";
						if(resourceType.equals("Desktop") || resourceType.equals("server")){

						}
						else
						 resourceSubType=rs.getString("resourceSubType");


						if(serviceMetricInfo.containsKey(serviceName+","+host+","+resourceId))
							serviceMetricInfo.remove(serviceName+","+host+","+resourceId);
						if(!host.equals("null"))
							serviceMetricInfo.put(serviceName+","+host+","+resourceId,"\"ServiceName\":\""+serviceName+"\",\"ResourceType\":\""+resourceType+"\",\"ServerName\":\""+host+"\",\"ResourceNames\":"+metricname+",\"Health\":"+metrictypehealth+",\"ResourceSubType\":\"" + resourceSubType + "\",\"ResourceChartType\":" + chartType + ",\"ResourceID\":\""+resourceId+"\"");

					}

		/**
		 * ***************************************************************************************************
		 * Here we are setting All Host list Available in that Period According
		 *  to this we are showing Availability
		 * ***************************************************************************************************
		 */

					rs=null;
					stat=null;
				} catch (Exception e) {
					e.printStackTrace();
					log.error("DashBoard Report Metric Query /"+resourceType+"/ \n"+e.getMessage());
				}

				return serviceMetricInfo;
			}


		public String getWatchDogAlertJson(String customer){
			String watchDogQuery="select metrictype,to_char(to_timestamp(max(timestamp1)), 'yyyy/MM/dd HH24:mi:SS') as TimeStamps,resourcetype from servicemetrics where metrictype in ('NetAgent','dbmonitor','jvmlocal','jvmremote','gmetad','desktop') group by metrictype,resourcetype";
			String watchdogJson="[";
			try{
				uniqueconnection=ConnectionDAO.getConnection(customer);
				 Statement stat2 = uniqueconnection.createStatement();
				 String szMetricTypeValueJson="";
				 ResultSet rs4 = stat2.executeQuery(watchDogQuery);
				 while (rs4.next()) {
					String resourceType=rs4.getString("resourcetype");
					String metrictype=rs4.getString("metrictype");
					String timestamp=rs4.getString("TimeStamps");
					 szMetricTypeValueJson=szMetricTypeValueJson+",{\"ResourceType\":\""+resourceType+"\",\"MetricType\":\""+metrictype+"\",\"Timestamps\":\""+timestamp+"\"}";
				 }
				 if(szMetricTypeValueJson.equals("")){
					 watchdogJson="[]";
				 }
				 else{
					 watchdogJson=watchdogJson+szMetricTypeValueJson.substring(1)+"]";
				 }

				stat2=null;
			 }
			 catch (Exception e) {
				e.printStackTrace();
				log.error("DashBoard Generate Default Service and ResourceType \n"+e.getMessage());
			}
			 return watchdogJson;
		}
		public String getMetricResourceTypeMapping(String customer){
	String MetricResourceTypeMappingJson="";
	String szQuery="select distinct metrictype,resourcetype from smetricslathresholds order by resourcetype,metrictype";
	try{
		uniqueconnection=ConnectionDAO.getConnection(customer);
		Statement stat2 = uniqueconnection.createStatement();
		String propertyMetricName1="";
		String resourceType1="";
		String propertyMetricName2="";
		String resourceType2="";
		String propertyMetricName3="";
		String resourceType3="";
		String propertyMetricName4="";
		String resourceType4="";
		String propertyMetricName5="";
		String resourceType5="";
		String propertyMetricName6="";
		String resourceType6="";
		ResultSet rs4 = stat2.executeQuery(szQuery);
		while (rs4.next()) {
			String resourceType= rs4.getString("resourcetype");
			if(resourceType.equals("Desktop")){
				propertyMetricName1 = propertyMetricName1+ ",\""+ DBUtilHelper.getMetrics_mapping_properties().getProperty(rs4.getString("metrictype")) + "\"";
				resourceType1=resourceType;
			}
			else if(resourceType.equals("server")){
				 propertyMetricName2 = propertyMetricName2+ ",\""+ DBUtilHelper.getMetrics_mapping_properties().getProperty(rs4.getString("metrictype")) + "\"";
				 resourceType2=resourceType;
			 }
			 else if(resourceType.equals("DataBase")){
				 propertyMetricName3 = propertyMetricName3+ ",\""+ DBUtilHelper.getMetrics_mapping_properties().getProperty(rs4.getString("metrictype")) + "\"";
				 resourceType3=resourceType;
			 }
			 else if(resourceType.equals("Network")){
				 propertyMetricName4 = propertyMetricName4+ ",\""+ DBUtilHelper.getMetrics_mapping_properties().getProperty(rs4.getString("metrictype")) + "\"";
				 resourceType4=resourceType;
			 }
			 else if(resourceType.equals("JVM")){
				 propertyMetricName5 = propertyMetricName5+ ",\""+ DBUtilHelper.getMetrics_mapping_properties().getProperty(rs4.getString("metrictype")) + "\"";
				 resourceType5=resourceType;
			 }
			 else if(resourceType.equals("Inverter")){
				 propertyMetricName6 = propertyMetricName6+ ",\""+ DBUtilHelper.getMetrics_mapping_properties().getProperty(rs4.getString("metrictype")) + "\"";
				 resourceType6=resourceType;
			 }

}
		 MetricResourceTypeMappingJson="[{\"ResourceType\":\""+resourceType1+"\",\"ResourceNames\":["+propertyMetricName1.substring(1)+"]},{\"ResourceType\":\""+resourceType2+"\",\"ResourceNames\":["+propertyMetricName2.substring(1)+"]},{\"ResourceType\":\""+resourceType3+"\",\"ResourceNames\":["+propertyMetricName3.substring(1)+"]},{\"ResourceType\":\""+resourceType4+"\",\"ResourceNames\":["+propertyMetricName4.substring(1)+"]},{\"ResourceType\":\""+resourceType5+"\",\"ResourceNames\":["+propertyMetricName5.substring(1)+"]},{\"ResourceType\":\""+resourceType6+"\",\"ResourceNames\":["+propertyMetricName6.substring(1)+"]}]";
	}
	catch (Exception e) {
		e.printStackTrace();
	}
	return MetricResourceTypeMappingJson;
	}
}
