// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.v2.runtime;

import com.sun.istack.NotNull;
import javax.xml.namespace.NamespaceContext;

public interface NamespaceContext2 extends NamespaceContext
{
    String declareNamespace(final String p0, final String p1, final boolean p2);
    
    int force(@NotNull final String p0, @NotNull final String p1);
}
