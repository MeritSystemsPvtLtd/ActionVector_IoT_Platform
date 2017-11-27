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
 * @author Bimal
 */
public class OVProcessMsg extends OVMsg {

    private static final Logger log = Logger.getLogger(OVProcessMsg.class.getName());

    public OVProcessMsg(String pgInput) {
        super(pgInput);
        initialize();
    }

    private void initialize() {

        String Concurrent_Processes = "-1";

        if (!getServiceoutput().equals("(null)")) {
            log.debug("Service output:: " + getServiceoutput());
            //"1297250072||Portfolio17||301 Check apache current-request status||APACHE OK:current_requests is 41||",
            String sop[] = getServiceoutput().split(" ");
            if (TokenParser.isValidToken(sop, 1)) {
                if (sop[1].contains("OK")) {
                    Concurrent_Processes = TokenParser.trimmedToken(sop, 3, "-1");
                }
            }
        } else {
            log.error("*** ERROR *** invalid messages received... cannot parse and make CEP msg...");
        }

        setCepMsg("stream=ConcurrentProcessesEvent," +
                "TimeStamp=" + getServicetime() + "," +
                "HostName=" + getHostname() + "," +
                "ResourceID=301," +
                "ConcurrentProcesses=" + Concurrent_Processes);

    }

    public String getCepMsg() {
        return super.getCepMsg();
    }


    public static void main(String[] args) {

       PropertyConfigurator.configure("config/logger.properties");

        String samples[] = {
            "1297250072||Portfolio8||301 Check apache current-request status||APACHE OK:current_requests is 57||",
            "1297255090||Portfolio6||301 Check apache current-request status",
            "1297255090||Portfolio6||301 Check apache current-request status||",
            "1297255090||Portfolio6||301 Check apache current-request status||||",
            "1297250072||Portfolio17||301 Check apache current-request status||CHECK_NRPE: Socket timeout after 10 seconds.",
            "1297250072||Portfolio17||301 Check apache current-request status||CHECK_NRPE: Socket timeout after 10 seconds.||",
            "1297250072||Portfolio17||301 Check apache current-request status||Connection refused||",
            "1297250072||Portfolio17||301 Check apache current-request status||Dependency failure||",
            "1297255090||Portfolio6||301 Check apache current-request status||APACHE OK:current_requests is 14||",
            "1297255123||Portfolio3||301 Check apache current-request status||APACHE OK:current_requests is ||",
            "1297260310||Portfolio6||301 Check apache current-request status||APACHE OK:current_requests  17||",
            "1297260310||Portfolio6||301 Check apache current-request status||APACHE OK:current_requests 17||",
            "1297260310||Portfolio6||301 Check apache current-request status||APACHE OK:current_requests17||",
            "1297260310||Portfolio6||301 Check apache current-request status||APACHE OK:current_requests ||"
        };
        for (int i = 0; i < samples.length; i++) {
            System.out.println("----- Test Case " + i + " ----------");
            System.out.println("Input Msg: " + samples[i]);
            OVProcessMsg ovp = new OVProcessMsg(samples[i]);
            System.out.println("Parsed CEP Msg: " + ovp.getCepMsg());
            System.out.println("----- End Test Case ----------\n");
        }

    }
}
