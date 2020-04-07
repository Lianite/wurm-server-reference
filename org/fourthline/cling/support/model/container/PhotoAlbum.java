// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.container;

import java.util.Iterator;
import org.fourthline.cling.support.model.item.Item;
import java.util.List;
import org.fourthline.cling.support.model.item.Photo;
import java.util.ArrayList;
import org.fourthline.cling.support.model.DIDLObject;

public class PhotoAlbum extends Album
{
    public static final Class CLASS;
    
    public PhotoAlbum() {
        this.setClazz(PhotoAlbum.CLASS);
    }
    
    public PhotoAlbum(final Container other) {
        super(other);
    }
    
    public PhotoAlbum(final String id, final Container parent, final String title, final String creator, final Integer childCount) {
        this(id, parent.getId(), title, creator, childCount, new ArrayList<Photo>());
    }
    
    public PhotoAlbum(final String id, final Container parent, final String title, final String creator, final Integer childCount, final List<Photo> photos) {
        this(id, parent.getId(), title, creator, childCount, photos);
    }
    
    public PhotoAlbum(final String id, final String parentID, final String title, final String creator, final Integer childCount) {
        this(id, parentID, title, creator, childCount, new ArrayList<Photo>());
    }
    
    public PhotoAlbum(final String id, final String parentID, final String title, final String creator, final Integer childCount, final List<Photo> photos) {
        super(id, parentID, title, creator, childCount);
        this.setClazz(PhotoAlbum.CLASS);
        this.addPhotos(photos);
    }
    
    public Photo[] getPhotos() {
        final List<Photo> list = new ArrayList<Photo>();
        for (final Item item : this.getItems()) {
            if (item instanceof Photo) {
                list.add((Photo)item);
            }
        }
        return list.toArray(new Photo[list.size()]);
    }
    
    public void addPhotos(final List<Photo> photos) {
        this.addPhotos(photos.toArray(new Photo[photos.size()]));
    }
    
    public void addPhotos(final Photo[] photos) {
        if (photos != null) {
            for (final Photo photo : photos) {
                photo.setAlbum(this.getTitle());
                this.addItem(photo);
            }
        }
    }
    
    static {
        CLASS = new Class("object.container.album.photoAlbum");
    }
}
