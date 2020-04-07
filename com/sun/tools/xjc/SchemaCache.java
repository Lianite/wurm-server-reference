// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import org.xml.sax.SAXException;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.ValidatorHandler;
import java.net.URL;
import javax.xml.validation.Schema;

public final class SchemaCache
{
    private Schema schema;
    private final URL source;
    
    public SchemaCache(final URL source) {
        this.source = source;
    }
    
    public ValidatorHandler newValidator() {
        synchronized (this) {
            if (this.schema == null) {
                try {
                    this.schema = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema").newSchema(this.source);
                }
                catch (SAXException e) {
                    throw new AssertionError((Object)e);
                }
            }
        }
        final ValidatorHandler handler = this.schema.newValidatorHandler();
        this.fixValidatorBug6246922(handler);
        return handler;
    }
    
    private void fixValidatorBug6246922(final ValidatorHandler handler) {
        try {
            final Field f = handler.getClass().getDeclaredField("errorReporter");
            f.setAccessible(true);
            final Object errorReporter = f.get(handler);
            final Method get = errorReporter.getClass().getDeclaredMethod("getMessageFormatter", String.class);
            final Object currentFormatter = get.invoke(errorReporter, "http://www.w3.org/TR/xml-schema-1");
            if (currentFormatter != null) {
                return;
            }
            Method put = null;
            for (final Method m : errorReporter.getClass().getDeclaredMethods()) {
                if (m.getName().equals("putMessageFormatter")) {
                    put = m;
                    break;
                }
            }
            if (put == null) {
                return;
            }
            final ClassLoader cl = errorReporter.getClass().getClassLoader();
            final String className = "com.sun.org.apache.xerces.internal.impl.xs.XSMessageFormatter";
            Class xsformatter;
            if (cl == null) {
                xsformatter = Class.forName(className);
            }
            else {
                xsformatter = cl.loadClass(className);
            }
            put.invoke(errorReporter, "http://www.w3.org/TR/xml-schema-1", xsformatter.newInstance());
        }
        catch (Throwable t) {}
    }
}
