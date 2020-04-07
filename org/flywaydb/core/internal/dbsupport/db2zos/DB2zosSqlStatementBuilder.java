// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.db2zos;

import org.flywaydb.core.internal.util.StringUtils;
import org.flywaydb.core.internal.dbsupport.Delimiter;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;

public class DB2zosSqlStatementBuilder extends SqlStatementBuilder
{
    private boolean insideBeginEndBlock;
    private String statementStart;
    
    public DB2zosSqlStatementBuilder() {
        this.statementStart = "";
    }
    
    @Override
    protected String cleanToken(final String token) {
        if (token.startsWith("X'")) {
            return token.substring(token.indexOf("'"));
        }
        return super.cleanToken(token);
    }
    
    @Override
    protected Delimiter changeDelimiterIfNecessary(final String line, final Delimiter delimiter) {
        if (StringUtils.countOccurrencesOf(this.statementStart, " ") < 4) {
            this.statementStart += line;
            this.statementStart += " ";
        }
        if (this.statementStart.startsWith("CREATE FUNCTION") || this.statementStart.startsWith("CREATE PROCEDURE") || this.statementStart.startsWith("CREATE TRIGGER") || this.statementStart.startsWith("CREATE OR REPLACE FUNCTION") || this.statementStart.startsWith("CREATE OR REPLACE PROCEDURE") || this.statementStart.startsWith("CREATE OR REPLACE TRIGGER")) {
            if (line.startsWith("BEGIN")) {
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
