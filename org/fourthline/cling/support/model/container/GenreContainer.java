// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.container;

import org.fourthline.cling.support.model.DIDLObject;

public class GenreContainer extends Container
{
    public static final Class CLASS;
    
    public GenreContainer() {
        this.setClazz(GenreContainer.CLASS);
    }
    
    public GenreContainer(final Container other) {
        super(other);
    }
    
    public GenreContainer(final String id, final Container parent, final String title, final String creator, final Integer childCount) {
        this(id, parent.getId(), title, creator, childCount);
    }
    
    public GenreContainer(final String id, final String parentID, final String title, final String creator, final Integer childCount) {
        super(id, parentID, title, creator, GenreContainer.CLASS, childCount);
    }
    
    static {
        CLASS = new Class("object.container.genre");
    }
}
