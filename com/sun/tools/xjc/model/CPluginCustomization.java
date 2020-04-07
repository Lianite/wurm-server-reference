// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import org.xml.sax.Locator;
import org.w3c.dom.Element;

public class CPluginCustomization
{
    public final Element element;
    public final Locator locator;
    private boolean acknowledged;
    
    public void markAsAcknowledged() {
        this.acknowledged = true;
    }
    
    public CPluginCustomization(final Element element, final Locator locator) {
        this.element = element;
        this.locator = locator;
    }
    
    public boolean isAcknowledged() {
        return this.acknowledged;
    }
}
