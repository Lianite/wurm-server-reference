// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing;

import javax.swing.AbstractButton;
import java.util.List;
import java.awt.event.WindowListener;
import java.awt.event.ActionListener;
import java.awt.Container;

public interface Controller<V extends Container> extends ActionListener, WindowListener
{
    V getView();
    
    Controller getParentController();
    
    List<Controller> getSubControllers();
    
    void dispose();
    
    void registerEventListener(final Class p0, final org.seamless.swing.EventListener p1);
    
    void fireEvent(final Event p0);
    
    void fireEventGlobal(final Event p0);
    
    void fireEvent(final Event p0, final boolean p1);
    
    void registerAction(final AbstractButton p0, final DefaultAction p1);
    
    void registerAction(final AbstractButton p0, final String p1, final DefaultAction p2);
    
    void preActionExecute();
    
    void postActionExecute();
    
    void failedActionExecute();
    
    void finalActionExecute();
}
