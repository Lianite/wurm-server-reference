// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

public class EventSequenceHeader extends UpnpHeader<UnsignedIntegerFourBytes>
{
    public EventSequenceHeader() {
    }
    
    public EventSequenceHeader(final long value) {
        this.setValue(new UnsignedIntegerFourBytes(value));
    }
    
    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (!"0".equals(s)) {
            while (s.startsWith("0")) {
                s = s.substring(1);
            }
        }
        try {
            this.setValue(new UnsignedIntegerFourBytes(s));
        }
        catch (NumberFormatException ex) {
            throw new InvalidHeaderException("Invalid event sequence, " + ex.getMessage());
        }
    }
    
    @Override
    public String getString() {
        return this.getValue().toString();
    }
}
