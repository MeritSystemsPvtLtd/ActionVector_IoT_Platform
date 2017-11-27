package OrderDetails;

/**
 * 
 * @author ananddw
 */
public class JsonGetResources extends AwareImplementer {

    //static Logger log = Logger.getLogger(JsonGetUserTasktypes.class);
    private String responseStr;
    private String Service;
    private String Customer;
    
    public void JsonResources()  {
 
        
        try {
            
            String urlString = "?customer=" + getCustomer() + "&service=" + Service;
            String resourceListURL = APIUrl.GetVendorAPIURL("getResourcesURL")+ urlString;
            setResponseStr(HttpRequestPoster.sendGetRequest(resourceListURL, null));
            String jsonString = getResponseStr().replaceAll("null\\(\'", "");
            jsonString = jsonString.replaceAll("\'\\)", "");
            this.setResponseStr(jsonString);
            System.out.println("Removing null" + this.getResponseStr());
            servletResponse.getWriter().write(this.getResponseStr());

        } catch (Exception ex) {
            ex.printStackTrace();
            //log.error("=============== ERROR ==============", ex);
        }

    }

    /**
     * @return the Service
     */
    public String getService() {
        return Service;
    }

    /**
     * @param Service the Service to set
     */
    public void setService(String Service) {
        this.Service = Service;
    }

    /**
     * @return the responseStr
     */
    public String getResponseStr() {
        return responseStr;
    }

    /**
     * @param responseStr the responseStr to set
     */
    public void setResponseStr(String responseStr) {
        this.responseStr = responseStr;
    }

    /**
     * @return the Customer
     */
    public String getCustomer() {
        return Customer;
    }

    /**
     * @param Customer the Customer to set
     */
    public void setCustomer(String Customer) {
        this.Customer = Customer;
    }
}
