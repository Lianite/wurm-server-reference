// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Properties;
import com.wurmonline.server.ServerEntry;
import java.util.Iterator;
import java.util.Set;
import com.wurmonline.server.Features;
import com.wurmonline.server.Servers;
import com.wurmonline.server.players.Player;
import java.util.HashMap;
import java.util.LinkedList;
import com.wurmonline.server.creatures.Creature;
import java.util.Map;
import com.wurmonline.server.players.Spawnpoint;
import java.util.List;

public final class SpawnQuestion extends Question
{
    private final List<Spawnpoint> spawnpoints;
    private final Map<Integer, Integer> servers;
    
    public SpawnQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 34, aTarget);
        this.spawnpoints = new LinkedList<Spawnpoint>();
        this.servers = new HashMap<Integer, Integer>();
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        Set<Spawnpoint> spawnPoints = ((Player)this.getResponder()).spawnpoints;
        if (spawnPoints == null || spawnPoints.isEmpty()) {
            ((Player)this.getResponder()).calculateSpawnPoints();
            spawnPoints = ((Player)this.getResponder()).spawnpoints;
        }
        if (spawnPoints != null && !spawnPoints.isEmpty()) {
            int x = 0;
            buf.append("dropdown{id='spawnpoint';options=\"");
            for (final Spawnpoint sp : spawnPoints) {
                if (x > 0) {
                    buf.append(",");
                }
                this.spawnpoints.add(sp);
                buf.append(sp.description);
                ++x;
            }
            buf.append("\"};");
        }
        else {
            buf.append("label{text=\"No valid spawn points found. Wait and try again using the /respawn command or send to start at the startpoint.\"};");
        }
        if (Servers.localServer.EPIC && Servers.localServer.getKingdom() != this.getResponder().getKingdomId()) {
            buf.append("label{text=\"You may also select to spawn on another Epic server.\"};");
            int x = 0;
            this.servers.put(x, 0);
            buf.append("dropdown{id='eserver';options=\"None");
            for (final ServerEntry s : Servers.getAllServers()) {
                if (s.EPIC && s.isAvailable(0, this.getResponder().isPaying()) && s.getId() != Servers.localServer.id && (s.getKingdom() == 0 || s.getKingdom() == this.getResponder().getKingdomId()) && (this.getResponder().isPaying() || !s.ISPAYMENT)) {
                    ++x;
                    buf.append(",");
                    buf.append(s.getName());
                    this.servers.put(x, s.getId());
                }
            }
            buf.append("\"};");
        }
        if (Features.Feature.FREE_ITEMS.isEnabled()) {
            buf.append("text{text=''};text{text=''};");
            buf.append("label{text=\"Do you require a weapon QL 40 or a rope?\"};");
            buf.append("dropdown{id='weapon';options=\"No");
            buf.append(",Long Sword + Shield,Two Handed Sword, Large Axe + Shield, Huge Axe, Medium Maul + Shield, Large Maul, Halberd, Long Spear");
            buf.append("\"};");
            buf.append("text{text=''};text{text=''};");
            buf.append("label{text=\"You may also select to spawn with some armour.\"};");
            buf.append("dropdown{id='armour';options=\"None");
            buf.append(",Chain (QL 40), Leather (QL 60), Plate (QL 20)");
            buf.append("\"};");
        }
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, Servers.localServer.isChallengeServer() ? 500 : 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    final Map<Integer, Integer> getServerEntries() {
        return this.servers;
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseSpawnQuestion(this);
    }
    
    Spawnpoint getSpawnpoint(final int aIndex) {
        if (this.spawnpoints.isEmpty()) {
            return null;
        }
        return this.spawnpoints.get(aIndex);
    }
}
