// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.communication;

public interface ServerListener
{
    void clientConnected(final SocketConnection p0);
    
    void clientException(final SocketConnection p0, final Exception p1);
}
