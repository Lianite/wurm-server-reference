// 
// Decompiled by Procyon v0.5.30
// 

package org.relaxng.datatype.helpers;

import java.util.NoSuchElementException;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;
import java.util.Enumeration;
import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;

public class DatatypeLibraryLoader implements DatatypeLibraryFactory
{
    private final Service service;
    static /* synthetic */ Class class$org$relaxng$datatype$DatatypeLibraryFactory;
    
    public DatatypeLibraryLoader() {
        this.service = new Service((DatatypeLibraryLoader.class$org$relaxng$datatype$DatatypeLibraryFactory == null) ? (DatatypeLibraryLoader.class$org$relaxng$datatype$DatatypeLibraryFactory = class$("org.relaxng.datatype.DatatypeLibraryFactory")) : DatatypeLibraryLoader.class$org$relaxng$datatype$DatatypeLibraryFactory);
    }
    
    public DatatypeLibrary createDatatypeLibrary(final String s) {
        final Enumeration providers = this.service.getProviders();
        while (providers.hasMoreElements()) {
            final DatatypeLibrary datatypeLibrary = providers.nextElement().createDatatypeLibrary(s);
            if (datatypeLibrary != null) {
                return datatypeLibrary;
            }
        }
        return null;
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    private static class Service
    {
        private final Class serviceClass;
        private final Enumeration configFiles;
        private Enumeration classNames;
        private final Vector providers;
        private Loader loader;
        private static final int START = 0;
        private static final int IN_NAME = 1;
        private static final int IN_COMMENT = 2;
        
        public Service(final Class serviceClass) {
            this.classNames = null;
            this.providers = new Vector();
            try {
                this.loader = new Loader2();
            }
            catch (NoSuchMethodError noSuchMethodError) {
                this.loader = new Loader();
            }
            this.serviceClass = serviceClass;
            this.configFiles = this.loader.getResources("META-INF/services/" + this.serviceClass.getName());
        }
        
        public Enumeration getProviders() {
            return new ProviderEnumeration();
        }
        
        private synchronized boolean moreProviders() {
            while (true) {
                if (this.classNames != null) {
                    while (this.classNames.hasMoreElements()) {
                        final String s = this.classNames.nextElement();
                        try {
                            final Object instance = this.loader.loadClass(s).newInstance();
                            if (this.serviceClass.isInstance(instance)) {
                                this.providers.addElement(instance);
                                return true;
                            }
                            continue;
                        }
                        catch (ClassNotFoundException ex) {}
                        catch (InstantiationException ex2) {}
                        catch (IllegalAccessException ex3) {}
                        catch (LinkageError linkageError) {}
                    }
                    this.classNames = null;
                }
                else {
                    if (!this.configFiles.hasMoreElements()) {
                        break;
                    }
                    this.classNames = parseConfigFile(this.configFiles.nextElement());
                }
            }
            return false;
        }
        
        private static Enumeration parseConfigFile(final URL url) {
            try {
                final InputStream openStream = url.openStream();
                InputStreamReader inputStreamReader;
                try {
                    inputStreamReader = new InputStreamReader(openStream, "UTF-8");
                }
                catch (UnsupportedEncodingException ex) {
                    inputStreamReader = new InputStreamReader(openStream, "UTF8");
                }
                final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                final Vector<String> vector = new Vector<String>();
                final StringBuffer sb = new StringBuffer();
                int n = 0;
                while (true) {
                    final int read = bufferedReader.read();
                    if (read < 0) {
                        break;
                    }
                    final char c = (char)read;
                    switch (c) {
                        case 10:
                        case 13: {
                            n = 0;
                            break;
                        }
                        case 9:
                        case 32: {
                            break;
                        }
                        case 35: {
                            n = 2;
                            break;
                        }
                        default: {
                            if (n != 2) {
                                n = 1;
                                sb.append(c);
                                break;
                            }
                            break;
                        }
                    }
                    if (sb.length() == 0 || n == 1) {
                        continue;
                    }
                    vector.addElement(sb.toString());
                    sb.setLength(0);
                }
                if (sb.length() != 0) {
                    vector.addElement(sb.toString());
                }
                return vector.elements();
            }
            catch (IOException ex2) {
                return null;
            }
        }
        
        private static class Loader2 extends Loader
        {
            private ClassLoader cl;
            static /* synthetic */ Class class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader2;
            
            Loader2() {
                this.cl = ((Loader2.class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader2 == null) ? (Loader2.class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader2 = class$("org.relaxng.datatype.helpers.DatatypeLibraryLoader$Service$Loader2")) : Loader2.class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader2).getClassLoader();
                ClassLoader classLoader;
                for (ClassLoader cl = classLoader = Thread.currentThread().getContextClassLoader(); classLoader != null; classLoader = classLoader.getParent()) {
                    if (classLoader == this.cl) {
                        this.cl = cl;
                        break;
                    }
                }
            }
            
            Enumeration getResources(final String s) {
                try {
                    return this.cl.getResources(s);
                }
                catch (IOException ex) {
                    return new Singleton((Object)null);
                }
            }
            
            Class loadClass(final String s) throws ClassNotFoundException {
                return Class.forName(s, true, this.cl);
            }
            
            static /* synthetic */ Class class$(final String s) {
                try {
                    return Class.forName(s);
                }
                catch (ClassNotFoundException ex) {
                    throw new NoClassDefFoundError(ex.getMessage());
                }
            }
        }
        
        private static class Singleton implements Enumeration
        {
            private Object obj;
            
            private Singleton(final Object obj) {
                this.obj = obj;
            }
            
            public boolean hasMoreElements() {
                return this.obj != null;
            }
            
            public Object nextElement() {
                if (this.obj == null) {
                    throw new NoSuchElementException();
                }
                final Object obj = this.obj;
                this.obj = null;
                return obj;
            }
        }
        
        private static class Loader
        {
            static /* synthetic */ Class class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader;
            
            Enumeration getResources(final String s) {
                final ClassLoader classLoader = ((Loader.class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader == null) ? (Loader.class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader = class$("org.relaxng.datatype.helpers.DatatypeLibraryLoader$Service$Loader")) : Loader.class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader).getClassLoader();
                URL url;
                if (classLoader == null) {
                    url = ClassLoader.getSystemResource(s);
                }
                else {
                    url = classLoader.getResource(s);
                }
                return new Singleton((Object)url);
            }
            
            Class loadClass(final String s) throws ClassNotFoundException {
                return Class.forName(s);
            }
            
            static /* synthetic */ Class class$(final String s) {
                try {
                    return Class.forName(s);
                }
                catch (ClassNotFoundException ex) {
                    throw new NoClassDefFoundError(ex.getMessage());
                }
            }
        }
        
        private class ProviderEnumeration implements Enumeration
        {
            private int nextIndex;
            
            private ProviderEnumeration() {
                this.nextIndex = 0;
            }
            
            public boolean hasMoreElements() {
                return this.nextIndex < Service.this.providers.size() || Service.this.moreProviders();
            }
            
            public Object nextElement() {
                try {
                    return Service.this.providers.elementAt(this.nextIndex++);
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    throw new NoSuchElementException();
                }
            }
        }
    }
}
