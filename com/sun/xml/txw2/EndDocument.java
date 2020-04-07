// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.txw2;

final class EndDocument extends Content
{
    boolean concludesPendingStartTag() {
        return true;
    }
    
    void accept(final ContentVisitor visitor) {
        visitor.onEndDocument();
    }
}
