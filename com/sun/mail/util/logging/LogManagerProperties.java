// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.util.logging;

import java.io.ObjectStreamException;
import java.util.Map;
import java.util.Enumeration;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.InvocationTargetException;
import javax.mail.Authenticator;
import java.util.logging.ErrorManager;
import java.util.Comparator;
import java.util.logging.Formatter;
import java.util.logging.Filter;
import java.util.Locale;
import java.util.logging.LogManager;
import java.util.Properties;

final class LogManagerProperties extends Properties
{
    private static final long serialVersionUID = -2239983349056806252L;
    private static final LogManager LOG_MANAGER;
    private final String prefix;
    static /* synthetic */ Class class$java$util$logging$Filter;
    static /* synthetic */ Class class$java$util$logging$Formatter;
    static /* synthetic */ Class class$java$util$Comparator;
    static /* synthetic */ Class class$java$util$logging$ErrorManager;
    static /* synthetic */ Class class$javax$mail$Authenticator;
    static /* synthetic */ Class class$com$sun$mail$util$logging$LogManagerProperties;
    
    static LogManager getLogManager() {
        return LogManagerProperties.LOG_MANAGER;
    }
    
    static String toLanguageTag(final Locale locale) {
        final String l = locale.getLanguage();
        final String c = locale.getCountry();
        final String v = locale.getVariant();
        final char[] b = new char[l.length() + c.length() + v.length() + 2];
        int count = l.length();
        l.getChars(0, count, b, 0);
        if (c.length() != 0 || (l.length() != 0 && v.length() != 0)) {
            b[count] = '-';
            ++count;
            c.getChars(0, c.length(), b, count);
            count += c.length();
        }
        if (v.length() != 0 && (l.length() != 0 || c.length() != 0)) {
            b[count] = '-';
            ++count;
            v.getChars(0, v.length(), b, count);
            count += v.length();
        }
        return String.valueOf(b, 0, count);
    }
    
    static Filter newFilter(final String name) throws Exception {
        return (Filter)newObjectFrom(name, (LogManagerProperties.class$java$util$logging$Filter == null) ? (LogManagerProperties.class$java$util$logging$Filter = class$("java.util.logging.Filter")) : LogManagerProperties.class$java$util$logging$Filter);
    }
    
    static Formatter newFormatter(final String name) throws Exception {
        return (Formatter)newObjectFrom(name, (LogManagerProperties.class$java$util$logging$Formatter == null) ? (LogManagerProperties.class$java$util$logging$Formatter = class$("java.util.logging.Formatter")) : LogManagerProperties.class$java$util$logging$Formatter);
    }
    
    static Comparator newComparator(final String name) throws Exception {
        return (Comparator)newObjectFrom(name, (LogManagerProperties.class$java$util$Comparator == null) ? (LogManagerProperties.class$java$util$Comparator = class$("java.util.Comparator")) : LogManagerProperties.class$java$util$Comparator);
    }
    
    static ErrorManager newErrorManager(final String name) throws Exception {
        return (ErrorManager)newObjectFrom(name, (LogManagerProperties.class$java$util$logging$ErrorManager == null) ? (LogManagerProperties.class$java$util$logging$ErrorManager = class$("java.util.logging.ErrorManager")) : LogManagerProperties.class$java$util$logging$ErrorManager);
    }
    
    static Authenticator newAuthenticator(final String name) throws Exception {
        return (Authenticator)newObjectFrom(name, (LogManagerProperties.class$javax$mail$Authenticator == null) ? (LogManagerProperties.class$javax$mail$Authenticator = class$("javax.mail.Authenticator")) : LogManagerProperties.class$javax$mail$Authenticator);
    }
    
    private static Object newObjectFrom(final String name, final Class type) throws Exception {
        try {
            final Class clazz = findClass(name);
            if (type.isAssignableFrom(clazz)) {
                try {
                    return clazz.getConstructor((Class<?>[])null).newInstance((Object[])null);
                }
                catch (InvocationTargetException ITE) {
                    throw paramOrError(ITE);
                }
            }
            throw new ClassCastException(clazz.getName() + " cannot be cast to " + type.getName());
        }
        catch (NoClassDefFoundError NCDFE) {
            throw new ClassNotFoundException(NCDFE.toString(), NCDFE);
        }
        catch (ExceptionInInitializerError EIIE) {
            if (EIIE.getCause() instanceof Error) {
                throw EIIE;
            }
            throw new InvocationTargetException(EIIE);
        }
    }
    
