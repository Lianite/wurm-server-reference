// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.io.Serializable;

public abstract class GenericServlet implements Servlet, ServletConfig, Serializable
{
    private static final String LSTRING_FILE = "javax.servlet.LocalStrings";
    private static ResourceBundle lStrings;
    private transient ServletConfig config;
    
    public void destroy() {
    }
    
    public String getInitParameter(final String name) {
        final ServletConfig sc = this.getServletConfig();
        if (sc == null) {
            throw new IllegalStateException(GenericServlet.lStrings.getString("err.servlet_config_not_initialized"));
        }
        return sc.getInitParameter(name);
    }
    
    public Enumeration<String> getInitParameterNames() {
        final ServletConfig sc = this.getServletConfig();
        if (sc == null) {
            throw new IllegalStateException(GenericServlet.lStrings.getString("err.servlet_config_not_initialized"));
        }
        return sc.getInitParameterNames();
    }
    
    public ServletConfig getServletConfig() {
        return this.config;
    }
    
    public ServletContext getServletContext() {
        final ServletConfig sc = this.getServletConfig();
        if (sc == null) {
            throw new IllegalStateException(GenericServlet.lStrings.getString("err.servlet_config_not_initialized"));
        }
        return sc.getServletContext();
    }
    
    public String getServletInfo() {
        return "";
    }
    
    public void init(final ServletConfig config) throws ServletException {
        this.config = config;
        this.init();
    }
    
    public void init() throws ServletException {
    }
    
    public void log(final String msg) {
        this.getServletContext().log(this.getServletName() + ": " + msg);
    }
    
    public void log(final String message, final Throwable t) {
        this.getServletContext().log(this.getServletName() + ": " + message, t);
    }
    
    public abstract void service(final ServletRequest p0, final ServletResponse p1) throws ServletException, IOException;
    
    public String getServletName() {
        final ServletConfig sc = this.getServletConfig();
        if (sc == null) {
            throw new IllegalStateException(GenericServlet.lStrings.getString("err.servlet_config_not_initialized"));
        }
        return sc.getServletName();
    }
    
    static {
        GenericServlet.lStrings = ResourceBundle.getBundle("javax.servlet.LocalStrings");
    }
}
