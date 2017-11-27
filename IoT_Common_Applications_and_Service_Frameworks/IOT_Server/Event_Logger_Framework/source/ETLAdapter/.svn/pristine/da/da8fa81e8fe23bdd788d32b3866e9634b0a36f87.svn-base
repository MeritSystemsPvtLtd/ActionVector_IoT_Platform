package com.prokosha.adapter.etl.message;

import com.prokosha.adapter.etl.ETLAdapter.TokenParser;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**** TODO
 * 1. Assuming all memory units are in MB. IF KB or GB they must get converted into MBs first.
 */
/**
 *
 * @author Bimal
 */
public class OVDiskMsg extends OVMsg {

    private static final Logger log = Logger.getLogger(OVDiskMsg.class.getName());

    public OVDiskMsg(String pgInput) {

        super(pgInput);
        initialize(pgInput);
    }

    private void initialize(String pgInput) {

        //parse following tokens for plugin specific information
        /* Token format:
         *      1297255106||Moneyalert2||/var||DISK OK - free space: /var 25833 MB (79% inode=98%):||/var=6640MB;32500;33526;0;34211
         */

        ////// FIX - thios should work on the performance data token and not on pgInput
        String bootUsg = "-1.1";
        String varUsg = "-1.1";
        String homeUsg = "-1.1";
        String usrUsg = "-1.1";
        String rootUsg = "-1.1";
        if (!getServiceoutput().equals("(null)")) {
            String Usage = "-1.1";
            String sop[] = getServiceoutput().split(" ");
            /*if (TokenParser.isValidToken(sop, 8)) {
                sop[8] = TokenParser.replaceTrimToken(sop, 8, '(', ' ', "-1.1");
                sop[8] = TokenParser.replaceTrimToken(sop, 8, '%', ' ', "-1.1");
                if (!(sop[8].equals(Usage))) {
                    Usage = "" + (100 - Integer.parseInt(sop[8]));
                }
            }*/
            if (TokenParser.isValidToken(sop, 8)) {
                sop[8] = TokenParser.replaceTrimToken(sop, 8, '(', ' ', "-1.1");
                String subsop[] = sop[8].split("%");
                if (!(subsop[0].equals(Usage))) {
                    //System.out.println("Parse disk==="+subsop[0]);
                    Usage = "" + (100 - Integer.parseInt(subsop[0]));
                }
            }

            if (!getServicename().equals("(null)")) {
                String[] token = getServicename().replace("\n", " ").trim().split("([|]{2}+)");
                if (token[0].contains("100")) {
                    varUsg = Usage;
                } else if (token[0].contains("102")) {
                    homeUsg = Usage;
                } else if (token[0].contains("103")) {
                    usrUsg = Usage;
                } else if (token[0].contains("101")) {
                    bootUsg = Usage;
                } else if (token[0].contains("114")) {
                    rootUsg = Usage;
                }
            }
        } else {
            log.error("*** ERROR *** invalid messages received... cannot parse and make CEP msg...");
            //log CRITICAL error to the logfile
        }

        System.out.println("disk," + getHostname() + "," + getServicetime()
              + "," + bootUsg + "," + varUsg + "," + homeUsg + "," + usrUsg + "," + rootUsg);
//commented afterCustomerID      diskInsert(getHostname(), Long.parseLong(getServicetime()),Double.parseDouble(bootUsg),Double.parseDouble(varUsg),Double.parseDouble(homeUsg),Double.parseDouble(usrUsg),Double.parseDouble(rootUsg));
    }

    public String getCepMsg() {
        return super.getCepMsg();
    }

    /*public String getReportingMsg() {
        return super.getReportingMsg();
    }*/

    public static void main(String[] args) {

        PropertyConfigurator.configure("config/logger.properties");

        String samples[] = {
            "1297255106||Moneyalert2||100 /var||DISK OK - free space: /var 25833 MB (79% inode=98%):||/var=6640MB;32500;33526;0;34211",
            "1297255106||Moneyalert2||100 /var||",
            "1297255106||Moneyalert2||100 /var||DISK OK - free space: /var 25833 MB (",
            "1297255106||Moneyalert2||101 /||DISK OK - free space: / 25833 MB (79% inode=98%):||/var=6640MB;32500;33526;0;34211",
            "1297255106||Moneyalert2||100 /var||",
            "1297255106||Moneyalert2",
            "1297255106||Moneyalert2||102 /var||DISK OK - free space: /home 25833 MB 79% inode=98%:||/home=6640MB;32500;33526;0;34211",
            "1309507451||Moneysearch1||102 /home||DISK OK - free space: /home 2519 MB (26% inode=87%):||/home=6824MB;9351;9647;0;9844",
            "1309507524||Moneysearch||114 /||DISK OK - free space: / 1721 MB (92%inode=97%):||/=147MB;1869;1928;0;1968",
            "1309507451||opsview||114 /||DISK OK - free space: / 12955 MB (91% inode=99%):||/=1154MB;14134;14580;0;14878"};

        for (int i = 0; i < samples.length; i++) {
            System.out.println("----- Test Case " + i + " ----------");
            System.out.println("Input Msg: " + samples[i]);
            OVDiskMsg ovm = new OVDiskMsg(samples[i]);
            System.out.println("Parsed CEP Msg: " + ovm.getCepMsg());
            //System.out.println("Parsed Reporting Msg: " + ovm.getReportingMsg());
            System.out.println("----- End Test Case ----------\n");
        }

    }
}

