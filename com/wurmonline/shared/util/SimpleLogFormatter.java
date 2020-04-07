// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.shared.util;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.FieldPosition;
import java.util.logging.LogRecord;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;

public class SimpleLogFormatter extends Formatter
{
    Date dat;
    private static final String FORMAT = "{0,date} {0,time}";
    private MessageFormat formatter;
    private Object[] args;
    private final String lineSeparator;
    
    public SimpleLogFormatter() {
        this.dat = new Date();
        this.args = new Object[1];
        this.lineSeparator = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("line.separator"));
    }
    
    @Override
    public synchronized String format(final LogRecord record) {
        final StringBuilder sb = new StringBuilder();
        this.dat.setTime(record.getMillis());
        this.args[0] = this.dat;
        final StringBuffer text = new StringBuffer();
        if (this.formatter == null) {
            this.formatter = new MessageFormat("{0,date} {0,time}");
        }
        this.formatter.format(this.args, text, null);
        sb.append(text);
        sb.append('.');
        sb.append(record.getMillis() % 1000L);
        sb.append(' ');
        sb.append(record.getThreadID());
        sb.append(' ');
        if (record.getSourceClassName() != null) {
            sb.append(record.getSourceClassName());
        }
        else {
            sb.append(record.getLoggerName());
        }
        if (record.getSourceMethodName() != null) {
            sb.append(' ');
            sb.append(record.getSourceMethodName());
        }
        sb.append(' ');
        final String message = this.formatMessage(record);
        sb.append(record.getLevel().getLocalizedName());
        sb.append(": ");
        sb.append(message);
        sb.append(this.lineSeparator);
        if (record.getThrown() != null) {
            try {
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            }
            catch (Exception ex) {}
        }
        return sb.toString();
    }
}
