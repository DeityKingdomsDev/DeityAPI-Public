package com.imdeity.deityapi.exception;

/**
 * Thrown when a region already exists
 * 
 * @author vanZeben
 */
public class DuplicateRegionException extends DeityException {
    private static final long serialVersionUID = 175945283391669005L;
    
    public DuplicateRegionException() {
        super();
        this.error = "Region %s already exists.";
    }
    
    public DuplicateRegionException(String error) {
        super(error);
        this.error = error;
    }
}
