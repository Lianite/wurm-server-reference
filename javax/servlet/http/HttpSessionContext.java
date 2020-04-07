// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet.http;

import java.util.Enumeration;

public interface HttpSessionContext
{
    HttpSession getSession(final String p0);
    
    Enumeration<String> getIds();
}
