// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.container;

import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.DIDLObject;

public class StorageSystem extends Container
{
    public static final Class CLASS;
    
    public StorageSystem() {
        this.setClazz(StorageSystem.CLASS);
    }
    
    public StorageSystem(final Container other) {
        super(other);
    }
    
    public StorageSystem(final String id, final Container parent, final String title, final String creator, final Integer childCount, final Long storageTotal, final Long storageUsed, final Long storageFree, final Long storageMaxPartition, final StorageMedium storageMedium) {
        this(id, parent.getId(), title, creator, childCount, storageTotal, storageUsed, storageFree, storageMaxPartition, storageMedium);
    }
    
    public StorageSystem(final String id, final String parentID, final String title, final String creator, final Integer childCount, final Long storageTotal, final Long storageUsed, final Long storageFree, final Long storageMaxPartition, final StorageMedium storageMedium) {
        super(id, parentID, title, creator, StorageSystem.CLASS, childCount);
        if (storageTotal != null) {
            this.setStorageTotal(storageTotal);
        }
        if (storageUsed != null) {
            this.setStorageUsed(storageUsed);
        }
        if (storageFree != null) {
            this.setStorageFree(storageFree);
        }
        if (storageMaxPartition != null) {
            this.setStorageMaxPartition(storageMaxPartition);
        }
        if (storageMedium != null) {
            this.setStorageMedium(storageMedium);
        }
    }
    
    public Long getStorageTotal() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Long>>)Property.UPNP.STORAGE_TOTAL.class);
    }
    
    public StorageSystem setStorageTotal(final Long l) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_TOTAL(l));
        return this;
    }
    
    public Long getStorageUsed() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Long>>)Property.UPNP.STORAGE_USED.class);
    }
    
    public StorageSystem setStorageUsed(final Long l) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_USED(l));
        return this;
    }
    
    public Long getStorageFree() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Long>>)Property.UPNP.STORAGE_FREE.class);
    }
    
    public StorageSystem setStorageFree(final Long l) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_FREE(l));
        return this;
    }
    
    public Long getStorageMaxPartition() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Long>>)Property.UPNP.STORAGE_MAX_PARTITION.class);
    }
    
    public StorageSystem setStorageMaxPartition(final Long l) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_MAX_PARTITION(l));
        return this;
    }
    
    public StorageMedium getStorageMedium() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<StorageMedium>>)Property.UPNP.STORAGE_MEDIUM.class);
    }
    
    public StorageSystem setStorageMedium(final StorageMedium storageMedium) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }
    
    static {
        CLASS = new Class("object.container.storageSystem");
    }
}
