/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.sql.*;

/**
 *
 * @author gopal
 * Created on Apr 24, 2012, 5:58:11 PM
 */
public class DatabaseConnection1
{

    public static Connection con = null;

    public static Connection getDatabaseConnection(javax.servlet.http.HttpServletRequest request)
    {


        String Driver = null;
        String password = null;
        String user = null;
        String DataBaseName = null;
        try
        {
            String FileName = null;
            try
            {
                String UserHome_dir = System.getProperty("user.home");
                String Fileseperator = System.getProperty("file.separator");
                String Servername = request.getServerName();
                FileName = UserHome_dir + Fileseperator + "SA_Desk1-" + Servername + ".properties";


            } catch (Exception ex)
            {
                ex.printStackTrace();
                System.out.println("PROPERTIES FILE NOT FOUND *****************");
            }
            Properties propsdAddressPath = new Properties();
            propsdAddressPath.load(new FileInputStream(FileName));
            DataBaseName = propsdAddressPath.getProperty("DataBaseName");
            System.out.println("Database:::"+ DataBaseName);
            user = propsdAddressPath.getProperty("User");
            password = propsdAddressPath.getProperty("Password");
            Driver = propsdAddressPath.getProperty("Driver");
            Class.forName(Driver);

            con = DriverManager.getConnection(DataBaseName, user, password);
            System.out.println("connected");

        } catch (Exception ex)
        {
            System.out.println("ERROR WHILE GETTING DATABASE CONNECTION");
            Logger.getLogger(DatabaseConnection1.class.getName()).log(Level.SEVERE, null, ex);
        }





        return con;

    }


    public static Connection getRefDatabaseConnection(javax.servlet.http.HttpServletRequest request)
    {


        String Driver = null;
        String password = null;
        String user = null;
        String DataBaseName = null;
        try
        {
            String FileName = null;
            try
            {
                String UserHome_dir = System.getProperty("user.home");
                System.out.println("User Home:"+UserHome_dir);
                String Fileseperator = System.getProperty("file.separator");
                String Servername = request.getServerName();
                FileName = UserHome_dir + Fileseperator + "SA_Desk-" + Servername + ".properties";


            } catch (Exception ex)
            {
                Logger.getLogger(DatabaseConnection1.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("PROPERTIES FILE NOT FOUND *****************");
            }
            Properties propsdAddressPath = new Properties();
            propsdAddressPath.load(new FileInputStream(FileName));
            DataBaseName = propsdAddressPath.getProperty("DataBaseName");
            System.out.println("Database:::"+ DataBaseName);
            user = propsdAddressPath.getProperty("User");
            password = propsdAddressPath.getProperty("Password");
            Driver = propsdAddressPath.getProperty("Driver");
            Class.forName(Driver);
            con = DriverManager.getConnection(DataBaseName, user, password);
            System.out.println("connected");

        } catch (Exception ex)
        {
            System.out.println("ERROR WHILE GETTING DATABASE CONNECTION");
            Logger.getLogger(DatabaseConnection1.class.getName()).log(Level.SEVERE, null, ex);
        }





        return con;

    }

    public static String GetPropertyattribute(javax.servlet.http.HttpServletRequest request, String Propertyname)
    {
        String Attributevalue = null;


        try
        {
            String FileName = null;
            try
            {
                String UserHome_dir = System.getProperty("user.home");
                String Fileseperator = System.getProperty("file.separator");
                String Servername = request.getServerName();
                FileName = UserHome_dir + Fileseperator + "SA_Desk1-" + Servername + ".properties";


            } catch (Exception ex)
            {
                Logger.getLogger(DatabaseConnection1.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("PROPERTIES FILE NOT FOUND *****************");
            }

            Properties propsdAddressPath = new Properties();
            propsdAddressPath.load(new FileInputStream(FileName));
            Attributevalue = propsdAddressPath.getProperty(Propertyname);

        } catch (Exception ex)
        {
            Logger.getLogger(DatabaseConnection1.class.getName()).log(Level.SEVERE, null, ex);
        }




        return Attributevalue;
    }





    public static String GetRefPropertyattribute(javax.servlet.http.HttpServletRequest request, String Propertyname)
    {
        String Attributevalue = null;


        try
        {
            String FileName = null;
            try
            {
                String UserHome_dir = System.getProperty("user.home");
                System.out.println("User Home:"+UserHome_dir);
                String Fileseperator = System.getProperty("file.separator");
                String Servername = request.getServerName();
                FileName = UserHome_dir + Fileseperator + "SA_Desk-" + Servername + ".properties";


            } catch (Exception ex)
            {
                Logger.getLogger(DatabaseConnection1.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("PROPERTIES FILE NOT FOUND *****************");
            }

            Properties propsdAddressPath = new Properties();
            propsdAddressPath.load(new FileInputStream(FileName));
            Attributevalue = propsdAddressPath.getProperty(Propertyname);

        } catch (Exception ex)
        {
            Logger.getLogger(DatabaseConnection1.class.getName()).log(Level.SEVERE, null, ex);
        }




        return Attributevalue;
    }






}
