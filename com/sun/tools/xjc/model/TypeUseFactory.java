// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import com.sun.xml.bind.v2.TODO;
import javax.activation.MimeType;
import com.sun.xml.bind.v2.model.core.ID;

public final class TypeUseFactory
{
    public static TypeUse makeID(final TypeUse t, final ID id) {
        if (t.idUse() != ID.NONE) {
            throw new IllegalStateException();
        }
        return new TypeUseImpl(t.getInfo(), t.isCollection(), id, t.getExpectedMimeType(), t.getAdapterUse());
    }
    
    public static TypeUse makeMimeTyped(final TypeUse t, final MimeType mt) {
        if (t.getExpectedMimeType() != null) {
            throw new IllegalStateException();
        }
        return new TypeUseImpl(t.getInfo(), t.isCollection(), t.idUse(), mt, t.getAdapterUse());
    }
    
    public static TypeUse makeCollection(final TypeUse t) {
        if (t.isCollection()) {
            return t;
        }
        final CAdapter au = t.getAdapterUse();
        if (au != null && !au.isWhitespaceAdapter()) {
            TODO.checkSpec();
            return CBuiltinLeafInfo.STRING_LIST;
        }
        return new TypeUseImpl(t.getInfo(), true, t.idUse(), t.getExpectedMimeType(), null);
    }
    
    public static TypeUse adapt(final TypeUse t, final CAdapter adapter) {
        assert t.getAdapterUse() == null;
        return new TypeUseImpl(t.getInfo(), t.isCollection(), t.idUse(), t.getExpectedMimeType(), adapter);
    }
    
    public static TypeUse adapt(final TypeUse t, final Class<? extends XmlAdapter> adapter, final boolean copy) {
        return adapt(t, new CAdapter(adapter, copy));
    }
}
