// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.nc;

import javax.xml.namespace.QName;

public final class NsNameClass extends NameClass
{
    private final String namespaceUri;
    
    public NsNameClass(final String namespaceUri) {
        this.namespaceUri = namespaceUri;
    }
    
    public boolean contains(final QName name) {
        return this.namespaceUri.equals(name.getNamespaceURI());
    }
    
    public int containsSpecificity(final QName name) {
        return this.contains(name) ? 1 : -1;
    }
    
    public int hashCode() {
        return this.namespaceUri.hashCode();
    }
    
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof NsNameClass && this.namespaceUri.equals(((NsNameClass)obj).namespaceUri);
    }
    
    public <V> V accept(final NameClassVisitor<V> visitor) {
        return visitor.visitNsName(this.namespaceUri);
    }
    
    public boolean isOpen() {
        return true;
    }
}
