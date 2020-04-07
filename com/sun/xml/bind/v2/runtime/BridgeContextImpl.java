// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime;

import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.JAXBException;
import javax.xml.bind.ValidationEventHandler;
import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import com.sun.xml.bind.api.BridgeContext;

public final class BridgeContextImpl extends BridgeContext
{
    public final UnmarshallerImpl unmarshaller;
    public final MarshallerImpl marshaller;
    
    BridgeContextImpl(final JAXBContextImpl context) {
        this.unmarshaller = context.createUnmarshaller();
        this.marshaller = context.createMarshaller();
    }
    
    public void setErrorHandler(final ValidationEventHandler handler) {
        try {
            this.unmarshaller.setEventHandler(handler);
            this.marshaller.setEventHandler(handler);
        }
        catch (JAXBException e) {
            throw new Error(e);
        }
    }
    
    public void setAttachmentMarshaller(final AttachmentMarshaller m) {
        this.marshaller.setAttachmentMarshaller(m);
    }
    
    public void setAttachmentUnmarshaller(final AttachmentUnmarshaller u) {
        this.unmarshaller.setAttachmentUnmarshaller(u);
    }
    
    public AttachmentMarshaller getAttachmentMarshaller() {
        return this.marshaller.getAttachmentMarshaller();
    }
    
    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        return this.unmarshaller.getAttachmentUnmarshaller();
    }
}
