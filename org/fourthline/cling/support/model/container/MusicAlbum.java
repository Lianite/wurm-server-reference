// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.container;

import java.util.Iterator;
import org.fourthline.cling.support.model.item.Item;
import java.net.URI;
import org.fourthline.cling.support.model.Person;
import org.fourthline.cling.support.model.PersonWithRole;
import java.util.List;
import org.fourthline.cling.support.model.item.MusicTrack;
import java.util.ArrayList;
import org.fourthline.cling.support.model.DIDLObject;

public class MusicAlbum extends Album
{
    public static final Class CLASS;
    
    public MusicAlbum() {
        this.setClazz(MusicAlbum.CLASS);
    }
    
    public MusicAlbum(final Container other) {
        super(other);
    }
    
    public MusicAlbum(final String id, final Container parent, final String title, final String creator, final Integer childCount) {
        this(id, parent.getId(), title, creator, childCount, new ArrayList<MusicTrack>());
    }
    
    public MusicAlbum(final String id, final Container parent, final String title, final String creator, final Integer childCount, final List<MusicTrack> musicTracks) {
        this(id, parent.getId(), title, creator, childCount, musicTracks);
    }
    
    public MusicAlbum(final String id, final String parentID, final String title, final String creator, final Integer childCount) {
        this(id, parentID, title, creator, childCount, new ArrayList<MusicTrack>());
    }
    
    public MusicAlbum(final String id, final String parentID, final String title, final String creator, final Integer childCount, final List<MusicTrack> musicTracks) {
        super(id, parentID, title, creator, childCount);
        this.setClazz(MusicAlbum.CLASS);
        this.addMusicTracks(musicTracks);
    }
    
    public PersonWithRole getFirstArtist() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<PersonWithRole>>)Property.UPNP.ARTIST.class);
    }
    
    public PersonWithRole[] getArtists() {
        final List<PersonWithRole> list = this.getPropertyValues((java.lang.Class<? extends Property<PersonWithRole>>)Property.UPNP.ARTIST.class);
        return list.toArray(new PersonWithRole[list.size()]);
    }
    
    public MusicAlbum setArtists(final PersonWithRole[] artists) {
        this.removeProperties(Property.UPNP.ARTIST.class);
        for (final PersonWithRole artist : artists) {
            this.addProperty(new Property.UPNP.ARTIST(artist));
        }
        return this;
    }
    
    public String getFirstGenre() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.GENRE.class);
    }
    
    public String[] getGenres() {
        final List<String> list = this.getPropertyValues((java.lang.Class<? extends Property<String>>)Property.UPNP.GENRE.class);
        return list.toArray(new String[list.size()]);
    }
    
    public MusicAlbum setGenres(final String[] genres) {
        this.removeProperties(Property.UPNP.GENRE.class);
        for (final String genre : genres) {
            this.addProperty(new Property.UPNP.GENRE(genre));
        }
        return this;
    }
    
    public Person getFirstProducer() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Person>>)Property.UPNP.PRODUCER.class);
    }
    
    public Person[] getProducers() {
        final List<Person> list = this.getPropertyValues((java.lang.Class<? extends Property<Person>>)Property.UPNP.PRODUCER.class);
        return list.toArray(new Person[list.size()]);
    }
    
    public MusicAlbum setProducers(final Person[] persons) {
        this.removeProperties(Property.UPNP.PRODUCER.class);
        for (final Person p : persons) {
            this.addProperty(new Property.UPNP.PRODUCER(p));
        }
        return this;
    }
    
    public URI getFirstAlbumArtURI() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<URI>>)Property.UPNP.ALBUM_ART_URI.class);
    }
    
    public URI[] getAlbumArtURIs() {
        final List<URI> list = this.getPropertyValues((java.lang.Class<? extends Property<URI>>)Property.UPNP.ALBUM_ART_URI.class);
        return list.toArray(new URI[list.size()]);
    }
    
    public MusicAlbum setAlbumArtURIs(final URI[] uris) {
        this.removeProperties(Property.UPNP.ALBUM_ART_URI.class);
        for (final URI uri : uris) {
            this.addProperty(new Property.UPNP.ALBUM_ART_URI(uri));
        }
        return this;
    }
    
    public String getToc() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.TOC.class);
    }
    
    public MusicAlbum setToc(final String toc) {
        this.replaceFirstProperty(new Property.UPNP.TOC(toc));
        return this;
    }
    
    public MusicTrack[] getMusicTracks() {
        final List<MusicTrack> list = new ArrayList<MusicTrack>();
        for (final Item item : this.getItems()) {
            if (item instanceof MusicTrack) {
                list.add((MusicTrack)item);
            }
        }
        return list.toArray(new MusicTrack[list.size()]);
    }
    
    public void addMusicTracks(final List<MusicTrack> musicTracks) {
        this.addMusicTracks(musicTracks.toArray(new MusicTrack[musicTracks.size()]));
    }
    
    public void addMusicTracks(final MusicTrack[] musicTracks) {
        if (musicTracks != null) {
            for (final MusicTrack musicTrack : musicTracks) {
                musicTrack.setAlbum(this.getTitle());
                this.addItem(musicTrack);
            }
        }
    }
    
    static {
        CLASS = new Class("object.container.album.musicAlbum");
    }
}
