// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.nc;

import javax.xml.namespace.QName;

public class ChoiceNameClass extends NameClass
{
    private final NameClass nameClass1;
    private final NameClass nameClass2;
    
    public ChoiceNameClass(final NameClass nameClass1, final NameClass nameClass2) {
        this.nameClass1 = nameClass1;
        this.nameClass2 = nameClass2;
    }
    
    public boolean contains(final QName name) {
        return this.nameClass1.contains(name) || this.nameClass2.contains(name);
    }
    
    public int containsSpecificity(final QName name) {
        return Math.max(this.nameClass1.containsSpecificity(name), this.nameClass2.containsSpecificity(name));
    }
    
    public int hashCode() {
        return this.nameClass1.hashCode() ^ this.nameClass2.hashCode();
    }
    
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof ChoiceNameClass)) {
            return false;
        }
        final ChoiceNameClass other = (ChoiceNameClass)obj;
        return this.nameClass1.equals(other.nameClass1) && this.nameClass2.equals(other.nameClass2);
    }
    
    public <V> V accept(final NameClassVisitor<V> visitor) {
        return visitor.visitChoice(this.nameClass1, this.nameClass2);
    }
    
    public boolean isOpen() {
        return this.nameClass1.isOpen() || this.nameClass2.isOpen();
    }
}
