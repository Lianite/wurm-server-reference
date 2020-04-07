// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.registry;

import org.fourthline.cling.model.ExpirationDetails;

class RegistryItem<K, I>
{
    private K key;
    private I item;
    private ExpirationDetails expirationDetails;
    
    RegistryItem(final K key) {
        this.expirationDetails = new ExpirationDetails();
        this.key = key;
    }
    
    RegistryItem(final K key, final I item, final int maxAgeSeconds) {
        this.expirationDetails = new ExpirationDetails();
        this.key = key;
        this.item = item;
        this.expirationDetails = new ExpirationDetails(maxAgeSeconds);
    }
    
    public K getKey() {
        return this.key;
    }
    
    public I getItem() {
        return this.item;
    }
    
    public ExpirationDetails getExpirationDetails() {
        return this.expirationDetails;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final RegistryItem that = (RegistryItem)o;
        return this.key.equals(that.key);
    }
    
    @Override
    public int hashCode() {
        return this.key.hashCode();
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") " + this.getExpirationDetails() + " KEY: " + this.getKey() + " ITEM: " + this.getItem();
    }
}
