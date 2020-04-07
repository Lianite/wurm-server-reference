// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.parser;

import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSType;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;
import com.sun.xml.xsom.impl.Ref;

public final class BaseContentRef implements Ref.ContentType, Patch
{
    private final Ref.Type baseType;
    private final Locator loc;
    
    public BaseContentRef(final NGCCRuntimeEx $runtime, final Ref.Type _baseType) {
        this.baseType = _baseType;
        $runtime.addPatcher(this);
        $runtime.addErrorChecker(new Patch() {
            public void run() throws SAXException {
                final XSType t = BaseContentRef.this.baseType.getType();
                if (t.isComplexType() && t.asComplexType().getContentType().asParticle() != null) {
                    $runtime.reportError(Messages.format("SimpleContentExpected", t.getTargetNamespace(), t.getName()), BaseContentRef.this.loc);
                }
            }
        });
        this.loc = $runtime.copyLocator();
    }
    
    public XSContentType getContentType() {
        final XSType t = this.baseType.getType();
        if (t.asComplexType() != null) {
            return t.asComplexType().getContentType();
        }
        return t.asSimpleType();
    }
    
    public void run() throws SAXException {
        if (this.baseType instanceof Patch) {
            ((Patch)this.baseType).run();
        }
    }
}
