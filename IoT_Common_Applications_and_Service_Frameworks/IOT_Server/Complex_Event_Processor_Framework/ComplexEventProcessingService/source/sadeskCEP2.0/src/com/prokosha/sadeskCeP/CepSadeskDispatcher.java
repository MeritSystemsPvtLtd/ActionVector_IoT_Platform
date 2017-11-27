/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.sadeskCeP;

import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import org.apache.log4j.Logger;
import java.io.*;
import java.net.MalformedURLException;

/**
 *
 * @author Bimal
 */
public class CepSadeskDispatcher {

    private static final Logger log = Logger.getLogger(CepSadeskDispatcher.class.getName());
    private static int counter = 0;
    int thrIndx;

    /*
     * EVENT FORMAT::::::: ServerSwapUsed = 4000.0 MemcLatency = 8.03E-4
     * MemcPort = 11211 ServerLoad5 = 0.03 ServerLoad15 = 0.02 ServerSwapMax =
     * 4000.0 EventSymptoms = Latency in WARNING range [>0.0005s] and server
     * load in WARNING range [>2.0] EventDesc = Memcached Performance
     * Degradation EventType = Correlated Event ServerLoad1 = 3.0 MemcServer =
     * 10.50.11.18 MemcClient = Bsefeed
     */
 /*
     * public static void main(String[] args) { HashMap hm = new HashMap();
     *
     * hm.put("ServerSwapUsed","4000.0"); hm.put("MemcLatency","8.03E-4");
     * hm.put("MemcPort","11211"); hm.put("ServerLoad5","0.03");
     * hm.put("ServerLoad5","0.02"); hm.put("ServerLoad5","0.03");
     * hm.put("EventType","Correlated Event"); hm.put("EventDesc",
     * "Memcached Performance Degradation"); hm.put("EventSymptoms",
     * "Latency in WARNING range [>0.0005s] and server load in WARNING range [>2.0]"
     * ); dispatchEvents(hm); }
     */
    public CepSadeskDispatcher() {
    }

    public void dispatchEvents(HashMap theEvent) throws IOException {
        StringBuffer context = new StringBuffer();
        String contextualtext = null;
        String HostName = null;
        String sadeskUrl = CepProperties.getProperty("sadeskUrl");
        try {
            thrIndx = counter;
            log.info("CepSadeskDispatcher thread start::" + thrIndx);
            counter++;
            // Get a set of the entries
            Set set = theEvent.entrySet();
            // Get an iterator
            Iterator i = set.iterator();
            // Display/Assign elements

            while (i.hasNext()) {
                Map.Entry me = (Map.Entry) i.next();
                log.info("me.getKey()::" + me.getKey());
                contextualtext = (me.getKey() + "=" + me.getValue().toString());
                if (i.hasNext()) {
                    contextualtext += ",";
                }
                context.append(contextualtext);
            }
            contextualtext = context.toString();
            log.info("contextualtext:::" + contextualtext);
            log.info("HostName::::::::::::::::::" + HostName);
            //sadeskUrl="http://"+szCustID+"."+sadeskUrl;
            //sadeskUrl = "http://" + szCustID + sadeskUrl;
            log.info("CEPDispather::SA-Desk URL=" + sadeskUrl);
            String postCEPEvent = sendPostRequest(sadeskUrl, "CEPEvent=" + contextualtext);
            log.debug("postCEPEvent==>>" + postCEPEvent);

        } catch (Exception e) {
            log.debug("========= ERROR ===============" + e.getStackTrace());
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static String sendPostRequest(String URL, String data) {

        String result = null;

        try {

            // Send the request
            log.info("================== Sending the URL =================");
            URL url = new URL(URL);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

            //write parameters
            writer.write(data);
            writer.flush();

            // Get the response
            StringBuffer answer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                answer.append(line);
            }
            writer.close();
            reader.close();

            result = answer.toString();
            answer = null;

        } catch (MalformedURLException ex) {
            // ex.printStackTrace();
            log.error("****************** ERROR ****************", ex);
        } catch (IOException ex) {
            // ex.printStackTrace();
            log.error("****************** ERROR ****************", ex);
        }
        return result;
    }

    protected void finalize() throws Throwable {
        log.info("CepSadeskDispatcher thread end::" + thrIndx);
        super.finalize();

    }
}
