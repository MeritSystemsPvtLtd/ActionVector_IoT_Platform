/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/***************************************************************************

Software Developed by
Merit Systems Pvt. Ltd.,
#55/C-42/1, Nandi Mansion, Ist Floor 40th Cross, Jayanagar 8rd Block
Bangalore - 560 070, India
Work Created for Merit Systems Private Limited
All rights reserved

THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT
LAWS AND TREATIES
NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED,
DISTRIBUTED, REVISED, MODIFIED,TRANSLATED, ABRIDGED,
CONDENSED,
EXPANDED, COLLECTED, COMPILED, LINKED, RECAST, TRANSFORMED OR
ADAPTED
WITHOUT THE PRIOR WRITTEN CONSENT
ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION
COULD SUBJECT
THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.


 ***************************************************************************/
package dbmanager;

import java.util.Properties;
import java.io.*;

/**
 *
 * @author gopal
 * Created on Jul 10, 2012, 11:51:01 AM
 */
public class InitDBDomainSingleDomain
{

    private static String szDatabaseName = null;
    private static String szDatabaseDriver = null;
    private static String szDataSourceName = null;
    private static String szDatabaseUser = null;
    private static String szDatabasePassword = null;

    public static String getSzDataSourceName()
    {
        return szDataSourceName;
    }

    public static String getSzDatabaseDriver()
    {
        return szDatabaseDriver;
    }

    public static String getSzDatabaseName()
    {
        return szDatabaseName;
    }

    public static String getSzDatabasePassword()
    {
        return szDatabasePassword;
    }

    public static String getSzDatabaseUser()
    {
        return szDatabaseUser;
    }

    /* public static boolean initialize(String szDomainName)
    {
    String szFileString = "";
    boolean finit = false;
    try
    {
    Properties properties = new Properties();

    String FileName = null;
    try
    {

    String UserHome_dir = System.getProperty("user.home");
    String Fileseperator = System.getProperty("file.separator");
    //String Servername = request.getServerName();
    FileName = UserHome_dir + System.getProperty("file.separator") + "ActionVector-" + szDomainName + ".ini";
    System.out.println("Database DataSource properties file::" + FileName);
    properties.load(new FileInputStream(FileName));
    szDatabaseName = properties.getProperty("Database Name");
    System.out.println("DatabaseName in init::" + szDatabaseName);
    szDatabaseDriver = properties.getProperty("Driver Name");
    System.out.println("DatabaseDriver in init::" + szDatabaseDriver);
    szDataSourceName = properties.getProperty("Data Source Name");
    System.out.println("DataSourceName in init::" + szDataSourceName);
    szDatabaseUser = properties.getProperty("Database User");
    System.out.println("DatabaseUserName in init::" + szDatabaseUser);
    szDatabasePassword = properties.getProperty("Database Password");
    System.out.println("DatabaseName in init::" + szDatabasePassword);
    finit = true;
    } catch (Exception ex)
    {
    ex.printStackTrace();
    System.out.println("PROPERTIES FILE NOT FOUND *****************");
    }
    } catch (Exception e)
    {
    e.printStackTrace();
    }

    return finit;
    }*/
    public static boolean initialize(String szServerName)
    {
        File FConfigFile = null;
        boolean bretrn = false;

        String szKeyId, szKeyValue, szReadLine, szSection = "";
        String szLogString = "";
        boolean bEOF = false;


        String szUserHomeDir = System.getProperty("user.home");

       // System.out.println("ini file path is    " + szUserHomeDir + System.getProperty("file.separator") + "ActionVector-" + szServerName + ".ini");
        // Code to read the INI File and retrieve the Database values inorder to make conection.
        FConfigFile = new File(szUserHomeDir + System.getProperty("file.separator") + "ActionVector-" + szServerName + ".ini");
        
        //System.out.println("File PATH::"+szUserHomeDir + System.getProperty("file.separator") + "ActionVector-" + szServerName + ".ini");

        RandomAccessFile RAFObj = null;
        try
        {
            RAFObj = new RandomAccessFile(FConfigFile, "r");
            szLogString = "Reading the ActionVector-" + szServerName + ".ini file";
            System.out.println(szLogString);
            while (!bEOF)
            {
                szReadLine = RAFObj.readLine();
                System.out.println("Read Line::"+szReadLine);
                if (szReadLine == null || szReadLine.equals(null))
                {
                    bEOF = true;
                } else
                {
                    if (szReadLine.startsWith("["))
                    {
                        szSection = szReadLine.substring(szReadLine.indexOf("[") + 1, szReadLine.indexOf("]"));
                        if (szSection.startsWith("Database"))
                        {
                            szReadLine = RAFObj.readLine();
                            if (szReadLine.startsWith("Database"))
                            {
                                szKeyId = szReadLine.substring(0, szReadLine.indexOf("="));
                                szKeyValue = szReadLine.substring(szReadLine.indexOf("=") + 1).trim();
                                szDatabaseName = szKeyValue;
                               // System.out.println("szDatabaseName" + szDatabaseName);

                            }
                            szReadLine = RAFObj.readLine();
                            if (szReadLine.startsWith("Data"))
                            {
                                szKeyId = szReadLine.substring(0, szReadLine.indexOf("="));
                                szKeyValue = szReadLine.substring(szReadLine.indexOf("=") + 1).trim();
                                szDataSourceName = szKeyValue;
                                //System.out.println("Datasource Name =" + szDataSourceName);


                            }
                            szReadLine = RAFObj.readLine();
                            if (szReadLine.startsWith("Driver"))
                            {
                                szKeyId = szReadLine.substring(0, szReadLine.indexOf("="));
                                szKeyValue = szReadLine.substring(szReadLine.indexOf("=") + 1).trim();
                                szDatabaseDriver = szKeyValue;
                                //System.out.println("szDatabaseDriver::" + szDatabaseDriver);

                            }
                            szReadLine = RAFObj.readLine();
                            if (szReadLine.startsWith("Database User"))
                            {
                                szKeyId = szReadLine.substring(0, szReadLine.indexOf("="));
                                szKeyValue = szReadLine.substring(szReadLine.indexOf("=") + 1).trim();
                                szDatabaseUser = szKeyValue;
                               // System.out.println("szDatabaseUser::" + szDatabaseUser);

                            }
                            szReadLine = RAFObj.readLine();
                            if (szReadLine.startsWith("Database Password"))
                            {
                                szKeyId = szReadLine.substring(0, szReadLine.indexOf("="));
                                szKeyValue = szReadLine.substring(szReadLine.indexOf("=") + 1).trim();
                                StringBuffer get_pass_encrypt = new StringBuffer(szKeyValue);
                                StringBuffer get_pass_decrypt = get_pass_encrypt;
                                String password_dec = get_pass_decrypt.toString();
                                szKeyValue = password_dec;
                                szKeyValue = szKeyValue.trim();
                                szDatabasePassword = szKeyValue;
                                //System.out.println("szDatabasePassword::" + szDatabasePassword);
                            }
                        }
                    }

                }//end of main if
            }
            RAFObj.close();
            bretrn = true;
            return bretrn;
        } catch (Exception ez)
        {
            try
            {
                bretrn = false;
                RAFObj.close();
                return bretrn;
            } catch (Exception e1)
            {
            }
            ez.printStackTrace();
            System.out.println("EXCEPTION while reading the INI File --> IGNORE");
        } finally
        {
            try
            {
                RAFObj.close();
                return bretrn;
            } catch (Exception e1)
            {
            }
        }

        return bretrn;

    }
}
