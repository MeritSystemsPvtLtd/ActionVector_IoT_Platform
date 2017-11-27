package serialportapp;

//import gnu.io.SerialPort;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Formatter;
import java.util.Properties;
import java.util.Scanner;
import org.apache.log4j.Logger;

/*
 * This class processes all the queries/commands to the inverter or sensor card.  
 * Methods:
 * 	1. checkSum: 
 * 	2. buildCommandSeq: 
 * 	3. convertString2Int:
 * 	4. loadCommands:
 * 	5. ModbusCmdTxRx: main method which processes each input command/query. 
 * 						Returns output data in byte array
 * 	6. bytesToHexString:
 * 	7. getDataValue: get data value as a string
 * 	8. getDbDataValue: get data value as a double
 * 	9. getIDataValue: get data value as anget data value as a double integer
 */
public class CommandProcessor {

    private static Logger logger = Logger.getLogger("CommandProcessor");
    private SerialConnection connection = null;
    Port cPort = null;
    //private SerialPort serialPort = null;
    //public byte[] outData = new byte[MAX_OUT_BUF_SIZE];
    public int numBytesRcvd = 0;
    private static int readTimeout = -1;
    private static int readSleepTimeslice = -1;
    public static int serialPortFailureCount = 0;

    public CommandProcessor() {

        try {
            cPort = XMLtoJavaSingleton.loadPortDetails();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        readTimeout = cPort.getReadTimeout();
        if (readTimeout == -1) {
            logger.error("readTimeout not defined in port.xml");
            System.exit(-1);
        }
        //logger.info("readTimeout defined in port.xml :: " + readTimeout);
        readSleepTimeslice = cPort.getReadSleepTimeslice();
        if (readSleepTimeslice == -1) {
            logger.error("readSleepTimeslice not defined in port.xml");
            System.exit(-1);
        }
        logger.info("readSleepTimeslice defined in port.xml :: " + readSleepTimeslice);
    }

    public int checkSum(byte[] chk) {

        try {
            logger.debug("in checkSum()");

            int chkSum3 = chk[3];
            int chkSum4 = chk[4];
            int chkSum5 = chk[5];
            int chkSum6 = chk[6];
            int chkSum = chkSum3 + chkSum4 + chkSum5 + chkSum6;
            return chkSum;
        } catch (Exception e) {
            logger.debug("its in checksum :: " + e.getMessage());
        }

        return 0;
    }

    /**
     * This command builds command sequence based on command code
     */
    // public byte[] buildCommandSeq(String cmdCode) {
    public byte[] buildCommandSeq(String cmdCode, int devId,
            byte deviceoption) {
        byte[] inData = new byte[20];
        int command = 0;
        String scmdCode = null;

        command = convertString2Int(cmdCode);


        Properties prop = loadCommands();

        scmdCode = prop.getProperty("request.commandsequence.startbytes.byte0");
        int istartbyteCode0 = convertString2Int(scmdCode);
        scmdCode = prop.getProperty("request.commandsequence.startbytes.byte1");
        int istartbyteCode1 = convertString2Int(scmdCode);
        scmdCode = prop.getProperty("request.commandsequence.startbytes.byte2");
        int istartbyteCode2 = convertString2Int(scmdCode);
        scmdCode = prop.getProperty("request.commandsequence.datalength.byte");
        int idatalengthbyte = convertString2Int(scmdCode);

        inData[0] = (byte) istartbyteCode0; // start byte
        inData[1] = (byte) istartbyteCode1; // start byte
        inData[2] = (byte) istartbyteCode2; // start byte
        inData[3] = (byte) idatalengthbyte; // data length, 00h-for req
        inData[4] = (byte) deviceoption; 	// 01h-Inverter, 02h-Sensor
        inData[5] = (byte) devId; 			// device number-00 to 99
        inData[6] = (byte) (command & (0xff)); // command
        inData[7] = (byte) checkSum(inData); // checksum

        return inData;
    }

    public int convertString2Int(String cmdCode) {
        int num = 0;
        StringReader r = new StringReader(cmdCode);

        Scanner s = new Scanner(r);
        while (s.hasNext()) {
            String hexnum = s.next();
            num = Integer.parseInt(hexnum.substring(2), 16);
            //System.out.println(num);
        }
        s.close();
        return num;
    }

    public Properties loadCommands() {

        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("commandsequence.properties");
            prop.load(input);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return prop;
    }

    public byte[] ModbusCmdTxRx(byte[] inData, int resBytes) {

        //byte[] outData = null;
        byte[] outData = new byte[120];

        try {
            //logger.debug("In ModbusCmdTxRx, input= " + inData);
            //logger.debug("Inside ModbusCmdTxRx()");
            //connection = new SerialConnection(XMLtoJavaSingleton.loadPortDetails());
            connection = new SerialConnection(cPort);
        } catch (Exception e2) {
            logger.debug(e2);
            //logger.debug("ModbusCmdTxRx():Exception:" + e2.getMessage());
        }

        try {
            connection.openConnection();
        } catch (SerialConnectionException e) {
            e.printStackTrace();
            if (connection.isOpen()) {
                //serialPort.close();
            }
            logger.debug("Unable to create connection:" + e.getMessage());
            serialPortFailureCount++;
            //logger.debug("ModbusCmdTxRx():Connection Exception:"+ e.getMessage());

        }
        if (connection.isOpen()) {
            serialPortFailureCount = 0;
            logger.debug("Connection successful");
            try {
                byte[] tmpData = new byte[120];
                int i = 0;
                while (i < 5) {
                    i++;
                    if (connection.is.available() > 0) {
                        connection.is.read(tmpData);
                    }
                }
                tmpData = null;
            } catch (Exception e) {
                logger.error("Error while cleaning up input stream before sending request");
                e.printStackTrace();
            }
            connection.sendData(inData);

            /*connection.readData();

             try {
             Thread.sleep(3000);
             //logger.debug("Classname:SerialPortApp  Method:ModbusCmdTxRx");

             } catch (InterruptedException e) {
             //logger.debug("Classname:SerialPortApp  Method:ModbusCmdTxRx"+ e.getMessage());
             e.printStackTrace();
             }*/

            //outData = connection.outputData;
            //outData = connection.outputData();
            /*int noofbytes;
             logger.debug("command byte is "+inData[6]);
             if(inData[6] == 55){
             noofbytes = 9; 
             }else{
             noofbytes = 11;
             }*/
            try {
                long sysCurrentTime = System.currentTimeMillis();
                long currentTime = sysCurrentTime + readTimeout;
                boolean firstTry = true;
                while (true) {
                    //logger.debug("waiting...");
                    if (!firstTry) {
                        Thread.sleep(readSleepTimeslice);
                    }
                    if (connection.is.available() == resBytes) {
                        connection.is.read(outData, 0, resBytes - 1);
                        logger.debug("data read " + resBytes);
                        break;
                    }
                    sysCurrentTime = System.currentTimeMillis();
                    if ((currentTime < sysCurrentTime) && !firstTry) {
                        logger.debug("Read time elapsed");
                        logger.error("Read Timeout error");
                        return outData;
                    }
                    firstTry = false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            //numBytesRcvd = connection.numBytes;
            numBytesRcvd = outData.length;
            //logger.debug("Classname:SerialPortApp  Method:ModbusCmdTxRx, #Bytes rcvd = "+ numBytesRcvd);

            connection.closeConnection();
        } else {
            logger.debug("Could not  open Connection..");
            //logger.debug("Classname:SerialPortApp  Method:ModbusCmdTxRx, Could not  open Connection");
        }
        return outData;
    }

    /**
     * This method converts byte Array to Hexadecimal string
     */
    public String bytesToHexString(byte[] bytes) {

        StringBuilder sb = new StringBuilder(bytes.length * 2);
        try {
            Formatter formatter = new Formatter(sb);
            for (byte b : bytes) {
                formatter.format("%02x ", b & 0xFF);
            }
            formatter.close();
            //logger.debug("bytesToHexString :: " + sb.toString());
        } catch (Exception e) {
            System.out.println("bytesToHexString(): " + e.getMessage());
        }
        return sb.toString();
    }

    public String getDataValue(byte[] data) {

        Integer i = (((data[7] & 0xff) << 8) | (data[8] & 0xff));

        Double j = (i.doubleValue()) * Math.pow(10, data[9]);

        return j.toString();
    }

    public Double getDbDataValue(byte[] data) {

        Integer i = (((data[7] & 0xff) << 8) | (data[8] & 0xff));

        Double j = (i.doubleValue()) * Math.pow(10, data[9]);

        double roundOff = Math.round(j * 100) / 100D;

        return roundOff;
    }

    public Integer getIDataValue(byte[] data) {

        Integer i = (((data[7] & 0xff) << 8) | (data[8] & 0xff));
        Double j = (i.doubleValue()) * Math.pow(10, data[9]);

        double roundOff = Math.round(j * 100.0) / 100.0;

        return (int) roundOff;
    }
}
