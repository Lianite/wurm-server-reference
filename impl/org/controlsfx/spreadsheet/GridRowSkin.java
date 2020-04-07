// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.spreadsheet;

import javafx.scene.Node;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import javafx.scene.layout.BorderStroke;
import java.util.Iterator;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.beans.value.ObservableValue;
import java.util.ArrayList;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import javafx.scene.control.Cell;
import com.sun.javafx.scene.control.behavior.TableRowBehavior;
import java.util.List;
import javafx.scene.control.TableColumnBase;
import java.util.HashMap;
import java.lang.ref.Reference;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import com.sun.javafx.scene.control.behavior.CellBehaviorBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import javafx.collections.ObservableList;
import javafx.scene.control.TableRow;
import com.sun.javafx.scene.control.skin.CellSkinBase;

public class GridRowSkin extends CellSkinBase<TableRow<ObservableList<SpreadsheetCell>>, CellBehaviorBase<TableRow<ObservableList<SpreadsheetCell>>>>
{
    private final SpreadsheetHandle handle;
    private final SpreadsheetView spreadsheetView;
    private Reference<HashMap<TableColumnBase, CellView>> cellsMap;
    private final List<CellView> cells;
    
    public GridRowSkin(final SpreadsheetHandle handle, final TableRow<ObservableList<SpreadsheetCell>> gridRow) {
        super((Cell)gridRow, (BehaviorBase)new TableRowBehavior((TableRow)gridRow));
        this.cells = new ArrayList<CellView>();
        this.handle = handle;
        this.spreadsheetView = handle.getView();
        ((TableRow)this.getSkinnable()).setPickOnBounds(false);
        this.registerChangeListener((ObservableValue)gridRow.itemProperty(), "ITEM");
        this.registerChangeListener((ObservableValue)gridRow.indexProperty(), "INDEX");
    }
    
    protected void handleControlPropertyChanged(final String p) {
        super.handleControlPropertyChanged(p);
        if ("INDEX".equals(p)) {
            if (((TableRow)this.getSkinnable()).isEmpty()) {
                this.requestCellUpdate();
            }
        }
        else if ("ITEM".equals(p)) {
            this.requestCellUpdate();
        }
        else if ("FIXED_CELL_SIZE".equals(p)) {}
    }
    
    private void requestCellUpdate() {
        ((TableRow)this.getSkinnable()).requestLayout();
        final int newIndex = ((TableRow)this.getSkinnable()).getIndex();
        this.getChildren().clear();
        for (int i = 0, max = this.cells.size(); i < max; ++i) {
            this.cells.get(i).updateIndex(newIndex);
        }
    }
    
