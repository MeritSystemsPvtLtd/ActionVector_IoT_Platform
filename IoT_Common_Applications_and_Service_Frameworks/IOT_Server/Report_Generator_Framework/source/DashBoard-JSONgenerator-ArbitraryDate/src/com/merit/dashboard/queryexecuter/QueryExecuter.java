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

import com.prokosha.dbconnection.ConnectionDAO;
import main.ServiceThreadListener;
import com.merit.dashboard.Availability.GetAvailability;
import com.merit.dashboard.DBUtil.DBUtilHelper;
import com.merit.dashboard.DateGenerator;
import com.merit.dashboard.bizlogic.BizLogic;
import com.merit.dashboard.jsongenerator.PerformanceRatioGenerator;
import com.merit.dashboard.model.PojoObject;
import com.merit.dashboard.util.MetricUOM;
import com.merit.dashboard.util.ResourceConfiguration;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * *****************************************************************************************************
 * All Status name with Count of each status,All Assignee Name with Count of
 * each Assignee, All Task-Summay with Count of each task-Summay in a single row
 * with distinct IP(ServerName or Host). Default Assignee, Status, task-summary
 *
 * *****************************************************************************************************
 */
public class QueryExecuter {

    static Logger log = Logger.getLogger(QueryExecuter.class);
//    Statement stat = null;
//    Connection uniqueconnection = null;
    BizLogic bizlogic = new BizLogic();
    public static PojoObject pojoObject = null;
    LinkedHashMap<String, String> ticketInfo = null;
    LinkedHashMap<String, String> serviceMetricInfo = null;

