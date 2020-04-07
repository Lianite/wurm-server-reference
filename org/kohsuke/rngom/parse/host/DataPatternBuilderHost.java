// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.host;

import org.kohsuke.rngom.ast.om.ParsedPattern;
import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;
import org.kohsuke.rngom.ast.builder.BuildException;
import org.kohsuke.rngom.ast.builder.Annotations;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.parse.Context;
import org.kohsuke.rngom.ast.builder.DataPatternBuilder;

final class DataPatternBuilderHost extends Base implements DataPatternBuilder
{
    final DataPatternBuilder lhs;
    final DataPatternBuilder rhs;
    
    DataPatternBuilderHost(final DataPatternBuilder lhs, final DataPatternBuilder rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public void addParam(final String name, final String value, final Context context, final String ns, final Location _loc, final Annotations _anno) throws BuildException {
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        this.lhs.addParam(name, value, context, ns, loc.lhs, anno.lhs);
        this.rhs.addParam(name, value, context, ns, loc.rhs, anno.rhs);
    }
    
    public void annotation(final ParsedElementAnnotation _ea) {
        final ParsedElementAnnotationHost ea = (ParsedElementAnnotationHost)_ea;
        this.lhs.annotation(ea.lhs);
        this.rhs.annotation(ea.rhs);
    }
    
    public ParsedPattern makePattern(final Location _loc, final Annotations _anno) throws BuildException {
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makePattern(loc.lhs, anno.lhs), this.rhs.makePattern(loc.rhs, anno.rhs));
    }
    
    public ParsedPattern makePattern(final ParsedPattern _except, final Location _loc, final Annotations _anno) throws BuildException {
        final ParsedPatternHost except = (ParsedPatternHost)_except;
        final LocationHost loc = this.cast(_loc);
        final AnnotationsHost anno = this.cast(_anno);
        return new ParsedPatternHost(this.lhs.makePattern(except.lhs, loc.lhs, anno.lhs), this.rhs.makePattern(except.rhs, loc.rhs, anno.rhs));
    }
}
