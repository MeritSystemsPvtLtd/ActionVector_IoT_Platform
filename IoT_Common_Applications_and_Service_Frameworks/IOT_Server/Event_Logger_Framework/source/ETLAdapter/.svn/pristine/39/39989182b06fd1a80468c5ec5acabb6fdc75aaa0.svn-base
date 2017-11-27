// A simple example to illustrate properties reloading.
package com.prokosha.adapter.etl.Listener.Filelistener;

import com.prokosha.adapter.etl.ETLAdapter.CEPEventMetricsMapping;
import com.prokosha.adapter.etl.ETLAdapter.ETLAdapter;
import java.util.*;
import java.io.*;
import qtart.QTartParser;


public class CofigureFile implements FileChangeListener {
    

    private boolean useClasspath;
    private long checkPeriod;
    private  String objectId=null;
    private   String PROPERTIES_FILE =null;
    Properties prop=null;

    public Properties getProp() {
        return prop;
    }

    public void setProp(Properties prop) {
        this.prop = prop;
    }
   
    public  CofigureFile(boolean b,String path,String id){
        this.useClasspath = b;
        PROPERTIES_FILE=path;
        objectId=id;
	reloadProperties();
    }
   
    public synchronized void setCheckPeriod(long period) {
        //System.out.println("Setting checkPeriod: " + period);
        checkPeriod = period;
	try
        {
	    FileMonitor.getInstance().addFileChangeListener(this, 
							PROPERTIES_FILE, 
							checkPeriod);
	}
	catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }

    
    
    
    public  void  reloadProperties() {
	Properties props = new Properties();
        InputStream in;

	try {
            if (useClasspath)
            {
	        in = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
	    }
	    else
            {
	        in = new FileInputStream(PROPERTIES_FILE);
	    }

	    props.load(in);
	    in.close();
	}
	catch (IOException e)
        {
	    e.printStackTrace();
	}
      
	setCheckPeriod(30);
       
        if(objectId.equalsIgnoreCase("ETL"))
        {
            ETLAdapter.setProperty(props);
        }
        if(objectId.equalsIgnoreCase("CEPMETRICS"))
        {

          CEPEventMetricsMapping.setProperty(props);
        }
        /*String load5=props.getProperty("Load5");
	String load15=props.getProperty("Load15");
        System.out.println("Load5=="+load5);
        System.out.println("Load15=="+load15);*/
        
    }

    public void fileChanged(String fileName) {
        reloadProperties();
    }

   

    public static void main(String[] args) throws Exception {
        boolean useClasspath = false;

	

       // CofigureFile ex = new CofigureFile(useClasspath);

	Thread.sleep(600000);
    }
}
