package com.meritsystems.monitor.mx;

import java.util.Date;
import java.util.List;

import com.meritsystems.core.MetricsEnum;
import com.meritsystems.core.configuration.ConfigurationReader;
import com.meritsystems.monitor.MX;

/**
 * @author shashi
 *
 */
public class AppMX extends MX {

    private boolean isAlive = false;
    private String hostname;
    private String clustername;
    private Date time;
    private String jmxurl;
    private String hostipaddress;
    /**
     *
     */
    private static final long serialVersionUID = 5492502919843169126L;

    public AppMX() {
        super(APPMXBEAN, null, MetricsEnum.APP_MXBEAN);
    }

    @Override
    public List<String> getSubmetricsConfiguration() {
        return ConfigurationReader.getConfiguration().getAppSubmetrics();
    }

    /**
     * @return
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * @param isAlive
     */
    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    /**
     * @return
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @param hostname
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * @return
     */
    public String getClustername() {
        return clustername;
    }

    /**
     * @param clustername
     */
    public void setClustername(String clustername) {
        this.clustername = clustername;
    }

    /**
     * @return
     */
    public Date getTime() {
        return time;
    }

    /**
     * @param time
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * @return
     */
    public String getJmxurl() {
        return jmxurl;
    }

    /**
     * @param jmxurl
     */
    public void setJmxurl(String jmxurl) {
        this.jmxurl = jmxurl;
    }

    /**
     * @return
     */
    public String getHostipaddress() {
        return hostipaddress;
    }

    /**
     * @param hostipaddress
     */
    public void setHostipaddress(String hostipaddress) {
        this.hostipaddress = hostipaddress;
    }
}
