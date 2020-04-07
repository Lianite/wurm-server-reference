// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse;

import org.kohsuke.rngom.ast.builder.Scope;
import org.kohsuke.rngom.ast.builder.IncludedGrammar;
import org.kohsuke.rngom.ast.builder.BuildException;
import org.kohsuke.rngom.ast.om.ParsedPattern;
import org.kohsuke.rngom.ast.builder.SchemaBuilder;

public interface Parseable
{
     <P extends ParsedPattern> P parse(final SchemaBuilder<?, P, ?, ?, ?, ?> p0) throws BuildException, IllegalSchemaException;
    
     <P extends ParsedPattern> P parseInclude(final String p0, final SchemaBuilder<?, P, ?, ?, ?, ?> p1, final IncludedGrammar<P, ?, ?, ?, ?> p2, final String p3) throws BuildException, IllegalSchemaException;
    
     <P extends ParsedPattern> P parseExternal(final String p0, final SchemaBuilder<?, P, ?, ?, ?, ?> p1, final Scope p2, final String p3) throws BuildException, IllegalSchemaException;
}
