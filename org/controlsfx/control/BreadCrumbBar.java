// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import javafx.event.Event;
import javafx.scene.control.Skin;
import javafx.event.EventDispatcher;
import javafx.event.EventDispatchChain;
import javafx.event.EventType;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import impl.org.controlsfx.skin.BreadCrumbBarSkin;
import javafx.event.EventHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.util.Callback;
import com.sun.javafx.event.EventHandlerManager;

public class BreadCrumbBar<T> extends ControlsFXControl
{
    private final EventHandlerManager eventHandlerManager;
    private final Callback<TreeItem<T>, Button> defaultCrumbNodeFactory;
    private final ObjectProperty<TreeItem<T>> selectedCrumb;
    private final BooleanProperty autoNavigation;
    private final ObjectProperty<Callback<TreeItem<T>, Button>> crumbFactory;
    private ObjectProperty<EventHandler<BreadCrumbActionEvent<T>>> onCrumbAction;
    private static final String DEFAULT_STYLE_CLASS = "bread-crumb-bar";
    
    public static <T> TreeItem<T> buildTreeModel(final T... crumbs) {
        TreeItem<T> subRoot = null;
        for (final T crumb : crumbs) {
            final TreeItem<T> currentNode = (TreeItem<T>)new TreeItem((Object)crumb);
            if (subRoot == null) {
                subRoot = currentNode;
            }
            else {
                subRoot.getChildren().add((Object)currentNode);
                subRoot = currentNode;
            }
        }
        return subRoot;
    }
    
    public BreadCrumbBar() {
        this(null);
    }
    
    public BreadCrumbBar(final TreeItem<T> selectedCrumb) {
        this.eventHandlerManager = new EventHandlerManager((Object)this);
        this.defaultCrumbNodeFactory = (Callback<TreeItem<T>, Button>)new Callback<TreeItem<T>, Button>() {
            public Button call(final TreeItem<T> crumb) {
                return new BreadCrumbBarSkin.BreadCrumbButton((crumb.getValue() != null) ? crumb.getValue().toString() : "");
            }
        };
        this.selectedCrumb = (ObjectProperty<TreeItem<T>>)new SimpleObjectProperty((Object)this, "selectedCrumb");
        this.autoNavigation = (BooleanProperty)new SimpleBooleanProperty((Object)this, "autoNavigationEnabled", true);
        this.crumbFactory = (ObjectProperty<Callback<TreeItem<T>, Button>>)new SimpleObjectProperty((Object)this, "crumbFactory");
        this.onCrumbAction = (ObjectProperty<EventHandler<BreadCrumbActionEvent<T>>>)new ObjectPropertyBase<EventHandler<BreadCrumbActionEvent<T>>>() {
            protected void invalidated() {
                BreadCrumbBar.this.eventHandlerManager.setEventHandler((EventType)BreadCrumbActionEvent.CRUMB_ACTION, (EventHandler)this.get());
            }
            
            public Object getBean() {
                return BreadCrumbBar.this;
            }
            
            public String getName() {
                return "onCrumbAction";
            }
        };
        this.getStyleClass().add((Object)"bread-crumb-bar");
        this.setSelectedCrumb(selectedCrumb);
        this.setCrumbFactory(this.defaultCrumbNodeFactory);
    }
    
    public EventDispatchChain buildEventDispatchChain(final EventDispatchChain tail) {
        return tail.prepend((EventDispatcher)this.eventHandlerManager);
    }
    
    public final ObjectProperty<TreeItem<T>> selectedCrumbProperty() {
        return this.selectedCrumb;
    }
    
    public final TreeItem<T> getSelectedCrumb() {
        return (TreeItem<T>)this.selectedCrumb.get();
    }
    
    public final void setSelectedCrumb(final TreeItem<T> selectedCrumb) {
        this.selectedCrumb.set((Object)selectedCrumb);
    }
    
    public final BooleanProperty autoNavigationEnabledProperty() {
        return this.autoNavigation;
    }
    
    public final boolean isAutoNavigationEnabled() {
        return this.autoNavigation.get();
    }
    
    public final void setAutoNavigationEnabled(final boolean enabled) {
        this.autoNavigation.set(enabled);
    }
    
    public final ObjectProperty<Callback<TreeItem<T>, Button>> crumbFactoryProperty() {
        return this.crumbFactory;
    }
    
    public final void setCrumbFactory(Callback<TreeItem<T>, Button> value) {
        if (value == null) {
            value = this.defaultCrumbNodeFactory;
        }
        this.crumbFactoryProperty().set((Object)value);
    }
    
    public final Callback<TreeItem<T>, Button> getCrumbFactory() {
        return (Callback<TreeItem<T>, Button>)this.crumbFactory.get();
    }
    
    public final ObjectProperty<EventHandler<BreadCrumbActionEvent<T>>> onCrumbActionProperty() {
        return this.onCrumbAction;
    }
    
    public final void setOnCrumbAction(final EventHandler<BreadCrumbActionEvent<T>> value) {
        this.onCrumbActionProperty().set((Object)value);
    }
    
    public final EventHandler<BreadCrumbActionEvent<T>> getOnCrumbAction() {
        return (EventHandler<BreadCrumbActionEvent<T>>)this.onCrumbActionProperty().get();
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new BreadCrumbBarSkin((BreadCrumbBar<Object>)this);
    }
    
    public String getUserAgentStylesheet() {
        return this.getUserAgentStylesheet(BreadCrumbBar.class, "breadcrumbbar.css");
    }
    
    public static class BreadCrumbActionEvent<TE> extends Event
    {
        public static final EventType<BreadCrumbActionEvent> CRUMB_ACTION;
        private final TreeItem<TE> selectedCrumb;
        
        public BreadCrumbActionEvent(final TreeItem<TE> selectedCrumb) {
            super((EventType)BreadCrumbActionEvent.CRUMB_ACTION);
            this.selectedCrumb = selectedCrumb;
        }
        
        public TreeItem<TE> getSelectedCrumb() {
            return this.selectedCrumb;
        }
        
        static {
            CRUMB_ACTION = new EventType("CRUMB_ACTION");
        }
    }
}
