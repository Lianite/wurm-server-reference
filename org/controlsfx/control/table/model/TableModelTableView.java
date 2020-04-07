// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.table.model;

import javafx.util.Callback;
import javafx.scene.control.TableColumn;
import javafx.collections.ObservableList;
import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import javafx.scene.control.TableView;

class TableModelTableView<S> extends TableView<TableModelRow<S>>
{
    public TableModelTableView(final JavaFXTableModel<S> tableModel) {
        this.setItems((ObservableList)new ReadOnlyUnbackedObservableList<TableModelRow<S>>() {
            public TableModelRow<S> get(final int row) {
                if (row < 0 || row >= tableModel.getRowCount()) {
                    return null;
                }
                final TableModelRow<S> backingRow = new TableModelRow<S>(tableModel, row);
                return backingRow;
            }
            
            public int size() {
                return tableModel.getRowCount();
            }
        });
        this.setSortPolicy(table -> {
            tableModel.sort(table);
            return true;
        });
        for (int i = 0; i < tableModel.getColumnCount(); ++i) {
            final TableColumn<TableModelRow<S>, ?> column = (TableColumn<TableModelRow<S>, ?>)new TableColumn(tableModel.getColumnName(i));
            column.setCellValueFactory((Callback)new TableModelValueFactory((JavaFXTableModel<Object>)tableModel, i));
            this.getColumns().add((Object)column);
        }
    }
}
