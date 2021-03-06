//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.10 at 11:02:44 AM IST 
//
package serialportapp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p> Java class for anonymous complex type.
 *
 * <p> The following schema fragment specifies the expected content contained
 * within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="baudRate" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="flowControlIn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="flowControlOut" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="parity" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="databits" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="stopbits" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="timeout" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"name", "baudRate", "flowControlIn",
    "flowControlOut", "parity", "databits", "stopbits", "timeout", "openPortTimeout", "readTimeout", "readSleepTimeslice"})
@XmlRootElement(name = "port")
public class Port {

    @XmlElement(required = true)
    protected String name;
    protected int baudRate;
    @XmlElement(required = true)
    protected int flowControlIn;
    @XmlElement(required = true)
    protected int flowControlOut;
    @XmlElement(required = true)
    protected int parity;
    protected int databits;
    protected int stopbits;
    protected int timeout;
    protected int openPortTimeout = -1;
    protected int readTimeout = -1;
    protected int readSleepTimeslice = -1;

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getReadSleepTimeslice() {
        return readSleepTimeslice;
    }

    public void setReadSleepTimeslice(int readSleepTimeslice) {
        this.readSleepTimeslice = readSleepTimeslice;
    }

    public int getOpenPortTimeout() {
        return openPortTimeout;
    }

    public void setOpenPortTimeout(int openPortTimeout) {
        this.openPortTimeout = openPortTimeout;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getPortName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setPortName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the baudRate property.
     *
     */
    public int getBaudRate() {
        return baudRate;
    }

    /**
     * Sets the value of the baudRate property.
     *
     */
    public void setBaudRate(int value) {
        this.baudRate = value;
    }

    /**
     * Gets the value of the flowControlIn property.
     *
     * @return possible object is {@link String }
     *
     */
    public int getFlowControlIn() {
        return flowControlIn;
    }

    /**
     * Sets the value of the flowControlIn property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setFlowControlIn(int value) {
        this.flowControlIn = value;
    }

    /**
     * Gets the value of the flowControlOut property.
     *
     * @return possible object is {@link String }
     *
     */
    public int getFlowControlOut() {
        return flowControlOut;
    }

    /**
     * Sets the value of the flowControlOut property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setFlowControlOut(int value) {
        this.flowControlOut = value;
    }

    /**
     * Gets the value of the parity property.
     *
     * @return possible object is {@link String }
     *
     */
    public int getParity() {
        return parity;
    }

    /**
     * Sets the value of the parity property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setParity(int value) {
        this.parity = value;
    }

    /**
     * Gets the value of the databits property.
     *
     */
    public int getDatabits() {
        return databits;
    }

    /**
     * Sets the value of the databits property.
     *
     */
    public void setDatabits(int value) {
        this.databits = value;
    }

    /**
     * Gets the value of the stopbits property.
     *
     */
    public int getStopbits() {
        return stopbits;
    }

    /**
     * Sets the value of the stopbits property.
     *
     */
    public void setStopbits(int value) {
        this.stopbits = value;
    }

    /**
     * Gets the value of the timeout property.
     *
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets the value of the timeout property.
     *
     */
    public void setTimeout(int value) {
        this.timeout = value;
    }
}
