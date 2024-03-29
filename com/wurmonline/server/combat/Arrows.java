// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.combat;

import java.util.HashSet;
import com.wurmonline.server.items.NoSpaceException;
import com.wurmonline.server.creatures.NoArmourException;
import com.wurmonline.server.Servers;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.MessageServer;
import javax.annotation.Nullable;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.Server;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.zones.NoSuchZoneException;
import java.util.logging.Level;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.utils.CreatureLineSegment;
import java.util.List;
import com.wurmonline.shared.util.MulticolorLineSegment;
import java.util.ArrayList;
import com.wurmonline.server.Items;
import java.util.Iterator;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.math.Vector;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import java.util.Set;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.shared.constants.SoundNames;
import com.wurmonline.shared.constants.ItemMaterials;
import com.wurmonline.server.MiscConstants;

public class Arrows implements MiscConstants, ItemMaterials, SoundNames, TimeConstants
{
    private static Logger logger;
    private static final Set<Arrows> arrows;
    private final Item arrow;
    private final Creature performer;
    private Creature defender;
    private boolean hitTarget;
    private final int tileArrowDownX;
    private final int tileArrowDownY;
    private final ArrowHitting arrowHitting;
    private final ArrowDestroy arrowDestroy;
    private final Item bow;
    private double damage;
    private float damMod;
    private float armourMod;
    private byte pos;
    private final float speed;
    private final float totalTime;
    private float time;
    private final boolean hittingCreature;
    private Item item;
    private String scoreString;
    private boolean dryRun;
    private double difficulty;
    private double bonus;
    
    public Arrows(final Item aArrow, final Creature aDefender, final ArrowHitting aArrowHitting, final ArrowDestroy aArrowDestroy, final Item aBow, final double aDamage, final byte aPos, final byte proj) {
        this.hittingCreature = true;
        this.arrow = aArrow;
        this.defender = aDefender;
        this.arrowHitting = aArrowHitting;
        this.arrowDestroy = aArrowDestroy;
        this.bow = aBow;
        this.damage = aDamage;
        this.damMod = 0.0f;
        this.armourMod = 0.0f;
        this.tileArrowDownX = -1;
        this.tileArrowDownY = -1;
        this.performer = null;
        this.pos = aPos;
        this.dryRun = true;
        this.difficulty = 0.0;
        this.bonus = 0.0;
        this.time = 0.0f;
        aArrow.setPosXYZ(aBow.getPosX(), aBow.getPosY(), aBow.getPosZ() + 2.0f);
        if (aArrowHitting == ArrowHitting.HIT || aArrowHitting == ArrowHitting.HIT_NO_DAMAGE || aArrowHitting == ArrowHitting.SHIELD || aArrowHitting == ArrowHitting.EVASION) {
            this.hitTarget = true;
        }
        else {
            this.hitTarget = false;
        }
        float length = 1.0f;
        if (this.hitTarget) {
            final float x = aDefender.getPosX() - aBow.getPosX();
            final float y = aDefender.getPosY() - aBow.getPosY();
            final float h = aDefender.getPositionZ() + aDefender.getAltOffZ() - (aBow.getPosZ() + 2.0f);
            final Vector vector = new Vector(x, y, h);
            length = vector.length();
        }
        this.speed = ((this.arrow.getMaterial() == 21) ? 13.0f : 45.0f);
        this.totalTime = length / this.speed * 1000.0f;
        if (this.hitTarget) {
            final double newrot = Math.atan2(aDefender.getPosY() - (int)aArrow.getPosY(), aDefender.getPosX() - (int)aArrow.getPosX());
            float attAngle = (float)(newrot * 57.29577951308232) - 90.0f;
            attAngle = Creature.normalizeAngle(attAngle);
            VolaTile tile = Zones.getOrCreateTile(aBow.getTileX(), aBow.getTileY(), aBow.isOnSurface());
            if (tile != null) {
                tile.sendProjectile(aArrow.getWurmId(), proj, aArrow.getModelName(), aArrow.getName(), aArrow.getMaterial(), aBow.getPosX(), aBow.getPosY(), aBow.getPosZ() + 2.0f, attAngle, (byte)(aBow.isOnSurface() ? 0 : -1), (int)aDefender.getPosX(), (int)aDefender.getPosY(), aDefender.getPositionZ() + aDefender.getAltOffZ(), aBow.getWurmId(), aDefender.getWurmId(), this.totalTime, this.totalTime);
            }
            tile = aDefender.getCurrentTile();
            if (tile != null) {
                tile.sendProjectile(aArrow.getWurmId(), proj, aArrow.getModelName(), aArrow.getName(), aArrow.getMaterial(), aBow.getPosX(), aBow.getPosY(), aBow.getPosZ() + 2.0f, attAngle, (byte)(aBow.isOnSurface() ? 0 : -1), (int)aDefender.getPosX(), (int)aDefender.getPosY(), aDefender.getPositionZ() + aDefender.getAltOffZ(), aBow.getWurmId(), aDefender.getWurmId(), this.totalTime, this.totalTime);
            }
        }
    }
    
