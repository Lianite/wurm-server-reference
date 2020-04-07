// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.item;

import java.util.ArrayList;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.DescMeta;
import org.fourthline.cling.support.model.Res;
import java.util.List;
import org.fourthline.cling.support.model.WriteStatus;
import org.fourthline.cling.support.model.DIDLObject;

public class Item extends DIDLObject
{
    protected String refID;
    
    public Item() {
    }
    
    public Item(final Item other) {
        super(other);
        this.setRefID(other.getRefID());
    }
    
    public Item(final String id, final String parentID, final String title, final String creator, final boolean restricted, final WriteStatus writeStatus, final Class clazz, final List<Res> resources, final List<Property> properties, final List<DescMeta> descMetadata) {
        super(id, parentID, title, creator, restricted, writeStatus, clazz, resources, properties, descMetadata);
    }
    
    public Item(final String id, final String parentID, final String title, final String creator, final boolean restricted, final WriteStatus writeStatus, final Class clazz, final List<Res> resources, final List<Property> properties, final List<DescMeta> descMetadata, final String refID) {
        super(id, parentID, title, creator, restricted, writeStatus, clazz, resources, properties, descMetadata);
        this.refID = refID;
    }
    
    public Item(final String id, final Container parent, final String title, final String creator, final Class clazz) {
        this(id, parent.getId(), title, creator, false, null, clazz, new ArrayList<Res>(), new ArrayList<Property>(), new ArrayList<DescMeta>());
    }
    
    public Item(final String id, final Container parent, final String title, final String creator, final Class clazz, final String refID) {
        this(id, parent.getId(), title, creator, false, null, clazz, new ArrayList<Res>(), new ArrayList<Property>(), new ArrayList<DescMeta>(), refID);
    }
    
    public Item(final String id, final String parentID, final String title, final String creator, final Class clazz) {
        this(id, parentID, title, creator, false, null, clazz, new ArrayList<Res>(), new ArrayList<Property>(), new ArrayList<DescMeta>());
    }
    
    public Item(final String id, final String parentID, final String title, final String creator, final Class clazz, final String refID) {
        this(id, parentID, title, creator, false, null, clazz, new ArrayList<Res>(), new ArrayList<Property>(), new ArrayList<DescMeta>(), refID);
    }
    
    public String getRefID() {
        return this.refID;
    }
    
    public void setRefID(final String refID) {
        this.refID = refID;
    }
}
