// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime.reflect;

import com.sun.xml.bind.v2.ClassFactory;
import com.sun.xml.bind.v2.runtime.Coordinator;
import com.sun.xml.bind.api.AccessorException;
import javax.xml.bind.annotation.adapters.XmlAdapter;

final class AdaptedAccessor<BeanT, InMemValueT, OnWireValueT> extends Accessor<BeanT, OnWireValueT>
{
    private final Accessor<BeanT, InMemValueT> core;
    private final Class<? extends XmlAdapter<OnWireValueT, InMemValueT>> adapter;
    private XmlAdapter<OnWireValueT, InMemValueT> staticAdapter;
    
    AdaptedAccessor(final Class<OnWireValueT> targetType, final Accessor<BeanT, InMemValueT> extThis, final Class<? extends XmlAdapter<OnWireValueT, InMemValueT>> adapter) {
        super(targetType);
        this.core = extThis;
        this.adapter = adapter;
    }
    
    public boolean isAdapted() {
        return true;
    }
    
    public OnWireValueT get(final BeanT bean) throws AccessorException {
        final InMemValueT v = this.core.get(bean);
        final XmlAdapter<OnWireValueT, InMemValueT> a = this.getAdapter();
        try {
            return a.marshal(v);
        }
        catch (Exception e) {
            throw new AccessorException(e);
        }
    }
    
    public void set(final BeanT bean, final OnWireValueT o) throws AccessorException {
        final XmlAdapter<OnWireValueT, InMemValueT> a = this.getAdapter();
        try {
            this.core.set(bean, a.unmarshal(o));
        }
        catch (Exception e) {
            throw new AccessorException(e);
        }
    }
    
    public Object getUnadapted(final BeanT bean) throws AccessorException {
        return this.core.getUnadapted(bean);
    }
    
    public void setUnadapted(final BeanT bean, final Object value) throws AccessorException {
        this.core.setUnadapted(bean, value);
    }
    
    private XmlAdapter<OnWireValueT, InMemValueT> getAdapter() {
        final Coordinator coordinator = Coordinator._getInstance();
        if (coordinator != null) {
            return coordinator.getAdapter(this.adapter);
        }
        synchronized (this) {
            if (this.staticAdapter == null) {
                this.staticAdapter = ClassFactory.create(this.adapter);
            }
        }
        return this.staticAdapter;
    }
}
