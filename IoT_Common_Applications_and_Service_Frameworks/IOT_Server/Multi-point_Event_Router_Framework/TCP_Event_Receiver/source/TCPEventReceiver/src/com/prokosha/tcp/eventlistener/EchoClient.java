/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.tcp.eventlistener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.prokosha.eventreceivermapper.EventReceiverMapper;
import javax.net.ssl.SSLSocket;
import java.util.TimerTask;
import org.apache.log4j.Logger;

/**
 *
 * @author anandkv
 */
public class EchoClient extends TimerTask {

    public static long clientMinLife = 1800;//in seconds
    private int clientIndex = 0;
    private long eventCount = 0;
    private SSLSocket skt = null;
    private BufferedReader br = null;
    private InputStream instrm = null;
    private long totalWait = 0;
    private InputStreamReader inrdr = null;
    private String temp = "";
    private String eventype = "";
    private int dataCount = 0;
    private static final Logger log = Logger.getLogger(EchoClient.class.getName());
    private int timeoutCount = 0;

    public EchoClient(SSLSocket skt, int clientIndex) {
        try {
            this.skt = skt;
            this.clientIndex = clientIndex;
            //System.out.println("got one client at index " + clientIndex);
            log.info("got one client at index " + clientIndex);
            //System.out.print("\nServer at localhost@"+ serverPort + " is ready to receive data from new point!\n");            
            //log.info("client " + clientIndex + " : toString()::" + skt.toString());
            //System.out.println("client " + clientIndex + " : toString()::" + skt.toString());
            //log.info("client " + clientIndex + " : InetAddress::" + skt.getInetAddress().getHostAddress() + "::" + skt.getInetAddress().getHostName());
            //System.out.println("client " + clientIndex + " : toString()::" + skt.getInetAddress().getHostAddress() + "::" + skt.getInetAddress().getHostName());
            //log.info("client " + clientIndex + " : RemoteSocketAddress::" + skt.getRemoteSocketAddress().toString());
            //System.out.println("client " + clientIndex + " : toString()::" + skt.getRemoteSocketAddress().toString());
            instrm = skt.getInputStream();
            inrdr = new InputStreamReader(instrm);
            br = new BufferedReader(inrdr);
            dataCount = 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            //System.gc();
            //System.out.println("In run() of client " + clientIndex);
            log.info("In run() of client " + clientIndex);
            skt.setSoTimeout(210000);

            //if (br.ready()) {
            temp = br.readLine();
            if (temp != null) {
                dataCount++;
                ++EventReceiver.totalEventCount;
                log.info("From Cleint[" + clientIndex + "]  data[" + ++eventCount + "] received = " + temp);
                log.info("totalEventCount = " + EventReceiver.totalEventCount + "\t" + "temp.length = " + temp.length());
                //log.info("From Cleint[" + clientIndex + "]  data[" + eventCount + "] received = " + temp);
                processData(temp);
                totalWait = 0;
                timeoutCount = 0;
            } else {
                skt.close();
                br.close();
                inrdr.close();
                instrm.close();
                //System.out.println("Calling TimerTask.cancel");
                log.info("Calling TimerTask.cancel");
                this.cancel();
                EventReceiver.timer.purge();
                //timer.cancel();
                //timer = null;
            }
            /*} else {
             totalWait += 1;
             }*/
            log.info("totalWait::" + totalWait);
            if (totalWait > clientMinLife) {
                skt.close();
                br.close();
                inrdr.close();
                instrm.close();
                //System.out.println("Calling TimerTask.cancel");
                log.info("Calling TimerTask.cancel");
                this.cancel();
                EventReceiver.timer.purge();
                //timer.cancel();
                //timer = null;
            }
        } catch (java.net.SocketTimeoutException ste) {
            timeoutCount++;
            log.error("Error in client " + clientIndex + " : SocketTimedOut " + timeoutCount + "th time");
            if (timeoutCount >= 1) {
                log.error("Closing socket for client " + clientIndex);
                try {
                    if (br != null) {
                        br.close();
                    }
                    br = null;
                    if (inrdr != null) {
                        inrdr.close();
                    }
                    inrdr = null;
                    if (instrm != null) {
                        instrm.close();
                    }
                    instrm = null;
                    if (skt != null) {
                        skt.close();
                    }
                    skt = null;
                    temp = null;
                    eventype = null;
                    dataCount = 0;
                    //System.out.println("Calling TimerTask.cancel after exception");
                    log.info("Calling TimerTask.cancel");
                    this.cancel();
                    EventReceiver.timer.purge();
                    //timer.cancel();
                    //timer = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            log.error("Error in client " + clientIndex + " : " + ex.toString());
            ex.printStackTrace();
            try {
                if (br != null) {
                    br.close();
                }
                br = null;
                if (inrdr != null) {
                    inrdr.close();
                }
                inrdr = null;
                if (instrm != null) {
                    instrm.close();
                }
                instrm = null;
                if (skt != null) {
                    skt.close();
                }
                skt = null;
                temp = null;
                eventype = null;
                dataCount = 0;
                //System.out.println("Calling TimerTask.cancel after exception");
                log.info("Calling TimerTask.cancel");
                this.cancel();
                EventReceiver.timer.purge();
                //timer.cancel();
                //timer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * send Event data to the corresponding server
     *
     * @param data event data as input parameter
     */
    //private void processData(String data) {
    public void processData(String data) {
        //log.info("\n\tgot the data : " + data);
        eventype = data.split(",")[0].trim();
        eventype = eventype.split("=")[1].trim();
        EventReceiverMapper.addressMap.sendEvent(eventype, data);
    }
}