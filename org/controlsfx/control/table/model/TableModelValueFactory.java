// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.table.model;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

class TableModelValueFactory<S, T> implements Callback<TableColumn.CellDataFeatures<TableModelRow<S>, T>, ObservableValue<T>>
{
    private final JavaFXTableModel<S> _tableModel;
    private final int _columnIndex;
    
    public TableModelValueFactory(final JavaFXTableModel<S> tableModel, final int columnIndex) {
        this._tableModel = tableModel;
        this._columnIndex = columnIndex;
    }
    
    public ObservableValue<T> call(final TableColumn.CellDataFeatures<TableModelRow<S>, T> cdf) {
        final TableModelRow<S> row = (TableModelRow<S>)cdf.getValue();
        final T valueAt = (T)row.get(this._columnIndex);
        return (ObservableValue<T>)((valueAt instanceof ObservableValue) ? ((ObservableValue)valueAt) : new ReadOnlyObjectWrapper((Object)valueAt));
    }
}
