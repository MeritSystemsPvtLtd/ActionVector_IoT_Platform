import java.util.ArrayList;

import serialportapp.SerialPortApp;

public class Testmod {
	
	
	public static void main(String[] args) {

			
			
			String jsonRequest = "{\"Scope\":\"Device\",\"DeviceId\":1,\"DataCollection\":\"CommonInverterData\"}";
			//String jsonRequest = "{\"Scope\":\"Device\",\"DeviceId\":1,\"DataCollection\":\"CumulativeInverterData\"}";
			//String jsonRequest = "{\"Scope\":\"Device\",\"DeviceId\":1,\"DataCollection\":\"MinMaxInverterData\"}";
			//String jsonRequest = "{\"Scope\":\"Device\",\"DeviceId\":1,\"DataCollection\":\"3PInverterData\"}";
			//String jsonRequest = "{\"Scope\":\"Device\",\"DeviceId\":1,\"DataCollection\":\"NowSensorData\"}";
			//String jsonRequest = "{\"Scope\":\"Device\",\"DeviceId\":1,\"DataCollection\":\"MinMaxSensorData\"}";
			//String jsonRequest = "Inverter";
			//String jsonRequest = "SensorCard";
			
			
	        SerialPortApp serialPort = new SerialPortApp();
			
			
	        String response = serialPort.getDeviceData(jsonRequest);
	        
	        if (response != null) {
	        	System.out.println("Response is: "+response);
	        } else {
	        	System.out.println("Unable to process input");
	        }
			
			

   	        

	}

}