    private static Exception paramOrError(final InvocationTargetException ite) {
        final Throwable cause = ite.getCause();
        if (cause != null && (cause instanceof VirtualMachineError || cause instanceof ThreadDeath)) {
            throw (Error)cause;
        }
        return ite;
    }
    
    private static Class findClass(final String name) throws ClassNotFoundException {
        final ClassLoader[] loaders = getClassLoaders();
        assert loaders.length == 2 : loaders.length;
        Class clazz;
        if (loaders[0] != null) {
            try {
                clazz = Class.forName(name, false, loaders[0]);
            }
            catch (ClassNotFoundException tryContext) {
                clazz = tryLoad(name, loaders[1]);
            }
        }
        else {
            clazz = tryLoad(name, loaders[1]);
        }
        return clazz;
    }
    
    private static Class tryLoad(final String name, final ClassLoader l) throws ClassNotFoundException {
        if (l != null) {
            return Class.forName(name, false, l);
        }
        return Class.forName(name);
    }
    
    private static ClassLoader[] getClassLoaders() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader[]>)new PrivilegedAction() {
            public Object run() {
                final ClassLoader[] loaders = new ClassLoader[2];
                try {
                    loaders[0] = ClassLoader.getSystemClassLoader();
                }
                catch (SecurityException ignore) {
                    loaders[0] = null;
                }
                try {
                    loaders[1] = Thread.currentThread().getContextClassLoader();
                }
                catch (SecurityException ignore) {
                    loaders[1] = null;
                }
                return loaders;
            }
        });
    }
    
    LogManagerProperties(final Properties parent, final String prefix) {
        super(parent);
        parent.isEmpty();
        if (prefix == null) {
            throw new NullPointerException();
        }
        this.prefix = prefix;
        super.isEmpty();
    }
    
    public synchronized Object clone() {
        return this.exportCopy(this.defaults);
    }
    
    public synchronized String getProperty(final String key) {
        String value = this.defaults.getProperty(key);
        if (value == null) {
            final LogManager manager = getLogManager();
            if (key.length() > 0) {
                value = manager.getProperty(this.prefix + '.' + key);
            }
            if (value == null) {
                value = manager.getProperty(key);
            }
            if (value != null) {
                super.put(key, value);
            }
            else {
                final Object v = super.get(key);
                value = ((v instanceof String) ? ((String)v) : null);
            }
        }
        return value;
    }
    
    public String getProperty(final String key, final String def) {
        final String value = this.getProperty(key);
        return (value == null) ? def : value;
    }
    
    public Object get(final Object key) {
        if (key instanceof String) {
            return this.getProperty((String)key);
        }
        return super.get(key);
    }
    
    public synchronized Object put(final Object key, final Object value) {
        final Object def = this.preWrite(key);
        final Object man = super.put(key, value);
        return (man == null) ? def : man;
    }
    
    public Object setProperty(final String key, final String value) {
        return this.put(key, value);
    }
    
    public boolean containsKey(final Object key) {
        if (key instanceof String) {
            return this.getProperty((String)key) != null;
        }
        return super.containsKey(key);
    }
    
    public synchronized Object remove(final Object key) {
        final Object def = this.preWrite(key);
        final Object man = super.remove(key);
        return (man == null) ? def : man;
    }
    
    public Enumeration propertyNames() {
        assert false;
        return super.propertyNames();
    }
    
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Properties)) {
            return false;
        }
        assert false : this.prefix;
        return super.equals(o);
    }
    
    public int hashCode() {
        assert false : this.prefix.hashCode();
        return super.hashCode();
    }
    
    private Object preWrite(final Object key) {
        assert Thread.holdsLock(this);
        Object value;
        if (key instanceof String && !super.containsKey(key)) {
            value = this.getProperty((String)key);
        }
        else {
            value = null;
        }
        return value;
    }
    
    private Properties exportCopy(final Properties parent) {
        Thread.holdsLock(this);
        final Properties child = new Properties(parent);
        child.putAll(this);
        return child;
    }
    
    private synchronized Object writeReplace() throws ObjectStreamException {
        assert false;
        return this.exportCopy((Properties)this.defaults.clone());
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError().initCause(x);
        }
    }
    
    static {
        $assertionsDisabled = !((LogManagerProperties.class$com$sun$mail$util$logging$LogManagerProperties == null) ? (LogManagerProperties.class$com$sun$mail$util$logging$LogManagerProperties = class$("com.sun.mail.util.logging.LogManagerProperties")) : LogManagerProperties.class$com$sun$mail$util$logging$LogManagerProperties).desiredAssertionStatus();
        LOG_MANAGER = LogManager.getLogManager();
    }
}
