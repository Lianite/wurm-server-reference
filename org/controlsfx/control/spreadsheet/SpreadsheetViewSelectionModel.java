// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.spreadsheet;

import impl.org.controlsfx.spreadsheet.GridViewBehavior;
import javafx.scene.control.TableColumnBase;
import java.util.Arrays;
import javafx.util.Pair;
import java.util.List;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TablePosition;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import impl.org.controlsfx.spreadsheet.TableViewSpanSelectionModel;

public class SpreadsheetViewSelectionModel
{
    private final TableViewSpanSelectionModel selectionModel;
    private final SpreadsheetView spv;
    
    SpreadsheetViewSelectionModel(final SpreadsheetView spv, final TableViewSpanSelectionModel selectionModel) {
        this.spv = spv;
        this.selectionModel = selectionModel;
    }
    
    public final void clearAndSelect(final int row, final SpreadsheetColumn column) {
        this.selectionModel.clearAndSelect(this.spv.getFilteredRow(row), column.column);
    }
    
    private final void clearAndSelectView(final int row, final SpreadsheetColumn column) {
        this.selectionModel.clearAndSelect(row, column.column);
    }
    
    public final void select(final int row, final SpreadsheetColumn column) {
        this.selectionModel.select(this.spv.getFilteredRow(row), column.column);
    }
    
    public final void clearSelection() {
        this.selectionModel.clearSelection();
    }
    
    public final ObservableList<TablePosition> getSelectedCells() {
        return this.selectionModel.getSelectedCells();
    }
    
    public final void selectAll() {
        this.selectionModel.selectAll();
    }
    
    public final TablePosition getFocusedCell() {
        return this.selectionModel.getTableView().getFocusModel().getFocusedCell();
    }
    
    public final void focus(final int row, final SpreadsheetColumn column) {
        this.selectionModel.getTableView().getFocusModel().focus(row, (TableColumn)column.column);
    }
    
    public final void setSelectionMode(final SelectionMode value) {
        this.selectionModel.setSelectionMode(value);
    }
    
    public SelectionMode getSelectionMode() {
        return this.selectionModel.getSelectionMode();
    }
    
    public void selectCells(final List<Pair<Integer, Integer>> selectedCells) {
        this.selectionModel.verifySelectedCells(selectedCells);
    }
    
    public void selectCells(final Pair<Integer, Integer>... selectedCells) {
        this.selectionModel.verifySelectedCells(Arrays.asList(selectedCells));
    }
    
    public void selectRange(final int minRow, final SpreadsheetColumn minColumn, final int maxRow, final SpreadsheetColumn maxColumn) {
        this.selectionModel.selectRange(this.spv.getFilteredRow(minRow), (TableColumnBase<ObservableList<SpreadsheetCell>, ?>)minColumn.column, this.spv.getFilteredRow(maxRow), (TableColumnBase<ObservableList<SpreadsheetCell>, ?>)maxColumn.column);
    }
    
    public void clearAndSelectLeftCell() {
        final TablePosition<ObservableList<SpreadsheetCell>, ?> position = (TablePosition<ObservableList<SpreadsheetCell>, ?>)this.getFocusedCell();
        int row = position.getRow();
        int column = position.getColumn();
        if (--column < 0) {
            if (row == 0) {
                ++column;
            }
            else {
                column = this.selectionModel.getTableView().getVisibleLeafColumns().size() - 1;
                --row;
                this.selectionModel.direction = (Pair<Integer, Integer>)new Pair((Object)(-1), (Object)(-1));
            }
            this.clearAndSelectView(row, (SpreadsheetColumn)this.spv.getColumns().get(this.spv.getModelColumn(column)));
        }
        else {
            ((GridViewBehavior)this.spv.getCellsViewSkin().getBehavior()).selectCell(0, -1);
        }
    }
    
    public void clearAndSelectRightCell() {
        final TablePosition<ObservableList<SpreadsheetCell>, ?> position = (TablePosition<ObservableList<SpreadsheetCell>, ?>)this.getFocusedCell();
        int row = position.getRow();
        int column = position.getColumn();
        if (++column >= this.selectionModel.getTableView().getVisibleLeafColumns().size()) {
            if (row == this.spv.getGrid().getRowCount() - 1) {
                --column;
            }
            else {
                this.selectionModel.direction = (Pair<Integer, Integer>)new Pair((Object)1, (Object)1);
                column = 0;
                ++row;
            }
            this.clearAndSelectView(row, (SpreadsheetColumn)this.spv.getColumns().get(this.spv.getModelColumn(column)));
        }
        else {
            ((GridViewBehavior)this.spv.getCellsViewSkin().getBehavior()).selectCell(0, 1);
        }
    }
}
