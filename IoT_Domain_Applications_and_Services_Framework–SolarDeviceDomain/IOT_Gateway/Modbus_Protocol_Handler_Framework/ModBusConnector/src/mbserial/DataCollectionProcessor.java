package mbserial;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mbserial.DataCollectionSet.DataCollection;

import org.apache.log4j.Logger;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/*
 * This class processes all the data collections  
 * Methods:
 * 	1. processMinMaxSensorData: 
 * 	2. processNowSensorData: 
 * 	3. processMinMaxInverterData:
 * 	4. process3PInverterData:
 * 	5. processCumulativeInverterData:
 * 	6. processCommonInverterData:
 * 	7. processData:
 * 	8. processGetActiveDeviceInfoData:
 */
public class DataCollectionProcessor {
	private static final int EXCEPTION_CODE = 2;
    private static final int DATA_INDEX = 3;
    private static final int STATUS_INDEX = 4;
    private static final int EXP_INDEX = 8;
    private static final int FUNCTION_CODE_INDEX = 1;

    private static final int FUNCTION_CODE = 0x04;
    private static final int ERROR_CODE = (0x80 | FUNCTION_CODE);
    
	boolean error = false;
    int numBytesRcvd = 0;
    private static Logger logger = null;
    CommandProcessor commandProcessor = new CommandProcessor();

