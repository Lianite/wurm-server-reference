// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet;

import java.util.EventObject;

public class ServletContextEvent extends EventObject
{
    public ServletContextEvent(final ServletContext source) {
        super(source);
    }
    
    public ServletContext getServletContext() {
        return (ServletContext)super.getSource();
    }
}
