/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controller;
//import AV_Action.AV_Action;
import AV_Action.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
/**
 *
 * @author raghu
 */
public class AV_ControllerServlet extends HttpServlet {
   private HashMap urlMappings;
    private HashMap actions;
    private HashMap modelcls;
    private ServletContext context;
    String sPrefix;
     public void init(ServletConfig config) throws ServletException {

        modelcls = new HashMap();

        this.context = config.getServletContext();
        super.init(config);
        String requestMappingsURL = null;
        try {
            requestMappingsURL = context.getResource("/WEB-INF/actionmapping.xml").toString();

        } catch (java.net.MalformedURLException ex) {
            ex.getMessage();
        }
        try {
            Document doc = null;
            Element root = null;
            try {
                URL url = new URL(requestMappingsURL);
                InputSource xmlInp = new InputSource(url.openStream());
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                doc = docBuilder.parse(xmlInp);

                NodeList listOfOperations = doc.getElementsByTagName("operation-mapping");
                int totalOperations = listOfOperations.getLength();
                for (int s = 0; s < listOfOperations.getLength(); s++) {
                    Node OperationNode = listOfOperations.item(s);
                    if (OperationNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element MainOperationElement = (Element) OperationNode;

                        NodeList OperationList = MainOperationElement.getElementsByTagName("operation-name");
                        Element OperationElement = (Element) OperationList.item(0);
                        NodeList operationList = OperationElement.getChildNodes();
                        String szOperationName = ((Node) operationList.item(0)).getNodeValue().trim();

                        NodeList ModelNameList = MainOperationElement.getElementsByTagName("action-model");
                        Element ModelNameElement = (Element) ModelNameList.item(0);
                        NodeList ModelList = ModelNameElement.getChildNodes();
                        String szActionModelName = ((Node) ModelList.item(0)).getNodeValue().trim();
                        try {
                            modelcls.put(szOperationName, szActionModelName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }//end of if clause
                }
            } catch (SAXParseException err) {
                System.err.println("URLMappingsXmlDAO ** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
                System.err.println("URLMappingsXmlDAO error: " + err.getMessage());
            } catch (SAXException e) {
                System.err.println("URLMappingsXmlDAO error: " + e);
            } catch (java.net.MalformedURLException mfx) {
                System.err.println("URLMappingsXmlDAO error: " + mfx);
            } catch (java.io.IOException e) {
                System.err.println("URLMappingsXmlDAO error: " + e);
            } catch (Exception pce) {
                System.err.println("URLMappingsXmlDAO error: " + pce);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // System.out.println("I m in COntroller Servlet...");
        //response.setContentType("text/html;charset=UTF-8");
        javax.servlet.ServletContext sc;
       
        try {
            String op = getOperation(request);
            System.out.println("The Operation to be performed ==" + op);
            Object SubModelObj = modelcls.get(op);
            System.out.println("The Model found here =====" + SubModelObj);
            Object ActionObject = null;
            AV_Model ModelObject = null;
            try {
                Class SubModelClass = Class.forName(SubModelObj.toString());
                ModelObject = (AV_Model) getClass().getClassLoader().loadClass(SubModelObj.toString()).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            ServletContext Application = getServletContext();
            AV_Action mainAction = new AV_Action(ModelObject);
            Object result = null;
            sc = getServletContext();
            request.setAttribute("sPrefix", sPrefix);

            try {
                result = mainAction.perform(request, response, sc);

            } catch (Exception e) {
                System.out.println("result not found");
                e.printStackTrace();

            }
            if ("true".equalsIgnoreCase(result.toString())) {
               // String actionresult = (String) request.getAttribute("result");
               System.out.println("request processed successfully");
               // out.println(actionresult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
         return super.getServletInfo();
    }// </editor-fold>
public String getOperation(javax.servlet.http.HttpServletRequest request) {
        String szOperationString = "";
        try {
            szOperationString = request.getParameter("op");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return szOperationString;
    }
}
