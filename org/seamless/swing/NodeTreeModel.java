// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeModel;

public class NodeTreeModel implements TreeModel
{
    private Node rootNode;
    private Node selectedNode;
    
    public NodeTreeModel(final Node rootNode) {
        this.rootNode = rootNode;
    }
    
    public Object getRoot() {
        return this.rootNode;
    }
    
    public boolean isLeaf(final Object object) {
        final Node node = (Node)object;
        final boolean isLeaf = node.getChildren().size() == 0;
        return isLeaf;
    }
    
    public int getChildCount(final Object parent) {
        final Node node = (Node)parent;
        return node.getChildren().size();
    }
    
    public Object getChild(final Object parent, final int i) {
        final Node node = (Node)parent;
        final Object child = node.getChildren().get(i);
        return child;
    }
    
    public int getIndexOfChild(final Object parent, final Object child) {
        if (parent == null || child == null) {
            return -1;
        }
        final Node node = (Node)parent;
        final int index = node.getChildren().indexOf(child);
        return index;
    }
    
    public void valueForPathChanged(final TreePath path, final Object newvalue) {
    }
    
    public void addTreeModelListener(final TreeModelListener l) {
    }
    
    public void removeTreeModelListener(final TreeModelListener l) {
    }
}
