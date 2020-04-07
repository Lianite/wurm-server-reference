// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.beans.value.WritableValue;
import javafx.animation.KeyValue;
import javafx.animation.Animation;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.geometry.HPos;
import javafx.event.Event;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.Image;
import java.util.List;
import javafx.scene.control.Control;
import java.util.Collections;
import javafx.beans.property.DoubleProperty;
import javafx.animation.Timeline;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.util.Duration;
import javafx.scene.image.ImageView;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import org.controlsfx.control.InfoOverlay;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

public class InfoOverlaySkin extends BehaviorSkinBase<InfoOverlay, BehaviorBase<InfoOverlay>>
{
    private final ImageView EXPAND_IMAGE;
    private final ImageView COLLAPSE_IMAGE;
    private static final Duration TRANSITION_DURATION;
    private Node content;
    private Label infoLabel;
    private HBox infoPanel;
    private ToggleButton expandCollapseButton;
    private Timeline timeline;
    private DoubleProperty transition;
    
    public InfoOverlaySkin(final InfoOverlay control) {
        super((Control)control, new BehaviorBase((Control)control, (List)Collections.emptyList()));
        this.EXPAND_IMAGE = new ImageView(new Image(InfoOverlay.class.getResource("expand.png").toExternalForm()));
        this.COLLAPSE_IMAGE = new ImageView(new Image(InfoOverlay.class.getResource("collapse.png").toExternalForm()));
        this.transition = (DoubleProperty)new SimpleDoubleProperty((Object)this, "transition", 0.0) {
            protected void invalidated() {
                ((InfoOverlay)InfoOverlaySkin.this.getSkinnable()).requestLayout();
            }
        };
        this.content = control.getContent();
        control.hoverProperty().addListener((ChangeListener)new ChangeListener<Boolean>() {
            public void changed(final ObservableValue<? extends Boolean> o, final Boolean wasHover, final Boolean isHover) {
                if (control.isShowOnHover() && ((isHover && !InfoOverlaySkin.this.isExpanded()) || (!isHover && InfoOverlaySkin.this.isExpanded()))) {
                    InfoOverlaySkin.this.doToggle();
                }
            }
        });
        (this.infoLabel = new Label()).setWrapText(true);
        this.infoLabel.setAlignment(Pos.TOP_LEFT);
        this.infoLabel.getStyleClass().add((Object)"info");
        this.infoLabel.textProperty().bind((ObservableValue)control.textProperty());
        (this.expandCollapseButton = new ToggleButton()).setMouseTransparent(true);
        this.expandCollapseButton.visibleProperty().bind((ObservableValue)Bindings.not((ObservableBooleanValue)control.showOnHoverProperty()));
        this.expandCollapseButton.managedProperty().bind((ObservableValue)Bindings.not((ObservableBooleanValue)control.showOnHoverProperty()));
        this.updateToggleButton();
        (this.infoPanel = new HBox(new Node[] { this.infoLabel, this.expandCollapseButton })).setAlignment(Pos.TOP_LEFT);
        this.infoPanel.setFillHeight(true);
        this.infoPanel.getStyleClass().add((Object)"info-panel");
        this.infoPanel.setCursor(Cursor.HAND);
        this.infoPanel.setOnMouseClicked((EventHandler)new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent e) {
                if (!control.isShowOnHover()) {
                    InfoOverlaySkin.this.doToggle();
                }
            }
        });
        this.getChildren().addAll((Object[])new Node[] { this.content, this.infoPanel });
        this.registerChangeListener((ObservableValue)control.contentProperty(), "CONTENT");
    }
    
    protected void handleControlPropertyChanged(final String p) {
        super.handleControlPropertyChanged(p);
        if ("CONTENT".equals(p)) {
            this.getChildren().remove(0);
            this.getChildren().add(0, (Object)((InfoOverlay)this.getSkinnable()).getContent());
            ((InfoOverlay)this.getSkinnable()).requestLayout();
        }
    }
    
    private void doToggle() {
        this.expandCollapseButton.setSelected(!this.expandCollapseButton.isSelected());
        this.toggleInfoPanel();
        this.updateToggleButton();
    }
    
    private boolean isExpanded() {
        return this.expandCollapseButton.isSelected();
    }
    
    protected void layoutChildren(final double contentX, final double contentY, final double contentWidth, final double contentHeight) {
        final double contentPrefHeight = this.content.prefHeight(contentWidth);
        final double toggleButtonPrefWidth = this.expandCollapseButton.prefWidth(-1.0);
        this.expandCollapseButton.setMinWidth(toggleButtonPrefWidth);
        final Insets infoPanelPadding = this.infoPanel.getPadding();
        final double infoLabelWidth = this.snapSize(contentWidth - toggleButtonPrefWidth - infoPanelPadding.getLeft() - infoPanelPadding.getRight());
        final double prefInfoPanelHeight = (this.snapSize(this.infoLabel.prefHeight(infoLabelWidth)) + this.snapSpace(this.infoPanel.getPadding().getTop()) + this.snapSpace(this.infoPanel.getPadding().getBottom())) * this.transition.get();
        this.infoLabel.setMaxWidth(infoLabelWidth);
        this.infoLabel.setMaxHeight(prefInfoPanelHeight);
        this.layoutInArea(this.content, contentX, contentY, contentWidth, contentHeight, -1.0, HPos.CENTER, VPos.TOP);
        this.layoutInArea((Node)this.infoPanel, contentX, this.snapPosition(contentPrefHeight - prefInfoPanelHeight), contentWidth, prefInfoPanelHeight, 0.0, HPos.CENTER, VPos.BOTTOM);
    }
    
    private void updateToggleButton() {
        if (this.expandCollapseButton.isSelected()) {
            this.expandCollapseButton.getStyleClass().setAll((Object[])new String[] { "collapse-button" });
            this.expandCollapseButton.setGraphic((Node)this.COLLAPSE_IMAGE);
        }
        else {
            this.expandCollapseButton.getStyleClass().setAll((Object[])new String[] { "expand-button" });
            this.expandCollapseButton.setGraphic((Node)this.EXPAND_IMAGE);
        }
    }
    
    protected double computePrefHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        final double insets = topInset + bottomInset;
        return insets + ((this.content == null) ? 0.0 : this.content.prefHeight(width));
    }
    
    protected double computePrefWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        final double insets = leftInset + rightInset;
        return insets + ((this.content == null) ? 0.0 : this.content.prefWidth(height));
    }
    
    protected double computeMaxHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return this.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
    }
    
    protected double computeMaxWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return this.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
    }
    
    private void toggleInfoPanel() {
        if (this.content == null) {
            return;
        }
        Duration duration;
        if (this.timeline != null && this.timeline.getStatus() != Animation.Status.STOPPED) {
            duration = this.timeline.getCurrentTime();
            this.timeline.stop();
        }
        else {
            duration = InfoOverlaySkin.TRANSITION_DURATION;
        }
        (this.timeline = new Timeline()).setCycleCount(1);
        KeyFrame k1;
        KeyFrame k2;
        if (this.isExpanded()) {
            k1 = new KeyFrame(Duration.ZERO, new KeyValue[] { new KeyValue((WritableValue)this.transition, (Object)0) });
            k2 = new KeyFrame(duration, new KeyValue[] { new KeyValue((WritableValue)this.transition, (Object)1, Interpolator.LINEAR) });
        }
        else {
            k1 = new KeyFrame(Duration.ZERO, new KeyValue[] { new KeyValue((WritableValue)this.transition, (Object)1) });
            k2 = new KeyFrame(duration, new KeyValue[] { new KeyValue((WritableValue)this.transition, (Object)0, Interpolator.LINEAR) });
        }
        this.timeline.getKeyFrames().setAll((Object[])new KeyFrame[] { k1, k2 });
        this.timeline.play();
    }
    
    static {
        TRANSITION_DURATION = new Duration(350.0);
    }
}
