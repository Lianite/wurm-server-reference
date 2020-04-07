// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import javafx.collections.ObservableList;

public interface CheckModel<T>
{
    int getItemCount();
    
    ObservableList<T> getCheckedItems();
    
    void checkAll();
    
    void clearCheck(final T p0);
    
    void clearChecks();
    
    boolean isEmpty();
    
    boolean isChecked(final T p0);
    
    void check(final T p0);
}
