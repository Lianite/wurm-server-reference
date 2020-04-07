// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Arrays;
import com.wurmonline.server.zones.VirtualZone;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.highways.HighwayFinder;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.Items;
import com.wurmonline.server.highways.Route;
import java.util.List;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.highways.Routes;
import com.wurmonline.server.villages.Villages;
import java.util.StringTokenizer;
import com.wurmonline.shared.util.StringUtilities;
import java.util.logging.Level;
import java.util.Properties;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.villages.Village;
import java.util.logging.Logger;

public class FindRouteQuestion extends Question
{
    private static final Logger logger;
    public String villageName;
    private Village[] villages;
    private Player player;
    
    public FindRouteQuestion(final Creature aResponder, final Item waystone) {
        super(aResponder, "Find a route", "Find a route", 139, waystone.getWurmId());
        this.villageName = "";
        if (aResponder.isPlayer()) {
            this.player = (Player)this.getResponder();
        }
        else {
            this.player = null;
        }
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        if (this.type == 0) {
            FindRouteQuestion.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (this.type != 139) {
            return;
        }
        Village village = null;
        this.villageName = this.getAnswer().getProperty("vname");
        this.villageName = this.villageName.replaceAll("\"", "");
        this.villageName = this.villageName.trim();
        if (this.villageName.length() > 3) {
            this.villageName = StringUtilities.raiseFirstLetter(this.villageName);
            final StringTokenizer tokens = new StringTokenizer(this.villageName);
            String newName = tokens.nextToken();
            while (tokens.hasMoreTokens()) {
                newName = newName + " " + StringUtilities.raiseFirstLetter(tokens.nextToken());
            }
            this.villageName = newName;
        }
        Label_0506: {
            if (!this.villageName.isEmpty()) {
                try {
                    village = Villages.getVillage(this.villageName);
                    if (Routes.getNodesFor(village).length == 0) {
                        this.player.getCommunicator().sendNormalServerMessage("Unable to find connected waystones in " + this.villageName);
                        return;
                    }
                    break Label_0506;
                }
                catch (NoSuchVillageException e2) {
                    this.player.getCommunicator().sendNormalServerMessage("Unable to find a village with that name: " + this.villageName);
                    return;
                }
            }
            final String clear = this.getAnswer().getProperty("clear");
            if (clear != null && clear.equals("true")) {
                this.player.setHighwayPath("", null);
                for (final Item waystone : Items.getWaystones()) {
                    final VolaTile vt = Zones.getTileOrNull(waystone.getTileX(), waystone.getTileY(), waystone.isOnSurface());
                    if (vt != null) {
                        for (final VirtualZone vz : vt.getWatchers()) {
                            try {
                                if (vz.getWatcher().getWurmId() == this.player.getWurmId()) {
                                    this.player.getCommunicator().sendWaystoneData(waystone);
                                    break;
                                }
                            }
                            catch (Exception e) {
                                FindRouteQuestion.logger.log(Level.WARNING, e.getMessage(), e);
                            }
                        }
                    }
                }
                return;
            }
            final String villno = this.getAnswer().getProperty("vill");
            final int vno = Integer.parseInt(villno);
            if (this.villages.length == 0 || vno > this.villages.length) {
                this.player.getCommunicator().sendNormalServerMessage("No village selected!");
                return;
            }
            village = this.villages[vno];
            this.villageName = village.getName();
        }
        if (village.equals(this.player.getCurrentVillage())) {
            this.player.getCommunicator().sendNormalServerMessage("You are already in that village.");
            return;
        }
        HighwayFinder.queueHighwayFinding(this.player, Routes.getNode(this.target), village, (byte)0);
        this.player.achievement(524);
    }
    
    @Override
    public void sendQuestion() {
        if (this.player == null) {
            return;
        }
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        int height = 220;
        if (this.player.getHighwayPathDestination().length() > 0) {
            buf.append("harray{label{text=\"Already heading to: " + this.player.getHighwayPathDestination() + "  \"}button{id=\"clear\";text=\"Clear route\"};}");
            buf.append("label{text=\"\"}");
            height += 50;
        }
        this.villages = Routes.getVillages(this.target);
        buf.append("harray{label{text=\"Find a route to village \"};dropdown{id=\"vill\";options=\"");
        if (this.villages.length == 0) {
            buf.append("None");
        }
        else {
            Arrays.sort(this.villages);
            for (int i = 0; i < this.villages.length; ++i) {
                if (i > 0) {
                    buf.append(",");
                }
                buf.append(this.villages[i].getName());
            }
        }
        buf.append("\"}}");
        buf.append("text{text=\"You may also specify a village name here to get a route to it.\"}");
        buf.append("harray{input{maxchars=\"40\";id=\"vname\";text=\"\"}}");
        buf.append("text{text=\"Note: The village must have a waystone in it, and be connected to the highway system.\"}");
        buf.append("label{text=\"\"}");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(400, height, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    static {
        logger = Logger.getLogger(FindRouteQuestion.class.getName());
    }
}
