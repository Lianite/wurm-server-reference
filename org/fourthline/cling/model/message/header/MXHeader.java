// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model.message.header;

public class MXHeader extends UpnpHeader<Integer>
{
    public static final Integer DEFAULT_VALUE;
    
    public MXHeader() {
        this.setValue(MXHeader.DEFAULT_VALUE);
    }
    
    public MXHeader(final Integer delayInSeconds) {
        this.setValue(delayInSeconds);
    }
    
    @Override
    public void setString(final String s) throws InvalidHeaderException {
        Integer value;
        try {
            value = Integer.parseInt(s);
        }
        catch (Exception ex) {
            throw new InvalidHeaderException("Can't parse MX seconds integer from: " + s);
        }
        if (value < 0 || value > 120) {
            this.setValue(MXHeader.DEFAULT_VALUE);
        }
        else {
            this.setValue(value);
        }
    }
    
    @Override
    public String getString() {
        return this.getValue().toString();
    }
    
    static {
        DEFAULT_VALUE = 3;
    }
}
