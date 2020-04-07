// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.server.LoginHandler;
import java.util.Set;
import com.wurmonline.server.skills.SkillSystem;
import com.wurmonline.server.behaviours.BuildMaterial;
import com.wurmonline.server.structures.BridgePartEnum;
import java.io.IOException;
import com.wurmonline.server.structures.RoofFloorEnum;
import java.io.UnsupportedEncodingException;
import javax.annotation.Nullable;
import com.wurmonline.server.utils.StringUtil;
import com.wurmonline.server.NoSuchEntryException;
import java.util.logging.Level;
import com.wurmonline.server.GeneralUtilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.shared.constants.WallConstants;
import com.wurmonline.shared.constants.StructureConstantsEnum;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.behaviours.CaveWallBehaviour;
import com.wurmonline.server.behaviours.MethodsStructure;
import com.wurmonline.server.structures.Structure;
import java.util.Iterator;
import java.util.List;
import java.nio.ByteBuffer;
import java.util.Optional;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.structures.WallEnum;
import com.wurmonline.server.Items;
import com.wurmonline.server.players.Player;
import javax.annotation.Nonnull;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.communication.SocketConnection;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.shared.constants.ProtoConstants;

public final class CreationWindowMethods implements ProtoConstants, MiscConstants
{
    private static final Logger logger;
    private static final String CHARSET_ENCODING_FOR_COMMS = "UTF-8";
    
    public static final boolean createWallBuildingBuffer(final SocketConnection connection, @Nonnull final Wall wall, @Nonnull final Player player, final long toolId) {
        Item tool = null;
        if (toolId != -10L) {
            final Optional<Item> optTool = Items.getItemOptional(toolId);
            if (!optTool.isPresent()) {
                return false;
            }
            tool = optTool.get();
        }
        WallEnum wallEnum = WallEnum.WALL_PLAN;
        wallEnum = WallEnum.getWall(wall.getType(), wall.getMaterial());
        if (wallEnum == WallEnum.WALL_PLAN) {
            return false;
        }
        final boolean sendNeededTool = tool == null || !WallEnum.isCorrectTool(wallEnum, player, tool);
        final ByteBuffer buffer = connection.getBuffer();
        addPartialRequestHeader(buffer);
        buffer.put((byte)1);
        buffer.putShort((short)(sendNeededTool ? 2 : 1));
        if (sendNeededTool && !addToolsNeededForWall(buffer, wallEnum, player)) {
            connection.clearBuffer();
            return false;
        }
        addStringToBuffer(buffer, "Item(s) needed in inventory", false);
        final int[] needed = WallEnum.getMaterialsNeeded(wall);
        buffer.putShort((short)(needed.length / 2));
        for (int i = 0; i < needed.length; i += 2) {
            final ItemTemplate template = getItemTemplate(needed[i]);
            if (template == null) {
                connection.clearBuffer();
                return false;
            }
            final String name = getFenceMaterialName(template);
            addStringToBuffer(buffer, name, false);
            buffer.putShort(template.getImageNumber());
            final short chance = (short)needed[i + 1];
            buffer.putShort(chance);
            buffer.putShort(wallEnum.getActionId());
        }
        return true;
    }
    
    private static boolean addToolsNeededForWall(final ByteBuffer buffer, final WallEnum wallEnum, @Nonnull final Player player) {
        addStringToBuffer(buffer, "Needed tool in crafting window", false);
        final List<Integer> list = WallEnum.getToolsForWall(wallEnum, player);
        buffer.putShort((short)list.size());
        for (final Integer tid : list) {
            final ItemTemplate template = getItemTemplate(tid);
            if (template == null) {
                return false;
            }
            final String name = getFenceMaterialName(template);
            addStringToBuffer(buffer, name, false);
            buffer.putShort(template.getImageNumber());
            final short chance = 1;
            buffer.putShort((short)1);
            buffer.putShort(wallEnum.getActionId());
        }
        return true;
    }
    
    private static final String getFenceMaterialName(final ItemTemplate template) {
        if (template.getTemplateId() == 218) {
            return "small iron " + template.getName();
        }
        if (template.getTemplateId() == 217) {
            return "large iron " + template.getName();
        }
        return template.getName();
    }
    
    public static final boolean createWallPlanBuffer(final SocketConnection connection, @Nonnull final Structure structure, @Nonnull final Wall wall, @Nonnull final Player player, final long toolId) {
        if (toolId == -10L) {
            return false;
        }
        final Optional<Item> optTool = Items.getItemOptional(toolId);
        if (!optTool.isPresent()) {
            return false;
        }
        final Item tool = optTool.get();
        final List<WallEnum> wallList = WallEnum.getWallsByTool(player, tool, structure.needsDoor(), MethodsStructure.hasInsideFence(wall));
        if (wallList.size() == 0) {
            return false;
        }
        final ByteBuffer buffer = connection.getBuffer();
        addPartialRequestHeader(buffer);
        buffer.put((byte)0);
        buffer.putShort((short)1);
        addStringToBuffer(buffer, "Walls", false);
        buffer.putShort((short)wallList.size());
        for (final WallEnum en : wallList) {
            addStringToBuffer(buffer, en.getName(), false);
            buffer.putShort(en.getIcon());
            final boolean canBuild = WallEnum.canBuildWall(wall, en.getMaterial(), player);
            final short chance = (short)(canBuild ? 100 : 0);
            buffer.putShort(chance);
            buffer.putShort(en.getActionId());
        }
        return true;
    }
    
    public static final boolean createCaveCladdingBuffer(final SocketConnection connection, final int tilex, final int tiley, final int tile, final byte type, final Player player, final long toolId) {
        Item tool = null;
        if (toolId != -10L) {
            final Optional<Item> optTool = Items.getItemOptional(toolId);
            if (!optTool.isPresent()) {
                return false;
            }
            tool = optTool.get();
        }
        final boolean sendNeededTool = tool == null || !CaveWallBehaviour.isCorrectTool(type, player, tool);
        final ByteBuffer buffer = connection.getBuffer();
        addPartialRequestHeader(buffer);
        buffer.put((byte)1);
        buffer.putShort((short)(sendNeededTool ? 3 : 2));
        if (sendNeededTool && !addToolsNeededForWall(buffer, type, player)) {
            connection.clearBuffer();
            return false;
        }
        addStringToBuffer(buffer, "Item(s) needed in inventory", false);
        final short action = CaveWallBehaviour.actionFromWallType(type);
        final int[] needed = CaveWallBehaviour.getMaterialsNeeded(tilex, tiley, type);
        buffer.putShort((short)(needed.length / 2));
        for (int i = 0; i < needed.length; i += 2) {
            final ItemTemplate template = getItemTemplate(needed[i]);
            if (template == null) {
                connection.clearBuffer();
                return false;
            }
            final String name = getFenceMaterialName(template);
            addStringToBuffer(buffer, name, false);
            buffer.putShort(template.getImageNumber());
            final short chance = 1;
            buffer.putShort((short)1);
            buffer.putShort(action);
        }
        addStringToBuffer(buffer, "Total materials needed", false);
        if (needed.length == 1 && needed[0] == -1) {
            buffer.putShort((short)0);
        }
        else {
            buffer.putShort((short)(needed.length / 2));
            for (int i = 0; i < needed.length; i += 2) {
                final ItemTemplate template = getItemTemplate(needed[i]);
                final String name = getFenceMaterialName(template);
                addStringToBuffer(buffer, name, false);
                buffer.putShort(template.getImageNumber());
                final short chance = (short)needed[i + 1];
                buffer.putShort(chance);
                buffer.putShort(action);
            }
        }
        return true;
    }
    
    private static boolean addToolsNeededForWall(final ByteBuffer buffer, final byte type, @Nonnull final Player player) {
        addStringToBuffer(buffer, "Needed tool in crafting window", false);
        final List<Integer> list = CaveWallBehaviour.getToolsForType(type, player);
        buffer.putShort((short)list.size());
        for (final Integer tid : list) {
            final ItemTemplate template = getItemTemplate(tid);
            if (template == null) {
                return false;
            }
            final String name = getFenceMaterialName(template);
            addStringToBuffer(buffer, name, false);
            buffer.putShort(template.getImageNumber());
            final short chance = 1;
            buffer.putShort((short)1);
            buffer.putShort(CaveWallBehaviour.actionFromWallType(type));
        }
        return true;
    }
    
