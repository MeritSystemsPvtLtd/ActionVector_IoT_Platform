/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AV_Action;

import businessmodel.GetXMLResourceConfiguration;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author rekhas
 */
public class AV_GetXMLResourceConfig extends HttpServlet {

    static Logger log = Logger.getLogger(AV_GetXMLResourceConfig.class);

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/xml;charset=UTF-8");
         StringBuffer result = new StringBuffer();
        String szopration = "GetResourceConfig";
        String szrespStr=null;
        String szSessionid = "";
        boolean fchkParam = false;
        String szResourceConfig = null;
        try
        {
            szSessionid = request.getParameter("szsessionid");
            /*if (szSessionid != null && !szSessionid.equalsIgnoreCase(""))
            {
                
                fchkParam = true;
            }
            if (fchkParam)
            {*/
               
                    result.append("<resourceconfig>");
                    
                    szResourceConfig = GetXMLResourceConfiguration.getResourecConfig(request);
                    if (szResourceConfig == null)
                    {
                        
                        AV_Constants.opStatus=AV_Constants.NODATA_FOUND;
                        log.debug("No data found");
                    } else
                    {
                        if(szResourceConfig.equals(null))
                            result.append("Undefined");
                        else
                            result.append(szResourceConfig);

                        AV_Constants.opStatus=AV_Constants.SUCCESS;
                    }
                    result.append("</resourceconfig>");
                    //System.out.println("Result==>>"+result);
                
            /*} else
            {
                
                AV_Constants.opStatus=AV_Constants.INVALID_INPUTS;

            }*/
        } catch (Exception e)
        {
            e.printStackTrace();
            log.error("Error while performing action", e);
            AV_Constants.opStatus=AV_Constants.FAILURE;
        }
        try
        {
            szrespStr=AV_OutputFormat.formatOutPut(AV_Constants.opStatus,result,szopration,null);
            
            response.getWriter().write(szrespStr);

        } catch (Exception e)
        {
            log.debug("exception while writing output", e);
        } finally
        {
            try
            {
                szopration = null;
                szrespStr=null;
                result = null;

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
