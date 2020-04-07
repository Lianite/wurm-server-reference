// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.sqlserver;

import java.util.regex.Matcher;
import org.flywaydb.core.internal.dbsupport.Delimiter;
import java.util.regex.Pattern;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;

public class SQLServerSqlStatementBuilder extends SqlStatementBuilder
{
    private static final Pattern KEYWORDS_BEFORE_STRING_LITERAL_REGEX;
    
    @Override
    protected Delimiter getDefaultDelimiter() {
        return new Delimiter("GO", true);
    }
    
    @Override
    protected String cleanToken(String token) {
        if (token.startsWith("N'")) {
            return token.substring(token.indexOf("'"));
        }
        final Matcher beforeMatcher = SQLServerSqlStatementBuilder.KEYWORDS_BEFORE_STRING_LITERAL_REGEX.matcher(token);
        if (beforeMatcher.find()) {
            token = beforeMatcher.group(2);
        }
        return token;
    }
    
    static {
        KEYWORDS_BEFORE_STRING_LITERAL_REGEX = Pattern.compile("^(LIKE)('.*)");
    }
}
