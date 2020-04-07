// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import org.kohsuke.rngom.ast.om.ParsedPattern;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.builder.BuildException;
import org.kohsuke.rngom.ast.builder.Annotations;
import org.kohsuke.rngom.parse.Context;
import org.xml.sax.Locator;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.builder.DataPatternBuilder;

final class DataPatternBuilderImpl implements DataPatternBuilder
{
    private final DDataPattern p;
    
    public DataPatternBuilderImpl(final String datatypeLibrary, final String type, final Location loc) {
        this.p = new DDataPattern();
        this.p.location = (Locator)loc;
        this.p.datatypeLibrary = datatypeLibrary;
        this.p.type = type;
    }
    
    public void addParam(final String name, final String value, final Context context, final String ns, final Location loc, final Annotations anno) throws BuildException {
        this.p.params.add(this.p.new Param(name, value, context.copy(), ns, loc, (Annotation)anno));
    }
    
    public void annotation(final ParsedElementAnnotation ea) {
    }
    
    public ParsedPattern makePattern(final Location loc, final Annotations anno) throws BuildException {
        return this.makePattern(null, loc, anno);
    }
    
    public ParsedPattern makePattern(final ParsedPattern except, final Location loc, final Annotations anno) throws BuildException {
        this.p.except = (DPattern)except;
        if (anno != null) {
            this.p.annotation = ((Annotation)anno).getResult();
        }
        return this.p;
    }
}
