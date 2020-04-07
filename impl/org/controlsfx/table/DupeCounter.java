// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.table;

import java.util.Optional;
import java.util.HashMap;

final class DupeCounter<T>
{
    private final HashMap<T, Integer> counts;
    private final boolean enforceFloor;
    
    public DupeCounter(final boolean enforceFloor) {
        this.counts = new HashMap<T, Integer>();
        this.enforceFloor = enforceFloor;
    }
    
    public int add(final T value) {
        final Integer prev = this.counts.get(value);
        int newVal;
        if (prev == null) {
            newVal = 1;
            this.counts.put(value, newVal);
        }
        else {
            newVal = prev + 1;
            this.counts.put(value, newVal);
        }
        return newVal;
    }
    
    public int get(final T value) {
        return Optional.ofNullable(this.counts.get(value)).orElse(0);
    }
    
    public int remove(final T value) {
        final Integer prev = this.counts.get(value);
        if (prev != null && prev > 0) {
            final int newVal = prev - 1;
            if (newVal == 0) {
                this.counts.remove(value);
            }
            else {
                this.counts.put(value, newVal);
            }
            return newVal;
        }
        if (this.enforceFloor) {
            throw new IllegalStateException();
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return this.counts.toString();
    }
}
