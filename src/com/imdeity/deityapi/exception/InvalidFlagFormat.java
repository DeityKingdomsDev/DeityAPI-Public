package com.imdeity.deityapi.exception;

/**
 * Thrown when an invalid flag was attempted to be accessed
 * 
 * @author vanZeben
 */
public class InvalidFlagFormat extends DeityException {
    
    private static final long serialVersionUID = 8101615074524004172L;
    
    public InvalidFlagFormat(String msg) {
        super(msg);
    }
}
