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
**********************************************************************
 */
package com.merit.dashboard.reports;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.merit.dashboard.DBUtil.DBUtilHelper;

/**
 * This class is used to get Parameters and generate JSON string for making
 * Summary grid (This summary view is generated by clicking on chart series)
 *
 * @author satya
 */
@SuppressWarnings("serial")
public class ReportsController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String sz_metricType = null;
        String sz_hostName = null;
        String sz_resourceType = null;
        //String sz_domainName[] = null;
        String sz_customerID = null;
        String sz_combobox_selected = null;
        String sz_tab_selected = null;
        String sz_interval = null;
        String resourceid = null;
        String sz_cCustomer = null;
        String sz_Service = null;
        String sz_date = null;
        try {
            URL url = new URL(request.getRequestURL().toString());
            sz_metricType = request.getParameter("metrictype");
            sz_resourceType = request.getParameter("resourcetype");
            sz_combobox_selected = request.getParameter("ComboSelected");
            sz_tab_selected = request.getParameter("selectedTab");
            sz_hostName = request.getParameter("hostName");
            sz_interval = request.getParameter("Interval");
            resourceid = request.getParameter("resourceId");
            sz_cCustomer = request.getParameter("customer");
            sz_Service = request.getParameter("service");
            sz_date = request.getParameter("date");
            //sz_domainName = url.getHost().split(".");
            System.out.println("sz_combobox_selected==" + sz_combobox_selected);
            System.out.println("<-------------- -------------------------------------------- ------------>");
            System.out.println("<-------------- domainName = " + request.getServerName() + " ------------>");
            System.out.println("<-------------- -------------------------------------------- ------------>");
            //updated By Anand Kumar Verma on date 18th March 2014
            //sz_customerID = DBUtilHelper.getMetrics_mapping_properties().getProperty("domainName");
            sz_customerID = request.getServerName();
            System.out.println("selectedTab===" + sz_tab_selected);
            DAOHelper helper = new DAOHelper();
            String sz_response_JSON = "";
            sz_response_JSON = helper.getMetricTypeWithTimestamp(sz_customerID, sz_metricType, sz_hostName, sz_resourceType, 
                    sz_combobox_selected, sz_tab_selected, sz_interval, resourceid, sz_cCustomer, sz_Service, sz_date);
            out.write(sz_response_JSON);
            url = null;
            helper = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // out.close();
        }
    }

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
