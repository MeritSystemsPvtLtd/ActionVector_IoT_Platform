package com.prokosha.adapter.etl.message;

import com.prokosha.adapter.etl.ETLAdapter.TokenParser;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/***
 * TODO : 1. Handle the cases where the elapsed time is not specified??
 */
/**
 *
 * @author Bimal
 */
public class OVMemcacheMsg extends OVMsg {

    private static final Logger log = Logger.getLogger(OVMemcacheMsg.class.getName());

    public OVMemcacheMsg(String pgInput) {
        super(pgInput);
        initialize();
    }

    private void initialize() {

        String Memcache_Server = "(null)";
        String Elapsed_Time = "-1.1";
        String Memcache_Port = "(null)";
        String ResourceID = "(null)";

        if (getServicename().contains("151")) {
            ResourceID = "151";
        } else if (getServicename().contains("152")) {
            ResourceID = "152";
        } else if (getServicename().contains("153")) {
            ResourceID = "153";
        } else if (getServicename().contains("225")) {
            ResourceID = "225";
        } else if (getServicename().contains("226")) {
            ResourceID = "226";
        } else if (getServicename().contains("227")) {
            ResourceID = "227";
        }


        log.debug("Service perfdata:: " + getServiceperfdata());
        if (!getServiceperfdata().equals("(null)")) {
            String spd[] = getServiceperfdata().split(",");

            //server=10.50.11.18:11211, elapsedtime=0.000803, key=17023928, objval=Sensex000000009 Feb,16:....

            if (TokenParser.isValidToken(spd, 0)) {
                String memsp[] = spd[0].split("=");
                if (TokenParser.isValidToken(memsp, 1)) {
                    memsp = memsp[1].split(":");
                    Memcache_Server = TokenParser.trimmedToken(memsp, 0, "(null)");
                    log.debug("Memcache server:: " + Memcache_Server);
                    Memcache_Port = TokenParser.trimmedToken(memsp, 1, "(null)");
                    log.debug("Memcache port:: " + Memcache_Port);
                }
            }

            //server=10.50.11.18:11211, elapsedtime=0.000803, key=17023928, objval=Sensex000000009 Feb,16:....
            if (TokenParser.isValidToken(spd, 1)) {
                String etime[] = spd[1].split("=");
                Elapsed_Time = TokenParser.trimmedToken(etime, 1, "-1.1");
                log.debug("Elapsed time:: " + Elapsed_Time);
            }
        } else {
            log.error("*** ERROR *** invalid messages received... cannot parse and make CEP msg...");
        }

       /* setCepMsg("stream=MemcacheReadLatencyEvent," +
                "TimeStamp=" + getServicetime() + "," +
                "HostName=" + getHostname() + "," +
                "ResourceID=" + ResourceID + "," +
                "ElapsedTime=" + Elapsed_Time + "," +
                "MemcacheServer=" + Memcache_Server + "," +
                "MemcachePort=" + Memcache_Port);*/
//commented afterCustomerID    memCacheInsert(getHostname(),Long.parseLong(getServicetime()),Double.parseDouble(Elapsed_Time));

    }

    public String getCepMsg() {
        return super.getCepMsg();
    }

