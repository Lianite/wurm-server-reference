// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;

public class MaxPrateHeader extends DLNAHeader<Long>
{
    public MaxPrateHeader() {
        this.setValue(0L);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        try {
            this.setValue(Long.parseLong(s));
        }
        catch (NumberFormatException ex) {
            throw new InvalidHeaderException("Invalid SCID header value: " + s);
        }
    }
    
    @Override
    public String getString() {
        return this.getValue().toString();
    }
}
