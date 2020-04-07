// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import com.wurmonline.server.behaviours.Vehicle;
import com.wurmonline.server.Servers;
import com.wurmonline.server.WurmCalendar;
import java.util.ListIterator;
import java.util.List;
import com.wurmonline.server.NoSuchPlayerException;
import java.util.LinkedList;
import com.wurmonline.server.PlonkData;
import com.wurmonline.server.players.ItemBonus;
import com.wurmonline.server.skills.NoSuchSkillException;
import java.io.IOException;
import com.wurmonline.server.effects.Effect;
import com.wurmonline.server.effects.EffectFactory;
import com.wurmonline.server.players.SpellResistance;
import com.wurmonline.math.Vector3f;
import com.wurmonline.math.Vector2f;
import com.wurmonline.server.tutorial.PlayerTutorial;
import com.wurmonline.server.villages.Village;
import java.util.Iterator;
import com.wurmonline.server.creatures.ai.Order;
import javax.annotation.Nullable;
import com.wurmonline.server.items.Item;
import java.util.HashSet;
import com.wurmonline.server.zones.NoSuchZoneException;
import java.util.logging.Level;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.bodys.BodyFactory;
import com.wurmonline.server.Server;
import java.util.BitSet;
import com.wurmonline.server.creatures.ai.Path;
import com.wurmonline.server.modifiers.DoubleValueModifier;
import java.util.Set;
import com.wurmonline.server.bodys.Body;
import com.wurmonline.server.items.Trade;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.CreatureTypes;
import com.wurmonline.shared.constants.ProtoConstants;
import com.wurmonline.server.modifiers.ModifierTypes;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.combat.CombatConstants;
import com.wurmonline.server.MiscConstants;

public abstract class CreatureStatus implements MiscConstants, CombatConstants, TimeConstants, ModifierTypes, ProtoConstants, CreatureTypes
{
    Creature statusHolder;
    private static Logger logger;
    private static final int FAT_INCREASE_LEVEL = 120;
    private static final int FAT_DECREASE_LEVEL = 1;
    private CreaturePos position;
    private byte diseaseCounter;
    boolean moving;
    private boolean unconscious;
    public boolean visible;
    private Trade trade;
    CreatureTemplate template;
    byte sex;
    long inventoryId;
    long bodyId;
    Body body;
    int thirst;
    int hunger;
    int stamina;
    float nutrition;
    private static int DAILY_CALORIES;
    private static int DAILY_CARBS;
    private static int DAILY_FATS;
    private static int DAILY_PROTEINS;
    private static float CCFP_MAX_PERCENTAGE;
    private static float CCFP_REDUCE_AMOUNT;
    float calories;
    float carbs;
    float fats;
    float proteins;
    public int damage;
    long buildingId;
    private int lastSentThirst;
    private int lastSentHunger;
    public int lastSentStamina;
    private int lastSentDamage;
    private boolean normalRegen;
    private float stunned;
    private Set<DoubleValueModifier> modifiers;
    private Path path;
    public static final int MOVE_MOD_LIMIT = 2000;
    private final DoubleValueModifier moveMod;
    public byte kingdom;
    boolean dead;
    public long lastPolledAge;
    public int age;
    public byte fat;
    private int fatCounter;
    public SpellEffects spellEffects;
    boolean reborn;
    public float loyalty;
    long lastPolledLoyalty;
    boolean stealth;
    boolean offline;
    boolean stayOnline;
    protected int detectInvisCounter;
    byte modtype;
    private float lastDamPercSent;
    protected long traits;
    protected BitSet traitbits;
    protected long mother;
    protected long father;
    public byte disease;
    protected long lastGroomed;
    private boolean statusExists;
    private boolean changed;
    
    CreatureStatus(final Creature creature, final float posX, final float posY, final float aRot, final int aLayer) throws Exception {
        this.position = null;
        this.diseaseCounter = 0;
        this.moving = false;
        this.unconscious = false;
        this.visible = true;
        this.trade = null;
        this.sex = 0;
        this.inventoryId = -10L;
        this.bodyId = -10L;
        this.stamina = 65535;
        this.nutrition = 0.0f;
        this.calories = 0.25f;
        this.carbs = 0.25f;
        this.fats = 0.25f;
        this.proteins = 0.25f;
        this.buildingId = -10L;
        this.lastSentThirst = 0;
        this.lastSentHunger = 0;
        this.lastSentStamina = 0;
        this.lastSentDamage = 0;
        this.normalRegen = true;
        this.stunned = 0.0f;
        this.modifiers = null;
        this.path = null;
        this.moveMod = new DoubleValueModifier(-0.5);
        this.kingdom = 0;
        this.dead = false;
        this.lastPolledAge = 0L;
        this.age = 0;
        this.fat = 50;
        this.fatCounter = Server.rand.nextInt(1000);
        this.spellEffects = null;
        this.reborn = false;
        this.loyalty = 0.0f;
        this.lastPolledLoyalty = 0L;
        this.stealth = false;
        this.offline = false;
        this.stayOnline = false;
        this.detectInvisCounter = 0;
        this.modtype = 0;
        this.lastDamPercSent = -1.0f;
        this.traits = 0L;
        this.traitbits = new BitSet(64);
        this.mother = -10L;
        this.father = -10L;
        this.disease = 0;
        this.lastGroomed = System.currentTimeMillis();
        this.statusExists = false;
        this.changed = false;
        this.statusHolder = creature;
        this.template = creature.template;
        if (this.template != null) {
            this.body = BodyFactory.getBody(creature, this.template.getBodyType(), this.template.getCentimetersHigh(), this.template.getCentimetersLong(), this.template.getCentimetersWide());
        }
        this.setPosition(CreaturePos.getPosition(creature.getWurmId()));
        if (this.getPosition() == null) {
            int zid = 0;
            try {
                zid = Zones.getZoneIdFor((int)posX >> 2, (int)posY >> 2, aLayer >= 0);
            }
            catch (NoSuchZoneException ex) {
                CreatureStatus.logger.log(Level.INFO, this.statusHolder.getWurmId() + "," + this.statusHolder.getName() + ": " + ex.getMessage(), ex);
            }
            this.setPosition(new CreaturePos(creature.getWurmId(), posX, posY, 0.0f, aRot, zid, aLayer, -10L, true));
        }
        if (this.template != null) {
            this.getPosition().setPosZ(Zones.calculateHeight(this.getPosition().getPosX(), this.getPosition().getPosY(), this.isOnSurface()), false);
            if (this.isOnSurface()) {
                if (!creature.isSubmerged()) {
                    this.getPosition().setPosZ(Math.max(-1.25f, this.getPosition().getPosZ()), false);
                }
                else if (this.getPosition().getPosZ() < 0.0f) {
                    if (creature.isFloating()) {
                        this.getPosition().setPosZ(creature.getTemplate().offZ, false);
                    }
                    else {
                        this.getPosition().setPosZ(this.getPosition().getPosZ() / 2.0f, false);
                    }
                }
            }
        }
    }
    
    CreatureStatus() {
        this.position = null;
        this.diseaseCounter = 0;
        this.moving = false;
        this.unconscious = false;
        this.visible = true;
        this.trade = null;
        this.sex = 0;
        this.inventoryId = -10L;
        this.bodyId = -10L;
        this.stamina = 65535;
        this.nutrition = 0.0f;
        this.calories = 0.25f;
        this.carbs = 0.25f;
        this.fats = 0.25f;
        this.proteins = 0.25f;
        this.buildingId = -10L;
        this.lastSentThirst = 0;
        this.lastSentHunger = 0;
        this.lastSentStamina = 0;
        this.lastSentDamage = 0;
        this.normalRegen = true;
        this.stunned = 0.0f;
        this.modifiers = null;
        this.path = null;
        this.moveMod = new DoubleValueModifier(-0.5);
        this.kingdom = 0;
        this.dead = false;
        this.lastPolledAge = 0L;
        this.age = 0;
        this.fat = 50;
        this.fatCounter = Server.rand.nextInt(1000);
        this.spellEffects = null;
        this.reborn = false;
        this.loyalty = 0.0f;
        this.lastPolledLoyalty = 0L;
        this.stealth = false;
        this.offline = false;
        this.stayOnline = false;
        this.detectInvisCounter = 0;
        this.modtype = 0;
        this.lastDamPercSent = -1.0f;
        this.traits = 0L;
        this.traitbits = new BitSet(64);
        this.mother = -10L;
        this.father = -10L;
        this.disease = 0;
        this.lastGroomed = System.currentTimeMillis();
        this.statusExists = false;
        this.changed = false;
    }
    
