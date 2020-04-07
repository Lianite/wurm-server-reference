// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.relaxng;

import org.kohsuke.rngom.digested.DRefPattern;
import java.util.Iterator;
import org.kohsuke.rngom.digested.DPatternVisitor;
import org.kohsuke.rngom.digested.DGrammarPattern;
import java.util.HashSet;
import org.kohsuke.rngom.digested.DDefine;
import java.util.Set;
import org.kohsuke.rngom.digested.DPatternWalker;

final class DefineFinder extends DPatternWalker
{
    public final Set<DDefine> defs;
    
    DefineFinder() {
        this.defs = new HashSet<DDefine>();
    }
    
    public Void onGrammar(final DGrammarPattern p) {
        for (final DDefine def : p) {
            this.defs.add(def);
            def.getPattern().accept((DPatternVisitor<Object>)this);
        }
        return p.getStart().accept((DPatternVisitor<Void>)this);
    }
    
    public Void onRef(final DRefPattern p) {
        return null;
    }
}
