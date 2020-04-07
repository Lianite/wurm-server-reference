// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.org.apache.xml.internal.resolver.helpers;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class FileURL
{
    public static URL makeURL(final String pathname) throws MalformedURLException {
        if (pathname.startsWith("/")) {
            return new URL("file://" + pathname);
        }
        final String userdir = System.getProperty("user.dir");
        userdir.replace('\\', '/');
        if (userdir.endsWith("/")) {
            return new URL("file:///" + userdir + pathname);
        }
        return new URL("file:///" + userdir + "/" + pathname);
    }
}
