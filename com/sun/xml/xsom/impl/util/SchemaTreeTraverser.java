// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.xml.xsom.impl.util;

import javax.swing.JTree;
import javax.swing.Icon;
import java.awt.Graphics;
import javax.swing.Box;
import java.awt.Component;
import javax.swing.border.Border;
import java.awt.LayoutManager;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.JPanel;
import org.xml.sax.Locator;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultTreeModel;
import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.visitor.XSTermVisitor;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSUnionSimpleType;
import com.sun.xml.xsom.XSListSimpleType;
import java.text.MessageFormat;
import com.sun.xml.xsom.XSAttributeUse;
import javax.swing.tree.MutableTreeNode;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttGroupDecl;
import java.util.Iterator;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.visitor.XSSimpleTypeVisitor;
import com.sun.xml.xsom.visitor.XSVisitor;

public class SchemaTreeTraverser implements XSVisitor, XSSimpleTypeVisitor
{
    private SchemaTreeModel model;
    private SchemaTreeNode currNode;
    
    public SchemaTreeTraverser() {
        this.model = SchemaTreeModel.getInstance();
        this.currNode = (SchemaTreeNode)this.model.getRoot();
    }
    
    public SchemaTreeModel getModel() {
        return this.model;
    }
    
    public void visit(final XSSchemaSet s) {
        for (final XSSchema schema : s.getSchemas()) {
            this.schema(schema);
        }
    }
    
    public void schema(final XSSchema s) {
        if (s.getTargetNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
            return;
        }
        final SchemaTreeNode newNode = new SchemaTreeNode("Schema " + s.getLocator().getSystemId(), s.getLocator());
        this.currNode = newNode;
        this.model.addSchemaNode(newNode);
        for (final XSAttGroupDecl groupDecl : s.getAttGroupDecls().values()) {
            this.attGroupDecl(groupDecl);
        }
        for (final XSAttributeDecl attrDecl : s.getAttributeDecls().values()) {
            this.attributeDecl(attrDecl);
        }
        for (final XSComplexType complexType : s.getComplexTypes().values()) {
            this.complexType(complexType);
        }
        for (final XSElementDecl elementDecl : s.getElementDecls().values()) {
            this.elementDecl(elementDecl);
        }
        for (final XSModelGroupDecl modelGroupDecl : s.getModelGroupDecls().values()) {
            this.modelGroupDecl(modelGroupDecl);
        }
        for (final XSSimpleType simpleType : s.getSimpleTypes().values()) {
            this.simpleType(simpleType);
        }
    }
    
    public void attGroupDecl(final XSAttGroupDecl decl) {
        final SchemaTreeNode newNode = new SchemaTreeNode("Attribute group \"" + decl.getName() + "\"", decl.getLocator());
        this.currNode.add(newNode);
        this.currNode = newNode;
        Iterator itr = decl.iterateAttGroups();
        while (itr.hasNext()) {
            this.dumpRef(itr.next());
        }
        itr = decl.iterateDeclaredAttributeUses();
        while (itr.hasNext()) {
            this.attributeUse(itr.next());
        }
        this.currNode = (SchemaTreeNode)this.currNode.getParent();
    }
    
    public void dumpRef(final XSAttGroupDecl decl) {
        final SchemaTreeNode newNode = new SchemaTreeNode("Attribute group ref \"{" + decl.getTargetNamespace() + "}" + decl.getName() + "\"", decl.getLocator());
        this.currNode.add(newNode);
    }
    
    public void attributeUse(final XSAttributeUse use) {
        final XSAttributeDecl decl = use.getDecl();
        String additionalAtts = "";
        if (use.isRequired()) {
            additionalAtts += " use=\"required\"";
        }
        if (use.getFixedValue() != null && use.getDecl().getFixedValue() == null) {
            additionalAtts = additionalAtts + " fixed=\"" + use.getFixedValue() + "\"";
        }
        if (use.getDefaultValue() != null && use.getDecl().getDefaultValue() == null) {
            additionalAtts = additionalAtts + " default=\"" + use.getDefaultValue() + "\"";
        }
        if (decl.isLocal()) {
            this.dump(decl, additionalAtts);
        }
        else {
            final String str = MessageFormat.format("Attribute ref \"'{'{0}'}'{1}{2}\"", decl.getTargetNamespace(), decl.getName(), additionalAtts);
            final SchemaTreeNode newNode = new SchemaTreeNode(str, decl.getLocator());
            this.currNode.add(newNode);
        }
    }
    
