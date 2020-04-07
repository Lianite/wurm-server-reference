// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.item;

import org.fourthline.cling.support.model.Person;
import org.fourthline.cling.support.model.StorageMedium;
import java.util.List;
import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.DIDLObject;

public class MusicVideoClip extends VideoItem
{
    public static final Class CLASS;
    
    public MusicVideoClip() {
        this.setClazz(MusicVideoClip.CLASS);
    }
    
    public MusicVideoClip(final Item other) {
        super(other);
    }
    
    public MusicVideoClip(final String id, final Container parent, final String title, final String creator, final Res... resource) {
        this(id, parent.getId(), title, creator, resource);
    }
    
    public MusicVideoClip(final String id, final String parentID, final String title, final String creator, final Res... resource) {
        super(id, parentID, title, creator, resource);
        this.setClazz(MusicVideoClip.CLASS);
    }
    
    public PersonWithRole getFirstArtist() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<PersonWithRole>>)Property.UPNP.ARTIST.class);
    }
    
    public PersonWithRole[] getArtists() {
        final List<PersonWithRole> list = this.getPropertyValues((java.lang.Class<? extends Property<PersonWithRole>>)Property.UPNP.ARTIST.class);
        return list.toArray(new PersonWithRole[list.size()]);
    }
    
    public MusicVideoClip setArtists(final PersonWithRole[] artists) {
        this.removeProperties(Property.UPNP.ARTIST.class);
        for (final PersonWithRole artist : artists) {
            this.addProperty(new Property.UPNP.ARTIST(artist));
        }
        return this;
    }
    
    public StorageMedium getStorageMedium() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<StorageMedium>>)Property.UPNP.STORAGE_MEDIUM.class);
    }
    
    public MusicVideoClip setStorageMedium(final StorageMedium storageMedium) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }
    
    public String getAlbum() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.ALBUM.class);
    }
    
    public MusicVideoClip setAlbum(final String album) {
        this.replaceFirstProperty(new Property.UPNP.ALBUM(album));
        return this;
    }
    
    public String getFirstScheduledStartTime() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.SCHEDULED_START_TIME.class);
    }
    
    public String[] getScheduledStartTimes() {
        final List<String> list = this.getPropertyValues((java.lang.Class<? extends Property<String>>)Property.UPNP.SCHEDULED_START_TIME.class);
        return list.toArray(new String[list.size()]);
    }
    
    public MusicVideoClip setScheduledStartTimes(final String[] strings) {
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
    
    public MusicVideoClip setScheduledEndTimes(final String[] strings) {
        this.removeProperties(Property.UPNP.SCHEDULED_END_TIME.class);
        for (final String s : strings) {
            this.addProperty(new Property.UPNP.SCHEDULED_END_TIME(s));
        }
        return this;
    }
    
    public Person getFirstContributor() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Person>>)Property.DC.CONTRIBUTOR.class);
    }
    
    public Person[] getContributors() {
        final List<Person> list = this.getPropertyValues((java.lang.Class<? extends Property<Person>>)Property.DC.CONTRIBUTOR.class);
        return list.toArray(new Person[list.size()]);
    }
    
    public MusicVideoClip setContributors(final Person[] contributors) {
        this.removeProperties(Property.DC.CONTRIBUTOR.class);
        for (final Person p : contributors) {
            this.addProperty(new Property.DC.CONTRIBUTOR(p));
        }
        return this;
    }
    
    public String getDate() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.DATE.class);
    }
    
    public MusicVideoClip setDate(final String date) {
        this.replaceFirstProperty(new Property.DC.DATE(date));
        return this;
    }
    
    static {
        CLASS = new Class("object.item.videoItem.musicVideoClip");
    }
}
