package OrderDetails;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * @author ananddw
 */
public class JsonGetCustomers extends AwareImplementer {

    //static Logger log = Logger.getLogger(JsonGetUserTasktypes.class);
    String responseStr = null;
    // static Properties props = null;
    //String APIURL = "SA-DeskAdminAPIURL.properties";
    //private String APIUrl;
    

    public void JsonCustomers() {

        try {
            String customerlistURL = APIUrl.GetVendorAPIURL("getCustomersURL");
            responseStr = HttpRequestPoster.sendGetRequest(customerlistURL, null);
            String jsonString = responseStr.replaceAll("null\\(\'", "");
            jsonString = jsonString.replaceAll("\'\\)", "");
            this.responseStr = jsonString;
            System.out.println("Removing null" + this.responseStr);
            servletResponse.getWriter().write(this.responseStr);

        } catch (Exception ex) {
            ex.printStackTrace();
            //log.error("=============== ERROR ==============", ex);
        }

    }
}
