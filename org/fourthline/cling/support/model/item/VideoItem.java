// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.item;

import java.net.URI;
import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.Person;
import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.DIDLObject;

public class VideoItem extends Item
{
    public static final Class CLASS;
    
    public VideoItem() {
        this.setClazz(VideoItem.CLASS);
    }
    
    public VideoItem(final Item other) {
        super(other);
    }
    
    public VideoItem(final String id, final Container parent, final String title, final String creator, final Res... resource) {
        this(id, parent.getId(), title, creator, resource);
    }
    
    public VideoItem(final String id, final String parentID, final String title, final String creator, final Res... resource) {
        super(id, parentID, title, creator, VideoItem.CLASS);
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
    
    public VideoItem setGenres(final String[] genres) {
        this.removeProperties(Property.UPNP.GENRE.class);
        for (final String genre : genres) {
            this.addProperty(new Property.UPNP.GENRE(genre));
        }
        return this;
    }
    
    public String getDescription() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.DESCRIPTION.class);
    }
    
    public VideoItem setDescription(final String description) {
        this.replaceFirstProperty(new Property.DC.DESCRIPTION(description));
        return this;
    }
    
    public String getLongDescription() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.LONG_DESCRIPTION.class);
    }
    
    public VideoItem setLongDescription(final String description) {
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
    
    public VideoItem setProducers(final Person[] persons) {
        this.removeProperties(Property.UPNP.PRODUCER.class);
        for (final Person p : persons) {
            this.addProperty(new Property.UPNP.PRODUCER(p));
        }
        return this;
    }
    
    public String getRating() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.RATING.class);
    }
    
    public VideoItem setRating(final String rating) {
        this.replaceFirstProperty(new Property.UPNP.RATING(rating));
        return this;
    }
    
    public PersonWithRole getFirstActor() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<PersonWithRole>>)Property.UPNP.ACTOR.class);
    }
    
    public PersonWithRole[] getActors() {
        final List<PersonWithRole> list = this.getPropertyValues((java.lang.Class<? extends Property<PersonWithRole>>)Property.UPNP.ACTOR.class);
        return list.toArray(new PersonWithRole[list.size()]);
    }
    
    public VideoItem setActors(final PersonWithRole[] persons) {
        this.removeProperties(Property.UPNP.ACTOR.class);
        for (final PersonWithRole p : persons) {
            this.addProperty(new Property.UPNP.ACTOR(p));
        }
        return this;
    }
    
    public Person getFirstDirector() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Person>>)Property.UPNP.DIRECTOR.class);
    }
    
    public Person[] getDirectors() {
        final List<Person> list = this.getPropertyValues((java.lang.Class<? extends Property<Person>>)Property.UPNP.DIRECTOR.class);
        return list.toArray(new Person[list.size()]);
    }
    
    public VideoItem setDirectors(final Person[] persons) {
        this.removeProperties(Property.UPNP.DIRECTOR.class);
        for (final Person p : persons) {
            this.addProperty(new Property.UPNP.DIRECTOR(p));
        }
        return this;
    }
    
    public Person getFirstPublisher() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Person>>)Property.DC.PUBLISHER.class);
    }
    
    public Person[] getPublishers() {
        final List<Person> list = this.getPropertyValues((java.lang.Class<? extends Property<Person>>)Property.DC.PUBLISHER.class);
        return list.toArray(new Person[list.size()]);
    }
    
    public VideoItem setPublishers(final Person[] publishers) {
        this.removeProperties(Property.DC.PUBLISHER.class);
        for (final Person publisher : publishers) {
            this.addProperty(new Property.DC.PUBLISHER(publisher));
        }
        return this;
    }
    
    public String getLanguage() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.LANGUAGE.class);
    }
    
    public VideoItem setLanguage(final String language) {
        this.replaceFirstProperty(new Property.DC.LANGUAGE(language));
        return this;
    }
    
    public URI getFirstRelation() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<URI>>)Property.DC.RELATION.class);
    }
    
    public URI[] getRelations() {
        final List<URI> list = this.getPropertyValues((java.lang.Class<? extends Property<URI>>)Property.DC.RELATION.class);
        return list.toArray(new URI[list.size()]);
    }
    
    public VideoItem setRelations(final URI[] relations) {
        this.removeProperties(Property.DC.RELATION.class);
        for (final URI relation : relations) {
            this.addProperty(new Property.DC.RELATION(relation));
        }
        return this;
    }
    
    static {
        CLASS = new Class("object.item.videoItem");
    }
}
