// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.property;

import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.model.core.Adapter;
import java.lang.reflect.Type;
import com.sun.xml.bind.v2.model.nav.Navigator;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.bind.v2.runtime.reflect.Lister;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;

abstract class ArrayProperty<BeanT, ListT, ItemT> extends PropertyImpl<BeanT>
{
    protected final Accessor<BeanT, ListT> acc;
    protected final Lister<BeanT, ListT, ItemT, Object> lister;
    
    protected ArrayProperty(final JAXBContextImpl context, final RuntimePropertyInfo prop) {
        super(context, prop);
        assert prop.isCollection();
        this.lister = Lister.create(Navigator.REFLECTION.erasure(prop.getRawType()), prop.id(), (Adapter<Type, Class>)prop.getAdapter());
        assert this.lister != null;
        this.acc = prop.getAccessor().optimize(context);
        assert this.acc != null;
    }
    
    public void reset(final BeanT o) throws AccessorException {
        this.lister.reset(o, this.acc);
    }
    
    public final String getIdValue(final BeanT bean) {
        return null;
    }
}
