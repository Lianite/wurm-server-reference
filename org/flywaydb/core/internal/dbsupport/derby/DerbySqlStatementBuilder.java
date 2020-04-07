// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.derby;

import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;

public class DerbySqlStatementBuilder extends SqlStatementBuilder
{
    @Override
    protected String extractAlternateOpenQuote(final String token) {
        if (token.startsWith("$$")) {
            return "$$";
        }
        return null;
    }
    
    @Override
    protected String cleanToken(final String token) {
        if (token.startsWith("X'")) {
            return token.substring(token.indexOf("'"));
        }
        return super.cleanToken(token);
    }
}
