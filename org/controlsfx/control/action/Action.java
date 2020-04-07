// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.action;

import javafx.event.Event;
import impl.org.controlsfx.i18n.Localization;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import impl.org.controlsfx.i18n.SimpleLocalizedStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.beans.NamedArg;
import javafx.collections.ObservableMap;
import javafx.scene.input.KeyCombination;
import javafx.scene.Node;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.beans.property.StringProperty;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class Action implements EventHandler<ActionEvent>
{
    private boolean locked;
    private Consumer<ActionEvent> eventHandler;
    private StringProperty style;
    private final ObservableList<String> styleClass;
    private final BooleanProperty selectedProperty;
    private final StringProperty textProperty;
    private final BooleanProperty disabledProperty;
    private final StringProperty longTextProperty;
    private final ObjectProperty<Node> graphicProperty;
    private final ObjectProperty<KeyCombination> acceleratorProperty;
    private ObservableMap<Object, Object> props;
    
    public Action(@NamedArg("text") final String text) {
        this(text, null);
    }
    
    public Action(final Consumer<ActionEvent> eventHandler) {
        this("", eventHandler);
    }
    
    public Action(@NamedArg("text") final String text, final Consumer<ActionEvent> eventHandler) {
        this.locked = false;
        this.styleClass = (ObservableList<String>)FXCollections.observableArrayList();
        this.selectedProperty = (BooleanProperty)new SimpleBooleanProperty((Object)this, "selected") {
            public void set(final boolean selected) {
                if (Action.this.locked) {
                    throw new UnsupportedOperationException("The action is immutable, property change support is disabled.");
                }
                super.set(selected);
            }
        };
        this.textProperty = (StringProperty)new SimpleLocalizedStringProperty((Object)this, "text") {
            public void set(final String value) {
                if (Action.this.locked) {
                    throw new RuntimeException("The action is immutable, property change support is disabled.");
                }
                super.set(value);
            }
        };
        this.disabledProperty = (BooleanProperty)new SimpleBooleanProperty((Object)this, "disabled") {
            public void set(final boolean value) {
                if (Action.this.locked) {
                    throw new RuntimeException("The action is immutable, property change support is disabled.");
                }
                super.set(value);
            }
        };
        this.longTextProperty = (StringProperty)new SimpleLocalizedStringProperty((Object)this, "longText") {
            public void set(final String value) {
                if (Action.this.locked) {
                    throw new RuntimeException("The action is immutable, property change support is disabled.");
                }
                super.set(value);
            }
        };
        this.graphicProperty = (ObjectProperty<Node>)new SimpleObjectProperty<Node>((Object)this, "graphic") {
            public void set(final Node value) {
                if (Action.this.locked) {
                    throw new RuntimeException("The action is immutable, property change support is disabled.");
                }
                super.set((Object)value);
            }
        };
        this.acceleratorProperty = (ObjectProperty<KeyCombination>)new SimpleObjectProperty<KeyCombination>((Object)this, "accelerator") {
            public void set(final KeyCombination value) {
                if (Action.this.locked) {
                    throw new RuntimeException("The action is immutable, property change support is disabled.");
                }
                super.set((Object)value);
            }
        };
        this.setText(text);
        this.setEventHandler(eventHandler);
        this.getStyleClass().add((Object)"action");
    }
    
    protected void lock() {
        this.locked = true;
    }
    
    public final void setStyle(final String value) {
        this.styleProperty().set((Object)value);
    }
    
    public final String getStyle() {
        return (String)((this.style == null) ? "" : this.style.get());
    }
    
    public final StringProperty styleProperty() {
        if (this.style == null) {
            this.style = (StringProperty)new SimpleStringProperty(this, "style") {
                public void set(final String style) {
                    if (Action.this.locked) {
                        throw new UnsupportedOperationException("The action is immutable, property change support is disabled.");
                    }
                    super.set(style);
                }
            };
        }
        return this.style;
    }
    
    public ObservableList<String> getStyleClass() {
        return this.styleClass;
    }
    
    public final BooleanProperty selectedProperty() {
        return this.selectedProperty;
    }
    
    public final boolean isSelected() {
        return this.selectedProperty.get();
    }
    
    public final void setSelected(final boolean selected) {
        this.selectedProperty.set(selected);
    }
    
    public final StringProperty textProperty() {
        return this.textProperty;
    }
    
    public final String getText() {
        return (String)this.textProperty.get();
    }
    
    public final void setText(final String value) {
        this.textProperty.set((Object)value);
    }
    
    public final BooleanProperty disabledProperty() {
        return this.disabledProperty;
    }
    
    public final boolean isDisabled() {
        return this.disabledProperty.get();
    }
    
    public final void setDisabled(final boolean value) {
        this.disabledProperty.set(value);
    }
    
    public final StringProperty longTextProperty() {
        return this.longTextProperty;
    }
    
    public final String getLongText() {
        return Localization.localize((String)this.longTextProperty.get());
    }
    
    public final void setLongText(final String value) {
        this.longTextProperty.set((Object)value);
    }
    
    public final ObjectProperty<Node> graphicProperty() {
        return this.graphicProperty;
    }
    
    public final Node getGraphic() {
        return (Node)this.graphicProperty.get();
    }
    
    public final void setGraphic(final Node value) {
        this.graphicProperty.set((Object)value);
    }
    
    public final ObjectProperty<KeyCombination> acceleratorProperty() {
        return this.acceleratorProperty;
    }
    
    public final KeyCombination getAccelerator() {
        return (KeyCombination)this.acceleratorProperty.get();
    }
    
    public final void setAccelerator(final KeyCombination value) {
        this.acceleratorProperty.set((Object)value);
    }
    
    public final synchronized ObservableMap<Object, Object> getProperties() {
        if (this.props == null) {
            this.props = (ObservableMap<Object, Object>)FXCollections.observableHashMap();
        }
        return this.props;
    }
    
    protected Consumer<ActionEvent> getEventHandler() {
        return this.eventHandler;
    }
    
    protected void setEventHandler(final Consumer<ActionEvent> eventHandler) {
        this.eventHandler = eventHandler;
    }
    
    public final void handle(final ActionEvent event) {
        if (this.eventHandler != null && !this.isDisabled()) {
            this.eventHandler.accept(event);
        }
    }
}
