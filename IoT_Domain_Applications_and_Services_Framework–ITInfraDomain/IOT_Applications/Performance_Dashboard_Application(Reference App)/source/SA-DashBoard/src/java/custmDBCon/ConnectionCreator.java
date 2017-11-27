/**
 *
 * @author anand kumar verma
 * ********************************************************************
 * Copyright message
 * Software Developed by
 * Merit Systems Pvt. Ltd.,
 * No. 42/1, 55/c, Nandi Mansion, 40th Cross, Jayanagar 8th Block
 * Bangalore - 560 070, India
 * Work Created for Merit Systems Private Limited
 * All rights reserved
 *
 * THIS WORK IS SUBJECT TO INDIAN AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES
 * NO PART OF THIS WORK MAY BE USED, PRACTICED, PERFORMED, COPIED,
 * DISTRIBUTED, REVISED, MODIFIED, TRANSLATED, ABRIDGED, CONDENSED,
 * EXPANDED, COLLECTED, COMPILED, LINKED, RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT
 * ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 **********************************************************************
 */
package custmDBCon;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionCreator {

    private String gSZDatabaseDriver = "";
    private String gSZDatabaseURL = "";
    private String gSZDatabaseUser = "";
    private String gSZDatabasePass = "";
    private Connection gCon = null;
    private boolean gflag = false;

    public ConnectionCreator(String databaseDriver, String databaseURL,
            String databaseUser, String databasePass) throws Exception{
        gSZDatabaseUser = databaseUser;
        gSZDatabasePass = databasePass;
        gSZDatabaseURL = databaseURL;
        gSZDatabaseDriver = databaseDriver;
        createConnection();
    }

    public boolean createConnection() throws Exception{
    	Exception eex = null;
        try {
            Class.forName(gSZDatabaseDriver); //load the driver
           // System.out.println("gSZDatabaseURL = " + gSZDatabaseURL);
           // System.out.println("gSZDatabaseUser = " + gSZDatabaseUser);
           // System.out.println("gSZDatabasePass = " + gSZDatabasePass);
            try {
            	gCon = null;
                gCon = DriverManager.getConnection(gSZDatabaseURL, gSZDatabaseUser, gSZDatabasePass);
			} catch (Exception e) {
				// TODO: handle exception
				eex = e;
			}
            if(gCon == null ){
            	String msg = "Warning: failed to create connection to URL: " + gSZDatabaseURL;
            	eex = new Exception(msg.concat("\n" + eex.getMessage()));
            }
            if (gCon.getMetaData() != null) {
                gflag = true;
            } else {
            	String msg = "Warning: connection error to URL: " + gSZDatabaseURL;
            	eex = new Exception(msg.concat("\n" + eex.getMessage()));
            }
        } catch (Exception ex) {
        	if(eex == null)
        		eex = new Exception("");
        	eex = new Exception( eex.getMessage() + "\n" + ex.getMessage());
        	throw eex;
        }
        return gflag;
    }

    public boolean closeConnection() {
        boolean flag = false;
        try {
            gCon.close();
            flag = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            gCon = null;
        }
        return flag;
    }

    public Connection getConnection() {
        if (gflag) {
            return gCon;
        }
        return null;
    }
}
