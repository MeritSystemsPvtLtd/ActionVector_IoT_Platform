/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adminAPI;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author ananddw
 *
 */
public class DeviceManagment {

    ResultSet rs = null;
    Statement st = null;
    String customer = null;

    /**
     * This method is used to retrieve all the Customer's present in
     * CustomerInfo Table
     *
     * @param connection
     * @return the list of Customer's in the form of String
     *
     */
    public String getCustomers(Connection connection) {

        StringBuffer stringBuffer = new StringBuffer();

        try {

            if (connection != null) {

                st = connection.createStatement();

                boolean recFound = false;

                String query = "select customername as customer from customerinfo";

                rs = st.executeQuery(query);

                while (rs.next()) {

                    recFound = true;

                    customer = rs.getString("customer");

                    stringBuffer.append("{\"Customer\":\"" + customer + "\"},");
                }

                rs.close();

                if (recFound) {

                    stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                }

                if (!recFound) {

                    stringBuffer.append("null");
                }

            } else {

                return null;
            }

            String custInfo = stringBuffer.toString();

            if (custInfo == null) {

                System.out.println("No data found");

                stringBuffer.append("{\"error\":\"error\"}");

            } else if (custInfo.equals("null")) {

                custInfo = "[]";

            } else {

                custInfo = "[" + custInfo + "]";
            }

            connection.close();
            
            System.out.println("---Return Customer Data Sucessfully ----"+custInfo);

            return custInfo;

        } catch (Exception e) {

            e.printStackTrace();

            System.err.println(" Error while establishing connection or retriveing Customer list Data : "+e);

            return null;

        } finally {

            try {

                if (connection != null) {

                    connection.close();
                }

                connection = null;

                if (rs != null) {

                    rs.close();

                }
                rs = null;

                if (st != null) {

                    st.close();
                }

                st = null;
                customer = null;
                stringBuffer = null;

            } catch (NullPointerException npe) {

                npe.printStackTrace();

            } catch (Exception e) {

                e.printStackTrace();

                System.err.println("Error While Closing Connection "+e);
            }

        }

    }

    /**
     * This Method will used to retrieve Services Based on Selected Customer
     *
     * @param connection
     * @param selectedcustomer
     * @return services
     */
    public String getServices(Connection connection, String selectedcustomer) {
        String service = null;

        StringBuffer sbr = new StringBuffer("");

        try {

            if (connection != null) {

                st = connection.createStatement();

                try {

                    boolean recFound = false;

                    String query = "select distinct service from hostinfo where customerid in (select id from"
                            + " customerinfo where customername='" + selectedcustomer + "')";

                    rs = st.executeQuery(query);

                    while (rs.next()) {

                        recFound = true;

                        service = rs.getString("service");

                        sbr.append("{\"Service\":\"" + service + "\"},");

                    }

                    rs.close();
                    rs = null;
                    st.close();
                    st = null;

                    if (recFound) {

                        sbr.deleteCharAt(sbr.length() - 1);

                    }

                    if (!recFound) {

                        sbr.append("null");
                    }

                } catch (Exception e) {

                    e.printStackTrace();

                    System.err.println("Error while fetching Services list in GetServices "+e);
                }

            } else {

                return null;
            }

            String servInfo = sbr.toString();

            if (servInfo == null) {

                System.out.println("No data found");

                sbr.append("{\"error\":\"error\"}");

            } else if (servInfo.equals("null")) {

                servInfo = "[]";

            } else {

                servInfo = "[" + servInfo + "]";
            }

            System.out.println("---Return Service Data Sucessfully ----"+servInfo);
            return servInfo;

        } catch (Exception e) {

            e.printStackTrace();

            System.err.println("Error while establishing connection "+e);

            return null;

        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (service != null) {
                    service = null;
                }
                if (sbr != null) {
                    sbr = null;
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error while closing connection "+ e);
            }
        }

    }

