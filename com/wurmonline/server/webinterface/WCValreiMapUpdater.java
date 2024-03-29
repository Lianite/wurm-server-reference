// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.WurmId;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import com.wurmonline.shared.constants.ValreiConstants;
import com.wurmonline.server.epic.ValreiFightHistory;
import com.wurmonline.server.epic.ValreiFightHistoryManager;
import java.util.Iterator;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.wurmonline.server.epic.CollectedValreiItem;
import com.wurmonline.server.epic.EpicServerStatus;
import java.util.ArrayList;
import com.wurmonline.server.epic.EpicEntity;
import java.util.List;
import com.wurmonline.server.epic.ValreiMapData;
import java.util.logging.Logger;

public class WCValreiMapUpdater extends WebCommand
{
    private static final Logger logger;
    public static final byte INITIAL_REQUEST = 0;
    public static final byte INITIAL_REQUEST_RESPONCE = 1;
    public static final byte UPDATE = 2;
    public static final byte REQUEST_TIME_UPDATE = 3;
    public static final byte SEND_TIME = 4;
    public static final byte NEW_FIGHT = 5;
    private byte messageType;
    private ValreiMapData toUpdate;
    private final List<EpicEntity> dataList;
    
    public WCValreiMapUpdater(final long aid, final byte _messageType) {
        super(aid, (short)27);
        this.toUpdate = null;
        this.dataList = new ArrayList<EpicEntity>();
        this.messageType = _messageType;
    }
    
    public WCValreiMapUpdater(final long aid, final byte[] data) {
        super(aid, (short)27, data);
        this.toUpdate = null;
        this.dataList = new ArrayList<EpicEntity>();
    }
    
    public void collectData() {
        if (this.dataList != null && this.dataList.size() > 0) {
            this.dataList.clear();
        }
        for (final EpicEntity ent : EpicServerStatus.getValrei().getAllEntities()) {
            this.dataList.add(ent);
        }
    }
    
    public void setEntityToUpdate(final EpicEntity entity) {
        final long eId = entity.getId();
        final int hexId = (entity.getMapHex() != null) ? entity.getMapHex().getId() : -1;
        final int type = entity.getType();
        final int targetHex = entity.getTargetHex();
        final String name = entity.getName();
        final long now = System.currentTimeMillis();
        final long remaining = entity.getTimeToNextHex() - now;
        final float attack = entity.getAttack();
        final float vitality = entity.getVitality();
        final List<EpicEntity> list = entity.getAllCollectedItems();
        final List<CollectedValreiItem> valList = CollectedValreiItem.fromList(list);
        final float bodyStr = entity.getCurrentSkill(102);
        final float bodySta = entity.getCurrentSkill(103);
        final float bodyCon = entity.getCurrentSkill(104);
        final float mindLog = entity.getCurrentSkill(100);
        final float mindSpe = entity.getCurrentSkill(101);
        final float soulStr = entity.getCurrentSkill(105);
        final float soulDep = entity.getCurrentSkill(106);
        this.toUpdate = new ValreiMapData(eId, hexId, type, targetHex, name, remaining, bodyStr, bodySta, bodyCon, mindLog, mindSpe, soulStr, soulDep, valList);
    }
    
    @Override
    public boolean autoForward() {
        return false;
    }
    
    @Override
    byte[] encode() {
        if (this.messageType == 2) {
            if (this.toUpdate != null) {
                return this.createUpdateMessage();
            }
        }
        else {
            if (this.messageType == 0) {
                return this.createInitialRequestMessage();
            }
            if (this.messageType == 1) {
                this.collectData();
                return this.createInitialResponceMessage();
            }
            if (this.messageType == 3) {
                return this.createTimeUpdateRequest();
            }
            if (this.messageType == 4) {
                this.collectData();
                return this.createTimeUpdateMessage();
            }
            if (this.messageType == 5) {
                return this.createFightDetails();
            }
        }
        return new byte[0];
    }
    
