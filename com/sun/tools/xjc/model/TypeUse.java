// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.codemodel.JExpression;
import com.sun.xml.xsom.XmlString;
import com.sun.tools.xjc.outline.Outline;
import javax.activation.MimeType;
import com.sun.xml.bind.v2.model.core.ID;

public interface TypeUse
{
    boolean isCollection();
    
    CAdapter getAdapterUse();
    
    CNonElement getInfo();
    
    ID idUse();
    
    MimeType getExpectedMimeType();
    
    JExpression createConstant(final Outline p0, final XmlString p1);
}
