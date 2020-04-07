// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.action;

import java.lang.ref.WeakReference;
import javafx.event.ActionEvent;
import java.util.function.Consumer;
import javafx.event.EventHandler;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.binding.When;
import javafx.beans.binding.StringBinding;
import javafx.scene.control.Tooltip;
import javafx.collections.MapChangeListener;
import java.util.Map;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.css.Styleable;
import org.controlsfx.tools.Duplicatable;
import javafx.scene.image.ImageView;
import javafx.scene.control.SeparatorMenuItem;
import java.util.ArrayList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Pane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.CheckMenuItem;
import java.lang.annotation.Annotation;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.CheckBox;
import java.util.Arrays;
import java.util.Iterator;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import org.controlsfx.control.SegmentedButton;
import java.util.Collection;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Button;

public class ActionUtils
{
    public static Action ACTION_SEPARATOR;
    public static Action ACTION_SPAN;
    
    public static Button createButton(final Action action, final ActionTextBehavior textBehavior) {
        return configure(new Button(), action, textBehavior);
    }
    
    public static Button createButton(final Action action) {
        return configure(new Button(), action, ActionTextBehavior.SHOW);
    }
    
    public static ButtonBase configureButton(final Action action, final ButtonBase button) {
        return configure(button, action, ActionTextBehavior.SHOW);
    }
    
    public static void unconfigureButton(final ButtonBase button) {
        unconfigure(button);
    }
    
    public static MenuButton createMenuButton(final Action action, final ActionTextBehavior textBehavior) {
        return configure(new MenuButton(), action, textBehavior);
    }
    
    public static MenuButton createMenuButton(final Action action) {
        return configure(new MenuButton(), action, ActionTextBehavior.SHOW);
    }
    
    public static Hyperlink createHyperlink(final Action action) {
        return configure(new Hyperlink(), action, ActionTextBehavior.SHOW);
    }
    
    public static ToggleButton createToggleButton(final Action action, final ActionTextBehavior textBehavior) {
        return configure(new ToggleButton(), action, textBehavior);
    }
    
    public static ToggleButton createToggleButton(final Action action) {
        return createToggleButton(action, ActionTextBehavior.SHOW);
    }
    
    public static SegmentedButton createSegmentedButton(final ActionTextBehavior textBehavior, final Collection<? extends Action> actions) {
        final ObservableList<ToggleButton> buttons = (ObservableList<ToggleButton>)FXCollections.observableArrayList();
        for (final Action a : actions) {
            buttons.add((Object)createToggleButton(a, textBehavior));
        }
        return new SegmentedButton(buttons);
    }
    
    public static SegmentedButton createSegmentedButton(final Collection<? extends Action> actions) {
        return createSegmentedButton(ActionTextBehavior.SHOW, actions);
    }
    
    public static SegmentedButton createSegmentedButton(final ActionTextBehavior textBehavior, final Action... actions) {
        return createSegmentedButton(textBehavior, Arrays.asList(actions));
    }
    
    public static SegmentedButton createSegmentedButton(final Action... actions) {
        return createSegmentedButton(ActionTextBehavior.SHOW, Arrays.asList(actions));
    }
    
    public static CheckBox createCheckBox(final Action action) {
        return configure(new CheckBox(), action, ActionTextBehavior.SHOW);
    }
    
    public static RadioButton createRadioButton(final Action action) {
        return configure(new RadioButton(), action, ActionTextBehavior.SHOW);
    }
    
    public static MenuItem createMenuItem(final Action action) {
        final MenuItem menuItem = (MenuItem)(action.getClass().isAnnotationPresent(ActionCheck.class) ? new CheckMenuItem() : new MenuItem());
        return configure(menuItem, action);
    }
    
    public static MenuItem configureMenuItem(final Action action, final MenuItem menuItem) {
        return configure(menuItem, action);
    }
    
    public static void unconfigureMenuItem(final MenuItem menuItem) {
        unconfigure(menuItem);
    }
    
    public static Menu createMenu(final Action action) {
        return configure(new Menu(), action);
    }
    
    public static CheckMenuItem createCheckMenuItem(final Action action) {
        return configure(new CheckMenuItem(), action);
    }
    
    public static RadioMenuItem createRadioMenuItem(final Action action) {
        return configure(new RadioMenuItem((String)action.textProperty().get()), action);
    }
    
    public static ToolBar createToolBar(final Collection<? extends Action> actions, final ActionTextBehavior textBehavior) {
        return updateToolBar(new ToolBar(), actions, textBehavior);
    }
    
