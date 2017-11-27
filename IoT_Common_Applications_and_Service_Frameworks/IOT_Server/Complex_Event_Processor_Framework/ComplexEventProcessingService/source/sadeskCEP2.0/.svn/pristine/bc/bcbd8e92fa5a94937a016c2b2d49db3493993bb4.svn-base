/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.prokosha.sadeskCeP;

import java.io.*;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.apache.log4j.Logger;
import com.espertech.esper.client.*;
import com.espertech.esper.client.deploy.*;


/**
 *
 * @author Abraham
 */
public class ModuleLoader {

   private static final Logger log = Logger.getLogger(ModuleLoader.class.getName());

   private static class ModuleLoaderSAXHandler extends DefaultHandler {
        private List<String> modList = new ArrayList<String>();

        private static StringBuffer modNameBuffer;
        private static Boolean modBegin = false;
        private static Boolean parseDone = false;
 
        public void startDocument() throws SAXException {
            log.debug("Parsing the module list registry...");
            parseDone = false;
        }
        public void endDocument() throws SAXException {
            log.debug("Completed parsing the module list registry...");
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

            log.debug("Start Element: " + qName);

            //only "module" elements exists for now. So just note that we found the element.
            if (qName.equals("module")) {
                if (modBegin) {
                    //nested module definitions not supported
                    //TODO: should throw an exception here
                    log.error("** ERROR ** Nested module definitions not supported in module registry");
                } else {
                    modNameBuffer = new StringBuffer();
                    modBegin = true;
                }
            } else {
                if (qName.equals("config")) {
                    parseDone = false;
                } else {
                    //only "module" elements exists for now. So just note that we found the element.
                    // TODO: Throw exception here
                    log.error("** ERROR ** Expecting only <module> element in module registry - found " + qName + " instead!");
                }
            }
        }

        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {

            log.debug("End Element: "+qName);
            if (qName.equals("module")) {
                if (modBegin) {
                    modList.add(modNameBuffer.toString());
                    modBegin = false;
                } else {
                    //TODO: throw exception
                    log.error("** ERROR ** Format error in module registry - found ");
                }
            } else {
                if (qName.equals("config")) {
                    parseDone = true;
                } else {
                    //only "module" elements exists for now. So just note that we found the element.
                    // TODO: Throw exception here
                    log.error("** ERROR ** Expecting only <module> element in module registry - found " + qName + " instead!");
                }
            }

        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            //ignore newlines and empty lines
            if (modBegin) {
                String token = new String(ch, start, length);
                token = token.trim();
                if (!token.isEmpty()) {
                    log.debug(token);
                    modNameBuffer.append(token);
                }
            }
        }
        
        public void ignorableWhitespace(){}

        public List<String> getModuleList(){
            if (parseDone)
                return modList;
            else {
                log.error("** ERROR ** Attempting to retrieve module list before parsing registry...");
                return null;
            }
        }

    }


   public ModuleLoader(){}

   public void loadModules(EPServiceProvider hCepProv, String configFile) throws ParserConfigurationException, SAXException, IOException, ParseException, DeploymentException {

       //read the config file and get the list of modules to load
       List<String> modList = loadConfig(configFile);
       
       //load all the module configurations
       loadAndDeployModules(hCepProv, modList);
       
   }

   private List<String> loadConfig(String cfgFile) throws ParserConfigurationException, SAXException, IOException {
        XMLReader xmlReader = null;
        SAXParserFactory spfactory = SAXParserFactory.newInstance();
        spfactory.setValidating(false);
        SAXParser saxParser = spfactory.newSAXParser();
        xmlReader = saxParser.getXMLReader();

        ModuleLoaderSAXHandler saxHandler = new ModuleLoaderSAXHandler();
        xmlReader.setContentHandler(saxHandler);

        InputSource source = new InputSource(cfgFile);
        xmlReader.parse(source);

        return saxHandler.getModuleList();
   }

   private void loadAndDeployModules(EPServiceProvider hCepProv, List<String> modList) throws IOException, ParseException, DeploymentException {
       // get handle to engine administrator - to config runtime - create statements, etc.
        EPAdministrator hCepAdm = hCepProv.getEPAdministrator();
        EPDeploymentAdmin deployAdmin = hCepAdm.getDeploymentAdmin();

        List<Module> readModList = new ArrayList<Module>();
        for (String moduleName: modList) {
//            readModList.add(deployAdmin.read(new File("config/"+moduleName)));
            readModList.add(deployAdmin.read(new File(moduleName)));
        }

        //order the modules based on inter-module dependency
        DeploymentOrder order = deployAdmin.getDeploymentOrder(readModList, new DeploymentOrderOptions());

        //deploy the modules
        DeploymentResult deployResult;
        for (Module modItem : order.getOrdered()) {
            deployResult = deployAdmin.deploy(modItem, new DeploymentOptions());
        }

    }

}
