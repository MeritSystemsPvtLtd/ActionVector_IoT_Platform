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
public class GetJsonHostInfo {

    static Logger log = Logger.getLogger(GetJsonHostInfo.class);

    public static String getHostInfo(javax.servlet.http.HttpServletRequest request) {
        Connection con = null;
        ResultSet rs = null;
        Statement st = null;
        String customer = null;
        String service = null;
        String resourceid = null;
        String resourcetype = null;
        String hostgroup = null;
        String subservice = null;
        String customized_service = null;
        String resourcesubtype = null;
        String host = null;
        StringBuffer sbr = new StringBuffer("");
        try {
            con = DatabaseConnection.getAVSAConnection(request);
            if (con != null) {
                log.info("Connection is not null before getting resource configuration");
                st = con.createStatement();
                try {
                    boolean recFound = false;
                    String query = "select ci.customername as customer,hi.service,hi.resourceid,hi.resourcetype,"
                            + "hi.hostgroup,hi.host,hi.subservice,hi.customized_service,hi.resourcesubtype from hostinfo hi inner join customerinfo ci "
                            + "on hi.customerid = ci.id";
                    rs = st.executeQuery(query);
                    //  System.out.println("Query" + query);
                    while (rs.next()) {
                        recFound = true;
                        customer = rs.getString("customer");
                        service = rs.getString("service");
                        resourceid = rs.getString("resourceid");
                        resourcetype = rs.getString("resourcetype");
                        hostgroup = rs.getString("hostgroup");
                        subservice = rs.getString("subservice");
                        customized_service = rs.getString("customized_service");
                        resourcesubtype = rs.getString("resourcesubtype");
                        host = rs.getString("host");
                        sbr.append("\"customer\":{ \"name\":\"" + customer + "\",\"service\":\"" + service + "\","
                                + "\"resourceid\":\"" + resourceid + "\",\"resourcetype\":\"" + resourcetype + "\","
                                + "\"hostgroup\":\"" + hostgroup + "\",\"subservice\":\"" + subservice + "\","
                                + "\"customized_service\":\"" + customized_service + "\","
                                + "\"resourcesubtype\":\"" + resourcesubtype + "\",\" host\":\"" + host + "\"},");
                    }
                    if (recFound) {
                        sbr.deleteCharAt(sbr.length() - 1);
                    }
                    //  System.out.println("List of Host==>" + sbr);
                    if (!recFound) {
                        sbr.append("null");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error while fetching hostInfo in GetJsonHostInfo ", e);
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
                customer = null;
                service = null;
                resourceid = null;
                resourcetype = null;
                hostgroup = null;
                subservice = null;
                customized_service = null;
                resourcesubtype = null;
                host = null;
                sbr = null;
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error while closing connection ", e);
            }
        }

    }
}