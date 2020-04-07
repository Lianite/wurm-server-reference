// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.compact;

class EscapeSyntaxException extends RuntimeException
{
    private final String key;
    private final int lineNumber;
    private final int columnNumber;
    
    EscapeSyntaxException(final String key, final int lineNumber, final int columnNumber) {
        this.key = key;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    String getKey() {
        return this.key;
    }
    
    int getLineNumber() {
        return this.lineNumber;
    }
    
    int getColumnNumber() {
        return this.columnNumber;
    }
}
