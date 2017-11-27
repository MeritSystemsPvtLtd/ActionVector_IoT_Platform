package com.prokosha.licensekeygenerator;

/*
 class GA_CreateKeys
 This class is responsible for generating keys by using information
 This class is also responsible for getting the key information if the keys are given
 */

public class GA_CreateKeys1 {
	// Genric Varibles required for Key Generation

	static final int N_SUCCESS = 0;
	static final int N_FAILURE = -1;

	// This is the maximum size of any string buffer here. Increase it whenever
	// necessary
	static final int MAX_BUFFER_SIZE = 5012;

	// Module variables for key generation
	static final int SERVER_MODULE = 1;
	static final int DESKTOP_MODULE = 2;
	static final int DB_MODULE = 4;
	static final int NETWORK_MODULE = 8;
	static final int JVM_MODULE = 16;

	// Disturubtion variables for key generation

	static final char DEMO_KEY = 'D';
	static final char TIMED_KEY = 'T';
	static final char EVAL_KEY = 'E';
	static final char RELEASE_KEY = 'R';

	// #define GIFTAPP "Software\\Merit Systems\\GIFT"

	static int iCheck = 0;
	static int icounter = 0;
	static int TOTAL_MODULE = 0;

	/*
	 * Method XXXCreateKeys This method is responsible for generating two sets
	 * of keys and setting those to GA_KEY_INFORMATION object. This class takes
	 * the required parameter from GA_KEY_INFORMATION object and generate two
	 * sets of keys which are of 16 byte each
	 */

