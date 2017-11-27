/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AV_Action;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import license.LicenseServiceImpl;
import org.apache.log4j.Logger;

/**
 *
 * @author niteshc
 */
public class AV_SALicense extends HttpServlet {

    static Logger log = Logger.getLogger(AV_SALicense.class);

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
        LicenseServiceImpl license = new LicenseServiceImpl(request);

        String operation = null;
        String customerName = null;
        String productId = null;
        String licenseType = null;
        String timeStamp = null;
        String timeZone = null;
        String startDate = null;
        String endDate = null;
        String optionalAttribs = null;
        String optionalLabel = null;
        String tLicenseKey = null;
        String ret = null;

        try {
            System.out.println("AV_SALicense");
            operation = request.getParameter("operation");
            customerName = request.getParameter("customerName");
            productId = request.getParameter("productId");
            licenseType = request.getParameter("licenseType");
            timeStamp = request.getParameter("timeStamp");
            timeZone = request.getParameter("timeZone");
            startDate = request.getParameter("startDate");
            endDate = request.getParameter("endDate");
            optionalAttribs = request.getParameter("optionalAttribs");
            optionalLabel = request.getParameter("optionalLabel");
            tLicenseKey = request.getParameter("tLicenseKey");

            if (operation.equalsIgnoreCase("createLicense")) {

                ret = license.createLicense(customerName, productId, licenseType, timeStamp, timeZone, startDate, endDate, optionalAttribs);

            }
            if (operation.equalsIgnoreCase("checkLicense")) {

                if (tLicenseKey != null) {

                    ret = license.checkLicense(customerName, productId, optionalLabel, tLicenseKey);

                } else {

                    ret = license.checkLicense(customerName, productId, optionalLabel);

                }
            }
            if (ret == "1") {
                //     System.out.println("if ret====>" + ret);

                out.print(ret);

            } else {
                //       System.out.println("else ret====>" + ret);
                ret = ret.substring(0, ret.length() - 1);
                out.print(ret);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
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
