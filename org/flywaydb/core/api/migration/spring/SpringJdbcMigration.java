// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.api.migration.spring;

import org.springframework.jdbc.core.JdbcTemplate;

public interface SpringJdbcMigration
{
    void migrate(final JdbcTemplate p0) throws Exception;
}
