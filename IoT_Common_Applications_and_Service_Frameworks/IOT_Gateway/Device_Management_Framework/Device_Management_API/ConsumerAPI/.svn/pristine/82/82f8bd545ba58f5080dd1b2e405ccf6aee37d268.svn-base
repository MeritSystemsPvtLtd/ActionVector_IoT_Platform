/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/***************************************************************************
 *
 *                            Software Developed by
 *                           Merit Systems Pvt. Ltd.,
 *     #55/C-42/1, Nandi Mansion, Ist Floor 40th Cross, Jayanagar 8th Block
 *                          Bangalore - 560 070, India
 *               Work Created for Merit Systems Private Limited
 *                             All rights reserved
 *
 *          THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT
 *                              LAWS AND TREATIES
 *       NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED,
 *             DISTRIBUTED, REVISED, MODIFIED,TRANSLATED, ABRIDGED,
 *                                  CONDENSED,
 *        EXPANDED, COLLECTED, COMPILED, LINKED, RECAST, TRANSFORMED OR
 *                                   ADAPTED
 *                      WITHOUT THE PRIOR WRITTEN CONSENT
 *          ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION
 *                                COULD SUBJECT
 *               THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 *
 *
 ***************************************************************************/
package businessmodel;

import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 *
 * @author gopal
 * Created on Aug 30, 2012, 3:24:45 PM
 */
public class GangliaRequestXML
{

    static Logger log = Logger.getLogger(GangliaRequestXML.class);

    public String getSocketXML(String serverHostname, int serverPort,javax.servlet.http.HttpServletRequest request)
    {
        Socket sock = null;
        InputStream sockInput = null;
        String szClusters =null;
        HashMap Clusters=new HashMap();
        boolean fconfig=false;
        System.err.println("Opening connection to " + serverHostname + " port " + serverPort);
        try
        {
            sock = new Socket(serverHostname, serverPort);
            sockInput = sock.getInputStream();

        } catch (IOException e)
        {
            e.printStackTrace(System.err);
            return null;
        }
        try
        {
            System.out.println("About to start reading/writing to/from socket.");
            BufferedReader fromGmond = new BufferedReader(new InputStreamReader(sockInput));
            StringBuffer xmlBuff = new StringBuffer("");
            boolean more = true;
            String data = "";
            log.debug("Reading XML document from gmond (" + serverHostname + ":" + serverPort + ")...");
            while (more)
            {//read the full ganglia XML doc
                try
                {
                    data = fromGmond.readLine();
                    if (data == null)
                    {
                        more = false;
                    } else
                    {
                        xmlBuff.append(data);
                    }
                } catch (Exception ex)
                {
                    log.error("*** ERROR *** Reading from gmond (" + serverHostname + ":" + serverPort + ") failed. Terminating download!!" + ex);
                    //gmondDisconnect();
                    return null;
                }
            }
            Clusters = XMLParsing.parseXML(xmlBuff,request);

            log.info("inserting into ipinfo and hostinfo tables");
            fconfig=ConfigHosts.configureHosts(Clusters,request);
            //System.out.println("The Clusters XML::" + szClusters);
            System.err.println("Done reading/writing to/from socket, closing socket.");
            log.info("Done reading/writing to/from socket, closing socket.");
        } catch (Exception e)
        {
            log.error("error while parsing the xml::", e);
        } finally
        {
            try
            {
                sock.close();
            } catch (IOException e)
            {
                System.out.println("Exception closing socket.");
                log.debug("Exception closing socket.", e);
                e.printStackTrace();
            }
        }
        if (fconfig)
        {
            return "success";
        } else
        {
            return null;
        }
    }
}
