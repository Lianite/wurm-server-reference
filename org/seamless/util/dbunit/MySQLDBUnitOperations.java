// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util.dbunit;

import org.dbunit.database.IDatabaseConnection;

public abstract class MySQLDBUnitOperations extends DBUnitOperations
{
    protected void disableReferentialIntegrity(final IDatabaseConnection con) {
        try {
            con.getConnection().prepareStatement("set foreign_key_checks=0").execute();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    protected void enableReferentialIntegrity(final IDatabaseConnection con) {
        try {
            con.getConnection().prepareStatement("set foreign_key_checks=1").execute();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
