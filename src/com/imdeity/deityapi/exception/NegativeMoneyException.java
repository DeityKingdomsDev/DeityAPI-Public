package com.imdeity.deityapi.exception;

/**
 * Thrown when a user attempts to pay a negative amount
 * 
 * @author vanZeben
 */
public class NegativeMoneyException extends DeityException {
    private static final long serialVersionUID = 175945283391669005L;
    
    public NegativeMoneyException() {
        super();
        this.error = "What do you think I am? An ATM?";
    }
    
    public NegativeMoneyException(String error) {
        super(error);
        this.error = error;
    }
}
