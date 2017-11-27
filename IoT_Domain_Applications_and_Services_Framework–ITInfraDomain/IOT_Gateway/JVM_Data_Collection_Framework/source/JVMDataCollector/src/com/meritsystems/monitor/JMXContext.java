package com.meritsystems.monitor;

import com.meritsystems.core.configuration.HostProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author shashi
 * Holds all parameters for an JMS
 */
public class JMXContext implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8562086633867893101L;
    private HostProperties hostProperties;
    private String appname;
    private List<MX> mx = new ArrayList<MX>();
    private Date dateOfCreation = null;
    private boolean isConnected = false;

    /**
     * @param url
     * @param title
     */
    public JMXContext(HostProperties hostProperties, String appname) {
        super();
        this.hostProperties = hostProperties;
        this.appname = appname;
        dateOfCreation = new Date();
    }

    /**
     * @return
     */
    public List<MX> getMx() {
        return mx;
    }

    /**
     * @param mx
     */
    public void setMx(List<MX> mx) {
        this.mx = mx;
    }

    /**
     * @return
     */
    public HostProperties getHostproHostProperties() {
        return hostProperties;
    }

    /**
     * @return
     */
    public String getAppname() {
        return appname;
    }

    /**
     * @return
     */
    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    /**
     * @param dateOfCreation
     */
    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    /**
     * @return
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * @param isConnected
     */
    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
