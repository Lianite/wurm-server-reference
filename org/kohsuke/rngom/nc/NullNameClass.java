// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.nc;

import javax.xml.namespace.QName;

final class NullNameClass extends NameClass
{
    public boolean contains(final QName name) {
        return false;
    }
    
    public int containsSpecificity(final QName name) {
        return -1;
    }
    
    public int hashCode() {
        return NullNameClass.class.hashCode();
    }
    
    public boolean equals(final Object obj) {
        return this == obj;
    }
    
    public <V> V accept(final NameClassVisitor<V> visitor) {
        return visitor.visitNull();
    }
    
    public boolean isOpen() {
        return false;
    }
    
    private Object readResolve() {
        return NameClass.NULL;
    }
}
