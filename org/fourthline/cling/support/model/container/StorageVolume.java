// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.container;

import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.DIDLObject;

public class StorageVolume extends Container
{
    public static final Class CLASS;
    
    public StorageVolume() {
        this.setClazz(StorageVolume.CLASS);
    }
    
    public StorageVolume(final Container other) {
        super(other);
    }
    
    public StorageVolume(final String id, final Container parent, final String title, final String creator, final Integer childCount, final Long storageTotal, final Long storageUsed, final Long storageFree, final StorageMedium storageMedium) {
        this(id, parent.getId(), title, creator, childCount, storageTotal, storageUsed, storageFree, storageMedium);
    }
    
    public StorageVolume(final String id, final String parentID, final String title, final String creator, final Integer childCount, final Long storageTotal, final Long storageUsed, final Long storageFree, final StorageMedium storageMedium) {
        super(id, parentID, title, creator, StorageVolume.CLASS, childCount);
        if (storageTotal != null) {
            this.setStorageTotal(storageTotal);
        }
        if (storageUsed != null) {
            this.setStorageUsed(storageUsed);
        }
        if (storageFree != null) {
            this.setStorageFree(storageFree);
        }
        if (storageMedium != null) {
            this.setStorageMedium(storageMedium);
        }
    }
    
    public Long getStorageTotal() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Long>>)Property.UPNP.STORAGE_TOTAL.class);
    }
    
    public StorageVolume setStorageTotal(final Long l) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_TOTAL(l));
        return this;
    }
    
    public Long getStorageUsed() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Long>>)Property.UPNP.STORAGE_USED.class);
    }
    
    public StorageVolume setStorageUsed(final Long l) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_USED(l));
        return this;
    }
    
    public Long getStorageFree() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Long>>)Property.UPNP.STORAGE_FREE.class);
    }
    
    public StorageVolume setStorageFree(final Long l) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_FREE(l));
        return this;
    }
    
    public StorageMedium getStorageMedium() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<StorageMedium>>)Property.UPNP.STORAGE_MEDIUM.class);
    }
    
    public StorageVolume setStorageMedium(final StorageMedium storageMedium) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }
    
    static {
        CLASS = new Class("object.container.storageVolume");
    }
}
