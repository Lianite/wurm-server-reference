// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.structures.StructureSupport;
import javax.annotation.Nullable;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.players.Permissions;
import com.wurmonline.server.questions.MissionManager;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.structures.DbDoor;
import java.io.IOException;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.questions.PermissionsHistory;
import com.wurmonline.server.players.PermissionsPlayerList;
import com.wurmonline.server.questions.ManagePermissions;
import com.wurmonline.server.questions.ManageObjectList;
import com.wurmonline.server.Items;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.players.PermissionsHistories;
import com.wurmonline.server.questions.DemolishCheckQuestion;
import com.wurmonline.server.Server;
import com.wurmonline.server.items.WurmColor;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.utils.StringUtil;
import com.wurmonline.server.villages.Village;
import java.util.Iterator;
import java.util.Collections;
import com.wurmonline.server.tutorial.MissionTrigger;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.structures.Door;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.tutorial.MissionTriggers;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.Servers;
import com.wurmonline.server.structures.NoSuchWallException;
import java.util.logging.Level;
import com.wurmonline.server.structures.NoSuchLockException;
import com.wurmonline.server.structures.WallEnum;
import com.wurmonline.shared.constants.StructureStateEnum;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.server.zones.Zones;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.shared.constants.StructureTypeEnum;
import com.wurmonline.shared.constants.StructureMaterialEnum;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.items.Item;
import java.util.logging.Logger;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.MiscConstants;

public final class WallBehaviour extends Behaviour implements MiscConstants, ItemTypes
{
    private static final Logger logger;
    private static Item[] inventoryItems;
    private static Item[] bodyItems;
    private static int[] foundTemplates;
    
    WallBehaviour() {
        super((short)20);
    }
    
