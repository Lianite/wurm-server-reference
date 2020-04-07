// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class BufferBytesHeader extends DLNAHeader<UnsignedIntegerFourBytes>
{
    public BufferBytesHeader() {
        this.setValue(new UnsignedIntegerFourBytes(0L));
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        try {
            this.setValue(new UnsignedIntegerFourBytes(s));
        }
        catch (NumberFormatException ex) {
            throw new InvalidHeaderException("Invalid header value: " + s);
        }
    }
    
    @Override
    public String getString() {
        return this.getValue().getValue().toString();
    }
}
