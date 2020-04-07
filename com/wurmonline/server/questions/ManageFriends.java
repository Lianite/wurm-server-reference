// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.players.PlayerState;
import com.wurmonline.server.players.PlayerInfoFactory;
import java.util.logging.Level;
import java.util.Properties;
import java.util.Arrays;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.players.Friend;
import java.util.logging.Logger;

public final class ManageFriends extends Question
{
    private static final Logger logger;
    private final Friend[] friends;
    private final Player player;
    private static final String line = "label{type=\"bold\";text=\"- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\"}";
    
    public ManageFriends(final Creature aResponder) {
        super(aResponder, aResponder.getName() + "'s List of Friends", "Manage Your List of Friends", 118, -10L);
        this.player = (Player)this.getResponder();
        Arrays.sort(this.friends = this.player.getFriends());
    }
    
    @Override
    public void answer(final Properties aAnswer) {
        this.setAnswer(aAnswer);
        if (this.type == 0) {
            ManageFriends.logger.log(Level.INFO, "Received answer for a question with NOQUESTION.");
            return;
        }
        if (this.type == 118) {
            for (int i = 0; i < this.friends.length; ++i) {
                final String remove = aAnswer.getProperty("rem" + i);
                if (remove != null && remove.equalsIgnoreCase("true")) {
                    this.player.removeFriend(this.friends[i].getFriendId());
                    this.player.removeMeFromFriendsList(this.friends[i].getFriendId(), this.friends[i].getName());
                    final ManageFriends mf = new ManageFriends(this.getResponder());
                    mf.sendQuestion();
                    return;
                }
            }
            final String reply = aAnswer.getProperty("reply");
            if (reply != null && reply.equalsIgnoreCase("true")) {
                final String cat = aAnswer.getProperty("cat");
                final String category = Friend.Category.catFromInt(Integer.parseInt(cat)).name();
                final String wffn = this.player.waitingForFriend();
                if (wffn.length() > 0) {
                    this.player.getCommunicator().addFriend(wffn, category);
                    final ManageFriends mf2 = new ManageFriends(this.getResponder());
                    mf2.sendQuestion();
                    return;
                }
                this.player.getCommunicator().sendNormalServerMessage("Too slow! Noone is waiting for a reply anymore.");
            }
            final String add = aAnswer.getProperty("add");
            if (add != null && add.equalsIgnoreCase("true")) {
                final String addname = aAnswer.getProperty("addname");
                final String cat2 = aAnswer.getProperty("addcat");
                final String category2 = Friend.Category.catFromInt(Integer.parseInt(cat2)).name();
                if (addname.length() < 3) {
                    this.player.getCommunicator().sendNormalServerMessage("Name is too short");
                }
                else {
                    this.player.getCommunicator().addFriend(addname, category2);
                }
                final ManageFriends mf3 = new ManageFriends(this.getResponder());
                mf3.sendQuestion();
                return;
            }
            final String update = aAnswer.getProperty("update");
            if (update != null && update.equalsIgnoreCase("true")) {
                boolean didChange = false;
                for (int j = 0; j < this.friends.length; ++j) {
                    final String cat3 = aAnswer.getProperty("cat" + j);
                    final String note = aAnswer.getProperty("note" + j);
                    if (cat3 != null) {
                        final byte catId = Byte.parseByte(cat3);
                        if (this.friends[j].getCatId() != catId || !this.friends[j].getNote().equals(note)) {
                            ((Player)this.getResponder()).updateFriendData(this.friends[j].getFriendId(), catId, note);
                            if (this.friends[j].getCatId() != catId) {
                                this.getResponder().getCommunicator().sendNormalServerMessage(this.friends[j].getName() + " is now in your category " + Friend.Category.catFromInt(catId).name() + ".");
                            }
                            if (!this.friends[j].getNote().equals(note)) {
                                this.getResponder().getCommunicator().sendNormalServerMessage("You added a note for " + this.friends[j].getName() + ".");
                            }
                            didChange = true;
                        }
                    }
                }
                if (!didChange) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("You decide not to do anything.");
                }
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        boolean notFound = false;
        buf.append(this.getBmlHeader());
        buf.append("text{text=\"\"};");
        int row = 0;
        final String blank = "image{src=\"img.gui.bridge.blank\";size=\"200,1\";text=\"\"}";
        for (final Friend friend : this.friends) {
            final PlayerState pState = PlayerInfoFactory.getPlayerState(friend.getFriendId());
            String pName = "Not found";
            final int cat = friend.getCatId();
            if (pState == null) {
                notFound = true;
            }
            else {
                pName = pState.getPlayerName();
            }
            buf.append("harray{varray{image{src=\"img.gui.bridge.blank\";size=\"200,1\";text=\"\"}label{text=\"" + pName + "\"}};radio{group=\"cat" + row + "\";id=\"3\";selected=\"" + (cat == 3) + "\";text=\"Trusted \";hover=\"Trusted\"}radio{group=\"cat" + row + "\";id=\"2\";selected=\"" + (cat == 2) + "\";text=\"Friend \";hover=\"Friend\"}radio{group=\"cat" + row + "\";id=\"1\";selected=\"" + (cat == 1) + "\";text=\"Contact \";hover=\"Contact\"}radio{group=\"cat" + row + "\";id=\"0\";selected=\"" + (cat == 0) + "\";text=\"Other \";hover=\"Other\"}harray{label{text=\" \"};button{id=\"rem" + row + "\";text=\"Remove\";confirm=\"You are about to remove " + pName + "  from your friends list.\";question=\"Do you really want to do that?\";hover=\"remove " + pName + " from your friends list\"}}}");
            buf.append("input{maxchars=\"40\";id=\"note" + row + "\";text=\"" + friend.getNote() + "\"};");
            ++row;
        }
        buf.append("text{text=\"\"};");
        buf.append("harray{button{text=\"Update Friends\";id=\"update\"}};");
        buf.append("text{text=\"\"};");
        if (notFound) {
            buf.append("label{text=\"'Not Found' could be the result of a server being offline.\"};");
        }
        buf.append("label{text=\"Note 'Remove' is immediate, but does double check.\"};");
        buf.append("label{type=\"bold\";text=\"- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\"}");
        final String wffn = this.player.waitingForFriend();
        if (wffn.length() != 0) {
            if (this.player.askingFriend()) {
                buf.append("text{text=\"You are still waiting for a response from " + wffn + ".\"};");
            }
            else {
                buf.append("label{text=\"" + wffn + " is waiting for you to add them to their list of friends. \"};");
                buf.append("harray{button{text=\"Send Reply\";id=\"reply\"};label{text=\" and add them to \"};dropdown{id=\"cat\";default=\"0\";options=\"Other,Contacts,Friends,Trusted\"}label{text=\" category.\"}};");
            }
        }
        else {
            buf.append("harray{button{text=\"Send Request\";id=\"add\"};label{text=\" to \"};input{maxchars=\"40\";id=\"addname\";onenter=\"add\"};label{text=\" so can add them to your \"};dropdown{id=\"addcat\";default=\"0\";options=\"Other,Contacts,Friends,Trusted\"}label{text=\" category.\"}};");
        }
        buf.append("text{text=\"\"}");
        buf.append("}};null;null;}");
        this.getResponder().getCommunicator().sendBml(500, 300, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    static {
        logger = Logger.getLogger(ManageFriends.class.getName());
    }
}
