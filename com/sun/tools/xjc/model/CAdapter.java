// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.model.nav.EagerNClass;
import com.sun.xml.bind.v2.model.nav.Navigator;
import com.sun.tools.xjc.model.nav.NavigatorImpl;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.xml.bind.v2.model.core.Adapter;

public final class CAdapter extends Adapter<NType, NClass>
{
    private JClass adapterClass1;
    private Class<? extends XmlAdapter> adapterClass2;
    
    public CAdapter(final Class<? extends XmlAdapter> adapter, final boolean copy) {
        super(getRef(adapter, copy), (Navigator<Object, NClass, ?, ?>)NavigatorImpl.theInstance);
        this.adapterClass1 = null;
        this.adapterClass2 = adapter;
    }
    
    static NClass getRef(final Class<? extends XmlAdapter> adapter, final boolean copy) {
        if (copy) {
            return new EagerNClass(adapter) {
                public JClass toType(final Outline o, final Aspect aspect) {
                    return o.addRuntime(adapter);
                }
                
                public String fullName() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return NavigatorImpl.theInstance.ref((Class)adapter);
    }
    
    public CAdapter(final JClass adapter) {
        super(NavigatorImpl.theInstance.ref(adapter), (Navigator<Object, NClass, ?, ?>)NavigatorImpl.theInstance);
        this.adapterClass1 = adapter;
        this.adapterClass2 = null;
    }
    
    public JClass getAdapterClass(final Outline o) {
        if (this.adapterClass1 == null) {
            this.adapterClass1 = o.getCodeModel().ref(this.adapterClass2);
        }
        return ((NClass)this.adapterType).toType(o, Aspect.EXPOSED);
    }
    
    public boolean isWhitespaceAdapter() {
        return this.adapterClass2 == CollapsedStringAdapter.class || this.adapterClass2 == NormalizedStringAdapter.class;
    }
    
    public Class<? extends XmlAdapter> getAdapterIfKnown() {
        return this.adapterClass2;
    }
}
