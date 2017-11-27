package mbserial;

/* @(#)SerialConnection.java	1.6 98/07/17 SMI
 *
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license
 * to use, modify and redistribute this software in source and binary
 * code form, provided that i) this copyright notice and license appear
 * on all copies of the software; and ii) Licensee does not utilize the
 * software in a manner which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind.
 * ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND
 * ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THE
 * SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS
 * BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 * HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING
 * OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control
 * of aircraft, air traffic, aircraft navigation or aircraft
 * communications; or in the design, construction, operation or
 * maintenance of any nuclear facility. Licensee represents and
 * warrants that it will not use or redistribute the Software for such
 * purposes.
 */
import gnu.io.*;

import java.io.*;
import java.util.Enumeration;
//import java.awt.TextArea;
//import java.awt.event.*;
import java.util.TooManyListenersException;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * A class that handles the details of a serial connection.
 */
public class SerialConnection implements SerialPortEventListener, Runnable {
    // private SerialParameters parameters;

    private Port parameters;
    private OutputStream os;
    //private 
    InputStream is;
    private static final int MAX_OUT_BUF_SIZE = 120;
    // private CommPortIdentifier portId;
    static CommPortIdentifier portId;
    static Enumeration<?> portList;
    private SerialPort sPort;
    // private byte[] outputData;
    byte[] outputData = new byte[MAX_OUT_BUF_SIZE];
    int numBytes = 0;
    private boolean open;
    Thread readThread;
    private static Logger logger = null;
    Vector szVect = new Vector();
    byte[] tmpbyteArray = new byte[0];
    private static int openPortTimeout = -1;

