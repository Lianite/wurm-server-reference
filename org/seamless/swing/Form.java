// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing;

import javax.swing.JSeparator;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.Container;
import java.awt.Component;
import java.awt.Insets;
import java.awt.GridBagConstraints;

public class Form
{
    public GridBagConstraints lastConstraints;
    public GridBagConstraints middleConstraints;
    public GridBagConstraints labelConstraints;
    public GridBagConstraints separatorConstraints;
    
    public Form(final int padding) {
        this.lastConstraints = null;
        this.middleConstraints = null;
        this.labelConstraints = null;
        this.separatorConstraints = null;
        this.lastConstraints = new GridBagConstraints();
        this.lastConstraints.fill = 2;
        this.lastConstraints.anchor = 18;
        this.lastConstraints.weightx = 1.0;
        this.lastConstraints.gridwidth = 0;
        this.lastConstraints.insets = new Insets(padding, padding, padding, padding);
        this.middleConstraints = (GridBagConstraints)this.lastConstraints.clone();
        this.middleConstraints.gridwidth = -1;
        this.labelConstraints = (GridBagConstraints)this.lastConstraints.clone();
        this.labelConstraints.weightx = 0.0;
        this.labelConstraints.gridwidth = 1;
        this.separatorConstraints = new GridBagConstraints();
        this.separatorConstraints.fill = 2;
        this.separatorConstraints.gridwidth = 0;
    }
    
    public void addLastField(final Component c, final Container parent) {
        final GridBagLayout gbl = (GridBagLayout)parent.getLayout();
        gbl.setConstraints(c, this.lastConstraints);
        parent.add(c);
    }
    
    public void addLabel(final Component c, final Container parent) {
        final GridBagLayout gbl = (GridBagLayout)parent.getLayout();
        gbl.setConstraints(c, this.labelConstraints);
        parent.add(c);
    }
    
    public JLabel addLabel(final String s, final Container parent) {
        final JLabel c = new JLabel(s);
        this.addLabel(c, parent);
        return c;
    }
    
    public void addMiddleField(final Component c, final Container parent) {
        final GridBagLayout gbl = (GridBagLayout)parent.getLayout();
        gbl.setConstraints(c, this.middleConstraints);
        parent.add(c);
    }
    
    public void addLabelAndLastField(final String s, final Container c, final Container parent) {
        this.addLabel(s, parent);
        this.addLastField(c, parent);
    }
    
    public void addLabelAndLastField(final String s, final String value, final Container parent) {
        this.addLabel(s, parent);
        this.addLastField(new JLabel(value), parent);
    }
    
    public void addSeparator(final Container parent) {
        final JSeparator separator = new JSeparator();
        final GridBagLayout gbl = (GridBagLayout)parent.getLayout();
        gbl.setConstraints(separator, this.separatorConstraints);
        parent.add(separator);
    }
}
