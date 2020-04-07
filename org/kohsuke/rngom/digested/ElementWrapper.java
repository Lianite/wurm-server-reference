// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import org.w3c.dom.Element;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;

final class ElementWrapper implements ParsedElementAnnotation
{
    final Element element;
    
    public ElementWrapper(final Element e) {
        this.element = e;
    }
}
