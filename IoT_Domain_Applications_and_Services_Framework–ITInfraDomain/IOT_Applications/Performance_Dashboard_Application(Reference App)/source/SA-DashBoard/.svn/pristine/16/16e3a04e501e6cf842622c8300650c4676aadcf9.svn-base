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
 * *********************************************************************
 */
package com.merit.dashboard;

import java.io.File;
import java.util.ArrayList;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

import com.merit.dashboard.DBUtil.DBUtilHelper;
import com.merit.dashboard.controller.DashBoardController;
import com.merit.dashboard.file.SendFileToJson;
import com.merit.dashboard.util.PropertyUtil;
import com.merit.dashboard.queryexecuter.QueryExecuter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Controller {

	File folderstructure = null;
    private Logger log;

    /** This method is used to call DashBoardController for generating different types of JSON for Chart
     * @param customerName customerId
     * @param timestampselection means (hour, days, week, month, year)
     * @param tomcat_home where you want to create a directory
     * */
    private void runTheCode(String customerName, String timestampselection, String tomcat_home) {
        String sz_startDate = null;
        String sz_endDate = null;
        String start_endarray[] = DateGenerator.dateGenerator(timestampselection);
        sz_startDate = start_endarray[1];
        sz_endDate = start_endarray[0];

        System.out.println(" Start---Date:"+start_endarray[1] + ";\n End-----Date:" + start_endarray[0]+";\n CustomerID:" + customerName);
        log.debug(" Start---Date:"+start_endarray[1] + ";\n End-----Date:" + start_endarray[0]+";\n CustomerID:" + customerName);

        String[] types = DBUtilHelper.getMetrics_mapping_properties().getProperty("ResourceName").split(",");
        try {
            Set<String> comboSelection = null;
        	DashBoardController list=null;
            for (int i = 0; i < types.length; i++) {
                log.debug("*************Starting " + types[i] + "**********************");
                System.out.println("************Starting " + types[i] + "**********************");
                comboSelection = ServletContextListener.getJSONLocationSet(types[i]);
                for(String selection : comboSelection){
                    list = new DashBoardController(types[i], selection, sz_startDate, sz_endDate, timestampselection, customerName);
                    if(i==types.length-1)
                        list.generateDefaultServiceAndResourceType(types[i], sz_startDate, sz_endDate, timestampselection, customerName);
                }
                list.generateJSONForLeftGrid(types[i], sz_startDate, sz_endDate, timestampselection, customerName);

                System.out.println("******************End of " + types[i] + "**********************");
                log.debug("***********************End of " + types[i] + "**********************");
                list=null;
            }
        } catch (Exception e) {
            log.error("IN DashBoardController some ERROR is here" + e.getMessage());
            e.printStackTrace();
        } finally {
            //log = null;
        }
    }

    /** This method we are calling generateDirectoryStructureByCustomerID() and runTheCode() method
     * @param forCustomer customerId
     * @param sz_tomcat_home where you want to create a directory
     * */
    private void createThread(String forCustomer, String sz_tomcat_home) {
    	//forCustomerName = forCustomer;
        //long iTime = 1000*60*60;
        long iTime = 1000*60;
        int count = 0;
        String[] tVals = {"Hour", "Day", "Week", "Month", "Year"};
        while (true) {
        	ArrayList<String> al_customer_list = getCustomerList();

            System.out.println("\nCustomer List Size==="+al_customer_list.size()+"\n");
            log.debug("\nCustomer List Size==="+al_customer_list.size()+"\n");

            if (al_customer_list.size() > 0) {
                for (int j = 0; j < al_customer_list.size(); j++) {
                    for (int i = 0; i < tVals.length; i++) {
                        generateDirectoryStructureByCustomerID(al_customer_list.get(j), tVals[i], sz_tomcat_home);
                        runTheCode(al_customer_list.get(j), tVals[i], sz_tomcat_home);
                        count++;
                        System.out.println("************************End Of "+tVals[i] + " Generated " + count+" *******************");
                    }
                }
                try {
                	Thread.sleep(iTime);
                	count = 0;
                }
                catch (InterruptedException interruptedException) {
                	System.out.println(interruptedException.getMessage());
                	log.error(interruptedException.getMessage());
                }
            } else {
                System.out.println("********Customer List Empty ***********");
            }
        }
    }

    /** This method is creating Directory like merit inside hour,day,week,month,year then
     * inside hour: server,Desktop,DataBase,Network,JVM etc
     * @param customerName customerId
     * @param timestampselection means (hour, days, week, month, year)
     * @param tomcat_home where you want to create a directory
     * */
    public void generateDirectoryStructureByCustomerID(String customerName, String timestampselection, String tomcat_home) {

    	String szProjectName=DBUtilHelper.getMetrics_mapping_properties().getProperty("projectName");
    	String[] types = DBUtilHelper.getMetrics_mapping_properties().getProperty("ResourceName").split(",");

    	System.out.println("\n ProjectName:" + szProjectName+";\n Customer Name:"+customerName+"\n Resource Type List::" + types.length);
    	log.debug("\n ProjectName:" + szProjectName+";\n Customer Name:"+customerName+"\n Resource Type List::" + types.length);

    	File f1=new File(tomcat_home + File.separator + "webapps" + File.separator + szProjectName + File.separator + customerName);
        if(!f1.exists()){
            System.out.println(" Customer directory not found:"+tomcat_home + File.separator + "webapps" + File.separator + szProjectName + File.separator + customerName);
        	String[] timedFOLDR = {"Hour", "Day", "Week", "Month", "Year"};
            List<String> arrMetricList = new ArrayList();
        	try {
                System.out.println("generating folder structure....");
            	for (int t = 0; t < timedFOLDR.length; t++) {
                	for (int i = 0; i < types.length; i++) {

                        //getArray of JSON Folders remoove space from names { }
                        //create one more for loop add the named folder except TIME LINE
                        //System.out.println("types[i].trim() = " + types[i].trim());
                        //Set<String> arrMetricGroup =
                        arrMetricList.clear();
                        arrMetricList.addAll(ServletContextListener.getJSONLocationSet(types[i].trim()));
                        for(String mtricgroup : arrMetricList){
                            if(mtricgroup!=null){
                                folderstructure = new File(
                                                            tomcat_home     + File.separator +
                                                           "webapps"        + File.separator +
                                                            szProjectName   + File.separator +
                                                            customerName    + File.separator +
                                                            timedFOLDR[t]   + File.separator +
                                                            types[i].trim() + File.separator +
                                                            mtricgroup
                                                          );
                                folderstructure.mkdirs();
                            }
                        }
                	}
                }
            	folderstructure=null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(" Ok! Customer directory found: webapps" + File.separator + szProjectName + File.separator + customerName);
        }
        f1=null;
    }

    public boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                if (!children[i].equalsIgnoreCase("TimeLine")) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    /**
     * This method is calling createThread() method after reading property files
     * that are stored externally
     * @param  sz_customer customerId
     */
    public void init(String sz_customer) throws ServletException {
        try {
            log = Logger.getLogger(Controller.class);
           // String params[] = new String[2];
            // ServletContext context = config.getServletContext();
            System.out.println("....init()" + sz_customer);

            String tomcat_home = System.getProperty("catalina.base");
            createThread(sz_customer, tomcat_home);
           // dbUtil=null;
        } catch (Exception ex) {
        	log.error("DashBoard Controller :\n"+ex.getMessage());
            ex.printStackTrace();
        }
    }

    /** This method is creating a list of customer */
    public ArrayList<String> getCustomerList() {

       /* Connection con = null;
        Statement st = null;
        ResultSet rs = null;*/
        ArrayList<String> al_customer_list = new ArrayList<String>();
        try {
            /*Properties properties = DBUtilHelper.getProperties();
            Class.forName(properties.getProperty("driverName"));
            con = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("username"), properties.getProperty("password"));
            st = con.createStatement();
            rs = st.executeQuery("select distinct(domainname) from customerregister where domainname!='null'");

            while (rs.next()) {
                al_customer_list.add(rs.getString("domainname"));
            }*/
        	//al_customer_list.add("192.168.1.2");
        	al_customer_list.add(DBUtilHelper.getMetrics_mapping_properties().getProperty("domainName"));


        } catch (Exception e) {
            e.printStackTrace();
            return al_customer_list;
        }
        return al_customer_list;

    }
}
