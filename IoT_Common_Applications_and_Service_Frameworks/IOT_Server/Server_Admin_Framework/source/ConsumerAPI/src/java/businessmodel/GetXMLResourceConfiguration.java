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
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 *
 * @author gopal Created on Sep 5, 2012, 11:43:09 AM
 */
public class GetXMLResourceConfiguration {

    static Logger log = Logger.getLogger(GetHostServices.class);

    public static String getResourecConfig(javax.servlet.http.HttpServletRequest request) {
        Connection con = null;
        StringBuffer sbr = new StringBuffer("");

        try {
            con = DatabaseConnection.getAVSAConnection(request);
            if (con != null) {
                log.info("Connection is not null before getting resource configuration");
                Statement st = con.createStatement();
                ResultSet rs = null;
                String customer = null;
                String service = null;
                String subservice = null;
                String resourceid = null;
                String paramname = null;
                String paramvalue = null;
                String mapKey = null;
                try {
                    rs = st.executeQuery("select customername as customer,service,subservice,resourceid,paramname,"
                            + "paramvalue from resourceconfig, customerinfo "
                            + "where customerid=id");
                    while (rs.next()) {
                        customer = rs.getString("customer");
                        service = rs.getString("service");
                        subservice = rs.getString("subservice");
                        resourceid = rs.getString("resourceid");
                        paramname = rs.getString("paramname");
                        paramvalue = rs.getString("paramvalue");

                        sbr.append("<customer>");
                        sbr.append("<name>");
                        sbr.append(customer);
                        sbr.append("</name>");
                        sbr.append("<service>");
                        sbr.append(service);
                        sbr.append("</service>");
                        sbr.append("<subservice>");
                        sbr.append(subservice);
                        sbr.append("</subservice>");
                        sbr.append("<resourceid>");
                        sbr.append(resourceid);
                        sbr.append("</resourceid>");
                        sbr.append("<paramname>");
                        sbr.append(paramname);
                        sbr.append("</paramname>");
                        sbr.append("<paramvalue>");
                        sbr.append(paramvalue);
                        sbr.append("</paramvalue>");
                        sbr.append("</customer>");
                    }
                    rs.close();
                    st.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                return null;
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("error while doing operations ", e);
            return null;
        } finally {
            try {
                if (con != null) {
                    con.close();

                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("error while closing connection ", e);

            }
        }
        return sbr.toString();
    }
}
