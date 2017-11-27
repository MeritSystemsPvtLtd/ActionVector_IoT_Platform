/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.adapter.etl.ETLAdapter;

import autoIDGenerator.AutoTimeStId;
import com.prokosha.adapter.etl.Listener.Filelistener.CofigureFile;
import com.prokosha.dbconnection.ConnectionDAO;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Anand
 */
public class ETLAdapter {

    ReadOpsView opsView = null;
    ReadPerfData perfData = null;
    private static Logger log = Logger.getLogger(ETLAdapter.class.getName());
    static boolean mapInitialized = false;
    static FileInputStream fstream1 = null;
    static DataInputStream din1 = null;
    static BufferedReader br1 = null;
    static Enumeration enum1 = null;
    static Properties properties = null;
    String metricValue = null;
    String home = System.getProperty("user.home");
    String szFile_Seperator = System.getProperty("file.separator");
    String path = home + szFile_Seperator + "ETLConfig" + szFile_Seperator + "metricsConfig.properties";
    String metricsConfigPath = home + szFile_Seperator + "ETLConfig" + szFile_Seperator;
    CofigureFile ex = null;
    CEPEventMetricsMapping cepEventMetrics = null;
    //public static int cCustomerID = -1;

    public ETLAdapter() {
        if (!mapInitialized) {
            ex = new CofigureFile(false, path, "ETL");
            initializeMap();
        }
    }

