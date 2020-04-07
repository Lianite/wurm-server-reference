// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import impl.org.controlsfx.skin.HiddenSidesPaneSkin;
import javafx.scene.control.Skin;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.util.Duration;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.DoubleProperty;

public class HiddenSidesPane extends ControlsFXControl
{
    private DoubleProperty triggerDistance;
    private ObjectProperty<Node> content;
    private ObjectProperty<Node> top;
    private ObjectProperty<Node> right;
    private ObjectProperty<Node> bottom;
    private ObjectProperty<Node> left;
    private ObjectProperty<Side> pinnedSide;
    private final ObjectProperty<Duration> animationDelay;
    private final ObjectProperty<Duration> animationDuration;
    
    public HiddenSidesPane(final Node content, final Node top, final Node right, final Node bottom, final Node left) {
        this.triggerDistance = (DoubleProperty)new SimpleDoubleProperty((Object)this, "triggerDistance", 16.0);
        this.content = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "content");
        this.top = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "top");
        this.right = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "right");
        this.bottom = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "bottom");
        this.left = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "left");
        this.pinnedSide = (ObjectProperty<Side>)new SimpleObjectProperty((Object)this, "pinnedSide");
        this.animationDelay = (ObjectProperty<Duration>)new SimpleObjectProperty((Object)this, "animationDelay", (Object)Duration.millis(300.0));
        this.animationDuration = (ObjectProperty<Duration>)new SimpleObjectProperty((Object)this, "animationDuration", (Object)Duration.millis(200.0));
        this.setContent(content);
        this.setTop(top);
        this.setRight(right);
        this.setBottom(bottom);
        this.setLeft(left);
    }
    
    public HiddenSidesPane() {
        this(null, null, null, null, null);
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new HiddenSidesPaneSkin(this);
    }
    
    public final DoubleProperty triggerDistanceProperty() {
        return this.triggerDistance;
    }
    
    public final double getTriggerDistance() {
        return this.triggerDistance.get();
    }
    
    public final void setTriggerDistance(final double distance) {
        this.triggerDistance.set(distance);
    }
    
    public final ObjectProperty<Node> contentProperty() {
        return this.content;
    }
    
    public final Node getContent() {
        return (Node)this.contentProperty().get();
    }
    
    public final void setContent(final Node content) {
        this.contentProperty().set((Object)content);
    }
    
    public final ObjectProperty<Node> topProperty() {
        return this.top;
    }
    
    public final Node getTop() {
        return (Node)this.topProperty().get();
    }
    
    public final void setTop(final Node top) {
        this.topProperty().set((Object)top);
    }
    
    public final ObjectProperty<Node> rightProperty() {
        return this.right;
    }
    
    public final Node getRight() {
        return (Node)this.rightProperty().get();
    }
    
    public final void setRight(final Node right) {
        this.rightProperty().set((Object)right);
    }
    
    public final ObjectProperty<Node> bottomProperty() {
        return this.bottom;
    }
    
    public final Node getBottom() {
        return (Node)this.bottomProperty().get();
    }
    
    public final void setBottom(final Node bottom) {
        this.bottomProperty().set((Object)bottom);
    }
    
    public final ObjectProperty<Node> leftProperty() {
        return this.left;
    }
    
    public final Node getLeft() {
        return (Node)this.leftProperty().get();
    }
    
    public final void setLeft(final Node left) {
        this.leftProperty().set((Object)left);
    }
    
    public final ObjectProperty<Side> pinnedSideProperty() {
        return this.pinnedSide;
    }
    
    public final Side getPinnedSide() {
        return (Side)this.pinnedSideProperty().get();
    }
    
    public final void setPinnedSide(final Side side) {
        this.pinnedSideProperty().set((Object)side);
    }
    
    public final ObjectProperty<Duration> animationDelayProperty() {
        return this.animationDelay;
    }
    
    public final Duration getAnimationDelay() {
        return (Duration)this.animationDelay.get();
    }
    
    public final void setAnimationDelay(final Duration duration) {
        this.animationDelay.set((Object)duration);
    }
    
    public final ObjectProperty<Duration> animationDurationProperty() {
        return this.animationDuration;
    }
    
    public final Duration getAnimationDuration() {
        return (Duration)this.animationDuration.get();
    }
    
    public final void setAnimationDuration(final Duration duration) {
        this.animationDuration.set((Object)duration);
    }
}
