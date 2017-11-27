/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AV_Action;

import Model.DatabaseConnection;
import businessmodel.GetJSONResourceConfiguration;
import com.adminAPI.DeviceManagment;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author niteshc
 */
public class AV_GetJSONResourceConfig extends HttpServlet {

    static Logger log = Logger.getLogger(AV_GetJSONResourceConfig.class);

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

        response.setContentType("application/json;charset=UTF-8");
        StringBuffer result = new StringBuffer();
        PrintWriter out = response.getWriter();
        String customer = null;
        String service = null;
        String subservice = null;
        String resource = null;
        String paramname = null;
        String resouceconfig = null;
        try {
            customer = request.getParameter("customer");
            service = request.getParameter("service");
            subservice = request.getParameter("subservice");
            resource = request.getParameter("resourceid");
            paramname = request.getParameter("paramname");
            Connection con = DatabaseConnection.getAVSAConnection(request);
            resouceconfig=new DeviceManagment().getResourceConfig(con,customer,service,subservice,resource,paramname);
            
            /**
            resouceconfig = GetJSONResourceConfiguration.getResourceConfig(request, customer, service, subservice, resource, paramname);
            ***/
             if (resouceconfig == null) {
                log.debug("No data found");
                result.append("{\"error\":\"error\"}");
            } else {
                resouceconfig = "[" + resouceconfig + "]";
                String callback = request.getParameter("callback");
                result.append(callback + "('" + resouceconfig + "')");
            }
            /**
            String callback = request.getParameter("callback");
            result.append(callback + "('" + resouceconfig + "')");
            **/
            //System.out.println("Response==>" + result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while performing action", e);
        }
        try {
            response.getWriter().write(result.toString());
        } catch (Exception e) {
            log.debug("exception while writing output", e);
        } finally {
            try {
                result = null;
                customer = null;
                service = null;
                resource = null;
                resource = null;
                paramname = null;
                if (out != null) {
                    out.close();
                    out = null;
                }
            } catch (Exception e) {
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
