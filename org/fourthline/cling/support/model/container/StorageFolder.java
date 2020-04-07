// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.container;

import org.fourthline.cling.support.model.DIDLObject;

public class StorageFolder extends Container
{
    public static final Class CLASS;
    
    public StorageFolder() {
        this.setClazz(StorageFolder.CLASS);
    }
    
    public StorageFolder(final Container other) {
        super(other);
    }
    
    public StorageFolder(final String id, final Container parent, final String title, final String creator, final Integer childCount, final Long storageUsed) {
        this(id, parent.getId(), title, creator, childCount, storageUsed);
    }
    
    public StorageFolder(final String id, final String parentID, final String title, final String creator, final Integer childCount, final Long storageUsed) {
        super(id, parentID, title, creator, StorageFolder.CLASS, childCount);
        if (storageUsed != null) {
            this.setStorageUsed(storageUsed);
        }
    }
    
    public Long getStorageUsed() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<Long>>)Property.UPNP.STORAGE_USED.class);
    }
    
    public StorageFolder setStorageUsed(final Long l) {
        this.replaceFirstProperty(new Property.UPNP.STORAGE_USED(l));
        return this;
    }
    
    static {
        CLASS = new Class("object.container.storageFolder");
    }
}
