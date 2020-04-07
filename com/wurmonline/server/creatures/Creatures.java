// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import java.util.TimerTask;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.zones.Den;
import com.wurmonline.server.zones.Dens;
import com.wurmonline.server.tutorial.MissionTargets;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.LoginHandler;
import java.util.LinkedList;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.Players;
import java.util.List;
import java.util.ArrayList;
import com.wurmonline.server.behaviours.Seat;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.behaviours.Vehicle;
import java.sql.PreparedStatement;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Server;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.Constants;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.server.bodys.BodyFactory;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.Servers;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.villages.NoSuchRoleException;
import java.io.IOException;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.Items;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.wurmonline.server.utils.DbUtilities;
import java.sql.SQLException;
import java.util.logging.Level;
import com.wurmonline.server.DbConnector;
import com.wurmonline.server.behaviours.Vehicles;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.PlayerInfoFactory;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Timer;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;

public final class Creatures implements MiscConstants, CreatureTemplateIds, TimeConstants
{
    private final Map<Long, Creature> creatures;
    private final Map<Long, Creature> offlineCreatures;
    private final ConcurrentHashMap<Long, Creature> avatars;
    private final Map<Long, Long> protectedCreatures;
    private final Map<String, Creature> npcs;
    private final Map<Integer, Integer> creaturesByType;
    private static Creatures instance;
    private static Logger logger;
    private static final String getAllCreatures = "SELECT * FROM CREATURES";
    private static final String COUNT_CREATURES = "SELECT COUNT(*) FROM CREATURES";
    private static final String DELETE_CREATURE = "DELETE FROM CREATURES WHERE WURMID=?";
    private static final String DELETE_CREATUREBODY = "DELETE FROM BODYPARTS WHERE OWNERID=?";
    private static final String DELETE_CREATURESKILLS = "DELETE FROM SKILLS WHERE OWNER=?";
    private static final String DELETE_CREATUREITEMS = "DELETE FROM ITEMS WHERE OWNERID=?";
    private static final String DELETE_CREATURE_SPLIT = "DELETE FROM CREATURES_BASE WHERE WURMID=?";
    private static final String DELETE_CREATURE_POS_SPLIT = "DELETE FROM CREATURES_POS WHERE WURMID=?";
    private static final String DELETE_PROT_CREATURE = "DELETE FROM PROTECTED WHERE WURMID=?";
    private static final String INSERT_PROT_CREATURE = "INSERT INTO PROTECTED (WURMID,PLAYERID) VALUES(?,?)";
    private static final String LOAD_PROT_CREATURES = "SELECT * FROM PROTECTED";
    private static final boolean fixColourTraits = false;
    private final Map<Long, Brand> brandedCreatures;
    private final Map<Long, Long> ledCreatures;
    private static Map<Long, Creature> rideCreatures;
    private final Timer creaturePollThread;
    private final PollTimerTask pollTask;
    private int numberOfNice;
    private int numberOfAgg;
    private int numberOfTyped;
    private int kingdomCreatures;
    private static int destroyedCaveCrets;
    private static boolean loading;
    private static int nums;
    private static int seaMonsters;
    private static int seaHunters;
    private int currentCreature;
    private Creature[] crets;
    public int numberOfZonesX;
    private long totalTime;
    private long startTime;
    private boolean logCreaturePolls;
    
    public static Creatures getInstance() {
        if (Creatures.instance == null) {
            Creatures.instance = new Creatures();
        }
        return Creatures.instance;
    }
    
    public int getNumberOfCreatures() {
        return this.creatures.size();
    }
    
    public int getNumberOfCreaturesWithTemplate(final int templateChecked) {
        int toReturn = 0;
        for (final Creature cret : this.creatures.values()) {
            if (cret.getTemplate().getTemplateId() == templateChecked) {
                ++toReturn;
            }
        }
        return toReturn;
    }
    
    public final void setLastLed(final long creatureLed, final long leader) {
        this.ledCreatures.put(creatureLed, leader);
    }
    
    public final boolean wasLastLed(final long potentialLeader, final long creatureLed) {
        final Long lastLeader = this.ledCreatures.get(creatureLed);
        return lastLeader != null && lastLeader == potentialLeader;
    }
    
    public final void addBrand(final Brand brand) {
        this.brandedCreatures.put(brand.getCreatureId(), brand);
    }
    
    public final void setBrand(final long creatureId, final long brandid) {
        if (brandid <= 0L) {
            this.brandedCreatures.remove(creatureId);
        }
        else {
            Brand brand = this.brandedCreatures.get(creatureId);
            if (brand == null) {
                brand = new Brand(creatureId, System.currentTimeMillis(), brandid, false);
            }
            else {
                brand.setBrandId(brandid);
            }
            this.brandedCreatures.put(creatureId, brand);
        }
    }
    
    public final Brand getBrand(final long creatureId) {
        final Brand brand = this.brandedCreatures.get(creatureId);
        return brand;
    }
    
    public final boolean isBrandedBy(final long creatureId, final long brandId) {
        final Brand brand = this.brandedCreatures.get(creatureId);
        return brand != null && brand.getBrandId() == brandId;
    }
    
    public final Creature[] getBranded(final long villageId) {
        final Map<Long, Brand> removeMap = new ConcurrentHashMap<Long, Brand>();
        final Set<Creature> brandedSet = new HashSet<Creature>();
        for (final Brand b : this.brandedCreatures.values()) {
            if (b.getBrandId() == villageId) {
                try {
                    brandedSet.add(this.getCreature(b.getCreatureId()));
                }
                catch (NoSuchCreatureException e) {
                    final Long cid = new Long(b.getCreatureId());
                    if (this.isCreatureOffline(cid)) {
                        final Creature creature = this.offlineCreatures.get(cid);
                        brandedSet.add(creature);
                    }
                    else {
                        removeMap.put(b.getCreatureId(), b);
                    }
                }
            }
        }
        return brandedSet.toArray(new Creature[brandedSet.size()]);
    }
    
    public void removeBrandingFor(final int villageId) {
        for (final Brand b : this.brandedCreatures.values()) {
            if (b.getBrandId() == villageId) {
                b.deleteBrand();
            }
        }
    }
    
    public int getNumberOfNice() {
        return this.numberOfNice;
    }
    
    public int getNumberOfAgg() {
        return this.numberOfAgg;
    }
    
    public int getNumberOfTyped() {
        return this.numberOfTyped;
    }
    
    public int getNumberOfKingdomCreatures() {
        return this.kingdomCreatures;
    }
    
    public int getNumberOfSeaMonsters() {
        return Creatures.seaMonsters;
    }
    
    public int getNumberOfSeaHunters() {
        return Creatures.seaHunters;
    }
    
