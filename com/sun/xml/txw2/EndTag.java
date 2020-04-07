// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.txw2;

final class EndTag extends Content
{
    boolean concludesPendingStartTag() {
        return true;
    }
    
    void accept(final ContentVisitor visitor) {
        visitor.onEndTag();
    }
}
