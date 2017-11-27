package dbmanager;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;


import org.apache.log4j.Logger;

public class DatabaseConnectionManager
{

    private static Logger log = Logger.getLogger(DatabaseConnectionManager.class);
    private static DataSource dataSource = null;
    private static Context initContext = null;

    /**
     * @param dataSourceURL example "datasource"
     * @param env_lookup example "env"
     * @throws NamingException
     * @throws ClassNotFoundException
     *
     * Call this method from startupservley
     */
    public static synchronized void init(String env_lookup) throws NamingException, ClassNotFoundException
    {
        try
        {
            //System.out.println("Inside init() of Database Connection.SaDesk");
            System.out.println("Datasource is:SaDesk " + dataSource);
            System.out.println("initContext is:SaDesk " + initContext);
            dataSource = null;

          
            //load driver //Class.forName("oracle.jdbc.OracleDriver");
            initContext = new InitialContext();
            System.out.println("initContext is:SaDesk " + initContext.getNameInNamespace());
            //Context envContext  = (Context)initContext.lookup(env_lookup);
            dataSource = (DataSource) initContext.lookup(env_lookup);
             // System.out.println("Datasource isSaDesk: " + ((BasicDataSource)dataSource).getUrl()+"=="+((BasicDataSource)dataSource).getDriverClassName());
            //dataSource.
            Class.forName("org.postgresql.Driver");
            System.out.println("DataSource is:SaDesk " + dataSource);
            //envContext.close();
            initContext.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static DataSource getDataSource()
    {
        return dataSource;
    }

    public static Connection getConnection() throws SQLException
    {
        //WrapperConnection.printOpenConnections());
        WrapperConnection.printOpenConnections();
        WrapperConnection wrapcon = new WrapperConnection(dataSource.getConnection(), "");
        System.out.println("Connection object is:SaDesk " + wrapcon);
        return wrapcon;
    }

    public static Connection getConnection(String classname) throws SQLException
    {
        //WrapperConnection.printOpenConnections());
        //System.out.println("DataSource::::::::::::::::SaDesk"+dataSource);
        if (dataSource instanceof BasicDataSource)
        {
            System.out.println("Max Active:SaDesk " + ((BasicDataSource) dataSource).getMaxActive());
            System.out.println("..Test"+((BasicDataSource) dataSource).getUrl());
            System.out.println("getMaxOpenPreparedStatements:SaDesk " + ((BasicDataSource) dataSource).getMaxOpenPreparedStatements());
            System.out.println("getMaxIdle:SaDesk " + ((BasicDataSource) dataSource).getMaxIdle());
            System.out.println("NumActive:SaDesk " + ((BasicDataSource) dataSource).getNumActive());
        }

        System.out.println("Creating connection for class name: SaDesk" + classname);
        WrapperConnection.printOpenConnections();
        WrapperConnection wrapcon = new WrapperConnection(dataSource.getConnection(), classname);
        System.out.println("Connection object is:SaDesk " + wrapcon);
        return wrapcon;
    }
}

