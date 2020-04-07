// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.spi;

import org.fourthline.cling.model.message.gena.IncomingEventRequestMessage;
import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.message.gena.OutgoingEventRequestMessage;

public interface GENAEventProcessor
{
    void writeBody(final OutgoingEventRequestMessage p0) throws UnsupportedDataException;
    
    void readBody(final IncomingEventRequestMessage p0) throws UnsupportedDataException;
}
