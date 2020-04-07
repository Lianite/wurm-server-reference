// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.spreadsheet;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.beans.property.BooleanProperty;
import java.util.Collection;
import javafx.collections.ObservableList;

public interface Grid
{
    public static final double AUTOFIT = -1.0;
    
    int getRowCount();
    
    int getColumnCount();
    
    ObservableList<ObservableList<SpreadsheetCell>> getRows();
    
    void setCellValue(final int p0, final int p1, final Object p2);
    
    double getRowHeight(final int p0);
    
    boolean isRowResizable(final int p0);
    
    ObservableList<String> getRowHeaders();
    
    ObservableList<String> getColumnHeaders();
    
    void spanRow(final int p0, final int p1, final int p2);
    
    void spanColumn(final int p0, final int p1, final int p2);
    
    void setRows(final Collection<ObservableList<SpreadsheetCell>> p0);
    
    boolean isDisplaySelection();
    
    void setDisplaySelection(final boolean p0);
    
    BooleanProperty displaySelectionProperty();
    
    void setCellDisplaySelection(final int p0, final int p1, final boolean p2);
    
    boolean isCellDisplaySelection(final int p0, final int p1);
    
     <E extends GridChange> void addEventHandler(final EventType<E> p0, final EventHandler<E> p1);
    
     <E extends GridChange> void removeEventHandler(final EventType<E> p0, final EventHandler<E> p1);
}
