/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.http.eventlistener;

import com.prokosha.eventreceivermapper.EventReceiverMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 *
 * @author niteshc
 */
public class HTTPEventReceiverServlet extends HttpServlet {

    static Logger log = Logger.getLogger(HTTPEventReceiverServlet.class);
    static HashMap IPMap = null;

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
        response.setContentType("text/html;charset=UTF-8");
        String eventype = "";
        String data = null;
        String currIPAdd = null;
        String serverIPAdd = null;
        String cCustomer = null, Service = null;
        StringTokenizer strToken = null;
        try {
            /* TODO output your page here. You may use following sample code. */
            currIPAdd = request.getRemoteAddr();
            serverIPAdd = InetAddress.getLocalHost().getHostAddress();
            data = request.getParameter("event");
            //System.out.println("request IP Address::" + currIPAdd);
            //System.out.println("server IP Address::" + serverIPAdd);
            if ((currIPAdd.equalsIgnoreCase("127.0.0.1")) || (currIPAdd.equalsIgnoreCase(serverIPAdd))) {
                eventype = data.split(",")[0].trim();
                eventype = eventype.split("=")[1].trim();
                EventReceiverMapper.addressMap.sendEvent(eventype, data);
            } else {
                strToken = new StringTokenizer(data, ",");
                while (strToken.hasMoreTokens()) {
                    String eventypes = strToken.nextToken();
                    if (eventypes.contains("cCustomer")) {
                        cCustomer = eventypes.split("=")[1].trim();
                        //   System.out.println("cCustomer==>" + cCustomer);
                    }
                    if (eventypes.contains("service")) {
                        Service = eventypes.split("=")[1].trim();
                        //   System.out.println("Service==>" + Service);
                    }
                }
                String currID = cCustomer + "-" + Service;
                System.out.println("ID from event==" + currID);
                System.out.println("IPMap keys==" + IPMap.keySet().toString());
                if (IPMap.containsKey(currID)) {
                    if (IPMap.get(currID).toString().equalsIgnoreCase(currIPAdd)) {
                        eventype = data.split(",")[0].trim();
                        eventype = eventype.split("=")[1].trim();
                        System.out.println("Received event=" + eventype);
                        EventReceiverMapper.addressMap.sendEvent(eventype, data);
                    } else {
                        System.out.println("IP don't match, Spooring Problem");
                    }
                } else {
                    System.out.println("cust-service don't match, Spooring Problem");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (strToken != null) {
                strToken = null;
            }
        }
    }

    public void init(ServletConfig config) throws ServletException {
        EventReceiverMapper.initialize();
        readFile();
    }

    public static void readFile() {
        BufferedReader br = null;
        IPMap = new HashMap();
        String Id = null;
        File file = new File(System.getProperty("user.home")
                + System.getProperty("file.separator") + "IP_Address.txt");
        if (file.exists()) {
            try {
                br = new BufferedReader(new FileReader(file));
                while ((Id = br.readLine()) != null) {
                    String ID = Id.split("=")[0].trim();
                    System.out.println("ID==>" + ID);
                    String IPAdd = Id.split("=")[1].trim();
                    System.out.println("IPAdd==>" + IPAdd);
                    IPMap.put(ID, IPAdd);
                }
            } catch (FileNotFoundException ex) {
                System.out.println("Error while reading the file");
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        } else {
            System.out.println("File doesn't exist");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
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
     * Handles the HTTP
     * <code>POST</code> method.
     *
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
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
