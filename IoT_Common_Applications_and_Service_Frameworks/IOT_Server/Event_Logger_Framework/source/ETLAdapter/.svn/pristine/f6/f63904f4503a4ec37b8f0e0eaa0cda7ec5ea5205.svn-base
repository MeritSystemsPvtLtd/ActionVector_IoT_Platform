/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package autoIDGenerator;

import com.prokosha.adapter.etl.ETLAdapter.ConnectionDAO;
import java.sql.*;

/**
 *
 * @author:anand kumar verma
 */
public class DBCon {

    AutoTimeStId obj = null;
    String query = "";
    Connection db = null;        // A connection to the database
    Statement sql = null;       // Our statement to run queries with
    // about the DB it just connected to. I use
    // it to get the DB version to confirm the
    // connection in this example.

    public DBCon(String customerID,AutoTimeStId obj) throws ClassNotFoundException, SQLException, Exception {
        db = ConnectionDAO.getConnection(customerID);
        sql = db.createStatement();
        this.obj = obj;
    }

    public String getIDFromDatabase() {
        String eventID = "";
        int intValue = 0;
        try {
            query = "INSERT INTO generatedId (timeOfCreation) values('" + obj.timeOfCreation + "')";
            sql.executeUpdate(query);
            //selectQuery
            query = "SELECT MAX(autoID)  FROM generatedId";
            ResultSet rs = sql.executeQuery(query);
            if (rs.next()) {
                intValue = rs.getInt(1);
                eventID = "" + intValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            query = null;
            sql = null;
            db = null;
        }
        return eventID;
    }
}