    public final void addModifier(final DoubleValueModifier modifier) {
        if (this.modifiers == null) {
            this.modifiers = new HashSet<DoubleValueModifier>();
        }
        this.modifiers.add(modifier);
    }
    
    public final void createNewBody() throws Exception {
        (this.body = BodyFactory.getBody(this.statusHolder, this.template.getBodyType(), this.template.getCentimetersHigh(), this.template.getCentimetersLong(), this.template.getCentimetersWide())).createBodyParts();
        final Item bodypart = this.body.getBodyItem();
        this.bodyId = bodypart.getWurmId();
    }
    
    public final void createNewPossessions() throws Exception {
        this.inventoryId = this.statusHolder.createPossessions();
        this.setChanged(true);
        CreatureStatus.logger.log(Level.INFO, "New inventory id for " + this.statusHolder.getName() + " is " + this.inventoryId);
        this.setInventoryId(this.inventoryId);
    }
    
    public final Path getPath() {
        return this.path;
    }
    
    public final void setPath(@Nullable final Path newPath) {
        if (newPath == null) {
            if (this.statusHolder.isDominated() && this.statusHolder.hasOrders()) {
                final Order order = this.statusHolder.getFirstOrder();
                if (order.isTile() && order.isResolved(this.statusHolder.getCurrentTile().tilex, this.statusHolder.getCurrentTile().tiley, this.statusHolder.getLayer())) {
                    this.statusHolder.removeOrder(order);
                }
            }
            if (this.path != null) {
                this.path.clear();
            }
            this.statusHolder.pathRecalcLength = 0;
        }
        this.path = newPath;
    }
    
    public final void removeModifier(final DoubleValueModifier modifier) {
        if (this.modifiers != null) {
            this.modifiers.remove(modifier);
        }
    }
    
    final double getModifierValuesFor(final int type) {
        double toReturn = 0.0;
        if (this.modifiers != null) {
            for (final DoubleValueModifier val : this.modifiers) {
                if (val.getType() == type) {
                    toReturn += val.getModifier();
                }
            }
        }
        return toReturn;
    }
    
    private void increaseFat() {
        if (this.fat < 125) {
            this.setChanged(true);
            this.fat = (byte)Math.min(125, this.fat + 1);
            if (this.statusHolder.isPlayer() && this.fat == 125) {
                this.statusHolder.achievement(149);
            }
            if ((this.fat == 120 || this.fat == 1) && !this.statusHolder.isPlayer()) {
                this.statusHolder.refreshVisible();
            }
        }
    }
    
    private int checkDisease() {
        final int nums = this.statusHolder.getCurrentTile().getCreatures().length;
        if (this.diseaseCounter == 8) {
            float villRatio = 10.0f;
            if (this.statusHolder.getCurrentVillage() != null) {
                villRatio = this.statusHolder.getCurrentVillage().getCreatureRatio();
            }
            if (nums < 3 && (villRatio >= Village.OPTIMUMCRETRATIO || Server.rand.nextInt((int)Math.max(1.0f, Village.OPTIMUMCRETRATIO - villRatio)) == 0)) {
                if (this.disease > 0) {
                    byte mod = 2;
                    if (System.currentTimeMillis() - this.lastGroomed < 172800000L) {
                        mod = 5;
                    }
                    if (this.hunger < 10000) {
                        mod += 5;
                    }
                    if (this.fat > 100) {
                        mod += 5;
                    }
                    if (this.fat < 2) {
                        mod -= 1.0f;
                    }
                    this.statusHolder.setDisease((byte)Math.max(0, this.disease - mod));
                }
            }
            else if (this.modtype != 11 && !this.statusHolder.isKingdomGuard() && !this.statusHolder.isSpiritGuard() && !this.statusHolder.isUnique() && (!this.statusHolder.isPlayer() || Server.rand.nextInt(4) == 0)) {
                float healthMod = 1.0f;
                if (this.isTraitBitSet(19)) {
                    ++healthMod;
                }
                if (this.isTraitBitSet(20)) {
                    healthMod -= 0.5f;
                }
                if (System.currentTimeMillis() - this.lastGroomed < 172800000L) {
                    healthMod -= 0.3f;
                }
                if (this.hunger < 10000) {
                    healthMod -= 0.1f;
                }
                else if (this.hunger > 60000) {
                    healthMod += 0.1f;
                }
                if (this.fat > 100) {
                    healthMod -= 0.1f;
                }
                if (this.fat < 2) {
                    healthMod += 0.1f;
                }
                if (this.disease > 0) {
                    this.statusHolder.setDisease((byte)Math.min(120.0f, this.disease + 5 * nums * healthMod));
                }
                else if (nums > 2 && Server.rand.nextInt(100) < nums * healthMod) {
                    this.statusHolder.setDisease((byte)1);
                }
            }
        }
        return nums;
    }
    
    protected void decreaseCCFPValues() {
        this.calories = Math.max(this.calories - CreatureStatus.CCFP_REDUCE_AMOUNT, 0.0f);
        this.carbs = Math.max(this.carbs - CreatureStatus.CCFP_REDUCE_AMOUNT, 0.0f);
        this.fats = Math.max(this.fats - CreatureStatus.CCFP_REDUCE_AMOUNT, 0.0f);
        this.proteins = Math.max(this.proteins - CreatureStatus.CCFP_REDUCE_AMOUNT, 0.0f);
    }
    
    protected void clearCCFPValues() {
        this.calories = 0.0f;
        this.carbs = 0.0f;
        this.fats = 0.0f;
        this.proteins = 0.0f;
        this.sendHunger();
    }
    
    protected void clearHunger() {
        this.hunger = 45000;
        this.sendHunger();
    }
    
    protected void clearThirst() {
        this.thirst = 65535;
        this.sendThirst();
    }
    
    protected boolean decreaseFat() {
        final int originalfat = this.fat;
        final int nums = this.checkDisease();
        if (this.fat > 3 && this.fat < 121) {
            byte newfatMod = -2;
            if (nums > 2) {
                newfatMod -= 2;
            }
            if (System.currentTimeMillis() - this.lastGroomed > 172800000L) {
                newfatMod -= 3;
            }
            if (this.modtype != 11 && this.disease > 0) {
                newfatMod -= 2;
            }
            this.fat = (byte)Math.max(0, this.fat + newfatMod);
            if (this.statusHolder.isPlayer() && this.fat == 0) {
                this.statusHolder.achievement(148);
            }
            this.statusHolder.achievement(147);
        }
        else {
            this.fat = (byte)Math.max(0, this.fat - 1);
            if (this.statusHolder.isPlayer() && this.fat == 0) {
                this.statusHolder.achievement(148);
            }
        }
        if ((this.fat == 120 || this.fat == 1) && !this.statusHolder.isPlayer()) {
            this.statusHolder.refreshVisible();
        }
        if (originalfat != this.fat) {
            this.setChanged(true);
        }
        return this.fat != 0;
    }
    
    public final boolean pollFat() {
        if (Server.rand.nextBoolean()) {
            ++this.fatCounter;
        }
        if (this.fatCounter > (this.statusHolder.isPlayer() ? 50 : 1500)) {
            ++this.diseaseCounter;
            this.fatCounter = 0;
            if (this.hunger < 10000 && this.nutrition > (this.statusHolder.isPlayer() ? 0.4f : 0.1f)) {
                this.increaseFat();
                this.checkDisease();
            }
            else if (this.hunger >= 60000) {
                this.decreaseFat();
                if (!this.statusHolder.isPlayer()) {
                    if ((this.statusHolder.isHerbivore() || this.statusHolder.isOmnivore()) && !this.statusHolder.isUnique()) {
                        if (this.fat < 1 && !this.statusHolder.isInvulnerable()) {
                            return true;
                        }
                        this.hunger = 10000;
                    }
                    else if (Server.rand.nextInt(10) == 0) {
                        this.hunger = 10000;
                    }
                }
                else if (this.fat > 0) {
                    this.statusHolder.getCommunicator().sendNormalServerMessage("Your hunger goes away as you fast.");
                    this.nutrition = Math.max(0.1f, this.nutrition - 0.2f);
                    this.hunger = 10000;
                }
            }
            else {
                this.checkDisease();
            }
            if (this.diseaseCounter == 8 && this.disease > 50) {
                final boolean canSpread = this.statusHolder.getHitched() == null || !this.statusHolder.isMoving();
                if (canSpread) {
                    this.statusHolder.getCurrentTile().checkDiseaseSpread();
                }
                if (this.disease > 100 && Server.rand.nextBoolean() && !this.statusHolder.isInvulnerable()) {
                    return true;
                }
            }
            if (this.diseaseCounter == 8) {
                this.diseaseCounter = 0;
            }
        }
        return false;
    }
    
