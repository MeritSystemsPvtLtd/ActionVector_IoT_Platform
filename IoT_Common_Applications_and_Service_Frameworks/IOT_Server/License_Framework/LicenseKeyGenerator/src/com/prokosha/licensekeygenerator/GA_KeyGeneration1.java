/***************************************************************************

                             Software Developed by
                            Merit Systems Pvt. Ltd.,
               No. 71/3, Elephant Rock Road, Jayanagar 3rd Block
                           Bangalore - 560 011, India
                 Work Created for Merit Systems Private Limited
                              All rights reserved

           THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT
                               LAWS AND TREATIES
        NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED,
              DISTRIBUTED, REVISED, MODIFIED,TRANSLATED, ABRIDGED,
                                   CONDENSED,
         EXPANDED, COLLECTED, COMPILED, LINKED, RECAST, TRANSFORMED OR
                                    ADAPTED
                       WITHOUT THE PRIOR WRITTEN CONSENT
           ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION
                                 COULD SUBJECT
                THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.


 ***************************************************************************/
package com.prokosha.licensekeygenerator;

/*
 This class is used to generate keys.  Here all the variables has be initialized( get from
 some other source) and generate the key according the variables initialized
 */

public class GA_KeyGeneration1 {
	/*
	 * Modules Here all the modules are defined. When defining a module define
	 * in Capital letters Intializing the module to NULL will make that module
	 * unavilable
	 */

	static String cServer = "SERVER";
	static String cDesktop = "DESKTOP";
	static String cDataBase = "DATABASE";
	static String cNetwork = "NETWORK";
	static String cJVM = "JVM";

	/*
	 * License Here all the licenses are defined. You have to define the number
	 * for each license variables which will be the license of that module
	 */

	static long llicense = 999;

	/*
	 * Distrubution type The distrubution type is a string. It can take any of
	 * four values "RELEASE","DEMO","TIMED", "EVAL"
	 */

	static String v1Version = "RELEASE";

	static String v1key1;
	static String v1key2;
	static String szFormattedKey1 = "";
	static String szFormattedKey2 = "";
	static int ick2;
	static int iValidate = -1;

	public GA_KeyGeneration1() {
	}

	/*
	 * This method will call XXXCreateKeys() method which will generate the keys
	 * and gives two sets of keys
	 */
	/*
	 * public void Generate_keys() { GA_KEY_INFORMATION1 KEY_INFORMATION = new
	 * GA_KEY_INFORMATION1(); //GA_CreateKeys1 ck2 = new GA_CreateKeys1(); ick2
	 * = GA_CreateKeys1.XXXCreateKeys(KEY_INFORMATION); v1key1 =
	 * (KEY_INFORMATION.szKey1).toString(); v1key2 =
	 * (KEY_INFORMATION.szKey2).toString();
	 * System.out.println(" first key is ...." + v1key1);
	 * System.out.println(" second key is ...." + v1key2);
	 *
	 * for (int iCount = 0; iCount < v1key1.length(); iCount++) { int intik =
	 * (int) v1key1.charAt(iCount); if ((intik <= 47) || (intik >= 91 && intik
	 * <= 96) || (intik >= 58 && intik <= 64) || (intik >= 123)) continue; else
	 * { szFormattedKey1 += v1key1.charAt(iCount); } } for (int iCount = 0;
	 * iCount < v1key2.length(); iCount++) { int intik = (int)
	 * v1key2.charAt(iCount); if ((intik <= 47) || (intik >= 91 && intik <= 96)
	 * || (intik >= 58 && intik <= 64) || (intik >= 123)) continue; else {
	 * szFormattedKey2 += v1key2.charAt(iCount); } } }
	 *
	 * /* This method will call XXXValidateKeys() method and validates the two
	 * sets of keys
	 */

	/*
	 * public int Validate_keys(java.lang.String szKey1, java.lang.String
	 * szKey2) { GA_KEY_INFORMATION1 KEY_INFORMATION = new
	 * GA_KEY_INFORMATION1(); //GA_CreateKeys1 ck2 = new GA_CreateKeys1();
	 *
	 * StringBuffer szKey1Buffer = new StringBuffer(szKey1);
	 *
	 * StringBuffer szKey2Buffer = new StringBuffer(szKey2);
	 *
	 * KEY_INFORMATION.szKey1 = szKey1Buffer; KEY_INFORMATION.szKey2 =
	 * szKey2Buffer;
	 *
	 * iValidate = GA_CreateKeys1.XXXValidateKeys(KEY_INFORMATION, 1); //
	 * System.out.println(" ivalidate here is ..."+iValidate); return iValidate;
	 * }
	 *
	 *
	 * public String getKey1() { return v1key1; }
	 *
	 * public String getKey2() { return v1key2; }
	 *
	 * public String getFormattedKey1() { return szFormattedKey1; }
	 *
	 * public String getFormattedKey2() { return szFormattedKey2; }
	 *
	 * public static void main(String args[]) { GA_KEY_INFORMATION1
	 * KEY_INFORMATION = new GA_KEY_INFORMATION1();
	 *
	 * /* gaKey.setRandomNum(randnum); System.out.println("randomString is :" +
	 * getRandomNum())
	 */;

	/*
	 * ick2 = GA_CreateKeys1.XXXCreateKeys(KEY_INFORMATION);
	 *
	 * v1key1 = (KEY_INFORMATION.szKey1).toString(); v1key2 =
	 * (KEY_INFORMATION.szKey2).toString();
	 *
	 * System.out.println(" first key is ...." + v1key1);
	 * System.out.println(" second key is ...." + v1key2);
	 *
	 * int ivalidate = GA_CreateKeys1.XXXValidateKeys(KEY_INFORMATION, 1);
	 * System.out.println(" ivalidate here is ..." + ivalidate);
	 *
	 * }
	 *
	 * public int firstKey(int key1) { return key1; }
	 *
	 * public int secondKey(int key2) { return key2; }
	 */
}
