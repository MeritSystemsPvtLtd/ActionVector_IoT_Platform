/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.emailsmsutility;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import org.apache.log4j.Logger;

/**
 *
 * @author rekhas
 */
public class EMailSMSUtility {
    private static Logger logger = Logger.getLogger(EMailSMSUtility.class.getName());
    static String emailIDs;
    static String fromAddress;
    static String smtpAddress;
    static int smtpPort;
    static String smtpUserName;
    static String smtpPassword;
    static String szSubject;
    static String szBody;
    static String[] mobNos;
    static String smsServerURL;
    static int smsServerPort;
    
    public static boolean initializeEmail(String emailIDs, String fromAddress, String smtpAddress, int smtpPort, 
            String smtpUserName, String smtpPassword) {
        try {
            
            EMailSMSUtility.emailIDs = emailIDs;
            EMailSMSUtility.fromAddress = fromAddress;
            EMailSMSUtility.smtpAddress = smtpAddress;
            EMailSMSUtility.smtpPort = smtpPort;
            EMailSMSUtility.smtpUserName = smtpUserName;
            EMailSMSUtility.smtpPassword = smtpPassword;
            return true;
        } catch (Exception e) {
            logger.error("Error in EmailSMSUtility.initializeEmail()==>>" + e.toString());
            e.printStackTrace();
        }
        return false;

    }
    
    public static boolean initializeSMS(String[] mobNos, String smsServerURL, int smsServerPort) {
        try {
            EMailSMSUtility.mobNos = mobNos;
            EMailSMSUtility.smsServerURL = smsServerURL;
            EMailSMSUtility.smsServerPort = smsServerPort;
            return true;
        } catch (Exception e) {
            logger.error("Error in EmailSMSUtility.initializeSMS()==>>" + e.toString());
            e.printStackTrace();
        }
        return false;

    }
    
    public static boolean sendSMS(String Subject) {
        URL url = null;
        URLConnection conn = null;
        Writer writer = null;
            try {
                url = new URL(smsServerURL);
                conn = url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.connect();
                writer = new OutputStreamWriter(conn.getOutputStream());
                if (mobNos != null) {
                    //write parameters
                    for (int i = 0; i < mobNos.length; i++) {
                        //System.out.println(" Mobile Number :"+Mobile_Array[i]);
                        if (mobNos[i] == null || mobNos[i].equalsIgnoreCase("") || mobNos[i].equalsIgnoreCase("None")) {
                            System.out.println("The user doesnot have Mobile Number");
                        } else {
                            writer.write("to=" + mobNos[i] + "&text=" + Subject + "&from=" + smsServerPort);
                        }
                    }
                    writer.flush();
                    writer.close();
                    writer = null;
                    conn.getInputStream();
                    conn = null;
                    url = null;
                    logger.info("sms sent successfully");
                    return true;
                } else {
                    logger.info("SMS Mobile nos not configured.");
                    return false;
                }
            } catch (IOException ex) {
                System.out.println("Number is Invalid");
                logger.error("Invalid Number==>>" + ex.toString());
                ex.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    url = null;
                    conn = null;
                    if (writer != null) {
                        writer.close();
                        writer = null;
            }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    public static void sendMail(String szSubject, String szMessagebody) {
        try {
            if (emailIDs != null) {
                Transport trans = null;
                Properties properties = System.getProperties();
                properties.put("mail.smtp.host", smtpAddress);
                properties.put("mail.smtp.auth", "true");

                Session mailsession = Session.getInstance(properties, null);
                MimeMessage msg = new MimeMessage(mailsession);
                    InternetAddress intaddrs = new InternetAddress(fromAddress);
                    msg.setFrom(intaddrs);
                msg.setSubject(szSubject);
                    Date date = new java.util.Date();
                    msg.setSentDate(date);
                //msg.setHeader("x-MessageID", szKey1 + ":" + szKey2);

                InternetAddress[] toaddr = InternetAddress.parse(emailIDs, false);
                msg.setRecipients(Message.RecipientType.TO, toaddr);
                try {
                    trans = mailsession.getTransport("smtp");
                    trans.connect(smtpAddress, smtpPort, smtpUserName, smtpPassword);
                } catch (Exception e) {
                    System.out.println("Connecting to smtpserver failed");
                    e.printStackTrace();
                }
                msg.setText(szMessagebody);
                try {
                    Address[] a;
                    a = msg.getAllRecipients();
                    trans.sendMessage(msg, a);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                    mailsession = null;
                    msg = null;
                    intaddrs = null;
                    date = null;
                    trans = null;
                    properties = null;
            } else {
                logger.info("Email IDs not configured.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
