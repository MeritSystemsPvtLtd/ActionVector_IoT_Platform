/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.*;
import java.util.Date;
import java.text.*;
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author gopal
 * Created on Apr 25, 2012, 1:07:00 PM
 */
public class DBConstants
{

    private static String szMediumStringColumnType = "";
    private static String szLongStringColumnType = "";
    private static String szTextColumnType = "";
    private static String szTimeStampColumnType = "";
    private static String szBinaryColumnType = "";
    private static String szBinaryColumnType1 = "";
    private static String szIntegerColumnType = "";
    private static String sz80StringColumnType = "";
    private static String szSmallStringColumnType = "";
    
    private static String szStringColumnType = "";
    private static String szSMStringColumnType = "";
    private static String szExternaluser = "";
    
    private static String szBigIntColumnType = "";
    
    public static final String STATUS_HISTORY_TABLE = "gastatushistory";
    public static final String ASSIGNMENT_HISTORY_TABLE = "gaassignmenthistory";
    public static final String STATUS_ASSIGNMENT_TABLE = "gastatusassignment";
    public static final String STATUS_STATEMACHINE_TABLE = "gastatusstatemachine";
    public static final String CHECKLIST_TABLE = "gachecklistattribute";
    public static final String TASK_CHECKLIST_TABLE = "gataskchecklist";
    public static final String DOMAIN_INFO_TABLE = "gadomaininfo";
    public static final String CUSTOMER_TABLE = "gacustomer";
    public static final String OPERATOR_TABLE = "gaoperator";
    public static final String USER_TABLE = "gauser";
    public static final String AUTOUSER_TABLE = "gaautouser";
    public static final String TASKTYPE_TABLE = "gatasktype";
    public static final String STATUS_TABLE = "gastatus";
    public static final String PRIORITY_TABLE = "gapriority";
    public static final String USER_STATISTICS_TABLE = "gauserstatistics";
    public static final String CHAT_STATISTICS_TABLE = "gachatstatistics";
    public static final String LANGUAGE_TABLE = "galanguage";
    public static final String EXTERNAL_TABLE_LINK_TABLE = "gaexternaltablelink";
    public static final String ASSIGNMENT_PREF_TABLE = "gaassignmentpref";
    public static final String OP_ASSIGN_TABLE = "gaopassign";
    public static final String TASK_TABLE = "gatask";
    public static final String ARCHIVE_TASK_TABLE = "gaarchivetask";
    public static final String HISTORY_TABLE = "gahistory";
    public static final String ARCHIVE_HISTORY_TABLE = "gaarchivehistory";
    public static final String TASK_CHAT_TABLE = "gataskchat";
    public static final String ARCHIVE_USER_STATISTICS_TABLE = "gaarchiveuserstatistics";
    public static final String ARCHIVE_TASK_CHAT_TABLE = "gaarchivetaskchat";
    public static final String MBOARD_TABLE = "gamboard";
    public static final String BULLETINBOARD_TABLE = "gabulletinboard";
    public static final String ARCHIVE_BULLETINBOARD_TABLE = "gaarchivebulletinboard";
    public static final String ARTICLES_TABLE = "gaarticles";
    public static final String ARTICLES_FILES_TABLE = "gaarticlesfiles";
    public static final String KB_STRUCTURE_TABLE = "gakbstructure";
    public static final String KB_ARTICLES_TABLE = "gakbarticles";
    public static final String KB_SECURITY_TABLE = "gakbsecurity";
    public static final String FAQ_STRUCTURE_TABLE = "gafaqstructure";
    public static final String FAQ_ARTICLES_TABLE = "gafaqarticles";
    public static final String FAQ_SECURITY_TABLE = "gafaqsecurity";
    public static final String TASK_ASSIGNEE_TABLE = "gataskassignee";
    public static final String ARCHIVE_TASK_ASSIGNEE_TABLE = "gaarchivetaskassignee";
    public static final String USER_PREFERENCES_TABLE = "gauserpreferences";
    public static final String PRODUCT_TYPE_TABLE = "gaproducttype";
    public static final String SERVICE_TYPE_TABLE = "gaservicetype";
    public static final String ADHOC_TYPE_TABLE = "gaadhoctype";
    public static final String PRODUCT_TABLE = "gaproduct";
    public static final String PRODUCT_LINK_TABLE = "gaproductlink";
    public static final String SERVICE_TABLE = "gaservice";
    public static final String ADHOC_TABLE = "gaadhoc";
    public static final String SECURITYLEVELS_TABLE = "gasecuritylevels";
    public static final String HIERARCHY_TABLE = "gahierarchy";
    public static final String USER_HIERARCHY_TABLE = "gauserhierarchy";
    public static final String GROUP_HIERARCHY_TABLE = "gagrouphierarchy";
    public static final String GROUPNAME_TABLE = "gagroupname";
    public static final String AGENT_GROUP_TABLE = "gaagentgroup";
    public static final String USER_GROUP_TABLE = "gausergroup";
    public static final String TASKTYPE_ATTRIBUTES_TABLE = "gatasktypeattributes";
    public static final String HISTORY_ATTRIBUTES_TABLE = "gahistoryattributes";
    public static final String HISTORY_ATTRIBUTE_LINK_TABLE = "gahistorylinktable";
    public static final String TASKTYPE_GROUP_TABLE = "gatasktypegroup";
    public static final String POLICIES_TABLE = "gapolicies";
    public static final String STATUS_PERMISSIONS_TABLE = "gastatuspermissions";
    public static final String TASKTYPE_ASSIGNEE_TABLE = "gatasktypeassignee";
    public static final String ARCHIVE_TASKTYPE_ASSIGNEE_TABLE = "gaarchivetasktypeassignee";
    public static final String PRODUCT_MASTER_TABLE = "gaproductmaster";
    public static final String FORUM_SECURITY_TABLE = "gaforumsecurity";
    public static final String FEEDBACK_ATTRIBUTE_TABLE = "gafeedbackattribute";
    public static final String FEEDBACK_TABLE = "gafeedback";
    public static final String ARCHIVE_FEEDBACK_TABLE = "gaarchivefeedback";
    public static final String CONTACT_SECURITY_TABLE = "gacontactsecurity";
    public static final String CHAT_POLICY_TABLE = "gachatpolicy";
    public static final String ESCALATION_RULE_TABLE = "gaescalationrule";
    public static final String ESCALATED_TASKS_TABLE = "gaescalatedtasks";
    public static final String ORGANIZATION_HOLIDAYS_TABLE = "gaorganizationholidays";
    public static final String MAIL_ROUTING_OPTIONS_TABLE = "gamailroutingoptions";
    public static final String CALENDAR_TABLE = "gacalendar";
    public static final String CUSTOMER_MESSAGES_TABLE = "gacustomermessages";
    public static final String USER_MESSAGES_TABLE = "gausermessages";
    public static final String TASK_VIEW_TABLE = "gataskview";
    public static final String TABLE_STRUCTURE_TABLE = "gatablestructure";
    public static final String TASK_TABLE_TABLE = "gatasktable";
    public static final String TASK_VOICE_TABLE = "gataskvoice";
    public static final String TASKTYPE_MAILBOX_TABLE = "gatasktypemailbox";
    public static final String FORUM_OPTIONS_TABLE = "gaforumoptions";
    public static final String EMAIL_ATTACHMENTS_TABLE = "gaemailattachments";
    public static final String TRANSIENT_TASK_TABLE = "gatransienttask";
    public static final String TRANSIENT_HISTORY_TABLE = "gatransienthistory";
    public static final String CATEGORY_TABLE = "gacategory";
    public static final String TASK_FILE_TABLE = "gataskfile";
    public static final String ARCHIVE_TASK_FILE_TABLE = "gaarchivetaskfile";
    public static final String PSUEDOUSER_TABLE = "gapsuedouser";
    public static final String CANNEDRESPONSE_TABLE = "gacannedresponse";
    public static final String TRANSIENT_VIEW_TABLE = "gatransientview";
    public static final String MAIL_ROUTING_RULES_TABLE = "gamailroutingrules";
    public static final String TASK_ASSIGNMENT_RULES_TABLE = "gataskassignmentrules";
    public static final String ASSIGNMENT_FILES_TABLE = "gaassignmentfiles";
    public static final String TASKTYPE_ATTRIBUTE_POLICY_TABLE = "gatasktypeattributepolicy";
    public static final String HISTORY_ATTRIBUTE_POLICY_TABLE = "gahistoryattributepolicy";
    public static final String TASKTYPE_ATTRIBUTE_LINK_TABLE = "gatasktypeattributelink";
    public static final String TASK_POLICIES_TABLE = "gataskpolicies";
    public static final String ARCHIVE_TASK_POLICIES_TABLE = "gaarchivetaskpolicies";
    public static final String CATEGORY_TASK_ASSIGNMENT_RULES_TABLE = "gacategorytaskassignmentrules";
    public static final String EVENT_ARCHITECTURE_TABLE = "gaeventarchitecture";
    public static final String RULE_ARCHITECTURE_TABLE = "garulearchitecture";
    public static final String USER_TASK_FILTER_TABLE = "gausertaskfilter";
    public static final String USER_TASK_QUERY_TABLE = "gausertaskquery";
    public static final String RECENT_TASK_VIEW_USER_TABLE = "garecenttaskviewuser";
    public static final String ARCHIVE_RECENT_TASK_VIEW_USER_TABLE = "gaarchiverecenttaskviewuser";
    public static final String STATUS_TASK_ASSIGNMENT_RULES_TABLE = "gastatustaskassignmentrules";//This table does not exist in the egestalt database
    public static final String DIRECTORY_LINK_TABLE = "gadirectorylink";
    public static final String ACTIVITY_REPORT_TABLE = "gaactivityreport";
    public static final String BP_STRUCTURE_TABLE = "gabpstructure";
    public static final String BP_STRUCTURE_FILES_TABLE = "gabpstructurefiles";
    public static final String BP_SECURITY_TABLE = "gabpsecurity";
    public static final String BP_ARTICLES_TABLE = "gabparticles";
    public static final String BUSINESS_UNIT_TABLE = "gabusinessunit";
    public static final String TASKTYPE_BUSINESSUNIT_TABLE = "gatasktypebusinessunit";
    public static final String STATUS_GROUP_TABLE = "gastatusgroup";
    public static final String SLA_TYPE_TABLE = "gaslatype";
    public static final String SLA_CUSTOMER_TABLE = "gaslacustomer";
    public static final String TASKTYPE_FORM_STRUCTURE_TABLE = "gatasktypeformstructure";
    public static final String TASK_FORM_TABLE = "gataskform";
    public static final String TASK_REPORT_TABLE = "gataskreport";
    public static final String TASK_REPORT_SECURITY_TABLE = "gataskreportsecurity";
    public static final String RECURR_TASK_TABLE = "garecurringtask";
    
