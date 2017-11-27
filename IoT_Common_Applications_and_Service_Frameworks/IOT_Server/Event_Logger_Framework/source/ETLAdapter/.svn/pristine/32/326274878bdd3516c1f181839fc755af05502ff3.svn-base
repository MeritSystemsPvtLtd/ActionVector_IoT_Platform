package com.prokosha.adapter.etl.message;

import com.prokosha.adapter.etl.ETLAdapter.TokenParser;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Abraham
 */

public class OVHttpMsg extends OVMsg {
    private static final Logger log = Logger.getLogger(OVHttpMsg.class.getName());

    public OVHttpMsg (String pgInput) {        
        super(pgInput);
        initialize();
    }

    private void initialize() {

        String Response_Time="-1.1";

        if (!getServiceperfdata().equals("(null)"))
        {
            log.debug("Service perfdata:: " + getServiceperfdata());
            String spd[] = getServiceperfdata().split(";");
            log.debug("Perfdata:: token 0:: " + spd[0]);

            if (TokenParser.isValidToken(spd, 0)) {
                String tok[] = spd[0].split("=");
                Response_Time = TokenParser.replaceTrimToken(tok, 1, 's', ' ', "-1.1");
            }
            log.debug("Perfdata:: Response time:: " + Response_Time);
        } else {
            log.error("*** ERROR *** invalid messages received... cannot parse msg...");
        }

       setCepMsg("stream=HTTPResponseTimeEvent," +
               "TimeStamp="+getServicetime()+"," +
               "HostName=" + getHostname() + "," +
               "ResourceID=304," +
               "ResponseTime="+Response_Time);
    }

    public String getCepMsg() {
        return super.getCepMsg();
    }


     public static void main(String[] args) {

         PropertyConfigurator.configure("config/logger.properties");

         String samples[] = {
	"1297250075||Portfolio16||304 check resin-and-httpd||HTTP OK HTTP/1.1 200 OK - 0.00 second response time||time=0.001357s;;;0.000000 size=237B;;;0",
        "1298524330||money2||304 check resin-and-httpd",
        "1298524330||money2||304 check resin-and-httpd||",
        "1298524330||money2||304 check resin-and-httpd||||",
        "1298524330||money2||304 check resin-and-httpd||Dependency failure||",
        "1298524330||money2||304 check resin-and-httpd||Connection refused||",
        "1298524330||money2||304 check resin-and-httpd||CHECK_NRPE: Socket timeout after 10 seconds.",
        "1298524330||money2||304 check resin-and-httpd||CHECK_NRPE: Socket timeout after 10 seconds.||",
	"1297250078||Portfolio2||304 check resin-and-httpd||HTTP OK HTTP/1.1 200 OK - 0.001 second response time||time=0.001353s;;;",
	"1297250078||Portfolio2||304 check resin-and-httpd||HTTP OK HTTP/1.1 200 OK - 0.001 second response time||;;0.000000 size=237B;;;0",
	"1297255011||Portfolio8||304 check resin-and-httpd||HTTP OK HTTP/1.1 200 OK - 0.001 second response time||time;;;0.000000 size=237B;;;0",
	"1297255011||Portfolio8||304 check resin-and-httpd||HTTP OK HTTP/1.1 200 OK - 0.001 second response time||time=;;;0.000000 size=237B;;;0",
	"1297255078||Portfolio15||304 check resin-and-httpd||HTTP OK HTTP/1.1 200 OK - 0.001 second response time||time=0.000147;;;0.000000 size=237B;;;0",
	"1297255078||Portfolio15||304 check resin-and-httpd||HTTP OK HTTP/1.1 200 OK - 0.001 second response time||time= 0.000147;;;0.000000 size=237B;;;0",
	"1297255078||Portfolio15||304 check resin-and-httpd||HTTP OK HTTP/1.1 200 OK - 0.001 second response time||time=s;;;0.000000 size=237B;;;0",
	"1297255078||Portfolio15||304 check resin-and-httpd||HTTP OK HTTP/1.1 200 OK - 0.001 second response time||time= s;;;0.000000 size=237B;;;0",
	"1297260300||Portfolio14||304 check resin-and-httpd||HTTP OK HTTP/1.1 200 OK - 0.001 second response time||time=0.001328s;;;0.000000 size=237B;;;0"
         };
      
        for (int i=0; i<samples.length; i++) {
            System.out.println("----- Test Case " + i + " ----------");
            System.out.println("Input Msg: " + samples[i]);
            OVHttpMsg ovhttp = new OVHttpMsg(samples[i]);
            System.out.println("Parsed CEP Msg: " + ovhttp.getCepMsg());
            System.out.println("----- End Test Case ----------\n");
        }
        
     }
}
