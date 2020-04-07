// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model;

import org.fourthline.cling.support.model.container.StorageFolder;
import org.fourthline.cling.support.model.container.StorageVolume;
import org.fourthline.cling.support.model.container.StorageSystem;
import org.fourthline.cling.support.model.container.MusicArtist;
import org.fourthline.cling.support.model.container.PersonContainer;
import org.fourthline.cling.support.model.container.PlaylistContainer;
import org.fourthline.cling.support.model.container.MovieGenre;
import org.fourthline.cling.support.model.container.MusicGenre;
import org.fourthline.cling.support.model.container.GenreContainer;
import org.fourthline.cling.support.model.container.PhotoAlbum;
import org.fourthline.cling.support.model.container.MusicAlbum;
import org.fourthline.cling.support.model.container.Album;
import java.util.Iterator;
import org.fourthline.cling.support.model.item.TextItem;
import org.fourthline.cling.support.model.item.PlaylistItem;
import org.fourthline.cling.support.model.item.Photo;
import org.fourthline.cling.support.model.item.ImageItem;
import org.fourthline.cling.support.model.item.MusicVideoClip;
import org.fourthline.cling.support.model.item.VideoBroadcast;
import org.fourthline.cling.support.model.item.Movie;
import org.fourthline.cling.support.model.item.VideoItem;
import org.fourthline.cling.support.model.item.AudioBroadcast;
import org.fourthline.cling.support.model.item.AudioBook;
import org.fourthline.cling.support.model.item.MusicTrack;
import org.fourthline.cling.support.model.item.AudioItem;
import java.util.ArrayList;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.container.Container;
import java.util.List;

public class DIDLContent
{
    public static final String NAMESPACE_URI = "urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/";
    public static final String DESC_WRAPPER_NAMESPACE_URI = "urn:fourthline-org:cling:support:content-directory-desc-1-0";
    protected List<Container> containers;
    protected List<Item> items;
    protected List<DescMeta> descMetadata;
    
    public DIDLContent() {
        this.containers = new ArrayList<Container>();
        this.items = new ArrayList<Item>();
        this.descMetadata = new ArrayList<DescMeta>();
    }
    
    public Container getFirstContainer() {
        return this.getContainers().get(0);
    }
    
    public DIDLContent addContainer(final Container container) {
        this.getContainers().add(container);
        return this;
    }
    
    public List<Container> getContainers() {
        return this.containers;
    }
    
    public void setContainers(final List<Container> containers) {
        this.containers = containers;
    }
    
    public DIDLContent addObject(final Object object) {
        if (object instanceof Item) {
            this.addItem((Item)object);
        }
        else if (object instanceof Container) {
            this.addContainer((Container)object);
        }
        return this;
    }
    
    public DIDLContent addItem(final Item item) {
        this.getItems().add(item);
        return this;
    }
    
    public List<Item> getItems() {
        return this.items;
    }
    
    public void setItems(final List<Item> items) {
        this.items = items;
    }
    
    public DIDLContent addDescMetadata(final DescMeta descMetadata) {
        this.getDescMetadata().add(descMetadata);
        return this;
    }
    
    public List<DescMeta> getDescMetadata() {
        return this.descMetadata;
    }
    
    public void setDescMetadata(final List<DescMeta> descMetadata) {
        this.descMetadata = descMetadata;
    }
    
    public void replaceGenericContainerAndItems() {
        this.setItems(this.replaceGenericItems(this.getItems()));
        this.setContainers(this.replaceGenericContainers(this.getContainers()));
    }
    
    protected List<Item> replaceGenericItems(final List<Item> genericItems) {
        final List<Item> specificItems = new ArrayList<Item>();
        for (final Item genericItem : genericItems) {
            final String genericType = genericItem.getClazz().getValue();
            if (AudioItem.CLASS.getValue().equals(genericType)) {
                specificItems.add(new AudioItem(genericItem));
            }
            else if (MusicTrack.CLASS.getValue().equals(genericType)) {
                specificItems.add(new MusicTrack(genericItem));
            }
            else if (AudioBook.CLASS.getValue().equals(genericType)) {
                specificItems.add(new AudioBook(genericItem));
            }
            else if (AudioBroadcast.CLASS.getValue().equals(genericType)) {
                specificItems.add(new AudioBroadcast(genericItem));
            }
            else if (VideoItem.CLASS.getValue().equals(genericType)) {
                specificItems.add(new VideoItem(genericItem));
            }
            else if (Movie.CLASS.getValue().equals(genericType)) {
                specificItems.add(new Movie(genericItem));
            }
            else if (VideoBroadcast.CLASS.getValue().equals(genericType)) {
                specificItems.add(new VideoBroadcast(genericItem));
            }
            else if (MusicVideoClip.CLASS.getValue().equals(genericType)) {
                specificItems.add(new MusicVideoClip(genericItem));
            }
            else if (ImageItem.CLASS.getValue().equals(genericType)) {
                specificItems.add(new ImageItem(genericItem));
            }
            else if (Photo.CLASS.getValue().equals(genericType)) {
                specificItems.add(new Photo(genericItem));
            }
            else if (PlaylistItem.CLASS.getValue().equals(genericType)) {
                specificItems.add(new PlaylistItem(genericItem));
            }
            else if (TextItem.CLASS.getValue().equals(genericType)) {
                specificItems.add(new TextItem(genericItem));
            }
            else {
                specificItems.add(genericItem);
            }
        }
        return specificItems;
    }
    
    protected List<Container> replaceGenericContainers(final List<Container> genericContainers) {
        final List<Container> specificContainers = new ArrayList<Container>();
        for (final Container genericContainer : genericContainers) {
            final String genericType = genericContainer.getClazz().getValue();
            Container specific;
            if (Album.CLASS.getValue().equals(genericType)) {
                specific = new Album(genericContainer);
            }
            else if (MusicAlbum.CLASS.getValue().equals(genericType)) {
                specific = new MusicAlbum(genericContainer);
            }
            else if (PhotoAlbum.CLASS.getValue().equals(genericType)) {
                specific = new PhotoAlbum(genericContainer);
            }
            else if (GenreContainer.CLASS.getValue().equals(genericType)) {
                specific = new GenreContainer(genericContainer);
            }
            else if (MusicGenre.CLASS.getValue().equals(genericType)) {
                specific = new MusicGenre(genericContainer);
            }
            else if (MovieGenre.CLASS.getValue().equals(genericType)) {
                specific = new MovieGenre(genericContainer);
            }
            else if (PlaylistContainer.CLASS.getValue().equals(genericType)) {
                specific = new PlaylistContainer(genericContainer);
            }
            else if (PersonContainer.CLASS.getValue().equals(genericType)) {
                specific = new PersonContainer(genericContainer);
            }
            else if (MusicArtist.CLASS.getValue().equals(genericType)) {
                specific = new MusicArtist(genericContainer);
            }
            else if (StorageSystem.CLASS.getValue().equals(genericType)) {
                specific = new StorageSystem(genericContainer);
            }
            else if (StorageVolume.CLASS.getValue().equals(genericType)) {
                specific = new StorageVolume(genericContainer);
            }
            else if (StorageFolder.CLASS.getValue().equals(genericType)) {
                specific = new StorageFolder(genericContainer);
            }
            else {
                specific = genericContainer;
            }
            specific.setItems(this.replaceGenericItems(genericContainer.getItems()));
            specificContainers.add(specific);
        }
        return specificContainers;
    }
    
    public long getCount() {
        return this.items.size() + this.containers.size();
    }
}
