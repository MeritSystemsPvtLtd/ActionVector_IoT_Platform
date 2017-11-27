/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.timelineconsumedgeneratorforgiventsts;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author yedukondalu
 */
public class StartAndEndTS {
    private static Logger logger = Logger.getLogger(StartAndEndTS.class.getName());
    public static List getPeriodEndTS(Long epochtime, String period) {
        
        DateFormat format = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
        Date date = new Date(epochtime);
        
        System.out.println(format.format(date));
        int year = 0, month = 0, day = 0,week=0;
        String startDate=null;
        Calendar calen = Calendar.getInstance();
        calen.setTime(date);
        List tsList = new ArrayList();
        long milliseconds;
        
       // tsList.add(format.format(date));
        try {
            year = calen.get(Calendar.YEAR);

            for (int i = 0; i < 2; i++) {

                if (period.equalsIgnoreCase("Day")) {
                    System.out.println("in day");
                    month = calen.get(Calendar.MONTH);
                    day = calen.get(Calendar.DATE);
                    week=calen.get(Calendar.WEEK_OF_MONTH);
                    System.out.println(week);
                    
                    
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
                if (i == 0) {
                    calen.set(year, month, day, 00, 00, 00);
                } else {
                    calen.set(year, month, day+1, 00, 00, 00);
                }
                startDate = format.format(calen.getTime());
               milliseconds = format.parse(startDate).getTime();
              System.out.println("=====milliseconds======>" + milliseconds);
              
                tsList.add(startDate);
                tsList.add(milliseconds/1000);
                
            }

            System.out.println("===========>" + tsList);
            tsList.add(week);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);

        } finally {
            try {

                if (date != null) {
                    date = null;
                } else if (calen != null) {
                    calen.clear();
                    calen = null;
                } else if(format!=null){
                format=null;
                } else if(startDate!=null){
                startDate=null;
                }

            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e);
            }

        }
        return tsList;
    }
    
   /* public static String getDate(Long epochtime) {
        
        DateFormat format = new SimpleDateFormat("dd/MMM/yyyy ");
        Date date = new Date(epochtime);
        String curDate=null;
        
     try{   
         curDate=format.format(date);
        System.out.println(curDate);
        Calendar calen = new GregorianCalendar();
        calen.setTime(date);
        long milliseconds;
        milliseconds = date.getTime();
      System.out.println("=====Date in milliseconds======>" + milliseconds);
                
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);

        } finally {
            try {

                if (date != null) {
                    date = null;
                } else if(format!=null){
                format=null;
                }

            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e);
            }

        }
        return curDate;
    }
    public static int getElapsedMinutes(String period,String timestamp) {
        
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        Calendar calen = new GregorianCalendar();
        Calendar calenTS = new GregorianCalendar();
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
             
        System.out.println("sys"+date);
        
        int minutesHour,hoursDay,daysWeek,weekMonth,monthYear;
        int minutes=0;
        List tsmills=null;
        Date timestampDate=null;
        String[] spliteTS=null;
     try{   
        calen.setTime(date);
                    
                    
        
                    minutesHour = calen.get(Calendar.MINUTE);
                    System.out.println("Minutes in hour"+minutesHour);
                    hoursDay = calen.get(Calendar.HOUR_OF_DAY);
                    System.out.println("day in Hours"+hoursDay);
                    daysWeek = calen.get(Calendar.DAY_OF_WEEK)-1;
                    System.out.println("days in week"+daysWeek);
                    weekMonth = calen.get(Calendar.DAY_OF_MONTH)-1;
                    System.out.println("days in month"+weekMonth);
                    monthYear = calen.get(Calendar.DAY_OF_YEAR)-1;
                    System.out.println("days in year"+monthYear);
                    if(period.equalsIgnoreCase("Hour")) {
                            minutes=minutesHour;
                            System.out.println("Minutes in a hour "+minutesHour);
                       
                   }else if (period.equalsIgnoreCase("Day")) {
                       if(timestamp.contains(" ")){
                           String[] hrminute=timestamp.split(" ");
                           String[] hrminute1=hrminute[1].split(":");
                           minutes=(Integer.parseInt(hrminute1[0])*60)+Integer.parseInt(hrminute1[1]);
                       System.out.println("minutes in a Day "+minutes);
                       }else{
                    if (period.equalsIgnoreCase("Day") && (timestamp==null ||timestamp.equalsIgnoreCase("null"))){
                     minutes=(hoursDay*60)+minutesHour;
                     System.out.println("minutes in a Day "+minutes);
                       }else{
                     System.out.println("minutes in a Day "+timestamp);
                     
                     System.out.println("calendear time"+calen.getTime());
                     
                     tsmills=getPeriodEndTS(calen.getTimeInMillis(),"Day");
                     System.out.println("minutes in a Day "+calen.getTimeInMillis());
                     timestampDate=format.parse(timestamp);;
                     calenTS.setTime(timestampDate);
                     System.out.println("minutes in a Day "+calenTS.getTime());
                     System.out.println("minutes in a Day "+calenTS.getTimeInMillis());
                     if(calenTS.getTimeInMillis()>=((Long)tsmills.get(1)*1000) && calenTS.getTimeInMillis()<((Long)tsmills.get(3)*1000)){
                     minutes=(hoursDay*60)+minutesHour;
                      System.out.println("current date "+minutes);
                     }else{
                     minutes=24*60;
                     System.out.println("not current date "+minutes);
                            }
                        }
                       }
                   }else if (period.equalsIgnoreCase("week")) {
                     minutes=((daysWeek*24)*60)+(hoursDay*60)+minutesHour;
                     System.out.println("minutes in week "+minutes);
                     
                      
                   }else if (period.equalsIgnoreCase("Month")) {
                     minutes=(((weekMonth)*24)*60)+(hoursDay*60)+minutesHour;
                     System.out.println("minutes in month "+minutes);
                     
                      if (period.equalsIgnoreCase("Month") && (timestamp==null ||timestamp.equalsIgnoreCase("null"))){
                     minutes=(((weekMonth)*24)*60)+(hoursDay*60)+minutesHour;
                     System.out.println("minutes in month "+minutes);
                       }else{
                        
                     System.out.println("minutes in a Day "+timestamp);
                     spliteTS=timestamp.split("/");
                     String mon=spliteTS[1];
                     System.out.println("minutes in a Day "+spliteTS[0]);
                     System.out.println("minutes in a Day "+spliteTS[1]);
                     for(int i=0;i<list.size();i++){
                    if(mon.equalsIgnoreCase((String) list.get(i))){
                    timestamp=spliteTS[0]+"/"+(i+1)+"/"+spliteTS[2];
                     System.out.println("minutes in a Day "+timestamp);
                    }
                  }
                     tsmills=getPeriodEndTS(calen.getTimeInMillis(),"Month");
                     System.out.println("minutes in a Month "+calen.getTimeInMillis());
                     timestampDate=format.parse(timestamp);
                     calenTS.setTime(timestampDate);
                     System.out.println("minutes in a Month "+calenTS.getTime());
                     System.out.println("minutes in a Month "+calenTS.getTimeInMillis());
                     if(calenTS.getTimeInMillis()>=((Long)tsmills.get(1)*1000) && calenTS.getTimeInMillis()<((Long)tsmills.get(3)*1000)){
                     minutes=(((weekMonth)*24)*60)+(hoursDay*60)+minutesHour;
                      System.out.println("current month "+minutes);
                     }else{
                      System.out.println("current month "+calenTS.getActualMaximum(Calendar.DAY_OF_MONTH));
                     minutes=((calenTS.getActualMaximum(Calendar.DAY_OF_MONTH)*24)*60);
                     System.out.println("not current month "+minutes);
                            }
                        }
                   
                   }else if (period.equalsIgnoreCase("Year")) {
                     minutes=((monthYear*24)*60)+(hoursDay*60)+minutesHour;
                     System.out.println("minutes in year "+minutes);
                     if (period.equalsIgnoreCase("Month") && (timestamp==null ||timestamp.equalsIgnoreCase("null"))){
                     minutes=(((weekMonth)*24)*60)+(hoursDay*60)+minutesHour;
                     System.out.println("minutes in month "+minutes);
                       }else{
                        
                     System.out.println("minutes in a Day "+timestamp);
                     timestamp=timestamp+"/01/01";
                     tsmills=getPeriodEndTS(calen.getTimeInMillis(),"Year");
                     System.out.println("minutes in a year "+calen.getTimeInMillis());
                     timestampDate=format.parse(timestamp);
                     calenTS.setTime(timestampDate);
                     System.out.println(timestampDate);
                     System.out.println("minutes in a year "+calenTS.getTime());
                     System.out.println("minutes in a year "+calenTS.getTimeInMillis());
                     if(calenTS.getTimeInMillis()>=((Long)tsmills.get(1)*1000) && calenTS.getTimeInMillis()<((Long)tsmills.get(3)*1000)){
                     minutes=(((weekMonth)*24)*60)+(hoursDay*60)+minutesHour;
                      System.out.println("current year "+minutes);
                     }else{
                      System.out.println("current year "+calenTS.getActualMaximum(Calendar.DAY_OF_YEAR));
                     minutes=((calenTS.getActualMaximum(Calendar.DAY_OF_YEAR)*24)*60);
                     System.out.println("not current year "+minutes);
                            }
                        }
                   } 
                    
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);

        } finally {
            try {

                if (date != null) {
                    date = null;
                } else if(format!=null){
                format=null;
                }else if (calen!=null){
                calen.clear();
                calen=null;
                }else if (calenTS!=null){
                calenTS.clear();
                calenTS=null;
                }else if (list!=null){
                list.clear();
                list=null;
                }else if (tsmills!=null){
                tsmills.clear();
                tsmills=null;
                }else if (timestampDate!=null){
                timestampDate=null;
                }else if(spliteTS!=null){
                spliteTS=null;
                }

            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e);
            }

        }
        return minutes;
    }*/
    
    
}
