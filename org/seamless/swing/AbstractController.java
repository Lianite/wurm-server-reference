// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.swing;

import javax.swing.JFrame;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;
import java.awt.Container;

public class AbstractController<V extends Container> implements Controller<V>
{
    private static Logger log;
    private V view;
    private Controller parentController;
    private List<Controller> subControllers;
    private Map<String, DefaultAction> actions;
    private Map<Class, List<org.seamless.swing.EventListener>> eventListeners;
    
    public AbstractController(final V view) {
        this.subControllers = new ArrayList<Controller>();
        this.actions = new HashMap<String, DefaultAction>();
        this.eventListeners = new HashMap<Class, List<org.seamless.swing.EventListener>>();
        this.view = view;
    }
    
    public AbstractController() {
        this.subControllers = new ArrayList<Controller>();
        this.actions = new HashMap<String, DefaultAction>();
        this.eventListeners = new HashMap<Class, List<org.seamless.swing.EventListener>>();
    }
    
    public AbstractController(final Controller parentController) {
        this(null, parentController);
    }
    
    public AbstractController(final V view, final Controller parentController) {
        this.subControllers = new ArrayList<Controller>();
        this.actions = new HashMap<String, DefaultAction>();
        this.eventListeners = new HashMap<Class, List<org.seamless.swing.EventListener>>();
        this.view = view;
        if (parentController != null) {
            this.parentController = parentController;
            parentController.getSubControllers().add(this);
        }
    }
    
    public V getView() {
        return this.view;
    }
    
    public Controller getParentController() {
        return this.parentController;
    }
    
    public List<Controller> getSubControllers() {
        return this.subControllers;
    }
    
    public void dispose() {
        AbstractController.log.fine("Disposing controller");
        final Iterator<Controller> it = (Iterator<Controller>)this.subControllers.iterator();
        while (it.hasNext()) {
            final Controller subcontroller = it.next();
            subcontroller.dispose();
            it.remove();
        }
    }
    
    public void registerAction(final AbstractButton source, final DefaultAction action) {
        source.removeActionListener(this);
        source.addActionListener(this);
        this.actions.put(source.getActionCommand(), action);
    }
    
    public void registerAction(final AbstractButton source, final String actionCommand, final DefaultAction action) {
        source.setActionCommand(actionCommand);
        this.registerAction(source, action);
    }
    
    public void deregisterAction(final String actionCommand) {
        this.actions.remove(actionCommand);
    }
    
    public void registerEventListener(final Class eventClass, final org.seamless.swing.EventListener eventListener) {
        AbstractController.log.fine("Registering listener: " + eventListener + " for event type: " + eventClass.getName());
        List<org.seamless.swing.EventListener> listenersForEvent = this.eventListeners.get(eventClass);
        if (listenersForEvent == null) {
            listenersForEvent = new ArrayList<org.seamless.swing.EventListener>();
        }
        listenersForEvent.add(eventListener);
        this.eventListeners.put(eventClass, listenersForEvent);
    }
    
    public void fireEvent(final Event event) {
        this.fireEvent(event, false);
    }
    
    public void fireEventGlobal(final Event event) {
        this.fireEvent(event, true);
    }
    
    public void fireEvent(final Event event, final boolean global) {
        if (!event.alreadyFired(this)) {
            AbstractController.log.finest("Event has not been fired already");
            if (this.eventListeners.get(event.getClass()) != null) {
                AbstractController.log.finest("Have listeners for this type of event: " + this.eventListeners.get(event.getClass()));
                for (final org.seamless.swing.EventListener eventListener : this.eventListeners.get(event.getClass())) {
                    AbstractController.log.fine("Processing event: " + event.getClass().getName() + " with listener: " + eventListener.getClass().getName());
                    eventListener.handleEvent(event);
                }
            }
            event.addFiredInController(this);
            AbstractController.log.fine("Passing event: " + event.getClass().getName() + " DOWN in the controller hierarchy");
            for (final Controller subController : this.subControllers) {
                subController.fireEvent(event, global);
            }
        }
        else {
            AbstractController.log.finest("Event already fired here, ignoring...");
        }
        if (this.getParentController() != null && !event.alreadyFired(this.getParentController()) && global) {
            AbstractController.log.fine("Passing event: " + event.getClass().getName() + " UP in the controller hierarchy");
            this.getParentController().fireEvent(event, global);
        }
        else {
            AbstractController.log.finest("Event does not propagate up the tree from here");
        }
    }
    
    public void actionPerformed(final ActionEvent actionEvent) {
        try {
            final AbstractButton button = (AbstractButton)actionEvent.getSource();
            final String actionCommand = button.getActionCommand();
            final DefaultAction action = this.actions.get(actionCommand);
            if (action != null) {
                AbstractController.log.fine("Handling command: " + actionCommand + " with action: " + action.getClass());
                try {
                    this.preActionExecute();
                    AbstractController.log.fine("Dispatching to action for execution");
                    action.executeInController(this, actionEvent);
                    this.postActionExecute();
                }
                catch (RuntimeException ex) {
                    this.failedActionExecute();
                    throw ex;
                }
                catch (Exception ex2) {
                    this.failedActionExecute();
                    throw new RuntimeException(ex2);
                }
                finally {
                    this.finalActionExecute();
                }
            }
            else {
                if (this.getParentController() == null) {
                    throw new RuntimeException("Nobody is responsible for action command: " + actionCommand);
                }
                AbstractController.log.fine("Passing action on to parent controller");
                this.parentController.actionPerformed(actionEvent);
            }
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("Action source is not an Abstractbutton: " + actionEvent);
        }
    }
    
    public void preActionExecute() {
    }
    
    public void postActionExecute() {
    }
    
    public void failedActionExecute() {
    }
    
    public void finalActionExecute() {
    }
    
    public void windowClosing(final WindowEvent windowEvent) {
        this.dispose();
        this.getView().dispose();
    }
    
    public void windowOpened(final WindowEvent windowEvent) {
    }
    
    public void windowClosed(final WindowEvent windowEvent) {
    }
    
    public void windowIconified(final WindowEvent windowEvent) {
    }
    
    public void windowDeiconified(final WindowEvent windowEvent) {
    }
    
    public void windowActivated(final WindowEvent windowEvent) {
    }
    
    public void windowDeactivated(final WindowEvent windowEvent) {
    }
    
    static {
        AbstractController.log = Logger.getLogger(AbstractController.class.getName());
    }
}