    public static ToolBar updateToolBar(final ToolBar toolbar, final Collection<? extends Action> actions, final ActionTextBehavior textBehavior) {
        toolbar.getItems().clear();
        for (final Action action : actions) {
            if (action instanceof ActionGroup) {
                final MenuButton menu = createMenuButton(action, textBehavior);
                menu.setFocusTraversable(false);
                menu.getItems().addAll((Collection)toMenuItems((Collection<? extends Action>)((ActionGroup)action).getActions()));
                toolbar.getItems().add((Object)menu);
            }
            else if (action == ActionUtils.ACTION_SEPARATOR) {
                toolbar.getItems().add((Object)new Separator());
            }
            else if (action == ActionUtils.ACTION_SPAN) {
                final Pane span = new Pane();
                HBox.setHgrow((Node)span, Priority.ALWAYS);
                VBox.setVgrow((Node)span, Priority.ALWAYS);
                toolbar.getItems().add((Object)span);
            }
            else {
                if (action == null) {
                    continue;
                }
                ButtonBase button;
                if (action.getClass().getAnnotation(ActionCheck.class) != null) {
                    button = (ButtonBase)createToggleButton(action, textBehavior);
                }
                else {
                    button = (ButtonBase)createButton(action, textBehavior);
                }
                button.setFocusTraversable(false);
                toolbar.getItems().add((Object)button);
            }
        }
        return toolbar;
    }
    
    public static MenuBar createMenuBar(final Collection<? extends Action> actions) {
        return updateMenuBar(new MenuBar(), actions);
    }
    
    public static MenuBar updateMenuBar(final MenuBar menuBar, final Collection<? extends Action> actions) {
        menuBar.getMenus().clear();
        for (final Action action : actions) {
            if (action != ActionUtils.ACTION_SEPARATOR) {
                if (action == ActionUtils.ACTION_SPAN) {
                    continue;
                }
                final Menu menu = createMenu(action);
                if (action instanceof ActionGroup) {
                    menu.getItems().addAll((Collection)toMenuItems((Collection<? extends Action>)((ActionGroup)action).getActions()));
                }
                else if (action == null) {}
                menuBar.getMenus().add((Object)menu);
            }
        }
        return menuBar;
    }
    
    public static ButtonBar createButtonBar(final Collection<? extends Action> actions) {
        return updateButtonBar(new ButtonBar(), actions);
    }
    
    public static ButtonBar updateButtonBar(final ButtonBar buttonBar, final Collection<? extends Action> actions) {
        buttonBar.getButtons().clear();
        for (final Action action : actions) {
            if (action instanceof ActionGroup) {
                continue;
            }
            if (action == ActionUtils.ACTION_SPAN || action == ActionUtils.ACTION_SEPARATOR) {
                continue;
            }
            if (action == null) {
                continue;
            }
            buttonBar.getButtons().add((Object)createButton(action, ActionTextBehavior.SHOW));
        }
        return buttonBar;
    }
    
    public static ContextMenu createContextMenu(final Collection<? extends Action> actions) {
        return updateContextMenu(new ContextMenu(), actions);
    }
    
    public static ContextMenu updateContextMenu(final ContextMenu menu, final Collection<? extends Action> actions) {
        menu.getItems().clear();
        menu.getItems().addAll((Collection)toMenuItems(actions));
        return menu;
    }
    
    private static Collection<MenuItem> toMenuItems(final Collection<? extends Action> actions) {
        final Collection<MenuItem> items = new ArrayList<MenuItem>();
        for (final Action action : actions) {
            if (action instanceof ActionGroup) {
                final Menu menu = createMenu(action);
                menu.getItems().addAll((Collection)toMenuItems((Collection<? extends Action>)((ActionGroup)action).getActions()));
                items.add((MenuItem)menu);
            }
            else if (action == ActionUtils.ACTION_SEPARATOR) {
                items.add((MenuItem)new SeparatorMenuItem());
            }
            else {
                if (action == null) {
                    continue;
                }
                if (action == ActionUtils.ACTION_SPAN) {
                    continue;
                }
                items.add(createMenuItem(action));
            }
        }
        return items;
    }
    
    private static Node copyNode(final Node node) {
        if (node instanceof ImageView) {
            return (Node)new ImageView(((ImageView)node).getImage());
        }
        if (node instanceof Duplicatable) {
            return ((Duplicatable)node).duplicate();
        }
        return null;
    }
    
