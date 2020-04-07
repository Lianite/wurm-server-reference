// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.scene.Parent;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import javafx.beans.value.ObservableValue;
import org.controlsfx.control.action.Action;
import javafx.collections.ObservableList;
import java.util.List;
import javafx.scene.control.Control;
import java.util.Collections;
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import org.controlsfx.control.NotificationPane;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

public class NotificationPaneSkin extends BehaviorSkinBase<NotificationPane, BehaviorBase<NotificationPane>>
{
    private NotificationBar notificationBar;
    private Node content;
    private Rectangle clip;
    
    public NotificationPaneSkin(final NotificationPane control) {
        super((Control)control, new BehaviorBase((Control)control, (List)Collections.emptyList()));
        this.clip = new Rectangle();
        this.notificationBar = new NotificationBar() {
            @Override
            public void requestContainerLayout() {
                control.requestLayout();
            }
            
            @Override
            public String getText() {
                return control.getText();
            }
            
            @Override
            public Node getGraphic() {
                return control.getGraphic();
            }
            
            @Override
            public ObservableList<Action> getActions() {
                return control.getActions();
            }
            
            @Override
            public boolean isShowing() {
                return control.isShowing();
            }
            
            @Override
            public boolean isShowFromTop() {
                return control.isShowFromTop();
            }
            
            @Override
            public void hide() {
                control.hide();
            }
            
            @Override
            public boolean isCloseButtonVisible() {
                return control.isCloseButtonVisible();
            }
            
            @Override
            public double getContainerHeight() {
                return control.getHeight();
            }
            
            @Override
            public void relocateInParent(final double x, final double y) {
                NotificationPaneSkin.this.notificationBar.relocate(x, y);
            }
        };
        control.setClip((Node)this.clip);
        this.updateContent();
        this.registerChangeListener((ObservableValue)control.heightProperty(), "HEIGHT");
        this.registerChangeListener((ObservableValue)control.contentProperty(), "CONTENT");
        this.registerChangeListener((ObservableValue)control.textProperty(), "TEXT");
        this.registerChangeListener((ObservableValue)control.graphicProperty(), "GRAPHIC");
        this.registerChangeListener((ObservableValue)control.showingProperty(), "SHOWING");
        this.registerChangeListener((ObservableValue)control.showFromTopProperty(), "SHOW_FROM_TOP");
        this.registerChangeListener((ObservableValue)control.closeButtonVisibleProperty(), "CLOSE_BUTTON_VISIBLE");
        final ParentTraversalEngine engine = new ParentTraversalEngine((Parent)this.getSkinnable());
        ((NotificationPane)this.getSkinnable()).setImpl_traversalEngine(engine);
        engine.setOverriddenFocusTraversability(false);
    }
    
    protected void handleControlPropertyChanged(final String p) {
        super.handleControlPropertyChanged(p);
        if ("CONTENT".equals(p)) {
            this.updateContent();
        }
        else if ("TEXT".equals(p)) {
            this.notificationBar.label.setText(((NotificationPane)this.getSkinnable()).getText());
        }
        else if ("GRAPHIC".equals(p)) {
            this.notificationBar.label.setGraphic(((NotificationPane)this.getSkinnable()).getGraphic());
        }
        else if ("SHOWING".equals(p)) {
            if (((NotificationPane)this.getSkinnable()).isShowing()) {
                this.notificationBar.doShow();
            }
            else {
                this.notificationBar.doHide();
            }
        }
        else if ("SHOW_FROM_TOP".equals(p)) {
            if (((NotificationPane)this.getSkinnable()).isShowing()) {
                ((NotificationPane)this.getSkinnable()).requestLayout();
            }
        }
        else if ("CLOSE_BUTTON_VISIBLE".equals(p)) {
            this.notificationBar.updatePane();
        }
        else if ("HEIGHT".equals(p) && ((NotificationPane)this.getSkinnable()).isShowing() && !((NotificationPane)this.getSkinnable()).isShowFromTop()) {
            this.notificationBar.requestLayout();
        }
    }
    
    private void updateContent() {
        if (this.content != null) {
            this.getChildren().remove((Object)this.content);
        }
        this.content = ((NotificationPane)this.getSkinnable()).getContent();
        if (this.content == null) {
            this.getChildren().setAll((Object[])new Node[] { this.notificationBar });
        }
        else {
            this.getChildren().setAll((Object[])new Node[] { this.content, this.notificationBar });
        }
    }
    
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        final double notificationBarHeight = this.notificationBar.prefHeight(w);
        this.notificationBar.resize(w, notificationBarHeight);
        if (this.content != null) {
            this.content.resizeRelocate(x, y, w, h);
        }
        this.clip.setX(x);
        this.clip.setY(y);
        this.clip.setWidth(w);
        this.clip.setHeight(h);
    }
    
    protected double computeMinWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return (this.content == null) ? 0.0 : this.content.minWidth(height);
    }
    
    protected double computeMinHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return (this.content == null) ? 0.0 : this.content.minHeight(width);
    }
    
    protected double computePrefWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return (this.content == null) ? 0.0 : this.content.prefWidth(height);
    }
    
    protected double computePrefHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return (this.content == null) ? 0.0 : this.content.prefHeight(width);
    }
    
    protected double computeMaxWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return (this.content == null) ? 0.0 : this.content.maxWidth(height);
    }
    
    protected double computeMaxHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return (this.content == null) ? 0.0 : this.content.maxHeight(width);
    }
}
