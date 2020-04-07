// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator;

import com.sun.msv.grammar.ChoiceExp;
import java.util.ArrayList;
import java.util.List;

class LookupTableInterner implements LookupTableBuilder
{
    private final List liveTable;
    private final LookupTableBuilder core;
    
    public LookupTableInterner(final LookupTableBuilder _core) {
        this.liveTable = new ArrayList();
        this.core = _core;
    }
    
    public LookupTableUse buildTable(final ChoiceExp exp) {
        final LookupTableUse t = this.core.buildTable(exp);
        if (t == null) {
            return null;
        }
        return new LookupTableUse(this.intern(t.table), t.anomaly, t.switchAttName);
    }
    
    private LookupTable intern(final LookupTable t) {
        for (int i = 0; i < this.liveTable.size(); ++i) {
            final LookupTable a = this.liveTable.get(i);
            if (a.isConsistentWith(t)) {
                a.absorb(t);
                return a;
            }
        }
        this.liveTable.add(t);
        return t;
    }
    
    public LookupTable[] listTables() {
        return this.liveTable.toArray(new LookupTable[this.liveTable.size()]);
    }
}
