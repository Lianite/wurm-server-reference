// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.container;

import org.fourthline.cling.support.model.DIDLObject;

public class MusicGenre extends GenreContainer
{
    public static final Class CLASS;
    
    public MusicGenre() {
        this.setClazz(MusicGenre.CLASS);
    }
    
    public MusicGenre(final Container other) {
        super(other);
    }
    
    public MusicGenre(final String id, final Container parent, final String title, final String creator, final Integer childCount) {
        this(id, parent.getId(), title, creator, childCount);
    }
    
    public MusicGenre(final String id, final String parentID, final String title, final String creator, final Integer childCount) {
        super(id, parentID, title, creator, childCount);
        this.setClazz(MusicGenre.CLASS);
    }
    
    static {
        CLASS = new Class("object.container.genre.musicGenre");
    }
}
