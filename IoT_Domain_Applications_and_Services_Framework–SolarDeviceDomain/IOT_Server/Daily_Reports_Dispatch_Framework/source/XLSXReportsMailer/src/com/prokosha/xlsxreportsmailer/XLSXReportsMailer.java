/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.xlsxreportsmailer;

import com.prokosha.xlsxreportsmailerconfiguration.XLSXReportsMailerConfiguration;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;

/**
 *
 * @author yedukondalu
 */
public class XLSXReportsMailer {
    
      private static final Logger logger = Logger.getLogger(XLSXReportsMailer.class.getName());
      public static String propertyFile = null;
      public static String key = null;
      public static String periods = null;

    
    public XLSXReportsMailer(String propertyFileName) {

        logger.info("Loading the property file : " + propertyFileName);

        this.propertyFile = propertyFileName;
    }

    public static void initialize() throws IOException {

        if (!XLSXReportsMailerConfiguration.loadProperties(propertyFile)) {
            logger.info("Error in loading property file");
            System.exit(0);
        }

    }
    
    public static boolean mailSender() {

        
            String szBodyPart = XLSXReportsMailerConfiguration.getBodyPart();
            String receipentMailIdsPath = XLSXReportsMailerConfiguration.getReceipentMailIDPath();
            String szSubject = XLSXReportsMailerConfiguration.getSubject();
            String szSMTPServer = XLSXReportsMailerConfiguration.getSmtpServer();
            String szSMTPUser = XLSXReportsMailerConfiguration.getSmtpUser();
            String iSMTPPort = XLSXReportsMailerConfiguration.getSmtpPort();
            String szSMTPPassword = XLSXReportsMailerConfiguration.getSmtpPassword();
            String xlsxFilesPath = XLSXReportsMailerConfiguration.getXlsxFilesPath();
            //periods=XLSXReportsMailerConfiguration.getPeriods();
            logger.info("SMTP port::" + iSMTPPort);
            
            Properties props=null,eMailsAddressPath=null;
            Session session=null;
            File directory=null;
            File[] attachFiles =null;
            ArrayList attachList =new ArrayList();
            FilenameFilter ff=null;
            Message msg=null;
            MimeBodyPart msgBodyPart=null,attachPart=null;
            Multipart multipart=null;
            FileInputStream fis=null;
            InternetAddress iAddress=null;
            try {
                props = new Properties();
                props.put("mail.smtp.host", szSMTPServer);
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", iSMTPPort);
                 session = Session.getDefaultInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(szSMTPUser, szSMTPPassword);
                            }
                        });
                try {
                    logger.info("in try msg initialized method");
                    //while loop for reapete action for given intervel     
                        eMailsAddressPath = new Properties();
                        fis=new FileInputStream(receipentMailIdsPath);
                        eMailsAddressPath.load(fis);
                        logger.info("recpeent before config ");
                        Enumeration eProps = eMailsAddressPath.propertyNames();
                        logger.info("recpeent before config "+eProps.toString());
                         String szToReceipent = null;
                         String szCCReceipent = null;
                           
                        while (eProps.hasMoreElements()) {
                            key = (String) eProps.nextElement();
                            String value = eMailsAddressPath.getProperty(key);

                            logger.info(key + " => " + value);
                             
                            String[] mailSplite = value.split(";");
                            logger.info("mailSplite.length==>" + mailSplite.length);
                            if (mailSplite.length == 1) {
                                szToReceipent = mailSplite[0];
                            } else {
                                szToReceipent = mailSplite[0];
                                szCCReceipent = mailSplite[1];
                            }
                            logger.info("TOReceipent mailid from module " + szToReceipent);
                            logger.info("CCReceipent mailids from module " + szCCReceipent);

                             directory = new File(xlsxFilesPath);
                            logger.info("xlsx files directory " + directory);
                               attachFiles = directory.listFiles(new FilenameFilter() {
                                //retriving files name startwith (Day,Month,Year)+key
                                public boolean accept(File directory, String name) {
                                    logger.info("key================> " + key);

                                    return name.startsWith("Day_" + key);
                                }
                            });
                             
                            logger.info("attach files" + attachFiles.length);
                           
                            logger.info("receipent mailid from module " + szToReceipent);
                            logger.info("SMTPUser:::" + szSMTPUser);
                            logger.info("szSMTPPassword:::" + szSMTPPassword);
                             msg = new MimeMessage(session);
                            iAddress=new InternetAddress((szSMTPUser));
                            msg.setFrom(iAddress);
                            if (szCCReceipent != null) {
                                logger.info("szCCReceipent is not null........");
                                msg.setRecipients(Message.RecipientType.TO,
                                        InternetAddress.parse(szToReceipent));
                                //a parse method in javax.mail.internet of internetaddress(class) it Parses the given comma-separated sequence of addresses into InternetAddresses. 
                                msg.setRecipients(Message.RecipientType.CC,
                                        InternetAddress.parse(szCCReceipent));
                                msg.setSubject(szSubject);
                                msg.setSentDate(new Date());
                            } else {
                                logger.info("szCCReceipent is null........");
                                msg.setRecipients(Message.RecipientType.TO,
                                        InternetAddress.parse(szToReceipent));
                                msg.setSubject(szSubject);
                                msg.setSentDate(new Date());
                            }
                                 
                            // creates msgbody part
                             msgBodyPart = new MimeBodyPart();
                            msgBodyPart.setContent(szBodyPart, "text/html");

                            // creates multi-part
                             multipart = new MimeMultipart();
                            multipart.addBodyPart(msgBodyPart);

                            // adds attachments
                            if (attachFiles != null && attachFiles.length > 0) {
                                for (File filePath : attachFiles) {
                                     attachPart = new MimeBodyPart();

                                    try {

                                        logger.info("attach file with path= " + filePath);
                                        attachPart.attachFile(filePath);
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }

                                    multipart.addBodyPart(attachPart);
                                }
                            }
                            // sets the multi-part as e-mail's content
                            msg.setContent(multipart);
                           // sends the e-mail
                            Transport.send(msg);

                            logger.info("Mail has sent successfully");

                            //sleep time in milliseconds here intervel values we take in hours
                        }
                } catch (MessagingException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e);
                return false;
            } 
            finally {

                try {
                    
                    if(props!=null){
                    props.clear();
                          props=null;
                    }
                    if(eMailsAddressPath!=null){
                    eMailsAddressPath.clear();
                      eMailsAddressPath=null;
                    }
                    if(fis!=null){
                    fis.close();
                    fis=null;
                    }
                             session=null;
                             iAddress=null;
                        directory=null;
                        attachFiles =null;
                        msg=null;
                            msgBodyPart=null;
                            attachPart=null;
                            multipart=null;
                        
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e);
                }
            }
          return true;  
            
        }
    public static void main(String[] args) {
        try {
        XLSXReportsMailer tl = new XLSXReportsMailer("config"+File.separator+"parameters.properties");
        logger.info("calling initialize method ....");
        logger.info("calling initialize method ....");
        
        initialize();
        logger.info("Mail is Sending....");
        System.out.println("calling initialize method ....");
        
        mailSender();
        } catch (IOException ex) {
        ex.printStackTrace();
        logger.error(ex);
        }
        
    }
    
}
