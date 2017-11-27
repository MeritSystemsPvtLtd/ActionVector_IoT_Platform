/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package independentDatabaseQuery;

import com.prokosha.adapter.etl.ETLAdapter.ETLProperties;
import java.sql.*;

/**
 *
 * @author mahesh:anand kumar verma
 */
public class QueryObject {
    //database properties
    public String driverName = null;
    public String urlPath = null;
    private String user = null;
    private String password = null;

    // idependent use of these members
    Connection con = null;
    Statement sql = null;
    DatabaseMetaData dbmd = null;      // This is basically info the driver delivers
     String szFile_Seperator=System.getProperty("file.separator");
    String home=System.getProperty("user.home");
    String propertyFilePath=home+szFile_Seperator+"ETLConfig"+szFile_Seperator+"CEPEventMetricsMapping.properties";
   

    public QueryObject (){
        try {
            driverName = ETLProperties.getDriverName();
            urlPath = ETLProperties.getUrlPath();
            user = ETLProperties.getUserID();
            password = ETLProperties.getPassword();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection createConnection(){
        boolean flag = false;
        try{
            //System.out.println("......."+driverName+" .. "+urlPath+" .. "+user+" .. "+password);
            Class.forName(driverName); //load the driver
            con = DriverManager.getConnection(urlPath, user, password);
            dbmd = con.getMetaData(); //get MetaData to confirm connection
            if(dbmd != null){
                flag = true;
                sql = con.createStatement();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return con;
    }

    public boolean closeConnection(){
        boolean flag = false;
        try {
            con.close();
        } catch (Exception e) {
                flag = true;
        }finally{
            con = null;
            dbmd = null;
            driverName = null;
            urlPath = null;
            user = null;
            password = null;
            sql = null;
            home = null;
            propertyFilePath = null;
        }
        return flag;
    }

}
