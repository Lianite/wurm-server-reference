// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;

public class GetAvailableSeekRangeHeader extends DLNAHeader<Integer>
{
    public GetAvailableSeekRangeHeader() {
        this.setValue(1);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            try {
                final int t = Integer.parseInt(s);
                if (t == 1) {
                    return;
                }
            }
            catch (Exception ex) {}
        }
        throw new InvalidHeaderException("Invalid GetAvailableSeekRange header value: " + s);
    }
    
    @Override
    public String getString() {
        return this.getValue().toString();
    }
}
