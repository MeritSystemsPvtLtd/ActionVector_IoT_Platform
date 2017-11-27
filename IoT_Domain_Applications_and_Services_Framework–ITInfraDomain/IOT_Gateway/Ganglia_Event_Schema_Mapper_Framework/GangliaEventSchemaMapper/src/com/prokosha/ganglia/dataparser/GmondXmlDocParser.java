/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.ganglia.dataparser;

import java.io.StringReader;
import java.util.HashMap;
import org.apache.log4j.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 *
 * @author Abraham
 */
public class GmondXmlDocParser {

    private static final Logger log = Logger.getLogger(GmondXmlDocParser.class.getName());
    XMLReader xmlReader = null;

    private static class EventRegisterSAXHandler extends DefaultHandler {

        HashMap hm = new HashMap();

        public void startDocument() throws SAXException {
            log.debug("Start document");
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            log.debug("Start Element:- localname: " + localName + " and element: " + qName);
            hm.clear();
            for (int att = 0; att < atts.getLength(); att++) {
                String attName = atts.getQName(att);
                String attVal = atts.getValue(attName);
                hm.put(attName, attVal);
            }
            NodeWatcher.nodeStart(qName, hm);
        }

        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            log.debug("End Element:- localname: " + localName + " and element: " + qName);
            NodeWatcher.nodeEnd(qName);
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            log.debug("Characters found: " + ch);
            String token = new String(ch, start, length);
            //ignore newlines
            token = token.trim();
            if (!token.isEmpty()) {
                log.debug(token);
            }
        }

        public void ignorableWhitespace() {
            log.debug("Ignorable whitespaces");
        }
    }

    public GmondXmlDocParser() {
        try {
            SAXParserFactory spfactory = SAXParserFactory.newInstance();
            spfactory.setValidating(false);
            SAXParser saxParser = spfactory.newSAXParser();
            xmlReader = saxParser.getXMLReader();

            EventRegisterSAXHandler saxHandler = new EventRegisterSAXHandler();
            xmlReader.setContentHandler(saxHandler);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void parseDocFromFile(String gangliaXmlFile) {
        try {
            log.debug("Begining parsing the Ganglia XML document: " + gangliaXmlFile);
            InputSource source = new InputSource(gangliaXmlFile);
            xmlReader.parse(source);
            log.debug("Parsing Ganglia XML document: " + gangliaXmlFile + "completed...");
        } catch (Exception ex) {
            log.error("**** ERROR ***** Ganglia XML error while parsing file: " + gangliaXmlFile + "\n" + ex);
        }
    }

    public void parseDocFromBuffer(StringBuffer gangliaXmlBuffer) {

        try {
            log.debug("Begining parsing the Ganglia XML document buffer...");
            InputSource source = new InputSource(new StringReader(gangliaXmlBuffer.toString()));
            xmlReader.parse(source);
            log.debug("Parsing Ganglia XML document buffer completed...");
        } catch (Exception ex) {
            log.error("**** ERROR ***** Ganglia XML error while parsing XML document buffer \n " + ex);
        }
    }
}
