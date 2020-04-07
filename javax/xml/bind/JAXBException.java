// 
// Decompiled by Procyon v0.5.30
// 

package javax.xml.bind;

import java.io.PrintWriter;
import java.io.PrintStream;

public class JAXBException extends Exception
{
    private String errorCode;
    private Throwable linkedException;
    static final long serialVersionUID = -5621384651494307979L;
    
    public JAXBException(final String message) {
        this(message, null, null);
    }
    
    public JAXBException(final String message, final String errorCode) {
        this(message, errorCode, null);
    }
    
    public JAXBException(final Throwable exception) {
        this(null, null, exception);
    }
    
    public JAXBException(final String message, final Throwable exception) {
        this(message, null, exception);
    }
    
    public JAXBException(final String message, final String errorCode, final Throwable exception) {
        super(message);
        this.errorCode = errorCode;
        this.linkedException = exception;
    }
    
    public String getErrorCode() {
        return this.errorCode;
    }
    
    public Throwable getLinkedException() {
        return this.linkedException;
    }
    
    public synchronized void setLinkedException(final Throwable exception) {
        this.linkedException = exception;
    }
    
    public String toString() {
        return (this.linkedException == null) ? super.toString() : (super.toString() + "\n - with linked exception:\n[" + this.linkedException.toString() + "]");
    }
    
    public void printStackTrace(final PrintStream s) {
        super.printStackTrace(s);
    }
    
    public void printStackTrace() {
        super.printStackTrace();
    }
    
    public void printStackTrace(final PrintWriter s) {
        super.printStackTrace(s);
    }
    
    public Throwable getCause() {
        return this.linkedException;
    }
}
