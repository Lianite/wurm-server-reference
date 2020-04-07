// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.support.model.dlna.types.BufferInfoType;

public class BufferInfoHeader extends DLNAHeader<BufferInfoType>
{
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            try {
                this.setValue(BufferInfoType.valueOf(s));
                return;
            }
            catch (Exception ex) {}
        }
        throw new InvalidHeaderException("Invalid BufferInfo header value: " + s);
    }
    
    @Override
    public String getString() {
        return this.getValue().getString();
    }
}