    protected void layoutChildren(double x, final double y, final double w, final double h) {
        final ObservableList<? extends TableColumnBase<?, ?>> visibleLeafColumns = (ObservableList<? extends TableColumnBase<?, ?>>)this.handle.getGridView().getVisibleLeafColumns();
        if (visibleLeafColumns.isEmpty()) {
            super.layoutChildren(x, y, w, h);
            return;
        }
        final GridRow control = (GridRow)this.getSkinnable();
        final SpreadsheetGridView gridView = this.handle.getGridView();
        final Grid grid = this.spreadsheetView.getGrid();
        final int index = control.getIndex();
        if (index < 0 || index >= gridView.getItems().size()) {
            this.getChildren().clear();
            this.putCellsInCache();
            return;
        }
        final List<SpreadsheetCell> row = (List<SpreadsheetCell>)((TableRow)this.getSkinnable()).getItem();
        final List<SpreadsheetColumn> columns = (List<SpreadsheetColumn>)this.spreadsheetView.getColumns();
        final ObservableList<TableColumn<ObservableList<SpreadsheetCell>, ?>> tableViewColumns = (ObservableList<TableColumn<ObservableList<SpreadsheetCell>, ?>>)gridView.getColumns();
        if (columns.size() != tableViewColumns.size()) {
            return;
        }
        ((TableRow)this.getSkinnable()).setVisible(true);
        final double verticalPadding = this.snappedTopInset() + this.snappedBottomInset();
        final double horizontalPadding = this.snappedLeftInset() + this.snappedRightInset();
        final double controlHeight = this.getTableRowHeight(index);
        double customHeight = (controlHeight == -1.0) ? GridViewSkin.DEFAULT_CELL_HEIGHT : controlHeight;
        final GridViewSkin skin = this.handle.getCellsViewSkin();
        skin.hBarValue.set(index, true);
        final double headerWidth = gridView.getWidth();
        final double hbarValue = skin.getHBar().getValue();
        ((GridRow)this.getSkinnable()).verticalShift.setValue((Number)this.getFixedRowShift(index));
        double fixedColumnWidth = 0.0;
        final List<CellView> fixedCells = new ArrayList<CellView>();
        this.putCellsInCache();
        final boolean firstVisibleCell = false;
        CellView lastCell = null;
        boolean rowHeightChange = false;
        for (int indexColumn = 0; indexColumn < columns.size(); ++indexColumn) {
            if (((TableColumn)((TableView)skin.getSkinnable()).getColumns().get(indexColumn)).isVisible()) {
                double width = this.snapSize(columns.get(indexColumn).getWidth()) - this.snapSize(horizontalPadding);
                if (row.size() <= indexColumn) {
                    break;
                }
                final SpreadsheetCell spreadsheetCell = row.get(indexColumn);
                final int columnSpan = this.spreadsheetView.getColumnSpan(spreadsheetCell);
                boolean isVisible = !this.isInvisible(x, width, hbarValue, headerWidth, columnSpan);
                if (columns.get(indexColumn).isFixed()) {
                    isVisible = true;
                }
                if (!isVisible) {
                    if (firstVisibleCell) {
                        break;
                    }
                    x += width;
                }
                else {
                    final CellView tableCell = this.getCell((TableColumnBase)gridView.getColumns().get(indexColumn));
                    this.cells.add(0, tableCell);
                    tableCell.setManaged(true);
                    double tableCellX = 0.0;
                    boolean increaseFixedWidth = false;
                    final int viewColumn = this.spreadsheetView.getViewColumn(spreadsheetCell.getColumn());
                    if (columns.get(indexColumn).isFixed() && hbarValue + fixedColumnWidth > x && spreadsheetCell.getColumn() == indexColumn) {
                        increaseFixedWidth = true;
                        tableCellX = Math.abs(hbarValue - x + fixedColumnWidth);
                        fixedColumnWidth += width;
                        fixedCells.add(tableCell);
                    }
                    if (isVisible) {
                        final SpreadsheetView.SpanType spanType = this.spreadsheetView.getSpanType(index, indexColumn);
                        switch (spanType) {
                            case ROW_SPAN_INVISIBLE:
                            case BOTH_INVISIBLE: {
                                fixedCells.remove(tableCell);
                                this.getChildren().remove((Object)tableCell);
                                x += width;
                                continue;
                            }
                            case COLUMN_SPAN_INVISIBLE: {
                                fixedCells.remove(tableCell);
                                this.getChildren().remove((Object)tableCell);
                                continue;
                            }
                            case ROW_VISIBLE:
                            case NORMAL_CELL: {
                                if (tableCell.getIndex() != index) {
                                    tableCell.updateIndex(index);
                                }
                                else {
                                    tableCell.updateItem(spreadsheetCell, false);
                                }
                                if (!tableCell.isEditing() && tableCell.getParent() != this.getSkinnable()) {
                                    this.getChildren().add(0, (Object)tableCell);
                                    break;
                                }
                                break;
                            }
                        }
                        if (columnSpan > 1) {
                            for (int max = ((TableView)skin.getSkinnable()).getVisibleLeafColumns().size() - viewColumn, i = 1, colSpan = columnSpan; i < colSpan && i < max; ++i) {
                                final double tempWidth = this.snapSize(((TableView)skin.getSkinnable()).getVisibleLeafColumn(viewColumn + i).getWidth());
                                width += tempWidth;
                                if (increaseFixedWidth) {
                                    fixedColumnWidth += tempWidth;
                                }
                            }
                        }
                        if (controlHeight == -1.0 && !tableCell.isEditing()) {
                            final double tempHeight = tableCell.prefHeight(width) + tableCell.snappedTopInset() + tableCell.snappedBottomInset();
                            if (tempHeight > customHeight) {
                                rowHeightChange = true;
                                skin.rowHeightMap.put((Object)spreadsheetCell.getRow(), (Object)tempHeight);
                                for (final CellView cell : this.cells) {
                                    cell.resize(cell.getWidth(), cell.getHeight() + (tempHeight - customHeight));
                                }
                                customHeight = tempHeight;
                                skin.getFlow().layoutChildren();
                            }
                        }
                        double height = customHeight;
                        height = this.snapSize(height) - this.snapSize(verticalPadding);
                        final int rowSpan = this.spreadsheetView.getRowSpan(spreadsheetCell, index);
                        if (rowSpan > 1) {
                            height = 0.0;
                            for (int maxRow = index + rowSpan, j = index; j < maxRow; ++j) {
                                height += this.snapSize(skin.getRowHeight(j));
                            }
                        }
                        boolean needToBeShifted = false;
                        if (lastCell != null && !this.hasRightBorder(lastCell) && !this.hasLeftBorder(tableCell)) {
                            tableCell.resize(width + 1.0, height);
                            needToBeShifted = true;
                        }
                        else {
                            tableCell.resize(width, height);
                        }
                        lastCell = tableCell;
                        final double spaceBetweenTopAndMe = 0.0;
                        tableCell.relocate(x + tableCellX + (needToBeShifted ? -1 : 0), this.snappedTopInset() - spaceBetweenTopAndMe + ((GridRow)this.getSkinnable()).verticalShift.get());
                    }
                    else {
                        this.getChildren().remove((Object)tableCell);
                    }
                    x += width;
                }
            }
        }
        skin.fixedColumnWidth = fixedColumnWidth;
        this.handleFixedCell(fixedCells, index);
        this.removeUselessCell(index);
        if (this.handle.getCellsViewSkin().lastRowLayout.get()) {
            this.handle.getCellsViewSkin().lastRowLayout.setValue(false);
        }
        if (rowHeightChange && this.spreadsheetView.getFixedRows().contains((Object)this.spreadsheetView.getModelRow(index))) {
            skin.computeFixedRowHeight();
        }
    }
    