    private static boolean isValidModifyableWall(final Wall wall, final Item tool) {
        final int templateId = tool.getTemplateId();
        if (wall.getMaterial() == StructureMaterialEnum.STONE) {
            return templateId == 219;
        }
        return wall.getMaterial() == StructureMaterialEnum.PLAIN_STONE && (templateId == 62 || templateId == 63) && wall.getType() != StructureTypeEnum.NARROW_WINDOW && wall.getType() != StructureTypeEnum.BARRED;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final Wall wall) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.addAll(super.getBehavioursFor(performer, source, wall));
        final VolaTile wallTile = Zones.getOrCreateTile(wall.getTileX(), wall.getTileY(), wall.isOnSurface());
        final Structure structure = Structures.getStructureOrNull(wall.getStructureId());
        if (structure != null && structure.isFinalized() && !wall.isFinished()) {
            toReturn.add(Actions.actionEntrys[607]);
        }
        if (isValidModifyableWall(wall, source)) {
            if (source.getTemplateId() == 219) {
                toReturn.add(new ActionEntry((short)647, "Remove decorations", "removing decorations"));
            }
            else {
                toReturn.add(new ActionEntry((short)647, "Add decorations", "adding decorations"));
            }
        }
        if (MethodsStructure.isCorrectToolForBuilding(performer, source.getTemplateId())) {
            if (structure != null) {
                final boolean hasMarker = StructureBehaviour.hasMarker(wall.getStartX(), wall.getStartY(), wall.isOnSurface(), wall.getDir(), wall.getHeight());
                if (!structure.isFinalized()) {
                    toReturn.add(Actions.actionEntrys[58]);
                }
                else if (wall.getType() == StructureTypeEnum.PLAN && hasMarker) {
                    toReturn.addAll(addBuildMenu(performer, source, wall, hasMarker));
                }
                else if (wall.getType() == StructureTypeEnum.PLAN && structure.needsDoor()) {
                    toReturn.addAll(addBuildMenu(performer, source, wall, hasMarker));
                }
                else if (!wall.isFinished()) {
                    if (wall.getState() == StructureStateEnum.INITIALIZED) {
                        toReturn.addAll(addBuildMenu(performer, source, wall, hasMarker));
                    }
                    else {
                        final WallEnum typeOfWall = WallEnum.getWall(wall.getType(), wall.getMaterial());
                        final StructureTypeEnum type = wall.getType();
                        if (typeOfWall != WallEnum.WALL_PLAN) {
                            if (type == StructureTypeEnum.DOOR) {
                                toReturn.add(new ActionEntry(typeOfWall.getActionId(), "Continue door", "building door", new int[] { 43 }, 2));
                            }
                            else if (type == StructureTypeEnum.DOUBLE_DOOR) {
                                toReturn.add(new ActionEntry(typeOfWall.getActionId(), "Continue door", "building double door", new int[] { 43 }, 2));
                            }
                            else if (type == StructureTypeEnum.ARCHED) {
                                toReturn.add(new ActionEntry(typeOfWall.getActionId(), "Continue arched wall", "building arched wall", new int[] { 43 }, 2));
                            }
                            else if (type == StructureTypeEnum.ARCHED_LEFT) {
                                toReturn.add(new ActionEntry(typeOfWall.getActionId(), "Continue left arch", "building arched wall", new int[] { 43 }, 2));
                            }
                            else if (type == StructureTypeEnum.ARCHED_RIGHT) {
                                toReturn.add(new ActionEntry(typeOfWall.getActionId(), "Continue right arch", "building arched wall", new int[] { 43 }, 2));
                            }
                            else if (type == StructureTypeEnum.ARCHED_T) {
                                toReturn.add(new ActionEntry(typeOfWall.getActionId(), "Continue T arch", "building arched wall", new int[] { 43 }, 2));
                            }
                            else if (type == StructureTypeEnum.SOLID) {
                                toReturn.add(new ActionEntry(typeOfWall.getActionId(), "Continue wall", "building wall", new int[] { 43 }, 2));
                            }
                            else if (type == StructureTypeEnum.PORTCULLIS) {
                                toReturn.add(new ActionEntry(typeOfWall.getActionId(), "Continue portcullis", "building portcullis", new int[] { 43 }, 2));
                            }
                            else if (type == StructureTypeEnum.BARRED) {
                                toReturn.add(new ActionEntry(typeOfWall.getActionId(), "Continue barred wall", "building barred wall", new int[] { 43 }, 2));
                            }
                            else if (type == StructureTypeEnum.BALCONY) {
                                toReturn.add(new ActionEntry(typeOfWall.getActionId(), "Continue balcony", "building balcony", new int[] { 43 }, 2));
                            }
                            else if (type == StructureTypeEnum.JETTY) {
                                toReturn.add(new ActionEntry(typeOfWall.getActionId(), "Continue jetty", "building jetty", new int[] { 43 }, 2));
                            }
                            else if (type == StructureTypeEnum.ORIEL) {
                                toReturn.add(new ActionEntry(typeOfWall.getActionId(), "Continue oriel", "building oriel", new int[] { 43 }, 2));
                            }
                            else if (type == StructureTypeEnum.CANOPY_DOOR) {
                                toReturn.add(new ActionEntry(typeOfWall.getActionId(), "Continue canopy door", "building canopy", new int[] { 43 }, 2));
                            }
                            else if (type == StructureTypeEnum.WIDE_WINDOW) {
                                toReturn.add(new ActionEntry(typeOfWall.getActionId(), "Continue wide window", "building wide widnow", new int[] { 43 }, 2));
                            }
                            else {
                                toReturn.add(new ActionEntry(typeOfWall.getActionId(), "Continue window", "building window", new int[] { 43 }, 2));
                            }
                        }
                    }
                }
            }
        }
        else if (source.isLock() && source.getTemplateId() == 167) {
            if ((wall.getType() == StructureTypeEnum.DOOR || wall.getType() == StructureTypeEnum.DOUBLE_DOOR || wall.getType() == StructureTypeEnum.PORTCULLIS || wall.getType() == StructureTypeEnum.CANOPY_DOOR) && wall.isFinished() && structure != null && structure.mayModify(performer)) {
                final Door[] doors = structure.getAllDoors();
                for (int x = 0; x < doors.length; ++x) {
                    try {
                        if (doors[x].getWall() == wall) {
                            try {
                                doors[x].getLockId();
                                toReturn.add(new ActionEntry((short)161, "Change lock", "changing lock", WallBehaviour.emptyIntArr));
                            }
                            catch (NoSuchLockException nsl) {
                                toReturn.add(Actions.actionEntrys[161]);
                            }
                            break;
                        }
                    }
                    catch (NoSuchWallException nsw) {
                        WallBehaviour.logger.log(Level.WARNING, "No inner wall");
                    }
                }
            }
        }
        else if (source.getTemplateId() == 463) {
            if ((wall.getType() == StructureTypeEnum.DOOR || wall.getType() == StructureTypeEnum.DOUBLE_DOOR || wall.getType() == StructureTypeEnum.PORTCULLIS || wall.getType() == StructureTypeEnum.CANOPY_DOOR) && (wall.isOnPvPServer() || Servers.isThisATestServer()) && wall.isFinished()) {
                final Door door = wall.getDoor();
                if (door != null) {
                    try {
                        final Item lock = door.getLock();
                        if (performer.isWithinDistanceTo(wall.getTileX(), wall.getTileY(), 1)) {
                            MethodsStructure.addLockPickEntry(performer, source, door, false, lock, toReturn);
                        }
                    }
                    catch (NoSuchLockException ex) {}
                }
            }
        }
        else if (source.getTemplateId() == wall.getRepairItemTemplate()) {
            if (wall.getDamage() > 0.0f) {
                if (!wall.isNoRepair() && (!Servers.localServer.challengeServer || performer.getEnemyPresense() <= 0)) {
                    toReturn.add(Actions.actionEntrys[193]);
                }
            }
            else if (wall.isFinished() && wall.getQualityLevel() < 100.0f && !wall.isNoImprove()) {
                toReturn.add(Actions.actionEntrys[192]);
            }
        }
        else if (source.isColor()) {
            if (wall.isFinished() && !wall.isNotPaintable()) {
                toReturn.add(Actions.actionEntrys[231]);
            }
        }
        else if (source.getTemplateId() == 441) {
            if (wall.getColor() != -1 && !wall.isNotPaintable()) {
                toReturn.add(Actions.actionEntrys[232]);
            }
        }
        else if (source.getTemplateId() == 676 && source.getOwnerId() == performer.getWurmId()) {
            toReturn.add(Actions.actionEntrys[472]);
        }
        boolean addedDestroy = false;
        if (performer.getPower() >= 2) {
            if (structure != null) {
                int num = 0;
                if (structure.isFinalized() && wall.getType() != StructureTypeEnum.PLAN) {
                    if (wall.getDamage() > 0.0f) {
                        ++num;
                    }
                    else if (wall.getQualityLevel() < 100.0f) {
                        ++num;
                    }
                    if (performer.getPower() >= 5) {
                        ++num;
                    }
                    toReturn.add(new ActionEntry((short)(-(num + 3)), "Wall", "wall"));
                    if (wall.getDamage() > 0.0f) {
                        if (!Servers.localServer.challengeServer || performer.getEnemyPresense() <= 0) {
                            toReturn.add(Actions.actionEntrys[193]);
                        }
                    }
                    else if (wall.getQualityLevel() < 100.0f) {
                        toReturn.add(Actions.actionEntrys[192]);
                    }
                    toReturn.add(Actions.actionEntrys[180]);
                    toReturn.add(new ActionEntry((short)(-1), "Annihilate", "Annihilate"));
                    toReturn.add(Actions.actionEntrys[82]);
                    toReturn.add(Actions.actionEntrys[662]);
                    if (performer.getPower() >= 5) {
                        toReturn.add(Actions.actionEntrys[90]);
                    }
                }
                else if (structure.isFinalized() && wall.getType() == StructureTypeEnum.PLAN && performer.getPower() >= 4) {
                    toReturn.add(Actions.actionEntrys[866]);
                }
                else if ((source.getTemplateId() == 176 || source.getTemplateId() == 315) && WurmPermissions.mayUseGMWand(performer)) {
                    if (!addedDestroy) {
                        toReturn.add(new ActionEntry((short)(-1), "Annihilate", "Annihilate"));
                        toReturn.add(Actions.actionEntrys[82]);
                        addedDestroy = true;
                    }
                    toReturn.add(Actions.actionEntrys[662]);
                }
                toReturn.add(Actions.actionEntrys[78]);
            }
            if ((source.getTemplateId() == 315 || source.getTemplateId() == 176) && performer.getPower() >= 2) {
                toReturn.add(Actions.actionEntrys[684]);
            }
        }
        final Skills skills = performer.getSkills();
        try {
            final Skill str = skills.getSkill(102);
            if (str.getRealKnowledge() > 21.0) {
                if (!wall.isWallPlan()) {
                    if (!wall.isRubble()) {
                        toReturn.add(new ActionEntry((short)(-1), "Wall", "Wall"));
                        toReturn.add(Actions.actionEntrys[174]);
                    }
                }
                else if (structure != null && !structure.hasWalls() && !addedDestroy) {
                    toReturn.add(new ActionEntry((short)(-1), "Structure", "Structure"));
                    toReturn.add(Actions.actionEntrys[82]);
                    addedDestroy = true;
                }
            }
        }
        catch (NoSuchSkillException nss) {
            WallBehaviour.logger.log(Level.WARNING, "Weird, " + performer.getName() + " has no strength!");
        }
        if (isIndoorWallPlan(wall)) {
            toReturn.add(Actions.actionEntrys[57]);
        }
        if (source.isTrellis() && performer.getFloorLevel() == 0) {
            toReturn.add(new ActionEntry((short)(-3), "Plant", "Plant options"));
            toReturn.add(Actions.actionEntrys[746]);
            toReturn.add(new ActionEntry((short)176, "In center", "planting"));
            toReturn.add(Actions.actionEntrys[747]);
        }
        final MissionTrigger[] m2 = MissionTriggers.getMissionTriggersWith(source.getTemplateId(), 473, wall.getId());
        if (m2.length > 0) {
            toReturn.add(Actions.actionEntrys[473]);
        }
        final MissionTrigger[] m3 = MissionTriggers.getMissionTriggersWith(source.getTemplateId(), 474, wall.getId());
        if (m3.length > 0) {
            toReturn.add(Actions.actionEntrys[474]);
        }
        addWarStuff(toReturn, performer, wall);
        toReturn.addAll(addManage(performer, structure, wall));
        if (wall.isFinished() && MethodsStructure.mayModifyStructure(performer, structure, wallTile, (short)683)) {
            toReturn.add(Actions.actionEntrys[683]);
            if (wall.isPlainStone() && (source.getTemplateId() == 130 || (source.isWand() && performer.getPower() >= 4))) {
                toReturn.add(Actions.actionEntrys[847]);
            }
            else if (wall.isPlastered() && (source.getTemplateId() == 1115 || (source.isWand() && performer.getPower() >= 4))) {
                toReturn.add(new ActionEntry((short)847, "Remove render", "removing"));
            }
            if (wall.isLRArch() && (source.getTemplateId() == 1115 || (source.isWand() && performer.getPower() >= 4))) {
                toReturn.add(Actions.actionEntrys[848]);
            }
        }
        return toReturn;
    }
    
    private static final List<ActionEntry> addBuildMenu(final Creature performer, final Item source, final Wall wall, final boolean hasMarker) {
        final List<ActionEntry> hlist = new LinkedList<ActionEntry>();
        final List<List<ActionEntry>> alist = new LinkedList<List<ActionEntry>>();
        final StructureMaterialEnum[] materialsFromToolType;
        final StructureMaterialEnum[] materials = materialsFromToolType = WallEnum.getMaterialsFromToolType(source, performer);
        for (final StructureMaterialEnum material : materialsFromToolType) {
            final List<ActionEntry> mlist = new LinkedList<ActionEntry>();
            final List<WallEnum> wallTypes = WallEnum.getWallsByToolAndMaterial(performer, source, false, hasMarker || MethodsStructure.hasInsideFence(wall), material);
            if (wallTypes.size() > 0) {
                hlist.add(new ActionEntry((short)(-wallTypes.size()), Wall.getMaterialName(material), "building"));
                for (int i = 0; i < wallTypes.size(); ++i) {
                    mlist.add(wallTypes.get(i).createActionEntry());
                }
                Collections.sort(mlist);
                alist.add(mlist);
            }
        }
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        switch (hlist.size()) {
            case 0: {
                break;
            }
            case 1: {
                toReturn.add(new ActionEntry((short)(-alist.get(0).size()), "Build", "building"));
                toReturn.addAll(alist.get(0));
                break;
            }
            default: {
                toReturn.add(new ActionEntry((short)(-hlist.size()), "Build", "building"));
                int count = 0;
                for (final List<ActionEntry> zlist : alist) {
                    toReturn.add(hlist.get(count++));
                    toReturn.addAll(zlist);
                }
                break;
            }
        }
        return toReturn;
    }
    
    private static final void addWarStuff(final List<ActionEntry> toReturn, final Creature performer, final Wall wall) {
        final Village targVill = wall.getTile().getVillage();
        final Village village = performer.getCitizenVillage();
        if (village != null && village.mayDoDiplomacy(performer) && targVill != null && village != targVill) {
            final boolean atPeace = village.mayDeclareWarOn(targVill);
            if (atPeace) {
                toReturn.add(new ActionEntry((short)(-1), "Village", "Village options", WallBehaviour.emptyIntArr));
                toReturn.add(Actions.actionEntrys[209]);
            }
        }
    }
    
    public static final List<ActionEntry> addManage(final Creature performer, final Structure structure, final Wall wall) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        final List<ActionEntry> permissions = new LinkedList<ActionEntry>();
        if (structure.isFinalized()) {
            if (structure.mayManage(performer)) {
                permissions.add(Actions.actionEntrys[673]);
            }
            if (structure.mayShowPermissions(performer) || structure.isActualOwner(performer.getWurmId())) {
                permissions.add(Actions.actionEntrys[664]);
            }
            final Door door = wall.getDoor();
            if (door != null && (door.mayShowPermissions(performer) || structure.mayManage(performer) || performer.getPower() > 1)) {
                permissions.add(Actions.actionEntrys[666]);
            }
            if (structure.maySeeHistory(performer)) {
                permissions.add(Actions.actionEntrys[691]);
                if (wall.isFinished() && door != null) {
                    permissions.add(Actions.actionEntrys[692]);
                }
            }
            if (!permissions.isEmpty()) {
                if (permissions.size() > 1) {
                    Collections.sort(permissions);
                    toReturn.add(new ActionEntry((short)(-permissions.size()), "Permissions", "viewing"));
                }
                toReturn.addAll(permissions);
            }
            if (door != null && door.mayLock(performer) && door.hasLock()) {
                if (door.isLocked()) {
                    toReturn.add(Actions.actionEntrys[102]);
                }
                else {
                    toReturn.add(Actions.actionEntrys[28]);
                }
            }
        }
        return toReturn;
    }
    
    private static boolean mayLockDoor(final Creature performer, final Wall wall, final Door door) {
        return wall.isFinished() && door != null && door.mayLock(performer);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Wall wall) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        toReturn.addAll(super.getBehavioursFor(performer, wall));
        final VolaTile wallTile = Zones.getOrCreateTile(wall.getTileX(), wall.getTileY(), wall.getLayer() >= 0);
        final Structure structure = Structures.getStructureOrNull(wall.getStructureId());
        if (structure == null) {
            toReturn.addAll(Actions.getDefaultItemActions());
            return toReturn;
        }
        if (this.canRemoveWallPlan(performer, wall)) {
            toReturn.add(Actions.actionEntrys[57]);
        }
        if (structure.isFinalized() && !wall.isFinished()) {
            toReturn.add(Actions.actionEntrys[607]);
        }
        toReturn.addAll(addManage(performer, structure, wall));
        if (wall.isFinished() && MethodsStructure.mayModifyStructure(performer, structure, wallTile, (short)683)) {
            toReturn.add(Actions.actionEntrys[683]);
        }
        return toReturn;
    }
    
    private static String getConstructionMessage(final Wall wall) {
        String genus = null;
        String type = null;
        switch (wall.getType()) {
            case SOLID: {
                genus = "a";
                type = "wall";
                break;
            }
            case WINDOW: {
                genus = "a";
                type = "window";
                break;
            }
            case WIDE_WINDOW: {
                genus = "a";
                type = "wide window";
                break;
            }
            case DOOR:
            case DOUBLE_DOOR:
            case CANOPY_DOOR: {
                genus = "a";
                type = "door";
                break;
            }
            case ARCHED:
            case ARCHED_LEFT:
            case ARCHED_RIGHT:
            case ARCHED_T: {
                genus = "an";
                type = "arched wall";
                break;
            }
            case NARROW_WINDOW: {
                genus = "a";
                type = "narrow window";
                break;
            }
            case PORTCULLIS: {
                genus = "a";
                type = "portcullis";
                break;
            }
            case BARRED: {
                genus = "a";
                type = "barred wall";
                break;
            }
            case JETTY: {
                genus = "a";
                type = "jetty wall";
                break;
            }
            case BALCONY: {
                genus = "a";
                type = "balcony";
                break;
            }
            case ORIEL: {
                genus = "an";
                type = "oriel wall";
                break;
            }
            default: {
                genus = "a";
                type = "wall";
                break;
            }
        }
        final String msg = StringUtil.format("You see %s %s under construction. The %s needs ", genus, type, type);
        final int[] neededMats = WallEnum.getMaterialsNeeded(wall);
        String part2 = "";
        for (int i = 0; i < neededMats.length; i += 2) {
            try {
                final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(neededMats[i]);
                final int count = neededMats[i + 1];
                if (part2.length() > 0) {
                    part2 += ", ";
                }
                part2 = part2 + count + " " + template.sizeString + template.getPlural();
            }
            catch (NoSuchTemplateException e) {
                WallBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
            }
        }
        if (part2.length() > 0) {
            part2 += ".";
        }
        return msg + part2;
    }
    
    static final void sendQlString(final Communicator comm, final Wall wall) {
        comm.sendNormalServerMessage("QL=" + wall.getQualityLevel() + ", dam=" + wall.getDamage());
        if (comm.player != null && comm.player.getPower() > 0) {
            try {
                final Structure struct = Structures.getStructure(wall.getStructureId());
                String ownerName = "unknown";
                final PlayerInfo info = PlayerInfoFactory.getPlayerInfoWithWurmId(struct.getOwnerId());
                if (info != null) {
                    ownerName = info.getName();
                }
                comm.sendNormalServerMessage("Structure=" + wall.getStructureId() + ", owner=" + ownerName + " (" + struct.getOwnerId() + ")");
                comm.sendNormalServerMessage("Structure finished=" + struct.isFinished());
                if (comm.player.getPower() >= 5) {
                    comm.sendNormalServerMessage("wall= " + wall.getNumber() + " start[" + wall.getStartX() + "," + wall.getStartY() + "] end=[" + wall.getEndX() + "," + wall.getEndY() + "] state=" + wall.getState() + " color=" + wall.getColor() + " material=" + wall.getMaterial() + " type=" + wall.getType() + " cover=" + wall.getCover() + " walltile=[" + wall.getTile().tilex + "," + wall.getTile().tiley + "] finished=" + wall.getTile().getStructure().isFinished() + " isIndoor=" + wall.isIndoor() + " height=" + wall.getHeight() + " layer=" + wall.getLayer() + ")");
                }
            }
            catch (NoSuchStructureException ex) {}
        }
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Wall wall, final short action, final float counter) {
        final boolean done = true;
        final Structure structure = Structures.getStructureOrNull(wall.getStructureId());
        final Door door = wall.getDoor();
        if (action == 1) {
            final Communicator comm = performer.getCommunicator();
            final StructureTypeEnum type = wall.getType();
            comm.sendNormalServerMessage("Material " + wall.getMaterialString());
            if (type == StructureTypeEnum.DOOR || type == StructureTypeEnum.DOUBLE_DOOR || type == StructureTypeEnum.PORTCULLIS || type == StructureTypeEnum.CANOPY_DOOR) {
                if (!wall.isFinished()) {
                    comm.sendNormalServerMessage(getConstructionMessage(wall));
                    sendQlString(comm, wall);
                    return true;
                }
                if (structure != null) {
                    final Floor[] floors2;
                    final Floor[] floors = floors2 = structure.getFloors();
                    for (final Floor floor : floors2) {
                        if (floor.getFloorLevel() == 0 && performer.getPower() > 0) {
                            comm.sendNormalServerMessage("State=" + floor.getState() + "  x=" + floor.getTileX() + ", " + floor.getTileY() + " finished=" + floor.isFinished());
                        }
                    }
                    final Door[] doors = structure.getAllDoors();
                    for (int x = 0; x < doors.length; ++x) {
                        try {
                            if (doors[x].getWall() == wall) {
                                if (performer.getPower() > 0) {
                                    comm.sendNormalServerMessage("State=" + wall.getState() + " inner x=" + doors[x].getInnerTile().getTileX() + ", " + doors[x].getInnerTile().getTileY() + ", outer: " + doors[x].getOuterTile().getTileX() + ", y=" + doors[x].getOuterTile().getTileY());
                                }
                                try {
                                    final Item lock = doors[x].getLock();
                                    final String lockStrength = lock.getLockStrength();
                                    comm.sendNormalServerMessage("You see a door with a lock. The lock is of " + lockStrength + " quality.");
                                    if (performer.getPower() >= 5) {
                                        comm.sendNormalServerMessage("Lock WurmId=" + lock.getWurmId() + ", dam=" + lock.getDamage());
                                    }
                                    if (wall.getColor() != -1) {
                                        comm.sendNormalServerMessage("Colors: R=" + WurmColor.getColorRed(wall.getColor()) + ", G=" + WurmColor.getColorGreen(wall.getColor()) + ", B=" + WurmColor.getColorBlue(wall.getColor()) + ".");
                                    }
                                    if (doors[x].getLockCounter() > 0) {
                                        comm.sendNormalServerMessage("The door is picked open and will shut in " + doors[x].getLockCounterTime());
                                    }
                                    else if (lock.isLocked()) {
                                        comm.sendNormalServerMessage("It is locked.");
                                    }
                                    else {
                                        comm.sendNormalServerMessage("It is unlocked.");
                                    }
                                    sendQlString(comm, wall);
                                    return true;
                                }
                                catch (NoSuchLockException nsl) {
                                    comm.sendNormalServerMessage("You see a door. The door has no lock.");
                                    if (wall.getColor() != -1) {
                                        comm.sendNormalServerMessage("Colors: R=" + WurmColor.getColorRed(wall.getColor()) + ", G=" + WurmColor.getColorGreen(wall.getColor()) + ", B=" + WurmColor.getColorBlue(wall.getColor()) + ".");
                                    }
                                    if (doors[x].getLockCounter() > 0) {
                                        comm.sendNormalServerMessage("The door is picked open and will shut in " + doors[x].getLockCounter() / 2 + " seconds.");
                                    }
                                    sendQlString(comm, wall);
                                    return true;
                                }
                            }
                        }
                        catch (NoSuchWallException nsw) {
                            WallBehaviour.logger.log(Level.WARNING, "No inner wall");
                        }
                    }
                }
                else {
                    WallBehaviour.logger.log(Level.WARNING, "This wall has no structure: " + wall.getId());
                    comm.sendNormalServerMessage("This wall has a problem with its data. Please report this.");
                }
            }
            else if (type == StructureTypeEnum.NARROW_WINDOW) {
                if (wall.isFinished()) {
                    comm.sendNormalServerMessage("You see a narrow window.");
                    sendQlString(comm, wall);
                }
                else {
                    comm.sendNormalServerMessage(getConstructionMessage(wall));
                    sendQlString(comm, wall);
                }
            }
            else if (Wall.isArched(type)) {
                if (wall.isFinished()) {
                    comm.sendNormalServerMessage("You see an arched wall opening.");
                    sendQlString(comm, wall);
                }
                else {
                    comm.sendNormalServerMessage(getConstructionMessage(wall));
                    sendQlString(comm, wall);
                }
            }
            else if (type == StructureTypeEnum.SOLID) {
                if (wall.isFinished()) {
                    comm.sendNormalServerMessage("You see a wall.");
                    sendQlString(comm, wall);
                }
                else {
                    comm.sendNormalServerMessage(getConstructionMessage(wall));
                    sendQlString(comm, wall);
                }
            }
            else if (type == StructureTypeEnum.BARRED) {
                if (wall.isFinished()) {
                    comm.sendNormalServerMessage("You see a barred wall.");
                    sendQlString(comm, wall);
                }
                else {
                    comm.sendNormalServerMessage(getConstructionMessage(wall));
                    sendQlString(comm, wall);
                }
            }
            else if (type == StructureTypeEnum.BALCONY) {
                if (wall.isFinished()) {
                    comm.sendNormalServerMessage("You see a balcony.");
                    sendQlString(comm, wall);
                }
                else {
                    comm.sendNormalServerMessage(getConstructionMessage(wall));
                    sendQlString(comm, wall);
                }
            }
            else if (type == StructureTypeEnum.ORIEL) {
                if (wall.isFinished()) {
                    comm.sendNormalServerMessage("You see an oriel wall.");
                    sendQlString(comm, wall);
                }
                else {
                    comm.sendNormalServerMessage(getConstructionMessage(wall));
                    sendQlString(comm, wall);
                }
            }
            else if (type == StructureTypeEnum.JETTY) {
                if (wall.isFinished()) {
                    comm.sendNormalServerMessage("You see a jetty wall.");
                    sendQlString(comm, wall);
                }
                else {
                    comm.sendNormalServerMessage(getConstructionMessage(wall));
                    sendQlString(comm, wall);
                }
            }
            else if (type == StructureTypeEnum.WINDOW) {
                if (wall.isFinished()) {
                    comm.sendNormalServerMessage("You see a window.");
                    sendQlString(comm, wall);
                }
                else {
                    comm.sendNormalServerMessage(getConstructionMessage(wall));
                    sendQlString(comm, wall);
                }
            }
            else if (type == StructureTypeEnum.WIDE_WINDOW) {
                if (wall.isFinished()) {
                    comm.sendNormalServerMessage("You see a wide window");
                    sendQlString(comm, wall);
                }
                else {
                    comm.sendNormalServerMessage(getConstructionMessage(wall));
                    sendQlString(comm, wall);
                }
            }
            else {
                comm.sendNormalServerMessage("You see some markers for a new structure.");
            }
            if (wall.getColor() != -1) {
                comm.sendNormalServerMessage("Colors: R=" + WurmColor.getColorRed(wall.getColor()) + ", G=" + WurmColor.getColorGreen(wall.getColor()) + ", B=" + WurmColor.getColorBlue(wall.getColor()) + ".");
            }
            if (performer.getPower() >= 2) {
                if (structure != null) {
                    comm.sendNormalServerMessage("State=" + wall.getState() + ", wall id=" + wall.getId() + ", structure id=" + wall.getStructureId() + " writid=" + structure.getWritId());
                    comm.sendNormalServerMessage("Planned by " + structure.getPlanner() + ".");
                }
                else {
                    comm.sendNormalServerMessage("No Such structure " + wall.getStructureId());
                }
            }
            else if (performer.getPower() > 1) {
                comm.sendNormalServerMessage("State=" + wall.getState());
            }
        }
        else if (action == 607) {
            performer.getCommunicator().sendAddWallToCreationWindow(wall, -10L);
        }
        else if (action == 57) {
            if (this.canRemoveWallPlan(performer, wall)) {
                wall.destroy();
                performer.getCommunicator().sendNormalServerMessage("You remove a plan for a new wall.");
                Server.getInstance().broadCastAction(performer.getName() + " removes a plan for a new wall.", performer, 3);
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You are not allowed to do that!");
            }
        }
        else if (action == 82) {
            final DemolishCheckQuestion dcq = new DemolishCheckQuestion(performer, "Demolish Building", "A word of warning!", wall.getStructureId());
            dcq.sendQuestion();
        }
        else {
            if (action == 683 && !wall.isNotTurnable()) {
                return rotateWall(performer, wall, act, counter);
            }
            if (action == 209) {
                if (performer.getCitizenVillage() != null) {
                    if (wall.getTile() != null && wall.getTile().getVillage() != null) {
                        if (performer.getCitizenVillage().mayDeclareWarOn(wall.getTile().getVillage())) {
                            Methods.sendWarDeclarationQuestion(performer, wall.getTile().getVillage());
                        }
                        else {
                            performer.getCommunicator().sendAlertServerMessage(wall.getTile().getVillage().getName() + " is already at war with your village.");
                        }
                    }
                }
                else {
                    performer.getCommunicator().sendAlertServerMessage("You are no longer a citizen of a village.");
                }
            }
            else if (action == 664) {
                this.manageBuilding(performer, structure, wall);
            }
            else if (action == 666) {
                this.manageDoor(performer, structure, wall);
            }
            else if (action == 673) {
                this.manageAllDoors(performer, structure, wall);
            }
            else if (action == 691) {
                this.historyBuilding(performer, structure, wall);
            }
            else if (action == 692) {
                this.historyDoor(performer, wall, door);
            }
            else if (action == 102 && mayLockDoor(performer, wall, door)) {
                if (door != null && door.hasLock() && door.isLocked() && !door.isNotLockable()) {
                    door.unlock(true);
                    PermissionsHistories.addHistoryEntry(door.getWurmId(), System.currentTimeMillis(), performer.getWurmId(), performer.getName(), "Unlocked door");
                }
            }
            else if (action == 28 && mayLockDoor(performer, wall, door) && door != null && door.hasLock() && !door.isLocked() && !door.isNotLockable()) {
                door.lock(true);
                PermissionsHistories.addHistoryEntry(door.getWurmId(), System.currentTimeMillis(), performer.getWurmId(), performer.getName(), "Locked door");
            }
        }
        return true;
    }
    
    void manageBuilding(final Creature performer, final Structure structure, final Wall wall) {
        if (structure != null) {
            if (structure.getWritId() != -10L && structure.isActualOwner(performer.getWurmId())) {
                Items.destroyItem(structure.getWritId());
                structure.setWritid(-10L, true);
            }
            if (structure.mayShowPermissions(performer) || structure.isActualOwner(performer.getWurmId())) {
                final ManagePermissions mp = new ManagePermissions(performer, ManageObjectList.Type.BUILDING, structure, false, -10L, false, null, "");
                mp.sendQuestion();
            }
        }
    }
    
    void manageDoor(final Creature performer, final Structure structure, final Wall wall) {
        if (wall.isFinished()) {
            final Door door = wall.getDoor();
            if (door != null && (door.mayShowPermissions(performer) || structure.mayManage(performer) || performer.getPower() > 1)) {
                final ManagePermissions mp = new ManagePermissions(performer, ManageObjectList.Type.DOOR, door, false, -10L, false, null, "");
                mp.sendQuestion();
            }
        }
    }
    
    void manageAllDoors(final Creature performer, final Structure structure, final Wall wall) {
        if (structure != null && structure.mayManage(performer)) {
            final ManageObjectList mol = new ManageObjectList(performer, ManageObjectList.Type.DOOR, wall.getStructureId(), false, 1, "", true);
            mol.sendQuestion();
        }
    }
    
    void historyBuilding(final Creature performer, final Structure structure, final Wall wall) {
        if (structure != null && (structure.isOwner(performer) || performer.getPower() > 0)) {
            final PermissionsHistory ph = new PermissionsHistory(performer, wall.getStructureId());
            ph.sendQuestion();
        }
    }
    
    void historyDoor(final Creature performer, final Wall wall, final Door door) {
        if (wall.isFinished() && door != null) {
            final PermissionsHistory ph = new PermissionsHistory(performer, wall.getId());
            ph.sendQuestion();
        }
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final Wall wall, final short action, final float counter) {
        boolean done = true;
        final Structure structure = Structures.getStructureOrNull(wall.getStructureId());
        final Door door = wall.getDoor();
        if (act.isBuildHouseWallAction() && performer.isFighting()) {
            performer.getCommunicator().sendNormalServerMessage("You cannot do that while in combat.");
            performer.getCommunicator().sendActionResult(false);
            return true;
        }
        if (action == 1) {
            done = this.action(act, performer, wall, action, counter);
        }
        else {
            if (action == 647) {
                return modifyWall(performer, source, wall, act, counter);
            }
            if (action == 683) {
                return this.action(act, performer, wall, action, counter);
            }
            if (action == 847 && wall.isPlainStone() && (source.getTemplateId() == 130 || (source.isWand() && performer.getPower() >= 4))) {
                return toggleRenderWall(performer, source, wall, act, counter);
            }
            if (action == 847 && wall.isPlastered() && (source.getTemplateId() == 1115 || (source.isWand() && performer.getPower() >= 4))) {
                return toggleRenderWall(performer, source, wall, act, counter);
            }
            if (action == 848 && wall.isLRArch() && (source.getTemplateId() == 1115 || (source.isWand() && performer.getPower() >= 4))) {
                return toggleLeftRightArch(performer, source, wall, act, counter);
            }
            if (act.isBuildHouseWallAction()) {
                final WallEnum targetWallType = WallEnum.getWallByActionId(action);
                done = false;
                String buildString = "wall";
                if (act.isBuildWindowAction()) {
                    buildString = "window";
                }
                else if (act.isBuildDoorAction()) {
                    buildString = "door";
                }
                else if (act.isBuildDoubleDoorAction()) {
                    buildString = "double door";
                }
                else if (act.isBuildArchedWallAction()) {
                    buildString = "arched wall";
                }
                else if (act.isBuildPortcullisAction()) {
                    buildString = "portcullis";
                }
                else if (act.isBuildBarredWall()) {
                    buildString = "barred wall";
                }
                else if (act.isBuildBalcony()) {
                    buildString = "balcony";
                }
                else if (act.isBuildJetty()) {
                    buildString = "jetty";
                }
                else if (act.isBuildOriel()) {
                    buildString = "oriel";
                }
                else if (act.isBuildCanopyDoor()) {
                    buildString = "canopy";
                }
                final int tilex = wall.getStartX();
                final int tiley = wall.getStartY();
                VolaTile wallTile = null;
                Wall orig = null;
                boolean usesRightItem = false;
                if (MethodsStructure.isCorrectToolForBuilding(performer, source.getTemplateId()) && targetWallType.isCorrectToolForType(source, performer)) {
                    usesRightItem = true;
                }
                if (!usesRightItem) {
                    performer.getCommunicator().sendNormalServerMessage("You can't use that.");
                    performer.getCommunicator().sendActionResult(false);
                    return true;
                }
                if (structure == null) {
                    WallBehaviour.logger.log(Level.WARNING, "Structure with id " + wall.getStructureId() + " not found!");
                    performer.getCommunicator().sendActionResult(false);
                    return done;
                }
                final boolean hasMarker = StructureBehaviour.hasMarker(tilex, tiley, wall.isOnSurface(), wall.getDir(), wall.getHeight());
                if (hasMarker && targetWallType.getType() != StructureTypeEnum.ARCHED_LEFT && targetWallType.getType() != StructureTypeEnum.ARCHED_RIGHT && targetWallType.getType() != StructureTypeEnum.ARCHED_T) {
                    performer.getCommunicator().sendNormalServerMessage("You can't build those over a highway.");
                    performer.getCommunicator().sendActionResult(false);
                    return true;
                }
                if (wall.getType() == StructureTypeEnum.PLAN && structure.needsDoor() && !act.isBuildDoorAction() && !act.isBuildDoubleDoorAction() && !act.isBuildArchedWallAction() && !act.isBuildPortcullisAction() && !act.isBuildCanopyDoor()) {
                    performer.getCommunicator().sendNormalServerMessage("Houses need at least one door. Build a door first.");
                    performer.getCommunicator().sendActionResult(false);
                    return true;
                }
                for (int xx = 1; xx >= -1; --xx) {
                    for (int yy = 1; yy >= -1; --yy) {
                        try {
                            final Zone zone = Zones.getZone(tilex + xx, tiley + yy, performer.isOnSurface());
                            final VolaTile tile = zone.getTileOrNull(tilex + xx, tiley + yy);
                            if (tile != null) {
                                final Wall[] walls = tile.getWalls();
                                int s = 0;
                                while (s < walls.length) {
                                    if (walls[s].getId() == wall.getId()) {
                                        wallTile = tile;
                                        orig = walls[s];
                                        if (wallTile.getStructure() != null && !wallTile.getStructure().isFinalized()) {
                                            performer.getCommunicator().sendNormalServerMessage("You need to finalize the build plan before you start building.");
                                            performer.getCommunicator().sendActionResult(false);
                                            return done;
                                        }
                                        break;
                                    }
                                    else {
                                        ++s;
                                    }
                                }
                            }
                        }
                        catch (NoSuchZoneException ex2) {}
                    }
                }
                if (orig == null) {
                    performer.getCommunicator().sendNormalServerMessage("No structure is planned there at the moment.");
                    performer.getCommunicator().sendActionResult(false);
                    return true;
                }
                if (orig.isFinished()) {
                    performer.getCommunicator().sendNormalServerMessage("You need to destroy the " + orig.getName() + " before modifying it.");
                    performer.getCommunicator().sendActionResult(false);
                    return true;
                }
                if (!MethodsStructure.mayModifyStructure(performer, structure, wallTile, action)) {
                    performer.getCommunicator().sendNormalServerMessage("You need permission in order to make modifications to this structure.");
                    performer.getCommunicator().sendActionResult(false);
                    return true;
                }
                StructureMaterialEnum material = targetWallType.getMaterial();
                final StructureTypeEnum actionType = targetWallType.getType();
                final int primskillTemplate = targetWallType.getSkillNumber();
                if (StructureStateEnum.INITIALIZED != wall.getState() && StructureStateEnum.FINISHED != wall.getState()) {
                    if (material != wall.getMaterial()) {
                        if (source.getTemplateId() != 176 || !WurmPermissions.mayUseGMWand(performer)) {
                            performer.getCommunicator().sendNormalServerMessage("You may not change the material of the wall now that you are building it.");
                            performer.getCommunicator().sendActionResult(false);
                            return true;
                        }
                        material = wall.getMaterial();
                        performer.getCommunicator().sendNormalServerMessage("You use the power of your " + source.getName() + " to change the material of the wall!");
                    }
                    if (wall.getType() != actionType) {
                        if (source.getTemplateId() != 176 || !WurmPermissions.mayUseGMWand(performer)) {
                            performer.getCommunicator().sendNormalServerMessage("You may not change the type of wall now that you are building it.");
                            performer.getCommunicator().sendActionResult(false);
                            return true;
                        }
                        wall.setType(actionType);
                        performer.getCommunicator().sendNormalServerMessage("You use the power of your " + source.getName() + " to change the structure of the wall!");
                    }
                }
                else if (StructureStateEnum.INITIALIZED == wall.getState()) {
                    wall.setMaterial(material);
                }
                Skill carpentry = null;
                Skill hammer = null;
                try {
                    carpentry = performer.getSkills().getSkill(primskillTemplate);
                    if (primskillTemplate == 1013 && carpentry.getKnowledge(0.0) < 30.0) {
                        performer.getCommunicator().sendNormalServerMessage("You need at least 30 masonry to build stone house walls.");
                        performer.getCommunicator().sendActionResult(false);
                        return true;
                    }
                }
                catch (NoSuchSkillException nss2) {
                    if (primskillTemplate == 1013) {
                        performer.getCommunicator().sendNormalServerMessage("You need at least 30 masonry to build stone house walls.");
                        performer.getCommunicator().sendActionResult(false);
                        return true;
                    }
                    carpentry = performer.getSkills().learn(primskillTemplate, 1.0f);
                }
                if (FloorBehaviour.getRequiredBuildSkillForFloorLevel(wall.getFloorLevel(), false) > carpentry.getKnowledge(0.0)) {
                    performer.getCommunicator().sendNormalServerMessage("Construction of walls is reserved for craftsmen of higher rank than yours.");
                    if (Servers.localServer.testServer) {
                        performer.getCommunicator().sendNormalServerMessage("You have " + carpentry.getKnowledge(0.0) + " and need " + FloorBehaviour.getRequiredBuildSkillForFloorLevel(wall.getFloorLevel(), false));
                    }
                    performer.getCommunicator().sendActionResult(false);
                    return true;
                }
                try {
                    hammer = performer.getSkills().getSkill(source.getPrimarySkill());
                }
                catch (NoSuchSkillException nss2) {
                    try {
                        hammer = performer.getSkills().learn(source.getPrimarySkill(), 1.0f);
                    }
                    catch (NoSuchSkillException ex3) {}
                }
                int time = 10;
                double bonus = 0.0;
                final StructureTypeEnum oldType = orig.getType();
                final boolean immediate = (source.getTemplateId() == 176 && WurmPermissions.mayUseGMWand(performer)) || (source.getTemplateId() == 315 && performer.getPower() >= 2 && Servers.isThisATestServer());
                if (oldType == actionType && orig.isFinished()) {
                    performer.getCommunicator().sendNormalServerMessage("The wall is finished already.");
                    performer.getCommunicator().sendActionResult(false);
                    return true;
                }
                if (counter == 1.0f && !immediate) {
                    time = Actions.getSlowActionTime(performer, carpentry, source, 0.0);
                    if (checkWallItem2(performer, wall, buildString, time, act)) {
                        performer.getCommunicator().sendActionResult(false);
                        return true;
                    }
                    act.setTimeLeft(time);
                    if (oldType == actionType) {
                        performer.getCommunicator().sendNormalServerMessage("You continue to build a " + buildString + ".");
                        Server.getInstance().broadCastAction(performer.getName() + " continues to build a " + buildString + ".", performer, 5);
                    }
                    performer.sendActionControl("Building " + buildString, true, time);
                    performer.getStatus().modifyStamina(-1000.0f);
                    if (source.getTemplateId() == 63) {
                        source.setDamage(source.getDamage() + 0.0015f * source.getDamageModifier());
                    }
                    else if (source.getTemplateId() == 62) {
                        source.setDamage(source.getDamage() + 3.0E-4f * source.getDamageModifier());
                    }
                    else if (source.getTemplateId() == 493) {
                        source.setDamage(source.getDamage() + 5.0E-4f * source.getDamageModifier());
                    }
                }
                else {
                    time = act.getTimeLeft();
                    if (Math.abs(performer.getPosX() - (wall.getEndX() << 2)) > 8.0f || Math.abs(performer.getPosX() - (wall.getStartX() << 2)) > 8.0f || Math.abs(performer.getPosY() - (wall.getEndY() << 2)) > 8.0f || Math.abs(performer.getPosY() - (wall.getStartY() << 2)) > 8.0f) {
                        performer.getCommunicator().sendAlertServerMessage("You are too far from the end.");
                        performer.getCommunicator().sendActionResult(false);
                        return true;
                    }
                    if (act.currentSecond() % 5 == 0) {
                        if (wall.isStone() || wall.isPlainStone() || wall.isSlate() || wall.isRoundedStone() || wall.isPottery() || wall.isSandstone() || wall.isMarble()) {
                            SoundPlayer.playSound("sound.work.masonry", tilex, tiley, performer.isOnSurface(), 1.6f);
                        }
                        else {
                            SoundPlayer.playSound((Server.rand.nextInt(2) == 0) ? "sound.work.carpentry.mallet1" : "sound.work.carpentry.mallet2", tilex, tiley, performer.isOnSurface(), 1.6f);
                        }
                        performer.getStatus().modifyStamina(-10000.0f);
                        if (source.getTemplateId() == 63) {
                            source.setDamage(source.getDamage() + 0.0015f * source.getDamageModifier());
                        }
                        else if (source.getTemplateId() == 62) {
                            source.setDamage(source.getDamage() + 3.0E-4f * source.getDamageModifier());
                        }
                        else if (source.getTemplateId() == 493) {
                            source.setDamage(source.getDamage() + 5.0E-4f * source.getDamageModifier());
                        }
                    }
                }
                if (counter * 10.0f > time || immediate) {
                    if (!immediate && !depleteWallItems2(performer, wall, act)) {
                        performer.getCommunicator().sendActionResult(false);
                        return true;
                    }
                    if (hammer != null) {
                        hammer.skillCheck(10.0, source, 0.0, false, counter);
                        bonus = hammer.getKnowledge(source, 0.0) / 10.0;
                    }
                    carpentry.skillCheck(10.0, source, bonus, false, counter);
                    done = true;
                    try {
                        final float oldql = wall.getQualityLevel();
                        float qlevel = MethodsStructure.calculateNewQualityLevel(act.getPower(), carpentry.getKnowledge(0.0), oldql, wall.getFinalState().state);
                        qlevel = Math.max(1.0f, qlevel);
                        if (immediate) {
                            qlevel = 50.0f;
                        }
                        boolean updateOrig = false;
                        if (oldType != actionType) {
                            orig.setType(actionType);
                            orig.setDamage(0.0f);
                            qlevel = MethodsStructure.calculateNewQualityLevel(act.getPower(), carpentry.getKnowledge(0.0), 0.0f, wall.getFinalState().state);
                            orig.setState(StructureStateEnum.INITIALIZED);
                            updateOrig = true;
                        }
                        StructureStateEnum state;
                        final StructureStateEnum oldState = state = orig.getState();
                        if (state.state < 127) {
                            state = StructureStateEnum.getStateByValue((byte)(state.state + 1));
                            if (WurmPermissions.mayUseGMWand(performer) && (source.getTemplateId() == 315 || source.getTemplateId() == 176) && Servers.isThisATestServer()) {
                                state = StructureStateEnum.FINISHED;
                                qlevel = 80.0f;
                            }
                            else if (performer.getPower() >= 4 && source.getTemplateId() == 176) {
                                state = StructureStateEnum.FINISHED;
                                qlevel = 80.0f;
                            }
                        }
                        orig.setState(state);
                        orig.setQualityLevel(qlevel);
                        orig.setDamage(0.0f);
                        orig.setMaterial(material);
                        if (updateOrig || orig.isFinished()) {
                            wallTile.updateWall(orig);
                            if (performer.getDeity() != null && performer.getDeity().number == 3) {
                                performer.maybeModifyAlignment(1.0f);
                            }
                            if (wall.isFinished() && (wall.isStone() || wall.isPlainStone())) {
                                performer.achievement(525);
                            }
                            if (wall.isFinished() && wall.getFloorLevel() == 1) {
                                performer.achievement(539);
                            }
                        }
                        if (orig.isFinished()) {
                            performer.getCommunicator().sendRemoveFromCreationWindow(orig.getId());
                        }
                        else {
                            performer.getCommunicator().sendAddWallToCreationWindow(wall, orig.getId());
                        }
                        if (wall.isHalfArch() && oldState == StructureStateEnum.INITIALIZED) {
                            final String beam = (wall.isWood() || wall.isTimberFramed()) ? "a beam" : "an iron bar";
                            Server.getInstance().broadCastAction(performer.getName() + " add " + beam + " as reinforcement to the arch.", performer, 5);
                            performer.getCommunicator().sendNormalServerMessage("You add " + beam + " as reinforcement to the arch.");
                        }
                        else if (wall.isWood()) {
                            Server.getInstance().broadCastAction(performer.getName() + " nails a plank to the wall.", performer, 5);
                            performer.getCommunicator().sendNormalServerMessage("You nail a plank to the wall.");
                        }
                        else if (wall.isTimberFramed()) {
                            if (state.state < 7) {
                                Server.getInstance().broadCastAction(performer.getName() + " affixes a beam to the frame.", performer, 5);
                                performer.getCommunicator().sendNormalServerMessage("You affix a beam to the frame.");
                            }
                            else if (state.state < 17) {
                                Server.getInstance().broadCastAction(performer.getName() + " adds some clay and mixed grass to the wall.", performer, 5);
                                performer.getCommunicator().sendNormalServerMessage("You add some clay and mixed grass to the wall.");
                            }
                            else {
                                Server.getInstance().broadCastAction(performer.getName() + " reinforces the wall with more clay.", performer, 5);
                                performer.getCommunicator().sendNormalServerMessage("You reinforce the wall with more clay.");
                            }
                        }
                        else {
                            final String brickType = wall.getBrickName();
                            Server.getInstance().broadCastAction(performer.getName() + " adds a " + brickType + " and some mortar to the wall.", performer, 5);
                            performer.getCommunicator().sendNormalServerMessage("You add a " + brickType + " and some mortar to the wall.");
                        }
                        performer.getCommunicator().sendActionResult(true);
                        try {
                            orig.save();
                        }
                        catch (IOException iox) {
                            WallBehaviour.logger.log(Level.WARNING, "Failed to save wall with id " + orig.getId());
                        }
                        if ((!structure.isFinished() || !structure.isFinalFinished()) && structure.updateStructureFinishFlag()) {
                            performer.achievement(216);
                            if (!structure.isOnSurface()) {
                                performer.achievement(571);
                            }
                        }
                        if (oldType == StructureTypeEnum.DOOR || oldType == StructureTypeEnum.DOUBLE_DOOR || oldType == StructureTypeEnum.CANOPY_DOOR) {
                            final Door[] doors = structure.getAllDoors();
                            for (int x = 0; x < doors.length; ++x) {
                                if (doors[x].getWall() == wall) {
                                    structure.removeDoor(doors[x]);
                                    doors[x].removeFromTiles();
                                }
                            }
                        }
                        if ((act.isBuildDoorAction() || act.isBuildDoubleDoorAction() || act.isBuildPortcullisAction() || act.isBuildCanopyDoor()) && orig.isFinished()) {
                            final Door newDoor = new DbDoor(orig);
                            newDoor.setStructureId(structure.getWurmId());
                            structure.addDoor(newDoor);
                            newDoor.setIsManaged(true, (Player)performer);
                            newDoor.save();
                            newDoor.addToTiles();
                        }
                    }
                    catch (Exception ex) {
                        WallBehaviour.logger.log(Level.WARNING, "Error when building wall:", ex);
                        performer.getCommunicator().sendNormalServerMessage("An error occured on the server when building wall. Please tell the administrators.");
                        performer.getCommunicator().sendActionResult(false);
                    }
                }
            }
            else if (action == 58) {
                final int tilex2 = wall.getTileX();
                final int tiley2 = wall.getTileY();
                MethodsStructure.tryToFinalize(performer, tilex2, tiley2);
            }
            else if (action == 57) {
                if (this.canRemoveWallPlan(performer, wall)) {
                    wall.destroy();
                    performer.getCommunicator().sendNormalServerMessage("You remove a plan for a new wall.");
                    Server.getInstance().broadCastAction(performer.getName() + " removes a plan for a new wall.", performer, 3);
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("This would cause a section of the structure to crash down since it lacks support.");
                }
            }
            else if (action == 209) {
                done = true;
                if (performer.getCitizenVillage() != null) {
                    if (wall.getTile() != null && wall.getTile().getVillage() != null) {
                        if (performer.getCitizenVillage().mayDeclareWarOn(wall.getTile().getVillage())) {
                            Methods.sendWarDeclarationQuestion(performer, wall.getTile().getVillage());
                        }
                        else {
                            performer.getCommunicator().sendAlertServerMessage(wall.getTile().getVillage().getName() + " is already at war with your village.");
                        }
                    }
                }
                else {
                    performer.getCommunicator().sendAlertServerMessage("You are no longer a citizen of a village.");
                }
            }
            else if (action == 161 && source.isLock() && source.getTemplateId() == 167) {
                if (source.isLocked()) {
                    performer.getCommunicator().sendNormalServerMessage("The " + source.getName() + " is already in use.");
                    return true;
                }
                if (wall.getType() == StructureTypeEnum.DOOR || wall.getType() == StructureTypeEnum.DOUBLE_DOOR || wall.getType() == StructureTypeEnum.PORTCULLIS || wall.getType() == StructureTypeEnum.CANOPY_DOOR) {
                    done = false;
                    Skill carpentry2 = null;
                    try {
                        carpentry2 = performer.getSkills().getSkill(1005);
                    }
                    catch (NoSuchSkillException nss3) {
                        carpentry2 = performer.getSkills().learn(1005, 1.0f);
                    }
                    int time2 = 10;
                    if (counter == 1.0f) {
                        if (structure != null) {
                            if (!structure.mayModify(performer)) {
                                return true;
                            }
                        }
                        else {
                            WallBehaviour.logger.log(Level.WARNING, "This wall has no structure: " + wall.getId());
                            performer.getCommunicator().sendNormalServerMessage("This wall has a problem with its data. Please report this.");
                        }
                        time2 = (int)Math.max(100.0, (100.0 - carpentry2.getKnowledge(source, 0.0)) * 3.0);
                        try {
                            performer.getCurrentAction().setTimeLeft(time2);
                        }
                        catch (NoSuchActionException nsa) {
                            WallBehaviour.logger.log(Level.INFO, "This action does not exist?", nsa);
                        }
                        performer.getCommunicator().sendNormalServerMessage("You start to attach the lock.");
                        Server.getInstance().broadCastAction(performer.getName() + " starts to attach a lock.", performer, 5);
                        performer.sendActionControl(Actions.actionEntrys[161].getVerbString(), true, time2);
                    }
                    else {
                        try {
                            time2 = performer.getCurrentAction().getTimeLeft();
                        }
                        catch (NoSuchActionException nsa) {
                            WallBehaviour.logger.log(Level.INFO, "This action does not exist?", nsa);
                        }
                        if (counter * 10.0f > time2) {
                            carpentry2.skillCheck(100.0f - source.getCurrentQualityLevel(), 0.0, false, counter);
                            done = true;
                            if (structure != null) {
                                final long parentId = source.getParentId();
                                if (parentId != -10L) {
                                    try {
                                        Items.getItem(parentId).dropItem(source.getWurmId(), false);
                                    }
                                    catch (NoSuchItemException nsi) {
                                        WallBehaviour.logger.log(Level.INFO, performer.getName() + " tried to attach nonexistant lock or lock with no parent.");
                                    }
                                }
                                else {
                                    WallBehaviour.logger.log(Level.INFO, performer.getName() + " tried to attach lock with no parent.");
                                    performer.getCommunicator().sendNormalServerMessage("You may not use that lock.");
                                }
                                source.addKey(structure.getWritId());
                                final Door[] doors2 = structure.getAllDoors();
                                for (int x2 = 0; x2 < doors2.length; ++x2) {
                                    try {
                                        if (doors2[x2].getWall() == wall) {
                                            if (!doors2[x2].isNotLockable()) {
                                                try {
                                                    final Item oldlock = doors2[x2].getLock();
                                                    oldlock.removeKey(structure.getWritId());
                                                    oldlock.unlock();
                                                    performer.getInventory().insertItem(oldlock);
                                                }
                                                catch (NoSuchLockException ex4) {}
                                                doors2[x2].setLock(source.getWurmId());
                                                source.lock();
                                                PermissionsHistories.addHistoryEntry(doors2[x2].getWurmId(), System.currentTimeMillis(), performer.getWurmId(), performer.getName(), "Attached lock to door");
                                                Server.getInstance().broadCastAction(performer.getName() + " attaches the lock.", performer, 5);
                                                performer.getCommunicator().sendNormalServerMessage("You attach the lock and lock the door.");
                                            }
                                            break;
                                        }
                                    }
                                    catch (NoSuchWallException nsw) {
                                        WallBehaviour.logger.log(Level.WARNING, "No inner wall");
                                    }
                                }
                            }
                            else {
                                WallBehaviour.logger.log(Level.WARNING, "This wall has no structure: " + wall.getId());
                                performer.getCommunicator().sendNormalServerMessage("This wall has a problem with its data. Please report this.");
                            }
                        }
                    }
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You can only attach locks to doors and fence gates.");
                }
            }
            else if (action == 101) {
                if ((wall.isOnPvPServer() || Servers.isThisATestServer()) && (wall.getType() == StructureTypeEnum.DOOR || wall.getType() == StructureTypeEnum.DOUBLE_DOOR || wall.getType() == StructureTypeEnum.PORTCULLIS || wall.getType() == StructureTypeEnum.CANOPY_DOOR) && wall.isFinished() && !wall.isNotLockpickable()) {
                    if (structure != null) {
                        final Door[] doors3 = structure.getAllDoors();
                        for (int x3 = 0; x3 < doors3.length; ++x3) {
                            try {
                                if (doors3[x3].getWall() == wall) {
                                    done = false;
                                    done = MethodsStructure.picklock(performer, source, doors3[x3], wall.getName(), counter, act);
                                    break;
                                }
                            }
                            catch (NoSuchWallException nsw2) {
                                WallBehaviour.logger.log(Level.WARNING, "No inner wall");
                            }
                        }
                    }
                    else {
                        WallBehaviour.logger.log(Level.WARNING, "This wall has no structure: " + wall.getId());
                        performer.getCommunicator().sendNormalServerMessage("This wall has a problem with its data. Please report this.");
                    }
                }
            }
            else if (action == 193) {
                done = ((Servers.localServer.challengeServer && performer.getEnemyPresense() > 0) || wall.isNoRepair() || MethodsStructure.repairWall(act, performer, source, wall, counter));
            }
            else if (action == 192) {
                done = (source == null || wall.isNoImprove() || MethodsStructure.improveWall(act, performer, source, wall, counter));
            }
            else if (action == 180) {
                if (performer.getPower() >= 2) {
                    performer.getLogger().log(Level.INFO, performer.getName() + " destroyed a wall at " + wall.getTileX() + ", " + wall.getTileY());
                    wall.setDamage(100.0f);
                    done = true;
                    performer.getCommunicator().sendNormalServerMessage("You deal a lot of damage to the wall!");
                }
            }
            else if (action == 174 && !wall.isIndestructible()) {
                if (wall.isRubble()) {
                    performer.getCommunicator().sendNormalServerMessage("The rubble will clear by itself soon.");
                    return true;
                }
                final int tilex2 = wall.getStartX();
                final int tiley2 = wall.getStartY();
                VolaTile wallTile2 = null;
                for (int xx2 = 1; xx2 >= -1; --xx2) {
                    for (int yy2 = 1; yy2 >= -1; --yy2) {
                        try {
                            final Zone zone2 = Zones.getZone(tilex2 + xx2, tiley2 + yy2, wall.isOnSurface());
                            final VolaTile tile2 = zone2.getTileOrNull(tilex2 + xx2, tiley2 + yy2);
                            if (tile2 != null) {
                                final Wall[] walls2 = tile2.getWalls();
                                for (int s2 = 0; s2 < walls2.length; ++s2) {
                                    if (walls2[s2].getId() == wall.getId()) {
                                        wallTile2 = tile2;
                                        break;
                                    }
                                }
                            }
                        }
                        catch (NoSuchZoneException ex5) {}
                    }
                }
                if (wallTile2 == null) {
                    performer.getCommunicator().sendNormalServerMessage("You fail to destroy the wall.");
                    return true;
                }
                done = MethodsStructure.destroyWall(action, performer, source, wall, false, counter);
            }
            else if (action == 231) {
                if (!wall.isFinished()) {
                    performer.getCommunicator().sendNormalServerMessage("Finish the wall first.");
                    return true;
                }
                if (!Methods.isActionAllowed(performer, action, wall.getTileX(), wall.getTileY())) {
                    performer.getCommunicator().sendNormalServerMessage("You are not allowed to paint this wall.");
                    return true;
                }
                if (wall.isNotPaintable()) {
                    performer.getCommunicator().sendNormalServerMessage("You are not allowed to paint this wall.");
                    return true;
                }
                done = MethodsStructure.colorWall(performer, source, wall, act);
            }
            else if (action == 232) {
                if (!Methods.isActionAllowed(performer, action, wall.getTileX(), wall.getTileY())) {
                    performer.getCommunicator().sendNormalServerMessage("You are not allowed to remove the paint from this wall.");
                    return true;
                }
                done = MethodsStructure.removeColor(performer, source, wall, act);
            }
            else if (action == 82) {
                final DemolishCheckQuestion dcq = new DemolishCheckQuestion(performer, "Demolish Building", "A word of warning!", wall.getStructureId());
                dcq.sendQuestion();
            }
            else {
                if (action == 662) {
                    if (performer.getPower() >= 2) {
                        wall.setIndoor(!wall.isIndoor());
                        performer.getCommunicator().sendNormalServerMessage("Wall toggled and now is " + (wall.isIndoor() ? "Inside" : "Outside"));
                        if (structure != null) {
                            structure.updateStructureFinishFlag();
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("The structural integrity of the building is at risk.");
                            WallBehaviour.logger.log(Level.WARNING, "Structure not found while trying to toggle a wall at [" + wall.getStartX() + "," + wall.getStartY() + "]");
                        }
                    }
                    return true;
                }
                if (action == 78) {
                    if (performer.getPower() >= 2) {
                        try {
                            final Structure struct = Structures.getStructure(wall.getStructureId());
                            try {
                                Items.getItem(struct.getWritId());
                                performer.getCommunicator().sendNormalServerMessage("Writ item exists for structure.");
                            }
                            catch (NoSuchItemException nss4) {
                                performer.getCommunicator().sendNormalServerMessage("Writ item does not exist for structure. Replacing.");
                                try {
                                    final Item newWrit = ItemFactory.createItem(166, 80.0f + Server.rand.nextFloat() * 20.0f, performer.getName());
                                    newWrit.setDescription(struct.getName());
                                    performer.getInventory().insertItem(newWrit);
                                    struct.setWritid(newWrit.getWurmId(), true);
                                }
                                catch (NoSuchTemplateException nst) {
                                    performer.getCommunicator().sendNormalServerMessage("Failed replace:" + nst.getMessage());
                                }
                                catch (FailedException enst) {
                                    performer.getCommunicator().sendNormalServerMessage("Failed replace:" + enst.getMessage());
                                }
                            }
                        }
                        catch (NoSuchStructureException nss) {
                            WallBehaviour.logger.log(Level.WARNING, nss.getMessage(), nss);
                            performer.getCommunicator().sendNormalServerMessage("No such structure. Bug. Good luck.");
                        }
                    }
                }
                else if (action == 472) {
                    done = true;
                    if (source.getTemplateId() == 676 && source.getOwnerId() == performer.getWurmId()) {
                        final MissionManager m = new MissionManager(performer, "Manage missions", "Select action", wall.getId(), wall.getName(), source.getWurmId());
                        m.sendQuestion();
                    }
                }
                else if (action == 90) {
                    if (performer.getPower() < 4) {
                        WallBehaviour.logger.log(Level.WARNING, "Possible hack attempt by " + performer.getName() + " calling Actions.POLL on wall in WallBehaviour without enough power.");
                        return true;
                    }
                    final int tilex2 = wall.getStartX();
                    final int tiley2 = wall.getStartY();
                    final VolaTile wallTile2 = Zones.getOrCreateTile(tilex2, tiley2, true);
                    if (wallTile2 != null) {
                        Structure struct2 = null;
                        try {
                            struct2 = Structures.getStructure(wall.getStructureId());
                        }
                        catch (NoSuchStructureException e) {
                            WallBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
                        }
                        if (struct2 == null) {
                            performer.getCommunicator().sendNormalServerMessage("Couldn't find structure for wall '" + wall.getId() + "'.");
                            return true;
                        }
                        wall.poll(struct2.getCreationDate() + 604800000L, wallTile2, struct2);
                        performer.getCommunicator().sendNormalServerMessage("Poll performed for wall '" + wall.getId() + "'.");
                    }
                    else {
                        performer.getCommunicator().sendNormalServerMessage("Unexpectedly missing a tile for " + tilex2 + "," + tiley2 + ".");
                    }
                }
                else if (action == 664) {
                    this.manageBuilding(performer, structure, wall);
                }
                else if (action == 666) {
                    this.manageDoor(performer, structure, wall);
                }
                else if (action == 673) {
                    this.manageAllDoors(performer, structure, wall);
                }
                else if (action == 102 && mayLockDoor(performer, wall, door)) {
                    if (door != null && door.hasLock() && door.isLocked() && !door.isNotLockable()) {
                        door.unlock(true);
                        PermissionsHistories.addHistoryEntry(door.getWurmId(), System.currentTimeMillis(), performer.getWurmId(), performer.getName(), "Unlocked door");
                    }
                }
                else if (action == 28 && mayLockDoor(performer, wall, door)) {
                    if (door != null && door.hasLock() && !door.isLocked() && !door.isNotLockable()) {
                        door.lock(true);
                        PermissionsHistories.addHistoryEntry(door.getWurmId(), System.currentTimeMillis(), performer.getWurmId(), performer.getName(), "Locked door");
                    }
                }
                else {
                    if (action == 866) {
                        if (performer.getPower() >= 4) {
                            Methods.sendGmBuildAllWallsQuestion(performer, structure);
                        }
                        return true;
                    }
                    if (action == 684) {
                        if ((source.getTemplateId() == 315 || source.getTemplateId() == 176) && performer.getPower() >= 2) {
                            Methods.sendItemRestrictionManagement(performer, wall, wall.getId());
                        }
                        else {
                            WallBehaviour.logger.log(Level.WARNING, performer.getName() + " hacking the protocol by trying to set the restrictions of " + wall + ", counter: " + counter + '!');
                        }
                        return true;
                    }
                    if (source.isTrellis() && (action == 176 || action == 746 || action == 747)) {
                        done = Terraforming.plantTrellis(performer, source, wall.getMinX(), wall.getMinY(), wall.isOnSurface(), wall.getDir(), action, counter, act);
                    }
                }
            }
        }
        return done;
    }
    
    static final int getItemTemplateWeightInGrams(final int itemTemplateId) {
        int neededTemplateWeightGrams = 0;
        try {
            neededTemplateWeightGrams = ItemTemplateFactory.getInstance().getTemplate(itemTemplateId).getWeightGrams();
        }
        catch (NoSuchTemplateException nst) {
            switch (itemTemplateId) {
                case 22: {
                    neededTemplateWeightGrams = 2000;
                    break;
                }
                case 217: {
                    neededTemplateWeightGrams = 300;
                    break;
                }
                case 492: {
                    neededTemplateWeightGrams = 2000;
                    break;
                }
                case 132: {
                    neededTemplateWeightGrams = 150000;
                    break;
                }
                default: {
                    neededTemplateWeightGrams = 150000;
                    break;
                }
            }
        }
        return neededTemplateWeightGrams;
    }
    
    static final boolean toggleRenderWall(final Creature performer, final Item tool, final Wall wall, final Action act, final float counter) {
        final boolean insta = tool.isWand() && performer.getPower() >= 4;
        final VolaTile wallTile = getWallTile(wall);
        if (wallTile == null) {
            return true;
        }
        final Structure structure = wallTile.getStructure();
        if (!insta && structure != null && !MethodsStructure.mayModifyStructure(performer, structure, wallTile, (short)683)) {
            performer.getCommunicator().sendNormalServerMessage("You are not allowed to modify the structure.");
            return true;
        }
        if (!Methods.isActionAllowed(performer, (short)116, wallTile.getTileX(), wallTile.getTileY())) {
            return true;
        }
        if (wall.isPlainStone() && !insta && tool.getWeightGrams() < 20000) {
            performer.getCommunicator().sendNormalServerMessage("It takes 20kg of " + tool.getName() + " to render the " + wall.getName() + ".");
            return true;
        }
        int time = 40;
        if (counter == 1.0f) {
            final String render = wall.isPlainStone() ? "render" : "remove the render from";
            final String action = wall.isPlainStone() ? "rendering wall" : "removing the wall render";
            act.setTimeLeft(time);
            performer.sendActionControl(action, true, time);
            performer.getCommunicator().sendNormalServerMessage(StringUtil.format("You start to " + render + " the %s.", wall.getName()));
            Server.getInstance().broadCastAction(StringUtil.format("%s starts to " + render + " the %s.", performer.getName(), wall.getName()), performer, 5);
            return false;
        }
        time = act.getTimeLeft();
        if (counter * 10.0f > time || insta) {
            final String render = wall.isPlainStone() ? "render" : "remove the render from";
            final String renders = wall.isPlainStone() ? "renders" : "removes the render from";
            performer.getCommunicator().sendNormalServerMessage(StringUtil.format("You " + render + " the %s.", wall.getName()));
            Server.getInstance().broadCastAction(StringUtil.format("%s " + renders + " the %s.", performer.getName(), wall.getName()), performer, 5);
            if (wall.isPlainStone() && !insta) {
                tool.setWeight(tool.getWeightGrams() - 20000, true);
            }
            if (wall.isPlainStone()) {
                wall.setMaterial(StructureMaterialEnum.RENDERED);
            }
            else {
                wall.setMaterial(StructureMaterialEnum.PLAIN_STONE);
            }
            try {
                wall.save();
            }
            catch (IOException e) {
                WallBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
            }
            wallTile.updateWall(wall);
            return true;
        }
        return false;
    }
    
    static final boolean toggleLeftRightArch(final Creature performer, final Item tool, final Wall wall, final Action act, final float counter) {
        final boolean insta = tool.isWand() && performer.getPower() >= 4;
        final VolaTile wallTile = getWallTile(wall);
        if (wallTile == null) {
            return true;
        }
        final Structure structure = wallTile.getStructure();
        if (!insta && !MethodsStructure.mayModifyStructure(performer, structure, wallTile, (short)683)) {
            performer.getCommunicator().sendNormalServerMessage("You are not allowed to modify the structure.");
            return true;
        }
        if (!Methods.isActionAllowed(performer, (short)116, wallTile.getTileX(), wallTile.getTileY())) {
            return true;
        }
        int time = 40;
        if (counter == 1.0f) {
            act.setTimeLeft(time);
            performer.sendActionControl("Moving Arch", true, time);
            performer.getCommunicator().sendNormalServerMessage(StringUtil.format("You start to move the %s.", wall.getName()));
            Server.getInstance().broadCastAction(StringUtil.format("%s starts to move the %s.", performer.getName(), wall.getName()), performer, 5);
            return false;
        }
        time = act.getTimeLeft();
        if (counter * 10.0f > time || insta) {
            performer.getCommunicator().sendNormalServerMessage(StringUtil.format("You move the %s.", wall.getName()));
            Server.getInstance().broadCastAction(StringUtil.format("%s move the %s.", performer.getName(), wall.getName()), performer, 5);
            if (wall.getType() == StructureTypeEnum.ARCHED_LEFT) {
                wall.setType(StructureTypeEnum.ARCHED_RIGHT);
            }
            else {
                wall.setType(StructureTypeEnum.ARCHED_LEFT);
            }
            try {
                wall.save();
            }
            catch (IOException e) {
                WallBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
            }
            wallTile.updateWall(wall);
            return true;
        }
        return false;
    }
    
    static final boolean rotateWall(final Creature performer, final Wall wall, final Action act, final float counter) {
        final boolean insta = performer.getPower() >= 4;
        final VolaTile wallTile = getWallTile(wall);
        if (wallTile == null) {
            return true;
        }
        final Structure structure = wallTile.getStructure();
        if (!insta && !MethodsStructure.mayModifyStructure(performer, structure, wallTile, (short)683)) {
            performer.getCommunicator().sendNormalServerMessage("You are not allowed to modify the structure.");
            return true;
        }
        if (!Methods.isActionAllowed(performer, (short)116, wallTile.getTileX(), wallTile.getTileY())) {
            return true;
        }
        int time = 40;
        if (counter == 1.0f) {
            act.setTimeLeft(time);
            performer.sendActionControl("Rotating wall", true, time);
            performer.getCommunicator().sendNormalServerMessage(StringUtil.format("You start to rotate the %s.", wall.getName()));
            Server.getInstance().broadCastAction(StringUtil.format("%s starts to rotate the %s.", performer.getName(), wall.getName()), performer, 5);
            return false;
        }
        time = act.getTimeLeft();
        if (counter * 10.0f > time || insta) {
            performer.getCommunicator().sendNormalServerMessage(StringUtil.format("You rotate the %s.", wall.getName()));
            Server.getInstance().broadCastAction(StringUtil.format("%s rotates the %s.", performer.getName(), wall.getName()), performer, 5);
            wall.setWallOrientation(!wall.getWallOrientationFlag());
            return true;
        }
        return false;
    }
    
    @Nullable
    static final VolaTile getWallTile(final Wall wall) {
        final int tilex = wall.getStartX();
        final int tiley = wall.getStartY();
        for (int xx = 1; xx >= -1; --xx) {
            for (int yy = 1; yy >= -1; --yy) {
                try {
                    final Zone zone = Zones.getZone(tilex + xx, tiley + yy, wall.isOnSurface());
                    final VolaTile tile = zone.getTileOrNull(tilex + xx, tiley + yy);
                    if (tile != null) {
                        final Wall[] walls = tile.getWalls();
                        for (int s = 0; s < walls.length; ++s) {
                            if (walls[s].getId() == wall.getId()) {
                                return tile;
                            }
                        }
                    }
                }
                catch (NoSuchZoneException ex) {}
            }
        }
        return null;
    }
    
    static final boolean modifyWall(final Creature performer, final Item tool, final Wall wall, final Action act, final float counter) {
        if (!isValidModifyableWall(wall, tool)) {
            return true;
        }
        final int tilex = wall.getStartX();
        final int tiley = wall.getStartY();
        VolaTile wallTile = null;
        for (int xx = 1; xx >= -1; --xx) {
            for (int yy = 1; yy >= -1; --yy) {
                try {
                    final Zone zone = Zones.getZone(tilex + xx, tiley + yy, performer.isOnSurface());
                    final VolaTile tile = zone.getTileOrNull(tilex + xx, tiley + yy);
                    if (tile != null) {
                        final Wall[] walls = tile.getWalls();
                        int s = 0;
                        while (s < walls.length) {
                            if (walls[s].getId() == wall.getId()) {
                                wallTile = tile;
                                if (wallTile.getStructure() != null && !wallTile.getStructure().isFinalized()) {
                                    performer.getCommunicator().sendNormalServerMessage("You need to finalize the build plan before you start building.");
                                    performer.getCommunicator().sendActionResult(false);
                                    return true;
                                }
                                break;
                            }
                            else {
                                ++s;
                            }
                        }
                    }
                }
                catch (NoSuchZoneException ex) {}
            }
        }
        if (wallTile == null) {
            return true;
        }
        final Structure structure = wallTile.getStructure();
        if (!MethodsStructure.mayModifyStructure(performer, structure, wallTile, act.getNumber())) {
            performer.getCommunicator().sendNormalServerMessage("You are not allowed to modify the structure.");
            return true;
        }
        if (!Methods.isActionAllowed(performer, (short)116, wallTile.getTileX(), wallTile.getTileY())) {
            return true;
        }
        int time = 40;
        if (counter == 1.0f) {
            final String action = (tool.getTemplateId() == 219) ? "removing decoration" : "adding decoration";
            final String modify = (tool.getTemplateId() == 219) ? "remove the decorations from the " : "add decorations to the ";
            act.setTimeLeft(time);
            performer.sendActionControl(action, true, time);
            performer.getCommunicator().sendNormalServerMessage("You start to " + modify + wall.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + " starts to " + modify + wall.getName() + ".", performer, 5);
            return false;
        }
        time = act.getTimeLeft();
        if (counter * 10.0f > time) {
            final String modify2 = (tool.getTemplateId() == 219) ? "removed the decorations from the " : "added decorations to the ";
            performer.getCommunicator().sendNormalServerMessage("You " + modify2 + wall.getName() + ".");
            Server.getInstance().broadCastAction(performer.getName() + modify2 + wall.getName() + ".", performer, 5);
            wall.setMaterial((wall.getMaterial() == StructureMaterialEnum.STONE) ? StructureMaterialEnum.PLAIN_STONE : StructureMaterialEnum.STONE);
            wallTile.updateWall(wall);
            return true;
        }
        return false;
    }
    
    static final boolean checkWallItem2(final Creature performer, final Wall wall, final String buildString, final int time, final Action act) {
        WallBehaviour.inventoryItems = performer.getInventory().getAllItems(false);
        WallBehaviour.bodyItems = performer.getBody().getAllItems();
        final int[] neededTemplates = wall.getTemplateIdsNeededForNextState(WallEnum.getWallByActionId(act.getNumber()).getType());
        resetFoundTemplates(neededTemplates);
        for (int x = 0; x < WallBehaviour.inventoryItems.length; ++x) {
            for (int n = 0; n < neededTemplates.length; ++n) {
                if (WallBehaviour.inventoryItems[x].getTemplateId() == neededTemplates[n]) {
                    final int neededTemplateWeightGrams = getItemTemplateWeightInGrams(WallBehaviour.inventoryItems[x].getTemplateId());
                    if (neededTemplateWeightGrams <= WallBehaviour.inventoryItems[x].getWeightGrams()) {
                        WallBehaviour.foundTemplates[n] = neededTemplates[n];
                    }
                }
            }
        }
        boolean found = true;
        for (int f = 0; f < WallBehaviour.foundTemplates.length; ++f) {
            if (WallBehaviour.foundTemplates[f] == -1) {
                found = false;
            }
        }
        if (!found) {
            for (int x2 = 0; x2 < WallBehaviour.bodyItems.length; ++x2) {
                for (int n2 = 0; n2 < neededTemplates.length; ++n2) {
                    if (WallBehaviour.bodyItems[x2].getTemplateId() == neededTemplates[n2]) {
                        final int neededTemplateWeightGrams2 = getItemTemplateWeightInGrams(WallBehaviour.bodyItems[x2].getTemplateId());
                        if (neededTemplateWeightGrams2 <= WallBehaviour.bodyItems[x2].getWeightGrams()) {
                            WallBehaviour.foundTemplates[n2] = neededTemplates[n2];
                        }
                    }
                }
            }
            found = true;
            for (int f = 0; f < WallBehaviour.foundTemplates.length; ++f) {
                if (WallBehaviour.foundTemplates[f] == -1) {
                    found = false;
                }
            }
        }
        for (int n = 0; n < WallBehaviour.foundTemplates.length; ++n) {
            if (WallBehaviour.foundTemplates[n] == -1) {
                try {
                    if (neededTemplates[n] == 217) {
                        performer.getCommunicator().sendNormalServerMessage("You need large iron nails.");
                    }
                    else {
                        final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(neededTemplates[n]);
                        if (template.isCombine()) {
                            performer.getCommunicator().sendNormalServerMessage("You need " + template.getName() + ".");
                        }
                        else {
                            performer.getCommunicator().sendNormalServerMessage("You need " + template.getNameWithGenus() + ".");
                        }
                    }
                }
                catch (NoSuchTemplateException nst) {
                    WallBehaviour.logger.log(Level.WARNING, nst.getMessage(), nst);
                }
                found = false;
            }
        }
        if (!found) {
            return true;
        }
        if (wall.getState() == StructureStateEnum.INITIALIZED) {
            final String a_an = (buildString.charAt(0) == 'a') ? "an" : "a";
            Server.getInstance().broadCastAction(performer.getName() + " starts to build " + a_an + " " + buildString + ".", performer, 5);
            performer.getCommunicator().sendNormalServerMessage("You start to build " + a_an + " " + buildString + ".");
            performer.sendActionControl("Building " + buildString, true, time);
        }
        return false;
    }
    
    static final void resetFoundTemplates(final int[] needed) {
        WallBehaviour.foundTemplates = new int[needed.length];
        for (int x = 0; x < WallBehaviour.foundTemplates.length; ++x) {
            WallBehaviour.foundTemplates[x] = -1;
        }
    }
    
    static final boolean depleteWallItems2(final Creature performer, final Wall wall, final Action act) {
        WallBehaviour.inventoryItems = performer.getInventory().getAllItems(false);
        WallBehaviour.bodyItems = performer.getBody().getAllItems();
        float qlevel = 0.0f;
        final int[] neededTemplates = wall.getTemplateIdsNeededForNextState(WallEnum.getWallByActionId(act.getNumber()).getType());
        resetFoundTemplates(neededTemplates);
        final Item[] depleteItems = new Item[neededTemplates.length];
        for (int i = 0; i < neededTemplates.length; ++i) {
            if (WallBehaviour.foundTemplates[i] == -1) {
                for (int j = 0; j < WallBehaviour.inventoryItems.length; ++j) {
                    if (WallBehaviour.inventoryItems[j].getTemplateId() == neededTemplates[i]) {
                        final int neededTemplateWeightGrams = getItemTemplateWeightInGrams(WallBehaviour.inventoryItems[j].getTemplateId());
                        if (neededTemplateWeightGrams <= WallBehaviour.inventoryItems[j].getWeightGrams()) {
                            depleteItems[i] = WallBehaviour.inventoryItems[j];
                            WallBehaviour.foundTemplates[i] = neededTemplates[i];
                            break;
                        }
                    }
                }
            }
        }
        boolean allInitialized = true;
        for (int k = 0; k < WallBehaviour.foundTemplates.length; ++k) {
            if (WallBehaviour.foundTemplates[k] == -1) {
                allInitialized = false;
                break;
            }
        }
        if (!allInitialized) {
            for (int k = 0; k < neededTemplates.length; ++k) {
                if (WallBehaviour.foundTemplates[k] == -1) {
                    for (int l = 0; l < WallBehaviour.bodyItems.length; ++l) {
                        if (WallBehaviour.bodyItems[l].getTemplateId() == neededTemplates[k]) {
                            final int neededTemplateWeightGrams2 = getItemTemplateWeightInGrams(WallBehaviour.bodyItems[l].getTemplateId());
                            if (neededTemplateWeightGrams2 <= WallBehaviour.bodyItems[l].getWeightGrams()) {
                                depleteItems[k] = WallBehaviour.bodyItems[l];
                                WallBehaviour.foundTemplates[k] = neededTemplates[k];
                                break;
                            }
                        }
                    }
                }
            }
            allInitialized = true;
            for (int k = 0; k < WallBehaviour.foundTemplates.length; ++k) {
                if (WallBehaviour.foundTemplates[k] == -1) {
                    allInitialized = false;
                    break;
                }
            }
        }
        if (!allInitialized) {
            for (int k = 0; k < WallBehaviour.foundTemplates.length; ++k) {
                if (WallBehaviour.foundTemplates[k] == -1) {
                    try {
                        final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(neededTemplates[k]);
                        performer.getCommunicator().sendNormalServerMessage("You did not have enough " + template.getPlural() + ".");
                    }
                    catch (NoSuchTemplateException nst) {
                        WallBehaviour.logger.log(Level.WARNING, nst.getMessage(), nst);
                    }
                }
            }
            return false;
        }
        for (int k = 0; k < depleteItems.length; ++k) {
            try {
                Items.getItem(depleteItems[k].getWurmId());
            }
            catch (NoSuchItemException nsie) {
                performer.getCommunicator().sendAlertServerMessage("ERROR: " + depleteItems[k].getName() + " not found, WurmID: " + depleteItems[k].getWurmId());
                return false;
            }
            if (depleteItems[k].isCombine()) {
                depleteItems[k].setWeight(depleteItems[k].getWeightGrams() - depleteItems[k].getTemplate().getWeightGrams(), true);
            }
            else {
                Items.destroyItem(depleteItems[k].getWurmId());
            }
            qlevel += depleteItems[k].getCurrentQualityLevel() / 21.0f;
        }
        act.setPower(qlevel);
        return true;
    }
    
    private static boolean isIndoorWallPlan(final Wall aWall) {
        return aWall.isIndoor() && aWall.isWallPlan();
    }
    
    private boolean canRemoveWallPlan(final Creature aPerformer, final Wall aWall) {
        if (!isIndoorWallPlan(aWall)) {
            return false;
        }
        if (!Methods.isActionAllowed(aPerformer, (short)57, aWall.getTileX(), aWall.getTileY())) {
            return false;
        }
        try {
            final Structure struct = Structures.getStructure(aWall.getStructureId());
            if (struct.wouldCreateFlyingStructureIfRemoved(aWall)) {
                return false;
            }
        }
        catch (NoSuchStructureException nsc) {
            return true;
        }
        return true;
    }
    
    static {
        logger = Logger.getLogger(WallBehaviour.class.getName());
        WallBehaviour.inventoryItems = new Item[0];
        WallBehaviour.bodyItems = new Item[0];
        WallBehaviour.foundTemplates = new int[0];
    }
}