    public static final boolean createCaveReinforcedBuffer(final SocketConnection connection, final Player player, final long toolId) {
        if (toolId == -10L) {
            return false;
        }
        final Optional<Item> optTool = Items.getItemOptional(toolId);
        if (!optTool.isPresent()) {
            return false;
        }
        final Item tool = optTool.get();
        final byte[] canMake = CaveWallBehaviour.getMaterialsFromToolType(player, tool);
        if (canMake.length == 0) {
            return false;
        }
        final ByteBuffer buffer = connection.getBuffer();
        addPartialRequestHeader(buffer);
        buffer.put((byte)0);
        buffer.putShort((short)1);
        addStringToBuffer(buffer, "CaveWalls", false);
        buffer.putShort((short)canMake.length);
        for (final byte type : canMake) {
            final Tiles.Tile theTile = Tiles.getTile(type);
            addStringToBuffer(buffer, theTile.getName(), false);
            buffer.putShort((short)theTile.getIconId());
            final boolean canBuild = CaveWallBehaviour.canCladWall(type, player);
            final short chance = (short)(canBuild ? 100 : 0);
            buffer.putShort(chance);
            buffer.putShort(CaveWallBehaviour.actionFromWallType(type));
        }
        return true;
    }
    
    public static final boolean createHedgeCreationBuffer(final SocketConnection connection, @Nonnull final Item sprout, final long borderId, @Nonnull final Player player) {
        final StructureConstantsEnum hedgeType = Fence.getLowHedgeType(sprout.getMaterial());
        if (hedgeType == StructureConstantsEnum.FENCE_PLAN_WOODEN) {
            return false;
        }
        final int x = Tiles.decodeTileX(borderId);
        final int y = Tiles.decodeTileY(borderId);
        final Tiles.TileBorderDirection dir = Tiles.decodeDirection(borderId);
        final Structure structure = MethodsStructure.getStructureOrNullAtTileBorder(x, y, dir, true);
        if (structure != null) {
            return false;
        }
        if (!player.isOnSurface()) {
            return false;
        }
        final ByteBuffer buffer = connection.getBuffer();
        addPartialRequestHeader(buffer);
        buffer.put((byte)0);
        buffer.putShort((short)1);
        addStringToBuffer(buffer, "Hedges", false);
        buffer.putShort((short)1);
        final String name = WallConstants.getName(hedgeType);
        addStringToBuffer(buffer, name, false);
        buffer.putShort((short)60);
        final Skill gardening = player.getSkills().getSkillOrLearn(10045);
        final short chance = (short)gardening.getChance(1.0f + sprout.getDamage(), null, sprout.getQualityLevel());
        buffer.putShort(chance);
        buffer.putShort(Actions.actionEntrys[186].getNumber());
        return true;
    }
    
    public static final boolean createFlowerbedBuffer(final SocketConnection connection, @Nonnull final Item tool, final long borderId, @Nonnull final Player player) {
        final StructureConstantsEnum flowerbedType = Fence.getFlowerbedType(tool.getTemplateId());
        final int x = Tiles.decodeTileX(borderId);
        final int y = Tiles.decodeTileY(borderId);
        final Tiles.TileBorderDirection dir = Tiles.decodeDirection(borderId);
        final Structure structure = MethodsStructure.getStructureOrNullAtTileBorder(x, y, dir, true);
        if (structure != null) {
            return false;
        }
        if (!player.isOnSurface()) {
            return false;
        }
        final ByteBuffer buffer = connection.getBuffer();
        addPartialRequestHeader(buffer);
        buffer.put((byte)0);
        buffer.putShort((short)1);
        addStringToBuffer(buffer, "Flowerbeds", false);
        buffer.putShort((short)1);
        final String name = WallConstants.getName(flowerbedType);
        addStringToBuffer(buffer, name, false);
        buffer.putShort((short)60);
        final Skill gardening = player.getSkills().getSkillOrLearn(10045);
        final short chance = (short)gardening.getChance(1.0f + tool.getDamage(), null, tool.getQualityLevel());
        buffer.putShort(chance);
        buffer.putShort(Actions.actionEntrys[563].getNumber());
        return true;
    }
    
    public static final boolean createFenceListBuffer(final SocketConnection connection, final long borderId) {
        final int x = Tiles.decodeTileX(borderId);
        final int y = Tiles.decodeTileY(borderId);
        final Tiles.TileBorderDirection dir = Tiles.decodeDirection(borderId);
        final int heightOffset = Tiles.decodeHeightOffset(borderId);
        final boolean onSurface = true;
        boolean hasArch = false;
        if (MethodsStructure.doesTileBorderContainWallOrFence(x, y, heightOffset, dir, true, false)) {
            hasArch = true;
        }
        final Structure structure = MethodsStructure.getStructureOrNullAtTileBorder(x, y, dir, true);
        final Map<String, List<ActionEntry>> fenceList = createFenceCreationList(structure != null, false, hasArch);
        if (Items.getMarker(x, y, true, 0, -10L) != null) {
            return false;
        }
        if (dir == Tiles.TileBorderDirection.DIR_HORIZ && Items.getMarker(x + 1, y, true, 0, -10L) != null) {
            return false;
        }
        if (dir == Tiles.TileBorderDirection.DIR_DOWN && Items.getMarker(x, y + 1, true, 0, -10L) != null) {
            return false;
        }
        if (fenceList.size() == 0) {
            return false;
        }
        final ByteBuffer buffer = connection.getBuffer();
        addPartialRequestHeader(buffer);
        buffer.put((byte)0);
        buffer.putShort((short)fenceList.size());
        for (final String category : fenceList.keySet()) {
            addStringToBuffer(buffer, category, false);
            final List<ActionEntry> fences = fenceList.get(category);
            buffer.putShort((short)fences.size());
            for (final ActionEntry ae : fences) {
                final StructureConstantsEnum type = Fence.getFencePlanType(ae.getNumber());
                final String name = WallConstants.getName(Fence.getFenceForPlan(type));
                addStringToBuffer(buffer, name, false);
                buffer.putShort((short)60);
                final short chance = 100;
                buffer.putShort((short)100);
                buffer.putShort(ae.getNumber());
            }
        }
        return true;
    }
    
