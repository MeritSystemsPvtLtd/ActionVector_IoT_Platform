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
public class OVMemoryMsg extends OVMsg {

    private static final Logger log = Logger.getLogger(OVMemoryMsg.class.getName());

    public OVMemoryMsg(String pgInput) {
        super(pgInput);
        initialize();
    }

    private void initialize() {

        String Real_Usage = "-1.1";
        String Memory_Used = "-1.1";
        String Memory_Max = "-1.1";
        String Buffer = "-1.1";
        String Cache = "-1.1";
        String Swap = "-1.1";
        String Swap_Used = "-1.1";
        String Swap_Max = "-1.1";
        String Utilization = "-1.1";

        String sopToken = getServiceoutput();

        if ((!sopToken.equals("(null)")) &&
                (sopToken.contains("real") && sopToken.contains("buffer") && sopToken.contains("cache") && sopToken.contains("swap")))
        {
            /*
             * Service ouput format
             * "Usage: real 27% (1060/3950 MB), buffer: 456 MB, cache: 1761 MB, swap: 0% (0/8189 MB)||utilisation=27"
             */
            log.debug("Service ouput: " + sopToken);
            String sop[] = sopToken.trim().split(" ");
            Real_Usage = TokenParser.replaceTrimToken(sop, 2, '%', ' ', "-1.1");
            log.debug("Real memory usage: " + Real_Usage);

            if (TokenParser.isValidToken(sop, 3)) {
                String memory1[] = sop[3].split("/"); //(663/947
                Memory_Used = TokenParser.replaceTrimToken(memory1, 0, '(', ' ', "-1.1");
                Memory_Max = TokenParser.trimmedToken(memory1, 1, "-1.1");
                log.debug("Memory usage: " + Memory_Used + " Memory max available: " + Memory_Max);
            }


            Buffer = TokenParser.trimmedToken(sop, 6, "-1.1");
            log.debug("Buffer memory: " + Buffer);

            Cache = TokenParser.trimmedToken(sop, 9, "-1.1");
            log.debug("Cache memory: " + Cache);

            Swap = TokenParser.replaceTrimToken(sop, 12, '%', ' ', "-1.1");
            log.debug("Swap memory utilisation: " + Swap);


            if (TokenParser.isValidToken(sop, 13)) {
               String memory1[] = sop[13].split("/"); //(80/2047
               Swap_Used = TokenParser.replaceTrimToken(memory1, 0, '(', ' ', "-1.1");
               Swap_Max = TokenParser.trimmedToken(memory1, 1, "-1.1");
               log.debug("Swap memory used: " + Swap_Used + " Swap maximum: " + Swap_Max);
            }
        } else {
            log.error("*** ERROR *** invalid message received... cannot parse and make CEP msg...");
        }

        if (!getServiceperfdata().equals("(null)")) {
            //Format: utilisation=70
            String spd[] = getServiceperfdata().split("=");

            Utilization = TokenParser.trimmedToken(spd, 1, "-1.1");
            log.debug("Utilisation: " + Utilization);
        } else {
            log.error("*** ERROR *** invalid message received... cannot parse and make CEP msg...");
        }

        /*setCepMsg("stream=MemoryStatsEvent," +
                "TimeStamp=" + getServicetime() + "," +
                "HostName=" +  getHostname() + "," +
                "ResourceID=110," +
                "RealUsage=" + Real_Usage + "," +
                "MemUsed=" + Memory_Used + "," +
                "MemMax=" + Memory_Max + "," +
                "Cache=" + Cache + "," +
                "Swap=" + Swap + "," +
                "SwapUsed=" + Swap_Used + "," +
                "SwapMax=" + Swap_Max + "," +
                "Buffer=" + Buffer + "," +
                "Utilization=" + Utilization);*/
        if(Double.parseDouble(Memory_Used)!=-1.1){


//commented afterCustomerID    memInsert(getHostname(), Long.parseLong(getServicetime()), Double.parseDouble(Memory_Used), Double.parseDouble(Memory_Max));

    }
    }

    public String getCepMsg() {
        return super.getCepMsg();
    }

