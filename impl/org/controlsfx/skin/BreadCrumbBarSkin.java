// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.beans.Observable;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.event.Event;
import java.util.ArrayList;
import javafx.scene.control.Button;
import javafx.util.Callback;
import javafx.scene.Parent;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import com.sun.javafx.scene.traversal.TraversalContext;
import com.sun.javafx.scene.traversal.Direction;
import javafx.scene.Node;
import com.sun.javafx.scene.traversal.Algorithm;
import java.util.List;
import javafx.scene.control.Control;
import java.util.Collections;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.beans.value.ChangeListener;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import org.controlsfx.control.BreadCrumbBar;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

public class BreadCrumbBarSkin<T> extends BehaviorSkinBase<BreadCrumbBar<T>, BehaviorBase<BreadCrumbBar<T>>>
{
    private static final String STYLE_CLASS_FIRST = "first";
    private final ChangeListener<TreeItem<T>> selectedPathChangeListener;
    private final EventHandler<TreeItem.TreeModificationEvent<Object>> treeChildrenModifiedHandler;
    
    public BreadCrumbBarSkin(final BreadCrumbBar<T> control) {
        super((Control)control, new BehaviorBase((Control)control, (List)Collections.emptyList()));
        this.selectedPathChangeListener = (ChangeListener<TreeItem<T>>)((obs, oldItem, newItem) -> this.updateSelectedPath((TreeItem<T>)newItem, (TreeItem<T>)oldItem));
        this.treeChildrenModifiedHandler = (EventHandler<TreeItem.TreeModificationEvent<Object>>)(args -> this.updateBreadCrumbs());
        control.selectedCrumbProperty().addListener((ChangeListener)this.selectedPathChangeListener);
        this.updateSelectedPath((TreeItem<T>)((BreadCrumbBar)this.getSkinnable()).selectedCrumbProperty().get(), null);
        this.fixFocusTraversal();
    }
    
    private void fixFocusTraversal() {
        final ParentTraversalEngine engine = new ParentTraversalEngine((Parent)this.getSkinnable(), (Algorithm)new Algorithm() {
            public Node select(final Node owner, final Direction dir, final TraversalContext context) {
                Node node = null;
                final int idx = BreadCrumbBarSkin.this.getChildren().indexOf((Object)owner);
                switch (dir) {
                    case NEXT:
                    case NEXT_IN_LINE:
                    case RIGHT: {
                        if (idx < BreadCrumbBarSkin.this.getChildren().size() - 1) {
                            node = (Node)BreadCrumbBarSkin.this.getChildren().get(idx + 1);
                            break;
                        }
                        break;
                    }
                    case PREVIOUS:
                    case LEFT: {
                        if (idx > 0) {
                            node = (Node)BreadCrumbBarSkin.this.getChildren().get(idx - 1);
                            break;
                        }
                        break;
                    }
                }
                return node;
            }
            
            public Node selectFirst(final TraversalContext context) {
                Node first = null;
                if (!BreadCrumbBarSkin.this.getChildren().isEmpty()) {
                    first = (Node)BreadCrumbBarSkin.this.getChildren().get(0);
                }
                return first;
            }
            
            public Node selectLast(final TraversalContext context) {
                Node last = null;
                if (!BreadCrumbBarSkin.this.getChildren().isEmpty()) {
                    last = (Node)BreadCrumbBarSkin.this.getChildren().get(BreadCrumbBarSkin.this.getChildren().size() - 1);
                }
                return last;
            }
        });
        engine.setOverriddenFocusTraversability(false);
        ((BreadCrumbBar)this.getSkinnable()).setImpl_traversalEngine(engine);
    }
    
    private void updateSelectedPath(final TreeItem<T> newTarget, final TreeItem<T> oldTarget) {
        if (oldTarget != null) {
            oldTarget.removeEventHandler(TreeItem.childrenModificationEvent(), (EventHandler)this.treeChildrenModifiedHandler);
        }
        if (newTarget != null) {
            newTarget.addEventHandler(TreeItem.childrenModificationEvent(), (EventHandler)this.treeChildrenModifiedHandler);
        }
        this.updateBreadCrumbs();
    }
    
    private void updateBreadCrumbs() {
        final BreadCrumbBar<T> buttonBar = (BreadCrumbBar<T>)this.getSkinnable();
        final TreeItem<T> pathTarget = buttonBar.getSelectedCrumb();
        final Callback<TreeItem<T>, Button> factory = buttonBar.getCrumbFactory();
        this.getChildren().clear();
        if (pathTarget != null) {
            final List<TreeItem<T>> crumbs = this.constructFlatPath(pathTarget);
            for (int i = 0; i < crumbs.size(); ++i) {
                final Button crumb = this.createCrumb(factory, crumbs.get(i));
                crumb.setMnemonicParsing(false);
                if (i == 0) {
                    if (!crumb.getStyleClass().contains((Object)"first")) {
                        crumb.getStyleClass().add((Object)"first");
                    }
                }
                else {
                    crumb.getStyleClass().remove((Object)"first");
                }
                this.getChildren().add((Object)crumb);
            }
        }
    }
    
