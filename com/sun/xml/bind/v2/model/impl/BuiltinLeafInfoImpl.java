// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.impl;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import com.sun.xml.bind.v2.model.nav.Navigator;
import com.sun.xml.bind.v2.model.core.Element;
import javax.xml.namespace.QName;
import com.sun.xml.bind.v2.model.core.BuiltinLeafInfo;

public class BuiltinLeafInfoImpl<TypeT, ClassDeclT> extends LeafInfoImpl<TypeT, ClassDeclT> implements BuiltinLeafInfo<TypeT, ClassDeclT>
{
    private final QName[] typeNames;
    
    protected BuiltinLeafInfoImpl(final TypeT type, final QName... typeNames) {
        super(type, (typeNames.length > 0) ? typeNames[0] : null);
        this.typeNames = typeNames;
    }
    
    public final QName[] getTypeNames() {
        return this.typeNames;
    }
    
    public final boolean isElement() {
        return false;
    }
    
    public final QName getElementName() {
        return null;
    }
    
    public final Element<TypeT, ClassDeclT> asElement() {
        return null;
    }
    
    public static <TypeT, ClassDeclT> Map<TypeT, BuiltinLeafInfoImpl<TypeT, ClassDeclT>> createLeaves(final Navigator<TypeT, ClassDeclT, ?, ?> nav) {
        final Map<TypeT, BuiltinLeafInfoImpl<TypeT, ClassDeclT>> leaves = new HashMap<TypeT, BuiltinLeafInfoImpl<TypeT, ClassDeclT>>();
        for (final RuntimeBuiltinLeafInfoImpl<?> leaf : RuntimeBuiltinLeafInfoImpl.builtinBeanInfos) {
            final TypeT t = nav.ref(leaf.getClazz());
            leaves.put(t, new BuiltinLeafInfoImpl<TypeT, ClassDeclT>(t, leaf.getTypeNames()));
        }
        return leaves;
    }
}