    public Arrows(final Item aArrow, final Creature aPerformer, final Creature aDefender, final int aTileArrowDownX, final int aTileArrowDownY, final ArrowHitting aArrowHitting, final ArrowDestroy aArrowDestroy, final Item aBow1, final double aDamage, final float aDamMod, final float aArmourMod, final byte aPos, final boolean dry, final double diff, final double bon) {
        this.hittingCreature = true;
        this.arrow = aArrow;
        this.performer = aPerformer;
        this.defender = aDefender;
        this.tileArrowDownX = aTileArrowDownX;
        this.tileArrowDownY = aTileArrowDownY;
        this.arrowHitting = aArrowHitting;
        this.arrowDestroy = aArrowDestroy;
        this.bow = aBow1;
        this.damage = aDamage;
        this.damMod = aDamMod;
        this.armourMod = aArmourMod;
        this.pos = aPos;
        this.dryRun = dry;
        this.difficulty = diff;
        this.bonus = bon;
        this.time = 0.0f;
        aArrow.setPosXYZ(aPerformer.getPosX(), aPerformer.getPosY(), aPerformer.getPositionZ());
        if (aArrowHitting == ArrowHitting.HIT || aArrowHitting == ArrowHitting.HIT_NO_DAMAGE || aArrowHitting == ArrowHitting.SHIELD || aArrowHitting == ArrowHitting.EVASION) {
            this.hitTarget = true;
        }
        else {
            this.hitTarget = false;
        }
        float length = 1.0f;
        if (this.hitTarget) {
            final float x = aDefender.getPosX() - aPerformer.getPosX();
            final float y = aDefender.getPosY() - aPerformer.getPosY();
            final float h = aDefender.getPositionZ() + aDefender.getAltOffZ() - aPerformer.getPositionZ() + aPerformer.getAltOffZ();
            final Vector vector = new Vector(x, y, h);
            length = vector.length();
        }
        else {
            final float x = (aTileArrowDownX << 2) + 2 - aPerformer.getPosX();
            final float y = (aTileArrowDownY << 2) + 2 - aPerformer.getPosY();
            final float h = aDefender.getPositionZ() + aDefender.getAltOffZ() - aPerformer.getPositionZ() + aPerformer.getAltOffZ();
            final Vector vector = new Vector(x, y, h);
            length = vector.length();
        }
        this.speed = 45.0f;
        this.totalTime = length / this.speed * 1000.0f;
        if (this.hitTarget) {
            VolaTile tile = aPerformer.getCurrentTile();
            if (tile != null) {
                tile.sendProjectile(aArrow.getWurmId(), (byte)1, aArrow.getModelName(), aArrow.getName(), aArrow.getMaterial(), aPerformer.getPosX(), aPerformer.getPosY(), aPerformer.getPositionZ() + aPerformer.getAltOffZ(), aPerformer.getStatus().getRotation(), (byte)aPerformer.getLayer(), (int)aDefender.getPosX(), (int)aDefender.getPosY(), aDefender.getPositionZ() + aDefender.getAltOffZ(), aPerformer.getWurmId(), aDefender.getWurmId(), this.totalTime, this.totalTime);
            }
            tile = aDefender.getCurrentTile();
            if (tile != null) {
                tile.sendProjectile(aArrow.getWurmId(), (byte)1, aArrow.getModelName(), aArrow.getName(), aArrow.getMaterial(), aPerformer.getPosX(), aPerformer.getPosY(), aPerformer.getPositionZ() + aPerformer.getAltOffZ(), aPerformer.getStatus().getRotation(), (byte)aPerformer.getLayer(), (int)aDefender.getPosX(), (int)aDefender.getPosY(), aDefender.getPositionZ() + aDefender.getAltOffZ(), aPerformer.getWurmId(), aDefender.getWurmId(), this.totalTime, this.totalTime);
            }
        }
        else {
            VolaTile tile = aPerformer.getCurrentTile();
            if (tile != null) {
                tile.sendProjectile(aArrow.getWurmId(), (byte)1, aArrow.getModelName(), aArrow.getName(), aArrow.getMaterial(), aPerformer.getPosX(), aPerformer.getPosY(), aPerformer.getPositionZ() + aPerformer.getAltOffZ(), aPerformer.getStatus().getRotation(), (byte)aPerformer.getLayer(), (aTileArrowDownX << 2) + 2, (aTileArrowDownY << 2) + 2, aDefender.getPositionZ() + aDefender.getAltOffZ(), aPerformer.getWurmId(), -2L, this.totalTime, this.totalTime);
            }
            tile = aDefender.getCurrentTile();
            if (tile != null) {
                tile.sendProjectile(aArrow.getWurmId(), (byte)1, aArrow.getModelName(), aArrow.getName(), aArrow.getMaterial(), aPerformer.getPosX(), aPerformer.getPosY(), aPerformer.getPositionZ() + aPerformer.getAltOffZ(), aPerformer.getStatus().getRotation(), (byte)aPerformer.getLayer(), (aTileArrowDownX << 2) + 2, (aTileArrowDownY << 2) + 2, aDefender.getPositionZ() + aDefender.getAltOffZ(), aPerformer.getWurmId(), -2L, this.totalTime, this.totalTime);
            }
        }
    }
    
