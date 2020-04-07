// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.textfield;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventDispatcher;
import javafx.event.EventDispatchChain;
import javafx.event.EventType;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.control.Skin;
import javafx.scene.control.ListView;
import impl.org.controlsfx.skin.AutoCompletePopupSkin;
import javafx.event.Event;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.util.StringConverter;
import com.sun.javafx.event.EventHandlerManager;
import javafx.event.EventHandler;
import javafx.beans.property.ObjectProperty;
import java.util.Collection;
import javafx.util.Callback;
import impl.org.controlsfx.skin.AutoCompletePopup;
import javafx.scene.Node;
import javafx.event.EventTarget;

public abstract class AutoCompletionBinding<T> implements EventTarget
{
    private final Node completionTarget;
    private final AutoCompletePopup<T> autoCompletionPopup;
    private final Object suggestionsTaskLock;
    private FetchSuggestionsTask suggestionsTask;
    private Callback<ISuggestionRequest, Collection<T>> suggestionProvider;
    private boolean ignoreInputChanges;
    private long delay;
    private ObjectProperty<EventHandler<AutoCompletionEvent<T>>> onAutoCompleted;
    final EventHandlerManager eventHandlerManager;
    
    protected AutoCompletionBinding(final Node completionTarget, final Callback<ISuggestionRequest, Collection<T>> suggestionProvider, final StringConverter<T> converter) {
        this.suggestionsTaskLock = new Object();
        this.suggestionsTask = null;
        this.suggestionProvider = null;
        this.ignoreInputChanges = false;
        this.delay = 250L;
        this.eventHandlerManager = new EventHandlerManager((Object)this);
        this.completionTarget = completionTarget;
        this.suggestionProvider = suggestionProvider;
        (this.autoCompletionPopup = new AutoCompletePopup<T>()).setConverter(converter);
        this.autoCompletionPopup.setOnSuggestion((javafx.event.EventHandler<AutoCompletePopup.SuggestionEvent<T>>)(sce -> {
            try {
                this.setIgnoreInputChanges(true);
                this.completeUserInput(sce.getSuggestion());
                this.fireAutoCompletion(sce.getSuggestion());
                this.hidePopup();
            }
            finally {
                this.setIgnoreInputChanges(false);
            }
        }));
    }
    
    public void setHideOnEscape(final boolean value) {
        this.autoCompletionPopup.setHideOnEscape(value);
    }
    
    public final void setUserInput(final String userText) {
        if (!this.isIgnoreInputChanges()) {
            this.onUserInputChanged(userText);
        }
    }
    
    public final void setDelay(final long delay) {
        this.delay = delay;
    }
    
    public Node getCompletionTarget() {
        return this.completionTarget;
    }
    
    public abstract void dispose();
    
    public final void setVisibleRowCount(final int value) {
        this.autoCompletionPopup.setVisibleRowCount(value);
    }
    
    public final int getVisibleRowCount() {
        return this.autoCompletionPopup.getVisibleRowCount();
    }
    
    public final IntegerProperty visibleRowCountProperty() {
        return this.autoCompletionPopup.visibleRowCountProperty();
    }
    
    public final void setPrefWidth(final double value) {
        this.autoCompletionPopup.setPrefWidth(value);
    }
    
    public final double getPrefWidth() {
        return this.autoCompletionPopup.getPrefWidth();
    }
    
    public final DoubleProperty prefWidthProperty() {
        return this.autoCompletionPopup.prefWidthProperty();
    }
    
    public final void setMinWidth(final double value) {
        this.autoCompletionPopup.setMinWidth(value);
    }
    
    public final double getMinWidth() {
        return this.autoCompletionPopup.getMinWidth();
    }
    
    public final DoubleProperty minWidthProperty() {
        return this.autoCompletionPopup.minWidthProperty();
    }
    
    public final void setMaxWidth(final double value) {
        this.autoCompletionPopup.setMaxWidth(value);
    }
    
    public final double getMaxWidth() {
        return this.autoCompletionPopup.getMaxWidth();
    }
    
    public final DoubleProperty maxWidthProperty() {
        return this.autoCompletionPopup.maxWidthProperty();
    }
    
    protected abstract void completeUserInput(final T p0);
    
    protected void showPopup() {
        this.autoCompletionPopup.show(this.completionTarget);
        this.selectFirstSuggestion(this.autoCompletionPopup);
    }
    
    protected void hidePopup() {
        this.autoCompletionPopup.hide();
    }
    
    protected void fireAutoCompletion(final T completion) {
        Event.fireEvent((EventTarget)this, (Event)new AutoCompletionEvent<Object>(completion));
    }
    
