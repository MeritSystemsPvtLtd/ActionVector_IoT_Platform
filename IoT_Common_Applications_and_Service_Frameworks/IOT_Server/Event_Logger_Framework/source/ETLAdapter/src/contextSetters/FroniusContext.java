/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package contextSetters;

import com.prokosha.adapter.etl.ETLAdapter.ReportData;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author mahesh
 */
public class FroniusContext {

    private String field[] = null;
    String fields = null;
    String resourceType = null;
    String resourceSubType = null;
    String resourceId = null;
    String service = null;
    String subService = null;
    String hostGroup = null;

    public FroniusContext(Properties pro) throws IOException {
        fields = pro.getProperty("FroniusEventContext");
        field = fields.split(",");
        service = field[0];
        subService = field[1];
        hostGroup = field[2];
        resourceType = field[3];
        resourceSubType = field[4];
        resourceId = field[5];
        pro = null;
    }

    public HashMap<String, ReportData> setContextsInMap(HashMap<String, ReportData> metricMap) {

        HashMap<String, ReportData> map = new HashMap<String, ReportData>();
        Set<String> set1 = null;
        set1 = metricMap.keySet();
        Iterator iter = set1.iterator();

        for (int i = 0; i < set1.size(); i++) {
            String key = (String) iter.next();
            ReportData rData = (ReportData) metricMap.get(key);

            rData = setContexts(rData);

            map.put(key,rData);

            key = null;
            rData = null;
        }
        set1 = null;
        iter = null;
        field = null;
        fields = null;
        resourceType = null;
        resourceSubType = null;
        resourceId = null;
        service = null;
        subService = null;
        hostGroup = null;
        metricMap = null;

        return map;
    }

    public ReportData setContexts(ReportData rData) {

        if ((rData.getResourceType() != null) && (rData.getResourceSubType() != null) && (!rData.getResourceType().equalsIgnoreCase("null")) && (!rData.getResourceSubType().equalsIgnoreCase("null"))) {
            resourceId = rData.getResourceType() + ":" + rData.getResourceSubType();
        }

        if (rData.getService() == null || rData.getService().equalsIgnoreCase("null")) {
            rData.setService(service);
        }
        if (rData.getSubService() == null || rData.getSubService().equalsIgnoreCase("null")) {
            rData.setSubService(subService);
        }
        if (rData.getHostGroup() == null || rData.getHostGroup().equalsIgnoreCase("null")) {
            rData.setHostGroup(hostGroup);
        }
        if (rData.getTimestamp1() == null || rData.getTimestamp1().equalsIgnoreCase("null")) {
            rData.setTimestamp1("000000");
        }
        if (rData.getResourceType() == null || rData.getResourceType().equalsIgnoreCase("null")) {
            rData.setResourceType(resourceType);
        }
        if (rData.getResourceSubType() == null || rData.getResourceSubType().equalsIgnoreCase("null")) {
            rData.setResourceSubType(resourceSubType);
        }
        if (rData.getResourceId() == null || rData.getResourceId().equalsIgnoreCase("null")) {
            rData.setResourceId(resourceId);
        }

        return rData;
    }
}
