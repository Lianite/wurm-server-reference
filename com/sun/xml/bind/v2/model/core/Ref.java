// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.core;

import com.sun.xml.bind.v2.model.nav.Navigator;
import com.sun.xml.bind.v2.model.annotation.AnnotationReader;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.sun.xml.bind.v2.model.impl.ModelBuilder;

public final class Ref<T, C>
{
    public final T type;
    public final Adapter<T, C> adapter;
    public final boolean valueList;
    
    public Ref(final T type) {
        this(type, null, false);
    }
    
    public Ref(T type, final Adapter<T, C> adapter, final boolean valueList) {
        this.adapter = adapter;
        if (adapter != null) {
            type = adapter.defaultType;
        }
        this.type = type;
        this.valueList = valueList;
    }
    
    public Ref(final ModelBuilder<T, C, ?, ?> builder, final T type, final XmlJavaTypeAdapter xjta, final XmlList xl) {
        this((AnnotationReader<Object, C, ?, ?>)builder.reader, (Navigator<Object, C, ?, ?>)builder.nav, type, xjta, xl);
    }
    
    public Ref(final AnnotationReader<T, C, ?, ?> reader, final Navigator<T, C, ?, ?> nav, T type, final XmlJavaTypeAdapter xjta, final XmlList xl) {
        Adapter<T, C> adapter = null;
        if (xjta != null) {
            adapter = new Adapter<T, C>(xjta, reader, nav);
            type = adapter.defaultType;
        }
        this.type = type;
        this.adapter = adapter;
        this.valueList = (xl != null);
    }
}
