// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.epic;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.Players;
import com.wurmonline.server.webinterface.WCValreiMapUpdater;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.Servers;
import com.wurmonline.server.Features;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.Map;

public class ValreiMapData
{
    private static Map<Long, ValreiMapData> enteties;
    static long lastPolled;
    static long lastUpdatedTime;
    private static boolean hasInitialData;
    private static boolean firstRequest;
    private static final Logger logger;
    private long entityId;
    private int hexId;
    private int type;
    private int targetHexId;
    private String name;
    private long timeRemaining;
    private float attack;
    private float vitality;
    private float bodyStr;
    private float bodySta;
    private float bodyCon;
    private float mindLog;
    private float mindSpe;
    private float soulStr;
    private float soulDep;
    private boolean dirty;
    private boolean dirtyTime;
    private List<CollectedValreiItem> carriedItems;
    
    public ValreiMapData(final long _entityId, final int _hexId, final int _type, final int _targetHexId, final String _name, final long _remainingTime, final float _bodyStr, final float _bodySta, final float _bodyCon, final float _mindLog, final float _mindSpe, final float _soulStr, final float _soulDep, final List<CollectedValreiItem> _carried) {
        this.dirty = false;
        this.dirtyTime = false;
        this.carriedItems = null;
        this.entityId = _entityId;
        this.hexId = _hexId;
        this.type = _type;
        this.targetHexId = _targetHexId;
        this.name = _name;
        this.timeRemaining = _remainingTime;
        this.bodyStr = _bodyStr;
        this.bodySta = _bodySta;
        this.bodyCon = _bodyCon;
        this.mindLog = _mindLog;
        this.mindSpe = _mindSpe;
        this.soulStr = _soulStr;
        this.soulDep = _soulDep;
        this.carriedItems = _carried;
    }
    
    public static synchronized void updateEntity(final long _entityId, final int _hexId, final int _type, final int _targetHexId, final String _name, final long _remainingTime, final float _bodyStr, final float _bodySta, final float _bodyCon, final float _mindLog, final float _mindSpe, final float _soulStr, final float _soulDep, final List<CollectedValreiItem> _carried) {
        final ValreiMapData entity = ValreiMapData.enteties.get(_entityId);
        if (entity == null) {
            ValreiMapData.enteties.put(_entityId, new ValreiMapData(_entityId, _hexId, _type, _targetHexId, _name, _remainingTime, _bodyStr, _bodySta, _bodyCon, _mindLog, _mindSpe, _soulStr, _soulDep, (_carried != null) ? _carried : new ArrayList<CollectedValreiItem>()));
        }
        else {
            entity.setEntityId(_entityId);
            entity.setHexId(_hexId);
            entity.setType(_type);
            entity.setTargetHexId(_targetHexId);
            entity.setName(_name);
            entity.setBodyStr(_bodyStr);
            entity.setBodySta(_bodySta);
            entity.setBodyCon(_bodyCon);
            entity.setMindLog(_mindLog);
            entity.setMindSpe(_mindSpe);
            entity.setSoulStr(_soulStr);
            entity.setSoulDep(_soulDep);
            entity.setCarried(_carried);
            entity.setTimeRemaining(_remainingTime);
        }
    }
    
    public static synchronized void updateEntityTime(final long _entityId, final long _time) {
        final ValreiMapData entity = ValreiMapData.enteties.get(_entityId);
        if (entity != null) {
            entity.setTimeRemaining(_time);
        }
    }
    
    public final synchronized long getEntityId() {
        return this.entityId;
    }
    
    public final synchronized int getHexId() {
        return this.hexId;
    }
    
    public final synchronized int getType() {
        return this.type;
    }
    
    public final synchronized String getName() {
        return this.name;
    }
    
    public final synchronized long getTimeRemaining() {
        return this.timeRemaining;
    }
    
    public final synchronized float getAttack() {
        return this.attack;
    }
    