	static int XXXCreateKeys(GA_KEY_INFORMATION1 lpKeyInfo) {

		// GA_KeyGeneration1 kg = new GA_KeyGeneration1();

		// Getting the Information about Modules of GIFTask

		if ((GA_KeyGeneration1.cServer).equalsIgnoreCase("SERVER"))
			GA_KEY_INFORMATION1.cTotalmodule = SERVER_MODULE;
		if ((GA_KeyGeneration1.cDesktop).equalsIgnoreCase("DESKTOP"))
			GA_KEY_INFORMATION1.cTotalmodule = GA_KEY_INFORMATION1.cTotalmodule
					| DESKTOP_MODULE;
		if ((GA_KeyGeneration1.cDataBase).equalsIgnoreCase("DATABASE"))
			GA_KEY_INFORMATION1.cTotalmodule = GA_KEY_INFORMATION1.cTotalmodule
					| DB_MODULE;
		if ((GA_KeyGeneration1.cNetwork).equalsIgnoreCase("NETWORK"))
			GA_KEY_INFORMATION1.cTotalmodule = GA_KEY_INFORMATION1.cTotalmodule
					| NETWORK_MODULE;
		if ((GA_KeyGeneration1.cJVM).equalsIgnoreCase("JVM"))
			GA_KEY_INFORMATION1.cTotalmodule = GA_KEY_INFORMATION1.cTotalmodule
					| JVM_MODULE;

		// Getting the information about Distrubution type of GIFTask

		if ((GA_KeyGeneration1.v1Version).equalsIgnoreCase("DEMO"))
			GA_KEY_INFORMATION1.cVersion = DEMO_KEY;
		if ((GA_KeyGeneration1.v1Version).equalsIgnoreCase("TIMED"))
			GA_KEY_INFORMATION1.cVersion = TIMED_KEY;
		if ((GA_KeyGeneration1.v1Version).equalsIgnoreCase("EVAL"))
			GA_KEY_INFORMATION1.cVersion = EVAL_KEY;
		if ((GA_KeyGeneration1.v1Version).equalsIgnoreCase("RELEASE"))
			GA_KEY_INFORMATION1.cVersion = RELEASE_KEY;

		// Getting the information about Group license of application

		GA_KEY_INFORMATION1.llicense = GA_KeyGeneration1.llicense;

		StringBuffer szBuffer = new StringBuffer();
		StringBuffer szSerialNumber = new StringBuffer();
		StringBuffer szStartDate = new StringBuffer();

		StringBuffer szEndDate = new StringBuffer();
		StringBuffer szGrouplicenses = new StringBuffer();
		StringBuffer szLicense = new StringBuffer();
		StringBuffer szKey = new StringBuffer();
		StringBuffer szKey1temp = new StringBuffer();
		StringBuffer szKey1 = new StringBuffer();
		StringBuffer szKey2 = new StringBuffer();

		szBuffer.setLength(MAX_BUFFER_SIZE);
		szEndDate.setLength(MAX_BUFFER_SIZE);
		szGrouplicenses.setLength(MAX_BUFFER_SIZE);
		szStartDate.setLength(MAX_BUFFER_SIZE);
		szSerialNumber.setLength(MAX_BUFFER_SIZE);
		szLicense.setLength(MAX_BUFFER_SIZE);
		szKey.setLength(MAX_BUFFER_SIZE);
		szKey1temp.setLength(MAX_BUFFER_SIZE);
		szKey1.setLength(MAX_BUFFER_SIZE);
		szKey2.setLength(MAX_BUFFER_SIZE);

		long lserialNumber;
		long lstartDate;
		long lGrouplic;
		long lendDate;

		// Set the modules from 0th byte to 4th byte ( 5 byte module type)
		// Integer intObj = new Integer(GA_KEY_INFORMATION1.cTotalmodule);
		String temp_hex = Integer.toHexString(GA_KEY_INFORMATION1.cTotalmodule);
		System.out.println("GA_KEY_INFORMATION1.cTotalmodule::"
				+ GA_KEY_INFORMATION1.cTotalmodule);

		int int_hex = Integer.parseInt(temp_hex, 16);

		System.out.println(" temp hex is..." + temp_hex);
		System.out.println(" int hex is...." + int_hex);
		// String abc = String.valueOf(lpKeyInfo.cTotalmodule);
		String abc = temp_hex;
		int aln = abc.length();
		System.out.println(" int hex aln length is...." + aln);
		char ac;
		for (int ai = 0; ai < aln; ai++) {
			ac = abc.charAt(ai);
			szBuffer.setCharAt(ai, ac);
		}
		szBuffer.setLength(aln);
		int sl = szBuffer.length();
		System.out.println(" int hex sl length is...." + sl);
		if (sl < 5) {
			int a = XXX_Pad_Leading_Zeros(szBuffer, 5);
		}
		abc = szBuffer.toString();
		System.out.println(" int hex abc2 string is...." + abc);
		aln = abc.length();
		System.out.println(" int hex aln2 length is...." + aln);
		for (int ai = 0; ai < aln; ai++) {
			ac = abc.charAt(ai);
			szKey.setCharAt(ai, ac);
		}
		System.out.println(" int hex szKey firsttime  is...." + szKey);
		// Set the distrubution type - 5th byte ( 1 byte distrubution type)

		szKey.setCharAt(5, lpKeyInfo.cVersion);
		System.out.println(" int hex szKey second szKey  is...." + szKey);
		// Set the Serial Number 6th Byte to 10th Byte (5 byte serial number)

		lserialNumber = GA_KEY_INFORMATION1.lSerialNumber;

		System.out.println(" szKey first time lserialNumber  is...."
				+ lserialNumber);
		abc = szBuffer.toString();
		aln = abc.length();
		for (int ai = 0; ai < aln; ai++) {
			ac = abc.charAt(ai);
			szKey.setCharAt(ai, ac);
		}
		System.out.println(" int hex szKey third time szKey  is...." + szKey);

		// Set the distrubution type - 5th byte ( 1 byte distrubution type)

		szKey.setCharAt(5, GA_KEY_INFORMATION1.cVersion);
		System.out.println(" szKey third time lpKeyInfo.cVersion  is...."
				+ GA_KEY_INFORMATION1.cVersion);
		System.out.println(" int hex szKey fourth time szKey  is...." + szKey);

		// Set the Serial Number 6th Byte to 10th Byte (5 byte serial number)

		lserialNumber = GA_KEY_INFORMATION1.lSerialNumber;
		abc = szBuffer.toString();
		aln = abc.length();
		for (int ai = 0; ai < aln; ai++) {
			ac = abc.charAt(ai);
			szKey.setCharAt(ai, ac);
		}

		// Set the distrubution type - 5th byte ( 1 byte distrubution type)

		szKey.setCharAt(5, GA_KEY_INFORMATION1.cVersion);
		System.out.println(" szKey fouth time lpKeyInfo.cVersion  is...."
				+ GA_KEY_INFORMATION1.cVersion);
		System.out.println(" int hex szKey fifth time szKey  is...." + szKey);

		// Set the Serial Number 6th Byte to 10th Byte (5 byte serial number)

		lserialNumber = GA_KEY_INFORMATION1.lSerialNumber;
		abc = String.valueOf(lserialNumber);
		System.out.println(" after serialnumber passing abc is string is...."
				+ abc);
		aln = abc.length();
		for (int ai = 0; ai < aln; ai++) {
			ac = abc.charAt(ai);
			szBuffer.setCharAt(ai, ac);
		}
		szBuffer.setLength(aln);
		sl = szBuffer.length();
		if (sl < 5) {
			int a = XXX_Pad_Leading_Zeros(szBuffer, 5);
		}
		abc = szBuffer.toString();
		aln = abc.length();
		for (int ai = 0; ai < aln; ai++) {
			ac = abc.charAt(ai);
			szKey.setCharAt(ai + 6, ac);
		}
		System.out.println(" szKey sixth time szBuffer  is...." + szBuffer);
		System.out.println(" int hex szKey sixth time szKey  is...." + szKey);

		// Set the Task type liesence 11b1yte to 13th byte ( 3bytes)

		lstartDate = GA_KEY_INFORMATION1.lStartDate;
		abc = String.valueOf(lstartDate);
		System.out.println(" after start date passing abc is string is...."
				+ abc);

		aln = abc.length();

		for (int ai = 0; ai < aln; ai++) {
			ac = abc.charAt(ai);
			// System.out.println(" here what is szStartDate ...." +
			// szStartDate);
			szStartDate.setCharAt(ai, ac);
		}
		szStartDate.setLength(aln);
		if (szStartDate.length() < 3) {
			int c = XXX_Pad_Leading_Zeros(szStartDate, 3);
		}

		abc = szStartDate.toString();
		aln = abc.length();
		for (int ai = 0; ai < aln; ai++) {
			ac = abc.charAt(ai);
			szKey.setCharAt(ai + 11, ac);
		}
		System.out.println(" szKey sixth time szStartDate  is...."
				+ szStartDate);
		System.out.println(" int hex szKey seventh time szKey  is...." + szKey);

		// Set the Group liesence 14byte to 16th byte ( 3bytes)

		lGrouplic = GA_KEY_INFORMATION1.llicense;
		abc = String.valueOf(lGrouplic);

		aln = abc.length();

		for (int ai = 0; ai < aln; ai++) {
			ac = abc.charAt(ai);
			szGrouplicenses.setCharAt(ai, ac);
		}
		szGrouplicenses.setLength(aln);
		if (szGrouplicenses.length() < 3) {
			int c = XXX_Pad_Leading_Zeros(szGrouplicenses, 3);
		}

		abc = szGrouplicenses.toString();
		aln = abc.length();
		for (int ai = 0; ai < aln; ai++) {
			ac = abc.charAt(ai);
			szKey.setCharAt(ai + 14, ac);
		}
		System.out.println(" szKey 7th  time szGrouplicenses  is...."
				+ szGrouplicenses);

		System.out.println(" int hex szKey 8th time szKey  is...." + szKey);

		// Set the Agent liesence 17byte to 19h byte ( 3bytes)

		lendDate = lpKeyInfo.lEndDate;
		abc = String.valueOf(lendDate);

		aln = abc.length();

		for (int ai = 0; ai < aln; ai++) {
			ac = abc.charAt(ai);
			szEndDate.setCharAt(ai, ac);
		}
		szEndDate.setLength(aln);
		if (szEndDate.length() < 3) {
			int c = XXX_Pad_Leading_Zeros(szEndDate, 3);
		}

		abc = szEndDate.toString();
		aln = abc.length();
		for (int ai = 0; ai < aln; ai++) {
			ac = abc.charAt(ai);
			szKey.setCharAt(ai + 17, ac);
		}
		System.out.println(" szKey 8thth  time szEndDate  is...." + szEndDate);

		System.out.println(" int hex szKey last time szKey  is...." + szKey);

		System.out.println("The Key created is...." + szKey);

		// Now encrypt the key. Encode to ASCII value

		int b = Encrypt_no(szKey, szKey1);
		System.out.println("After encryption b value is ...." + b);

		String szValueKey = szKey1.toString();

		System.out.println("After encryption szValueKey value is ....."
				+ szValueKey);

		StringBuffer szValueKeyBuffer = new StringBuffer();
		// szValueKeyBuffer.setLength(27);
		szValueKeyBuffer.setLength(27);

		for (int ch = 0; ch < szValueKey.length(); ch++) {
			char iChar = szValueKey.charAt(ch);
			int intik = (int) iChar;

			if ((intik <= 47) || (intik >= 91 && intik <= 96)
					|| (intik >= 58 && intik <= 64) || (intik >= 123))
				continue;
			szValueKeyBuffer.setCharAt(ch, iChar);
		}
		System.out
				.println("After encryption szValueKeyBuffer decode value is ....."
						+ szValueKeyBuffer);
		// Divide the single key into two keys of same length (Each key length
		// is 12 byte)

		String sztemp1 = new String();
		String sztemp2 = new String();

		// sztemp1 = szValueKeyBuffer.toString().substring(0,13); //previus
		// sztemp2 = szValueKeyBuffer.toString().substring(13,26); //prevoius
		int szlength = szValueKeyBuffer.length();
		System.out.println("totLen::" + szlength);
		sztemp1 = szValueKeyBuffer.toString().substring(0, 13);
		sztemp2 = szValueKeyBuffer.toString().substring(13, 26);

		StringBuffer sztemp1Buffer = new StringBuffer(sztemp1);
		StringBuffer sztemp2Buffer = new StringBuffer(sztemp2);

		StringBuffer szCRCtemp1Buffer = new StringBuffer();
		szCRCtemp1Buffer.setLength(MAX_BUFFER_SIZE);
		StringBuffer szCRCtemp2Buffer = new StringBuffer();
		szCRCtemp2Buffer.setLength(MAX_BUFFER_SIZE);

		// Send First String buffer for generation of CRC byte

		int c = Encode_CRC(sztemp1Buffer);
		System.out.println("after encryption of half key1 is :" + c);

		System.out
				.println(" first crc is...." + c + "...for.." + sztemp1Buffer);

		// Append 2 byte of CRC bit to the starting of bit ( now data becomes
		// 14byte)

		System.out.println("szCRCtemp1Buffer.length() is :"
				+ szCRCtemp1Buffer.length());
		szCRCtemp1Buffer = Append_CRC(sztemp1Buffer, c, 255);
		// szCRCtemp1Buffer.setLength(16);
		szCRCtemp1Buffer.setLength(15);
		System.out.println("szCRCtemp1Buffer.length::"
				+ szCRCtemp1Buffer.length());
		System.out.println("After append method szCRCtemp1Buffer is :"
				+ szCRCtemp1Buffer);

		// Send Second String buffer for generation of CRC byte

		int e = Encode_CRC(sztemp2Buffer);
		System.out
				.println(" second crc is..." + e + "...for.." + sztemp2Buffer);

		// Append 2 byte of CRC bit to the starting of bit ( now data becomes
		// 14byte)

		szCRCtemp2Buffer = Append_CRC(sztemp2Buffer, e, 255);
		// szCRCtemp2Buffer.setLength(16);
		szCRCtemp2Buffer.setLength(15);

		String sCRCTemp1String = szCRCtemp1Buffer.toString();
		String sCRCTemp2String = szCRCtemp2Buffer.toString();

		System.out.println(" first key wih CRC is...." + sCRCTemp1String);
		System.out.println(" second key wih CRC is..." + sCRCTemp2String);

		// Concatenate two string to make one Key ( Now key becomes 28byte)

		String tempString = sCRCTemp1String + sCRCTemp2String;
		System.out.println("tempString length is : " + tempString.length()
				+ ", but string value is : " + tempString);

		StringBuffer tempStringBuffer = new StringBuffer(tempString);

		// Generate 2 byte of CRC for the whole key

		int g = Encode_CRC(tempStringBuffer);
		System.out.println(" final crc is...." + g + "...for.."
				+ tempStringBuffer);

		StringBuffer szTotalBuffer = new StringBuffer();
		szTotalBuffer.setLength(1024);

		// Append that CRC to Total String buffer (Now the total byte is 30byte)

		szTotalBuffer = Append_CRC(tempStringBuffer, g, 1024);
		szTotalBuffer.setLength(32);

		System.out.println(" the total output is...." + szTotalBuffer);

		String szTotal1 = new String();
		String szTotal2 = new String();

		// Divide the 32byte key into two 16 byte keys ( These keys needs to be
		// shipped)
		szTotal1 = szTotalBuffer.toString().substring(0, 16);
		szTotal2 = szTotalBuffer.toString().substring(16, 32);

		int iLength = szTotal1.length();
		int iLength2 = szTotal2.length();
		System.out.println("szTotal1 lenght is " + szTotal1);

		System.out.println("szTotal1 lenght iszzz " + szTotal2);

		StringBuffer sBufferKey1 = new StringBuffer();
		// sBufferKey1.setLength(18);
		sBufferKey1.setLength(16);

		StringBuffer sBufferKey2 = new StringBuffer();
		// sBufferKey2.setLength(18);
		sBufferKey2.setLength(16);

		for (int ch = 0; ch < iLength; ch++) {
			char iChar = szTotal1.charAt(ch);
			int intik = (int) iChar;
			if ((intik <= 47) || (intik >= 91 && intik <= 96)
					|| (intik >= 58 && intik <= 64) || (intik >= 123))
				continue;
			sBufferKey1.setCharAt(ch, iChar);
		}
		for (int ch = 0; ch < iLength2; ch++) {
			char iChar = szTotal2.charAt(ch);
			int intik = (int) iChar;
			if ((intik <= 47) || (intik >= 91 && intik <= 96)
					|| (intik >= 58 && intik <= 64) || (intik >= 123))
				continue;
			sBufferKey2.setCharAt(ch, iChar);
		}

		System.out.println(" Key1 Generated is......" + szTotal1);
		System.out.println(" Key1 Generated is......" + sBufferKey1);

		System.out.println(" Key2 Generated is......" + szTotal2);
		System.out.println(" Key2 Generated is......" + sBufferKey2);

		lpKeyInfo.szKey1 = sBufferKey1;
		lpKeyInfo.szKey2 = sBufferKey2;
		Installer ins = new Installer();
		ins.readKeys(lpKeyInfo.szKey1, lpKeyInfo.szKey2);

		return (N_SUCCESS);
	}