    final String getFatString() {
        if (this.disease > 0 && this.modtype != 11) {
            return "diseased ";
        }
        if (this.statusHolder.isNeedFood()) {
            if (this.fat <= 1) {
                return "starving ";
            }
            if (this.fat > 120) {
                return "fat ";
            }
        }
        return "";
    }
    
    public final String getBodyType() {
        final double strength = this.statusHolder.getStrengthSkill();
        if (strength < 21.0) {
            if (this.fat < 10) {
                return this.statusHolder.getHeSheItString() + " is only skin and bones.";
            }
            if (this.fat < 30) {
                return this.statusHolder.getHeSheItString() + " is very thin.";
            }
            if (this.fat > 100) {
                return this.statusHolder.getHeSheItString() + " is extremely well nourished.";
            }
            if (this.fat > 70) {
                return this.statusHolder.getHeSheItString() + " is a bit round.";
            }
            return this.statusHolder.getHeSheItString() + " has a normal build.";
        }
        else if (strength < 25.0) {
            if (this.fat < 10) {
                return this.statusHolder.getHeSheItString() + " has muscles, but looks a bit undernourished.";
            }
            if (this.fat < 30) {
                return this.statusHolder.getHeSheItString() + " is strong but lacks body fat.";
            }
            if (this.fat > 100) {
                return this.statusHolder.getHeSheItString() + " is strong and has a good reserve of fat.";
            }
            if (this.fat > 70) {
                return this.statusHolder.getHeSheItString() + " is strong and well nourished.";
            }
            return this.statusHolder.getHeSheItString() + " is well defined.";
        }
        else {
            if (this.fat < 10) {
                return this.statusHolder.getHeSheItString() + " has large muscles, but looks a bit undernourished.";
            }
            if (this.fat < 30) {
                return this.statusHolder.getHeSheItString() + " looks very strong, but lacks body fat.";
            }
            if (this.fat > 100) {
                return this.statusHolder.getHeSheItString() + " is very strong and has a good reserve of fat.";
            }
            if (this.fat > 70) {
                return this.statusHolder.getHeSheItString() + " is very strong and well nourished.";
            }
            return this.statusHolder.getHeSheItString() + " is very well defined.";
        }
    }
    
    public final void setLayer(final int aLayer) {
        this.getPosition().setLayer(aLayer);
    }
    
    public final boolean isOnSurface() {
        return this.getPosition().getLayer() >= 0;
    }
    
    public final CreatureTemplate getTemplate() {
        return this.template;
    }
    
    public final byte getSex() {
        return this.sex;
    }
    
    public final void setBuildingId(final long id) {
        this.buildingId = id;
    }
    
    public final long getBuildingId() {
        return this.buildingId;
    }
    
    public final Body getBody() {
        return this.body;
    }
    
    public final void setSex(final byte aSex) {
        this.sex = aSex;
        this.setChanged(true);
    }
    
    public final void setPositionX(final float pos) {
        this.getPosition().setPosX(pos);
        this.statusHolder.updateEffects();
    }
    
    public final void setPositionY(final float pos) {
        this.getPosition().setPosY(pos);
        this.statusHolder.updateEffects();
    }
    
    public final void setPositionZ(final float pos) {
        this.setPositionZ(pos, false);
        this.statusHolder.updateEffects();
    }
    
    public final void setPositionZ(final float pos, final boolean force) {
        this.getPosition().setPosZ(pos, force);
        this.statusHolder.updateEffects();
    }
    
    public final void setPositionXYZ(final float posX, final float posY, final float posZ) {
        this.getPosition().setPosX(posX);
        this.getPosition().setPosY(posY);
        this.getPosition().setPosZ(posZ, false);
        this.statusHolder.updateEffects();
    }
    
    public final void setRotation(final float r) {
        this.getPosition().setRotation(Creature.normalizeAngle(r));
    }
    
    public final boolean isMoving() {
        return this.moving;
    }
    
    public final void setMoving(final boolean aMoving) {
        this.moving = aMoving;
        if (aMoving) {
            PlayerTutorial.firePlayerTrigger(this.statusHolder.getWurmId(), PlayerTutorial.PlayerTrigger.MOVED_PLAYER);
        }
    }
    
    public final boolean hasNormalRegen() {
        return this.normalRegen;
    }
    
    public final void setNormalRegen(final boolean nregen) {
        this.normalRegen = nregen;
    }
    
    public final void setUnconscious(final boolean aUnconscious) {
        this.unconscious = aUnconscious;
    }
    
    public final boolean isUnconscious() {
        return this.unconscious;
    }
    
    public final Vector2f getPosition2f() {
        return this.getPosition().getPos2f();
    }
    
    public final Vector3f getPosition3f() {
        return this.getPosition().getPos3f();
    }
    
    public final float getPositionX() {
        return this.getPosition().getPosX();
    }
    
    public final float getPositionY() {
        return this.getPosition().getPosY();
    }
    
    public final long getInventoryId() {
        return this.inventoryId;
    }
    
    public final float getPositionZ() {
        return this.getPosition().getPosZ();
    }
    
    public final float getRotation() {
        return this.getPosition().getRotation();
    }
    
    public final long getBridgeId() {
        return this.getPosition().getBridgeId();
    }
    
    public final byte getDir() {
        return (byte)(((int)this.getRotation() + 45) / 90 * 2 % 8);
    }
    
    public final long getBodyId() {
        return this.bodyId;
    }
    
    public final float getStunned() {
        return this.stunned;
    }
    
    public final void setStunned(final float stunTime) {
        this.setStunned(stunTime, true);
    }
    
