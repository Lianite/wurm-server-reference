// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.container;

import org.fourthline.cling.support.model.DescMeta;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.WriteStatus;
import java.util.ArrayList;
import org.fourthline.cling.support.model.item.Item;
import java.util.List;
import org.fourthline.cling.support.model.DIDLObject;

public class Container extends DIDLObject
{
    protected Integer childCount;
    protected boolean searchable;
    protected List<Class> createClasses;
    protected List<Class> searchClasses;
    protected List<Container> containers;
    protected List<Item> items;
    
    public Container() {
        this.childCount = null;
        this.createClasses = new ArrayList<Class>();
        this.searchClasses = new ArrayList<Class>();
        this.containers = new ArrayList<Container>();
        this.items = new ArrayList<Item>();
    }
    
    public Container(final Container other) {
        super(other);
        this.childCount = null;
        this.createClasses = new ArrayList<Class>();
        this.searchClasses = new ArrayList<Class>();
        this.containers = new ArrayList<Container>();
        this.items = new ArrayList<Item>();
        this.setChildCount(other.getChildCount());
        this.setSearchable(other.isSearchable());
        this.setCreateClasses(other.getCreateClasses());
        this.setSearchClasses(other.getSearchClasses());
        this.setItems(other.getItems());
    }
    
    public Container(final String id, final String parentID, final String title, final String creator, final boolean restricted, final WriteStatus writeStatus, final Class clazz, final List<Res> resources, final List<Property> properties, final List<DescMeta> descMetadata) {
        super(id, parentID, title, creator, restricted, writeStatus, clazz, resources, properties, descMetadata);
        this.childCount = null;
        this.createClasses = new ArrayList<Class>();
        this.searchClasses = new ArrayList<Class>();
        this.containers = new ArrayList<Container>();
        this.items = new ArrayList<Item>();
    }
    
    public Container(final String id, final String parentID, final String title, final String creator, final boolean restricted, final WriteStatus writeStatus, final Class clazz, final List<Res> resources, final List<Property> properties, final List<DescMeta> descMetadata, final Integer childCount, final boolean searchable, final List<Class> createClasses, final List<Class> searchClasses, final List<Item> items) {
        super(id, parentID, title, creator, restricted, writeStatus, clazz, resources, properties, descMetadata);
        this.childCount = null;
        this.createClasses = new ArrayList<Class>();
        this.searchClasses = new ArrayList<Class>();
        this.containers = new ArrayList<Container>();
        this.items = new ArrayList<Item>();
        this.childCount = childCount;
        this.searchable = searchable;
        this.createClasses = createClasses;
        this.searchClasses = searchClasses;
        this.items = items;
    }
    
    public Container(final String id, final Container parent, final String title, final String creator, final Class clazz, final Integer childCount) {
        this(id, parent.getId(), title, creator, true, null, clazz, new ArrayList<Res>(), new ArrayList<Property>(), new ArrayList<DescMeta>(), childCount, false, new ArrayList<Class>(), new ArrayList<Class>(), new ArrayList<Item>());
    }
    
    public Container(final String id, final String parentID, final String title, final String creator, final Class clazz, final Integer childCount) {
        this(id, parentID, title, creator, true, null, clazz, new ArrayList<Res>(), new ArrayList<Property>(), new ArrayList<DescMeta>(), childCount, false, new ArrayList<Class>(), new ArrayList<Class>(), new ArrayList<Item>());
    }
    
    public Container(final String id, final Container parent, final String title, final String creator, final Class clazz, final Integer childCount, final boolean searchable, final List<Class> createClasses, final List<Class> searchClasses, final List<Item> items) {
        this(id, parent.getId(), title, creator, true, null, clazz, new ArrayList<Res>(), new ArrayList<Property>(), new ArrayList<DescMeta>(), childCount, searchable, createClasses, searchClasses, items);
    }
    
    public Container(final String id, final String parentID, final String title, final String creator, final Class clazz, final Integer childCount, final boolean searchable, final List<Class> createClasses, final List<Class> searchClasses, final List<Item> items) {
        this(id, parentID, title, creator, true, null, clazz, new ArrayList<Res>(), new ArrayList<Property>(), new ArrayList<DescMeta>(), childCount, searchable, createClasses, searchClasses, items);
    }
    
    public Integer getChildCount() {
        return this.childCount;
    }
    
    public void setChildCount(final Integer childCount) {
        this.childCount = childCount;
    }
    
    public boolean isSearchable() {
        return this.searchable;
    }
    
    public void setSearchable(final boolean searchable) {
        this.searchable = searchable;
    }
    
    public List<Class> getCreateClasses() {
        return this.createClasses;
    }
    
    public void setCreateClasses(final List<Class> createClasses) {
        this.createClasses = createClasses;
    }
    
    public List<Class> getSearchClasses() {
        return this.searchClasses;
    }
    
    public void setSearchClasses(final List<Class> searchClasses) {
        this.searchClasses = searchClasses;
    }
    
    public Container getFirstContainer() {
        return this.getContainers().get(0);
    }
    
    public Container addContainer(final Container container) {
        this.getContainers().add(container);
        return this;
    }
    
    public List<Container> getContainers() {
        return this.containers;
    }
    
    public void setContainers(final List<Container> containers) {
        this.containers = containers;
    }
    
    public List<Item> getItems() {
        return this.items;
    }
    
    public void setItems(final List<Item> items) {
        this.items = items;
    }
    
    public Container addItem(final Item item) {
        this.getItems().add(item);
        return this;
    }
}