    public static final String ACTIONS_TABLE = "gaaction";
    public static final String SLA_TABLE = "gasla";
    public static final String WEB_SERVICE_TABLE = "gawebservice";
    public static final String WEB_SERVICE_MESSAGE_MAP_TABLE = "gawebservicemessagemap";
    public static final String WEB_SERVICE_MESSAGES_TABLE = "gawebservicemessages";
    public static final String WEB_SERVICE_OPERATIONS_TABLE = "gawebserviceoperations";
    public static final String SPARES_CATALOG_TABLE = "gasparescatalog";
    
    public static final String PARENT_TASK_TYPE_TABLE = "gaparenttasktype";
    public static final String SLA_ATTRIBUTE_STATUS_TABLE = "gaslaattributestatus";
    public static final String TASK_TABLE_EXPRESSION_CONFIG_TABLE = "gatasktableexpressionconfig";
    public static final String TASK_TYPE_ACTION_POLICY_TABLE = "gatasktypeactionpolicy";
    public static final String TASK_TYPE_ATTRIBUTE_VALUE_CONFIG_TABLE = "gatasktypeattributevalueconfig";
    public static final String TASK_TYPE_TABLE_ATTRIBUTE_VALUE_CONFIG_TABLE = "gatasktypetableattributevalueconfig";
    public static final String USER_REPORTS_TABLE = "gauserreports";
    
