// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.container;

import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.Person;
import java.util.List;
import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.DIDLObject;

public class PlaylistContainer extends Container
{
    public static final Class CLASS;
    
    public PlaylistContainer() {
        this.setClazz(PlaylistContainer.CLASS);
    }
    
    public PlaylistContainer(final Container other) {
        super(other);
    }
    
    public PlaylistContainer(final String id, final Container parent, final String title, final String creator, final Integer childCount) {
        this(id, parent.getId(), title, creator, childCount);
    }
    
    public PlaylistContainer(final String id, final String parentID, final String title, final String creator, final Integer childCount) {
        super(id, parentID, title, creator, PlaylistContainer.CLASS, childCount);
    }
    
    public PersonWithRole getFirstArtist() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<PersonWithRole>>)Property.UPNP.ARTIST.class);
    }
    
    public PersonWithRole[] getArtists() {
        final List<PersonWithRole> list = this.getPropertyValues((java.lang.Class<? extends Property<PersonWithRole>>)Property.UPNP.ARTIST.class);
        return list.toArray(new PersonWithRole[list.size()]);
    }
    
    public PlaylistContainer setArtists(final PersonWithRole[] artists) {
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
    
    public PlaylistContainer setGenres(final String[] genres) {
        this.removeProperties(Property.UPNP.GENRE.class);
        for (final String genre : genres) {
            this.addProperty(new Property.UPNP.GENRE(genre));
        }
        return this;
    }
    
    public String getDescription() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.DESCRIPTION.class);
    }
    
    public PlaylistContainer setDescription(final String description) {
        this.replaceFirstProperty(new Property.DC.DESCRIPTION(description));
        return this;
    }
    
    public String getLongDescription() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.LONG_DESCRIPTION.class);
    }
    
    public PlaylistContainer setLongDescription(final String description) {
        this.replaceFirstProperty(new Property.UPNP.LONG_DESCRIPTION(description));
        return this;
    }
    
    public Person getFirstProducer() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Person>>)Property.UPNP.PRODUCER.class);
    }
    
    public Person[] getProducers() {
        final List<Person> list = this.getPropertyValues((java.lang.Class<? extends Property<Person>>)Property.UPNP.PRODUCER.class);
        return list.toArray(new Person[list.size()]);
    }
    
    public PlaylistContainer setProducers(final Person[] persons) {
        this.removeProperties(Property.UPNP.PRODUCER.class);
        for (final Person p : persons) {
            this.addProperty(new Property.UPNP.PRODUCER(p));
        }
        return this;
    }
    
    public StorageMedium getStorageMedium() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<StorageMedium>>)Property.UPNP.STORAGE_MEDIUM.class);
    }
    
    public PlaylistContainer setStorageMedium(final StorageMedium storageMedium) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }
    
    public String getDate() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.DATE.class);
    }
    
    public PlaylistContainer setDate(final String date) {
        this.replaceFirstProperty(new Property.DC.DATE(date));
        return this;
    }
    
    public String getFirstRights() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.RIGHTS.class);
    }
    
    public String[] getRights() {
        final List<String> list = this.getPropertyValues((java.lang.Class<? extends Property<String>>)Property.DC.RIGHTS.class);
        return list.toArray(new String[list.size()]);
    }
    
    public PlaylistContainer setRights(final String[] rights) {
        this.removeProperties(Property.DC.RIGHTS.class);
        for (final String right : rights) {
            this.addProperty(new Property.DC.RIGHTS(right));
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
    
    public PlaylistContainer setContributors(final Person[] contributors) {
        this.removeProperties(Property.DC.CONTRIBUTOR.class);
        for (final Person p : contributors) {
            this.addProperty(new Property.DC.CONTRIBUTOR(p));
        }
        return this;
    }
    
    public String getLanguage() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.LANGUAGE.class);
    }
    
    public PlaylistContainer setLanguage(final String language) {
        this.replaceFirstProperty(new Property.DC.LANGUAGE(language));
        return this;
    }
    
    static {
        CLASS = new Class("object.container.playlistContainer");
    }
}
