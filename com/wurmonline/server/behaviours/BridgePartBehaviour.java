// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import javax.annotation.Nonnull;
import com.wurmonline.server.utils.logging.TileEvent;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.questions.MissionManager;
import com.wurmonline.server.players.Permissions;
import com.wurmonline.server.questions.TextInputQuestion;
import com.wurmonline.server.structures.IFloor;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.Server;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.items.NoSuchTemplateException;
import java.util.Iterator;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import java.util.ArrayList;
import com.wurmonline.server.Servers;
import java.io.IOException;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.highways.MethodsHighways;
import com.wurmonline.shared.constants.BridgeConstants;
import com.wurmonline.server.structures.NoSuchStructureException;
import java.util.logging.Level;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.server.items.Item;
import com.wurmonline.mesh.Tiles;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.server.questions.QuestionTypes;

public final class BridgePartBehaviour extends TileBehaviour implements QuestionTypes
{
    private static final Logger logger;
    
    BridgePartBehaviour() {
        super((short)51);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final boolean onSurface, final BridgePart bridgePart) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (!bridgePart.isFinished()) {
            toReturn.add(Actions.actionEntrys[607]);
        }
        toReturn.addAll(Actions.getDefaultTileActions());
        toReturn.addAll(super.getTileAndFloorBehavioursFor(performer, null, bridgePart.getTileX(), bridgePart.getTileY(), Tiles.Tile.TILE_DIRT.id));
        final Structure bridge = Structures.getBridge(bridgePart.getStructureId());
        if (bridge != null && (bridge.isOwner(performer) || performer.getPower() >= 2)) {
            toReturn.add(Actions.actionEntrys[59]);
        }
        return toReturn;
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item source, final boolean onSurface, final BridgePart bridgePart) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        if (!bridgePart.isFinished()) {
            toReturn.add(Actions.actionEntrys[607]);
        }
        toReturn.addAll(super.getTileAndFloorBehavioursFor(performer, source, bridgePart.getTileX(), bridgePart.getTileY(), Tiles.Tile.TILE_DIRT.id));
        final VolaTile floorTile = Zones.getOrCreateTile(bridgePart.getTileX(), bridgePart.getTileY(), bridgePart.isOnSurface());
        final BridgeConstants.BridgeState bpState = bridgePart.getBridgePartState();
        Structure structure = null;
        try {
            structure = Structures.getStructure(bridgePart.getStructureId());
        }
        catch (NoSuchStructureException e) {
            BridgePartBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
            toReturn.addAll(Actions.getDefaultItemActions());
            return toReturn;
        }
        if (MethodsStructure.mayModifyStructure(performer, structure, floorTile, (short)169)) {
            if (bpState.isBeingBuilt()) {
                toReturn.add(new ActionEntry((short)169, "Continue building", "building"));
            }
            else if (bpState == BridgeConstants.BridgeState.PLANNED) {
                toReturn.add(new ActionEntry((short)(20000 + bridgePart.getMaterial().getCode()), "Build " + bridgePart.getMaterial().getName().toLowerCase() + " " + bridgePart.getType().getName(), bridgePart.getMaterial().getName() + " " + bridgePart.getType().getName(), BridgePartBehaviour.emptyIntArr));
            }
        }
        if (!source.isTraded()) {
            if (source.getTemplateId() == bridgePart.getRepairItemTemplate()) {
                if (bridgePart.getDamage() > 0.0f) {
                    if (!bridgePart.isNoRepair()) {
                        toReturn.add(Actions.actionEntrys[193]);
                    }
                }
                else if (bridgePart.getQualityLevel() < 100.0f && !bridgePart.isNoImprove()) {
                    toReturn.add(Actions.actionEntrys[192]);
                }
            }
            toReturn.addAll(Actions.getDefaultItemActions());
            if (!bridgePart.isIndestructible() && !MethodsHighways.onHighway(bridgePart)) {
                toReturn.add(new ActionEntry((short)(-1), "Destroy", "Destroy"));
                toReturn.add(new ActionEntry((short)524, bridgePart.getName(), "destroying"));
            }
            if ((source.getTemplateId() == 176 || source.getTemplateId() == 315) && WurmPermissions.mayUseGMWand(performer) && performer.getPower() >= 2) {
                toReturn.add(new ActionEntry((short)(-1), "Annihilate", "Annihilate"));
                toReturn.add(new ActionEntry((short)82, "Destroy " + bridgePart.getMaterial().getName() + " bridge", "destroying"));
            }
            if ((source.getTemplateId() == 315 || source.getTemplateId() == 176) && performer.getPower() >= 2) {
                toReturn.add(Actions.actionEntrys[684]);
            }
            if (bridgePart.isFinished() && bridgePart.getMaterial() != BridgeConstants.BridgeMaterial.ROPE && bridgePart.getMaterial() != BridgeConstants.BridgeMaterial.WOOD) {
                final byte roadType = bridgePart.getRoadType();
                if ((source.getTemplateId() == 492 || (WurmPermissions.mayUseDeityWand(performer) && source.getTemplateId() == 176) || (WurmPermissions.mayUseGMWand(performer) && source.getTemplateId() == 315)) && roadType == 0) {
                    toReturn.add(new ActionEntry((short)155, "Prepare", "preparing the floor"));
                }
                else if ((source.getTemplateId() == 97 || (WurmPermissions.mayUseDeityWand(performer) && source.getTemplateId() == 176) || (WurmPermissions.mayUseGMWand(performer) && source.getTemplateId() == 315)) && roadType == Tiles.Tile.TILE_PREPARED_BRIDGE.id) {
                    toReturn.add(new ActionEntry((short)191, "Remove mortar", "removing mortar"));
                }
                else if (source.isPaveable() && roadType == Tiles.Tile.TILE_PREPARED_BRIDGE.id) {
                    toReturn.add(Actions.actionEntrys[155]);
                }
                else if ((source.getTemplateId() == 1115 || (WurmPermissions.mayUseDeityWand(performer) && source.getTemplateId() == 176) || (WurmPermissions.mayUseGMWand(performer) && source.getTemplateId() == 315)) && Tiles.isRoadType(roadType)) {
                    toReturn.add(Actions.actionEntrys[191]);
                }
                else if (source.getTemplateId() == 153 && roadType == Tiles.Tile.TILE_PLANKS.id) {
                    toReturn.add(new ActionEntry((short)231, "Tar", "tarring"));
                }
                if (MethodsHighways.onHighway(bridgePart) && source.isPaveable() && roadType != 0 && roadType != Tiles.Tile.TILE_PREPARED_BRIDGE.id && source.isPaveable() && roadType != 0) {
                    toReturn.add(new ActionEntry((short)155, "Replace paving", "re-paving"));
                }
            }
        }
        final Structure bridge = Structures.getBridge(bridgePart.getStructureId());
        if (bridge != null && (bridge.isOwner(performer) || performer.getPower() >= 2)) {
            toReturn.add(Actions.actionEntrys[59]);
        }
        return toReturn;
    }
    
    private boolean buildAction(final Action act, final Creature performer, final Item source, final BridgePart bridgePart, final short action, final float counter) {
        if (bridgePart.getBridgePartState() == BridgeConstants.BridgeState.PLANNED) {
            if (action - 20000 != bridgePart.getMaterial().getCode() && action != 169) {
                performer.getCommunicator().sendNormalServerMessage("You Cannot build a " + bridgePart.getName() + " made of " + BridgeConstants.BridgeMaterial.fromByte((byte)(action - 20000)).getName().toLowerCase() + " as It's already planned to be made of " + bridgePart.getMaterial().getName().toLowerCase() + ".");
                return true;
            }
            if (!isOkToBuild(performer, source, bridgePart)) {
                return true;
            }
            bridgePart.setBridgePartState(BridgeConstants.BridgeState.STAGE1);
            bridgePart.setMaterialCount(0);
            final BuildAllMaterials bam = getRequiredMaterials(bridgePart);
            bam.setNeeded(bridgePart.getState(), bridgePart.getMaterialCount());
            try {
                bridgePart.save();
                final VolaTile bridgeTile = Zones.getOrCreateTile(bridgePart.getTileX(), bridgePart.getTileY(), bridgePart.isOnSurface());
                bridgeTile.updateBridgePart(bridgePart);
            }
            catch (IOException e) {
                BridgePartBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
            }
            performer.getCommunicator().sendNormalServerMessage("You prepare a " + bridgePart.getName() + ".");
        }
        if (!bridgePart.getBridgePartState().isBeingBuilt()) {
            BridgePartBehaviour.logger.log(Level.WARNING, "BridgePartBehaviour buildAction on a completed bridge part, it should not happen?!");
            performer.getCommunicator().sendNormalServerMessage("You failed to find anything to do with that.");
            return true;
        }
        if (bridgePartBuilding(act, performer, source, bridgePart, (short)169, counter)) {
            performer.getCommunicator().sendAddBridgePartToCreationWindow(bridgePart, bridgePart.getId());
            return true;
        }
        return false;
    }
    
    static boolean advanceNextState(final Creature performer, final BridgePart bridgePart, final Action act, final boolean justCheckIfItemsArePresent) {
        final BuildAllMaterials mats = getRequiredMaterials(bridgePart);
        if (takeItemsFromCreature(performer, bridgePart, act, mats, justCheckIfItemsArePresent)) {
            return true;
        }
        if (performer.getPower() >= 2 && !justCheckIfItemsArePresent && Servers.isThisATestServer()) {
            performer.getCommunicator().sendNormalServerMessage("You magically summon some necessary materials.");
            return true;
        }
        return false;
    }
    
    static boolean takeItemsFromCreature(final Creature performer, final BridgePart bridgePart, final Action act, final BuildAllMaterials bam, final boolean justCheckIfItemsArePresent) {
        final Item[] inventoryItems = performer.getInventory().getAllItems(false);
        final Item[] bodyItems = performer.getBody().getAllItems();
        final List<Item> takeItemsOnSuccess = new ArrayList<Item>();
        final List<BuildMaterial> mats = bam.getBuildStageMaterials(bridgePart.getState()).getBuildMaterials();
        for (final Item item : inventoryItems) {
            for (final BuildMaterial mat : mats) {
                if (mat.getNeededQuantity() > 0 && item.getTemplateId() == mat.getTemplateId() && item.getWeightGrams() >= mat.getWeightGrams()) {
                    takeItemsOnSuccess.add(item);
                    mat.setNeededQuantity(0);
                    break;
                }
            }
        }
        for (final Item item : bodyItems) {
            for (final BuildMaterial mat : mats) {
                if (mat.getNeededQuantity() > 0 && item.getTemplateId() == mat.getTemplateId() && item.getWeightGrams() >= mat.getWeightGrams()) {
                    takeItemsOnSuccess.add(item);
                    mat.setNeededQuantity(0);
                    break;
                }
            }
        }
        float divider = 1.0f;
        for (final BuildMaterial mat2 : mats) {
            divider += mat2.getTotalQuantityRequired();
            if (mat2.getNeededQuantity() > 0) {
                return false;
            }
        }
        float qlevel = 0.0f;
        if (!justCheckIfItemsArePresent) {
            for (final Item item : takeItemsOnSuccess) {
                try {
                    Items.getItem(item.getWurmId());
                }
                catch (NoSuchItemException nsie) {
                    performer.getCommunicator().sendAlertServerMessage("ERROR: " + item.getName() + " not found, WurmID: " + item.getWurmId());
                    return false;
                }
                act.setPower(item.getCurrentQualityLevel() / divider);
                performer.sendToLoggers("Adding " + item.getCurrentQualityLevel() + ", divider=" + divider + "=" + act.getPower());
                qlevel += item.getCurrentQualityLevel() / 21.0f;
                if (item.isCombine()) {
                    item.setWeight(item.getWeightGrams() - item.getTemplate().getWeightGrams(), true);
                }
                else {
                    Items.destroyItem(item.getWurmId());
                }
            }
        }
        act.setPower(qlevel);
        return true;
    }
    
    public static int getRequiredSkill(final BridgeConstants.BridgeMaterial material) {
        switch (material) {
            case BRICK: {
                return 1013;
            }
            case MARBLE:
            case SLATE:
            case ROUNDED_STONE:
            case POTTERY:
            case SANDSTONE:
            case RENDERED: {
                return 1013;
            }
            case ROPE: {
                return 1014;
            }
            case WOOD: {
                return 1005;
            }
            default: {
                return 1005;
            }
        }
    }
    
    public static BuildAllMaterials getRequiredMaterials(final BridgePart bridgePart) {
        final BuildAllMaterials toReturn = getRequiredMaterials(bridgePart.getType(), bridgePart.getMaterial(), bridgePart.getNumberOfExtensions());
        toReturn.setNeeded(bridgePart.getState(), bridgePart.getMaterialCount());
        return toReturn;
    }
    
    public static BuildAllMaterials getRequiredMaterials(final BridgeConstants.BridgeType bridgeType, final BridgeConstants.BridgeMaterial bridgeMaterial, final int bridgeExtensions) {
        final BuildAllMaterials toReturn = new BuildAllMaterials();
        final BuildStageMaterials main = new BuildStageMaterials("Main");
        final BuildStageMaterials foundations = new BuildStageMaterials("Foundations");
        final BuildStageMaterials ballast = new BuildStageMaterials("Ballast");
        final BuildStageMaterials filler = new BuildStageMaterials("Filler");
        final BuildStageMaterials roadway = new BuildStageMaterials("Roadway");
        final BuildStageMaterials wall = new BuildStageMaterials("Walls");
        final BuildStageMaterials keystone = new BuildStageMaterials("Keystone");
        try {
            switch (bridgeMaterial) {
                case WOOD: {
                    if (bridgeType.isAbutment()) {
                        main.add(860, 8);
                        main.add(188, 4);
                        main.add(217, 2);
                    }
                    if (bridgeType.isCrown()) {
                        main.add(860, 4);
                        main.add(188, 2);
                        main.add(217, 1);
                    }
                    if (bridgeType.isSupportType()) {
                        main.add(860, 12);
                        main.add(188, 6);
                        main.add(217, 3);
                        if (bridgeExtensions > 0) {
                            foundations.add(860, 4 * bridgeExtensions);
                            foundations.add(188, 2 * bridgeExtensions);
                            foundations.add(217, 1 * bridgeExtensions);
                        }
                    }
                    roadway.add(22, 20);
                    roadway.add(217, 2);
                    final int wcw = bridgeType.wallCount();
                    if (wcw > 0) {
                        wall.add(22, 2 * wcw);
                        wall.add(23, 2 * wcw);
                        wall.add(218, 1 * wcw);
                        break;
                    }
                    break;
                }
                case BRICK: {
                    if (bridgeType.isAbutment()) {
                        main.add(132, 40);
                        main.add(492, 40);
                        filler.add(146, 16);
                    }
                    if (bridgeType.isBracing()) {
                        main.add(132, 25);
                        main.add(492, 25);
                        filler.add(146, 8);
                    }
                    if (bridgeType.isCrown()) {
                        main.add(132, 10);
                        main.add(492, 10);
                        keystone.add(905, 1);
                        filler.add(146, 4);
                    }
                    if (bridgeType.isFloating()) {
                        main.add(132, 10);
                        main.add(492, 10);
                        filler.add(146, 4);
                    }
                    if (bridgeType.isDoubleBracing()) {
                        main.add(132, 30);
                        main.add(492, 30);
                        filler.add(146, 12);
                    }
                    if (bridgeType.isDoubleAbutment()) {
                        main.add(132, 60);
                        main.add(492, 60);
                        filler.add(146, 24);
                    }
                    if (bridgeType.isSupportType()) {
                        main.add(132, 90);
                        main.add(492, 90);
                        filler.add(146, 36);
                        if (bridgeExtensions > 0) {
                            foundations.add(132, 30 * bridgeExtensions);
                            foundations.add(492, 30 * bridgeExtensions);
                            ballast.add(146, 12 * bridgeExtensions);
                        }
                    }
                    roadway.add(132, 2);
                    final int wcs = bridgeType.wallCount();
                    if (wcs > 0) {
                        wall.add(132, 10 * wcs);
                        break;
                    }
                    break;
                }
                case MARBLE: {
                    if (bridgeType.isAbutment()) {
                        main.add(786, 40);
                        main.add(492, 40);
                        filler.add(146, 16);
                    }
                    if (bridgeType.isBracing()) {
                        main.add(786, 25);
                        main.add(492, 25);
                        filler.add(146, 8);
                    }
                    if (bridgeType.isCrown()) {
                        main.add(786, 10);
                        main.add(492, 10);
                        keystone.add(906, 1);
                        filler.add(146, 4);
                    }
                    if (bridgeType.isFloating()) {
                        main.add(786, 10);
                        main.add(492, 10);
                        filler.add(146, 4);
                    }
                    if (bridgeType.isDoubleBracing()) {
                        main.add(786, 30);
                        main.add(492, 30);
                        filler.add(146, 12);
                    }
                    if (bridgeType.isDoubleAbutment()) {
                        main.add(786, 60);
                        main.add(492, 60);
                        filler.add(146, 24);
                    }
                    if (bridgeType.isSupportType()) {
                        main.add(786, 90);
                        main.add(492, 90);
                        filler.add(146, 36);
                        if (bridgeExtensions > 0) {
                            foundations.add(786, 30 * bridgeExtensions);
                            foundations.add(492, 30 * bridgeExtensions);
                            ballast.add(146, 12 * bridgeExtensions);
                        }
                    }
                    roadway.add(786, 2);
                    final int wcm = bridgeType.wallCount();
                    if (wcm > 0) {
                        wall.add(786, 10 * wcm);
                        break;
                    }
                    break;
                }
                case SLATE: {
                    if (bridgeType.isAbutment()) {
                        main.add(1123, 40);
                        main.add(492, 40);
                        filler.add(146, 16);
                    }
                    if (bridgeType.isBracing()) {
                        main.add(1123, 25);
                        main.add(492, 25);
                        filler.add(146, 8);
                    }
                    if (bridgeType.isCrown()) {
                        main.add(1123, 10);
                        main.add(492, 10);
                        keystone.add(1302, 1);
                        filler.add(146, 4);
                    }
                    if (bridgeType.isFloating()) {
                        main.add(1123, 10);
                        main.add(492, 10);
                        filler.add(146, 4);
                    }
                    if (bridgeType.isDoubleBracing()) {
                        main.add(1123, 30);
                        main.add(492, 30);
                        filler.add(146, 12);
                    }
                    if (bridgeType.isDoubleAbutment()) {
                        main.add(1123, 60);
                        main.add(492, 60);
                        filler.add(146, 24);
                    }
                    if (bridgeType.isSupportType()) {
                        main.add(1123, 90);
                        main.add(492, 90);
                        filler.add(146, 36);
                        if (bridgeExtensions > 0) {
                            foundations.add(1123, 30 * bridgeExtensions);
                            foundations.add(492, 30 * bridgeExtensions);
                            ballast.add(146, 12 * bridgeExtensions);
                        }
                    }
                    roadway.add(1123, 2);
                    final int wcm = bridgeType.wallCount();
                    if (wcm > 0) {
                        wall.add(1123, 10 * wcm);
                        break;
                    }
                    break;
                }
                case ROUNDED_STONE: {
                    if (bridgeType.isAbutment()) {
                        main.add(1122, 40);
                        main.add(492, 40);
                        filler.add(146, 16);
                    }
                    if (bridgeType.isBracing()) {
                        main.add(1122, 25);
                        main.add(492, 25);
                        filler.add(146, 8);
                    }
                    if (bridgeType.isCrown()) {
                        main.add(1122, 10);
                        main.add(492, 10);
                        keystone.add(905, 1);
                        filler.add(146, 4);
                    }
                    if (bridgeType.isFloating()) {
                        main.add(1122, 10);
                        main.add(492, 10);
                        filler.add(146, 4);
                    }
                    if (bridgeType.isDoubleBracing()) {
                        main.add(1122, 30);
                        main.add(492, 30);
                        filler.add(146, 12);
                    }
                    if (bridgeType.isDoubleAbutment()) {
                        main.add(1122, 60);
                        main.add(492, 60);
                        filler.add(146, 24);
                    }
                    if (bridgeType.isSupportType()) {
                        main.add(1122, 90);
                        main.add(492, 90);
                        filler.add(146, 36);
                        if (bridgeExtensions > 0) {
                            foundations.add(1122, 30 * bridgeExtensions);
                            foundations.add(492, 30 * bridgeExtensions);
                            ballast.add(146, 12 * bridgeExtensions);
                        }
                    }
                    roadway.add(1122, 2);
                    final int wcm = bridgeType.wallCount();
                    if (wcm > 0) {
                        wall.add(1122, 10 * wcm);
                        break;
                    }
                    break;
                }
                case POTTERY: {
                    if (bridgeType.isAbutment()) {
                        main.add(776, 40);
                        main.add(492, 40);
                        filler.add(146, 16);
                    }
                    if (bridgeType.isBracing()) {
                        main.add(776, 25);
                        main.add(492, 25);
                        filler.add(146, 8);
                    }
                    if (bridgeType.isCrown()) {
                        main.add(776, 10);
                        main.add(492, 10);
                        keystone.add(1304, 1);
                        filler.add(146, 4);
                    }
                    if (bridgeType.isFloating()) {
                        main.add(776, 10);
                        main.add(492, 10);
                        filler.add(146, 4);
                    }
                    if (bridgeType.isDoubleBracing()) {
                        main.add(776, 30);
                        main.add(492, 30);
                        filler.add(146, 12);
                    }
                    if (bridgeType.isDoubleAbutment()) {
                        main.add(776, 60);
                        main.add(492, 60);
                        filler.add(146, 24);
                    }
                    if (bridgeType.isSupportType()) {
                        main.add(776, 90);
                        main.add(492, 90);
                        filler.add(146, 36);
                        if (bridgeExtensions > 0) {
                            foundations.add(776, 30 * bridgeExtensions);
                            foundations.add(492, 30 * bridgeExtensions);
                            ballast.add(146, 12 * bridgeExtensions);
                        }
                    }
                    roadway.add(776, 2);
                    final int wcm = bridgeType.wallCount();
                    if (wcm > 0) {
                        wall.add(776, 10 * wcm);
                        break;
                    }
                    break;
                }
                case SANDSTONE: {
                    if (bridgeType.isAbutment()) {
                        main.add(1121, 40);
                        main.add(492, 40);
                        filler.add(146, 16);
                    }
                    if (bridgeType.isBracing()) {
                        main.add(1121, 25);
                        main.add(492, 25);
                        filler.add(146, 8);
                    }
                    if (bridgeType.isCrown()) {
                        main.add(1121, 10);
                        main.add(492, 10);
                        keystone.add(1305, 1);
                        filler.add(146, 4);
                    }
                    if (bridgeType.isFloating()) {
                        main.add(1121, 10);
                        main.add(492, 10);
                        filler.add(146, 4);
                    }
                    if (bridgeType.isDoubleBracing()) {
                        main.add(1121, 30);
                        main.add(492, 30);
                        filler.add(146, 12);
                    }
                    if (bridgeType.isDoubleAbutment()) {
                        main.add(1121, 60);
                        main.add(492, 60);
                        filler.add(146, 24);
                    }
                    if (bridgeType.isSupportType()) {
                        main.add(1121, 90);
                        main.add(492, 90);
                        filler.add(146, 36);
                        if (bridgeExtensions > 0) {
                            foundations.add(1121, 30 * bridgeExtensions);
                            foundations.add(492, 30 * bridgeExtensions);
                            ballast.add(146, 12 * bridgeExtensions);
                        }
                    }
                    roadway.add(1121, 2);
                    final int wcm = bridgeType.wallCount();
                    if (wcm > 0) {
                        wall.add(1121, 10 * wcm);
                        break;
                    }
                    break;
                }
                case RENDERED: {
                    if (bridgeType.isAbutment()) {
                        main.add(132, 40);
                        main.add(492, 40);
                        main.add(130, 4);
                        filler.add(146, 16);
                    }
                    if (bridgeType.isBracing()) {
                        main.add(132, 25);
                        main.add(492, 25);
                        main.add(130, 2);
                        filler.add(146, 8);
                    }
                    if (bridgeType.isCrown()) {
                        main.add(132, 10);
                        main.add(492, 10);
                        main.add(130, 1);
                        keystone.add(905, 1);
                        filler.add(146, 4);
                    }
                    if (bridgeType.isFloating()) {
                        main.add(132, 10);
                        main.add(492, 10);
                        main.add(130, 1);
                        filler.add(146, 4);
                    }
                    if (bridgeType.isDoubleBracing()) {
                        main.add(132, 30);
                        main.add(492, 30);
                        main.add(130, 3);
                        filler.add(146, 12);
                    }
                    if (bridgeType.isDoubleAbutment()) {
                        main.add(132, 60);
                        main.add(492, 60);
                        main.add(130, 6);
                        filler.add(146, 24);
                    }
                    if (bridgeType.isSupportType()) {
                        main.add(132, 90);
                        main.add(492, 90);
                        main.add(130, 9);
                        filler.add(146, 36);
                        if (bridgeExtensions > 0) {
                            foundations.add(132, 30 * bridgeExtensions);
                            foundations.add(492, 30 * bridgeExtensions);
                            foundations.add(130, 3 * bridgeExtensions);
                            ballast.add(146, 12 * bridgeExtensions);
                        }
                    }
                    roadway.add(132, 2);
                    roadway.add(130, 1);
                    final int wcm = bridgeType.wallCount();
                    if (wcm > 0) {
                        wall.add(132, 10 * wcm);
                        wall.add(130, wcm);
                        break;
                    }
                    break;
                }
                case ROPE: {
                    if (bridgeType.isAbutment()) {
                        main.add(557, 2);
                        main.add(9, 4);
                        main.add(558, 2);
                    }
                    if (bridgeType.isDoubleAbutment()) {
                        main.add(557, 4);
                        main.add(9, 8);
                        main.add(558, 4);
                    }
                    if (bridgeType.isCrown()) {
                        main.add(558, 2);
                    }
                    roadway.add(22, 10);
                    wall.add(558, 4);
                    wall.add(559, 4);
                    break;
                }
            }
            if (roadway.getBuildMaterials().size() > 0) {
                int stageNo = 1;
                main.setStageNumber(stageNo++);
                toReturn.add(main);
                if (foundations.getBuildMaterials().size() > 0) {
                    foundations.setStageNumber(stageNo++);
                    toReturn.add(foundations);
                }
                if (ballast.getBuildMaterials().size() > 0) {
                    ballast.setStageNumber(stageNo++);
                    toReturn.add(ballast);
                }
                if (filler.getBuildMaterials().size() > 0) {
                    filler.setStageNumber(stageNo++);
                    toReturn.add(filler);
                }
                if (keystone.getBuildMaterials().size() > 0) {
                    keystone.setStageNumber(stageNo++);
                    toReturn.add(keystone);
                }
                if (roadway.getBuildMaterials().size() > 0) {
                    roadway.setStageNumber(stageNo++);
                    toReturn.add(roadway);
                }
                if (wall.getBuildMaterials().size() > 0) {
                    wall.setStageNumber(stageNo++);
                    toReturn.add(wall);
                }
            }
            else {
                BridgePartBehaviour.logger.log(Level.WARNING, "Someone tried to make a bridge but the material choice was not supported (" + bridgeMaterial.toString() + ")");
            }
        }
        catch (NoSuchTemplateException nste) {
            BridgePartBehaviour.logger.log(Level.WARNING, "BridgePartBehaviour.getRequiredMaterials trying to use material that have a non existing template.", nste);
        }
        return toReturn;
    }
    
    static final boolean isOkToBuild(final Creature performer, final Item tool, final BridgePart bridgePart) {
        if (bridgePart == null) {
            performer.getCommunicator().sendNormalServerMessage("You fail to focus, and cannot find that bridge part.");
            return false;
        }
        if (!hasValidTool(bridgePart.getMaterial(), tool)) {
            performer.getCommunicator().sendNormalServerMessage("You need to activate the correct building tool if you want to build that.");
            return false;
        }
        final Skill buildSkill = getBuildSkill(bridgePart.getType(), bridgePart.getMaterial(), performer);
        if (buildSkill.getKnowledge(0.0) < bridgePart.minRequiredSkill()) {
            performer.getCommunicator().sendNormalServerMessage("You are not skilled enough to build that.");
            return false;
        }
        if (performer.getTileX() == bridgePart.getTileX() && performer.getTileY() == bridgePart.getTileY()) {
            performer.getCommunicator().sendNormalServerMessage("You cannot work on the bridge part when standing on the same tile.");
            return false;
        }
        if (performer.getBridgeId() == bridgePart.getStructureId()) {
            return true;
        }
        if (!bridgePart.hasAnExit()) {
            performer.getCommunicator().sendNormalServerMessage("You cannot work on the bridge part when not standing on the bridge.");
            return false;
        }
        boolean ok = false;
        if (performer.getTileX() == bridgePart.getTileX() && performer.getTileY() + 1 == bridgePart.getTileY() && bridgePart.hasNorthExit()) {
            ok = true;
        }
        else if (performer.getTileX() == bridgePart.getTileX() && performer.getTileY() - 1 == bridgePart.getTileY() && bridgePart.hasSouthExit()) {
            ok = true;
        }
        else if (performer.getTileX() + 1 == bridgePart.getTileX() && performer.getTileY() == bridgePart.getTileY() && bridgePart.hasWestExit()) {
            ok = true;
        }
        else if (performer.getTileX() - 1 == bridgePart.getTileX() && performer.getTileY() == bridgePart.getTileY() && bridgePart.hasEastExit()) {
            ok = true;
        }
        if (!ok) {
            performer.getCommunicator().sendNormalServerMessage("You cannot work on the bridge part when not standing next to it.");
            return false;
        }
        return true;
    }
    
    private static final boolean bridgePartBuilding(final Action act, final Creature performer, final Item source, final BridgePart bridgePart, final short action, final float counter) {
        if (!isOkToBuild(performer, source, bridgePart)) {
            return true;
        }
        int time = 10;
        final boolean insta = ((Servers.isThisATestServer() && performer.getPower() > 1) || performer.getPower() >= 4) && (source.getTemplateId() == 315 || source.getTemplateId() == 176);
        if (bridgePart.isFinished()) {
            performer.getCommunicator().sendNormalServerMessage("The " + bridgePart.getName() + " is finished already.");
            performer.getCommunicator().sendActionResult(false);
            return true;
        }
        if (!Methods.isActionAllowed(performer, (short)116, bridgePart.getTileX(), bridgePart.getTileY())) {
            return true;
        }
        if (counter == 1.0f) {
            try {
                Structures.getStructure(bridgePart.getStructureId());
            }
            catch (NoSuchStructureException e) {
                BridgePartBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
                performer.getCommunicator().sendNormalServerMessage("Your sensitive mind notices a wrongness in the fabric of space.");
                performer.getCommunicator().sendActionResult(false);
                return true;
            }
            if (!advanceNextState(performer, bridgePart, act, true) && !insta) {
                final BuildAllMaterials bam = getRequiredMaterials(bridgePart);
                final BuildStageMaterials bsm = bam.getBuildStageMaterials(bridgePart.getState());
                final String message = bsm.getRequiredMaterialString(false);
                performer.getCommunicator().sendNormalServerMessage("You need " + message + " to start building the " + bridgePart.getName() + " of " + bridgePart.getMaterial().getName().toLowerCase() + ".");
                performer.getCommunicator().sendActionResult(false);
                return true;
            }
            final Skill buildSkill = getBuildSkill(bridgePart.getType(), bridgePart.getMaterial(), performer);
            time = Actions.getSlowActionTime(performer, buildSkill, source, 0.0);
            act.setTimeLeft(time);
            performer.getStatus().modifyStamina(-1000.0f);
            damageTool(performer, bridgePart, source);
            Server.getInstance().broadCastAction(performer.getName() + " continues to build a " + bridgePart.getName() + ".", performer, 5);
            performer.getCommunicator().sendNormalServerMessage("You continue to build a " + bridgePart.getName() + ".");
            if (!insta) {
                performer.sendActionControl("Building a " + bridgePart.getName(), true, time);
            }
        }
        else {
            time = act.getTimeLeft();
            if (act.currentSecond() % 5 == 0) {
                SoundPlayer.playSound(bridgePart.getSoundByMaterial(), bridgePart.getTileX(), bridgePart.getTileY(), performer.isOnSurface(), 1.6f);
                performer.getStatus().modifyStamina(-1000.0f);
                damageTool(performer, bridgePart, source);
            }
        }
        if (counter * 10.0f <= time && !insta) {
            return false;
        }
        final BuildAllMaterials bam = getRequiredMaterials(bridgePart);
        final BuildStageMaterials bsm = bam.getBuildStageMaterials(bridgePart.getState());
        final String message = bsm.getRequiredMaterialString(false);
        if (!advanceNextState(performer, bridgePart, act, false) && !insta) {
            performer.getCommunicator().sendNormalServerMessage("You need " + message + " to build the " + bridgePart.getName() + ".");
            performer.getCommunicator().sendActionResult(false);
            return true;
        }
        double bonus = 0.0;
        final Skill toolSkill = getToolSkill(bridgePart, performer, source);
        if (toolSkill != null) {
            toolSkill.skillCheck(10.0, source, 0.0, false, counter);
            bonus = toolSkill.getKnowledge(source, 0.0) / 10.0;
        }
        final Skill buildSkill2 = getBuildSkill(bridgePart.getType(), bridgePart.getMaterial(), performer);
        final double check = buildSkill2.skillCheck(buildSkill2.getRealKnowledge(), source, bonus, false, counter);
        int inc = 1;
        if (WurmPermissions.mayUseGMWand(performer) && (source.getTemplateId() == 315 || source.getTemplateId() == 176)) {
            if (Servers.isThisATestServer()) {
                inc = 100;
            }
            else {
                inc = 10;
            }
        }
        bridgePart.buildProgress(inc);
        bam.setNeeded(bridgePart.getState(), bridgePart.getMaterialCount());
        Server.getInstance().broadCastAction(performer.getName() + " attaches " + message + " to a " + bridgePart.getName() + ".", performer, 5);
        performer.getCommunicator().sendNormalServerMessage("You attach " + message + " to a " + bridgePart.getName() + ".");
        final float divider = bam.getTotalQuantityRequired();
        final float oldql = bridgePart.getQualityLevel();
        final float qlevel = Math.max(1.0f, (float)Math.min(act.getPower() + oldql, oldql + buildSkill2.getKnowledge(0.0) / divider));
        bridgePart.setQualityLevel(qlevel);
        if (bsm.isStageComplete(bridgePart)) {
            performer.getCommunicator().sendNormalServerMessage(bsm.getName() + " stage completed!");
            if (bam.getStageCount() - 1 >= bridgePart.getState() + 1) {
                bridgePart.incBridgePartStage();
            }
            else {
                if (insta) {
                    bridgePart.setQualityLevel(80.0f);
                    if (!Servers.isThisATestServer()) {
                        BridgePartBehaviour.logger.info("Insta Bridge: " + performer.getName() + " completed bridge part at " + bridgePart.getTileX() + "," + bridgePart.getTileY());
                    }
                }
                bridgePart.setBridgePartState(BridgeConstants.BridgeState.COMPLETED);
            }
            final VolaTile bridgeTile = Zones.getOrCreateTile(bridgePart.getTileX(), bridgePart.getTileY(), bridgePart.isOnSurface());
            bridgeTile.updateBridgePart(bridgePart);
            if (bam.getStageCount() - 1 < bridgePart.getState()) {
                Server.getInstance().broadCastAction(performer.getName() + " completes a " + bridgePart.getName() + ".", performer, 5);
                performer.getCommunicator().sendNormalServerMessage(bridgePart.getName() + " completed!");
            }
        }
        try {
            bridgePart.save();
            if (bridgePart.getState() == BridgeConstants.BridgeState.COMPLETED.getCode()) {
                final Structure structure = Structures.getStructure(bridgePart.getStructureId());
                if (!structure.isFinished() && structure.updateStructureFinishFlag()) {
                    if (bridgePart.getMaterial() == BridgeConstants.BridgeMaterial.ROPE) {
                        performer.achievement(358);
                    }
                    else if (bridgePart.getMaterial() == BridgeConstants.BridgeMaterial.WOOD) {
                        performer.achievement(359);
                    }
                    else if (bridgePart.getMaterial() == BridgeConstants.BridgeMaterial.BRICK) {
                        performer.achievement(360);
                    }
                    else if (bridgePart.getMaterial() == BridgeConstants.BridgeMaterial.MARBLE) {
                        performer.achievement(361);
                    }
                }
            }
        }
        catch (IOException e2) {
            BridgePartBehaviour.logger.log(Level.WARNING, e2.getMessage(), e2);
        }
        catch (NoSuchStructureException e3) {
            BridgePartBehaviour.logger.log(Level.WARNING, e3.getMessage(), e3);
        }
        if (bridgePart.isFinished()) {
            performer.getCommunicator().sendRemoveFromCreationWindow(bridgePart.getId());
        }
        else {
            performer.getCommunicator().sendAddBridgePartToCreationWindow(bridgePart, bridgePart.getId());
        }
        performer.getCommunicator().sendActionResult(true);
        return true;
    }
    
    public static final Skill getBuildSkill(final BridgeConstants.BridgeType bridgepartType, final BridgeConstants.BridgeMaterial bridgeMaterial, final Creature performer) {
        return performer.getSkills().getSkillOrLearn(getRequiredSkill(bridgeMaterial));
    }
    
    private static boolean hasValidTool(final BridgeConstants.BridgeMaterial bridgeMaterial, final Item source) {
        if (bridgeMaterial == BridgeConstants.BridgeMaterial.ROPE) {
            return true;
        }
        if (source == null || bridgeMaterial == null) {
            return false;
        }
        final int tid = source.getTemplateId();
        boolean hasRightTool = false;
        switch (bridgeMaterial) {
            case BRICK: {
                hasRightTool = (tid == 493);
                break;
            }
            case WOOD: {
                hasRightTool = (tid == 62 || tid == 63);
                break;
            }
            case MARBLE:
            case SLATE:
            case ROUNDED_STONE:
            case POTTERY:
            case SANDSTONE:
            case RENDERED: {
                hasRightTool = (tid == 493);
                break;
            }
            default: {
                BridgePartBehaviour.logger.log(Level.WARNING, "Enum value '" + bridgeMaterial.toString() + "' added to BridgeMaterial but not to a switch statement in method BridgePartBehaviour.hasValidTool()");
                break;
            }
        }
        return tid == 315 || tid == 176 || hasRightTool;
    }
    
    public static boolean actionDestroyBridgePart(final Action act, final Creature performer, final Item source, final BridgePart bridgePart, final short action, final float counter) {
        if (!Methods.isActionAllowed(performer, (short)116, bridgePart.getTileX(), bridgePart.getTileY())) {
            return true;
        }
        if (MethodsHighways.onHighway(bridgePart)) {
            performer.getCommunicator().sendNormalServerMessage("That bridge part is protected by the highway.");
            return true;
        }
        if (bridgePart.getBridgePartState().isBeingBuilt()) {
            if ((WurmPermissions.mayUseDeityWand(performer) && source.getTemplateId() == 176) || (WurmPermissions.mayUseGMWand(performer) && source.getTemplateId() == 315)) {
                return maybeDestroy(performer, bridgePart);
            }
            return MethodsStructure.destroyFloor(action, performer, source, bridgePart, counter);
        }
        else {
            if (bridgePart.getBridgePartState() != BridgeConstants.BridgeState.COMPLETED) {
                return bridgePart.getBridgePartState() != BridgeConstants.BridgeState.PLANNED || maybeDestroy(performer, bridgePart);
            }
            if ((WurmPermissions.mayUseDeityWand(performer) && source.getTemplateId() == 176) || (WurmPermissions.mayUseGMWand(performer) && source.getTemplateId() == 315)) {
                return maybeDestroy(performer, bridgePart);
            }
            return MethodsStructure.destroyFloor(action, performer, source, bridgePart, counter);
        }
    }
    
    private static boolean maybeDestroy(final Creature performer, final BridgePart bridgePart) {
        String wand = "";
        String effort = "";
        if (performer.getPower() > 1) {
            wand = " with your magic wand";
            effort = " effortlessly";
        }
        bridgePart.setBridgePartState(BridgeConstants.BridgeState.PLANNED);
        try {
            final Structure structure = Structures.getStructure(bridgePart.getStructureId());
            if (structure.isBridgeGone()) {
                performer.getCommunicator().sendNormalServerMessage("The last parts of the bridge falls down with a crash.");
                Server.getInstance().broadCastAction(performer.getName() + " cheers as the last parts of the bridge fall down with a crash.", performer, 5);
                return true;
            }
            bridgePart.revertToPlan();
            performer.getCommunicator().sendNormalServerMessage("You revert the " + bridgePart.getName() + " back to planning stage" + wand + ".");
            Server.getInstance().broadCastAction(performer.getName() + effort + " reverts the " + bridgePart.getName() + wand + ".", performer, 3);
            return true;
        }
        catch (NoSuchStructureException nss) {
            if (performer.getPower() > 1) {
                performer.getCommunicator().sendNormalServerMessage("Could not find the bridge that " + bridgePart.getName() + " belongs to.");
            }
            return true;
        }
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final boolean onSurface, final BridgePart bridgePart, final int encodedTile, final short action, final float counter) {
        if (action == 1) {
            return this.examine(performer, bridgePart);
        }
        if (action == 607) {
            if (!bridgePart.isFinished()) {
                performer.getCommunicator().sendAddBridgePartToCreationWindow(bridgePart, -10L);
            }
            return true;
        }
        if (action == 59) {
            final Structure bridge = Structures.getBridge(bridgePart.getStructureId());
            if (bridge == null) {
                performer.getCommunicator().sendNormalServerMessage("Could not find the bridge that " + bridgePart.getName() + " belongs to.");
            }
            else if (bridge.isOwner(performer) || performer.getPower() >= 2) {
                final int maxSize = 40;
                final TextInputQuestion tiq = new TextInputQuestion(performer, "Setting description for " + bridge.getName() + ".", "Set the new description:", 1, bridge.getWurmId(), 40, false);
                tiq.setOldtext(bridge.getName());
                tiq.sendQuestion();
            }
        }
        return super.action(act, performer, bridgePart.getTileX(), bridgePart.getTileY(), onSurface, Zones.getTileIntForTile(bridgePart.getTileX(), bridgePart.getTileY(), 0), action, counter);
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final boolean onSurface, final BridgePart bridgePart, final int encodedTile, final short action, final float counter) {
        final byte roadType = bridgePart.getRoadType();
        if (action == 1) {
            return this.examine(performer, bridgePart);
        }
        if (action == 607) {
            if (!bridgePart.isFinished()) {
                performer.getCommunicator().sendAddBridgePartToCreationWindow(bridgePart, -10L);
            }
            return true;
        }
        if (source == null) {
            return super.action(act, performer, bridgePart.getTileX(), bridgePart.getTileY(), onSurface, Zones.getTileIntForTile(bridgePart.getTileX(), bridgePart.getTileY(), 0), action, counter);
        }
        if (action == 179) {
            if ((source.getTemplateId() == 176 || source.getTemplateId() == 315) && WurmPermissions.mayUseGMWand(performer)) {
                Methods.sendSummonQuestion(performer, source, bridgePart.getTileX(), bridgePart.getTileY(), bridgePart.getStructureId());
            }
            return true;
        }
        if (action == 684) {
            if ((source.getTemplateId() == 315 || source.getTemplateId() == 176) && performer.getPower() >= 2) {
                Methods.sendItemRestrictionManagement(performer, bridgePart, bridgePart.getId());
            }
            else {
                BridgePartBehaviour.logger.log(Level.WARNING, performer.getName() + " hacking the protocol by trying to set the restrictions of " + bridgePart + ", counter: " + counter + '!');
            }
            return true;
        }
        if (action == 472) {
            if (source.getTemplateId() == 676 && source.getOwnerId() == performer.getWurmId()) {
                final MissionManager m = new MissionManager(performer, "Manage missions", "Select action", bridgePart.getId(), bridgePart.getName(), source.getWurmId());
                m.sendQuestion();
            }
            return true;
        }
        if (bridgePart.isFinished() && bridgePart.getMaterial() != BridgeConstants.BridgeMaterial.ROPE && bridgePart.getMaterial() != BridgeConstants.BridgeMaterial.WOOD) {
            if (action == 155 && (source.getTemplateId() == 492 || (WurmPermissions.mayUseDeityWand(performer) && source.getTemplateId() == 176) || (WurmPermissions.mayUseGMWand(performer) && source.getTemplateId() == 315)) && roadType == 0) {
                return this.changeFloor(act, performer, source, bridgePart, action, counter);
            }
            if (action == 155 && source.isPaveable() && roadType == Tiles.Tile.TILE_PREPARED_BRIDGE.id) {
                return this.changeFloor(act, performer, source, bridgePart, action, counter);
            }
            if (action == 191 && (source.getTemplateId() == 1115 || (WurmPermissions.mayUseDeityWand(performer) && source.getTemplateId() == 176) || (WurmPermissions.mayUseGMWand(performer) && source.getTemplateId() == 315)) && Tiles.isRoadType(roadType)) {
                return this.changeFloor(act, performer, source, bridgePart, action, counter);
            }
            if (action == 191 && (source.getTemplateId() == 97 || (WurmPermissions.mayUseDeityWand(performer) && source.getTemplateId() == 176) || (WurmPermissions.mayUseGMWand(performer) && source.getTemplateId() == 315)) && roadType == Tiles.Tile.TILE_PREPARED_BRIDGE.id) {
                return this.changeFloor(act, performer, source, bridgePart, action, counter);
            }
            if (action == 231 && source.getTemplateId() == 153 && roadType == Tiles.Tile.TILE_PLANKS.id) {
                return this.changeFloor(act, performer, source, bridgePart, action, counter);
            }
            if (MethodsHighways.onHighway(bridgePart) && source.isPaveable() && roadType != 0 && roadType != Tiles.Tile.TILE_PREPARED_BRIDGE.id) {
                return this.changeFloor(act, performer, source, bridgePart, action, counter);
            }
        }
        if (!this.isConstructionAction(action)) {
            return super.action(act, performer, source, bridgePart.getTileX(), bridgePart.getTileY(), onSurface, bridgePart.getHeightOffset(), Zones.getTileIntForTile(bridgePart.getTileX(), bridgePart.getTileY(), 0), action, counter);
        }
        if (action == 524 && !bridgePart.isIndestructible()) {
            return actionDestroyBridgePart(act, performer, source, bridgePart, action, counter);
        }
        if (action == 193 && !bridgePart.isNoRepair()) {
            return MethodsStructure.repairFloor(performer, source, bridgePart, counter, act);
        }
        if (action == 192 && !bridgePart.isNoImprove()) {
            return MethodsStructure.improveFloor(performer, source, bridgePart, counter, act);
        }
        if (action == 169 || action - 20000 >= 0) {
            return this.buildAction(act, performer, source, bridgePart, action, counter);
        }
        if (action == 82 && (source.getTemplateId() == 176 || source.getTemplateId() == 315) && WurmPermissions.mayUseGMWand(performer) && performer.getPower() >= 2) {
            try {
                final Structure struct = Structures.getStructure(bridgePart.getStructureId());
                performer.getLogger().log(Level.INFO, performer.getName() + " destroyed bridge " + struct.getName() + " at " + bridgePart.getTileX() + ", " + bridgePart.getTileY());
                Zones.flash(bridgePart.getTileX(), bridgePart.getTileY(), false);
                struct.totallyDestroy();
            }
            catch (NoSuchStructureException nss) {
                BridgePartBehaviour.logger.log(Level.WARNING, nss.getMessage(), nss);
            }
        }
        return true;
    }
    
    private final boolean isConstructionAction(final short action) {
        if (action - 20000 >= 0) {
            return true;
        }
        switch (action) {
            case 82:
            case 169:
            case 192:
            case 193:
            case 524: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean examine(final Creature performer, final BridgePart bridgePart) {
        final BridgeConstants.BridgeState bpState = bridgePart.getBridgePartState();
        if (bpState.isBeingBuilt()) {
            final BuildAllMaterials bam = getRequiredMaterials(bridgePart);
            final BuildStageMaterials bsm = bam.getBuildStageMaterials(bpState.getCode());
            final String allmaterials = bam.getRequiredMaterialString(true);
            final String stagematerials = bsm.getRequiredMaterialString(true);
            performer.getCommunicator().sendNormalServerMessage("You see a " + bridgePart.getName() + " under construction. The " + bridgePart.getName() + " requires " + allmaterials + " to be finished.");
            performer.getCommunicator().sendNormalServerMessage("The current stage being constructed is " + bridgePart.getFloorStageAsString() + "of " + bam.getStageCountAsString() + " and requires " + stagematerials + " to be completed.");
            sendQlString(performer, bridgePart);
        }
        else if (bpState == BridgeConstants.BridgeState.PLANNED) {
            final BuildAllMaterials bam = getRequiredMaterials(bridgePart);
            final BuildStageMaterials bsm = bam.getBuildStageMaterials(BridgeConstants.BridgeState.STAGE1.getCode());
            final String allmaterials = bam.getRequiredMaterialString(true);
            final String stagematerials = bsm.getRequiredMaterialString(true);
            performer.getCommunicator().sendNormalServerMessage("You see plans for a " + bridgePart.getName() + ". The " + bridgePart.getName() + " requires " + allmaterials + " to be finished.");
            performer.getCommunicator().sendNormalServerMessage("There are " + bam.getStageCountAsString() + " stages and the initial stage requires " + stagematerials + " to be completed.");
            sendQlString(performer, bridgePart);
        }
        else {
            performer.getCommunicator().sendNormalServerMessage("It is a " + bridgePart.getName() + ".");
            sendQlString(performer, bridgePart);
            final int templateId = bridgePart.getRepairItemTemplate();
            try {
                final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(templateId);
                if (bridgePart.getDamage() > 0.0f) {
                    performer.getCommunicator().sendNormalServerMessage("It needs to be repaired with " + template.getNameWithGenus() + ".");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("It can be improved with " + template.getNameWithGenus() + ".");
                }
            }
            catch (NoSuchTemplateException e) {
                BridgePartBehaviour.logger.log(Level.WARNING, e.getMessage(), e);
            }
            if (bridgePart.getRoadType() != 0) {
                final Tiles.Tile tile = Tiles.getTile(bridgePart.getRoadType());
                if (bridgePart.getRoadType() == Tiles.Tile.TILE_PREPARED_BRIDGE.id) {
                    performer.getCommunicator().sendNormalServerMessage("The surface has been " + tile.getDesc().toLowerCase() + ".");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("The surface has been paved with " + tile.getDesc().toLowerCase() + ".");
                }
            }
        }
        if (performer.getPower() >= 2) {
            final Structure bridge = Structures.getBridge(bridgePart.getStructureId());
            if (bridge != null) {
                performer.getCommunicator().sendNormalServerMessage("Planned by " + bridge.getPlanner() + ".");
            }
        }
        return true;
    }
    
    static final void sendQlString(final Creature performer, final BridgePart bridgePart) {
        performer.getCommunicator().sendNormalServerMessage("QL = " + bridgePart.getCurrentQL() + ", dam=" + bridgePart.getDamage() + ".");
        if (performer.getPower() > 0) {
            performer.getCommunicator().sendNormalServerMessage("id: " + bridgePart.getId() + " @" + bridgePart.getTileX() + "," + bridgePart.getTileY() + " height: " + bridgePart.getHeightOffset() + " " + bridgePart.getMaterial().getName() + " " + bridgePart.getType().getName() + " (" + bridgePart.getBridgePartState().toString().toLowerCase() + ").");
        }
    }
    
    private static void damageTool(final Creature performer, final BridgePart bridgePart, final Item source) {
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
    
    private static Skill getToolSkill(final BridgePart bridgePart, final Creature performer, final Item source) {
        if (bridgePart.getMaterial() == BridgeConstants.BridgeMaterial.ROPE) {
            return null;
        }
        try {
            return performer.getSkills().getSkillOrLearn(source.getPrimarySkill());
        }
        catch (NoSuchSkillException e) {
            return null;
        }
    }
    
    private boolean changeFloor(final Action act, final Creature performer, final Item source, final BridgePart bridgePart, final short action, final float counter) {
        final int pavingItem = source.getTemplateId();
        if (!Methods.isActionAllowed(performer, action, bridgePart.getTileX(), bridgePart.getTileY())) {
            return true;
        }
        final byte roadType = bridgePart.getRoadType();
        String prepared = "";
        String finished = "";
        int checkSkill = 0;
        boolean insta = false;
        if (performer.getPower() >= 2 && (source.getTemplateId() == 176 || source.getTemplateId() == 315)) {
            insta = true;
        }
        if (pavingItem == 492 || (insta && roadType == 0)) {
            if (!insta && source.getWeightGrams() < source.getTemplate().getWeightGrams()) {
                performer.getCommunicator().sendNormalServerMessage("It takes " + source.getTemplate().getWeightGrams() / 1000 + "kg of " + source.getName() + " to prepare the bridge part.");
                return true;
            }
            prepared = "prepare the bridge part";
            finished = "The bridge part roadway is prepared now.";
            checkSkill = 10031;
        }
        else if (pavingItem == 153) {
            if (source.getWeightGrams() < source.getTemplate().getWeightGrams()) {
                performer.getCommunicator().sendNormalServerMessage("It takes " + source.getTemplate().getWeightGrams() / 1000 + "kg of " + source.getName() + " to colour the floorboards.");
                return true;
            }
            prepared = "colour the floorboards";
            finished = "The floorboards are now coated with tar.";
        }
        else if (pavingItem == 97 || (insta && roadType == Tiles.Tile.TILE_PREPARED_BRIDGE.id)) {
            prepared = "remove the mortar";
            finished = "The mortar has been removed.";
            checkSkill = 10031;
        }
        else if (pavingItem == 1115 || (insta && Tiles.isRoadType(roadType))) {
            prepared = "remove the paving";
            finished = "The paving has been removed.";
            checkSkill = 1009;
        }
        else {
            if (source.getWeightGrams() < source.getTemplate().getWeightGrams()) {
                performer.getCommunicator().sendNormalServerMessage("The amount of " + source.getName() + " is too little to pave. You may need to combine them with other " + source.getTemplate().getPlural() + ".");
                return true;
            }
            if (Tiles.isRoadType(roadType)) {
                if (performer.getStrengthSkill() < 20.0) {
                    performer.getCommunicator().sendNormalServerMessage("You need to be stronger to replace pavement.");
                    return true;
                }
                prepared = "repave the bridge part";
            }
            else {
                prepared = "pave the prepared bridge part";
            }
            finished = "The bridge part is now paved.";
            checkSkill = 10031;
        }
        if (counter == 1.0f && !insta) {
            final Skill paving = performer.getSkills().getSkillOrLearn(checkSkill);
            if (checkSkill == 1009 && paving.getRealKnowledge() < 10.0) {
                if (roadType == Tiles.Tile.TILE_PLANKS.id || roadType == Tiles.Tile.TILE_PLANKS_TARRED.id) {
                    performer.getCommunicator().sendNormalServerMessage("You can't figure out how to remove the floor boards. You must become a bit better at digging first.");
                }
                else {
                    performer.getCommunicator().sendNormalServerMessage("You can't figure out how to remove the stone. You must become a bit better at digging first.");
                }
                return true;
            }
            int time = 500;
            if (pavingItem == 1115 || pavingItem == 97) {
                time = Actions.getDestroyActionTime(performer, paving, source, 0.0);
            }
            else {
                time = Actions.getStandardActionTime(performer, paving, source, 0.0);
            }
            act.setTimeLeft(time);
            if (pavingItem == 519) {
                performer.getCommunicator().sendNormalServerMessage("You break up the collosus brick and start to " + prepared + ".");
            }
            else {
                performer.getCommunicator().sendNormalServerMessage("You start to " + prepared + " with the " + source.getName() + ".");
            }
            Server.getInstance().broadCastAction(performer.getName() + " starts to " + prepared + ".", performer, 5);
            performer.sendActionControl(act.getActionEntry().getVerbString(), true, time);
            performer.getStatus().modifyStamina(-1000.0f);
            return false;
        }
        else {
            final int time2 = act.getTimeLeft();
            if (act.currentSecond() % 5 == 0) {
                performer.getStatus().modifyStamina(-10000.0f);
            }
            if (act.mayPlaySound()) {
                Methods.sendSound(performer, "sound.work.paving");
            }
            if (counter * 10.0f <= time2 && !insta) {
                return false;
            }
            final Skill paving2 = performer.getSkills().getSkillOrLearn(checkSkill);
            paving2.skillCheck((pavingItem == 146) ? 5.0 : 30.0, source, 0.0, false, counter);
            TileEvent.log(bridgePart.getTileX(), bridgePart.getTileY(), -1, performer.getWurmId(), action);
            byte newTileType = 0;
            switch (pavingItem) {
                case 176:
                case 315: {
                    newTileType = 0;
                    if (roadType == 0) {
                        newTileType = Tiles.Tile.TILE_PREPARED_BRIDGE.id;
                        break;
                    }
                    break;
                }
                case 492: {
                    newTileType = Tiles.Tile.TILE_PREPARED_BRIDGE.id;
                    break;
                }
                case 97: {
                    newTileType = 0;
                    break;
                }
                case 1115: {
                    newTileType = 0;
                    break;
                }
                case 132: {
                    newTileType = Tiles.Tile.TILE_COBBLESTONE.id;
                    break;
                }
                case 1122: {
                    newTileType = Tiles.Tile.TILE_COBBLESTONE_ROUND.id;
                    break;
                }
                case 519: {
                    newTileType = Tiles.Tile.TILE_COBBLESTONE_ROUGH.id;
                    break;
                }
                case 406: {
                    newTileType = Tiles.Tile.TILE_STONE_SLABS.id;
                    break;
                }
                case 1123: {
                    newTileType = Tiles.Tile.TILE_SLATE_BRICKS.id;
                    break;
                }
                case 771: {
                    newTileType = Tiles.Tile.TILE_SLATE_SLABS.id;
                    break;
                }
                case 1121: {
                    newTileType = Tiles.Tile.TILE_SANDSTONE_BRICKS.id;
                    break;
                }
                case 1124: {
                    newTileType = Tiles.Tile.TILE_SANDSTONE_SLABS.id;
                    break;
                }
                case 787: {
                    newTileType = Tiles.Tile.TILE_MARBLE_SLABS.id;
                    break;
                }
                case 786: {
                    newTileType = Tiles.Tile.TILE_MARBLE_BRICKS.id;
                    break;
                }
                case 776: {
                    newTileType = Tiles.Tile.TILE_POTTERY_BRICKS.id;
                    break;
                }
                case 495: {
                    newTileType = Tiles.Tile.TILE_PLANKS.id;
                    break;
                }
                case 153: {
                    newTileType = Tiles.Tile.TILE_PLANKS_TARRED.id;
                    break;
                }
                default: {
                    newTileType = Tiles.Tile.TILE_GRAVEL.id;
                    break;
                }
            }
            bridgePart.saveRoadType(newTileType);
            if (pavingItem == 492 || pavingItem == 153) {
                source.setWeight(source.getWeightGrams() - source.getTemplate().getWeightGrams(), true);
            }
            else if (!insta && pavingItem != 97) {
                if (pavingItem != 1115) {
                    Items.destroyItem(source.getWurmId());
                }
            }
            performer.getCommunicator().sendNormalServerMessage(finished);
            final VolaTile vt = Zones.getOrCreateTile(bridgePart.getTileX(), bridgePart.getTileY(), bridgePart.isOnSurface());
            if (vt != null) {
                vt.updateBridgePart(bridgePart);
            }
            return true;
        }
    }
    
    static {
        logger = Logger.getLogger(BridgePartBehaviour.class.getName());
    }
}
