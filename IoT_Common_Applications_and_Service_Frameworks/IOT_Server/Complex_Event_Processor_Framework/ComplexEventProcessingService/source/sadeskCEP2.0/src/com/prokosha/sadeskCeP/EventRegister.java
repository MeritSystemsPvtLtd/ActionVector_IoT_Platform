/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.prokosha.sadeskCeP;

import com.espertech.esper.client.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 *
 * @author Abraham
 */
public class EventRegister {

    private static final Logger log = Logger.getLogger(EventRegister.class.getName());
    private static Configuration cepConfig;

    private static class EventRegisterSAXHandler extends DefaultHandler {

        private static String evName;
        private static Boolean evFound = false;
        private static Stack evStack = new Stack();
        
        public void startDocument() throws SAXException {
            log.debug("Parsing the event registry...");
        }
        
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            log.debug("Start element: " + qName);

            //only "event" element has a attribute which is the event name. Unamed evvents ignored.
            if (qName.equals("event")) {
                for (int att = 0; att < atts.getLength(); att++) {
                    String attName = atts.getQName(att);
                    String attVal = atts.getValue(attName);
                    log.debug(" " + attName + ": " + attVal);
                    if (attName.equals("name")) {
                        evName = attVal;
                        evFound = true;
                    }
                }
            }
        }

        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {

            Map evType = new HashMap<String, Class>();
            String cName, cType;

            log.debug("End Element: "+qName);

            //create the event representation HashMap
            if (qName.equals("event")){
                //pop entire stack of field,type pairs
                log.debug("Composing event: "+evName);
                while (!evStack.empty()) {
                    try {
                        cType = evStack.pop().toString();
                        cName = evStack.pop().toString();
                        log.debug("Type: " + cType.toString());
                        log.debug("Name: " + cName.toString());
                        //evType.put(evStack.pop().toString(), Class.forName(evStack.pop().toString()));
                        evType.put(cName, Class.forName(cType));
                    } catch (ClassNotFoundException ex) {
                    }
                }
                cepConfig.addEventType(evName, evType);
                evFound = false;
            }
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            String token = new String(ch, start, length);
            //ignore newlines
            token = token.trim();
            if (!token.isEmpty()) {
                log.debug(token);
                if (evFound) {
                    evStack.push(token);
                }
            }
        }
        public void ignorableWhitespace(){}
        
    }

    public EventRegister(){}

    public static Configuration registerEvents(String regFile, Configuration cepConfig) throws ParserConfigurationException, SAXException, IOException {

        EventRegister.cepConfig = cepConfig;
        
        XMLReader xmlReader = null;
        SAXParserFactory spfactory = SAXParserFactory.newInstance();
        spfactory.setValidating(false);
        SAXParser saxParser = spfactory.newSAXParser();
        xmlReader = saxParser.getXMLReader();

        EventRegisterSAXHandler saxHandler = new EventRegisterSAXHandler();
        xmlReader.setContentHandler(saxHandler);

        InputSource source = new InputSource(regFile);
        xmlReader.parse(source);

        return EventRegister.cepConfig;
    }

}
