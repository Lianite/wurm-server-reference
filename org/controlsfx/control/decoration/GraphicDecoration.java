// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.decoration;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.beans.value.ChangeListener;
import java.util.List;
import impl.org.controlsfx.ImplUtils;
import javafx.scene.Parent;
import javafx.geometry.Pos;
import javafx.scene.Node;

public class GraphicDecoration extends Decoration
{
    private final Node decorationNode;
    private final Pos pos;
    private final double xOffset;
    private final double yOffset;
    
    public GraphicDecoration(final Node decorationNode) {
        this(decorationNode, Pos.TOP_LEFT);
    }
    
    public GraphicDecoration(final Node decorationNode, final Pos position) {
        this(decorationNode, position, 0.0, 0.0);
    }
    
    public GraphicDecoration(final Node decorationNode, final Pos position, final double xOffset, final double yOffset) {
        (this.decorationNode = decorationNode).setManaged(false);
        this.pos = position;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
    
    @Override
    public Node applyDecoration(final Node targetNode) {
        final List<Node> targetNodeChildren = ImplUtils.getChildren((Parent)targetNode, true);
        this.updateGraphicPosition(targetNode);
        if (!targetNodeChildren.contains(this.decorationNode)) {
            targetNodeChildren.add(this.decorationNode);
        }
        return null;
    }
    
    @Override
    public void removeDecoration(final Node targetNode) {
        final List<Node> targetNodeChildren = ImplUtils.getChildren((Parent)targetNode, true);
        if (targetNodeChildren.contains(this.decorationNode)) {
            targetNodeChildren.remove(this.decorationNode);
        }
    }
    
    private void updateGraphicPosition(final Node targetNode) {
        final double decorationNodeWidth = this.decorationNode.prefWidth(-1.0);
        final double decorationNodeHeight = this.decorationNode.prefHeight(-1.0);
        final Bounds targetBounds = targetNode.getLayoutBounds();
        double x = targetBounds.getMinX();
        double y = targetBounds.getMinY();
        double targetWidth = targetBounds.getWidth();
        if (targetWidth <= 0.0) {
            targetWidth = targetNode.prefWidth(-1.0);
        }
        double targetHeight = targetBounds.getHeight();
        if (targetHeight <= 0.0) {
            targetHeight = targetNode.prefHeight(-1.0);
        }
        if (targetWidth <= 0.0 && targetHeight <= 0.0) {
            targetNode.layoutBoundsProperty().addListener((ChangeListener)new ChangeListener<Bounds>() {
                public void changed(final ObservableValue<? extends Bounds> observable, final Bounds oldValue, final Bounds newValue) {
                    targetNode.layoutBoundsProperty().removeListener((ChangeListener)this);
                    GraphicDecoration.this.updateGraphicPosition(targetNode);
                }
            });
        }
        switch (this.pos.getHpos()) {
            case CENTER: {
                x += targetWidth / 2.0 - decorationNodeWidth / 2.0;
                break;
            }
            case LEFT: {
                x -= decorationNodeWidth / 2.0;
                break;
            }
            case RIGHT: {
                x += targetWidth - decorationNodeWidth / 2.0;
                break;
            }
        }
        switch (this.pos.getVpos()) {
            case CENTER: {
                y += targetHeight / 2.0 - decorationNodeHeight / 2.0;
                break;
            }
            case TOP: {
                y -= decorationNodeHeight / 2.0;
                break;
            }
            case BOTTOM: {
                y += targetHeight - decorationNodeWidth / 2.0;
                break;
            }
            case BASELINE: {
                y += targetNode.getBaselineOffset() - this.decorationNode.getBaselineOffset() - decorationNodeHeight / 2.0;
                break;
            }
        }
        this.decorationNode.setLayoutX(x + this.xOffset);
        this.decorationNode.setLayoutY(y + this.yOffset);
    }
}
