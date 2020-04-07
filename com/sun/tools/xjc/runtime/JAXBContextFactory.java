// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.runtime;

import java.util.List;
import java.util.StringTokenizer;
import java.util.ArrayList;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import java.util.Map;

public class JAXBContextFactory
{
    private static final String DOT_OBJECT_FACTORY = ".ObjectFactory";
    private static final String IMPL_DOT_OBJECT_FACTORY = ".impl.ObjectFactory";
    
    public static JAXBContext createContext(final Class[] classes, final Map properties) throws JAXBException {
        final Class[] r = new Class[classes.length];
        boolean modified = false;
        for (int i = 0; i < r.length; ++i) {
            Class c = classes[i];
            String name = c.getName();
            if (name.endsWith(".ObjectFactory") && !name.endsWith(".impl.ObjectFactory")) {
                name = name.substring(0, name.length() - ".ObjectFactory".length()) + ".impl.ObjectFactory";
                try {
                    c = c.getClassLoader().loadClass(name);
                }
                catch (ClassNotFoundException e) {
                    throw new JAXBException(e);
                }
                modified = true;
            }
            r[i] = c;
        }
        if (!modified) {
            throw new JAXBException("Unable to find a JAXB implementation to delegate");
        }
        return JAXBContext.newInstance(r, properties);
    }
    
    public static JAXBContext createContext(final String contextPath, final ClassLoader classLoader, final Map properties) throws JAXBException {
        final List<Class> classes = new ArrayList<Class>();
        final StringTokenizer tokens = new StringTokenizer(contextPath, ":");
        try {
            while (tokens.hasMoreTokens()) {
                final String pkg = tokens.nextToken();
                classes.add(classLoader.loadClass(pkg + ".impl.ObjectFactory"));
            }
        }
        catch (ClassNotFoundException e) {
            throw new JAXBException(e);
        }
        return JAXBContext.newInstance(classes.toArray(new Class[classes.size()]), properties);
    }
}
