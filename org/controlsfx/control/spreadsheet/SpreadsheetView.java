// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.spreadsheet;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableCell;
import impl.org.controlsfx.spreadsheet.GridViewBehavior;
import org.controlsfx.tools.Utils;
import javafx.scene.control.Menu;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.MenuItem;
import impl.org.controlsfx.i18n.Localization;
import impl.org.controlsfx.spreadsheet.RectangleSelection;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.FutureTask;
import javafx.scene.control.TablePosition;
import javafx.util.Pair;
import java.util.Collection;
import java.util.Comparator;
import javafx.scene.control.TableColumn;
import java.util.function.Predicate;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Scale;
import impl.org.controlsfx.spreadsheet.CellView;
import javafx.beans.value.WeakChangeListener;
import impl.org.controlsfx.spreadsheet.FocusModelListener;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import impl.org.controlsfx.spreadsheet.TableViewSpanSelectionModel;
import javafx.scene.control.Skinnable;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.beans.Observable;
import javafx.beans.InvalidationListener;
import javafx.event.EventType;
import javafx.event.Event;
import javafx.application.Platform;
import javafx.event.WeakEventHandler;
import javafx.beans.value.ObservableValue;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import java.util.IdentityHashMap;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import java.util.Iterator;
import impl.org.controlsfx.spreadsheet.GridViewSkin;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import impl.org.controlsfx.spreadsheet.SpreadsheetHandle;
import javafx.collections.transformation.SortedList;
import javafx.collections.transformation.FilteredList;
import java.util.HashMap;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.DoubleProperty;
import java.util.Map;
import javafx.collections.ObservableMap;
import java.util.BitSet;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.input.DataFormat;
import javafx.beans.property.SimpleObjectProperty;
import impl.org.controlsfx.spreadsheet.SpreadsheetGridView;
import javafx.scene.control.Control;

public class SpreadsheetView extends Control
{
    private static final double DEFAULT_ROW_HEADER_WIDTH = 30.0;
    private final SpreadsheetGridView cellsView;
    private SimpleObjectProperty<Grid> gridProperty;
    private DataFormat fmt;
    private final ObservableList<Integer> fixedRows;
    private final ObservableList<SpreadsheetColumn> fixedColumns;
    private final BooleanProperty fixingRowsAllowedProperty;
    private final BooleanProperty fixingColumnsAllowedProperty;
    private final BooleanProperty showColumnHeader;
    private final BooleanProperty showRowHeader;
    private BitSet rowFix;
    private final ObservableMap<Integer, Picker> rowPickers;
    private final ObservableMap<Integer, Picker> columnPickers;
    private ObservableList<SpreadsheetColumn> columns;
    private Map<SpreadsheetCellType<?>, SpreadsheetCellEditor> editors;
    private final SpreadsheetViewSelectionModel selectionModel;
    private final DoubleProperty rowHeaderWidth;
    private DoubleProperty zoomFactor;
    private static final double MIN_ZOOM = 0.1;
    private static final double MAX_ZOOM = 2.0;
    private static final double STEP_ZOOM = 0.1;
    private final ObjectProperty<BitSet> hiddenRowsProperty;
    private final ObjectProperty<BitSet> hiddenColumnsProperty;
    private HashMap<Integer, Integer> rowMap;
    private HashMap<Integer, Integer> columnMap;
    private Integer filteredRow;
    private FilteredList<ObservableList<SpreadsheetCell>> filteredList;
    private SortedList<ObservableList<SpreadsheetCell>> sortedList;
    private final BitSet columnWidthSet;
    final SpreadsheetHandle handle;
    private final ListChangeListener<Integer> fixedRowsListener;
    private final ListChangeListener<SpreadsheetColumn> fixedColumnsListener;
    private final ChangeListener<ContextMenu> contextMenuChangeListener;
    private final EventHandler<WindowEvent> hideContextMenuEventHandler;
    private final EventHandler<KeyEvent> keyPressedHandler;
    
    final GridViewSkin getCellsViewSkin() {
        return (GridViewSkin)this.cellsView.getSkin();
    }
    
    final SpreadsheetGridView getCellsView() {
        return this.cellsView;
    }
    
    void columnWidthSet(final int indexColumn) {
        this.columnWidthSet.set(indexColumn);
    }
    
    public SpreadsheetView() {
        this(getSampleGrid());
        for (final SpreadsheetColumn column : this.getColumns()) {
            column.setPrefWidth(100.0);
        }
    }
    
