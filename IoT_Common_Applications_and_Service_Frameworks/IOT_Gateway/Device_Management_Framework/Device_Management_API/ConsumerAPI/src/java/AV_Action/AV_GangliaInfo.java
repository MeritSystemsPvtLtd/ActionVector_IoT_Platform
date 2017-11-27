/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package AV_Action;
import businessmodel.*;


import org.apache.log4j.Logger;
/**
 *
 * @author gopal
 */
public class AV_GangliaInfo extends AV_Model{
static Logger log = Logger.getLogger(AV_GangliaInfo.class);
public void performRequestAction(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, javax.servlet.ServletContext sc) {

        response.setContentType("text/xml;charset=UTF-8");
        StringBuffer results = new StringBuffer("<userprofile>");
        StringBuffer result = new StringBuffer();
        String szopration = "gangliainfo";
        String szrespStr=null;
        String szSessionid = "";
        boolean fchecksession = false;
        boolean fchkParam = false;
        String szIpaddress = "";
        String szPort = "";
        int iPort = 0;
        String szClusterXML = null;
        try
        {
            szSessionid = request.getParameter("szsessionid");
            szIpaddress = request.getParameter("szipaddress");
            szPort = request.getParameter("szport");
            if ((szSessionid != null && !szSessionid.equalsIgnoreCase("")) && (szIpaddress != null && !szIpaddress.equalsIgnoreCase("")) && (szPort != null && !szPort.equalsIgnoreCase("")))
            {
                iPort = Integer.parseInt(szPort);
                fchkParam = true;
            }
            if (fchkParam)
            {
                  try
                    {
                        GangliaRequestXML gXMLObj = new GangliaRequestXML();
                        szClusterXML = gXMLObj.getSocketXML(szIpaddress, iPort, request);
                       
                        if (szClusterXML.equalsIgnoreCase("success"))
                        {
                           
                            AV_Constants.opStatus=AV_Constants.SUCCESS;

                        } else
                        {
                            
                            AV_Constants.opStatus=AV_Constants.FAILURE;
                        }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        
                        AV_Constants.opStatus=AV_Constants.FAILURE;
                        log.debug("Unable to process the request action", e);
                    }
                
            } else
            {
                
                AV_Constants.opStatus=AV_Constants.INVALID_INPUTS;
            }
        } catch (Exception e)
        {
            log.debug("Exception while performing operation ", e);
        }
        try
        {

            szrespStr=AV_OutputFormat.formatOutPut(AV_Constants.opStatus,szopration,sc);
            
            response.getWriter().write(szrespStr);

        } catch (Exception e)
        {
            log.debug("exception while writing output", e);
        } finally
        {
            try
            {
                results = null;
                result = null;
                szopration=null;
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

}
}
