// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.webinterface;

import com.wurmonline.server.Servers;
import com.wurmonline.server.support.Tickets;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import com.wurmonline.shared.util.StreamUtilities;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.support.TicketAction;
import com.wurmonline.server.support.Ticket;
import java.util.logging.Logger;
import com.wurmonline.server.MiscConstants;

public class WcTicket extends WebCommand implements MiscConstants
{
    private static final Logger logger;
    public static final byte DO_NOTHING = 0;
    public static final byte GET_BATCHNOS = 1;
    public static final byte THE_BATCHNOS = 2;
    public static final byte SEND_TICKET = 3;
    public static final byte CHECK_FOR_UPDATES = 4;
    private byte type;
    private int noBatchNos;
    private int firstBatchNos;
    private int secondBatchNos;
    private Ticket ticket;
    private boolean sendActions;
    private TicketAction ticketAction;
    private long checkDate;
    
    public WcTicket(final int aNoBatchNos) {
        super(WurmId.getNextWCCommandId(), (short)18);
        this.type = 0;
        this.noBatchNos = 1;
        this.firstBatchNos = 0;
        this.secondBatchNos = 0;
        this.ticket = null;
        this.sendActions = false;
        this.ticketAction = null;
        this.checkDate = 0L;
        this.type = 1;
        this.noBatchNos = aNoBatchNos;
    }
    
    public WcTicket(final int aFirstBatchNos, final int aSecondBatchNos) {
        super(WurmId.getNextWCCommandId(), (short)18);
        this.type = 0;
        this.noBatchNos = 1;
        this.firstBatchNos = 0;
        this.secondBatchNos = 0;
        this.ticket = null;
        this.sendActions = false;
        this.ticketAction = null;
        this.checkDate = 0L;
        this.type = 2;
        this.firstBatchNos = aFirstBatchNos;
        this.secondBatchNos = aSecondBatchNos;
    }
    
    public WcTicket(final Ticket aTicket) {
        super(WurmId.getNextWCCommandId(), (short)18);
        this.type = 0;
        this.noBatchNos = 1;
        this.firstBatchNos = 0;
        this.secondBatchNos = 0;
        this.ticket = null;
        this.sendActions = false;
        this.ticketAction = null;
        this.checkDate = 0L;
        this.type = 3;
        this.ticket = aTicket;
        this.ticketAction = null;
        this.sendActions = true;
    }
    
    public WcTicket(final long aId, final Ticket aTicket, final int aNumbActions, final TicketAction aTicketAction) {
        super(aId, (short)18);
        this.type = 0;
        this.noBatchNos = 1;
        this.firstBatchNos = 0;
        this.secondBatchNos = 0;
        this.ticket = null;
        this.sendActions = false;
        this.ticketAction = null;
        this.checkDate = 0L;
        this.type = 3;
        this.ticket = aTicket;
        if (aNumbActions > 1) {
            this.ticketAction = null;
        }
        else {
            this.ticketAction = aTicketAction;
        }
        this.sendActions = (aNumbActions > 0);
    }
    
    public WcTicket(final Ticket aTicket, final TicketAction aTicketAction) {
        super(WurmId.getNextWCCommandId(), (short)18);
        this.type = 0;
        this.noBatchNos = 1;
        this.firstBatchNos = 0;
        this.secondBatchNos = 0;
        this.ticket = null;
        this.sendActions = false;
        this.ticketAction = null;
        this.checkDate = 0L;
        this.type = 3;
        this.ticket = aTicket;
        this.ticketAction = aTicketAction;
        this.sendActions = true;
    }
    
    public WcTicket(final long aDate) {
        super(WurmId.getNextWCCommandId(), (short)18);
        this.type = 0;
        this.noBatchNos = 1;
        this.firstBatchNos = 0;
        this.secondBatchNos = 0;
        this.ticket = null;
        this.sendActions = false;
        this.ticketAction = null;
        this.checkDate = 0L;
        this.type = 4;
        this.checkDate = aDate;
    }
    
