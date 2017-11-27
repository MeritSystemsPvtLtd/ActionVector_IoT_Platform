/**
 * *********************************************************************
 * Software Developed by Merit Systems Pvt. Ltd., No. 42/1, 55/c, Nandi Mansion,
 * 40th Cross, Jayanagar 8th Block Bangalore - 560 070, India Work Created for
 * Merit Systems Private Limited All rights reserved
 *
 * THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES
 * NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED, DISTRIBUTED,
 * REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED,
 * COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD
 * SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 * *********************************************************************
 */


package com.merit.connection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import custmDBCon.ConnectionMap;


public class ConnectionDAO {

    private static ConnectionMap conmap = new ConnectionMap();
    private static boolean initialized = false;

  /** This method is used to get Connection by passing CustomerID in custmDBCon package (API) */
    public static Connection getConnection(String CustomerID) throws IOException, SQLException {

        try {
            if (!initialized) {
                return conmap.getConntionWithID(CustomerID);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