    private static final Map<String, List<ActionEntry>> createFenceCreationList(final boolean inStructure, final boolean showAll, final boolean borderHasArch) {
        final Map<String, List<ActionEntry>> list = new HashMap<String, List<ActionEntry>>();
        if (!inStructure || showAll) {
            list.put("Log", new ArrayList<ActionEntry>());
        }
        list.put("Plank", new ArrayList<ActionEntry>());
        list.put("Rope", new ArrayList<ActionEntry>());
        list.put("Shaft", new ArrayList<ActionEntry>());
        list.put("Woven", new ArrayList<ActionEntry>());
        list.put("Stone", new ArrayList<ActionEntry>());
        list.put("Iron", new ArrayList<ActionEntry>());
        list.put("Slate", new ArrayList<ActionEntry>());
        list.put("Rounded stone", new ArrayList<ActionEntry>());
        list.put("Pottery", new ArrayList<ActionEntry>());
        list.put("Sandstone", new ArrayList<ActionEntry>());
        list.put("Marble", new ArrayList<ActionEntry>());
        if (!inStructure || showAll) {
            list.get("Log").add(Actions.actionEntrys[165]);
            list.get("Log").add(Actions.actionEntrys[167]);
        }
        list.get("Plank").add(Actions.actionEntrys[166]);
        list.get("Plank").add(Actions.actionEntrys[168]);
        list.get("Plank").add(Actions.actionEntrys[520]);
        list.get("Plank").add(Actions.actionEntrys[528]);
        if ((inStructure && !borderHasArch) || showAll) {
            list.get("Plank").add(Actions.actionEntrys[516]);
        }
        list.get("Rope").add(Actions.actionEntrys[543]);
        list.get("Rope").add(Actions.actionEntrys[544]);
        list.get("Shaft").add(Actions.actionEntrys[526]);
        list.get("Shaft").add(Actions.actionEntrys[527]);
        list.get("Shaft").add(Actions.actionEntrys[529]);
        list.get("Woven").add(Actions.actionEntrys[478]);
        if (!inStructure || showAll) {
            list.get("Stone").add(Actions.actionEntrys[163]);
        }
        if (!inStructure || !borderHasArch || showAll) {
            list.get("Stone").add(Actions.actionEntrys[164]);
        }
        if ((!inStructure && !borderHasArch) || showAll) {
            list.get("Stone").add(Actions.actionEntrys[654]);
        }
        list.get("Stone").add(Actions.actionEntrys[541]);
        list.get("Stone").add(Actions.actionEntrys[542]);
        if ((inStructure && !borderHasArch) || showAll) {
            list.get("Stone").add(Actions.actionEntrys[517]);
        }
        list.get("Iron").add(Actions.actionEntrys[477]);
        list.get("Iron").add(Actions.actionEntrys[479]);
        if (!inStructure || !borderHasArch || showAll) {
            list.get("Iron").add(Actions.actionEntrys[545]);
            list.get("Iron").add(Actions.actionEntrys[546]);
        }
        list.get("Iron").add(Actions.actionEntrys[611]);
        if (inStructure || showAll) {
            list.get("Iron").add(Actions.actionEntrys[521]);
        }
        list.get("Slate").add(Actions.actionEntrys[832]);
        list.get("Slate").add(Actions.actionEntrys[833]);
        list.get("Slate").add(Actions.actionEntrys[834]);
        if (!inStructure || !borderHasArch || showAll) {
            list.get("Slate").add(Actions.actionEntrys[870]);
        }
        if ((!inStructure && !borderHasArch) || showAll) {
            list.get("Slate").add(Actions.actionEntrys[871]);
        }
        if (!inStructure || !borderHasArch || showAll) {
            list.get("Slate").add(Actions.actionEntrys[872]);
            list.get("Slate").add(Actions.actionEntrys[873]);
        }
        if ((inStructure && !borderHasArch) || showAll) {
            list.get("Slate").add(Actions.actionEntrys[874]);
        }
        list.get("Slate").add(Actions.actionEntrys[875]);
        list.get("Rounded stone").add(Actions.actionEntrys[835]);
        list.get("Rounded stone").add(Actions.actionEntrys[836]);
        list.get("Rounded stone").add(Actions.actionEntrys[837]);
        if (!inStructure || !borderHasArch || showAll) {
            list.get("Rounded stone").add(Actions.actionEntrys[876]);
        }
        if ((!inStructure && !borderHasArch) || showAll) {
            list.get("Rounded stone").add(Actions.actionEntrys[877]);
        }
        if (!inStructure || !borderHasArch || showAll) {
            list.get("Rounded stone").add(Actions.actionEntrys[878]);
            list.get("Rounded stone").add(Actions.actionEntrys[879]);
        }
        if ((inStructure && !borderHasArch) || showAll) {
            list.get("Rounded stone").add(Actions.actionEntrys[880]);
        }
        list.get("Rounded stone").add(Actions.actionEntrys[881]);
        list.get("Pottery").add(Actions.actionEntrys[838]);
        list.get("Pottery").add(Actions.actionEntrys[839]);
        list.get("Pottery").add(Actions.actionEntrys[840]);
        if (!inStructure || !borderHasArch || showAll) {
            list.get("Pottery").add(Actions.actionEntrys[894]);
        }
        if ((!inStructure && !borderHasArch) || showAll) {
            list.get("Pottery").add(Actions.actionEntrys[895]);
        }
        if (!inStructure || !borderHasArch || showAll) {
            list.get("Pottery").add(Actions.actionEntrys[896]);
            list.get("Pottery").add(Actions.actionEntrys[897]);
        }
        if ((inStructure && !borderHasArch) || showAll) {
            list.get("Pottery").add(Actions.actionEntrys[898]);
        }
        list.get("Pottery").add(Actions.actionEntrys[899]);
        list.get("Sandstone").add(Actions.actionEntrys[841]);
        list.get("Sandstone").add(Actions.actionEntrys[842]);
        list.get("Sandstone").add(Actions.actionEntrys[843]);
        if (!inStructure || !borderHasArch || showAll) {
            list.get("Sandstone").add(Actions.actionEntrys[882]);
        }
        if ((!inStructure && !borderHasArch) || showAll) {
            list.get("Sandstone").add(Actions.actionEntrys[883]);
        }
        if (!inStructure || !borderHasArch || showAll) {
            list.get("Sandstone").add(Actions.actionEntrys[884]);
            list.get("Sandstone").add(Actions.actionEntrys[885]);
        }
        if ((inStructure && !borderHasArch) || showAll) {
            list.get("Sandstone").add(Actions.actionEntrys[886]);
        }
        list.get("Sandstone").add(Actions.actionEntrys[887]);
        list.get("Marble").add(Actions.actionEntrys[844]);
        list.get("Marble").add(Actions.actionEntrys[845]);
        list.get("Marble").add(Actions.actionEntrys[846]);
        if (!inStructure || !borderHasArch || showAll) {
            list.get("Marble").add(Actions.actionEntrys[900]);
        }
        if ((!inStructure && !borderHasArch) || showAll) {
            list.get("Marble").add(Actions.actionEntrys[901]);
        }
        if (!inStructure || !borderHasArch || showAll) {
            list.get("Marble").add(Actions.actionEntrys[902]);
            list.get("Marble").add(Actions.actionEntrys[903]);
        }
        if ((inStructure && !borderHasArch) || showAll) {
            list.get("Marble").add(Actions.actionEntrys[904]);
        }
        list.get("Marble").add(Actions.actionEntrys[905]);
        return list;
    }
    
    private static final List<ActionEntry> createCaveWallCreationList() {
        final List<ActionEntry> list = new ArrayList<ActionEntry>();
        list.add(Actions.actionEntrys[856]);
        list.add(Actions.actionEntrys[857]);
        list.add(Actions.actionEntrys[858]);
        list.add(Actions.actionEntrys[859]);
        list.add(Actions.actionEntrys[860]);
        list.add(Actions.actionEntrys[861]);
        list.add(Actions.actionEntrys[862]);
        return list;
    }
    
    public static final boolean createCreationListBuffer(final SocketConnection connection, @Nonnull final Item source, @Nonnull final Item target, @Nonnull final Player player) {
        final Map<String, Map<CreationEntry, Integer>> map = GeneralUtilities.getCreationList(source, target, player);
        if (map.size() != 0) {
            final ByteBuffer buffer = connection.getBuffer();
            addPartialRequestHeader(buffer);
            buffer.put((byte)0);
            buffer.putShort((short)map.size());
            for (final String category : map.keySet()) {
                addStringToBuffer(buffer, category, false);
                final Map<CreationEntry, Integer> entries = map.get(category);
                buffer.putShort((short)entries.size());
                if (!addCreationEntriesToPartialList(buffer, entries)) {
                    connection.clearBuffer();
                    return false;
                }
            }
            return true;
        }
        final Recipe recipe = Recipes.getRecipeFor(player.getWurmId(), (byte)2, source, target, true, false);
        if (recipe == null) {
            return false;
        }
        final ByteBuffer buffer2 = connection.getBuffer();
        addPartialRequestHeader(buffer2);
        buffer2.put((byte)0);
        buffer2.putShort((short)1);
        addStringToBuffer(buffer2, "Cooking", false);
        buffer2.putShort((short)1);
        Item realSource = source;
        Item realTarget = target;
        if (recipe.hasActiveItem() && source != null && recipe.getActiveItem().getTemplateId() != realSource.getTemplateId()) {
            realSource = target;
            realTarget = source;
        }
        final ItemTemplate template = recipe.getResultTemplate(realTarget);
        if (template == null) {
            connection.clearBuffer();
            return false;
        }
        addStringToBuffer(buffer2, recipe.getSubMenuName(realTarget), false);
        buffer2.putShort(template.getImageNumber());
        buffer2.putShort((short)recipe.getChanceFor(realSource, realTarget, player));
        buffer2.putShort(recipe.getMenuId());
        return true;
    }
    
    public static final boolean createUnfinishedCreationListBuffer(final SocketConnection connection, @Nonnull final Item source, @Nonnull final Player player) {
        final AdvancedCreationEntry entry = getAdvancedCreationEntry(source.getRealTemplateId());
        if (entry == null) {
            return false;
        }
        final List<String> itemNames = new ArrayList<String>();
        final List<Integer> numberOfItemsNeeded = new ArrayList<Integer>();
        final List<Short> icons = new ArrayList<Short>();
        if (!fillRequirmentsLists(entry, source, itemNames, numberOfItemsNeeded, icons)) {
            return false;
        }
        final ByteBuffer buffer = connection.getBuffer();
        addPartialRequestHeader(buffer);
        buffer.put((byte)1);
        buffer.putShort((short)1);
        final String category = "Needed items";
        addStringToBuffer(buffer, "Needed items", false);
        buffer.putShort((short)numberOfItemsNeeded.size());
        for (int i = 0; i < numberOfItemsNeeded.size(); ++i) {
            final String itemName = itemNames.get(i);
            addStringToBuffer(buffer, itemName, false);
            buffer.putShort(icons.get(i));
            final short count = (short)(Object)numberOfItemsNeeded.get(i);
            buffer.putShort(count);
            buffer.putShort((short)0);
        }
        return true;
    }
    
