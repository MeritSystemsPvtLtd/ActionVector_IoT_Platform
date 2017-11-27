/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parsers;

import com.prokosha.adapter.etl.ETLAdapter.*;
import com.prokosha.dbconnection.ConnectionDAO;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author satya
 */
public class GmetadDownParser 
{
    HashMap<String, ReportData> metricValueMap = new HashMap<String, ReportData>();
    HashMap<String, ReportData> availMetricValueMap = new HashMap<String, ReportData>();
    private static Logger log = Logger.getLogger(EventLogParser.class.getName());
    ETLAdapter etl = null;
    PersistReportData dataInsert = null;
    int insertStatus = 0;
    String defaultKeyConstants[]=CEPEventMetricsMapping.getSzdefaultConstants();
    
     public GmetadDownParser(String CEPEvent, String metrics, Properties properties, String eventID) {
        initialize(CEPEvent, metrics, properties, eventID);
    }
    public void initialize(String CEPEvent, String metrics, Properties properties, String eventID)
    {
          String metricsToken[] = null;
          ReportData reportData = null;
          String customerID = null;// default avilability should be 1
          String availability = "1";// default avilability should be 1
          String metricToken[] = null;
          String metricType = null;
          boolean flag2=false;
          String service=null;
          String subService=null;
          
    try
    {
            Map<String,String> mp_string_to_map=convertStringToMap(CEPEvent);
            String parseToken[] = CEPEvent.split(",");
            metricsToken = metrics.split(" ");
            dataInsert = new PersistReportData();
            int metricLen = metricsToken.length;//no of object based on required metrics
            int parseLen = parseToken.length;//total no of metrics in the event stream
             flag2 = service == null || subService == null ;
             System.out.println("Customer ID In Default Parser==="+mp_string_to_map.get(defaultKeyConstants[3].trim()));
             Connection connection = ConnectionDAO.getConnection(mp_string_to_map.get(defaultKeyConstants[3].trim()));

                    Statement stmt = connection.createStatement();
                    System.out.println("Query=="+"Select service,subservice,hostgroup,resourceType,resourceSubType,resourceId "
                            + "from hostinfo where lower(resourceId)=lower('" + mp_string_to_map.get(defaultKeyConstants[2]) 
                            + "') and customerid='" + mp_string_to_map.get(defaultKeyConstants[19]) + "'");
                    ResultSet rs = stmt.executeQuery("Select service,subservice,hostgroup,resourceType,resourceSubType,resourceId "
                            + "from hostinfo where lower(resourceId)=lower('" + mp_string_to_map.get(defaultKeyConstants[2]) 
                            + "') and customerid='" + mp_string_to_map.get(defaultKeyConstants[19]) + "'");
                    while (rs.next()) {
                        if (service == null || service.equalsIgnoreCase("null")) {
                            service = rs.getString("service");
                        }
                        if (subService == null || subService.equalsIgnoreCase("null")) {
                            subService = rs.getString("subservice");
                        }
                       
                    }

            
                for (int i = 0; i < metricLen; i++) {
                    String mName = null;
                    String mValue = null;
                    String token[] = null;
                    metricType = null;
                    metricToken = null;
                    reportData = null;
                    boolean vfound = false;
                    String Type=null;
                    String staticMetricValue = "1"; //indicates occurence, no specific value
                    //for (int j = 0; j < parseLen && (vfound == false); j++) {
                        //token = parseToken[j].trim().split("=");
                        if (mp_string_to_map.containsKey(metricsToken[i])) {
                            vfound = true;
                            mName = metricsToken[i];
                            mValue = mp_string_to_map.get(metricsToken[i]);
                            Type=mValue;
                           
                            if(token[1]!=null && token[1]!="null"){
                            reportData = new ReportData();
                            if(token[1]!=null && token[1]!="null"){
                           
                            if (mValue!=null) {

                                metricType = properties.getProperty(mName);
                                log.debug("MetricType,Category====" + metricType);


                                metricToken = metricType.split(",");

                                reportData.setHost(mp_string_to_map.get(defaultKeyConstants[10]));
                                reportData.setService(service);
                                reportData.setSubService(subService);
                                reportData.setHostGroup("");
                                reportData.setTimestamp1(mp_string_to_map.get(defaultKeyConstants[0]));

                                reportData.setMetricType(Type);
                                reportData.setValue("0");
                                reportData.setCategory(metricToken[1]);
                                reportData.setEventID(eventID);

                                reportData.setResourceType(mp_string_to_map.get(defaultKeyConstants[4]));
                                reportData.setResourceSubType(mp_string_to_map.get(defaultKeyConstants[1]));
                                reportData.setResourceId(mp_string_to_map.get(defaultKeyConstants[2]));
                                reportData.setAvailabilty(availability);
                                reportData.setCCustomerID(Integer.parseInt(mp_string_to_map.get(defaultKeyConstants[19])));

                                log.debug("Setting The Report Data Sucessfull");
                                metricValueMap.put(metricsToken[i], reportData);
                            }
                            }
                            }else
                               {
                                   System.out.println("*******Metrics Values is Null Event Discarded******");
                               }
                        }
                    //}
                    mName = null;
                    mValue = null;
                    metricType = null;
                    metricToken = null;
                    reportData = null;
                    token = null;
                }
                
                this.insertStatus = dataInsert.sendToDatabse(metricValueMap,mp_string_to_map.get(defaultKeyConstants[3].trim()));

    
    }catch(Exception e)
    {
        e.printStackTrace();
        if(e.getMessage().contains("java.net.SocketException: Broken pipe")){
                            System.out.println("SokectException :Broken pipe");
                        System.out.println("calling ConDAO.closeConn()");
                ConnectionDAO.closeConnection(customerID);
                System.out.println("exited from ConDAO.closeCon");
                        }
    }
    }
    
    public Map<String,String> convertStringToMap(String szCepEvent)
    {
        Map<String,String> metrics_map=new HashMap<String,String>();
        String splitByComma[]=szCepEvent.split(",");
        
            for(int i=0;i<splitByComma.length;i++)
            {
                String keyValueSplit[]=splitByComma[i].split("=");
                metrics_map.put(keyValueSplit[0].trim(), keyValueSplit[1].trim());
               

            }
        
        
        
        
        
        System.out.println("After Converted=="+metrics_map);
        return metrics_map;
        
    }
     public int getInsertStatus() {
        return this.insertStatus;
    }
    

    
}
