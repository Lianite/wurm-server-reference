// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc;

import org.xml.sax.SAXParseException;
import java.io.OutputStream;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.writer.FilterCodeWriter;
import com.sun.tools.xjc.reader.Util;
import org.apache.tools.ant.DirectoryScanner;
import com.sun.tools.xjc.model.Model;
import java.util.Iterator;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import org.xml.sax.EntityResolver;
import com.sun.tools.xjc.util.ForkEntityResolver;
import org.apache.tools.ant.AntClassLoader;
import java.lang.reflect.Constructor;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import com.sun.tools.xjc.api.SpecVersion;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import com.sun.xml.bind.v2.util.EditDistance;
import org.apache.tools.ant.types.Reference;
import org.xml.sax.InputSource;
import java.util.List;
import org.apache.tools.ant.types.FileSet;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.XMLCatalog;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import java.io.File;
import java.util.ArrayList;
import org.apache.tools.ant.Task;

public class XJC2Task extends Task
{
    public final Options options;
    private long stackSize;
    private boolean failonerror;
    private boolean removeOldOutput;
    private final ArrayList<File> dependsSet;
    private final ArrayList<File> producesSet;
    private boolean producesSpecified;
    private final Path classpath;
    private final Commandline cmdLine;
    private XMLCatalog xmlCatalog;
    
    public XJC2Task() {
        this.options = new Options();
        this.stackSize = -1L;
        this.failonerror = true;
        this.removeOldOutput = false;
        this.dependsSet = new ArrayList<File>();
        this.producesSet = new ArrayList<File>();
        this.producesSpecified = false;
        this.cmdLine = new Commandline();
        this.xmlCatalog = null;
        this.classpath = new Path((Project)null);
        this.options.setSchemaLanguage(Language.XMLSCHEMA);
    }
    
    public void setSchema(final String schema) {
        try {
            this.options.addGrammar(this.getInputSource(new URL(schema)));
        }
        catch (MalformedURLException e) {
            final File f = this.getProject().resolveFile(schema);
            this.options.addGrammar(f);
            this.dependsSet.add(f);
        }
    }
    
    public void addConfiguredSchema(final FileSet fs) {
        for (final InputSource value : this.toInputSources(fs)) {
            this.options.addGrammar(value);
        }
        this.addIndividualFilesTo(fs, this.dependsSet);
    }
    
    public void setClasspath(final Path cp) {
        this.classpath.createPath().append(cp);
    }
    
    public Path createClasspath() {
        return this.classpath.createPath();
    }
    
    public void setClasspathRef(final Reference r) {
        this.classpath.createPath().setRefid(r);
    }
    
    public void setLanguage(final String language) {
        final Language l = Language.valueOf(language.toUpperCase());
        if (l == null) {
            final Language[] languages = Language.values();
            final String[] candidates = new String[languages.length];
            for (int i = 0; i < candidates.length; ++i) {
                candidates[i] = languages[i].name();
            }
            throw new BuildException("Unrecognized language: " + language + ". Did you mean " + EditDistance.findNearest(language.toUpperCase(), candidates) + " ?");
        }
        this.options.setSchemaLanguage(l);
    }
    
    public void setBinding(final String binding) {
        try {
            this.options.addBindFile(this.getInputSource(new URL(binding)));
        }
        catch (MalformedURLException e) {
            final File f = this.getProject().resolveFile(binding);
            this.options.addBindFile(f);
            this.dependsSet.add(f);
        }
    }
    
    public void addConfiguredBinding(final FileSet fs) {
        for (final InputSource is : this.toInputSources(fs)) {
            this.options.addBindFile(is);
        }
        this.addIndividualFilesTo(fs, this.dependsSet);
    }
    
    public void setPackage(final String pkg) {
        this.options.defaultPackage = pkg;
    }
    
    public void setCatalog(final File catalog) {
        try {
            this.options.addCatalog(catalog);
        }
        catch (IOException e) {
            throw new BuildException((Throwable)e);
        }
    }
    
    public void setFailonerror(final boolean value) {
        this.failonerror = value;
    }
    
    public void setStackSize(final String ss) {
        try {
            this.stackSize = Long.parseLong(ss);
        }
        catch (NumberFormatException e) {
            if (ss.length() > 2) {
                final String head = ss.substring(0, ss.length() - 2);
                final String tail = ss.substring(ss.length() - 2);
                if (tail.equalsIgnoreCase("kb")) {
                    try {
                        this.stackSize = Long.parseLong(head) * 1024L;
                        return;
                    }
                    catch (NumberFormatException ex) {}
                }
                if (tail.equalsIgnoreCase("mb")) {
                    try {
                        this.stackSize = Long.parseLong(head) * 1024L * 1024L;
                        return;
                    }
                    catch (NumberFormatException ex2) {}
                }
            }
            throw new BuildException("Unrecognizable stack size: " + ss);
        }
    }
    
