/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.froniusadapter;

import com.prokosha.froniusadapter.configuration.AdapterProperties;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

/**
 *
 * @author rekhas
 */
class WatchDogEventSender extends Thread {

    public static Logger logger = Logger.getLogger(WatchDogEventSender.class.getName());
    public static String watchDogURL = AdapterProperties.getWatchDogURL();
    static HttpClient client = new HttpClient();
    static HostConfiguration hf = new HostConfiguration();

    public static boolean sendMessage(String event) {
        BufferedReader br = null;
        watchDogURL = AdapterProperties.getWatchDogURL();
        PostMethod pMethod = new PostMethod(watchDogURL);
        String temp[];
        try {
            logger.info("WD URL=" + watchDogURL);
            temp = watchDogURL.split(":");
            hf.setHost(temp[0] + temp[1], Integer.parseInt(temp[2].split("/")[0]));
            client.setHostConfiguration(hf);
            logger.info("WD event==>>" + event);
            System.out.println("post method ==>>" + pMethod);
            pMethod.addParameter("event", event);
            client.executeMethod(pMethod);
            br = new BufferedReader(new InputStreamReader(pMethod.getResponseBodyAsStream()));
            String res;
            while ((res = br.readLine()) != null) {
                System.out.println(res);
            }
            res = null;
            return true;
        } catch (Exception e) {
            logger.error("Error while sending watchdog event" + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
            pMethod = null;
        }
    }

    public void run() {
        boolean evtSent;
        while (true) {
            try {
                evtSent = sendMessage("type=InverterAdapter:" + AdapterProperties.getcCustomerID() + ":" + AdapterProperties.getInstallationID() + ",status=Alive");
                if (evtSent) {
                    logger.info("watchdog event sent successfully");
                } else {
                    logger.info("watchdog event not sent ");
                }
                Thread.sleep(AdapterProperties.getWatchDogSleep());
            } catch (Exception e) {
                logger.error("Error in WatchDogEventSender sleep==" + e.getMessage());
                e.printStackTrace();
                break;
            }
        }
    }
}
