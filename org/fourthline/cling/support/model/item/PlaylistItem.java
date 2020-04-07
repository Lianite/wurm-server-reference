// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.item;

import org.fourthline.cling.support.model.StorageMedium;
import java.util.List;
import org.fourthline.cling.support.model.PersonWithRole;
import java.util.Collection;
import java.util.Arrays;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.DIDLObject;

public class PlaylistItem extends Item
{
    public static final Class CLASS;
    
    public PlaylistItem() {
        this.setClazz(PlaylistItem.CLASS);
    }
    
    public PlaylistItem(final Item other) {
        super(other);
    }
    
    public PlaylistItem(final String id, final Container parent, final String title, final String creator, final Res... resource) {
        this(id, parent.getId(), title, creator, resource);
    }
    
    public PlaylistItem(final String id, final String parentID, final String title, final String creator, final Res... resource) {
        super(id, parentID, title, creator, PlaylistItem.CLASS);
        if (resource != null) {
            this.getResources().addAll(Arrays.asList(resource));
        }
    }
    
    public PersonWithRole getFirstArtist() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<PersonWithRole>>)Property.UPNP.ARTIST.class);
    }
    
    public PersonWithRole[] getArtists() {
        final List<PersonWithRole> list = this.getPropertyValues((java.lang.Class<? extends Property<PersonWithRole>>)Property.UPNP.ARTIST.class);
        return list.toArray(new PersonWithRole[list.size()]);
    }
    
    public PlaylistItem setArtists(final PersonWithRole[] artists) {
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
    
    public PlaylistItem setGenres(final String[] genres) {
        this.removeProperties(Property.UPNP.GENRE.class);
        for (final String genre : genres) {
            this.addProperty(new Property.UPNP.GENRE(genre));
        }
        return this;
    }
    
    public String getDescription() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.DESCRIPTION.class);
    }
    
    public PlaylistItem setDescription(final String description) {
        this.replaceFirstProperty(new Property.DC.DESCRIPTION(description));
        return this;
    }
    
    public String getLongDescription() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.LONG_DESCRIPTION.class);
    }
    
    public PlaylistItem setLongDescription(final String description) {
        this.replaceFirstProperty(new Property.UPNP.LONG_DESCRIPTION(description));
        return this;
    }
    
    public String getLanguage() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.LANGUAGE.class);
    }
    
    public PlaylistItem setLanguage(final String language) {
        this.replaceFirstProperty(new Property.DC.LANGUAGE(language));
        return this;
    }
    
    public StorageMedium getStorageMedium() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<StorageMedium>>)Property.UPNP.STORAGE_MEDIUM.class);
    }
    
    public PlaylistItem setStorageMedium(final StorageMedium storageMedium) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }
    
    public String getDate() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.DATE.class);
    }
    
    public PlaylistItem setDate(final String date) {
        this.replaceFirstProperty(new Property.DC.DATE(date));
        return this;
    }
    
    static {
        CLASS = new Class("object.item.playlistItem");
    }
}
