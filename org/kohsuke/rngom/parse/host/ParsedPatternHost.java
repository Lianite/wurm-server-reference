// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.host;

import org.kohsuke.rngom.ast.om.ParsedPattern;

public class ParsedPatternHost implements ParsedPattern
{
    public final ParsedPattern lhs;
    public final ParsedPattern rhs;
    
    ParsedPatternHost(final ParsedPattern lhs, final ParsedPattern rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
}
