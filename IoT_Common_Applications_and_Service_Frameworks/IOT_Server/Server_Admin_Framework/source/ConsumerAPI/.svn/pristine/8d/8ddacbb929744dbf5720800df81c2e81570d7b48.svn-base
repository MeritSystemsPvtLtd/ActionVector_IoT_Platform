/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package businessmodel;

import java.io.*;
import java.util.*;

/**
 *
 * @author gopal
 * Created on Jun 21, 2012, 2:54:19 PM
 */
public class Configuration
{

    public static Properties initialize()
    {

        Properties properties = null;
        properties = new Properties();
        try
        {
            String FileName = null;
            try
            {
                String UserHome_dir = System.getProperty("user.home");
                String Fileseperator = System.getProperty("file.separator");
                FileName = UserHome_dir + Fileseperator + "configuration.properties";
                System.out.println("File location:::::" + FileName);

            } catch (Exception ex)
            {
                ex.printStackTrace();
                System.out.println("PROPERTIES FILE NOT FOUND *****************");
            }
            Properties propsdAddressPath = new Properties();
            propsdAddressPath.load(new FileInputStream(FileName));
            properties.load(new FileInputStream(FileName));

            //System.out.println("JVM Attributes::" + properties.getProperty("JVM"));
            //System.out.println("JVM Status::" + properties.getProperty("JVMstatus"));

        } catch (Exception e)
        {
            e.printStackTrace();
        }


        return properties;
    //return false;

    }
}
