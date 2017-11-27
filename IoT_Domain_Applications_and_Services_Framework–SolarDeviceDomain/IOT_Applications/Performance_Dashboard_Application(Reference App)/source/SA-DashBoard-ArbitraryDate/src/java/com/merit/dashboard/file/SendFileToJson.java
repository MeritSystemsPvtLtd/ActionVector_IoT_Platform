/**
 * *********************************************************************
 * Software Developed by Merit Systems Pvt. Ltd., No. 42/1, 55/c, Nandi Mansion,
 * 40th Cross, Jayanagar 8th Block Bangalore - 560 070, India Work Created for
 * Merit Systems Private Limited All rights reserved
 *
 * THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES
 * NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED, DISTRIBUTED,
 * REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED,
 * COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD
 * SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
**********************************************************************
 */
package com.merit.dashboard.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.merit.connection.ConnectionDAO;
import com.merit.dashboard.DBUtil.DBUtilHelper;

/**
 * *************************************************************************************************
 * This Class is designed to send JSON File in appropriate Directory, That will
 * be useful for Generating Chart
 *
 * @author satya
 * **************************************************************************************************
 */
public class SendFileToJson {

    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SendFileToJson.class);
    private String resourceType = null;
    private String jsonFileName = null;
    private String dataString = null;

    /**
     * *************************************************************************************************
     *
     * @param customer name of Directory-timestampselection-resourceType, where
     * all JSON file should be placed
     * @param timestampselection Inside customer directory having different
     * sub-Directory of timestamp like (hour, day, week, month, year). Here
     * timestampselection-resourceType is telling where all JSON file should be
     * placed
     * @param resourceType Inside timestamp directory having different
     * sub-directory of resourcetype like (Desktop,server,
     * DataBase,Network,JVM). Here resourceType is telling where all JSON file
     * should be placed
     * @param jsonFileName Name of the JSON
     * @param dataString Data to be stored in JSON
     *
     * **************************************************************************************************
     */
    public SendFileToJson(String customer, String selection, String timestampselection, String resourceType, String jsonFileName, String dataString) {
        this.resourceType = resourceType;
        this.jsonFileName = jsonFileName;
        this.dataString = dataString;
		try {
			Connection con=ConnectionDAO.getConnection(customer);
			if(con == null){
				System.out.println("Connection is not Established, Please Check Database Connection");
			}
			else{
				 sendDataToFile(customer, timestampselection, selection);
				 System.out.println("Query Executing...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

    }

    /**
     * *************************************************************************************************
     * This method is sending JSON File Periodically to Specific defined Path.
     *
     * @param customer name of Directory, where all JSON file should be placed
     * @param timestampselection Inside customer directory different
     * sub-Directory of timestamp like (hour, day, week, month, year). Here
     * timestampselection is telling where all JSON file should be placed
     * **************************************************************************************************
     */
    public void sendDataToFile(String customer, String timestampselection, String selection) {
        String szProjectName = DBUtilHelper.getMetrics_mapping_properties().getProperty("projectName");
        String tomcat_home = System.getProperty("catalina.base");
        String file_Path = "";
        if (resourceType.equalsIgnoreCase("")) {
            file_Path = tomcat_home + File.separator + "webapps" + File.separator + szProjectName + File.separator + customer + File.separator + timestampselection + File.separator + jsonFileName + ".json";
        } else {
        	if(selection.equalsIgnoreCase(""))
        		file_Path = tomcat_home + File.separator + "webapps" + File.separator + szProjectName + File.separator + customer + File.separator + timestampselection + File.separator + resourceType + File.separator + resourceType + jsonFileName + ".json";
        	else
        	    file_Path = tomcat_home + File.separator + "webapps" + File.separator + szProjectName + File.separator + customer + File.separator + timestampselection + File.separator + resourceType + File.separator + selection + File.separator + resourceType + jsonFileName + ".json";
        		file_Path = tomcat_home + File.separator + "webapps" + File.separator + szProjectName + File.separator + customer + File.separator + timestampselection + File.separator + resourceType + File.separator + selection + File.separator + resourceType + jsonFileName + ".json";
        }
        File file = new File(file_Path);
        try {

            FileOutputStream fos = new FileOutputStream(file, false);
            fos.write(dataString.getBytes());
            fos.close();
            String szFileContent = "";
            if (dataString.length() < 5) {
                szFileContent = " File is Empty::" + dataString + ";\n";
            }
            log.info(szFileContent + "\n File Sent Successfully:::" + file_Path);
            System.out.println(szFileContent + "\n File Sent Successfully:::" + file_Path);
            file = null;
            fos = null;
            Date date = new Date();
            System.out.println(" System Time:::::::::::::" + date.toString() + "\n");
            date = null;

        } catch (Exception e) {
            log.error("DashBoard SendFileToJson Writing into :" + file_Path + "\n" + e.getMessage());
            e.printStackTrace();

        }


    }
}
