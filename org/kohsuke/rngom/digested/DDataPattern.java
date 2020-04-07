// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.parse.Context;
import java.util.ArrayList;
import java.util.List;

public class DDataPattern extends DPattern
{
    DPattern except;
    String datatypeLibrary;
    String type;
    final List<Param> params;
    
    public DDataPattern() {
        this.params = new ArrayList<Param>();
    }
    
    public String getDatatypeLibrary() {
        return this.datatypeLibrary;
    }
    
    public String getType() {
        return this.type;
    }
    
    public List<Param> getParams() {
        return this.params;
    }
    
    public DPattern getExcept() {
        return this.except;
    }
    
    public boolean isNullable() {
        return false;
    }
    
    public Object accept(final DPatternVisitor visitor) {
        return visitor.onData(this);
    }
    
    public final class Param
    {
        String name;
        String value;
        Context context;
        String ns;
        Location loc;
        Annotation anno;
        
        public Param(final String name, final String value, final Context context, final String ns, final Location loc, final Annotation anno) {
            this.name = name;
            this.value = value;
            this.context = context;
            this.ns = ns;
            this.loc = loc;
            this.anno = anno;
        }
        
        public String getName() {
            return this.name;
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
        
        public Location getLoc() {
            return this.loc;
        }
        
        public Annotation getAnno() {
            return this.anno;
        }
    }
}
