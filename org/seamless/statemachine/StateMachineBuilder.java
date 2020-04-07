// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.statemachine;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class StateMachineBuilder
{
    public static <T extends StateMachine> T build(final Class<T> stateMachine, final Class initialState) {
        return build(stateMachine, initialState, null, null);
    }
    
    public static <T extends StateMachine> T build(final Class<T> stateMachine, final Class initialState, final Class[] constructorArgumentTypes, final Object[] constructorArguments) {
        return (T)Proxy.newProxyInstance(stateMachine.getClassLoader(), new Class[] { stateMachine }, new StateMachineInvocationHandler(Arrays.asList(stateMachine.getAnnotation(States.class).value()), initialState, constructorArgumentTypes, constructorArguments));
    }
}
