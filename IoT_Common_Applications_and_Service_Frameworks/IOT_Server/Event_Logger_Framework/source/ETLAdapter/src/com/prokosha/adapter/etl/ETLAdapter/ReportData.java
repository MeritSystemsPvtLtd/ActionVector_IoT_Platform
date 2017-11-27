/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.adapter.etl.ETLAdapter;

import java.lang.Exception;
import java.lang.reflect.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Anand
 */
public class ReportData {

    private String host = null;
    private String hostGroup = null;
    private String service = null;
    private String subService = null;
    private String alertName = null;
    private String metricType = null;
    private String category = null;
    private String timestamp1 = null;//timestamp1
    private String startTime = null;
    private String endTime = null;
    private String status = null;
    private String size = null;
    private String eventID = null;
    private String resourceType = null;
    private String resourceSubType = null;
    private String resourceId = null;
    private String availabilty = null;
    private String value = null;
    private String SLAvalue = null;
    private int cCustomerID = -1;

    public String getSLAvalue() {
        return SLAvalue;
    }

    public void setSLAvalue(String SLAvalue) {
        this.SLAvalue = SLAvalue;
    }

    public String getAvailabilty() {
        return availabilty;
    }

    public void setAvailabilty(String availabilty) {
        this.availabilty = availabilty;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceSubType() {
        return resourceSubType;
    }

    public void setResourceSubType(String resourceSubType) {
        this.resourceSubType = resourceSubType;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public void setHostGroup(String hostGroup) {
        this.hostGroup = hostGroup;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setSubService(String subService) {
        this.subService = subService;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }

    public void setSize(String size) {

        this.size = size;
    }

    public void setStatus(String status) {

        this.status = status;
    }

    public void setEndTime(String endTime) {

        this.endTime = endTime;
    }

    public void setStartTime(String startTime) {

        this.startTime = startTime;
    }

    public void setCategory(String category) {

        this.category = category;
    }

    public void setHost(String host) {
        if (host == null) {

            host = "rediff_mail";
        }
        if (host.split(":").length > 1) {
            String str[] = host.split(":");
            host = str[0];
        }
        this.host = host;
    }

    public void setMetricType(String metricType) {

        this.metricType = metricType;
    }

    public void setTimestamp1(String timestamp1) {
        System.out.println("timestamp in ReportData setTimeStamp1::"+timestamp1);
        this.timestamp1 = timestamp1;
    }

    public void setValue(String value) {

        this.value = value;
    }

    public void setCCustomerID(int cCustID) {

        this.cCustomerID = cCustID;
    }

    public String getHostGroup() {
        return hostGroup;
    }

    public String getService() {
        return service;
    }

    public String getSubService() {
        return subService;
    }

    public String getAlertName() {
        return alertName;
    }

    public String getEventID() {
        return eventID;
    }

    public String getSize() {

        return size;
    }

    public String getStatus() {
        return status;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getCategory() {
        return category;
    }

    public String getHost() {
        return host;
    }

    public String getMetricType() {
        return metricType;
    }

    public String getTimestamp1() {
        return timestamp1;
    }

    public String getValue() {
        return value;
    }

    public int getCCustomerID() {
        return cCustomerID;
    }

    public String getSuper() {
        String str = null;
        str = "host = " + host + ",hostGroup = " + hostGroup + ",service = " + service
                + ",subService = " + subService + ",alertName = " + alertName
                + ",metricType = " + metricType + ",category = " + category
                + ",timestamp1 = " + timestamp1 + ",startTime = " + startTime
                + ",endTime = " + endTime + ",status = " + status + ",size = " + size
                + ",resourceType = " + resourceType + ",resourceSubType = " + resourceSubType
                + ",resourceId = " + resourceId + ",value = " + value + ",eventID = " + eventID + ",cCustomerID = " + cCustomerID;
        return str;
    }
}
