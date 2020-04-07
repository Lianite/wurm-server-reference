// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.renderingcontrol.lastchange;

import org.fourthline.cling.support.model.Channel;

public class ChannelVolume
{
    protected Channel channel;
    protected Integer volume;
    
    public ChannelVolume(final Channel channel, final Integer volume) {
        this.channel = channel;
        this.volume = volume;
    }
    
    public Channel getChannel() {
        return this.channel;
    }
    
    public Integer getVolume() {
        return this.volume;
    }
    
    @Override
    public String toString() {
        return "Volume: " + this.getVolume() + " (" + this.getChannel() + ")";
    }
}
