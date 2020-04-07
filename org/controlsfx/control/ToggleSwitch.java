// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import impl.org.controlsfx.skin.ToggleSwitchSkin;
import javafx.scene.control.Skin;
import javafx.event.Event;
import javafx.event.ActionEvent;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Labeled;

public class ToggleSwitch extends Labeled
{
    private BooleanProperty selected;
    private static final String DEFAULT_STYLE_CLASS = "toggle-switch";
    private static final PseudoClass PSEUDO_CLASS_SELECTED;
    
    public ToggleSwitch() {
        this.initialize();
    }
    
    public ToggleSwitch(final String text) {
        super(text);
        this.initialize();
    }
    
    private void initialize() {
        this.getStyleClass().setAll((Object[])new String[] { "toggle-switch" });
    }
    
    public final void setSelected(final boolean value) {
        this.selectedProperty().set(value);
    }
    
    public final boolean isSelected() {
        return this.selected != null && this.selected.get();
    }
    
    public final BooleanProperty selectedProperty() {
        if (this.selected == null) {
            this.selected = (BooleanProperty)new BooleanPropertyBase() {
                protected void invalidated() {
                    final Boolean v = this.get();
                    ToggleSwitch.this.pseudoClassStateChanged(ToggleSwitch.PSEUDO_CLASS_SELECTED, (boolean)v);
                }
                
                public Object getBean() {
                    return ToggleSwitch.this;
                }
                
                public String getName() {
                    return "selected";
                }
            };
        }
        return this.selected;
    }
    
    public void fire() {
        if (!this.isDisabled()) {
            this.setSelected(!this.isSelected());
            this.fireEvent((Event)new ActionEvent());
        }
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new ToggleSwitchSkin(this);
    }
    
    public String getUserAgentStylesheet() {
        return ToggleSwitch.class.getResource("toggleswitch.css").toExternalForm();
    }
    
    static {
        PSEUDO_CLASS_SELECTED = PseudoClass.getPseudoClass("selected");
    }
}
