// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.spreadsheet;

import javafx.scene.control.Skin;
import javafx.application.Platform;
import javafx.scene.control.TableColumnBase;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetCellEditor;
import java.util.logging.Logger;
import javafx.scene.layout.Region;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.binding.When;
import javafx.scene.image.ImageView;
import java.util.Optional;
import javafx.scene.Cursor;
import java.util.Objects;
import javafx.animation.FadeTransition;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import org.controlsfx.control.spreadsheet.Filter;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.geometry.Side;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import java.util.Collection;
import javafx.event.Event;
import javafx.scene.control.SelectionMode;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.Control;
import javafx.event.WeakEventHandler;
import javafx.scene.input.MouseEvent;
import javafx.collections.WeakSetChangeListener;
import javafx.collections.SetChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.Node;
import javafx.beans.value.ChangeListener;
import javafx.util.Duration;
import javafx.scene.input.DragEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;

public class CellView extends TableCell<ObservableList<SpreadsheetCell>, SpreadsheetCell>
{
    final SpreadsheetHandle handle;
    private Tooltip tooltip;
    private EventHandler<DragEvent> dragOverHandler;
    private EventHandler<DragEvent> dragDropHandler;
    private static final String ANCHOR_PROPERTY_KEY = "table.anchor";
    private static final int TOOLTIP_MAX_WIDTH = 400;
    private static final Duration FADE_DURATION;
    private final ChangeListener<Node> graphicListener;
    private final WeakChangeListener<Node> weakGraphicListener;
    private final SetChangeListener<String> styleClassListener;
    private final WeakSetChangeListener<String> weakStyleClassListener;
    private ChangeListener<String> styleListener;
    private WeakChangeListener<String> weakStyleListener;
    private final EventHandler<MouseEvent> startFullDragEventHandler;
    private final EventHandler<MouseEvent> dragMouseEventHandler;
    private final ChangeListener<SpreadsheetCell> itemChangeListener;
    private final EventHandler<MouseEvent> actionEventHandler;
    private final WeakEventHandler weakActionhandler;
    
    static TablePositionBase<?> getAnchor(final Control table, final TablePositionBase<?> focusedCell) {
        return (TablePositionBase<?>)(hasAnchor(table) ? ((TablePositionBase)table.getProperties().get((Object)"table.anchor")) : focusedCell);
    }
    
    static boolean hasAnchor(final Control table) {
        return table.getProperties().get((Object)"table.anchor") != null;
    }
    
    static void setAnchor(final Control table, final TablePositionBase anchor) {
        if (table != null && anchor == null) {
            removeAnchor(table);
        }
        else {
            table.getProperties().put((Object)"table.anchor", (Object)anchor);
        }
    }
    
    static void removeAnchor(final Control table) {
        table.getProperties().remove((Object)"table.anchor");
    }
    
