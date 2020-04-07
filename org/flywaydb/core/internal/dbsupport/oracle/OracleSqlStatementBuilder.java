// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.oracle;

import java.util.regex.Matcher;
import org.flywaydb.core.internal.util.StringUtils;
import org.flywaydb.core.internal.dbsupport.Delimiter;
import java.util.regex.Pattern;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;

public class OracleSqlStatementBuilder extends SqlStatementBuilder
{
    private static final Pattern KEYWORDS_BEFORE_STRING_LITERAL_REGEX;
    private static final Pattern KEYWORDS_AFTER_STRING_LITERAL_REGEX;
    private static final Delimiter PLSQL_DELIMITER;
    private String statementStart;
    
    public OracleSqlStatementBuilder() {
        this.statementStart = "";
    }
    
    @Override
    protected Delimiter changeDelimiterIfNecessary(final String line, final Delimiter delimiter) {
        if (line.matches("DECLARE|DECLARE\\s.*") || line.matches("BEGIN|BEGIN\\s.*")) {
            return OracleSqlStatementBuilder.PLSQL_DELIMITER;
        }
        if (StringUtils.countOccurrencesOf(this.statementStart, " ") < 8) {
            this.statementStart += line;
            this.statementStart += " ";
            this.statementStart = this.statementStart.replaceAll("\\s+", " ");
        }
        if (this.statementStart.matches("CREATE( OR REPLACE)? (FUNCTION|PROCEDURE|PACKAGE|TYPE|TRIGGER).*") || this.statementStart.matches("CREATE( OR REPLACE)?( AND (RESOLVE|COMPILE))?( NOFORCE)? JAVA (SOURCE|RESOURCE|CLASS).*")) {
            return OracleSqlStatementBuilder.PLSQL_DELIMITER;
        }
        return delimiter;
    }
    
    @Override
    protected String cleanToken(String token) {
        if (token.startsWith("'") && token.endsWith("'")) {
            return token;
        }
        final Matcher beforeMatcher = OracleSqlStatementBuilder.KEYWORDS_BEFORE_STRING_LITERAL_REGEX.matcher(token);
        if (beforeMatcher.find()) {
            token = beforeMatcher.group(2);
        }
        final Matcher afterMatcher = OracleSqlStatementBuilder.KEYWORDS_AFTER_STRING_LITERAL_REGEX.matcher(token);
        if (afterMatcher.find()) {
            token = afterMatcher.group(1);
        }
        return token;
    }
    
    @Override
    protected String simplifyLine(final String line) {
        final String simplifiedQQuotes = StringUtils.replaceAll(StringUtils.replaceAll(line, "q'(", "q'["), ")'", "]'");
        return super.simplifyLine(simplifiedQQuotes);
    }
    
    @Override
    protected String extractAlternateOpenQuote(final String token) {
        if (token.startsWith("Q'") && token.length() >= 3) {
            return token.substring(0, 3);
        }
        return null;
    }
    
    @Override
    protected String computeAlternateCloseQuote(final String openQuote) {
        final char specialChar = openQuote.charAt(2);
        switch (specialChar) {
            case '[': {
                return "]'";
            }
            case '(': {
                return ")'";
            }
            case '{': {
                return "}'";
            }
            case '<': {
                return ">'";
            }
            default: {
                return specialChar + "'";
            }
        }
    }
    
    @Override
    public boolean canDiscard() {
        return super.canDiscard() || this.statementStart.startsWith("SET DEFINE OFF");
    }
    
    static {
        KEYWORDS_BEFORE_STRING_LITERAL_REGEX = Pattern.compile("^(N|IF|ELSIF|SELECT|IMMEDIATE|RETURN|IS)('.*)");
        KEYWORDS_AFTER_STRING_LITERAL_REGEX = Pattern.compile("(.*')(USING|THEN|FROM|AND|OR)(?!.)");
        PLSQL_DELIMITER = new Delimiter("/", true);
    }
}
