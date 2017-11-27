package com.prokosha.licensekeygenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ValidateKey {
	static int iValidate = -1;
	public static String PREF_KEY;
	public static String FinalKey;

	public void storeFinalKey(String szFinalKey) {
		String storeFinalKey = szFinalKey;
		FinalKey = szFinalKey;
		System.out.println("storing FinalKey is :::" + storeFinalKey);

	}

	public static int Validate_keys(java.lang.String szKey1,
			java.lang.String szKey2) {
		GA_KEY_INFORMATION1 KEY_INFORMATION = new GA_KEY_INFORMATION1();

		StringBuffer szKey1Buffer = new StringBuffer(szKey1);
		StringBuffer szKey2Buffer = new StringBuffer(szKey2);
		KEY_INFORMATION.szKey1 = szKey1Buffer;
		KEY_INFORMATION.szKey2 = szKey2Buffer;
		System.out.println("szkey1 is ::" + KEY_INFORMATION.szKey1);
		System.out.println("szkey1 is ::" + KEY_INFORMATION.szKey2);
		try {
			iValidate = GA_CreateKeys1.XXXValidateKeys(KEY_INFORMATION, 1);
			System.out
					.println(" ivalidate in from after reading keys from text file  ..."
							+ iValidate);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(" error in validating keys  ..." + iValidate);
		}
		return iValidate;
	}

	public static int readFinalKey(String szFinalkey) {
		String szFinal1 = szFinalkey.substring(0, 16);
		System.out.println("In read final key method szFinal1 is " + szFinal1);
		String szFinal2 = szFinalkey.substring(16, 32);
		System.out.println("In read final key method szFinal2 is " + szFinal2);
		return Validate_keys(szFinal1, szFinal2);
	}

	// this method for reading keys from text file .....
	public static String[] readFile(String fileName) throws IOException {
		String key[] = new String[2];
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		String lines;
		try {
			while ((lines = br.readLine()) != null) {
				String[] value = lines.split("=");
				if (lines.startsWith("key=") && key[0] == null) {
					if (value.length <= 1) {
						throw new IOException("Missing key information");
					} else {
						key[0] = value[1];
						System.out.println("key value here is " + key[0]);
					}
				}

				/*
				 * if (lines.startsWith("key2=") && key[1] == null) { if
				 * (value.length <= 1) { throw new
				 * IOException("Missing key2 information"); } else { key[1] =
				 * value[1]; System.out.println("key2 value here is " + key[1]);
				 * } }
				 */

				else
					continue;
			}
			String keyFromFile = key[0].toString();
			System.out.println("key from file is :" + keyFromFile);
			// String stringKey2 = key[1].toString();
			String szFinal1 = keyFromFile.substring(0, 16);
			System.out.println("In read final key method szFinal1 is "
					+ szFinal1);
			String szFinal2 = keyFromFile.substring(16, 32);
			System.out.println("In read final key method szFinal2 is "
					+ szFinal2);
			Validate_keys(szFinal1, szFinal2);

			// Validate_keys(stringKey1, stringKey2);

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return key;
	}

	public static void main(String[] args) throws IOException {

		try {

			String seperator=System.getProperty("file.separator");
			String home = System.getProperty("user.home");
			String keyfileName = home+seperator+"encryptedKey.txt";
			//String keyfileName = "/home/gajendrak/encryptedKey.txt";

			readFile(keyfileName);
		} catch (Exception e) {
			System.out.println("exception in getting key from file");
			e.printStackTrace();
		}
		try {

			LicenseKeyGenerator wr = new LicenseKeyGenerator();
			String keyFromReg = wr.getValue();
			System.out.println("Key from Registry is :" + keyFromReg);
			int keyfromreg = readFinalKey(keyFromReg);
			if (keyfromreg == 0) {
				System.out.println("Key Validation is success");
			}
			if (keyfromreg == -1) {
				System.out.println("Key Validation is Failure");
			}
		} catch (Exception e) {
			System.out.println("Key Validation is Failure");
			e.printStackTrace();
		}
	}

}
