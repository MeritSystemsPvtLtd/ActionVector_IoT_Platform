/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package AV_Action;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import java.sql.*;
import java.io.*;
import Model.*;

import org.apache.commons.fileupload.*;

/**
 *
 * @author gopal
 */
public class AV_FileUploadServlet extends AV_Model {
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AV_FileUploadServlet.class);
  public void performRequestAction(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, javax.servlet.ServletContext sc) {

      response.setContentType("text/xml;charset=UTF-8");

        String szopration = "fileupload";
        String szrespStr=null;
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
        ArrayList filelist = new ArrayList();
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List uploadedItems = null;
        FileItem fileItem = null;
        String fp = System.getProperty("file.separator");
        String path = fp + "files";
        String filePath = getServletContext().getRealPath(path);
        
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


  
        System.out.println(Taskid + user + "%%%%%%%%%%%%%%" + tasktype);
        String myFileName1 = null;
        if (checkparam)
        {


                try
                {
                    uploadedItems = upload.parseRequest(request);
                    System.out.println(uploadedItems.size() + "^^^^^^^^^^^^^");
                    log.info("Upload files size::"+uploadedItems.size());

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
                                log.info("FILESTORING LOCATION=====>" + filePath);
                                File filedir = new File(fileoathtostore, Taskid);
                                filedir.mkdirs();
                                myFileName1 = fileItem.getName();
                                System.out.println(myFileName1);
                                
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
                                System.out.println(myFileName1 + "******************");
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
                    AV_Constants.opStatus=AV_Constants.FAILURE;
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
  
                        con = DatabaseConnection.getAVSAConnection(request);
                        log.info("connection got successfully");
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
                                    log.info("insert into gafileupload VALUES('" + Taskid + "','" + targetEndpoint + "','" + user + "','" + filename + "')");
                                }
                            } catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }
                        }

                    }

                    AV_Constants.opStatus=AV_Constants.SUCCESS;


                } catch (Exception ex)
                {
                    ex.printStackTrace();
                    filelist = null;
                    AV_Constants.opStatus=AV_Constants.FAILURE;
                } finally
                {
                    try
                    {
                        
                        if (con != null)
                        {
                            con.close();
                        }
                        if (stat != null)
                        {
                            stat.close();
                        }
                        filelist = null;
                    
                    } catch (SQLException ex)
                    {
                        Logger.getLogger(AV_FileUploadServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

        
        } else
        {
           
            AV_Constants.opStatus=AV_Constants.INVALID_INPUTS;
            filelist = null;
        }

        try
        {
            szrespStr=AV_OutputFormat.formatOutPut(AV_Constants.opStatus,szopration,sc);
          
            response.getWriter().write(szrespStr);

        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                szrespStr = null;
                filelist = null;
                szopration=null;

            } catch (Exception e)
            {
                e.printStackTrace();
                filelist = null;
            }
        }
  }
}
