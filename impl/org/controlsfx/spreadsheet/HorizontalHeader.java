// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.spreadsheet;

import java.util.List;
import javafx.scene.control.TablePosition;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import com.sun.javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.control.TableColumnBase;
import com.sun.javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import javafx.application.Platform;
import java.util.Iterator;
import javafx.beans.value.ObservableValue;
import javafx.beans.Observable;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import javafx.collections.ListChangeListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.InvalidationListener;
import java.util.BitSet;
import com.sun.javafx.scene.control.skin.TableHeaderRow;

public class HorizontalHeader extends TableHeaderRow
{
    final GridViewSkin gridViewSkin;
    private boolean working;
    protected BitSet selectedColumns;
    private final InvalidationListener verticalHeaderListener;
    private final ChangeListener<Boolean> horizontalHeaderVisibilityListener;
    private final ListChangeListener<SpreadsheetColumn> fixedColumnsListener;
    private final InvalidationListener selectionListener;
    
    public HorizontalHeader(final GridViewSkin skin) {
        super((TableViewSkinBase)skin);
        this.working = true;
        this.selectedColumns = new BitSet();
        this.verticalHeaderListener = (InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable observable) {
                HorizontalHeader.this.updateTableWidth();
            }
        };
        this.horizontalHeaderVisibilityListener = (ChangeListener<Boolean>)new ChangeListener<Boolean>() {
            public void changed(final ObservableValue<? extends Boolean> arg0, final Boolean arg1, final Boolean arg2) {
                HorizontalHeader.this.updateHorizontalHeaderVisibility(arg2);
            }
        };
        this.fixedColumnsListener = (ListChangeListener<SpreadsheetColumn>)new ListChangeListener<SpreadsheetColumn>() {
            public void onChanged(final ListChangeListener.Change<? extends SpreadsheetColumn> change) {
                while (change.next()) {
                    for (final SpreadsheetColumn remitem : change.getRemoved()) {
                        HorizontalHeader.this.unfixColumn(remitem);
                    }
                    for (final SpreadsheetColumn additem : change.getAddedSubList()) {
                        HorizontalHeader.this.fixColumn(additem);
                    }
                }
                HorizontalHeader.this.updateHighlightSelection();
            }
        };
        this.selectionListener = (InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable valueModel) {
                HorizontalHeader.this.updateHighlightSelection();
            }
        };
        this.gridViewSkin = skin;
    }
    
    public void init() {
        final SpreadsheetView spv = this.gridViewSkin.handle.getView();
        this.updateHorizontalHeaderVisibility(spv.isShowColumnHeader());
        spv.showRowHeaderProperty().addListener(this.verticalHeaderListener);
        this.gridViewSkin.verticalHeader.verticalHeaderWidthProperty().addListener(this.verticalHeaderListener);
        spv.showColumnHeaderProperty().addListener((ChangeListener)this.horizontalHeaderVisibilityListener);
        this.gridViewSkin.getSelectedColumns().addListener(this.selectionListener);
        spv.getFixedColumns().addListener((ListChangeListener)this.fixedColumnsListener);
        final Iterator<SpreadsheetColumn> iterator;
        SpreadsheetColumn column;
        Platform.runLater(() -> {
            spv.getFixedColumns().iterator();
            while (iterator.hasNext()) {
                column = iterator.next();
                this.fixColumn(column);
            }
            this.requestLayout();
            this.installHeaderMouseEvent();
            return;
        });
        this.getRootHeader().getColumnHeaders().addListener(o -> {
            for (final SpreadsheetColumn fixItem : spv.getFixedColumns()) {
                this.fixColumn(fixItem);
            }
            this.updateHighlightSelection();
            this.installHeaderMouseEvent();
        });
    }
    
    public HorizontalHeaderColumn getRootHeader() {
        return (HorizontalHeaderColumn)super.getRootHeader();
    }
    
    void clearSelectedColumns() {
        this.selectedColumns.clear();
    }
    
    protected void updateTableWidth() {
        super.updateTableWidth();
        double padding = 0.0;
        if (this.working && this.gridViewSkin != null && this.gridViewSkin.spreadsheetView != null && this.gridViewSkin.spreadsheetView.showRowHeaderProperty().get() && this.gridViewSkin.verticalHeader != null) {
            padding += this.gridViewSkin.verticalHeader.getVerticalHeaderWidth();
        }
        final Rectangle clip = (Rectangle)this.getClip();
        clip.setWidth((clip.getWidth() == 0.0) ? 0.0 : (clip.getWidth() - padding));
    }
    
    protected void updateScrollX() {
        super.updateScrollX();
        this.gridViewSkin.horizontalPickers.updateScrollX();
        if (this.working) {
            this.requestLayout();
            this.getRootHeader().layoutFixedColumns();
        }
    }
    
    protected NestedTableColumnHeader createRootHeader() {
        return new HorizontalHeaderColumn((TableViewSkinBase<?, ?, ?, ?, ?, ?>)this.getTableSkin(), null);
    }
    
    private void installHeaderMouseEvent() {
        for (final TableColumnHeader columnHeader : this.getRootHeader().getColumnHeaders()) {
            final EventHandler<MouseEvent> mouseEventHandler = (EventHandler<MouseEvent>)(mouseEvent -> {
                if (mouseEvent.isPrimaryButtonDown()) {
                    this.headerClicked((TableColumn)columnHeader.getTableColumn(), mouseEvent);
                }
            });
            ((Node)columnHeader.getChildrenUnmodifiable().get(0)).setOnMousePressed((EventHandler)mouseEventHandler);
        }
    }
    
    private void headerClicked(final TableColumn column, final MouseEvent event) {
        final TableView.TableViewSelectionModel<ObservableList<SpreadsheetCell>> sm = (TableView.TableViewSelectionModel<ObservableList<SpreadsheetCell>>)this.gridViewSkin.handle.getGridView().getSelectionModel();
        final int lastRow = this.gridViewSkin.getItemCount() - 1;
        final int indexColumn = column.getTableView().getColumns().indexOf((Object)column);
        final TablePosition focusedPosition = sm.getTableView().getFocusModel().getFocusedCell();
        if (event.isShortcutDown()) {
            final BitSet tempSet = (BitSet)this.selectedColumns.clone();
            sm.selectRange(0, (TableColumnBase)column, lastRow, (TableColumnBase)column);
            this.selectedColumns.or(tempSet);
            this.selectedColumns.set(indexColumn);
        }
        else if (event.isShiftDown() && focusedPosition != null && focusedPosition.getTableColumn() != null) {
            sm.clearSelection();
            sm.selectRange(0, (TableColumnBase)column, lastRow, (TableColumnBase)focusedPosition.getTableColumn());
            sm.getTableView().getFocusModel().focus(0, focusedPosition.getTableColumn());
            final int min = Math.min(indexColumn, focusedPosition.getColumn());
            final int max = Math.max(indexColumn, focusedPosition.getColumn());
            this.selectedColumns.set(min, max + 1);
        }
        else {
            sm.clearSelection();
            sm.selectRange(0, (TableColumnBase)column, lastRow, (TableColumnBase)column);
            sm.getTableView().getFocusModel().focus(0, column);
            this.selectedColumns.set(indexColumn);
        }
    }
    
    private void fixColumn(final SpreadsheetColumn column) {
        this.addStyleHeader(this.gridViewSkin.spreadsheetView.getViewColumn(this.gridViewSkin.spreadsheetView.getColumns().indexOf((Object)column)));
    }
    
    private void unfixColumn(final SpreadsheetColumn column) {
        this.removeStyleHeader(this.gridViewSkin.spreadsheetView.getViewColumn(this.gridViewSkin.spreadsheetView.getColumns().indexOf((Object)column)));
    }
    
    private void removeStyleHeader(final Integer i) {
        if (this.getRootHeader().getColumnHeaders().size() > i) {
            ((TableColumnHeader)this.getRootHeader().getColumnHeaders().get((int)i)).getStyleClass().removeAll((Object[])new String[] { "fixed" });
        }
    }
    
    private void addStyleHeader(final Integer i) {
        if (this.getRootHeader().getColumnHeaders().size() > i) {
            ((TableColumnHeader)this.getRootHeader().getColumnHeaders().get((int)i)).getStyleClass().addAll((Object[])new String[] { "fixed" });
        }
    }
    
    private void updateHighlightSelection() {
        for (final TableColumnHeader i : this.getRootHeader().getColumnHeaders()) {
            i.getStyleClass().removeAll((Object[])new String[] { "selected" });
        }
        final List<Integer> selectedColumns = (List<Integer>)this.gridViewSkin.getSelectedColumns();
        for (final Integer j : selectedColumns) {
            if (this.getRootHeader().getColumnHeaders().size() > j) {
                ((TableColumnHeader)this.getRootHeader().getColumnHeaders().get((int)j)).getStyleClass().addAll((Object[])new String[] { "selected" });
            }
        }
    }
    
    private void updateHorizontalHeaderVisibility(final boolean visible) {
        this.setManaged(this.working = visible);
        if (!visible) {
            this.getStyleClass().add((Object)"invisible");
        }
        else {
            this.getStyleClass().remove((Object)"invisible");
            this.requestLayout();
            this.getRootHeader().layoutFixedColumns();
            this.updateHighlightSelection();
        }
    }
    
    protected double computePrefHeight(final double width) {
        if (!this.gridViewSkin.handle.getView().isShowColumnHeader()) {
            return 0.0;
        }
        double headerPrefHeight = this.getRootHeader().prefHeight(width);
        headerPrefHeight = ((headerPrefHeight == 0.0) ? 24.0 : headerPrefHeight);
        double height = this.snappedTopInset() + headerPrefHeight + this.snappedBottomInset();
        height = ((height < GridViewSkin.DEFAULT_CELL_HEIGHT) ? GridViewSkin.DEFAULT_CELL_HEIGHT : height);
        return height;
    }
}
