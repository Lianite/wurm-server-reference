// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.beans.Observable;
import javafx.scene.control.Skinnable;
import java.util.Collection;
import javafx.scene.shape.PathElement;
import java.util.ArrayList;
import javafx.stage.Window;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.Line;
import javafx.scene.shape.Circle;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.beans.InvalidationListener;
import javafx.event.EventHandler;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.Node;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.control.ContentDisplay;
import javafx.geometry.Pos;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableNumberValue;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.scene.shape.VLineTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.geometry.Point2D;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Path;
import javafx.scene.control.Label;
import org.controlsfx.control.PopOver;
import javafx.scene.control.Skin;

public class PopOverSkin implements Skin<PopOver>
{
    private static final String DETACHED_STYLE_CLASS = "detached";
    private double xOffset;
    private double yOffset;
    private boolean tornOff;
    private Label title;
    private Label closeIcon;
    private Path path;
    private Path clip;
    private BorderPane content;
    private StackPane titlePane;
    private StackPane stackPane;
    private Point2D dragStartLocation;
    private PopOver popOver;
    private MoveTo moveTo;
    private QuadCurveTo topCurveTo;
    private QuadCurveTo rightCurveTo;
    private QuadCurveTo bottomCurveTo;
    private QuadCurveTo leftCurveTo;
    private HLineTo lineBTop;
    private HLineTo lineETop;
    private HLineTo lineHTop;
    private HLineTo lineKTop;
    private LineTo lineCTop;
    private LineTo lineDTop;
    private LineTo lineFTop;
    private LineTo lineGTop;
    private LineTo lineITop;
    private LineTo lineJTop;
    private VLineTo lineBRight;
    private VLineTo lineERight;
    private VLineTo lineHRight;
    private VLineTo lineKRight;
    private LineTo lineCRight;
    private LineTo lineDRight;
    private LineTo lineFRight;
    private LineTo lineGRight;
    private LineTo lineIRight;
    private LineTo lineJRight;
    private HLineTo lineBBottom;
    private HLineTo lineEBottom;
    private HLineTo lineHBottom;
    private HLineTo lineKBottom;
    private LineTo lineCBottom;
    private LineTo lineDBottom;
    private LineTo lineFBottom;
    private LineTo lineGBottom;
    private LineTo lineIBottom;
    private LineTo lineJBottom;
    private VLineTo lineBLeft;
    private VLineTo lineELeft;
    private VLineTo lineHLeft;
    private VLineTo lineKLeft;
    private LineTo lineCLeft;
    private LineTo lineDLeft;
    private LineTo lineFLeft;
    private LineTo lineGLeft;
    private LineTo lineILeft;
    private LineTo lineJLeft;
    
