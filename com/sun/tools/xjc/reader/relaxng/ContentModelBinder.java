// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.relaxng;

import com.sun.tools.xjc.model.CAttributePropertyInfo;
import org.kohsuke.rngom.digested.DPatternVisitor;
import com.sun.tools.xjc.model.TypeUse;
import javax.xml.namespace.QName;
import org.kohsuke.rngom.digested.DAttributePattern;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.xml.xsom.XSComponent;
import javax.activation.MimeType;
import com.sun.xml.bind.v2.model.core.ID;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.reader.RawTypeSet;
import com.sun.tools.xjc.model.Multiplicity;
import org.kohsuke.rngom.digested.DOneOrMorePattern;
import org.kohsuke.rngom.digested.DPattern;
import org.kohsuke.rngom.digested.DZeroOrMorePattern;
import org.kohsuke.rngom.digested.DOptionalPattern;
import org.kohsuke.rngom.digested.DChoicePattern;
import org.kohsuke.rngom.digested.DMixedPattern;
import com.sun.tools.xjc.model.CClassInfo;
import org.kohsuke.rngom.digested.DPatternWalker;

final class ContentModelBinder extends DPatternWalker
{
    private final RELAXNGCompiler compiler;
    private final CClassInfo clazz;
    private boolean insideOptional;
    private int iota;
    
    public ContentModelBinder(final RELAXNGCompiler compiler, final CClassInfo clazz) {
        this.insideOptional = false;
        this.iota = 1;
        this.compiler = compiler;
        this.clazz = clazz;
    }
    
    public Void onMixed(final DMixedPattern p) {
        throw new UnsupportedOperationException();
    }
    
    public Void onChoice(final DChoicePattern p) {
        final boolean old = this.insideOptional;
        this.insideOptional = true;
        super.onChoice(p);
        this.insideOptional = old;
        return null;
    }
    
    public Void onOptional(final DOptionalPattern p) {
        final boolean old = this.insideOptional;
        this.insideOptional = true;
        super.onOptional(p);
        this.insideOptional = old;
        return null;
    }
    
    public Void onZeroOrMore(final DZeroOrMorePattern p) {
        return this.onRepeated(p, true);
    }
    
    public Void onOneOrMore(final DOneOrMorePattern p) {
        return this.onRepeated(p, this.insideOptional);
    }
    
    private Void onRepeated(final DPattern p, final boolean optional) {
        final RawTypeSet rts = RawTypeSetBuilder.build(this.compiler, p, optional ? Multiplicity.STAR : Multiplicity.PLUS);
        if (rts.canBeTypeRefs == RawTypeSet.Mode.SHOULD_BE_TYPEREF) {
            final CElementPropertyInfo prop = new CElementPropertyInfo(this.calcName(p), CElementPropertyInfo.CollectionMode.REPEATED_ELEMENT, ID.NONE, null, null, null, p.getLocation(), !optional);
            rts.addTo(prop);
            this.clazz.addProperty(prop);
        }
        else {
            final CReferencePropertyInfo prop2 = new CReferencePropertyInfo(this.calcName(p), true, false, null, null, p.getLocation());
            rts.addTo(prop2);
            this.clazz.addProperty(prop2);
        }
        return null;
    }
    
    public Void onAttribute(final DAttributePattern p) {
        final QName name = p.getName().listNames().iterator().next();
        final CAttributePropertyInfo ap = new CAttributePropertyInfo(this.calcName(p), null, null, p.getLocation(), name, p.getChild().accept((DPatternVisitor<TypeUse>)this.compiler.typeUseBinder), null, !this.insideOptional);
        this.clazz.addProperty(ap);
        return null;
    }
    
    private String calcName(final DPattern p) {
        return "field" + this.iota++;
    }
}
