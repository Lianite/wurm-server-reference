// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.host;

import org.kohsuke.rngom.ast.om.ParsedNameClass;

final class ParsedNameClassHost implements ParsedNameClass
{
    final ParsedNameClass lhs;
    final ParsedNameClass rhs;
    
    ParsedNameClassHost(final ParsedNameClass lhs, final ParsedNameClass rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
}
