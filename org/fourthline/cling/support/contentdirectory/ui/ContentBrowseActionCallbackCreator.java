// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.contentdirectory.ui;

import org.fourthline.cling.controlpoint.ActionCallback;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.fourthline.cling.model.meta.Service;

public interface ContentBrowseActionCallbackCreator
{
    ActionCallback createContentBrowseActionCallback(final Service p0, final DefaultTreeModel p1, final DefaultMutableTreeNode p2);
}
