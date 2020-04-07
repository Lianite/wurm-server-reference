// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.dlna;

import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;

public class DLNAPlaySpeedAttribute extends DLNAAttribute<AVTransportVariable.TransportPlaySpeed[]>
{
    public DLNAPlaySpeedAttribute() {
        this.setValue(new AVTransportVariable.TransportPlaySpeed[0]);
    }
    
    public DLNAPlaySpeedAttribute(final AVTransportVariable.TransportPlaySpeed[] speeds) {
        this.setValue(speeds);
    }
    
    public DLNAPlaySpeedAttribute(final String[] speeds) {
        final AVTransportVariable.TransportPlaySpeed[] sp = new AVTransportVariable.TransportPlaySpeed[speeds.length];
        try {
            for (int i = 0; i < speeds.length; ++i) {
                sp[i] = new AVTransportVariable.TransportPlaySpeed(speeds[i]);
            }
        }
        catch (InvalidValueException invalidValueException) {
            throw new InvalidDLNAProtocolAttributeException("Can't parse DLNA play speeds.");
        }
        this.setValue(sp);
    }
    
    @Override
    public void setString(final String s, final String cf) throws InvalidDLNAProtocolAttributeException {
        AVTransportVariable.TransportPlaySpeed[] value = null;
        if (s != null && s.length() != 0) {
            final String[] speeds = s.split(",");
            try {
                value = new AVTransportVariable.TransportPlaySpeed[speeds.length];
                for (int i = 0; i < speeds.length; ++i) {
                    value[i] = new AVTransportVariable.TransportPlaySpeed(speeds[i]);
                }
            }
            catch (InvalidValueException invalidValueException) {
                value = null;
            }
        }
        if (value == null) {
            throw new InvalidDLNAProtocolAttributeException("Can't parse DLNA play speeds from: " + s);
        }
        this.setValue(value);
    }
    
    @Override
    public String getString() {
        String s = "";
        for (final AVTransportVariable.TransportPlaySpeed speed : this.getValue()) {
            if (!speed.getValue().equals("1")) {
                s = s + ((s.length() == 0) ? "" : ",") + speed;
            }
        }
        return s;
    }
}
