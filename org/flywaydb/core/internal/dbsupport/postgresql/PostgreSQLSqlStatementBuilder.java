// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.postgresql;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.flywaydb.core.internal.dbsupport.Delimiter;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;

public class PostgreSQLSqlStatementBuilder extends SqlStatementBuilder
{
    private static final Delimiter COPY_DELIMITER;
    static final String DOLLAR_QUOTE_REGEX = "(\\$[A-Za-z0-9_]*\\$).*";
    private boolean firstLine;
    private String copyStatement;
    private boolean pgCopy;
    
    public PostgreSQLSqlStatementBuilder() {
        this.firstLine = true;
    }
    
    @Override
    protected String extractAlternateOpenQuote(final String token) {
        final Matcher matcher = Pattern.compile("(\\$[A-Za-z0-9_]*\\$).*").matcher(token);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    @Override
    protected Delimiter changeDelimiterIfNecessary(final String line, final Delimiter delimiter) {
        if (this.pgCopy) {
            return PostgreSQLSqlStatementBuilder.COPY_DELIMITER;
        }
        if (this.firstLine) {
            this.firstLine = false;
            if (line.matches("COPY|COPY\\s.*")) {
                this.copyStatement = line;
            }
        }
        else if (this.copyStatement != null) {
            this.copyStatement = this.copyStatement + " " + line;
        }
        if (this.copyStatement != null && this.copyStatement.contains(" FROM STDIN")) {
            this.pgCopy = true;
            return PostgreSQLSqlStatementBuilder.COPY_DELIMITER;
        }
        return delimiter;
    }
    
    @Override
    public boolean isPgCopyFromStdIn() {
        return this.pgCopy;
    }
    
    @Override
    protected String cleanToken(final String token) {
        if (token.startsWith("E'")) {
            return token.substring(token.indexOf("'"));
        }
        return token;
    }
    
    static {
        COPY_DELIMITER = new Delimiter("\\.", true);
    }
}
