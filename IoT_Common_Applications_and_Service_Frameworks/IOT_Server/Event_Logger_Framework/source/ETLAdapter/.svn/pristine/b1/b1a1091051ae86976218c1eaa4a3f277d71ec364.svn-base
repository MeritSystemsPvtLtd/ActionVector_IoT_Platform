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

public class OVLoadAvgMsg extends OVMsg {

    private static final Logger log = Logger.getLogger(OVLoadAvgMsg.class.getName());

    public OVLoadAvgMsg (String pgInput) {

        super(pgInput);
        initialize();
    }

    private void initialize() {
        
        String Load1="-1.1";
        String Load5="-1.1";
        String Load15="-1.1";
        if (!getServiceperfdata().equals("(null)"))
        { /*0,4,8*/      
            log.debug("Service perfdata:: " + getServiceperfdata());
            String spd[] = getServiceperfdata().split(";");

            //Load1
            String tok[];
            if (TokenParser.isValidToken(spd, 0)) {
                tok = spd[0].split("=");
                Load1 = TokenParser.trimmedToken(tok, 1, "-1.1");
                log.debug("Perfdata:: Load1:: " + Load1);
            }
            if (TokenParser.isValidToken(spd, 4)) {
                tok = spd[4].split("=");
                Load5 = TokenParser.trimmedToken(tok, 1, "-1.1");
                log.debug("Perfdata:: Load5:: " + Load5);
            }
            if (TokenParser.isValidToken(spd, 8)) {
                tok = spd[8].split("=");
                Load15 = TokenParser.trimmedToken(tok, 1, "-1.1");
                log.debug("Perfdata:: Load15:: " + Load15);
            }
        } else {
            log.error("*** ERROR *** invalid messages received... cannot parse and make CEP msg...");
        }

       /* setCepMsg("stream=LoadAverageEvent," +
                "TimeStamp=" + getServicetime() + "," +
                "HostName=" + getHostname() + "," +
                "ResourceID=108," +
                "Load1=" + Load1 + "," +
                "Load5=" + Load5 + "," +
                "Load15=" + Load15);*/
//commented afterCustomerID     cpuInsert(getHostname(), Long.parseLong(getServicetime()), Double.parseDouble(Load1),Double.parseDouble(Load5),Double.parseDouble(Load15));


    }

    public String getCepMsg() {
        return super.getCepMsg();
    }


     public static void main(String[] args) {

       PropertyConfigurator.configure("config/logger.properties");

        String samples[] = {
        "1297250073||Portfolio15||108 Unix Load Average||OK - load average: , 0.06, 0.10||load1=0.070;20.000;30.000;0; load5=0.060;50.000;60.000;0; load15=0.100;50.000;60.000;0;",
        "1298389682||Bsefeed||108 Unix Load Average",
        "1298389682||Bsefeed||108 Unix Load Average||",
        "1298389682||Bsefeed||108 Unix Load Average||||",
        "1298389682||Bsefeed||108 Unix Load Average||Dependency failure||",
        "1298389682||Bsefeed||108 Unix Load Average||Connection refused||",
        "1298389682||Bsefeed||108 Unix Load Average||CHECK_NRPE: Socket timeout after 10 seconds.",
        "1298389682||Bsefeed||108 Unix Load Average||CHECK_NRPE: Socket timeout after 10 seconds.||",
	"1297250075||Portfolio6||108 Unix Load Average||OK - load average: 0.39, 0.31, 0.22||;20.000;30.000;0; load5=0.310;50.000;60.000;0; load15=0.220;50.000;60.000;0;",
	"1297255061||Portfolio5||108 Unix Load Average||OK - load average: 0.30, 0.35, 0.21||load1;20.000;30.000;0; load5=0.350;50.000;60.000;0; load15=0.210;50.000;60.000;0;",
	"1297255062||Portfolio17||108 Unix Load Average||OK - load average: 0.09, 0.10, 0.09||load1=;20.000;30.000;0; load5=0.100;50.000;60.000;0; load15=0.090;50.000;60.000;0;",
	"1297255062||Portfolio17||108 Unix Load Average||OK - load average: 0.09, 0.10, 0.09||load1=  ;20.000;30.000;0; load5=0.100;50.000;60.000;0; load15=0.090;50.000;60.000;0;",
	"1297255062||Portfolio17||108 Unix Load Average||OK - load average: 0.09, 0.10, 0.09||load1= 0.750 ;20.000;30.000;0; load5=0.100;50.000;60.000;0; load15=0.090;50.000;60.000;0;",
	"1297255065||money8||108 Unix Load Average||OK - load average: 0.75, 0.46, 0.44||load1=0.750;20.000;30.000;0; ;50.000;60.000;0; load15=0.440;50.000;60.000;0;",
        "1297250073||Portfolio15||108 Unix Load Average||OK - load average: , 0.06, 0.10||load1=0.070;20.000;30.000;0; load5;50.000;60.000;0; load15=0.100;50.000;60.000;0;",
        "1297250073||Portfolio15||108 Unix Load Average||OK - load average: , 0.06, 0.10||load1=0.070;20.000;30.000;0; load5=;50.000;60.000;0; load15=0.100;50.000;60.000;0;",
        "1297250073||Portfolio15||108 Unix Load Average||OK - load average: , 0.06, 0.10||load1=0.070;20.000;30.000;0; load5=0.060;50.000;",
        "1297250073||Portfolio15||108 Unix Load Average||OK - load average: , 0.06, 0.10||load1=0.070;20.000;30.000;0; load5=0.060;50.000;60.000;0; ;50.000;60.000;0;",
        "1297250073||Portfolio15||108 Unix Load Average||OK - load average: , 0.06, 0.10||load1=0.070;20.000;30.000;0; load5=0.060;50.000;60.000;0; load15;50.000;60.000;0;",
        "1297250073||Portfolio15||108 Unix Load Average||OK - load average: , 0.06, 0.10||load1=0.070;20.000;30.000;0; load5=0.060;50.000;60.000;0; load15=;50.000;60.000;0;",
	"1297260313||Moneyalert1||108 Unix Load Average||OK - load average: 0.04, 0.05, 0.09||load1=0.040;20.000;30.000;0; load5=0.050;50.000;60.000;0; load15=0.090;50.000;60.000;0;"
       };
        
        for (int i=0; i<samples.length; i++) {
            System.out.println("----- Test Case " + i + " ----------");
            System.out.println("Input Msg: " + samples[i]);
            OVLoadAvgMsg ovlam = new OVLoadAvgMsg(samples[i]);
            System.out.println("Parsed CEP Msg: " + ovlam.getCepMsg());
            System.out.println("----- End Test Case ----------\n");
        }
     }
}
