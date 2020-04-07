// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import org.kohsuke.rngom.parse.Context;

public class DValuePattern extends DPattern
{
    private String datatypeLibrary;
    private String type;
    private String value;
    private Context context;
    private String ns;
    
    public DValuePattern(final String datatypeLibrary, final String type, final String value, final Context context, final String ns) {
        this.datatypeLibrary = datatypeLibrary;
        this.type = type;
        this.value = value;
        this.context = context;
        this.ns = ns;
    }
    
    public String getDatatypeLibrary() {
        return this.datatypeLibrary;
    }
    
    public String getType() {
        return this.type;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public Context getContext() {
        return this.context;
    }
    
    public String getNs() {
        return this.ns;
    }
    
    public boolean isNullable() {
        return false;
    }
    
    public Object accept(final DPatternVisitor visitor) {
        return visitor.onValue(this);
    }
}