    private Creatures() {
        this.protectedCreatures = new ConcurrentHashMap<Long, Long>();
        this.npcs = new ConcurrentHashMap<String, Creature>();
        this.brandedCreatures = new ConcurrentHashMap<Long, Brand>();
        this.ledCreatures = new ConcurrentHashMap<Long, Long>();
        this.numberOfNice = 0;
        this.numberOfAgg = 0;
        this.numberOfTyped = 0;
        this.kingdomCreatures = 0;
        this.currentCreature = 0;
        this.numberOfZonesX = 64;
        this.totalTime = 0L;
        this.startTime = 0L;
        this.logCreaturePolls = false;
        final int numberOfCreaturesInDatabase = Math.max(this.getNumberOfCreaturesInDatabase(), 100);
        this.creatures = new ConcurrentHashMap<Long, Creature>(numberOfCreaturesInDatabase);
        this.avatars = new ConcurrentHashMap<Long, Creature>();
        this.creaturesByType = new ConcurrentHashMap<Integer, Integer>(numberOfCreaturesInDatabase);
        this.offlineCreatures = new ConcurrentHashMap<Long, Creature>();
        this.creaturePollThread = new Timer();
        this.pollTask = new PollTimerTask();
    }
    
    public final void startPollTask() {
    }
    
    public final void shutDownPolltask() {
        this.pollTask.shutDown();
    }
    
    public void sendOfflineCreatures(final Communicator c, final boolean showOwner) {
        for (final Creature cret : this.offlineCreatures.values()) {
            String dominatorName = " dominator=" + cret.dominator;
            if (showOwner) {
                try {
                    final PlayerInfo p = PlayerInfoFactory.getPlayerInfoWithWurmId(cret.dominator);
                    if (p != null) {
                        dominatorName = " dominator=" + p.getName();
                    }
                }
                catch (Exception ex) {}
            }
            else {
                dominatorName = "";
            }
            c.sendNormalServerMessage(cret.getName() + " at " + cret.getPosX() / 4.0f + ", " + cret.getPosY() / 4.0f + " loyalty " + cret.getLoyalty() + dominatorName);
        }
    }
    
    public void setCreatureDead(final Creature dead) {
        final long deadid = dead.getWurmId();
        for (final Creature creature : this.creatures.values()) {
            if (creature.opponent == dead) {
                creature.setOpponent(null);
            }
            if (creature.target == deadid) {
                creature.setTarget(-10L, true);
            }
            creature.removeTarget(deadid);
        }
        Vehicles.removeDragger(dead);
    }
    
    public void combatRound() {
        for (final Creature lCreature : this.creatures.values()) {
            lCreature.getCombatHandler().clearRound();
        }
    }
    
    private int getNumberOfCreaturesInDatabase() {
        Statement stmt = null;
        ResultSet rs = null;
        int numberOfCreatures = 0;
        try {
            final Connection dbcon = DbConnector.getCreatureDbCon();
            stmt = dbcon.createStatement();
            rs = stmt.executeQuery("SELECT COUNT(*) FROM CREATURES");
            if (rs.next()) {
                numberOfCreatures = rs.getInt(1);
            }
        }
        catch (SQLException e) {
            Creatures.logger.log(Level.WARNING, "Failed to count creatures:" + e.getMessage(), e);
        }
        finally {
            DbUtilities.closeDatabaseObjects(stmt, rs);
        }
        return numberOfCreatures;
    }
    