    public final synchronized float getVitality() {
        return this.vitality;
    }
    
    public final synchronized int getTargetHexId() {
        return this.targetHexId;
    }
    
    public final synchronized List<CollectedValreiItem> getCarried() {
        return this.carriedItems;
    }
    
    public synchronized void setEntityId(final long id) {
        if (this.entityId != id) {
            this.entityId = id;
            this.onChanged();
        }
    }
    
    public synchronized void setHexId(final int id) {
        if (this.hexId != id) {
            this.hexId = id;
            this.onChanged();
        }
    }
    
    public synchronized void setType(final int newType) {
        if (this.type != newType) {
            this.type = newType;
            this.onChanged();
        }
    }
    
    public synchronized void setTargetHexId(final int newTarget) {
        if (this.targetHexId != newTarget) {
            this.targetHexId = newTarget;
            this.onChanged();
        }
    }
    
    public synchronized void setName(final String newName) {
        if (!this.name.equals(newName)) {
            this.name = newName;
            this.onChanged();
        }
    }
    
    public synchronized void setTimeRemaining(final long remaining) {
        if (this.timeRemaining != remaining) {
            this.timeRemaining = remaining;
            this.onTimeChanged();
        }
    }
    
    public synchronized void setAttack(final float newAttack) {
        if (this.attack != newAttack) {
            this.attack = newAttack;
            this.onChanged();
        }
    }
    
    public synchronized void setVitality(final float newVitality) {
        if (this.vitality != newVitality) {
            this.vitality = newVitality;
            this.onChanged();
        }
    }
    
    public void setBodyStr(final float bodyStr) {
        if (this.bodyStr != bodyStr) {
            this.bodyStr = bodyStr;
            this.onChanged();
        }
    }
    
    public void setBodySta(final float bodySta) {
        if (this.bodySta != bodySta) {
            this.bodySta = bodySta;
            this.onChanged();
        }
    }
    
    public void setBodyCon(final float bodyCon) {
        if (this.bodyCon != bodyCon) {
            this.bodyCon = bodyCon;
            this.onChanged();
        }
    }
    
    public void setMindLog(final float mindLog) {
        if (this.mindLog != mindLog) {
            this.mindLog = mindLog;
            this.onChanged();
        }
    }
    
    public void setMindSpe(final float mindSpe) {
        if (this.mindSpe != mindSpe) {
            this.mindSpe = mindSpe;
            this.onChanged();
        }
    }
    
    public void setSoulStr(final float soulStr) {
        if (this.soulStr != soulStr) {
            this.soulStr = soulStr;
            this.onChanged();
        }
    }
    
    public void setSoulDep(final float soulDep) {
        if (this.soulDep != soulDep) {
            this.soulDep = soulDep;
            this.onChanged();
        }
    }
    
    public float getBodyStr() {
        return this.bodyStr;
    }
    
    public float getBodySta() {
        return this.bodySta;
    }
    
    public float getBodyCon() {
        return this.bodyCon;
    }
    
    public float getMindLog() {
        return this.mindLog;
    }
    
    public float getMindSpe() {
        return this.mindSpe;
    }
    
    public float getSoulStr() {
        return this.soulStr;
    }
    
    public float getSoulDep() {
        return this.soulDep;
    }
    
    public synchronized void setCarried(final List<CollectedValreiItem> _carried) {
        if (this.carriedItems != _carried || !this.carriedItems.containsAll(_carried)) {
            this.carriedItems = _carried;
            this.onChanged();
        }
    }
    
    private void onChanged() {
        this.dirty = true;
    }
    
    private void onTimeChanged() {
        this.dirtyTime = true;
    }
    
    public void toggleNotDirty() {
        this.dirty = false;
    }
    
    public void toggleTimeNotDirty() {
        this.dirtyTime = false;
    }
    
    public final boolean isDirty() {
        return this.dirty;
    }
    
