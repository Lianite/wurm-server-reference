// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.nc;

import javax.xml.namespace.QName;

public class AnyNameExceptNameClass extends NameClass
{
    private final NameClass nameClass;
    
    public AnyNameExceptNameClass(final NameClass nameClass) {
        this.nameClass = nameClass;
    }
    
    public boolean contains(final QName name) {
        return !this.nameClass.contains(name);
    }
    
    public int containsSpecificity(final QName name) {
        return this.contains(name) ? 0 : -1;
    }
    
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof AnyNameExceptNameClass && this.nameClass.equals(((AnyNameExceptNameClass)obj).nameClass);
    }
    
    public int hashCode() {
        return ~this.nameClass.hashCode();
    }
    
    public <V> V accept(final NameClassVisitor<V> visitor) {
        return visitor.visitAnyNameExcept(this.nameClass);
    }
    
    public boolean isOpen() {
        return true;
    }
}