	/*
	 * Method XXXValidateKeys This method accepts GA_KEY_INFORMATION structure
	 * and validate the two sets of keys While validating it checks the CRC byte
	 * of each key and also CRC byte of whole key
	 */

	static int XXXValidateKeys(GA_KEY_INFORMATION1 lpKeyInfo, int piErrCode) {
		// long lLicenses = 0l;

		// Get the two keys from the Key Object

		StringBuffer sTotalValueKey = new StringBuffer();
		sTotalValueKey.setLength(36);

		// Generate the Total Key by adding two keys

		String sTotal_Key = (lpKeyInfo.szKey1).toString()
				+ (lpKeyInfo.szKey2).toString();
		System.out.println("Total_Key :::" + sTotal_Key);
		System.out.println("Total_Key length :::" + sTotal_Key.length());
		for (int ch = 0; ch < sTotal_Key.length(); ch++) {
			char iChar = sTotal_Key.charAt(ch);
			// System.out.println(iChar);
			int intik = (int) iChar;
			// System.out.println("intik is ASCII values ::" + intik);
			if ((intik <= 47) || (intik >= 91 && intik <= 96)
					|| (intik >= 58 && intik <= 64) || (intik >= 123))
				continue;
			sTotalValueKey.setCharAt(ch, iChar);
		}
		System.out.println(" sTotalValueKey key is ...." + sTotalValueKey);

		// Get the first two byte of total Key

		String sTotalCRCString = sTotalValueKey.substring(0, 2);
		int iTotalCRCInt = 0;
		try {
			System.out.println("TotalCRCString::" + sTotalCRCString);
			iTotalCRCInt = Integer.parseInt(sTotalCRCString);
		} catch (Exception e1) {
			System.out
					.println("Exception Caught at parseInt Please check for keys : sTotalCRCString"
							+ sTotalCRCString);
			e1.printStackTrace();
		}

		// Saparate the other 30 byte of key and generate CRC for that key

		String sTotalCRCCheck = sTotalValueKey.substring(2,
				sTotalValueKey.length());
		StringBuffer szTotalCRCBuffer = new StringBuffer(sTotalCRCCheck);
		szTotalCRCBuffer.setLength(30);
		System.out.println("szTotalCRCBuffer is @@@@@" + szTotalCRCBuffer);
		int i = Encode_CRC(szTotalCRCBuffer);

		System.out.println(" FIRST CRC is..." + iTotalCRCInt
				+ "...CRC Count is..." + i);

		// Compare generated CRC with key CRC and if they are incorrect then
		// send error

		if (i != iTotalCRCInt) {
			System.out.println("CRC didn't match");
			return (N_FAILURE);
		} else {
			// Divide the generated key into two keys of 14 byte each

			String sKey1Index = sTotalCRCCheck.substring(0, 15);
			String sKey2Index = sTotalCRCCheck.substring(15,
					sTotalCRCCheck.length());

			// Get the first two byte of first key

			String sKey1CRCString = sKey1Index.substring(0, 2);
			System.out.println("Key Value at 617::::::" + sKey1CRCString);
			int iKey1CRCInt = Integer.parseInt(sKey1CRCString);

			// Generate the CRC for the other

			String sKey1CRCCheckString = sKey1Index.substring(2,
					sKey1Index.length());
			StringBuffer sKey1CRCCheckBuffer = new StringBuffer(
					sKey1CRCCheckString);

			// String crc_temp = sKey1CRCCheckBuffer.toString();

			/*
			 * for (int ii = 0; ii < crc_temp.length(); ii++) { char ccheck =
			 * crc_temp.charAt(ii); }
			 */

			int j = Encode_CRC(sKey1CRCCheckBuffer);
			// System.out.println(" First CRC is..."+j+"  for  "+sKey1CRCCheckBuffer);

			// System.out.println("Second CRC is..."+iKey1CRCInt+"...CRC Count is..."+j);

			// Compare the generated CRC with CRC of key

			if (j != iKey1CRCInt) {
				System.out.println(" First CRC is not correct");
				return (N_FAILURE);
			} else {
				// Get first two byte of second key

				String sKey2CRCString = sKey2Index.substring(0, 2);
				int iKey2CRCInt = Integer.parseInt(sKey2CRCString);

				// Generate the CRC of second key

				String sKey2CRCCheckString = sKey2Index.substring(2,
						sKey2Index.length());
				StringBuffer sKey2CRCCheckBuffer = new StringBuffer(
						sKey2CRCCheckString);

				int k = Encode_CRC(sKey2CRCCheckBuffer);

				// System.out.println("Third CRC is..."+iKey2CRCInt+"...CRC Count is..."+k);

				// Compare the two CRC's

				if (k != iKey2CRCInt) {
					System.out.println(" Second CRC is not correct");
					return (N_FAILURE);
				}

				// Get the concatedned string of two keys ( 26 byte string)

				String sEncryKey = sKey1CRCCheckString + sKey2CRCCheckString;

				StringBuffer sEncryKeyBuffer = new StringBuffer(sEncryKey);
				sEncryKeyBuffer.setLength(27);

				// Decrypt the string

				StringBuffer temp1Buffer = dec(sEncryKeyBuffer);

				// Original key will ge generated

				// System.out.println(" Original key is ...."+temp1Buffer);

				lpKeyInfo.szTotalKey = temp1Buffer;
			}
		}
		return 0;
	}

