// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.ItemTemplateFactory;
import java.util.Iterator;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.epic.EpicMission;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.epic.Hota;
import com.wurmonline.server.Servers;
import com.wurmonline.server.Message;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.epic.EpicServerStatus;
import com.wurmonline.server.Players;
import com.wurmonline.server.creatures.ai.PathTile;
import com.wurmonline.server.items.CreationEntry;
import com.wurmonline.server.tutorial.MissionTrigger;
import com.wurmonline.server.Items;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.CreationMatrix;
import com.wurmonline.server.tutorial.MissionTriggers;
import com.wurmonline.server.zones.FocusZone;
import com.wurmonline.server.creatures.ai.NoPathException;
import com.wurmonline.server.creatures.ai.Path;
import javax.annotation.Nullable;
import com.wurmonline.server.creatures.ai.PathFinder;
import com.wurmonline.server.zones.VolaTile;
import java.util.List;
import com.wurmonline.server.behaviours.Behaviour;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.items.Item;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.BehaviourDispatcher;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.ai.ChatManager;
import java.util.Random;

public class Npc extends Creature
{
    static final Random faceRandom;
    int lastX;
    int lastY;
    final ChatManager chatManager;
    LongTarget longTarget;
    int longTargetAttempts;
    int passiveCounter;
    int MAXSEED;
    
    public Npc() throws Exception {
        this.lastX = 0;
        this.lastY = 0;
        this.longTargetAttempts = 0;
        this.passiveCounter = 0;
        this.MAXSEED = 100;
        this.chatManager = new ChatManager(this);
    }
    
    public Npc(final CreatureTemplate aTemplate) throws Exception {
        super(aTemplate);
        this.lastX = 0;
        this.lastY = 0;
        this.longTargetAttempts = 0;
        this.passiveCounter = 0;
        this.MAXSEED = 100;
        this.chatManager = new ChatManager(this);
    }
    
    public Npc(final long aId) throws Exception {
        super(aId);
        this.lastX = 0;
        this.lastY = 0;
        this.longTargetAttempts = 0;
        this.passiveCounter = 0;
        this.MAXSEED = 100;
        this.chatManager = new ChatManager(this);
    }
    
    public final ChatManager getChatManager() {
        return this.chatManager;
    }
    
    @Override
    public final byte getKingdomId() {
        if (this.isAggHuman()) {
            return 0;
        }
        return this.status.kingdom;
    }
    
    @Override
    public final boolean isAggHuman() {
        return this.status.modtype == 2;
    }
    
    @Override
    public final void pollNPCChat() {
        this.getChatManager().checkChats();
    }
    
    @Override
    public final void pollNPC() {
        this.checkItemSpawn();
        if (this.passiveCounter-- == 0) {
            this.doSomething();
        }
    }
    
