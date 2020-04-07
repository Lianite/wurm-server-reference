// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing;

import java.awt.event.KeyEvent;
import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JButton;

public class JPopupMenuButton extends JButton
{
    public JPopupMenu popup;
    
    public JPopupMenuButton(final JPopupMenu m) {
        this.popup = m;
        this.enableEvents(8L);
        this.enableEvents(16L);
    }
    
    public JPopupMenuButton(final String s, final JPopupMenu m) {
        super(s);
        this.popup = m;
        this.enableEvents(8L);
        this.enableEvents(16L);
    }
    
    public JPopupMenuButton(final String s, final Icon icon, final JPopupMenu popup) {
        super(s, icon);
        this.popup = popup;
        this.enableEvents(8L);
        this.enableEvents(16L);
        this.setModel(new DefaultButtonModel() {
            public void setPressed(final boolean b) {
            }
        });
    }
    
    protected void processMouseEvent(final MouseEvent e) {
        super.processMouseEvent(e);
        final int id = e.getID();
        if (id == 501) {
            if (this.popup != null) {
                this.popup.show(this, 0, 0);
            }
        }
        else if (id == 502 && this.popup != null) {
            this.popup.setVisible(false);
        }
    }
    
    protected void processKeyEvent(final KeyEvent e) {
        final int id = e.getID();
        if (id == 401 || id == 400) {
            if (e.getKeyCode() == 10) {
                this.popup.show(this, 0, 10);
            }
            super.processKeyEvent(e);
        }
    }
}
