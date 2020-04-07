// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.item;

import java.net.URI;
import org.fourthline.cling.support.model.Person;
import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.DIDLObject;

public class AudioItem extends Item
{
    public static final Class CLASS;
    
    public AudioItem() {
        this.setClazz(AudioItem.CLASS);
    }
    
    public AudioItem(final Item other) {
        super(other);
    }
    
    public AudioItem(final String id, final Container parent, final String title, final String creator, final Res... resource) {
        this(id, parent.getId(), title, creator, resource);
    }
    
    public AudioItem(final String id, final String parentID, final String title, final String creator, final Res... resource) {
        super(id, parentID, title, creator, AudioItem.CLASS);
        if (resource != null) {
            this.getResources().addAll(Arrays.asList(resource));
        }
    }
    
    public String getFirstGenre() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.GENRE.class);
    }
    
    public String[] getGenres() {
        final List<String> list = this.getPropertyValues((java.lang.Class<? extends Property<String>>)Property.UPNP.GENRE.class);
        return list.toArray(new String[list.size()]);
    }
    
    public AudioItem setGenres(final String[] genres) {
        this.removeProperties(Property.UPNP.GENRE.class);
        for (final String genre : genres) {
            this.addProperty(new Property.UPNP.GENRE(genre));
        }
        return this;
    }
    
    public String getDescription() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.DESCRIPTION.class);
    }
    
    public AudioItem setDescription(final String description) {
        this.replaceFirstProperty(new Property.DC.DESCRIPTION(description));
        return this;
    }
    
    public String getLongDescription() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.LONG_DESCRIPTION.class);
    }
    
    public AudioItem setLongDescription(final String description) {
        this.replaceFirstProperty(new Property.UPNP.LONG_DESCRIPTION(description));
        return this;
    }
    
    public Person getFirstPublisher() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Person>>)Property.DC.PUBLISHER.class);
    }
    
    public Person[] getPublishers() {
        final List<Person> list = this.getPropertyValues((java.lang.Class<? extends Property<Person>>)Property.DC.PUBLISHER.class);
        return list.toArray(new Person[list.size()]);
    }
    
    public AudioItem setPublishers(final Person[] publishers) {
        this.removeProperties(Property.DC.PUBLISHER.class);
        for (final Person publisher : publishers) {
            this.addProperty(new Property.DC.PUBLISHER(publisher));
        }
        return this;
    }
    
    public URI getFirstRelation() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<URI>>)Property.DC.RELATION.class);
    }
    
    public URI[] getRelations() {
        final List<URI> list = this.getPropertyValues((java.lang.Class<? extends Property<URI>>)Property.DC.RELATION.class);
        return list.toArray(new URI[list.size()]);
    }
    
    public AudioItem setRelations(final URI[] relations) {
        this.removeProperties(Property.DC.RELATION.class);
        for (final URI relation : relations) {
            this.addProperty(new Property.DC.RELATION(relation));
        }
        return this;
    }
    
    public String getLanguage() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.LANGUAGE.class);
    }
    
    public AudioItem setLanguage(final String language) {
        this.replaceFirstProperty(new Property.DC.LANGUAGE(language));
        return this;
    }
    
    public String getFirstRights() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.RIGHTS.class);
    }
    
    public String[] getRights() {
        final List<String> list = this.getPropertyValues((java.lang.Class<? extends Property<String>>)Property.DC.RIGHTS.class);
        return list.toArray(new String[list.size()]);
    }
    
    public AudioItem setRights(final String[] rights) {
        this.removeProperties(Property.DC.RIGHTS.class);
        for (final String right : rights) {
            this.addProperty(new Property.DC.RIGHTS(right));
        }
        return this;
    }
    
    static {
        CLASS = new Class("object.item.audioItem");
    }
}
