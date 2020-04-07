// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.item;

import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.DIDLObject;

public class Photo extends ImageItem
{
    public static final Class CLASS;
    
    public Photo() {
        this.setClazz(Photo.CLASS);
    }
    
    public Photo(final Item other) {
        super(other);
    }
    
    public Photo(final String id, final Container parent, final String title, final String creator, final String album, final Res... resource) {
        this(id, parent.getId(), title, creator, album, resource);
    }
    
    public Photo(final String id, final String parentID, final String title, final String creator, final String album, final Res... resource) {
        super(id, parentID, title, creator, resource);
        this.setClazz(Photo.CLASS);
        if (album != null) {
            this.setAlbum(album);
        }
    }
    
    public String getAlbum() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.UPNP.ALBUM.class);
    }
    
    public Photo setAlbum(final String album) {
        this.replaceFirstProperty(new Property.UPNP.ALBUM(album));
        return this;
    }
    
    static {
        CLASS = new Class("object.item.imageItem.photo");
    }
}
