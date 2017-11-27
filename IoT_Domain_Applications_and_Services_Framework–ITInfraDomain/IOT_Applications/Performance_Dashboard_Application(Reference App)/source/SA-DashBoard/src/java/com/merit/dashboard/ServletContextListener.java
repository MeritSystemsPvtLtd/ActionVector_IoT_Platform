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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.merit.dashboard;

import com.merit.dashboard.DBUtil.DBUtilHelper;
import com.merit.dashboard.util.PropertyUtil;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * ****************************************************************************************************
 * This Class is listening when tomcat started then start Executing
 *
 * @author satya
 * ****************************************************************************************************
 */
public class ServletContextListener /*extends Thread*/ implements javax.servlet.ServletContextListener, Runnable {

    Thread thread = null;
    public static ServletContext servletContext = null;
    public static HashMap<String, String> serverTimeLinemap = null;
    public static HashMap<String, String> desktopTimeLinemap = null;

    public static HashMap<String, String> servermap = null;
    public static HashMap<String, String> networkmap = null;
    public static HashMap<String, String> jvmmap = null;
    public static HashMap<String, String> databasemap = null;
    public static HashMap<String, String> desktopmap = null;
    public static HashMap<String, String> invertermap = null;
    public static HashMap<String, String> indexList = new HashMap<String, String>();

