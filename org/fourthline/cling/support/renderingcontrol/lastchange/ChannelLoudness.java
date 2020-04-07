// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.renderingcontrol.lastchange;

import org.fourthline.cling.support.model.Channel;

public class ChannelLoudness
{
    protected Channel channel;
    protected Boolean loudness;
    
    public ChannelLoudness(final Channel channel, final Boolean loudness) {
        this.channel = channel;
        this.loudness = loudness;
    }
    
    public Channel getChannel() {
        return this.channel;
    }
    
    public Boolean getLoudness() {
        return this.loudness;
    }
    
    @Override
    public String toString() {
        return "Loudness: " + this.getLoudness() + " (" + this.getChannel() + ")";
    }
}
