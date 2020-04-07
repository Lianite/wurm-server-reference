// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.geometry.Orientation;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import javafx.scene.layout.Region;
import org.controlsfx.tools.Utils;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import javafx.scene.control.Control;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import impl.org.controlsfx.behavior.RatingBehavior;
import org.controlsfx.control.Rating;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

public class RatingSkin extends BehaviorSkinBase<Rating, RatingBehavior>
{
    private static final String STRONG = "strong";
    private boolean updateOnHover;
    private boolean partialRating;
    private Pane backgroundContainer;
    private Pane foregroundContainer;
    private double rating;
    private Rectangle forgroundClipRect;
    private final EventHandler<MouseEvent> mouseMoveHandler;
    private final EventHandler<MouseEvent> mouseClickHandler;
    
    private void updateRatingFromMouseEvent(final MouseEvent event) {
        final Rating control = (Rating)this.getSkinnable();
        if (!control.ratingProperty().isBound()) {
            final Point2D mouseLocation = new Point2D(event.getSceneX(), event.getSceneY());
            control.setRating(this.calculateRating(mouseLocation));
        }
    }
    
    public RatingSkin(final Rating control) {
        super((Control)control, (BehaviorBase)new RatingBehavior(control));
        this.rating = -1.0;
        this.mouseMoveHandler = (EventHandler<MouseEvent>)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent event) {
                if (RatingSkin.this.updateOnHover) {
                    RatingSkin.this.updateRatingFromMouseEvent(event);
                }
            }
        };
        this.mouseClickHandler = (EventHandler<MouseEvent>)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent event) {
                if (!RatingSkin.this.updateOnHover) {
                    RatingSkin.this.updateRatingFromMouseEvent(event);
                }
            }
        };
        this.updateOnHover = control.isUpdateOnHover();
        this.partialRating = control.isPartialRating();
        this.recreateButtons();
        this.updateRating();
        this.registerChangeListener((ObservableValue)control.ratingProperty(), "RATING");
        this.registerChangeListener((ObservableValue)control.maxProperty(), "MAX");
        this.registerChangeListener((ObservableValue)control.orientationProperty(), "ORIENTATION");
        this.registerChangeListener((ObservableValue)control.updateOnHoverProperty(), "UPDATE_ON_HOVER");
        this.registerChangeListener((ObservableValue)control.partialRatingProperty(), "PARTIAL_RATING");
        this.registerChangeListener((ObservableValue)control.boundsInLocalProperty(), "BOUNDS");
    }
    
    protected void handleControlPropertyChanged(final String p) {
        super.handleControlPropertyChanged(p);
        if (p == "RATING") {
            this.updateRating();
        }
        else if (p == "MAX") {
            this.recreateButtons();
        }
        else if (p == "ORIENTATION") {
            this.recreateButtons();
        }
        else if (p == "PARTIAL_RATING") {
            this.partialRating = ((Rating)this.getSkinnable()).isPartialRating();
            this.recreateButtons();
        }
        else if (p == "UPDATE_ON_HOVER") {
            this.updateOnHover = ((Rating)this.getSkinnable()).isUpdateOnHover();
            this.recreateButtons();
        }
        else if (p == "BOUNDS" && this.partialRating) {
            this.updateClip();
        }
    }
    
    private void recreateButtons() {
        this.backgroundContainer = null;
        this.foregroundContainer = null;
        this.backgroundContainer = (Pane)(this.isVertical() ? new VBox() : new HBox());
        this.backgroundContainer.getStyleClass().add((Object)"container");
        this.getChildren().setAll((Object[])new Node[] { this.backgroundContainer });
        if (this.updateOnHover || this.partialRating) {
            this.foregroundContainer = (Pane)(this.isVertical() ? new VBox() : new HBox());
            this.foregroundContainer.getStyleClass().add((Object)"container");
            this.foregroundContainer.setMouseTransparent(true);
            this.getChildren().add((Object)this.foregroundContainer);
            this.forgroundClipRect = new Rectangle();
            this.foregroundContainer.setClip((Node)this.forgroundClipRect);
        }
        for (int index = 0; index <= ((Rating)this.getSkinnable()).getMax(); ++index) {
            final Node backgroundNode = this.createButton();
            if (index > 0) {
                if (this.isVertical()) {
                    this.backgroundContainer.getChildren().add(0, (Object)backgroundNode);
                }
                else {
                    this.backgroundContainer.getChildren().add((Object)backgroundNode);
                }
                if (this.partialRating) {
                    final Node foregroundNode = this.createButton();
                    foregroundNode.getStyleClass().add((Object)"strong");
                    foregroundNode.setMouseTransparent(true);
                    if (this.isVertical()) {
                        this.foregroundContainer.getChildren().add(0, (Object)foregroundNode);
                    }
                    else {
                        this.foregroundContainer.getChildren().add((Object)foregroundNode);
                    }
                }
            }
        }
        this.updateRating();
    }
    
    private double calculateRating(final Point2D sceneLocation) {
        final Point2D b = this.backgroundContainer.sceneToLocal(sceneLocation);
        final double x = b.getX();
        final double y = b.getY();
        final Rating control = (Rating)this.getSkinnable();
        final int max = control.getMax();
        final double w = control.getWidth() - (this.snappedLeftInset() + this.snappedRightInset());
        final double h = control.getHeight() - (this.snappedTopInset() + this.snappedBottomInset());
        double newRating = -1.0;
        if (this.isVertical()) {
            newRating = (h - y) / h * max;
        }
        else {
            newRating = x / w * max;
        }
        if (!this.partialRating) {
            newRating = Utils.clamp(1.0, Math.ceil(newRating), control.getMax());
        }
        return newRating;
    }
    
    private void updateClip() {
        final Rating control = (Rating)this.getSkinnable();
        final double h = control.getHeight() - (this.snappedTopInset() + this.snappedBottomInset());
        final double w = control.getWidth() - (this.snappedLeftInset() + this.snappedRightInset());
        if (this.isVertical()) {
            final double y = h * this.rating / control.getMax();
            this.forgroundClipRect.relocate(0.0, h - y);
            this.forgroundClipRect.setWidth(control.getWidth());
            this.forgroundClipRect.setHeight(y);
        }
        else {
            final double x = w * this.rating / control.getMax();
            this.forgroundClipRect.setWidth(x);
            this.forgroundClipRect.setHeight(control.getHeight());
        }
    }
    
    private Node createButton() {
        final Region btn = new Region();
        btn.getStyleClass().add((Object)"button");
        btn.setOnMouseMoved((EventHandler)this.mouseMoveHandler);
        btn.setOnMouseClicked((EventHandler)this.mouseClickHandler);
        return (Node)btn;
    }
    
    private void updateRating() {
        final double newRating = ((Rating)this.getSkinnable()).getRating();
        if (newRating == this.rating) {
            return;
        }
        this.rating = Utils.clamp(0.0, newRating, ((Rating)this.getSkinnable()).getMax());
        if (this.partialRating) {
            this.updateClip();
        }
        else {
            this.updateButtonStyles();
        }
    }
    
    private void updateButtonStyles() {
        final int max = ((Rating)this.getSkinnable()).getMax();
        final List<Node> buttons = new ArrayList<Node>((Collection<? extends Node>)this.backgroundContainer.getChildren());
        if (this.isVertical()) {
            Collections.reverse(buttons);
        }
        for (int i = 0; i < max; ++i) {
            final Node button = buttons.get(i);
            final List<String> styleClass = (List<String>)button.getStyleClass();
            final boolean containsStrong = styleClass.contains("strong");
            if (i < this.rating) {
                if (!containsStrong) {
                    styleClass.add("strong");
                }
            }
            else if (containsStrong) {
                styleClass.remove("strong");
            }
        }
    }
    
    private boolean isVertical() {
        return ((Rating)this.getSkinnable()).getOrientation() == Orientation.VERTICAL;
    }
    
    protected double computeMaxWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
    }
}
