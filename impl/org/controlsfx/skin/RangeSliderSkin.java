// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.scene.layout.BackgroundFill;
import javafx.geometry.Side;
import javafx.beans.Observable;
import javafx.scene.Cursor;
import javafx.beans.binding.ObjectBinding;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.event.Event;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import javafx.scene.input.KeyCode;
import com.sun.javafx.scene.traversal.Direction;
import java.util.List;
import javafx.scene.Node;
import com.sun.javafx.scene.traversal.TraversalContext;
import com.sun.javafx.scene.traversal.Algorithm;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import javafx.scene.control.Control;
import javafx.geometry.Point2D;
import javafx.scene.layout.StackPane;
import javafx.geometry.Orientation;
import javafx.scene.chart.NumberAxis;
import impl.org.controlsfx.behavior.RangeSliderBehavior;
import org.controlsfx.control.RangeSlider;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

public class RangeSliderSkin extends BehaviorSkinBase<RangeSlider, RangeSliderBehavior>
{
    private NumberAxis tickLine;
    private double trackToTickGap;
    private boolean showTickMarks;
    private double thumbWidth;
    private double thumbHeight;
    private Orientation orientation;
    private StackPane track;
    private double trackStart;
    private double trackLength;
    private double lowThumbPos;
    private double rangeEnd;
    private double rangeStart;
    private ThumbPane lowThumb;
    private ThumbPane highThumb;
    private StackPane rangeBar;
    private double preDragPos;
    private Point2D preDragThumbPoint;
    private RangeSliderBehavior.FocusedChild currentFocus;
    
