
/* ------------------------------------------------------------------------
Software Developed by
Merit Systems Pvt. Ltd.,
No. 42/1, 55/c, Nandi Mansion, 40th Cross, Jayanagar 8th Block
Bangalore - 560 070, India
Work Created for Merit Systems Private Limited
All rights reserved

THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES
NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED,
DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED,
EXPANDED, COLLECTED, COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED
WITHOUT THE PRIOR WRITTEN CONSENT
ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT
THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
-------------------------------------------------------------------------- */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author gopal
 */
public class InitLogServlet extends HttpServlet
{

    private ServletContext context;
    String sPrefix;

    public void init(ServletConfig config) throws ServletException
    {

        this.context = config.getServletContext();
        super.init(config);
        System.out.println("Log4JInitServlet is initializing log4j");
        String log4jLocation = config.getInitParameter("log4j-properties-location");
        ServletContext sc = config.getServletContext();
        sPrefix = getServletContext().getRealPath("/");
        System.out.println("SERVLET CONTEXT : " + sPrefix);
        System.setProperty("info.path", sPrefix);
        if (log4jLocation == null)
        {
            System.err.println("*** No log4j-properties-location init param, so initializing log4j with BasicConfigurator");
            BasicConfigurator.configure();
        } else
        {
            String webAppPath = sc.getRealPath("/");

            String log4jProp = webAppPath + log4jLocation;
            File logFile = new File(log4jProp);
            if (logFile.exists())
            {
                System.out.println("Initializing log4j with: " + log4jProp);
                PropertyConfigurator.configure(log4jProp);
            } else
            {
                System.err.println("*** " + log4jProp + " file not found, so initializing log4j with BasicConfigurator");
                BasicConfigurator.configure();
            }
        }
        super.init(config);
    }
}
