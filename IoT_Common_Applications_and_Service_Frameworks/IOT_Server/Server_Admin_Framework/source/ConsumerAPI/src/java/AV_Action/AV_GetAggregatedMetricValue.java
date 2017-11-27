/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AV_Action;

import businessmodel.GetAggregatedMetricValue;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author niteshc
 */
public class AV_GetAggregatedMetricValue extends HttpServlet {

    static Logger log = Logger.getLogger(AV_GetAggregatedMetricValue.class);

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        StringBuffer result = new StringBuffer();
        String customername = null;
        String service = null;
        String deviceid = null;
        String metrictype = null;
        String timeperiod = null;
        String metricvalue = null;
        String resourcetype = null;
        String slot = null;
        int selectedYear = 0;
        String selectedMonth = null;
        int selectedWeek = 0;
        int selectedDate = 0;
        int selectedHour = 0;
        try {
            customername = request.getParameter("customer");
            service = request.getParameter("service");
            deviceid = request.getParameter("resourceid");
            metrictype = request.getParameter("metrictype");
            resourcetype = request.getParameter("resourcetype");
            timeperiod = request.getParameter("timeperiod");
            slot = request.getParameter("slot");
            selectedYear = Integer.valueOf(request.getParameter("year"));
            selectedMonth = request.getParameter("month");
            selectedWeek = Integer.valueOf(request.getParameter("week"));
            selectedDate = Integer.valueOf(request.getParameter("date"));
            String tmp = request.getParameter("hour");
            if (tmp != null) {
                if (!(tmp.equals("null"))) {
                    selectedHour = Integer.valueOf(tmp);
                }
            }

            metricvalue = GetAggregatedMetricValue.getAggregatedMetricValue(request, customername, service, deviceid, metrictype, resourcetype, timeperiod, slot, selectedYear, selectedMonth, selectedWeek, selectedDate, selectedHour);
            if (metricvalue == null) {
                log.debug("No data found");
                result.append("{\"error\":\"error\"}");
            } else {
                if (metricvalue.equals("null")) {
                    metricvalue = "[" + metricvalue + "]";
                } else {
                    metricvalue = "[" + metricvalue + "]";
                }
                String callback = request.getParameter("callback");
                result.append(callback + "('" + metricvalue + "')");
            }
            //System.out.println("Response==>" + result);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Error while performing action", ex);
        }
        try {
            response.getWriter().write(result.toString());
        } catch (Exception ex) {
            log.debug("exception while writing output", ex);
        } finally {
            try {
                customername = null;
                service = null;
                deviceid = null;
                metrictype = null;
                timeperiod = null;
                result = null;
                slot = null;
                selectedMonth = null;
                if (out != null) {
                    out.close();
                    out = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
