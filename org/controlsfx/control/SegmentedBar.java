// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import javafx.geometry.Pos;
import javafx.scene.control.OverrunStyle;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.StackPane;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import javafx.scene.control.Control;
import javafx.css.StyleableProperty;
import javafx.css.StyleConverter;
import com.sun.javafx.css.converters.EnumConverter;
import javafx.css.Styleable;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import java.util.stream.Collectors;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;
import impl.org.controlsfx.skin.SegmentedBarSkin;
import javafx.scene.control.Skin;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.css.CssMetaData;
import javafx.css.StyleableObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ListProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.util.Callback;
import javafx.beans.property.ObjectProperty;
import javafx.css.PseudoClass;

public class SegmentedBar<T extends Segment> extends ControlsFXControl
{
    private static final String DEFAULT_STYLE = "segmented-bar";
    private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE;
    private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE;
    private final ObjectProperty<Callback<T, Node>> infoNodeFactory;
    private ObjectProperty<Orientation> orientation;
    private final ObjectProperty<Callback<T, Node>> segmentViewFactory;
    private final ListProperty<T> segments;
    private final ReadOnlyDoubleWrapper total;
    private final InvalidationListener sumListener;
    private final WeakInvalidationListener weakSumListener;
    
    public SegmentedBar() {
        this.infoNodeFactory = (ObjectProperty<Callback<T, Node>>)new SimpleObjectProperty((Object)this, "infoNodeFactory");
        this.orientation = (ObjectProperty<Orientation>)new StyleableObjectProperty<Orientation>((Orientation)null) {
            protected void invalidated() {
                final boolean vertical = this.get() == Orientation.VERTICAL;
                SegmentedBar.this.pseudoClassStateChanged(SegmentedBar.VERTICAL_PSEUDOCLASS_STATE, vertical);
                SegmentedBar.this.pseudoClassStateChanged(SegmentedBar.HORIZONTAL_PSEUDOCLASS_STATE, !vertical);
            }
            
            public CssMetaData<SegmentedBar, Orientation> getCssMetaData() {
                return StyleableProperties.ORIENTATION;
            }
            
            public Object getBean() {
                return SegmentedBar.this;
            }
            
            public String getName() {
                return "orientation";
            }
        };
        this.segmentViewFactory = (ObjectProperty<Callback<T, Node>>)new SimpleObjectProperty((Object)this, "segmentViewFactory");
        this.segments = (ListProperty<T>)new SimpleListProperty((Object)this, "segments", FXCollections.observableArrayList());
        this.total = new ReadOnlyDoubleWrapper((Object)this, "total");
        this.sumListener = (it -> this.total.set((double)this.segments.stream().collect(Collectors.summingDouble(segment -> segment.getValue()))));
        this.weakSumListener = new WeakInvalidationListener(this.sumListener);
        this.segments.addListener(it -> this.listenToValues());
        this.listenToValues();
        this.getStyleClass().add((Object)"segmented-bar");
        this.setSegmentViewFactory((Callback<T, Node>)(segment -> new SegmentView((T)segment)));
        this.setInfoNodeFactory((Callback<T, Node>)(segment -> {
            final Label label = new Label("Value: " + segment.getValue());
            label.setPadding(new Insets(4.0));
            return label;
        }));
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new SegmentedBarSkin((SegmentedBar<Segment>)this);
    }
    
    public String getUserAgentStylesheet() {
        return this.getUserAgentStylesheet(SegmentedBar.class, "segmentedbar.css");
    }
    
    public final ObjectProperty<Callback<T, Node>> infoNodeFactoryProperty() {
        return this.infoNodeFactory;
    }
    
    public final Callback<T, Node> getInfoNodeFactory() {
        return (Callback<T, Node>)this.infoNodeFactory.get();
    }
    
    public void setInfoNodeFactory(final Callback<T, Node> factory) {
        this.infoNodeFactory.set((Object)factory);
    }
    
    public final void setOrientation(final Orientation value) {
        this.orientationProperty().set((Object)value);
    }
    
    public final Orientation getOrientation() {
        return (Orientation)((this.orientation == null) ? Orientation.HORIZONTAL : this.orientation.get());
    }
    
    public final ObjectProperty<Orientation> orientationProperty() {
        return this.orientation;
    }
    
