// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.kingdom;

import com.wurmonline.server.villages.Village;
import com.wurmonline.server.players.PlayerState;
import com.wurmonline.server.Server;
import com.wurmonline.server.Message;
import java.io.IOException;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.webinterface.WcExpelMember;
import com.wurmonline.server.LoginHandler;
import com.wurmonline.server.creatures.Creature;
import java.util.Iterator;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.Players;
import com.wurmonline.server.Servers;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;

public final class Kingdom implements MiscConstants, TimeConstants
{
    private static final Logger logger;
    final byte kingdomId;
    private final byte template;
    private final String name;
    private final String chatname;
    private final String suffix;
    private final String password;
    private String firstMotto;
    private String secondMotto;
    private boolean existsHere;
    private boolean shouldBeDeleted;
    private final byte red;
    private final byte blue;
    private final byte green;
    public static final byte ALLIANCE_TYPE_NONE = 0;
    public static final byte ALLIANCE_TYPE_ALLIANCE = 1;
    public static final byte ALLIANCE_TYPE_SENT_REQUEST = 2;
    private static int winPoints;
    private long startedDisbandWarning;
    public int activePremiums;
    public boolean countedAtleastOnce;
    private boolean acceptsTransfers;
    private static final String LOAD_ALL_KINGDOMS = "SELECT * FROM KINGDOMS";
    private static final String LOAD_ALLIANCES = "SELECT * FROM KALLIANCES";
    private static final String INSERT_KINGDOM = "INSERT INTO KINGDOMS (KINGDOM, KINGDOMNAME,PASSWORD, TEMPLATE, SUFFIX, CHATNAME, FIRSTMOTTO,SECONDMOTTO,ACCEPTSTRANSFERS) VALUES (?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_KINGDOM = "UPDATE KINGDOMS SET KINGDOMNAME=?, PASSWORD=?,TEMPLATE=?, SUFFIX=?, CHATNAME=?, FIRSTMOTTO=?,SECONDMOTTO=?,ACCEPTSTRANSFERS=? WHERE KINGDOM=?";
    private static final String INSERT_ALLIANCE = "INSERT INTO KALLIANCES (ALLIANCETYPE,KINGDOMONE, KINGDOMTWO) VALUES (?,?,?)";
    private static final String UPDATE_ALLIANCE = "UPDATE KALLIANCES SET ALLIANCETYPE=? WHERE KINGDOMONE=? AND KINGDOMTWO=?";
    private static final String DELETE_ALLIANCE = "DELETE FROM KALLIANCES WHERE KINGDOMONE=? AND KINGDOMTWO=?";
    private static final String DELETE_ALL_ALLIANCE = "DELETE FROM KALLIANCES WHERE KINGDOMONE=? OR KINGDOMTWO=?";
    private static final String SET_ERA_NONE = "UPDATE KING_ERA SET KINGDOM=0 WHERE KINGDOM=?";
    private static final String SET_WINPOINTS = "UPDATE KINGDOMS SET WINPOINTS=? WHERE KINGDOM=?";
    private static final String GET_MEMBERS = "SELECT WURMID FROM PLAYERS WHERE KINGDOM=?";
    private static final Random colorRand;
    private Map<Byte, Byte> alliances;
    private List<Long> members;
    private ArrayList<KingdomBuff> kingdomBuffs;
    public int lastConfrontationTileX;
    public int lastConfrontationTileY;
    private long lastMemberLoad;
    
    public Kingdom(final byte id, final byte templateKingdom, final String _name, final String _password, final String _chatName, final String _suffix, final String mottoOne, final String mottoTwo, final boolean acceptsPortals) {
        this.firstMotto = "";
        this.secondMotto = "";
        this.existsHere = false;
        this.shouldBeDeleted = false;
        this.startedDisbandWarning = 0L;
        this.activePremiums = 0;
        this.countedAtleastOnce = false;
        this.acceptsTransfers = true;
        this.alliances = new HashMap<Byte, Byte>();
        this.members = new LinkedList<Long>();
        this.kingdomBuffs = new ArrayList<KingdomBuff>();
        this.lastMemberLoad = 0L;
        this.kingdomId = id;
        this.template = templateKingdom;
        this.name = _name;
        this.password = _password;
        this.chatname = _chatName;
        this.suffix = _suffix;
        this.firstMotto = mottoOne;
        this.secondMotto = mottoTwo;
        this.acceptsTransfers = acceptsPortals;
        Kingdom.colorRand.setSeed(this.name.hashCode());
        this.red = (byte)Kingdom.colorRand.nextInt(255);
        this.blue = (byte)Kingdom.colorRand.nextInt(255);
        this.green = (byte)Kingdom.colorRand.nextInt(255);
        this.loadAllMembers();
    }
    