    public boolean initializeMap() {
        try {
            //properties.load(new FileInputStream(home+"/"+"ETLConfig/metricsConfig.properties"));
            //log.info("+++++properties = " + properties);
            enum1 = properties.propertyNames();
            //log.info("\t enum1" + enum1);
            mapInitialized = true;
        } catch (Exception e) {
            log.error("Exception :" + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }

    public String getMetricType(String key) {
        if (enum1.hasMoreElements()) {
            String metricKey = key;
            metricValue = properties.getProperty(metricKey);
        }
        return metricValue;
    }

    public static void setProperty(Properties prop) {
        properties = prop;
    }

    public static boolean initialize() {

        boolean intialized = ETLProperties.initialize();
        ConnectionDAO.initialize("ETL");
        if (!intialized) {
            log.error("Error in initializing ETL properties, exiting...");
            return false;
        } else {
            return true;
        }
    }

    // adding one more parameter "boolean flag" to dumpCEPEvent like 
    //dumpCEPEvent(String CEPEvent, boolean flag){}
    public String dumpCEPEvent(String CEPEvent) throws IOException {//dumpCEPEvent,

        Class msgClass = null;
        Object msgObj = null;
        String className = null;
        String token[] = null;
        String ResourceMapping = ETLProperties.getResourceMapping();

        String customerID = CEPEvent.split("CustomerID=")[1].trim();
        customerID = customerID.split(",")[0].trim();
        AutoTimeStId idGenrator = new AutoTimeStId(customerID);
        String eventID = idGenrator.getID();
        String statusId = "0";
        String service;

        try {
            //log.debug("CepEvent=="+CEPEvent);
            String tok[] = CEPEvent.split(",");
            String token1[] = tok[0].split("=");
            String streamName = token1[1];
            log.debug("CEPEvent======" + streamName);
            if (ResourceMapping.equalsIgnoreCase("true")) {
                String host = CEPEvent.split("HostName=")[1].trim();
                host = host.split(",")[0].trim();
                String resourcetype = CEPEvent.split("resourceType=")[1].trim();
                resourcetype = resourcetype.split(",")[0].trim();
                String resourcesubtype = CEPEvent.split("resourceSubType=")[1].trim();
                resourcesubtype = resourcesubtype.split(",")[0].trim();
                String resourceid = CEPEvent.split("resourceId=")[1].trim();
                resourceid = resourceid.split(",")[0].trim();
                String cCustomer = CEPEvent.split("cCustomer=")[1].trim();
                cCustomer = cCustomer.split(",")[0].trim();
                service = CEPEvent.split("service=")[1].trim();
                service = service.split(",")[0].trim();
                setHostInfo(host, customerID, resourcetype, resourcesubtype, resourceid, cCustomer, service);
            }
            cepEventMetrics = new CEPEventMetricsMapping();
            String metrics = cepEventMetrics.getMetricsForEvent(streamName);
            //log.debug("Metrics==="+metrics);
            token = metrics.split(",");
            //log.debug("SIze=="+token.length);
            if (metrics != null) {
                if (token.length > 1) {
                    className = token[0];
                    //  log.debug("Constructing class object for OvMsgXXX class::" + className);
                    msgClass = Class.forName(className);
                    // log.debug("Constructing ctor method for OvMsgXXX class::" + className);
                    Constructor msgObjConstructor = msgClass.getConstructor(Class.forName("java.lang.String"), Class.forName("java.lang.String"), Class.forName("java.util.Properties"), Class.forName("java.lang.String"));
                    //String load1= properties.getProperty("Load1");
                    //log.debug("Properties==="+properties);
                    // log.debug("Constructing object instance for OvMsgXXX class::" + className);
                    msgObj = msgObjConstructor.newInstance(CEPEvent, token[1], properties, eventID);

                    Class params[] = {};
                    Object paramsObjs[] = {};
                    Method getInsertStatus = msgClass.getDeclaredMethod("getInsertStatus", params);
                    Object insertStatus = getInsertStatus.invoke(msgObj, paramsObjs);
                    statusId = eventID + ":" + Integer.parseInt(insertStatus.toString());
                } else {
                    log.debug("NO Metrics Found in properties file");
                }
            } else {
                log.error("******************Stream Name Not Found Event Disgarded*********************");
            }
        } catch (Exception e) {
            log.error("Exception In ETLAdapter :" + e.getMessage());
            e.printStackTrace();
        } finally {

            cepEventMetrics = null;
        }
        return statusId;
    }

    public void dumpOpsviewEvent(String RTData) {
        try {
            opsView = new ReadOpsView();
//commented afterCustomerID    opsView.initialize(RTData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            opsView = null;
        }
    }

    public void dumpPerfDataEvent(String RTData) {
        try {
            perfData = new ReadPerfData();
            perfData.initilize(RTData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            perfData = null;
        }
    }

    public static void main(String[] arg) {
        ETLAdapter etl = new ETLAdapter();
        if (!(etl.initialize())) {
            log.error("Error in initializing ETL properties, exiting...");
            return;
        }
        PropertyConfigurator.configure(etl.metricsConfigPath + "logger.properties");
        try {
            Socket skt = null;
            InputStreamReader in = null;
            BufferedReader br = null;
            ServerSocket srvr = null;
            try {
                int serverPort = ETLProperties.getETLPort();
                System.out.println("ETL Receiving Port : " + serverPort);
                log.info("ETL Receiving Port : " + serverPort);
                srvr = new ServerSocket(serverPort);
                String data = "";
                int itest = 0;
                while (true) {
                    System.out.println("==============  itest = " + itest++);
                    try {
                        data = "";
                        skt = srvr.accept();
                        in = new InputStreamReader(skt.getInputStream());
                        br = new BufferedReader(in);
                        while ((data = br.readLine()) != null) {
                            System.out.println("Got the Event::" + data);
                            log.info("Got the Event::" + data);
                            try {
                                etl.dumpCEPEvent(data);
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (skt.isClosed() || skt.isConnected() || skt.isInputShutdown()) {
                                    System.out.println("closing socket....");
                                    break;
                                }
                            }
                            while (!br.ready()) {
                                Thread.sleep(2000);
                            }
                            continue;
                        }
                        if (br != null) {
                            try {
                                br.close();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        if (in != null) {
                            try {
                                in.close();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        if (skt != null) {
                            try {
                                skt.close();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }

                        br = null;
                        in = null;
                        skt = null;
                        continue;
                    } catch (Exception eee) {
                        eee.printStackTrace();
                    }
                }
            } catch (Exception e) {
                log.error("Error in ETL Socket receiver");
                e.printStackTrace();
                try {
                    br.close();
                    in.close();
                    skt.close();
                    srvr.close();
                    br = null;
                    in = null;
                    skt = null;
                    srvr = null;
                } catch (Exception ex) {
                    e.printStackTrace();
                    br = null;
                    in = null;
                    skt = null;
                    srvr = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void main1(String arg[]) {
        ETLAdapter etl = new ETLAdapter();
        PropertyConfigurator.configure("ETLConfig/logger.properties");
        try {
            String strLine1 = null;
            /* try{
             File file1 = new File("/home/mahesh/Desktop/cepEvents.log");
             if(file1.exists() && (file1.length() > 0)){
             fstream1 = new FileInputStream(file1);
             din1 = new DataInputStream(fstream1);
             br1 = new BufferedReader(new InputStreamReader(din1));
             while ((strLine1 = br1.readLine()) != null){
             etl.dumpCEPEvent(strLine1);
             //log.debug("Hai ....");
             Thread.sleep(10000);
             }
             }
             }catch(Exception e){
             e.printStackTrace();
             }*/

            // etl.dumpPerfDataEvent("1298614738||Bsefeed||151 Bse Memcache1||OK - object exists in memcached on 10.50.11.18:||server=10.50.11.18:11211, elapsedtime=0.000523, key=17023928, objval=Sensex000000025 Feb,11:49:5317725.920.017632.411702392821206.7712316.117724.9300000");
            // logwatch
            // etl.dumpCEPEvent("stream=LogWatchEvent,Latency=50,Hits=6606,Status=404,HostName=portfolio1,HostAddress=HealthMonitor,EndTime=2011-06-30 08:32:00,StartTime=2011-06-30 08:30:00,BucketID=1,ServiceClass=MONEY");
            // hralth
            //etl.dumpCEPEvent("stream=MailSearchEvent,TimeStamp=1314014160,DataLastPosted=-1,RecordsNotLoaded=-1,EmitFail=-1,EmitFailProcessed=-1,RecordsLoaded=-1,RecordsPosted=-1,Size=0,ServiceClass=MAIL_SEARCH,HostName=portfolio15:portfolio13,MetricType=TOBEPROCESSED");
            // ganglia
            //etl.dumpCEPEvent("stream=ServerStateEvent,MemBuffers=537960,MemTotal=4044348,ProcRun=0,CpuAidle=97.2,SwapFree=8385776,CpuSpeed=1595,MemCached=1507328,DiskTotal=62.666,OSRelease=2.6.18-164.el5,CpuNice=0.0,CpuWio=0.5,Load15=0.31,Load1=0.18,Load5=0.27,DiskFree=38.198,CpuNum=4,MachineType=x86_64,BytesOut=493486.81,MemShared=0,HostAddress=10.50.110.107,TN=60,TMAX=248,SwapTotal=8385888,MemFree=221312,OSName=Linux,OracleConnections=-1,ProcTotal=372,HostName=portfolio15,CpuUser=5.9,TimeStamp=1313991479,PartitionMaxUsed=66.2,PacketsIn=3857.82,BootTime=1297081057,PacketsOut=4184.47,CpuSystem=1.0,BytesIn=756191.75,CpuIdle=92.7");
            //QTart
            //stream=QtartStorageEvent,Alert=Storage,Messages=7,eventSource=n24.rfs208,Delay=3,HostGroup=Storage-VSNL,Filter=Storage-VSNL,EventSourceXml=/opt/sadesk/MailXmlBackup/Storage-VSNL-24-04-201209:46:20.xml
            //stream=QtartGranEvent,Alert=GranularTrends,Messages=640210,Delay=1,Duration=15:15,HostGroup=Local,Filter=Gran-Local,EventSourceXml=/opt/sadesk/MailXmlBackup/Local-03-04-2012 15:33:41.xml
            //stream=QtartHealthEvent,Alert=Health,Messages=127724,eventSource=119.252.145.80,LastSync=1333775177000,Defferred=3606,Failure=2899,Success=120834,HostGroup=Health,Filter=Health-Health,EventSourceXml=/opt/sadesk/MailXmlBackup/Health-07-04-201210:36:55.xml
            //stream=QtartTopMailerEvent,Alert=Topper,Messages=675,eventSource=mail.ru,AvgSize=10412.78,HostGroup=LSHOST,Filter=TopMailer-LSHOST,EventSourceXml=/opt/sadesk/MailXmlBackup/LSHOST-04-04-201212:31:22.xml
            //stream=QtartStorageEvent,Alert=Storage,Messages=266149,eventSource=n30home4,Delay=0,HostGroup=QtartStorageNetmagic,Filter=Netmagic,EventSourceXml=/opt/sadesk/MailXmlBackup/LSHOST-04-04-201212:31:22.xml
            //etl.dumpCEPEvent("stream=QtartHealthEvent,Alert=Health,Messages=127724,eventSource=119.252.145.80,LastSync=1333775177000,Defferred=3606,Failure=2899,Success=120834,HostGroup=Health,Filter=Health-Health,EventSourceXml=/opt/sadesk/MailXmlBackup/Health-07-04-201210:36:55.xml");
            //DBMonitorEvent
            // etl.dumpCEPEvent("stream=DBMonitorEvent,CustomerID=merit,BACKENDS=-1,BATCH_REQUESTS_PER_SEC=-1.1,BUFFER_CACHE_HIT_RATIO=-1.1,CHECKPOINT_PAGES_PER_SEC=-1.1,COMMITS=-1,CONNECTED_USERS=-1,CONNECTION_TIME=-1.1,CPU_BUSY=-1.1,DELETE=-1,FETCH=-1,FLUSHTABLES=1,FREE_LIST_STALLS_PER_SEC=-1.1,FULL_SCANS_PER_SEC=-1.1,HIT=-1,HostName=192.168.1.2,IDXBLKSHIT=-1,IDXBLKSREAD=-1,IDXSCAN=-1,IDXTUPFETCH=-1,IDXTUPREAD=-1,INSERT=-1,IO_BUSY=-1.1,LATCH_WAITS_PER_SEC=-1.1,LAZY_WRITES_PER_SEC=-1.1,OPENS=33,OPENTABLES=26,PAGE_LIFE_EXPECTANCY=-1.1,QUERIESPERSECONDAVG=0.000,QUESTIONS=1,READ=-1,RET=-1,ROLLBACKS=-1,SEQSCAN=-1,SEQTUPREAD=-1,SLOWQUERIES=0,SQL_INITCOMPILATIONS_PER_SEC=-1.1,SQL_RECOMPILATIONS_PER_SEC=-1.1,THREADS=1,UPDATE=-1,UPTIME=178339,availability=1");
            //NetEvent
            //etl.dumpCEPEvent("stream=NetEvent,CustomerID=merit,IPADDRESS=192.168.1.46,PORT=161,DEVICETYPE=router,DEVICEIDENTIFIER=beetel45x,TIMESTAMP=04/13/12 11:26:48,sysDescr0=Linux ip46.merit.co.in 2.6.18-194.17.4.el5 #1 SMP Mon Oct 25 15:50:53 EDT 2010 x86_64,sysObjectID0=1.3.6.1.4.1.8072.3.2.10,sysUpTime0=0:09:04.07,sysContact0=Root <root@localhost> (configure /etc/snmp/snmp.local.conf),sysName0=ip46.merit.co.in,sysLocation0=Unknown (edit /etc/snmp/snmpd.conf),sysServices0=noSuchInstance,SysORDescr1=The MIB module for SNMPv2 entities,No of Network Interfaces=4,NetworkInterface1=lo,NetworkInterface2=eth0,NetworkInterface3=sit0,bandwidth1=10000000,bandwidth2=100000000,bandwidth3=0,packetin1=7229335,packetin2=25126576,packetin3=0,packetout1=7229335,packetout2=3071497,packetout3=0,");
            //etl.dumpCEPEvent("stream=NetEvent,CustomerID=merit,availability=1,IPADDRESS=192.168.1.46,PORT=161,DEVICETYPE=router,DEVICEIDENTIFIER=beetel45x,TIMESTAMP=04/13/12 11:26:49,sysDescr0=Linux ip46.merit.co.in 2.6.18-194.17.4.el5 #1 SMP Mon Oct 25 15:50:53 EDT 2010 x86_64,resourceId=router,sysObjectID0=1.3.6.1.4.1.8072.3.2.10,sysUpTime0=0:09:04.07,sysContact0=Root <root@localhost> (configure /etc/snmp/snmp.local.conf),sysName0=ip46.merit.co.in,sysLocation0=Unknown (edit /etc/snmp/snmpd.conf),sysServices0=noSuchInstance,SysORDescr1=The MIB module for SNMPv2 entities,No of Network Interfaces=4,NetworkInterface1=lo,NetworkInterface2=eth0,NetworkInterface3=sit0,bandwidth1=10000000,bandwidth2=100000000,bandwidth3=0,packetin1=2134434,packetout1=443434,Total_number_of_packets=4328734,outbound_error_packets=324,outbound_discarded_packets=423798,packetin2=25126576,packetin3=0,packetout2=3071497,packetout3=0,");
            //JVMEvent
            //stream=JVMEvent,resourceType=JVM,resourceSubType=JVM,alive=false,clustername=money,hostname=localhost,time=2012/05/31 12:21:46,jmxurl=service:jmx:rmi:///jndi/rmi://192.168.1.21:8091/jmxrmi,hostipaddress=192.168.1.21,resourceId=192.168.1.21:8091,
            etl.dumpCEPEvent("stream=JVMEvent,CustomerID=merit,availability=1,loadedClassCount=1476,totalLoadedClassCount=1478,unloadedClassCount=0,heapMemoryUsage.used=1704040,heapMemoryUsage.max=66650112,nonHeapMemoryUsage.used=17680280,nonHeapMemoryUsage.max=121634816,availableProcessors=1,version=2.6.10-1.741_FC3,systemLoadAverage=1.68,peakThreadCount=11,threadCount=11,totalStartedThreadCount=14,alive=true,clustername=money,hostname=localhost,time=2012/05/09 11:10:29,jmxurl=service:jmx:rmi:///jndi/rmi://localhost:8091/jmxrmi,hostipaddress=192.168.1.21");
            // etl.dumpCEPEvent("stream=JVMEvent,CustomerID=merit,availability=1,resourceType=JVM,resourceSubType=JVM,alive=false,clustername=money,hostname=localhost,time=2012/05/31 12:21:46,jmxurl=service:jmx:rmi:///jndi/rmi://192.168.1.21:8091/jmxrmi,hostipaddress=192.168.1.21,resourceId=192.168.1.21:8091,");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setHostInfo(String hostName, String CustomerID, String resourcetype, String resourcesubtype, String resourceid,
            String cCustomer, String service) {
        Connection connection;
        Statement stmt;
        ResultSet rs;
        int cCustomerID = -1;
        try {
            System.out.println("Im in setHostInfo()");
            connection = ConnectionDAO.getConnection(CustomerID);

            stmt = connection.createStatement();
            rs = null;
            String cidQuery = "select id from customerinfo where customername='" + cCustomer + "'";
            System.out.println("customerinfo query==>>" + cidQuery);
            rs = stmt.executeQuery(cidQuery);
            while (rs.next()) {
                cCustomerID = rs.getInt(1);
            }
            rs = null;
            String szQuery = "select * from hostinfo where lower(host)='" + hostName.toLowerCase() + "' and resourcetype='" + resourcetype
                    + "' and resourcesubtype='" + resourcesubtype + "' and resourceid='" + resourceid + "' and customerid='" + cCustomerID
                    + "' and service='" + service + "'";
            rs = stmt.executeQuery(szQuery);
            System.out.println("Service mapping query==>>" + szQuery);
            System.out.println("ResultSet==>>" + rs);
            if (rs.next()) {
                System.out.println("HostInfo entry found for host==>>" + hostName);
            } else {
                System.out.println("HostInfo entry not found for host==>>" + hostName);
                szQuery = "select max(config_id) from ipinfo";
                rs = stmt.executeQuery(szQuery);
                int config_id = -1;
                while (rs.next()) {
                    config_id = rs.getInt(1);
                }
                if (config_id != -1) {
                    config_id = config_id + 1;
                } else {
                    config_id = 1;
                }
                System.out.println("config_id==>>" + config_id);
                szQuery = "insert into ipinfo(ipaddress,config_id) values('" + hostName + "'," + config_id + ")";
                int inserted = stmt.executeUpdate(szQuery);
                System.out.println("ipinfo inserted 0/1==>>" + inserted);
                szQuery = "insert into hostinfo(config_id,host,hostgroup,service,subservice,customized_service,resourcetype,"
                        + "resourcesubtype,resourceid,customerid) values(" + config_id + ",'" + hostName
                        + "','Default','" + service + "','Default','Default',"
                        + "'" + resourcetype + "','" + resourcesubtype + "','" + resourceid + "'," + cCustomerID + ")";
                System.out.println("hostinfo insert query==>>" + szQuery);
                inserted = stmt.executeUpdate(szQuery);
                System.out.println("hostinfo inserted 0/1==>>" + inserted);
                rs.close();
                stmt.close();
                //connection.close();

            }
        } catch (Exception e) {
            System.out.println("Error in setHostInfo()");
            e.printStackTrace();
            if (e.getMessage().contains("java.net.SocketException: Broken pipe")) {
                System.out.println("SokectException :Broken pipe");
                System.out.println("calling ConDAO.closeConn()");
                ConnectionDAO.closeConnection(CustomerID);
                System.out.println("exited from ConDAO.closeCon");
            }
        } finally {
            connection = null;
            stmt = null;
            rs = null;
        }
    }
}
