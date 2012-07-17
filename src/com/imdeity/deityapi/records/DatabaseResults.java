package com.imdeity.deityapi.records;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Sexy class for dealing with MySQL Results
 * 
 * @author vanZeben
 */
public class DatabaseResults {
    
    private class ColumnInfo {
        @SuppressWarnings("unused")
        public int columnIndex = -1;
        public String columnName = "";
        public int columnType;
        
        public ColumnInfo(ResultSetMetaData meta, int index) {
            try {
                if (index < meta.getColumnCount()) {
                    this.columnIndex = index;
                    this.columnName = meta.getColumnLabel(index + 1).toLowerCase();
                    this.columnType = meta.getColumnType(index + 1);
                }
            } catch (SQLException e) {
                this.columnType = 9999;
            }
        }
    }
    
    private ArrayList<Integer> columnXrefColumnInfo = new ArrayList<Integer>();
    private ArrayList<ColumnInfo> columnInfo = new ArrayList<ColumnInfo>();
    private HashMap<String, Integer> columnNameToIndex = new HashMap<String, Integer>();
    public ArrayList<ArrayList<Object>> rawResults = new ArrayList<ArrayList<Object>>();
    
    public DatabaseResults(ResultSetMetaData meta) {
        try {
            for (int counter = 0; counter < meta.getColumnCount(); counter++) {
                
                int columnXref = -1;
                if (this.isSupported(meta.getColumnName(counter + 1), meta.getColumnType(counter + 1))) {
                    ColumnInfo cInfo = new ColumnInfo(meta, counter);
                    this.columnInfo.add(cInfo);
                    columnXref = this.columnInfo.size() - 1;
                    this.columnNameToIndex.put(cInfo.columnName, columnXref);
                }
                this.columnXrefColumnInfo.add(columnXref);
            }
        } catch (SQLException e) { /* EMPTY */
        }
    }
    
