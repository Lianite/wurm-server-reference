// 
// Decompiled by Procyon v0.5.30
// 

package org.controlsfx.control.action;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import javafx.event.ActionEvent;
import java.util.Objects;
import java.util.Map;

public class ActionMap
{
    private static AnnotatedActionFactory actionFactory;
    private static final Map<String, AnnotatedAction> actions;
    
    public static AnnotatedActionFactory getActionFactory() {
        return ActionMap.actionFactory;
    }
    
    public static void setActionFactory(final AnnotatedActionFactory factory) {
        Objects.requireNonNull(factory);
        ActionMap.actionFactory = factory;
    }
    
    public static void register(final Object target) {
        for (final Method method : target.getClass().getDeclaredMethods()) {
            final Annotation[] annotations = method.getAnnotationsByType(ActionProxy.class);
            if (annotations.length != 0) {
                final int paramCount = method.getParameterCount();
                final Class[] paramTypes = method.getParameterTypes();
                if (paramCount > 2) {
                    throw new IllegalArgumentException(String.format("Method %s has too many parameters", method.getName()));
                }
                if (paramCount == 1 && !ActionEvent.class.isAssignableFrom(paramTypes[0])) {
                    throw new IllegalArgumentException(String.format("Method %s -- single parameter must be of type ActionEvent", method.getName()));
                }
                if (paramCount == 2 && (!ActionEvent.class.isAssignableFrom(paramTypes[0]) || !Action.class.isAssignableFrom(paramTypes[1]))) {
                    throw new IllegalArgumentException(String.format("Method %s -- parameters must be of types (ActionEvent, Action)", method.getName()));
                }
                final ActionProxy annotation = (ActionProxy)annotations[0];
                final AnnotatedActionFactory factory = determineActionFactory(annotation);
                final AnnotatedAction action = factory.createAction(annotation, method, target);
                final String id = annotation.id().isEmpty() ? method.getName() : annotation.id();
                ActionMap.actions.put(id, action);
            }
        }
    }
    
    private static AnnotatedActionFactory determineActionFactory(final ActionProxy annotation) {
        AnnotatedActionFactory factory = ActionMap.actionFactory;
        final String factoryClassName = annotation.factory();
        if (!factoryClassName.isEmpty()) {
            try {
                final Class factoryClass = Class.forName(factoryClassName);
                factory = factoryClass.newInstance();
            }
            catch (ClassNotFoundException ex) {
                throw new IllegalArgumentException(String.format("Action proxy refers to non-existant factory class %s", factoryClassName), ex);
            }
            catch (InstantiationException | IllegalAccessException ex4) {
                final ReflectiveOperationException ex3;
                final ReflectiveOperationException ex2 = ex3;
                throw new IllegalStateException(String.format("Unable to instantiate action factory class %s", factoryClassName), ex2);
            }
        }
        return factory;
    }
    
    public static void unregister(final Object target) {
        if (target != null) {
            final Iterator<Map.Entry<String, AnnotatedAction>> entryIter = ActionMap.actions.entrySet().iterator();
            while (entryIter.hasNext()) {
                final Map.Entry<String, AnnotatedAction> entry = entryIter.next();
                final Object actionTarget = entry.getValue().getTarget();
                if (actionTarget == null || actionTarget == target) {
                    entryIter.remove();
                }
            }
        }
    }
    
    public static Action action(final String id) {
        return ActionMap.actions.get(id);
    }
    
    public static Collection<Action> actions(final String... ids) {
        final List<Action> result = new ArrayList<Action>();
        for (final String id : ids) {
            if (id.startsWith("---")) {
                result.add(ActionUtils.ACTION_SEPARATOR);
            }
            final Action action = action(id);
            if (action != null) {
                result.add(action);
            }
        }
        return result;
    }
    
    static {
        ActionMap.actionFactory = new DefaultActionFactory();
        actions = new HashMap<String, AnnotatedAction>();
    }
}
