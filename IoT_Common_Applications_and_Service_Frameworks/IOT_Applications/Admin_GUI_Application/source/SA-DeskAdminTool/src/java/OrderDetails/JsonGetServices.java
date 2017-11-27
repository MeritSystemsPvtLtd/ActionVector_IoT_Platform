package OrderDetails;

/**
 * 
 * @author ananddw
 */
public class JsonGetServices extends AwareImplementer {

    //static Logger log = Logger.getLogger(JsonGetUserTasktypes.class);
    private String responseStr;
    private String Customer;
    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String Customer) {
        this.Customer = Customer;
    }
    public void JsonServices()  {
 
        
        try {
            
            String urlString = "?customer=" + Customer;
            String serviceListURL  = APIUrl.GetVendorAPIURL("getServicesURL")+ urlString;
            responseStr = HttpRequestPoster.sendGetRequest(serviceListURL, null);
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

//--------------------------------------------------------------------------------------------------------------------------

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*package OrderDetails;

import org.apache.log4j.Logger;
import Controller.APIUrl;
import Controller.AwareImplementer;
import Controller.HttpRequestPoster;
import Controller.UserDetails;
import javax.servlet.ServletResponse;*/

/**
 *
 * @author raghu
 */
/*public class JsonGetServices extends AwareImplementer {

    private String Customer;
    static Logger log = Logger.getLogger(JsonGetServices.class);

    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String Customer) {
        this.Customer = Customer;
    }
    public void JsonServices() {
        try {
            log.info("===========Getting the filepath location ==========");
            String filePath = servletContext.getRealPath(System.getProperty("file.separator"));
            log.info("=============== Sending the filePath to APIUrl class =============");
            String URLStr = APIUrl.GetVendorAPIURL("ServiceList", filePath);
            URLStr = "http://" + servletRequest.getServerName() + ":" + URLStr;
            
            URLStr += "?customer="+Customer;
            
            System.out.println("url String=====>"+URLStr);
            log.info("=============== Sending the URLStr to HttpRequestPoster class =============");
            String responseStr = HttpRequestPoster.sendPostRequest(URLStr, URLStr);
            String jsonString = responseStr.replaceAll("null\\(\'", "");
            jsonString = jsonString.replaceAll("\'\\)", "");
            servletResponse.getWriter().write(jsonString);

        } catch (Exception ex) {
            //  ex.printStackTrace();
            log.error("============= ERROR ==============", ex);

        }
        
    }


}*/

