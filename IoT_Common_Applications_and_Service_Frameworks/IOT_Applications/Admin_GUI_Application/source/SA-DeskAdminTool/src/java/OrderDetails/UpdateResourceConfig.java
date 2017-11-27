package OrderDetails;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * 
 * @author niteshc
 */
public class UpdateResourceConfig extends AwareImplementer {

    
    private String Service;
    private String Customer;
    private String Resource;
    private String paramsJson;

    public void resourceConfigUpdate() {

        HttpClient client = new HttpClient();

        BufferedReader br = null;
       

        try {
            
            /*String request = "[";
            request += "{\"resourceconfig\":{\"customer\":\"customerid\",\"service\":\"null\","
                    + "\"resourceid\":\"null\",\"params\":[{\"name\":\"ParamName\",\"value\":\"ParamValue\"},{\"name\":\"ParamName\",\"value\":\"ParamValue\"}}],";
            /*request += "{\"resourceconfig\":{\"customer\":\"customerid\",\"service\":null,"
                    + "\"resourceid\":null,\"paramname\":\"OperatingHours\",\"paramvalue\":\"7\"}}";
            
            request += "]";*/
            /*String request = "[";
            request += {\"resourceconfig\":{\"customer\":null,\"service\":null,\"resourceid\":null,\"params\":[{\"name\":null,
            \"value\":null},{\"name\":null,\"value\":null}]}};
            request += "]";*/
             String request = "{\"resourceconfig\":";
            request += "{\"customer\":\""+Customer+"\",\"service\":\""+Service+"\","
                    + "\"resourceid\":\""+Resource+"\",\"params\":"+paramsJson;
            request += "}}";
            
            System.out.println("request JSON==>>" + request);
            String updateURL = APIUrl.GetVendorAPIURL("updateResourceConfigURL");
            System.out.println("Url for updating" + updateURL);

            PostMethod pMethod = new PostMethod(updateURL);

            System.out.println("post method ==>>" + pMethod);

            pMethod.addParameter("resConfigReq", request);
            
       /*     try {
                    JSONObject jsonObject = (JSONObject) parser.parse(resConfigReq);
                    JSONArray resConf = (JSONArray) jsonObject.get("resourceconfig");

                    for (int i = 0; i < resConf.size(); i++) {
                     
                            JSONObject factObj = (JSONObject) resConf.get(i);
                            System.out.println(factObj.toJSONString());
                            JSONObject custObj = (JSONObject)factObj.get("customer");
                            customername = (String) custObj.get("name");
                            service = (String) custObj.get("service");
                            resourceid = (String) custObj.get("resourceid");
                            paramname = (String) custObj.get("paramname");
                            paramvalue = (String) custObj.get("paramvalue");*/
                      

            System.out.println("post method after appending response ==>>" + pMethod);

            client.executeMethod(pMethod);

            br = new BufferedReader(new InputStreamReader(pMethod.getResponseBodyAsStream()));

            String res;

            while ((res = br.readLine()) != null) {
                System.out.println(res);

            }

        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            if (br != null) {
                try {
                    br.close();
                } catch (Exception ee) {

                    ee.printStackTrace();
                }
            }
        }
    }

    /**
     * @return the responseStr
     */
    

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

    /**
     * @return the paramsJson
     */
    public String getParamsJson() {
        return paramsJson;
    }

    /**
     * @param paramsJson the paramsJson to set
     */
    public void setParamsJson(String paramsJson) {
        this.paramsJson = paramsJson;
    }
}

//----------------------------------------------------------------------------------------------------------------------------
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 
 package demoproj;

 import java.io.BufferedReader;
 import java.io.InputStreamReader;
 import org.apache.commons.httpclient.HttpClient;
 import org.apache.commons.httpclient.methods.PostMethod;

 /**
 *
 * @author niteshc
 
 public class UpdateResourceConfigClient {

 public static void main(String[] args) {

 HttpClient client = new HttpClient();

 BufferedReader br = null;

       
 try {

 String response = "{\"resourceconfig\":[";
 response += "{\"customer\":{\"name\":\"fronius\",\"service\":\"testservice\","
 + "\"resourceid\":\"testres\",\"paramname\":\"testparam\",\"paramvalue\":\"0.53\"}},";
 response += "{\"customer\":{\"name\":\"fronius\",\"service\":\"testres\","
 + "\"resourceid\":null,\"paramname\":\"testparam\",\"paramvalue\":\"testvalue\"}}";
 response += "]}";

 System.out.println("request JSON==>>" + response);



 PostMethod pMethod = new PostMethod("http://192.168.1.2:8084/CloudUserAPI/AV_UpdateResourceConfig");

 pMethod.addParameter("resConfigReq", response);

 client.executeMethod(pMethod);

 br = new BufferedReader(new InputStreamReader(pMethod.getResponseBodyAsStream()));

 String res;

 while ((res = br.readLine()) != null) {
 System.out.println(res);

 }

 } catch (Exception e) {

 e.printStackTrace();
 } finally {

 if (br != null) {
 try {
 br.close();
 } catch (Exception ee) {
                    
 ee.printStackTrace();
 }
 }
 }
 }
 }

 */
     
