// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import com.wurmonline.server.Server;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.players.Player;
import java.util.Iterator;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.deities.DbRitual;
import com.wurmonline.server.deities.Deities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class RiteEvent
{
    protected static Logger logger;
    protected static final int MAXIMUM_RITES_CAST = Integer.MAX_VALUE;
    protected static HashMap<Integer, RiteEvent> riteEvents;
    protected static int lastRiteId;
    public static int lastClaimId;
    protected ArrayList<Long> claimedReward;
    protected int id;
    protected long casterId;
    protected int spellId;
    protected int deityNum;
    protected int templateDeity;
    protected long castTime;
    protected long duration;
    protected long expiration;
    
    public RiteEvent(final int id, final long casterId, final int spellId, final int deityNum, final long castTime, final long duration) {
        this.claimedReward = new ArrayList<Long>();
        this.casterId = casterId;
        this.spellId = spellId;
        this.deityNum = deityNum;
        final Deity baseDeity = Deities.getDeity(deityNum);
        if (baseDeity != null) {
            this.templateDeity = baseDeity.getTemplateDeity();
        }
        else {
            RiteEvent.logger.warning(String.format("No template deity found for deity with ID %d when creating a RiteEvent.", deityNum));
            this.templateDeity = deityNum;
        }
        this.castTime = castTime;
        this.duration = duration;
        this.expiration = castTime + duration;
        if (id < 0) {
            for (int i = RiteEvent.lastRiteId; i < Integer.MAX_VALUE; ++i) {
                final RiteEvent result = RiteEvent.riteEvents.putIfAbsent(i, this);
                if (result == null) {
                    this.id = i;
                    RiteEvent.lastRiteId = i;
                    break;
                }
            }
            DbRitual.createRiteEvent(this);
        }
        else {
            this.id = id;
            RiteEvent.riteEvents.put(id, this);
            if (id > RiteEvent.lastRiteId) {
                RiteEvent.lastRiteId = id;
            }
        }
    }
    
    public int getId() {
        return this.id;
    }
    
    public long getCasterId() {
        return this.casterId;
    }
    
    public int getSpellId() {
        return this.spellId;
    }
    
    public int getDeityNum() {
        return this.deityNum;
    }
    
    public long getCastTime() {
        return this.castTime;
    }
    
    public long getDuration() {
        return this.duration;
    }
    
    public static void addRitualClaim(final int id, final long playerId, final int ritualCastsId, final long claimTime) {
        final RiteEvent event = RiteEvent.riteEvents.get(ritualCastsId);
        if (event == null) {
            RiteEvent.logger.warning(String.format("Could not load Ritual Claim for player %d because RiteEvent %d does not exist.", playerId, ritualCastsId));
            return;
        }
        event.claimedReward.add(playerId);
        if (RiteEvent.lastClaimId < id) {
            RiteEvent.lastClaimId = id;
        }
    }
    
    public static void createGenericRiteEvent(final int id, final long casterId, final int spellId, final int deityNum, final long castTime, final long duration) {
        switch (spellId) {
            case 403: {
                new RiteOfSpringEvent(id, casterId, spellId, deityNum, castTime, duration);
                break;
            }
            case 402: {
                new RiteOfDeathEvent(id, casterId, spellId, deityNum, castTime, duration);
                break;
            }
            case 401: {
                new RiteOfTheSunEvent(id, casterId, spellId, deityNum, castTime, duration);
                break;
            }
            case 400: {
                new RiteOfCropEvent(id, casterId, spellId, deityNum, castTime, duration);
                break;
            }
        }
    }
    
    public static boolean isActive(final int spellid) {
        for (final RiteEvent event : RiteEvent.riteEvents.values()) {
            if (spellid == 400 && !(event instanceof RiteOfCropEvent)) {
                continue;
            }
            if (spellid == 403 && !(event instanceof RiteOfSpringEvent)) {
                continue;
            }
            if (spellid == 402 && !(event instanceof RiteOfDeathEvent)) {
                continue;
            }
            if (spellid == 401 && !(event instanceof RiteOfTheSunEvent)) {
                continue;
            }
            if (event.expiration > System.currentTimeMillis()) {
                return true;
            }
        }
        return false;
    }
    
    protected void awardBasicBonuses(final Player player, final Skill skill) {
        final double currentKnowledge = skill.getKnowledge();
        final double bonus = (100.0 - currentKnowledge) * 0.002;
        skill.setKnowledge(currentKnowledge + bonus, false);
        player.getSaveFile().addToSleep(18000);
    }
    
    public boolean claimRiteReward(final Player player) {
        if (player.getDeity().getTemplateDeity() != this.templateDeity) {
            return false;
        }
        if (this.expiration < System.currentTimeMillis()) {
            return false;
        }
        if (this.claimedReward.contains(player.getWurmId())) {
            return false;
        }
        this.claimedReward.add(player.getWurmId());
        DbRitual.createRiteClaim(++RiteEvent.lastClaimId, player.getWurmId(), this.id, System.currentTimeMillis());
        return true;
    }
    
    public static void checkRiteRewards(final Player player) {
        for (final RiteEvent event : RiteEvent.riteEvents.values()) {
            event.claimRiteReward(player);
        }
    }
    
    static {
        RiteEvent.logger = Logger.getLogger(RiteEvent.class.getName());
        RiteEvent.riteEvents = new HashMap<Integer, RiteEvent>();
        RiteEvent.lastRiteId = 0;
        RiteEvent.lastClaimId = -1;
    }
    
    public static class RiteOfSpringEvent extends RiteEvent
    {
        public RiteOfSpringEvent(final int id, final long casterId, final int spellId, final int deityNum, final long castTime, final long duration) {
            super(id, casterId, spellId, deityNum, castTime, duration);
        }
        
        @Override
        public boolean claimRiteReward(final Player player) {
            if (!super.claimRiteReward(player)) {
                return false;
            }
            player.getCommunicator().sendSafeServerMessage("You feel enlightened!", (byte)2);
            this.awardBasicBonuses(player, player.getMindLogical());
            return true;
        }
    }
    
    public static class RiteOfTheSunEvent extends RiteEvent
    {
        public RiteOfTheSunEvent(final int id, final long casterId, final int spellId, final int deityNum, final long castTime, final long duration) {
            super(id, casterId, spellId, deityNum, castTime, duration);
        }
        
        @Override
        public boolean claimRiteReward(final Player player) {
            if (!super.claimRiteReward(player)) {
                return false;
            }
            player.getCommunicator().sendSafeServerMessage("You feel a sudden surge of energy!", (byte)2);
            this.awardBasicBonuses(player, player.getStaminaSkill());
            player.getBody().healFully();
            final float nut = (80 + Server.rand.nextInt(19)) / 100.0f;
            player.getStatus().refresh(nut, false);
            return true;
        }
    }
    
    public static class RiteOfCropEvent extends RiteEvent
    {
        public RiteOfCropEvent(final int id, final long casterId, final int spellId, final int deityNum, final long castTime, final long duration) {
            super(id, casterId, spellId, deityNum, castTime, duration);
        }
        
        @Override
        public boolean claimRiteReward(final Player player) {
            if (!super.claimRiteReward(player)) {
                return false;
            }
            player.getCommunicator().sendSafeServerMessage("You feel a wave of warmth!", (byte)2);
            this.awardBasicBonuses(player, player.getSoulDepth());
            return true;
        }
    }
    
    public static class RiteOfDeathEvent extends RiteEvent
    {
        public RiteOfDeathEvent(final int id, final long casterId, final int spellId, final int deityNum, final long castTime, final long duration) {
            super(id, casterId, spellId, deityNum, castTime, duration);
        }
        
        @Override
        public boolean claimRiteReward(final Player player) {
            if (!super.claimRiteReward(player)) {
                return false;
            }
            player.getCommunicator().sendSafeServerMessage("You feel a sudden surge of power!", (byte)2);
            this.awardBasicBonuses(player, player.getSoulStrength());
            return true;
        }
    }
}
