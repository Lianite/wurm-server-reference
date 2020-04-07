// 
// Decompiled by Procyon v0.5.30
// 

package javax.jnlp;

import java.net.URL;

public interface BasicService
{
    URL getCodeBase();
    
    boolean isOffline();
    
    boolean showDocument(final URL p0);
    
    boolean isWebBrowserSupported();
}
