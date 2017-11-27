/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AV_Action;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

/**
 *
 * @author gopal
 */
public class AV_OutputFormat {

    public static StringBuffer szoutPut = new StringBuffer();
    public static String szfinalFormat = null;
    public static boolean initErrors = false;
    public static Properties errorProps = null;
    public static String szDesc = null;

    public static String formatOutPut(String opStatus, String operation, javax.servlet.ServletContext sc) {
        szoutPut.append("<" + operation + ">");
        szoutPut.append("<status>\n");
        szoutPut.append("<code>\n");
        szoutPut.append(opStatus);
        szoutPut.append("</code>\n");
        szoutPut.append("<Description>\n");
        szoutPut.append(getDescription(opStatus, sc));
        szoutPut.append("</Description>\n");
        szoutPut.append("</status>\n");
        szoutPut.append("</" + operation + ">");

        szfinalFormat = szoutPut.toString();
        szoutPut.delete(0, szoutPut.length());

        return szfinalFormat;

    }

    public static String formatOutPut(String opStatus, StringBuffer result, String operation, javax.servlet.ServletContext sc) {

        szoutPut.append("<" + operation + ">");
        szoutPut.append("<status>\n");
        szoutPut.append("<code>\n");
        szoutPut.append(opStatus);
        szoutPut.append("</code>\n");
        szoutPut.append("<Description>\n");
        szoutPut.append(getDescription(opStatus, sc));
        szoutPut.append("</Description>\n");
        szoutPut.append("</status>\n");
        if (!(result == null)) {
            szoutPut.append(result);
        }
        szoutPut.append("</" + operation + ">");
        szfinalFormat = szoutPut.toString();
        szoutPut.delete(0, szoutPut.length());
        return szfinalFormat;

    }

  public static String formatJSONOutPut(String opStatus, StringBuffer result, String operation, javax.servlet.ServletContext sc) {

        szoutPut.append("{\"" + operation +"\"\n");
        szoutPut.append("{\"status\"\n");
        szoutPut.append("{\"code\"\n");
        szoutPut.append("\""+opStatus+"\"\n");
      
        szoutPut.append("{\"Description\"\n");
        szoutPut.append("\""+getDescription(opStatus, sc)+"\"\n");
        szoutPut.append("}\n");
        szoutPut.append("}\n");
        if (!(result == null)) {
            szoutPut.append(result);
        }
        szoutPut.append("\n}");
        szfinalFormat = szoutPut.toString();
        szoutPut.delete(0, szoutPut.length());
        return szfinalFormat;

    }
   
        public static String formatHostJsonOutPut(String opStatus, StringBuffer result, String operation, javax.servlet.ServletContext sc) {

        szoutPut.append("{\"" + operation +"\"\n");
        szoutPut.append("{\"status\"\n");
        szoutPut.append("{\"code\"\n");
        szoutPut.append("\""+opStatus+"\"\n");
      
        szoutPut.append("{\"Description\"\n");
        szoutPut.append("\""+getDescription(opStatus, sc)+"\"\n");
        szoutPut.append("}\n");
        szoutPut.append("}\n");
        if (!(result == null)) {
            szoutPut.append(result);
        }
        szoutPut.append("\n}");
        szfinalFormat = szoutPut.toString();
        szoutPut.delete(0, szoutPut.length());
        return szfinalFormat;

    }
         public static String formatCustomerInfoOutPut(String opStatus, StringBuffer result, String operation, javax.servlet.ServletContext sc) {

        szoutPut.append("{\"" + operation +"\"\n");
        szoutPut.append("{\"status\"\n");
        szoutPut.append("{\"code\"\n");
        szoutPut.append("\""+opStatus+"\"\n");
      
        szoutPut.append("{\"Description\"\n");
        szoutPut.append("\""+getDescription(opStatus, sc)+"\"\n");
        szoutPut.append("}\n");
        szoutPut.append("}\n");
        if (!(result == null)) {
            szoutPut.append(result);
        }
        szoutPut.append("\n}");
        szfinalFormat = szoutPut.toString();
        szoutPut.delete(0, szoutPut.length());
        return szfinalFormat;

    }
         
         public static String formatInstallatonsInfoOutPut(String opStatus, StringBuffer result, String operation, javax.servlet.ServletContext sc) {

        szoutPut.append("{\"" + operation +"\"\n");
        szoutPut.append("{\"status\"\n");
        szoutPut.append("{\"code\"\n");
        szoutPut.append("\""+opStatus+"\"\n");
      
        szoutPut.append("{\"Description\"\n");
        szoutPut.append("\""+getDescription(opStatus, sc)+"\"\n");
        szoutPut.append("}\n");
        szoutPut.append("}\n");
        if (!(result == null)) {
            szoutPut.append(result);
        }
        szoutPut.append("\n}");
        szfinalFormat = szoutPut.toString();
        szoutPut.delete(0, szoutPut.length());
        return szfinalFormat;

    }
    public static String getDescription(String opStatus, javax.servlet.ServletContext sc) {

        if (!initErrors) {
            errorProps = errorProperties(sc);
            if (errorProps != null) {
                szDesc = errorProps.getProperty(opStatus);
            }
        } else {
            if (errorProps != null) {
                szDesc = errorProps.getProperty(opStatus);
            }
        }
        if (szDesc != null) {
            return szDesc;
        } else {
            return "Undefined";
        }
    }

