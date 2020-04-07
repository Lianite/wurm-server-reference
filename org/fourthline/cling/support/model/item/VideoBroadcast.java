// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.item;

import java.net.URI;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.DIDLObject;

public class VideoBroadcast extends VideoItem
{
    public static final Class CLASS;
    
    public VideoBroadcast() {
        this.setClazz(VideoBroadcast.CLASS);
    }
    
    public VideoBroadcast(final Item other) {
        super(other);
    }
    
    public VideoBroadcast(final String id, final Container parent, final String title, final String creator, final Res... resource) {
        this(id, parent.getId(), title, creator, resource);
    }
    
    public VideoBroadcast(final String id, final String parentID, final String title, final String creator, final Res... resource) {
        super(id, parentID, title, creator, resource);
        this.setClazz(VideoBroadcast.CLASS);
    }
    
    public URI getIcon() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<URI>>)Property.UPNP.ICON.class);
    }
    
    public VideoBroadcast setIcon(final URI icon) {
        this.replaceFirstProperty(new Property.UPNP.ICON(icon));
        return this;
    }
    
    public String getRegion() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.REGION.class);
    }
    
    public VideoBroadcast setRegion(final String region) {
        this.replaceFirstProperty(new Property.UPNP.REGION(region));
        return this;
    }
    
    public Integer getChannelNr() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Integer>>)Property.UPNP.CHANNEL_NR.class);
    }
    
    public VideoBroadcast setChannelNr(final Integer channelNr) {
        this.replaceFirstProperty(new Property.UPNP.CHANNEL_NR(channelNr));
        return this;
    }
    
    static {
        CLASS = new Class("object.item.videoItem.videoBroadcast");
    }
}
