/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.adapter.ganglia;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import org.apache.log4j.Logger;
/**
 *
 * @author gopal
 */
public class WatchDogEventConnectorTCP {

    static Logger logger = Logger.getLogger(WatchDogEventConnectorTCP.class);
    private static java.net.Socket requestSocket;
    private static PrintWriter toWatchDog;
    private static OutputStream out;
    private static boolean watchDogReady;
    private static String watchDogHost;
    private static int watchDogPort;
    private static String newline;

    private WatchDogEventConnectorTCP() {
    }

    public static void initialize() {

        if (!isWatchDogReady()) {
            watchDogReady = false;
            watchDogHost = AdapterProperties.getWatchDogHost();
            if (AdapterProperties.getWatchDogPort() !=0) {
                watchDogPort = AdapterProperties.getWatchDogPort();
            }
            //newline = CommandParameters.getNewline();

            if (connectToWatchDogHost()) {
                logger.debug("Connected to the WatchDog engine at host:" + watchDogHost + " and port: " + watchDogPort + "...");
            } else {
                logger.error("*** ERROR ****Could not connect to WatchDog engine at host:" + watchDogHost + " and port: " + watchDogPort + ". Will try again later...");
            }
        }
    }

    private static boolean connectToWatchDogHost() {
        //connect to the WatchDog engine socket
        logger.info("Connecting to WatchDog engine at host:" + watchDogHost + " and port: " + watchDogPort);
        try {
            if (!watchDogReady) {

                if (watchDogHost == null) {
                    logger.error("*** FATAL ERROR *** WatchDog host not specified in properties file. Will not send to WatchDog...");
                    watchDogReady = false;
                } else {
                    SocketChannel ssc = null;
                    ssc = SocketChannel.open();
                    InetSocketAddress isa = new InetSocketAddress(InetAddress.getByName(watchDogHost), watchDogPort);
                    requestSocket = ssc.socket();
                    requestSocket.connect(isa);
                    if (requestSocket == null) {
                        logger.error("socket is null");
                    }
                    out = requestSocket.getOutputStream();
                    toWatchDog = new PrintWriter(out, true);
                    watchDogReady = true;
                }

            }
        } catch (UnknownHostException ex) {
            logger.error("** CRITICAL ERROR ** Cannot connect to WatchDog engine : unknown host exception\n" + ex);
            watchDogReady = false;
        } catch (IOException ex) {
            logger.error("** CRITICAL ERROR ** Cannot connect to WatchDog engine : IO exception\n" + ex);
            watchDogReady = false;
        }
        return watchDogReady;
    }

    public static boolean isWatchDogReady() {
        return watchDogReady;
    }

    public static boolean sendMessage(String message) {
        //log.info("Sending this message to WatchDog: " + data);
        try {
            if(toWatchDog== null){
                logger.debug("writer Object is null; could send data to watchDogHost  " + watchDogHost + "@" + watchDogPort + "....");
                closeDataProcessor();
                connectToWatchDogHost();
            }
            if (isWatchDogReady()) {
                logger.info("Sending data " + message);
                toWatchDog.println(message);
                logger.info("Sending data successful");
                toWatchDog.flush();
                //toCep.write(message);
                //toCep.flush();
                logger.info("Message sent successfully!!");
                closeDataProcessor();
                return true;
            } else {
                logger.error("*** ERROR *** No connection to WatchDog engine...attempting to reconnect...this WatchDog event discarded!!");
                if (connectToWatchDogHost()) {
                    logger.info("Re-connected to the WatchDog engine at host:" + watchDogHost + " and port: " + watchDogPort + "...");
                } else {
                    logger.error("*** ERROR **** Still could not connect to WatchDog engine at host:" + watchDogHost + " and port: " + watchDogPort + ". Will try again later...");
                }
                return false; //in both cases false because we have not sent this CEP message
            }
            /*if(toWatchDog!= null){
            if(toWatchDog.checkError()) {
            closeDataProcessor();
            connectToWatchDogHost();
            }
            }
            if(!requestSocket.getChannel().isOpen()){
            closeDataProcessor();
            connectToWatchDogHost();
            }
            if ((!toWatchDog.checkError()) && !isWatchDogReady()) {
            logger.info("Sending data " + message);
            toWatchDog.println(message);
            System.out.println("Message:::::::::::::::"+message);
            logger.info("Sending data successful");
            toWatchDog.flush();
            }else{
            watchDogReady = false;
            connectToWatchDogHost();
            }*/

            
        } catch (Exception ex) {
            logger.error("** CRITICAL ERROR ** Connection to WatchDog engine broken : IO exception\n" + ex);
            watchDogReady = false;
            return false;
        }
    }
    
    public static void closeDataProcessor() {
        //log.debug("Closing Socket and PrintWriter for host : " + gszEventProcessorHost + "...........");
        try {
            try {
                if (toWatchDog != null) {
                    toWatchDog.close();
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
            /*try {
            if (requestSocket != null) {
            requestSocket.shutdownInput();
            }
            } catch (Exception ex) {
            ex.printStackTrace();
            }*/
            watchDogReady = false;
            toWatchDog = null;
            out = null;
            requestSocket = null;
            //dataStorage = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
