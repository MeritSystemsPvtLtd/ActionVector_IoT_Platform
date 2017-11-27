/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package AV_Action;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletOutputStream;
/**
 *
 * @author gopal
 */
public class AV_DownloadXl extends AV_Model {
public void performRequestAction(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, javax.servlet.ServletContext sc) {

         
          
        System.out.println("IAM in DOWNLOAD XLl CALss");
       // response.setContentType("text/html;charset=UTF-8");
        String concatFileName = "";
        //PrintWriter out = response.getWriter();
        String Filename = request.getParameter("filename");
        String fileSplit[] = Filename.split(" ");
        for (int i = 0; i < fileSplit.length; i++)
        {

            concatFileName += fileSplit[i];
        }
      
        ServletOutputStream stream = null;
        Properties properties = new Properties();
        String fp = System.getProperty("file.separator");
        String home =System.getProperty("user.home");
        String configPath = home;
        configPath = configPath + fp+"Chartconfig"+fp+"reportxl.properties";
        try {
            properties.load(new FileInputStream(configPath));
        } catch (IOException ex) {
            Logger.getLogger(AV_DownloadXl.class.getName()).log(Level.SEVERE, null, ex);
        }
        String xl_path = properties.getProperty("memmoryusage");
        xl_path = xl_path + fp + Filename;
        BufferedInputStream buf = null;
        String Filepath = "";

        try {

            stream = response.getOutputStream();
            File pdf = null;
            pdf = new File(xl_path);
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment; filename=" + concatFileName);
            response.setContentLength((int) pdf.length());
            FileInputStream input = new FileInputStream(pdf);
            buf = new BufferedInputStream(input);
            int readBytes = 0;

            while ((readBytes = buf.read()) != -1) {
                stream.write(readBytes);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        finally
        {
           stream=null;

        }

}
}
