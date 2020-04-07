// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet;

import java.util.EventListener;

public interface ServletContextAttributeListener extends EventListener
{
    void attributeAdded(final ServletContextAttributeEvent p0);
    
    void attributeRemoved(final ServletContextAttributeEvent p0);
    
    void attributeReplaced(final ServletContextAttributeEvent p0);
}