    private final byte[] createTimeUpdateRequest() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] byteData = null;
        try {
            dos = new DataOutputStream(bos);
            dos.writeByte(3);
        }
        catch (Exception ex) {
            WCValreiMapUpdater.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeOutputStreamIgnoreExceptions(dos);
            byteData = bos.toByteArray();
            StreamUtilities.closeOutputStreamIgnoreExceptions(bos);
            this.setData(byteData);
        }
        return byteData;
    }
    
    private final byte[] createTimeUpdateMessage() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] byteData = null;
        try {
            dos = new DataOutputStream(bos);
            dos.writeByte(4);
            int count = 0;
            for (final EpicEntity entity : this.dataList) {
                if (!entity.isCollectable()) {
                    if (entity.isSource()) {
                        continue;
                    }
                    ++count;
                }
            }
            dos.writeInt(count);
            for (final EpicEntity entity : this.dataList) {
                if (!entity.isCollectable()) {
                    if (entity.isSource()) {
                        continue;
                    }
                    dos.writeLong(entity.getId());
                    final long now = System.currentTimeMillis();
                    final long remaining = entity.getTimeUntilLeave() - now;
                    dos.writeLong(remaining);
                }
            }
        }
        catch (Exception ex) {
            WCValreiMapUpdater.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeOutputStreamIgnoreExceptions(dos);
            byteData = bos.toByteArray();
            StreamUtilities.closeOutputStreamIgnoreExceptions(bos);
            this.setData(byteData);
        }
        return byteData;
    }
    
    private final byte[] createUpdateMessage() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] byteData = null;
        try {
            dos = new DataOutputStream(bos);
            dos.writeByte(2);
            final ValreiMapData entity = this.toUpdate;
            dos.writeLong(entity.getEntityId());
            dos.writeInt(entity.getType());
            dos.writeInt(entity.getHexId());
            dos.writeInt(entity.getTargetHexId());
            dos.writeUTF(entity.getName());
            dos.writeLong(entity.getTimeRemaining());
            if (!entity.isCollectable() && !entity.isSourceItem()) {
                dos.writeFloat(entity.getBodyStr());
                dos.writeFloat(entity.getBodySta());
                dos.writeFloat(entity.getBodyCon());
                dos.writeFloat(entity.getMindLog());
                dos.writeFloat(entity.getMindSpe());
                dos.writeFloat(entity.getSoulStr());
                dos.writeFloat(entity.getSoulDep());
                final List<CollectedValreiItem> list = entity.getCarried();
                final int collected = list.size();
                dos.writeInt(collected);
                for (int i = 0; i < collected; ++i) {
                    dos.writeUTF(list.get(i).getName());
                    dos.writeInt(list.get(i).getType());
                    dos.writeLong(list.get(i).getId());
                }
            }
        }
        catch (Exception ex) {
            WCValreiMapUpdater.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeOutputStreamIgnoreExceptions(dos);
            byteData = bos.toByteArray();
            StreamUtilities.closeOutputStreamIgnoreExceptions(bos);
            this.setData(byteData);
        }
        return byteData;
    }
    
    private final byte[] createFightDetails() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] byteData = null;
        final ValreiFightHistory vf = ValreiFightHistoryManager.getInstance().getLatestFight();
        try {
            dos = new DataOutputStream(bos);
            dos.writeByte(5);
            this.writeFightDetails(dos, vf);
        }
        catch (Exception ex) {
            WCValreiMapUpdater.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeOutputStreamIgnoreExceptions(dos);
            byteData = bos.toByteArray();
            StreamUtilities.closeOutputStreamIgnoreExceptions(bos);
            this.setData(byteData);
        }
        return byteData;
    }
    
    private final byte[] createInitialRequestMessage() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] byteData = null;
        try {
            dos = new DataOutputStream(bos);
            dos.writeByte(0);
        }
        catch (Exception ex) {
            WCValreiMapUpdater.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeOutputStreamIgnoreExceptions(dos);
            byteData = bos.toByteArray();
            StreamUtilities.closeOutputStreamIgnoreExceptions(bos);
            this.setData(byteData);
        }
        return byteData;
    }
    
    private final byte[] createInitialResponceMessage() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] byteData = null;
        try {
            dos = new DataOutputStream(bos);
            dos.writeByte(1);
            dos.writeInt(this.dataList.size());
            for (final EpicEntity entity : this.dataList) {
                dos.writeLong(entity.getId());
                dos.writeInt(entity.getType());
                dos.writeInt((entity.getMapHex() == null) ? -1 : entity.getMapHex().getId());
                dos.writeInt(entity.getTargetHex());
                dos.writeUTF(entity.getName());
                final long now = System.currentTimeMillis();
                final long remaining = entity.getTimeUntilLeave() - now;
                dos.writeLong(remaining);
                if (!entity.isCollectable() && !entity.isSource()) {
                    dos.writeFloat(entity.getCurrentSkill(102));
                    dos.writeFloat(entity.getCurrentSkill(103));
                    dos.writeFloat(entity.getCurrentSkill(104));
                    dos.writeFloat(entity.getCurrentSkill(100));
                    dos.writeFloat(entity.getCurrentSkill(101));
                    dos.writeFloat(entity.getCurrentSkill(105));
                    dos.writeFloat(entity.getCurrentSkill(106));
                    final List<EpicEntity> list = entity.getAllCollectedItems();
                    final int collected = list.size();
                    dos.writeInt(collected);
                    for (int i = 0; i < collected; ++i) {
                        dos.writeUTF(list.get(i).getName());
                        dos.writeInt(list.get(i).getType());
                        dos.writeLong(list.get(i).getId());
                    }
                }
            }
            final ArrayList<ValreiFightHistory> allFights = ValreiFightHistoryManager.getInstance().getAllFights();
            dos.writeInt(allFights.size());
            for (final ValreiFightHistory vf : allFights) {
                this.writeFightDetails(dos, vf);
            }
        }
        catch (Exception ex) {
            WCValreiMapUpdater.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeOutputStreamIgnoreExceptions(dos);
            byteData = bos.toByteArray();
            StreamUtilities.closeOutputStreamIgnoreExceptions(bos);
            this.setData(byteData);
        }
        return byteData;
    }
    
    public void writeFightDetails(final DataOutputStream dos, final ValreiFightHistory vf) throws IOException {
        dos.writeLong(vf.getFightId());
        dos.writeInt(vf.getMapHexId());
        dos.writeUTF(vf.getMapHexName());
        dos.writeLong(vf.getFightTime());
        dos.writeInt(vf.getFighters().size());
        for (final ValreiFightHistory.ValreiFighter v : vf.getFighters().values()) {
            dos.writeLong(v.getFighterId());
            dos.writeUTF(v.getName());
        }
        dos.writeInt(vf.getTotalActions());
        for (int i = 0; i <= vf.getTotalActions(); ++i) {
            final ValreiConstants.ValreiFightAction act = vf.getFightAction(i);
            dos.writeInt(act.getActionNum());
            dos.writeShort(act.getActionId());
            dos.writeInt(act.getActionData().length);
            dos.write(act.getActionData());
        }
    }
    
    @Override
    public void execute() {
        DataInputStream dis = null;
        byte type = -1;
        try {
            dis = new DataInputStream(new ByteArrayInputStream(this.getData()));
            type = dis.readByte();
            if (type == 2) {
                this.readUpdateRequest(dis);
            }
            else if (type == 0) {
                this.handleInitialRequest();
            }
            else if (type == 1) {
                this.readFullRequestResponce(dis);
            }
            else if (type == 3) {
                this.handleTimeUpdateRequest();
            }
            else if (type == 4) {
                this.readTimeUpdateMessage(dis);
            }
            else if (type == 5) {
                this.readFightDetails(dis);
            }
        }
        catch (IOException ex) {
            WCValreiMapUpdater.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage() + " messageType " + type, ex);
        }
        finally {
            StreamUtilities.closeInputStreamIgnoreExceptions(dis);
        }
    }
    
    private void readTimeUpdateMessage(final DataInputStream dis) throws IOException {
        for (int count = dis.readInt(), i = 0; i < count; ++i) {
            final long id = dis.readLong();
            final long remaining = dis.readLong();
            ValreiMapData.updateEntityTime(id, remaining);
        }
    }
    
    private void handleTimeUpdateRequest() {
        final WCValreiMapUpdater updater = new WCValreiMapUpdater(WurmId.getNextWCCommandId(), (byte)4);
        updater.sendFromLoginServer();
    }
    
    private void handleInitialRequest() {
        final WCValreiMapUpdater updater = new WCValreiMapUpdater(WurmId.getNextWCCommandId(), (byte)1);
        updater.sendFromLoginServer();
    }
    
    public void testDataEncoding() {
        this.collectData();
        final byte[] responce = this.createInitialResponceMessage();
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new ByteArrayInputStream(responce));
            final byte type = dis.readByte();
            if (type == 1) {
                this.readFullRequestResponce(dis);
            }
        }
        catch (IOException ex) {
            WCValreiMapUpdater.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeInputStreamIgnoreExceptions(dis);
        }
    }
    
    private void readUpdateRequest(final DataInputStream dis) throws IOException {
        final long id = dis.readLong();
        final int type = dis.readInt();
        final int hexId = dis.readInt();
        final int targetHex = dis.readInt();
        final String name = dis.readUTF();
        final long remainingTime = dis.readLong();
        final float attack = 0.0f;
        final float vitality = 0.0f;
        float bodyStr = 0.0f;
        float bodySta = 0.0f;
        float bodyCon = 0.0f;
        float mindLog = 0.0f;
        float mindSpe = 0.0f;
        float soulStr = 0.0f;
        float soulDep = 0.0f;
        final List<CollectedValreiItem> list = new ArrayList<CollectedValreiItem>();
        if (type != 2 && type != 1) {
            bodyStr = dis.readFloat();
            bodySta = dis.readFloat();
            bodyCon = dis.readFloat();
            mindLog = dis.readFloat();
            mindSpe = dis.readFloat();
            soulStr = dis.readFloat();
            soulDep = dis.readFloat();
            for (int count = dis.readInt(), j = 0; j < count; ++j) {
                final String carriedName = dis.readUTF();
                final int collType = dis.readInt();
                final long collId = dis.readLong();
                list.add(new CollectedValreiItem(collId, carriedName, collType));
            }
        }
        ValreiMapData.updateEntity(id, hexId, type, targetHex, name, remainingTime, bodyStr, bodySta, bodyCon, mindLog, mindSpe, soulStr, soulDep, list);
    }
    
    private void readFullRequestResponce(final DataInputStream dis) throws IOException {
        for (int size = dis.readInt(), i = 0; i < size; ++i) {
            final long id = dis.readLong();
            final int type = dis.readInt();
            final int hexId = dis.readInt();
            final int targetHex = dis.readInt();
            final String name = dis.readUTF();
            final long remainingTime = dis.readLong();
            final float attack = 0.0f;
            final float vitality = 0.0f;
            float bodyStr = 0.0f;
            float bodySta = 0.0f;
            float bodyCon = 0.0f;
            float mindLog = 0.0f;
            float mindSpe = 0.0f;
            float soulStr = 0.0f;
            float soulDep = 0.0f;
            final List<CollectedValreiItem> list = new ArrayList<CollectedValreiItem>();
            if (type != 2 && type != 1) {
                bodyStr = dis.readFloat();
                bodySta = dis.readFloat();
                bodyCon = dis.readFloat();
                mindLog = dis.readFloat();
                mindSpe = dis.readFloat();
                soulStr = dis.readFloat();
                soulDep = dis.readFloat();
                for (int count = dis.readInt(), j = 0; j < count; ++j) {
                    final String carriedName = dis.readUTF();
                    final int carriedType = dis.readInt();
                    final long collId = dis.readLong();
                    list.add(new CollectedValreiItem(collId, carriedName, carriedType));
                }
            }
            ValreiMapData.updateEntity(id, hexId, type, targetHex, name, remainingTime, bodyStr, bodySta, bodyCon, mindLog, mindSpe, soulStr, soulDep, list);
        }
        for (int fightsSize = dis.readInt(), k = 0; k < fightsSize; ++k) {
            this.readFightDetails(dis);
        }
        ValreiMapData.setHasInitialData();
    }
    
    private void readFightDetails(final DataInputStream dis) throws IOException {
        final long fightId = dis.readLong();
        final int mapHexId = dis.readInt();
        final String mapHexName = dis.readUTF();
        final long fightTime = dis.readLong();
        final ValreiFightHistory vf = new ValreiFightHistory(fightId, mapHexId, mapHexName, fightTime);
        for (int fightersSize = dis.readInt(), j = 0; j < fightersSize; ++j) {
            final long fighterId = dis.readLong();
            final String fighterName = dis.readUTF();
            vf.addFighter(fighterId, fighterName);
        }
        for (int actionsSize = dis.readInt(), i = 0; i <= actionsSize; ++i) {
            final int actionNum = dis.readInt();
            final short actionId = dis.readShort();
            final int dataLen = dis.readInt();
            final byte[] actionData = new byte[dataLen];
            dis.read(actionData);
            vf.addAction(actionId, actionData);
        }
        vf.setFightCompleted(true);
        ValreiFightHistoryManager.getInstance().addFight(fightId, vf, false);
    }
    
    static {
        logger = Logger.getLogger(WCValreiMapUpdater.class.getName());
    }
}