    protected void layoutChildren(double x, final double y, final double w, final double h) {
        for (int i = 0; i < this.getChildren().size(); ++i) {
            final Node n = (Node)this.getChildren().get(i);
            final double nw = this.snapSize(n.prefWidth(h));
            final double nh = this.snapSize(n.prefHeight(-1.0));
            if (i > 0) {
                final double ins = (n instanceof BreadCrumbButton) ? ((BreadCrumbButton)n).getArrowWidth() : 0.0;
                x = this.snapPosition(x - ins);
            }
            n.resize(nw, nh);
            n.relocate(x, y);
            x += nw;
        }
    }
    
    private List<TreeItem<T>> constructFlatPath(final TreeItem<T> bottomMost) {
        final List<TreeItem<T>> path = new ArrayList<TreeItem<T>>();
        TreeItem<T> current = bottomMost;
        do {
            path.add(current);
            current = (TreeItem<T>)current.getParent();
        } while (current != null);
        Collections.reverse(path);
        return path;
    }
    
    private Button createCrumb(final Callback<TreeItem<T>, Button> factory, final TreeItem<T> selectedCrumb) {
        final Button crumb = (Button)factory.call((Object)selectedCrumb);
        crumb.getStyleClass().add((Object)"crumb");
        crumb.setOnAction(ae -> this.onBreadCrumbAction(selectedCrumb));
        return crumb;
    }
    
    protected void onBreadCrumbAction(final TreeItem<T> crumbModel) {
        final BreadCrumbBar<T> breadCrumbBar = (BreadCrumbBar<T>)this.getSkinnable();
        Event.fireEvent((EventTarget)breadCrumbBar, (Event)new BreadCrumbBar.BreadCrumbActionEvent<Object>(crumbModel));
        if (breadCrumbBar.isAutoNavigationEnabled()) {
            breadCrumbBar.setSelectedCrumb(crumbModel);
        }
    }
    
    public static class BreadCrumbButton extends Button
    {
        private final ObjectProperty<Boolean> first;
        private final double arrowWidth = 5.0;
        private final double arrowHeight = 20.0;
        
        public BreadCrumbButton(final String text) {
            this(text, null);
        }
        
        public BreadCrumbButton(final String text, final Node gfx) {
            super(text, gfx);
            this.first = (ObjectProperty<Boolean>)new SimpleObjectProperty((Object)this, "first");
            this.first.set((Object)false);
            this.getStyleClass().addListener((InvalidationListener)new InvalidationListener() {
                public void invalidated(final Observable arg0) {
                    BreadCrumbButton.this.updateShape();
                }
            });
            this.updateShape();
        }
        
        private void updateShape() {
            this.setShape((Shape)this.createButtonShape());
        }
        
        public double getArrowWidth() {
            return 5.0;
        }
        
        private Path createButtonShape() {
            final Path path = new Path();
            final MoveTo e1 = new MoveTo(0.0, 0.0);
            path.getElements().add((Object)e1);
            final HLineTo e2 = new HLineTo();
            e2.xProperty().bind((ObservableValue)this.widthProperty().subtract(5.0));
            path.getElements().add((Object)e2);
            final LineTo e3 = new LineTo();
            e3.xProperty().bind((ObservableValue)e2.xProperty().add(5.0));
            e3.setY(10.0);
            path.getElements().add((Object)e3);
            final LineTo e4 = new LineTo();
            e4.xProperty().bind((ObservableValue)e2.xProperty());
            e4.setY(20.0);
            path.getElements().add((Object)e4);
            final HLineTo e5 = new HLineTo(0.0);
            path.getElements().add((Object)e5);
            if (!this.getStyleClass().contains((Object)"first")) {
                final LineTo e6 = new LineTo(5.0, 10.0);
                path.getElements().add((Object)e6);
            }
            else {
                final ArcTo arcTo = new ArcTo();
                arcTo.setSweepFlag(true);
                arcTo.setX(0.0);
                arcTo.setY(0.0);
                arcTo.setRadiusX(15.0);
                arcTo.setRadiusY(15.0);
                path.getElements().add((Object)arcTo);
            }
            final ClosePath e7 = new ClosePath();
            path.getElements().add((Object)e7);
            path.setFill((Paint)Color.BLACK);
            return path;
        }
    }
}
