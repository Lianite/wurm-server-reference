// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.nc;

import javax.xml.namespace.QName;

final class AnyNameClass extends NameClass
{
    public boolean contains(final QName name) {
        return true;
    }
    
    public int containsSpecificity(final QName name) {
        return 0;
    }
    
    public boolean equals(final Object obj) {
        return obj == this;
    }
    
    public int hashCode() {
        return AnyNameClass.class.hashCode();
    }
    
    public <V> V accept(final NameClassVisitor<V> visitor) {
        return visitor.visitAnyName();
    }
    
    public boolean isOpen() {
        return true;
    }
    
    private static Object readReplace() {
        return NameClass.ANY;
    }
}
