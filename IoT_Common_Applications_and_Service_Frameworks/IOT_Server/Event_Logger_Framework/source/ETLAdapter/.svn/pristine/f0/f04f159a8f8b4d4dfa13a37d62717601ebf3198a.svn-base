/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qtart;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author anandkv
 */
public class ABC {
    public static void main(String[] args) {
        try {
            Socket sc = new Socket("192.168.1.2", 8099);
            OutputStream out = sc.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(out);
            
            if(sc.isConnected()){
                System.out.println("conneting to 192.168.1.66:8099 successful.");
            }
            int i = 0; 
            while(true){
                System.out.println("i = " + i++);
                writer.write("stream=MySQLDBMonitorEvent,resourceType=DataBase,FLUSHTABLES=1,HostName=192.168.1.66,OPENS=30,OPENTABLES=32,QUERIESPERSECONDAVG=28.93,QUESTIONS=540125,SLOWQUERIES=0,THREADS=7,UPTIME=18676,connections=1,resourceSubType=MySQL,resourceId=saVector,availability=1,CustomerID=merit,timestamp1=1371707890,eventID=177259");
                Thread.sleep(10000);
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    public static void main1(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            String strURL = "jdbc:postgresql://192.168.1.66:5432/abc";
            Connection con = DriverManager.getConnection(strURL, "postgres", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select count(*) from servicemetrics");
            while(rs.next()){
                System.out.println("........ " + rs.getInt(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}