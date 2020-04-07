// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator;

import com.sun.msv.grammar.ChoiceExp;
import java.util.HashMap;
import java.util.Map;

class LookupTableCache implements LookupTableBuilder
{
    private final Map cache;
    private final LookupTableBuilder core;
    
    public LookupTableCache(final LookupTableBuilder _core) {
        this.cache = new HashMap();
        this.core = _core;
    }
    
    public LookupTableUse buildTable(final ChoiceExp exp) {
        if (this.cache.containsKey(exp)) {
            return this.cache.get(exp);
        }
        final LookupTableUse t = this.core.buildTable(exp);
        this.cache.put(exp, t);
        return t;
    }
}
