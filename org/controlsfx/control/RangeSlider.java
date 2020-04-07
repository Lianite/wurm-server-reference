// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import javafx.scene.control.Control;
import com.sun.javafx.css.converters.EnumConverter;
import javafx.css.StyleOrigin;
import com.sun.javafx.css.converters.BooleanConverter;
import javafx.css.StyleableProperty;
import javafx.css.StyleConverter;
import com.sun.javafx.css.converters.SizeConverter;
import java.util.List;
import org.controlsfx.tools.Utils;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableIntegerProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.Styleable;
import javafx.css.CssMetaData;
import javafx.css.StyleableBooleanProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import impl.org.controlsfx.skin.RangeSliderSkin;
import javafx.scene.control.Skin;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.beans.property.IntegerProperty;
import javafx.util.StringConverter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;

public class RangeSlider extends ControlsFXControl
{
    private DoubleProperty lowValue;
    private BooleanProperty lowValueChanging;
    private DoubleProperty highValue;
    private BooleanProperty highValueChanging;
    private final ObjectProperty<StringConverter<Number>> tickLabelFormatter;
    private DoubleProperty max;
    private DoubleProperty min;
    private BooleanProperty snapToTicks;
    private DoubleProperty majorTickUnit;
    private IntegerProperty minorTickCount;
    private DoubleProperty blockIncrement;
    private ObjectProperty<Orientation> orientation;
    private BooleanProperty showTickLabels;
    private BooleanProperty showTickMarks;
    private static final String DEFAULT_STYLE_CLASS = "range-slider";
    private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE;
    private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE;
    
    public RangeSlider() {
        this(0.0, 1.0, 0.25, 0.75);
    }
    
    public RangeSlider(final double min, final double max, final double lowValue, final double highValue) {
        this.lowValue = (DoubleProperty)new SimpleDoubleProperty((Object)this, "lowValue", 0.0) {
            protected void invalidated() {
                RangeSlider.this.adjustLowValues();
            }
        };
        this.highValue = (DoubleProperty)new SimpleDoubleProperty((Object)this, "highValue", 100.0) {
            protected void invalidated() {
                RangeSlider.this.adjustHighValues();
            }
            
            public Object getBean() {
                return RangeSlider.this;
            }
            
            public String getName() {
                return "highValue";
            }
        };
        this.tickLabelFormatter = (ObjectProperty<StringConverter<Number>>)new SimpleObjectProperty();
        this.getStyleClass().setAll((Object[])new String[] { "range-slider" });
        this.setMax(max);
        this.setMin(min);
        this.adjustValues();
        this.setLowValue(lowValue);
        this.setHighValue(highValue);
    }
    
    public String getUserAgentStylesheet() {
        return this.getUserAgentStylesheet(RangeSlider.class, "rangeslider.css");
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new RangeSliderSkin(this);
    }
    
    public final DoubleProperty lowValueProperty() {
        return this.lowValue;
    }
    
    public final void setLowValue(final double d) {
        this.lowValueProperty().set(d);
    }
    
    public final double getLowValue() {
        return (this.lowValue != null) ? this.lowValue.get() : 0.0;
    }
    
    public final BooleanProperty lowValueChangingProperty() {
        if (this.lowValueChanging == null) {
            this.lowValueChanging = (BooleanProperty)new SimpleBooleanProperty((Object)this, "lowValueChanging", false);
        }
        return this.lowValueChanging;
    }
    
    public final void setLowValueChanging(final boolean value) {
        this.lowValueChangingProperty().set(value);
    }
    
    public final boolean isLowValueChanging() {
        return this.lowValueChanging != null && this.lowValueChanging.get();
    }
    
    public final DoubleProperty highValueProperty() {
        return this.highValue;
    }
    
    public final void setHighValue(final double d) {
        if (!this.highValueProperty().isBound()) {
            this.highValueProperty().set(d);
        }
    }
    
    public final double getHighValue() {
        return (this.highValue != null) ? this.highValue.get() : 100.0;
    }
    
    public final BooleanProperty highValueChangingProperty() {
        if (this.highValueChanging == null) {
            this.highValueChanging = (BooleanProperty)new SimpleBooleanProperty((Object)this, "highValueChanging", false);
        }
        return this.highValueChanging;
    }
    