    public void attributeDecl(final XSAttributeDecl decl) {
        this.dump(decl, "");
    }
    
    private void dump(final XSAttributeDecl decl, final String additionalAtts) {
        final XSSimpleType type = decl.getType();
        final String str = MessageFormat.format("Attribute \"{0}\"{1}{2}{3}{4}", decl.getName(), additionalAtts, type.isLocal() ? "" : MessageFormat.format(" type=\"'{'{0}'}'{1}\"", type.getTargetNamespace(), type.getName()), (decl.getFixedValue() == null) ? "" : (" fixed=\"" + decl.getFixedValue() + "\""), (decl.getDefaultValue() == null) ? "" : (" default=\"" + decl.getDefaultValue() + "\""));
        final SchemaTreeNode newNode = new SchemaTreeNode(str, decl.getLocator());
        this.currNode.add(newNode);
        this.currNode = newNode;
        if (type.isLocal()) {
            this.simpleType(type);
        }
        this.currNode = (SchemaTreeNode)this.currNode.getParent();
    }
    
    public void simpleType(final XSSimpleType type) {
        final String str = MessageFormat.format("Simple type {0}", type.isLocal() ? "" : (" name=\"" + type.getName() + "\""));
        final SchemaTreeNode newNode = new SchemaTreeNode(str, type.getLocator());
        this.currNode.add(newNode);
        this.currNode = newNode;
        type.visit((XSSimpleTypeVisitor)this);
        this.currNode = (SchemaTreeNode)this.currNode.getParent();
    }
    
    public void listSimpleType(final XSListSimpleType type) {
        final XSSimpleType itemType = type.getItemType();
        if (itemType.isLocal()) {
            final SchemaTreeNode newNode = new SchemaTreeNode("List", type.getLocator());
            this.currNode.add(newNode);
            this.currNode = newNode;
            this.simpleType(itemType);
            this.currNode = (SchemaTreeNode)this.currNode.getParent();
        }
        else {
            final String str = MessageFormat.format("List itemType=\"'{'{0}'}'{1}\"", itemType.getTargetNamespace(), itemType.getName());
            final SchemaTreeNode newNode2 = new SchemaTreeNode(str, itemType.getLocator());
            this.currNode.add(newNode2);
        }
    }
    
    public void unionSimpleType(final XSUnionSimpleType type) {
        final int len = type.getMemberSize();
        final StringBuffer ref = new StringBuffer();
        for (int i = 0; i < len; ++i) {
            final XSSimpleType member = type.getMember(i);
            if (member.isGlobal()) {
                ref.append(MessageFormat.format(" '{'{0}'}'{1}", member.getTargetNamespace(), member.getName()));
            }
        }
        final String name = (ref.length() == 0) ? "Union" : ("Union memberTypes=\"" + (Object)ref + "\"");
        final SchemaTreeNode newNode = new SchemaTreeNode(name, type.getLocator());
        this.currNode.add(newNode);
        this.currNode = newNode;
        for (int j = 0; j < len; ++j) {
            final XSSimpleType member2 = type.getMember(j);
            if (member2.isLocal()) {
                this.simpleType(member2);
            }
        }
        this.currNode = (SchemaTreeNode)this.currNode.getParent();
    }
    
    public void restrictionSimpleType(final XSRestrictionSimpleType type) {
        if (type.getBaseType() != null) {
            final XSSimpleType baseType = type.getSimpleBaseType();
            final String str = MessageFormat.format("Restriction {0}", baseType.isLocal() ? "" : (" base=\"{" + baseType.getTargetNamespace() + "}" + baseType.getName() + "\""));
            final SchemaTreeNode newNode = new SchemaTreeNode(str, baseType.getLocator());
            this.currNode.add(newNode);
            this.currNode = newNode;
            if (baseType.isLocal()) {
                this.simpleType(baseType);
            }
            final Iterator itr = type.iterateDeclaredFacets();
            while (itr.hasNext()) {
                this.facet(itr.next());
            }
            this.currNode = (SchemaTreeNode)this.currNode.getParent();
            return;
        }
        if (!type.getName().equals("anySimpleType")) {
            throw new InternalError();
        }
        if (!"http://www.w3.org/2001/XMLSchema".equals(type.getTargetNamespace())) {
            throw new InternalError();
        }
    }
    
