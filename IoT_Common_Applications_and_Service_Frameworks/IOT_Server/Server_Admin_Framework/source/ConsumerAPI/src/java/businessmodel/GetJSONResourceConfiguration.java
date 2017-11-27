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
import java.sql.*;
import org.apache.log4j.Logger;
import java.util.HashMap;

/**
 *
 * @author niteshc
 */
public class GetJSONResourceConfiguration {

    static Logger log = Logger.getLogger(GetJSONResourceConfiguration.class);
    static final String All = "All";

    public static String getResourceConfig(javax.servlet.http.HttpServletRequest request, String customer, String service, String subservice, String resource,
            String paramname) {

        Connection con = null;
        ResultSet rs = null;
        //String paramname = null;
        String paramvalue = null;
        String paramunit = null;
        Statement st = null;
        StringBuffer sbr = new StringBuffer("");
        String serviceQuery = " AND service = 'null'";
        String subserviceQuery = " AND subservice = 'null'";
        String resourceQuery = " AND resourceid = 'null'";
        String paramQuery = " AND paramname = '" + paramname + "'";
        String query = null;
        try {
            con = DatabaseConnection.getAVSAConnection(request);
            if (con != null) {
                //log.info("Connection is not null before getting resource configuration");
                st = con.createStatement();
                try {
                    if (service != null) {
                        if (!(service.equals("")) && !(service.equals(All))) {
                            serviceQuery = " AND service ='" + service + "'";
                        }
                    }
                    if (subservice != null) {
                        if (!(subservice.equals("")) && !(subservice.equals(All))) {
                            subserviceQuery = " AND subservice ='" + subservice + "'";
                        }
                    }
                    if (resource != null) {
                        if (!(resource.equals("")) && !(resource.equals(All))) {
                            resourceQuery = " AND resourceid ='" + resource + "'";
                        }
                    }
                    boolean recFound = false;
                    query = "select paramname,paramvalue,paramunit from resourceconfig "
                            + "where customerid in (select id from customerinfo where customername='"
                            + customer + "')" + serviceQuery + subserviceQuery + resourceQuery;
                    if (paramname != null) {
                        query += paramQuery;
                    }
                    query += " order by serialno";
                    //System.out.println("GetJSONResourceConfig Query===>" + query);
                    rs = st.executeQuery(query);
                    while (rs.next()) {
                        recFound = true;
                        paramname = rs.getString("paramname");
                        paramvalue = rs.getString("paramvalue");
                        paramunit = rs.getString("paramunit");
                        /*sbr.append("{\"ResConfig\":{\"ParamName\":\"" + paramname + "\",\"ParamValue\":\"" + paramvalue + "\",\"ParamUnit\":\"" 
                         + paramunit + "\"}},");*/
                        sbr.append("{\"ParamName\":\"" + paramname + "\",\"ParamValue\":\"" + paramvalue + "\",\"ParamUnit\":\""
                                + paramunit + "\"},");
                    }
                    if (recFound) {
                        sbr.deleteCharAt(sbr.length() - 1);
                    }
                    //System.out.println("List Of ParamValues==>" + sbr);
                    //    if (!recFound) {
                    //       sbr.append("null");
                    //  }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error while fetching paramvalues in GetJSONResourceConfiguration ", e);
                }
            } else {
                return null;
            }
            return sbr.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while establishing connection", e);
            return null;
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                con = null;
                if (rs != null) {
                    rs.close();
                }
                rs = null;
                if (st != null) {
                    st.close();
                }
                st = null;
                paramname = null;
                paramvalue = null;
                paramunit = null;
                sbr = null;
                serviceQuery = null;
                subserviceQuery = null;
                resourceQuery = null;
                paramQuery = null;
                query = null;
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error while closing connection ", e);
            }
        }

    }
}