    public SpreadsheetView(final Grid grid) {
        this.gridProperty = (SimpleObjectProperty<Grid>)new SimpleObjectProperty();
        this.fixedRows = (ObservableList<Integer>)FXCollections.observableArrayList();
        this.fixedColumns = (ObservableList<SpreadsheetColumn>)FXCollections.observableArrayList();
        this.fixingRowsAllowedProperty = (BooleanProperty)new SimpleBooleanProperty(true);
        this.fixingColumnsAllowedProperty = (BooleanProperty)new SimpleBooleanProperty(true);
        this.showColumnHeader = (BooleanProperty)new SimpleBooleanProperty((Object)true, "showColumnHeader", true);
        this.showRowHeader = (BooleanProperty)new SimpleBooleanProperty((Object)true, "showRowHeader", true);
        this.rowPickers = (ObservableMap<Integer, Picker>)FXCollections.observableHashMap();
        this.columnPickers = (ObservableMap<Integer, Picker>)FXCollections.observableHashMap();
        this.columns = (ObservableList<SpreadsheetColumn>)FXCollections.observableArrayList();
        this.editors = new IdentityHashMap<SpreadsheetCellType<?>, SpreadsheetCellEditor>();
        this.rowHeaderWidth = (DoubleProperty)new SimpleDoubleProperty(30.0);
        this.zoomFactor = (DoubleProperty)new SimpleDoubleProperty(1.0);
        this.hiddenRowsProperty = (ObjectProperty<BitSet>)new SimpleObjectProperty();
        this.hiddenColumnsProperty = (ObjectProperty<BitSet>)new SimpleObjectProperty();
        this.columnMap = new HashMap<Integer, Integer>();
        this.columnWidthSet = new BitSet();
        this.handle = new SpreadsheetHandle() {
            @Override
            protected SpreadsheetView getView() {
                return SpreadsheetView.this;
            }
            
            @Override
            protected GridViewSkin getCellsViewSkin() {
                return SpreadsheetView.this.getCellsViewSkin();
            }
            
            @Override
            protected SpreadsheetGridView getGridView() {
                return SpreadsheetView.this.getCellsView();
            }
            
            @Override
            protected boolean isColumnWidthSet(final int indexColumn) {
                return SpreadsheetView.this.columnWidthSet.get(indexColumn);
            }
        };
        this.fixedRowsListener = (ListChangeListener<Integer>)new ListChangeListener<Integer>() {
            public void onChanged(final ListChangeListener.Change<? extends Integer> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        final List<? extends Integer> newRows = (List<? extends Integer>)c.getAddedSubList();
                        if (!SpreadsheetView.this.areRowsFixable(newRows)) {
                            throw new IllegalArgumentException(SpreadsheetView.this.computeReason(newRows));
                        }
                        FXCollections.sort(SpreadsheetView.this.fixedRows);
                    }
                    if (c.wasRemoved()) {
                        continue;
                    }
                }
            }
        };
        this.fixedColumnsListener = (ListChangeListener<SpreadsheetColumn>)new ListChangeListener<SpreadsheetColumn>() {
            public void onChanged(final ListChangeListener.Change<? extends SpreadsheetColumn> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        final List<? extends SpreadsheetColumn> newColumns = (List<? extends SpreadsheetColumn>)c.getAddedSubList();
                        if (!SpreadsheetView.this.areSpreadsheetColumnsFixable(newColumns)) {
                            final List<Integer> newList = new ArrayList<Integer>();
                            for (final SpreadsheetColumn column : newColumns) {
                                if (column != null) {
                                    newList.add(SpreadsheetView.this.columns.indexOf((Object)column));
                                }
                            }
                            throw new IllegalArgumentException(this.computeReason(newList));
                        }
                        continue;
                    }
                }
            }
            
            private String computeReason(final List<Integer> list) {
                String reason = "\n This column cannot be fixed.";
                final ObservableList<ObservableList<SpreadsheetCell>> rows = SpreadsheetView.this.getGrid().getRows();
                for (final Integer columnIndex : list) {
                    if (!SpreadsheetView.this.isColumnFixable(columnIndex)) {
                        int maxSpan = 1;
                        for (final List<SpreadsheetCell> row : rows) {
                            final SpreadsheetCell cell = row.get(columnIndex);
                            if (!list.contains(cell.getColumn())) {
                                reason = reason + "The column " + columnIndex + " is inside a column span and the starting column " + cell.getColumn() + " is not fixed.\n";
                            }
                            if (cell.getColumnSpan() > maxSpan && cell.getColumn() == columnIndex) {
                                maxSpan = cell.getColumnSpan();
                            }
                        }
                        for (int count = columnIndex + maxSpan - 1, index = columnIndex + 1; index < count; ++index) {
                            if (!list.contains(index)) {
                                reason = reason + "One cell on the column " + columnIndex + " has a column span of " + maxSpan + ". But the column " + index + " contained within that span is not fixed.\n";
                            }
                        }
                    }
                }
                return reason;
            }
        };
        this.contextMenuChangeListener = (ChangeListener<ContextMenu>)new ChangeListener<ContextMenu>() {
            public void changed(final ObservableValue<? extends ContextMenu> arg0, final ContextMenu oldContextMenu, final ContextMenu newContextMenu) {
                if (oldContextMenu != null) {
                    oldContextMenu.setOnShowing((EventHandler)null);
                }
                if (newContextMenu != null) {
                    newContextMenu.setOnShowing((EventHandler)new WeakEventHandler(SpreadsheetView.this.hideContextMenuEventHandler));
                }
            }
        };
        this.hideContextMenuEventHandler = (EventHandler<WindowEvent>)new EventHandler<WindowEvent>() {
            public void handle(final WindowEvent arg0) {
                if (SpreadsheetView.this.getEditingCell() != null) {
                    Platform.runLater(() -> SpreadsheetView.this.getContextMenu().hide());
                }
            }
        };
        this.keyPressedHandler = (EventHandler<KeyEvent>)(keyEvent -> {
            final TablePosition<ObservableList<SpreadsheetCell>, ?> position = (TablePosition<ObservableList<SpreadsheetCell>, ?>)this.getSelectionModel().getFocusedCell();
            if (this.getEditingCell() == null && KeyCode.ENTER.equals((Object)keyEvent.getCode())) {
                if (position != null) {
                    if (keyEvent.isShiftDown()) {
                        ((GridViewBehavior)this.getCellsViewSkin().getBehavior()).selectCell(-1, 0);
                    }
                    else {
                        ((GridViewBehavior)this.getCellsViewSkin().getBehavior()).selectCell(1, 0);
                    }
                    keyEvent.consume();
                }
                this.getCellsViewSkin().scrollHorizontally();
            }
            else if (this.getEditingCell() == null && KeyCode.TAB.equals((Object)keyEvent.getCode()) && !keyEvent.isShortcutDown()) {
                if (position != null) {
                    if (keyEvent.isShiftDown()) {
                        this.getSelectionModel().clearAndSelectLeftCell();
                    }
                    else {
                        this.getSelectionModel().clearAndSelectRightCell();
                    }
                }
                keyEvent.consume();
                this.getCellsViewSkin().scrollHorizontally();
            }
            else if (KeyCode.DELETE.equals((Object)keyEvent.getCode())) {
                this.deleteSelectedCells();
            }
            else if (this.isEditionKey(keyEvent)) {
                this.getCellsView().edit(position.getRow(), position.getTableColumn());
            }
            else if (keyEvent.isShortcutDown() && (KeyCode.NUMPAD0.equals((Object)keyEvent.getCode()) || KeyCode.DIGIT0.equals((Object)keyEvent.getCode()))) {
                this.setZoomFactor(1.0);
            }
            else if (keyEvent.isShortcutDown() && KeyCode.ADD.equals((Object)keyEvent.getCode())) {
                this.incrementZoom();
            }
            else if (keyEvent.isShortcutDown() && KeyCode.SUBTRACT.equals((Object)keyEvent.getCode())) {
                this.decrementZoom();
            }
        });
        this.addEventHandler((EventType)RowHeightEvent.ROW_HEIGHT_CHANGE, event -> {
            if (this.getFixedRows().contains((Object)this.getModelRow(event.getRow())) && this.getCellsViewSkin() != null) {
                this.getCellsViewSkin().computeFixedRowHeight();
            }
        });
        this.hiddenRowsProperty.addListener((InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable observable) {
                SpreadsheetView.this.computeRowMap();
                SpreadsheetView.this.initRowFix(grid);
            }
        });
        this.hiddenColumnsProperty.addListener((InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable observable) {
                SpreadsheetView.this.computeColumnMap();
                SpreadsheetView.this.initRowFix(grid);
            }
        });
        this.getStyleClass().add((Object)"SpreadsheetView");
        this.setSkin((Skin)new Skin<SpreadsheetView>() {
            public Node getNode() {
                return (Node)SpreadsheetView.this.getCellsView();
            }
            
            public SpreadsheetView getSkinnable() {
                return SpreadsheetView.this;
            }
            
            public void dispose() {
            }
        });
        this.cellsView = new SpreadsheetGridView(this.handle);
        this.getChildren().add((Object)this.cellsView);
        final TableViewSpanSelectionModel tableViewSpanSelectionModel = new TableViewSpanSelectionModel(this, this.cellsView);
        this.cellsView.setSelectionModel((TableView.TableViewSelectionModel)tableViewSpanSelectionModel);
        tableViewSpanSelectionModel.setCellSelectionEnabled(true);
        tableViewSpanSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);
        this.selectionModel = new SpreadsheetViewSelectionModel(this, tableViewSpanSelectionModel);
        this.cellsView.getFocusModel().focusedCellProperty().addListener((ChangeListener)new FocusModelListener(this, this.cellsView));
        this.cellsView.setOnKeyPressed((EventHandler)this.keyPressedHandler);
        this.contextMenuProperty().addListener((ChangeListener)new WeakChangeListener((ChangeListener)this.contextMenuChangeListener));
        CellView.getValue(() -> this.setContextMenu(this.getSpreadsheetViewContextMenu()));
        this.setGrid(grid);
        this.setEditable(true);
        this.fixedRows.addListener((ListChangeListener)this.fixedRowsListener);
        this.fixedColumns.addListener((ListChangeListener)this.fixedColumnsListener);
        final Scale scale = new Scale(1.0, 1.0);
        this.getTransforms().add((Object)scale);
        this.zoomFactor.addListener((ChangeListener)new ChangeListener<Number>() {
            public void changed(final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) {
                scale.setX(newValue.doubleValue());
                scale.setY(newValue.doubleValue());
                SpreadsheetView.this.requestLayout();
            }
        });
        this.addEventFilter(ScrollEvent.ANY, event -> {
            if (event.isShortcutDown()) {
                if (event.getTextDeltaY() > 0.0) {
                    this.incrementZoom();
                }
                else {
                    this.decrementZoom();
                }
                event.consume();
            }
        });
    }
    
    protected void layoutChildren() {
        super.layoutChildren();
        final Pos pos = Pos.TOP_LEFT;
        final double width = this.getWidth();
        final double height = this.getHeight();
        final double top = this.getInsets().getTop();
        final double right = this.getInsets().getRight();
        final double left = this.getInsets().getLeft();
        final double bottom = this.getInsets().getBottom();
        final double contentWidth = (width - left - right) / this.zoomFactor.get();
        final double contentHeight = (height - top - bottom) / this.zoomFactor.get();
        this.layoutInArea((Node)this.getChildren().get(0), left, top, contentWidth, contentHeight, 0.0, (Insets)null, pos.getHpos(), pos.getVpos());
    }
    
    public boolean isRowHidden(final int row) {
        return ((BitSet)this.hiddenRowsProperty.get()).get(row);
    }
    
    public BitSet getHiddenRows() {
        return (BitSet)this.hiddenRowsProperty.get();
    }
    
    public final ObjectProperty<BitSet> hiddenRowsProperty() {
        return this.hiddenRowsProperty;
    }
    
    public void setHiddenRows(final BitSet hiddenRows) {
        final BitSet bitSet = new BitSet(hiddenRows.size());
        bitSet.or(hiddenRows);
        this.hiddenRowsProperty.setValue((Object)bitSet);
        this.requestLayout();
    }
    
    public void setHiddenColumns(final BitSet hiddenColumns) {
        final BitSet bitSet = new BitSet(hiddenColumns.size());
        bitSet.or(hiddenColumns);
        this.hiddenColumnsProperty.setValue((Object)bitSet);
        this.requestLayout();
    }
    
    public boolean isColumnHidden(final int column) {
        return ((BitSet)this.hiddenColumnsProperty.get()).get(column);
    }
    
    public BitSet getHiddenColumns() {
        return (BitSet)this.hiddenColumnsProperty.get();
    }
    
    public final ObjectProperty<BitSet> hiddenColumnsProperty() {
        return this.hiddenColumnsProperty;
    }
    
    public int getFilteredRow() {
        return (this.filteredRow == null) ? -1 : this.filteredRow;
    }
    
    public void setFilteredRow(final Integer row) {
        if (row == null || row > this.getGrid().getRowCount()) {
            this.filteredRow = null;
        }
        else {
            this.filteredRow = row;
        }
    }
    
    public void hideRow(final int row) {
        if (this.getHiddenRows().get(row)) {
            return;
        }
        this.getHiddenRows().set(row, true);
        final BitSet bitSet = new BitSet(this.getHiddenRows().size());
        bitSet.or(this.getHiddenRows());
        this.setHiddenRows(bitSet);
    }
    
    public void hideColumn(final SpreadsheetColumn column) {
        final int indexColumn = this.getColumns().indexOf((Object)column);
        if (this.getHiddenColumns().get(indexColumn)) {
            return;
        }
        this.getHiddenColumns().set(indexColumn, true);
        final BitSet bitSet = new BitSet(this.getHiddenColumns().size());
        bitSet.or(this.getHiddenColumns());
        this.setHiddenColumns(bitSet);
    }
    
    private void computeRowMap() {
        if (this.getHiddenRows().isEmpty()) {
            this.filteredList.setPredicate((Predicate)null);
        }
        else {
            this.filteredList.setPredicate((Predicate)new Predicate<ObservableList<SpreadsheetCell>>() {
                @Override
                public boolean test(final ObservableList<SpreadsheetCell> t) {
                    final int index = SpreadsheetView.this.getGrid().getRows().indexOf((Object)t);
                    return !SpreadsheetView.this.getHiddenRows().get(index) || index == SpreadsheetView.this.getFilteredRow();
                }
            });
        }
        final int rowCount = this.getGrid().getRowCount();
        this.rowMap = new HashMap<Integer, Integer>(rowCount);
        int visibleRow = 0;
        for (int i = 0; i < rowCount; ++i) {
            if (!this.getHiddenRows().get(i)) {
                this.rowMap.put(i, visibleRow++);
            }
            else {
                this.rowMap.put(i, visibleRow);
            }
        }
    }
    
    private void computeColumnMap() {
        final int columnCount = this.getGrid().getColumnCount();
        this.columnMap = new HashMap<Integer, Integer>(columnCount);
        final int columnSize;
        final int totalColumn;
        int visibleColumn;
        int i;
        HashMap<Integer, Integer> columnMap;
        final Object o;
        final int n;
        CellView.getValue(() -> {
            columnSize = this.getColumns().size();
            totalColumn = this.getGrid().getColumnCount();
            visibleColumn = 0;
            for (i = 0; i < totalColumn; ++i) {
                if (!this.getHiddenColumns().get(i)) {
                    if (i < columnSize) {
                        ((SpreadsheetColumn)this.getColumns().get(i)).column.setVisible(true);
                    }
                    columnMap = this.columnMap;
                    i;
                    visibleColumn++;
                    columnMap.put((Integer)o, n);
                }
                else {
                    if (i < columnSize) {
                        ((SpreadsheetColumn)this.getColumns().get(i)).column.setVisible(false);
                    }
                    this.columnMap.put(i, visibleColumn);
                }
            }
        });
    }
    
    public void showRow(final int row) {
        if (!this.getHiddenRows().get(row)) {
            return;
        }
        this.getHiddenRows().set(row, false);
        final BitSet bitSet = new BitSet(this.getHiddenRows().size());
        bitSet.or(this.getHiddenRows());
        this.setHiddenRows(bitSet);
    }
    
    public void showColumn(final SpreadsheetColumn column) {
        final int indexColumn = this.getColumns().indexOf((Object)column);
        if (!this.getHiddenColumns().get(indexColumn)) {
            return;
        }
        this.getHiddenColumns().set(indexColumn, false);
        final BitSet bitSet = new BitSet(this.getHiddenColumns().size());
        bitSet.or(this.getHiddenColumns());
        this.setHiddenColumns(bitSet);
    }
    
    public int getFilteredRow(final int modelRow) {
        try {
            return this.rowMap.get(modelRow);
        }
        catch (NullPointerException ex) {
            return modelRow;
        }
    }
    
    public int getViewColumn(final int modelColumn) {
        try {
            return this.columnMap.get(modelColumn);
        }
        catch (NullPointerException ex) {
            return modelColumn;
        }
    }
    
    public int getModelColumn(final int viewColumn) {
        try {
            return this.cellsView.getColumns().indexOf((Object)this.cellsView.getVisibleLeafColumn(viewColumn));
        }
        catch (NullPointerException ex) {
            return viewColumn;
        }
    }
    
    public int getModelRow(final int viewRow) {
        if (viewRow < 0 || viewRow >= this.sortedList.size()) {
            return viewRow;
        }
        try {
            return this.getFilteredSourceIndex(this.sortedList.getSourceIndex(viewRow));
        }
        catch (NullPointerException | IndexOutOfBoundsException ex3) {
            final RuntimeException ex2;
            final RuntimeException ex = ex2;
            return viewRow;
        }
    }
    
    public int getFilteredSourceIndex(final int viewRow) {
        if (viewRow < 0 || viewRow >= this.filteredList.size()) {
            return viewRow;
        }
        try {
            return this.filteredList.getSourceIndex(viewRow);
        }
        catch (NullPointerException | IndexOutOfBoundsException ex3) {
            final RuntimeException ex2;
            final RuntimeException ex = ex2;
            return viewRow;
        }
    }
    
    public int getRowSpan(final SpreadsheetCell cell, int index) {
        int rowSpan = 0;
        do {
            ++rowSpan;
        } while (++index < this.sortedList.size() && cell.getColumn() < this.getGrid().getColumnCount() && ((ObservableList)this.sortedList.get(index)).get(cell.getColumn()) == cell);
        return rowSpan;
    }
    
    public int getRowSpanFilter(final SpreadsheetCell cell) {
        int rowSpan = cell.getRowSpan();
        for (int i = cell.getRow(); i < cell.getRow() + cell.getRowSpan(); ++i) {
            rowSpan -= (this.getHiddenRows().get(i) ? 1 : 0);
        }
        return rowSpan;
    }
    
    public ObservableList<ObservableList<SpreadsheetCell>> getItems() {
        return (ObservableList<ObservableList<SpreadsheetCell>>)this.cellsView.getItems();
    }
    
    public int getColumnSpan(final SpreadsheetCell cell) {
        int colSpan = cell.getColumnSpan();
        for (int i = cell.getColumn(); i < cell.getColumn() + cell.getColumnSpan(); ++i) {
            colSpan -= (this.getHiddenColumns().get(i) ? 1 : 0);
        }
        return colSpan;
    }
    
    public final Double getZoomFactor() {
        return this.zoomFactor.get();
    }
    
    public final void setZoomFactor(final Double zoomFactor) {
        this.zoomFactor.set((double)zoomFactor);
    }
    
    public final DoubleProperty zoomFactorProperty() {
        return this.zoomFactor;
    }
    
    public void incrementZoom() {
        double newZoom = this.getZoomFactor();
        final int prevValue = (int)((newZoom - 0.1) / 0.1);
        newZoom = (prevValue + 1) * 0.1 + 0.1;
        this.setZoomFactor((newZoom > 2.0) ? 2.0 : newZoom);
    }
    
    public void decrementZoom() {
        double newZoom = this.getZoomFactor() - 0.01;
        final int prevValue = (int)((newZoom - 0.1) / 0.1);
        newZoom = prevValue * 0.1 + 0.1;
        this.setZoomFactor((newZoom < 0.1) ? 0.1 : newZoom);
    }
    
    public void edit(final int row, final SpreadsheetColumn column) {
        this.cellsView.edit(row, (TableColumn)column.column);
    }
    
    public Comparator getComparator() {
        return (this.sortedList == null) ? null : this.sortedList.getComparator();
    }
    
    public ObjectProperty<Comparator<? super ObservableList<SpreadsheetCell>>> comparatorProperty() {
        return (ObjectProperty<Comparator<? super ObservableList<SpreadsheetCell>>>)this.sortedList.comparatorProperty();
    }
    
    public void setComparator(final Comparator<ObservableList<SpreadsheetCell>> comparator) {
        this.sortedList.setComparator((Comparator)comparator);
        this.computeRowMap();
        this.requestLayout();
    }
    
    public final void setGrid(final Grid grid) {
        if (grid == null) {
            return;
        }
        this.filteredList = (FilteredList<ObservableList<SpreadsheetCell>>)new FilteredList((ObservableList)grid.getRows());
        this.sortedList = (SortedList<ObservableList<SpreadsheetCell>>)new SortedList((ObservableList)this.filteredList);
        this.gridProperty.set((Object)grid);
        this.setHiddenRows(new BitSet(this.filteredList.getSource().size()));
        this.setHiddenColumns(new BitSet(grid.getColumnCount()));
        this.initRowFix(grid);
        final List<Integer> newFixedRows = new ArrayList<Integer>();
        for (final Integer rowFixed : this.getFixedRows()) {
            if (this.isRowFixable(rowFixed)) {
                newFixedRows.add(rowFixed);
            }
        }
        this.getFixedRows().setAll((Collection)newFixedRows);
        final List<Integer> columnsFixed = new ArrayList<Integer>();
        for (final SpreadsheetColumn column : this.getFixedColumns()) {
            columnsFixed.add(this.getColumns().indexOf((Object)column));
        }
        this.getFixedColumns().clear();
        final List<Double> widthColumns = new ArrayList<Double>();
        for (final SpreadsheetColumn column2 : this.columns) {
            widthColumns.add(column2.getWidth());
        }
        Pair<Integer, Integer> focusedPair = null;
        final TablePosition focusedCell = this.cellsView.getFocusModel().getFocusedCell();
        if (focusedCell != null && focusedCell.getRow() != -1 && focusedCell.getColumn() != -1) {
            focusedPair = (Pair<Integer, Integer>)new Pair((Object)focusedCell.getRow(), (Object)focusedCell.getColumn());
        }
        final Pair<Integer, Integer> finalPair = focusedPair;
        if (grid.getRows() != null) {
            this.cellsView.setItems((ObservableList)this.sortedList);
            this.computeRowMap();
            final int columnCount = grid.getColumnCount();
            this.columns.clear();
            for (int columnIndex = 0; columnIndex < columnCount; ++columnIndex) {
                final SpreadsheetColumn spreadsheetColumn = new SpreadsheetColumn(this.getTableColumn(grid, columnIndex), this, columnIndex, grid);
                if (widthColumns.size() > columnIndex) {
                    spreadsheetColumn.setPrefWidth(widthColumns.get(columnIndex));
                }
                this.columns.add((Object)spreadsheetColumn);
                if (columnsFixed.contains(columnIndex) && spreadsheetColumn.isColumnFixable()) {
                    spreadsheetColumn.setFixed(true);
                }
            }
        }
        final List<Pair<Integer, Integer>> selectedCells = new ArrayList<Pair<Integer, Integer>>();
        for (final TablePosition position : this.getSelectionModel().getSelectedCells()) {
            selectedCells.add((Pair<Integer, Integer>)new Pair((Object)position.getRow(), (Object)position.getColumn()));
        }
        int i;
        final List<Pair<Integer, Integer>> selectedCells2;
        final Pair pair;
        final Runnable runnable = () -> {
            if (this.cellsView.getColumns().size() > grid.getColumnCount()) {
                this.cellsView.getColumns().remove(grid.getColumnCount(), this.cellsView.getColumns().size());
            }
            else if (this.cellsView.getColumns().size() < grid.getColumnCount()) {
                for (i = this.cellsView.getColumns().size(); i < grid.getColumnCount(); ++i) {
                    this.cellsView.getColumns().add((Object)((SpreadsheetColumn)this.columns.get(i)).column);
                }
            }
            ((TableViewSpanSelectionModel)this.cellsView.getSelectionModel()).verifySelectedCells(selectedCells2);
            if (pair != null && (int)pair.getKey() < this.getGrid().getRowCount() && (int)pair.getValue() < this.getGrid().getColumnCount()) {
                this.cellsView.getFocusModel().focus((int)pair.getKey(), (TableColumn)this.cellsView.getColumns().get((int)pair.getValue()));
            }
            return;
        };
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        }
        else {
            try {
                final FutureTask future = new FutureTask(runnable, null);
                Platform.runLater((Runnable)future);
                future.get();
            }
            catch (InterruptedException | ExecutionException ex3) {
                final Exception ex2;
                final Exception ex = ex2;
                Logger.getLogger(SpreadsheetView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public TablePosition<ObservableList<SpreadsheetCell>, ?> getEditingCell() {
        return (TablePosition<ObservableList<SpreadsheetCell>, ?>)this.cellsView.getEditingCell();
    }
    
    public ReadOnlyObjectProperty<TablePosition<ObservableList<SpreadsheetCell>, ?>> editingCellProperty() {
        return (ReadOnlyObjectProperty<TablePosition<ObservableList<SpreadsheetCell>, ?>>)this.cellsView.editingCellProperty();
    }
    
    public final ObservableList<SpreadsheetColumn> getColumns() {
        return this.columns;
    }
    
    public final Grid getGrid() {
        return (Grid)this.gridProperty.get();
    }
    
    public final ReadOnlyObjectProperty<Grid> gridProperty() {
        return (ReadOnlyObjectProperty<Grid>)this.gridProperty;
    }
    
    public ObservableList<Integer> getFixedRows() {
        return this.fixedRows;
    }
    
    public boolean isRowFixable(final int row) {
        return row >= 0 && row < this.rowFix.size() && this.isFixingRowsAllowed() && this.rowFix.get(row);
    }
    
    public boolean areRowsFixable(final List<? extends Integer> list) {
        if (list == null || list.isEmpty() || !this.isFixingRowsAllowed()) {
            return false;
        }
        final Grid grid = this.getGrid();
        final int rowCount = grid.getRowCount();
        final ObservableList<ObservableList<SpreadsheetCell>> rows = grid.getRows();
        for (final Integer row : list) {
            if (row == null || row < 0 || row >= rowCount) {
                return false;
            }
            if (this.isRowFixable(row)) {
                continue;
            }
            int maxSpan = 1;
            final List<SpreadsheetCell> gridRow = (List<SpreadsheetCell>)rows.get((int)row);
            for (final SpreadsheetCell cell : gridRow) {
                if (!list.contains(cell.getRow())) {
                    return false;
                }
                if (this.getRowSpan(cell, row) <= maxSpan || cell.getRow() != row) {
                    continue;
                }
                maxSpan = cell.getRowSpan();
            }
            for (int count = row + maxSpan - 1, index = row + 1; index <= count; ++index) {
                if (!list.contains(index)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isFixingRowsAllowed() {
        return this.fixingRowsAllowedProperty.get();
    }
    
    public void setFixingRowsAllowed(final boolean b) {
        this.fixingRowsAllowedProperty.set(b);
    }
    
    public ReadOnlyBooleanProperty fixingRowsAllowedProperty() {
        return (ReadOnlyBooleanProperty)this.fixingRowsAllowedProperty;
    }
    
    public ObservableList<SpreadsheetColumn> getFixedColumns() {
        return this.fixedColumns;
    }
    
    public boolean isColumnFixable(final int columnIndex) {
        return columnIndex >= 0 && columnIndex < this.getColumns().size() && this.isFixingColumnsAllowed() && ((SpreadsheetColumn)this.getColumns().get(columnIndex)).isColumnFixable();
    }
    
    public boolean areSpreadsheetColumnsFixable(final List<? extends SpreadsheetColumn> list) {
        final List<Integer> newList = new ArrayList<Integer>();
        for (final SpreadsheetColumn column : list) {
            if (column != null) {
                newList.add(this.columns.indexOf((Object)column));
            }
        }
        return this.areColumnsFixable(newList);
    }
    
    public boolean areColumnsFixable(final List<? extends Integer> list) {
        if (list == null || list.isEmpty() || !this.isFixingRowsAllowed()) {
            return false;
        }
        final Grid grid = this.getGrid();
        final int columnCount = grid.getColumnCount();
        final ObservableList<ObservableList<SpreadsheetCell>> rows = grid.getRows();
        for (final Integer columnIndex : list) {
            if (columnIndex == null || columnIndex < 0 || columnIndex >= columnCount) {
                return false;
            }
            if (this.isColumnFixable(columnIndex)) {
                continue;
            }
            int maxSpan = 1;
            for (final List<SpreadsheetCell> row : rows) {
                final SpreadsheetCell cell = row.get(columnIndex);
                if (!list.contains(cell.getColumn())) {
                    return false;
                }
                if (cell.getColumnSpan() <= maxSpan || cell.getColumn() != columnIndex) {
                    continue;
                }
                maxSpan = cell.getColumnSpan();
            }
            for (int count = columnIndex + maxSpan - 1, index = columnIndex + 1; index <= count; ++index) {
                if (!list.contains(index)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isFixingColumnsAllowed() {
        return this.fixingColumnsAllowedProperty.get();
    }
    
    public void setFixingColumnsAllowed(final boolean b) {
        this.fixingColumnsAllowedProperty.set(b);
    }
    
    public ReadOnlyBooleanProperty fixingColumnsAllowedProperty() {
        return (ReadOnlyBooleanProperty)this.fixingColumnsAllowedProperty;
    }
    
    public final void setShowColumnHeader(final boolean b) {
        this.showColumnHeader.setValue(b);
    }
    
    public final boolean isShowColumnHeader() {
        return this.showColumnHeader.get();
    }
    
    public final BooleanProperty showColumnHeaderProperty() {
        return this.showColumnHeader;
    }
    
    public final void setShowRowHeader(final boolean b) {
        this.showRowHeader.setValue(b);
    }
    
    public final boolean isShowRowHeader() {
        return this.showRowHeader.get();
    }
    
    public final BooleanProperty showRowHeaderProperty() {
        return this.showRowHeader;
    }
    
    public final DoubleProperty rowHeaderWidthProperty() {
        return this.rowHeaderWidth;
    }
    
    public final void setRowHeaderWidth(final double value) {
        this.rowHeaderWidth.setValue((Number)value);
    }
    
    public final double getRowHeaderWidth() {
        return this.rowHeaderWidth.get();
    }
    
    public ObservableMap<Integer, Picker> getRowPickers() {
        return this.rowPickers;
    }
    
    public ObservableMap<Integer, Picker> getColumnPickers() {
        return this.columnPickers;
    }
    
    public void resizeRowsToFitContent() {
        if (this.getCellsViewSkin() != null) {
            this.getCellsViewSkin().resizeRowsToFitContent();
        }
    }
    
    public void resizeRowsToMaximum() {
        if (this.getCellsViewSkin() != null) {
            this.getCellsViewSkin().resizeRowsToMaximum();
        }
    }
    
    public void resizeRowsToDefault() {
        if (this.getCellsViewSkin() != null) {
            this.getCellsViewSkin().resizeRowsToDefault();
        }
    }
    
    public double getRowHeight(final int row) {
        if (this.getCellsViewSkin() == null) {
            return this.getGrid().getRowHeight(row);
        }
        return this.getCellsViewSkin().getRowHeight(row);
    }
    
    public SpreadsheetViewSelectionModel getSelectionModel() {
        return this.selectionModel;
    }
    
    public void scrollToRow(final int modelRow) {
        this.cellsView.scrollTo(this.getFilteredRow(modelRow));
    }
    
    public void setVBarValue(final double value) {
        if (this.getCellsViewSkin() == null) {
            Platform.runLater(() -> this.setVBarValue(value));
            return;
        }
        this.getCellsViewSkin().getVBar().setValue(value);
    }
    
    public void setHBarValue(final double value) {
        this.setHBarValue(value, 0);
    }
    
    private void setHBarValue(final double value, int attempt) {
        if (attempt > 10) {
            return;
        }
        if (this.getCellsViewSkin() == null) {
            final int newAttempt = ++attempt;
            Platform.runLater(() -> this.setHBarValue(value, newAttempt));
            return;
        }
        this.getCellsViewSkin().setHbarValue(value);
    }
    
    public double getVBarValue() {
        if (this.getCellsViewSkin() != null && this.getCellsViewSkin().getVBar() != null) {
            return this.getCellsViewSkin().getVBar().getValue();
        }
        return 0.0;
    }
    
    public double getHBarValue() {
        if (this.getCellsViewSkin() != null && this.getCellsViewSkin().getHBar() != null) {
            return this.getCellsViewSkin().getHBar().getValue();
        }
        return 0.0;
    }
    
    public void scrollToColumn(final SpreadsheetColumn column) {
        this.cellsView.scrollToColumn((TableColumn)column.column);
    }
    
    public void scrollToColumnIndex(final int modelColumn) {
        this.cellsView.scrollToColumnIndex(modelColumn);
    }
    
    public final Optional<SpreadsheetCellEditor> getEditor(final SpreadsheetCellType<?> cellType) {
        if (cellType == null) {
            return Optional.empty();
        }
        SpreadsheetCellEditor cellEditor = this.editors.get(cellType);
        if (cellEditor == null) {
            cellEditor = cellType.createEditor(this);
            if (cellEditor == null) {
                return Optional.empty();
            }
            this.editors.put(cellType, cellEditor);
        }
        return Optional.of(cellEditor);
    }
    
    public final void setEditable(final boolean b) {
        this.cellsView.setEditable(b);
    }
    
    public final boolean isEditable() {
        return this.cellsView.isEditable();
    }
    
    public final BooleanProperty editableProperty() {
        return this.cellsView.editableProperty();
    }
    
    public final ObjectProperty<Node> placeholderProperty() {
        return (ObjectProperty<Node>)this.cellsView.placeholderProperty();
    }
    
    public final void setPlaceholder(final Node placeholder) {
        this.cellsView.setPlaceholder(placeholder);
    }
    
    public final Node getPlaceholder() {
        return this.cellsView.getPlaceholder();
    }
    
    public void copyClipboard() {
        this.checkFormat();
        final ArrayList<GridChange> list = new ArrayList<GridChange>();
        final ObservableList<TablePosition> posList = this.getSelectionModel().getSelectedCells();
        for (final TablePosition<?, ?> p : posList) {
            final SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)this.getGrid().getRows().get(this.getModelRow(p.getRow()))).get(this.getModelColumn(p.getColumn()));
            for (int row = 0; row < this.getRowSpan(cell, p.getRow()); ++row) {
                for (int col = 0; col < this.getColumnSpan(cell); ++col) {
                    try {
                        new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(cell.getItem());
                        list.add(new GridChange(p.getRow() + row, p.getColumn() + col, null, (cell.getItem() == null) ? null : cell.getItem()));
                    }
                    catch (IOException exception) {
                        list.add(new GridChange(p.getRow() + row, p.getColumn() + col, null, (cell.getItem() == null) ? null : cell.getItem().toString()));
                    }
                }
            }
        }
        final ClipboardContent content = new ClipboardContent();
        content.put((Object)this.fmt, (Object)list);
        Clipboard.getSystemClipboard().setContent((Map)content);
    }
    
    private void pasteOneValue(final GridChange change) {
        for (final TablePosition position : this.getSelectionModel().getSelectedCells()) {
            this.tryPasteCell(this.getModelRow(position.getRow()), this.getModelColumn(position.getColumn()), change.getNewValue());
        }
    }
    
    private void tryPasteCell(final int row, final int column, final Object value) {
        final SpanType type = this.getSpanType(row, column);
        if (type == SpanType.NORMAL_CELL || type == SpanType.ROW_VISIBLE) {
            final SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)this.getGrid().getRows().get(row)).get(column);
            final boolean succeed = cell.getCellType().match(value);
            if (succeed) {
                this.getGrid().setCellValue(cell.getRow(), cell.getColumn(), cell.getCellType().convertValue(value));
            }
        }
    }
    
    private void pasteMixedValues(final ArrayList<GridChange> list) {
        final RectangleSelection.SelectionRange sourceSelectionRange = new RectangleSelection.SelectionRange();
        sourceSelectionRange.fillGridRange(list);
        if (sourceSelectionRange.getRange() != null) {
            final RectangleSelection.SelectionRange targetSelectionRange = new RectangleSelection.SelectionRange();
            targetSelectionRange.fill((List<TablePosition>)this.cellsView.getSelectionModel().getSelectedCells());
            if (targetSelectionRange.getRange() != null) {
                final RectangleSelection.GridRange sourceRange = sourceSelectionRange.getRange();
                final RectangleSelection.GridRange targetRange = targetSelectionRange.getRange();
                final int sourceRowGap = sourceRange.getBottom() - sourceRange.getTop() + 1;
                final int targetRowGap = targetRange.getBottom() - targetRange.getTop() + 1;
                final int sourceColumnGap = sourceRange.getRight() - sourceRange.getLeft() + 1;
                final int targetColumnGap = targetRange.getRight() - targetRange.getLeft() + 1;
                final int offsetRow = targetRange.getTop() - sourceRange.getTop();
                final int offsetCol = targetRange.getLeft() - sourceRange.getLeft();
                if ((sourceRowGap == targetRowGap || targetRowGap == 1) && targetColumnGap % sourceColumnGap == 0) {
                    for (final GridChange change : list) {
                        final int row = this.getModelRow(change.getRow() + offsetRow);
                        int column = change.getColumn() + offsetCol;
                        do {
                            final int modelColumn = this.getModelColumn(column);
                            if (row < this.getGrid().getRowCount() && modelColumn < this.getGrid().getColumnCount() && row >= 0 && column >= 0) {
                                this.tryPasteCell(row, modelColumn, change.getNewValue());
                            }
                        } while ((column += sourceColumnGap) <= targetRange.getRight());
                    }
                }
                else if ((sourceColumnGap == targetColumnGap || targetColumnGap == 1) && targetRowGap % sourceRowGap == 0) {
                    for (final GridChange change : list) {
                        int row = change.getRow() + offsetRow;
                        final int column = this.getModelColumn(change.getColumn() + offsetCol);
                        do {
                            final int modelRow = this.getModelRow(row);
                            if (modelRow < this.getGrid().getRowCount() && column < this.getGrid().getColumnCount() && row >= 0 && column >= 0) {
                                this.tryPasteCell(modelRow, column, change.getNewValue());
                            }
                        } while ((row += sourceRowGap) <= targetRange.getBottom());
                    }
                }
            }
        }
    }
    
    private void pasteSeveralValues(final ArrayList<GridChange> list) {
        int minRow = this.getGrid().getRowCount();
        int minCol = this.getGrid().getColumnCount();
        int maxRow = 0;
        int maxCol = 0;
        for (final GridChange p : list) {
            final int tempcol = p.getColumn();
            final int temprow = p.getRow();
            if (tempcol < minCol) {
                minCol = tempcol;
            }
            if (tempcol > maxCol) {
                maxCol = tempcol;
            }
            if (temprow < minRow) {
                minRow = temprow;
            }
            if (temprow > maxRow) {
                maxRow = temprow;
            }
        }
        final TablePosition<?, ?> p2 = (TablePosition<?, ?>)this.cellsView.getFocusModel().getFocusedCell();
        final int offsetRow = p2.getRow() - minRow;
        final int offsetCol = p2.getColumn() - minCol;
        final int rowCount = this.getGrid().getRowCount();
        final int columnCount = this.getGrid().getColumnCount();
        for (final GridChange change : list) {
            final int row = this.getModelRow(change.getRow() + offsetRow);
            final int column = this.getModelColumn(change.getColumn() + offsetCol);
            if (row < rowCount && column < columnCount && row >= 0 && column >= 0) {
                this.tryPasteCell(row, column, change.getNewValue());
            }
        }
    }
    
    public void pasteClipboard() {
        final List<TablePosition> selectedCells = (List<TablePosition>)this.cellsView.getSelectionModel().getSelectedCells();
        if (!this.isEditable() || selectedCells.isEmpty()) {
            return;
        }
        this.checkFormat();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.getContent(this.fmt) != null) {
            final ArrayList<GridChange> list = (ArrayList<GridChange>)clipboard.getContent(this.fmt);
            if (list.size() == 1) {
                this.pasteOneValue(list.get(0));
            }
            else if (selectedCells.size() > 1) {
                this.pasteMixedValues(list);
            }
            else {
                this.pasteSeveralValues(list);
            }
        }
        else if (clipboard.hasString()) {}
    }
    
    public ContextMenu getSpreadsheetViewContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem copyItem = new MenuItem(Localization.localize(Localization.asKey("spreadsheet.view.menu.copy")));
        copyItem.setGraphic((Node)new ImageView(new Image(SpreadsheetView.class.getResourceAsStream("copySpreadsheetView.png"))));
        copyItem.setAccelerator((KeyCombination)new KeyCodeCombination(KeyCode.C, new KeyCombination.Modifier[] { KeyCombination.SHORTCUT_DOWN }));
        copyItem.setOnAction((EventHandler)new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent e) {
                SpreadsheetView.this.copyClipboard();
            }
        });
        final MenuItem pasteItem = new MenuItem(Localization.localize(Localization.asKey("spreadsheet.view.menu.paste")));
        pasteItem.setGraphic((Node)new ImageView(new Image(SpreadsheetView.class.getResourceAsStream("pasteSpreadsheetView.png"))));
        pasteItem.setAccelerator((KeyCombination)new KeyCodeCombination(KeyCode.V, new KeyCombination.Modifier[] { KeyCombination.SHORTCUT_DOWN }));
        pasteItem.setOnAction((EventHandler)new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent e) {
                SpreadsheetView.this.pasteClipboard();
            }
        });
        final Menu cornerMenu = new Menu(Localization.localize(Localization.asKey("spreadsheet.view.menu.comment")));
        cornerMenu.setGraphic((Node)new ImageView(new Image(SpreadsheetView.class.getResourceAsStream("comment.png"))));
        final MenuItem topLeftItem = new MenuItem(Localization.localize(Localization.asKey("spreadsheet.view.menu.comment.top-left")));
        topLeftItem.setOnAction((EventHandler)new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent t) {
                final TablePosition<ObservableList<SpreadsheetCell>, ?> pos = (TablePosition<ObservableList<SpreadsheetCell>, ?>)SpreadsheetView.this.cellsView.getFocusModel().getFocusedCell();
                final SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)SpreadsheetView.this.getGrid().getRows().get(SpreadsheetView.this.getModelRow(pos.getRow()))).get(SpreadsheetView.this.getModelColumn(pos.getColumn()));
                cell.activateCorner(SpreadsheetCell.CornerPosition.TOP_LEFT);
            }
        });
        final MenuItem topRightItem = new MenuItem(Localization.localize(Localization.asKey("spreadsheet.view.menu.comment.top-right")));
        topRightItem.setOnAction((EventHandler)new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent t) {
                final TablePosition<ObservableList<SpreadsheetCell>, ?> pos = (TablePosition<ObservableList<SpreadsheetCell>, ?>)SpreadsheetView.this.cellsView.getFocusModel().getFocusedCell();
                final SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)SpreadsheetView.this.getGrid().getRows().get(SpreadsheetView.this.getModelRow(pos.getRow()))).get(SpreadsheetView.this.getModelColumn(pos.getColumn()));
                cell.activateCorner(SpreadsheetCell.CornerPosition.TOP_RIGHT);
            }
        });
        final MenuItem bottomRightItem = new MenuItem(Localization.localize(Localization.asKey("spreadsheet.view.menu.comment.bottom-right")));
        bottomRightItem.setOnAction((EventHandler)new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent t) {
                final TablePosition<ObservableList<SpreadsheetCell>, ?> pos = (TablePosition<ObservableList<SpreadsheetCell>, ?>)SpreadsheetView.this.cellsView.getFocusModel().getFocusedCell();
                final SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)SpreadsheetView.this.getGrid().getRows().get(SpreadsheetView.this.getModelRow(pos.getRow()))).get(SpreadsheetView.this.getModelColumn(pos.getColumn()));
                cell.activateCorner(SpreadsheetCell.CornerPosition.BOTTOM_RIGHT);
            }
        });
        final MenuItem bottomLeftItem = new MenuItem(Localization.localize(Localization.asKey("spreadsheet.view.menu.comment.bottom-left")));
        bottomLeftItem.setOnAction((EventHandler)new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent t) {
                final TablePosition<ObservableList<SpreadsheetCell>, ?> pos = (TablePosition<ObservableList<SpreadsheetCell>, ?>)SpreadsheetView.this.cellsView.getFocusModel().getFocusedCell();
                final SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)SpreadsheetView.this.getGrid().getRows().get(SpreadsheetView.this.getModelRow(pos.getRow()))).get(SpreadsheetView.this.getModelColumn(pos.getColumn()));
                cell.activateCorner(SpreadsheetCell.CornerPosition.BOTTOM_LEFT);
            }
        });
        cornerMenu.getItems().addAll((Object[])new MenuItem[] { topLeftItem, topRightItem, bottomRightItem, bottomLeftItem });
        contextMenu.getItems().addAll((Object[])new MenuItem[] { copyItem, pasteItem, cornerMenu });
        return contextMenu;
    }
    
    public void deleteSelectedCells() {
        for (final TablePosition<ObservableList<SpreadsheetCell>, ?> position : this.getSelectionModel().getSelectedCells()) {
            this.getGrid().setCellValue(this.getModelRow(position.getRow()), this.getModelColumn(position.getColumn()), null);
        }
    }
    
    public SpanType getSpanType(final int rowIndex, final int modelColumn) {
        if (this.getGrid() == null) {
            return SpanType.NORMAL_CELL;
        }
        if (rowIndex < 0 || modelColumn < 0 || rowIndex >= this.getItems().size() || modelColumn >= this.getGrid().getColumnCount()) {
            return SpanType.NORMAL_CELL;
        }
        final SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)this.getCellsView().getItems().get(rowIndex)).get(modelColumn);
        final int cellColumn = this.getHiddenColumns().nextClearBit(cell.getColumn());
        final int cellRowSpan = this.getRowSpanFilter(cell);
        if (cellColumn == modelColumn && cellRowSpan == 1) {
            return SpanType.NORMAL_CELL;
        }
        final int cellColumnSpan = this.getColumnSpan(cell);
        final GridViewSkin skin = this.getCellsViewSkin();
        final boolean containsRowMinusOne = skin == null || skin.containsRow(rowIndex - 1);
        final boolean containsSameCellMinusOne = rowIndex > 0 && ((ObservableList)this.getCellsView().getItems().get(rowIndex - 1)).get(modelColumn) == cell;
        if (containsRowMinusOne && cellColumnSpan > 1 && cellColumn != modelColumn && cellRowSpan > 1 && containsSameCellMinusOne) {
            return SpanType.BOTH_INVISIBLE;
        }
        if (cellRowSpan > 1 && cellColumn == modelColumn) {
            if (!containsSameCellMinusOne || !containsRowMinusOne) {
                return SpanType.ROW_VISIBLE;
            }
            return SpanType.ROW_SPAN_INVISIBLE;
        }
        else {
            if (cellColumnSpan <= 1 || (containsSameCellMinusOne && containsRowMinusOne)) {
                return SpanType.NORMAL_CELL;
            }
            if (cellColumn == modelColumn) {
                return SpanType.NORMAL_CELL;
            }
            return SpanType.COLUMN_SPAN_INVISIBLE;
        }
    }
    
    private TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell> getTableColumn(final Grid grid, final int columnIndex) {
        final String columnHeader = (String)((grid.getColumnHeaders().size() > columnIndex) ? grid.getColumnHeaders().get(columnIndex) : Utils.getExcelLetterFromNumber(columnIndex));
        TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell> column;
        if (columnIndex < this.cellsView.getColumns().size()) {
            column = (TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell>)this.cellsView.getColumns().get(columnIndex);
            column.setText(columnHeader);
        }
        else {
            column = (TableColumn<ObservableList<SpreadsheetCell>, SpreadsheetCell>)new TableColumn(columnHeader);
            column.setEditable(true);
            column.setSortable(false);
            column.impl_setReorderable(false);
            column.setCellValueFactory(p -> {
                if (columnIndex >= ((ObservableList)p.getValue()).size()) {
                    return null;
                }
                return new ReadOnlyObjectWrapper(((ObservableList)p.getValue()).get(columnIndex));
            });
            column.setCellFactory(p -> new CellView(this.handle));
        }
        return column;
    }
    
    private static Grid getSampleGrid() {
        final GridBase gridBase = new GridBase(100, 15);
        final ObservableList<ObservableList<SpreadsheetCell>> rows = (ObservableList<ObservableList<SpreadsheetCell>>)FXCollections.observableArrayList();
        for (int row = 0; row < gridBase.getRowCount(); ++row) {
            final ObservableList<SpreadsheetCell> currentRow = (ObservableList<SpreadsheetCell>)FXCollections.observableArrayList();
            for (int column = 0; column < gridBase.getColumnCount(); ++column) {
                currentRow.add((Object)SpreadsheetCellType.STRING.createCell(row, column, 1, 1, "toto"));
            }
            rows.add((Object)currentRow);
        }
        gridBase.setRows((Collection<ObservableList<SpreadsheetCell>>)rows);
        return gridBase;
    }
    
    private void initRowFix(final Grid grid) {
        final ObservableList<ObservableList<SpreadsheetCell>> rows = grid.getRows();
        this.rowFix = new BitSet(rows.size());
        int r = 0;
    Label_0026:
        while (r < rows.size()) {
            final ObservableList<SpreadsheetCell> row = (ObservableList<SpreadsheetCell>)rows.get(r);
            while (true) {
                for (final SpreadsheetCell cell : row) {
                    if (this.getRowSpanFilter(cell) > 1) {
                        ++r;
                        continue Label_0026;
                    }
                }
                this.rowFix.set(r);
                continue;
            }
        }
    }
    
    private void verifyGrid(final Grid grid) {
        this.verifyColumnSpan(grid);
    }
    
    private void verifyColumnSpan(final Grid grid) {
        for (int i = 0; i < grid.getRows().size(); ++i) {
            final ObservableList<SpreadsheetCell> row = (ObservableList<SpreadsheetCell>)grid.getRows().get(i);
            int count = 0;
            for (int j = 0; j < row.size(); ++j) {
                if (((SpreadsheetCell)row.get(j)).getColumnSpan() == 1) {
                    ++count;
                }
                else {
                    if (((SpreadsheetCell)row.get(j)).getColumnSpan() <= 1) {
                        throw new IllegalStateException("\n At row " + i + " and column " + j + ": this cell has a negative columnSpan");
                    }
                    ++count;
                    final SpreadsheetCell currentCell = (SpreadsheetCell)row.get(j);
                    for (int k = j + 1; k < currentCell.getColumn() + currentCell.getColumnSpan(); ++k) {
                        if (!((SpreadsheetCell)row.get(k)).equals(currentCell)) {
                            throw new IllegalStateException("\n At row " + i + " and column " + j + ": this cell is in the range of a columnSpan but is different. \nEvery cell in a range of a ColumnSpan must be of the same instance.");
                        }
                        ++count;
                        ++j;
                    }
                }
            }
            if (count != grid.getColumnCount()) {
                throw new IllegalStateException("The row" + i + " has a number of cells different of the columnCount declared in the grid.");
            }
        }
    }
    
    private void checkFormat() {
        final DataFormat lookupMimeType = DataFormat.lookupMimeType("SpreadsheetView");
        this.fmt = lookupMimeType;
        if (lookupMimeType == null) {
            this.fmt = new DataFormat(new String[] { "SpreadsheetView" });
        }
    }
    
    private String computeReason(final List<? extends Integer> list) {
        String reason = "\n A row cannot be fixed. \n";
        for (final Integer row : list) {
            if (!this.isRowFixable(row)) {
                int maxSpan = 1;
                final List<SpreadsheetCell> gridRow = (List<SpreadsheetCell>)this.getGrid().getRows().get((int)row);
                for (final SpreadsheetCell cell : gridRow) {
                    if (!list.contains(cell.getRow())) {
                        reason = reason + "The row " + row + " is inside a row span and the starting row " + cell.getRow() + " is not fixed.\n";
                    }
                    if (cell.getRowSpan() > maxSpan && cell.getRow() == row) {
                        maxSpan = cell.getRowSpan();
                    }
                }
                for (int count = row + maxSpan - 1, index = row + 1; index < count; ++index) {
                    if (!list.contains(index)) {
                        reason = reason + "One cell on the row " + row + " has a row span of " + maxSpan + ". But the row " + index + " contained within that span is not fixed.\n";
                    }
                }
            }
        }
        return reason;
    }
    
    private boolean isEditionKey(final KeyEvent keyEvent) {
        return !keyEvent.isShortcutDown() && !keyEvent.getCode().isNavigationKey() && !keyEvent.getCode().isFunctionKey() && !keyEvent.getCode().isModifierKey() && !keyEvent.getCode().isMediaKey() && keyEvent.getCode() != KeyCode.ESCAPE;
    }
    
    public enum SpanType
    {
        NORMAL_CELL, 
        COLUMN_SPAN_INVISIBLE, 
        ROW_SPAN_INVISIBLE, 
        ROW_VISIBLE, 
        BOTH_INVISIBLE;
    }
    
    public static class RowHeightEvent extends Event
    {
        public static final EventType<RowHeightEvent> ROW_HEIGHT_CHANGE;
        private final int modelRow;
        private final double height;
        
        public RowHeightEvent(final int row, final double height) {
            super((EventType)RowHeightEvent.ROW_HEIGHT_CHANGE);
            this.modelRow = row;
            this.height = height;
        }
        
        public int getRow() {
            return this.modelRow;
        }
        
        public double getHeight() {
            return this.height;
        }
        
        static {
            ROW_HEIGHT_CHANGE = new EventType(Event.ANY, "RowHeightChange");
        }
    }
    
    public static class ColumnWidthEvent extends Event
    {
        public static final EventType<ColumnWidthEvent> COLUMN_WIDTH_CHANGE;
        private final int column;
        private final double width;
        
        public ColumnWidthEvent(final int column, final double width) {
            super((EventType)ColumnWidthEvent.COLUMN_WIDTH_CHANGE);
            this.column = column;
            this.width = width;
        }
        
        public int getColumn() {
            return this.column;
        }
        
        public double getWidth() {
            return this.width;
        }
        
        static {
            COLUMN_WIDTH_CHANGE = new EventType(Event.ANY, "ColumnWidthChange");
        }
    }
}
