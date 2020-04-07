// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.renderingcontrol.lastchange;

import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.support.shared.AbstractMap;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.BooleanDatatype;
import org.fourthline.cling.support.model.Channel;
import java.util.Map;
import org.fourthline.cling.support.lastchange.EventedValue;

public class EventedValueChannelLoudness extends EventedValue<ChannelLoudness>
{
    public EventedValueChannelLoudness(final ChannelLoudness value) {
        super(value);
    }
    
    public EventedValueChannelLoudness(final Map.Entry<String, String>[] attributes) {
        super(attributes);
    }
    
    @Override
    protected ChannelLoudness valueOf(final Map.Entry<String, String>[] attributes) throws InvalidValueException {
        Channel channel = null;
        Boolean loudness = null;
        for (final Map.Entry<String, String> attribute : attributes) {
            if (attribute.getKey().equals("channel")) {
                channel = Channel.valueOf(attribute.getValue());
            }
            if (attribute.getKey().equals("val")) {
                loudness = new BooleanDatatype().valueOf(attribute.getValue());
            }
        }
        return (channel != null && loudness != null) ? new ChannelLoudness(channel, loudness) : null;
    }
    
    @Override
    public Map.Entry<String, String>[] getAttributes() {
        return (Map.Entry<String, String>[])new Map.Entry[] { new AbstractMap.SimpleEntry("val", new BooleanDatatype().getString(this.getValue().getLoudness())), new AbstractMap.SimpleEntry("channel", this.getValue().getChannel().name()) };
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
