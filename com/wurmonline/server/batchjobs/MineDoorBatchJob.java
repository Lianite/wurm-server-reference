// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.batchjobs;

import com.wurmonline.server.creatures.MineDoorPermission;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MineDoorBatchJob
{
    private static Logger logger;
    
    public static final void convertToNewPermissions() {
        MineDoorBatchJob.logger.log(Level.INFO, "Converting Mine Doors to New Permission System.");
        int minedoorsDone = 0;
        for (final MineDoorPermission md : MineDoorPermission.getAllMineDoors()) {
            if (md.convertToNewPermissions()) {
                ++minedoorsDone;
            }
        }
        MineDoorBatchJob.logger.log(Level.INFO, "Converted " + minedoorsDone + " Mine Doors to New Permissions System.");
    }
    
    static {
        MineDoorBatchJob.logger = Logger.getLogger(MineDoorBatchJob.class.getName());
    }
}
