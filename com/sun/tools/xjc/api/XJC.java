// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.api;

import com.sun.xml.bind.api.impl.NameConverter;
import com.sun.tools.xjc.api.impl.s2j.SchemaCompilerImpl;
import com.sun.tools.xjc.api.impl.j2s.JavaCompilerImpl;

public final class XJC
{
    public static JavaCompiler createJavaCompiler() {
        return new JavaCompilerImpl();
    }
    
    public static SchemaCompiler createSchemaCompiler() {
        return new SchemaCompilerImpl();
    }
    
    public static String getDefaultPackageName(final String namespaceUri) {
        if (namespaceUri == null) {
            throw new IllegalArgumentException();
        }
        return NameConverter.standard.toPackageName(namespaceUri);
    }
}
