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
import java.util.*;
import java.sql.*;
import org.apache.log4j.Logger;

/**
 *
 * @author gopal Created on Sep 11, 2012, 10:54:09 AM
 */
public class UpdateHostInfo {

    static Logger log = Logger.getLogger(UpdateHostInfo.class);

    public static boolean updateHosts(javax.servlet.http.HttpServletRequest request, String szHostXml) {
        Connection con = null;
        PreparedStatement ps1 = null;
        ResultSet rs = null;
        Statement st = null;
        boolean fupdate = false;
        boolean fhostList = false;
        String szIp = null;
        String szService = null;
        ArrayList<HostBean> HostList = new ArrayList<HostBean>();
        HostBean hbean = null;
        try {
            HostList = XMLParsing.parseXML(szHostXml);
            if (HostList != null) {
                fhostList = true;
            }
            if (fhostList) {
                con = DatabaseConnection.getAVSAConnection(request);
                if (con != null) {
                    log.info("reading the data from HostList");
                    Iterator it = HostList.iterator();
                    while (it.hasNext()) {
                        hbean = new HostBean();
                        st = con.createStatement();
                        hbean = (HostBean) it.next();
                        szService = hbean.getService();
                        szIp = hbean.getIp();

                        rs = st.executeQuery("select * from hostinfo where host='" + szIp + "' and service='" + szService + "'");
                        if (!rs.next()) {
                            log.info("If data is not present in hostinfo then inserting into it");
                            int imaxId = 0;
                            Statement st1 = con.createStatement();
                            ResultSet rs1 = st1.executeQuery("select max(config_id) from hostinfo");
                            while (rs1.next()) {
                                imaxId = rs1.getInt(1);
                            }
                            rs1.close();
                            st1.close();
                            ps1 = con.prepareStatement("insert into hostinfo(config_id,host,service,subservice,customized_service)values(?,?,?,?,?)");
                            ps1.setInt(1, imaxId + 1);
                            ps1.setString(2, hbean.getIp());
                            ps1.setString(3, hbean.getService());
                            ps1.setString(4, hbean.getSubService());
                            ps1.setString(5, hbean.getCustomizedService());
                            ps1.executeUpdate();

                        } else {
                            ps1 = con.prepareStatement("update hostinfo set subservice=?, customized_service=? where host=? and service=?");
                            ps1.setString(1, hbean.getSubService());
                            ps1.setString(2, hbean.getCustomizedService());
                            ps1.setString(3, hbean.getIp());
                            ps1.setString(4, hbean.getService());
                            ps1.executeUpdate();
                        }
                        ps1 = null;
                        rs = null;
                        st = null;
                        hbean = null;
                    }
                    fupdate = true;
                    HostList = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps1 != null) {
                    ps1.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                    log.info("Connection closed successfully");
                }

            } catch (Exception e) {
                log.error("error while closing connection::", e);
            }
        }
        return fupdate;
    }
}