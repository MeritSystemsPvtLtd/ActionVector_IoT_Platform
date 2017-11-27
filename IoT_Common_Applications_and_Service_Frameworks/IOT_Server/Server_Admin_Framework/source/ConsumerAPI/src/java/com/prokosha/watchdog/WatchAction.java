package com.prokosha.watchdog;

import AV_Action.AV_WatchDog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author gopal
 */
public class WatchAction {

    private static final Logger log = Logger.getLogger(WatchAction.class.getName());
    private static boolean bsendEvent = false;
    private static boolean execmd = false;
    private static ArrayList<WatchConfigProps> watchProps = null;
    private static String szResourceName = null;
    private static String szResourceId = null;
    private static String szResourceType = null;
    private static String szInstanceId = null;
    private static String szInstancePort = null;
    private static WatchConfigProps wcp = null;
    private static String[] comparEventArr = null;
    private static Long curTime = null;
    private static Map<String, Long> mp = null;
    private static Map<String, Integer> upTimeMap = null;
    private static int iAvailability;
    //private static int iAgentDown;
    private static String customerId;

    /*
     * watchAction method is called from StartWatch to update the time interval
     * It maintains a Map<AdapterInstance,TimeStamp> and updates accordingly
     * upon every client(adpter) request
     */
    public static void watchAction(String input) {

        try {
            comparEventArr = input.split(",");

            String[] tempeventArr = null;
            for (String str : comparEventArr) {
                if (comparEventArr != null) {
                    tempeventArr = str.split("=");
                    log.info("tempeventArr[0]=" + tempeventArr[0]);
                    log.info("tempeventArr[1]=" + tempeventArr[1]);
                    if (tempeventArr[0] != null && tempeventArr[0].equalsIgnoreCase("Type") && tempeventArr[1] != null) {
                        log.info("Im in if 2");
                        if (ConfigValues.eventComparMap.containsKey(tempeventArr[1])) {
                            log.info("Im in if 3");
                            curTime = new Date().getTime();
                            ConfigValues.eventComparMap.put(tempeventArr[1], curTime);
                            log.info("Timestamp updated successfully for type " + tempeventArr[1]);

                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("error while updateing timestamp" + e);

        }
    }

    public static boolean sendWatchEvent() {
        try {
            //map which contains adapter instance as key and timestamp as value to compare time intervals
            //example ResourceName:Host:port/instanceName,timestamp
            mp = ConfigValues.getEventComparMap();
            upTimeMap = ConfigValues.getUpTimeMap();
            customerId = ConfigValues.getCustomerId();
            ConfigValues.getWatchProps();
            if (watchProps == null) {
                watchProps = ConfigValues.getWatchProps();

                Iterator it = watchProps.iterator();
                while (it.hasNext()) {

                    wcp = new WatchConfigProps();
                    wcp = (WatchConfigProps) it.next();
                    szInstanceId = wcp.getInstanceId();
                    szResourceId = wcp.getResourceId();
                    szResourceName = wcp.getResourceName();
                    szResourceType = wcp.getResourceType();
                    szInstancePort = wcp.getInstancePort();
                    curTime = new Date().getTime();

                    StringBuffer timeDiff = WatchAction.getMonitorTime(customerId, szInstanceId, szInstancePort);
                    String[] time = timeDiff.toString().split("-");
                    String sunStartTime = time[0];
                    String sunEndTime = time[1];

                    DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    String currTime = dateFormat.format(new Date()).toString();

                    boolean flag = new WatchAction().isTimeBetweenTwoTime(sunStartTime, sunEndTime, currTime);

                    if (curTime > 0) {
                        Long tempTime = (Long) mp.get(szResourceName + ":" + szResourceId + ":" + szInstancePort);
                        log.info("curTime::" + curTime);
                        Long lng = (curTime.longValue() - (tempTime));

                        //checking the current timestamp against stored timestamp
                        if ((lng > ConfigValues.getIsleepInterval()) && (flag)) {

                            upTimeMap.put(szResourceName + ":" + szResourceId + ":" + szInstancePort, 1);
                            iAvailability = 0;
                            execmd = WatchAction.executeCmd(szResourceId, szResourceType, szResourceName, szInstanceId, szInstancePort, curTime.longValue(), iAvailability, customerId);
                            log.info(szResourceName + ":" + szResourceId + ":" + szInstancePort + " is in invalid state::" + iAvailability);
                        } else {

                            //iAgentDown = upTimeMap.get(szResourceName + ":" + szResourceId + ":" + szInstancePort);
                            //if(iAgentDown==1){
                            iAvailability = 1;
                            execmd = WatchAction.executeCmd(szResourceId, szResourceType, szResourceName, szInstanceId, szInstancePort, curTime.longValue(), iAvailability, customerId);
                            log.info(szResourceName + ":" + szResourceId + ":" + szInstancePort + " is in valid state::" + iAvailability);
                            upTimeMap.put(szResourceName + ":" + szResourceId + ":" + szInstancePort, 0);
                            //}

                        }
                    }
                    wcp = null;
                }
                watchProps = null;
                mp = null;
            } else {
                log.info("Invalid data");
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            log.debug("Exception::" + e);
        } finally {
        }
        return execmd;
    }

    public static boolean executeCmd(String szResourceId, String szResourceType, String szResourceName, String szInstanceId, String szInstancePort, Long lTimeStamp, int iAvailability, String customerId) {

        try {

            lTimeStamp = lTimeStamp / 1000;
            log.info("send event");
            bsendEvent = SendWatchEvent.sendWatchEvent(szResourceId, szResourceType, szResourceName, szInstanceId, szInstancePort, lTimeStamp, iAvailability, customerId);
            log.info("bsendEvent::" + bsendEvent);
            if (bsendEvent) {
                log.info("event sent successfully");
                log.info(szResourceName + " event sent successfully");
            } else {
                log.info("unable to send event " + szResourceName);
                log.info("unable to send event " + szResourceName);
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(WatchAction.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        }
        return false;
    }

    public static StringBuffer getMonitorTime(String domainName, String customerID, String service) throws IOException {
        StringBuffer betTime = new StringBuffer();

        URL url = null;
        HttpURLConnection conn = null;
        String sunStartTime = null;
        String sunEndTime = null;
        BufferedReader br = null;
        InputStreamReader inpred = null;
        JSONParser parser = null;
        String pValue = "ParamValue";

        try {

            parser = new JSONParser();
            String response = null;
            String sunLightStart = "SunLightStart";
            String sunLightEnd = "SunLightEnd";
            String cloudAPIURL = ConfigValues.getResConfigURL();
            cloudAPIURL = domainName + cloudAPIURL;
            url = new URL("http://" + cloudAPIURL + "/GetJSONResourceConfig?customer=" + customerID + "&service=" + service
                    + "&subservice=Default&resource=&paramname=" + sunLightStart);
            System.out.println("url for start time ==>" + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            inpred = new InputStreamReader(conn.getInputStream());
            br = new BufferedReader(inpred);
            while ((response = br.readLine()) != null) {
                String jsonString = response.replaceAll("null\\(\'\\[", "");
                jsonString = jsonString.replaceAll("]\'\\)", "");
                System.out.println("jsonString==>" + jsonString);
                JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
                sunStartTime = (String) jsonObject.get(pValue);
            }
            inpred = null;
            br = null;
            conn.disconnect();
            url = null;
            response = null;
            url = new URL("http://" + cloudAPIURL + "/GetJSONResourceConfig?customer=" + customerID + "&service=" + service
                    + "&subservice=Default&resource=&paramname=" + sunLightEnd);
            System.out.println("url for end time ==>" + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            inpred = new InputStreamReader(conn.getInputStream());
            br = new BufferedReader(inpred);
            while ((response = br.readLine()) != null) {
                String jsonString = response.replaceAll("null\\(\'\\[", "");
                jsonString = jsonString.replaceAll("]\'\\)", "");
                System.out.println("jsonString==>" + jsonString);
                JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
                sunEndTime = (String) jsonObject.get(pValue);
            }
            inpred = null;
            br = null;
            conn.disconnect();
            url = null;
            betTime.append(sunStartTime + "-" + sunEndTime);
            System.out.println("Time Between " + betTime);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inpred != null) {
                inpred.close();
            }
            if (br != null) {
                br.close();
            }
            if (parser != null) {
                parser = null;
            }
        }
        return betTime;
    }

    public boolean isTimeBetweenTwoTime(String sunStartTime, String sunEndTime, String currTime) {
        boolean valid = false;
        try {
            System.out.println("sunstarttime:"+sunStartTime);
            System.out.println("sunendtime:"+sunEndTime);
            System.out.println("currtime:"+currTime);
            
            String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9])$";
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

            if (sunStartTime.matches(reg) && sunEndTime.matches(reg) && currTime.matches(reg)) {
                System.out.println("inside if all times matches regex");
                // Start Time
                Date startTime = sdf.parse(sunStartTime);
                Calendar startCalendar = Calendar.getInstance();
                startCalendar.setTime(startTime);

                // Current Time
                Date currentTime = sdf.parse(currTime);
                Calendar currentCalendar = Calendar.getInstance();
                currentCalendar.setTime(currentTime);

                // End Time
                Date endTime = sdf.parse(sunEndTime);
                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(endTime);

                if (currentTime.compareTo(endTime) < 0) {
                    currentCalendar.add(Calendar.DATE, 1);
                    currentTime = currentCalendar.getTime();
                }
                if (startTime.compareTo(endTime) < 0) {
                    startCalendar.add(Calendar.DATE, 1);
                    startTime = startCalendar.getTime();
                }
                if (currentTime.before(startTime)) {
                    System.out.println("Monitoring Time " + currTime + " does not lies b/w " + sunStartTime + " and " + sunEndTime);
                    valid = false;
                } else {
                    if (currentTime.after(endTime)) {
                        endCalendar.add(Calendar.DATE, 1);
                        endTime = endCalendar.getTime();
                    }
                    if (currentTime.before(endTime)) {
                        System.out.println("Monitoring Time " + currTime + " lies b/w " + sunStartTime + " and " + sunEndTime);
                        valid = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Not in proper fomat HH:mm");
            return false;
        }
        return valid;
    }
}
