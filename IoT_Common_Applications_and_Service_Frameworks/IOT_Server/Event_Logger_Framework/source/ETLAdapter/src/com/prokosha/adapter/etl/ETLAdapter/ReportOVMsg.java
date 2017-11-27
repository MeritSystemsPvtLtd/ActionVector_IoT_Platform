/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.adapter.etl.ETLAdapter;

import java.util.StringTokenizer;

/**
 *
 * @author Abraham
 */
public class ReportOVMsg {

    private String inputMsg;
    private String servicename; //depends on plugin
    private String hostname; //independent of plugin; sample - Host_Name=opsview
    private String serviceoutput;//depends on plugin
    private String serviceperfdata;//depends on plugin
    private String servicetime;//independent of plugin' sample - Service_Time=08-02-2011 16:09:34
    private String cepMsg = "";
    private String reportingMsg = "";
    private String eventtype = null;

    public String getEventtype() {
        return eventtype;
    }

    public ReportOVMsg(String inputMsg) {
        this.inputMsg = inputMsg;

        servicetime = "(null)";
        hostname = "(null)";
        servicename = "(null)";
        serviceoutput = "(null)";
        serviceperfdata = "(null)";

        //parse the input string - the derived classes will do the actual processing of the messages
        //Order of tokens is $LASTSERVICECHECK$||$HOSTNAME$||$SERVICEDESC$||$SERVICEOUTPUT$||$SERVICEPERFDATA$
        String token[];
        if (this.inputMsg != null) {
            //first strip the record terminator (newline character)
            //inputMsg.replace("\n", " ").trim();
            token = inputMsg.replace("\n", " ").trim().split("([|]{2}+)");
            if (token.length > 0) { //because the last token might not even be there
                servicetime = (((token[0] == null) || (token[0].length() == 0)) ? "(null)" : token[0]);

            }
            if (token.length > 1) { //because the last token might not even be there
                hostname = (((token[1] == null) || (token[1].length() == 0)) ? "(null)" : token[1]);

            }
            if (token.length > 2) { //because the last token might not even be there
                servicename = (((token[2] == null) || (token[2].length() == 0)) ? "(null)" : token[2]);

            }
            if (token.length > 3) { //because the last token might not even be there
                serviceoutput = (((token[3] == null) || (token[3].length() == 0)) ? "(null)" : token[3]);

            }
            if (token.length > 4) { //because the last token might not even be there
                serviceperfdata = (((token[4] == null) || (token[4].length() == 0)) ? "(null)" : token[4]);

            }
        }
        setEventType();
    }

    private void setEventType() {
        String tokentype[] = servicename.split(" ");
        eventtype = tokentype[0];
    }

    //string- value pair
    private String extractTokenVal(String token) {
        String val[] = token.split("=", 2);
        if (val.length > 1) {
            return val[1];
        } else {
            return "(null)";
        }
    }

    public String getServicename() {
        return servicename;
    }

    public String getHostname() {
        return hostname;
    } //independent of plugin; sample - Hostgroup_names=Monitoring Servers

    public String getServiceoutput() {
        return serviceoutput;
    }//depends on plugin

    public String getServiceperfdata() {
        return serviceperfdata;
    }//depends on plugin

    public String getServicetime() {
        return servicetime;
    }//independent of plugin' sample - Service_Time=08-02-2011 16:09:34

    public void setCepMsg(String msg) {
        cepMsg = msg;
    }

    public void setReportingMsg(String msg) {
        reportingMsg = msg;
    }

    public String getCepMsg() {
        return cepMsg;
    }

    public String getReportingMsg() {
        return reportingMsg;
    }
}
