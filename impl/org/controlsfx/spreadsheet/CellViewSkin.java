// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.spreadsheet;

import org.controlsfx.control.spreadsheet.Filter;
import javafx.geometry.VPos;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.beans.value.WeakChangeListener;
import javafx.beans.value.ChangeListener;
import javafx.event.WeakEventHandler;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.Region;
import javafx.event.EventType;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import javafx.collections.ObservableList;
import com.sun.javafx.scene.control.skin.TableCellSkin;

public class CellViewSkin extends TableCellSkin<ObservableList<SpreadsheetCell>, SpreadsheetCell>
{
    public static final EventType FILTER_EVENT_TYPE;
    private static final String TOP_LEFT_CLASS = "top-left";
    private static final String TOP_RIGHT_CLASS = "top-right";
    private static final String BOTTOM_RIGHT_CLASS = "bottom-right";
    private static final String BOTTOM_LEFT_CLASS = "bottom-left";
    private static final int TRIANGLE_SIZE = 8;
    private Region topLeftRegion;
    private Region topRightRegion;
    private Region bottomRightRegion;
    private Region bottomLeftRegion;
    private MenuButton filterButton;
    private final EventHandler<Event> triangleEventHandler;
    private final WeakEventHandler weakTriangleEventHandler;
    private final ChangeListener<SpreadsheetCell> itemChangeListener;
    private final WeakChangeListener<SpreadsheetCell> weakItemChangeListener;
    private final ChangeListener<TableColumn> columnChangeListener;
    private final WeakChangeListener<TableColumn> weakColumnChangeListener;
    
    public CellViewSkin(final CellView tableCell) {
        super((TableCell)tableCell);
        this.topLeftRegion = null;
        this.topRightRegion = null;
        this.bottomRightRegion = null;
        this.bottomLeftRegion = null;
        this.filterButton = null;
        this.triangleEventHandler = (EventHandler<Event>)new EventHandler<Event>() {
            public void handle(final Event event) {
                ((TableCell)CellViewSkin.this.getSkinnable()).requestLayout();
            }
        };
        this.weakTriangleEventHandler = new WeakEventHandler((EventHandler)this.triangleEventHandler);
        this.itemChangeListener = (ChangeListener<SpreadsheetCell>)new ChangeListener<SpreadsheetCell>() {
            public void changed(final ObservableValue<? extends SpreadsheetCell> arg0, final SpreadsheetCell oldCell, final SpreadsheetCell newCell) {
                if (oldCell != null) {
                    oldCell.removeEventHandler((EventType<Event>)SpreadsheetCell.CORNER_EVENT_TYPE, (EventHandler<Event>)CellViewSkin.this.weakTriangleEventHandler);
                }
                if (newCell != null) {
                    newCell.addEventHandler((EventType<Event>)SpreadsheetCell.CORNER_EVENT_TYPE, (EventHandler<Event>)CellViewSkin.this.weakTriangleEventHandler);
                }
                if (((TableCell)CellViewSkin.this.getSkinnable()).getItem() != null) {
                    CellViewSkin.this.layoutTriangle();
                }
            }
        };
        this.weakItemChangeListener = (WeakChangeListener<SpreadsheetCell>)new WeakChangeListener((ChangeListener)this.itemChangeListener);
        this.columnChangeListener = (ChangeListener<TableColumn>)new ChangeListener<TableColumn>() {
            public void changed(final ObservableValue<? extends TableColumn> arg0, final TableColumn oldCell, final TableColumn newCell) {
                if (oldCell != null) {
                    oldCell.removeEventHandler(CellViewSkin.FILTER_EVENT_TYPE, (EventHandler)CellViewSkin.this.weakTriangleEventHandler);
                }
                if (newCell != null) {
                    newCell.addEventHandler(CellViewSkin.FILTER_EVENT_TYPE, (EventHandler)CellViewSkin.this.weakTriangleEventHandler);
                }
            }
        };
        this.weakColumnChangeListener = (WeakChangeListener<TableColumn>)new WeakChangeListener((ChangeListener)this.columnChangeListener);
        tableCell.itemProperty().addListener((ChangeListener)this.weakItemChangeListener);
        tableCell.tableColumnProperty().addListener((ChangeListener)this.weakColumnChangeListener);
        tableCell.getTableColumn().addEventHandler(CellViewSkin.FILTER_EVENT_TYPE, (EventHandler)this.weakTriangleEventHandler);
        if (tableCell.getItem() != null) {
            ((SpreadsheetCell)tableCell.getItem()).addEventHandler((EventType<Event>)SpreadsheetCell.CORNER_EVENT_TYPE, (EventHandler<Event>)this.weakTriangleEventHandler);
        }
    }
    
