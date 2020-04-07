// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api;

import javax.xml.transform.Result;
import java.io.IOException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;

public interface J2SJAXBModel extends JAXBModel
{
    QName getXmlTypeName(final Reference p0);
    
    void generateSchema(final SchemaOutputResolver p0, final ErrorListener p1) throws IOException;
    
    void generateEpisodeFile(final Result p0);
}