    public WcTicket(final long aId, final byte[] aData) {
        super(aId, (short)18, aData);
        this.type = 0;
        this.noBatchNos = 1;
        this.firstBatchNos = 0;
        this.secondBatchNos = 0;
        this.ticket = null;
        this.sendActions = false;
        this.ticketAction = null;
        this.checkDate = 0L;
    }
    
    @Override
    public boolean autoForward() {
        return false;
    }
    
    @Override
    byte[] encode() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = null;
        byte[] barr = null;
        try {
            dos = new DataOutputStream(bos);
            dos.writeByte(this.type);
            switch (this.type) {
                case 1: {
                    dos.writeInt(this.noBatchNos);
                    break;
                }
                case 2: {
                    dos.writeInt(this.firstBatchNos);
                    dos.writeInt(this.secondBatchNos);
                    break;
                }
                case 3: {
                    dos.writeInt(this.ticket.getTicketId());
                    dos.writeLong(this.ticket.getTicketDate());
                    dos.writeLong(this.ticket.getPlayerId());
                    dos.writeUTF(this.ticket.getPlayerName());
                    dos.writeByte(this.ticket.getCategoryCode());
                    dos.writeInt(this.ticket.getServerId());
                    dos.writeBoolean(this.ticket.isGlobal());
                    dos.writeLong(this.ticket.getClosedDate());
                    dos.writeByte(this.ticket.getStateCode());
                    dos.writeByte(this.ticket.getLevelCode());
                    dos.writeUTF(this.ticket.getResponderName());
                    dos.writeUTF(this.ticket.getDescription());
                    dos.writeShort(this.ticket.getRefFeedback());
                    dos.writeBoolean(this.ticket.getAcknowledged());
                    if (!this.sendActions) {
                        dos.writeByte(0);
                        break;
                    }
                    if (this.ticketAction == null) {
                        final TicketAction[] ticketActions = this.ticket.getTicketActions();
                        dos.writeByte(ticketActions.length);
                        for (final TicketAction ta : ticketActions) {
                            this.addTicketAction(dos, ta);
                        }
                        break;
                    }
                    dos.writeByte(1);
                    this.addTicketAction(dos, this.ticketAction);
                    break;
                }
                case 4: {
                    dos.writeLong(this.checkDate);
                    break;
                }
            }
            dos.flush();
            dos.close();
        }
        catch (Exception ex) {
            WcTicket.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        finally {
            StreamUtilities.closeOutputStreamIgnoreExceptions(dos);
            barr = bos.toByteArray();
            StreamUtilities.closeOutputStreamIgnoreExceptions(bos);
            this.setData(barr);
        }
        return barr;
    }
    
    private void addTicketAction(final DataOutputStream dos, final TicketAction ta) throws IOException {
        dos.writeShort(ta.getActionNo());
        dos.writeByte(ta.getAction());
        dos.writeLong(ta.getDate());
        dos.writeUTF(ta.getByWhom());
        dos.writeUTF(ta.getNote());
        dos.writeByte(ta.getVisibilityLevel());
        if (ta.getAction() == 14) {
            dos.writeByte(ta.getQualityOfServiceCode());
            dos.writeByte(ta.getCourteousCode());
            dos.writeByte(ta.getKnowledgeableCode());
            dos.writeByte(ta.getGeneralFlags());
            dos.writeByte(ta.getQualitiesFlags());
            dos.writeByte(ta.getIrkedFlags());
        }
    }
    
