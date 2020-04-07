// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;

public class TransferModeHeader extends DLNAHeader<Type>
{
    public TransferModeHeader() {
        this.setValue(Type.Interactive);
    }
    
    public TransferModeHeader(final Type mode) {
        this.setValue(mode);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            try {
                this.setValue(Type.valueOf(s));
                return;
            }
            catch (Exception ex) {}
        }
        throw new InvalidHeaderException("Invalid TransferMode header value: " + s);
    }
    
    @Override
    public String getString() {
        return this.getValue().toString();
    }
    
    public enum Type
    {
        Streaming, 
        Interactive, 
        Background;
    }
}
