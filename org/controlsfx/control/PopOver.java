// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.animation.FadeTransition;
import javafx.geometry.Bounds;
import java.util.Objects;
import impl.org.controlsfx.skin.PopOverSkin;
import javafx.scene.control.Skin;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.event.Event;
import javafx.stage.PopupWindow;
import javafx.beans.property.SimpleStringProperty;
import impl.org.controlsfx.i18n.Localization;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.BooleanProperty;
import javafx.event.WeakEventHandler;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;
import javafx.stage.Window;
import javafx.beans.value.WeakChangeListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.InvalidationListener;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Duration;
import javafx.scene.control.PopupControl;

public class PopOver extends PopupControl
{
    private static final String DEFAULT_STYLE_CLASS = "popover";
    private static final Duration DEFAULT_FADE_DURATION;
    private double targetX;
    private double targetY;
    private final SimpleBooleanProperty animated;
    private final ObjectProperty<Duration> fadeInDuration;
    private final ObjectProperty<Duration> fadeOutDuration;
    private final StackPane root;
    private final ObjectProperty<Node> contentNode;
    private InvalidationListener hideListener;
    private WeakInvalidationListener weakHideListener;
    private ChangeListener<Number> xListener;
    private WeakChangeListener<Number> weakXListener;
    private ChangeListener<Number> yListener;
    private WeakChangeListener<Number> weakYListener;
    private Window ownerWindow;
    private final EventHandler<WindowEvent> closePopOverOnOwnerWindowCloseLambda;
    private final WeakEventHandler<WindowEvent> closePopOverOnOwnerWindowClose;
    private final BooleanProperty headerAlwaysVisible;
    private final BooleanProperty closeButtonEnabled;
    private final BooleanProperty detachable;
    private final BooleanProperty detached;
    private final DoubleProperty arrowSize;
    private final DoubleProperty arrowIndent;
    private final DoubleProperty cornerRadius;
    private final StringProperty title;
    private final ObjectProperty<ArrowLocation> arrowLocation;
    
