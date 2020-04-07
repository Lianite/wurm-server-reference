// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import org.xml.sax.Locator;
import javax.xml.namespace.QName;

public interface BIDeclaration
{
    void setParent(final BindInfo p0);
    
    QName getName();
    
    Locator getLocation();
    
    void markAsAcknowledged();
    
    boolean isAcknowledged();
}
