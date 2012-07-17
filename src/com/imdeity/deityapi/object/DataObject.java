package com.imdeity.deityapi.object;

import com.imdeity.deityapi.records.Database;

/**
 * Handles interaction with MySQL and other data functions
 * 
 * @author vanZeben
 */
public class DataObject {
    
    /**
     * Database class
     */
    private Database mySQL = null;
    
    public DataObject() throws Exception {
        mySQL = new Database();
    }
    
    /**
     * API call to deal with MySQL
     * 
     * @return
     */
    public Database getMySQL() {
        return mySQL;
    }
}
