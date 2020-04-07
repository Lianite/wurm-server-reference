// 
// Decompiled by Procyon v0.5.30
// 

package javax.jnlp;

import java.net.URL;

public interface DownloadServiceListener
{
    void progress(final URL p0, final String p1, final long p2, final long p3, final int p4);
    
    void validating(final URL p0, final String p1, final long p2, final long p3, final int p4);
    
    void upgradingArchive(final URL p0, final String p1, final int p2, final int p3);
    
    void downloadFailed(final URL p0, final String p1);
}
