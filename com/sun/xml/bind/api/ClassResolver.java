// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.bind.api;

import com.sun.istack.Nullable;
import com.sun.istack.NotNull;

public abstract class ClassResolver
{
    @Nullable
    public abstract Class<?> resolveElementName(@NotNull final String p0, @NotNull final String p1) throws Exception;
}
