// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.support;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.webinterface.WcTicket;
import com.wurmonline.shared.constants.TicketGroup;
import com.wurmonline.server.players.Player;
import java.util.Iterator;
import com.wurmonline.server.Players;
import com.wurmonline.server.Servers;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.Map;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.shared.constants.ProtoConstants;
import com.wurmonline.server.MiscConstants;

public final class Ticket implements MiscConstants, ProtoConstants, TimeConstants
{
    private final Map<Long, TicketAction> ticketActions;
    private static final String ADDTICKET = "INSERT INTO TICKETS (TICKETID,TICKETDATE,PLAYERWURMID,PLAYERNAME,CATEGORYCODE,SERVERID,ISGLOBAL,CLOSEDDATE,STATECODE,LEVELCODE,RESPONDERNAME,DESCRIPTION,ISDIRTY,REFFEEDBACK,TRELLOCARDID,TRELLOLISTCODE,HASDESCRIPTIONCHANGED,HASSUMMARYCHANGED,HASTRELLOLISTCHANGED,ACKNOWLEDGED) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String UPDATETICKET = "UPDATE TICKETS SET ISGLOBAL=?,CLOSEDDATE=?,STATECODE=?,LEVELCODE=?,RESPONDERNAME=?,ISDIRTY=?,REFFEEDBACK=?,TRELLOLISTCODE=?,HASDESCRIPTIONCHANGED=?,HASSUMMARYCHANGED=?,HASTRELLOLISTCHANGED=?,ACKNOWLEDGED=? WHERE TICKETID=?";
    private static final String UPDATETICKETDESCRIPTION = "UPDATE TICKETS SET DESCRIPTION=?,ISDIRTY=?,HASDESCRIPTIONCHANGED=? WHERE TICKETID=?";
    private static final String UPDATEISDIRTY = "UPDATE TICKETS SET ISDIRTY=?,TRELLOLISTCODE=?,HASDESCRIPTIONCHANGED=?,HASSUMMARYCHANGED=?,HASTRELLOLISTCHANGED=?,ACKNOWLEDGED=? WHERE TICKETID=?";
    private static final String UPDATETRELLOCARDID = "UPDATE TICKETS SET TRELLOCARDID=?,ISDIRTY=?,TRELLOLISTCODE=?,HASDESCRIPTIONCHANGED=?,HASSUMMARYCHANGED=?,HASTRELLOLISTCHANGED=? WHERE TICKETID=?";
    private static final String UPDATETRELLOFEEDBACKCARDID = "UPDATE TICKETS SET TRELLOFEEDBACKCARDID=?,ISDIRTY=? WHERE TICKETID=?";
    private static final String UPDATEARCHIVESTATE = "UPDATE TICKETS SET ARCHIVESTATECODE=? WHERE TICKETID=?";
    private static final String UPDATEACKNOWLEDGED = "UPDATE TICKETS SET ACKNOWLEDGED=? WHERE TICKETID=?";
    private static final String DELETETICKETACTIONS = "DELETE FROM TICKETACTIONS WHERE TICKETID=?";
    private static final String DELETETICKET = "DELETE FROM TICKETS WHERE TICKETID=?";
    private static final Logger logger;
    public static final byte STATE_NEW = 0;
    public static final byte STATE_ONHOLD = 1;
    public static final byte STATE_RESOLVED = 2;
    public static final byte STATE_RESPONDED = 3;
    public static final byte STATE_CANCELLED = 4;
    public static final byte STATE_WATCHING = 5;
    public static final byte STATE_TAKEN = 6;
    public static final byte STATE_FORWARDED = 7;
    public static final byte STATE_REOPENED = 8;
    public static final byte LEVEL_NONE = 0;
    public static final byte LEVEL_CM = 1;
    public static final byte LEVEL_GM = 2;
    public static final byte LEVEL_ARCH = 3;
    public static final byte LEVEL_DEV = 4;
    public static final byte ARCHIVE_NOT_YET = 0;
    public static final byte ARCHIVE_TELL_PLAYERS = 1;
    public static final byte ARCHIVE_UPDATE_TRELLO = 2;
    public static final byte ARCHIVE_REMOVE_FROM_DB = 3;
    private final int ticketId;
    private final long ticketDate;
    private final long playerWurmId;
    private final String playerName;
    private final byte categoryCode;
    private final int serverId;
    private boolean global;
    private long closedDate;
    private byte stateCode;
    private byte levelCode;
    private String responderName;
    private String description;
    private boolean dirty;
    private short refFeedback;
    private String trelloCardId;
    private String trelloFeedbackCardId;
    private byte trelloListCode;
    private boolean descriptionChanged;
    private boolean summaryChanged;
    private boolean trelloListChanged;
    private byte lastListCode;
    private String lastDescription;
    private String lastSummary;
    private byte archiveState;
    private boolean acknowledged;
    
