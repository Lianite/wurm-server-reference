// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.database;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.sql.Connection;
import java.io.File;
import java.util.Locale;
import org.sqlite.SQLiteDataSource;
import java.nio.file.Path;
import org.sqlite.SQLiteConfig;

public class SqliteConnectionFactory extends ConnectionFactory
{
    private final SQLiteConfig config;
    private final Path fileDirectory;
    private final Path filePath;
    private final SQLiteDataSource dataSource;
    
    private static String buildFilename(final WurmDatabaseSchema schema) {
        return schema.getDatabase().toLowerCase(Locale.ENGLISH) + ".db";
    }
    
    private static Path sqliteDirectory(final String worldDirectory) {
        return new File(worldDirectory).toPath().resolve("sqlite");
    }
    
    private static String buildUrl(final String directory, final WurmDatabaseSchema schema) {
        return "jdbc:sqlite:" + directory + "/sqlite/" + schema.getDatabase().toLowerCase(Locale.ENGLISH) + ".db";
    }
    
    public SqliteConnectionFactory(final String worldDirectory, final WurmDatabaseSchema schema, final SQLiteConfig config) {
        super(buildUrl(worldDirectory, schema), schema);
        this.fileDirectory = sqliteDirectory(worldDirectory);
        this.filePath = this.fileDirectory.resolve(buildFilename(schema));
        this.config = config;
        (this.dataSource = new SQLiteDataSource(config)).setUrl(this.getUrl());
    }
    
    @Override
    public Connection createConnection() throws SQLException {
        return this.dataSource.getConnection();
    }
    
    @Override
    public boolean isValid(@Nullable final Connection con) throws SQLException {
        return con != null;
    }
    
    @Override
    public boolean isStale(final long lastUsed, @Nullable final Connection connection) throws SQLException {
        return System.currentTimeMillis() - lastUsed > 3600000L;
    }
    
    public Path getFilePath() {
        return this.filePath;
    }
    
    public Path getFileDirectory() {
        return this.fileDirectory;
    }
    
    public SQLiteDataSource getDataSource() {
        return this.dataSource;
    }
}
