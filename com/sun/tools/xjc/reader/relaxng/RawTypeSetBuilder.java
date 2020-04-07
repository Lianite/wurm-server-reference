// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.relaxng;

import com.sun.xml.xsom.XmlString;
import com.sun.tools.xjc.model.CNonElement;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.xml.bind.v2.model.core.ID;
import org.kohsuke.rngom.digested.DOneOrMorePattern;
import org.kohsuke.rngom.digested.DZeroOrMorePattern;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import org.kohsuke.rngom.digested.DElementPattern;
import org.kohsuke.rngom.digested.DAttributePattern;
import java.util.HashSet;
import org.kohsuke.rngom.digested.DPatternVisitor;
import org.kohsuke.rngom.digested.DPattern;
import com.sun.tools.xjc.reader.RawTypeSet;
import java.util.Set;
import com.sun.tools.xjc.model.Multiplicity;
import org.kohsuke.rngom.digested.DPatternWalker;

public final class RawTypeSetBuilder extends DPatternWalker
{
    private Multiplicity mul;
    private final Set<RawTypeSet.Ref> refs;
    private final RELAXNGCompiler compiler;
    
    public static RawTypeSet build(final RELAXNGCompiler compiler, final DPattern contentModel, final Multiplicity mul) {
        final RawTypeSetBuilder builder = new RawTypeSetBuilder(compiler, mul);
        contentModel.accept((DPatternVisitor<Object>)builder);
        return builder.create();
    }
    
    public RawTypeSetBuilder(final RELAXNGCompiler compiler, final Multiplicity mul) {
        this.refs = new HashSet<RawTypeSet.Ref>();
        this.mul = mul;
        this.compiler = compiler;
    }
    
    private RawTypeSet create() {
        return new RawTypeSet(this.refs, this.mul);
    }
    
    public Void onAttribute(final DAttributePattern p) {
        return null;
    }
    
    public Void onElement(final DElementPattern p) {
        final CTypeInfo[] tis = this.compiler.classes.get(p);
        if (tis != null) {
            for (final CTypeInfo ti : tis) {
                this.refs.add(new CClassInfoRef((CClassInfo)ti));
            }
        }
        else {
            assert false;
        }
        return null;
    }
    
    public Void onZeroOrMore(final DZeroOrMorePattern p) {
        this.mul = this.mul.makeRepeated();
        return super.onZeroOrMore(p);
    }
    
    public Void onOneOrMore(final DOneOrMorePattern p) {
        this.mul = this.mul.makeRepeated();
        return super.onOneOrMore(p);
    }
    
    private static final class CClassInfoRef extends RawTypeSet.Ref
    {
        private final CClassInfo ci;
        
        CClassInfoRef(final CClassInfo ci) {
            this.ci = ci;
            assert ci.isElement();
        }
        
        protected ID id() {
            return ID.NONE;
        }
        
        protected boolean isListOfValues() {
            return false;
        }
        
        protected RawTypeSet.Mode canBeType(final RawTypeSet parent) {
            return RawTypeSet.Mode.SHOULD_BE_TYPEREF;
        }
        
        protected void toElementRef(final CReferencePropertyInfo prop) {
            prop.getElements().add(this.ci);
        }
        
        protected CTypeRef toTypeRef(final CElementPropertyInfo ep) {
            return new CTypeRef(this.ci, this.ci.getElementName(), this.ci.getTypeName(), false, null);
        }
    }
}
