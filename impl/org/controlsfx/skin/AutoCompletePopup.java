// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.event.Event;
import javafx.scene.control.Skin;
import javafx.event.EventDispatcher;
import javafx.event.EventDispatchChain;
import javafx.stage.Window;
import javafx.scene.Node;
import javafx.event.EventType;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.beans.property.ObjectProperty;
import com.sun.javafx.event.EventHandlerManager;
import javafx.beans.property.IntegerProperty;
import javafx.util.StringConverter;
import javafx.collections.ObservableList;
import javafx.scene.control.PopupControl;

public class AutoCompletePopup<T> extends PopupControl
{
    private static final int TITLE_HEIGHT = 28;
    private final ObservableList<T> suggestions;
    private StringConverter<T> converter;
    private IntegerProperty visibleRowCount;
    private final EventHandlerManager eventHandlerManager;
    private ObjectProperty<EventHandler<SuggestionEvent<T>>> onSuggestion;
    public static final String DEFAULT_STYLE_CLASS = "auto-complete-popup";
    
    public AutoCompletePopup() {
        this.suggestions = (ObservableList<T>)FXCollections.observableArrayList();
        this.visibleRowCount = (IntegerProperty)new SimpleIntegerProperty((Object)this, "visibleRowCount", 10);
        this.eventHandlerManager = new EventHandlerManager((Object)this);
        this.onSuggestion = (ObjectProperty<EventHandler<SuggestionEvent<T>>>)new ObjectPropertyBase<EventHandler<SuggestionEvent<T>>>() {
            protected void invalidated() {
                AutoCompletePopup.this.eventHandlerManager.setEventHandler((EventType)SuggestionEvent.SUGGESTION, (EventHandler)this.get());
            }
            
            public Object getBean() {
                return AutoCompletePopup.this;
            }
            
            public String getName() {
                return "onSuggestion";
            }
        };
        this.setAutoFix(true);
        this.setAutoHide(true);
        this.setHideOnEscape(true);
        this.getStyleClass().add((Object)"auto-complete-popup");
    }
    
    public ObservableList<T> getSuggestions() {
        return this.suggestions;
    }
    
    public void show(final Node node) {
        if (node.getScene() == null || node.getScene().getWindow() == null) {
            throw new IllegalStateException("Can not show popup. The node must be attached to a scene/window.");
        }
        if (this.isShowing()) {
            return;
        }
        final Window parent = node.getScene().getWindow();
        this.show(parent, parent.getX() + node.localToScene(0.0, 0.0).getX() + node.getScene().getX(), parent.getY() + node.localToScene(0.0, 0.0).getY() + node.getScene().getY() + 28.0);
    }
    
    public void setConverter(final StringConverter<T> converter) {
        this.converter = converter;
    }
    
    public StringConverter<T> getConverter() {
        return this.converter;
    }
    
    public final void setVisibleRowCount(final int value) {
        this.visibleRowCount.set(value);
    }
    
    public final int getVisibleRowCount() {
        return this.visibleRowCount.get();
    }
    
    public final IntegerProperty visibleRowCountProperty() {
        return this.visibleRowCount;
    }
    
    public final ObjectProperty<EventHandler<SuggestionEvent<T>>> onSuggestionProperty() {
        return this.onSuggestion;
    }
    
    public final void setOnSuggestion(final EventHandler<SuggestionEvent<T>> value) {
        this.onSuggestionProperty().set((Object)value);
    }
    
    public final EventHandler<SuggestionEvent<T>> getOnSuggestion() {
        return (EventHandler<SuggestionEvent<T>>)this.onSuggestionProperty().get();
    }
    
    public EventDispatchChain buildEventDispatchChain(final EventDispatchChain tail) {
        return super.buildEventDispatchChain(tail).append((EventDispatcher)this.eventHandlerManager);
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new AutoCompletePopupSkin((AutoCompletePopup<Object>)this);
    }
    
    public static class SuggestionEvent<TE> extends Event
    {
        public static final EventType<SuggestionEvent> SUGGESTION;
        private final TE suggestion;
        
        public SuggestionEvent(final TE suggestion) {
            super((EventType)SuggestionEvent.SUGGESTION);
            this.suggestion = suggestion;
        }
        
        public TE getSuggestion() {
            return this.suggestion;
        }
        
        static {
            SUGGESTION = new EventType("SUGGESTION");
        }
    }
}
