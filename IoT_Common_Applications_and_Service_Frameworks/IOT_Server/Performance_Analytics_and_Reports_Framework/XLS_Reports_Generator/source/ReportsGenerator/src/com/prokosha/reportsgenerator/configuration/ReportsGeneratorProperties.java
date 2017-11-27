/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.reportsgenerator.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author naveen
 */
public class ReportsGeneratorProperties {

    private static final Logger logger = Logger.getLogger(ReportsGeneratorProperties.class.getName());
    private static String jsonSource = null;
    private static String instances = null;
    private static String periods = null;
    private static HashMap dirStructure = new HashMap();
    private static String xlsxTemplatePath=null;
    //private static String directoryStructurePath=null;
    private static String dashBoardReports=null;
    //private static String installationLevelPath=null;
    private static HashMap installationMap = new HashMap();
    private static String latestJSON=null;
    private static List latestList=null;
    private static String average=null;
    private static List averageList=null;
    private static int intervel = 0;
    private static String resources = null;
    
    public static boolean loadProperties(String fileName) throws IOException {
        String temp;
        logger.info("in loadproperties method" + fileName);
            try{
        Properties props = new Properties();
        //loading File Stream
        FileInputStream fps = new FileInputStream(fileName);
        logger.info("fps obj" + fps);
        props.load(fps);
        fps.close();
        //jsonSource is reading from property file
        temp = props.getProperty("jsonSource");
        if (temp != null) {
            jsonSource = temp;
            logger.info("tempnot null :" + jsonSource);
        } else {
            logger.info("tempnull :" + jsonSource);
            return false;
        }
         //jsonSource is reading from property file
        temp = props.getProperty("Resources");
        if (temp != null) {
            resources = temp;
            logger.info("resources null :" + resources);
        } else {
            logger.info("resources :" + resources);
            return false;
        }
        
        //Instaces is reading from property file
        temp = props.getProperty("Instances");
        if (temp != null) {
            instances = temp;
            logger.info("instances not null :" + instances);
        } else {
            logger.info("instances null :" + instances);
            return false;
        }
      
    //xlsxTemplatePath is reading from property file
        temp = props.getProperty("xlsxTemplatePath");
        if (temp != null) {
            xlsxTemplatePath = temp;
            logger.info("xlsxTemplatePath not null :" + xlsxTemplatePath);
        } else {
            logger.info("xlsxTemplatePath null :" + xlsxTemplatePath);
            return false;
        }
      //dashBoardReports is reading from property file
        temp = props.getProperty("dashBoardReports");
        if (temp != null) {
            dashBoardReports = temp;
            logger.info("dashBoardReports not null :" + dashBoardReports);
        } else {
            logger.info("dashBoardReports null :" + dashBoardReports);
            return false;
        }
        //Periods is reading from property file
        temp = props.getProperty("periods");
        if (temp != null) {
            periods = temp;
            logger.info("periods not null :" + periods);
        } else {
            logger.info("periods null :" + periods);
            return false;
        }
        //latestJSON is reading from property file
        temp = props.getProperty("latest");
        if (temp != null) {
            latestJSON = temp;
            logger.info("latestJSON not null :" + latestJSON);
        } else {
            logger.info("latestJSON :" + latestJSON);
            return false;
        }
        
        
        //intervel is reading from property file
        temp =props.getProperty("intervel");
        logger.info(temp);
        int inter=Integer.parseInt(temp);
        logger.info(inter);

        if (inter != 0) {
            intervel = inter;
            logger.info("intervel not null :" + intervel);
        } else {
            logger.info("intervel :" + intervel);
            return false;
        }
        //average is reading from property file
        temp = props.getProperty("average");
        if (temp != null) {
            average = temp;
            logger.info("average not null :" + average);
        } else {
            logger.info("average null :" + average);
            return false;
        }
        //calling the createInstanceMap method
        createInstanceMap();
       // MetricsMap();
        latest();
        average();
            }catch(Exception e){
            e.printStackTrace();
            }
        return true;
    }