	/*
	 * Method Encrypt_no This will take any String buffer and send this across
	 * to Encryption mechanism
	 */

	static int Encrypt_no(StringBuffer sz1, StringBuffer sz2) {
		int ism;
		StringBuffer szstr = new StringBuffer();
		szstr.setLength(MAX_BUFFER_SIZE);
		String abc = sz1.toString();
		System.out.println(" total key string is encrypt method " + abc);

		int aln = abc.length();
		char ac;
		for (int ai = 0; ai < aln; ai++) {
			ac = abc.charAt(ai);
			szstr.setCharAt(ai, ac);
		}

		// here we are setting length based on how much keysnumbers we want here
		// 26
		// based on this change above key length
		sz1.setLength(26);
		szstr.setLength(26);
		System.out.println(" total szstrkey string is encrypt method szstr "
				+ szstr);
		System.out.println(" total sz2 string is encrypt method szstr " + sz2);

		int encr = enc(sz1, sz2);
		System.out.println(" here encryption is o mean success: " + encr);
		ism = iCheck;
		iCheck = 0;

		return 0;
	}

	/*
	 * Method XXX_Pad_Leading_Zeros This method takes the String buffer and padd
	 * O into left of String upto iBufLen bytes
	 */

	static int XXX_Pad_Leading_Zeros(StringBuffer pszBuffer, int iBufLen) {
		int i, j = 0;
		int iLen = pszBuffer.length();
		i = iLen;
		j = iBufLen;
		int k = j - i;
		pszBuffer.setLength(iBufLen);
		for (; k > 0; k--) {
			pszBuffer.insert(0, "0");
		}
		return (N_SUCCESS);
	}

