/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AV_Action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author niteshc
 */
public class AV_PublishIP extends HttpServlet {

    static Logger log = Logger.getLogger(AV_PublishIP.class);
    static HashMap hs = new HashMap();
    static BufferedWriter bw = null;
    static BufferedReader br = null;
    static File file = null;

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
        String id = null;
        String ipAddress = null;
        FileWriter fw = null;
        FileReader fr = null;
        try {
            System.out.println(" AV_PublishIP Started ");
            id = request.getParameter("id");
            ipAddress = request.getRemoteAddr();
            file = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "IP_Address.txt");
            System.out.println("file===>" + file);
            if (!file.exists()) {
                file = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "IP_Address.txt");
                file.createNewFile();
                fw = new FileWriter(file);
                bw = new BufferedWriter(fw);
                bw.write(id + "=" + ipAddress);
                bw.newLine();
                bw.flush();
                // log.info("first id " + id + "=" + ipAddress + " is added ");
                System.out.println("first id " + id + "=" + ipAddress + " is added ");
            } else {
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                String oldIP = null;
                String updatedIP = null;
                String currentID = null, oldText = "";;
                while ((currentID = br.readLine()) != null) {
                    hs.put(currentID.split("=")[0].trim(), currentID.split("=")[1].trim());
                    oldText += currentID + "\r";
                    if (hs.containsKey(id)) {
                        oldIP = id + "=" + (String) hs.get(id);
                    }
                }
                br.close();
                if (hs.containsKey(id)) {
                    if (!(hs.get(id).toString().equalsIgnoreCase(ipAddress))) {
                        System.out.println(" id already exist");
                        updatedIP = oldText.replaceAll(oldIP, id + "=" + ipAddress);
                        fw = new FileWriter(file);
                        bw = new BufferedWriter(fw);
                        bw.write(updatedIP);
                        bw.newLine();
                        bw.flush();
                        //   log.info(id + "=" + ipAddress + " is updated");
                        System.out.println(id + "=" + ipAddress + " is updated");
                    }
                } else if (!hs.containsKey(id)) {
                    System.out.println("id not exist");
                    fw = new FileWriter(file, true);
                    bw = new BufferedWriter(fw);
                    bw.write(id + "=" + ipAddress);
                    bw.newLine();
                    bw.flush();
                    // log.info(id + "=" + ipAddress + " is added");
                    System.out.println(id + "=" + ipAddress + " is added");
                }
            }
            if (fr != null) {
                fr.close();
            }
            fr = null;
            if (fw != null) {
                fw.close();
            }
            fw = null;
            AV_FrontController.readFile();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (fr != null) {
                fr.close();
            }
            fr = null;
            if (fw != null) {
                fw.close();
            }
            fw = null;
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