    public CellView(final SpreadsheetHandle handle) {
        this.graphicListener = (ChangeListener<Node>)new ChangeListener<Node>() {
            public void changed(final ObservableValue<? extends Node> arg0, final Node arg1, final Node newGraphic) {
                CellView.this.setCellGraphic((SpreadsheetCell)CellView.this.getItem());
            }
        };
        this.weakGraphicListener = (WeakChangeListener<Node>)new WeakChangeListener((ChangeListener)this.graphicListener);
        this.styleClassListener = (SetChangeListener<String>)new SetChangeListener<String>() {
            public void onChanged(final SetChangeListener.Change<? extends String> arg0) {
                if (arg0.wasAdded()) {
                    CellView.this.getStyleClass().add(arg0.getElementAdded());
                }
                else if (arg0.wasRemoved()) {
                    CellView.this.getStyleClass().remove(arg0.getElementRemoved());
                }
            }
        };
        this.weakStyleClassListener = (WeakSetChangeListener<String>)new WeakSetChangeListener((SetChangeListener)this.styleClassListener);
        this.startFullDragEventHandler = (EventHandler<MouseEvent>)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent arg0) {
                if (CellView.this.handle.getGridView().getSelectionModel().getSelectionMode().equals((Object)SelectionMode.MULTIPLE)) {
                    CellView.setAnchor((Control)CellView.this.getTableView(), (TablePositionBase)CellView.this.getTableView().getFocusModel().getFocusedCell());
                    CellView.this.startFullDrag();
                }
            }
        };
        this.dragMouseEventHandler = (EventHandler<MouseEvent>)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent arg0) {
                CellView.this.dragSelect(arg0);
            }
        };
        this.itemChangeListener = (ChangeListener<SpreadsheetCell>)new ChangeListener<SpreadsheetCell>() {
            public void changed(final ObservableValue<? extends SpreadsheetCell> arg0, final SpreadsheetCell oldItem, final SpreadsheetCell newItem) {
                if (oldItem != null) {
                    oldItem.getStyleClass().removeListener((SetChangeListener)CellView.this.weakStyleClassListener);
                    oldItem.graphicProperty().removeListener((ChangeListener)CellView.this.weakGraphicListener);
                    if (oldItem.styleProperty() != null) {
                        oldItem.styleProperty().removeListener((ChangeListener)CellView.this.weakStyleListener);
                    }
                }
                if (newItem != null) {
                    CellView.this.getStyleClass().clear();
                    CellView.this.getStyleClass().setAll((Collection)newItem.getStyleClass());
                    newItem.getStyleClass().addListener((SetChangeListener)CellView.this.weakStyleClassListener);
                    CellView.this.setCellGraphic(newItem);
                    newItem.graphicProperty().addListener((ChangeListener)CellView.this.weakGraphicListener);
                    if (newItem.styleProperty() != null) {
                        CellView.this.initStyleListener();
                        newItem.styleProperty().addListener((ChangeListener)CellView.this.weakStyleListener);
                        CellView.this.setStyle(newItem.getStyle());
                    }
                    else {
                        CellView.this.setStyle((String)null);
                    }
                }
            }
        };
        this.actionEventHandler = (EventHandler<MouseEvent>)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent event) {
                if (CellView.this.getItem() != null && ((SpreadsheetCell)CellView.this.getItem()).hasPopup() && MouseButton.PRIMARY.equals((Object)event.getButton()) && (CellView.this.getFilter() == null || !CellView.this.getFilter().getMenuButton().isShowing())) {
                    final ContextMenu menu = new ContextMenu();
                    menu.getScene().getStylesheets().add((Object)SpreadsheetView.class.getResource("spreadsheet.css").toExternalForm());
                    menu.getStyleClass().add((Object)"popup-button");
                    menu.getItems().setAll((Collection)((SpreadsheetCell)CellView.this.getItem()).getPopupItems());
                    menu.show((Node)CellView.this, Side.BOTTOM, 0.0, 0.0);
                }
            }
        };
        this.weakActionhandler = new WeakEventHandler((EventHandler)this.actionEventHandler);
        this.handle = handle;
        this.addEventHandler(MouseEvent.DRAG_DETECTED, (EventHandler)new WeakEventHandler((EventHandler)this.startFullDragEventHandler));
        this.setOnMouseDragEntered((EventHandler)new WeakEventHandler((EventHandler)this.dragMouseEventHandler));
        this.itemProperty().addListener((ChangeListener)this.itemChangeListener);
    }
    
    public void startEdit() {
        if (this.getParent() == null) {
            this.updateTableView((TableView)null);
            this.updateTableRow((TableRow)null);
            this.updateTableColumn((TableColumn)null);
            return;
        }
        if (!this.isEditable()) {
            this.getTableView().edit(-1, (TableColumn)null);
            return;
        }
        final int column = this.getTableView().getColumns().indexOf((Object)this.getTableColumn());
        final int row = this.getIndex();
        final SpreadsheetView spv = this.handle.getView();
        final SpreadsheetView.SpanType type = spv.getSpanType(row, column);
        if (type == SpreadsheetView.SpanType.NORMAL_CELL || type == SpreadsheetView.SpanType.ROW_VISIBLE) {
            if (!this.getTableRow().isManaged()) {
                return;
            }
            final GridCellEditor editor = this.getEditor((SpreadsheetCell)this.getItem(), spv);
            if (editor != null) {
                super.startEdit();
                this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                editor.startEdit();
            }
            else {
                this.getTableView().edit(-1, (TableColumn)null);
            }
        }
    }
    
    Filter getFilter() {
        final Filter filter = (this.getItem() != null) ? ((SpreadsheetColumn)this.handle.getView().getColumns().get(((SpreadsheetCell)this.getItem()).getColumn())).getFilter() : null;
        if (filter != null && ((SpreadsheetCell)this.getItem()).getRowSpan() > 1) {
            int i;
            for (int rowSpan = this.handle.getView().getRowSpan((SpreadsheetCell)this.getItem(), this.getIndex()), row = i = ((SpreadsheetCell)this.getItem()).getRow(); i < row + rowSpan; ++i) {
                if (this.handle.getView().getFilteredRow() == this.handle.getView().getModelRow(i)) {
                    return filter;
                }
            }
            return null;
        }
        return (filter != null && this.handle.getView().getFilteredRow() == this.handle.getView().getModelRow(this.getIndex())) ? filter : null;
    }
    
    public void commitEdit(final SpreadsheetCell newValue) {
        final FadeTransition fadeTransition = new FadeTransition(CellView.FADE_DURATION, (Node)this);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
        if (!this.isEditing()) {
            return;
        }
        super.commitEdit((Object)newValue);
        this.setContentDisplay(ContentDisplay.LEFT);
        this.updateItem(newValue, false);
        if (this.getTableView() != null) {
            this.getTableView().requestFocus();
        }
    }
    
    public void cancelEdit() {
        if (!this.isEditing()) {
            return;
        }
        super.cancelEdit();
        this.setContentDisplay(ContentDisplay.LEFT);
        this.updateItem((SpreadsheetCell)this.getItem(), false);
        final GridCellEditor editor = this.handle.getCellsViewSkin().getSpreadsheetCellEditorImpl();
        if (editor.isEditing()) {
            editor.endEdit(false);
        }
        if (this.getTableView() != null) {
            this.getTableView().requestFocus();
        }
    }
    
    public void updateItem(final SpreadsheetCell item, final boolean empty) {
        final boolean emptyRow = this.getTableView().getItems().size() < this.getIndex() + 1;
        if (!this.isEditing()) {
            super.updateItem((Object)item, empty && emptyRow);
        }
        if (empty && this.isSelected()) {
            this.updateSelected(false);
        }
        if (empty && emptyRow) {
            this.textProperty().unbind();
            this.setText((String)null);
            this.setContentDisplay((ContentDisplay)null);
        }
        else if (!this.isEditing() && item != null) {
            this.show(item);
            if (item.getGraphic() == null) {
                this.setGraphic((Node)null);
            }
        }
    }
    
    public void show(final SpreadsheetCell cell) {
        this.textProperty().bind((ObservableValue)cell.textProperty());
        this.setCellGraphic(cell);
        final Optional<String> tooltipText = cell.getTooltip();
        final String trimTooltip = tooltipText.isPresent() ? tooltipText.get().trim() : null;
        if (trimTooltip != null && !trimTooltip.isEmpty()) {
            final Tooltip localTooltip = this.getAvailableTooltip();
            if (localTooltip != null) {
                if (!Objects.equals(localTooltip.getText(), trimTooltip)) {
                    this.getTooltip().setText(trimTooltip);
                }
            }
            else {
                final Tooltip newTooltip;
                getValue(() -> {
                    newTooltip = new Tooltip((String)tooltipText.get());
                    newTooltip.setWrapText(true);
                    newTooltip.setMaxWidth(400.0);
                    this.setTooltip(newTooltip);
                    return;
                });
            }
        }
        else {
            if (this.getTooltip() != null) {
                this.tooltip = this.getTooltip();
            }
            this.setTooltip((Tooltip)null);
        }
        this.setWrapText(cell.isWrapText());
        this.setEditable(!cell.hasPopup() && cell.isEditable());
        if (cell.hasPopup()) {
            this.setOnMouseClicked((EventHandler)this.weakActionhandler);
            this.setCursor(Cursor.HAND);
        }
        else {
            this.setOnMouseClicked((EventHandler)null);
            this.setCursor(Cursor.DEFAULT);
        }
        if (cell.getCellType().acceptDrop()) {
            this.setOnDragOver((EventHandler)this.getDragOverHandler());
            this.setOnDragDropped((EventHandler)this.getDragDropHandler());
        }
        else {
            this.setOnDragOver((EventHandler)null);
            this.setOnDragDropped((EventHandler)null);
        }
    }
    
    private Tooltip getAvailableTooltip() {
        if (this.getTooltip() != null) {
            return this.getTooltip();
        }
        if (this.tooltip != null) {
            this.setTooltip(this.tooltip);
            return this.tooltip;
        }
        return null;
    }
    
    private void setCellGraphic(final SpreadsheetCell item) {
        if (this.isEditing()) {
            return;
        }
        final Node graphic = item.getGraphic();
        if (graphic != null) {
            if (graphic instanceof ImageView) {
                final ImageView image = (ImageView)graphic;
                image.setCache(true);
                image.setPreserveRatio(true);
                image.setSmooth(true);
                if (image.getImage() != null) {
                    image.fitHeightProperty().bind((ObservableValue)new When((ObservableBooleanValue)this.heightProperty().greaterThan(image.getImage().getHeight())).then(image.getImage().getHeight()).otherwise((ObservableNumberValue)this.heightProperty()));
                    image.fitWidthProperty().bind((ObservableValue)new When((ObservableBooleanValue)this.widthProperty().greaterThan(image.getImage().getWidth())).then(image.getImage().getWidth()).otherwise((ObservableNumberValue)this.widthProperty()));
                }
            }
            else if (graphic instanceof Region && item.getItem() == null) {
                final Region region = (Region)graphic;
                region.minHeightProperty().bind((ObservableValue)this.heightProperty());
                region.minWidthProperty().bind((ObservableValue)this.widthProperty());
            }
            this.setGraphic(graphic);
            if (!this.getChildren().contains((Object)graphic)) {
                this.getChildren().add((Object)graphic);
            }
        }
        else {
            this.setGraphic((Node)null);
        }
    }
    
    private GridCellEditor getEditor(final SpreadsheetCell cell, final SpreadsheetView spv) {
        final SpreadsheetCellType<?> cellType = (SpreadsheetCellType<?>)cell.getCellType();
        final Optional<SpreadsheetCellEditor> cellEditor = spv.getEditor(cellType);
        if (cellEditor.isPresent()) {
            final GridCellEditor editor = this.handle.getCellsViewSkin().getSpreadsheetCellEditorImpl();
            if (editor.isEditing()) {
                if (editor.getModelCell() != null) {
                    final StringBuilder builder = new StringBuilder();
                    builder.append("The cell at row ").append(editor.getModelCell().getRow()).append(" and column ").append(editor.getModelCell().getColumn()).append(" was in edition and cell at row ").append(cell.getRow()).append(" and column ").append(cell.getColumn()).append(" requested edition. This situation should not happen as the previous cell should not be in edition.");
                    Logger.getLogger("root").warning(builder.toString());
                }
                editor.endEdit(false);
            }
            editor.updateSpreadsheetCell(this);
            editor.updateDataCell(cell);
            editor.updateSpreadsheetCellEditor(cellEditor.get());
            return editor;
        }
        return null;
    }
    
    private EventHandler<DragEvent> getDragOverHandler() {
        if (this.dragOverHandler == null) {
            this.dragOverHandler = (EventHandler<DragEvent>)new EventHandler<DragEvent>() {
                public void handle(final DragEvent event) {
                    final Dragboard db = event.getDragboard();
                    if (db.hasFiles()) {
                        event.acceptTransferModes(TransferMode.ANY);
                    }
                    else {
                        event.consume();
                    }
                }
            };
        }
        return this.dragOverHandler;
    }
    
    private EventHandler<DragEvent> getDragDropHandler() {
        if (this.dragDropHandler == null) {
            this.dragDropHandler = (EventHandler<DragEvent>)new EventHandler<DragEvent>() {
                public void handle(final DragEvent event) {
                    final Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasFiles() && db.getFiles().size() == 1 && ((SpreadsheetCell)CellView.this.getItem()).getCellType().match(db.getFiles().get(0))) {
                        CellView.this.handle.getView().getGrid().setCellValue(((SpreadsheetCell)CellView.this.getItem()).getRow(), ((SpreadsheetCell)CellView.this.getItem()).getColumn(), ((SpreadsheetCell)CellView.this.getItem()).getCellType().convertValue(db.getFiles().get(0)));
                        success = true;
                    }
                    event.setDropCompleted(success);
                    event.consume();
                }
            };
        }
        return this.dragDropHandler;
    }
    
    private void dragSelect(final MouseEvent e) {
        if (!this.contains(e.getX(), e.getY())) {
            return;
        }
        final TableView<ObservableList<SpreadsheetCell>> tableView = (TableView<ObservableList<SpreadsheetCell>>)this.getTableView();
        if (tableView == null) {
            return;
        }
        final int count = tableView.getItems().size();
        if (this.getIndex() >= count) {
            return;
        }
        final TableView.TableViewSelectionModel<ObservableList<SpreadsheetCell>> sm = (TableView.TableViewSelectionModel<ObservableList<SpreadsheetCell>>)tableView.getSelectionModel();
        if (sm == null) {
            return;
        }
        final int row = this.getIndex();
        final int column = tableView.getVisibleLeafIndex(this.getTableColumn());
        final SpreadsheetCell cell = (SpreadsheetCell)this.getItem();
        final int rowCell = this.getIndex() + this.handle.getView().getRowSpan(cell, this.getIndex()) - 1;
        final int columnCell = this.handle.getView().getViewColumn(cell.getColumn()) + this.handle.getView().getColumnSpan(cell) - 1;
        final TableView.TableViewFocusModel<?> fm = (TableView.TableViewFocusModel<?>)tableView.getFocusModel();
        if (fm == null) {
            return;
        }
        final TablePositionBase<?> focusedCell = (TablePositionBase<?>)fm.getFocusedCell();
        final MouseButton button = e.getButton();
        if (button == MouseButton.PRIMARY) {
            final TablePositionBase<?> anchor = getAnchor((Control)tableView, focusedCell);
            int minRow = Math.min(anchor.getRow(), row);
            minRow = Math.min(minRow, rowCell);
            int maxRow = Math.max(anchor.getRow(), row);
            maxRow = Math.max(maxRow, rowCell);
            int minColumn = Math.min(anchor.getColumn(), column);
            minColumn = Math.min(minColumn, columnCell);
            int maxColumn = Math.max(anchor.getColumn(), column);
            maxColumn = Math.max(maxColumn, columnCell);
            if (!e.isShortcutDown()) {
                sm.clearSelection();
            }
            if (minColumn != -1 && maxColumn != -1) {
                sm.selectRange(minRow, (TableColumnBase)tableView.getVisibleLeafColumn(minColumn), maxRow, (TableColumnBase)tableView.getVisibleLeafColumn(maxColumn));
            }
            setAnchor((Control)tableView, anchor);
        }
    }
    
    public static void getValue(final Runnable runnable) {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        }
        else {
            Platform.runLater(runnable);
        }
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new CellViewSkin(this);
    }
    
    private void initStyleListener() {
        if (this.styleListener == null) {
            this.styleListener = (ChangeListener<String>)((observable, oldValue, newValue) -> this.styleProperty().set((Object)newValue));
        }
        this.weakStyleListener = (WeakChangeListener<String>)new WeakChangeListener((ChangeListener)this.styleListener);
    }
    
    static {
        FADE_DURATION = Duration.millis(200.0);
    }
}
