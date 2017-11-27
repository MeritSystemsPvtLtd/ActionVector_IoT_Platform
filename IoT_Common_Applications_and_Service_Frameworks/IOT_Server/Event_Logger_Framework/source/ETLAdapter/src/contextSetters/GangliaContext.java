/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contextSetters;

import com.prokosha.adapter.etl.ETLAdapter.ReportData;
import com.prokosha.dbconnection.ConnectionDAO;
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
public class GangliaContext 
{

    public HashMap<String, ReportData> setContextsInMap(HashMap<String, ReportData> metricMap,String customerID) 
    {

        HashMap<String, ReportData> map = new HashMap<String, ReportData>();
        Set<String> set1 = null;
        set1 = metricMap.keySet();
        Iterator iter = set1.iterator();

        String service = null;
        String subService = null;
        String hostGroupName = null;

        ResultSet rs = null;

        Connection connection = null;

        try 
        {
            if (metricMap.size() > 0) {

                //QueryObject qObj = new QueryObject();
                connection = ConnectionDAO.getConnection(customerID);

                Statement stmt = connection.createStatement();
                set1 = metricMap.keySet();
                iter = set1.iterator();

                for (int i = 0; i < set1.size(); i++) 
                {

                    String resourceType = null;
                    String resourceSubType = null;
                    String resourceId = null;
                    String key = iter.next().toString();
                    ReportData rData = (ReportData) metricMap.get(key);
                    rs = stmt.executeQuery("Select service,subservice,hostgroup,resourceType,resourceSubType,resourceId from hostinfo "
                            + "where host='" + rData.getHost() + "' and resourcetype='server' and customerid='" 
                            + rData.getCCustomerID() + "'");
                    while (rs.next()) 
                    {
                        service = rs.getString("service");
                        subService = rs.getString("subservice");
                        hostGroupName = rs.getString("hostgroup");

                        //resourceType = rs.getString("resourceType");
                        resourceSubType = rs.getString("resourceSubType");
                        resourceId = rs.getString("resourceId");
                    }

                    if (resourceType == null) 
                    {
                        resourceType = "server";
                    }
                    if (resourceSubType == null) 
                    {
                        resourceSubType = rData.getMetricType();
                    }
                    if (resourceId == null) 
                    {
                        resourceId = rData.getHost();
                    }

                   // rData.setResourceType(resourceType);
                    rData.setResourceSubType(resourceSubType);

                    if (rData.getResourceId() == null) 
                    {
                        rData.setResourceId(resourceId);
                    }

                    rData.setService(service);
                    rData.setSubService(subService);
                    rData.setHostGroup(hostGroupName);
                    map.put(key, rData);
                    rs.close();
                    
                    
                    resourceType = null;
                    resourceSubType = null;
                    resourceId = null;
                    key = null;
                    rData = null;
                }
                    //qObj.closeConnection();
                    stmt = null;
                    //qObj = null;
            }
        } catch (Exception e) 
        {
            e.printStackTrace();
            if(e.getMessage().contains("java.net.SocketException: Broken pipe")){
                            System.out.println("SokectException :Broken pipe");
                        System.out.println("calling ConDAO.closeConn()");
                ConnectionDAO.closeConnection(customerID);
                System.out.println("exited from ConDAO.closeCon");
                        }
        } 
        finally 
        {
            set1 = null;
            iter = null;
            service = null;
            subService = null;
            hostGroupName = null;
            rs = null;
            connection = null;
            metricMap = null;
        }

        return map;
    }
}
