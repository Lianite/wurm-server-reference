// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.contentdirectory.ui;

import javax.swing.tree.TreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.Iterator;
import javax.swing.SwingUtilities;
import java.util.List;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.support.model.item.Item;
import java.util.ArrayList;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.model.meta.Service;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.logging.Logger;
import org.fourthline.cling.support.contentdirectory.callback.Browse;

public abstract class ContentBrowseActionCallback extends Browse
{
    private static Logger log;
    protected final DefaultTreeModel treeModel;
    protected final DefaultMutableTreeNode treeNode;
    
    public ContentBrowseActionCallback(final Service service, final DefaultTreeModel treeModel, final DefaultMutableTreeNode treeNode) {
        super(service, ((Container)treeNode.getUserObject()).getId(), BrowseFlag.DIRECT_CHILDREN, "*", 0L, null, new SortCriterion[] { new SortCriterion(true, "dc:title") });
        this.treeModel = treeModel;
        this.treeNode = treeNode;
    }
    
    public ContentBrowseActionCallback(final Service service, final DefaultTreeModel treeModel, final DefaultMutableTreeNode treeNode, final String filter, final long firstResult, final long maxResults, final SortCriterion... orderBy) {
        super(service, ((Container)treeNode.getUserObject()).getId(), BrowseFlag.DIRECT_CHILDREN, filter, firstResult, maxResults, orderBy);
        this.treeModel = treeModel;
        this.treeNode = treeNode;
    }
    
    public DefaultTreeModel getTreeModel() {
        return this.treeModel;
    }
    
    public DefaultMutableTreeNode getTreeNode() {
        return this.treeNode;
    }
    
    @Override
    public void received(final ActionInvocation actionInvocation, final DIDLContent didl) {
        ContentBrowseActionCallback.log.fine("Received browse action DIDL descriptor, creating tree nodes");
        final List<DefaultMutableTreeNode> childNodes = new ArrayList<DefaultMutableTreeNode>();
        try {
            for (final Container childContainer : didl.getContainers()) {
                final DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childContainer) {
                    @Override
                    public boolean isLeaf() {
                        return false;
                    }
                };
                childNodes.add(childNode);
            }
            for (final Item childItem : didl.getItems()) {
                final DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childItem) {
                    @Override
                    public boolean isLeaf() {
                        return true;
                    }
                };
                childNodes.add(childNode);
            }
        }
        catch (Exception ex) {
            ContentBrowseActionCallback.log.fine("Creating DIDL tree nodes failed: " + ex);
            actionInvocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Can't create tree child nodes: " + ex, ex));
            this.failure(actionInvocation, null);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ContentBrowseActionCallback.this.updateTreeModel(childNodes);
            }
        });
    }
    
    @Override
    public void updateStatus(final Status status) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ContentBrowseActionCallback.this.updateStatusUI(status, ContentBrowseActionCallback.this.treeNode, ContentBrowseActionCallback.this.treeModel);
            }
        });
    }
    
    @Override
    public void failure(final ActionInvocation invocation, final UpnpResponse operation, final String defaultMsg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ContentBrowseActionCallback.this.failureUI(defaultMsg);
            }
        });
    }
    
    protected void updateTreeModel(final List<DefaultMutableTreeNode> childNodes) {
        ContentBrowseActionCallback.log.fine("Adding nodes to tree: " + childNodes.size());
        this.removeChildren();
        for (final DefaultMutableTreeNode childNode : childNodes) {
            this.insertChild(childNode);
        }
    }
    
    protected void removeChildren() {
        this.treeNode.removeAllChildren();
        this.treeModel.nodeStructureChanged(this.treeNode);
    }
    
    protected void insertChild(final MutableTreeNode childNode) {
        final int index = (this.treeNode.getChildCount() <= 0) ? 0 : this.treeNode.getChildCount();
        this.treeModel.insertNodeInto(childNode, this.treeNode, index);
    }
    
    public abstract void updateStatusUI(final Status p0, final DefaultMutableTreeNode p1, final DefaultTreeModel p2);
    
    public abstract void failureUI(final String p0);
    
    static {
        ContentBrowseActionCallback.log = Logger.getLogger(ContentBrowseActionCallback.class.getName());
    }
}