    public void addConfiguredXMLCatalog(final XMLCatalog xmlCatalog) {
        if (this.xmlCatalog == null) {
            (this.xmlCatalog = new XMLCatalog()).setProject(this.getProject());
        }
        this.xmlCatalog.addConfiguredXMLCatalog(xmlCatalog);
    }
    
    public void setReadonly(final boolean flg) {
        this.options.readOnly = flg;
    }
    
    public void setHeader(final boolean flg) {
        this.options.noFileHeader = !flg;
    }
    
    public void setXexplicitAnnotation(final boolean flg) {
        this.options.runtime14 = flg;
    }
    
    public void setExtension(final boolean flg) {
        if (flg) {
            this.options.compatibilityMode = 2;
        }
        else {
            this.options.compatibilityMode = 1;
        }
    }
    
    public void setTarget(final String version) {
        this.options.target = SpecVersion.parse(version);
        if (this.options.target == null) {
            throw new BuildException(version + " is not a valid version number. Perhaps you meant @destdir?");
        }
    }
    
    public void setDestdir(final File dir) {
        this.options.targetDir = dir;
    }
    
    public void addConfiguredDepends(final FileSet fs) {
        this.addIndividualFilesTo(fs, this.dependsSet);
    }
    
    public void addConfiguredProduces(final FileSet fs) {
        this.producesSpecified = true;
        if (!fs.getDir(this.getProject()).exists()) {
            this.log(fs.getDir(this.getProject()).getAbsolutePath() + " is not found and thus excluded from the dependency check", 2);
        }
        else {
            this.addIndividualFilesTo(fs, this.producesSet);
        }
    }
    
    public void setRemoveOldOutput(final boolean roo) {
        this.removeOldOutput = roo;
    }
    
    public Commandline.Argument createArg() {
        return this.cmdLine.createArgument();
    }
    
    public void execute() throws BuildException {
        this.log("build id of XJC is " + Driver.getBuildID(), 3);
        this.classpath.setProject(this.getProject());
        try {
            if (this.stackSize == -1L) {
                this.doXJC();
            }
            else {
                try {
                    final Throwable[] e = { null };
                    final Runnable job = new Runnable() {
                        public void run() {
                            try {
                                XJC2Task.this.doXJC();
                            }
                            catch (Throwable be) {
                                e[0] = be;
                            }
                        }
                    };
                    Thread t;
                    try {
                        final Constructor c = Thread.class.getConstructor(ThreadGroup.class, Runnable.class, String.class, Long.TYPE);
                        t = c.newInstance(Thread.currentThread().getThreadGroup(), job, Thread.currentThread().getName() + ":XJC", this.stackSize);
                    }
                    catch (Throwable err) {
                        this.log("Unable to set the stack size. Use JDK1.4 or above", 1);
                        this.doXJC();
                        return;
                    }
                    t.start();
                    t.join();
                    if (e[0] instanceof Error) {
                        throw (Error)e[0];
                    }
                    if (e[0] instanceof RuntimeException) {
                        throw (RuntimeException)e[0];
                    }
                    if (e[0] instanceof BuildException) {
                        throw (BuildException)e[0];
                    }
                    if (e[0] != null) {
                        throw new BuildException(e[0]);
                    }
                }
                catch (InterruptedException e2) {
                    throw new BuildException((Throwable)e2);
                }
            }
        }
        catch (BuildException e3) {
            this.log("failure in the XJC task. Use the Ant -verbose switch for more details");
            if (this.failonerror) {
                throw e3;
            }
            final StringWriter sw = new StringWriter();
            e3.printStackTrace(new PrintWriter(sw));
            this.getProject().log(sw.toString(), 1);
        }
    }
    