    public final ObjectProperty<Callback<T, Node>> segmentViewFactoryProperty() {
        return this.segmentViewFactory;
    }
    
    public final Callback<T, Node> getSegmentViewFactory() {
        return (Callback<T, Node>)this.segmentViewFactory.get();
    }
    
    public final void setSegmentViewFactory(final Callback<T, Node> factory) {
        this.segmentViewFactory.set((Object)factory);
    }
    
    public final ListProperty<T> segmentsProperty() {
        return this.segments;
    }
    
    public final ObservableList<T> getSegments() {
        return (ObservableList<T>)this.segments.get();
    }
    
    public void setSegments(final ObservableList<T> segments) {
        this.segments.set((Object)segments);
    }
    
    public final ReadOnlyDoubleProperty totalProperty() {
        return this.total.getReadOnlyProperty();
    }
    
    public final double getTotal() {
        return this.total.get();
    }
    
    private void listenToValues() {
        ((ObservableList)this.segments.get()).addListener((InvalidationListener)this.weakSumListener);
        this.getSegments().forEach(segment -> {
            segment.valueProperty().removeListener((InvalidationListener)this.weakSumListener);
            segment.valueProperty().addListener((InvalidationListener)this.weakSumListener);
        });
    }
    
    static {
        VERTICAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("vertical");
        HORIZONTAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("horizontal");
    }
    
    public static class Segment
    {
        private final StringProperty text;
        private final DoubleProperty value;
        
        public Segment(final double value) {
            this.text = (StringProperty)new SimpleStringProperty((Object)this, "text");
            this.value = (DoubleProperty)new SimpleDoubleProperty((Object)this, "value") {
                public void set(final double newValue) {
                    if (newValue < 0.0) {
                        throw new IllegalArgumentException("segment value must be >= 0 but was " + newValue);
                    }
                    super.set(newValue);
                }
            };
            if (value < 0.0) {
                throw new IllegalArgumentException("value must be larger or equal to 0 but was " + value);
            }
            this.setValue(value);
        }
        
        public Segment(final double value, final String text) {
            this(value);
            this.setText(text);
        }
        
        public final StringProperty textProperty() {
            return this.text;
        }
        
        public final void setText(final String text) {
            this.text.set((Object)text);
        }
        
        public final String getText() {
            return (String)this.text.get();
        }
        
        public final DoubleProperty valueProperty() {
            return this.value;
        }
        
        public final void setValue(final double value) {
            this.value.set(value);
        }
        
        public final double getValue() {
            return this.value.get();
        }
    }
    
    private static class StyleableProperties
    {
        private static final CssMetaData<SegmentedBar, Orientation> ORIENTATION;
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        
        static {
            ORIENTATION = new CssMetaData<SegmentedBar, Orientation>("-fx-orientation", (StyleConverter)new EnumConverter((Class)Orientation.class), Orientation.VERTICAL) {
                public Orientation getInitialValue(final SegmentedBar node) {
                    return node.getOrientation();
                }
                
                public boolean isSettable(final SegmentedBar n) {
                    return n.orientation == null || !n.orientation.isBound();
                }
                
                public StyleableProperty<Orientation> getStyleableProperty(final SegmentedBar n) {
                    return (StyleableProperty<Orientation>)n.orientationProperty();
                }
            };
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
            styleables.add((CssMetaData<? extends Styleable, ?>)StyleableProperties.ORIENTATION);
            STYLEABLES = Collections.unmodifiableList((List<? extends CssMetaData<? extends Styleable, ?>>)styleables);
        }
    }
    
    public class SegmentView extends StackPane
    {
        private Label label;
        
        public SegmentView(final T segment) {
            this.getStyleClass().add((Object)"segment-view");
            this.label = new Label();
            this.label.textProperty().bind((ObservableValue)segment.textProperty());
            this.label.setTextOverrun(OverrunStyle.CLIP);
            StackPane.setAlignment((Node)this.label, Pos.CENTER_LEFT);
            this.getChildren().add((Object)this.label);
        }
        
        protected void layoutChildren() {
            super.layoutChildren();
            this.label.setVisible(this.label.prefWidth(-1.0) < this.getWidth() - this.getPadding().getLeft() - this.getPadding().getRight());
        }
    }
}