    public DataCollectionProcessor() {
        try {
            logger = Logger.getLogger("DataCollectionProcessor");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /*
     * Assumption:
     * There are 5 channels available - T1, T2, Irradiance channel, D1 and D2 and all are active.
     * SensorActive is hard coded for all the 5 channels
     */
    
    public String processMinMaxSensorData(DataCollection dataCollection,
            JSONRequest jsonRequest) {


        byte deviceoption = 0x02;			//for sensor commands
        String jsonString = "";
        int ctr = 0;
        int cmdResBytes=0;
        int excCode=0;


        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");  // set current system date time in RFC-3339 format
            String dateStr = f.format(new Date());
            JsonObject jHeader = new JsonObject();
            JsonObject jBody = new JsonObject();
            JsonObject jData = new JsonObject();
            JsonObject dayobj0 = new JsonObject();
            JsonObject monthobj0 = new JsonObject();
            JsonObject yearobj0 = new JsonObject();
            JsonObject totalobj0 = new JsonObject();
            JsonObject dayobj1 = new JsonObject();
            JsonObject monthobj1 = new JsonObject();
            JsonObject yearobj1 = new JsonObject();
            JsonObject totalobj1 = new JsonObject();
            JsonObject dayobj2 = new JsonObject();
            JsonObject monthobj2 = new JsonObject();
            JsonObject yearobj2 = new JsonObject();
            JsonObject totalobj2 = new JsonObject();
            JsonObject dayobj3 = new JsonObject();
            JsonObject monthobj3 = new JsonObject();
            JsonObject yearobj3 = new JsonObject();
            JsonObject totalobj3 = new JsonObject();
            JsonObject dayobj4 = new JsonObject();
            JsonObject monthobj4 = new JsonObject();
            JsonObject yearobj4 = new JsonObject();
            JsonObject totalobj4 = new JsonObject();

            JsonArray jch0 = new JsonArray();		//T1 Channel

            JsonArray jch1 = new JsonArray();		//T2 Channel
            JsonArray jch2 = new JsonArray();		//IRR Channel
            JsonArray jch3 = new JsonArray();		//D1 Channel
            JsonArray jch4 = new JsonArray();		//D2 Channel
            JsonArray day0 = new JsonArray();
            JsonArray month0 = new JsonArray();
            JsonArray year0 = new JsonArray();
            JsonArray total0 = new JsonArray();
            JsonArray day1 = new JsonArray();
            JsonArray month1 = new JsonArray();
            JsonArray year1 = new JsonArray();
            JsonArray total1 = new JsonArray();
            JsonArray day2 = new JsonArray();
            JsonArray month2 = new JsonArray();
            JsonArray year2 = new JsonArray();
            JsonArray total2 = new JsonArray();
            JsonArray day3 = new JsonArray();
            JsonArray month3 = new JsonArray();
            JsonArray year3 = new JsonArray();
            JsonArray total3 = new JsonArray();
            JsonArray day4 = new JsonArray();
            JsonArray month4 = new JsonArray();
            JsonArray year4 = new JsonArray();
            JsonArray total4 = new JsonArray();


            DataCollectionSet.DataCollection.CommandSet commandSet = dataCollection.getCommandSet();
            List<DataCollectionSet.DataCollection.CommandSet.Command> commands = commandSet.getCommand();


            for (int j = 0; j < commands.size(); j++) {

                logger.debug("inside for loop j=" + j);

                DataCollectionSet.DataCollection.CommandSet.Command command = commands.get(j);
                String cmdName = command.getName();
                String cmdCode = command.getCode();
                String regAddrHi = command.getRegAddrHi();
                String regAddrLo = command.getRegAddrLo();
                String regCountHi = command.getRegCountHi();
                String regCountLo = command.getRegCountLo();                
                String cmdUnit = command.getUnit();
                String cmdDataType = command.getDataType();
                cmdResBytes = command.getResponseBytes();
                if (cmdResBytes == 0) {
                    logger.error("ResponseBytes not defined for command Name::" + cmdName);
                    return jsonString;
                }

                // Building the command sequence to be sent here
                byte[] cmdSeq = commandProcessor.buildCommandSeq (cmdCode, jsonRequest.getDeviceId(), 
    					regAddrHi, regAddrLo, regCountHi, regCountLo);
                String cmdSequence = commandProcessor.bytesToHexString(cmdSeq);


                logger.debug("processMinMaxSensorData(): Send Bytes = " + cmdSequence);
                logger.debug("its here : " + cmdSeq[6]);

                // Send the command sequence and receive the response bytes
                // in rData.
                byte[] rData = commandProcessor.ModbusCmdTxRx(cmdSeq, cmdResBytes);
                numBytesRcvd = commandProcessor.numBytesRcvd;
                if (numBytesRcvd <= 0) {
                    logger.debug("Unable to read data from port");
                    return null;
                }
                JsonObject metric = new JsonObject();
                JsonObject min = new JsonObject();
                JsonObject max = new JsonObject();

                //process protocol error(Unknown Command) here.
                if (rData[1] == (byte)ERROR_CODE) { // function code is set on error by the slave
                    error = true;
                    excCode = rData[EXCEPTION_CODE];   // exception code set by slave on error
                    //JsonObject metric = new JsonObject();
                    metric.addProperty("Value", "Error");
                    metric.addProperty("Unit", "");
                    jData.add(cmdName, metric);
                } else {

                    if (cmdDataType.equals("Float")) {
                        Double sDatadbValue = commandProcessor.getDbDataValue(rData, DATA_INDEX);
                        metric.addProperty("Value", sDatadbValue);
                    } else {
                        Integer sDataiValue = commandProcessor.getIDataValue(rData, DATA_INDEX);
                        //metric.addProperty("Value", sDataiValue);
                        if (sDataiValue.byteValue()<0) {
                        	metric.addProperty("Value", sDataiValue.byteValue());
                        } else {
                        	metric.addProperty("Value", sDataiValue);

                        }                        
                    }
                    metric.addProperty("Unit", cmdUnit);


                }

                if (cmdName.matches("(.*)T1CH(.*)")) {

                    if (ctr == 0) {

                        JsonObject obj = new JsonObject();
                        obj.add("SensorActive", new JsonPrimitive(true));
                        jch0.add(obj);
                        ctr = 1;
                    }
                    if (cmdName.matches("(.*)Day(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            day0.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            day0.add(max);

                            dayobj0.add("Day", day0);
                            jch0.add(dayobj0);
                        }

                    } else if (cmdName.matches("(.*)Month(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            month0.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            month0.add(max);

                            monthobj0.add("Month", month0);
                            jch0.add(monthobj0);
                        }
                        //month.add(cmdName, metric);
                        //jch0.add(month);

                    } else if (cmdName.matches("(.*)Yr(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            year0.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            year0.add(max);

                            yearobj0.add("Year", year0);
                            jch0.add(yearobj0);
                        }

                    } else if (cmdName.matches("(.*)Tot(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            total0.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            total0.add(max);
                            totalobj0.add("Total", total0);
                            jch0.add(totalobj0);
                        }

                    }

                    jData.add("0", jch0);

                }


                if (cmdName.matches("(.*)T2CH(.*)")) {

                    if (ctr == 1) {
                        day1 = new JsonArray();
                        month1 = new JsonArray();
                        year1 = new JsonArray();
                        total1 = new JsonArray();
                        JsonObject obj = new JsonObject();
                        obj.add("SensorActive", new JsonPrimitive(true));
                        jch1.add(obj);
                        ctr = 2;
                    }
                    if (cmdName.matches("(.*)Day(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            day1.add(min);
                            dayobj1.add("Day", day1);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            day1.add(max);
                            dayobj1.add("Day", day1);
                            jch1.add(dayobj1);
                            //jch1.add(day);
                        }


                    } else if (cmdName.matches("(.*)Month(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            month1.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            month1.add(max);

                            monthobj1.add("Month", month1);
                            jch1.add(monthobj1);
                        }
                    } else if (cmdName.matches("(.*)Yr(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            year1.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            year1.add(max);

                            yearobj1.add("Year", year1);
                            jch1.add(yearobj1);
                        }
                    } else if (cmdName.matches("(.*)Tot(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            total1.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            total1.add(max);
                            totalobj1.add("Total", total1);
                            jch1.add(totalobj1);
                        }
                    }
                    //jData.addProperty("SensorActive", "true");
                    jData.add("1", jch1);

                }
                if (cmdName.matches("(.*)IRRCH(.*)")) {

                    if (ctr == 2) {
                        day2 = new JsonArray();
                        month2 = new JsonArray();
                        year2 = new JsonArray();
                        total2 = new JsonArray();
                        JsonObject obj = new JsonObject();
                        obj.add("SensorActive", new JsonPrimitive(true));
                        jch2.add(obj);
                        ctr = 3;
                    }
                    if (cmdName.matches("(.*)Day(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            day2.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            day2.add(max);
                            dayobj2.add("Day", day2);
                            jch2.add(dayobj2);
                        }


                    } else if (cmdName.matches("(.*)Month(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            month2.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            month2.add(max);

                            monthobj2.add("Month", month2);
                            jch2.add(monthobj2);
                        }


                    } else if (cmdName.matches("(.*)Yr(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            year2.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            year2.add(max);

                            yearobj2.add("Year", year2);
                            jch2.add(yearobj2);
                        }
                    } else if (cmdName.matches("(.*)Tot(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            total2.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            total2.add(max);
                            totalobj2.add("Total", total2);
                            jch2.add(totalobj2);
                        }
                    }
                    //jData.addProperty("SensorActive", "true");
                    jData.add("2", jch2);

                }
                if (cmdName.matches("(.*)D1CH(.*)")) {

                    if (ctr == 3) {
                        day3 = new JsonArray();
                        month3 = new JsonArray();
                        year3 = new JsonArray();
                        total3 = new JsonArray();
                        JsonObject obj = new JsonObject();
                        obj.add("SensorActive", new JsonPrimitive(true));
                        jch3.add(obj);
                        ctr = 4;
                    }
                    if (cmdName.matches("(.*)Day(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            day3.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            day3.add(max);
                            dayobj3.add("Day", day3);
                            jch3.add(dayobj3);
                        }


                    } else if (cmdName.matches("(.*)Month(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            month3.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            month3.add(max);

                            monthobj3.add("Month", month3);
                            jch3.add(monthobj3);
                        }

                    } else if (cmdName.matches("(.*)Yr(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            year3.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            year3.add(max);

                            yearobj3.add("Year", year3);
                            jch3.add(yearobj3);
                        }
                    } else if (cmdName.matches("(.*)Tot(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            total3.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            total3.add(max);
                            totalobj3.add("Total", total3);
                            jch3.add(totalobj3);
                        }
                    }

                    jData.add("3", jch3);

                }
                if (cmdName.matches("(.*)D2CH(.*)")) {

                    if (ctr == 4) {
                        day4 = new JsonArray();
                        month4 = new JsonArray();
                        year4 = new JsonArray();
                        total4 = new JsonArray();

                        JsonObject obj = new JsonObject();
                        obj.add("SensorActive", new JsonPrimitive(true));
                        jch4.add(obj);
                        ctr = 5;
                    }
                    if (cmdName.matches("(.*)Day(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            day4.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            day4.add(max);
                            dayobj4.add("Day", day4);
                            jch4.add(dayobj4);
                        }

                    } else if (cmdName.matches("(.*)Month(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            month4.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            month4.add(max);

                            monthobj4.add("Month", month4);
                            jch4.add(monthobj4);
                        }

                    } else if (cmdName.matches("(.*)Yr(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            year4.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            year4.add(max);

                            yearobj4.add("Year", year4);
                            jch4.add(yearobj4);
                        }
                    } else if (cmdName.matches("(.*)Tot(.*)")) {
                        if (cmdName.matches("(.*)Min(.*)")) {
                            min.add(cmdName, metric);
                            total4.add(min);
                        }

                        if (cmdName.matches("(.*)Max(.*)")) {
                            max.add(cmdName, metric);
                            total4.add(max);
                            totalobj4.add("Total", total4);
                            jch4.add(totalobj4);
                        }
                    }
                    //jData.addProperty("SensorActive", "true");
                    jData.add("4", jch4);

                }

            }// end of for


            jBody.add("Data", jData);

            JsonObject jRequestArguments = new JsonObject();
            jRequestArguments.addProperty("Scope", jsonRequest.getScope());
            jRequestArguments.addProperty("DeviceId", jsonRequest.getDeviceId());
            jRequestArguments.addProperty("DataCollection", jsonRequest.getDataCollection());
            jHeader.add("RequestArguments", jRequestArguments);

            JsonObject jStatus = new JsonObject();
            if (error) {
                jStatus.addProperty("Code", excCode);
                jStatus.addProperty("Reason", "Protocol Error");
                jStatus.addProperty("UserMessage", "Unkown Command");
            } else {
                jStatus.addProperty("Code", 0);
                jStatus.addProperty("Reason", "");
                jStatus.addProperty("UserMessage", "");
            }

            jHeader.add("Status", jStatus);

            jHeader.addProperty("Timestamp", dateStr);

            JsonObject root = new JsonObject();
            root.add("Header", jHeader);
            root.add("Body", jBody);
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            jsonString = gson.toJson(root);
            //System.out.println(jsonString);
            //logger.debug("Built the cmdResponse Object.Returning...");
            logger.debug("processMinMaxSensorData :: jsonString" + jsonString);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return jsonString;

    }

  
    public String processNowSensorData(DataCollection dataCollection,
            JSONRequest jsonRequest) {

        byte deviceoption = 0x02;
        String jsonString = "";
        int cmdResBytes=0;
        int excCode=0;


        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");  // set current system date time in RFC-3339 format
            String dateStr = f.format(new Date());
            JsonObject jHeader = new JsonObject();
            JsonObject jBody = new JsonObject();
            JsonObject jData = new JsonObject();

            DataCollectionSet.DataCollection.CommandSet commandSet = dataCollection.getCommandSet();
            List<DataCollectionSet.DataCollection.CommandSet.Command> commands = commandSet.getCommand();

            for (int j = 0; j < commands.size(); j++) {
                logger.debug("processNowSensorData(): inside for loop j=" + j);

                DataCollectionSet.DataCollection.CommandSet.Command command = commands.get(j);
                String cmdName = command.getName();
                String cmdCode = command.getCode();
                String regAddrHi = command.getRegAddrHi();
                String regAddrLo = command.getRegAddrLo();
                String regCountHi = command.getRegCountHi();
                String regCountLo = command.getRegCountLo();
                String cmdUnit = command.getUnit();
                String cmdDataType = command.getDataType();
                cmdResBytes = command.getResponseBytes();
                if (cmdResBytes == 0) {
                    logger.error("ResponseBytes not defined for command Name::" + cmdName);
                    return jsonString;
                }

                // Building the command sequence to be sent here
                byte[] cmdSeq = commandProcessor.buildCommandSeq (cmdCode, jsonRequest.getDeviceId(), 
    					regAddrHi, regAddrLo, regCountHi, regCountLo);
                String sendBytes = commandProcessor.bytesToHexString(cmdSeq);
                logger.debug("processNowSensorData(): Send byte sequence: " + sendBytes);

                // Send the command sequence and receive the response bytes
                // in rData.
                byte[] rData = commandProcessor.ModbusCmdTxRx(cmdSeq, cmdResBytes);

                String rcvBytes = commandProcessor.bytesToHexString(rData);
                logger.debug("processNowSensorData(): Rcvd byte sequence: " + rcvBytes);

                numBytesRcvd = commandProcessor.numBytesRcvd;
                if (numBytesRcvd <= 0) {
                    logger.debug("Unable to read data from port");
                    return null;
                }
                // }
                //process protocol error(Unknown Command) here.
                if (rData[1] == (byte)ERROR_CODE) { // function code is set on error by the slave
                    error = true;
                    excCode = rData[EXCEPTION_CODE];   // exception code set by slave on error
                    JsonObject metric = new JsonObject();
                    metric.addProperty("Value", "Error");
                    metric.addProperty("Unit", "");
                    jData.add(cmdName, metric);
                } else {


                    JsonObject metric = new JsonObject();
                    if (cmdDataType.equals("Float")) {
                        Double sDatadbValue = commandProcessor.getDbDataValue(rData, DATA_INDEX);
                        metric.addProperty("Value", sDatadbValue);
                    } else {
                        Integer sDataiValue = commandProcessor.getIDataValue(rData, DATA_INDEX);
                        if (sDataiValue.byteValue()<0) {
                        	metric.addProperty("Value", sDataiValue.byteValue());
                        } else {
                        	metric.addProperty("Value", sDataiValue);

                        }
                    }
                    metric.addProperty("Unit", cmdUnit);

                    jData.add(cmdName, metric);

                }
                //error=false;
            }// end of for

            //populating json object

            jBody.add("Data", jData);

            JsonObject jRequestArguments = new JsonObject();
            jRequestArguments.addProperty("Scope", jsonRequest.getScope());
            jRequestArguments.addProperty("DeviceId", jsonRequest.getDeviceId());
            jRequestArguments.addProperty("DataCollection", jsonRequest.getDataCollection());
            jHeader.add("RequestArguments", jRequestArguments);

            JsonObject jStatus = new JsonObject();
            if (error) {
                jStatus.addProperty("Code", excCode);
                jStatus.addProperty("Reason", "Protocol Error");
                jStatus.addProperty("UserMessage", "Unkown Command");
            } else {
                jStatus.addProperty("Code", 0);
                jStatus.addProperty("Reason", "");
                jStatus.addProperty("UserMessage", "");
            }

            jHeader.add("Status", jStatus);

            jHeader.addProperty("Timestamp", dateStr);

            JsonObject root = new JsonObject();
            root.add("Header", jHeader);
            root.add("Body", jBody);
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            jsonString = gson.toJson(root);

            logger.debug("processNowSensorData :: jsonString" + jsonString);
        } catch (Exception e) {

            e.printStackTrace();
        }


        return jsonString;

    }
    
    
    /* 17-Dec-14
     * How to implement this for modbus?
	*/
        public String processGetActiveDeviceInfo(String deviceclass) {

        String jsonString = "";

        String cmdCode = null;
        byte deviceoption;
        int numOfActiveDevices = 0;

        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");  // set current system date time in RFC-3339 format
            String dateStr = f.format(new Date());
            JsonObject jHeader = new JsonObject();
            JsonObject jBody = new JsonObject();
            JsonObject jData = new JsonObject();
            JsonArray jdevices = new JsonArray();
            if (deviceclass.equals("Inverter")) {
                cmdCode = "0x04";
                deviceoption = 0x01;

                logger.debug("processGetActiveDeviceInfoData(): Inverter");
            } else if (deviceclass.equals("SensorCard")) {
                cmdCode = "0x05";
                deviceoption = 0x02;
                logger.debug("processGetActiveDeviceInfoData(): SensorCard");
            } else {
                logger.debug("processGetActiveDeviceInfoData(): Invalid parameter");
                return null;  // returning null
            }
            //Get device number list - inverter or sensor card
            //for devices that are active.
            byte[] inData = commandProcessor.buildCommandSeq(cmdCode, 0, null, null, null, null);
            String sendBytes = commandProcessor.bytesToHexString(inData);
            logger.debug("Send Bytes = " + sendBytes);
            byte[] outData = commandProcessor.ModbusCmdTxRx(inData, 11);
            numBytesRcvd = commandProcessor.numBytesRcvd;
            if (numBytesRcvd <= 0) {
                logger.debug("Unable to read data from port");
                return null;
            }
            String rcvBytes = commandProcessor.bytesToHexString(outData);
            logger.debug("Received Bytes = " + rcvBytes);
            numOfActiveDevices = outData[3];
            logger.debug("#Active devices = " + numOfActiveDevices);


            if (numOfActiveDevices > 0) {

                byte devicetype = 0x00;
                int devicenum = 0;
                cmdCode = "0x02";            // get device type command
                //Get device type for each device.
                logger.debug("before for....");


                for (int i = 7, j = 0; j < numOfActiveDevices; i++, j++) {
                    JsonObject jdevice = new JsonObject();

                    //devices.add(new DEV());
                    devicenum = (int) outData[i];
                    logger.debug("Inside for....Devnum= " + devicenum);


                    byte[] sendData = commandProcessor.buildCommandSeq(cmdCode, devicenum, null, null, null, null);
                    String sBytes = commandProcessor.bytesToHexString(sendData);
                    logger.debug("Send Bytes = " + sBytes);
                    byte[] devtypeData = commandProcessor.ModbusCmdTxRx(sendData, 11);
                    String rBytes = commandProcessor.bytesToHexString(devtypeData);
                    logger.debug("Receive Bytes = " + rBytes);
                    logger.debug("Inside for: num bytes read=" + numBytesRcvd);
                    numBytesRcvd = commandProcessor.numBytesRcvd;
                    logger.debug("processGetActiveDeviceInfoData():Inside for: num bytes read=" + numBytesRcvd);
                    if (numBytesRcvd <= 0) {
                        logger.debug("processGetActiveDeviceInfoData(): Unable to read data from port");
                        return null;
                    }
                    // }
                    //process protocol error(Unknown Command) here.
                    if (devtypeData[6] == 0x0E) {
                        error = true;
                        continue;
                    }


                    devicetype = devtypeData[7];
                    jdevice.addProperty("devno", devicenum);
                    jdevice.addProperty("DT", (int) (devicetype & 0x00ff));
                    jdevices.add(jdevice);


                }

                jData.add("Devices", jdevices);
                jBody.add("Data", jData);

                JsonObject jRequestArguments = new JsonObject();
                jRequestArguments.addProperty("Scope", "");
                jRequestArguments.addProperty("DeviceId", "");
                jRequestArguments.addProperty("DataCollection", "");
                jHeader.add("RequestArguments", jRequestArguments);

                JsonObject jStatus = new JsonObject();
                if (error) {
                    jStatus.addProperty("Code", 1);
                    jStatus.addProperty("Reason", "Protocol Error");
                    jStatus.addProperty("UserMessage", "Unkown Command");
                } else {
                    jStatus.addProperty("Code", 0);
                    jStatus.addProperty("Reason", "");
                    jStatus.addProperty("UserMessage", "");
                }

                jHeader.add("Status", jStatus);

                jHeader.addProperty("Timestamp", dateStr);

                JsonObject root = new JsonObject();
                root.add("Header", jHeader);
                root.add("Body", jBody);
                Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
                jsonString = gson.toJson(root);

                logger.debug("processGetActiveDeviceInfoData() :: jsonString" + jsonString);
            } else {
                logger.debug("processGetActiveDeviceInfoData():No active devices");
                return null;
            }
        } catch (Exception e) {

            System.out.println("processGetActiveDeviceInfoData():" + e.getMessage());
            e.printStackTrace();
        }
        return jsonString;

    }
    

