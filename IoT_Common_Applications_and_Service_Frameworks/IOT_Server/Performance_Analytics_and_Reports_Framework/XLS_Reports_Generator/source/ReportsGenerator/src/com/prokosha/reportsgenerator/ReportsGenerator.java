/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.reportsgenerator;

import com.prokosha.reportsgenerator.configuration.ReportsGeneratorProperties;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.apache.log4j.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author yedukondalu
 */
public class ReportsGenerator {

    private static Logger logger = Logger.getLogger(ReportsGenerator.class.getName());
    public static String propertyFile = null;
    public static String periods = null;
    public static int intervel = 0;
    public static String resources = null;

    public ReportsGenerator(String propertyFileName) {
        logger.info("getting property file :" + propertyFileName);
        //accesing Property file 
        this.propertyFile = propertyFileName;

    }

    public boolean initialize() throws IOException {
        //calling the loadpropery methods
        if (!ReportsGeneratorProperties.loadProperties(propertyFile)) {
            logger.error("Error in  loading propertyFile");
            System.exit(0);
        }
        logger.info("loaded properties sucessfully");
        return true;

    }

    public static void generateXLS(String zDate) {

        FileOutputStream fileOutputStream = null;
        FileInputStream templateStream = null;
        HashMap installationLevelPath = null, dirStructure = null, subMap = null, customerMap = null, installationMap = null;
        Set<String> subKeys = null;
        ArrayList list = null;
        File xlsxFile = null, jsonDirectory = null, jsonSubDirectory = null;
        File[] matchFiles = null, matchJsons = null;
        JSONParser parser = null;
        JSONArray jsonArray = null, value = null, resourceNamesDev = null, resourceValuesDev = null, timeStampsDev = null, resourceValuesDev1 = null, timeStampsDev1 = null;
        XSSFWorkbook wbDB = null;
        XSSFSheet sheet = null;
        XSSFRow row = null, row1 = null, rowInst = null, rowInst1 = null;
        URL jUrl = null;
        HttpURLConnection conn = null;
        InputStream resopnse = null;
        InputStreamReader inputStrem = null;
        BufferedReader jsonInputStream = null;
        String serviceName, urlString, ParamValue, ParamValueDev, jsonName = null, output, url = null, finalJson = null;
        JSONObject deviceNamesObj, dataObj = null, obj = null, objCust1, objCust2, objDev1, objDev2, va1ue1;
        XSSFRow rows = null;
        XSSFCell cells = null;
        Set<Date> vall;
        Set<Integer> subKeys2;
        JSONArray apiDeviceNamesArray = null, jsonArrayData = null, yield, yield1, resourceNames, timeStamps1, resourceValues1, timeStamps, resourceValues;;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat formatter21 = new SimpleDateFormat("yyyy/MMM/dd");
        SimpleDateFormat formattery22 = new SimpleDateFormat("yyyy/MMM");

        Calendar perAPCalStart = null, perAPCalEnd, calJSON;
        SimpleDateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        Date result = null;
        Map custMap = null;
        Map custMap1 = null;
        Map<Date, HashMap> map = null;
        HashMap mm = null;
        Set<String> keys = null;
        String installationPath;
        String dbIstallreportsFile;
        String divicePath;
        String dbDivicereportsFile;
        String custSourcePath;
        String installationSourcePath;
        String deviceSourcePath;
        String destiFile;
        String deviceName;
        String sheetName = null;
        int rowCountDev = 0;
        String rid;
        try {
            String jsonSource = ReportsGeneratorProperties.getJsonSource();
            logger.info("Date of xls" + zDate);
            jsonSource = jsonSource + zDate + File.separator;
            logger.info(jsonSource);
            String templatePath = ReportsGeneratorProperties.getxlsxTemplatePath();
            String dashBoardReports = ReportsGeneratorProperties.getDashBoardReports();
            dirStructure = ReportsGeneratorProperties.getDirStructureMap();
            periods = ReportsGeneratorProperties.getPeriods();
            resources = ReportsGeneratorProperties.getResource();
            customerMap = new HashMap();
            installationMap = new HashMap();
            String[] spiltePeriods = periods.split(",");

            String name, minVal, maxVal, avg, curVal;
            for (int j = 0; j < spiltePeriods.length; j++) {
                //retriving all key from hashamp
                keys = dirStructure.keySet();

                for (String key : keys) {
                    String dbCustReportsFile = spiltePeriods[j] + "_";
                    String customerPath = spiltePeriods[j] + File.separator;
                    logger.info("key==> " + key);
                    customerPath += key + File.separator;
                    dbCustReportsFile += key + "_";
                    logger.info("customerPath==> " + customerPath);
                    logger.info("dbCustomerReports file " + dbCustReportsFile);
                    //retriving all key from subhashamp
                    subMap = (HashMap) dirStructure.get(key);
                    subKeys = subMap.keySet();
                    logger.info(subKeys);

                    for (String subKey : subKeys) {
                        installationPath = "";
                        dbIstallreportsFile = "";
                        logger.info(subKey);
                        installationPath = customerPath + subKey + File.separator;
                        dbIstallreportsFile = dbCustReportsFile + subKey + "_";
                        logger.info("Instalation path" + installationPath);
                        logger.info("reportsFiles1" + dbIstallreportsFile);
                        list = new ArrayList();
                        //retriving all key from list
                        list = (ArrayList) subMap.get(subKey);

                        for (int i = 0; i < list.size(); i++) {
                            divicePath = "";
                            dbDivicereportsFile = "";
                            custSourcePath = "";
                            installationSourcePath = "";
                            deviceSourcePath = "";
                            divicePath = installationPath + list.get(i) + File.separator;

                            dbDivicereportsFile = dbIstallreportsFile;
                            logger.info(list.get(i));
                            logger.info("DivicePAth= " + divicePath);
                            logger.info("reportsFile name= " + dbDivicereportsFile);
                            custSourcePath = jsonSource + customerPath;
                            logger.info("custSourcePath is=" + custSourcePath);
                            installationSourcePath = jsonSource + installationPath;
                            logger.info("installationSourcePath is=" + installationSourcePath);
                            deviceSourcePath = jsonSource + divicePath;
                            logger.info("deviceSourcePath is=" + deviceSourcePath);

                            custMap = new HashMap();
                            custMap1 = null;
                            map = null;
                            mm = new LinkedHashMap();
                            try {
                                destiFile = null;
                                parser = new JSONParser();
                                for (int y = 0; y < 3; y++) {
                                    logger.info("y==============>" + y);

                                    if (y == 0) {
                                        destiFile = dashBoardReports + dbCustReportsFile;
                                        if (customerMap.containsKey(key)) {
                                            logger.info("contain keyy" + customerMap);
                                            continue;
                                        } else {
                                            customerMap.put(key, true);
                                            logger.info("key====>" + y + key);
                                            logger.info("customerMap==>" + customerMap);
                                            xlsxFile = new File(templatePath + "customer" + File.separator + spiltePeriods[j] + "_Summary.xlsx");
                                        }
                                        jsonArray = (JSONArray) parser.parse(new FileReader(custSourcePath + "PerformanceRatio.json"));
                                    } else if (y == 1) {
                                        destiFile = dashBoardReports + dbIstallreportsFile;
                                        if (installationMap.containsKey(subKey)) {
                                            logger.info("contain keyy" + installationMap);
                                            continue;
                                        } else {
                                            installationMap.put(subKey, true);
                                            logger.info("key=====> " + y + subKey);
                                            logger.info("installationMap=====> " + installationMap);
                                            xlsxFile = new File(templatePath + "installation" + File.separator + spiltePeriods[j] + "_Summary.xlsx");
                                        }
                                        jsonArray = (JSONArray) parser.parse(new FileReader(installationSourcePath + "PerformanceRatio.json"));
                                    } else if (list.get(i).equals("Sensor")) {
                                        logger.info("Sensor=================>");
                                        continue;
                                    } else {

                                        urlString = resources + "GetResources?customer=";
                                        ParamValueDev = "ResourceId";
                                        url = urlString + key + "&service=" + subKey;
                                        logger.info(url);
                                        jsonName = urlResopnes(url);

                                        logger.info("Output from Server .... " + jsonName);
                                        finalJson = "";
                                        if (jsonName != null) {
                                            finalJson = jsonName.substring(jsonName.indexOf("["), jsonName.lastIndexOf("]") + 1);
                                            logger.info("FinalJson" + finalJson);

                                            apiDeviceNamesArray = (JSONArray) parser.parse(finalJson);
                                            logger.info("apiDeviceNamesArray" + apiDeviceNamesArray.size());

                                            if (apiDeviceNamesArray != null) {
                                                for (int w = 0; w < apiDeviceNamesArray.size(); w++) {
                                                    deviceNamesObj = (JSONObject) apiDeviceNamesArray.get(w);
                                                    deviceName = (String) deviceNamesObj.get(ParamValueDev);
                                                    logger.info("Device Name================>" + deviceName);
                                                    destiFile = dashBoardReports + dbDivicereportsFile + deviceName + "_";
                                                    xlsxFile = new File(templatePath + spiltePeriods[j] + "_Summary.xlsx");
                                                    if (list.get(i).equals("Inverter")) {
                                                        jsonArray = (JSONArray) parser.parse(new FileReader(deviceSourcePath + "InverterPerformanceRatio.json"));
                                                        //logger.info("Device Level  " + jsonArray);
                                                    } else {
                                                        logger.info("Continue.....");
                                                        continue;
                                                    }
                                                    logger.info("array size=========> " + jsonArray.size());

                                                    templateStream = new FileInputStream(xlsxFile);
                                                    wbDB = new XSSFWorkbook(templateStream);
                                                    //loading json one by one
                                                    logger.info("File contain no of sheet : " + wbDB.getNumberOfSheets());

                                                    for (int s = 0; s < wbDB.getNumberOfSheets(); s++) {
                                                        sheet = wbDB.getSheetAt(s);
                                                        sheetName = sheet.getSheetName();
                                                        logger.info("sheet no " + s + " Sheet name is " + sheetName);
                                                        rowCountDev = 0;
                                                        for (int l = 0; l < jsonArray.size(); l++) {
                                                            logger.info("Device level l====>" + l);
                                                            obj = (JSONObject) jsonArray.get(l);

                                                            if (sheetName.equalsIgnoreCase("Summary") && l == 0) {

                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Report Name"));
                                                                cells = rows.getCell(2);
                                                                cells.setCellValue(key + "-" + subKey + "-" + deviceName + "-" + spiltePeriods[j] + " Report");
                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Customer Name"));
                                                                cells = rows.getCell(2);
                                                                cells.setCellValue(key);
                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Installation Name"));
                                                                cells = rows.getCell(2);
                                                                cells.setCellValue(subKey);
                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Device name"));
                                                                cells = rows.getCell(2);
                                                                cells.setCellValue(deviceName);
                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Device type (Inv, SC, etc)"));
                                                                cells = rows.getCell(2);
                                                                cells.setCellValue((String) list.get(i));

                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Performance analysis period"));
                                                                cells = rows.getCell(2);
                                                                String zdatePeriod = spiltePeriods[j];
                                                                logger.info(zDate);

                                                                String perAP = performanceAnalysisPeriod(zDate, zdatePeriod);
                                                                logger.info(perAP);
                                                                /*
                                                                 if (spiltePeriods[j].equalsIgnoreCase("Hour")) {
                                                                 perAPCalStart.set(Calendar.MINUTE,0);
                                                                 perAPCalStart.set(Calendar.SECOND,0);
                                                                  
                                                                 }else{
                                                                 perAPCalStart.set(Calendar.HOUR_OF_DAY,0);
                                                                 perAPCalStart.set(Calendar.MINUTE,0);
                                                                 perAPCalStart.set(Calendar.SECOND,0);
                                                                   
                                                                 }

                                                                 if (spiltePeriods[j].equalsIgnoreCase("Hour")) {
                                                                 perAPCalStart.add(Calendar.HOUR, -1);
                                                                 } else if (spiltePeriods[j].equalsIgnoreCase("Day")) {
                                                                 //perAPCalStart.add(Calendar.DATE, -1);
                                                                 } else if (spiltePeriods[j].equalsIgnoreCase("Week")) {
                                                                 perAPCalStart.add(Calendar.DATE,  -((perAPCalEnd.get(Calendar.DAY_OF_WEEK))-1));
                                                                 } else if (spiltePeriods[j].equalsIgnoreCase("Month")) {
                                                                 perAPCalStart.add(Calendar.MONTH, -1);
                                                                 } else if (spiltePeriods[j].equalsIgnoreCase("year")) {
                                                                 perAPCalStart.add(Calendar.YEAR, -1);
                                                                 }
                                                                 logger.info("tssss"+(dFormat.format(perAPCalStart.getTime()) + " - " + dFormat.format(perAPCalEnd.getTime())));
                                                                 )
                                                                 */
                                                                cells.setCellValue(perAP);
                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Report Date"));
                                                                cells = rows.getCell(2);
                                                                String[] reportDatsplite = perAP.split("-");
                                                                logger.info(reportDatsplite[1].substring(1, reportDatsplite[1].length()));
                                                                cells.setCellValue(reportDatsplite[1].substring(1, reportDatsplite[1].length()));

                                                                jsonArrayData = (JSONArray) parser.parse(new FileReader(deviceSourcePath + "InverterSummary.json"));
                                                                logger.info(deviceSourcePath);
                                                                logger.info("Device Level InverterCurrentSnapShot Json size=========> " + jsonArrayData.size());
                                                                for (int l1 = 0; l1 < jsonArrayData.size(); l1++) {
                                                                    logger.info(jsonArrayData.size());
                                                                    logger.info("l1=======>" + l1);
                                                                    dataObj = (JSONObject) jsonArrayData.get(l1);
                                                                    rid = (String) dataObj.get("ResourceID");
                                                                    if (rid.equalsIgnoreCase(deviceName)) {
                                                                        value = (JSONArray) dataObj.get("Value");
                                                                        logger.info("val=======>" + value);
                                                                        logger.info("rid=======>" + rid);
                                                                        for (int v = 0; v < value.size(); v++) {
                                                                            va1ue1 = (JSONObject) value.get(v);
                                                                            name = (String) va1ue1.get("name");
                                                                            curVal = (String) va1ue1.get("curvalue");
                                                                            minVal = (String) va1ue1.get("minvalue");
                                                                            maxVal = (String) va1ue1.get("maxvalue");
                                                                            avg = (String) va1ue1.get("avgvalue");

                                                                            if (name.equalsIgnoreCase("PerformanceRatio")) {
                                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Performance Ratio"));
                                                                            } else if (name.equalsIgnoreCase("EnergyPerRatedPower")) {
                                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Energy/Rated power"));
                                                                            } else if (name.equalsIgnoreCase("Earning")) {
                                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Earnings"));
                                                                            } else if (name.equalsIgnoreCase("CarbonSaving")) {
                                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Carbon Savings"));
                                                                            } else if (name.equalsIgnoreCase("ACPower")) {
                                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "AC Power"));
                                                                            } else if (name.equalsIgnoreCase("Energy")) {
                                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Day energy"));
                                                                            } else if (name.equalsIgnoreCase("ACVoltage")) {
                                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "AC Voltage"));
                                                                            } else if (name.equalsIgnoreCase("DCVoltage")) {
                                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "DC Voltage"));
                                                                            } else if (name.equalsIgnoreCase("ACCurrent")) {
                                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "AC Current"));
                                                                            } else if (name.equalsIgnoreCase("DCCurrent")) {
                                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "DC Current"));
                                                                            } else {
                                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Frequency"));
                                                                            }
                                                                            cells = rows.getCell(2);
                                                                            if ((curVal.length() > 0 || minVal.length() > 0) || (maxVal.length() > 0 || avg.length() > 0)) {
                                                                                cells = rows.getCell(2);
                                                                                if (curVal.length() > 0) {
                                                                                    cells.setCellValue(Double.parseDouble(curVal));
                                                                                } else {
                                                                                    cells.setCellValue("-");
                                                                                }
                                                                                cells = rows.getCell(3);
                                                                                if (minVal.length() > 0) {

                                                                                    cells.setCellValue(Double.parseDouble(minVal));
                                                                                } else {
                                                                                    cells.setCellValue("-");
                                                                                }
                                                                                cells = rows.getCell(4);
                                                                                if (maxVal.length() > 0) {

                                                                                    cells.setCellValue(Double.parseDouble(maxVal));
                                                                                } else {
                                                                                    cells.setCellValue("-");
                                                                                }
                                                                                cells = rows.getCell(5);
                                                                                if (avg.length() > 0) {

                                                                                    cells.setCellValue(Double.parseDouble(avg));
                                                                                } else {
                                                                                    cells.setCellValue("-");
                                                                                }
                                                                            } else {
                                                                                cells = rows.getCell(2);
                                                                                cells.setCellValue("-");
                                                                                cells = rows.getCell(3);
                                                                                cells.setCellValue("-");
                                                                                cells = rows.getCell(4);
                                                                                cells.setCellValue("-");
                                                                                cells = rows.getCell(5);
                                                                                cells.setCellValue("-");
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                jsonArrayData = (JSONArray) parser.parse(new FileReader(deviceSourcePath + "AlertsByStatus" + File.separator + "InverterMetricTypes.json"));
                                                                logger.info("Json size=========> " + jsonArrayData.size());
                                                                for (int l1 = 0; l1 < jsonArrayData.size(); l1++) {
                                                                    logger.info("l1=======>" + l1);
                                                                    dataObj = (JSONObject) jsonArrayData.get(l1);
                                                                    value = (JSONArray) dataObj.get("ResourceValues");
                                                                    if (value == null) {
                                                                        logger.info("nulllll");
                                                                    } else if (!value.isEmpty()) {
                                                                        rows = sheet.getRow(rowIteratorMethod(sheet, "Number of open tickets"));
                                                                        cells = rows.getCell(2);
                                                                        if (value.size() > 4) {
                                                                            if (((String) value.get(4)).length() > 0) {
                                                                                cells.setCellValue(Double.parseDouble((String) value.get(4)));
                                                                            } else {
                                                                                cells.setCellValue("-");
                                                                            }
                                                                        }
                                                                    } else {
                                                                        cells.setCellValue("-");
                                                                    }

                                                                }
                                                                jsonArrayData = (JSONArray) parser.parse(new FileReader(installationSourcePath + "Weather.json"));
                                                                logger.info("Installation Weather Json size=========> " + jsonArrayData.size());
                                                                for (int l1 = 0; l1 < jsonArrayData.size(); l1++) {
                                                                    logger.info("l1=======>" + l1);

                                                                    dataObj = (JSONObject) jsonArrayData.get(l1);
                                                                    value = null;
                                                                    value = (JSONArray) dataObj.get("Forecast");
                                                                    if (value == null) {
                                                                    } else {
                                                                        logger.info("========>" + value.size());
                                                                        objDev1 = (JSONObject) value.get(0);
                                                                        objDev2 = (JSONObject) value.get(1);

                                                                        if (objDev1.get("name1").equals("Mon")) {
                                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Mon"));
                                                                            cells = rows.getCell(2);
                                                                            cells.setCellValue((String) objDev2.get("name1"));
                                                                        }
                                                                        if (objDev1.get("name2").equals("Tue")) {
                                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Tue"));
                                                                            cells = rows.getCell(2);
                                                                            cells.setCellValue((String) objDev2.get("name2"));
                                                                        }
                                                                        if (objDev1.get("name3").equals("Wed")) {
                                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Wed"));
                                                                            cells = rows.getCell(2);
                                                                            cells.setCellValue((String) objDev2.get("name3"));
                                                                        }
                                                                        if (objDev1.get("name4").equals("Thu")) {
                                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Thu"));
                                                                            cells = rows.getCell(2);
                                                                            cells.setCellValue((String) objDev2.get("name4"));
                                                                        }
                                                                        if (objDev1.get("name5").equals("Fri")) {
                                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Fri"));
                                                                            cells = rows.getCell(2);
                                                                            cells.setCellValue((String) objDev2.get("name5"));
                                                                        }
                                                                        if (objDev1.get("name6").equals("Sat")) {
                                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Sat"));
                                                                            cells = rows.getCell(2);
                                                                            cells.setCellValue((String) objDev2.get("name6"));
                                                                        }
                                                                        if (objDev1.get("name7").equals("Sun")) {
                                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Sun"));
                                                                            cells = rows.getCell(2);
                                                                            cells.setCellValue((String) objDev2.get("name7"));
                                                                        }
                                                                    }
                                                                }

                                                                urlString = resources + "GetJSONResourceConfig?customer=";
                                                                ParamValue = "ParamValue";
                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Device name"));
                                                                cells = rows.getCell(2);
                                                                cells.setCellValue(deviceName);
                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Device type (Inv, SC, etc)"));
                                                                cells = rows.getCell(2);
                                                                cells.setCellValue((String) list.get(i));
                                                                for (int e = 0; e < 7; e++) {
                                                                    if (e == 0) {
                                                                        url = urlString + key + "&service=" + subKey + "&resourceid=" + deviceName + "&paramname=Pmax";
                                                                    } else if (e == 1) {
                                                                        url = urlString + key + "&service=" + subKey + "&resourceid=" + deviceName + "&paramname=Model";
                                                                    } else if (e == 2) {
                                                                        url = urlString + key + "&service=" + subKey + "&resourceid=" + deviceName + "&paramname=Vendor";
                                                                    } else if (e == 3) {
                                                                        url = urlString + key + "&service=" + subKey + "&resourceid=" + deviceName + "&paramname=Pmax";
                                                                    } else if (e == 4) {
                                                                        url = urlString + key + "&service=" + subKey + "&resourceid=" + deviceName + "&paramname=ModuleEfficiency";
                                                                    } else if (e == 5) {
                                                                        url = urlString + key + "&service=" + subKey + "&resourceid=" + deviceName + "&paramname=PVPanelArea";
                                                                    } else {
                                                                        url = urlString + key + "&service=" + subKey + "&resourceid=" + deviceName + "&paramname=PVModules";
                                                                    }

                                                                    logger.info(url);
                                                                    jsonName = urlResopnes(url + "&subservice=Default");
                                                                    /* jUrl = new URL(url);
                                                                     conn = (HttpURLConnection) jUrl.openConnection();
                                                                     conn.setRequestMethod("POST");
                                                                     conn.setRequestProperty("Accept", "application/json");
                                                                     conn.connect();
                                                                     resopnse = conn.getInputStream();
                                                                     inputStrem = new InputStreamReader(resopnse);
                                                                     jsonInputStream = new BufferedReader(inputStrem);
                                                                     jsonName = "";

                                                                     while ((output = jsonInputStream.readLine()) != null) {
                                                                        
                                                                     jsonName = output;
                                                                     }*/
                                                                    logger.info("Output from Server .... " + jsonName);
                                                                    if (jsonName != null) {
                                                                        finalJson = jsonName.substring(jsonName.indexOf("["), jsonName.lastIndexOf("]") + 1);
                                                                        logger.info("FinalJson" + finalJson);

                                                                        jsonArrayData = (JSONArray) parser.parse(finalJson);
                                                                        //logger.info("jsonArray" + jsonArrayData.toJSONString());

                                                                        if (jsonArrayData != null) {
                                                                            logger.info("paramvalue=====>");
                                                                            rows = sheet.getRow(11 + e);
                                                                            cells = rows.getCell(2);
                                                                            if (jsonArrayData.size() > 0) {

                                                                                dataObj = (JSONObject) jsonArrayData.get(0);
                                                                                if (dataObj.get(ParamValue) instanceof Double) {
                                                                                    cells.setCellValue(Double.parseDouble((String) dataObj.get(ParamValue)));
                                                                                } else {
                                                                                    cells.setCellValue((String) dataObj.get(ParamValue));
                                                                                }
                                                                            } else {
                                                                                cells.setCellValue("-");
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                            } else if (sheetName.equalsIgnoreCase("Inv_Yield_History") && l == 0) {
                                                                jsonArrayData = (JSONArray) parser.parse(new FileReader(deviceSourcePath + "TimeLineDayEnergy" + File.separator + "InverterLineChartByTime.json"));
                                                                logger.info("Inv_Yield_History sheet array size=========> " + jsonArrayData.size());

                                                                for (int l1 = 0; l1 < jsonArrayData.size(); l1++) {
                                                                    logger.info("l1=======>" + l1);
                                                                    dataObj = (JSONObject) jsonArrayData.get(l1);
                                                                    logger.info("deviceName  =>" + deviceName);
                                                                    String resourceID = (String) dataObj.get("ResourceID");
                                                                    logger.info("resourceID  =>" + resourceID);
                                                                    if (resourceID.equalsIgnoreCase(deviceName)) {
                                                                        resourceNamesDev = (JSONArray) dataObj.get("ResourceNames");
                                                                        resourceValuesDev = (JSONArray) dataObj.get("ResourceValues");
                                                                        timeStampsDev = (JSONArray) dataObj.get("TimeStamps");

                                                                        //logger.info("serviceNameDev=======>" + resourceID);
                                                                        //logger.info("resourceNamesDev=======>" + resourceNamesDev);
                                                                        //logger.info("resourceValuesDev=======>" + resourceValuesDev.size());
                                                                        /*if (l1 == 0) {
                                                                         rowCountDev = 0;
                                                                         rowInst1 = sheet.createRow(rowCountDev++);
                                                                         cells = rowInst1.createCell(0);
                                                                         cells.setCellValue("Date");
                                                                         cells = rowInst1.createCell(1);
                                                                         cells.setCellValue(resourceID);
                                                                         }*/
                                                                        for (int r = 0; r < resourceNamesDev.size(); r++) {
                                                                            String resourceNamesDev1 = (String) resourceNamesDev.get(r);
                                                                            resourceValuesDev1 = (JSONArray) resourceValuesDev.get(r);
                                                                            timeStampsDev1 = (JSONArray) timeStampsDev.get(r);

                                                                            if (resourceNamesDev1.equalsIgnoreCase("DayEnergy")) {
                                                                                rowCountDev = 1;
                                                                                //logger.info(" metric " + resourceNamesDev1);
                                                                                //logger.info(" metric " + resourceValuesDev1);
                                                                                //logger.info(" metric " + timeStampsDev1);

                                                                                for (int u = 0; u < resourceValuesDev1.size(); u++) {
                                                                                    String timeStampsDev2 = (String) timeStampsDev1.get(u);
                                                                                    rowInst1 = sheet.createRow(rowCountDev++);
                                                                                    cells = rowInst1.createCell(0);
                                                                                    cells.setCellValue(timeStampsDev2);
                                                                                    cells = rowInst1.createCell(1);
                                                                                    // logger.info(resourceValuesDev1.get(u) + "==>" + u + "===>" + rowCountDev);
                                                                                    if (resourceValuesDev1.get(u) instanceof Long) {
                                                                                        cells.setCellValue((Long) resourceValuesDev1.get(u));
                                                                                    } else {
                                                                                        cells.setCellValue((Double) resourceValuesDev1.get(u));
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                            } else if (sheetName.equalsIgnoreCase("Inv.Energy_Consumed") && l == 0) {
                                                                jsonArrayData = (JSONArray) parser.parse(new FileReader(deviceSourcePath + "TimeLineDayEnergyConsumed" + File.separator + "InverterLineChartByTime.json"));
                                                                logger.info("Inv_Energy_Consumed sheet array size=========> " + jsonArrayData.size());
                                                                for (int l1 = 0; l1 < jsonArrayData.size(); l1++) {
                                                                    logger.info("l1=======>" + l1);
                                                                    dataObj = (JSONObject) jsonArrayData.get(l1);
                                                                    logger.info("deviceName  =>" + deviceName);
                                                                    String resourceID = (String) dataObj.get("ResourceID");
                                                                    logger.info("resourceID  =>" + resourceID);
                                                                    if (resourceID.equalsIgnoreCase(deviceName)) {
                                                                        resourceNamesDev = (JSONArray) dataObj.get("ResourceNames");
                                                                        resourceValuesDev = (JSONArray) dataObj.get("ResourceValues");
                                                                        timeStampsDev = (JSONArray) dataObj.get("TimeStamps");

                                                                        //logger.info("serviceNameDev=======>" + resourceID);
                                                                        //logger.info("resourceNamesDev=======>" + resourceNamesDev);
                                                                        //logger.info("resourceValuesDev=======>" + resourceValuesDev.size());
                                                                        /*if (l1 == 0) {
                                                                         rowCountDev = 0;
                                                                         rowInst1 = sheet.createRow(rowCountDev++);
                                                                         cells = rowInst1.createCell(0);
                                                                         cells.setCellValue("Date");
                                                                         cells = rowInst1.createCell(1);
                                                                         cells.setCellValue(resourceID);
                                                                         }*/
                                                                        for (int r = 0; r < resourceNamesDev.size(); r++) {
                                                                            String resourceNamesDev1 = (String) resourceNamesDev.get(r);
                                                                            resourceValuesDev1 = (JSONArray) resourceValuesDev.get(r);
                                                                            timeStampsDev1 = (JSONArray) timeStampsDev.get(r);

                                                                            if (resourceNamesDev1.equalsIgnoreCase("EnergyConsumed")) {
                                                                                rowCountDev = 1;
                                                                                //logger.info(" metric " + resourceNamesDev1);
                                                                                //logger.info(" metric " + resourceValuesDev1);
                                                                                //logger.info(" metric " + timeStampsDev1);

                                                                                for (int u = 0; u < resourceValuesDev1.size(); u++) {
                                                                                    String timeStampsDev2 = (String) timeStampsDev1.get(u);
                                                                                    rowInst1 = sheet.createRow(rowCountDev++);
                                                                                    cells = rowInst1.createCell(0);
                                                                                    cells.setCellValue(timeStampsDev2);
                                                                                    cells = rowInst1.createCell(1);
                                                                                    // logger.info(resourceValuesDev1.get(u) + "==>" + u + "===>" + rowCountDev);
                                                                                    if (resourceValuesDev1.get(u) instanceof Long) {
                                                                                        cells.setCellValue((Long) resourceValuesDev1.get(u));
                                                                                    } else {
                                                                                        cells.setCellValue((Double) resourceValuesDev1.get(u));
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                            } else if (sheetName.equalsIgnoreCase("Inv.Net_Energy") && l == 0) {
                                                                jsonArrayData = (JSONArray) parser.parse(new FileReader(deviceSourcePath + "TimeLineNetEnergy" + File.separator + "InverterLineChartByTime.json"));
                                                                logger.info("Inv_Net_Energy sheet array size=========> " + jsonArrayData.size());
                                                                for (int l1 = 0; l1 < jsonArrayData.size(); l1++) {
                                                                    logger.info("l1=======>" + l1);
                                                                    dataObj = (JSONObject) jsonArrayData.get(l1);
                                                                    logger.info("deviceName  =>" + deviceName);
                                                                    String resourceID = (String) dataObj.get("ResourceID");
                                                                    logger.info("resourceID  =>" + resourceID);
                                                                    if (resourceID.equalsIgnoreCase(deviceName)) {
                                                                        resourceNamesDev = (JSONArray) dataObj.get("ResourceNames");
                                                                        resourceValuesDev = (JSONArray) dataObj.get("ResourceValues");
                                                                        timeStampsDev = (JSONArray) dataObj.get("TimeStamps");

                                                                        //logger.info("serviceNameDev=======>" + resourceID);
                                                                        //logger.info("resourceNamesDev=======>" + resourceNamesDev);
                                                                        //logger.info("resourceValuesDev=======>" + resourceValuesDev.size());
                                                                        /*if (l1 == 0) {
                                                                         rowCountDev = 0;
                                                                         rowInst1 = sheet.createRow(rowCountDev++);
                                                                         cells = rowInst1.createCell(0);
                                                                         cells.setCellValue("Date");
                                                                         cells = rowInst1.createCell(1);
                                                                         cells.setCellValue(resourceID);
                                                                         }*/
                                                                        for (int r = 0; r < resourceNamesDev.size(); r++) {
                                                                            String resourceNamesDev1 = (String) resourceNamesDev.get(r);
                                                                            resourceValuesDev1 = (JSONArray) resourceValuesDev.get(r);
                                                                            timeStampsDev1 = (JSONArray) timeStampsDev.get(r);

                                                                            if (resourceNamesDev1.equalsIgnoreCase("NetEnergy")) {
                                                                                rowCountDev = 1;
                                                                                //logger.info(" metric " + resourceNamesDev1);
                                                                                //logger.info(" metric " + resourceValuesDev1);
                                                                                //logger.info(" metric " + timeStampsDev1);

                                                                                for (int u = 0; u < resourceValuesDev1.size(); u++) {
                                                                                    String timeStampsDev2 = (String) timeStampsDev1.get(u);
                                                                                    rowInst1 = sheet.createRow(rowCountDev++);
                                                                                    cells = rowInst1.createCell(0);
                                                                                    cells.setCellValue(timeStampsDev2);
                                                                                    cells = rowInst1.createCell(1);
                                                                                    // logger.info(resourceValuesDev1.get(u) + "==>" + u + "===>" + rowCountDev);
                                                                                    if (resourceValuesDev1.get(u) instanceof Long) {
                                                                                        cells.setCellValue((Long) resourceValuesDev1.get(u));
                                                                                    } else {
                                                                                        cells.setCellValue((Double) resourceValuesDev1.get(u));
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                            } else if (sheetName.equalsIgnoreCase("Inv_ACPower") && l == 0) {
                                                                jsonArrayData = (JSONArray) parser.parse(new FileReader(deviceSourcePath + "TimeLineACPower" + File.separator + "InverterLineChartByTime.json"));
                                                                logger.info("Inv_ACPower sheet array size=========> " + jsonArrayData.size());

                                                                for (int l1 = 0; l1 < jsonArrayData.size(); l1++) {
                                                                    logger.info("l1=======>" + l1);
                                                                    dataObj = (JSONObject) jsonArrayData.get(l1);
                                                                    logger.info("deviceName  =>" + deviceName);
                                                                    String resourceID = (String) dataObj.get("ResourceID");
                                                                    logger.info("resourceID  =>" + resourceID);
                                                                    if (resourceID.equalsIgnoreCase(deviceName)) {
                                                                        resourceNamesDev = (JSONArray) dataObj.get("ResourceNames");
                                                                        resourceValuesDev = (JSONArray) dataObj.get("ResourceValues");
                                                                        timeStampsDev = (JSONArray) dataObj.get("TimeStamps");

                                                                        //logger.info("serviceNameDev=======>" + resourceID);
                                                                        //logger.info("resourceNamesDev=======>" + resourceNamesDev);
                                                                        //logger.info("resourceValuesDev=======>" + resourceValuesDev.size());
                                                                       /* if (l1 == 0) {
                                                                         rowCountDev = 0;
                                                                         rowInst1 = sheet.createRow(rowCountDev++);
                                                                         cells = rowInst1.createCell(0);
                                                                         cells.setCellValue("Date");
                                                                         cells = rowInst1.createCell(1);
                                                                         cells.setCellValue(resourceID);
                                                                         }*/
                                                                        for (int r = 0; r < resourceNamesDev.size(); r++) {
                                                                            String resourceNamesDev1 = (String) resourceNamesDev.get(r);
                                                                            resourceValuesDev1 = (JSONArray) resourceValuesDev.get(r);
                                                                            timeStampsDev1 = (JSONArray) timeStampsDev.get(r);

                                                                            if (resourceNamesDev1.equalsIgnoreCase("ACPower")) {
                                                                                rowCountDev = 1;
                                                                                // logger.info(" metric " + resourceNamesDev1);
                                                                                //logger.info(" metric " + resourceValuesDev1);
                                                                                //logger.info(" metric " + timeStampsDev1);

                                                                                for (int u = 0; u < resourceValuesDev1.size(); u++) {
                                                                                    String timeStampsDev2 = (String) timeStampsDev1.get(u);
                                                                                    rowInst1 = sheet.createRow(rowCountDev++);
                                                                                    cells = rowInst1.createCell(0);
                                                                                    cells.setCellValue(timeStampsDev2);
                                                                                    cells = rowInst1.createCell(1);
                                                                                    // logger.info(resourceValuesDev1.get(u) + "==>" + u + "===>" + rowCountDev);
                                                                                    if (resourceValuesDev1.get(u) instanceof Long) {
                                                                                        cells.setCellValue((Long) resourceValuesDev1.get(u));
                                                                                    } else {
                                                                                        cells.setCellValue((Double) resourceValuesDev1.get(u));
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                            } else if (sheetName.equalsIgnoreCase("Inv_Performance_Trend") || sheetName.equalsIgnoreCase("Inv.Energy_PerRatedPower_Tren")) {
                                                                logger.info("Device Level Json array size=========> " + jsonArray.size());
                                                                if (l == 0) {
                                                                    rowCountDev = 1;
                                                                }
                                                                String resourceID = (String) obj.get("ResourceID");
                                                                logger.info("deviceName  =>" + deviceName);
                                                                logger.info("resourceID  =>" + resourceID);
                                                                if (resourceID.equalsIgnoreCase(deviceName)) {
                                                                    resourceNamesDev = (JSONArray) obj.get("ResourceNames");
                                                                    resourceValuesDev = (JSONArray) obj.get("ResourceValues");
                                                                    timeStampsDev = (JSONArray) obj.get("TimeStamps");
                                                                    // logger.info("resourceNamesDev=======>" + resourceNamesDev);
                                                                    //logger.info("resourceValuesDev=======>" + resourceValuesDev);
                                                                    //logger.info("resourceValuesDev=======>" + resourceValuesDev.size());
                                                                    for (int r = 0; r < resourceNamesDev.size(); r++) {
                                                                        String resourceNamesDev1 = (String) resourceNamesDev.get(r);
                                                                        resourceValuesDev1 = (JSONArray) resourceValuesDev.get(r);
                                                                        timeStampsDev1 = (JSONArray) timeStampsDev.get(r);
                                                                        String rName;

                                                                        if (sheetName.equalsIgnoreCase("Inv_Performance_Trend")) {
                                                                            rName = "PerformanceRatio";
                                                                        } else {
                                                                            rName = "EnergyPerRatedPower";
                                                                        }
                                                                        if (resourceNamesDev1.equalsIgnoreCase(rName)) {
                                                                            for (int u = 0; u < resourceValuesDev1.size(); u++) {
                                                                                String timeStampsDev2 = (String) timeStampsDev1.get(u);
                                                                                rowInst1 = sheet.createRow(rowCountDev++);
                                                                                cells = rowInst1.createCell(0);
                                                                                cells.setCellValue(timeStampsDev2);
                                                                                cells = rowInst1.createCell(1);
                                                                                // logger.info("iffff");
                                                                                if (resourceValuesDev1.get(u) instanceof Long) {
                                                                                    cells.setCellValue((Long) resourceValuesDev1.get(u));
                                                                                } else if (resourceValuesDev1.get(u) instanceof String) {
                                                                                    cells.setCellValue((Double) Double.parseDouble((String) resourceValuesDev1.get(u)));
                                                                                } else {
                                                                                    cells.setCellValue((Double) resourceValuesDev1.get(u));
                                                                                }

                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }//end l      
                                                    }//end of ss

                                                    fileOutputStream = new FileOutputStream(destiFile + "Summary.xlsx");
                                                    wbDB.write(fileOutputStream);
                                                    logger.info("data writen sucessfully");
                                                }

                                            }
                                        }
                                    }
                                    if (y == 0 || y == 1) {
                                        logger.info("array size=========> " + jsonArray.size());
                                        templateStream = new FileInputStream(xlsxFile);
                                        wbDB = new XSSFWorkbook(templateStream);
                                        //loading json one by one
                                        logger.info("File contain no of sheet : " + wbDB.getNumberOfSheets());

                                        int sheetIndex = 0;
                                        for (int s = 0; s < wbDB.getNumberOfSheets(); s++) {
                                            sheet = wbDB.getSheetAt(s);
                                            sheetName = sheet.getSheetName();
                                            logger.info("sheet no " + s + " Sheet name is " + sheetName);

                                            int rowCount = 1;
                                            int rowCountCust = 1;
                                            int rowCountInst = 0;
                                            int rowCountInst1 = 0;
                                            String ServiceName;
                                            double values;
                                            for (int l = 0; l < jsonArray.size(); l++) {
                                                logger.info("l================================================" + l);
                                                obj = (JSONObject) jsonArray.get(l);
                                                if ((y == 0 || y == 1)) {
                                                    logger.info("y================================================" + y);
                                                    values = 0.0;
                                                    ServiceName = (String) obj.get("ServiceName");
                                                    if ((sheetName.equalsIgnoreCase("Summary") && l == 0)) {
                                                        if (y == 0) {
                                                            jsonArrayData = (JSONArray) parser.parse(new FileReader(custSourcePath + "Summary.json"));
                                                            logger.info("Customer sheet Json size=========> " + jsonArrayData.size());
                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Report Name"));
                                                            cells = rows.getCell(2);
                                                            cells.setCellValue(key + " " + spiltePeriods[j] + " Report");
                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Customer Name"));
                                                            cells = rows.getCell(2);
                                                            cells.setCellValue(key);
                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Number of Installations"));
                                                            cells = rows.getCell(2);
                                                            cells.setCellValue(subKeys.size());
                                                            String installNames = "";
                                                            for (String s2 : subKeys) {
                                                                installNames += s2 + ",";
                                                            }
                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Installation Names"));
                                                            cells = rows.getCell(2);
                                                            cells.setCellValue(installNames.substring(0, installNames.length() - 1));
                                                        } else if (y == 1) {
                                                            jsonArrayData = (JSONArray) parser.parse(new FileReader(installationSourcePath + "Summary.json"));
                                                            logger.info("Installation sheet Json size=========> " + jsonArrayData.size());
                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Report Name"));
                                                            cells = rows.getCell(2);
                                                            cells.setCellValue(key + "-" + subKey + "-" + spiltePeriods[j] + " Report");
                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Customer Name"));
                                                            cells = rows.getCell(2);
                                                            cells.setCellValue(key);
                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Installation Name"));
                                                            cells = rows.getCell(2);
                                                            cells.setCellValue(subKey);
                                                        }

                                                        //if(zDate.equalsIgnoreCase(dFormat.format(perAPCalStart.getTime()).substring(0,6)));
                                                        //Applying the Date cell style to a cell
                                                        rows = sheet.getRow(rowIteratorMethod(sheet, "Performance analysis period"));
                                                        cells = rows.getCell(2);

                                                        String zdatePeriod = spiltePeriods[j];
                                                        logger.info(zDate);

                                                        String perAP = performanceAnalysisPeriod(zDate, zdatePeriod);
                                                        logger.info(perAP);

                                                        /*   
                                                         String[] zDateSplite=zDate.split("-");
                                                         SimpleDateFormat checkDate = new SimpleDateFormat("MM-dd-yyyy");
                                                         perAPCalStart = Calendar.getInstance();
                                                         if(zDate.equalsIgnoreCase(checkDate.format(perAPCalStart.getTime()))){
                                                         logger.info("Current day");
                                                           
                                                         }else{
                                                         logger.info("differnt day"+Integer.parseInt(zDateSplite[2]));
                                                         logger.info("differnt day"+Integer.parseInt(zDateSplite[1]));
                                                         logger.info("differnt day"+Integer.parseInt(zDateSplite[0]));
                                                         perAPCalStart.set(Integer.parseInt(zDateSplite[2]),(Integer.parseInt(zDateSplite[0])-1),Integer.parseInt(zDateSplite[1]));
                                                         perAPCalEnd.set(Integer.parseInt(zDateSplite[2]),(Integer.parseInt(zDateSplite[0])-1),Integer.parseInt(zDateSplite[1]));
                                                         logger.info("differnt day"+perAPCalStart.getTime());
                                                         if (spiltePeriods[j].equalsIgnoreCase("Hour")) {
                                                         perAPCalEnd.set(Calendar.MINUTE,59);
                                                         perAPCalEnd.set(Calendar.SECOND,59);
                                                              
                                                         }else{
                                                         perAPCalEnd.set(Calendar.HOUR_OF_DAY,23);
                                                         perAPCalEnd.set(Calendar.MINUTE,59);
                                                         perAPCalEnd.set(Calendar.SECOND,59);
                                                              
                                                         }
                                                         }
                                                       
                                                         if (spiltePeriods[j].equalsIgnoreCase("Hour")) {
                                                         perAPCalStart.set(Calendar.MINUTE,0);
                                                         perAPCalStart.set(Calendar.SECOND,0);
                                                                  
                                                         }else{
                                                         perAPCalStart.set(Calendar.HOUR_OF_DAY,00);
                                                         perAPCalStart.set(Calendar.MINUTE ,00);
                                                         perAPCalStart.set(Calendar.SECOND,00);
                                                                   
                                                         }
                                                       
                                                         if (spiltePeriods[j].equalsIgnoreCase("Hour")) {
                                                         perAPCalStart.add(Calendar.HOUR, -1);
                                                         } else if (spiltePeriods[j].equalsIgnoreCase("Day")) {
                                                         //perAPCalStart.add(Calendar.DATE, -1);
                                                         } else if (spiltePeriods[j].equalsIgnoreCase("Week")) {
                                                           
                                                         perAPCalStart.add(Calendar.DATE, -((perAPCalEnd.get(Calendar.DAY_OF_WEEK))-1));
                                                         } else if (spiltePeriods[j].equalsIgnoreCase("Month")) {
                                                         perAPCalStart.add(Calendar.MONTH, -1);
                                                         } else if (spiltePeriods[j].equalsIgnoreCase("year")) {
                                                         perAPCalStart.add(Calendar.YEAR, -1);
                                                         }
                                                         logger.info((dFormat.format(perAPCalStart.getTime()) + " - " + dFormat.format(perAPCalEnd.getTime())));
                                                         System.exit(0);*/
                                                        cells.setCellValue(perAP);

                                                        rows = sheet.getRow(rowIteratorMethod(sheet, "Report Date"));
                                                        cells = rows.getCell(2);
                                                        String[] reportDatsplite = perAP.split("-");
                                                        logger.info(reportDatsplite[1].substring(1, reportDatsplite[1].length()));
                                                        cells.setCellValue(reportDatsplite[1].substring(1, reportDatsplite[1].length()));

                                                        int cc = 2;
                                                        for (int l1 = 0; l1 < jsonArrayData.size(); l1++) {
                                                            logger.info("l1=======>" + l1);
                                                            dataObj = (JSONObject) jsonArrayData.get(l1);

                                                            if (y == 0) {

                                                                rid = (String) dataObj.get("ResourceID");
                                                                value = (JSONArray) dataObj.get("Value");
                                                                logger.info("val=======>" + value);
                                                                logger.info("rid=======>" + rid);
                                                                rows = sheet.createRow((rowIteratorMethod(sheet, "Plants") + 1 + l1));
                                                                cells = rows.createCell(1);
                                                                cells.setCellValue(rid);
                                                                int val = 0;
                                                                for (int v = 0; v < value.size(); v++) {
                                                                    va1ue1 = (JSONObject) value.get(v);
                                                                    name = (String) va1ue1.get("name");
                                                                    curVal = (String) va1ue1.get("curvalue");
                                                                    minVal = (String) va1ue1.get("minvalue");
                                                                    maxVal = (String) va1ue1.get("maxvalue");
                                                                    avg = (String) va1ue1.get("avgvalue");

                                                                    //logger.info(rid);
                                                                    XSSFCellStyle cs = wbDB.createCellStyle();

                                                                    if ((curVal.length() > 0 || minVal.length() > 0) || (maxVal.length() > 0 || avg.length() > 0)) {

                                                                        if (name.equalsIgnoreCase("PerformanceRatio")) {
                                                                            val = 2;
                                                                        } else if (name.equalsIgnoreCase("EnergyPerRatedPower")) {
                                                                            val = 6;
                                                                        } else if (name.equalsIgnoreCase("Earning")) {
                                                                            val = 10;
                                                                        } else if (name.equalsIgnoreCase("CarbonSaving")) {
                                                                            val = 14;
                                                                        } else if (name.equalsIgnoreCase("ACPower")) {
                                                                            val = 22;
                                                                        } else {
                                                                            val = 18;
                                                                        }

                                                                        cells = rows.createCell(val);
                                                                        //logger.info(39 + l1);
                                                                        logger.info("cell val" + val);
                                                                        if (curVal.length() > 0) {
                                                                            cells.setCellValue(Double.parseDouble(curVal));
                                                                            logger.info(curVal);
                                                                        } else {
                                                                            cells.setCellValue("-");
                                                                        }
                                                                        cells = rows.createCell(val + 1);
                                                                        //logger.info(39 + l1);
                                                                        //logger.info(val + 1);
                                                                        if (minVal.length() > 0) {
                                                                            cells.setCellValue(Double.parseDouble(minVal));
                                                                            //logger.info(minVal);
                                                                        } else {
                                                                            cells.setCellValue("-");
                                                                        }

                                                                        cells = rows.createCell(val + 2);
                                                                        //logger.info(39 + l1);
                                                                        //logger.info(val + 2);
                                                                        if (maxVal.length() > 0) {
                                                                            cells.setCellValue(Double.parseDouble(maxVal));
                                                                            //logger.info(maxVal);
                                                                        } else {
                                                                            cells.setCellValue("-");
                                                                        }
                                                                        cells = rows.createCell(val + 3);
                                                                        //logger.info(39 + l1);
                                                                        logger.info(val + 3);
                                                                        if (avg.length() > 0) {
                                                                            cells.setCellValue(Double.parseDouble(avg));
                                                                            //logger.info(avg);
                                                                        } else {
                                                                            cells.setCellValue("-");
                                                                        }
                                                                    } else {
                                                                        cells = rows.createCell(cc++);
                                                                        cells.setCellValue("-");

                                                                        cells = rows.createCell(cc++);
                                                                        cells.setCellValue("-");

                                                                        cells = rows.createCell(cc++);
                                                                        cells.setCellValue("-");

                                                                        cells = rows.createCell(cc++);
                                                                        cells.setCellValue("-");

                                                                    }
                                                                }
                                                            } else if (y == 1) {

                                                                name = (String) dataObj.get("name");
                                                                curVal = (String) dataObj.get("curvalue");
                                                                minVal = (String) dataObj.get("minvalue");
                                                                maxVal = (String) dataObj.get("maxvalue");
                                                                avg = (String) dataObj.get("avgvalue");
                                                                logger.info("name" + name);
                                                                logger.info("curVal" + curVal);
                                                                logger.info("minVal" + minVal);
                                                                logger.info("maxVal" + maxVal);
                                                                logger.info("avg" + avg);

                                                                if (name.equalsIgnoreCase("PerformanceRatio")) {
                                                                    rows = sheet.getRow(rowIteratorMethod(sheet, "Performance Ratio"));
                                                                } else if (name.equalsIgnoreCase("EnergyPerRatedPower")) {
                                                                    rows = sheet.getRow(rowIteratorMethod(sheet, "Energy/Rated power"));
                                                                } else if (name.equalsIgnoreCase("Earning")) {
                                                                    rows = sheet.getRow(rowIteratorMethod(sheet, "Earnings"));
                                                                } else if (name.equalsIgnoreCase("CarbonSaving")) {
                                                                    rows = sheet.getRow(rowIteratorMethod(sheet, "Carbon Savings"));
                                                                } else if (name.equalsIgnoreCase("ACPower")) {
                                                                    rows = sheet.getRow(rowIteratorMethod(sheet, "ACPower"));
                                                                } else {
                                                                    rows = sheet.getRow(rowIteratorMethod(sheet, "Day energy"));
                                                                }

                                                                if ((curVal.length() > 0 || minVal.length() > 0) || (maxVal.length() > 0 || avg.length() > 0)) {
                                                                    cc = 2;
                                                                    cells = rows.getCell(2);
                                                                    logger.info(curVal);

                                                                    if (curVal.length() > 0 && !(curVal.equalsIgnoreCase("null"))) {

                                                                        cells.setCellValue(Double.parseDouble(curVal));
                                                                    } else {
                                                                        cells.setCellValue("-");

                                                                    }
                                                                    cells = rows.getCell(3);
                                                                    //logger.info(cc);

                                                                    if (minVal.length() > 0) {

                                                                        cells.setCellValue(Double.parseDouble(minVal));
                                                                    } else {
                                                                        cells.setCellValue("-");
                                                                    }

                                                                    cells = rows.getCell(4);
                                                                    //logger.info(cc);
                                                                    if (maxVal.length() > 0) {

                                                                        cells.setCellValue(Double.parseDouble(maxVal));
                                                                    } else {
                                                                        cells.setCellValue("-");

                                                                    }
                                                                    cells = rows.getCell(5);
                                                                    //logger.info(cc);
                                                                    if (avg.length() > 0) {

                                                                        cells.setCellValue(Double.parseDouble(avg));
                                                                    } else {
                                                                        cells.setCellValue("-");
                                                                    }

                                                                } else {
                                                                    cells = rows.getCell(2);
                                                                    cells.setCellValue("-");

                                                                    cells = rows.getCell(3);
                                                                    cells.setCellValue("-");

                                                                    cells = rows.getCell(4);
                                                                    cells.setCellValue("-");

                                                                    cells = rows.getCell(5);
                                                                    cells.setCellValue("-");

                                                                }

                                                            }
                                                        }
                                                        int eval = 0;
                                                        if (y == 0) {
                                                            jsonArrayData = (JSONArray) parser.parse(new FileReader(custSourcePath + "AlertsByStatus.json"));
                                                            eval = 4;
                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Number of open tickets"));
                                                            cells = rows.getCell(2);
                                                        } else if (y == 1) {
                                                            jsonArrayData = (JSONArray) parser.parse(new FileReader(installationSourcePath + "AlertsByStatus.json"));
                                                            eval = 6;
                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Number of open tickets"));
                                                            cells = rows.getCell(2);
                                                        }
                                                        logger.info("Json size=========> " + jsonArrayData.size());
                                                        for (int l1 = 0; l1 < jsonArrayData.size(); l1++) {
                                                            logger.info("l1=======>" + l1);
                                                            dataObj = (JSONObject) jsonArrayData.get(l1);
                                                            value = (JSONArray) dataObj.get("ResourceValues");
                                                            if (value.size() > 4) {
                                                                if (((String) value.get(4)).length() > 0) {
                                                                    cells.setCellValue(Double.parseDouble((String) value.get(4)));
                                                                } else {
                                                                    cells.setCellValue("-");
                                                                }
                                                            } else {
                                                                // System.exit(0);
                                                                cells.setCellValue("-");
                                                                logger.info("nullll size=========> ");
                                                            }

                                                        }
                                                        urlString = resources + "GetJSONResourceConfig?customer=";
                                                        ParamValue = "ParamValue";
                                                        for (int e = 0; e < eval; e++) {
                                                            if (y == 0) {
                                                                if (e == 0) {
                                                                    url = urlString + key + "&paramname=InstalledCapacity";
                                                                } else if (e == 1) {
                                                                    url = urlString + key + "&paramname=Earnings/kWh";
                                                                } else if (e == 2) {
                                                                    url = urlString + key + "&paramname=Currency";
                                                                } else {
                                                                    url = urlString + key + "&paramname=CO2Factor";
                                                                }
                                                            } else if (y == 1) {
                                                                if (e == 0) {
                                                                    url = urlString + key + "&service=" + subKey + "&paramname=InstalledCapacity";
                                                                } else if (e == 1) {
                                                                    url = urlString + key + "&service=" + subKey + "&paramname=Latitude";
                                                                } else if (e == 2) {
                                                                    url = urlString + key + "&service=" + subKey + "&paramname=Longitude";
                                                                } else if (e == 3) {
                                                                    url = urlString + key + "&service=" + subKey + "&paramname=IrradianceDay";
                                                                } else if (e == 4) {
                                                                    url = urlString + key + "&service=" + subKey + "&paramname=IrradianceMonth";
                                                                } else {
                                                                    url = urlString + key + "&service=" + subKey + "&paramname=IrradianceYear";
                                                                }
                                                            } else {

                                                                if (e == 0) {
                                                                    url = urlString + key + "&service=" + subKey;
                                                                }

                                                            }

                                                            logger.info(url);

                                                            jsonName = urlResopnes(url + "&subservice=Default");
                                                            finalJson = "";
                                                            logger.info("Output from Server .... " + jsonName);
                                                            if (jsonName != null) {
                                                                finalJson = jsonName.substring(jsonName.indexOf("["), jsonName.lastIndexOf("]") + 1);
                                                                logger.info("FinalJson" + finalJson);

                                                                jsonArrayData = (JSONArray) parser.parse(finalJson);
                                                                logger.info("jsonArray" + jsonArrayData.toJSONString());

                                                                if (jsonArrayData != null) {
                                                                    logger.info("paramvalue=====>");
                                                                    rows = sheet.getRow(10 + e);
                                                                    cells = rows.getCell(2);
                                                                    if (jsonArrayData.size() > 0) {
                                                                        dataObj = (JSONObject) jsonArrayData.get(0);
                                                                        if (dataObj.get(ParamValue) instanceof Double) {
                                                                            cells.setCellValue(Double.parseDouble((String) dataObj.get(ParamValue)));
                                                                        } else {
                                                                            cells.setCellValue((String) dataObj.get(ParamValue));
                                                                        }
                                                                    } else {
                                                                        cells.setCellValue("-");
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (y == 1) {
                                                            jsonArrayData = (JSONArray) parser.parse(new FileReader(installationSourcePath + "PerformanceRatioComparison.json"));
                                                            rows = sheet.getRow(9);
                                                            cells = rows.getCell(2);
                                                            cells.setCellValue(jsonArrayData.size());

                                                            jsonArrayData = (JSONArray) parser.parse(new FileReader(installationSourcePath + "Weather.json"));
                                                            logger.info("Installation Weather Json size=========> " + jsonArrayData.size());
                                                            for (int l1 = 0; l1 < jsonArrayData.size(); l1++) {
                                                                logger.info("l1=======>" + l1);
                                                                dataObj = (JSONObject) jsonArrayData.get(l1);
                                                                value = null;
                                                                if (l1 == 0) {
                                                                    value = (JSONArray) dataObj.get("Now");
                                                                    if (!value.isEmpty()) {
                                                                        for (int t = 0; t < value.size(); t++) {

                                                                            objCust1 = (JSONObject) value.get(t);
                                                                            logger.info(objCust1.get("name"));
                                                                            if (objCust1.get("name").equals("Module_Temp")) {
                                                                                logger.info("l1=======>" + t);

                                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Module Temperature"));

                                                                            } else if (objCust1.get("name").equals("Irradiation")) {
                                                                                logger.info("l1=======>" + t);
                                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Irridiance"));
                                                                            } else if (objCust1.get("name").equals("Ambient_Temp")) {
                                                                                logger.info("l1=======>" + t);
                                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Ambient Temperature"));
                                                                            } else if (objCust1.get("name").equals("Wind_Speed")) {
                                                                                logger.info("l1=======>" + t);
                                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Wind speed"));
                                                                            } else {
                                                                                logger.info("else" + t);
                                                                                rows = sheet.getRow(rowIteratorMethod(sheet, "Temperature"));
                                                                            }
                                                                            cells = rows.getCell(2);
                                                                            cells.setCellValue(Double.parseDouble((String) objCust1.get("value")));
                                                                            logger.info(objCust1.get("value"));
                                                                        }
                                                                    }
                                                                } else {
                                                                    value = (JSONArray) dataObj.get("Forecast");
                                                                    if (!value.isEmpty()) {
                                                                        logger.info("========>" + value.size());
                                                                        objCust1 = (JSONObject) value.get(0);
                                                                        objCust2 = (JSONObject) value.get(1);

                                                                        logger.info(objCust2.get("name1"));
                                                                        logger.info(objCust1.get("name1"));
                                                                        if (objCust1.get("name1").equals("Mon")) {
                                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Mon"));
                                                                            cells = rows.getCell(2);
                                                                            cells.setCellValue((String) objCust2.get("name1"));
                                                                        }

                                                                        if (objCust1.get("name2").equals("Tue")) {
                                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Tue"));
                                                                            cells = rows.getCell(2);
                                                                            cells.setCellValue((String) objCust2.get("name2"));
                                                                        }
                                                                        if (objCust1.get("name3").equals("Wed")) {
                                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Wed"));
                                                                            cells = rows.getCell(2);
                                                                            cells.setCellValue((String) objCust2.get("name3"));
                                                                        }
                                                                        if (objCust1.get("name4").equals("Thu")) {
                                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Thu"));
                                                                            cells = rows.getCell(2);
                                                                            cells.setCellValue((String) objCust2.get("name4"));
                                                                        }
                                                                        if (objCust1.get("name5").equals("Fri")) {
                                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Fri"));
                                                                            cells = rows.getCell(2);
                                                                            cells.setCellValue((String) objCust2.get("name5"));
                                                                        }
                                                                        if (objCust1.get("name6").equals("Sat")) {
                                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Sat"));
                                                                            cells = rows.getCell(2);
                                                                            cells.setCellValue((String) objCust2.get("name6"));
                                                                        }
                                                                        if (objCust1.get("name7").equals("Sun")) {
                                                                            rows = sheet.getRow(rowIteratorMethod(sheet, "Sun"));
                                                                            cells = rows.getCell(2);
                                                                            cells.setCellValue((String) objCust2.get("name7"));
                                                                        }

                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else if (sheetName.equalsIgnoreCase("Comparative_Plant_Yield")) {
                                                        sheetIndex = s;
                                                        logger.info(obj);

                                                        String dayEnergy = (String) obj.get("ActualEnergy");

                                                        if (dayEnergy.equalsIgnoreCase("")) {
                                                            logger.info("act " + dayEnergy);
                                                            values = 0.0;

                                                        } else {
                                                            logger.info("elseact " + dayEnergy);
                                                            values = Double.parseDouble(dayEnergy);
                                                        }
                                                        logger.info("act " + values);

                                                    } else if (sheetName.equalsIgnoreCase("Comparative_Plant_Performance")) {
                                                        sheetIndex = s;
                                                        String performanceRatio = (String) obj.get("PerformanceRatio");
                                                        if (performanceRatio.equalsIgnoreCase("")) {
                                                            logger.info("act " + performanceRatio);
                                                            values = 0.0;
                                                        } else {
                                                            logger.info("elseact " + performanceRatio);
                                                            values = Double.parseDouble(performanceRatio);
                                                        }
                                                        logger.info("per " + values);
                                                    } else if (sheetName.equalsIgnoreCase("Comparative_Energy_to_Rated_Pow")) {
                                                        sheetIndex = s;
                                                        String EnergyPerRatedPower = (String) obj.get("EnergyPerRatedPower");
                                                        if (EnergyPerRatedPower.equalsIgnoreCase("")) {
                                                            logger.info("act " + EnergyPerRatedPower);
                                                            values = 0.0;
                                                        } else {
                                                            logger.info("elseact " + EnergyPerRatedPower);
                                                            values = Double.parseDouble(EnergyPerRatedPower);
                                                        }
                                                        logger.info("energy " + values);
                                                    }
                                                    if ((sheetName.equalsIgnoreCase("Comparative_Plant_Yield") || sheetName.equalsIgnoreCase("Comparative_Plant_Performance")) || sheetName.equalsIgnoreCase("Comparative_Energy_to_Rated_Pow")) {
                                                        sheet = wbDB.getSheetAt(sheetIndex);
                                                        logger.info(sheet.getSheetName() + " sheet is available");
                                                        logger.info("Writing on existing Sheet ...." + sheet.getSheetName());
                                                        row = sheet.createRow(rowCountCust++);
                                                        cells = row.createCell(0);
                                                        cells.setCellValue(ServiceName);
                                                        cells = row.createCell(1);
                                                        cells.setCellValue(values);
                                                    }
                                                    if ((sheetName.equalsIgnoreCase("Comparative_Plant_Yield_History") || sheetName.equalsIgnoreCase("Comparative_Plant_Perf._Trend")) && l == 0) {
                                                        logger.info("l value in history and trends sheet " + l);
                                                        row = sheet.createRow(0);
                                                        int fo = 0;
                                                        cells = row.createCell(fo);
                                                        cells.setCellValue("Date");

                                                        for (String subKey1 : subKeys) {
                                                            fo = fo + 1;
                                                            mm.put(fo, subKey1);
                                                            cells = row.createCell(fo);
                                                            //logger.info("in for" + fo);
                                                            cells.setCellValue(subKey1);
                                                            logger.info("subKey1 " + subKey1);
                                                            jsonArrayData = (JSONArray) parser.parse(new FileReader(custSourcePath + subKey1 + File.separator + "PerformanceRatio.json"));
                                                            logger.info("Customer sheet Json size=========> " + jsonArrayData.size());

                                                            //for (int l1 = 0; l1 < jsonArrayData.size(); l1++) {
                                                            int k1 = 0;
                                                            if (jsonArrayData.size() > 0) {
                                                                dataObj = (JSONObject) jsonArrayData.get(k1);

                                                                if (sheetName.equalsIgnoreCase("Comparative_Plant_Yield_History")) {
                                                                    k1 = 2;
                                                                    logger.info("in yeild_history=======>" + k1);
                                                                } else if (sheetName.equalsIgnoreCase("Comparative_Plant_Perf._Trend")) {
                                                                    k1 = 0;
                                                                    logger.info("in perf_trend=======>" + k1);
                                                                }
                                                                logger.info("k1=======>" + k1);

                                                                timeStamps = (JSONArray) dataObj.get("TimeStamps");
                                                                serviceName = (String) dataObj.get("ServiceName");
                                                                resourceValues = (JSONArray) dataObj.get("ResourceValues");
                                                                //logger.info("timeStampCust=======>" + timeStamps.size());
                                                                //logger.info("resourceValuesCust=======>" + resourceValues.size());

                                                                logger.info("k1=======>" + k1);
                                                                timeStamps1 = (JSONArray) timeStamps.get(k1);
                                                                //String resourceNamesCust1=(String) resourceNamesCust.get(k1);
                                                                resourceValues1 = (JSONArray) resourceValues.get(k1);
                                                                logger.info("timeStampCust1=======>" + timeStamps1);
                                                                logger.info("resourceValuesCust1=======>" + resourceValues1);
                                                                logger.info("timeStampCust1 size " + timeStamps1.size());
                                                                logger.info("resourceValuesCust1 size " + resourceValues1.size());
                                                                for (int m1 = 0; m1 < resourceValues1.size(); m1++) {

                                                                    logger.info("m1=======>" + m1);
                                                                    String ts2 = (String) timeStamps1.get(m1);
                                                                    logger.info("ts=====>" + ts2);

                                                                    Date ts;
                                                                    if (spiltePeriods[j].equalsIgnoreCase("Week") || spiltePeriods[j].equalsIgnoreCase("Month")) {
                                                                        ts = formatter21.parse(ts2);
                                                                        //logger.info(formatter21.format(date));
                                                                    } else if (spiltePeriods[j].equalsIgnoreCase("Year")) {
                                                                        ts = formattery22.parse(ts2);
                                                                    } else {
                                                                        Date dd = new Date();
                                                                        String[] tssplite = ts2.split(":");
                                                                        logger.info(tssplite[0]);
                                                                        dd.setHours(Integer.parseInt(tssplite[0]));

                                                                        logger.info("tsday=====>" + dd);
                                                                        String ttd = formatter.format(dd);
                                                                        ts = formatter.parse(ttd);

                                                                        logger.info("ts=====>" + ts);

                                                                    }
                                                                    logger.info("ts=====>" + ts);
                                                                    logger.info("ts=====>" + ts);
                                                                    //System.exit(0);

                                                                    if (custMap.containsKey(ts)) {

                                                                        custMap1 = (HashMap) custMap.get(ts);
                                                                        logger.info("main avail =======>" + ts);
                                                                        if (!(custMap1.containsKey(serviceName))) {
                                                                            if (resourceValues1.get(m1) instanceof Long) {
                                                                                custMap1.put(serviceName, (Long) resourceValues1.get(m1));
                                                                                logger.info("sub map avil=====>" + ts);
                                                                            } else if (resourceValues1.get(m1) instanceof String) {
                                                                                logger.info("sub map avil=====>" + ts);
                                                                                custMap1.put(serviceName, (Double) Double.parseDouble((String) resourceValues1.get(m1)));
                                                                                logger.info("sub map avil=====>" + ts);
                                                                            } else {
                                                                                custMap1.put(serviceName, (Double) resourceValues1.get(m1));
                                                                                logger.info("not avail sub===== " + ts);
                                                                            }
                                                                        }
                                                                        custMap.put(ts, custMap1);
                                                                        logger.info("main first=====> " + ts);

                                                                    } else {
                                                                        custMap1 = new HashMap();
                                                                        if (resourceValues1.get(m1) instanceof Long) {
                                                                            custMap1.put(serviceName, (Long) resourceValues1.get(m1));
                                                                            logger.info("inside  if " + custMap1);
                                                                        } else if (resourceValues1.get(m1) instanceof String) {
                                                                            custMap1.put(serviceName, (Double) Double.parseDouble((String) resourceValues1.get(m1)));
                                                                            logger.info("inside  if " + custMap1);

                                                                        } else {
                                                                            custMap1.put(serviceName, (Double) resourceValues1.get(m1));
                                                                            logger.info("inside  if " + custMap1);
                                                                        }
                                                                        custMap.put(ts, custMap1);
                                                                        logger.info(ts);
                                                                        logger.info("new entry " + custMap1);
                                                                    }
                                                                    //}
                                                                }

                                                            } else {
                                                                logger.info("Json is empty in  " + subKey1);

                                                            }
                                                        }
                                                        map = new TreeMap<Date, HashMap>(custMap);
                                                        logger.info("map " + map);
                                                        logger.info("cmap " + custMap);
                                                        logger.info("mm " + mm);

                                                        vall = map.keySet();
                                                        for (Date ts1 : vall) {

                                                            row1 = sheet.createRow(rowCount++);
                                                            cells = row1.createCell(0);
                                                            cells.setCellValue(formatter.format(ts1));
                                                            logger.info("ts================>" + formatter.format(ts1));

                                                            subKeys2 = mm.keySet();
                                                            for (Integer subkey21 : subKeys2) {

                                                                logger.info("in for each loop subkey================>" + subkey21);
                                                                String subkey2 = (String) mm.get(subkey21);
                                                                logger.info("in for each loop subkey================>" + subkey2);
                                                                HashMap val2 = (HashMap) custMap.get(ts1);
                                                                cells = row1.createCell(subkey21);
                                                                if (val2 != null) {
                                                                    logger.info("val2================>" + val2);
                                                                    if (val2.get(subkey2) instanceof Long) {

                                                                        if (val2.get(subkey2) != null) {
                                                                            cells.setCellValue((Long) val2.get(subkey2));
                                                                        } else {
                                                                            //cells.setCellValue("");
                                                                        }
                                                                    } else {
                                                                        if (val2.get(subkey2) != null) {
                                                                            cells.setCellValue((Double) val2.get(subkey2));
                                                                        } else {
                                                                            //cells.setCellValue("");
                                                                        }
                                                                    }
                                                                } else {
                                                                    cells.setCellValue("undefined ");
                                                                }

                                                            }
                                                        }
                                                        custMap1.clear();
                                                        map.clear();
                                                        custMap.clear();
                                                        logger.info("map are cleared" + sheet.getSheetName());
                                                        logger.info(custMap1);
                                                        logger.info(map);
                                                        logger.info(custMap);
                                                    }
                                                }
                                                if (y == 1) {
                                                    if (sheetName.equalsIgnoreCase("Plant_Yield_History")) {
                                                        ServiceName = (String) obj.get("ServiceName");
                                                        yield = (JSONArray) obj.get("ResourceValues");
                                                        timeStamps = (JSONArray) obj.get("TimeStamps");
                                                        //logger.info("==========>" + ServiceName);
                                                        //logger.info("==========>" + yield);
                                                        //logger.info("==========>" + timeStamps);

                                                        yield1 = (JSONArray) yield.get(2);
                                                        //String resourceNamesCust1=(String) resourceNamesCust.get(k1);
                                                        timeStamps1 = (JSONArray) timeStamps.get(2);
                                                        for (int r = 0; r < yield1.size(); r++) {
                                                            String tsInst = (String) timeStamps1.get(r);
                                                            logger.info("rowCountInst" + tsInst);

                                                            if (r == 0) {
                                                                logger.info("rowCountInst" + rowCountInst);
                                                                rowInst = sheet.createRow(rowCountInst++);
                                                                cells = rowInst.createCell(0);
                                                                cells.setCellValue("Date");
                                                                cells = rowInst.createCell(1);
                                                                cells.setCellValue(ServiceName);
                                                            }
                                                            logger.info("rowCountInst" + rowCountInst);
                                                            rowInst = sheet.createRow(rowCountInst++);
                                                            cells = rowInst.createCell(0);
                                                            cells.setCellValue(tsInst);
                                                            cells = rowInst.createCell(1);
                                                            if (yield1.get(r) instanceof Long) {
                                                                cells.setCellValue((Long) yield1.get(r));
                                                            } else if (yield1.get(r) instanceof String) {
                                                                cells.setCellValue((Double) Double.parseDouble((String) yield1.get(r)));
                                                            } else {
                                                                cells.setCellValue((Double) yield1.get(r));
                                                            }

                                                        }
                                                    } else if ((sheetName.equalsIgnoreCase("Comparative_Inverter_Yield") || sheetName.equalsIgnoreCase("Comparative_Inverter_Performanc")) && l == 0) {

                                                        jsonArrayData = (JSONArray) parser.parse(new FileReader(installationSourcePath + "PerformanceRatioComparison.json"));

                                                        logger.info("Install sheet Json array size=========> " + jsonArrayData.size());
                                                        rowCountInst = 1;
                                                        rowCountInst1 = 1;

                                                        for (int l1 = 0; l1 < jsonArrayData.size(); l1++) {
                                                            logger.info("l1=======>" + l1);
                                                            dataObj = (JSONObject) jsonArrayData.get(l1);
                                                            logger.info("jjjj" + dataObj);
                                                            if (sheetName.equalsIgnoreCase("Comparative_Inverter_Yield")) {
                                                                serviceName = (String) dataObj.get("ResourceID");
                                                                rowInst1 = sheet.createRow(rowCountInst1++);
                                                                cells = rowInst1.createCell(0);
                                                                cells.setCellValue(serviceName);
                                                                cells = rowInst1.createCell(1);
                                                                if ((dataObj.get("Yield").equals(""))) {
                                                                    cells.setCellValue(0);
                                                                } else {
                                                                    cells.setCellValue(Integer.parseInt((String) dataObj.get("Yield")));
                                                                }

                                                            } else if (sheetName.equalsIgnoreCase("Comparative_Inverter_Performanc")) {
                                                                serviceName = (String) dataObj.get("ResourceID");
                                                                logger.info("serviceNameInstInst=======>" + serviceName);
                                                                rowInst1 = sheet.createRow(rowCountInst1++);
                                                                logger.info("rowCount in =======>" + rowCountInst1);
                                                                cells = rowInst1.createCell(0);
                                                                cells.setCellValue(serviceName);
                                                                cells = rowInst1.createCell(1);
                                                                if ((dataObj.get("PerformanceRatio").equals(""))) {
                                                                    cells.setCellValue(0);
                                                                } else {
                                                                    cells.setCellValue(Double.parseDouble((String) dataObj.get("PerformanceRatio")));
                                                                }
                                                            }

                                                        }
                                                    }
                                                }

                                            }//end "l" for             
                                        }//end "s" for
                                        fileOutputStream = new FileOutputStream(destiFile + "Summary.xlsx");
                                        wbDB.write(fileOutputStream);
                                        logger.info("data writen sucessfully");

                                    }
                                }

                            } catch (FileNotFoundException e) {
                                logger.error(e.toString());
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                                logger.error(e.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                                logger.error(e.toString());
                            }
                        }//endofarray
                    }//endof submap
                }//endof Map
                customerMap.clear();
                installationMap.clear();
            }
        } catch (Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
        } finally {

            try {
                  jUrl = null;
                if (inputStrem != null) {
                    inputStrem.close();
                    inputStrem = null;
                }if (jsonInputStream != null) {
                    jsonInputStream.close();
                    jsonInputStream = null;
                }if (jsonArrayData != null) {
                    jsonArrayData.clear();
                    jsonArrayData = null;
                }if (conn != null) {
                    conn.disconnect();
                    conn = null;
                }if (resopnse != null) {
                    resopnse.close();
                    resopnse = null;
                }if (jsonName != null) {
                    jsonName = null;
                }if (apiDeviceNamesArray != null) {
                    apiDeviceNamesArray.clear();
                    apiDeviceNamesArray = null;
                }if (keys != null) {
                    keys.clear();
                    keys = null;
                    url = null;
                }if (obj != null) {
                    obj.clear();
                    obj = null;
                }if (dataObj != null) {
                    dataObj.clear();
                    dataObj = null;
                }if (map != null) {
                    map.clear();
                    map = null;
                }if (mm != null) {
                    mm.clear();
                    mm = null;
                }if (custMap != null) {
                    custMap.clear();
                    custMap = null;
                }if (custMap1 != null) {
                    custMap1.clear();
                    custMap1 = null;
                }if (formatter != null) {
                    formatter = null;
                    dFormat = null;
                }if (fileOutputStream != null) {
                    fileOutputStream.close();
                    fileOutputStream = null;
                } if (templateStream != null) {
                    templateStream.close();
                    templateStream = null;
                    installationLevelPath = null;
                    dirStructure = null;
                    subMap = null;
                    subKeys = null;
                    list = null;
                    xlsxFile = null;
                    jsonDirectory = null;
                
                    jsonSubDirectory = null;
                }if (matchFiles != null) {
                    matchFiles = null;
                    matchJsons = null;
                    parser = null;
                }if (jsonArray != null) {
                    jsonArray.clear();
                    jsonArray = null;
                }    wbDB = null;
                    sheet = null;
                    row = null;
                if (customerMap != null) {
                    installationMap.clear();
                    installationMap = null;
                }if (installationMap != null) {
                    customerMap.clear();
                    customerMap = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.toString());
            }
        }
    }

    public static String urlResopnes(String url) {
        URL jUrl = null;
        HttpURLConnection conn = null;
        InputStream resopnse = null;
        InputStreamReader inputStrem = null;
        BufferedReader jsonInputStream = null;
        String output;
        String jsonName = null;
        try {
            logger.info("res" + url);
            jUrl = new URL(url);
            conn = (HttpURLConnection) jUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.connect();
            resopnse = conn.getInputStream();
            inputStrem = new InputStreamReader(resopnse);
            jsonInputStream = new BufferedReader(inputStrem);
            while ((output = jsonInputStream.readLine()) != null) {
                logger.info(output);
                jsonName = output;
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException e) {

        } finally {

            try {
                   jUrl = null;
                 if (inputStrem != null) {
                    inputStrem.close();
                    inputStrem = null;
                 }if (jsonInputStream != null) {
                    jsonInputStream.close();
                    jsonInputStream = null;
                }if (conn != null) {
                    conn.disconnect();
                    conn = null;
                }if (resopnse != null) {
                    resopnse.close();
                    resopnse = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonName;
    }

    public static int rowIteratorMethod(XSSFSheet sheet, String value) {
        logger.info("sheetName" + sheet.getSheetName());;
        XSSFRow row = null;
        XSSFCell cell = null;
        try {
            int noOfRows = sheet.getLastRowNum();
            logger.info("noOfRows" + noOfRows);;

            for (int i = 0; i <= noOfRows; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    cell = row.getCell(1);
                    if (cell != null) {
                        String xlCellVal = cell.getStringCellValue();
                        logger.info("cell date : " + xlCellVal);
                        if (xlCellVal.equalsIgnoreCase(value)) {
                            logger.info("row no : " + i);
                            return i;

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String performanceAnalysisPeriod(String zDate, String zdatePeriod) {

        String perAP = null;
        logger.info(zDate);
         String[] zDateSplite=null;
         SimpleDateFormat checkDate=null,checkDate1=null;
        Calendar perAPCalStart=null,perAPCalEnd=null;
         try{
        zDateSplite = zDate.split("-");
        checkDate = new SimpleDateFormat("MM-dd-yyyy");
        checkDate1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        perAPCalStart = Calendar.getInstance();
        perAPCalEnd = Calendar.getInstance();
        if (zDate.equalsIgnoreCase(checkDate.format(perAPCalStart.getTime()))) {
            logger.info("Current day");
        } else {
            perAPCalStart.set(Integer.parseInt(zDateSplite[2]), (Integer.parseInt(zDateSplite[0]) - 1), Integer.parseInt(zDateSplite[1]));
            perAPCalEnd.set(Integer.parseInt(zDateSplite[2]), (Integer.parseInt(zDateSplite[0]) - 1), Integer.parseInt(zDateSplite[1]));
            logger.info("differnt day" + perAPCalStart.getTime());
            if (zdatePeriod.equalsIgnoreCase("Hour")) {
                perAPCalEnd.set(Calendar.MINUTE, 59);
                perAPCalEnd.set(Calendar.SECOND, 59);
            } else {
                perAPCalEnd.set(Calendar.HOUR_OF_DAY, 23);
                perAPCalEnd.set(Calendar.MINUTE, 59);
                perAPCalEnd.set(Calendar.SECOND, 59);
            }
        }
        if (zdatePeriod.equalsIgnoreCase("Hour")) {
            perAPCalStart.set(Calendar.MINUTE, 0);
            perAPCalStart.set(Calendar.SECOND, 0);
        } else {
            perAPCalStart.set(Calendar.HOUR_OF_DAY, 00);
            perAPCalStart.set(Calendar.MINUTE, 00);
            perAPCalStart.set(Calendar.SECOND, 00);
        }
        if (zdatePeriod.equalsIgnoreCase("Hour")) {
            perAPCalStart.add(Calendar.HOUR, -1);
        } else if (zdatePeriod.equalsIgnoreCase("Day")) {
            //perAPCalStart.add(Calendar.DATE, -1);
        } else if (zdatePeriod.equalsIgnoreCase("Week")) {
            perAPCalStart.add(Calendar.DATE, -((perAPCalEnd.get(Calendar.DAY_OF_WEEK)) - 1));
        } else if (zdatePeriod.equalsIgnoreCase("Month")) {
            perAPCalStart.add(Calendar.MONTH, -1);
        } else if (zdatePeriod.equalsIgnoreCase("year")) {
            perAPCalStart.add(Calendar.YEAR, -1);
        }
        logger.info((checkDate.format(perAPCalStart.getTime()) + " - " + checkDate.format(perAPCalEnd.getTime())));
        perAP = checkDate1.format(perAPCalStart.getTime()) + " - " + checkDate1.format(perAPCalEnd.getTime());
        logger.info(perAP);
        }catch(Exception e){
        e.printStackTrace();
        logger.error(e);
        
        }finally{
        try{
        zDateSplite=null;
        checkDate=null;
        checkDate1=null; 
        if(perAPCalStart!=null){
        perAPCalStart.clear();
        perAPCalStart=null;
        }
        if(perAPCalEnd!=null){
        perAPCalEnd.clear();
        perAPCalEnd=null;
        }
        }catch(Exception e1){
        e1.printStackTrace();
        logger.error(e1);
        }
        
        }
        return perAP;

    }

    public static void main(String[] args) throws IOException {
        String fs = System.getProperty("file.separator");
        String[] forDate = null;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dFormat = new SimpleDateFormat("MM-dd-yyyy");
        ReportsGenerator generator = new ReportsGenerator("config" + fs + "ReportGenerator.properties");
        try {
            logger.info("calling the initilize method");
            generator.initialize();
            logger.info("in generatexls Method Call");
            String zDate = "-1";
            for (String arg : args) {
                if (arg.contains("--forDate")) {
                    forDate = arg.split("=");
                    zDate = forDate[1];
                }
            }
            if (zDate != null) {
                if (zDate.equalsIgnoreCase("-1")) {
                    zDate = dFormat.format(cal.getTime());
                    logger.info("taking curTime " + zDate);
                }
            }
            generator.generateXLS(zDate);
            intervel = ReportsGeneratorProperties.getIntervel();
            logger.info("Reports Are Generated Sucessfully");
            //Thread.sleep(intervel * 60 * 1000);

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.toString());
        } finally {
            try {
                if (intervel != 0) {
                    intervel = 0;
                }
            } catch (Exception e) {
                logger.error(e.toString());
                e.printStackTrace();
            }
        }
    }
}
