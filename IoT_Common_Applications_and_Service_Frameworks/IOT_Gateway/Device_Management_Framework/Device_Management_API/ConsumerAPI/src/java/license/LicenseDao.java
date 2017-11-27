package license;

import java.sql.Connection;
import java.sql.PreparedStatement;
import org.apache.log4j.Logger;
import org.postgresql.util.PSQLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;

public class LicenseDao {

    public String licenseKeyStr;
    public String gtimeStamp;

    HttpServletRequest request;
    private static Logger logger = null;

    public LicenseDao(javax.servlet.http.HttpServletRequest request) {
        try {
            this.request = request;
            logger = Logger.getLogger("LicenseDao");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public int updateCustomerLicense(String cusname, String prodid, String ts, String lkey) {
        PreparedStatement pst = null;
        Connection con = null;
        String sql = null;
        int retVal = 0;
        try {
            if (lkey != null) {
                System.out.println("lkey not null");
                sql = "insert into licenseinfo values('" + cusname + "','" + prodid + "','" + ts + "'," + "'" + lkey + "');";
            }
            if (lkey == null) {
                System.out.println("lkey is null");
                sql = "insert into licenseinfo values('" + cusname + "','" + prodid + "','" + ts + "');";
            }
            System.out.println("SQL Stmt: " + sql);
            // System.out.println("Insert Query===>" + sql);
            try {
                con = Model.DatabaseConnection.getAVSAConnection(request);
                if (con != null) {
                    System.out.println("Connection is not null before getting resource configuration");
                    //   System.out.println("Connection is not null before getting resource configuration");
                    pst = con.prepareStatement(sql);
                    retVal = pst.executeUpdate();
                    System.out.println("retVal from executeUpdate() = " + retVal);
                    //System.out.println("retVal from executeUpdate() = " + retVal);
                }
                con.close();
                pst.close();
            } catch (PSQLException e1) {
                System.out.println("Returning 0.." + e1.getMessage());
                e1.printStackTrace();
                return 0;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (pst != null) {
                        pst.close();
                    }
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException e1) {
            e1.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public int getCustomerLicenseKey(String cusname, String prdid) {
        PreparedStatement pst = null;
        ResultSet queryResult = null;
        Connection con = null;
        boolean found = false;

        String sql = "SELECT * FROM  licenseinfo  WHERE customername = '" + cusname + "' and prodid = '" + prdid + "';";
        //   System.out.println("Select Query===>" + sql);

        try {
            con = Model.DatabaseConnection.getAVSAConnection(request);
            if (con != null) {
                System.out.println("Connection is not null before getting resource configuration");
                // System.out.println("Connection is not null before getting resource configuration");
                pst = con.prepareStatement(sql);
                queryResult = pst.executeQuery();
                //  System.out.println("Query Result: " + queryResult);
                boolean getlicRecFound = false;
                while (queryResult.next()) {
                    getlicRecFound = true;
                    //     System.out.println("Getting the record..");
                    gtimeStamp = queryResult.getObject("ts").toString();
                    licenseKeyStr = queryResult.getObject("lickey").toString();
                    //    System.out.println("License key= " + licenseKeyStr);
                }
                con.close();
                pst.close();
                if (!getlicRecFound) {
                    System.out.println("Customer record NOT found..");
                    //  System.out.println("Customer record NOT found..");
                    return 0;
                }
            }
        } catch (PSQLException e1) {
            System.out.println("Returning 0.." + e1.getMessage());
            e1.printStackTrace();
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQLException..returning 0" + e.getMessage());
            return 0;
        } catch (NullPointerException e1) {
            e1.printStackTrace();
            System.out.println("Null pointer exception..returning 0" + e1.getMessage());
            return 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception..returning 0" + ex.getMessage());
            return 0;
        } finally {
            try {
                if (queryResult != null) {
                    System.out.println("Closing queryResult");
                    queryResult.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return 1;
    }

    /*  public String getCustId(String customerName) {
     Connection con = null;
     PreparedStatement pst = null;
     ResultSet queryResult = null;
     boolean found = false;
     String customerId = null;
     String sql = "Select id from licenseinfo where customername='" + customerName + "'";
     //     System.out.println("Select Query==>" + sql);

     try {
     con = Model.DatabaseConnection.getAVSAConnection(request);
     if (con != null) {
     logger.info("Connection is not null before getting resource configuration");
     pst = con.prepareStatement(sql);
     queryResult = pst.executeQuery();
     System.out.println("Query Result: " + queryResult);
     boolean custIdfound = false;
     while (queryResult.next()) {
     custIdfound = true;
     System.out.println("Getting the record..");
     customerId = queryResult.getObject("id").toString();
     System.out.println("CustomerId = " + customerId);
     //       System.out.println("CustomerId = " + customerId);
     }
     if (!custIdfound) {
     System.out.println("Customer record NOT found..");
     //       System.out.println("Customer record NOT found..");
     return null;
     }
     }
     } catch (PSQLException e1) {
     System.out.println("Returning null.." + e1.getMessage());
     e1.printStackTrace();
     return null;
     } catch (SQLException e) {
     System.out.println("SQLException..returning null" + e.getMessage());
     e.printStackTrace();
     return null;
     } catch (NullPointerException e1) {
     System.out.println("Null pointer exception..returning " + e1.getMessage());
     e1.printStackTrace();
     return null;
     } catch (Exception ex) {
     System.out.println("Exception..returning null" + ex.getMessage());
     ex.printStackTrace();
     return null;
     } finally {
     try {
     if (queryResult != null) {
     System.out.println("Closing queryResult");
     queryResult.close();
     queryResult = null;
     }
     if (pst != null) {
     pst.close();
     pst = null;
     }
     if (con != null) {
     con.close();
     }
     } catch (SQLException e) {
     e.printStackTrace();
     }
     }
     return customerId;
     }*/
}
