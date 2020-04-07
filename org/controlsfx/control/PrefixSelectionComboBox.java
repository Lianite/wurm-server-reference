// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import javafx.beans.value.ObservableValue;
import javafx.beans.property.SimpleObjectProperty;
import impl.org.controlsfx.tools.PrefixSelectionCustomizer;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.util.Optional;
import java.util.function.BiFunction;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;

public class PrefixSelectionComboBox<T> extends ComboBox<T>
{
    private final ChangeListener<Boolean> focusedListener;
    private final BooleanProperty displayOnFocusedEnabled;
    private final BooleanProperty backSpaceAllowed;
    private final IntegerProperty typingDelay;
    private final ObjectProperty<BiFunction<ComboBox, String, Optional>> lookup;
    
    public PrefixSelectionComboBox() {
        this.focusedListener = (ChangeListener<Boolean>)((obs, ov, nv) -> {
            if (nv) {
                this.show();
            }
        });
        this.displayOnFocusedEnabled = (BooleanProperty)new SimpleBooleanProperty((Object)this, "displayOnFocusedEnabled", false) {
            protected void invalidated() {
                if (this.get()) {
                    PrefixSelectionComboBox.this.focusedProperty().addListener(PrefixSelectionComboBox.this.focusedListener);
                }
                else {
                    PrefixSelectionComboBox.this.focusedProperty().removeListener(PrefixSelectionComboBox.this.focusedListener);
                }
            }
        };
        this.backSpaceAllowed = (BooleanProperty)new SimpleBooleanProperty((Object)this, "backSpaceAllowed", false);
        this.typingDelay = (IntegerProperty)new SimpleIntegerProperty((Object)this, "typingDelay", 500);
        this.lookup = (ObjectProperty<BiFunction<ComboBox, String, Optional>>)new SimpleObjectProperty((Object)this, "lookup", (Object)PrefixSelectionCustomizer.DEFAULT_LOOKUP_COMBOBOX);
        this.setEditable(false);
        PrefixSelectionCustomizer.customize(this);
    }
    
    public final boolean isDisplayOnFocusedEnabled() {
        return this.displayOnFocusedEnabled.get();
    }
    
    public final void setDisplayOnFocusedEnabled(final boolean value) {
        this.displayOnFocusedEnabled.set(value);
    }
    
    public final BooleanProperty displayOnFocusedEnabledProperty() {
        return this.displayOnFocusedEnabled;
    }
    
    public final boolean isBackSpaceAllowed() {
        return this.backSpaceAllowed.get();
    }
    
    public final void setBackSpaceAllowed(final boolean value) {
        this.backSpaceAllowed.set(value);
    }
    
    public final BooleanProperty backSpaceAllowedProperty() {
        return this.backSpaceAllowed;
    }
    
    public final int getTypingDelay() {
        return this.typingDelay.get();
    }
    
    public final void setTypingDelay(final int value) {
        this.typingDelay.set(value);
    }
    
    public final IntegerProperty typingDelayProperty() {
        return this.typingDelay;
    }
    
    public final BiFunction<ComboBox, String, Optional> getLookup() {
        return (BiFunction<ComboBox, String, Optional>)this.lookup.get();
    }
    
    public final void setLookup(final BiFunction<ComboBox, String, Optional> value) {
        this.lookup.set((Object)value);
    }
    
    public final ObjectProperty<BiFunction<ComboBox, String, Optional>> lookupProperty() {
        return this.lookup;
    }
}
