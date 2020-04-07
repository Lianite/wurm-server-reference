// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.spreadsheet;

import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.scene.image.ImageView;
import javafx.stage.WindowEvent;
import javafx.scene.control.MenuItem;
import impl.org.controlsfx.i18n.Localization;
import javafx.scene.Cursor;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableColumn;
import javafx.geometry.NodeOrientation;
import javafx.collections.ObservableList;
import java.util.Set;
import javafx.beans.value.ObservableNumberValue;
import java.util.Iterator;
import javafx.scene.control.ScrollBar;
import java.util.Collection;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import org.controlsfx.control.spreadsheet.Picker;
import javafx.event.EventTarget;
import javafx.event.Event;
import java.util.ArrayList;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.InvalidationListener;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import java.util.BitSet;
import java.util.Stack;
import javafx.scene.control.Label;
import java.util.List;
import javafx.scene.control.ContextMenu;
import javafx.scene.shape.Rectangle;
import javafx.beans.property.DoubleProperty;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

public class VerticalHeader extends StackPane
{
    public static final int PICKER_SIZE = 16;
    private static final int DRAG_RECT_HEIGHT = 5;
    private static final String TABLE_ROW_KEY = "TableRow";
    private static final String PICKER_INDEX = "PickerIndex";
    private static final String TABLE_LABEL_KEY = "Label";
    private static final Image pinImage;
    private final SpreadsheetHandle handle;
    private final SpreadsheetView spreadsheetView;
    private double horizontalHeaderHeight;
    private final DoubleProperty innerVerticalHeaderWidth;
    private Rectangle clip;
    private ContextMenu blankContextMenu;
    private double lastY;
    private static double dragAnchorY;
    private final List<Rectangle> dragRects;
    private int dragRectCount;
    private final List<Label> labelList;
    private int labelCount;
    private GridViewSkin skin;
    private boolean resizing;
    private final Stack<Label> pickerPile;
    private final Stack<Label> pickerUsed;
    private final BitSet selectedRows;
    private final EventHandler<MouseEvent> rectMousePressed;
    private final EventHandler<MouseEvent> rectMouseDragged;
    private final EventHandler<MouseEvent> rectMouseReleased;
    private final EventHandler<MouseEvent> pickerMouseEvent;
    private final InvalidationListener layout;
    
