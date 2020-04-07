// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.beans.Observable;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.beans.value.WritableValue;
import javafx.animation.KeyValue;
import javafx.animation.Animation;
import javafx.beans.InvalidationListener;
import javafx.scene.Node;
import javafx.beans.value.ObservableValue;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Side;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Control;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.HiddenSidesPane;
import javafx.scene.control.SkinBase;

public class HiddenSidesPaneSkin extends SkinBase<HiddenSidesPane>
{
    private final StackPane stackPane;
    private final EventHandler<MouseEvent> exitedHandler;
    private boolean mousePressed;
    private DoubleProperty[] visibility;
    private Timeline showTimeline;
    private Timeline hideTimeline;
    
    public HiddenSidesPaneSkin(final HiddenSidesPane pane) {
        super((Control)pane);
        this.visibility = (DoubleProperty[])new SimpleDoubleProperty[Side.values().length];
        this.exitedHandler = (EventHandler<MouseEvent>)(event -> {
            if (this.isMouseEnabled() && ((HiddenSidesPane)this.getSkinnable()).getPinnedSide() == null && !this.mousePressed) {
                this.hide();
            }
        });
        this.stackPane = new StackPane();
        this.getChildren().add((Object)this.stackPane);
        this.updateStackPane();
        final InvalidationListener rebuildListener = observable -> this.updateStackPane();
        pane.contentProperty().addListener(rebuildListener);
        pane.topProperty().addListener(rebuildListener);
        pane.rightProperty().addListener(rebuildListener);
        pane.bottomProperty().addListener(rebuildListener);
        pane.leftProperty().addListener(rebuildListener);
        pane.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            if (this.isMouseEnabled() && ((HiddenSidesPane)this.getSkinnable()).getPinnedSide() == null) {
                final Side side = this.getSide(event);
                if (side != null) {
                    this.show(side);
                }
                else if (this.isMouseMovedOutsideSides(event)) {
                    this.hide();
                }
            }
        });
        pane.addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler)this.exitedHandler);
        pane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> this.mousePressed = true);
        pane.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            this.mousePressed = false;
            if (this.isMouseEnabled() && ((HiddenSidesPane)this.getSkinnable()).getPinnedSide() == null) {
                final Side side = this.getSide(event);
                if (side != null) {
                    this.show(side);
                }
                else {
                    this.hide();
                }
            }
        });
        for (final Side side : Side.values()) {
            (this.visibility[side.ordinal()] = (DoubleProperty)new SimpleDoubleProperty(0.0)).addListener(observable -> ((HiddenSidesPane)this.getSkinnable()).requestLayout());
        }
        final Side pinnedSide = ((HiddenSidesPane)this.getSkinnable()).getPinnedSide();
        if (pinnedSide != null) {
            this.show(pinnedSide);
        }
        pane.pinnedSideProperty().addListener(observable -> this.show(((HiddenSidesPane)this.getSkinnable()).getPinnedSide()));
        final Rectangle clip = new Rectangle();
        clip.setX(0.0);
        clip.setY(0.0);
        clip.widthProperty().bind((ObservableValue)((HiddenSidesPane)this.getSkinnable()).widthProperty());
        clip.heightProperty().bind((ObservableValue)((HiddenSidesPane)this.getSkinnable()).heightProperty());
        ((HiddenSidesPane)this.getSkinnable()).setClip((Node)clip);
    }
    
    private boolean isMouseMovedOutsideSides(final MouseEvent event) {
        return (((HiddenSidesPane)this.getSkinnable()).getLeft() == null || !((HiddenSidesPane)this.getSkinnable()).getLeft().getBoundsInParent().contains(event.getX(), event.getY())) && (((HiddenSidesPane)this.getSkinnable()).getTop() == null || !((HiddenSidesPane)this.getSkinnable()).getTop().getBoundsInParent().contains(event.getX(), event.getY())) && (((HiddenSidesPane)this.getSkinnable()).getRight() == null || !((HiddenSidesPane)this.getSkinnable()).getRight().getBoundsInParent().contains(event.getX(), event.getY())) && (((HiddenSidesPane)this.getSkinnable()).getBottom() == null || !((HiddenSidesPane)this.getSkinnable()).getBottom().getBoundsInParent().contains(event.getX(), event.getY()));
    }
    
    private boolean isMouseEnabled() {
        return ((HiddenSidesPane)this.getSkinnable()).getTriggerDistance() > 0.0;
    }
    
    private Side getSide(final MouseEvent evt) {
        if (this.stackPane.getBoundsInLocal().contains(evt.getX(), evt.getY())) {
            final double trigger = ((HiddenSidesPane)this.getSkinnable()).getTriggerDistance();
            if (evt.getX() <= trigger) {
                return Side.LEFT;
            }
            if (evt.getX() > ((HiddenSidesPane)this.getSkinnable()).getWidth() - trigger) {
                return Side.RIGHT;
            }
            if (evt.getY() <= trigger) {
                return Side.TOP;
            }
            if (evt.getY() > ((HiddenSidesPane)this.getSkinnable()).getHeight() - trigger) {
                return Side.BOTTOM;
            }
        }
        return null;
    }
    
    private void show(final Side side) {
        if (this.hideTimeline != null) {
            this.hideTimeline.stop();
        }
        if (this.showTimeline != null && this.showTimeline.getStatus() == Animation.Status.RUNNING) {
            return;
        }
        final KeyValue[] keyValues = new KeyValue[Side.values().length];
        for (final Side s : Side.values()) {
            keyValues[s.ordinal()] = new KeyValue((WritableValue)this.visibility[s.ordinal()], (Object)(int)(s.equals((Object)side) ? 1 : 0));
        }
        final Duration delay = (((HiddenSidesPane)this.getSkinnable()).getAnimationDelay() != null) ? ((HiddenSidesPane)this.getSkinnable()).getAnimationDelay() : Duration.millis(300.0);
        final Duration duration = (((HiddenSidesPane)this.getSkinnable()).getAnimationDuration() != null) ? ((HiddenSidesPane)this.getSkinnable()).getAnimationDuration() : Duration.millis(200.0);
        final KeyFrame keyFrame = new KeyFrame(duration, keyValues);
        (this.showTimeline = new Timeline(new KeyFrame[] { keyFrame })).setDelay(delay);
        this.showTimeline.play();
    }
    
    private void hide() {
        if (this.showTimeline != null) {
            this.showTimeline.stop();
        }
        if (this.hideTimeline != null && this.hideTimeline.getStatus() == Animation.Status.RUNNING) {
            return;
        }
        boolean sideVisible = false;
        for (final Side side : Side.values()) {
            if (this.visibility[side.ordinal()].get() > 0.0) {
                sideVisible = true;
                break;
            }
        }
        if (!sideVisible) {
            return;
        }
        final KeyValue[] keyValues = new KeyValue[Side.values().length];
        for (final Side side2 : Side.values()) {
            keyValues[side2.ordinal()] = new KeyValue((WritableValue)this.visibility[side2.ordinal()], (Object)0);
        }
        final Duration delay = (((HiddenSidesPane)this.getSkinnable()).getAnimationDelay() != null) ? ((HiddenSidesPane)this.getSkinnable()).getAnimationDelay() : Duration.millis(300.0);
        final Duration duration = (((HiddenSidesPane)this.getSkinnable()).getAnimationDuration() != null) ? ((HiddenSidesPane)this.getSkinnable()).getAnimationDuration() : Duration.millis(200.0);
        final KeyFrame keyFrame = new KeyFrame(duration, keyValues);
        (this.hideTimeline = new Timeline(new KeyFrame[] { keyFrame })).setDelay(delay);
        this.hideTimeline.play();
    }
    
    private void updateStackPane() {
        this.stackPane.getChildren().clear();
        if (((HiddenSidesPane)this.getSkinnable()).getContent() != null) {
            this.stackPane.getChildren().add((Object)((HiddenSidesPane)this.getSkinnable()).getContent());
        }
        if (((HiddenSidesPane)this.getSkinnable()).getTop() != null) {
            this.stackPane.getChildren().add((Object)((HiddenSidesPane)this.getSkinnable()).getTop());
            ((HiddenSidesPane)this.getSkinnable()).getTop().setManaged(false);
            ((HiddenSidesPane)this.getSkinnable()).getTop().removeEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler)this.exitedHandler);
            ((HiddenSidesPane)this.getSkinnable()).getTop().addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler)this.exitedHandler);
        }
        if (((HiddenSidesPane)this.getSkinnable()).getRight() != null) {
            this.stackPane.getChildren().add((Object)((HiddenSidesPane)this.getSkinnable()).getRight());
            ((HiddenSidesPane)this.getSkinnable()).getRight().setManaged(false);
            ((HiddenSidesPane)this.getSkinnable()).getRight().removeEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler)this.exitedHandler);
            ((HiddenSidesPane)this.getSkinnable()).getRight().addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler)this.exitedHandler);
        }
        if (((HiddenSidesPane)this.getSkinnable()).getBottom() != null) {
            this.stackPane.getChildren().add((Object)((HiddenSidesPane)this.getSkinnable()).getBottom());
            ((HiddenSidesPane)this.getSkinnable()).getBottom().setManaged(false);
            ((HiddenSidesPane)this.getSkinnable()).getBottom().removeEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler)this.exitedHandler);
            ((HiddenSidesPane)this.getSkinnable()).getBottom().addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler)this.exitedHandler);
        }
        if (((HiddenSidesPane)this.getSkinnable()).getLeft() != null) {
            this.stackPane.getChildren().add((Object)((HiddenSidesPane)this.getSkinnable()).getLeft());
            ((HiddenSidesPane)this.getSkinnable()).getLeft().setManaged(false);
            ((HiddenSidesPane)this.getSkinnable()).getLeft().removeEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler)this.exitedHandler);
            ((HiddenSidesPane)this.getSkinnable()).getLeft().addEventFilter(MouseEvent.MOUSE_EXITED, (EventHandler)this.exitedHandler);
        }
    }
    
    protected void layoutChildren(final double contentX, final double contentY, final double contentWidth, final double contentHeight) {
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
        final Node bottom = ((HiddenSidesPane)this.getSkinnable()).getBottom();
        if (bottom != null) {
            final double prefHeight = bottom.prefHeight(-1.0);
            final double offset = prefHeight * this.visibility[Side.BOTTOM.ordinal()].get();
            bottom.resizeRelocate(contentX, contentY + contentHeight - offset, contentWidth, prefHeight);
            bottom.setVisible(this.visibility[Side.BOTTOM.ordinal()].get() > 0.0);
        }
        final Node left = ((HiddenSidesPane)this.getSkinnable()).getLeft();
        if (left != null) {
            final double prefWidth = left.prefWidth(-1.0);
            final double offset2 = prefWidth * this.visibility[Side.LEFT.ordinal()].get();
            left.resizeRelocate(contentX - (prefWidth - offset2), contentY, prefWidth, contentHeight);
            left.setVisible(this.visibility[Side.LEFT.ordinal()].get() > 0.0);
        }
        final Node right = ((HiddenSidesPane)this.getSkinnable()).getRight();
        if (right != null) {
            final double prefWidth2 = right.prefWidth(-1.0);
            final double offset3 = prefWidth2 * this.visibility[Side.RIGHT.ordinal()].get();
            right.resizeRelocate(contentX + contentWidth - offset3, contentY, prefWidth2, contentHeight);
            right.setVisible(this.visibility[Side.RIGHT.ordinal()].get() > 0.0);
        }
        final Node top = ((HiddenSidesPane)this.getSkinnable()).getTop();
        if (top != null) {
            final double prefHeight2 = top.prefHeight(-1.0);
            final double offset4 = prefHeight2 * this.visibility[Side.TOP.ordinal()].get();
            top.resizeRelocate(contentX, contentY - (prefHeight2 - offset4), contentWidth, prefHeight2);
            top.setVisible(this.visibility[Side.TOP.ordinal()].get() > 0.0);
        }
    }
}
