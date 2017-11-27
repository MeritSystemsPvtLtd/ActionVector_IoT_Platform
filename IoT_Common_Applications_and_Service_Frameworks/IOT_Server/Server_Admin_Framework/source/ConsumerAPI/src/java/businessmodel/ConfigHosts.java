/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * *************************************************************************
 *
 * Software Developed by Merit Systems Pvt. Ltd., #55/C-42/1, Nandi Mansion, Ist
 * Floor 40th Cross, Jayanagar 8th Block Bangalore - 560 070, India Work Created
 * for Merit Systems Private Limited All rights reserved
 *
 * THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES
 * NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED, DISTRIBUTED,
 * REVISED, MODIFIED,TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED,
 * COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD
 * SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 *
 *
 **************************************************************************
 */
package businessmodel;

import Model.*;
import controller.*;
import java.sql.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 *
 * @author gopal Created on Sep 4, 2012, 11:47:47 AM
 */
public class ConfigHosts {

    static Logger log = Logger.getLogger(ConfigHosts.class);

    public static boolean configureHosts(HashMap Clusters, HttpServletRequest request) {
        Connection con = null;
        boolean fconfig = false;
        ResultSet rs = null;
        PreparedStatement ps1 = null;
        Statement st1 = null;
        String szTempHost = null;
        String szSql = "";
        int count = 0;
        boolean faddHostInfo = false;
        String szTempHosts[] = null;
        try {
            con = DatabaseConnection.getAVSAConnection(request);
            if (con != null) {
                Collection al = Clusters.values();
                /*List al1 = new ArrayList(al);*/
                Iterator it = Clusters.entrySet().iterator();
                System.out.println("Clusters in configureHosts::" + Clusters);
                while (it.hasNext()) {
                    //szTempHost = it.next().toString();
                    Map.Entry me = (Map.Entry) it.next();
                    szTempHosts = me.getValue().toString().split(",");
                    for (int i = 0; i < szTempHosts.length; i++) {
                        szTempHost = szTempHosts[i];
                        ps1 = con.prepareStatement("select * from " + DBConstants.IPINFO_TABLE + " where ipaddress=?");
                        ps1.setString(1, szTempHost);
                        rs = ps1.executeQuery();
                        if (!rs.next()) {
                            szSql = "select max(config_id) from " + DBConstants.IPINFO_TABLE;
                            st1 = con.createStatement();
                            ResultSet rs2 = st1.executeQuery(szSql);
                            while (rs2.next()) {
                                count = rs2.getInt(1);
                                count += 1;
                            }
                            st1.close();
                            st1 = null;
                            rs2.close();
                            rs2= null;
                            PreparedStatement ps = con.prepareStatement("insert into " + DBConstants.IPINFO_TABLE + " (config_id,ipaddress) values (?,?)");
                            ps.setInt(1, count);
                            ps.setString(2, szTempHost);
                            ps.executeUpdate();
                            ps.close();
                            ps = null;
                            count = 0;
                        }
                        szTempHost = null;
                        ps1.close();
                        ps1 = null;
                        rs.close();
                        rs = null;
                    }
                    me = null;
                    szTempHosts = null;
                }
                log.info("Going to Add data into hostinfo table ");
                System.out.println("Going to Add data into hostinfo table ");
                faddHostInfo = addIntoHostInfo(con, Clusters);
                log.info("after updating the hostinfo");
                System.out.println("after updating the hostinfo");
                fconfig = true;
            } else {
                fconfig = false;
                System.out.println("Invalid connection");
                log.error("Invalid connection");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();

            log.error("Exception while processing the database operation", e);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                con = null;
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error while closing connection");
            }
        }
        if (fconfig && faddHostInfo) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean addIntoHostInfo(java.sql.Connection con, java.util.HashMap Clusters) {
        ResultSet rs = null;
        ResultSet rs1 = null;
        PreparedStatement ps = null;
        PreparedStatement ps1 = null;
        Statement st1 = null;
        Statement st = null;
        int iTempid = 0;
        String szTempService = null;
        String szTempHost = null;
        String szTempHost1 = null;
        String aHosts[] = null;
        //int icount=0;
        boolean fHostAdd = false;
        try {
            Iterator it = Clusters.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                szTempService = (String) me.getKey();
                szTempHost1 = (String) me.getValue();
                aHosts = szTempHost1.split(",");
                for (int i = 0; i < aHosts.length; i++) {
                    szTempHost = aHosts[i];
                    st = con.createStatement();
                    rs = st.executeQuery("select config_id from " + DBConstants.IPINFO_TABLE + " where ipaddress='" + szTempHost + "'");
                    while (rs.next()) {
                        iTempid = rs.getInt("config_id");
                    }
                    System.out.println("TempService::" + szTempService + " Host::" + szTempHost + " ConfigId::" + iTempid);

                    log.info("TempService::" + szTempService + " Host::" + szTempHost + " ConfigId::" + iTempid);
                    st.close();
                    rs.close();
                    st1 = con.createStatement();
                    rs1 = st1.executeQuery("select * from " + DBConstants.HOSTINFO_TABLE + " where host='" + szTempHost + "' and service='" + szTempService + "'");
                    if (!rs1.next()) {
                        ps = con.prepareStatement("insert into " + DBConstants.HOSTINFO_TABLE + " (config_id,host,service) values(?,?,?)");
                        ps.setInt(1, iTempid);
                        ps.setString(2, szTempHost);
                        ps.setString(3, szTempService);
                        ps.executeUpdate();

                    }/*else
                     {
                     ps1 = con.prepareStatement("update " + DBConstants.HOSTINFO_TABLE + " set host=? and service=?");
                     ps1.setString(1, szTempHost);
                     ps1.setString(2, szTempService);
                     }*/
                    if (ps != null) {
                        ps.close();
                    }
                    rs1.close();
                    st1.close();
                    szTempHost = null;
                    iTempid = 0;
                }
                szTempService = null;
            }
            fHostAdd = true;
        } catch (Exception e) {
            log.error("error while storing data into hostinfo", e);
            e.printStackTrace();
        }
        return fHostAdd;
    }

