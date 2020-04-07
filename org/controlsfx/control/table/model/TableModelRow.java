// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.table.model;

class TableModelRow<S>
{
    private final int columnCount;
    private final JavaFXTableModel<S> tableModel;
    private final int row;
    
    TableModelRow(final JavaFXTableModel<S> tableModel, final int row) {
        this.row = row;
        this.tableModel = tableModel;
        this.columnCount = tableModel.getColumnCount();
    }
    
    public Object get(final int column) {
        return (column < 0 || column >= this.columnCount) ? null : this.tableModel.getValueAt(this.row, column);
    }
    
    @Override
    public String toString() {
        String text = "Row " + this.row + ": [ ";
        for (int col = 0; col < this.columnCount; ++col) {
            text += this.get(col);
            if (col < this.columnCount - 1) {
                text += ", ";
            }
        }
        text += " ]";
        return text;
    }
}
