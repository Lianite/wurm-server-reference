// 
// Decompiled by Procyon v0.5.30
// 

package javax.servlet;

import java.util.Set;

public interface ServletContainerInitializer
{
    void onStartup(final Set<Class<?>> p0, final ServletContext p1) throws ServletException;
}