    public Arrows(final Item aArrow, final Creature aPerformer, final Item aItem, final int aTileArrowDownX, final int aTileArrowDownY, final Item aBow, final ArrowHitting aArrowHitting, final ArrowDestroy aArrowDestroy, final String aScoreString) {
        this.hittingCreature = false;
        this.arrow = aArrow;
        this.performer = aPerformer;
        this.item = aItem;
        this.tileArrowDownX = aTileArrowDownX;
        this.tileArrowDownY = aTileArrowDownY;
        this.arrowHitting = aArrowHitting;
        this.arrowDestroy = aArrowDestroy;
        this.bow = aBow;
        this.scoreString = aScoreString;
        this.time = 0.0f;
        aArrow.setPosXYZ(aPerformer.getPosX(), aPerformer.getPosY(), aPerformer.getPositionZ() + aPerformer.getAltOffZ());
        if (aArrowHitting == ArrowHitting.HIT || aArrowHitting == ArrowHitting.HIT_NO_DAMAGE || aArrowHitting == ArrowHitting.SHIELD || aArrowHitting == ArrowHitting.EVASION) {
            this.hitTarget = true;
        }
        else {
            this.hitTarget = false;
        }
        float length = 1.0f;
        if (this.hitTarget) {
            final float x = aItem.getPosX() - aPerformer.getPosX();
            final float y = aItem.getPosY() - aPerformer.getPosY();
            final float h = aItem.getPosZ() + 1.0f - aPerformer.getPositionZ() + aPerformer.getAltOffZ();
            final Vector vector = new Vector(x, y, h);
            length = vector.length();
        }
        else {
            final float x = (aTileArrowDownX << 2) + 2 - aPerformer.getPosX();
            final float y = (aTileArrowDownY << 2) + 2 - aPerformer.getPosY();
            final float h = aItem.getPosZ() - aPerformer.getPositionZ() + aPerformer.getAltOffZ();
            final Vector vector = new Vector(x, y, h);
            length = vector.length();
        }
        this.speed = 45.0f;
        this.totalTime = length / this.speed * 1000.0f;
        if (this.hitTarget) {
            final VolaTile tile = aPerformer.getCurrentTile();
            if (tile != null) {
                tile.sendProjectile(aArrow.getWurmId(), (byte)1, aArrow.getModelName(), aArrow.getName(), aArrow.getMaterial(), aPerformer.getPosX(), aPerformer.getPosY(), aPerformer.getPositionZ() + aPerformer.getAltOffZ(), aPerformer.getStatus().getRotation(), (byte)aPerformer.getLayer(), (int)aItem.getPosX(), (int)aItem.getPosY(), aItem.getPosZ(), aPerformer.getWurmId(), aItem.getWurmId(), this.totalTime, this.totalTime);
            }
        }
        else {
            final VolaTile tile = aPerformer.getCurrentTile();
            if (tile != null) {
                tile.sendProjectile(aArrow.getWurmId(), (byte)1, aArrow.getModelName(), aArrow.getName(), aArrow.getMaterial(), aPerformer.getPosX(), aPerformer.getPosY(), aPerformer.getPositionZ() + aPerformer.getAltOffZ(), aPerformer.getStatus().getRotation(), (byte)aPerformer.getLayer(), (aTileArrowDownX << 2) + 2, (aTileArrowDownY << 2) + 2, aItem.getPosZ(), aPerformer.getWurmId(), -2L, this.totalTime, this.totalTime);
            }
        }
    }
    
    public static void pollAll(final float elapsedTime) {
        final Iterator<Arrows> i = Arrows.arrows.iterator();
        while (i.hasNext()) {
            final Arrows arrow = i.next();
            if (arrow.hittingCreature) {
                if (arrow.pollHitCreature(elapsedTime)) {
                    continue;
                }
                i.remove();
            }
            else {
                if (arrow.pollHitItem(elapsedTime)) {
                    continue;
                }
                i.remove();
            }
        }
    }
    
    public boolean pollHitCreature(final float elapsedTime) {
        this.time += elapsedTime;
        if (this.defender.isDead()) {
            return false;
        }
        if (this.time <= this.totalTime) {
            return true;
        }
        final boolean tooLate = this.arrow.getMaterial() == 21 && !this.defender.isWithinDistanceTo(this.bow.getPosX(), this.bow.getPosY(), this.bow.getPosZ(), 12.0f);
        if (tooLate) {
            Items.destroyItem(this.arrow.getWurmId());
            return false;
        }
        if (this.arrowHitting == ArrowHitting.HIT) {
            if (this.performer == null) {
                byte type = 2;
                if (this.bow.isEnchantedTurret()) {
                    switch (this.bow.getTemplateId()) {
                        case 940: {
                            type = 10;
                            break;
                        }
                        case 941: {
                            type = 4;
                            break;
                        }
                        case 968: {
                            type = 8;
                            break;
                        }
                        case 942: {
                            type = 4;
                            break;
                        }
                    }
                }
                final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                segments.add(new MulticolorLineSegment(this.arrow.getNameWithGenus() + " from the ", (byte)7));
                segments.add(new MulticolorLineSegment(this.bow.getName(), (byte)2));
                segments.add(new MulticolorLineSegment(" hits you in the " + this.defender.getBody().getWoundLocationString(this.pos) + ".", (byte)7));
                this.defender.getCommunicator().sendColoredMessageCombat(segments);
                this.defender.addWoundOfType(this.performer, type, this.pos, false, 1.0f, true, this.damage, 0.0f, 0.0f, false, false);
            }
            else {
                Archery.hit(this.defender, this.performer, this.arrow, this.bow, this.damage, this.damMod, this.armourMod, this.pos, true, this.dryRun, this.difficulty, this.bonus);
            }
        }
        else if (this.arrowHitting == ArrowHitting.HIT_NO_DAMAGE) {
            if (this.performer != null) {
                final ArrayList<MulticolorLineSegment> segments2 = new ArrayList<MulticolorLineSegment>();
                segments2.add(new MulticolorLineSegment("Your arrow glances off ", (byte)0));
                segments2.add(new CreatureLineSegment(this.defender));
                segments2.add(new MulticolorLineSegment(" and does no damage.", (byte)0));
                this.performer.getCommunicator().sendColoredMessageCombat(segments2);
            }
            this.defender.getCommunicator().sendCombatSafeMessage("An arrow hits you on the " + this.defender.getBody().getWoundLocationString(this.pos) + " but does no damage.");
        }
        else if (this.arrowHitting == ArrowHitting.SHIELD) {
            if (this.performer != null) {
                final ArrayList<MulticolorLineSegment> segments2 = new ArrayList<MulticolorLineSegment>();
                segments2.add(new MulticolorLineSegment("Your arrow glances off ", (byte)0));
                segments2.add(new CreatureLineSegment(this.defender));
                segments2.add(new MulticolorLineSegment("'s shield.", (byte)0));
                this.performer.getCommunicator().sendColoredMessageCombat(segments2);
            }
            this.defender.getCommunicator().sendCombatSafeMessage("You instinctively block an arrow with your shield.");
        }
        else if (this.arrowHitting == ArrowHitting.EVASION) {
            if (this.performer != null) {
                final ArrayList<MulticolorLineSegment> segments2 = new ArrayList<MulticolorLineSegment>();
                segments2.add(new MulticolorLineSegment("Your arrow glances off ", (byte)0));
                segments2.add(new CreatureLineSegment(this.defender));
                segments2.add(new MulticolorLineSegment("'s armour.", (byte)0));
                this.performer.getCommunicator().sendColoredMessageCombat(segments2);
            }
            this.defender.getCommunicator().sendCombatSafeMessage("An arrow hits you on the " + this.defender.getBody().getWoundLocationString(this.pos) + " but glances off your armour.");
        }
        else if (this.arrowHitting == ArrowHitting.TREE) {
            SoundPlayer.playSound("sound.arrow.stuck.wood", this.tileArrowDownX, this.tileArrowDownY, this.defender.isOnSurface(), 0.0f);
        }
        else if (this.arrowHitting == ArrowHitting.FENCE_STONE) {
            SoundPlayer.playSound("sound.work.masonry", this.tileArrowDownX, this.tileArrowDownY, this.defender.isOnSurface(), 0.0f);
        }
        else if (this.arrowHitting == ArrowHitting.FENCE_TREE) {
            SoundPlayer.playSound("sound.arrow.stuck.wood", this.tileArrowDownX, this.tileArrowDownY, this.defender.isOnSurface(), 0.0f);
        }
        else if (this.arrowHitting == ArrowHitting.GROUND) {
            SoundPlayer.playSound("sound.arrow.stuck.ground", this.tileArrowDownX, this.tileArrowDownY, this.defender.isOnSurface(), 0.0f);
        }
        if (this.arrowDestroy == ArrowDestroy.BREAKS) {
            Items.destroyItem(this.arrow.getWurmId());
            if (this.performer != null) {
                this.performer.getCommunicator().sendCombatNormalMessage("The arrow breaks.");
            }
        }
        else if (this.arrowDestroy == ArrowDestroy.NORMAL) {
            Items.destroyItem(this.arrow.getWurmId());
        }
        else if (this.arrowDestroy == ArrowDestroy.WATER) {
            Items.destroyItem(this.arrow.getWurmId());
            if (this.performer != null) {
                this.performer.getCommunicator().sendCombatNormalMessage("The arrow disappears from your view.");
            }
        }
        else if (this.arrowDestroy == ArrowDestroy.NOT && this.arrowHitting != ArrowHitting.HIT) {
            try {
                final Zone z = Zones.getZone(this.tileArrowDownX, this.tileArrowDownY, this.defender.isOnSurface());
                final VolaTile t = z.getOrCreateTile(this.tileArrowDownX, this.tileArrowDownY);
                t.addItem(this.arrow, false, false);
            }
            catch (NoSuchZoneException e) {
                Arrows.logger.log(Level.WARNING, e.getMessage());
            }
        }
        return false;
    }
    
