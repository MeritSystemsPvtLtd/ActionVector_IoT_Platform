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

import org.xml.sax.InputSource;
import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.apache.log4j.Logger;



import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

/**
 *
 * @author gopal
 * Created on Aug 30, 2012, 3:35:00 PM
 */
public class XMLParsing
{

    static Logger log = Logger.getLogger(XMLParsing.class);

    public static HashMap parseXML(StringBuffer xmlStr, javax.servlet.http.HttpServletRequest request)
    {
        // StringBuffer result = new StringBuffer();
        String name = "";
        HashMap clusters = new HashMap();
        String szhosts = "";
        String szTempIp = "";
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            String filestr = (String) xmlStr.toString();
            //System.out.println("XML String inside Configuration::" + filestr);
            InputSource is = new InputSource(new StringReader(filestr));
            Document doc = docBuilder.parse(is);
            doc.getDocumentElement().normalize();
            // System.out.println("Root element of the doc is " + doc.getDocumentElement().getNodeName());
            log.info("Root element of the doc is " + doc.getDocumentElement().getNodeName());
            NodeList listOfClusters = doc.getElementsByTagName("CLUSTER");
            System.out.println("Cluster count" + listOfClusters.getLength());
            for (int s = 0; s < listOfClusters.getLength(); s++)
            {

                Node firstClusterNode = listOfClusters.item(s);
                if (firstClusterNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element firstClusterElement = (Element) firstClusterNode;
                    name = firstClusterElement.getAttribute("NAME");
                    System.out.println("Name of the cluster::" + name);
                    NodeList firstHostList = firstClusterElement.getElementsByTagName("HOST");
                    System.out.println("List of Hosts::" + firstHostList.getLength());

                    for (int i = 0; i < firstHostList.getLength(); i++)
                    {
                        Element firstIpElement = (Element) firstHostList.item(i);

                        szTempIp = (String) firstIpElement.getAttribute("IP");
                        if (szTempIp != null && !szTempIp.equalsIgnoreCase(""))
                        {
                            szhosts += szTempIp + ",";
                        }

                        szTempIp = "";
                    }
                    clusters.put(name, szhosts);
                    szhosts = "";
                    name = "";
                }//end of if

            }//end of for
            System.out.println("Clusters in XMLParsing::" + clusters);
            log.info("Clusters in XMLParsing::" + clusters);

        } catch (ParserConfigurationException pCE)
        {
            pCE.printStackTrace();
            log.error("Exception while parsing::", pCE);
        } catch (SAXException sAXE)
        {
            sAXE.printStackTrace();
            log.error("Exception while parsing::", sAXE);
        } catch (IOException iOE)
        {
            iOE.printStackTrace();
            log.error("Exception while parsing::", iOE);
        }
        if (clusters != null)
        {
            return clusters;
        } else
        {
            return null;
        }
    }

    public static ArrayList<HostBean> parseXML(String szHostXml)
    {
        String szServiceName = null;
        String szSubService = null;
        String szCustomizedService = null;
        String szIp=null;
        ArrayList<HostBean>hostServices=new ArrayList<HostBean>();
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            String filestr = szHostXml.toString();
            InputSource is = new InputSource(new StringReader(filestr));
            Document doc = docBuilder.parse(is);
            doc.getDocumentElement().normalize();
            log.info("Root element of the doc is " + doc.getDocumentElement().getNodeName());
            NodeList listOfServices = doc.getElementsByTagName("service");
            System.out.println("size of the services list::" + listOfServices.getLength());

            for (int temp = 0; temp < listOfServices.getLength(); temp++)
            {
                HostBean hbean=new HostBean();
                Node nNode = listOfServices.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) nNode;
                    szServiceName = getTagValue("name", eElement);
                     hbean.setService(szServiceName);
                    szSubService=getTagValue("subservice", eElement);
                     hbean.setSubService(szSubService);
                    szCustomizedService=getTagValue("customizedservice", eElement);
                     hbean.setCustomizedService(szCustomizedService);
                    szIp=getTagValue("ip", eElement);
                     hbean.setIp(szIp);
                  hostServices.add(hbean);
                }
                hbean=null;
                szServiceName=null;
                szSubService=null;
                szCustomizedService=null;
                szIp=null;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            log.error("exception while parsing the XML ");
            
        }

        if (hostServices!=null)
        return hostServices;
        else
            return null;
    }

    private static String getTagValue(String sTag, Element eElement)
    {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

        Node nValue = (Node) nlList.item(0);

        return nValue.getNodeValue();
    }
}
