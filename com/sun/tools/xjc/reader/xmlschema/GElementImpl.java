// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.xml.xsom.XSElementDecl;
import javax.xml.namespace.QName;

final class GElementImpl extends GElement
{
    public final QName tagName;
    public final XSElementDecl decl;
    
    public GElementImpl(final QName tagName, final XSElementDecl decl) {
        this.tagName = tagName;
        this.decl = decl;
    }
    
    public String toString() {
        return this.tagName.toString();
    }
    
    String getPropertyNameSeed() {
        return this.tagName.getLocalPart();
    }
}