    private void doXJC() throws BuildException {
        final ClassLoader old = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader((ClassLoader)new AntClassLoader(this.getProject(), this.classpath));
            this._doXJC();
        }
        finally {
            Thread.currentThread().setContextClassLoader(old);
        }
    }
    
    private void _doXJC() throws BuildException {
        try {
            this.options.parseArguments(this.cmdLine.getArguments());
        }
        catch (BadCommandLineException e) {
            throw new BuildException(e.getMessage(), (Throwable)e);
        }
        if (this.xmlCatalog != null) {
            if (this.options.entityResolver == null) {
                this.options.entityResolver = (EntityResolver)this.xmlCatalog;
            }
            else {
                this.options.entityResolver = new ForkEntityResolver(this.options.entityResolver, (EntityResolver)this.xmlCatalog);
            }
        }
        if (!this.producesSpecified) {
            this.log("Consider using <depends>/<produces> so that XJC won't do unnecessary compilation", 2);
        }
        final long srcTime = this.computeTimestampFor(this.dependsSet, true);
        final long dstTime = this.computeTimestampFor(this.producesSet, false);
        this.log("the last modified time of the inputs is  " + srcTime, 3);
        this.log("the last modified time of the outputs is " + dstTime, 3);
        if (srcTime < dstTime) {
            this.log("files are up to date");
            return;
        }
        final InputSource[] grammars = this.options.getGrammars();
        String msg = "Compiling " + grammars[0].getSystemId();
        if (grammars.length > 1) {
            msg += " and others";
        }
        this.log(msg, 2);
        if (this.removeOldOutput) {
            this.log("removing old output files", 2);
            for (final File f : this.producesSet) {
                f.delete();
            }
        }
        final ErrorReceiver errorReceiver = new ErrorReceiverImpl();
        final Model model = ModelLoader.load(this.options, new JCodeModel(), errorReceiver);
        if (model == null) {
            throw new BuildException("unable to parse the schema. Error messages should have been provided");
        }
        try {
            if (model.generateCode(this.options, errorReceiver) == null) {
                throw new BuildException("failed to compile a schema");
            }
            this.log("Writing output to " + this.options.targetDir, 2);
            model.codeModel.build(new AntProgressCodeWriter(this.options.createCodeWriter()));
        }
        catch (IOException e2) {
            throw new BuildException("unable to write files: " + e2.getMessage(), (Throwable)e2);
        }
    }
    
    private long computeTimestampFor(final List<File> files, final boolean findNewest) {
        long lastModified = findNewest ? Long.MIN_VALUE : Long.MAX_VALUE;
        for (final File file : files) {
            this.log("Checking timestamp of " + file.toString(), 3);
            if (findNewest) {
                lastModified = Math.max(lastModified, file.lastModified());
            }
            else {
                lastModified = Math.min(lastModified, file.lastModified());
            }
        }
        if (lastModified == Long.MIN_VALUE) {
            return Long.MAX_VALUE;
        }
        if (lastModified == Long.MAX_VALUE) {
            return Long.MIN_VALUE;
        }
        return lastModified;
    }
    
    private void addIndividualFilesTo(final FileSet fs, final List<File> lst) {
        final DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
        final String[] includedFiles = ds.getIncludedFiles();
        final File baseDir = ds.getBasedir();
        for (final String value : includedFiles) {
            lst.add(new File(baseDir, value));
        }
    }
    
    private InputSource[] toInputSources(final FileSet fs) {
        final DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
        final String[] includedFiles = ds.getIncludedFiles();
        final File baseDir = ds.getBasedir();
        final ArrayList<InputSource> lst = new ArrayList<InputSource>();
        for (final String value : includedFiles) {
            lst.add(this.getInputSource(new File(baseDir, value)));
        }
        return lst.toArray(new InputSource[lst.size()]);
    }
    
    private InputSource getInputSource(final File f) {
        try {
            return new InputSource(f.toURL().toExternalForm());
        }
        catch (MalformedURLException e) {
            return new InputSource(f.getPath());
        }
    }
    
    private InputSource getInputSource(final URL url) {
        return Util.getInputSource(url.toExternalForm());
    }
    
    private class AntProgressCodeWriter extends FilterCodeWriter
    {
        public AntProgressCodeWriter(final CodeWriter output) {
            super(output);
        }
        
        public OutputStream openBinary(final JPackage pkg, final String fileName) throws IOException {
            if (pkg.isUnnamed()) {
                XJC2Task.this.log("generating " + fileName, 3);
            }
            else {
                XJC2Task.this.log("generating " + pkg.name().replace('.', File.separatorChar) + File.separatorChar + fileName, 3);
            }
            return super.openBinary(pkg, fileName);
        }
    }
    
    private class ErrorReceiverImpl extends ErrorReceiver
    {
        public void warning(final SAXParseException e) {
            this.print(1, "Driver.WarningMessage", e);
        }
        
        public void error(final SAXParseException e) {
            this.print(0, "Driver.ErrorMessage", e);
        }
        
        public void fatalError(final SAXParseException e) {
            this.print(0, "Driver.ErrorMessage", e);
        }
        
        public void info(final SAXParseException e) {
            this.print(3, "Driver.InfoMessage", e);
        }
        
        private void print(final int logLevel, final String header, final SAXParseException e) {
            XJC2Task.this.log(Messages.format(header, e.getMessage()), logLevel);
            XJC2Task.this.log(this.getLocationString(e), logLevel);
            XJC2Task.this.log("", logLevel);
        }
    }
}
