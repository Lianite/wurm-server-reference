// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.txw2;

final class Cdata extends Text
{
    Cdata(final Document document, final NamespaceResolver nsResolver, final Object obj) {
        super(document, nsResolver, obj);
    }
    
    void accept(final ContentVisitor visitor) {
        visitor.onCdata(this.buffer);
    }
}
