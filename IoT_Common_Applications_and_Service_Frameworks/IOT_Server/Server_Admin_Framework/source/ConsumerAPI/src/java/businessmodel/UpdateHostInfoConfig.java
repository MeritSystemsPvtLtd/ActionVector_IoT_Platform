/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package businessmodel;

import Model.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author niteshc
 */
public class UpdateHostInfoConfig {

    static Logger log = Logger.getLogger(UpdateHostInfoConfig.class);

    public static boolean updateHostInfo(javax.servlet.http.HttpServletRequest request, String hostInfoConfig) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        String customername = null;
        String service = null;
        String resourceid = null;
        String resourcetype = null;
        String host = null;
        String hostgroup = null;
        String subservice = null;
        String customized_service = null;
        String resourcesubtype = null;
        String query;
        int cID = -1;
        int config_id = -1;
        int tempID = 0;
        int res = 0;
        try {
            Calendar cal = Calendar.getInstance();
            Date currentTime = cal.getTime();
            con = DatabaseConnection.getAVSAConnection(request);
            if (con != null) {
                log.error("Connection is not null before getting resource configuration");
                st = con.createStatement();
                try {
                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) parser.parse(hostInfoConfig);
                    JSONArray resConf = (JSONArray) jsonObject.get("hostinfoconfig");
                    for (int i = 0; i < resConf.size(); i++) {
                        JSONObject factObj = (JSONObject) resConf.get(i);
                        JSONObject custObj = (JSONObject) factObj.get("customer");
                        host = (String) custObj.get("host");
                        hostgroup = (String) custObj.get("hostgroup");
                        service = (String) custObj.get("service");
                        subservice = (String) custObj.get("subservice");
                        customized_service = (String) custObj.get("customized_service");
                        resourceid = (String) custObj.get("resourceid");
                        resourcetype = (String) custObj.get("resourcetype");
                        resourcesubtype = (String) custObj.get("resourcesubtype");
                        customername = (String) custObj.get("name");
                        query = "select id from customerinfo where customername='" + customername
                                + "'";
                        //  System.out.println("Query==>" + query);
                        rs = st.executeQuery(query);
                        while (rs.next()) {
                            cID = rs.getInt("id");
                        }
                        rs = null;
                        if (cID == -1) {
                            rs = st.executeQuery("select max(id) as id from customerinfo");
                            while (rs.next()) {
                                tempID = rs.getInt("id");
                            }
                            rs = null;
                            tempID++;
                            int updateCust = st.executeUpdate("insert into customerinfo values(" + tempID
                                    + ",'" + customername + "')");
                            if (updateCust != 0) {
                                return false;
                            }
                            cID = tempID;
                        }
                        rs = st.executeQuery("select config_id from ipinfo where ipaddress='" + host + "'");
                        while (rs.next()) {
                            config_id = rs.getInt("config_id");
                        }
                        rs = null;
                        if (config_id == -1) {
                            rs = st.executeQuery("select max(config_id)as config_id from ipinfo");
                            while (rs.next()) {
                                tempID = rs.getInt("config_id");
                            }
                            rs = null;
                            tempID++;
                            int updateIpInfo = st.executeUpdate("insert into ipinfo values(" + tempID
                                    + ",'" + host + "')");
                            if (updateIpInfo != 0) {
                                return false;
                            }
                            config_id = tempID;
                        }
                        // System.out.println("customername==>>" + customername);
                        //  System.out.println("cID==>>" + cID);
                        query = "insert into hostinfo (config_id,host,hostgroup,service,"
                                + "subservice,customized_service,createtime,resourceid,resourcetype,"
                                + "resourcesubtype,customerid)values(" + config_id + ",'"
                                + host + "','" + hostgroup + "','" + service + "','" + subservice + "','"
                                + customized_service + "','" + currentTime + "','" + resourceid + "','" + resourcetype + "','"
                                + resourcesubtype + "','" + cID + "')";
                        st.executeUpdate(query);
                        System.out.println("UpdateHostInfoConfig Query==>" + query);
                    }
                    if (res != 0) {
                        return true;
                    } else {
                        //System.out.println("Error while perfoming update operation in HostInfo");
                        log.error("Error while perfoming update operation in HostInfo");
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error while parsing the json");
                    // return false;
                }
            } else {
                System.out.println("Connection is null");
               // log.error("Connection is null");
                //return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while establishing connection", e);
            //return false;
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                if (st != null) {
                    st.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (customername != null) {
                    customername = null;
                }
                if (service != null) {
                    service = null;
                }
                if (resourceid != null) {
                    resourceid = null;
                }
                if (resourcetype != null) {
                    resourcetype = null;
                }
                if (host != null) {
                    host = null;
                }
                if (hostgroup != null) {
                    hostgroup = null;
                }
                if (subservice != null) {
                    subservice = null;
                }
                if (customized_service != null) {
                    customized_service = null;
                }
                if (resourcesubtype != null) {
                    resourcesubtype = null;
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error while closing connection", e);
            }
        }
        return false;
    }
}