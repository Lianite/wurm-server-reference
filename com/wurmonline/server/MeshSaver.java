// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.mesh.MeshIO;
import java.util.logging.Logger;

final class MeshSaver implements Runnable
{
    private static final Logger logger;
    private final MeshIO iMapLayer;
    private final String iMapLayerName;
    private final int iNumberOfRowsPerCall;
    
    MeshSaver(final MeshIO aMapLayerToSave, final String aMapLayerName, final int aNumberOfRowsPerCall) {
        this.iMapLayer = aMapLayerToSave;
        this.iMapLayerName = aMapLayerName;
        this.iNumberOfRowsPerCall = aNumberOfRowsPerCall;
        MeshSaver.logger.info("Created MeshSaver for map layer: '" + this.iMapLayerName + "', " + aMapLayerToSave + ", rowsPerCall: " + aNumberOfRowsPerCall);
    }
    
    MeshSaver(final MeshIO aMapLayerToSave, final String aMapLayerName) {
        this(aMapLayerToSave, aMapLayerName, 1);
    }
    
    @Override
    public void run() {
        if (MeshSaver.logger.isLoggable(Level.FINEST)) {
            MeshSaver.logger.finest("Running MeshSaver for calling MeshIO.saveDirtyRow() for '" + this.iMapLayerName + "', " + this.iMapLayer + ", rowsPerCall: " + this.iNumberOfRowsPerCall);
        }
        try {
            final long now = System.nanoTime();
            int numberOfRowsSaved = this.iNumberOfRowsPerCall;
            if (this.iNumberOfRowsPerCall <= 0) {
                numberOfRowsSaved = this.iMapLayer.saveAllDirtyRows();
            }
            else if (this.iNumberOfRowsPerCall > this.iMapLayer.getSize()) {
                this.iMapLayer.saveAll();
            }
            else {
                for (int i = 0; i < this.iNumberOfRowsPerCall; ++i) {
                    if (this.iMapLayer.saveNextDirtyRow()) {
                        break;
                    }
                }
            }
            final float lElapsedTime = (System.nanoTime() - now) / 1000000.0f;
            if (lElapsedTime > Constants.lagThreshold || MeshSaver.logger.isLoggable(Level.FINER)) {
                MeshSaver.logger.info("Finished saving " + numberOfRowsSaved + " rows for '" + this.iMapLayerName + "', which took " + lElapsedTime + " millis.");
            }
        }
        catch (RuntimeException e) {
            MeshSaver.logger.log(Level.WARNING, "Caught exception in MeshSaver while saving Mesh for '" + this.iMapLayerName + "' " + this.iMapLayer, e);
            throw e;
        }
        catch (IOException e2) {
            MeshSaver.logger.log(Level.WARNING, "Caught exception in MeshSaver while saving Mesh for '" + this.iMapLayerName + "' " + this.iMapLayer, e2);
            throw new RuntimeException(e2);
        }
    }
    
    static {
        logger = Logger.getLogger(MeshSaver.class.getName());
    }
}
