// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport.solid;

import org.flywaydb.core.internal.dbsupport.Delimiter;
import org.flywaydb.core.internal.dbsupport.SqlStatementBuilder;

public class SolidSqlStatementBuilder extends SqlStatementBuilder
{
    public Delimiter changeDelimiterIfNecessary(final String line, final Delimiter delimiter) {
        if (line.startsWith("\"")) {
            return new Delimiter("\"", false);
        }
        if (line.endsWith("\";")) {
            return this.getDefaultDelimiter();
        }
        return delimiter;
    }
}