    public static void main(String[] args) {
        //System.out.println("inside main");
        PropertyConfigurator.configure("config/logger.properties");

        String samples[] = {
            "1297255061||Bsefeed||151 Bse Memcache1||OK - object exists in memcached on 10.50.11.18:||server=10.50.11.18:11211, elapsedtime=0.000999, key=17023928, objval=Sensex000000009 Feb,16:04:1117592.770.017775.71702392821206.7712316.117592.770000",
            "1297255061||Bsefeed||151 Bse Memcache1||",
            "1297255061||Bsefeed||151 Bse Memcache1||CHECK_NRPE: Socket timeout after 10 seconds.",
            "1297255061||Bsefeed||151 Bse Memcache1||CHECK_NRPE: Socket timeout after 10 seconds.||",
            "1297255061||Bsefeed||151 Bse Memcache1||Connection refused",
            "1297255061||Bsefeed||151 Bse Memcache1||Dependency failure",
            "1297255061||Bsefeed||153 Bse Memcache3||OK - object exists in memcached on 10.50.11.18:||, , key=17023928, objval=Sensex000000009 Feb,16:04:1117592.770.017775.71702392821206.7712316.117592.770000",
            "1297255107||Bsefeed||152 Bse Memcache2||OK - object exists in memcached on 10.50.31.1:||server, elapsedtime=0.002241, key=17023928, objval=Sensex000000009 Feb,16:04:1117592.770.017775.71702392821206.7712316.117592.770000",
            "1297260207||Bsefeed||152 Bse Memcache2||OK - object exists in memcached on 10.50.31.1:||server=, elapsedtime=0.000966, key=17023928, objval=Sensex000000009 Feb,16:04:1117592.770.017775.71702392821206.7712316.117592.770000",
            "1297260207||Bsefeed||152 Bse Memcache2||OK - object exists in memcached on 10.50.31.1:||server= : , elapsedtime=0.000966, key=17023928, objval=Sensex000000009 Feb,16:04:1117592.770.017775.71702392821206.7712316.117592.770000",
            "1297260207||Bsefeed||152 Bse Memcache2||OK - object exists in memcached on 10.50.31.1:||server=10.50.31.1, elapsedtime=0.000966, key=17023928, objval=Sensex000000009 Feb,16:04:1117592.770.017775.71702392821206.7712316.117592.770000",
            "1297260207||Bsefeed||152 Bse Memcache2||OK - object exists in memcached on 10.50.31.1:||server=10.50.31.1:, elapsedtime=0.000966, key=17023928, objval=Sensex000000009 Feb,16:04:1117592.770.017775.71702392821206.7712316.117592.770000",
            "1297260207||Bsefeed||152 Bse Memcache2||OK - object exists in memcached on 10.50.31.1:||server= 10.50.31.1:, elapsedtime=0.000966, key=17023928, objval=Sensex000000009 Feb,16:04:1117592.770.017775.71702392821206.7712316.117592.770000",
            "1297260207||Bsefeed||152 Bse Memcache2||OK - object exists in memcached on 10.50.31.1:||server=10.50.31.1: 11212, elapsedtime=0.000966, key=17023928, objval=Sensex000000009 Feb,16:04:1117592.770.017775.71702392821206.7712316.117592.770000",
            "1297260207||Bsefeed||152 Bse Memcache2||OK - object exists in memcached on 10.50.31.1:||server=10.50.31.1:11212, elapsedtime=  0.000966, key=17023928, objval=Sensex000000009 Feb,16:04:1117592.770.017775.71702392821206.7712316.117592.770000",
            "1297260207||Bsefeed||152 Bse Memcache2||OK - object exists in memcached on 10.50.31.1:||server=10.50.31.1:11212, , key=17023928, objval=Sensex000000009 Feb,16:04:1117592.770.017775.71702392821206.7712316.117592.770000",
            "1297260207||Bsefeed||152 Bse Memcache2||OK - object exists in memcached on 10.50.31.1:||server=10.50.31.1:11212, elapsedtime, key=17023928, objval=Sensex000000009 Feb,16:04:1117592.770.017775.71702392821206.7712316.117592.770000",
            "1297260207||Bsefeed||152 Bse Memcache2||OK - object exists in memcached on 10.50.31.1:||server=10.50.31.1:11212, elapsedtime= , key=17023928, objval=Sensex000000009 Feb,16:04:1117592.770.017775.71702392821206.7712316.117592.770000",
            "1297260215||Portfolio17||152 Bse Memcache2||OK - object exists in memcached on 10.50.31.1:||server=10.50.31.1:11212, elapsedtime=, key=17023928, objval=Sensex000000009 Feb,16:04:1117592.770.017775.71702392821206.7712316.117592.770000"
        };

        for (int i = 0; i < samples.length; i++) {
            System.out.println("----- Test Case " + i + " ----------");
            System.out.println("Input Msg: " + samples[i]);
            OVMemcacheMsg ovmc = new OVMemcacheMsg(samples[i]);
            System.out.println("Parsed CEP Msg: " + ovmc.getCepMsg());
            System.out.println("----- End Test Case ----------\n");
        }
    }
}
