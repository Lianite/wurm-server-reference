// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.jxc.apt;

import org.xml.sax.SAXParseException;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Messager;
import com.sun.tools.xjc.ErrorReceiver;

final class ErrorReceiverImpl extends ErrorReceiver
{
    private final Messager messager;
    private final boolean debug;
    
    public ErrorReceiverImpl(final Messager messager, final boolean debug) {
        this.messager = messager;
        this.debug = debug;
    }
    
    public ErrorReceiverImpl(final Messager messager) {
        this(messager, false);
    }
    
    public ErrorReceiverImpl(final AnnotationProcessorEnvironment env) {
        this(env.getMessager());
    }
    
    public void error(final SAXParseException exception) {
        this.messager.printError(exception.getMessage());
        this.messager.printError(this.getLocation(exception));
        this.printDetail(exception);
    }
    
    public void fatalError(final SAXParseException exception) {
        this.messager.printError(exception.getMessage());
        this.messager.printError(this.getLocation(exception));
        this.printDetail(exception);
    }
    
    public void warning(final SAXParseException exception) {
        this.messager.printWarning(exception.getMessage());
        this.messager.printWarning(this.getLocation(exception));
        this.printDetail(exception);
    }
    
    public void info(final SAXParseException exception) {
        this.printDetail(exception);
    }
    
    private String getLocation(final SAXParseException e) {
        return "";
    }
    
    private void printDetail(final SAXParseException e) {
        if (this.debug) {
            e.printStackTrace(System.out);
        }
    }
}
