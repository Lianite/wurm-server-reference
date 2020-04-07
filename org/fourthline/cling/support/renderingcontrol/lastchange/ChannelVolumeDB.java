// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.renderingcontrol.lastchange;

import org.fourthline.cling.support.model.Channel;

public class ChannelVolumeDB
{
    protected Channel channel;
    protected Integer volumeDB;
    
    public ChannelVolumeDB(final Channel channel, final Integer volumeDB) {
        this.channel = channel;
        this.volumeDB = volumeDB;
    }
    
    public Channel getChannel() {
        return this.channel;
    }
    
    public Integer getVolumeDB() {
        return this.volumeDB;
    }
    
    @Override
    public String toString() {
        return "VolumeDB: " + this.getVolumeDB() + " (" + this.getChannel() + ")";
    }
}