    public static void main(String[] args) {

        PropertyConfigurator.configure("config/logger.properties");

        String samples[] = {
            "1297250077||Portfolio15||110 Unix Memory||Usage: real 27% (1060/3950 MB), buffer: 456 MB, cache: 1761 MB, swap: 0% (0/8189 MB)||utilisation=27",
            "1297250077||Portfolio15||110 Unix Memory",
            "1297250077||Portfolio15||110 Unix Memory||",
            "1297250077||Portfolio15||110 Unix Memory||||",
            "1297250077||Portfolio15||110 Unix Memory||Dependency failure||",
            "1297250077||Portfolio15||110 Unix Memory||Connection refused||",
            "1297250077||Portfolio15||110 Unix Memory||CHECK_NRPE: Socket timeout after 10 seconds.",
            "1297250077||Portfolio15||110 Unix Memory||CHECK_NRPE: Socket timeout after 10 seconds.||",
            "1297255012||money7||110 Unix Memory||Usage: real  % (1074/3950 MB), buffer: 288 MB, cache: 570 MB, swap: 0% (0/8189 MB)||utilisation=27",
            "1297255143||Portfolio8||110 Unix Memory||Usage: real 23% (/3950 MB), buffer: 405 MB, cache: 1780 MB, swap: 0% (0/8189 MB)||utilisation=23",
            "1297255143||MoneyMemcache-1||110 Unix Memory||Usage: real 30% (  /3992 MB), buffer: 123 MB, cache: 2662 MB, swap: 0% (0/4000 MB)||utilisation=30",
            "1297255143||Portfolio8||110 Unix Memory||Usage: real 23% (914/ MB), buffer: 405 MB, cache: 1780 MB, swap: 0% (0/8189 MB)||utilisation=23",
            "1297255143||MoneyMemcache-1||110 Unix Memory||Usage: real 30% (1181/   MB), buffer: 123 MB, cache: 2662 MB, swap: 0% (0/4000 MB)||utilisation=30",
            "1297255143||Nsefeed||110 Unix Memory||Usage: real 18% (704/3992 MB), buffer:  MB, cache: 2881 MB, swap: 0% (0/4001 MB)||utilisation=18",
            "1297255143||Moneyalert2||110 Unix Memory||Usage: real 55% (1121/2026 MB), buffer: , cache: 763 MB, swap: 4% (152/3848 MB)||utilisation",
            "1297255143||Moneyalert2||110 Unix Memory||Usage: real 55% (1121/2026 MB), buffer: 77 MB, cache:  MB, swap: 4% (152/3848 MB)||utilisation=",
            "1297255143||Moneyalert2||110 Unix Memory||Usage: real 55% (1121/2026 MB), buffer: 77 MB, cache:, swap: 4% (152/3848 MB)",
            "1297255143||Moneyalert2||110 Unix Memory||Usage: real 55% (1121/2026 MB), buffer: 77 MB, cache: 763 MB, swap:   (152/3848 MB)||",
            "1297255143||Moneyalert2||110 Unix Memory||Usage: real 55% (1121/2026 MB), buffer: 77 MB, cache: 763 MB, swap: % (152/3848 MB)||",
            "1297255143||Moneyalert2||110 Unix Memory||Usage: real 55% (1121/2026 MB), buffer: 77 MB, cache: 763 MB, swap: (152/3848 MB)||",
            "1297255143||Moneyalert2||110 Unix Memory||Usage: real 55% (1121/2026 MB), buffer: 77 MB, cache: 763 MB, swap: 4% ( 152/)||",
            "1297255143||Moneyalert2||110 Unix Memory||Usage: real 55% (1121/2026 MB), buffer: 77 MB, cache: 763 MB, swap: 4% ( 152/ MB)||",
            "1297255143||Moneyalert2||110 Unix Memory||Usage: real 55% (1121/2026 MB), buffer: 77 MB, cache: 763 MB, swap: 4% ( 152/MB)||",
            "1297255143||Moneyalert2||110 Unix Memory||Usage: real  (  /   MB), buffer:   MB, cache:   MB, swap:   % (  /  MB)||utilisation=  ",
            "1297260300||mysqldb||110 Unix Memory||Usage: real 8% (332/4050 MB), buffer: 3 MB, cache: 3699 MB, swap: 4% (322/7680 MB)||utilisation=8"
        };

        for (int i = 0; i < samples.length; i++) {
            System.out.println("----- Test Case " + i + " ----------");
            System.out.println("Input Msg: " + samples[i]);
            OVMemoryMsg ovm = new OVMemoryMsg(samples[i]);
            System.out.println("Parsed CEP Msg: " + ovm.getCepMsg());
            System.out.println("----- End Test Case ----------\n");
        }

    }
}
