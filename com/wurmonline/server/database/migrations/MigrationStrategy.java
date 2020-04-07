// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.database.migrations;

public interface MigrationStrategy
{
    MigrationResult migrate();
    
    boolean hasPendingMigrations();
}
