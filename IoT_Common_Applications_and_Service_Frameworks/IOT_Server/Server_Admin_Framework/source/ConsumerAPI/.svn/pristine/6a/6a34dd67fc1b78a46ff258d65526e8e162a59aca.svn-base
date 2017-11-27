/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.logging.Level;

import java.io.*;
import javax.servlet.*;
//import javax.servlet.http.*;
import org.apache.log4j.Logger;
import java.io.IOException;
//import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author gopal
 */
public class DownloadFiles extends HttpServlet
{
    public static Logger log=Logger.getLogger(DownloadFiles.class);
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        //response.setContentType("text/html;charset=UTF-8");
        ServletOutputStream stream = null;
        BufferedInputStream buf = null;
        String Filepath = "";
        String Filename = null;
        String Tasktype=null;
        String Taskid=null;
        FileInputStream input = null;
        String concatFileName = "";
        try
        {
            String fp = System.getProperty("file.separator");
            //String Filepath1=request.getParameter("path");
            //String [] fileslocation=Filepath1.split(",");

            //for(int i=0;i<fileslocation.length;i++){
            //  Filepath=Filepath+fp+fileslocation[i];
            //}
            Tasktype=request.getParameter("tasktype");
            Taskid=request.getParameter("taskid");
            Filename = request.getParameter("filename");
            System.out.println(Filepath + "$$$$$$" + Filename);

            Filepath=Filepath+fp+request.getServerName()+fp+Tasktype+fp+Taskid;
            String pdfDir = getServletContext().getRealPath(fp);
            System.out.println(getServletContext().getRealPath(fp));
            pdfDir = pdfDir + "files" + Filepath + fp + Filename;
            System.out.println("pdfDir::::::::::::::" + pdfDir);
           
            stream = response.getOutputStream();
            File pdf = null;
            pdf = new File(pdfDir);
            response.setContentType("text/xml;charset=UTF-8");
            response.addHeader("Content-Disposition", "attachment; filename=" + Filename);
            response.setContentLength((int) pdf.length());
            input = new FileInputStream(pdf);
            buf = new BufferedInputStream(input);
            int readBytes = 0;

            while ((readBytes = buf.read()) != -1)
            {
                stream.write(readBytes);
            }

        } catch (Exception e)
        {
            e.printStackTrace();

        } finally
        {
            try
            {
                stream.close();
                buf.close();
                input.close();

            } catch (IOException ex)
            {
                java.util.logging.Logger.getLogger(DownloadFiles.class.getName()).log(Level.SEVERE, null, ex);
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
