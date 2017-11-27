/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package autoIDGenerator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author anand kumar verma
 */
public class AutoTimeStId {
    public String timeOfCreation = null;
    public String autoID = null;
    Date date = null;
    public AutoTimeStId(String customerID){
        //timeOfCreation = date.toString();
        for(int i =0;i<10;i++){
            date = new Date();
            DateFormat dtfmt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            timeOfCreation = dtfmt.format(date).toString();
        }

        try {
            DBCon dacn = new DBCon(customerID,this);
            autoID = dacn.getIDFromDatabase();

            dacn = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            timeOfCreation = null;
            date = null;
        }
    }
    public String getID(){
        return autoID;
    }
}
