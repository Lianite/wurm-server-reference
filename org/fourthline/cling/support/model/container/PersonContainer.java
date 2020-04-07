// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.model.container;

import org.fourthline.cling.support.model.DIDLObject;

public class PersonContainer extends Container
{
    public static final Class CLASS;
    
    public PersonContainer() {
        this.setClazz(PersonContainer.CLASS);
    }
    
    public PersonContainer(final Container other) {
        super(other);
    }
    
    public PersonContainer(final String id, final Container parent, final String title, final String creator, final Integer childCount) {
        this(id, parent.getId(), title, creator, childCount);
    }
    
    public PersonContainer(final String id, final String parentID, final String title, final String creator, final Integer childCount) {
        super(id, parentID, title, creator, PersonContainer.CLASS, childCount);
    }
    
    public String getLanguage() {
        return this.getFirstPropertyValue((java.lang.Class<? extends Property<String>>)Property.DC.LANGUAGE.class);
    }
    
    public PersonContainer setLanguage(final String language) {
        this.replaceFirstProperty(new Property.DC.LANGUAGE(language));
        return this;
    }
    
    static {
        CLASS = new Class("object.container.person");
    }
}
