/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AV_Action;

import Model.DatabaseConnection;
import businessmodel.GetServices;
import com.adminAPI.DeviceManagment;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
/**
 *
 * @author niteshc
 */
public class AV_GetServices extends HttpServlet {

    static Logger log = Logger.getLogger(AV_GetServices.class);

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        StringBuffer result = new StringBuffer();
        String servInfo = null;
        String customer = null;
        try {
            customer = request.getParameter("customer");
            Connection con = DatabaseConnection.getAVSAConnection(request);
            servInfo=new DeviceManagment().getServices(con, customer);
            
            
            if (servInfo == null) {
                log.debug("No data found");
                result.append("{\"error\":\"error\"}");
            } 
            /* Commented By ananddw
            servInfo = GetServices.getInstallationsInfo(request, customer);
            if (servInfo == null) {
                log.debug("No data found");
                result.append("{\"error\":\"error\"}");
            } else {
                if (servInfo.equals("null")) {
                    servInfo = "[]";
                } else {
                    servInfo = "[" + servInfo + "]";
                }
                }
            */
                String callback = request.getParameter("callback");
                result.append(callback + "('" + servInfo + "')");
            
            //System.out.println("Response==>>" + result);
            con.close();
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
                if (result != null) {
                    result = null;
                }
                if (servInfo != null) {
                    servInfo = null;
                }
                if (customer != null) {
                    customer = null;
                }
                if (out != null) {
                    out.close();
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
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
     * Handles the HTTP <code>POST</code> method.
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