    public PopOverSkin(final PopOver popOver) {
        this.popOver = popOver;
        (this.stackPane = popOver.getRoot()).setPickOnBounds(false);
        Bindings.bindContent((List)this.stackPane.getStyleClass(), popOver.getStyleClass());
        this.stackPane.minWidthProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)Bindings.multiply(2, (ObservableNumberValue)popOver.arrowSizeProperty()), (ObservableNumberValue)Bindings.add((ObservableNumberValue)Bindings.multiply(2, (ObservableNumberValue)popOver.cornerRadiusProperty()), (ObservableNumberValue)Bindings.multiply(2, (ObservableNumberValue)popOver.arrowIndentProperty()))));
        this.stackPane.minHeightProperty().bind((ObservableValue)this.stackPane.minWidthProperty());
        this.title = new Label();
        this.title.textProperty().bind((ObservableValue)popOver.titleProperty());
        this.title.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.title.setAlignment(Pos.CENTER);
        this.title.getStyleClass().add((Object)"text");
        (this.closeIcon = new Label()).setGraphic(this.createCloseIcon());
        this.closeIcon.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.closeIcon.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.closeIcon.visibleProperty().bind((ObservableValue)popOver.closeButtonEnabledProperty().and((ObservableBooleanValue)popOver.detachedProperty().or((ObservableBooleanValue)popOver.headerAlwaysVisibleProperty())));
        this.closeIcon.getStyleClass().add((Object)"icon");
        this.closeIcon.setAlignment(Pos.CENTER_LEFT);
        this.closeIcon.getGraphic().setOnMouseClicked(evt -> popOver.hide());
        this.titlePane = new StackPane();
        this.titlePane.getChildren().add((Object)this.title);
        this.titlePane.getChildren().add((Object)this.closeIcon);
        this.titlePane.getStyleClass().add((Object)"title");
        (this.content = new BorderPane()).setCenter(popOver.getContentNode());
        this.content.getStyleClass().add((Object)"content");
        if (popOver.isDetached() || popOver.isHeaderAlwaysVisible()) {
            this.content.setTop((Node)this.titlePane);
        }
        if (popOver.isDetached()) {
            popOver.getStyleClass().add((Object)"detached");
            this.content.getStyleClass().add((Object)"detached");
        }
        popOver.headerAlwaysVisibleProperty().addListener((o, oV, isVisible) -> {
            if (isVisible) {
                this.content.setTop((Node)this.titlePane);
            }
            else if (!popOver.isDetached()) {
                this.content.setTop((Node)null);
            }
        });
        final InvalidationListener updatePathListener = observable -> this.updatePath();
        this.getPopupWindow().xProperty().addListener(updatePathListener);
        this.getPopupWindow().yProperty().addListener(updatePathListener);
        popOver.arrowLocationProperty().addListener(updatePathListener);
        popOver.contentNodeProperty().addListener((value, oldContent, newContent) -> this.content.setCenter(newContent));
        popOver.detachedProperty().addListener((value, oldDetached, newDetached) -> {
            if (newDetached) {
                popOver.getStyleClass().add((Object)"detached");
                this.content.getStyleClass().add((Object)"detached");
                this.content.setTop((Node)this.titlePane);
                switch (this.getSkinnable().getArrowLocation()) {
                    case LEFT_TOP:
                    case LEFT_CENTER:
                    case LEFT_BOTTOM: {
                        popOver.setAnchorX(popOver.getAnchorX() + popOver.getArrowSize());
                        break;
                    }
                    case TOP_LEFT:
                    case TOP_CENTER:
                    case TOP_RIGHT: {
                        popOver.setAnchorY(popOver.getAnchorY() + popOver.getArrowSize());
                        break;
                    }
                }
            }
            else {
                popOver.getStyleClass().remove((Object)"detached");
                this.content.getStyleClass().remove((Object)"detached");
                if (!popOver.isHeaderAlwaysVisible()) {
                    this.content.setTop((Node)null);
                }
            }
            popOver.sizeToScene();
            this.updatePath();
        });
        this.path = new Path();
        this.path.getStyleClass().add((Object)"border");
        this.path.setManaged(false);
        (this.clip = new Path()).setFill((Paint)Color.YELLOW);
        this.createPathElements();
        this.updatePath();
        final EventHandler<MouseEvent> mousePressedHandler = (EventHandler<MouseEvent>)(evt -> {
            if (popOver.isDetachable() || popOver.isDetached()) {
                this.tornOff = false;
                this.xOffset = evt.getScreenX();
                this.yOffset = evt.getScreenY();
                this.dragStartLocation = new Point2D(this.xOffset, this.yOffset);
            }
        });
        final EventHandler<MouseEvent> mouseReleasedHandler = (EventHandler<MouseEvent>)(evt -> {
            if (this.tornOff && !this.getSkinnable().isDetached()) {
                this.tornOff = false;
                this.getSkinnable().detach();
            }
        });
        final EventHandler<MouseEvent> mouseDragHandler = (EventHandler<MouseEvent>)(evt -> {
            if (popOver.isDetachable() || popOver.isDetached()) {
                final double deltaX = evt.getScreenX() - this.xOffset;
                final double deltaY = evt.getScreenY() - this.yOffset;
                final Window window = this.getSkinnable().getScene().getWindow();
                window.setX(window.getX() + deltaX);
                window.setY(window.getY() + deltaY);
                this.xOffset = evt.getScreenX();
                this.yOffset = evt.getScreenY();
                if (this.dragStartLocation.distance(this.xOffset, this.yOffset) > 20.0) {
                    this.tornOff = true;
                    this.updatePath();
                }
                else if (this.tornOff) {
                    this.tornOff = false;
                    this.updatePath();
                }
            }
        });
        this.stackPane.setOnMousePressed((EventHandler)mousePressedHandler);
        this.stackPane.setOnMouseDragged((EventHandler)mouseDragHandler);
        this.stackPane.setOnMouseReleased((EventHandler)mouseReleasedHandler);
        this.stackPane.getChildren().add((Object)this.path);
        this.stackPane.getChildren().add((Object)this.content);
        this.content.setClip((Node)this.clip);
    }
    
    public Node getNode() {
        return (Node)this.stackPane;
    }
    
    public PopOver getSkinnable() {
        return this.popOver;
    }
    
    public void dispose() {
    }
    
    private Node createCloseIcon() {
        final Group group = new Group();
        group.getStyleClass().add((Object)"graphics");
        final Circle circle = new Circle();
        circle.getStyleClass().add((Object)"circle");
        circle.setRadius(6.0);
        circle.setCenterX(6.0);
        circle.setCenterY(6.0);
        group.getChildren().add((Object)circle);
        final Line line1 = new Line();
        line1.getStyleClass().add((Object)"line");
        line1.setStartX(4.0);
        line1.setStartY(4.0);
        line1.setEndX(8.0);
        line1.setEndY(8.0);
        group.getChildren().add((Object)line1);
        final Line line2 = new Line();
        line2.getStyleClass().add((Object)"line");
        line2.setStartX(8.0);
        line2.setStartY(4.0);
        line2.setEndX(4.0);
        line2.setEndY(8.0);
        group.getChildren().add((Object)line2);
        return (Node)group;
    }
    
    private void createPathElements() {
        final DoubleProperty centerYProperty = (DoubleProperty)new SimpleDoubleProperty();
        final DoubleProperty centerXProperty = (DoubleProperty)new SimpleDoubleProperty();
        final DoubleProperty leftEdgeProperty = (DoubleProperty)new SimpleDoubleProperty();
        final DoubleProperty leftEdgePlusRadiusProperty = (DoubleProperty)new SimpleDoubleProperty();
        final DoubleProperty topEdgeProperty = (DoubleProperty)new SimpleDoubleProperty();
        final DoubleProperty topEdgePlusRadiusProperty = (DoubleProperty)new SimpleDoubleProperty();
        final DoubleProperty rightEdgeProperty = (DoubleProperty)new SimpleDoubleProperty();
        final DoubleProperty rightEdgeMinusRadiusProperty = (DoubleProperty)new SimpleDoubleProperty();
        final DoubleProperty bottomEdgeProperty = (DoubleProperty)new SimpleDoubleProperty();
        final DoubleProperty bottomEdgeMinusRadiusProperty = (DoubleProperty)new SimpleDoubleProperty();
        final DoubleProperty cornerProperty = this.getSkinnable().cornerRadiusProperty();
        final DoubleProperty arrowSizeProperty = this.getSkinnable().arrowSizeProperty();
        final DoubleProperty arrowIndentProperty = this.getSkinnable().arrowIndentProperty();
        centerYProperty.bind((ObservableValue)Bindings.divide((ObservableNumberValue)this.stackPane.heightProperty(), 2));
        centerXProperty.bind((ObservableValue)Bindings.divide((ObservableNumberValue)this.stackPane.widthProperty(), 2));
        leftEdgePlusRadiusProperty.bind((ObservableValue)Bindings.add((ObservableNumberValue)leftEdgeProperty, (ObservableNumberValue)this.getSkinnable().cornerRadiusProperty()));
        topEdgePlusRadiusProperty.bind((ObservableValue)Bindings.add((ObservableNumberValue)topEdgeProperty, (ObservableNumberValue)this.getSkinnable().cornerRadiusProperty()));
        rightEdgeProperty.bind((ObservableValue)this.stackPane.widthProperty());
        rightEdgeMinusRadiusProperty.bind((ObservableValue)Bindings.subtract((ObservableNumberValue)rightEdgeProperty, (ObservableNumberValue)this.getSkinnable().cornerRadiusProperty()));
        bottomEdgeProperty.bind((ObservableValue)this.stackPane.heightProperty());
        bottomEdgeMinusRadiusProperty.bind((ObservableValue)Bindings.subtract((ObservableNumberValue)bottomEdgeProperty, (ObservableNumberValue)this.getSkinnable().cornerRadiusProperty()));
        this.moveTo = new MoveTo();
        this.moveTo.xProperty().bind((ObservableValue)leftEdgePlusRadiusProperty);
        this.moveTo.yProperty().bind((ObservableValue)topEdgeProperty);
        this.lineBTop = new HLineTo();
        this.lineBTop.xProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)leftEdgePlusRadiusProperty, (ObservableNumberValue)arrowIndentProperty));
        this.lineCTop = new LineTo();
        this.lineCTop.xProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)this.lineBTop.xProperty(), (ObservableNumberValue)arrowSizeProperty));
        this.lineCTop.yProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)topEdgeProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineDTop = new LineTo();
        this.lineDTop.xProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)this.lineCTop.xProperty(), (ObservableNumberValue)arrowSizeProperty));
        this.lineDTop.yProperty().bind((ObservableValue)topEdgeProperty);
        this.lineETop = new HLineTo();
        this.lineETop.xProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)centerXProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineFTop = new LineTo();
        this.lineFTop.xProperty().bind((ObservableValue)centerXProperty);
        this.lineFTop.yProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)topEdgeProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineGTop = new LineTo();
        this.lineGTop.xProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)centerXProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineGTop.yProperty().bind((ObservableValue)topEdgeProperty);
        this.lineHTop = new HLineTo();
        this.lineHTop.xProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)Bindings.subtract((ObservableNumberValue)rightEdgeMinusRadiusProperty, (ObservableNumberValue)arrowIndentProperty), (ObservableNumberValue)Bindings.multiply((ObservableNumberValue)arrowSizeProperty, 2)));
        this.lineITop = new LineTo();
        this.lineITop.xProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)Bindings.subtract((ObservableNumberValue)rightEdgeMinusRadiusProperty, (ObservableNumberValue)arrowIndentProperty), (ObservableNumberValue)arrowSizeProperty));
        this.lineITop.yProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)topEdgeProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineJTop = new LineTo();
        this.lineJTop.xProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)rightEdgeMinusRadiusProperty, (ObservableNumberValue)arrowIndentProperty));
        this.lineJTop.yProperty().bind((ObservableValue)topEdgeProperty);
        this.lineKTop = new HLineTo();
        this.lineKTop.xProperty().bind((ObservableValue)rightEdgeMinusRadiusProperty);
        this.rightCurveTo = new QuadCurveTo();
        this.rightCurveTo.xProperty().bind((ObservableValue)rightEdgeProperty);
        this.rightCurveTo.yProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)topEdgeProperty, (ObservableNumberValue)cornerProperty));
        this.rightCurveTo.controlXProperty().bind((ObservableValue)rightEdgeProperty);
        this.rightCurveTo.controlYProperty().bind((ObservableValue)topEdgeProperty);
        this.lineBRight = new VLineTo();
        this.lineBRight.yProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)topEdgePlusRadiusProperty, (ObservableNumberValue)arrowIndentProperty));
        this.lineCRight = new LineTo();
        this.lineCRight.xProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)rightEdgeProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineCRight.yProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)this.lineBRight.yProperty(), (ObservableNumberValue)arrowSizeProperty));
        this.lineDRight = new LineTo();
        this.lineDRight.xProperty().bind((ObservableValue)rightEdgeProperty);
        this.lineDRight.yProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)this.lineCRight.yProperty(), (ObservableNumberValue)arrowSizeProperty));
        this.lineERight = new VLineTo();
        this.lineERight.yProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)centerYProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineFRight = new LineTo();
        this.lineFRight.xProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)rightEdgeProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineFRight.yProperty().bind((ObservableValue)centerYProperty);
        this.lineGRight = new LineTo();
        this.lineGRight.xProperty().bind((ObservableValue)rightEdgeProperty);
        this.lineGRight.yProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)centerYProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineHRight = new VLineTo();
        this.lineHRight.yProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)Bindings.subtract((ObservableNumberValue)bottomEdgeMinusRadiusProperty, (ObservableNumberValue)arrowIndentProperty), (ObservableNumberValue)Bindings.multiply((ObservableNumberValue)arrowSizeProperty, 2)));
        this.lineIRight = new LineTo();
        this.lineIRight.xProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)rightEdgeProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineIRight.yProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)Bindings.subtract((ObservableNumberValue)bottomEdgeMinusRadiusProperty, (ObservableNumberValue)arrowIndentProperty), (ObservableNumberValue)arrowSizeProperty));
        this.lineJRight = new LineTo();
        this.lineJRight.xProperty().bind((ObservableValue)rightEdgeProperty);
        this.lineJRight.yProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)bottomEdgeMinusRadiusProperty, (ObservableNumberValue)arrowIndentProperty));
        this.lineKRight = new VLineTo();
        this.lineKRight.yProperty().bind((ObservableValue)bottomEdgeMinusRadiusProperty);
        this.bottomCurveTo = new QuadCurveTo();
        this.bottomCurveTo.xProperty().bind((ObservableValue)rightEdgeMinusRadiusProperty);
        this.bottomCurveTo.yProperty().bind((ObservableValue)bottomEdgeProperty);
        this.bottomCurveTo.controlXProperty().bind((ObservableValue)rightEdgeProperty);
        this.bottomCurveTo.controlYProperty().bind((ObservableValue)bottomEdgeProperty);
        this.lineBBottom = new HLineTo();
        this.lineBBottom.xProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)rightEdgeMinusRadiusProperty, (ObservableNumberValue)arrowIndentProperty));
        this.lineCBottom = new LineTo();
        this.lineCBottom.xProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)this.lineBBottom.xProperty(), (ObservableNumberValue)arrowSizeProperty));
        this.lineCBottom.yProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)bottomEdgeProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineDBottom = new LineTo();
        this.lineDBottom.xProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)this.lineCBottom.xProperty(), (ObservableNumberValue)arrowSizeProperty));
        this.lineDBottom.yProperty().bind((ObservableValue)bottomEdgeProperty);
        this.lineEBottom = new HLineTo();
        this.lineEBottom.xProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)centerXProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineFBottom = new LineTo();
        this.lineFBottom.xProperty().bind((ObservableValue)centerXProperty);
        this.lineFBottom.yProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)bottomEdgeProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineGBottom = new LineTo();
        this.lineGBottom.xProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)centerXProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineGBottom.yProperty().bind((ObservableValue)bottomEdgeProperty);
        this.lineHBottom = new HLineTo();
        this.lineHBottom.xProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)Bindings.add((ObservableNumberValue)leftEdgePlusRadiusProperty, (ObservableNumberValue)arrowIndentProperty), (ObservableNumberValue)Bindings.multiply((ObservableNumberValue)arrowSizeProperty, 2)));
        this.lineIBottom = new LineTo();
        this.lineIBottom.xProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)Bindings.add((ObservableNumberValue)leftEdgePlusRadiusProperty, (ObservableNumberValue)arrowIndentProperty), (ObservableNumberValue)arrowSizeProperty));
        this.lineIBottom.yProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)bottomEdgeProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineJBottom = new LineTo();
        this.lineJBottom.xProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)leftEdgePlusRadiusProperty, (ObservableNumberValue)arrowIndentProperty));
        this.lineJBottom.yProperty().bind((ObservableValue)bottomEdgeProperty);
        this.lineKBottom = new HLineTo();
        this.lineKBottom.xProperty().bind((ObservableValue)leftEdgePlusRadiusProperty);
        this.leftCurveTo = new QuadCurveTo();
        this.leftCurveTo.xProperty().bind((ObservableValue)leftEdgeProperty);
        this.leftCurveTo.yProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)bottomEdgeProperty, (ObservableNumberValue)cornerProperty));
        this.leftCurveTo.controlXProperty().bind((ObservableValue)leftEdgeProperty);
        this.leftCurveTo.controlYProperty().bind((ObservableValue)bottomEdgeProperty);
        this.lineBLeft = new VLineTo();
        this.lineBLeft.yProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)bottomEdgeMinusRadiusProperty, (ObservableNumberValue)arrowIndentProperty));
        this.lineCLeft = new LineTo();
        this.lineCLeft.xProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)leftEdgeProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineCLeft.yProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)this.lineBLeft.yProperty(), (ObservableNumberValue)arrowSizeProperty));
        this.lineDLeft = new LineTo();
        this.lineDLeft.xProperty().bind((ObservableValue)leftEdgeProperty);
        this.lineDLeft.yProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)this.lineCLeft.yProperty(), (ObservableNumberValue)arrowSizeProperty));
        this.lineELeft = new VLineTo();
        this.lineELeft.yProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)centerYProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineFLeft = new LineTo();
        this.lineFLeft.xProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)leftEdgeProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineFLeft.yProperty().bind((ObservableValue)centerYProperty);
        this.lineGLeft = new LineTo();
        this.lineGLeft.xProperty().bind((ObservableValue)leftEdgeProperty);
        this.lineGLeft.yProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)centerYProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineHLeft = new VLineTo();
        this.lineHLeft.yProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)Bindings.add((ObservableNumberValue)topEdgePlusRadiusProperty, (ObservableNumberValue)arrowIndentProperty), (ObservableNumberValue)Bindings.multiply((ObservableNumberValue)arrowSizeProperty, 2)));
        this.lineILeft = new LineTo();
        this.lineILeft.xProperty().bind((ObservableValue)Bindings.subtract((ObservableNumberValue)leftEdgeProperty, (ObservableNumberValue)arrowSizeProperty));
        this.lineILeft.yProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)Bindings.add((ObservableNumberValue)topEdgePlusRadiusProperty, (ObservableNumberValue)arrowIndentProperty), (ObservableNumberValue)arrowSizeProperty));
        this.lineJLeft = new LineTo();
        this.lineJLeft.xProperty().bind((ObservableValue)leftEdgeProperty);
        this.lineJLeft.yProperty().bind((ObservableValue)Bindings.add((ObservableNumberValue)topEdgePlusRadiusProperty, (ObservableNumberValue)arrowIndentProperty));
        this.lineKLeft = new VLineTo();
        this.lineKLeft.yProperty().bind((ObservableValue)topEdgePlusRadiusProperty);
        this.topCurveTo = new QuadCurveTo();
        this.topCurveTo.xProperty().bind((ObservableValue)leftEdgePlusRadiusProperty);
        this.topCurveTo.yProperty().bind((ObservableValue)topEdgeProperty);
        this.topCurveTo.controlXProperty().bind((ObservableValue)leftEdgeProperty);
        this.topCurveTo.controlYProperty().bind((ObservableValue)topEdgeProperty);
    }
    
    private Window getPopupWindow() {
        return this.getSkinnable().getScene().getWindow();
    }
    
    private boolean showArrow(final PopOver.ArrowLocation location) {
        final PopOver.ArrowLocation arrowLocation = this.getSkinnable().getArrowLocation();
        return location.equals(arrowLocation) && !this.getSkinnable().isDetached() && !this.tornOff;
    }
    
    private void updatePath() {
        final List<PathElement> elements = new ArrayList<PathElement>();
        elements.add((PathElement)this.moveTo);
        if (this.showArrow(PopOver.ArrowLocation.TOP_LEFT)) {
            elements.add((PathElement)this.lineBTop);
            elements.add((PathElement)this.lineCTop);
            elements.add((PathElement)this.lineDTop);
        }
        if (this.showArrow(PopOver.ArrowLocation.TOP_CENTER)) {
            elements.add((PathElement)this.lineETop);
            elements.add((PathElement)this.lineFTop);
            elements.add((PathElement)this.lineGTop);
        }
        if (this.showArrow(PopOver.ArrowLocation.TOP_RIGHT)) {
            elements.add((PathElement)this.lineHTop);
            elements.add((PathElement)this.lineITop);
            elements.add((PathElement)this.lineJTop);
        }
        elements.add((PathElement)this.lineKTop);
        elements.add((PathElement)this.rightCurveTo);
        if (this.showArrow(PopOver.ArrowLocation.RIGHT_TOP)) {
            elements.add((PathElement)this.lineBRight);
            elements.add((PathElement)this.lineCRight);
            elements.add((PathElement)this.lineDRight);
        }
        if (this.showArrow(PopOver.ArrowLocation.RIGHT_CENTER)) {
            elements.add((PathElement)this.lineERight);
            elements.add((PathElement)this.lineFRight);
            elements.add((PathElement)this.lineGRight);
        }
        if (this.showArrow(PopOver.ArrowLocation.RIGHT_BOTTOM)) {
            elements.add((PathElement)this.lineHRight);
            elements.add((PathElement)this.lineIRight);
            elements.add((PathElement)this.lineJRight);
        }
        elements.add((PathElement)this.lineKRight);
        elements.add((PathElement)this.bottomCurveTo);
        if (this.showArrow(PopOver.ArrowLocation.BOTTOM_RIGHT)) {
            elements.add((PathElement)this.lineBBottom);
            elements.add((PathElement)this.lineCBottom);
            elements.add((PathElement)this.lineDBottom);
        }
        if (this.showArrow(PopOver.ArrowLocation.BOTTOM_CENTER)) {
            elements.add((PathElement)this.lineEBottom);
            elements.add((PathElement)this.lineFBottom);
            elements.add((PathElement)this.lineGBottom);
        }
        if (this.showArrow(PopOver.ArrowLocation.BOTTOM_LEFT)) {
            elements.add((PathElement)this.lineHBottom);
            elements.add((PathElement)this.lineIBottom);
            elements.add((PathElement)this.lineJBottom);
        }
        elements.add((PathElement)this.lineKBottom);
        elements.add((PathElement)this.leftCurveTo);
        if (this.showArrow(PopOver.ArrowLocation.LEFT_BOTTOM)) {
            elements.add((PathElement)this.lineBLeft);
            elements.add((PathElement)this.lineCLeft);
            elements.add((PathElement)this.lineDLeft);
        }
        if (this.showArrow(PopOver.ArrowLocation.LEFT_CENTER)) {
            elements.add((PathElement)this.lineELeft);
            elements.add((PathElement)this.lineFLeft);
            elements.add((PathElement)this.lineGLeft);
        }
        if (this.showArrow(PopOver.ArrowLocation.LEFT_TOP)) {
            elements.add((PathElement)this.lineHLeft);
            elements.add((PathElement)this.lineILeft);
            elements.add((PathElement)this.lineJLeft);
        }
        elements.add((PathElement)this.lineKLeft);
        elements.add((PathElement)this.topCurveTo);
        this.path.getElements().setAll((Collection)elements);
        this.clip.getElements().setAll((Collection)elements);
    }
}
