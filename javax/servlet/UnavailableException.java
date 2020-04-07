// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet;

public class UnavailableException extends ServletException
{
    private Servlet servlet;
    private boolean permanent;
    private int seconds;
    
    public UnavailableException(final Servlet servlet, final String msg) {
        super(msg);
        this.servlet = servlet;
        this.permanent = true;
    }
    
    public UnavailableException(final int seconds, final Servlet servlet, final String msg) {
        super(msg);
        this.servlet = servlet;
        if (seconds <= 0) {
            this.seconds = -1;
        }
        else {
            this.seconds = seconds;
        }
        this.permanent = false;
    }
    
    public UnavailableException(final String msg) {
        super(msg);
        this.permanent = true;
    }
    
    public UnavailableException(final String msg, final int seconds) {
        super(msg);
        if (seconds <= 0) {
            this.seconds = -1;
        }
        else {
            this.seconds = seconds;
        }
        this.permanent = false;
    }
    
    public boolean isPermanent() {
        return this.permanent;
    }
    
    public Servlet getServlet() {
        return this.servlet;
    }
    
    public int getUnavailableSeconds() {
        return this.permanent ? -1 : this.seconds;
    }
}
