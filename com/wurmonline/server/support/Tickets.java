// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.support;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.Players;
import com.wurmonline.server.players.PlayerState;
import java.util.Iterator;
import java.util.HashMap;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.webinterface.WcTicket;
import com.wurmonline.server.Servers;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.shared.constants.CounterTypes;

public final class Tickets implements CounterTypes, TimeConstants
{
    private static Logger logger;
    private static final Map<Integer, Ticket> tickets;
    private static final ConcurrentLinkedDeque<TicketToSend> ticketsToSend;
    private static final ConcurrentLinkedDeque<TicketToUpdate> ticketsToUpdate;
    private static final String LOADTICKETNOS = "SELECT * FROM TICKETNOS";
    private static final String ADDTICKETNOS = "INSERT INTO TICKETNOS (PK,NEXTTICKETID,LASTTICKETID,NEXTBATCH) VALUES(0,?,?,?)";
    private static final String UPDATETICKETNOS = "UPDATE TICKETNOS SET NEXTTICKETID=?, LASTTICKETID=?, NEXTBATCH=? WHERE PK=0";
    private static final String LOADALLTICKETS = "SELECT * FROM TICKETS";
    private static final String LOADALLTICKETACTIONS = "SELECT * FROM TICKETACTIONS";
    private static final String PURGEBADACTIONS = "DELETE FROM TICKETACTIONS USING TICKETACTIONS LEFT JOIN TICKETS ON TICKETS.TICKETID = TICKETACTIONS.TICKETID WHERE TICKETS.TICKETID IS NULL";
    private static int nextTicketId;
    private static int lastTicketId;
    private static int nextBatch;
    
    public static void loadTickets() {
        final long start = System.nanoTime();
        try {
            dbLoadTicketNos();
            dbLoadAllTickets();
        }
        catch (Exception ex) {
            Tickets.logger.log(Level.WARNING, "Problems loading Tickets", ex);
        }
        final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
        Tickets.logger.log(Level.INFO, "Loaded " + Tickets.tickets.size() + " Tickets. It took " + lElapsedTime + " millis.");
    }
    
    public static Ticket addTicket(final Ticket newTicket, final boolean saveit) {
        if (Tickets.tickets.containsKey(newTicket.getTicketId())) {
            final Ticket oldTicket = Tickets.tickets.get(newTicket.getTicketId());
            oldTicket.update(newTicket.isGlobal(), newTicket.getClosedDate(), newTicket.getStateCode(), newTicket.getLevelCode(), newTicket.getResponderName(), newTicket.getDescription(), newTicket.getRefFeedback(), newTicket.getAcknowledged());
            return oldTicket;
        }
        Tickets.tickets.put(newTicket.getTicketId(), newTicket);
        if (saveit) {
            newTicket.save();
        }
        return newTicket;
    }
    
    public static long calcWurmId(final int ticketId) {
        return (ticketId << 8) + 25;
    }
    
    public static int decodeTicketId(final long wurmId) {
        return (int)(wurmId >>> 8 & -1L);
    }
    
    public static int getNextTicketNo() {
        final int nextTicketNo = Tickets.nextTicketId;
        ++Tickets.nextTicketId;
        if (Tickets.nextTicketId > Tickets.lastTicketId) {
            Tickets.nextTicketId = Tickets.nextBatch;
            Tickets.lastTicketId = Tickets.nextTicketId + 9999;
            if (Servers.isThisLoginServer()) {
                Tickets.nextBatch += 10000;
            }
            else {
                Tickets.nextBatch = 0;
                final WcTicket wt = new WcTicket(1);
                wt.sendToLoginServer();
            }
        }
        dbUpdateTicketNos();
        return nextTicketNo;
    }
    
    public static int getNextBatchNo() {
        if (Servers.isThisLoginServer()) {
            final int nextBatchNo = Tickets.nextBatch;
            Tickets.nextBatch += 10000;
            dbUpdateTicketNos();
            return nextBatchNo;
        }
        return Tickets.nextBatch;
    }
    
    public static void checkBatchNos() {
        if (Tickets.nextTicketId == 0) {
            final WcTicket wt = new WcTicket(2);
            wt.sendToLoginServer();
        }
        else if (Tickets.nextBatch == 0) {
            final WcTicket wt = new WcTicket(1);
            wt.sendToLoginServer();
        }
    }
    
    public static void setNextBatchNo(final int newBatchNo, final int secondBatchNo) {
        if (Tickets.nextTicketId == 0) {
            Tickets.nextTicketId = newBatchNo;
            Tickets.lastTicketId = newBatchNo + 9999;
            if (Tickets.nextBatch == 0) {
                Tickets.nextBatch = secondBatchNo;
            }
        }
        else if (Tickets.nextBatch == 0) {
            Tickets.nextBatch = newBatchNo;
        }
        dbUpdateTicketNos();
    }
    