    private static void bindStyle(final Styleable styleable, final Action action) {
        styleable.getStyleClass().addAll((Collection)action.getStyleClass());
        action.getStyleClass().addListener((ListChangeListener)new ListChangeListener<String>() {
            public void onChanged(final ListChangeListener.Change<? extends String> c) {
                while (c.next()) {
                    if (c.wasRemoved()) {
                        styleable.getStyleClass().removeAll((Collection)c.getRemoved());
                    }
                    if (c.wasAdded()) {
                        styleable.getStyleClass().addAll((Collection)c.getAddedSubList());
                    }
                }
            }
        });
    }
    
    private static <T extends ButtonBase> T configure(final T btn, final Action action, final ActionTextBehavior textBehavior) {
        if (action == null) {
            throw new NullPointerException("Action can not be null");
        }
        bindStyle((Styleable)btn, action);
        if (textBehavior == ActionTextBehavior.SHOW) {
            btn.textProperty().bind((ObservableValue)action.textProperty());
        }
        btn.disableProperty().bind((ObservableValue)action.disabledProperty());
        btn.graphicProperty().bind((ObservableValue)new ObjectBinding<Node>() {
            {
                this.bind(new Observable[] { action.graphicProperty() });
            }
            
            protected Node computeValue() {
                return copyNode((Node)action.graphicProperty().get());
            }
            
            public void removeListener(final InvalidationListener listener) {
                super.removeListener(listener);
                this.unbind(new Observable[] { action.graphicProperty() });
            }
        });
        btn.getProperties().putAll((Map)action.getProperties());
        action.getProperties().addListener((MapChangeListener)new ButtonPropertiesMapChangeListener((ButtonBase)btn, action));
        btn.tooltipProperty().bind((ObservableValue)new ObjectBinding<Tooltip>() {
            private Tooltip tooltip = new Tooltip();
            private StringBinding textBinding = new When((ObservableBooleanValue)action.longTextProperty().isEmpty()).then((ObservableStringValue)action.textProperty()).otherwise((ObservableStringValue)action.longTextProperty());
            
            {
                this.bind(new Observable[] { this.textBinding });
                this.tooltip.textProperty().bind((ObservableValue)this.textBinding);
            }
            
            protected Tooltip computeValue() {
                final String longText = this.textBinding.get();
                return (longText == null || this.textBinding.get().isEmpty()) ? null : this.tooltip;
            }
            
            public void removeListener(final InvalidationListener listener) {
                super.removeListener(listener);
                this.unbind(new Observable[] { action.longTextProperty() });
                this.tooltip.textProperty().unbind();
            }
        });
        if (btn instanceof ToggleButton) {
            ((ToggleButton)btn).selectedProperty().bindBidirectional((Property)action.selectedProperty());
        }
        btn.setOnAction((EventHandler)action);
        return btn;
    }
    
    private static void unconfigure(final ButtonBase btn) {
        if (btn == null || !(btn.getOnAction() instanceof Action)) {
            return;
        }
        final Action action = (Action)btn.getOnAction();
        btn.styleProperty().unbind();
        btn.textProperty().unbind();
        btn.disableProperty().unbind();
        btn.graphicProperty().unbind();
        action.getProperties().removeListener((MapChangeListener)new ButtonPropertiesMapChangeListener(btn, action));
        btn.tooltipProperty().unbind();
        if (btn instanceof ToggleButton) {
            ((ToggleButton)btn).selectedProperty().unbindBidirectional((Property)action.selectedProperty());
        }
        btn.setOnAction((EventHandler)null);
    }
    
    private static <T extends MenuItem> T configure(final T menuItem, final Action action) {
        if (action == null) {
            throw new NullPointerException("Action can not be null");
        }
        bindStyle((Styleable)menuItem, action);
        menuItem.textProperty().bind((ObservableValue)action.textProperty());
        menuItem.disableProperty().bind((ObservableValue)action.disabledProperty());
        menuItem.acceleratorProperty().bind((ObservableValue)action.acceleratorProperty());
        menuItem.graphicProperty().bind((ObservableValue)new ObjectBinding<Node>() {
            {
                this.bind(new Observable[] { action.graphicProperty() });
            }
            
            protected Node computeValue() {
                return copyNode((Node)action.graphicProperty().get());
            }
            
            public void removeListener(final InvalidationListener listener) {
                super.removeListener(listener);
                this.unbind(new Observable[] { action.graphicProperty() });
            }
        });
        menuItem.getProperties().putAll((Map)action.getProperties());
        action.getProperties().addListener((MapChangeListener)new MenuItemPropertiesMapChangeListener((MenuItem)menuItem, action));
        if (menuItem instanceof RadioMenuItem) {
            ((RadioMenuItem)menuItem).selectedProperty().bindBidirectional((Property)action.selectedProperty());
        }
        else if (menuItem instanceof CheckMenuItem) {
            ((CheckMenuItem)menuItem).selectedProperty().bindBidirectional((Property)action.selectedProperty());
        }
        menuItem.setOnAction((EventHandler)action);
        return menuItem;
    }
    
