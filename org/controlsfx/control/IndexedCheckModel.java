// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import javafx.collections.ObservableList;

public interface IndexedCheckModel<T> extends CheckModel<T>
{
    T getItem(final int p0);
    
    int getItemIndex(final T p0);
    
    ObservableList<Integer> getCheckedIndices();
    
    void checkIndices(final int... p0);
    
    void clearCheck(final int p0);
    
    boolean isChecked(final int p0);
    
    void check(final int p0);
}