	/*
	 * Method enc This method takes String buffer,encrypt and put the result
	 * string in String buffef During encryption it takes each values,
	 * calculates ascii value of each and convert into interger equivalent of
	 * ASCII value
	 */

	static int enc(StringBuffer sznumber, StringBuffer szEnctptd1) {
		int ai, aik, ilen, itemp = 0;
		// int iTotal = 0;
		int itemp1 = 0;

		char szEncoded1[] = { '9', '7', '8', '5', '6', '1', '0', '4', '2', '3',
				'o', 'n', 'k', 'g', 'z', 'p', 'm', 'e', 'l', 's', 't', 'x',
				'q', 'r', 'y', 'b', 'h', 'f', 'a', 'j', 'c', 'i', 'd', 'u',
				'w', 'v' };

		ilen = sznumber.length();

		System.out.println("sznumber is..." + sznumber);

		for (ai = 0; ai <= ilen - 1; ai++) {
			char t = sznumber.charAt(ai);
			aik = (int) t;

			if (Character.isDigit(t)) {
				itemp = (aik - 48) % 10;
				sznumber.setCharAt(ai, szEncoded1[itemp]);
			}
			if (Character.isLetter(t) && Character.isLowerCase(t)) {
				itemp = (aik - 96);

				if (itemp > 0 && itemp < 9) {
					itemp1 = itemp % 10 + 9;
				}
				if (itemp > 9 && itemp < 19) {
					itemp1 = itemp % 10 + 19;
				}
				if (itemp > 19) {
					itemp1 = itemp1 % 10 + 29;
				}
				sznumber.setCharAt(ai, szEncoded1[itemp1]);
			}
			if (Character.isLetter(t) && Character.isUpperCase(t)) {
				itemp = (aik - 64);
				if (itemp > 0 && itemp < 9) {
					itemp1 = itemp % 10 + 9;
				}
				if (itemp > 9 && itemp < 19) {
					itemp1 = itemp % 10 + 19;
				}
				if (itemp > 19) {
					itemp1 = itemp1 % 10 + 29;
				}
				sznumber.setCharAt(ai,
						(Character.toUpperCase(szEncoded1[itemp1])));
			}
		}

		String abc = sznumber.toString();
		System.out.println(" after half of Encrypted key is....." + abc);

		int aln = abc.length();
		char ac;
		for (int ch = 0; ch < aln; ch++) {
			ac = abc.charAt(ch);
			int intik = (int) ac;
			if ((intik <= 47) || (intik >= 91 && intik <= 96)
					|| (intik >= 58 && intik <= 64) || (intik >= 123))
				continue;
			szEnctptd1.setCharAt(ch, ac);
		}

		System.out.println(" Encrypted key is....." + szEnctptd1);
		return (0);
	}