    private final void loadMoreStuff(final Creature toReturn) {
        try {
            toReturn.getBody().createBodyParts();
            Items.loadAllItemsForNonPlayer(toReturn, toReturn.getStatus().getInventoryId());
            Village v = Villages.getVillageForCreature(toReturn);
            if (v == null && toReturn.isNpcTrader() && toReturn.getName().startsWith("Trader")) {
                v = Villages.getVillage(toReturn.getTileX(), toReturn.getTileY(), true);
                if (v != null) {
                    try {
                        Creatures.logger.log(Level.INFO, "Adding " + toReturn.getName() + " as citizen to " + v.getName());
                        v.addCitizen(toReturn, v.getRoleForStatus((byte)3));
                    }
                    catch (IOException iox) {
                        Creatures.logger.log(Level.INFO, iox.getMessage());
                    }
                    catch (NoSuchRoleException nsx) {
                        Creatures.logger.log(Level.INFO, nsx.getMessage());
                    }
                }
            }
            toReturn.setCitizenVillage(v);
            toReturn.postLoad();
            if (toReturn.getTemplate().getTemplateId() == 46 || toReturn.getTemplate().getTemplateId() == 47) {
                Zones.setHasLoadedChristmas(true);
                if (!WurmCalendar.isChristmas()) {
                    this.permanentlyDelete(toReturn);
                }
                else if (toReturn.getTemplate().getTemplateId() == 46) {
                    if (!Servers.localServer.HOMESERVER && toReturn.getKingdomId() == 2) {
                        Zones.santaMolRehan = toReturn;
                    }
                    else if (Servers.localServer.HOMESERVER && toReturn.getKingdomId() == 4) {
                        Zones.santas.put(toReturn.getWurmId(), toReturn);
                    }
                    else {
                        Zones.santa = toReturn;
                    }
                }
                else {
                    Zones.evilsanta = toReturn;
                }
            }
        }
        catch (Exception ex) {
            Creatures.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
    }
    
    private static final void initializeCreature(final String templateName, final ResultSet rs, final Creature statusHolder) {
        final long id = statusHolder.getWurmId();
        statusHolder.getStatus().setPosition(CreaturePos.getPosition(id));
        try {
            statusHolder.getStatus().setStatusExists(true);
            statusHolder.getStatus().template = CreatureTemplateFactory.getInstance().getTemplate(templateName);
            statusHolder.template = statusHolder.getStatus().template;
            statusHolder.getStatus().bodyId = rs.getLong("BODYID");
            (statusHolder.getStatus().body = BodyFactory.getBody(statusHolder, statusHolder.getStatus().template.getBodyType(), statusHolder.getStatus().template.getCentimetersHigh(), statusHolder.getStatus().template.getCentimetersLong(), statusHolder.getStatus().template.getCentimetersWide())).setCentimetersLong(rs.getShort("CENTIMETERSLONG"));
            statusHolder.getStatus().body.setCentimetersHigh(rs.getShort("CENTIMETERSHIGH"));
            statusHolder.getStatus().body.setCentimetersWide(rs.getShort("CENTIMETERSWIDE"));
            statusHolder.getStatus().sex = rs.getByte("SEX");
            statusHolder.getStatus().modtype = rs.getByte("TYPE");
            final String name = rs.getString("NAME");
            statusHolder.setName(name);
            statusHolder.getStatus().inventoryId = rs.getLong("INVENTORYID");
            statusHolder.getStatus().stamina = (rs.getShort("STAMINA") & 0xFFFF);
            statusHolder.getStatus().hunger = (rs.getShort("HUNGER") & 0xFFFF);
            statusHolder.getStatus().thirst = (rs.getShort("THIRST") & 0xFFFF);
            statusHolder.getStatus().buildingId = rs.getLong("BUILDINGID");
            statusHolder.getStatus().kingdom = rs.getByte("KINGDOM");
            statusHolder.getStatus().dead = rs.getBoolean("DEAD");
            statusHolder.getStatus().stealth = rs.getBoolean("STEALTH");
            statusHolder.getStatus().age = rs.getInt("AGE");
            statusHolder.getStatus().fat = rs.getByte("FAT");
            statusHolder.getStatus().lastPolledAge = rs.getLong("LASTPOLLEDAGE");
            statusHolder.dominator = rs.getLong("DOMINATOR");
            statusHolder.getStatus().reborn = rs.getBoolean("REBORN");
            statusHolder.getStatus().loyalty = rs.getFloat("LOYALTY");
            statusHolder.getStatus().lastPolledLoyalty = rs.getLong("LASTPOLLEDLOYALTY");
            statusHolder.getStatus().detectInvisCounter = rs.getShort("DETECTIONSECS");
            statusHolder.getStatus().traits = rs.getLong("TRAITS");
            if (statusHolder.getStatus().traits != 0L) {
                statusHolder.getStatus().setTraitBits(statusHolder.getStatus().traits);
            }
            statusHolder.getStatus().mother = rs.getLong("MOTHER");
            statusHolder.getStatus().father = rs.getLong("FATHER");
            statusHolder.getStatus().nutrition = rs.getFloat("NUTRITION");
            statusHolder.getStatus().disease = rs.getByte("DISEASE");
            if (statusHolder.getStatus().buildingId != -10L) {
                try {
                    final Structure struct = Structures.getStructure(statusHolder.getStatus().buildingId);
                    if (!struct.isFinalFinished()) {
                        statusHolder.setStructure(struct);
                    }
                    else {
                        statusHolder.getStatus().buildingId = -10L;
                    }
                }
                catch (NoSuchStructureException nss) {
                    statusHolder.getStatus().buildingId = -10L;
                    Creatures.logger.log(Level.INFO, "Could not find structure for " + statusHolder.getName());
                    statusHolder.setStructure(null);
                }
            }
            statusHolder.getStatus().lastGroomed = rs.getLong("LASTGROOMED");
            statusHolder.getStatus().offline = rs.getBoolean("OFFLINE");
            statusHolder.getStatus().stayOnline = rs.getBoolean("STAYONLINE");
            final String petName = rs.getString("PETNAME");
            statusHolder.setPetName(petName);
            statusHolder.calculateSize();
            statusHolder.vehicle = rs.getLong("VEHICLE");
            statusHolder.seatType = rs.getByte("SEAT_TYPE");
            if (statusHolder.vehicle > 0L) {
                Creatures.rideCreatures.put(id, statusHolder);
            }
        }
        catch (Exception ex) {
            Creatures.logger.log(Level.WARNING, "Failed to load creature " + id + " " + ex.getMessage(), ex);
        }
    }
    
    public int loadAllCreatures() throws NoSuchCreatureException {
        Brand.loadAllBrands();
        Creatures.loading = true;
        Offspring.loadAllOffspring();
        this.loadAllProtectedCreatures();
        final long lNow2 = System.nanoTime();
        Creatures.logger.info("Loading all skills for creatures");
        try {
            Skills.loadAllCreatureSkills();
        }
        catch (Exception ex) {
            Creatures.logger.log(Level.INFO, "Failed Loading creature skills.", ex);
            System.exit(0);
        }
        Creatures.logger.log(Level.INFO, "Loaded creature skills. That took " + (System.nanoTime() - lNow2) / 1000000.0f);
        Creatures.logger.info("Loading Creatures");
        final long lNow3 = System.nanoTime();
        long cpS = 0L;
        long cpOne = 0L;
        final long cpTwo = 0L;
        final long cpThree = 0L;
        final long cpFour = 0L;
        Creature toReturn = null;
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        final Set<Creature> toRemove = new HashSet<Creature>();
        Creatures.rideCreatures = new ConcurrentHashMap<Long, Creature>();
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM CREATURES");
            rs = ps.executeQuery();
            while (rs.next()) {
                cpS = System.nanoTime();
                try {
                    final String templateName = rs.getString("TEMPLATENAME");
                    if (templateName.equalsIgnoreCase("human") || templateName.equalsIgnoreCase("npc human")) {
                        toReturn = new Npc(rs.getLong("WURMID"));
                    }
                    else {
                        toReturn = new Creature(rs.getLong("WURMID"));
                    }
                    initializeCreature(templateName, rs, toReturn);
                    toReturn.loadTemplate();
                    if (toReturn.isFish()) {
                        Creatures.logger.info("Fish removed " + toReturn.getName());
                        this.permanentlyDelete(toReturn);
                    }
                    else if ((!toReturn.isUnique() && (toReturn.isOffline() || toReturn.isDominated()) && !toReturn.isStayonline()) || (!Constants.loadNpcs && toReturn.isNpc())) {
                        this.addOfflineCreature(toReturn);
                        this.addCreature(toReturn, true);
                        toRemove.add(toReturn);
                    }
                    else if (!this.addCreature(toReturn, false)) {
                        this.permanentlyDelete(toReturn);
                    }
                    cpOne += System.nanoTime() - cpS;
                    cpS = System.nanoTime();
                }
                catch (Exception ex2) {
                    Creatures.logger.log(Level.WARNING, "Failed to load creature: " + toReturn + "due to " + ex2.getMessage(), ex2);
                }
            }
            for (final Creature rider : Creatures.rideCreatures.values()) {
                final long vehicleId = rider.vehicle;
                final byte seatType = rider.seatType;
                rider.vehicle = -10L;
                rider.seatType = -1;
                try {
                    Vehicle vehic = null;
                    Item vehicle = null;
                    Creature creature = null;
                    if (WurmId.getType(vehicleId) == 1) {
                        creature = Server.getInstance().getCreature(vehicleId);
                        vehic = Vehicles.getVehicle(creature);
                    }
                    else {
                        vehicle = Items.getItem(vehicleId);
                        vehic = Vehicles.getVehicle(vehicle);
                    }
                    if (vehic == null) {
                        continue;
                    }
                    try {
                        if (seatType == -1 || seatType == 2) {
                            if (vehic.addDragger(rider)) {
                                rider.setHitched(vehic, true);
                                final Seat driverseat = vehic.getPilotSeat();
                                if (driverseat != null) {
                                    final float _r = (-vehicle.getRotation() + 180.0f) * 3.1415927f / 180.0f;
                                    final float _s = (float)Math.sin(_r);
                                    final float _c = (float)Math.cos(_r);
                                    final float xo = _s * -driverseat.offx - _c * -driverseat.offy;
                                    final float yo = _c * -driverseat.offx + _s * -driverseat.offy;
                                    final float nPosX = rider.getStatus().getPositionX() - xo;
                                    final float nPosY = rider.getStatus().getPositionY() - yo;
                                    final float nPosZ = rider.getStatus().getPositionZ() - driverseat.offz;
                                    rider.getStatus().setPositionX(nPosX);
                                    rider.getStatus().setPositionY(nPosY);
                                    rider.getStatus().setRotation(-vehicle.getRotation() + 180.0f);
                                    rider.getMovementScheme().setPosition(rider.getStatus().getPositionX(), rider.getStatus().getPositionY(), nPosZ, rider.getStatus().getRotation(), rider.getLayer());
                                }
                            }
                        }
                        else if (seatType == 0 || seatType == 1) {
                            for (int x = 0; x < vehic.seats.length; ++x) {
                                if (vehic.seats[x].getType() == seatType && (!vehic.seats[x].isOccupied() || vehic.seats[x].occupant == rider.getWurmId())) {
                                    vehic.seats[x].occupy(vehic, rider);
                                    if (seatType == 0) {
                                        vehic.pilotId = rider.getWurmId();
                                        rider.setVehicleCommander(true);
                                    }
                                    final MountAction m = new MountAction(creature, vehicle, vehic, x, seatType == 0, vehic.seats[x].offz);
                                    rider.setMountAction(m);
                                    rider.setVehicle(vehicleId, true, seatType);
                                    break;
                                }
                            }
                        }
                    }
                    catch (NoSuchPlayerException nsi) {
                        Creatures.logger.log(Level.INFO, "Item " + vehicleId + " missing for hitched " + rider.getWurmId() + " " + rider.getName());
                    }
                }
                catch (NoSuchItemException ex3) {}
                catch (NoSuchPlayerException ex4) {}
                catch (NoSuchCreatureException ex5) {}
            }
            Creatures.rideCreatures = null;
            final long lNow4 = System.nanoTime();
            Creatures.logger.info("Loading all items for creatures");
            Items.loadAllCreatureItems();
            Creatures.logger.log(Level.INFO, "Loaded creature items. That took " + (System.nanoTime() - lNow4) / 1000000.0f + " ms for " + Items.getNumItems() + " items and " + Items.getNumCoins() + " coins.");
            for (final Creature creature2 : this.creatures.values()) {
                Skills.fillCreatureTempSkills(creature2);
                this.loadMoreStuff(creature2);
            }
            for (final Creature creature2 : toRemove) {
                this.loadMoreStuff(creature2);
                this.removeCreature(creature2);
                creature2.getStatus().offline = true;
            }
        }
        catch (SQLException sqx) {
            Creatures.logger.log(Level.WARNING, "Failed to load creatures:" + sqx.getMessage(), sqx);
            throw new NoSuchCreatureException(sqx);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
            Creatures.logger.log(Level.INFO, "Loaded " + this.getNumberOfCreatures() + " creatures. Destroyed " + Creatures.destroyedCaveCrets + ". That took " + (System.nanoTime() - lNow3) / 1000000.0f + " ms. CheckPoints cp1=" + cpOne / 1000000.0f + ", cp2=" + 0.0f + ", cp3=" + 0.0f + ", cp4=" + 0.0f);
            Creatures.logger.log(Level.INFO, "Loaded items for creature. CheckPoints cp1=" + Items.getCpOne() / 1000000.0f + ", cp2=" + Items.getCpTwo() / 1000000.0f + ", cp3=" + Items.getCpThree() / 1000000.0f + ", cp4=" + Items.getCpFour() / 1000000.0f);
        }
        Creatures.loading = false;
        Items.clearCreatureLoadMap();
        Skills.clearCreatureLoadMap();
        Offspring.resetOffspringCounters();
        return this.getNumberOfCreatures();
    }
    
