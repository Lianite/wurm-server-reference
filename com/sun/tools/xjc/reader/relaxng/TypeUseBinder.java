// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.relaxng;

import org.kohsuke.rngom.digested.DMixedPattern;
import org.kohsuke.rngom.digested.DElementPattern;
import org.kohsuke.rngom.digested.DAttributePattern;
import org.kohsuke.rngom.digested.DTextPattern;
import org.kohsuke.rngom.digested.DRefPattern;
import org.kohsuke.rngom.digested.DOptionalPattern;
import org.kohsuke.rngom.digested.DZeroOrMorePattern;
import com.sun.tools.xjc.model.TypeUseFactory;
import org.kohsuke.rngom.digested.DOneOrMorePattern;
import org.kohsuke.rngom.digested.DListPattern;
import org.kohsuke.rngom.digested.DEmptyPattern;
import org.kohsuke.rngom.digested.DNotAllowedPattern;
import java.util.Iterator;
import org.kohsuke.rngom.digested.DPattern;
import org.kohsuke.rngom.digested.DGroupPattern;
import org.kohsuke.rngom.digested.DContainerPattern;
import org.kohsuke.rngom.digested.DInterleavePattern;
import org.kohsuke.rngom.digested.DValuePattern;
import org.kohsuke.rngom.digested.DDataPattern;
import org.kohsuke.rngom.digested.DChoicePattern;
import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import org.kohsuke.rngom.digested.DGrammarPattern;
import com.sun.tools.xjc.model.TypeUse;
import org.kohsuke.rngom.digested.DPatternVisitor;

final class TypeUseBinder implements DPatternVisitor<TypeUse>
{
    private final RELAXNGCompiler compiler;
    
    public TypeUseBinder(final RELAXNGCompiler compiler) {
        this.compiler = compiler;
    }
    
    public TypeUse onGrammar(final DGrammarPattern p) {
        return CBuiltinLeafInfo.STRING;
    }
    
    public TypeUse onChoice(final DChoicePattern p) {
        return CBuiltinLeafInfo.STRING;
    }
    
    public TypeUse onData(final DDataPattern p) {
        return this.onDataType(p.getDatatypeLibrary(), p.getType());
    }
    
    public TypeUse onValue(final DValuePattern p) {
        return this.onDataType(p.getDatatypeLibrary(), p.getType());
    }
    
    private TypeUse onDataType(final String datatypeLibrary, final String type) {
        final DatatypeLib lib = this.compiler.datatypes.get(datatypeLibrary);
        if (lib != null) {
            final TypeUse use = lib.get(type);
            if (use != null) {
                return use;
            }
        }
        return CBuiltinLeafInfo.STRING;
    }
    
    public TypeUse onInterleave(final DInterleavePattern p) {
        return this.onContainer(p);
    }
    
    public TypeUse onGroup(final DGroupPattern p) {
        return this.onContainer(p);
    }
    
    private TypeUse onContainer(final DContainerPattern p) {
        TypeUse t = null;
        for (final DPattern child : p) {
            final TypeUse s = child.accept((DPatternVisitor<TypeUse>)this);
            if (t != null && t != s) {
                return CBuiltinLeafInfo.STRING;
            }
            t = s;
        }
        return t;
    }
    
    public TypeUse onNotAllowed(final DNotAllowedPattern p) {
        return this.error();
    }
    
    public TypeUse onEmpty(final DEmptyPattern p) {
        return CBuiltinLeafInfo.STRING;
    }
    
    public TypeUse onList(final DListPattern p) {
        return p.getChild().accept((DPatternVisitor<TypeUse>)this);
    }
    
    public TypeUse onOneOrMore(final DOneOrMorePattern p) {
        return TypeUseFactory.makeCollection(p.getChild().accept((DPatternVisitor<TypeUse>)this));
    }
    
    public TypeUse onZeroOrMore(final DZeroOrMorePattern p) {
        return TypeUseFactory.makeCollection(p.getChild().accept((DPatternVisitor<TypeUse>)this));
    }
    
    public TypeUse onOptional(final DOptionalPattern p) {
        return CBuiltinLeafInfo.STRING;
    }
    
    public TypeUse onRef(final DRefPattern p) {
        return p.getTarget().getPattern().accept((DPatternVisitor<TypeUse>)this);
    }
    
    public TypeUse onText(final DTextPattern p) {
        return CBuiltinLeafInfo.STRING;
    }
    
    public TypeUse onAttribute(final DAttributePattern p) {
        return this.error();
    }
    
    public TypeUse onElement(final DElementPattern p) {
        return this.error();
    }
    
    public TypeUse onMixed(final DMixedPattern p) {
        return this.error();
    }
    
    private TypeUse error() {
        throw new IllegalStateException();
    }
}
