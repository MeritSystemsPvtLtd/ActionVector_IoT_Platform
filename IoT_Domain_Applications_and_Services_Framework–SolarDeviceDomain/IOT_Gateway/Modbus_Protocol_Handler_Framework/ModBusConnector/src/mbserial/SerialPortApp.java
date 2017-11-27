package mbserial;

import org.apache.log4j.Logger;

/*
 * This is the main class of the application which exposes the user APIs. 
 * API:
 * 	1. getDeviceData: gets the data in JSON format from the fronius inverters/sensor cards depending 
 * on the query sets (data collections) supplied by the user. If the input is either "Inverter" or "SensorCard",
 * it returns the active devices (inverters/sensorcards) in the network.
 * 	
 */
public class SerialPortApp {
	public static final int MAX_IN_BUF_SIZE = 20;
	public static final int MAX_OUT_BUF_SIZE = 120;
	private static Logger logger = null;

	public SerialPortApp() {
		try {
			logger = Logger.getLogger("SerialPortApp");
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/*Get device data based on 
	 * Input (for querying measured values): JSON String - scope, deviceid,  datacollection
	 * 		scope: "Device"
	 * 		deviceid: 0 .. 99
	 * 		datacollection: datacollection name
	 * Input (for broadcast command, GetActiveDeviceInfo): "Inverter" or "SensorCard"
	 * Output: JSON String
	 * Remarks: Device can be either inverter or sensor which is determined by the data collection name given as input.
	 */
	public String getDeviceData(String jsonString) {
		JSONBuilder jsonBuilder = null;
		String jsonResponseString = null;
		
		
		DataCollectionProcessor dataCollectionProcessor = null;
		
		try{
			logger.debug("-------------------BEGIN---------------------");
			logger.debug("Inside getDeviceData() ----");
		
		}catch(Exception e){
			logger.debug(e.getMessage());
		}

		try{
			jsonBuilder = new JSONBuilder ();
			dataCollectionProcessor = new DataCollectionProcessor();
			if (jsonString.equals("Inverter") | jsonString.equals("SensorCard")) {
				jsonResponseString = dataCollectionProcessor.processGetActiveDeviceInfo(jsonString);
			} else {
			
				DataCollectionSet dataCollectionSet = XMLtoJavaSingleton.loadDataCollections();

				
				JSONRequest jsonRequest = jsonBuilder.getJSONRequest(jsonString);		
					
				jsonResponseString = dataCollectionProcessor.processData(dataCollectionSet, jsonRequest);
			}
				if (jsonResponseString == null){
					logger.debug("Unable to process input");
					return null;
				}
				logger.debug("In getDeviceData(): cmdresponse is="+ jsonResponseString);
				
			 
		} catch(NullPointerException e){
			logger.debug("java object for request " + e.getMessage());
		} catch(Exception e){
			logger.debug("java object for request " + e.getMessage());

		}

		return jsonResponseString;
	}
	
	/*
	 * Input - "Inverter" or "SensorCard"
	 * Output - JSON String
	 */
	public String getActiveDeviceInfo(String deviceclass) {		

		
		String jsonResponseString = null;
		
		DataCollectionProcessor dataCollectionProcessor = null;
		
		try {

			logger.debug("In getActiveDeviceInfo()");
						
			dataCollectionProcessor = new DataCollectionProcessor();
			jsonResponseString=dataCollectionProcessor.processGetActiveDeviceInfo(deviceclass);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return jsonResponseString;

	}
	
}
