package com.imdeity.deityapi.exception;

/**
 * Thrown when a region does not exist
 * 
 * @author vanZeben
 */
public class InvalidRegionException extends DeityException {
    private static final long serialVersionUID = 175945283391669005L;
    
    public InvalidRegionException() {
        super();
        this.error = "Invalid Region.";
    }
    
    public InvalidRegionException(String error) {
        super(error);
        this.error = error;
    }
}
