// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import javafx.scene.control.Control;
import javafx.css.StyleableProperty;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.StyleableDoubleProperty;
import impl.org.controlsfx.skin.GridViewSkin;
import javafx.scene.control.Skin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.DoubleProperty;

public class GridView<T> extends ControlsFXControl
{
    private DoubleProperty horizontalCellSpacing;
    private DoubleProperty verticalCellSpacing;
    private DoubleProperty cellWidth;
    private DoubleProperty cellHeight;
    private ObjectProperty<Callback<GridView<T>, GridCell<T>>> cellFactory;
    private ObjectProperty<ObservableList<T>> items;
    private static final String DEFAULT_STYLE_CLASS = "grid-view";
    
    public GridView() {
        this(FXCollections.observableArrayList());
    }
    
    public GridView(final ObservableList<T> items) {
        this.getStyleClass().add((Object)"grid-view");
        this.setItems(items);
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new GridViewSkin((GridView<Object>)this);
    }
    
    public String getUserAgentStylesheet() {
        return this.getUserAgentStylesheet(GridView.class, "gridview.css");
    }
    
    public final DoubleProperty horizontalCellSpacingProperty() {
        if (this.horizontalCellSpacing == null) {
            this.horizontalCellSpacing = (DoubleProperty)new StyleableDoubleProperty(12.0) {
                public CssMetaData<GridView<?>, Number> getCssMetaData() {
                    return StyleableProperties.HORIZONTAL_CELL_SPACING;
                }
                
                public Object getBean() {
                    return GridView.this;
                }
                
                public String getName() {
                    return "horizontalCellSpacing";
                }
            };
        }
        return this.horizontalCellSpacing;
    }
    
    public final void setHorizontalCellSpacing(final double value) {
        this.horizontalCellSpacingProperty().set(value);
    }
    
    public final double getHorizontalCellSpacing() {
        return (this.horizontalCellSpacing == null) ? 12.0 : this.horizontalCellSpacing.get();
    }
    
    public final DoubleProperty verticalCellSpacingProperty() {
        if (this.verticalCellSpacing == null) {
            this.verticalCellSpacing = (DoubleProperty)new StyleableDoubleProperty(12.0) {
                public CssMetaData<GridView<?>, Number> getCssMetaData() {
                    return StyleableProperties.VERTICAL_CELL_SPACING;
                }
                
                public Object getBean() {
                    return GridView.this;
                }
                
                public String getName() {
                    return "verticalCellSpacing";
                }
            };
        }
        return this.verticalCellSpacing;
    }
    
    public final void setVerticalCellSpacing(final double value) {
        this.verticalCellSpacingProperty().set(value);
    }
    
    public final double getVerticalCellSpacing() {
        return (this.verticalCellSpacing == null) ? 12.0 : this.verticalCellSpacing.get();
    }
    
    public final DoubleProperty cellWidthProperty() {
        if (this.cellWidth == null) {
            this.cellWidth = (DoubleProperty)new StyleableDoubleProperty(64.0) {
                public CssMetaData<GridView<?>, Number> getCssMetaData() {
                    return StyleableProperties.CELL_WIDTH;
                }
                
                public Object getBean() {
                    return GridView.this;
                }
                
                public String getName() {
                    return "cellWidth";
                }
            };
        }
        return this.cellWidth;
    }
    
    public final void setCellWidth(final double value) {
        this.cellWidthProperty().set(value);
    }
    
    public final double getCellWidth() {
        return (this.cellWidth == null) ? 64.0 : this.cellWidth.get();
    }
    
    public final DoubleProperty cellHeightProperty() {
        if (this.cellHeight == null) {
            this.cellHeight = (DoubleProperty)new StyleableDoubleProperty(64.0) {
                public CssMetaData<GridView<?>, Number> getCssMetaData() {
                    return StyleableProperties.CELL_HEIGHT;
                }
                
                public Object getBean() {
                    return GridView.this;
                }
                
                public String getName() {
                    return "cellHeight";
                }
            };
        }
        return this.cellHeight;
    }
    
    public final void setCellHeight(final double value) {
        this.cellHeightProperty().set(value);
    }
    
    public final double getCellHeight() {
        return (this.cellHeight == null) ? 64.0 : this.cellHeight.get();
    }
    
    public final ObjectProperty<Callback<GridView<T>, GridCell<T>>> cellFactoryProperty() {
        if (this.cellFactory == null) {
            this.cellFactory = (ObjectProperty<Callback<GridView<T>, GridCell<T>>>)new SimpleObjectProperty((Object)this, "cellFactory");
        }
        return this.cellFactory;
    }
    