    public Ticket(final int aTicketId, final long aTicketDate, final long aPlayerWurmId, final String aPlayerName, final byte aCategoryCode, final int aServerId, final boolean aGlobal, final long aClosedDate, final byte aStateCode, final byte aLevelCode, final String aResponderName, final String aDescription, final boolean isDirty, final short aRefFeedback, final String aTrelloFeedbackCardId, final String aTrelloCardId, final byte currentTrelloListCode, final boolean hasDescriptionChanged, final boolean hasSummaryChanged, final boolean hasListChanged, final byte theArchiveState, final boolean isAcknowledged) {
        this.ticketActions = new HashMap<Long, TicketAction>();
        this.global = true;
        this.closedDate = 0L;
        this.stateCode = 0;
        this.levelCode = 0;
        this.responderName = "";
        this.description = "";
        this.dirty = true;
        this.refFeedback = 0;
        this.trelloCardId = "";
        this.trelloFeedbackCardId = "";
        this.trelloListCode = 0;
        this.descriptionChanged = true;
        this.summaryChanged = true;
        this.trelloListChanged = true;
        this.lastListCode = 0;
        this.lastDescription = "";
        this.lastSummary = "";
        this.archiveState = 0;
        this.acknowledged = true;
        this.ticketId = aTicketId;
        this.ticketDate = aTicketDate;
        this.playerWurmId = aPlayerWurmId;
        this.playerName = aPlayerName;
        this.categoryCode = aCategoryCode;
        this.serverId = aServerId;
        this.global = true;
        this.closedDate = aClosedDate;
        this.stateCode = aStateCode;
        this.levelCode = aLevelCode;
        this.responderName = aResponderName;
        this.description = ((aDescription.length() < 10240) ? aDescription : aDescription.substring(0, 10240));
        this.dirty = isDirty;
        this.refFeedback = aRefFeedback;
        this.trelloFeedbackCardId = aTrelloFeedbackCardId;
        this.trelloCardId = aTrelloCardId;
        this.trelloListCode = currentTrelloListCode;
        this.descriptionChanged = hasDescriptionChanged;
        this.summaryChanged = hasSummaryChanged;
        this.trelloListChanged = hasListChanged;
        this.archiveState = theArchiveState;
        this.acknowledged = isAcknowledged;
        if (!hasDescriptionChanged) {
            this.lastDescription = this.description;
        }
        if (!hasSummaryChanged) {
            this.lastSummary = this.getTrelloName();
        }
        if (!hasListChanged) {
            this.lastListCode = this.trelloListCode;
        }
    }
    
    public Ticket(final int aTicketId, final long aTicketDate, final long aPlayerWurmId, final String aPlayerName, final byte aCategoryCode, final int aServerId, final boolean aGlobal, final long aClosedDate, final byte aStateCode, final byte aLevelCode, final String aResponderName, final String aDescription, final boolean isDirty, final short aRefFeedback, final boolean aAcknowledge) {
        this(aTicketId, aTicketDate, aPlayerWurmId, aPlayerName, aCategoryCode, aServerId, true, aClosedDate, aStateCode, aLevelCode, aResponderName, aDescription, isDirty, aRefFeedback, "", "", (byte)0, false, false, false, (byte)0, aAcknowledge);
    }
    
