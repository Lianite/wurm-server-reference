// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl;

import org.xml.sax.Locator;
import org.relaxng.datatype.ValidationContext;
import com.sun.xml.xsom.ForeignAttributes;
import org.xml.sax.helpers.AttributesImpl;

public final class ForeignAttributesImpl extends AttributesImpl implements ForeignAttributes
{
    private final ValidationContext context;
    private final Locator locator;
    final ForeignAttributesImpl next;
    
    public ForeignAttributesImpl(final ValidationContext context, final Locator locator, final ForeignAttributesImpl next) {
        this.context = context;
        this.locator = locator;
        this.next = next;
    }
    
    public ValidationContext getContext() {
        return this.context;
    }
    
    public Locator getLocator() {
        return this.locator;
    }
}
