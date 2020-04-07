// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader;

import com.sun.codemodel.JPackage;
import com.sun.msv.grammar.ReferenceExp;

public interface PackageTracker
{
    JPackage get(final ReferenceExp p0);
}
