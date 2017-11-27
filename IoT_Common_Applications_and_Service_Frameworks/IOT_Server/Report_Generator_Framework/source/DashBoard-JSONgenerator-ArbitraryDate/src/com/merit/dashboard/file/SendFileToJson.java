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
package com.merit.dashboard.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;

import com.prokosha.dbconnection.ConnectionDAO;
import com.merit.dashboard.DBUtil.DBUtilHelper;
import com.merit.dashboard.DateGenerator;
import com.merit.dashboard.util.MetricUOM;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * *************************************************************************************************
 * This Class is designed to send JSON File in appropriate Directory, That will
 * be useful for Generating Chart
 *
 * @author satya
 * **************************************************************************************************
 */
public class SendFileToJson {

    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SendFileToJson.class);
    //static Calendar cal = Calendar.getInstance();
    static Calendar cal = DateGenerator.getCurrentTime();
    static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
    private String resourceType = null;
    private String jsonFileName = null;
    private String dataString = null;
    private String cCustomer = null;
    private String service = null;
    private String customer = null;

    /**
     * *************************************************************************************************
     *
     * @param customer name of Directory-timestampselection-resourceType, where
     * all JSON file should be placed
     * @param timestampselection Inside customer directory having different
     * sub-Directory of timestamp like (hour, day, week, month, year). Here
     * timestampselection-resourceType is telling where all JSON file should be
     * placed
     * @param resourceType Inside timestamp directory having different
     * sub-directory of resourcetype like (Desktop,server,
     * DataBase,Network,JVM). Here resourceType is telling where all JSON file
     * should be placed
     * @param jsonFileName Name of the JSON
     * @param dataString Data to be stored in JSON
     *
     * **************************************************************************************************
     */
    public SendFileToJson(String customer, String selection, String timestampselection, String resourceType, String cCustomer,
            String service, String jsonFileName, String dataString) {
        this.resourceType = resourceType;
        this.jsonFileName = jsonFileName;
        this.dataString = dataString;
        this.cCustomer = cCustomer;
        this.service = service;
        this.customer = customer;
        try {
            Connection con = ConnectionDAO.getConnection(customer);
            if (con == null) {
            } else {
                injectMetricUOM();
                sendDataToFile(timestampselection, selection);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in SendFileToJson" + e.toString());
        }

    }

    public void injectMetricUOM() throws IOException, ParseException {

        JSONParser parser = new JSONParser();
        BufferedReader br = null;
        InputStreamReader inStrRed = null;
        InputStream inStr = null;
        try {
            log.info("in injectMetricUOM method");
            HashMap map = new HashMap();
            JSONArray jsonArray = (JSONArray) parser.parse(dataString);
            String mUnit = "";
            HttpURLConnection con = null;//
            JSONArray list;//
            String resourceNames1;//
            String requestURL;//
            JSONObject obj;//
            JSONArray resourceNames;//
            URL url;//
            String res;//
            String jsonString;//
            JSONObject jObject;//
            for (int l = 0; l < jsonArray.size(); l++) {

                log.info("in jsonArray size " + jsonArray.size());

                obj = (JSONObject) jsonArray.get(l);

                resourceNames = (JSONArray) obj.get("ResourceNames");
                list = new JSONArray();
                if (resourceNames != null) {
                    for (int k = 0; k < resourceNames.size(); k++) {
                        resourceNames1 = (String) resourceNames.get(k);
                        log.info("resourceNames " + resourceNames1);
                        if (resourceNames1.equalsIgnoreCase("Default")) {
                            mUnit = "";
                        } else {
                            if (map.containsKey(resourceNames1)) {
                                log.info("resourceNames1 in map :" + resourceNames1);

                                mUnit = (String) map.get(resourceNames1);
                                //list.add(resourceUOM);
                            } else {
                                /*requestURL = DBUtilHelper.MetricUOMURL + "?customer="
                                 + cCustomer + "&service=" + service + "&metrictype=" + resourceNames1 + "&resourcetype="
                                 + resourceType;
                                 url = new URL(requestURL);
                                 log.info("URL " + requestURL);
                                 con = (HttpURLConnection) url.openConnection();
                                 con.setDoOutput(true);
                                 inStr = con.getInputStream();
                                 inStrRed = new InputStreamReader(inStr);
                                 br = new BufferedReader(inStrRed);

                                 while ((res = br.readLine()) != null) {

                                 jsonString = res.replaceAll("null\\(\'", "");
                                 jsonString = jsonString.replaceAll("\'\\)", "");
                                 if (jsonString != null) {

                                 if ((!(jsonString.equalsIgnoreCase("null"))) && (!(jsonString.equalsIgnoreCase("")))) {
                                 jObject = (JSONObject) parser.parse(jsonString);

                                 mUnit = (String) jObject.get("MetricUOM");
                                 }
                                 }

                                 }*/
                                mUnit = MetricUOM.getConfValue(customer, cCustomer, service, "Default", resourceType, resourceNames1);
                                if (mUnit != null) {
                                    if (mUnit.equals("")) {
                                    } else {
                                        mUnit = "(" + mUnit + ")";
                                    }
                                }
                            }

                        }
                        list.add(mUnit);

                        obj.put("MetricUOM", list);
                        //  map.clear();
                        //list.clear();
                        if (br != null) {
                            br.close();
                            br = null;
                        }
                        if (inStrRed != null) {
                            inStrRed.close();
                            inStrRed = null;
                        }
                        inStr = null;
                        requestURL = null;
                        if (con != null) {
                            con.disconnect();
                            con = null;
                        }
                    }

                }
            }
            map = null;
            dataString = jsonArray.toJSONString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    /**
     * *************************************************************************************************
     * This method is sending JSON File Periodically to Specific defined Path.
     *
     * @param customer name of Directory, where all JSON file should be placed
     * @param timestampselection Inside customer directory different
     * sub-Directory of timestamp like (hour, day, week, month, year). Here
     * timestampselection is telling where all JSON file should be placed
     * **************************************************************************************************
     */
    public void sendDataToFile(String timestampselection, String selection) {
        String szProjectName = DBUtilHelper.getMetrics_mapping_properties().getProperty("projectName");
        //String tomcat_home = System.getProperty("catalina.base");
        String tomcat_home = DBUtilHelper.dashBoardJSONPATH;
        String file_Path = "";
        String szDate = sdf.format(cal.getTime());
        System.out.println("currentTime in sendDataToFile=" + cal.toString());
        System.out.println("szDate in sendDataToFile=" + szDate);
        if (resourceType.equalsIgnoreCase("")) {
            //file_Path = tomcat_home + File.separator + "webapps" + File.separator + szProjectName + File.separator + customer + File.separator + timestampselection + File.separator + jsonFileName + ".json";
            /*this line replace with below if else block for watchdogalert.json to be saved under date directory
             * file_Path = tomcat_home + File.separator + szProjectName + File.separator + customer
                        + File.separator + szDate + File.separator + timestampselection;
             */
            if (jsonFileName.equalsIgnoreCase("watchdogalert")) {
                file_Path = tomcat_home/*+ File.separator + "webapps"*/ + File.separator + szProjectName + File.separator + customer
                        + File.separator + szDate;
            } else {
                file_Path = tomcat_home/*+ File.separator + "webapps"*/ + File.separator + szProjectName + File.separator + customer
                        + File.separator + szDate + File.separator + timestampselection;
            }
            if (cCustomer != null) {
                file_Path += File.separator + cCustomer;
            }
            if (service != null) {
                file_Path += File.separator + service;
            }
            file_Path += File.separator + jsonFileName + ".json";
        } else {
            if (selection.equalsIgnoreCase("")) {
                //file_Path = tomcat_home + File.separator + "webapps" + File.separator + szProjectName + File.separator + customer + File.separator + timestampselection + File.separator + resourceType + File.separator + resourceType + jsonFileName + ".json";
                file_Path = tomcat_home + /*File.separator + "webapps" +*/ File.separator + szProjectName + File.separator + customer
                        + File.separator + szDate + File.separator + timestampselection;
                if (cCustomer != null) {
                    file_Path += File.separator + cCustomer;
                }
                if (service != null) {
                    file_Path += File.separator + service;
                }
                file_Path += File.separator + resourceType + File.separator + resourceType + jsonFileName + ".json";
            } else {
                //file_Path = tomcat_home + File.separator + "webapps" + File.separator + szProjectName + File.separator + customer + File.separator + timestampselection + File.separator + resourceType + File.separator + selection + File.separator + resourceType + jsonFileName + ".json";
                file_Path = tomcat_home + /*File.separator + "webapps" +*/ File.separator + szProjectName + File.separator + customer
                        + File.separator + szDate + File.separator + timestampselection;
                if (cCustomer != null) {
                    file_Path += File.separator + cCustomer;
                }
                if (service != null) {
                    file_Path += File.separator + service;
                }
                file_Path += File.separator + resourceType + File.separator + selection + File.separator + resourceType
                        + jsonFileName + ".json";
            }
        }
        while (file_Path.contains(File.separator + File.separator)) {
            file_Path = file_Path.replaceAll(File.separator + File.separator, File.separator);
        }
        File file = new File(file_Path);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
            log.error("Error in sendDataToFile" + ex.toString());
        }
        try {
            FileOutputStream fos = new FileOutputStream(file, false);
            fos.write(dataString.getBytes());
            fos.close();
            String szFileContent = "";
            if (dataString.length() < 5) {
                szFileContent = " File is Empty::" + dataString + ";\n";
            }
            log.info(szFileContent + "\n File Sent Successfully:::" + file_Path);
            System.out.println(szFileContent + "\n File Sent Successfully:::" + file_Path);
            file = null;
            fos = null;
            Date date = new Date();
            log.info(" System Time:::::::::::::" + date.toString() + "\n");
            date = null;
            dataString = null;
        } catch (Exception e) {
            log.error("DashBoard SendFileToJson Writing into :" + file_Path + "\n" + e.getMessage());
            e.printStackTrace();

        } finally {
            dataString = null;
            file_Path = null;
        }


    }
}
