// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.io.IOException;
import com.wurmonline.server.DbConnector;
import java.util.Iterator;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.questions.MailSendConfirmQuestion;
import com.wurmonline.server.Items;
import java.util.logging.Level;
import java.util.HashSet;
import com.wurmonline.server.Servers;
import java.util.logging.Logger;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.TimeConstants;

public final class WurmMail implements TimeConstants, MiscConstants, CounterTypes, Comparable<WurmMail>
{
    public static final byte MAIL_TYPE_PREPAID = 0;
    public static final byte MAIL_TYPE_CASHONDELIVERY = 1;
    public final byte type;
    public final long itemId;
    public long sender;
    public long receiver;
    public final long price;
    public final long sent;
    public long expiration;
    public final int sourceserver;
    public boolean rejected;
    private static final ConcurrentHashMap<Long, Set<WurmMail>> mails;
    private static final ConcurrentHashMap<Long, Long> mailsByItemId;
    private static final Logger logger;
    private static final String GET_ALL_MAIL = "SELECT * FROM MAIL";
    private static final String DELETE_MAIL = "DELETE FROM MAIL WHERE ITEMID=?";
    private static final String SAVE_MAIL = "INSERT INTO MAIL (ITEMID,TYPE,SENDER,RECEIVER,PRICE,SENT,EXPIRATION,SOURCESERVER,RETURNED ) VALUES(?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_MAIL = "UPDATE MAIL SET TYPE=?,SENDER=?,RECEIVER=?,PRICE=?,SENT=?,EXPIRATION=?,RETURNED=? WHERE ITEMID=?";
    public static final int maxNumberMailsToDisplay = 100;
    
    public WurmMail(final byte _type, final long _itemid, final long _sender, final long _receiver, final long _price, final long _sent, final long _expiration, final int _sourceserver, final boolean _rejected, final boolean loading) {
        this.rejected = false;
        this.type = _type;
        this.itemId = _itemid;
        this.sender = _sender;
        this.receiver = _receiver;
        this.price = _price;
        this.sent = _sent;
        this.expiration = _expiration;
        this.rejected = _rejected;
        this.sourceserver = _sourceserver;
        if (loading) {
            addWurmMail(this);
        }
    }
    
    public static final void addWurmMail(final WurmMail mail) {
        if (!mail.isExpired() || mail.isRejected()) {
            if (mail.isRejected() && mail.isExpired()) {
                deleteMail(mail.getItemId());
            }
            else {
                getMailsFor(mail.receiver).add(mail);
                WurmMail.mailsByItemId.put(new Long(mail.itemId), new Long(mail.receiver));
            }
        }
        else if (mail.sourceserver == Servers.localServer.id && !mail.rejected) {
            final long oldRec = mail.receiver;
            mail.receiver = mail.sender;
            mail.sender = oldRec;
            mail.expiration = System.currentTimeMillis() + 1209600000L;
            mail.rejected = true;
            mail.update();
            getMailsFor(mail.receiver).add(mail);
            WurmMail.mailsByItemId.put(new Long(mail.itemId), new Long(mail.receiver));
        }
    }
    
