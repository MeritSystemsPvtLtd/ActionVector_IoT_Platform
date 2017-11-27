package license;

public interface LicenseService {

    public static final int SUCCESS = 1;
    public static final int FAILURE = 0;
    public static final int CUSTOMER_DOES_NOT_EXIST = 2;

    public String createLicense(String customerName, String productId,
            String licenseType, String timeStamp, String timeZone, String startDate,
            String endDate, String optionalAttribs);

    public String checkLicense(String customerName, String productId, String optionalFeature);

    public String checkLicense(String customerName, String productId, String optionalLabel,
            String encryptedLicenseKey);
}
