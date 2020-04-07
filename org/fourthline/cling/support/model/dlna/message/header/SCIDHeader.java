// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;

public class SCIDHeader extends DLNAHeader<String>
{
    public SCIDHeader() {
        this.setValue("");
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            this.setValue(s);
            return;
        }
        throw new InvalidHeaderException("Invalid SCID header value: " + s);
    }
    
    @Override
    public String getString() {
        return this.getValue().toString();
    }
}
