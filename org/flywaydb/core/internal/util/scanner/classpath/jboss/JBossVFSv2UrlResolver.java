// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.util.scanner.classpath.jboss;

import java.io.IOException;
import java.lang.reflect.Method;
import org.flywaydb.core.api.FlywayException;
import java.net.URL;
import org.flywaydb.core.internal.util.scanner.classpath.UrlResolver;

public class JBossVFSv2UrlResolver implements UrlResolver
{
    @Override
    public URL toStandardJavaUrl(final URL url) throws IOException {
        try {
            final Class<?> vfsClass = Class.forName("org.jboss.virtual.VFS");
            final Class<?> vfsUtilsClass = Class.forName("org.jboss.virtual.VFSUtils");
            final Class<?> virtualFileClass = Class.forName("org.jboss.virtual.VirtualFile");
            final Method getRootMethod = vfsClass.getMethod("getRoot", URL.class);
            final Method getRealURLMethod = vfsUtilsClass.getMethod("getRealURL", virtualFileClass);
            final Object root = getRootMethod.invoke(null, url);
            return (URL)getRealURLMethod.invoke(null, root);
        }
        catch (Exception e) {
            throw new FlywayException("JBoss VFS v2 call failed", e);
        }
    }
}