    public Ticket(final long aPlayerWurmId, final String aPlayerName, final byte aCategoryCode, final String aDescription) {
        this(Tickets.getNextTicketNo(), System.currentTimeMillis(), aPlayerWurmId, aPlayerName, aCategoryCode, Servers.getLocalServerId(), true, 0L, (byte)0, (byte)1, "", aDescription.replace('\"', '\''), true, (short)0, "", "", (byte)0, false, false, false, (byte)0, true);
        this.dbAddTicket();
        Players.getInstance().sendTicket(this);
        this.sendTicketGlobal();
    }
    
    public int getTicketId() {
        return this.ticketId;
    }
    
    public String getTicketName() {
        return "#" + this.ticketId;
    }
    
    public long getWurmId() {
        return Tickets.calcWurmId(this.ticketId);
    }
    
    public long getTicketDate() {
        return this.ticketDate;
    }
    
    public String getDateAsString() {
        return Tickets.convertTime(this.ticketDate);
    }
    
    public boolean isOpen() {
        return this.closedDate == 0L;
    }
    
    public boolean isClosed() {
        return this.closedDate != 0L;
    }
    
    public long getClosedDate() {
        return this.closedDate;
    }
    
    public byte getStateCode() {
        return this.stateCode;
    }
    
    public void setStateCode(final byte aStateCode) {
        this.stateCode = aStateCode;
    }
    
    public byte getCategoryCode() {
        return this.categoryCode;
    }
    
    public int getServerId() {
        return this.serverId;
    }
    
    public boolean isGlobal() {
        return this.global;
    }
    
    public boolean isWaitingAcknowledgement() {
        return !this.acknowledged;
    }
    
    public boolean getAcknowledged() {
        return this.acknowledged;
    }
    
    public boolean isDirty() {
        return this.dirty;
    }
    
    public void setDirty(final boolean isDirty) {
        if (isDirty) {
            this.dirty = isDirty;
            this.descriptionChanged = !this.lastDescription.equals(this.description);
            this.summaryChanged = !this.lastSummary.equals(this.getTrelloName());
            this.trelloListChanged = (this.lastListCode != this.getTrelloListCode());
        }
        else {
            this.dirty = isDirty;
            this.lastDescription = this.description;
            this.lastSummary = this.getTrelloName();
            this.lastListCode = this.getTrelloListCode();
        }
    }
    
    public void setTrelloCardId(final String aTrelloCardId) {
        this.trelloCardId = aTrelloCardId;
        this.setDirty(false);
        this.dbUpdateTrelloCardId();
    }
    
    public void setTrelloFeedbackCardId(final String aTrelloCardId) {
        this.trelloFeedbackCardId = aTrelloCardId;
        this.setDirty(false);
        this.dbUpdateTrelloFeedbackCardId();
    }
    
    public byte getLevelCode() {
        return this.levelCode;
    }
    
    public void setLevelCode(final byte aLevelCode) {
        this.levelCode = aLevelCode;
    }
    
    public String getResponderName() {
        return this.responderName;
    }
    
    public boolean hasFeedback() {
        return this.refFeedback != 0;
    }
    
    public short getRefFeedback() {
        return this.refFeedback;
    }
    
    public String getTrelloCardId() {
        return this.trelloCardId;
    }
    
    public String getTrelloFeedbackCardId() {
        return this.trelloFeedbackCardId;
    }
    
    public byte getArchiveState() {
        return this.archiveState;
    }
    
    public void setArchiveState(final byte newArchiveState) {
        if (!Servers.isThisLoginServer() && newArchiveState == 2) {
            this.dbDelete();
            Tickets.removeTicket(this.ticketId);
        }
        else if (newArchiveState == 3) {
            this.dbDelete();
            Tickets.removeTicket(this.ticketId);
        }
        else {
            this.archiveState = newArchiveState;
            this.dbUpdateArchiveState();
        }
    }
    
    public boolean hasTicketChangedSince(final long aDate) {
        for (final TicketAction ta : this.ticketActions.values()) {
            if (ta.isActionAfter(aDate)) {
                return true;
            }
        }
        return false;
    }
    
