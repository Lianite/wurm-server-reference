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

public class MusicTrack extends AudioItem
{
    public static final Class CLASS;
    
    public MusicTrack() {
        this.setClazz(MusicTrack.CLASS);
    }
    
    public MusicTrack(final Item other) {
        super(other);
    }
    
    public MusicTrack(final String id, final Container parent, final String title, final String creator, final String album, final String artist, final Res... resource) {
        this(id, parent.getId(), title, creator, album, artist, resource);
    }
    
    public MusicTrack(final String id, final Container parent, final String title, final String creator, final String album, final PersonWithRole artist, final Res... resource) {
        this(id, parent.getId(), title, creator, album, artist, resource);
    }
    
    public MusicTrack(final String id, final String parentID, final String title, final String creator, final String album, final String artist, final Res... resource) {
        this(id, parentID, title, creator, album, (artist == null) ? null : new PersonWithRole(artist), resource);
    }
    
    public MusicTrack(final String id, final String parentID, final String title, final String creator, final String album, final PersonWithRole artist, final Res... resource) {
        super(id, parentID, title, creator, resource);
        this.setClazz(MusicTrack.CLASS);
        if (album != null) {
            this.setAlbum(album);
        }
        if (artist != null) {
            this.addProperty(new Property.UPNP.ARTIST(artist));
        }
    }
    
    public PersonWithRole getFirstArtist() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<PersonWithRole>>)Property.UPNP.ARTIST.class);
    }
    
    public PersonWithRole[] getArtists() {
        final List<PersonWithRole> list = this.getPropertyValues((java.lang.Class<? extends Property<PersonWithRole>>)Property.UPNP.ARTIST.class);
        return list.toArray(new PersonWithRole[list.size()]);
    }
    
    public MusicTrack setArtists(final PersonWithRole[] artists) {
        this.removeProperties(Property.UPNP.ARTIST.class);
        for (final PersonWithRole artist : artists) {
            this.addProperty(new Property.UPNP.ARTIST(artist));
        }
        return this;
    }
    
    public String getAlbum() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.ALBUM.class);
    }
    
    public MusicTrack setAlbum(final String album) {
        this.replaceFirstProperty(new Property.UPNP.ALBUM(album));
        return this;
    }
    
    public Integer getOriginalTrackNumber() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Integer>>)Property.UPNP.ORIGINAL_TRACK_NUMBER.class);
    }
    
    public MusicTrack setOriginalTrackNumber(final Integer number) {
        this.replaceFirstProperty(new Property.UPNP.ORIGINAL_TRACK_NUMBER(number));
        return this;
    }
    
    public String getFirstPlaylist() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.PLAYLIST.class);
    }
    
    public String[] getPlaylists() {
        final List<String> list = this.getPropertyValues((java.lang.Class<? extends Property<String>>)Property.UPNP.PLAYLIST.class);
        return list.toArray(new String[list.size()]);
    }
    
    public MusicTrack setPlaylists(final String[] playlists) {
        this.removeProperties(Property.UPNP.PLAYLIST.class);
        for (final String s : playlists) {
            this.addProperty(new Property.UPNP.PLAYLIST(s));
        }
        return this;
    }
    
    public StorageMedium getStorageMedium() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<StorageMedium>>)Property.UPNP.STORAGE_MEDIUM.class);
    }
    
    public MusicTrack setStorageMedium(final StorageMedium storageMedium) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }
    
    public Person getFirstContributor() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Person>>)Property.DC.CONTRIBUTOR.class);
    }
    
    public Person[] getContributors() {
        final List<Person> list = this.getPropertyValues((java.lang.Class<? extends Property<Person>>)Property.DC.CONTRIBUTOR.class);
        return list.toArray(new Person[list.size()]);
    }
    
    public MusicTrack setContributors(final Person[] contributors) {
        this.removeProperties(Property.DC.CONTRIBUTOR.class);
        for (final Person p : contributors) {
            this.addProperty(new Property.DC.CONTRIBUTOR(p));
        }
        return this;
    }
    
    public String getDate() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.DATE.class);
    }
    
    public MusicTrack setDate(final String date) {
        this.replaceFirstProperty(new Property.DC.DATE(date));
        return this;
    }
    
    static {
        CLASS = new Class("object.item.audioItem.musicTrack");
    }
}
