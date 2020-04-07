// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.dtd;

import java.util.LinkedHashSet;
import java.util.Set;

final class Block
{
    final boolean isOptional;
    final boolean isRepeated;
    final Set<Element> elements;
    
    Block(final boolean optional, final boolean repeated) {
        this.elements = new LinkedHashSet<Element>();
        this.isOptional = optional;
        this.isRepeated = repeated;
    }
}
