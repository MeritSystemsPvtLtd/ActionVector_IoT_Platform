/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contextSetters;

import com.prokosha.adapter.etl.ETLAdapter.ReportData;
import independentDatabaseQuery.QueryObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author mahesh
 */
public class SadeskHealthMonContext {

    public HashMap<String, ReportData> setContextsInMap(HashMap<String, ReportData> metricMap) {

        HashMap<String, ReportData> map = new HashMap<String, ReportData>();
        Set<String> set1 = null;
        set1 = metricMap.keySet();
        Iterator iter = set1.iterator();

        String service = null;
        String subService = null;
        String hostGroupName = null;

        String resourceType = null;
        String resourceSubType = null;
        String resourceId = null;

        ResultSet rs = null;
        Connection connection = null;

        try {
            if (metricMap.size() > 0) {

                QueryObject qObj = new QueryObject();
                connection = qObj.createConnection();

                Statement stmt = connection.createStatement();
                set1 = metricMap.keySet();
                iter = set1.iterator();

                for (int i = 0; i < set1.size(); i++) {
                    String key = iter.next().toString();
                    ReportData rData = (ReportData) metricMap.get(key);
                    if (i == 0) {
                        rs = stmt.executeQuery("Select service,subservice,hostgroup,resourceType,resourceSubType,resourceId from "
                                + "hostinfo where lower(host)=lower('" + rData.getHost() + "')  and customerid='" 
                            + rData.getCCustomerID() + "'");
                        while (rs.next()) {
                            service = rs.getString("service");
                            subService = rs.getString("subservice");
                            hostGroupName = rs.getString("hostgroup");

                            resourceType = rs.getString("resourceType");
                            resourceSubType = rs.getString("resourceSubType");
                            resourceId = rs.getString("resourceId");
                        }

                        if (resourceType == null) {
                            resourceType = "applictions";
                        }
                        if (resourceSubType == null) {
                            resourceSubType = rData.getMetricType();
                        }
                        if (resourceId == null) {
                            resourceId = rData.getHost();//resourceType+"/"+resourceSubType+
                            //"/"/* +<Unique sequence number>+"/" */+rData.getHost();
                        }

                        rData.setResourceType(resourceType);
                        rData.setResourceSubType(resourceSubType);

                        if (rData.getResourceId() == null) {
                            rData.setResourceId(resourceId);
                        }

                        rData.setService(service);
                        rData.setSubService(subService);
                        rData.setHostGroup(hostGroupName);
                        map.put(key, rData);

                        key = null;
                        rData = null;
                    }

                    rs.close();
                }
                qObj.closeConnection();
                qObj = null;
                stmt = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            set1 = null;
            iter = null;
            service = null;
            subService = null;
            hostGroupName = null;
            resourceType = null;
            resourceSubType = null;
            resourceId = null;
            rs = null;
            connection = null;
        }

        return map;
    }
}
