/**
 * *********************************************************************
 * Software Developed by Merit Systems Pvt. Ltd., No. 42/1, 55/c, Nandi Mansion,
 * 40th Cross, Jayanagar 8th Block Bangalore - 560 070, India Work Created for
 * Merit Systems Private Limited All rights reserved
 *
 * THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES
 * NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED, DISTRIBUTED,
 * REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED,
 * COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD
 * SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
**********************************************************************
 */
package com.merit.dashboard.reports;

import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.prokosha.dbconnection.ConnectionDAO;
import com.merit.dashboard.DateGenerator;
import com.merit.dashboard.util.PropertyUtil;
import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 *
 * @author satya
 */
public class DAOHelper {

    static Logger log = Logger.getLogger(DAOHelper.class);
    static String sz_sheet_Name = "";
    static PdfPTable pdfObject = null;
    static HSSFWorkbook xlsObject = null;

    public static PdfPTable getPdfObject() {
        return pdfObject;
    }

    public static void setPdfObject(PdfPTable pdfObject) {
        DAOHelper.pdfObject = pdfObject;
    }

    public static HSSFWorkbook getXlsObject() {
        return xlsObject;
    }

    public static void setXlsObject(HSSFWorkbook xlsObject) {
        DAOHelper.xlsObject = xlsObject;
    }
//    Connection uniqueconnection = null;