    private boolean hasRightBorder(final CellView tableCell) {
        return tableCell.getBorder() != null && !tableCell.getBorder().isEmpty() && tableCell.getBorder().getStrokes().get(0).getWidths().getRight() > 0.0;
    }
    
    private boolean hasLeftBorder(final CellView tableCell) {
        return tableCell.getBorder() != null && !tableCell.getBorder().isEmpty() && tableCell.getBorder().getStrokes().get(0).getWidths().getLeft() > 0.0;
    }
    
    private void removeUselessCell(final int index) {
        this.getChildren().removeIf(t -> {
            if (t instanceof CellView) {
                return !this.cells.contains(t) && t.getIndex() == index;
            }
            else {
                return false;
            }
        });
    }
    
    private void removeDeportedCells() {
        final GridViewSkin skin = this.handle.getCellsViewSkin();
        for (final Map.Entry<GridRow, Set<CellView>> entry : skin.deportedCells.entrySet()) {
            final ArrayList<CellView> toRemove = new ArrayList<CellView>();
            for (final CellView cell : entry.getValue()) {
                if (!cell.isEditing() && cell.getTableRow() == this.getSkinnable() && entry.getKey() != this.getSkinnable()) {
                    entry.getKey().removeCell(cell);
                    toRemove.add(cell);
                }
            }
            entry.getValue().removeAll(toRemove);
        }
    }
    
