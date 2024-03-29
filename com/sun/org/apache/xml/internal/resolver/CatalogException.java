// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.org.apache.xml.internal.resolver;

public class CatalogException extends Exception
{
    public static final int WRAPPER = 1;
    public static final int INVALID_ENTRY = 2;
    public static final int INVALID_ENTRY_TYPE = 3;
    public static final int NO_XML_PARSER = 4;
    public static final int UNKNOWN_FORMAT = 5;
    public static final int UNPARSEABLE = 6;
    public static final int PARSE_FAILED = 7;
    public static final int UNENDED_COMMENT = 8;
    private Exception exception;
    private int exceptionType;
    
    public CatalogException(final int type, final String message) {
        super(message);
        this.exception = null;
        this.exceptionType = 0;
        this.exceptionType = type;
        this.exception = null;
    }
    
    public CatalogException(final int type) {
        super("Catalog Exception " + type);
        this.exception = null;
        this.exceptionType = 0;
        this.exceptionType = type;
        this.exception = null;
    }
    
    public CatalogException(final Exception e) {
        this.exception = null;
        this.exceptionType = 0;
        this.exceptionType = 1;
        this.exception = e;
    }
    
    public CatalogException(final String message, final Exception e) {
        super(message);
        this.exception = null;
        this.exceptionType = 0;
        this.exceptionType = 1;
        this.exception = e;
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
