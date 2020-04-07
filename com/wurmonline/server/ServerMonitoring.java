// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

public interface ServerMonitoring
{
    boolean isLagging();
    
    byte[] getExternalIp();
    
    byte[] getInternalIp();
    
    int getIntraServerPort();
}