    public boolean creatureWithTemplateExists(final int templateId) {
        for (final Creature lCreature : this.creatures.values()) {
            if (lCreature.template.getTemplateId() == templateId) {
                return true;
            }
        }
        return false;
    }
    
    public Creature getUniqueCreatureWithTemplate(final int templateId) {
        final List<Creature> foundCreatures = new ArrayList<Creature>();
        for (final Creature lCreature : this.creatures.values()) {
            if (lCreature.template.getTemplateId() == templateId) {
                foundCreatures.add(lCreature);
            }
        }
        if (foundCreatures.size() == 0) {
            return null;
        }
        if (foundCreatures.size() == 1) {
            return foundCreatures.get(0);
        }
        throw new UnsupportedOperationException("Multiple creatures found");
    }
    
    public Creature getCreature(final long id) throws NoSuchCreatureException {
        Creature toReturn = null;
        final Long cid = new Long(id);
        if (!this.creatures.containsKey(cid)) {
            throw new NoSuchCreatureException("No such creature for id: " + id);
        }
        toReturn = this.creatures.get(cid);
        if (toReturn == null) {
            throw new NoSuchCreatureException("No creature with id " + id);
        }
        return toReturn;
    }
    
    public Creature getCreatureOrNull(final long id) {
        try {
            return this.getCreature(id);
        }
        catch (NoSuchCreatureException n) {
            return null;
        }
    }
    
    private void removeTarget(final long id) {
        for (final Creature cret : this.creatures.values()) {
            if (cret.target == id) {
                cret.setTarget(-10L, true);
            }
        }
        for (final Creature cret : this.offlineCreatures.values()) {
            if (cret.target == id) {
                cret.setTarget(-10L, true);
            }
        }
        final Player[] players2;
        final Player[] players = players2 = Players.getInstance().getPlayers();
        for (final Player lPlayer : players2) {
            if (lPlayer.target == id) {
                lPlayer.setTarget(-10L, true);
            }
        }
    }
    
    public void setCreatureOffline(final Creature creature) {
        try {
            final Creature[] watchers2;
            final Creature[] watchers = watchers2 = creature.getInventory().getWatchers();
            for (final Creature lWatcher : watchers2) {
                creature.getInventory().removeWatcher(lWatcher, true);
            }
        }
        catch (NoSuchCreatureException ex) {}
        catch (Exception nsc) {
            Creatures.logger.log(Level.WARNING, creature.getName() + " " + nsc.getMessage(), nsc);
        }
        try {
            final Creature[] watchers3;
            final Creature[] watchers = watchers3 = creature.getBody().getBodyItem().getWatchers();
            for (final Creature lWatcher : watchers3) {
                creature.getBody().getBodyItem().removeWatcher(lWatcher, true);
            }
        }
        catch (NoSuchCreatureException ex2) {}
        catch (Exception nsc) {
            Creatures.logger.log(Level.WARNING, creature.getName() + " " + nsc.getMessage(), nsc);
        }
        creature.clearOrders();
        creature.setLeader(null);
        creature.destroyVisionArea();
        this.removeTarget(creature.getWurmId());
        this.removeCreature(creature);
        this.addOfflineCreature(creature);
        creature.setPathing(false, true);
        creature.setOffline(true);
        try {
            creature.getStatus().savePosition(creature.getWurmId(), false, -10, true);
        }
        catch (IOException iox) {
            Creatures.logger.log(Level.WARNING, iox.getMessage(), iox);
        }
    }
    
