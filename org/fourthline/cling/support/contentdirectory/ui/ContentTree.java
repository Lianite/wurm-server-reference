// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.contentdirectory.ui;

import javax.swing.tree.MutableTreeNode;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.controlpoint.ActionCallback;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.controlpoint.ControlPoint;
import javax.swing.tree.DefaultMutableTreeNode;
import org.fourthline.cling.support.model.container.Container;
import javax.swing.JTree;

public abstract class ContentTree extends JTree implements ContentBrowseActionCallbackCreator
{
    protected org.fourthline.cling.support.model.container.Container rootContainer;
    protected DefaultMutableTreeNode rootNode;
    
    protected ContentTree() {
    }
    
    public ContentTree(final ControlPoint controlPoint, final Service service) {
        this.init(controlPoint, service);
    }
    
    public void init(final ControlPoint controlPoint, final Service service) {
        this.rootContainer = this.createRootContainer(service);
        this.rootNode = new DefaultMutableTreeNode(this.rootContainer) {
            @Override
            public boolean isLeaf() {
                return false;
            }
        };
        final DefaultTreeModel treeModel = new DefaultTreeModel(this.rootNode);
        this.setModel(treeModel);
        this.getSelectionModel().setSelectionMode(1);
        this.addTreeWillExpandListener(this.createContainerTreeExpandListener(controlPoint, service, treeModel));
        this.setCellRenderer(this.createContainerTreeCellRenderer());
        controlPoint.execute(this.createContentBrowseActionCallback(service, treeModel, this.getRootNode()));
    }
    
    public org.fourthline.cling.support.model.container.Container getRootContainer() {
        return this.rootContainer;
    }
    
    public DefaultMutableTreeNode getRootNode() {
        return this.rootNode;
    }
    
    public DefaultMutableTreeNode getSelectedNode() {
        return (DefaultMutableTreeNode)this.getLastSelectedPathComponent();
    }
    
    protected org.fourthline.cling.support.model.container.Container createRootContainer(final Service service) {
        final org.fourthline.cling.support.model.container.Container rootContainer = new org.fourthline.cling.support.model.container.Container();
        rootContainer.setId("0");
        rootContainer.setTitle("Content Directory on " + service.getDevice().getDisplayString());
        return rootContainer;
    }
    
    protected TreeWillExpandListener createContainerTreeExpandListener(final ControlPoint controlPoint, final Service service, final DefaultTreeModel treeModel) {
        return new ContentTreeExpandListener(controlPoint, service, treeModel, this);
    }
    
    protected DefaultTreeCellRenderer createContainerTreeCellRenderer() {
        return new ContentTreeCellRenderer();
    }
    
    @Override
    public ActionCallback createContentBrowseActionCallback(final Service service, final DefaultTreeModel treeModel, final DefaultMutableTreeNode treeNode) {
        return new ContentBrowseActionCallback(service, treeModel, treeNode) {
            @Override
            public void updateStatusUI(final Status status, final DefaultMutableTreeNode treeNode, final DefaultTreeModel treeModel) {
                ContentTree.this.updateStatus(status, treeNode, treeModel);
            }
            
            @Override
            public void failureUI(final String failureMessage) {
                ContentTree.this.failure(failureMessage);
            }
        };
    }
    
    public void updateStatus(final Browse.Status status, final DefaultMutableTreeNode treeNode, final DefaultTreeModel treeModel) {
        switch (status) {
            case LOADING:
            case NO_CONTENT: {
                treeNode.removeAllChildren();
                final int index = (treeNode.getChildCount() <= 0) ? 0 : treeNode.getChildCount();
                treeModel.insertNodeInto(new DefaultMutableTreeNode(status.getDefaultMessage()), treeNode, index);
                treeModel.nodeStructureChanged(treeNode);
                break;
            }
        }
    }
    
    public abstract void failure(final String p0);
}
