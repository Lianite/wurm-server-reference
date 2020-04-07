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
import javax.annotation.Nullable;
import com.wurmonline.server.players.Player;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public final class TicketAction implements MiscConstants
{
    private static final Logger logger;
    private static final String ADDTICKETACTION = "INSERT INTO TICKETACTIONS (TICKETID,ACTIONNO,ACTIONDATE,ACTIONTYPE,BYWHOM,NOTE,VISIBILITYLEVEL,ISDIRTY,QUALITYOFSERVICECODE,COURTEOUSCODE,KNOWLEDGEABLECODE,GENERALFLAGS,QUALITIESFLAGS,IRKEDFLAGS,TRELLOCOMMENTID) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String UPDATETICKETACTIONISDIRTY = "UPDATE TICKETACTIONS SET ISDIRTY=? WHERE TICKETID=? AND ACTIONNO=?";
    private static final String UPDATETRELLOCOMMENTID = "UPDATE TICKETACTIONS SET TRELLOCOMMENTID=?,ISDIRTY=? WHERE TICKETID=? AND ACTIONNO=?";
    private final int ticketId;
    private final short actionNo;
    private final long date;
    private final String note;
    private final byte action;
    private final String byWhom;
    private final byte visibilityLevel;
    private boolean dirty;
    private String trelloCommentId;
    private byte qualityOfServiceCode;
    private byte courteousCode;
    private byte knowledgeableCode;
    private byte generalFlags;
    private byte qualitiesFlags;
    private byte irkedFlags;
    
    public TicketAction(final int aTicketId, final short aActionNo, final byte aAction, final long aDate, final String aByWhom, final String aNote, final byte aVisibilityLevel, final boolean isDirty, final byte aQualityOfServiceCode, final byte aCourteousCode, final byte aKnowledgeableCode, final byte aGeneralFlags, final byte aQualitiesFlags, final byte aIrkedFlags, final String aTrelloLink) {
        this.dirty = true;
        this.trelloCommentId = "";
        this.qualityOfServiceCode = 0;
        this.courteousCode = 0;
        this.knowledgeableCode = 0;
        this.generalFlags = 0;
        this.qualitiesFlags = 0;
        this.irkedFlags = 0;
        this.ticketId = aTicketId;
        this.actionNo = aActionNo;
        this.action = aAction;
        this.date = aDate;
        this.byWhom = aByWhom;
        this.note = aNote;
        this.visibilityLevel = aVisibilityLevel;
        this.dirty = isDirty;
        this.trelloCommentId = aTrelloLink;
        this.qualityOfServiceCode = aQualityOfServiceCode;
        this.courteousCode = aCourteousCode;
        this.knowledgeableCode = aKnowledgeableCode;
        this.generalFlags = aGeneralFlags;
        this.qualitiesFlags = aQualitiesFlags;
        this.irkedFlags = aIrkedFlags;
    }
    
    public TicketAction(final int aTicketId, final short aActionNo, final byte aAction, final String aByWhom, final String aNote, final byte aVisibilityLevel, final byte aQualityOfServiceCode, final byte aCourteousCode, final byte aKnowledgeableCode, final byte aGeneralFlags, final byte aQualitiesFlags, final byte aIrkedFlags) {
        this.dirty = true;
        this.trelloCommentId = "";
        this.qualityOfServiceCode = 0;
        this.courteousCode = 0;
        this.knowledgeableCode = 0;
        this.generalFlags = 0;
        this.qualitiesFlags = 0;
        this.irkedFlags = 0;
        this.ticketId = aTicketId;
        this.actionNo = aActionNo;
        this.action = aAction;
        this.date = System.currentTimeMillis();
        this.byWhom = aByWhom;
        this.note = aNote;
        this.visibilityLevel = aVisibilityLevel;
        this.dirty = true;
        this.trelloCommentId = "";
        this.qualityOfServiceCode = aQualityOfServiceCode;
        this.courteousCode = aCourteousCode;
        this.knowledgeableCode = aKnowledgeableCode;
        this.generalFlags = aGeneralFlags;
        this.qualitiesFlags = aQualitiesFlags;
        this.irkedFlags = aIrkedFlags;
    }
    
    public String getNote() {
        return this.note;
    }
    
    public String getNote(final Player player) {
        if (this.canSeeNote(player)) {
            return this.note;
        }
        return "";
    }
    
    public String getNotePlus(final Player player) {
        if (this.canSeeNote(player)) {
            return this.getVisPlusNote(player);
        }
        return "";
    }
    
    public boolean canSeeNote(final Player player) {
        return player.mayHearDevTalk() || (player.mayHearMgmtTalk() && this.visibilityLevel == 1) || this.visibilityLevel == 0;
    }
    
    private String getVisPlusNote(final Player player) {
        if (this.note.length() == 0) {
            return "";
        }
        if (player.mayHearDevTalk()) {
            return this.getVisPlusNote();
        }
        if (player.mayHearMgmtTalk() && this.visibilityLevel != 2) {
            return this.getVisPlusNote();
        }
        return this.note;
    }
    
    private String getVisPlusNote() {
        switch (this.visibilityLevel) {
            case 2: {
                return "(GM+) " + this.note;
            }
            case 1: {
                return "(CM+) " + this.note;
            }
            default: {
                return "(All) " + this.note;
            }
        }
    }
    
    public long getDate() {
        return this.date;
    }
    
    public byte getAction() {
        return this.action;
    }
    
    public short getActionNo() {
        return this.actionNo;
    }
    
    public String getByWhom() {
        return this.byWhom;
    }
    
    public byte getVisibilityLevel() {
        return this.visibilityLevel;
    }
    
    public boolean isDirty() {
        return this.dirty;
    }
    
    public void setDirty(final boolean isDirty) {
        if (this.dirty != isDirty) {
            this.dirty = isDirty;
            this.dbUpdateIsDirty();
        }
    }
    
    public boolean isActionAfter(final long aDate) {
        return this.date > aDate;
    }
    
    public String getTrelloCommentId() {
        return this.trelloCommentId;
    }
    
    public void setTrelloCommentId(final String aTrelloCommentId) {
        this.trelloCommentId = aTrelloCommentId;
        this.dirty = false;
        this.dbUpdateTrelloCommentId();
    }
    
    public byte getQualityOfServiceCode() {
        return this.qualityOfServiceCode;
    }
    
    public byte getCourteousCode() {
        return this.courteousCode;
    }
    
    public byte getKnowledgeableCode() {
        return this.knowledgeableCode;
    }
    
    public byte getGeneralFlags() {
        return this.generalFlags;
    }
    
    public byte getQualitiesFlags() {
        return this.qualitiesFlags;
    }
    
    public byte getIrkedFlags() {
        return this.irkedFlags;
    }
    
    public String getGeneralFlagString() {
        return Integer.toBinaryString(256 + this.generalFlags).substring(2);
    }
    
    public String getQualitiesFlagsString() {
        return Integer.toBinaryString(256 + this.qualitiesFlags).substring(3);
    }
    
    public String getIrkedFlagsString() {
        return Integer.toBinaryString(256 + this.irkedFlags).substring(3);
    }
    
    public boolean wasServiceSuperior() {
        return this.qualityOfServiceCode == 1;
    }
    
    public boolean wasServiceGood() {
        return this.qualityOfServiceCode == 2;
    }
    
    public boolean wasServiceAverage() {
        return this.qualityOfServiceCode == 3;
    }
    
    public boolean wasServiceFair() {
        return this.qualityOfServiceCode == 4;
    }
    
    public boolean wasServicePoor() {
        return this.qualityOfServiceCode == 5;
    }
    
    public boolean wasCourteousStronglyAgree() {
        return this.courteousCode == 1;
    }
    
    public boolean wasCourteousSomewhatAgree() {
        return this.courteousCode == 2;
    }
    
    public boolean wasCourteousNeutral() {
        return this.courteousCode == 3;
    }
    
    public boolean wasCourteousSomewhatDisagree() {
        return this.courteousCode == 4;
    }
    
    public boolean wasCourteousStronglyDisagree() {
        return this.courteousCode == 5;
    }
    
    public boolean wasKnowledgeableStronglyAgree() {
        return this.knowledgeableCode == 1;
    }
    
    public boolean wasKnowledgeableSomewhatAgree() {
        return this.knowledgeableCode == 2;
    }
    
    public boolean wasKnowledgeableNeutral() {
        return this.knowledgeableCode == 3;
    }
    
    public boolean wasKnowledgeableSomewhatDisagree() {
        return this.knowledgeableCode == 4;
    }
    
    public boolean wasKnowledgeableStronglyDisagree() {
        return this.knowledgeableCode == 5;
    }
    
    public boolean wasGeneralWrongInfo() {
        return (this.generalFlags & 0x1) != 0x0;
    }
    
    public boolean wasGeneralNoUnderstand() {
        return (this.generalFlags & 0x2) != 0x0;
    }
    
    public boolean wasGeneralUnclear() {
        return (this.generalFlags & 0x4) != 0x0;
    }
    
    public boolean wasGeneralNoSolve() {
        return (this.generalFlags & 0x8) != 0x0;
    }
    
    public boolean wasGeneralDisorganized() {
        return (this.generalFlags & 0x10) != 0x0;
    }
    
    public boolean wasGeneralOther() {
        return (this.generalFlags & 0x20) != 0x0;
    }
    
    public boolean wasGeneralFine() {
        return (this.generalFlags & 0x40) != 0x0;
    }
    
    public boolean wasQualityPatient() {
        return (this.qualitiesFlags & 0x1) != 0x0;
    }
    
    public boolean wasQualityEnthusiastic() {
        return (this.qualitiesFlags & 0x2) != 0x0;
    }
    
    public boolean wasQualityListened() {
        return (this.qualitiesFlags & 0x4) != 0x0;
    }
    
    public boolean wasQualityFriendly() {
        return (this.qualitiesFlags & 0x8) != 0x0;
    }
    
    public boolean wasQualityResponsive() {
        return (this.qualitiesFlags & 0x10) != 0x0;
    }
    
    public boolean wasQualityNothing() {
        return (this.qualitiesFlags & 0x20) != 0x0;
    }
    
    public boolean wasIrkedPatient() {
        return (this.irkedFlags & 0x1) != 0x0;
    }
    
    public boolean wasIrkedEnthusiastic() {
        return (this.irkedFlags & 0x2) != 0x0;
    }
    
    public boolean wasIrkedListened() {
        return (this.irkedFlags & 0x4) != 0x0;
    }
    
    public boolean wasIrkedFriendly() {
        return (this.irkedFlags & 0x8) != 0x0;
    }
    
    public boolean wasIrkedResponsive() {
        return (this.irkedFlags & 0x10) != 0x0;
    }
    
    public boolean wasIrkedNothing() {
        return (this.irkedFlags & 0x20) != 0x0;
    }
    
    public String getTrelloComment() {
        if (this.note.length() == 0) {
            return this.getLine(null);
        }
        return this.getLine(null) + "\n" + this.getVisPlusNote();
    }
    
    public String getLine(final Player player) {
        return Tickets.convertTime(this.date) + " " + this.getActionAsString(player);
    }
    
    private String getActionAsString(@Nullable final Player player) {
        final String by = " (by " + this.byWhom + ")";
        switch (this.action) {
            case 0: {
                return "Note (by " + this.byWhom + ")";
            }
            case 1: {
                return "Cancelled";
            }
            case 2: {
                return "CM " + this.byWhom + " Responded";
            }
            case 3: {
                return "GM " + this.byWhom + " Responded";
            }
            case 4: {
                return "GM " + this.byWhom + " Responded";
            }
            case 5: {
                return "DEV " + this.byWhom + " Responded";
            }
            case 13: {
                if (player == null || player.mayHearMgmtTalk()) {
                    return "Fwd CM" + by;
                }
                return "Fwd CM";
            }
            case 6: {
                if (player == null || player.mayHearDevTalk()) {
                    return "Fwd GM" + by;
                }
                return "Fwd GM";
            }
            case 7: {
                if (player == null || player.mayHearDevTalk()) {
                    return "Fwd Arch" + by;
                }
                return "Fwd Arch";
            }
            case 8: {
                if (player == null || player.mayHearDevTalk()) {
                    return "Fwd Dev" + by;
                }
                return "Fwd Dev";
            }
            case 9: {
                if (player == null || player.mayHearMgmtTalk()) {
                    return "Resolved" + by;
                }
                return "Resolved";
            }
            case 10: {
                if (player == null || player.mayHearMgmtTalk()) {
                    return "OnHold" + by;
                }
                return "OnHold";
            }
            case 15: {
                if (player == null || player.mayHearMgmtTalk()) {
                    return "ReOpened" + by;
                }
                return "ReOpened";
            }
            case 11: {
                if (player == null || player.mayHearDevTalk()) {
                    return "GM" + by;
                }
                return "GM";
            }
            case 14: {
                return "Feedback";
            }
            default: {
                return "";
            }
        }
    }
    
    public boolean isActionShownTo(final Player player) {
        if (player.mayHearDevTalk()) {
            return true;
        }
        if (player.mayHearMgmtTalk()) {
            return this.action != 0 || this.visibilityLevel == 1 || this.visibilityLevel == 0;
        }
        return this.action != 0 || this.visibilityLevel == 0;
    }
    
    public void dbSave() {
        this.dbAddTicketAction();
    }
    
    private void dbAddTicketAction() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("INSERT INTO TICKETACTIONS (TICKETID,ACTIONNO,ACTIONDATE,ACTIONTYPE,BYWHOM,NOTE,VISIBILITYLEVEL,ISDIRTY,QUALITYOFSERVICECODE,COURTEOUSCODE,KNOWLEDGEABLECODE,GENERALFLAGS,QUALITIESFLAGS,IRKEDFLAGS,TRELLOCOMMENTID) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, this.ticketId);
            ps.setShort(2, this.actionNo);
            ps.setLong(3, this.date);
            ps.setByte(4, this.action);
            ps.setString(5, this.byWhom);
            ps.setString(6, this.note);
            ps.setByte(7, this.visibilityLevel);
            ps.setBoolean(8, this.dirty);
            ps.setByte(9, this.qualityOfServiceCode);
            ps.setByte(10, this.courteousCode);
            ps.setByte(11, this.knowledgeableCode);
            ps.setByte(12, this.generalFlags);
            ps.setByte(13, this.qualitiesFlags);
            ps.setByte(14, this.irkedFlags);
            ps.setString(15, this.trelloCommentId);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            TicketAction.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void dbUpdateIsDirty() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE TICKETACTIONS SET ISDIRTY=? WHERE TICKETID=? AND ACTIONNO=?");
            ps.setBoolean(1, this.dirty);
            ps.setInt(2, this.ticketId);
            ps.setShort(3, this.actionNo);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            TicketAction.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private void dbUpdateTrelloCommentId() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getLoginDbCon();
            ps = dbcon.prepareStatement("UPDATE TICKETACTIONS SET TRELLOCOMMENTID=?,ISDIRTY=? WHERE TICKETID=? AND ACTIONNO=?");
            ps.setString(1, this.trelloCommentId);
            ps.setBoolean(2, this.dirty);
            ps.setInt(3, this.ticketId);
            ps.setShort(4, this.actionNo);
            ps.executeUpdate();
        }
        catch (SQLException sqx) {
            TicketAction.logger.log(Level.WARNING, sqx.getMessage(), sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public String toString() {
        return "TicketAction [ticketId=" + this.ticketId + ", actionNo=" + this.actionNo + ", note=" + this.note + "]";
    }
    
    static {
        logger = Logger.getLogger(TicketAction.class.getName());
    }
}