    private final void saveCreatureProtected(final long creatureId, final long protector) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("INSERT INTO PROTECTED (WURMID,PLAYERID) VALUES(?,?)");
            ps.setLong(1, creatureId);
            ps.setLong(2, protector);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Creatures.logger.log(Level.WARNING, "Failed to insert creature protected " + creatureId, sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private final void deleteCreatureProtected(final long creatureId) {
        Connection dbcon = null;
        PreparedStatement ps = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("DELETE FROM PROTECTED WHERE WURMID=?");
            ps.setLong(1, creatureId);
            ps.executeUpdate();
        }
        catch (SQLException sqex) {
            Creatures.logger.log(Level.WARNING, "Failed to delete creature protected " + creatureId, sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    private final void loadAllProtectedCreatures() {
        Connection dbcon = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            ps = dbcon.prepareStatement("SELECT * FROM PROTECTED");
            rs = ps.executeQuery();
            while (rs.next()) {
                this.protectedCreatures.put(rs.getLong("WURMID"), rs.getLong("PLAYERID"));
            }
        }
        catch (SQLException sqex) {
            Creatures.logger.log(Level.WARNING, "Failed to load creatures protected.", sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, rs);
            DbConnector.returnConnection(dbcon);
        }
    }
    
    public final int getNumberOfCreaturesProtectedBy(final long protector) {
        int numsToReturn = 0;
        for (final Long l : this.protectedCreatures.values()) {
            if (l == protector) {
                ++numsToReturn;
            }
        }
        return numsToReturn;
    }
    
    public final int setNoCreaturesProtectedBy(final long protector) {
        int numsToReturn = 0;
        final LinkedList<Long> toRemove = new LinkedList<Long>();
        for (final Map.Entry<Long, Long> l : this.protectedCreatures.entrySet()) {
            if (l.getValue() == protector) {
                toRemove.add(l.getKey());
            }
        }
        for (final Long i : toRemove) {
            ++numsToReturn;
            this.deleteCreatureProtected(i);
            this.protectedCreatures.remove(i);
        }
        return numsToReturn;
    }
    
    public final void setCreatureProtected(final Creature creature, final long protector, final boolean setProtected) {
        if (setProtected) {
            if (!this.protectedCreatures.containsKey(creature.getWurmId())) {
                this.saveCreatureProtected(creature.getWurmId(), protector);
            }
            this.protectedCreatures.put(creature.getWurmId(), protector);
        }
        else if (this.protectedCreatures.containsKey(creature.getWurmId())) {
            this.deleteCreatureProtected(creature.getWurmId());
            this.protectedCreatures.remove(creature.getWurmId());
        }
    }
    
    public final long getCreatureProtectorFor(final long wurmId) {
        if (this.protectedCreatures.containsKey(wurmId)) {
            return this.protectedCreatures.get(wurmId);
        }
        return -10L;
    }
    
    public final Creature[] getProtectedCreaturesFor(final long playerId) {
        final Set<Creature> protectedSet = new HashSet<Creature>();
        for (final Map.Entry<Long, Long> entry : this.protectedCreatures.entrySet()) {
            if (entry.getValue() == playerId) {
                try {
                    protectedSet.add(this.getCreature(entry.getKey()));
                }
                catch (NoSuchCreatureException e) {
                    Creatures.logger.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }
        return protectedSet.toArray(new Creature[protectedSet.size()]);
    }
    
    public final boolean isCreatureProtected(final long wurmId) {
        return this.protectedCreatures.containsKey(wurmId);
    }
    
    public final long getCreatureProctector(final Creature creature) {
        final Long whom = this.protectedCreatures.get(creature.getWurmId());
        if (whom != null) {
            return whom;
        }
        return -10L;
    }
    
    public void pollOfflineCreatures() {
        final Set<Creature> toReturn = new HashSet<Creature>();
        final Iterator<Creature> creatureIterator = this.offlineCreatures.values().iterator();
        while (creatureIterator.hasNext()) {
            final Creature offline = creatureIterator.next();
            if (offline.pollAge()) {
                if (Creatures.logger.isLoggable(Level.FINER)) {
                    Creatures.logger.finer(offline.getWurmId() + ", " + offline.getName() + " is dead.");
                }
                creatureIterator.remove();
            }
            else {
                offline.pollLoyalty();
                if (offline.dominator != -10L || (offline.isNpc() && !Constants.loadNpcs)) {
                    continue;
                }
                toReturn.add(offline);
            }
        }
        for (final Creature c : toReturn) {
            try {
                Creatures.logger.log(Level.INFO, "Returning " + c.getName() + " from being offline due to no loyalty.");
                this.loadOfflineCreature(c.getWurmId());
            }
            catch (NoSuchCreatureException nsc) {
                Creatures.logger.log(Level.WARNING, nsc.getMessage());
            }
        }
    }
    
    public Creature loadOfflineCreature(final long creatureId) throws NoSuchCreatureException {
        final Long cid = new Long(creatureId);
        if (this.isCreatureOffline(cid)) {
            final Creature creature = this.offlineCreatures.remove(cid);
            creature.setOffline(false);
            creature.setLeader(null);
            creature.setCitizenVillage(Villages.getVillageForCreature(creature));
            creature.getStatus().visible = true;
            try {
                creature.createVisionArea();
            }
            catch (Exception ex) {
                Creatures.logger.log(Level.WARNING, "Problem creating VisionArea for creature with id " + creatureId + "due to " + ex.getMessage(), ex);
            }
            this.addCreature(creature, false);
            return creature;
        }
        throw new NoSuchCreatureException("No such creature with id " + creatureId);
    }
    
    public boolean isCreatureOffline(final Long aCreatureId) {
        return this.offlineCreatures.containsKey(aCreatureId);
    }
    
    public final long getPetId(final long dominatorId) {
        for (final Creature c : this.offlineCreatures.values()) {
            if (c.dominator == dominatorId) {
                return c.getWurmId();
            }
        }
        for (final Creature c : this.creatures.values()) {
            if (c.dominator == dominatorId) {
                return c.getWurmId();
            }
        }
        return -10L;
    }
    
    public void returnCreaturesForPlayer(final long playerId) {
        final Set<Creature> toLoad = new HashSet<Creature>();
        for (final Creature c : this.offlineCreatures.values()) {
            if (c.dominator == playerId) {
                toLoad.add(c);
                c.setLoyalty(0.0f);
                c.setDominator(-10L);
            }
        }
        for (final Creature c : toLoad) {
            try {
                Creatures.logger.log(Level.INFO, "Returning " + c.getName() + " from being offline due to no loyalty.");
                this.loadOfflineCreature(c.getWurmId());
            }
            catch (NoSuchCreatureException nsc) {
                Creatures.logger.log(Level.WARNING, nsc.getMessage());
            }
        }
    }
    
    public final Creature getNpc(final String name) {
        return this.npcs.get(LoginHandler.raiseFirstLetter(name));
    }
    
    public final Npc[] getNpcs() {
        return this.npcs.values().toArray(new Npc[this.npcs.size()]);
    }
    
    public void removeCreature(final Creature creature) {
        if (creature.isNpc()) {
            this.npcs.remove(creature.getName());
        }
        this.creatures.remove(new Long(creature.getWurmId()));
        this.avatars.remove(new Long(creature.getWurmId()));
        this.removeCreatureByType(creature.getTemplate().getTemplateId());
    }
    
    public boolean addCreature(final Creature creature, final boolean offline) {
        return this.addCreature(creature, offline, true);
    }
    
    void sendToWorld(final Creature creature) {
        try {
            Zones.getZone(creature.getTileX(), creature.getTileY(), creature.isOnSurface()).addCreature(creature.getWurmId());
        }
        catch (NoSuchCreatureException nex) {
            Creatures.logger.log(Level.WARNING, "Failed to add creature ID: " + creature.getWurmId() + " due to " + nex.getMessage(), nex);
        }
        catch (NoSuchZoneException sex) {
            Creatures.logger.log(Level.WARNING, "Failed to add creature ID: " + creature.getWurmId() + " due to " + sex.getMessage(), sex);
        }
        catch (NoSuchPlayerException nsp) {
            Creatures.logger.log(Level.WARNING, "Failed to add creature ID: " + creature.getWurmId() + " due to " + nsp.getMessage(), nsp);
        }
    }
    
    final void addCreatureByType(final int creatureType) {
        final Integer val = this.creaturesByType.get(creatureType);
        if (val == null) {
            this.creaturesByType.put(creatureType, 1);
        }
        else {
            this.creaturesByType.put(creatureType, val + 1);
        }
    }
    
    final void removeCreatureByType(final int creatureType) {
        final Integer val = this.creaturesByType.get(creatureType);
        if (val == null || val == 0) {
            this.creaturesByType.put(creatureType, 0);
        }
        else {
            this.creaturesByType.put(creatureType, val - 1);
        }
    }
    
    public final int getCreatureByType(final int creatureType) {
        final Integer val = this.creaturesByType.get(creatureType);
        if (val == null || val == 0) {
            return 0;
        }
        return val;
    }
    
    public final Map<Integer, Integer> getCreatureTypeList() {
        return this.creaturesByType;
    }
    
    public final int getOpenSpawnSlotsForCreatureType(final int creatureType) {
        final int currentCount = this.getCreatureByType(creatureType);
        try {
            final CreatureTemplate ctemplate = CreatureTemplateFactory.getInstance().getTemplate(creatureType);
            final int maxByPercent = (int)(Servers.localServer.maxCreatures * ctemplate.getMaxPercentOfCreatures());
            final int slotsOpenForPercent = Math.max(maxByPercent - currentCount, 0);
            if (!ctemplate.usesMaxPopulation()) {
                return slotsOpenForPercent;
            }
            final int maxPop = ctemplate.getMaxPopulationOfCreatures();
            final int slotsByPopulation = Math.max(maxPop - currentCount, 0);
            if (maxPop <= maxByPercent) {
                return slotsByPopulation;
            }
            return slotsOpenForPercent;
        }
        catch (NoSuchCreatureTemplateException e) {
            Creatures.logger.log(Level.WARNING, "Unable to find creature template with id: " + creatureType + ".", e);
            return 0;
        }
    }
    
    boolean addCreature(final Creature creature, final boolean offline, final boolean sendToWorld) {
        this.creatures.put(new Long(creature.getWurmId()), creature);
        if (creature.isNpc()) {
            this.npcs.put(LoginHandler.raiseFirstLetter(creature.getName()), creature);
        }
        if (creature.isAvatar()) {
            this.avatars.put(new Long(creature.getWurmId()), creature);
        }
        this.addCreatureByType(creature.getTemplate().getTemplateId());
        if (!creature.isDead()) {
            try {
                if (!creature.isOnSurface() && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(creature.getTileX(), creature.getTileY())))) {
                    creature.setLayer(0, false);
                    Creatures.logger.log(Level.INFO, "Changed layer to surface for ID: " + creature.getWurmId() + " - " + creature.getName() + '.');
                }
                if (!offline) {
                    if (!creature.isFloating()) {
                        if (creature.isMonster() || creature.isAggHuman()) {
                            ++this.numberOfAgg;
                        }
                        else {
                            ++this.numberOfNice;
                        }
                    }
                    if (creature.getStatus().modtype > 0) {
                        ++this.numberOfTyped;
                    }
                    if (creature.isAggWhitie() || creature.isDefendKingdom()) {
                        ++this.kingdomCreatures;
                    }
                    if (creature.isFloating() && !creature.isSpiritGuard()) {
                        if (creature.getTemplate().getTemplateId() == 70) {
                            ++Creatures.seaMonsters;
                        }
                        else {
                            ++Creatures.seaHunters;
                        }
                    }
                    if (sendToWorld) {
                        final int numsOnTile = Zones.getZone(creature.getTileX(), creature.getTileY(), creature.isOnSurface()).addCreature(creature.getWurmId());
                        if (Creatures.loading && numsOnTile > 2 && !creature.isHorse() && !creature.isOnSurface() && !creature.isDominated() && !creature.isUnique() && !creature.isSalesman() && !creature.isWagoner() && !creature.hasTrait(63) && !creature.isHitched() && creature.getBody().getAllItems().length == 0 && !creature.isBranded() && !creature.isCaredFor()) {
                            Zones.getZone(creature.getTileX(), creature.getTileY(), creature.isOnSurface()).deleteCreature(creature, true);
                            Creatures.logger.log(Level.INFO, "Destroying " + creature.getName() + ", " + creature.getWurmId() + " at cave " + creature.getTileX() + ", " + creature.getTileY() + " - overcrowded.");
                            ++Creatures.destroyedCaveCrets;
                            return false;
                        }
                    }
                }
            }
            catch (NoSuchCreatureException nex) {
                Creatures.logger.log(Level.WARNING, "Failed to add creature ID: " + creature.getWurmId() + " due to " + nex.getMessage(), nex);
                this.creatures.remove(new Long(creature.getWurmId()));
                this.avatars.remove(new Long(creature.getWurmId()));
                this.removeCreatureByType(creature.getTemplate().getTemplateId());
                return false;
            }
            catch (NoSuchZoneException sex) {
                Creatures.logger.log(Level.WARNING, "Failed to add creature ID: " + creature.getWurmId() + " due to " + sex.getMessage(), sex);
                this.creatures.remove(new Long(creature.getWurmId()));
                this.avatars.remove(new Long(creature.getWurmId()));
                this.removeCreatureByType(creature.getTemplate().getTemplateId());
                return false;
            }
            catch (NoSuchPlayerException nsp) {
                Creatures.logger.log(Level.WARNING, "Failed to add creature ID: " + creature.getWurmId() + " due to " + nsp.getMessage(), nsp);
                this.creatures.remove(new Long(creature.getWurmId()));
                this.avatars.remove(new Long(creature.getWurmId()));
                this.removeCreatureByType(creature.getTemplate().getTemplateId());
                return false;
            }
        }
        return true;
    }
    
    public static final boolean isLoading() {
        return Creatures.loading;
    }
    
    private void addOfflineCreature(final Creature creature) {
        this.offlineCreatures.put(new Long(creature.getWurmId()), creature);
        if (!creature.isDead() && !creature.isOnSurface() && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(creature.getTileX(), creature.getTileY())))) {
            creature.setLayer(0, false);
            Creatures.logger.log(Level.INFO, "Changed layer to surface for ID: " + creature.getWurmId() + " - " + creature.getName() + '.');
        }
    }
    
