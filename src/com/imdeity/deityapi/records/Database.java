package com.imdeity.deityapi.records;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import com.avaje.ebeaninternal.server.lib.sql.DataSourceException;
import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.DeityAPIConfigHelper;

/**
 * MySQL Database Class
 * 
 * @author vanZeben
 */
public class Database {
    
    /**
     * Persistant connection
     */
    private Connection conn;
    private boolean showQueries = false;
    
    public Database() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            throw new DataSourceException("[DeityAPI] Failed to initialize JDBC driver");
        }
        
        this.connect();
    }
    
    /**
     * Whether or not queries should be logged to console
     * 
     * @return
     */
    public boolean shouldShowQueries() {
        return showQueries;
    }
    
    /**
     * Sets the status of console logging
     * 
     * @param showQueries
     */
    public void setShowQueries(boolean showQueries) {
        this.showQueries = showQueries;
    }
    
    /**
     * Connects to the database
     * 
     * @throws Exception
     */
    private void connect() throws Exception {
        try {
            this.conn = DriverManager.getConnection(this.getConnectionString());
        } catch (SQLException e) {
            throw new DataSourceException("Failed to connect to MySQL. Have you updated the config?");
        }
    }
    
    /**
     * Dumps an SQLException with some actual debug info
     * 
     * @param ex
     */
    private void dumpSqlException(SQLException ex) {
        System.out.println("SQLException: " + ex.getMessage());
        System.out.println("SQLState: " + ex.getSQLState());
        System.out.println("VendorError: " + ex.getErrorCode());
        ex.printStackTrace();
    }
    
    /**
     * Verifies connection to the database
     */
    private void ensureConnection() {
        try {
            if (!this.conn.isValid(5)) {
                try {
                    this.connect();
                } catch (Exception e) {
                    if (e.getMessage() != null) {
                        DeityAPI.getAPI().getChatAPI().outSevere("DeityAPI", e.getMessage());
                    } else {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException ex) {
            this.dumpSqlException(ex);
        }
    }
    
    /**
     * Returns the connection
     * 
     * @return
     */
    public Connection getConn() {
        return this.conn;
    }
    
    /**
     * Returns the location of the database
     * 
     * @return
     */
    private String getConnectionString() {
        return "jdbc:mysql://" + DeityAPI.plugin.config.getString(DeityAPIConfigHelper.MYSQL_SERVER_ADDRESS) + ":" + DeityAPI.plugin.config.getInt(DeityAPIConfigHelper.MYSQL_SERVER_PORT) + "/" + DeityAPI.plugin.config.getString(DeityAPIConfigHelper.MYSQL_DATABASE_NAME) + "?user="
                + DeityAPI.plugin.config.getString(DeityAPIConfigHelper.MYSQL_DATABASE_USERNAME) + "&password=" + DeityAPI.plugin.config.getString(DeityAPIConfigHelper.MYSQL_DATABASE_PASSWORD);
    }
    
    /**
     * Prepares an SQL Statement to be sent
     * 
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    private PreparedStatement prepareSqlStatement(String sql, Object[] params) throws SQLException {
        PreparedStatement stmt = this.conn.prepareStatement(sql);
        
        int counter = 1;
        
        for (Object param : params) {
            if (param instanceof Integer) {
                stmt.setInt(counter++, (Integer) param);
            } else if (param instanceof Short) {
                stmt.setShort(counter++, (Short) param);
            } else if (param instanceof Long) {
                stmt.setLong(counter++, (Long) param);
            } else if (param instanceof Double) {
                stmt.setDouble(counter++, (Double) param);
            } else if (param instanceof String) {
                stmt.setString(counter++, (String) param);
            } else if (param == null) {
                stmt.setNull(counter++, Types.NULL);
            } else if (param instanceof Object) {
                stmt.setObject(counter++, param);
            } else {
                System.out.printf("Database -> Unsupported data type %s", param.getClass().getSimpleName());
            }
        }
        if (this.shouldShowQueries()) {
            DeityAPI.plugin.chat.out(stmt.toString());
        }
        
        return stmt;
    }
    
    /**
     * Reads a query and returns raw results
     * 
     * @param sql
     * @param params
     * @return
     */
    public HashMap<Integer, ArrayList<Object>> readRaw(String sql, Object... params) {
        this.ensureConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        HashMap<Integer, ArrayList<Object>> Rows = new HashMap<Integer, ArrayList<Object>>();
        
        try {
            stmt = this.prepareSqlStatement(sql, params);
            if (stmt.executeQuery() != null) {
                stmt.executeQuery();
                rs = stmt.getResultSet();
                while (rs.next()) {
                    ArrayList<Object> Col = new ArrayList<Object>();
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        Col.add(rs.getString(i));
                    }
                    Rows.put(rs.getRow(), Col);
                }
            }
        } catch (SQLException ex) {
            this.dumpSqlException(ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                }
                rs = null;
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                }
                stmt = null;
            }
            if (Rows.isEmpty() || (Rows == null) || (Rows.get(1) == null)) { return null; }
        }
        return Rows;
    }
    
    /**
     * Reads a query and returns a DatabaseResults object
     * 
     * @param sql
     * @param params
     * @return
     */
    public DatabaseResults readEnhanced(String sql, Object... params) {
        this.ensureConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        DatabaseResults results = null;
        
        try {
            stmt = this.prepareSqlStatement(sql, params);
            rs = stmt.executeQuery();
            if (rs != null) {
                ResultSetMetaData meta = rs.getMetaData();
                results = new DatabaseResults(meta);
                while (rs.next()) {
                    results.addRow(rs);
                }
            }
        } catch (SQLException ex) {
            this.dumpSqlException(ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                }
                rs = null;
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                }
                stmt = null;
            }
            if ((results == null) || !results.hasRows() || (results.rawResults == null)) { return null; }
        }
        return results;
    }
    
    /**
     * Returns a formatted table name
     * 
     * @param prefix
     * @param nameOfTable
     * @return
     */
    public String tableName(String prefix, String nameOfTable) {
        return (String.format("`%s`.`%s`", DeityAPI.plugin.config.getString(DeityAPIConfigHelper.MYSQL_DATABASE_NAME), prefix + nameOfTable));
    }
    
    /**
     * Writes a query to the database
     * 
     * @param sql
     * @param params
     * @return
     */
    public boolean write(String sql, Object... params) {
        try {
            this.ensureConnection();
            PreparedStatement stmt = this.prepareSqlStatement(sql, params);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            this.dumpSqlException(ex);
            return false;
        }
    }
}