    private void handleFixedCell(final List<CellView> fixedCells, final int index) {
        this.removeDeportedCells();
        if (fixedCells.isEmpty()) {
            return;
        }
        final GridViewSkin skin = this.handle.getCellsViewSkin();
        if (skin.rowToLayout.get(index)) {
            final GridRow gridRow = skin.getFlow().getTopRow();
            if (gridRow != null) {
                for (final CellView cell : fixedCells) {
                    if (!cell.isEditing()) {
                        gridRow.removeCell(cell);
                        gridRow.addCell(cell);
                    }
                    final double originalLayoutY = ((TableRow)this.getSkinnable()).getLayoutY() + cell.getLayoutY();
                    if (skin.deportedCells.containsKey(gridRow)) {
                        skin.deportedCells.get(gridRow).add(cell);
                    }
                    else {
                        final Set<CellView> temp = new HashSet<CellView>();
                        temp.add(cell);
                        skin.deportedCells.put(gridRow, temp);
                    }
                    cell.relocate(cell.getLayoutX(), originalLayoutY - gridRow.getLayoutY());
                }
            }
        }
        else {
            for (final CellView cell2 : fixedCells) {
                cell2.toFront();
            }
        }
    }
    
    private HashMap<TableColumnBase, CellView> getCellsMap() {
        if (this.cellsMap == null || this.cellsMap.get() == null) {
            final HashMap<TableColumnBase, CellView> map = new HashMap<TableColumnBase, CellView>();
            this.cellsMap = new WeakReference<HashMap<TableColumnBase, CellView>>(map);
            return map;
        }
        return this.cellsMap.get();
    }
    
    private void putCellsInCache() {
        for (final CellView cell : this.cells) {
            this.getCellsMap().put((TableColumnBase)cell.getTableColumn(), cell);
        }
        this.cells.clear();
    }
    
    private CellView getCell(final TableColumnBase tcb) {
        final TableColumn tableColumn = (TableColumn)tcb;
        if (this.getCellsMap().containsKey(tableColumn)) {
            return this.getCellsMap().remove(tableColumn);
        }
        final CellView cell = (CellView)tableColumn.getCellFactory().call((Object)tableColumn);
        cell.updateTableColumn(tableColumn);
        cell.updateTableView(tableColumn.getTableView());
        cell.updateTableRow((TableRow)this.getSkinnable());
        return cell;
    }
    
    private double getFixedRowShift(final int index) {
        double tableCellY = 0.0;
        final int positionY = this.spreadsheetView.getFixedRows().indexOf((Object)this.spreadsheetView.getFilteredSourceIndex(index));
        double space = 0.0;
        for (int o = 0; o < positionY; ++o) {
            if (!this.spreadsheetView.isRowHidden(o)) {
                space += this.handle.getCellsViewSkin().getRowHeight((int)this.spreadsheetView.getFixedRows().get(o));
            }
        }
        if (positionY != -1 && ((TableRow)this.getSkinnable()).getLocalToParentTransform().getTy() <= space) {
            tableCellY = space - ((TableRow)this.getSkinnable()).getLocalToParentTransform().getTy();
            this.handle.getCellsViewSkin().getCurrentlyFixedRow().add((Object)index);
        }
        else {
            this.handle.getCellsViewSkin().getCurrentlyFixedRow().remove((Object)index);
        }
        return tableCellY;
    }
    
    private double getTableRowHeight(final int row) {
        final Double rowHeightCache = (Double)this.handle.getCellsViewSkin().rowHeightMap.get((Object)this.spreadsheetView.getModelRow(row));
        return (rowHeightCache == null) ? this.handle.getView().getGrid().getRowHeight(this.spreadsheetView.getModelRow(row)) : rowHeightCache;
    }
    
    private boolean isInvisible(final double x, final double width, final double hbarValue, final double headerWidth, final int columnSpan) {
        return (x + width < hbarValue && columnSpan == 1) || x > hbarValue + headerWidth;
    }
    
    protected double computePrefWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        double prefWidth = 0.0;
        final List<? extends TableColumnBase> visibleLeafColumns = (List<? extends TableColumnBase>)this.handle.getGridView().getVisibleLeafColumns();
        for (int i = 0, max = visibleLeafColumns.size(); i < max; ++i) {
            prefWidth += ((TableColumnBase)visibleLeafColumns.get(i)).getWidth();
        }
        return prefWidth;
    }
    
    protected double computePrefHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return ((TableRow)this.getSkinnable()).getPrefHeight();
    }
    
    protected double computeMinHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return ((TableRow)this.getSkinnable()).getPrefHeight();
    }
    
    protected double computeMaxHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return super.computeMaxHeight(width, topInset, rightInset, bottomInset, leftInset);
    }
}
