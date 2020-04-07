// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc;

import java.net.MalformedURLException;
import java.util.List;
import java.net.URLClassLoader;
import com.sun.istack.tools.ParallelWorldClassLoader;
import java.net.URL;
import com.sun.istack.tools.MaskingClassLoader;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;

class ClassLoaderBuilder
{
    private static String[] maskedPackages;
    private static String[] toolPackages;
    public static final boolean noHack;
    static /* synthetic */ Class class$javax$xml$bind$JAXBContext;
    static /* synthetic */ Class class$com$sun$tools$xjc$XJCFacade;
    
    protected static ClassLoader createProtectiveClassLoader(ClassLoader cl, final String v) throws ClassNotFoundException, MalformedURLException {
        if (ClassLoaderBuilder.noHack) {
            return cl;
        }
        boolean mustang = false;
        if (((ClassLoaderBuilder.class$javax$xml$bind$JAXBContext == null) ? (ClassLoaderBuilder.class$javax$xml$bind$JAXBContext = class$("javax.xml.bind.JAXBContext")) : ClassLoaderBuilder.class$javax$xml$bind$JAXBContext).getClassLoader() == null) {
            mustang = true;
            final List mask = new ArrayList(Arrays.asList(ClassLoaderBuilder.maskedPackages));
            mask.add("javax.xml.bind.");
            cl = new MaskingClassLoader(cl, mask);
            final URL apiUrl = cl.getResource("javax/xml/bind/annotation/XmlSeeAlso.class");
            if (apiUrl == null) {
                throw new ClassNotFoundException("There's no JAXB 2.1 API in the classpath");
            }
            cl = new URLClassLoader(new URL[] { ParallelWorldClassLoader.toJarUrl(apiUrl) }, cl);
        }
        if (v.equals("1.0")) {
            if (!mustang) {
                cl = new MaskingClassLoader(cl, ClassLoaderBuilder.toolPackages);
            }
            cl = new ParallelWorldClassLoader(cl, "1.0/");
        }
        else if (mustang) {
            cl = new ParallelWorldClassLoader(cl, "");
        }
        return cl;
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError(x.getMessage());
        }
    }
    
    static {
        ClassLoaderBuilder.maskedPackages = new String[] { "com.sun.tools.", "com.sun.codemodel.", "com.sun.relaxng.", "com.sun.xml.xsom.", "com.sun.xml.bind." };
        ClassLoaderBuilder.toolPackages = new String[] { "com.sun.tools.", "com.sun.codemodel.", "com.sun.relaxng.", "com.sun.xml.xsom." };
        noHack = Boolean.getBoolean(((ClassLoaderBuilder.class$com$sun$tools$xjc$XJCFacade == null) ? (ClassLoaderBuilder.class$com$sun$tools$xjc$XJCFacade = class$("com.sun.tools.xjc.XJCFacade")) : ClassLoaderBuilder.class$com$sun$tools$xjc$XJCFacade).getName() + ".nohack");
    }
}
