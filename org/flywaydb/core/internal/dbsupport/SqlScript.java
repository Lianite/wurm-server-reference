// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport;

import org.flywaydb.core.internal.util.logging.LogFactory;
import java.io.IOException;
import org.flywaydb.core.api.FlywayException;
import java.io.BufferedReader;
import org.flywaydb.core.internal.util.StringUtils;
import java.util.ArrayList;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.sql.SQLException;
import org.flywaydb.core.internal.util.PlaceholderReplacer;
import org.flywaydb.core.internal.util.scanner.Resource;
import java.util.List;
import org.flywaydb.core.internal.util.logging.Log;

public class SqlScript
{
    private static final Log LOG;
    private final DbSupport dbSupport;
    private final List<SqlStatement> sqlStatements;
    private final Resource resource;
    
    public SqlScript(final String sqlScriptSource, final DbSupport dbSupport) {
        this.dbSupport = dbSupport;
        this.sqlStatements = this.parse(sqlScriptSource);
        this.resource = null;
    }
    
    public SqlScript(final DbSupport dbSupport, final Resource sqlScriptResource, final PlaceholderReplacer placeholderReplacer, final String encoding) {
        this.dbSupport = dbSupport;
        final String sqlScriptSource = sqlScriptResource.loadAsString(encoding);
        this.sqlStatements = this.parse(placeholderReplacer.replacePlaceholders(sqlScriptSource));
        this.resource = sqlScriptResource;
    }
    
    public List<SqlStatement> getSqlStatements() {
        return this.sqlStatements;
    }
    
    public Resource getResource() {
        return this.resource;
    }
    
    public void execute(final JdbcTemplate jdbcTemplate) {
        for (final SqlStatement sqlStatement : this.sqlStatements) {
            final String sql = sqlStatement.getSql();
            SqlScript.LOG.debug("Executing SQL: " + sql);
            try {
                if (sqlStatement.isPgCopy()) {
                    this.dbSupport.executePgCopy(jdbcTemplate.getConnection(), sql);
                }
                else {
                    jdbcTemplate.executeStatement(sql);
                }
            }
            catch (SQLException e) {
                throw new FlywaySqlScriptException(this.resource, sqlStatement, e);
            }
        }
    }
    
    List<SqlStatement> parse(final String sqlScriptSource) {
        return this.linesToStatements(this.readLines(new StringReader(sqlScriptSource)));
    }
    
    List<SqlStatement> linesToStatements(final List<String> lines) {
        final List<SqlStatement> statements = new ArrayList<SqlStatement>();
        Delimiter nonStandardDelimiter = null;
        SqlStatementBuilder sqlStatementBuilder = this.dbSupport.createSqlStatementBuilder();
        for (int lineNumber = 1; lineNumber <= lines.size(); ++lineNumber) {
            final String line = lines.get(lineNumber - 1);
            if (sqlStatementBuilder.isEmpty()) {
                if (!StringUtils.hasText(line)) {
                    continue;
                }
                final Delimiter newDelimiter = sqlStatementBuilder.extractNewDelimiterFromLine(line);
                if (newDelimiter != null) {
                    nonStandardDelimiter = newDelimiter;
                    continue;
                }
                sqlStatementBuilder.setLineNumber(lineNumber);
                if (nonStandardDelimiter != null) {
                    sqlStatementBuilder.setDelimiter(nonStandardDelimiter);
                }
            }
            sqlStatementBuilder.addLine(line);
            if (sqlStatementBuilder.canDiscard()) {
                sqlStatementBuilder = this.dbSupport.createSqlStatementBuilder();
            }
            else if (sqlStatementBuilder.isTerminated()) {
                final SqlStatement sqlStatement = sqlStatementBuilder.getSqlStatement();
                statements.add(sqlStatement);
                SqlScript.LOG.debug("Found statement at line " + sqlStatement.getLineNumber() + ": " + sqlStatement.getSql());
                sqlStatementBuilder = this.dbSupport.createSqlStatementBuilder();
            }
        }
        if (!sqlStatementBuilder.isEmpty()) {
            statements.add(sqlStatementBuilder.getSqlStatement());
        }
        return statements;
    }
    
    private List<String> readLines(final Reader reader) {
        final List<String> lines = new ArrayList<String>();
        final BufferedReader bufferedReader = new BufferedReader(reader);
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
        }
        catch (IOException e) {
            final String message = (this.resource == null) ? "Unable to parse lines" : ("Unable to parse " + this.resource.getLocation() + " (" + this.resource.getLocationOnDisk() + ")");
            throw new FlywayException(message, e);
        }
        return lines;
    }
    
    static {
        LOG = LogFactory.getLog(SqlScript.class);
    }
}
