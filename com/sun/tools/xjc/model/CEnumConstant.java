// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import com.sun.xml.bind.v2.model.core.EnumLeafInfo;
import org.xml.sax.Locator;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.xml.bind.v2.model.core.EnumConstant;

public final class CEnumConstant implements EnumConstant<NType, NClass>
{
    public final String name;
    public final String javadoc;
    private final String lexical;
    private CEnumLeafInfo parent;
    private final Locator locator;
    
    public CEnumConstant(final String name, final String javadoc, final String lexical, final Locator loc) {
        assert name != null;
        this.name = name;
        this.javadoc = javadoc;
        this.lexical = lexical;
        this.locator = loc;
    }
    
    public CEnumLeafInfo getEnclosingClass() {
        return this.parent;
    }
    
    void setParent(final CEnumLeafInfo parent) {
        this.parent = parent;
    }
    
    public String getLexicalValue() {
        return this.lexical;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Locator getLocator() {
        return this.locator;
    }
}
