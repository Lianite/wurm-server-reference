// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.villages.Village;
import com.wurmonline.server.endgames.EndGameItem;
import com.wurmonline.server.items.Item;
import java.util.logging.Level;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.endgames.EndGameItems;
import com.wurmonline.server.Servers;
import com.wurmonline.server.zones.Zones;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;

public final class VillageTeleportQuestion extends Question
{
    private static final Logger logger;
    private int floorLevel;
    
    public VillageTeleportQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final int aType, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 111, aTarget);
        this.floorLevel = 0;
    }
    
    public VillageTeleportQuestion(final Creature responder) {
        super(responder, "Village Teleport", "", 111, responder.getWurmId());
        this.floorLevel = 0;
    }
    
    @Override
    public void answer(final Properties aAnswers) {
        final boolean teleport = aAnswers.getProperty("teleport") != null && aAnswers.getProperty("teleport").equals("true");
        this.floorLevel = this.getResponder().getFloorLevel();
        if (this.getResponder().isDead()) {
            this.getResponder().getCommunicator().sendNormalServerMessage("The dead can't teleport.");
            return;
        }
        if (this.floorLevel != 0) {
            this.getResponder().getCommunicator().sendNormalServerMessage("You need to be on ground level to teleport.");
            return;
        }
        final Item[] allItems;
        final Item[] inventoryItems = allItems = this.getResponder().getInventory().getAllItems(true);
        for (final Item lInventoryItem : allItems) {
            if (lInventoryItem.isArtifact()) {
                this.getResponder().getCommunicator().sendNormalServerMessage("The " + lInventoryItem.getName() + " hums and disturbs the weave. You can not teleport right now.");
                return;
            }
        }
        final Item[] allItems2;
        final Item[] bodyItems = allItems2 = this.getResponder().getBody().getBodyItem().getAllItems(true);
        for (final Item lInventoryItem2 : allItems2) {
            if (lInventoryItem2.isArtifact()) {
                this.getResponder().getCommunicator().sendNormalServerMessage("The " + lInventoryItem2.getName() + " hums and disturbs the weave. You can not teleport right now.");
                return;
            }
        }
        if (teleport && this.floorLevel == 0) {
            if (this.getResponder().getEnemyPresense() > 0 || this.getResponder().isFighting()) {
                this.getResponder().getCommunicator().sendNormalServerMessage("There are enemies in the vicinity. You fail to focus.");
                return;
            }
            if (this.getResponder().getCitizenVillage() == null) {
                this.getResponder().getCommunicator().sendNormalServerMessage("You need to be citizen in a village to teleport home.");
                return;
            }
            if (this.getResponder().isOnPvPServer() && Zones.isWithinDuelRing(this.getResponder().getTileX(), this.getResponder().getTileY(), true) != null) {
                this.getResponder().getCommunicator().sendNormalServerMessage("The magic of the duelling ring interferes. You can not teleport here.");
                return;
            }
            if (this.getResponder().isInPvPZone()) {
                this.getResponder().getCommunicator().sendNormalServerMessage("The magic of the pvp zone interferes. You can not teleport here.");
                return;
            }
            if (Servers.localServer.PVPSERVER && EndGameItems.getEvilAltar() != null) {
                final EndGameItem egi = EndGameItems.getEvilAltar();
                if (this.getResponder().isWithinDistanceTo(egi.getItem().getPosX(), egi.getItem().getPosY(), egi.getItem().getPosZ(), 50.0f)) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("The magic of this place interferes. You can not teleport here.");
                    return;
                }
            }
            else if (Servers.localServer.PVPSERVER && EndGameItems.getGoodAltar() != null) {
                final EndGameItem egi = EndGameItems.getGoodAltar();
                if (this.getResponder().isWithinDistanceTo(egi.getItem().getPosX(), egi.getItem().getPosY(), egi.getItem().getPosZ(), 50.0f)) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("The magic of this place interferes. You can not teleport here.");
                    return;
                }
            }
            final Village village = this.getResponder().getCitizenVillage();
            if (village != null) {
                this.getResponder().setTeleportPoints((short)village.getTokenX(), (short)village.getTokenY(), 0, 0);
                if (this.getResponder().startTeleporting()) {
                    this.getResponder().getCommunicator().sendTeleport(false);
                    this.getResponder().teleport();
                    ((Player)this.getResponder()).setUsedFreeVillageTeleport();
                }
            }
            else {
                VillageTeleportQuestion.logger.log(Level.WARNING, this.getResponder().getName() + " tried to teleport to null settlement!");
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        this.floorLevel = this.getResponder().getFloorLevel();
        if (this.floorLevel == 0) {
            final Village village = this.getResponder().getCitizenVillage();
            if (village != null) {
                final String villageName = village.getName();
                final StringBuilder buf = new StringBuilder();
                buf.append(this.getBmlHeader());
                buf.append("text{type=\"bold\";text=\"Teleport to settlement " + villageName + ":\"}");
                buf.append("text{text=\"\"}");
                buf.append("text{text=\"You have to option to teleport directly to the village token of your new village.\"}");
                buf.append("text{text=\"\"}");
                buf.append("text{type=\"bold\";text=\"You can only do this once per character.\"}");
                buf.append("text{text=\"\"}");
                buf.append("text{text=\"Do you want to teleport to " + villageName + "?\"}");
                buf.append("radio{ group=\"teleport\"; id=\"true\";text=\"Yes\"}");
                buf.append("radio{ group=\"teleport\"; id=\"false\";text=\"No\";selected=\"true\"}");
                buf.append(this.createOkAnswerButton());
                this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
            }
            else {
                VillageTeleportQuestion.logger.log(Level.WARNING, this.getResponder().getName() + " tried to teleport to null settlement!");
                this.getResponder().getCommunicator().sendNormalServerMessage("Failed to locate the settlement for the teleportation. Please contact administration.");
            }
        }
        else {
            final Village village = this.getResponder().getCitizenVillage();
            if (village != null) {
                final String villageName = village.getName();
                final StringBuilder buf = new StringBuilder();
                buf.append(this.getBmlHeader());
                buf.append("text{type=\"bold\";text=\"Teleport to settlement " + villageName + ":\"}");
                buf.append("text{text=\"\"}");
                buf.append("text{text=\"You have to option to teleport directly to the village token of your new village.\"}");
                buf.append("text{text=\"\"}");
                buf.append("text{type=\"bold\";text=\"You can only do this once per character.\"}");
                buf.append("text{text=\"\"}");
                buf.append("text{type=\"bold\";text=\"You need to be on ground level in order to teleport to your village.\"}");
                buf.append("text{type=\"bold\";text=\"Once on ground level write /vteleport in the chat to teleport.\"}");
                buf.append(this.createOkAnswerButton());
                this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
            }
            else {
                VillageTeleportQuestion.logger.log(Level.WARNING, this.getResponder().getName() + " tried to teleport to null settlement!");
                this.getResponder().getCommunicator().sendNormalServerMessage("Failed to locate the settlement for the teleportation. Please contact administration.");
            }
        }
    }
    
    static {
        logger = Logger.getLogger(VillageTeleportQuestion.class.getName());
    }
}
