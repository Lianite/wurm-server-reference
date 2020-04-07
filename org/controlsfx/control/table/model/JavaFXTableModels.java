// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.table.model;

import java.util.Iterator;
import java.util.List;
import java.util.Comparator;
import javax.swing.SortOrder;
import javafx.scene.control.TableColumn;
import javax.swing.RowSorter;
import java.util.ArrayList;
import javafx.scene.control.TableView;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableModel;

class JavaFXTableModels
{
    public static <S> JavaFXTableModel<S> wrap(final TableModel tableModel) {
        return new JavaFXTableModel<S>() {
            final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);
            
            @Override
            public S getValueAt(final int rowIndex, final int columnIndex) {
                return (S)tableModel.getValueAt(this.sorter.convertRowIndexToView(rowIndex), columnIndex);
            }
            
            @Override
            public void setValueAt(final S value, final int rowIndex, final int columnIndex) {
                tableModel.setValueAt(value, rowIndex, columnIndex);
            }
            
            @Override
            public int getRowCount() {
                return tableModel.getRowCount();
            }
            
            @Override
            public int getColumnCount() {
                return tableModel.getColumnCount();
            }
            
            @Override
            public String getColumnName(final int columnIndex) {
                return tableModel.getColumnName(columnIndex);
            }
            
            @Override
            public void sort(final TableView<TableModelRow<S>> table) {
                final List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
                for (final TableColumn<TableModelRow<S>, ?> column : table.getSortOrder()) {
                    final int columnIndex = table.getVisibleLeafIndex((TableColumn)column);
                    final TableColumn.SortType sortType = column.getSortType();
                    final SortOrder sortOrder = (sortType == TableColumn.SortType.ASCENDING) ? SortOrder.ASCENDING : ((sortType == TableColumn.SortType.DESCENDING) ? SortOrder.DESCENDING : SortOrder.UNSORTED);
                    final RowSorter.SortKey sortKey = new RowSorter.SortKey(columnIndex, sortOrder);
                    sortKeys.add(sortKey);
                    this.sorter.setComparator(columnIndex, column.getComparator());
                }
                this.sorter.setSortKeys(sortKeys);
                this.sorter.sort();
            }
        };
    }
}
