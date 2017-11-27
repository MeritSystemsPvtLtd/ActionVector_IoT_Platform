/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 *
 * @author niteshc
 */
public class HTTPEventDispatcher {

    public static Logger logger = Logger.getLogger(HTTPEventDispatcher.class.getName());
    public static String frontcontrollerHTTPURL = null;
    static String temp[] = null;
    static HttpClient client = null;
    static HostConfiguration hf = null;
    static int timeout;

    public static boolean initialize(String httpURL, int httptimeout) {
        try {
            frontcontrollerHTTPURL = httpURL;
            timeout = httptimeout;
            temp = frontcontrollerHTTPURL.split(":");
            client = new HttpClient();
            hf = new HostConfiguration();
            hf.setHost(temp[0] + temp[1], Integer.parseInt(temp[2].split("/")[0]));
            client.setHostConfiguration(hf);
            //client.setConnectionTimeout(timeout);
            /*client.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
         client.getHttpConnectionManager().getParams().setSoTimeout(timeout);*/
            client.getParams().setParameter("http.socket.timeout", timeout * 1000);
            client.getParams().setParameter("http.connection.timeout", timeout * 1000);
            client.getParams().setParameter("http.connection-manager.timeout", new Long(timeout * 1000));
            client.getParams().setParameter("http.protocol.head-body-timeout", timeout * 1000);
        } catch (Exception e) {
            logger.error("Error while initializing HTTPEventDispatcher " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean sendMessage(String event) {
        BufferedReader br = null;
        PostMethod pMethod = null;
        try {
            pMethod = new PostMethod(frontcontrollerHTTPURL);
            logger.info("HTTP URL=" + frontcontrollerHTTPURL);
            logger.info("frontcontrollerHTTPURL event==>>" + event);
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
            logger.error("Error while sending HTTPEventDispatcher " + e.getMessage());
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
}
