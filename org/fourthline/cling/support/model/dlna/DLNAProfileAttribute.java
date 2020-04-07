// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna;

public class DLNAProfileAttribute extends DLNAAttribute<DLNAProfiles>
{
    public DLNAProfileAttribute() {
        this.setValue(DLNAProfiles.NONE);
    }
    
    public DLNAProfileAttribute(final DLNAProfiles profile) {
        this.setValue(profile);
    }
    
    @Override
    public void setString(final String s, final String cf) throws InvalidDLNAProtocolAttributeException {
        final DLNAProfiles value = DLNAProfiles.valueOf(s, cf);
        if (value == null) {
            throw new InvalidDLNAProtocolAttributeException("Can't parse DLNA profile from: " + s);
        }
        this.setValue(value);
    }
    
    @Override
    public String getString() {
        return this.getValue().getCode();
    }
}
