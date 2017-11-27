/***************************************************************************
 *
 *                            Software Developed by
 *                           Merit Systems Pvt. Ltd.,
 *              #55/C-42/1, Nandi Mansion, Ist Floor 40th Cross, Jayanagar 8th Block
 *                          Bangalore - 560 070, India
 *                Work Created for Merit Systems Private Limited
 *                             All rights reserved
 *
 *          THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT
 *                              LAWS AND TREATIES
 *       NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED,
 *             DISTRIBUTED, REVISED, MODIFIED,TRANSLATED, ABRIDGED,
 *                                  CONDENSED,
 *        EXPANDED, COLLECTED, COMPILED, LINKED, RECAST, TRANSFORMED OR
 *                                   ADAPTED
 *                      WITHOUT THE PRIOR WRITTEN CONSENT
 *          ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION
 *                                COULD SUBJECT
 *               THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 *
 *
 ***************************************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import businessmodel.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author gopal
 */
public class GetHosts extends HttpServlet
{

    static Logger log = Logger.getLogger(GetHosts.class);

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("text/xml;charset=UTF-8");
        StringBuffer results = new StringBuffer("<hostservices>");
        StringBuffer result = new StringBuffer();
        String szSessionid = "";
        String opStatus = "";
        String opDescription = "";
        boolean fchecksession = false;
        boolean fchkParam = false;
        String szHostServices = null;
        try
        {
            szSessionid = request.getParameter("szsessionid");
            if (szSessionid != null && !szSessionid.equalsIgnoreCase(""))
            {
                //iPort = Integer.parseInt(szPort);
                fchkParam = true;
            }
            if (fchkParam)
            {
                /*fchecksession = CheckSession.checkSession(request, szSessionid);
                if (fchecksession)
                {*/
                    result.append("<hosttoservices>");
                    //logic to get the 
                    szHostServices = GetHostServices.getAllHostServices(request);
                    if (szHostServices == null)
                    {
                        opStatus = "107";
                        opDescription = "Database Operation failed";

                    } else
                    {
                        if(szHostServices.equalsIgnoreCase(""))
                            result.append("Undefined");
                        else
                            result.append(szHostServices);

                        opStatus = "0";
                        opDescription = "Operation Success";
                    }
                    result.append("</hosttoservices>");
                /*} else
                {
                opStatus = "102";
                opDescription = "Invalid Session";
                }*/
            } else
            {
                opStatus = "108";
                opDescription = "Invalid Inputs";

            }
        } catch (Exception e)
        {
            e.printStackTrace();
            log.error("Error while performing action", e);
            opStatus="106";
            opDescription="Unable to process the request action";
        }
        try
        {
            results.append("<status>\n");
            results.append("<code>\n");
            results.append(opStatus);
            results.append("</code>\n");
            results.append("<Description>\n");
            results.append(opDescription);
            results.append("</Description>\n");
            results.append("</status>\n");
            if (!(result == null))
            {
                results.append(result);
            }
            results.append("</hostservices>");
            response.getWriter().write(results.toString());

        } catch (Exception e)
        {
            log.debug("exception while writing output", e);
            log.debug("exception while writing output");
        } finally
        {
            try
            {
                results = null;
                result = null;
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo()
    {
        return "Short description";
    }// </editor-fold>
}