    public final void setStunned(final float stunTime, final boolean applyResistance) {
        if (this.stunned > 0.0f && stunTime <= 0.0f) {
            this.statusHolder.getCombatHandler().setCurrentStance(-1, (byte)0);
            this.statusHolder.getMovementScheme().setFightMoveMod(false);
            if (!this.statusHolder.isDead()) {
                Server.getInstance().broadCastAction(this.statusHolder.getNameWithGenus() + " regains " + this.statusHolder.getHisHerItsString() + " bearings.", this.statusHolder, 2, true);
            }
            this.statusHolder.getCommunicator().sendStunned(false);
            this.statusHolder.getCommunicator().sendRemoveSpellEffect(SpellEffectsEnum.STUNNED);
            this.stunned = Math.max(0.0f, stunTime);
        }
        else if (this.stunned <= 0.0f && stunTime > 0.0f) {
            SpellResistance currSpellRes = null;
            if (applyResistance) {
                currSpellRes = this.statusHolder.getSpellResistance((short)SpellEffectsEnum.STUNNED.getTypeId());
            }
            float successChance = 1.0f;
            if (applyResistance && this.statusHolder.isPlayer()) {
                if (currSpellRes == null) {
                    this.statusHolder.addSpellResistance((short)SpellEffectsEnum.STUNNED.getTypeId());
                    currSpellRes = this.statusHolder.getSpellResistance((short)SpellEffectsEnum.STUNNED.getTypeId());
                    if (currSpellRes != null) {
                        currSpellRes.setResistance(0.5f, 0.023f);
                    }
                }
                else {
                    successChance = 1.0f - currSpellRes.getResistance();
                }
            }
            if (Server.rand.nextFloat() < successChance || !applyResistance) {
                this.statusHolder.getCombatHandler().setCurrentStance(-1, (byte)8);
                this.statusHolder.maybeInterruptAction(200000);
                this.statusHolder.getMovementScheme().setFightMoveMod(true);
                this.statusHolder.getCommunicator().sendStunned(true);
                this.statusHolder.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.STUNNED, (int)stunTime, stunTime);
                this.stunned = Math.max(0.0f, stunTime);
                this.statusHolder.playAnimation("stun", false);
            }
            else {
                Server.getInstance().broadCastAction(this.statusHolder.getNameWithGenus() + " brushes away the stunned feeling.", this.statusHolder, 2, true);
            }
        }
        else {
            this.stunned = Math.max(0.0f, stunTime);
        }
        this.sendStateString();
    }
    
    public final boolean modifyWounds(int dam) {
        boolean _dead = false;
        if (this.statusHolder.getTemplate().getCreatureAI() != null) {
            dam = this.statusHolder.getTemplate().getCreatureAI().woundDamageChanged(this.statusHolder, dam);
        }
        this.setChanged(true);
        this.damage += dam;
        if (this.damage >= 65535) {
            if (this.statusHolder.getPower() >= 3) {
                if (CreatureStatus.logger.isLoggable(Level.FINE)) {
                    CreatureStatus.logger.fine("Deity with id=" + this.statusHolder.getWurmId() + ", damage has reached or exceeded the maximum so it is time heal. Damage was: " + this.damage + ", creature/player: " + this.statusHolder);
                }
                this.damage = 0;
                this.statusHolder.getBody().healFully();
                this.statusHolder.getCommunicator().sendCombatAlertMessage("You died but were instantly healed.");
            }
            else {
                if (CreatureStatus.logger.isLoggable(Level.FINE)) {
                    CreatureStatus.logger.fine("Creature/Player with id=" + this.statusHolder.getWurmId() + ", damage has reached or exceeded the maximum so it is time to die. Damage: " + this.damage + ", creature/player: " + this.statusHolder);
                }
                this.damage = 65535;
                this.statusHolder.die(false, "Damage");
                _dead = true;
            }
        }
        else if (this.damage < 0) {
            this.damage = 0;
        }
        if (!this.statusHolder.isUnique()) {
            this.stamina = Math.min(this.stamina, 65535 - this.damage);
        }
        this.sendStamina();
        return _dead;
    }
    
    public final void removeWounds() {
        this.damage = 0;
        this.sendStamina();
    }
    
    protected final void setTraitBits(final long bits) {
        for (int x = 0; x < 64; ++x) {
            if (x != 28 || this.statusHolder.getName().contains("traitor")) {
                if (x == 28 && this.statusHolder.getName().contains("traitor")) {
                    final Effect traitorEffect = EffectFactory.getInstance().createGenericEffect(this.statusHolder.getWurmId(), "traitor", this.statusHolder.getPosX(), this.statusHolder.getPosY(), this.statusHolder.getPositionZ() + this.statusHolder.getHalfHeightDecimeters() / 10.0f, this.statusHolder.isOnSurface(), -1.0f, this.statusHolder.getStatus().getRotation());
                    this.statusHolder.addEffect(traitorEffect);
                }
                if (x == 0) {
                    if ((bits & 0x1L) == 0x1L) {
                        this.traitbits.set(x, true);
                    }
                    else {
                        this.traitbits.set(x, false);
                    }
                }
                else if ((bits >> x & 0x1L) == 0x1L) {
                    this.traitbits.set(x, true);
                }
                else {
                    this.traitbits.set(x, false);
                }
            }
        }
    }
    
    protected final long getTraitBits() {
        long ret = 0L;
        for (int x = 0; x < 64; ++x) {
            if (this.traitbits.get(x)) {
                ret += 1L << x;
            }
        }
        return ret;
    }
    
    public final int traitsCount() {
        int cnt = 0;
        for (int x = 0; x < 64; ++x) {
            if (this.traitbits.get(x) && Traits.getTraitString(x).length() > 0) {
                ++cnt;
            }
        }
        return cnt;
    }
    
    public final boolean isTraitBitSet(final int setting) {
        return this.traitbits.get(setting);
    }
    
    protected final boolean removeRandomNegativeTrait() {
        if (this.traits == 0L) {
            return false;
        }
        for (int x = 0; x < 64; ++x) {
            if (Traits.isTraitNegative(x) && this.isTraitBitSet(x)) {
                this.setTraitBit(x, false);
                return true;
            }
        }
        return false;
    }
    
    public final void setTraitBit(final int setting, final boolean value) {
        this.traitbits.set(setting, value);
        this.traits = this.getTraitBits();
        try {
            this.setInheritance(this.traits, this.mother, this.father);
        }
        catch (IOException iox) {
            CreatureStatus.logger.log(Level.WARNING, iox.getMessage());
        }
    }
    
    public final float getStaminaSkill() {
        try {
            return (float)this.statusHolder.getSkills().getSkill(103).getKnowledge(0.0);
        }
        catch (NoSuchSkillException nss) {
            CreatureStatus.logger.log(Level.WARNING, "Creature has no stamina :" + this.statusHolder.getName() + "," + nss.getMessage(), nss);
            this.statusHolder.getSkills().learn(103, 20.0f);
            return 20.0f;
        }
    }
    
    public final void resetCreatureStamina() {
        final int currMax = 65535 - this.damage;
        final int oldStamina = this.stamina;
        this.stamina = currMax;
        if (this.stamina != oldStamina) {
            this.setChanged(true);
        }
        this.checkStaminaEffects(oldStamina);
    }
    
    public final void modifyStamina(final float staminaPoints) {
        if (!this.statusHolder.isUnique() || this.statusHolder.getPower() >= 4) {
            if ((staminaPoints > 0.0f && this.stamina < 65535) || (staminaPoints < 0.0f && this.stamina > 1)) {
                float staminaMod = this.getStaminaSkill() / 100.0f;
                final int currMax = 65535 - this.damage;
                final int oldStamina = this.stamina;
                if (staminaPoints > 0.0f) {
                    this.stamina += (int)(staminaPoints * Math.max(0.0, 1.0f + staminaMod + this.getModifierValuesFor(1)));
                }
                else if (staminaPoints < 0.0f) {
                    if (this.hunger < 10000) {
                        staminaMod += 0.05;
                    }
                    if (this.statusHolder.getCultist() != null && this.statusHolder.getCultist().usesNoStamina()) {
                        staminaMod += 0.3f;
                    }
                    final float caloriesModifier = 1.0f / (1.0f + Math.min(this.calories, 1.0f) / 3.0f);
                    staminaMod = (1.0f - staminaMod) * caloriesModifier;
                    this.stamina += (int)Math.min(staminaPoints * staminaMod * ItemBonus.getStaminaReductionBonus(this.statusHolder), staminaPoints * 0.01f);
                }
                this.stamina = Math.max(1, this.stamina);
                this.stamina = Math.min(this.stamina, currMax);
                if (this.crossedStatusBorder(oldStamina, this.stamina)) {
                    this.sendStateString();
                }
                if (oldStamina != this.stamina) {
                    this.setChanged(true);
                }
                this.sendStamina();
                this.checkStaminaEffects(oldStamina);
                if (staminaPoints < 0.0f) {
                    this.modifyThirst(Math.max(1.0f, -staminaPoints / 1000.0f));
                }
            }
        }
        else {
            if (this.stamina != 65535) {
                this.setChanged(true);
            }
            this.stamina = 65535;
        }
    }
    
    public final void modifyStamina2(final float staminaPercent) {
        if (!this.statusHolder.isUnique()) {
            final int currMax = 65535 - this.damage;
            final int oldStamina = this.stamina;
            this.stamina += (int)(staminaPercent * currMax);
            this.stamina = Math.max(1, this.stamina);
            this.stamina = Math.min(this.stamina, currMax);
            if (this.crossedStatusBorder(oldStamina, this.stamina)) {
                this.sendStateString();
            }
            if (oldStamina != this.stamina) {
                this.setChanged(true);
            }
            this.sendStamina();
            this.checkStaminaEffects(oldStamina);
        }
        else {
            if (this.stamina != 65535) {
                this.setChanged(true);
            }
            this.stamina = 65535;
        }
    }
    
    public final void checkStaminaEffects(final int oldStamina) {
        if (this.stamina < 2000 && oldStamina >= 2000) {
            this.statusHolder.getMovementScheme().addModifier(this.moveMod);
        }
        else if (this.stamina >= 2000 && oldStamina < 2000) {
            this.statusHolder.getMovementScheme().removeModifier(this.moveMod);
        }
    }
    
    public final void sendStamina() {
        if (this.stamina > this.lastSentStamina + 100 || this.stamina < this.lastSentStamina - 100 || this.damage > this.lastSentDamage + 100 || this.damage < this.lastSentDamage - 100) {
            this.lastSentStamina = this.stamina;
            this.lastSentDamage = this.damage;
            this.statusHolder.getCommunicator().sendStamina(this.stamina, this.damage);
            final float damp = this.calcDamPercent();
            if (damp != this.lastDamPercSent) {
                this.statusHolder.sendDamage(damp);
                this.lastDamPercSent = damp;
            }
            if (this.statusHolder.isPlayer() && damp < 90.0f) {
                PlonkData.FIRST_DAMAGE.trigger(this.statusHolder);
            }
            if (this.statusHolder.isPlayer() && this.calcStaminaPercent() < 30.0f) {
                PlonkData.LOW_STAMINA.trigger(this.statusHolder);
            }
        }
    }
    
    public final float calcDamPercent() {
        if (this.damage == 0) {
            return 100.0f;
        }
        return Math.max(1.0f, (65535 - this.damage) / 65535.0f * 100.0f);
    }
    
    public final float calcStaminaPercent() {
        if (this.stamina == 65535) {
            return 100.0f;
        }
        return this.stamina / 65535.0f * 100.0f;
    }
    
    public final int modifyHunger(final int hungerModification, final float newNutritionLevel) {
        return this.modifyHunger(hungerModification, newNutritionLevel, -1.0f, -1.0f, -1.0f, -1.0f);
    }
    
    public final int modifyHunger(final int hungerModification, float newNutritionLevel, final float addCalories, final float addCarbs, final float addFats, final float addProteins) {
        final int oldHunger = this.hunger;
        int localHungerModification = hungerModification;
        if (localHungerModification > 0) {
            localHungerModification *= (int)(1.0f - Math.min(this.proteins, 1.0f) / 3.0f);
        }
        this.hunger = Math.min(65535, Math.max(1, this.hunger + localHungerModification));
        if (this.hunger < 65535) {
            final int realHungerModification = this.hunger - oldHunger;
            if (realHungerModification < 0) {
                newNutritionLevel = Math.min(newNutritionLevel * 1.1f, 0.99f);
                if (this.nutrition > 0.0f) {
                    final float oldNutPercent = Math.max(1.0f, 65535 - oldHunger) / Math.max(1.0f, 65535 - this.hunger);
                    final float newNutPercent = -realHungerModification / Math.max(1.0f, 65535 - this.hunger);
                    this.nutrition = oldNutPercent * this.nutrition + newNutPercent * newNutritionLevel;
                }
                else {
                    this.nutrition = newNutritionLevel;
                }
            }
            if (addCalories >= 0.0f) {
                this.addToCCFPValues(addCalories, addCarbs, addFats, addProteins);
            }
        }
        else {
            this.nutrition = 0.0f;
        }
        if (this.crossedStatusBorder(oldHunger, this.hunger)) {
            this.sendStateString();
        }
        if (this.hunger < this.lastSentHunger - 100 || this.hunger > this.lastSentHunger + 100) {
            this.lastSentHunger = this.hunger;
            this.sendHunger();
            if (this.statusHolder.isPlayer() && PlonkData.HUNGRY.hasSeenThis(this.statusHolder)) {
                final float hungerPercent = 100.0f - 100.0f * (this.hunger / 65535.0f);
                if (hungerPercent <= 50.0f) {
                    PlonkData.HUNGRY.trigger(this.statusHolder);
                }
            }
        }
        return this.hunger;
    }
    
    void addToCCFPValues(final float addCalories, final float addCarbs, final float addFats, final float addProteins) {
        this.calories = Math.min(this.calories + addCalories / CreatureStatus.DAILY_CALORIES, CreatureStatus.CCFP_MAX_PERCENTAGE);
        this.carbs = Math.min(this.carbs + addCarbs / CreatureStatus.DAILY_CARBS, CreatureStatus.CCFP_MAX_PERCENTAGE);
        this.fats = Math.min(this.fats + addFats / CreatureStatus.DAILY_FATS, CreatureStatus.CCFP_MAX_PERCENTAGE);
        this.proteins = Math.min(this.proteins + addProteins / CreatureStatus.DAILY_PROTEINS, CreatureStatus.CCFP_MAX_PERCENTAGE);
    }
    
    public final boolean refresh(final float newNutrition, final boolean fullStamina) {
        this.hunger = 0;
        this.thirst = 0;
        if (fullStamina) {
            final int oldStam = this.stamina;
            this.nutrition = 0.99f;
            this.stamina = 65535;
            this.setChanged(true);
            this.sendStamina();
            this.sendStateString();
            this.checkStaminaEffects(oldStam);
            return true;
        }
        if (this.nutrition < 0.5f) {
            this.setChanged(true);
            this.nutrition = 0.5f;
            this.sendStateString();
            return true;
        }
        return false;
    }
    
    public void setMaxCCFP() {
        this.calories = CreatureStatus.CCFP_MAX_PERCENTAGE;
        this.carbs = CreatureStatus.CCFP_MAX_PERCENTAGE;
        this.fats = CreatureStatus.CCFP_MAX_PERCENTAGE;
        this.proteins = CreatureStatus.CCFP_MAX_PERCENTAGE;
    }
    
    private boolean crossedStatusBorder(final int oldnumber, final int newnumber) {
        return (oldnumber >= 65535 && newnumber < 65535) || (oldnumber > 60000 && newnumber <= 60000) || (oldnumber > 45000 && newnumber <= 45000) || (oldnumber > 20000 && newnumber <= 20000) || (oldnumber > 10000 && newnumber <= 10000) || (oldnumber > 1000 && newnumber <= 1000) || (oldnumber > 1 && newnumber <= 1) || (oldnumber < 1 && newnumber >= 1) || (oldnumber < 1000 && newnumber >= 1000) || (oldnumber < 10000 && newnumber >= 10000) || (oldnumber < 20000 && newnumber >= 20000) || (oldnumber < 45000 && newnumber >= 45000) || (oldnumber < 60000 && newnumber >= 60000) || (oldnumber < 65535 && newnumber >= 65535);
    }
    
    public float getCaloriesAsPercent() {
        return Math.min(this.calories * 100.0f, 100.0f);
    }
    
    public float getCarbsAsPercent() {
        return Math.min(this.carbs * 100.0f, 100.0f);
    }
    
    public float getFatsAsPercent() {
        return Math.min(this.fats * 100.0f, 100.0f);
    }
    
    public float getProteinsAsPercent() {
        return Math.min(this.proteins * 100.0f, 100.0f);
    }
    
    public final void sendHunger() {
        this.statusHolder.getCommunicator().sendHunger(this.hunger, this.nutrition, this.getCaloriesAsPercent(), this.getCarbsAsPercent(), this.getFatsAsPercent(), this.getProteinsAsPercent());
    }
    
    public final int modifyThirst(final float thirstModification) {
        return this.modifyThirst(thirstModification, -1.0f, -1.0f, -1.0f, 1.0f);
    }
    
    public final int modifyThirst(final float thirstModification, final float addCalories, final float addCarbs, final float addFats, final float addProteins) {
        final int oldThirst = this.thirst;
        float realThirstModification = thirstModification;
        if (realThirstModification > 0.0f) {
            realThirstModification *= 1.0f - Math.min(this.carbs, 1.0f) / 3.0f;
        }
        this.thirst += (int)realThirstModification;
        this.thirst = Math.max(1, this.thirst);
        this.thirst = Math.min(65535, this.thirst);
        if (this.crossedStatusBorder(oldThirst, this.thirst)) {
            this.sendStateString();
        }
        if (this.thirst < this.lastSentThirst - 100 || this.thirst > this.lastSentThirst + 100) {
            this.lastSentThirst = this.thirst;
            this.sendThirst();
            if (!PlonkData.THIRSTY.hasSeenThis(this.statusHolder)) {
                final float thirstPercent = 100.0f - 100.0f * (this.thirst / 65535.0f);
                if (thirstPercent <= 50.0f) {
                    PlonkData.THIRSTY.trigger(this.statusHolder);
                }
            }
        }
        if (addCalories >= 0.0f) {
            this.addToCCFPValues(addCalories, addCarbs, addFats, addProteins);
        }
        return this.thirst;
    }
    
    public final void sendThirst() {
        this.statusHolder.getCommunicator().sendThirst(this.thirst);
    }
    
    public final int getHunger() {
        return this.hunger;
    }
    
    public final float getNutritionlevel() {
        return this.nutrition;
    }
    
    public final float getCalories() {
        return this.calories;
    }
    
    public final float getCarbs() {
        return this.carbs;
    }
    
    public final float getFats() {
        return this.fats;
    }
    
    public final float getProteins() {
        return this.proteins;
    }
    
    public final int getThirst() {
        return this.thirst;
    }
    
    public final int getStamina() {
        return this.stamina;
    }
    
    final void setTrade(@Nullable final Trade _trade) {
        this.trade = _trade;
    }
    
    public final Trade getTrade() {
        return this.trade;
    }
    
    public final boolean isTrading() {
        return this.trade != null;
    }
    
    public final void sendStateString() {
        if (this.statusHolder.isPlayer()) {
            final List<String> strings = new LinkedList<String>();
            if (this.stunned > 0.0f) {
                strings.add("Stunned");
            }
            final String agg = this.getAggressiveness();
            if (agg.length() > 0) {
                strings.add(agg);
            }
            if (!this.visible && this.statusHolder.getPower() > 0) {
                strings.add("Invisible");
            }
            if (this.disease > 0) {
                strings.add("Diseased");
            }
            if (this.statusHolder.opponent != null) {
                strings.add("Opponent:" + this.statusHolder.opponent.getName());
            }
            if (this.statusHolder.getTarget() != null) {
                strings.add("Target:" + this.statusHolder.getTarget().getName());
            }
            if (this.detectInvisCounter > 0) {
                strings.add("Alerted");
            }
            if (this.stealth) {
                strings.add("Stealthmode");
            }
            if (this.statusHolder.getCRCounterBonus() > 0) {
                strings.add("Sharp");
            }
            if (this.statusHolder.isDead()) {
                strings.add("Dead");
            }
            if (this.statusHolder.damageCounter > 0) {
                strings.add("Hurting");
            }
            if (this.statusHolder.getFarwalkerSeconds() > 0) {
                strings.add("Unstoppable");
            }
            if (this.statusHolder.linkedTo != -10L) {
                try {
                    final Creature c = Server.getInstance().getCreature(this.statusHolder.linkedTo);
                    strings.add("Link: " + c.getName());
                }
                catch (NoSuchCreatureException ex) {}
                catch (NoSuchPlayerException ex2) {}
            }
            final StringBuilder stbuf = new StringBuilder();
            final ListIterator<String> it = strings.listIterator();
            while (it.hasNext()) {
                final String next = it.next();
                it.remove();
                stbuf.append(next);
                if (strings.size() > 0) {
                    stbuf.append(", ");
                }
            }
            this.statusHolder.getCommunicator().sendStatus(stbuf.toString());
        }
    }
    
    public final boolean canEat() {
        return this.hunger >= 10000;
    }
    
    public final boolean isHungry() {
        return this.hunger >= 45000;
    }
    
    private String getAggressiveness() {
        final byte fightStyle = this.statusHolder.getFightStyle();
        if (fightStyle == 1) {
            return "Aggressive";
        }
        if (fightStyle == 2) {
            return "Defensive";
        }
        return "";
    }
    
    final boolean pollAge(final int maxAge) {
        boolean rebornPoll = false;
        if (this.reborn && this.mother == -10L && WurmCalendar.currentTime - this.lastPolledAge > 604800L) {
            rebornPoll = true;
        }
        final boolean fasterGrowth = this.statusHolder.getTemplate().getTemplateId() == 65 || this.statusHolder.getTemplate().getTemplateId() == 117 || this.statusHolder.getTemplate().getTemplateId() == 118;
        if (WurmCalendar.currentTime - this.lastPolledAge > ((Servers.localServer.PVPSERVER && this.age < 8 && fasterGrowth) ? 259200L : 2419200L) || (this.isTraitBitSet(29) && WurmCalendar.currentTime - this.lastPolledAge > 345600L) || rebornPoll) {
            if ((this.statusHolder.isGhost() || this.statusHolder.isKingdomGuard() || this.statusHolder.isUnique()) && (!this.reborn || this.mother == -10L)) {
                this.age = Math.max(this.age, 11);
            }
            int newAge = this.age + 1;
            boolean updated = false;
            if (!this.statusHolder.isCaredFor() && !this.isTraitBitSet(29) && (newAge >= maxAge || (this.isTraitBitSet(13) && newAge >= Math.max(1, maxAge - Server.rand.nextInt(maxAge / 2))))) {
                return true;
            }
            if (!rebornPoll) {
                if (newAge > (this.isTraitBitSet(21) ? 75 : (this.isTraitBitSet(29) ? 36 : 50)) && !this.statusHolder.isGhost() && !this.statusHolder.isHuman() && !this.statusHolder.isUnique() && !this.statusHolder.isCaredFor()) {
                    return true;
                }
                if (newAge - 1 >= 5 && !this.reborn && (this.getTemplate().getAdultMaleTemplateId() > -1 || this.getTemplate().getAdultFemaleTemplateId() > -1)) {
                    int newtemplateId = this.getTemplate().getAdultMaleTemplateId();
                    if (this.sex == 1 && this.getTemplate().getAdultFemaleTemplateId() > -1) {
                        newtemplateId = this.getTemplate().getAdultFemaleTemplateId();
                    }
                    if (newtemplateId != this.getTemplate().getTemplateId()) {
                        newAge = 1;
                        try {
                            this.updateAge(newAge);
                            updated = true;
                        }
                        catch (IOException iox) {
                            CreatureStatus.logger.log(Level.WARNING, iox.getMessage(), iox);
                        }
                        try {
                            this.setChanged(true);
                            final CreatureTemplate newTemplate = CreatureTemplateFactory.getInstance().getTemplate(newtemplateId);
                            this.template = newTemplate;
                            this.statusHolder.template = this.template;
                            Label_0734: {
                                if (!this.statusHolder.isNpc()) {
                                    Label_0657: {
                                        if (this.statusHolder.getMother() != -10L) {
                                            if (this.statusHolder.isHorse() || this.statusHolder.isUnicorn() || this.reborn) {
                                                break Label_0657;
                                            }
                                        }
                                        try {
                                            if (this.statusHolder.getName().endsWith("traitor")) {
                                                this.statusHolder.setName(this.template.getName() + " traitor");
                                            }
                                            else {
                                                this.statusHolder.setName(this.template.getName());
                                            }
                                        }
                                        catch (Exception ex) {
                                            CreatureStatus.logger.log(Level.WARNING, ex.getMessage(), ex);
                                        }
                                    }
                                    if (this.reborn) {
                                        if (this.mother != -10L) {
                                            break Label_0734;
                                        }
                                    }
                                    try {
                                        this.statusHolder.skills.delete();
                                        this.statusHolder.skills.clone(newTemplate.getSkills().getSkills());
                                        this.statusHolder.skills.save();
                                    }
                                    catch (Exception ex) {
                                        CreatureStatus.logger.log(Level.WARNING, ex.getMessage(), ex);
                                    }
                                }
                            }
                            this.save();
                            this.statusHolder.refreshVisible();
                        }
                        catch (NoSuchCreatureTemplateException nsc) {
                            CreatureStatus.logger.log(Level.WARNING, this.statusHolder.getName() + ", " + this.statusHolder.getWurmId() + ": " + nsc.getMessage(), nsc);
                        }
                        catch (IOException iox) {
                            CreatureStatus.logger.log(Level.WARNING, this.statusHolder.getName() + ", " + this.statusHolder.getWurmId() + ": " + iox.getMessage(), iox);
                        }
                    }
                }
            }
            if (!updated) {
                try {
                    this.updateAge(newAge);
                    if (this.statusHolder.getHitched() != null && !this.statusHolder.getHitched().isAnySeatOccupied(false) && !this.statusHolder.isDomestic() && this.getBattleRatingTypeModifier() > 1.2f) {
                        Server.getInstance().broadCastMessage(this.statusHolder.getName() + " stops dragging a " + Vehicle.getVehicleName(this.statusHolder.getHitched()) + ".", this.statusHolder.getTileX(), this.statusHolder.getTileY(), this.statusHolder.isOnSurface(), 5);
                        if (this.statusHolder.getHitched().removeDragger(this.statusHolder)) {
                            this.statusHolder.setHitched(null, false);
                        }
                    }
                }
                catch (IOException iox2) {
                    CreatureStatus.logger.log(Level.WARNING, iox2.getMessage(), iox2);
                }
            }
        }
        return false;
    }
    
    public final boolean isChampion() {
        return this.modtype == 99;
    }
    
    public final String getAgeString() {
        if (this.age < 3) {
            return "young";
        }
        if (this.age < 8) {
            return "adolescent";
        }
        if (this.age < 12) {
            return "mature";
        }
        if (this.age < 30) {
            return "aged";
        }
        if (this.age < 40) {
            return "old";
        }
        return "venerable";
    }
    
    final boolean hasCustomColor() {
        if (this.modtype <= 0) {
            return false;
        }
        switch (this.modtype) {
            case 1: {
                return true;
            }
            case 2: {
                return true;
            }
            case 3: {
                return true;
            }
            case 4: {
                return true;
            }
            case 5: {
                return true;
            }
            case 6: {
                return true;
            }
            case 7: {
                return true;
            }
            case 8: {
                return true;
            }
            case 9: {
                return true;
            }
            case 10: {
                return true;
            }
            case 11: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    final byte getColorRed() {
        return -1;
    }
    
    final byte getColorGreen() {
        return -1;
    }
    
    final byte getColorBlue() {
        return -1;
    }
    
    public abstract void setVehicle(final long p0, final byte p1);
    
    final float getSizeMod() {
        float aiDataModifier = 1.0f;
        if (this.statusHolder.getCreatureAIData() != null) {
            aiDataModifier = this.statusHolder.getCreatureAIData().getSizeModifier();
        }
        float floatToRet = 1.0f;
        if (!this.statusHolder.isVehicle() && this.modtype != 0) {
            switch (this.modtype) {
                case 3: {
                    floatToRet = 1.4f;
                    break;
                }
                case 4: {
                    floatToRet = 2.0f;
                    break;
                }
                case 6: {
                    floatToRet = 2.0f;
                    break;
                }
                case 7: {
                    floatToRet = 0.8f;
                    break;
                }
                case 8: {
                    floatToRet = 0.9f;
                    break;
                }
                case 9: {
                    floatToRet = 1.5f;
                    break;
                }
                case 10: {
                    floatToRet = 1.3f;
                    break;
                }
                case 99: {
                    floatToRet = 3.0f;
                    break;
                }
                case -1: {
                    floatToRet = 0.5f;
                    break;
                }
                case -2: {
                    floatToRet = 0.25f;
                    break;
                }
                case -3: {
                    floatToRet = 0.125f;
                    break;
                }
            }
        }
        if (this.statusHolder.getHitched() == null && this.statusHolder.getTemplate().getTemplateId() == 82 && !this.statusHolder.getNameWithoutPrefixes().equalsIgnoreCase(this.statusHolder.getTemplate().getName())) {
            floatToRet = 2.0f;
        }
        if (!this.statusHolder.isVehicle() && this.statusHolder.hasTrait(28)) {
            floatToRet *= 1.5f;
        }
        return floatToRet * this.getAgeSizeModifier() * aiDataModifier;
    }
    
    private float getAgeSizeModifier() {
        if (this.statusHolder.isHuman() || this.statusHolder.isGhost() || this.template.getAdultFemaleTemplateId() > -1 || this.template.getAdultMaleTemplateId() > -1) {
            return 1.0f;
        }
        if (this.age < 3) {
            return 0.7f;
        }
        if (this.age < 8) {
            return 0.8f;
        }
        if (this.age < 12) {
            return 0.9f;
        }
        if (this.age < 40) {
            return 1.0f;
        }
        return 0.95f;
    }
    
    final String getTypeString() {
        if (this.modtype <= 0) {
            return "";
        }
        switch (this.modtype) {
            case 1: {
                return "fierce ";
            }
            case 2: {
                return "angry ";
            }
            case 3: {
                return "raging ";
            }
            case 4: {
                return "slow ";
            }
            case 5: {
                return "alert ";
            }
            case 6: {
                return "greenish ";
            }
            case 7: {
                return "lurking ";
            }
            case 8: {
                return "sly ";
            }
            case 9: {
                return "hardened ";
            }
            case 10: {
                return "scared ";
            }
            case 11: {
                return "diseased ";
            }
            case 99: {
                return "champion ";
            }
            default: {
                return "";
            }
        }
    }
    
    public static final byte getModtypeForString(final String corpseString) {
        if (corpseString.contains(" fierce ")) {
            return 1;
        }
        if (corpseString.contains(" angry ")) {
            return 2;
        }
        if (corpseString.contains(" raging ")) {
            return 3;
        }
        if (corpseString.contains(" slow ")) {
            return 4;
        }
        if (corpseString.contains(" alert ")) {
            return 5;
        }
        if (corpseString.contains(" greenish ")) {
            return 6;
        }
        if (corpseString.contains(" lurking ")) {
            return 7;
        }
        if (corpseString.contains(" sly ")) {
            return 8;
        }
        if (corpseString.contains(" hardened ")) {
            return 9;
        }
        if (corpseString.contains(" scared ")) {
            return 10;
        }
        if (corpseString.contains(" diseased ")) {
            return 11;
        }
        if (corpseString.contains(" champion ")) {
            return 99;
        }
        return 0;
    }
    
    final boolean setStealth(final boolean st) {
        if (this.stealth != st) {
            this.stealth = st;
            this.sendStateString();
            this.statusHolder.getCommunicator().sendToggle(3, this.stealth);
            return true;
        }
        return false;
    }
    
    final boolean modifyLoyalty(final float mod) {
        this.setLoyalty(this.loyalty + mod);
        return this.loyalty <= 0.0f;
    }
    
    final boolean pollLoyalty() {
        if (this.loyalty > 0.0f) {
            long timeBetweenPolls = 86400000L;
            if (this.statusHolder.isAggHuman() && (this.statusHolder.getBaseCombatRating() > 20.0f || this.statusHolder.getSoulStrengthVal() > 40.0)) {
                timeBetweenPolls = 3600000L;
            }
            if (System.currentTimeMillis() - this.lastPolledLoyalty > timeBetweenPolls) {
                this.setLastPolledLoyalty();
                if (!this.statusHolder.isReborn() || this.statusHolder.getMother() != -10L) {
                    int sstrngth = (int)this.statusHolder.getSoulStrengthVal();
                    sstrngth += (this.isTraitBitSet(11) ? 20 : 0);
                    if (Server.rand.nextInt(50) < sstrngth) {
                        if (CreatureStatus.logger.isLoggable(Level.FINEST)) {
                            CreatureStatus.logger.finest(this.statusHolder.getName() + " decreasing loyalty (" + this.loyalty + ") by " + Math.min(-5, sstrngth / -5));
                        }
                        if (this.modifyLoyalty(Math.min(-5, sstrngth / -5))) {
                            if (CreatureStatus.logger.isLoggable(Level.FINER)) {
                                CreatureStatus.logger.finer(this.statusHolder.getName() + " loyalty became " + this.loyalty + ". Turned!");
                            }
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }
    
    public final void pollDetectInvis() {
        if (this.detectInvisCounter > 0) {
            --this.detectInvisCounter;
            if (this.detectInvisCounter == 0 || this.detectInvisCounter % 60 == 0) {
                this.setDetectionSecs();
            }
            if (this.detectInvisCounter == 0) {
                this.statusHolder.getCommunicator().sendRemoveSpellEffect(SpellEffectsEnum.DETECT_INVIS);
            }
        }
    }
    
    public final void setDetectInvisCounter(final int detectInvisSecs) {
        this.detectInvisCounter = detectInvisSecs;
        if (this.statusHolder.isPlayer()) {
            if (this.detectInvisCounter > 0) {
                this.statusHolder.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.DETECT_INVIS, this.detectInvisCounter, 100.0f);
            }
            else {
                this.statusHolder.getCommunicator().sendRemoveSpellEffect(SpellEffectsEnum.DETECT_INVIS);
            }
        }
    }
    
    public final int getDetectInvisCounter() {
        return this.detectInvisCounter;
    }
    
    public final float getBattleRatingTypeModifier() {
        float floatToRet = 1.0f;
        switch (this.modtype) {
            case 1: {
                floatToRet = 1.6f;
                break;
            }
            case 3: {
                floatToRet = 2.0f;
                break;
            }
            case 2:
            case 9: {
                floatToRet = 1.3f;
                break;
            }
            case 4:
            case 11: {
                floatToRet = 0.5f;
                break;
            }
            case 6: {
                floatToRet = 3.0f;
                break;
            }
            case 99: {
                floatToRet = 6.0f;
                break;
            }
            default: {
                return floatToRet * this.getAgeBRModifier();
            }
        }
        return floatToRet * this.getAgeBRModifier();
    }
    
    final float getAgeBRModifier() {
        if (this.age < 3) {
            return 0.9f;
        }
        if (this.age < 8) {
            return 1.0f;
        }
        if (this.age < 12) {
            return 1.1f;
        }
        if (this.age < 30) {
            return 1.2f;
        }
        if (this.age < 40) {
            return 1.3f;
        }
        return 1.4f;
    }
    
    final float getMovementTypeModifier() {
        float floatToRet = 1.0f;
        switch (this.modtype) {
            case 11: {
                floatToRet = 0.5f;
                break;
            }
            case 3: {
                floatToRet = 1.2f;
                break;
            }
            case 2:
            case 10: {
                floatToRet = 1.1f;
                break;
            }
            case 4: {
                floatToRet = 0.7f;
                break;
            }
            case 5: {
                floatToRet = 1.4f;
                break;
            }
            case 99: {
                floatToRet = 1.4f;
                break;
            }
            default: {
                return Math.min(1.6f, floatToRet * this.getAgeMoveModifier());
            }
        }
        return floatToRet * this.getAgeMoveModifier();
    }
    
    private float getAgeMoveModifier() {
        if (this.age < 3) {
            return 0.9f;
        }
        if (this.age < 8) {
            return 1.0f;
        }
        if (this.age < 12) {
            return 1.1f;
        }
        if (this.age < 30) {
            return 1.3f;
        }
        if (this.age < 40) {
            return 1.0f;
        }
        return 0.8f;
    }
    
    final float getDamageTypeModifier() {
        float floatToRet = 1.0f;
        switch (this.modtype) {
            case 6: {
                floatToRet = 2.0f;
                break;
            }
            case 1: {
                floatToRet = 1.1f;
                break;
            }
            case 3: {
                floatToRet = 1.3f;
                break;
            }
            case 2:
            case 9: {
                floatToRet = 1.2f;
                break;
            }
            case 10:
            case 11: {
                floatToRet = 0.7f;
                break;
            }
            case 8: {
                floatToRet = 0.5f;
                break;
            }
            case 99: {
                floatToRet = 2.0f;
                break;
            }
            default: {
                return floatToRet * this.getAgeDamageModifier();
            }
        }
        return floatToRet * this.getAgeDamageModifier();
    }
    
    private float getAgeDamageModifier() {
        if (this.age < 3) {
            return 0.8f;
        }
        if (this.age < 8) {
            return 0.9f;
        }
        if (this.age < 12) {
            return 1.0f;
        }
        if (this.age < 30) {
            return 1.1f;
        }
        if (this.age < 40) {
            return 1.2f;
        }
        return 1.5f;
    }
    
    final float getParryTypeModifier() {
        float floatToRet = 1.0f;
        switch (this.modtype) {
            case 1: {
                floatToRet = 1.2f;
                break;
            }
            case 3:
            case 7: {
                floatToRet = 1.5f;
                break;
            }
            case 2: {
                floatToRet = 1.1f;
                break;
            }
            case 10: {
                floatToRet = 0.3f;
                break;
            }
            case 8: {
                floatToRet = 0.1f;
                break;
            }
            case 6:
            case 9: {
                floatToRet = 0.7f;
                break;
            }
            case 99: {
                floatToRet = 1.2f;
                break;
            }
            default: {
                return floatToRet * this.getAgeParryModifier();
            }
        }
        return floatToRet * this.getAgeParryModifier();
    }
    
    private float getAgeParryModifier() {
        if (this.age < 3) {
            return 1.2f;
        }
        if (this.age < 8) {
            return 1.1f;
        }
        if (this.age < 12) {
            return 0.8f;
        }
        if (this.age < 30) {
            return 1.0f;
        }
        if (this.age < 40) {
            return 1.2f;
        }
        return 1.3f;
    }
    
    final float getDodgeTypeModifier() {
        float floatToRet = 1.0f;
        switch (this.modtype) {
            case 1: {
                floatToRet = 0.94f;
                break;
            }
            case 2: {
                floatToRet = 0.97f;
                break;
            }
            case 10: {
                floatToRet = 0.9f;
                break;
            }
            case 6:
            case 9: {
                floatToRet = 0.95f;
                break;
            }
            case 7: {
                floatToRet = 1.5f;
                break;
            }
            case 5:
            case 8: {
                floatToRet = 0.85f;
                break;
            }
            case 4:
            case 11: {
                floatToRet = 1.7f;
                break;
            }
            case 99: {
                floatToRet = 0.9f;
                break;
            }
            default: {
                return floatToRet * this.getAgeDodgeModifier();
            }
        }
        return floatToRet * this.getAgeDodgeModifier();
    }
    
    private float getAgeDodgeModifier() {
        if (this.age < 3) {
            return 1.0f;
        }
        if (this.age < 8) {
            return 1.1f;
        }
        if (this.age < 12) {
            return 1.2f;
        }
        if (this.age < 30) {
            return 1.1f;
        }
        if (this.age < 40) {
            return 1.4f;
        }
        return 1.5f;
    }
    
    public final float getAggTypeModifier() {
        float floatToRet = 1.0f;
        switch (this.modtype) {
            case 1:
            case 6: {
                floatToRet = 1.3f;
                break;
            }
            case 3: {
                floatToRet = 1.5f;
                break;
            }
            case 2:
            case 7: {
                floatToRet = 1.1f;
                break;
            }
            case 10:
            case 11: {
                floatToRet = 0.5f;
                break;
            }
            default: {
                return floatToRet * this.getAgeAggModifier();
            }
        }
        return floatToRet * this.getAgeAggModifier();
    }
    
    private float getAgeAggModifier() {
        if (this.age < 3) {
            return 0.8f;
        }
        if (this.age < 8) {
            return 1.3f;
        }
        if (this.age < 12) {
            return 1.2f;
        }
        if (this.age < 30) {
            return 1.0f;
        }
        if (this.age < 40) {
            return 0.8f;
        }
        return 0.6f;
    }
    
    public final int getLayer() {
        return this.getPosition().getLayer();
    }
    
    public final int getZoneId() {
        return this.getPosition().getZoneId();
    }
    
    abstract void setLoyalty(final float p0);
    
    public abstract void load() throws Exception;
    
    public abstract void savePosition(final long p0, final boolean p1, final int p2, final boolean p3) throws IOException;
    
    public abstract boolean save() throws IOException;
    
    public abstract void setKingdom(final byte p0) throws IOException;
    
    public abstract void setDead(final boolean p0) throws IOException;
    
    abstract void updateAge(final int p0) throws IOException;
    
    abstract void setDominator(final long p0);
    
    public abstract void setReborn(final boolean p0);
    
    public abstract void setLastPolledLoyalty();
    
    abstract void setOffline(final boolean p0);
    
    abstract boolean setStayOnline(final boolean p0);
    
    abstract void setDetectionSecs();
    
    abstract void setType(final byte p0);
    
    abstract void updateFat() throws IOException;
    
    abstract void setInheritance(final long p0, final long p1, final long p2) throws IOException;
    
    public abstract void setInventoryId(final long p0) throws IOException;
    
    abstract void saveCreatureName(final String p0) throws IOException;
    
    abstract void setLastGroomed(final long p0);
    
    abstract void setDisease(final byte p0);
    
    public boolean isStatusExists() {
        return this.statusExists;
    }
    
    public void setStatusExists(final boolean aStatusExists) {
        this.statusExists = aStatusExists;
    }
    
    public CreaturePos getPosition() {
        return this.position;
    }
    
    public void setPosition(final CreaturePos aPosition) {
        this.position = aPosition;
        if (aPosition != null && aPosition.getRotation() > 1000.0f) {
            aPosition.setRotation(aPosition.getRotation() % 360.0f);
        }
    }
    
    public boolean isChanged() {
        return this.changed;
    }
    
    public void setChanged(final boolean aChanged) {
        this.changed = aChanged;
    }
    
    public byte getModType() {
        return this.modtype;
    }
    
    static {
        CreatureStatus.logger = Logger.getLogger(CreatureStatus.class.getName());
        CreatureStatus.DAILY_CALORIES = 2000;
        CreatureStatus.DAILY_CARBS = 300;
        CreatureStatus.DAILY_FATS = 80;
        CreatureStatus.DAILY_PROTEINS = 50;
        CreatureStatus.CCFP_MAX_PERCENTAGE = 1.0f;
        CreatureStatus.CCFP_REDUCE_AMOUNT = 3.3333334E-5f;
    }
}
