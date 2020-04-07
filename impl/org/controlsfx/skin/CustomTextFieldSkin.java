// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import com.sun.javafx.scene.text.HitInfo;
import javafx.geometry.Pos;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import com.sun.javafx.scene.control.behavior.TextFieldBehavior;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.css.PseudoClass;
import com.sun.javafx.scene.control.skin.TextFieldSkin;

public abstract class CustomTextFieldSkin extends TextFieldSkin
{
    private static final PseudoClass HAS_NO_SIDE_NODE;
    private static final PseudoClass HAS_LEFT_NODE;
    private static final PseudoClass HAS_RIGHT_NODE;
    private Node left;
    private StackPane leftPane;
    private Node right;
    private StackPane rightPane;
    private final TextField control;
    
    public CustomTextFieldSkin(final TextField control) {
        super(control, new TextFieldBehavior(control));
        this.control = control;
        this.updateChildren();
        this.registerChangeListener((ObservableValue)this.leftProperty(), "LEFT_NODE");
        this.registerChangeListener((ObservableValue)this.rightProperty(), "RIGHT_NODE");
        this.registerChangeListener((ObservableValue)control.focusedProperty(), "FOCUSED");
    }
    
    public abstract ObjectProperty<Node> leftProperty();
    
    public abstract ObjectProperty<Node> rightProperty();
    
    protected void handleControlPropertyChanged(final String p) {
        super.handleControlPropertyChanged(p);
        if (p == "LEFT_NODE" || p == "RIGHT_NODE") {
            this.updateChildren();
        }
    }
    
    private void updateChildren() {
        final Node newLeft = (Node)this.leftProperty().get();
        if (newLeft != null) {
            this.getChildren().remove((Object)this.leftPane);
            (this.leftPane = new StackPane(new Node[] { newLeft })).setAlignment(Pos.CENTER_LEFT);
            this.leftPane.getStyleClass().add((Object)"left-pane");
            this.getChildren().add((Object)this.leftPane);
            this.left = newLeft;
        }
        final Node newRight = (Node)this.rightProperty().get();
        if (newRight != null) {
            this.getChildren().remove((Object)this.rightPane);
            (this.rightPane = new StackPane(new Node[] { newRight })).setAlignment(Pos.CENTER_RIGHT);
            this.rightPane.getStyleClass().add((Object)"right-pane");
            this.getChildren().add((Object)this.rightPane);
            this.right = newRight;
        }
        this.control.pseudoClassStateChanged(CustomTextFieldSkin.HAS_LEFT_NODE, this.left != null);
        this.control.pseudoClassStateChanged(CustomTextFieldSkin.HAS_RIGHT_NODE, this.right != null);
        this.control.pseudoClassStateChanged(CustomTextFieldSkin.HAS_NO_SIDE_NODE, this.left == null && this.right == null);
    }
    
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        final double fullHeight = h + this.snappedTopInset() + this.snappedBottomInset();
        final double leftWidth = (this.leftPane == null) ? 0.0 : this.snapSize(this.leftPane.prefWidth(fullHeight));
        final double rightWidth = (this.rightPane == null) ? 0.0 : this.snapSize(this.rightPane.prefWidth(fullHeight));
        final double textFieldStartX = this.snapPosition(x) + this.snapSize(leftWidth);
        final double textFieldWidth = w - this.snapSize(leftWidth) - this.snapSize(rightWidth);
        super.layoutChildren(textFieldStartX, 0.0, textFieldWidth, fullHeight);
        if (this.leftPane != null) {
            final double leftStartX = 0.0;
            this.leftPane.resizeRelocate(0.0, 0.0, leftWidth, fullHeight);
        }
        if (this.rightPane != null) {
            final double rightStartX = (this.rightPane == null) ? 0.0 : (w - rightWidth + this.snappedLeftInset());
            this.rightPane.resizeRelocate(rightStartX, 0.0, rightWidth, fullHeight);
        }
    }
    
    public HitInfo getIndex(final double x, final double y) {
        final double leftWidth = (this.leftPane == null) ? 0.0 : this.snapSize(this.leftPane.prefWidth(((TextField)this.getSkinnable()).getHeight()));
        return super.getIndex(x - leftWidth, y);
    }
    
    protected double computePrefWidth(final double h, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        final double pw = super.computePrefWidth(h, topInset, rightInset, bottomInset, leftInset);
        final double leftWidth = (this.leftPane == null) ? 0.0 : this.snapSize(this.leftPane.prefWidth(h));
        final double rightWidth = (this.rightPane == null) ? 0.0 : this.snapSize(this.rightPane.prefWidth(h));
        return pw + leftWidth + rightWidth;
    }
    
    protected double computePrefHeight(final double w, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        final double ph = super.computePrefHeight(w, topInset, rightInset, bottomInset, leftInset);
        final double leftHeight = (this.leftPane == null) ? 0.0 : this.snapSize(this.leftPane.prefHeight(-1.0));
        final double rightHeight = (this.rightPane == null) ? 0.0 : this.snapSize(this.rightPane.prefHeight(-1.0));
        return Math.max(ph, Math.max(leftHeight, rightHeight));
    }
    
    static {
        HAS_NO_SIDE_NODE = PseudoClass.getPseudoClass("no-side-nodes");
        HAS_LEFT_NODE = PseudoClass.getPseudoClass("left-node-visible");
        HAS_RIGHT_NODE = PseudoClass.getPseudoClass("right-node-visible");
    }
}
