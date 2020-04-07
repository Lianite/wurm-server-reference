// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.io.IOException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Players;
import java.util.TreeMap;
import java.util.Properties;
import com.wurmonline.server.structures.NoSuchStructureException;
import java.util.logging.Level;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.PermissionsByPlayer;
import com.wurmonline.server.players.Friend;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.players.Player;
import java.util.logging.Logger;

public final class StructureManagement extends Question
{
    private static final Logger logger;
    private Player player;
    private Structure structure;
    private Friend[] friends;
    private PermissionsByPlayer[] guests;
    
    public StructureManagement(final Creature aResponder, final String aTitle, final String aQuestion, final int aType, final long aTarget) {
        super(aResponder, aTitle, aQuestion, aType, aTarget);
        this.friends = new Friend[0];
        this.player = (Player)this.getResponder();
        try {
            this.structure = Structures.getStructure(this.target);
            this.friends = this.player.getFriends();
            this.guests = this.structure.getPermissionsPlayerList().getPermissionsByPlayer();
        }
        catch (NoSuchStructureException e) {
            StructureManagement.logger.log(Level.INFO, this.getResponder().getWurmId() + " tried to manage structure with id " + this.target + " but no structure was found.");
        }
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        QuestionParser.parseStructureManagement(this);
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        buf.append("text{type='bold';text='Add guest:'}");
        if (this.friends.length > 0) {
            final Map<String, Long> fset = new TreeMap<String, Long>();
            for (int x = 0; x < this.friends.length; ++x) {
                try {
                    boolean add = true;
                    for (int g = 0; g < this.guests.length; ++g) {
                        if (this.friends[x].getFriendId() == this.guests[g].getPlayerId()) {
                            add = false;
                            break;
                        }
                    }
                    if (add) {
                        fset.put(Players.getInstance().getNameFor(this.friends[x].getFriendId()), new Long(this.friends[x].getFriendId()));
                    }
                }
                catch (NoSuchPlayerException ex) {}
                catch (IOException iox) {
                    this.getResponder().getCommunicator().sendAlertServerMessage("There was a problem when handling your request. Please contact an administrator.");
                    StructureManagement.logger.log(Level.WARNING, "Got ioexception when looking for player with id " + this.friends[x], iox);
                }
            }
            final Set<Map.Entry<String, Long>> entries = fset.entrySet();
            for (final Map.Entry<String, Long> e : entries) {
                buf.append("checkbox{id='f" + (long)e.getValue() + "';text='" + e.getKey() + "'}");
            }
        }
        else {
            buf.append("text{type='bold';text='No friends to add.'}");
        }
        buf.append("text{type='bold';text='Remove guest:'}");
        final Map<String, Long> fset = new TreeMap<String, Long>();
        if (this.guests.length > 0) {
            for (int x = 0; x < this.guests.length; ++x) {
                try {
                    if (this.guests[x].getPlayerId() != -20L && this.guests[x].getPlayerId() != -30L && this.guests[x].getPlayerId() != -40L) {
                        fset.put(Players.getInstance().getNameFor(this.guests[x].getPlayerId()), this.guests[x].getPlayerId());
                    }
                }
                catch (NoSuchPlayerException ex2) {}
                catch (IOException iox) {
                    this.getResponder().getCommunicator().sendAlertServerMessage("There was a problem when handling your request. Please contact an administrator.");
                    StructureManagement.logger.log(Level.WARNING, "Got ioexception when looking for player with id " + this.guests[x], iox);
                }
            }
        }
        if (fset.isEmpty()) {
            buf.append("text{type='bold';text='No guests to remove.'}");
        }
        else {
            final Set<Map.Entry<String, Long>> entries = fset.entrySet();
            for (final Map.Entry<String, Long> e : entries) {
                buf.append("checkbox{id='g" + (long)e.getValue() + "';text='" + e.getKey() + "'}");
            }
        }
        buf.append("text{type='bold';text='Lock structure:'}");
        if (!this.structure.isLockable()) {
            buf.append("text{type='bold';text='WARNING! Not all doors have locks.'}");
        }
        if (this.structure.isLocked()) {
            buf.append("checkbox{id='unlock';text='Unlock all doors'}");
        }
        else {
            buf.append("checkbox{id='lock';text='Lock all doors'}");
        }
        if (this.getResponder().getCitizenVillage() != null) {
            buf.append("checkbox{id='allowVillagers';text='Allow citizens to enter';selected=\"" + this.structure.allowsCitizens() + "\"}");
            buf.append("checkbox{id='allowAllies';text='Allow allies to enter';selected=\"" + this.structure.allowsAllies() + "\"}");
            buf.append("checkbox{id='allowKingdom';text='Allow kingdom to enter';selected=\"" + this.structure.allowsKingdom() + "\"}");
        }
        buf.append("text{type='bold';text='Change name:'}");
        buf.append("input{maxchars='40'; id='sname'; text=\"" + this.structure.getName() + "\"}");
        buf.append("text{type='italic';text='Note! The name may contain the following letters: '}");
        buf.append("text{type='italic';text=\"a-z,A-Z,', and -\"}");
        buf.append("text{text=''}checkbox{id='demolish';text='Destroy this structure';selected='false';confirm=\"You are about to demolish this building" + (this.structure.hasBridgeEntrance() ? " and connected bridge(s)" : "") + ".\";question=\"Are you sure you want to do that?\"}text{text=''}text{text=''}");
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(300, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    static {
        logger = Logger.getLogger(StructureManagement.class.getName());
    }
}
