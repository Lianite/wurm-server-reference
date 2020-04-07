// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import javafx.css.StyleableProperty;
import javafx.css.StyleConverter;
import com.sun.javafx.css.converters.SizeConverter;
import javafx.scene.input.MouseEvent;
import javafx.css.StyleableDoubleProperty;
import javafx.geometry.Pos;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.util.Duration;
import javafx.scene.control.Control;
import javafx.css.Styleable;
import java.util.List;
import javafx.css.CssMetaData;
import javafx.beans.property.DoubleProperty;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.ToggleSwitch;
import javafx.scene.control.SkinBase;

public class ToggleSwitchSkin extends SkinBase<ToggleSwitch>
{
    private final StackPane thumb;
    private final StackPane thumbArea;
    private final Label label;
    private final StackPane labelContainer;
    private final TranslateTransition transition;
    private DoubleProperty thumbMoveAnimationTime;
    private static final CssMetaData<ToggleSwitch, Number> THUMB_MOVE_ANIMATION_TIME;
    private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
    
    public ToggleSwitchSkin(final ToggleSwitch control) {
        super((Control)control);
        this.thumbMoveAnimationTime = null;
        this.thumb = new StackPane();
        this.thumbArea = new StackPane();
        this.label = new Label();
        this.labelContainer = new StackPane();
        this.transition = new TranslateTransition(Duration.millis(this.getThumbMoveAnimationTime()), (Node)this.thumb);
        this.label.textProperty().bind((ObservableValue)control.textProperty());
        this.getChildren().addAll((Object[])new Node[] { this.labelContainer, this.thumbArea, this.thumb });
        this.labelContainer.getChildren().addAll((Object[])new Node[] { this.label });
        StackPane.setAlignment((Node)this.label, Pos.CENTER_LEFT);
        this.thumb.getStyleClass().setAll((Object[])new String[] { "thumb" });
        this.thumbArea.getStyleClass().setAll((Object[])new String[] { "thumb-area" });
        this.thumbArea.setOnMouseReleased(event -> this.mousePressedOnToggleSwitch(control));
        this.thumb.setOnMouseReleased(event -> this.mousePressedOnToggleSwitch(control));
        control.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != (boolean)oldValue) {
                this.selectedStateChanged();
            }
        });
    }
    
    private void selectedStateChanged() {
        if (this.transition != null) {
            this.transition.stop();
        }
        final double thumbAreaWidth = this.snapSize(this.thumbArea.prefWidth(-1.0));
        final double thumbWidth = this.snapSize(this.thumb.prefWidth(-1.0));
        final double distance = thumbAreaWidth - thumbWidth;
        if (!((ToggleSwitch)this.getSkinnable()).isSelected()) {
            this.thumb.setLayoutX(this.thumbArea.getLayoutX());
            this.transition.setFromX(distance);
            this.transition.setToX(0.0);
        }
        else {
            this.thumb.setTranslateX(this.thumbArea.getLayoutX());
            this.transition.setFromX(0.0);
            this.transition.setToX(distance);
        }
        this.transition.setCycleCount(1);
        this.transition.play();
    }
    
    private void mousePressedOnToggleSwitch(final ToggleSwitch toggleSwitch) {
        toggleSwitch.setSelected(!toggleSwitch.isSelected());
    }
    
    private DoubleProperty thumbMoveAnimationTimeProperty() {
        if (this.thumbMoveAnimationTime == null) {
            this.thumbMoveAnimationTime = (DoubleProperty)new StyleableDoubleProperty(200.0) {
                public Object getBean() {
                    return ToggleSwitchSkin.this;
                }
                
                public String getName() {
                    return "thumbMoveAnimationTime";
                }
                
                public CssMetaData<ToggleSwitch, Number> getCssMetaData() {
                    return ToggleSwitchSkin.THUMB_MOVE_ANIMATION_TIME;
                }
            };
        }
        return this.thumbMoveAnimationTime;
    }
    
    private double getThumbMoveAnimationTime() {
        return (this.thumbMoveAnimationTime == null) ? 200.0 : this.thumbMoveAnimationTime.get();
    }
    
    protected void layoutChildren(final double contentX, final double contentY, final double contentWidth, final double contentHeight) {
        final ToggleSwitch toggleSwitch = (ToggleSwitch)this.getSkinnable();
        final double thumbWidth = this.snapSize(this.thumb.prefWidth(-1.0));
        final double thumbHeight = this.snapSize(this.thumb.prefHeight(-1.0));
        this.thumb.resize(thumbWidth, thumbHeight);
        if (this.transition != null) {
            this.transition.stop();
        }
        this.thumb.setTranslateX(0.0);
        final double thumbAreaY = this.snapPosition(contentY);
        final double thumbAreaWidth = this.snapSize(this.thumbArea.prefWidth(-1.0));
        final double thumbAreaHeight = this.snapSize(this.thumbArea.prefHeight(-1.0));
        this.thumbArea.resize(thumbAreaWidth, thumbAreaHeight);
        this.thumbArea.setLayoutX(contentWidth - thumbAreaWidth);
        this.thumbArea.setLayoutY(thumbAreaY);
        this.labelContainer.resize(contentWidth - thumbAreaWidth, thumbAreaHeight);
        this.labelContainer.setLayoutY(thumbAreaY);
        if (!toggleSwitch.isSelected()) {
            this.thumb.setLayoutX(this.thumbArea.getLayoutX());
        }
        else {
            this.thumb.setLayoutX(this.thumbArea.getLayoutX() + thumbAreaWidth - thumbWidth);
        }
        this.thumb.setLayoutY(thumbAreaY + (thumbAreaHeight - thumbHeight) / 2.0);
    }
    
    protected double computeMinWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return leftInset + this.label.prefWidth(-1.0) + this.thumbArea.prefWidth(-1.0) + rightInset;
    }
    
    protected double computeMinHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return topInset + Math.max(this.thumb.prefHeight(-1.0), this.label.prefHeight(-1.0)) + bottomInset;
    }
    
    protected double computePrefWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return leftInset + this.label.prefWidth(-1.0) + 20.0 + this.thumbArea.prefWidth(-1.0) + rightInset;
    }
    
    protected double computePrefHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return topInset + Math.max(this.thumb.prefHeight(-1.0), this.label.prefHeight(-1.0)) + bottomInset;
    }
    
    protected double computeMaxWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return ((ToggleSwitch)this.getSkinnable()).prefWidth(height);
    }
    
    protected double computeMaxHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return ((ToggleSwitch)this.getSkinnable()).prefHeight(width);
    }
    
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return ToggleSwitchSkin.STYLEABLES;
    }
    
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }
    
    static {
        THUMB_MOVE_ANIMATION_TIME = new CssMetaData<ToggleSwitch, Number>("-thumb-move-animation-time", SizeConverter.getInstance(), 200) {
            public boolean isSettable(final ToggleSwitch toggleSwitch) {
                final ToggleSwitchSkin skin = (ToggleSwitchSkin)toggleSwitch.getSkin();
                return skin.thumbMoveAnimationTime == null || !skin.thumbMoveAnimationTime.isBound();
            }
            
            public StyleableProperty<Number> getStyleableProperty(final ToggleSwitch toggleSwitch) {
                final ToggleSwitchSkin skin = (ToggleSwitchSkin)toggleSwitch.getSkin();
                return (StyleableProperty<Number>)skin.thumbMoveAnimationTimeProperty();
            }
        };
        final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<CssMetaData<? extends Styleable, ?>>(SkinBase.getClassCssMetaData());
        styleables.add((CssMetaData<? extends Styleable, ?>)ToggleSwitchSkin.THUMB_MOVE_ANIMATION_TIME);
        STYLEABLES = Collections.unmodifiableList((List<? extends CssMetaData<? extends Styleable, ?>>)styleables);
    }
}