    /**
     * **************************************************************************************************
     * This method is creating Query to get necessay information to generate
     * chart from these tables like gatasktypeassignee and gatask.
     *
     * Query is generated to keep in mind like: 1.One IP in one Row, All
     * information related to this IP should be present in that row 2.For
     * generating Alert information in DashBoard these count is required:
     * Task-Summary count, Status count,Assignee Count
     *
     * These all above count's query, Joining with the help of left outer join
     * and we are using user_defined/InBuilt function like array_agg() for
     * concatenating a column and gives array for generating JSON. array_agg
     * function in postgresql below 8 version we have to create by following
     * command:
     *
     * create aggregate array_agg ( sfunc = array_append, basetype = anyelement,
     * stype = anyarray, initcond = '{}' );
     *
     * @param customer Selected customerId
     * @param resourceType Different category as
     * Desktop,Server,DataBase,Network,JVM
     * @param date from date as subtract(endDate-timestampselection) in Date
     * Format
     * @param date1 Current date in Date Format
     * @return ticketInfo Ticket generated Information
     *
     *  **************************************************************************************************
     */
    public LinkedHashMap<String, String> collectTicketdata(String customer, String resourceType, String cCustomer, String service,
            String date, String date1, String selection) throws IOException, SQLException {
        ticketInfo = new LinkedHashMap<String, String>();
        try {
//            uniqueconnection = ConnectionDAO.getConnection(customer + "");
//            stat = uniqueconnection.createStatement();

            String szQuery = "";

            if (selection.equals("CriticalAlerts")) {
                szQuery = "select servicename as ServiceName,resourcetype as ResourceType,host as ServerName,"
                        + "array_to_string(array_agg(summaryname),',') as ResourceNames,array_to_string(array_agg(summarycount),',') "
                        + "as ResourceValues,case when sum(criticalcheck)>0 then 'CRITICAL' else 'OK' end as Health,"
                        + "resourceid as ResourceID from (select count(*) as summarycount,attributes6 as host,"
                        + "attributes10 as resourcetype,attributes4 as ServiceName,attributes12 as resourceID,"
                        + "substring(task_summary from position('[' in task_summary) for 25) as summaryname,"
                        + "CASE WHEN substring(task_summary from position('[' in task_summary) for 25) like '%HSTDWN%' THEN 1 ELSE 0 END "
                        + "as criticalcheck from gatask where created_date between  '" + date + "' and '" + date1 + "' and "
                        + "status!='Closed' and task_type!='SOX' and attributes10='" + resourceType + "' and attributes4='" + service
                        + "' group by attributes6,summaryname,attributes4,attributes12,attributes10) as cAlert group by servicename, "
                        + "resourcetype, host,resourceid";
            } else if (selection.equals("AlertsByAssignee")) {
                szQuery = "select servicename as ServiceName,resourcetype as ResourceType,host as ServerName,"
                        + "array_to_string(array_agg(assigneenames),',') as ResourceNames,array_to_string(array_agg(ticketcount),',') "
                        + "as ResourceValues,case when host is not null then 'OK' end as Health,resourceid as ResourceID from "
                        + "(select count(*)as ticketcount,g2.attributes6 as host ,g2.attributes10 as resourcetype ,g2.attributes4 "
                        + "as ServiceName,g1.assignee as assigneenames,g2.attributes12 as ResourceID from gatask g2 ,"
                        + "gatasktypeassignee g1 where created_date between  '" + date + "' and '" + date1 + "' and "
                        + "g1.taskid=g2.task_id and action_assignee=1 and g2.attributes10='" + resourceType
                        + "' and attributes4='" + service + "' group by g2.attributes6,g1.assignee,attributes4,g2.attributes10,g2.attributes12) as assig "
                        + "group by servicename, resourcetype, host,resourceid";
            } else {
                szQuery = "select servicename as ServiceName,resourcetype as ResourceType,host as ServerName,"
                        + "array_to_string(array_agg(statusnames),',') as ResourceNames,array_to_string(array_agg(statuscount),',') "
                        + "as ResourceValues,case when host is not null then 'OK' end as Health,resourceid as ResourceID from "
                        + "(select count(*) as statuscount,attributes6 as host,attributes10 as resourcetype,attributes4 as "
                        + "ServiceName,status as statusnames,attributes12 as resourceID from gatask where created_date between  '"
                        + date + "' and '" + date1 + "' and attributes6!='none' and attributes10='" + resourceType + "' and "
                        + "attributes4='" + service + "' and attributes10!='none' and attributes6 is not null and attributes12 is not null group by attributes6,"
                        + "attributes4,status,attributes10,attributes12) as sAlert group by servicename, resourcetype, host,resourceid";
            }

            /**
             * ***************************************************************************************************
             * This Query will give Modified result from all necessary tables
             * like gatasktypeassignee, gatask table .. here concatenating the
             * result came from GAtask for Summary Count and checking hostdown
             * status array_agg function in postgresql below 8 version we have
             * to create by following command: create aggregate array_agg (
             * sfunc = array_append, basetype = anyelement, stype = anyarray,
             * initcond = '{}' );
             * ***************************************************************************************************
             */
            System.out.println("Ticket Query /" + resourceType + "/\n" + szQuery);
            System.out.println("Ticket Query /" + resourceType + "/\n" + szQuery);

            ResultSet rs = ConnectionDAO.executerQuery(szQuery, customer);//stat.executeQuery(szQuery);
            String alertCount;//
            String alertName;//
            String alertHealth;//
            String nameOfAlert;//
            String serverName;//
            String resourceID;//
            String serviceName;//
            String sz_query_assignee_list;//
            String assigneeList;//
            String sz_query_status_list;//
            String statusList;//
            String summaryname1;//
            String summarynameconcat;//
            String[] splitsummaryname;//
            String[] szArrayCriticalNames;//
            String tmpName;//
            while (rs.next()) {
                alertCount = "[]";
                alertName = "";
                alertHealth = "[]";
                nameOfAlert = "";
                serverName = rs.getString("ServerName");
                resourceID = rs.getString("ResourceID");
                serviceName = rs.getString("ServiceName");
                resourceType = rs.getString("ResourceType");

                /**
                 * ************************************************************************************************
                 * Default Assignee that having no Ticket Assigned for specific
                 * host
                 * ************************************************************************************************
                 *
                 */
                sz_query_assignee_list = "select distinct(user_id) as status from gaoperator where user_id !='Admin' "
                        + "and user_id!='None' and  user_id not in (select g1.assignee from gatask g2 ,"
                        + "gatasktypeassignee g1 where  created_date between '" + date + "' and '" + date1 + "'  and "
                        + "g1.taskid=g2.task_id and action_assignee=1 and g2.attributes10='" + resourceType
                        + "' and attributes4='" + service + "' and attributes4='" + service + "'  and attributes6='" + serverName + "') order by user_id";
                assigneeList = getDefaultAssigneeStatus(sz_query_assignee_list, customer);

                /**
                 * Default Status for specific host
                 */
                sz_query_status_list = " select distinct(status) as status from gastatus where status not in "
                        + "(select status from gatask where created_date between '" + date + "' and '" + date1
                        + "'  and attributes6!='none' and attributes10='" + resourceType + "' and attributes4='" + service + "' and attributes4='" + service + "' and attributes4='" + service
                        + "'  and attributes10!='none' and  attributes6 is not null and  attributes12 is not null   and attributes6='" + serverName + "')"
                        + " order by status";
                statusList = getDefaultAssigneeStatus(sz_query_status_list, customer);

                if (rs.getString("ResourceValues") != null) {
                    alertCount = "[" + rs.getString("ResourceValues") + "]";
                }
                if (rs.getString("ResourceNames") != null) {
                    alertName = "\'" + rs.getString("ResourceNames").replace(",", "\',\'") + "\'";
                }
                if (rs.getString("Health") != null) {
                    alertHealth = "[\"" + rs.getString("Health") + "\"]";
                }
                if (selection.equals("AlertsByAssignee")) {
                    if (alertName.equals("")) {
                        nameOfAlert = "[" + assigneeList.substring(1) + "]";
                    } else {
                        //nameOfAlert = "[" + alertName + assigneeList + "]";
                        tmpName = alertName.replaceAll("\'", "\"");
                        nameOfAlert = "[" + tmpName + assigneeList + "]";
                    }

                } else if (selection.equals("AlertsByStatus")) {
                    if (alertName.equals("")) {
                        nameOfAlert = "[" + statusList.substring(1) + "]";
                    } else {
                        //nameOfAlert = "[" + alertName + statusList + "]";
                        tmpName = alertName.replaceAll("\'", "\"");
                        nameOfAlert = "[" + tmpName + statusList + "]";
                    }

                }
                summaryname1 = "";
                summarynameconcat = "";

                /**
                 * **************************************************************************************************
                 * Default Task Summary accepted from properties: File name is
                 * TaskSummay.Properties For specific resourceType and Checking
                 * Task-Summary is present or not. if present then changing from
                 * the properties(MetricMapping.prperties) file
                 * **************************************************************************************************
                 */
                if (selection.equals("CriticalAlerts")) {

                    splitsummaryname = alertName.split(",");
                    if (splitsummaryname.length > 0 && !splitsummaryname[0].equalsIgnoreCase("")) {
                        for (int b = 0; b < splitsummaryname.length; b++) {
                            System.out.println("splitsummaryname==>>" + splitsummaryname[b]);
                            if (resourceType.equals("server")) {
                                if (!splitsummaryname[b].toLowerCase().contains("fnsp") && !splitsummaryname[b].toLowerCase().contains("tem")) {
                                    //System.out.println("183>>>>splitsummaryname[b] = " + splitsummaryname[b] + ", DBUtilHelper.getMetrics_mapping_properties().getProperty(splitsummaryname[b]) = " + DBUtilHelper.getMetrics_mapping_properties().getProperty(splitsummaryname[b]).trim());
                                    summarynameconcat = summarynameconcat + ",\"" + DBUtilHelper.getMetrics_mapping_properties().getProperty(splitsummaryname[b]).trim() + "\"";
                                }
                            } else {
                                summarynameconcat = summarynameconcat + ",\"" + DBUtilHelper.getMetrics_mapping_properties().getProperty(splitsummaryname[b]).trim() + "\"";
                            }
                        }

                        summarynameconcat = summarynameconcat.substring(1);
                        summaryname1 = summarynameconcat;
                    }

                    /**
                     * **************************************************************************************************
                     * Changing Task-Summary Name Present in
                     * Properties(TaskSummary.properties) with some useful
                     * meaning from other properties(MetricMapping.properties)
                     * files
                     * **************************************************************************************************
                     *
                     */
                    szArrayCriticalNames = DBUtilHelper.getProperties().getProperty(resourceType).split(",");
                    for (int c = 0; c < szArrayCriticalNames.length; c++) {
                        if (!summaryname1.contains(DBUtilHelper.getMetrics_mapping_properties().getProperty(szArrayCriticalNames[c]).trim())) {
                            summaryname1 = summaryname1 + ",\"" + DBUtilHelper.getMetrics_mapping_properties().getProperty(szArrayCriticalNames[c]).trim() + "\"";
                        }
                    }
                }
                if (selection.equals("CriticalAlerts")) {
                    nameOfAlert = "[" + summaryname1 + "]";
                }

                /**
                 * Generating Map for Chart Generation
                 */
                ticketInfo.put(serverName + "," + resourceID, "\"ServiceName\":\"" + serviceName + "\",\"ResourceType\":\"" + resourceType + "\",\"ServerName\":\"" + serverName + "\",\"ResourceNames\":" + nameOfAlert + ",\"ResourceValues\":" + alertCount + ",\"SlaValues\":[10000],\"Health\":" + alertHealth + ",\"ResourceID\":\"" + resourceID + "\"");
                //ticketInfo.put(host+","+resourceId,"\"ServerName\":\""+host+"\",\"ServiceName\":\""+serviceName+"\",\"ResourceID\":\""+resourceId+"\",\"ResourceType\":\""+resourceType+"\",\"summarycount\":"+summarycount+",\"summaryname\":"+criticalSummaryName+",\"hstdwnstatus\":"+hstdwnstatus+",\"ticketcount\":"+ticketcount+",\"assigneenames\":"+assigneenames+",\"statusnames\":"+statusnames+",\"statuscount\":"+statuscount);
            }
            ConnectionDAO.closeStatement();

            if (ticketInfo.isEmpty()) {
                ticketInfo.put("DefaultMapValue", "\"summarycount\":[],\"summaryname\":[],\"hstdwnstatus\":[],\"ticketcount\":[],\"assigneenames\":[],\"statusnames\":[],\"statuscount\":[]");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("DashBoard Ticket Query /" + resourceType + "/\n" + e.getMessage());
        } finally {
        }
        return ticketInfo;
    }

    /**
     * ***************************************************************************************************
     * This method is returning the String of all Default values like
     * Status,Assignee that having no data but we have to show while generating
     * chart
     *
     * @param szExecuteQuery Query to be Executed
     * @param uniqueconnection Providing unique connection
     * @return szExecutedString gives output as string
     *
     * ***************************************************************************************************
     */
    public String getDefaultAssigneeStatus(String szExecuteQuery, String customer) throws IOException, SQLException {
        String szExecutedString = "";
        try {
//            stat = uniqueconnection.createStatement();
            System.out.println("szExecuteQuery :" + szExecuteQuery);
            log.info(szExecuteQuery);
            ResultSet rs2 = ConnectionDAO.executerQuery(szExecuteQuery, customer);//stat.executeQuery(szExecuteQuery);
            while (rs2.next()) {
                szExecutedString = szExecutedString + ",\"" + rs2.getString("status") + "\"";
            }
            //uniqueconnection=null;
            ConnectionDAO.closeStatement();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("DashBoard Ticket Assignee-Status Query \n" + e.getMessage());
        }
        return szExecutedString;
    }

    /**
     * ************************************************************************************************
     * This method is giving All metricType,Avg(Metricvalues) related to each
     * metricType, SLAValues related to each
     * metricType,services,ResourceType,ResourceId,Servername
     *
     * @param customer Selected customerID
     * @param resourceType Different category as
     * Desktop,Server,DataBase,Network,JVM
     * @param smilli date in second as long Timestamp(Current Time-selected Time
     * Period)
     * @param emilli date in second as long Timestamp(Current Time)
     * @return serviceMetricInfo Metric information as string Json
     *
     * *************************************************************************************************
     */
    public LinkedHashMap<String, String> collectMetricdata(String customer, String resourceType, String cCustomer, String service,
            String metrictypeIn, long smilli, long emilli) throws IOException, SQLException {
        try {
            pojoObject = new PojoObject();
            serviceMetricInfo = new LinkedHashMap<String, String>();
            ArrayList<String> host_resourcetype_list = new ArrayList<String>();
            serviceMetricInfo.clear();

            /**
             * *************************************************************************************************
             * Here we are checking the condition of showing critical according
             * to avg (means avg of actual value compare with avg of actual sla)
             * or Count (means actual value compare with actual sla)
             * *************************************************************************************************
             *
             */
            String metricHealthCheckLogic = "case when sum(criticalflag)>0 then 'CRITICAL' else 'OK' end";
            if (DBUtilHelper.getMetrics_mapping_properties().getProperty("HealthCheck").equals("AVG")) {
                metricHealthCheckLogic = "case when (avg(metricvalue)>avg(sla) and metrictype not in (" + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger") + ")) or (avg(metricvalue)<avg(sla) and metrictype in (" + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger") + ")) then  'CRITICAL' else 'OK' end";
            }

//            uniqueconnection = ConnectionDAO.getConnection(customer);
//            stat = uniqueconnection.createStatement();


            /**
             * ***************************************************************************************************
             * Here Query is Separated according to resource type because server
             * and Desktop taking more time because of more data , We can use
             * the same query by removing if condition array_agg function in
             * postgresql below 8 version we have to create by following
             * command: create aggregate array_agg ( sfunc = array_append,
             * basetype = anyelement, stype = anyarray, initcond = '{}' );
             * ***************************************************************************************************
             *
             */
            String szQuery = "select host,service,resourcetype,resourceid,array_to_string(array_agg(metricvalue),',') as metricvalues,"
                    + "array_to_string(array_agg(sla),',') as slavalues ,array_to_string(array_agg(metrictype),',') as metricname,"
                    + "array_to_string(array_agg(criticalsum),',') as metrictypehealth,resourceSubType from "
                    + "( select def.resourcesubtype as resourceSubType,f1.host,case when  f1.host=def.host and "
                    + "f1.resourceid=def.resourceid then def.service end as service,resourcetype,f1.resourceid,metricvalue,"
                    + "metrictype,sla,criticalsum from ( select host,service,resourcetype,resourceid,case when metrictype in ("
                    + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeCountAsValue") + ") then count(metrictype) "
                    + "else   round(cast(avg(metricvalue) as numeric),2) end as metricvalue,metrictype,round(cast(avg(sla) as numeric),2)"
                    + " as sla, " + metricHealthCheckLogic + " as criticalsum from ( select host,service,resourcetype,resourceid, "
                    + "metricvalue,metrictype,sla,case when metricvalue>sla and metrictype not in ("
                    + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger") + ") "
                    + "or metricvalue<sla and metrictype  in (" + DBUtilHelper.getMetrics_mapping_properties().getProperty(
                    "MetricTypeBelowThresholdIsDanger") + ") then 1 else 0  end as criticalflag from servicemetrics "
                    + "where timestamp1 between " + smilli + " and " + emilli + " and  "
                    + "resourcetype='" + resourceType + "' and "
                    + "customerid=(select id from customerinfo where customername='" + cCustomer + "') and "
                    + "service='" + service + "' and "
                    + "metrictype in (" + metrictypeIn + ") and "
                    + "metricvalue is not null and "
                    + "metrictype not in ('downtime'," + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeNotUsedInChart") + ")) as f "
                    + "group by host,service,resourcetype,resourceid,metrictype "
                    + "order by criticalsum) as f1 "
                    + "left outer join "
                    + "(select distinct host,resourceid,service,resourcesubtype from "
                    + "servicemetrics "
                    + "where resourcetype='" + resourceType + "' and "
                    + "customerid=(select id from customerinfo where customername='" + cCustomer + "') and "
                    + "service='" + service + "' and "
                    + "metrictype in (" + metrictypeIn + ") and "
                    + "service <>'null' and "
                    + "resourcesubtype !='') as def using(host,resourceid)) as s1 "
                    + "group by host,service,resourcetype,resourceid,resourcesubtype order by metrictypehealth";
            if (resourceType.equals("Desktop") || resourceType.equals("server")) {
                szQuery = "select host,service,resourcetype,resourceid,array_to_string(array_agg(metricvalue),',') as metricvalues,array_to_string(array_agg(sla),',') as slavalues ,array_to_string(array_agg(metrictype),',') as metricname,array_to_string(array_agg(criticalsum),',') as metrictypehealth from "
                        + "( select host,resourcetype, service,al.resourceid,metricvalue,metrictype,sla,criticalsum from "
                        + "( select service,resourceid,case when metrictype in (" + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeCountAsValue") + ") then count(metrictype) else   round(cast(avg(metricvalue) as numeric),2) end as metricvalue,metrictype,round(cast(avg(sla) as numeric),2) as sla, " + metricHealthCheckLogic + " as criticalsum from "
                        + "( select service,resourceid, metricvalue,metrictype,sla,case when metricvalue>sla and metrictype not in (" + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger") + ") or metricvalue<sla and metrictype in (" + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger") + ") then 1 else 0  end as criticalflag from "
                        + "servicemetrics "
                        + "where timestamp1 between " + smilli + " and " + emilli + " and  "
                        + "resourcetype='" + resourceType + "' and "
                        + "customerid=(select id from customerinfo where customername='" + cCustomer + "') and "
                        + "service='" + service + "' and "
                        + "metrictype in (" + metrictypeIn + ") and "
                        + "metricvalue is not null and "
                        + "metrictype not in ('downtime'," + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeNotUsedInChart") + ")) as f "
                        + "group by resourceid,service,metrictype ) as al "
                        + "left outer join "
                        + "(select distinct resourceid,host,resourcetype from "
                        + "servicemetrics "
                        + "where timestamp1 between " + smilli + " and " + emilli + " and "
                        + "metrictype in (" + metrictypeIn + ") and "
                        + "resourcetype='" + resourceType + "' and "
                        + "customerid=(select id from customerinfo where customername='" + cCustomer + "') and "
                        + "service='" + service + "') as hr "
                        + "using(resourceid) "
                        + "order by criticalsum) as fullservicetable "
                        + "group by host,service,resourcetype,resourceid order by metrictypehealth";
            }
            System.out.println("Report Metric Query /" + resourceType + "/ \n" + szQuery);

            System.out.println("Report Metric Query /" + resourceType + "/ \n" + szQuery);
            ResultSet rs = ConnectionDAO.executerQuery(szQuery, customer);//stat.executeQuery(szQuery);
            String metricvalues;//
            String metricname;//
            String slavalues;//
            String metrictypehealth;//
            String host;//
            String metricnameTemp;//
            String serviceName;//
            String resourcetype;//
            String resourceId;//
            String resourceSubType;//
            while (rs.next()) {

                metricvalues = "[]";
                metricname = "[]";
                slavalues = "[]";
                metrictypehealth = "[]";

                host = rs.getString("host");
                if (rs.getString("metricvalues") != null) {
                    metricvalues = "[" + rs.getString("metricvalues") + "]";
                }
                if (rs.getString("metricname") != null) {
                    metricnameTemp = "\"" + rs.getString("metricname").replace(",", "\",\"") + "\"";
                    metricname = bizlogic.ChangeMetricNameAsPropertyname(metricnameTemp);
                }
                if (rs.getString("slavalues") != null) {
                    slavalues = "[" + rs.getString("slavalues") + "]";
                }
                if (rs.getString("metrictypehealth") != null) {
                    metrictypehealth = "[\"" + rs.getString("metrictypehealth").replace(",", "\",\"") + "\"]";
                }
                serviceName = rs.getString("service");
                resourcetype = rs.getString("resourcetype");
                resourceId = rs.getString("resourceId");
                resourceSubType = "";
                if (resourceType.equals("Desktop") || resourceType.equals("server")) {
                } else {
                    resourceSubType = rs.getString("resourceSubType");
                }


                if (serviceMetricInfo.containsKey(host + "," + resourceId)) {
                    serviceMetricInfo.remove(host + "," + resourceId);
                }
                if (!host.equals("null")) {
                    serviceMetricInfo.put(host + "," + resourceId, "\"ServiceName\":\"" + serviceName + "\",\"ResourceType\":\"" + resourcetype + "\",\"ServerName\":\"" + host + "\",\"ResourceNames\":" + metricname + ",\"ResourceValues\":" + metricvalues + ",\"SlaValues\":" + slavalues + ",\"Health\":" + metrictypehealth + ",\"ResourceSubType\":\"" + resourceSubType + "\",\"ResourceID\":\"" + resourceId + "\"");
                }
                host_resourcetype_list.add(host.trim() + "/" + resourcetype.trim());
            }

            /**
             * ***************************************************************************************************
             * Here we are setting All Host list Available in that Period
             * According to this we are showing Availability
             * ***************************************************************************************************
             */
            pojoObject.setHostListAvailable(host_resourcetype_list);
            ConnectionDAO.closeStatement();
            host_resourcetype_list = null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("DashBoard Report Metric Query /" + resourceType + "/ \n" + e.getMessage());
        }
        return serviceMetricInfo;
    }

    /**
     * ***************************************************************************************************
     * This method is giving default services with Health and Alert status for
     * Specified period and Generating JSON with this method
     * generateDefaultServiceResourcetypeJson()
     *
     * @param resourceType Different category as
     * Desktop,Server,DataBase,Network,JVM
     * @param startDate sub(endDate-timestampselection) in Date Format
     * @param endDate Current date in Date Format
     * @param timestampselection Selected Time Period
     * @param customer Selected customerID
     * @return szResourceTypeJson Json having service,health and Alert
     *
     * ***************************************************************************************************
     */
    public String generateServicesLeftGridJson(String cCustomer,
            String startDate, String endDate, String timestampselection,
            String customer) throws IOException, SQLException {
        String szServiceJson = "";
        ResultSet rs = null;
        String cquery = "";
        String addedJson = "";
        try {
            serviceMetricInfo = new LinkedHashMap<String, String>();
            ticketInfo = new LinkedHashMap<String, String>();
            SimpleDateFormat formatter1 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            Date dateStart = formatter1.parse(startDate);
            Date dateEnd = formatter1.parse(endDate);
            String date = formatter1.format(dateStart);
            String date1 = formatter1.format(dateEnd);
            long smilli = dateStart.getTime() / 1000;
            long emilli = dateEnd.getTime() / 1000;
            String szQueryMetric = "select dService.service as ServiceName, case when sum(health)>0 then 'CRITICAL' "
                    + "when sum(health)=0 then 'OK' else 'Default' end as  Health from "
                    + "(select distinct service from servicemetrics where resourcetype in (" + "\'"
                    + DBUtilHelper.getMetrics_mapping_properties().getProperty("ResourceName").replaceAll(",", "\',\'") + "\'"
                    + ") and service <> 'null' and customerid=(select id from customerinfo where customername='" + cCustomer
                    + "')) as dService left outer join (select service,CASE WHEN (metrictype in ("
                    + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")
                    + ") and avg(metricvalue) < avg(sla)) or (metrictype not in ("
                    + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")
                    + ") and avg(metricvalue) > avg(sla)) THEN 1 WHEN (metrictype not in ("
                    + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")
                    + ") and avg(metricvalue) < avg(sla)) or (metrictype in ("
                    + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")
                    + ") and avg(metricvalue) > avg(sla)) THEN 0 END as health from servicemetrics where service <> 'null' "
                    + "and timestamp1 between " + smilli + " and " + emilli + "  and metricvalue is not null and "
                    + "customerid=(select id from customerinfo where customername='" + cCustomer + "') and "
                    + "metrictype not in (" + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeNotUsedInChart")
                    + ") group by customerid,service,resourcetype,resourceid,metrictype) as hService using(service) group by ServiceName order by health";
//            Connection dataBaseConnectionMetric = ConnectionDAO.getConnection(customer);
            String[] headingNamesMetric = {"ServiceName", "Health"};
            serviceMetricInfo = generateJsonFromGivenQuery(szQueryMetric, headingNamesMetric, customer);
            serviceMetricInfo = modifyHashMap(serviceMetricInfo);
            System.out.println("Default Service Metric Query /" + timestampselection + "/ \n" + szQueryMetric);

            String resTypes = DBUtilHelper.getMetrics_mapping_properties().getProperty("ResourceName");
            resTypes = "'" + resTypes.replaceAll(",", "','") + "'";
            System.out.println("resTypes==>>" + resTypes);
            //String szAvailJson = GetAvailability.getAvailabilityByPeriod(emilli, smilli, 
            //"'Desktop','server','DataBase','Network','JVM'", customer, timestampselection);
            //String szAvailJson = GetAvailability.getAvailabilityByPeriod(emilli, smilli, resTypes, cCustomer, null, customer, timestampselection);
            String szAvailJson = GetAvailability.getAvailabilityByPeriod(emilli, smilli, resTypes, customer, timestampselection,
                    cCustomer, null);
            org.json.JSONArray arrJobj = null;
            org.json.JSONObject temp = null;
            String tempKey = "";
            String chartType = "[\"0\",\"1\"]";
            String resourceId = "[\"Default\"]";
            try {
                arrJobj = new org.json.JSONArray(szAvailJson);
                String availValue;//
                for (int p = 0; p < arrJobj.length(); p++) {
                    temp = arrJobj.getJSONObject(p);
                    tempKey = temp.getString("ServiceName");
                    System.out.println("rekha  ServiceName==>>" + tempKey);
                    //if (!(tempKey.equals("null"))){
                    if (serviceMetricInfo.get(tempKey) == null || temp.getString("Health").equals("CRITICAL")) {
                        availValue = "\"ServiceName\":\"" + temp.getString("ServiceName") + "\",\"Health\":\""
                                + temp.getString("Health") + "\",\"ResourceChartType\":" + chartType + ",\"ResourceNames\":"
                                + resourceId;
                        if (serviceMetricInfo.containsKey(tempKey)) {
                            serviceMetricInfo.remove(tempKey);
                        }
                        serviceMetricInfo.put(tempKey, availValue);
                    }
                    //}
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                System.out.println("in generateServicesLeftGridJson(), deriving health based on Performance Ratio");
                String service;
                String PRHealthJSON;
                JSONParser jsonParser = new JSONParser();
                JSONObject prObject;
                JSONArray prJSONArray;
                Double prVal;
                String availValue = null;
                boolean isCritical = false;
                PRHealthJSON = PerformanceRatioGenerator.getJSONFromAggregatedMetrics("", cCustomer, null, "Inverter", null, null, smilli, emilli,
                        timestampselection, customer);
                prJSONArray = (JSONArray) jsonParser.parse(PRHealthJSON);
                for (int i = 0; i < prJSONArray.size(); i++) {
                    prObject = (JSONObject) prJSONArray.get(i);
                    service = prObject.get("ServiceName").toString();
                    availValue = "\"ServiceName\":\"" + service + "\",\"Health\":\""
                            + "Default" + "\",\"ResourceChartType\":" + chartType + ",\"ResourceNames\":"
                            + resourceId;
                    if (prObject.get("PerformanceRatio") == null) {
                    } else if (prObject.get("PerformanceRatio").toString().equals("")) {
                    } else {
                        prVal = Double.parseDouble(prObject.get("PerformanceRatio").toString());
                        if (prVal < 0.9) {
                            availValue = "\"ServiceName\":\"" + service + "\",\"Health\":\""
                                    + "CRITICAL" + "\",\"ResourceChartType\":" + chartType + ",\"ResourceNames\":"
                                    + resourceId;
                            isCritical = true;
                        } else {
                            availValue = "\"ServiceName\":\"" + service + "\",\"Health\":\""
                                    + "OK" + "\",\"ResourceChartType\":" + chartType + ",\"ResourceNames\":"
                                    + resourceId;
                            isCritical = false;
                        }
                    }
                    if ((!serviceMetricInfo.containsKey(service)) || (isCritical)) {
                        if (serviceMetricInfo.containsKey(service)) {
                            serviceMetricInfo.remove(service);
                        }
                        System.out.println("Map value for service " + service + "=" + availValue);
                        serviceMetricInfo.put(service, availValue);
                    }

                }
                jsonParser = null;
                prObject = null;
                prJSONArray = null;
                availValue = null;
            } catch (Exception e) {
                log.error("Error in generateServicesLeftGridJson() while deriving health based on Performance Ratio");
                e.printStackTrace();
            }


            String szQueryTicket = "select service as ServiceName,case when sum(summary)>0 then 'CRITICAL' when sum(summary)=0 "
                    + "then 'OK' else 'Default' end as Alert from  (select attributes4 as service,CASE WHEN substring"
                    + "(task_summary from position('[' in task_summary) for 25) like '%HSTDWN%' THEN 1 ELSE 0 END as summary "
                    + "from gatask where created_date between '" + date + "' and '" + date1 + "' and status!='Closed' and "
                    + "task_type!='SOX' and attributes4!='null' and customerid='" + cCustomer + "' group by summary,attributes4,attributes10) as dTicket "
                    + "group by ServiceName";
//            Connection dataBaseConnectionTicket = ConnectionDAO.getConnection(customer + "");
            String[] headingNamesTicket = {"ServiceName", "Alert"};
            ticketInfo = generateJsonFromGivenQuery(szQueryTicket, headingNamesTicket, customer);
            System.out.println("Default Service Metric Query /" + timestampselection + "/ \n" + szQueryTicket);

            szServiceJson = bizlogic.generateDefaultServiceResourcetypeJson(serviceMetricInfo, ticketInfo);
            addedJson = ",{\"ResourceNames\":[\"Default\"],\"ServiceName\":\"?\",\"Alert\":\"Default\",\"ResourceChartType\":[\"0\",\"1\"],\"MetricUOM\":[\"\"],\"Health\":\"Default\"}]";
            cquery = "select distinct service from hostinfo where customerid=(select id from customerinfo where customername='" + cCustomer + "'" + ")";
            rs = ConnectionDAO.executerQuery(cquery, customer);

            while (rs.next()) {
                System.out.println("service =" + rs.getString(1));
                if (szServiceJson.contains(rs.getString(1))) {
                    System.out.println("present =" + rs.getString(1));
                } else {
                    System.out.println("not present =" + rs.getString(1));
                    szServiceJson = szServiceJson.substring(0, szServiceJson.length() - 1) + addedJson.replace("?", rs.getString(1));
                    System.out.println(szServiceJson);
                }
            }

//            dataBaseConnectionMetric = null;
//            dataBaseConnectionTicket = null;
            ticketInfo = null;
            serviceMetricInfo = null;
            formatter1 = null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("DashBoard Generate Default Service \n" + e.getMessage());
        } finally {
            try {
                if (rs != null) {

                    rs.close();
                    rs = null;
                } else if (cquery != null) {
                    cquery = null;
                } else if (addedJson != null) {
                    addedJson = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return szServiceJson;
    }

    public String generateCustomersLeftGridJson(String startDate, String endDate, String timestampselection,
            String customer) throws IOException, SQLException {
        String szServiceJson = "";
        ResultSet rs = null;
        String addedJson = "";
        String cquery = "";
        try {
            serviceMetricInfo = new LinkedHashMap<String, String>();
            ticketInfo = new LinkedHashMap<String, String>();
            SimpleDateFormat formatter1 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            Date dateStart = formatter1.parse(startDate);
            Date dateEnd = formatter1.parse(endDate);
            String date = formatter1.format(dateStart);
            String date1 = formatter1.format(dateEnd);
            long smilli = dateStart.getTime() / 1000;
            long emilli = dateEnd.getTime() / 1000;
            String szQueryMetric = "select dCustomer.customername as Customer, case when sum(health)>0 then 'CRITICAL' when sum(health)=0 "
                    + "then 'OK' else 'Default' end as  Health from (select customername from customerinfo where id "
                    + "in(select distinct customerid from servicemetrics where resourcetype in "
                    + "(" + "\'" + DBUtilHelper.getMetrics_mapping_properties().getProperty("ResourceName").replaceAll(",", "\',\'")
                    + "\'" + ") and customerid is not null)) as dCustomer left outer join (select customername,CASE WHEN (metrictype in ("
                    + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger") + ") and "
                    + "avg(metricvalue) < avg(sla)) or (metrictype not in ("
                    + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger") + ") and "
                    + "avg(metricvalue) > avg(sla)) THEN 1 WHEN (metrictype not in ("
                    + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger") + ") and "
                    + "avg(metricvalue) < avg(sla)) or (metrictype in ("
                    + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger") + ") and "
                    + "avg(metricvalue) > avg(sla)) THEN 0 END as health from customerinfo,servicemetrics where customerid is not null and "
                    + "timestamp1 between " + smilli + " and " + emilli + "  and metricvalue is not null and metrictype not in ("
                    + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeNotUsedInChart") + ") and customerid=id group by "
                    + "customername,service,resourcetype,resourceid,metrictype) as hCustomer using(customername) group by Customer order by health";
//            Connection dataBaseConnectionMetric = ConnectionDAO.getConnection(customer);
            String[] headingNamesMetric = {"Customer", "Health"};
            serviceMetricInfo = generateJsonFromGivenQuery(szQueryMetric, headingNamesMetric, customer);
            serviceMetricInfo = modifyHashMap(serviceMetricInfo);
            System.out.println("Default Service Metric Query /" + timestampselection + "/ \n" + szQueryMetric);
            System.out.println("Query for customers left grid==>>" + szQueryMetric);

            String resTypes = DBUtilHelper.getMetrics_mapping_properties().getProperty("ResourceName");
            resTypes = "'" + resTypes.replaceAll(",", "','") + "'";
            System.out.println("resTypes==>>" + resTypes);
            //String szAvailJson = GetAvailability.getAvailabilityByPeriod(emilli, smilli, 
            //"'Desktop','server','DataBase','Network','JVM'", customer, timestampselection);
            //String szAvailJson = GetAvailability.getAvailabilityByPeriod(emilli, smilli, resTypes, null, null, customer, timestampselection);
            String szAvailJson = GetAvailability.getAvailabilityByPeriod(emilli, smilli, resTypes, customer, timestampselection, null,
                    null);
            System.out.println("szAvailJson==" + szAvailJson);
            org.json.JSONArray arrJobj = null;
            org.json.JSONObject temp = null;
            String tempKey = "";
            String chartType = "[\"0\",\"1\"]";
            String resourceId = "[\"Default\"]";
            try {
                arrJobj = new org.json.JSONArray(szAvailJson);
                String availValue;//
                for (int p = 0; p < arrJobj.length(); p++) {
                    temp = arrJobj.getJSONObject(p);
                    tempKey = temp.getString("Customer");
                    System.out.println("rekha  Customer==>>" + tempKey);
                    //if (!(tempKey.equals("null"))){
                    if (serviceMetricInfo.get(tempKey) == null || temp.getString("Health").equals("CRITICAL")) {
                        availValue = "\"Customer\":\"" + temp.getString("Customer") + "\",\"Health\":\""
                                + temp.getString("Health") + "\",\"ResourceChartType\":" + chartType + ",\"ResourceNames\":"
                                + resourceId;
                        if (serviceMetricInfo.containsKey(tempKey)) {
                            serviceMetricInfo.remove(tempKey);
                        }
                        serviceMetricInfo.put(tempKey, availValue);
                        System.out.println("tempKey==" + tempKey);
                        System.out.println("availValue==" + availValue);
                    }
                    // }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                System.out.println("in generateCustomersLeftGridJson(), deriving health based on Performance Ratio");
                String cCustomer;
                String PRHealthJSON;
                cquery = "select distinct customername from customerinfo;";
                rs = ConnectionDAO.executerQuery(cquery, customer);
                JSONParser jsonParser = new JSONParser();
                JSONObject prObject;
                JSONArray prJSONArray;
                Double prVal;
                String availValue = null;
                boolean isCritical = false;
                while (rs.next()) {
                    cCustomer = rs.getString(1);
                    PRHealthJSON = PerformanceRatioGenerator.getJSONFromAggregatedMetrics("", cCustomer, null, "Inverter", null, null, smilli, emilli,
                            timestampselection, customer);
                    prJSONArray = (JSONArray) jsonParser.parse(PRHealthJSON);
                    availValue = "\"Customer\":\"" + cCustomer + "\",\"Health\":\""
                            + "Default" + "\",\"ResourceChartType\":" + chartType + ",\"ResourceNames\":"
                            + resourceId;
                    for (int i = 0; i < prJSONArray.size(); i++) {
                        prObject = (JSONObject) prJSONArray.get(i);
                        if (prObject.get("PerformanceRatio") == null) {
                        } else if (prObject.get("PerformanceRatio").toString().equals("")) {
                        } else {
                            prVal = Double.parseDouble(prObject.get("PerformanceRatio").toString());
                            if (prVal < 0.9) {
                                availValue = "\"Customer\":\"" + cCustomer + "\",\"Health\":\""
                                        + "CRITICAL" + "\",\"ResourceChartType\":" + chartType + ",\"ResourceNames\":"
                                        + resourceId;
                                isCritical = true;
                                break;
                            } else {
                                availValue = "\"Customer\":\"" + cCustomer + "\",\"Health\":\""
                                        + "OK" + "\",\"ResourceChartType\":" + chartType + ",\"ResourceNames\":"
                                        + resourceId;
                                isCritical = false;
                            }
                        }
                    }
                    if ((!serviceMetricInfo.containsKey(cCustomer)) || (isCritical)) {
                        if (serviceMetricInfo.containsKey(cCustomer)) {
                            serviceMetricInfo.remove(cCustomer);
                        }
                        System.out.println("Map value for customer " + cCustomer + "=" + availValue);
                        serviceMetricInfo.put(cCustomer, availValue);
                        System.out.println("cCustomer==" + cCustomer);
                        System.out.println("availValue==" + availValue);
                    }
                }
                rs.close();
                rs = null;
                jsonParser = null;
                prObject = null;
                prJSONArray = null;
                availValue = null;
            } catch (Exception e) {
                log.error("Error in generateCustomersLeftGridJson() while deriving health based on Performance Ratio");
                e.printStackTrace();
            }


            String szQueryTicket = "select customerid as Customer,case when sum(summary)>0 then 'CRITICAL' when sum(summary)=0 "
                    + "then 'OK' else 'Default' end as Alert from  (select customerid,CASE WHEN substring"
                    + "(task_summary from position('[' in task_summary) for 25) like '%HSTDWN%' THEN 1 ELSE 0 END as summary "
                    + "from gatask where created_date between '" + date + "' and '" + date1 + "' and status!='Closed' and "
                    + "task_type!='SOX' and attributes4!='null' and customerid!='null' group by customerid,summary,attributes10) as dTicket "
                    + "group by Customer";
//            Connection dataBaseConnectionTicket = ConnectionDAO.getConnection(customer + "");
            String[] headingNamesTicket = {"Customer", "Alert"};
            ticketInfo = generateJsonFromGivenQuery(szQueryTicket, headingNamesTicket, customer);
            System.out.println("Default Customer Metric Query /" + timestampselection + "/ \n" + szQueryTicket);

            szServiceJson = bizlogic.generateDefaultServiceResourcetypeJson(serviceMetricInfo, ticketInfo);

            addedJson = ",{\"Customer\":\"?\",\"ResourceNames\":[\"Default\"],\"Alert\":\"Default\",\"ResourceChartType\":[\"0\",\"1\"],\"MetricUOM\":[\"\"],\"Health\":\"Default\"}]";

            rs = ConnectionDAO.executerQuery(cquery, customer);
            while (rs.next()) {
                System.out.println("service =" + rs.getString(1));
                if (szServiceJson.contains(rs.getString(1))) {
                    System.out.println("present =" + rs.getString(1));
                } else {
                    System.out.println("not present =" + rs.getString(1));
                    szServiceJson = szServiceJson.substring(0, szServiceJson.length() - 1) + addedJson.replace("?", rs.getString(1));
                }
            }

//            dataBaseConnectionMetric = null;
//            dataBaseConnectionTicket = null;
            ticketInfo = null;
            serviceMetricInfo = null;
            formatter1 = null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("DashBoard Generate Default Service \n" + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                } else if (cquery != null) {
                    cquery = null;
                } else if (addedJson != null) {
                    addedJson = null;
                }
            } catch (Exception e) {
                e.printStackTrace();

            }

        }
        return szServiceJson;
    }

    /**
     * ***************************************************************************************************
     * This method is giving default Service ,ResourceType with Health and Alert
     * status for Specified period and Generating JSON with this method
     * generateDefaultServiceResourcetypeJson()
     *
     * @param resourceType Different category as
     * Desktop,Server,DataBase,Network,JVM
     * @param startDate sub(endDate-timestampselection) in Date Format
     * @param endDate Current date in Date Format
     * @param timestampselection Selected Time Period
     * @param customer Selected customerID
     * @return szResourceTypeJson Json having service,Resourcetype,health and
     * Alert
     *
     * ***************************************************************************************************
     */
    public String generateDefaultResourceTypeJson(String resourceType, String cCustomer, String service,
            String startDate, String endDate, String timestampselection,
            String customer) throws IOException, SQLException {
        String szResourceTypeJson = "";
        String resTypesQuery;
        ResultSet rs = null;
        String addedJson;
        try {
            ticketInfo = new LinkedHashMap<String, String>();
            serviceMetricInfo = new LinkedHashMap<String, String>();
            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateStart = formatter1.parse(startDate);
            Date dateEnd = formatter1.parse(endDate);
            String date = formatter1.format(dateStart);
            String date1 = formatter1.format(dateEnd);
            long smilli = dateStart.getTime() / 1000;
            long emilli = dateEnd.getTime() / 1000;
            //            Connection dataBaseConnectionMetric = ConnectionDAO.getConnection(customer);
            //            Connection dataBaseConnectionTicket = ConnectionDAO.getConnection(customer + "");
            ticketInfo.clear();
            serviceMetricInfo.clear();
            String szQueryMetric = "select dService.service as ServiceName, resourcetype as ResourceType ,case when sum(health)>0 "
                    + "then 'CRITICAL' when sum(health)=0 then 'OK' else 'Default' end as  Health from "
                    + "(select distinct service from servicemetrics where resourcetype in (" + "\'"
                    + DBUtilHelper.getMetrics_mapping_properties().getProperty("ResourceName").replaceAll(",", "\',\'") + "\'"
                    + ") and service='" + service + "' and service!=resourcetype) as dService left outer join (select service,resourcetype,CASE WHEN (metrictype in ("
                    + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger") + ") and "
                    + "avg(metricvalue) < avg(sla)) or (metrictype not in (" + DBUtilHelper.getMetrics_mapping_properties()
                    .getProperty("MetricTypeBelowThresholdIsDanger") + ") and avg(metricvalue) > avg(sla)) THEN 1 WHEN "
                    + "(metrictype not in (" + DBUtilHelper.getMetrics_mapping_properties().
                    getProperty("MetricTypeBelowThresholdIsDanger") + ") and avg(metricvalue) < avg(sla)) or (metrictype in ("
                    + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger") + ") and "
                    + "avg(metricvalue) > avg(sla)) THEN 0 END as health from servicemetrics where service='" + service
                    + "' and service!=resourcetype and timestamp1  between " + smilli + " and " + emilli + "  and metricvalue is not null and metrictype not in ("
                    /*+ "' and service!=resourcetype and resourcetype in (" + "\'" + DBUtilHelper.getMetrics_mapping_properties().getProperty("ResourceName").replaceAll(",", "\',\'") + "\'"
                     + ") and timestamp1  between " + smilli + " and " + emilli + "  and metricvalue is not null and metrictype not in ("*/
                    + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeNotUsedInChart")
                    + ") and customerid=(select id from customerinfo where customername='" + cCustomer + "') and "
                    + "service='" + service + "' and service!=resourcetype "
                    + "group by service,resourcetype,metrictype) as hService using(service) group by ServiceName,ResourceType";
            String[] headingNamesMetric1 = {"ServiceName", "ResourceType", "Health"};
            System.out.println("Default ResourceType Metric Query /" + timestampselection + "/ \n" + szQueryMetric);
            serviceMetricInfo = generateJsonFromGivenQuery(szQueryMetric, headingNamesMetric1, customer);
            serviceMetricInfo = modifyHashMap(serviceMetricInfo);
            System.out.println("serviceMetricInfo====>"+serviceMetricInfo);
            String resTypes = DBUtilHelper.getMetrics_mapping_properties().getProperty("ResourceName");
            resTypes = "'" + resTypes.replaceAll(",", "','") + "'";
            //String szAvailJson = GetAvailability.getAvailabilityByPeriod(emilli, smilli, 
            //"'Desktop','server','DataBase','Network','JVM'", customer, timestampselection);
            System.out.println("resTypes==>>" + resTypes);
            //String szAvailJson = GetAvailability.getAvailabilityByPeriod(emilli, smilli, resTypes, cCustomer, service, customer, timestampselection);
            String szAvailJson = GetAvailability.getAvailabilityByPeriod(emilli, smilli, resTypes, customer, timestampselection, cCustomer,
                    service);
            org.json.JSONArray arrJobj = null;
            org.json.JSONObject temp = null;
            String tempKey = "";
            try {
                arrJobj = new org.json.JSONArray(szAvailJson);
                String availValue;//
                for (int p = 0; p < arrJobj.length(); p++) {
                    temp = arrJobj.getJSONObject(p);
                    tempKey = temp.getString("ServiceName") + "," + temp.getString("ResourceType");
                    //tempKey = temp.getString("ServiceName") + "," +"null";
                    
                    System.out.println("temp===>"+temp);
                    System.out.println("tempkey====>"+tempKey);
                    
                   /* if (serviceMetricInfo.get(tempKey) == null || temp.getString("Health").equals("CRITICAL")) {
                        System.out.println("inside if loop -------------------------------------");
                        System.out.println("serviceMetricInfo====>"+serviceMetricInfo);
                        
                        System.out.println("--------------------------------------");
            
                        
                        serviceMetricInfo.remove(temp.getString("ServiceName") + ",null");
                        System.out.println("serviceMetricInfo====>"+serviceMetricInfo);
                        
                        System.out.println("--------------------------------------");
                        
                        availValue = "\"ServiceName\":\"" + temp.getString("ServiceName") + "\",\"ResourceType\":\"" + temp.getString("ResourceType") + "\",\"Health\":\"" + temp.getString("Health") + "\"";
                        serviceMetricInfo.put(tempKey, availValue);
                        
                         System.out.println("serviceMetricInfo====>"+serviceMetricInfo);
                        
                        System.out.println("--------------------------------------"); 
                        
                    }*/
                     System.out.println("serviceMetricInfo====>"+serviceMetricInfo);
                    if (serviceMetricInfo.get(tempKey) == null){
                        serviceMetricInfo.remove(temp.getString("ServiceName") + ",null");
                        availValue = "\"ServiceName\":\"" + temp.getString("ServiceName") + "\",\"ResourceType\":\"" + temp.getString("ResourceType") + "\",\"Health\":\"" + "Default" + "\"";
                        serviceMetricInfo.put(tempKey, availValue);
                        }
                    
                    if( temp.getString("Health").equals("CRITICAL")) {
                        serviceMetricInfo.remove(tempKey);
                        availValue = "\"ServiceName\":\"" + temp.getString("ServiceName") + "\",\"ResourceType\":\"" + temp.getString("ResourceType") + "\",\"Health\":\"" + temp.getString("Health") + "\"";
                        serviceMetricInfo.put(tempKey, availValue);
                        
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            String szQueryTicket = "select service as ServiceName,ResourceType,case when sum(summary)>0 then 'CRITICAL' when "
                    + "sum(summary)=0 then 'OK' else 'Default' end as Alert from  (select attributes4 as service,attributes10 "
                    + "as ResourceType,CASE WHEN substring(task_summary from position('[' in task_summary) for 25) like '%HSTDWN%' "
                    + "THEN 1 ELSE 0 END as summary from gatask where created_date between '" + date + "' and '" + date1
                    + "' and status!='Closed' and task_type!='SOX' and attributes4!='null' and attributes4='" + service + "' group by summary,attributes4,attributes10) "
                    + "as dTicket group by ServiceName,ResourceType";

            String[] headingNamesTicket1 = {"ServiceName", "ResourceType", "Alert"};
            System.out.println("Default ResourceType Ticket Query /" + timestampselection + "/ \n" + szQueryTicket);

            ticketInfo = generateJsonFromGivenQuery(szQueryTicket, headingNamesTicket1, customer);

            szResourceTypeJson = bizlogic.generateDefaultServiceResourcetypeJson(serviceMetricInfo, ticketInfo);
            //            dataBaseConnectionMetric = null;
            //            dataBaseConnectionTicket = null;
            System.out.println("+++++++szResourceTypeJson"+szResourceTypeJson);
            resTypesQuery = "select distinct resourcetype from hostinfo where customerid=(select id from customerinfo where customername='" + cCustomer + "') and "
                    + "service='" + service + "' and service!=resourcetype and resourcetype in (" + "\'"
                    + DBUtilHelper.getMetrics_mapping_properties().getProperty("ResourceName").replaceAll(",", "\',\'") + "\'"
                    + ")";
            rs = ConnectionDAO.executerQuery(resTypesQuery, customer);
            addedJson = ",{\"ResourceNames\":[\"Default\"],\"ResourceType\":\"?\",\"ServiceName\":\"" + service
                    + "\",\"Alert\":\"Default\",\"ResourceChartType\":[\"0\",\"1\"],\"MetricUOM\":[\"\"],\"Health\":\"Default\"}]";
            while (rs.next()) {
                if (!(szResourceTypeJson.contains("\"ResourceType\":\"" + rs.getString("resourcetype") + "\""))) {
                    szResourceTypeJson = szResourceTypeJson.substring(0, szResourceTypeJson.length() - 1) + addedJson.replace("?",
                            rs.getString("resourcetype"));
                    System.out.println("QEszResourceTypeJson>> " + szResourceTypeJson);
                }
            }
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(szResourceTypeJson);
            String jsonEle;
            for (int i = 0; i > jsonArray.size(); i++) {
                jsonEle = jsonArray.get(i).toString();
                if (!(jsonEle.contains("ResourceType"))) {
                    int commaIndex = szResourceTypeJson.indexOf(jsonEle) + jsonEle.length() + 1;
                    if (",".equals(szResourceTypeJson.charAt(commaIndex))) {
                        szResourceTypeJson = szResourceTypeJson.replace(jsonEle + ",", "");
                    } else {
                        szResourceTypeJson = szResourceTypeJson.replace(jsonEle, "");
                    }
                }
            }
            jsonParser = null;
            jsonArray.clear();
            jsonArray = null;
            jsonEle = null;


            addedJson = null;
            rs.close();
            rs = null;
            ticketInfo = null;
            serviceMetricInfo = null;
            formatter1 = null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("DashBoard Generate DefaultResourceType \n" + e.getMessage());
        } finally {
            if (rs != null) {
                rs.close();
            }
            rs = null;
            resTypesQuery = null;
            addedJson = null;
        }
        System.out.println("Returning defaultResourceType.json==>>" + szResourceTypeJson);
        return szResourceTypeJson;
    }

    /**
     * ***************************************************************************************************
     * Here we are Executing both Ticket and Metric Query one by one because
     * both having different dataBase Connection and sending as LinkedHashMap
     *
     * @param szQueryString Query to be Executed
     * @param headingNames Name of Columns in Executed Query
     * @param dataBaseConnection Specific query specific Connection
     * @return szMetricTypemap return LinkedHashMap
     * ***************************************************************************************************
     */
    public LinkedHashMap<String, String> generateJsonFromGivenQuery(String szQueryString, String[] headingNames, String customer) {
        LinkedHashMap<String, String> szMetricTypemap = new LinkedHashMap<String, String>();

        try {
//            Statement stat2 = dataBaseConnection.createStatement();
            String szMetricTypeValueJson = "";
            ResultSet rs4 = ConnectionDAO.executerQuery(szQueryString, customer);//stat2.executeQuery(szQueryString);
            String szConcatColumn;//
            while (rs4.next()) {
                System.out.println("rs4.next====> while");
                szConcatColumn = "";
                szMetricTypeValueJson = "";
                for (int i = 0; i < headingNames.length - 1; i++) {
                    System.out.println("headingNames[i]==>>" + headingNames[i]);
                    if (!headingNames[headingNames.length - 1].equals("Alert")) {
                        System.out.println("rs4.getString(headingNames[i])==>>" + rs4.getString(headingNames[i]));
                        if (rs4.getString(headingNames[i]) != null) {
                            szConcatColumn = szConcatColumn + ",\"" + headingNames[i] + "\":\"" + rs4.getString(headingNames[i]) + "\"";
                        }
                    }
                    szMetricTypeValueJson = szMetricTypeValueJson + "," + rs4.getString(headingNames[i]);
                }
                szConcatColumn = szConcatColumn + ",\"" + headingNames[headingNames.length - 1] + "\":\"" + rs4.getString(headingNames[headingNames.length - 1]) + "\"";
                szMetricTypemap.put(szMetricTypeValueJson.substring(1), szConcatColumn.substring(1));
                System.out.println("");
                
            }
            ConnectionDAO.closeStatement();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in generateJsonFromGivenQuery" + e.getMessage());
        }
        return szMetricTypemap;
    }

    public LinkedHashMap<String, String> collectTicketdata1(String customer, String resourceType, String cCustomer, String service, String date, String date1) throws IOException, SQLException {
        ticketInfo = new LinkedHashMap<String, String>();
        try {
//            uniqueconnection = ConnectionDAO.getConnection(customer + "");
//            stat = uniqueconnection.createStatement();

            String szQuery = "select host,resourcetype,ServiceName,resourceID,case when sum(criticalcheck)>0 "
                    + "then 'CRITICAL' else 'OK' end as health from (select attributes6 as host,attributes10 "
                    + "as resourcetype,attributes4 as ServiceName,attributes12 as resourceID,CASE WHEN "
                    + "substring(task_summary from position('[' in task_summary) for 25) like '%HSTDWN%' THEN 1"
                    + " ELSE 0 END as criticalcheck from gatask where created_date between  '" + date
                    + "' and '" + date1 + "' and status!='Closed' and task_type!='SOX' and attributes10='"
                    + resourceType + "' and attributes4='" + service + "' group by attributes4,attributes12,attributes6,attributes10,"
                    + "criticalcheck) as alert group by host,resourcetype,ServiceName,resourceID";
            /**
             * ***************************************************************************************************
             * This Query will give Modified result from all necessary tables
             * like gatasktypeassignee, gatask table .. here concatenating the
             * result came from GAtask for Summary Count and checking hostdown
             * status array_agg function in postgresql below 8 version we have
             * to create by following command: create aggregate array_agg (
             * sfunc = array_append, basetype = anyelement, stype = anyarray,
             * initcond = '{}' );
             * ***************************************************************************************************
             */
            System.out.println("Ticket Query /" + resourceType + "/\n" + szQuery);

            ResultSet rs = ConnectionDAO.executerQuery(szQuery, customer);//stat.executeQuery(szQuery);
            String serviceName;
            String host;
            String resourceID;
            String health;
            while (rs.next()) {
                serviceName = rs.getString("ServiceName");
                resourceType = rs.getString("resourcetype");
                host = rs.getString("host");
                resourceID = rs.getString("resourceID");
                health = rs.getString("health");


                /**
                 * Generating Map for Chart Generation
                 */
                ticketInfo.put(serviceName + "," + host + "," + resourceID, "\"hstdwnstatus\":[\"" + health + "\"]");
            }
            ConnectionDAO.closeStatement();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("DashBoard Ticket Query /" + resourceType + "/\n" + e.getMessage());
        } finally {
        }
        return ticketInfo;
    }

    /**
     * ************************************************************************************************
     * This method is giving All metricType,Avg(Metricvalues) related to each
     * metricType, SLAValues related to each
     * metricType,services,ResourceType,ResourceId,Servername
     *
     * @param customer Selected customerID
     * @param resourceType Different category as
     * Desktop,Server,DataBase,Network,JVM
     * @param smilli date in second as long Timestamp(Current Time-selected Time
     * Period)
     * @param emilli date in second as long Timestamp(Current Time)
     * @return serviceMetricInfo Metric information as string Json
     *
     * *************************************************************************************************
     */
    public LinkedHashMap<String, String> collectMetricdata1(String customer, String resourceType, String cCustomer, String service,
            long smilli, long emilli) throws IOException, SQLException {
        try {
            serviceMetricInfo = new LinkedHashMap<String, String>();

            /**
             * *************************************************************************************************
             * Here we are checking the condition of showing critical according
             * to avg (means avg of actual value compare with avg of actual sla)
             * or Count (means actual value compare with actual sla)
             * *************************************************************************************************
             *
             */
            String metricHealthCheckLogic = "case when sum(criticalflag)>0 then 'CRITICAL' else 'OK' end";
            if (DBUtilHelper.getMetrics_mapping_properties().getProperty("HealthCheck").equals("AVG")) {
                metricHealthCheckLogic = "case when (avg(metricvalue)>avg(sla) and metrictype not in ("
                        + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")
                        + ")) or (avg(metricvalue)<avg(sla) and metrictype in ("
                        + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger")
                        + ")) then  'CRITICAL' else 'OK' end";
            }

            String szQuery = "select host,service,resourcetype,resourceid,array_to_string(array_agg(metrictype),',') as metricname,array_to_string(array_agg(criticalsum),',') as metrictypehealth,case when resourceSubType is null then resourcetype else resourceSubType end as resourceSubType from "
                    + "( select def.resourcesubtype as resourceSubType,def.host,def.service as service,def.resourcetype,def.resourceid as resourceid,case when metrictype is null then 'default' else metrictype end as metrictype,case when criticalsum is null then 'GREY' else criticalsum end as criticalsum from "
                    + "( select host,resourcetype,resourceid,case when metrictype in (" + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeCountAsValue") + ") then count(metrictype) else   round(cast(avg(metricvalue) as numeric),2) end as metricvalue,metrictype,round(cast(avg(sla) as numeric),2) as sla, " + metricHealthCheckLogic + " as criticalsum from "
                    + "( select host,resourcetype,resourceid, metricvalue,metrictype,sla,case when metricvalue>sla and metrictype not in (" + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger") + ") or metricvalue<sla and metrictype  in (" + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger") + ") then 1 else 0  end as criticalflag from "
                    + "  servicemetrics "
                    + "where timestamp1 between " + smilli + " and " + emilli + " and  "
                    + "resourcetype='" + resourceType + "' and "
                    + "customerid=(select id from customerinfo where customername='" + cCustomer + "') and "
                    + "service='" + service + "' and "
                    + "metricvalue is not null and "
                    + "metrictype not in ('downtime'," + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeNotUsedInChart") + ")) as f "
                    + "group by host,resourcetype,resourceid,metrictype "
                    + "order by criticalsum) as f1 "
                    + "right outer join "
                    + "(select distinct host,resourceid,service,resourcetype,resourcesubtype from "
                    + "hostinfo "
                    + "where resourcetype='" + resourceType + "' and "
                    + "customerid=(select id from customerinfo where customername='" + cCustomer + "') and "
                    + "service='" + service + "' and "
                    + "service <>'null') as def using(host,resourceid)) as s1 "
                    + "group by host,service,resourcetype,resourceid,resourcesubtype order by metrictypehealth";


            /*if (resourceType.equals("Desktop") || resourceType.equals("server")) {
             szQuery = "select host,service,resourcetype,resourceid,array_to_string(array_agg(metrictype),',') as metricname,array_to_string(array_agg(criticalsum),',') as metrictypehealth from "
             + "( select host,resourcetype, hr.service,hr.resourceid as resourceid,case when metrictype is null then 'default' else metrictype end as metrictype,case when criticalsum is null then 'GREY' else criticalsum end as criticalsum from "
             + "( select host,resourceid,case when metrictype in (" + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeCountAsValue") + ") then count(metrictype) else   round(cast(avg(metricvalue) as numeric),2) end as metricvalue,metrictype,round(cast(avg(sla) as numeric),2) as sla, " + metricHealthCheckLogic + " as criticalsum from "
             + "( select host,resourceid, metricvalue,metrictype,sla,case when metricvalue>sla and metrictype not in (" + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger") + ") or metricvalue<sla and metrictype in (" + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetricTypeBelowThresholdIsDanger") + ") then 1 else 0  end as criticalflag from "
             + "servicemetrics "
             + "where timestamp1 between " + smilli + " and " + emilli + " and  "
             + "resourcetype='" + resourceType + "' and "
             + "customerid=(select id from customerinfo where customername='" + cCustomer + "') and "
             + "service='" + service + "' and "
             + "metricvalue is not null and "
             + "metrictype not in ('downtime'," + DBUtilHelper.getMetrics_mapping_properties().getProperty("MetrictypeNotUsedInChart") + ")) as f "
             + "group by host,resourceid,metrictype ) as al "
             + "right outer join "
             + "(select distinct resourceid,host,service,resourcetype from "
             + "hostinfo "
             + "where resourcetype='" + resourceType + "' and "
             + "customerid=(select id from customerinfo where customername='" + cCustomer + "') and "
             + "service='" + service + "' ) as hr "
             + "using(host,resourceid) "
             + "order by criticalsum) as fullservicetable "
             + "group by host,service,resourcetype,resourceid order by metrictypehealth";
             }*/