    public static final void poll() {
        final Set<WurmMail> toDelete = new HashSet<WurmMail>();
        for (final Set<WurmMail> mailset : WurmMail.mails.values()) {
            for (final WurmMail m : mailset) {
                if (m.isExpired()) {
                    WurmMail.logger.log(Level.INFO, "Checking expired WurmMail " + m.itemId);
                    if (m.sourceserver != Servers.localServer.id && !m.isRejected()) {
                        WurmMail.logger.log(Level.INFO, "Trying to return expired WurmMail " + m.itemId);
                        final int targetServer = m.sourceserver;
                        try {
                            final Item toReturn = Items.getItem(m.getItemId());
                            if (!Servers.getServerWithId(targetServer).isAvailable(5, true)) {
                                continue;
                            }
                            WurmMail.logger.log(Level.INFO, "Returning to " + targetServer);
                            m.expiration = System.currentTimeMillis() + 1209600000L;
                            m.setRejected(true);
                            final long sender = m.receiver;
                            m.receiver = m.sender;
                            m.sender = sender;
                            final Set<WurmMail> oneMail = new HashSet<WurmMail>();
                            oneMail.add(m);
                            final Item[] itemarr = { toReturn };
                            if (MailSendConfirmQuestion.sendMailSetToServer(m.receiver, null, targetServer, oneMail, m.sender, itemarr)) {
                                continue;
                            }
                            toDelete.add(m);
                        }
                        catch (NoSuchItemException nsi) {
                            toDelete.add(m);
                            break;
                        }
                    }
                    else {
                        if (!m.isRejected()) {
                            continue;
                        }
                        WurmMail.logger.log(Level.INFO, "Deleting expired rejected mail " + m.getItemId() + " sent to " + m.getReceiver() + " from " + m.getSender());
                        toDelete.add(m);
                    }
                }
            }
        }
        for (final WurmMail deleted : toDelete) {
            Items.destroyItem(deleted.getItemId());
            removeMail(deleted.getItemId());
            WurmMail.logger.log(Level.INFO, "Deleted WurmMail " + deleted.getItemId());
        }
        toDelete.clear();
    }
    
    public static final boolean isItemInMail(final long itemId) {
        return WurmMail.mailsByItemId.get(itemId) != null;
    }
    
    public boolean isRejected() {
        return this.rejected;
    }
    
    public void setRejected(final boolean aRejected) {
        this.rejected = aRejected;
    }
    
    public byte getType() {
        return this.type;
    }
    
    public long getItemId() {
        return this.itemId;
    }
    
    public long getSender() {
        return this.sender;
    }
    
    public long getReceiver() {
        return this.receiver;
    }
    
    public long getPrice() {
        return this.price;
    }
    
    public long getSent() {
        return this.sent;
    }
    
    public long getExpiration() {
        return this.expiration;
    }
    
    public int getSourceserver() {
        return this.sourceserver;
    }
    
    public String getName() {
        try {
            final Item item = Items.getItem(this.itemId);
            return item.getName();
        }
        catch (NoSuchItemException e) {
            return "UnKnown";
        }
    }
    
    public boolean isExpired() {
        return this.expiration < System.currentTimeMillis();
    }
    
    public static final Set<WurmMail> getWaitingMailFor(final long receiverid) {
        final Set<WurmMail> set = WurmMail.mails.get(new Long(receiverid));
        final Set<WurmMail> toReturn = new HashSet<WurmMail>();
        if (set != null) {
            for (final WurmMail mail : set) {
                if (mail.sent < System.currentTimeMillis()) {
                    toReturn.add(mail);
                }
            }
        }
        return toReturn;
    }
    
    public static final Set<WurmMail> getMailsFor(final long receiverid) {
        Set<WurmMail> set = WurmMail.mails.get(new Long(receiverid));
        if (set == null) {
            set = new HashSet<WurmMail>();
            WurmMail.mails.put(new Long(receiverid), set);
        }
        return set;
    }
    
    public static final Set<WurmMail> getSentMailsFor(final long receiverid, final int maxNumbers) {
        final Set<WurmMail> set = getMailsFor(receiverid);
        final Set<WurmMail> toReturn = new HashSet<WurmMail>();
        int nums = 0;
        for (final WurmMail toAdd : set) {
            if (toAdd.sent < System.currentTimeMillis()) {
                toReturn.add(toAdd);
                ++nums;
            }
            if (nums >= maxNumbers) {
                break;
            }
        }
        return toReturn;
    }
    
    public static final Set<WurmMail> getMailsSendBy(final long senderid) {
        final Set<WurmMail> sent = new HashSet<WurmMail>();
        for (final Set<WurmMail> mailset : WurmMail.mails.values()) {
            for (final WurmMail m : mailset) {
                if (m.sender == senderid) {
                    sent.add(m);
                }
            }
        }
        return sent;
    }
    
    public static final long getReceiverForItem(final long itemId) {
        final Long receiver = WurmMail.mailsByItemId.get(new Long(itemId));
        if (receiver == null) {
            return -10L;
        }
        return receiver;
    }
    
