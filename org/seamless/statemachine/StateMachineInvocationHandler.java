// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.statemachine;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.lang.reflect.InvocationHandler;

public class StateMachineInvocationHandler implements InvocationHandler
{
    private static Logger log;
    public static final String METHOD_ON_ENTRY = "onEntry";
    public static final String METHOD_ON_EXIT = "onExit";
    final Class initialStateClass;
    final Map<Class, Object> stateObjects;
    Object currentState;
    
    StateMachineInvocationHandler(final List<Class<?>> stateClasses, final Class<?> initialStateClass, final Class[] constructorArgumentTypes, final Object[] constructorArguments) {
        this.stateObjects = new ConcurrentHashMap<Class, Object>();
        StateMachineInvocationHandler.log.fine("Creating state machine with initial state: " + initialStateClass);
        this.initialStateClass = initialStateClass;
        for (final Class<?> stateClass : stateClasses) {
            try {
                final Object state = (constructorArgumentTypes != null) ? stateClass.getConstructor((Class<?>[])constructorArgumentTypes).newInstance(constructorArguments) : stateClass.newInstance();
                StateMachineInvocationHandler.log.fine("Adding state instance: " + state.getClass().getName());
                this.stateObjects.put(stateClass, state);
            }
            catch (NoSuchMethodException ex) {
                throw new RuntimeException("State " + stateClass.getName() + " has the wrong constructor: " + ex, ex);
            }
            catch (Exception ex2) {
                throw new RuntimeException("State " + stateClass.getName() + " can't be instantiated: " + ex2, ex2);
            }
        }
        if (!this.stateObjects.containsKey(initialStateClass)) {
            throw new RuntimeException("Initial state not in list of states: " + initialStateClass);
        }
        this.currentState = this.stateObjects.get(initialStateClass);
        synchronized (this) {
            this.invokeEntryMethod(this.currentState);
        }
    }
    
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        synchronized (this) {
            if ("getCurrentState".equals(method.getName()) && method.getParameterTypes().length == 0) {
                return this.currentState;
            }
            if (!"forceState".equals(method.getName()) || method.getParameterTypes().length != 1 || args.length != 1 || args[0] == null || !(args[0] instanceof Class)) {
                final Method signalMethod = this.getMethodOfCurrentState(method);
                StateMachineInvocationHandler.log.fine("Invoking signal method of current state: " + signalMethod.toString());
                final Object methodReturn = signalMethod.invoke(this.currentState, args);
                if (methodReturn != null && methodReturn instanceof Class) {
                    final Class nextStateClass = (Class)methodReturn;
                    if (this.stateObjects.containsKey(nextStateClass)) {
                        StateMachineInvocationHandler.log.fine("Executing transition to next state: " + nextStateClass.getName());
                        this.invokeExitMethod(this.currentState);
                        this.invokeEntryMethod(this.currentState = this.stateObjects.get(nextStateClass));
                    }
                }
                return methodReturn;
            }
            final Object forcedState = this.stateObjects.get(args[0]);
            if (forcedState == null) {
                throw new TransitionException("Can't force to invalid state: " + args[0]);
            }
            StateMachineInvocationHandler.log.finer("Forcing state machine into state: " + forcedState.getClass().getName());
            this.invokeExitMethod(this.currentState);
            this.invokeEntryMethod(this.currentState = forcedState);
            return null;
        }
    }
    
    private Method getMethodOfCurrentState(final Method method) {
        try {
            return this.currentState.getClass().getMethod(method.getName(), method.getParameterTypes());
        }
        catch (NoSuchMethodException ex) {
            throw new TransitionException("State '" + this.currentState.getClass().getName() + "' doesn't support signal '" + method.getName() + "'");
        }
    }
    
    private void invokeEntryMethod(final Object state) {
        StateMachineInvocationHandler.log.fine("Trying to invoke entry method of state: " + state.getClass().getName());
        try {
            final Method onEntryMethod = state.getClass().getMethod("onEntry", (Class<?>[])new Class[0]);
            onEntryMethod.invoke(state, new Object[0]);
        }
        catch (NoSuchMethodException ex2) {
            StateMachineInvocationHandler.log.finer("No entry method found on state: " + state.getClass().getName());
        }
        catch (Exception ex) {
            throw new TransitionException("State '" + state.getClass().getName() + "' entry method threw exception: " + ex, ex);
        }
    }
    
    private void invokeExitMethod(final Object state) {
        StateMachineInvocationHandler.log.finer("Trying to invoking exit method of state: " + state.getClass().getName());
        try {
            final Method onExitMethod = state.getClass().getMethod("onExit", (Class<?>[])new Class[0]);
            onExitMethod.invoke(state, new Object[0]);
        }
        catch (NoSuchMethodException ex2) {
            StateMachineInvocationHandler.log.finer("No exit method found on state: " + state.getClass().getName());
        }
        catch (Exception ex) {
            throw new TransitionException("State '" + state.getClass().getName() + "' exit method threw exception: " + ex, ex);
        }
    }
    
    static {
        StateMachineInvocationHandler.log = Logger.getLogger(StateMachineInvocationHandler.class.getName());
    }
}
