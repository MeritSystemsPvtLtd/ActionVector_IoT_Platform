package license;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

public class LicenseServiceImpl implements LicenseService {

    private String licenseKey = new String();
    private static Logger logger = null;
    HttpServletRequest request;

    public LicenseServiceImpl(javax.servlet.http.HttpServletRequest request) {
        try {
            this.request = request;
            logger = Logger.getLogger("LicenseServiceImpl");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    /*
     * Input: Label value pairs
     * Description: Builds licenseKey as comma separated string of label-value pairs and stores in
     * the database table CustomerInfoTable
     * Returns: Success or Failure
     */

    public String createLicense(String customerName, String productId,
            String licenseType, String timeStamp, String timeZone, String startDate,
            String endDate, String optionalAttribs) {

        int retVal = FAILURE;
        char sep = ',';

        try {

            System.out.println("In License: " + customerName + " " + productId + " " + licenseType + " " + timeStamp + " " + timeZone + " " + endDate + " " + optionalAttribs);
            LicenseDao licenseDao = new LicenseDao(request);

            if (customerName != null) {
                licenseKey = customerName;
            } else {
                System.out.println("createLicense(): customerName is null");
                return "" + FAILURE;
            }
            licenseKey += sep;
            //  System.out.println("In License():1 " + licenseKey);
            if (productId != null) {
                licenseKey = licenseKey.concat(productId);
            } else {
                System.out.println("createLicense(): Product id is null");
                return "" + FAILURE;
            }
            licenseKey += sep;
            //   System.out.println("In License():2 " + licenseKey);
            if (licenseType != null) {
                licenseKey = licenseKey.concat(licenseType);
            } else {
                System.out.println("createLicense(): License type is null");
                return "" + FAILURE;
            }
            licenseKey += sep;
            if (timeZone != null) {
                licenseKey = licenseKey.concat(timeZone);
            } else {
                System.out.println("createLicense(): TimeZone is null");
                return "" + FAILURE;
            }
            licenseKey += sep;
            if (startDate != null) {
                licenseKey = licenseKey.concat(startDate);
            } else {
                System.out.println("createLicense(): Start Date is null");
                return "" + FAILURE;
            }
            licenseKey += sep;
            if (endDate != null) {
                licenseKey = licenseKey.concat(endDate);
            } else {
                System.out.println("createLicense(): End Date is null");
                return "" + FAILURE;
            }
            //Optional attributes can be null
            if (optionalAttribs != null) {
                licenseKey += sep;
                licenseKey = licenseKey.concat(optionalAttribs);
            } else {
                System.out.println("createLicense(): Optional attributes are null");
            }
            //  System.out.println("In License: " + licenseKey);

            //Read customerId,  productId values into cusId, and prodId
            String scustName = customerName;
            System.out.println("scustName==>" + scustName);
            String sprodId = productId;
            System.out.println("sprodId===>" + sprodId);
            String tStamp = timeStamp;
            System.out.println("License: tStamp=" + tStamp);

            //Encrypt licenseKey here - 14-Jan-2015
            String encryptedLicenseKey = encryptLicenseKey(scustName, sprodId, licenseKey);
            System.out.println("encryptedLicenseKe==>" + encryptedLicenseKey);

            //Insert customerId, productId, licenseKey into database table CustomerInfo.
            if (encryptedLicenseKey != null) {
                retVal = licenseDao.updateCustomerLicense(customerName, sprodId, tStamp, encryptedLicenseKey);
            } else {
                System.out.println("Encrytped key is null. Returning FAILURE...");
                return "" + FAILURE;
            }
            if (retVal != SUCCESS) {
                System.out.println("Could not add license details");
                return "" + FAILURE;
            }
        } catch (NullPointerException e1) {
            e1.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Licenses details added successfully");
        return "" + SUCCESS;
    }

    /* 28-Jan-2015
     * Input: 	customerId - String value
     * 			productId - String value
     * 			optionalLabel - label string
     */
    public String checkLicense(String customerName, String productId, String optionalLabel) {

        String licenseStatus = "" + FAILURE;
        try {

            LicenseDao licenseDao = new LicenseDao(request);

            if (customerName == null) {
                return "" + FAILURE;
            }

            if (productId == null) {
                return "" + FAILURE;
            }

            if (licenseDao.getCustomerLicenseKey(customerName, productId) != SUCCESS) {
                System.out.println("checkCustomerTbl() returned null");
                return "Customer's License Key is not available..." + CUSTOMER_DOES_NOT_EXIST;
            }
            System.out.println("License(): license key is " + licenseDao.licenseKeyStr);

            //Store the encrypted license key here.
            String tLicenseKey = licenseDao.licenseKeyStr;
            //String tTimeStamp = licenseDao.gtimeStamp;

            licenseStatus = validateLicense(customerName, productId, optionalLabel, tLicenseKey);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return licenseStatus;
    }

    public String checkLicense(String customerName, String productId, String optionalLabel,
            String tLicenseKey) {

        String licenseStatus = "" + FAILURE;

        try {

            LicenseDao licenseDao = new LicenseDao(request);

            //   customerId = licenseDao.getCustId(customerName);
            if (customerName == null) {
                return licenseStatus;
            }
            if (productId == null) {
                return licenseStatus;
            }

            licenseStatus = validateLicense(customerName, productId, optionalLabel, tLicenseKey);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return licenseStatus;
    }

    /*
     * Inputs:
     * 	1. customerId
     * 	2. productId
     * 	3. pLicenseKey - encrypted key
     * Key content:
     * 	customer id, product id, license type, time zone, start date, end date, feature label-value pairs
     * 	Eg: "cusid=15,prodid=20,lictype=Eval,tz=IST,sdt=2015-01-01,edt=2015-01-31,workflow=enabled,admintool=disabled"
     */
    private String validateLicense(String customerName, String productId, String featureLabel,
            String pEncryptedLicenseKey) {

        boolean paramLabels = false;
        boolean keyLabels = false;
        int feature_start_index = 6;

        //Decrypt the encrypted license key.
        //String decryptedKey = decryptLicenseKey(customerId, productId, tStamp, tkey);
        try {
            Utils utils = new Utils();

            if (featureLabel != null) {
                paramLabels = true;
            }
            String decryptedKey = decryptLicenseKey(customerName, productId, pEncryptedLicenseKey);
            if (decryptedKey == null) {
                //     System.out.println("Decrypted key is null..returning failure");
                return "Customer is not licensed..." + FAILURE;//msg="Not licensed"
            } else {
                //  System.out.println("validateLicense(): Decryted key is " + decryptedKey);
                //      System.out.println("validateLicense(): Decryted key is " + decryptedKey);
            }

            String[] keyArray = decryptedKey.split(",");

            // To do: extract custid and prodid from license key and check
            // against
            // customerId and productId parameters.
            String customerNamelvPair = keyArray[0];
            String productidlvPair = keyArray[1];
            //    System.out.println("Customer id = " + customerNamelvPair + " product id = " + productidlvPair);
            if (customerNamelvPair.equals(customerName)) {
                //     System.out.println("Customer " + customerName + " is valid");
                //return ""+FAILURE;	
            } else {
                //     System.out.println("Customer " + customerName + " is NOT valid");
                return "Customer's License Key is not available..." + FAILURE;//msg="Customer " + customerName + " is NOT valid"
            }
            if (productidlvPair.equals(productId)) {
                //     System.out.println("Product is valid");
                //return ""+FAILURE;	
            } else {
                //     System.out.println("Product NOT valid");
                return "Customer's License Key for" + productId + " is not available..." + FAILURE;//msg="Product NOT valid
            }
            if (keyArray.length > feature_start_index) {
                //    System.out.println("validateLicense(): Feature labels are present in the key");
                keyLabels = true;
            }

            // feature labels not present in the license key
            if (paramLabels == true && keyLabels == false) {
                // System.out.println("validateLicense(): No feature label in the key..returning failure");
                //     System.out.println("validateLicense(): No feature label in the key..returning failure");
                return "Feature not supported..." + FAILURE;//msg="Feature not supported"
            }

            // do not know which feature to be checked for valid license
            if (paramLabels == false && keyLabels == true) {
                //System.out.println("validateLicense(): You must specify feature label..returning failure");
                //System.out.println("validateLicense(): You must specify feature label..returning failure");
                return "Specify feature label..." + FAILURE;//msg="Specify feature label"
            }

            String keytimeZonePair = keyArray[3];
            String keystartDatePair = keyArray[4];
            String keyendDatePair = keyArray[5];

            for (int i = 0; i < keyArray.length; i++) {
                //       System.out.println("Key value in array: " + keyArray[i]);
            }

            // Get current date and time zone
            Date currentDate = new Date();
            String dateStr = currentDate.toString();
            System.out.println("validateLicense(): Current timestamp=" + dateStr);
            String[] dateVaues = dateStr.split(" ");
            String localTimeZone = dateVaues[4];
            System.out.println("localTimeZone" + localTimeZone);
            System.out.println("keytimeZonePair=" + keytimeZonePair);

            if (localTimeZone.equalsIgnoreCase(keytimeZonePair)) {
                if (utils.compareDates(currentDate, keystartDatePair, keyendDatePair)) {
                    if ((paramLabels == true) && (keyLabels == true)) {
                        if (utils.checkFeature(featureLabel, keyArray)) {
                            //        System.out.println("validateLicense():..1: License is Active");
                            //      System.out.println("validateLicense():..1: License is Active");
                            return "" + SUCCESS;
                        } else {
                            //      System.out.println("validateLicense():..2: License is Expired");
                            //     System.out.println("validateLicense():..2: License is Expired");
                            return "Customer's License is expired..." + FAILURE;//msg="License is Expired"
                        }
                    } else if ((paramLabels == false) && (keyLabels == false)) {
                        System.out.println("validateLicense():..3: License is Active");
                        // System.out.println("License is Active");
                        return "" + SUCCESS;
                    }
                    System.out.println("validateLicense(): date is valid");
                    //   System.out.println("validateLicense(): date is valid");
                }
                System.out.println("validateLicense(): Time zones are same");
                //   System.out.println("validateLicense(): Time zones are same");
                // return 0;
            } else {
                System.out.println("validateLicense():..4: License is Expired");
                //   System.out.println("validateLicense():..4: License is Expired");
                return "Customer's License is expired..." + FAILURE;//msg="License is Expired";
            }
            System.out.println("validateLicense(): Returning failure");
            System.out.println("validateLicense():..5: License is Expired");
            //      System.out.println("validateLicense():..5: License is Expired");
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Customer's License is expired..." + FAILURE;//msg="License is Expired"
    }

    /*
     * Encrypts the given license key and returns the encrypted key as byte[].
     * @see com.license.service.LicenseService#encryptLicenseKey(java.lang.String, java.lang.String)
     */
    public String encryptLicenseKey(String customerName, String productId, String lKey) {
        String encryptedKey = null;

        try {
            EncryptDecrypt encryptorObj = new EncryptDecrypt(customerName, productId);
            System.out.println("encryptorObj=" + encryptorObj.toString());
            encryptedKey = encryptorObj.encrypt(lKey);
            System.out.println("encryptedKey=" + encryptedKey);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("LicenseServiceImpl::encryptLicenseKey():" + e.getMessage());
        }

        return encryptedKey;
    }

    /*
     * De-crypts the encrypted license key read from the database.
     * @see com.license.service.LicenseService#decryptLicenseKey(byte[])
     */
    public String decryptLicenseKey(String customerName, String productId,
            String lKey) {
        String decryptedKey = null;
        try {
            EncryptDecrypt encryptorObj = new EncryptDecrypt(customerName, productId);
            decryptedKey = encryptorObj.decrypt(lKey);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("LicenseServiceImpl::decryptLicenseKey():" + e.getMessage());
        }
        return decryptedKey;
    }
}