    public void contextInitialized(ServletContextEvent sce) {
        try {
            new DBUtilHelper();
            PropertyUtil.createMapFromProperty();
            servletContext = sce.getServletContext();
            System.out.println(" Context Listener Started");
            String[] types = new String[] {"server", "Network", "JVM", "DataBase", "Desktop", "Inverter"};
                    //DBUtilHelper.getMetrics_mapping_properties().getProperty("ResourceName").split(",");
            for(String resourceType : types){
                if(resourceType.equalsIgnoreCase("server")){
                    servermap   = createJSONMappingFolders(resourceType);
                }
                if(resourceType.equalsIgnoreCase("Network")){
                    networkmap  = createJSONMappingFolders(resourceType);
                }
                if(resourceType.equalsIgnoreCase("JVM")){
                    jvmmap      = createJSONMappingFolders(resourceType);
                }
                if(resourceType.equalsIgnoreCase("DataBase")){
                    databasemap = createJSONMappingFolders(resourceType);
                    //System.out.println(">>>>>  databasemap = " + databasemap);
                }
                if(resourceType.equalsIgnoreCase("Desktop")){
                    desktopmap  = createJSONMappingFolders(resourceType);
                }
                if(resourceType.equalsIgnoreCase("Inverter")){
                    invertermap = createJSONMappingFolders(resourceType);
                }
            }
          /*  System.out.println("desktopmap  = " + desktopmap);
            System.out.println("databasemap = " + databasemap);
            System.out.println("jvmmap      = " + jvmmap);
            System.out.println("networkmap  = " + networkmap);
            System.out.println("servermap   = " + servermap);*/
           /* thread = new Thread(this);
            thread.start();*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            Controller controler = new Controller();
            controler.init("merit");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("ContextDestroyed");

    }

    private HashMap<String, String> createJSONMappingFolders(String resourceType){
        System.out.println(">>>>>  reading file + " + "jsonconfigurations/" + resourceType + "ComboSource.json");
        InputStream in = servletContext.getResourceAsStream("jsonconfigurations/" + resourceType + "ComboSource.json");
        InputStreamReader reader = new InputStreamReader(in);
        JSONParser parser = new JSONParser();
        HashMap<String, String> map = new HashMap();

        JSONArray jsonArr = null;
        JSONObject tjsonObj = null;
        JSONArray t1jsonArr = null;
        int len1 = 0;
        int len2 = 0;

        String field = "";
        String metrictypeIn = "";
        String index = "";
        try{
            jsonArr = new JSONArray(parser.parse(reader).toString());
            tjsonObj = jsonArr.getJSONObject(0);
            jsonArr = tjsonObj.getJSONArray("items");//first josn Array contains Single json Object

            len1 = jsonArr.length();
            String tfield = "";
            for(int i = 0; i < len1; i++){
                tjsonObj = jsonArr.getJSONObject(i);
                t1jsonArr = tjsonObj.getJSONArray("items");
                len2 = t1jsonArr.length();
                for(int j = 0; j < len2; j++){
                    tjsonObj = t1jsonArr.getJSONObject(j);

                    field = tjsonObj.getString("text");
                    index = tjsonObj.getString("index");
                    metrictypeIn = tjsonObj.get("metricType").toString();

                    tfield = field;
                    //System.out.println("tjsonObj =  ++  " + tjsonObj);
                    metrictypeIn = metrictypeIn.substring(1);
                    metrictypeIn = metrictypeIn.substring(0, metrictypeIn.length()-1);
                   // System.out.println("\t .. before field = " + field + "  ..  metrictypeIn = " +metrictypeIn);
                    if(!field.contains("Alert"))
                        metrictypeIn = modifyMetricTypeAccordingtoProperty(metrictypeIn);
                   // System.out.println("\t .. after field = " + field + "  ..  metrictypeIn = " +metrictypeIn);
                    field = field.replaceAll(" ", "");
                    field = field.replaceAll("\\s", "");
                    field = field.replaceAll("\\W", "");
                    //System.out.println("putting  field = " + field + ":: metrictypeIn = " + metrictypeIn) ;
                    indexList.put(resourceType + "##" + field, index);
                    map.put(field, metrictypeIn);
                }
            }
            in.close();
            reader.close();
            in = null;
            reader = null;
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
        //System.out.println("map = " + map);
        return map;
    }

    public static HashMap<String, String> getJSONLocationMap(String resourceType){
        //System.out.println("'server'" + "  "+  "'Network'" + "  "+ "'JVM'" + "  "+ "'DataBase'" + "  "+ "'Desktop'");
        //System.out.println("resourceType = '" + resourceType + "'");
        if(resourceType.equalsIgnoreCase("server")){
            return servermap;
        }else if (resourceType.equalsIgnoreCase("Desktop")){
            return desktopmap;
        }else if (resourceType.equalsIgnoreCase("JVM")){
            return jvmmap;
        }else if (resourceType.equalsIgnoreCase("DataBase")){
            return databasemap;
        }else if (resourceType.equalsIgnoreCase("Network")){
            return networkmap;
        }else if (resourceType.equalsIgnoreCase("Inverter")){
            return invertermap;
        }

        return null;
    }

    public static Set<String> getJSONLocationSet(String resourceType){
        //System.out.println("'server'" + "  "+  "'Network'" + "  "+ "'JVM'" + "  "+ "'DataBase'" + "  "+ "'Desktop'");
        //System.out.println("resourceType = '" + resourceType + "'");
        if(resourceType.equalsIgnoreCase("server")){
            return servermap.keySet();
        }else if (resourceType.equalsIgnoreCase("Desktop")){
            return desktopmap.keySet();
        }else if (resourceType.equalsIgnoreCase("JVM")){
            return jvmmap.keySet();
        }else if (resourceType.equalsIgnoreCase("DataBase")){
            return databasemap.keySet();
        }else if (resourceType.equalsIgnoreCase("Network")){
            return networkmap.keySet();
        }else if (resourceType.equalsIgnoreCase("Inverter")){
            return invertermap.keySet();
        }
        return null;
    }

    public static String getJSONMetricGroupSet(String resourceType, String selection){
        //System.out.println("'server'" + "  "+  "'Network'" + "  "+ "'JVM'" + "  "+ "'DataBase'" + "  "+ "'Desktop'");
        //System.out.println("resourceType = '" + resourceType + "'");
        if(resourceType.equalsIgnoreCase("server")){
            return servermap.get(selection);
        }else if (resourceType.equalsIgnoreCase("Desktop")){
            return desktopmap.get(selection);
        }else if (resourceType.equalsIgnoreCase("JVM")){
            return jvmmap.get(selection);
        }else if (resourceType.equalsIgnoreCase("DataBase")){
            return databasemap.get(selection);
        }else if (resourceType.equalsIgnoreCase("Network")){
            return networkmap.get(selection);
        }else if (resourceType.equalsIgnoreCase("Inverter")){
            return invertermap.get(selection);
        }
        return null;
    }

    public static String modifyMetricTypeAccordingtoProperty(String metrictypeIn) {
        if(metrictypeIn==null || metrictypeIn.equalsIgnoreCase("")){
            return null;
        }
        metrictypeIn = metrictypeIn.replaceAll("\"", "");
        metrictypeIn = metrictypeIn.replaceAll("'", "");
        String [] metrictypes = metrictypeIn.split(",");
        metrictypeIn = "";
        String temp = "";
        for(String metrictype : metrictypes){
        	temp = metrictype;
            if(metrictype.equalsIgnoreCase("null")){
                metrictype = "''";
            } else {
                metrictype = "'" + PropertyUtil.getreverseMapping_Properties().get(metrictype) + "'";
                if(metrictype.equalsIgnoreCase("'null'")){
                	//System.out.println("...\t\t temp = " + temp + " ..metrictype = " + metrictype);
                }
            }
            metrictypeIn += "," + metrictype;
        }
        metrictypeIn = metrictypeIn.substring(1);
        return metrictypeIn;
    }

    public static String modifyMapToJSONArray(HashMap<String, String> map){
        Collection<String> set = map.values();
        if(set.size()==0){
            return "[]";
        }
        String strJSONArray = "";
        String strJSONObject = "";
        for(String value : set){
            if(value.trim().equalsIgnoreCase("")||value==null){
            }else{
                strJSONObject = "{"+value+"}";
                strJSONArray += "," + strJSONObject;
            }
        }
        if(strJSONArray.trim().equalsIgnoreCase("")||strJSONArray==null){
            return "[]";
        }else{
            strJSONArray = "[" + strJSONArray.substring(1) + "]";
            return strJSONArray;
        }
    }

    public static String getLocationGroupType(String resourceType, String metrictype){
        String grpType = null;
        String vals = null;
        HashMap<String, String> map = getJSONLocationMap(resourceType);
        Collection<String> set = map.keySet();
        for(String loc : set){
            vals = map.get(loc);
            if(vals.contains(metrictype)){
            	//System.out.println(set+"....\n     resourceType = " + resourceType + "... metrictype = " + metrictype + "....>>>>group Location  " + loc);
                grpType = loc;
                break;
            }
        }
    	//System.out.println(map+"....\n     resourceType = " + resourceType + "... metrictype = " + metrictype + "....>>>>group Location  " + grpType);
        return grpType;
    }

    public static String getIndexOfLocationGroupType(String resourceType, String selection){
    	return indexList.get(resourceType + "##" + selection);
    }

}