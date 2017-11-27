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
 * *********************************************************************
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.merit.dashboard;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.merit.dashboard.DBUtil.DBUtilHelper;
import com.merit.dashboard.util.ResourceConfiguration;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * **************************************************************************************************
 * This Class is used to get Start Date as current Date minus selected time
 * period and current Date
 *
 * @author satyaJay
 * **************************************************************************************************
 */
public class DateGeneratorDynamic {

    static Logger log = Logger.getLogger(DateGeneratorDynamic.class);

    @SuppressWarnings("deprecation")
    public static String[] DateGeneratorDynamic(String interval) {
        String strDate[] = new String[2];
        Calendar now = Calendar.getInstance();
        SimpleDateFormat formatter1 = null;
        String peroidType = null;
        String elapsedLastTime;
        String CalendarCurTime;
        String CalendarStartTime;
        String elapsedGivenTime;
        int year = 0, month = 0, day = 0, hour = 0;
        try {
            String dateFormate1 = DBUtilHelper.getMetrics_mapping_properties().getProperty("ReferenceTime");
            peroidType = DBUtilHelper.getMetrics_mapping_properties().getProperty("PeroidType");
            formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (dateFormate1 != null) {
                Date dt = formatter1.parse(dateFormate1);
                now.set((dt.getYear() + 1900), dt.getMonth(), dt.getDate(), dt.getHours(), dt.getMinutes(), dt.getSeconds());
            } else {
                now = Calendar.getInstance();
            }


            if (peroidType.equalsIgnoreCase("Elapsed")) {

                elapsedGivenTime = formatter1.format(now.getTime());
                System.out.println("elapsedGivenTime= " + elapsedGivenTime);

                if (interval.equalsIgnoreCase("hour")) {
                    now.add(Calendar.HOUR, -1);

                } else if (interval.equalsIgnoreCase("day")) {
                    now.add(Calendar.DATE, -1);

                } else if (interval.equalsIgnoreCase("week")) {
                    now.add(Calendar.DATE, -7);

                } else if (interval.equalsIgnoreCase("month")) {
                    now.add(Calendar.MONTH, -1);

                } else if (interval.equalsIgnoreCase("year")) {
                    now.add(Calendar.YEAR, -1);
                }

                elapsedLastTime = formatter1.format(now.getTime());
                System.out.println("Date " + "Before " + interval + " elapsedLastTime = " + elapsedLastTime);
                strDate[0] = elapsedGivenTime;
                strDate[1] = elapsedLastTime;

            } else {
                year = now.get(Calendar.YEAR);
                if (interval.equalsIgnoreCase("hour")) {
                    System.out.println("in hour   " + formatter1.format(now.getTime()));
                    hour = now.get(Calendar.HOUR_OF_DAY);
                    year = now.get(Calendar.YEAR);
                    month = now.get(Calendar.MONTH);
                    day = now.get(Calendar.DATE);
                } else if (interval.equalsIgnoreCase("day")) {
                    System.out.println("in day");
                    month = now.get(Calendar.MONTH);
                    day = now.get(Calendar.DATE);
                } else if (interval.equalsIgnoreCase("week")) {
                    System.out.println("in week");
                    now.add(Calendar.DATE, -(now.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY));
                    System.out.println(now.getTime());
                    year = now.get(Calendar.YEAR);
                    month = now.get(Calendar.MONTH);
                    day = now.get(Calendar.DATE);

                } else if (interval.equalsIgnoreCase("month")) {
                    System.out.println("in month");
                    day = now.getActualMinimum(Calendar.DATE);
                    month = now.get(Calendar.MONTH);
                } else if (interval.equalsIgnoreCase("year")) {
                    System.out.println("in year");
                    month = now.getActualMinimum(Calendar.MONTH);
                    day = now.getActualMinimum(Calendar.DATE);

                }
                if (interval.equalsIgnoreCase("Hour") && hour != 0) {
                    now.add(Calendar.HOUR, -1);
                } else {
                    now.set(year, month, day, 00, 00, 00);
                }
                //now.set(year, month, day, 00, 00, 00);
                CalendarStartTime = formatter1.format(now.getTime());
                System.out.println("CalendarStartTime= " + CalendarStartTime);

                CalendarCurTime = formatter1.format(Calendar.getInstance().getTime());
                System.out.println("CalendarCurrentTime = " + CalendarCurTime);
                strDate[1] = CalendarStartTime;
                strDate[0] = CalendarCurTime;

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (now != null) {
                now.clear();
                now = null;
            } else if (formatter1 != null) {
                formatter1 = null;
            }
        }

        return strDate;
    }

    public static double getIrradiationOfMinutes(String period, String timestamp, String customer, String cCustomer, String service) {

        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        DateFormat dayFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Calendar calen = Calendar.getInstance();
        System.out.println("sys" + calen.getTimeInMillis());
        Calendar sunHrStart = Calendar.getInstance();
        Calendar sunHrEnd = Calendar.getInstance();


        Calendar timeStampCal = Calendar.getInstance();;
        List list = new ArrayList();
        list.add("Jan");
        list.add("Feb");
        list.add("Mar");
        list.add("Apr");
        list.add("May");
        list.add("Jun");
        list.add("Jul");
        list.add("Aug");
        list.add("Sep");
        list.add("Oct");
        list.add("Nov");
        list.add("Dec");

        System.out.println("sys" + date);

        long minutesHour, hoursDay, daysWeek, daysMonth, monthYear;
        int minutes = 0;
        List curDaySEMills = null;
        Date timestampDate = null;
        String[] spliteTS = null;
        double irradiationMinute = 0;
        double irradiationNull = 0;
        long tsMillis;


        String sunHoursStart = ResourceConfiguration.getConfValue(customer, cCustomer, service, "Default", null, "SunLightStart");
        String[] sunHoursStart1 = sunHoursStart.split(":");

        sunHrStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sunHoursStart1[0]));
        sunHrStart.set(Calendar.MINUTE, Integer.parseInt(sunHoursStart1[1]));
        System.out.println("sunHrStart time" + sunHrStart.getTime());
        long sunHrStartMillis = sunHrStart.getTimeInMillis();
        System.out.println("sunHrStartMillis" + (sunHrStartMillis));


