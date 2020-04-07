// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import com.wurmonline.server.questions.TicketUpdateQuestion;
import com.wurmonline.server.support.Ticket;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.support.Tickets;
import java.util.LinkedList;
import java.util.List;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.MiscConstants;

public class TicketBehaviour extends Behaviour implements MiscConstants
{
    TicketBehaviour() {
        super((short)50);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final Item object, final int ticketId) {
        return this.getBehavioursFor(performer, ticketId);
    }
    
    @Override
    public List<ActionEntry> getBehavioursFor(final Creature performer, final int ticketId) {
        final List<ActionEntry> toReturn = new LinkedList<ActionEntry>();
        final Ticket ticket = Tickets.getTicket(ticketId);
        if (ticket == null) {
            return toReturn;
        }
        final Player player = (Player)performer;
        if (player.mayHearDevTalk()) {
            if (ticket.isOpen()) {
                toReturn.add(new ActionEntry((short)(-4), "Forward", "forward", TicketBehaviour.emptyIntArr));
                toReturn.add(Actions.actionEntrys[596]);
                toReturn.add(Actions.actionEntrys[591]);
                toReturn.add(Actions.actionEntrys[592]);
                toReturn.add(Actions.actionEntrys[593]);
                if (ticket.getResponderName().equalsIgnoreCase(performer.getName())) {
                    toReturn.add(Actions.actionEntrys[594]);
                }
                if (ticket.getCategoryCode() != 11) {
                    toReturn.add(Actions.actionEntrys[589]);
                }
                toReturn.add(Actions.actionEntrys[590]);
                if (!ticket.getResponderName().equalsIgnoreCase(performer.getName())) {
                    toReturn.add(Actions.actionEntrys[595]);
                }
            }
            else if (ticket.hasFeedback()) {
                toReturn.add(Actions.actionEntrys[597]);
            }
            else if (ticket.getStateCode() == 2) {
                toReturn.add(Actions.actionEntrys[599]);
            }
            toReturn.add(Actions.actionEntrys[587]);
        }
        else if (ticket.getPlayerId() == player.getWurmId()) {
            if (ticket.isOpen()) {
                if (player.mayHearMgmtTalk() && ticket.getLevelCode() == 1) {
                    toReturn.add(new ActionEntry((short)(-1), "Forward", "forward", TicketBehaviour.emptyIntArr));
                    toReturn.add(Actions.actionEntrys[591]);
                }
                toReturn.add(Actions.actionEntrys[587]);
                toReturn.add(Actions.actionEntrys[588]);
            }
            else {
                toReturn.add(new ActionEntry((short)587, "View", "viewing", TicketBehaviour.emptyIntArr));
                toReturn.add(Actions.actionEntrys[597]);
            }
        }
        else if (player.mayHearMgmtTalk()) {
            if (ticket.isOpen() && player.mayMute()) {
                toReturn.add(new ActionEntry((short)(-2), "Forward", "forward", TicketBehaviour.emptyIntArr));
                toReturn.add(Actions.actionEntrys[596]);
                toReturn.add(Actions.actionEntrys[591]);
                if (ticket.getResponderName().equalsIgnoreCase(performer.getName())) {
                    toReturn.add(Actions.actionEntrys[594]);
                }
                toReturn.add(Actions.actionEntrys[589]);
                toReturn.add(Actions.actionEntrys[590]);
            }
            toReturn.add(Actions.actionEntrys[587]);
        }
        return toReturn;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final int ticketId, final short action, final float counter) {
        final Ticket ticket = Tickets.getTicket(ticketId);
        final Player player = (Player)performer;
        if (player.mayHearDevTalk()) {
            if (ticket.isOpen()) {
                if (action == 596) {
                    this.updateTicket(performer, ticketId, action);
                }
                else if (action == 591) {
                    this.updateTicket(performer, ticketId, action);
                }
                else if (action == 592) {
                    this.updateTicket(performer, ticketId, action);
                }
                else if (action == 593) {
                    this.updateTicket(performer, ticketId, action);
                }
                else if (ticket.getResponderName().equalsIgnoreCase(performer.getName()) && action == 594) {
                    this.updateTicket(performer, ticketId, action);
                }
                else if (action == 589) {
                    player.respondGMTab(ticket.getPlayerName(), String.valueOf(ticket.getTicketId()));
                    if (performer.getPower() >= 2) {
                        ticket.addNewTicketAction((byte)3, performer.getName(), "GM " + performer.getName() + " responded.", (byte)0);
                    }
                    else {
                        ticket.addNewTicketAction((byte)2, performer.getName(), "CM " + performer.getName() + " responded.", (byte)0);
                    }
                }
                else if (action == 590) {
                    this.updateTicket(performer, ticketId, action);
                }
                else if (action == 595) {
                    ticket.addNewTicketAction((byte)11, performer.getName(), performer.getName() + " took ticket.", (byte)1);
                }
            }
            else if (action == 597) {
                this.updateTicket(performer, ticketId, action);
            }
            else if (action == 599 && ticket.getStateCode() == 2) {
                this.updateTicket(performer, ticketId, action);
            }
            if (action == 587) {
                this.updateTicket(performer, ticketId, action);
            }
        }
        else if (ticket.getPlayerId() == player.getWurmId()) {
            if (player.mayHearMgmtTalk() && ticket.getLevelCode() == 1) {
                this.updateTicket(performer, ticketId, action);
            }
            else if (ticket.isOpen() && action == 588) {
                this.updateTicket(performer, ticketId, action);
            }
            else if (action == 587) {
                this.updateTicket(performer, ticketId, action);
            }
            else if (action == 597) {
                this.updateTicket(performer, ticketId, action);
            }
        }
        else if (player.mayHearMgmtTalk()) {
            if (ticket.isOpen()) {
                if (action == 596) {
                    this.updateTicket(performer, ticketId, action);
                }
                else if (action == 591) {
                    this.updateTicket(performer, ticketId, action);
                }
                else if (ticket.getResponderName().equalsIgnoreCase(performer.getName()) && action == 594) {
                    this.updateTicket(performer, ticketId, action);
                }
                else if (action == 589) {
                    player.respondGMTab(ticket.getPlayerName(), String.valueOf(ticket.getTicketId()));
                    ticket.addNewTicketAction((byte)2, performer.getName(), "CM " + performer.getName() + " responded.", (byte)0);
                }
                else if (action == 590) {
                    this.updateTicket(performer, ticketId, action);
                }
            }
            if (action == 587) {
                this.updateTicket(performer, ticketId, action);
            }
        }
        return true;
    }
    
    @Override
    public boolean action(final Action act, final Creature performer, final Item source, final int ticketId, final short action, final float counter) {
        return this.action(act, performer, ticketId, action, counter);
    }
    
    private void updateTicket(final Creature performer, final int ticketId, final short action) {
        final TicketUpdateQuestion tuq = new TicketUpdateQuestion(performer, ticketId, action);
        tuq.sendQuestion();
    }
}
