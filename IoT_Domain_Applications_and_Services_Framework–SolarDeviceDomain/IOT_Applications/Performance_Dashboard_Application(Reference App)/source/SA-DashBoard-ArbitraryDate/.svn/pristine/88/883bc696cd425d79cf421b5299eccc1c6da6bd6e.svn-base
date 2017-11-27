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

package com.merit.dashboard.updatethresholds;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.merit.connection.ConnectionDAO;
import com.merit.dashboard.util.PropertyUtil;


public class UpdateMetricThreshold extends HttpServlet {

	private static final long serialVersionUID = 1L;
	static  Logger log = Logger.getLogger(UpdateMetricThreshold.class);
	String MetricName="";

/**
 * ************************************************************************************************
 * This method is for updating threshold values of each metric type of each service
 * and then returning JSON String for creating JSON Files for generating Chart
 * *************************************************************************************************
 */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {

        log.debug("==============================UpdateMetricThreshold===========================================");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        PropertyUtil propertyUtil=null;
        Statement st=null;
        PreparedStatement prest=null;
        ResultSet rs = null;
                String sql="";
                int record_count = 0;
        try {
        	propertyUtil=new PropertyUtil();
       String customer=request.getParameter("CustomerName");
       String ServiceName=request.getParameter("ServiceName");
       MetricName=propertyUtil.getreverseMapping_Properties().get(request.getParameter("MetricName"));
       String SLAValue=request.getParameter("SLAValue");
       String resourceId=request.getParameter("resourceId");
       String host=request.getParameter("host");
       String resourceType=request.getParameter("resourceTypeName");

        Connection uniqueconnection=ConnectionDAO.getConnection(customer);
       sql="update servicemetrics set SLA= ? where metrictype= ? and host=? and resourceid=? and timestamp1=(select max(timestamp1) from servicemetrics where resourceType='"+resourceType+"' and metrictype='"+MetricName+"' and host='"+host+"' and resourceid='"+resourceId+"')";

        prest=uniqueconnection.prepareStatement(sql);
         prest.setDouble(1,Double.parseDouble(SLAValue));
        prest.setString(2, MetricName);
        prest.setString(3, host);
        prest.setString(4, resourceId);
       prest.executeUpdate();
     String check_query="select count(*) as count from smetricslathresholds where metrictype ='"+MetricName+"' and service ='"+ServiceName+"' and host='"+host+"' and resourceid='"+resourceId+"'";

       st=uniqueconnection.createStatement();
       rs = st.executeQuery(check_query);
        while (rs.next()) {
        record_count=rs.getInt("count");
            }
        if(record_count==0)
        {
            sql="insert into smetricslathresholds (metricthresholdvalue,metrictype,service,host,resourceid,resourcetype) values(?,?,?,?,?,?)";
            System.out.println(sql);
        }
         else
        {
         sql="update smetricslathresholds set metricthresholdvalue= ? where metrictype= ? and service= ? and host=? and resourceid=? and resourcetype=?";
        }

			prest = uniqueconnection.prepareStatement(sql);
			prest.setDouble(1, Double.parseDouble(SLAValue));
			prest.setString(2, MetricName);
			prest.setString(3, ServiceName);
			prest.setString(4, host);
			prest.setString(5, resourceId);
			prest.setString(6, resourceType);


			prest.executeUpdate();
			char[] stringArray = MetricName.toCharArray();
			stringArray[0] = Character.toUpperCase(stringArray[0]);
			String MetricNameWithFirstCharCaps = new String(stringArray);
			log.info("in Update metric threshold \n ServiceName=" + ServiceName
					+ "MetricName=" + MetricName + "SLAValue=" + SLAValue);
			response.getWriter().write(
					MetricNameWithFirstCharCaps + " Successfully Updated");
			MetricNameWithFirstCharCaps=null;
			log.debug("==============================End of UpdateMetricThreshold===========================================");
		} catch (Exception e) {
			char[] stringArray = MetricName.toCharArray();
			stringArray[0] = Character.toUpperCase(stringArray[0]);
			String MetricNameWithFirstCharCaps = new String(stringArray);
			response.getWriter().write(MetricNameWithFirstCharCaps + "Not  Successfully Updated");
			MetricNameWithFirstCharCaps=null;
			log.error("IN UpdateMetricThreshold some ERROR is here :"
					+ e.getMessage());
			e.printStackTrace();
		}
            finally {
            out.close();

        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }


}
