// 
// Decompiled by Procyon v0.5.30
// 

package com.sun.tools.xjc.reader.internalizer;

import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.Locator;
import org.w3c.dom.Element;
import java.util.HashMap;
import java.util.Map;

public final class LocatorTable
{
    private final Map startLocations;
    private final Map endLocations;
    
    public LocatorTable() {
        this.startLocations = new HashMap();
        this.endLocations = new HashMap();
    }
    
    public void storeStartLocation(final Element e, final Locator loc) {
        this.startLocations.put(e, new LocatorImpl(loc));
    }
    
    public void storeEndLocation(final Element e, final Locator loc) {
        this.endLocations.put(e, new LocatorImpl(loc));
    }
    
    public Locator getStartLocation(final Element e) {
        return this.startLocations.get(e);
    }
    
    public Locator getEndLocation(final Element e) {
        return this.endLocations.get(e);
    }
}
