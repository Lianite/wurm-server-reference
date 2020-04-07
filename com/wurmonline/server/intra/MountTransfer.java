// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.intra;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Map;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;

public final class MountTransfer implements MiscConstants, TimeConstants
{
    private static final Map<Long, MountTransfer> transfers;
    private static final Map<Long, MountTransfer> transfersPerCreature;
    private final Map<Long, Integer> seats;
    private static final Logger logger;
    private final long vehicleid;
    private final long pilotid;
    private final long creationTime;
    
    public MountTransfer(final long vehicleId, final long pilotId) {
        this.seats = new HashMap<Long, Integer>();
        this.vehicleid = vehicleId;
        this.pilotid = pilotId;
        this.creationTime = System.currentTimeMillis();
        MountTransfer.transfers.put(vehicleId, this);
    }
    
    public void addToSeat(final long wid, final int seatid) {
        if (MountTransfer.logger.isLoggable(Level.FINER)) {
            MountTransfer.logger.finer("Adding " + wid + ", seat=" + seatid);
        }
        this.seats.put(wid, seatid);
        MountTransfer.transfersPerCreature.put(wid, this);
    }
    
    public int getSeatFor(final long wurmid) {
        if (this.seats.keySet().contains(wurmid)) {
            return this.seats.get(wurmid);
        }
        return -1;
    }
    
    public void remove(final long wurmid) {
        if (MountTransfer.logger.isLoggable(Level.FINER)) {
            MountTransfer.logger.finer("Removing " + wurmid);
        }
        this.seats.remove(wurmid);
        MountTransfer.transfersPerCreature.remove(wurmid);
        if (this.seats.isEmpty()) {
            this.clearAndRemove();
        }
    }
    
    long getCreationTime() {
        return this.creationTime;
    }
    
    private void clearAndRemove() {
        final Iterator<Long> seatIt = this.seats.keySet().iterator();
        while (seatIt.hasNext()) {
            MountTransfer.transfersPerCreature.remove(seatIt.next());
        }
        MountTransfer.transfers.remove(this.vehicleid);
        this.seats.clear();
    }
    
    public long getVehicleId() {
        return this.vehicleid;
    }
    
    public long getPilotId() {
        return this.pilotid;
    }
    
    public static final MountTransfer getTransferFor(final long wurmid) {
        return MountTransfer.transfersPerCreature.get(wurmid);
    }
    
    public static final void pruneTransfers() {
        final Set<MountTransfer> toRemove = new HashSet<MountTransfer>();
        for (final MountTransfer mt : MountTransfer.transfers.values()) {
            if (System.currentTimeMillis() - mt.getCreationTime() > 1800000L) {
                toRemove.add(mt);
            }
        }
        final Iterator<MountTransfer> it2 = toRemove.iterator();
        while (it2.hasNext()) {
            it2.next().clearAndRemove();
        }
    }
    
    static {
        transfers = new HashMap<Long, MountTransfer>();
        transfersPerCreature = new HashMap<Long, MountTransfer>();
        logger = Logger.getLogger(MountTransfer.class.getName());
    }
}
