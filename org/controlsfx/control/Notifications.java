// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import java.lang.ref.WeakReference;
import javafx.event.EventTarget;
import javafx.scene.input.MouseEvent;
import javafx.animation.Transition;
import java.util.LinkedList;
import javafx.event.Event;
import javafx.animation.KeyFrame;
import javafx.beans.value.WritableValue;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import java.util.Collection;
import impl.org.controlsfx.skin.NotificationBar;
import javafx.stage.PopupWindow;
import javafx.geometry.Rectangle2D;
import java.util.HashMap;
import javafx.animation.ParallelTransition;
import javafx.stage.Popup;
import java.util.Map;
import javafx.scene.image.ImageView;
import org.controlsfx.tools.Utils;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import java.util.List;
import javafx.stage.Screen;
import javafx.stage.Window;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import javafx.geometry.Pos;
import org.controlsfx.control.action.Action;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public class Notifications
{
    private static final String STYLE_CLASS_DARK = "dark";
    private String title;
    private String text;
    private Node graphic;
    private ObservableList<Action> actions;
    private Pos position;
    private Duration hideAfterDuration;
    private boolean hideCloseButton;
    private EventHandler<ActionEvent> onAction;
    private Window owner;
    private Screen screen;
    private List<String> styleClass;
    
    private Notifications() {
        this.actions = (ObservableList<Action>)FXCollections.observableArrayList();
        this.position = Pos.BOTTOM_RIGHT;
        this.hideAfterDuration = Duration.seconds(5.0);
        this.screen = Screen.getPrimary();
        this.styleClass = new ArrayList<String>();
    }
    
    public static Notifications create() {
        return new Notifications();
    }
    
    public Notifications text(final String text) {
        this.text = text;
        return this;
    }
    
    public Notifications title(final String title) {
        this.title = title;
        return this;
    }
    
    public Notifications graphic(final Node graphic) {
        this.graphic = graphic;
        return this;
    }
    
    public Notifications position(final Pos position) {
        this.position = position;
        return this;
    }
    
    public Notifications owner(final Object owner) {
        if (owner instanceof Screen) {
            this.screen = (Screen)owner;
        }
        else {
            this.owner = Utils.getWindow(owner);
        }
        return this;
    }
    
    public Notifications hideAfter(final Duration duration) {
        this.hideAfterDuration = duration;
        return this;
    }
    
    public Notifications onAction(final EventHandler<ActionEvent> onAction) {
        this.onAction = onAction;
        return this;
    }
    
    public Notifications darkStyle() {
        this.styleClass.add("dark");
        return this;
    }
    
    public Notifications hideCloseButton() {
        this.hideCloseButton = true;
        return this;
    }
    
    public Notifications action(final Action... actions) {
        this.actions = (ObservableList<Action>)((actions == null) ? FXCollections.observableArrayList() : FXCollections.observableArrayList((Object[])actions));
        return this;
    }
    
    public void showWarning() {
        this.graphic((Node)new ImageView(Notifications.class.getResource("/org/controlsfx/dialog/dialog-warning.png").toExternalForm()));
        this.show();
    }
    
    public void showInformation() {
        this.graphic((Node)new ImageView(Notifications.class.getResource("/org/controlsfx/dialog/dialog-information.png").toExternalForm()));
        this.show();
    }
    
    public void showError() {
        this.graphic((Node)new ImageView(Notifications.class.getResource("/org/controlsfx/dialog/dialog-error.png").toExternalForm()));
        this.show();
    }
    
    public void showConfirm() {
        this.graphic((Node)new ImageView(Notifications.class.getResource("/org/controlsfx/dialog/dialog-confirm.png").toExternalForm()));
        this.show();
    }
    
    public void show() {
        NotificationPopupHandler.getInstance().show(this);
    }
    
    private static final class NotificationPopupHandler
    {
        private static final NotificationPopupHandler INSTANCE;
        private double startX;
        private double startY;
        private double screenWidth;
        private double screenHeight;
        private final Map<Pos, List<Popup>> popupsMap;
        private final double padding = 15.0;
        private ParallelTransition parallelTransition;
        private boolean isShowing;
        
        private NotificationPopupHandler() {
            this.popupsMap = new HashMap<Pos, List<Popup>>();
            this.parallelTransition = new ParallelTransition();
            this.isShowing = false;
        }
        
        static final NotificationPopupHandler getInstance() {
            return NotificationPopupHandler.INSTANCE;
        }
        
        public void show(final Notifications notification) {
            Window window;
            if (notification.owner == null) {
                final Rectangle2D screenBounds = notification.screen.getVisualBounds();
                this.startX = screenBounds.getMinX();
                this.startY = screenBounds.getMinY();
                this.screenWidth = screenBounds.getWidth();
                this.screenHeight = screenBounds.getHeight();
                window = Utils.getWindow(null);
            }
            else {
                this.startX = notification.owner.getX();
                this.startY = notification.owner.getY();
                this.screenWidth = notification.owner.getWidth();
                this.screenHeight = notification.owner.getHeight();
                window = notification.owner;
            }
            this.show(window, notification);
        }
        
        private void show(final Window owner, final Notifications notification) {
            Window ownerWindow;
            for (ownerWindow = owner; ownerWindow instanceof PopupWindow; ownerWindow = ((PopupWindow)ownerWindow).getOwnerWindow()) {}
            final Scene ownerScene = ownerWindow.getScene();
            if (ownerScene != null) {
                final String stylesheetUrl = Notifications.class.getResource("notificationpopup.css").toExternalForm();
                if (!ownerScene.getStylesheets().contains((Object)stylesheetUrl)) {
                    ownerScene.getStylesheets().add(0, (Object)stylesheetUrl);
                }
            }
            final Popup popup = new Popup();
            popup.setAutoFix(false);
            final Pos p = notification.position;
            final NotificationBar notificationBar = new NotificationBar() {
                @Override
                public String getTitle() {
                    return notification.title;
                }
                
                @Override
                public String getText() {
                    return notification.text;
                }
                
                @Override
                public Node getGraphic() {
                    return notification.graphic;
                }
                
                @Override
                public ObservableList<Action> getActions() {
                    return notification.actions;
                }
                
                @Override
                public boolean isShowing() {
                    return NotificationPopupHandler.this.isShowing;
                }
                
                protected double computeMinWidth(final double height) {
                    final String text = this.getText();
                    final Node graphic = this.getGraphic();
                    if ((text == null || text.isEmpty()) && graphic != null) {
                        return graphic.minWidth(height);
                    }
                    return 400.0;
                }
                
                @Override
                protected double computeMinHeight(final double width) {
                    final String text = this.getText();
                    final Node graphic = this.getGraphic();
                    if ((text == null || text.isEmpty()) && graphic != null) {
                        return graphic.minHeight(width);
                    }
                    return 100.0;
                }
                
                @Override
                public boolean isShowFromTop() {
                    return NotificationPopupHandler.this.isShowFromTop(notification.position);
                }
                
                @Override
                public void hide() {
                    NotificationPopupHandler.this.isShowing = false;
                    NotificationPopupHandler.this.createHideTimeline(popup, this, p, Duration.ZERO).play();
                }
                
                @Override
                public boolean isCloseButtonVisible() {
                    return !notification.hideCloseButton;
                }
                
                @Override
                public double getContainerHeight() {
                    return NotificationPopupHandler.this.startY + NotificationPopupHandler.this.screenHeight;
                }
                
                @Override
                public void relocateInParent(final double x, final double y) {
                    switch (p) {
                        case BOTTOM_LEFT:
                        case BOTTOM_CENTER:
                        case BOTTOM_RIGHT: {
                            popup.setAnchorY(y - 15.0);
                            break;
                        }
                    }
                }
            };
            notificationBar.getStyleClass().addAll((Collection)notification.styleClass);
            notificationBar.setOnMouseClicked(e -> {
                if (notification.onAction != null) {
                    final ActionEvent actionEvent = new ActionEvent((Object)notificationBar, (EventTarget)notificationBar);
                    notification.onAction.handle((Event)actionEvent);
                    this.createHideTimeline(popup, notificationBar, p, Duration.ZERO).play();
                }
            });
            popup.getContent().add((Object)notificationBar);
            popup.show(owner, 0.0, 0.0);
            double anchorX = 0.0;
            double anchorY = 0.0;
            final double barWidth = notificationBar.getWidth();
            final double barHeight = notificationBar.getHeight();
            switch (p) {
                case BOTTOM_LEFT:
                case TOP_LEFT:
                case CENTER_LEFT: {
                    anchorX = 15.0 + this.startX;
                    break;
                }
                case BOTTOM_CENTER:
                case TOP_CENTER:
                case CENTER: {
                    anchorX = this.startX + this.screenWidth / 2.0 - barWidth / 2.0 - 7.5;
                    break;
                }
                default: {
                    anchorX = this.startX + this.screenWidth - barWidth - 15.0;
                    break;
                }
            }
            switch (p) {
                case TOP_LEFT:
                case TOP_CENTER:
                case TOP_RIGHT: {
                    anchorY = 15.0 + this.startY;
                    break;
                }
                case CENTER_LEFT:
                case CENTER:
                case CENTER_RIGHT: {
                    anchorY = this.startY + this.screenHeight / 2.0 - barHeight / 2.0 - 7.5;
                    break;
                }
                default: {
                    anchorY = this.startY + this.screenHeight - barHeight - 15.0;
                    break;
                }
            }
            popup.setAnchorX(anchorX);
            popup.setAnchorY(anchorY);
            this.isShowing = true;
            notificationBar.doShow();
            this.addPopupToMap(p, popup);
            final Timeline timeline = this.createHideTimeline(popup, notificationBar, p, notification.hideAfterDuration);
            timeline.play();
        }
        
        private void hide(final Popup popup, final Pos p) {
            popup.hide();
            this.removePopupFromMap(p, popup);
        }
        
        private Timeline createHideTimeline(final Popup popup, final NotificationBar bar, final Pos p, final Duration startDelay) {
            final KeyValue fadeOutBegin = new KeyValue((WritableValue)bar.opacityProperty(), (Object)1.0);
            final KeyValue fadeOutEnd = new KeyValue((WritableValue)bar.opacityProperty(), (Object)0.0);
            final KeyFrame kfBegin = new KeyFrame(Duration.ZERO, new KeyValue[] { fadeOutBegin });
            final KeyFrame kfEnd = new KeyFrame(Duration.millis(500.0), new KeyValue[] { fadeOutEnd });
            final Timeline timeline = new Timeline(new KeyFrame[] { kfBegin, kfEnd });
            timeline.setDelay(startDelay);
            timeline.setOnFinished((EventHandler)new EventHandler<ActionEvent>() {
                public void handle(final ActionEvent e) {
                    NotificationPopupHandler.this.hide(popup, p);
                }
            });
            return timeline;
        }
        
        private void addPopupToMap(final Pos p, final Popup popup) {
            List<Popup> popups;
            if (!this.popupsMap.containsKey(p)) {
                popups = new LinkedList<Popup>();
                this.popupsMap.put(p, popups);
            }
            else {
                popups = this.popupsMap.get(p);
            }
            this.doAnimation(p, popup);
            popups.add(popup);
        }
        
        private void removePopupFromMap(final Pos p, final Popup popup) {
            if (this.popupsMap.containsKey(p)) {
                final List<Popup> popups = this.popupsMap.get(p);
                popups.remove(popup);
            }
        }
        
        private void doAnimation(final Pos p, final Popup changedPopup) {
            final List<Popup> popups = this.popupsMap.get(p);
            if (popups == null) {
                return;
            }
            final double newPopupHeight = ((Node)changedPopup.getContent().get(0)).getBoundsInParent().getHeight();
            this.parallelTransition.stop();
            this.parallelTransition.getChildren().clear();
            final boolean isShowFromTop = this.isShowFromTop(p);
            double sum = 0.0;
            final double[] targetAnchors = new double[popups.size()];
            for (int i = popups.size() - 1; i >= 0; --i) {
                final Popup _popup = popups.get(i);
                final double popupHeight = ((Node)_popup.getContent().get(0)).getBoundsInParent().getHeight();
                if (isShowFromTop) {
                    if (i == popups.size() - 1) {
                        sum = this.startY + newPopupHeight + 15.0;
                    }
                    else {
                        sum += popupHeight;
                    }
                    targetAnchors[i] = sum;
                }
                else {
                    if (i == popups.size() - 1) {
                        sum = changedPopup.getAnchorY() - popupHeight;
                    }
                    else {
                        sum -= popupHeight;
                    }
                    targetAnchors[i] = sum;
                }
            }
            for (int i = popups.size() - 1; i >= 0; --i) {
                final Popup _popup = popups.get(i);
                final double anchorYTarget = targetAnchors[i];
                if (anchorYTarget < 0.0) {
                    _popup.hide();
                }
                final double oldAnchorY = _popup.getAnchorY();
                final double distance = anchorYTarget - oldAnchorY;
                final Transition t = new CustomTransition(_popup, oldAnchorY, distance);
                t.setCycleCount(1);
                this.parallelTransition.getChildren().add((Object)t);
            }
            this.parallelTransition.play();
        }
        
        private boolean isShowFromTop(final Pos p) {
            switch (p) {
                case TOP_LEFT:
                case TOP_CENTER:
                case TOP_RIGHT: {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
        
        static {
            INSTANCE = new NotificationPopupHandler();
        }
        
        class CustomTransition extends Transition
        {
            private WeakReference<Popup> popupWeakReference;
            private double oldAnchorY;
            private double distance;
            
            CustomTransition(final Popup popup, final double oldAnchorY, final double distance) {
                this.popupWeakReference = new WeakReference<Popup>(popup);
                this.oldAnchorY = oldAnchorY;
                this.distance = distance;
                this.setCycleDuration(Duration.millis(350.0));
            }
            
            protected void interpolate(final double frac) {
                final Popup popup = this.popupWeakReference.get();
                if (popup != null) {
                    final double newAnchorY = this.oldAnchorY + this.distance * frac;
                    popup.setAnchorY(newAnchorY);
                }
            }
        }
    }
}
