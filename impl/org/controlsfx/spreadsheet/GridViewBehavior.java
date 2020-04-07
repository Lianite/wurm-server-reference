// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.spreadsheet;

import javafx.scene.control.TableSelectionModel;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableFocusModel;
import javafx.scene.control.TableColumnBase;
import javafx.util.Pair;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import javafx.collections.ObservableList;
import com.sun.javafx.scene.control.behavior.TableViewBehavior;

public class GridViewBehavior extends TableViewBehavior<ObservableList<SpreadsheetCell>>
{
    private GridViewSkin skin;
    
    public GridViewBehavior(final TableView<ObservableList<SpreadsheetCell>> control) {
        super((TableView)control);
    }
    
    void setGridViewSkin(final GridViewSkin skin) {
        this.skin = skin;
    }
    
    protected void updateCellVerticalSelection(final int delta, final Runnable defaultAction) {
        final TableViewSpanSelectionModel sm = (TableViewSpanSelectionModel)this.getSelectionModel();
        if (sm == null || sm.getSelectionMode() == SelectionMode.SINGLE) {
            return;
        }
        final TableFocusModel fm = this.getFocusModel();
        if (fm == null) {
            return;
        }
        final TablePositionBase focusedCell = this.getFocusedCell();
        if (this.isShiftDown && this.getAnchor() != null) {
            final SpreadsheetCell cell = (SpreadsheetCell)focusedCell.getTableColumn().getCellData(focusedCell.getRow());
            sm.direction = (Pair<Integer, Integer>)new Pair((Object)delta, (Object)0);
            int newRow;
            if (delta < 0) {
                newRow = this.skin.getFirstRow(cell, focusedCell.getRow()) + delta;
            }
            else {
                newRow = focusedCell.getRow() + this.skin.spreadsheetView.getRowSpan(cell, focusedCell.getRow()) - 1 + delta;
            }
            newRow = Math.max(Math.min(this.getItemCount() - 1, newRow), 0);
            final TablePositionBase<?> anchor = (TablePositionBase<?>)this.getAnchor();
            final int minRow = Math.min(anchor.getRow(), newRow);
            final int maxRow = Math.max(anchor.getRow(), newRow);
            final int minColumn = Math.min(anchor.getColumn(), focusedCell.getColumn());
            final int maxColumn = Math.max(anchor.getColumn(), focusedCell.getColumn());
            sm.clearSelection();
            if (minColumn != -1 && maxColumn != -1) {
                sm.selectRange(minRow, (TableColumnBase<ObservableList<SpreadsheetCell>, ?>)((TableView)this.getControl()).getVisibleLeafColumn(minColumn), maxRow, (TableColumnBase<ObservableList<SpreadsheetCell>, ?>)((TableView)this.getControl()).getVisibleLeafColumn(maxColumn));
            }
            fm.focus(newRow, focusedCell.getTableColumn());
        }
        else {
            final int focusIndex = fm.getFocusedIndex();
            if (!sm.isSelected(focusIndex, focusedCell.getTableColumn())) {
                sm.select(focusIndex, focusedCell.getTableColumn());
            }
            defaultAction.run();
        }
    }
    
    protected void updateCellHorizontalSelection(final int delta, final Runnable defaultAction) {
        final TableViewSpanSelectionModel sm = (TableViewSpanSelectionModel)this.getSelectionModel();
        if (sm == null || sm.getSelectionMode() == SelectionMode.SINGLE) {
            return;
        }
        final TableFocusModel fm = this.getFocusModel();
        if (fm == null) {
            return;
        }
        final TablePositionBase focusedCell = this.getFocusedCell();
        if (focusedCell == null || focusedCell.getTableColumn() == null) {
            return;
        }
        final TableColumnBase adjacentColumn = this.getColumn(focusedCell.getTableColumn(), delta);
        if (adjacentColumn == null) {
            return;
        }
        final int focusedCellRow = focusedCell.getRow();
        if (this.isShiftDown && this.getAnchor() != null) {
            final SpreadsheetCell cell = (SpreadsheetCell)focusedCell.getTableColumn().getCellData(focusedCell.getRow());
            sm.direction = (Pair<Integer, Integer>)new Pair((Object)0, (Object)delta);
            int newColumn;
            if (delta < 0) {
                newColumn = this.skin.spreadsheetView.getViewColumn(cell.getColumn()) + delta;
            }
            else {
                newColumn = this.skin.spreadsheetView.getViewColumn(cell.getColumn()) + this.skin.spreadsheetView.getColumnSpan(cell) - 1 + delta;
            }
            final TablePositionBase<?> anchor = (TablePositionBase<?>)this.getAnchor();
            final int minRow = Math.min(anchor.getRow(), focusedCellRow);
            final int maxRow = Math.max(anchor.getRow(), focusedCellRow);
            final int minColumn = Math.min(anchor.getColumn(), newColumn);
            final int maxColumn = Math.max(anchor.getColumn(), newColumn);
            sm.clearSelection();
            if (minColumn != -1 && maxColumn != -1) {
                sm.selectRange(minRow, (TableColumnBase<ObservableList<SpreadsheetCell>, ?>)((TableView)this.getControl()).getVisibleLeafColumn(minColumn), maxRow, (TableColumnBase<ObservableList<SpreadsheetCell>, ?>)((TableView)this.getControl()).getVisibleLeafColumn(maxColumn));
            }
            fm.focus(focusedCell.getRow(), this.getColumn(newColumn));
        }
        else {
            defaultAction.run();
        }
    }
    
