// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.impl;

import com.sun.xml.bind.v2.model.annotation.Locatable;
import javax.xml.namespace.QName;
import com.sun.xml.bind.v2.runtime.Location;
import com.sun.xml.bind.v2.model.core.LeafInfo;

abstract class LeafInfoImpl<TypeT, ClassDeclT> implements LeafInfo<TypeT, ClassDeclT>, Location
{
    private final TypeT type;
    private final QName typeName;
    
    protected LeafInfoImpl(final TypeT type, final QName typeName) {
        assert type != null;
        this.type = type;
        this.typeName = typeName;
    }
    
    public TypeT getType() {
        return this.type;
    }
    
    public final boolean canBeReferencedByIDREF() {
        return false;
    }
    
    public QName getTypeName() {
        return this.typeName;
    }
    
    public Locatable getUpstream() {
        return null;
    }
    
    public Location getLocation() {
        return this;
    }
    
    public boolean isSimpleType() {
        return true;
    }
    
    public String toString() {
        return this.type.toString();
    }
}