    protected double computePrefHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        final Node graphic = ((TableCell)this.getSkinnable()).getGraphic();
        if (graphic != null && graphic instanceof ImageView) {
            final ImageView view = (ImageView)graphic;
            if (view.getImage() != null) {
                return view.getImage().getHeight();
            }
        }
        return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }
    
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        super.layoutChildren(x, y, w, h);
        if (((TableCell)this.getSkinnable()).getItem() != null) {
            this.layoutTriangle();
            this.handleFilter(x, y, w, h);
        }
    }
    
    private void layoutTriangle() {
        final SpreadsheetCell cell = (SpreadsheetCell)((TableCell)this.getSkinnable()).getItem();
        this.handleTopLeft(cell);
        this.handleTopRight(cell);
        this.handleBottomLeft(cell);
        this.handleBottomRight(cell);
        ((TableCell)this.getSkinnable()).requestLayout();
    }
    
    private void handleFilter(final double x, final double y, final double w, final double h) {
        final Filter filter = ((CellView)this.getSkinnable()).getFilter();
        if (filter != null) {
            this.removeMenuButton();
            this.filterButton = filter.getMenuButton();
            if (!this.getChildren().contains((Object)this.filterButton)) {
                this.getChildren().add((Object)this.filterButton);
            }
            this.layoutInArea((Node)this.filterButton, x, y, w, h, 0.0, HPos.RIGHT, VPos.BOTTOM);
        }
        else if (this.filterButton != null) {
            this.removeMenuButton();
        }
    }
    
    private void removeMenuButton() {
        if (this.filterButton != null && this.getChildren().contains((Object)this.filterButton)) {
            this.getChildren().remove((Object)this.filterButton);
            this.filterButton = null;
        }
    }
    
    private void handleTopLeft(final SpreadsheetCell cell) {
        if (cell.isCornerActivated(SpreadsheetCell.CornerPosition.TOP_LEFT)) {
            if (this.topLeftRegion == null) {
                this.topLeftRegion = getRegion(SpreadsheetCell.CornerPosition.TOP_LEFT);
            }
            if (!this.getChildren().contains((Object)this.topLeftRegion)) {
                this.getChildren().add((Object)this.topLeftRegion);
            }
            this.topLeftRegion.relocate(0.0, 0.0);
        }
        else if (this.topLeftRegion != null) {
            this.getChildren().remove((Object)this.topLeftRegion);
            this.topLeftRegion = null;
        }
    }
    
    private void handleTopRight(final SpreadsheetCell cell) {
        if (cell.isCornerActivated(SpreadsheetCell.CornerPosition.TOP_RIGHT)) {
            if (this.topRightRegion == null) {
                this.topRightRegion = getRegion(SpreadsheetCell.CornerPosition.TOP_RIGHT);
            }
            if (!this.getChildren().contains((Object)this.topRightRegion)) {
                this.getChildren().add((Object)this.topRightRegion);
            }
            this.topRightRegion.relocate(((TableCell)this.getSkinnable()).getWidth() - 8.0, 0.0);
        }
        else if (this.topRightRegion != null) {
            this.getChildren().remove((Object)this.topRightRegion);
            this.topRightRegion = null;
        }
    }
    
    private void handleBottomRight(final SpreadsheetCell cell) {
        if (cell.isCornerActivated(SpreadsheetCell.CornerPosition.BOTTOM_RIGHT)) {
            if (this.bottomRightRegion == null) {
                this.bottomRightRegion = getRegion(SpreadsheetCell.CornerPosition.BOTTOM_RIGHT);
            }
            if (!this.getChildren().contains((Object)this.bottomRightRegion)) {
                this.getChildren().add((Object)this.bottomRightRegion);
            }
            this.bottomRightRegion.relocate(((TableCell)this.getSkinnable()).getWidth() - 8.0, ((TableCell)this.getSkinnable()).getHeight() - 8.0);
        }
        else if (this.bottomRightRegion != null) {
            this.getChildren().remove((Object)this.bottomRightRegion);
            this.bottomRightRegion = null;
        }
    }
    
    private void handleBottomLeft(final SpreadsheetCell cell) {
        if (cell.isCornerActivated(SpreadsheetCell.CornerPosition.BOTTOM_LEFT)) {
            if (this.bottomLeftRegion == null) {
                this.bottomLeftRegion = getRegion(SpreadsheetCell.CornerPosition.BOTTOM_LEFT);
            }
            if (!this.getChildren().contains((Object)this.bottomLeftRegion)) {
                this.getChildren().add((Object)this.bottomLeftRegion);
            }
            this.bottomLeftRegion.relocate(0.0, ((TableCell)this.getSkinnable()).getHeight() - 8.0);
        }
        else if (this.bottomLeftRegion != null) {
            this.getChildren().remove((Object)this.bottomLeftRegion);
            this.bottomLeftRegion = null;
        }
    }
    
    private static Region getRegion(final SpreadsheetCell.CornerPosition position) {
        final Region region = new Region();
        region.resize(8.0, 8.0);
        region.getStyleClass().add((Object)"cell-corner");
        switch (position) {
            case TOP_LEFT: {
                region.getStyleClass().add((Object)"top-left");
                break;
            }
            case TOP_RIGHT: {
                region.getStyleClass().add((Object)"top-right");
                break;
            }
            case BOTTOM_RIGHT: {
                region.getStyleClass().add((Object)"bottom-right");
                break;
            }
            case BOTTOM_LEFT: {
                region.getStyleClass().add((Object)"bottom-left");
                break;
            }
        }
        return region;
    }
    
    static {
        FILTER_EVENT_TYPE = new EventType("FilterEventType");
    }
}
