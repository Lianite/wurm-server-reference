// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.container;

import java.net.URI;
import java.util.List;
import org.fourthline.cling.support.model.DIDLObject;

public class MusicArtist extends PersonContainer
{
    public static final Class CLASS;
    
    public MusicArtist() {
        this.setClazz(MusicArtist.CLASS);
    }
    
    public MusicArtist(final Container other) {
        super(other);
    }
    
    public MusicArtist(final String id, final Container parent, final String title, final String creator, final Integer childCount) {
        this(id, parent.getId(), title, creator, childCount);
    }
    
    public MusicArtist(final String id, final String parentID, final String title, final String creator, final Integer childCount) {
        super(id, parentID, title, creator, childCount);
        this.setClazz(MusicArtist.CLASS);
    }
    
    public String getFirstGenre() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.GENRE.class);
    }
    
    public String[] getGenres() {
        final List<String> list = this.getPropertyValues((java.lang.Class<? extends Property<String>>)Property.UPNP.GENRE.class);
        return list.toArray(new String[list.size()]);
    }
    
    public MusicArtist setGenres(final String[] genres) {
        this.removeProperties(Property.UPNP.GENRE.class);
        for (final String genre : genres) {
            this.addProperty(new Property.UPNP.GENRE(genre));
        }
        return this;
    }
    
    public URI getArtistDiscographyURI() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<URI>>)Property.UPNP.ARTIST_DISCO_URI.class);
    }
    
    public MusicArtist setArtistDiscographyURI(final URI uri) {
        this.replaceFirstProperty(new Property.UPNP.ARTIST_DISCO_URI(uri));
        return this;
    }
    
    static {
        CLASS = new Class("object.container.person.musicArtist");
    }
}
