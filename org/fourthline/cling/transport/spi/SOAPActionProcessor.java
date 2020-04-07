// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.transport.spi;

import org.fourthline.cling.model.message.control.ActionResponseMessage;
import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.control.ActionRequestMessage;

public interface SOAPActionProcessor
{
    void writeBody(final ActionRequestMessage p0, final ActionInvocation p1) throws UnsupportedDataException;
    
    void writeBody(final ActionResponseMessage p0, final ActionInvocation p1) throws UnsupportedDataException;
    
    void readBody(final ActionRequestMessage p0, final ActionInvocation p1) throws UnsupportedDataException;
    
    void readBody(final ActionResponseMessage p0, final ActionInvocation p1) throws UnsupportedDataException;
}
