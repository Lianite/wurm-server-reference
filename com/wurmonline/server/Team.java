// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import com.wurmonline.server.creatures.Creature;

public class Team extends Group
{
    private Creature leader;
    private final Map<Long, Boolean> offlineMembers;
    
    public Team(final String aName, final Creature _leader) {
        super(aName);
        this.leader = null;
        this.offlineMembers = new HashMap<Long, Boolean>();
        this.leader = _leader;
    }
    
    @Override
    public boolean isTeam() {
        return true;
    }
    
    public boolean isTeamLeader(final Creature c) {
        return c == this.leader;
    }
    
    public Creature[] getMembers() {
        return this.members.values().toArray(new Creature[this.members.size()]);
    }
    
    public final void setNewLeader(final Creature newLeader) {
        this.leader = newLeader;
        final Message m = new Message(newLeader, (byte)((newLeader == this.leader) ? 14 : 13), "Team", newLeader.getName() + " has been appointed new leader.");
        for (final Creature c : this.members.values()) {
            c.getCommunicator().sendRemoveTeam(newLeader.getName());
            c.getCommunicator().sendAddTeam(newLeader.getName(), newLeader.getWurmId());
            c.getCommunicator().sendMessage(m);
        }
    }
    
    public final void creatureJoinedTeam(final Creature joined) {
        this.addMember(joined.getName(), joined);
        final Message m = new Message(joined, (byte)((joined == this.leader) ? 14 : 13), "Team", "Welcome to team chat.");
        joined.getCommunicator().sendMessage(m);
        for (final Creature c : this.members.values()) {
            c.getCommunicator().sendAddTeam(joined.getName(), joined.getWurmId());
            joined.getCommunicator().sendAddTeam(c.getName(), c.getWurmId());
        }
        if (this.offlineMembers.containsKey(joined.getWurmId())) {
            final Boolean mayInvite = this.offlineMembers.remove(joined.getWurmId());
            joined.setMayInviteTeam(mayInvite);
        }
    }
    
    public final void creatureReconnectedTeam(final Creature joined) {
        final Message m = new Message(joined, (byte)((joined == this.leader) ? 14 : 13), "Team", "Welcome to team chat.");
        joined.getCommunicator().sendMessage(m);
        for (final Creature c : this.members.values()) {
            joined.getCommunicator().sendAddTeam(c.getName(), c.getWurmId());
        }
    }
    
    public final void creaturePartedTeam(final Creature parted, final boolean sendRemove) {
        for (final Creature c : this.members.values()) {
            c.getCommunicator().sendRemoveTeam(parted.getName());
            if (sendRemove) {
                parted.getCommunicator().sendRemoveTeam(c.getName());
            }
        }
        this.dropMember(parted.getName());
        if (this.members.size() == 1) {
            final Creature[] s = this.getMembers();
            s[0].getCommunicator().sendNormalServerMessage("The team has dissolved.");
            s[0].setTeam(null, true);
        }
        else if (this.members.size() > 1) {
            if (parted == this.leader) {
                final Creature[] s = this.getMembers();
                this.setNewLeader(s[0]);
                if (!sendRemove) {
                    this.offlineMembers.put(parted.getWurmId(), parted.mayInviteTeam());
                }
            }
        }
        else {
            Groups.removeGroup(this.name);
        }
    }
    
    public final void sendTeamMessage(final Creature sender, final Message message) {
        for (final Creature c : this.members.values()) {
            if (!c.isIgnored(message.getSender().getWurmId())) {
                c.getCommunicator().sendMessage(message);
            }
        }
    }
    
    @Override
    public boolean containsOfflineMember(final long wurmid) {
        return this.offlineMembers.keySet().contains(wurmid);
    }
    
    public final void sendTeamMessage(final Creature sender, final String message) {
        final Message m = new Message(sender, (byte)((sender == this.leader) ? 14 : 13), "Team", "<" + sender.getName() + "> " + message);
        for (final Creature c : this.members.values()) {
            if (!c.isIgnored(m.getSender().getWurmId())) {
                c.getCommunicator().sendMessage(m);
            }
        }
    }
}
