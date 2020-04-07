// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader;

import com.sun.xml.bind.v2.model.core.TypeInfo;
import javax.activation.MimeType;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import java.util.List;
import java.util.Iterator;
import com.sun.tools.xjc.model.nav.NType;
import java.util.HashSet;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.Multiplicity;
import java.util.Set;

public final class RawTypeSet
{
    public final Set<Ref> refs;
    public final Mode canBeTypeRefs;
    public final Multiplicity mul;
    private CElementPropertyInfo.CollectionMode collectionMode;
    
    public RawTypeSet(final Set<Ref> refs, final Multiplicity m) {
        this.refs = refs;
        this.mul = m;
        this.canBeTypeRefs = this.canBeTypeRefs();
    }
    
    public CElementPropertyInfo.CollectionMode getCollectionMode() {
        return this.collectionMode;
    }
    
    public boolean isRequired() {
        return this.mul.min > 0;
    }
    
    private Mode canBeTypeRefs() {
        final Set<NType> types = new HashSet<NType>();
        this.collectionMode = (this.mul.isAtMostOnce() ? CElementPropertyInfo.CollectionMode.NOT_REPEATED : CElementPropertyInfo.CollectionMode.REPEATED_ELEMENT);
        Mode mode = Mode.SHOULD_BE_TYPEREF;
        for (final Ref r : this.refs) {
            mode = mode.or(r.canBeType(this));
            if (mode == Mode.MUST_BE_REFERENCE) {
                return mode;
            }
            if (!types.add(((TypeInfo<NType, C>)r.toTypeRef(null).getTarget()).getType())) {
                return Mode.MUST_BE_REFERENCE;
            }
            if (!r.isListOfValues()) {
                continue;
            }
            if (this.refs.size() > 1 || !this.mul.isAtMostOnce()) {
                return Mode.MUST_BE_REFERENCE;
            }
            this.collectionMode = CElementPropertyInfo.CollectionMode.REPEATED_VALUE;
        }
        return mode;
    }
    
    public void addTo(final CElementPropertyInfo prop) {
        assert this.canBeTypeRefs != Mode.MUST_BE_REFERENCE;
        if (this.mul.isZero()) {
            return;
        }
        final List<CTypeRef> dst = prop.getTypes();
        for (final Ref t : this.refs) {
            dst.add(t.toTypeRef(prop));
        }
    }
    
    public void addTo(final CReferencePropertyInfo prop) {
        if (this.mul.isZero()) {
            return;
        }
        for (final Ref t : this.refs) {
            t.toElementRef(prop);
        }
    }
    
    public ID id() {
        for (final Ref t : this.refs) {
            final ID id = t.id();
            if (id != ID.NONE) {
                return id;
            }
        }
        return ID.NONE;
    }
    
    public MimeType getExpectedMimeType() {
        for (final Ref t : this.refs) {
            final MimeType mt = t.getExpectedMimeType();
            if (mt != null) {
                return mt;
            }
        }
        return null;
    }
    
    public enum Mode
    {
        SHOULD_BE_TYPEREF(0), 
        CAN_BE_TYPEREF(1), 
        MUST_BE_REFERENCE(2);
        
        private final int rank;
        
        private Mode(final int rank) {
            this.rank = rank;
        }
        
        Mode or(final Mode that) {
            switch (Math.max(this.rank, that.rank)) {
                case 0: {
                    return Mode.SHOULD_BE_TYPEREF;
                }
                case 1: {
                    return Mode.CAN_BE_TYPEREF;
                }
                case 2: {
                    return Mode.MUST_BE_REFERENCE;
                }
                default: {
                    throw new AssertionError();
                }
            }
        }
    }
    
    public abstract static class Ref
    {
        protected abstract CTypeRef toTypeRef(final CElementPropertyInfo p0);
        
        protected abstract void toElementRef(final CReferencePropertyInfo p0);
        
        protected abstract Mode canBeType(final RawTypeSet p0);
        
        protected abstract boolean isListOfValues();
        
        protected abstract ID id();
        
        protected MimeType getExpectedMimeType() {
            return null;
        }
    }
}
