// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.generator;

import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.Expression;

public class LookupTableUse
{
    public final LookupTable table;
    public final Expression anomaly;
    public final SimpleNameClass switchAttName;
    
    LookupTableUse(final LookupTable _table, final Expression _anomaly, final SimpleNameClass _switchAttName) {
        this.table = _table;
        this.anomaly = _anomaly;
        this.switchAttName = _switchAttName;
    }
}
