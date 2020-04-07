// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.contentdirectory.ui;

import javax.swing.tree.ExpandVetoException;
import org.fourthline.cling.controlpoint.ActionCallback;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.DefaultTreeModel;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.controlpoint.ControlPoint;
import javax.swing.event.TreeWillExpandListener;

public class ContentTreeExpandListener implements TreeWillExpandListener
{
    protected final ControlPoint controlPoint;
    protected final Service service;
    protected final DefaultTreeModel treeModel;
    protected final ContentBrowseActionCallbackCreator actionCreator;
    
    public ContentTreeExpandListener(final ControlPoint controlPoint, final Service service, final DefaultTreeModel treeModel, final ContentBrowseActionCallbackCreator actionCreator) {
        this.controlPoint = controlPoint;
        this.service = service;
        this.treeModel = treeModel;
        this.actionCreator = actionCreator;
    }
    
    @Override
    public void treeWillExpand(final TreeExpansionEvent e) throws ExpandVetoException {
        final DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
        treeNode.removeAllChildren();
        this.treeModel.nodeStructureChanged(treeNode);
        final ActionCallback callback = this.actionCreator.createContentBrowseActionCallback(this.service, this.treeModel, treeNode);
        this.controlPoint.execute(callback);
    }
    
    @Override
    public void treeWillCollapse(final TreeExpansionEvent e) throws ExpandVetoException {
    }
}
