// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.beans.value.WritableValue;
import javafx.animation.KeyValue;
import javafx.event.EventType;
import org.controlsfx.control.NotificationPane;
import javafx.animation.Animation;
import java.util.Collection;
import org.controlsfx.control.action.ActionUtils;
import javafx.geometry.VPos;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.event.Event;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.Observable;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import javafx.beans.property.SimpleDoubleProperty;
import org.controlsfx.control.action.Action;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

public abstract class NotificationBar extends Region
{
    private static final double MIN_HEIGHT = 40.0;
    final Label label;
    Label title;
    ButtonBar actionsBar;
    Button closeBtn;
    private final GridPane pane;
    public DoubleProperty transition;
    private final Duration TRANSITION_DURATION;
    private Timeline timeline;
    private double transitionStartValue;
    
    public void requestContainerLayout() {
        this.layoutChildren();
    }
    
    public String getTitle() {
        return "";
    }
    
    public boolean isCloseButtonVisible() {
        return true;
    }
    
    public abstract String getText();
    
    public abstract Node getGraphic();
    
    public abstract ObservableList<Action> getActions();
    
    public abstract void hide();
    
    public abstract boolean isShowing();
    
    public abstract boolean isShowFromTop();
    
    public abstract double getContainerHeight();
    
    public abstract void relocateInParent(final double p0, final double p1);
    
