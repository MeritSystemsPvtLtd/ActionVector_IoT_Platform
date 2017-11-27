/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import java.sql.*;
import java.io.*;
import Model.*;
import org.apache.commons.fileupload.*;
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
public class FileUploadServlet extends HttpServlet
{

    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FileUploadServlet.class);

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {

        response.setContentType("text/xml;charset=UTF-8");

        StringBuffer results = new StringBuffer("<fileupload>");

        String opStatus = "";
        String opDescription = "";
        String customerid = "";
        boolean insertinto = true;
        Connection con = null;
        Statement stat = null;
        ResultSet rs = null;
        String Taskid = null;
        String user = null;
        boolean checkparam = false;
        String targetEndpoint = null;
        String tasktype = null;
        String szSessionid = "";
        boolean checksession = false;
        //DatabaseConnection dbconnection = null;
        ArrayList filelist = new ArrayList();

        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);


        //upload.setProgressListener(listener);
        //HttpSession session = request.getSession(false);
        //List namelist=new ArrayList();
        List uploadedItems = null;
        FileItem fileItem = null;
        String fp = System.getProperty("file.separator");
        String path = fp + "files";
        String filePath = getServletContext().getRealPath(path);
        System.out.println(filePath + "^^^^^^^^^^^^^^^^^^^^^^^");


        try
        {
            Taskid = request.getParameter("taskid");
            tasktype = request.getParameter("tasktype");
            user = request.getParameter("username");
            szSessionid = request.getParameter("szsessionid");

            if ((Taskid != null && !Taskid.equalsIgnoreCase("")) && (tasktype != null && !tasktype.equalsIgnoreCase("")) && (user != null && !user.equalsIgnoreCase("")))
            {
                checkparam = true;
            } else
            {
                checkparam = false;
            }



        } catch (Exception e)
        {
            e.printStackTrace();
        }


        // user = (String) session.getAttribute("szUserName");
        System.out.println(Taskid + user + "%%%%%%%%%%%%%%" + tasktype);
        String myFileName1 = null;
        if (checkparam)
        {

            /*checksession = CheckSession.checkSession(request, szSessionid);
            if (checksession)
            {*/
                try
                {
                    uploadedItems = upload.parseRequest(request);
                    System.out.println(uploadedItems.size() + "^^^^^^^^^^^^^");
                    //int n=uploadedItems.size();
                    Iterator i = uploadedItems.iterator();
                    String szserverName=filePath + fp +request.getServerName();
                    String fileoathtostore = filePath + fp +request.getServerName()+fp +tasktype;

                    while (i.hasNext())
                    {
                        //fileItem = (FileItem) uploadedItems.get(n-1);
                        fileItem = (FileItem) i.next();
                        System.out.println(fileItem);
                        if (fileItem.isFormField() == false)
                        {
                            if (fileItem.getSize() > 0)
                            {
                                File uploadedFile = null;
                                String myFullFileName = fileItem.getName(), myFileName = "", slashType =
                                        (myFullFileName.lastIndexOf("\\") > 0) ? "\\" : "/";
                                System.out.println(fileItem.getName() + "########################");
                                int startIndex = myFullFileName.lastIndexOf(slashType);
                                myFileName = myFullFileName.substring(startIndex + 1, myFullFileName.length());
                                
                                File userServerFile=new File(filePath,request.getServerName());
                                userServerFile.mkdirs();
                                File Tasktypedir = new File(szserverName, tasktype);

                                Tasktypedir.mkdirs();


                                System.out.println("FILESTORING LOCATION=====>" + filePath);

                                File filedir = new File(fileoathtostore, Taskid);
                                filedir.mkdirs();
                                myFileName1 = fileItem.getName();
                                System.out.println(myFileName1);
                                // uploadedFile = new File(filedir, myFileName);
                                if (new File(filedir, myFileName1).exists())
                                {
                                    for (int j = 1; j < j + 1; j++)
                                    {
                                        myFileName1 = myFileName + "(" + j + ")";
                                        if (new File(filedir, myFileName1).exists())
                                        {
                                        } else
                                        {

                                            break;
                                        }
                                    }
                                }

                                uploadedFile = new File(filedir, myFileName1);

                                //uploadedFile.exists();
                                System.out.println(myFileName1 + "******************");
                                // filename=Taskid+fp+myFileName1;
                                fileItem.write(uploadedFile);

                                filelist.add(myFileName1);
                            }
                        }

                    }


                    targetEndpoint = tasktype + "," + Taskid;
                } catch (FileUploadException e)
                {
                    insertinto = false;
                    filelist = null;
                    e.printStackTrace();
                } catch (Exception e)
                {
                    insertinto = false;
                    filelist = null;

                    e.printStackTrace();
                }
                try
                {
                    System.out.println("coming into single file upload to database");
                    int filelistsize = filelist.size();
                    if (insertinto == true && filelistsize > 0)
                    {
                        //dbconnection= new DatabaseConnection ();
                        con = DatabaseConnection.getAVSAConnection(request);

                        stat = con.createStatement();
                        String filename = null;

                        for (int z = 0; z < filelistsize; z++)
                        {
                            try
                            {
                                filename = (String) filelist.get(z);
                                if (targetEndpoint != null && filename != null)
                                {
                                    stat.executeUpdate("insert into gafileupload VALUES('" + Taskid + "','" + targetEndpoint + "','" + user + "','" + filename + "')");
                                    System.out.println("insert into gafileupload VALUES('" + Taskid + "','" + targetEndpoint + "','" + user + "','" + filename + "')");
                                }
                            } catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }
                        }

                    }

                    opStatus = "0";
                    opDescription = "operation success";


                } catch (Exception ex)
                {
                    ex.printStackTrace();
                    filelist = null;

                } finally
                {
                    try
                    {
                        //dbconnection = null;
                        if (con != null)
                        {
                            con.close();
                        }
                        if (stat != null)
                        {
                            stat.close();
                        }
                        filelist = null;
                    /*RequestDispatcher view = request.getRequestDispatcher("fileuploadconfirm.jsp");
                    try
                    {
                    view.forward(request, response);
                    } catch (ServletException ex)
                    {
                    Logger.getLogger(FileUploadServlet.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex)
                    {
                    Logger.getLogger(FileUploadServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }*/
                    } catch (SQLException ex)
                    {
                        Logger.getLogger(FileUploadServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            /*} else
            {
            opStatus = "102";
            opDescription = "Invalid Session";
            }*/
        } else
        {
            opStatus = "108";
            opDescription = "Invalid Inputs";
            filelist = null;
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
            results.append("</fileupload>");
            response.getWriter().write(results.toString());

        } catch (Exception e)
        {
            e.printStackTrace();
            log.debug("exception while writing output");
        } finally
        {
            try
            {
                results = null;
                filelist = null;


            } catch (Exception e)
            {
                e.printStackTrace();
                filelist = null;
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
