// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.schemagen;

import com.sun.xml.bind.v2.schemagen.xmlschema.Particle;
import com.sun.xml.bind.v2.schemagen.xmlschema.Occurs;
import com.sun.xml.bind.v2.schemagen.xmlschema.TypeDefParticle;
import com.sun.xml.bind.v2.schemagen.xmlschema.ContentModelContainer;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

abstract class Tree
{
    Tree makeOptional(final boolean really) {
        return really ? new Optional(this) : this;
    }
    
    Tree makeRepeated(final boolean really) {
        return really ? new Repeated(this) : this;
    }
    
    static Tree makeGroup(final GroupKind kind, final List<Tree> children) {
        if (children.size() == 1) {
            return children.get(0);
        }
        final List<Tree> normalizedChildren = new ArrayList<Tree>(children.size());
        for (final Tree t : children) {
            if (t instanceof Group) {
                final Group g = (Group)t;
                if (g.kind == kind) {
                    normalizedChildren.addAll(Arrays.asList(g.children));
                    continue;
                }
            }
            normalizedChildren.add(t);
        }
        return new Group(kind, (Tree[])normalizedChildren.toArray(new Tree[normalizedChildren.size()]));
    }
    
    abstract boolean isNullable();
    
    boolean canBeTopLevel() {
        return false;
    }
    
    protected abstract void write(final ContentModelContainer p0, final boolean p1, final boolean p2);
    
    protected void write(final TypeDefParticle ct) {
        if (this.canBeTopLevel()) {
            this.write(ct._cast(ContentModelContainer.class), false, false);
        }
        else {
            new Group(GroupKind.SEQUENCE, new Tree[] { this }).write(ct);
        }
    }
    
    protected final void writeOccurs(final Occurs o, final boolean isOptional, final boolean repeated) {
        if (isOptional) {
            o.minOccurs(0);
        }
        if (repeated) {
            o.maxOccurs("unbounded");
        }
    }
    
    abstract static class Term extends Tree
    {
        boolean isNullable() {
            return false;
        }
    }
    
    private static final class Optional extends Tree
    {
        private final Tree body;
        
        private Optional(final Tree body) {
            this.body = body;
        }
        
        boolean isNullable() {
            return true;
        }
        
        Tree makeOptional(final boolean really) {
            return this;
        }
        
        protected void write(final ContentModelContainer parent, final boolean isOptional, final boolean repeated) {
            this.body.write(parent, true, repeated);
        }
    }
    
    private static final class Repeated extends Tree
    {
        private final Tree body;
        
        private Repeated(final Tree body) {
            this.body = body;
        }
        
        boolean isNullable() {
            return this.body.isNullable();
        }
        
        Tree makeRepeated(final boolean really) {
            return this;
        }
        
        protected void write(final ContentModelContainer parent, final boolean isOptional, final boolean repeated) {
            this.body.write(parent, isOptional, true);
        }
    }
    
    private static final class Group extends Tree
    {
        private final GroupKind kind;
        private final Tree[] children;
        
        private Group(final GroupKind kind, final Tree... children) {
            this.kind = kind;
            this.children = children;
        }
        
        boolean canBeTopLevel() {
            return true;
        }
        
        boolean isNullable() {
            if (this.kind == GroupKind.CHOICE) {
                for (final Tree t : this.children) {
                    if (t.isNullable()) {
                        return true;
                    }
                }
                return false;
            }
            for (final Tree t : this.children) {
                if (!t.isNullable()) {
                    return false;
                }
            }
            return true;
        }
        
        protected void write(final ContentModelContainer parent, final boolean isOptional, final boolean repeated) {
            final Particle c = this.kind.write(parent);
            this.writeOccurs(c, isOptional, repeated);
            for (final Tree child : this.children) {
                child.write(c, false, false);
            }
        }
    }
}
