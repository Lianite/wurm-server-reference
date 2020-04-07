// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.contentdirectory.ui;

import javax.swing.Icon;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.container.Container;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class ContentTreeCellRenderer extends DefaultTreeCellRenderer
{
    @Override
    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        if (node.getUserObject() instanceof org.fourthline.cling.support.model.container.Container) {
            final org.fourthline.cling.support.model.container.Container container = (org.fourthline.cling.support.model.container.Container)node.getUserObject();
            this.setText(container.getTitle());
            this.setIcon(expanded ? this.getContainerOpenIcon() : this.getContainerClosedIcon());
        }
        else if (node.getUserObject() instanceof Item) {
            final Item item = (Item)node.getUserObject();
            this.setText(item.getTitle());
            final DIDLObject.Class upnpClass = item.getClazz();
            this.setIcon(this.getItemIcon(item, (upnpClass != null) ? upnpClass.getValue() : null));
        }
        else if (node.getUserObject() instanceof String) {
            this.setIcon(this.getInfoIcon());
        }
        this.onCreate();
        return this;
    }
    
    protected void onCreate() {
    }
    
    protected Icon getContainerOpenIcon() {
        return null;
    }
    
    protected Icon getContainerClosedIcon() {
        return null;
    }
    
    protected Icon getItemIcon(final Item item, final String upnpClass) {
        return null;
    }
    
    protected Icon getInfoIcon() {
        return null;
    }
}
