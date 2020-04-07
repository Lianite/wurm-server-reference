// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

import javax.xml.namespace.QName;
import org.xml.sax.Locator;

final class RestrictionViolationException extends Exception
{
    private String messageId;
    private Locator loc;
    private QName name;
    
    RestrictionViolationException(final String messageId) {
        this.messageId = messageId;
    }
    
    RestrictionViolationException(final String messageId, final QName name) {
        this.messageId = messageId;
        this.name = name;
    }
    
    String getMessageId() {
        return this.messageId;
    }
    
    Locator getLocator() {
        return this.loc;
    }
    
    void maybeSetLocator(final Locator loc) {
        if (this.loc == null) {
            this.loc = loc;
        }
    }
    
    QName getName() {
        return this.name;
    }
}