    public String getMetricTypeWithTimestamp(String sz_customerID, String sz_metricType, String hostName, String sz_resourceType, String sz_selected_combo, String sz_tabName, String sz_interval) {
        String summaryJson = "";
        PropertyUtil propertyUtil = null;
        String ar_dates[] = DateGenerator.dateGenerator(sz_interval);
        String sz_concat_combo = "";
        if (sz_selected_combo.contains("%20")) {
            sz_concat_combo = sz_selected_combo.replaceAll("%20", "");
        } else if (sz_selected_combo.contains(" ")) {
            sz_concat_combo = sz_selected_combo.replaceAll(" ", "");

        } else {
            sz_concat_combo = sz_selected_combo.trim();
        }
        
        System.out.println("Concatinated String===" + sz_tabName.trim() + sz_concat_combo);

        if (!(sz_concat_combo.contains("Alerts"))) {
            sz_sheet_Name = "MetricReports";
        } else {
            sz_sheet_Name = "Alert_Summary_Report";
        }

        try {
            propertyUtil = new PropertyUtil();
            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = formatter1.parse(ar_dates[1]);
            long smilli = date.getTime() / 1000;
            Date date1 = formatter1.parse(ar_dates[0]);
            long emilli = date1.getTime() / 1000;
            String szQueryString = "";
//            uniqueconnection = ConnectionDAO.getConnection(sz_customerID);
            if (sz_concat_combo.contains("Availability")) {
                /**
                 * Modified Date 30Apr,2013 for mongo
                 */
                /*szQueryString = "select "
                                    + "host,"
                                    + "service,"
                                    + "subservice,"
                                    + "to_timestamp(timestamp1)::timestamp as downtime,"
                                    + "to_timestamp(timestamp2)::timestamp as uptime,"
                                    + "metrictype,"
                                    + "case when metricvalue is null then 'NaN' else metricvalue end as metricvalue,"
                                    + "category,"
                                    + "resourceid,"
                                    + "sla "
                                + "from "
                                    + "servicemetrics "
                                + "where "
                                    + "metrictype in ('"+new PropertyUtil().getreverseMapping_Properties().get("Availability") +"') and "
                                    + "host='"+hostName.trim()+"'"+" and "
                                    + "resourcetype='"+sz_resourceType.trim()+"' and "
                                    + "(timestamp1>"+smilli+" or timestamp1=1)";*/
                szQueryString = "select "
                                    + "host,"
                                    + "service,"
                                    + "subservice,"
                                    + "to_timestamp(timestamp1)::timestamp as downtime,"
                                    + "to_timestamp(timestamp2)::timestamp as uptime,"
                                    + "metrictype,"
                                    + "case when metricvalue is null then 'NaN' else (timestamp2-timestamp1)::text end as metricvalue,"
                                    + "category,"
                                    + "resourceid,"
                                    + "sla "
                                + "from "
                                    + "servicemetrics "
                                + "where "
                                    + "metrictype in ('" + propertyUtil.getreverseMapping_Properties().get("Availability") + "') and "
                                    + "host='" + hostName.trim() + "'" + " and "
                                    + "resourcetype='" + sz_resourceType.trim() + "' and "
                                    + "(timestamp2>" + smilli + " or timestamp2 is null)";
                String[] headingNames = {"host", "service", "subservice", "downtime", "uptime", "metrictype", "metricvalue"};
                summaryJson = generateJsonFromGivenQuery(szQueryString, sz_resourceType, sz_interval, sz_customerID, "SummaryPopup", headingNames);

            } else if (sz_concat_combo.contains("Alert")) {
//                uniqueconnection = ConnectionDAO.getConnection(sz_customerID + "");
                if (sz_selected_combo.contains("Assignee")) {
                    szQueryString = "select "
                                        + "task_id as TicketID,"
                                        + "g2.attributes6 as Host,"
                                        + "g1.assignee as Assignee,"
                                        + "task_summary as Summary,"
                                        + "attributes4 as Service,"
                                        + "created_date as CreatedDate,"
                                        + "g2.attributes7 as SubService,"
                                        + "g2.status as Status,"
                                        + "g2.priority as Priority "

                                    + "from "
                                        + "gatask g2 ,"
                                        + "gatasktypeassignee g1 "

                                    + "where  "
                                        + "created_date between '" + ar_dates[1] + "' and '" + ar_dates[0] + "'" + " and "
                                        + "g1.taskid=g2.task_id  and "
                                        + "g2.attributes10='" + sz_resourceType.trim() + "' and "
                                        + "g1.assignee='" + sz_metricType + "' and "
                                        + "g2.attributes6='" + hostName.trim() + "'";
                    
                    String[] headingNames = {"TicketID", "Host", "Service", "SubService", "CreatedDate", "Summary", "Status", "Priority", "Assignee"};
                    summaryJson = generateJsonFromGivenQuery(szQueryString, sz_resourceType, sz_interval, sz_customerID, "SummaryPopup", headingNames);
                } else if (sz_selected_combo.contains("Status")) {
                    szQueryString = "select "
                                        + "task_id as TicketID,"
                                        + "attributes6 as Host,"
                                        + "attributes4 as Service,"
                                        + "task_summary as Summary,"
                                        + "created_date as CreatedDate,"
                                        + "attributes7 as SubService,"
                                        + "status as Status,"
                                        + "priority as Priority "
                                    + "from "
                                        + "gatask "
                                    + "where "
                                        + "attributes10 ='" + sz_resourceType.trim() + "' and "
                                        + "attributes6='" + hostName.trim() + "' and "
                                        + "created_date between '" + ar_dates[1] + "' and '" + ar_dates[0] + "'  and "
                                        + "status='" + sz_metricType + "'";
                    String[] headingNames = {"TicketID", "Host", "Service", "SubService", "CreatedDate", "Summary", "Status", "Priority"};
                    summaryJson = generateJsonFromGivenQuery(szQueryString, sz_resourceType, sz_interval, sz_customerID, "SummaryPopup", headingNames);
                } else {
                    String sz_SummaryName = propertyUtil.getreverseMapping_Properties().get(sz_metricType);
                    szQueryString = "select "
                                        + "task_id as TicketID,"
                                        + "attributes6 as Host,"
                                        + "attributes4 as Service,"
                                        + "task_summary as Summary,"
                                        + "created_date as CreatedDate,"
                                        + "attributes7 as SubService,"
                                        + "status as Status,"
                                        + "priority as Priority "
                                    + "from "
                                        + "gatask "
                                    + "where "
                                        + "attributes10 ='" + sz_resourceType.trim() + "' and "
                                        + "attributes6='" + hostName.trim() + "' and "
                                        + "created_date between '" + ar_dates[1] + "' and '" + ar_dates[0] + "'  and "
                                        + "task_summary like'%" + sz_SummaryName.replaceAll("'", "") + "%'";
                    String[] headingNames = {"TicketID", "Host", "Service", "SubService", "CreatedDate", "Summary", "Status", "Priority"};
                    summaryJson = generateJsonFromGivenQuery(szQueryString, sz_resourceType, sz_interval, sz_customerID, "SummaryPopup", headingNames);
                }
            } else {
                szQueryString = "select "
                                    + "host as Host,"
                                    + "service as Service,"
                                    + "subservice as SubService,"
                                    + "to_timestamp(timestamp1)::timestamp as Timestamp,"
                                    + "metrictype as MetricType,"
                                    + "case when metricvalue is null then 'NaN' else round(cast(metricvalue as numeric),2) end as MetricValue,"
                                    + "category as Description,"
                                    + "resourceid,"
                                    + "sla "
                                + "from "
                                    + "servicemetrics "
                                + "where "
                                    + "metrictype in ('" + propertyUtil.getreverseMapping_Properties().get(sz_metricType) + "') and "
                                    + "host='" + hostName.trim() + "'" + " and "
                                    + "resourcetype='" + sz_resourceType.trim() + "' and "
                                    + "timestamp1 between " + smilli + " and " + emilli;
                if (sz_concat_combo.contains("Count")) {
                    String[] headingNames = {"Host", "Timestamp", "MetricType", "Description"};
                    summaryJson = generateJsonFromGivenQuery(szQueryString, sz_resourceType, sz_interval, sz_customerID, "SummaryPopup", headingNames);
                } else {
                    String[] headingNames1 = {"Host", "Service", "SubService", "Timestamp", "MetricType", "MetricValue"};
                    summaryJson = generateJsonFromGivenQuery(szQueryString, sz_resourceType, sz_interval, sz_customerID, "SummaryPopup", headingNames1);
                }
            }
            formatter1 = null;
            propertyUtil = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return summaryJson;
    }

    @SuppressWarnings("deprecation")
    public String generateJsonFromGivenQuery(String szQueryString, String resourceType, String timestampselection, String customer, String jsonnames, String[] headingNames) throws SQLException {
        log.debug(szQueryString);
        PdfPTable table = null;
        String szMetricTypeValueJson = "[";
         ResultSet rs = null;
         Color clr ;//modified
         Region region;//modified
         Paragraph para;//modified
         
         
        try {
            /**
             * Initialization of PDF
             */
            table = new PdfPTable(headingNames.length);
            para = new Paragraph("Summary");//modified
            PdfPCell cell = new PdfPCell(para);//modified
            cell.setColspan(headingNames.length);
            cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            clr = new Color(128, 200, 128);//modified
            cell.setBackgroundColor(clr);//modified
            table.addCell(cell);

            /**
             * Initialization of xls
             */
            HSSFWorkbook hwb = new HSSFWorkbook();
            HSSFSheet sheet = hwb.createSheet(sz_sheet_Name);
            HSSFCellStyle style = hwb.createCellStyle();
            style.setFillBackgroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            HSSFRow rowhead = sheet.createRow((short) 1);
            HSSFRow rowhead1 = sheet.createRow((short) 0);
            HSSFRichTextString string = new HSSFRichTextString("Summary");

            /**
             * apply custom font style
             */
            HSSFFont font = hwb.createFont();
            font.setFontName("verdana");
            font.setFontHeightInPoints((short) 10);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            string.applyFont(font);

            /**
             * Setting Style to XLS Title as Summary
             */
            int centerOfSheet = (short) (headingNames.length - 1);
            HSSFCell cellMgHead = rowhead1.createCell((short) 0);
            cellMgHead.setCellValue(string);
            region = new Region(0, (short) 0, 0, (short) centerOfSheet);//modified
            sheet.addMergedRegion(region);//modified
            style.setAlignment(CellStyle.ALIGN_CENTER);
            cellMgHead.setCellStyle(style);
            HSSFRichTextString szHeader = null;
            for (int p = 0; p < headingNames.length; p++) {
                table.addCell(headingNames[p]);
                /**
                 * PDF Header String
                 */
                /**
                 * XLS Header String with Style applied
                 */
                sheet.setColumnWidth(p, 4500);
                szHeader = new HSSFRichTextString(headingNames[p]);
                szHeader.applyFont(font);
                HSSFCell cellHeader = rowhead.createCell((short) p);
                cellHeader.setCellValue(szHeader);
                cellHeader.setCellStyle(style);
            }
            /**
             * Connection with DataBase
             */
//            java.sql.Statement stat = uniqueconnection.createStatement();
           
            rs = ConnectionDAO.executerQuery(szQueryString, customer);//stat.executeQuery(szQueryString);
            int rowindex = 1;

            /**
             * Executing Query with Generating Json for Summary Chart, XLS, PDF
             */
            String szConcatColumn;
            String metricValues;
            while (rs.next()) {
                szConcatColumn = "{";
                HSSFRow row = sheet.createRow((short) rowindex + 1);
                for (int i = 0; i < headingNames.length; i++) {
                    metricValues = "";
                    if (rs.getString(headingNames[i]) == null) {
                        metricValues = "";
                    } else {
                        if (rs.getString(headingNames[i]).contains("1970-01-01")) {
                            metricValues = "";
                        } else {
                            metricValues = rs.getString(headingNames[i]);
                        }
                        table.addCell(metricValues);
                        if (headingNames[i].equalsIgnoreCase("metricvalue")) {
                            row.createCell((short) i).setCellValue(Float.parseFloat(metricValues));
                        } else {
                            row.createCell((short) i).setCellValue(metricValues);
                        }
                    }
                    szConcatColumn = szConcatColumn + "\"" + headingNames[i] + "\":\"" + metricValues + "\",";
                }
                szConcatColumn = szConcatColumn.substring(0, szConcatColumn.lastIndexOf(",")) + "}";
                szMetricTypeValueJson = szMetricTypeValueJson + szConcatColumn + ",";
                rowindex++;
            }
            ConnectionDAO.closeStatement();
            rs = null;
                                    
            if (szMetricTypeValueJson.endsWith(",")) {
                szMetricTypeValueJson = szMetricTypeValueJson.substring(0, szMetricTypeValueJson.lastIndexOf(",")) + "]";
            } else {
                szMetricTypeValueJson = "[{}]";
            }
            setPdfObject(table);
            setXlsObject(hwb);
            table = null;
            cell = null;
            hwb = null;
            string = null;
            szHeader = null;
            clr = null;
            region = null;
            para = null;
        } catch (Exception e) {
            e.printStackTrace();
            szMetricTypeValueJson = "[{}]";
            return szMetricTypeValueJson;
        }finally{
            if(rs != null){
                rs.close();
            rs = null;
        }
        }
        System.out.println(szQueryString);

        return szMetricTypeValueJson;
    }
}
