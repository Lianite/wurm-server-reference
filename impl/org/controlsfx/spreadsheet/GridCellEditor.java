// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.spreadsheet;

import javafx.beans.binding.Bindings;
import javafx.scene.control.TextArea;
import javafx.scene.control.Control;
import javafx.scene.Node;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import org.controlsfx.control.spreadsheet.SpreadsheetCellEditor;
import javafx.beans.binding.BooleanExpression;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;

public class GridCellEditor
{
    private final SpreadsheetHandle handle;
    private SpreadsheetCell modelCell;
    private CellView viewCell;
    private BooleanExpression focusProperty;
    private boolean editing;
    private SpreadsheetCellEditor spreadsheetCellEditor;
    private KeyCode lastKeyPressed;
    private final EventHandler<KeyEvent> enterKeyPressed;
    private final ChangeListener<Boolean> focusListener;
    private final InvalidationListener endEditionListener;
    
    public GridCellEditor(final SpreadsheetHandle handle) {
        this.editing = false;
        this.enterKeyPressed = (EventHandler<KeyEvent>)new EventHandler<KeyEvent>() {
            public void handle(final KeyEvent t) {
                GridCellEditor.this.lastKeyPressed = t.getCode();
            }
        };
        this.focusListener = (ChangeListener<Boolean>)new ChangeListener<Boolean>() {
            public void changed(final ObservableValue<? extends Boolean> observable, final Boolean oldValue, final Boolean isFocus) {
                if (!isFocus) {
                    GridCellEditor.this.endEdit(true);
                }
            }
        };
        this.endEditionListener = (InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable observable) {
                GridCellEditor.this.endEdit(true);
            }
        };
        this.handle = handle;
    }
    
    public void updateDataCell(final SpreadsheetCell cell) {
        this.modelCell = cell;
    }
    
    public void updateSpreadsheetCell(final CellView cell) {
        this.viewCell = cell;
    }
    
    public void updateSpreadsheetCellEditor(final SpreadsheetCellEditor spreadsheetCellEditor) {
        this.spreadsheetCellEditor = spreadsheetCellEditor;
    }
    
    public void endEdit(final boolean commitValue) {
        if (commitValue && this.editing) {
            final SpreadsheetView view = this.handle.getView();
            final boolean match = this.modelCell.getCellType().match(this.spreadsheetCellEditor.getControlValue());
            if (match && this.viewCell != null) {
                final Object value = this.modelCell.getCellType().convertValue(this.spreadsheetCellEditor.getControlValue());
                view.getGrid().setCellValue(this.modelCell.getRow(), this.modelCell.getColumn(), value);
                this.editing = false;
                this.viewCell.commitEdit(this.modelCell);
                this.end();
                this.spreadsheetCellEditor.end();
                if (KeyCode.ENTER.equals((Object)this.lastKeyPressed)) {
                    ((GridViewBehavior)this.handle.getCellsViewSkin().getBehavior()).selectCell(1, 0);
                }
                else if (KeyCode.TAB.equals((Object)this.lastKeyPressed)) {
                    this.handle.getView().getSelectionModel().clearAndSelectRightCell();
                    this.handle.getCellsViewSkin().scrollHorizontally();
                }
            }
        }
        if (this.editing) {
            this.editing = false;
            if (this.viewCell != null) {
                this.viewCell.cancelEdit();
            }
            this.end();
            if (this.spreadsheetCellEditor != null) {
                this.spreadsheetCellEditor.end();
            }
        }
    }
    
    public boolean isEditing() {
        return this.editing;
    }
    
    public SpreadsheetCell getModelCell() {
        return this.modelCell;
    }
    
    void startEdit() {
        this.lastKeyPressed = null;
        this.editing = true;
        this.handle.getGridView().addEventFilter(KeyEvent.KEY_PRESSED, (EventHandler)this.enterKeyPressed);
        this.handle.getCellsViewSkin().getVBar().valueProperty().addListener(this.endEditionListener);
        this.handle.getCellsViewSkin().getHBar().valueProperty().addListener(this.endEditionListener);
        final Control editor = this.spreadsheetCellEditor.getEditor();
        final Object value = this.modelCell.getItem();
        final Double maxHeight = Math.min(this.viewCell.getHeight(), this.spreadsheetCellEditor.getMaxHeight());
        if (editor != null) {
            this.viewCell.setGraphic((Node)editor);
            editor.setMaxHeight((double)maxHeight);
            editor.setPrefWidth(this.viewCell.getWidth());
        }
        this.spreadsheetCellEditor.startEdit(value, this.modelCell.getFormat());
        if (editor != null) {
            (this.focusProperty = this.getFocusProperty(editor)).addListener((ChangeListener)this.focusListener);
        }
    }
    
    private void end() {
        if (this.focusProperty != null) {
            this.focusProperty.removeListener((ChangeListener)this.focusListener);
            this.focusProperty = null;
        }
        this.handle.getCellsViewSkin().getVBar().valueProperty().removeListener(this.endEditionListener);
        this.handle.getCellsViewSkin().getHBar().valueProperty().removeListener(this.endEditionListener);
        this.handle.getGridView().removeEventFilter(KeyEvent.KEY_PRESSED, (EventHandler)this.enterKeyPressed);
        this.modelCell = null;
        this.viewCell = null;
    }
    
    private BooleanExpression getFocusProperty(final Control control) {
        if (control instanceof TextArea) {
            Node n;
            return (BooleanExpression)Bindings.createBooleanBinding(() -> {
                if (this.handle.getView().getScene() == null) {
                    return false;
                }
                else {
                    n = this.handle.getView().getScene().getFocusOwner();
                    while (n != null) {
                        if (n == control) {
                            return true;
                        }
                        else {
                            n = (Node)n.getParent();
                        }
                    }
                    return false;
                }
            }, new Observable[] { this.handle.getView().getScene().focusOwnerProperty() });
        }
        return (BooleanExpression)control.focusedProperty();
    }
}
