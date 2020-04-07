// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.flywaydb.core.internal.util.StringUtils;

public class SqlStatementBuilder
{
    private StringBuilder statement;
    private int lineNumber;
    private boolean empty;
    private boolean terminated;
    private boolean insideQuoteStringLiteral;
    private boolean insideAlternateQuoteStringLiteral;
    private String alternateQuote;
    private boolean lineEndsWithSingleLineComment;
    private boolean insideMultiLineComment;
    private boolean nonCommentStatementPartSeen;
    protected Delimiter delimiter;
    
    public SqlStatementBuilder() {
        this.statement = new StringBuilder();
        this.empty = true;
        this.insideQuoteStringLiteral = false;
        this.insideAlternateQuoteStringLiteral = false;
        this.lineEndsWithSingleLineComment = false;
        this.insideMultiLineComment = false;
        this.nonCommentStatementPartSeen = false;
        this.delimiter = this.getDefaultDelimiter();
    }
    
    protected Delimiter getDefaultDelimiter() {
        return new Delimiter(";", false);
    }
    
    public void setLineNumber(final int lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    public void setDelimiter(final Delimiter delimiter) {
        this.delimiter = delimiter;
    }
    
    public boolean isEmpty() {
        return this.empty;
    }
    
    public boolean isTerminated() {
        return this.terminated;
    }
    
    public SqlStatement getSqlStatement() {
        final String sql = this.statement.toString();
        return new SqlStatement(this.lineNumber, sql, this.isPgCopyFromStdIn());
    }
    
    public Delimiter extractNewDelimiterFromLine(final String line) {
        return null;
    }
    
    public boolean isPgCopyFromStdIn() {
        return false;
    }
    
    public boolean isCommentDirective(final String line) {
        return false;
    }
    
    protected boolean isSingleLineComment(final String line) {
        return line.startsWith("--");
    }
    
    public void addLine(final String line) {
        if (this.isEmpty()) {
            this.empty = false;
        }
        else {
            this.statement.append("\n");
        }
        if (this.isCommentDirective(line.trim())) {
            this.nonCommentStatementPartSeen = true;
        }
        final String lineSimplified = this.simplifyLine(line);
        this.applyStateChanges(lineSimplified);
        if (this.endWithOpenMultilineStringLiteral() || this.insideMultiLineComment) {
            this.statement.append(line);
            return;
        }
        this.delimiter = this.changeDelimiterIfNecessary(lineSimplified, this.delimiter);
        this.statement.append(line);
        if (!this.lineEndsWithSingleLineComment && this.lineTerminatesStatement(lineSimplified, this.delimiter)) {
            stripDelimiter(this.statement, this.delimiter);
            this.terminated = true;
        }
    }
    
    boolean endWithOpenMultilineStringLiteral() {
        return this.insideQuoteStringLiteral || this.insideAlternateQuoteStringLiteral;
    }
    
    public boolean canDiscard() {
        return !this.insideAlternateQuoteStringLiteral && !this.insideQuoteStringLiteral && !this.insideMultiLineComment && !this.nonCommentStatementPartSeen;
    }
    
    protected String simplifyLine(final String line) {
        return this.removeEscapedQuotes(line).replace("--", " -- ").replace("/*", " /* ").replace("*/", " */ ").replaceAll("\\s+", " ").trim().toUpperCase();
    }
    
    protected Delimiter changeDelimiterIfNecessary(final String line, final Delimiter delimiter) {
        return delimiter;
    }
    
    private boolean lineTerminatesStatement(final String line, final Delimiter delimiter) {
        if (delimiter == null) {
            return false;
        }
        final String upperCaseDelimiter = delimiter.getDelimiter().toUpperCase();
        if (delimiter.isAloneOnLine()) {
            return line.equals(upperCaseDelimiter);
        }
        return line.endsWith(upperCaseDelimiter);
    }
    
    static void stripDelimiter(final StringBuilder sql, final Delimiter delimiter) {
        int last;
        for (last = sql.length(); last > 0 && Character.isWhitespace(sql.charAt(last - 1)); --last) {}
        sql.delete(last - delimiter.getDelimiter().length(), sql.length());
    }
    
    protected String extractAlternateOpenQuote(final String token) {
        return null;
    }
    
    protected String computeAlternateCloseQuote(final String openQuote) {
        return openQuote;
    }
    
    protected void applyStateChanges(final String line) {
        final String[] tokens = StringUtils.tokenizeToStringArray(line, " @<>;:=|(),+{}");
        final List<TokenType> delimitingTokens = this.extractStringLiteralDelimitingTokens(tokens);
        this.lineEndsWithSingleLineComment = false;
        for (final TokenType delimitingToken : delimitingTokens) {
            if (!this.insideQuoteStringLiteral && !this.insideAlternateQuoteStringLiteral && TokenType.MULTI_LINE_COMMENT_OPEN.equals(delimitingToken)) {
                this.insideMultiLineComment = true;
            }
            if (!this.insideQuoteStringLiteral && !this.insideAlternateQuoteStringLiteral && TokenType.MULTI_LINE_COMMENT_CLOSE.equals(delimitingToken)) {
                this.insideMultiLineComment = false;
            }
            if (!this.insideQuoteStringLiteral && !this.insideAlternateQuoteStringLiteral && !this.insideMultiLineComment && TokenType.SINGLE_LINE_COMMENT.equals(delimitingToken)) {
                this.lineEndsWithSingleLineComment = true;
                return;
            }
            if (!this.insideMultiLineComment && !this.insideQuoteStringLiteral && TokenType.ALTERNATE_QUOTE.equals(delimitingToken)) {
                this.insideAlternateQuoteStringLiteral = !this.insideAlternateQuoteStringLiteral;
            }
            if (!this.insideMultiLineComment && !this.insideAlternateQuoteStringLiteral && TokenType.QUOTE.equals(delimitingToken)) {
                this.insideQuoteStringLiteral = !this.insideQuoteStringLiteral;
            }
            if (this.insideMultiLineComment || this.insideQuoteStringLiteral || this.insideAlternateQuoteStringLiteral || !TokenType.OTHER.equals(delimitingToken)) {
                continue;
            }
            this.nonCommentStatementPartSeen = true;
        }
    }
    
    private List<TokenType> extractStringLiteralDelimitingTokens(final String[] tokens) {
        final List<TokenType> delimitingTokens = new ArrayList<TokenType>();
        for (final String token : tokens) {
            final String cleanToken = this.cleanToken(token);
            boolean handled = false;
            Label_0396: {
                if (this.alternateQuote == null) {
                    final String alternateQuoteFromToken = this.extractAlternateOpenQuote(cleanToken);
                    if (alternateQuoteFromToken != null) {
                        final String closeQuote = this.computeAlternateCloseQuote(alternateQuoteFromToken);
                        if (cleanToken.length() >= alternateQuoteFromToken.length() + closeQuote.length() && cleanToken.startsWith(alternateQuoteFromToken) && cleanToken.endsWith(closeQuote)) {
                            break Label_0396;
                        }
                        this.alternateQuote = closeQuote;
                        delimitingTokens.add(TokenType.ALTERNATE_QUOTE);
                        break Label_0396;
                    }
                }
                if (this.alternateQuote != null && cleanToken.endsWith(this.alternateQuote)) {
                    this.alternateQuote = null;
                    delimitingTokens.add(TokenType.ALTERNATE_QUOTE);
                }
                else if (cleanToken.length() < 2 || !cleanToken.startsWith("'") || !cleanToken.endsWith("'")) {
                    if (cleanToken.length() >= 4) {
                        final int numberOfOpeningMultiLineComments = StringUtils.countOccurrencesOf(cleanToken, "/*");
                        final int numberOfClosingMultiLineComments = StringUtils.countOccurrencesOf(cleanToken, "*/");
                        if (numberOfOpeningMultiLineComments > 0 && numberOfOpeningMultiLineComments == numberOfClosingMultiLineComments) {
                            break Label_0396;
                        }
                    }
                    if (this.isSingleLineComment(cleanToken)) {
                        delimitingTokens.add(TokenType.SINGLE_LINE_COMMENT);
                        handled = true;
                    }
                    if (cleanToken.contains("/*")) {
                        delimitingTokens.add(TokenType.MULTI_LINE_COMMENT_OPEN);
                        handled = true;
                    }
                    else if (cleanToken.startsWith("'")) {
                        delimitingTokens.add(TokenType.QUOTE);
                        handled = true;
                    }
                    if (!cleanToken.contains("/*") && cleanToken.contains("*/")) {
                        delimitingTokens.add(TokenType.MULTI_LINE_COMMENT_CLOSE);
                        handled = true;
                    }
                    else if (!cleanToken.startsWith("'") && cleanToken.endsWith("'")) {
                        delimitingTokens.add(TokenType.QUOTE);
                        handled = true;
                    }
                    if (!handled) {
                        delimitingTokens.add(TokenType.OTHER);
                    }
                }
            }
        }
        return delimitingTokens;
    }
    
    protected String removeEscapedQuotes(final String token) {
        return StringUtils.replaceAll(token, "''", "");
    }
    
    protected String cleanToken(final String token) {
        return token;
    }
    
    private enum TokenType
    {
        OTHER, 
        QUOTE, 
        ALTERNATE_QUOTE, 
        SINGLE_LINE_COMMENT, 
        MULTI_LINE_COMMENT_OPEN, 
        MULTI_LINE_COMMENT_CLOSE;
    }
}
