// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.api;

import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.ValidationEventHandler;

public abstract class BridgeContext
{
    public abstract void setErrorHandler(final ValidationEventHandler p0);
    
    public abstract void setAttachmentMarshaller(final AttachmentMarshaller p0);
    
    public abstract void setAttachmentUnmarshaller(final AttachmentUnmarshaller p0);
    
    public abstract AttachmentMarshaller getAttachmentMarshaller();
    
    public abstract AttachmentUnmarshaller getAttachmentUnmarshaller();
}
