package com.prokosha.licensekeygenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Installer {
	static String Key1 = null;
	static String Key2 = null;
	static long lSerialNumber;
	static long lStartDate;
	static long lEndDate;

	public void readKeys(StringBuffer szKey1, StringBuffer szKey2) {
		// TODO Auto-generated method stub
		String Key1 = szKey1.toString();
		String Key2 = szKey2.toString();
		System.out.println("szKey1 in Installer class : " + Key1);
		System.out.println("szKey2 in Installer class: " + Key2);
		setSzKey1(Key1);
		setSzKey2(Key2);
	}

	public static int getRandomNumberBetween(int min, int max) {
		Random randnum = new Random();
		int randomNumber = randnum.nextInt(max - min) + min;
		System.out.println("randomnumberis :" + randomNumber);
		return randomNumber;

	}

	public void setSzKey1(String Key1) {
		Installer.Key1 = Key1;
	}

	public String getSzKey1() {
		return Key1;
	}

	public void setSzKey2(String Key2) {
		Installer.Key2 = Key2;
	}

	public String getSzKey2() {
		return Key2;
	}

	public static void main(String[] args) throws IOException {
		Installer insObj = new Installer();
		GA_KEY_INFORMATION1 KEY_INFORMATION = new GA_KEY_INFORMATION1();
		int randnum = Installer.getRandomNumberBetween(1000, 9999);

		GA_KEY_INFORMATION1.lSerialNumber = randnum;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		// get current date time with Date()
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		long startDate = 0;
		try {
			startDate = dateFormat.parse(dateFormat.format(date)).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// gaKey.setStartDate(startDate);

		GA_KEY_INFORMATION1.lStartDate = startDate;

		String untildate = dateFormat.format(date);// can take any date in
													// current format
		// SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd" );
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(dateFormat.parse(untildate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cal.add(Calendar.DATE, 30);
		String convertedDate = dateFormat.format(cal.getTime());
		System.out.println("Date increase by 30 days.." + convertedDate);

		DateFormat formatter;
		// Date date ;
		formatter = new SimpleDateFormat("yyyy/MM/dd");
		try {
			date = (Date) formatter.parse(convertedDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long lEndDate = date.getTime();
		System.out.println("Today is " + lEndDate);
		// gaKey.setEndDate(lEndDate);
		GA_KEY_INFORMATION1.lEndDate = lEndDate;

		int key1 = GA_CreateKeys1.XXXCreateKeys(KEY_INFORMATION);
		System.out.println("key is in installer class " + key1);
		String szRegKey1 = insObj.getSzKey1();
		String szRegKey2 = insObj.getSzKey2();
		String szRegTotKey = szRegKey1 + szRegKey2;
		System.out.println("The final key in Installer class is::"
				+ szRegTotKey);

		try {
			String userHomeFolder = System.getProperty("user.home");
			File textFile = new File(userHomeFolder, "encryptedKey.txt");
			BufferedWriter out = new BufferedWriter(new FileWriter(textFile));
			String fileKey = insObj.getSzKey1() + insObj.getSzKey2();
			out.write("key=" + fileKey);
			// out.write("key2="+insObj.getSzKey2());
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Exception in write data to file ");
		}

		/*
		 * int ivalidate = GA_CreateKeys1.XXXValidateKeys(KEY_INFORMATION,1);
		 * System.out.println(" ivalidate here is ..."+ivalidate);
		 */

		/*
		 * //its using for setting expiry date ....... Calendar expiry =
		 * Calendar.getInstance(); expiry.set(2010, 1, 31,0,0); // Expire at 31
		 * Jan 2010 Calendar now = Calendar.getInstance(); // If you don't trust
		 * client's clock, fetch time from some reliable time server if(
		 * now.after(expiry)){ // Exit with proper expiry message } else { //
		 * let the customer enjoy your software }
		 */

	}

}