//            uniqueconnection = ConnectionDAO.getConnection(customer);
//            stat = uniqueconnection.createStatement();

            /**
             * ***************************************************************************************************
             * Here Query is Separated according to resource type because server
             * and Desktop taking more time because of more data , We can use
             * the same query by removing if condition array_agg function in
             * postgresql below 8 version we have to create by following
             * command: create aggregate array_agg ( sfunc = array_append,
             * basetype = anyelement, stype = anyarray, initcond = '{}' );
             * ***************************************************************************************************
             *
             */
            System.out.println("Report Metric Query /" + resourceType + "/ \n" + szQuery);
            ResultSet rs = ConnectionDAO.executerQuery(szQuery, customer);//stat.executeQuery(szQuery);
            String metricname;//
            String chartType;//
            String metrictypehealth;//
            String host;//
            String serviceName;//
            String resourcetype;//
            String resourceId;//
            String resourceSubType;//
            String metricnameTemp;//
            while (rs.next()) {
                metricname = "[]";
                chartType = "[\"0\",\"1\"]";
                metrictypehealth = "[]";

                host = rs.getString("host");
                if (rs.getString("metrictypehealth") != null) {
                    metrictypehealth = "[\"" + rs.getString("metrictypehealth").replace(",", "\",\"") + "\"]";
                }
                if (rs.getString("metricname") != null) {
                    metricnameTemp = "\"" + rs.getString("metricname").replace(",", "\",\"") + "\"";
                    metricname = bizlogic.ChangeMetricNameAsPropertyname(metricnameTemp);
                    if (!metricnameTemp.equals("Default")) {
                        chartType = bizlogic.getChartType(resourceType, metricnameTemp, metrictypehealth);
                    }
                }

                serviceName = rs.getString("service");
                resourcetype = rs.getString("resourcetype");
                resourceId = rs.getString("resourceId");
                resourceSubType = "resourceSubType";
                /*if (resourceType.equals("Desktop") || resourceType.equals("server")) {
                 } else {*/
                resourceSubType = rs.getString("resourceSubType");
                //}


                if (serviceMetricInfo.containsKey(serviceName + "," + host + "," + resourceId)) {
                    serviceMetricInfo.remove(serviceName + "," + host + "," + resourceId);
                }
                if (!host.equals("null")) {
                    serviceMetricInfo.put(serviceName + "," + host + "," + resourceId, "\"ServiceName\":\"" + serviceName
                            + "\",\"ResourceType\":\"" + resourceType + "\",\"ServerName\":\"" + host + "\",\"ResourceNames\":"
                            + metricname + ",\"Health\":" + metrictypehealth + ",\"ResourceSubType\":\"" + resourceSubType
                            + "\",\"ResourceChartType\":" + chartType + ",\"ResourceID\":\"" + resourceId + "\"");
                }

            }
            System.out.println("QueryExecutor.collectMetricdata1()    serviceMetricInfo = " + serviceMetricInfo);

            /**
             * ***************************************************************************************************
             * Here we are setting All Host list Available in that Period
             * According to this we are showing Availability
             * ***************************************************************************************************
             */
            ConnectionDAO.closeStatement();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("DashBoard Report Metric Query /" + resourceType + "/ \n" + e.getMessage());
        }

        return serviceMetricInfo;
    }

    public String getWatchDogAlertJson(String customer, String cCustomer) {
        String watchDogQuery = "select host,metrictype,"
                + "to_char(to_timestamp(max(timestamp1)) AT TIME ZONE 'Asia/Calcutta' , 'yyyy/MM/dd HH24:mi:SS') as TimeStamps,"
                + "resourcetype,resourceid from servicemetrics where metrictype in ("
                + DBUtilHelper.getMetrics_mapping_properties().getProperty("WatchDogMetricList")
                + ") and customerid=(select id from customerinfo where customername='" + cCustomer + "') "
                + "group by metrictype,resourcetype,host,resourceid";
        System.out.println("WatchDog Query:====" + watchDogQuery);
        String watchdogJson = "[";
        try {
            Connection uniqueconnection = ConnectionDAO.getConnection(customer);
            Statement stat2 = uniqueconnection.createStatement();

            String szMetricTypeValueJson = "";
            ResultSet rs4 = stat2.executeQuery(watchDogQuery);
            if (rs4 == null) {
                System.out.println("822 rs4 null;");
            } else {
                System.out.println("822 rs4 is not null;");
            }
            String resourceType = "";
            String metrictype = "";
            String timestamp = "";
            String serverName = "";
            String resourceID = "";
            while (rs4.next()) {
                serverName = rs4.getString("host");
                resourceType = rs4.getString("resourcetype");
                metrictype = rs4.getString("metrictype");
                timestamp = rs4.getString("TimeStamps");
                resourceID = rs4.getString("resourceid");

                szMetricTypeValueJson = szMetricTypeValueJson + ",{\"ServerName\":\"" + serverName + "\",\"ResourceType\":\"" + resourceType
                        + "\",\"MetricType\":\"" + metrictype + "\",\"ResourceID\":\"" + resourceID + "\",\"Timestamps\":\"" + timestamp + "\"}";
            }
            if (szMetricTypeValueJson.equals("")) {
                watchdogJson = "[]";
            } else {
                watchdogJson = watchdogJson + szMetricTypeValueJson.substring(1) + "]";
            }
            try {
                rs4.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                stat2.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            rs4 = null;
            stat2 = null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("DashBoard Generate Default Service and ResourceType \n" + e.getMessage());
        }
        return watchdogJson;
    }

    public String getMetricResourceTypeMapping(String customer, String cCustomer, String service) {
        String MetricResourceTypeMappingJson = "";
        String szQuery = "select distinct metrictype,resourcetype from smetricslathresholds where "
                + "customerid=(select id from customerinfo where customername='" + cCustomer + "' and service='" + service + "') order by resourcetype,metrictype ";

        try {
//            uniqueconnection = ConnectionDAO.getConnection(customer);
//            Statement stat2 = uniqueconnection.createStatement();

            String propertyMetricName1 = "";
            String resourceType1 = "";
            String propertyMetricName2 = "";
            String resourceType2 = "";
            String propertyMetricName3 = "";
            String resourceType3 = "";
            String propertyMetricName4 = "";
            String resourceType4 = "";
            String propertyMetricName5 = "";
            String resourceType5 = "";
            ResultSet rs4 = ConnectionDAO.executerQuery(szQuery, customer);//stat2.executeQuery(szQuery);
            String resourceType;//
            while (rs4.next()) {
                resourceType = rs4.getString("resourcetype");
                if (resourceType.equals("Desktop")) {
                    propertyMetricName1 = propertyMetricName1 + ",\"" + DBUtilHelper.getMetrics_mapping_properties().getProperty(rs4.getString("metrictype")) + "\"";
                    resourceType1 = resourceType;
                } else if (resourceType.equals("server")) {
                    propertyMetricName2 = propertyMetricName2 + ",\"" + DBUtilHelper.getMetrics_mapping_properties().getProperty(rs4.getString("metrictype")) + "\"";
                    resourceType2 = resourceType;
                } else if (resourceType.equals("DataBase")) {
                    propertyMetricName3 = propertyMetricName3 + ",\"" + DBUtilHelper.getMetrics_mapping_properties().getProperty(rs4.getString("metrictype")) + "\"";
                    resourceType3 = resourceType;
                } else if (resourceType.equals("Network")) {
                    propertyMetricName4 = propertyMetricName4 + ",\"" + DBUtilHelper.getMetrics_mapping_properties().getProperty(rs4.getString("metrictype")) + "\"";
                    resourceType4 = resourceType;
                } else if (resourceType.equals("JVM")) {
                    propertyMetricName5 = propertyMetricName5 + ",\"" + DBUtilHelper.getMetrics_mapping_properties().getProperty(rs4.getString("metrictype")) + "\"";
                    resourceType5 = resourceType;
                }

            }
            if (!propertyMetricName1.equals("")) {
                propertyMetricName1 = propertyMetricName1.substring(1);
            }
            if (!propertyMetricName2.equals("")) {
                propertyMetricName2 = propertyMetricName2.substring(1);
            }
            if (!propertyMetricName3.equals("")) {
                propertyMetricName3 = propertyMetricName3.substring(1);
            }
            if (!propertyMetricName4.equals("")) {
                propertyMetricName4 = propertyMetricName4.substring(1);
            }
            if (!propertyMetricName5.equals("")) {
                propertyMetricName5 = propertyMetricName5.substring(1);
            }

            MetricResourceTypeMappingJson = "[{"
                    + "\"ResourceType\":\"" + resourceType1 + "\","
                    + "\"ResourceNames\":[" + propertyMetricName1 + "]"
                    + "},{"
                    + "\"ResourceType\":\"" + resourceType2 + "\","
                    + "\"ResourceNames\":[" + propertyMetricName2 + "]"
                    + "},{"
                    + "\"ResourceType\":\"" + resourceType3 + "\","
                    + "\"ResourceNames\":[" + propertyMetricName3 + "]"
                    + "},{"
                    + "\"ResourceType\":\"" + resourceType4 + "\","
                    + "\"ResourceNames\":[" + propertyMetricName4 + "]"
                    + "},{"
                    + "\"ResourceType\":\"" + resourceType5 + "\","
                    + "\"ResourceNames\":[" + propertyMetricName5 + "]"
                    + "}]";
            ConnectionDAO.closeStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MetricResourceTypeMappingJson;
    }

    public String generatePerformanceJsons(String szResourceType, String cCustomer, String service, String resType, String metricType,
            String configuredMetricType, String jsonGenerator, String startDate, String endDate, String timestampselection,
            String customer) {
        String perfJSON = "";
        try {
            SimpleDateFormat formatter1 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            Date dateStart = formatter1.parse(startDate);
            Date dateEnd = formatter1.parse(endDate);
            long smilli = dateStart.getTime() / 1000;
            long emilli = dateEnd.getTime() / 1000;
            System.out.println("In qe.generatePerformanceJsons==>>smilli:" + smilli + "::emilli:" + emilli);
            Class generatorCls = Class.forName(jsonGenerator);
            Object generatorObj = generatorCls.newInstance();
            Class[] inputArr = new Class[]{String.class, String.class, String.class, String.class, String.class, String.class, Long.TYPE,
                Long.TYPE, String.class, String.class};
            Method generatorMthd = generatorCls.getDeclaredMethod("generateJSON", inputArr);
            perfJSON = (String) generatorMthd.invoke(generatorObj, szResourceType, cCustomer, service, resType, metricType, configuredMetricType,
                    smilli, emilli, timestampselection, customer);
            return perfJSON;
        } catch (Exception e) {
            log.error("Error in generateServicePerformanceJson()==>>" + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList getCCustomers(String customer) {
        String szQuery = "select customername from customerinfo";
        ArrayList cCustomers = new ArrayList();
        ResultSet rs;
        try {
            rs = ConnectionDAO.executerQuery(szQuery, customer);
            while (rs.next()) {
                cCustomers.add(rs.getString(1));
            }
            return cCustomers;
        } catch (Exception e) {
            log.error("Error in getCCustomers==>>" + e.toString());
            e.printStackTrace();
        } finally {
            rs = null;
        }
        return null;
    }

    public static ArrayList getServiceList(String customer, String cCustomer) {
        String szQuery = "select distinct service from hostinfo where customerid=(select id from customerinfo "
                + "where customername='" + cCustomer + "')";
        ArrayList serviceLst = new ArrayList();
        ResultSet rs;
        try {
            rs = ConnectionDAO.executerQuery(szQuery, customer);
            while (rs.next()) {
                serviceLst.add(rs.getString(1));
            }
            return serviceLst;
        } catch (Exception e) {
            log.error("Error in getServiceList==>>" + e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public LinkedHashMap<String, String> collectTicketDataForCustomer(String customer, String cCustomer, String date, String date1,
            String selection) throws IOException, SQLException {
        ticketInfo = new LinkedHashMap<String, String>();
        try {
//            uniqueconnection = ConnectionDAO.getConnection(customer + "");
//            stat = uniqueconnection.createStatement();

            String szQuery = "";

            if (selection.equals("CriticalAlerts")) {
                szQuery = "select array_to_string(array_agg(summaryname),',') as ResourceNames,"
                        + "array_to_string(array_agg(summarycount),',') as ResourceValues,case when sum(criticalcheck)>0 "
                        + "then 'CRITICAL' else 'OK' end as Health from (select count(*) as summarycount,"
                        + "substring(task_summary from position('[' in task_summary) for 25) as summaryname, CASE WHEN "
                        + "substring(task_summary from position('[' in task_summary) for 25) like '%HSTDWN%' "
                        + "THEN 1 ELSE 0 END as criticalcheck from gatask where created_date between  '" + date + "' and '" + date1
                        + "' and status!='Closed' and task_type!='SOX' and customerid='" + cCustomer
                        + "' group by summaryname) as cAlert";
            } else if (selection.equals("AlertsByAssignee")) {
                szQuery = "select array_to_string(array_agg(assigneenames),',') as ResourceNames,"
                        + "array_to_string(array_agg(ticketcount),',') as ResourceValues,case when sum(ticketcount)>0 then 'CRITICAL' else 'OK' "
                        + "end as Health from (select count(*)as ticketcount,g1.assignee as assigneenames "
                        + "from gatask g2 ,gatasktypeassignee g1 where created_date between  '" + date + "' and '" + date1
                        + "' and g1.taskid=g2.task_id and action_assignee=1 and customerid='" + cCustomer
                        + "' group by g1.assignee) as assig";
            } else if (selection.equals("AlertsByStatus")) {
                szQuery = "select array_to_string(array_agg(statusnames),',') as ResourceNames,"
                        + "array_to_string(array_agg(statuscount),',') as ResourceValues,case when sum(statuscount)>0 then 'CRITICAL' else 'OK' "
                        + "end as Health from (select count(*) as statuscount,status as statusnames "
                        + "from gatask where created_date between  '" + date + "' and '" + date1
                        + "' and customerid='" + cCustomer + "' group by status) as sAlert";
            }

            /**
             * ***************************************************************************************************
             * This Query will give Modified result from all necessary tables
             * like gatasktypeassignee, gatask table .. here concatenating the
             * result came from GAtask for Summary Count and checking hostdown
             * status array_agg function in postgresql below 8 version we have
             * to create by following command: create aggregate array_agg (
             * sfunc = array_append, basetype = anyelement, stype = anyarray,
             * initcond = '{}' );
             * ***************************************************************************************************
             */
            System.out.println("Ticket Query /" + cCustomer + "/\n" + szQuery);
            System.out.println("Ticket Query /" + cCustomer + "/\n" + szQuery);

            ResultSet rs = ConnectionDAO.executerQuery(szQuery, customer);//stat.executeQuery(szQuery);
            String alertCount;//
            String alertName;//
            String alertHealth;//
            String nameOfAlert;//
            String sz_query_assignee_list;//
            String assigneeList;//
            String sz_query_status_list;//
            String statusList;//
            String tmpName;//
            String summaryname1;//
            String summarynameconcat;//
            String[] splitsummaryname;//
            while (rs.next()) {
                alertCount = "[]";
                alertName = "";
                alertHealth = "[]";
                nameOfAlert = "";
                //String serviceName = rs.getString("ServiceName");

                /**
                 * ************************************************************************************************
                 * Default Assignee that having no Ticket Assigned for specific
                 * host
                 * ************************************************************************************************
                 *
                 */
                sz_query_assignee_list = "select distinct(user_id) as status from gaoperator where user_id !='Admin' "
                        + "and user_id!='None' and  user_id not in (select g1.assignee from gatask g2 ,"
                        + "gatasktypeassignee g1 where  created_date between '" + date + "' and '" + date1 + "'  and "
                        + "g1.taskid=g2.task_id and action_assignee=1 and customerid='" + cCustomer + "') order by user_id";
                assigneeList = getDefaultAssigneeStatus(sz_query_assignee_list, customer);

                /**
                 * Default Status for specific host
                 */
                sz_query_status_list = " select distinct(status) as status from gastatus where status not in "
                        + "(select status from gatask where created_date between '" + date + "' and '" + date1
                        + "'  and customerid='" + cCustomer + "') order by status";
                statusList = getDefaultAssigneeStatus(sz_query_status_list, customer);

                if (rs.getString("ResourceValues") != null) {
                    alertCount = "[" + rs.getString("ResourceValues") + "]";
                }
                if (rs.getString("ResourceNames") != null) {
                    alertName = "\'" + rs.getString("ResourceNames").replace(",", "\',\'") + "\'";
                }
                if (rs.getString("Health") != null) {
                    alertHealth = "[\"" + rs.getString("Health") + "\"]";
                }

                if (selection.equals("AlertsByAssignee")) {

                    if (alertName.equals("")) {
                        System.out.println("assigneeList " + assigneeList);
                        nameOfAlert = "[" + assigneeList.substring(1) + "]";
                    } else {
                        //nameOfAlert = "[" + alertName + assigneeList + "]";
                        tmpName = alertName.replaceAll("\'", "\"");
                        nameOfAlert = "[" + tmpName + assigneeList + "]";
                    }

                } else if (selection.equals("AlertsByStatus")) {
                    if (alertName.equals("")) {
                        nameOfAlert = "[" + statusList.substring(1) + "]";
                    } else {
                        //nameOfAlert = "[" + alertName + statusList + "]";
                        tmpName = alertName.replaceAll("\'", "\"");
                        nameOfAlert = "[" + tmpName + statusList + "]";
                    }

                }
                summaryname1 = "";
                summarynameconcat = "";

                /**
                 * **************************************************************************************************
                 * Default Task Summary accepted from properties: File name is
                 * TaskSummay.Properties For specific resourceType and Checking
                 * Task-Summary is present or not. if present then changing from
                 * the properties(MetricMapping.prperties) file
                 * **************************************************************************************************
                 */
                if (selection.equals("CriticalAlerts")) {

                    splitsummaryname = alertName.split(",");
                    if (splitsummaryname.length > 0 && !splitsummaryname[0].equalsIgnoreCase("")) {
                        for (int b = 0; b < splitsummaryname.length; b++) {
                            System.out.println("splitsummaryname==>>" + splitsummaryname[b]);
                            /*if (resourceType.equals("server")) {
                             if (!splitsummaryname[b].toLowerCase().contains("fnsp") && !splitsummaryname[b].toLowerCase().contains("tem")) {
                             //System.out.println("183>>>>splitsummaryname[b] = " + splitsummaryname[b] + ", DBUtilHelper.getMetrics_mapping_properties().getProperty(splitsummaryname[b]) = " + DBUtilHelper.getMetrics_mapping_properties().getProperty(splitsummaryname[b]).trim());
                             summarynameconcat = summarynameconcat + ",\"" + DBUtilHelper.getMetrics_mapping_properties().getProperty(splitsummaryname[b]).trim() + "\"";
                             }
                             } else {*/
                            summarynameconcat = summarynameconcat + ",\"" + DBUtilHelper.getMetrics_mapping_properties().getProperty(splitsummaryname[b]).trim() + "\"";
                            //}
                        }

                        summarynameconcat = summarynameconcat.substring(1);
                        summaryname1 = summarynameconcat;
                    }

                    /**
                     * **************************************************************************************************
                     * Changing Task-Summary Name Present in
                     * Properties(TaskSummary.properties) with some useful
                     * meaning from other properties(MetricMapping.properties)
                     * files
                     * **************************************************************************************************
                     *
                     */
                    /*String[] szArrayCriticalNames = DBUtilHelper.getProperties().getProperty(resourceType).split(",");
                     for (int c = 0; c < szArrayCriticalNames.length; c++) {
                     if (!summaryname1.contains(DBUtilHelper.getMetrics_mapping_properties().getProperty(szArrayCriticalNames[c]).trim())) {
                     summaryname1 = summaryname1 + ",\"" + DBUtilHelper.getMetrics_mapping_properties().getProperty(szArrayCriticalNames[c]).trim() + "\"";
                     }
                     }*/
                }
                if (selection.equals("CriticalAlerts")) {
                    nameOfAlert = "[" + summaryname1 + "]";
                }

                /**
                 * Generating Map for Chart Generation
                 */
                //ticketInfo.put(serviceName, "\"ServiceName\":\"" + serviceName + "\",\"ResourceNames\":" + nameOfAlert
                ticketInfo.put(cCustomer, "\"ServiceName\":\"" + cCustomer + "\",\"ResourceNames\":" + nameOfAlert
                        + ",\"ResourceValues\":" + alertCount + ",\"SlaValues\":[10000],\"Health\":" + alertHealth);
                //ticketInfo.put(host+","+resourceId,"\"ServerName\":\""+host+"\",\"ServiceName\":\""+serviceName+"\",\"ResourceID\":\""+resourceId+"\",\"ResourceType\":\""+resourceType+"\",\"summarycount\":"+summarycount+",\"summaryname\":"+criticalSummaryName+",\"hstdwnstatus\":"+hstdwnstatus+",\"ticketcount\":"+ticketcount+",\"assigneenames\":"+assigneenames+",\"statusnames\":"+statusnames+",\"statuscount\":"+statuscount);
            }
            ConnectionDAO.closeStatement();

            if (ticketInfo.isEmpty()) {
                ticketInfo.put("DefaultMapValue", "\"summarycount\":[],\"summaryname\":[],\"hstdwnstatus\":[],\"ticketcount\":[],\"assigneenames\":[],\"statusnames\":[],\"statuscount\":[]");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("DashBoard Ticket Query /" + cCustomer + "/\n" + e.getMessage());
        } finally {
        }
        return ticketInfo;
    }

    public LinkedHashMap<String, String> collectTicketDataForService(String customer, String cCustomer, String service, String date,
            String date1, String selection) throws IOException, SQLException {
        ticketInfo = new LinkedHashMap<String, String>();
        try {
//            uniqueconnection = ConnectionDAO.getConnection(customer + "");
//            stat = uniqueconnection.createStatement();

            String szQuery = "";

            if (selection.equals("CriticalAlerts")) {
                szQuery = "select array_to_string(array_agg(summaryname),',') as ResourceNames,"
                        + "array_to_string(array_agg(summarycount),',') as ResourceValues,case when sum(criticalcheck)>0 "
                        + "then 'CRITICAL' else 'OK' end as Health from (select count(*) as summarycount,"
                        + "substring(task_summary from position('[' in task_summary) for 25) as summaryname, CASE WHEN "
                        + "substring(task_summary from position('[' in task_summary) for 25) like '%HSTDWN%' "
                        + "THEN 1 ELSE 0 END as criticalcheck from gatask where created_date between  '" + date + "' and '" + date1
                        + "' and status!='Closed' and task_type!='SOX' and customerid='" + cCustomer + "' and attributes4='" + service
                        + "' group by summaryname) as cAlert";
            } else if (selection.equals("AlertsByAssignee")) {
                szQuery = "select array_to_string(array_agg(assigneenames),',') as ResourceNames,"
                        + "array_to_string(array_agg(ticketcount),',') as ResourceValues,case when sum(ticketcount)>0 then 'CRITICAL' else 'OK' "
                        + "end as Health from (select count(*)as ticketcount,g1.assignee as assigneenames "
                        + "from gatask g2 ,gatasktypeassignee g1 where created_date between  '" + date + "' and '" + date1
                        + "' and g1.taskid=g2.task_id and action_assignee=1 and customerid='" + cCustomer + "' and attributes4='"
                        + service + "' group by g1.assignee) as assig";
            } else if (selection.equals("AlertsByStatus")) {
                szQuery = "select array_to_string(array_agg(statusnames),',') as ResourceNames,"
                        + "array_to_string(array_agg(statuscount),',') as ResourceValues,case when sum(statuscount)>0 then 'CRITICAL' else 'OK' "
                        + "end as Health from (select count(*) as statuscount,status as statusnames "
                        + "from gatask where created_date between  '" + date + "' and '" + date1
                        + "' and customerid='" + cCustomer + "' and attributes4='" + service + "' group by status) as sAlert";
            }

            /**
             * ***************************************************************************************************
             * This Query will give Modified result from all necessary tables
             * like gatasktypeassignee, gatask table .. here concatenating the
             * result came from GAtask for Summary Count and checking hostdown
             * status array_agg function in postgresql below 8 version we have
             * to create by following command: create aggregate array_agg (
             * sfunc = array_append, basetype = anyelement, stype = anyarray,
             * initcond = '{}' );
             * ***************************************************************************************************
             */
            System.out.println("Ticket Query /" + cCustomer + "/\n" + szQuery);
            System.out.println("Ticket Query /" + cCustomer + "/\n" + szQuery);

            ResultSet rs = ConnectionDAO.executerQuery(szQuery, customer);//stat.executeQuery(szQuery);
            String alertCount;//
            String alertName;//
            String alertHealth;//
            String nameOfAlert;//
            String sz_query_assignee_list;//
            String assigneeList;//
            String sz_query_status_list;//
            String statusList;//
            String tmpName;//
            String summaryname1;//
            String summarynameconcat;//
            String[] splitsummaryname;//
            while (rs.next()) {
                alertCount = "[]";
                alertName = "";
                alertHealth = "[]";
                nameOfAlert = "";

                /**
                 * ************************************************************************************************
                 * Default Assignee that having no Ticket Assigned for specific
                 * host
                 * ************************************************************************************************
                 *
                 */
                sz_query_assignee_list = "select distinct(user_id) as status from gaoperator where user_id !='Admin' "
                        + "and user_id!='None' and  user_id not in (select g1.assignee from gatask g2 ,"
                        + "gatasktypeassignee g1 where  created_date between '" + date + "' and '" + date1 + "'  and "
                        + "g1.taskid=g2.task_id and action_assignee=1 and customerid='" + cCustomer + "' and attributes4='" + service
                        + "') order by user_id";
                assigneeList = getDefaultAssigneeStatus(sz_query_assignee_list, customer);

                /**
                 * Default Status for specific host
                 */
                sz_query_status_list = " select distinct(status) as status from gastatus where status not in "
                        + "(select status from gatask where created_date between '" + date + "' and '" + date1
                        + "'  and customerid='" + cCustomer + "' and attributes4='" + service + "') order by status";
                statusList = getDefaultAssigneeStatus(sz_query_status_list, customer);

                if (rs.getString("ResourceValues") != null) {
                    alertCount = "[" + rs.getString("ResourceValues") + "]";
                }
                if (rs.getString("ResourceNames") != null) {
                    alertName = "\'" + rs.getString("ResourceNames").replace(",", "\',\'") + "\'";
                }
                if (rs.getString("Health") != null) {
                    alertHealth = "[\"" + rs.getString("Health") + "\"]";
                }
                System.out.println("alertName==>>" + alertName);
                if (selection.equals("AlertsByAssignee")) {
                    if (alertName.equals("")) {
                        nameOfAlert = "[" + assigneeList.substring(1) + "]";
                    } else {
                        //nameOfAlert = "[" + alertName + assigneeList + "]";
                        tmpName = alertName.replaceAll("\'", "\"");
                        nameOfAlert = "[" + tmpName + assigneeList + "]";
                    }

                } else if (selection.equals("AlertsByStatus")) {
                    if (alertName.equals("")) {
                        nameOfAlert = "[" + statusList.substring(1) + "]";
                    } else {
                        //nameOfAlert = "[" + alertName + statusList + "]";
                        tmpName = alertName.replaceAll("\'", "\"");
                        nameOfAlert = "[" + tmpName + statusList + "]";
                    }

                }
                summaryname1 = "";
                summarynameconcat = "";

                /**
                 * **************************************************************************************************
                 * Default Task Summary accepted from properties: File name is
                 * TaskSummay.Properties For specific resourceType and Checking
                 * Task-Summary is present or not. if present then changing from
                 * the properties(MetricMapping.prperties) file
                 * **************************************************************************************************
                 */
                if (selection.equals("CriticalAlerts")) {

                    splitsummaryname = alertName.split(",");
                    if (splitsummaryname.length > 0 && !splitsummaryname[0].equalsIgnoreCase("")) {
                        for (int b = 0; b < splitsummaryname.length; b++) {
                            System.out.println("splitsummaryname==>>" + splitsummaryname[b]);
                            /*if (resourceType.equals("server")) {
                             if (!splitsummaryname[b].toLowerCase().contains("fnsp") && !splitsummaryname[b].toLowerCase().contains("tem")) {
                             //System.out.println("183>>>>splitsummaryname[b] = " + splitsummaryname[b] + ", DBUtilHelper.getMetrics_mapping_properties().getProperty(splitsummaryname[b]) = " + DBUtilHelper.getMetrics_mapping_properties().getProperty(splitsummaryname[b]).trim());
                             summarynameconcat = summarynameconcat + ",\"" + DBUtilHelper.getMetrics_mapping_properties().getProperty(splitsummaryname[b]).trim() + "\"";
                             }
                             } else {*/
                            summarynameconcat = summarynameconcat + ",\"" + DBUtilHelper.getMetrics_mapping_properties().getProperty(splitsummaryname[b]).trim() + "\"";
                            //}
                        }

                        summarynameconcat = summarynameconcat.substring(1);
                        summaryname1 = summarynameconcat;
                    }

                    /**
                     * **************************************************************************************************
                     * Changing Task-Summary Name Present in
                     * Properties(TaskSummary.properties) with some useful
                     * meaning from other properties(MetricMapping.properties)
                     * files
                     * **************************************************************************************************
                     *
                     */
                    /*String[] szArrayCriticalNames = DBUtilHelper.getProperties().getProperty(resourceType).split(",");
                     for (int c = 0; c < szArrayCriticalNames.length; c++) {
                     if (!summaryname1.contains(DBUtilHelper.getMetrics_mapping_properties().getProperty(szArrayCriticalNames[c]).trim())) {
                     summaryname1 = summaryname1 + ",\"" + DBUtilHelper.getMetrics_mapping_properties().getProperty(szArrayCriticalNames[c]).trim() + "\"";
                     }
                     }*/
                }
                if (selection.equals("CriticalAlerts")) {
                    nameOfAlert = "[" + summaryname1 + "]";
                }

                /**
                 * Generating Map for Chart Generation
                 */
                ticketInfo.put(service, "\"ServiceName\":\"" + service + "\",\"ResourceNames\":" + nameOfAlert
                        + ",\"ResourceValues\":" + alertCount + ",\"SlaValues\":[10000],\"Health\":" + alertHealth);
                //ticketInfo.put(host+","+resourceId,"\"ServerName\":\""+host+"\",\"ServiceName\":\""+serviceName+"\",\"ResourceID\":\""+resourceId+"\",\"ResourceType\":\""+resourceType+"\",\"summarycount\":"+summarycount+",\"summaryname\":"+criticalSummaryName+",\"hstdwnstatus\":"+hstdwnstatus+",\"ticketcount\":"+ticketcount+",\"assigneenames\":"+assigneenames+",\"statusnames\":"+statusnames+",\"statuscount\":"+statuscount);
            }
            ConnectionDAO.closeStatement();

            if (ticketInfo.isEmpty()) {
                ticketInfo.put("DefaultMapValue", "\"summarycount\":[],\"summaryname\":[],\"hstdwnstatus\":[],\"ticketcount\":[],\"assigneenames\":[],\"statusnames\":[],\"statuscount\":[]");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("DashBoard Ticket Query /" + cCustomer + "/\n" + e.getMessage());
        } finally {
        }
        return ticketInfo;
    }

    private LinkedHashMap modifyHashMap(LinkedHashMap hm) {
        try {
            LinkedHashMap modifiedMap = new LinkedHashMap(hm);
            String chartType = "[\"0\",\"1\"]";
            String resourceId = "[\"Default\"]";
            Iterator hmKeySetItr = hm.keySet().iterator();
            System.out.println("Displaying HashMap");
            String szKey;//
            String szValue;//
            while (hmKeySetItr.hasNext()) {
                System.out.println("in modifyHashmap for loop ====>");
                
                szKey = hmKeySetItr.next().toString();
                System.out.println("key==>>" + szKey);
                szValue = hm.get(szKey).toString();
                System.out.println("value==>>" + szValue);
                szValue += ",\"ResourceChartType\":" + chartType + ",\"ResourceNames\":" + resourceId;
                modifiedMap.remove(szKey);
                modifiedMap.put(szKey, szValue);
            }
                System.out.println("in modifyHashmap ====>"+modifiedMap);
            return modifiedMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String generateWeatherJson(String resourceType, String cCustomer, String service,
            String startDate, String endDate, String timestampselection,
            String customer) {
        try {
            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateStart = formatter1.parse(startDate);
            Date dateEnd = formatter1.parse(endDate);
            long smilli = dateStart.getTime() / 1000;
            long emilli = dateEnd.getTime() / 1000;
            serviceMetricInfo = new LinkedHashMap<String, String>();
            String szQueryMetric = "select t.metricvalue, t.metrictype from(select customerid,service,resourcetype,metrictype, "
                    + "max(timestamp1) as MaxTime from servicemetrics where resourcetype='Sensor' and metrictype in "
                    + "('Ambient_Temp','Irradiance','Wind_Speed','Module_Temp') and service='" + service + "' and customerid="
                    + "(select id from customerinfo where customername='" + cCustomer + "') and timestamp1 between " + smilli + "and "
                    + emilli + " group by customerid,service,metrictype,resourcetype) r inner join servicemetrics t on t.service=r.service and "
                    + "t.resourcetype=r.resourcetype and t.timestamp1 = r.MaxTime and t.metrictype=r.metrictype and "
                    + "t.customerid=r.customerid group by t.customerid,t.service,t.resourcetype,t.metrictype,t.metricvalue";

            String[] headingNamesMetric1 = {"MetricType", "MetricValue"};
            System.out.println("Weather Query /" + timestampselection + "/ \n" + szQueryMetric);
            serviceMetricInfo = generateJsonFromGivenQuery(szQueryMetric, headingNamesMetric1, customer);
            System.out.println("Weather json hashmap==>>" + serviceMetricInfo.toString());
            String zWeatherJSON = createWeatherJSONFromHashMap(serviceMetricInfo, customer, cCustomer, service, "Sensor");
            System.out.println("Weather json==>>" + zWeatherJSON);
            serviceMetricInfo = null;
            return zWeatherJSON;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in generateWeatherJson==>>" + e.getMessage());
        }
        return null;
    }

    public String generateIrradiationJson(String resourceType, String cCustomer, String service,
            String startDate, String endDate, String timestampselection,
            String customer) {
        String szFileName = cCustomer + "_" + service + "_Irradiation.json";
        FileReader in = null;
        BufferedReader reader = null;
        String line = null;
        StringBuffer szTempBuf = new StringBuffer();
        try {
            System.out.println("Reading file " + szFileName);
            in = new FileReader(szFileName);

            reader = new BufferedReader(in);
            while ((line = reader.readLine()) != null) {
                szTempBuf.append(line);
            }
            return szTempBuf.toString();
        } catch (Exception e) {
            log.error("Error in generateIrradiationJson==>>" + e.getMessage());
            e.printStackTrace();
        } finally {
            szFileName = null;
            try {
                if (in != null) {
                    in.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
            }
            in = null;
            reader = null;
            line = null;
            szTempBuf = null;
        }
        return null;
    }

    public String generateCurrentSnapShotJson(String resourceType, String cCustomer, String service, String startDate, String endDate, String timestampselection, String customer) {
        try {
            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateStart = formatter1.parse(startDate);
            Date dateEnd = formatter1.parse(endDate);
            long smilli = dateStart.getTime() / 1000L;
            long emilli = dateEnd.getTime() / 1000L;
            this.serviceMetricInfo = new LinkedHashMap();
            String szQueryMetric = null;
            String curQuery = null;
            String aggQuery = null;
            String[] headingNamesMetric1 = null;
            String resID = null;
            String mtype = null;
            String mvalue = null;

            String zCurrentSnapShotJSON = "";
            System.out.println("ResourceType in generateCurrentSnapShotJson is " + resourceType);
            if (service.equals("")) {
                aggQuery = "(select resourceid,metrictype,min(metricvalue) as minmetricvalue, max(metricvalue) as maxmetricvalue, round(avg(metricvalue)) "
                        + "as avgmetricvalue from servicemetrics where resourcetype=service and metrictype in ("/*'YearEnergy','TotalEnergy',"*/
                        + "'ACPower') and resourceid = service and customerid=(select id from customerinfo where customername='" + cCustomer + "') and "
                        + "timestamp1 between " + smilli + " and " + emilli + " group by metrictype,resourceid) t1";

                curQuery = "(select t.resourceid,t.metricvalue, t.metrictype from(select resourceid,customerid,service,resourcetype,metrictype,"
                        + "max(timestamp1) as MaxTime from servicemetrics where resourcetype=service and metrictype in ("/*'YearEnergy','TotalEnergy',"*/
                        + "'ACPower') and resourceid = service and customerid=(select id from customerinfo where customername='"
                        + cCustomer + "') and timestamp1 between " + smilli + "and " + emilli + " group by resourceid,customerid,service,metrictype,"
                        + "resourcetype) r " + "inner join servicemetrics t on t.service=r.service and t.resourcetype=r.resourcetype and "
                        + "t.timestamp1 = r.MaxTime and " + "t.metrictype=r.metrictype and t.customerid=r.customerid and t.resourceid=r.resourceid "
                        + "group by t.customerid,t.service,t.resourcetype,t.metrictype," + "t.metricvalue, t.resourceid) t2";

                szQueryMetric = "select resourceid,metrictype, minmetricvalue, maxmetricvalue, metricvalue, avgmetricvalue from "
                        + "(select t1.resourceid,t1.metrictype, t1.minmetricvalue, t1.maxmetricvalue, t2.metricvalue, t1.avgmetricvalue from " + aggQuery
                        + " inner join " + curQuery + " on " + "t1.metrictype=t2.metrictype and t1.resourceid=t2.resourceid) t3 order by resourceid";

                System.out.println("CurrentSnapShot Query at customer level /" + timestampselection + "/ \n" + szQueryMetric);
                headingNamesMetric1 = new String[]{"ResourceID", "MetricType", "MetricValue", "MinMetricValue", "MaxMetricValue", "AvgMetricValue"};
            } else if (resourceType.equals("")) {
                aggQuery = "(select metrictype,min(metricvalue) as minmetricvalue, max(metricvalue) as maxmetricvalue, round(avg(metricvalue)) as "
                        + "avgmetricvalue from servicemetrics where resourcetype='" + service + "' and metrictype in ("/*'YearEnergy','TotalEnergy',"*/
                        + "'ACPower') " + "and resourceid = service and service='" + service + "' and customerid=(select id from "
                        + "customerinfo where customername='" + cCustomer + "') and timestamp1 between " + smilli + " and " + emilli + " group by "
                        + "metrictype) t1";

                curQuery = "(select t.metricvalue, t.metrictype from(select customerid,service,resourcetype,metrictype,max(timestamp1) as MaxTime from "
                        + "servicemetrics where resourcetype=service and metrictype in ("/*'YearEnergy','TotalEnergy',*/ + "'ACPower') and "
                        + "resourceid = service and service='" + service + "' and customerid=(select id from customerinfo where customername='"
                        + cCustomer + "') and timestamp1 between " + smilli + "and " + emilli + " group by customerid,service,metrictype,resourcetype) r "
                        + "inner join servicemetrics t on t.service=r.service and t.resourcetype=r.resourcetype and t.timestamp1 = r.MaxTime and "
                        + "t.metrictype=r.metrictype and t.customerid=r.customerid group by t.customerid,t.service,t.resourcetype,t.metrictype,"
                        + "t.metricvalue) t2";

                szQueryMetric = "select metrictype, minmetricvalue, maxmetricvalue, metricvalue, avgmetricvalue from (select t1.metrictype, "
                        + "t1.minmetricvalue, t1.maxmetricvalue, t2.metricvalue, t1.avgmetricvalue from " + aggQuery + " inner join " + curQuery
                        + " on " + "t1.metrictype=t2.metrictype) t3";

                headingNamesMetric1 = new String[]{"MetricType", "MetricValue", "MinMetricValue", "MaxMetricValue", "AvgMetricValue"};
            } else {
                aggQuery = "(select resourceid,metrictype,min(metricvalue) as minmetricvalue,max(metricvalue) as maxmetricvalue,round(avg(metricvalue)) "
                        + "as avgmetricvalue from servicemetrics where resourcetype='" + resourceType + "' and metrictype in (" /*'YearEnergy','TotalEnergy',"*/
                        + "'ACPower','ACCurrent','ACFrequency','DCCurrent', 'DCVoltage', 'ACVoltage') and resourceid != service and service='" + service
                        + "' and customerid=(select id from customerinfo where customername='" + cCustomer + "') and timestamp1 between " + smilli
                        + " and " + emilli + " group by resourceid, metrictype) t1";

                curQuery = "(select t.resourceid,t.metricvalue, t.metrictype from(select customerid,service,resourcetype,resourceid,metrictype,"
                        + "max(timestamp1) as MaxTime from servicemetrics where resourcetype='" + resourceType + "' and metrictype in ("
                        + /*'YearEnergy','TotalEnergy',*/ "'ACPower','ACCurrent','ACFrequency','DCCurrent', 'DCVoltage', 'ACVoltage') and "
                        + "resourceid != service and service='" + service + "' and customerid=(select id from customerinfo where customername='"
                        + cCustomer + "') and timestamp1 between " + smilli + " and " + emilli + " group by customerid,service,resourceid,metrictype,"
                        + "resourcetype) r inner join servicemetrics t on t.service=r.service and t.resourcetype=r.resourcetype and t.timestamp1 = "
                        + "r.MaxTime" + " and t.metrictype=r.metrictype and t.customerid=r.customerid and t.resourceid=r.resourceid group by t.customerid,"
                        + "t.service," + "t.resourcetype,t.metrictype,t.metricvalue,t.resourceid) t2";

                szQueryMetric = "select resourceid,metrictype, minmetricvalue, maxmetricvalue, metricvalue, avgmetricvalue from (select t1.metrictype, "
                        + "t1.minmetricvalue, t1.maxmetricvalue, t2.metricvalue, t1.avgmetricvalue,t1.resourceid from " + aggQuery + " inner join "
                        + curQuery + " on t1.resourceid=t2.resourceid and t1.metrictype=t2.metrictype) t3 order by resourceid";

                headingNamesMetric1 = new String[]{"ResourceID", "MetricType", "MetricValue", "MinMetricValue", "MaxMetricValue", "AvgMetricValue"};
                System.out.println("Device level snapshot query==for " + timestampselection + "==" + szQueryMetric);
            }

            System.out.println("CurrentSnapShot Query /" + timestampselection + "/ \n" + szQueryMetric);
            this.serviceMetricInfo = generateJsonFromGivenQuery(szQueryMetric, headingNamesMetric1, customer);
            szQueryMetric = null;
            curQuery = null;
            aggQuery = null;
            System.out.println("CurrentSnapShot json hashmap==>>" + this.serviceMetricInfo.toString());
            HashMap deviceLevelMap = new HashMap();

            if ((!resourceType.equals("")) || (service.equals(""))) {
                Iterator entryItr = this.serviceMetricInfo.entrySet().iterator();
                while (entryItr.hasNext()) {
                    Entry szMapEntry = (Entry) entryItr.next();
                    String temp = szMapEntry.getKey().toString();
                    System.out.println("temp==>>" + temp);
                    String[] tempAry1 = temp.split(",");
                    resID = tempAry1[0];
                    System.out.println("resID==>>" + resID);
                    /* if (service.equals("")) {
                     service = resID;
                     }*/

                    mtype = tempAry1[1];
                    System.out.println("mtype==>>" + mtype);
                    mvalue = szMapEntry.getValue().toString();
                    System.out.println("mvalue==>>" + mvalue);
                    LinkedHashMap resMap;
                    if (deviceLevelMap.containsKey(resID)) {
                        resMap = (LinkedHashMap) deviceLevelMap.get(resID);
                    } else {
                        resMap = new LinkedHashMap();
                        deviceLevelMap.put(resID, resMap);
                    }
                    resMap.put(mtype, mvalue);
                    tempAry1 = null;
                    mtype = null;
                    mvalue = null;
                }
                entryItr = null;
                this.serviceMetricInfo.clear();
                this.serviceMetricInfo = null;
                zCurrentSnapShotJSON = "[";
                entryItr = deviceLevelMap.entrySet().iterator();
                while (entryItr.hasNext()) {
                    Entry szMapEntry = (Entry) entryItr.next();
                    LinkedHashMap tempHM = (LinkedHashMap) szMapEntry.getValue();
                    // tempHM = fillHashMap(tempHM, timestampselection, customer, cCustomer, service, resourceType, emilli, (String) szMapEntry.getKey());
                    if (service.equals("")) {
                        tempHM = fillHashMap(tempHM, timestampselection, customer, cCustomer, szMapEntry.getKey().toString(), resourceType, emilli, (String) szMapEntry.getKey());
                        zCurrentSnapShotJSON = zCurrentSnapShotJSON + "{\"ResourceID\":\"" + szMapEntry.getKey() + "\",\n\"Value\":";
                        zCurrentSnapShotJSON = zCurrentSnapShotJSON + createCurrentSnapShotJSONFromHashMap(tempHM, customer, cCustomer, szMapEntry.getKey().toString(), szMapEntry.getKey().toString()) + "},";

                    } else {
                        System.out.println("service in QE" + service);
                        tempHM = fillHashMap(tempHM, timestampselection, customer, cCustomer, service, resourceType, emilli, (String) szMapEntry.getKey());
                        zCurrentSnapShotJSON = zCurrentSnapShotJSON + "{\"ResourceID\":\"" + szMapEntry.getKey() + "\",\n\"Value\":";
                        zCurrentSnapShotJSON = zCurrentSnapShotJSON + createCurrentSnapShotJSONFromHashMap(tempHM, customer, cCustomer, service, service) + "},";

                    }
                }
                if (zCurrentSnapShotJSON.length() > 1) {
                    zCurrentSnapShotJSON = zCurrentSnapShotJSON.substring(0, zCurrentSnapShotJSON.length() - 1);
                }
                zCurrentSnapShotJSON = zCurrentSnapShotJSON + "]";
                entryItr = null;
            } else {
                HashMap tmpMap = new HashMap(this.serviceMetricInfo);
                Iterator entryItr = tmpMap.keySet().iterator();

                while (entryItr.hasNext()) {
                    String szOri = entryItr.next().toString();
                    String szRnm = szOri.substring(0, szOri.indexOf(","));
                    String keyVal = (String) this.serviceMetricInfo.get(szOri);
                    this.serviceMetricInfo.remove(szOri);
                    this.serviceMetricInfo.put(szRnm, keyVal);
                }
                tmpMap = null;

                this.serviceMetricInfo = fillHashMap(this.serviceMetricInfo, timestampselection, customer, cCustomer, service, resourceType, emilli, null);

                zCurrentSnapShotJSON = createCurrentSnapShotJSONFromHashMap(this.serviceMetricInfo, customer, cCustomer, service, service);
            }
            System.out.println("CurrentSnapShot json==>>" + zCurrentSnapShotJSON);
            this.serviceMetricInfo = null;
            return zCurrentSnapShotJSON;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in generateCurrentSnapShotJson==>>" + e.getMessage());
        }
        return null;
    }

    private String createWeatherJSONFromHashMap(LinkedHashMap hm, String customer, String cCustomer, String service, String resourcetype) {

        String mUnit = "";
        try {
            String weatherJSON = "[{\"Now\": [";
            Iterator entryItr = hm.entrySet().iterator();
            Entry szHMEntry;//
            String value;//
            String rem;//
            int startIndx;//
            String lastCh;//
            while (entryItr.hasNext()) {
                mUnit = "";
                szHMEntry = (Entry) entryItr.next();
                value = (String) szHMEntry.getValue();
                rem = "\"MetricValue\":\"";
                startIndx = value.lastIndexOf(rem) + rem.length();
                value = value.substring(startIndx, value.length() - 1);//GetMetricUOM============================================================================================================================
                /*requestURL = DBUtilHelper.MetricUOMURL + "?customer="
                 + cCustomer + "&service=" + service + "&metrictype=" + szHMEntry.getKey() + "&resourcetype="
                 + resourcetype;
                 url = new URL(requestURL);
                 System.out.println("URL " + requestURL);
                 con = (HttpURLConnection) url.openConnection();
                 con.setDoOutput(true);
                 br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                 while ((res = br.readLine()) != null) {
                 jsonString = res.replaceAll("null\\(\'", "");
                 jsonString = jsonString.replaceAll("\'\\)", "");
                 if (jsonString != null) {
                 if (!(jsonString.equalsIgnoreCase("null"))) {
                 System.out.println("response from getMetricUOM::" + jsonString);
                 org.json.simple.JSONObject jObject = (org.json.simple.JSONObject) parser.parse(jsonString);
                 mUnit = (String) jObject.get("MetricUOM");
                 }
                 }
                 }*/
                mUnit = MetricUOM.getConfValue(customer, cCustomer, service, "Default", "Sensor", szHMEntry.getKey().toString());
                if (mUnit.equals("")) {
                } else {
                    mUnit = "(" + mUnit + ")";
                }
                //========================================================================================================================================
                weatherJSON += "{\"name\":\"" + szHMEntry.getKey() + "\",\"value\":\"" + value + "\",\"unit\":\"" + mUnit + "\" },";
            }
            weatherJSON += "{\"name\":\"Ambient_Temp\",\"value\":\"" + -9 + "\",\"unit\":\"\u00B0C\" },";
            weatherJSON += "{\"name\":\"Irradiation\",\"value\":\"" + 4664 + "\",\"unit\":\"kWh/m\u00B2\" },";
            weatherJSON += "{\"name\":\"Wind_Speed\",\"value\":\"" + 70 + "\",\"unit\":\"km/h\" },";
            weatherJSON += "{\"name\":\" Module_Temp\",\"value\":\"" + 24 + "\",\"unit\":\"\u00B0C\" },";
            lastCh = "" + weatherJSON.charAt(weatherJSON.length() - 1);
            if (lastCh.equals(",")) {
                weatherJSON = weatherJSON.substring(0, weatherJSON.length() - 1);
            }
            weatherJSON += "]},{\n"
                    + "        \"Forecast\": [\n"
                    + "\n"
                    + "                {\n"
                    + "                    \"name1\": \"Mon\"\n"
                    + "                ,\n"
                    + "\n"
                    + "                    \"name2\": \"Tue\"\n"
                    + "                ,\n"
                    + "\n"
                    + "                    \"name3\": \"Wed\"\n"
                    + "                ,\n"
                    + "\n"
                    + "                    \"name4\": \"Thu\"\n"
                    + "                ,\n"
                    + "\n"
                    + "                    \"name5\": \"Fri\"\n"
                    + "                ,\n"
                    + "\n"
                    + "                    \"name6\": \"Sat\"\n"
                    + "                ,\n"
                    + "\n"
                    + "                    \"name7\": \"Sun\"\n"
                    + "                }\n"
                    + "            ,\n"
                    + "\n"
                    + "                {\n"
                    + "                    \"name1\": \"28C\"\n"
                    + "                ,\n"
                    + "\n"
                    + "                    \"name2\": \"32C\"\n"
                    + "                ,\n"
                    + "\n"
                    + "                    \"name3\": \"26C\"\n"
                    + "                ,\n"
                    + "\n"
                    + "                    \"name4\": \"22C\"\n"
                    + "                ,\n"
                    + "\n"
                    + "                    \"name5\": \"24C\"\n"
                    + "                ,\n"
                    + "\n"
                    + "                    \"name6\": \"26C\"\n"
                    + "                ,\n"
                    + "\n"
                    + "                    \"name7\": \"24C\"\n"
                    + "                }\n"
                    + "            ,\n"
                    + "\n"
                    + "                {\n"
                    + "                    \"name1\": \"C\"\n"
                    + "                ,\n"
                    + "\n"
                    + "                    \"name2\": \"R\"\n"
                    + "                ,\n"
                    + "\n"
                    + "                    \"name3\": \"S\"\n"
                    + "                ,\n"
                    + "\n"
                    + "                    \"name4\": \"PC\"\n"
                    + "                ,\n"
                    + "\n"
                    + "                    \"name5\": \"C\"\n"
                    + "                ,\n"
                    + "\n"
                    + "                    \"name6\": \"R\"\n"
                    + "                ,\n"
                    + "\n"
                    + "                    \"name7\": \"S\"\n"
                    + "                }\n"
                    + "]\n"
                    + "}\n"
                    + "]";
            return weatherJSON;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in createWeatherJSONFronHashMap==>>" + e.getMessage());
        }
        return "[]";
    }

    private String createCurrentSnapShotJSONFromHashMap(LinkedHashMap hm, String customer, String cCustomer, String service, String resourcetype) {

        try {
            String snapshotJSON = "[";
            String mUnit = "";
            Iterator entryItr = hm.entrySet().iterator();
            Entry szHMEntry;//
            String curVal;
            String minVal;
            String maxVal;
            String avgVal;
            String rem;//
            int startIndx;//
            String lastCh;//
            String value;
            org.json.simple.JSONObject jObject;
            while (entryItr.hasNext()) {
                mUnit = "";
                curVal = "";
                minVal = "";
                maxVal = "";
                avgVal = "";
                szHMEntry = (Entry) entryItr.next();
                value = (String) szHMEntry.getValue();

                value = value.replaceAll("\"", "");
                System.out.println("value==" + value);
                rem = ",MetricValue:";
                startIndx = value.lastIndexOf(rem);
                System.out.println("startIndx==" + startIndx);
                if (startIndx != -1) {
                    startIndx += rem.length();
                    int tmpIndx = value.indexOf(",", startIndx);
                    if (tmpIndx != -1) {
                        curVal = value.substring(startIndx, tmpIndx);
                    } else {
                        curVal = value.substring(startIndx);
                    }
                }
                rem = ",MinMetricValue:";
                startIndx = value.lastIndexOf(rem);
                System.out.println("startIndx==" + startIndx);
                if (startIndx != -1) {
                    startIndx += rem.length();
                    int tmpIndx = value.indexOf(",", startIndx);
                    if (tmpIndx != -1) {
                        minVal = value.substring(startIndx, tmpIndx);
                    } else {
                        minVal = value.substring(startIndx);
                    }
                }
                rem = ",MaxMetricValue:";
                startIndx = value.lastIndexOf(rem);
                System.out.println("startIndx==" + startIndx);
                if (startIndx != -1) {
                    startIndx += rem.length();
                    int tmpIndx = value.indexOf(",", startIndx);
                    if (tmpIndx != -1) {
                        maxVal = value.substring(startIndx, tmpIndx);
                    } else {
                        maxVal = value.substring(startIndx);
                    }
                }
                rem = ",AvgMetricValue:";
                startIndx = value.lastIndexOf(rem);
                System.out.println("startIndx==" + startIndx);
                if (startIndx != -1) {
                    startIndx += rem.length();
                    if (startIndx != value.length() - 1) {
                        avgVal = value.substring(startIndx, value.length());
                    } else {
                        avgVal = value.substring(startIndx);
                    }
                }
                System.out.println("cur-value=" + curVal + "min-value=" + minVal + "max-value=" + maxVal + "avg-value" + avgVal);

                /*requestURL = DBUtilHelper.MetricUOMURL + "?customer=" + cCustomer + "&service=" + service + "&metrictype=" + szHMEntry.getKey() + "&resourcetype=" + resourcetype;

                 url = new URL(requestURL);
                 System.out.println("URL " + requestURL);
                 con = (HttpURLConnection) url.openConnection();
                 con.setDoOutput(true);
                 br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                 while ((res = br.readLine()) != null) {
                 jsonString = res.replaceAll("null\\('", "");
                 jsonString = jsonString.replaceAll("'\\)", "");
                 if ((jsonString == null)
                 || (jsonString.equalsIgnoreCase("null"))) {
                 continue;
                 }
                 System.out.println("response from getMetricUOM::" + jsonString);
                 jObject = (org.json.simple.JSONObject) parser.parse(jsonString);
                 mUnit = (String) jObject.get("MetricUOM");
                 }*/
                mUnit = MetricUOM.getConfValue(customer, cCustomer, service, "Default", "Inverter", szHMEntry.getKey().toString());
                if (mUnit != null) {
                    if (mUnit.equals("")) {
                    } else {
                        mUnit = "(" + mUnit + ")";
                    }
                }

                snapshotJSON = snapshotJSON + "{\"name\":\"" + szHMEntry.getKey() + "\",\"curvalue\":\"" + curVal + "\",\"minvalue\":\"" + minVal + "\",\"maxvalue\":\"" + maxVal + "\",\"avgvalue\":\"" + avgVal + "\",\"unit\":\"" + mUnit + "\" },";
            }
            lastCh = "" + snapshotJSON.charAt(snapshotJSON.length() - 1);
            if (lastCh.equals(",")) {
                snapshotJSON = snapshotJSON.substring(0, snapshotJSON.length() - 1);
            }
            snapshotJSON = snapshotJSON + "]";
            return snapshotJSON;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in createCurrentSnapShotJSONFromHashMap==>>" + e.getMessage());
        }
        return "[]";
    }

    private LinkedHashMap fillHashMap(LinkedHashMap<String, String> serviceMetricInfo, String timestampselection, String customer, String cCustomer,
            String service, String resType, long eMilli, String resourceid) {
        String periodMetric = "XXXEnergy";
        ResultSet rs = null;
        //String targetEnergyPeriod = "TargetEnergyXXX";
        String pr = "";
        String eprp = "";
        String co2saving = "";
        String earning = "";
        Date date = new Date(eMilli * 1000);
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        System.out.println("Date>> " + date);
        System.out.println(cal1.getTime());
        int day = cal1.get(Calendar.DAY_OF_MONTH);
        int year = cal1.get(Calendar.YEAR);
        int month = cal1.get(Calendar.MONTH);
        int week = cal1.get(Calendar.WEEK_OF_MONTH);
        URL url = null;
        boolean deviceLevel = false;
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        HttpURLConnection conn = null;
        InputStreamReader isReader = null;
        BufferedReader br = null;
        String response = null;
        String panelArea;
        String mEfficiency;
        String pvpa = "PVPanelArea";
        String meff = "ModuleEfficiency";
        double totalNA = 0;
        String subServiceQuery = null;
        try {
            // if (timestampselection.equalsIgnoreCase("Hour")) {
            //    timestampselection = "Day";
            //}
            System.out.println("Service in query>>" + service);
            System.out.println("resType in query>>" + resType);

            if (service.equals("") || service == null) {
                System.out.println("QEcustomer>>> ");

                url = new URL(DBUtilHelper.MetricValueURL + "?customer=" + cCustomer
                        + "&metrictype=Energy" + "&service=" + resourceid + "&timeperiod=" + timestampselection
                        + "&resourcetype=" + resourceid + "&resourceid=" + resourceid + "&slot=" + timestampselection + "&date=" + day + "&week=" + week + "&month=" + months[month]
                        + "&year=" + year);

            } else if (resType.equals("")) {
                System.out.println("QEservice>>> ");

                url = new URL(DBUtilHelper.MetricValueURL + "?customer=" + cCustomer
                        + "&metrictype=Energy" + "&service=" + service + "&timeperiod=" + timestampselection
                        + "&resourcetype=" + service + "&resourceid=" + service + "&slot=" + timestampselection + "&date=" + day + "&week=" + week + "&month=" + months[month]
                        + "&year=" + year);

            } else {
                System.out.println("QEdevice>>> ");
                deviceLevel = true;
                url = new URL(DBUtilHelper.MetricValueURL + "?customer=" + cCustomer
                        + "&metrictype=Energy" + "&service=" + service + "&timeperiod=" + timestampselection
                        + "&resourcetype=" + resType + "&slot=" + timestampselection + "&resourceid=" + resourceid + "&date=" + day + "&week=" + week + "&month=" + months[month]
                        + "&year=" + year);

            }
            System.out.println("QEurl>>> " + url);

            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            isReader = new InputStreamReader(conn.getInputStream());
            br = new BufferedReader(isReader);
            // resultJson = "[]";
            String[] splite;
            String[] spliteca;
            String[] spliteco;
            String szEnergy = null;
            Double periodEnergy = null;
            double jsonIrradianValue = 0;

            while ((response = br.readLine()) != null) {
                System.out.println("QEResponse from getMetricValue API::" + response);

                if (response.contains(":")) {
                    splite = response.split("}");

                    for (int i = 0; i < splite.length; i++) {

                        System.out.println("sp[i]==>" + i + splite[i]);
                        if (splite[i].contains(",") && splite[i].contains(":")) {
                            spliteca = splite[i].split(",");
                            if (spliteca[1].contains(":")) {
                                spliteco = spliteca[1].split(":");
                            } else {
                                spliteco = spliteca[2].split(":");
                            }
                            System.out.println("spca==>" + spliteca[1]);
                            System.out.println("spco==>" + spliteco[0]);
                            System.out.println("spco==>" + spliteco[1]);
                            szEnergy = spliteco[1].substring(1, spliteco[1].length() - 1);
                            System.out.println("val==>" + szEnergy);

                        } else if (splite[i].contains(":")) {
                            System.out.println("else bb");
                            spliteco = splite[i].split(":");
                            System.out.println("spco==>" + spliteco[0]);
                            System.out.println("spco==>" + spliteco[1]);
                            szEnergy = spliteco[1].substring(1, spliteco[1].length() - 1);
                        }
                    }
                }
                /*if (serviceMetricInfo.get("YearEnergy") == null) {
                 serviceMetricInfo.put("YearEnergy", "\"MetricType\":\"YearEnergy\",\"MetricValue\":\"\"");
                 }
                 if (serviceMetricInfo.get("TotalEnergy") == null) {
                 serviceMetricInfo.put("TotalEnergy", "\"MetricType\":\"TotalEnergy\",\"MetricValue\":\"\"");
                 }*/
                if (serviceMetricInfo.get("ACPower") == null) {
                    serviceMetricInfo.put("ACPower", "\"MetricType\":\"ACPower\",\"MetricValue\":\"\"");
                }
                periodMetric = periodMetric.replace("XXX", timestampselection);
                //targetEnergyMinute = targetEnergyMinute.replace("XXX", timestampselection);



                //  String szTemp = (String) serviceMetricInfo.get(periodMetric);
                if (deviceLevel == true) {
                    subServiceQuery = "select distinct service,subservice,resourceid from hostinfo where customerid=(select id from customerinfo where customername='" + cCustomer + "') and service='" + service + "' and resourceid='" + resourceid + "'";

                } else {
                    subServiceQuery = "select distinct service,subservice from hostinfo where customerid=(select id from customerinfo where customername='" + cCustomer + "') and service='" + service + "'";

                }

                System.out.println("subquery of queryexe " + subServiceQuery);
                rs = ConnectionDAO.executerQuery(subServiceQuery, customer);
                totalNA = 0;
                while (rs.next()) {
                    System.out.println("subservice of queryexe " + rs.getString("subservice"));
                    if (deviceLevel == true) {

                        panelArea = ResourceConfiguration.getConfValue(customer, cCustomer, rs.getString("service"), rs.getString("subservice"),
                                rs.getString("resourceid"), pvpa);
                        System.out.println("panel area of queryexe " + panelArea);
                        mEfficiency = ResourceConfiguration.getConfValue(customer, cCustomer, rs.getString("service"), rs.getString("subservice"),
                                rs.getString("resourceid"), meff);
                        System.out.println("mEfficiency of queryexe " + mEfficiency);
                        totalNA = totalNA + (Double.parseDouble(panelArea) * Double.parseDouble(mEfficiency));

                        System.out.println("TotalNA of queryexe " + totalNA);

                    } else {



                        panelArea = ResourceConfiguration.getConfValue(customer, cCustomer, rs.getString("service"), rs.getString("subservice"),
                                null, pvpa);
                        System.out.println("panel area of queryexe " + panelArea);
                        mEfficiency = ResourceConfiguration.getConfValue(customer, cCustomer, rs.getString("service"), rs.getString("subservice"),
                                null, meff);
                        System.out.println("mEfficiency of queryexe " + mEfficiency);
                        if (mEfficiency != null) {
                            totalNA = totalNA + (Double.parseDouble(panelArea) * Double.parseDouble(mEfficiency));
                        }
                        System.out.println("TotalNA of queryexe " + totalNA);
                    }
                }

                String szTemp = szEnergy;
                System.out.println(szTemp + "QEval>>> " + szEnergy);

                if (service.equals("")) {
                    System.out.println("QEcustomermap");
                    //service = null;
                    service = resourceid;
                }
                if (szTemp != null) {
                    System.out.println(szTemp);

                    if ((szTemp != null)
                            && (!szTemp.equals(""))) {
                        periodEnergy = Double.valueOf(Double.parseDouble(szTemp));
                        System.out.println(szTemp);
                        String confVal = null;
                        /* if (deviceLevel == true) {
                         confVal = ResourceConfiguration.getConfValue(customer, cCustomer, service, "Default", resourceid, targetEnergyMinute);
                         } else {
                         confVal = ResourceConfiguration.getConfValue(customer, cCustomer, service, "Default", null, targetEnergyMinute);
                         }
                         System.out.println("confVal" + confVal);
                         if (confVal != null) {
                         */
                        /*Double confDVal = Double.valueOf(Double.parseDouble(confVal)
                         * DateGenerator.getElapsedMinutes(timestampselection, null, customer, cCustomer, service));
                         */
                        jsonIrradianValue = DateGenerator.getIrradiationOfMinutes(timestampselection, null, customer, cCustomer, service);
                        if (jsonIrradianValue > 0) {

                            Double val = Double.valueOf(periodEnergy.doubleValue() / (jsonIrradianValue * totalNA));
                            System.out.println(val);
                            DecimalFormat df = new DecimalFormat("#.#");
                            pr = pr + df.format(val);
                            System.out.println("QEval>>> " + val);

                        }
                        if (deviceLevel == true) {
                            confVal = ResourceConfiguration.getConfValue(customer, cCustomer, service, "Default", resourceid, "Pmax");
                        } else {
                            confVal = ResourceConfiguration.getConfValue(customer, cCustomer, service, "Default", null, "InstalledCapacity");
                        }
                        if (confVal != null) {
                            Double confDVal = Double.valueOf(Double.parseDouble(confVal));
                            Double val = Double.valueOf(periodEnergy.doubleValue() / confDVal.doubleValue());
                            DecimalFormat df = new DecimalFormat("#.#");
                            eprp = eprp + df.format(val);
                            System.out.println("QEval>>> " + val);
                        }

                        confVal = ResourceConfiguration.getConfValue(customer, cCustomer, null, "Default", null, "CO2Factor");
                        if (confVal != null) {
                            Double confDVal = Double.valueOf(Double.parseDouble(confVal));
                            Double val = Double.valueOf(periodEnergy.doubleValue() * confDVal.doubleValue());
                            DecimalFormat df = new DecimalFormat("#.#");
                            co2saving = co2saving + df.format(val);
                            System.out.println("QEval>>> " + val);
                        }

                        confVal = ResourceConfiguration.getConfValue(customer, cCustomer, null, "Default", null, "Earnings/kWh");
                        if (confVal != null) {
                            Double confDVal = Double.valueOf(Double.parseDouble(confVal));
                            Double val = Double.valueOf(periodEnergy.doubleValue() * confDVal.doubleValue());
                            DecimalFormat df = new DecimalFormat("#.#");
                            earning = earning + df.format(val);
                            System.out.println("QEval>>> " + val);
                        }
                    }

                }
            }
            serviceMetricInfo.put("PerformanceRatio", "\"MetricType\":\"PerformanceRatio\",\"MetricValue\":\"" + pr + "\"");
            serviceMetricInfo.put("EnergyPerRatedPower", "\"MetricType\":\"EnergyPerRatedPower\",\"MetricValue\":\"" + eprp + "\"");
            serviceMetricInfo.put("CarbonSaving", "\"MetricType\":\"CarbonSaving\",\"MetricValue\":\"" + co2saving + "\"");
            serviceMetricInfo.put("Earning", "\"MetricType\":\"Earning\",\"MetricValue\":\"" + earning + "\"");
            LinkedHashMap tmpMap = new LinkedHashMap();
            tmpMap.put("Energy", "\"MetricType\":\"Energy\",\"MetricValue\":\"" + periodEnergy + "\"");
            tmpMap.putAll(serviceMetricInfo);
            serviceMetricInfo.clear();
            serviceMetricInfo = null;
            System.out.println("mapval==>" + tmpMap);
            return tmpMap;

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in fillHashMap");
        } finally {
            try {
                ConnectionDAO.closeStatement();
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    rs = null;
                } else if (subServiceQuery != null) {
                    subServiceQuery = null;
                } else if (date != null) {
                    date = null;
                } else if (cal1 != null) {
                    cal1.clear();
                    cal1 = null;
                } else if (months != null) {
                    months = null;
                } else if (conn != null) {
                    conn.disconnect();
                    conn = null;
                } else if (isReader != null) {
                    isReader.close();
                    isReader = null;
                } else if (br != null) {
                    br.close();
                    br = null;
                } else if (response != null) {
                    response = null;
                } else if (url != null) {
                    url = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        return null;
    }
}
