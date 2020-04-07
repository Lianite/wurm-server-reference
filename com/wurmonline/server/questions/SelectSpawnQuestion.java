// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.List;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.players.Spawnpoint;
import java.io.IOException;
import com.wurmonline.server.epic.ValreiMapData;
import com.wurmonline.server.players.Achievements;
import com.wurmonline.shared.constants.PlayerOnlineStatus;
import com.wurmonline.server.Players;
import com.wurmonline.server.LoginHandler;
import com.wurmonline.server.Server;
import com.wurmonline.server.intra.IntraServerConnection;
import java.util.logging.Level;
import com.wurmonline.server.Servers;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.kingdom.Kingdom;
import java.util.LinkedList;
import java.util.logging.Logger;

public class SelectSpawnQuestion extends Question
{
    private static final Logger logger;
    final String welcomeMess;
    boolean unDead;
    private final LinkedList<Kingdom> availKingdoms;
    
    public SelectSpawnQuestion(final Player aResponder, final String aTitle, final String aQuestion, final long aTarget, final String message, final boolean Undead) {
        super(aResponder, aTitle, aQuestion, 134, aTarget);
        this.unDead = false;
        this.availKingdoms = new LinkedList<Kingdom>();
        this.welcomeMess = message;
        this.unDead = Undead;
    }
    
    @Override
    public void answer(final Properties answers) {
        try {
            final Player player = (Player)this.getResponder();
            byte kingdom = 4;
            if (Servers.localServer.HOMESERVER) {
                kingdom = Servers.localServer.KINGDOM;
            }
            else {
                try {
                    final String did = answers.getProperty("kingdomid");
                    final int index = Integer.parseInt(did);
                    final Kingdom k = this.getAvailKingdoms().get(index);
                    kingdom = (byte)((k == null) ? 0 : k.getId());
                }
                catch (Exception ex) {
                    SelectSpawnQuestion.logger.log(Level.INFO, ex.getMessage(), ex);
                }
            }
            boolean male = true;
            try {
                male = Boolean.parseBoolean(answers.getProperty("male"));
            }
            catch (Exception ex2) {
                SelectSpawnQuestion.logger.log(Level.INFO, ex2.getMessage(), ex2);
            }
            if (!male) {
                player.setSex((byte)1, false);
            }
            player.setKingdomId(kingdom, true);
            player.setBlood(IntraServerConnection.calculateBloodFromKingdom(kingdom));
            float posX = Servers.localServer.SPAWNPOINTJENNX * 4 + Server.rand.nextInt(10);
            float posY = Servers.localServer.SPAWNPOINTJENNY * 4 + Server.rand.nextInt(10);
            final int r = Server.rand.nextInt(3);
            final float rot = Server.rand.nextInt(360);
            if (this.unDead) {
                kingdom = 0;
                final float[] txty = Player.findRandomSpawnX(false, false);
                posX = txty[0];
                posY = txty[1];
            }
            else {
                if (Servers.localServer.KINGDOM != 0) {
                    kingdom = Servers.localServer.KINGDOM;
                }
                else if (r == 1) {
                    posX = Servers.localServer.SPAWNPOINTMOLX * 4 + Server.rand.nextInt(10);
                    posY = Servers.localServer.SPAWNPOINTMOLY * 4 + Server.rand.nextInt(10);
                }
                else if (r == 2) {
                    posX = Servers.localServer.SPAWNPOINTLIBX * 4 + Server.rand.nextInt(10);
                    posY = Servers.localServer.SPAWNPOINTLIBY * 4 + Server.rand.nextInt(10);
                }
                if (Servers.localServer.randomSpawns) {
                    final float[] txty = Player.findRandomSpawnX(true, true);
                    posX = txty[0];
                    posY = txty[1];
                }
            }
            final Spawnpoint sp = LoginHandler.getInitialSpawnPoint(kingdom);
            if (sp != null) {
                posX = sp.tilex * 4 + Server.rand.nextInt(10);
                posY = sp.tiley * 4 + Server.rand.nextInt(10);
            }
            player.setPositionX(posX);
            player.setPositionY(posY);
            player.setRotation(rot);
            LoginHandler.putOutsideWall(player);
            if (player.isOnSurface()) {
                LoginHandler.putOutsideHouse(player, false);
                LoginHandler.putOutsideFence(player);
            }
            player.setTeleportPoints(posX, posY, 0, 0);
            player.startTeleporting();
            Players.getInstance().sendConnectInfo(player, " has logged in.", player.getLastLogin(), PlayerOnlineStatus.ONLINE, true);
            Players.getInstance().addToGroups(player);
            Server.getInstance().startSendingFinals(player);
            player.getCommunicator().sendMapInfo();
            Achievements.sendAchievementList(player);
            Players.loadAllPrivatePOIForPlayer(player);
            player.sendAllMapAnnotations();
            ValreiMapData.sendAllMapData(player);
            player.resetLastSentToolbelt();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        buf.append("text{text=\"\"}text{text=\"\"}");
        final boolean selected = Server.rand.nextBoolean();
        buf.append("radio{ group='male'; id='false';text='Female';selected='" + !selected + "'}");
        buf.append("radio{ group='male'; id='true';text='Male';selected='" + selected + "'}");
        if (!Servers.localServer.HOMESERVER) {
            buf.append("text{text=\"\"}text{text=\"\"}");
            buf.append("text{text=\"Please select kingdom.\"}text{text=\"\"}");
            buf.append("harray{label{text='Kingdom: '};dropdown{id='kingdomid';options=\"");
            final Kingdom[] kingdoms = Kingdoms.getAllKingdoms();
            for (int x = 0; x < kingdoms.length; ++x) {
                if (kingdoms[x].getId() != 0 && kingdoms[x].existsHere() && (!kingdoms[x].isCustomKingdom() || kingdoms[x].acceptsTransfers())) {
                    this.availKingdoms.add(kingdoms[x]);
                    buf.append(kingdoms[x].getName());
                    buf.append(",");
                }
            }
            buf.append(",None\"}}");
        }
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    List<Kingdom> getAvailKingdoms() {
        return this.availKingdoms;
    }
    
    static {
        logger = Logger.getLogger(SelectSpawnQuestion.class.getName());
    }
}