    public final boolean isTimeDirty() {
        return this.dirtyTime;
    }
    
    public final boolean isCollectable() {
        return this.type == 2;
    }
    
    public final boolean isSourceItem() {
        return this.type == 1;
    }
    
    public final boolean isDemiGod() {
        return this.type == 7;
    }
    
    public final boolean isSentinel() {
        return this.type == 5;
    }
    
    public final boolean isAlly() {
        return this.type == 6;
    }
    
    public final boolean isItem() {
        return this.isCollectable() || this.isSourceItem();
    }
    
    public final boolean isCustomGod() {
        return !this.isItem() && !this.isDemiGod() && this.entityId > 100L;
    }
    
    public static synchronized void setHasInitialData() {
        ValreiMapData.hasInitialData = true;
        ValreiMapData.lastUpdatedTime = System.currentTimeMillis();
    }
    
    public static final synchronized boolean hasInitialData() {
        return ValreiMapData.hasInitialData;
    }
    
    public static void pollValreiData() {
        if (!Features.Feature.VALREI_MAP.isEnabled()) {
            return;
        }
        final long now = System.currentTimeMillis();
        if (ValreiMapData.lastPolled == 0L || ValreiMapData.lastUpdatedTime == 0L) {
            ValreiMapData.lastPolled = now;
            ValreiMapData.lastUpdatedTime = now;
            return;
        }
        final long elapsed = now - ValreiMapData.lastPolled;
        final long elapsedSinceTimeUpdate = now - ValreiMapData.lastUpdatedTime;
        if (!hasInitialData()) {
            if (ValreiMapData.firstRequest || elapsed > 600000L) {
                ValreiMapData.lastPolled = now;
                ValreiMapData.firstRequest = false;
                if (!Servers.localServer.LOGINSERVER) {
                    final WCValreiMapUpdater updater = new WCValreiMapUpdater(WurmId.getNextWCCommandId(), (byte)0);
                    updater.sendToLoginServer();
                }
                else {
                    collectLocalData();
                    setHasInitialData();
                }
            }
            return;
        }
        if (elapsedSinceTimeUpdate > 2400000L) {
            if (!Servers.localServer.LOGINSERVER) {
                final WCValreiMapUpdater updater = new WCValreiMapUpdater(WurmId.getNextWCCommandId(), (byte)3);
                updater.sendToLoginServer();
            }
            else {
                updateTimeData();
            }
        }
        if (elapsed < 1800000L) {
            return;
        }
        ValreiMapData.lastPolled = now;
        final Player[] players2;
        final Player[] players = players2 = Players.getInstance().getPlayers();
        for (final Player player : players2) {
            boolean sent = false;
            for (final ValreiMapData data : ValreiMapData.enteties.values()) {
                if (data.isDirty() || !player.hasReceivedInitialValreiData) {
                    player.getCommunicator().sendValreiMapData(data);
                    sent = true;
                }
                else {
                    if (data.isItem() || !data.isTimeDirty()) {
                        continue;
                    }
                    player.getCommunicator().sendValreiMapDataTimeUpdate(data);
                    sent = true;
                }
            }
            if (sent && !player.hasReceivedInitialValreiData) {
                player.hasReceivedInitialValreiData = true;
            }
        }
        for (final ValreiMapData data2 : ValreiMapData.enteties.values()) {
            if (data2.isDirty()) {
                data2.toggleNotDirty();
                data2.toggleTimeNotDirty();
            }
            else {
                if (!data2.isTimeDirty()) {
                    continue;
                }
                data2.toggleTimeNotDirty();
            }
        }
    }
    
    public static void sendAllMapData(final Player player) {
        if (player.hasReceivedInitialValreiData) {
            player.hasReceivedInitialValreiData = false;
        }
        if (Features.Feature.VALREI_MAP.isEnabled() && hasInitialData() && player != null) {
            player.getCommunicator().sendValreiMapDataList(ValreiMapData.enteties.values());
            player.hasReceivedInitialValreiData = (ValreiMapData.enteties.values().size() > 0);
        }
    }
    
