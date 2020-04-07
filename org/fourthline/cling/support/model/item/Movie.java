// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.item;

import java.util.List;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.DIDLObject;

public class Movie extends VideoItem
{
    public static final Class CLASS;
    
    public Movie() {
        this.setClazz(Movie.CLASS);
    }
    
    public Movie(final Item other) {
        super(other);
    }
    
    public Movie(final String id, final Container parent, final String title, final String creator, final Res... resource) {
        this(id, parent.getId(), title, creator, resource);
    }
    
    public Movie(final String id, final String parentID, final String title, final String creator, final Res... resource) {
        super(id, parentID, title, creator, resource);
        this.setClazz(Movie.CLASS);
    }
    
    public StorageMedium getStorageMedium() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<StorageMedium>>)Property.UPNP.STORAGE_MEDIUM.class);
    }
    
    public Movie setStorageMedium(final StorageMedium storageMedium) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }
    
    public Integer getDVDRegionCode() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Integer>>)Property.UPNP.DVD_REGION_CODE.class);
    }
    
    public Movie setDVDRegionCode(final Integer DVDRegionCode) {
        this.replaceFirstProperty(new Property.UPNP.DVD_REGION_CODE(DVDRegionCode));
        return this;
    }
    
    public String getChannelName() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.CHANNEL_NAME.class);
    }
    
    public Movie setChannelName(final String channelName) {
        this.replaceFirstProperty(new Property.UPNP.CHANNEL_NAME(channelName));
        return this;
    }
    
    public String getFirstScheduledStartTime() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.SCHEDULED_START_TIME.class);
    }
    
    public String[] getScheduledStartTimes() {
        final List<String> list = this.getPropertyValues((java.lang.Class<? extends Property<String>>)Property.UPNP.SCHEDULED_START_TIME.class);
        return list.toArray(new String[list.size()]);
    }
    
    public Movie setScheduledStartTimes(final String[] strings) {
        this.removeProperties(Property.UPNP.SCHEDULED_START_TIME.class);
        for (final String s : strings) {
            this.addProperty(new Property.UPNP.SCHEDULED_START_TIME(s));
        }
        return this;
    }
    
    public String getFirstScheduledEndTime() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.SCHEDULED_END_TIME.class);
    }
    
    public String[] getScheduledEndTimes() {
        final List<String> list = this.getPropertyValues((java.lang.Class<? extends Property<String>>)Property.UPNP.SCHEDULED_END_TIME.class);
        return list.toArray(new String[list.size()]);
    }
    
    public Movie setScheduledEndTimes(final String[] strings) {
        this.removeProperties(Property.UPNP.SCHEDULED_END_TIME.class);
        for (final String s : strings) {
            this.addProperty(new Property.UPNP.SCHEDULED_END_TIME(s));
        }
        return this;
    }
    
    static {
        CLASS = new Class("object.item.videoItem.movie");
    }
}
