// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.spreadsheet;

import java.lang.reflect.Field;
import com.sun.javafx.scene.control.skin.CellSkinBase;
import javafx.stage.Screen;
import javafx.application.Platform;
import javafx.scene.control.ResizeFeaturesBase;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableSelectionModel;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.scene.control.TableFocusModel;
import javafx.geometry.VPos;
import javafx.geometry.HPos;
import javafx.scene.control.TableColumnBase;
import java.time.LocalDate;
import javafx.scene.control.Skin;
import javafx.scene.control.TableCell;
import javafx.scene.Node;
import javafx.event.EventTarget;
import javafx.event.Event;
import javafx.scene.layout.Region;
import org.controlsfx.control.spreadsheet.Grid;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.IndexedCell;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import java.util.Iterator;
import java.util.List;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import java.util.HashSet;
import javafx.collections.FXCollections;
import java.util.HashMap;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import javafx.scene.control.Control;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import javafx.collections.SetChangeListener;
import javafx.collections.ListChangeListener;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import java.util.BitSet;
import javafx.collections.ObservableSet;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import javafx.collections.ObservableMap;
import java.util.Set;
import java.util.Map;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import com.sun.javafx.scene.control.behavior.TableViewBehavior;
import javafx.scene.control.TableView;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import javafx.collections.ObservableList;
import com.sun.javafx.scene.control.skin.TableViewSkinBase;

public class GridViewSkin extends TableViewSkinBase<ObservableList<SpreadsheetCell>, ObservableList<SpreadsheetCell>, TableView<ObservableList<SpreadsheetCell>>, TableViewBehavior<ObservableList<SpreadsheetCell>>, TableRow<ObservableList<SpreadsheetCell>>, TableColumn<ObservableList<SpreadsheetCell>, ?>>
{
    public static final double DEFAULT_CELL_HEIGHT;
    private static final double DATE_CELL_MIN_WIDTH;
    final Map<GridRow, Set<CellView>> deportedCells;
    ObservableMap<Integer, Double> rowHeightMap;
    private GridCellEditor gridCellEditor;
    protected final SpreadsheetHandle handle;
    protected SpreadsheetView spreadsheetView;
    protected VerticalHeader verticalHeader;
    protected HorizontalPicker horizontalPickers;
    private ObservableSet<Integer> currentlyFixedRow;
    private final ObservableList<Integer> selectedRows;
    private final ObservableList<Integer> selectedColumns;
    private double fixedRowHeight;
    BitSet hBarValue;
    BitSet rowToLayout;
    RectangleSelection rectangleSelection;
    double fixedColumnWidth;
    BooleanProperty lastRowLayout;
    private InvalidationListener rowToLayoutListener;
    private final InvalidationListener vbarValueListener;
    private final ListChangeListener<Integer> fixedRowsListener;
    private final SetChangeListener<? super Integer> currentlyFixedRowListener;
    private final ListChangeListener<SpreadsheetColumn> fixedColumnsListener;
    
