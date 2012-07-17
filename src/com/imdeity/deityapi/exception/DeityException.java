package com.imdeity.deityapi.exception;

/**
 * Default Deity Exception
 * 
 * @author vanZeben
 */
public class DeityException extends Exception {
    private static final long serialVersionUID = -6821768221748544277L;
    public String error;
    
    public DeityException() {
        super();
        this.error = "unknown";
    }
    
    public DeityException(String error) {
        super(error);
        this.error = error;
    }
    
    public String getError() {
        return this.error;
    }
}
