/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dbmanager;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
//import GIFTask.Logwriter;

public class WrapperConnection implements Connection
{

    private static int szInt = 0;
    Logwriter GALogwriter = null;
    Connection connection = null;
    private int localszInt = 0;
    private static List openConnections = new ArrayList();
    private String className = null;

    /**
     * @param connection
     */
    public WrapperConnection(Connection connection, String className)
    {
        super();
        szInt++;
        localszInt = szInt;

        System.out.println("Opening Connection ***** " + localszInt);
        GALogwriter.writeDebug("Opening Connection ***** " + localszInt);
        this.connection = connection;
        GALogwriter.writeDebug("Openened Connection ***** " + localszInt + " " + className);
        openConnections.add(new Integer(localszInt));
        this.className = className;

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#clearWarnings()
     */
    public void clearWarnings() throws SQLException
    {
        connection.clearWarnings();

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#close()
     */
    public void close() throws SQLException
    {
        System.out.println("Closing Connection ***** " + localszInt);
        GALogwriter.writeDebug("Closing Connection ***** " + localszInt);
        if (connection != null)
        {
            connection.close();
        }
        GALogwriter.writeDebug("Closed Connection ***** " + localszInt);
        boolean val = openConnections.remove(new Integer(localszInt));
        GALogwriter.writeDebug("Removed Connection ***** " + val);

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#commit()
     */
    public void commit() throws SQLException
    {
        connection.commit();
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#createStatement()
     */
    public Statement createStatement() throws SQLException
    {
        return connection.createStatement();
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#createStatement(int, int)
     */
    public Statement createStatement(int resultSetType,
            int resultSetConcurrency) throws SQLException
    {
        return connection.createStatement(resultSetType, resultSetConcurrency);
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#createStatement(int, int, int)
     */
    public Statement createStatement(int resultSetType,
            int resultSetConcurrency, int resultSetHoldability)
            throws SQLException
    {

        return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#getAutoCommit()
     */
    public boolean getAutoCommit() throws SQLException
    {

        return connection.getAutoCommit();
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#getCatalog()
     */
    public String getCatalog() throws SQLException
    {

        return connection.getCatalog();
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#getHoldability()
     */
    public int getHoldability() throws SQLException
    {

        return connection.getHoldability();
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#getMetaData()
     */
    public DatabaseMetaData getMetaData() throws SQLException
    {
        return connection.getMetaData();
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#getTransactionIsolation()
     */
    public int getTransactionIsolation() throws SQLException
    {

        return connection.getTransactionIsolation();
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#getTypeMap()
     */
    public Map<String, Class<?>> getTypeMap() throws SQLException
    {

        return connection.getTypeMap();
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#getWarnings()
     */
    public SQLWarning getWarnings() throws SQLException
    {

        return connection.getWarnings();
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#isClosed()
     */
    public boolean isClosed() throws SQLException
    {

        return connection.isClosed();
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#isReadOnly()
     */
    public boolean isReadOnly() throws SQLException
    {

        return connection.isReadOnly();
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#nativeSQL(java.lang.String)
     */
    public String nativeSQL(String sql) throws SQLException
    {

        return connection.nativeSQL(sql);
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#prepareCall(java.lang.String)
     */
    public CallableStatement prepareCall(String sql) throws SQLException
    {

        return connection.prepareCall(sql);
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
     */
    public CallableStatement prepareCall(String sql, int resultSetType,
            int resultSetConcurrency) throws SQLException
    {

        return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
     */
    public CallableStatement prepareCall(String sql, int resultSetType,
            int resultSetConcurrency, int resultSetHoldability)
            throws SQLException
    {

        return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#prepareStatement(java.lang.String)
     */
    public PreparedStatement prepareStatement(String sql)
            throws SQLException
    {

        return connection.prepareStatement(sql);
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#prepareStatement(java.lang.String, int)
     */
    public PreparedStatement prepareStatement(String sql,
            int autoGeneratedKeys) throws SQLException
    {

        return connection.prepareStatement(sql, autoGeneratedKeys);
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
     */
    public PreparedStatement prepareStatement(String sql,
            int[] columnIndexes) throws SQLException
    {

        return connection.prepareStatement(sql, columnIndexes);
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
     */
    public PreparedStatement prepareStatement(String sql,
            String[] columnNames) throws SQLException
    {

        return connection.prepareStatement(sql, columnNames);
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
     */
    public PreparedStatement prepareStatement(String sql,
            int resultSetType, int resultSetConcurrency)
            throws SQLException
    {

        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
     */
    public PreparedStatement prepareStatement(String sql,
            int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) throws SQLException
    {

        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
     */
    public void releaseSavepoint(Savepoint savepoint) throws SQLException
    {
        connection.releaseSavepoint(savepoint);

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#rollback()
     */
    public void rollback() throws SQLException
    {
        connection.rollback();

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#rollback(java.sql.Savepoint)
     */
    public void rollback(Savepoint savepoint) throws SQLException
    {
        connection.rollback(savepoint);

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#setAutoCommit(boolean)
     */
    public void setAutoCommit(boolean autoCommit) throws SQLException
    {
        connection.setAutoCommit(autoCommit);

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#setCatalog(java.lang.String)
     */
    public void setCatalog(String catalog) throws SQLException
    {
        connection.setCatalog(catalog);

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#setHoldability(int)
     */
    public void setHoldability(int holdability) throws SQLException
    {
        connection.setHoldability(holdability);

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#setReadOnly(boolean)
     */
    public void setReadOnly(boolean readOnly) throws SQLException
    {
        connection.setReadOnly(readOnly);
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#setSavepoint()
     */
    public Savepoint setSavepoint() throws SQLException
    {

        return connection.setSavepoint();
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#setSavepoint(java.lang.String)
     */
    public Savepoint setSavepoint(String name) throws SQLException
    {

        return connection.setSavepoint(name);
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#setTransactionIsolation(int)
     */
    public void setTransactionIsolation(int level) throws SQLException
    {
        connection.setTransactionIsolation(level);

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#setTypeMap(java.util.Map)
     */
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException
    {
        connection.setTypeMap(map);

    }

    public static void printOpenConnections()
    {
        Logwriter.writeDebug("Number of Open connections " + openConnections.size());
        System.out.println("Number of Open connections " + openConnections.size());
        for (int i = 0, count = openConnections.size(); i < count; i++)
        {
            Logwriter.writeDebug("Connection Id " + openConnections.get(i));
            System.out.println("Connection Id " + openConnections.get(i));
            System.out.println("Number of Open connections " + openConnections.size());
        }
    }

    /**
     * Returns the underlying connection
     * @return
     */
    public Connection getRawConnection()
    {
        return this.connection;
    }

    @Override
    public Array createArrayOf(String arg0, Object[] arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Clob createClob() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Struct createStruct(String arg0, Object[] arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getClientInfo(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isValid(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setClientInfo(Properties arg0) throws SQLClientInfoException
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void setClientInfo(String arg0, String arg1)
            throws SQLClientInfoException
    {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isWrapperFor(Class<?> arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getClassName()
    {
        return className;
    }

    public void setSchema(String schema) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getSchema() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void abort(Executor executor) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getNetworkTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

