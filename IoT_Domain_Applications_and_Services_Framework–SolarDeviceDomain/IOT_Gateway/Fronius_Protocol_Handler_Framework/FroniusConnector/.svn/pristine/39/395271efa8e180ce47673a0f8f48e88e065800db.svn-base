package serialportapp;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

public class XMLtoJavaSingleton {
	// File Name: Singleton.java
	
		private static Logger logger= null;
		static Port port=null;
	
	   //private static XMLtoJavaSingleton xmltojavaSingleton = new XMLtoJavaSingleton( );
	   
	   /* A private Constructor prevents any other 
	    * class from instantiating.
	    */
	   private XMLtoJavaSingleton(){ 
		   try{
				logger = Logger.getLogger("XMLtoJavaSingleton");
				}catch(Exception e){
					System.out.println(e.getMessage());
				}
	   }
	   
	   /* Static 'instance' method */
	   /*
	   public static Singleton getInstance( ) {
	      return xmltojavaSingleton;
	   }
	   */
	   /* Other methods protected by singleton-ness */
	   public static DataCollectionSet loadDataCollections( ) {
			try {
				//logger.debug("in loadDataCollections(): Loading dataCollectionSet.xml");
				JAXBContext context = JAXBContext.newInstance(DataCollectionSet.class);
				Unmarshaller un = context.createUnmarshaller();
				DataCollectionSet cmd = (DataCollectionSet) un.unmarshal(new File("dataCollectionSet.xml"));
				return cmd;
			} catch (JAXBException e) {
				logger.debug("Unable to load dataCollectionSet"+e.getMessage());
			}
			return null;
	   }
	   public static Port loadPortDetails(){
			try {
				//logger.debug("in loadPortDetails(): Loading port.xml");
				JAXBContext context = JAXBContext.newInstance(Port.class);
				Unmarshaller un = context.createUnmarshaller();
				//Port port = (Port) un.unmarshal(new File("port.xml"));
				
				port = (Port) un.unmarshal(new File("port.xml"));
				return port;
				
			} catch (JAXBException e) {
				logger.debug("Unable to load port details.."+e.getMessage());
			}
			return null;

	   }
}
