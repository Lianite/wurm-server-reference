// 
// Decompiled by Procyon v0.5.30
// 

package org.apache.xml.resolver.apps;

import org.xml.sax.SAXParseException;
import java.net.MalformedURLException;
import java.net.URL;
import org.xml.sax.ErrorHandler;

public class XParseError implements ErrorHandler
{
    private boolean showErrors;
    private boolean showWarnings;
    private int maxMessages;
    private int fatalCount;
    private int errorCount;
    private int warningCount;
    private String baseURI;
    
    public XParseError(final boolean showErrors, final boolean showWarnings) {
        this.showErrors = true;
        this.showWarnings = false;
        this.maxMessages = 10;
        this.fatalCount = 0;
        this.errorCount = 0;
        this.warningCount = 0;
        this.baseURI = "";
        this.showErrors = showErrors;
        this.showWarnings = showWarnings;
        final String property = System.getProperty("user.dir");
        final String s = "";
        String s2;
        if (property.endsWith("/")) {
            s2 = "file:" + property + "file";
        }
        else {
            s2 = "file:" + property + "/" + s;
        }
        try {
            this.baseURI = new URL(s2).toString();
        }
        catch (MalformedURLException ex) {}
    }
    
    public int getErrorCount() {
        return this.errorCount;
    }
    
    public int getFatalCount() {
        return this.fatalCount;
    }
    
    public int getWarningCount() {
        return this.warningCount;
    }
    
    public int getMaxMessages() {
        return this.maxMessages;
    }
    
    public void setMaxMessages(final int maxMessages) {
        this.maxMessages = maxMessages;
    }
    
    public void error(final SAXParseException ex) {
        if (this.showErrors) {
            if (this.errorCount + this.warningCount < this.maxMessages) {
                this.message("Error", ex);
            }
            ++this.errorCount;
        }
    }
    
    public void fatalError(final SAXParseException ex) {
        if (this.showErrors) {
            if (this.errorCount + this.warningCount < this.maxMessages) {
                this.message("Fatal error", ex);
            }
            ++this.errorCount;
            ++this.fatalCount;
        }
    }
    
    public void warning(final SAXParseException ex) {
        if (this.showWarnings) {
            if (this.errorCount + this.warningCount < this.maxMessages) {
                this.message("Warning", ex);
            }
            ++this.warningCount;
        }
    }
    
    private void message(final String s, final SAXParseException ex) {
        String s2 = ex.getSystemId();
        if (s2.startsWith(this.baseURI)) {
            s2 = s2.substring(this.baseURI.length());
        }
        System.out.print(s + ":" + s2 + ":" + ex.getLineNumber());
        if (ex.getColumnNumber() > 0) {
            System.out.print(":" + ex.getColumnNumber());
        }
        System.out.println(":" + ex.getMessage());
    }
}
