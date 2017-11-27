package com.prokosha.licensekeygenerator;


import java.io.IOException;

import java.util.prefs.Preferences;

public class LicenseKeyGenerator {
	Preferences prefs;
	String defaultValue = "NA";
	String InstalledKey = "";// version="";

	public static void main(String[] args) {


		Installer insObj = new Installer();
		ValidateKey vKey = new ValidateKey();
		try {
			Installer.main(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String szRegKey1 = insObj.getSzKey1();
		String szRegKey2 = insObj.getSzKey2();
		String szRegTotKey = szRegKey1 + szRegKey2;

		System.out.println("szRegTotKey is ::" + szRegTotKey);
		vKey.storeFinalKey(szRegTotKey);

		LicenseKeyGenerator wr = new LicenseKeyGenerator();
		wr.setValue(szRegTotKey);

		// wr.getValue();
	}

	public LicenseKeyGenerator() {
		try {
			// HKEY_CURRENT_USER\Software\JavaSoft\Prefs
			// HKEY_LOCAL_MACHINE\Software\JavaSoft\Prefs
			prefs = Preferences.userNodeForPackage(LicenseKeyGenerator.class);// HKEY_CURRENT_USER

			// Preferences.systemNodeForPackage(LicenseKey.class);//HKEY_LOCAL_MACHINE
		} catch (Exception E) {
			System.out.println(E);
		}

	}// winReg

	public void setValue(String szRegTotKey) {
		try {
			prefs.put("AVSALicenseKey", szRegTotKey);
			prefs.flush();
			// prefs.put("Version","1.0.0");
		} catch (Exception E) {
			System.out.println(E);
		}
	}

	public String getValue() {
		try {
			InstalledKey = prefs.get("AVSALicenseKey", defaultValue);
			// version= prefs.get("Version", defaultValue);
			System.out.println("Installed key is :::" + InstalledKey);

		} catch (Exception E) {
			System.out.println(E);
		}
		return InstalledKey;
	}
}