    public final void setHighValueChanging(final boolean value) {
        this.highValueChangingProperty().set(value);
    }
    
    public final boolean isHighValueChanging() {
        return this.highValueChanging != null && this.highValueChanging.get();
    }
    
    public final StringConverter<Number> getLabelFormatter() {
        return (StringConverter<Number>)this.tickLabelFormatter.get();
    }
    
    public final void setLabelFormatter(final StringConverter<Number> value) {
        this.tickLabelFormatter.set((Object)value);
    }
    
    public final ObjectProperty<StringConverter<Number>> labelFormatterProperty() {
        return this.tickLabelFormatter;
    }
    
    public void incrementLowValue() {
        this.adjustLowValue(this.getLowValue() + this.getBlockIncrement());
    }
    
    public void decrementLowValue() {
        this.adjustLowValue(this.getLowValue() - this.getBlockIncrement());
    }
    
    public void incrementHighValue() {
        this.adjustHighValue(this.getHighValue() + this.getBlockIncrement());
    }
    
    public void decrementHighValue() {
        this.adjustHighValue(this.getHighValue() - this.getBlockIncrement());
    }
    
    public void adjustLowValue(double newValue) {
        final double d1 = this.getMin();
        final double d2 = this.getMax();
        if (d2 > d1) {
            newValue = ((newValue >= d1) ? newValue : d1);
            newValue = ((newValue <= d2) ? newValue : d2);
            this.setLowValue(this.snapValueToTicks(newValue));
        }
    }
    
    public void adjustHighValue(double newValue) {
        final double d1 = this.getMin();
        final double d2 = this.getMax();
        if (d2 > d1) {
            newValue = ((newValue >= d1) ? newValue : d1);
            newValue = ((newValue <= d2) ? newValue : d2);
            this.setHighValue(this.snapValueToTicks(newValue));
        }
    }
    
    public final void setMax(final double value) {
        this.maxProperty().set(value);
    }
    
    public final double getMax() {
        return (this.max == null) ? 100.0 : this.max.get();
    }
    
    public final DoubleProperty maxProperty() {
        if (this.max == null) {
            this.max = (DoubleProperty)new DoublePropertyBase(100.0) {
                protected void invalidated() {
                    if (this.get() < RangeSlider.this.getMin()) {
                        RangeSlider.this.setMin(this.get());
                    }
                    RangeSlider.this.adjustValues();
                }
                
                public Object getBean() {
                    return RangeSlider.this;
                }
                
                public String getName() {
                    return "max";
                }
            };
        }
        return this.max;
    }
    
    public final void setMin(final double value) {
        this.minProperty().set(value);
    }
    
    public final double getMin() {
        return (this.min == null) ? 0.0 : this.min.get();
    }
    
    public final DoubleProperty minProperty() {
        if (this.min == null) {
            this.min = (DoubleProperty)new DoublePropertyBase(0.0) {
                protected void invalidated() {
                    if (this.get() > RangeSlider.this.getMax()) {
                        RangeSlider.this.setMax(this.get());
                    }
                    RangeSlider.this.adjustValues();
                }
                
                public Object getBean() {
                    return RangeSlider.this;
                }
                
                public String getName() {
                    return "min";
                }
            };
        }
        return this.min;
    }
    
    public final void setSnapToTicks(final boolean value) {
        this.snapToTicksProperty().set(value);
    }
    
    public final boolean isSnapToTicks() {
        return this.snapToTicks != null && this.snapToTicks.get();
    }
    