    public VerticalHeader(final SpreadsheetHandle handle) {
        this.innerVerticalHeaderWidth = (DoubleProperty)new SimpleDoubleProperty();
        this.lastY = 0.0;
        this.dragRects = new ArrayList<Rectangle>();
        this.dragRectCount = 0;
        this.labelList = new ArrayList<Label>();
        this.labelCount = 0;
        this.resizing = false;
        this.selectedRows = new BitSet();
        this.rectMousePressed = (EventHandler<MouseEvent>)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent me) {
                if (me.getClickCount() == 2 && me.isPrimaryButtonDown()) {
                    final Rectangle rect = (Rectangle)me.getSource();
                    final GridRow row = (GridRow)rect.getProperties().get((Object)"TableRow");
                    VerticalHeader.this.skin.resizeRowToFitContent(VerticalHeader.this.spreadsheetView.getModelRow(row.getIndex()));
                    VerticalHeader.this.requestLayout();
                }
                else {
                    VerticalHeader.dragAnchorY = me.getSceneY();
                    VerticalHeader.this.resizing = true;
                }
                me.consume();
            }
        };
        this.rectMouseDragged = (EventHandler<MouseEvent>)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent me) {
                final Rectangle rect = (Rectangle)me.getSource();
                final GridRow row = (GridRow)rect.getProperties().get((Object)"TableRow");
                final Label label = (Label)rect.getProperties().get((Object)"Label");
                if (row != null) {
                    VerticalHeader.this.rowResizing(row, label, me);
                }
                me.consume();
            }
        };
        this.rectMouseReleased = (EventHandler<MouseEvent>)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent me) {
                VerticalHeader.this.lastY = 0.0;
                VerticalHeader.this.resizing = false;
                VerticalHeader.this.requestLayout();
                me.consume();
                final Rectangle rect = (Rectangle)me.getSource();
                final GridRow row = (GridRow)rect.getProperties().get((Object)"TableRow");
                if (VerticalHeader.this.selectedRows.get(row.getIndex())) {
                    final double height = row.getHeight();
                    for (int i = VerticalHeader.this.selectedRows.nextSetBit(0); i >= 0; i = VerticalHeader.this.selectedRows.nextSetBit(i + 1)) {
                        VerticalHeader.this.skin.rowHeightMap.put((Object)VerticalHeader.this.spreadsheetView.getModelRow(i), (Object)height);
                        Event.fireEvent((EventTarget)VerticalHeader.this.spreadsheetView, (Event)new SpreadsheetView.RowHeightEvent(VerticalHeader.this.spreadsheetView.getModelRow(i), height));
                    }
                }
            }
        };
        this.pickerMouseEvent = (EventHandler<MouseEvent>)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent mouseEvent) {
                final Label picker = (Label)mouseEvent.getSource();
                ((Picker)picker.getProperties().get((Object)"PickerIndex")).onClick();
            }
        };
        this.layout = (arg0 -> this.requestLayout());
        this.handle = handle;
        this.spreadsheetView = handle.getView();
        this.pickerPile = new Stack<Label>();
        this.pickerUsed = new Stack<Label>();
    }
    
    void init(final GridViewSkin skin, final HorizontalHeader horizontalHeader) {
        this.skin = skin;
        horizontalHeader.heightProperty().addListener((ChangeListener)new ChangeListener<Number>() {
            public void changed(final ObservableValue<? extends Number> arg0, final Number oldHeight, final Number newHeight) {
                VerticalHeader.this.horizontalHeaderHeight = newHeight.doubleValue();
                VerticalHeader.this.requestLayout();
            }
        });
        this.handle.getView().gridProperty().addListener(this.layout);
        this.handle.getView().hiddenRowsProperty().addListener(this.layout);
        this.handle.getView().hiddenColumnsProperty().addListener(this.layout);
        (this.clip = new Rectangle(this.getVerticalHeaderWidth(), this.snapSize(((TableView)skin.getSkinnable()).getHeight()))).relocate(this.snappedTopInset(), this.snappedLeftInset());
        this.clip.setSmooth(false);
        this.clip.heightProperty().bind((ObservableValue)((TableView)skin.getSkinnable()).heightProperty());
        this.clip.widthProperty().bind((ObservableValue)this.innerVerticalHeaderWidth);
        this.setClip((Node)this.clip);
        this.spreadsheetView.showRowHeaderProperty().addListener(this.layout);
        this.spreadsheetView.showColumnHeaderProperty().addListener(this.layout);
        this.spreadsheetView.getFixedRows().addListener(this.layout);
        this.spreadsheetView.fixingRowsAllowedProperty().addListener(this.layout);
        this.spreadsheetView.rowHeaderWidthProperty().addListener(this.layout);
        this.spreadsheetView.heightProperty().addListener(this.layout);
        this.spreadsheetView.getRowPickers().addListener(this.layout);
        skin.getSelectedRows().addListener(this.layout);
        this.blankContextMenu = new ContextMenu();
    }
    
    public double getVerticalHeaderWidth() {
        return this.innerVerticalHeaderWidth.get();
    }
    
    public ReadOnlyDoubleProperty verticalHeaderWidthProperty() {
        return (ReadOnlyDoubleProperty)this.innerVerticalHeaderWidth;
    }
    
    public double computeHeaderWidth() {
        double width = 0.0;
        if (!this.spreadsheetView.getRowPickers().isEmpty()) {
            width += 16.0;
        }
        if (this.spreadsheetView.isShowRowHeader()) {
            width += this.spreadsheetView.getRowHeaderWidth();
        }
        return width;
    }
    
    void clearSelectedRows() {
        this.selectedRows.clear();
    }
    
    protected void layoutChildren() {
        if (this.resizing) {
            return;
        }
        if ((this.spreadsheetView.isShowRowHeader() || !this.spreadsheetView.getRowPickers().isEmpty()) && this.skin.getCellsSize() > 0) {
            double x = this.snappedLeftInset();
            this.pickerPile.addAll((Collection<?>)this.pickerUsed.subList(0, this.pickerUsed.size()));
            this.pickerUsed.clear();
            this.labelCount = 0;
            this.dragRectCount = 0;
            if (!this.spreadsheetView.getRowPickers().isEmpty()) {
                this.innerVerticalHeaderWidth.setValue((Number)16);
                x += 16.0;
            }
            else {
                this.innerVerticalHeaderWidth.setValue((Number)0);
            }
            if (this.spreadsheetView.isShowRowHeader()) {
                this.innerVerticalHeaderWidth.setValue((Number)(this.getVerticalHeaderWidth() + this.spreadsheetView.getRowHeaderWidth()));
            }
            this.getChildren().clear();
            final int cellSize = this.skin.getCellsSize();
            this.addVisibleRows(x, cellSize);
            this.addFixedRows(x, cellSize);
            if (this.spreadsheetView.showColumnHeaderProperty().get()) {
                final Label label = this.getLabel(null);
                label.setOnMousePressed(event -> this.spreadsheetView.getSelectionModel().selectAll());
                label.setText("");
                label.resize(this.spreadsheetView.getRowHeaderWidth(), this.horizontalHeaderHeight);
                label.layoutYProperty().unbind();
                label.setLayoutY(0.0);
                label.setLayoutX(x);
                label.getStyleClass().clear();
                label.setContextMenu(this.blankContextMenu);
                this.getChildren().add((Object)label);
            }
            final ScrollBar hbar = this.handle.getCellsViewSkin().getHBar();
            if (hbar.isVisible()) {
                final Label label = this.getLabel(null);
                label.getProperties().put((Object)"TableRow", (Object)null);
                label.setText("");
                label.resize(this.getVerticalHeaderWidth(), hbar.getHeight());
                label.layoutYProperty().unbind();
                label.relocate(this.snappedLeftInset(), this.getHeight() - hbar.getHeight());
                label.getStyleClass().clear();
                label.setContextMenu(this.blankContextMenu);
                this.getChildren().add((Object)label);
            }
        }
        else {
            this.getChildren().clear();
        }
    }
    
    public static boolean isFixedRowEmpty(final SpreadsheetView spreadsheetView) {
        for (final Integer fixedRow : spreadsheetView.getFixedRows()) {
            if (!spreadsheetView.getHiddenRows().get(fixedRow)) {
                return false;
            }
        }
        return true;
    }
    
    private void addFixedRows(final double x, final int cellSize) {
        double spaceUsedByFixedRows = 0.0;
        final Set<Integer> currentlyFixedRow = (Set<Integer>)this.handle.getCellsViewSkin().getCurrentlyFixedRow();
        if (!isFixedRowEmpty(this.spreadsheetView) && cellSize != 0) {
            for (int j = 0; j < this.spreadsheetView.getFixedRows().size(); ++j) {
                final int modelRow = (int)this.spreadsheetView.getFixedRows().get(j);
                if (!this.spreadsheetView.getHiddenRows().get(modelRow)) {
                    final int viewRow = this.spreadsheetView.getFilteredRow(modelRow);
                    if (!currentlyFixedRow.contains(viewRow)) {
                        break;
                    }
                    final double rowHeight = this.skin.getRowHeight(viewRow);
                    final double y = this.spreadsheetView.showColumnHeaderProperty().get() ? (this.snappedTopInset() + this.horizontalHeaderHeight + spaceUsedByFixedRows) : (this.snappedTopInset() + spaceUsedByFixedRows);
                    if (this.spreadsheetView.getRowPickers().containsKey((Object)modelRow)) {
                        final Label picker = this.getPicker((Picker)this.spreadsheetView.getRowPickers().get((Object)modelRow));
                        picker.resize(16.0, rowHeight);
                        picker.layoutYProperty().unbind();
                        picker.setLayoutY(y);
                        this.getChildren().add((Object)picker);
                    }
                    if (this.spreadsheetView.isShowRowHeader()) {
                        final Label label = this.getLabel(viewRow);
                        final GridRow row = this.skin.getRowIndexed(viewRow);
                        label.getProperties().put((Object)"TableRow", (Object)row);
                        label.setText(this.getRowHeader(viewRow));
                        label.resize(this.spreadsheetView.getRowHeaderWidth(), rowHeight);
                        label.setContextMenu(this.getRowContextMenu(viewRow));
                        if (row != null) {
                            label.layoutYProperty().bind((ObservableValue)row.layoutYProperty().add(this.horizontalHeaderHeight).add((ObservableNumberValue)row.verticalShift));
                        }
                        label.setLayoutX(x);
                        final ObservableList<String> css = (ObservableList<String>)label.getStyleClass();
                        if (this.skin.getSelectedRows().contains((Object)viewRow)) {
                            css.addAll((Object[])new String[] { "selected" });
                        }
                        else {
                            css.removeAll((Object[])new String[] { "selected" });
                        }
                        css.addAll((Object[])new String[] { "fixed" });
                        this.getChildren().add((Object)label);
                        if (this.spreadsheetView.getGrid().isRowResizable(viewRow)) {
                            final Rectangle dragRect = this.getDragRect();
                            dragRect.getProperties().put((Object)"TableRow", (Object)row);
                            dragRect.getProperties().put((Object)"Label", (Object)label);
                            dragRect.setWidth(label.getWidth());
                            dragRect.relocate(this.snappedLeftInset() + x, y + rowHeight - 5.0);
                            this.getChildren().add((Object)dragRect);
                        }
                    }
                    spaceUsedByFixedRows += this.skin.getRowHeight(viewRow);
                }
            }
        }
    }
    
    private void addVisibleRows(final double x, final int cellSize) {
        double y = this.snappedTopInset();
        if (this.spreadsheetView.showColumnHeaderProperty().get()) {
            y += this.horizontalHeaderHeight;
        }
        if (cellSize != 0) {
            y += this.skin.getRow(0).getLocalToParentTransform().getTy();
        }
        final int viewRowCount = this.skin.getItemCount();
        int i = 0;
        GridRow row = this.skin.getRow(i);
        final double fixedRowHeight = this.skin.getFixedRowHeight();
        final double rowHeaderWidth = this.spreadsheetView.getRowHeaderWidth();
        while (cellSize != 0 && row != null && row.getIndex() < viewRowCount) {
            final int rowIndex = row.getIndex();
            final double height = row.getHeight();
            final int modelRow = this.spreadsheetView.getFilteredSourceIndex(rowIndex);
            if (row.getLayoutY() >= fixedRowHeight && this.spreadsheetView.getRowPickers().containsKey((Object)modelRow)) {
                final Label picker = this.getPicker((Picker)this.spreadsheetView.getRowPickers().get((Object)modelRow));
                picker.resize(16.0, height);
                picker.layoutYProperty().bind((ObservableValue)row.layoutYProperty().add(this.horizontalHeaderHeight));
                this.getChildren().add((Object)picker);
            }
            if (this.spreadsheetView.isShowRowHeader()) {
                final Label label = this.getLabel(rowIndex);
                label.getProperties().put((Object)"TableRow", (Object)row);
                label.setText(this.getRowHeader(rowIndex));
                label.resize(rowHeaderWidth, height);
                label.setLayoutX(x);
                label.layoutYProperty().bind((ObservableValue)row.layoutYProperty().add(this.horizontalHeaderHeight));
                label.setContextMenu(this.getRowContextMenu(rowIndex));
                this.getChildren().add((Object)label);
                final ObservableList<String> css = (ObservableList<String>)label.getStyleClass();
                if (this.skin.getSelectedRows().contains((Object)rowIndex)) {
                    css.addAll((Object[])new String[] { "selected" });
                }
                else {
                    css.removeAll((Object[])new String[] { "selected" });
                }
                if (this.spreadsheetView.getFixedRows().contains((Object)modelRow)) {
                    css.addAll((Object[])new String[] { "fixed" });
                }
                else {
                    css.removeAll((Object[])new String[] { "fixed" });
                }
                y += height;
                if (this.spreadsheetView.getGrid().isRowResizable(modelRow)) {
                    final Rectangle dragRect = this.getDragRect();
                    dragRect.getProperties().put((Object)"TableRow", (Object)row);
                    dragRect.getProperties().put((Object)"Label", (Object)label);
                    dragRect.setWidth(label.getWidth());
                    dragRect.relocate(this.snappedLeftInset() + x, y - 5.0);
                    this.getChildren().add((Object)dragRect);
                }
            }
            row = this.skin.getRow(++i);
        }
    }
    
    private void rowResizing(final GridRow gridRow, final Label label, final MouseEvent me) {
        double draggedY = me.getSceneY() - VerticalHeader.dragAnchorY;
        if (gridRow.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT) {
            draggedY = -draggedY;
        }
        final double delta = draggedY - this.lastY;
        final Double newHeight = gridRow.getHeight() + delta;
        if (newHeight < 0.0) {
            return;
        }
        this.handle.getCellsViewSkin().rowHeightMap.put((Object)this.spreadsheetView.getModelRow(gridRow.getIndex()), (Object)newHeight);
        Event.fireEvent((EventTarget)this.spreadsheetView, (Event)new SpreadsheetView.RowHeightEvent(this.spreadsheetView.getModelRow(gridRow.getIndex()), newHeight));
        label.resize(this.spreadsheetView.getRowHeaderWidth(), (double)newHeight);
        gridRow.setPrefHeight((double)newHeight);
        gridRow.requestLayout();
        this.lastY = draggedY;
    }
    
    private Label getLabel(final Integer row) {
        Label label;
        if (this.labelList.isEmpty() || this.labelList.size() <= this.labelCount) {
            label = new Label();
            this.labelList.add(label);
        }
        else {
            label = this.labelList.get(this.labelCount);
        }
        ++this.labelCount;
        label.setOnMousePressed((row == null) ? null : (event -> {
            if (event.isPrimaryButtonDown()) {
                if (event.getClickCount() == 2) {
                    this.skin.resizeRowToFitContent(this.spreadsheetView.getModelRow(row));
                    this.requestLayout();
                }
                else {
                    this.headerClicked(row, event);
                }
            }
        }));
        return label;
    }
    
    private void headerClicked(final int row, final MouseEvent event) {
        final TableView.TableViewSelectionModel<ObservableList<SpreadsheetCell>> sm = (TableView.TableViewSelectionModel<ObservableList<SpreadsheetCell>>)this.handle.getGridView().getSelectionModel();
        final int focusedRow = sm.getFocusedIndex();
        final int rowCount = this.handle.getCellsViewSkin().getItemCount();
        final ObservableList<TableColumn<ObservableList<SpreadsheetCell>, ?>> columns = (ObservableList<TableColumn<ObservableList<SpreadsheetCell>, ?>>)sm.getTableView().getColumns();
        final TableColumn<ObservableList<SpreadsheetCell>, ?> firstColumn = (TableColumn<ObservableList<SpreadsheetCell>, ?>)columns.get(0);
        final TableColumn<ObservableList<SpreadsheetCell>, ?> lastColumn = (TableColumn<ObservableList<SpreadsheetCell>, ?>)columns.get(columns.size() - 1);
        if (event.isShortcutDown()) {
            final BitSet tempSet = (BitSet)this.selectedRows.clone();
            sm.selectRange(row, (TableColumnBase)firstColumn, row, (TableColumnBase)lastColumn);
            this.selectedRows.or(tempSet);
            this.selectedRows.set(row);
        }
        else if (event.isShiftDown() && focusedRow >= 0 && focusedRow < rowCount) {
            sm.clearSelection();
            sm.selectRange(focusedRow, (TableColumnBase)firstColumn, row, (TableColumnBase)lastColumn);
            sm.getTableView().getFocusModel().focus(focusedRow, (TableColumn)firstColumn);
            final int min = Math.min(row, focusedRow);
            final int max = Math.max(row, focusedRow);
            this.selectedRows.set(min, max + 1);
        }
        else {
            sm.clearSelection();
            sm.selectRange(row, (TableColumnBase)firstColumn, row, (TableColumnBase)lastColumn);
            sm.getTableView().getFocusModel().focus(row, (TableColumn)firstColumn);
            this.selectedRows.set(row);
        }
    }
    
    private Label getPicker(final Picker picker) {
        Label pickerLabel;
        if (this.pickerPile.isEmpty()) {
            pickerLabel = new Label();
            picker.getStyleClass().addListener(this.layout);
            pickerLabel.setOnMouseClicked((EventHandler)this.pickerMouseEvent);
        }
        else {
            pickerLabel = this.pickerPile.pop();
        }
        this.pickerUsed.push(pickerLabel);
        pickerLabel.getStyleClass().setAll((Collection)picker.getStyleClass());
        pickerLabel.getProperties().put((Object)"PickerIndex", (Object)picker);
        return pickerLabel;
    }
    
    private Rectangle getDragRect() {
        if (this.dragRects.isEmpty() || this.dragRects.size() <= this.dragRectCount) {
            final Rectangle rect = new Rectangle();
            rect.setWidth(this.getVerticalHeaderWidth());
            rect.setHeight(5.0);
            rect.setFill((Paint)Color.TRANSPARENT);
            rect.setSmooth(false);
            rect.setOnMousePressed((EventHandler)this.rectMousePressed);
            rect.setOnMouseDragged((EventHandler)this.rectMouseDragged);
            rect.setOnMouseReleased((EventHandler)this.rectMouseReleased);
            rect.setCursor(Cursor.V_RESIZE);
            this.dragRects.add(rect);
            ++this.dragRectCount;
            return rect;
        }
        return this.dragRects.get(this.dragRectCount++);
    }
    
    private ContextMenu getRowContextMenu(final Integer row) {
        if (this.spreadsheetView.isRowFixable(row)) {
            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem fixItem = new MenuItem(Localization.localize(Localization.asKey("spreadsheet.verticalheader.menu.fix")));
            contextMenu.setOnShowing((EventHandler)new EventHandler<WindowEvent>() {
                public void handle(final WindowEvent event) {
                    if (VerticalHeader.this.spreadsheetView.getFixedRows().contains((Object)VerticalHeader.this.spreadsheetView.getFilteredSourceIndex(row))) {
                        fixItem.setText(Localization.localize(Localization.asKey("spreadsheet.verticalheader.menu.unfix")));
                    }
                    else {
                        fixItem.setText(Localization.localize(Localization.asKey("spreadsheet.verticalheader.menu.fix")));
                    }
                }
            });
            fixItem.setGraphic((Node)new ImageView(VerticalHeader.pinImage));
            fixItem.setOnAction((EventHandler)new EventHandler<ActionEvent>() {
                public void handle(final ActionEvent arg0) {
                    final Integer modelRow = VerticalHeader.this.spreadsheetView.getFilteredSourceIndex(row);
                    if (VerticalHeader.this.spreadsheetView.getFixedRows().contains((Object)modelRow)) {
                        VerticalHeader.this.spreadsheetView.getFixedRows().remove((Object)modelRow);
                    }
                    else {
                        VerticalHeader.this.spreadsheetView.getFixedRows().add((Object)modelRow);
                    }
                }
            });
            contextMenu.getItems().add((Object)fixItem);
            return contextMenu;
        }
        return this.blankContextMenu;
    }
    
    private String getRowHeader(final int index) {
        final int newIndex = this.spreadsheetView.getFilteredSourceIndex(index);
        return (String)((this.spreadsheetView.getGrid().getRowHeaders().size() > newIndex) ? this.spreadsheetView.getGrid().getRowHeaders().get(newIndex) : String.valueOf(newIndex + 1));
    }
    
    static {
        pinImage = new Image(SpreadsheetView.class.getResource("pinSpreadsheetView.png").toExternalForm());
        VerticalHeader.dragAnchorY = 0.0;
    }
}
