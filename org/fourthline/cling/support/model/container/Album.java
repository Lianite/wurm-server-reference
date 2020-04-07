// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.container;

import org.fourthline.cling.support.model.Person;
import java.util.List;
import java.net.URI;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.DIDLObject;

public class Album extends Container
{
    public static final Class CLASS;
    
    public Album() {
        this.setClazz(Album.CLASS);
    }
    
    public Album(final Container other) {
        super(other);
    }
    
    public Album(final String id, final Container parent, final String title, final String creator, final Integer childCount) {
        this(id, parent.getId(), title, creator, childCount);
    }
    
    public Album(final String id, final String parentID, final String title, final String creator, final Integer childCount) {
        super(id, parentID, title, creator, Album.CLASS, childCount);
    }
    
    public String getDescription() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.DESCRIPTION.class);
    }
    
    public Album setDescription(final String description) {
        this.replaceFirstProperty(new Property.DC.DESCRIPTION(description));
        return this;
    }
    
    public String getLongDescription() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.LONG_DESCRIPTION.class);
    }
    
    public Album setLongDescription(final String description) {
        this.replaceFirstProperty(new Property.UPNP.LONG_DESCRIPTION(description));
        return this;
    }
    
    public StorageMedium getStorageMedium() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<StorageMedium>>)Property.UPNP.STORAGE_MEDIUM.class);
    }
    
    public Album setStorageMedium(final StorageMedium storageMedium) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }
    
    public String getDate() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.DATE.class);
    }
    
    public Album setDate(final String date) {
        this.replaceFirstProperty(new Property.DC.DATE(date));
        return this;
    }
    
    public URI getFirstRelation() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<URI>>)Property.DC.RELATION.class);
    }
    
    public URI[] getRelations() {
        final List<URI> list = this.getPropertyValues((java.lang.Class<? extends Property<URI>>)Property.DC.RELATION.class);
        return list.toArray(new URI[list.size()]);
    }
    
    public Album setRelations(final URI[] relations) {
        this.removeProperties(Property.DC.RELATION.class);
        for (final URI relation : relations) {
            this.addProperty(new Property.DC.RELATION(relation));
        }
        return this;
    }
    
    public String getFirstRights() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.RIGHTS.class);
    }
    
    public String[] getRights() {
        final List<String> list = this.getPropertyValues((java.lang.Class<? extends Property<String>>)Property.DC.RIGHTS.class);
        return list.toArray(new String[list.size()]);
    }
    
    public Album setRights(final String[] rights) {
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
    
    public Album setContributors(final Person[] contributors) {
        this.removeProperties(Property.DC.CONTRIBUTOR.class);
        for (final Person p : contributors) {
            this.addProperty(new Property.DC.CONTRIBUTOR(p));
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
    
    public Album setPublishers(final Person[] publishers) {
        this.removeProperties(Property.DC.PUBLISHER.class);
        for (final Person publisher : publishers) {
            this.addProperty(new Property.DC.PUBLISHER(publisher));
        }
        return this;
    }
    
    static {
        CLASS = new Class("object.container.album");
    }
}
