/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.merit.dashboard.file.SendFileToJson;
import com.merit.dashboard.queryexecuter.QueryExecuter;
import java.io.File;
import java.sql.SQLException;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author anandkv
 */
public class QueryExecutorThread extends TimerTask {

    private QueryExecuter queryExecutor = null;
    private String customer = null;
    private String cCustomer = null;
    private String service = null;

    public QueryExecutorThread(String customer, String cCustomer, String service) {
        this.customer = customer;
        this.cCustomer = cCustomer;
        this.service = service;
        queryExecutor = new QueryExecuter();
    }

    @Override
    public void run() {
        String watchDogAlertJson;
        SendFileToJson sfj ;//modified
        while (true) {
            try {
                watchDogAlertJson = queryExecutor.getWatchDogAlertJson(customer, cCustomer);
                sfj = new SendFileToJson(customer, "", "Hour", "", cCustomer, service, ".." + File.separator + "WatchDogAlert", watchDogAlertJson);//modified
                try {
                    Thread.sleep(1000 * 60);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sfj = null;
            } catch (Exception ex) {
                Logger.getLogger(QueryExecutorThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    //QueryExecuter  getWatchDogAlertJson(String customer)
}
