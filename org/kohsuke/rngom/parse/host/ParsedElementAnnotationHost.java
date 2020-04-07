// 
// Decompiled by Procyon v0.5.30
// 

package org.kohsuke.rngom.parse.host;

import org.kohsuke.rngom.ast.om.ParsedElementAnnotation;

final class ParsedElementAnnotationHost implements ParsedElementAnnotation
{
    final ParsedElementAnnotation lhs;
    final ParsedElementAnnotation rhs;
    
    ParsedElementAnnotationHost(final ParsedElementAnnotation lhs, final ParsedElementAnnotation rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
}
