// 
// Decompiled by Procyon v0.5.30
// 

package org.seamless.util.dbunit;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.InputStream;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.database.DatabaseConfig;
import java.sql.Connection;
import org.dbunit.database.DatabaseConnection;
import java.util.Iterator;
import org.dbunit.database.IDatabaseConnection;
import java.util.logging.Level;
import javax.sql.DataSource;
import java.util.logging.Logger;
import java.util.ArrayList;

public abstract class DBUnitOperations extends ArrayList<Op>
{
    private static final Logger log;
    
    public abstract DataSource getDataSource();
    
    public void execute() {
        DBUnitOperations.log.info("Executing DBUnit operations: " + this.size());
        IDatabaseConnection con = null;
        try {
            con = this.getConnection();
            this.disableReferentialIntegrity(con);
            for (final Op op : this) {
                op.execute(con);
            }
            this.enableReferentialIntegrity(con);
        }
        finally {
            if (con != null) {
                try {
                    con.close();
                }
                catch (Exception ex) {
                    DBUnitOperations.log.log(Level.WARNING, "Failed to close connection after DBUnit operation: " + ex, ex);
                }
            }
        }
    }
    
    protected IDatabaseConnection getConnection() {
        try {
            final DataSource datasource = this.getDataSource();
            final Connection con = datasource.getConnection();
            final IDatabaseConnection dbUnitCon = (IDatabaseConnection)new DatabaseConnection(con);
            this.editConfig(dbUnitCon.getConfig());
            return dbUnitCon;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    protected abstract void disableReferentialIntegrity(final IDatabaseConnection p0);
    
    protected abstract void enableReferentialIntegrity(final IDatabaseConnection p0);
    
    protected void editConfig(final DatabaseConfig config) {
    }
    
    static {
        log = Logger.getLogger(DBUnitOperations.class.getName());
    }
    
    public abstract static class Op
    {
        ReplacementDataSet dataSet;
        DatabaseOperation operation;
        
        public Op(final String dataLocation) {
            this(dataLocation, null, DatabaseOperation.CLEAN_INSERT);
        }
        
        public Op(final String dataLocation, final String dtdLocation) {
            this(dataLocation, dtdLocation, DatabaseOperation.CLEAN_INSERT);
        }
        
        public Op(final String dataLocation, final String dtdLocation, final DatabaseOperation operation) {
            try {
                ReplacementDataSet dataSet;
                if (dtdLocation != null) {
                    final FlatXmlDataSet set;
                    dataSet = new ReplacementDataSet((IDataSet)set);
                    set = new FlatXmlDataSet(this.openStream(dataLocation), this.openStream(dtdLocation));
                }
                else {
                    final FlatXmlDataSet set2;
                    dataSet = new ReplacementDataSet((IDataSet)set2);
                    set2 = new FlatXmlDataSet(this.openStream(dataLocation));
                }
                this.dataSet = dataSet;
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            this.dataSet.addReplacementObject((Object)"[NULL]", (Object)null);
            this.operation = operation;
        }
        
        public IDataSet getDataSet() {
            return (IDataSet)this.dataSet;
        }
        
        public DatabaseOperation getOperation() {
            return this.operation;
        }
        
        public void execute(final IDatabaseConnection connection) {
            try {
                this.operation.execute(connection, (IDataSet)this.dataSet);
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        
        protected abstract InputStream openStream(final String p0);
    }
    
    public static class ClasspathOp extends Op
    {
        public ClasspathOp(final String dataLocation) {
            super(dataLocation);
        }
        
        public ClasspathOp(final String dataLocation, final String dtdLocation) {
            super(dataLocation, dtdLocation);
        }
        
        public ClasspathOp(final String dataLocation, final String dtdLocation, final DatabaseOperation operation) {
            super(dataLocation, dtdLocation, operation);
        }
        
        protected InputStream openStream(final String location) {
            return Thread.currentThread().getContextClassLoader().getResourceAsStream(location);
        }
    }
    
    public class FileOp extends Op
    {
        public FileOp(final String dataLocation) {
            super(dataLocation);
        }
        
        public FileOp(final String dataLocation, final String dtdLocation) {
            super(dataLocation, dtdLocation);
        }
        
        public FileOp(final String dataLocation, final String dtdLocation, final DatabaseOperation operation) {
            super(dataLocation, dtdLocation, operation);
        }
        
        protected InputStream openStream(final String location) {
            try {
                return new FileInputStream(location);
            }
            catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
