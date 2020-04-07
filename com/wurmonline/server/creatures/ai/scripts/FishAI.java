// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures.ai.scripts;

import com.wurmonline.shared.util.StringUtilities;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.behaviours.FishEnums;
import com.wurmonline.server.creatures.ai.CreatureAIData;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.ai.CreatureAI;

public class FishAI extends CreatureAI
{
    @Override
    protected boolean pollMovement(final Creature c, final long delta) {
        final FishAIData aiData = (FishAIData)c.getCreatureAIData();
        final float targetX = aiData.getTargetPosX();
        final float targetY = aiData.getTargetPosY();
        if (targetX < 0.0f || targetY < 0.0f) {
            return false;
        }
        if (c.getPosX() != targetX || c.getPosY() != targetY) {
            final float diffX = c.getPosX() - targetX;
            final float diffY = c.getPosY() - targetY;
            final float totalDiff = (float)Math.sqrt(diffX * diffX + diffY * diffY);
            float movementSpeed = aiData.getSpeed() * aiData.getMovementSpeedModifier();
            if (totalDiff < movementSpeed) {
                movementSpeed = totalDiff;
            }
            final double lRotation = Math.atan2(targetY - c.getPosY(), targetX - c.getPosX()) * 57.29577951308232 + 90.0;
            final float lXPosMod = (float)Math.sin(lRotation * 0.01745329238474369) * movementSpeed;
            final float lYPosMod = -(float)Math.cos(lRotation * 0.01745329238474369) * movementSpeed;
            final int lNewTileX = (int)(c.getPosX() + lXPosMod) >> 2;
            final int lNewTileY = (int)(c.getPosY() + lYPosMod) >> 2;
            final int lDiffTileX = lNewTileX - c.getTileX();
            final int lDiffTileY = lNewTileY - c.getTileY();
            c.setPositionX(c.getPosX() + lXPosMod);
            c.setPositionY(c.getPosY() + lYPosMod);
            c.setRotation((float)lRotation);
            try {
                final float minZ = Math.min(-0.1f, Zones.calculateHeight(c.getPosX(), c.getPosY(), c.isOnSurface()));
                if (c.getPositionZ() < minZ) {
                    c.setPositionZ(minZ + Math.abs(minZ * 0.2f));
                }
                else if (c.getPositionZ() < minZ * 0.15f) {
                    c.setPositionZ(minZ * 0.15f);
                }
            }
            catch (NoSuchZoneException ex) {}
            c.moved(lXPosMod, lYPosMod, 0.0f, lDiffTileX, lDiffTileY);
        }
        return false;
    }
    
    @Override
    protected boolean pollAttack(final Creature c, final long delta) {
        return false;
    }
    
    @Override
    protected boolean pollBreeding(final Creature c, final long delta) {
        return false;
    }
    
    @Override
    public CreatureAIData createCreatureAIData() {
        return new FishAIData();
    }
    
    @Override
    public void creatureCreated(final Creature c) {
    }
    
    public class FishAIData extends CreatureAIData
    {
        private byte fishTypeId;
        private double ql;
        private float qlperc;
        private int weight;
        private float targetPosX;
        private float targetPosY;
        private float timeToTarget;
        private float bodyStrength;
        private float bodyStamina;
        private float bodyControl;
        private float mindSpeed;
        private float difficulty;
        private boolean racingAway;
        private static final int PERC_OFFSET = 25;
        private static final int SPEED_OFFSET = 75;
        
        public FishAIData() {
            this.fishTypeId = 0;
            this.ql = 10.0;
            this.qlperc = 1.0f;
            this.weight = 0;
            this.targetPosX = -1.0f;
            this.targetPosY = -1.0f;
            this.timeToTarget = 0.0f;
            this.bodyStrength = 1.0f;
            this.bodyStamina = 1.0f;
            this.bodyControl = 1.0f;
            this.mindSpeed = 1.0f;
            this.difficulty = -10.0f;
            this.racingAway = false;
        }
        
