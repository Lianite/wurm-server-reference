// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;

public class FriendlyNameHeader extends DLNAHeader<String>
{
    public FriendlyNameHeader() {
        this.setValue("");
    }
    
    public FriendlyNameHeader(final String name) {
        this.setValue(name);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            this.setValue(s);
            return;
        }
        throw new InvalidHeaderException("Invalid GetAvailableSeekRange header value: " + s);
    }
    
    @Override
    public String getString() {
        return this.getValue();
    }
}
