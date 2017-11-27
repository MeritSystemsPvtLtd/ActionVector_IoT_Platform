/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/***************************************************************************
 *
 *                            Software Developed by
 *                           Merit Systems Pvt. Ltd.,
 *     #55/C-42/1, Nandi Mansion, Ist Floor 40th Cross, Jayanagar 8th Block
 *                          Bangalore - 560 070, India
 *               Work Created for Merit Systems Private Limited
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
package businessmodel;

import Model.*;
import java.sql.*;

import org.apache.log4j.Logger;

/**
 *
 * @author gopal
 * Created on Sep 5, 2012, 11:43:09 AM
 */
public class GetHostServices
{

    static Logger log = Logger.getLogger(GetHostServices.class);

    public static String getAllHostServices(javax.servlet.http.HttpServletRequest request)
    {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        StringBuffer sbr = new StringBuffer("");
        try
        {
            con = DatabaseConnection.getAVSAConnection(request);
            if (con != null)
            {
                log.info("Connection is not null before getting hosts");
               
                String szTempHost1 = "";
                String szTempService1 = "";

               
                System.out.println("Before geting subservices");
                Statement st2 = con.createStatement();

                ResultSet rs2 = null;
                try
                {
                    //rs2 = st2.executeQuery("select subservice,customized_service from hostinfo where host='" + szTempHost1 + "' and service='" + szTempService1 + "'");
                    rs2 = st2.executeQuery("select host,service,subservice,customized_service,resourcetype,resourcesubtype,resourceid,hostgroup from hostinfo");

                } catch (Exception e)
                {
                    e.printStackTrace();
                }

                while (rs2.next())
                {
                    
                    sbr.append("<service>");
                    sbr.append("<ip>");
                    sbr.append(rs2.getString(1));
                    sbr.append("</ip>");
                    if (rs2.getString(2) == null || rs2.getString(2).equalsIgnoreCase("") || rs2.getString(2).equalsIgnoreCase("none"))
                    {
                        sbr.append("<name>");
                        sbr.append("Undefined");
                       // System.out.println("service::" + "Undefined");
                        sbr.append("</name>");
                    } else
                    {
                        sbr.append("<name>");
                        sbr.append(rs2.getString(2));
                      //  System.out.println("Service::" + szTempService1);
                        sbr.append("</name>");
                    }

                    if (rs2.getString(3) == null || rs2.getString(3).equalsIgnoreCase("") || rs2.getString(3).equalsIgnoreCase("none"))
                    {
                        sbr.append("<subservice>");
                        sbr.append("Undefined");
                       // System.out.println("subservice::" + "Undefined");
                        sbr.append("</subservice>");
                    } else
                    {
                        sbr.append("<subservice>");
                        sbr.append(rs2.getString(3));
                      //  System.out.println("subservice::" + rs2.getString(3));
                        sbr.append("</subservice>");
                    }
                    if (rs2.getString(4) == null || rs2.getString(4).equalsIgnoreCase("") || rs2.getString(4).equalsIgnoreCase("none"))
                    {
                        sbr.append("<customizedservice>");
                        sbr.append("Undefined");
                      //  System.out.println("customized_service::" + "Undefined");
                        sbr.append("</customizedservice>");
                    } else
                    {
                        sbr.append("<customizedservice>");
                        sbr.append(rs2.getString(4));
                       // System.out.println("customized_service::" + rs2.getString(4));
                        sbr.append("</customizedservice>");
                    }

                     if (rs2.getString(5) == null || rs2.getString(5).equalsIgnoreCase("") || rs2.getString(5).equalsIgnoreCase("none"))
                    {
                        sbr.append("<resourcetype>");
                        sbr.append("Undefined");
                      //  System.out.println("customized_service::" + "Undefined");
                        sbr.append("</resourcetype>");
                    } else
                    {
                        sbr.append("<resourcetype>");
                        sbr.append(rs2.getString(5));
                       // System.out.println("customized_service::" + rs2.getString(4));
                        sbr.append("</resourcetype>");
                    }

                    if (rs2.getString(6) == null || rs2.getString(6).equalsIgnoreCase("") || rs2.getString(6).equalsIgnoreCase("none"))
                    {
                        sbr.append("<resourcesubtype>");
                        sbr.append("Undefined");
                      //  System.out.println("customized_service::" + "Undefined");
                        sbr.append("</resourcesubtype>");
                    } else
                    {
                        sbr.append("<resourcesubtype>");
                        sbr.append(rs2.getString(6));
                       // System.out.println("customized_service::" + rs2.getString(4));
                        sbr.append("</resourcesubtype>");
                    }


                    if (rs2.getString(7) == null || rs2.getString(7).equalsIgnoreCase("") || rs2.getString(7).equalsIgnoreCase("none"))
                    {
                        sbr.append("<resourceid>");
                        sbr.append("Undefined");
                      //  System.out.println("customized_service::" + "Undefined");
                        sbr.append("</resourceid>");
                    } else
                    {
                        sbr.append("<resourceid>");
                        sbr.append(rs2.getString(7));
                       // System.out.println("customized_service::" + rs2.getString(4));
                        sbr.append("</resourceid>");
                    }

                    if (rs2.getString(8) == null || rs2.getString(8).equalsIgnoreCase("") || rs2.getString(8).equalsIgnoreCase("none"))
                    {
                        sbr.append("<hostgroup>");
                        sbr.append("Undefined");
                      //  System.out.println("customized_service::" + "Undefined");
                        sbr.append("</hostgroup>");
                    } else
                    {
                        sbr.append("<hostgroup>");
                        sbr.append(rs2.getString(8));
                       // System.out.println("customized_service::" + rs2.getString(4));
                        sbr.append("</hostgroup>");
                    }


                    sbr.append("</service>");
                    // sbr.append("</host>");
                    szTempHost1 = "";
                    szTempService1 = "";
                }
                rs2.close();
                
                log.info("final xml::" + sbr.toString());


            } else
            {
                return null;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            log.error("error while doing operations ", e);
            return null;
        } finally
        {
            try
            {
                if (con != null)
                {
                    con.close();

                }
            } catch (Exception e)
            {
                e.printStackTrace();
                log.error("error while closing connection ", e);

            }
        }
        //if(sbr.toString()!=null&&!sbr.toString().equalsIgnoreCase(""))
        return sbr.toString();

    }
}
