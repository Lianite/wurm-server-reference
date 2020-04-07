// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet.http;

import java.util.Enumeration;
import javax.servlet.ServletContext;

public interface HttpSession
{
    long getCreationTime();
    
    String getId();
    
    long getLastAccessedTime();
    
    ServletContext getServletContext();
    
    void setMaxInactiveInterval(final int p0);
    
    int getMaxInactiveInterval();
    
    HttpSessionContext getSessionContext();
    
    Object getAttribute(final String p0);
    
    Object getValue(final String p0);
    
    Enumeration<String> getAttributeNames();
    
    String[] getValueNames();
    
    void setAttribute(final String p0, final Object p1);
    
    void putValue(final String p0, final Object p1);
    
    void removeAttribute(final String p0);
    
    void removeValue(final String p0);
    
    void invalidate();
    
    boolean isNew();
}
