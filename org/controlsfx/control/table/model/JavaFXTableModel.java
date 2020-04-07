// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.table.model;

import javafx.scene.control.TableView;

interface JavaFXTableModel<T>
{
    T getValueAt(final int p0, final int p1);
    
    void setValueAt(final T p0, final int p1, final int p2);
    
    int getRowCount();
    
    int getColumnCount();
    
    String getColumnName(final int p0);
    
    void sort(final TableView<TableModelRow<T>> p0);
}
