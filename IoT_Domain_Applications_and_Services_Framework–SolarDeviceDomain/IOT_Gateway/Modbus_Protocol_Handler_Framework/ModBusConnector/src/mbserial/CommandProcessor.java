package mbserial;

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
    private static final int INPUT_BUF_SIZE = 8;
    private static final int INPUT_DATA_LEN = 6;
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
        logger.info("readTimeout defined in port.xml :: " + readTimeout);
        readSleepTimeslice = cPort.getReadSleepTimeslice();
        if (readSleepTimeslice == -1) {
            logger.error("readSleepTimeslice not defined in port.xml");
            System.exit(-1);
        }
        logger.info("readSleepTimeslice defined in port.xml :: " + readSleepTimeslice);
    }

    public int checkSum(byte[] chk) {

        try {
            logger.debug("Inside checkSum() ---------- ");

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
            String regAddrHi, String regAddrLo, String regCountHi, String regCountLo) {
        byte[] inData = new byte[INPUT_BUF_SIZE];
  


        int ifunctioncode = convertString2Int(cmdCode);
                //scmdCode = prop.getProperty("request.commandsequence.startaddress.byte0");
        int istartaddressHi = convertString2Int(regAddrHi);
        //scmdCode = prop.getProperty("request.commandsequence.startaddress.byte1");
        int istartaddressLo = convertString2Int(regAddrLo);
        //scmdCode = prop.getProperty("request.commandsequence.regcount.byte0");
        int iregcountHi = convertString2Int(regCountHi);
        //scmdCode = prop.getProperty("request.commandsequence.regcount.byte1");
        int iregcountLo = convertString2Int(regCountLo);
          
        
        inData[0] = (byte) devId; 			// slave id
        inData[1] = (byte) ifunctioncode; 	// function code
        inData[2] = (byte) istartaddressHi; 	// start address high byte
        inData[3] = (byte) istartaddressLo; 	// start address low byte
        inData[4] = (byte) iregcountHi; 		// count of regs to read, high byte
        inData[5] = (byte) iregcountLo; 		// count of regs to read, low byte
        
        int CRC[] = calculateCRC(inData, 0, INPUT_DATA_LEN);
        
        inData[6] = (byte) CRC[0];		// Checksum high byte
        inData[7] = (byte) CRC[1];		// Checksum low byte
        
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
        int numBytesCanbeRead=0;
        
        try {
            logger.debug("In ModbusCmdTxRx, input= " + inData);
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
            logger.debug("Connection is open..");
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
                boolean flag = false;
                while (true) {
                    logger.debug("Required bytes not available yet, going to sleep...");
                    if (!firstTry) {
                        Thread.sleep(readSleepTimeslice);
                    }
                    if (connection.is.available() == resBytes) {
                        logger.debug("Proper response bytes===>" + connection.is.available());
                        connection.is.read(outData, 0, connection.is.available() - 1);
                        //logger.debug("value of outData[6] available ====>" + outData[6]);
                        flag = true;
                        break;
                    }
                    sysCurrentTime = System.currentTimeMillis();
                    if ((currentTime < sysCurrentTime) && !firstTry) {
                        logger.debug("Read time elapsed");
                        logger.error("Read Timeout error");
                        break;
                    }
                    firstTry = false;
                }
                if (!flag) {
                    logger.debug("Error response bytes===>" + connection.is.available());
                    connection.is.read(outData, 0, connection.is.available() - 1);
                   // logger.debug("value of outData[6] in timeout error raised ====>" + outData[6]);
                }
                String sBytes = bytesToHexString(outData);
                logger.debug("response from serialport::" + sBytes);

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

    public String getDataValue(byte[] data, int index) {

        Integer i = (((data[index] & 0xff) << 8) | (data[++index] & 0xff));

        Double j = (i.doubleValue()) * Math.pow(10, data[index+2]);

        return j.toString();
    }

    public Double getDbDataValue(byte[] data, int index) {

        Integer i = (((data[index] & 0xff) << 8) | (data[++index] & 0xff));

        Double j = (i.doubleValue()) * Math.pow(10, data[index+2]);

        double roundOff = Math.round(j * 100) / 100D;

        return roundOff;
    }

    public Integer getIDataValue(byte[] data, int index) {

        Integer i = (((data[index] & 0xff) << 8) | (data[++index] & 0xff));
        Double j = (i.doubleValue()) * Math.pow(10, data[index+2]);

        double roundOff = Math.round(j * 100.0) / 100.0;

        return (int) roundOff;
    }
    
    public static final int[] calculateCRC(byte[] data, int offset, int len) {

        int[] crc = {0xFF, 0xFF};
        int nextByte = 0;
        int uIndex; // index into CRC lookup
        /* scan message buffer */
        for (int i = offset; i < len && i < data.length; i++) {
          nextByte = 0xFF & ((int) data[i]);
          uIndex = crc[0] ^ nextByte; // calculate the CRC
          crc[0] = crc[1] ^ crcHi[uIndex];
          crc[1] = crcLo[uIndex];
        }

        return crc;
      }

      // Table of CRC values for high-order byte
      private final static short[] crcHi = {
        0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
        0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
        0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
        0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
        0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
        0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41,
        0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
        0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
        0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
        0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40,
        0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
        0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
        0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
        0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40,
        0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
        0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40,
        0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
        0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
        0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
        0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
        0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0,
        0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40,
        0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1,
        0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41,
        0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0,
        0x80, 0x41, 0x00, 0xC1, 0x81, 0x40
      };

      // Table of CRC values for low-order byte
      private final static short[] crcLo = {
        0x00, 0xC0, 0xC1, 0x01, 0xC3, 0x03, 0x02, 0xC2, 0xC6, 0x06,
        0x07, 0xC7, 0x05, 0xC5, 0xC4, 0x04, 0xCC, 0x0C, 0x0D, 0xCD,
        0x0F, 0xCF, 0xCE, 0x0E, 0x0A, 0xCA, 0xCB, 0x0B, 0xC9, 0x09,
        0x08, 0xC8, 0xD8, 0x18, 0x19, 0xD9, 0x1B, 0xDB, 0xDA, 0x1A,
        0x1E, 0xDE, 0xDF, 0x1F, 0xDD, 0x1D, 0x1C, 0xDC, 0x14, 0xD4,
        0xD5, 0x15, 0xD7, 0x17, 0x16, 0xD6, 0xD2, 0x12, 0x13, 0xD3,
        0x11, 0xD1, 0xD0, 0x10, 0xF0, 0x30, 0x31, 0xF1, 0x33, 0xF3,
        0xF2, 0x32, 0x36, 0xF6, 0xF7, 0x37, 0xF5, 0x35, 0x34, 0xF4,
        0x3C, 0xFC, 0xFD, 0x3D, 0xFF, 0x3F, 0x3E, 0xFE, 0xFA, 0x3A,
        0x3B, 0xFB, 0x39, 0xF9, 0xF8, 0x38, 0x28, 0xE8, 0xE9, 0x29,
        0xEB, 0x2B, 0x2A, 0xEA, 0xEE, 0x2E, 0x2F, 0xEF, 0x2D, 0xED,
        0xEC, 0x2C, 0xE4, 0x24, 0x25, 0xE5, 0x27, 0xE7, 0xE6, 0x26,
        0x22, 0xE2, 0xE3, 0x23, 0xE1, 0x21, 0x20, 0xE0, 0xA0, 0x60,
        0x61, 0xA1, 0x63, 0xA3, 0xA2, 0x62, 0x66, 0xA6, 0xA7, 0x67,
        0xA5, 0x65, 0x64, 0xA4, 0x6C, 0xAC, 0xAD, 0x6D, 0xAF, 0x6F,
        0x6E, 0xAE, 0xAA, 0x6A, 0x6B, 0xAB, 0x69, 0xA9, 0xA8, 0x68,
        0x78, 0xB8, 0xB9, 0x79, 0xBB, 0x7B, 0x7A, 0xBA, 0xBE, 0x7E,
        0x7F, 0xBF, 0x7D, 0xBD, 0xBC, 0x7C, 0xB4, 0x74, 0x75, 0xB5,
        0x77, 0xB7, 0xB6, 0x76, 0x72, 0xB2, 0xB3, 0x73, 0xB1, 0x71,
        0x70, 0xB0, 0x50, 0x90, 0x91, 0x51, 0x93, 0x53, 0x52, 0x92,
        0x96, 0x56, 0x57, 0x97, 0x55, 0x95, 0x94, 0x54, 0x9C, 0x5C,
        0x5D, 0x9D, 0x5F, 0x9F, 0x9E, 0x5E, 0x5A, 0x9A, 0x9B, 0x5B,
        0x99, 0x59, 0x58, 0x98, 0x88, 0x48, 0x49, 0x89, 0x4B, 0x8B,
        0x8A, 0x4A, 0x4E, 0x8E, 0x8F, 0x4F, 0x8D, 0x4D, 0x4C, 0x8C,
        0x44, 0x84, 0x85, 0x45, 0x87, 0x47, 0x46, 0x86, 0x82, 0x42,
        0x43, 0x83, 0x41, 0x81, 0x80, 0x40
      };
}
