// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.ast.util;

import org.kohsuke.rngom.parse.IllegalSchemaException;
import org.kohsuke.rngom.ast.builder.BuildException;
import org.kohsuke.rngom.parse.host.ParsedPatternHost;
import org.kohsuke.rngom.ast.om.ParsedPattern;
import org.kohsuke.rngom.binary.SchemaPatternBuilder;
import org.relaxng.datatype.DatatypeLibraryFactory;
import org.kohsuke.rngom.binary.SchemaBuilderImpl;
import org.xml.sax.ErrorHandler;
import org.kohsuke.rngom.ast.builder.SchemaBuilder;
import org.kohsuke.rngom.parse.host.SchemaBuilderHost;

public class CheckingSchemaBuilder extends SchemaBuilderHost
{
    public CheckingSchemaBuilder(final SchemaBuilder sb, final ErrorHandler eh) {
        super(new SchemaBuilderImpl(eh), sb);
    }
    
    public CheckingSchemaBuilder(final SchemaBuilder sb, final ErrorHandler eh, final DatatypeLibraryFactory dlf) {
        super(new SchemaBuilderImpl(eh, dlf, new SchemaPatternBuilder()), sb);
    }
    
    public ParsedPattern expandPattern(final ParsedPattern p) throws BuildException, IllegalSchemaException {
        final ParsedPatternHost r = (ParsedPatternHost)super.expandPattern(p);
        return r.rhs;
    }
}
