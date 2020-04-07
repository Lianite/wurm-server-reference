// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.villages.Villages;
import java.io.IOException;
import com.wurmonline.server.creatures.MineDoorSettings;
import com.wurmonline.server.Players;
import com.wurmonline.server.creatures.MineDoorPermission;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.PermissionsByPlayer;

public final class MineDoorQuestion extends Question
{
    private PermissionsByPlayer[] permitted;
    private final int tx;
    private final int ty;
    
    public MineDoorQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget, final int tilex, final int tiley) {
        super(aResponder, aTitle, aQuestion, 72, aTarget);
        this.permitted = null;
        this.tx = tilex;
        this.ty = tiley;
    }
    
    @Override
    public void answer(final Properties aAnswers) {
        final MineDoorPermission md = MineDoorPermission.getPermission(this.tx, this.ty);
        this.permitted = md.getPermissionsPlayerList().getPermissionsByPlayer();
        boolean changed = false;
        int villid = -1;
        if (this.getResponder().getCitizenVillage() != null) {
            villid = this.getResponder().getCitizenVillage().id;
        }
        int removed = 0;
        for (int x = 0; x < this.permitted.length; ++x) {
            final String key = "nperm" + x;
            final String val = aAnswers.getProperty(key);
            if (val != null && val.equals("true")) {
                ++removed;
                md.removeMDPerm(this.permitted[x].getPlayerId());
            }
        }
        if (removed > 0) {
            this.getResponder().getCommunicator().sendNormalServerMessage("Removed " + removed + " existing permissions.");
        }
        String key2 = "vperm";
        String val2 = aAnswers.getProperty(key2);
        if (val2 != null && val2.equals("true")) {
            changed = (md.setVillageId(villid) || changed);
            this.getResponder().getCommunicator().sendNormalServerMessage("Added the right for your village to use the door.");
        }
        else if (md.getVillageId() == villid && villid >= 0) {
            changed = (md.setVillageId(-1) || changed);
            this.getResponder().getCommunicator().sendNormalServerMessage("Removed the right for your village to use the door.");
        }
        key2 = "alliedperm";
        val2 = aAnswers.getProperty(key2);
        if (val2 != null && val2.equals("true")) {
            if (!md.isAllowAllies()) {
                changed = (md.setAllowAllies(true) || changed);
                this.getResponder().getCommunicator().sendNormalServerMessage("Added the right for your alliance to use the door.");
            }
        }
        else if (md.isAllowAllies()) {
            changed = (md.setAllowAllies(false) || changed);
            this.getResponder().getCommunicator().sendNormalServerMessage("Removed the right for your alliance to use the door.");
        }
        key2 = "allperm";
        val2 = aAnswers.getProperty(key2);
        if (val2 != null && val2.equals("true")) {
            if (!md.isAllowAll()) {
                changed = (md.setAllowAll(true) || changed);
                this.getResponder().getCommunicator().sendNormalServerMessage("Added the right for anyone to use the door.");
            }
        }
        else if (md.isAllowAll()) {
            changed = (md.setAllowAll(false) || changed);
            this.getResponder().getCommunicator().sendNormalServerMessage("Removed the right for anyone to use the door.");
        }
        key2 = "newperm";
        val2 = aAnswers.getProperty(key2);
        if (val2 != null && val2.length() > 0) {
            if (md.getPermissionsPlayerList().size() < md.getMaxAllowed()) {
                final long mdid = Players.getInstance().getWurmIdByPlayerName(val2);
                if (mdid > 0L) {
                    final byte kingdom = Players.getInstance().getKingdomForPlayer(mdid);
                    if (kingdom == this.getResponder().getKingdomId()) {
                        md.addMDPerm(mdid, MineDoorSettings.MinedoorPermissions.PASS.getValue());
                        this.getResponder().getCommunicator().sendNormalServerMessage("Gave permission to " + val2 + ".");
                    }
                    else {
                        this.getResponder().getCommunicator().sendNormalServerMessage("You may not permit the enemy " + val2 + " to enter the mine door.");
                    }
                }
                else {
                    this.getResponder().getCommunicator().sendNormalServerMessage("There is no known player with the name " + val2 + ".");
                }
            }
            else {
                this.getResponder().getCommunicator().sendNormalServerMessage("May not add new permissions at the moment. You only have 100 keys.");
            }
        }
        key2 = "newcont";
        val2 = aAnswers.getProperty(key2);
        if (val2 != null && val2.length() > 0) {
            final long mdid = Players.getInstance().getWurmIdByPlayerName(val2);
            if (mdid > 0L) {
                final byte kingdom = Players.getInstance().getKingdomForPlayer(mdid);
                if (kingdom == this.getResponder().getKingdomId()) {
                    changed = (md.setController(mdid) || changed);
                    this.getResponder().getCommunicator().sendNormalServerMessage("You gave control of the mine door to " + val2 + ".");
                }
                else {
                    this.getResponder().getCommunicator().sendNormalServerMessage("You may not permit the enemy " + val2 + " to enter the mine door.");
                }
            }
            else {
                this.getResponder().getCommunicator().sendNormalServerMessage("There is no known player with the name " + val2 + ".");
            }
        }
        key2 = "newname";
        val2 = aAnswers.getProperty(key2);
        if (val2 != null) {
            changed = (md.setObjectName(val2, this.getResponder()) || changed);
        }
        if (changed) {
            try {
                md.save();
            }
            catch (IOException ex) {}
        }
    }
    
    @Override
    public void sendQuestion() {
        final MineDoorPermission md = MineDoorPermission.getPermission(this.tx, this.ty);
        this.permitted = md.getPermissionsPlayerList().getPermissionsByPlayer();
        int villid = -1;
        if (this.getResponder().getCitizenVillage() != null) {
            villid = this.getResponder().getCitizenVillage().id;
        }
        final StringBuilder buf = new StringBuilder(this.getBmlHeader());
        buf.append("text{type='bold';text=\"Permissions to use mine door:\"}text{text=''}");
        String rvname = "none";
        if (villid >= 0) {
            try {
                rvname = Villages.getVillage(villid).getName();
            }
            catch (NoSuchVillageException ex) {}
        }
        if (md.getVillageId() >= 0) {
            try {
                final Village cvill = Villages.getVillage(md.getVillageId());
                buf.append("text{text='Currently the village of " + cvill.getName() + " may use and manage the mine door.'}");
            }
            catch (NoSuchVillageException nsv) {
                md.setVillageId(-1);
                try {
                    md.save();
                }
                catch (IOException ex2) {}
            }
            buf.append("checkbox{id='vperm';selected='" + (md.getVillageId() == villid) + "';text='Check here if you want everyone in your village (" + rvname + ") to be able to use and manage the mine door.'};");
        }
        else if (villid >= 0) {
            buf.append("checkbox{id='vperm';selected='" + (md.getVillageId() == villid) + "';text='Check here if you want everyone in your village (" + rvname + ") to be able to use and manage the mine door.'};");
        }
        buf.append("checkbox{id='allperm';selected='" + md.isAllowAll() + "';text='Check here if you want everyone to be able to use the mine door.'};");
        buf.append("checkbox{id='alliedperm';selected='" + md.isAllowAllies() + "';text='Check here if you want your allies to be able to use the mine door.'};");
        if (this.getResponder().getCitizenVillage() == null || this.getResponder().getCitizenVillage().getAllianceNumber() <= 0) {
            buf.append("text{text=\"Note that the alliance setting only has effect if your settlement actually is in an alliance.\"}");
        }
        if (this.permitted.length > 0) {
            buf.append("text{text='These are the people who may use and manage the mine door:'}");
            buf.append("table{rows='" + (this.permitted.length + 1) + "';cols='2';label{text='Remove'};label{text='Name'};");
            for (int x = 0; x < this.permitted.length; ++x) {
                String name = "unknown";
                try {
                    name = Players.getInstance().getNameFor(this.permitted[x].getPlayerId());
                }
                catch (Exception ex3) {}
                buf.append("checkbox{id='nperm" + x + "';selected='false';text=''};label{text='" + name + "'};");
            }
            buf.append("};");
        }
        else if (!md.isAllowAll()) {
            buf.append("text{text='No other people may use the mine door.'}");
        }
        if (md.getPermissionsPlayerList().size() < md.getMaxAllowed()) {
            buf.append("text{text='Add new person:'};input{maxchars='40'; id='newperm'; text=''};");
        }
        else {
            buf.append("text{text='You have no more keys for the door to give away.'}");
        }
        if (md.getController() == this.getResponder().getWurmId()) {
            buf.append("text{text='Only you may change the controller of this door.'}");
            buf.append("text{text='Change controller of this door to:'};input{maxchars='40'; id='newcont'; text=''};");
            buf.append("text{text=\"Rename this door\"};input{maxchars=\"40\"; id=\"newname\"; text=\"" + md.getObjectName() + "\"};");
        }
        buf.append(this.createAnswerButton2());
        this.getResponder().getCommunicator().sendBml(400, 400, true, true, buf.toString(), 200, 200, 200, this.title);
    }
}
