// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.saphana;

import org.flywaydb.core.internal.util.StringUtils;
import org.flywaydb.core.internal.dbsupport.Delimiter;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;

public class SapHanaSqlStatementBuilder extends SqlStatementBuilder
{
    private boolean insideBeginEndBlock;
    private String statementStart;
    
    public SapHanaSqlStatementBuilder() {
        this.statementStart = "";
    }
    
    @Override
    protected String cleanToken(final String token) {
        if (token.startsWith("N'") || token.startsWith("X'") || token.startsWith("DATE'") || token.startsWith("TIME'") || token.startsWith("TIMESTAMP'")) {
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
        if (this.statementStart.startsWith("CREATE TRIGGER")) {
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