    /**
     * Creates a SerialConnection object and initilizes variables passed in as
     * params.
     *
     * @param parent A SerialPortApp object.
     * @param parameters A SerialParameters object.
     */
    // public SerialConnection(SerialPortApp parent, SerialParameters
    // parameters) {
    public SerialConnection(Port portParameters) {
        try {
            logger = Logger.getLogger("SerialConnection");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        this.parameters = portParameters;
        open = false;
        openPortTimeout = parameters.getOpenPortTimeout();
        logger.info("openPortTimeout is "+openPortTimeout);
        if(openPortTimeout == -1){
            logger.error("openPortTimeout not defined in port.xml");
            System.exit(-1);
        }        
    }

    /**
     * Attempts to open a serial connection and streams using the parameters in
     * the SerialParameters object. If it is unsuccesfull at any step it returns
     * the port to a closed state, throws a
     * <code>SerialConnectionException</code>, and returns.
     *
     * Gives a timeout of 30 seconds on the portOpen to allow other applications
     * to reliquish the port if have it open and no longer need it.
     */
    public void openConnection() throws SerialConnectionException {

        // Obtain a CommPortIdentifier object for the port you want to open.
        logger.debug("In openConnection(): " + parameters.getPortName());
        try {
            logger.debug("In openConnection(): inside try..catch block "
                    + parameters.getPortName());
            portId = CommPortIdentifier.getPortIdentifier(parameters
                    .getPortName());
            logger.debug("In openConnection(): inside try block,portid=  "
                    + portId.getPortType());
            /*
             * portList = CommPortIdentifier.getPortIdentifiers();
             * 
             * while (portList.hasMoreElements()) {
             * System.out.println("here..1"); portId = (CommPortIdentifier)
             * portList.nextElement(); if (portId.getPortType() ==
             * CommPortIdentifier.PORT_SERIAL) { // if
             * (portId.getName().equals("COM1")) { if
             * (portId.getName().equals(parameters.getPortName())) {
             * System.out.println("here..2"); portId =
             * CommPortIdentifier.getPortIdentifier(parameters.getPortName());
             * System.out.println("here..3"); } } }
             */

        } catch (NoSuchPortException e) {
            throw new SerialConnectionException(e.getMessage());
        } catch (Exception e1) {
            logger.debug("Exception" + e1);
            throw new SerialConnectionException(e1.getMessage());
        }

        logger.debug("In openConnection(): port type= "
                + portId.getPortType());

        // Open the port represented by the CommPortIdentifier object. Give
        // the open call a relatively long timeout of 30 seconds to allow
        // a different application to reliquish the port if the user
        // wants to.
        try {
            //sPort = (SerialPort) portId.open("SerialPortApp", 2000);
            sPort = (SerialPort) portId.open("SerialPortApp", openPortTimeout);
        } catch (PortInUseException e) {
            throw new SerialConnectionException(e.getMessage());
        } catch (Exception e) {
            throw new SerialConnectionException(e.getMessage());
        }

        // Set the parameters of the connection. If they won't set, close the
        // port before throwing an exception.
        try {
            setConnectionParameters();
        } catch (SerialConnectionException e) {
            sPort.close();
            throw e;
        }

        // Open the input and output streams for the connection. If they won't
        // open, close the port before throwing an exception.
        try {
            os = sPort.getOutputStream();
            is = sPort.getInputStream();
        } catch (IOException e) {
            sPort.close();
            throw new SerialConnectionException("Error opening i/o streams");
        }

        // Add this object as an event listener for the serial port.
        /*try {
         sPort.addEventListener(this);
         } catch (TooManyListenersException e) {
         sPort.close();
         throw new SerialConnectionException("too many listeners added");
         }*/

        // Set notifyOnDataAvailable to true to allow event driven input.
        sPort.notifyOnDataAvailable(true);

        // Set notifyOnBreakInterrup to allow event driven break handling.
        sPort.notifyOnBreakInterrupt(true);

        // Set receive timeout to allow breaking out of polling loop during
        // input handling.
        try {
            sPort.enableReceiveTimeout(3000);
        } catch (UnsupportedCommOperationException e) {
        }

        open = true;
        // System.out.println("In openConnection(): open flag-" + open);
    }

    /**
     * Sets the connection parameters to the setting in the parameters object.
     * If set fails return the parameters object to origional settings and throw
     * exception.
     */
    public void setConnectionParameters() throws SerialConnectionException {

        // Save state of parameters before trying a set.
        int oldBaudRate = sPort.getBaudRate();
        int oldDatabits = sPort.getDataBits();
        int oldStopbits = sPort.getStopBits();
        int oldParity = sPort.getParity();
        //int oldFlowControl = sPort.getFlowControlMode();

        // Set connection parameters, if set fails return parameters object
        // to original state.
        try {
            sPort.setSerialPortParams(parameters.getBaudRate(),
                    parameters.getDatabits(), parameters.getStopbits(),
                    parameters.getParity());
        } catch (UnsupportedCommOperationException e) {
            parameters.setBaudRate(oldBaudRate);
            parameters.setDatabits(oldDatabits);
            parameters.setStopbits(oldStopbits);
            parameters.setParity(oldParity);
            throw new SerialConnectionException("Unsupported parameter");
        }

        // Set flow control.
        try {
            sPort.setFlowControlMode(parameters.getFlowControlIn()
                    | parameters.getFlowControlOut());
        } catch (UnsupportedCommOperationException e) {
            throw new SerialConnectionException("Unsupported flow control");
        }
    }

    /**
     * Close the port and clean up associated elements.
     */
    public void closeConnection() {
        // If port is alread closed just return.
        if (!open) {
            return;
        }

        // Check to make sure sPort has reference to avoid a NPE.
        if (sPort != null) {
            try {
                // close the i/o streams.
                os.close();
                is.close();
            } catch (IOException e) {
                System.err.println(e);
            }

            // Close the port.
            sPort.close();

        }

        open = false;
    }

    /**
     * Reports the open status of the port.
     *
     * @return true if port is open, false if port is closed.
     */
    public boolean isOpen() {
        return open;
    }

    public byte[] outputData() {
        /*
         * for (int i=0; i<outputData.length; i++){
         * System.out.print(outputData[i] + "  "); }
         */

        logger.debug("returning output with size::" + tmpbyteArray.length);
        outputData = tmpbyteArray;
        tmpbyteArray = new byte[0];
        return outputData;

    }

    /**
     * Handles SerialPortEvents. The two types of SerialPortEvents that this
     * program is registered to listen for are DATA_AVAILABLE and BI. During
     * DATA_AVAILABLE the port buffer is read until it is drained, when no more
     * data is availble and 30ms has passed the method returns.
     */
    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                try {
                    byte[] szTempBuf = new byte[MAX_OUT_BUF_SIZE];
                    int szCnt;
                    while ((szCnt = is.available()) > 0) {
                        //numBytes = is.read(outputData);
                        numBytes = is.read(szTempBuf);

                    }

                    logger.debug("\nNum bytes read=" + numBytes + "Bytes read: " + szTempBuf);
                    byte[] tmp2 = tmpbyteArray;
                    int newSize = numBytes + tmp2.length;
                    tmpbyteArray = new byte[newSize];
                    int i = 0;
                    for (int j = 0; j < tmp2.length; j++) {
                        tmpbyteArray[i] = tmp2[j];
                        i++;
                    }
                    for (int j = 0; j < szCnt; j++) {
                        tmpbyteArray[i] = szTempBuf[j];
                        i++;
                    }

                } catch (IOException e) {
                    logger.debug("Could not read data from port:" + e);
                }
                break;
        }
    } /* End of serialEvent() */


    public void readData() {
        readThread = new Thread(this);
        readThread.start();
    }

    public void run() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.debug(e);
        }
    }

    public void sendData(byte bData[]) {
        logger.debug("In sendData().." + bData);
        try {
            for (byte input : bData) {
                os.write(input);
                os.flush();
            }
            // } catch (Exception e) {
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