    public void facet(final XSFacet facet) {
        final SchemaTreeNode newNode = new SchemaTreeNode(MessageFormat.format("{0} value=\"{1}\"", facet.getName(), facet.getValue()), facet.getLocator());
        this.currNode.add(newNode);
    }
    
    public void notation(final XSNotation notation) {
        final SchemaTreeNode newNode = new SchemaTreeNode(MessageFormat.format("Notation name='\"0}\" public =\"{1}\" system=\"{2}\"", notation.getName(), notation.getPublicId(), notation.getSystemId()), notation.getLocator());
        this.currNode.add(newNode);
    }
    
    public void complexType(final XSComplexType type) {
        final SchemaTreeNode newNode = new SchemaTreeNode(MessageFormat.format("ComplexType {0}", type.isLocal() ? "" : (" name=\"" + type.getName() + "\"")), type.getLocator());
        this.currNode.add(newNode);
        this.currNode = newNode;
        if (type.getContentType().asSimpleType() != null) {
            final SchemaTreeNode newNode2 = new SchemaTreeNode("Simple content", type.getContentType().getLocator());
            this.currNode.add(newNode2);
            this.currNode = newNode2;
            final XSType baseType = type.getBaseType();
            if (type.getDerivationMethod() == 2) {
                final String str = MessageFormat.format("Restriction base=\"<{0}>{1}\"", baseType.getTargetNamespace(), baseType.getName());
                final SchemaTreeNode newNode3 = new SchemaTreeNode(str, baseType.getLocator());
                this.currNode.add(newNode3);
                this.currNode = newNode3;
                this.dumpComplexTypeAttribute(type);
                this.currNode = (SchemaTreeNode)this.currNode.getParent();
            }
            else {
                final String str = MessageFormat.format("Extension base=\"<{0}>{1}\"", baseType.getTargetNamespace(), baseType.getName());
                final SchemaTreeNode newNode3 = new SchemaTreeNode(str, baseType.getLocator());
                this.currNode.add(newNode3);
                this.currNode = newNode3;
                if (type.getTargetNamespace().compareTo(baseType.getTargetNamespace()) == 0 && type.getName().compareTo(baseType.getName()) == 0) {
                    final SchemaTreeNode newNodeRedefine = new SchemaTreeNode("redefine", type.getLocator());
                    this.currNode.add(newNodeRedefine);
                    this.currNode = newNodeRedefine;
                    baseType.visit(this);
                    this.currNode = (SchemaTreeNode)newNodeRedefine.getParent();
                }
                this.dumpComplexTypeAttribute(type);
                this.currNode = (SchemaTreeNode)this.currNode.getParent();
            }
            this.currNode = (SchemaTreeNode)this.currNode.getParent();
        }
        else {
            final SchemaTreeNode newNode2 = new SchemaTreeNode("Complex content", type.getContentType().getLocator());
            this.currNode.add(newNode2);
            this.currNode = newNode2;
            final XSComplexType baseType2 = type.getBaseType().asComplexType();
            if (type.getDerivationMethod() == 2) {
                final String str = MessageFormat.format("Restriction base=\"<{0}>{1}\"", baseType2.getTargetNamespace(), baseType2.getName());
                final SchemaTreeNode newNode3 = new SchemaTreeNode(str, baseType2.getLocator());
                this.currNode.add(newNode3);
                this.currNode = newNode3;
                type.getContentType().visit(this);
                this.dumpComplexTypeAttribute(type);
                this.currNode = (SchemaTreeNode)this.currNode.getParent();
            }
            else {
                final String str = MessageFormat.format("Extension base=\"'{'{0}'}'{1}\"", baseType2.getTargetNamespace(), baseType2.getName());
                final SchemaTreeNode newNode3 = new SchemaTreeNode(str, baseType2.getLocator());
                this.currNode.add(newNode3);
                this.currNode = newNode3;
                if (type.getTargetNamespace().compareTo(baseType2.getTargetNamespace()) == 0 && type.getName().compareTo(baseType2.getName()) == 0) {
                    final SchemaTreeNode newNodeRedefine = new SchemaTreeNode("redefine", type.getLocator());
                    this.currNode.add(newNodeRedefine);
                    this.currNode = newNodeRedefine;
                    baseType2.visit(this);
                    this.currNode = (SchemaTreeNode)newNodeRedefine.getParent();
                }
                type.getExplicitContent().visit(this);
                this.dumpComplexTypeAttribute(type);
                this.currNode = (SchemaTreeNode)this.currNode.getParent();
            }
            this.currNode = (SchemaTreeNode)this.currNode.getParent();
        }
        this.currNode = (SchemaTreeNode)this.currNode.getParent();
    }
    
