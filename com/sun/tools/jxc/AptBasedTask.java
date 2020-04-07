// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc;

import java.lang.reflect.Method;
import org.apache.tools.ant.taskdefs.compilers.DefaultCompilerAdapter;
import org.apache.tools.ant.BuildException;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.taskdefs.Javac;

public abstract class AptBasedTask extends Javac
{
    static /* synthetic */ Class class$com$sun$mirror$apt$AnnotationProcessorFactory;
    static /* synthetic */ Class array$Ljava$lang$String;
    
    public AptBasedTask() {
        this.setExecutable("apt");
    }
    
    protected abstract void setupCommandlineSwitches(final Commandline p0);
    
    protected abstract AnnotationProcessorFactory createFactory();
    
    protected void compile() {
        if (this.compileList.length == 0) {
            return;
        }
        this.log(this.getCompilationMessage() + this.compileList.length + " source file" + ((this.compileList.length == 1) ? "" : "s"));
        if (this.listFiles) {
            for (int i = 0; i < this.compileList.length; ++i) {
                final String filename = this.compileList[i].getAbsolutePath();
                this.log(filename);
            }
        }
        final AptAdapter apt = new InternalAptAdapter();
        if (!apt.execute()) {
            if (this.failOnError) {
                throw new BuildException(this.getFailedMessage(), this.getLocation());
            }
            this.log(this.getFailedMessage(), 0);
        }
    }
    
    protected abstract String getCompilationMessage();
    
    protected abstract String getFailedMessage();
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError(x.getMessage());
        }
    }
    
    private abstract class AptAdapter extends DefaultCompilerAdapter
    {
        protected AptAdapter() {
            this.setJavac((Javac)AptBasedTask.this);
        }
        
        protected Commandline setupModernJavacCommandlineSwitches(final Commandline cmd) {
            super.setupModernJavacCommandlineSwitches(cmd);
            AptBasedTask.this.setupCommandlineSwitches(cmd);
            return cmd;
        }
    }
    
    private final class InternalAptAdapter extends AptAdapter
    {
        public boolean execute() throws BuildException {
            final Commandline cmd = this.setupModernJavacCommand();
            try {
                final Class apt = Class.forName("com.sun.tools.apt.Main");
                Method process;
                try {
                    process = apt.getMethod("process", (AptBasedTask.class$com$sun$mirror$apt$AnnotationProcessorFactory == null) ? (AptBasedTask.class$com$sun$mirror$apt$AnnotationProcessorFactory = AptBasedTask.class$("com.sun.mirror.apt.AnnotationProcessorFactory")) : AptBasedTask.class$com$sun$mirror$apt$AnnotationProcessorFactory, (AptBasedTask.array$Ljava$lang$String == null) ? (AptBasedTask.array$Ljava$lang$String = AptBasedTask.class$("[Ljava.lang.String;")) : AptBasedTask.array$Ljava$lang$String);
                }
                catch (NoSuchMethodException e) {
                    throw new BuildException("JDK 1.5.0_01 or later is necessary", (Throwable)e, this.location);
                }
                final int result = (int)process.invoke(null, AptBasedTask.this.createFactory(), cmd.getArguments());
                return result == 0;
            }
            catch (BuildException e2) {
                throw e2;
            }
            catch (Exception ex) {
                throw new BuildException("Error starting apt", (Throwable)ex, this.location);
            }
        }
    }
}
