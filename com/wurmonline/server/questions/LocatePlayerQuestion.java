// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.endgames.EndGameItems;
import com.wurmonline.server.Items;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.behaviours.MethodsCreatures;
import com.wurmonline.server.Servers;
import com.wurmonline.server.spells.SpellResist;
import com.wurmonline.server.Server;
import com.wurmonline.server.players.PlayerInfoFactory;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;

public class LocatePlayerQuestion extends Question
{
    private boolean properlySent;
    private boolean override;
    private double power;
    
    public LocatePlayerQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget, final boolean eyeVyn, final double power) {
        super(aResponder, aTitle, aQuestion, 79, aTarget);
        this.properlySent = false;
        this.override = false;
        if (eyeVyn) {
            this.override = true;
        }
        this.power = power;
    }
    
    public static String locatePlayerString(final int targetDistance, final String name, final String direction, final int maxDistance) {
        if (targetDistance > maxDistance) {
            return "No such soul found.";
        }
        String toReturn = "";
        if (targetDistance == 0) {
            toReturn = toReturn + "You are practically standing on the " + name + "! ";
        }
        else if (targetDistance < 1) {
            toReturn = toReturn + "The " + name + " is " + direction + " a few steps away! ";
        }
        else if (targetDistance < 4) {
            toReturn = toReturn + "The " + name + " is " + direction + " a stone's throw away! ";
        }
        else if (targetDistance < 6) {
            toReturn = toReturn + "The " + name + " is " + direction + " very close. ";
        }
        else if (targetDistance < 10) {
            toReturn = toReturn + "The " + name + " is " + direction + " pretty close by. ";
        }
        else if (targetDistance < 20) {
            toReturn = toReturn + "The " + name + " is " + direction + " fairly close by. ";
        }
        else if (targetDistance < 50) {
            toReturn = toReturn + "The " + name + " is some distance away " + direction + ". ";
        }
        else if (targetDistance < 200) {
            toReturn = toReturn + "The " + name + " is quite some distance away " + direction + ". ";
        }
        else if (targetDistance < 500) {
            toReturn = toReturn + "The " + name + " is rather a long distance away " + direction + ". ";
        }
        else if (targetDistance < 1000) {
            toReturn = toReturn + "The " + name + " is pretty far away " + direction + ". ";
        }
        else if (targetDistance < 2000) {
            toReturn = toReturn + "The " + name + " is far away " + direction + ". ";
        }
        else {
            toReturn = toReturn + "The " + name + " is very far away " + direction + ". ";
        }
        return toReturn;
    }
    
    @Override
    public void answer(final Properties aAnswers) {
        if (!this.properlySent) {
            return;
        }
        boolean found = false;
        final String name = aAnswers.getProperty("name");
        if (name != null && name.length() > 1) {
            found = locateCorpse(name, this.getResponder(), this.power, this.override);
        }
        if (!found) {
            this.getResponder().getCommunicator().sendNormalServerMessage("No such soul found.");
        }
    }
    
    public static boolean locateCorpse(final String name, final Creature responder, final double power, final boolean overrideNolocate) {
        boolean found = false;
        final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(name);
        if (pinf == null || !pinf.loaded) {
            return false;
        }
        if (pinf.getPower() > responder.getPower()) {
            return false;
        }
        try {
            final Creature player = Server.getInstance().getCreature(pinf.wurmId);
            boolean nolocation = player.getBonusForSpellEffect((byte)29) >= power;
            if (!nolocation) {
                nolocation = (SpellResist.getSpellResistance(player, 451) < 1.0);
            }
            if (!nolocation || overrideNolocate) {
                int maxDistance = Integer.MAX_VALUE;
                if (Servers.isThisAnEpicOrChallengeServer()) {
                    maxDistance = 200;
                }
                else if (Servers.isThisAChaosServer()) {
                    maxDistance = 500;
                }
                found = true;
                final int centerx = player.getTileX();
                final int centery = player.getTileY();
                final int dx = Math.abs(centerx - responder.getTileX());
                final int dy = Math.abs(centery - responder.getTileY());
                final int mindist = (int)Math.sqrt(dx * dx + dy * dy);
                final int dir = MethodsCreatures.getDir(responder, centerx, centery);
                final float bon = player.getNoLocateItemBonus(mindist <= maxDistance);
                if (bon > 0.0f && 1.0 + power < bon) {
                    found = false;
                }
                if (found) {
                    final String direction = MethodsCreatures.getLocationStringFor(responder.getStatus().getRotation(), dir, "you");
                    final String toReturn = locatePlayerString(mindist, player.getName(), direction, maxDistance);
                    responder.getCommunicator().sendNormalServerMessage(toReturn);
                    if (bon > 0.0f && responder.getKingdomId() != player.getKingdomId()) {
                        SpellResist.addSpellResistance(player, 451, bon);
                    }
                }
            }
        }
        catch (NoSuchCreatureException ex) {}
        catch (NoSuchPlayerException ex2) {}
        final Item[] its = Items.getAllItems();
        for (int itx = 0; itx < its.length; ++itx) {
            if (its[itx].getZoneId() > -1 && its[itx].getTemplateId() == 272 && its[itx].getName().equals("corpse of " + pinf.getName())) {
                found = true;
                final int centerx2 = its[itx].getTileX();
                final int centery2 = its[itx].getTileY();
                final int mindist2 = Math.max(Math.abs(centerx2 - responder.getTileX()), Math.abs(centery2 - responder.getTileY()));
                if (responder.getPower() <= 0) {
                    final int dir2 = MethodsCreatures.getDir(responder, centerx2, centery2);
                    final String direction2 = MethodsCreatures.getLocationStringFor(responder.getStatus().getRotation(), dir2, "you");
                    final String toReturn2 = EndGameItems.getDistanceString(mindist2, its[itx].getName(), direction2, false);
                    if (!its[itx].isOnSurface()) {
                        responder.getCommunicator().sendNormalServerMessage(toReturn2 + " It lies below ground.");
                    }
                    else {
                        responder.getCommunicator().sendNormalServerMessage(toReturn2);
                    }
                }
                else {
                    responder.getCommunicator().sendNormalServerMessage(its[itx].getName() + " at " + centerx2 + ", " + centery2 + " surfaced=" + its[itx].isOnSurface());
                }
            }
        }
        return found;
    }
    
    @Override
    public void sendQuestion() {
        boolean ok = true;
        if (this.getResponder().getPower() <= 0) {
            try {
                ok = false;
                final Action act = this.getResponder().getCurrentAction();
                if (act.getNumber() == 419 || act.getNumber() == 118) {
                    ok = true;
                }
            }
            catch (NoSuchActionException ex) {}
        }
        if (!ok) {
            try {
                final Item arti = Items.getItem(this.target);
                if (arti.getTemplateId() == 332 && arti.getOwnerId() == this.getResponder().getWurmId()) {
                    ok = true;
                }
            }
            catch (NoSuchItemException ex2) {}
        }
        if (ok) {
            this.properlySent = true;
            final StringBuilder sb = new StringBuilder();
            sb.append(this.getBmlHeader());
            sb.append("text{text='Which soul do you wish to locate?'};");
            sb.append("label{text='Name:'};input{id='name';maxchars='40';text=\"\"};");
            sb.append(this.createAnswerButton2());
            this.getResponder().getCommunicator().sendBml(300, 300, true, true, sb.toString(), 200, 200, 200, this.title);
        }
    }
}
