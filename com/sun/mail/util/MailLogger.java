// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.mail.util;

import java.text.MessageFormat;
import java.util.logging.Level;
import javax.mail.Session;
import java.io.PrintStream;
import java.util.logging.Logger;

public final class MailLogger
{
    private final Logger logger;
    private final String prefix;
    private final boolean debug;
    private final PrintStream out;
    static /* synthetic */ Class class$com$sun$mail$util$MailLogger;
    
    public MailLogger(final String name, final String prefix, final boolean debug, final PrintStream out) {
        this.logger = Logger.getLogger(name);
        this.prefix = prefix;
        this.debug = debug;
        this.out = ((out != null) ? out : System.out);
    }
    
    public MailLogger(final Class clazz, final String prefix, final boolean debug, final PrintStream out) {
        final String name = this.packageOf(clazz);
        this.logger = Logger.getLogger(name);
        this.prefix = prefix;
        this.debug = debug;
        this.out = ((out != null) ? out : System.out);
    }
    
    public MailLogger(final Class clazz, final String subname, final String prefix, final boolean debug, final PrintStream out) {
        final String name = this.packageOf(clazz) + "." + subname;
        this.logger = Logger.getLogger(name);
        this.prefix = prefix;
        this.debug = debug;
        this.out = ((out != null) ? out : System.out);
    }
    
    public MailLogger(final String name, final String prefix, final Session session) {
        this(name, prefix, session.getDebug(), session.getDebugOut());
    }
    
    public MailLogger(final Class clazz, final String prefix, final Session session) {
        this(clazz, prefix, session.getDebug(), session.getDebugOut());
    }
    
    public MailLogger getLogger(final String name, final String prefix) {
        return new MailLogger(name, prefix, this.debug, this.out);
    }
    
    public MailLogger getLogger(final Class clazz, final String prefix) {
        return new MailLogger(clazz, prefix, this.debug, this.out);
    }
    
    public MailLogger getSubLogger(final String subname, final String prefix) {
        return new MailLogger(this.logger.getName() + "." + subname, prefix, this.debug, this.out);
    }
    
    public MailLogger getSubLogger(final String subname, final String prefix, final boolean debug) {
        return new MailLogger(this.logger.getName() + "." + subname, prefix, debug, this.out);
    }
    
    public void log(final Level level, final String msg) {
        this.ifDebugOut(msg);
        if (this.logger.isLoggable(level)) {
            final String[] frame = this.inferCaller();
            this.logger.logp(level, frame[0], frame[1], msg);
        }
    }
    
    public void log(final Level level, String msg, final Object param1) {
        if (this.debug) {
            msg = MessageFormat.format(msg, param1);
            this.debugOut(msg);
        }
        if (this.logger.isLoggable(level)) {
            final String[] frame = this.inferCaller();
            this.logger.logp(level, frame[0], frame[1], msg, param1);
        }
    }
    
    public void log(final Level level, String msg, final Object[] params) {
        if (this.debug) {
            msg = MessageFormat.format(msg, params);
            this.debugOut(msg);
        }
        if (this.logger.isLoggable(level)) {
            final String[] frame = this.inferCaller();
            this.logger.logp(level, frame[0], frame[1], msg, params);
        }
    }
    
    public void log(final Level level, final String msg, final Throwable thrown) {
        if (this.debug) {
            if (thrown != null) {
                this.debugOut(msg + ", THROW: ");
                thrown.printStackTrace(this.out);
            }
            else {
                this.debugOut(msg);
            }
        }
        if (this.logger.isLoggable(level)) {
            final String[] frame = this.inferCaller();
            this.logger.logp(level, frame[0], frame[1], msg, thrown);
        }
    }
    
    public void config(final String msg) {
        this.log(Level.CONFIG, msg);
    }
    
    public void fine(final String msg) {
        this.log(Level.FINE, msg);
    }
    
    public void finer(final String msg) {
        this.log(Level.FINER, msg);
    }
    
    public void finest(final String msg) {
        this.log(Level.FINEST, msg);
    }
    
    public boolean isLoggable(final Level level) {
        return this.debug || this.logger.isLoggable(level);
    }
    
    private final void ifDebugOut(final String msg) {
        if (this.debug) {
            this.debugOut(msg);
        }
    }
    
    private final void debugOut(final String msg) {
        if (this.prefix != null) {
            this.out.println(this.prefix + ": " + msg);
        }
        else {
            this.out.println(msg);
        }
    }
    
    private String packageOf(final Class clazz) {
        final Package p = clazz.getPackage();
        if (p != null) {
            return p.getName();
        }
        final String cname = clazz.getName();
        final int i = cname.lastIndexOf(46);
        if (i > 0) {
            return cname.substring(0, i);
        }
        return "";
    }
    
    private String[] inferCaller() {
        StackTraceElement[] stack;
        int ix;
        StackTraceElement frame;
        String cname;
        for (stack = new Throwable().getStackTrace(), ix = 0; ix < stack.length; ++ix) {
            frame = stack[ix];
            cname = frame.getClassName();
            if (this.isLoggerImplFrame(cname)) {
                break;
            }
        }
        while (ix < stack.length) {
            frame = stack[ix];
            cname = frame.getClassName();
            if (!this.isLoggerImplFrame(cname)) {
                return new String[] { cname, frame.getMethodName() };
            }
            ++ix;
        }
        return new String[] { null, null };
    }
    
    private boolean isLoggerImplFrame(final String cname) {
        return ((MailLogger.class$com$sun$mail$util$MailLogger == null) ? (MailLogger.class$com$sun$mail$util$MailLogger = class$("com.sun.mail.util.MailLogger")) : MailLogger.class$com$sun$mail$util$MailLogger).getName().equals(cname);
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError().initCause(x);
        }
    }
}