	/*
	 * Method dec This method takes the Stringbuffer and decrypt the value which
	 * is exactly opposite to encryption mechanism
	 */

	static StringBuffer dec(StringBuffer sznumber) {
		int ai, ilen;
		// int iTotal = 0;
		// int ai1 = 0;

		char szEncoded1[] = { '9', '7', '8', '5', '6', '1', '0', '4', '2', '3',
				'o', 'n', 'k', 'g', 'z', 'p', 'm', 'e', 'l', 's', 't', 'x',
				'q', 'r', 'y', 'b', 'h', 'f', 'a', 'j', 'c', 'i', 'd', 'u',
				'w', 'v' };
		char szDecoded1[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
				'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
				'y', 'z' };

		ilen = sznumber.length();
		StringBuffer tempBuffer = new StringBuffer();
		tempBuffer.setLength(26);

		StringBuffer tempBuffer1 = new StringBuffer();
		tempBuffer1.setLength(26);

		for (ai = 0; ai <= ilen - 1; ai++) {
			for (int al = 0; al < szEncoded1.length; al++) {
				boolean flag = false;
				if ((szEncoded1[al] == sznumber.charAt(ai))) {
					tempBuffer.setCharAt(ai, szDecoded1[al]);
					flag = true;
				}
				if (szEncoded1[al] == Character
						.toLowerCase(sznumber.charAt(ai)) && flag == false) {
					tempBuffer.setCharAt(ai,
							Character.toUpperCase(szDecoded1[al]));
				}
			}
		}
		String abc = tempBuffer.toString();
		int aln = abc.length();
		char ac;
		for (int ch = 0; ch < aln; ch++) {
			ac = abc.charAt(ch);
			int intik = (int) ac;
			if ((intik <= 47) || (intik >= 91 && intik <= 96)
					|| (intik >= 58 && intik <= 64) || (intik >= 123))
				continue;
			tempBuffer1.setCharAt(ch, ac);
		}

		System.out.println(" Encoded is...." + sznumber);
		System.out.println(" Decoded is...." + tempBuffer);

		return tempBuffer;
	}