    public final void setCellFactory(final Callback<GridView<T>, GridCell<T>> value) {
        this.cellFactoryProperty().set((Object)value);
    }
    
    public final Callback<GridView<T>, GridCell<T>> getCellFactory() {
        return (Callback<GridView<T>, GridCell<T>>)((this.cellFactory == null) ? null : ((Callback)this.cellFactory.get()));
    }
    
    public final ObjectProperty<ObservableList<T>> itemsProperty() {
        if (this.items == null) {
            this.items = (ObjectProperty<ObservableList<T>>)new SimpleObjectProperty((Object)this, "items");
        }
        return this.items;
    }
    
    public final void setItems(final ObservableList<T> value) {
        this.itemsProperty().set((Object)value);
    }
    
    public final ObservableList<T> getItems() {
        return (ObservableList<T>)((this.items == null) ? null : ((ObservableList)this.items.get()));
    }
    
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }
    
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }
    
    private static class StyleableProperties
    {
        private static final CssMetaData<GridView<?>, Number> HORIZONTAL_CELL_SPACING;
        private static final CssMetaData<GridView<?>, Number> VERTICAL_CELL_SPACING;
        private static final CssMetaData<GridView<?>, Number> CELL_WIDTH;
        private static final CssMetaData<GridView<?>, Number> CELL_HEIGHT;
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        
        static {
            HORIZONTAL_CELL_SPACING = new CssMetaData<GridView<?>, Number>("-fx-horizontal-cell-spacing", StyleConverter.getSizeConverter(), 12.0) {
                public Double getInitialValue(final GridView<?> node) {
                    return node.getHorizontalCellSpacing();
                }
                
                public boolean isSettable(final GridView<?> n) {
                    return ((GridView<Object>)n).horizontalCellSpacing == null || !((GridView<Object>)n).horizontalCellSpacing.isBound();
                }
                
                public StyleableProperty<Number> getStyleableProperty(final GridView<?> n) {
                    return (StyleableProperty<Number>)n.horizontalCellSpacingProperty();
                }
            };
            VERTICAL_CELL_SPACING = new CssMetaData<GridView<?>, Number>("-fx-vertical-cell-spacing", StyleConverter.getSizeConverter(), 12.0) {
                public Double getInitialValue(final GridView<?> node) {
                    return node.getVerticalCellSpacing();
                }
                
                public boolean isSettable(final GridView<?> n) {
                    return ((GridView<Object>)n).verticalCellSpacing == null || !((GridView<Object>)n).verticalCellSpacing.isBound();
                }
                
                public StyleableProperty<Number> getStyleableProperty(final GridView<?> n) {
                    return (StyleableProperty<Number>)n.verticalCellSpacingProperty();
                }
            };
            CELL_WIDTH = new CssMetaData<GridView<?>, Number>("-fx-cell-width", StyleConverter.getSizeConverter(), 64.0) {
                public Double getInitialValue(final GridView<?> node) {
                    return node.getCellWidth();
                }
                
                public boolean isSettable(final GridView<?> n) {
                    return ((GridView<Object>)n).cellWidth == null || !((GridView<Object>)n).cellWidth.isBound();
                }
                
                public StyleableProperty<Number> getStyleableProperty(final GridView<?> n) {
                    return (StyleableProperty<Number>)n.cellWidthProperty();
                }
            };
            CELL_HEIGHT = new CssMetaData<GridView<?>, Number>("-fx-cell-height", StyleConverter.getSizeConverter(), 64.0) {
                public Double getInitialValue(final GridView<?> node) {
                    return node.getCellHeight();
                }
                
                public boolean isSettable(final GridView<?> n) {
                    return ((GridView<Object>)n).cellHeight == null || !((GridView<Object>)n).cellHeight.isBound();
                }
                
                public StyleableProperty<Number> getStyleableProperty(final GridView<?> n) {
                    return (StyleableProperty<Number>)n.cellHeightProperty();
                }
            };
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
            styleables.add((CssMetaData<? extends Styleable, ?>)StyleableProperties.HORIZONTAL_CELL_SPACING);
            styleables.add((CssMetaData<? extends Styleable, ?>)StyleableProperties.VERTICAL_CELL_SPACING);
            styleables.add((CssMetaData<? extends Styleable, ?>)StyleableProperties.CELL_WIDTH);
            styleables.add((CssMetaData<? extends Styleable, ?>)StyleableProperties.CELL_HEIGHT);
            STYLEABLES = Collections.unmodifiableList((List<? extends CssMetaData<? extends Styleable, ?>>)styleables);
        }
    }
}
