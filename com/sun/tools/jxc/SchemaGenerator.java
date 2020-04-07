// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc;

import com.sun.mirror.apt.AnnotationProcessorFactory;
import java.util.Iterator;
import java.net.MalformedURLException;
import java.net.URL;
import com.sun.xml.bind.util.Which;
import javax.xml.bind.JAXBContext;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.ArrayList;
import java.io.File;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.jxc.apt.Options;
import com.sun.tools.xjc.api.util.ToolsJarNotFoundException;
import com.sun.tools.xjc.api.util.APTClassLoader;

public class SchemaGenerator
{
    private static final String[] packagePrefixes;
    
    public static void main(final String[] args) throws Exception {
        System.exit(run(args));
    }
    
    public static int run(final String[] args) throws Exception {
        try {
            ClassLoader cl = SchemaGenerator.class.getClassLoader();
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
            final ClassLoader classLoader = new APTClassLoader(cl, SchemaGenerator.packagePrefixes);
            return run(args, classLoader);
        }
        catch (ToolsJarNotFoundException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }
    
    public static int run(final String[] args, final ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Options options = new Options();
        if (args.length == 0) {
            usage();
            return -1;
        }
        for (final String arg : args) {
            if (arg.equals("-help")) {
                usage();
                return -1;
            }
            if (arg.equals("-version")) {
                System.out.println(Messages.VERSION.format(new Object[0]));
                return -1;
            }
        }
        try {
            options.parseArguments(args);
        }
        catch (BadCommandLineException e) {
            System.out.println(e.getMessage());
            System.out.println();
            usage();
            return -1;
        }
        final Class schemagenRunner = classLoader.loadClass(Runner.class.getName());
        final Method mainMethod = schemagenRunner.getDeclaredMethod("main", String[].class, File.class);
        final List<String> aptargs = new ArrayList<String>();
        if (hasClass(options.arguments)) {
            aptargs.add("-XclassesAsDecls");
        }
        final File jaxbApi = findJaxbApiJar();
        if (jaxbApi != null) {
            if (options.classpath != null) {
                options.classpath = options.classpath + File.pathSeparatorChar + jaxbApi;
            }
            else {
                options.classpath = jaxbApi.getPath();
            }
        }
        aptargs.add("-cp");
        aptargs.add(options.classpath);
        if (options.targetDir != null) {
            aptargs.add("-d");
            aptargs.add(options.targetDir.getPath());
        }
        aptargs.addAll(options.arguments);
        final String[] argsarray = aptargs.toArray(new String[aptargs.size()]);
        return (int)mainMethod.invoke(null, argsarray, options.episodeFile);
    }
    
    private static File findJaxbApiJar() {
        final String url = Which.which(JAXBContext.class);
        if (url == null) {
            return null;
        }
        if (!url.startsWith("jar:") || url.lastIndexOf(33) == -1) {
            return null;
        }
        final String jarFileUrl = url.substring(4, url.lastIndexOf(33));
        if (!jarFileUrl.startsWith("file:")) {
            return null;
        }
        try {
            final File f = new File(new URL(jarFileUrl).getFile());
            if (f.exists() && f.getName().endsWith(".jar")) {
                return f;
            }
            return null;
        }
        catch (MalformedURLException e) {
            return null;
        }
    }
    
    private static boolean hasClass(final List<String> args) {
        for (final String arg : args) {
            if (!arg.endsWith(".java")) {
                return true;
            }
        }
        return false;
    }
    
    private static void usage() {
        System.out.println(Messages.USAGE.format(new Object[0]));
    }
    
    static {
        packagePrefixes = new String[] { "com.sun.tools.jxc.", "com.sun.tools.xjc.", "com.sun.istack.tools.", "com.sun.tools.apt.", "com.sun.tools.javac.", "com.sun.tools.javadoc.", "com.sun.mirror." };
    }
    
    public static final class Runner
    {
        public static int main(final String[] args, final File episode) throws Exception {
            final ClassLoader cl = Runner.class.getClassLoader();
            final Class apt = cl.loadClass("com.sun.tools.apt.Main");
            final Method processMethod = apt.getMethod("process", AnnotationProcessorFactory.class, String[].class);
            final com.sun.tools.jxc.apt.SchemaGenerator r = new com.sun.tools.jxc.apt.SchemaGenerator();
            if (episode != null) {
                r.setEpisodeFile(episode);
            }
            return (int)processMethod.invoke(null, r, args);
        }
    }
}
