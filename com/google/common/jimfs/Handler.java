// 
// Decompiled by Procyon v0.5.30
// 

package com.google.common.jimfs;

import java.io.IOException;
import java.net.URLConnection;
import java.net.URL;
import com.google.common.base.Preconditions;
import java.net.URLStreamHandler;

public final class Handler extends URLStreamHandler
{
    private static final String JAVA_PROTOCOL_HANDLER_PACKAGES = "java.protocol.handler.pkgs";
    
    static void register() {
        register(Handler.class);
    }
    
    static void register(final Class<? extends URLStreamHandler> handlerClass) {
        Preconditions.checkArgument("Handler".equals(handlerClass.getSimpleName()));
        final String pkg = handlerClass.getPackage().getName();
        final int lastDot = pkg.lastIndexOf(46);
        Preconditions.checkArgument(lastDot > 0, "package for Handler (%s) must have a parent package", pkg);
        final String parentPackage = pkg.substring(0, lastDot);
        String packages = System.getProperty("java.protocol.handler.pkgs");
        if (packages == null) {
            packages = parentPackage;
        }
        else {
            packages = packages + "|" + parentPackage;
        }
        System.setProperty("java.protocol.handler.pkgs", packages);
    }
    
    @Override
    protected URLConnection openConnection(final URL url) throws IOException {
        return new PathURLConnection(url);
    }
}
