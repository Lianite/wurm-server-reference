// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.nc;

import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;
import java.io.Serializable;
import org.kohsuke.rngom.ast.om.ParsedNameClass;

public abstract class NameClass implements ParsedNameClass, Serializable
{
    static final int SPECIFICITY_NONE = -1;
    static final int SPECIFICITY_ANY_NAME = 0;
    static final int SPECIFICITY_NS_NAME = 1;
    static final int SPECIFICITY_NAME = 2;
    public static final NameClass ANY;
    public static final NameClass NULL;
    
    public abstract boolean contains(final QName p0);
    
    public abstract int containsSpecificity(final QName p0);
    
    public abstract <V> V accept(final NameClassVisitor<V> p0);
    
    public abstract boolean isOpen();
    
    public Set<QName> listNames() {
        final Set<QName> names = new HashSet<QName>();
        this.accept((NameClassVisitor<Object>)new NameClassWalker() {
            public Void visitName(final QName name) {
                names.add(name);
                return null;
            }
        });
        return names;
    }
    
    public final boolean hasOverlapWith(final NameClass nc2) {
        return OverlapDetector.overlap(this, nc2);
    }
    
    static {
        ANY = new AnyNameClass();
        NULL = new NullNameClass();
    }
}
