// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.combat;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.effects.EffectFactory;
import com.wurmonline.server.structures.Structures;
import java.util.ArrayList;
import com.wurmonline.server.structures.Blocker;
import com.wurmonline.server.creatures.ai.PathTile;
import com.wurmonline.server.structures.Blocking;
import com.wurmonline.math.Vector3f;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.bodys.TempWound;
import java.util.Set;
import java.util.HashSet;
import com.wurmonline.server.zones.VirtualZone;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.behaviours.MethodsItems;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.Items;
import com.wurmonline.server.behaviours.MethodsStructure;
import com.wurmonline.server.utils.logging.TileEvent;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import java.util.Iterator;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.Servers;
import com.wurmonline.math.Vector2f;
import java.util.logging.Level;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.Features;
import com.wurmonline.server.zones.NoSuchZoneException;
import java.util.concurrent.CopyOnWriteArraySet;
import com.wurmonline.server.structures.BlockingResult;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class ServerProjectile implements MiscConstants
{
    private static final Logger logger;
    public static final float meterPerSecond = 12.0f;
    private static final float gravity = 0.04f;
    private static final float newGravity = -9.8f;
    public static final int TICKS_PER_SECOND = 24;
    private final float posDownX;
    private final float posDownY;
    private final Item projectile;
    private float currentSecondsInAir;
    private long timeAtLanding;
    private final Creature shooter;
    private final Item weapon;
    private final byte rarity;
    private float damageDealth;
    private BlockingResult result;
    private static final CopyOnWriteArraySet<ServerProjectile> projectiles;
    private ProjectileInfo projectileInfo;
    boolean sentEffect;
    
    public ServerProjectile(final Item aWeapon, final Item aProjectile, final float aPosDownX, final float aPosDownY, final Creature aShooter, final byte actionRarity, final float damDealt) throws NoSuchZoneException {
        this.currentSecondsInAir = 0.0f;
        this.timeAtLanding = 0L;
        this.damageDealth = 0.0f;
        this.result = null;
        this.projectileInfo = null;
        this.sentEffect = false;
        this.weapon = aWeapon;
        this.projectile = aProjectile;
        this.posDownX = aPosDownX;
        this.posDownY = aPosDownY;
        this.shooter = aShooter;
        this.setDamageDealt(damDealt);
        this.rarity = actionRarity;
        ServerProjectile.projectiles.add(this);
    }
    
    public boolean fire(final boolean isOnSurface) throws NoSuchZoneException {
        if (Features.Feature.NEW_PROJECTILES.isEnabled() && this.weapon.getTemplateId() != 936) {
            float firingAngle = 45.0f + this.weapon.getAuxData() * 5;
            if (this.weapon.getTemplateId() == 936) {
                firingAngle = 7.5f;
            }
            final ProjectileInfo projectileInfo = getProjectileInfo(this.weapon, this.shooter, firingAngle, 15.0f);
            this.projectileInfo = projectileInfo;
            final VolaTile t = Zones.getOrCreateTile((int)(projectileInfo.endPosition.x / 4.0f), (int)(projectileInfo.endPosition.y / 4.0f), this.weapon.isOnSurface());
            final Village v = Villages.getVillage((int)(projectileInfo.endPosition.x / 4.0f), (int)(projectileInfo.endPosition.y / 4.0f), this.weapon.isOnSurface());
            if (!isOkToAttack(t, this.getShooter(), this.getDamageDealt())) {
                boolean ok = false;
                if (v != null && (v.isActionAllowed((short)174, this.getShooter(), false, 0, 0) || v.isEnemy(this.getShooter()))) {
                    ok = true;
                }
                if (!ok) {
                    this.shooter.getCommunicator().sendNormalServerMessage("You cannot fire the " + this.getProjectile().getName() + " to there, you are not allowed.");
                    return false;
                }
            }
            Skill firingSkill = null;
            int skillType = 10077;
            if (this.weapon.getTemplateId() == 936) {
                skillType = 10093;
            }
            else if (this.weapon.getTemplateId() == 937) {
                skillType = 10094;
            }
            firingSkill = this.shooter.getSkills().getSkillOrLearn(skillType);
            firingSkill.skillCheck(this.weapon.getWinches(), 0.0, false, this.weapon.getWinches() / 5.0f);
            this.weapon.setData(0L);
            this.weapon.setWinches((short)0);
            if (this.weapon.getTemplateId() == 937) {
                int weight = 0;
                for (final Item i : this.weapon.getAllItems(true)) {
                    weight += i.getWeightGrams();
                }
                this.weapon.setWinches((byte)Math.min(50, weight / 20000));
            }
            this.timeAtLanding = System.currentTimeMillis() + projectileInfo.timeToImpact;
            final VolaTile startTile = Zones.getOrCreateTile((int)(projectileInfo.startPosition.x / 4.0f), (int)(projectileInfo.startPosition.y / 4.0f), isOnSurface);
            startTile.sendNewProjectile(this.getProjectile().getWurmId(), (byte)2, this.getProjectile().getModelName(), this.getProjectile().getName(), this.getProjectile().getMaterial(), projectileInfo.startPosition, projectileInfo.startVelocity, projectileInfo.endPosition, this.weapon.getRotation(), this.weapon.isOnSurface());
            final VolaTile endTile = Zones.getOrCreateTile((int)(projectileInfo.endPosition.x / 4.0f), (int)(projectileInfo.endPosition.y / 4.0f), isOnSurface);
            endTile.sendNewProjectile(this.getProjectile().getWurmId(), (byte)2, this.getProjectile().getModelName(), this.getProjectile().getName(), this.getProjectile().getMaterial(), projectileInfo.startPosition, projectileInfo.startVelocity, projectileInfo.endPosition, this.weapon.getRotation(), this.weapon.isOnSurface());
            return true;
        }
        final float targetZ = Zones.calculateHeight(this.posDownX, this.posDownY, isOnSurface);
        this.result = calculateBlocker(this.weapon, this.posDownX, this.posDownY, targetZ);
        if (this.result == null) {
            ServerProjectile.logger.log(Level.INFO, "Blocker is null");
            return false;
        }
        if (this.result.getFirstBlocker() == null) {
            ServerProjectile.logger.log(Level.INFO, "No blocker");
            return false;
        }
        final float newx = this.result.getFirstBlocker().getTileX() * 4 + 2;
        final float newy = this.result.getFirstBlocker().getTileY() * 4 + 2;
        final Vector2f targPos = new Vector2f(this.weapon.getTileX() * 4 + 2, this.weapon.getTileY() * 4 + 2);
        final Vector2f projPos = new Vector2f(newx, newy);
        final float dist = projPos.subtract(targPos).length() / 4.0f;
        if (dist < 8.0f) {
            if (this.shooter.getPower() > 0 && Servers.isThisATestServer()) {
                this.shooter.getCommunicator().sendNormalServerMessage("Calculated block from " + this.weapon.getPosX() + "," + this.weapon.getPosY() + " dist:" + dist + " at " + newx + "," + newy + ".");
            }
            this.shooter.getCommunicator().sendNormalServerMessage(" You cannot fire at such a short range.");
            return false;
        }
        this.weapon.setData(0L);
        this.weapon.setWinches((short)0);
        this.setTimeAtLanding(System.currentTimeMillis() + (long)(this.result.getActualBlockingTime() * 1000.0f));
        VolaTile tile = Zones.getOrCreateTile(this.weapon.getTileX(), this.weapon.getTileY(), isOnSurface);
        tile.sendProjectile(this.getProjectile().getWurmId(), (byte)((this.weapon.getTemplateId() == 936) ? 9 : 2), this.getProjectile().getModelName(), this.getProjectile().getName(), this.getProjectile().getMaterial(), this.weapon.getPosX(), this.weapon.getPosY(), this.weapon.getPosZ(), this.weapon.getRotation(), (byte)0, projPos.x, projPos.y, targetZ, this.weapon.getWurmId(), -10L, this.result.getEstimatedBlockingTime(), this.result.getActualBlockingTime());
        tile = Zones.getOrCreateTile((int)(projPos.x / 4.0f), (int)(projPos.y / 4.0f), true);
        tile.sendProjectile(this.getProjectile().getWurmId(), (byte)((this.weapon.getTemplateId() == 936) ? 9 : 2), this.getProjectile().getModelName(), this.getProjectile().getName(), this.getProjectile().getMaterial(), this.weapon.getPosX(), this.weapon.getPosY(), this.weapon.getPosZ(), this.weapon.getRotation(), (byte)0, projPos.x, projPos.y, targetZ, this.weapon.getWurmId(), -10L, this.result.getEstimatedBlockingTime(), this.result.getActualBlockingTime());
        if (this.shooter.getPower() >= 5) {
            this.shooter.getCommunicator().sendNormalServerMessage("You hit tile (" + this.result.getFirstBlocker().getTileX() + "," + this.result.getFirstBlocker().getTileY() + "), distance: " + dist + ".");
        }
        if (this.weapon.getTemplateId() == 937) {
            this.weapon.setLastMaintained(WurmCalendar.currentTime);
        }
        return true;
    }
    
    public long getTimeAtLanding() {
        return this.timeAtLanding;
    }
    
    public void setTimeAtLanding(final long aTimeAtLanding) {
        this.timeAtLanding = aTimeAtLanding;
    }
    
    public static final void clear() {
        for (final ServerProjectile projectile : ServerProjectile.projectiles) {
            projectile.poll(Long.MAX_VALUE);
        }
    }
    
    public static final boolean isOkToAttack(final VolaTile t, final Creature performer, final float damdealt) {
        boolean ok = true;
        final Village v = t.getVillage();
        if (v != null && performer.isFriendlyKingdom(v.kingdom)) {
            if (v.isActionAllowed((short)174, performer, false, 0, 0)) {
                ok = true;
            }
            else if (!v.isEnemy(performer)) {
                performer.setUnmotivatedAttacker();
                ok = false;
                if (t.isInPvPZone()) {
                    v.modifyReputation(performer.getWurmId(), -20, false);
                    if (performer.getKingdomTemplateId() != 3) {
                        performer.setReputation(performer.getReputation() - 30);
                        performer.getCommunicator().sendAlertServerMessage("This is bad for your reputation.");
                        if (performer.getDeity() != null && !performer.getDeity().isLibila() && Server.rand.nextInt(Math.max(1, (int)performer.getFaith())) < 5) {
                            performer.getCommunicator().sendNormalServerMessage(performer.getDeity().name + " has noticed you and is upset at your behaviour!");
                            performer.modifyFaith(-0.25f);
                            performer.maybeModifyAlignment(-1.0f);
                        }
                    }
                    else {
                        ok = true;
                    }
                }
            }
        }
        final Structure structure = t.getStructure();
        if (structure != null && structure.isTypeBridge()) {
            ok = true;
        }
        if (structure != null && structure.isTypeHouse() && damdealt > 0.0f) {
            if (v != null && v.isEnemy(performer)) {
                ok = true;
            }
            if (performer.getKingdomTemplateId() != 3) {
                final byte ownerkingdom = Players.getInstance().getKingdomForPlayer(structure.getOwnerId());
                if (performer.isFriendlyKingdom(ownerkingdom)) {
                    ok = false;
                    boolean found = false;
                    if ((!t.isInPvPZone() || v != null) && structure.isFinished() && structure.isLocked()) {
                        found = (structure.mayModify(performer) || t.isInPvPZone());
                    }
                    if (found) {
                        ok = true;
                    }
                    if (!found) {
                        performer.setUnmotivatedAttacker();
                        if (t.isInPvPZone()) {
                            ok = true;
                        }
                    }
                }
                else {
                    ok = true;
                }
                if (structure.mayModify(performer)) {
                    ok = true;
                }
            }
            else {
                ok = true;
            }
        }
        return ok;
    }
    
    public static final boolean setEffects(final Item weapon, final Item projectile, final int newx, final int newy, final float dist, final int floorLevelDown, final Creature performer, final byte rarity, float damdealt) {
        try {
            Zones.getZone(newx, newy, weapon.isOnSurface());
            VolaTile t = Zones.getOrCreateTile(newx, newy, weapon.isOnSurface());
            String whatishit = "the ground";
            boolean hit = false;
            boolean ok = isOkToAttack(t, performer, damdealt);
            double pwr = 0.0;
            int floorLevel = 0;
            final Structure structure = t.getStructure();
            Skill cataskill = null;
            boolean doneSkillRoll = false;
            boolean arrowStuck = false;
            int skilltype = 10077;
            if (weapon.getTemplateId() == 936) {
                skilltype = 10093;
            }
            if (weapon.getTemplateId() == 937) {
                skilltype = 10094;
            }
            try {
                cataskill = performer.getSkills().getSkill(skilltype);
            }
            catch (NoSuchSkillException nss) {
                cataskill = performer.getSkills().learn(skilltype, 1.0f);
            }
            if (structure != null && structure.isTypeHouse()) {
                hit = true;
                whatishit = structure.getName();
                if (!t.isInPvPZone() && !ok && performer.getKingdomTemplateId() != 3) {
                    final byte ownerkingdom = Players.getInstance().getKingdomForPlayer(structure.getOwnerId());
                    if (performer.isFriendlyKingdom(ownerkingdom)) {
                        damdealt = 0.0f;
                        hit = false;
                    }
                }
                if (hit && !doneSkillRoll) {
                    pwr = cataskill.skillCheck(dist - 9.0, weapon, 0.0, false, 10.0f);
                    doneSkillRoll = true;
                }
                if (pwr > 0.0) {
                    if (damdealt > 0.0f) {
                        int destroyed = 0;
                        final Floor f = t.getTopFloor();
                        if (f != null) {
                            floorLevel = f.getFloorLevel();
                        }
                        final Wall w = t.getTopWall();
                        if (w != null && w.getFloorLevel() > floorLevel) {
                            floorLevel = w.getFloorLevel();
                        }
                        final Fence fence = t.getTopFence();
                        if (fence != null && fence.getFloorLevel() > floorLevel) {
                            floorLevel = fence.getFloorLevel();
                        }
                        boolean logged = false;
                        final float mod = 2.0f;
                        if (floorLevel > 0) {
                            final Floor[] floors = t.getFloors(floorLevel * 30, floorLevel * 30);
                            if (floors.length > 0) {
                                for (int x = 0; x < floors.length; ++x) {
                                    final float newdam = floors[x].getDamage() + Math.min(20.0f, floors[x].getDamageModifier() * damdealt / 2.0f);
                                    if (newdam >= 100.0f) {
                                        if (!logged) {
                                            logged = true;
                                            TileEvent.log(floors[x].getTileX(), floors[x].getTileY(), 0, performer.getWurmId(), 236);
                                        }
                                        ++destroyed;
                                    }
                                    if (floors[x].setDamage(newdam)) {
                                        floors[x].getTile().removeFloor(floors[x]);
                                    }
                                    arrowStuck = true;
                                }
                            }
                        }
                        final Wall[] warr = t.getWalls();
                        for (int x = 0; x < warr.length; ++x) {
                            if (warr[x].getFloorLevel() == floorLevel) {
                                final float newdam = warr[x].getDamage() + Math.min(warr[x].isFinished() ? 20.0f : 100.0f, warr[x].getDamageModifier() * damdealt / 2.0f);
                                if (newdam >= 100.0f) {
                                    if (!logged) {
                                        logged = true;
                                        TileEvent.log(warr[x].getTileX(), warr[x].getTileY(), 0, performer.getWurmId(), 236);
                                    }
                                    ++destroyed;
                                }
                                warr[x].setDamage(newdam);
                                arrowStuck = true;
                            }
                        }
                        final Floor[] floors2 = t.getFloors();
                        for (int x2 = 0; x2 < floors2.length; ++x2) {
                            if (floors2[x2].getFloorLevel() == floorLevel) {
                                final float newdam2 = floors2[x2].getDamage() + Math.min(20.0f, floors2[x2].getDamageModifier() * damdealt / 2.0f);
                                if (newdam2 >= 100.0f) {
                                    if (!logged) {
                                        logged = true;
                                        TileEvent.log(floors2[x2].getTileX(), floors2[x2].getTileY(), 0, performer.getWurmId(), 236);
                                    }
                                    ++destroyed;
                                }
                                floors2[x2].setDamage(newdam2);
                                arrowStuck = true;
                            }
                        }
                        if (destroyed > 0 && !ok) {
                            performer.getCommunicator().sendNormalServerMessage("You feel very bad about this.");
                            performer.maybeModifyAlignment(-5.0f);
                            performer.punishSkills(0.1 * Math.min(3, destroyed), false);
                        }
                        alertGuards(performer, newx, newy, ok, t, destroyed);
                    }
                    if (damdealt > 0.0f) {
                        performer.getCommunicator().sendNormalServerMessage("You seem to have hit " + t.getStructure().getName() + "!" + (Servers.isThisATestServer() ? (" Dealt:" + damdealt) : ""));
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You seem to have hit " + t.getStructure().getName() + " but luckily it took no damage!");
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You just missed " + t.getStructure().getName() + ".");
                }
                if (t.getStructure() == null) {
                    performer.achievement(51);
                }
            }
            if (structure != null && structure.isTypeBridge()) {
                hit = true;
                whatishit = structure.getName();
                if (hit && !doneSkillRoll) {
                    pwr = cataskill.skillCheck(dist - 9.0, weapon, 0.0, false, 10.0f);
                    doneSkillRoll = true;
                }
                if (pwr > 0.0) {
                    if (damdealt > 0.0f) {
                        for (final BridgePart bp : t.getBridgeParts()) {
                            final float mod2 = bp.getModByMaterial();
                            final float newdam3 = bp.getDamage() + Math.min(20.0f, bp.getDamageModifier() * damdealt / mod2);
                            TileEvent.log(bp.getTileX(), bp.getTileY(), 0, performer.getWurmId(), 236);
                            bp.setDamage(newdam3);
                        }
                    }
                    if (damdealt > 0.0f) {
                        performer.getCommunicator().sendNormalServerMessage("You seem to have hit " + t.getStructure().getName() + "!" + (Servers.isThisATestServer() ? (" Dealt:" + damdealt) : ""));
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("You seem to have hit " + t.getStructure().getName() + " but luckily it took no damage!");
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You just missed " + t.getStructure().getName() + ".");
                }
            }
            final Fence[] fencesForLevel = t.getFencesForLevel(floorLevel);
            for (int length2 = fencesForLevel.length, j = 0; j < length2; ++j) {
                final Fence fence = fencesForLevel[j];
                hit = true;
                whatishit = "the " + fence.getName();
                final Village vill = MethodsStructure.getVillageForFence(fence);
                if (!ok && vill != null) {
                    if (vill.isActionAllowed((short)174, performer, false, 0, 0)) {
                        ok = true;
                    }
                    else if (!vill.isEnemy(performer)) {
                        hit = false;
                        damdealt = 0.0f;
                    }
                }
                if (hit && !doneSkillRoll) {
                    pwr = cataskill.skillCheck(dist - 9.0, weapon, 0.0, false, 10.0f);
                    doneSkillRoll = true;
                }
                if (pwr > 0.0) {
                    if (damdealt > 0.0f) {
                        final float mod = 2.0f;
                        TileEvent.log(fence.getTileX(), fence.getTileY(), 0, performer.getWurmId(), 236);
                        fence.setDamage(fence.getDamage() + Math.min(fence.isFinished() ? 20.0f : 100.0f, fence.getDamageModifier() * damdealt / 2.0f));
                        performer.getCommunicator().sendNormalServerMessage("You seem to have hit " + whatishit + "!");
                        arrowStuck = true;
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You just missed some fences with the " + projectile.getName() + ".");
                }
            }
            final VolaTile southTile = Zones.getTileOrNull(newx, newy + 1, true);
            if (southTile != null) {
                for (final Fence fence2 : southTile.getFencesForLevel(floorLevel)) {
                    if (fence2.isHorizontal()) {
                        hit = true;
                        whatishit = "the " + fence2.getName();
                        final Village vill2 = MethodsStructure.getVillageForFence(fence2);
                        if (!ok && vill2 != null) {
                            if (vill2.isActionAllowed((short)174, performer, false, 0, 0)) {
                                ok = true;
                            }
                            else if (!vill2.isEnemy(performer)) {
                                hit = false;
                                damdealt = 0.0f;
                            }
                        }
                        if (hit && !doneSkillRoll) {
                            pwr = cataskill.skillCheck(dist - 9.0, weapon, 0.0, false, 10.0f);
                            doneSkillRoll = true;
                        }
                        if (pwr > 0.0) {
                            if (damdealt > 0.0f) {
                                final float mod3 = 2.0f;
                                TileEvent.log(fence2.getTileX(), fence2.getTileY(), 0, performer.getWurmId(), 236);
                                fence2.setDamage(fence2.getDamage() + Math.min(20.0f, fence2.getDamageModifier() * damdealt / 2.0f));
                                performer.getCommunicator().sendNormalServerMessage("You seem to have hit " + whatishit + "!");
                                arrowStuck = true;
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("You just missed some fences with the " + projectile.getName() + ".");
                        }
                    }
                }
            }
            final VolaTile eastTile = Zones.getTileOrNull(newx + 1, newy, true);
            if (eastTile != null) {
                for (final Fence fence3 : eastTile.getFencesForLevel(floorLevel)) {
                    if (!fence3.isHorizontal()) {
                        hit = true;
                        whatishit = "the " + fence3.getName();
                        final Village vill3 = MethodsStructure.getVillageForFence(fence3);
                        if (!ok && vill3 != null) {
                            if (vill3.isActionAllowed((short)174, performer, false, 0, 0)) {
                                ok = true;
                            }
                            else if (!vill3.isEnemy(performer)) {
                                hit = false;
                                damdealt = 0.0f;
                            }
                        }
                        if (hit && !doneSkillRoll) {
                            pwr = cataskill.skillCheck(dist - 9.0, weapon, 0.0, false, 10.0f);
                            doneSkillRoll = true;
                        }
                        if (pwr > 0.0) {
                            if (damdealt > 0.0f) {
                                final float mod4 = 2.0f;
                                TileEvent.log(fence3.getTileX(), fence3.getTileY(), 0, performer.getWurmId(), 236);
                                fence3.setDamage(fence3.getDamage() + Math.min(20.0f, fence3.getDamageModifier() * damdealt / 2.0f));
                                performer.getCommunicator().sendNormalServerMessage("You seem to have hit " + whatishit + "!");
                                arrowStuck = true;
                            }
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("You just missed some fences with the " + projectile.getName() + ".");
                        }
                    }
                }
            }
            if (testHitCreaturesOnTile(t, performer, projectile, damdealt, dist, floorLevel)) {
                if (weapon.getTemplateId() != 936 || !arrowStuck) {
                    if (weapon.getTemplateId() == 936) {
                        damdealt *= 3.0f;
                    }
                    if (!doneSkillRoll) {
                        pwr = cataskill.skillCheck(dist - 9.0, weapon, 0.0, false, 10.0f);
                        doneSkillRoll = true;
                    }
                    final boolean hit2 = hitCreaturesOnTile(t, pwr, performer, projectile, damdealt, dist, floorLevel);
                    if (!hit && hit2) {
                        hit = true;
                    }
                }
                if (!hit) {
                    performer.getCommunicator().sendNormalServerMessage("You hit nothing with the " + projectile.getName() + ".");
                }
            }
            t = Zones.getOrCreateTile(newx, newy, weapon.isOnSurface());
            if (projectile.isEgg()) {
                t.broadCast("A " + projectile.getName() + " comes flying through the air, hits " + whatishit + ", and shatters.");
                performer.getCommunicator().sendNormalServerMessage("The " + projectile.getName() + " shatters.");
                Items.destroyItem(projectile.getWurmId());
            }
            else if (projectile.setDamage(projectile.getDamage() + projectile.getDamageModifier() * (20 + Server.rand.nextInt(Math.max(1, (int)dist))))) {
                t.broadCast("A " + projectile.getName() + " comes flying through the air, hits " + whatishit + ", and shatters.");
                performer.getCommunicator().sendNormalServerMessage("The " + projectile.getName() + " shatters.");
            }
            else {
                t.broadCast("A " + projectile.getName() + " comes flying through the air and hits " + whatishit + ".");
            }
        }
        catch (NoSuchZoneException nsz) {
            performer.getCommunicator().sendNormalServerMessage("You hit nothing with the " + projectile.getName() + ".");
            Items.destroyItem(weapon.getData());
            return true;
        }
        return projectile.deleted;
    }
    
    private static final void alertGuards(final Creature performer, final int newx, final int newy, final boolean ok, final VolaTile t, final int destroyed) {
        try {
            if (!MethodsItems.mayTakeThingsFromStructure(performer, null, newx, newy) && !ok) {
                final Structure struct = t.getStructure();
                if (struct != null && struct.isFinished()) {
                    for (final VirtualZone vz : t.getWatchers()) {
                        try {
                            if (vz.getWatcher() != null && vz.getWatcher().getCurrentTile() != null && performer.isFriendlyKingdom(vz.getWatcher().getKingdomId())) {
                                boolean cares = false;
                                if (vz.getWatcher().isKingdomGuard()) {
                                    cares = true;
                                }
                                if (!cares) {
                                    cares = struct.isGuest(vz.getWatcher());
                                }
                                if (cares && (Math.abs(vz.getWatcher().getCurrentTile().tilex - newx) <= 20 || Math.abs(vz.getWatcher().getCurrentTile().tiley - newy) <= 20) && cares && performer.getStealSkill().skillCheck(95 - Math.min(Math.abs(vz.getWatcher().getCurrentTile().tilex - newx), Math.abs(vz.getWatcher().getCurrentTile().tiley - newy)) * 5, 0.0, true, 10.0f) < 0.0 && (!Servers.localServer.PVPSERVER || destroyed > 0)) {
                                    performer.setReputation(performer.getReputation() - 10);
                                    performer.getCommunicator().sendNormalServerMessage("People notice you. This is bad for your reputation!", (byte)2);
                                    break;
                                }
                            }
                        }
                        catch (Exception e) {
                            ServerProjectile.logger.log(Level.WARNING, e.getMessage(), e);
                        }
                    }
                }
            }
        }
        catch (NoSuchStructureException ex) {}
    }
    
    private static final boolean testHitCreaturesOnTile(final VolaTile t, final Creature performer, final Item projectile, final float damdealt, final float dist, final int floorlevel) {
        boolean hit = false;
        final Creature[] initialCreatures = t.getCreatures();
        final Set<Creature> creatureSet = new HashSet<Creature>();
        for (final Creature c : initialCreatures) {
            if (t.getBridgeParts().length > 0) {
                if (c.getBridgeId() == t.getBridgeParts()[0].getStructureId()) {
                    creatureSet.add(c);
                }
            }
            else if (c.getFloorLevel() == floorlevel) {
                creatureSet.add(c);
            }
        }
        final Creature[] creatures = creatureSet.toArray(new Creature[creatureSet.size()]);
        if (creatures.length > 0) {
            boolean nonpvp = false;
            final int x = Server.rand.nextInt(creatures.length);
            if (!creatures[x].isUnique() && !creatures[x].isInvulnerable() && creatures[x].getPower() == 0) {
                if (performer.isFriendlyKingdom(creatures[x].getKingdomId()) && (!creatures[x].isOnPvPServer() || !performer.isOnPvPServer()) && (performer.getCitizenVillage() == null || !performer.getCitizenVillage().isEnemy(creatures[x].getCitizenVillage()))) {
                    nonpvp = true;
                }
                if (!nonpvp) {
                    try {
                        hit = true;
                    }
                    catch (Exception ex) {
                        ServerProjectile.logger.log(Level.WARNING, creatures[x].getName() + ex.getMessage(), ex);
                    }
                }
            }
        }
        return hit;
    }
    
    private static final boolean hitCreaturesOnTile(final VolaTile t, final double power, final Creature performer, final Item projectile, final float damdealt, final float dist, final int floorlevel) {
        boolean hit = false;
        final Creature[] initialCreatures = t.getCreatures();
        final Set<Creature> creatureSet = new HashSet<Creature>();
        for (final Creature c : initialCreatures) {
            if (t.getBridgeParts().length > 0) {
                if (c.getBridgeId() == t.getBridgeParts()[0].getStructureId()) {
                    creatureSet.add(c);
                }
            }
            else if (c.getFloorLevel() == floorlevel) {
                creatureSet.add(c);
            }
        }
        final Creature[] creatures = creatureSet.toArray(new Creature[creatureSet.size()]);
        if (power > 0.0 && creatures.length > 0) {
            boolean nonpvp = false;
            final int x = Server.rand.nextInt(creatures.length);
            if (!creatures[x].isUnique() && !creatures[x].isInvulnerable() && creatures[x].getPower() == 0) {
                if (performer.isFriendlyKingdom(creatures[x].getKingdomId())) {
                    if ((performer.getCitizenVillage() == null || !performer.getCitizenVillage().isEnemy(creatures[x].getCitizenVillage())) && !performer.hasBeenAttackedBy(creatures[x].getWurmId()) && creatures[x].getCurrentKingdom() == creatures[x].getKingdomId()) {
                        performer.setUnmotivatedAttacker();
                    }
                    if (!creatures[x].isOnPvPServer() || !performer.isOnPvPServer()) {
                        if (performer.getCitizenVillage() == null || !performer.getCitizenVillage().isEnemy(creatures[x].getCitizenVillage())) {
                            nonpvp = true;
                        }
                    }
                    else if (Servers.localServer.HOMESERVER && !performer.isOnHostileHomeServer() && creatures[x].getReputation() >= 0 && (creatures[x].citizenVillage == null || !creatures[x].citizenVillage.isEnemy(performer))) {
                        performer.setReputation(performer.getReputation() - 30);
                        performer.getCommunicator().sendAlertServerMessage("This is bad for your reputation.");
                    }
                }
                if (!nonpvp) {
                    try {
                        creatures[x].getCommunicator().sendAlertServerMessage("You are hit by some " + projectile.getName() + " coming through the air!");
                        if (!creatures[x].isPlayer()) {
                            creatures[x].setTarget(performer.getWurmId(), false);
                            creatures[x].setFleeCounter(20);
                        }
                        if (damdealt > 0.0f) {
                            if (creatures[x].isPlayer()) {
                                final boolean dead = creatures[x].addWoundOfType(performer, (byte)0, 1, true, 1.0f, false, Math.min(25000.0f, damdealt * 1000.0f), 0.0f, 0.0f, false, false);
                                performer.achievement(47);
                                if (dead) {
                                    creatures[x].achievement(48);
                                }
                            }
                            else {
                                creatures[x].getBody().addWound(new TempWound((byte)0, creatures[x].getBody().getRandomWoundPos(), Math.min(25000.0f, damdealt * 1000.0f), creatures[x].getWurmId(), 0.0f, 0.0f, false));
                            }
                        }
                        hit = true;
                        performer.getCommunicator().sendNormalServerMessage("You hit " + creatures[x].getNameWithGenus() + "!");
                    }
                    catch (Exception ex) {
                        ServerProjectile.logger.log(Level.WARNING, creatures[x].getName() + ex.getMessage(), ex);
                    }
                }
            }
            else if (creatures[x].isVisible()) {
                performer.getCommunicator().sendNormalServerMessage(creatures[x].getNameWithGenus() + " dodges your " + projectile.getName() + " with no problem.");
            }
        }
        return hit;
    }
    
    public static final BlockingResult calculateBlocker(final Item weapon, final float estimatedEndPosX, final float estimatedEndPosY, final float estimatedPosZ) throws NoSuchZoneException {
        final Vector3f startPos = new Vector3f(weapon.getTileX() * 4 + 2, weapon.getTileY() * 4 + 2, weapon.getPosZ());
        final Vector3f currentPos = startPos.clone();
        final Vector3f targetPos = new Vector3f(estimatedEndPosX, estimatedEndPosY, estimatedPosZ);
        final Vector3f vector = targetPos.subtract(startPos);
        final float length = vector.length();
        final Vector3f dir = vector.normalize();
        final float totalTimeInAir = length / 12.0f;
        final float speed = 0.5f;
        float hVelocity = totalTimeInAir * 24.0f * 0.02f;
        hVelocity += dir.z * 0.5f;
        boolean hitSomething = false;
        final float stepLength = 2.0f;
        final float secondsPerStep = 0.16666667f;
        final float gravityPerStep = 0.16f;
        float lastGroundHeight = Zones.calculateHeight(currentPos.getX(), currentPos.getY(), true);
        BlockingResult toReturn = null;
        float timeMoved = 0.0f;
        while (!hitSomething) {
            timeMoved += 0.16666667f;
            final Vector3f lastPos = currentPos.clone();
            hVelocity -= 0.16f;
            final Vector3f vector3f = currentPos;
            vector3f.z += hVelocity;
            final Vector3f vector3f2 = currentPos;
            vector3f2.x += dir.x * 2.0f;
            final Vector3f vector3f3 = currentPos;
            vector3f3.y += dir.y * 2.0f;
            final float groundHeight = Zones.calculateHeight(currentPos.getX(), currentPos.getY(), true);
            toReturn = Blocking.getBlockerBetween(null, lastPos.getX(), lastPos.getY(), currentPos.getX(), currentPos.getY(), lastPos.getZ(), currentPos.z, true, true, true, 4, -10L, -10L, -10L, false);
            if (currentPos.getZ() < groundHeight - 1.0f) {
                toReturn = new BlockingResult();
                toReturn.addBlocker(new PathTile((int)currentPos.getX() / 4, (int)currentPos.getY() / 4, Server.surfaceMesh.getTile((int)currentPos.getX() / 4, (int)currentPos.getY() / 4), true, 0), currentPos, 100.0f);
                ServerProjectile.logger.log(Level.INFO, "Hit ground at " + (int)(currentPos.getX() / 4.0f) + "," + (int)(currentPos.getY() / 4.0f) + " height was " + groundHeight + ", compared to " + currentPos.getZ());
                toReturn.setEstimatedBlockingTime(totalTimeInAir);
                toReturn.setActualBlockingTime(timeMoved);
                return toReturn;
            }
            if (toReturn != null) {
                toReturn.setEstimatedBlockingTime(totalTimeInAir);
                toReturn.setActualBlockingTime(timeMoved);
                hitSomething = true;
            }
            lastGroundHeight = groundHeight;
        }
        return toReturn;
    }
    
    public static final float getProjectileDistance(final Vector3f startingPosition, final float heightOffset, final float power, final float rotation, final float firingAngle) {
        final Vector3f startingVelocity = new Vector3f((float)(power * Math.cos(rotation) * Math.cos(firingAngle)), (float)(power * Math.sin(rotation) * Math.cos(firingAngle)), (float)(power * Math.sin(firingAngle)));
        final float offsetModifier = heightOffset / (startingVelocity.z / -9.8f * startingVelocity.z);
        final float flightTime = startingVelocity.z * (2.0f - offsetModifier) / -9.8f;
        startingVelocity.z = 0.0f;
        final Vector3f endingPosition = startingPosition.add(startingVelocity.mult(flightTime));
        return endingPosition.distance(startingPosition.add(0.0f, 0.0f, heightOffset));
    }
    
    public static final ProjectileInfo getProjectileInfo(final Item weapon, final Creature cret, final float averageFiringAngle, final float firingAngleVariationMax) throws NoSuchZoneException {
        final Vector3f landingPosition = new Vector3f();
        int power = weapon.getWinches();
        final float angleVar = 1.0f - Math.abs(averageFiringAngle - 45.0f) / 45.0f;
        float tiltAngle = averageFiringAngle - firingAngleVariationMax * angleVar;
        final float rotation = (float)((weapon.getRotation() - 90.0f) * 3.141592653589793 / 180.0);
        Skill firingSkill = null;
        int skillType = 10077;
        if (weapon.getTemplateId() == 936) {
            skillType = 10093;
            power = Math.min(40, power);
            power *= (int)1.5f;
        }
        else if (weapon.getTemplateId() == 937) {
            skillType = 10094;
            power = Math.min(50, power);
        }
        else {
            power = Math.min(30, power);
        }
        try {
            firingSkill = cret.getSkills().getSkill(skillType);
        }
        catch (NoSuchSkillException nss) {
            firingSkill = cret.getSkills().learn(skillType, 1.0f);
        }
        final double skillModifier = firingSkill.skillCheck(power, 0.0, true, 1.0f) / 100.0;
        final float qlModifier = (100.0f - weapon.getCurrentQualityLevel()) / 100.0f;
        tiltAngle += (float)(firingAngleVariationMax * angleVar * qlModifier * skillModifier);
        if (skillModifier < 0.0) {
            tiltAngle /= 3.0f;
            power /= 2;
            cret.getCommunicator().sendNormalServerMessage("Something goes wrong when you fire the " + weapon.getName() + " and it doesn't fire as far as you expected.");
        }
        tiltAngle *= 0.017453292519943295;
        final Vector3f currentVelocity = new Vector3f((float)(power * Math.cos(rotation) * Math.cos(tiltAngle)), (float)(power * Math.sin(rotation) * Math.cos(tiltAngle)), (float)(power * Math.sin(tiltAngle)));
        final Vector3f pos3f;
        Vector3f currentPos = pos3f = weapon.getPos3f();
        pos3f.z += weapon.getTemplate().getSizeY() * 0.75f / 100.0f;
        final Vector3f startingPosition = currentPos.clone();
        final Vector3f startingVelocity = currentVelocity.clone();
        Vector3f nextPos = null;
        BlockingResult blocker = null;
        final float stepAmount = 2.0f / currentVelocity.length();
        final float gravityPerStep = -9.8f * stepAmount;
        long flightTime = 0L;
        boolean landed = false;
        while (!landed) {
            flightTime += (long)(stepAmount * 1000.0f);
            nextPos = currentPos.add(currentVelocity.mult(stepAmount));
            blocker = Blocking.getBlockerBetween(null, currentPos.getX(), currentPos.getY(), nextPos.getX(), nextPos.getY(), currentPos.getZ() - 0.5f, nextPos.getZ() - 0.5f, weapon.isOnSurface(), weapon.isOnSurface(), true, 4, -10L, weapon.getBridgeId(), -10L, false);
            if (blocker != null) {
                landingPosition.set(blocker.getFirstIntersection());
                landed = true;
            }
            else {
                final float groundHeight = Zones.calculateHeight(nextPos.getX(), nextPos.getY(), weapon.isOnSurface());
                if (nextPos.getZ() <= groundHeight) {
                    landingPosition.set(nextPos.getX(), nextPos.getY(), groundHeight);
                    landed = true;
                }
            }
            final Vector3f vector3f = currentVelocity;
            vector3f.z += gravityPerStep;
            currentPos = nextPos;
        }
        final ProjectileInfo toReturn = new ProjectileInfo(startingPosition, startingVelocity, landingPosition, currentVelocity, flightTime);
        return toReturn;
    }
    
    private final boolean poll(final long now) {
        if (Features.Feature.NEW_PROJECTILES.isEnabled() && this.weapon.getTemplateId() != 936) {
            if (now > this.timeAtLanding) {
                final float majorRadius = (this.getProjectile().getSizeX(true) + this.getProjectile().getSizeY(true)) / 2.0f / 10.0f;
                final float damageMultiplier = this.projectileInfo.endVelocity.length() / 30.0f * (this.weapon.getCurrentQualityLevel() / 300.0f + 0.33f) * (this.getProjectile().getWeightGrams() / 20000.0f);
                float damage = 1.0f * damageMultiplier;
                if (this.getProjectile().isStone() || this.getProjectile().isMetal()) {
                    damage *= 10.0f;
                }
                else if (this.getProjectile().isCorpse()) {
                    damage *= 2.5f;
                }
                if (this.getProjectile().getTemplateId() == 298 || this.getProjectile().getTemplateId() == 26 || this.getProjectile().isEgg()) {
                    damage /= 15.0f;
                }
                final float extraDamage = (damage - 20.0f) / 4.0f;
                final float minorRadius = 1.0f + extraDamage / 10.0f;
                damage = Math.min(20.0f, damage);
                final float radius = majorRadius * minorRadius;
                int hitCounter = 0;
                int wallCount = 0;
                int fenceCount = 0;
                int floorCount = 0;
                int roofCount = 0;
                int bridgeCount = 0;
                int itemCount = 0;
                ArrayList<Item> itemHitList = null;
                ArrayList<Structure> structureHitList = null;
                for (int i = (int)((this.projectileInfo.endPosition.x - radius) / 4.0f); i <= (int)((this.projectileInfo.endPosition.x + radius) / 4.0f); ++i) {
                    for (int j = (int)((this.projectileInfo.endPosition.y - radius) / 4.0f); j <= (int)((this.projectileInfo.endPosition.y + radius) / 4.0f); ++j) {
                        final VolaTile tileInRadius = Zones.getOrCreateTile(i, j, this.weapon.isOnSurface());
                        if (tileInRadius != null) {
                            for (final Creature c : tileInRadius.getCreatures()) {
                                Label_0971: {
                                    if (!c.isUnique() && !c.isInvulnerable()) {
                                        if (c.getPower() <= 0) {
                                            if (!c.isOnPvPServer()) {
                                                if (c.isHitched() || c.isCaredFor()) {
                                                    break Label_0971;
                                                }
                                                if (c.isBranded() && !c.mayManage(this.shooter)) {
                                                    break Label_0971;
                                                }
                                            }
                                            final float distance = c.getPos3f().distance(this.projectileInfo.endPosition);
                                            if (distance <= radius) {
                                                if (c.isPlayer() && this.shooter.isFriendlyKingdom(c.getKingdomId()) && c != this.shooter) {
                                                    if (this.shooter.getCitizenVillage() == null || !this.shooter.getCitizenVillage().isEnemy(c.getCitizenVillage())) {
                                                        if (!this.shooter.hasBeenAttackedBy(c.getWurmId()) && c.getCurrentKingdom() == c.getKingdomId()) {
                                                            this.shooter.setUnmotivatedAttacker();
                                                        }
                                                        if (!this.shooter.isOnPvPServer()) {
                                                            break Label_0971;
                                                        }
                                                        if (!c.isOnPvPServer()) {
                                                            break Label_0971;
                                                        }
                                                    }
                                                    if (Servers.localServer.HOMESERVER && !this.shooter.isOnHostileHomeServer() && c.getReputation() >= 0 && (c.citizenVillage == null || !c.citizenVillage.isEnemy(this.shooter))) {
                                                        this.shooter.setReputation(this.shooter.getReputation() - 30);
                                                        this.shooter.getCommunicator().sendAlertServerMessage("This is bad for your reputation.");
                                                    }
                                                }
                                                if (damage > 0.0f) {
                                                    c.getCommunicator().sendAlertServerMessage("You are hit by " + this.projectile.getName() + " coming through the air!");
                                                    if (!c.isPlayer()) {
                                                        c.setTarget(this.shooter.getWurmId(), false);
                                                        c.setFleeCounter(20);
                                                    }
                                                    this.shooter.getCommunicator().sendNormalServerMessage("You hit " + c.getNameWithGenus() + "!");
                                                    final float actualDam = (distance > majorRadius) ? Math.min(10.0f, extraDamage) : damage;
                                                    if (c.isPlayer()) {
                                                        final boolean dead = c.addWoundOfType(this.shooter, (byte)0, 1, true, 1.0f, true, Math.min(25000.0f, actualDam * 1000.0f), 0.0f, 0.0f, false, false);
                                                        this.shooter.achievement(47);
                                                        if (dead) {
                                                            c.achievement(48);
                                                            if (this.weapon.getTemplateId() == 445) {
                                                                this.shooter.achievement(573);
                                                            }
                                                        }
                                                    }
                                                    else {
                                                        final boolean dead = c.addWoundOfType(this.shooter, (byte)0, 1, true, 1.0f, true, Math.min(25000.0f, actualDam * 1000.0f), 0.0f, 0.0f, false, false);
                                                        if (dead && this.weapon.getTemplateId() == 445) {
                                                            this.shooter.achievement(573);
                                                        }
                                                    }
                                                }
                                                ++hitCounter;
                                            }
                                        }
                                    }
                                }
                            }
                            if (Servers.localServer.PVPSERVER || tileInRadius.getStructure() == null || tileInRadius.getStructure().isActionAllowed(this.shooter, (short)174)) {
                                for (final Wall w : tileInRadius.getWalls()) {
                                    if (!w.isWallPlan()) {
                                        if (Math.abs(w.getCenterPoint().z - this.projectileInfo.endPosition.z) <= 1.5f) {
                                            final float distance = w.isHorizontal() ? Math.abs(w.getCenterPoint().y - this.projectileInfo.endPosition.y) : Math.abs(w.getCenterPoint().x - this.projectileInfo.endPosition.x);
                                            final float actualDam = (distance > majorRadius) ? Math.min(10.0f, extraDamage) : damage;
                                            if (distance <= radius) {
                                                ++wallCount;
                                                ++hitCounter;
                                                if (Servers.localServer.testServer) {
                                                    this.shooter.getCommunicator().sendSafeServerMessage(w.getName() + " hit for " + w.getDamageModifier() * actualDam);
                                                }
                                                w.setDamage(w.getDamage() + w.getDamageModifier() * actualDam);
                                                try {
                                                    if (structureHitList == null) {
                                                        structureHitList = new ArrayList<Structure>();
                                                    }
                                                    if (!structureHitList.contains(Structures.getStructure(w.getStructureId()))) {
                                                        structureHitList.add(Structures.getStructure(w.getStructureId()));
                                                    }
                                                }
                                                catch (NoSuchStructureException ex) {}
                                            }
                                        }
                                    }
                                }
                                for (final Fence f : tileInRadius.getFences()) {
                                    if (Math.abs(f.getCenterPoint().z - this.projectileInfo.endPosition.z) <= 1.5f) {
                                        final float distance = f.isHorizontal() ? Math.abs(f.getCenterPoint().y - this.projectileInfo.endPosition.y) : Math.abs(f.getCenterPoint().x - this.projectileInfo.endPosition.x);
                                        final float actualDam = (distance > majorRadius) ? Math.min(10.0f, extraDamage) : damage;
                                        if (distance <= radius) {
                                            ++fenceCount;
                                            ++hitCounter;
                                            if (Servers.localServer.testServer) {
                                                this.shooter.getCommunicator().sendSafeServerMessage(f.getName() + " hit for " + f.getDamageModifier() * actualDam);
                                            }
                                            f.setDamage(f.getDamage() + f.getDamageModifier() * actualDam);
                                        }
                                    }
                                }
                                for (final Floor f2 : tileInRadius.getFloors()) {
                                    if (!f2.isAPlan()) {
                                        final float distance = Math.abs(f2.getCenterPoint().z - this.projectileInfo.endPosition.z);
                                        final float actualDam = (distance > majorRadius) ? Math.min(10.0f, extraDamage) : damage;
                                        if (distance <= radius) {
                                            if (f2.isRoof()) {
                                                ++roofCount;
                                            }
                                            else {
                                                ++floorCount;
                                            }
                                            ++hitCounter;
                                            if (Servers.localServer.testServer) {
                                                this.shooter.getCommunicator().sendSafeServerMessage(f2.getName() + " hit for " + f2.getDamageModifier() * actualDam);
                                            }
                                            f2.setDamage(f2.getDamage() + f2.getDamageModifier() * actualDam);
                                            try {
                                                if (structureHitList == null) {
                                                    structureHitList = new ArrayList<Structure>();
                                                }
                                                if (!structureHitList.contains(Structures.getStructure(f2.getStructureId()))) {
                                                    structureHitList.add(Structures.getStructure(f2.getStructureId()));
                                                }
                                            }
                                            catch (NoSuchStructureException ex2) {}
                                        }
                                    }
                                }
                                for (final BridgePart bp : tileInRadius.getBridgeParts()) {
                                    if (!bp.isAPlan()) {
                                        if (bp.getCenterPoint().distance(this.projectileInfo.endPosition) <= 4.0f) {
                                            ++bridgeCount;
                                            ++hitCounter;
                                            bp.setDamage(bp.getDamage() + bp.getDamageModifier() * damage);
                                        }
                                    }
                                }
                                for (final Item it : tileInRadius.getItems()) {
                                    Label_2124: {
                                        if (!it.isIndestructible()) {
                                            if (!it.isRoadMarker()) {
                                                if (!it.isLocked()) {
                                                    if (!it.isVehicle()) {
                                                        if (it.isOwnerDestroyable() && it.lastOwner != this.shooter.getWurmId() && !this.shooter.isOnPvPServer()) {
                                                            final Village village = tileInRadius.getVillage();
                                                            if (village == null) {
                                                                break Label_2124;
                                                            }
                                                            if (!village.isActionAllowed((short)83, this.shooter)) {
                                                                break Label_2124;
                                                            }
                                                        }
                                                        if (it.getZoneId() != -10L && it.isKingdomMarker() && it.getKingdom() == this.shooter.getKingdomId() && this.shooter.getWurmId() != it.lastOwner) {
                                                            if (tileInRadius.getVillage() == null) {
                                                                break Label_2124;
                                                            }
                                                            if (tileInRadius.getVillage() != this.shooter.getCitizenVillage()) {
                                                                break Label_2124;
                                                            }
                                                        }
                                                        if (it.getPos3f().distance(this.projectileInfo.endPosition) <= radius) {
                                                            ++itemCount;
                                                            if (itemHitList == null) {
                                                                itemHitList = new ArrayList<Item>();
                                                            }
                                                            itemHitList.add(it);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (itemHitList != null) {
                    ++hitCounter;
                    final Item t = itemHitList.get(Server.rand.nextInt(itemHitList.size()));
                    final float actualDam2 = (t.getPos3f().distance(this.projectileInfo.endPosition) > majorRadius) ? Math.min(10.0f, extraDamage) : damage;
                    t.setDamage(t.getDamage() + t.getDamageModifier() * actualDam2 / 25.0f);
                }
                if (hitCounter == 0) {
                    this.shooter.getCommunicator().sendNormalServerMessage("It doesn't sound like the " + this.getProjectile().getName() + " hit anything.");
                }
                else {
                    final StringBuilder targetList = new StringBuilder();
                    targetList.append("It sounds as though the " + this.getProjectile().getName() + " hit ");
                    if (wallCount > 0) {
                        targetList.append(((wallCount > 1) ? wallCount : "a") + " wall" + ((wallCount > 1) ? "s" : "") + ", ");
                    }
                    if (fenceCount > 0) {
                        targetList.append(((fenceCount > 1) ? fenceCount : "a") + " fence" + ((fenceCount > 1) ? "s" : "") + ", ");
                    }
                    if (roofCount > 0) {
                        targetList.append(((roofCount > 1) ? roofCount : "a") + " roof" + ((roofCount > 1) ? "s" : "") + ", ");
                    }
                    if (floorCount > 0) {
                        targetList.append(((floorCount > 1) ? floorCount : "a") + " floor" + ((floorCount > 1) ? "s" : "") + ", ");
                    }
                    if (bridgeCount > 0) {
                        targetList.append(((bridgeCount > 1) ? bridgeCount : "a") + " bridge part" + ((bridgeCount > 1) ? "s" : "") + ", ");
                    }
                    if (itemCount > 0) {
                        targetList.append("an item, ");
                    }
                    targetList.append("and nothing else.");
                    this.shooter.getCommunicator().sendNormalServerMessage(targetList.toString());
                    if (structureHitList != null && !structureHitList.isEmpty()) {
                        for (final Structure struct : structureHitList) {
                            if (struct.isDestroyed()) {
                                struct.totallyDestroy();
                                if (this.weapon.getTemplateId() != 445) {
                                    continue;
                                }
                                this.shooter.achievement(51);
                            }
                        }
                        targetList.delete(0, targetList.length());
                        targetList.append("You managed to hit ");
                        for (int id = 0; id < structureHitList.size(); ++id) {
                            final boolean hasMore = id < structureHitList.size() - 1;
                            targetList.append(((hasMore && id > 0) ? "" : ((id == 0) ? "" : "and ")) + structureHitList.get(id).getName());
                            if (hasMore && id + 1 < structureHitList.size() - 1) {
                                targetList.append(", ");
                            }
                            else if (hasMore) {
                                targetList.append(" ");
                            }
                            else {
                                targetList.append(".");
                            }
                        }
                        this.shooter.getCommunicator().sendNormalServerMessage(targetList.toString());
                    }
                }
                final boolean projDestroyed = this.getProjectile().setDamage(this.getProjectile().getDamage() + damage);
                if (!projDestroyed) {
                    try {
                        this.getProjectile().setPosXYZ(this.projectileInfo.endPosition.x, this.projectileInfo.endPosition.y, this.projectileInfo.endPosition.z);
                        final Zone z = Zones.getZone(this.getProjectile().getTileX(), this.getProjectile().getTileY(), this.weapon.isOnSurface());
                        z.addItem(this.getProjectile());
                    }
                    catch (NoSuchZoneException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    this.shooter.getCommunicator().sendNormalServerMessage("The " + this.getProjectile().getName() + " crumbles to dust as it lands.");
                }
                if (Servers.localServer.testServer) {
                    this.shooter.getCommunicator().sendNormalServerMessage("[TEST] Projectile " + this.getProjectile().getName() + " landed, damage multiplier: " + damageMultiplier + ", damage: " + damage + " total things hit: " + hitCounter + ". Total distance: " + this.projectileInfo.startPosition.distance(this.projectileInfo.endPosition) + "m or " + this.projectileInfo.startPosition.distance(this.projectileInfo.endPosition) / 4.0f + " tiles.");
                }
                return true;
            }
            if (this.timeAtLanding - now <= 500L && !this.sentEffect) {
                EffectFactory.getInstance().createGenericTempEffect("dust03", this.projectileInfo.endPosition.x, this.projectileInfo.endPosition.y, this.projectileInfo.endPosition.z, this.weapon.isOnSurface(), -1.0f, 0.0f);
                this.sentEffect = true;
            }
            return false;
        }
        else {
            if (now > this.timeAtLanding) {
                float newx = this.getPosDownX();
                float newy = this.getPosDownY();
                if (this.result != null && this.result.getFirstBlocker() != null) {
                    newx = this.result.getFirstBlocker().getTileX() * 4 + 2;
                    newy = this.result.getFirstBlocker().getTileY() * 4 + 2;
                }
                Skill cataskill = null;
                int skilltype = 10077;
                if (this.weapon.getTemplateId() == 936) {
                    skilltype = 10093;
                }
                if (this.weapon.getTemplateId() == 937) {
                    skilltype = 10094;
                }
                try {
                    cataskill = this.getShooter().getSkills().getSkill(skilltype);
                }
                catch (NoSuchSkillException nss) {
                    cataskill = this.getShooter().getSkills().learn(skilltype, 1.0f);
                }
                final Vector2f targPos = new Vector2f(this.weapon.getPosX(), this.weapon.getPosY());
                final Vector2f projPos = new Vector2f(newx, newy);
                final float dist = Math.abs(projPos.subtract(targPos).length() / 4.0f);
                double power = 0.0;
                final VolaTile droptile = Zones.getOrCreateTile((int)(newx / 4.0f), (int)(newy / 4.0f), true);
                int dropFloorLevel = 0;
                boolean hit = false;
                boolean itemDestroyed = false;
                try {
                    final Item k = this.getProjectile();
                    if (this.result != null) {
                        if (this.result.getFirstBlocker() != null) {
                            dropFloorLevel = droptile.getDropFloorLevel(this.result.getFirstBlocker().getFloorLevel());
                            if (this.result.getFirstBlocker().isTile()) {
                                itemDestroyed = setEffects(this.getWeapon(), k, (int)(newx / 4.0f), (int)(newy / 4.0f), dist, this.result.getFirstBlocker().getFloorLevel(), this.getShooter(), this.getRarity(), this.getDamageDealt());
                            }
                            else {
                                boolean hadSkillGainChance = false;
                                boolean messageSent = false;
                                String whatishit = "the " + this.result.getFirstBlocker().getName();
                                final Village vill = MethodsStructure.getVillageForBlocker(this.result.getFirstBlocker());
                                final VolaTile t2 = Zones.getOrCreateTile(this.result.getFirstBlocker().getTileX(), this.result.getFirstBlocker().getTileY(), true);
                                boolean ok = isOkToAttack(t2, this.getShooter(), this.getDamageDealt());
                                if (!ok && vill != null) {
                                    if (vill.isActionAllowed((short)174, this.getShooter(), false, 0, 0)) {
                                        ok = true;
                                    }
                                    else if (!vill.isEnemy(this.getShooter())) {
                                        ok = false;
                                    }
                                }
                                if (ok) {
                                    power = cataskill.skillCheck(dist - 9.0, this.weapon, 0.0, false, 10.0f);
                                    hadSkillGainChance = true;
                                    hit = (power > 0.0);
                                    if (hit && this.getDamageDealt() > 0.0f) {
                                        final int fl = this.result.getFirstBlocker().getFloorLevel();
                                        final float mod = 1.0f;
                                        final float newDam = this.result.getFirstBlocker().getDamage() + Math.min(20.0f, this.result.getFirstBlocker().getDamageModifier() * this.getDamageDealt() / 1.0f);
                                        whatishit = this.result.getFirstBlocker().getName();
                                        this.getShooter().getCommunicator().sendNormalServerMessage("You seem to have hit " + whatishit + "!" + (Servers.isThisATestServer() ? (" Dam:" + this.getDamageDealt() + " NewDam:" + newDam) : ""));
                                        if (!this.result.getFirstBlocker().isFloor()) {
                                            t2.damageFloors(fl, fl + 1, Math.min(20.0f, this.result.getFirstBlocker().getDamageModifier() * this.getDamageDealt()));
                                            if (this.result.getFirstBlocker().isHorizontal()) {
                                                final VolaTile t3 = Zones.getTileOrNull(this.result.getFirstBlocker().getTileX() - 1, this.result.getFirstBlocker().getTileY(), true);
                                                if (t3 != null) {
                                                    t3.damageFloors(fl, fl + 1, Math.min(20.0f, this.result.getFirstBlocker().getDamageModifier() * this.getDamageDealt()));
                                                }
                                            }
                                        }
                                        this.result.getFirstBlocker().setDamage(newDam);
                                        if (newDam >= 100.0f) {
                                            TileEvent.log(this.result.getFirstBlocker().getTileX(), this.result.getFirstBlocker().getTileY(), 0, this.getShooter().getWurmId(), 236);
                                            if (this.result.getFirstBlocker().isFloor()) {
                                                t2.removeFloor(this.result.getFirstBlocker());
                                            }
                                        }
                                    }
                                    itemDestroyed = this.projectile.setDamage(this.projectile.getDamage() + this.projectile.getDamageModifier() * (20 + Server.rand.nextInt(Math.max(1, (int)dist))));
                                    if (itemDestroyed) {
                                        t2.broadCast("A " + this.projectile.getName() + " comes flying through the air, hits " + whatishit + ", and shatters.");
                                    }
                                    else {
                                        t2.broadCast("A " + this.projectile.getName() + " comes flying through the air and hits " + whatishit + ".");
                                    }
                                }
                                else {
                                    this.getShooter().getCommunicator().sendNormalServerMessage("You seem to miss with the " + this.projectile.getName() + ".");
                                    messageSent = true;
                                }
                                if (testHitCreaturesOnTile(droptile, this.getShooter(), k, this.getDamageDealt(), dist, dropFloorLevel)) {
                                    if (!hadSkillGainChance) {
                                        power = cataskill.skillCheck(dist - 9.0, this.weapon, 0.0, false, 10.0f);
                                    }
                                    hit = hitCreaturesOnTile(droptile, power, this.getShooter(), k, this.getDamageDealt(), dist, dropFloorLevel);
                                }
                                if (!hit && !messageSent) {
                                    this.getShooter().getCommunicator().sendNormalServerMessage("You just missed with the " + this.projectile.getName() + ".");
                                }
                            }
                        }
                        if (!itemDestroyed) {
                            k.setPosXYZ(newx, newy, Zones.calculateHeight(newx, newy, this.result.getFirstBlocker().getFloorLevel() >= 0) + Math.max(0, dropFloorLevel) * 3);
                            final VolaTile vt = Zones.getOrCreateTile((int)(newx / 4.0f), (int)(newy / 4.0f), this.weapon.isOnSurface());
                            if (vt.getBridgeParts().length > 0) {
                                k.setOnBridge(vt.getBridgeParts()[0].getStructureId());
                            }
                            final Zone z2 = Zones.getZone(k.getTileX(), k.getTileY(), this.result.getFirstBlocker().getFloorLevel() >= 0);
                            z2.addItem(k);
                            ServerProjectile.logger.log(Level.INFO, "Adding " + k.getName() + " at " + (int)(newx / 4.0f) + "," + (int)(newy / 4.0f));
                        }
                    }
                    else if (!setEffects(this.getWeapon(), k, (int)(this.getPosDownX() / 4.0f), (int)(this.getPosDownY() / 4.0f), dist, 0, this.getShooter(), this.getRarity(), this.getDamageDealt())) {
                        final VolaTile vt = Zones.getOrCreateTile((int)(newx / 4.0f), (int)(newy / 4.0f), this.weapon.isOnSurface());
                        float newz = 0.0f;
                        if (vt.getBridgeParts().length > 0) {
                            k.setOnBridge(-10L);
                        }
                        else {
                            newz = Zones.calculateHeight(this.getPosDownX(), this.getPosDownY(), false);
                        }
                        k.setPosXYZ(this.getPosDownX(), this.getPosDownY(), newz);
                        final Zone z = Zones.getZone(k.getTileX(), k.getTileY(), true);
                        z.addItem(k);
                        ServerProjectile.logger.log(Level.INFO, "Adding " + k.getName() + " at " + (int)(this.getPosDownX() / 4.0f) + "," + (int)(this.getPosDownY() / 4.0f));
                    }
                }
                catch (NoSuchZoneException nsz) {
                    ServerProjectile.logger.log(Level.INFO, this.getProjectile().getModelName() + " projectile with id " + this.getProjectile().getWurmId() + " shot outside the map");
                }
                return true;
            }
            return false;
        }
    }
    
    public static void pollAll() {
        final long now = System.currentTimeMillis();
        for (final ServerProjectile projectile : ServerProjectile.projectiles) {
            if (projectile.poll(now)) {
                ServerProjectile.projectiles.remove(projectile);
            }
        }
    }
    
    public static final void removeProjectile(final ServerProjectile projectile) {
        ServerProjectile.projectiles.remove(projectile);
    }
    
    public float getPosDownX() {
        return this.posDownX;
    }
    
    public float getPosDownY() {
        return this.posDownY;
    }
    
    public float getCurrentSecondsInAir() {
        return this.currentSecondsInAir;
    }
    
    public void setCurrentSecondsInAir(final float aCurrentSecondsInAir) {
        this.currentSecondsInAir = aCurrentSecondsInAir;
    }
    
    public Item getWeapon() {
        return this.weapon;
    }
    
    public byte getRarity() {
        return this.rarity;
    }
    
    public BlockingResult getResult() {
        return this.result;
    }
    
    public void setResult(final BlockingResult aResult) {
        this.result = aResult;
    }
    
    public Item getProjectile() {
        return this.projectile;
    }
    
    public float getDamageDealt() {
        return this.damageDealth;
    }
    
    public void setDamageDealt(final float aDamageDealth) {
        this.damageDealth = aDamageDealth;
    }
    
    public Creature getShooter() {
        return this.shooter;
    }
    
    static {
        logger = Logger.getLogger(ServerProjectile.class.getName());
        projectiles = new CopyOnWriteArraySet<ServerProjectile>();
    }
    
    static class ProjectileInfo
    {
        public final Vector3f startPosition;
        public final Vector3f startVelocity;
        public final Vector3f endPosition;
        public final Vector3f endVelocity;
        public final long timeToImpact;
        
        ProjectileInfo(final Vector3f startPosition, final Vector3f startVelocity, final Vector3f endPosition, final Vector3f endVelocity, final long timeToImpact) {
            this.startPosition = startPosition.clone();
            this.startVelocity = startVelocity.clone();
            this.endPosition = endPosition.clone();
            this.endVelocity = endVelocity.clone();
            this.timeToImpact = timeToImpact;
        }
    }
}
