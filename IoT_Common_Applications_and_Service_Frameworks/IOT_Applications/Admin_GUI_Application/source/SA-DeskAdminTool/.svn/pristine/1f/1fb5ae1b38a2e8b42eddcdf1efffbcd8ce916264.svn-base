package OrderDetails;

import org.apache.log4j.Logger;
import java.io.FileInputStream;
import java.util.Properties;

public class APIUrl {

    static Properties props = null;
    static boolean initialized = false;
    static Logger log = Logger.getLogger(APIUrl.class);

    private static void initialize() {
        String workingDir = System.getProperty("user.dir");
        System.out.println("Current working directory " + workingDir);
        try {

            String proppath = workingDir + System.getProperty("file.separator") + "SA-DeskAdminAPIURL.properties";
            props = new Properties();
            props.load(new FileInputStream(proppath));
            String APIurl = props.getProperty("customerListURL");
            System.out.println("static block executed " + APIurl);
            initialized=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String GetVendorAPIURL(String URLProperty) {
        String APIurl = null;

        try {
            if (!initialized) {
                initialize();
            }
            log.info("************** Getting the properties file ***************");

            APIurl = props.getProperty(URLProperty);

            log.debug("************** Calling the API *************");
        } catch (Exception ex) {
            //  ex.printStackTrace();
            log.error("============== ERROR ===============", ex);
        }
        return APIurl;
    }
}
