// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.beans.Observable;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import java.util.List;
import java.util.OptionalDouble;
import javafx.geometry.Orientation;
import java.util.HashMap;
import javafx.scene.control.Control;
import org.controlsfx.control.PopOver;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.InvalidationListener;
import javafx.scene.Node;
import java.util.Map;
import javafx.scene.control.SkinBase;
import org.controlsfx.control.SegmentedBar;

public class SegmentedBarSkin<T extends SegmentedBar.Segment> extends SkinBase<SegmentedBar<T>>
{
    private Map<T, Node> segmentNodes;
    private InvalidationListener buildListener;
    private WeakInvalidationListener weakBuildListener;
    private InvalidationListener layoutListener;
    private WeakInvalidationListener weakLayoutListener;
    private PopOver popOver;
    
    public SegmentedBarSkin(final SegmentedBar<T> bar) {
        super((Control)bar);
        this.segmentNodes = new HashMap<T, Node>();
        this.buildListener = (it -> this.buildSegments());
        this.weakBuildListener = new WeakInvalidationListener(this.buildListener);
        this.layoutListener = (it -> ((SegmentedBar)this.getSkinnable()).requestLayout());
        this.weakLayoutListener = new WeakInvalidationListener(this.layoutListener);
        bar.segmentViewFactoryProperty().addListener((InvalidationListener)this.weakBuildListener);
        bar.getSegments().addListener((InvalidationListener)this.weakBuildListener);
        bar.orientationProperty().addListener((InvalidationListener)this.weakLayoutListener);
        bar.totalProperty().addListener((InvalidationListener)this.weakBuildListener);
        bar.orientationProperty().addListener(it -> {
            if (this.popOver == null) {
                return;
            }
            switch (bar.getOrientation()) {
                case HORIZONTAL: {
                    this.popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
                    break;
                }
                case VERTICAL: {
                    this.popOver.setArrowLocation(PopOver.ArrowLocation.RIGHT_CENTER);
                    break;
                }
            }
        });
        this.buildSegments();
    }
    
    protected double computePrefHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        if (((SegmentedBar)this.getSkinnable()).getOrientation().equals((Object)Orientation.HORIZONTAL)) {
            final OptionalDouble maxHeight = this.getChildren().stream().mapToDouble(node -> node.prefHeight(-1.0)).max();
            if (maxHeight.isPresent()) {
                return maxHeight.getAsDouble();
            }
        }
        return ((SegmentedBar)this.getSkinnable()).getPrefHeight();
    }
    
    protected double computePrefWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        if (((SegmentedBar)this.getSkinnable()).getOrientation().equals((Object)Orientation.VERTICAL)) {
            final OptionalDouble maxWidth = this.getChildren().stream().mapToDouble(node -> node.prefWidth(height)).max();
            if (maxWidth.isPresent()) {
                return maxWidth.getAsDouble();
            }
        }
        return ((SegmentedBar)this.getSkinnable()).getPrefWidth();
    }
    
    protected double computeMinHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        if (((SegmentedBar)this.getSkinnable()).getOrientation().equals((Object)Orientation.HORIZONTAL)) {
            return this.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
        }
        return 0.0;
    }
    
    protected double computeMinWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        if (((SegmentedBar)this.getSkinnable()).getOrientation().equals((Object)Orientation.VERTICAL)) {
            return this.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
        }
        return 0.0;
    }
    
    protected double computeMaxHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        if (((SegmentedBar)this.getSkinnable()).getOrientation().equals((Object)Orientation.HORIZONTAL)) {
            return this.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
        }
        return Double.MAX_VALUE;
    }
    
    protected double computeMaxWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        if (((SegmentedBar)this.getSkinnable()).getOrientation().equals((Object)Orientation.VERTICAL)) {
            return this.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
        }
        return Double.MAX_VALUE;
    }
    
    private void buildSegments() {
        this.segmentNodes.clear();
        this.getChildren().clear();
        final List<T> segments = (List<T>)((SegmentedBar)this.getSkinnable()).getSegments();
        final int size = segments.size();
        final Callback<T, Node> cellFactory = ((SegmentedBar)this.getSkinnable()).getSegmentViewFactory();
        for (int i = 0; i < size; ++i) {
            final T segment = segments.get(i);
            final Node segmentNode = (Node)cellFactory.call((Object)segment);
            this.segmentNodes.put(segment, segmentNode);
            this.getChildren().add((Object)segmentNode);
            segmentNode.getStyleClass().add((Object)"segment");
            if (i == 0) {
                if (size == 1) {
                    segmentNode.getStyleClass().add((Object)"only-segment");
                }
                else {
                    segmentNode.getStyleClass().add((Object)"first-segment");
                }
            }
            else if (i == size - 1) {
                segmentNode.getStyleClass().add((Object)"last-segment");
            }
            else {
                segmentNode.getStyleClass().add((Object)"middle-segment");
            }
            segmentNode.setOnMouseEntered(evt -> this.showPopOver(segmentNode, segment));
            segmentNode.setOnMouseExited(evt -> this.hidePopOver());
        }
        ((SegmentedBar)this.getSkinnable()).requestLayout();
    }
    
    private void showPopOver(final Node owner, final T segment) {
        final Callback<T, Node> infoNodeFactory = ((SegmentedBar)this.getSkinnable()).getInfoNodeFactory();
        Node infoNode = null;
        if (infoNodeFactory != null) {
            infoNode = (Node)infoNodeFactory.call((Object)segment);
        }
        if (infoNode != null) {
            if (this.popOver == null) {
                (this.popOver = new PopOver()).setAnimated(false);
                this.popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
                this.popOver.setDetachable(false);
                this.popOver.setArrowSize(6.0);
                this.popOver.setCornerRadius(3.0);
                this.popOver.setAutoFix(false);
                this.popOver.setAutoHide(true);
            }
            this.popOver.setContentNode(infoNode);
            this.popOver.show(owner, -2.0);
        }
    }
    
    private void hidePopOver() {
        if (this.popOver != null && this.popOver.isShowing()) {
            this.popOver.hide();
        }
    }
    
    protected void layoutChildren(final double contentX, final double contentY, final double contentWidth, final double contentHeight) {
        final double total = ((SegmentedBar)this.getSkinnable()).getTotal();
        final List<T> segments = (List<T>)((SegmentedBar)this.getSkinnable()).getSegments();
        final int size = segments.size();
        double x = contentX;
        double y = contentY + contentHeight;
        for (int i = 0; i < size; ++i) {
            final SegmentedBar.Segment segment = segments.get(i);
            final Node segmentNode = this.segmentNodes.get(segment);
            final double segmentValue = segment.getValue();
            if (((SegmentedBar)this.getSkinnable()).getOrientation().equals((Object)Orientation.HORIZONTAL)) {
                final double segmentWidth = segmentValue / total * contentWidth;
                segmentNode.resizeRelocate(x, contentY, segmentWidth, contentHeight);
                x += segmentWidth;
            }
            else {
                final double segmentHeight = segmentValue / total * contentHeight;
                segmentNode.resizeRelocate(contentX, y - segmentHeight, contentWidth, segmentHeight);
                y -= segmentHeight;
            }
        }
    }
}
