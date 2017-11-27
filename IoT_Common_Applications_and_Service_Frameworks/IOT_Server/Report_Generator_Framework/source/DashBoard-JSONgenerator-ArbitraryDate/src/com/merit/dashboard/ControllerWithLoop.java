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
package com.merit.dashboard;

import main.ServiceThreadListener;
import java.io.File;
import java.util.ArrayList;


import org.apache.log4j.Logger;

import com.merit.dashboard.DBUtil.DBUtilHelper;
import com.merit.dashboard.controller.DashBoardController;
import com.merit.dashboard.file.SendFileToJson;
import com.merit.dashboard.queryexecuter.QueryExecuter;
import com.merit.dashboard.service.DashBoardService;
import com.merit.dashboard.util.PerformanceJsonsConfig;
import com.merit.dashboard.util.ResourceConfiguration;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class ControllerWithLoop {

    File folderstructure = null;
    private static Logger log;
    private static HashMap PerfJsonPropMap;
    //public static String[] cCustomers = {"192.168.1.2","c2","c3","c4","c5"};
    public static HashMap<String, HashMap<String, ArrayList>> custServiceMap = new HashMap();

    /**
     * This method is used to call DashBoardController for generating different
     * types of JSON for Chart
     *
     * @param customerName customerId
     * @param timestampselection means (hour, days, week, month, year)
     * @param tomcat_home where you want to create a directory
     *
     */
    private void runTheCode(String customerName, String timestampselection, String cCustomer, String service, String tomcat_home) {
        String sz_startDate = null;
        String sz_endDate = null;
        String start_endarray[] = DateGenerator.dateGenerator(timestampselection);
        sz_startDate = start_endarray[1];
        sz_endDate = start_endarray[0];

        //System.out.println(" Start---Date:" + start_endarray[1] + ";\n End-----Date:" + start_endarray[0] + ";\n CustomerID:" + customerName);
        log.debug(" Start---Date:" + start_endarray[1] + ";\n End-----Date:" + start_endarray[0] + ";\n CustomerID:" + customerName);

        //String[] types = DBUtilHelper.getMetrics_mapping_properties().getProperty("ResourceName").split(",");
        String[] types;
        String resTypes = DBUtilHelper.getMetrics_mapping_properties().getProperty("ResourceName");
        if (resTypes.contains(",")) {
            types = resTypes.split(",");
        } else {
            types = new String[1];
            types[0] = resTypes;
        }
        try {
            Set<String> comboSelection = null;
            DashBoardController list = null;
            for (int i = 0; i < types.length; i++) {
                log.debug("*************Starting " + types[i] + "**********************");
                // System.out.println("************Starting " + types[i] + "**********************");
                comboSelection = ServiceThreadListener.getJSONLocationSet(types[i]);
                for (String selection : comboSelection) {
                    list = new DashBoardController(types[i], cCustomer, service, selection, sz_startDate, sz_endDate, timestampselection,
                            customerName);
                    if (i == types.length - 1) {
                        list.generateDefaultServiceAndResourceType(types[i], cCustomer, service, sz_startDate, sz_endDate,
                                timestampselection, customerName);
                    }
                }
                list.generateJSONForLeftGrid(types[i], cCustomer, service, sz_startDate, sz_endDate, timestampselection, customerName);
                //device level derived jsons
                generatePerformanceJsons(customerName, cCustomer, service, types[i], timestampselection);

                //System.out.println("******************End of " + types[i] + "**********************");
                log.debug("***********************End of " + types[i] + "**********************");
                list = null;
            }
        } catch (Exception e) {
            log.error("IN DashBoardController some ERROR is here" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method we are calling generateDirectoryStructureByCustomerID() and
     * runTheCode() method
     *
     * @param forCustomer customerId
     * @param sz_tomcat_home where you want to create a directory
     *
     */
    private void createThread_1(String forCustomer, String sz_tomcat_home) {
        /*//forCustomerName = forCustomer;
         String[] tVals = {"Hour", "Day", "Week", "Month", "Year"};
         ArrayList<String> al_customer_list = getCustomerList();
         long iTime = 1000 * 20;
         int count = 0;
         boolean flag = false;
         boolean firstRun = true;

         while (true) {
         System.out.println("\nCustomer List Size===" + al_customer_list.size() + "\n");
         log.debug("\nCustomer List Size===" + al_customer_list.size() + "\n");

         if (al_customer_list.size() > 0) {
         for (int j = 0; j < al_customer_list.size(); j++) {
         for (int i = 0; i < tVals.length; i++) {
         if(firstRun){
         flag = true;
         }else {
         flag = ServiceThreadListener.tickTimer.getFlagForTvals(tVals[i]);
         }
         if(firstRun || flag){
         generateDirectoryStructureByCustomerID(al_customer_list.get(j), tVals[i], sz_tomcat_home);
         runTheCode(al_customer_list.get(j), tVals[i], sz_tomcat_home);
         count++;
         System.out.println("************************End Of " + tVals[i] + " Generated " + count + " *******************");
         }
         }
         }
         firstRun = false;
         System.out.println("*******************************First run of Loop has completed to generate all the data for {Hour, Day, Week, Month, Year}****************************************");
         try {
         Thread.sleep(iTime);
         count = 0;
         } catch (Exception interruptedException) {
         System.out.println(interruptedException.getMessage());
         log.error(interruptedException.getMessage());
         }
         } else {
         System.out.println("********Customer List Empty ***********");
         }
         }*/
    }

    //public void main(String[] fors) {
    public void createThread(String forCustomer, String sz_tomcat_home) {
        Object[] cCustomers;//
        String cCustomer;//
        String service;//
        ArrayList services;//
        List<String> al_customer_list = getCustomerList();
        String timestamps[] = new String[]{"Hour", "Day", "Week", "Month", "Year"};
        long stampsStandardFreq[] = new long[]{ServiceThreadListener.gHTimeWait, ServiceThreadListener.gDTimeWait, ServiceThreadListener.gWTimeWait, ServiceThreadListener.gMTimeWait, ServiceThreadListener.gYTimeWait};
        boolean flag_forced$sleep = ServiceThreadListener.forcedJSON;

        long least_freq = getCommonDivisor(stampsStandardFreq);//iTime

        long counter = 0;

        long ff[] = {0, 0, 0, 0, 0};

        String[] tVals = timestamps;


        //code added by Rekha to initialize resource configuration for all customers and customer's customer.
        ResourceConfiguration.initialize(al_customer_list);
        PerfJsonPropMap = PerformanceJsonsConfig.initialize();
        if (flag_forced$sleep) {
            System.out.println("__________________generating forced data for first execution._____________________");
            for (String customer : al_customer_list) {
                System.out.println("--------------------------------for customer : " + customer + "work-------------------------------");
                cCustomers = custServiceMap.get(customer).keySet().toArray();
                generateDirectoryStructureByCustomerID(customer, sz_tomcat_home);
                for (int i = 0; i < 5; i++) {
                    //timestamp[i]
                    //generateDirectoryStructureByCustomerID(customer, tVals[i], sz_tomcat_home);
                    generateCustomersStatus(customer, tVals[i], sz_tomcat_home);
                    for (int c = 0; c < cCustomers.length; c++) {
                        cCustomer = cCustomers[c].toString();
                        generateServicesStatus(customer, cCustomer, tVals[i], sz_tomcat_home);
                        generatePerformanceJsons(customer, cCustomer, null, "", tVals[i]);
                        generateCustomerLevelAlerts(customer, cCustomer, tVals[i], sz_tomcat_home);
                        services = custServiceMap.get(customer).get(cCustomers[c]);
                        for (int s = 0; s < services.size(); s++) {
                            System.out.println("Running code for customer::" + customer + "==tVals[i]::" + tVals[i] + "==cCustomers"
                                    + cCustomer + "==services::" + services.get(s).toString());
                            service = services.get(s).toString();
                            generatePerformanceJsons(customer, cCustomer, service, "", tVals[i]);
                            generateServiceLevelAlerts(customer, cCustomer, service, tVals[i], sz_tomcat_home);
                            runTheCode(customer, tVals[i], cCustomer, service, sz_tomcat_home);
                        }
                    }
                }
                System.out.println("---------------------------------------------------------------------------------------");
            }
            System.out.println("______________________________________________________________");
        } else {
            System.out.println("generating data with sleep configuration..");
        }

        System.out.println("\n\n++++++++++");
        System.out.println("   Standard HOUR  frequency[0] = " + stampsStandardFreq[0] + " sec  :  current Hour   = " + ff[0] + " sec.");
        System.out.println("   Standard DAY   frequency[1] = " + stampsStandardFreq[1] + " sec  :  current Day    = " + ff[1] + " sec.");
        System.out.println("   Standard WEEK  frequency[2] = " + stampsStandardFreq[2] + " sec  :  current Week   = " + ff[2] + " sec.");
        System.out.println("   Standard MONTH frequency[3] = " + stampsStandardFreq[3] + " sec  :  current Month  = " + ff[3] + " sec.");
        System.out.println("   Standard YEAR  frequency[4] = " + stampsStandardFreq[4] + " sec  :  current Year   = " + ff[4] + " sec.");
        System.out.println("_____________");
        System.out.println("counter = " + counter);
        long seconds1 = 0;
        long seconds2 = 0;
        Date d1 = new Date();//modified
        Date d2 = new Date();//modified
        while (true) {
            for (String customer : al_customer_list) {
                System.out.println("--------------------------------for customer : " + customer + " work-------------------------------");
                cCustomers = custServiceMap.get(customer).keySet().toArray();
                generateDirectoryStructureByCustomerID(customer, sz_tomcat_home);
                for (int i = 0; i < 5; i++) {
                    seconds1 = (d1).getTime() / 1000;//
                    //timestamp[i]
                    if (ff[i] >= stampsStandardFreq[i] && stampsStandardFreq[i] > 0) {
                        System.out.println(".\t\tstamps[i]   " + timestamps[i]);
                        //generateDirectoryStructureByCustomerID(customer, tVals[i], sz_tomcat_home);
                        for (int c = 0; c < cCustomers.length; c++) {
                            services = custServiceMap.get(customer).get(cCustomers[c]);
                            for (int s = 0; s < services.size(); s++) {
                                runTheCode(customer, tVals[i], cCustomers[c].toString(), services.get(s).toString(), sz_tomcat_home);
                            }
                        }
                    }
                    seconds2 = ((d2).getTime() / 1000) - seconds1;//
                    for (int j = 0; j < 5; j++) {
                        ff[j] += seconds2;
                    }
                }
                System.out.println("----------------------------------------------------------------------------------------");
            }
            try {
                System.out.println("going to sleep for " + least_freq + " seconds.");
                Thread.sleep(1000 * least_freq);//multiply by LCM
                counter += least_freq;
                System.out.println("\n\n++++++++++");
                for (int i = 0; i < 5; i++) {
                    //timestamp[i]
                    if (ff[i] >= stampsStandardFreq[i] && stampsStandardFreq[i] > 0) {
                        ff[i] = ff[i] % stampsStandardFreq[i];
                    }
                    ff[i] += least_freq;
                    System.out.println("   Standard " + timestamps[i].toUpperCase() + " frequency[" + i + "] = " + stampsStandardFreq[i] + " sec  :  current " + timestamps[i].toUpperCase() + " fval = " + ff[i] + " sec.");
                }
                System.out.println("_____________");
                System.out.println("counter = " + counter);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error in createThread" + e.toString());//
            }
        }
    }

    /**
     * This method we are calling generateDirectoryStructureByCustomerID() and
     * runTheCode() method
     *
     * @param forCustomer customerId
     * @param sz_tomcat_home where you want to create a directory
     *
     */
    /*private void createThread(String forCustomer, String sz_tomcat_home, String[] tVals, long sleepInterval) {
     //forCustomerName = forCustomer;
     //long iTime = 1000*60*60;
     long iTime = sleepInterval;
     int count = 0;
     //String[] tVals = {"Hour", "Day", "Week", "Month", "Year"};
     while (true) {
     ArrayList<String> al_customer_list = getCustomerList();

     System.out.println("\nCustomer List Size===" + al_customer_list.size() + "\n");
     log.debug("\nCustomer List Size===" + al_customer_list.size() + "\n");

     if (al_customer_list.size() > 0) {
     for (int j = 0; j < al_customer_list.size(); j++) {
     Object[] cCustomers = custServiceMap.get(al_customer_list.get(j)).keySet().toArray();
     generateDirectoryStructureByCustomerID(al_customer_list.get(j), sz_tomcat_home);
     for (int i = 0; i < tVals.length; i++) {
     //generateDirectoryStructureByCustomerID(al_customer_list.get(j), tVals[i], sz_tomcat_home);
     for (int c = 0; c < cCustomers.length; c++) {
     ArrayList services = custServiceMap.get(al_customer_list.get(j)).get(cCustomers[c]);
     for (int s = 0; s < services.size(); s++) {
     runTheCode(al_customer_list.get(j), tVals[i], cCustomers[c].toString(), services.get(s).toString(),
     sz_tomcat_home);
     }
     }
     count++;
     System.out.println("************************End Of " + tVals[i] + " Generated " + count + " *******************");
     }
     }
     try {
     Thread.sleep(iTime);
     count = 0;
     } catch (InterruptedException interruptedException) {
     System.out.println(interruptedException.getMessage());
     log.error(interruptedException.getMessage());
     }
     } else {
     System.out.println("********Customer List Empty ***********");
     }
     }
     }*/
    /**
     * This method is creating Directory like merit inside
     * hour,day,week,month,year then inside hour:
     * server,Desktop,DataBase,Network,JVM etc
     *
     * @param customerName customerId
     * @param timestampselection means (hour, days, week, month, year)
     * @param tomcat_home where you want to create a directory
     *
     */
    public void generateDirectoryStructureByCustomerID(String customerName, String tomcat_home) {

        String szProjectName = DBUtilHelper.getMetrics_mapping_properties().getProperty("projectName");
        String[] types;
        String resTypes = DBUtilHelper.getMetrics_mapping_properties().getProperty("ResourceName");
        if (resTypes.contains(",")) {
            types = resTypes.split(",");
        } else {
            types = new String[1];
            types[0] = resTypes;
        }

        Object[] cCustomers = custServiceMap.get(customerName).keySet().toArray();

        //System.out.println("\n ProjectName:" + szProjectName + ";\n Customer Name:" + customerName + "\n Resource Type List::" + types.length);
        log.debug("\n ProjectName:" + szProjectName + ";\n Customer Name:" + customerName + "\n Resource Type List::" + types.length);

        File f1 = new File(
                tomcat_home + File.separator
                + //"webapps" + File.separator +
                szProjectName + File.separator
                + customerName);
        if (!f1.exists()) {
            //System.out.println(" Customer directory not found:" + tomcat_home + File.separator +   "webapps" + File.separator +   szProjectName + File.separator + customerName);
            System.out.println(" Customer directory not found:" + tomcat_home + File.separator + /*"webapps" + File.separator +*/ szProjectName + File.separator + customerName);
            String[] timedFOLDR = {"Hour", "Day", "Week", "Month", "Year"};
            List<String> arrMetricList = new ArrayList();
            try {
                System.out.println("generating folder structure....");
                ArrayList services;//
                for (int t = 0; t < timedFOLDR.length; t++) {
                    for (int i = 0; i < types.length; i++) {
                        arrMetricList.clear();
                        arrMetricList.addAll(ServiceThreadListener.getJSONLocationSet(types[i].trim()));
                        for (int c = 0; c < cCustomers.length; c++) {
                            services = custServiceMap.get(customerName).get(cCustomers[c].toString());
                            for (int s = 0; s < services.size(); s++) {
                                //getArray of JSON Folders remoove space from names { }
                                //create one more for loop add the named folder except TIME LINE
                                //System.out.println("types[i].trim() = " + types[i].trim());
                                //Set<String> arrMetricGroup =
                                for (String mtricgroup : arrMetricList) {
                                    if (mtricgroup != null) {
                                        folderstructure = new File(
                                                tomcat_home + File.separator
                                                //+ "webapps" + File.separator
                                                + szProjectName + File.separator
                                                + customerName + File.separator
                                                + timedFOLDR[t] + File.separator
                                                + cCustomers[c].toString() + File.separator
                                                + services.get(s).toString() + File.separator
                                                + types[i].trim() + File.separator
                                                + mtricgroup);
                                        folderstructure.mkdirs();
                                    }
                                }
                            }
                        }
                    }
                }
                folderstructure = null;
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error in generateDirectoryStructureByCustomerID" + e.toString());//
            } finally {
                cCustomers = null;// modified
                arrMetricList = null;//modified 
            }
        } else {
            //System.out.println(" Ok! Customer directory found: webapps" + File.separator + szProjectName + File.separator + customerName);
            System.out.println(" Ok! Customer directory found: " + szProjectName + File.separator + customerName);
        }
        f1 = null;
    }

    public boolean deleteDir(File dir) {
        boolean success;//
        String[] children;
        File f;//modified
        if (dir.isDirectory()) {
            children = dir.list();
            for (int i = 0; i < children.length; i++) {
                if (!children[i].equalsIgnoreCase("TimeLine")) {
                    f = new File(dir, children[i]);//modified
                    success = deleteDir(f);//modified
                    if (!success) {
                        return false;
                    }
                }
                f = null;
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    /**
     * This method is calling createThread() method after reading property files
     * that are stored externally
     *
     * @param sz_customer customerId
     */
    public void init(String sz_customer) {
        try {
            log = Logger.getLogger(ControllerWithLoop.class);
            System.out.println("....init()" + sz_customer);

            //String tomcat_home = System.getProperty("catalina.base");
            String tomcat_home = DBUtilHelper.dashBoardJSONPATH;
            createThread(sz_customer, tomcat_home);
            // dbUtil=null;
        } catch (Exception ex) {
            log.error("DashBoard Controller :\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * This method is calling createThread() method after reading property files
     * that are stored externally
     *
     * @param sz_customer customerId
     */
    public void init(String sz_customer, String[] tVals, long sleepInterval) {
        try {
            log = Logger.getLogger(ControllerWithLoop.class);
            System.out.println("....init()" + sz_customer);

            //String tomcat_home = System.getProperty("catalina.base");
            String tomcat_home = DBUtilHelper.dashBoardJSONPATH;
            createThread(sz_customer, tomcat_home);
            // dbUtil=null;
        } catch (Exception ex) {
            log.error("DashBoard Controller :\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * This method is creating a list of customer
     */
    public ArrayList<String> getCustomerList() {

        /*
         Connection con = null;
         Statement st = null;
         ResultSet rs = null;
         */
        ArrayList<String> al_customer_list = new ArrayList<String>();
        try {
            /*Properties properties = DBUtilHelper.getProperties();
             Class.forName(properties.getProperty("driverName"));
             con = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("username"), properties.getProperty("password"));
             st = con.createStatement();
             rs = st.executeQuery("select distinct(domainname) from customerregister where domainname!='null'");

             while (rs.next()) {
             al_customer_list.add(rs.getString("domainname"));
             }*/
            //al_customer_list.add("192.168.1.2");
            //String custList[] = DBUtilHelper.getMetrics_mapping_properties().getProperty("domainName").split(",");
            String custList[] = new String[]{ServiceThreadListener.argCustomerID};
            for (String cust : custList) {
                al_customer_list.add(cust.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in getCustomerList" + e.toString());
            return al_customer_list;
        }
        return al_customer_list;

    }

    /**
     * @return greatest common divisor for many integers max value of common
     * divisor 9699690 which does not include Prime Numbers greater than 19.
     */
    public static int getCommonDivisor(long... nums) {
        int i[] = {2, 3, 5, 7, 11, 13, 17, 19};
        int gcd = 1;
        int devigibleNums = 0;
        for (int j : i) {
            devigibleNums = 0;
            for (long num : nums) {
                if (num % j == 0) {
                    devigibleNums++;
                }
            }
            if (devigibleNums == nums.length) {
                gcd *= j;
            }
        }
        return gcd;
    }

    public static PerformanceJsonsConfig getJsonConfig(String perfJson) {
        PerformanceJsonsConfig szConf = (PerformanceJsonsConfig) PerfJsonPropMap.get(perfJson);
        return szConf;
    }

    public static void generateCustomerServiceMap(List customer) {
        try {
            System.out.println("Im in generateCustomerServiceMap");
            custServiceMap = DashBoardService.generateCustomerServiceMap(customer);
        } catch (Exception e) {
            log.error("Error in generateCustomerServiceMap==>>" + e.toString());
            e.printStackTrace();
        }
    }

    private void generateCustomersStatus(String customerName, String timestampselection, String tomcat_home) {
        String sz_startDate = null;
        String sz_endDate = null;
        String start_endarray[] = DateGenerator.dateGenerator(timestampselection);
        sz_startDate = start_endarray[1];
        sz_endDate = start_endarray[0];
        SendFileToJson sfj;//modified
        try {
            QueryExecuter qe = new QueryExecuter();
            String szCustomerJson = qe.generateCustomersLeftGridJson(sz_startDate, sz_endDate,
                    timestampselection, customerName);
           sfj =  new SendFileToJson(customerName, "", timestampselection, "", null, null, "CustomersLeftGrid", szCustomerJson);//modified
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in generateCustomersStatus" + e.toString());
        }
        sfj = null;
    }

    private void generateServicesStatus(String customerName, String cCustomer, String timestampselection, String tomcat_home) {
        String sz_startDate = null;
        String sz_endDate = null;
        String start_endarray[] = DateGenerator.dateGenerator(timestampselection);
        sz_startDate = start_endarray[1];
        sz_endDate = start_endarray[0];
        SendFileToJson sfj;//modified
        try {
            QueryExecuter qe = new QueryExecuter();
            String szServerJson = qe.generateServicesLeftGridJson(cCustomer, sz_startDate, sz_endDate,
                    timestampselection, customerName);
           sfj =  new SendFileToJson(customerName, "", timestampselection, "", cCustomer, null, "ServicesLeftGrid", szServerJson);//modified
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in generateServicesStatus" + e.toString());
        }
        sfj = null;
    }

    private void generatePerformanceJsons(String customerName, String cCustomer, String service, String resType, String timestampselection) {
        String sz_startDate = null;
        String sz_endDate = null;
        String start_endarray[] = DateGenerator.dateGenerator(timestampselection);
        sz_startDate = start_endarray[1];
        sz_endDate = start_endarray[0];
       SendFileToJson sfj;//modified
        try {
            //added by Rekha for generating service performance json======================================================================
            String[] szPerformanceJsons = DBUtilHelper.getMetrics_mapping_properties().getProperty("PerformanceJsons").split(",");
            log.debug("Performance jsons configured==>>" + szPerformanceJsons.length);
            String szResourceType, metricType, configuredMetricType, jsonGenr;
            PerformanceJsonsConfig szJsonConf;
            QueryExecuter qe;
            for (String perfJSON : szPerformanceJsons) {
                log.debug("Generating derivedJSON==>>" + perfJSON);
                szJsonConf = ControllerWithLoop.getJsonConfig(perfJSON);
                szResourceType = szJsonConf.getResourceType();
                metricType = szJsonConf.getMetricType();
                configuredMetricType = szJsonConf.getConfiguredMetricType();
                jsonGenr = szJsonConf.getJsonGenerator(resType);
                if (jsonGenr != null) {
                    qe = new QueryExecuter();
                    String szServicePerformance = qe.generatePerformanceJsons(szResourceType, cCustomer, service, resType, metricType, configuredMetricType,
                            jsonGenr, sz_startDate, sz_endDate, timestampselection, customerName);
                    sfj = new SendFileToJson(customerName, "", timestampselection, resType, cCustomer, service, perfJSON, szServicePerformance);//modified
                    log.debug("Done Generating derivedJSON==>>" + perfJSON);
                }
                sfj = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in generatePerformanceJsons" + e.toString());
        } finally {
            start_endarray = null;//
        }

        //============================================================================================================================
    }

    private void generateCustomerLevelAlerts(String customerName, String cCustomer, String timestampselection, String tomcat_home) {
        String sz_startDate = null;
        String sz_endDate = null;
        String start_endarray[] = DateGenerator.dateGenerator(timestampselection);
        sz_startDate = start_endarray[1];
        sz_endDate = start_endarray[0];
        String[] custLevelAlerts;
        LinkedHashMap<String, String> szAlertsMap;
        String szAlertJSON = null;
        SendFileToJson sfj;//modified
        try {
            custLevelAlerts = DBUtilHelper.getMetrics_mapping_properties().getProperty("CustomerAlertsCategories").split(",");
            log.debug("Customer Alerts Categories==>>" + custLevelAlerts.toString());
            QueryExecuter qe;
            for (String alertCat : custLevelAlerts) {
                log.debug("Get alerts for==>>" + alertCat);
                qe = new QueryExecuter();
                szAlertsMap = qe.collectTicketDataForCustomer(customerName, cCustomer, sz_startDate, sz_endDate, alertCat);
                szAlertJSON = ServiceThreadListener.modifyMapToJSONArray(szAlertsMap);
               sfj =  new SendFileToJson(customerName, "", timestampselection, "", cCustomer, null, alertCat, szAlertJSON);//modified
               sfj = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in generateCustomerLevelAlerts" + e.toString());
        } finally {
            custLevelAlerts = null;
            szAlertsMap = null;
            szAlertJSON = null;
        }
    }

    private void generateServiceLevelAlerts(String customerName, String cCustomer, String service, String timestampselection, String tomcat_home) {
        String sz_startDate = null;
        String sz_endDate = null;
        String start_endarray[] = DateGenerator.dateGenerator(timestampselection);
        sz_startDate = start_endarray[1];
        sz_endDate = start_endarray[0];
        String[] custLevelAlerts;
        LinkedHashMap<String, String> szAlertsMap;
        String szAlertJSON;
        SendFileToJson sfj;//modified
        try {
            custLevelAlerts = DBUtilHelper.getMetrics_mapping_properties().getProperty("CustomerAlertsCategories").split(",");
            log.debug("Customer Alerts Categories==>>" + custLevelAlerts.toString());
            QueryExecuter qe;
            for (String alertCat : custLevelAlerts) {
                log.debug("Get alerts for==>>" + alertCat);
                qe = new QueryExecuter();
                szAlertsMap = qe.collectTicketDataForService(customerName, cCustomer, service, sz_startDate, sz_endDate, alertCat);
                szAlertJSON = ServiceThreadListener.modifyMapToJSONArray(szAlertsMap);
               sfj = new SendFileToJson(customerName, "", timestampselection, "", cCustomer, service, alertCat, szAlertJSON);//modified
               sfj = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in generateServiceLevelAlerts" + e.toString());
        } finally {
            custLevelAlerts = null;
            szAlertsMap = null;
            szAlertJSON = null;
            start_endarray = null;//modified
        }
    }
}
