/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author gopal
 */
public class GetXlPath extends HttpServlet
{

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("text/xml;charset=UTF-8");
        StringBuffer results = new StringBuffer("<file-names>");
        StringBuffer result = null;
        String opStatus = "";
        String opDescription = "";
        boolean filesource = false;
        String szSessionid = "";
        boolean checksession = false;
        szSessionid = request.getParameter("szsessionid");
        /*checksession = CheckSession.checkSession(request, szSessionid);
        if (checksession)
        {*/
            try
            {
                System.out.println("GETXLPAth Started ..");

                Properties properties = new Properties();
                try
                {
                    String fp = System.getProperty("file.separator");
                    //String home = fp+"opt"+fp+"sadesk";
                    String home = System.getProperty("user.home");

                    String configPath = home;
                    configPath = configPath + fp+"Chartconfig"+fp+"reportxl.properties";
                    System.out.println("path=========" + configPath);
                    properties.load(new FileInputStream(configPath));
                    filesource = true;
                } catch (Exception fe)
                {
                    fe.printStackTrace();
                    opStatus = "112";
                    opDescription = "Source not found";
                }

                if (filesource)
                {
                    String xl_path = properties.getProperty("memmoryusage");
                    System.out.println("XL path::"+xl_path);
                    File pathName = new File(xl_path);
                    String[] fileNames = pathName.list();
                    result = new StringBuffer("<files>");
                    for (int i = 0; i < fileNames.length; i++)
                    {
                        result.append("<name>");
                        result.append(fileNames[i]);
                        result.append("</name>");
                    }
                    result.append("</files>");

                    opStatus = "0";
                    opDescription = "Operation Success";
                } else
                {
                    opStatus = "107";
                    opDescription = "No data found";
                }




            //request.setAttribute("filenames", fileNames);
            } catch (Exception e)
            {
                System.out.println("Error: " + e);
                e.printStackTrace();
                opStatus = "106";
                opDescription = "Unable to process the request";
            }
        /*} else
        {
        opStatus = "102";
        opDescription = "Invalid Session";
        }*/
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
            results.append("</file-names>");
            response.getWriter().write(results.toString());

        } catch (Exception e)
        {
            e.printStackTrace();
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