    public Creature[] getCreatures() {
        final Creature[] toReturn = new Creature[this.creatures.size()];
        return this.creatures.values().toArray(toReturn);
    }
    
    public Creature[] getAvatars() {
        final Creature[] toReturn = new Creature[this.avatars.size()];
        return this.avatars.values().toArray(toReturn);
    }
    
    public void saveCreatures() {
        final Creature[] creatarr = this.getCreatures();
        Exception error = null;
        int numsSaved = 0;
        for (final Creature creature : creatarr) {
            try {
                if (creature.getStatus().save()) {
                    ++numsSaved;
                }
            }
            catch (IOException iox) {
                error = iox;
            }
        }
        Creatures.logger.log(Level.INFO, "Saved " + numsSaved + " creature statuses.");
        if (error != null) {
            Creatures.logger.log(Level.INFO, "An error occurred while saving creatures:" + error.getMessage(), error);
        }
    }
    
    void permanentlyDelete(final Creature creature) {
        this.removeCreature(creature);
        if (!creature.isFloating()) {
            if (creature.isMonster() || creature.isAggHuman()) {
                --this.numberOfAgg;
            }
            else {
                --this.numberOfNice;
            }
        }
        if (creature.getStatus().modtype > 0) {
            --this.numberOfTyped;
        }
        if (creature.isAggWhitie() || creature.isDefendKingdom()) {
            --this.kingdomCreatures;
        }
        if (creature.isFloating()) {
            if (creature.getTemplate().getTemplateId() == 70) {
                --Creatures.seaMonsters;
            }
            else {
                --Creatures.seaHunters;
            }
        }
        final Brand brand = this.getBrand(creature.getWurmId());
        if (brand != null) {
            brand.deleteBrand();
            this.setBrand(creature.getWurmId(), 0L);
        }
        this.setCreatureProtected(creature, -10L, false);
        CreaturePos.delete(creature.getWurmId());
        MissionTargets.destroyMissionTarget(creature.getWurmId(), true);
        Connection dbcon = null;
        PreparedStatement ps = null;
        Connection dbcon2 = null;
        PreparedStatement ps2 = null;
        try {
            dbcon = DbConnector.getCreatureDbCon();
            if (Constants.useSplitCreaturesTable) {
                ps = dbcon.prepareStatement("DELETE FROM CREATURES_BASE WHERE WURMID=?");
                ps.setLong(1, creature.getWurmId());
                ps.executeUpdate();
                DbUtilities.closeDatabaseObjects(ps, null);
                ps = dbcon.prepareStatement("DELETE FROM CREATURES_POS WHERE WURMID=?");
                ps.setLong(1, creature.getWurmId());
                ps.executeUpdate();
                DbUtilities.closeDatabaseObjects(ps, null);
                ps = dbcon.prepareStatement("DELETE FROM SKILLS WHERE OWNER=?");
                ps.setLong(1, creature.getWurmId());
                ps.executeUpdate();
                DbUtilities.closeDatabaseObjects(ps, null);
            }
            else {
                ps = dbcon.prepareStatement("DELETE FROM CREATURES WHERE WURMID=?");
                ps.setLong(1, creature.getWurmId());
                ps.executeUpdate();
                DbUtilities.closeDatabaseObjects(ps, null);
                ps = dbcon.prepareStatement("DELETE FROM SKILLS WHERE OWNER=?");
                ps.setLong(1, creature.getWurmId());
                ps.executeUpdate();
                DbUtilities.closeDatabaseObjects(ps, null);
            }
            dbcon2 = DbConnector.getItemDbCon();
            ps2 = dbcon2.prepareStatement("DELETE FROM BODYPARTS WHERE OWNERID=?");
            ps2.setLong(1, creature.getWurmId());
            ps2.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps2, null);
            ps2 = dbcon2.prepareStatement("DELETE FROM ITEMS WHERE OWNERID=?");
            ps2.setLong(1, creature.getWurmId());
            ps2.executeUpdate();
            DbUtilities.closeDatabaseObjects(ps2, null);
        }
        catch (SQLException sqex) {
            Creatures.logger.log(Level.WARNING, "Failed to delete creature " + creature, sqex);
        }
        finally {
            DbUtilities.closeDatabaseObjects(ps, null);
            DbUtilities.closeDatabaseObjects(ps2, null);
            DbConnector.returnConnection(dbcon);
            DbConnector.returnConnection(dbcon2);
        }
        if (creature.isUnique() && creature.getTemplate() != null) {
            final Den d = Dens.getDen(Integer.valueOf(creature.getTemplate().getTemplateId()));
            if (d != null && !getInstance().creatureWithTemplateExists(creature.getTemplate().getTemplateId())) {
                Dens.deleteDen(creature.getTemplate().getTemplateId());
            }
        }
    }
    
    int resetGuardSkills() {
        int count = 0;
        for (final Creature cret : this.creatures.values()) {
            if (cret.isSpiritGuard()) {
                try {
                    cret.skills.delete();
                    cret.skills.clone(cret.getSkills().getSkills());
                    cret.skills.save();
                    ++count;
                }
                catch (Exception ex) {
                    Creatures.logger.log(Level.WARNING, cret.getWurmId() + ":" + ex.getMessage(), ex);
                }
            }
        }
        Creatures.logger.log(Level.INFO, "Reset " + count + " guards skills.");
        return count;
    }
    
    final Creature[] getCreaturesWithName(String name) {
        name = name.toLowerCase();
        final Set<Creature> toReturn = new HashSet<Creature>();
        for (final Creature cret : this.creatures.values()) {
            if (cret.getName().toLowerCase().indexOf(name) >= 0) {
                toReturn.add(cret);
            }
        }
        return toReturn.toArray(new Creature[toReturn.size()]);
    }
    
    public Creature[] getHorsesWithName(final String aName) {
        final String name = aName.toLowerCase();
        final Set<Creature> toReturn = new HashSet<Creature>();
        for (final Creature cret : this.creatures.values()) {
            if (cret.getTemplate().isHorse && cret.getName().toLowerCase().indexOf(name) >= 0) {
                toReturn.add(cret);
            }
        }
        return toReturn.toArray(new Creature[toReturn.size()]);
    }
    
    public static boolean shouldDestroy(final Creature c) {
        final int tid = c.getTemplate().getTemplateId();
        if (Creatures.nums < 7000 && (tid == 15 || tid == 54 || tid == 25 || tid == 44 || tid == 52 || tid == 55 || tid == 10 || tid == 42 || tid == 12 || tid == 45 || tid == 48 || tid == 59 || tid == 13 || tid == 21)) {
            ++Creatures.nums;
            return true;
        }
        return false;
    }
    
    public static void destroySwimmers() {
        final Creature[] creatures;
        final Creature[] crets = creatures = getInstance().getCreatures();
        for (final Creature lCret : creatures) {
            if (shouldDestroy(lCret)) {
                lCret.destroy();
            }
        }
    }
    
    public static void createLightAvengers() {
        int numsa = 0;
        while (numsa < 20) {
            final int x = Zones.safeTileX(Server.rand.nextInt(Zones.worldTileSizeX));
            final int y = Zones.safeTileY(Server.rand.nextInt(Zones.worldTileSizeY));
            final int t = Server.surfaceMesh.getTile(x, y);
            if (Tiles.decodeHeight(t) > 0) {
                byte deity = 1;
                if (Tiles.decodeHeightAsFloat(t) > 100.0f) {
                    deity = 2;
                }
                try {
                    final CreatureTemplate ctemplate = CreatureTemplateFactory.getInstance().getTemplate(68);
                    final Creature cret = Creature.doNew(68, (x << 2) + 2.0f, (y << 2) + 2.0f, Server.rand.nextInt(360), 0, "", ctemplate.getSex(), (byte)0);
                    cret.setDeity(Deities.getDeity(deity));
                    ++numsa;
                }
                catch (Exception ex) {
                    Creatures.logger.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
    }
    
    public final void togglePollTaskLog() {
        this.setLog(!this.isLog());
    }
    
    public void pollAllCreatures(final int num) {
        if (num == 1 || this.crets == null) {
            if (this.crets != null) {
                if (this.isLog() && this.totalTime > 0L) {
                    Creatures.logger.log(Level.INFO, "Creatures polled " + this.crets.length + " Took " + this.totalTime);
                }
                this.totalTime = 0L;
            }
            this.currentCreature = 0;
            this.crets = this.getCreatures();
            if (this.crets != null) {
                for (final Player creature : Players.getInstance().getPlayers()) {
                    try {
                        final VolaTile t = creature.getCurrentTile();
                        if (creature.poll()) {
                            if (t != null) {
                                t.deleteCreature(creature);
                            }
                        }
                        else if (creature.isDoLavaDamage() && creature.doLavaDamage() && t != null) {
                            t.deleteCreature(creature);
                        }
                        if (creature.isDoAreaDamage() && t != null && t.doAreaDamage(creature) && t != null) {
                            t.deleteCreature(creature);
                        }
                    }
                    catch (Exception ex) {
                        Creatures.logger.log(Level.INFO, ex.getMessage(), ex);
                        ex.printStackTrace();
                    }
                }
            }
        }
        this.startTime = System.currentTimeMillis();
        final long start = System.currentTimeMillis();
        int rest = 0;
        if (num == this.numberOfZonesX) {
            rest = this.crets.length % this.numberOfZonesX;
        }
        for (int x = this.currentCreature; x < rest + this.crets.length / this.numberOfZonesX * num; ++x) {
            ++this.currentCreature;
            try {
                final VolaTile t = this.crets[x].getCurrentTile();
                if (this.crets[x].poll()) {
                    if (t != null) {
                        t.deleteCreature(this.crets[x]);
                    }
                }
                else if (this.crets[x].isDoLavaDamage() && this.crets[x].doLavaDamage() && t != null) {
                    t.deleteCreature(this.crets[x]);
                }
                if (this.crets[x].isDoAreaDamage() && t != null && t.doAreaDamage(this.crets[x]) && t != null) {
                    t.deleteCreature(this.crets[x]);
                }
            }
            catch (Exception ex) {
                Creatures.logger.log(Level.INFO, ex.getMessage(), ex);
                ex.printStackTrace();
            }
        }
        this.totalTime += System.currentTimeMillis() - start;
    }
    
    public boolean isLog() {
        return this.logCreaturePolls;
    }
    
    public void setLog(final boolean log) {
        this.logCreaturePolls = log;
    }
    
    public static final Creature[] getManagedAnimalsFor(final Player player, final int villageId, final boolean includeAll) {
        final Set<Creature> animals = new HashSet<Creature>();
        if (villageId >= 0 && includeAll) {
            for (final Creature animal : getInstance().getBranded(villageId)) {
                animals.add(animal);
            }
        }
        for (final Creature animal2 : getInstance().creatures.values()) {
            final long whom = getInstance().getCreatureProctector(animal2);
            if (whom == player.getWurmId()) {
                animals.add(animal2);
            }
            else {
                if (!animal2.canManage(player) || animal2.isWagoner()) {
                    continue;
                }
                animals.add(animal2);
            }
        }
        if (player.getPet() != null) {
            animals.add(player.getPet());
        }
        return animals.toArray(new Creature[animals.size()]);
    }
    
    public static final Creature[] getManagedWagonersFor(final Player player, final int villageId) {
        final Set<Creature> animals = new HashSet<Creature>();
        if (!Servers.isThisAPvpServer()) {
            for (final Map.Entry<Long, Wagoner> entry : Wagoner.getWagoners().entrySet()) {
                if (entry.getValue().getVillageId() == villageId) {
                    final Creature wagoner = entry.getValue().getCreature();
                    if (wagoner == null) {
                        continue;
                    }
                    animals.add(wagoner);
                }
                else {
                    final Creature wagoner = entry.getValue().getCreature();
                    if (wagoner == null || !wagoner.canManage(player)) {
                        continue;
                    }
                    animals.add(wagoner);
                }
            }
        }
        return animals.toArray(new Creature[animals.size()]);
    }
    
    public static final Set<Creature> getMayUseWagonersFor(final Creature performer) {
        final Set<Creature> wagoners = new HashSet<Creature>();
        if (!Servers.isThisAPvpServer()) {
            for (final Map.Entry<Long, Wagoner> entry : Wagoner.getWagoners().entrySet()) {
                final Wagoner wagoner = entry.getValue();
                final Creature creature = wagoner.getCreature();
                if (wagoner.getVillageId() != -1 && creature != null && creature.mayUse(performer)) {
                    wagoners.add(creature);
                }
            }
        }
        return wagoners;
    }
    
    static {
        Creatures.instance = null;
        Creatures.logger = Logger.getLogger(Creatures.class.getName());
        Creatures.destroyedCaveCrets = 0;
        Creatures.loading = false;
        Creatures.nums = 0;
        Creatures.seaMonsters = 0;
        Creatures.seaHunters = 0;
    }
    
    private class PollTimerTask extends TimerTask
    {
        private boolean keeprunning;
        private boolean log;
        
        private PollTimerTask() {
            this.keeprunning = true;
            this.log = false;
        }
        
        public final void shutDown() {
            this.keeprunning = false;
        }
        
        @Override
        public void run() {
            if (this.keeprunning) {
                int polled = 0;
                int destroyed = 0;
                int failedRemove = 0;
                final long start = System.currentTimeMillis();
                for (final Creature creature : Creatures.getInstance().creatures.values()) {
                    try {
                        final VolaTile t = creature.getCurrentTile();
                        if (creature.poll()) {
                            ++destroyed;
                            if (t != null) {
                                t.deleteCreature(creature);
                            }
                            else {
                                ++failedRemove;
                            }
                        }
                        else if (creature.isDoLavaDamage() && creature.doLavaDamage()) {
                            ++destroyed;
                            if (t != null) {
                                t.deleteCreature(creature);
                            }
                            else {
                                ++failedRemove;
                            }
                        }
                        if (creature.isDoAreaDamage()) {}
                        ++polled;
                    }
                    catch (Exception ex) {
                        Creatures.logger.log(Level.INFO, ex.getMessage(), ex);
                    }
                }
                if (this.isLog()) {
                    Creatures.logger.log(Level.INFO, "PTT polled " + polled + " Took " + (System.currentTimeMillis() - start) + " destroyed=" + destroyed + " failed remove=" + failedRemove);
                }
                for (final Player creature2 : Players.getInstance().getPlayers()) {
                    try {
                        final VolaTile t2 = creature2.getCurrentTile();
                        if (creature2.poll()) {
                            ++destroyed;
                            if (t2 != null) {
                                t2.deleteCreature(creature2);
                            }
                            else {
                                ++failedRemove;
                            }
                        }
                        else if (creature2.isDoLavaDamage() && creature2.doLavaDamage()) {
                            ++destroyed;
                            if (t2 != null) {
                                t2.deleteCreature(creature2);
                            }
                            else {
                                ++failedRemove;
                            }
                        }
                        if (creature2.isDoAreaDamage()) {}
                        ++polled;
                    }
                    catch (Exception ex2) {
                        Creatures.logger.log(Level.INFO, ex2.getMessage(), ex2);
                    }
                }
            }
            else {
                Creatures.logger.log(Level.INFO, "PollTimerTask shut down.");
                this.cancel();
            }
        }
        
        public boolean isLog() {
            return this.log;
        }
    }
}