    private final void doSomething() {
        if (!this.isFighting() && this.target == -10L) {
            if (!this.capturePillar()) {
                if (!this.performLongTargetAction()) {
                    if (this.getStatus().getPath() == null && Server.rand.nextBoolean()) {
                        this.startPathing(0);
                        this.setPassiveCounter(120);
                    }
                    else {
                        final int seed = Server.rand.nextInt(this.MAXSEED);
                        if (seed < 10) {
                            if (this.getStatus().damage > 0) {
                                final Wound[] wounds = this.getBody().getWounds().getWounds();
                                if (wounds.length > 0) {
                                    final Wound rand = wounds[Server.rand.nextInt(wounds.length)];
                                    if (Server.rand.nextBoolean()) {
                                        rand.setBandaged(true);
                                        if (Server.rand.nextBoolean()) {
                                            rand.setHealeff((byte)(Server.rand.nextInt(70) + 30));
                                        }
                                    }
                                    else {
                                        rand.heal();
                                    }
                                }
                            }
                            this.setPassiveCounter(30);
                        }
                        if (seed < 20) {
                            final Item[] allItems = this.getInventory().getAllItems(false);
                            if (allItems.length > 0) {
                                final Item rand2 = allItems[Server.rand.nextInt(allItems.length)];
                                try {
                                    if (rand2.isFood()) {
                                        BehaviourDispatcher.action(this, this.communicator, -10L, rand2.getWurmId(), (short)182);
                                    }
                                    else if (rand2.isLiquid()) {
                                        BehaviourDispatcher.action(this, this.communicator, -10L, rand2.getWurmId(), (short)183);
                                    }
                                    else {
                                        BehaviourDispatcher.action(this, this.communicator, -10L, rand2.getWurmId(), (short)118);
                                    }
                                }
                                catch (Exception ex) {}
                            }
                            this.setPassiveCounter(30);
                        }
                        if (seed < 30) {
                            if (this.getCurrentTile() != null) {
                                try {
                                    final Item[] allItems = this.getInventory().getAllItems(false);
                                    final Item[] groundItems = this.getCurrentTile().getItems();
                                    Item rand3 = null;
                                    if (allItems.length > 0) {
                                        rand3 = allItems[Server.rand.nextInt(allItems.length)];
                                    }
                                    if (groundItems.length > 0) {
                                        final Item targ = groundItems[Server.rand.nextInt(groundItems.length)];
                                        if (Server.rand.nextBoolean() && this.getCurrentTile().getVillage() == null) {
                                            if (Server.rand.nextInt(4) == 0 && targ != null && targ.isHollow() && targ.testInsertItem(rand3)) {
                                                targ.insertItem(rand3);
                                            }
                                            else if (this.canCarry(targ.getWeightGrams())) {
                                                BehaviourDispatcher.action(this, this.communicator, -10L, targ.getWurmId(), (short)6);
                                            }
                                            else if (targ.isHollow()) {
                                                final Item[] containeds = targ.getAllItems(false);
                                                if (containeds.length > 0) {
                                                    final Item targ2 = containeds[Server.rand.nextInt(containeds.length)];
                                                    if (!targ2.isBodyPart() && !targ2.isNoTake()) {
                                                        BehaviourDispatcher.action(this, this.communicator, -10L, targ2.getWurmId(), (short)6);
                                                        this.wearItems();
                                                    }
                                                }
                                            }
                                        }
                                        else {
                                            BehaviourDispatcher.action(this, this.communicator, -10L, targ.getWurmId(), (short)162);
                                        }
                                    }
                                    else {
                                        BehaviourDispatcher.action(this, this.communicator, -10L, rand3.getWurmId(), (short)162);
                                    }
                                    this.setPassiveCounter(30);
                                }
                                catch (Exception ex2) {}
                            }
                            this.setPassiveCounter(10);
                        }
                        if (seed < 40) {
                            if (this.getCurrentTile() != null) {
                                try {
                                    final Item[] allItems = this.getInventory().getAllItems(false);
                                    final Item[] groundItems = this.getCurrentTile().getItems();
                                    Item rand3 = null;
                                    if (allItems.length > 0) {
                                        rand3 = allItems[Server.rand.nextInt(allItems.length)];
                                    }
                                    if (groundItems.length > 0) {
                                        Item targ = groundItems[Server.rand.nextInt(groundItems.length)];
                                        if (Server.rand.nextBoolean() && this.getCurrentTile().getVillage() == null) {
                                            if (Server.rand.nextInt(4) == 0 && targ != null && targ.isHollow() && targ.testInsertItem(rand3)) {
                                                targ.insertItem(rand3);
                                            }
                                            else if (this.canCarry(targ.getWeightGrams())) {
                                                BehaviourDispatcher.action(this, this.communicator, -10L, targ.getWurmId(), (short)6);
                                            }
                                            else if (targ.isHollow()) {
                                                final Item[] containeds = targ.getAllItems(false);
                                                if (containeds.length > 0) {
                                                    final Item targ2 = containeds[Server.rand.nextInt(containeds.length)];
                                                    if (!targ2.isBodyPart() && !targ2.isNoTake()) {
                                                        BehaviourDispatcher.action(this, this.communicator, -10L, targ2.getWurmId(), (short)6);
                                                        this.wearItems();
                                                    }
                                                }
                                            }
                                        }
                                        else {
                                            if (targ.isHollow()) {
                                                final Item[] containeds = targ.getAllItems(false);
                                                if (containeds.length > 0 && Server.rand.nextBoolean()) {
                                                    targ = containeds[Server.rand.nextInt(containeds.length)];
                                                }
                                            }
                                            final Behaviour behaviour = Action.getBehaviour(targ.getWurmId(), this.isOnSurface());
                                            final BehaviourDispatcher.RequestParam param = BehaviourDispatcher.requestActionForItemsBodyIdsCoinIds(this, targ.getWurmId(), rand3, behaviour);
                                            final List<ActionEntry> actions = param.getAvailableActions();
                                            if (actions.size() > 0) {
                                                final ActionEntry ae = actions.get(Server.rand.nextInt(actions.size()));
                                                if (ae.getNumber() > 0) {
                                                    BehaviourDispatcher.action(this, this.communicator, rand3.getWurmId(), targ.getWurmId(), ae.getNumber());
                                                }
                                            }
                                        }
                                    }
                                    else if (rand3 != null) {
                                        BehaviourDispatcher.action(this, this.communicator, -10L, rand3.getWurmId(), (short)162);
                                    }
                                    this.setPassiveCounter(30);
                                }
                                catch (Exception ex3) {}
                            }
                            this.setPassiveCounter(10);
                        }
                        if (seed < 50) {
                            if (this.getCurrentTile() != null) {
                                try {
                                    final Item[] allItems = this.getInventory().getAllItems(false);
                                    Item rand2 = null;
                                    if (allItems.length > 0) {
                                        boolean abilused = false;
                                        for (final Item abil : allItems) {
                                            if (abil.isAbility() && Server.rand.nextBoolean()) {
                                                BehaviourDispatcher.action(this, this.communicator, -10L, abil.getWurmId(), (short)118);
                                                abilused = true;
                                                break;
                                            }
                                        }
                                        if (!abilused) {
                                            rand2 = allItems[Server.rand.nextInt(allItems.length)];
                                            if (Server.rand.nextInt(5) == 0 && !rand2.isEpicTargetItem() && rand2.isUnique() && !rand2.isAbility() && !rand2.isMagicStaff() && !rand2.isRoyal()) {
                                                rand2.putItemInfrontof(this);
                                            }
                                            else if (this.isOnSurface()) {
                                                final long targTile = Tiles.getTileId(this.getTileX(), this.getTileY(), 0);
                                                final Behaviour behaviour2 = Action.getBehaviour(targTile, this.isOnSurface());
                                                final BehaviourDispatcher.RequestParam param2 = BehaviourDispatcher.requestActionForTiles(this, targTile, true, rand2, behaviour2);
                                                final List<ActionEntry> actions2 = param2.getAvailableActions();
                                                if (actions2.size() > 0) {
                                                    final ActionEntry ae2 = actions2.get(Server.rand.nextInt(actions2.size()));
                                                    if (ae2.getNumber() > 0) {
                                                        BehaviourDispatcher.action(this, this.communicator, (rand2 == null) ? -10L : rand2.getWurmId(), targTile, ae2.getNumber());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else if (this.isOnSurface()) {
                                        final long targTile2 = Tiles.getTileId(this.getTileX(), this.getTileY(), 0);
                                        final Behaviour behaviour = Action.getBehaviour(targTile2, this.isOnSurface());
                                        final BehaviourDispatcher.RequestParam param = BehaviourDispatcher.requestActionForTiles(this, targTile2, true, null, behaviour);
                                        final List<ActionEntry> actions = param.getAvailableActions();
                                        if (actions.size() > 0) {
                                            final ActionEntry ae = actions.get(Server.rand.nextInt(actions.size()));
                                            if (ae.getNumber() > 0) {
                                                BehaviourDispatcher.action(this, this.communicator, -10L, targTile2, ae.getNumber());
                                            }
                                        }
                                    }
                                    this.setPassiveCounter(30);
                                }
                                catch (Exception ex4) {}
                            }
                            this.setPassiveCounter(10);
                        }
                        if (seed < 70) {
                            if (this.getCurrentTile() != null) {
                                try {
                                    final Item[] allItems = this.getInventory().getAllItems(false);
                                    Item rand2 = null;
                                    if (allItems.length > 0) {
                                        rand2 = allItems[Server.rand.nextInt(allItems.length)];
                                    }
                                    boolean found = false;
                                    Creature[] crets = null;
                                    for (int x = -2; x <= 2; ++x) {
                                        for (int y = -2; y <= 2; ++y) {
                                            final VolaTile t = Zones.getTileOrNull(Zones.safeTileX(this.getTileX() + x), Zones.safeTileY(this.getTileY() + y), this.isOnSurface());
                                            if (t != null) {
                                                crets = t.getCreatures();
                                                if (crets.length > 0) {
                                                    final Creature targC = crets[Server.rand.nextInt(crets.length)];
                                                    final Behaviour behaviour3 = Action.getBehaviour(targC.getWurmId(), this.isOnSurface());
                                                    final BehaviourDispatcher.RequestParam param3 = BehaviourDispatcher.requestActionForCreaturesPlayers(this, targC.getWurmId(), rand2, targC.isPlayer() ? 0 : 1, behaviour3);
                                                    final List<ActionEntry> actions3 = param3.getAvailableActions();
                                                    if (actions3.size() > 0) {
                                                        final ActionEntry ae3 = actions3.get(Server.rand.nextInt(actions3.size()));
                                                        if (ae3.getNumber() > 0) {
                                                            BehaviourDispatcher.action(this, this.communicator, (rand2 == null) ? -10L : rand2.getWurmId(), targC.getWurmId(), ae3.getNumber());
                                                        }
                                                        this.setPassiveCounter(30);
                                                        found = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (!found) {
                                        final long targTile3 = Tiles.getTileId(this.getTileX() - 1 + Server.rand.nextInt(2), this.getTileY() - 1 + Server.rand.nextInt(2), 0, this.isOnSurface());
                                        final Behaviour behaviour4 = Action.getBehaviour(targTile3, this.isOnSurface());
                                        final BehaviourDispatcher.RequestParam param4 = BehaviourDispatcher.requestActionForTiles(this, targTile3, true, rand2, behaviour4);
                                        final List<ActionEntry> actions4 = param4.getAvailableActions();
                                        if (actions4.size() > 0) {
                                            final ActionEntry ae4 = actions4.get(Server.rand.nextInt(actions4.size()));
                                            if (ae4.getNumber() > 0) {
                                                BehaviourDispatcher.action(this, this.communicator, (rand2 == null) ? -10L : rand2.getWurmId(), targTile3, ae4.getNumber());
                                            }
                                        }
                                    }
                                    this.setPassiveCounter(30);
                                }
                                catch (Exception ex5) {}
                            }
                            this.setPassiveCounter(10);
                        }
                        if (seed < 80) {
                            Creature[] crets2 = null;
                            for (int x2 = -2; x2 <= 2; ++x2) {
                                for (int y2 = -2; y2 <= 2; ++y2) {
                                    final VolaTile t2 = Zones.getTileOrNull(Zones.safeTileX(this.getTileX() + x2), Zones.safeTileY(this.getTileY() + y2), this.isOnSurface());
                                    if (t2 != null) {
                                        crets2 = t2.getCreatures();
                                        if (crets2.length > 0) {
                                            try {
                                                final Creature targC2 = crets2[Server.rand.nextInt(crets2.length)];
                                                final Behaviour behaviour2 = Action.getBehaviour(targC2.getWurmId(), this.isOnSurface());
                                                final BehaviourDispatcher.RequestParam param2 = BehaviourDispatcher.requestActionForCreaturesPlayers(this, targC2.getWurmId(), null, targC2.isPlayer() ? 0 : 1, behaviour2);
                                                final List<ActionEntry> actions2 = param2.getAvailableActions();
                                                if (actions2.size() > 0) {
                                                    final ActionEntry ae2 = actions2.get(Server.rand.nextInt(actions2.size()));
                                                    if ((!ae2.isOffensive() || !this.isFriendlyKingdom(targC2.getKingdomId())) && ae2.getNumber() > 0) {
                                                        BehaviourDispatcher.action(this, this.communicator, -10L, targC2.getWurmId(), ae2.getNumber());
                                                    }
                                                    break;
                                                }
                                            }
                                            catch (Exception ex6) {}
                                        }
                                    }
                                }
                            }
                        }
                        try {
                            final Item[] allItems = this.getInventory().getAllItems(false);
                            if (allItems.length > 2) {
                                final Item rand4 = allItems[Server.rand.nextInt(allItems.length)];
                                final Item rand5 = allItems[Server.rand.nextInt(allItems.length)];
                                final Behaviour behaviour5 = Action.getBehaviour(rand5.getWurmId(), this.isOnSurface());
                                final BehaviourDispatcher.RequestParam param5 = BehaviourDispatcher.requestActionForItemsBodyIdsCoinIds(this, rand5.getWurmId(), rand4, behaviour5);
                                final List<ActionEntry> actions5 = param5.getAvailableActions();
                                if (actions5.size() > 0) {
                                    final ActionEntry ae5 = actions5.get(Server.rand.nextInt(actions5.size()));
                                    if (ae5.getNumber() > 0) {
                                        BehaviourDispatcher.action(this, this.communicator, (rand4 == null) ? -10L : rand4.getWurmId(), rand5.getWurmId(), ae5.getNumber());
                                    }
                                }
                                this.setPassiveCounter(30);
                            }
                        }
                        catch (Exception ex7) {}
                    }
                }
                else {
                    this.setPassiveCounter(180);
                }
            }
            else {
                this.setPassiveCounter(30);
            }
        }
    }
    
    private void clearLongTarget() {
        this.longTarget = null;
        this.longTargetAttempts = 0;
    }
    
    public boolean isOnLongTargetTile() {
        return this.getStatus() != null && this.longTarget.getTileX() == (int)this.status.getPositionX() >> 2 && this.longTarget.getTileY() == (int)this.status.getPositionY() >> 2;
    }
    
    @Override
    public final Path findPath(final int targetX, final int targetY, @Nullable final PathFinder pathfinder) throws NoPathException {
        Path path = null;
        final PathFinder pf = (pathfinder != null) ? pathfinder : new PathFinder();
        this.setPathfindcounter(this.getPathfindCounter() + 1);
        if (this.getPathfindCounter() < 10 || this.target != -10L || this.getPower() > 0) {
            path = pf.findPath(this, this.getTileX(), this.getTileY(), targetX, targetY, this.isOnSurface(), 20);
            if (path != null) {
                this.setPathfindcounter(0);
            }
            return path;
        }
        throw new NoPathException("No pathing now");
    }
    
    private final boolean capturePillar() {
        if (this.getCitizenVillage() != null) {
            final FocusZone hota = FocusZone.getHotaZone();
            if (hota != null && hota.covers(this.getTileX(), this.getTileY())) {
                for (final Item i : this.getCurrentTile().getItems()) {
                    if (i.getTemplateId() == 739 && i.getData1() != this.getCitizenVillage().getId()) {
                        try {
                            BehaviourDispatcher.action(this, this.communicator, -10L, i.getWurmId(), (short)504);
                        }
                        catch (Exception ex) {}
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private final boolean performLongTargetAction() {
        if (this.longTarget != null && this.longTarget.getMissionTrigger() > 0) {
            final MissionTrigger trigger = MissionTriggers.getTriggerWithId(this.longTarget.getMissionTrigger());
            if (trigger != null && Math.abs(this.longTarget.getTileX() - this.getTileX()) < 3 && Math.abs(this.longTarget.getTileY() - this.getTileY()) < 3) {
                Item found = null;
                if (trigger.getItemUsedId() > 0) {
                    if (trigger.getOnActionPerformed() == 148) {
                        final CreationEntry ce = CreationMatrix.getInstance().getCreationEntry(trigger.getItemUsedId());
                        if (ce != null && !ce.isAdvanced()) {
                            try {
                                found = ItemFactory.createItem(trigger.getItemUsedId(), 20.0f + Server.rand.nextFloat() * 20.0f, this.getName());
                                this.getInventory().insertItem(found, true);
                                if (found.getWeightGrams() > 20000) {
                                    found.putItemInfrontof(this);
                                }
                                else {
                                    this.wearItems();
                                }
                                MissionTriggers.activateTriggers(this, found, 148, 0L, 1);
                                this.clearLongTarget();
                                return true;
                            }
                            catch (Exception ex) {}
                        }
                    }
                    for (final Item item : this.getAllItems()) {
                        if (item.getTemplateId() == trigger.getItemUsedId()) {
                            found = item;
                        }
                    }
                    if (found == null) {
                        try {
                            found = ItemFactory.createItem(trigger.getItemUsedId(), 20.0f + Server.rand.nextFloat() * 20.0f, this.getName());
                            this.getInventory().insertItem(found, true);
                        }
                        catch (Exception ex2) {}
                    }
                }
                Label_0340: {
                    if (WurmId.getType(trigger.getTarget()) != 1) {
                        if (WurmId.getType(trigger.getTarget()) != 0) {
                            break Label_0340;
                        }
                    }
                    try {
                        final Creature c = Server.getInstance().getCreature(trigger.getTarget());
                        if (c == null || c.isDead()) {
                            this.clearLongTarget();
                            return true;
                        }
                    }
                    catch (NoSuchCreatureException nsc) {
                        this.clearLongTarget();
                        return true;
                    }
                    catch (NoSuchPlayerException nsp) {
                        this.clearLongTarget();
                        return true;
                    }
                }
                Label_0575: {
                    if (WurmId.getType(trigger.getTarget()) == 3 && trigger.getOnActionPerformed() == 492) {
                        final int tilenum = Server.surfaceMesh.getTile(this.getTileX(), this.getTileY());
                        if (!Tiles.isTree(Tiles.decodeType(tilenum))) {
                            return true;
                        }
                        if (found == null) {
                            for (final Item axe : this.getBody().getBodyItem().getAllItems(false)) {
                                if (axe.isWeaponAxe() || axe.isWeaponSlash()) {
                                    found = axe;
                                }
                            }
                        }
                        if (found == null) {
                            for (final Item axe : this.getInventory().getAllItems(false)) {
                                if (axe.isWeaponAxe() || axe.isWeaponSlash()) {
                                    found = axe;
                                }
                            }
                        }
                        if (found == null) {
                            try {
                                found = ItemFactory.createItem(7, 10.0f, this.getName());
                            }
                            catch (Exception ex3) {}
                        }
                        if (found == null || !found.isWeaponAxe()) {
                            if (!found.isWeaponSlash()) {
                                break Label_0575;
                            }
                        }
                        try {
                            BehaviourDispatcher.action(this, this.communicator, found.getWurmId(), trigger.getTarget(), (short)96);
                        }
                        catch (Exception ex4) {}
                    }
                }
                MissionTriggers.activateTriggers(this, found, trigger.getOnActionPerformed(), trigger.getTarget(), 1);
                this.clearLongTarget();
                return true;
            }
        }
        else if (this.longTarget != null && this.isOnLongTargetTile()) {
            final Item[] items;
            final Item[] currentItems = items = this.getCurrentTile().getItems();
            for (final Item current : items) {
                if (current.isCorpse() && current.getLastOwnerId() == this.getWurmId()) {
                    for (final Item incorpse : current.getAllItems(false)) {
                        if (!incorpse.isBodyPart()) {
                            this.getInventory().insertItem(incorpse);
                        }
                    }
                    this.wearItems();
                    Items.destroyItem(current.getWurmId());
                }
            }
            this.clearLongTarget();
            return true;
        }
        return false;
    }
    
    @Override
    public final PathTile getMoveTarget(final int seed) {
        if (this.getStatus() == null) {
            return null;
        }
        final float lPosX = this.status.getPositionX();
        final float lPosY = this.status.getPositionY();
        final boolean hasTarget = false;
        int tilePosX = (int)lPosX >> 2;
        int tilePosY = (int)lPosY >> 2;
        final int tx = tilePosX;
        final int ty = tilePosY;
        if (!this.isAggHuman() && this.getCitizenVillage() != null) {
            if (this.longTarget == null) {
                if (Server.rand.nextInt(100) == 0) {
                    final Player[] players = Players.getInstance().getPlayers();
                    int x = 0;
                    while (x < 10) {
                        final Player p = players[Server.rand.nextInt(players.length)];
                        if (p.isWithinDistanceTo(this, 200.0f) && p.getPower() == 0) {
                            int tile = Server.surfaceMesh.getTile(tilePosX, tilePosY);
                            if (!p.isOnSurface()) {
                                tile = Server.caveMesh.getTile(tilePosX, tilePosY);
                            }
                            this.longTarget = new LongTarget(p.getTileX(), p.getTileY(), tile, p.isOnSurface(), p.getFloorLevel(), this);
                            if (p.isFriendlyKingdom(this.getKingdomId())) {
                                this.getChatManager().createAndSendMessage(p, "Oi.", false);
                                break;
                            }
                            this.getChatManager().createAndSendMessage(p, "Coming for you.", false);
                            break;
                        }
                        else {
                            ++x;
                        }
                    }
                }
                if (this.longTarget == null && Server.rand.nextInt(10) == 0) {
                    final Item[] allItems;
                    final Item[] allIts = allItems = Items.getAllItems();
                    for (final Item corpse : allItems) {
                        if (corpse.getZoneId() > 0 && corpse.getTemplateId() == 272 && corpse.getLastOwnerId() == this.getWurmId() && corpse.getName().toLowerCase().contains(this.getName().toLowerCase())) {
                            final Item[] contained = corpse.getAllItems(false);
                            if (contained.length > 4) {
                                final boolean surf = corpse.isOnSurface();
                                int tile2 = Server.surfaceMesh.getTile(corpse.getTileX(), corpse.getTileY());
                                if (!surf) {
                                    tile2 = Server.caveMesh.getTile(corpse.getTileX(), corpse.getTileY());
                                }
                                this.longTarget = new LongTarget(corpse.getTileX(), corpse.getTileY(), tile2, surf, surf ? 0 : -1, this);
                            }
                        }
                    }
                }
                if (this.longTarget == null && Server.rand.nextInt(10) == 0) {
                    final EpicMission[] currentEpicMissions;
                    final EpicMission[] ems = currentEpicMissions = EpicServerStatus.getCurrentEpicMissions();
                    for (final EpicMission em : currentEpicMissions) {
                        if (em.isCurrent()) {
                            final Deity deity = Deities.getDeity(em.getEpicEntityId());
                            if (deity != null && deity.getFavoredKingdom() == this.getKingdomId()) {
                                for (final MissionTrigger trig : MissionTriggers.getAllTriggers()) {
                                    if (trig.getMissionRequired() == em.getMissionId()) {
                                        final long target = trig.getTarget();
                                        Label_0993: {
                                            if (WurmId.getType(target) == 3 || WurmId.getType(target) == 17) {
                                                final int x2 = Tiles.decodeTileX(target);
                                                final int y2 = Tiles.decodeTileY(target);
                                                final boolean surf2 = WurmId.getType(target) == 3;
                                                int tile3 = Server.surfaceMesh.getTile(x2, y2);
                                                if (!surf2) {
                                                    tile3 = Server.caveMesh.getTile(x2, y2);
                                                }
                                                this.longTarget = new LongTarget(x2, y2, tile3, surf2, surf2 ? 0 : -1, this);
                                            }
                                            else if (WurmId.getType(target) == 2) {
                                                try {
                                                    final Item i = Items.getItem(target);
                                                    int tile4 = Server.surfaceMesh.getTile(i.getTileX(), i.getTileY());
                                                    if (!i.isOnSurface()) {
                                                        tile4 = Server.caveMesh.getTile(i.getTileX(), i.getTileY());
                                                    }
                                                    this.longTarget = new LongTarget(i.getTileX(), i.getTileY(), tile4, i.isOnSurface(), i.getFloorLevel(), this);
                                                }
                                                catch (NoSuchItemException ex) {}
                                            }
                                            else {
                                                if (WurmId.getType(target) != 1) {
                                                    if (WurmId.getType(target) != 0) {
                                                        if (WurmId.getType(target) != 5) {
                                                            break Label_0993;
                                                        }
                                                        final int x3 = (int)(target >> 32) & 0xFFFF;
                                                        final int y3 = (int)(target >> 16) & 0xFFFF;
                                                        final Wall wall = Wall.getWall(target);
                                                        if (wall != null) {
                                                            final int tile3 = Server.surfaceMesh.getTile(x3, y3);
                                                            this.longTarget = new LongTarget(x3, y3, tile3, true, wall.getFloorLevel(), this);
                                                        }
                                                        break Label_0993;
                                                    }
                                                }
                                                try {
                                                    final Creature c = Server.getInstance().getCreature(target);
                                                    int tile4 = Server.surfaceMesh.getTile(c.getTileX(), c.getTileY());
                                                    if (!c.isOnSurface()) {
                                                        tile4 = Server.caveMesh.getTile(c.getTileX(), c.getTileY());
                                                    }
                                                    this.longTarget = new LongTarget(c.getTileX(), c.getTileY(), tile4, c.isOnSurface(), c.getFloorLevel(), this);
                                                }
                                                catch (NoSuchCreatureException ex2) {}
                                                catch (NoSuchPlayerException ex3) {}
                                            }
                                        }
                                        if (this.longTarget != null) {
                                            this.longTarget.setMissionTrigger(trig.getId());
                                            this.longTarget.setEpicMission(em.getMissionId());
                                            this.longTarget.setMissionTarget(target);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (this.longTarget == null && Server.rand.nextInt(10) == 0) {
                    if (this.getCitizenVillage() != null) {
                        if (Creature.getTileRange(this, this.getCitizenVillage().getTokenX(), this.getCitizenVillage().getTokenY()) > 300.0) {
                            final int tile5 = Server.surfaceMesh.getTile(this.getCitizenVillage().getTokenX(), this.getCitizenVillage().getTokenY());
                            this.longTarget = new LongTarget(this.getCitizenVillage().getTokenX(), this.getCitizenVillage().getTokenY(), tile5, true, 0, this);
                        }
                    }
                    else {
                        for (final Village v : Villages.getVillages()) {
                            if (v.isPermanent && v.kingdom == this.getKingdomId() && Creature.getTileRange(this, v.getTokenX(), v.getTokenY()) > 300.0) {
                                final int tile6 = Server.surfaceMesh.getTile(v.getTokenX(), v.getTokenY());
                                this.longTarget = new LongTarget(v.getTokenX(), v.getTokenY(), tile6, true, 0, this);
                            }
                        }
                    }
                    if (this.longTarget != null) {
                        final int seedh = Server.rand.nextInt(5);
                        String mess = "Think I'll head home again...";
                        switch (seedh) {
                            case 0: {
                                mess = "Time to go home!";
                                break;
                            }
                            case 1: {
                                mess = "Enough of this. Home Sweet Home.";
                                break;
                            }
                            case 2: {
                                mess = "Heading home. Are you coming?";
                                break;
                            }
                            case 3: {
                                mess = "I will go home now.";
                                break;
                            }
                            case 4: {
                                mess = "That's it. I'm going home.";
                                break;
                            }
                            default: {
                                mess = "Think I'll go home for a while.";
                                break;
                            }
                        }
                        if (this.getCurrentTile() != null) {
                            final Message m = new Message(this, (byte)0, ":Local", "<" + this.getName() + "> " + mess);
                            this.getCurrentTile().broadCastMessage(m);
                        }
                    }
                }
                if (this.longTarget == null && Server.rand.nextInt(100) == 0 && this.getCitizenVillage() != null) {
                    final FocusZone hota = FocusZone.getHotaZone();
                    if (hota != null && !hota.covers(this.getTileX(), this.getTileY())) {
                        final int hx = hota.getStartX() + Server.rand.nextInt(hota.getEndX() - hota.getStartX());
                        final int hy = hota.getStartY() + Server.rand.nextInt(hota.getEndY() - hota.getStartY());
                        final int tile = Server.surfaceMesh.getTile(hx, hy);
                        this.longTarget = new LongTarget(hx, hy, tile, true, 0, this);
                        final int seedh2 = Server.rand.nextInt(5);
                        String mess2 = "Think I'll go hunt for some pillars a bit...";
                        switch (seedh2) {
                            case 0: {
                                mess2 = "Anyone in the Hunt of the Ancients is in trouble now!";
                                break;
                            }
                            case 1: {
                                mess2 = "Going to check out what happens in the Hunt.";
                                break;
                            }
                            case 2: {
                                mess2 = "Heading to join the Hunt. Coming with me?";
                                break;
                            }
                            case 3: {
                                mess2 = "Going to head to the Hunt of the Ancients. You interested?";
                                break;
                            }
                            case 4: {
                                mess2 = "I want to do some gloryhunting in the HOTA.";
                                break;
                            }
                            default: {
                                mess2 = "Think I'll go join the hunt a bit...";
                                break;
                            }
                        }
                        if (this.getCurrentTile() != null) {
                            final Message j = new Message(this, (byte)0, ":Local", "<" + this.getName() + "> " + mess2);
                            this.getCurrentTile().broadCastMessage(j);
                        }
                    }
                }
                if (this.longTarget != null) {
                    return this.longTarget;
                }
            }
            else {
                boolean clear = false;
                if (this.longTarget.getCreatureTarget() != null && this.longTarget.getTileX() != this.longTarget.getCreatureTarget().getTileX()) {
                    this.longTarget.setTileX(this.longTarget.getCreatureTarget().getTileX());
                }
                if (this.longTarget.getCreatureTarget() != null && this.longTarget.getTileY() != this.longTarget.getCreatureTarget().getTileY()) {
                    this.longTarget.setTileY(this.longTarget.getCreatureTarget().getTileY());
                }
                if (this.longTarget.getEpicMission() > 0) {
                    final EpicMission em2 = EpicServerStatus.getEpicMissionForMission(this.longTarget.getEpicMission());
                    if (em2 == null || !em2.isCurrent() || em2.isCompleted()) {
                        clear = true;
                    }
                }
                if (Math.abs(this.longTarget.getTileX() - tx) < 20 && Math.abs(this.longTarget.getTileY() - ty) < 20) {
                    if (Math.abs(this.longTarget.getTileX() - tx) < 10 && Math.abs(this.longTarget.getTileY() - ty) < 10 && this.longTarget.getCreatureTarget() != null && !this.longTarget.getCreatureTarget().isFriendlyKingdom(this.getKingdomId())) {
                        this.setTarget(this.longTarget.getCreatureTarget().getWurmId(), false);
                        clear = true;
                    }
                    if (this.isOnLongTargetTile() && this.longTargetAttempts++ <= 50) {
                        return this.longTarget;
                    }
                    clear = true;
                }
                else if (System.currentTimeMillis() - this.longTarget.getStartTime() > 3600000L) {
                    clear = true;
                }
                if (clear) {
                    this.clearLongTarget();
                }
            }
        }
        boolean flee = false;
        if ((this.target == -10L || this.fleeCounter > 0) && (this.isTypeFleeing() || this.fleeCounter > 0) && this.isOnSurface()) {
            if (Server.rand.nextBoolean()) {
                if (this.getCurrentTile() != null && this.getCurrentTile().getVillage() != null) {
                    final Long[] creatures;
                    final Long[] crets = creatures = this.getVisionArea().getSurface().getCreatures();
                    for (final Long lCret : creatures) {
                        try {
                            final Creature cret = Server.getInstance().getCreature(lCret);
                            if (cret.getPower() == 0 && (cret.isPlayer() || cret.isAggHuman() || cret.isCarnivore() || cret.isMonster())) {
                                if (cret.getPosX() > this.getPosX()) {
                                    tilePosX -= Server.rand.nextInt(6);
                                }
                                else {
                                    tilePosX += Server.rand.nextInt(6);
                                }
                                if (cret.getPosY() > this.getPosY()) {
                                    tilePosY -= Server.rand.nextInt(6);
                                }
                                else {
                                    tilePosY += Server.rand.nextInt(6);
                                }
                                flee = true;
                                break;
                            }
                        }
                        catch (Exception ex4) {}
                    }
                }
            }
            else {
                for (final Player p2 : Players.getInstance().getPlayers()) {
                    if ((p2.getPower() == 0 || Servers.localServer.testServer) && p2.getVisionArea() != null && p2.getVisionArea().getSurface() != null && p2.getVisionArea().getSurface().containsCreature(this)) {
                        if (p2.getPosX() > this.getPosX()) {
                            tilePosX -= Server.rand.nextInt(6);
                        }
                        else {
                            tilePosX += Server.rand.nextInt(6);
                        }
                        if (p2.getPosY() > this.getPosY()) {
                            tilePosY -= Server.rand.nextInt(6);
                        }
                        else {
                            tilePosY += Server.rand.nextInt(6);
                        }
                        flee = true;
                        break;
                    }
                }
            }
        }
        if (!flee && !hasTarget) {
            final VolaTile currTile = this.getCurrentTile();
            if (currTile != null) {
                int rand = Server.rand.nextInt(9);
                int tpx = currTile.getTileX() + 4 - rand;
                rand = Server.rand.nextInt(9);
                int tpy = currTile.getTileY() + 4 - rand;
                Npc.totx += currTile.getTileX() - tpx;
                Npc.toty += currTile.getTileY() - tpy;
                if (this.longTarget != null) {
                    if (Math.abs(this.longTarget.getTileX() - this.getTileX()) < 20) {
                        tpx = this.longTarget.getTileX();
                    }
                    else {
                        tpx = this.getTileX() + 5 + Server.rand.nextInt(6);
                        if (this.getTileX() > this.longTarget.getTileX()) {
                            tpx = this.getTileX() - 5 - Server.rand.nextInt(6);
                        }
                    }
                    if (Math.abs(this.longTarget.getTileY() - this.getTileY()) < 20) {
                        tpy = this.longTarget.getTileY();
                    }
                    else {
                        tpy = this.getTileY() + 5 + Server.rand.nextInt(6);
                        if (this.getTileY() > this.longTarget.getTileY()) {
                            tpy = this.getTileY() - 5 - Server.rand.nextInt(6);
                        }
                    }
                }
                else if (this.getCitizenVillage() != null) {
                    final FocusZone hota2 = FocusZone.getHotaZone();
                    if (hota2 != null && hota2.covers(this.getTileX(), this.getTileY())) {
                        for (final Item pillar : Hota.getHotaItems()) {
                            if (pillar.getTemplateId() == 739 && pillar.getZoneId() > 0 && pillar.getData1() != this.getCitizenVillage().getId() && Creature.getTileRange(this, pillar.getTileX(), pillar.getTileY()) < 20.0) {
                                tpx = pillar.getTileX();
                                tpy = pillar.getTileY();
                            }
                        }
                    }
                }
                tpx = Zones.safeTileX(tpx);
                tpy = Zones.safeTileY(tpy);
                final VolaTile t = Zones.getOrCreateTile(tpx, tpy, this.isOnSurface());
                if (this.isOnSurface()) {
                    boolean stepOnBridge = false;
                    if (Server.rand.nextInt(5) == 0) {
                        for (final VolaTile stile : this.currentTile.getThisAndSurroundingTiles(1)) {
                            if (stile.getStructure() != null && stile.getStructure().isTypeBridge()) {
                                if (stile.getStructure().isHorizontal()) {
                                    if ((stile.getStructure().getMaxX() == stile.getTileX() || stile.getStructure().getMinX() == stile.getTileX()) && this.getTileY() == stile.getTileY()) {
                                        tilePosX = stile.getTileX();
                                        tilePosY = stile.getTileY();
                                        stepOnBridge = true;
                                        break;
                                    }
                                    continue;
                                }
                                else {
                                    if ((stile.getStructure().getMaxY() == stile.getTileY() || stile.getStructure().getMinY() == stile.getTileY()) && this.getTileX() == stile.getTileX()) {
                                        tilePosX = stile.getTileX();
                                        tilePosY = stile.getTileY();
                                        stepOnBridge = true;
                                        break;
                                    }
                                    continue;
                                }
                            }
                        }
                    }
                    if (!stepOnBridge && (t == null || t.getCreatures().length < 3)) {
                        tilePosX = tpx;
                        tilePosY = tpy;
                    }
                }
                else if (t == null || t.getCreatures().length < 3) {
                    tilePosX = tpx;
                    tilePosY = tpy;
                }
            }
        }
        final Creature targ = this.getTarget();
        if (targ != null) {
            if (targ.getCultist() != null && targ.getCultist().hasFearEffect()) {
                this.setTarget(-10L, true);
            }
            final VolaTile currTile2 = targ.getCurrentTile();
            if (currTile2 != null) {
                tilePosX = currTile2.tilex;
                tilePosY = currTile2.tiley;
                if (seed == 100) {
                    tilePosX = currTile2.tilex - 1 + Server.rand.nextInt(3);
                    tilePosY = currTile2.tiley - 1 + Server.rand.nextInt(3);
                }
                final int targGroup = targ.getGroupSize();
                final int myGroup = this.getGroupSize();
                if (this.isOnSurface() != currTile2.isOnSurface()) {
                    boolean changeLayer = false;
                    if (this.getCurrentTile().isTransition) {
                        changeLayer = true;
                    }
                    final VolaTile t2 = this.getCurrentTile();
                    if ((this.isAggHuman() || this.isHunter() || this.isDominated()) && (!currTile2.isGuarded() || (t2 != null && t2.isGuarded())) && this.isWithinTileDistanceTo(currTile2.getTileX(), currTile2.getTileY(), (int)targ.getPositionZ(), this.template.getMaxHuntDistance())) {
                        if (!changeLayer) {
                            int[] tiles = { tilePosX, tilePosY };
                            if (this.isOnSurface()) {
                                tiles = this.findRandomCaveEntrance(tiles);
                            }
                            else {
                                tiles = this.findRandomCaveExit(tiles);
                            }
                            tilePosX = tiles[0];
                            tilePosY = tiles[1];
                        }
                    }
                    else {
                        this.setTarget(-10L, true);
                    }
                    if (changeLayer && (!Tiles.isMineDoor(Tiles.decodeType(Server.surfaceMesh.getTile(tx, ty))) || MineDoorPermission.getPermission(tx, ty).mayPass(this))) {
                        this.setLayer(this.isOnSurface() ? -1 : 0, true);
                    }
                }
                if (targ.getCultist() != null && targ.getCultist().hasFearEffect()) {
                    if (Server.rand.nextBoolean()) {
                        tilePosX = Math.max(currTile2.getTileX() + 10, this.getTileX());
                    }
                    else {
                        tilePosX = Math.min(currTile2.getTileX() - 10, this.getTileX());
                    }
                    if (Server.rand.nextBoolean()) {
                        tilePosX = Math.max(currTile2.getTileY() + 10, this.getTileY());
                    }
                    else {
                        tilePosX = Math.min(currTile2.getTileY() - 10, this.getTileY());
                    }
                }
                else {
                    final VolaTile t = this.getCurrentTile();
                    if (targGroup <= myGroup * this.getMaxGroupAttackSize() && (this.isAggHuman() || this.isHunter()) && (!currTile2.isGuarded() || (t != null && t.isGuarded()))) {
                        if (this.isWithinTileDistanceTo(currTile2.getTileX(), currTile2.getTileY(), (int)targ.getPositionZ(), this.template.getMaxHuntDistance())) {
                            if (targ.getKingdomId() != 0 && !this.isFriendlyKingdom(targ.getKingdomId()) && (this.isDefendKingdom() || (this.isAggWhitie() && targ.getKingdomTemplateId() != 3))) {
                                if (!this.isFighting()) {
                                    if (seed == 100) {
                                        tilePosX = currTile2.tilex - 1 + Server.rand.nextInt(3);
                                        tilePosY = currTile2.tiley - 1 + Server.rand.nextInt(3);
                                    }
                                    else {
                                        tilePosX = currTile2.getTileX();
                                        tilePosY = currTile2.getTileY();
                                        this.setTarget(targ.getWurmId(), false);
                                    }
                                }
                            }
                            else if (seed == 100) {
                                tilePosX = currTile2.tilex - 1 + Server.rand.nextInt(3);
                                tilePosY = currTile2.tiley - 1 + Server.rand.nextInt(3);
                            }
                            else {
                                tilePosX = currTile2.getTileX();
                                tilePosY = currTile2.getTileY();
                                if (this.getSize() < 5 && targ.getBridgeId() != -10L && this.getBridgeId() < 0L) {
                                    final int[] tiles2 = this.findBestBridgeEntrance(targ.getTileX(), targ.getTileY(), targ.getLayer(), targ.getBridgeId());
                                    if (tiles2[0] > 0) {
                                        tilePosX = tiles2[0];
                                        tilePosY = tiles2[1];
                                        if (this.getTileX() == tilePosX && this.getTileY() == tilePosY) {
                                            tilePosX = currTile2.tilex;
                                            tilePosY = currTile2.tiley;
                                        }
                                    }
                                }
                                else if (this.getBridgeId() != targ.getBridgeId()) {
                                    final int[] tiles2 = this.findBestBridgeEntrance(targ.getTileX(), targ.getTileY(), targ.getLayer(), this.getBridgeId());
                                    if (tiles2[0] > 0) {
                                        tilePosX = tiles2[0];
                                        tilePosY = tiles2[1];
                                        if (this.getTileX() == tilePosX && this.getTileY() == tilePosY) {
                                            tilePosX = currTile2.tilex;
                                            tilePosY = currTile2.tiley;
                                        }
                                    }
                                }
                            }
                        }
                        else if (!this.isFighting()) {
                            this.setTarget(-10L, true);
                        }
                    }
                    else if (!this.isFighting()) {
                        this.setTarget(-10L, true);
                    }
                }
            }
        }
        if (tilePosX == tx && tilePosY == ty) {
            return null;
        }
        tilePosX = Zones.safeTileX(tilePosX);
        tilePosY = Zones.safeTileY(tilePosY);
        if (!this.isOnSurface()) {
            final int tile7 = Server.caveMesh.getTile(tilePosX, tilePosY);
            if (!Tiles.isSolidCave(Tiles.decodeType(tile7)) && (Tiles.decodeHeight(tile7) > -this.getHalfHeightDecimeters() || this.isSwimming() || this.isSubmerged())) {
                return new PathTile(tilePosX, tilePosY, tile7, this.isOnSurface(), -1);
            }
        }
        else {
            final int tile7 = Server.surfaceMesh.getTile(tilePosX, tilePosY);
            if (Tiles.decodeHeight(tile7) > -this.getHalfHeightDecimeters() || this.isSwimming() || this.isSubmerged()) {
                return new PathTile(tilePosX, tilePosY, tile7, this.isOnSurface(), this.getFloorLevel());
            }
        }
        this.setTarget(-10L, true);
        if (this.isDominated() && this.hasOrders()) {
            this.removeOrder(this.getFirstOrder());
        }
        return null;
    }
    
    private final void setPassiveCounter(final int counter) {
        this.passiveCounter = counter;
    }
    
    private final void checkItemSpawn() {
        if (this.lastX == 0) {
            this.lastX = this.getTileX();
        }
        if (this.lastY == 0) {
            this.lastY = this.getTileY();
        }
        if (this.lastX - this.getTileX() > 50 || this.lastY - this.getTileY() > 50) {
            this.lastX = this.getTileX();
            this.lastY = this.getTileY();
            if (Server.rand.nextInt(10) == 0 && this.getBody().getContainersAndWornItems().length < 10) {
                try {
                    final int templateId = Server.rand.nextInt(1437);
                    final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(templateId);
                    if ((template.isArmour() || template.isWeapon()) && !template.isRoyal && !template.artifact) {
                        try {
                            final Item toInsert = ItemFactory.createItem(templateId, Server.rand.nextFloat() * 80.0f + 20.0f, this.getName());
                            this.getInventory().insertItem(toInsert, true);
                            this.wearItems();
                            if (toInsert.getParentId() == this.getInventory().getWurmId()) {
                                Items.destroyItem(toInsert.getWurmId());
                            }
                        }
                        catch (FailedException ex) {}
                    }
                }
                catch (NoSuchTemplateException ex2) {}
            }
        }
    }
    
    @Override
    public final boolean isMoveLocal() {
        return this.hasTrait(8) || this.template.isMoveLocal();
    }
    
    @Override
    public final boolean isSentinel() {
        return this.hasTrait(9) || this.template.isSentinel();
    }
    
    @Override
    public final boolean isMoveGlobal() {
        return this.hasTrait(1) || this.template.isMoveGlobal();
    }
    
    @Override
    public boolean isNpc() {
        return true;
    }
    
    @Override
    public long getFace() {
        Npc.faceRandom.setSeed(this.getWurmId());
        return Npc.faceRandom.nextLong();
    }
    
    @Override
    public float getSpeed() {
        if (this.getVehicle() > -10L && WurmId.getType(this.getVehicle()) == 1) {
            return 1.7f;
        }
        return 1.1f;
    }
    
    @Override
    public boolean isTypeFleeing() {
        return this.getStatus().modtype == 10 || this.getStatus().damage > 45000;
    }
    
    @Override
    public boolean isRespawn() {
        return !this.hasTrait(19);
    }
    
    @Override
    public final boolean isDominatable(final Creature aDominator) {
        return (this.getLeader() == null || this.getLeader() == aDominator) && !this.isRidden() && this.hitchedTo == null && this.hasTrait(22);
    }
    
    @Override
    public final float getBaseCombatRating() {
        double fskill = 1.0;
        try {
            fskill = this.skills.getSkill(1023).getKnowledge();
        }
        catch (NoSuchSkillException nss) {
            this.skills.learn(1023, 1.0f);
            fskill = 1.0;
        }
        if (this.getLoyalty() > 0.0f) {
            return (float)Math.max(1.0, (this.isReborn() ? 0.7f : 0.5f) * fskill / 5.0 * this.status.getBattleRatingTypeModifier()) * Servers.localServer.getCombatRatingModifier();
        }
        return (float)Math.max(1.0, fskill / 5.0 * this.status.getBattleRatingTypeModifier()) * Servers.localServer.getCombatRatingModifier();
    }
    
    static {
        faceRandom = new Random();
    }
}
