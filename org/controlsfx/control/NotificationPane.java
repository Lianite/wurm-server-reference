// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import javafx.beans.property.ReadOnlyBooleanProperty;
import impl.org.controlsfx.skin.NotificationPaneSkin;
import javafx.scene.control.Skin;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.controlsfx.control.action.Action;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.beans.property.ObjectProperty;
import javafx.event.Event;
import javafx.event.EventType;

public class NotificationPane extends ControlsFXControl
{
    public static final String STYLE_CLASS_DARK = "dark";
    public static final EventType<Event> ON_SHOWING;
    public static final EventType<Event> ON_SHOWN;
    public static final EventType<Event> ON_HIDING;
    public static final EventType<Event> ON_HIDDEN;
    private ObjectProperty<Node> content;
    private StringProperty text;
    private ObjectProperty<Node> graphic;
    private ReadOnlyBooleanWrapper showing;
    private BooleanProperty showFromTop;
    private ObjectProperty<EventHandler<Event>> onShowing;
    private ObjectProperty<EventHandler<Event>> onShown;
    private ObjectProperty<EventHandler<Event>> onHiding;
    private ObjectProperty<EventHandler<Event>> onHidden;
    private BooleanProperty closeButtonVisible;
    private final ObservableList<Action> actions;
    private static final String DEFAULT_STYLE_CLASS = "notification-pane";
    
    public NotificationPane() {
        this(null);
    }
    
