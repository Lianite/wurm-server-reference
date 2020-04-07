// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.support.model.dlna.types.NormalPlayTimeRange;

public class AvailableRangeHeader extends DLNAHeader<NormalPlayTimeRange>
{
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            try {
                this.setValue(NormalPlayTimeRange.valueOf(s, true));
                return;
            }
            catch (Exception ex) {}
        }
        throw new InvalidHeaderException("Invalid AvailableRange header value: " + s);
    }
    
    @Override
    public String getString() {
        return this.getValue().toString();
    }
}