    public static synchronized void updateTimeData() {
        final EpicEntity[] epicEnts = EpicServerStatus.getValrei().getAllEntities();
        if (epicEnts != null) {
            final long now = System.currentTimeMillis();
            for (final EpicEntity ent : epicEnts) {
                final long id = ent.getId();
                final long remaining = ent.getTimeUntilLeave() - now;
                updateEntityTime(id, remaining);
            }
        }
    }
    
    public static synchronized void collectLocalData() {
        final EpicEntity[] epicEnts = EpicServerStatus.getValrei().getAllEntities();
        if (epicEnts != null) {
            final long now = System.currentTimeMillis();
            for (final EpicEntity ent : epicEnts) {
                final long id = ent.getId();
                final int hexId = (ent.getMapHex() != null) ? ent.getMapHex().getId() : -1;
                final int type = ent.getType();
                final int targetHex = ent.getTargetHex();
                final String name = ent.getName();
                final long remaining = ent.getTimeToNextHex() - now;
                final float attack = ent.getAttack();
                final float vitality = ent.getVitality();
                final float bodyStr = ent.getCurrentSkill(102);
                final float bodySta = ent.getCurrentSkill(103);
                final float bodyCon = ent.getCurrentSkill(104);
                final float mindLog = ent.getCurrentSkill(100);
                final float mindSpe = ent.getCurrentSkill(101);
                final float soulStr = ent.getCurrentSkill(105);
                final float soulDep = ent.getCurrentSkill(106);
                final List<CollectedValreiItem> carried = CollectedValreiItem.fromList(ent.getAllCollectedItems());
                updateEntity(id, hexId, type, targetHex, name, remaining, bodyStr, bodySta, bodyCon, mindLog, mindSpe, soulStr, soulDep, carried);
            }
        }
    }
    
    public static synchronized void updateFromEpicEntity(final EpicEntity ent) {
        if (Servers.localServer.LOGINSERVER) {
            final long now = System.currentTimeMillis();
            final WCValreiMapUpdater updater = new WCValreiMapUpdater(WurmId.getNextWCCommandId(), (byte)2);
            updater.setEntityToUpdate(ent);
            updater.sendFromLoginServer();
            final long id = ent.getId();
            final int hexId = (ent.getMapHex() != null) ? ent.getMapHex().getId() : -1;
            final int type = ent.getType();
            final int targetHex = ent.getTargetHex();
            final String name = ent.getName();
            final long remaining = ent.getTimeUntilLeave() - now;
            final float attack = ent.getAttack();
            final float vitality = ent.getVitality();
            final float bodyStr = ent.getCurrentSkill(102);
            final float bodySta = ent.getCurrentSkill(103);
            final float bodyCon = ent.getCurrentSkill(104);
            final float mindLog = ent.getCurrentSkill(100);
            final float mindSpe = ent.getCurrentSkill(101);
            final float soulStr = ent.getCurrentSkill(105);
            final float soulDep = ent.getCurrentSkill(106);
            final List<CollectedValreiItem> carried = CollectedValreiItem.fromList(ent.getAllCollectedItems());
            updateEntity(id, hexId, type, targetHex, name, remaining, bodyStr, bodySta, bodyCon, mindLog, mindSpe, soulStr, soulDep, carried);
        }
    }
    
    static {
        ValreiMapData.enteties = new ConcurrentHashMap<Long, ValreiMapData>();
        ValreiMapData.lastPolled = 0L;
        ValreiMapData.lastUpdatedTime = 0L;
        ValreiMapData.hasInitialData = false;
        ValreiMapData.firstRequest = true;
        logger = Logger.getLogger(ValreiMapData.class.getName());
    }
}
