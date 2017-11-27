/**
 *
 * @author anand kumar verma
 * ********************************************************************
 * Copyright message Software Developed by Merit Systems Pvt. Ltd., No. 42/1,
 * 55/c, Nandi Mansion, 40th Cross, Jayanagar 8th Block Bangalore - 560 070,
 * India Work Created for Merit Systems Private Limited All rights reserved
 *
 * THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES
 * NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED, DISTRIBUTED,
 * REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED,
 * COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD
 * SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 * *********************************************************************
 */
package com.prokosha.dbconnection;

import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Logger;

public class ConnectionMap {

    private static final Logger log = Logger.getLogger(ConnectionMap.class.getName());
    private HashMap<String, Connection> gConectionMap = new HashMap<String, Connection>();
    private HashMap<String, Statement> gConectionStatementMap = new HashMap<String, Statement>();
    private String fileNameFormat = "customerDBConfigFileFormat";
    private String fileEndsFormat[] = null;

    public ConnectionMap(String moduleName) {
        String format = null;
        try {
            if (moduleName.equalsIgnoreCase("ETL")) {
                System.out.println("format===>");
                Class etlCls = Class.forName("com.prokosha.adapter.etl.ETLAdapter.ETLProperties");
                System.out.println("etlCls===>" + etlCls);
                Object etlObj = etlCls.newInstance();
                System.out.println("etlObj===>" + etlObj);
                Class[] inputArr = new Class[]{};
                Method etlMthd1 = etlCls.getDeclaredMethod("initialize");
                System.out.println("etlMthd1===>" + etlMthd1);
                etlMthd1.invoke(etlObj, null);
                Method etlMthd = etlCls.getDeclaredMethod("getCustomerDBConfigFileFormat");
                format = (String) etlMthd.invoke(etlObj, null);
                System.out.println("etlMthd===>" + format);

            } else if (moduleName.equalsIgnoreCase("CloudUserAPI")) {
                format = "ActionVector-####.ini";
            } else if (moduleName.equalsIgnoreCase("General")) {
                format = initFile_sFormatedName();
            }
            fileEndsFormat = format.split("####");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException i) {
            i.printStackTrace();
        } catch (IllegalAccessException i) {
            i.printStackTrace();
        } catch (NoSuchMethodException ne) {
            ne.printStackTrace();
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (InvocationTargetException it) {
            it.printStackTrace();
        }

    }

    public void updateMapforConntionWithID(String customerID, Connection con) {
        gConectionMap.put(customerID, con);
    }
    public Connection getConntionWithID(String customerID) {
        //System.out.println("in getConntionWithID method customerID = " + customerID);
        Connection con = null;
        try {
            // System.out.println("gConectionMap = " + gConectionMap);
            // System.out.println("gConectionMap.containsKey("+customerID+") = " + gConectionMap.containsKey(customerID));
            if (gConectionMap.containsKey(customerID)) {
                con = gConectionMap.get(customerID);
                if (con == null) {
                    gConectionMap.remove(customerID);
                    return null;
                }
                System.out.println("Is Connection Closed===" + con.isClosed());
                if (con.getMetaData() != null) {
                    return con;
                }
            }
            String fileName = fileEndsFormat[0] + customerID + fileEndsFormat[1];
            //System.out.println("customerDBConfigfileName = " + fileName);
            CustmonerDBCon custDBCon = new CustmonerDBCon(fileName);
            con = custDBCon.getCustmonerDBCon(customerID);
            gConectionMap.put(customerID, con);
            return con;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Statement getConntionStatementWithID(String customerID) {
        // System.out.println("in getConntionWithID method customerID = " + customerID);
        Statement conStat = null;
        Connection con = null;
        try {
            // System.out.println("gConectionMap = " + gConectionMap);
            // System.out.println("gConectionMap.containsKey("+customerID+") = " + gConectionMap.containsKey(customerID));
            if (gConectionStatementMap.containsKey(customerID)) {
                conStat = gConectionStatementMap.get(customerID);
                // System.out.println("Is Connection Closed==="+con.isClosed());
                try {
                    if (conStat != null) {
                        if(conStat.getConnection().getMetaData()!=null){
                            conStat.clearBatch();
                            conStat.clearWarnings();
                            return conStat;
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            con = getConntionWithID(customerID);
            conStat = con.createStatement();
            gConectionStatementMap.put(customerID, conStat);
            return conStat;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public Connection CloseConeectionWithID(String customerID) {

        try {
            System.out.println("Received cID==" + customerID);
            Iterator keyItr = gConectionMap.keySet().iterator();
            while (keyItr.hasNext()) {
                System.out.println("key::" + keyItr.next().toString());
            }
            if (gConectionMap.containsKey(customerID)) {
                gConectionMap.remove(customerID);
                System.out.println("Connection Closed===");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String initFile_sFormatedName() {
        try {
            String home = System.getProperty("user.home");
            String sz_fileseparator = System.getProperty("file.separator");
            log.info("HOME DIRECTORY==" + home);
            Properties properties = new Properties();
            FileInputStream fileINPTstr = new FileInputStream(home + sz_fileseparator + "ETLConfig" + sz_fileseparator + "reportadapter.properties");
            properties.load(fileINPTstr);
            String format = properties.getProperty(fileNameFormat).trim();
            //fileEndsFormat = format.split("####");
            fileINPTstr.close();
            fileINPTstr = null;
            return format;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
