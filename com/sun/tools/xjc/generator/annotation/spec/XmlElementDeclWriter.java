// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator.annotation.spec;

import com.sun.codemodel.JType;
import javax.xml.bind.annotation.XmlElementDecl;
import com.sun.codemodel.JAnnotationWriter;

public interface XmlElementDeclWriter extends JAnnotationWriter<XmlElementDecl>
{
    XmlElementDeclWriter name(final String p0);
    
    XmlElementDeclWriter namespace(final String p0);
    
    XmlElementDeclWriter defaultValue(final String p0);
    
    XmlElementDeclWriter scope(final Class p0);
    
    XmlElementDeclWriter scope(final JType p0);
    
    XmlElementDeclWriter substitutionHeadNamespace(final String p0);
    
    XmlElementDeclWriter substitutionHeadName(final String p0);
}
