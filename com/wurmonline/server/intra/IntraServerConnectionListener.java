// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.intra;

import java.nio.ByteBuffer;

public interface IntraServerConnectionListener
{
    void reschedule(final IntraClient p0);
    
    void remove(final IntraClient p0);
    
    void commandExecuted(final IntraClient p0);
    
    void commandFailed(final IntraClient p0);
    
    void dataReceived(final IntraClient p0);
    
    void receivingData(final ByteBuffer p0);
}
