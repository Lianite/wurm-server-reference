// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.vertica;

import org.flywaydb.core.internal.util.StringUtils;
import org.flywaydb.core.internal.dbsupport.Delimiter;
import org.flywaydb.core.internal.dbsupport.postgresql.PostgreSQLSqlStatementBuilder;

public class VerticaStatementBuilder extends PostgreSQLSqlStatementBuilder
{
    private boolean insideBeginEndBlock;
    private String statementStart;
    
    public VerticaStatementBuilder() {
        this.statementStart = "";
    }
    
    @Override
    protected Delimiter changeDelimiterIfNecessary(final String line, final Delimiter delimiter) {
        if (StringUtils.countOccurrencesOf(this.statementStart, " ") < 4) {
            this.statementStart += line;
            this.statementStart += " ";
        }
        if (this.statementStart.startsWith("CREATE FUNCTION") || this.statementStart.startsWith("CREATE OR REPLACE FUNCTION")) {
            if (line.startsWith("BEGIN") || line.endsWith("BEGIN")) {
                this.insideBeginEndBlock = true;
            }
            if (line.endsWith("END;")) {
                this.insideBeginEndBlock = false;
            }
        }
        if (this.insideBeginEndBlock) {
            return null;
        }
        return this.getDefaultDelimiter();
    }
}