    public static String getJsonSource() {
        logger.info("in get method jsonSource :" + jsonSource);
        return jsonSource;
    }
    public static String getResource() {
        logger.info("in get method resources :" + resources);
        return resources;
    }
    public static String getInstances() {
        logger.info("in get method instances :" + instances);
        return instances;
    }
    public static String getPeriods() {
        logger.info("in get method periods :" + periods);
        return periods;
    }
    public static HashMap getDirStructureMap() {
        logger.info("in get method dirStructure :" + dirStructure);
        return dirStructure;
    }
    public static HashMap getInstallationMap() {
        logger.info("in get method installationMap :" + installationMap);
        return installationMap;
    }
    public static String getxlsxTemplatePath() {
        logger.info("in get method xlsxTemplatePath :" + xlsxTemplatePath);
        return xlsxTemplatePath;
    }
   /* public static String getDirectoryStructurePath() {
        logger.info("in get method xlsxTemplatePath :" + directoryStructurePath);
        return directoryStructurePath;
    }*/
    public static String getLatestJSON() {
        logger.info("in get method latestJSON :" + latestJSON);
        return latestJSON;
    }
       public static List getLatestList() {
        logger.info("in get method latestList :" + latestList);
        return latestList;
    }
    public static String getAverage() {
        logger.info("in get method average :" + average);
        return average;
    }

    public static int getIntervel() {
        logger.info("in get method intervel :" + intervel);
        return intervel;
    }
       public static List getAverageList() {
        logger.info("in get method averageList :" + averageList);
        return averageList;
    }
     /* public static String getInstallationLevelPath() {
        logger.info("in get method installationLevelPath :" + installationLevelPath);
        return installationLevelPath;
    }*/
    public static String getDashBoardReports() {
        logger.info("in get method dashBoardReports :" + dashBoardReports);
        return dashBoardReports;
    }
    public static boolean createInstanceMap() {
        String[] splites = instances.split(",");
         try{
        for (int i = 0; i < splites.length; i++) {
            String[] splitesColon = splites[i].split(":");     
            String key = splitesColon[0];
            if (!dirStructure.containsKey(key)) {
                HashMap subMap = new HashMap();
                ArrayList resTypes = new ArrayList();
                resTypes.add(splitesColon[2]);
                subMap.put(splitesColon[1], resTypes);
                dirStructure.put(key, subMap);
            } else {
                String subKey = splitesColon[1];
                HashMap subMap = (HashMap) dirStructure.get(key);
                if (subMap.containsKey(subKey)) {
                    ArrayList resTypes = (ArrayList) subMap.get(subKey);
                    resTypes.add(splitesColon[2]);
                    subMap.remove(subKey);
                    subMap.put(subKey, resTypes);
                } else {
                    ArrayList resTypes = new ArrayList();
                    resTypes.add(splitesColon[2]);
                    subMap.put(subKey, resTypes);
                }
                dirStructure.remove(key);
                dirStructure.put(key, subMap);
            }
        }
         }catch(Exception e){
         e.printStackTrace();
         }
        return true;
    }
    /*public static boolean MetricsMap(){
    try{
    String installationMetrics=installationLevelPath;
    String[] spliteInstallation=installationMetrics.split(",");
    int installLen=spliteInstallation.length;
    List list = new ArrayList();
    for(int i=0;i<installLen;i++){   
    list.add(spliteInstallation[i]);
    }
    String key="InstallationLevelMetrics";
        installationMap.put(key,list);
    }catch(Exception e){
    e.printStackTrace();
    }
    return true;
    }*/
    public static boolean latest(){
        try{
        String[] spliteLatest=latestJSON.split(",");
    int latestLen=spliteLatest.length;
    latestList = new ArrayList();
    for(int i=0;i<latestLen;i++){
    latestList.add(spliteLatest[i]);
    }
        }catch(Exception e){
        e.printStackTrace();
        }
    return true;
    }
    public static boolean average(){
        try{
        String[] spliteAverage=average.split(",");
    int latestLen=spliteAverage.length;
    averageList = new ArrayList();
    for(int i=0;i<latestLen;i++){
    averageList.add(spliteAverage[i]);
    }
        }catch(Exception e){
        e.printStackTrace();
        }
    return true;
    }
}