    private void selectFirstSuggestion(final AutoCompletePopup<?> autoCompletionPopup) {
        final Skin<?> skin = (Skin<?>)autoCompletionPopup.getSkin();
        if (skin instanceof AutoCompletePopupSkin) {
            final AutoCompletePopupSkin<?> au = (AutoCompletePopupSkin<?>)(AutoCompletePopupSkin)skin;
            final ListView<?> li = (ListView<?>)au.getNode();
            if (li.getItems() != null && !li.getItems().isEmpty()) {
                li.getSelectionModel().select(0);
            }
        }
    }
    
    private final void onUserInputChanged(final String userText) {
        synchronized (this.suggestionsTaskLock) {
            if (this.suggestionsTask != null && this.suggestionsTask.isRunning()) {
                this.suggestionsTask.cancel();
            }
            this.suggestionsTask = new FetchSuggestionsTask(userText, this.delay);
            new Thread((Runnable)this.suggestionsTask).start();
        }
    }
    
    private boolean isIgnoreInputChanges() {
        return this.ignoreInputChanges;
    }
    
    private void setIgnoreInputChanges(final boolean state) {
        this.ignoreInputChanges = state;
    }
    
    public final void setOnAutoCompleted(final EventHandler<AutoCompletionEvent<T>> value) {
        this.onAutoCompletedProperty().set((Object)value);
    }
    
    public final EventHandler<AutoCompletionEvent<T>> getOnAutoCompleted() {
        return (EventHandler<AutoCompletionEvent<T>>)((this.onAutoCompleted == null) ? null : ((EventHandler)this.onAutoCompleted.get()));
    }
    
    public final ObjectProperty<EventHandler<AutoCompletionEvent<T>>> onAutoCompletedProperty() {
        if (this.onAutoCompleted == null) {
            this.onAutoCompleted = (ObjectProperty<EventHandler<AutoCompletionEvent<T>>>)new ObjectPropertyBase<EventHandler<AutoCompletionEvent<T>>>() {
                protected void invalidated() {
                    AutoCompletionBinding.this.eventHandlerManager.setEventHandler((EventType)AutoCompletionEvent.AUTO_COMPLETED, (EventHandler)this.get());
                }
                
                public Object getBean() {
                    return AutoCompletionBinding.this;
                }
                
                public String getName() {
                    return "onAutoCompleted";
                }
            };
        }
        return this.onAutoCompleted;
    }
    
    public <E extends Event> void addEventHandler(final EventType<E> eventType, final EventHandler<E> eventHandler) {
        this.eventHandlerManager.addEventHandler((EventType)eventType, (EventHandler)eventHandler);
    }
    
    public <E extends Event> void removeEventHandler(final EventType<E> eventType, final EventHandler<E> eventHandler) {
        this.eventHandlerManager.removeEventHandler((EventType)eventType, (EventHandler)eventHandler);
    }
    
    public EventDispatchChain buildEventDispatchChain(final EventDispatchChain tail) {
        return tail.prepend((EventDispatcher)this.eventHandlerManager);
    }
    
    private class FetchSuggestionsTask extends Task<Void> implements ISuggestionRequest
    {
        private final String userText;
        private final long delay;
        
        public FetchSuggestionsTask(final String userText, final long delay) {
            this.userText = userText;
            this.delay = delay;
        }
        
        protected Void call() throws Exception {
            final Callback<ISuggestionRequest, Collection<T>> provider = AutoCompletionBinding.this.suggestionProvider;
            if (provider != null) {
                final long startTime = System.currentTimeMillis();
                final long sleepTime = startTime + this.delay - System.currentTimeMillis();
                if (sleepTime > 0L && !this.isCancelled()) {
                    Thread.sleep(sleepTime);
                }
                if (!this.isCancelled()) {
                    final Collection<T> fetchedSuggestions = (Collection<T>)provider.call((Object)this);
                    final Collection all;
                    Platform.runLater(() -> {
                        if (all != null && !all.isEmpty()) {
                            AutoCompletionBinding.this.autoCompletionPopup.getSuggestions().setAll(all);
                            AutoCompletionBinding.this.showPopup();
                        }
                        else {
                            AutoCompletionBinding.this.hidePopup();
                        }
                        return;
                    });
                }
            }
            else {
                AutoCompletionBinding.this.hidePopup();
            }
            return null;
        }
        
        public String getUserText() {
            return this.userText;
        }
    }
    
    public static class AutoCompletionEvent<TE> extends Event
    {
        public static final EventType<AutoCompletionEvent> AUTO_COMPLETED;
        private final TE completion;
        
        public AutoCompletionEvent(final TE completion) {
            super((EventType)AutoCompletionEvent.AUTO_COMPLETED);
            this.completion = completion;
        }
        
        public TE getCompletion() {
            return this.completion;
        }
        
        static {
            AUTO_COMPLETED = new EventType("AUTO_COMPLETED");
        }
    }
    
    public interface ISuggestionRequest
    {
        boolean isCancelled();
        
        String getUserText();
    }
}
