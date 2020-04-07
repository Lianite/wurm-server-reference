// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.impl;

import com.sun.xml.bind.v2.runtime.Location;
import com.sun.xml.bind.v2.model.annotation.Locatable;
import javax.xml.namespace.QName;
import com.sun.xml.bind.v2.model.nav.Navigator;
import com.sun.xml.bind.v2.model.core.NonElement;

class AnyTypeImpl<T, C> implements NonElement<T, C>
{
    private final T type;
    private final Navigator<T, C, ?, ?> nav;
    private static final QName name;
    
    public AnyTypeImpl(final Navigator<T, C, ?, ?> nav) {
        this.type = nav.ref(Object.class);
        this.nav = nav;
    }
    
    public QName getTypeName() {
        return AnyTypeImpl.name;
    }
    
    public T getType() {
        return this.type;
    }
    
    public Locatable getUpstream() {
        return null;
    }
    
    public boolean isSimpleType() {
        return false;
    }
    
    public Location getLocation() {
        return this.nav.getClassLocation(this.nav.asDecl(Object.class));
    }
    
    public final boolean canBeReferencedByIDREF() {
        return true;
    }
    
    static {
        name = new QName("http://www.w3.org/2001/XMLSchema", "anyType");
    }
}
