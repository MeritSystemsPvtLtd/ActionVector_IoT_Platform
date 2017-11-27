/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.tcp.eventlistener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import org.apache.log4j.Logger;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author anand kumar verma
 * ********************************************************************
 * Copyright message Software Developed by Merit Systems Pvt. Ltd., No. 42/1,
 * 55/c, Nandi Mansion, 40th Cross, Jayanagar 8th Block Bangalore - 560 070,
 * India Work Created for Merit Systems Private Limited All rights reserved
 *
 * THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES
 * NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED, DISTRIBUTED,
 * REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED,
 * COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD
 * SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 * *********************************************************************
 */
/**
 * EventReceiver class creates a continuous Process to Listen a port and to
 * receive events and to send these events to corresponding hosts
 */
public class EventReceiver {

    private static final Logger log = Logger.getLogger(EventReceiver.class.getName());
    private String eventReceiverPort;
    public static Timer timer = null;
    public static long totalEventCount = 0;

    /**
     * @param port numeric value of port as string at which EventReceiver listen
     */
    public EventReceiver(String port) {
        eventReceiverPort = port;
    }

    /**
     * call this method to recieve event data and send these to events to
     * corressponding hosts according to configuration.
     */
    public void startListener() {
        SSLSocket skt = null;
        InputStreamReader in = null;
        BufferedReader br = null;
        SSLServerSocket srvr = null;

        try {
            int serverPort = Integer.parseInt(eventReceiverPort);
            log.info("Server Port : " + serverPort);

            SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            srvr = (SSLServerSocket) sslserversocketfactory.createServerSocket(serverPort);
            int i = 0;
            TimerTask t = null;
            //Timer timer = null;
            timer = new Timer();
            while (true) {
                try {
                    skt = (SSLSocket) srvr.accept();
                    t = new EchoClient(skt, i++);
                    //timer.schedule(t, 2000 + i, 500);
                    timer.schedule(t, 50 + i, 500);
                } catch (Exception eee) {
                    eee.printStackTrace();
                }
            }
        } catch (Exception e) {
            log.error("Whoops! Error setting server socket\n");
            e.printStackTrace();
            try {
                br.close();
                in.close();
                skt.close();
                srvr.close();
                br = null;
                in = null;
                skt = null;
                srvr = null;
            } catch (Exception ex) {
                e.printStackTrace();
                br = null;
                in = null;
                skt = null;
                srvr = null;
            }
        }
    }
}
