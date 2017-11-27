/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package AV_Action;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.io.IOException;
import org.apache.log4j.Logger;
/**
 *
 * @author gopal
 */
public class AV_GetXlPath extends AV_Model {
    static Logger log = Logger.getLogger(AV_GetXlPath.class);
public void performRequestAction(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, javax.servlet.ServletContext sc) {

    response.setContentType("text/xml;charset=UTF-8");
        String szopration = "file-names";
        String szrespStr=null;
        StringBuffer result = null;
        boolean filesource = false;
       // String szSessionid = "";
        //boolean checksession = false;
       // szSessionid = request.getParameter("szsessionid");
          try
            {
                System.out.println("GETXLPAth started");
                Properties properties = new Properties();
                try
                {
                    String fp = System.getProperty("file.separator");
                    String home = System.getProperty("user.home");
                    String configPath = home;
                    configPath = configPath + fp+"Chartconfig"+fp+"reportxl.properties";
                    log.info("path=========" + configPath);
                    properties.load(new FileInputStream(configPath));
                    filesource = true;
                } catch (Exception fe)
                {
                    fe.printStackTrace();
                    AV_Constants.opStatus=AV_Constants.WEBSERVICE_ERROR;
                }

                if (filesource)
                {
                    String xl_path = properties.getProperty("memmoryusage");
                    System.out.println("XL path::"+xl_path);
                    File pathName = new File(xl_path);
                    String[] fileNames = pathName.list();
                    result = new StringBuffer("<files>");
                    for (int i = 0; i < fileNames.length; i++)
                    {
                        result.append("<name>");
                        result.append(fileNames[i]);
                        result.append("</name>");
                    }
                    result.append("</files>");
                    AV_Constants.opStatus=AV_Constants.SUCCESS;
                } else
                {
                  
                    AV_Constants.opStatus=AV_Constants.NODATA_FOUND;
                }

            } catch (Exception e)
            {
                System.out.println("Error: " + e);
                e.printStackTrace();
                AV_Constants.opStatus=AV_Constants.FAILURE;
            }
       
        try
        {
            szrespStr=AV_OutputFormat.formatOutPut(AV_Constants.opStatus,result,szopration,sc);
            response.getWriter().write(szrespStr);

        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                szrespStr=null;
                szopration=null;
                result = null;
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
}
}