    public boolean pollHitItem(final float elapsedTime) {
        this.time += elapsedTime;
        if (this.time > this.totalTime) {
            if (this.arrowHitting == ArrowHitting.HIT) {
                SoundPlayer.playSound("sound.arrow.hit.wood", this.item, 1.6f);
                this.item.setDamage(this.item.getDamage() + Math.max(Server.rand.nextFloat() * 0.4f, (float)(this.damage / 5000.0) * this.item.getDamageModifier()));
                if (this.item.getTemplateId() == 458) {
                    this.performer.getCommunicator().sendSafeServerMessage("You score " + this.scoreString);
                    Server.getInstance().broadCastAction(this.performer.getName() + " scores " + this.scoreString, this.performer, 5);
                }
            }
            else if (this.arrowHitting == ArrowHitting.NOT) {
                SoundPlayer.playSound("sound.arrow.miss", this.item, 1.6f);
            }
            if (this.arrowDestroy == ArrowDestroy.BREAKS) {
                Items.destroyItem(this.arrow.getWurmId());
                this.performer.getCommunicator().sendCombatNormalMessage("The arrow breaks.");
            }
            else if (this.arrowDestroy == ArrowDestroy.NORMAL) {
                Items.destroyItem(this.arrow.getWurmId());
            }
            else if (this.arrowDestroy == ArrowDestroy.WATER) {
                Items.destroyItem(this.arrow.getWurmId());
                this.performer.getCommunicator().sendCombatNormalMessage("The arrow disappears from your view.");
            }
            else if (this.arrowDestroy == ArrowDestroy.NOT) {
                try {
                    final Zone z = Zones.getZone(this.tileArrowDownX, this.tileArrowDownY, this.performer.isOnSurface());
                    final VolaTile t = z.getOrCreateTile(this.tileArrowDownX, this.tileArrowDownY);
                    t.addItem(this.arrow, false, false);
                    if (this.arrowHitting == ArrowHitting.NOT) {
                        SoundPlayer.playSound("sound.arrow.stuck.ground", this.tileArrowDownX, this.tileArrowDownY, this.item.isOnSurface(), 0.0f);
                    }
                }
                catch (NoSuchZoneException e) {
                    Arrows.logger.log(Level.WARNING, e.getMessage());
                }
            }
            return false;
        }
        return true;
    }
    
    public static final void hitCreature(final Item projectile, final Item source, final Creature performer, final Creature defender, final int damage, final ArrowDestroy arrowDestroy) {
        try {
            Arrows.arrows.add(new Arrows(projectile, performer, defender, -1, -1, ArrowHitting.HIT, arrowDestroy, source, damage, 1.0f, 0.0f, defender.getBody().getRandomWoundPos(), false, 0.0, 0.0));
        }
        catch (Exception e) {
            Arrows.logger.log(Level.WARNING, e.getMessage(), e);
        }
    }
    
