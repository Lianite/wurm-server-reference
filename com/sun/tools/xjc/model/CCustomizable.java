// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.xml.xsom.XSComponent;
import org.xml.sax.Locator;

public interface CCustomizable
{
    CCustomizations getCustomizations();
    
    Locator getLocator();
    
    XSComponent getSchemaComponent();
}
