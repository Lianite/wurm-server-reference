// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.PragmaType;

public class PragmaHeader extends UpnpHeader<PragmaType>
{
    public PragmaHeader() {
    }
    
    public PragmaHeader(final PragmaType value) {
        this.setValue(value);
    }
    
    public PragmaHeader(final String s) {
        this.setString(s);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        try {
            this.setValue(PragmaType.valueOf(s));
        }
        catch (InvalidValueException invalidValueException) {
            throw new InvalidHeaderException("Invalid Range Header: " + invalidValueException.getMessage());
        }
    }
    
    @Override
    public String getString() {
        return this.getValue().getString();
    }
}