    public static boolean configureResource(HttpServletRequest request, String szresourceType, String szhostName) {
        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement pst = null;
        Statement st1 = null;
        Statement st = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        boolean fconfig = false;
        int iMaxId = 0;
        try {
            con = DatabaseConnection.getAVSAConnection(request);
            if (con != null) {
                st1 = con.createStatement();
                rs = st1.executeQuery("select max(config_id) from " + DBConstants.IPINFO_TABLE);
                while (rs.next()) {
                    iMaxId = rs.getInt(1) + 1;
                }
                st1.close();
                st1 = null;
                rs.close();
                rs = null;
                pst = con.prepareStatement("insert into " + DBConstants.IPINFO_TABLE + " (config_id,ipaddress) values(?,?)");
                pst.setInt(1, iMaxId);
                pst.setString(2, szhostName);
                pst.execute();
                pst.close();
                pst = null;
                //Connection con=null;
                st = con.createStatement();
                rs1 = st.executeQuery("select * from " + DBConstants.HOSTINFO_TABLE + " where host='" + szhostName + "' and service='" + szresourceType + "'");
                if (!rs1.next()) {
                    ps = con.prepareStatement("insert into " + DBConstants.HOSTINFO_TABLE + " (config_id,host,resourcetype) values(?,?,?)");
                    ps.setInt(1, iMaxId);
                    ps.setString(2, szhostName);
                    ps.setString(3, szresourceType);
                    ps.executeUpdate();
                }
                ps.close();
                rs1.close();
                ps = null;
                rs1 = null;
                log.info("Successfully configured Resource types");
                fconfig = true;
            } else {
                log.info("Unable to connect the database");
                return fconfig;
            }
            con.close();
            con = null;
        } catch (Exception e) {
            log.error("Unable to configure the Resource", e);
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (con != null) {
                    con.close();
                    con = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return fconfig;
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