    /**
     * This Method will retrieve resources Based on selected Customer,services
     *
     * @param connection
     * @param selectedCustomer
     * @param selectedService
     * @return resources
     */
    public String getResources(Connection connection, String selectedCustomer, String selectedService) {
        ResultSet rs = null;

        Statement st = null;

        String resource = null;

        StringBuffer sbr = new StringBuffer("");

        StringBuffer result = new StringBuffer();

        try {

            if (connection != null) {

                st = connection.createStatement();

                try {

                    boolean recFound = false;

                    String query = "select resourceid from hostinfo where service='" + selectedService + "'and customerid in (select id from"
                            + " customerinfo where customername='" + selectedCustomer + "') and service!=resourceid and resourceid not like 'FroniusAdapter%'";

                    rs = st.executeQuery(query);

                    while (rs.next()) {

                        recFound = true;

                        resource = rs.getString("resourceid");

                        sbr.append("{\"ResourceId\":\"" + resource + "\"},");

                    }

                    rs.close();
                    rs = null;
                    st.close();
                    st = null;

                    if (recFound) {

                        sbr.deleteCharAt(sbr.length() - 1);

                    }

                    if (!recFound) {

                        sbr.append("null");
                    }

                } catch (Exception e) {

                    e.printStackTrace();

                    System.err.println("Error while fetching resourceId list in GetResources "+e);
                }

            } else {

                return null;
            }

            String resourceInfo = sbr.toString();

            if (resourceInfo == null) {

                System.out.println("No data found");

                result.append("{\"error\":\"error\"}");

            } else {

                resourceInfo = "[" + resourceInfo + "]";

            }

            System.out.println("----------resource Data loaded Sucessfully-------"+resourceInfo);
            return resourceInfo;

        } catch (Exception e) {

            e.printStackTrace();

            System.err.println("Error while establishing connection "+ e);

            return null;

        } finally {

            try {
                if (connection != null) {

                    connection.close();
                }
                connection = null;

                if (rs != null) {

                    rs.close();
                }

                rs = null;

                if (st != null) {

                    st.close();
                }
                st = null;

                resource = null;

                sbr = null;

            } catch (NullPointerException npe) {

                npe.printStackTrace();

            } catch (Exception e) {

                e.printStackTrace();

                System.err.println("error while closing connection "+ e);
            }
        }

    }

    /**
     * This Method is Used to Load Resource Configuration
     * @param connection
     * @param customer
     * @param service
     * @param subservice
     * @param resource
     * @param paramname
     * @return getResourceConfig
     */
    public String getResourceConfig(Connection connection, String customer, String service, String subservice, String resource, String paramname) {
        StringBuffer sbr = new StringBuffer("");
        String paramvalue = null;
        String paramunit = null;
        String serviceQuery = " AND service = 'null'";
        String subserviceQuery = " AND subservice = 'null'";
        String resourceQuery = " AND resourceid = 'null'";
        String paramQuery = " AND paramname = '" + paramname + "'";
        String query = null;
        try {

            if (connection != null) {
                
                st = connection.createStatement();
                
                try {
                    
                    if (service != null) {
                        
                        if (!(service.equals("")) && !(service.equals("All"))) {
                            
                            serviceQuery = " AND service ='" + service + "'";
                        }
                    }
                    if (subservice != null) {
                        
                        if (!(subservice.equals("")) && !(subservice.equals("All"))) {
                            
                            subserviceQuery = " AND subservice ='" + subservice + "'";
                        }
                    }
                    if (resource != null) {
                        
                        if (!(resource.equals("")) && !(resource.equals("All"))) {
                            
                            resourceQuery = " AND resourceid ='" + resource + "'";
                            
                        }
                    }
                    boolean recFound = false;
                    
                    query = "select paramname,paramvalue,paramunit from resourceconfig "
                            + "where customerid in (select id from customerinfo where customername='"
                            + customer + "')" + serviceQuery + subserviceQuery + resourceQuery;
                    
                    if (paramname != null) {
                        
                        query += paramQuery;
                    }
                    
                    query += " order by serialno";
                    
                    System.out.println("GetJSONResourceConfig Query===>" + query);
                    
                    rs = st.executeQuery(query);
                    
                    while (rs.next()) {
                        
                        recFound = true;
                        
                        paramname = rs.getString("paramname");
                        
                        paramvalue = rs.getString("paramvalue");
                        
                        paramunit = rs.getString("paramunit");
                      
                        sbr.append("{\"ParamName\":\"" + paramname + "\",\"ParamValue\":\"" + paramvalue + "\",\"ParamUnit\":\""
                                + paramunit + "\"},");
                    }
                    if (recFound) {
                        
                        sbr.deleteCharAt(sbr.length() - 1);
                        
                    }
                   
                } catch (Exception e) {
                    
                    e.printStackTrace();
                    
                    System.err.println("Error while fetching paramvalues in GetJSONResourceConfiguration "+ e);
                }
                
            } else {
                return null;
            }
            
            System.out.println("----------resource config Data loaded Sucessfully-------"+sbr);
            return sbr.toString();
            
        } catch (Exception e) {
            
            e.printStackTrace();
            
            System.err.println("Error while establishing connection"+ e);
            
            return null;
            
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                connection = null;
                if (rs != null) {
                    rs.close();
                }
                rs = null;
                if (st != null) {
                    st.close();
                }
                st = null;
                paramname = null;
                paramvalue = null;
                paramunit = null;
                sbr = null;
                serviceQuery = null;
                subserviceQuery = null;
                resourceQuery = null;
                paramQuery = null;
                query = null;
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error while closing connection "+e);
            }
        }

    }