    private static final boolean fillRequirmentsLists(final AdvancedCreationEntry entry, final Item source, final List<String> itemNames, final List<Integer> numberOfItemsNeeded, final List<Short> icons) {
        final CreationRequirement[] requirements = entry.getRequirements();
        if (requirements.length == 0) {
            return false;
        }
        for (final CreationRequirement requirement : requirements) {
            final int remaining = requirement.getResourceNumber() - AdvancedCreationEntry.getStateForRequirement(requirement, source);
            if (remaining > 0) {
                final int templateNeeded = requirement.getResourceTemplateId();
                final ItemTemplate needed = getItemTemplate(templateNeeded);
                if (needed == null) {
                    return false;
                }
                itemNames.add(buildTemplateName(needed, null, (byte)0));
                icons.add(needed.getImageNumber());
                numberOfItemsNeeded.add(remaining);
            }
        }
        return true;
    }
    
    private static final AdvancedCreationEntry getAdvancedCreationEntry(final int id) {
        try {
            return CreationMatrix.getInstance().getAdvancedCreationEntry(id);
        }
        catch (NoSuchEntryException nse) {
            CreationWindowMethods.logger.log(Level.WARNING, "No advanced creation entry with id: " + id, nse);
            return null;
        }
    }
    
    private static final boolean addCreationEntriesToPartialList(final ByteBuffer buffer, final Map<CreationEntry, Integer> entries) {
        for (final CreationEntry entry : entries.keySet()) {
            final ItemTemplate template = getItemTemplate(entry.getObjectCreated());
            if (template == null) {
                return false;
            }
            final String entryName = buildTemplateName(template, entry, (byte)0);
            addStringToBuffer(buffer, entryName, false);
            buffer.putShort(template.getImageNumber());
            final short chance = (short)(Object)entries.get(entry);
            buffer.putShort(chance);
            buffer.putShort((short)(10000 + entry.getObjectCreated()));
        }
        return true;
    }
    
    private static final ItemTemplate getItemTemplate(final int templateId) {
        try {
            return ItemTemplateFactory.getInstance().getTemplate(templateId);
        }
        catch (NoSuchTemplateException nst) {
            CreationWindowMethods.logger.log(Level.WARNING, "Unable to find item template with id: " + templateId, nst);
            return null;
        }
    }
    
    private static final String buildTemplateCaptionName(final ItemTemplate toCreate, final ItemTemplate source, final ItemTemplate target) {
        final String nameFormat = "%s %s";
        final String materialFormat = "%s, %s";
        String name = toCreate.getName();
        final String sourceMaterial = Item.getMaterialString(source.getMaterial());
        final String targetMaterial = Item.getMaterialString(target.getMaterial());
        final String createMaterial = Item.getMaterialString(toCreate.getMaterial());
        if (toCreate.sizeString.length() > 0) {
            name = StringUtil.format("%s %s", toCreate.sizeString.trim(), name);
        }
        if (toCreate.isMetal()) {
            if (!name.equals("lump") && !name.equals("sheet")) {
                if (!source.isTool() && source.isMetal() && !sourceMaterial.equals("unknown")) {
                    name = StringUtil.format("%s, %s", name, sourceMaterial);
                }
                else if (!target.isTool() && target.isMetal() && !targetMaterial.equals("unknown")) {
                    name = StringUtil.format("%s, %s", name, targetMaterial);
                }
            }
            else if (!createMaterial.equals("unknown")) {
                name = StringUtil.format("%s %s", createMaterial, name);
            }
        }
        else if (toCreate.isLiquidCooking()) {
            if (target.isFood()) {
                name = StringUtil.format("%s, %s", name, target.getName());
            }
        }
        else if (toCreate.getTemplateId() == 74) {
            if (!createMaterial.equals("unknown")) {
                name = StringUtil.format("%s %s", createMaterial, name);
            }
        }
        else if (toCreate.getTemplateId() == 891) {
            name = StringUtil.format("%s %s", "wooden", toCreate.getName());
        }
        else if (toCreate.getTemplateId() == 404) {
            name = StringUtil.format("%s %s", "stone", toCreate.getName());
        }
        else if (toCreate.isStone()) {
            if (name.equals("shards") && !createMaterial.equals("unknown")) {
                name = StringUtil.format("%s %s", createMaterial, name);
            }
            else if (name.equals("altar")) {
                name = StringUtil.format("%s %s", "stone", name);
            }
            else if (toCreate.getTemplateId() == 593) {
                name = StringUtil.format("%s %s", "stone", name);
            }
        }
        else if (toCreate.getTemplateId() == 322) {
            if (name.equals("altar")) {
                name = StringUtil.format("%s %s", "wooden", name);
            }
        }
        else if (toCreate.getTemplateId() == 592) {
            name = StringUtil.format("%s %s", "plank", name);
        }
        return name;
    }
    
    private static final String buildTemplateName(final ItemTemplate template, @Nullable final CreationEntry entry, final byte materialOverride) {
        final String nameFormat = "%s %s";
        final String materialFormat = "%s, %s";
        String name = template.getName();
        final String material = Item.getMaterialString(template.getMaterial());
        if (template.sizeString.length() > 0) {
            name = StringUtil.format("%s %s", template.sizeString.trim(), name);
        }
        if (template.isMetal() && (name.equals("lump") || name.equals("sheet"))) {
            name = StringUtil.format("%s %s", material, name);
        }
        else if (materialOverride != 0) {
            name = StringUtil.format("%s, %s", name, Materials.convertMaterialByteIntoString(materialOverride));
        }
        else if (name.equals("barding")) {
            if (template.isCloth()) {
                name = StringUtil.format("%s %s", "cloth", name);
            }
            else if (template.isMetal()) {
                name = StringUtil.format("%s %s", "chain", name);
            }
            else {
                name = StringUtil.format("%s %s", material, name);
            }
        }
        else if (name.equals("rock")) {
            name = StringUtil.format("%s, %s", name, "iron");
        }
        else if (template.getTemplateId() == 216 || template.getTemplateId() == 215) {
            name = StringUtil.format("%s, %s", name, material);
        }
        else if (template.isStone()) {
            if (name.equals("shards") && !material.equals("unknown")) {
                name = StringUtil.format("%s, %s", name, material);
            }
        }
        else if (name.equals("fur")) {
            if (entry != null) {
                if (entry.getObjectCreated() == 846) {
                    name = "black bear fur";
                }
                else if (entry.getObjectCreated() == 847) {
                    name = "brown bear fur";
                }
                else if (entry.getObjectCreated() == 849) {
                    name = "black wolf fur";
                }
            }
        }
        else if (name.equals("pelt") && entry != null && entry.getObjectCreated() == 848) {
            name = "mountain lion pelt";
        }
        return name;
    }
    
    private static void addStringToBuffer(final ByteBuffer buffer, final String string, final boolean shortLength) {
        final byte[] bytes = getEncodedBytesFromString(string);
        if (!shortLength) {
            buffer.put((byte)bytes.length);
        }
        else {
            buffer.putShort((short)bytes.length);
        }
        buffer.put(bytes);
    }
    
    private static final byte[] getEncodedBytesFromString(final String string) {
        try {
            return string.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            CreationWindowMethods.logger.log(Level.WARNING, e.getMessage(), e);
            return new byte[0];
        }
    }
    
    private static void addPartialRequestHeader(final ByteBuffer buffer) {
        buffer.put((byte)(-46));
        buffer.put((byte)0);
    }
    
    public static boolean sendAllCraftingRecipes(final SocketConnection connection, @Nonnull final Player player) {
        final RecipesListParameter params = new RecipesListParameter();
        final short numberOfEntries = buildCreationsList(params);
        if (!sendCreationListCategories(connection, params, numberOfEntries)) {
            player.setLink(false);
            return false;
        }
        return sendCreationRecipes(connection, player, params) && sendFenceRecipes(connection, player, params) && sendHedgeRecipes(connection, player, params) && sendFlowerbedRecipes(connection, player, params) && sendWallRecipes(connection, player, params) && sendRoofFloorRecipes(connection, player, params) && sendBridgePartRecipes(connection, player, params) && sendCaveWallRecipes(connection, player, params);
    }
    
