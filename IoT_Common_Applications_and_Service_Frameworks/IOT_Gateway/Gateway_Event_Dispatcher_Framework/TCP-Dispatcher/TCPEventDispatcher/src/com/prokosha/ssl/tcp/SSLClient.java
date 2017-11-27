/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.ssl.tcp;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.UnknownHostException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.apache.log4j.Logger;

/**
 *
 * @author rekha
 */
public class SSLClient {

    private final Logger log = Logger.getLogger(SSLClient.class.getName());
    private SSLSocket requestSocket;
    //private BufferedWriter toSSLServer;
    private OutputStreamWriter outputStream;
    private boolean serverReady = false;
    private String serverHost;
    private int serverPort;
    private String newline;

    public void initialize(String host, int port, String newlinechar) {

        if (serverReady) {
            return;
        }
        serverReady = false;
        serverHost = host;
        serverPort = port;
        newline = newlinechar;

        if (connectToSSLServer()) {
            log.debug("Connected to FC at host:" + serverHost + " and port: " + serverPort + "...");
        } else {
            log.error("*** ERROR **** Could not connect to FC at host:" + serverHost + " and port: " + serverPort + ". Will try again later...");
        }
    }

    private boolean connectToSSLServer() {
        //connect to the SSL Server socket
        log.debug("Connecting to FC at host:" + serverHost + " port: " + serverPort);
        try {
            if (!serverReady) {
                SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                requestSocket = (SSLSocket) sslsocketfactory.createSocket(serverHost, serverPort);

                //toSSLServer = new BufferedWriter(new OutputStreamWriter(requestSocket.getOutputStream()));
                outputStream = new OutputStreamWriter(requestSocket.getOutputStream());
                //toSSLServer = new BufferedWriter(outputStream);
                String protocols[] = requestSocket.getEnabledProtocols();
                String tProtocols = "";
                for (String str : protocols) {
                    tProtocols += str + ", ";
                }

                System.out.println("*************** -> " + tProtocols + " <- ****************");
                serverReady = true;
            }

        } catch (UnknownHostException ex) {
            log.fatal("** CRITICAL ERROR ** Cannot connect to SA-Desk FrontController : unknown host exception\n" + ex);
            serverReady = false;
            ex.printStackTrace();
        } catch (IOException ex) {
            log.fatal("** CRITICAL ERROR ** Cannot connect to SA-Desk FrontController : IO exception\n" + ex);
            serverReady = false;
            ex.printStackTrace();
        } catch (Exception ex) {
            log.fatal("** CRITICAL ERROR ** Cannot connect to SA-Desk FrontController : exception\n" + ex);
            serverReady = false;
            ex.printStackTrace();
        }
        return serverReady;
    }

    public boolean isServerReady() {
        return serverReady;
    }

    public boolean sendMessage(String event) {
        log.info("Sending event to FC: " + event);
        try {
            System.out.println("isServerReady() for serverHost = " + serverHost + " serverPort = " + serverPort + " [" + isServerReady() + "]");
            if (isServerReady()) {
                //toSSLServer.write(message + newline);
                //toSSLServer.flush();
                outputStream.write(event + newline);
                outputStream.flush();

                log.info("event sent successfully");
                return true;
            } else {
                serverReady = false;
                log.error("*** ERROR *** No connection to SA-Desk FrontController...attempting to reconnect & resend");
                if (connectToSSLServer()) {
                    log.debug("Re-connected to the SA-Desk FrontController at host:" + serverHost + " and port: " + serverPort + "...");
                    //toSSLServer.write(message + newline);
                    //toSSLServer.flush();
                    outputStream.write(event + newline);
                    outputStream.flush();

                    log.info("Message sent successfully!!");
                    return true;
                } else {
                    log.error("*** ERROR **** Still could not connect to SA-Desk FrontController at host:" + serverHost + " and port: " + serverPort + ". Will try again later...");
                }
                return false; //in both cases false because we have not sent this message
            }
        } catch (IOException ex) {
            log.fatal("** CRITICAL ERROR ** Connection to SA-Desk FrontController broken : IO exception\n" + ex);
            ex.printStackTrace();
            try {
                if (requestSocket != null) {
                    requestSocket.shutdownOutput();
                    requestSocket.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                /*if (toSSLServer != null) {
                    toSSLServer.close();
                }*/
                requestSocket = null;
                outputStream = null;
                //toSSLServer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            serverReady = false;
            return false;
        }
    }
}