    public void update(final boolean aGlobal, final long aClosedDate, final byte aStateCode, final byte aLevelCode, final String aResponderName, final String aDescription, final short aRefFeedback, final boolean isAcknowledged) {
        this.global = true;
        this.closedDate = aClosedDate;
        this.stateCode = aStateCode;
        this.levelCode = aLevelCode;
        this.responderName = aResponderName;
        this.description = aDescription;
        this.refFeedback = aRefFeedback;
        this.acknowledged = isAcknowledged;
        this.setDirty(true);
        this.dbUpdateTicket();
    }
    
    public void save() {
        this.dbAddTicket();
    }
    
    public void addTicketAction(final TicketAction newTicketAction) {
        if (newTicketAction.getAction() == 14) {
            this.refFeedback = newTicketAction.getActionNo();
        }
        this.ticketActions.put((long)newTicketAction.getActionNo(), newTicketAction);
    }
    
    public TicketAction addTicketAction(final short aActionNo, final byte aAction, final long aDate, final String aByWhom, final String aNote, final byte aVisibilityLevel, final byte aQualityOfServiceCode, final byte aCourteousCode, final byte aKnowledgeableCode, final byte aGeneralFlags, final byte aQualitiesFlags, final byte aIrkedFlags) {
        if (!this.ticketActions.containsKey((long)aActionNo)) {
            final TicketAction ta = new TicketAction(this.ticketId, aActionNo, aAction, aDate, aByWhom, aNote, aVisibilityLevel, true, aQualityOfServiceCode, aCourteousCode, aKnowledgeableCode, aGeneralFlags, aQualitiesFlags, aIrkedFlags, "");
            ta.dbSave();
            this.addTicketAction(ta);
            Tickets.addTicketToSend(this, ta);
            return ta;
        }
        return this.ticketActions.get((long)aActionNo);
    }
    
    public void autoForwardToGM() {
        if (this.stateCode == 0 && this.serverId == Servers.getLocalServerId() && this.ticketDate < System.currentTimeMillis() - 900000L) {
            this.addNewTicketAction((byte)6, "Auto", "Auto forward to GMs", (byte)1);
        }
    }
    
    public void addNewTicketAction(final byte action, final String byWhom, final String note, final byte visibilityLevel) {
        this.addNewTicketAction(action, byWhom, note, visibilityLevel, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0);
    }
    
    public void addNewTicketAction(final byte action, final String byWhom, final String note, final byte visibilityLevel, final byte aQualityOfServiceCode, final byte aCourteousCode, final byte aKnowledgeableCode, final byte aGeneralFlags, final byte aQualitiesFlags, final byte aIrkedFlags) {
        final TicketAction ta = new TicketAction(this.ticketId, (short)(this.ticketActions.size() + 1), action, byWhom, note, visibilityLevel, aQualityOfServiceCode, aCourteousCode, aKnowledgeableCode, aGeneralFlags, aQualitiesFlags, aIrkedFlags);
        ta.dbSave();
        this.addTicketAction(ta);
        final boolean oldGlobal = this.global;
        this.global = true;
        this.setAckFor(action, visibilityLevel);
        switch (action) {
            case 1: {
                this.stateCode = 4;
                this.closedDate = System.currentTimeMillis();
                this.responderName = "";
                break;
            }
            case 13: {
                this.stateCode = 7;
                this.levelCode = 1;
                this.responderName = "";
                break;
            }
            case 6: {
                this.stateCode = 7;
                this.levelCode = 2;
                this.responderName = "";
                break;
            }
            case 7: {
                this.stateCode = 7;
                this.levelCode = 3;
                this.responderName = "";
                break;
            }
            case 8: {
                this.stateCode = 7;
                this.levelCode = 4;
                this.responderName = "";
                break;
            }
            case 10: {
                this.stateCode = 1;
                this.responderName = "";
                break;
            }
            case 3: {
                if (this.levelCode != 3 && this.levelCode != 4) {
                    this.levelCode = 2;
                }
                this.stateCode = 3;
                this.responderName = byWhom;
                break;
            }
            case 2: {
                this.stateCode = 3;
                this.responderName = byWhom;
                break;
            }
            case 11: {
                if (this.levelCode != 3 && this.levelCode != 4) {
                    this.levelCode = 2;
                }
                this.stateCode = 6;
                this.responderName = byWhom;
                break;
            }
            case 9: {
                this.stateCode = 2;
                this.closedDate = System.currentTimeMillis();
                this.responderName = byWhom;
                break;
            }
            case 15: {
                if (this.categoryCode == 11) {
                    this.stateCode = 5;
                }
                else {
                    this.stateCode = 8;
                }
                this.responderName = byWhom;
                this.closedDate = 0L;
                break;
            }
            case 14: {
                this.refFeedback = ta.getActionNo();
            }
        }
        this.setDirty(true);
        this.dbUpdateTicket();
        Tickets.addTicketToSend(this, ta);
        if (!oldGlobal) {
            this.sendTicketGlobal();
        }
        else {
            this.sendTicketGlobal(ta);
        }
    }
    
