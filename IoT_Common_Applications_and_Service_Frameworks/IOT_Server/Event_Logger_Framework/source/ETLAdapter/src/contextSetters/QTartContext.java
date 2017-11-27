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
public class QTartContext {

    String field[];
    Properties pro = null;
    String fields = null;
    String resourceType = null;
    String resourceSubType = null;
    String resourceId = null;

    public QTartContext(Properties pro) throws IOException {
        this.pro = pro;
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

            map.put(key, rData);

            key = null;
            rData = null;
        }
        iter = null;
        set1 = null;
        metricMap = null;
        return map;
    }

    public ReportData setContexts(ReportData rData) {

        fields = pro.getProperty(rData.getAlertName());
        field = fields.split(",");

        rData.setService(field[0]);
        rData.setSubService(field[1]);
        rData.setHostGroup(field[2]);
        rData.setTimestamp1("000000");

        if ((rData.getResourceType() != null) && (rData.getResourceSubType() != null)) {
            resourceId = rData.getResourceType() + ":" + rData.getResourceSubType();
        }
        resourceType = field[3];
        resourceSubType = field[4];
        if (resourceId == null) {
            resourceId = field[5];
        }

        if (rData.getResourceType() == null) {
            rData.setResourceType(resourceType);
        }
        if (rData.getResourceSubType() == null) {
            rData.setResourceSubType(resourceSubType);
        }
        if (rData.getResourceId() == null) {
            rData.setResourceId(resourceId);
        }


        field = null;
        fields = null;
        resourceType = null;
        resourceSubType = null;
        resourceId = null;

        return rData;
    }
}
