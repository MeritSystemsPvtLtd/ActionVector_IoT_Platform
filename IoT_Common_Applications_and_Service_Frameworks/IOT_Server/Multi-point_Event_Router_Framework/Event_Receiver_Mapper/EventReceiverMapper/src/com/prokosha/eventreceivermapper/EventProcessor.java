/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.eventreceivermapper;

import java.io.OutputStream;
import java.io.PrintWriter;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.apache.log4j.Logger;
import com.prokosha.adapter.etl.ETLAdapter.ETLAdapter;
import java.net.Socket;
import java.nio.channels.SocketChannel;

/**
 *
 * @author anand kumar verma
 * ********************************************************************************
 * Copyright message * Software Developed by * Merit Systems Pvt. Ltd., * No.
 * 42/1, 55/c, Nandi Mansion, 40th Cross, Jayanagar 8th Block * Bangalore - 560
 * 070, India * Work Created for Merit Systems Private Limited * All rights
 * reserved * * THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT LAWS
 * AND TREATIES * NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED,
 * COPIED, * DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, *
 * EXPANDED, COLLECTED, COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED *
 * WITHOUT THE PRIOR WRITTEN CONSENT * ANY USE OR EXPLOITATION OF THIS WORK
 * WITHOUT AUTHORIZATION COULD SUBJECT * THE PERPETRATOR TO CRIMINAL AND CIVIL
 * LIABILITY. *
 * ********************************************************************************
 */
/**
 * EventProcessor maps a connection b/w current system and the server specified
 * by arguments
 */
public class EventProcessor {

    private static final Logger log = Logger.getLogger(EventProcessor.class.getName());
    private static final Logger evtLogger = Logger.getLogger("eventsLogger");
    private boolean entered = false;
    private int openPortCount = 0;
    /**
     * stores name of mapped host
     */
    String gszEventProcessorName;
    /**
     * stores IP address of mapped host
     */
    String gszEventProcessorHost;
    /**
     * stores port of mapped host
     */
    String gszEventProcessorPort;
    //private List<String> dataStorage = null;
    private Socket skt;
    private OutputStream out;
    private PrintWriter writer;
    private InetAddress inetAddress;
    private int iport;
    private static ETLAdapter szETLAdapter = new ETLAdapter();
    private static boolean ETLInitialized;

    static {
        if (!(szETLAdapter.initialize())) {
            System.out.println("Error in initializing ETL properties, exiting...");
        } else {
            ETLInitialized = true;
        }
    }
    public EventProcessor(){
        
    }

    /**
     * creates an object of EventProcessor
     *
     * @param eventSenderName name of mapped server
     * @param eventSenderHost IP address of mapped server
     * @param eventSenderPort port of mapped server
     */
    public EventProcessor(String eventProcessorName, String eventProcessorHost, String eventProcessorPort) {
        this.gszEventProcessorName = eventProcessorName;
        this.gszEventProcessorHost = eventProcessorHost;
        this.gszEventProcessorPort = eventProcessorPort;
        //dataStorage = new ArrayList();
        try {
            inetAddress = InetAddress.getByName(gszEventProcessorHost);
        } catch (Exception ex) {
            System.out.println("Exception : while initializing 'inetAddress'...............");
            ex.printStackTrace();
        }

        try {
            iport = Integer.parseInt(gszEventProcessorPort);
        } catch (Exception ex) {
            System.out.println("Exception : while initializing 'iport'............");
            ex.printStackTrace();
        }
    }

    /**
     * initialize connections to the mapped host.
     */
    public boolean init() {
        if (gszEventProcessorName.equalsIgnoreCase("etl")) {
            if (ETLInitialized) {
                return true;
            } else {
                return false;
            }
        }
        //System.out.println("Initializing Socket, PrintWriter for host : " + gszEventProcessorHost + "...............");
        FundamentalBlock:
        {
            SocketChannel ssc = null;
            try {
                ssc = SocketChannel.open();
                InetSocketAddress isa = new InetSocketAddress(InetAddress.getByName(gszEventProcessorHost), iport);
                skt = ssc.socket();
                skt.connect(isa);
                //ssc.configureBlocking(false); // active polling mode
                if (skt == null) {
                    System.out.println("socket is null");
                }
                out = skt.getOutputStream();
                //skt.getChannel().
                //skt.setOOBInline(true);
                //skt.setTcpNoDelay(true);
                skt.setReuseAddress(true);
                writer = new PrintWriter(out, true);
                entered = true;
                openPortCount++;
                System.out.println("Initialization of Socket, PrintWriter for host : " + gszEventProcessorHost + " successful");
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Initialization of Socket, PrintWriter for host : " + gszEventProcessorHost + " failed");
                System.out.println("Exception: connecting to " + gszEventProcessorHost + "@" + gszEventProcessorPort + " failed");
                entered = false;
                //System.out.println("Closing Socket and PrintWriter for host : " + gszEventProcessorHost + "...........");
                try {
                    entered = false;
                    if (skt != null) {
                        skt.close();
                    }
                    skt = null;
                } catch (Exception ex1) {
                    ex1.printStackTrace();
                }
                return false;
            }
        }
    }

