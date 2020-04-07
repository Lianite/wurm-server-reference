// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.model;

import org.xml.sax.Locator;
import com.sun.tools.xjc.model.nav.NavigatorImpl;
import org.w3c.dom.Element;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.XSComponent;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.xml.bind.v2.model.core.WildcardTypeInfo;

public final class CWildcardTypeInfo extends AbstractCTypeInfoImpl implements WildcardTypeInfo<NType, NClass>
{
    public static final CWildcardTypeInfo INSTANCE;
    
    private CWildcardTypeInfo() {
        super(null, null, null);
    }
    
    public JType toType(final Outline o, final Aspect aspect) {
        return o.getCodeModel().ref(Element.class);
    }
    
    public NType getType() {
        return NavigatorImpl.create(Element.class);
    }
    
    public Locator getLocator() {
        return Model.EMPTY_LOCATOR;
    }
    
    static {
        INSTANCE = new CWildcardTypeInfo();
    }
}
