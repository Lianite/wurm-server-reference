// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import javafx.beans.value.ObservableValue;
import java.util.Optional;
import impl.org.controlsfx.skin.PropertySheetSkin;
import javafx.scene.control.Skin;
import javafx.collections.FXCollections;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.controlsfx.property.editor.PropertyEditor;
import javafx.util.Callback;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

public class PropertySheet extends ControlsFXControl
{
    private final ObservableList<Item> items;
    private final SimpleObjectProperty<Mode> modeProperty;
    private final SimpleObjectProperty<Callback<Item, PropertyEditor<?>>> propertyEditorFactory;
    private final SimpleBooleanProperty modeSwitcherVisible;
    private final SimpleBooleanProperty searchBoxVisible;
    private final SimpleStringProperty titleFilterProperty;
    private static final String DEFAULT_STYLE_CLASS = "property-sheet";
    
    public PropertySheet() {
        this(null);
    }
    
    public PropertySheet(final ObservableList<Item> items) {
        this.modeProperty = (SimpleObjectProperty<Mode>)new SimpleObjectProperty((Object)this, "mode", (Object)Mode.NAME);
        this.propertyEditorFactory = (SimpleObjectProperty<Callback<Item, PropertyEditor<?>>>)new SimpleObjectProperty((Object)this, "propertyEditor", (Object)new DefaultPropertyEditorFactory());
        this.modeSwitcherVisible = new SimpleBooleanProperty((Object)this, "modeSwitcherVisible", true);
        this.searchBoxVisible = new SimpleBooleanProperty((Object)this, "searchBoxVisible", true);
        this.titleFilterProperty = new SimpleStringProperty((Object)this, "titleFilter", "");
        this.getStyleClass().add((Object)"property-sheet");
        this.items = (ObservableList<Item>)((items == null) ? FXCollections.observableArrayList() : items);
    }
    
    public ObservableList<Item> getItems() {
        return this.items;
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new PropertySheetSkin(this);
    }
    
    public String getUserAgentStylesheet() {
        return this.getUserAgentStylesheet(PropertySheet.class, "propertysheet.css");
    }
    
    public final SimpleObjectProperty<Mode> modeProperty() {
        return this.modeProperty;
    }
    
    public final Mode getMode() {
        return (Mode)this.modeProperty.get();
    }
    
    public final void setMode(final Mode mode) {
        this.modeProperty.set((Object)mode);
    }
    
    public final SimpleObjectProperty<Callback<Item, PropertyEditor<?>>> propertyEditorFactory() {
        return this.propertyEditorFactory;
    }
    
    public final Callback<Item, PropertyEditor<?>> getPropertyEditorFactory() {
        return (Callback<Item, PropertyEditor<?>>)this.propertyEditorFactory.get();
    }
    
    public final void setPropertyEditorFactory(final Callback<Item, PropertyEditor<?>> factory) {
        this.propertyEditorFactory.set((factory == null) ? new DefaultPropertyEditorFactory() : factory);
    }
    
    public final SimpleBooleanProperty modeSwitcherVisibleProperty() {
        return this.modeSwitcherVisible;
    }
    
    public final boolean isModeSwitcherVisible() {
        return this.modeSwitcherVisible.get();
    }
    
    public final void setModeSwitcherVisible(final boolean visible) {
        this.modeSwitcherVisible.set(visible);
    }
    
    public final SimpleBooleanProperty searchBoxVisibleProperty() {
        return this.searchBoxVisible;
    }
    
    public final boolean isSearchBoxVisible() {
        return this.searchBoxVisible.get();
    }
    
    public final void setSearchBoxVisible(final boolean visible) {
        this.searchBoxVisible.set(visible);
    }
    
    public final SimpleStringProperty titleFilter() {
        return this.titleFilterProperty;
    }
    
    public final String getTitleFilter() {
        return this.titleFilterProperty.get();
    }
    
    public final void setTitleFilter(final String filter) {
        this.titleFilterProperty.set(filter);
    }
    
    public enum Mode
    {
        NAME, 
        CATEGORY;
    }
    
    public interface Item
    {
        Class<?> getType();
        
        String getCategory();
        
        String getName();
        
        String getDescription();
        
        Object getValue();
        
        void setValue(final Object p0);
        
        Optional<ObservableValue<?>> getObservableValue();
        
        default Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
            return Optional.empty();
        }
        
        default boolean isEditable() {
            return true;
        }
    }
}