    /**
     * Adds a row
     * 
     * @param rs
     * @return
     */
    public boolean addRow(ResultSet rs) {
        ArrayList<Object> newResult = new ArrayList<Object>();
        try {
            for (int counter = 0; counter < rs.getMetaData().getColumnCount(); counter++) {
                int xRefIndex = this.columnXrefColumnInfo.get(counter);
                if (xRefIndex != -1) {
                    newResult.add(rs.getObject(counter + 1));
                }
            }
            this.rawResults.add(newResult);
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
    
    /**
     * Returns a boolean from the query
     * 
     * @param index
     * @param columnName
     * @return
     * @throws SQLDataException
     */
    public boolean getBoolean(int index, String columnName) throws SQLDataException {
        Integer columnIndex = this.getColumnIndex(columnName);
        if (columnIndex != null) {
            ColumnInfo columnInfo = this.columnInfo.get(columnIndex);
            switch (columnInfo.columnType) {
                case Types.BOOLEAN:
                    Object temp = this.rawResults.get(index).get(columnIndex);
                    if (temp instanceof Boolean) { return (Boolean) temp; }
                    throw new SQLDataException(String.format("Field index %d (%s): Bad field type, cannot case to Boolean.", columnIndex, columnInfo.columnName));
                default:
                    throw new SQLDataException(String.format("Field index %d (%s): Bad field type, cannot be retrieved with getBoolean.", columnIndex, columnInfo.columnName));
            }
        }
        throw new SQLDataException(String.format("Column '%s' does not exist in query", columnName));
    }
    
    /**
     * Returns the column index
     * 
     * @param columnName
     * @return
     */
    private Integer getColumnIndex(String columnName) {
        return this.columnNameToIndex.get(columnName.toLowerCase());
    }
    
    /**
     * Returns a date object from an SQL Date/Time/Timestamp from the query
     * 
     * @param index
     * @param columnName
     * @return
     * @throws SQLDataException
     */
    public Date getDate(int index, String columnName) throws SQLDataException {
        Integer columnIndex = this.getColumnIndex(columnName);
        if (columnIndex != null) {
            ColumnInfo columnInfo = this.columnInfo.get(columnIndex);
            switch (columnInfo.columnType) {
                case Types.DATE:
                case Types.TIME:
                case Types.TIMESTAMP:
                    return (Date) (this.rawResults.get(index).get(columnIndex));
                default:
                    throw new SQLDataException(String.format("Field index %d (%s): Bad field type, cannot be retrieved with getDate.", columnIndex, columnInfo.columnName));
            }
        }
        throw new SQLDataException(String.format("Column '%s' does not exist in query", columnName));
    }
    
    /**
     * Returns a double from the query
     * 
     * @param index
     * @param columnName
     * @return
     * @throws SQLDataException
     */
    public Double getDouble(int index, String columnName) throws SQLDataException {
        Integer columnIndex = this.getColumnIndex(columnName);
        if (columnIndex != null) {
            ColumnInfo columnInfo = this.columnInfo.get(columnIndex);
            switch (columnInfo.columnType) {
                case Types.FLOAT:
                case Types.DOUBLE:
                    return (Double) (this.rawResults.get(index).get(columnIndex));
                default:
                    throw new SQLDataException(String.format("Field index %d (%s): Bad field type, cannot be retrieved with getString.", columnIndex, columnInfo.columnName));
            }
        }
        throw new SQLDataException(String.format("Field index %d out of range", columnIndex));
    }
    
    /**
     * Returns a int from the query
     * 
     * @param index
     * @param columnName
     * @return
     * @throws SQLDataException
     */
    public Integer getInteger(int index, String columnName) throws SQLDataException {
        Integer columnIndex = this.getColumnIndex(columnName);
        if (columnIndex != null) {
            ColumnInfo columnInfo = this.columnInfo.get(columnIndex);
            
            switch (columnInfo.columnType) {
                case Types.INTEGER:
                    return (Integer) (this.rawResults.get(index).get(columnIndex));
                default:
                    throw new SQLDataException(String.format("Field index %d (%s): Bad field type, cannot be retrieved with getInteger.", columnIndex, columnInfo.columnName));
            }
        }
        throw new SQLDataException(String.format("Column '%s' does not exist in query", columnName));
    }
    
    /**
     * Returns a long from the query
     * 
     * @param index
     * @param columnName
     * @return
     * @throws SQLDataException
     */
    public long getLong(int index, String columnName) throws SQLDataException {
        Integer columnIndex = this.getColumnIndex(columnName);
        if (columnIndex != null) {
            ColumnInfo columnInfo = this.columnInfo.get(columnIndex);
            switch (columnInfo.columnType) {
                case Types.INTEGER:
                    Object temp = this.rawResults.get(index).get(columnIndex);
                    if (temp instanceof Integer) {
                        return ((Integer) temp).longValue();
                    } else if (temp instanceof Long) {
                        return (Long) temp;
                    } else {
                        throw new SQLDataException(String.format("Field index %d (%s): Bad field type, cannot case to LONG.", columnIndex, columnInfo.columnName));
                    }
                default:
                    throw new SQLDataException(String.format("Field index %d (%s): Bad field type, cannot be retrieved with getLong.", columnIndex, columnInfo.columnName));
            }
        }
        throw new SQLDataException(String.format("Column '%s' does not exist in query", columnName));
    }
    
    /**
     * Returns a string from the query
     * 
     * @param index
     * @param columnName
     * @return
     * @throws SQLDataException
     */
    public String getString(int index, String columnName) throws SQLDataException {
        Integer columnIndex = this.getColumnIndex(columnName);
        if (columnIndex != null) {
            ColumnInfo columnInfo = this.columnInfo.get(columnIndex);
            switch (columnInfo.columnType) {
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                    return (String) (this.rawResults.get(index).get(columnIndex));
                default:
                    throw new SQLDataException(String.format("Field index %d (%s): Bad field type, cannot be retrieved with getString.", columnIndex, columnInfo.columnName));
            }
        }
        throw new SQLDataException(String.format("Field index %d out of range"));
    }
    
    /**
     * Returns whether or not the query has rows
     * 
     * @return
     */
    public boolean hasRows() {
        return this.rowCount() > 0;
    }
    
    /**
     * Returns whether or not the datatype is supported
     * 
     * @param columnName
     * @param columnType
     * @return
     */
    private boolean isSupported(String columnName, int columnType) {
        switch (columnType) {
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.INTEGER:
            case Types.LONGVARCHAR:
            case Types.VARCHAR:
            case Types.BOOLEAN:
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:

                return true;
            default:
                System.out.println(String.format("column '%s' has an unsupported columntype(%d)", columnName, columnType));
                return false;
        }
    }
    
    /**
     * Returns the amount of rows returned from the query
     * 
     * @return
     */
    public int rowCount() {
        return this.rawResults.size();
    }
}
