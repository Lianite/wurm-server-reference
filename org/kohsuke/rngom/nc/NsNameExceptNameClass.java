// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.nc;

import javax.xml.namespace.QName;

public class NsNameExceptNameClass extends NameClass
{
    private final NameClass nameClass;
    private final String namespaceURI;
    
    public NsNameExceptNameClass(final String namespaceURI, final NameClass nameClass) {
        this.namespaceURI = namespaceURI;
        this.nameClass = nameClass;
    }
    
    public boolean contains(final QName name) {
        return this.namespaceURI.equals(name.getNamespaceURI()) && !this.nameClass.contains(name);
    }
    
    public int containsSpecificity(final QName name) {
        return this.contains(name) ? 1 : -1;
    }
    
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof NsNameExceptNameClass)) {
            return false;
        }
        final NsNameExceptNameClass other = (NsNameExceptNameClass)obj;
        return this.namespaceURI.equals(other.namespaceURI) && this.nameClass.equals(other.nameClass);
    }
    
    public int hashCode() {
        return this.namespaceURI.hashCode() ^ this.nameClass.hashCode();
    }
    
    public <V> V accept(final NameClassVisitor<V> visitor) {
        return visitor.visitNsNameExcept(this.namespaceURI, this.nameClass);
    }
    
    public boolean isOpen() {
        return true;
    }
}
