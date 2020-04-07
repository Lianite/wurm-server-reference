// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.sql.Connection;
import com.wurmonline.server.database.WurmDatabaseSchema;
import java.util.logging.Logger;

public final class DbConnectorPooled extends DbConnector
{
    private static final Logger logger;
    
    private DbConnectorPooled(final String driver, final String host, final String port, final WurmDatabaseSchema schema, final String user, final String password, final String loggingName) {
        super(driver, host, port, schema, user, password, loggingName);
    }
    
    public static void returnConnection(final Connection aConnection) {
        DbConnectorPooled.logger.warning("The DbConnectorPooled is just a place holder and should not be used yet. Check the wurm.ini. Make sure that USE_POOLED_DB=false is there.");
    }
    
    static {
        logger = Logger.getLogger(DbConnectorPooled.class.getName());
    }
}