    public NotificationBar() {
        this.transition = (DoubleProperty)new SimpleDoubleProperty() {
            protected void invalidated() {
                NotificationBar.this.requestContainerLayout();
            }
        };
        this.TRANSITION_DURATION = new Duration(350.0);
        this.getStyleClass().add((Object)"notification-bar");
        this.setVisible(this.isShowing());
        this.pane = new GridPane();
        this.pane.getStyleClass().add((Object)"pane");
        this.pane.setAlignment(Pos.BASELINE_LEFT);
        this.getChildren().setAll((Object[])new Node[] { this.pane });
        final String titleStr = this.getTitle();
        if (titleStr != null && !titleStr.isEmpty()) {
            this.title = new Label();
            this.title.getStyleClass().add((Object)"title");
            this.title.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            GridPane.setHgrow((Node)this.title, Priority.ALWAYS);
            this.title.setText(titleStr);
            this.title.opacityProperty().bind((ObservableValue)this.transition);
        }
        (this.label = new Label()).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setVgrow((Node)this.label, Priority.ALWAYS);
        GridPane.setHgrow((Node)this.label, Priority.ALWAYS);
        this.label.setText(this.getText());
        this.label.setGraphic(this.getGraphic());
        this.label.opacityProperty().bind((ObservableValue)this.transition);
        this.getActions().addListener((InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable arg0) {
                NotificationBar.this.updatePane();
            }
        });
        (this.closeBtn = new Button()).setOnAction((EventHandler)new EventHandler<ActionEvent>() {
            public void handle(final ActionEvent arg0) {
                NotificationBar.this.hide();
            }
        });
        this.closeBtn.getStyleClass().setAll((Object[])new String[] { "close-button" });
        final StackPane graphic = new StackPane();
        graphic.getStyleClass().setAll((Object[])new String[] { "graphic" });
        this.closeBtn.setGraphic((Node)graphic);
        this.closeBtn.setMinSize(17.0, 17.0);
        this.closeBtn.setPrefSize(17.0, 17.0);
        this.closeBtn.setFocusTraversable(false);
        this.closeBtn.opacityProperty().bind((ObservableValue)this.transition);
        GridPane.setMargin((Node)this.closeBtn, new Insets(0.0, 0.0, 0.0, 8.0));
        final double minHeight = this.minHeight(-1.0);
        GridPane.setValignment((Node)this.closeBtn, (minHeight == 40.0) ? VPos.CENTER : VPos.TOP);
        this.updatePane();
    }
    
    void updatePane() {
        this.actionsBar = ActionUtils.createButtonBar((Collection<? extends Action>)this.getActions());
        this.actionsBar.opacityProperty().bind((ObservableValue)this.transition);
        GridPane.setHgrow((Node)this.actionsBar, Priority.SOMETIMES);
        this.pane.getChildren().clear();
        int row = 0;
        if (this.title != null) {
            this.pane.add((Node)this.title, 0, row++);
        }
        this.pane.add((Node)this.label, 0, row);
        this.pane.add((Node)this.actionsBar, 1, row);
        if (this.isCloseButtonVisible()) {
            this.pane.add((Node)this.closeBtn, 2, 0, 1, row + 1);
        }
    }
    
    protected void layoutChildren() {
        final double w = this.getWidth();
        final double h = this.computePrefHeight(-1.0);
        final double notificationBarHeight = this.prefHeight(w);
        final double notificationMinHeight = this.minHeight(w);
        if (this.isShowFromTop()) {
            this.pane.resize(w, h);
            this.relocateInParent(0.0, (this.transition.get() - 1.0) * notificationMinHeight);
        }
        else {
            this.pane.resize(w, notificationBarHeight);
            this.relocateInParent(0.0, this.getContainerHeight() - notificationBarHeight);
        }
    }
    
    protected double computeMinHeight(final double width) {
        return Math.max(super.computePrefHeight(width), 40.0);
    }
    
    protected double computePrefHeight(final double width) {
        return Math.max(this.pane.prefHeight(width), this.minHeight(width)) * this.transition.get();
    }
    
    public void doShow() {
        this.transitionStartValue = 0.0;
        this.doAnimationTransition();
    }
    
    public void doHide() {
        this.transitionStartValue = 1.0;
        this.doAnimationTransition();
    }
    
    private void doAnimationTransition() {
        Duration duration;
        if (this.timeline != null && this.timeline.getStatus() != Animation.Status.STOPPED) {
            duration = this.timeline.getCurrentTime();
            duration = ((duration == Duration.ZERO) ? this.TRANSITION_DURATION : duration);
            this.transitionStartValue = this.transition.get();
            this.timeline.stop();
        }
        else {
            duration = this.TRANSITION_DURATION;
        }
        (this.timeline = new Timeline()).setCycleCount(1);
        KeyFrame k1;
        KeyFrame k2;
        if (this.isShowing()) {
            k1 = new KeyFrame(Duration.ZERO, (EventHandler)new EventHandler<ActionEvent>() {
                public void handle(final ActionEvent event) {
                    NotificationBar.this.setCache(true);
                    NotificationBar.this.setVisible(true);
                    NotificationBar.this.pane.fireEvent(new Event((EventType)NotificationPane.ON_SHOWING));
                }
            }, new KeyValue[] { new KeyValue((WritableValue)this.transition, (Object)this.transitionStartValue) });
            k2 = new KeyFrame(duration, (EventHandler)new EventHandler<ActionEvent>() {
                public void handle(final ActionEvent event) {
                    NotificationBar.this.pane.setCache(false);
                    NotificationBar.this.pane.fireEvent(new Event((EventType)NotificationPane.ON_SHOWN));
                }
            }, new KeyValue[] { new KeyValue((WritableValue)this.transition, (Object)1, Interpolator.EASE_OUT) });
        }
        else {
            k1 = new KeyFrame(Duration.ZERO, (EventHandler)new EventHandler<ActionEvent>() {
                public void handle(final ActionEvent event) {
                    NotificationBar.this.pane.setCache(true);
                    NotificationBar.this.pane.fireEvent(new Event((EventType)NotificationPane.ON_HIDING));
                }
            }, new KeyValue[] { new KeyValue((WritableValue)this.transition, (Object)this.transitionStartValue) });
            k2 = new KeyFrame(duration, (EventHandler)new EventHandler<ActionEvent>() {
                public void handle(final ActionEvent event) {
                    NotificationBar.this.setCache(false);
                    NotificationBar.this.setVisible(false);
                    NotificationBar.this.pane.fireEvent(new Event((EventType)NotificationPane.ON_HIDDEN));
                }
            }, new KeyValue[] { new KeyValue((WritableValue)this.transition, (Object)0, Interpolator.EASE_IN) });
        }
        this.timeline.getKeyFrames().setAll((Object[])new KeyFrame[] { k1, k2 });
        this.timeline.play();
    }
}