    /**
     * sends event data to the host mapped by the current EventProcessor
     *
     * @param eventType Type of event
     * @param event String data of event
     */
    public synchronized void sendEvent(String eventType, String event) {
        String customerId = getCustomerID(event);
        if (gszEventProcessorName.equalsIgnoreCase("etl")) {
            try {
                //System.out.println("Sending event to ETL through method invocation");
                String status = szETLAdapter.dumpCEPEvent(event);
                String splitst[] = status.split(":");
                if ((status.equalsIgnoreCase("null:0") || (status.equalsIgnoreCase("0")))) {
                    System.out.println("Error in ETL insert metric, logging eventdata to reprocess later");
                    log.error("Error in ETL insert metric, logging eventdata to reprocess later");
                    evtLogger.info(event);
                }else if (splitst.length > 1) {
                    if (Integer.parseInt(splitst[1]) == -1) {
                        System.out.println("Error in ETL insert metric, logging eventdata to reprocess later");
                        log.error("Error in ETL insert metric, logging eventdata to reprocess later");
                        evtLogger.info(event);
                    }
                }
                System.out.println("ETL return status==>>" + status);
            } catch (Exception e) {
                System.out.println("Error in sending event to ETL through method invocation");
                e.printStackTrace();
            }
            return;
        }
        //System.out.println("gszEventProcessorName={" + gszEventProcessorName + "} and customerId{" + customerId + "} evaluating expression  == "  + "gszEventProcessorName.contains(['customerId']) > " + gszEventProcessorName.contains("[" + customerId + "]"));
        if (EventReceiverMapper.isForMultipleClient() && !gszEventProcessorName.contains("[" + customerId + "]")) {
            int unknowncustomerID = 0;
            int totalCustomers = EventReceiverMapper.gszArrProcessorGroup.length;
            for (String str : EventReceiverMapper.gszArrProcessorGroup) {
                if (!str.contains("[" + customerId + "]")) {
                    unknowncustomerID++;
                }
            }
            if (unknowncustomerID >= totalCustomers) {
                try {
                    throw new Exception("UnknownCustomerIDException: Unknown CustomerID '" + customerId + "' in Event-> " + event + " is found.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return;
        }
        //System.out.println("for eventType : " + eventType + " sender of name : " + gszEventProcessorName + " sending data : " + event);
        //System.out.println("sending data to " + gszEventProcessorHost + "@" + gszEventProcessorPort + "....");
        try {
            if (writer == null) {
                System.out.println("writer Object is null; could send data to " + gszEventProcessorHost + "@" + gszEventProcessorPort + "....");
                closeDataProcessor();
                init();
            }
            if (writer != null) {
                if (writer.checkError()) {
                    closeDataProcessor();
                    init();
                }
            }
            if (!skt.getChannel().isOpen()) {
                closeDataProcessor();
                init();
            }
            if ((!writer.checkError()) && entered) {
                System.out.println("Analysing status for " + gszEventProcessorHost + "@" + gszEventProcessorPort);
                System.out.println("\tskt.isClosed()         : " + skt.isClosed());
                System.out.println("\tskt.isConnected()      : " + skt.isConnected());
                System.out.println("\tskt.isOutputShutdown() : " + skt.isOutputShutdown());
                //writer.notify();
                //writer.flush();
                System.out.println("soket is reachable : " + skt.getInetAddress().isReachable(5000));
                System.out.println("writer.checkError() = " + writer.checkError());
                //skt.
                writer.println(event);
                /*if (!dataStorage.isEmpty()) {
                 System.out.println("sending " + dataStorage.size() + " data (previous data) to " + gszEventProcessorHost + "@" + iport);
                 for (String dataSt : dataStorage) {
                 writer.flush();
                 writer.println(dataSt);
                 }
                 }*/
                writer.flush();
                event = null;
            } else {
                System.out.println("Event not sent to " + gszEventProcessorHost + "@" + iport);
                //dataStorage.add(event);
                entered = false;
                init();
            }
            System.out.println("openPortCount = " + openPortCount);
            //closeDataSender();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isServerUP() {
        try {
            if (!writer.checkError()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * call this method to close all the connections to the current sender
     */
    public void closeDataProcessor() {
        //System.out.println("Closing Socket and PrintWriter for host : " + gszEventProcessorHost + "...........");
        try {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                if (skt != null) {
                    skt.shutdownInput();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                if (skt != null) {
                    skt.shutdownOutput();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                if (skt != null) {
                    skt.setSoTimeout(500);
                    skt.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            entered = false;
            writer = null;
            out = null;
            skt = null;
            //dataStorage = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCustomerID(String event) {
        String custToken = "CustomerID";

        if (event.contains("CustomerId")) {
            custToken = "CustomerId";
        } else if (event.contains("Customerid")) {
            custToken = "Customerid";
        } else if (event.contains("customerID")) {
            custToken = "customerID";
        } else if (event.contains("customerId")) {
            custToken = "customerId";
        } else if (event.contains("customerid")) {
            custToken = "customerid";
        }
        custToken = event.split(custToken)[1].trim();
        custToken = custToken.split(",")[0].trim();
        String customerID = custToken.replace("=", "").trim();
        return customerID;
    }
}