    protected void focusPreviousRow() {
        this.focusVertical(true);
    }
    
    protected void focusNextRow() {
        this.focusVertical(false);
    }
    
    protected void focusLeftCell() {
        this.focusHorizontal(true);
    }
    
    protected void focusRightCell() {
        this.focusHorizontal(false);
    }
    
    protected void discontinuousSelectPreviousRow() {
        this.discontinuousSelectVertical(true);
    }
    
    protected void discontinuousSelectNextRow() {
        this.discontinuousSelectVertical(false);
    }
    
    protected void discontinuousSelectPreviousColumn() {
        this.discontinuousSelectHorizontal(true);
    }
    
    protected void discontinuousSelectNextColumn() {
        this.discontinuousSelectHorizontal(false);
    }
    
    private void focusVertical(final boolean previous) {
        final TableSelectionModel sm = this.getSelectionModel();
        if (sm == null || sm.getSelectionMode() == SelectionMode.SINGLE) {
            return;
        }
        final TableFocusModel fm = this.getFocusModel();
        if (fm == null) {
            return;
        }
        final TablePositionBase focusedCell = this.getFocusedCell();
        if (focusedCell == null || focusedCell.getTableColumn() == null) {
            return;
        }
        final SpreadsheetCell cell = (SpreadsheetCell)focusedCell.getTableColumn().getCellData(focusedCell.getRow());
        sm.clearAndSelect(previous ? this.findPreviousRow(focusedCell, cell) : this.findNextRow(focusedCell, cell), focusedCell.getTableColumn());
        this.skin.focusScroll();
    }
    
    private void focusHorizontal(final boolean previous) {
        final TableSelectionModel sm = this.getSelectionModel();
        if (sm == null) {
            return;
        }
        final TableFocusModel fm = this.getFocusModel();
        if (fm == null) {
            return;
        }
        final TablePositionBase focusedCell = this.getFocusedCell();
        if (focusedCell == null || focusedCell.getTableColumn() == null) {
            return;
        }
        final SpreadsheetCell cell = (SpreadsheetCell)focusedCell.getTableColumn().getCellData(focusedCell.getRow());
        sm.clearAndSelect(focusedCell.getRow(), (TableColumnBase)((TableView)this.getControl()).getVisibleLeafColumn(previous ? this.findPreviousColumn(focusedCell, cell) : this.findNextColumn(focusedCell, cell)));
        this.skin.focusScroll();
    }
    
