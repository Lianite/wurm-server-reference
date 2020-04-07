// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna;

public class DLNAConversionIndicatorAttribute extends DLNAAttribute<DLNAConversionIndicator>
{
    public DLNAConversionIndicatorAttribute() {
        this.setValue(DLNAConversionIndicator.NONE);
    }
    
    public DLNAConversionIndicatorAttribute(final DLNAConversionIndicator indicator) {
        this.setValue(indicator);
    }
    
    @Override
    public void setString(final String s, final String cf) throws InvalidDLNAProtocolAttributeException {
        DLNAConversionIndicator value = null;
        try {
            value = DLNAConversionIndicator.valueOf(Integer.parseInt(s));
        }
        catch (NumberFormatException ex) {}
        if (value == null) {
            throw new InvalidDLNAProtocolAttributeException("Can't parse DLNA play speed integer from: " + s);
        }
        this.setValue(value);
    }
    
    @Override
    public String getString() {
        return Integer.toString(this.getValue().getCode());
    }
}