    private static final boolean sendRoofFloorRecipes(final SocketConnection connection, @Nonnull final Player player, final RecipesListParameter params) {
        for (final String category : params.getRoofs_floors().keySet()) {
            final List<RoofFloorEnum> entries = params.getRoofs_floors().get(category);
            for (final RoofFloorEnum entry : entries) {
                final int[] validToolsForMaterial;
                final int[] tools = validToolsForMaterial = RoofFloorEnum.getValidToolsForMaterial(entry.getMaterial());
                for (final int tool : validToolsForMaterial) {
                    final ByteBuffer buffer = connection.getBuffer();
                    addCreationRecipesMessageHeaders(buffer);
                    addCategoryIdToBuffer(params, category, buffer);
                    addRoofFloorRecipeInfoToBuffer(entry, buffer);
                    final ItemTemplate toolTemplate = getItemTemplate(tool);
                    if (toolTemplate == null) {
                        CreationWindowMethods.logger.log(Level.WARNING, "sendRoofFlorRecipes() No item template found with id: " + tool);
                        connection.clearBuffer();
                        return false;
                    }
                    addRoofFloorToolInfoToBuffer(buffer, toolTemplate);
                    addWallPlanInfoToBuffer(buffer, entry);
                    if (!addAdditionalMaterialsForRoofsFloors(buffer, entry)) {
                        connection.clearBuffer();
                        return false;
                    }
                    try {
                        connection.flush();
                    }
                    catch (IOException ex) {
                        CreationWindowMethods.logger.log(Level.WARNING, "Failed to flush floor|roof recipes!", ex);
                        player.setLink(false);
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private static final boolean sendBridgePartRecipes(final SocketConnection connection, @Nonnull final Player player, final RecipesListParameter params) {
        for (final String category : params.getBridgeParts().keySet()) {
            final List<BridgePartEnum> entries = params.getBridgeParts().get(category);
            for (final BridgePartEnum entry : entries) {
                final int[] validToolsForMaterial;
                final int[] tools = validToolsForMaterial = BridgePartEnum.getValidToolsForMaterial(entry.getMaterial());
                for (final int tool : validToolsForMaterial) {
                    final ByteBuffer buffer = connection.getBuffer();
                    addCreationRecipesMessageHeaders(buffer);
                    addCategoryIdToBuffer(params, category, buffer);
                    addBridgePartRecipeInfoToBuffer(entry, buffer);
                    final ItemTemplate toolTemplate = getItemTemplate(tool);
                    if (toolTemplate == null) {
                        CreationWindowMethods.logger.log(Level.WARNING, "sendRoofFlorRecipes() No item template found with id: " + tool);
                        connection.clearBuffer();
                        return false;
                    }
                    addRoofFloorToolInfoToBuffer(buffer, toolTemplate);
                    buffer.putShort((short)60);
                    addStringToBuffer(buffer, entry.getName() + " plan", true);
                    if (!addTotalMaterialsForBridgeParts(buffer, entry)) {
                        connection.clearBuffer();
                        return false;
                    }
                    try {
                        connection.flush();
                    }
                    catch (IOException ex) {
                        CreationWindowMethods.logger.log(Level.WARNING, "Failed to flush bridge part recipes!", ex);
                        player.setLink(false);
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private static final boolean addAdditionalMaterialsForRoofsFloors(final ByteBuffer buffer, final RoofFloorEnum entry) {
        final List<BuildMaterial> list = entry.getTotalMaterialsNeeded();
        buffer.putShort((short)list.size());
        for (final BuildMaterial bMat : list) {
            final ItemTemplate mat = getItemTemplate(bMat.getTemplateId());
            if (mat == null) {
                CreationWindowMethods.logger.log(Level.WARNING, "Unable to find item template with id: " + bMat.getTemplateId());
                return false;
            }
            buffer.putShort(mat.getImageNumber());
            addStringToBuffer(buffer, buildTemplateName(mat, null, (byte)0), true);
            buffer.putShort((short)bMat.getNeededQuantity());
        }
        return true;
    }
    
    private static final boolean addTotalMaterialsForBridgeParts(final ByteBuffer buffer, final BridgePartEnum entry) {
        final List<BuildMaterial> list = entry.getTotalMaterialsNeeded();
        buffer.putShort((short)list.size());
        for (final BuildMaterial bMat : list) {
            final ItemTemplate mat = getItemTemplate(bMat.getTemplateId());
            if (mat == null) {
                CreationWindowMethods.logger.log(Level.WARNING, "Unable to find item template with id: " + bMat.getTemplateId());
                return false;
            }
            buffer.putShort(mat.getImageNumber());
            addStringToBuffer(buffer, buildTemplateName(mat, null, (byte)0), true);
            buffer.putShort((short)bMat.getNeededQuantity());
        }
        return true;
    }
    
    private static void addRoofFloorToolInfoToBuffer(final ByteBuffer buffer, final ItemTemplate toolTemplate) {
        buffer.putShort(toolTemplate.getImageNumber());
        addStringToBuffer(buffer, toolTemplate.getName(), true);
    }
    
    private static void addRoofFloorRecipeInfoToBuffer(final RoofFloorEnum entry, final ByteBuffer buffer) {
        buffer.putShort((short)60);
        addStringToBuffer(buffer, entry.getName(), true);
        addStringToBuffer(buffer, SkillSystem.getNameFor(entry.getNeededSkillNumber()), true);
    }
    
    private static void addBridgePartRecipeInfoToBuffer(final BridgePartEnum entry, final ByteBuffer buffer) {
        buffer.putShort((short)60);
        addStringToBuffer(buffer, entry.getName(), true);
        addStringToBuffer(buffer, SkillSystem.getNameFor(entry.getNeededSkillNumber()), true);
    }
    
    private static final boolean sendWallRecipes(final SocketConnection connection, @Nonnull final Player player, final RecipesListParameter params) {
        for (final String category : params.getWalls().keySet()) {
            final List<WallEnum> entries = params.getWalls().get(category);
            for (final WallEnum entry : entries) {
                final List<Integer> tools = WallEnum.getToolsForWall(entry, null);
                for (final Integer tool : tools) {
                    final ByteBuffer buffer = connection.getBuffer();
                    addCreationRecipesMessageHeaders(buffer);
                    addCategoryIdToBuffer(params, category, buffer);
                    addWallInfoToBuffer(entry, buffer);
                    final ItemTemplate toolTemplate = getItemTemplate(tool);
                    if (toolTemplate == null) {
                        connection.clearBuffer();
                        CreationWindowMethods.logger.log(Level.WARNING, "Unable to find tool with id: " + (int)tool);
                        return false;
                    }
                    addWallToolIInfoToBuffer(buffer, toolTemplate);
                    addWallPlanInfoToBuffer(buffer);
                    if (!addAdditionalMaterialsForWall(buffer, entry)) {
                        connection.clearBuffer();
                        return false;
                    }
                    try {
                        connection.flush();
                    }
                    catch (IOException iex) {
                        CreationWindowMethods.logger.log(Level.WARNING, "Failed to flush well recipe", iex);
                        player.setLink(false);
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private static final boolean addAdditionalMaterialsForWall(final ByteBuffer buffer, final WallEnum entry) {
        final int[] needed = entry.getTotalMaterialsNeeded();
        buffer.putShort((short)(needed.length / 2));
        for (int i = 0; i < needed.length; i += 2) {
            final ItemTemplate mat = getItemTemplate(needed[i]);
            if (mat == null) {
                return false;
            }
            buffer.putShort(mat.getImageNumber());
            addStringToBuffer(buffer, buildTemplateName(mat, null, (byte)0), true);
            buffer.putShort((short)needed[i + 1]);
        }
        return true;
    }
    
    private static void addWallPlanInfoToBuffer(final ByteBuffer buffer) {
        buffer.putShort((short)60);
        addStringToBuffer(buffer, WallEnum.WALL_PLAN.getName(), true);
    }
    
    private static void addWallPlanInfoToBuffer(final ByteBuffer buffer, final RoofFloorEnum entry) {
        buffer.putShort((short)60);
        final String planString = entry.isFloor() ? "planned floor" : "planned roof";
        addStringToBuffer(buffer, planString, true);
    }
    
    private static void addWallToolIInfoToBuffer(final ByteBuffer buffer, final ItemTemplate toolTemplate) {
        buffer.putShort(toolTemplate.getImageNumber());
        addStringToBuffer(buffer, toolTemplate.getName(), true);
    }
    
    private static void addWallInfoToBuffer(final WallEnum entry, final ByteBuffer buffer) {
        buffer.putShort((short)60);
        addStringToBuffer(buffer, entry.getName(), true);
        addStringToBuffer(buffer, SkillSystem.getNameFor(WallEnum.getSkillNumber(entry.getMaterial())), true);
    }
    
    private static final boolean sendFlowerbedRecipes(final SocketConnection connection, @Nonnull final Player player, final RecipesListParameter params) {
        for (final String category : params.getFlowerbeds().keySet()) {
            final List<Short> entries = params.getFlowerbeds().get(category);
            for (final short bedType : entries) {
                final short entry = bedType;
                final String name = WallConstants.getName(StructureConstantsEnum.getEnumByValue(bedType));
                final int flowerType = Fence.getFlowerTypeByFlowerbedType(StructureConstantsEnum.getEnumByValue(bedType));
                final ByteBuffer buffer = connection.getBuffer();
                addCreationRecipesMessageHeaders(buffer);
                addCategoryIdToBuffer(params, category, buffer);
                addFlowerbedInfoToBuffer(name, buffer);
                final ItemTemplate flower = getItemTemplate(flowerType);
                if (flower == null) {
                    connection.clearBuffer();
                    return false;
                }
                addWallToolIInfoToBuffer(buffer, flower);
                addTileBorderToBuffer(buffer);
                if (!addAdditionalMaterialsForFlowerbed(buffer, flower)) {
                    connection.clearBuffer();
                    return false;
                }
                try {
                    connection.flush();
                }
                catch (IOException ex) {
                    CreationWindowMethods.logger.log(Level.WARNING, "IO Exception when sending flowerbed recipes.", ex);
                    player.setLink(false);
                    return false;
                }
            }
        }
        return true;
    }
    
    private static final boolean addAdditionalMaterialsForFlowerbed(final ByteBuffer buffer, final ItemTemplate flower) {
        final int[] needed = { flower.getTemplateId(), 4, 22, 3, 218, 1, 26, 1 };
        buffer.putShort((short)(needed.length / 2));
        for (int i = 0; i < needed.length; i += 2) {
            ItemTemplate mat = null;
            if (needed[i] == flower.getTemplateId()) {
                mat = flower;
            }
            else {
                mat = getItemTemplate(needed[i]);
                if (mat == null) {
                    return false;
                }
            }
            buffer.putShort((short)60);
            addStringToBuffer(buffer, buildTemplateName(mat, null, (byte)0), true);
            buffer.putShort((short)needed[i + 1]);
        }
        return true;
    }
    
    private static void addFlowerbedInfoToBuffer(final String name, final ByteBuffer buffer) {
        buffer.putShort((short)60);
        addStringToBuffer(buffer, name, true);
        addStringToBuffer(buffer, SkillSystem.getNameFor(10045), true);
    }
    
    private static final boolean sendHedgeRecipes(final SocketConnection connection, @Nonnull final Player player, final RecipesListParameter params) {
        for (final String category : params.getHedges().keySet()) {
            final List<Short> entries = params.getHedges().get(category);
            for (final short hedgeType : entries) {
                final short entry = hedgeType;
                final ByteBuffer buffer = connection.getBuffer();
                addCreationRecipesMessageHeaders(buffer);
                addCategoryIdToBuffer(params, category, buffer);
                addHedgeInfoToBuffer(StructureConstantsEnum.getEnumByValue(hedgeType), buffer);
                final ItemTemplate sprout = getItemTemplate(266);
                if (sprout == null) {
                    connection.clearBuffer();
                    return false;
                }
                final byte materialType = Fence.getMaterialForLowHedge(StructureConstantsEnum.getEnumByValue(hedgeType));
                final String materialString = Item.getMaterialString(materialType);
                addSproutInfoToBuffer(sprout, materialString, buffer);
                addTileBorderToBuffer(buffer);
                addAdditionalMaterialsForHedge(buffer, sprout, materialString);
                try {
                    connection.flush();
                }
                catch (IOException ex) {
                    CreationWindowMethods.logger.log(Level.WARNING, "IO Exception when sending hedge recipes.", ex);
                    player.setLink(false);
                    return false;
                }
            }
        }
        return true;
    }
    
    private static final void addAdditionalMaterialsForHedge(final ByteBuffer buffer, final ItemTemplate template, final String material) {
        buffer.putShort((short)1);
        buffer.putShort(template.getImageNumber());
        addStringToBuffer(buffer, StringUtil.format("%s, %s", template.getName(), material), true);
        buffer.putShort((short)4);
    }
    
    private static void addSproutInfoToBuffer(final ItemTemplate sprout, final String material, final ByteBuffer buffer) {
        buffer.putShort(sprout.getImageNumber());
        final String sproutName = StringUtil.format("%s, %s", sprout.getName(), material);
        addStringToBuffer(buffer, sproutName, true);
    }
    
    private static void addHedgeInfoToBuffer(final StructureConstantsEnum hedgeType, final ByteBuffer buffer) {
        buffer.putShort((short)60);
        addStringToBuffer(buffer, WallConstants.getName(hedgeType), true);
        addStringToBuffer(buffer, SkillSystem.getNameFor(10045), true);
    }
    
    private static final boolean sendFenceRecipes(final SocketConnection connection, @Nonnull final Player player, final RecipesListParameter params) {
        for (final String category : params.getFences().keySet()) {
            final List<ActionEntry> entries = params.getFences().get(category);
            for (final ActionEntry entry : entries) {
                final StructureConstantsEnum originalFenceType = Fence.getFencePlanType(entry.getNumber());
                final StructureConstantsEnum fenceType = Fence.getFenceForPlan(originalFenceType);
                final int[] correctTools = MethodsStructure.getCorrectToolsForBuildingFences();
                for (int i = 0; i < correctTools.length; ++i) {
                    final ByteBuffer buffer = connection.getBuffer();
                    addCreationRecipesMessageHeaders(buffer);
                    addCategoryIdToBuffer(params, category, buffer);
                    addCreatedFenceToBuffer(originalFenceType, fenceType, buffer);
                    if (!addFenceToolToBuffer(buffer, correctTools[i])) {
                        connection.clearBuffer();
                        return false;
                    }
                    addTileBorderToBuffer(buffer);
                    if (!addAdditionalMaterialsForFence(buffer, originalFenceType)) {
                        connection.clearBuffer();
                        return false;
                    }
                    try {
                        connection.flush();
                    }
                    catch (IOException ex) {
                        CreationWindowMethods.logger.log(Level.WARNING, "IO Exception when sending fence recipes.", ex);
                        player.setLink(false);
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private static final boolean addAdditionalMaterialsForFence(final ByteBuffer buffer, final StructureConstantsEnum fence) {
        final int[] items = Fence.getItemTemplatesNeededForFenceTotal(fence);
        if (items.length < 2) {
            buffer.putShort((short)0);
        }
        else {
            buffer.putShort((short)(items.length / 2));
            for (int i = 0; i < items.length; i += 2) {
                final ItemTemplate mat = getItemTemplate(items[i]);
                if (mat == null) {
                    return false;
                }
                buffer.putShort(mat.getImageNumber());
                addStringToBuffer(buffer, buildTemplateName(mat, null, (byte)0), true);
                buffer.putShort((short)items[i + 1]);
            }
        }
        return true;
    }
    
    private static void addTileBorderToBuffer(final ByteBuffer buffer) {
        buffer.putShort((short)60);
        addStringToBuffer(buffer, "Tile Border", true);
    }
    
    private static boolean addFenceToolToBuffer(final ByteBuffer buffer, final int toolId) {
        final ItemTemplate toolTemplate = getItemTemplate(toolId);
        if (toolTemplate == null) {
            CreationWindowMethods.logger.log(Level.WARNING, "Unable to find tool template with id: " + toolId);
            return false;
        }
        buffer.putShort(toolTemplate.imageNumber);
        addStringToBuffer(buffer, toolTemplate.getName(), true);
        return true;
    }
    
    private static void addCreatedFenceToBuffer(final StructureConstantsEnum originalFenceType, final StructureConstantsEnum fenceType, final ByteBuffer buffer) {
        buffer.putShort((short)60);
        final String fenceName = WallConstants.getName(fenceType);
        addStringToBuffer(buffer, fenceName, true);
        final int skillNumber = Fence.getSkillNumberNeededForFence(originalFenceType);
        addStringToBuffer(buffer, SkillSystem.getNameFor(skillNumber), true);
    }
    
    private static void addReinforcedWallToBuffer(final ByteBuffer buffer) {
        buffer.putShort((short)60);
        addStringToBuffer(buffer, Tiles.Tile.TILE_CAVE_WALL_REINFORCED.getName(), true);
    }
    
    private static void addCreatedReinforcedWallToBuffer(final byte partCladType, final byte cladType, final ByteBuffer buffer) {
        buffer.putShort((short)60);
        final String fenceName = Tiles.getTile(cladType).getName();
        addStringToBuffer(buffer, fenceName, true);
        final int skillNumber = CaveWallBehaviour.getSkillNumberNeededForCladding(partCladType);
        addStringToBuffer(buffer, SkillSystem.getNameFor(skillNumber), true);
    }
    
    private static final boolean addAdditionalMaterialsForReinforcedWall(final ByteBuffer buffer, final short action) {
        final int[] items = CaveWallBehaviour.getMaterialsNeededTotal(action);
        if (items.length < 2) {
            buffer.putShort((short)0);
        }
        else {
            buffer.putShort((short)(items.length / 2));
            for (int i = 0; i < items.length; i += 2) {
                final ItemTemplate mat = getItemTemplate(items[i]);
                if (mat == null) {
                    return false;
                }
                buffer.putShort(mat.getImageNumber());
                addStringToBuffer(buffer, buildTemplateName(mat, null, (byte)0), true);
                buffer.putShort((short)items[i + 1]);
            }
        }
        return true;
    }
    
    private static final boolean sendCreationRecipes(final SocketConnection connection, @Nonnull final Player player, final RecipesListParameter params) {
        for (final String category : params.getCreationEntries().keySet()) {
            final List<CreationEntry> entries = params.getCreationEntries().get(category);
            for (final CreationEntry entry : entries) {
                final ByteBuffer buffer = connection.getBuffer();
                addCreationRecipesMessageHeaders(buffer);
                addCategoryIdToBuffer(params, category, buffer);
                final ItemTemplate created = getItemTemplate(entry.getObjectCreated());
                final ItemTemplate source = getItemTemplate(entry.getObjectSource());
                final ItemTemplate target = getItemTemplate(entry.getObjectTarget());
                if (created == null || source == null || target == null) {
                    connection.clearBuffer();
                    return false;
                }
                addItemCreatedToRecipesBuffer(entry, buffer, created, source, target);
                addInitialItemUsedToRecipesBuffer(entry, buffer, source, entry.getObjectSourceMaterial());
                addInitialItemUsedToRecipesBuffer(entry, buffer, target, entry.getObjectTargetMaterial());
                if (!addAditionalMaterialsForAdvancedEntries(buffer, entry)) {
                    connection.clearBuffer();
                    return false;
                }
                try {
                    connection.flush();
                }
                catch (IOException iex) {
                    CreationWindowMethods.logger.log(Level.WARNING, "Failed to send creation entries to recipes list", iex);
                    player.setLink(false);
                    return false;
                }
            }
        }
        return true;
    }
    
    private static final boolean addAditionalMaterialsForAdvancedEntries(final ByteBuffer buffer, final CreationEntry entry) {
        if (entry instanceof AdvancedCreationEntry) {
            final AdvancedCreationEntry adv = (AdvancedCreationEntry)entry;
            final CreationRequirement[] reqs = adv.getRequirements();
            buffer.putShort((short)reqs.length);
            for (final CreationRequirement req : reqs) {
                final int id = req.getResourceTemplateId();
                final ItemTemplate mat = getItemTemplate(id);
                if (mat == null) {
                    return false;
                }
                buffer.putShort(mat.getImageNumber());
                addStringToBuffer(buffer, buildTemplateName(mat, null, (byte)0), true);
                buffer.putShort((short)req.getResourceNumber());
            }
        }
        else {
            buffer.putShort((short)0);
        }
        return true;
    }
    
    private static void addInitialItemUsedToRecipesBuffer(final CreationEntry entry, final ByteBuffer buffer, final ItemTemplate item, final byte materialOverride) {
        buffer.putShort(item.getImageNumber());
        addStringToBuffer(buffer, buildTemplateName(item, entry, materialOverride), true);
    }
    
    private static void addItemCreatedToRecipesBuffer(final CreationEntry entry, final ByteBuffer buffer, final ItemTemplate created, final ItemTemplate source, final ItemTemplate target) {
        buffer.putShort(created.getImageNumber());
        addStringToBuffer(buffer, buildTemplateCaptionName(created, source, target), true);
        final String skillName = SkillSystem.getNameFor(entry.getPrimarySkill());
        addStringToBuffer(buffer, skillName, true);
    }
    
    private static void addCategoryIdToBuffer(final RecipesListParameter params, final String category, final ByteBuffer buffer) {
        buffer.putShort((short)(Object)params.getCategoryIds().get(category));
    }
    
    private static void addCreationRecipesMessageHeaders(final ByteBuffer buffer) {
        buffer.put((byte)(-46));
        buffer.put((byte)3);
    }
    
    private static final boolean sendCreationListCategories(final SocketConnection connection, final RecipesListParameter params, final short numberOfEntries) {
        final ByteBuffer buffer = connection.getBuffer();
        addRecipesCategoryListMessageHeadersToBuffer(buffer);
        buffer.putShort((short)params.getTotalCategories());
        addCategoryToBuffer(buffer, params.getCreationEntries().keySet(), params.getCategoryIds());
        addCategoryToBuffer(buffer, params.getFences().keySet(), params.getCategoryIds());
        addCategoryToBuffer(buffer, params.getHedges().keySet(), params.getCategoryIds());
        addCategoryToBuffer(buffer, params.getFlowerbeds().keySet(), params.getCategoryIds());
        addCategoryToBuffer(buffer, params.getWalls().keySet(), params.getCategoryIds());
        addCategoryToBuffer(buffer, params.getRoofs_floors().keySet(), params.getCategoryIds());
        addCategoryToBuffer(buffer, params.getBridgeParts().keySet(), params.getCategoryIds());
        addCategoryToBuffer(buffer, params.getCaveWalls().keySet(), params.getCategoryIds());
        buffer.putShort(numberOfEntries);
        try {
            connection.flush();
            return true;
        }
        catch (IOException iex) {
            CreationWindowMethods.logger.log(Level.WARNING, "An error occured while flushing the categories for the recipes list.", iex);
            connection.clearBuffer();
            return false;
        }
    }
    
    private static void addCategoryToBuffer(final ByteBuffer buffer, final Set<String> categories, final Map<String, Integer> categoryIds) {
        for (final String categoryName : categories) {
            buffer.putShort((short)(Object)categoryIds.get(categoryName));
            addStringToBuffer(buffer, categoryName, true);
        }
    }
    
    private static void addRecipesCategoryListMessageHeadersToBuffer(final ByteBuffer buffer) {
        buffer.put((byte)(-46));
        buffer.put((byte)4);
    }
    
    private static short addCraftingRecipesToRecipesList(final RecipesListParameter params, final CreationEntry[] toAdd, final boolean isSimple) {
        short numberOfEntries = 0;
        for (final CreationEntry entry : toAdd) {
            Label_0150: {
                if (isSimple) {
                    if (CreationMatrix.getInstance().getAdvancedEntriesMap().containsKey(entry.getObjectCreated())) {
                        break Label_0150;
                    }
                    if (entry.getObjectTarget() == 672) {
                        break Label_0150;
                    }
                }
                final String categoryName = entry.getCategory().getCategoryName();
                List<CreationEntry> entries = null;
                if (!params.getCreationEntries().containsKey(categoryName)) {
                    params.getCreationEntries().put(categoryName, new ArrayList<CreationEntry>());
                }
                assignCategoryId(categoryName, params);
                entries = params.getCreationEntries().get(categoryName);
                entries.add(entry);
                ++numberOfEntries;
            }
        }
        return numberOfEntries;
    }
    
    private static final short addFencesToCraftingRecipesList(final RecipesListParameter param) {
        final Map<String, List<ActionEntry>> flist = createFenceCreationList(true, true, false);
        final int[] cTools = MethodsStructure.getCorrectToolsForBuildingFences();
        short numberOfEntries = 0;
        for (final String name : flist.keySet()) {
            final String categoryName = StringUtil.format("%s %s", name, "fences");
            if (!param.getFences().containsKey(categoryName)) {
                param.getFences().put(categoryName, new ArrayList<ActionEntry>());
            }
            assignCategoryId(categoryName, param);
            final List<ActionEntry> entries = param.getFences().get(categoryName);
            for (final ActionEntry entry : flist.get(name)) {
                entries.add(entry);
                numberOfEntries += (short)cTools.length;
            }
        }
        return numberOfEntries;
    }
    
    private static final short addGenericRecipesToList(final Map<String, List<Short>> list, final RecipesListParameter param, final short[] toAdd, final String categoryToAdd) {
        short numberOfEntries = 0;
        assignCategoryId(categoryToAdd, param);
        for (int i = 0; i < toAdd.length; ++i) {
            if (!list.containsKey(categoryToAdd)) {
                list.put(categoryToAdd, new ArrayList<Short>());
            }
            final List<Short> entries = list.get(categoryToAdd);
            entries.add(toAdd[i]);
            ++numberOfEntries;
        }
        return numberOfEntries;
    }
    
    private static void assignCategoryId(final String category, final RecipesListParameter params) {
        if (!params.getCategoryIds().containsKey(category)) {
            params.getCategoryIds().put(category, params.getCategoryIdsSize() + 1);
        }
    }
    
    private static final short addWallsToTheCraftingList(final RecipesListParameter param) {
        short numberOfEntries = 0;
        final String wallsCategory = "Walls";
        assignCategoryId("Walls", param);
        for (final WallEnum en : WallEnum.values()) {
            if (en != WallEnum.WALL_PLAN) {
                if (!param.getWalls().containsKey("Walls")) {
                    param.getWalls().put("Walls", new ArrayList<WallEnum>());
                }
                final List<WallEnum> entries = param.getWalls().get("Walls");
                entries.add(en);
                numberOfEntries += (short)WallEnum.getToolsForWall(en, null).size();
            }
        }
        return numberOfEntries;
    }
    
    private static final short addBridgePartsToTheCraftingList(final RecipesListParameter param) {
        short numberOfEntries = 0;
        for (final BridgePartEnum en : BridgePartEnum.values()) {
            if (en != BridgePartEnum.UNKNOWN) {
                String typeName = StringUtil.toLowerCase(en.getMaterial().getName());
                typeName = StringUtil.format("%s %s", "bridge,", typeName);
                final String categoryName = LoginHandler.raiseFirstLetter(typeName);
                assignCategoryId(categoryName, param);
                if (!param.getBridgeParts().containsKey(categoryName)) {
                    param.getBridgeParts().put(categoryName, new ArrayList<BridgePartEnum>());
                }
                final List<BridgePartEnum> entries = param.getBridgeParts().get(categoryName);
                entries.add(en);
                numberOfEntries += (short)BridgePartEnum.getValidToolsForMaterial(en.getMaterial()).length;
            }
        }
        return numberOfEntries;
    }
    
    private static final short addCaveWallsToTheCraftingList(final RecipesListParameter param) {
        final String wallsCategory = "Cave walls";
        assignCategoryId("Cave walls", param);
        final List<ActionEntry> flist = createCaveWallCreationList();
        short numberOfEntries = 0;
        if (!param.getCaveWalls().containsKey("Cave walls")) {
            param.getCaveWalls().put("Cave walls", new ArrayList<ActionEntry>());
        }
        final List<ActionEntry> entries = param.getCaveWalls().get("Cave walls");
        for (final ActionEntry entry : flist) {
            entries.add(entry);
            numberOfEntries += (short)CaveWallBehaviour.getCorrectToolsForCladding(entry.getNumber()).length;
        }
        return numberOfEntries;
    }
    
    private static final short addRoofsFloorsToTheCraftingList(final RecipesListParameter param) {
        short numberOfEntries = 0;
        for (final RoofFloorEnum en : RoofFloorEnum.values()) {
            if (en != RoofFloorEnum.UNKNOWN) {
                String typeName = en.getType().getName();
                if (typeName.contains("opening")) {
                    typeName = StringUtil.format("%s %s%s", "floor", typeName, "s");
                }
                else if (typeName.contains("staircase,")) {
                    typeName = StringUtil.format("%s", typeName.replace("se,", "ses,"));
                }
                else {
                    typeName = StringUtil.format("%s%s", typeName, "s");
                }
                final String categoryName = LoginHandler.raiseFirstLetter(typeName);
                assignCategoryId(categoryName, param);
                if (!param.getRoofs_floors().containsKey(categoryName)) {
                    param.getRoofs_floors().put(categoryName, new ArrayList<RoofFloorEnum>());
                }
                final List<RoofFloorEnum> entries = param.getRoofs_floors().get(categoryName);
                entries.add(en);
                numberOfEntries += (short)RoofFloorEnum.getValidToolsForMaterial(en.getMaterial()).length;
            }
        }
        return numberOfEntries;
    }
    
    private static final boolean sendCaveWallRecipes(final SocketConnection connection, @Nonnull final Player player, final RecipesListParameter params) {
        for (final String category : params.getCaveWalls().keySet()) {
            final List<ActionEntry> entries = params.getCaveWalls().get(category);
            for (final ActionEntry entry : entries) {
                final byte partCladType = CaveWallBehaviour.getPartReinforcedWallFromAction(entry.getNumber());
                final byte cladType = CaveWallBehaviour.getReinforcedWallFromAction(entry.getNumber());
                final int[] correctTools = CaveWallBehaviour.getCorrectToolsForCladding(entry.getNumber());
                for (int i = 0; i < correctTools.length; ++i) {
                    final ByteBuffer buffer = connection.getBuffer();
                    addCreationRecipesMessageHeaders(buffer);
                    addCategoryIdToBuffer(params, category, buffer);
                    addCreatedReinforcedWallToBuffer(partCladType, cladType, buffer);
                    if (!addFenceToolToBuffer(buffer, correctTools[i])) {
                        connection.clearBuffer();
                        return false;
                    }
                    addReinforcedWallToBuffer(buffer);
                    if (!addAdditionalMaterialsForReinforcedWall(buffer, entry.getNumber())) {
                        connection.clearBuffer();
                        return false;
                    }
                    try {
                        connection.flush();
                    }
                    catch (IOException ex) {
                        CreationWindowMethods.logger.log(Level.WARNING, "IO Exception when sending fence recipes.", ex);
                        player.setLink(false);
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private static final short buildCreationsList(final RecipesListParameter param) {
        short numberOfEntries = 0;
        numberOfEntries += addCraftingRecipesToRecipesList(param, CreationMatrix.getInstance().getSimpleEntries(), true);
        numberOfEntries += addCraftingRecipesToRecipesList(param, CreationMatrix.getInstance().getAdvancedEntries(), false);
        numberOfEntries += addFencesToCraftingRecipesList(param);
        numberOfEntries += addGenericRecipesToList(param.getHedges(), param, Fence.getAllLowHedgeTypes(), "Hedges");
        numberOfEntries += addGenericRecipesToList(param.getFlowerbeds(), param, Fence.getAllFlowerbeds(), "Flowerbeds");
        numberOfEntries += addWallsToTheCraftingList(param);
        numberOfEntries += addRoofsFloorsToTheCraftingList(param);
        numberOfEntries += addBridgePartsToTheCraftingList(param);
        numberOfEntries += addCaveWallsToTheCraftingList(param);
        return numberOfEntries;
    }
    
    static {
        logger = Logger.getLogger(CreationWindowMethods.class.getName());
    }
    
    public static class RecipesListParameter
    {
        private Map<String, List<CreationEntry>> creationEntries;
        private Map<String, Integer> categoryIds;
        private Map<String, List<ActionEntry>> fences;
        private Map<String, List<Short>> hedges;
        private Map<String, List<Short>> flowerbeds;
        private Map<String, List<WallEnum>> walls;
        private Map<String, List<RoofFloorEnum>> roofs_floors;
        private Map<String, List<BridgePartEnum>> bridgeParts;
        private Map<String, List<ActionEntry>> cavewalls;
        
        public RecipesListParameter() {
            this.creationEntries = new HashMap<String, List<CreationEntry>>();
            this.categoryIds = new HashMap<String, Integer>();
            this.fences = new HashMap<String, List<ActionEntry>>();
            this.hedges = new HashMap<String, List<Short>>();
            this.flowerbeds = new HashMap<String, List<Short>>();
            this.walls = new HashMap<String, List<WallEnum>>();
            this.roofs_floors = new HashMap<String, List<RoofFloorEnum>>();
            this.bridgeParts = new HashMap<String, List<BridgePartEnum>>();
            this.cavewalls = new HashMap<String, List<ActionEntry>>();
        }
        
        public Map<String, List<CreationEntry>> getCreationEntries() {
            return this.creationEntries;
        }
        
        public final int getCreationEntriesSize() {
            return this.creationEntries.size();
        }
        
        public Map<String, Integer> getCategoryIds() {
            return this.categoryIds;
        }
        
        public final int getCategoryIdsSize() {
            return this.categoryIds.size();
        }
        
        public Map<String, List<ActionEntry>> getFences() {
            return this.fences;
        }
        
        public final int getFencesSize() {
            return this.fences.size();
        }
        
        public Map<String, List<Short>> getHedges() {
            return this.hedges;
        }
        
        public final int getHedgesSize() {
            return this.hedges.size();
        }
        
        public Map<String, List<Short>> getFlowerbeds() {
            return this.flowerbeds;
        }
        
        public final int getFlowerbedsSize() {
            return this.flowerbeds.size();
        }
        
        public Map<String, List<WallEnum>> getWalls() {
            return this.walls;
        }
        
        public final int getWallsSize() {
            return this.walls.size();
        }
        
        public Map<String, List<RoofFloorEnum>> getRoofs_floors() {
            return this.roofs_floors;
        }
        
        public final int getRoofs_floorsSize() {
            return this.roofs_floors.size();
        }
        
        public Map<String, List<BridgePartEnum>> getBridgeParts() {
            return this.bridgeParts;
        }
        
        public final int getBridgePartsSize() {
            return this.bridgeParts.size();
        }
        
        public Map<String, List<ActionEntry>> getCaveWalls() {
            return this.cavewalls;
        }
        
        public final int getCaveWallsSize() {
            return this.cavewalls.size();
        }
        
        public final int getTotalCategories() {
            return this.getCreationEntriesSize() + this.getFencesSize() + this.getHedgesSize() + this.getFlowerbedsSize() + this.getWallsSize() + this.getRoofs_floorsSize() + this.getBridgePartsSize() + this.getCaveWallsSize();
        }
    }
}
