// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.istack.tools;

import java.util.Iterator;
import org.apache.tools.ant.IntrospectionHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicConfigurator;
import org.apache.tools.ant.Task;

public abstract class ProtectedTask extends Task implements DynamicConfigurator
{
    private final AntElement root;
    
    public ProtectedTask() {
        this.root = new AntElement("root");
    }
    
    public void setDynamicAttribute(final String name, final String value) throws BuildException {
        this.root.setDynamicAttribute(name, value);
    }
    
    public Object createDynamicElement(final String name) throws BuildException {
        return this.root.createDynamicElement(name);
    }
    
    public void execute() throws BuildException {
        final ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        try {
            final ClassLoader cl = this.createClassLoader();
            final Class driver = cl.loadClass(this.getCoreClassName());
            final Task t = driver.newInstance();
            t.setProject(this.getProject());
            t.setTaskName(this.getTaskName());
            this.root.configure(t);
            Thread.currentThread().setContextClassLoader(cl);
            t.execute();
        }
        catch (UnsupportedClassVersionError e5) {
            throw new BuildException("Requires JDK 5.0 or later. Please download it from http://java.sun.com/j2se/1.5/");
        }
        catch (ClassNotFoundException e) {
            throw new BuildException((Throwable)e);
        }
        catch (InstantiationException e2) {
            throw new BuildException((Throwable)e2);
        }
        catch (IllegalAccessException e3) {
            throw new BuildException((Throwable)e3);
        }
        catch (IOException e4) {
            throw new BuildException((Throwable)e4);
        }
        finally {
            Thread.currentThread().setContextClassLoader(ccl);
        }
    }
    
    protected abstract String getCoreClassName();
    
    protected abstract ClassLoader createClassLoader() throws ClassNotFoundException, IOException;
    
    private class AntElement implements DynamicConfigurator
    {
        private final String name;
        private final Map attributes;
        private final List elements;
        
        public AntElement(final String name) {
            this.attributes = new HashMap();
            this.elements = new ArrayList();
            this.name = name;
        }
        
        public void setDynamicAttribute(final String name, final String value) throws BuildException {
            this.attributes.put(name, value);
        }
        
        public Object createDynamicElement(final String name) throws BuildException {
            final AntElement e = new AntElement(name);
            this.elements.add(e);
            return e;
        }
        
        public void configure(final Object antObject) {
            final IntrospectionHelper ih = IntrospectionHelper.getHelper((Class)antObject.getClass());
            for (final Map.Entry att : this.attributes.entrySet()) {
                ih.setAttribute(ProtectedTask.this.getProject(), antObject, (String)att.getKey(), (String)att.getValue());
            }
            for (final AntElement e : this.elements) {
                final Object child = ih.createElement(ProtectedTask.this.getProject(), antObject, e.name);
                e.configure(child);
                ih.storeElement(ProtectedTask.this.getProject(), antObject, child, e.name);
            }
        }
    }
}
