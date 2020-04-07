// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.ast.util;

import org.kohsuke.rngom.ast.om.Location;
import org.xml.sax.Locator;

public class LocatorImpl implements Locator, Location
{
    private final String systemId;
    private final int lineNumber;
    private final int columnNumber;
    
    public LocatorImpl(final String systemId, final int lineNumber, final int columnNumber) {
        this.systemId = systemId;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public String getPublicId() {
        return null;
    }
    
    public String getSystemId() {
        return this.systemId;
    }
    
    public int getLineNumber() {
        return this.lineNumber;
    }
    
    public int getColumnNumber() {
        return this.columnNumber;
    }
}
