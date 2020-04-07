// 
// Decompiled by Procyon v0.5.30
// 

package org.flywaydb.core.internal.dbsupport;

import org.flywaydb.core.internal.util.StringUtils;
import java.sql.SQLException;
import org.flywaydb.core.internal.util.scanner.Resource;
import org.flywaydb.core.api.FlywayException;

public class FlywaySqlScriptException extends FlywayException
{
    private final Resource resource;
    private final SqlStatement statement;
    
    public FlywaySqlScriptException(final Resource resource, final SqlStatement statement, final SQLException sqlException) {
        super(sqlException);
        this.resource = resource;
        this.statement = statement;
    }
    
    public int getLineNumber() {
        return this.statement.getLineNumber();
    }
    
    public String getStatement() {
        return this.statement.getSql();
    }
    
    @Override
    public String getMessage() {
        final String title = (this.resource == null) ? "Script failed" : ("Migration " + this.resource.getFilename() + " failed");
        final String underline = StringUtils.trimOrPad("", title.length(), '-');
        SQLException cause;
        for (cause = (SQLException)this.getCause(); cause.getNextException() != null; cause = cause.getNextException()) {}
        String message = "\n" + title + "\n" + underline + "\n";
        message = message + "SQL State  : " + cause.getSQLState() + "\n";
        message = message + "Error Code : " + cause.getErrorCode() + "\n";
        if (cause.getMessage() != null) {
            message = message + "Message    : " + cause.getMessage().trim() + "\n";
        }
        if (this.resource != null) {
            message = message + "Location   : " + this.resource.getLocation() + " (" + this.resource.getLocationOnDisk() + ")\n";
        }
        message = message + "Line       : " + this.getLineNumber() + "\n";
        message = message + "Statement  : " + this.getStatement() + "\n";
        return message;
    }
}
