/**
 *
 * @author anand kumar verma
 *********************************************************************
Copyright message
Software Developed by
Merit Systems Pvt. Ltd.,
No. 42/1, 55/c, Nandi Mansion, 40th Cross, Jayanagar 8th Block
Bangalore - 560 070, India
Work Created for Merit Systems Private Limited
All rights reserved

THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES
NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED,
DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED,
EXPANDED, COLLECTED, COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED
WITHOUT THE PRIOR WRITTEN CONSENT
ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT
THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 **********************************************************************
 */
package custmDBCon;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

public class ConnectionMap {

    private static final Logger log = Logger.getLogger(ConnectionMap.class.getName());
    private HashMap<String, Connection> gConectionMap = new HashMap<String, Connection>();
    private String fileNameFormat = "customerDBConfigFileFormat";
    private String fileEndsFormat[] = null;

    public ConnectionMap() {
        initFile_sFormatedName();
    }

    public Connection getConntionWithID(String customerID) {
        // System.out.println("in getConntionWithID method customerID = " + customerID);
        Connection con = null;
        try {
           // System.out.println("gConectionMap = " + gConectionMap);
           // System.out.println("gConectionMap.containsKey("+customerID+") = " + gConectionMap.containsKey(customerID));
            if (gConectionMap.containsKey(customerID)) {
                con = gConectionMap.get(customerID);
                // System.out.println("Is Connection Closed==="+con.isClosed());
                if (con != null) {
                    return con;
                }
            }

	            String fileName = fileEndsFormat[0] + customerID + fileEndsFormat[1];
	            System.out.println("customerDBConfigfileName = " + fileName);
	            CustmonerDBCon custDBCon = new CustmonerDBCon(fileName);
	            con = custDBCon.getCustmonerDBCon(customerID);
	            if (con == null)
	            	System.out.println("Connection Not Established to the DataBase url :"+custDBCon.databaseURL);

	            gConectionMap.put(customerID, con);
	            return con;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String initFile_sFormatedName() {
        try {
            String home =System.getProperty("user.home");
            String sz_fileseparator=System.getProperty("file.separator");
            log.info("HOME DIRECTORY==" + home);
            Properties properties = new Properties();
            FileInputStream fileINPTstr = new FileInputStream(home + sz_fileseparator + "ETLConfig"+sz_fileseparator+"reportadapter.properties");
            properties.load(fileINPTstr);
            String format = properties.getProperty(fileNameFormat).trim();
            fileEndsFormat = format.split("####");
            fileINPTstr.close();
            fileINPTstr = null;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
