// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.txw2;

final class Comment extends Content
{
    private final StringBuilder buffer;
    
    public Comment(final Document document, final NamespaceResolver nsResolver, final Object obj) {
        document.writeValue(obj, nsResolver, this.buffer = new StringBuilder());
    }
    
    boolean concludesPendingStartTag() {
        return false;
    }
    
    void accept(final ContentVisitor visitor) {
        visitor.onComment(this.buffer);
    }
}