    public void addSurvey(final byte aQualityOfServiceCode, final byte aCourteousCode, final byte aKnowledgeableCode, final byte aGeneralFlags, final byte aQualitiesFlags, final byte aIrkedFlags) {
        this.setDirty(true);
        this.dbUpdateTicket();
    }
    
    public String getShortDescription() {
        if (this.description.length() <= 400 || this.getStateCode() != 5) {
            return this.getDescription();
        }
        if (this.getTrelloCardId().length() == 0) {
            return "Possible Client Hack: Soon in a trello near you!";
        }
        return "Possible Client Hack: see trello https://trello.com/c/" + this.getTrelloCardId();
    }
    
    public String getDescription() {
        return this.description.replace('\"', '\'');
    }
    
    public void appendDescription(final String aDescription) {
        if (this.description.length() == 0) {
            this.description = aDescription.replace('\"', '\'');
        }
        else {
            this.description = this.description + "\n" + aDescription;
            if (this.description.length() > 400) {
                this.description = this.description.substring(0, 399).replace('\"', '\'');
            }
        }
        this.setDirty(true);
        this.dbUpdateTicketDescription();
    }
    
    public byte getTicketGroup(final Player player) {
        if (player.mayHearDevTalk()) {
            if (!this.isOpen()) {
                return TicketGroup.CLOSED.getId();
            }
            if (this.categoryCode == 11) {
                return TicketGroup.WATCH.getId();
            }
            if (this.categoryCode == 4) {
                return TicketGroup.FORUM.getId();
            }
            return TicketGroup.OPEN.getId();
        }
        else {
            if (!player.mayHearMgmtTalk()) {
                return TicketGroup.NONE.getId();
            }
            if (!this.isOpen()) {
                return TicketGroup.CLOSED.getId();
            }
            return TicketGroup.OPEN.getId();
        }
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public long getPlayerId() {
        return this.playerWurmId;
    }
    
    public void sendTicketGlobal() {
        final WcTicket wct = new WcTicket(this);
        if (Servers.isThisLoginServer()) {
            wct.sendFromLoginServer();
        }
        else {
            wct.sendToLoginServer();
        }
    }
    
    private void sendTicketGlobal(final TicketAction ticketAction) {
        final WcTicket wct = new WcTicket(this, ticketAction);
        if (Servers.isThisLoginServer()) {
            wct.sendFromLoginServer();
        }
        else {
            wct.sendToLoginServer();
        }
    }
    
    public byte getColourCode(final Player player) {
        if (player.mayHearDevTalk() || player.mayHearMgmtTalk()) {
            if (!this.isOpen()) {
                return 0;
            }
            if (!PlayerInfoFactory.isPlayerOnline(this.playerWurmId)) {
                return 14;
            }
            switch (this.levelCode) {
                case 2: {
                    return 11;
                }
                case 3: {
                    return 8;
                }
                case 4: {
                    return 2;
                }
                default: {
                    return 3;
                }
            }
        }
        else {
            if (this.isOpen()) {
                return 3;
            }
            return 0;
        }
    }
    
    private String getLastAction() {
        String lastAction = decodeState(this.stateCode);
        if (this.stateCode != 2 && this.stateCode != 4 && this.stateCode != 0) {
            if (lastAction.length() > 0) {
                lastAction += " ";
            }
            lastAction += decodeLevel(this.levelCode);
        }
        return lastAction;
    }
    
    public boolean hasSummaryChanged() {
        return this.summaryChanged;
    }
    
    public boolean hasDescriptionChanged() {
        return this.descriptionChanged;
    }
    
    public boolean hasListChanged() {
        return this.trelloListChanged;
    }
    
    public String getTrelloName() {
        String lastAction = this.getLastAction();
        if (this.hasFeedback()) {
            lastAction += "*";
        }
        return "#" + this.ticketId + " " + Tickets.convertTime(this.ticketDate) + " " + this.playerName + " : " + abbreviateCategory(this.categoryCode) + " (" + this.getServerAbbreviation() + ") " + lastAction + " " + this.responderName;
    }
    
    public String getTrelloFeedbackTitle() {
        if (this.hasFeedback()) {
            final TicketAction ta = this.getFeedback();
            return "#" + this.ticketId + " " + Tickets.convertTime(this.closedDate) + " S:" + ta.getQualityOfServiceCode() + " C:" + ta.getCourteousCode() + " K:" + ta.getKnowledgeableCode() + " G:" + ta.getGeneralFlagString() + " Q:" + ta.getQualitiesFlagsString() + " I:" + ta.getIrkedFlagsString();
        }
        return "";
    }
    
    public String getFeedbackText() {
        if (this.hasFeedback()) {
            return this.getFeedback().getNote();
        }
        return "";
    }
    
    private String getServerAbbreviation() {
        final ServerEntry serverEntry = Servers.getServerWithId(this.serverId);
        if (serverEntry == null) {
            final String s = "unknown" + String.valueOf(this.serverId);
            return s.substring(s.length() - 3);
        }
        return serverEntry.getAbbreviation();
    }
    
    public String getTicketSummary(final Player player) {
        if (player.mayHearDevTalk()) {
            return this.getTrelloName();
        }
        if (player.mayHearMgmtTalk()) {
            return "#" + this.ticketId + " " + Tickets.convertTime(this.ticketDate) + " " + this.playerName + " : " + abbreviateCategory(this.categoryCode) + ((this.serverId == Servers.getLocalServerId()) ? " " : (" (" + this.getServerAbbreviation() + ")")) + " " + this.getLastAction() + ((this.levelCode <= 1 || player.mayMute()) ? (" " + this.responderName) : "");
        }
        return "#" + this.ticketId + " " + Tickets.convertTime(this.ticketDate) + " : " + this.getLastAction();
    }
    
    public byte getTrelloListCode() {
        if (this.getStateCode() == 4 || this.getStateCode() == 2) {
            return 3;
        }
        if (this.categoryCode == 11) {
            return 4;
        }
        if (this.getLevelCode() == 3 || this.getLevelCode() == 4) {
            return 2;
        }
        return 1;
    }
    
    public TicketAction getFeedback() {
        if (this.hasFeedback()) {
            return this.ticketActions.get((long)this.refFeedback);
        }
        return null;
    }
    
    public TicketAction[] getTicketActions(final Player player) {
        final Map<Long, TicketAction> playerTicketAction = new HashMap<Long, TicketAction>();
        for (final Map.Entry<Long, TicketAction> entry : this.ticketActions.entrySet()) {
            if (entry.getValue().isActionShownTo(player)) {
                playerTicketAction.put(entry.getKey(), entry.getValue());
            }
        }
        return playerTicketAction.values().toArray(new TicketAction[playerTicketAction.size()]);
    }
    
    public TicketAction[] getDirtyTicketActions() {
        final Map<Long, TicketAction> dirtyTicketAction = new HashMap<Long, TicketAction>();
        for (final Map.Entry<Long, TicketAction> entry : this.ticketActions.entrySet()) {
            if (entry.getValue().isDirty()) {
                dirtyTicketAction.put(entry.getKey(), entry.getValue());
            }
        }
        return dirtyTicketAction.values().toArray(new TicketAction[dirtyTicketAction.size()]);
    }
    
    public TicketAction[] getTicketActions() {
        return this.ticketActions.values().toArray(new TicketAction[this.ticketActions.size()]);
    }
    
    public long getLatestActionDate() {
        long newestAction = 0L;
        for (final Map.Entry<Long, TicketAction> entry : this.ticketActions.entrySet()) {
            if (entry.getValue().getDate() > newestAction) {
                newestAction = entry.getValue().getDate();
            }
        }
        return newestAction;
    }
    
    public boolean isTicketShownTo(final Player player) {
        return this.archiveState == 0 && ((player.mayHearDevTalk() && (Servers.getLocalServerId() == this.serverId || this.levelCode >= 2)) || (player.mayHearMgmtTalk() && this.categoryCode != 11) || (this.categoryCode != 11 && player.getWurmId() == this.playerWurmId));
    }
    
    public void acknowledgeTicketUpdate(final Player player) {
        if (this.categoryCode != 11 && player.getWurmId() == this.playerWurmId && !this.acknowledged) {
            this.acknowledged = true;
            this.dbUpdateAchnowledged();
            this.sendTicketGlobal();
        }
    }
    
    public static String decodeState(final byte aState) {
        switch (aState) {
            case 0: {
                return "New";
            }
            case 1: {
                return "OnHold";
            }
            case 8: {
                return "ReOpened";
            }
            case 2: {
                return "Resolved";
            }
            case 4: {
                return "Cancelled";
            }
            case 7: {
                return "Fwd";
            }
            default: {
                return "";
            }
        }
    }
    
    public static String abbreviateCategory(final byte aCategory) {
        switch (aCategory) {
            case 3: {
                return "Bug";
            }
            case 8: {
                return "Paymt";
            }
            case 7: {
                return "Pwd";
            }
            case 1: {
                return "Acct";
            }
            case 5: {
                return "Grief";
            }
            case 9: {
                return "Stuck";
            }
            case 2: {
                return "Boat";
            }
            case 6: {
                return "Horse";
            }
            case 11: {
                return "Watch";
            }
            case 4: {
                return "Forum";
            }
            case 10: {
                return "Other";
            }
            default: {
                return "";
            }
        }
    }
    
    public static String decodeLevel(final byte aLevel) {
        switch (aLevel) {
            case 1: {
                return "CM";
            }
            case 2: {
                return "GM";
            }
            case 3: {
                return "Arch";
            }
            case 4: {
                return "Admin";
            }
            default: {
                return "";
            }
        }
    }
    
    private void setAckFor(final byte action, final byte noteVisibility) {
        if (this.categoryCode == 11) {
            return;
        }
        if (action != 1 && action != 14 && noteVisibility == 0) {
            this.acknowledged = false;
        }
        if (action == 9 || action == 15) {
            this.acknowledged = false;
        }
    }
    
    private void dbAddTicket() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("INSERT INTO TICKETS (TICKETID,TICKETDATE,PLAYERWURMID,PLAYERNAME,CATEGORYCODE,SERVERID,ISGLOBAL,CLOSEDDATE,STATECODE,LEVELCODE,RESPONDERNAME,DESCRIPTION,ISDIRTY,REFFEEDBACK,TRELLOCARDID,TRELLOLISTCODE,HASDESCRIPTIONCHANGED,HASSUMMARYCHANGED,HASTRELLOLISTCHANGED,ACKNOWLEDGED) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, this.ticketId);
            ps.setLong(2, this.ticketDate);
            ps.setLong(3, this.playerWurmId);
            ps.setString(4, this.playerName);
            ps.setByte(5, this.categoryCode);
            ps.setInt(6, this.serverId);
            ps.setBoolean(7, this.global);
            ps.setLong(8, this.closedDate);
            ps.setByte(9, this.stateCode);
            ps.setByte(10, this.levelCode);
            ps.setString(11, this.responderName);
            ps.setString(12, this.description.substring(0, Math.min(this.description.length(), 398)));
            ps.setBoolean(13, this.dirty);
            ps.setShort(14, this.refFeedback);
            ps.setString(15, this.trelloCardId);
            ps.setByte(16, this.trelloListCode);
            ps.setBoolean(17, this.descriptionChanged);
            ps.setBoolean(18, this.summaryChanged);
            ps.setBoolean(19, this.trelloListChanged);
            ps.setBoolean(20, this.acknowledged);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Ticket.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void dbUpdateTicket() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE TICKETS SET ISGLOBAL=?,CLOSEDDATE=?,STATECODE=?,LEVELCODE=?,RESPONDERNAME=?,ISDIRTY=?,REFFEEDBACK=?,TRELLOLISTCODE=?,HASDESCRIPTIONCHANGED=?,HASSUMMARYCHANGED=?,HASTRELLOLISTCHANGED=?,ACKNOWLEDGED=? WHERE TICKETID=?");
            ps.setBoolean(1, this.global);
            ps.setLong(2, this.closedDate);
            ps.setByte(3, this.stateCode);
            ps.setByte(4, this.levelCode);
            ps.setString(5, this.responderName);
            ps.setBoolean(6, this.dirty);
            ps.setShort(7, this.refFeedback);
            ps.setByte(8, this.trelloListCode);
            ps.setBoolean(9, this.descriptionChanged);
            ps.setBoolean(10, this.summaryChanged);
            ps.setBoolean(11, this.trelloListChanged);
            ps.setBoolean(12, this.acknowledged);
            ps.setInt(13, this.ticketId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Ticket.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void dbUpdateTicketDescription() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE TICKETS SET DESCRIPTION=?,ISDIRTY=?,HASDESCRIPTIONCHANGED=? WHERE TICKETID=?");
            ps.setString(1, this.description);
            ps.setBoolean(2, this.dirty);
            ps.setBoolean(3, this.descriptionChanged);
            ps.setInt(4, this.ticketId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Ticket.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void dbUpdateIsDirty() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE TICKETS SET ISDIRTY=?,TRELLOLISTCODE=?,HASDESCRIPTIONCHANGED=?,HASSUMMARYCHANGED=?,HASTRELLOLISTCHANGED=?,ACKNOWLEDGED=? WHERE TICKETID=?");
            ps.setBoolean(1, this.dirty);
            ps.setByte(2, this.trelloListCode);
            ps.setBoolean(3, this.descriptionChanged);
            ps.setBoolean(4, this.summaryChanged);
            ps.setBoolean(5, this.trelloListChanged);
            ps.setBoolean(6, this.acknowledged);
            ps.setInt(7, this.ticketId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Ticket.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void dbUpdateTrelloCardId() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE TICKETS SET TRELLOCARDID=?,ISDIRTY=?,TRELLOLISTCODE=?,HASDESCRIPTIONCHANGED=?,HASSUMMARYCHANGED=?,HASTRELLOLISTCHANGED=? WHERE TICKETID=?");
            ps.setString(1, this.trelloCardId);
            ps.setBoolean(2, this.dirty);
            ps.setByte(3, this.trelloListCode);
            ps.setBoolean(4, this.descriptionChanged);
            ps.setBoolean(5, this.summaryChanged);
            ps.setBoolean(6, this.trelloListChanged);
            ps.setInt(7, this.ticketId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Ticket.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void dbUpdateTrelloFeedbackCardId() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE TICKETS SET TRELLOFEEDBACKCARDID=?,ISDIRTY=? WHERE TICKETID=?");
            ps.setString(1, this.trelloFeedbackCardId);
            ps.setBoolean(2, this.dirty);
            ps.setInt(3, this.ticketId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Ticket.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void dbUpdateArchiveState() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE TICKETS SET ARCHIVESTATECODE=? WHERE TICKETID=?");
            ps.setByte(1, this.archiveState);
            ps.setInt(2, this.ticketId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Ticket.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void dbUpdateAchnowledged() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE TICKETS SET ACKNOWLEDGED=? WHERE TICKETID=?");
            ps.setBoolean(1, this.acknowledged);
            ps.setInt(2, this.ticketId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Ticket.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void dbDelete() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("DELETE FROM TICKETACTIONS WHERE TICKETID=?");
            ps.setInt(1, this.ticketId);
            ps.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps, null);
            ps = dbcon.prepareStatement("DELETE FROM TICKETS WHERE TICKETID=?");
            ps.setInt(1, this.ticketId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            Ticket.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public String toString() {
        return "Ticket [ticketId=" + this.ticketId + ", player=" + this.playerName + ", category=" + this.categoryCode + ", state=" + this.stateCode + ", level=" + this.levelCode + ", description=" + this.description + "]";
    }
    
    static {
        logger = Logger.getLogger(Ticket.class.getName());
    }
}
