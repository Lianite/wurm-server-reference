// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.sybase.ase;

import org.flywaydb.core.internal.dbsupport.Delimiter;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;

public class SybaseASESqlStatementBuilder extends SqlStatementBuilder
{
    @Override
    protected Delimiter getDefaultDelimiter() {
        return new Delimiter("GO", true);
    }
    
    @Override
    protected String computeAlternateCloseQuote(final String openQuote) {
        final char specialChar = openQuote.charAt(2);
        switch (specialChar) {
            case '(': {
                return ")'";
            }
            default: {
                return specialChar + "'";
            }
        }
    }
}
