// 
// Decompiled by Procyon v0.5.30
// 

package org.fourthline.cling.support.shared;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class NewPlatformApple
{
    public static void setup(final ShutdownHandler shutdownHandler, final String appName) throws Exception {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
        System.setProperty("apple.awt.showGrowBox", "true");
        final Class appClass = Class.forName("com.apple.eawt.Application");
        final Object application = appClass.newInstance();
        final Class listenerClass = Class.forName("com.apple.eawt.ApplicationListener");
        final Method addAppListmethod = appClass.getDeclaredMethod("addApplicationListener", listenerClass);
        final Class adapterClass = Class.forName("com.apple.eawt.ApplicationAdapter");
        final Object listener = AppListenerProxy.newInstance(adapterClass.newInstance(), shutdownHandler);
        addAppListmethod.invoke(application, listener);
    }
    
    static class AppListenerProxy implements InvocationHandler
    {
        private ShutdownHandler shutdownHandler;
        private Object object;
        
        public static Object newInstance(final Object obj, final ShutdownHandler shutdownHandler) {
            return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), new AppListenerProxy(obj, shutdownHandler));
        }
        
        private AppListenerProxy(final Object obj, final ShutdownHandler shutdownHandler) {
            this.object = obj;
            this.shutdownHandler = shutdownHandler;
        }
        
        @Override
        public Object invoke(final Object proxy, final Method m, final Object[] args) throws Throwable {
            Object result = null;
            try {
                if ("handleQuit".equals(m.getName())) {
                    if (this.shutdownHandler != null) {
                        this.shutdownHandler.shutdown();
                    }
                }
                else {
                    result = m.invoke(this.object, args);
                }
            }
            catch (Exception ex) {}
            return result;
        }
    }
}
