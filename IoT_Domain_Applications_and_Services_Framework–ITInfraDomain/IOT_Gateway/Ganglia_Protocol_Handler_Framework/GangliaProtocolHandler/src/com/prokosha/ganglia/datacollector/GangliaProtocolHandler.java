/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.ganglia.datacollector;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;
import org.apache.log4j.Logger;
import java.io.InputStream;

/**
 *
 * @author Abraham
 */
public class GangliaProtocolHandler {

    private static final Logger log = Logger.getLogger(GangliaProtocolHandler.class.getName());
    private BufferedReader fromGmond;
    private java.net.Socket gmondSocket;
    private String gHost;
    private int gPort;
    private InputStream istream = null;
    private InputStreamReader istreamReader = null;
    private boolean isconnected = false;

    public GangliaProtocolHandler(String host, int port) {
        log.debug("Constructing  GangliaDataCollector...");
        gHost = host;
        gPort = port;
    }

    public String getgHost(){
        return gHost;
    }
    
    public int getgPort(){
        return gPort;
    }
    
    private boolean gmondConnect_old() {

        log.debug("Connecting to gmond socket port: " + gPort + " on host: " + gHost);
        try {
            gmondSocket = new Socket(gHost, gPort);
            fromGmond = new BufferedReader(new InputStreamReader(gmondSocket.getInputStream()));
            return true;
        } catch (Exception ex) {
            log.error("*** ERROR *** Cannot connect to gmond (" + gHost + ":" + gPort + ")!!\n" + ex);
            return false;
        }
    }

    private boolean gmondDisconnect() {
        //connect to the gmond socket
        log.debug("Disconnecting gmond (" + gHost + ":" + gPort + ").");
        try {
            closeGmondConnection();
            //gmondSocket.close();
            return true;
        } catch (Exception ex) {
            log.error("*** ERROR *** Cannot disconnect from gmond (" + gHost + ":" + gPort + ")!!\n" + ex);
            return false;
        }
    }

    public boolean download(StringBuffer xmlBuff) {

        //connect to gmond
        //if (!gmondConnect()) {
        if (!isGmondConnected()) {//modified by anand on 9th Aug
            log.error("*** ERROR *** Connecting to gmond (" + gHost + ":" + gPort + ") failed. Terminating download!!");
            return false;
        }

        //read full XML doc from gmond
        boolean more = true;
        String data;
        log.debug("Reading XML document from gmond (" + gHost + ":" + gPort + ")...");
        while (more) {//read the full ganglia XML doc
            try {
                data = fromGmond.readLine();
                if (data == null) {
                    more = false;
                } else {
                    xmlBuff.append(data);
                }
            } catch (Exception ex) {
                log.error("*** ERROR *** Reading from gmond (" + gHost + ":" + gPort + ") failed. Terminating download!!" + ex);
                gmondDisconnect();
                return false;
            }
        }

        //disconnect gmond and return
        return gmondDisconnect();
    }

    private boolean gmondConnect() {

        log.debug("Connecting to gmond socket port: " + gPort + " on host: " + gHost);
        try {
            gmondSocket = new Socket(gHost, gPort);
            istream = gmondSocket.getInputStream();
            istreamReader = new InputStreamReader(istream);
            fromGmond = new BufferedReader(istreamReader);
            isconnected = true;
            return true;
        } catch (Exception ex) {
            log.error("*** ERROR *** Cannot connect to gmond (" + gHost + ":" + gPort + ")!!\n" + ex);
            isconnected = false;
            return false;
        }
    }

    private void closeGmondConnection() {

        log.debug("Connecting to gmond socket port: " + gPort + " on host: " + gHost);
        try {
            isconnected = false;
            if (fromGmond != null) {
                fromGmond.close();
            }
            if (istreamReader != null) {
                istreamReader.close();
            }
            if (istream != null) {
                istream.close();
            }
            if (gmondSocket != null && !gmondSocket.isClosed()) {
                gmondSocket.close();
            }
        } catch (Exception ex) {
            log.error("*** ERROR *** Cannot connect to gmond (" + gHost + ":" + gPort + ")!!\n" + ex);
            isconnected = false;
        }
    }

    private boolean isGmondConnected() {
        if (gmondSocket == null || istream == null || istreamReader == null || fromGmond == null) {
            isconnected = false;
        } else {
            if (gmondSocket.isClosed() || !gmondSocket.isConnected() || gmondSocket.isInputShutdown() || gmondSocket.isOutputShutdown()) {
                isconnected = false;
                closeGmondConnection();
            }
        }
        if(!isconnected){
            gmondConnect();
        }
        return isconnected;
    }
}

