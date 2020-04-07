// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.mysql;

import org.flywaydb.core.internal.util.StringUtils;
import java.util.regex.Pattern;
import org.flywaydb.core.internal.dbsupport.Delimiter;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;

public class MySQLSqlStatementBuilder extends SqlStatementBuilder
{
    private static final String DELIMITER_KEYWORD = "DELIMITER";
    private final String[] charSets;
    boolean isInMultiLineCommentDirective;
    
    public MySQLSqlStatementBuilder() {
        this.charSets = new String[] { "ARMSCII8", "ASCII", "BIG5", "BINARY", "CP1250", "CP1251", "CP1256", "CP1257", "CP850", "CP852", "CP866", "CP932", "DEC8", "EUCJPMS", "EUCKR", "GB2312", "GBK", "GEOSTD8", "GREEK", "HEBREW", "HP8", "KEYBCS2", "KOI8R", "KOI8U", "LATIN1", "LATIN2", "LATIN5", "LATIN7", "MACCE", "MACROMAN", "SJIS", "SWE7", "TIS620", "UCS2", "UJIS", "UTF8" };
        this.isInMultiLineCommentDirective = false;
    }
    
    @Override
    public Delimiter extractNewDelimiterFromLine(final String line) {
        if (line.toUpperCase().startsWith("DELIMITER")) {
            return new Delimiter(line.substring("DELIMITER".length()).trim(), false);
        }
        return null;
    }
    
    @Override
    protected Delimiter changeDelimiterIfNecessary(final String line, final Delimiter delimiter) {
        if (line.toUpperCase().startsWith("DELIMITER")) {
            return new Delimiter(line.substring("DELIMITER".length()).trim(), false);
        }
        return delimiter;
    }
    
    @Override
    public boolean isCommentDirective(final String line) {
        if (line.matches("^" + Pattern.quote("/*!") + "\\d{5} .*" + Pattern.quote("*/") + "\\s*;?")) {
            return true;
        }
        if (line.matches("^" + Pattern.quote("/*!") + "\\d{5} .*")) {
            return this.isInMultiLineCommentDirective = true;
        }
        if (this.isInMultiLineCommentDirective && line.matches(".*" + Pattern.quote("*/") + "\\s*;?")) {
            this.isInMultiLineCommentDirective = false;
            return true;
        }
        return this.isInMultiLineCommentDirective;
    }
    
    @Override
    protected boolean isSingleLineComment(final String token) {
        return token.startsWith("--") || (token.startsWith("#") && (!"#".equals(this.delimiter.getDelimiter()) || !"#".equals(token)));
    }
    
    @Override
    protected String removeEscapedQuotes(final String token) {
        final String noEscapedBackslashes = StringUtils.replaceAll(token, "\\\\", "");
        final String noBackslashEscapes = StringUtils.replaceAll(StringUtils.replaceAll(noEscapedBackslashes, "\\'", ""), "\\\"", "");
        return StringUtils.replaceAll(noBackslashEscapes, "''", "").replace("'", " ' ");
    }
    
    @Override
    protected String cleanToken(final String token) {
        if (token.startsWith("B'") || token.startsWith("X'")) {
            return token.substring(token.indexOf("'"));
        }
        if (token.startsWith("_")) {
            for (final String charSet : this.charSets) {
                final String cast = "_" + charSet;
                if (token.startsWith(cast)) {
                    return token.substring(cast.length());
                }
            }
        }
        return token;
    }
    
    @Override
    protected String extractAlternateOpenQuote(final String token) {
        if (token.startsWith("\"")) {
            return "\"";
        }
        return null;
    }
}