    public String processMinMaxInverterData(DataCollection dataCollection,
            JSONRequest jsonRequest) {

        byte deviceoption = 0x01;
        String jsonString = "";
        int cmdResBytes=0;
        int excCode=0;

        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");  // set current system date time in RFC-3339 format
            String dateStr = f.format(new Date());
            JsonObject jHeader = new JsonObject();
            JsonObject jBody = new JsonObject();
            JsonObject jData = new JsonObject();

            DataCollectionSet.DataCollection.CommandSet commandSet = dataCollection.getCommandSet();
            List<DataCollectionSet.DataCollection.CommandSet.Command> commands = commandSet.getCommand();


            for (int j = 0; j < commands.size(); j++) {

                logger.debug("inside for loop j=" + j);

                DataCollectionSet.DataCollection.CommandSet.Command command = commands
                        .get(j);
                String cmdName = command.getName();
                String cmdCode = command.getCode();
                String regAddrHi = command.getRegAddrHi();
                String regAddrLo = command.getRegAddrLo();
                String regCountHi = command.getRegCountHi();
                String regCountLo = command.getRegCountLo();
                String cmdUnit = command.getUnit();
                String cmdDataType = command.getDataType();
                cmdResBytes = command.getResponseBytes();
                if (cmdResBytes == 0) {
                    logger.error("ResponseBytes not defined for command Name::" + cmdName);
                    return jsonString;
                }

                // Building the command sequence to be sent here
                byte[] cmdSeq = commandProcessor.buildCommandSeq (cmdCode, jsonRequest.getDeviceId(), 
    					regAddrHi, regAddrLo, regCountHi, regCountLo);
                String sendBytes = commandProcessor.bytesToHexString(cmdSeq);

                logger.debug("processMinMaxInverterData(): Send bytes: " + sendBytes);

                // Send the command sequence and receive the response bytes
                // in rData.
                byte[] rData = commandProcessor.ModbusCmdTxRx(cmdSeq, cmdResBytes);
                numBytesRcvd = commandProcessor.numBytesRcvd;
                if (numBytesRcvd <= 0) {
                    logger.debug("Unable to read data from port");
                    return null;
                }

                //process protocol error(Unknown Command) here.
                if (rData[1] == (byte)ERROR_CODE) { // function code is set on error by the slave
                    error = true;
                    excCode = rData[EXCEPTION_CODE];   // exception code set by slave on error
                    JsonObject metric = new JsonObject();
                    metric.addProperty("Value", "Error");
                    metric.addProperty("Unit", "");
                    jData.add(cmdName, metric);
                    
                } else {


                    JsonObject metric = new JsonObject();
                    if (cmdDataType.equals("Float")) {
                        if (rData[EXP_INDEX] >= (byte)(-3) && rData[EXP_INDEX] <= (byte)10){
                        	
                        	Double sDatadbValue = commandProcessor.getDbDataValue(rData, DATA_INDEX);
                        	metric.addProperty("Value", sDatadbValue);
                        } else {
                        	rData[EXP_INDEX]=(byte)0x00;   // ignoring exponent
                        	logger.debug("Ignoring Exponent..");
                        	Double sDatadbValue = commandProcessor.getDbDataValue(rData, DATA_INDEX);
                        	metric.addProperty("Value", sDatadbValue);
                        }
                    } else {
                        if (rData[EXP_INDEX] >= (byte)(-3) && rData[EXP_INDEX] <= (byte)10){
                        	Integer sDataiValue = commandProcessor.getIDataValue(rData, DATA_INDEX);
                        	
                        	metric.addProperty("Value", sDataiValue);
                        } else {
                        	rData[EXP_INDEX]=(byte)0x00;   // ignoring exponent
                        	logger.debug("Ignoring Exponent..");
                        	Integer sDataiValue = commandProcessor.getIDataValue(rData, DATA_INDEX);
                        	
                        	metric.addProperty("Value", sDataiValue);
                        }
                    }
                    metric.addProperty("Unit", cmdUnit);

                    jData.add(cmdName, metric);

                }
                //error=false;
            }// end of for

            //populating json object

            jBody.add("Data", jData);

            JsonObject jRequestArguments = new JsonObject();
            jRequestArguments.addProperty("Scope", jsonRequest.getScope());
            jRequestArguments.addProperty("DeviceId", jsonRequest.getDeviceId());
            jRequestArguments.addProperty("DataCollection", jsonRequest.getDataCollection());
            jHeader.add("RequestArguments", jRequestArguments);

            JsonObject jStatus = new JsonObject();
            if (error) {
                jStatus.addProperty("Code", excCode);
                jStatus.addProperty("Reason", "Protocol Error");
                jStatus.addProperty("UserMessage", "Unkown Command");
            } else {
                jStatus.addProperty("Code", 0);
                jStatus.addProperty("Reason", "");
                jStatus.addProperty("UserMessage", "");
            }

            jHeader.add("Status", jStatus);

            jHeader.addProperty("Timestamp", dateStr);


            JsonObject root = new JsonObject();
            root.add("Header", jHeader);
            root.add("Body", jBody);
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            jsonString = gson.toJson(root);
            //System.out.println(jsonString);
            //logger.debug("Built the cmdResponse Object.Returning...");
            logger.debug("processMinMaxInverterData :: jsonString" + jsonString);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return jsonString;
    }

