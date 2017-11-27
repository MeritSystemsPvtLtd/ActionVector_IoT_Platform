/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package businessmodel;

import Model.DatabaseConnection;
import static businessmodel.GetAllMetricUOM.log;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.log4j.Logger;

/**
 *
 * @author niteshc
 */
public class GetAllMetricUOM {

    static Logger log = Logger.getLogger(GetAllMetricUOM.class);

    public static String getAllMetricUOM(javax.servlet.http.HttpServletRequest request) {

        Connection con = null;
        ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null, rs4 = null, rs5 = null;
        Statement st = null, st1 = null, st2 = null, st3 = null, st4 = null, st5 = null;
        String metricUOM = null;
        String customerName = null;
        String service = null;
        String subService = null;
        String resourcetype = null;
        String metrictype = null;
        String custID = null;
        boolean recFound = false;
        StringBuffer sbr = new StringBuffer("{\"UOMMap\":[");
        int len = sbr.length();
        try {
            con = DatabaseConnection.getAVSAConnection(request);
            if (con != null) {
                log.info("Connection is not null before getting resource configuration");
                try {
                    String query = "select distinct customerid from metricsinfo";
                    //   System.out.println("Query===>" + query);
                    st = con.createStatement();
                    st1 = con.createStatement();
                    st2 = con.createStatement();
                    st3 = con.createStatement();
                    st4 = con.createStatement();
                    st5 = con.createStatement();
                    rs1 = st1.executeQuery(query);
                    //  sbr.append("{\"CustomerName\":[");
                    while (rs1.next()) {
                        recFound = true;
                        custID = rs1.getObject("customerid").toString();
                        //       System.out.println("custID==>" + custID);
                        query = "select customername from customerinfo where id='" + custID + "'";
                        rs = st.executeQuery(query);
                        if (rs.next()) {
                            customerName = rs.getObject("customername").toString();
                        }
                        rs = null;
                        sbr.append("{\"customer\":\"" + customerName + "\",\"UOMMap\":[");
                        //    System.out.println("sbr==>" + sbr);
                        query = "select distinct service from metricsinfo where customerid ='" + custID + "' ";
                        //     System.out.println("query===>" + query);
                        rs2 = st2.executeQuery(query);
                        while (rs2.next()) {
                            service = rs2.getObject("service").toString();
                            //       System.out.println("service==>" + service);
                            sbr.append("{\"service\":\"" + service + "\",\"UOMMap\":[");
                            //     System.out.println("sbr==>" + sbr);
                            query = "select distinct subservice from metricsinfo where customerid ='" + custID + "' and service = '" + service + "' ";
                            //     System.out.println("query==>" + query);
                            rs3 = st3.executeQuery(query);
                            while (rs3.next()) {
                                subService = rs3.getObject("subservice").toString();
                                //       System.out.println("subService===>" + subService);
                                sbr.append("{\"subservice\":\"" + subService + "\",\"UOMMap\":[");
                                //       System.out.println("sbr==>" + sbr);
                                query = "select distinct resourcetype from metricsinfo where customerid ='" + custID + "' and service = '" + service + "' and subservice = '" + subService + "' ";
                                //      System.out.println("query==>" + query);
                                rs4 = st4.executeQuery(query);
                                while (rs4.next()) {
                                    resourcetype = rs4.getObject("resourcetype").toString();
                                    //     System.out.println("resourcetype===>" + resourcetype);
                                    sbr.append("{\"resourcetype\":\"" + resourcetype + "\",\"UOMMap\":[");
                                    //      System.out.println("sbr==>" + sbr);
                                    query = "select distinct metrictype,metricuom from metricsinfo where customerid ='" + custID + "' and service = '" + service + "' and subservice = '" + subService + "' and resourcetype = '" + resourcetype + "' ";
                                    //      System.out.println("query==>" + query);
                                    rs5 = st5.executeQuery(query);
                                    while (rs5.next()) {
                                        metrictype = rs5.getObject("metrictype").toString();
                                        //        System.out.println("metricty===>" + metrictype);
                                        if ((metrictype.equalsIgnoreCase("downtime")) || (metrictype.equalsIgnoreCase("DeviceStatus"))) {
                                            metricUOM = "null";
                                        } else {
                                            metricUOM = rs5.getObject("metricuom").toString();
                                        }
                                        //        System.out.println("metricUOM===>" + metricUOM);
                                        sbr.append("{\"metrictype\":\"" + metrictype + "\",\"metricUOM\":\"" + metricUOM + "\"},");
                                        //         System.out.println("sbr==>" + sbr);
                                    }
                                    // st5 = null;
                                    rs5 = null;
                                    sbr.deleteCharAt(sbr.length() - 1);
                                    sbr.append("]},");
                                    //      System.out.println("rs5.next()");
                                }
                                rs4 = null;
                                //    st = null;
                                sbr.deleteCharAt(sbr.length() - 1);
                                sbr.append("]},");
                                // sbr.append("}");
                                //    System.out.println("rs4.next()");
                            }
                            //   st3 = null;
                            rs3 = null;
                            sbr.deleteCharAt(sbr.length() - 1);
                            sbr.append("]},");
                            //   System.out.println("rs3.next()");
                        }
                        rs2 = null;
                        //   st2 = null;
                        sbr.deleteCharAt(sbr.length() - 1);
                        sbr.append("]},");
                        //   System.out.println("rs2.next()");
                    }
                    rs1 = null;
                    //    st1 = null;
                    sbr.deleteCharAt(sbr.length() - 1);
                    sbr.append("]}");
                    if (!recFound) {
                        sbr = null;
                        sbr = new StringBuffer("{}");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error while fetching metricUOM list in GetMetricUOM ", e);
                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (st != null) {
                            st.close();
                        }
                        if (rs1 != null) {
                            rs1.close();
                        }
                        if (st1 != null) {
                            st1.close();
                        }
                        if (rs2 != null) {
                            rs2.close();
                        }
                        if (st2 != null) {
                            st2.close();
                        }
                        if (rs3 != null) {
                            rs3.close();
                        }
                        if (st3 != null) {
                            st3.close();
                        }
                        if (rs4 != null) {
                            rs4.close();
                        }
                        if (st4 != null) {
                            st4.close();
                        }
                        if (rs5 != null) {
                            rs5.close();
                        }
                        if (st5 != null) {
                            st5.close();
                        }
                        rs = null;
                        st = null;

                        rs1 = null;
                        st1 = null;
                        rs2 = null;
                        st2 = null;
                        rs3 = null;
                        st3 = null;
                        rs4 = null;
                        st4 = null;
                        rs5 = null;
                        st5 = null;
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("Error while closing rs and st connection ", e);
                    }
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                log.error("Error while closing con connection ", ex);
            }
        }
        return sbr.toString();
    }
}
