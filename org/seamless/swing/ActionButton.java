// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing;

import javax.swing.AbstractButton;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

public class ActionButton extends JButton
{
    public ActionButton(final String actionCommand) {
        this.setActionCommand(actionCommand);
    }
    
    public ActionButton(final Icon icon, final String actionCommand) {
        super(icon);
        this.setActionCommand(actionCommand);
    }
    
    public ActionButton(final String s, final String actionCommand) {
        super(s);
        this.setActionCommand(actionCommand);
    }
    
    public ActionButton(final Action action, final String actionCommand) {
        super(action);
        this.setActionCommand(actionCommand);
    }
    
    public ActionButton(final String s, final Icon icon, final String actionCommand) {
        super(s, icon);
        this.setActionCommand(actionCommand);
    }
    
    public ActionButton enableDefaultEvents(final Controller controller) {
        controller.registerAction(this, new DefaultAction() {
            public void actionPerformed(final ActionEvent actionEvent) {
                Event e;
                if ((e = ActionButton.this.createDefaultEvent()) != null) {
                    controller.fireEvent(e);
                }
                if ((e = ActionButton.this.createDefaultGlobalEvent()) != null) {
                    controller.fireEventGlobal(e);
                }
            }
        });
        return this;
    }
    
    public Event createDefaultEvent() {
        return null;
    }
    
    public Event createDefaultGlobalEvent() {
        return null;
    }
}