    public String process3PInverterData(DataCollection dataCollection,
            JSONRequest jsonRequest) {

        byte deviceoption = 0x01;
        String jsonString = "";
        int cmdResBytes=0;
        int excCode=0;


        try {
            logger.debug("In process3PInverterData()... ");
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");  // set current system date time in RFC-3339 format
            String dateStr = f.format(new Date());
            JsonObject jHeader = new JsonObject();
            JsonObject jBody = new JsonObject();
            JsonObject jData = new JsonObject();

            DataCollectionSet.DataCollection.CommandSet commandSet = dataCollection.getCommandSet();
            List<DataCollectionSet.DataCollection.CommandSet.Command> commands = commandSet.getCommand();


            for (int j = 0; j < commands.size(); j++) {

                logger.debug("inside for loop j=" + j);

                DataCollectionSet.DataCollection.CommandSet.Command command = commands.get(j);
                String cmdName = command.getName();
                String cmdCode = command.getCode();
                String regAddrHi = command.getRegAddrHi();
                String regAddrLo = command.getRegAddrLo();
                String regCountHi = command.getRegCountHi();
                String regCountLo = command.getRegCountLo();
                String cmdUnit = command.getUnit();
                String cmdDataType = command.getDataType();
                cmdResBytes = command.getResponseBytes();
                if (cmdResBytes == 0) {
                    logger.error("ResponseBytes not defined for command Name::" + cmdName);
                    return jsonString;
                }

                // Building the command sequence to be sent here
                byte[] cmdSeq = commandProcessor.buildCommandSeq (cmdCode, jsonRequest.getDeviceId(), 
    					regAddrHi, regAddrLo, regCountHi, regCountLo);
                String sBytes = commandProcessor.bytesToHexString(cmdSeq);


                logger.debug("process3PInverterData():Send bytes = " + sBytes);

                // Send the command sequence and receive the response bytes
                // in rData.
                byte[] rData = commandProcessor.ModbusCmdTxRx(cmdSeq, cmdResBytes);
                numBytesRcvd = commandProcessor.numBytesRcvd;
                if (numBytesRcvd <= 0) {
                    logger.debug("Unable to read data from port");
                    return null;
                }

                //process protocol error(Unknown Command) here.
                if (rData[1] == (byte)ERROR_CODE) { // function code is set on error by the slave
                    error = true;
                    excCode = rData[EXCEPTION_CODE];   // exception code set by slave on error
                    JsonObject metric = new JsonObject();
                    metric.addProperty("Value", "Error");
                    metric.addProperty("Unit", "");
                    jData.add(cmdName, metric);
                } else {

                    JsonObject metric = new JsonObject();
                    if (cmdDataType.equals("Float")) {
                        if (rData[EXP_INDEX] >= (byte)(-3) && rData[EXP_INDEX] <= (byte)10){
                        	
                        	Double sDatadbValue = commandProcessor.getDbDataValue(rData, DATA_INDEX);
                        	metric.addProperty("Value", sDatadbValue);
                        } else {
                        	rData[EXP_INDEX]=(byte)0x00;   // ignoring exponent
                        	logger.debug("Ignoring Exponent..");
                        	Double sDatadbValue = commandProcessor.getDbDataValue(rData, DATA_INDEX);
                        	metric.addProperty("Value", sDatadbValue);
                        }
                    } else {
                        if (rData[EXP_INDEX] >= (byte)(-3) && rData[EXP_INDEX] <= (byte)10){
                        	Integer sDataiValue = commandProcessor.getIDataValue(rData, DATA_INDEX);
                        	
                        	metric.addProperty("Value", sDataiValue);
                        } else {
                        	rData[EXP_INDEX]=(byte)0x00;   // ignoring exponent
                        	logger.debug("Ignoring Exponent..");
                        	Integer sDataiValue = commandProcessor.getIDataValue(rData, DATA_INDEX);
                        	
                        	metric.addProperty("Value", sDataiValue);
                        }
                    }
                    metric.addProperty("Unit", cmdUnit);

                    jData.add(cmdName, metric);

                }

                //error=false;
            }// end of for

            //populating json object

            jBody.add("Data", jData);

            JsonObject jRequestArguments = new JsonObject();
            jRequestArguments.addProperty("Scope", jsonRequest.getScope());
            jRequestArguments.addProperty("DeviceId", jsonRequest.getDeviceId());
            jRequestArguments.addProperty("DataCollection", jsonRequest.getDataCollection());
            jHeader.add("RequestArguments", jRequestArguments);

            JsonObject jStatus = new JsonObject();
            if (error) {
                jStatus.addProperty("Code", excCode);
                jStatus.addProperty("Reason", "Protocol Error");
                jStatus.addProperty("UserMessage", "Unkown Command");
            } else {
                jStatus.addProperty("Code", 0);
                jStatus.addProperty("Reason", "");
                jStatus.addProperty("UserMessage", "");
            }

            jHeader.add("Status", jStatus);

            jHeader.addProperty("Timestamp", dateStr);

            JsonObject root = new JsonObject();
            root.add("Header", jHeader);
            root.add("Body", jBody);
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            jsonString = gson.toJson(root);

            logger.debug("process3PInverterData :: jsonString" + jsonString);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return jsonString;
    }

    //16-Dec-2014.Test changes done for modbus sim
    //send mesg is hard coded.
    public String processCumulativeInverterData(DataCollection dataCollection,
            JSONRequest jsonRequest) {

        byte deviceoption = 0x01;
        String jsonString = "";
        int cmdResBytes=0;
        int excCode=0;

        try {

            logger.debug("In processCumulativeInverterData()... ");

            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");  // set current system date time in RFC-3339 format
            String dateStr = f.format(new Date());


            JsonObject jHeader = new JsonObject();
            JsonObject jBody = new JsonObject();
            JsonObject jData = new JsonObject();

            DataCollectionSet.DataCollection.CommandSet commandSet = dataCollection.getCommandSet();
            List<DataCollectionSet.DataCollection.CommandSet.Command> commands = commandSet.getCommand();


            // Device status info
            //DeviceStatus deviceStatus = new DeviceStatus();

            for (int j = 0; j < commands.size(); j++) {

                logger.debug("inside for loop j=" + j);

                DataCollectionSet.DataCollection.CommandSet.Command command = commands
                        .get(j);
                String cmdName = command.getName();
                String cmdCode = command.getCode();
                String regAddrHi = command.getRegAddrHi();
                String regAddrLo = command.getRegAddrLo();
                String regCountHi = command.getRegCountHi();
                String regCountLo = command.getRegCountLo();
                String cmdUnit = command.getUnit();
                String cmdDataType = command.getDataType();
                cmdResBytes = command.getResponseBytes();
                if (cmdResBytes == 0) {
                    logger.error("ResponseBytes not defined for command Name::" + cmdName);
                    return jsonString;
                }
                //Test
                System.out.println("cmd unit: "+cmdUnit);
                
                // Building the command sequence to be sent here
                byte[] cmdSeq = commandProcessor.buildCommandSeq (cmdCode, jsonRequest.getDeviceId(), 
    					regAddrHi, regAddrLo, regCountHi, regCountLo);
                String sBytes = commandProcessor.bytesToHexString(cmdSeq);

                // System.out.println("Cmd Seq: "+cmdSeq);
                logger.debug("processCumulativeInverterData(): Send bytes " + sBytes);
                
                //Test.16-Dec-2014,modbus message.
                //byte[] modbusMsg = new byte[] {(byte)0x01, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0xB1, (byte)0xC8};

                // Send the command sequence and receive the response bytes
                // in rData.
              //Test.16-Dec-2014.commented
                byte[] rData = commandProcessor.ModbusCmdTxRx(cmdSeq, cmdResBytes);
                //byte[] rData = commandProcessor.ModbusCmdTxRx(modbusMsg, cmdResBytes);
                
                String rBytes = commandProcessor.bytesToHexString(rData);
                //logger.debug("processCumulativeInverterData(): Rcvd bytes in hex: " + rBytes);
                System.out.println("Test modbus msg: "+rBytes);

                numBytesRcvd = commandProcessor.numBytesRcvd;
                if (numBytesRcvd <= 0) {
                    logger.debug("Unable to read data from port");
                    return null;
                }
                // }
                //process protocol error(Unknown Command) here.
                if (rData[FUNCTION_CODE_INDEX] == (byte)ERROR_CODE) { // function code is set on error by the slave
                    error = true;
                    excCode = rData[EXCEPTION_CODE];   // exception code set by slave on error
                    
                    JsonObject metric = new JsonObject();
                    metric.addProperty("Value", "Error");
                    metric.addProperty("Unit", "");
                    
                    jData.add(cmdName, metric); // added, 18-Dec-2014
                } else {

                    if (cmdUnit.length() > 0) {

                        JsonObject metric = new JsonObject();
                        if (cmdDataType.equals("Float")) {
                            if (rData[EXP_INDEX] >= (byte)(-3) && rData[EXP_INDEX] <= (byte)10){
                            	
                            	Double sDatadbValue = commandProcessor.getDbDataValue(rData, DATA_INDEX);
                            	metric.addProperty("Value", sDatadbValue);
                            } else {
                            	rData[EXP_INDEX]=(byte)0x00;   // ignoring exponent
                            	logger.debug("Ignoring Exponent..");
                            	Double sDatadbValue = commandProcessor.getDbDataValue(rData, DATA_INDEX);
                            	metric.addProperty("Value", sDatadbValue);
                            }
                            
                        } else {
                            
                            if (rData[EXP_INDEX] >= (byte)(-3) && rData[EXP_INDEX] <= (byte)10){
                            	Integer sDataiValue = commandProcessor.getIDataValue(rData, DATA_INDEX);
                            	
                            	metric.addProperty("Value", sDataiValue);
                            } else {
                            	rData[EXP_INDEX]=(byte)0x00;   // ignoring exponent
                            	logger.debug("Ignoring Exponent..");
                            	Integer sDataiValue = commandProcessor.getIDataValue(rData, DATA_INDEX);
                            	
                            	metric.addProperty("Value", sDataiValue);
                            }
                        }

                        metric.addProperty("Unit", cmdUnit);

                        jData.add(cmdName, metric);

                    } else if (cmdUnit.length() == 0) {
                        JsonObject metric = new JsonObject();

                        metric.addProperty("StatusCode", (int) rData[STATUS_INDEX]); // status of modbus slave device
                        metric.addProperty("MgmtTimerRemainingTime", 0);
                        metric.addProperty("ErrorCode", 0);
                        metric.addProperty("LEDColor", 0);
                        metric.addProperty("LEDState", 0);
                        metric.addProperty("StateToReset", false);
                        jData.add(cmdName, metric); 

                    } 
                }

                //error=false;
            }// end of for

            //populating json objects

            jBody.add("Data", jData);

            JsonObject jRequestArguments = new JsonObject();
            jRequestArguments.addProperty("Scope", jsonRequest.getScope());
            jRequestArguments.addProperty("DeviceId", jsonRequest.getDeviceId());
            jRequestArguments.addProperty("DataCollection", jsonRequest.getDataCollection());
            jHeader.add("RequestArguments", jRequestArguments);

            JsonObject jStatus = new JsonObject();
            if (error) {
                jStatus.addProperty("Code", excCode);
                jStatus.addProperty("Reason", "Protocol Error");
                jStatus.addProperty("UserMessage", "Unkown Command");
            } else {
                jStatus.addProperty("Code", 0);
                jStatus.addProperty("Reason", "");
                jStatus.addProperty("UserMessage", "");
            }

            jHeader.add("Status", jStatus);

            jHeader.addProperty("Timestamp", dateStr);

            JsonObject root = new JsonObject();
            root.add("Header", jHeader);
            root.add("Body", jBody);
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            jsonString = gson.toJson(root);

            logger.debug("processCumulativeInverterData :: jsonString" + jsonString);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return jsonString;

    }

    public String processCommonInverterData(DataCollectionSet.DataCollection dataCollection, JSONRequest jsonRequest) {


        byte deviceoption = 0x01; //01-Inverter, 02-Sensorcard

        String jsonString = "";
        int cmdResBytes=0;
        int excCode=0;
        
        try {

            logger.debug("Inside processCommonInverterData()");
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");  // set current system date time in RFC-3339 format
            String dateStr = f.format(new Date());
            JsonObject jHeader = new JsonObject();
            JsonObject jBody = new JsonObject();
            JsonObject jData = new JsonObject();

            DataCollectionSet.DataCollection.CommandSet commandSet = dataCollection.getCommandSet();
            List<DataCollectionSet.DataCollection.CommandSet.Command> commands = commandSet.getCommand();

            logger.debug("In processCommonInverterData()");


            for (int j = 0; j < commands.size(); j++) {

                logger.debug("inside for loop j=" + j);

                DataCollectionSet.DataCollection.CommandSet.Command command = commands
                        .get(j);
                String cmdName = command.getName();
                String cmdCode = command.getCode();
                String regAddrHi = command.getRegAddrHi();
                String regAddrLo = command.getRegAddrLo();
                String regCountHi = command.getRegCountHi();
                String regCountLo = command.getRegCountLo();
                String cmdUnit = command.getUnit();
                String cmdDataType = command.getDataType();
                cmdResBytes = command.getResponseBytes();
                if (cmdResBytes == 0) {
                    logger.error("ResponseBytes not defined for command Name::" + cmdName);
                    return jsonString;
                }

                // Building the command sequence to be sent here
                byte[] cmdSeq = commandProcessor.buildCommandSeq (cmdCode, jsonRequest.getDeviceId(), 
                					regAddrHi, regAddrLo, regCountHi, regCountLo);

                String sBytes = commandProcessor.bytesToHexString(cmdSeq);

                // System.out.println("Cmd Seq: "+cmdSeq);
                logger.debug("processCommonInverterData(): Send bytes: " + sBytes);

                // Send the command sequence and receive the response bytes
                // in rData.
                byte[] rData = commandProcessor.ModbusCmdTxRx(cmdSeq, cmdResBytes);
                numBytesRcvd = commandProcessor.numBytesRcvd;

                if (numBytesRcvd <= 0) {
                    logger.debug("Unable to read data from port");
                    return null;
                }

                //process protocol error(Unknown Command) here.
                // need change for modbus
                if (rData[FUNCTION_CODE_INDEX] == (byte)ERROR_CODE) { // function code is set on error by the slave
                    error = true;
                    excCode = rData[EXCEPTION_CODE];   // exception code set by slave on error
                    JsonObject metric = new JsonObject();
                    metric.addProperty("Value", "Error");
                    metric.addProperty("Unit", "");
                    jData.add(cmdName, metric); // added, 18-Dec-2014
                } else {

                    if (cmdUnit.length() > 0) {
                        JsonObject metric = new JsonObject();
                        if (cmdDataType.equals("Float")) {
                            if (rData[EXP_INDEX] >= (byte)(-3) && rData[EXP_INDEX] <= (byte)10){
                            	
                            	Double sDatadbValue = commandProcessor.getDbDataValue(rData, DATA_INDEX);
                            	metric.addProperty("Value", sDatadbValue);
                            } else {
                            	rData[EXP_INDEX]=(byte)0x00;   // ignoring exponent
                            	logger.debug("Ignoring Exponent..");
                            	Double sDatadbValue = commandProcessor.getDbDataValue(rData, DATA_INDEX);
                            	metric.addProperty("Value", sDatadbValue);
                            }
                        } else {
                            if (rData[EXP_INDEX] >= (byte)(-3) && rData[EXP_INDEX] <= (byte)10){
                            	Integer sDataiValue = commandProcessor.getIDataValue(rData, DATA_INDEX);
                            	
                            	metric.addProperty("Value", sDataiValue);
                            } else {
                            	rData[EXP_INDEX]=(byte)0x00;   // ignoring exponent
                            	logger.debug("Ignoring Exponent..");
                            	Integer sDataiValue = commandProcessor.getIDataValue(rData, DATA_INDEX);
                            	
                            	metric.addProperty("Value", sDataiValue);
                            }
                        }
                        metric.addProperty("Unit", cmdUnit);

                        jData.add(cmdName, metric);


                    } else if (cmdUnit.length() == 0) {

                        JsonObject metric = new JsonObject();

                        metric.addProperty("StatusCode", (int) rData[STATUS_INDEX]);
                        metric.addProperty("MgmtTimerRemainingTime", 0);
                        metric.addProperty("ErrorCode", 0);
                        metric.addProperty("LEDColor", 0);
                        metric.addProperty("LEDState", 0);
                        metric.addProperty("StateToReset", false);
                        jData.add(cmdName, metric);

                    }
                }

                //error=false;
            }// end of for


            jBody.add("Data", jData);

            JsonObject jRequestArguments = new JsonObject();
            jRequestArguments.addProperty("Scope", jsonRequest.getScope());
            jRequestArguments.addProperty("DeviceId", jsonRequest.getDeviceId());
            jRequestArguments.addProperty("DataCollection", jsonRequest.getDataCollection());
            jHeader.add("RequestArguments", jRequestArguments);

            JsonObject jStatus = new JsonObject();
            if (error) {
                jStatus.addProperty("Code", excCode);
                jStatus.addProperty("Reason", "Protocol Error");
                jStatus.addProperty("UserMessage", "Unkown Command");
            } else {
                jStatus.addProperty("Code", 0);
                jStatus.addProperty("Reason", "");
                jStatus.addProperty("UserMessage", "");
            }

            jHeader.add("Status", jStatus);

            jHeader.addProperty("Timestamp", dateStr);

            JsonObject root = new JsonObject();
            root.add("Header", jHeader);
            root.add("Body", jBody);
            Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
            jsonString = gson.toJson(root);

            logger.debug("processCommonInverterData :: jsonString" + jsonString);
        } catch (Exception e) {

            e.printStackTrace();
        }


        return jsonString;
    }

    public String processData(DataCollectionSet dataCollectionSet, JSONRequest jsonRequest) {

        String cmdResponse = null;

        try {
            logger.debug("DataCollection.processData() ----------- ");
            List<DataCollectionSet.DataCollection> dataCollections = dataCollectionSet.getDataCollection();

            DataCollectionSet.DataCollection dataCollection = null;
            String dataCollectionNameFromRequest = null;


            for (int i = 0; i < dataCollections.size(); i++) {
                dataCollection = dataCollections.get(i);
                String name = dataCollection.getName();
                if (name.trim().equalsIgnoreCase(jsonRequest.getDataCollection())) {
                    break;
                }
            }

            dataCollectionNameFromRequest = jsonRequest.getDataCollection().trim();

            logger.debug("dataCollectionNameFromRequest :: " + dataCollectionNameFromRequest);

            if (dataCollectionNameFromRequest.equalsIgnoreCase("CommonInverterData") || dataCollectionNameFromRequest.equalsIgnoreCase("PumpInverterData")) {
                logger.debug(" Data Collection Name is CommonInverterData..");

                cmdResponse = processCommonInverterData(dataCollection, jsonRequest);
                //logger.debug("cmd response :: " + cmdResponse.toString());


            } else if (dataCollectionNameFromRequest.equalsIgnoreCase("CumulativeInverterData")) {
                logger.debug(" Data Collection Name is CumulativeInverterData..");

                cmdResponse = processCumulativeInverterData(dataCollection, jsonRequest);
            } else if (dataCollectionNameFromRequest.equalsIgnoreCase("3PInverterData")) {
                logger.debug(" Data Collection Name is 3PInverterData..");

                cmdResponse = process3PInverterData(dataCollection, jsonRequest);
            } else if (dataCollectionNameFromRequest.equalsIgnoreCase("MinMaxInverterData")) {
                logger.debug(" Data Collection Name is MinMaxInverterData..");

                cmdResponse = processMinMaxInverterData(dataCollection, jsonRequest);
            } else if (dataCollectionNameFromRequest.equalsIgnoreCase("NowSensorData")) {
                logger.debug(" Data Collection Name is NowSensorData..");

                cmdResponse = processNowSensorData(dataCollection, jsonRequest);
            } else if (dataCollectionNameFromRequest.equalsIgnoreCase("MinMaxSensorData")) {
                logger.debug(" Data Collection Name is MinMaxSensorData..");

                cmdResponse = processMinMaxSensorData(dataCollection, jsonRequest);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }


        return cmdResponse;
    }
}
