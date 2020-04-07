// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.http.io;

public interface HttpTransportMetrics
{
    long getBytesTransferred();
    
    void reset();
}