    private int findPreviousRow(final TablePositionBase focusedCell, final SpreadsheetCell cell) {
        final ObservableList<ObservableList<SpreadsheetCell>> items = (ObservableList<ObservableList<SpreadsheetCell>>)((TableView)this.getControl()).getItems();
        if (this.isEmpty(cell)) {
            for (int row = focusedCell.getRow() - 1; row >= 0; --row) {
                final SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(row)).get(focusedCell.getColumn());
                if (!this.isEmpty(temp)) {
                    return row;
                }
            }
        }
        else if (focusedCell.getRow() - 1 >= 0 && !this.isEmpty((SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow() - 1)).get(focusedCell.getColumn()))) {
            for (int row = focusedCell.getRow() - 2; row >= 0; --row) {
                final SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(row)).get(focusedCell.getColumn());
                if (this.isEmpty(temp)) {
                    return row + 1;
                }
            }
        }
        else {
            for (int row = focusedCell.getRow() - 2; row >= 0; --row) {
                final SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(row)).get(focusedCell.getColumn());
                if (!this.isEmpty(temp)) {
                    return row;
                }
            }
        }
        return 0;
    }
    
    public void selectCell(final int rowDiff, final int columnDiff) {
        final TableViewSpanSelectionModel sm = (TableViewSpanSelectionModel)this.getSelectionModel();
        if (sm == null) {
            return;
        }
        sm.direction = (Pair<Integer, Integer>)new Pair((Object)rowDiff, (Object)columnDiff);
        final TableFocusModel fm = this.getFocusModel();
        if (fm == null) {
            return;
        }
        final TablePositionBase focusedCell = this.getFocusedCell();
        final int currentRow = focusedCell.getRow();
        final int currentColumn = this.getVisibleLeafIndex(focusedCell.getTableColumn());
        if (rowDiff < 0 && currentRow <= 0) {
            return;
        }
        if (rowDiff > 0 && currentRow >= this.getItemCount() - 1) {
            return;
        }
        if (columnDiff < 0 && currentColumn <= 0) {
            return;
        }
        if (columnDiff > 0 && currentColumn >= this.getVisibleLeafColumns().size() - 1) {
            return;
        }
        if (columnDiff > 0 && currentColumn == -1) {
            return;
        }
        TableColumnBase tc = focusedCell.getTableColumn();
        tc = this.getColumn(tc, columnDiff);
        final int row = focusedCell.getRow() + rowDiff;
        sm.clearAndSelect(row, tc);
        this.setAnchor(row, tc);
    }
    
    private int findNextRow(final TablePositionBase focusedCell, final SpreadsheetCell cell) {
        final ObservableList<ObservableList<SpreadsheetCell>> items = (ObservableList<ObservableList<SpreadsheetCell>>)((TableView)this.getControl()).getItems();
        final int itemCount = this.getItemCount();
        if (this.isEmpty(cell)) {
            for (int row = focusedCell.getRow() + 1; row < itemCount; ++row) {
                final SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(row)).get(focusedCell.getColumn());
                if (!this.isEmpty(temp)) {
                    return row;
                }
            }
        }
        else if (focusedCell.getRow() + 1 < itemCount && !this.isEmpty((SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow() + 1)).get(focusedCell.getColumn()))) {
            for (int row = focusedCell.getRow() + 2; row < this.getItemCount(); ++row) {
                final SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(row)).get(focusedCell.getColumn());
                if (this.isEmpty(temp)) {
                    return row - 1;
                }
            }
        }
        else {
            for (int row = focusedCell.getRow() + 2; row < itemCount; ++row) {
                final SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(row)).get(focusedCell.getColumn());
                if (!this.isEmpty(temp)) {
                    return row;
                }
            }
        }
        return itemCount - 1;
    }
    
    private void discontinuousSelectVertical(final boolean previous) {
        final TableSelectionModel sm = this.getSelectionModel();
        if (sm == null) {
            return;
        }
        final TableFocusModel fm = this.getFocusModel();
        if (fm == null) {
            return;
        }
        final TablePositionBase focusedCell = this.getFocusedCell();
        if (focusedCell == null || focusedCell.getTableColumn() == null) {
            return;
        }
        final SpreadsheetCell cell = (SpreadsheetCell)focusedCell.getTableColumn().getCellData(focusedCell.getRow());
        int newRow = previous ? this.findPreviousRow(focusedCell, cell) : this.findNextRow(focusedCell, cell);
        newRow = Math.max(Math.min(this.getItemCount() - 1, newRow), 0);
        final TablePositionBase<?> anchor = (TablePositionBase<?>)this.getAnchor();
        final int minRow = Math.min(anchor.getRow(), newRow);
        final int maxRow = Math.max(anchor.getRow(), newRow);
        final int minColumn = Math.min(anchor.getColumn(), focusedCell.getColumn());
        final int maxColumn = Math.max(anchor.getColumn(), focusedCell.getColumn());
        sm.clearSelection();
        if (minColumn != -1 && maxColumn != -1) {
            sm.selectRange(minRow, (TableColumnBase)((TableView)this.getControl()).getVisibleLeafColumn(minColumn), maxRow, (TableColumnBase)((TableView)this.getControl()).getVisibleLeafColumn(maxColumn));
        }
        fm.focus(newRow, focusedCell.getTableColumn());
        this.skin.focusScroll();
    }
    
    private void discontinuousSelectHorizontal(final boolean previous) {
        final TableSelectionModel sm = this.getSelectionModel();
        if (sm == null) {
            return;
        }
        final TableFocusModel fm = this.getFocusModel();
        if (fm == null) {
            return;
        }
        final TablePositionBase focusedCell = this.getFocusedCell();
        if (focusedCell == null || focusedCell.getTableColumn() == null) {
            return;
        }
        final int columnPos = this.getVisibleLeafIndex(focusedCell.getTableColumn());
        final int focusedCellRow = focusedCell.getRow();
        final SpreadsheetCell cell = (SpreadsheetCell)focusedCell.getTableColumn().getCellData(focusedCell.getRow());
        final int newColumn = previous ? this.findPreviousColumn(focusedCell, cell) : this.findNextColumn(focusedCell, cell);
        final TablePositionBase<?> anchor = (TablePositionBase<?>)this.getAnchor();
        final int minRow = Math.min(anchor.getRow(), focusedCellRow);
        final int maxRow = Math.max(anchor.getRow(), focusedCellRow);
        final int minColumn = Math.min(anchor.getColumn(), newColumn);
        final int maxColumn = Math.max(anchor.getColumn(), newColumn);
        sm.clearSelection();
        if (minColumn != -1 && maxColumn != -1) {
            sm.selectRange(minRow, (TableColumnBase)((TableView)this.getControl()).getVisibleLeafColumn(minColumn), maxRow, (TableColumnBase)((TableView)this.getControl()).getVisibleLeafColumn(maxColumn));
        }
        fm.focus(focusedCell.getRow(), this.getColumn(newColumn));
        this.skin.focusScroll();
    }
    
    private int findNextColumn(final TablePositionBase focusedCell, final SpreadsheetCell cell) {
        final ObservableList<ObservableList<SpreadsheetCell>> items = (ObservableList<ObservableList<SpreadsheetCell>>)((TableView)this.getControl()).getItems();
        final int itemCount = ((TableView)this.getControl()).getColumns().size();
        if (this.isEmpty(cell)) {
            for (int column = focusedCell.getColumn() + 1; column < itemCount; ++column) {
                final SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow())).get(column);
                if (!this.isEmpty(temp)) {
                    return column;
                }
            }
        }
        else if (focusedCell.getColumn() + 1 < itemCount && !this.isEmpty((SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow())).get(focusedCell.getColumn() + 1))) {
            for (int column = focusedCell.getColumn() + 2; column < itemCount; ++column) {
                final SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow())).get(column);
                if (this.isEmpty(temp)) {
                    return column - 1;
                }
            }
        }
        else {
            for (int column = focusedCell.getColumn() + 2; column < itemCount; ++column) {
                final SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow())).get(column);
                if (!this.isEmpty(temp)) {
                    return column;
                }
            }
        }
        return itemCount - 1;
    }
    
    private int findPreviousColumn(final TablePositionBase focusedCell, final SpreadsheetCell cell) {
        final ObservableList<ObservableList<SpreadsheetCell>> items = (ObservableList<ObservableList<SpreadsheetCell>>)((TableView)this.getControl()).getItems();
        if (this.isEmpty(cell)) {
            for (int column = focusedCell.getColumn() - 1; column >= 0; --column) {
                final SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow())).get(column);
                if (!this.isEmpty(temp)) {
                    return column;
                }
            }
        }
        else if (focusedCell.getColumn() - 1 >= 0 && !this.isEmpty((SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow())).get(focusedCell.getColumn() - 1))) {
            for (int column = focusedCell.getColumn() - 2; column >= 0; --column) {
                final SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow())).get(column);
                if (this.isEmpty(temp)) {
                    return column + 1;
                }
            }
        }
        else {
            for (int column = focusedCell.getColumn() - 2; column >= 0; --column) {
                final SpreadsheetCell temp = (SpreadsheetCell)((ObservableList)items.get(focusedCell.getRow())).get(column);
                if (!this.isEmpty(temp)) {
                    return column;
                }
            }
        }
        return 0;
    }
    
    private boolean isEmpty(final SpreadsheetCell cell) {
        return cell.getGraphic() == null && (cell.getItem() == null || (cell.getItem() instanceof Double && ((Double)cell.getItem()).isNaN()));
    }
}
