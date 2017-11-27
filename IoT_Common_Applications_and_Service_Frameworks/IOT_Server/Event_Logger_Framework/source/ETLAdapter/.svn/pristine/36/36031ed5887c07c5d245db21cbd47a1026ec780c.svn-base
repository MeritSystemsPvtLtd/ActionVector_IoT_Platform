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
public class OVSwapMsg extends OVMsg {

    private static final Logger log = Logger.getLogger(OVSwapMsg.class.getName());

    public OVSwapMsg(String pgInput) {
        super(pgInput);
        initialize();
    }

    private void initialize() {

        String Swap_Usage = "-1.1";
        String Max_Swaps = "-1.1";

        if (!getServiceoutput().equals("(null)")) {
            //"1297255115||Sadesk||106 Unix Swap||SWAP OK - 47% free (3691 MB out of 8001 MB)||swap=3691MB;400;80;0;8001",
            log.debug("Service output:: " + getServiceoutput());
            String sop[] = getServiceoutput().split(" ");
            if (TokenParser.isValidToken(sop, 1)) {
                if (sop[1].contains("OK")) {
                    //"Service_Output=SWAP OK - 97% free (1967 MB out of 2047 MB)|" +
                    Swap_Usage = TokenParser.replaceTrimToken(sop, 5, '(', ' ', "-1.1");
                    Max_Swaps = TokenParser.replaceTrimToken(sop, 9, '(', ' ', "-1.1");
                }
            }
        } else {
            log.error("*** ERROR *** invalid messages received... cannot parse and make CEP msg...");
        }

        setCepMsg("stream=SwapStatsEvent,"
                + "TimeStamp=" + getServicetime() + ","
                + "HostName=" + getHostname() + ","
                + "ResourceID=106,"
                + "SwapUsage=" + Swap_Usage + ","
                + "MaxSwaps=" + Max_Swaps);
    }

    public String getCepMsg() {
        return super.getCepMsg();
    }

    public static void main(String[] args) {

        PropertyConfigurator.configure("config/logger.properties");

        String samples[] = {
            "1297255012||Bsefeed||106 Unix Swap||SWAP OK - 100% free (4000 MB out of 4000 MB)||swap=4000MB;200;40;0;4000",
            "1297255012||Bsefeed||106 Unix Swap",
            "1297255012||Bsefeed||106 Unix Swap||",
            "1297255012||Bsefeed||106 Unix Swap||||",
            "1297255115||Sadesk||106 Unix Swap||||swap=3691MB;400;80;0;8001",
            "1297255115||Sadesk||106 Unix Swap||CHECK_NRPE: Socket timeout after 10 seconds.||swap=3691MB;400;80;0;8001",
            "1297255115||Sadesk||106 Unix Swap||Connection refused||swap=3691MB;400;80;0;8001",
            "1297255115||Sadesk||106 Unix Swap||Dependency failure||swap=3691MB;400;80;0;8001",
            /** ignorable error inputs - program should not exit with exception **/
            "1297255012||Bsefeed||106 Unix Swap||SWAP OK - 100% free (    out of 4000 MB)||swap=4000MB;200;40;0;4000",
            "1297255012||Bsefeed||106 Unix Swap||SWAP OK - 100% free (   MB out of 4000 MB)||swap=4000MB;200;40;0;4000",
            "1297255012||Bsefeed||106 Unix Swap||SWAP OK - 100% free ( 3691   out of 4000 MB)||swap=4000MB;200;40;0;4000",
            "1297255115||Sadesk||106 Unix Swap||SWAP OK - 47% free (3691 MB out of  )||swap=3691MB;400;80;0;8001",
            "1297255115||Sadesk||106 Unix Swap||SWAP OK - 47% free (3691 MB out of   MB )||swap=3691MB;400;80;0;8001",
            "1297255115||Sadesk||106 Unix Swap||SWAP OK - 47% free (3691 MB out of  4000 )||swap=3691MB;400;80;0;8001",
            /** end of ignorable error inputs **/
            "1297260306||Portfolio17||106 Unix Swap||SWAP OK - 100% free (8189 MB out of 8189 MB)||swap=8189MB;409;81;0;8189"
        };


        for (int i = 0; i < samples.length; i++) {
            System.out.println("----- Test Case " + i + " ----------");
            System.out.println("Input Msg: " + samples[i]);
            OVSwapMsg ovsm = new OVSwapMsg(samples[i]);
            System.out.println("Parsed CEP Msg: " + ovsm.getCepMsg());
            System.out.println("----- End Test Case ----------\n");
        }

    }
}
