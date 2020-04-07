// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.model.impl;

import com.sun.xml.bind.v2.model.core.EnumLeafInfo;
import com.sun.xml.bind.v2.model.core.EnumConstant;

class EnumConstantImpl<T, C, F, M> implements EnumConstant<T, C>
{
    protected final String lexical;
    protected final EnumLeafInfoImpl<T, C, F, M> owner;
    protected final String name;
    protected final EnumConstantImpl<T, C, F, M> next;
    
    public EnumConstantImpl(final EnumLeafInfoImpl<T, C, F, M> owner, final String name, final String lexical, final EnumConstantImpl<T, C, F, M> next) {
        this.lexical = lexical;
        this.owner = owner;
        this.name = name;
        this.next = next;
    }
    
    public EnumLeafInfo<T, C> getEnclosingClass() {
        return this.owner;
    }
    
    public final String getLexicalValue() {
        return this.lexical;
    }
    
    public final String getName() {
        return this.name;
    }
}
