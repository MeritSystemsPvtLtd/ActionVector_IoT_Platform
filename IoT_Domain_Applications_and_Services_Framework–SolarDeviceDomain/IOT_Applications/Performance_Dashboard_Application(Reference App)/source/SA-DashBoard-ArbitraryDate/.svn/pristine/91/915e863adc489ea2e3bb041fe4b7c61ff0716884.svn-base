/**
 * *********************************************************************
 * Software Developed by Merit Systems Pvt. Ltd., No. 42/1, 55/c, Nandi Mansion,
 * 40th Cross, Jayanagar 8th Block Bangalore - 560 070, India Work Created for
 * Merit Systems Private Limited All rights reserved
 *
 * THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES
 * NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED, DISTRIBUTED,
 * REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED,
 * COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD
 * SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 * *********************************************************************
 */
package com.merit.dashboard.util;

import java.util.HashMap;
import java.util.Properties;
import com.merit.dashboard.DBUtil.DBUtilHelper;

public class PropertyUtil {

    static HashMap<String, String> reverseMapping_Properties = new HashMap<String, String>();

    /**
     * ************************************************************************************************
     * This method is for creating HashMap that contains values as key and keys
     * as values of external property files that will be useful when you click
     * on chart according to this summayGrid should be filled In this case we
     * are passing the name of metrictype that we have already changed so this
     * name is not same as database having. so this name will not execute in
     * query that's why we are reversing the property files and storing as once
     * and many times.
     * *************************************************************************************************
     */
    public static HashMap<String, String> createMapFromProperty() {
        try {
            Properties properties = DBUtilHelper.getMetrics_mapping_properties();
            for (String key : properties.stringPropertyNames())
                reverseMapping_Properties.put(properties.getProperty(key), key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reverseMapping_Properties;

    }

    /**
     * ************************************************************************************************
     * This method is for getting metric type that is same as in DataBase.
     * *************************************************************************************************
     */
    public static HashMap<String, String> getreverseMapping_Properties() {
        return reverseMapping_Properties;
    }
}
