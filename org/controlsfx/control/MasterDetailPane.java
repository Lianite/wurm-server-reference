// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import impl.org.controlsfx.skin.MasterDetailPaneSkin;
import javafx.scene.control.Skin;
import java.util.Objects;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Side;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.DoubleProperty;

public class MasterDetailPane extends ControlsFXControl
{
    private final DoubleProperty dividerSizeHint;
    private final ObjectProperty<Side> detailSide;
    private final BooleanProperty showDetailNode;
    private final ObjectProperty<Node> masterNode;
    private final ObjectProperty<Node> detailNode;
    private final BooleanProperty animated;
    private DoubleProperty dividerPosition;
    
    public MasterDetailPane(final Side side, final Node masterNode, final Node detailNode, final boolean showDetail) {
        this.dividerSizeHint = (DoubleProperty)new SimpleDoubleProperty((Object)this, "dividerSizeHint", 10.0) {
            public void set(final double newValue) {
                super.set(Math.max(0.0, newValue));
            }
        };
        this.detailSide = (ObjectProperty<Side>)new SimpleObjectProperty((Object)this, "detailSide", (Object)Side.RIGHT);
        this.showDetailNode = (BooleanProperty)new SimpleBooleanProperty((Object)this, "showDetailNode", true);
        this.masterNode = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "masterNode");
        this.detailNode = (ObjectProperty<Node>)new SimpleObjectProperty((Object)this, "detailNode");
        this.animated = (BooleanProperty)new SimpleBooleanProperty((Object)this, "animated", true);
        this.dividerPosition = (DoubleProperty)new SimpleDoubleProperty((Object)this, "dividerPosition", 0.33);
        Objects.requireNonNull(side);
        Objects.requireNonNull(masterNode);
        Objects.requireNonNull(detailNode);
        this.getStyleClass().add((Object)"master-detail-pane");
        this.setDetailSide(side);
        this.setMasterNode(masterNode);
        this.setDetailNode(detailNode);
        this.setShowDetailNode(showDetail);
        switch (side) {
            case BOTTOM:
            case RIGHT: {
                this.setDividerPosition(0.8);
                break;
            }
            case TOP:
            case LEFT: {
                this.setDividerPosition(0.2);
                break;
            }
        }
    }
    
    public MasterDetailPane(final Side pos, final boolean showDetail) {
        this(pos, (Node)new Placeholder(true), (Node)new Placeholder(false), showDetail);
    }
    
    public MasterDetailPane(final Side pos) {
        this(pos, (Node)new Placeholder(true), (Node)new Placeholder(false), true);
    }
    
    public MasterDetailPane() {
        this(Side.RIGHT, (Node)new Placeholder(true), (Node)new Placeholder(false), true);
    }
    
    protected Skin<?> createDefaultSkin() {
        return (Skin<?>)new MasterDetailPaneSkin(this);
    }
    
    public String getUserAgentStylesheet() {
        return this.getUserAgentStylesheet(MasterDetailPane.class, "masterdetailpane.css");
    }
    
    public final void resetDividerPosition() {
        final Node node = this.getDetailNode();
        if (node == null) {
            return;
        }
        final boolean wasShowing = this.isShowDetailNode();
        final boolean wasAnimated = this.isAnimated();
        if (!wasShowing) {
            this.setAnimated(false);
            this.setShowDetailNode(true);
            node.applyCss();
        }
        final double dividerSize = this.getDividerSizeHint();
        double ps = 0.0;
        switch (this.getDetailSide()) {
            case RIGHT:
            case LEFT: {
                ps = node.prefWidth(-1.0) + dividerSize;
                break;
            }
            default: {
                ps = node.prefHeight(-1.0) + dividerSize;
                break;
            }
        }
        double position = 0.0;
        switch (this.getDetailSide()) {
            case LEFT: {
                position = ps / this.getWidth();
                break;
            }
            case RIGHT: {
                position = 1.0 - ps / this.getWidth();
                break;
            }
            case TOP: {
                position = ps / this.getHeight();
                break;
            }
            case BOTTOM: {
                position = 1.0 - ps / this.getHeight();
                break;
            }
        }
        this.setDividerPosition(Math.min(1.0, Math.max(0.0, position)));
        if (!wasShowing) {
            this.setShowDetailNode(wasShowing);
            this.setAnimated(wasAnimated);
        }
    }
    
    public final DoubleProperty dividerSizeHintProperty() {
        return this.dividerSizeHint;
    }
    
    public final void setDividerSizeHint(final double size) {
        this.dividerSizeHint.set(size);
    }
    
    public final double getDividerSizeHint() {
        return this.dividerSizeHint.get();
    }
    
    public final ObjectProperty<Side> detailSideProperty() {
        return this.detailSide;
    }
    
    public final Side getDetailSide() {
        return (Side)this.detailSideProperty().get();
    }
    
    public final void setDetailSide(final Side side) {
        Objects.requireNonNull(side);
        this.detailSideProperty().set((Object)side);
    }
    
    public final BooleanProperty showDetailNodeProperty() {
        return this.showDetailNode;
    }
    
    public final boolean isShowDetailNode() {
        return this.showDetailNodeProperty().get();
    }
    
    public final void setShowDetailNode(final boolean show) {
        this.showDetailNodeProperty().set(show);
    }
    
    public final ObjectProperty<Node> masterNodeProperty() {
        return this.masterNode;
    }
    
    public final Node getMasterNode() {
        return (Node)this.masterNodeProperty().get();
    }
    
    public final void setMasterNode(final Node node) {
        Objects.requireNonNull(node);
        this.masterNodeProperty().set((Object)node);
    }
    
    public final ObjectProperty<Node> detailNodeProperty() {
        return this.detailNode;
    }
    
    public final Node getDetailNode() {
        return (Node)this.detailNodeProperty().get();
    }
    
    public final void setDetailNode(final Node node) {
        this.detailNodeProperty().set((Object)node);
    }
    
    public final BooleanProperty animatedProperty() {
        return this.animated;
    }
    
    public final boolean isAnimated() {
        return this.animatedProperty().get();
    }
    
    public final void setAnimated(final boolean animated) {
        this.animatedProperty().set(animated);
    }
    
    public final DoubleProperty dividerPositionProperty() {
        return this.dividerPosition;
    }
    
    public final double getDividerPosition() {
        return this.dividerPosition.get();
    }
    
    public final void setDividerPosition(final double position) {
        if (this.getDividerPosition() == position) {
            this.dividerPosition.set(-1.0);
        }
        this.dividerPosition.set(position);
    }
    
    private static final class Placeholder extends Label
    {
        public Placeholder(final boolean master) {
            super(master ? "Master" : "Detail");
            this.setAlignment(Pos.CENTER);
            if (master) {
                this.setStyle("-fx-background-color: -fx-background;");
            }
            else {
                this.setStyle("-fx-background-color: derive(-fx-background, -10%);");
            }
        }
    }
}
