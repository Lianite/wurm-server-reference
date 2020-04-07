// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.parser;

import java.util.Set;
import com.sun.xml.xsom.XSSchema;

public interface SchemaDocument
{
    String getSystemId();
    
    String getTargetNamespace();
    
    XSSchema getSchema();
    
    Set<SchemaDocument> getReferencedDocuments();
    
    Set<SchemaDocument> getIncludedDocuments();
    
    Set<SchemaDocument> getImportedDocuments(final String p0);
    
    boolean includes(final SchemaDocument p0);
    
    boolean imports(final SchemaDocument p0);
    
    Set<SchemaDocument> getReferers();
}
