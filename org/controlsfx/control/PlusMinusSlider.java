// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import javafx.event.EventTarget;
import javafx.scene.input.InputEvent;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import javafx.scene.control.Control;
import javafx.css.StyleableProperty;
import javafx.css.StyleConverter;
import com.sun.javafx.css.converters.EnumConverter;
import javafx.css.Styleable;
import java.util.List;
import javafx.css.CssMetaData;
import javafx.css.StyleableObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import impl.org.controlsfx.skin.PlusMinusSliderSkin;
import javafx.scene.control.Skin;
import javafx.collections.MapChangeListener;
import javafx.event.EventType;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.css.PseudoClass;

public class PlusMinusSlider extends ControlsFXControl
{
    private static final String DEFAULT_STYLE_CLASS = "plus-minus-slider";
    private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE;
    private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE;
    private ReadOnlyDoubleWrapper value;
    private ObjectProperty<Orientation> orientation;
    private ObjectProperty<EventHandler<PlusMinusEvent>> onValueChanged;
    
    public PlusMinusSlider() {
        this.value = new ReadOnlyDoubleWrapper((Object)this, "value", 0.0);
        this.onValueChanged = (ObjectProperty<EventHandler<PlusMinusEvent>>)new ObjectPropertyBase<EventHandler<PlusMinusEvent>>() {
            protected void invalidated() {
                PlusMinusSlider.access$400(PlusMinusSlider.this, PlusMinusEvent.VALUE_CHANGED, (EventHandler)this.get());
            }
            
            public Object getBean() {
                return PlusMinusSlider.this;
            }
            
            public String getName() {
                return "onValueChanged";
            }
        };
        this.getStyleClass().add((Object)"plus-minus-slider");
        this.setOrientation(Orientation.HORIZONTAL);
        this.getProperties().addListener((MapChangeListener)new MapChangeListener<Object, Object>() {
            public void onChanged(final MapChangeListener.Change<?, ?> change) {
                if (change.getKey().equals("plusminusslidervalue") && change.getValueAdded() != null) {
                    final Double valueAdded = (Double)change.getValueAdded();
                    PlusMinusSlider.this.value.set((double)valueAdded);
                    change.getMap().remove((Object)"plusminusslidervalue");
                }
            }
        });
    }
    
    public String getUserAgentStylesheet() {
        return this.getUserAgentStylesheet(PlusMinusSlider.class, "plusminusslider.css");
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new PlusMinusSliderSkin(this);
    }
    
    public final ReadOnlyDoubleProperty valueProperty() {
        return this.value.getReadOnlyProperty();
    }
    
    public final double getValue() {
        return this.valueProperty().get();
    }
    
    public final void setOrientation(final Orientation value) {
        this.orientationProperty().set((Object)value);
    }
    
    public final Orientation getOrientation() {
        return (Orientation)((this.orientation == null) ? Orientation.HORIZONTAL : this.orientation.get());
    }
    
    public final ObjectProperty<Orientation> orientationProperty() {
        if (this.orientation == null) {
            this.orientation = (ObjectProperty<Orientation>)new StyleableObjectProperty<Orientation>(null) {
                protected void invalidated() {
                    final boolean vertical = this.get() == Orientation.VERTICAL;
                    PlusMinusSlider.this.pseudoClassStateChanged(PlusMinusSlider.VERTICAL_PSEUDOCLASS_STATE, vertical);
                    PlusMinusSlider.this.pseudoClassStateChanged(PlusMinusSlider.HORIZONTAL_PSEUDOCLASS_STATE, !vertical);
                }
                
                public CssMetaData<PlusMinusSlider, Orientation> getCssMetaData() {
                    return StyleableProperties.ORIENTATION;
                }
                
                public Object getBean() {
                    return PlusMinusSlider.this;
                }
                
                public String getName() {
                    return "orientation";
                }
            };
        }
        return this.orientation;
    }
    
    public final ObjectProperty<EventHandler<PlusMinusEvent>> onValueChangedProperty() {
        return this.onValueChanged;
    }
    
    public final void setOnValueChanged(final EventHandler<PlusMinusEvent> value) {
        this.onValueChangedProperty().set((Object)value);
    }
    
    public final EventHandler<PlusMinusEvent> getOnValueChanged() {
        return (EventHandler<PlusMinusEvent>)this.onValueChangedProperty().get();
    }
    
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }
    
    static /* synthetic */ void access$400(final PlusMinusSlider x0, final EventType x1, final EventHandler x2) {
        x0.setEventHandler(x1, x2);
    }
    
    static {
        VERTICAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("vertical");
        HORIZONTAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("horizontal");
    }
    
    private static class StyleableProperties
    {
        private static final CssMetaData<PlusMinusSlider, Orientation> ORIENTATION;
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        
        static {
            ORIENTATION = new CssMetaData<PlusMinusSlider, Orientation>("-fx-orientation", (StyleConverter)new EnumConverter((Class)Orientation.class), Orientation.VERTICAL) {
                public Orientation getInitialValue(final PlusMinusSlider node) {
                    return node.getOrientation();
                }
                
                public boolean isSettable(final PlusMinusSlider n) {
                    return n.orientation == null || !n.orientation.isBound();
                }
                
                public StyleableProperty<Orientation> getStyleableProperty(final PlusMinusSlider n) {
                    return (StyleableProperty<Orientation>)n.orientationProperty();
                }
            };
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
            styleables.add((CssMetaData<? extends Styleable, ?>)StyleableProperties.ORIENTATION);
            STYLEABLES = Collections.unmodifiableList((List<? extends CssMetaData<? extends Styleable, ?>>)styleables);
        }
    }
    
    public static class PlusMinusEvent extends InputEvent
    {
        private static final long serialVersionUID = 2881004583512990781L;
        public static final EventType<PlusMinusEvent> ANY;
        public static final EventType<PlusMinusEvent> VALUE_CHANGED;
        private double value;
        
        public PlusMinusEvent(final Object source, final EventTarget target, final EventType<? extends InputEvent> eventType, final double value) {
            super(source, target, (EventType)eventType);
            this.value = value;
        }
        
        public double getValue() {
            return this.value;
        }
        
        static {
            ANY = new EventType(InputEvent.ANY, "ANY");
            VALUE_CHANGED = new EventType((EventType)PlusMinusEvent.ANY, "VALUE_CHANGED");
        }
    }
}
