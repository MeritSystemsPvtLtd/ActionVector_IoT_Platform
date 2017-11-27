package OrderDetails;

/**
 * 
 * @author ananddw
 */
public class ResourceConfigService extends AwareImplementer {
    
    private String responseStr;
    private String Service;
    private String Customer;
    private String Resource;
    
    
    public void JsonServiceUpdate(){
         try {
            String urlString = "?subservice=Default&customer=" + getCustomer() + "&service=" + getService();
            String resourceConfigList2 = APIUrl.GetVendorAPIURL("getResourceConfigURL")+ urlString;
            System.out.println("resourceConfigList2 in JsonUpdateDatabase==>>" + resourceConfigList2);
            setResponseStr(HttpRequestPoster.sendGetRequest(resourceConfigList2, null));
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

    /**
     * @return the Resource
     */
    public String getResource() {
        return Resource;
    }

    /**
     * @param Resource the Resource to set
     */
    public void setResource(String Resource) {
        this.Resource = Resource;
    }
    
}