    private static void unconfigure(final MenuItem menuItem) {
        if (menuItem == null || !(menuItem.getOnAction() instanceof Action)) {
            return;
        }
        final Action action = (Action)menuItem.getOnAction();
        menuItem.styleProperty().unbind();
        menuItem.textProperty().unbind();
        menuItem.disableProperty().unbind();
        menuItem.acceleratorProperty().unbind();
        menuItem.graphicProperty().unbind();
        action.getProperties().removeListener((MapChangeListener)new MenuItemPropertiesMapChangeListener(menuItem, action));
        if (menuItem instanceof RadioMenuItem) {
            ((RadioMenuItem)menuItem).selectedProperty().unbindBidirectional((Property)action.selectedProperty());
        }
        else if (menuItem instanceof CheckMenuItem) {
            ((CheckMenuItem)menuItem).selectedProperty().unbindBidirectional((Property)action.selectedProperty());
        }
        menuItem.setOnAction((EventHandler)null);
    }
    
    static {
        ActionUtils.ACTION_SEPARATOR = new Action(null, null) {
            @Override
            public String toString() {
                return "Separator";
            }
        };
        ActionUtils.ACTION_SPAN = new Action(null, null) {
            @Override
            public String toString() {
                return "Span";
            }
        };
    }
    
    public enum ActionTextBehavior
    {
        SHOW, 
        HIDE;
    }
    
    private static class ButtonPropertiesMapChangeListener<T extends ButtonBase> implements MapChangeListener<Object, Object>
    {
        private final WeakReference<T> btnWeakReference;
        private final Action action;
        
        private ButtonPropertiesMapChangeListener(final T btn, final Action action) {
            this.btnWeakReference = new WeakReference<T>(btn);
            this.action = action;
        }
        
        public void onChanged(final MapChangeListener.Change<?, ?> change) {
            final T btn = this.btnWeakReference.get();
            if (btn == null) {
                this.action.getProperties().removeListener((MapChangeListener)this);
            }
            else {
                btn.getProperties().clear();
                btn.getProperties().putAll((Map)this.action.getProperties());
            }
        }
        
        @Override
        public boolean equals(final Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || this.getClass() != otherObject.getClass()) {
                return false;
            }
            final ButtonPropertiesMapChangeListener<?> otherListener = (ButtonPropertiesMapChangeListener<?>)otherObject;
            final T btn = this.btnWeakReference.get();
            final ButtonBase otherBtn = otherListener.btnWeakReference.get();
            if (btn != null) {
                if (btn.equals(otherBtn)) {
                    return this.action.equals(otherListener.action);
                }
            }
            else if (otherBtn == null) {
                return this.action.equals(otherListener.action);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            final T btn = this.btnWeakReference.get();
            int result = (btn != null) ? btn.hashCode() : 0;
            result = 31 * result + this.action.hashCode();
            return result;
        }
    }
    
    private static class MenuItemPropertiesMapChangeListener<T extends MenuItem> implements MapChangeListener<Object, Object>
    {
        private final WeakReference<T> menuItemWeakReference;
        private final Action action;
        
        private MenuItemPropertiesMapChangeListener(final T menuItem, final Action action) {
            this.menuItemWeakReference = new WeakReference<T>(menuItem);
            this.action = action;
        }
        
        public void onChanged(final MapChangeListener.Change<?, ?> change) {
            final T menuItem = this.menuItemWeakReference.get();
            if (menuItem == null) {
                this.action.getProperties().removeListener((MapChangeListener)this);
            }
            else {
                menuItem.getProperties().clear();
                menuItem.getProperties().putAll((Map)this.action.getProperties());
            }
        }
        
        @Override
        public boolean equals(final Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || this.getClass() != otherObject.getClass()) {
                return false;
            }
            final MenuItemPropertiesMapChangeListener<?> otherListener = (MenuItemPropertiesMapChangeListener<?>)otherObject;
            final T menuItem = this.menuItemWeakReference.get();
            final MenuItem otherMenuItem = otherListener.menuItemWeakReference.get();
            return (menuItem != null) ? menuItem.equals(otherMenuItem) : (otherMenuItem == null && this.action.equals(otherListener.action));
        }
        
        @Override
        public int hashCode() {
            final T menuItem = this.menuItemWeakReference.get();
            int result = (menuItem != null) ? menuItem.hashCode() : 0;
            result = 31 * result + this.action.hashCode();
            return result;
        }
    }
}