    public PopOver() {
        this.animated = new SimpleBooleanProperty(true);
        this.fadeInDuration = (ObjectProperty<Duration>)new SimpleObjectProperty((Object)PopOver.DEFAULT_FADE_DURATION);
        this.fadeOutDuration = (ObjectProperty<Duration>)new SimpleObjectProperty((Object)PopOver.DEFAULT_FADE_DURATION);
        this.root = new StackPane();
        this.contentNode = (ObjectProperty<Node>)new SimpleObjectProperty<Node>((Object)this, "contentNode") {
            public void setValue(final Node node) {
                if (node == null) {
                    throw new IllegalArgumentException("content node can not be null");
                }
            }
        };
        this.hideListener = (InvalidationListener)new InvalidationListener() {
            public void invalidated(final Observable observable) {
                if (!PopOver.this.isDetached()) {
                    PopOver.this.hide(Duration.ZERO);
                }
            }
        };
        this.weakHideListener = new WeakInvalidationListener(this.hideListener);
        this.xListener = (ChangeListener<Number>)new ChangeListener<Number>() {
            public void changed(final ObservableValue<? extends Number> value, final Number oldX, final Number newX) {
                if (!PopOver.this.isDetached()) {
                    PopOver.this.setAnchorX(PopOver.this.getAnchorX() + (newX.doubleValue() - oldX.doubleValue()));
                }
            }
        };
        this.weakXListener = (WeakChangeListener<Number>)new WeakChangeListener((ChangeListener)this.xListener);
        this.yListener = (ChangeListener<Number>)new ChangeListener<Number>() {
            public void changed(final ObservableValue<? extends Number> value, final Number oldY, final Number newY) {
                if (!PopOver.this.isDetached()) {
                    PopOver.this.setAnchorY(PopOver.this.getAnchorY() + (newY.doubleValue() - oldY.doubleValue()));
                }
            }
        };
        this.weakYListener = (WeakChangeListener<Number>)new WeakChangeListener((ChangeListener)this.yListener);
        this.closePopOverOnOwnerWindowCloseLambda = (EventHandler<WindowEvent>)(event -> this.ownerWindowClosing());
        this.closePopOverOnOwnerWindowClose = (WeakEventHandler<WindowEvent>)new WeakEventHandler((EventHandler)this.closePopOverOnOwnerWindowCloseLambda);
        this.headerAlwaysVisible = (BooleanProperty)new SimpleBooleanProperty((Object)this, "headerAlwaysVisible");
        this.closeButtonEnabled = (BooleanProperty)new SimpleBooleanProperty((Object)this, "closeButtonEnabled", true);
        this.detachable = (BooleanProperty)new SimpleBooleanProperty((Object)this, "detachable", true);
        this.detached = (BooleanProperty)new SimpleBooleanProperty((Object)this, "detached", false);
        this.arrowSize = (DoubleProperty)new SimpleDoubleProperty((Object)this, "arrowSize", 12.0);
        this.arrowIndent = (DoubleProperty)new SimpleDoubleProperty((Object)this, "arrowIndent", 12.0);
        this.cornerRadius = (DoubleProperty)new SimpleDoubleProperty((Object)this, "cornerRadius", 6.0);
        this.title = (StringProperty)new SimpleStringProperty((Object)this, "title", Localization.localize(Localization.asKey("popOver.default.title")));
        this.arrowLocation = (ObjectProperty<ArrowLocation>)new SimpleObjectProperty((Object)this, "arrowLocation", (Object)ArrowLocation.LEFT_TOP);
        this.getStyleClass().add((Object)"popover");
        this.getRoot().getStylesheets().add((Object)PopOver.class.getResource("popover.css").toExternalForm());
        this.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_LEFT);
        this.setOnHiding((EventHandler)new EventHandler<WindowEvent>() {
            public void handle(final WindowEvent evt) {
                PopOver.this.setDetached(false);
            }
        });
        final Label label = new Label(Localization.localize(Localization.asKey("popOver.default.content")));
        label.setPrefSize(200.0, 200.0);
        label.setPadding(new Insets(4.0));
        this.setContentNode((Node)label);
        final InvalidationListener repositionListener = observable -> {
            if (this.isShowing() && !this.isDetached()) {
                this.show(this.getOwnerNode(), this.targetX, this.targetY);
                this.adjustWindowLocation();
            }
        };
        this.arrowSize.addListener(repositionListener);
        this.cornerRadius.addListener(repositionListener);
        this.arrowLocation.addListener(repositionListener);
        this.arrowIndent.addListener(repositionListener);
        this.headerAlwaysVisible.addListener(repositionListener);
        this.detached.addListener(it -> {
            if (this.isDetached()) {
                this.setAutoHide(false);
            }
            else {
                this.setAutoHide(true);
            }
        });
        this.setAutoHide(true);
    }
    
    public PopOver(final Node content) {
        this();
        this.setContentNode(content);
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new PopOverSkin(this);
    }
    
    public final StackPane getRoot() {
        return this.root;
    }
    
    public final ObjectProperty<Node> contentNodeProperty() {
        return this.contentNode;
    }
    
    public final Node getContentNode() {
        return (Node)this.contentNodeProperty().get();
    }
    
    public final void setContentNode(final Node content) {
        this.contentNodeProperty().set((Object)content);
    }
    
    public final void show(final Node owner) {
        this.show(owner, 4.0);
    }
    
    public final void show(final Node owner, final double offset) {
        Objects.requireNonNull(owner);
        final Bounds bounds = owner.localToScreen(owner.getBoundsInLocal());
        switch (this.getArrowLocation()) {
            case BOTTOM_CENTER:
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT: {
                this.show(owner, bounds.getMinX() + bounds.getWidth() / 2.0, bounds.getMinY() + offset);
                break;
            }
            case LEFT_BOTTOM:
            case LEFT_CENTER:
            case LEFT_TOP: {
                this.show(owner, bounds.getMaxX() - offset, bounds.getMinY() + bounds.getHeight() / 2.0);
                break;
            }
            case RIGHT_BOTTOM:
            case RIGHT_CENTER:
            case RIGHT_TOP: {
                this.show(owner, bounds.getMinX() + offset, bounds.getMinY() + bounds.getHeight() / 2.0);
                break;
            }
            case TOP_CENTER:
            case TOP_LEFT:
            case TOP_RIGHT: {
                this.show(owner, bounds.getMinX() + bounds.getWidth() / 2.0, bounds.getMinY() + bounds.getHeight() - offset);
                break;
            }
        }
    }
    
    public final void show(final Window owner) {
        super.show(owner);
        this.ownerWindow = owner;
        if (this.isAnimated()) {
            this.showFadeInAnimation(this.getFadeInDuration());
        }
        this.ownerWindow.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, (EventHandler)this.closePopOverOnOwnerWindowClose);
        this.ownerWindow.addEventFilter(WindowEvent.WINDOW_HIDING, (EventHandler)this.closePopOverOnOwnerWindowClose);
    }
    
    public final void show(final Window ownerWindow, final double anchorX, final double anchorY) {
        super.show(ownerWindow, anchorX, anchorY);
        this.ownerWindow = ownerWindow;
        if (this.isAnimated()) {
            this.showFadeInAnimation(this.getFadeInDuration());
        }
        ownerWindow.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, (EventHandler)this.closePopOverOnOwnerWindowClose);
        ownerWindow.addEventFilter(WindowEvent.WINDOW_HIDING, (EventHandler)this.closePopOverOnOwnerWindowClose);
    }
    
    public final void show(final Node owner, final double x, final double y) {
        this.show(owner, x, y, this.getFadeInDuration());
    }
    
    public final void show(final Node owner, final double x, final double y, Duration fadeInDuration) {
        if (this.ownerWindow != null && this.isShowing()) {
            super.hide();
        }
        this.targetX = x;
        this.targetY = y;
        if (owner == null) {
            throw new IllegalArgumentException("owner can not be null");
        }
        if (fadeInDuration == null) {
            fadeInDuration = PopOver.DEFAULT_FADE_DURATION;
        }
        if (this.ownerWindow != null) {
            this.ownerWindow.xProperty().removeListener((ChangeListener)this.weakXListener);
            this.ownerWindow.yProperty().removeListener((ChangeListener)this.weakYListener);
            this.ownerWindow.widthProperty().removeListener((InvalidationListener)this.weakHideListener);
            this.ownerWindow.heightProperty().removeListener((InvalidationListener)this.weakHideListener);
        }
        this.ownerWindow = owner.getScene().getWindow();
        this.ownerWindow.xProperty().addListener((ChangeListener)this.weakXListener);
        this.ownerWindow.yProperty().addListener((ChangeListener)this.weakYListener);
        this.ownerWindow.widthProperty().addListener((InvalidationListener)this.weakHideListener);
        this.ownerWindow.heightProperty().addListener((InvalidationListener)this.weakHideListener);
        this.setOnShown(evt -> {
            this.getScene().addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                if (mouseEvent.getTarget().equals(this.getScene().getRoot()) && !this.isDetached()) {
                    this.hide();
                }
            });
            this.adjustWindowLocation();
        });
        super.show(owner, x, y);
        if (this.isAnimated()) {
            this.showFadeInAnimation(fadeInDuration);
        }
        this.ownerWindow.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, (EventHandler)this.closePopOverOnOwnerWindowClose);
        this.ownerWindow.addEventFilter(WindowEvent.WINDOW_HIDING, (EventHandler)this.closePopOverOnOwnerWindowClose);
    }
    
    private void showFadeInAnimation(final Duration fadeInDuration) {
        final Node skinNode = this.getSkin().getNode();
        skinNode.setOpacity(0.0);
        final FadeTransition fadeIn = new FadeTransition(fadeInDuration, skinNode);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    
    private void ownerWindowClosing() {
        this.hide(Duration.ZERO);
    }
    
    public final void hide() {
        this.hide(this.getFadeOutDuration());
    }
    
    public final void hide(Duration fadeOutDuration) {
        if (this.ownerWindow != null) {
            this.ownerWindow.removeEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, (EventHandler)this.closePopOverOnOwnerWindowClose);
            this.ownerWindow.removeEventFilter(WindowEvent.WINDOW_HIDING, (EventHandler)this.closePopOverOnOwnerWindowClose);
        }
        if (fadeOutDuration == null) {
            fadeOutDuration = PopOver.DEFAULT_FADE_DURATION;
        }
        if (this.isShowing()) {
            if (this.isAnimated()) {
                final Node skinNode = this.getSkin().getNode();
                final FadeTransition fadeOut = new FadeTransition(fadeOutDuration, skinNode);
                fadeOut.setFromValue(skinNode.getOpacity());
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(evt -> super.hide());
                fadeOut.play();
            }
            else {
                super.hide();
            }
        }
    }
    
    private void adjustWindowLocation() {
        final Bounds bounds = this.getSkin().getNode().getBoundsInParent();
        switch (this.getArrowLocation()) {
            case TOP_CENTER:
            case TOP_LEFT:
            case TOP_RIGHT: {
                this.setAnchorX(this.getAnchorX() + bounds.getMinX() - this.computeXOffset());
                this.setAnchorY(this.getAnchorY() + bounds.getMinY() + this.getArrowSize());
                break;
            }
            case LEFT_BOTTOM:
            case LEFT_CENTER:
            case LEFT_TOP: {
                this.setAnchorX(this.getAnchorX() + bounds.getMinX() + this.getArrowSize());
                this.setAnchorY(this.getAnchorY() + bounds.getMinY() - this.computeYOffset());
                break;
            }
            case BOTTOM_CENTER:
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT: {
                this.setAnchorX(this.getAnchorX() + bounds.getMinX() - this.computeXOffset());
                this.setAnchorY(this.getAnchorY() - bounds.getMinY() - bounds.getMaxY() - 1.0);
                break;
            }
            case RIGHT_BOTTOM:
            case RIGHT_CENTER:
            case RIGHT_TOP: {
                this.setAnchorX(this.getAnchorX() - bounds.getMinX() - bounds.getMaxX() - 1.0);
                this.setAnchorY(this.getAnchorY() + bounds.getMinY() - this.computeYOffset());
                break;
            }
        }
    }
    
    private double computeXOffset() {
        switch (this.getArrowLocation()) {
            case BOTTOM_LEFT:
            case TOP_LEFT: {
                return this.getCornerRadius() + this.getArrowIndent() + this.getArrowSize();
            }
            case BOTTOM_CENTER:
            case TOP_CENTER: {
                return this.getContentNode().prefWidth(-1.0) / 2.0;
            }
            case BOTTOM_RIGHT:
            case TOP_RIGHT: {
                return this.getContentNode().prefWidth(-1.0) - this.getArrowIndent() - this.getCornerRadius() - this.getArrowSize();
            }
            default: {
                return 0.0;
            }
        }
    }
    
    private double computeYOffset() {
        final double prefContentHeight = this.getContentNode().prefHeight(-1.0);
        switch (this.getArrowLocation()) {
            case LEFT_TOP:
            case RIGHT_TOP: {
                return this.getCornerRadius() + this.getArrowIndent() + this.getArrowSize();
            }
            case LEFT_CENTER:
            case RIGHT_CENTER: {
                return Math.max(prefContentHeight, 2.0 * (this.getCornerRadius() + this.getArrowIndent() + this.getArrowSize())) / 2.0;
            }
            case LEFT_BOTTOM:
            case RIGHT_BOTTOM: {
                return Math.max(prefContentHeight - this.getCornerRadius() - this.getArrowIndent() - this.getArrowSize(), this.getCornerRadius() + this.getArrowIndent() + this.getArrowSize());
            }
            default: {
                return 0.0;
            }
        }
    }
    
    public final void detach() {
        if (this.isDetachable()) {
            this.setDetached(true);
        }
    }
    
    public final BooleanProperty headerAlwaysVisibleProperty() {
        return this.headerAlwaysVisible;
    }
    
    public final void setHeaderAlwaysVisible(final boolean visible) {
        this.headerAlwaysVisible.setValue(visible);
    }
    
    public final boolean isHeaderAlwaysVisible() {
        return this.headerAlwaysVisible.getValue();
    }
    
    public final BooleanProperty closeButtonEnabledProperty() {
        return this.closeButtonEnabled;
    }
    
    public final void setCloseButtonEnabled(final boolean enabled) {
        this.closeButtonEnabled.setValue(enabled);
    }
    
    public final boolean isCloseButtonEnabled() {
        return this.closeButtonEnabled.getValue();
    }
    
    public final BooleanProperty detachableProperty() {
        return this.detachable;
    }
    
    public final void setDetachable(final boolean detachable) {
        this.detachableProperty().set(detachable);
    }
    
    public final boolean isDetachable() {
        return this.detachableProperty().get();
    }
    
    public final BooleanProperty detachedProperty() {
        return this.detached;
    }
    
    public final void setDetached(final boolean detached) {
        this.detachedProperty().set(detached);
    }
    
    public final boolean isDetached() {
        return this.detachedProperty().get();
    }
    
    public final DoubleProperty arrowSizeProperty() {
        return this.arrowSize;
    }
    
    public final double getArrowSize() {
        return this.arrowSizeProperty().get();
    }
    
    public final void setArrowSize(final double size) {
        this.arrowSizeProperty().set(size);
    }
    
    public final DoubleProperty arrowIndentProperty() {
        return this.arrowIndent;
    }
    
    public final double getArrowIndent() {
        return this.arrowIndentProperty().get();
    }
    
    public final void setArrowIndent(final double size) {
        this.arrowIndentProperty().set(size);
    }
    
    public final DoubleProperty cornerRadiusProperty() {
        return this.cornerRadius;
    }
    
    public final double getCornerRadius() {
        return this.cornerRadiusProperty().get();
    }
    
    public final void setCornerRadius(final double radius) {
        this.cornerRadiusProperty().set(radius);
    }
    
    public final StringProperty titleProperty() {
        return this.title;
    }
    
    public final String getTitle() {
        return (String)this.titleProperty().get();
    }
    
    public final void setTitle(final String title) {
        if (title == null) {
            throw new IllegalArgumentException("title can not be null");
        }
        this.titleProperty().set((Object)title);
    }
    
    public final ObjectProperty<ArrowLocation> arrowLocationProperty() {
        return this.arrowLocation;
    }
    
    public final void setArrowLocation(final ArrowLocation location) {
        this.arrowLocationProperty().set((Object)location);
    }
    
    public final ArrowLocation getArrowLocation() {
        return (ArrowLocation)this.arrowLocationProperty().get();
    }
    
    public final ObjectProperty<Duration> fadeInDurationProperty() {
        return this.fadeInDuration;
    }
    
    public final ObjectProperty<Duration> fadeOutDurationProperty() {
        return this.fadeOutDuration;
    }
    
    public final Duration getFadeInDuration() {
        return (Duration)this.fadeInDurationProperty().get();
    }
    
    public final void setFadeInDuration(final Duration duration) {
        this.fadeInDurationProperty().setValue((Object)duration);
    }
    
    public final Duration getFadeOutDuration() {
        return (Duration)this.fadeOutDurationProperty().get();
    }
    
    public final void setFadeOutDuration(final Duration duration) {
        this.fadeOutDurationProperty().setValue((Object)duration);
    }
    
    public final BooleanProperty animatedProperty() {
        return (BooleanProperty)this.animated;
    }
    
    public final boolean isAnimated() {
        return this.animatedProperty().get();
    }
    
    public final void setAnimated(final boolean animated) {
        this.animatedProperty().set(animated);
    }
    
    static {
        DEFAULT_FADE_DURATION = Duration.seconds(0.2);
    }
    
    public enum ArrowLocation
    {
        LEFT_TOP, 
        LEFT_CENTER, 
        LEFT_BOTTOM, 
        RIGHT_TOP, 
        RIGHT_CENTER, 
        RIGHT_BOTTOM, 
        TOP_LEFT, 
        TOP_CENTER, 
        TOP_RIGHT, 
        BOTTOM_LEFT, 
        BOTTOM_CENTER, 
        BOTTOM_RIGHT;
    }
}
