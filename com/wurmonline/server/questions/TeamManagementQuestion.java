// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.Group;
import com.wurmonline.server.Servers;
import java.util.Arrays;
import java.util.Comparator;
import com.wurmonline.server.NoSuchGroupException;
import com.wurmonline.server.Groups;
import com.wurmonline.server.Team;
import java.util.Properties;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.Server;
import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.creatures.Creature;

public class TeamManagementQuestion extends Question
{
    private final Creature invited;
    private final boolean mayInvite;
    private boolean founding;
    private boolean sendToResponder;
    private String teamName;
    private boolean managing;
    private boolean removing;
    private final List<Creature> memberlist;
    
    public TeamManagementQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final boolean _mayInvite, final long aTarget, final boolean manage, final boolean removeSelf) throws NoSuchCreatureException, NoSuchPlayerException {
        super(aResponder, aTitle, aQuestion, 85, aTarget);
        this.sendToResponder = true;
        this.managing = false;
        this.removing = false;
        this.memberlist = new LinkedList<Creature>();
        this.invited = Server.getInstance().getCreature(aTarget);
        if (this.getResponder().getTeam() == null) {
            this.founding = true;
        }
        else {
            this.founding = false;
        }
        this.mayInvite = _mayInvite;
        this.managing = manage;
        this.removing = removeSelf;
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        final Creature asker = this.getResponder();
        if (this.sendToResponder) {
            if (this.managing) {
                final String cancel = this.getAnswer().getProperty("cancel");
                if (cancel != null && cancel.equals("true")) {
                    return;
                }
                final String did = this.getAnswer().getProperty("did");
                if (did != null) {
                    try {
                        final int x = Integer.parseInt(did);
                        final Creature c = this.memberlist.get(x);
                        final String kick = this.getAnswer().getProperty("kick");
                        if (kick != null && kick.equals("true")) {
                            if (this.getResponder().getTeam() == c.getTeam()) {
                                this.getResponder().getCommunicator().sendNormalServerMessage("You remove " + c.getName() + " from the team.");
                                c.setTeam(null, true);
                            }
                        }
                        else {
                            final String appoint = this.getAnswer().getProperty("appoint");
                            if (appoint != null && appoint.equals("true")) {
                                this.getResponder().getTeam().setNewLeader(c);
                            }
                            final String invite = this.getAnswer().getProperty("invite");
                            if (invite != null && invite.equals("true") && !c.isTeamLeader()) {
                                c.setMayInviteTeam(!c.mayInviteTeam());
                            }
                        }
                    }
                    catch (NumberFormatException nf) {
                        asker.getCommunicator().sendNormalServerMessage("Failed to parse " + did + " to a number.");
                    }
                }
            }
            else if (this.removing) {
                final String cancel = this.getAnswer().getProperty("cancel");
                if (cancel != null && cancel.equals("true")) {
                    return;
                }
                if (this.getResponder().getTeam() != null) {
                    this.getResponder().setTeam(null, true);
                }
            }
            else if (this.invited == null || !this.invited.hasLink()) {
                asker.getCommunicator().sendNormalServerMessage("The team invitation is lost - the creature can no longer be found.");
            }
            else {
                final String cancel = this.getAnswer().getProperty("cancel");
                if (cancel != null && cancel.equals("true")) {
                    asker.getCommunicator().sendNormalServerMessage("You decide to skip inviting for now.");
                }
                else {
                    final boolean _mayInvite = this.getAnswer().getProperty("invite") != null && this.getAnswer().getProperty("invite").equals("true");
                    this.teamName = this.getAnswer().getProperty("teamname");
                    if (this.teamName != null) {
                        if (this.teamName.length() >= 21) {
                            this.getResponder().getCommunicator().sendNormalServerMessage("Please select a shorter name. Max 21 characters.");
                            return;
                        }
                        if (QuestionParser.containsIllegalVillageCharacters(this.teamName)) {
                            this.getResponder().getCommunicator().sendNormalServerMessage("The name " + this.teamName + " contains illegal characters. Please select another name.");
                            return;
                        }
                    }
                    if (this.teamName == null) {
                        if (this.founding) {
                            this.teamName = asker.getNamePossessive();
                        }
                        else if (asker.getTeam() != null) {
                            this.teamName = asker.getTeam().getName();
                        }
                    }
                    try {
                        Groups.getGroup(this.teamName);
                        this.getResponder().getCommunicator().sendNormalServerMessage("The name " + this.teamName + " is already in use. Please select another name.");
                    }
                    catch (NoSuchGroupException ex2) {
                        try {
                            final TeamManagementQuestion tj = new TeamManagementQuestion(asker, "Joining a team", "Do you want to join " + asker.getNamePossessive() + " team?", _mayInvite, this.target, false, false);
                            tj.teamName = this.teamName;
                            tj.sendToResponder = false;
                            tj.sendQuestion();
                            asker.getCommunicator().sendNormalServerMessage("You ask " + this.invited.getName() + " to join your team " + this.teamName);
                        }
                        catch (Exception ex) {
                            asker.getCommunicator().sendNormalServerMessage("The player could not be found.");
                        }
                    }
                }
            }
        }
        else {
            final boolean join = this.getAnswer().getProperty("join") != null && this.getAnswer().getProperty("join").equals("true");
            if (this.invited == null || !this.invited.hasLink()) {
                asker.getCommunicator().sendNormalServerMessage("The team invitation is lost - the creature can no longer be found.");
            }
            else if (!join) {
                this.invited.getCommunicator().sendNormalServerMessage("You decline to join the team.");
                asker.getCommunicator().sendNormalServerMessage(this.invited.getName() + " declines to join the team.");
            }
            else if (!this.founding && asker.getTeam() == null) {
                this.invited.getCommunicator().sendNormalServerMessage(asker.getName() + " is no longer part of a team.");
                asker.getCommunicator().sendNormalServerMessage("You are no longer part of a team.");
            }
            else if (!asker.mayInviteTeam()) {
                this.invited.getCommunicator().sendNormalServerMessage(asker.getName() + " may no longer invite to " + asker.getHisHerItsString() + " team.");
                asker.getCommunicator().sendNormalServerMessage("You may no longer invite to the team.");
            }
            else {
                if (this.founding) {
                    this.getResponder().setTeam(new Team(this.teamName, this.getResponder()), true);
                    this.getResponder().getCommunicator().sendNormalServerMessage("You form a team with " + this.invited.getName() + ".");
                }
                this.invited.setTeam(this.getResponder().getTeam(), true);
                this.invited.setMayInviteTeam(this.mayInvite);
                this.invited.getCommunicator().sendNormalServerMessage("You join " + this.teamName + ".");
            }
        }
    }
    
    public Creature getInvited() {
        return this.invited;
    }
    
    @Override
    public void sendQuestion() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeader());
        if (this.sendToResponder) {
            if (this.managing) {
                buf.append("text{text=\"Here you may remove team members or appoint a new leader.\"};");
                final Creature[] members = this.getResponder().getTeam().getMembers();
                Arrays.sort(members, new Comparator<Creature>() {
                    @Override
                    public int compare(final Creature o1, final Creature o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                buf.append("harray{dropdown{id='did';options=\"");
                for (int x = 0; x < members.length; ++x) {
                    if (x > 0) {
                        buf.append(",");
                    }
                    this.memberlist.add(members[x]);
                    buf.append(members[x].getName());
                    if (members[x].mayInviteTeam()) {
                        buf.append(" (invite)");
                    }
                }
                buf.append("\"}};");
                buf.append("text{type='italic';text=\"The people who may invite to the team are listed with (invite) after their name.\"};");
                buf.append("text{text=\"\"};");
                buf.append("checkbox{  id='kick';text='Remove';selected='false'}");
                buf.append("checkbox{  id='appoint';text='Appoint leader';selected='false'}");
                buf.append("checkbox{  id='invite';text='Toggle may invite';selected='false'}");
                buf.append("harray {button{text='Cancel';id='cancel'};label{text=' ';id='spacedlxg'};button{text='Send';id='submit'}}}};null;null;}");
            }
            else if (this.removing) {
                buf.append("text{text=\"Do you really want to leave the team?\"};");
                buf.append("harray {button{text='Cancel';id='cancel'};label{text=' ';id='spacedlxg'};button{text='Leave the team!';id='submit'}}}};null;null;}");
            }
            else {
                String tn = this.getResponder().getNamePossessive();
                boolean label = false;
                if (this.getResponder().getTeam() != null) {
                    tn = this.getResponder().getTeam().getName();
                    label = true;
                }
                if (this.founding) {
                    buf.append("text{text=\"Do you want to form a team with " + this.invited.getName() + "?\"}");
                    buf.append("text{text=\"The main benefit of being in a team is a new chat window.\"}");
                    if (!Servers.localServer.PVPSERVER) {
                        buf.append("text{text=\"As part of a team you can also use each others tools and resources easier.\"}");
                        buf.append("text{text=\"Other team members will be able to pick up your stuff close to you.\"}");
                    }
                }
                else {
                    buf.append("text{text=\"Do you want to invite " + this.invited.getName() + " to " + tn + "?\"}");
                }
                buf.append("text{text=\"\"};");
                if (!label) {
                    buf.append("label{text=\"Select a name\"};");
                    buf.append("input{id=\"teamname\";maxchars=\"20\";text=\"" + tn + "\"};");
                }
                buf.append("label{text=\"Should " + this.invited.getName() + " be allowed to invite others to the team?\"};");
                buf.append("radio{ group='invite'; id='true';text='Yes';selected='true'}");
                buf.append("radio{ group='invite'; id='false';text='No'}");
                buf.append("harray {button{text='Cancel';id='cancel'};label{text=' ';id='spacedlxg'};button{text='Form team!';id='submit'}}}};null;null;}");
            }
            this.getResponder().getCommunicator().sendBml(400, 300, true, true, buf.toString(), 200, 200, 200, this.title);
        }
        else {
            final Group team = this.getResponder().getTeam();
            buf.append("text{text=\"You have been invited by " + this.getResponder().getName() + " to join " + this.getResponder().getHisHerItsString() + " team " + this.teamName + ".\"}");
            buf.append("text{text=\"\"};");
            if (this.invited.getTeam() != null && this.invited.getTeam() != team) {
                buf.append("text{text=\"You will no longer be part of the team " + this.invited.getTeam().getName() + " if you accept.\"}");
            }
            buf.append("text{text=\"The main benefit of being in a team is a new chat window.\"}");
            if (!Servers.localServer.PVPSERVER) {
                buf.append("text{text=\"As part of a team you can also use each others tools and resources easier.\"}");
                buf.append("text{text=\"Other team members will be able to pick up your stuff close to you.\"}");
            }
            buf.append("text{text=\"\"};");
            buf.append("text{text=\"Do you want to join " + this.teamName + "?\"}");
            buf.append("text{text=\"\"};");
            buf.append("radio{ group='join'; id='true';text='Yes'}");
            buf.append("radio{ group='join'; id='false';text='No';selected='true'}");
            buf.append(this.createAnswerButton2());
            this.getInvited().getCommunicator().sendBml(400, 300, true, true, buf.toString(), 200, 200, 200, this.title);
        }
    }
}