        public byte getFishTypeId() {
            return this.fishTypeId;
        }
        
        public void setFishTypeId(final byte fishTypeId) {
            this.fishTypeId = fishTypeId;
        }
        
        @Override
        public float getSpeed() {
            float mod;
            if (this.racingAway) {
                mod = 2.5f;
            }
            else {
                mod = (75.0f + (float)this.ql) / 175.0f;
            }
            return this.getFishData().getBaseSpeed() * mod;
        }
        
        public FishEnums.FishData getFishData() {
            return FishEnums.FishData.fromInt(this.fishTypeId);
        }
        
        public void setQL(final double ql) {
            this.ql = ql;
            this.qlperc = (25.0f + (float)this.ql) / 125.0f;
            this.bodyStrength = Math.max(this.getFishData().getBodyStrength() * this.qlperc, 1.0f);
            this.bodyStamina = Math.max(this.getFishData().getBodyStamina() * this.qlperc, 1.0f);
            this.bodyControl = Math.max(this.getFishData().getBodyControl() * this.qlperc, 1.0f);
            this.mindSpeed = Math.max(this.getFishData().getMindSpeed() * this.qlperc, 1.0f);
            this.setSizeModifier(this.qlperc * this.getFishData().getScaleMod());
            final ItemTemplate it = this.getFishData().getTemplate();
            if (it != null) {
                this.weight = (int)(it.getWeightGrams() * (ql / 100.0));
            }
        }
        
        public void setTargetPos(final float targetPosX, final float targetPosY) {
            this.targetPosX = targetPosX;
            this.targetPosY = targetPosY;
            this.calcTimeToTarget();
        }
        
        public void setRaceAway(final boolean raceAway) {
            this.racingAway = raceAway;
            this.calcTimeToTarget();
        }
        
        private void calcTimeToTarget() {
            final float diffX = this.targetPosX - this.getCreature().getPosX();
            final float diffY = this.targetPosY - this.getCreature().getPosY();
            final float dist = (float)Math.sqrt(diffX * diffX + diffY * diffY);
            final float movementSpeed = this.getSpeed() * this.getMovementSpeedModifier();
            this.timeToTarget = dist / movementSpeed * 10.0f + 2.0f;
        }
        
        public float getTargetPosX() {
            return this.targetPosX;
        }
        
        public float getTargetPosY() {
            return this.targetPosY;
        }
        
        public float getTimeToTarget() {
            return this.timeToTarget;
        }
        
        public double getQL() {
            return this.ql;
        }
        
        public String getNameWithGenusAndSize() {
            return StringUtilities.addGenus(this.getNameWithSize(), false);
        }
        
        public String getNameWithSize() {
            final StringBuilder buf = new StringBuilder();
            if (this.ql >= 99.0) {
                buf.append("stupendous ");
            }
            else if (this.ql >= 95.0) {
                buf.append("massive ");
            }
            else if (this.ql >= 85.0) {
                buf.append("huge ");
            }
            else if (this.ql >= 75.0) {
                buf.append("impressive ");
            }
            else if (this.ql >= 65.0) {
                buf.append("large ");
            }
            if (this.ql < 15.0) {
                buf.append("small ");
            }
            buf.append(this.getFishData().getName());
            return buf.toString();
        }
        
        public int getWeight() {
            return this.weight;
        }
        
        public float getBodyStrength() {
            return this.bodyStrength;
        }
        
        public float getBodyStamina() {
            return this.bodyStamina;
        }
        
        public void decBodyStamina(final float bodyStamina) {
            this.bodyStamina = Math.max(this.bodyStamina - bodyStamina, 0.0f);
        }
        
        public float getBodyControl() {
            return this.bodyControl;
        }
        
        public float getMindSpeed() {
            return this.mindSpeed;
        }
        
        public void setDifficulty(final float difficulty) {
            this.difficulty = difficulty;
        }
        
        public float getDifficulty() {
            return this.difficulty;
        }
    }
}