    public GridViewSkin(final SpreadsheetHandle handle) {
        super((Control)handle.getGridView(), (BehaviorBase)new GridViewBehavior(handle.getGridView()));
        this.deportedCells = new HashMap<GridRow, Set<CellView>>();
        this.rowHeightMap = (ObservableMap<Integer, Double>)FXCollections.observableHashMap();
        this.currentlyFixedRow = (ObservableSet<Integer>)FXCollections.observableSet((Set)new HashSet());
        this.selectedRows = (ObservableList<Integer>)FXCollections.observableArrayList();
        this.selectedColumns = (ObservableList<Integer>)FXCollections.observableArrayList();
        this.fixedRowHeight = 0.0;
        this.lastRowLayout = (BooleanProperty)new SimpleBooleanProperty(true);
        this.rowToLayoutListener = (InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable observable) {
                GridViewSkin.this.rowToLayout = GridViewSkin.this.initRowToLayoutBitSet();
            }
        };
        this.vbarValueListener = (InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable valueModel) {
                GridViewSkin.this.verticalScroll();
            }
        };
        this.fixedRowsListener = (ListChangeListener<Integer>)new ListChangeListener<Integer>() {
            public void onChanged(final ListChangeListener.Change<? extends Integer> c) {
                GridViewSkin.this.hBarValue.clear();
                while (c.next()) {
                    if (c.wasPermutated()) {
                        for (final Integer fixedRow : c.getList()) {
                            GridViewSkin.this.rowToLayout.set(GridViewSkin.this.spreadsheetView.getFilteredRow(fixedRow), true);
                        }
                    }
                    else {
                        for (final Integer unfixedRow : c.getRemoved()) {
                            GridViewSkin.this.rowToLayout.set(GridViewSkin.this.spreadsheetView.getFilteredRow(unfixedRow), false);
                            if (GridViewSkin.this.spreadsheetView.getGrid().getRows().size() > unfixedRow) {
                                final List<SpreadsheetCell> myRow = (List<SpreadsheetCell>)GridViewSkin.this.spreadsheetView.getGrid().getRows().get((int)unfixedRow);
                                for (final SpreadsheetCell cell : myRow) {
                                    if (GridViewSkin.this.spreadsheetView.getRowSpanFilter(cell) > 1) {
                                        GridViewSkin.this.rowToLayout.set(GridViewSkin.this.spreadsheetView.getFilteredRow(unfixedRow), true);
                                        break;
                                    }
                                }
                            }
                        }
                        for (final Integer fixedRow : c.getAddedSubList()) {
                            GridViewSkin.this.rowToLayout.set(GridViewSkin.this.spreadsheetView.getFilteredRow(fixedRow), true);
                        }
                    }
                }
                GridViewSkin.this.getFlow().requestLayout();
            }
        };
        this.currentlyFixedRowListener = (SetChangeListener<? super Integer>)new SetChangeListener<Integer>() {
            public void onChanged(final SetChangeListener.Change<? extends Integer> arg0) {
                GridViewSkin.this.computeFixedRowHeight();
            }
        };
        this.fixedColumnsListener = (ListChangeListener<SpreadsheetColumn>)new ListChangeListener<SpreadsheetColumn>() {
            public void onChanged(final ListChangeListener.Change<? extends SpreadsheetColumn> c) {
                GridViewSkin.this.hBarValue.clear();
                GridViewSkin.this.getFlow().requestLayout();
            }
        };
        super.init((Control)handle.getGridView());
        this.handle = handle;
        this.spreadsheetView = handle.getView();
        this.gridCellEditor = new GridCellEditor(handle);
        final TableView<ObservableList<SpreadsheetCell>> tableView = handle.getGridView();
        tableView.setRowFactory((Callback)new Callback<TableView<ObservableList<SpreadsheetCell>>, TableRow<ObservableList<SpreadsheetCell>>>() {
            public TableRow<ObservableList<SpreadsheetCell>> call(final TableView<ObservableList<SpreadsheetCell>> p) {
                return new GridRow(handle);
            }
        });
        tableView.getStyleClass().add((Object)"cell-spreadsheet");
        this.getCurrentlyFixedRow().addListener((SetChangeListener)this.currentlyFixedRowListener);
        this.spreadsheetView.getFixedRows().addListener((ListChangeListener)this.fixedRowsListener);
        this.spreadsheetView.getFixedColumns().addListener((ListChangeListener)this.fixedColumnsListener);
        this.init();
        handle.getView().gridProperty().addListener(this.rowToLayoutListener);
        handle.getView().hiddenRowsProperty().addListener(this.rowToLayoutListener);
        handle.getView().hiddenColumnsProperty().addListener(this.rowToLayoutListener);
        this.hBarValue = new BitSet(this.getItemCount());
        this.rowToLayout = this.initRowToLayoutBitSet();
        this.computeFixedRowHeight();
        final EventHandler<MouseEvent> ml = (EventHandler<MouseEvent>)(event -> {
            if (tableView.getEditingCell() != null) {
                tableView.edit(-1, (TableColumn)null);
            }
            tableView.requestFocus();
        });
        this.getFlow().getVerticalBar().addEventFilter(MouseEvent.MOUSE_PRESSED, (EventHandler)ml);
        this.getFlow().getHorizontalBar().addEventFilter(MouseEvent.MOUSE_PRESSED, (EventHandler)ml);
        final TableViewBehavior<ObservableList<SpreadsheetCell>> behavior = (TableViewBehavior<ObservableList<SpreadsheetCell>>)this.getBehavior();
        behavior.setOnFocusPreviousRow((Runnable)new Runnable() {
            @Override
            public void run() {
                GridViewSkin.this.onFocusPreviousCell();
            }
        });
        behavior.setOnFocusNextRow((Runnable)new Runnable() {
            @Override
            public void run() {
                GridViewSkin.this.onFocusNextCell();
            }
        });
        behavior.setOnMoveToFirstCell((Runnable)new Runnable() {
            @Override
            public void run() {
                GridViewSkin.access$000(GridViewSkin.this);
            }
        });
        behavior.setOnMoveToLastCell((Runnable)new Runnable() {
            @Override
            public void run() {
                GridViewSkin.access$100(GridViewSkin.this);
            }
        });
        behavior.setOnScrollPageDown((Callback)new Callback<Boolean, Integer>() {
            public Integer call(final Boolean isFocusDriven) {
                return GridViewSkin.this.onScrollPageDown((boolean)isFocusDriven);
            }
        });
        behavior.setOnScrollPageUp((Callback)new Callback<Boolean, Integer>() {
            public Integer call(final Boolean isFocusDriven) {
                return GridViewSkin.this.onScrollPageUp((boolean)isFocusDriven);
            }
        });
        behavior.setOnSelectPreviousRow((Runnable)new Runnable() {
            @Override
            public void run() {
                GridViewSkin.this.onSelectPreviousCell();
            }
        });
        behavior.setOnSelectNextRow((Runnable)new Runnable() {
            @Override
            public void run() {
                GridViewSkin.this.onSelectNextCell();
            }
        });
        behavior.setOnSelectLeftCell((Runnable)new Runnable() {
            @Override
            public void run() {
                GridViewSkin.access$200(GridViewSkin.this);
            }
        });
        behavior.setOnSelectRightCell((Runnable)new Runnable() {
            @Override
            public void run() {
                GridViewSkin.access$300(GridViewSkin.this);
            }
        });
        this.registerChangeListener((ObservableValue)tableView.fixedCellSizeProperty(), "FIXED_CELL_SIZE");
    }
    
    public double getRowHeight(final int row) {
        if (row == -1) {
            return GridViewSkin.DEFAULT_CELL_HEIGHT;
        }
        final Double rowHeightCache = (Double)this.rowHeightMap.get((Object)this.spreadsheetView.getModelRow(row));
        if (rowHeightCache == null) {
            final double rowHeight = this.handle.getView().getGrid().getRowHeight(this.spreadsheetView.getModelRow(row));
            return (rowHeight == -1.0) ? GridViewSkin.DEFAULT_CELL_HEIGHT : rowHeight;
        }
        return rowHeightCache;
    }
    
    public double getFixedRowHeight() {
        return this.fixedRowHeight;
    }
    
    public ObservableList<Integer> getSelectedRows() {
        return this.selectedRows;
    }
    
    public ObservableList<Integer> getSelectedColumns() {
        return this.selectedColumns;
    }
    
    public GridCellEditor getSpreadsheetCellEditorImpl() {
        return this.gridCellEditor;
    }
    
    public GridRow getRowIndexed(final int index) {
        final List<? extends IndexedCell> cells = (List<? extends IndexedCell>)this.getFlow().getCells();
        if (!cells.isEmpty()) {
            final IndexedCell cell = (IndexedCell)cells.get(0);
            if (index >= cell.getIndex() && index - cell.getIndex() < cells.size()) {
                return (GridRow)cells.get(index - cell.getIndex());
            }
        }
        for (final IndexedCell cell2 : this.getFlow().getFixedCells()) {
            if (cell2.getIndex() == index) {
                return (GridRow)cell2;
            }
        }
        return null;
    }
    
    public int getFirstRow(final SpreadsheetCell cell, int index) {
        while (--index >= 0 && ((ObservableList)this.spreadsheetView.getItems().get(index)).get(cell.getColumn()) == cell) {}
        return index + 1;
    }
    
    public GridRow getRow(final int index) {
        if (index < this.getFlow().getCells().size()) {
            return (GridRow)this.getFlow().getCells().get(index);
        }
        return null;
    }
    
    public final boolean containsRow(final int index) {
        for (final Object obj : this.getFlow().getCells()) {
            if (((GridRow)obj).getIndex() == index && !((GridRow)obj).getChildrenUnmodifiable().isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    public int getCellsSize() {
        return this.getFlow().getCells().size();
    }
    
    public ScrollBar getHBar() {
        if (this.getFlow() != null) {
            return this.getFlow().getHorizontalBar();
        }
        return null;
    }
    
    public ScrollBar getVBar() {
        return this.getFlow().getVerticalBar();
    }
    
    public void resizeRowsToFitContent() {
        final Grid grid = this.spreadsheetView.getGrid();
        for (int maxRows = this.handle.getView().getGrid().getRowCount(), row = 0; row < maxRows; ++row) {
            if (grid.isRowResizable(row)) {
                this.resizeRowToFitContent(row);
            }
        }
    }
    
    public void resizeRowToFitContent(final int modelRow) {
        if (((TableView)this.getSkinnable()).getColumns().isEmpty()) {
            return;
        }
        final TableColumn<ObservableList<SpreadsheetCell>, ?> col = (TableColumn<ObservableList<SpreadsheetCell>, ?>)((TableView)this.getSkinnable()).getColumns().get(0);
        final List<?> items = (List<?>)this.itemsProperty().get();
        if (items == null || items.isEmpty()) {
            return;
        }
        if (!this.spreadsheetView.getGrid().isRowResizable(modelRow)) {
            return;
        }
        final Callback cellFactory = col.getCellFactory();
        if (cellFactory == null) {
            return;
        }
        final CellView cell = (CellView)cellFactory.call((Object)col);
        if (cell == null) {
            return;
        }
        cell.getProperties().put((Object)"deferToParentPrefWidth", (Object)Boolean.TRUE);
        double padding = 5.0;
        final Node n = (cell.getSkin() == null) ? null : cell.getSkin().getNode();
        if (n instanceof Region) {
            final Region r = (Region)n;
            padding = r.snappedTopInset() + r.snappedBottomInset();
        }
        double maxHeight = 0.0;
        this.getChildren().add((Object)cell);
        for (int columnSize = ((TableView)this.getSkinnable()).getColumns().size(), viewColumn = 0; viewColumn < columnSize; ++viewColumn) {
            final TableColumn column = (TableColumn)((TableView)this.getSkinnable()).getColumns().get(viewColumn);
            cell.updateTableColumn(column);
            cell.updateTableView((TableView)this.handle.getGridView());
            cell.updateIndex(modelRow);
            final SpreadsheetCell spc = (SpreadsheetCell)cell.getItem();
            double width = column.getWidth();
            if (spc != null && spc.getColumn() == viewColumn && spc.getColumnSpan() > 1) {
                for (int max = ((TableView)this.getSkinnable()).getVisibleLeafColumns().size() - viewColumn, i = 1, colSpan = spc.getColumnSpan(); i < colSpan && i < max; ++i) {
                    final double tempWidth = this.snapSize(((TableView)this.getSkinnable()).getVisibleLeafColumn(viewColumn + i).getWidth());
                    width += tempWidth;
                }
            }
            if (spc != null && spc.getColumn() == viewColumn && ((cell.getText() != null && !cell.getText().isEmpty()) || cell.getGraphic() != null)) {
                cell.setWrapText(true);
                cell.impl_processCSS(false);
                maxHeight = Math.max(maxHeight, cell.prefHeight(width));
            }
        }
        this.getChildren().remove((Object)cell);
        this.rowHeightMap.put((Object)modelRow, (Object)(maxHeight + padding));
        Event.fireEvent((EventTarget)this.spreadsheetView, (Event)new SpreadsheetView.RowHeightEvent(modelRow, maxHeight + padding));
        this.rectangleSelection.updateRectangle();
    }
    
    public void resizeRowsToMaximum() {
        this.resizeRowsToFitContent();
        final Grid grid = this.spreadsheetView.getGrid();
        double maxHeight = 0.0;
        for (final int key : this.rowHeightMap.keySet()) {
            maxHeight = Math.max(maxHeight, (double)this.rowHeightMap.get((Object)key));
        }
        this.rowHeightMap.clear();
        for (int maxRows = this.handle.getView().getGrid().getRows().size(), modelRow = 0; modelRow < maxRows; ++modelRow) {
            if (grid.isRowResizable(modelRow)) {
                Event.fireEvent((EventTarget)this.spreadsheetView, (Event)new SpreadsheetView.RowHeightEvent(modelRow, maxHeight));
                this.rowHeightMap.put((Object)modelRow, (Object)maxHeight);
            }
        }
        this.rectangleSelection.updateRectangle();
    }
    
    public void resizeRowsToDefault() {
        this.rowHeightMap.clear();
        final Grid grid = this.spreadsheetView.getGrid();
        for (final GridRow row : this.getFlow().getCells()) {
            if (grid.isRowResizable(this.spreadsheetView.getModelRow(row.getIndex()))) {
                final double newHeight = row.computePrefHeight(-1.0);
                if (row.getPrefHeight() == newHeight) {
                    continue;
                }
                row.setRowHeight(newHeight);
                row.requestLayout();
            }
        }
        this.getFlow().layoutChildren();
        for (final GridRow row : this.getFlow().getCells()) {
            final double height = this.getRowHeight(this.spreadsheetView.getModelRow(row.getIndex()));
            if (row.getHeight() != height && grid.isRowResizable(this.spreadsheetView.getModelRow(row.getIndex()))) {
                row.setRowHeight(height);
            }
        }
        this.rectangleSelection.updateRectangle();
    }
    
    public void resizeColumnToFitContent(final TableColumn<ObservableList<SpreadsheetCell>, ?> tc, final int maxRows) {
        final List<?> items = (List<?>)this.itemsProperty().get();
        if (items == null || items.isEmpty()) {
            return;
        }
        final Callback cellFactory = tc.getCellFactory();
        if (cellFactory == null) {
            return;
        }
        final TableCell<ObservableList<SpreadsheetCell>, ?> cell = (TableCell<ObservableList<SpreadsheetCell>, ?>)cellFactory.call((Object)tc);
        if (cell == null) {
            return;
        }
        final int indexColumn = this.handle.getGridView().getColumns().indexOf((Object)tc);
        if (maxRows == 30 && this.handle.isColumnWidthSet(indexColumn)) {
            return;
        }
        cell.getProperties().put((Object)"deferToParentPrefWidth", (Object)Boolean.TRUE);
        double padding = 10.0;
        final Node n = (cell.getSkin() == null) ? null : cell.getSkin().getNode();
        if (n instanceof Region) {
            final Region r = (Region)n;
            padding = r.snappedLeftInset() + r.snappedRightInset();
        }
        final ObservableList<ObservableList<SpreadsheetCell>> gridRows = this.spreadsheetView.getGrid().getRows();
        final int rows = (maxRows == -1) ? items.size() : Math.min(items.size(), (maxRows == 30) ? 100 : maxRows);
        double maxWidth = 0.0;
        boolean datePresent = false;
        cell.updateTableColumn((TableColumn)tc);
        cell.updateTableView((TableView)this.handle.getGridView());
        if (cell.getSkin() == null) {
            cell.setSkin((Skin)new CellViewSkin((CellView)cell));
        }
        final SpreadsheetColumn column = (SpreadsheetColumn)this.spreadsheetView.getColumns().get(indexColumn);
        double cellFilterWidth = 0.0;
        for (int row = 0; row < rows; ++row) {
            cell.updateIndex(row);
            if ((cell.getText() != null && !cell.getText().isEmpty()) || cell.getGraphic() != null) {
                this.getChildren().add((Object)cell);
                if (((SpreadsheetCell)cell.getItem()).getItem() instanceof LocalDate) {
                    datePresent = true;
                }
                cell.impl_processCSS(false);
                double width = cell.prefWidth(-1.0);
                if (row == this.spreadsheetView.getFilteredRow()) {
                    cellFilterWidth = width;
                }
                final SpreadsheetCell spc = (SpreadsheetCell)((ObservableList)gridRows.get(row)).get(indexColumn);
                if (this.spreadsheetView.getColumnSpan(spc) > 1) {
                    for (int i = this.spreadsheetView.getViewColumn(spc.getColumn()); i < this.spreadsheetView.getViewColumn(spc.getColumn()) + this.spreadsheetView.getColumnSpan(spc); ++i) {
                        if (i != indexColumn) {
                            width -= ((SpreadsheetColumn)this.spreadsheetView.getColumns().get(i)).getWidth();
                        }
                    }
                }
                maxWidth = Math.max(maxWidth, width);
                this.getChildren().remove((Object)cell);
            }
        }
        cell.updateIndex(-1);
        double widthMax = maxWidth + padding;
        if (this.handle.getGridView().getColumnResizePolicy() == TableView.CONSTRAINED_RESIZE_POLICY) {
            widthMax = Math.max(widthMax, tc.getWidth());
        }
        if (datePresent && widthMax < GridViewSkin.DATE_CELL_MIN_WIDTH) {
            widthMax = GridViewSkin.DATE_CELL_MIN_WIDTH;
        }
        if (column.getFilter() != null) {
            cellFilterWidth += ((column.getFilter().getMenuButton().getWidth() <= 0.0) ? 24.0 : column.getFilter().getMenuButton().getWidth());
            widthMax = Math.max(widthMax, cellFilterWidth);
        }
        widthMax = this.snapSize(widthMax);
        if (tc.getPrefWidth() == widthMax && tc.getWidth() != widthMax) {
            tc.impl_setWidth(widthMax);
        }
        else {
            tc.setPrefWidth(widthMax);
        }
        this.rectangleSelection.updateRectangle();
    }
    
    protected final void init() {
        this.rectangleSelection = new RectangleSelection(this, (TableViewSpanSelectionModel)this.handle.getGridView().getSelectionModel());
        this.getFlow().getVerticalBar().valueProperty().addListener(this.vbarValueListener);
        this.verticalHeader = new VerticalHeader(this.handle);
        this.getChildren().add((Object)this.verticalHeader);
        ((HorizontalHeader)this.getTableHeaderRow()).init();
        this.verticalHeader.init(this, (HorizontalHeader)this.getTableHeaderRow());
        this.horizontalPickers = new HorizontalPicker((HorizontalHeader)this.getTableHeaderRow(), this.spreadsheetView);
        this.getChildren().add((Object)this.horizontalPickers);
        this.getFlow().init(this.spreadsheetView);
        ((GridViewBehavior)this.getBehavior()).setGridViewSkin(this);
    }
    
    protected final ObservableSet<Integer> getCurrentlyFixedRow() {
        return this.currentlyFixedRow;
    }
    
    public void resize(final TableColumnBase<?, ?> tc, final int maxRows) {
        if (tc.isResizable()) {
            final int columnIndex = this.getColumns().indexOf((Object)tc);
            final TableColumn tableColumn = (TableColumn)this.getColumns().get(columnIndex);
            this.resizeColumnToFitContent((TableColumn<ObservableList<SpreadsheetCell>, ?>)tableColumn, maxRows);
            Event.fireEvent((EventTarget)this.spreadsheetView, (Event)new SpreadsheetView.ColumnWidthEvent(columnIndex, tableColumn.getWidth()));
        }
    }
    
    protected void layoutChildren(double x, double y, double w, final double h) {
        if (this.spreadsheetView == null) {
            return;
        }
        final double verticalHeaderWidth = this.verticalHeader.computeHeaderWidth();
        final double horizontalPickerHeight = this.spreadsheetView.getColumnPickers().isEmpty() ? 0.0 : 16.0;
        if (this.spreadsheetView.isShowRowHeader() || !this.spreadsheetView.getRowPickers().isEmpty()) {
            x += verticalHeaderWidth;
            w -= verticalHeaderWidth;
        }
        else {
            x = 0.0;
        }
        y += horizontalPickerHeight;
        super.layoutChildren(x, y, w, h - horizontalPickerHeight);
        final double baselineOffset = ((TableView)this.getSkinnable()).getLayoutBounds().getHeight() / 2.0;
        double tableHeaderRowHeight = 0.0;
        if (!this.spreadsheetView.getColumnPickers().isEmpty()) {
            this.layoutInArea((Node)this.horizontalPickers, x, y - 16.0, w, tableHeaderRowHeight, baselineOffset, HPos.CENTER, VPos.CENTER);
        }
        if (this.spreadsheetView.showColumnHeaderProperty().get()) {
            tableHeaderRowHeight = this.getTableHeaderRow().prefHeight(-1.0);
            tableHeaderRowHeight = ((tableHeaderRowHeight < GridViewSkin.DEFAULT_CELL_HEIGHT) ? GridViewSkin.DEFAULT_CELL_HEIGHT : tableHeaderRowHeight);
            this.layoutInArea((Node)this.getTableHeaderRow(), x, y, w, tableHeaderRowHeight, baselineOffset, HPos.CENTER, VPos.CENTER);
            y += tableHeaderRowHeight;
        }
        if (this.spreadsheetView.isShowRowHeader() || !this.spreadsheetView.getRowPickers().isEmpty()) {
            this.layoutInArea((Node)this.verticalHeader, x - verticalHeaderWidth, y - tableHeaderRowHeight, w, h, baselineOffset, HPos.CENTER, VPos.CENTER);
        }
    }
    
    protected void onFocusPreviousCell() {
        this.focusScroll();
    }
    
    protected void onFocusNextCell() {
        this.focusScroll();
    }
    
    private int getFixedRowSize() {
        int i = 0;
        for (final Integer fixedRow : this.spreadsheetView.getFixedRows()) {
            if (!this.spreadsheetView.getHiddenRows().get(fixedRow)) {
                ++i;
            }
        }
        return i;
    }
    
    void focusScroll() {
        final TableFocusModel<?, ?> fm = this.getFocusModel();
        if (fm == null) {
            return;
        }
        final int row = fm.getFocusedIndex();
        if (!this.getFlow().getCells().isEmpty() && ((IndexedCell)this.getFlow().getCells().get(this.getFixedRowSize())).getIndex() > row && !this.spreadsheetView.getFixedRows().contains((Object)this.spreadsheetView.getModelRow(row))) {
            this.flow.scrollTo(row);
        }
        else {
            this.flow.show(row);
        }
        this.scrollHorizontally();
    }
    
    protected void onSelectPreviousCell() {
        super.onSelectPreviousCell();
        this.scrollHorizontally();
    }
    
    protected void onSelectNextCell() {
        super.onSelectNextCell();
        this.scrollHorizontally();
    }
    
    protected VirtualFlow<TableRow<ObservableList<SpreadsheetCell>>> createVirtualFlow() {
        return new GridVirtualFlow<TableRow<ObservableList<SpreadsheetCell>>>(this);
    }
    
    protected TableHeaderRow createTableHeaderRow() {
        return new HorizontalHeader(this);
    }
    
    protected HorizontalHeader getHorizontalHeader() {
        return (HorizontalHeader)this.getTableHeaderRow();
    }
    
    BooleanProperty getTableMenuButtonVisibleProperty() {
        return this.tableMenuButtonVisibleProperty();
    }
    
    public void scrollHorizontally() {
        super.scrollHorizontally();
    }
    
    protected void scrollHorizontally(final TableColumn<ObservableList<SpreadsheetCell>, ?> col) {
        if (col == null || !col.isVisible()) {
            return;
        }
        this.fixedColumnWidth = 0.0;
        final double pos = this.getFlow().getHorizontalBar().getValue();
        final int index = this.getColumns().indexOf((Object)col);
        double start = 0.0;
        for (int columnIndex = 0; columnIndex < index; ++columnIndex) {
            if (!this.spreadsheetView.isColumnHidden(columnIndex)) {
                final SpreadsheetColumn column = (SpreadsheetColumn)this.spreadsheetView.getColumns().get(columnIndex);
                if (column.isFixed()) {
                    this.fixedColumnWidth += column.getWidth();
                }
                start += column.getWidth();
            }
        }
        final double end = start + col.getWidth();
        final double headerWidth = this.handle.getView().getWidth() - this.snappedLeftInset() - this.snappedRightInset() - this.verticalHeader.getVerticalHeaderWidth();
        final double max = this.getFlow().getHorizontalBar().getMax();
        if (start < pos + this.fixedColumnWidth && start >= 0.0 && start >= this.fixedColumnWidth) {
            final double newPos = (start - this.fixedColumnWidth < 0.0) ? start : (start - this.fixedColumnWidth);
            this.getFlow().getHorizontalBar().setValue(newPos);
        }
        else if (start > pos + headerWidth) {
            final double delta = (start < 0.0 || end > headerWidth) ? (start - pos - this.fixedColumnWidth) : 0.0;
            final double newPos = (pos + delta > max) ? max : (pos + delta);
            this.getFlow().getHorizontalBar().setValue(newPos);
        }
    }
    
    private void verticalScroll() {
        this.verticalHeader.requestLayout();
    }
    
    GridVirtualFlow<?> getFlow() {
        return (GridVirtualFlow<?>)this.flow;
    }
    
    private BitSet initRowToLayoutBitSet() {
        final int rowCount = this.getItemCount();
        final BitSet bitSet = new BitSet(rowCount);
        for (int row = 0; row < rowCount; ++row) {
            if (this.spreadsheetView.getFixedRows().contains((Object)this.spreadsheetView.getModelRow(row))) {
                bitSet.set(row);
            }
            else {
                final List<SpreadsheetCell> myRow = (List<SpreadsheetCell>)this.handle.getGridView().getItems().get(row);
                for (final SpreadsheetCell cell : myRow) {
                    if (this.spreadsheetView.getRowSpanFilter(cell) > 1) {
                        bitSet.set(row);
                        break;
                    }
                }
            }
        }
        return bitSet;
    }
    
    public void computeFixedRowHeight() {
        this.fixedRowHeight = 0.0;
        for (final int i : this.getCurrentlyFixedRow()) {
            this.fixedRowHeight += this.getRowHeight(i);
        }
    }
    
    protected TableSelectionModel<ObservableList<SpreadsheetCell>> getSelectionModel() {
        return (TableSelectionModel<ObservableList<SpreadsheetCell>>)((TableView)this.getSkinnable()).getSelectionModel();
    }
    
    protected TableFocusModel<ObservableList<SpreadsheetCell>, TableColumn<ObservableList<SpreadsheetCell>, ?>> getFocusModel() {
        return (TableFocusModel<ObservableList<SpreadsheetCell>, TableColumn<ObservableList<SpreadsheetCell>, ?>>)((TableView)this.getSkinnable()).getFocusModel();
    }
    
    protected TablePositionBase<? extends TableColumn<ObservableList<SpreadsheetCell>, ?>> getFocusedCell() {
        return (TablePositionBase<? extends TableColumn<ObservableList<SpreadsheetCell>, ?>>)((TableView)this.getSkinnable()).getFocusModel().getFocusedCell();
    }
    
    protected ObservableList<? extends TableColumn<ObservableList<SpreadsheetCell>, ?>> getVisibleLeafColumns() {
        return (ObservableList<? extends TableColumn<ObservableList<SpreadsheetCell>, ?>>)((TableView)this.getSkinnable()).getVisibleLeafColumns();
    }
    
    protected int getVisibleLeafIndex(final TableColumn<ObservableList<SpreadsheetCell>, ?> tc) {
        return ((TableView)this.getSkinnable()).getVisibleLeafIndex((TableColumn)tc);
    }
    
    protected TableColumn<ObservableList<SpreadsheetCell>, ?> getVisibleLeafColumn(final int col) {
        return (TableColumn<ObservableList<SpreadsheetCell>, ?>)((TableView)this.getSkinnable()).getVisibleLeafColumn(col);
    }
    
    protected ObservableList<TableColumn<ObservableList<SpreadsheetCell>, ?>> getColumns() {
        return (ObservableList<TableColumn<ObservableList<SpreadsheetCell>, ?>>)((TableView)this.getSkinnable()).getColumns();
    }
    
    protected ObservableList<TableColumn<ObservableList<SpreadsheetCell>, ?>> getSortOrder() {
        return (ObservableList<TableColumn<ObservableList<SpreadsheetCell>, ?>>)((TableView)this.getSkinnable()).getSortOrder();
    }
    
    protected ObjectProperty<ObservableList<ObservableList<SpreadsheetCell>>> itemsProperty() {
        return (ObjectProperty<ObservableList<ObservableList<SpreadsheetCell>>>)((TableView)this.getSkinnable()).itemsProperty();
    }
    
    protected ObjectProperty<Callback<TableView<ObservableList<SpreadsheetCell>>, TableRow<ObservableList<SpreadsheetCell>>>> rowFactoryProperty() {
        return (ObjectProperty<Callback<TableView<ObservableList<SpreadsheetCell>>, TableRow<ObservableList<SpreadsheetCell>>>>)((TableView)this.getSkinnable()).rowFactoryProperty();
    }
    
    protected ObjectProperty<Node> placeholderProperty() {
        return (ObjectProperty<Node>)((TableView)this.getSkinnable()).placeholderProperty();
    }
    
    protected BooleanProperty tableMenuButtonVisibleProperty() {
        return ((TableView)this.getSkinnable()).tableMenuButtonVisibleProperty();
    }
    
    protected ObjectProperty<Callback<ResizeFeaturesBase, Boolean>> columnResizePolicyProperty() {
        return (ObjectProperty<Callback<ResizeFeaturesBase, Boolean>>)((TableView)this.getSkinnable()).columnResizePolicyProperty();
    }
    
    protected boolean resizeColumn(final TableColumn<ObservableList<SpreadsheetCell>, ?> tc, final double delta) {
        this.getHorizontalHeader().getRootHeader().lastColumnResized = this.getColumns().indexOf((Object)tc);
        final boolean returnedValue = ((TableView)this.getSkinnable()).resizeColumn((TableColumn)tc, delta);
        if (returnedValue) {
            Event.fireEvent((EventTarget)this.spreadsheetView, (Event)new SpreadsheetView.ColumnWidthEvent(this.getColumns().indexOf((Object)tc), tc.getWidth()));
        }
        return returnedValue;
    }
    
    protected void edit(final int index, final TableColumn<ObservableList<SpreadsheetCell>, ?> column) {
        ((TableView)this.getSkinnable()).edit(index, (TableColumn)column);
    }
    
    public TableRow<ObservableList<SpreadsheetCell>> createCell() {
        TableRow<ObservableList<SpreadsheetCell>> cell;
        if (((TableView)this.getSkinnable()).getRowFactory() != null) {
            cell = (TableRow<ObservableList<SpreadsheetCell>>)((TableView)this.getSkinnable()).getRowFactory().call((Object)this.getSkinnable());
        }
        else {
            cell = (TableRow<ObservableList<SpreadsheetCell>>)new TableRow();
        }
        cell.updateTableView((TableView)this.getSkinnable());
        return cell;
    }
    
    public final int getItemCount() {
        return (((TableView)this.getSkinnable()).getItems() == null) ? 0 : ((TableView)this.getSkinnable()).getItems().size();
    }
    
    public void setHbarValue(final double value) {
        this.setHbarValue(value, 0);
    }
    
    public void setHbarValue(final double value, final int count) {
        if (count > 5) {
            return;
        }
        final int newCount = count + 1;
        if (this.flow.getScene() == null) {
            Platform.runLater(() -> this.setHbarValue(value, newCount));
            return;
        }
        this.getHBar().setValue(value);
    }
    
    static /* synthetic */ void access$000(final GridViewSkin x0) {
        x0.onMoveToFirstCell();
    }
    
    static /* synthetic */ void access$100(final GridViewSkin x0) {
        x0.onMoveToLastCell();
    }
    
    static /* synthetic */ void access$200(final GridViewSkin x0) {
        x0.onSelectLeftCell();
    }
    
    static /* synthetic */ void access$300(final GridViewSkin x0) {
        x0.onSelectRightCell();
    }
    
    static {
        DATE_CELL_MIN_WIDTH = 200.0 - Screen.getPrimary().getDpi();
        double cell_size = 24.0;
        try {
            final Class<?> clazz = CellSkinBase.class;
            final Field f = clazz.getDeclaredField("DEFAULT_CELL_SIZE");
            f.setAccessible(true);
            cell_size = f.getDouble(null);
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        catch (SecurityException e2) {
            e2.printStackTrace();
        }
        catch (IllegalArgumentException e3) {
            e3.printStackTrace();
        }
        catch (IllegalAccessException e4) {
            e4.printStackTrace();
        }
        DEFAULT_CELL_HEIGHT = cell_size;
    }
}