	/*
	 * Method Encode_CRC This method takes the String buffer and gen799997pF9554075erate the
	 * CRC byte for the same
	 */

	static int Encode_CRC(StringBuffer sztemp1Buffer) {
		int ilen = sztemp1Buffer.length();
		System.out.println("here lenght in Encode_CRC method for first key : "
				+ ilen);
		String stempString = sztemp1Buffer.toString();
		int iSum = 0;
		int iTemp = 0;
		int iCRC = 0;
		for (int ch = 0; ch < ilen; ch++) {
			char cac = stempString.charAt(ch);
			iTemp = (int) cac;
			iSum += iTemp;
		}
		iCRC = iSum % 100;

		return iCRC;
	}

	// This method append 2 digits to our key every time when we are calling
	static StringBuffer Append_CRC(StringBuffer sztemp1Buffer, int iCRC,
			int size) {
		Integer ICRC = new Integer(iCRC);
		System.out.println("In Append_CRC method ICRC is :" + ICRC);

		String abc = ICRC.toString();
		int aln = abc.length();

		StringBuffer sztempStringBuffer = new StringBuffer();
		sztempStringBuffer.setLength(size);

		StringBuffer tempCRCBuffer = new StringBuffer();
		tempCRCBuffer.setLength(size);

		for (int ch = 0; ch < aln; ch++) {
			char ac = abc.charAt(ch);
			sztempStringBuffer.setCharAt(ch, ac);
		}
		System.out.println("In Append_CRC method sztempStringBuffer is :"
				+ sztempStringBuffer);

		sztempStringBuffer.setLength(aln);
		if (sztempStringBuffer.length() < 2) {
			int c = XXX_Pad_Leading_Zeros(sztempStringBuffer, 2);
		}

		String sXYZ = sztempStringBuffer.toString();
		int iLENGTH = sXYZ.length();
		char cCHAR;
		for (int ch = 0; ch < iLENGTH; ch++) {
			cCHAR = sXYZ.charAt(ch);
			tempCRCBuffer.setCharAt(ch, cCHAR);
		}
		System.out.println("In Append_CRC method tempCRCBuffer is :"
				+ tempCRCBuffer);

		String xyz = sztemp1Buffer.toString();
		int ilength = xyz.length();
		char cac;
		for (int ch = 0; ch < ilength; ch++) {
			cac = xyz.charAt(ch);
			int intik = (int) cac;
			if ((intik <= 47) || (intik >= 91 && intik <= 96)
					|| (intik >= 58 && intik <= 64) || (intik >= 123))
				continue;

			tempCRCBuffer.setCharAt(ch + 2, cac);
		}
		System.out.println("In Append_CRC method tempCRCBuffer is :"
				+ tempCRCBuffer);
		return tempCRCBuffer;
	}
}

/*
 * class GIFT_KEY_INFORMATION This is generic structure for key generation. This
 * whole object will get passed to Creae Keys and retrive keys
 */

class GA_KEY_INFORMATION1 {
	static final int MAX_BUFFER_SIZE = 255;
	static int cServer;
	static int cDesktop;
	static int cDataBase;
	static int cNetwork;
	static int cJVM;
	static int cTotalmodule;
	static char cVersion;
	static long lStartDate;
	static long lEndDate;
	static long lSerialNumber;
	static long llicense;
	StringBuffer szKey1 = new StringBuffer(MAX_BUFFER_SIZE);
	StringBuffer szKey2 = new StringBuffer(MAX_BUFFER_SIZE);
	StringBuffer szTotalKey = new StringBuffer(MAX_BUFFER_SIZE);
}
