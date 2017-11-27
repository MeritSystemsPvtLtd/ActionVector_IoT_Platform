/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prokosha.timelineaggregator.hourts;

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
public class StartAndEndHourTS {
 private static Logger logger = Logger.getLogger(StartAndEndHourTS.class.getName());
    public static List getPeriodEndTS(Long epochtime, String period) {
        
        DateFormat format = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
        Date date = new Date(epochtime);
        
        logger.info(format.format(date));
        int year = 0, month = 0, day = 0,week=0,hour=0;
        String startDate=null;
        Calendar calen = new GregorianCalendar();
        calen.setTime(date);
        List tsList = new ArrayList();
        long milliseconds;
        
       // tsList.add(format.format(date));
        try {
            year = calen.get(Calendar.YEAR);

            for (int i = 0; i < 2; i++) {
                
                 if (period.equalsIgnoreCase("Hour")) {
                    logger.info("in Hour");
                    month = calen.get(Calendar.MONTH);
                    day = calen.get(Calendar.DATE);
                    hour=calen.get(Calendar.HOUR_OF_DAY);
                     week=calen.get(Calendar.WEEK_OF_MONTH);
                 }else if (period.equalsIgnoreCase("Day")) {
                    logger.info("in day");
                    month = calen.get(Calendar.MONTH);
                    day = calen.get(Calendar.DATE);
                    week=calen.get(Calendar.WEEK_OF_MONTH);
                    logger.info(week);
                    
                } /*else if (period.equalsIgnoreCase("Month")) {
                    logger.info("in month");
                    if (i == 0) {
                        day = calen.getActualMinimum(Calendar.DATE);
                    } else {
                        day = calen.getActualMaximum(Calendar.DATE);
                    }
                    month = calen.get(Calendar.MONTH);
                } else if (period.equalsIgnoreCase("year")) {
                    logger.info("in year");
                    if (i == 0) {
                        month = calen.getActualMinimum(Calendar.MONTH);
                        day = calen.getActualMinimum(Calendar.DATE);;
                    } else {
                        month = month = calen.getActualMaximum(Calendar.MONTH);;
                        day = calen.getActualMaximum(Calendar.DATE);
                    }
                }*/
                if(period.equalsIgnoreCase("Hour")){
                if (i == 0) {
                    calen.set(year, month, day, hour-1, 00, 00);
                } else {
                    calen.set(year, month, day, hour+1, 00, 00);
                }
                }else{
                if (i == 0) {
                    calen.set(year, month, day, 00, 00, 00);
                } else {
                    calen.set(year, month, day, 23, 59, 59);
                }
                }
                startDate = format.format(calen.getTime());
               milliseconds = format.parse(startDate).getTime();
                tsList.add(startDate);
                tsList.add(milliseconds/1000);
                
            }

            logger.info("===========>" + tsList);
            
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
  
    
    
}
