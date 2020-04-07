// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.h2;

import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;

public class H2SqlStatementBuilder extends SqlStatementBuilder
{
    @Override
    protected String extractAlternateOpenQuote(final String token) {
        if (token.startsWith("$$")) {
            return "$$";
        }
        return null;
    }
}
