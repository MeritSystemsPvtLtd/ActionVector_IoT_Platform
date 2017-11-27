package serialportapp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/*
 * This class puts the JSON request in java object format, object response in JSON format.  
 * Methods:
 * 	1. buildJSONResponse: 
 * 	2. getJSONRequest: 
 * 	
 */
public class JSONBuilder {
	
	private static Logger logger= null;

	public JSONBuilder() {
		try{
		logger = Logger.getLogger("JSONBuilder");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}

	}
	
	/*
	 * Input: String in json format
	 * Output: Object
	 */
	public JSONRequest getJSONRequest(String jsonString){
		Gson gson = new Gson();
		JSONRequest jsonRequest = null;
		
		
		// convert String into InputStream
		try {
			logger.debug("getJSONRequest(");
			
			InputStream inputStream = new ByteArrayInputStream(
					jsonString.getBytes());

			// read it with BufferedReader
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

			jsonRequest = gson.fromJson(br, JSONRequest.class);
			//jsonRequest = gson.fromJson(jsonString, JSONRequest.class);
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonRequest;

	}
	
	

}
