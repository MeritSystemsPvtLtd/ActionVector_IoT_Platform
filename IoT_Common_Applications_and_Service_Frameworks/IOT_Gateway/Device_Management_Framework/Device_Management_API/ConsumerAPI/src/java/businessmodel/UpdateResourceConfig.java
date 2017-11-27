/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package businessmodel;

import Model.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author niteshc
 */
public class UpdateResourceConfig {

    static Logger log = Logger.getLogger(UpdateResourceConfig.class);

    public static boolean updateResourceConfig(javax.servlet.http.HttpServletRequest request, String updateJson) throws SQLException {

        Connection con = null;
        Statement st = null;
        String customername = null;
        String service = null;
        String resourceid = null;
        String name = null;
        String value = null;
        int res = 0;
        String szSerQuery;
        String szResQuery;
        try {
            System.out.println(" UpdateResourceConfig.updateResourceConfig");
            con = DatabaseConnection.getAVSAConnection(request);
            if (con != null) {
                System.out.println("Connection is not null before getting resource configuration");
                st = con.createStatement();
                try {
                   // System.out.println("In try");
                    JSONParser parser = new JSONParser();
                    System.out.println("parser=" + parser.toString());
                    System.out.println("updateJson=" + updateJson);
                    JSONObject jsonObject = (JSONObject) parser.parse(updateJson);
                    JSONObject jsonObj = (JSONObject) jsonObject.get("resourceconfig");
                    System.out.println("jsonObj=" + jsonObj.toString());
                    customername = (String) jsonObj.get("customer");
                    System.out.println("customer=" + customername);
                    service = (String) jsonObj.get("service");
                    if (service != null) {
                        if (service.equalsIgnoreCase("null") || service.equals("")) {
                            szSerQuery = "service='null'";
                        } else {
                            szSerQuery = "service = '" + service + "'";
                        }
                    } else {
                        szSerQuery = "service='null'";
                    }
                    System.out.println("service=" + service);
                    resourceid = (String) jsonObj.get("resourceid");
                    if (resourceid != null) {
                        if (resourceid.equalsIgnoreCase("null") || resourceid.equals("")) {
                            szResQuery = "resourceid='null'";
                        } else {
                            szResQuery = "resourceid = '" + resourceid + "'";
                        }
                    } else {
                        szResQuery = "resourceid='null'";
                    }
                    System.out.println("resourceid=" + resourceid);
                    JSONArray params = (JSONArray) jsonObj.get("params");
                    System.out.println(params);
                    Iterator itr = params.iterator();
                    while (itr.hasNext()) {
                        JSONObject jsObj = (JSONObject) itr.next();
                        name = (String) jsObj.get("ParamName");
                        value = (String) jsObj.get("ParamValue");
                        String szUpdateQuery = "update resourceconfig set paramvalue='" + value + "' where "
                                + "customerid=(select id from customerinfo where customername='" + customername
                                + "') and " + szSerQuery + " and " + szResQuery + " and paramname='" + name + "'";
                        System.out.println("UpdateResourceConfig Query ==>" + szUpdateQuery);
                        res = st.executeUpdate(szUpdateQuery);
                    }
                    if (res != 0) {
                        return true;
                    } else {
                        // System.out.println("Error while doing updation operation");
                        log.error("Error while doing updation operation");
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error while parsing the json");
                    //   return false;
                }
            } else {
                System.out.println("Connection is null");
                //log.info("Connection is null");
                //  return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while connecting with the database", e);
            // return false;
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                if (st != null) {
                    st.close();
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
                if (name != null) {
                    name = null;
                }
                if (value != null) {
                    value = null;
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
                log.error("Error while closing connection ", ex);
            }
        }
        return false;
    }
}
