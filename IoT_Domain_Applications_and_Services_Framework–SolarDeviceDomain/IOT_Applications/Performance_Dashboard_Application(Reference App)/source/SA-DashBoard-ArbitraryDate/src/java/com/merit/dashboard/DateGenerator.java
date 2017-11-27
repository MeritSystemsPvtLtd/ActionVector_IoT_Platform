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

/**
 * **************************************************************************************************
 * This Class is used to get Start Date as current Date minus selected time
 * period and current Date
 *
 * @author satyaJay
 * **************************************************************************************************
 */
public class DateGenerator {

    @SuppressWarnings("deprecation")
    public static String[] dateGenerator(String interval) {
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
            //String dateFormate1 = DBUtilHelper.getMetrics_mapping_properties().getProperty("ReferenceTime");
            peroidType = DBUtilHelper.getMetrics_mapping_properties().getProperty("PeroidType");
            formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            /*if (dateFormate1 != null) {
                Date dt = formatter1.parse(dateFormate1);
                now.set((dt.getYear() + 1900), dt.getMonth(), dt.getDate(), dt.getHours(), dt.getMinutes(), dt.getSeconds());
            } else {
                now = Calendar.getInstance();
            }*/


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

    @SuppressWarnings("deprecation")
    public static String[] dateGenerator(String interval, String date) {
        String strDate[] = new String[2];
        int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0;
        Calendar now = Calendar.getInstance();
        hour = now.get(Calendar.HOUR_OF_DAY);
        minute = now.get(Calendar.MINUTE);
        second = now.get(Calendar.SECOND);

        String[] splite = date.split("-");
        now.set(Integer.parseInt(splite[2]), Integer.parseInt(splite[0]) - 1, Integer.parseInt(splite[1]), 0, 0, 0);

        SimpleDateFormat formatter1 = null;
        String peroidType = null;
        String elapsedLastTime;
        String CalendarCurTime;
        String CalendarStartTime;
        String elapsedGivenTime;

        try {
            //String dateFormate1 = DBUtilHelper.getMetrics_mapping_properties().getProperty("ReferenceTime");
            peroidType = DBUtilHelper.getMetrics_mapping_properties().getProperty("PeroidType");
            formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            /*if (dateFormate1 != null) {
             Date dt = formatter1.parse(dateFormate1);
             now.set((dt.getYear() + 1900), dt.getMonth(), dt.getDate(), dt.getHours(), dt.getMinutes(), dt.getSeconds());
             } else {
             now = Calendar.getInstance();
             }*/

            if (peroidType.equalsIgnoreCase("Elapsed")) {
                now.set(Integer.parseInt(splite[2]), Integer.parseInt(splite[0]) - 1, Integer.parseInt(splite[1]), hour, minute, second);

                elapsedGivenTime = formatter1.format(now.getTime());

                System.out.println("elapsedGivenTime= " + elapsedGivenTime);

                if (interval.equalsIgnoreCase("Hour")) {
                    now.add(Calendar.HOUR, -1);

                } else if (interval.equalsIgnoreCase("Day")) {
                    now.add(Calendar.DATE, -1);

                } else if (interval.equalsIgnoreCase("Week")) {
                    now.add(Calendar.DATE, -7);

                } else if (interval.equalsIgnoreCase("Month")) {
                    now.add(Calendar.MONTH, -1);

                } else if (interval.equalsIgnoreCase("Year")) {
                    now.add(Calendar.YEAR, -1);
                }

                elapsedLastTime = formatter1.format(now.getTime());
                System.out.println("Date " + "Before " + interval + " elapsedLastTime = " + elapsedLastTime);
                strDate[0] = elapsedGivenTime;
                strDate[1] = elapsedLastTime;

            } else {
                year = now.get(Calendar.YEAR);
                if (interval.equalsIgnoreCase("hour")) {
                    System.out.println("in hour");
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
                    System.out.println("hour" + hour);
                    now.add(Calendar.HOUR, hour-1);
                    now.add(Calendar.MINUTE, minute);
                    now.add(Calendar.SECOND, second);
                } else {
                    now.set(year, month, day, 00, 00, 00);
                }
                //now.set(year, month, day, 00, 00, 00);
                CalendarStartTime = formatter1.format(now.getTime());
                System.out.println("CalendarStartTime= " + CalendarStartTime);
                if (interval.equalsIgnoreCase("week")) {
                    day = day + 6;
                } else if (interval.equalsIgnoreCase("month")) {
                    day = now.getActualMaximum(Calendar.DATE);
                    month = now.get(Calendar.MONTH);
                } else if (interval.equalsIgnoreCase("year")) {
                    month = now.getActualMaximum(Calendar.MONTH);
                    day = now.getActualMaximum(Calendar.DATE);
                }
                if (interval.equalsIgnoreCase("Hour") && hour != 0) {
                    now.set(year, month, day, hour, minute, second);

                } else {
                    now.set(year, month, day, 23, 59, 59);
                }

                CalendarCurTime = formatter1.format(now.getTime());
                System.out.println("CalendarEndTime = " + CalendarCurTime);
                strDate[1] = CalendarStartTime;
                strDate[0] = CalendarCurTime;

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (now != null) {
                now.clear();
                now = null;
            }
            formatter1 = null;
            splite = null;
        }

        return strDate;
    }

    public static void main(String... a) {
        dateGenerator("hour", "07-03-2015");
    }
}