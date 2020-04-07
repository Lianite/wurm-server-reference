// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary.visitor;

import org.kohsuke.rngom.binary.Pattern;
import org.kohsuke.rngom.nc.NameClass;
import java.util.HashSet;
import java.util.Set;

public class ChildElementFinder extends PatternWalker
{
    private final Set children;
    
    public ChildElementFinder() {
        this.children = new HashSet();
    }
    
    public Set getChildren() {
        return this.children;
    }
    
    public void visitElement(final NameClass nc, final Pattern content) {
        this.children.add(new Element(nc, content));
    }
    
    public void visitAttribute(final NameClass ns, final Pattern value) {
    }
    
    public void visitList(final Pattern p) {
    }
    
    public static class Element
    {
        public final NameClass nc;
        public final Pattern content;
        
        public Element(final NameClass nc, final Pattern content) {
            this.nc = nc;
            this.content = content;
        }
        
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Element)) {
                return false;
            }
            final Element element = (Element)o;
            Label_0054: {
                if (this.content != null) {
                    if (this.content.equals(element.content)) {
                        break Label_0054;
                    }
                }
                else if (element.content == null) {
                    break Label_0054;
                }
                return false;
            }
            if (this.nc != null) {
                if (this.nc.equals(element.nc)) {
                    return true;
                }
            }
            else if (element.nc == null) {
                return true;
            }
            return false;
        }
        
        public int hashCode() {
            int result = (this.nc != null) ? this.nc.hashCode() : 0;
            result = 29 * result + ((this.content != null) ? this.content.hashCode() : 0);
            return result;
        }
    }
}
