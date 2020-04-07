// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver;

public class CatalogException extends Exception
{
    public static final int WRAPPER = 1;
    public static final int INVALID_ENTRY = 2;
    public static final int INVALID_ENTRY_TYPE = 3;
    public static final int NO_XML_PARSER = 4;
    public static final int UNKNOWN_FORMAT = 5;
    public static final int UNPARSEABLE = 6;
    public static final int PARSE_FAILED = 7;
    private Exception exception;
    private int exceptionType;
    
    public CatalogException(final int exceptionType, final String s) {
        super(s);
        this.exception = null;
        this.exceptionType = 0;
        this.exceptionType = exceptionType;
        this.exception = null;
    }
    
    public CatalogException(final int exceptionType) {
        super("Catalog Exception " + exceptionType);
        this.exception = null;
        this.exceptionType = 0;
        this.exceptionType = exceptionType;
        this.exception = null;
    }
    
    public CatalogException(final Exception exception) {
        this.exception = null;
        this.exceptionType = 0;
        this.exceptionType = 1;
        this.exception = exception;
    }
    
    public CatalogException(final String s, final Exception exception) {
        super(s);
        this.exception = null;
        this.exceptionType = 0;
        this.exceptionType = 1;
        this.exception = exception;
    }
    
    public String getMessage() {
        final String message = super.getMessage();
        if (message == null && this.exception != null) {
            return this.exception.getMessage();
        }
        return message;
    }
    
    public Exception getException() {
        return this.exception;
    }
    
    public int getExceptionType() {
        return this.exceptionType;
    }
    
    public String toString() {
        if (this.exception != null) {
            return this.exception.toString();
        }
        return super.toString();
    }
}
