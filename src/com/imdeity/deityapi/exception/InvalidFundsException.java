package com.imdeity.deityapi.exception;

/**
 * Thrown when a user has invalid funds
 * 
 * @author vanZeben
 */
public class InvalidFundsException extends DeityException {
    private static final long serialVersionUID = 175945283391669005L;
    
    public InvalidFundsException() {
        super();
        this.error = "You have insufficient funds to perform this action.";
    }
    
    public InvalidFundsException(String error) {
        super(error);
        this.error = error;
    }
}
