// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public abstract class DefaultAction extends AbstractAction
{
    public void executeInController(final Controller controller, final ActionEvent event) {
        this.actionPerformed(event);
    }
}
