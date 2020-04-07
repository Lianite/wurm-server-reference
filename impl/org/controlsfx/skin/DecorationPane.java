// 
// Decompiled by Procyon v0.5.30
// 

package impl.org.controlsfx.skin;

import java.util.Iterator;
import javafx.geometry.Pos;
import java.util.ArrayList;
import org.controlsfx.control.decoration.Decoration;
import javafx.scene.layout.Background;
import org.controlsfx.control.decoration.Decorator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import java.util.WeakHashMap;
import javafx.beans.value.ChangeListener;
import java.util.List;
import javafx.scene.Node;
import java.util.Map;
import javafx.scene.layout.StackPane;

public class DecorationPane extends StackPane
{
    private final Map<Node, List<Node>> nodeDecorationMap;
    ChangeListener<Boolean> visibilityListener;
    
    public DecorationPane() {
        this.nodeDecorationMap = new WeakHashMap<Node, List<Node>>();
        this.visibilityListener = (ChangeListener<Boolean>)new ChangeListener<Boolean>() {
            public void changed(final ObservableValue<? extends Boolean> o, final Boolean wasVisible, final Boolean isVisible) {
                final BooleanProperty p = (BooleanProperty)o;
                final Node n = (Node)p.getBean();
                DecorationPane.this.removeAllDecorationsOnNode(n, (List)Decorator.getDecorations(n));
                Decorator.removeAllDecorations(n);
            }
        };
        this.setBackground((Background)null);
    }
    
    public void setRoot(final Node root) {
        this.getChildren().setAll((Object[])new Node[] { root });
    }
    
    public void updateDecorationsOnNode(final Node targetNode, final List<Decoration> added, final List<Decoration> removed) {
        this.removeAllDecorationsOnNode(targetNode, removed);
        this.addAllDecorationsOnNode(targetNode, added);
    }
    
    private void showDecoration(final Node targetNode, final Decoration decoration) {
        final Node decorationNode = decoration.applyDecoration(targetNode);
        if (decorationNode != null) {
            List<Node> decorationNodes = this.nodeDecorationMap.get(targetNode);
            if (decorationNodes == null) {
                decorationNodes = new ArrayList<Node>();
                this.nodeDecorationMap.put(targetNode, decorationNodes);
            }
            decorationNodes.add(decorationNode);
            if (!this.getChildren().contains((Object)decorationNode)) {
                this.getChildren().add((Object)decorationNode);
                StackPane.setAlignment(decorationNode, Pos.TOP_LEFT);
            }
        }
        targetNode.visibleProperty().addListener((ChangeListener)this.visibilityListener);
    }
    
    private void removeAllDecorationsOnNode(final Node targetNode, final List<Decoration> decorations) {
        if (decorations == null || targetNode == null) {
            return;
        }
        final List<Node> decorationNodes = this.nodeDecorationMap.remove(targetNode);
        if (decorationNodes != null) {
            for (final Node decorationNode : decorationNodes) {
                final boolean success = this.getChildren().remove((Object)decorationNode);
                if (!success) {
                    throw new IllegalStateException("Could not remove decoration " + decorationNode + " from decoration pane children list: " + this.getChildren());
                }
            }
        }
        for (final Decoration decoration : decorations) {
            decoration.removeDecoration(targetNode);
        }
    }
    
    private void addAllDecorationsOnNode(final Node targetNode, final List<Decoration> decorations) {
        if (decorations == null) {
            return;
        }
        for (final Decoration decoration : decorations) {
            this.showDecoration(targetNode, decoration);
        }
    }
}
