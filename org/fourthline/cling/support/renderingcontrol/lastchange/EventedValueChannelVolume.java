// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.renderingcontrol.lastchange;

import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.support.shared.AbstractMap;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytesDatatype;
import org.fourthline.cling.support.model.Channel;
import java.util.Map;
import org.fourthline.cling.support.lastchange.EventedValue;

public class EventedValueChannelVolume extends EventedValue<ChannelVolume>
{
    public EventedValueChannelVolume(final ChannelVolume value) {
        super(value);
    }
    
    public EventedValueChannelVolume(final Map.Entry<String, String>[] attributes) {
        super(attributes);
    }
    
    @Override
    protected ChannelVolume valueOf(final Map.Entry<String, String>[] attributes) throws InvalidValueException {
        Channel channel = null;
        Integer volume = null;
        for (final Map.Entry<String, String> attribute : attributes) {
            if (attribute.getKey().equals("channel")) {
                channel = Channel.valueOf(attribute.getValue());
            }
            if (attribute.getKey().equals("val")) {
                volume = (int)(Object)new UnsignedIntegerTwoBytesDatatype().valueOf(attribute.getValue()).getValue();
            }
        }
        return (channel != null && volume != null) ? new ChannelVolume(channel, volume) : null;
    }
    
    @Override
    public Map.Entry<String, String>[] getAttributes() {
        return (Map.Entry<String, String>[])new Map.Entry[] { new AbstractMap.SimpleEntry("val", new UnsignedIntegerTwoBytesDatatype().getString(new UnsignedIntegerTwoBytes(this.getValue().getVolume()))), new AbstractMap.SimpleEntry("channel", this.getValue().getChannel().name()) };
    }
    
    @Override
    public String toString() {
        return this.getValue().toString();
    }
    
    @Override
    protected Datatype getDatatype() {
        return null;
    }
}