    public NotificationPane(final Node content) {
        this.content = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "content");
        this.text = (StringProperty)new SimpleStringProperty((Object)this, "text");
        this.graphic = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "graphic");
        this.showing = new ReadOnlyBooleanWrapper((Object)this, "showing");
        this.showFromTop = (BooleanProperty)new SimpleBooleanProperty((Object)this, "showFromTop", true) {
            protected void invalidated() {
                NotificationPane.this.updateStyleClasses();
            }
        };
        this.onShowing = (ObjectProperty<EventHandler<Event>>)new SimpleObjectProperty<EventHandler<Event>>((Object)this, "onShowing") {
            protected void invalidated() {
                NotificationPane.access$100(NotificationPane.this, NotificationPane.ON_SHOWING, (EventHandler)this.get());
            }
        };
        this.onShown = (ObjectProperty<EventHandler<Event>>)new SimpleObjectProperty<EventHandler<Event>>((Object)this, "onShown") {
            protected void invalidated() {
                NotificationPane.access$200(NotificationPane.this, NotificationPane.ON_SHOWN, (EventHandler)this.get());
            }
        };
        this.onHiding = (ObjectProperty<EventHandler<Event>>)new SimpleObjectProperty<EventHandler<Event>>((Object)this, "onHiding") {
            protected void invalidated() {
                NotificationPane.access$300(NotificationPane.this, NotificationPane.ON_HIDING, (EventHandler)this.get());
            }
        };
        this.onHidden = (ObjectProperty<EventHandler<Event>>)new SimpleObjectProperty<EventHandler<Event>>((Object)this, "onHidden") {
            protected void invalidated() {
                NotificationPane.access$400(NotificationPane.this, NotificationPane.ON_HIDDEN, (EventHandler)this.get());
            }
        };
        this.closeButtonVisible = (BooleanProperty)new SimpleBooleanProperty((Object)this, "closeButtonVisible", true);
        this.actions = (ObservableList<Action>)FXCollections.observableArrayList();
        this.getStyleClass().add((Object)"notification-pane");
        this.setContent(content);
        this.updateStyleClasses();
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new NotificationPaneSkin(this);
    }
    
    public String getUserAgentStylesheet() {
        return this.getUserAgentStylesheet(NotificationPane.class, "notificationpane.css");
    }
    
    public final ObjectProperty<Node> contentProperty() {
        return this.content;
    }
    
    public final void setContent(final Node value) {
        this.content.set((Object)value);
    }
    
    public final Node getContent() {
        return (Node)this.content.get();
    }
    
    public final StringProperty textProperty() {
        return this.text;
    }
    
    public final void setText(final String value) {
        this.text.set((Object)value);
    }
    
    public final String getText() {
        return (String)this.text.get();
    }
    
    public final ObjectProperty<Node> graphicProperty() {
        return this.graphic;
    }
    
    public final void setGraphic(final Node value) {
        this.graphic.set((Object)value);
    }
    
    public final Node getGraphic() {
        return (Node)this.graphic.get();
    }
    
    public final ReadOnlyBooleanProperty showingProperty() {
        return this.showing.getReadOnlyProperty();
    }
    
    private final void setShowing(final boolean value) {
        this.showing.set(value);
    }
    
    public final boolean isShowing() {
        return this.showing.get();
    }
    
    public final BooleanProperty showFromTopProperty() {
        return this.showFromTop;
    }
    
    public final void setShowFromTop(final boolean value) {
        this.showFromTop.set(value);
    }
    
    public final boolean isShowFromTop() {
        return this.showFromTop.get();
    }
    
    public final ObjectProperty<EventHandler<Event>> onShowingProperty() {
        return this.onShowing;
    }
    
    public final void setOnShowing(final EventHandler<Event> value) {
        this.onShowingProperty().set((Object)value);
    }
    
    public final EventHandler<Event> getOnShowing() {
        return (EventHandler<Event>)this.onShowingProperty().get();
    }
    
    public final ObjectProperty<EventHandler<Event>> onShownProperty() {
        return this.onShown;
    }
    
    public final void setOnShown(final EventHandler<Event> value) {
        this.onShownProperty().set((Object)value);
    }
    
    public final EventHandler<Event> getOnShown() {
        return (EventHandler<Event>)this.onShownProperty().get();
    }
    
    public final ObjectProperty<EventHandler<Event>> onHidingProperty() {
        return this.onHiding;
    }
    
    public final void setOnHiding(final EventHandler<Event> value) {
        this.onHidingProperty().set((Object)value);
    }
    
    public final EventHandler<Event> getOnHiding() {
        return (EventHandler<Event>)this.onHidingProperty().get();
    }
    
    public final ObjectProperty<EventHandler<Event>> onHiddenProperty() {
        return this.onHidden;
    }
    
    public final void setOnHidden(final EventHandler<Event> value) {
        this.onHiddenProperty().set((Object)value);
    }
    
    public final EventHandler<Event> getOnHidden() {
        return (EventHandler<Event>)this.onHiddenProperty().get();
    }
    
    public final BooleanProperty closeButtonVisibleProperty() {
        return this.closeButtonVisible;
    }
    
    public final void setCloseButtonVisible(final boolean value) {
        this.closeButtonVisible.set(value);
    }
    
    public final boolean isCloseButtonVisible() {
        return this.closeButtonVisible.get();
    }
    
    public final ObservableList<Action> getActions() {
        return this.actions;
    }
    
    public void show() {
        this.setShowing(true);
    }
    
    public void show(final String text) {
        this.hideAndThen(new Runnable() {
            @Override
            public void run() {
                NotificationPane.this.setText(text);
                NotificationPane.this.setShowing(true);
            }
        });
    }
    
    public void show(final String text, final Node graphic) {
        this.hideAndThen(new Runnable() {
            @Override
            public void run() {
                NotificationPane.this.setText(text);
                NotificationPane.this.setGraphic(graphic);
                NotificationPane.this.setShowing(true);
            }
        });
    }
    
    public void show(final String text, final Node graphic, final Action... actions) {
        this.hideAndThen(new Runnable() {
            @Override
            public void run() {
                NotificationPane.this.setText(text);
                NotificationPane.this.setGraphic(graphic);
                if (actions == null) {
                    NotificationPane.this.getActions().clear();
                }
                else {
                    for (final Action action : actions) {
                        if (action != null) {
                            NotificationPane.this.getActions().add((Object)action);
                        }
                    }
                }
                NotificationPane.this.setShowing(true);
            }
        });
    }
    
    public void hide() {
        this.setShowing(false);
    }
    
    private void updateStyleClasses() {
        this.getStyleClass().removeAll((Object[])new String[] { "top", "bottom" });
        this.getStyleClass().add((Object)(this.isShowFromTop() ? "top" : "bottom"));
    }
    
    private void hideAndThen(final Runnable r) {
        if (this.isShowing()) {
            final EventHandler<Event> eventHandler = (EventHandler<Event>)new EventHandler<Event>() {
                public void handle(final Event e) {
                    r.run();
                    NotificationPane.this.removeEventHandler((EventType)NotificationPane.ON_HIDDEN, (EventHandler)this);
                }
            };
            this.addEventHandler((EventType)NotificationPane.ON_HIDDEN, (EventHandler)eventHandler);
            this.hide();
        }
        else {
            r.run();
        }
    }
    
    static /* synthetic */ void access$100(final NotificationPane x0, final EventType x1, final EventHandler x2) {
        x0.setEventHandler(x1, x2);
    }
    
    static /* synthetic */ void access$200(final NotificationPane x0, final EventType x1, final EventHandler x2) {
        x0.setEventHandler(x1, x2);
    }
    
    static /* synthetic */ void access$300(final NotificationPane x0, final EventType x1, final EventHandler x2) {
        x0.setEventHandler(x1, x2);
    }
    
    static /* synthetic */ void access$400(final NotificationPane x0, final EventType x1, final EventHandler x2) {
        x0.setEventHandler(x1, x2);
    }
    
    static {
        ON_SHOWING = new EventType(Event.ANY, "NOTIFICATION_PANE_ON_SHOWING");
        ON_SHOWN = new EventType(Event.ANY, "NOTIFICATION_PANE_ON_SHOWN");
        ON_HIDING = new EventType(Event.ANY, "NOTIFICATION_PANE_ON_HIDING");
        ON_HIDDEN = new EventType(Event.ANY, "NOTIFICATION_PANE_ON_HIDDEN");
    }
}
