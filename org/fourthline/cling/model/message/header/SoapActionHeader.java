// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import java.net.URI;
import org.fourthline.cling.model.types.SoapActionType;

public class SoapActionHeader extends UpnpHeader<SoapActionType>
{
    public SoapActionHeader() {
    }
    
    public SoapActionHeader(final URI uri) {
        this.setValue(SoapActionType.valueOf(uri.toString()));
    }
    
    public SoapActionHeader(final SoapActionType value) {
        this.setValue(value);
    }
    
    public SoapActionHeader(final String s) throws InvalidHeaderException {
        this.setString(s);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        try {
            if (!s.startsWith("\"") && s.endsWith("\"")) {
                throw new InvalidHeaderException("Invalid SOAP action header, must be enclosed in doublequotes:" + s);
            }
            final SoapActionType t = SoapActionType.valueOf(s.substring(1, s.length() - 1));
            this.setValue(t);
        }
        catch (RuntimeException ex) {
            throw new InvalidHeaderException("Invalid SOAP action header value, " + ex.getMessage());
        }
    }
    
    @Override
    public String getString() {
        return "\"" + this.getValue().toString() + "\"";
    }
}
