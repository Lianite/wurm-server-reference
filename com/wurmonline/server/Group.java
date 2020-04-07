// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import com.wurmonline.server.players.MapAnnotation;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.logging.Logger;
import com.wurmonline.server.creatures.Creature;
import java.util.Map;

public class Group
{
    protected final Map<String, Creature> members;
    protected String name;
    protected static final Logger logger;
    
    public Group(final String aName) {
        this.members = new HashMap<String, Creature>();
        this.name = aName;
        if (Group.logger.isLoggable(Level.FINER)) {
            Group.logger.finer("Creating a Group - Name: " + this.name);
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    protected void setName(final String newName) {
        this.name = newName;
    }
    
    public boolean contains(final Creature c) {
        return this.members.get(c.getName()) != null;
    }
    
    public boolean isTeam() {
        return false;
    }
    
    public final void addMember(final String aName, final Creature aMember) {
        if (!this.members.values().contains(aMember)) {
            this.members.put(aName, aMember);
        }
    }
    
    public final void dropMember(final String aName) {
        this.members.remove(aName);
    }
    
    int getNumberOfMembers() {
        return (this.members != null) ? this.members.size() : 0;
    }
    
    public final void sendMessage(final Message message) {
        for (final Creature c : this.members.values()) {
            final Creature sender = message.getSender();
            if (sender == null || !c.isIgnored(sender.getWurmId())) {
                c.getCommunicator().sendMessage(message);
            }
        }
    }
    
    public final void sendMapAnnotation(final MapAnnotation[] annotations) {
        for (final Creature c : this.members.values()) {
            c.getCommunicator().sendMapAnnotations(annotations);
        }
    }
    
    public final void sendRemoveMapAnnotation(final MapAnnotation annotation) {
        for (final Creature c : this.members.values()) {
            c.getCommunicator().sendRemoveMapAnnotation(annotation.getId(), annotation.getType(), annotation.getServer());
        }
    }
    
    public final void sendClearMapAnnotationsOfType(final byte type) {
        for (final Creature c : this.members.values()) {
            c.getCommunicator().sendClearMapAnnotationsOfType(type);
        }
    }
    
    public final void broadCastSafe(final String message) {
        this.broadCastSafe(message, (byte)0);
    }
    
    public final void broadCastSafe(final String message, final byte messageType) {
        for (final Creature player : this.members.values()) {
            player.getCommunicator().sendSafeServerMessage(message, messageType);
        }
    }
    
    public final void broadCastAlert(final String message, final byte messageType) {
        for (final Creature player : this.members.values()) {
            player.getCommunicator().sendAlertServerMessage(message, messageType);
        }
    }
    
    public final void broadCastNormal(final String message) {
        for (final Creature player : this.members.values()) {
            player.getCommunicator().sendNormalServerMessage(message);
        }
    }
    
    public boolean containsOfflineMember(final long wurmid) {
        return false;
    }
    
    static {
        logger = Logger.getLogger(Group.class.getName());
    }
}