/**
 * This Method is Used to Updated Resource
 * @param connection
 * @param updateJson
 * @return updatedResourceConfiguration Data
 */    
    public boolean  updateResourceConfig(Connection connection,String updateJson) {
        String customername = null;
        String service = null;
        String resourceid = null;
        String name = null;
        String value = null;
        int res = 0;
        String szSerQuery;
        String szResQuery;
        try {
            if (connection != null) {
                
                st = connection.createStatement();
                
                try {
                    JSONParser parser = new JSONParser();
                    
                    System.out.println("parser=" + parser.toString());
                    
                    System.out.println("updateJson=" + updateJson);
                    
                    JSONObject jsonObject = (JSONObject) parser.parse(updateJson);
                    
                    JSONObject jsonObj = (JSONObject) jsonObject.get("resourceconfig");
                    
                    System.out.println("jsonObj=" + jsonObj.toString());
                    
                    customername = (String) jsonObj.get("customer");
                    
                    System.out.println("customer=" + customername);
                    
                    service = (String) jsonObj.get("service");
                    
                    if (service != null) {
                        
                        if (service.equalsIgnoreCase("null") || service.equals("")) {
                            
                            szSerQuery = "service='null'";
                            
                        } else {
                            
                            szSerQuery = "service = '" + service + "'";
                        }
                        
                    } else {
                        
                        szSerQuery = "service='null'";
                        
                    }
                    
                    System.out.println("service=" + service);
                    
                    resourceid = (String) jsonObj.get("resourceid");
                    
                    if (resourceid != null) {
                        
                        if (resourceid.equalsIgnoreCase("null") || resourceid.equals("")) {
                            
                            szResQuery = "resourceid='null'";
                            
                        } else {
                            
                            szResQuery = "resourceid = '" + resourceid + "'";
                        }
                        
                    } else {
                        
                        szResQuery = "resourceid='null'";
                        
                    }
                    
                    System.out.println("resourceid=" + resourceid);
                    
                    JSONArray params = (JSONArray) jsonObj.get("params");
                    
                    Iterator itr = params.iterator();
                    
                    while (itr.hasNext()) {
                        
                        JSONObject jsObj = (JSONObject) itr.next();
                        
                        name = (String) jsObj.get("ParamName");
                        
                        value = (String) jsObj.get("ParamValue");
                        
                        String szUpdateQuery = "update resourceconfig set paramvalue='" + value + "' where "
                                + "customerid=(select id from customerinfo where customername='" + customername
                                + "') and " + szSerQuery + " and " + szResQuery + " and paramname='" + name + "'";
                        
                        System.out.println("UpdateResourceConfig Query ==>" + szUpdateQuery);
                        
                        res = st.executeUpdate(szUpdateQuery);
                        
                    }
                    if (res != 0) {
                        
                        return true;
                        
                    } else {
                        
                        System.err.println("Error while doing updation operation");
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error while parsing the json");
                   
                }
            } else {
                System.out.println("Connection is null");
               
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error while connecting with the database"+e);
            
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (st != null) {
                    st.close();
                }
                if (customername != null) {
                    customername = null;
                }
                if (service != null) {
                    service = null;
                }
                if (resourceid != null) {
                    resourceid = null;
                }
                if (name != null) {
                    name = null;
                }
                if (value != null) {
                    value = null;
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("Error while closing connection "+ ex);
            }
        }
         return false;
    }
}//End Of Class 
