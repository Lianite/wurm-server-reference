// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.epic.EpicMission;
import com.wurmonline.server.ServerEntry;
import java.io.IOException;
import com.wurmonline.server.epic.Effectuator;
import java.text.DateFormat;
import java.util.Date;
import com.wurmonline.server.epic.EpicServerStatus;
import com.wurmonline.server.Server;
import com.wurmonline.server.Servers;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.deities.Deities;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.wurmonline.server.epic.EpicEntity;
import com.wurmonline.server.epic.HexMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class WcEpicStatusReport extends WebCommand
{
    private static final Logger logger;
    private boolean success;
    private boolean entityStatusMessage;
    private long entityId;
    private byte missionType;
    private int missionDifficulty;
    private final Map<String, Integer> entityStatuses;
    private final Map<Integer, Integer> entityHexes;
    private final Map<Integer, String> entityMap;
    
    public WcEpicStatusReport(final long aId, final boolean wasSuccess, final int epicEntityId, final byte type, final int difficulty) {
        super(aId, (short)10);
        this.success = false;
        this.entityStatusMessage = false;
        this.entityId = 0L;
        this.missionType = -1;
        this.missionDifficulty = -1;
        this.entityStatuses = new HashMap<String, Integer>();
        this.entityHexes = new HashMap<Integer, Integer>();
        this.entityMap = new HashMap<Integer, String>();
        this.success = wasSuccess;
        this.entityId = epicEntityId;
        this.missionType = type;
        this.missionDifficulty = difficulty;
        this.isRestrictedEpic = true;
    }
    
    public WcEpicStatusReport(final long aId, final byte[] _data) {
        super(aId, (short)10, _data);
        this.success = false;
        this.entityStatusMessage = false;
        this.entityId = 0L;
        this.missionType = -1;
        this.missionDifficulty = -1;
        this.entityStatuses = new HashMap<String, Integer>();
        this.entityHexes = new HashMap<Integer, Integer>();
        this.entityMap = new HashMap<Integer, String>();
        this.isRestrictedEpic = true;
    }
    
    public final void addEntityStatus(final String status, final int statusEntityId) {
        this.entityStatuses.put(status, statusEntityId);
        this.entityStatusMessage = true;
    }
    
    public final void addEntityHex(final int entity, final int hexId) {
        this.entityHexes.put(entity, hexId);
    }
    
    public final void fillStatusReport(final HexMap map) {
        final EpicEntity[] allEntities;
        final EpicEntity[] entities = allEntities = map.getAllEntities();
        for (final EpicEntity entity : allEntities) {
            if (entity.isDeity()) {
                this.entityMap.put((int)entity.getId(), entity.getName());
            }
            this.addEntityStatus(entity.getLocationStatus(), (int)entity.getId());
            this.addEntityStatus(entity.getEnemyStatus(), (int)entity.getId());
            final int collsCarried = entity.countCollectables();
            if (collsCarried > 0) {
                this.addEntityStatus(entity.getName() + " is carrying " + collsCarried + " of the " + entity.getCollectibleName() + ".", (int)entity.getId());
            }
            if (entity.isDeity() && entity.getMapHex() != null) {
                this.addEntityHex((int)entity.getId(), entity.getMapHex().getId());
            }
        }
    }
    
    @Override
    public boolean autoForward() {
        return true;
    }
    
    @Override
    byte[] encode() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] barr = null;
        try {
            dos = new DataOutputStream(bos);
            dos.writeBoolean(this.entityStatusMessage);
            if (this.entityStatusMessage) {
                dos.writeInt(this.entityMap.size());
                for (final Map.Entry<Integer, String> entry : this.entityMap.entrySet()) {
                    dos.writeInt(entry.getKey());
                    dos.writeUTF(entry.getValue());
                }
                dos.writeInt(this.entityStatuses.size());
                if (this.entityStatuses.size() > 0) {
                    for (final Map.Entry<String, Integer> entry2 : this.entityStatuses.entrySet()) {
                        dos.writeUTF(entry2.getKey());
                        dos.writeInt(entry2.getValue());
                    }
                }
                dos.writeInt(this.entityHexes.size());
                if (this.entityHexes.size() > 0) {
                    for (final Map.Entry<Integer, Integer> entry3 : this.entityHexes.entrySet()) {
                        dos.writeInt(entry3.getKey());
                        dos.writeInt(entry3.getValue());
                    }
                }
            }
            else {
                dos.writeBoolean(this.success);
                dos.writeLong(this.entityId);
                dos.writeByte(this.missionType);
                dos.writeInt(this.missionDifficulty);
            }
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcEpicStatusReport.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeOutputStreamIgnoreExceptions(dos);
            barr = bos.toByteArray();
            StreamUtilities.closeOutputStreamIgnoreExceptions(bos);
            this.setData(barr);
        }
        return barr;
    }
    
    @Override
    public void execute() {
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new ByteArrayInputStream(this.getData()));
            this.entityStatusMessage = dis.readBoolean();
            if (this.entityStatusMessage) {
                Deities.clearValreiStatuses();
                for (int numsx = dis.readInt(), x = 0; x < numsx; ++x) {
                    final int entity = dis.readInt();
                    final String name = dis.readUTF();
                    Deities.addEntity(entity, name);
                }
                for (int nums = dis.readInt(), x2 = 0; x2 < nums; ++x2) {
                    final String status = dis.readUTF();
                    final int entity2 = dis.readInt();
                    Deities.addStatus(status, entity2);
                }
                for (int numsPos = dis.readInt(), x3 = 0; x3 < numsPos; ++x3) {
                    final int entity2 = dis.readInt();
                    final int hexPos = dis.readInt();
                    Deities.addPosition(entity2, hexPos);
                }
            }
            else {
                this.success = dis.readBoolean();
                this.entityId = dis.readLong();
                this.missionType = dis.readByte();
                this.missionDifficulty = dis.readInt();
                final ServerEntry entry = Servers.getServerWithId(WurmId.getOrigin(this.getWurmId()));
                if (entry != null && Server.getEpicMap() != null) {
                    if (entry.EPIC) {
                        final EpicMission mission = EpicServerStatus.getEpicMissionForEntity((int)this.entityId);
                        if (mission != null) {
                            if (!Servers.localServer.EPIC && this.success) {
                                final float oldStatus = mission.getMissionProgress();
                                mission.updateProgress(oldStatus + 1.0f);
                            }
                            final EpicEntity entity3 = Server.getEpicMap().getEntity(this.entityId);
                            if (entity3 != null) {
                                final Date now = new Date();
                                final DateFormat format = DateFormat.getDateInstance(3);
                                if (this.success) {
                                    Server.getEpicMap().broadCast(entity3.getName() + " received help from " + entry.name + ". " + format.format(now) + " " + Server.rand.nextInt(1000));
                                    Server.getEpicMap().setEntityHelped(this.entityId, this.missionType, this.missionDifficulty);
                                }
                                else {
                                    Server.getEpicMap().broadCast(entity3.getName() + " never received help from " + entry.name + ". " + format.format(now) + " " + Server.rand.nextInt(1000));
                                    if (entity3.isDeity()) {
                                        entity3.setShouldCreateMission(true, false);
                                    }
                                }
                            }
                        }
                        else {
                            final EpicEntity entity3 = Server.getEpicMap().getEntity(this.entityId);
                            final Date now = new Date();
                            final DateFormat format = DateFormat.getDateInstance(3);
                            Server.getEpicMap().broadCast(entity3.getName() + " did not have an active mission when receiving help from " + entry.name + ". " + format.format(now) + " " + Server.rand.nextInt(1000));
                            entity3.setShouldCreateMission(true, false);
                        }
                    }
                    else if (this.success) {
                        final EpicEntity entity4 = Server.getEpicMap().getEntity(this.entityId);
                        if (entity4 != null) {
                            final int effect = Server.rand.nextInt(4) + 1;
                            final WcEpicEvent wce = new WcEpicEvent(WurmId.getNextWCCommandId(), 0, this.entityId, 0, effect, entity4.getName() + "s followers now have the attention of the " + Effectuator.getSpiritType(effect) + " spirits.", false);
                            wce.sendToServer(entry.id);
                        }
                    }
                }
            }
        }
        catch (IOException ex) {
            WcEpicStatusReport.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeInputStreamIgnoreExceptions(dis);
        }
    }
    
    static {
        logger = Logger.getLogger(WcEpicStatusReport.class.getName());
    }
}
