// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet;

public class ServletException extends Exception
{
    private Throwable rootCause;
    
    public ServletException() {
    }
    
    public ServletException(final String message) {
        super(message);
    }
    
    public ServletException(final String message, final Throwable rootCause) {
        super(message, rootCause);
        this.rootCause = rootCause;
    }
    
    public ServletException(final Throwable rootCause) {
        super(rootCause);
        this.rootCause = rootCause;
    }
    
    public Throwable getRootCause() {
        return this.rootCause;
    }
}
