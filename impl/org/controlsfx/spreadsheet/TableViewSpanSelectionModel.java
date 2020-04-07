// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.spreadsheet;

import javafx.scene.control.TableColumnBase;
import com.sun.javafx.collections.NonIterableChange;
import javafx.beans.Observable;
import javafx.beans.InvalidationListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.scene.control.SelectionMode;
import com.sun.javafx.collections.MappingChange;
import javafx.collections.WeakListChangeListener;
import javafx.animation.KeyValue;
import javafx.event.WeakEventHandler;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.event.Event;
import javafx.beans.NamedArg;
import javafx.collections.ListChangeListener;
import javafx.scene.input.KeyEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.animation.Timeline;
import javafx.util.Pair;
import javafx.scene.control.TableColumn;
import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import javafx.scene.control.TablePosition;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import javafx.scene.input.MouseEvent;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class TableViewSpanSelectionModel extends TableView.TableViewSelectionModel<ObservableList<SpreadsheetCell>>
{
    private boolean shift;
    private boolean key;
    private boolean drag;
    private MouseEvent mouseEvent;
    private boolean makeAtomic;
    private SpreadsheetGridView cellsView;
    private SpreadsheetView spreadsheetView;
    private final SelectedCellsMapTemp<TablePosition<ObservableList<SpreadsheetCell>, ?>> selectedCellsMap;
    private final ReadOnlyUnbackedObservableList<TablePosition<ObservableList<SpreadsheetCell>, ?>> selectedCellsSeq;
    private TableColumn oldTableColumn;
    private int oldRow;
    public Pair<Integer, Integer> direction;
    private int oldColSpan;
    private int oldRowSpan;
    private final Timeline timer;
    private final EventHandler<ActionEvent> timerEventHandler;
    private final EventHandler<MouseEvent> dragDoneHandler;
    private final EventHandler<KeyEvent> keyPressedEventHandler;
    private final EventHandler<MouseEvent> mousePressedEventHandler;
    private final EventHandler<MouseEvent> onDragDetectedEventHandler;
    private final EventHandler<MouseEvent> onMouseDragEventHandler;
    private final ListChangeListener<TablePosition<ObservableList<SpreadsheetCell>, ?>> listChangeListener;
    private TablePosition<ObservableList<SpreadsheetCell>, ?> old;
    
    public TableViewSpanSelectionModel(@NamedArg("spreadsheetView") final SpreadsheetView spreadsheetView, @NamedArg("cellsView") final SpreadsheetGridView cellsView) {
        super((TableView)cellsView);
        this.shift = false;
        this.key = false;
        this.drag = false;
        this.oldTableColumn = null;
        this.oldRow = -1;
        this.oldColSpan = -1;
        this.oldRowSpan = -1;
        this.timerEventHandler = (EventHandler<ActionEvent>)(event -> {
            final GridViewSkin skin = this.getCellsViewSkin();
            if (this.mouseEvent != null && !this.cellsView.contains(this.mouseEvent.getX(), this.mouseEvent.getY())) {
                final double sceneX = this.mouseEvent.getSceneX();
                final double sceneY = this.mouseEvent.getSceneY();
                final double layoutX = this.cellsView.getLocalToSceneTransform().getTx();
                final double layoutY = this.cellsView.getLocalToSceneTransform().getTy();
                final double layoutXMax = layoutX + this.cellsView.getWidth();
                final double layoutYMax = layoutY + this.cellsView.getHeight();
                if (sceneX > layoutXMax) {
                    skin.getHBar().increment();
                }
                else if (sceneX < layoutX) {
                    skin.getHBar().decrement();
                }
                if (sceneY > layoutYMax) {
                    skin.getVBar().increment();
                }
                else if (sceneY < layoutY) {
                    skin.getVBar().decrement();
                }
            }
        });
        this.dragDoneHandler = (EventHandler<MouseEvent>)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent mouseEvent) {
                TableViewSpanSelectionModel.this.drag = false;
                TableViewSpanSelectionModel.this.timer.stop();
                TableViewSpanSelectionModel.this.spreadsheetView.removeEventHandler(MouseEvent.MOUSE_RELEASED, (EventHandler)this);
            }
        };
        this.keyPressedEventHandler = (EventHandler<KeyEvent>)(keyEvent -> {
            this.key = true;
            this.shift = keyEvent.isShiftDown();
        });
        this.mousePressedEventHandler = (EventHandler<MouseEvent>)(mouseEvent1 -> {
            this.key = false;
            this.shift = mouseEvent1.isShiftDown();
        });
        this.onDragDetectedEventHandler = (EventHandler<MouseEvent>)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent mouseEvent) {
                TableViewSpanSelectionModel.this.cellsView.addEventHandler(MouseEvent.MOUSE_RELEASED, TableViewSpanSelectionModel.this.dragDoneHandler);
                TableViewSpanSelectionModel.this.drag = true;
                TableViewSpanSelectionModel.this.timer.setCycleCount(-1);
                TableViewSpanSelectionModel.this.timer.play();
            }
        };
        this.onMouseDragEventHandler = (EventHandler<MouseEvent>)(e -> this.mouseEvent = e);
        this.listChangeListener = (ListChangeListener<TablePosition<ObservableList<SpreadsheetCell>, ?>>)this::handleSelectedCellsListChangeEvent;
        this.old = null;
        this.cellsView = cellsView;
        this.spreadsheetView = spreadsheetView;
        this.timer = new Timeline(new KeyFrame[] { new KeyFrame(Duration.millis(100.0), (EventHandler)new WeakEventHandler((EventHandler)this.timerEventHandler), new KeyValue[0]) });
        cellsView.addEventHandler(KeyEvent.KEY_PRESSED, (EventHandler)new WeakEventHandler((EventHandler)this.keyPressedEventHandler));
        cellsView.addEventFilter(MouseEvent.MOUSE_PRESSED, (EventHandler)new WeakEventHandler((EventHandler)this.mousePressedEventHandler));
        cellsView.setOnDragDetected((EventHandler)new WeakEventHandler((EventHandler)this.onDragDetectedEventHandler));
        cellsView.setOnMouseDragged((EventHandler)new WeakEventHandler((EventHandler)this.onMouseDragEventHandler));
        this.selectedCellsMap = new SelectedCellsMapTemp<TablePosition<ObservableList<SpreadsheetCell>, ?>>((javafx.collections.ListChangeListener<TablePosition<ObservableList<SpreadsheetCell>, ?>>)new WeakListChangeListener((ListChangeListener)this.listChangeListener));
        this.selectedCellsSeq = new ReadOnlyUnbackedObservableList<TablePosition<ObservableList<SpreadsheetCell>, ?>>() {
            public TablePosition<ObservableList<SpreadsheetCell>, ?> get(final int i) {
                return (TablePosition<ObservableList<SpreadsheetCell>, ?>)TableViewSpanSelectionModel.this.selectedCellsMap.get(i);
            }
            
            public int size() {
                return TableViewSpanSelectionModel.this.selectedCellsMap.size();
            }
        };
    }
    
    private void handleSelectedCellsListChangeEvent(final ListChangeListener.Change<? extends TablePosition<ObservableList<SpreadsheetCell>, ?>> c) {
        if (this.makeAtomic) {
            return;
        }
        this.selectedCellsSeq.callObservers((ListChangeListener.Change)new MappingChange((ListChangeListener.Change)c, MappingChange.NOOP_MAP, (ObservableList)this.selectedCellsSeq));
        c.reset();
    }
    
    public void select(final int row, final TableColumn<ObservableList<SpreadsheetCell>, ?> column) {
        if (row < 0 || row >= this.getItemCount()) {
            return;
        }
        if (this.isCellSelectionEnabled() && column == null) {
            return;
        }
        TablePosition<ObservableList<SpreadsheetCell>, ?> posFinal = (TablePosition<ObservableList<SpreadsheetCell>, ?>)new TablePosition(this.getTableView(), row, (TableColumn)column);
        final int columnIndex = this.cellsView.getColumns().indexOf((Object)posFinal.getTableColumn());
        final SpreadsheetView.SpanType spanType = this.spreadsheetView.getSpanType(row, columnIndex);
        Label_0428: {
            switch (spanType) {
                case ROW_SPAN_INVISIBLE: {
                    if (this.old != null && !this.shift && this.old.getColumn() == posFinal.getColumn() && this.old.getRow() == posFinal.getRow() - 1) {
                        final int visibleRow = FocusModelListener.getNextRowNumber(this.old, this.cellsView, this.spreadsheetView);
                        if (visibleRow < this.getItemCount()) {
                            posFinal = this.getVisibleCell(visibleRow, (TableColumn<ObservableList<SpreadsheetCell>, ?>)((this.oldColSpan > 1) ? this.oldTableColumn : this.old.getTableColumn()));
                            break Label_0428;
                        }
                    }
                    posFinal = this.getVisibleCell(row, (TableColumn<ObservableList<SpreadsheetCell>, ?>)((this.oldColSpan > 1) ? this.oldTableColumn : column));
                    break Label_0428;
                }
                case BOTH_INVISIBLE: {
                    posFinal = this.getVisibleCell(row, column);
                    break Label_0428;
                }
                case COLUMN_SPAN_INVISIBLE: {
                    if (this.old != null && !this.shift && this.old.getColumn() == posFinal.getColumn() - 1 && this.old.getRow() == posFinal.getRow()) {
                        posFinal = this.getVisibleCell((this.oldRowSpan > 1) ? this.oldRow : this.old.getRow(), FocusModelListener.getTableColumnSpan(this.old, this.cellsView, this.spreadsheetView));
                        break;
                    }
                    posFinal = this.getVisibleCell(row, column);
                    break;
                }
            }
            if (this.direction != null && this.key) {
                if ((int)this.direction.getKey() != 0 && this.oldColSpan > 1) {
                    posFinal = this.getVisibleCell(posFinal.getRow(), (TableColumn<ObservableList<SpreadsheetCell>, ?>)this.oldTableColumn);
                }
                else if ((int)this.direction.getValue() != 0 && this.oldRowSpan > 1) {
                    posFinal = this.getVisibleCell(this.oldRow, (TableColumn<ObservableList<SpreadsheetCell>, ?>)posFinal.getTableColumn());
                }
            }
        }
        this.old = posFinal;
        if (!this.key) {
            this.oldRow = this.old.getRow();
            this.oldTableColumn = this.old.getTableColumn();
        }
        else if (this.direction != null && (int)this.direction.getKey() != 0) {
            this.oldRow = this.old.getRow();
        }
        else if (this.direction != null && (int)this.direction.getValue() != 0) {
            this.oldTableColumn = this.old.getTableColumn();
        }
        if (this.getSelectionMode() == SelectionMode.SINGLE) {
            this.quietClearSelection();
        }
        final SpreadsheetCell cell = (SpreadsheetCell)this.old.getTableColumn().getCellData(this.old.getRow());
        this.oldRowSpan = this.spreadsheetView.getRowSpan(cell, this.old.getRow());
        this.oldColSpan = this.spreadsheetView.getColumnSpan(cell);
        for (int i = this.old.getRow(); i < this.oldRowSpan + this.old.getRow(); ++i) {
            for (int j = this.spreadsheetView.getViewColumn(cell.getColumn()); j < this.oldColSpan + this.spreadsheetView.getViewColumn(cell.getColumn()); ++j) {
                posFinal = (TablePosition<ObservableList<SpreadsheetCell>, ?>)new TablePosition(this.getTableView(), i, this.getTableView().getVisibleLeafColumn(j));
                this.selectedCellsMap.add(posFinal);
            }
        }
        this.updateScroll(this.old);
        this.addSelectedRowsAndColumns(this.old);
        this.setSelectedIndex(this.old.getRow());
        this.setSelectedItem(this.getModelItem(this.old.getRow()));
        if (this.getTableView().getFocusModel() == null) {
            return;
        }
        this.getTableView().getFocusModel().focus(this.old.getRow(), this.old.getTableColumn());
    }
    
    private void updateScroll(final TablePosition<ObservableList<SpreadsheetCell>, ?> posFinal) {
        if (!this.drag && this.key && this.getCellsViewSkin().getCellsSize() != 0 && !VerticalHeader.isFixedRowEmpty(this.spreadsheetView)) {
            final int start = this.getCellsViewSkin().getRow(0).getIndex();
            double posFinalOffset = 0.0;
            for (int j = start; j < posFinal.getRow(); ++j) {
                posFinalOffset += this.getSpreadsheetViewSkin().getRowHeight(j);
            }
            if (this.getCellsViewSkin().getFixedRowHeight() > posFinalOffset) {
                this.cellsView.scrollTo(posFinal.getRow());
            }
        }
    }
    
    public void clearSelection(final int row, final TableColumn<ObservableList<SpreadsheetCell>, ?> column) {
        final TablePosition<ObservableList<SpreadsheetCell>, ?> tp = (TablePosition<ObservableList<SpreadsheetCell>, ?>)new TablePosition(this.getTableView(), row, (TableColumn)column);
        if (tp.getRow() < 0 || tp.getColumn() < 0) {
            return;
        }
        final List<TablePosition<ObservableList<SpreadsheetCell>, ?>> selectedCells;
        if ((selectedCells = this.isSelectedRange(row, column, tp.getColumn())) != null) {
            for (final TablePosition<ObservableList<SpreadsheetCell>, ?> cell : selectedCells) {
                this.selectedCellsMap.remove(cell);
                this.removeSelectedRowsAndColumns(cell);
                this.focus(cell.getRow());
            }
        }
        else {
            for (final TablePosition<ObservableList<SpreadsheetCell>, ?> pos : this.getSelectedCells()) {
                if (pos.equals((Object)tp)) {
                    this.selectedCellsMap.remove(pos);
                    this.removeSelectedRowsAndColumns(pos);
                    this.focus(row);
                }
            }
        }
    }
    
    public void verifySelectedCells(final List<Pair<Integer, Integer>> selectedCells) {
        final List<TablePosition<ObservableList<SpreadsheetCell>, ?>> newList = new ArrayList<TablePosition<ObservableList<SpreadsheetCell>, ?>>();
        this.clearSelection();
        final int itemCount = this.getItemCount();
        final int columnSize = this.getTableView().getVisibleLeafColumns().size();
        final HashSet<Integer> selectedRows = new HashSet<Integer>();
        final HashSet<Integer> selectedColumns = new HashSet<Integer>();
        TablePosition<ObservableList<SpreadsheetCell>, ?> pos = null;
        for (final Pair<Integer, Integer> position : selectedCells) {
            if ((int)position.getKey() >= 0 && (int)position.getKey() < itemCount && (int)position.getValue() >= 0) {
                if ((int)position.getValue() >= columnSize) {
                    continue;
                }
                final TableColumn<ObservableList<SpreadsheetCell>, ?> column = (TableColumn<ObservableList<SpreadsheetCell>, ?>)this.getTableView().getVisibleLeafColumn((int)position.getValue());
                pos = this.getVisibleCell((int)position.getKey(), column);
                final SpreadsheetCell cell = (SpreadsheetCell)column.getCellData(pos.getRow());
                if (cell == null) {
                    continue;
                }
                for (int rowSpan = this.spreadsheetView.getRowSpan(cell, pos.getRow()), currentRow = pos.getRow(), i = pos.getRow(); i < rowSpan + currentRow; ++i) {
                    selectedColumns.add(i);
                    for (int j = this.spreadsheetView.getViewColumn(cell.getColumn()); j < this.spreadsheetView.getColumnSpan(cell) + this.spreadsheetView.getViewColumn(cell.getColumn()); ++j) {
                        selectedRows.add(j);
                        pos = (TablePosition<ObservableList<SpreadsheetCell>, ?>)new TablePosition(this.getTableView(), i, this.getTableView().getVisibleLeafColumn(j));
                        newList.add(pos);
                    }
                }
            }
        }
        this.selectedCellsMap.setAll(newList);
        final TablePosition finalPos = pos;
        final GridViewSkin skin = this.getSpreadsheetViewSkin();
        if (skin == null) {
            this.cellsView.skinProperty().addListener((InvalidationListener)new InvalidationListener() {
                public void invalidated(final Observable observable) {
                    TableViewSpanSelectionModel.this.cellsView.skinProperty().removeListener((InvalidationListener)this);
                    final GridViewSkin skin = TableViewSpanSelectionModel.this.getSpreadsheetViewSkin();
                    if (skin != null) {
                        TableViewSpanSelectionModel.this.updateSelectedVisuals(skin, finalPos, selectedRows, selectedColumns);
                    }
                }
            });
        }
        else {
            this.updateSelectedVisuals(skin, pos, selectedRows, selectedColumns);
        }
    }
    
    private void updateSelectedVisuals(final GridViewSkin skin, final TablePosition pos, final HashSet<Integer> selectedRows, final HashSet<Integer> selectedColumns) {
        if (skin != null) {
            skin.getSelectedRows().addAll((Collection)selectedColumns);
            skin.getSelectedColumns().addAll((Collection)selectedRows);
        }
        if (pos != null) {
            this.getCellsViewSkin().lastRowLayout.set(true);
            this.getCellsViewSkin().lastRowLayout.addListener((InvalidationListener)new InvalidationListener() {
                public void invalidated(final Observable observable) {
                    TableViewSpanSelectionModel.this.handleSelectedCellsListChangeEvent((ListChangeListener.Change<? extends TablePosition<ObservableList<SpreadsheetCell>, ?>>)new NonIterableChange.SimpleAddChange(0, TableViewSpanSelectionModel.this.selectedCellsMap.size(), (ObservableList)TableViewSpanSelectionModel.this.selectedCellsSeq));
                    TableViewSpanSelectionModel.this.getCellsViewSkin().lastRowLayout.removeListener((InvalidationListener)this);
                }
            });
        }
    }
    
    public void selectRange(final int minRow, final TableColumnBase<ObservableList<SpreadsheetCell>, ?> minColumn, final int maxRow, final TableColumnBase<ObservableList<SpreadsheetCell>, ?> maxColumn) {
        if (this.getSelectionMode() == SelectionMode.SINGLE) {
            this.quietClearSelection();
            this.select(maxRow, (TableColumnBase)maxColumn);
            return;
        }
        this.makeAtomic = true;
        final int itemCount = this.getItemCount();
        final int minColumnIndex = this.getTableView().getVisibleLeafIndex((TableColumn)minColumn);
        final int maxColumnIndex = this.getTableView().getVisibleLeafIndex((TableColumn)maxColumn);
        final int _minColumnIndex = Math.min(minColumnIndex, maxColumnIndex);
        final int _maxColumnIndex = Math.max(minColumnIndex, maxColumnIndex);
        final int _minRow = Math.min(minRow, maxRow);
        final int _maxRow = Math.max(minRow, maxRow);
        final HashSet<Integer> selectedRows = new HashSet<Integer>();
        final HashSet<Integer> selectedColumns = new HashSet<Integer>();
        for (int _row = _minRow; _row <= _maxRow; ++_row) {
            for (int _col = _minColumnIndex; _col <= _maxColumnIndex; ++_col) {
                if (_row >= 0) {
                    if (_row < itemCount) {
                        final TableColumn<ObservableList<SpreadsheetCell>, ?> column = (TableColumn<ObservableList<SpreadsheetCell>, ?>)this.getTableView().getVisibleLeafColumn(_col);
                        if (column != null) {
                            TablePosition<ObservableList<SpreadsheetCell>, ?> pos = this.getVisibleCell(_row, column);
                            final SpreadsheetCell cell = (SpreadsheetCell)column.getCellData(pos.getRow());
                            int i;
                            for (int rowSpan = this.spreadsheetView.getRowSpan(cell, pos.getRow()), currentRow = i = pos.getRow(); i < rowSpan + currentRow; ++i) {
                                selectedRows.add(i);
                                for (int j = this.spreadsheetView.getViewColumn(cell.getColumn()); j < this.spreadsheetView.getColumnSpan(cell) + this.spreadsheetView.getViewColumn(cell.getColumn()); ++j) {
                                    selectedColumns.add(j);
                                    pos = (TablePosition<ObservableList<SpreadsheetCell>, ?>)new TablePosition(this.getTableView(), i, this.getTableView().getVisibleLeafColumn(j));
                                    this.selectedCellsMap.add(pos);
                                }
                            }
                        }
                    }
                }
            }
        }
        this.makeAtomic = false;
        this.getSpreadsheetViewSkin().getSelectedRows().addAll((Collection)selectedRows);
        this.getSpreadsheetViewSkin().getSelectedColumns().addAll((Collection)selectedColumns);
        this.setSelectedIndex(maxRow);
        this.setSelectedItem(this.getModelItem(maxRow));
        if (this.getTableView().getFocusModel() == null) {
            return;
        }
        this.getTableView().getFocusModel().focus(maxRow, (TableColumn)maxColumn);
        final int startChangeIndex = this.selectedCellsMap.indexOf((TablePosition<ObservableList<SpreadsheetCell>, ?>)new TablePosition(this.getTableView(), minRow, (TableColumn)minColumn));
        final int endChangeIndex = this.selectedCellsMap.getSelectedCells().size() - 1;
        if (startChangeIndex > -1 && endChangeIndex > -1) {
            final int startIndex = Math.min(startChangeIndex, endChangeIndex);
            final int endIndex = Math.max(startChangeIndex, endChangeIndex);
            this.handleSelectedCellsListChangeEvent((ListChangeListener.Change<? extends TablePosition<ObservableList<SpreadsheetCell>, ?>>)new NonIterableChange.SimpleAddChange(startIndex, endIndex + 1, (ObservableList)this.selectedCellsSeq));
        }
    }
    
    public void selectAll() {
        if (this.getSelectionMode() == SelectionMode.SINGLE) {
            return;
        }
        this.quietClearSelection();
        final List<TablePosition<ObservableList<SpreadsheetCell>, ?>> indices = new ArrayList<TablePosition<ObservableList<SpreadsheetCell>, ?>>();
        TablePosition<ObservableList<SpreadsheetCell>, ?> tp = null;
        for (int col = 0; col < this.getTableView().getVisibleLeafColumns().size(); ++col) {
            final TableColumn<ObservableList<SpreadsheetCell>, ?> column = (TableColumn<ObservableList<SpreadsheetCell>, ?>)this.getTableView().getVisibleLeafColumns().get(col);
            for (int row = 0; row < this.getItemCount(); ++row) {
                tp = (TablePosition<ObservableList<SpreadsheetCell>, ?>)new TablePosition(this.getTableView(), row, (TableColumn)column);
                indices.add(tp);
            }
        }
        this.selectedCellsMap.setAll(indices);
        final ArrayList<Integer> selectedColumns = new ArrayList<Integer>();
        for (int col2 = 0; col2 < this.spreadsheetView.getGrid().getColumnCount(); ++col2) {
            selectedColumns.add(col2);
        }
        final ArrayList<Integer> selectedRows = new ArrayList<Integer>();
        for (int row2 = 0; row2 < this.getItemCount(); ++row2) {
            selectedRows.add(row2);
        }
        this.getSpreadsheetViewSkin().getSelectedRows().addAll((Collection)selectedRows);
        this.getSpreadsheetViewSkin().getSelectedColumns().addAll((Collection)selectedColumns);
        if (tp != null) {
            this.select(tp.getRow(), (TableColumn<ObservableList<SpreadsheetCell>, ?>)tp.getTableColumn());
            this.getTableView().getFocusModel().focus(0, (TableColumn)this.getTableView().getColumns().get(0));
        }
    }
    
    public boolean isSelected(final int row, final TableColumn<ObservableList<SpreadsheetCell>, ?> column) {
        if (column == null || row < 0) {
            return false;
        }
        final int columnIndex = this.getTableView().getVisibleLeafIndex((TableColumn)column);
        if (this.getCellsViewSkin().getCellsSize() != 0) {
            final TablePosition<ObservableList<SpreadsheetCell>, ?> posFinal = this.getVisibleCell(row, column);
            return this.selectedCellsMap.isSelected(posFinal.getRow(), posFinal.getColumn());
        }
        return this.selectedCellsMap.isSelected(row, columnIndex);
    }
    
    public List<TablePosition<ObservableList<SpreadsheetCell>, ?>> isSelectedRange(final int row, final TableColumn<ObservableList<SpreadsheetCell>, ?> column, final int col) {
        if (col < 0 || row < 0) {
            return null;
        }
        final SpreadsheetCell cell = (SpreadsheetCell)column.getCellData(row);
        final int supRow = row + this.spreadsheetView.getRowSpan(cell, row);
        final int infCol = this.spreadsheetView.getViewColumn(cell.getColumn());
        final int supCol = infCol + this.spreadsheetView.getColumnSpan(cell);
        final List<TablePosition<ObservableList<SpreadsheetCell>, ?>> selectedCells = new ArrayList<TablePosition<ObservableList<SpreadsheetCell>, ?>>();
        for (final TablePosition<ObservableList<SpreadsheetCell>, ?> tp : this.getSelectedCells()) {
            if (tp.getRow() >= row && tp.getRow() < supRow && tp.getColumn() >= infCol && tp.getColumn() < supCol) {
                selectedCells.add(tp);
            }
        }
        return selectedCells.isEmpty() ? null : selectedCells;
    }
    
    private void addSelectedRowsAndColumns(final TablePosition<?, ?> pos) {
        final GridViewSkin skin = this.getSpreadsheetViewSkin();
        if (skin == null) {
            return;
        }
        final SpreadsheetCell cell = (SpreadsheetCell)pos.getTableColumn().getCellData(pos.getRow());
        for (int rowSpan = this.spreadsheetView.getRowSpan(cell, pos.getRow()), i = pos.getRow(); i < rowSpan + pos.getRow(); ++i) {
            skin.getSelectedRows().add((Object)i);
            for (int j = this.spreadsheetView.getViewColumn(cell.getColumn()); j < this.spreadsheetView.getColumnSpan(cell) + this.spreadsheetView.getViewColumn(cell.getColumn()); ++j) {
                skin.getSelectedColumns().add((Object)j);
            }
        }
    }
    
    private void removeSelectedRowsAndColumns(final TablePosition<?, ?> pos) {
        final SpreadsheetCell cell = (SpreadsheetCell)pos.getTableColumn().getCellData(pos.getRow());
        for (int rowSpan = this.spreadsheetView.getRowSpan(cell, pos.getRow()), i = pos.getRow(); i < rowSpan + pos.getRow(); ++i) {
            this.getSpreadsheetViewSkin().getSelectedRows().remove((Object)i);
            for (int j = this.spreadsheetView.getViewColumn(cell.getColumn()); j < this.spreadsheetView.getColumnSpan(cell) + this.spreadsheetView.getViewColumn(cell.getColumn()); ++j) {
                this.getSpreadsheetViewSkin().getSelectedColumns().remove((Object)j);
            }
        }
    }
    
    public void clearAndSelect(final int row, final TableColumn<ObservableList<SpreadsheetCell>, ?> column) {
        this.makeAtomic = true;
        final List<TablePosition<ObservableList<SpreadsheetCell>, ?>> previousSelection = new ArrayList<TablePosition<ObservableList<SpreadsheetCell>, ?>>((Collection<? extends TablePosition<ObservableList<SpreadsheetCell>, ?>>)this.selectedCellsMap.getSelectedCells());
        this.clearSelection();
        this.select(row, column);
        this.makeAtomic = false;
        if (this.old != null && this.old.getColumn() >= 0) {
            final TableColumn<ObservableList<SpreadsheetCell>, ?> columnFinal = (TableColumn<ObservableList<SpreadsheetCell>, ?>)this.getTableView().getVisibleLeafColumn(this.old.getColumn());
            final int changeIndex = this.selectedCellsSeq.indexOf((Object)new TablePosition(this.getTableView(), this.old.getRow(), (TableColumn)columnFinal));
            final NonIterableChange.GenericAddRemoveChange<TablePosition<ObservableList<SpreadsheetCell>, ?>> change = (NonIterableChange.GenericAddRemoveChange<TablePosition<ObservableList<SpreadsheetCell>, ?>>)new NonIterableChange.GenericAddRemoveChange(changeIndex, changeIndex + 1, (List)previousSelection, (ObservableList)this.selectedCellsSeq);
            this.handleSelectedCellsListChangeEvent((ListChangeListener.Change<? extends TablePosition<ObservableList<SpreadsheetCell>, ?>>)change);
        }
    }
    
    public ObservableList<TablePosition> getSelectedCells() {
        return (ObservableList<TablePosition>)this.selectedCellsSeq;
    }
    
    public void selectAboveCell() {
        final TablePosition<ObservableList<SpreadsheetCell>, ?> pos = this.getFocusedCell();
        if (pos.getRow() == -1) {
            this.select(this.getItemCount() - 1);
        }
        else if (pos.getRow() > 0) {
            this.select(pos.getRow() - 1, (TableColumn<ObservableList<SpreadsheetCell>, ?>)pos.getTableColumn());
        }
    }
    
    public void selectBelowCell() {
        final TablePosition<ObservableList<SpreadsheetCell>, ?> pos = this.getFocusedCell();
        if (pos.getRow() == -1) {
            this.select(0);
        }
        else if (pos.getRow() < this.getItemCount() - 1) {
            this.select(pos.getRow() + 1, (TableColumn<ObservableList<SpreadsheetCell>, ?>)pos.getTableColumn());
        }
    }
    
    public void selectLeftCell() {
        if (!this.isCellSelectionEnabled()) {
            return;
        }
        final TablePosition<ObservableList<SpreadsheetCell>, ?> pos = this.getFocusedCell();
        if (pos.getColumn() - 1 >= 0) {
            this.select(pos.getRow(), this.getTableColumn((TableColumn<ObservableList<SpreadsheetCell>, ?>)pos.getTableColumn(), -1));
        }
    }
    
    public void selectRightCell() {
        if (!this.isCellSelectionEnabled()) {
            return;
        }
        final TablePosition<ObservableList<SpreadsheetCell>, ?> pos = this.getFocusedCell();
        if (pos.getColumn() + 1 < this.getTableView().getVisibleLeafColumns().size()) {
            this.select(pos.getRow(), this.getTableColumn((TableColumn<ObservableList<SpreadsheetCell>, ?>)pos.getTableColumn(), 1));
        }
    }
    
    public void clearSelection() {
        if (!this.makeAtomic) {
            this.setSelectedIndex(-1);
            this.setSelectedItem(this.getModelItem(-1));
            this.focus(-1);
        }
        this.quietClearSelection();
    }
    
    private void quietClearSelection() {
        this.selectedCellsMap.clear();
        final GridViewSkin skin = this.getSpreadsheetViewSkin();
        if (skin != null) {
            skin.getSelectedRows().clear();
            skin.getSelectedColumns().clear();
        }
    }
    
    private TablePosition<ObservableList<SpreadsheetCell>, ?> getFocusedCell() {
        if (this.getTableView().getFocusModel() == null) {
            return (TablePosition<ObservableList<SpreadsheetCell>, ?>)new TablePosition(this.getTableView(), -1, (TableColumn)null);
        }
        return (TablePosition<ObservableList<SpreadsheetCell>, ?>)this.cellsView.getFocusModel().getFocusedCell();
    }
    
    private TableColumn<ObservableList<SpreadsheetCell>, ?> getTableColumn(final TableColumn<ObservableList<SpreadsheetCell>, ?> column, final int offset) {
        final int columnIndex = this.getTableView().getVisibleLeafIndex((TableColumn)column);
        final int newColumnIndex = columnIndex + offset;
        return (TableColumn<ObservableList<SpreadsheetCell>, ?>)this.getTableView().getVisibleLeafColumn(newColumnIndex);
    }
    
    private GridViewSkin getSpreadsheetViewSkin() {
        return this.getCellsViewSkin();
    }
    
    private TablePosition<ObservableList<SpreadsheetCell>, ?> getVisibleCell(final int row, final TableColumn<ObservableList<SpreadsheetCell>, ?> column) {
        final int modelColumn = this.cellsView.getColumns().indexOf((Object)column);
        final SpreadsheetView.SpanType spanType = this.spreadsheetView.getSpanType(row, modelColumn);
        switch (spanType) {
            case NORMAL_CELL:
            case ROW_VISIBLE: {
                return (TablePosition<ObservableList<SpreadsheetCell>, ?>)new TablePosition((TableView)this.cellsView, row, (TableColumn)column);
            }
            default: {
                final SpreadsheetCell cell = (SpreadsheetCell)((ObservableList)this.cellsView.getItems().get(row)).get(modelColumn);
                final int firstRow = (this.getCellsViewSkin() == null) ? -1 : this.getCellsViewSkin().getFirstRow(cell, row);
                if (this.getCellsViewSkin() == null || (this.getCellsViewSkin().getCellsSize() != 0 && this.getNonFixedRow(0).getIndex() <= firstRow)) {
                    return (TablePosition<ObservableList<SpreadsheetCell>, ?>)new TablePosition((TableView)this.cellsView, firstRow, this.cellsView.getVisibleLeafColumn(this.spreadsheetView.getViewColumn(cell.getColumn())));
                }
                final GridRow gridRow = this.getNonFixedRow(0);
                return (TablePosition<ObservableList<SpreadsheetCell>, ?>)new TablePosition((TableView)this.cellsView, (gridRow == null) ? row : gridRow.getIndex(), this.cellsView.getVisibleLeafColumn(this.spreadsheetView.getViewColumn(cell.getColumn())));
            }
        }
    }
    
    final GridViewSkin getCellsViewSkin() {
        return (GridViewSkin)this.cellsView.getSkin();
    }
    
    private GridRow getNonFixedRow(final int index) {
        return this.getCellsViewSkin().getRow(index);
    }
}