    private void dumpComplexTypeAttribute(final XSComplexType type) {
        Iterator itr = type.iterateAttGroups();
        while (itr.hasNext()) {
            this.dumpRef(itr.next());
        }
        itr = type.iterateDeclaredAttributeUses();
        while (itr.hasNext()) {
            this.attributeUse(itr.next());
        }
    }
    
    public void elementDecl(final XSElementDecl decl) {
        this.elementDecl(decl, "");
    }
    
    private void elementDecl(final XSElementDecl decl, final String extraAtts) {
        final XSType type = decl.getType();
        final String str = MessageFormat.format("Element name=\"{0}\"{1}{2}", decl.getName(), type.isLocal() ? "" : (" type=\"{" + type.getTargetNamespace() + "}" + type.getName() + "\""), extraAtts);
        final SchemaTreeNode newNode = new SchemaTreeNode(str, decl.getLocator());
        this.currNode.add(newNode);
        this.currNode = newNode;
        if (type.isLocal() && type.isLocal()) {
            type.visit(this);
        }
        this.currNode = (SchemaTreeNode)this.currNode.getParent();
    }
    
    public void modelGroupDecl(final XSModelGroupDecl decl) {
        final SchemaTreeNode newNode = new SchemaTreeNode(MessageFormat.format("Group name=\"{0}\"", decl.getName()), decl.getLocator());
        this.currNode.add(newNode);
        this.currNode = newNode;
        this.modelGroup(decl.getModelGroup());
        this.currNode = (SchemaTreeNode)this.currNode.getParent();
    }
    
    public void modelGroup(final XSModelGroup group) {
        this.modelGroup(group, "");
    }
    
    private void modelGroup(final XSModelGroup group, final String extraAtts) {
        final SchemaTreeNode newNode = new SchemaTreeNode(MessageFormat.format("{0}{1}", group.getCompositor(), extraAtts), group.getLocator());
        this.currNode.add(newNode);
        this.currNode = newNode;
        for (int len = group.getSize(), i = 0; i < len; ++i) {
            this.particle(group.getChild(i));
        }
        this.currNode = (SchemaTreeNode)this.currNode.getParent();
    }
    
    public void particle(final XSParticle part) {
        final StringBuffer buf = new StringBuffer();
        int i = part.getMaxOccurs();
        if (i == -1) {
            buf.append(" maxOccurs=\"unbounded\"");
        }
        else if (i != 1) {
            buf.append(" maxOccurs=\"" + i + "\"");
        }
        i = part.getMinOccurs();
        if (i != 1) {
            buf.append(" minOccurs=\"" + i + "\"");
        }
        final String extraAtts = buf.toString();
        part.getTerm().visit(new XSTermVisitor() {
            public void elementDecl(final XSElementDecl decl) {
                if (decl.isLocal()) {
                    SchemaTreeTraverser.this.elementDecl(decl, extraAtts);
                }
                else {
                    final SchemaTreeNode newNode = new SchemaTreeNode(MessageFormat.format("Element ref=\"'{'{0}'}'{1}\"{2}", decl.getTargetNamespace(), decl.getName(), extraAtts), decl.getLocator());
                    SchemaTreeTraverser.this.currNode.add(newNode);
                }
            }
            
            public void modelGroupDecl(final XSModelGroupDecl decl) {
                final SchemaTreeNode newNode = new SchemaTreeNode(MessageFormat.format("Group ref=\"'{'{0}'}'{1}\"{2}", decl.getTargetNamespace(), decl.getName(), extraAtts), decl.getLocator());
                SchemaTreeTraverser.this.currNode.add(newNode);
            }
            
            public void modelGroup(final XSModelGroup group) {
                SchemaTreeTraverser.this.modelGroup(group, extraAtts);
            }
            
            public void wildcard(final XSWildcard wc) {
                SchemaTreeTraverser.this.wildcard(wc, extraAtts);
            }
        });
    }
    