        String sunHoursEnd = ResourceConfiguration.getConfValue(customer, cCustomer, service, "Default", null, "SunLightEnd");
        String[] sunHoursEnd1 = sunHoursEnd.split(":");

        sunHrEnd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sunHoursEnd1[0]));
        sunHrEnd.set(Calendar.MINUTE, Integer.parseInt(sunHoursEnd1[1]));
        System.out.println("sunHrEnd time" + sunHrEnd.getTime());
        long sunHrEndMillis = sunHrEnd.getTimeInMillis();
        System.out.println("sunHrEndMillis" + sunHrEndMillis);

        try {
            calen.setTime(date);



            minutesHour = calen.get(Calendar.MINUTE);
            System.out.println("Minutes in hour" + minutesHour);
            hoursDay = calen.get(Calendar.HOUR_OF_DAY);
            System.out.println("day in Hours" + hoursDay);
            daysWeek = calen.get(Calendar.DAY_OF_WEEK_IN_MONTH) - 1;
            System.out.println("days in week" + daysWeek);
            daysMonth = calen.get(Calendar.DAY_OF_MONTH) - 1;
            System.out.println("days in month" + daysMonth);
            monthYear = calen.get(Calendar.DAY_OF_YEAR) - 1;
            System.out.println("days in year" + monthYear);


            if (period.equalsIgnoreCase("Hour")) {
                //minutes = minutesHour;
                System.out.println("Minutes in a hour " + minutesHour);
                System.out.println("current calendear time" + calen.getTime());
                System.out.println("timestamp: " + timestamp);
                if (timestamp == null || timestamp.equalsIgnoreCase("null")) {
                    timestamp = dayFormat.format(date);
                    System.out.println("curtime: " + timestamp);
                } else {
                    spliteTS = timestamp.split("/");
                    String mon = spliteTS[1];
                    System.out.println("year " + spliteTS[2]);
                    System.out.println("month " + spliteTS[1]);
                    System.out.println("day " + spliteTS[0]);
                    for (int i = 0; i < list.size(); i++) {
                        if (mon.equalsIgnoreCase((String) list.get(i))) {
                            timestamp = spliteTS[2].substring(0, 4) + "/" + (i + 1) + "/" + spliteTS[0] + spliteTS[2].substring(4);
                            System.out.println("param timestamp is " + timestamp);
                        }
                    }
                }
                curDaySEMills = getPeriodEndTS(calen.getTimeInMillis(), "Day");
                timestampDate = dayFormat.parse(timestamp);
                timeStampCal.setTime(timestampDate);
                System.out.println("timestamp time " + timeStampCal.getTime());

                System.out.println("timestamp in millis " + timeStampCal.getTimeInMillis());
                if (timeStampCal.getTimeInMillis() >= ((Long) curDaySEMills.get(1) * 1000)
                        && timeStampCal.getTimeInMillis() < ((Long) curDaySEMills.get(3) * 1000)) {
                    System.out.println(timestamp);

                    System.out.println("timestamp is in current day only ");
                    if (timestamp.contains(" ")) {
                        String[] hrminute = timestamp.split(" ");
                        System.out.println(hrminute[1]);
                        String[] hrminute1 = hrminute[1].split(":");
                        System.out.println(hrminute1.length);
                        System.out.println(hrminute1[0]);
                        System.out.println(hrminute1[1]);

                        minutes = Integer.parseInt((String) hrminute1[0]);
                        System.out.println(minutes);

                    } else {
                        minutes = calen.get(Calendar.HOUR_OF_DAY);
                        System.out.println("adding current date hours and minutes to timestamp :" + minutes);

                    }

                    tsMillis = timeStampCal.getTimeInMillis();
                    System.out.println("tsmillis " + tsMillis);

                    //if (tsMillis >= sunHrStartMillis && tsMillis <= sunHrEndMillis) {
                    System.out.println("timestamp is bet ween  sun hours ");
                    irradiationMinute = DateGeneratorDynamic.uRLM(minutes, "Cumm");
                    System.out.println("irradiation of that minutes " + irradiationMinute);
                    /* }else{
                     System.out.println("timestamp is not bet ween  sun hours ");
                     irradiationMinute=0;
                     System.out.println("irradiation of that minutes "+irradiationMinute);
                     }*/

                } else {
                    minutes = 0;
                    System.out.println("not current day " + minutes);
                    irradiationMinute = DateGeneratorDynamic.uRLM(minutes, "Cumm");
                    System.out.println("irradiation of that minutes " + irradiationMinute);
                }


            } else if (period.equalsIgnoreCase("Day")) {

                System.out.println("current calendear time" + calen.getTime());
                System.out.println("timestamp: " + timestamp);
                if (timestamp == null || timestamp.equalsIgnoreCase("null")) {
                    timestamp = dayFormat.format(date);
                    System.out.println("curtime: " + timestamp);
                }
                spliteTS = timestamp.split("/");
                String mon = spliteTS[1];
                System.out.println("year " + spliteTS[0]);
                System.out.println("month " + spliteTS[1]);
                for (int i = 0; i < list.size(); i++) {
                    if (mon.equalsIgnoreCase((String) list.get(i))) {
                        timestamp = spliteTS[0] + "/" + (i + 1) + "/" + spliteTS[2];
                        System.out.println("param timestamp is " + timestamp);
                    }
                }

                curDaySEMills = getPeriodEndTS(calen.getTimeInMillis(), "Day");
                if (timestamp.contains(":")) {
                    timestampDate = dayFormat.parse(timestamp);
                    timeStampCal.setTime(timestampDate);

                } else {
                    timestampDate = format.parse(timestamp);
                    timeStampCal.setTime(timestampDate);
                    timeStampCal.set(Calendar.HOUR_OF_DAY, calen.get(Calendar.HOUR_OF_DAY));
                    timeStampCal.set(Calendar.MINUTE, calen.get(Calendar.MINUTE));
                }
                System.out.println("timestamp time " + timeStampCal.getTime());
                System.out.println("timestamp in millis " + timeStampCal.getTimeInMillis());
                if (timeStampCal.getTimeInMillis() >= ((Long) curDaySEMills.get(1) * 1000) && timeStampCal.getTimeInMillis() < ((Long) curDaySEMills.get(3) * 1000)) {
                    System.out.println("timestamp is in current day only ");
                    if (timestamp.contains(" ")) {
                        String[] hrminute = timestamp.split(" ");
                        String[] hrminute1 = hrminute[1].split(":");
                        minutes = Integer.parseInt((String) hrminute[1].subSequence(0, hrminute[1].length() - 6));
                        System.out.println(timestamp);
                        System.out.println(minutes);


                    } else {
                        minutes = calen.get(Calendar.HOUR_OF_DAY);
                        System.out.println("adding current date hours and minutes to timestamp :" + minutes);

                    }
                    tsMillis = timeStampCal.getTimeInMillis();
                    System.out.println("tsmillis " + tsMillis);

                    if (tsMillis >= sunHrStartMillis && tsMillis <= sunHrEndMillis) {
                        System.out.println("timestamp is bet ween  sun hours ");

                        irradiationMinute = DateGeneratorDynamic.uRLM(minutes, "Cumm");
                        System.out.println("irradiation of that minutes " + irradiationMinute);
                    } else {
                        System.out.println("timestamp is not bet ween  sun hours ");
                        irradiationMinute = 0;
                        System.out.println("irradiation of that minutes " + irradiationMinute);
                    }


                } else {
                    minutes = 0;
                    System.out.println("not current day " + minutes);
                    irradiationMinute = DateGeneratorDynamic.uRLM(minutes, "Cumm");
                    System.out.println("irradiation of that minutes " + irradiationMinute);
                }

                //}
            } else if (period.equalsIgnoreCase("week")) {

                if (timestamp == null || timestamp.equalsIgnoreCase("null")) {
                    timestamp = format.format(date);
                    System.out.println("curtime: " + timestamp);
                }
                System.out.println("timestamp in week " + timestamp);
                spliteTS = timestamp.split("/");
                String mon = spliteTS[1];
                System.out.println("year(Week)  " + spliteTS[0]);
                System.out.println("month(week) " + spliteTS[1]);
                for (int i = 0; i < list.size(); i++) {
                    if (mon.equalsIgnoreCase((String) list.get(i))) {
                        timestamp = spliteTS[0] + "/" + (i + 1) + "/" + spliteTS[2];
                        System.out.println("timestamp(week) " + timestamp);
                    }
                }
                //curDaySEMills this indicate starting and end timestamp millis of month
                timestampDate = format.parse(timestamp);
                timeStampCal.setTime(timestampDate);

                System.out.println("minutes in a week " + timeStampCal.getTime());
                System.out.println("millies in a week " + timeStampCal.getTimeInMillis());
                List noOfDays = getPeriodEndTS(timeStampCal.getTimeInMillis(), "Week");

                System.out.println("timestamp is in current week only ");

                minutes = calen.get(Calendar.HOUR_OF_DAY);
                System.out.println("adding current date hours and minutes to timestamp " + minutes);
                timeStampCal.set(Calendar.HOUR_OF_DAY, calen.get(Calendar.HOUR_OF_DAY));
                timeStampCal.set(Calendar.MINUTE, calen.get(Calendar.MINUTE));
                System.out.println("adding current date hours " + timeStampCal.getTime());

                tsMillis = timeStampCal.getTimeInMillis();
                System.out.println("tsmillis " + tsMillis);
                if (tsMillis >= sunHrStartMillis && tsMillis <= sunHrEndMillis) {
                    System.out.println("timestamp is bet ween  sun hours ");
                    irradiationMinute = DateGeneratorDynamic.uRLM(minutes, "Cumm");
                    System.out.println("irradiation of that minutes " + irradiationMinute);
                } else {
                    System.out.println("timestamp is not bet ween  sun hours ");
                    irradiationMinute = 0;
                    System.out.println("irradiation of that minutes in week " + irradiationMinute);

                }

                System.out.println("no of day in week " + noOfDays.get(0));
                irradiationNull = (DateGeneratorDynamic.uRLM(0, "Cumm") * ((Integer) noOfDays.get(0)));
                System.out.println("irradiation of that minutes null " + irradiationNull);
                irradiationMinute = irradiationMinute + irradiationNull;
                System.out.println("irradiation of week till curdate " + irradiationMinute);



            } else if (period.equalsIgnoreCase("Month")) {

                if (timestamp == null || timestamp.equalsIgnoreCase("null")) {
                    timestamp = format.format(date);
                    System.out.println("curtime: " + timestamp);
                }
                System.out.println("minutes in a Day " + timestamp);
                spliteTS = timestamp.split("/");
                String mon = spliteTS[1];
                System.out.println("year(month)  " + spliteTS[0]);
                System.out.println("month(month) " + spliteTS[1]);
                for (int i = 0; i < list.size(); i++) {
                    if (mon.equalsIgnoreCase((String) list.get(i))) {
                        timestamp = spliteTS[0] + "/" + (i + 1) + "/" + calen.get(Calendar.DAY_OF_MONTH);
                        System.out.println("timestamp(month) " + timestamp);
                    }
                }
                //curDaySEMills this indicate starting and end timestamp millis of month
                curDaySEMills = getPeriodEndTS(calen.getTimeInMillis(), "Month");
                timestampDate = format.parse(timestamp);
                timeStampCal.setTime(timestampDate);
                System.out.println("minutes in a Month " + timeStampCal.getTime());
                timeStampCal.set(Calendar.HOUR_OF_DAY, calen.get(Calendar.HOUR_OF_DAY));
                timeStampCal.set(Calendar.MINUTE, calen.get(Calendar.MINUTE));
                System.out.println("adding current date hours " + timeStampCal.getTime());

                System.out.println("millies in a Month " + timeStampCal.getTimeInMillis());
                if (timeStampCal.getTimeInMillis() >= ((Long) curDaySEMills.get(1) * 1000) && timeStampCal.getTimeInMillis() < ((Long) curDaySEMills.get(3) * 1000)) {


                    System.out.println("timestamp is in current Month only ");
                    minutes = calen.get(Calendar.HOUR_OF_DAY);
                    System.out.println("adding current date hours and minutes to timestamp " + minutes);
                    tsMillis = timeStampCal.getTimeInMillis();
                    System.out.println("tsmillis " + tsMillis);
                    if (tsMillis >= sunHrStartMillis && tsMillis <= sunHrEndMillis) {
                        System.out.println("timestamp is bet ween  sun hours ");

                        irradiationMinute = DateGeneratorDynamic.uRLM(minutes, "Cumm");
                        System.out.println("irradiation of that minutes " + irradiationMinute);
                    } else {
                        System.out.println("timestamp is not bet ween  sun hours ");
                        irradiationMinute = 0;
                        System.out.println("irradiation of that minutes in week " + irradiationMinute);

                    }
                    System.out.println("no of day in month " + daysMonth);
                    irradiationNull = (DateGeneratorDynamic.uRLM(0, "Cumm") * daysMonth);
                    System.out.println("irradiation of that minutes null " + irradiationNull);

                    irradiationMinute = irradiationMinute + irradiationNull;
                    System.out.println("irradiation of month till curdate " + irradiationMinute);


                } else {
                    //  minutes = (timeStampCal.getActualMaximum(Calendar.DAY_OF_MONTH) * totalMinutesOfDay);
                    System.out.println("not current month " + minutes);
                    System.out.println("irradiation of that minutes " + irradiationMinute);
                    System.out.println("no of day in month " + timeStampCal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    irradiationMinute = (DateGeneratorDynamic.uRLM(0, "Cumm") * timeStampCal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    System.out.println("irradiation of full month" + irradiationMinute);
                }
                //}


            } else if (period.equalsIgnoreCase("Year")) {

                if (timestamp == null || timestamp.equalsIgnoreCase("null")) {
                    timestamp = format.format(date);
                    System.out.println("curtime: " + timestamp);
                }
                System.out.println("timestamp in year " + timestamp);
                timestamp = timestamp + "/" + (calen.get(Calendar.MONTH) + 1) + "/" + calen.get(Calendar.DAY_OF_MONTH);
                System.out.println("timestamp after adding month and day " + timestamp);
                curDaySEMills = getPeriodEndTS(calen.getTimeInMillis(), "Year");
                System.out.println("calendar minllis " + calen.getTimeInMillis());
                timestampDate = format.parse(timestamp);
                timeStampCal.setTime(timestampDate);
                System.out.println(timestampDate);
                System.out.println("timestamp time year" + timeStampCal.getTime());
                timeStampCal.set(Calendar.HOUR_OF_DAY, calen.get(Calendar.HOUR_OF_DAY));
                timeStampCal.set(Calendar.MINUTE, calen.get(Calendar.MINUTE));
                System.out.println("adding current date hours " + timeStampCal.getTime());

                System.out.println("timestamp millis year " + timeStampCal.getTimeInMillis());
                if (timeStampCal.getTimeInMillis() >= ((Long) curDaySEMills.get(1) * 1000) && timeStampCal.getTimeInMillis() < ((Long) curDaySEMills.get(3) * 1000)) {




                    System.out.println("timestamp is in current Year only ");
                    minutes = calen.get(Calendar.HOUR_OF_DAY);
                    System.out.println("adding current date hours and minutes to timestamp " + minutes);
                    tsMillis = timeStampCal.getTimeInMillis();
                    System.out.println("tsmillis " + tsMillis);
                    if (tsMillis >= sunHrStartMillis && tsMillis <= sunHrEndMillis) {
                        System.out.println("timestamp is bet ween  sun hours ");

                        irradiationMinute = DateGeneratorDynamic.uRLM(minutes, "Cumm");
                        System.out.println("irradiation of that minutes " + irradiationMinute);
                    } else {
                        System.out.println("timestamp is not bet ween  sun hours ");
                        irradiationMinute = 0;
                        System.out.println("irradiation of that minutes in week " + irradiationMinute);

                    }
                    System.out.println("no of Months in year " + monthYear);
                    irradiationNull = (DateGeneratorDynamic.uRLM(20, "Cumm") * monthYear);
                    System.out.println("irradiation of that minutes " + irradiationNull);
                    irradiationMinute = irradiationMinute + irradiationNull;
                    System.out.println("irradiation of year till curdate " + irradiationMinute);

                } else {
                    System.out.println("not current year " + minutes);

                    System.out.println("irradiation of that minutes " + irradiationMinute);
                    System.out.println("no of Day in year " + timeStampCal.getActualMaximum(Calendar.DAY_OF_YEAR));
                    irradiationMinute = (DateGeneratorDynamic.uRLM(20, "Cumm") * timeStampCal.getActualMaximum(Calendar.DAY_OF_YEAR));
                    System.out.println("irradiation of full month" + irradiationMinute);


                }
                // }
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {

                if (date != null) {
                    date = null;
                } else if (format != null) {
                    format = null;
                } else if (calen != null) {
                    calen.clear();
                    calen = null;
                } else if (timeStampCal != null) {
                    timeStampCal.clear();
                    timeStampCal = null;
                } else if (list != null) {
                    list.clear();
                    list = null;
                } else if (curDaySEMills != null) {
                    curDaySEMills.clear();
                    curDaySEMills = null;
                } else if (timestampDate != null) {
                    timestampDate = null;
                } else if (spliteTS != null) {
                    spliteTS = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return irradiationMinute;
    }

    public static double uRLM(int minutes, String IrradiationType) {
        JSONParser parser = null;
        System.out.println("in urlm method");

        JSONArray jsonArray, IrradianValArray;

        String irradiationTS;
        JSONObject obj, irradianObj;
        double irradiationCumVal = 0;
        double irradiationDeltaVal = 0;
        try {
            System.out.println("minutes : " + minutes);
            parser = new JSONParser();
            jsonArray = (JSONArray) parser.parse(new FileReader("Irradiation.json"));

            System.out.println("size " + jsonArray.size());

            if (minutes == 0) {
                System.out.println("size " + jsonArray.size());
                obj = (JSONObject) jsonArray.get(jsonArray.size() - 1);
                irradiationTS = (String) obj.get("timestamp");
                minutes = Integer.parseInt(irradiationTS);
            }
            for (int i = 0; i < jsonArray.size(); i++) {
                obj = (JSONObject) jsonArray.get(i);
                irradiationTS = (String) obj.get("timestamp");
                if (Integer.parseInt(irradiationTS) == minutes) {
                    System.out.println("find " + irradiationTS);

                    IrradianValArray = (JSONArray) obj.get("IrradiationValue");
                    for (int j = 0; j < IrradianValArray.size(); j++) {
                        irradianObj = (JSONObject) IrradianValArray.get(j);
                        System.out.println("i= " + i + " j=" + j + "   " + irradianObj);
                        if (irradianObj.get("cumm") != null) {
                            if (irradianObj.get("cumm") instanceof Long) {
                                irradiationCumVal = (Long) irradianObj.get("cumm");
                            } else if (irradianObj.get("cumm") instanceof Double) {
                                irradiationCumVal = (Double) irradianObj.get("cumm");
                            } else {
                                irradiationCumVal = Double.parseDouble((String) irradianObj.get("cumm"));
                            }
                        }
                        if (irradianObj.get("delta") != null) {
                            if (irradianObj.get("delta") instanceof Long) {
                                irradiationDeltaVal = (Long) irradianObj.get("delta");
                            } else if (irradianObj.get("delta") instanceof Double) {
                                irradiationDeltaVal = (Double) irradianObj.get("delta");
                            } else {
                                irradiationDeltaVal = Double.parseDouble((String) irradianObj.get("delta"));
                            }
                        }
                    }
                    break;
                }
                System.out.println("ts " + irradiationTS);
                System.out.println("cum " + irradiationCumVal);
                System.out.println("delta " + irradiationDeltaVal);


                //System.exit(0);
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (org.json.simple.parser.ParseException ex) {
            ex.printStackTrace();
        }
        if (IrradiationType.equalsIgnoreCase("Delta")) {
            return irradiationDeltaVal;
        }
        return irradiationCumVal;


    }

    public static List getPeriodEndTS(Long epochtime, String period) {

        DateFormat format = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
        Date date = new Date(epochtime);

        System.out.println("getPeriodEndTS :" + format.format(date));
        int year = 0, month = 0, day = 0, week = 0, hour = 0;
        String startDate = null;
        Calendar calen = new GregorianCalendar();
        calen.setTime(date);
        List tsList = new ArrayList();
        long milliseconds;

        // tsList.add(format.format(date));
        try {
            year = calen.get(Calendar.YEAR);

            for (int i = 0; i < 2; i++) {

                if (period.equalsIgnoreCase("Hour")) {
                    System.out.println("in Hour");
                    month = calen.get(Calendar.MONTH);
                    day = calen.get(Calendar.DATE);
                    hour = calen.get(Calendar.HOUR_OF_DAY);
                    System.out.println("calen " + calen.getTime());

                    System.out.println(hour);
                    System.out.println(day);
                    System.out.println(month);
                    System.out.println(year);


                } else if (period.equalsIgnoreCase("Day")) {
                    System.out.println("in day");
                    month = calen.get(Calendar.MONTH);
                    day = calen.get(Calendar.DATE);
                    week = calen.get(Calendar.WEEK_OF_MONTH);
                    System.out.println(week);


                } else if (period.equalsIgnoreCase("Week")) {
                    System.out.println("in week");
                    int noDaysInWeek = calen.get(Calendar.DAY_OF_WEEK);
                    int curDay = date.getDate();
                    System.out.println("Date " + calen.getTime());
                    System.out.println("noDaysInWeek " + noDaysInWeek);
                    int noOfDays = 0;
                    int jj = 0;
                    for (int k = noDaysInWeek; k >= 1; k--) {
                        System.out.println("curDay" + curDay);
                        System.out.println("curDay - k " + (curDay - k));
                        if ((curDay - k) >= 1) {
                            noOfDays = noOfDays + 1;
                            System.out.println("k" + k);
                            jj = jj + 1;
                            System.out.println("noOfDays" + noOfDays);
                        }

                    }
                    System.out.println("noOfDays" + noOfDays);
                    if (jj == noDaysInWeek) {
                        noOfDays = noOfDays - 1;
                        System.out.println("include curDate noOfDays" + noOfDays);

                    } else {
                        System.out.println("Exclude curDate noOfDays" + noOfDays);
                    }
                    tsList.add(noOfDays);
                    return tsList;



                } else if (period.equalsIgnoreCase("Month")) {
                    System.out.println("in month");
                    if (i == 0) {
                        day = calen.getActualMinimum(Calendar.DATE);
                    } else {
                        day = calen.getActualMaximum(Calendar.DATE);
                    }
                    month = calen.get(Calendar.MONTH);
                } else if (period.equalsIgnoreCase("year")) {
                    System.out.println("in year");
                    if (i == 0) {
                        month = calen.getActualMinimum(Calendar.MONTH);
                        day = calen.getActualMinimum(Calendar.DATE);;
                    } else {
                        month = month = calen.getActualMaximum(Calendar.MONTH);;
                        day = calen.getActualMaximum(Calendar.DATE);
                    }
                }
                if (period.equalsIgnoreCase("Hour")) {
                    if (i == 0) {
                        calen.set(year, month, day, hour - 1, 00, 00);
                        System.out.println("hour" + hour);
                    } else {
                        calen.set(year, month, day, hour + 1, 00, 00);
                    }
                } else {
                    if (i == 0) {
                        calen.set(year, month, day, 00, 00, 00);
                    } else {
                        calen.set(year, month, day + 1, 00, 00, 00);
                    }
                }

                startDate = format.format(calen.getTime());
                milliseconds = format.parse(startDate).getTime();
                System.out.println("=====milliseconds======>" + milliseconds);

                tsList.add(startDate);
                tsList.add(milliseconds / 1000);

            }

            System.out.println("===========>" + tsList);
            tsList.add(week);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {

                if (date != null) {
                    date = null;
                } else if (calen != null) {
                    calen.clear();
                    calen = null;
                } else if (format != null) {
                    format = null;
                } else if (startDate != null) {
                    startDate = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return tsList;
    }
}