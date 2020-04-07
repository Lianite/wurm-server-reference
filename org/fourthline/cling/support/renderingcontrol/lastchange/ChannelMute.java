// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.renderingcontrol.lastchange;

import org.fourthline.cling.support.model.Channel;

public class ChannelMute
{
    protected Channel channel;
    protected Boolean mute;
    
    public ChannelMute(final Channel channel, final Boolean mute) {
        this.channel = channel;
        this.mute = mute;
    }
    
    public Channel getChannel() {
        return this.channel;
    }
    
    public Boolean getMute() {
        return this.mute;
    }
    
    @Override
    public String toString() {
        return "Mute: " + this.getMute() + " (" + this.getChannel() + ")";
    }
}
