// 
// Decompiled by Procyon v0.5.30
// 

package javax.jnlp;

import java.net.URL;

public interface ExtensionInstallerService
{
    String getInstallPath();
    
    String getExtensionVersion();
    
    URL getExtensionLocation();
    
    void hideProgressBar();
    
    void hideStatusWindow();
    
    void setHeading(final String p0);
    
    void setStatus(final String p0);
    
    void updateProgress(final int p0);
    
    void installSucceeded(final boolean p0);
    
    void installFailed();
    
    void setJREInfo(final String p0, final String p1);
    
    void setNativeLibraryInfo(final String p0);
    
    String getInstalledJRE(final URL p0, final String p1);
}
