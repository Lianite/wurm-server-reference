// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.digested;

import org.kohsuke.rngom.parse.IllegalSchemaException;
import org.kohsuke.rngom.ast.builder.IncludedGrammar;
import org.kohsuke.rngom.ast.builder.SchemaBuilder;
import org.kohsuke.rngom.parse.Parseable;
import org.kohsuke.rngom.ast.builder.BuildException;
import org.kohsuke.rngom.ast.builder.Annotations;
import org.kohsuke.rngom.ast.om.Location;
import org.kohsuke.rngom.ast.om.ParsedPattern;
import org.kohsuke.rngom.ast.builder.GrammarSection;
import java.util.HashSet;
import org.kohsuke.rngom.ast.builder.Scope;
import java.util.Set;
import org.kohsuke.rngom.ast.builder.Include;

final class IncludeImpl extends GrammarBuilderImpl implements Include
{
    private Set overridenPatterns;
    private boolean startOverriden;
    
    public IncludeImpl(final DGrammarPattern p, final Scope parent, final DSchemaBuilderImpl sb) {
        super(p, parent, sb);
        this.overridenPatterns = new HashSet();
        this.startOverriden = false;
    }
    
    public void define(final String name, final GrammarSection.Combine combine, final ParsedPattern pattern, final Location loc, final Annotations anno) throws BuildException {
        super.define(name, combine, pattern, loc, anno);
        if (name == "\u0000#start\u0000") {
            this.startOverriden = true;
        }
        else {
            this.overridenPatterns.add(name);
        }
    }
    
    public void endInclude(final Parseable current, final String uri, final String ns, final Location loc, final Annotations anno) throws BuildException, IllegalSchemaException {
        current.parseInclude(uri, (SchemaBuilder<?, ParsedPattern, ?, ?, ?, ?>)this.sb, new IncludedGrammarImpl(this.grammar, this.parent, this.sb), ns);
    }
    
    private class IncludedGrammarImpl extends GrammarBuilderImpl implements IncludedGrammar
    {
        public IncludedGrammarImpl(final DGrammarPattern p, final Scope parent, final DSchemaBuilderImpl sb) {
            super(p, parent, sb);
        }
        
        public void define(final String name, final GrammarSection.Combine combine, final ParsedPattern pattern, final Location loc, final Annotations anno) throws BuildException {
            if (name == "\u0000#start\u0000") {
                if (IncludeImpl.this.startOverriden) {
                    return;
                }
            }
            else if (IncludeImpl.this.overridenPatterns.contains(name)) {
                return;
            }
            super.define(name, combine, pattern, loc, anno);
        }
        
        public ParsedPattern endIncludedGrammar(final Location loc, final Annotations anno) throws BuildException {
            return null;
        }
    }
}
