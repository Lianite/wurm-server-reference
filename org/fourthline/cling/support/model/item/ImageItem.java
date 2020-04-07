// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.item;

import org.fourthline.cling.support.model.StorageMedium;
import java.util.List;
import org.fourthline.cling.support.model.Person;
import java.util.Collection;
import java.util.Arrays;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.DIDLObject;

public class ImageItem extends Item
{
    public static final Class CLASS;
    
    public ImageItem() {
        this.setClazz(ImageItem.CLASS);
    }
    
    public ImageItem(final Item other) {
        super(other);
    }
    
    public ImageItem(final String id, final Container parent, final String title, final String creator, final Res... resource) {
        this(id, parent.getId(), title, creator, resource);
    }
    
    public ImageItem(final String id, final String parentID, final String title, final String creator, final Res... resource) {
        super(id, parentID, title, creator, ImageItem.CLASS);
        if (resource != null) {
            this.getResources().addAll(Arrays.asList(resource));
        }
    }
    
    public String getDescription() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.DESCRIPTION.class);
    }
    
    public ImageItem setDescription(final String description) {
        this.replaceFirstProperty(new Property.DC.DESCRIPTION(description));
        return this;
    }
    
    public String getLongDescription() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.LONG_DESCRIPTION.class);
    }
    
    public ImageItem setLongDescription(final String description) {
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
    
    public ImageItem setPublishers(final Person[] publishers) {
        this.removeProperties(Property.DC.PUBLISHER.class);
        for (final Person publisher : publishers) {
            this.addProperty(new Property.DC.PUBLISHER(publisher));
        }
        return this;
    }
    
    public StorageMedium getStorageMedium() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<StorageMedium>>)Property.UPNP.STORAGE_MEDIUM.class);
    }
    
    public ImageItem setStorageMedium(final StorageMedium storageMedium) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }
    
    public String getRating() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.RATING.class);
    }
    
    public ImageItem setRating(final String rating) {
        this.replaceFirstProperty(new Property.UPNP.RATING(rating));
        return this;
    }
    
    public String getDate() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.DATE.class);
    }
    
    public ImageItem setDate(final String date) {
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
    
    public ImageItem setRights(final String[] rights) {
        this.removeProperties(Property.DC.RIGHTS.class);
        for (final String right : rights) {
            this.addProperty(new Property.DC.RIGHTS(right));
        }
        return this;
    }
    
    static {
        CLASS = new Class("object.item.imageItem");
    }
}
