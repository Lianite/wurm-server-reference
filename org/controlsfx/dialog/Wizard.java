// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.dialog;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import org.controlsfx.tools.ValueExtractor;
import java.util.Iterator;
import impl.org.controlsfx.ImplUtils;
import java.util.function.BooleanSupplier;
import javafx.geometry.Rectangle2D;
import javafx.collections.ObservableList;
import java.util.List;
import javafx.stage.Screen;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import java.util.Map;
import java.util.HashMap;
import javafx.scene.Node;
import javafx.stage.Window;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ButtonBar;
import impl.org.controlsfx.i18n.Localization;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.property.BooleanProperty;
import java.util.Optional;
import java.util.Stack;
import javafx.collections.ObservableMap;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class Wizard
{
    private Dialog<ButtonType> dialog;
    private final ObservableMap<String, Object> settings;
    private final Stack<WizardPane> pageHistory;
    private Optional<WizardPane> currentPage;
    private final BooleanProperty invalidProperty;
    private final BooleanProperty readSettingsProperty;
    private final ButtonType BUTTON_PREVIOUS;
    private final EventHandler<ActionEvent> BUTTON_PREVIOUS_ACTION_HANDLER;
    private final ButtonType BUTTON_NEXT;
    private final EventHandler<ActionEvent> BUTTON_NEXT_ACTION_HANDLER;
    private final StringProperty titleProperty;
    private ObjectProperty<Flow> flow;
    private static final Object USER_DATA_KEY;
    private ObservableMap<Object, Object> properties;
    private int settingCounter;
    
    public Wizard() {
        this(null);
    }
    
    public Wizard(final Object owner) {
        this(owner, "");
    }
    
    public Wizard(final Object owner, final String title) {
        this.settings = (ObservableMap<String, Object>)FXCollections.observableHashMap();
        this.pageHistory = new Stack<WizardPane>();
        this.currentPage = Optional.empty();
        this.invalidProperty = (BooleanProperty)new SimpleBooleanProperty(false);
        this.readSettingsProperty = (BooleanProperty)new SimpleBooleanProperty(true);
        this.BUTTON_PREVIOUS = new ButtonType(Localization.localize(Localization.asKey("wizard.previous.button")), ButtonBar.ButtonData.BACK_PREVIOUS);
        this.BUTTON_PREVIOUS_ACTION_HANDLER = (EventHandler<ActionEvent>)(actionEvent -> {
            actionEvent.consume();
            this.currentPage = Optional.ofNullable(this.pageHistory.isEmpty() ? null : this.pageHistory.pop());
            this.updatePage(this.dialog, false);
        });
        this.BUTTON_NEXT = new ButtonType(Localization.localize(Localization.asKey("wizard.next.button")), ButtonBar.ButtonData.NEXT_FORWARD);
        this.BUTTON_NEXT_ACTION_HANDLER = (EventHandler<ActionEvent>)(actionEvent -> {
            actionEvent.consume();
            final WizardPane wizardPane;
            this.currentPage.ifPresent(page -> wizardPane = this.pageHistory.push(page));
            this.currentPage = this.getFlow().advance(this.currentPage.orElse(null));
            this.updatePage(this.dialog, true);
        });
        this.titleProperty = (StringProperty)new SimpleStringProperty();
        this.flow = (ObjectProperty<Flow>)new SimpleObjectProperty<Flow>((Flow)new LinearFlow(new WizardPane[0])) {
            protected void invalidated() {
                Wizard.this.updatePage(Wizard.this.dialog, false);
            }
            
            public void set(final Flow flow) {
                super.set((Object)flow);
                Wizard.this.pageHistory.clear();
                if (flow != null) {
                    Wizard.this.currentPage = flow.advance(Wizard.this.currentPage.orElse(null));
                    Wizard.this.updatePage(Wizard.this.dialog, true);
                }
            }
        };
        this.invalidProperty.addListener((o, ov, nv) -> this.validateActionState());
        this.dialog = (Dialog<ButtonType>)new Dialog();
        this.dialog.titleProperty().bind((ObservableValue)this.titleProperty);
        this.setTitle(title);
        Window window = null;
        if (owner instanceof Window) {
            window = (Window)owner;
        }
        else if (owner instanceof Node) {
            window = ((Node)owner).getScene().getWindow();
        }
        this.dialog.initOwner(window);
    }
    
    public final Optional<ButtonType> showAndWait() {
        return (Optional<ButtonType>)this.dialog.showAndWait();
    }
    
    public final ObjectProperty<ButtonType> resultProperty() {
        return (ObjectProperty<ButtonType>)this.dialog.resultProperty();
    }
    
    public final ObservableMap<String, Object> getSettings() {
        return this.settings;
    }
    
    public final StringProperty titleProperty() {
        return this.titleProperty;
    }
    
    public final String getTitle() {
        return (String)this.titleProperty.get();
    }
    
    public final void setTitle(final String title) {
        this.titleProperty.set((Object)title);
    }
    
    public final ObjectProperty<Flow> flowProperty() {
        return this.flow;
    }
    
    public final Flow getFlow() {
        return (Flow)this.flow.get();
    }
    
    public final void setFlow(final Flow flow) {
        this.flow.set((Object)flow);
    }
    
    public final ObservableMap<Object, Object> getProperties() {
        if (this.properties == null) {
            this.properties = (ObservableMap<Object, Object>)FXCollections.observableMap((Map)new HashMap());
        }
        return this.properties;
    }
    
    public boolean hasProperties() {
        return this.properties != null && !this.properties.isEmpty();
    }
    
    public void setUserData(final Object value) {
        this.getProperties().put(Wizard.USER_DATA_KEY, value);
    }
    
    public Object getUserData() {
        return this.getProperties().get(Wizard.USER_DATA_KEY);
    }
    
    public final void setInvalid(final boolean invalid) {
        this.invalidProperty.set(invalid);
    }
    
    public final boolean isInvalid() {
        return this.invalidProperty.get();
    }
    
    public final BooleanProperty invalidProperty() {
        return this.invalidProperty;
    }
    
    public final void setReadSettings(final boolean readSettings) {
        this.readSettingsProperty.set(readSettings);
    }
    
    public final boolean isReadSettings() {
        return this.readSettingsProperty.get();
    }
    
    public final BooleanProperty readSettingsProperty() {
        return this.readSettingsProperty;
    }
    
    private void updatePage(final Dialog<ButtonType> dialog, final boolean advancing) {
        final Flow flow = this.getFlow();
        if (flow == null) {
            return;
        }
        final Optional<WizardPane> prevPage = Optional.ofNullable(this.pageHistory.isEmpty() ? null : this.pageHistory.peek());
        prevPage.ifPresent(page -> {
            if (advancing && this.isReadSettings()) {
                this.readSettings(page);
            }
            page.onExitingPage(this);
            return;
        });
        final List<ButtonType> buttons;
        final Button button;
        final Button button2;
        final Pane parentOfCurrentPage;
        final double previousX;
        final double previousY;
        final double previousWidth;
        final double previousHeight;
        final Window wizard;
        final double newWidth;
        final double newHeight;
        int newX;
        int newY;
        final ObservableList<Screen> screens;
        final Screen screen;
        final Rectangle2D scrBounds;
        final int minX;
        final int maxX;
        final int minY;
        final int maxY;
        this.currentPage.ifPresent(currentPage -> {
            buttons = (List<ButtonType>)currentPage.getButtonTypes();
            if (!buttons.contains(this.BUTTON_PREVIOUS)) {
                buttons.add(this.BUTTON_PREVIOUS);
                button = (Button)currentPage.lookupButton(this.BUTTON_PREVIOUS);
                button.addEventFilter(ActionEvent.ACTION, (EventHandler)this.BUTTON_PREVIOUS_ACTION_HANDLER);
            }
            if (!buttons.contains(this.BUTTON_NEXT)) {
                buttons.add(this.BUTTON_NEXT);
                button2 = (Button)currentPage.lookupButton(this.BUTTON_NEXT);
                button2.addEventFilter(ActionEvent.ACTION, (EventHandler)this.BUTTON_NEXT_ACTION_HANDLER);
            }
            if (!buttons.contains(ButtonType.FINISH)) {
                buttons.add(ButtonType.FINISH);
            }
            if (!buttons.contains(ButtonType.CANCEL)) {
                buttons.add(ButtonType.CANCEL);
            }
            currentPage.onEnteringPage(this);
            if (currentPage.getParent() != null && currentPage.getParent() instanceof Pane) {
                parentOfCurrentPage = (Pane)currentPage.getParent();
                parentOfCurrentPage.getChildren().remove((Object)currentPage);
            }
            previousX = dialog.getX();
            previousY = dialog.getY();
            previousWidth = dialog.getWidth();
            previousHeight = dialog.getHeight();
            dialog.setDialogPane((DialogPane)currentPage);
            wizard = currentPage.getScene().getWindow();
            wizard.sizeToScene();
            if (!Double.isNaN(previousX) && !Double.isNaN(previousY)) {
                newWidth = dialog.getWidth();
                newHeight = dialog.getHeight();
                newX = (int)(previousX + previousWidth / 2.0 - newWidth / 2.0);
                newY = (int)(previousY + previousHeight / 2.0 - newHeight / 2.0);
                screens = (ObservableList<Screen>)Screen.getScreensForRectangle(previousX, previousY, 1.0, 1.0);
                screen = (Screen)(screens.isEmpty() ? Screen.getPrimary() : screens.get(0));
                scrBounds = screen.getBounds();
                minX = (int)Math.round(scrBounds.getMinX());
                maxX = (int)Math.round(scrBounds.getMaxX());
                minY = (int)Math.round(scrBounds.getMinY());
                maxY = (int)Math.round(scrBounds.getMaxY());
                if (newX + newWidth > maxX) {
                    newX = maxX - (int)Math.round(newWidth);
                }
                if (newY + newHeight > maxY) {
                    newY = maxY - (int)Math.round(newHeight);
                }
                if (newX < minX) {
                    newX = minX;
                }
                if (newY < minY) {
                    newY = minY;
                }
                dialog.setX((double)newX);
                dialog.setY((double)newY);
            }
            return;
        });
        this.validateActionState();
    }
    
    private void validateActionState() {
        final List<ButtonType> currentPaneButtons = (List<ButtonType>)this.dialog.getDialogPane().getButtonTypes();
        if (this.getFlow().canAdvance(this.currentPage.orElse(null))) {
            currentPaneButtons.remove(ButtonType.FINISH);
        }
        else {
            currentPaneButtons.remove(this.BUTTON_NEXT);
        }
        this.validateButton(this.BUTTON_PREVIOUS, () -> this.pageHistory.isEmpty());
        this.validateButton(this.BUTTON_NEXT, () -> this.invalidProperty.get());
        this.validateButton(ButtonType.FINISH, () -> this.invalidProperty.get());
    }
    
    private void validateButton(final ButtonType buttonType, final BooleanSupplier condition) {
        final Button btn = (Button)this.dialog.getDialogPane().lookupButton(buttonType);
        if (btn != null) {
            final Node focusOwner = (btn.getScene() != null) ? btn.getScene().getFocusOwner() : null;
            btn.setDisable(condition.getAsBoolean());
            if (focusOwner != null) {
                focusOwner.requestFocus();
            }
        }
    }
    
    private void readSettings(final WizardPane page) {
        this.settingCounter = 0;
        this.checkNode(page.getContent());
    }
    
    private boolean checkNode(final Node n) {
        final boolean success = this.readSetting(n);
        if (success) {
            return true;
        }
        final List<Node> children = ImplUtils.getChildren(n, true);
        boolean childSuccess = false;
        for (final Node child : children) {
            childSuccess |= this.checkNode(child);
        }
        return childSuccess;
    }
    
    private boolean readSetting(final Node n) {
        if (n == null) {
            return false;
        }
        final Object setting = ValueExtractor.getValue(n);
        if (setting != null) {
            String settingName = n.getId();
            if (settingName == null || settingName.isEmpty()) {
                settingName = "page_.setting_" + this.settingCounter;
            }
            this.getSettings().put((Object)settingName, setting);
            ++this.settingCounter;
        }
        return setting != null;
    }
    
    Dialog<ButtonType> getDialog() {
        return this.dialog;
    }
    
    static {
        USER_DATA_KEY = new Object();
    }
    
    public static class LinearFlow implements Flow
    {
        private final List<WizardPane> pages;
        
        public LinearFlow(final Collection<WizardPane> pages) {
            this.pages = new ArrayList<WizardPane>(pages);
        }
        
        public LinearFlow(final WizardPane... pages) {
            this(Arrays.asList(pages));
        }
        
        @Override
        public Optional<WizardPane> advance(final WizardPane currentPage) {
            int pageIndex = this.pages.indexOf(currentPage);
            return Optional.ofNullable(this.pages.get(++pageIndex));
        }
        
        @Override
        public boolean canAdvance(final WizardPane currentPage) {
            final int pageIndex = this.pages.indexOf(currentPage);
            return this.pages.size() - 1 > pageIndex;
        }
    }
    
    public interface Flow
    {
        Optional<WizardPane> advance(final WizardPane p0);
        
        boolean canAdvance(final WizardPane p0);
    }
}
