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

/****************************************************************************************************
 *This Class is used to get Start Date as current Date minus selected time period and current Date
 * @author satyaJay
 * **************************************************************************************************
 */
public class DateGenerator {
	@SuppressWarnings("deprecation")
	public static String[] dateGenerator(String interval) {
		String strDate[] = new String[2];
		Calendar now=Calendar.getInstance();
		try{
		String dateFormate1=DBUtilHelper.getMetrics_mapping_properties().getProperty("ReferenceTime");
		if(dateFormate1!=null){
			 SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			  Date dt = formatter1.parse(dateFormate1);
			  now.set((dt.getYear()+1900), dt.getMonth(), dt.getDate(), dt.getHours(), dt.getMinutes(), dt.getSeconds());
		}
		else
			now = Calendar.getInstance();
}
catch(Exception e){
	e.printStackTrace();
}
		if (interval.equalsIgnoreCase("hour")) {
			strDate[0] = now.get(Calendar.YEAR) + "-"
					+ (now.get(Calendar.MONTH) + 1) + "-"
					+ now.get(Calendar.DATE) + " "
					+ now.get(Calendar.HOUR_OF_DAY) + ":"
					+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
			strDate[1] = now.get(Calendar.YEAR) + "-"
					+ (now.get(Calendar.MONTH) + 1) + "-"
					+ now.get(Calendar.DATE) + " "
					+ (now.get(Calendar.HOUR_OF_DAY) - 1) + ":"
					+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);

		} else if (interval.equalsIgnoreCase("day")) {
			strDate[0] = now.get(Calendar.YEAR) + "-"
					+ (now.get(Calendar.MONTH) + 1) + "-"
					+ now.get(Calendar.DATE) + " "
					+ now.get(Calendar.HOUR_OF_DAY) + ":"
					+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
			if(now.get(Calendar.DATE)==1 && now.get(Calendar.MONTH)!=0){
				Calendar calendar = Calendar.getInstance();
				calendar.set(now.get(Calendar.YEAR) , now.get(Calendar.MONTH)-1, 1);
				strDate[1] = now.get(Calendar.YEAR) + "-"
				+ now.get(Calendar.MONTH) + "-"
				+ calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + " "
				+ now.get(Calendar.HOUR_OF_DAY) + ":"
				+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
			}
			else if(now.get(Calendar.DATE)==1 && now.get(Calendar.MONTH)==0){
				strDate[1] = now.get(Calendar.YEAR)-1 + "-"
				+ "12"+ "-"
				+ "31" + " "
				+ now.get(Calendar.HOUR_OF_DAY) + ":"
				+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
			}
			else
			strDate[1] = now.get(Calendar.YEAR) + "-"
					+ (now.get(Calendar.MONTH) + 1) + "-"
					+ (now.get(Calendar.DATE) - 1) + " "
					+ now.get(Calendar.HOUR_OF_DAY) + ":"
					+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);

		} else if (interval.equalsIgnoreCase("week")) {
			int diff = (now.get(Calendar.DATE) - 7);
			strDate[0] = now.get(Calendar.YEAR) + "-"
					+ (now.get(Calendar.MONTH) + 1) + "-"
					+ now.get(Calendar.DATE) + " "
					+ now.get(Calendar.HOUR_OF_DAY) + ":"
					+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
			if (diff <= 0) {
				Calendar calendar = Calendar.getInstance();
				int year = now.get(Calendar.YEAR);
				int month = (now.get(Calendar.MONTH)-1);
				int date = 1;
				calendar.set(year, month, date);
				int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				if(now.get(Calendar.MONTH)==0){
					strDate[1] = (now.get(Calendar.YEAR) - 1)+ "-" + "12"
					+ "-" + (diff + days) + " "
					+ now.get(Calendar.HOUR_OF_DAY) + ":"
					+ now.get(Calendar.MINUTE) + ":"
					+ now.get(Calendar.SECOND);
				}
				else{
					strDate[1] = now.get(Calendar.YEAR)+ "-" +(calendar.get(Calendar.MONTH)+1)
					+ "-" + (diff + days) + " "
					+ now.get(Calendar.HOUR_OF_DAY) + ":"
					+ now.get(Calendar.MINUTE) + ":"
					+ now.get(Calendar.SECOND);
				}
			} else {
				strDate[1] = now.get(Calendar.YEAR) + "-"
						+ (now.get(Calendar.MONTH) + 1) + "-"
						+ (now.get(Calendar.DATE) - 7) + " "
						+ now.get(Calendar.HOUR_OF_DAY) + ":"
						+ now.get(Calendar.MINUTE) + ":"
						+ now.get(Calendar.SECOND);
			}
		} else if (interval.equalsIgnoreCase("month")) {

			strDate[0] = now.get(Calendar.YEAR) + "-"
					+ (now.get(Calendar.MONTH) + 1) + "-"
					+ now.get(Calendar.DATE) + " "
					+ now.get(Calendar.HOUR_OF_DAY) + ":"
					+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
			if(now.get(Calendar.MONTH)==0){
			strDate[1] = (now.get(Calendar.YEAR) - 1)+ "-" + "12"
				+ "-" + now.get(Calendar.DATE) + " "
				+ now.get(Calendar.HOUR_OF_DAY) + ":"
				+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
			}
			else
			strDate[1] = now.get(Calendar.YEAR) + "-" + now.get(Calendar.MONTH)
					+ "-" + now.get(Calendar.DATE) + " "
					+ now.get(Calendar.HOUR_OF_DAY) + ":"
					+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);

		} else {
			strDate[0] = now.get(Calendar.YEAR) + "-"
					+ (now.get(Calendar.MONTH) + 1) + "-"
					+ now.get(Calendar.DATE) + " "
					+ now.get(Calendar.HOUR_OF_DAY) + ":"
					+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
			strDate[1] = (now.get(Calendar.YEAR) - 1) + "-"
					+ (now.get(Calendar.MONTH) + 1) + "-"
					+ now.get(Calendar.DATE) + " "
					+ now.get(Calendar.HOUR_OF_DAY) + ":"
					+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);

		}
		return strDate;

	}
public static void main(String ...a) {
	dateGenerator("week");
}

}