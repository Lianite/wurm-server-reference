// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

public class SubscriptionIdHeader extends UpnpHeader<String>
{
    public static final String PREFIX = "uuid:";
    
    public SubscriptionIdHeader() {
    }
    
    public SubscriptionIdHeader(final String value) {
        this.setValue(value);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        if (!s.startsWith("uuid:")) {
            throw new InvalidHeaderException("Invalid subscription ID header value, must start with 'uuid:': " + s);
        }
        this.setValue(s);
    }
    
    @Override
    public String getString() {
        return this.getValue();
    }
}
