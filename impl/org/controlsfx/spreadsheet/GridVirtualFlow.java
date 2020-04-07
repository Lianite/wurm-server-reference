// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.spreadsheet;

import com.sun.javafx.scene.control.skin.VirtualScrollBar;
import javafx.beans.Observable;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import javafx.collections.ObservableList;
import javafx.scene.control.TableRow;
import java.util.Collections;
import javafx.scene.Group;
import javafx.scene.layout.Region;
import javafx.scene.control.ScrollBar;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.binding.When;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.MouseEvent;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.scene.transform.Scale;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import java.util.List;
import java.util.ArrayList;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import java.util.Comparator;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.scene.control.IndexedCell;

final class GridVirtualFlow<T extends IndexedCell<?>> extends VirtualFlow<T>
{
    private static final Comparator<GridRow> ROWCMP;
    private SpreadsheetView spreadSheetView;
    private final GridViewSkin gridViewSkin;
    private final ArrayList<T> myFixedCells;
    public final List<Node> sheetChildren;
    private StackPane corner;
    private Scale scale;
    private final ChangeListener<Number> hBarValueChangeListener;
    
    public GridVirtualFlow(final GridViewSkin gridViewSkin) {
        this.myFixedCells = new ArrayList<T>();
        this.hBarValueChangeListener = (ChangeListener<Number>)new ChangeListener<Number>() {
            public void changed(final ObservableValue<? extends Number> ov, final Number t, final Number t1) {
                GridVirtualFlow.this.gridViewSkin.hBarValue.clear();
            }
        };
        this.gridViewSkin = gridViewSkin;
        final ChangeListener<Number> listenerY = (ChangeListener<Number>)new ChangeListener<Number>() {
            public void changed(final ObservableValue<? extends Number> ov, final Number t, final Number t1) {
                GridVirtualFlow.this.layoutTotal();
            }
        };
        this.getVbar().valueProperty().addListener((ChangeListener)listenerY);
        this.getHbar().valueProperty().addListener((ChangeListener)this.hBarValueChangeListener);
        this.widthProperty().addListener((ChangeListener)this.hBarValueChangeListener);
        this.sheetChildren = this.findSheetChildren();
        this.findCorner();
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getTarget().getClass() == GridRow.class) {
                this.spreadSheetView.getSelectionModel().clearSelection();
            }
        });
    }
    
    public void init(final SpreadsheetView spv) {
        this.getHbar().maxProperty().addListener((ChangeListener)new ChangeListener<Number>() {
            public void changed(final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) {
                GridVirtualFlow.access$000(GridVirtualFlow.this).setBlockIncrement(GridVirtualFlow.this.getWidth());
                GridVirtualFlow.access$100(GridVirtualFlow.this).setUnitIncrement(newValue.doubleValue() / 20.0);
            }
        });
        (this.scale = new Scale(1.0 / spv.getZoomFactor(), 1.0 / spv.getZoomFactor())).setPivotX(this.getHbar().getWidth() / 2.0);
        this.getHbar().getTransforms().add((Object)this.scale);
        this.getVbar().getTransforms().add((Object)this.scale);
        this.corner.getTransforms().add((Object)this.scale);
        this.spreadSheetView = spv;
        this.spreadSheetView.zoomFactorProperty().addListener((observable, oldValue, newValue) -> {
            this.scale.setX(1.0 / newValue.doubleValue());
            this.scale.setY(1.0 / newValue.doubleValue());
        });
        final Rectangle rec = new Rectangle();
        rec.widthProperty().bind((ObservableValue)this.widthProperty().subtract((ObservableNumberValue)new When((ObservableBooleanValue)this.getVbar().visibleProperty()).then((ObservableNumberValue)this.getVbar().widthProperty()).otherwise(0)));
        rec.heightProperty().bind((ObservableValue)this.heightProperty().subtract((ObservableNumberValue)new When((ObservableBooleanValue)this.getHbar().visibleProperty()).then((ObservableNumberValue)this.getHbar().heightProperty()).otherwise(0)));
        this.gridViewSkin.rectangleSelection.setClip((Node)rec);
        this.getChildren().add((Object)this.gridViewSkin.rectangleSelection);
        spv.getFixedRows().addListener(observable -> {
            final List<T> toRemove = new ArrayList<T>();
            for (final T cell : this.myFixedCells) {
                if (!spv.getFixedRows().contains((Object)this.spreadSheetView.getFilteredSourceIndex(cell.getIndex()))) {
                    cell.setManaged(false);
                    cell.setVisible(false);
                    toRemove.add(cell);
                }
            }
            this.myFixedCells.removeAll(toRemove);
        });
    }
    
    public void show(final int index) {
        super.show(index);
        this.layoutTotal();
        this.layoutFixedRows();
    }
    
    public void scrollTo(int index) {
        if (!this.getCells().isEmpty() && !VerticalHeader.isFixedRowEmpty(this.spreadSheetView)) {
            for (double offset = this.gridViewSkin.getFixedRowHeight(); offset >= 0.0 && index > 0; --index, offset -= this.gridViewSkin.getRowHeight(index)) {}
        }
        super.scrollTo(index);
        this.layoutTotal();
        this.layoutFixedRows();
    }
    
    public double adjustPixels(final double delta) {
        final double returnValue = super.adjustPixels(delta);
        this.layoutTotal();
        this.layoutFixedRows();
        return returnValue;
    }
    
    List<T> getFixedCells() {
        return this.myFixedCells;
    }
    
    GridRow getTopRow() {
        if (!this.sheetChildren.isEmpty()) {
            int i;
            for (i = this.sheetChildren.size() - 1; ((GridRow)this.sheetChildren.get(i)).getChildrenUnmodifiable().isEmpty() && i > 0; --i) {}
            return (GridRow)this.sheetChildren.get(i);
        }
        return null;
    }
    
    protected void layoutChildren() {
        if (this.spreadSheetView != null) {
            this.sortRows();
            super.layoutChildren();
            this.layoutTotal();
            this.layoutFixedRows();
            if (this.getVbar().getVisibleAmount() == 0.0 && this.getVbar().isVisible() && this.getCells().size() != this.getCellCount()) {
                this.getVbar().setMax(1.0);
                this.getVbar().setVisibleAmount((double)(this.getCells().size() / this.getCellCount()));
            }
        }
        final Pos pos = Pos.TOP_LEFT;
        final double width = this.getWidth();
        final double height = this.getHeight();
        final double top = this.getInsets().getTop();
        final double right = this.getInsets().getRight();
        final double left = this.getInsets().getLeft();
        final double bottom = this.getInsets().getBottom();
        final double scaleX = this.scale.getX();
        final double shift = 1.0 - scaleX;
        final double contentWidth = width / scaleX - left - right - this.getVbar().getWidth();
        final double contentHeight = height / scaleX - top - bottom - this.getHbar().getHeight();
        this.layoutInArea((Node)this.getHbar(), 0.0 - shift * 10.0, height - this.getHbar().getHeight() * scaleX, contentWidth, contentHeight, 0.0, (Insets)null, pos.getHpos(), pos.getVpos());
        this.layoutInArea((Node)this.getVbar(), width - this.getVbar().getWidth() + shift, 0.0, contentWidth, contentHeight, 0.0, (Insets)null, pos.getHpos(), pos.getVpos());
        if (this.corner != null) {
            this.layoutInArea((Node)this.corner, width - this.getVbar().getWidth() + shift, this.getHeight() - this.getHbar().getHeight() * scaleX, this.corner.getWidth(), this.corner.getHeight(), 0.0, (Insets)null, pos.getHpos(), pos.getVpos());
        }
    }
    
    protected void layoutTotal() {
        this.sortRows();
        this.removeDeportedCells();
        if (this.getCells().isEmpty()) {
            this.reconfigureCells();
        }
        for (final GridRow cell : this.getCells()) {
            if (cell != null && cell.getIndex() >= 0 && (!this.gridViewSkin.hBarValue.get(cell.getIndex()) || this.gridViewSkin.rowToLayout.get(cell.getIndex()))) {
                cell.requestLayout();
            }
        }
    }
    
    private void removeDeportedCells() {
        final ArrayList<GridRow> rowToRemove = new ArrayList<GridRow>();
        for (final Map.Entry<GridRow, Set<CellView>> entry : this.gridViewSkin.deportedCells.entrySet()) {
            final ArrayList<CellView> toRemove = new ArrayList<CellView>();
            for (final CellView cell : entry.getValue()) {
                if (!cell.isEditing() && !this.getCells().contains(cell.getTableRow())) {
                    entry.getKey().removeCell(cell);
                    toRemove.add(cell);
                }
            }
            entry.getValue().removeAll(toRemove);
            if (entry.getValue().isEmpty()) {
                rowToRemove.add(entry.getKey());
            }
        }
        for (final GridRow row : rowToRemove) {
            this.gridViewSkin.deportedCells.remove(row);
        }
    }
    
    protected ScrollBar getVerticalBar() {
        return (ScrollBar)this.getVbar();
    }
    
    protected ScrollBar getHorizontalBar() {
        return (ScrollBar)this.getHbar();
    }
    
    protected List<T> getCells() {
        return (List<T>)super.getCells();
    }
    
    private List<Node> findSheetChildren() {
        if (!this.getChildren().isEmpty() && this.getChildren().get(0) instanceof Region) {
            final Region region = (Region)this.getChildren().get(0);
            if (!region.getChildrenUnmodifiable().isEmpty() && region.getChildrenUnmodifiable().get(0) instanceof Group) {
                return (List<Node>)((Group)region.getChildrenUnmodifiable().get(0)).getChildren();
            }
        }
        return new ArrayList<Node>();
    }
    
    private void findCorner() {
        if (!this.getChildren().isEmpty()) {
            for (final Node node : this.getChildren()) {
                if (node instanceof StackPane) {
                    this.corner = (StackPane)node;
                }
            }
        }
    }
    
    private void layoutFixedRows() {
        if (!VerticalHeader.isFixedRowEmpty(this.spreadSheetView) && this.getFirstVisibleCellWithinViewPort() != null) {
            this.sortRows();
            T row = null;
        Label_0349:
            for (int i = this.spreadSheetView.getFixedRows().size() - 1; i >= 0; --i) {
                Integer fixedRowIndex = (Integer)this.spreadSheetView.getFixedRows().get(i);
                if (!this.spreadSheetView.isRowHidden(i)) {
                    fixedRowIndex = this.spreadSheetView.getFilteredRow(fixedRowIndex);
                    final T lastCell = (T)this.getLastVisibleCellWithinViewPort();
                    if (lastCell != null && fixedRowIndex > lastCell.getIndex()) {
                        if (row != null) {
                            row.setVisible(false);
                            row.setManaged(false);
                            this.sheetChildren.remove(row);
                        }
                    }
                    else {
                        for (final T virtualFlowCells : this.getCells()) {
                            if (virtualFlowCells.getIndex() > fixedRowIndex) {
                                break;
                            }
                            if (virtualFlowCells.getIndex() == fixedRowIndex) {
                                row = this.containsRows(fixedRowIndex);
                                if (row != null) {
                                    row.setVisible(false);
                                    row.setManaged(false);
                                    this.sheetChildren.remove(row);
                                }
                                virtualFlowCells.toFront();
                                continue Label_0349;
                            }
                        }
                        row = this.containsRows(fixedRowIndex);
                        if (row == null) {
                            row = (T)this.getCreateCell().call((Object)this);
                            row.getProperties().put((Object)"newcell", (Object)null);
                            this.setCellIndex((IndexedCell)row, (int)fixedRowIndex);
                            this.resizeCellSize((IndexedCell)row);
                            this.myFixedCells.add(row);
                        }
                        if (!this.sheetChildren.contains(row)) {
                            this.sheetChildren.add((Node)row);
                        }
                        row.setManaged(true);
                        row.setVisible(true);
                        row.toFront();
                        row.requestLayout();
                    }
                }
            }
        }
    }
    
    private T containsRows(final int i) {
        for (final T cell : this.myFixedCells) {
            if (cell.getIndex() == i) {
                return cell;
            }
        }
        return null;
    }
    
    private void sortRows() {
        final List<GridRow> temp = this.getCells();
        final List<GridRow> tset = new ArrayList<GridRow>(temp);
        Collections.sort(tset, GridVirtualFlow.ROWCMP);
        for (final TableRow<ObservableList<SpreadsheetCell>> r : tset) {
            r.toFront();
        }
    }
    
    static /* synthetic */ VirtualScrollBar access$000(final GridVirtualFlow x0) {
        return x0.getHbar();
    }
    
    static /* synthetic */ VirtualScrollBar access$100(final GridVirtualFlow x0) {
        return x0.getHbar();
    }
    
    static {
        ROWCMP = new Comparator<GridRow>() {
            @Override
            public int compare(final GridRow firstRow, final GridRow secondRow) {
                return secondRow.getIndex() - firstRow.getIndex();
            }
        };
    }
}
