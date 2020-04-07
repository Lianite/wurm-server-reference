// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import javafx.beans.property.SimpleObjectProperty;
import impl.org.controlsfx.skin.HyperlinkLabelSkin;
import javafx.scene.control.Skin;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import com.sun.javafx.event.EventHandlerManager;
import javafx.event.EventTarget;

public class HyperlinkLabel extends ControlsFXControl implements EventTarget
{
    private final EventHandlerManager eventHandlerManager;
    private final StringProperty text;
    private ObjectProperty<EventHandler<ActionEvent>> onAction;
    
    public HyperlinkLabel() {
        this(null);
    }
    
    public HyperlinkLabel(final String text) {
        this.eventHandlerManager = new EventHandlerManager((Object)this);
        this.text = (StringProperty)new SimpleStringProperty((Object)this, "text");
        this.setText(text);
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new HyperlinkLabelSkin(this);
    }
    
    public final StringProperty textProperty() {
        return this.text;
    }
    
    public final String getText() {
        return (String)this.text.get();
    }
    
    public final void setText(final String value) {
        this.text.set((Object)value);
    }
    
    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        if (this.onAction == null) {
            this.onAction = (ObjectProperty<EventHandler<ActionEvent>>)new SimpleObjectProperty<EventHandler<ActionEvent>>(this, "onAction") {
                protected void invalidated() {
                    HyperlinkLabel.this.eventHandlerManager.setEventHandler(ActionEvent.ACTION, (EventHandler)this.get());
                }
            };
        }
        return this.onAction;
    }
    
    public final void setOnAction(final EventHandler<ActionEvent> value) {
        this.onActionProperty().set((Object)value);
    }
    
    public final EventHandler<ActionEvent> getOnAction() {
        return (EventHandler<ActionEvent>)((this.onAction == null) ? null : ((EventHandler)this.onAction.get()));
    }
}
