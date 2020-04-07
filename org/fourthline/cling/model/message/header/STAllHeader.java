// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.NotificationSubtype;

public class STAllHeader extends UpnpHeader<NotificationSubtype>
{
    public STAllHeader() {
        this.setValue(NotificationSubtype.ALL);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (!s.equals(NotificationSubtype.ALL.getHeaderString())) {
            throw new InvalidHeaderException("Invalid ST header value (not " + NotificationSubtype.ALL + "): " + s);
        }
    }
    
    @Override
    public String getString() {
        return this.getValue().getHeaderString();
    }
}