    public static Properties errorProperties(javax.servlet.ServletContext sc) {
        String filepath;
        String proppath;

        try {
            if (sc == null) {
                return null;
            } else {
                filepath = sc.getRealPath(System.getProperty("file.separator"));

                System.out.println("filepath==>>" + filepath);
                proppath = filepath + "APIErrors.properties";
                errorProps = new Properties();
                errorProps.load(new FileInputStream(proppath));
            }

            proppath = null;
            filepath = null;
        } catch (IOException ex) {
            Logger.getLogger(AV_OutputFormat.class.getName()).log(Level.SEVERE, null, ex);
        }
        return errorProps;
    }

    public static String formatOutPut(String opStatus, StringBuffer result, StringBuffer userinfo, String szopration, ServletContext sc) {
        szoutPut.append("<" + szopration + ">");
        szoutPut.append("<status>\n");
        szoutPut.append("<code>\n");
        szoutPut.append(opStatus);
        szoutPut.append("</code>\n");
        szoutPut.append("<Description>\n");
        szoutPut.append(getDescription(opStatus, sc));
        szoutPut.append("</Description>\n");
        szoutPut.append("</status>\n");
        if (!(result == null)) {
            szoutPut.append(userinfo);
            szoutPut.append(result);
        }
        szoutPut.append("</" + szopration + ">");
        szfinalFormat = szoutPut.toString();
        szoutPut.delete(0, szoutPut.length());
        return szfinalFormat;

    }
    
  

    static String formatJSONOutPut(String opStatus, StringBuffer result, String szopration, Object object) {
        
        
        szoutPut.append("\"{" + szopration +"\"\n");
        szoutPut.append("{\"status\"\n");
        szoutPut.append("\"code\"\n");
        szoutPut.append("\""+opStatus+"\"");
      
     //   szoutPut.append("\"Description\"\n");
       // szoutPut.append("\""+getDescription(opStatus, sc)+"\"");
        //szoutPut.append("</Description>\n");
        szoutPut.append("}");
        if (!(result == null)) {
            szoutPut.append(result);
        }
        szoutPut.append("}");
        szfinalFormat = szoutPut.toString();
        szoutPut.delete(0, szoutPut.length());
        return szfinalFormat;

        
        
        
    }

    static String formatHostJsonOutPut(String opStatus, StringBuffer result, String szopration, Object object) {
        
        szoutPut.append("\"{" + szopration +"\"\n");
        szoutPut.append("\"{status\"\n");
        szoutPut.append("\"code\"\n");
        szoutPut.append("\""+opStatus+"\"");
      
     //   szoutPut.append("\"Description\"\n");
     //   szoutPut.append("\""+getDescription(opStatus, sc)+"\"");
        //szoutPut.append("</Description>\n");
        szoutPut.append("}");
        if (!(result == null)) {
            szoutPut.append(result);
        }
        szoutPut.append("}");
        szfinalFormat = szoutPut.toString();
        szoutPut.delete(0, szoutPut.length());
        return szfinalFormat;

        
        
    }

    static String formatCustomerInfoOutPut(String opStatus, StringBuffer result, String szopration, Object object) {
        
        szoutPut.append("{\"" + szopration +"\"\n");
        szoutPut.append("{\"status\"\n");
        szoutPut.append("{\"code\"\n");
        szoutPut.append("\""+opStatus+"\"\n");
      
     //szoutPut.append("{\"Description\"\n");
     //szoutPut.append("\""+getDescription(opStatus, sc)+"\"\n");
        //szoutPut.append("</Description>\n");
        szoutPut.append("}\n");
        if (!(result == null)) {
            szoutPut.append(result);
        }
        szoutPut.append("\n}");
        szfinalFormat = szoutPut.toString();
        szoutPut.delete(0, szoutPut.length());
        return szfinalFormat;
    }

    static String formatInstallatonsInfoOutPut(String opStatus, StringBuffer result, String szopration, Object object) {
        
          szoutPut.append("{\"" + szopration +"\"\n");
        szoutPut.append("{\"status\"\n");
        szoutPut.append("{\"code\"\n");
        szoutPut.append("\""+opStatus+"\"\n");
      
      //szoutPut.append("{"\"Description\"\n");
     //szoutPut.append("\""+getDescription(opStatus, sc)+"\"");
        //szoutPut.append("</Description>\n");
        szoutPut.append("}\n");
        if (!(result == null)) {
            szoutPut.append(result);
        }
        szoutPut.append("\n}");
        szfinalFormat = szoutPut.toString();
        szoutPut.delete(0, szoutPut.length());
        return szfinalFormat;
     
        
    }
}