    public void wildcard(final XSWildcard wc) {
        this.wildcard(wc, "");
    }
    
    private void wildcard(final XSWildcard wc, final String extraAtts) {
        final SchemaTreeNode newNode = new SchemaTreeNode(MessageFormat.format("Any ", extraAtts), wc.getLocator());
        this.currNode.add(newNode);
    }
    
    public void annotation(final XSAnnotation ann) {
    }
    
    public void empty(final XSContentType t) {
    }
    
    public void identityConstraint(final XSIdentityConstraint ic) {
    }
    
    public void xpath(final XSXPath xp) {
    }
    
    public static final class SchemaTreeModel extends DefaultTreeModel
    {
        private SchemaTreeModel(final SchemaRootNode root) {
            super(root);
        }
        
        public static SchemaTreeModel getInstance() {
            final SchemaRootNode root = new SchemaRootNode();
            return new SchemaTreeModel(root);
        }
        
        public void addSchemaNode(final SchemaTreeNode node) {
            ((SchemaRootNode)this.root).add(node);
        }
    }
    
    public static class SchemaTreeNode extends DefaultMutableTreeNode
    {
        private String fileName;
        private int lineNumber;
        private String artifactName;
        
        public SchemaTreeNode(final String artifactName, final Locator locator) {
            this.artifactName = artifactName;
            if (locator == null) {
                this.fileName = null;
            }
            else {
                String filename = locator.getSystemId();
                filename = filename.replaceAll("%20", " ");
                if (filename.startsWith("file:/")) {
                    filename = filename.substring(6);
                }
                this.fileName = filename;
                this.lineNumber = locator.getLineNumber() - 1;
            }
        }
        
        public String getCaption() {
            return this.artifactName;
        }
        
        public String getFileName() {
            return this.fileName;
        }
        
        public void setFileName(final String fileName) {
            this.fileName = fileName;
        }
        
        public int getLineNumber() {
            return this.lineNumber;
        }
        
        public void setLineNumber(final int lineNumber) {
            this.lineNumber = lineNumber;
        }
    }
    
    public static class SchemaRootNode extends SchemaTreeNode
    {
        public SchemaRootNode() {
            super("Schema set", null);
        }
    }
    
    public static class SchemaTreeCellRenderer extends JPanel implements TreeCellRenderer
    {
        protected final JLabel iconLabel;
        protected final JLabel nameLabel;
        private boolean isSelected;
        public final Color selectedBackground;
        public final Color selectedForeground;
        public final Font nameFont;
        
        public SchemaTreeCellRenderer() {
            this.selectedBackground = new Color(255, 244, 232);
            this.selectedForeground = new Color(64, 32, 0);
            this.nameFont = new Font("Arial", 1, 12);
            final FlowLayout fl = new FlowLayout(0, 1, 1);
            this.setLayout(fl);
            (this.iconLabel = new JLabel()).setOpaque(false);
            this.iconLabel.setBorder(null);
            this.add(this.iconLabel);
            this.add(Box.createHorizontalStrut(5));
            (this.nameLabel = new JLabel()).setOpaque(false);
            this.nameLabel.setBorder(null);
            this.nameLabel.setFont(this.nameFont);
            this.add(this.nameLabel);
            this.setOpaque(this.isSelected = false);
            this.setBorder(null);
        }
        
        public final void paintComponent(final Graphics g) {
            final int width = this.getWidth();
            final int height = this.getHeight();
            if (this.isSelected) {
                g.setColor(this.selectedBackground);
                g.fillRect(0, 0, width - 1, height - 1);
                g.setColor(this.selectedForeground);
                g.drawRect(0, 0, width - 1, height - 1);
            }
            super.paintComponent(g);
        }
        
        protected final void setValues(final Icon icon, final String caption, final boolean selected) {
            this.iconLabel.setIcon(icon);
            this.nameLabel.setText(caption);
            this.isSelected = selected;
            if (selected) {
                this.nameLabel.setForeground(this.selectedForeground);
            }
            else {
                this.nameLabel.setForeground(Color.black);
            }
        }
        
        public final Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
            if (value instanceof SchemaTreeNode) {
                final SchemaTreeNode stn = (SchemaTreeNode)value;
                this.setValues(null, stn.getCaption(), selected);
                return this;
            }
            throw new IllegalStateException("Unknown node");
        }
    }
}
