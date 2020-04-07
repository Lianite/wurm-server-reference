// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.container;

import org.fourthline.cling.support.model.DIDLObject;

public class MovieGenre extends GenreContainer
{
    public static final Class CLASS;
    
    public MovieGenre() {
        this.setClazz(MovieGenre.CLASS);
    }
    
    public MovieGenre(final Container other) {
        super(other);
    }
    
    public MovieGenre(final String id, final Container parent, final String title, final String creator, final Integer childCount) {
        this(id, parent.getId(), title, creator, childCount);
    }
    
    public MovieGenre(final String id, final String parentID, final String title, final String creator, final Integer childCount) {
        super(id, parentID, title, creator, childCount);
        this.setClazz(MovieGenre.CLASS);
    }
    
    static {
        CLASS = new Class("object.container.genre.movieGenre");
    }
}
