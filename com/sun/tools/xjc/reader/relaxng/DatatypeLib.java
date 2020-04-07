// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.relaxng;

import com.sun.tools.xjc.reader.xmlschema.SimpleTypeBuilder;
import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import java.util.HashMap;
import com.sun.tools.xjc.model.TypeUse;
import java.util.Map;

final class DatatypeLib
{
    public final String nsUri;
    private final Map<String, TypeUse> types;
    public static final DatatypeLib BUILTIN;
    public static final DatatypeLib XMLSCHEMA;
    
    public DatatypeLib(final String nsUri) {
        this.types = new HashMap<String, TypeUse>();
        this.nsUri = nsUri;
    }
    
    TypeUse get(final String name) {
        return this.types.get(name);
    }
    
    static {
        BUILTIN = new DatatypeLib("");
        XMLSCHEMA = new DatatypeLib("http://www.w3.org/2001/XMLSchema-datatypes");
        DatatypeLib.BUILTIN.types.put("token", CBuiltinLeafInfo.TOKEN);
        DatatypeLib.BUILTIN.types.put("string", CBuiltinLeafInfo.STRING);
        DatatypeLib.XMLSCHEMA.types.putAll(SimpleTypeBuilder.builtinConversions);
    }
}
