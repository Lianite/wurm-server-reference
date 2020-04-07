// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.binary;

final class PatternInterner
{
    private static final int INIT_SIZE = 256;
    private static final float LOAD_FACTOR = 0.3f;
    private Pattern[] table;
    private int used;
    private int usedLimit;
    
    PatternInterner() {
        this.table = null;
        this.used = 0;
        this.usedLimit = 0;
    }
    
    PatternInterner(final PatternInterner parent) {
        this.table = parent.table;
        if (this.table != null) {
            this.table = this.table.clone();
        }
        this.used = parent.used;
        this.usedLimit = parent.usedLimit;
    }
    
    Pattern intern(final Pattern p) {
        int h;
        if (this.table == null) {
            this.table = new Pattern[256];
            this.usedLimit = 76;
            h = this.firstIndex(p);
        }
        else {
            for (h = this.firstIndex(p); this.table[h] != null; h = this.nextIndex(h)) {
                if (p.samePattern(this.table[h])) {
                    return this.table[h];
                }
            }
        }
        if (this.used >= this.usedLimit) {
            final Pattern[] oldTable = this.table;
            this.table = new Pattern[this.table.length << 1];
            int i = oldTable.length;
            while (i > 0) {
                --i;
                if (oldTable[i] != null) {
                    int j;
                    for (j = this.firstIndex(oldTable[i]); this.table[j] != null; j = this.nextIndex(j)) {}
                    this.table[j] = oldTable[i];
                }
            }
            for (h = this.firstIndex(p); this.table[h] != null; h = this.nextIndex(h)) {}
            this.usedLimit = (int)(this.table.length * 0.3f);
        }
        ++this.used;
        return this.table[h] = p;
    }
    
    private int firstIndex(final Pattern p) {
        return p.patternHashCode() & this.table.length - 1;
    }
    
    private int nextIndex(final int i) {
        return (i == 0) ? (this.table.length - 1) : (i - 1);
    }
}
