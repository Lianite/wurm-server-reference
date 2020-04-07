// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.item;

import org.fourthline.cling.support.model.Person;
import java.net.URI;
import org.fourthline.cling.support.model.StorageMedium;
import java.util.List;
import org.fourthline.cling.support.model.PersonWithRole;
import java.util.Collection;
import java.util.Arrays;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.DIDLObject;

public class TextItem extends Item
{
    public static final Class CLASS;
    
    public TextItem() {
        this.setClazz(TextItem.CLASS);
    }
    
    public TextItem(final Item other) {
        super(other);
    }
    
    public TextItem(final String id, final Container parent, final String title, final String creator, final Res... resource) {
        this(id, parent.getId(), title, creator, resource);
    }
    
    public TextItem(final String id, final String parentID, final String title, final String creator, final Res... resource) {
        super(id, parentID, title, creator, TextItem.CLASS);
        if (resource != null) {
            this.getResources().addAll(Arrays.asList(resource));
        }
    }
    
    public PersonWithRole getFirstAuthor() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<PersonWithRole>>)Property.UPNP.AUTHOR.class);
    }
    
    public PersonWithRole[] getAuthors() {
        final List<PersonWithRole> list = this.getPropertyValues((java.lang.Class<? extends Property<PersonWithRole>>)Property.UPNP.AUTHOR.class);
        return list.toArray(new PersonWithRole[list.size()]);
    }
    
    public TextItem setAuthors(final PersonWithRole[] persons) {
        this.removeProperties(Property.UPNP.AUTHOR.class);
        for (final PersonWithRole p : persons) {
            this.addProperty(new Property.UPNP.AUTHOR(p));
        }
        return this;
    }
    
    public String getDescription() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.DESCRIPTION.class);
    }
    
    public TextItem setDescription(final String description) {
        this.replaceFirstProperty(new Property.DC.DESCRIPTION(description));
        return this;
    }
    
    public String getLongDescription() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.LONG_DESCRIPTION.class);
    }
    
    public TextItem setLongDescription(final String description) {
        this.replaceFirstProperty(new Property.UPNP.LONG_DESCRIPTION(description));
        return this;
    }
    
    public String getLanguage() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.LANGUAGE.class);
    }
    
    public TextItem setLanguage(final String language) {
        this.replaceFirstProperty(new Property.DC.LANGUAGE(language));
        return this;
    }
    
    public StorageMedium getStorageMedium() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<StorageMedium>>)Property.UPNP.STORAGE_MEDIUM.class);
    }
    
    public TextItem setStorageMedium(final StorageMedium storageMedium) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }
    
    public String getDate() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.DATE.class);
    }
    
    public TextItem setDate(final String date) {
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
    
    public TextItem setRelations(final URI[] relations) {
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
    
    public TextItem setRights(final String[] rights) {
        this.removeProperties(Property.DC.RIGHTS.class);
        for (final String right : rights) {
            this.addProperty(new Property.DC.RIGHTS(right));
        }
        return this;
    }
    
    public String getRating() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.RATING.class);
    }
    
    public TextItem setRating(final String rating) {
        this.replaceFirstProperty(new Property.UPNP.RATING(rating));
        return this;
    }
    
    public Person getFirstContributor() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Person>>)Property.DC.CONTRIBUTOR.class);
    }
    
    public Person[] getContributors() {
        final List<Person> list = this.getPropertyValues((java.lang.Class<? extends Property<Person>>)Property.DC.CONTRIBUTOR.class);
        return list.toArray(new Person[list.size()]);
    }
    
    public TextItem setContributors(final Person[] contributors) {
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
    
    public TextItem setPublishers(final Person[] publishers) {
        this.removeProperties(Property.DC.PUBLISHER.class);
        for (final Person publisher : publishers) {
            this.addProperty(new Property.DC.PUBLISHER(publisher));
        }
        return this;
    }
    
    static {
        CLASS = new Class("object.item.textItem");
    }
}
