package com.prokosha.licensekeygenerator;

public class UserKeyValidation {
	static int iValidate = -1;
	String userKey=null;

	public String getUserKey()
	{
		 userKey = "74919997pF9893675720333750821529";
		return userKey;
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
		try{
		iValidate = GA_CreateKeys1.XXXValidateKeys(KEY_INFORMATION, 1);
		System.out
				.println(" ivalidate in from after reading keys from text file  ..."
						+ iValidate);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out
			.println(" error in validating keys  ..."
					+ iValidate);
		}
		return iValidate;
	}
	public static int readFinalKey(String szFinalkey)
	 {
		 String szFinal1=szFinalkey.substring(0, 16);
		 System.out.println("In read final key method szFinal1 is "+szFinal1);
		 String szFinal2=szFinalkey.substring(16, 32);
		 System.out.println("In read final key method szFinal2 is "+szFinal2);
		 return Validate_keys(szFinal1, szFinal2);
	 }

	public static void main(String[] args){
		LicenseKeyGenerator wr=new LicenseKeyGenerator();
		UserKeyValidation usrKeyVal=new UserKeyValidation();
		 String keyFromReg=wr.getValue();
		 String userKey=usrKeyVal.getUserKey();
		 System.out.println("some key is :"+keyFromReg);

        try{
		 int UserKey=readFinalKey(userKey);
		 if(UserKey==0)
		 {
			 System.out.println("Key Validation is success");
		 }
		 if(UserKey==-1)
		 {
			 System.out.println("Key Validation is Failure");
		 }
		}
		catch(Exception e)
		{	System.out.println("Key Validation is Failure");
			e.printStackTrace();
		}

	}

}