    public RangeSliderSkin(final RangeSlider rangeSlider) {
        super((Control)rangeSlider, (BehaviorBase)new RangeSliderBehavior(rangeSlider));
        this.tickLine = null;
        this.trackToTickGap = 2.0;
        this.currentFocus = RangeSliderBehavior.FocusedChild.LOW_THUMB;
        this.orientation = ((RangeSlider)this.getSkinnable()).getOrientation();
        this.initFirstThumb();
        this.initSecondThumb();
        this.initRangeBar();
        this.registerChangeListener((ObservableValue)rangeSlider.lowValueProperty(), "LOW_VALUE");
        this.registerChangeListener((ObservableValue)rangeSlider.highValueProperty(), "HIGH_VALUE");
        this.registerChangeListener((ObservableValue)rangeSlider.minProperty(), "MIN");
        this.registerChangeListener((ObservableValue)rangeSlider.maxProperty(), "MAX");
        this.registerChangeListener((ObservableValue)rangeSlider.orientationProperty(), "ORIENTATION");
        this.registerChangeListener((ObservableValue)rangeSlider.showTickMarksProperty(), "SHOW_TICK_MARKS");
        this.registerChangeListener((ObservableValue)rangeSlider.showTickLabelsProperty(), "SHOW_TICK_LABELS");
        this.registerChangeListener((ObservableValue)rangeSlider.majorTickUnitProperty(), "MAJOR_TICK_UNIT");
        this.registerChangeListener((ObservableValue)rangeSlider.minorTickCountProperty(), "MINOR_TICK_COUNT");
        this.lowThumb.focusedProperty().addListener((ChangeListener)new ChangeListener<Boolean>() {
            public void changed(final ObservableValue<? extends Boolean> ov, final Boolean t, final Boolean hasFocus) {
                if (hasFocus) {
                    RangeSliderSkin.this.currentFocus = RangeSliderBehavior.FocusedChild.LOW_THUMB;
                }
            }
        });
        this.highThumb.focusedProperty().addListener((ChangeListener)new ChangeListener<Boolean>() {
            public void changed(final ObservableValue<? extends Boolean> ov, final Boolean t, final Boolean hasFocus) {
                if (hasFocus) {
                    RangeSliderSkin.this.currentFocus = RangeSliderBehavior.FocusedChild.HIGH_THUMB;
                }
            }
        });
        this.rangeBar.focusedProperty().addListener((ChangeListener)new ChangeListener<Boolean>() {
            public void changed(final ObservableValue<? extends Boolean> ov, final Boolean t, final Boolean hasFocus) {
                if (hasFocus) {
                    RangeSliderSkin.this.currentFocus = RangeSliderBehavior.FocusedChild.RANGE_BAR;
                }
            }
        });
        rangeSlider.focusedProperty().addListener((ChangeListener)new ChangeListener<Boolean>() {
            public void changed(final ObservableValue<? extends Boolean> ov, final Boolean t, final Boolean hasFocus) {
                if (hasFocus) {
                    RangeSliderSkin.this.lowThumb.setFocus(true);
                }
                else {
                    RangeSliderSkin.this.lowThumb.setFocus(false);
                    RangeSliderSkin.this.highThumb.setFocus(false);
                    RangeSliderSkin.this.currentFocus = RangeSliderBehavior.FocusedChild.NONE;
                }
            }
        });
        final EventHandler<KeyEvent> keyEventHandler = (EventHandler<KeyEvent>)new EventHandler<KeyEvent>() {
            private final Algorithm algorithm = new Algorithm() {
                public Node selectLast(final TraversalContext context) {
                    final List<Node> focusTraversableNodes = (List<Node>)context.getAllTargetNodes();
                    return focusTraversableNodes.get(focusTraversableNodes.size() - 1);
                }
                
                public Node selectFirst(final TraversalContext context) {
                    return context.getAllTargetNodes().get(0);
                }
                
                public Node select(final Node owner, final Direction dir, final TraversalContext context) {
                    int direction = 0;
                    switch (dir) {
                        case DOWN:
                        case RIGHT:
                        case NEXT:
                        case NEXT_IN_LINE: {
                            direction = 1;
                            break;
                        }
                        case LEFT:
                        case PREVIOUS:
                        case UP: {
                            direction = -2;
                            break;
                        }
                        default: {
                            throw new EnumConstantNotPresentException((Class<? extends Enum>)dir.getClass(), dir.name());
                        }
                    }
                    final List<Node> focusTraversableNodes = (List<Node>)context.getAllTargetNodes();
                    final int focusReceiverIndex = focusTraversableNodes.indexOf(owner) + direction;
                    if (focusReceiverIndex < 0) {
                        return focusTraversableNodes.get(focusTraversableNodes.size() - 1);
                    }
                    if (focusReceiverIndex == focusTraversableNodes.size()) {
                        return focusTraversableNodes.get(0);
                    }
                    return focusTraversableNodes.get(focusReceiverIndex);
                }
            };
            
            public void handle(final KeyEvent event) {
                if (KeyCode.TAB.equals((Object)event.getCode())) {
                    if (RangeSliderSkin.this.lowThumb.isFocused()) {
                        if (event.isShiftDown()) {
                            RangeSliderSkin.this.lowThumb.setFocus(false);
                            new ParentTraversalEngine(rangeSlider.getScene().getRoot(), this.algorithm).select((Node)RangeSliderSkin.this.lowThumb, Direction.PREVIOUS).requestFocus();
                        }
                        else {
                            RangeSliderSkin.this.lowThumb.setFocus(false);
                            RangeSliderSkin.this.highThumb.setFocus(true);
                        }
                        event.consume();
                    }
                    else if (RangeSliderSkin.this.highThumb.isFocused()) {
                        if (event.isShiftDown()) {
                            RangeSliderSkin.this.highThumb.setFocus(false);
                            RangeSliderSkin.this.lowThumb.setFocus(true);
                        }
                        else {
                            RangeSliderSkin.this.highThumb.setFocus(false);
                            new ParentTraversalEngine(rangeSlider.getScene().getRoot(), this.algorithm).select((Node)RangeSliderSkin.this.highThumb, Direction.NEXT).requestFocus();
                        }
                        event.consume();
                    }
                }
            }
        };
        ((RangeSlider)this.getSkinnable()).addEventHandler(KeyEvent.KEY_PRESSED, (EventHandler)keyEventHandler);
        ((RangeSliderBehavior)this.getBehavior()).setSelectedValue((Callback<Void, RangeSliderBehavior.FocusedChild>)new Callback<Void, RangeSliderBehavior.FocusedChild>() {
            public RangeSliderBehavior.FocusedChild call(final Void v) {
                return RangeSliderSkin.this.currentFocus;
            }
        });
    }
    
