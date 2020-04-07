// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.item;

import java.util.List;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.Person;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.DIDLObject;

public class AudioBook extends AudioItem
{
    public static final Class CLASS;
    
    public AudioBook() {
        this.setClazz(AudioBook.CLASS);
    }
    
    public AudioBook(final Item other) {
        super(other);
    }
    
    public AudioBook(final String id, final Container parent, final String title, final String creator, final Res... resource) {
        this(id, parent.getId(), title, creator, null, null, (String)null, resource);
    }
    
    public AudioBook(final String id, final Container parent, final String title, final String creator, final String producer, final String contributor, final String date, final Res... resource) {
        this(id, parent.getId(), title, creator, new Person(producer), new Person(contributor), date, resource);
    }
    
    public AudioBook(final String id, final String parentID, final String title, final String creator, final Person producer, final Person contributor, final String date, final Res... resource) {
        super(id, parentID, title, creator, resource);
        this.setClazz(AudioBook.CLASS);
        if (producer != null) {
            this.addProperty(new Property.UPNP.PRODUCER(producer));
        }
        if (contributor != null) {
            this.addProperty(new Property.DC.CONTRIBUTOR(contributor));
        }
        if (date != null) {
            this.setDate(date);
        }
    }
    
    public StorageMedium getStorageMedium() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<StorageMedium>>)Property.UPNP.STORAGE_MEDIUM.class);
    }
    
    public AudioBook setStorageMedium(final StorageMedium storageMedium) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }
    
    public Person getFirstProducer() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Person>>)Property.UPNP.PRODUCER.class);
    }
    
    public Person[] getProducers() {
        final List<Person> list = this.getPropertyValues((java.lang.Class<? extends Property<Person>>)Property.UPNP.PRODUCER.class);
        return list.toArray(new Person[list.size()]);
    }
    
    public AudioBook setProducers(final Person[] persons) {
        this.removeProperties(Property.UPNP.PRODUCER.class);
        for (final Person p : persons) {
            this.addProperty(new Property.UPNP.PRODUCER(p));
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
    
    public AudioBook setContributors(final Person[] contributors) {
        this.removeProperties(Property.DC.CONTRIBUTOR.class);
        for (final Person p : contributors) {
            this.addProperty(new Property.DC.CONTRIBUTOR(p));
        }
        return this;
    }
    
    public String getDate() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.DATE.class);
    }
    
    public AudioBook setDate(final String date) {
        this.replaceFirstProperty(new Property.DC.DATE(date));
        return this;
    }
    
    static {
        CLASS = new Class("object.item.audioItem.audioBook");
    }
}
