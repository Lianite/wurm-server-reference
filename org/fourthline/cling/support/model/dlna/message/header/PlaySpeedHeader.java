// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;

public class PlaySpeedHeader extends DLNAHeader<AVTransportVariable.TransportPlaySpeed>
{
    public PlaySpeedHeader() {
    }
    
    public PlaySpeedHeader(final AVTransportVariable.TransportPlaySpeed speed) {
        this.setValue(speed);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (s.length() != 0) {
            try {
                final AVTransportVariable.TransportPlaySpeed t = new AVTransportVariable.TransportPlaySpeed(s);
                this.setValue(t);
                return;
            }
            catch (InvalidValueException ex) {}
        }
        throw new InvalidHeaderException("Invalid PlaySpeed header value: " + s);
    }
    
    @Override
    public String getString() {
        return this.getValue().getValue();
    }
}
