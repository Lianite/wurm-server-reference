// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.BytesRange;

public class ContentRangeHeader extends UpnpHeader<BytesRange>
{
    public static final String PREFIX = "bytes ";
    
    public ContentRangeHeader() {
    }
    
    public ContentRangeHeader(final BytesRange value) {
        this.setValue(value);
    }
    
    public ContentRangeHeader(final String s) {
        this.setString(s);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        try {
            this.setValue(BytesRange.valueOf(s, "bytes "));
        }
        catch (InvalidValueException invalidValueException) {
            throw new InvalidHeaderException("Invalid Range Header: " + invalidValueException.getMessage());
        }
    }
    
    @Override
    public String getString() {
        return this.getValue().getString(true, "bytes ");
    }
}
