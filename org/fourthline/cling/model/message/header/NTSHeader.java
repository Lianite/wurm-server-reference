// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.NotificationSubtype;

public class NTSHeader extends UpnpHeader<NotificationSubtype>
{
    public NTSHeader() {
    }
    
    public NTSHeader(final NotificationSubtype type) {
        this.setValue(type);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        for (final NotificationSubtype type : NotificationSubtype.values()) {
            if (s.equals(type.getHeaderString())) {
                this.setValue(type);
                break;
            }
        }
        if (this.getValue() == null) {
            throw new InvalidHeaderException("Invalid NTS header value: " + s);
        }
    }
    
    @Override
    public String getString() {
        return this.getValue().getHeaderString();
    }
}
