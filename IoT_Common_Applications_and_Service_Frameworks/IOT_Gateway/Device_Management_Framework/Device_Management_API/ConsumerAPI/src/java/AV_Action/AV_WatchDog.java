/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AV_Action;

import com.prokosha.watchdog.ConfigValues;
import com.prokosha.watchdog.WatchAction;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author niteshc
 */
public class AV_WatchDog extends HttpServlet implements Runnable {

    static Logger log = Logger.getLogger(AV_WatchDog.class);
    Thread searcher;
    int i = 0;
    private static boolean isinitialized = false;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        String input = null;
        try {
            input = request.getParameter("event");
            log.info("WatchDogEvent=" + input);
            if (input != null) {
                WatchAction.watchAction(input);
                log.info("event time updated");
            }
        } catch (Exception e) {
            log.error("Error handling client# ", e);
        } finally {
            try {
                input = null;
                log.info("connection closed");
            } catch (Exception e) {
                log.error("Couldn't close a socket, what's going on?");
            }
            log.info("Connection with client  closed");
        }
    }

    public void init(ServletConfig config) throws ServletException {
        isinitialized = ConfigValues.initialized();
        if (isinitialized) {
            if (ConfigValues.isEnabled()) {
                log.info("WatchDog enabled");
                System.out.println("WatchDog enasbled");
                searcher = new Thread(this);
                searcher.setPriority(Thread.MIN_PRIORITY);
                searcher.start();
            } else {
                log.info("WatchDog disabled");
                System.out.println("WatchDog diasbled");
            }
        }
    }

    public void run() {
        i++;
        while (true) {
            try {
                searcher.sleep(ConfigValues.getIsleepInterval());
                WatchAction.sendWatchEvent();
                log.info("*********************");
                log.info("sleep time for " + ConfigValues.getIsleepInterval() / 1000 + " seconds");

            } catch (InterruptedException ex) {
                log.error("Error in watchdog servlet", ex);
                // java.util.logging.Logger.getLogger(StartWatch.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void destroy() {
        searcher.stop();
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
