// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet;

import java.util.Enumeration;

public interface FilterConfig
{
    String getFilterName();
    
    ServletContext getServletContext();
    
    String getInitParameter(final String p0);
    
    Enumeration<String> getInitParameterNames();
}