    public byte getId() {
        return this.kingdomId;
    }
    
    public byte getTemplate() {
        return this.template;
    }
    
    public Map<Byte, Byte> getAllianceMap() {
        return this.alliances;
    }
    
    void setAlliances(final Map<Byte, Byte> newAlliances) {
        this.alliances = newAlliances;
    }
    
    public boolean existsHere() {
        return this.existsHere;
    }
    
    public void setExistsHere(final boolean exists) {
        if (this.existsHere != exists) {
            Servers.loginServer.shouldResendKingdoms = true;
        }
        this.existsHere = exists;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public String getChatName() {
        return this.chatname;
    }
    
    public String getSuffix() {
        return this.suffix;
    }
    
    public void setAcceptsTransfers(final boolean accepts) {
        if (!this.isCustomKingdom()) {
            return;
        }
        this.acceptsTransfers = accepts;
    }
    
    public boolean acceptsTransfers() {
        return this.acceptsTransfers;
    }
    
    public void setFirstMotto(final String motto) {
        this.firstMotto = motto;
    }
    
    public void setSecondMotto(final String motto) {
        this.secondMotto = motto;
    }
    
    public String getFirstMotto() {
        return this.firstMotto;
    }
    
    public String getSecondMotto() {
        return this.secondMotto;
    }
    
    boolean isShouldBeDeleted() {
        return this.shouldBeDeleted;
    }
    
    void setShouldBeDeleted(final boolean aShouldBeDeleted) {
        this.shouldBeDeleted = aShouldBeDeleted;
    }
    
    public byte getColorRed() {
        return this.red;
    }
    
    public byte getColorBlue() {
        return this.blue;
    }
    
    public byte getColorGreen() {
        return this.green;
    }
    
    public boolean isCustomKingdom() {
        return this.kingdomId < 0 || this.kingdomId > 4;
    }
    
    public void disband() {
        Kingdoms.destroyTowersWithKingdom(this.kingdomId);
        final Kingdom[] allKingdoms;
        final Kingdom[] kingdomArr = allKingdoms = Kingdoms.getAllKingdoms();
        for (final Kingdom k2 : allKingdoms) {
            if (k2.getId() != this.getId()) {
                k2.removeKingdomFromAllianceMap(this.getId());
            }
        }
        final King i = King.getKing(this.kingdomId);
        final byte newKingdomId = (byte)((this.getTemplate() == 3) ? 3 : (Servers.localServer.isChallengeOrEpicServer() ? this.getTemplate() : 4));
        if (i != null) {
            i.abdicate(true, true);
            Players.getInstance().convertFromKingdomToKingdom(this.kingdomId, newKingdomId);
        }
        this.existsHere = false;
        final PlayerInfo[] playerInfos;
        final PlayerInfo[] pinfs = playerInfos = PlayerInfoFactory.getPlayerInfos();
        for (final PlayerInfo pinf : playerInfos) {
            if (pinf.epicKingdom == this.kingdomId) {
                pinf.setEpicLocation(newKingdomId, pinf.epicServerId);
            }
            if (pinf.getChaosKingdom() == this.kingdomId) {
                pinf.setChaosKingdom(newKingdomId);
            }
        }
        this.delete();
    }
    
    public boolean isAllied(final byte otherKingdom) {
        final Byte b = this.alliances.get(otherKingdom);
        return b != null && b == 1;
    }
    
    public boolean hasSentRequestingAlliance(final byte otherKingdom) {
        final Byte b = this.alliances.get(otherKingdom);
        return b != null && b == 2;
    }
    
    public void setAlliance(final byte kingdId, final byte allianceType) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            if (allianceType == 0) {
                this.alliances.remove(kingdId);
                ps = dbcon.prepareStatement("DELETE FROM KALLIANCES WHERE KINGDOMONE=? AND KINGDOMTWO=?");
                ps.setByte(1, this.kingdomId);
                ps.setByte(2, kingdId);
                ps.executeUpdate();
            }
            else {
                if (this.alliances.containsKey(kingdId)) {
                    ps = dbcon.prepareStatement("UPDATE KALLIANCES SET ALLIANCETYPE=? WHERE KINGDOMONE=? AND KINGDOMTWO=?");
                }
                else {
                    ps = dbcon.prepareStatement("INSERT INTO KALLIANCES (ALLIANCETYPE,KINGDOMONE, KINGDOMTWO) VALUES (?,?,?)");
                }
                ps.setByte(1, allianceType);
                ps.setByte(2, this.kingdomId);
                ps.setByte(3, kingdId);
                ps.executeUpdate();
                this.alliances.put(kingdId, allianceType);
            }
        }
        catch (SQLException sqex) {
            Kingdom.logger.log(Level.WARNING, "Failed to load kingdom: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    void removeKingdomFromAllianceMap(final byte kingdom) {
        this.alliances.remove(kingdom);
    }
    
    public static final void loadAllKingdoms() {
        Kingdom.logger.log(Level.INFO, "Loading all kingdoms.");
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM KINGDOMS");
            rs = ps.executeQuery();
            while (rs.next()) {
                final Kingdom k = new Kingdom(rs.getByte("KINGDOM"), rs.getByte("TEMPLATE"), rs.getString("KINGDOMNAME"), rs.getString("PASSWORD"), rs.getString("CHATNAME"), rs.getString("SUFFIX"), rs.getString("FIRSTMOTTO"), rs.getString("SECONDMOTTO"), rs.getBoolean("ACCEPTSTRANSFERS"));
                k.setWinpoints(rs.getInt("WINPOINTS"));
                Kingdoms.loadKingdom(k);
            }
        }
        catch (SQLException sqex) {
            Kingdom.logger.log(Level.WARNING, "Failed to load kingdom: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            Kingdom.logger.info("Loaded kingdoms from database took " + (end - start) / 1000000.0f + " ms");
        }
        loadAlliances();
        if (Kingdoms.numKingdoms() == 0) {
            Kingdoms.createBasicKingdoms();
        }
    }
    
    public void loadAllMembers() {
        if (System.currentTimeMillis() - this.lastMemberLoad < 900000L) {
            return;
        }
        this.lastMemberLoad = System.currentTimeMillis();
        if (!Servers.localServer.PVPSERVER || this.getId() == 4) {
            return;
        }
        Kingdom.logger.log(Level.INFO, "Loading all members for " + this.getName() + ".");
        final long start = System.nanoTime();
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getPlayerDbCon();
            ps = dbcon.prepareStatement("SELECT WURMID FROM PLAYERS WHERE KINGDOM=?");
            ps.setByte(1, this.kingdomId);
            rs = ps.executeQuery();
            while (rs.next()) {
                final long wurmId = rs.getLong("WURMID");
                if (wurmId == -10L) {
                    continue;
                }
                this.addMember(wurmId);
            }
        }
        catch (SQLException sqex) {
            Kingdom.logger.log(Level.WARNING, "Failed to load kingdom members: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            final long end = System.nanoTime();
            Kingdom.logger.info("Loaded " + this.members.size() + " kingdom members from database took " + (end - start) / 1000000.0f + " ms");
        }
    }
    
    final void loadAlliance(final byte otherKingdom, final byte allianceType) {
        Kingdom.logger.log(Level.INFO, "Alliance between " + this.getId() + " and " + otherKingdom + ":" + allianceType);
        this.alliances.put(otherKingdom, allianceType);
    }
    
    private static final void loadAlliances() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM KALLIANCES");
            rs = ps.executeQuery();
            while (rs.next()) {
                final Kingdom k = Kingdoms.getKingdom(rs.getByte("KINGDOMONE"));
                if (k != null) {
                    k.loadAlliance(rs.getByte("KINGDOMTWO"), rs.getByte("ALLIANCETYPE"));
                }
            }
        }
        catch (SQLException sqex) {
            Kingdom.logger.log(Level.WARNING, "Failed to load alliances: " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void delete() {
        Kingdom.logger.log(Level.INFO, "Deleting " + this.kingdomId + ", " + this.name);
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("DELETE FROM KALLIANCES WHERE KINGDOMONE=? OR KINGDOMTWO=?");
            ps.setByte(1, this.kingdomId);
            ps.setByte(2, this.kingdomId);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Kingdom.logger.log(Level.WARNING, "Failed to delete all alliances for " + this.kingdomId + ", " + this.name + " : " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE KING_ERA SET KINGDOM=0 WHERE KINGDOM=?");
            ps.setByte(1, this.kingdomId);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Kingdom.logger.log(Level.WARNING, "Failed to update king era set to none for " + this.kingdomId + ", " + this.name + " : " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
        King.setToNoKingdom(this.kingdomId);
    }
    
    private void updatePointsDB() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE KINGDOMS SET WINPOINTS=? WHERE KINGDOM=?");
            ps.setInt(1, Kingdom.winPoints);
            ps.setByte(2, this.kingdomId);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Kingdom.logger.log(Level.WARNING, "Failed to update king era set to none for " + this.kingdomId + ", " + this.name + " : " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void update() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("UPDATE KINGDOMS SET KINGDOMNAME=?, PASSWORD=?,TEMPLATE=?, SUFFIX=?, CHATNAME=?, FIRSTMOTTO=?,SECONDMOTTO=?,ACCEPTSTRANSFERS=? WHERE KINGDOM=?");
            ps.setString(1, this.name);
            ps.setString(2, this.password);
            ps.setByte(3, this.template);
            ps.setString(4, this.suffix);
            ps.setString(5, this.chatname);
            ps.setString(6, this.firstMotto);
            ps.setString(7, this.secondMotto);
            ps.setBoolean(8, this.acceptsTransfers);
            ps.setByte(9, this.kingdomId);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Kingdom.logger.log(Level.WARNING, "Failed to delete kingdom " + this.kingdomId + ", " + this.name + " : " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    void saveToDisk() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            Kingdom.logger.log(Level.INFO, "Saving " + this.name + " id=" + this.kingdomId);
            dbcon = DbConnector.getZonesDbCon();
            ps = dbcon.prepareStatement("INSERT INTO KINGDOMS (KINGDOM, KINGDOMNAME,PASSWORD, TEMPLATE, SUFFIX, CHATNAME, FIRSTMOTTO,SECONDMOTTO,ACCEPTSTRANSFERS) VALUES (?,?,?,?,?,?,?,?,?)");
            ps.setByte(1, this.kingdomId);
            ps.setString(2, this.name);
            ps.setString(3, this.password);
            ps.setByte(4, this.template);
            ps.setString(5, this.suffix);
            ps.setString(6, this.chatname);
            ps.setString(7, this.firstMotto);
            ps.setString(8, this.secondMotto);
            ps.setBoolean(9, this.acceptsTransfers);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Kingdom.logger.log(Level.WARNING, "Failed to save kingdom " + this.kingdomId + ", " + this.name + " : " + sqex.getMessage(), sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public void sendDisbandTick() {
        if (this.getStartedDisbandWarning() == 0L) {
            this.setStartedDisbandWarning(System.currentTimeMillis());
        }
        Kingdom.logger.log(Level.INFO, "The appointments of " + this.getName() + " does not work because of low population.");
    }
    
    public int getWinpoints() {
        return Kingdom.winPoints;
    }
    
    public void setWinpoints(final int newpoints) {
        Kingdom.winPoints = newpoints;
        this.updatePointsDB();
    }
    
    public void addWinpoints(final int pointsAdded) {
        Kingdom.winPoints += pointsAdded;
        this.updatePointsDB();
    }
    
    public void addMember(final long wurmId) {
        final PlayerInfo p = PlayerInfoFactory.getPlayerInfoWithWurmId(wurmId);
        if (p == null) {
            return;
        }
        if (p.getPower() != 0) {
            return;
        }
        if (this.members.contains(new Long(wurmId))) {
            return;
        }
        this.members.add(wurmId);
    }
    
    public void removeMember(final long wurmId) {
        if (!this.members.contains(new Long(wurmId))) {
            return;
        }
        this.members.remove(new Long(wurmId));
    }
    
    public final PlayerInfo getMember(final long wurmId) {
        if (!this.members.contains(new Long(wurmId))) {
            return null;
        }
        return PlayerInfoFactory.getPlayerInfoWithWurmId(wurmId);
    }
    
    public final PlayerInfo[] getAllMembers() {
        if (this.members.size() == 0) {
            Kingdom.logger.log(Level.WARNING, "No members to return for kingdom id " + this.getId() + "!");
            return new PlayerInfo[0];
        }
        final List<PlayerInfo> m = new LinkedList<PlayerInfo>();
        for (final long w : this.members) {
            final PlayerInfo p = PlayerInfoFactory.getPlayerInfoWithWurmId(w);
            if (p != null) {
                m.add(p);
            }
            else {
                Kingdom.logger.log(Level.WARNING, w + " returns null player info!");
            }
        }
        return m.toArray(new PlayerInfo[m.size()]);
    }
    
    public void expelMember(final Creature performer, final String ostra) {
        boolean isOnline = true;
        Player p = Players.getInstance().getPlayerOrNull(LoginHandler.raiseFirstLetter(ostra));
        final byte toKingdom = (byte)((Servers.localServer.EPIC || this.getTemplate() == 3) ? this.getTemplate() : 4);
        final PlayerInfo pInfo = PlayerInfoFactory.getPlayerInfoWithName(LoginHandler.raiseFirstLetter(ostra));
        if (pInfo == null) {
            performer.getCommunicator().sendNormalServerMessage("That player does not exist.", (byte)3);
            return;
        }
        if (pInfo.realdeath > 0) {
            performer.getCommunicator().sendNormalServerMessage("You cannot expel a champion of your kingdom.", (byte)3);
            return;
        }
        if (p == null) {
            final PlayerState ps = PlayerInfoFactory.getPlayerState(pInfo.wurmId);
            if (ps == null || ps.getServerId() != Servers.localServer.getId()) {
                final WcExpelMember wcx = new WcExpelMember(pInfo.wurmId, this.getId(), toKingdom, Servers.localServer.getId());
                if (!Servers.isThisLoginServer()) {
                    wcx.sendToLoginServer();
                }
                else {
                    wcx.sendFromLoginServer();
                }
            }
            isOnline = false;
            try {
                pInfo.load();
                p = new Player(pInfo);
            }
            catch (Exception ex) {
                performer.getCommunicator().sendNormalServerMessage("Failed to load '" + ostra + "' to expel, please /support.", (byte)3);
                ex.printStackTrace();
                return;
            }
        }
        if (p.getWurmId() == performer.getWurmId()) {
            performer.getCommunicator().sendNormalServerMessage("You cannot expel yourself!", (byte)3);
            return;
        }
        if (p.getKingdomId() != this.getId()) {
            performer.getCommunicator().sendNormalServerMessage("Only " + p.getName() + "'s king may expel them.", (byte)3);
            return;
        }
        final Village village = p.getCitizenVillage();
        if (village != null && village.isMayor(p)) {
            performer.getCommunicator().sendNormalServerMessage("You cannot expel " + p.getName() + " as they are mayor of " + p.getVillageName() + ".", (byte)3);
            return;
        }
        try {
            if (!p.setKingdomId(toKingdom, false, false, isOnline)) {
                performer.getCommunicator().sendNormalServerMessage("Unable to expel " + p.getName() + ", please /support.", (byte)3);
                return;
            }
            if (isOnline) {
                p.getCommunicator().sendAlertServerMessage("You have been expelled from " + this.getName() + "!");
                p.getCommunicator().sendAlertServerMessage("You better leave the kingdom immediately!");
            }
        }
        catch (IOException iox) {
            performer.getCommunicator().sendNormalServerMessage("Failed to expel '" + p.getName() + "', please /support.", (byte)3);
            iox.printStackTrace();
            return;
        }
        performer.getCommunicator().sendSafeServerMessage("You successfully expel " + p.getName() + ". Let the dog run!");
        final Message mess = new Message(performer, (byte)10, this.getChatName(), "<" + performer.getName() + "> expelled " + p.getName());
        Server.getInstance().addMessage(mess);
    }
    
    public int getPremiumMemberCount() {
        this.activePremiums = 0;
        final PlayerInfo p;
        this.members.forEach(w -> {
            p = PlayerInfoFactory.getPlayerInfoWithWurmId(w);
            if (p != null && p.isPaying()) {
                ++this.activePremiums;
            }
            return;
        });
        return this.activePremiums;
    }
    
    public long getStartedDisbandWarning() {
        return this.startedDisbandWarning;
    }
    
    public void setStartedDisbandWarning(final long startedDisbandWarning) {
        this.startedDisbandWarning = startedDisbandWarning;
    }
    
    static {
        logger = Logger.getLogger(Kingdom.class.getName());
        Kingdom.winPoints = 0;
        colorRand = new Random();
    }
}