    public static final void shootCreature(final Item shooter, final Creature defender, final int damage) {
        byte proj = 1;
        final int template = 830;
        if (!shooter.isEnchantedTurret()) {
            SoundPlayer.playSound("sound.arrow.shot", shooter, shooter.getSizeZ() / 2.0f);
        }
        else {
            switch (shooter.getTemplateId()) {
                case 940: {
                    proj = 6;
                    break;
                }
                case 941: {
                    proj = 7;
                    break;
                }
                case 968: {
                    proj = 5;
                    break;
                }
                case 942: {
                    proj = 8;
                    break;
                }
                default: {
                    proj = 1;
                    break;
                }
            }
        }
        try {
            final Item arrow = ItemFactory.createItem(830, 50.0f + shooter.getQualityLevel() / 2.0f, null);
            if (shooter.isEnchantedTurret()) {
                arrow.setName("ball of energy");
            }
            boolean parriedShield = false;
            final Item defShield = defender.getShield();
            if (defShield != null && defender.getStatus().getStamina() >= 300) {
                Skill defShieldSkill = null;
                final Skills defenderSkills = defender.getSkills();
                double defCheck = 0.0;
                int skillnum = -10;
                try {
                    skillnum = defShield.getPrimarySkill();
                    defShieldSkill = defenderSkills.getSkill(skillnum);
                }
                catch (NoSuchSkillException nss) {
                    if (skillnum != -10) {
                        defShieldSkill = defenderSkills.learn(skillnum, 1.0f);
                    }
                }
                if (defShieldSkill != null) {
                    double sdiff = Math.max(defShieldSkill.getKnowledge() - 10.0, Math.max(20.0f, defender.isMoving() ? (shooter.getQualityLevel() / 2.0f + 20.0f) : (shooter.getQualityLevel() / 2.0f)));
                    sdiff -= (defShield.getSizeY() + defShield.getSizeZ()) / 3.0f;
                    defCheck = defShieldSkill.skillCheck(sdiff, defShield, 0.0, false, 1.0f);
                }
                if (defCheck > 0.0) {
                    parriedShield = true;
                }
                defender.getStatus().modifyStamina(-300.0f);
            }
            try {
                if (parriedShield) {
                    Arrows.arrows.add(new Arrows(arrow, defender, ArrowHitting.SHIELD, ArrowDestroy.NORMAL, shooter, 0.0, defender.getBody().getRandomWoundPos(), proj));
                }
                else {
                    Arrows.arrows.add(new Arrows(arrow, defender, ArrowHitting.HIT, ArrowDestroy.NORMAL, shooter, damage, defender.getBody().getRandomWoundPos(), proj));
                }
            }
            catch (Exception e) {
                Arrows.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
        catch (FailedException e2) {
            Arrows.logger.log(Level.WARNING, e2.getMessage(), e2);
        }
        catch (NoSuchTemplateException e3) {
            Arrows.logger.log(Level.WARNING, e3.getMessage(), e3);
        }
    }
    
    public static final void addToHitCreature(final Item arrow, final Creature performer, final Creature defender, final float counter, final Action act, final int trees, final Item bow, final Skill bowskill, final Skill archery, final boolean isAttackingPenned, int tileArrowDownX, int tileArrowDownY, final int treetilex, final int treetiley, @Nullable final Fence fence, final boolean limitFail) {
        ArrowHitting arrowHitting = ArrowHitting.NOT;
        ArrowDestroy arrowDestroy = ArrowDestroy.NOT;
        double damage = 0.0;
        float armourMod = 0.0f;
        byte pos = 0;
        final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
        segments.add(new CreatureLineSegment(performer));
        segments.add(new MulticolorLineSegment(" lets an arrow fly.", (byte)0));
        MessageServer.broadcastColoredAction(segments, performer, 5, true);
        segments.get(1).setText(" let the arrow fly.");
        performer.getCommunicator().sendColoredMessageCombat(segments);
        SoundPlayer.playSound("sound.arrow.shot", performer, 1.6f);
        double diff = Archery.getBaseDifficulty(act.getNumber());
        if (WurmCalendar.getHour() > 19) {
            diff += WurmCalendar.getHour() - 19;
        }
        else if (WurmCalendar.getHour() < 6) {
            diff += 6 - WurmCalendar.getHour();
        }
        diff += trees;
        diff += Zones.getCoverHolder();
        final double deviation = Archery.getRangeDifficulty(performer, bow.getTemplateId(), defender.getPosX(), defender.getPosY());
        diff += deviation;
        diff -= arrow.getMaterialArrowDifficulty();
        float damMod = arrow.getMaterialArrowDamageModifier();
        diff -= bow.getRarity() * 5;
        diff -= arrow.getRarity() * 5;
        final float bon = bow.getSpellNimbleness();
        if (bon > 0.0f) {
            diff -= bon / 10.0f;
        }
        if (deviation > 5.0) {
            damMod *= 0.9f;
        }
        if (deviation > 10.0) {
            damMod *= (Servers.localServer.isChallengeServer() ? 0.6f : 0.9f);
        }
        if (deviation > 20.0) {
            damMod *= (Servers.localServer.isChallengeServer() ? 0.4f : 0.9f);
        }
        int nums = 1;
        diff -= bow.getMaterialBowDifficulty();
        diff = Math.max(1.0, diff);
        if (arrow.getTemplateId() == 456) {
            diff += 3.0;
        }
        else if (arrow.getTemplateId() == 454) {
            diff += 5.0;
        }
        try {
            final Skill bcontrol = defender.getSkills().getSkill(104);
            diff += bcontrol.getKnowledge(0.0) / 5.0;
        }
        catch (NoSuchSkillException nss) {
            Arrows.logger.log(Level.WARNING, defender.getWurmId() + ", " + defender.getName() + " no body control.");
        }
        damage = Archery.getDamage(performer, defender, bow, arrow, archery);
        if (damage < 3000.0 || arrow.getTemplateId() == 454) {
            nums = 0;
        }
        if (defender.getPositionZ() + defender.getAltOffZ() + defender.getCentimetersHigh() / 100.0f < -1.0f) {
            diff += 40.0;
        }
        diff *= 1.0 - defender.getMovementScheme().armourMod.getModifier();
        diff *= 1.0 + performer.getMovementScheme().armourMod.getModifier();
        boolean dryRun = defender.isNoSkillgain() || defender.isNoSkillFor(performer) || defender.isSentinel() || isAttackingPenned || (defender.getCitizenVillage() != null && defender.getCitizenVillage() == performer.getCitizenVillage());
        if (defender.isPlayer() && (!defender.isPaying() || defender.isNewbie())) {
            dryRun = true;
        }
        if (!defender.isPlayer() && (defender.getHitched() != null || defender.isRidden() || defender.getDominator() != null)) {
            dryRun = (Server.rand.nextInt(3) == 0);
        }
        final float armourBonusMultiplier = 1.0f + ((performer.getArmourLimitingFactor() > 0.0f) ? performer.getArmourLimitingFactor() : 0.0f);
        double bonus = bowskill.skillCheck(diff, bow, arrow.getCurrentQualityLevel(), dryRun, nums);
        if (defender.isMoving()) {
            if (defender.isPlayer()) {
                diff += 6.0;
            }
            else {
                diff += 4.0;
            }
        }
        if (performer.getArmourLimitingFactor() < 0.0f) {
            diff += Math.abs(performer.getArmourLimitingFactor()) * 20.0f;
        }
        if (act.getNumber() == 126 || act.getNumber() == 127 || act.getNumber() == 131) {
            bonus -= 30.0;
        }
        if (Servers.localServer.HOMESERVER && performer.getKingdomId() != Servers.localServer.KINGDOM) {
            bonus -= 30.0;
        }
        double power = archery.skillCheck(diff, bow, bonus / 5.0 * armourBonusMultiplier, dryRun, nums);
        double defCheck = 0.0;
        boolean parriedShield = false;
        if (power > 0.0) {
            final Item defShield = defender.getShield();
            if (defShield != null && defender.getStatus().getStamina() >= 300 && Archery.willParryWithShield(performer, defender)) {
                Skill defShieldSkill = null;
                final Skills defenderSkills = defender.getSkills();
                int skillnum = -10;
                try {
                    skillnum = defShield.getPrimarySkill();
                    defShieldSkill = defenderSkills.getSkill(skillnum);
                }
                catch (NoSuchSkillException nss2) {
                    if (skillnum != -10) {
                        defShieldSkill = defenderSkills.learn(skillnum, 1.0f);
                    }
                }
                if (defShieldSkill != null) {
                    double sdiff = Math.max(20.0, defender.isMoving() ? (power + 20.0) : power);
                    sdiff -= (defShield.getSizeY() + defShield.getSizeZ()) * (defShield.getCurrentQualityLevel() / 100.0f) / 10.0f;
                    defCheck = defShieldSkill.skillCheck(sdiff, defShield, 0.0, dryRun, 1.0f);
                }
                if (defCheck > 0.0) {
                    parriedShield = true;
                }
                defender.getStatus().modifyStamina(-300.0f);
            }
            defender.addAttacker(performer);
            power = Math.max(power, 5.0);
            pos = Archery.getWoundPos(defender, act.getNumber());
            pos = (byte)CombatEngine.getRealPosition(pos);
            armourMod = defender.getArmourMod();
            float evasionChance = 0.0f;
            Label_1362: {
                if (defCheck > 0.0) {
                    arrowHitting = ArrowHitting.SHIELD;
                }
                else {
                    if (armourMod != 1.0f) {
                        if (!defender.isVehicle()) {
                            break Label_1362;
                        }
                    }
                    try {
                        final byte bodyPosition = ArmourTemplate.getArmourPosition(pos);
                        final Item armour = defender.getArmour(bodyPosition);
                        armourMod = ArmourTemplate.calculateDR(armour, (byte)2);
                        armour.setDamage(armour.getDamage() + (float)(damage * armourMod / 300000.0) * armour.getDamageModifier() * ArmourTemplate.getArmourDamageModFor(armour, (byte)2));
                        CombatEngine.checkEnchantDestruction(arrow, armour, defender);
                        evasionChance = ArmourTemplate.calculateArrowGlance(armour, arrow);
                    }
                    catch (NoArmourException nsi) {
                        if (!CombatEngine.isEye(pos) || defender.isUnique()) {
                            evasionChance = 1.0f - defender.getArmourMod();
                        }
                    }
                    catch (NoSpaceException nsp) {
                        Arrows.logger.log(Level.WARNING, defender.getName() + " no armour space on loc " + pos, nsp);
                    }
                    if (defender.getBonusForSpellEffect((byte)22) > 0.0f) {
                        if (armourMod >= 1.0f) {
                            armourMod = 0.2f + (1.0f - defender.getBonusForSpellEffect((byte)22) / 100.0f) * 0.6f;
                        }
                        else {
                            armourMod = Math.min(armourMod, 0.2f + (1.0f - defender.getBonusForSpellEffect((byte)22) / 100.0f) * 0.6f);
                        }
                    }
                }
            }
            if (!defender.isPlayer() && !performer.isInvulnerable()) {
                defender.setTarget(performer.getWurmId(), false);
            }
            if (defender.isUnique()) {
                evasionChance = 0.5f;
                damage *= armourMod;
            }
            boolean dropattile = false;
            if (defCheck > 0.0) {
                dropattile = true;
            }
            else if (Server.rand.nextFloat() < evasionChance) {
                dropattile = true;
                arrowHitting = ArrowHitting.EVASION;
            }
            else if (damage > 500.0) {
                arrowHitting = ArrowHitting.HIT;
            }
            else {
                dropattile = true;
                arrowHitting = ArrowHitting.HIT_NO_DAMAGE;
            }
            if (dropattile) {
                if (defCheck > 0.0 && parriedShield) {
                    tileArrowDownX = defender.getCurrentTile().tilex;
                    tileArrowDownY = defender.getCurrentTile().tiley;
                    final float damageMod = 1.0E-5f;
                    if (defender.isPlayer()) {
                        defShield.setDamage(defShield.getDamage() + Math.max(0.01f, 1.0E-5f * (float)damage * defShield.getDamageModifier()));
                    }
                    if (defShield.isWood()) {
                        SoundPlayer.playSound("sound.arrow.hit.wood", defender, 1.6f);
                    }
                    else if (defShield.isMetal()) {
                        SoundPlayer.playSound("sound.arrow.hit.metal", defender, 1.6f);
                    }
                }
                if (Server.rand.nextInt(Math.max(1, (int)arrow.getCurrentQualityLevel())) < 2) {
                    arrowDestroy = ArrowDestroy.NORMAL;
                }
                else if (!arrow.setDamage(arrow.getDamage() + 5.0f * damMod)) {
                    tileArrowDownX = defender.getCurrentTile().tilex;
                    tileArrowDownY = defender.getCurrentTile().tiley;
                    try {
                        Zones.getZone(tileArrowDownX, tileArrowDownY, performer.isOnSurface());
                    }
                    catch (NoSuchZoneException nsz) {
                        arrowDestroy = ArrowDestroy.WATER;
                    }
                }
            }
        }
        else if (Server.rand.nextInt(Math.max(1, (int)arrow.getCurrentQualityLevel())) < 2) {
            arrowDestroy = ArrowDestroy.BREAKS;
            arrowHitting = ArrowHitting.HIT_NO_DAMAGE;
        }
        else if (arrow.setDamage(arrow.getDamage() + 5.0f * damMod)) {
            arrowDestroy = ArrowDestroy.BREAKS;
            arrowHitting = ArrowHitting.HIT_NO_DAMAGE;
        }
        else {
            if (trees > 0 && treetilex > 0) {
                tileArrowDownX = treetilex;
                tileArrowDownY = treetiley;
            }
            boolean hitdef = false;
            if (tileArrowDownX == -1) {
                if (defender.opponent != null && performer.getKingdomId() == defender.opponent.getKingdomId() && power < -20.0 && Server.rand.nextInt(100) < Math.abs(power) && defender.opponent.isPlayer() && defender.opponent != performer) {
                    pos = Archery.getWoundPos(defender.opponent, act.getNumber());
                    pos = (byte)CombatEngine.getRealPosition(pos);
                    armourMod = defender.opponent.getArmourMod();
                    damage = Archery.getDamage(performer, defender, bow, arrow, archery);
                    Label_2004: {
                        if (armourMod != 1.0f) {
                            if (!defender.isVehicle()) {
                                break Label_2004;
                            }
                        }
                        try {
                            final byte bodyPosition2 = ArmourTemplate.getArmourPosition(pos);
                            final Item armour2 = defender.opponent.getArmour(bodyPosition2);
                            armourMod = ArmourTemplate.calculateDR(armour2, (byte)2);
                            armour2.setDamage(armour2.getDamage() + (float)(damage * armourMod / 300000.0) * armour2.getDamageModifier() * ArmourTemplate.getArmourDamageModFor(armour2, (byte)2));
                            CombatEngine.checkEnchantDestruction(arrow, armour2, defender.opponent);
                        }
                        catch (NoArmourException ex) {}
                        catch (NoSpaceException nsp2) {
                            Arrows.logger.log(Level.WARNING, defender.getName() + " no armour space on loc " + pos);
                        }
                    }
                    arrowHitting = ArrowHitting.HIT;
                    hitdef = true;
                }
                if (!hitdef) {
                    tileArrowDownX = defender.getCurrentTile().tilex;
                    tileArrowDownY = defender.getCurrentTile().tiley;
                }
            }
            if (!hitdef) {
                try {
                    Zones.getZone(tileArrowDownX, tileArrowDownY, performer.isOnSurface());
                    if (treetilex > 0) {
                        arrowHitting = ArrowHitting.TREE;
                    }
                    else if (fence != null) {
                        if (fence.isStone()) {
                            arrowHitting = ArrowHitting.FENCE_STONE;
                        }
                        else {
                            arrowHitting = ArrowHitting.FENCE_TREE;
                        }
                    }
                    else {
                        arrowHitting = ArrowHitting.GROUND;
                    }
                }
                catch (NoSuchZoneException nsz2) {
                    arrowDestroy = ArrowDestroy.WATER;
                }
            }
        }
        Arrows.arrows.add(new Arrows(arrow, performer, defender, tileArrowDownX, tileArrowDownY, arrowHitting, arrowDestroy, bow, damage, damMod, armourMod, pos, dryRun, diff, bonus));
    }
    
    public static void addToHitItem(final Item arrow, final Creature performer, final Item target, final float counter, final Skill bowskill, final Item bow, int tileArrowDownX, int tileArrowDownY, final double deviation, double diff, final int trees, final int treetilex, final int treetiley) {
        ArrowHitting arrowHitting = ArrowHitting.NOT;
        ArrowDestroy arrowDestroy = ArrowDestroy.NOT;
        String scoreString = "outside the rings.";
        Server.getInstance().broadCastAction(performer.getName() + " lets an arrow fly.", performer, 5);
        performer.getCommunicator().sendNormalServerMessage("You let the arrow fly.");
        SoundPlayer.playSound("sound.arrow.aim", performer, 1.0f);
        diff -= arrow.getMaterialArrowDifficulty();
        float damMod = arrow.getMaterialArrowDamageModifier();
        if (deviation > 5.0) {
            damMod *= 0.9f;
        }
        if (deviation > 10.0) {
            damMod *= 0.9f;
        }
        if (deviation > 20.0) {
            damMod *= 0.9f;
        }
        diff -= bow.getMaterialBowDifficulty();
        diff = Math.max(1.0, diff);
        if (arrow.getTemplateId() == 456) {
            diff += 3.0;
        }
        else if (arrow.getTemplateId() == 454) {
            diff += 5.0;
        }
        boolean dryrun = false;
        if (bowskill.getRealKnowledge() >= 30.0) {
            if (Server.rand.nextInt(10) == 0) {
                performer.getCommunicator().sendNormalServerMessage("You don't learn anything from this type of shooting any more.");
            }
            dryrun = true;
        }
        if (target.getTemplateId() != 458 && !target.isBoat()) {
            dryrun = true;
        }
        Skill archery = null;
        try {
            archery = performer.getSkills().getSkill(1030);
        }
        catch (NoSuchSkillException nss) {
            archery = performer.getSkills().learn(1030, 1.0f);
        }
        if (archery.getKnowledge() >= 40.0) {
            if (!dryrun && Server.rand.nextInt(10) == 0) {
                performer.getCommunicator().sendNormalServerMessage("You don't learn anything from this type of shooting any more.");
            }
            dryrun = true;
        }
        final double bonus = bowskill.skillCheck(diff, bow, arrow.getCurrentQualityLevel(), dryrun, counter);
        double power = archery.skillCheck(diff, bow, bonus, dryrun, counter);
        if (target.isBoat() && target.getDamage() > 50.0f) {
            power = 0.0;
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is too hard to hit because of existing damage.");
        }
        if (power > 0.0) {
            power = Math.max(power, 5.0);
            double damage = bow.getDamagePercent() * bow.getCurrentQualityLevel() / 100.0f;
            damage *= 1.0 + (performer.getStrengthSkill() - 20.0) / 100.0;
            damage *= 1.0f + arrow.getCurrentQualityLevel() / 100.0f;
            if (arrow.getTemplateId() == 456) {
                damage *= 1.2000000476837158;
            }
            else if (arrow.getTemplateId() == 454) {
                damage *= 0.30000001192092896;
            }
            target.setDamage((float)(target.getDamage() + damage / 1000000.0));
            boolean dropattile = false;
            if (target.getTemplateId() != 458) {
                performer.getCommunicator().sendSafeServerMessage("Your arrow hits the " + target.getName() + ".");
                Server.getInstance().broadCastAction("An arrow hits " + target.getNameWithGenus() + ".", performer, 5);
            }
            else {
                scoreString = "outside the rings.";
                final int points = (int)(power / 10.0);
                if (points == 10) {
                    scoreString = "a perfect ten!";
                }
                else if (points == 9) {
                    scoreString = "a fine 9!";
                }
                else if (points == 8) {
                    scoreString = "a skilled 8.";
                }
                else if (points > 2) {
                    scoreString = "a " + points + ".";
                }
                else {
                    scoreString = "a measly " + points + ".";
                }
            }
            arrowHitting = ArrowHitting.HIT;
            if (Server.rand.nextInt(Math.max(1, (int)arrow.getCurrentQualityLevel())) < 2) {
                arrowDestroy = ArrowDestroy.BREAKS;
            }
            else if (!arrow.setDamage(arrow.getDamage() + 5.0f * damMod)) {
                if (target.isHollow()) {
                    target.insertItem(arrow, true);
                }
                else {
                    dropattile = true;
                }
            }
            if (dropattile) {
                try {
                    tileArrowDownX = target.getTileX();
                    tileArrowDownY = target.getTileY();
                    Zones.getZone(tileArrowDownX, tileArrowDownY, performer.isOnSurface());
                }
                catch (NoSuchZoneException nsz) {
                    arrowDestroy = ArrowDestroy.WATER;
                }
            }
        }
        else {
            arrowHitting = ArrowHitting.NOT;
            if (Server.rand.nextInt(Math.max(1, (int)arrow.getCurrentQualityLevel())) < 2) {
                arrowDestroy = ArrowDestroy.BREAKS;
            }
            else if (arrow.setDamage(arrow.getDamage() + 5.0f * damMod)) {
                arrowDestroy = ArrowDestroy.BREAKS;
            }
            else {
                if (trees > 0) {
                    if (treetilex > 0) {
                        tileArrowDownX = treetilex;
                        tileArrowDownY = treetiley;
                    }
                }
                else {
                    tileArrowDownX = target.getTileX();
                    tileArrowDownY = target.getTileY();
                }
                try {
                    Zones.getZone(tileArrowDownX, tileArrowDownY, performer.isOnSurface());
                }
                catch (NoSuchZoneException nsz2) {
                    arrowDestroy = ArrowDestroy.WATER;
                }
            }
        }
        Arrows.arrows.add(new Arrows(arrow, performer, target, tileArrowDownX, tileArrowDownY, bow, arrowHitting, arrowDestroy, scoreString));
    }
    
    static {
        Arrows.logger = Logger.getLogger(Arrows.class.getName());
        arrows = new HashSet<Arrows>();
    }
    
    public enum ArrowHitting
    {
        NOT, 
        SHIELD, 
        EVASION, 
        HIT, 
        HIT_NO_DAMAGE, 
        TREE, 
        FENCE_STONE, 
        FENCE_TREE, 
        GROUND;
    }
    
    public enum ArrowDestroy
    {
        NOT, 
        NORMAL, 
        WATER, 
        BREAKS, 
        DO_NOTHING;
    }
}
