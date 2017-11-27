package OrderDetails;
/**
 * 
 * @author ananddw
 */
public class ResourceConfigCustomer extends AwareImplementer {
    
    private String responseStr;
    private String Service;
    private String Customer;
    private String Resource;
    
    public void JsonCustomerUpdate(){
         try {
            String urlString = "?subservice=Default&customer=" + getCustomer();
            String resourceConfigList1 = APIUrl.GetVendorAPIURL("getResourceConfigURL")+ urlString;
            System.out.println("resourceConfigList1 in JsonUpdateDatabase==>>" + resourceConfigList1);
            setResponseStr(HttpRequestPoster.sendGetRequest(resourceConfigList1, null));
            String jsonString = getResponseStr().replaceAll("null\\(\'", "");
            jsonString = jsonString.replaceAll("\'\\)", "");
            System.out.println("Removing null" + this.getResponseStr());
            this.setResponseStr(jsonString);
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
