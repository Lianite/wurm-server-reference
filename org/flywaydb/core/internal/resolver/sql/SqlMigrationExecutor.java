// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.resolver.sql;

import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.SqlScript;
import java.sql.Connection;
import org.flywaydb.core.internal.util.scanner.Resource;
import org.flywaydb.core.internal.util.PlaceholderReplacer;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.api.resolver.MigrationExecutor;

public class SqlMigrationExecutor implements MigrationExecutor
{
    private final DbSupport dbSupport;
    private final PlaceholderReplacer placeholderReplacer;
    private final Resource sqlScriptResource;
    private final String encoding;
    
    public SqlMigrationExecutor(final DbSupport dbSupport, final Resource sqlScriptResource, final PlaceholderReplacer placeholderReplacer, final String encoding) {
        this.dbSupport = dbSupport;
        this.sqlScriptResource = sqlScriptResource;
        this.encoding = encoding;
        this.placeholderReplacer = placeholderReplacer;
    }
    
    @Override
    public void execute(final Connection connection) {
        final SqlScript sqlScript = new SqlScript(this.dbSupport, this.sqlScriptResource, this.placeholderReplacer, this.encoding);
        sqlScript.execute(new JdbcTemplate(connection, 0));
    }
    
    @Override
    public boolean executeInTransaction() {
        return true;
    }
}