    public static final String ADSDETAILS = "gadirectorylinkdetails";
    public static final String OUTOFOFFICE = "gaoutofoffice";
    public static final String STATUSSTATEMACHINE_TABLE = "gastatusstatemachine"; //8/27/2009 added for stustus not be rerouted

    /* *******Configuring some Adapter related table in proChara Customer DB Configuration
                                  **********************Adding tables  08/28/2012 -- GOPAL ************ */

    public static final String FILEUPLOAD_TABLE = "gafileupload";
    public static final String CHARTSCHEMA_TABLE = "chartschema";
    public static final String HOSTINFO_TABLE = "hostinfo";
    public static final String HOSTINFOSERVICES_TABLE="hostinfoservices";
    public static final String IPINFO_TABLE="ipinfo";
    public static final String IPINFOONECMDB_TABLE="ipinfoonecmdb";
    public static final String SERVICEMETRICS_TABLE="servicemetrics";
    public static final String SMETRICSLATHRESHOLDS_TABLE="smetricslathresholds";
    public static final String GENERATEDID_TABLE="generatedId";
    public static final String INCREASING_ID_SEQ="increasing_id_seq";
   

    public void setColumnFields(String szDatabaseName)
    {
        System.out.println("Setting the column datatypes fields to the database " + szDatabaseName);
        szSmallStringColumnType = "VARCHAR(5)";
        if ("Postgresql".equalsIgnoreCase(szDatabaseName))
        {
            sz80StringColumnType = "VARCHAR(80)";
            szStringColumnType = "VARCHAR";
            szMediumStringColumnType = "VARCHAR(255)";
            szLongStringColumnType = "VARCHAR(2048)";
            szTextColumnType = "TEXT";
            szTimeStampColumnType = "TIMESTAMP";
            szBinaryColumnType = "BYTEA";
            szBinaryColumnType1 = "OID";//Added by krishna
            szIntegerColumnType = "INTEGER";
            szSMStringColumnType = "VARCHAR(50)";
            szBigIntColumnType = "bigint";
            szExternaluser = "VARCHAR(50)";
        }
        if ("Mysql".equalsIgnoreCase(szDatabaseName))
        {
            sz80StringColumnType = "VARCHAR(80)";
            szStringColumnType = "VARCHAR";
            szMediumStringColumnType = "VARCHAR(255)";
            szLongStringColumnType = "TEXT";
            szTextColumnType = "LONGTEXT";
            szTimeStampColumnType = "DATETIME";
            szBinaryColumnType = "LONGBLOB";
            szIntegerColumnType = "INTEGER";
            szSMStringColumnType = "VARCHAR(50)";
            szBigIntColumnType = "bigint";
            szExternaluser = "VARCHAR(50)";

        }
        if ("Mysql4".equalsIgnoreCase(szDatabaseName))
        {
            sz80StringColumnType = "VARCHAR(80)";
            szStringColumnType = "VARCHAR";
            szMediumStringColumnType = "VARCHAR(255)";
            szLongStringColumnType = "TEXT";
            szTextColumnType = "LONGTEXT";
            szTimeStampColumnType = "DATETIME";
            szBinaryColumnType = "LONGBLOB";
            szIntegerColumnType = "INTEGER";
            szSMStringColumnType = "VARCHAR(50)";
            szBigIntColumnType = "bigint";
            szExternaluser = "VARCHAR(50)";
        }

        if ("Sybase".equalsIgnoreCase(szDatabaseName))
        {
            sz80StringColumnType = "VARCHAR(80)";
            szStringColumnType = "VARCHAR";
            szMediumStringColumnType = "VARCHAR(255)";
            szLongStringColumnType = "VARCHAR(2048)";
            szTextColumnType = "TEXT";
            szTimeStampColumnType = "TIMESTAMP";
            szBinaryColumnType = "IMAGE";
            szIntegerColumnType = "INTEGER";
            szSMStringColumnType = "VARCHAR(50)";
            szBigIntColumnType = "bigint";
            szExternaluser = "VARCHAR(50)";

        }
        if ("Sql Server".equalsIgnoreCase(szDatabaseName))
        {
            sz80StringColumnType = "varchar(80)";
            szStringColumnType = "varchar";
            szMediumStringColumnType = "varchar(255)";
            szLongStringColumnType = "varchar(2048)";
            szTextColumnType = "text";
            szTimeStampColumnType = "datetime";
            szBinaryColumnType = "image";
            szIntegerColumnType = "int";
            szSMStringColumnType = "varchar(50)";
            szBigIntColumnType = "bigint";
            szExternaluser = "VARCHAR(50)";

        }

        if ("IBM DB2".equalsIgnoreCase(szDatabaseName))
        {
            sz80StringColumnType = "VARCHAR(80)";
            szStringColumnType = "VARCHAR";
            szMediumStringColumnType = "VARCHAR(255)";
            szLongStringColumnType = "VARCHAR(2048)";
            szTextColumnType = "\"LONG VARCHAR\"";
            szTimeStampColumnType = "TIMESTAMP";
            szBinaryColumnType = "BLOB";
            szIntegerColumnType = "INTEGER";
            szSMStringColumnType = "VARCHAR(50)";
            szBigIntColumnType = "bigint";
            szExternaluser = "VARCHAR(50)";
        }
        if ("Oracle".equalsIgnoreCase(szDatabaseName))
        {
            sz80StringColumnType = "VARCHAR(80)";
            szStringColumnType = "VARCHAR";
            szMediumStringColumnType = "VARCHAR(255)";
            szLongStringColumnType = "VARCHAR(2048)";
            szTextColumnType = "CLOB";
            szTimeStampColumnType = "TIMESTAMP";
            szBinaryColumnType = "BLOB";
            szIntegerColumnType = "NUMBER";
            szSMStringColumnType = "VARCHAR(50)";
            szBigIntColumnType = "bigint";
            szExternaluser = "VARCHAR(50)";
        }


    }

    public String get80StringColumnType()
    {
        return sz80StringColumnType;
    }
    

    public String getStringColumnType()
    {
        return szStringColumnType;
    }

    public String getszSMStringColumnType()
    {
        return szSMStringColumnType;
    }
    

    public String getszExternaluser()
    {
        return szExternaluser;
    }

    public String getszBigIntColumnType()
    {
        return szBigIntColumnType;
    }
    

    public String getMediumStringColumnType()
    {
        return szMediumStringColumnType;
    }

    public String getLongStringColumnType()
    {
        return szLongStringColumnType;
    }

    public String getTextColumnType()
    {
        return szTextColumnType;
    }

    public String getTimeStampColumnType()
    {
        return szTimeStampColumnType;
    }

    public String getBinaryColumnType()
    {
        return szBinaryColumnType;
    }

    public String getBinaryColumnType1()
    {
        return szBinaryColumnType1;
    }

    public String getIntegerColumnType()
    {
        return szIntegerColumnType;
    }

    public String getSmallStringColumnType()
    {
        return szSmallStringColumnType;
    }
}
