// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.model;

import org.seamless.util.Exceptions;
import java.util.Arrays;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import org.seamless.util.Reflections;
import org.fourthline.cling.model.state.StateVariableAccessor;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.StateVariable;
import java.util.ArrayList;
import org.fourthline.cling.model.state.StateVariableValue;
import java.util.Collection;
import java.util.logging.Level;
import java.util.concurrent.TimeUnit;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.locks.ReentrantLock;
import org.fourthline.cling.model.meta.LocalService;
import java.util.logging.Logger;

public class DefaultServiceManager<T> implements ServiceManager<T>
{
    private static Logger log;
    protected final LocalService<T> service;
    protected final Class<T> serviceClass;
    protected final ReentrantLock lock;
    protected T serviceImpl;
    protected PropertyChangeSupport propertyChangeSupport;
    
    protected DefaultServiceManager(final LocalService<T> service) {
        this(service, null);
    }
    
    public DefaultServiceManager(final LocalService<T> service, final Class<T> serviceClass) {
        this.lock = new ReentrantLock(true);
        this.service = service;
        this.serviceClass = serviceClass;
    }
    
    protected void lock() {
        try {
            if (!this.lock.tryLock(this.getLockTimeoutMillis(), TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Failed to acquire lock in milliseconds: " + this.getLockTimeoutMillis());
            }
            if (DefaultServiceManager.log.isLoggable(Level.FINEST)) {
                DefaultServiceManager.log.finest("Acquired lock");
            }
        }
        catch (InterruptedException e) {
            throw new RuntimeException("Failed to acquire lock:" + e);
        }
    }
    
    protected void unlock() {
        if (DefaultServiceManager.log.isLoggable(Level.FINEST)) {
            DefaultServiceManager.log.finest("Releasing lock");
        }
        this.lock.unlock();
    }
    
    protected int getLockTimeoutMillis() {
        return 500;
    }
    
    @Override
    public LocalService<T> getService() {
        return this.service;
    }
    
    @Override
    public T getImplementation() {
        this.lock();
        try {
            if (this.serviceImpl == null) {
                this.init();
            }
            return this.serviceImpl;
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public PropertyChangeSupport getPropertyChangeSupport() {
        this.lock();
        try {
            if (this.propertyChangeSupport == null) {
                this.init();
            }
            return this.propertyChangeSupport;
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public void execute(final Command<T> cmd) throws Exception {
        this.lock();
        try {
            cmd.execute(this);
        }
        finally {
            this.unlock();
        }
    }
    
    @Override
    public Collection<StateVariableValue> getCurrentState() throws Exception {
        this.lock();
        try {
            Collection<StateVariableValue> values = this.readInitialEventedStateVariableValues();
            if (values != null) {
                DefaultServiceManager.log.fine("Obtained initial state variable values for event, skipping individual state variable accessors");
                return values;
            }
            values = new ArrayList<StateVariableValue>();
            for (final StateVariable stateVariable : this.getService().getStateVariables()) {
                if (stateVariable.getEventDetails().isSendEvents()) {
                    final StateVariableAccessor accessor = this.getService().getAccessor(stateVariable);
                    if (accessor == null) {
                        throw new IllegalStateException("No accessor for evented state variable");
                    }
                    values.add(accessor.read(stateVariable, this.getImplementation()));
                }
            }
            return values;
        }
        finally {
            this.unlock();
        }
    }
    
    protected Collection<StateVariableValue> getCurrentState(final String[] variableNames) throws Exception {
        this.lock();
        try {
            final Collection<StateVariableValue> values = new ArrayList<StateVariableValue>();
            for (String variableName : variableNames) {
                variableName = variableName.trim();
                final StateVariable stateVariable = this.getService().getStateVariable(variableName);
                if (stateVariable == null || !stateVariable.getEventDetails().isSendEvents()) {
                    DefaultServiceManager.log.fine("Ignoring unknown or non-evented state variable: " + variableName);
                }
                else {
                    final StateVariableAccessor accessor = this.getService().getAccessor(stateVariable);
                    if (accessor == null) {
                        DefaultServiceManager.log.warning("Ignoring evented state variable without accessor: " + variableName);
                    }
                    else {
                        values.add(accessor.read(stateVariable, this.getImplementation()));
                    }
                }
            }
            return values;
        }
        finally {
            this.unlock();
        }
    }
    
    protected void init() {
        DefaultServiceManager.log.fine("No service implementation instance available, initializing...");
        try {
            this.serviceImpl = this.createServiceInstance();
            (this.propertyChangeSupport = this.createPropertyChangeSupport(this.serviceImpl)).addPropertyChangeListener(this.createPropertyChangeListener(this.serviceImpl));
        }
        catch (Exception ex) {
            throw new RuntimeException("Could not initialize implementation: " + ex, ex);
        }
    }
    
    protected T createServiceInstance() throws Exception {
        if (this.serviceClass == null) {
            throw new IllegalStateException("Subclass has to provide service class or override createServiceInstance()");
        }
        try {
            return this.serviceClass.getConstructor(LocalService.class).newInstance(this.getService());
        }
        catch (NoSuchMethodException ex) {
            DefaultServiceManager.log.fine("Creating new service implementation instance with no-arg constructor: " + this.serviceClass.getName());
            return this.serviceClass.newInstance();
        }
    }
    
    protected PropertyChangeSupport createPropertyChangeSupport(final T serviceImpl) throws Exception {
        final Method m;
        if ((m = Reflections.getGetterMethod(serviceImpl.getClass(), "propertyChangeSupport")) != null && PropertyChangeSupport.class.isAssignableFrom(m.getReturnType())) {
            DefaultServiceManager.log.fine("Service implementation instance offers PropertyChangeSupport, using that: " + serviceImpl.getClass().getName());
            return (PropertyChangeSupport)m.invoke(serviceImpl, new Object[0]);
        }
        DefaultServiceManager.log.fine("Creating new PropertyChangeSupport for service implementation: " + serviceImpl.getClass().getName());
        return new PropertyChangeSupport(serviceImpl);
    }
    
    protected PropertyChangeListener createPropertyChangeListener(final T serviceImpl) throws Exception {
        return new DefaultPropertyChangeListener();
    }
    
    protected Collection<StateVariableValue> readInitialEventedStateVariableValues() throws Exception {
        return null;
    }
    
    @Override
    public String toString() {
        return "(" + this.getClass().getSimpleName() + ") Implementation: " + this.serviceImpl;
    }
    
    static {
        DefaultServiceManager.log = Logger.getLogger(DefaultServiceManager.class.getName());
    }
    
    protected class DefaultPropertyChangeListener implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent e) {
            DefaultServiceManager.log.finer("Property change event on local service: " + e.getPropertyName());
            if (e.getPropertyName().equals("_EventedStateVariables")) {
                return;
            }
            final String[] variableNames = ModelUtil.fromCommaSeparatedList(e.getPropertyName());
            DefaultServiceManager.log.fine("Changed variable names: " + Arrays.toString(variableNames));
            try {
                final Collection<StateVariableValue> currentValues = DefaultServiceManager.this.getCurrentState(variableNames);
                if (!currentValues.isEmpty()) {
                    DefaultServiceManager.this.getPropertyChangeSupport().firePropertyChange("_EventedStateVariables", null, currentValues);
                }
            }
            catch (Exception ex) {
                DefaultServiceManager.log.log(Level.SEVERE, "Error reading state of service after state variable update event: " + Exceptions.unwrap(ex), ex);
            }
        }
    }
}
