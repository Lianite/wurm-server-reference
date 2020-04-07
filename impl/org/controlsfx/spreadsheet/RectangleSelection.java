// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.spreadsheet;

import org.controlsfx.control.spreadsheet.GridChange;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import java.util.TreeSet;
import javafx.scene.control.TablePosition;
import javafx.beans.Observable;
import java.util.Iterator;
import java.util.List;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import javafx.scene.control.TableColumn;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import javafx.scene.control.TableView;
import javafx.scene.control.IndexedCell;
import javafx.event.Event;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.beans.InvalidationListener;
import javafx.scene.shape.Rectangle;

public class RectangleSelection extends Rectangle
{
    private final GridViewSkin skin;
    private final TableViewSpanSelectionModel sm;
    private final SelectionRange selectionRange;
    private final InvalidationListener selectedCellListener;
    private final InvalidationListener layoutListener;
    private final EventHandler<MouseEvent> mouseDraggedListener;
    
    public RectangleSelection(final GridViewSkin skin, final TableViewSpanSelectionModel sm) {
        this.layoutListener = (observable -> this.updateRectangle());
        this.mouseDraggedListener = (EventHandler<MouseEvent>)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent event) {
                RectangleSelection.this.skin.getVBar().valueProperty().removeListener(RectangleSelection.this.layoutListener);
                RectangleSelection.this.setVisible(false);
                RectangleSelection.this.skin.getVBar().addEventFilter(MouseEvent.MOUSE_RELEASED, (EventHandler)new EventHandler<MouseEvent>() {
                    public void handle(final MouseEvent event) {
                        RectangleSelection.this.skin.getVBar().removeEventFilter(MouseEvent.MOUSE_RELEASED, (EventHandler)this);
                        RectangleSelection.this.skin.getVBar().valueProperty().addListener(RectangleSelection.this.layoutListener);
                        RectangleSelection.this.updateRectangle();
                    }
                });
            }
        };
        this.skin = skin;
        this.sm = sm;
        this.getStyleClass().add((Object)"selection-rectangle");
        this.setMouseTransparent(true);
        this.selectionRange = new SelectionRange();
        this.selectedCellListener = (observable -> {
            skin.getHorizontalHeader().clearSelectedColumns();
            skin.verticalHeader.clearSelectedRows();
            this.selectionRange.fill((List<TablePosition>)sm.getSelectedCells(), skin.spreadsheetView);
            this.updateRectangle();
        });
        skin.getVBar().valueProperty().addListener(this.layoutListener);
        skin.getVBar().addEventFilter(MouseEvent.MOUSE_DRAGGED, (EventHandler)this.mouseDraggedListener);
        skin.spreadsheetView.hiddenRowsProperty().addListener(this.selectedCellListener);
        skin.spreadsheetView.hiddenColumnsProperty().addListener(this.selectedCellListener);
        skin.getHBar().valueProperty().addListener(this.layoutListener);
        sm.getSelectedCells().addListener(this.selectedCellListener);
    }
    
    public final void updateRectangle() {
        if (this.sm.getSelectedCells().isEmpty() || this.skin.getSelectedRows().isEmpty() || this.skin.getSelectedColumns().isEmpty() || this.selectionRange.range == null) {
            this.setVisible(false);
            return;
        }
        final IndexedCell topRowCell = (IndexedCell)this.skin.getFlow().getTopRow();
        if (topRowCell == null) {
            return;
        }
        final int topRow = topRowCell.getIndex();
        final IndexedCell bottomRowCell = (IndexedCell)this.skin.getFlow().getCells().get(this.skin.getFlow().getCells().size() - 1);
        if (bottomRowCell == null) {
            return;
        }
        final int bottomRow = bottomRowCell.getIndex();
        int minRow = this.selectionRange.range.getTop();
        if (minRow > bottomRow) {
            this.setVisible(false);
            return;
        }
        minRow = Math.max(minRow, topRow);
        int maxRow = this.selectionRange.range.getBottom();
        if (maxRow < topRow) {
            this.setVisible(false);
            return;
        }
        maxRow = Math.min(maxRow, bottomRow);
        final int minColumn = this.selectionRange.range.getLeft();
        final int maxColumn = this.selectionRange.range.getRight();
        final GridRow gridMinRow = this.skin.getRowIndexed(minRow);
        if (gridMinRow == null) {
            this.setVisible(false);
            return;
        }
        if (maxRow >= this.skin.getItemCount() || maxColumn >= ((TableView)this.skin.getSkinnable()).getVisibleLeafColumns().size() || minColumn < 0) {
            this.setVisible(false);
            return;
        }
        final SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)((TableView)this.skin.getSkinnable()).getItems().get(maxRow)).get(this.skin.spreadsheetView.getModelColumn(maxColumn));
        this.handleHorizontalPositioning(minColumn, this.skin.spreadsheetView.getViewColumn(cell.getColumn()) + this.skin.spreadsheetView.getColumnSpan(cell) - 1);
        if (this.getX() + this.getWidth() < 0.0) {
            this.setVisible(false);
            return;
        }
        final GridRow gridMaxRow = this.skin.getRowIndexed(maxRow);
        if (gridMaxRow == null) {
            this.setVisible(false);
            return;
        }
        this.setVisible(true);
        this.handleVerticalPositioning(minRow, maxRow, gridMinRow, gridMaxRow);
    }
    
    private void handleVerticalPositioning(final int minRow, final int maxRow, final GridRow gridMinRow, final GridRow gridMaxRow) {
        double height = 0.0;
        for (int i = maxRow; i <= maxRow; ++i) {
            height += this.skin.getRowHeight(i);
        }
        if (!this.skin.getCurrentlyFixedRow().contains((Object)minRow)) {
            this.yProperty().unbind();
            if (gridMinRow.getLayoutY() < this.skin.getFixedRowHeight()) {
                this.setY(this.skin.getFixedRowHeight());
            }
            else {
                this.yProperty().bind((ObservableValue)gridMinRow.layoutYProperty());
            }
        }
        else {
            this.yProperty().bind((ObservableValue)gridMinRow.layoutYProperty().add((ObservableNumberValue)gridMinRow.verticalShift));
        }
        this.heightProperty().bind((ObservableValue)gridMaxRow.layoutYProperty().add((ObservableNumberValue)gridMaxRow.verticalShift).subtract((ObservableNumberValue)this.yProperty()).add(height));
    }
    
    private void handleHorizontalPositioning(final int minColumn, final int maxColumn) {
        double x = 0.0;
        final List<TableColumn<ObservableList<SpreadsheetCell>, ?>> visibleColumns = (List<TableColumn<ObservableList<SpreadsheetCell>, ?>>)this.skin.handle.getGridView().getVisibleLeafColumns();
        final List<TableColumn<ObservableList<SpreadsheetCell>, ?>> allColumns = (List<TableColumn<ObservableList<SpreadsheetCell>, ?>>)this.skin.handle.getGridView().getColumns();
        final List<SpreadsheetColumn> columns = (List<SpreadsheetColumn>)this.skin.spreadsheetView.getColumns();
        if (visibleColumns.size() <= minColumn || visibleColumns.size() <= maxColumn) {
            return;
        }
        for (int i = 0; i < minColumn; ++i) {
            x += this.snapSize(visibleColumns.get(i).getWidth());
        }
        x -= this.skin.getHBar().getValue();
        double width = 0.0;
        for (int j = minColumn; j <= maxColumn; ++j) {
            width += this.snapSize(visibleColumns.get(j).getWidth());
        }
        if (!this.skin.spreadsheetView.getFixedColumns().contains((Object)columns.get(this.skin.spreadsheetView.getModelColumn(minColumn)))) {
            if (x < this.skin.fixedColumnWidth) {
                width -= this.skin.fixedColumnWidth - x;
                x = this.skin.fixedColumnWidth;
            }
        }
        else if (x + width < this.skin.fixedColumnWidth) {
            x = 0.0;
            width = 0.0;
            for (final SpreadsheetColumn column : this.skin.spreadsheetView.getFixedColumns()) {
                final int indexColumn = this.skin.spreadsheetView.getViewColumn(columns.indexOf(column));
                if (indexColumn < minColumn && indexColumn != minColumn) {
                    x += this.snapSize(column.getWidth());
                }
                if (indexColumn >= minColumn && indexColumn <= maxColumn) {
                    width += this.snapSize(column.getWidth());
                }
            }
        }
        else if (x < this.skin.fixedColumnWidth) {
            double tempX = 0.0;
            for (final SpreadsheetColumn column2 : this.skin.spreadsheetView.getFixedColumns()) {
                final int indexColumn2 = this.skin.spreadsheetView.getViewColumn(columns.indexOf(column2));
                if (indexColumn2 < minColumn && indexColumn2 != minColumn) {
                    tempX += this.snapSize(column2.getWidth());
                }
            }
            width -= tempX - x;
            x = tempX;
        }
        this.setX(x);
        this.setWidth(width);
    }
    
    private double snapSize(final double value) {
        return Math.ceil(value);
    }
    
    public static class SelectionRange
    {
        private final TreeSet<Long> set;
        private GridRange range;
        
        public SelectionRange() {
            this.set = new TreeSet<Long>();
        }
        
        public void fill(final List<TablePosition> list) {
            this.set.clear();
            for (final TablePosition pos : list) {
                final long key = key(pos.getRow(), pos.getColumn());
                this.set.add(key);
            }
            this.computeRange();
        }
        
        public void fill(final List<TablePosition> list, final SpreadsheetView spv) {
            this.set.clear();
            this.range = null;
            for (final TablePosition pos : list) {
                final long key = key(pos.getRow(), pos.getColumn());
                this.set.add(key);
                if (!spv.getGrid().isCellDisplaySelection(spv.getModelRow(pos.getRow()), spv.getModelColumn(pos.getColumn()))) {
                    return;
                }
            }
            this.computeRange();
        }
        
        public void fillGridRange(final List<GridChange> list) {
            this.set.clear();
            this.range = null;
            for (final GridChange pos : list) {
                this.set.add(key(pos.getRow(), pos.getColumn()));
            }
            this.computeRange();
        }
        
        public GridRange getRange() {
            return this.range;
        }
        
        public static Long key(final int row, final int column) {
            return (Long)(row << 32 | column);
        }
        
        private int getRow(final Long l) {
            return (int)(l >> 32);
        }
        
        private int getColumn(final Long l) {
            return (int)(l & -1L);
        }
        
        private void computeRange() {
            this.range = null;
            while (!this.set.isEmpty()) {
                if (this.range != null) {
                    this.range = null;
                    return;
                }
                final long first = this.set.first();
                this.set.remove(first);
                int row = this.getRow(first);
                int column = this.getColumn(first);
                while (this.set.contains(key(row, column + 1))) {
                    ++column;
                    this.set.remove(key(row, column));
                }
                boolean flag = true;
                while (flag) {
                    ++row;
                    for (int col = this.getColumn(first); col <= column; ++col) {
                        if (!this.set.contains(key(row, col))) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        for (int col = this.getColumn(first); col <= column; ++col) {
                            this.set.remove(key(row, col));
                        }
                    }
                    else {
                        --row;
                    }
                }
                this.range = new GridRange(this.getRow(first), row, this.getColumn(first), column);
            }
        }
    }
    
    public static class GridRange
    {
        private final int top;
        private final int bottom;
        private final int left;
        private final int right;
        
        public GridRange(final int top, final int bottom, final int left, final int right) {
            this.top = top;
            this.bottom = bottom;
            this.left = left;
            this.right = right;
        }
        
        public int getTop() {
            return this.top;
        }
        
        public int getBottom() {
            return this.bottom;
        }
        
        public int getLeft() {
            return this.left;
        }
        
        public int getRight() {
            return this.right;
        }
    }
}