    public static final WurmMail getWurmMailForItem(final long itemId) {
        final Long receiver = WurmMail.mailsByItemId.get(new Long(itemId));
        if (receiver == null) {
            return null;
        }
        final Set<WurmMail> set = getMailsFor(receiver);
        if (set != null) {
            for (final WurmMail m : set) {
                if (m.itemId == itemId) {
                    return m;
                }
            }
        }
        return null;
    }
    
    public static final WurmMail[] getAllMail() {
        final Set<WurmMail> sent = new HashSet<WurmMail>();
        for (final Set<WurmMail> mailset : WurmMail.mails.values()) {
            for (final WurmMail m : mailset) {
                sent.add(m);
            }
        }
        return sent.toArray(new WurmMail[sent.size()]);
    }
    
    public static final void removeMail(final long itemId) {
        final Long receiver = WurmMail.mailsByItemId.get(new Long(itemId));
        if (receiver != null) {
            final Set<WurmMail> set = getMailsFor(receiver);
            if (set != null) {
                WurmMail toRemove = null;
                for (final WurmMail m : set) {
                    if (m.itemId == itemId) {
                        toRemove = m;
                        break;
                    }
                }
                if (toRemove != null) {
                    set.remove(toRemove);
                }
            }
            WurmMail.mailsByItemId.remove(new Long(itemId));
            deleteMail(itemId);
        }
    }
    
    public static final void loadAllMails() throws IOException {
        final long start = System.nanoTime();
        int loadedMails = 0;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM MAIL");
            rs = ps.executeQuery();
            while (rs.next()) {
                new WurmMail(rs.getByte("TYPE"), rs.getLong("ITEMID"), rs.getLong("SENDER"), rs.getLong("RECEIVER"), rs.getLong("PRICE"), rs.getLong("SENT"), rs.getLong("EXPIRATION"), rs.getInt("SOURCESERVER"), rs.getBoolean("RETURNED"), true);
                ++loadedMails;
            }
        }
        catch (SQLException sqex) {
            WurmMail.logger.log(Level.WARNING, "Problem loading Mails from database due to " + sqex.getMessage(), sqex);
            throw new IOException(sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            WurmMail.logger.info("Loaded " + loadedMails + " Mails from the database took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    public static final void deleteMail(final long itemid) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("DELETE FROM MAIL WHERE ITEMID=?");
            ps.setLong(1, itemid);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            WurmMail.logger.log(Level.WARNING, itemid + " : " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final void createInDatabase() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("INSERT INTO MAIL (ITEMID,TYPE,SENDER,RECEIVER,PRICE,SENT,EXPIRATION,SOURCESERVER,RETURNED ) VALUES(?,?,?,?,?,?,?,?,?)");
            ps.setLong(1, this.itemId);
            ps.setByte(2, this.type);
            ps.setLong(3, this.sender);
            ps.setLong(4, this.receiver);
            ps.setLong(5, this.price);
            ps.setLong(6, this.sent);
            ps.setLong(7, this.expiration);
            ps.setInt(8, this.sourceserver);
            ps.setBoolean(9, this.rejected);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            WurmMail.logger.log(Level.WARNING, this.itemId + " : " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private final void update() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getItemDbCon();
            ps = dbcon.prepareStatement("UPDATE MAIL SET TYPE=?,SENDER=?,RECEIVER=?,PRICE=?,SENT=?,EXPIRATION=?,RETURNED=? WHERE ITEMID=?");
            ps.setByte(1, this.type);
            ps.setLong(2, this.sender);
            ps.setLong(3, this.receiver);
            ps.setLong(4, this.price);
            ps.setLong(5, this.sent);
            ps.setLong(6, this.expiration);
            ps.setBoolean(7, this.rejected);
            ps.setLong(8, this.itemId);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            WurmMail.logger.log(Level.WARNING, this.itemId + " : " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    @Override
    public int compareTo(final WurmMail otherWurmMail) {
        return this.getName().compareTo(otherWurmMail.getName());
    }
    
    static {
        mails = new ConcurrentHashMap<Long, Set<WurmMail>>();
        mailsByItemId = new ConcurrentHashMap<Long, Long>();
        logger = Logger.getLogger(WurmMail.class.getName());
    }
}