    public final BooleanProperty snapToTicksProperty() {
        if (this.snapToTicks == null) {
            this.snapToTicks = (BooleanProperty)new StyleableBooleanProperty(false) {
                public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
                    return (CssMetaData<? extends Styleable, Boolean>)StyleableProperties.SNAP_TO_TICKS;
                }
                
                public Object getBean() {
                    return RangeSlider.this;
                }
                
                public String getName() {
                    return "snapToTicks";
                }
            };
        }
        return this.snapToTicks;
    }
    
    public final void setMajorTickUnit(final double value) {
        if (value <= 0.0) {
            throw new IllegalArgumentException("MajorTickUnit cannot be less than or equal to 0.");
        }
        this.majorTickUnitProperty().set(value);
    }
    
    public final double getMajorTickUnit() {
        return (this.majorTickUnit == null) ? 25.0 : this.majorTickUnit.get();
    }
    
    public final DoubleProperty majorTickUnitProperty() {
        if (this.majorTickUnit == null) {
            this.majorTickUnit = (DoubleProperty)new StyleableDoubleProperty(25.0) {
                public void invalidated() {
                    if (this.get() <= 0.0) {
                        throw new IllegalArgumentException("MajorTickUnit cannot be less than or equal to 0.");
                    }
                }
                
                public CssMetaData<? extends Styleable, Number> getCssMetaData() {
                    return (CssMetaData<? extends Styleable, Number>)StyleableProperties.MAJOR_TICK_UNIT;
                }
                
                public Object getBean() {
                    return RangeSlider.this;
                }
                
                public String getName() {
                    return "majorTickUnit";
                }
            };
        }
        return this.majorTickUnit;
    }
    
    public final void setMinorTickCount(final int value) {
        this.minorTickCountProperty().set(value);
    }
    
    public final int getMinorTickCount() {
        return (this.minorTickCount == null) ? 3 : this.minorTickCount.get();
    }
    
    public final IntegerProperty minorTickCountProperty() {
        if (this.minorTickCount == null) {
            this.minorTickCount = (IntegerProperty)new StyleableIntegerProperty(3) {
                public CssMetaData<? extends Styleable, Number> getCssMetaData() {
                    return (CssMetaData<? extends Styleable, Number>)StyleableProperties.MINOR_TICK_COUNT;
                }
                
                public Object getBean() {
                    return RangeSlider.this;
                }
                
                public String getName() {
                    return "minorTickCount";
                }
            };
        }
        return this.minorTickCount;
    }
    
    public final void setBlockIncrement(final double value) {
        this.blockIncrementProperty().set(value);
    }
    
    public final double getBlockIncrement() {
        return (this.blockIncrement == null) ? 10.0 : this.blockIncrement.get();
    }
    
    public final DoubleProperty blockIncrementProperty() {
        if (this.blockIncrement == null) {
            this.blockIncrement = (DoubleProperty)new StyleableDoubleProperty(10.0) {
                public CssMetaData<? extends Styleable, Number> getCssMetaData() {
                    return (CssMetaData<? extends Styleable, Number>)StyleableProperties.BLOCK_INCREMENT;
                }
                
                public Object getBean() {
                    return RangeSlider.this;
                }
                
                public String getName() {
                    return "blockIncrement";
                }
            };
        }
        return this.blockIncrement;
    }
    
    public final void setOrientation(final Orientation value) {
        this.orientationProperty().set((Object)value);
    }
    
    public final Orientation getOrientation() {
        return (Orientation)((this.orientation == null) ? Orientation.HORIZONTAL : this.orientation.get());
    }
    
    public final ObjectProperty<Orientation> orientationProperty() {
        if (this.orientation == null) {
            this.orientation = (ObjectProperty<Orientation>)new StyleableObjectProperty<Orientation>(Orientation.HORIZONTAL) {
                protected void invalidated() {
                    final boolean vertical = this.get() == Orientation.VERTICAL;
                    RangeSlider.this.pseudoClassStateChanged(RangeSlider.VERTICAL_PSEUDOCLASS_STATE, vertical);
                    RangeSlider.this.pseudoClassStateChanged(RangeSlider.HORIZONTAL_PSEUDOCLASS_STATE, !vertical);
                }
                
                public CssMetaData<? extends Styleable, Orientation> getCssMetaData() {
                    return (CssMetaData<? extends Styleable, Orientation>)StyleableProperties.ORIENTATION;
                }
                
                public Object getBean() {
                    return RangeSlider.this;
                }
                
                public String getName() {
                    return "orientation";
                }
            };
        }
        return this.orientation;
    }
    
    public final void setShowTickLabels(final boolean value) {
        this.showTickLabelsProperty().set(value);
    }
    
    public final boolean isShowTickLabels() {
        return this.showTickLabels != null && this.showTickLabels.get();
    }
    
    public final BooleanProperty showTickLabelsProperty() {
        if (this.showTickLabels == null) {
            this.showTickLabels = (BooleanProperty)new StyleableBooleanProperty(false) {
                public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
                    return (CssMetaData<? extends Styleable, Boolean>)StyleableProperties.SHOW_TICK_LABELS;
                }
                
                public Object getBean() {
                    return RangeSlider.this;
                }
                
                public String getName() {
                    return "showTickLabels";
                }
            };
        }
        return this.showTickLabels;
    }
    
    public final void setShowTickMarks(final boolean value) {
        this.showTickMarksProperty().set(value);
    }
    
    public final boolean isShowTickMarks() {
        return this.showTickMarks != null && this.showTickMarks.get();
    }
    
    public final BooleanProperty showTickMarksProperty() {
        if (this.showTickMarks == null) {
            this.showTickMarks = (BooleanProperty)new StyleableBooleanProperty(false) {
                public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
                    return (CssMetaData<? extends Styleable, Boolean>)StyleableProperties.SHOW_TICK_MARKS;
                }
                
                public Object getBean() {
                    return RangeSlider.this;
                }
                
                public String getName() {
                    return "showTickMarks";
                }
            };
        }
        return this.showTickMarks;
    }
    
    private void adjustValues() {
        this.adjustLowValues();
        this.adjustHighValues();
    }
    
    private void adjustLowValues() {
        if (this.getLowValue() < this.getMin() || this.getLowValue() > this.getMax()) {
            final double value = Utils.clamp(this.getMin(), this.getLowValue(), this.getMax());
            this.setLowValue(value);
        }
        else if (this.getLowValue() >= this.getHighValue() && this.getHighValue() >= this.getMin() && this.getHighValue() <= this.getMax()) {
            final double value = Utils.clamp(this.getMin(), this.getLowValue(), this.getHighValue());
            this.setLowValue(value);
        }
    }
    
    private double snapValueToTicks(final double d) {
        double d2 = d;
        if (this.isSnapToTicks()) {
            double d3 = 0.0;
            if (this.getMinorTickCount() != 0) {
                d3 = this.getMajorTickUnit() / (Math.max(this.getMinorTickCount(), 0) + 1);
            }
            else {
                d3 = this.getMajorTickUnit();
            }
            final int i = (int)((d2 - this.getMin()) / d3);
            final double d4 = i * d3 + this.getMin();
            final double d5 = (i + 1) * d3 + this.getMin();
            d2 = Utils.nearest(d4, d2, d5);
        }
        return Utils.clamp(this.getMin(), d2, this.getMax());
    }
    
    private void adjustHighValues() {
        if (this.getHighValue() < this.getMin() || this.getHighValue() > this.getMax()) {
            this.setHighValue(Utils.clamp(this.getMin(), this.getHighValue(), this.getMax()));
        }
        else if (this.getHighValue() < this.getLowValue() && this.getLowValue() >= this.getMin() && this.getLowValue() <= this.getMax()) {
            this.setHighValue(Utils.clamp(this.getLowValue(), this.getHighValue(), this.getMax()));
        }
    }
    
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }
    
    static {
        VERTICAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("vertical");
        HORIZONTAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("horizontal");
    }
    
    private static class StyleableProperties
    {
        private static final CssMetaData<RangeSlider, Number> BLOCK_INCREMENT;
        private static final CssMetaData<RangeSlider, Boolean> SHOW_TICK_LABELS;
        private static final CssMetaData<RangeSlider, Boolean> SHOW_TICK_MARKS;
        private static final CssMetaData<RangeSlider, Boolean> SNAP_TO_TICKS;
        private static final CssMetaData<RangeSlider, Number> MAJOR_TICK_UNIT;
        private static final CssMetaData<RangeSlider, Number> MINOR_TICK_COUNT;
        private static final CssMetaData<RangeSlider, Orientation> ORIENTATION;
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        
        static {
            BLOCK_INCREMENT = new CssMetaData<RangeSlider, Number>("-fx-block-increment", SizeConverter.getInstance(), 10.0) {
                public boolean isSettable(final RangeSlider n) {
                    return n.blockIncrement == null || !n.blockIncrement.isBound();
                }
                
                public StyleableProperty<Number> getStyleableProperty(final RangeSlider n) {
                    return (StyleableProperty<Number>)n.blockIncrementProperty();
                }
            };
            SHOW_TICK_LABELS = new CssMetaData<RangeSlider, Boolean>("-fx-show-tick-labels", BooleanConverter.getInstance(), Boolean.FALSE) {
                public boolean isSettable(final RangeSlider n) {
                    return n.showTickLabels == null || !n.showTickLabels.isBound();
                }
                
                public StyleableProperty<Boolean> getStyleableProperty(final RangeSlider n) {
                    return (StyleableProperty<Boolean>)n.showTickLabelsProperty();
                }
            };
            SHOW_TICK_MARKS = new CssMetaData<RangeSlider, Boolean>("-fx-show-tick-marks", BooleanConverter.getInstance(), Boolean.FALSE) {
                public boolean isSettable(final RangeSlider n) {
                    return n.showTickMarks == null || !n.showTickMarks.isBound();
                }
                
                public StyleableProperty<Boolean> getStyleableProperty(final RangeSlider n) {
                    return (StyleableProperty<Boolean>)n.showTickMarksProperty();
                }
            };
            SNAP_TO_TICKS = new CssMetaData<RangeSlider, Boolean>("-fx-snap-to-ticks", BooleanConverter.getInstance(), Boolean.FALSE) {
                public boolean isSettable(final RangeSlider n) {
                    return n.snapToTicks == null || !n.snapToTicks.isBound();
                }
                
                public StyleableProperty<Boolean> getStyleableProperty(final RangeSlider n) {
                    return (StyleableProperty<Boolean>)n.snapToTicksProperty();
                }
            };
            MAJOR_TICK_UNIT = new CssMetaData<RangeSlider, Number>("-fx-major-tick-unit", SizeConverter.getInstance(), 25.0) {
                public boolean isSettable(final RangeSlider n) {
                    return n.majorTickUnit == null || !n.majorTickUnit.isBound();
                }
                
                public StyleableProperty<Number> getStyleableProperty(final RangeSlider n) {
                    return (StyleableProperty<Number>)n.majorTickUnitProperty();
                }
            };
            MINOR_TICK_COUNT = new CssMetaData<RangeSlider, Number>("-fx-minor-tick-count", SizeConverter.getInstance(), 3.0) {
                public void set(final RangeSlider node, final Number value, final StyleOrigin origin) {
                    super.set((Styleable)node, (Object)value.intValue(), origin);
                }
                
                public boolean isSettable(final RangeSlider n) {
                    return n.minorTickCount == null || !n.minorTickCount.isBound();
                }
                
                public StyleableProperty<Number> getStyleableProperty(final RangeSlider n) {
                    return (StyleableProperty<Number>)n.minorTickCountProperty();
                }
            };
            ORIENTATION = new CssMetaData<RangeSlider, Orientation>("-fx-orientation", (StyleConverter)new EnumConverter((Class)Orientation.class), Orientation.HORIZONTAL) {
                public Orientation getInitialValue(final RangeSlider node) {
                    return node.getOrientation();
                }
                
                public boolean isSettable(final RangeSlider n) {
                    return n.orientation == null || !n.orientation.isBound();
                }
                
                public StyleableProperty<Orientation> getStyleableProperty(final RangeSlider n) {
                    return (StyleableProperty<Orientation>)n.orientationProperty();
                }
            };
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
            styleables.add((CssMetaData<? extends Styleable, ?>)StyleableProperties.BLOCK_INCREMENT);
            styleables.add((CssMetaData<? extends Styleable, ?>)StyleableProperties.SHOW_TICK_LABELS);
            styleables.add((CssMetaData<? extends Styleable, ?>)StyleableProperties.SHOW_TICK_MARKS);
            styleables.add((CssMetaData<? extends Styleable, ?>)StyleableProperties.SNAP_TO_TICKS);
            styleables.add((CssMetaData<? extends Styleable, ?>)StyleableProperties.MAJOR_TICK_UNIT);
            styleables.add((CssMetaData<? extends Styleable, ?>)StyleableProperties.MINOR_TICK_COUNT);
            styleables.add((CssMetaData<? extends Styleable, ?>)StyleableProperties.ORIENTATION);
            STYLEABLES = Collections.unmodifiableList((List<? extends CssMetaData<? extends Styleable, ?>>)styleables);
        }
    }
}
