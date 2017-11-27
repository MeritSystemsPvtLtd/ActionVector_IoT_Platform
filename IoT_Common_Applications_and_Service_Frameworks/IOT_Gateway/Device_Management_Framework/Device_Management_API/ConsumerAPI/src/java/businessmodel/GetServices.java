/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package businessmodel;

import Model.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.log4j.Logger;

/**
 *
 * @author niteshc
 */
public class GetServices {

    static Logger log = Logger.getLogger(GetServices.class);

    public static String getInstallationsInfo(javax.servlet.http.HttpServletRequest request, String customer) {

        Connection con = null;
        ResultSet rs = null;
        Statement st = null;
        String service = null;
        StringBuffer sbr = new StringBuffer("");
        try {
            con = DatabaseConnection.getAVSAConnection(request);
            if (con != null) {
                log.info("Connection is not null before getting resource configuration");
                st = con.createStatement();
                try {
                    boolean recFound = false;
                    String query = "select distinct service from hostinfo where customerid in (select id from"
                            + " customerinfo where customername='" + customer + "')";
                    //  System.out.println("Query===>" + query);
                    rs = st.executeQuery(query);
                    while (rs.next()) {
                        recFound = true;
                        service = rs.getString("service");
                        sbr.append("{\"Service\":\"" + service + "\"},");
                    }
                    rs.close();
                    rs = null;
                    st.close();
                    st = null;
                    if (recFound) {
                        sbr.deleteCharAt(sbr.length() - 1);
                    }
                    //System.out.println("Services ====>" + sbr);
                    if (!recFound) {
                        sbr.append("null");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error while fetching Services list in GetServices ", e);
                }
            } else {
                return null;
            }
            return sbr.toString();
        } catch (Exception e) {

            e.printStackTrace();
            log.error("Error while establishing connection ", e);
            return null;
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (service != null) {
                    service = null;
                }
                if (sbr != null) {
                    sbr = null;
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error while closing connection ", e);
            }
        }

    }
}