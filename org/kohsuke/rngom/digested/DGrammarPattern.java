// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public class DGrammarPattern extends DPattern implements Iterable<DDefine>
{
    private final Map<String, DDefine> patterns;
    DPattern start;
    
    public DGrammarPattern() {
        this.patterns = new HashMap<String, DDefine>();
    }
    
    public DPattern getStart() {
        return this.start;
    }
    
    public DDefine get(final String name) {
        return this.patterns.get(name);
    }
    
    DDefine getOrAdd(final String name) {
        if (this.patterns.containsKey(name)) {
            return this.get(name);
        }
        final DDefine d = new DDefine(name);
        this.patterns.put(name, d);
        return d;
    }
    
    public Iterator<DDefine> iterator() {
        return this.patterns.values().iterator();
    }
    
    public boolean isNullable() {
        return this.start.isNullable();
    }
    
    public <V> V accept(final DPatternVisitor<V> visitor) {
        return visitor.onGrammar(this);
    }
}
