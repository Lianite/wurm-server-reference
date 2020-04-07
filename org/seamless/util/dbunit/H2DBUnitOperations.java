// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util.dbunit;

import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;

public abstract class H2DBUnitOperations extends DBUnitOperations
{
    protected void disableReferentialIntegrity(final IDatabaseConnection con) {
        try {
            con.getConnection().prepareStatement("set referential_integrity FALSE").execute();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    protected void enableReferentialIntegrity(final IDatabaseConnection con) {
        try {
            con.getConnection().prepareStatement("set referential_integrity TRUE").execute();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    protected void editConfig(final DatabaseConfig config) {
        super.editConfig(config);
        config.setProperty("http://www.dbunit.org/properties/datatypeFactory", (Object)new DefaultDataTypeFactory() {
            public DataType createDataType(final int sqlType, final String sqlTypeName) throws DataTypeException {
                if (sqlType == 16) {
                    return DataType.BOOLEAN;
                }
                return super.createDataType(sqlType, sqlTypeName);
            }
        });
    }
}
