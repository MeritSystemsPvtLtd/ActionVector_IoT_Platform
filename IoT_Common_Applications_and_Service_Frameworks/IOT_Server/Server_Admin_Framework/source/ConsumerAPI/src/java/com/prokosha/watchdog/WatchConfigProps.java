/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.prokosha.watchdog;
import java.io.*;
import org.apache.log4j.Logger;

/**
 *
 * @author gopal
 */
public class WatchConfigProps implements Serializable{
    private static final Logger log = Logger.getLogger(WatchConfigProps.class.getName());
    protected String resourceName=null;
    protected String resourceId=null;
    protected String resourceType=null;
    protected String processName=null;
    protected String instanceId=null;
    protected String instancePort=null;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstancePort() {
        return instancePort;
    }

    public void setInstancePort(String instancePort) {
        this.instancePort = instancePort;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
    

}
