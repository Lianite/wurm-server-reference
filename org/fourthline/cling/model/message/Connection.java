// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message;

import java.net.InetAddress;

public interface Connection
{
    boolean isOpen();
    
    InetAddress getRemoteAddress();
    
    InetAddress getLocalAddress();
}
