// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.nc;

import javax.xml.namespace.QName;

public class SimpleNameClass extends NameClass
{
    public final QName name;
    
    public SimpleNameClass(final QName name) {
        this.name = name;
    }
    
    public SimpleNameClass(final String nsUri, final String localPart) {
        this(new QName(nsUri, localPart));
    }
    
    public boolean contains(final QName name) {
        return this.name.equals(name);
    }
    
    public int containsSpecificity(final QName name) {
        return this.contains(name) ? 2 : -1;
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }
    
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof SimpleNameClass)) {
            return false;
        }
        final SimpleNameClass other = (SimpleNameClass)obj;
        return this.name.equals(other.name);
    }
    
    public <V> V accept(final NameClassVisitor<V> visitor) {
        return visitor.visitName(this.name);
    }
    
    public boolean isOpen() {
        return false;
    }
}
