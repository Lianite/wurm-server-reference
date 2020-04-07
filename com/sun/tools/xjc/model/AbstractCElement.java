// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.xml.xsom.XSComponent;
import javax.xml.bind.annotation.XmlTransient;
import org.xml.sax.Locator;

abstract class AbstractCElement extends AbstractCTypeInfoImpl implements CElement
{
    @XmlTransient
    private final Locator locator;
    private boolean isAbstract;
    
    protected AbstractCElement(final Model model, final XSComponent source, final Locator locator, final CCustomizations customizations) {
        super(model, source, customizations);
        this.locator = locator;
    }
    
    public Locator getLocator() {
        return this.locator;
    }
    
    public boolean isAbstract() {
        return this.isAbstract;
    }
    
    public void setAbstract() {
        this.isAbstract = true;
    }
}
