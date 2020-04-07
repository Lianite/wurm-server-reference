// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import com.google.common.base.Function;
import javax.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Ordering;

final class Name
{
    static final Name EMPTY;
    public static final Name SELF;
    public static final Name PARENT;
    private final String display;
    private final String canonical;
    private static final Ordering<Name> DISPLAY_ORDERING;
    private static final Ordering<Name> CANONICAL_ORDERING;
    
    @VisibleForTesting
    static Name simple(final String name) {
        switch (name) {
            case ".": {
                return Name.SELF;
            }
            case "..": {
                return Name.PARENT;
            }
            default: {
                return new Name(name, name);
            }
        }
    }
    
    public static Name create(final String display, final String canonical) {
        return new Name(display, canonical);
    }
    
    private Name(final String display, final String canonical) {
        this.display = Preconditions.checkNotNull(display);
        this.canonical = Preconditions.checkNotNull(canonical);
    }
    
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj instanceof Name) {
            final Name other = (Name)obj;
            return this.canonical.equals(other.canonical);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return Util.smearHash(this.canonical.hashCode());
    }
    
    @Override
    public String toString() {
        return this.display;
    }
    
    public static Ordering<Name> displayOrdering() {
        return Name.DISPLAY_ORDERING;
    }
    
    public static Ordering<Name> canonicalOrdering() {
        return Name.CANONICAL_ORDERING;
    }
    
    static {
        EMPTY = new Name("", "");
        SELF = new Name(".", ".");
        PARENT = new Name("..", "..");
        DISPLAY_ORDERING = Ordering.natural().onResultOf((Function<Name, ?>)new Function<Name, String>() {
            @Override
            public String apply(final Name name) {
                return name.display;
            }
        });
        CANONICAL_ORDERING = Ordering.natural().onResultOf((Function<Name, ?>)new Function<Name, String>() {
            @Override
            public String apply(final Name name) {
                return name.canonical;
            }
        });
    }
}
