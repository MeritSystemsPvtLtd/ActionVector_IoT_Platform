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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import org.apache.log4j.Logger;

class CustmonerDBCon {

    private static Logger log = Logger.getLogger(CustmonerDBCon.class.getName());
    private String customerID = null;
    private String databaseUser = null;
    private String databasePassword = null;
    private String databaseName = null;
    private String databaseURL = null;
    private String databaseDriver = null;
    
    private String cutomerFileName = null;

    public CustmonerDBCon(String iniFileName){
        cutomerFileName = iniFileName;
    }
    //ActionVector-merit.actionvector.com.ini
    Connection getCustmonerDBCon(String customerID) {
        this.customerID = customerID;
        if(!cutomerFileName.contains(this.customerID)){
            return null;
        }
        readINIFile();
        ConnectionCreator concreator = new ConnectionCreator(databaseDriver, databaseURL, databaseUser, databasePassword);
        Connection con = concreator.getConnection();
        concreator = null;
        return con;
    }

    private void readINIFile() {
        String absolutePath = System.getProperty("user.home") + System.getProperty("file.separator") + cutomerFileName;

        try {
            File file = new File(absolutePath);
            FileReader filereader = new FileReader(file);
            BufferedReader bReader = new BufferedReader(filereader);
            String line = "";
            line = bReader.readLine();
            if (line.startsWith("[")) {
                String szSection = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                if (szSection.startsWith("Database")) {
                    while ((line = bReader.readLine()) != null) {

                        if (line.startsWith("Database Name")) {
                            databaseName = line.split("=")[1].trim();

                            log.debug("DatabaseName = " +"["+ databaseName+"]");
                            //System.out.println("DatabaseName = " +"["+ databaseName+"]");
                        } else if (line.startsWith("Data Source Name")) {
                            databaseURL = line.split("=")[1].trim();

                            log.debug("DataBase URL = " +"["+ databaseURL+"]");
                           // System.out.println("DataBase URL = " +"["+ databaseURL+"]");
                        } else if (line.startsWith("Driver Name")) {
                            databaseDriver = line.split("=")[1].trim();

                            log.debug("DataBase Driver = " +"["+ databaseDriver+"]");
                           // System.out.println("DataBase Driver = " +"["+ databaseDriver+"]");
                        } else if (line.startsWith("Database User")) {
                            databaseUser = line.split("=")[1].trim();

                            log.debug("DataBase user = " +"["+ databaseUser+"]");
                        } else if (line.startsWith("Database Password")) {
                              System.out.println("In Database Password");
                            
                            
                            {
                                databasePassword="";
                            }

                            log.debug("DataBase Password = " + "["+databasePassword+"]");
                            System.out.println("DataBase Password = " + "["+databasePassword+"]");
                        }
                    }
                }
            }
            filereader.close();
            bReader.close();
            filereader = null;
            bReader = null;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