    private void initFirstThumb() {
        this.lowThumb = new ThumbPane();
        this.lowThumb.getStyleClass().setAll((Object[])new String[] { "low-thumb" });
        this.lowThumb.setFocusTraversable(true);
        this.track = new StackPane();
        this.track.getStyleClass().setAll((Object[])new String[] { "track" });
        this.getChildren().clear();
        this.getChildren().addAll((Object[])new Node[] { this.track, this.lowThumb });
        this.setShowTickMarks(((RangeSlider)this.getSkinnable()).isShowTickMarks(), ((RangeSlider)this.getSkinnable()).isShowTickLabels());
        this.track.setOnMousePressed((EventHandler)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent me) {
                if (!RangeSliderSkin.this.lowThumb.isPressed() && !RangeSliderSkin.this.highThumb.isPressed()) {
                    if (RangeSliderSkin.this.isHorizontal()) {
                        ((RangeSliderBehavior)RangeSliderSkin.this.getBehavior()).trackPress(me, me.getX() / RangeSliderSkin.this.trackLength);
                    }
                    else {
                        ((RangeSliderBehavior)RangeSliderSkin.this.getBehavior()).trackPress(me, me.getY() / RangeSliderSkin.this.trackLength);
                    }
                }
            }
        });
        this.track.setOnMouseReleased((EventHandler)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent me) {
                ((RangeSliderBehavior)RangeSliderSkin.this.getBehavior()).trackRelease(me, 0.0);
            }
        });
        this.lowThumb.setOnMousePressed((EventHandler)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent me) {
                RangeSliderSkin.this.highThumb.setFocus(false);
                RangeSliderSkin.this.lowThumb.setFocus(true);
                ((RangeSliderBehavior)RangeSliderSkin.this.getBehavior()).lowThumbPressed(me, 0.0);
                RangeSliderSkin.this.preDragThumbPoint = RangeSliderSkin.this.lowThumb.localToParent(me.getX(), me.getY());
                RangeSliderSkin.this.preDragPos = (((RangeSlider)RangeSliderSkin.this.getSkinnable()).getLowValue() - ((RangeSlider)RangeSliderSkin.this.getSkinnable()).getMin()) / RangeSliderSkin.this.getMaxMinusMinNoZero();
            }
        });
        this.lowThumb.setOnMouseReleased((EventHandler)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent me) {
                ((RangeSliderBehavior)RangeSliderSkin.this.getBehavior()).lowThumbReleased(me);
            }
        });
        this.lowThumb.setOnMouseDragged((EventHandler)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent me) {
                final Point2D cur = RangeSliderSkin.this.lowThumb.localToParent(me.getX(), me.getY());
                final double dragPos = RangeSliderSkin.this.isHorizontal() ? (cur.getX() - RangeSliderSkin.this.preDragThumbPoint.getX()) : (-(cur.getY() - RangeSliderSkin.this.preDragThumbPoint.getY()));
                ((RangeSliderBehavior)RangeSliderSkin.this.getBehavior()).lowThumbDragged(me, RangeSliderSkin.this.preDragPos + dragPos / RangeSliderSkin.this.trackLength);
            }
        });
    }
    
    private void initSecondThumb() {
        this.highThumb = new ThumbPane();
        this.highThumb.getStyleClass().setAll((Object[])new String[] { "high-thumb" });
        this.highThumb.setFocusTraversable(true);
        if (!this.getChildren().contains((Object)this.highThumb)) {
            this.getChildren().add((Object)this.highThumb);
        }
        this.highThumb.setOnMousePressed((EventHandler)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent e) {
                RangeSliderSkin.this.lowThumb.setFocus(false);
                RangeSliderSkin.this.highThumb.setFocus(true);
                ((RangeSliderBehavior)RangeSliderSkin.this.getBehavior()).highThumbPressed(e, 0.0);
                RangeSliderSkin.this.preDragThumbPoint = RangeSliderSkin.this.highThumb.localToParent(e.getX(), e.getY());
                RangeSliderSkin.this.preDragPos = (((RangeSlider)RangeSliderSkin.this.getSkinnable()).getHighValue() - ((RangeSlider)RangeSliderSkin.this.getSkinnable()).getMin()) / RangeSliderSkin.this.getMaxMinusMinNoZero();
            }
        });
        this.highThumb.setOnMouseReleased((EventHandler)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent e) {
                ((RangeSliderBehavior)RangeSliderSkin.this.getBehavior()).highThumbReleased(e);
            }
        });
        this.highThumb.setOnMouseDragged((EventHandler)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent e) {
                final boolean orientation = ((RangeSlider)RangeSliderSkin.this.getSkinnable()).getOrientation() == Orientation.HORIZONTAL;
                final double trackLength = orientation ? RangeSliderSkin.this.track.getWidth() : RangeSliderSkin.this.track.getHeight();
                final Point2D point2d = RangeSliderSkin.this.highThumb.localToParent(e.getX(), e.getY());
                final double d = (((RangeSlider)RangeSliderSkin.this.getSkinnable()).getOrientation() != Orientation.HORIZONTAL) ? (-(point2d.getY() - RangeSliderSkin.this.preDragThumbPoint.getY())) : (point2d.getX() - RangeSliderSkin.this.preDragThumbPoint.getX());
                ((RangeSliderBehavior)RangeSliderSkin.this.getBehavior()).highThumbDragged(e, RangeSliderSkin.this.preDragPos + d / trackLength);
            }
        });
    }
    
    private void initRangeBar() {
        this.rangeBar = new StackPane();
        this.rangeBar.cursorProperty().bind((ObservableValue)new ObjectBinding<Cursor>() {
            {
                this.bind(new Observable[] { RangeSliderSkin.this.rangeBar.hoverProperty() });
            }
            
            protected Cursor computeValue() {
                return RangeSliderSkin.this.rangeBar.isHover() ? Cursor.HAND : Cursor.DEFAULT;
            }
        });
        this.rangeBar.getStyleClass().setAll((Object[])new String[] { "range-bar" });
        this.rangeBar.setOnMousePressed((EventHandler)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent e) {
                RangeSliderSkin.this.rangeBar.requestFocus();
                RangeSliderSkin.this.preDragPos = (RangeSliderSkin.this.isHorizontal() ? e.getX() : (-e.getY()));
            }
        });
        this.rangeBar.setOnMouseDragged((EventHandler)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent e) {
                final double delta = (RangeSliderSkin.this.isHorizontal() ? e.getX() : (-e.getY())) - RangeSliderSkin.this.preDragPos;
                ((RangeSliderBehavior)RangeSliderSkin.this.getBehavior()).moveRange(delta);
            }
        });
        this.rangeBar.setOnMouseReleased((EventHandler)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent e) {
                ((RangeSliderBehavior)RangeSliderSkin.this.getBehavior()).confirmRange();
            }
        });
        this.getChildren().add((Object)this.rangeBar);
    }
    
    private void setShowTickMarks(final boolean ticksVisible, final boolean labelsVisible) {
        this.showTickMarks = (ticksVisible || labelsVisible);
        final RangeSlider rangeSlider = (RangeSlider)this.getSkinnable();
        if (this.showTickMarks) {
            if (this.tickLine == null) {
                this.tickLine = new NumberAxis();
                this.tickLine.tickLabelFormatterProperty().bind((ObservableValue)((RangeSlider)this.getSkinnable()).labelFormatterProperty());
                this.tickLine.setAnimated(false);
                this.tickLine.setAutoRanging(false);
                this.tickLine.setSide(this.isHorizontal() ? Side.BOTTOM : Side.RIGHT);
                this.tickLine.setUpperBound(rangeSlider.getMax());
                this.tickLine.setLowerBound(rangeSlider.getMin());
                this.tickLine.setTickUnit(rangeSlider.getMajorTickUnit());
                this.tickLine.setTickMarkVisible(ticksVisible);
                this.tickLine.setTickLabelsVisible(labelsVisible);
                this.tickLine.setMinorTickVisible(ticksVisible);
                this.tickLine.setMinorTickCount(Math.max(rangeSlider.getMinorTickCount(), 0) + 1);
                this.getChildren().clear();
                this.getChildren().addAll((Object[])new Node[] { this.tickLine, this.track, this.lowThumb });
            }
            else {
                this.tickLine.setTickLabelsVisible(labelsVisible);
                this.tickLine.setTickMarkVisible(ticksVisible);
                this.tickLine.setMinorTickVisible(ticksVisible);
            }
        }
        else {
            this.getChildren().clear();
            this.getChildren().addAll((Object[])new Node[] { this.track, this.lowThumb });
        }
        ((RangeSlider)this.getSkinnable()).requestLayout();
    }
    
    protected void handleControlPropertyChanged(final String p) {
        super.handleControlPropertyChanged(p);
        if ("ORIENTATION".equals(p)) {
            this.orientation = ((RangeSlider)this.getSkinnable()).getOrientation();
            if (this.showTickMarks && this.tickLine != null) {
                this.tickLine.setSide(this.isHorizontal() ? Side.BOTTOM : Side.RIGHT);
            }
            ((RangeSlider)this.getSkinnable()).requestLayout();
        }
        else if ("MIN".equals(p)) {
            if (this.showTickMarks && this.tickLine != null) {
                this.tickLine.setLowerBound(((RangeSlider)this.getSkinnable()).getMin());
            }
            ((RangeSlider)this.getSkinnable()).requestLayout();
        }
        else if ("MAX".equals(p)) {
            if (this.showTickMarks && this.tickLine != null) {
                this.tickLine.setUpperBound(((RangeSlider)this.getSkinnable()).getMax());
            }
            ((RangeSlider)this.getSkinnable()).requestLayout();
        }
        else if ("SHOW_TICK_MARKS".equals(p) || "SHOW_TICK_LABELS".equals(p)) {
            this.setShowTickMarks(((RangeSlider)this.getSkinnable()).isShowTickMarks(), ((RangeSlider)this.getSkinnable()).isShowTickLabels());
            if (!this.getChildren().contains((Object)this.highThumb)) {
                this.getChildren().add((Object)this.highThumb);
            }
            if (!this.getChildren().contains((Object)this.rangeBar)) {
                this.getChildren().add((Object)this.rangeBar);
            }
        }
        else if ("MAJOR_TICK_UNIT".equals(p)) {
            if (this.tickLine != null) {
                this.tickLine.setTickUnit(((RangeSlider)this.getSkinnable()).getMajorTickUnit());
                ((RangeSlider)this.getSkinnable()).requestLayout();
            }
        }
        else if ("MINOR_TICK_COUNT".equals(p)) {
            if (this.tickLine != null) {
                this.tickLine.setMinorTickCount(Math.max(((RangeSlider)this.getSkinnable()).getMinorTickCount(), 0) + 1);
                ((RangeSlider)this.getSkinnable()).requestLayout();
            }
        }
        else if ("LOW_VALUE".equals(p)) {
            this.positionLowThumb();
            this.rangeBar.resizeRelocate(this.rangeStart, this.rangeBar.getLayoutY(), this.rangeEnd - this.rangeStart, this.rangeBar.getHeight());
        }
        else if ("HIGH_VALUE".equals(p)) {
            this.positionHighThumb();
            this.rangeBar.resize(this.rangeEnd - this.rangeStart, this.rangeBar.getHeight());
        }
        super.handleControlPropertyChanged(p);
    }
    
    private double getMaxMinusMinNoZero() {
        final RangeSlider s = (RangeSlider)this.getSkinnable();
        return (s.getMax() - s.getMin() == 0.0) ? 1.0 : (s.getMax() - s.getMin());
    }
    
    private void positionLowThumb() {
        final RangeSlider s = (RangeSlider)this.getSkinnable();
        final boolean horizontal = this.isHorizontal();
        final double lx = horizontal ? (this.trackStart + (this.trackLength * ((s.getLowValue() - s.getMin()) / this.getMaxMinusMinNoZero()) - this.thumbWidth / 2.0)) : this.lowThumbPos;
        final double ly = horizontal ? this.lowThumbPos : (((RangeSlider)this.getSkinnable()).getInsets().getTop() + this.trackLength - this.trackLength * ((s.getLowValue() - s.getMin()) / this.getMaxMinusMinNoZero()));
        this.lowThumb.setLayoutX(lx);
        this.lowThumb.setLayoutY(ly);
        if (horizontal) {
            this.rangeStart = lx + this.thumbWidth;
        }
        else {
            this.rangeEnd = ly;
        }
    }
    
    private void positionHighThumb() {
        final RangeSlider slider = (RangeSlider)this.getSkinnable();
        final boolean orientation = ((RangeSlider)this.getSkinnable()).getOrientation() == Orientation.HORIZONTAL;
        final double thumbWidth = this.lowThumb.getWidth();
        final double thumbHeight = this.lowThumb.getHeight();
        this.highThumb.resize(thumbWidth, thumbHeight);
        final double pad = 0.0;
        double trackStart = orientation ? this.track.getLayoutX() : this.track.getLayoutY();
        trackStart += pad;
        double trackLength = orientation ? this.track.getWidth() : this.track.getHeight();
        trackLength -= 2.0 * pad;
        final double x = orientation ? (trackStart + (trackLength * ((slider.getHighValue() - slider.getMin()) / this.getMaxMinusMinNoZero()) - thumbWidth / 2.0)) : this.lowThumb.getLayoutX();
        final double y = orientation ? this.lowThumb.getLayoutY() : (((RangeSlider)this.getSkinnable()).getInsets().getTop() + trackLength - trackLength * ((slider.getHighValue() - slider.getMin()) / this.getMaxMinusMinNoZero()));
        this.highThumb.setLayoutX(x);
        this.highThumb.setLayoutY(y);
        if (orientation) {
            this.rangeEnd = x;
        }
        else {
            this.rangeStart = y + thumbWidth;
        }
    }
    
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        this.thumbWidth = this.lowThumb.prefWidth(-1.0);
        this.thumbHeight = this.lowThumb.prefHeight(-1.0);
        this.lowThumb.resize(this.thumbWidth, this.thumbHeight);
        final double trackRadius = (this.track.getBackground() == null) ? 0.0 : ((this.track.getBackground().getFills().size() > 0) ? this.track.getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius() : 0.0);
        if (this.isHorizontal()) {
            final double tickLineHeight = this.showTickMarks ? this.tickLine.prefHeight(-1.0) : 0.0;
            final double trackHeight = this.track.prefHeight(-1.0);
            final double trackAreaHeight = Math.max(trackHeight, this.thumbHeight);
            final double totalHeightNeeded = trackAreaHeight + (this.showTickMarks ? (this.trackToTickGap + tickLineHeight) : 0.0);
            final double startY = y + (h - totalHeightNeeded) / 2.0;
            this.trackLength = w - this.thumbWidth;
            this.trackStart = x + this.thumbWidth / 2.0;
            final double trackTop = (int)(startY + (trackAreaHeight - trackHeight) / 2.0);
            this.lowThumbPos = (int)(startY + (trackAreaHeight - this.thumbHeight) / 2.0);
            this.positionLowThumb();
            this.track.resizeRelocate(this.trackStart - trackRadius, trackTop, this.trackLength + trackRadius + trackRadius, trackHeight);
            this.positionHighThumb();
            this.rangeBar.resizeRelocate(this.rangeStart, trackTop, this.rangeEnd - this.rangeStart, trackHeight);
            if (this.showTickMarks) {
                this.tickLine.setLayoutX(this.trackStart);
                this.tickLine.setLayoutY(trackTop + trackHeight + this.trackToTickGap);
                this.tickLine.resize(this.trackLength, tickLineHeight);
                this.tickLine.requestAxisLayout();
            }
            else {
                if (this.tickLine != null) {
                    this.tickLine.resize(0.0, 0.0);
                    this.tickLine.requestAxisLayout();
                }
                this.tickLine = null;
            }
        }
        else {
            final double tickLineWidth = this.showTickMarks ? this.tickLine.prefWidth(-1.0) : 0.0;
            final double trackWidth = this.track.prefWidth(-1.0);
            final double trackAreaWidth = Math.max(trackWidth, this.thumbWidth);
            final double totalWidthNeeded = trackAreaWidth + (this.showTickMarks ? (this.trackToTickGap + tickLineWidth) : 0.0);
            final double startX = x + (w - totalWidthNeeded) / 2.0;
            this.trackLength = h - this.thumbHeight;
            this.trackStart = y + this.thumbHeight / 2.0;
            final double trackLeft = (int)(startX + (trackAreaWidth - trackWidth) / 2.0);
            this.lowThumbPos = (int)(startX + (trackAreaWidth - this.thumbWidth) / 2.0);
            this.positionLowThumb();
            this.track.resizeRelocate(trackLeft, this.trackStart - trackRadius, trackWidth, this.trackLength + trackRadius + trackRadius);
            this.positionHighThumb();
            this.rangeBar.resizeRelocate(trackLeft, this.rangeStart, trackWidth, this.rangeEnd - this.rangeStart);
            if (this.showTickMarks) {
                this.tickLine.setLayoutX(trackLeft + trackWidth + this.trackToTickGap);
                this.tickLine.setLayoutY(this.trackStart);
                this.tickLine.resize(tickLineWidth, this.trackLength);
                this.tickLine.requestAxisLayout();
            }
            else {
                if (this.tickLine != null) {
                    this.tickLine.resize(0.0, 0.0);
                    this.tickLine.requestAxisLayout();
                }
                this.tickLine = null;
            }
        }
    }
    
    private double minTrackLength() {
        return 2.0 * this.lowThumb.prefWidth(-1.0);
    }
    
    protected double computeMinWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        if (this.isHorizontal()) {
            return leftInset + this.minTrackLength() + this.lowThumb.minWidth(-1.0) + rightInset;
        }
        return leftInset + this.lowThumb.prefWidth(-1.0) + rightInset;
    }
    
    protected double computeMinHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        if (this.isHorizontal()) {
            return topInset + this.lowThumb.prefHeight(-1.0) + bottomInset;
        }
        return topInset + this.minTrackLength() + this.lowThumb.prefHeight(-1.0) + bottomInset;
    }
    
    protected double computePrefWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        if (!this.isHorizontal()) {
            return leftInset + Math.max(this.lowThumb.prefWidth(-1.0), this.track.prefWidth(-1.0)) + (this.showTickMarks ? (this.trackToTickGap + this.tickLine.prefWidth(-1.0)) : 0.0) + rightInset;
        }
        if (this.showTickMarks) {
            return Math.max(140.0, this.tickLine.prefWidth(-1.0));
        }
        return 140.0;
    }
    
    protected double computePrefHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        if (this.isHorizontal()) {
            return ((RangeSlider)this.getSkinnable()).getInsets().getTop() + Math.max(this.lowThumb.prefHeight(-1.0), this.track.prefHeight(-1.0)) + (this.showTickMarks ? (this.trackToTickGap + this.tickLine.prefHeight(-1.0)) : 0.0) + bottomInset;
        }
        if (this.showTickMarks) {
            return Math.max(140.0, this.tickLine.prefHeight(-1.0));
        }
        return 140.0;
    }
    
    protected double computeMaxWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        if (this.isHorizontal()) {
            return Double.MAX_VALUE;
        }
        return ((RangeSlider)this.getSkinnable()).prefWidth(-1.0);
    }
    
    protected double computeMaxHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        if (this.isHorizontal()) {
            return ((RangeSlider)this.getSkinnable()).prefHeight(width);
        }
        return Double.MAX_VALUE;
    }
    
    private boolean isHorizontal() {
        return this.orientation == null || this.orientation == Orientation.HORIZONTAL;
    }
    
    private static class ThumbPane extends StackPane
    {
        public void setFocus(final boolean value) {
            this.setFocused(value);
        }
    }
}