    @Override
    public void execute() {
        new Thread() {
            @Override
            public void run() {
                DataInputStream dis = null;
                try {
                    dis = new DataInputStream(new ByteArrayInputStream(WcTicket.this.getData()));
                    WcTicket.this.type = dis.readByte();
                    switch (WcTicket.this.type) {
                        case 1: {
                            WcTicket.this.noBatchNos = dis.readInt();
                            break;
                        }
                        case 2: {
                            WcTicket.this.firstBatchNos = dis.readInt();
                            WcTicket.this.secondBatchNos = dis.readInt();
                            break;
                        }
                        case 3: {
                            final int ticketId = dis.readInt();
                            final long ticketDate = dis.readLong();
                            final long playerWurmId = dis.readLong();
                            final String playerName = dis.readUTF();
                            final byte categoryCode = dis.readByte();
                            final int serverId = dis.readInt();
                            final boolean global = dis.readBoolean();
                            final long closedDate = dis.readLong();
                            final byte stateCode = dis.readByte();
                            final byte levelCode = dis.readByte();
                            final String responderName = dis.readUTF();
                            final String description = dis.readUTF();
                            final short refFeedback = dis.readShort();
                            final boolean acknowledge = dis.readBoolean();
                            WcTicket.this.ticket = new Ticket(ticketId, ticketDate, playerWurmId, playerName, categoryCode, serverId, global, closedDate, stateCode, levelCode, responderName, description, true, refFeedback, acknowledge);
                            WcTicket.this.ticket = Tickets.addTicket(WcTicket.this.ticket, true);
                            final int numbActions = dis.readByte();
                            TicketAction ta = null;
                            for (int x = 0; x < numbActions; ++x) {
                                byte qualityOfServiceCode = 0;
                                byte courteousCode = 0;
                                byte knowledgeableCode = 0;
                                byte generalFlags = 0;
                                byte qualitiesFlags = 0;
                                byte irkedFlags = 0;
                                final short actionNo = dis.readShort();
                                final byte action = dis.readByte();
                                final long dated = dis.readLong();
                                final String byWhom = dis.readUTF();
                                final String note = dis.readUTF();
                                final byte visLevel = dis.readByte();
                                if (action == 14) {
                                    qualityOfServiceCode = dis.readByte();
                                    courteousCode = dis.readByte();
                                    knowledgeableCode = dis.readByte();
                                    generalFlags = dis.readByte();
                                    qualitiesFlags = dis.readByte();
                                    irkedFlags = dis.readByte();
                                }
                                ta = WcTicket.this.ticket.addTicketAction(actionNo, action, dated, byWhom, note, visLevel, qualityOfServiceCode, courteousCode, knowledgeableCode, generalFlags, qualitiesFlags, irkedFlags);
                            }
                            if (Servers.isThisLoginServer() && WcTicket.this.ticket.isGlobal()) {
                                final WcTicket wct = new WcTicket(WcTicket.this.getWurmId(), WcTicket.this.ticket, numbActions, ta);
                                wct.sendFromLoginServer();
                            }
                            Tickets.addTicketToSend(WcTicket.this.ticket, numbActions, ta);
                            break;
                        }
                        case 4: {
                            WcTicket.this.checkDate = dis.readLong();
                            break;
                        }
                    }
                }
                catch (IOException ex) {
                    WcTicket.logger.log(Level.WARNING, "Unpack exception " + ex.getMessage(), ex);
                    return;
                }
                finally {
                    StreamUtilities.closeInputStreamIgnoreExceptions(dis);
                }
                if (WcTicket.this.type == 1 && Servers.isThisLoginServer()) {
                    WcTicket.this.firstBatchNos = Tickets.getNextBatchNo();
                    if (WcTicket.this.noBatchNos > 1) {
                        WcTicket.this.secondBatchNos = Tickets.getNextBatchNo();
                    }
                    final WcTicket wt = new WcTicket(WcTicket.this.firstBatchNos, WcTicket.this.secondBatchNos);
                    wt.sendToServer(WurmId.getOrigin(WcTicket.this.getWurmId()));
                }
                else if (WcTicket.this.type == 2 && !Servers.isThisLoginServer()) {
                    Tickets.setNextBatchNo(WcTicket.this.firstBatchNos, WcTicket.this.secondBatchNos);
                }
                else if (WcTicket.this.type != 3) {
                    if (WcTicket.this.type == 4) {
                        for (final Ticket t : Tickets.getTicketsChangedSince(WcTicket.this.checkDate)) {
                            final WcTicket wt2 = new WcTicket(t);
                            wt2.sendToServer(WurmId.getOrigin(WcTicket.this.getWurmId()));
                        }
                    }
                }
            }
        }.start();
    }
    
    static {
        logger = Logger.getLogger(WcTicket.class.getName());
    }
}