    public static Ticket[] getTickets(final Player player) {
        final Map<Integer, Ticket> playerTickets = new HashMap<Integer, Ticket>();
        for (final Map.Entry<Integer, Ticket> entry : Tickets.tickets.entrySet()) {
            if (entry.getValue().isTicketShownTo(player)) {
                playerTickets.put(entry.getKey(), entry.getValue());
            }
        }
        return playerTickets.values().toArray(new Ticket[playerTickets.size()]);
    }
    
    public static void acknowledgeTicketUpdatesFor(final Player player) {
        for (final Map.Entry<Integer, Ticket> entry : Tickets.tickets.entrySet()) {
            entry.getValue().acknowledgeTicketUpdate(player);
        }
    }
    
    public static Ticket[] getTicketsChangedSince(final long aDate) {
        final Map<Integer, Ticket> playerTickets = new HashMap<Integer, Ticket>();
        for (final Map.Entry<Integer, Ticket> entry : Tickets.tickets.entrySet()) {
            if (entry.getValue().hasTicketChangedSince(aDate)) {
                playerTickets.put(entry.getKey(), entry.getValue());
            }
        }
        return playerTickets.values().toArray(new Ticket[playerTickets.size()]);
    }
    
    public static long getLatestActionDate() {
        long latestTicketDate = 0L;
        for (final Map.Entry<Integer, Ticket> entry : Tickets.tickets.entrySet()) {
            if (latestTicketDate < entry.getValue().getLatestActionDate()) {
                latestTicketDate = entry.getValue().getLatestActionDate();
            }
        }
        return latestTicketDate;
    }
    
    public static Ticket[] getDirtyTickets() {
        final Map<Integer, Ticket> dirtyTickets = new HashMap<Integer, Ticket>();
        for (final Map.Entry<Integer, Ticket> entry : Tickets.tickets.entrySet()) {
            final Ticket ticket = entry.getValue();
            if (ticket.isDirty() && (ticket.isGlobal() || ticket.isClosed())) {
                dirtyTickets.put(entry.getKey(), entry.getValue());
            }
        }
        return dirtyTickets.values().toArray(new Ticket[dirtyTickets.size()]);
    }
    
    public static Ticket[] getArchiveTickets() {
        final Map<Integer, Ticket> archiveTickets = new HashMap<Integer, Ticket>();
        for (final Map.Entry<Integer, Ticket> entry : Tickets.tickets.entrySet()) {
            final Ticket ticket = entry.getValue();
            if (ticket.getArchiveState() == 2) {
                archiveTickets.put(entry.getKey(), entry.getValue());
            }
        }
        return archiveTickets.values().toArray(new Ticket[archiveTickets.size()]);
    }
    
    public static Ticket getTicket(final int ticketNo) {
        return Tickets.tickets.get(ticketNo);
    }
    
    public static void removeTicket(final int ticketNo) {
        Tickets.tickets.remove(ticketNo);
    }
    
    public static void playerStateChange(final PlayerState pState) {
        for (final Ticket ticket : Tickets.tickets.values()) {
            if (ticket.getPlayerId() == pState.getPlayerId() && ticket.isOpen()) {
                Players.getInstance().sendTicket(ticket, null);
            }
        }
    }
    
