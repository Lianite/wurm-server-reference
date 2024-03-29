// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.utils.BMLBuilder;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.villages.Village;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.structures.DbFence;
import com.wurmonline.shared.constants.StructureConstantsEnum;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.Server;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.server.behaviours.Terraforming;
import com.wurmonline.server.Constants;
import com.wurmonline.server.Servers;
import com.wurmonline.server.zones.Zones;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.zones.FocusZone;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;

public class CreateZoneQuestion extends Question implements TimeConstants
{
    private static final Logger logger;
    private FocusZone[] allZones;
    final float minDirtDist = 1.0f;
    
    public CreateZoneQuestion(final Creature aResponder) {
        super(aResponder, "Creating a special zone", "Select the type and boundaries of the special zone", 98, aResponder.getWurmId());
    }
    
    @Override
    public void answer(final Properties answers) {
        final String sizex = answers.getProperty("sizex");
        final String sizey = answers.getProperty("sizey");
        final String typeS = answers.getProperty("typedd");
        final String zname = answers.getProperty("zname");
        final String zdesc = answers.getProperty("zdesc");
        final String delzone = answers.getProperty("deldd");
        int sx = 0;
        int sy = 0;
        byte zoneType = 0;
        try {
            if (typeS != null && typeS.length() > 0) {
                final int index = Integer.parseInt(typeS);
                if (index == 0) {
                    zoneType = 2;
                }
                else if (index == 1) {
                    zoneType = 3;
                }
                else if (index == 2) {
                    zoneType = 4;
                }
                else if (index == 3) {
                    zoneType = 5;
                }
                else if (index == 4) {
                    zoneType = 6;
                }
                else if (index == 6) {
                    zoneType = 8;
                }
                else if (index == 7) {
                    zoneType = 15;
                }
                else if (index == 8) {
                    zoneType = 9;
                }
                else if (index == 9) {
                    zoneType = 10;
                }
                else if (index == 10) {
                    zoneType = 11;
                }
                else if (index == 11) {
                    zoneType = 12;
                }
                else if (index == 12) {
                    zoneType = 13;
                }
                else if (index == 13) {
                    zoneType = 14;
                }
                else if (index == 14) {
                    zoneType = 16;
                }
                else if (index == 15) {
                    zoneType = 17;
                }
                else if (index == 16) {
                    zoneType = 18;
                }
            }
            if (zoneType != 0) {
                if (zoneType == 6) {
                    if (this.getResponder().getPower() < 4) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("Only Arch angels+ may create Hota zones since these have extended functionality.");
                        return;
                    }
                    if (FocusZone.getHotaZone() != null) {
                        this.getResponder().getCommunicator().sendNormalServerMessage("There is already a Hota PvP zone on this server.");
                        return;
                    }
                }
                if (sizex != null && sizex.length() > 0) {
                    sx = Integer.parseInt(sizex);
                }
                if (sizey != null && sizey.length() > 0) {
                    sy = Integer.parseInt(sizey);
                }
                if (zname != null && zname.length() > 2) {
                    if (zoneType < 7 || zoneType == 11 || zoneType == 12 || zoneType == 14 || zoneType == 16 || zoneType == 17 || zoneType == 18) {
                        final FocusZone fz = new FocusZone(Zones.safeTileX(this.getResponder().getTileX() - sx), Zones.safeTileX(this.getResponder().getTileX() + sx), Zones.safeTileY(this.getResponder().getTileY() - sy), Zones.safeTileY(this.getResponder().getTileY() + sy), zoneType, zname, zdesc, true);
                        this.getResponder().getCommunicator().sendNormalServerMessage("Created the zone " + zname + " XY:" + fz.getStartX() + "," + fz.getStartY() + " to " + fz.getEndX() + "," + fz.getEndY());
                        if (zoneType == 6) {
                            Servers.localServer.setNextHota(System.currentTimeMillis() + (Servers.isThisATestServer() ? 300000L : 115200000L));
                        }
                    }
                    else {
                        final int stx = Zones.safeTileX(this.getResponder().getTileX() - sx);
                        final int endtx = Zones.safeTileX(this.getResponder().getTileX() + sx);
                        final int sty = Zones.safeTileY(this.getResponder().getTileY() - sy);
                        final int endty = Zones.safeTileY(this.getResponder().getTileY() + sy);
                        if (zoneType == 8) {
                            if (this.getResponder().getPower() < 4) {
                                CreateZoneQuestion.logger.severe(this.getResponder().getName() + " has attempted to use Focus Zone Flatten DIRT with power level " + this.getResponder().getPower() + "! Needs " + 4 + "at least!");
                                return;
                            }
                            if (Constants.devmode) {
                                this.getResponder().getCommunicator().sendAlertServerMessage("Made flatten zone. Player standing on [" + this.getResponder().getTileX() + ", " + this.getResponder().getTileY() + "]. Creating zones with StartX " + stx + ", EndX " + endtx + ", StartY " + sty + ", EndY " + endty);
                            }
                            Terraforming.flattenImmediately(this.getResponder(), stx, endtx, sty, endty, 1.0f, 0, false);
                        }
                        else if (zoneType == 15) {
                            if (this.getResponder().getPower() < 4) {
                                CreateZoneQuestion.logger.severe(this.getResponder().getName() + " has attempted to use Focus Zone Flatten ROCK with power level " + this.getResponder().getPower() + "! Needs " + 4 + "at least!");
                                return;
                            }
                            if (Constants.devmode) {
                                this.getResponder().getCommunicator().sendAlertServerMessage("Made flatten zone. Player standing on [" + this.getResponder().getTileX() + ", " + this.getResponder().getTileY() + "]. Creating zones with StartX " + stx + ", EndX " + endtx + ", StartY " + sty + ", EndY " + endty);
                            }
                            Terraforming.flattenImmediately(this.getResponder(), stx, endtx, sty, endty, 1.0f, 0, true);
                        }
                        else if (zoneType == 9) {
                            Structures.createRandomStructure(this.getResponder(), stx, endtx, sty, endty, this.getResponder().getTileX(), this.getResponder().getTileY(), (byte)14, zname);
                        }
                        else if (zoneType == 10) {
                            Structures.createRandomStructure(this.getResponder(), stx, endtx, sty, endty, this.getResponder().getTileX(), this.getResponder().getTileY(), (byte)15, zname);
                        }
                        else if (zoneType == 13) {
                            if (this.getResponder().getCurrentVillage() != null) {
                                final Village v = this.getResponder().getCurrentVillage();
                                final int xa = Zones.safeTileX(v.getStartX() + sx);
                                final int xe = Zones.safeTileX(v.getEndX() - sx + 1);
                                final int ya = Zones.safeTileY(v.getStartY() + sy);
                                final int ye = Zones.safeTileY(v.getEndY() - sy + 1);
                                for (int x = xa; x <= xe; ++x) {
                                    for (int y = ya; y <= ye; ++y) {
                                        if (x == xa || x == xe) {
                                            if (Tiles.decodeHeight(Server.surfaceMesh.getTile(x, y)) > 0) {
                                                try {
                                                    final Zone zone = Zones.getZone(x, y, true);
                                                    final Fence fence = new DbFence(StructureConstantsEnum.FENCE_STONEWALL_HIGH, x, y, 0, 1.0f, Tiles.TileBorderDirection.DIR_DOWN, zone.getId(), 0);
                                                    fence.setState(fence.getFinishState());
                                                    fence.setQualityLevel(80.0f);
                                                    fence.improveOrigQualityLevel(80.0f);
                                                    fence.save();
                                                    zone.addFence(fence);
                                                }
                                                catch (NoSuchZoneException nsz) {
                                                    this.getResponder().getCommunicator().sendAlertServerMessage("Failed to create fence due to a server exception? - No Zone at " + x + "," + y);
                                                }
                                                catch (IOException iox) {
                                                    CreateZoneQuestion.logger.log(Level.WARNING, iox.getMessage(), iox);
                                                }
                                            }
                                        }
                                        else if ((y == ya || y == ye) && Tiles.decodeHeight(Server.surfaceMesh.getTile(x, y)) > 0) {
                                            try {
                                                final Zone zone = Zones.getZone(x, y, true);
                                                final Fence fence = new DbFence(StructureConstantsEnum.FENCE_STONEWALL_HIGH, x, y, 0, 1.0f, Tiles.TileBorderDirection.DIR_HORIZ, zone.getId(), 0);
                                                fence.setState(fence.getFinishState());
                                                fence.setQualityLevel(80.0f);
                                                fence.improveOrigQualityLevel(80.0f);
                                                fence.save();
                                                zone.addFence(fence);
                                            }
                                            catch (NoSuchZoneException nsz) {
                                                this.getResponder().getCommunicator().sendAlertServerMessage("Failed to create fence due to a server exception? - No Zone at " + x + "," + y);
                                            }
                                            catch (IOException iox) {
                                                CreateZoneQuestion.logger.log(Level.WARNING, iox.getMessage(), iox);
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                this.getResponder().getCommunicator().sendNormalServerMessage("You're not in a village, fool!");
                            }
                        }
                    }
                }
                else {
                    this.getResponder().getCommunicator().sendNormalServerMessage("The name must be at least 3 characters.");
                }
            }
        }
        catch (NumberFormatException nfe) {
            this.getResponder().getCommunicator().sendNormalServerMessage("The values were incorrect.");
        }
        try {
            if (delzone != null && delzone.length() > 0) {
                final int index = Integer.parseInt(delzone);
                if (index > 0) {
                    try {
                        this.allZones[index - 1].delete();
                        this.getResponder().getCommunicator().sendNormalServerMessage("Deleted the " + this.allZones[index - 1].getName() + " zone.");
                    }
                    catch (IOException ex) {
                        CreateZoneQuestion.logger.log(Level.INFO, ex.getMessage(), ex);
                    }
                }
            }
        }
        catch (NumberFormatException nfe) {
            CreateZoneQuestion.logger.log(Level.INFO, nfe.getMessage());
        }
    }
    
    @Override
    public void sendQuestion() {
        this.allZones = FocusZone.getAllZones();
        final String[] zoneNames = new String[this.allZones.length + 1];
        zoneNames[0] = "None";
        for (int i = 0; i < this.allZones.length; ++i) {
            zoneNames[i + 1] = this.allZones[i].getName();
        }
        final BMLBuilder bml = BMLBuilder.createNormalWindow(Integer.toString(this.getId()), "Manage Focus Zones", BMLBuilder.createGenericBuilder().addLabel("What type of zone do you wish to create?").addDropdown("typedd", "17", "PvP", "Name", "Name with Popup", "Non-PvP", "HOTA (Arch+)", "Battlecamp", "Flatten Dirt (Arch+)", "Flatten Rock (Arch+)", "Wooden House", "Stone House", "Premium Only Spawn", "No Build", "Tall Walls", "Fog", "Replenish Dirt", "Replenish Trees", "Replenish Ores", "None").addText("").addLabel("What dimensions should the zone have?").addString(BMLBuilder.createHorizArrayNode(false).addInput("sizex", "0", 4, 1).addLabel("East/West Distance from Center (You)").toString()).addString(BMLBuilder.createHorizArrayNode(false).addInput("sizey", "0", 4, 1).addLabel("North/South Distance from Center (You)").toString()).addText("").addText("A type Flatten will flatten the ground to your level (even if flying!) by raising dirt, lowering rock to 10.0 dirt below the dirt level if it is higher.").addText("A House type will create a house around you, complete with random windows.").addText("").addLabel("Zone Name:").addInput("zname", "", 30, 1).addLabel("Description (Used for Popups):").addInput("zdesc", "", 1000, 1).addText("").addLabel("Delete Zone:").addDropdown("deldd", "0", zoneNames).addText("").addButton("submit", "Send", null, null, null, true));
        this.getResponder().getCommunicator().sendBml(400, 390, true, true, bml.toString(), 200, 200, 200, this.title);
    }
    
    static {
        logger = Logger.getLogger(CreateZoneQuestion.class.getName());
    }
}
