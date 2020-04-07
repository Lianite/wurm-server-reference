// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import java.net.MalformedURLException;
import java.net.URL;

public class LocationHeader extends UpnpHeader<URL>
{
    public LocationHeader() {
    }
    
    public LocationHeader(final URL value) {
        this.setValue(value);
    }
    
    public LocationHeader(final String s) {
        this.setString(s);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        try {
            final URL url = new URL(s);
            this.setValue(url);
        }
        catch (MalformedURLException ex) {
            throw new InvalidHeaderException("Invalid URI: " + ex.getMessage());
        }
    }
    
    @Override
    public String getString() {
        return this.getValue().toString();
    }
}