    private static void dbLoadTicketNos() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean create = false;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM TICKETNOS");
            rs = ps.executeQuery();
            if (rs.next()) {
                Tickets.nextTicketId = rs.getInt("NEXTTICKETID");
                Tickets.lastTicketId = rs.getInt("LASTTICKETID");
                Tickets.nextBatch = rs.getInt("NEXTBATCH");
            }
            else if (Servers.isThisLoginServer()) {
                Tickets.nextTicketId = 10000;
                Tickets.lastTicketId = 9999;
                Tickets.nextBatch = 200000;
                create = true;
            }
            else {
                final int lastDigits = Servers.getLocalServerId() % 100;
                Tickets.nextTicketId = lastDigits * 10000;
                Tickets.lastTicketId = Tickets.nextTicketId + 9999;
                Tickets.nextBatch = 0;
                create = true;
            }
            if (create) {
                DbUtilities.closeDatabaseObjects(ps, rs);
                ps = dbcon.prepareStatement("INSERT INTO TICKETNOS (PK,NEXTTICKETID,LASTTICKETID,NEXTBATCH) VALUES(0,?,?,?)");
                ps.setInt(1, Tickets.nextTicketId);
                ps.setInt(2, Tickets.lastTicketId);
                ps.setInt(3, Tickets.nextBatch);
                ps.executeUpdate();
            }
        }
        catch (SQLException sqx) {
            Tickets.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbUpdateTicketNos() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE TICKETNOS SET NEXTTICKETID=?, LASTTICKETID=?, NEXTBATCH=? WHERE PK=0");
            ps.setInt(1, Tickets.nextTicketId);
            ps.setInt(2, Tickets.lastTicketId);
            ps.setInt(3, Tickets.nextBatch);
            if (ps.executeUpdate() == 0) {
                DbUtilities.closeDatabaseObjects(ps, null);
                ps = dbcon.prepareStatement("INSERT INTO TICKETNOS (PK,NEXTTICKETID,LASTTICKETID,NEXTBATCH) VALUES(0,?,?,?)");
                ps.setInt(1, Tickets.nextTicketId);
                ps.setInt(2, Tickets.lastTicketId);
                ps.setInt(3, Tickets.nextBatch);
                ps.executeUpdate();
            }
        }
        catch (SQLException sqx) {
            Tickets.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private static void dbLoadAllTickets() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            if (!DbConnector.isUseSqlite()) {
                ps = dbcon.prepareStatement("DELETE FROM TICKETACTIONS USING TICKETACTIONS LEFT JOIN TICKETS ON TICKETS.TICKETID = TICKETACTIONS.TICKETID WHERE TICKETS.TICKETID IS NULL");
                final int purgedActions = ps.executeUpdate();
                if (purgedActions > 0) {
                    Tickets.logger.log(Level.INFO, "Purged " + purgedActions + " Ticket Actions from database.");
                }
                DbUtilities.closeDatabaseObjects(ps, null);
            }
            ps = dbcon.prepareStatement("SELECT * FROM TICKETS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int aTicketId = rs.getInt("TICKETID");
                final long aTicketDate = rs.getLong("TICKETDATE");
                final long aPlayerWurmId = rs.getLong("PLAYERWURMID");
                final String aPlayerName = rs.getString("PLAYERNAME");
                final byte aTicketCategory = rs.getByte("CATEGORYCODE");
                final int aServerId = rs.getInt("SERVERID");
                final boolean aGloabl = rs.getBoolean("ISGLOBAL");
                final long aClosedDate = rs.getLong("CLOSEDDATE");
                final byte aState = rs.getByte("STATECODE");
                final byte aLevel = rs.getByte("LEVELCODE");
                final String aResponderName = rs.getString("RESPONDERNAME");
                final String aDescription = rs.getString("DESCRIPTION");
                final boolean isDirty = rs.getBoolean("ISDIRTY");
                final short refFeedback = rs.getShort("REFFEEDBACK");
                final String trelloFeedbackCardId = rs.getString("TRELLOFEEDBACKCARDID");
                final String trelloCardId = rs.getString("TRELLOCARDID");
                final byte trelloListCode = rs.getByte("TRELLOLISTCODE");
                final boolean hasDescriptionChanged = rs.getBoolean("HASDESCRIPTIONCHANGED");
                final boolean hasSummaryChanged = rs.getBoolean("HASSUMMARYCHANGED");
                final boolean hasListChanged = rs.getBoolean("HASTRELLOLISTCHANGED");
                final byte archiveCode = rs.getByte("ARCHIVESTATECODE");
                final boolean acknowledged = rs.getBoolean("ACKNOWLEDGED");
                addTicket(new Ticket(aTicketId, aTicketDate, aPlayerWurmId, aPlayerName, aTicketCategory, aServerId, aGloabl, aClosedDate, aState, aLevel, aResponderName, aDescription, isDirty, refFeedback, trelloFeedbackCardId, trelloCardId, trelloListCode, hasDescriptionChanged, hasSummaryChanged, hasListChanged, archiveCode, acknowledged), false);
            }
            DbUtilities.closeDatabaseObjects(ps, rs);
            ps = dbcon.prepareStatement("SELECT * FROM TICKETACTIONS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final int aTicketId = rs.getInt("TICKETID");
                final short aActionNo = rs.getShort("ACTIONNO");
                final long aActionDate = rs.getLong("ACTIONDATE");
                final byte aAction = rs.getByte("ACTIONTYPE");
                final String aByWhom = rs.getString("BYWHOM");
                final String aNote = rs.getString("NOTE");
                final byte aVisibilityLevel = rs.getByte("VISIBILITYLEVEL");
                final boolean isDirty2 = rs.getBoolean("ISDIRTY");
                final byte aQualityOfServiceCode = rs.getByte("QUALITYOFSERVICECODE");
                final byte aCourteousCode = rs.getByte("COURTEOUSCODE");
                final byte aKnowledgeableCode = rs.getByte("KNOWLEDGEABLECODE");
                final byte aGeneralFlags = rs.getByte("GENERALFLAGS");
                final byte aQualitiesFlags = rs.getByte("QUALITIESFLAGS");
                final byte aIrkedFlags = rs.getByte("IRKEDFLAGS");
                final String aTrelloLink = rs.getString("TRELLOCOMMENTID");
                getTicket(aTicketId).addTicketAction(new TicketAction(aTicketId, aActionNo, aAction, aActionDate, aByWhom, aNote, aVisibilityLevel, isDirty2, aQualityOfServiceCode, aCourteousCode, aKnowledgeableCode, aGeneralFlags, aQualitiesFlags, aIrkedFlags, aTrelloLink));
            }
            DbUtilities.closeDatabaseObjects(ps, rs);
        }
        catch (SQLException sqx) {
            Tickets.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public static String convertTime(final long time) {
        final String fd = new SimpleDateFormat("dd/MMM/yyyy HH:mm").format(new Date(time));
        return fd;
    }
    
    public static final void addTicketToSend(final Ticket ticket, final int numbActions, final TicketAction action) {
        Tickets.ticketsToSend.add(new TicketToSend(ticket, numbActions, action));
    }
    
    public static final void addTicketToSend(final Ticket ticket) {
        Tickets.ticketsToSend.add(new TicketToSend(ticket));
    }
    
    public static final void addTicketToSend(final Ticket ticket, final TicketAction action) {
        Tickets.ticketsToSend.add(new TicketToSend(ticket, action));
    }
    
    public static final void setTicketArchiveState(final Ticket ticket, final byte newArchiveState) {
        Tickets.ticketsToUpdate.add(new TicketToUpdate(ticket, (byte)0, newArchiveState));
    }
    
    public static final void setTicketTrelloCardId(final Ticket ticket, final String aTrelloCardId) {
        Tickets.ticketsToUpdate.add(new TicketToUpdate(ticket, (byte)1, aTrelloCardId));
    }
    
    public static final void setTicketTrelloFeedbackCardId(final Ticket ticket, final String aTrelloCardId) {
        Tickets.ticketsToUpdate.add(new TicketToUpdate(ticket, (byte)2, aTrelloCardId));
    }
    
    public static final void setTicketIsDirty(final Ticket ticket, final boolean isDirty) {
        Tickets.ticketsToUpdate.add(new TicketToUpdate(ticket, (byte)3, isDirty));
    }
    
    public static final void handleTicketsToSend() {
        for (TicketToSend ticketToSend = Tickets.ticketsToSend.pollFirst(); ticketToSend != null; ticketToSend = Tickets.ticketsToSend.pollFirst()) {
            ticketToSend.send();
        }
        for (TicketToUpdate ticketToUpdate = Tickets.ticketsToUpdate.pollFirst(); ticketToUpdate != null; ticketToUpdate = Tickets.ticketsToUpdate.pollFirst()) {
            ticketToUpdate.update();
        }
    }
    
    public static final void handleArchiveTickets() {
        for (final Ticket ticket : Tickets.tickets.values()) {
            if (ticket.getArchiveState() == 0 && ticket.isClosed() && ticket.getClosedDate() < System.currentTimeMillis() - 604800000L) {
                ticket.setArchiveState((byte)1);
                addTicketToSend(ticket);
            }
        }
    }
    
    public static final boolean checkForFlooding(final long playerId) {
        int count = 0;
        for (final Ticket ticket : Tickets.tickets.values()) {
            if (ticket.getPlayerId() == playerId && ticket.isOpen() && ticket.getTicketDate() > System.currentTimeMillis() - 10800000L) {
                ++count;
            }
        }
        return count >= 3;
    }
    
    public static final void sendRequiresAckMessage(final Player player) {
        for (final Ticket ticket : Tickets.tickets.values()) {
            if (ticket.getPlayerId() == player.getWurmId() && ticket.isWaitingAcknowledgement()) {
                final String msg = "Your ticket " + ticket.getTicketName() + " has been ";
                if (ticket.getStateCode() == 2) {
                    player.getCommunicator().sendSafeServerMessage(msg + "resolved.");
                }
                else {
                    player.getCommunicator().sendSafeServerMessage(msg + "updated.");
                }
            }
        }
    }
    
    static {
        Tickets.logger = Logger.getLogger(Tickets.class.getName());
        tickets = new ConcurrentHashMap<Integer, Ticket>();
        ticketsToSend = new ConcurrentLinkedDeque<TicketToSend>();
        ticketsToUpdate = new ConcurrentLinkedDeque<TicketToUpdate>();
        Tickets.nextTicketId = 0;
        Tickets.lastTicketId = 0;
        Tickets.nextBatch = 0;
    }
}
