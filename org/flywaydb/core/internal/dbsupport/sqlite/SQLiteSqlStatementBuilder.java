// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.sqlite;

import org.flywaydb.core.internal.util.StringUtils;
import org.flywaydb.core.internal.dbsupport.Delimiter;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;

public class SQLiteSqlStatementBuilder extends SqlStatementBuilder
{
    private String statementStart;
    
    public SQLiteSqlStatementBuilder() {
        this.statementStart = "";
    }
    
    @Override
    protected Delimiter changeDelimiterIfNecessary(final String line, final Delimiter delimiter) {
        if (StringUtils.countOccurrencesOf(this.statementStart, " ") < 8) {
            this.statementStart += line;
            this.statementStart += " ";
            this.statementStart = this.statementStart.replaceAll("\\s+", " ");
        }
        final boolean createTriggerStatement = this.statementStart.matches("CREATE( TEMP| TEMPORARY)? TRIGGER.*");
        if (createTriggerStatement && !line.endsWith("END;")) {
            return null;
        }
        return this.getDefaultDelimiter();
    }
    
    @Override
    protected String cleanToken(final String token) {
        if (token.startsWith("X'")) {
            return token.substring(token.indexOf("'"));
        }
        return token;
    }
}
