// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import com.wurmonline.server.Constants;
import com.wurmonline.server.loot.LootTable;
import com.wurmonline.server.items.Recipe;
import com.wurmonline.server.items.Recipes;
import com.wurmonline.server.villages.VillageRole;
import com.wurmonline.server.players.SpellResistance;
import com.wurmonline.server.combat.Attack;
import com.wurmonline.shared.exceptions.WurmServerException;
import com.wurmonline.server.utils.StringUtil;
import com.wurmonline.server.combat.ArmourTemplate;
import com.wurmonline.server.players.Cultist;
import com.wurmonline.server.players.KingdomIp;
import com.wurmonline.server.Team;
import com.wurmonline.server.zones.Den;
import com.wurmonline.server.creatures.ai.Order;
import com.wurmonline.server.zones.Dens;
import com.wurmonline.server.creatures.ai.NoPathException;
import com.wurmonline.server.creatures.ai.PathFinder;
import com.wurmonline.server.behaviours.TileRockBehaviour;
import com.wurmonline.server.utils.logging.TileEvent;
import com.wurmonline.server.behaviours.MethodsStructure;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.behaviours.FishEnums;
import com.wurmonline.server.creatures.ai.scripts.FishAI;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.server.skills.SkillSystem;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.structures.Blocker;
import com.wurmonline.server.structures.BlockingResult;
import com.wurmonline.mesh.MeshIO;
import com.wurmonline.server.structures.Blocking;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.shared.util.MovementChecker;
import com.wurmonline.server.structures.Fence;
import com.wurmonline.server.bodys.Wounds;
import com.wurmonline.server.Message;
import com.wurmonline.server.items.ItemSpellEffects;
import java.util.Optional;
import com.wurmonline.server.LoginHandler;
import com.wurmonline.server.players.PlayerKills;
import com.wurmonline.server.tutorial.MissionPerformer;
import com.wurmonline.server.epic.EpicMission;
import com.wurmonline.server.tutorial.MissionTrigger;
import com.wurmonline.server.HistoryManager;
import com.wurmonline.server.webinterface.WcEpicKarmaCommand;
import com.wurmonline.server.tutorial.MissionPerformed;
import com.wurmonline.server.epic.EpicMissionEnum;
import com.wurmonline.server.villages.Citizen;
import com.wurmonline.server.players.PermissionsByPlayer;
import com.wurmonline.server.weather.Weather;
import com.wurmonline.server.items.ItemSettings;
import java.util.StringTokenizer;
import com.wurmonline.server.webinterface.WcTrelloDeaths;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.webinterface.WcKillCommand;
import com.wurmonline.math.TilePos;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.questions.TraderManagementQuestion;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.economy.Shop;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.mesh.GrassData;
import com.wurmonline.server.behaviours.TileFieldBehaviour;
import com.wurmonline.server.epic.EpicServerStatus;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.combat.CombatEngine;
import com.wurmonline.server.utils.CreatureLineSegment;
import com.wurmonline.shared.util.MulticolorLineSegment;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.ai.Path;
import com.wurmonline.server.structures.NoSuchWallException;
import com.wurmonline.server.behaviours.NoSuchBehaviourException;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.behaviours.BehaviourDispatcher;
import com.wurmonline.server.combat.Weapon;
import com.wurmonline.server.items.NoSpaceException;
import com.wurmonline.server.spells.Spells;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.math.Vector3f;
import com.wurmonline.math.Vector2f;
import com.wurmonline.server.villages.Guard;
import com.wurmonline.server.behaviours.Seat;
import com.wurmonline.server.behaviours.Vehicles;
import com.wurmonline.server.players.Abilities;
import com.wurmonline.server.questions.TestQuestion;
import com.wurmonline.server.bodys.Body;
import com.wurmonline.server.intra.MountTransfer;
import com.wurmonline.server.behaviours.MethodsItems;
import com.wurmonline.mesh.Tiles;
import com.wurmonline.server.PlonkData;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.players.ItemBonus;
import com.wurmonline.server.items.Trade;
import javax.annotation.Nonnull;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.shared.util.StringUtilities;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.tutorial.PlayerTutorial;
import java.util.HashSet;
import com.wurmonline.server.skills.SkillsFactory;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.kingdom.Appointments;
import com.wurmonline.server.kingdom.King;
import com.wurmonline.server.zones.FaithZone;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.Features;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.zones.VirtualZone;
import com.wurmonline.server.highways.Route;
import java.util.List;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Players;
import com.wurmonline.server.Items;
import com.wurmonline.server.players.Titles;
import com.wurmonline.server.players.Achievements;
import com.wurmonline.server.spells.SpellEffect;
import com.wurmonline.server.combat.BattleEvent;
import com.wurmonline.server.combat.Battles;
import com.wurmonline.server.behaviours.Terraforming;
import com.wurmonline.server.kingdom.Kingdom;
import java.util.ListIterator;
import com.wurmonline.server.zones.HiveZone;
import com.wurmonline.server.tutorial.MissionTriggers;
import com.wurmonline.server.endgames.EndGameItems;
import com.wurmonline.server.Servers;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.questions.SimplePopup;
import java.io.IOException;
import com.wurmonline.server.zones.FocusZone;
import com.wurmonline.server.players.Player;
import java.util.logging.Level;
import javax.annotation.Nullable;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.zones.Zones;
import java.util.Iterator;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.zones.Trap;
import com.wurmonline.server.behaviours.Behaviours;
import com.wurmonline.server.Server;
import com.wurmonline.server.creatures.ai.PathTile;
import com.wurmonline.server.creatures.ai.CreatureAIData;
import com.wurmonline.server.kingdom.GuardTower;
import com.wurmonline.server.creatures.ai.CreaturePathFinderNPC;
import com.wurmonline.server.creatures.ai.CreaturePathFinderAgg;
import com.wurmonline.server.creatures.ai.CreaturePathFinder;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.effects.Effect;
import java.util.ArrayList;
import com.wurmonline.server.players.MovementEntity;
import com.wurmonline.server.creatures.ai.DecisionStack;
import com.wurmonline.server.zones.VolaTile;
import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.combat.Battle;
import com.wurmonline.server.villages.Village;
import java.util.LinkedList;
import com.wurmonline.server.structures.Door;
import java.util.Set;
import java.util.Map;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.behaviours.ActionStack;
import com.wurmonline.server.behaviours.Behaviour;
import com.wurmonline.server.items.Possessions;
import com.wurmonline.server.players.MusicPlayer;
import com.wurmonline.server.behaviours.Vehicle;
import java.util.logging.Logger;
import com.wurmonline.server.modifiers.DoubleValueModifier;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.combat.SpecialMove;
import com.wurmonline.server.spells.SpellResist;
import java.util.HashMap;
import com.wurmonline.server.skills.Skills;
import com.wurmonline.server.players.PermissionsPlayerList;
import com.wurmonline.shared.constants.AttitudeConstants;
import com.wurmonline.server.economy.MonetaryConstants;
import com.wurmonline.server.modifiers.ModifierTypes;
import com.wurmonline.server.combat.CombatConstants;
import com.wurmonline.shared.constants.ProtoConstants;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.shared.constants.CreatureTypes;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.items.ItemTypes;

public class Creature implements ItemTypes, CounterTypes, MiscConstants, CreatureTypes, TimeConstants, ProtoConstants, CombatConstants, ModifierTypes, CreatureTemplateIds, MonetaryConstants, AttitudeConstants, PermissionsPlayerList.ISettings, Comparable<Creature>
{
    protected Skills skills;
    private int respawnCounter;
    private static final int NPCRESPAWN = 600;
    protected CreatureStatus status;
    protected HashMap<Integer, SpellResist> spellResistances;
    private long id;
    private static final double skillLost = 0.25;
    public static final double MAX_LEAD_DEPTH = -0.71;
    public long loggerCreature1;
    private long loggerCreature2;
    public int combatRound;
    public SpecialMove specialMove;
    protected boolean isVehicleCommander;
    public Creature lastOpponent;
    public int opponentCounter;
    protected boolean _enterVehicle;
    protected MountAction mountAction;
    public boolean addingAfterTeleport;
    private static final Item[] emptyItems;
    protected long linkedTo;
    private boolean isInDuelRing;
    private static final DoubleValueModifier willowMod;
    public boolean shouldStandStill;
    public byte opportunityAttackCounter;
    private long lastSentToolbelt;
    protected static final float submergedMinDepth = -5.0f;
    protected static final Logger logger;
    protected CreatureTemplate template;
    protected Vehicle hitchedTo;
    protected MusicPlayer musicPlayer;
    private boolean inHostilePerimeter;
    private int hugeMoveCounter;
    protected String name;
    protected String petName;
    protected Possessions possessions;
    protected Communicator communicator;
    private VisionArea visionArea;
    private final Behaviour behaviour;
    protected ActionStack actions;
    private Structure structure;
    public int numattackers;
    public int numattackerslast;
    protected Map<Long, Long> attackers;
    public Creature opponent;
    private Set<Long> riders;
    private Set<Item> keys;
    protected byte fightlevel;
    protected boolean guest;
    protected boolean isTeleporting;
    private long startTeleportTime;
    public boolean faithful;
    private Door currentDoor;
    private float teleportX;
    private float teleportY;
    protected int teleportLayer;
    protected int teleportFloorLevel;
    protected boolean justSpawned;
    public String spawnWeapon;
    public String spawnArmour;
    private LinkedList<int[]> openedTiles;
    private int carriedWeight;
    private static final float DEGS_TO_RADS = 0.017453292f;
    private TradeHandler tradeHandler;
    public Village citizenVillage;
    public Village currentVillage;
    private Set<Item> itemsTaken;
    private Set<Item> itemsDropped;
    protected MovementScheme movementScheme;
    protected Battle battle;
    private Set<Long> stealthBreakers;
    private Set<DoubleValueModifier> visionModifiers;
    private final ConcurrentHashMap<Item, Float> weaponsUsed;
    private final ConcurrentHashMap<AttackAction, UsedAttackData> attackUsed;
    public long lastSavedPos;
    protected byte guardSecondsLeft;
    private byte fightStyle;
    private boolean milked;
    private boolean sheared;
    private boolean isRiftSummoned;
    public long target;
    public Creature leader;
    public long dominator;
    public float zoneBonus;
    private byte currentDeity;
    public byte fleeCounter;
    public boolean isLit;
    private int encumbered;
    private int moveslow;
    private int cantmove;
    private byte tilesMoved;
    private byte pathfindcounter;
    protected Map<Creature, Item> followers;
    protected static final Creature[] emptyCreatures;
    public byte currentKingdom;
    protected short damageCounter;
    private final DoubleValueModifier woundMoveMod;
    public long lastParry;
    public VolaTile currentTile;
    public int staminaPollCounter;
    private DecisionStack decisions;
    private static final float HUNGER_RANGE = 20535.0f;
    public boolean goOffline;
    private Item bestLightsource;
    private Item bestCompass;
    private Item bestToolbelt;
    private Item bestBeeSmoker;
    private Item bestTackleBox;
    public boolean lightSourceChanged;
    public boolean lastSentHasCompass;
    private CombatHandler combatHandler;
    private int pollCounter;
    private static final int secondsBetweenItemPolls = 10800;
    private static final int secondsBetweenTraderCoolingPolls = 600;
    private int heatCheckTick;
    private int mountPollCounter;
    protected int breedCounter;
    private boolean visibleToPlayers;
    private boolean forcedBreed;
    private boolean hasSpiritStamina;
    protected boolean hasSpiritFavorgain;
    public boolean hasAddedToAttack;
    private static final long LOG_ELAPSED_TIME_THRESHOLD;
    private static final boolean DO_MORE_ELAPSED_TIME_MEASUREMENTS = false;
    protected boolean hasSentPoison;
    int pathRecalcLength;
    protected boolean isInPvPZone;
    protected boolean isInNonPvPZone;
    protected boolean isInFogZone;
    private static final Set<Long> pantLess;
    private static final Map<Long, Set<MovementEntity>> illusions;
    protected boolean isInOwnBattleCamp;
    private boolean doLavaDamage;
    private boolean doAreaDamage;
    protected float webArmourModTime;
    private ArrayList<Effect> effects;
    private ServerEntry destination;
    private static CreaturePathFinder pathFinder;
    private static CreaturePathFinderAgg pathFinderAgg;
    private static CreaturePathFinderNPC pathFinderNPC;
    public long vehicle;
    protected byte seatType;
    protected int teleports;
    private long lastWaystoneChecked;
    private boolean checkedHotItemsAfterLogin;
    private boolean ignoreSaddleDamage;
    private boolean isPlacingItem;
    private Item placementItem;
    private float[] pendingPlacement;
    private GuardTower guardTower;
    private int lastSecond;
    static long firstCreature;
    static int pollChecksPer;
    static final int breedPollCounter = 201;
    int breedTick;
    private int lastPolled;
    private CreatureAIData aiData;
    private boolean isPathing;
    private boolean setTargetNOID;
    private Creature creatureToBlinkTo;
    public boolean receivedPath;
    private PathTile targetPathTile;
    static int totx;
    static int toty;
    static int movesx;
    static int movesy;
    protected static final String NOPATH = "No pathing now";
    float creatureFavor;
    boolean switchv;
    
    public static void shutDownPathFinders() {
        Creature.pathFinder.shutDown();
        Creature.pathFinderAgg.shutDown();
        Creature.pathFinderNPC.shutDown();
    }
    
    public static final CreaturePathFinder getPF() {
        return Creature.pathFinder;
    }
    
    public static final CreaturePathFinderAgg getPFA() {
        return Creature.pathFinderAgg;
    }
    
    public static final CreaturePathFinderNPC getPFNPC() {
        return Creature.pathFinderNPC;
    }
    
    protected Creature() throws Exception {
        this.respawnCounter = 0;
        this.spellResistances = new HashMap<Integer, SpellResist>();
        this.loggerCreature1 = -10L;
        this.loggerCreature2 = -10L;
        this.combatRound = 0;
        this.specialMove = null;
        this.isVehicleCommander = false;
        this.opponentCounter = 0;
        this._enterVehicle = false;
        this.mountAction = null;
        this.addingAfterTeleport = false;
        this.linkedTo = -10L;
        this.isInDuelRing = false;
        this.shouldStandStill = false;
        this.opportunityAttackCounter = 0;
        this.lastSentToolbelt = 0L;
        this.hitchedTo = null;
        this.inHostilePerimeter = false;
        this.hugeMoveCounter = 0;
        this.name = "Noname";
        this.petName = "";
        this.opponent = null;
        this.riders = null;
        this.fightlevel = 0;
        this.guest = false;
        this.isTeleporting = false;
        this.startTeleportTime = Long.MIN_VALUE;
        this.faithful = true;
        this.currentDoor = null;
        this.teleportX = -1.0f;
        this.teleportY = -1.0f;
        this.teleportLayer = 0;
        this.teleportFloorLevel = 0;
        this.justSpawned = false;
        this.spawnWeapon = "";
        this.spawnArmour = "";
        this.carriedWeight = 0;
        this.itemsTaken = null;
        this.itemsDropped = null;
        this.battle = null;
        this.stealthBreakers = null;
        this.weaponsUsed = new ConcurrentHashMap<Item, Float>();
        this.attackUsed = new ConcurrentHashMap<AttackAction, UsedAttackData>();
        this.lastSavedPos = System.currentTimeMillis() - Server.rand.nextInt(1800000);
        this.guardSecondsLeft = 0;
        this.fightStyle = 2;
        this.milked = false;
        this.sheared = false;
        this.isRiftSummoned = false;
        this.target = -10L;
        this.leader = null;
        this.dominator = -10L;
        this.zoneBonus = 0.0f;
        this.currentDeity = 0;
        this.fleeCounter = 0;
        this.isLit = false;
        this.encumbered = 70000;
        this.moveslow = 40000;
        this.cantmove = 140000;
        this.tilesMoved = 0;
        this.pathfindcounter = 0;
        this.followers = null;
        this.currentKingdom = 0;
        this.damageCounter = 0;
        this.woundMoveMod = new DoubleValueModifier(7, -0.25);
        this.lastParry = 0L;
        this.staminaPollCounter = 0;
        this.decisions = null;
        this.goOffline = false;
        this.bestLightsource = null;
        this.bestCompass = null;
        this.bestToolbelt = null;
        this.bestBeeSmoker = null;
        this.bestTackleBox = null;
        this.lightSourceChanged = false;
        this.lastSentHasCompass = false;
        this.combatHandler = null;
        this.pollCounter = 0;
        this.heatCheckTick = 0;
        this.mountPollCounter = 10;
        this.breedCounter = 0;
        this.visibleToPlayers = false;
        this.forcedBreed = false;
        this.hasSpiritStamina = false;
        this.hasSpiritFavorgain = false;
        this.hasAddedToAttack = false;
        this.hasSentPoison = false;
        this.pathRecalcLength = 0;
        this.isInPvPZone = false;
        this.isInNonPvPZone = false;
        this.isInFogZone = false;
        this.isInOwnBattleCamp = false;
        this.doLavaDamage = false;
        this.doAreaDamage = false;
        this.webArmourModTime = 0.0f;
        this.vehicle = -10L;
        this.seatType = -1;
        this.teleports = 0;
        this.lastWaystoneChecked = -10L;
        this.checkedHotItemsAfterLogin = false;
        this.ignoreSaddleDamage = false;
        this.isPlacingItem = false;
        this.placementItem = null;
        this.pendingPlacement = null;
        this.guardTower = null;
        this.lastSecond = 1;
        this.breedTick = 0;
        this.lastPolled = Server.rand.nextInt(Creature.pollChecksPer);
        this.aiData = null;
        this.isPathing = false;
        this.setTargetNOID = false;
        this.creatureToBlinkTo = null;
        this.receivedPath = false;
        this.targetPathTile = null;
        this.creatureFavor = 100.0f;
        this.switchv = true;
        this.behaviour = Behaviours.getInstance().getBehaviour((short)4);
        this.communicator = new CreatureCommunicator(this);
        this.actions = new ActionStack();
        this.movementScheme = new MovementScheme(this);
        this.pollCounter = Server.rand.nextInt(10800);
    }
    
    public void checkTrap() {
        if (!this.isDead()) {
            final Trap trap = Trap.getTrap(this.currentTile.tilex, this.currentTile.tiley, this.getLayer());
            if (this.getPower() >= 3) {
                if (trap != null) {
                    this.getCommunicator().sendNormalServerMessage("A " + trap.getName() + " is here.");
                }
            }
            else if (trap != null) {
                boolean trigger = false;
                if (trap.getKingdom() != this.getKingdomId()) {
                    if (this.getKingdomId() == 0 && !this.isAggHuman()) {
                        trigger = false;
                        if (this.riders != null && this.riders.size() > 0) {
                            for (final Long rider : this.riders) {
                                try {
                                    final Creature rr = Server.getInstance().getCreature(rider);
                                    if (rr.getKingdomId() == trap.getKingdom()) {
                                        continue;
                                    }
                                    trigger = true;
                                }
                                catch (NoSuchCreatureException ex) {}
                                catch (NoSuchPlayerException ex2) {}
                            }
                        }
                    }
                    else {
                        trigger = true;
                    }
                }
                else if (trap.getVillage() > 0) {
                    try {
                        final Village vill = Villages.getVillage(trap.getVillage());
                        if (vill.isEnemy(this)) {
                            trigger = true;
                        }
                    }
                    catch (NoSuchVillageException ex3) {}
                }
                if (trigger) {
                    trap.doEffect(this, this.currentTile.tilex, this.currentTile.tiley, this.getLayer());
                }
            }
        }
    }
    
    public void sendDetectTrap(final Trap trap) {
        if (trap != null && Server.rand.nextInt(100) < this.getDetectDangerBonus()) {
            this.getCommunicator().sendAlertServerMessage("TRAP!", (byte)4);
        }
    }
    
    public final void calculateFloorLevel(final VolaTile tile, final boolean forceAddFloorLayer) {
        this.calculateFloorLevel(tile, forceAddFloorLayer, false);
    }
    
    public final void calculateFloorLevel(final VolaTile tile, final boolean forceAddFloorLayer, final boolean wasOnBridge) {
        try {
            if (tile.getStructure() != null && tile.getStructure().isTypeHouse()) {
                if (this.getFloorLevel() == 0 && !wasOnBridge) {
                    if (!this.isPlayer()) {
                        final float oldposz = this.getPositionZ();
                        if (oldposz >= -1.25) {
                            final float newPosz = Zones.calculateHeight(this.getPosX(), this.getPosY(), this.isOnSurface()) + ((tile.getFloors(-10, 10).length == 0) ? 0.0f : 0.25f);
                            final float diffz = newPosz - oldposz;
                            this.setPositionZ(newPosz);
                            if (this.currentTile != null && this.getVisionArea() != null) {
                                this.moved(0.0f, 0.0f, diffz, 0, 0);
                            }
                        }
                    }
                }
                else {
                    final int targetFloorLevel = tile.getDropFloorLevel(this.getFloorLevel());
                    if (targetFloorLevel != this.getFloorLevel()) {
                        if (!this.isPlayer()) {
                            this.pushToFloorLevel(targetFloorLevel);
                        }
                    }
                    else if (forceAddFloorLayer) {
                        if (!this.isPlayer()) {
                            final float oldposz2 = this.getPositionZ();
                            final float newPosz2 = Zones.calculateHeight(this.getPosX(), this.getPosY(), this.isOnSurface()) + ((tile.getFloors(-10, 10).length == 0) ? 0.0f : 0.25f);
                            final float diffz2 = newPosz2 - oldposz2;
                            this.setPositionZ(newPosz2);
                            if (this.currentTile != null && this.getVisionArea() != null) {
                                this.moved(0.0f, 0.0f, diffz2, 0, 0);
                            }
                        }
                    }
                }
            }
            else if (tile.getStructure() == null || !tile.getStructure().isTypeBridge()) {
                if (this.getFloorLevel() >= 0) {
                    if (!this.isPlayer()) {
                        final float oldposz = this.getPositionZ();
                        if (oldposz >= 0.0f) {
                            final float newPosz = Zones.calculateHeight(this.getPosX(), this.getPosY(), this.isOnSurface());
                            final float diffz = newPosz - oldposz;
                            this.setPositionZ(newPosz);
                            if (this.currentTile != null && this.getVisionArea() != null) {
                                this.moved(0.0f, 0.0f, diffz, 0, 0);
                            }
                        }
                    }
                }
            }
        }
        catch (NoSuchZoneException ex) {}
    }
    
    @Override
    public int compareTo(final Creature otherCreature) {
        return this.getName().compareTo(otherCreature.getName());
    }
    
    public boolean setNewTile(@Nullable final VolaTile newtile, final float diffZ, final boolean ignoreBridge) {
        if (newtile != null && (this.getTileX() != newtile.tilex || this.getTileY() != newtile.tiley)) {
            Creature.logger.log(Level.WARNING, this.getName() + " set to " + newtile.tilex + "," + newtile.tiley + " but at " + this.getTileX() + "," + this.getTileY(), new Exception());
            if (this.currentTile != null) {
                Creature.logger.log(Level.WARNING, "old is " + this.currentTile.tilex + "(" + this.getPosX() + "), " + this.currentTile.tiley + "(" + this.getPosY() + "), vehic=" + this.getVehicle());
                if (this.isPlayer()) {
                    ((Player)this).intraTeleport((this.currentTile.tilex << 2) + 2, (this.currentTile.tiley << 2) + 2, this.getPositionZ(), this.getStatus().getRotation(), this.getLayer(), "on wrong tile");
                }
            }
            return false;
        }
        boolean wasInDuelRing = false;
        Set<FocusZone> oldFocusZones = null;
        HiveZone oldHiveZone = null;
        boolean oldHiveClose = false;
        final long oldBridgeId = this.getBridgeId();
        if (this.currentTile != null) {
            if (this.isPlayer()) {
                final Item ring = Zones.isWithinDuelRing(this.currentTile.tilex, this.currentTile.tiley, this.currentTile.isOnSurface());
                if (ring != null) {
                    wasInDuelRing = true;
                }
                oldFocusZones = FocusZone.getZonesAt(this.currentTile.tilex, this.currentTile.tiley);
                oldHiveZone = Zones.getHiveZoneAt(this.currentTile.tilex, this.currentTile.tiley, this.currentTile.isOnSurface());
                if (oldHiveZone != null) {
                    oldHiveClose = oldHiveZone.isClose(this.currentTile.tilex, this.currentTile.tiley);
                }
            }
            if (newtile != null && !this.isDead()) {
                this.currentTile.checkOpportunityAttacks(this);
                if (this.currentTile != null) {
                    final int diffX = newtile.tilex - this.currentTile.tilex;
                    final int diffY = newtile.tiley - this.currentTile.tiley;
                    if (diffX != 0) {
                        this.sendDetectTrap(Trap.getTrap(newtile.tilex + diffX, newtile.tiley, this.getLayer()));
                    }
                    if (diffY != 0) {
                        this.sendDetectTrap(Trap.getTrap(newtile.tilex, newtile.tiley + diffY, this.getLayer()));
                    }
                    if (diffY != 0 && diffX != 0) {
                        this.sendDetectTrap(Trap.getTrap(newtile.tilex + diffX, newtile.tiley + diffY, this.getLayer()));
                    }
                    else if (diffX != 0) {
                        this.sendDetectTrap(Trap.getTrap(newtile.tilex + diffX, newtile.tiley - 1, this.getLayer()));
                        this.sendDetectTrap(Trap.getTrap(newtile.tilex + diffX, newtile.tiley + 1, this.getLayer()));
                    }
                    else if (diffY != 0) {
                        this.sendDetectTrap(Trap.getTrap(newtile.tilex + 1, newtile.tiley + diffY, this.getLayer()));
                        this.sendDetectTrap(Trap.getTrap(newtile.tilex - 1, newtile.tiley + diffY, this.getLayer()));
                    }
                    if (this.currentTile != newtile) {
                        this.currentTile.removeCreature(this);
                    }
                }
                if (this.isPlayer()) {
                    this.addTileMoved();
                }
            }
            else {
                this.currentTile.removeCreature(this);
            }
            if (this.currentTile != null && this.isPlayer() && this.currentTile != newtile && this.openedTiles != null) {
                final ListIterator<int[]> openedIterator = this.openedTiles.listIterator();
                while (openedIterator.hasNext()) {
                    final int[] opened = openedIterator.next();
                    if (newtile != null && opened[0] == newtile.getTileX()) {
                        if (opened[1] == newtile.getTileY()) {
                            continue;
                        }
                    }
                    try {
                        this.getCommunicator().sendTileDoor((short)opened[0], (short)opened[1], false);
                        openedIterator.remove();
                        final MineDoorPermission md = MineDoorPermission.getPermission((short)opened[0], (short)opened[1]);
                        if (md == null) {
                            continue;
                        }
                        md.close(this);
                    }
                    catch (IOException ex) {}
                }
                if (this.openedTiles.isEmpty()) {
                    this.openedTiles = null;
                }
            }
            if (this.currentTile != null && newtile != null) {
                this.currentTile = newtile;
                this.checkTrap();
                if (this.isDead()) {
                    return false;
                }
                if (!this.isPlayer() && !ignoreBridge) {
                    this.checkBridgeMove(this.currentTile, newtile, diffZ);
                }
            }
            else if (newtile != null && !ignoreBridge && !this.isPlayer()) {
                this.checkBridgeMove(null, newtile, diffZ);
            }
        }
        this.currentTile = newtile;
        if (this.currentTile != null) {
            if (!this.isRidden()) {
                boolean wasOnBridge = false;
                if (oldBridgeId != -10L && oldBridgeId != this.getBridgeId()) {
                    wasOnBridge = true;
                }
                this.calculateFloorLevel(this.currentTile, false, wasOnBridge);
            }
            final Set<FocusZone> newFocusZones = FocusZone.getZonesAt(this.currentTile.tilex, this.currentTile.tiley);
            if (!this.isPlayer()) {
                this.isInPvPZone = false;
                this.isInNonPvPZone = false;
                for (final FocusZone fz : newFocusZones) {
                    if (fz.isPvP()) {
                        this.isInPvPZone = true;
                        break;
                    }
                    if (fz.isNonPvP()) {
                        this.isInNonPvPZone = true;
                        break;
                    }
                }
                ++this.tilesMoved;
                if (this.tilesMoved >= 10) {
                    Label_1134: {
                        if (!this.isDominated()) {
                            if (!this.isHorse()) {
                                break Label_1134;
                            }
                        }
                        try {
                            this.savePosition(this.currentTile.getZone().getId());
                        }
                        catch (IOException ex2) {}
                    }
                    this.tilesMoved = 0;
                }
            }
            if (this.isPlayer()) {
                try {
                    this.savePosition(this.currentTile.getZone().getId());
                }
                catch (IOException ex3) {}
                for (final FocusZone fz : newFocusZones) {
                    if (fz.isFog() && !this.isInFogZone) {
                        this.isInFogZone = true;
                        this.getCommunicator().sendSpecificWeather(0.85f);
                    }
                    if (fz.isPvP()) {
                        if (!this.isInPvPZone) {
                            if (!this.isOnPvPServer()) {
                                this.achievement(4);
                                this.getCommunicator().sendAlertServerMessage("You enter the " + fz.getName() + " PvP area. Other players may attack you here.", (byte)4);
                            }
                            else {
                                this.getCommunicator().sendAlertServerMessage("You enter the " + fz.getName() + " area.", (byte)4);
                            }
                            this.sendAttitudeChange();
                        }
                        this.isInPvPZone = true;
                        break;
                    }
                    if (fz.isNonPvP()) {
                        if (!this.isInNonPvPZone) {
                            if (this.isOnPvPServer()) {
                                this.getCommunicator().sendSafeServerMessage("You enter the " + fz.getName() + " No-PvP area. Other players may no longer attack you here.", (byte)2);
                            }
                            else {
                                this.getCommunicator().sendSafeServerMessage("You enter the " + fz.getName() + " No-PvP area.", (byte)2);
                            }
                            this.sendAttitudeChange();
                        }
                        this.isInNonPvPZone = true;
                        break;
                    }
                    if ((!fz.isName() && !fz.isNamePopup() && !fz.isNoBuild() && !fz.isPremSpawnOnly()) || (oldFocusZones != null && oldFocusZones.contains(fz))) {
                        continue;
                    }
                    if (fz.isName() || fz.isNoBuild() || fz.isPremSpawnOnly()) {
                        this.getCommunicator().sendSafeServerMessage("You enter the " + fz.getName() + " area.", (byte)2);
                    }
                    else {
                        final SimplePopup sp = new SimplePopup(this, "Entering " + fz.getName(), "You enter the " + fz.getName() + " area.", fz.getDescription());
                        sp.sendQuestion();
                    }
                }
                if (oldFocusZones != null) {
                    for (final FocusZone fz : oldFocusZones) {
                        if (fz.isFog() && (newFocusZones == null || !newFocusZones.contains(fz))) {
                            this.isInFogZone = false;
                            this.getCommunicator().checkSendWeather();
                        }
                        if (fz.isPvP()) {
                            if (newFocusZones != null && newFocusZones.contains(fz)) {
                                continue;
                            }
                            this.isInPvPZone = false;
                            if (this.isOnPvPServer()) {
                                this.getCommunicator().sendSafeServerMessage("You leave the " + fz.getName() + " area.", (byte)2);
                            }
                            else {
                                this.getCommunicator().sendSafeServerMessage("You leave the " + fz.getName() + " PvP area.", (byte)2);
                            }
                            this.sendAttitudeChange();
                        }
                        else if (fz.isNonPvP()) {
                            if (newFocusZones != null && newFocusZones.contains(fz)) {
                                continue;
                            }
                            this.isInNonPvPZone = false;
                            this.sendAttitudeChange();
                            if (this.isOnPvPServer()) {
                                this.getCommunicator().sendAlertServerMessage("You leave the " + fz.getName() + " No-PvP area. Other players may attack you here.", (byte)2);
                            }
                            else {
                                this.getCommunicator().sendAlertServerMessage("You leave the " + fz.getName() + " No-PvP area.", (byte)2);
                            }
                        }
                        else {
                            if ((!fz.isName() && !fz.isNamePopup() && !fz.isNoBuild() && !fz.isPremSpawnOnly()) || (newFocusZones != null && newFocusZones.contains(fz))) {
                                continue;
                            }
                            if (fz.isName() || fz.isNoBuild() || fz.isPremSpawnOnly()) {
                                this.getCommunicator().sendSafeServerMessage("You leave the " + fz.getName() + " area.", (byte)2);
                            }
                            else {
                                final SimplePopup sp = new SimplePopup(this, "Leaving " + fz.getName(), "You leave the " + fz.getName() + " area.");
                                sp.sendQuestion();
                            }
                        }
                    }
                }
                if (!WurmCalendar.isSeasonWinter()) {
                    final HiveZone newHiveZone = Zones.getHiveZoneAt(this.currentTile.tilex, this.currentTile.tiley, this.isOnSurface());
                    final boolean newHiveClose = newHiveZone != null && newHiveZone.isClose(this.currentTile.tilex, this.currentTile.tiley);
                    final boolean domestic = newHiveZone != null && newHiveZone.getCurrentHive().getTemplateId() == 1175;
                    if (oldHiveClose && !newHiveClose) {
                        this.getCommunicator().sendSafeServerMessage("The sounds of bees decreases as you move further away from the hive.", (byte)(domestic ? 0 : 2));
                    }
                    if (oldHiveZone == null && newHiveZone != null) {
                        this.getCommunicator().sendSafeServerMessage("You hear bees, maybe you are getting close to a hive.", (byte)(domestic ? 0 : 2));
                    }
                    else if (oldHiveZone != null && newHiveZone == null) {
                        this.getCommunicator().sendSafeServerMessage("The sounds of bees disappears in the distance.", (byte)((oldHiveZone.getCurrentHive().getTemplateId() == 1175) ? 0 : 2));
                    }
                    if (!oldHiveClose && newHiveClose) {
                        if (newHiveZone.getCurrentHive().hasTwoQueens()) {
                            this.getCommunicator().sendSafeServerMessage("The bees noise is getting louder, sounds like there is unusual activity in the hive.", (byte)(domestic ? 0 : 2));
                        }
                        else {
                            this.getCommunicator().sendSafeServerMessage("The bees noise is getting louder, maybe you are getting closer to their hive.", (byte)(domestic ? 0 : 2));
                        }
                    }
                }
                this.isInDuelRing = false;
                final Item ring2 = Zones.isWithinDuelRing(this.currentTile.tilex, this.currentTile.tiley, this.currentTile.isOnSurface());
                if (ring2 != null) {
                    final Kingdom k = Kingdoms.getKingdom(ring2.getAuxData());
                    if (k != null) {
                        if (ring2.getAuxData() == this.getKingdomId()) {
                            this.isInDuelRing = true;
                        }
                        if (!wasInDuelRing) {
                            this.getCommunicator().sendAlertServerMessage("You enter the duelling area of " + k.getName() + ".", (byte)4);
                            if (this.isInDuelRing) {
                                this.getCommunicator().sendAlertServerMessage("People from your own kingdom may slay you here without penalty.", (byte)4);
                            }
                        }
                    }
                }
                else if (wasInDuelRing) {
                    this.getCommunicator().sendSafeServerMessage("You leave the duelling area.", (byte)2);
                }
                if (!Servers.localServer.HOMESERVER && this.isOnSurface() && this.getFaith() > 0.0f && Server.rand.nextInt(100) < this.getFaith() && EndGameItems.getArtifactAtTile(this.currentTile.tilex, this.currentTile.tiley) != null && this.getDeity() != null) {
                    this.getCommunicator().sendSafeServerMessage(this.getDeity().name + " urges you to deeply investigate the area!");
                }
            }
            if (this.isPlayer() && !this.currentTile.isTransition && this.getVisionArea() != null && this.getVisionArea().isInitialized()) {
                this.checkOpenMineDoor();
            }
        }
        if (this.currentTile != null) {
            this.checkInvisDetection();
            boolean hostilePerimeter = false;
            if (this.isPlayer()) {
                final Village lVill = Villages.getVillageWithPerimeterAt(this.getTileX(), this.getTileY(), true);
                if (lVill != null && lVill.kingdom == this.getKingdomId() && lVill.isEnemy(this)) {
                    if (!this.inHostilePerimeter) {
                        this.getCommunicator().sendAlertServerMessage("You are now within the hostile perimeter of " + lVill.getName() + " and will be attacked by kingdom guards.", (byte)4);
                    }
                    hostilePerimeter = true;
                }
            }
            if (!hostilePerimeter && this.inHostilePerimeter) {
                this.getCommunicator().sendSafeServerMessage("You are now outside the hostile perimeters.");
                this.inHostilePerimeter = false;
            }
            if (hostilePerimeter) {
                this.inHostilePerimeter = true;
            }
            if (this.isPlayer()) {
                MissionTriggers.activateTriggerPlate(this, this.currentTile.tilex, this.currentTile.tiley, this.getLayer());
            }
        }
        return true;
    }
    
    public final boolean isInOwnDuelRing() {
        return this.isInDuelRing;
    }
    
    public final boolean hasOpenedMineDoor(final int tilex, final int tiley) {
        if (this.openedTiles == null) {
            return false;
        }
        for (final int[] openedTile : this.openedTiles) {
            if (openedTile[0] == tilex && openedTile[1] == tiley) {
                return true;
            }
        }
        return false;
    }
    
    public void checkOpenMineDoor() {
        if (this.currentTile != null) {
            final Set<int[]> oldM = Terraforming.getAllMineDoors(this.currentTile.tilex, this.currentTile.tiley);
            if (oldM != null) {
                for (final int[] checkedTile : oldM) {
                    if (!this.hasOpenedMineDoor(checkedTile[0], checkedTile[1])) {
                        try {
                            boolean ok = false;
                            final MineDoorPermission md = MineDoorPermission.getPermission(checkedTile[0], checkedTile[1]);
                            if (md != null) {
                                if (md.mayPass(this)) {
                                    ok = true;
                                    if (this.isPlayer()) {
                                        final VolaTile tile = Zones.getOrCreateTile(checkedTile[0], checkedTile[1], true);
                                        if (this.getEnemyPresense() > 0 && (tile == null || tile.getVillage() == null)) {
                                            md.setClosingTime(System.currentTimeMillis() + (Servers.isThisAChaosServer() ? 30000L : 120000L));
                                        }
                                    }
                                }
                                else if (md.isWideOpen()) {
                                    ok = true;
                                }
                            }
                            if (!ok) {
                                continue;
                            }
                            if (this.openedTiles == null) {
                                this.openedTiles = new LinkedList<int[]>();
                            }
                            this.openedTiles.add(checkedTile);
                            this.getMovementScheme().touchFreeMoveCounter();
                            this.getVisionArea().checkCaves(false);
                            this.getCommunicator().sendTileDoor((short)checkedTile[0], (short)checkedTile[1], true);
                            md.open(this);
                        }
                        catch (IOException ex) {}
                    }
                }
            }
        }
    }
    
    public Creature(final CreatureTemplate aTemplate) throws Exception {
        this();
        this.template = aTemplate;
        this.getMovementScheme().initalizeModifiersWithTemplate();
        this.name = aTemplate.getName();
        this.skills = aTemplate.getSkills();
    }
    
    public Item getBestLightsource() {
        return this.bestLightsource;
    }
    
    public Item getBestCompass() {
        return this.bestCompass;
    }
    
    public Item getBestToolbelt() {
        return this.bestToolbelt;
    }
    
    public Item getBestBeeSmoker() {
        return this.bestBeeSmoker;
    }
    
    public Item getBestTackleBox() {
        return this.bestTackleBox;
    }
    
    public void setBestLightsource(@Nullable final Item item, final boolean override) {
        if (override || (this.getVisionArea() != null && this.getVisionArea().isInitialized())) {
            this.bestLightsource = item;
            this.lightSourceChanged = true;
        }
    }
    
    public void setBestCompass(final Item item) {
        this.bestCompass = item;
    }
    
    public void setBestToolbelt(@Nullable final Item item) {
        this.bestToolbelt = item;
    }
    
    public void setBestBeeSmoker(final Item item) {
        this.bestBeeSmoker = item;
    }
    
    public void setBestTackleBox(final Item item) {
        this.bestTackleBox = item;
    }
    
    public void resetCompassLantern() {
        this.bestCompass = null;
        this.bestToolbelt = null;
        if (this.bestLightsource != null && (!this.bestLightsource.isOnFire() || this.bestLightsource.getOwnerId() != this.getWurmId())) {
            this.bestLightsource = null;
            this.lightSourceChanged = true;
        }
        this.bestBeeSmoker = null;
        this.bestTackleBox = null;
    }
    
    public void pollToolbelt() {
        if (this.bestToolbelt != null && this.lastSentToolbelt != this.bestToolbelt.getWurmId()) {
            this.getCommunicator().sendToolbelt(this.bestToolbelt);
            this.lastSentToolbelt = this.bestToolbelt.getWurmId();
        }
        else if (this.bestToolbelt == null && this.lastSentToolbelt != 0L) {
            this.getCommunicator().sendToolbelt(this.bestToolbelt);
            this.lastSentToolbelt = 0L;
        }
    }
    
    public void resetLastSentToolbelt() {
        this.lastSentToolbelt = 0L;
    }
    
    public void pollCompassLantern() {
        if (!this.lastSentHasCompass) {
            if (this.bestCompass != null) {
                this.getCommunicator().sendCompass(this.bestCompass);
                this.lastSentHasCompass = true;
            }
        }
        else if (this.bestCompass == null) {
            this.getCommunicator().sendCompass(this.bestCompass);
            this.lastSentHasCompass = false;
        }
        this.pollToolbelt();
        if (this.lightSourceChanged) {
            if (this.bestLightsource != null) {
                if (this.getCurrentTile() != null) {
                    this.getCurrentTile().setHasLightSource(this, this.bestLightsource);
                }
            }
            else if (this.bestLightsource == null && this.getCurrentTile() != null) {
                this.getCurrentTile().setHasLightSource(this, null);
                this.isLit = false;
            }
            this.lightSourceChanged = false;
        }
    }
    
    public void mute(final boolean mute, final String reason, final long expiry) {
    }
    
    public boolean isMute() {
        return false;
    }
    
    public boolean hasSleepBonus() {
        return false;
    }
    
    public void setOpponent(@Nullable final Creature _opponent) {
        if (_opponent != null && this.target == -10L && !this.isPrey()) {
            this.setTarget(_opponent.getWurmId(), true);
        }
        if (_opponent != null && _opponent.getAttackers() >= _opponent.getMaxGroupAttackSize() && (!_opponent.isPlayer() || this.isPlayer())) {
            return;
        }
        if (this.opponent != _opponent && _opponent != null && this.isPlayer() && _opponent.isPlayer()) {
            (this.battle = Battles.getBattleFor(this, _opponent)).addEvent(new BattleEvent((short)(-1), this.getName(), _opponent.getName()));
        }
        this.opponent = _opponent;
        if (this.opponent != null) {
            this.opponent.getCommunicator().changeAttitude(this.getWurmId(), this.getAttitude(this.opponent));
            if (!this.opponent.equals(this.lastOpponent)) {
                this.resetWeaponsUsed();
                this.resetAttackUsed();
                this.getCombatHandler().setCurrentStance(-1, (byte)0);
                this.lastOpponent = this.opponent;
                this.combatRound = 0;
                if (this.isPlayer() && this.opponent.isPlayer()) {
                    if (this.opponent.getKingdomId() != this.getKingdomId() && this.getKingdomId() != 0) {
                        final Kingdom k = Kingdoms.getKingdom(this.getKingdomId());
                        k.lastConfrontationTileX = this.getTileX();
                        k.lastConfrontationTileY = this.getTileY();
                    }
                    if (this.getDeity() != null) {
                        this.getDeity().lastConfrontationTileX = this.getTileX();
                        this.getDeity().lastConfrontationTileY = this.getTileY();
                    }
                }
            }
        }
        else {
            this.resetWeaponsUsed();
            this.resetAttackUsed();
        }
        this.status.sendStateString();
        if (this.isPlayer()) {
            if (this.opponent == null) {
                this.getCommunicator().sendSpecialMove((short)(-1), "N/A");
                this.getCommunicator().sendCombatOptions(CombatHandler.NO_COMBAT_OPTIONS, (short)(-1));
                this.getCombatHandler().setSentAttacks(false);
            }
            else {
                this.getCombatHandler().setSentAttacks(false);
                this.getCombatHandler().calcAttacks(false);
            }
        }
    }
    
    public boolean mayRaiseFightLevel() {
        if (this.combatRound > 2 && this.fightlevel < 5) {
            if (this.fightlevel == 0) {
                return true;
            }
            if (this.fightlevel == 1) {
                return this.getFightingSkill().getKnowledge(0.0) > 30.0;
            }
            if (this.fightlevel == 2) {
                return this.getBodyControl() > 25.0;
            }
            if (this.fightlevel == 3) {
                return this.getMindSpeed().getKnowledge(0.0) > 25.0;
            }
            if (this.fightlevel == 4) {
                return this.getSoulDepth().getKnowledge(0.0) > 25.0;
            }
        }
        return false;
    }
    
    public CombatHandler getCombatHandler() {
        if (this.combatHandler == null) {
            this.combatHandler = new CombatHandler(this);
        }
        return this.combatHandler;
    }
    
    public void removeTarget(final long targetId) {
        this.actions.removeTarget(targetId);
    }
    
    public boolean isPlayer() {
        return false;
    }
    
    public boolean isLegal() {
        return true;
    }
    
    public void setLegal(final boolean mode) {
    }
    
    public void setAutofight(final boolean mode) {
    }
    
    public void setFaithMode(final boolean mode) {
    }
    
    public Item getDraggedItem() {
        return this.movementScheme.getDraggedItem();
    }
    
    public void setDraggedItem(@Nullable final Item dragged) {
        this.movementScheme.setDraggedItem(dragged);
    }
    
    public Door getCurrentDoor() {
        return this.currentDoor;
    }
    
    public void setCurrentDoor(@Nullable final Door door) {
        this.currentDoor = door;
    }
    
    public Battle getBattle() {
        return this.battle;
    }
    
    public void setBattle(@Nullable final Battle batle) {
        this.battle = batle;
    }
    
    public void setCitizenVillage(@Nullable final Village newVillage) {
        this.citizenVillage = newVillage;
        if (this.citizenVillage != null) {
            this.setVillageSkillModifier(this.citizenVillage.getSkillModifier());
            if (this.citizenVillage.kingdom != this.getKingdomId()) {
                try {
                    this.setKingdomId(this.citizenVillage.kingdom, true);
                }
                catch (IOException ex) {}
            }
            if (this.isPlayer()) {
                ((Player)this).maybeTriggerAchievement(576, true);
            }
        }
        else {
            this.setVillageSkillModifier(0.0);
        }
        this.refreshAttitudes();
    }
    
    public Village getCitizenVillage() {
        return this.citizenVillage;
    }
    
    public void setFightingStyle(final byte style) {
        this.setFightingStyle(style, false);
    }
    
    public void setFightingStyle(final byte style, final boolean loading) {
        String mess = "";
        if (style == 2) {
            mess = "You will now fight defensively.";
        }
        else if (style == 1) {
            mess = "You will now fight aggressively.";
        }
        else {
            mess = "You will now fight normally.";
        }
        if (this.isFighting()) {
            this.getCommunicator().sendCombatNormalMessage(mess);
        }
        else {
            this.getCommunicator().sendNormalServerMessage(mess);
        }
        this.getCombatHandler().setFightingStyle(style);
        this.fightStyle = style;
        this.getCommunicator().sendFightStyle(this.fightStyle);
        this.status.sendStateString();
        if (!loading) {
            this.saveFightMode(this.fightStyle);
        }
    }
    
    public void saveFightMode(final byte mode) {
    }
    
    public byte getFightStyle() {
        return this.fightStyle;
    }
    
    public float getBaseCombatRating() {
        if (this.isPlayer()) {
            return this.template.getBaseCombatRating();
        }
        if (this.getLoyalty() > 0.0f) {
            return (this.isReborn() ? 0.7f : 0.5f) * this.template.getBaseCombatRating() * this.status.getBattleRatingTypeModifier();
        }
        return this.template.getBaseCombatRating() * this.status.getBattleRatingTypeModifier();
    }
    
    public float getBonusCombatRating() {
        return this.template.getBonusCombatRating();
    }
    
    public final boolean isOkToKillBy(final Creature attacker) {
        if (!Servers.localServer.HOMESERVER && !Servers.localServer.isChallengeServer()) {
            return true;
        }
        if (!attacker.isFriendlyKingdom(this.getKingdomId())) {
            return true;
        }
        if (Servers.isThisAChaosServer()) {
            return true;
        }
        if (this.getKingdomTemplateId() == 3) {
            return true;
        }
        if (this.hasAttackedUnmotivated()) {
            return true;
        }
        if (attacker.isDuelOrSpar(this)) {
            return true;
        }
        if (this.getReputation() < 0) {
            return true;
        }
        if (this.isInOwnDuelRing()) {
            return true;
        }
        if (Zones.isWithinDuelRing(this.getTileX(), this.getTileY(), true) != null) {
            return true;
        }
        if (attacker.getCitizenVillage() != null) {
            if (attacker.getCitizenVillage().isEnemy(this.getCitizenVillage())) {
                return true;
            }
            if (Servers.localServer.PVPSERVER) {
                final Village v = Villages.getVillageWithPerimeterAt(attacker.getTileX(), attacker.getTileY(), true);
                if (v == attacker.getCitizenVillage() && this.getCurrentVillage() == v) {
                    return true;
                }
                if (attacker.getCitizenVillage().isEnemy(this)) {
                    return true;
                }
                if (attacker.getCitizenVillage().isAlly(this.getCitizenVillage())) {
                    return false;
                }
            }
        }
        return this.isInPvPZone();
    }
    
    public final boolean isEnemyOnChaos(final Creature creature) {
        return Servers.isThisAChaosServer() && this.isInSameAlliance(creature) && false;
    }
    
    public final boolean isInSameAlliance(final Creature creature) {
        return this.getCitizenVillage() != null && creature.getCitizenVillage() != null && this.getCitizenVillage().getAllianceNumber() == creature.getCitizenVillage().getAllianceNumber();
    }
    
    public boolean hasAttackedUnmotivated() {
        if (this.isDominated() && this.getDominator() != null) {
            return this.getDominator().hasAttackedUnmotivated();
        }
        final SpellEffects effs = this.getSpellEffects();
        if (effs == null) {
            return false;
        }
        final SpellEffect eff = effs.getSpellEffect((byte)64);
        return eff != null;
    }
    
    public void setUnmotivatedAttacker() {
        if (this.isNpc()) {
            return;
        }
        if (!Servers.isThisAPvpServer() || !Servers.localServer.HOMESERVER) {
            return;
        }
        if (this.getKingdomTemplateId() != 3) {
            SpellEffects effs = this.getSpellEffects();
            if (effs == null) {
                effs = this.createSpellEffects();
            }
            SpellEffect eff = effs.getSpellEffect((byte)64);
            if (eff == null) {
                this.setVisible(false);
                Creature.logger.log(Level.INFO, this.getName() + " set unmotivated attacker at ", new Exception());
                eff = new SpellEffect(this.getWurmId(), (byte)64, 100.0f, (int)(Servers.isThisATestServer() ? 120L : 1800L), (byte)1, (byte)1, true);
                effs.addSpellEffect(eff);
                this.setVisible(true);
                this.getCommunicator().sendAlertServerMessage("You have received the hunted status and may be attacked without penalty for half an hour.");
                if (this.getCitizenVillage() != null) {
                    this.getCitizenVillage().setVillageRep(this.getCitizenVillage().getVillageReputation() + 10);
                }
                final Achievements ach = Achievements.getAchievementObject(this.getWurmId());
                if (ach != null && ach.getAchievement(369) != null) {
                    this.achievement(373);
                    this.removeTitle(Titles.Title.Knigt);
                    this.addTitle(Titles.Title.FallenKnight);
                }
            }
            else {
                eff.setTimeleft(1800);
                this.sendUpdateSpellEffect(eff);
            }
        }
    }
    
    public void addAttacker(final Creature creature) {
        if (!this.isDuelOrSpar(creature)) {
            if (this.isSpiritGuard() && this.getCitizenVillage() != null && !this.getCitizenVillage().containsTarget(creature)) {
                this.getCitizenVillage().addTarget(creature);
            }
            if (this.attackers == null) {
                this.attackers = new HashMap<Long, Long>();
            }
            if (creature.isPlayer()) {
                if (!this.isInvulnerable()) {
                    this.setSecondsToLogout(this.getSecondsToLogout());
                }
                if (this.isPlayer()) {
                    if (!this.isOkToKillBy(creature) && !creature.hasBeenAttackedBy(this.getWurmId())) {
                        creature.setUnmotivatedAttacker();
                    }
                }
                else if (this.isRidden()) {
                    if (creature.getCitizenVillage() == null || this.getCurrentVillage() != creature.getCitizenVillage()) {
                        for (final Long riderLong : this.getRiders()) {
                            try {
                                final Creature rider = Server.getInstance().getCreature(riderLong);
                                if (rider == creature) {
                                    continue;
                                }
                                if (!creature.hasBeenAttackedBy(rider.getWurmId()) && !creature.hasBeenAttackedBy(this.getWurmId()) && !rider.isOkToKillBy(creature)) {
                                    creature.setUnmotivatedAttacker();
                                }
                                rider.addAttacker(creature);
                            }
                            catch (NoSuchCreatureException ex) {}
                            catch (NoSuchPlayerException ex2) {}
                        }
                    }
                }
                else if (this.getHitched() != null) {
                    if (Servers.localServer.HOMESERVER && (creature.getCitizenVillage() == null || this.getCurrentVillage() != creature.getCitizenVillage()) && !this.getHitched().isCreature()) {
                        try {
                            final Item i = Items.getItem(this.getHitched().wurmid);
                            final long ownid = i.getLastOwnerId();
                            try {
                                if (ownid != creature.getWurmId()) {
                                    final byte kingd = Players.getInstance().getKingdomForPlayer(ownid);
                                    if (creature.isFriendlyKingdom(kingd) && !creature.hasBeenAttackedBy(ownid)) {
                                        creature.setUnmotivatedAttacker();
                                    }
                                }
                            }
                            catch (Exception ex3) {}
                        }
                        catch (NoSuchItemException nsi) {
                            Creature.logger.log(Level.INFO, this.getHitched().wurmid + " no such item:", nsi);
                        }
                    }
                }
                else if (this.isDominated()) {
                    if (Servers.localServer.HOMESERVER) {
                        this.attackers.put(creature.getWurmId(), System.currentTimeMillis());
                        if (creature.isFriendlyKingdom(this.getKingdomId()) && !creature.hasBeenAttackedBy(this.dominator) && !creature.hasBeenAttackedBy(this.getWurmId()) && creature != this.getDominator()) {
                            creature.setUnmotivatedAttacker();
                        }
                    }
                }
                else if (this.getCurrentVillage() != null && Servers.localServer.HOMESERVER) {
                    final Brand brand = Creatures.getInstance().getBrand(this.getWurmId());
                    if (brand != null) {
                        try {
                            final Village villageBrand = Villages.getVillage((int)brand.getBrandId());
                            if (this.getCurrentVillage() == villageBrand && creature.getCitizenVillage() != villageBrand && !villageBrand.isEnemy(creature.getCitizenVillage())) {
                                creature.setUnmotivatedAttacker();
                            }
                        }
                        catch (NoSuchVillageException nsv) {
                            brand.deleteBrand();
                        }
                    }
                }
                if (!creature.hasAddedToAttack) {
                    this.attackers.put(creature.getWurmId(), System.currentTimeMillis());
                }
            }
            else if (!creature.hasAddedToAttack) {
                this.attackers.put(creature.getWurmId(), System.currentTimeMillis());
            }
            if (!creature.hasAddedToAttack) {
                ++this.numattackers;
                creature.hasAddedToAttack = true;
            }
        }
    }
    
    public int getSecondsToLogout() {
        return 300;
    }
    
    public boolean hasBeenAttackedBy(final long _id) {
        if (!this.isPlayer()) {
            return false;
        }
        if (this.attackers == null) {
            return false;
        }
        final Long l = _id;
        return this.attackers.keySet().contains(l);
    }
    
    public long[] getLatestAttackers() {
        if (this.attackers != null && this.attackers.size() > 0) {
            final Long[] lKeys = this.attackers.keySet().toArray(new Long[this.attackers.size()]);
            final long[] toReturn = new long[lKeys.length];
            for (int x = 0; x < toReturn.length; ++x) {
                toReturn[x] = lKeys[x];
            }
            return toReturn;
        }
        return Creature.EMPTY_LONG_PRIMITIVE_ARRAY;
    }
    
    protected long[] getAttackerIds() {
        if (this.attackers == null) {
            return Creature.EMPTY_LONG_PRIMITIVE_ARRAY;
        }
        final Long[] longs = this.attackers.keySet().toArray(new Long[this.attackers.size()]);
        final long[] ll = new long[longs.length];
        for (int x = 0; x < longs.length; ++x) {
            ll[x] = longs[x];
        }
        return ll;
    }
    
    public void trimAttackers(final boolean delete) {
        if (delete) {
            this.attackers = null;
        }
        else if (this.attackers != null && this.attackers.size() > 0) {
            final Long[] array;
            final Long[] lKeys = array = this.attackers.keySet().toArray(new Long[this.attackers.size()]);
            for (final Long lLKey : array) {
                final Long time = this.attackers.get(lLKey);
                if (WurmId.getType(lLKey) == 1) {
                    if (System.currentTimeMillis() - time > 180000L) {
                        this.attackers.remove(lLKey);
                    }
                }
                else if (System.currentTimeMillis() - time > 300000L) {
                    this.attackers.remove(lLKey);
                }
            }
            if (this.attackers.isEmpty()) {
                this.attackers = null;
            }
        }
    }
    
    public void setMilked(final boolean aMilked) {
        this.milked = aMilked;
    }
    
    public void setSheared(final boolean isSheared) {
        this.sheared = isSheared;
    }
    
    public boolean isMilked() {
        return this.milked;
    }
    
    public boolean isSheared() {
        return this.sheared;
    }
    
    public int getAttackers() {
        return this.numattackers;
    }
    
    public int getLastAttackers() {
        return this.numattackerslast;
    }
    
    public final boolean hasBeenAttackedWithin(final int seconds) {
        if (this.attackers != null) {
            for (final Long l : this.attackers.values()) {
                if (System.currentTimeMillis() - l < seconds * 1000) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void setCurrentVillage(final Village newVillage) {
        if (this.currentVillage == null) {
            if (newVillage != null) {
                this.getCommunicator().sendNormalServerMessage("You enter " + newVillage.getName() + ".");
                newVillage.checkIfRaiseAlert(this);
                if (this.isPlayer() && this.getHighwayPathDestination().length() > 0 && this.getHighwayPathDestination().equalsIgnoreCase(newVillage.getName())) {
                    this.getCommunicator().sendNormalServerMessage("You have arrived at your destination.");
                    this.setLastWaystoneChecked(-10L);
                    this.setHighwayPath("", null);
                    if (this.isPlayer()) {
                        for (final Item waystone : Items.getWaystones()) {
                            final VolaTile vt = Zones.getTileOrNull(waystone.getTileX(), waystone.getTileY(), waystone.isOnSurface());
                            if (vt != null) {
                                for (final VirtualZone vz : vt.getWatchers()) {
                                    try {
                                        if (vz.getWatcher().getWurmId() == this.getWurmId()) {
                                            this.getCommunicator().sendWaystoneData(waystone);
                                            break;
                                        }
                                    }
                                    catch (Exception e) {
                                        Creature.logger.log(Level.WARNING, e.getMessage(), e);
                                    }
                                }
                            }
                        }
                    }
                }
                if (this.getLogger() != null) {
                    this.getLogger().log(Level.INFO, this.getName() + " enters " + newVillage.getName() + ".");
                }
            }
        }
        else if (!this.currentVillage.equals(newVillage)) {
            if (newVillage == null) {
                this.getCommunicator().sendNormalServerMessage("You leave " + this.currentVillage.getName() + ".");
                if (!this.isFighting()) {
                    this.currentVillage.removeTarget(this);
                }
                if (this.getLogger() != null) {
                    this.getLogger().log(Level.INFO, this.getName() + " leaves " + this.currentVillage.getName() + ".");
                }
            }
            if (newVillage != null) {
                this.getCommunicator().sendNormalServerMessage("You enter " + newVillage.getName() + ".");
                newVillage.checkIfRaiseAlert(this);
                if (this.getLogger() != null) {
                    this.getLogger().log(Level.INFO, this.getName() + " enters " + newVillage.getName() + ".");
                }
            }
        }
        this.currentVillage = newVillage;
    }
    
    public Village getCurrentVillage() {
        return this.currentVillage;
    }
    
    public boolean isVisible() {
        return this.status.visible;
    }
    
    public void refreshVisible() {
        if (!this.isVisible()) {
            return;
        }
        this.setVisible(false);
        this.setVisible(true);
    }
    
    public void setVisible(final boolean visible) {
        this.status.visible = visible;
        if (this.getStatus().offline) {
            this.status.visible = false;
        }
        else {
            final int tilex = this.getTileX();
            final int tiley = this.getTileY();
            try {
                final Zone zone = Zones.getZone(tilex, tiley, this.isOnSurface());
                final VolaTile tile = zone.getOrCreateTile(tilex, tiley);
                if (visible) {
                    try {
                        if (!this.isDead()) {
                            tile.makeVisible(this);
                        }
                    }
                    catch (NoSuchCreatureException nsc) {
                        Creature.logger.log(Level.INFO, nsc.getMessage() + " " + this.id + ", " + this.name, nsc);
                    }
                    catch (NoSuchPlayerException nsp) {
                        Creature.logger.log(Level.INFO, nsp.getMessage() + " " + this.id + ", " + this.name, nsp);
                    }
                }
                else {
                    tile.makeInvisible(this);
                }
            }
            catch (NoSuchZoneException nsz) {
                Creature.logger.log(Level.INFO, this.getName() + " outside of bounds when going invis.");
            }
            if (this.isPlayer()) {
                if (!this.status.visible) {
                    Players.getInstance().partChannels((Player)this);
                }
                else {
                    Players.getInstance().joinChannels((Player)this);
                }
            }
            this.status.sendStateString();
        }
    }
    
    public void calculateZoneBonus(final int tilex, final int tiley, final boolean surfaced) {
        try {
            if (Servers.localServer.HOMESERVER) {
                if (this.currentKingdom == 0) {
                    this.currentKingdom = Servers.localServer.KINGDOM;
                }
            }
            else {
                this.setCurrentKingdom(this.getCurrentKingdom());
            }
            this.zoneBonus = 0.0f;
            final Deity deity = this.getDeity();
            if (deity != null) {
                final FaithZone z = Zones.getFaithZone(tilex, tiley, surfaced);
                if (z != null) {
                    if (z.getCurrentRuler() == deity) {
                        if (this.getFaith() > 30.0f) {
                            this.zoneBonus += 10.0f;
                        }
                        if (this.getFaith() > 90.0f) {
                            this.zoneBonus += this.getFaith() - 90.0f;
                        }
                        if (Features.Feature.NEWDOMAINS.isEnabled()) {
                            this.zoneBonus += z.getStrengthForTile(tilex, tiley, surfaced) / 2.0f;
                        }
                        else {
                            this.zoneBonus += z.getStrength() / 2.0f;
                        }
                    }
                    else if ((Features.Feature.NEWDOMAINS.isEnabled() ? z.getStrengthForTile(tilex, tiley, surfaced) : z.getStrength()) == 0 && this.getFaith() >= 90.0f) {
                        this.zoneBonus = 5.0f + this.getFaith() - 90.0f;
                    }
                }
                else if (this.getFaith() >= 90.0f) {
                    this.zoneBonus = 5.0f + this.getFaith() - 90.0f;
                }
            }
        }
        catch (NoSuchZoneException nsz) {
            Creature.logger.log(Level.WARNING, "No faith zone at " + tilex + "," + tiley + ", surf=" + surfaced);
        }
    }
    
    public boolean mustChangeTerritory() {
        return false;
    }
    
    protected byte getLastTaggedKingdom() {
        return this.currentKingdom;
    }
    
    public void setLastTaggedTerr(final byte newKingdom) {
    }
    
    public void setCurrentKingdom(final byte newKingdom) {
        if (this.currentKingdom == 0) {
            if (newKingdom != 0) {
                this.getCommunicator().sendNormalServerMessage("You enter " + Kingdoms.getNameFor(newKingdom) + ".");
                if (Servers.localServer.isChallengeOrEpicServer() && this.getLastTaggedKingdom() != newKingdom) {
                    if (this.mustChangeTerritory()) {
                        this.getCommunicator().sendSafeServerMessage("You feel an energy boost, as if " + this.getDeity().getName() + " turns " + this.getDeity().getHisHerItsString() + " eyes at you.");
                    }
                    this.setLastTaggedTerr(newKingdom);
                }
                if (newKingdom != this.getKingdomId()) {
                    this.achievement(374);
                }
                if (this.musicPlayer != null && this.musicPlayer.isItOkToPlaySong(true)) {
                    if (newKingdom != this.getKingdomTemplateId()) {
                        if (Kingdoms.getKingdomTemplateFor(newKingdom) == 3 && Kingdoms.getKingdomTemplateFor(this.getKingdomId()) != 3) {
                            this.musicPlayer.checkMUSIC_TERRITORYHOTS_SND();
                        }
                        else if (Kingdoms.getKingdomTemplateFor(this.getKingdomId()) == 3) {
                            this.musicPlayer.checkMUSIC_TERRITORYWL_SND();
                        }
                    }
                    else {
                        this.playAnthem();
                    }
                }
            }
        }
        else if (newKingdom != this.currentKingdom) {
            if (newKingdom == 0) {
                this.getCommunicator().sendNormalServerMessage("You leave " + Kingdoms.getNameFor(this.currentKingdom) + ".");
            }
            if (newKingdom != 0) {
                this.getCommunicator().sendNormalServerMessage("You enter " + Kingdoms.getNameFor(newKingdom) + ".");
                if (this.getPower() <= 0 && this.musicPlayer != null && this.musicPlayer.isItOkToPlaySong(true)) {
                    if (newKingdom != this.getKingdomId()) {
                        this.achievement(374);
                        if (newKingdom == 3 && this.getKingdomId() != 3) {
                            this.musicPlayer.checkMUSIC_TERRITORYHOTS_SND();
                        }
                        else if (this.getKingdomId() == 3) {
                            this.musicPlayer.checkMUSIC_TERRITORYWL_SND();
                        }
                        final Appointments p = King.getCurrentAppointments(newKingdom);
                        if (p != null) {
                            final long secret = p.getOfficialForId(1500);
                            if (secret > 0L) {
                                try {
                                    final Creature c = Server.getInstance().getCreature(secret);
                                    if (c.getMindLogical().skillCheck(40.0, 0.0, false, 1.0f) > 0.0) {
                                        c.getCommunicator().sendNormalServerMessage("Your informers relay information that " + this.getName() + " has entered your territory.");
                                    }
                                }
                                catch (Exception ex) {}
                            }
                        }
                    }
                    else {
                        this.playAnthem();
                    }
                }
            }
        }
        this.currentKingdom = newKingdom;
    }
    
    public void setCurrentDeity(final Deity deity) {
        if (deity != null) {
            if (this.currentDeity != deity.number) {
                this.currentDeity = (byte)deity.number;
                this.getCommunicator().sendNormalServerMessage("You feel the presence of " + deity.name + ".");
            }
        }
        else if (this.currentDeity != 0) {
            this.getCommunicator().sendNormalServerMessage("You no longer feel the presence of " + Deities.getDeity(this.currentDeity).name + ".");
            this.currentDeity = 0;
        }
    }
    
    public Creature(final long aId) throws Exception {
        this();
        this.setWurmId(aId, 0.0f, 0.0f, 0.0f, 0);
        this.skills = SkillsFactory.createSkills(aId);
    }
    
    public final void loadTemplate() {
        this.template = this.status.getTemplate();
        this.getMovementScheme().initalizeModifiersWithTemplate();
        this.breedCounter = (Servers.isThisAPvpServer() ? 900 : 2000) + Server.rand.nextInt(1000);
    }
    
    public Creature setWurmId(final long aId, final float posx, final float posy, final float aRot, final int layer) throws Exception {
        this.id = aId;
        this.status = CreatureStatusFactory.createCreatureStatus(this, posx, posy, aRot, layer);
        this.getMovementScheme().setBridgeId(this.getBridgeId());
        return this;
    }
    
    public void postLoad() throws Exception {
        this.loadSkills();
        if (!this.isDead() && !this.isOffline()) {
            this.createVisionArea();
        }
        if (this.getTemplate().getCreatureAI() != null) {
            this.getTemplate().getCreatureAI().creatureCreated(this);
        }
    }
    
    public TradeHandler getTradeHandler() {
        if (this.tradeHandler == null) {
            this.tradeHandler = new TradeHandler(this, this.getStatus().getTrade());
        }
        return this.tradeHandler;
    }
    
    public void endTrade() {
        this.tradeHandler.end();
        this.tradeHandler = null;
    }
    
    public void addItemTaken(final Item item) {
        if (this.itemsTaken == null) {
            this.itemsTaken = new HashSet<Item>();
        }
        this.itemsTaken.add(item);
    }
    
    public void addItemDropped(final Item item) {
        this.checkTheftWarnQuestion();
        if (this.itemsDropped == null) {
            this.itemsDropped = new HashSet<Item>();
        }
        this.itemsDropped.add(item);
    }
    
    public void addChallengeScore(final int type, final float scoreAdded) {
    }
    
    protected void sendItemsTaken() {
        if (this.itemsTaken != null) {
            PlayerTutorial.firePlayerTrigger(this.getWurmId(), PlayerTutorial.PlayerTrigger.TAKEN_ITEM);
            final Map<Integer, Integer> diffItems = new HashMap<Integer, Integer>();
            final Map<String, Integer> foodItems = new HashMap<String, Integer>();
            for (final Item item : this.itemsTaken) {
                if (item.isFood()) {
                    final String name = item.getName();
                    if (foodItems.containsKey(name)) {
                        final Integer num = foodItems.get(name);
                        int nums = num;
                        ++nums;
                        foodItems.put(name, nums);
                    }
                    else {
                        foodItems.put(name, 1);
                    }
                }
                else {
                    final Integer templateId = item.getTemplateId();
                    if (diffItems.containsKey(templateId)) {
                        final Integer num = diffItems.get(templateId);
                        int nums = num;
                        ++nums;
                        diffItems.put(templateId, nums);
                    }
                    else {
                        diffItems.put(templateId, 1);
                    }
                }
            }
            for (final Integer key : diffItems.keySet()) {
                try {
                    final ItemTemplate lTemplate = ItemTemplateFactory.getInstance().getTemplate(key);
                    final Integer num = diffItems.get(key);
                    final int number = num;
                    if (number == 1) {
                        this.getCommunicator().sendNormalServerMessage("You get " + lTemplate.getNameWithGenus() + ".");
                        if (!this.isVisible()) {
                            continue;
                        }
                        Server.getInstance().broadCastAction(this.name + " gets " + lTemplate.getNameWithGenus() + ".", this, 5);
                    }
                    else {
                        this.getCommunicator().sendNormalServerMessage("You get " + StringUtilities.getWordForNumber(number) + " " + lTemplate.sizeString + lTemplate.getPlural() + ".");
                        if (!this.isVisible()) {
                            continue;
                        }
                        Server.getInstance().broadCastAction(this.name + " gets " + StringUtilities.getWordForNumber(number) + " " + lTemplate.sizeString + lTemplate.getPlural() + ".", this, 5);
                    }
                }
                catch (NoSuchTemplateException nst) {
                    Creature.logger.log(Level.WARNING, nst.getMessage(), nst);
                }
            }
            for (final String key2 : foodItems.keySet()) {
                final Integer num2 = foodItems.get(key2);
                final int number2 = num2;
                if (number2 == 1) {
                    this.getCommunicator().sendNormalServerMessage("You get " + StringUtilities.addGenus(key2) + ".");
                    if (!this.isVisible()) {
                        continue;
                    }
                    Server.getInstance().broadCastAction(this.name + " gets " + StringUtilities.addGenus(key2) + ".", this, 5);
                }
                else {
                    this.getCommunicator().sendNormalServerMessage("You get " + StringUtilities.getWordForNumber(number2) + " " + key2 + ".");
                    if (!this.isVisible()) {
                        continue;
                    }
                    Server.getInstance().broadCastAction(this.name + " gets " + StringUtilities.getWordForNumber(number2) + " " + key2 + ".", this, 5);
                }
            }
            this.itemsTaken = null;
        }
    }
    
    public boolean isIgnored(final long playerId) {
        return false;
    }
    
    public void sendItemsDropped() {
        if (this.itemsDropped != null) {
            final Map<Integer, Integer> diffItems = new HashMap<Integer, Integer>();
            final Map<String, Integer> foodItems = new HashMap<String, Integer>();
            for (final Item item : this.itemsDropped) {
                if (item.isFood()) {
                    final String name = item.getName();
                    if (foodItems.containsKey(name)) {
                        final Integer num = foodItems.get(name);
                        int nums = num;
                        ++nums;
                        foodItems.put(name, nums);
                    }
                    else {
                        foodItems.put(name, 1);
                    }
                }
                else {
                    final Integer templateId = item.getTemplateId();
                    if (diffItems.containsKey(templateId)) {
                        final Integer num = diffItems.get(templateId);
                        int nums = num;
                        ++nums;
                        diffItems.put(templateId, nums);
                    }
                    else {
                        diffItems.put(templateId, 1);
                    }
                }
            }
            for (final Integer key : diffItems.keySet()) {
                try {
                    final ItemTemplate lTemplate = ItemTemplateFactory.getInstance().getTemplate(key);
                    final Integer num = diffItems.get(key);
                    final int number = num;
                    if (number == 1) {
                        this.getCommunicator().sendNormalServerMessage("You drop " + lTemplate.getNameWithGenus() + ".");
                        Server.getInstance().broadCastAction(this.name + " drops " + lTemplate.getNameWithGenus() + ".", this, Math.max(3, lTemplate.getSizeZ() / 10));
                    }
                    else {
                        this.getCommunicator().sendNormalServerMessage("You drop " + StringUtilities.getWordForNumber(number) + " " + lTemplate.getPlural() + ".");
                        Server.getInstance().broadCastAction(this.name + " drops " + StringUtilities.getWordForNumber(number) + " " + lTemplate.getPlural() + ".", this, 5);
                    }
                }
                catch (NoSuchTemplateException nst) {
                    Creature.logger.log(Level.WARNING, nst.getMessage(), nst);
                }
            }
            for (final String key2 : foodItems.keySet()) {
                final Integer num2 = foodItems.get(key2);
                final int number2 = num2;
                if (number2 == 1) {
                    this.getCommunicator().sendNormalServerMessage("You drop " + StringUtilities.addGenus(key2) + ".");
                    Server.getInstance().broadCastAction(this.name + " drops " + StringUtilities.addGenus(key2) + ".", this, 5);
                }
                else {
                    this.getCommunicator().sendNormalServerMessage("You drop " + StringUtilities.getWordForNumber(number2) + " " + key2 + ".");
                    Server.getInstance().broadCastAction(this.name + " drops " + StringUtilities.getWordForNumber(number2) + " " + key2 + ".", this, 5);
                }
            }
            this.itemsDropped = null;
        }
    }
    
    public String getHoverText(@Nonnull final Creature watcher) {
        String hoverText = "";
        if ((!watcher.isPlayer() || !watcher.hasFlag(57)) && (!this.isPlayer() || !this.hasFlag(58)) && this.getCitizenVillage() != null && this.isPlayer()) {
            hoverText = hoverText + this.getCitizenVillage().getCitizen(this.getWurmId()).getRole().getName() + " of " + this.getCitizenVillage().getName();
        }
        return hoverText;
    }
    
    public String getNameWithGenus() {
        if (this.isUnique() || this.isPlayer()) {
            return this.getName();
        }
        if (this.name.toLowerCase().compareTo(this.template.getName().toLowerCase()) != 0) {
            return "the " + this.getName();
        }
        if (this.template.isVowel(this.getName().substring(0, 1))) {
            return "an " + this.getName();
        }
        return "a " + this.getName();
    }
    
    public void setTrade(@Nullable final Trade trade) {
        this.status.setTrade(trade);
    }
    
    public Trade getTrade() {
        return this.status.getTrade();
    }
    
    public boolean isTrading() {
        return this.status.isTrading();
    }
    
    public boolean isLeadable(final Creature potentialLeader) {
        if (this.hitchedTo != null) {
            return false;
        }
        if (this.riders != null && this.riders.size() > 0) {
            return false;
        }
        if (this.isDominated()) {
            return this.getDominator() != null && this.getDominator().equals(potentialLeader);
        }
        return this.template.isLeadable();
    }
    
    public boolean isOffline() {
        return this.getStatus().offline;
    }
    
    public boolean isLoggedOut() {
        return false;
    }
    
    public boolean isStayonline() {
        return this.getStatus().stayOnline;
    }
    
    public boolean setStayOnline(final boolean stayOnline) {
        return this.getStatus().setStayOnline(stayOnline);
    }
    
    void setOffline(final boolean offline) {
        this.getStatus().setOffline(offline);
    }
    
    public Creature getLeader() {
        return this.leader;
    }
    
    public void setWounded() {
        this.removeIllusion();
        if (this.damageCounter == 0) {
            this.addWoundMod();
        }
        this.playAnimation("wounded", false);
        this.damageCounter = (short)(30.0f * ItemBonus.getHurtingReductionBonus(this));
        this.setStealth(false);
        this.getStatus().sendStateString();
    }
    
    private void addWoundMod() {
        this.getMovementScheme().addModifier(this.woundMoveMod);
        if (this.isPlayer()) {
            this.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.WOUNDMOVE, 100000, 100.0f);
        }
    }
    
    public void removeWoundMod() {
        this.getMovementScheme().removeModifier(this.woundMoveMod);
        if (this.isPlayer()) {
            this.getCommunicator().sendRemoveSpellEffect(SpellEffectsEnum.WOUNDMOVE);
        }
    }
    
    public boolean isEncumbered() {
        return this.carriedWeight >= this.encumbered;
    }
    
    public boolean isMoveSlow() {
        return this.carriedWeight >= this.moveslow;
    }
    
    public boolean isCantMove() {
        return this.carriedWeight >= this.cantmove;
    }
    
    public int getMovePenalty() {
        if (this.isMoveSlow()) {
            return 5;
        }
        if (this.isEncumbered()) {
            return 10;
        }
        if (this.isCantMove()) {
            return 20;
        }
        return 0;
    }
    
    public final int getMoveSlow() {
        return this.moveslow;
    }
    
    private void setMoveLimits() {
        if (this.getPower() > 1) {
            this.moveslow = Integer.MAX_VALUE;
            this.encumbered = Integer.MAX_VALUE;
            this.cantmove = Integer.MAX_VALUE;
            if (this.movementScheme.stealthMod == null) {
                this.movementScheme.stealthMod = new DoubleValueModifier(-(80.0 - Math.min(79.0, this.getBodyControl())) / 100.0);
            }
            else {
                this.movementScheme.stealthMod.setModifier(-(80.0 - Math.min(79.0, this.getBodyControl())) / 100.0);
            }
        }
        else {
            try {
                final Skill strength = this.skills.getSkill(102);
                this.moveslow = (int)strength.getKnowledge(0.0) * 2000;
                this.encumbered = (int)strength.getKnowledge(0.0) * 3500;
                this.cantmove = (int)strength.getKnowledge(0.0) * 7000;
                if (this.movementScheme.stealthMod == null) {
                    this.movementScheme.stealthMod = new DoubleValueModifier(-(80.0 - Math.min(79.0, this.getBodyControl())) / 100.0);
                }
                else {
                    this.movementScheme.stealthMod.setModifier(-(80.0 - Math.min(79.0, this.getBodyControl())) / 100.0);
                }
            }
            catch (NoSuchSkillException nss) {
                Creature.logger.log(Level.WARNING, "No strength skill for " + this, nss);
            }
        }
    }
    
    public void calcBaseMoveMod() {
        if (this.carriedWeight < this.moveslow) {
            this.movementScheme.setEncumbered(false);
            this.movementScheme.setBaseModifier(1.0f);
        }
        else if (this.carriedWeight >= this.cantmove) {
            this.movementScheme.setEncumbered(true);
            this.movementScheme.setBaseModifier(0.05f);
            this.getCommunicator().sendAlertServerMessage("You are encumbered and move extremely slow.");
        }
        else if (this.carriedWeight >= this.encumbered) {
            this.movementScheme.setEncumbered(false);
            this.movementScheme.setBaseModifier(0.25f);
        }
        else if (this.carriedWeight >= this.moveslow) {
            this.movementScheme.setEncumbered(false);
            this.movementScheme.setBaseModifier(0.75f);
        }
    }
    
    public void addCarriedWeight(final int weight) {
        boolean canTriggerPlonk = false;
        if (this.isPlayer()) {
            if (this.carriedWeight < this.moveslow) {
                if (this.carriedWeight + weight >= this.cantmove) {
                    this.movementScheme.setEncumbered(true);
                    this.movementScheme.setBaseModifier(0.05f);
                    this.getCommunicator().sendAlertServerMessage("You are encumbered and move extremely slow.");
                    canTriggerPlonk = true;
                }
                else if (this.carriedWeight + weight >= this.encumbered) {
                    this.movementScheme.setBaseModifier(0.25f);
                    canTriggerPlonk = true;
                }
                else if (this.carriedWeight + weight >= this.moveslow) {
                    this.movementScheme.setBaseModifier(0.75f);
                    canTriggerPlonk = true;
                }
            }
            else if (this.carriedWeight < this.encumbered) {
                if (this.carriedWeight + weight >= this.cantmove) {
                    this.movementScheme.setEncumbered(true);
                    this.movementScheme.setBaseModifier(0.05f);
                    this.getCommunicator().sendAlertServerMessage("You are encumbered and move extremely slow.");
                    canTriggerPlonk = true;
                }
                else if (this.carriedWeight + weight >= this.encumbered) {
                    this.movementScheme.setBaseModifier(0.25f);
                    canTriggerPlonk = true;
                }
            }
            else if (this.carriedWeight < this.cantmove && this.carriedWeight + weight >= this.cantmove) {
                this.movementScheme.setEncumbered(true);
                this.movementScheme.setBaseModifier(0.05f);
                this.getCommunicator().sendAlertServerMessage("You are encumbered and move extremely slow.");
                canTriggerPlonk = true;
            }
            if (canTriggerPlonk && !PlonkData.ENCUMBERED.hasSeenThis(this)) {
                PlonkData.ENCUMBERED.trigger(this);
            }
            this.carriedWeight += weight;
            if (this.getVehicle() != -10L) {
                final Creature c = Creatures.getInstance().getCreatureOrNull(this.getVehicle());
                if (c != null) {
                    c.ignoreSaddleDamage = true;
                    c.getMovementScheme().update();
                }
            }
        }
        else {
            this.carriedWeight += weight;
            this.ignoreSaddleDamage = true;
            this.movementScheme.update();
        }
    }
    
    public boolean removeCarriedWeight(final int weight) {
        if (this.isPlayer()) {
            if (this.carriedWeight >= this.cantmove) {
                if (this.carriedWeight - weight < this.moveslow) {
                    this.movementScheme.setEncumbered(false);
                    this.movementScheme.setBaseModifier(1.0f);
                    this.getCommunicator().sendAlertServerMessage("You can now move again.");
                }
                else if (this.carriedWeight - weight < this.encumbered) {
                    this.movementScheme.setEncumbered(false);
                    this.movementScheme.setBaseModifier(0.75f);
                    this.getCommunicator().sendAlertServerMessage("You can now move again.");
                }
                else if (this.carriedWeight - weight < this.cantmove) {
                    this.movementScheme.setEncumbered(false);
                    this.movementScheme.setBaseModifier(0.25f);
                    this.getCommunicator().sendAlertServerMessage("You can now move again.");
                }
            }
            else if (this.carriedWeight >= this.encumbered) {
                if (this.carriedWeight - weight < this.moveslow) {
                    this.movementScheme.setEncumbered(false);
                    this.movementScheme.setBaseModifier(1.0f);
                }
                else if (this.carriedWeight - weight < this.encumbered) {
                    this.movementScheme.setEncumbered(false);
                    this.movementScheme.setBaseModifier(0.75f);
                }
            }
            else if (this.carriedWeight >= this.moveslow && this.carriedWeight - weight < this.moveslow) {
                this.movementScheme.setEncumbered(false);
                this.movementScheme.setBaseModifier(1.0f);
            }
            this.carriedWeight -= weight;
            if (this.getVehicle() != -10L) {
                final Creature c = Creatures.getInstance().getCreatureOrNull(this.getVehicle());
                if (c != null) {
                    c.ignoreSaddleDamage = true;
                    c.getMovementScheme().update();
                }
            }
        }
        else {
            this.carriedWeight -= weight;
            this.ignoreSaddleDamage = true;
            this.movementScheme.update();
        }
        if (this.carriedWeight < 0) {
            Creature.logger.log(Level.WARNING, "Carried weight is less than 0 for " + this);
            if (this instanceof Player) {
                Creature.logger.log(Level.INFO, this.name + " now carries " + this.carriedWeight + " AFTER removing " + weight + " gs. Modifier is:" + this.movementScheme.getSpeedModifier() + ".");
            }
            return false;
        }
        return true;
    }
    
    public boolean canCarry(final int weight) {
        try {
            if (this.getPower() > 1) {
                return true;
            }
            final Skill strength = this.skills.getSkill(102);
            return strength.getKnowledge(0.0) * 7000.0 > weight + this.carriedWeight;
        }
        catch (NoSuchSkillException nss) {
            Creature.logger.log(Level.WARNING, "No strength skill for " + this);
            return false;
        }
    }
    
    public int getCarryCapacityFor(final int weight) {
        try {
            final Skill strength = this.skills.getSkill(102);
            return (int)(strength.getKnowledge(0.0) * 7000.0 - this.carriedWeight) / weight;
        }
        catch (NoSuchSkillException nss) {
            Creature.logger.log(Level.WARNING, "No strength skill for " + this);
            return 0;
        }
    }
    
    public int getCarriedWeight() {
        return this.carriedWeight;
    }
    
    public int getSaddleBagsCarriedWeight() {
        for (final Item i : this.getBody().getAllItems()) {
            if (i.isSaddleBags()) {
                float mod = 0.5f;
                if (i.getTemplateId() == 1334) {
                    mod = 0.6f;
                }
                return (int)(i.getFullWeight() * mod);
            }
        }
        return 0;
    }
    
    public int getCarryingCapacityLeft() {
        try {
            final Skill strength = this.skills.getSkill(102);
            return (int)(strength.getKnowledge(0.0) * 7000.0) - this.carriedWeight;
        }
        catch (NoSuchSkillException nss) {
            Creature.logger.log(Level.WARNING, "No strength skill for " + this);
            return 0;
        }
    }
    
    public void setTeleportPoints(final short x, final short y, final int layer, final int floorLevel) {
        this.setTeleportPoints((x << 2) + 2.0f, (y << 2) + 2.0f, layer, floorLevel);
    }
    
    public void setTeleportPoints(final float x, final float y, final int layer, final int floorLevel) {
        this.teleportX = x;
        this.teleportY = y;
        this.teleportLayer = layer;
        this.teleportFloorLevel = floorLevel;
    }
    
    public void setTeleportLayer(final int layer) {
        this.teleportLayer = layer;
    }
    
    public void setTeleportFloorLevel(final int floorLevel) {
        this.teleportFloorLevel = floorLevel;
    }
    
    public int getTeleportLayer() {
        return this.teleportLayer;
    }
    
    public int getTeleportFloorLevel() {
        return this.teleportFloorLevel;
    }
    
    public VolaTile getCurrentTile() {
        if (this.currentTile != null) {
            return this.currentTile;
        }
        if (this.status != null) {
            final int tilex = this.getTileX();
            final int tiley = this.getTileY();
            try {
                final Zone zone = Zones.getZone(tilex, tiley, this.isOnSurface());
                return this.currentTile = zone.getOrCreateTile(tilex, tiley);
            }
            catch (NoSuchZoneException ex) {}
        }
        return null;
    }
    
    public int getCurrentTileNum() {
        final int tilex = this.getTileX();
        final int tiley = this.getTileY();
        if (this.isOnSurface()) {
            return Server.surfaceMesh.getTile(tilex, tiley);
        }
        return Server.caveMesh.getTile(tilex, tiley);
    }
    
    public void addItemsToTrade() {
        if (this.isTrader()) {
            this.getTradeHandler().addItemsToTrade();
        }
    }
    
    public boolean startTeleporting() {
        this.disembark(false);
        return this.startTeleporting(false);
    }
    
    public float getTeleportX() {
        return this.teleportX;
    }
    
    public float getTeleportY() {
        return this.teleportY;
    }
    
    public void startTrading() {
    }
    
    public boolean shouldStopTrading(final boolean firstCall) {
        if (this.isTrading()) {
            if (this.getTrade().creatureOne != null && this.getTrade().creatureOne.isPlayer() && this.getTrade().creatureOne.shouldStopTrading(false)) {
                this.getTrade().creatureOne.getCommunicator().sendAlertServerMessage("You took too long to trade and " + this.getName() + " takes care of another customer.");
                this.getTrade().end(this, false);
                return true;
            }
            if (this.getTrade().creatureTwo != null && this.getTrade().creatureTwo.isPlayer() && this.getTrade().creatureTwo.shouldStopTrading(false)) {
                this.getTrade().creatureTwo.getCommunicator().sendAlertServerMessage("You took too long to trade and " + this.getName() + " takes care of another customer.");
                this.getTrade().end(this, false);
                return true;
            }
        }
        return false;
    }
    
    public boolean startTeleporting(final boolean enterVehicle) {
        if (this.teleportLayer < 0 && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile((int)this.teleportX >> 2, (int)this.teleportY >> 2)))) {
            this.getCommunicator().sendAlertServerMessage("The teleportation target is in rock!");
            return false;
        }
        this.stopLeading();
        if (!(this._enterVehicle = enterVehicle)) {
            Creatures.getInstance().setCreatureDead(this);
            Players.getInstance().setCreatureDead(this);
        }
        this.startTeleportTime = System.currentTimeMillis();
        this.communicator.setReady(false);
        if (this.status.isTrading()) {
            this.status.getTrade().end(this, false);
        }
        if (this.movementScheme.draggedItem != null) {
            MethodsItems.stopDragging(this, this.movementScheme.draggedItem);
        }
        final int tileX = this.getTileX();
        final int tileY = this.getTileY();
        try {
            this.destroyVisionArea();
            if (!this.isDead()) {
                final Zone zone = Zones.getZone(tileX, tileY, this.isOnSurface());
                zone.deleteCreature(this, true);
            }
        }
        catch (NoSuchZoneException nsz) {
            Creature.logger.log(Level.WARNING, this.getName() + " tried to teleport to nonexistant zone at " + tileX + ", " + tileY);
        }
        catch (NoSuchCreatureException nsc) {
            Creature.logger.log(Level.WARNING, this + " creature doesn't exist?", nsc);
        }
        catch (NoSuchPlayerException nsp) {
            Creature.logger.log(Level.WARNING, this + " player doesn't exist?", nsp);
        }
        this.status.setPositionX(this.teleportX);
        this.status.setPositionY(this.teleportY);
        try {
            this.status.setLayer((this.teleportLayer >= 0) ? 0 : -1);
            boolean setOffZ = false;
            if (this.mountAction != null) {
                setOffZ = true;
            }
            if (setOffZ) {
                this.status.setPositionZ(Math.max(Zones.calculateHeight(this.teleportX, this.teleportY, this.isOnSurface()) + this.mountAction.getOffZ(), this.mountAction.getOffZ()));
                this.getMovementScheme().offZ = this.mountAction.getOffZ();
            }
            else {
                final VolaTile targetTile = Zones.getTileOrNull((int)(this.teleportX / 4.0f), (int)(this.teleportY / 4.0f), this.teleportLayer >= 0);
                final float height = (this.teleportFloorLevel > 0) ? (this.teleportFloorLevel * 3) : 0.0f;
                if (targetTile != null) {
                    this.getMovementScheme().setGroundOffset((int)(height * 10.0f), true);
                    this.calculateFloorLevel(targetTile, true);
                }
                this.status.setPositionZ(Zones.calculateHeight(this.teleportX, this.teleportY, this.isOnSurface()) + height);
            }
        }
        catch (NoSuchZoneException nsz) {
            Creature.logger.log(Level.WARNING, this.getName() + " tried to teleport to nonexistant zone at " + this.teleportX + ", " + this.teleportY);
        }
        this.getMovementScheme().setPosition(this.teleportX, this.teleportY, this.status.getPositionZ(), this.status.getRotation(), this.getLayer());
        this.getMovementScheme().haltSpeedModifier();
        boolean zoneExists = true;
        try {
            this.status.savePosition(this.getWurmId(), this.isPlayer(), Zones.getZoneIdFor((int)this.teleportX >> 2, (int)this.teleportY >> 2, this.isOnSurface()), true);
        }
        catch (IOException iox) {
            Creature.logger.log(Level.WARNING, iox.getMessage(), iox);
        }
        catch (NoSuchZoneException nsz2) {
            Creature.logger.log(Level.INFO, this.getName() + " no zone at " + ((int)this.teleportX >> 2) + ", " + ((int)this.teleportY >> 2) + ", surf=" + this.isOnSurface());
            zoneExists = false;
        }
        try {
            if (zoneExists) {
                Zones.getZone((int)this.teleportX >> 2, (int)this.teleportY >> 2, this.isOnSurface()).addCreature(this.id);
            }
            Server.getInstance().addCreatureToPort(this);
        }
        catch (Exception ex) {
            Creature.logger.log(Level.WARNING, this.getName() + " failed to recreate vision area after teleporting: " + ex.getMessage());
        }
        return true;
    }
    
    public long getPlayingTime() {
        return System.currentTimeMillis();
    }
    
    public void teleport() {
        this.teleport(true);
    }
    
    public void teleport(final boolean destroyVisionArea) {
        this.communicator.setReady(true);
        if (destroyVisionArea) {
            try {
                final Zone newzone = Zones.getZone(this.getTileX(), this.getTileY(), this.isOnSurface());
                this.addingAfterTeleport = true;
                newzone.addCreature(this.id);
                this.sendActionControl("", false, 0);
                try {
                    this.createVisionArea();
                }
                catch (Exception ex) {
                    Creature.logger.log(Level.WARNING, "Failed to create visionArea:" + ex.getMessage(), ex);
                }
                Server.getInstance().addCreatureToPort(this);
            }
            catch (NoSuchZoneException nsz) {
                Creature.logger.log(Level.WARNING, this.getName() + " tried to teleport to nonexistant zone at " + this.getTileX() + ", " + this.getTileY());
            }
            catch (NoSuchCreatureException nsc) {
                Creature.logger.log(Level.WARNING, "This creature doesn't exist?", nsc);
            }
            catch (NoSuchPlayerException nsp) {
                Creature.logger.log(Level.WARNING, "This player doesn't exist?", nsp);
            }
        }
        this.addingAfterTeleport = false;
        this.stopTeleporting();
    }
    
    public void cancelTeleport() {
        this.teleportX = -1.0f;
        this.teleportY = -1.0f;
        this.teleportLayer = 0;
        this.startTeleportTime = Long.MIN_VALUE;
    }
    
    public void sendMountData() {
        if (this._enterVehicle) {
            if (this.mountAction != null) {
                this.mountAction.sendData(this);
                final MountTransfer mt = MountTransfer.getTransferFor(this.getWurmId());
                if (mt != null) {
                    mt.remove(this.getWurmId());
                }
            }
            this.setMountAction(null);
        }
    }
    
    public void stopTeleporting() {
        if (this.isTeleporting()) {
            this.teleportX = -1.0f;
            this.teleportY = -1.0f;
            this.teleportLayer = 0;
            this.startTeleportTime = Long.MIN_VALUE;
            if (!this._enterVehicle) {
                this.getMovementScheme().setMooredMod(false);
                this.getMovementScheme().addWindImpact((byte)0);
                this.disembark(false);
                this.setMountAction(null);
                this.calcBaseMoveMod();
            }
            if (this.isPlayer()) {
                ((Player)this).sentClimbing = 0L;
                ((Player)this).sentMountSpeed = 0L;
                ((Player)this).sentWind = 0L;
                if (!this._enterVehicle) {
                    try {
                        if (this.getLayer() >= 0) {
                            this.getVisionArea().getSurface().checkIfEnemyIsPresent(false);
                        }
                        else {
                            this.getVisionArea().getUnderGround().checkIfEnemyIsPresent(false);
                        }
                    }
                    catch (Exception ex) {}
                }
            }
            this._enterVehicle = false;
            if (!this.getCommunicator().stillLoggingIn() || !this.isPlayer()) {
                this.setTeleporting(false);
            }
            if (this.justSpawned) {
                this.justSpawned = false;
            }
        }
    }
    
    public boolean isWithinTeleportTime() {
        return System.currentTimeMillis() - this.startTeleportTime < 30000L;
    }
    
    public final boolean isTeleporting() {
        return this.isTeleporting;
    }
    
    public final void setTeleporting(final boolean teleporting) {
        this.isTeleporting = teleporting;
    }
    
    public Body getBody() {
        return this.status.getBody();
    }
    
    public String examine() {
        return this.template.examine();
    }
    
    public void setSpam(final boolean spam) {
    }
    
    public boolean spamMode() {
        return false;
    }
    
    public byte getSex() {
        if (this.status.getSex() == 127) {
            return this.template.getSex();
        }
        return this.status.getSex();
    }
    
    public boolean setSex(final byte sex, final boolean creation) {
        this.status.setSex(sex);
        if (!creation && this.currentTile != null) {
            this.refreshVisible();
        }
        return true;
    }
    
    public final void spawnFreeItems() {
        if (Features.Feature.FREE_ITEMS.isEnabled()) {
            if (this.spawnWeapon != null && this.spawnWeapon.length() > 0) {
                TestQuestion.createAndInsertItems(this, 319, 319, 40.0f, true, (byte)(-1));
                try {
                    final int w = Integer.parseInt(this.spawnWeapon);
                    int lTemplate = 0;
                    boolean shield = false;
                    switch (w) {
                        case 1: {
                            lTemplate = 21;
                            shield = true;
                            break;
                        }
                        case 2: {
                            lTemplate = 81;
                            break;
                        }
                        case 3: {
                            lTemplate = 90;
                            shield = true;
                            break;
                        }
                        case 4: {
                            lTemplate = 87;
                            break;
                        }
                        case 5: {
                            lTemplate = 292;
                            shield = true;
                            break;
                        }
                        case 6: {
                            lTemplate = 290;
                            break;
                        }
                        case 7: {
                            lTemplate = 706;
                            break;
                        }
                        case 8: {
                            lTemplate = 705;
                            break;
                        }
                    }
                    if (lTemplate > 0) {
                        try {
                            TestQuestion.createAndInsertItems(this, lTemplate, lTemplate, 40.0f, true, (byte)(-1));
                            if (shield) {
                                TestQuestion.createAndInsertItems(this, 84, 84, 40.0f, true, (byte)(-1));
                            }
                        }
                        catch (Exception ex) {
                            Creature.logger.log(Level.INFO, "Failed to create item for spawning.", ex);
                            this.getCommunicator().sendAlertServerMessage("Failed to spawn weapon.");
                        }
                    }
                }
                catch (Exception ex2) {
                    this.getCommunicator().sendAlertServerMessage("Failed to spawn weapon.");
                }
            }
            this.spawnWeapon = null;
            if (this.spawnArmour != null && this.spawnArmour.length() > 0) {
                try {
                    final int arm = Integer.parseInt(this.spawnArmour);
                    float ql = 20.0f;
                    final byte matType = -1;
                    switch (arm) {
                        case 1: {
                            ql = 40.0f;
                            TestQuestion.createAndInsertItems(this, 274, 279, ql, true, (byte)(-1));
                            TestQuestion.createAndInsertItems(this, 278, 278, ql, true, (byte)(-1));
                            TestQuestion.createAndInsertItems(this, 274, 274, ql, true, (byte)(-1));
                            TestQuestion.createAndInsertItems(this, 277, 277, ql, true, (byte)(-1));
                            break;
                        }
                        case 2: {
                            ql = 60.0f;
                            TestQuestion.createAndInsertItems(this, 103, 108, ql, true, (byte)(-1));
                            TestQuestion.createAndInsertItems(this, 103, 103, ql, true, (byte)(-1));
                            TestQuestion.createAndInsertItems(this, 105, 105, ql, true, (byte)(-1));
                            TestQuestion.createAndInsertItems(this, 106, 106, ql, true, (byte)(-1));
                            break;
                        }
                        case 3: {
                            ql = 20.0f;
                            TestQuestion.createAndInsertItems(this, 280, 287, ql, true, (byte)(-1));
                            TestQuestion.createAndInsertItems(this, 284, 284, ql, true, (byte)(-1));
                            TestQuestion.createAndInsertItems(this, 280, 280, ql, true, (byte)(-1));
                            TestQuestion.createAndInsertItems(this, 283, 283, ql, true, (byte)(-1));
                            break;
                        }
                    }
                }
                catch (Exception ex2) {
                    this.getCommunicator().sendAlertServerMessage("Failed to spawn weapon.");
                }
            }
            this.spawnArmour = null;
        }
    }
    
    public Communicator getCommunicator() {
        return this.communicator;
    }
    
    public void setSecondsToLogout(final int seconds) {
    }
    
    public void addKey(final Item key, final boolean loading) {
        if (this.keys == null) {
            this.keys = new HashSet<Item>();
        }
        if (!this.keys.contains(key)) {
            this.keys.add(key);
            if (!loading) {
                final Item[] itemarr = this.getInventory().getAllItems(false);
                if (!this.unlockItems(key, itemarr)) {
                    this.unlockItems(key, this.getBody().getAllItems());
                }
                this.updateGates(key, false);
            }
        }
    }
    
    public void removeKey(final Item key, final boolean loading) {
        if (this.keys != null) {
            if (this.keys.remove(key) && !loading) {
                final Item[] itemarr = this.getInventory().getAllItems(false);
                if (!this.lockItems(key, itemarr)) {
                    this.lockItems(key, this.getBody().getAllItems());
                }
                this.updateGates(key, true);
            }
            if (this.keys.isEmpty()) {
                this.keys = null;
            }
        }
    }
    
    public void updateGates(final Item key, final boolean removedKey) {
        final VolaTile t = this.getCurrentTile();
        if (t != null) {
            final Door[] doors = t.getDoors();
            if (doors != null) {
                for (final Door lDoor : doors) {
                    lDoor.updateDoor(this, key, removedKey);
                }
            }
        }
        else {
            Creature.logger.log(Level.WARNING, this.getName() + " was on null tile.", new Exception());
        }
    }
    
    public void updateGates() {
        final VolaTile t = this.getCurrentTile();
        if (t != null) {
            final Door[] doors = t.getDoors();
            if (doors != null) {
                for (final Door lDoor : doors) {
                    lDoor.removeCreature(this);
                    if (lDoor.covers(this.getPosX(), this.getPosY(), this.getPositionZ(), this.getFloorLevel(), this.followsGround())) {
                        lDoor.addCreature(this);
                    }
                }
            }
        }
        else {
            Creature.logger.log(Level.WARNING, this.getName() + " was on null tile.", new Exception());
        }
    }
    
    public boolean unlockItems(final Item key, final Item[] items) {
        for (final Item lItem : items) {
            if (lItem.isLockable() && lItem.getLockId() != -10L) {
                try {
                    final Item lock = Items.getItem(lItem.getLockId());
                    final long[] keyIds;
                    final long[] keyarr = keyIds = lock.getKeyIds();
                    for (final long lElement : keyIds) {
                        if (lElement == key.getWurmId()) {
                            if (!lItem.isEmpty(false)) {
                                if (lItem.getOwnerId() == this.getWurmId()) {
                                    this.getCommunicator().sendHasMoreItems(-1L, lItem.getWurmId());
                                }
                                else {
                                    this.getCommunicator().sendHasMoreItems(lItem.getTopParent(), lItem.getWurmId());
                                }
                            }
                            return true;
                        }
                    }
                }
                catch (NoSuchItemException nsi) {
                    Creature.logger.log(Level.WARNING, nsi.getMessage(), nsi);
                }
            }
        }
        return false;
    }
    
    public boolean lockItems(final Item key, final Item[] items) {
        boolean stillUnlocked = false;
        for (final Item lItem : items) {
            if (lItem.isLockable() && lItem.getLockId() != -10L) {
                try {
                    final Item lock = Items.getItem(lItem.getLockId());
                    final long[] keyarr = lock.getKeyIds();
                    boolean thisLock = false;
                    for (final long lElement : keyarr) {
                        for (final Item key2 : this.keys) {
                            if (lElement == key2.getWurmId()) {
                                stillUnlocked = true;
                            }
                        }
                        if (lElement == key.getWurmId()) {
                            thisLock = true;
                        }
                    }
                    if (thisLock && !stillUnlocked) {
                        final Set<Item> contItems = lItem.getItems();
                        for (final Item item : contItems) {
                            item.removeWatcher(this, true);
                        }
                        return true;
                    }
                    if (thisLock) {
                        return true;
                    }
                }
                catch (NoSuchItemException nsi) {
                    Creature.logger.log(Level.WARNING, nsi.getMessage(), nsi);
                }
            }
        }
        return false;
    }
    
    public boolean hasKeyForLock(final Item lock) {
        if (lock.getWurmId() == this.getWurmId()) {
            return true;
        }
        if (this.keys == null || this.keys.isEmpty()) {
            return false;
        }
        if (lock.getWurmId() == 5390789413122L && lock.getParentId() == 5390755858690L) {
            boolean ok = true;
            if (!this.hasAbility(Abilities.getAbilityForItem(809, this))) {
                ok = false;
            }
            if (!this.hasAbility(Abilities.getAbilityForItem(808, this))) {
                ok = false;
            }
            if (!this.hasAbility(Abilities.getAbilityForItem(798, this))) {
                ok = false;
            }
            if (!this.hasAbility(Abilities.getAbilityForItem(810, this))) {
                ok = false;
            }
            if (!this.hasAbility(Abilities.getAbilityForItem(807, this))) {
                ok = false;
            }
            if (!ok) {
                this.getCommunicator().sendAlertServerMessage("There is some mysterious enchantment on this lock!");
                return ok;
            }
        }
        final long[] keyIds;
        final long[] keyarr = keyIds = lock.getKeyIds();
        for (final long lElement : keyIds) {
            for (final Item key : this.keys) {
                if (lElement == key.getWurmId()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean hasAllKeysForLock(final Item lock) {
        for (final long aKey : lock.getKeyIds()) {
            boolean foundit = false;
            for (final Item key : this.getKeys()) {
                if (aKey == key.getWurmId()) {
                    foundit = true;
                    break;
                }
            }
            if (!foundit) {
                return false;
            }
        }
        return true;
    }
    
    public Item[] getKeys() {
        Item[] toReturn = new Item[0];
        if (this.keys != null) {
            toReturn = this.keys.toArray(new Item[this.keys.size()]);
        }
        return toReturn;
    }
    
    public boolean isOnSurface() {
        return this.status.isOnSurface();
    }
    
    public void setLayer(final int layer, final boolean removeFromTile) {
        if (this.getStatus().getLayer() != layer) {
            if (this.isPlayer() || removeFromTile) {
                if (this.currentTile != null) {
                    if (!(this instanceof Player)) {
                        this.setPositionZ(Zones.calculatePosZ(this.getPosX(), this.getPosY(), this.getCurrentTile(), this.isOnSurface(), this.isFloating(), this.getPositionZ(), this, this.getBridgeId()));
                    }
                    this.getStatus().setLayer(layer);
                    if (this.getVehicle() != -10L && this.isVehicleCommander()) {
                        final Vehicle vehic = Vehicles.getVehicleForId(this.getVehicle());
                        if (vehic != null) {
                            boolean ok = true;
                            if (vehic.creature) {
                                try {
                                    final Creature cretVehicle = Server.getInstance().getCreature(vehic.wurmid);
                                    if (layer < 0) {
                                        final int tile = Server.caveMesh.getTile(cretVehicle.getTileX(), cretVehicle.getTileY());
                                        if (!Tiles.isSolidCave(Tiles.decodeType(tile))) {
                                            cretVehicle.setLayer(layer, false);
                                        }
                                    }
                                    else {
                                        cretVehicle.setLayer(layer, false);
                                    }
                                }
                                catch (NoSuchCreatureException nsi) {
                                    Creature.logger.log(Level.WARNING, this + ", cannot get creature for vehicle: " + vehic + " due to " + nsi.getMessage(), nsi);
                                }
                                catch (NoSuchPlayerException nsp) {
                                    Creature.logger.log(Level.WARNING, this + ", cannot get creature for vehicle: " + vehic + " due to " + nsp.getMessage(), nsp);
                                }
                            }
                            else {
                                try {
                                    final Item itemVehicle = Items.getItem(vehic.wurmid);
                                    if (layer < 0) {
                                        final int caveTile = Server.caveMesh.getTile((int)itemVehicle.getPosX() >> 2, (int)itemVehicle.getPosY() >> 2);
                                        if (Tiles.isSolidCave(Tiles.decodeType(caveTile))) {
                                            ok = false;
                                        }
                                    }
                                    if (ok) {
                                        itemVehicle.newLayer = (byte)layer;
                                        Zone zone = null;
                                        try {
                                            zone = Zones.getZone((int)itemVehicle.getPosX() >> 2, (int)itemVehicle.getPosY() >> 2, itemVehicle.isOnSurface());
                                            zone.removeItem(itemVehicle, true, true);
                                        }
                                        catch (NoSuchZoneException nsz) {
                                            Creature.logger.log(Level.WARNING, itemVehicle.getName() + " this shouldn't happen: " + nsz.getMessage() + " at " + ((int)itemVehicle.getPosX() >> 2) + ", " + ((int)itemVehicle.getPosY() >> 2), nsz);
                                        }
                                        try {
                                            zone = Zones.getZone((int)itemVehicle.getPosX() >> 2, (int)itemVehicle.getPosY() >> 2, layer >= 0);
                                            zone.addItem(itemVehicle, false, false, false);
                                        }
                                        catch (NoSuchZoneException nsz) {
                                            Creature.logger.log(Level.WARNING, itemVehicle.getName() + " this shouldn't happen: " + nsz.getMessage() + " at " + ((int)itemVehicle.getPosX() >> 2) + ", " + ((int)itemVehicle.getPosY() >> 2), nsz);
                                        }
                                        itemVehicle.newLayer = -128;
                                        final Seat[] seats = vehic.hitched;
                                        if (seats != null) {
                                            for (int x = 0; x < seats.length; ++x) {
                                                if (seats[x] != null) {
                                                    if (seats[x].occupant != -10L) {
                                                        try {
                                                            final Creature c = Server.getInstance().getCreature(seats[x].occupant);
                                                            c.getStatus().setLayer(layer);
                                                            c.getCurrentTile().newLayer(c);
                                                        }
                                                        catch (NoSuchPlayerException nsp2) {
                                                            Creature.logger.log(Level.WARNING, this.getName() + " " + nsp2.getMessage(), nsp2);
                                                        }
                                                        catch (NoSuchCreatureException nsc) {
                                                            Creature.logger.log(Level.WARNING, this.getName() + " " + nsc.getMessage(), nsc);
                                                        }
                                                    }
                                                }
                                                else {
                                                    Creature.logger.log(Level.WARNING, this.getName() + " " + vehic.name + ": lacking seat " + x, new Exception());
                                                }
                                            }
                                        }
                                    }
                                }
                                catch (NoSuchItemException is) {
                                    Creature.logger.log(Level.WARNING, this.getName() + " " + is.getMessage(), is);
                                }
                            }
                            if (ok) {
                                final Seat[] seats2 = vehic.seats;
                                if (seats2 != null) {
                                    for (int x2 = 0; x2 < seats2.length; ++x2) {
                                        if (x2 > 0) {
                                            if (seats2[x2] != null) {
                                                if (seats2[x2].occupant != -10L) {
                                                    try {
                                                        final Creature c2 = Server.getInstance().getCreature(seats2[x2].occupant);
                                                        c2.getStatus().setLayer(layer);
                                                        c2.getCurrentTile().newLayer(c2);
                                                        if (c2.isPlayer()) {
                                                            if (c2.isOnSurface()) {
                                                                c2.getCommunicator().sendNormalServerMessage("You leave the cave.");
                                                            }
                                                            else {
                                                                c2.getCommunicator().sendNormalServerMessage("You enter the cave.");
                                                                if (c2.getVisionArea() != null) {
                                                                    c2.getVisionArea().initializeCaves();
                                                                }
                                                            }
                                                        }
                                                    }
                                                    catch (NoSuchPlayerException nsp3) {
                                                        Creature.logger.log(Level.WARNING, this.getName() + " " + nsp3.getMessage(), nsp3);
                                                    }
                                                    catch (NoSuchCreatureException nsc2) {
                                                        Creature.logger.log(Level.WARNING, this.getName() + " " + nsc2.getMessage(), nsc2);
                                                    }
                                                }
                                            }
                                            else {
                                                Creature.logger.log(Level.WARNING, this.getName() + " " + vehic.name + ": lacking seat " + x2, new Exception());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    this.currentTile.newLayer(this);
                }
                else {
                    this.getStatus().setLayer(layer);
                }
                if (this.isPlayer()) {
                    if (layer < 0 && this.getVisionArea() != null) {
                        this.getVisionArea().checkCaves(true);
                    }
                    if (layer < 0) {
                        this.getCommunicator().sendNormalServerMessage("You enter the cave.");
                    }
                    else {
                        this.getCommunicator().sendNormalServerMessage("You leave the cave.");
                    }
                    final Village v = Villages.getVillage(this.getTileX(), this.getTileY(), true);
                    if (v != null && v.isEnemy(this)) {
                        final Guard[] guards = v.getGuards();
                        for (int gx = 0; gx < guards.length; ++gx) {
                            if (guards[gx].getCreature().isWithinDistanceTo(this, 20.0f) && this.visibilityCheck(guards[gx].getCreature(), 0.0f)) {
                                v.checkIfRaiseAlert(this);
                                break;
                            }
                        }
                    }
                }
            }
            else {
                this.getStatus().setLayer(layer);
                this.getCurrentTile().newLayer(this);
            }
        }
    }
    
    public int getLayer() {
        return this.getStatus().getLayer();
    }
    
    public void setPositionX(final float pos) {
        this.status.setPositionX(pos);
    }
    
    public void setPositionY(final float pos) {
        this.status.setPositionY(pos);
    }
    
    public void setPositionZ(final float pos) {
        this.status.setPositionZ(pos);
    }
    
    public void setRotation(final float aRot) {
        this.status.setRotation(aRot);
    }
    
    public void turnTo(final float newRot) {
        this.setRotation(normalizeAngle(newRot));
        this.moved(0.0f, 0.0f, 0.0f, 0, 0);
    }
    
    public void turnBy(final float turnAmount) {
        this.setRotation(normalizeAngle(this.status.getRotation() + turnAmount));
        this.moved(0.0f, 0.0f, 0.0f, 0, 0);
    }
    
    public void submerge() {
        try {
            final float lOldPosZ = this.getPositionZ();
            final float lNewPosZ = this.isFloating() ? this.template.offZ : (Zones.calculateHeight(this.getPosX(), this.getPosY(), true) / 2.0f);
            this.moved(0.0f, 0.0f, lNewPosZ - lOldPosZ, 0, 0);
        }
        catch (NoSuchZoneException ex) {}
    }
    
    public void surface() {
        final float lOldPosZ = this.getPositionZ();
        final float lNewPosZ = this.isFloating() ? this.template.offZ : -1.25f;
        this.setPositionZ(lNewPosZ);
        this.moved(0.0f, 0.0f, lNewPosZ - lOldPosZ, 0, 0);
    }
    
    public void almostSurface() {
        final float _oldPosZ = this.getPositionZ();
        final float _newPosZ = -2.0f;
        this.setPositionZ(-2.0f);
        this.moved(0.0f, 0.0f, -2.0f - _oldPosZ, 0, 0);
    }
    
    public void setCommunicator(final Communicator comm) {
        this.communicator = comm;
    }
    
    public void loadPossessions(final long inventoryId) throws Exception {
        try {
            this.possessions = new Possessions(this, inventoryId);
        }
        catch (Exception ex) {
            Creature.logger.log(Level.INFO, ex.getMessage(), ex);
            this.status.createNewPossessions();
        }
    }
    
    public long createPossessions() throws Exception {
        this.possessions = new Possessions(this);
        return this.possessions.getInventory().getWurmId();
    }
    
    public Behaviour getBehaviour() {
        return this.behaviour;
    }
    
    public final boolean hasFightDistanceTo(final Creature _target) {
        if (Math.abs(this.getStatus().getPositionX() - _target.getStatus().getPositionX()) > Math.abs(this.getStatus().getPositionY() - _target.getStatus().getPositionY())) {
            return Math.abs(this.getStatus().getPositionX() - _target.getStatus().getPositionX()) < 8.0f;
        }
        return Math.abs(this.getStatus().getPositionY() - _target.getStatus().getPositionY()) < 8.0f;
    }
    
    public static final int rangeTo(final Creature performer, final Creature target) {
        if (Math.abs(performer.getStatus().getPositionX() - target.getStatus().getPositionX()) > Math.abs(performer.getStatus().getPositionY() - target.getStatus().getPositionY())) {
            return (int)Math.abs(performer.getStatus().getPositionX() - target.getStatus().getPositionX());
        }
        return (int)Math.abs(performer.getStatus().getPositionY() - target.getStatus().getPositionY());
    }
    
    private static final float calcModPosX(final double sinRot, final double cosRot, final float widthCM, final float lengthCM) {
        return (float)(cosRot * widthCM - sinRot * lengthCM);
    }
    
    private static final float calcModPosY(final double sinRot, final double cosRot, final float widthCM, final float lengthCM) {
        return (float)(widthCM * sinRot + lengthCM * cosRot);
    }
    
    private static Vector2f rotate(final float angle, final Vector2f center, final Vector2f point) {
        final double rads = angle * 3.141592653589793 / 180.0;
        final Vector2f nPoint = new Vector2f();
        nPoint.x = (float)(center.x + (point.x - center.x) * Math.cos(rads) + (point.y - center.y) * Math.sin(rads));
        nPoint.y = (float)(center.y - (point.x - center.x) * Math.sin(rads) + (point.y - center.y) * Math.cos(rads));
        return nPoint;
    }
    
    private static final boolean isLeftOf(final Vector2f point, final float posX) {
        return posX < point.x;
    }
    
    private static final boolean isRightOf(final Vector2f point, final float posX) {
        return posX > point.x;
    }
    
    private static final boolean isAbove(final Vector2f point, final float posY) {
        return posY > point.y;
    }
    
    private static final boolean isBelow(final Vector2f point, final float posY) {
        return posY < point.y;
    }
    
    private static final int closestPoint(final Vector2f[] points, final Vector2f pos, final Vector2f[] ignore) {
        final boolean canIgnore = ignore != null;
        float min = 10000.0f;
        int index = -1;
        for (int i = 0; i < points.length; ++i) {
            if (canIgnore) {
                boolean doIgnore = false;
                for (int x = 0; x < ignore.length; ++x) {
                    if (points[i] == ignore[x]) {
                        doIgnore = true;
                    }
                }
                if (doIgnore) {
                    continue;
                }
            }
            final float len = pos.subtract(points[i]).length();
            if (len < min) {
                index = i;
                min = len;
            }
        }
        return index;
    }
    
    public static final float rangeToInDec(final Creature performer, final Creature target) {
        if (target.getTemplate().hasBoundingBox() && Features.Feature.CREATURE_COMBAT_CHANGES.isEnabled()) {
            final float minX = target.getTemplate().getBoundMinX() * target.getStatus().getSizeMod();
            final float minY = target.getTemplate().getBoundMinY() * target.getStatus().getSizeMod();
            final float maxX = target.getTemplate().getBoundMaxX() * target.getStatus().getSizeMod();
            final float maxY = target.getTemplate().getBoundMaxY() * target.getStatus().getSizeMod();
            final Vector2f center = new Vector2f(target.getStatus().getPositionX(), target.getStatus().getPositionY());
            final float PX = performer.getStatus().getPositionX();
            final float PY = performer.getStatus().getPositionY();
            final Vector3f cpos = new Vector3f(center.x, center.y, 1.0f);
            final float rotation = target.getStatus().getRotation();
            final Vector3f mp1 = new Vector3f(minX, minY, 0.0f);
            final Vector3f mp2 = new Vector3f(maxX, maxY, 0.0f);
            final BoxMatrix M = new BoxMatrix(true);
            final BoundBox box = new BoundBox(M, mp1, mp2);
            box.M.translate(cpos);
            box.M.rotate(rotation + 180.0f, false, false, true);
            final Vector3f ppos = new Vector3f(PX, PY, 0.5f);
            if (box.isPointInBox(ppos)) {
                return box.distOutside(ppos, cpos) * 10.0f;
            }
            return box.distOutside(ppos, cpos) * 10.0f;
        }
        else {
            if (Math.abs(performer.getStatus().getPositionX() - target.getStatus().getPositionX()) > Math.abs(performer.getStatus().getPositionY() - target.getStatus().getPositionY())) {
                return Math.abs(performer.getStatus().getPositionX() - target.getStatus().getPositionX()) * 10.0f;
            }
            return Math.abs(performer.getStatus().getPositionY() - target.getStatus().getPositionY()) * 10.0f;
        }
    }
    
    public static int rangeTo(final Creature performer, final Item aTarget) {
        if (Math.abs(performer.getStatus().getPositionX() - aTarget.getPosX()) > Math.abs(performer.getStatus().getPositionY() - aTarget.getPosY())) {
            return (int)Math.abs(performer.getStatus().getPositionX() - aTarget.getPosX());
        }
        return (int)Math.abs(performer.getStatus().getPositionY() - aTarget.getPosY());
    }
    
    public void setAction(final Action action) {
        this.actions.addAction(action);
    }
    
    public ActionStack getActions() {
        return this.actions;
    }
    
    public Action getCurrentAction() throws NoSuchActionException {
        return this.actions.getCurrentAction();
    }
    
    public void modifyRanking() {
    }
    
    public void dropLeadingItem(final Item item) {
    }
    
    public Item getLeadingItem(final Creature follower) {
        return null;
    }
    
    public Creature getFollowedCreature(final Item leadingItem) {
        return null;
    }
    
    public boolean isItemLeading(final Item item) {
        return false;
    }
    
    public void addFollower(final Creature follower, @Nullable final Item leadingItem) {
        if (this.followers == null) {
            this.followers = new HashMap<Creature, Item>();
        }
        this.followers.put(follower, leadingItem);
    }
    
    public Creature[] getFollowers() {
        if (this.followers == null || this.followers.size() == 0) {
            return Creature.emptyCreatures;
        }
        return this.followers.keySet().toArray(new Creature[this.followers.size()]);
    }
    
    public final int getNumberOfFollowers() {
        if (this.followers == null) {
            return 0;
        }
        return this.followers.size();
    }
    
    public void stopLeading() {
        if (this.followers != null) {
            final Creature[] array;
            final Creature[] followArr = array = this.followers.keySet().toArray(new Creature[this.followers.size()]);
            for (final Creature lElement : array) {
                lElement.setLeader(null);
            }
            this.followers = null;
        }
    }
    
    public boolean mayLeadMoreCreatures() {
        return this.followers == null || this.followers.size() < 10;
    }
    
    public final boolean isLeading(final Creature checked) {
        for (final Creature c : this.getFollowers()) {
            for (final Creature c2 : this.getFollowers()) {
                for (final Creature c3 : this.getFollowers()) {
                    for (final Creature c4 : this.getFollowers()) {
                        for (final Creature c5 : this.getFollowers()) {
                            for (final Creature c6 : this.getFollowers()) {
                                for (final Creature c7 : this.getFollowers()) {
                                    if (c7.getWurmId() == checked.getWurmId()) {
                                        return true;
                                    }
                                }
                                if (c6.getWurmId() == checked.getWurmId()) {
                                    return true;
                                }
                            }
                            if (c5.getWurmId() == checked.getWurmId()) {
                                return true;
                            }
                        }
                        if (c4.getWurmId() == checked.getWurmId()) {
                            return true;
                        }
                    }
                    if (c3.getWurmId() == checked.getWurmId()) {
                        return true;
                    }
                }
                if (c2.getWurmId() == checked.getWurmId()) {
                    return true;
                }
            }
            if (c.getWurmId() == checked.getWurmId()) {
                return true;
            }
        }
        return false;
    }
    
    public void setLeader(@Nullable final Creature leadingCreature) {
        if (leadingCreature == this) {
            Creature.logger.log(Level.WARNING, this.getName() + " tries to lead itself at ", new Exception());
            return;
        }
        this.clearOrders();
        if (this.leader == null) {
            if (leadingCreature != null) {
                if (this.isLeading(leadingCreature)) {
                    return;
                }
                this.leader = leadingCreature;
                Creatures.getInstance().setLastLed(this.getWurmId(), this.leader.getWurmId());
                Server.getInstance().broadCastAction(this.getNameWithGenus() + " now follows " + this.leader.getNameWithGenus() + ".", this.leader, this, 5);
                this.leader.getCommunicator().sendNormalServerMessage("You start leading " + this.getNameWithGenus() + ".");
                this.getCommunicator().sendNormalServerMessage("You start following " + this.leader.getNameWithGenus() + ".");
            }
        }
        else if (leadingCreature == null) {
            Server.getInstance().broadCastAction(this.getNameWithGenus() + " stops following " + this.leader.getNameWithGenus() + ".", this.leader, this, 5);
            this.leader.getCommunicator().sendNormalServerMessage("You stop leading " + this.getNameWithGenus() + ".");
            this.getCommunicator().sendNormalServerMessage("You stop following " + this.leader.getNameWithGenus() + ".");
            this.leader.removeFollower(this);
            this.leader = null;
        }
    }
    
    public void removeFollower(final Creature follower) {
        if (this.followers != null) {
            this.followers.remove(follower);
        }
    }
    
    public void putInWorld() {
        try {
            final Zone z = Zones.getZone(this.getTileX(), this.getTileY(), this.getLayer() >= 0);
            z.addCreature(this.getWurmId());
        }
        catch (NoSuchZoneException nsz) {
            Creature.logger.log(Level.WARNING, this.getName() + " " + nsz.getMessage(), nsz);
        }
        catch (NoSuchPlayerException nsp) {
            Creature.logger.log(Level.WARNING, this.getName() + " " + nsp.getMessage(), nsp);
        }
        catch (NoSuchCreatureException nsc) {
            Creature.logger.log(Level.WARNING, this.getName() + " " + nsc.getMessage(), nsc);
        }
    }
    
    public static final double getRange(final Creature performer, final double targetX, final double targetY) {
        final double diffx = Math.abs(performer.getPosX() - targetX);
        final double diffy = Math.abs(performer.getPosY() - targetY);
        return Math.sqrt(diffx * diffx + diffy * diffy);
    }
    
    public static final double getTileRange(final Creature performer, final int targetX, final int targetY) {
        final double diffx = Math.abs(performer.getTileX() - targetX);
        final double diffy = Math.abs(performer.getTileY() - targetY);
        return Math.sqrt(diffx * diffx + diffy * diffy);
    }
    
    public boolean isWithinTileDistanceTo(final int tileX, final int tileY, final int heigh1tOffset, final int maxDist) {
        final int ptilex = this.getTileX();
        final int ptiley = this.getTileY();
        return ptilex <= tileX + maxDist && ptilex >= tileX - maxDist && ptiley <= tileY + maxDist && ptiley >= tileY - maxDist;
    }
    
    public boolean isWithinDistanceTo(@Nonnull final Item item, final float maxDist) {
        return this.isWithinDistanceTo(item.getPos3f(), maxDist);
    }
    
    public boolean isWithinDistanceTo(@Nonnull final Vector3f targetPos, final float maxDist) {
        return this.isWithinDistanceTo(targetPos.x, targetPos.y, targetPos.z, maxDist);
    }
    
    public boolean isWithinDistanceTo(final float aPosX, final float aPosY, final float aPosZ, final float maxDist) {
        return Math.abs(this.getStatus().getPositionX() + this.getAltOffZ() - aPosX) <= maxDist && Math.abs(this.getStatus().getPositionY() - aPosY) <= maxDist;
    }
    
    public boolean isWithinDistanceTo(final Creature targetCret, final float maxDist) {
        return Math.abs(this.getStatus().getPositionX() - targetCret.getPosX()) <= maxDist && Math.abs(this.getStatus().getPositionY() - targetCret.getPosY()) <= maxDist;
    }
    
    public boolean isWithinDistanceTo(final float aPosX, final float aPosY, final float aPosZ, final float maxDist, final float modifier) {
        return Math.abs(this.getStatus().getPositionX() - (aPosX + modifier)) < maxDist && Math.abs(this.getStatus().getPositionY() - (aPosY + modifier)) < maxDist;
    }
    
    public boolean isWithinDistanceToZ(final float aPosZ, final float maxDist, final boolean addHalfHeight) {
        return Math.abs(this.getStatus().getPositionZ() + (addHalfHeight ? (this.getHalfHeightDecimeters() / 10.0f) : 0.0f) - aPosZ) < maxDist;
    }
    
    public boolean isWithinDistanceTo(final int aPosX, final int aPosY, final int maxDistance) {
        return Math.abs(this.getTileX() - aPosX) <= maxDistance && Math.abs(this.getTileY() - aPosY) <= maxDistance;
    }
    
    public void creatureMoved(final Creature creature, final float diffX, final float diffY, final float diffZ) {
        if (this.leader != null && this.leader.equals(creature) && !this.isRidden() && (diffX != 0.0f || diffY != 0.0f)) {
            this.followLeader();
        }
        if (this.isTypeFleeing()) {
            if (creature.isPlayer() && this.isBred()) {
                return;
            }
            if (creature.isPlayer() || creature.isAggHuman() || creature.isHuman() || creature.isCarnivore() || creature.isMonster()) {
                final Vector2f mypos = new Vector2f(this.getPosX(), this.getPosY());
                final float oldDistance = new Vector2f(creature.getPosX() - diffX, creature.getPosY() - diffY).distance(mypos);
                final float newDistance = new Vector2f(creature.getPosX(), creature.getPosY()).distance(mypos);
                if (oldDistance > newDistance) {
                    if (Features.Feature.CREATURE_MOVEMENT_CHANGES.isEnabled()) {
                        final int baseCounter = (int)(Math.max(1.0f, creature.getBaseCombatRating() - this.getBaseCombatRating()) * 5.0f);
                        if (baseCounter - newDistance > 0.0f) {
                            this.setFleeCounter((int)Math.min(60.0f, Math.max(3.0f, baseCounter - newDistance)));
                        }
                    }
                    else {
                        this.setFleeCounter(60);
                    }
                }
            }
        }
    }
    
    public final boolean isPrey() {
        return this.template.isPrey();
    }
    
    public final boolean isSpy() {
        return this.status.modtype == 8 && (this.template.getTemplateId() == 84 || this.template.getTemplateId() == 10 || this.template.getTemplateId() == 12);
    }
    
    public void delete() {
        Server.getInstance().addCreatureToRemove(this);
    }
    
    public void destroyVisionArea() {
        if (this.visionArea != null) {
            this.visionArea.destroy();
        }
        this.visionArea = null;
    }
    
    public void createVisionArea() throws Exception {
        if (this.visionArea != null) {
            this.visionArea.destroy();
        }
        this.visionArea = new VisionArea(this, this.template.getVision());
    }
    
    public String getHisHerItsString() {
        if (this.status.getSex() == 0) {
            return "his";
        }
        if (this.status.getSex() == 1) {
            return "her";
        }
        return "its";
    }
    
    public String getHimHerItString() {
        if (this.status.getSex() == 0) {
            return "him";
        }
        if (this.status.getSex() == 1) {
            return "her";
        }
        return "it";
    }
    
    public boolean mayAttack(@Nullable final Creature cret) {
        return this.status.getStunned() <= 0.0f && !this.status.isUnconscious();
    }
    
    public boolean isStunned() {
        return this.status.getStunned() > 0.0f;
    }
    
    public boolean isUnconscious() {
        return this.status.isUnconscious();
    }
    
    public String getHeSheItString() {
        if (this.status.getSex() == 0) {
            return "he";
        }
        if (this.status.getSex() == 1) {
            return "she";
        }
        return "it";
    }
    
    public void stopCurrentAction() {
        try {
            final String toSend = this.actions.stopCurrentAction(false);
            if (toSend.length() > 0) {
                this.communicator.sendNormalServerMessage(toSend);
            }
            this.sendActionControl("", false, 0);
        }
        catch (NoSuchActionException ex) {}
    }
    
    public void maybeInterruptAction(final int damage) {
        try {
            final Action act = this.actions.getCurrentAction();
            if (act.isVulnerable() && act.getNumber() != Spells.SPELL_CHARM_ANIMAL.number && act.getNumber() != Spells.SPELL_DOMINATE.number && this.getBodyControlSkill().skillCheck(damage / 100.0f, this.zoneBonus, false, 1.0f) < 0.0) {
                final String toSend = this.actions.stopCurrentAction(false);
                if (toSend.length() > 0) {
                    this.communicator.sendNormalServerMessage(toSend);
                }
                this.sendActionControl("", false, 0);
            }
        }
        catch (NoSuchActionException ex) {}
    }
    
    public float getCombatDamage(final Item bodyPart) {
        final short pos = bodyPart.getPlace();
        if (pos == 13 || pos == 14) {
            return this.getHandDamage();
        }
        if (pos == 34) {
            return this.getKickDamage();
        }
        if (pos == 1) {
            return this.getHeadButtDamage();
        }
        if (pos == 29) {
            return this.getBiteDamage();
        }
        if (pos == 2) {
            return this.getBreathDamage();
        }
        return 0.0f;
    }
    
    public String getAttackStringForBodyPart(final Item bodypart) {
        if (bodypart.getPlace() == 13 || bodypart.getPlace() == 14) {
            return this.template.getHandDamString();
        }
        if (bodypart.getPlace() == 34) {
            return this.template.getKickDamString();
        }
        if (bodypart.getPlace() == 29) {
            return this.template.getBiteDamString();
        }
        if (bodypart.getPlace() == 1) {
            return this.template.getHeadButtDamString();
        }
        if (bodypart.getPlace() == 2) {
            return this.template.getBreathDamString();
        }
        return this.template.getHandDamString();
    }
    
    public float getBodyWeaponSpeed(final Item bodypart) {
        final float size = this.template.getSize();
        if (bodypart.getPlace() == 13 || bodypart.getPlace() == 14) {
            return size + 1.0f;
        }
        if (bodypart.getPlace() == 34) {
            return size + 2.0f;
        }
        if (bodypart.getPlace() == 29) {
            return size + 2.5f;
        }
        if (bodypart.getPlace() == 1) {
            return size + 3.0f;
        }
        if (bodypart.getPlace() == 2) {
            return size + 3.5f;
        }
        return 4.0f;
    }
    
    public Item getArmour(final byte location) throws NoArmourException, NoSpaceException {
        Item bodyPart = null;
        try {
            final boolean barding = this.isHorse();
            if (barding) {
                bodyPart = this.status.getBody().getBodyPart(2);
            }
            else {
                bodyPart = this.status.getBody().getBodyPart(location);
            }
            if (location == 29) {
                final Item helmet = this.getArmour((byte)1);
                return helmet;
            }
            final Set<Item> its = bodyPart.getItems();
            for (final Item item : its) {
                if (item.isArmour()) {
                    final byte[] bodySpaces;
                    final byte[] spaces = bodySpaces = item.getBodySpaces();
                    for (final byte lSpace : bodySpaces) {
                        if (lSpace == location || barding) {
                            return item;
                        }
                    }
                }
            }
        }
        catch (NoArmourException noa) {
            throw noa;
        }
        catch (Exception ex) {
            throw new NoSpaceException(ex);
        }
        throw new NoArmourException("No armour worn on bodypart " + location);
    }
    
    public Item getCarriedItem(final int itemTemplateId) {
        final Item inventory = this.getInventory();
        final Item[] allItems;
        Item[] items = allItems = inventory.getAllItems(false);
        for (final Item lItem : allItems) {
            if (lItem.getTemplateId() == itemTemplateId) {
                return lItem;
            }
        }
        final Item body = this.getBody().getBodyItem();
        final Item[] allItems2;
        items = (allItems2 = body.getAllItems(false));
        for (final Item lItem2 : allItems2) {
            if (lItem2.getTemplateId() == itemTemplateId) {
                return lItem2;
            }
        }
        return null;
    }
    
    public Item getEquippedItem(final byte location) throws NoSpaceException {
        try {
            final Set<Item> wornItems = this.status.getBody().getBodyPart(location).getItems();
            for (final Item item : wornItems) {
                if (!item.isArmour() && !item.isBodyPartAttached()) {
                    return item;
                }
            }
        }
        catch (NullPointerException npe) {
            if (this.status == null) {
                Creature.logger.log(Level.WARNING, "status is null for creature" + this.getName(), npe);
            }
            else if (this.status.getBody() == null) {
                Creature.logger.log(Level.WARNING, "body is null for creature" + this.getName(), npe);
            }
            else if (this.status.getBody().getBodyPart(location) == null) {
                Creature.logger.log(Level.WARNING, "body inventoryspace(" + location + ") is null for creature" + this.getName(), npe);
            }
            else {
                Creature.logger.log(Level.WARNING, "seems wornItems for inventoryspace was null for creature" + this.getName(), npe);
            }
        }
        return null;
    }
    
    public Item getEquippedWeapon(final byte location) throws NoSpaceException {
        return this.getEquippedWeapon(location, true);
    }
    
    public Item getEquippedWeapon(final byte location, final boolean allowBow) throws NoSpaceException {
        return this.getEquippedWeapon(location, allowBow, false);
    }
    
    public Item getEquippedWeapon(final byte location, final boolean allowBow, final boolean fetchBodypart) throws NoSpaceException {
        Item bodyPart = null;
        try {
            bodyPart = this.status.getBody().getBodyPart(location);
            if (this.isAnimal()) {
                return bodyPart;
            }
            if ((bodyPart.getPlace() != 37 && bodyPart.getPlace() != 38 && bodyPart.getPlace() != 13 && bodyPart.getPlace() != 14) || (!this.isPlayer() && fetchBodypart)) {
                return bodyPart;
            }
            final Set<Item> wornItems = bodyPart.getItems();
            for (final Item item : wornItems) {
                if (!item.isArmour() && !item.isBodyPartAttached() && (Weapon.getBaseDamageForWeapon(item) > 0.0f || (item.isWeaponBow() && allowBow))) {
                    return item;
                }
            }
            if (bodyPart.getPlace() == 37 || bodyPart.getPlace() == 38) {
                final int handSlot = (bodyPart.getPlace() == 37) ? 13 : 14;
                bodyPart = this.status.getBody().getBodyPart(handSlot);
            }
        }
        catch (NullPointerException npe) {
            if (this.status == null) {
                Creature.logger.log(Level.WARNING, "status is null for creature" + this.getName(), npe);
            }
            else if (this.status.getBody() == null) {
                Creature.logger.log(Level.WARNING, "body is null for creature" + this.getName(), npe);
            }
            else if (this.status.getBody().getBodyPart(location) == null) {
                Creature.logger.log(Level.WARNING, "body inventoryspace(" + location + ") is null for creature" + this.getName(), npe);
            }
            else {
                Creature.logger.log(Level.WARNING, "seems wornItems for inventoryspace was null for creature" + this.getName(), npe);
            }
            throw new NoSpaceException("No  bodypart " + location, npe);
        }
        return bodyPart;
    }
    
    public int getTotalInventoryWeightGrams() {
        final Body body = this.status.getBody();
        int weight = 0;
        final Item[] allItems;
        final Item[] items = allItems = body.getAllItems();
        for (final Item lItem : allItems) {
            weight += lItem.getFullWeight();
        }
        final Item[] inventoryItems = this.possessions.getInventory().getAllItems(true);
        for (int x = 0; x < items.length; ++x) {
            weight += inventoryItems[x].getFullWeight();
        }
        return weight;
    }
    
    public void startPersonalAction(final short action, final long subject, final long _target) {
        try {
            BehaviourDispatcher.action(this, this.communicator, subject, _target, action);
        }
        catch (FailedException ex) {}
        catch (NoSuchBehaviourException ex2) {}
        catch (NoSuchCreatureException ex3) {}
        catch (NoSuchItemException ex4) {}
        catch (NoSuchPlayerException ex5) {}
        catch (NoSuchWallException ex6) {}
    }
    
    public void setFighting() {
        if (this.opponent != null) {
            if (this.getPower() > 0 && !this.isVisible()) {
                this.setOpponent(null);
                return;
            }
            try {
                Action lCurrentAction = null;
                try {
                    lCurrentAction = this.getCurrentAction();
                }
                catch (NoSuchActionException ex) {}
                if (lCurrentAction == null || lCurrentAction.getNumber() != 114) {
                    BehaviourDispatcher.action(this, this.communicator, -1L, this.opponent.getWurmId(), (short)114);
                }
                else if (lCurrentAction != null) {
                    this.sendToLoggers("busy " + lCurrentAction.getActionString() + " seconds " + lCurrentAction.getCounterAsFloat() + " " + lCurrentAction.getTarget() + ", path is null:" + (this.status.getPath() == null), (byte)4);
                }
                this.status.setPath(null);
            }
            catch (FailedException fe) {
                this.setOpponent(null);
            }
            catch (NoSuchBehaviourException nsb) {
                this.setTarget(-10L, true);
                this.setOpponent(null);
                Creature.logger.log(Level.WARNING, nsb.getMessage(), nsb);
            }
            catch (NoSuchCreatureException nsc) {
                this.setTarget(-10L, true);
                this.setOpponent(null);
            }
            catch (NoSuchItemException nsi) {
                this.setTarget(-10L, true);
                this.setOpponent(null);
                Creature.logger.log(Level.WARNING, nsi.getMessage(), nsi);
            }
            catch (NoSuchPlayerException nsp) {
                this.setTarget(-10L, true);
                this.setOpponent(null);
            }
            catch (NoSuchWallException nsw) {
                this.setOpponent(null);
                Creature.logger.log(Level.WARNING, nsw.getMessage(), nsw);
            }
        }
    }
    
    public void attackTarget() {
        if (this.target != -10L && (this.opponent == null || this.opponent.getWurmId() != this.target)) {
            final long start = System.nanoTime();
            final Creature tg = this.getTarget();
            if (tg != null && (tg.isDead() || tg.isOffline())) {
                this.setTarget(-10L, true);
            }
            else if (this.isDominated() && tg != null && tg.isDominated() && this.getDominator() == tg.getDominator()) {
                this.setTarget(-10L, true);
                this.setOpponent(null);
            }
            else if (tg != null) {
                if (rangeTo(this, tg) < Actions.actionEntrys[114].getRange()) {
                    if (!this.isPlayer() && tg.getFloorLevel() != this.getFloorLevel()) {
                        if (this.isSpiritGuard()) {
                            this.pushToFloorLevel(this.getTarget().getFloorLevel());
                        }
                        else if (tg.getFloorLevel() != this.getFloorLevel()) {
                            final Floor[] floors2;
                            final Floor[] floors = floors2 = this.getCurrentTile().getFloors(Math.min(this.getFloorLevel(), tg.getFloorLevel()) * 30, Math.max(this.getFloorLevel(), tg.getFloorLevel()) * 30);
                            for (final Floor f : floors2) {
                                if (tg.getFloorLevel() > this.getFloorLevel()) {
                                    if (f.getFloorLevel() == this.getFloorLevel() + 1 && ((f.isOpening() && this.canOpenDoors()) || f.isStair())) {
                                        this.pushToFloorLevel(f.getFloorLevel());
                                        break;
                                    }
                                }
                                else if (f.getFloorLevel() == this.getFloorLevel() && ((f.isOpening() && this.canOpenDoors()) || f.isStair())) {
                                    this.pushToFloorLevel(f.getFloorLevel() - 1);
                                    break;
                                }
                            }
                        }
                    }
                    if (tg.getLayer() != this.getLayer() && (!tg.getCurrentTile().isTransition || !this.getCurrentTile().isTransition)) {
                        return;
                    }
                    if (tg != this.opponent && tg.getAttackers() >= tg.getMaxGroupAttackSize()) {
                        final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                        segments.add(new CreatureLineSegment(tg));
                        segments.add(new MulticolorLineSegment(" is too crowded with attackers. You find no space.", (byte)0));
                        this.getCommunicator().sendColoredMessageCombat(segments);
                        return;
                    }
                    if (!CombatHandler.prerequisitesFail(this, tg, true, this.getPrimWeapon())) {
                        if (!tg.isTeleporting()) {
                            this.setOpponent(tg);
                            if (!tg.isPlayer() && this.fightlevel > 1) {
                                this.fightlevel /= 2;
                                if (this.isPlayer()) {
                                    this.getCommunicator().sendFocusLevel(this.getWurmId());
                                }
                            }
                            if (!this.isPlayer()) {
                                this.status.setMoving(false);
                            }
                            final ArrayList<MulticolorLineSegment> segments = new ArrayList<MulticolorLineSegment>();
                            segments.add(new CreatureLineSegment(this));
                            segments.add(new MulticolorLineSegment(" try to " + CombatEngine.getAttackString(this, this.getPrimWeapon()) + " ", (byte)0));
                            segments.add(new CreatureLineSegment(tg));
                            segments.add(new MulticolorLineSegment(".", (byte)0));
                            this.getCommunicator().sendColoredMessageCombat(segments);
                            if (this.isPlayer() || this.isDominated()) {
                                segments.get(1).setText(" tries to " + CombatEngine.getAttackString(this, this.getPrimWeapon()) + " ");
                                tg.getCommunicator().sendColoredMessageCombat(segments);
                                if (this.isDominated() && this.getDominator() != null && this.getDominator().isPlayer()) {
                                    this.getDominator().getCommunicator().sendColoredMessageCombat(segments);
                                }
                            }
                            else {
                                segments.get(1).setText(" moves in to attack ");
                                tg.getCommunicator().sendColoredMessageCombat(segments);
                            }
                        }
                    }
                    else if (!this.isPlayer() && Server.rand.nextInt(50) == 0) {
                        this.setTarget(-10L, true);
                    }
                }
                else if (this.isSpellCaster() && rangeTo(this, tg) < 24 && !this.isPlayer() && tg.getFloorLevel() == this.getFloorLevel() && this.getLayer() == tg.getLayer() && this.getFavor() >= 100.0f && Server.rand.nextInt(10) == 0) {
                    this.setOpponent(tg);
                    short spellAction = 420;
                    switch (this.template.getTemplateId()) {
                        case 110: {
                            if (Server.rand.nextInt(3) == 0) {
                                spellAction = 485;
                            }
                            if (Server.rand.nextBoolean()) {
                                spellAction = 414;
                                break;
                            }
                            break;
                        }
                        case 111: {
                            if (Server.rand.nextInt(3) == 0) {
                                spellAction = 550;
                            }
                            if (Server.rand.nextBoolean()) {
                                spellAction = 549;
                                break;
                            }
                            break;
                        }
                        default: {
                            spellAction = 420;
                            break;
                        }
                    }
                    if (this.opponent != null) {
                        try {
                            long itemId = -10L;
                            try {
                                final Item bodyHand = this.getBody().getBodyPart(14);
                                itemId = bodyHand.getWurmId();
                            }
                            catch (Exception ex2) {
                                Creature.logger.log(Level.INFO, this.getName() + ": No hand.");
                            }
                            if (spellAction == 420 || spellAction == 414) {
                                BehaviourDispatcher.action(this, this.communicator, itemId, Tiles.getTileId(this.opponent.getTileX(), this.opponent.getTileY(), 0), spellAction);
                            }
                            else {
                                BehaviourDispatcher.action(this, this.communicator, itemId, this.opponent.getWurmId(), spellAction);
                            }
                        }
                        catch (Exception ex) {
                            Creature.logger.log(Level.INFO, this.getName() + " casting " + spellAction + ":" + ex.getMessage(), ex);
                        }
                    }
                }
            }
            else {
                this.setTarget(-10L, true);
            }
        }
    }
    
    public void moan() {
        if (this.isDominated()) {
            if (this.getDominator() != null) {
                this.getDominator().getCommunicator().sendNormalServerMessage("You sense a disturbance in " + this.getNameWithGenus() + ".");
            }
            if (this.isAnimal()) {
                Server.getInstance().broadCastAction(this.getNameWithGenus() + " grunts.", this, 5);
            }
            else {
                Server.getInstance().broadCastAction(this.getNameWithGenus() + " moans.", this, 5);
            }
        }
    }
    
    private void frolic() {
        if (this.isDominated()) {
            if (this.getDominator() != null) {
                this.getDominator().getCommunicator().sendNormalServerMessage("You sense a sudden calm in " + this.getNameWithGenus() + ".");
            }
            if (this.isAnimal()) {
                Server.getInstance().broadCastAction(this.getNameWithGenus() + " purrs.", this, 5);
            }
            else {
                Server.getInstance().broadCastAction(this.getNameWithGenus() + " hizzes.", this, 5);
            }
        }
    }
    
    private boolean isOutOfBounds() {
        return this.getTileX() < 0 || this.getTileX() > Zones.worldTileSizeX - 1 || this.getTileY() < 0 || this.getTileY() > Zones.worldTileSizeY - 1;
    }
    
    private boolean isFlying() {
        return false;
    }
    
    public boolean healRandomWound(final int power) {
        if (this.getBody().getWounds() != null) {
            final Wound[] wounds = this.getBody().getWounds().getWounds();
            if (wounds.length > 0) {
                final int num = Server.rand.nextInt(wounds.length);
                if (wounds[num].getSeverity() / 1000.0f < power) {
                    wounds[num].heal();
                    return true;
                }
                wounds[num].modifySeverity(-power * 1000);
                return true;
            }
        }
        return false;
    }
    
    protected void decreaseOpportunityCounter() {
        if (this.opportunityAttackCounter > 0) {
            --this.opportunityAttackCounter;
        }
    }
    
    public void pollNPC() {
    }
    
    public void pollNPCChat() {
    }
    
    public CreatureAIData getCreatureAIData() {
        if (this.template.getCreatureAI() != null) {
            if (this.aiData == null) {
                (this.aiData = this.template.getCreatureAI().createCreatureAIData()).setCreature(this);
            }
            return this.aiData;
        }
        return null;
    }
    
    public boolean poll() throws Exception {
        if (this.template.getCreatureAI() != null) {
            final boolean toDestroy = this.template.getCreatureAI().pollCreature(this, System.currentTimeMillis() - this.getCreatureAIData().getLastPollTime());
            this.getCreatureAIData().setLastPollTime(System.currentTimeMillis());
            return toDestroy;
        }
        if (this.breedTick++ >= 201) {
            this.checkBreedCounter();
            this.breedTick = 0;
        }
        if (this.isNpcTrader() && this.heatCheckTick++ >= 600) {
            this.getInventory().pollCoolingItems(this, 600000L);
            this.heatCheckTick = 0;
        }
        if (!this.isVisibleToPlayers() && !this.isTrader() && this.lastPolled != 0 && this.status.getPath() == null && this.target == -10L && !this.isUnique() && !this.isNpc()) {
            --this.lastPolled;
            return false;
        }
        if (Creature.firstCreature == -10L) {
            Creature.firstCreature = this.getWurmId();
        }
        this.lastPolled = Creature.pollChecksPer - 1;
        final long start = System.nanoTime();
        try {
            if (this.fleeCounter > 0) {
                --this.fleeCounter;
            }
            this.setHugeMoveCounter(this.getHugeMoveCounter() - 1);
            this.decreaseOpportunityCounter();
            if (this.guardSecondsLeft > 0) {
                --this.guardSecondsLeft;
            }
            if (this.getPathfindCounter() > 100) {
                if (this.isSpiritGuard()) {
                    Creature.logger.log(Level.WARNING, this.getName() + " " + this.getWurmId() + " pathfind " + this.getPathfindCounter() + ". Target was " + this.target + ". Surfaced=" + this.isOnSurface());
                }
                this.setPathfindcounter(0);
                this.setTarget(-10L, true);
                if (this.isDominated()) {
                    Creature.logger.log(Level.WARNING, this.getName() + " was dominated and failed to find path.");
                    if (this.getDominator() != null) {
                        this.getDominator().getCommunicator().sendNormalServerMessage("The " + this.getName() + " fails to follow your orders.");
                    }
                    if (this.decisions != null) {
                        this.decisions.clearOrders();
                    }
                }
            }
            if (this.getTemplate().getTemplateId() == 88) {
                if (!WurmCalendar.isNight() && this.getLayer() >= 0) {
                    this.die(false, "Wraith in Daylight");
                    return true;
                }
            }
            else if (this.isOutOfBounds()) {
                this.handleCreatureOutOfBounds();
                return true;
            }
            if (this.opponentCounter > 0 && this.opponent == null && --this.opponentCounter == 0) {
                this.lastOpponent = null;
                this.getCombatHandler().setCurrentStance(-1, (byte)15);
                this.combatRound = 0;
            }
            this.status.pollDetectInvis();
            if (this.isStunned()) {
                this.getStatus().setStunned((byte)(this.getStatus().getStunned() - 1.0f), false);
            }
            if (!this.isDead()) {
                if (this.getSpellEffects() != null) {
                    this.getSpellEffects().poll();
                }
                this.pollNPCChat();
                if (this.actions.poll(this)) {
                    this.attackTarget();
                    if (this.isFighting()) {
                        this.setFighting();
                    }
                    else if (!this.isDead()) {
                        if (Server.getSecondsUptime() != this.lastSecond) {
                            this.lastSecond = Server.getSecondsUptime();
                            if (!this.isRidden() && this.isNeedFood() && this.canEat() && Server.rand.nextInt(60) == 0) {
                                this.findFood();
                                if (this.hasTrait(7) && Zone.hasSpring(this.getTileX(), this.getTileY()) && Server.rand.nextInt(5) == 0) {
                                    this.frolic();
                                }
                                if (!this.isRidden() && this.hasTrait(12) && Server.rand.nextInt(10) == 0 && this.getLeader() != null) {
                                    Server.getInstance().broadCastAction(this.getName() + " refuses to move on.", this, 5);
                                    this.setLeader(null);
                                }
                            }
                            this.checkStealthing();
                            this.pollNPC();
                            this.checkEggLaying();
                            if (!this.isRidden() && !this.pollAge()) {
                                this.checkMove();
                                this.startUsingPath();
                            }
                            if (this.getStatus().pollFat()) {
                                final boolean disease = this.getStatus().disease >= 100;
                                String deathCause = "starvation";
                                if (disease) {
                                    deathCause = "disease";
                                }
                                Server.getInstance().broadCastAction(this.getNameWithGenus() + " rolls with the eyes, ejects " + this.getHisHerItsString() + " tongue and dies from " + deathCause + ".", this, 5);
                                Creature.logger.log(Level.INFO, this.getName() + " dies from " + deathCause + ".");
                                this.die(false, deathCause);
                            }
                            else {
                                this.checkForEnemies();
                            }
                        }
                    }
                    else {
                        Creature.logger.log(Level.INFO, this.getName() + " died when attacking?");
                    }
                }
            }
            if (this.webArmourModTime > 0.0f) {
                final float webArmourModTime = this.webArmourModTime;
                this.webArmourModTime = webArmourModTime - 1.0f;
                if (webArmourModTime <= 1.0f) {
                    this.webArmourModTime = 0.0f;
                    if (this.getMovementScheme().setWebArmourMod(false, 0.0f)) {
                        this.getMovementScheme().setWebArmourMod(false, 0.0f);
                    }
                    if (!this.isFighting() && this.fightlevel > 0) {
                        this.fightlevel = (byte)Math.max(0, this.fightlevel - 1);
                        if (this.isPlayer()) {
                            this.getCommunicator().sendFocusLevel(this.getWurmId());
                        }
                    }
                }
            }
            if (System.currentTimeMillis() - this.lastSavedPos > 3600000L) {
                this.lastSavedPos = System.currentTimeMillis() + Server.rand.nextInt(3600) * 1000;
                this.savePosition(this.status.getZoneId());
                this.getStatus().save();
                if ((this.getTemplateId() == 78 || this.getTemplateId() == 79 || this.getTemplateId() == 80 || this.getTemplateId() == 81 || this.getTemplateId() == 68) && !EpicServerStatus.doesGiveItemMissionExist(this.getWurmId())) {
                    return true;
                }
            }
            if (this.status.dead) {
                if (this.respawnCounter > 0) {
                    --this.respawnCounter;
                    if (this.respawnCounter == 0) {
                        final float[] xy = Player.findRandomSpawnX(true, true);
                        try {
                            this.setLayer(0, true);
                            this.setPositionX(xy[0]);
                            this.setPositionY(xy[1]);
                            this.setPositionZ(this.calculatePosZ());
                            this.respawn();
                            final Zone zone = Zones.getZone(this.getTileX(), this.getTileY(), this.isOnSurface());
                            zone.addCreature(this.getWurmId());
                            this.savePosition(zone.getId());
                            return false;
                        }
                        catch (NoSuchZoneException ex) {}
                        catch (NoSuchCreatureException ex2) {}
                        catch (NoSuchPlayerException ex3) {}
                        catch (Exception ex4) {}
                    }
                }
                return true;
            }
            if (this.damageCounter > 0) {
                --this.damageCounter;
                if (this.damageCounter <= 0) {
                    this.removeWoundMod();
                    this.getStatus().sendStateString();
                }
            }
            this.breakout();
            this.pollItems();
            if (this.tradeHandler != null) {
                this.tradeHandler.balance();
            }
            this.sendItemsTaken();
            this.sendItemsDropped();
            if (this.isVehicle()) {
                this.pollMount();
            }
            if (this.getBody() != null) {
                this.getBody().poll();
            }
            else {
                Creature.logger.log(Level.WARNING, this.getName() + "'s body is null.");
            }
            if (this.template.isMilkable() && !this.canEat() && Server.rand.nextInt(7200) == 0) {
                this.setMilked(false);
            }
            if (this.template.isWoolProducer()) {
                if (!this.canEat() && Server.rand.nextInt(14400) == 0) {
                    this.setSheared(false);
                }
            }
            else {
                this.removeRandomItems();
            }
            this.pollStamina();
            this.pollFavor();
            this.pollLoyalty();
            this.trimAttackers(false);
            this.numattackers = 0;
            this.hasAddedToAttack = false;
            if (this.isSpiritGuard() && this.citizenVillage != null && this.target == -10L && this.citizenVillage.targets.size() > 0) {
                this.citizenVillage.assignTargets();
            }
            if (this.hitchedTo != null || this.isRidden()) {
                this.goOffline = false;
            }
            if (!this.isUnique() && this.goOffline && !this.isFighting() && this.isDominated() && Players.getInstance().getPlayerOrNull(this.dominator) == null) {
                Creature.logger.log(Level.INFO, this.getName() + " going offline.");
                Creatures.getInstance().setCreatureOffline(this);
                this.goOffline = false;
                return true;
            }
            return this.isTransferring() || !this.isOnCurrentServer();
        }
        finally {
            this.shouldStandStill = false;
            final float lElapsedTime = (System.nanoTime() - start) / 1000000.0f;
            if (lElapsedTime > Creature.LOG_ELAPSED_TIME_THRESHOLD) {
                Creature.logger.info("Polled Creature id, " + this.getWurmId() + ", which took " + lElapsedTime + " millis.");
            }
        }
    }
    
    public void setWebArmourModTime(final float time) {
        this.webArmourModTime = time;
    }
    
    private void checkStealthing() {
    }
    
    public boolean isSpellCaster() {
        return this.template.isCaster();
    }
    
    public boolean isSummoner() {
        return this.template.isSummoner();
    }
    
    public boolean isRespawn() {
        return false;
    }
    
    private void handleCreatureOutOfBounds() {
        Creature.logger.log(Level.WARNING, this.getName() + " was out of bounds. Killing.");
        Creatures.getInstance().setCreatureDead(this);
        Players.getInstance().setCreatureDead(this);
        this.destroy();
    }
    
    protected void checkBreedCounter() {
        if (this.breedCounter > 0) {
            this.breedCounter -= 201;
        }
        if (this.breedCounter < 0) {
            this.breedCounter = 0;
        }
        if (this.breedCounter == 0) {
            if (this.leader == null && !this.isDominated() && this.isInTheMoodToBreed(false)) {
                this.checkBreedingPossibility();
            }
            float mod = Servers.localServer.getBreedingTimer();
            if (mod <= 0.0f) {
                mod = 1.0f;
            }
            int base = (int)(84000.0f / mod);
            if (this.checkPregnancy(false)) {
                base = (int)(Servers.isThisAPvpServer() ? (2000.0f / mod) : (84000.0f / mod));
                this.forcedBreed = true;
            }
            else {
                base = (int)(Servers.isThisAPvpServer() ? (900.0f / mod) : (2000.0f / mod));
                this.forcedBreed = false;
            }
            this.breedCounter = base + (int)(Server.rand.nextInt(Math.max(1000, 100 * Math.abs(20 - this.getStatus().age))) / mod);
        }
    }
    
    public void pollLoyalty() {
        if (this.isDominated() && this.getStatus().pollLoyalty()) {
            if (this.getDominator() != null) {
                this.getDominator().getCommunicator().sendAlertServerMessage(this.getNameWithGenus() + " is tame no more.", (byte)2);
                if (this.getDominator().getPet() == this) {
                    this.getDominator().setPet(-10L);
                }
            }
            this.setDominator(-10L);
        }
    }
    
    public boolean isInRock() {
        return this.getLayer() < 0 && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(this.getTileX(), this.getTileY())));
    }
    
    public void findFood() {
        if (this.currentTile != null && !this.graze()) {
            final Item[] items2;
            final Item[] items = items2 = this.currentTile.getItems();
            for (final Item lItem : items2) {
                if (lItem.isEdibleBy(this)) {
                    if (lItem.getTemplateId() != 272) {
                        this.eat(lItem);
                        return;
                    }
                    if (lItem.isCorpseLootable()) {
                        this.eat(lItem);
                        return;
                    }
                }
            }
        }
    }
    
    public int eat(final Item item) {
        final int hungerStilled = MethodsItems.eat(this, item);
        if (hungerStilled > 0) {
            this.getStatus().modifyHunger(-hungerStilled, item.getNutritionLevel());
            Server.getInstance().broadCastAction(this.getNameWithGenus() + " eats " + item.getNameWithGenus() + ".", this, 5);
        }
        else if (item.getTemplateId() != 272) {
            Server.getInstance().broadCastAction(this.getNameWithGenus() + " eats " + item.getNameWithGenus() + ".", this, 5);
        }
        return hungerStilled;
    }
    
    public boolean graze() {
        if (!this.isGrazer() || !this.isOnSurface()) {
            return false;
        }
        if (this.hasTrait(13)) {
            if (Server.rand.nextBoolean()) {
                try {
                    final Skill str = this.skills.getSkill(102);
                    if (str.getKnowledge() > 15.0) {
                        str.setKnowledge(str.getKnowledge() - 0.003000000026077032, false);
                    }
                }
                catch (NoSuchSkillException nss) {
                    this.skills.learn(102, 20.0f);
                }
                return false;
            }
        }
        else if (Server.rand.nextBoolean()) {
            try {
                final Skill str = this.skills.getSkill(102);
                final double templateStr = this.getTemplate().getSkills().getSkill(102).getKnowledge();
                if (str.getKnowledge() < templateStr) {
                    str.setKnowledge(str.getKnowledge() + 0.029999999329447746, false);
                }
            }
            catch (NoSuchSkillException e) {
                this.skills.learn(102, 20.0f);
            }
            catch (Exception ex) {}
        }
        final int tile = Server.surfaceMesh.getTile(this.currentTile.tilex, this.currentTile.tiley);
        final byte type = Tiles.decodeType(tile);
        final Village v = Villages.getVillage(this.currentTile.tilex, this.currentTile.tiley, this.currentTile.isOnSurface());
        if (!this.hasTrait(22)) {
            return this.grazeNonCorrupt(tile, type, v);
        }
        return this.grazeCorrupt(tile, type);
    }
    
    private boolean grazeCorrupt(final int tile, final byte type) {
        if (type == Tiles.Tile.TILE_MYCELIUM.id || type == Tiles.Tile.TILE_FIELD.id || type == Tiles.Tile.TILE_FIELD2.id) {
            this.getStatus().modifyHunger(-10000, 0.9f);
            if (Server.rand.nextInt(20) == 0) {
                if (type == Tiles.Tile.TILE_FIELD.id || type == Tiles.Tile.TILE_FIELD2.id) {
                    TileFieldBehaviour.graze(this.currentTile.tilex, this.currentTile.tiley, tile);
                }
                else if (type == Tiles.Tile.TILE_MYCELIUM.id) {
                    GrassData.GrowthStage growthStage = GrassData.GrowthStage.decodeTileData(Tiles.decodeData(tile));
                    if (growthStage == GrassData.GrowthStage.SHORT) {
                        Server.setSurfaceTile(this.currentTile.tilex, this.currentTile.tiley, Tiles.decodeHeight(tile), Tiles.Tile.TILE_DIRT_PACKED.id, (byte)0);
                    }
                    else {
                        growthStage = growthStage.getPreviousStage();
                        Server.setSurfaceTile(this.currentTile.tilex, this.currentTile.tiley, Tiles.decodeHeight(tile), Tiles.Tile.TILE_MYCELIUM.id, GrassData.encodeGrassTileData(growthStage, GrassData.FlowerType.NONE));
                    }
                    Players.getInstance().sendChangedTile(this.currentTile.tilex, this.currentTile.tiley, true, true);
                }
            }
            Server.getInstance().broadCastAction(this.getNameWithGenus() + " grazes.", this, 5);
            return true;
        }
        return false;
    }
    
    private boolean grazeNonCorrupt(final int tile, final byte type, final Village v) {
        if (type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_FIELD.id || type == Tiles.Tile.TILE_FIELD2.id || type == Tiles.Tile.TILE_STEPPE.id || type == Tiles.Tile.TILE_ENCHANTED_GRASS.id) {
            this.getStatus().modifyHunger(-10000, (type == Tiles.Tile.TILE_STEPPE.id) ? 0.5f : 0.9f);
            if (Server.rand.nextInt(20) == 0) {
                int enchGrassPackChance = 120;
                if (v == null) {
                    enchGrassPackChance = 80;
                }
                else if (v.getCreatureRatio() > Village.OPTIMUMCRETRATIO) {
                    enchGrassPackChance = 240;
                }
                if (type == Tiles.Tile.TILE_FIELD.id || type == Tiles.Tile.TILE_FIELD2.id) {
                    TileFieldBehaviour.graze(this.currentTile.tilex, this.currentTile.tiley, tile);
                }
                else if (type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_STEPPE.id || (type == Tiles.Tile.TILE_ENCHANTED_GRASS.id && Server.rand.nextInt(enchGrassPackChance) == 0)) {
                    GrassData.GrowthStage growthStage = GrassData.GrowthStage.decodeTileData(Tiles.decodeData(tile));
                    if (growthStage == GrassData.GrowthStage.SHORT) {
                        Server.setSurfaceTile(this.currentTile.tilex, this.currentTile.tiley, Tiles.decodeHeight(tile), Tiles.Tile.TILE_DIRT_PACKED.id, (byte)0);
                    }
                    else {
                        growthStage = growthStage.getPreviousStage();
                        Server.setSurfaceTile(this.currentTile.tilex, this.currentTile.tiley, Tiles.decodeHeight(tile), Tiles.Tile.TILE_GRASS.id, GrassData.encodeGrassTileData(growthStage, GrassData.FlowerType.NONE));
                    }
                    Players.getInstance().sendChangedTile(this.currentTile.tilex, this.currentTile.tiley, true, true);
                }
            }
            Server.getInstance().broadCastAction(this.getNameWithGenus() + " grazes.", this, 5);
            return true;
        }
        return false;
    }
    
    public boolean pollAge() {
        final long start = System.nanoTime();
        try {
            int maxAge = this.template.getMaxAge();
            if (this.isReborn()) {
                maxAge = 14;
            }
            if (this.getStatus().pollAge(maxAge)) {
                this.sendDeathString();
                this.die(true, "Old Age");
                return true;
            }
            return false;
        }
        finally {}
    }
    
    public void sendDeathString() {
        if (!this.isOffline()) {
            String act = "hiccups";
            final int x = Server.rand.nextInt(6);
            if (x == 0) {
                act = "drools";
            }
            else if (x == 1) {
                act = "faints";
            }
            else if (x == 2) {
                act = "makes a weird gurgly sound";
            }
            else if (x == 3) {
                act = "falls down";
            }
            else if (x == 4) {
                act = "rolls over";
            }
            Server.getInstance().broadCastAction(this.getNameWithGenus() + " " + act + " and dies.", this, 5);
        }
    }
    
    public void pollFavor() {
        if ((this.isSpellCaster() || this.isSummoner()) && Server.rand.nextInt(30) == 0) {
            try {
                this.setFavor(this.getFavor() + 10.0f);
            }
            catch (Exception ex) {}
        }
    }
    
    public boolean isSalesman() {
        return this.template.getTemplateId() == 9;
    }
    
    public boolean isAvatar() {
        return this.template.getTemplateId() == 78 || this.template.getTemplateId() == 79 || this.template.getTemplateId() == 80 || this.template.getTemplateId() == 81 || this.template.getTemplateId() == 68;
    }
    
    public void removeRandomItems() {
        if (!this.isTrading() && this.isNpcTrader() && Server.rand.nextInt(86400) == 0) {
            try {
                this.actions.getCurrentAction();
            }
            catch (NoSuchActionException nsa) {
                final Shop myshop = Economy.getEconomy().getShop(this);
                if (myshop.getOwnerId() == -10L) {
                    final Shop kingsMoney = Economy.getEconomy().getKingsShop();
                    if (kingsMoney.getMoney() > 0L) {
                        int value = 0;
                        value = (int)(kingsMoney.getMoney() / Shop.getNumTraders());
                        if (!Servers.localServer.HOMESERVER) {
                            value *= (int)(1.0f + Zones.getPercentLandForKingdom(this.getKingdomId()) / 100.0f);
                            value *= (int)(1.0f + Items.getBattleCampControl(this.getKingdomId()) / 10.0f);
                        }
                        if (value > 0 && myshop != null && myshop.getMoney() < Servers.localServer.getTraderMaxIrons() && (myshop.getSellRatio() > 0.1f || Server.getInstance().isPS()) && (Server.getInstance().isPS() || Servers.localServer.id != 15 || kingsMoney.getMoney() > 2000000L)) {
                            myshop.setMoney(myshop.getMoney() + value);
                            kingsMoney.setMoney(kingsMoney.getMoney() - value);
                        }
                    }
                }
                else if (this.canAutoDismissMerchant(myshop)) {
                    try {
                        final Item sign = ItemFactory.createItem(209, 10.0f + Server.rand.nextFloat() * 10.0f, this.getName());
                        sign.setDescription("Due to poor business I have moved on. Thank you for your time. " + this.getName());
                        sign.setLastOwnerId(myshop.getOwnerId());
                        sign.putItemInfrontof(this);
                        sign.setIsPlanted(true);
                    }
                    catch (Exception e) {
                        Creature.logger.log(Level.WARNING, e.getMessage() + " " + this.getName() + " at " + this.getTileX() + ", " + this.getTileY(), e);
                    }
                    TraderManagementQuestion.dismissMerchant(this, this.getWurmId());
                }
            }
        }
    }
    
    private boolean canAutoDismissMerchant(final Shop myshop) {
        if (myshop.howLongEmpty() > 7257600000L) {
            return true;
        }
        final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(myshop.getOwnerId());
        if (pinf != null) {
            try {
                if (!pinf.loaded) {
                    pinf.load();
                }
                if (pinf.lastLogin == 0L && System.currentTimeMillis() - pinf.lastLogout > 7257600000L) {
                    Creature.logger.log(Level.INFO, pinf.getName() + " last login was " + Server.getTimeFor(System.currentTimeMillis() - pinf.lastLogout) + " ago.");
                    return true;
                }
                return false;
            }
            catch (IOException ex) {}
        }
        return true;
    }
    
    public float getArmourMod() {
        return this.template.getNaturalArmour();
    }
    
    public final Vector2f getPos2f() {
        return this.getStatus().getPosition2f();
    }
    
    public final Vector3f getPos3f() {
        return this.getStatus().getPosition3f();
    }
    
    public final float getPosX() {
        return this.getStatus().getPositionX();
    }
    
    public final float getPosY() {
        return this.getStatus().getPositionY();
    }
    
    public final float getPositionZ() {
        return this.getStatus().getPositionZ();
    }
    
    @Nonnull
    public final TilePos getTilePos() {
        return TilePos.fromXY(this.getTileX(), this.getTileY());
    }
    
    public final int getTileX() {
        return (int)this.getPosX() >> 2;
    }
    
    public final int getTileY() {
        return (int)this.getPosY() >> 2;
    }
    
    public final int getPosZDirts() {
        return (int)(this.getPositionZ() * 10.0f);
    }
    
    public final void pollItems() {
        this.resetCompassLantern();
        ++this.pollCounter;
        boolean triggerPoll = false;
        if (this.isHorse() && this.getBody().getAllItems().length > 0) {
            triggerPoll = true;
        }
        if (this.isPlayer() || ((this.isReborn() || this.isHuman()) && this.pollCounter > 10800) || (triggerPoll && this.pollCounter > 60L)) {
            if (!this.checkedHotItemsAfterLogin && this.isPlayer()) {
                this.checkedHotItemsAfterLogin = true;
                final long timeSinceLastCoolingCheck = System.currentTimeMillis() - PlayerInfoFactory.createPlayerInfo(this.getName()).getLastLogout();
                this.getInventory().pollCoolingItems(this, timeSinceLastCoolingCheck);
            }
            this.getInventory().pollOwned(this);
            this.getBody().getBodyItem().pollOwned(this);
            if (triggerPoll) {
                this.getInventory().pollCoolingItems(this, (this.pollCounter - 1) * 1000L);
                this.getBody().getBodyItem().pollCoolingItems(this, (this.pollCounter - 1) * 1000L);
            }
        }
        if (this.pollCounter > 10800 || (triggerPoll && this.pollCounter > 60L)) {
            this.pollCounter = 0;
        }
        this.pollCompassLantern();
    }
    
    public boolean isLastDeath() {
        return false;
    }
    
    public boolean isOnHostileHomeServer() {
        return false;
    }
    
    public void playPersonalSound(final String soundName) {
    }
    
    public final void setReputationEffects() {
        if (Servers.localServer.HOMESERVER && ((!this.isPlayer() && this.isDominated()) || this.isRidden() || this.getHitched() != null) && this.attackers != null) {
            for (final Long attl : this.attackers.keySet()) {
                try {
                    final Creature attacker = Server.getInstance().getCreature(attl);
                    if (!attacker.isPlayer() && !attacker.isDominated()) {
                        continue;
                    }
                    if (this.isRidden()) {
                        if (attacker.getCitizenVillage() != null && this.getCurrentVillage() == attacker.getCitizenVillage()) {
                            continue;
                        }
                        for (final Long riderLong : this.getRiders()) {
                            try {
                                final Creature rider = Server.getInstance().getCreature(riderLong);
                                if (rider == attacker || rider.isOkToKillBy(attacker)) {
                                    continue;
                                }
                                attacker.setUnmotivatedAttacker();
                                attacker.setReputation(attacker.getReputation() - 10);
                            }
                            catch (NoSuchPlayerException ex) {}
                        }
                    }
                    else if (this.getHitched() != null) {
                        if ((attacker.getCitizenVillage() != null && this.getCurrentVillage() == attacker.getCitizenVillage()) || this.getHitched().isCreature()) {
                            continue;
                        }
                        try {
                            final Item i = Items.getItem(this.getHitched().wurmid);
                            final long ownid = i.getLastOwnerId();
                            if (ownid == attacker.getWurmId()) {
                                continue;
                            }
                            try {
                                final byte kingd = Players.getInstance().getKingdomForPlayer(ownid);
                                if (!attacker.isFriendlyKingdom(kingd) || attacker.hasBeenAttackedBy(ownid)) {
                                    continue;
                                }
                                boolean ok = false;
                                try {
                                    final Creature owner = Server.getInstance().getCreature(ownid);
                                    if (owner.isOkToKillBy(attacker)) {
                                        ok = true;
                                    }
                                }
                                catch (NoSuchCreatureException ex2) {}
                                if (ok) {
                                    continue;
                                }
                                attacker.setUnmotivatedAttacker();
                                attacker.setReputation(attacker.getReputation() - 10);
                            }
                            catch (Exception ex3) {}
                        }
                        catch (NoSuchItemException nsi) {
                            Creature.logger.log(Level.INFO, this.getHitched().wurmid + " no such item:", nsi);
                        }
                    }
                    else if (this.isDominated()) {
                        if (!attacker.isFriendlyKingdom(this.getKingdomId())) {
                            continue;
                        }
                        boolean ok2 = false;
                        try {
                            final Creature owner2 = Server.getInstance().getCreature(this.dominator);
                            if (attacker == owner2 || owner2.isOkToKillBy(attacker)) {
                                ok2 = true;
                            }
                        }
                        catch (NoSuchCreatureException ex4) {}
                        if (ok2) {
                            continue;
                        }
                        attacker.setUnmotivatedAttacker();
                        attacker.setReputation(attacker.getReputation() - 10);
                    }
                    else {
                        if (this.getCurrentVillage() == null) {
                            continue;
                        }
                        final Brand brand = Creatures.getInstance().getBrand(this.getWurmId());
                        if (brand == null) {
                            continue;
                        }
                        try {
                            final Village villageBrand = Villages.getVillage((int)brand.getBrandId());
                            if (this.getCurrentVillage() != villageBrand || attacker.getCitizenVillage() == villageBrand) {
                                continue;
                            }
                            attacker.setUnmotivatedAttacker();
                            attacker.setReputation(attacker.getReputation() - 10);
                        }
                        catch (NoSuchVillageException nsv) {
                            brand.deleteBrand();
                        }
                    }
                }
                catch (Exception ex5) {}
            }
        }
    }
    
    public void die(final boolean freeDeath, final String reasonOfDeath) {
        this.die(freeDeath, reasonOfDeath, false);
    }
    
    public void die(final boolean freeDeath, final String reasonOfDeath, final boolean noCorpse) {
        final WcKillCommand wkc = new WcKillCommand(WurmId.getNextWCCommandId(), this.getWurmId());
        if (Servers.isThisLoginServer()) {
            wkc.sendFromLoginServer();
        }
        else {
            wkc.sendToLoginServer();
        }
        if (this.isPregnant()) {
            Offspring.deleteSettings(this.getWurmId());
        }
        if (this.getTemplate().getCreatureAI() != null) {
            final boolean fullOverride = this.getTemplate().getCreatureAI().creatureDied(this);
            if (fullOverride) {
                return;
            }
        }
        String corpseDescription = "";
        if (this.template.isHorse) {
            final String col = corpseDescription = this.template.getColourName(this.status);
        }
        else if (this.template.isBlackOrWhite) {
            if (!this.hasTrait(15) && !this.hasTrait(16) && !this.hasTrait(18) && !this.hasTrait(24) && !this.hasTrait(25) && !this.hasTrait(23) && !this.hasTrait(30) && !this.hasTrait(31) && !this.hasTrait(32) && !this.hasTrait(33)) {
                if (!this.hasTrait(34)) {
                    if (this.hasTrait(17)) {
                        corpseDescription = "black";
                    }
                }
            }
        }
        else if (this.template.isColoured) {
            corpseDescription = this.template.getColourName(this.getStatus());
        }
        if (this.isCaredFor()) {
            if (corpseDescription.equals("")) {
                corpseDescription += reasonOfDeath.toLowerCase();
            }
            else {
                corpseDescription = corpseDescription + " [" + reasonOfDeath.toLowerCase() + "]";
            }
        }
        if (this.getTemplate().getTemplateId() == 105) {
            try {
                final Item water = ItemFactory.createItem(128, 100.0f, "");
                this.getInventory().insertItem(water);
            }
            catch (NoSuchTemplateException nst3) {
                Creature.logger.log(Level.WARNING, this.getName() + " No template for item id " + 128);
            }
            catch (FailedException e) {
                Creature.logger.log(Level.WARNING, this.getName() + " failed for item id " + 128);
            }
            final Weather weather = Server.getWeather();
            if (weather != null) {
                weather.modifyFogTarget(-0.025f);
            }
        }
        if (this.isUnique() && !this.isReborn()) {
            final Player[] ps = Players.getInstance().getPlayers();
            final HashSet<Player> lootReceivers = new HashSet<Player>();
            for (final Player p : ps) {
                if (p != null && p.getInventory() != null && p.isWithinDistanceTo(this, 300.0f) && p.isPaying()) {
                    if (!p.isDead()) {
                        try {
                            final Item blood = ItemFactory.createItem(866, 100.0f, "");
                            blood.setData2(this.template.getTemplateId());
                            p.getInventory().insertItem(blood);
                            lootReceivers.add(p);
                        }
                        catch (NoSuchTemplateException nst) {
                            Creature.logger.log(Level.WARNING, p.getName() + " No template for item id " + 866);
                        }
                        catch (FailedException fe) {
                            Creature.logger.log(Level.WARNING, p.getName() + " " + fe.getMessage() + ":" + 866);
                        }
                    }
                    else {
                        Creature.logger.log(Level.INFO, "Player " + p.getName() + " is dead, and therefor received no loot from " + this.getNameWithGenus() + ".");
                    }
                }
            }
            this.setPathing(false, true);
            if (this.isDragon()) {
                final Set<Player> primeLooters = new HashSet<Player>();
                final Set<Player> leecher = new HashSet<Player>();
                for (final Player looter : lootReceivers) {
                    final Skill bStrength = looter.getBodyStrength();
                    final Skill bControl = looter.getBodyControlSkill();
                    final Skill fighting = looter.getFightingSkill();
                    if ((bStrength != null && bStrength.getRealKnowledge() >= 30.0) || (bControl != null && bControl.getRealKnowledge() >= 30.0) || (fighting != null && fighting.getRealKnowledge() >= 65.0) || looter.isPriest()) {
                        primeLooters.add(looter);
                    }
                    else {
                        leecher.add(looter);
                    }
                }
                int lootTemplate = 371;
                if (this.getTemplate().getTemplateId() == 16 || this.getTemplate().getTemplateId() == 89 || this.getTemplate().getTemplateId() == 91 || this.getTemplate().getTemplateId() == 90 || this.getTemplate().getTemplateId() == 92) {
                    lootTemplate = 372;
                }
                try {
                    this.distributeDragonScaleOrHide(primeLooters, leecher, lootTemplate);
                }
                catch (NoSuchTemplateException nst4) {
                    Creature.logger.log(Level.WARNING, "No template for " + lootTemplate + "! Players to receive were:");
                    for (final Player p2 : lootReceivers) {
                        Creature.logger.log(Level.WARNING, p2.getName());
                    }
                }
            }
        }
        this.removeIllusion();
        this.setReputationEffects();
        this.getCombatHandler().clearMoveStack();
        this.getCommunicator().setGroundOffset(0, true);
        this.setDoLavaDamage(false);
        this.setDoAreaEffect(false);
        if (this.isPlayer()) {
            for (int x = 0; x < 5; ++x) {
                this.getStatus().decreaseFat();
            }
        }
        this.combatRound = 0;
        Item corpse = null;
        final int tilex = this.getTileX();
        final int tiley = this.getTileY();
        try {
            final boolean wasHunted = this.hasAttackedUnmotivated();
            if (this.isPlayer()) {
                final Item i = this.getDraggedItem();
                if (i != null && (i.getTemplateId() == 539 || i.getTemplateId() == 186 || i.getTemplateId() == 445 || i.getTemplateId() == 1125)) {
                    this.achievement(72);
                }
                if (this.getVehicle() != -10L) {
                    final Vehicle vehic = Vehicles.getVehicleForId(this.getVehicle());
                    if (vehic != null && vehic.getPilotId() == this.getWurmId()) {
                        try {
                            final Item c = Items.getItem(this.getVehicle());
                            if (c.getTemplateId() == 539) {
                                this.achievement(71);
                            }
                        }
                        catch (NoSuchItemException ex2) {}
                    }
                }
                if (!PlonkData.DEATH.hasSeenThis(this)) {
                    PlonkData.DEATH.trigger(this);
                }
            }
            if (this.getDraggedItem() != null) {
                MethodsItems.stopDragging(this, this.getDraggedItem());
            }
            this.stopLeading();
            if (this.leader != null) {
                this.leader.removeFollower(this);
            }
            this.clearLinks();
            this.disableLink();
            this.disembark(false);
            if (!this.hasNoServerSound()) {
                SoundPlayer.playSound(this.getDeathSound(), this, 1.6f);
            }
            if (this.musicPlayer != null) {
                this.musicPlayer.checkMUSIC_DYING1_SND();
            }
            Creatures.getInstance().setCreatureDead(this);
            Players.getInstance().setCreatureDead(this);
            if (this.getSpellEffects() != null) {
                this.getSpellEffects().destroy(true);
            }
            if (this.currentVillage != null) {
                this.currentVillage.removeTarget(this.getWurmId(), true);
            }
            this.setOpponent(null);
            this.target = -10L;
            try {
                this.getCurrentAction().stop(false);
            }
            catch (NoSuchActionException ex3) {}
            this.actions.clear();
            if (this.isKing()) {
                final King king = King.getKing(this.getKingdomId());
                if (king != null) {
                    if (king.getChallengeAcceptedDate() > 0L && System.currentTimeMillis() > king.getChallengeAcceptedDate()) {
                        king.setFailedChallenge();
                    }
                    if (this.isInOwnDuelRing() && !king.hasFailedAllChallenges()) {
                        king.setFailedChallenge();
                    }
                }
            }
            this.getCommunicator().sendSafeServerMessage("You are dead.");
            this.getCommunicator().sendCombatSafeMessage("You are dead.");
            Server.getInstance().broadCastAction(this.getNameWithGenus() + " is dead. R.I.P.", this, 5);
            if (!this.isPlayer() && (this.isTrader() || this.isSalesman() || this.isBartender() || (this.template != null && (this.template.id == 63 || this.template.id == 62)))) {
                String message = "(" + this.getWurmId() + ") died at [" + this.getTileX() + ", " + this.getTileY() + "] surf=" + this.isOnSurface() + " with the reason of death being " + reasonOfDeath;
                if (this.attackers != null && this.attackers.size() > 0) {
                    message = message + ". numAttackers=" + this.attackers.size() + " :";
                    int counter = 0;
                    for (final long playerID : this.attackers.keySet()) {
                        ++counter;
                        message = message + " " + PlayerInfoFactory.getPlayerName(playerID) + ((counter == this.attackers.size()) ? "," : ".");
                    }
                }
                Players.getInstance().sendGmMessage(null, this.getName(), message, false);
                final String templateAndName = ((this.getTemplate() != null) ? this.getTemplate().getName() : "Important creature") + " " + this.getName() + " died";
                Creature.logger.warning(templateAndName + " " + message);
                final WcTrelloDeaths wtd = new WcTrelloDeaths(templateAndName, message);
                wtd.sendToLoginServer();
            }
            if (!this.isGhost() && !this.template.isNoCorpse() && !noCorpse && (this.getCreatureAIData() == null || (this.getCreatureAIData() != null && this.getCreatureAIData().doesDropCorpse()))) {
                corpse = ItemFactory.createItem(272, 100.0f, null);
                corpse.setPosXY(this.getStatus().getPositionX(), this.getStatus().getPositionY());
                corpse.setPosZ(this.calculatePosZ());
                corpse.onBridge = this.getBridgeId();
                if (this.hasCustomSize()) {
                    corpse.setSizes((int)(corpse.getSizeX() * (this.getSizeModX() & 0xFF) / 64.0f), (int)(corpse.getSizeY() * (this.getSizeModY() & 0xFF) / 64.0f), (int)(corpse.getSizeZ() * (this.getSizeModZ() & 0xFF) / 64.0f));
                }
                corpse.setRotation(normalizeAngle(this.getStatus().getRotation() - 180.0f));
                final int nameLength = 10 + this.name.length() + this.getStatus().getAgeString().length() + 1 + this.getStatus().getTypeString().length();
                final int nameLengthNoType = 10 + this.name.length() + this.getStatus().getAgeString().length();
                final int nameLengthNoAge = 10 + this.name.length() + 1 + this.getStatus().getTypeString().length();
                if (this.isPlayer()) {
                    corpse.setName("corpse of " + this.name);
                }
                else if (nameLength < 40) {
                    corpse.setName("corpse of " + this.getStatus().getAgeString() + " " + ((nameLength < 40) ? this.getStatus().getTypeString() : "") + this.name.toLowerCase());
                }
                else if (nameLengthNoAge < 40) {
                    corpse.setName("corpse of " + this.getStatus().getTypeString() + this.name.toLowerCase());
                }
                else if (nameLengthNoType < 40) {
                    corpse.setName("corpse of " + this.getStatus().getAgeString() + " " + this.name.toLowerCase());
                }
                else if (("corpse of " + this.name).length() < 40) {
                    corpse.setName("corpse of " + this.name.toLowerCase());
                }
                else {
                    final StringTokenizer strt = new StringTokenizer(this.name.toLowerCase());
                    final int maxNumber = strt.countTokens();
                    String coname = "corpse of " + strt.nextToken();
                    int number = 1;
                    while (strt.hasMoreTokens()) {
                        ++number;
                        final String next = strt.nextToken();
                        if (maxNumber < 4 || (maxNumber > 4 && number > 4)) {
                            if ((coname + " " + next).length() >= 40) {
                                break;
                            }
                            coname += " ";
                            coname += next;
                        }
                    }
                    corpse.setName(coname);
                }
                byte extra1 = -1;
                final byte extra2 = this.status.modtype;
                if (this.template.isHorse || this.template.isBlackOrWhite) {
                    extra1 = this.template.getColourCode(this.status);
                }
                if (this.isReborn()) {
                    corpse.setDamage(20.0f);
                    corpse.setButchered();
                    corpse.setAllData(this.template.getTemplateId(), 1, extra1, extra2);
                }
                else {
                    corpse.setAllData(this.template.getTemplateId(), this.getStatus().fat << 1, extra1, extra2);
                }
                if (this.isUnique()) {
                    Server.getInstance().broadCastNormal(this.getNameWithGenus() + " has been slain.");
                    if (!Servers.localServer.EPIC && !this.isReborn()) {
                        try {
                            boolean drop = false;
                            if (this.isDragon()) {
                                drop = (Server.rand.nextInt(10) == 0);
                            }
                            else {
                                drop = Server.rand.nextBoolean();
                            }
                            if (drop) {
                                int item = 795 + Server.rand.nextInt(16);
                                if (item == 1009) {
                                    item = 807;
                                }
                                else if (item == 805) {
                                    item = 808;
                                }
                                final Item epicItem = ItemFactory.createItem(item, 60 + Server.rand.nextInt(20), "");
                                epicItem.setOwnerId(corpse.getWurmId());
                                epicItem.setLastOwnerId(corpse.getWurmId());
                                if (this.isDragon()) {
                                    epicItem.setAuxData((byte)2);
                                }
                                Creature.logger.info("Dropping a " + epicItem.getName() + " (" + epicItem.getWurmId() + ")  for the slaying of " + corpse.getName());
                                corpse.insertItem(epicItem);
                            }
                        }
                        catch (NoSuchTemplateException nst5) {
                            Creature.logger.log(Level.WARNING, "No template for item id 866");
                        }
                        catch (FailedException fe2) {
                            Creature.logger.log(Level.WARNING, fe2.getMessage() + ":" + 866);
                        }
                    }
                    else if (Servers.localServer.EPIC && !Servers.localServer.HOMESERVER && this.isDragon()) {
                        try {
                            final boolean dropLoot = Server.rand.nextBoolean();
                            if (dropLoot) {
                                final int lootId = CreatureTemplateCreator.getDragonLoot(this.template.getTemplateId());
                                if (lootId > 0) {
                                    final Item loot = ItemFactory.createItem(lootId, 60 + Server.rand.nextInt(20), "");
                                    Creature.logger.info("Dropping a " + loot.getName() + " (" + loot.getWurmId() + ") for the slaying of " + corpse.getName());
                                    corpse.insertItem(loot);
                                    loot.setOwnerId(corpse.getWurmId());
                                }
                            }
                        }
                        catch (Exception ex4) {}
                    }
                }
                if (this.isPlayer() && !wasHunted && this.getReputation() >= 0 && !this.isInPvPZone() && Servers.localServer.KINGDOM != 0 && !this.isOnHostileHomeServer()) {
                    boolean killedInVillageWar = false;
                    if (this.attackers != null) {
                        for (final Long l : this.attackers.keySet()) {
                            try {
                                final Creature c2 = Creatures.getInstance().getCreature(l);
                                if (c2.getCitizenVillage() == null || !c2.getCitizenVillage().isEnemy(this) || !Servers.isThisAPvpServer()) {
                                    continue;
                                }
                                Creature.logger.log(Level.INFO, this.getName() + " was killed by " + c2.getName() + " during village war. May be looted.");
                                killedInVillageWar = true;
                            }
                            catch (Exception ex5) {}
                        }
                    }
                    if (!killedInVillageWar) {
                        corpse.setProtected(true);
                    }
                }
                corpse.setAuxData(this.getKingdomId());
                corpse.setWeight((int)Math.min(50000.0f, this.status.body.getWeight(this.status.fat)), false);
                corpse.setLastOwnerId(this.getWurmId());
                if (this.isKingdomGuard()) {
                    corpse.setDamage(50.0f);
                }
                if (this.getSex() == 1) {
                    corpse.setFemale(true);
                }
                corpse.setDescription(corpseDescription);
                if (!this.isPlayer() && !Servers.isThisAPvpServer()) {
                    final Brand brand = Creatures.getInstance().getBrand(this.getWurmId());
                    if (brand != null) {
                        try {
                            corpse.setWasBrandedTo(brand.getBrandId());
                            final PermissionsPlayerList allowedList = this.getPermissionsPlayerList();
                            final PermissionsByPlayer[] pbpList = allowedList.getPermissionsByPlayer();
                            final byte bito = ItemSettings.CorpsePermissions.COMMANDER.getBit();
                            final int valueo = ItemSettings.CorpsePermissions.COMMANDER.getValue();
                            final byte bitx = ItemSettings.CorpsePermissions.EXCLUDE.getBit();
                            final int valuex = ItemSettings.CorpsePermissions.EXCLUDE.getValue();
                            Village bVill = null;
                            for (final PermissionsByPlayer pbp : pbpList) {
                                if (pbp.getPlayerId() == -60L) {
                                    if (bVill == null) {
                                        bVill = Villages.getVillage((int)brand.getBrandId());
                                    }
                                    int value = 0;
                                    if (pbp.hasPermission(bito)) {
                                        value += valueo;
                                    }
                                    if (pbp.hasPermission(bitx)) {
                                        value += valuex;
                                    }
                                    if (value != 0) {
                                        for (final Citizen citz : bVill.getCitizens()) {
                                            if (citz.isPlayer() && citz.getRole().mayBrand()) {
                                                ItemSettings.addPlayer(corpse.getWurmId(), citz.wurmId, value);
                                            }
                                        }
                                    }
                                }
                            }
                            for (final PermissionsByPlayer pbp : pbpList) {
                                if (pbp.getPlayerId() != -60L) {
                                    int value = 0;
                                    if (pbp.hasPermission(bito)) {
                                        value += valueo;
                                    }
                                    if (pbp.hasPermission(bitx)) {
                                        value += valuex;
                                    }
                                    if (value != 0) {
                                        ItemSettings.addPlayer(corpse.getWurmId(), pbp.getPlayerId(), value);
                                    }
                                }
                            }
                        }
                        catch (NoSuchVillageException e2) {
                            Creatures.getInstance().setBrand(this.getWurmId(), -10L);
                        }
                    }
                }
                final VolaTile vvtile = Zones.getOrCreateTile(tilex, tiley, this.isOnSurface());
                vvtile.addItem(corpse, false, this.getWurmId(), false);
            }
            else if (this.isGhost() || this.template.isNoCorpse()) {
                final int[] butcheredItems = this.getTemplate().getItemsButchered();
                for (int x2 = 0; x2 < butcheredItems.length; ++x2) {
                    try {
                        ItemFactory.createItem(butcheredItems[x2], 20.0f + Server.rand.nextFloat() * 80.0f, this.getPosX(), this.getPosY(), Server.rand.nextInt() * 360, this.isOnSurface(), (byte)0, this.getStatus().getBridgeId(), this.getName());
                    }
                    catch (FailedException fe) {
                        Creature.logger.log(Level.WARNING, fe.getMessage());
                    }
                    catch (NoSuchTemplateException nst) {
                        Creature.logger.log(Level.WARNING, nst.getMessage());
                    }
                }
            }
            final VolaTile vtile = Zones.getOrCreateTile(tilex, tiley, this.isOnSurface());
            boolean keepItems = this.isTransferring();
            if (!this.isOnCurrentServer()) {
                keepItems = true;
            }
            if (this.getDeity() != null && this.getDeity().isDeathItemProtector() && this.getFaith() >= 70.0f && this.getFavor() >= 35.0f) {
                float chance = 0.35f;
                String successMessage = this.getDeity().getName() + " is with you and keeps your items safe.";
                String failMessage = this.getDeity().getName() + " could not keep your items safe this time.";
                final float rand = Server.rand.nextFloat();
                if (this.isDeathProtected()) {
                    chance = 0.5f;
                    if (rand > 0.35f && rand <= chance) {
                        successMessage = this.getDeity().getName() + " could not keep your items safe this time, but ethereal strands of web attach to your items and keep them safe, close to your spirit!";
                    }
                    else {
                        failMessage = this.getDeity().getName() + " could not keep your items safe this time.";
                    }
                }
                if (rand <= chance) {
                    this.getCommunicator().sendNormalServerMessage(successMessage);
                    keepItems = true;
                }
                else {
                    this.getCommunicator().sendNormalServerMessage(failMessage);
                }
            }
            else if (this.isDeathProtected()) {
                if (Server.rand.nextInt(2) > 0) {
                    this.getCommunicator().sendNormalServerMessage("Ethereal strands of web attach to your items and keep them safe, close to your spirit!");
                    keepItems = true;
                }
                else {
                    this.getCommunicator().sendNormalServerMessage("Your items could not be kept safe this time.");
                }
            }
            if (this.isPlayer()) {
                try {
                    final Item legs = this.getBody().getBodyPart(19);
                    boolean found = false;
                    final Set<Item> worn = legs.getItems();
                    if (worn != null) {
                        for (final Item w : worn) {
                            if (w.isArmour()) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        Creature.pantLess.add(this.getWurmId());
                    }
                }
                catch (NoSpaceException ex6) {}
            }
            boolean insertItem = true;
            boolean dropNewbieItems = false;
            if (this.attackers != null) {
                for (final Long cid : this.attackers.keySet()) {
                    if (WurmId.getType(cid) == 0 && (!Servers.localServer.isChallengeServer() || this.getPlayingTime() > 86400000L)) {
                        dropNewbieItems = true;
                        break;
                    }
                }
            }
            final Item inventory = this.getInventory();
            final Item[] invarr = inventory.getAllItems(true);
            for (int x3 = 0; x3 < invarr.length; ++x3) {
                if (invarr[x3].isTraded() && this.getTrade() != null) {
                    invarr[x3].getTradeWindow().removeItem(invarr[x3]);
                }
                boolean destroyChall = false;
                if (Features.Feature.FREE_ITEMS.isEnabled() && invarr[x3].isChallengeNewbieItem() && (invarr[x3].isArmour() || invarr[x3].isWeapon() || invarr[x3].isShield())) {
                    destroyChall = true;
                }
                if (destroyChall) {
                    Items.destroyItem(invarr[x3].getWurmId());
                }
                else {
                    Label_4921: {
                        Block_168: {
                            if (!invarr[x3].isArtifact()) {
                                if (!keepItems && !invarr[x3].isNoDrop()) {
                                    if (!invarr[x3].isNewbieItem() || dropNewbieItems) {
                                        break Label_4921;
                                    }
                                    if (invarr[x3].isHollow() && !invarr[x3].isTent()) {
                                        break Block_168;
                                    }
                                }
                                if (!invarr[x3].isArtifact() && !keepItems) {
                                    try {
                                        final Item parent = invarr[x3].getParent();
                                        invarr[x3].setBusy(false);
                                        insertItem = !parent.isNoDrop();
                                        if (invarr[x3].getTemplateId() == 443 && this.getStrengthSkill() <= 21.0 && this.getFaith() <= 35.0f) {
                                            insertItem = false;
                                            if (!invarr[x3].setDamage(invarr[x3].getDamage() + 0.3f, true)) {
                                                insertItem = true;
                                            }
                                        }
                                        if (insertItem) {
                                            parent.dropItem(invarr[x3].getWurmId(), false);
                                            inventory.insertItem(invarr[x3], true);
                                        }
                                    }
                                    catch (NoSuchItemException nsi) {
                                        Creature.logger.log(Level.WARNING, this.getName() + " " + invarr[x3].getName() + ":" + nsi.getMessage(), nsi);
                                    }
                                }
                                continue;
                            }
                        }
                        try {
                            final Item parent = invarr[x3].getParent();
                            if (inventory.equals(parent) || parent.getTemplateId() == 824) {
                                parent.dropItem(invarr[x3].getWurmId(), true);
                                invarr[x3].setBusy(false);
                                if (corpse == null || !corpse.insertItem(invarr[x3], true)) {
                                    if (invarr[x3].isTent() && invarr[x3].isNewbieItem()) {
                                        Items.destroyItem(invarr[x3].getWurmId());
                                    }
                                    else {
                                        vtile.addItem(invarr[x3], false, false);
                                    }
                                }
                            }
                        }
                        catch (NoSuchItemException nsi) {
                            Creature.logger.log(Level.WARNING, this.getName() + " " + invarr[x3].getName() + ":" + nsi.getMessage(), nsi);
                        }
                    }
                }
            }
            final Item[] boditems = this.getBody().getContainersAndWornItems();
            for (int x4 = 0; x4 < boditems.length; ++x4) {
                if (boditems[x4].isTraded() && this.getTrade() != null) {
                    boditems[x4].getTradeWindow().removeItem(boditems[x4]);
                }
                if (boditems[x4].isArtifact() || (!keepItems && !boditems[x4].isNoDrop() && (!boditems[x4].isNewbieItem() || dropNewbieItems || (boditems[x4].isHollow() && !boditems[x4].isTent())))) {
                    if (boditems[x4].isHollow()) {
                        final Item[] allItems;
                        final Item[] containedItems = allItems = boditems[x4].getAllItems(false);
                        for (final Item lContainedItem : allItems) {
                            Label_5621: {
                                if (!lContainedItem.isNoDrop()) {
                                    if (!lContainedItem.isNewbieItem() || dropNewbieItems || lContainedItem.isHollow()) {
                                        break Label_5621;
                                    }
                                }
                                try {
                                    lContainedItem.setBusy(false);
                                    final Item parent2 = lContainedItem.getParent();
                                    parent2.dropItem(lContainedItem.getWurmId(), false);
                                    inventory.insertItem(lContainedItem, true);
                                }
                                catch (NoSuchItemException nsi2) {
                                    Creature.logger.log(Level.WARNING, this.getName() + ":" + nsi2.getMessage(), nsi2);
                                }
                            }
                        }
                    }
                    try {
                        final Item parent = boditems[x4].getParent();
                        parent.dropItem(boditems[x4].getWurmId(), true);
                        boditems[x4].setBusy(false);
                        if (corpse == null || !corpse.insertItem(boditems[x4], true)) {
                            if (boditems[x4].isTent() && boditems[x4].isNewbieItem()) {
                                Items.destroyItem(invarr[x4].getWurmId());
                            }
                            else {
                                vtile.addItem(boditems[x4], false, false);
                            }
                        }
                    }
                    catch (NoSuchItemException nsi) {
                        Creature.logger.log(Level.WARNING, this.getName() + ":" + nsi.getMessage(), nsi);
                    }
                }
                else if (!boditems[x4].isArtifact() && !keepItems) {
                    try {
                        final Item parent = boditems[x4].getParent();
                        boditems[x4].setBusy(false);
                        insertItem = !parent.isNoDrop();
                        if (boditems[x4].getTemplateId() == 443 && this.getStrengthSkill() <= 21.0 && this.getFaith() <= 35.0f) {
                            insertItem = false;
                            if (!boditems[x4].setDamage(boditems[x4].getDamage() + 0.3f, true)) {
                                insertItem = true;
                            }
                        }
                        if (insertItem) {
                            parent.dropItem(boditems[x4].getWurmId(), false);
                            inventory.insertItem(boditems[x4], true);
                        }
                    }
                    catch (NoSuchItemException nsi) {
                        Creature.logger.log(Level.WARNING, this.getName() + " " + boditems[x4].getName() + ":" + nsi.getMessage(), nsi);
                    }
                }
            }
        }
        catch (FailedException fe3) {
            Creature.logger.log(Level.WARNING, this.getName() + ":" + fe3.getMessage(), fe3);
        }
        catch (NoSuchTemplateException nst2) {
            Creature.logger.log(Level.WARNING, this.getName() + ":" + nst2.getMessage(), nst2);
        }
        if (corpse != null) {
            if (this.isSuiciding() && corpse.getAllItems(true).length == 0) {
                Items.destroyItem(corpse.getWurmId());
                corpse = null;
            }
        }
        else {
            this.playAnimation("die", false);
        }
        try {
            this.setBridgeId(-10L);
            this.getBody().healFully();
        }
        catch (Exception ex) {
            Creature.logger.log(Level.WARNING, this.getName() + ex.getMessage(), ex);
        }
        if (this.isTransferring() || !this.isOnCurrentServer()) {
            return;
        }
        if (this.getTemplateId() == 78 || this.getTemplateId() == 79 || this.getTemplateId() == 80 || this.getTemplateId() == 81 || this.getTemplateId() == 68) {
            EpicServerStatus.avatarCreatureKilled(this.getWurmId());
        }
        this.setDeathEffects(freeDeath, tilex, tiley);
        if (EpicServerStatus.doesTraitorMissionExist(this.getWurmId())) {
            EpicServerStatus.traitorCreatureKilled(this.getWurmId());
        }
    }
    
    private void distributeDragonScaleOrHide(final Set<Player> primeLooters, final Set<Player> leecher, final int lootTemplate) throws NoSuchTemplateException {
        final ItemTemplate itemt = ItemTemplateFactory.getInstance().getTemplate(lootTemplate);
        final float lootNums = this.calculateDragonLootMultiplier();
        final float totalWeightToDistribute = this.calculateDragonLootTotalWeight(itemt, lootNums) * ((lootTemplate == 371) ? 3.0f : 1.0f);
        float leecherShare = 0.0f;
        if (leecher.size() > 0) {
            leecherShare = totalWeightToDistribute / 5.0f;
        }
        float primeShare = totalWeightToDistribute - leecherShare;
        if (leecher.size() > 0) {
            final float lSplit = leecherShare / leecher.size();
            final float pSplit = primeShare / primeLooters.size();
            if (lSplit > pSplit) {
                leecherShare = pSplit * 0.9f * leecher.size();
                primeShare = totalWeightToDistribute - leecherShare;
            }
        }
        this.splitDragonLootTo(primeLooters, itemt, lootTemplate, primeShare);
        this.splitDragonLootTo(leecher, itemt, lootTemplate, leecherShare);
    }
    
    private final float calculateDragonLootTotalWeight(final ItemTemplate template, final float lootMult) {
        return 1.0f + template.getWeightGrams() * lootMult;
    }
    
    private final float calculateDragonLootMultiplier() {
        float lootNums = 1.0f;
        if (!Servers.isThisAnEpicServer()) {
            lootNums = Math.max(1.0f, 1.0f + Server.rand.nextFloat() * 3.0f);
        }
        return lootNums;
    }
    
    private void splitDragonLootTo(final Set<Player> lootReceivers, final ItemTemplate itemt, final int lootTemplate, final float totalWeight) {
        if (lootReceivers.size() == 0) {
            return;
        }
        final float receivers = lootReceivers.size();
        final float weight = totalWeight / receivers;
        for (final Player p : lootReceivers) {
            try {
                double power = 0.0;
                try {
                    final Skill butchering = p.getSkills().getSkill(10059);
                    power = Math.max(0.0, butchering.skillCheck(10.0, 0.0, false, 10.0f));
                }
                catch (NoSuchSkillException nss) {
                    final Skill butchering2 = p.getSkills().learn(10059, 1.0f);
                    power = Math.max(0.0, butchering2.skillCheck(10.0, 0.0, false, 10.0f));
                }
                final Item loot = ItemFactory.createItem(lootTemplate, (float)(80.0 + power / 5.0), "");
                final String creatureName = this.getTemplate().getName().toLowerCase();
                if (!loot.getName().contains(creatureName)) {
                    loot.setName(creatureName.toLowerCase() + " " + itemt.getName());
                }
                loot.setData2(this.template.getTemplateId());
                loot.setWeight((int)weight, true);
                p.getInventory().insertItem(loot);
                lootReceivers.add(p);
            }
            catch (NoSuchTemplateException nst) {
                Creature.logger.log(Level.WARNING, p.getName() + " No template for item id " + lootTemplate);
            }
            catch (FailedException fe) {
                Creature.logger.log(Level.WARNING, p.getName() + " " + fe.getMessage() + ":" + lootTemplate);
            }
        }
    }
    
    public boolean isSuiciding() {
        return false;
    }
    
    public Item[] getAllItems() {
        final Set<Item> allitems = new HashSet<Item>();
        final Item inventory = this.getInventory();
        allitems.add(inventory);
        final Item body = this.getBody().getBodyItem();
        allitems.add(body);
        final Item[] allItems;
        final Item[] boditems = allItems = body.getAllItems(true);
        for (final Item lBoditem : allItems) {
            allitems.add(lBoditem);
        }
        final Item[] allItems2;
        final Item[] invitems = allItems2 = inventory.getAllItems(true);
        for (final Item lInvitem : allItems2) {
            allitems.add(lInvitem);
        }
        return allitems.toArray(new Item[allitems.size()]);
    }
    
    public void checkWorkMusic() {
        if (this.musicPlayer != null) {
            this.musicPlayer.checkMUSIC_VILLAGEWORK_SND();
        }
    }
    
    public boolean isFightingSpiritGuard() {
        return this.opponent != null && this.opponent.isSpiritGuard();
    }
    
    public boolean isFighting(final long opponentid) {
        return this.opponent != null && this.opponent.getWurmId() == opponentid;
    }
    
    public void setFleeCounter(final int newCounter) {
        this.setFleeCounter(newCounter, false);
    }
    
    public void setFleeCounter(final int newCounter, final boolean warded) {
        if (newCounter <= 0 || newCounter < this.fleeCounter) {
            return;
        }
        if (((!this.isPlayer() && !this.isUnique() && (!this.isDominated() || warded)) || this.isPrey()) && (warded || this.isPrey())) {
            this.fleeCounter = (byte)newCounter;
            this.sendToLoggers("updated flee counter: " + this.fleeCounter);
        }
    }
    
    public void setTarget(long targ, final boolean switchTarget) {
        if (targ == this.getWurmId()) {
            targ = -10L;
        }
        if (this.isPrey()) {
            return;
        }
        if (targ != -10L && this.getVehicle() != -10L) {
            try {
                final Creature cret = Server.getInstance().getCreature(this.target);
                if (cret.getHitched() != null) {
                    final Vehicle v = Vehicles.getVehicleForId(this.getVehicle());
                    if (v != null && v == cret.getHitched()) {
                        this.getCommunicator().sendNormalServerMessage("You cannot target " + cret.getName() + " while on the same vehicle.");
                        targ = -10L;
                    }
                }
            }
            catch (NoSuchPlayerException ex) {}
            catch (NoSuchCreatureException ex2) {}
        }
        if (this.loggerCreature1 != -10L) {
            Creature.logger.log(Level.FINE, this.getName() + " target=" + targ, new Exception());
        }
        if (targ == -10L) {
            this.getCommunicator().sendCombatStatus(0.0f, 0.0f, (byte)0);
            if (this.opponent != null && this.opponent.getWurmId() == this.target) {
                this.setOpponent(null);
            }
            if (this.target != targ) {
                try {
                    final Creature cret = Server.getInstance().getCreature(this.target);
                    cret.getCommunicator().changeAttitude(this.getWurmId(), this.getAttitude(cret));
                }
                catch (NoSuchCreatureException ex3) {}
                catch (NoSuchPlayerException ex4) {}
            }
            this.target = targ;
            this.getCommunicator().sendTarget(targ);
            final VolaTile t = Zones.getTileOrNull(this.getTileX(), this.getTileY(), this.isOnSurface());
            if (t != null) {
                t.sendUpdateTarget(this);
            }
            this.status.sendStateString();
        }
        else if ((this.target == -10L || switchTarget) && this.target != targ && (this.getBaseCombatRating() > 10.0f || this.fleeCounter <= 0)) {
            if (this.target != -10L) {
                try {
                    final Creature cret = Server.getInstance().getCreature(this.target);
                    cret.getCommunicator().changeAttitude(this.getWurmId(), this.getAttitude(cret));
                }
                catch (NoSuchCreatureException ex5) {}
                catch (NoSuchPlayerException ex6) {}
            }
            try {
                final Creature cret = Server.getInstance().getCreature(targ);
                if (this.isSpiritGuard() && this.citizenVillage != null) {
                    final VolaTile currTile = cret.getCurrentTile();
                    if (currTile.getTileX() < this.citizenVillage.getStartX() - 5 || currTile.getTileX() > this.citizenVillage.getEndX() + 5 || currTile.getTileY() < this.citizenVillage.getStartY() - 5 || currTile.getTileY() > this.citizenVillage.getEndY() + 5) {
                        if (cret.opponent == this) {
                            cret.setOpponent(null);
                            cret.setTarget(-10L, true);
                            cret.getCommunicator().sendNormalServerMessage("The " + this.getName() + " suddenly becomes hazy and hard to target.");
                        }
                        targ = -10L;
                        this.setOpponent(null);
                        if (this.status.getPath() == null) {
                            this.getMoveTarget(0);
                        }
                    }
                    else {
                        this.citizenVillage.cryForHelp(this, false);
                    }
                }
                if (targ != -10L) {
                    cret.getCommunicator().changeAttitude(this.getWurmId(), this.getAttitude(cret));
                }
            }
            catch (NoSuchCreatureException ex7) {}
            catch (NoSuchPlayerException ex8) {}
            this.target = targ;
            this.getCommunicator().sendTarget(targ);
            final VolaTile t = Zones.getTileOrNull(this.getTileX(), this.getTileY(), this.isOnSurface());
            if (t != null) {
                t.sendUpdateTarget(this);
            }
            this.status.sendStateString();
        }
    }
    
    public boolean modifyFightSkill(final int dtilex, final int dtiley) {
        boolean pvp = false;
        Map<Creature, Double> lSkillReceivers = null;
        boolean activatedTrigger = false;
        if (!this.isNoSkillgain()) {
            lSkillReceivers = new HashMap<Creature, Double>();
            final long now = System.currentTimeMillis();
            double kskill = 0.0;
            double sumskill = 0.0;
            boolean wasHelped = false;
            if (this.attackers != null && this.attackers.size() > 0) {
                final ArrayList<Long> possibleTriggerOwners = new ArrayList<Long>();
                for (final long l : this.attackers.keySet()) {
                    if (now - this.attackers.get(l) < 600000L && WurmId.getType(l) == 0 && (!this.isPlayer() || !Players.getInstance().isOverKilling(l, this.getWurmId()))) {
                        possibleTriggerOwners.add(l);
                    }
                }
                if (!possibleTriggerOwners.isEmpty()) {
                    try {
                        final Player player = Players.getInstance().getPlayer(possibleTriggerOwners.get(Server.rand.nextInt(possibleTriggerOwners.size())));
                        final MissionTrigger[] missionTriggersWith;
                        final MissionTrigger[] trigs = missionTriggersWith = MissionTriggers.getMissionTriggersWith(this.getTemplate().getTemplateId(), 491, this.getWurmId());
                        for (final MissionTrigger t2 : missionTriggersWith) {
                            final EpicMission em = EpicServerStatus.getEpicMissionForMission(t2.getMissionRequired());
                            if (em != null) {
                                final EpicMissionEnum missionEnum = EpicMissionEnum.getMissionForType(em.getMissionType());
                                if (missionEnum != null && EpicMissionEnum.isMissionKarmaGivenOnKill(missionEnum)) {
                                    final float karmaSplit = missionEnum.getKarmaBonusDiffMult() * em.getDifficulty();
                                    float karmaGained = karmaSplit / EpicServerStatus.getNumberRequired(em.getDifficulty(), missionEnum);
                                    karmaGained = (float)Math.ceil(karmaGained / possibleTriggerOwners.size());
                                    for (final long id : possibleTriggerOwners) {
                                        try {
                                            final Player p = Players.getInstance().getPlayer(id);
                                            if (Deities.getFavoredKingdom(em.getEpicEntityId()) != p.getKingdomTemplateId() && Servers.localServer.EPIC) {
                                                continue;
                                            }
                                            MissionPerformer mp = MissionPerformed.getMissionPerformer(id);
                                            if (mp == null) {
                                                mp = MissionPerformed.startNewMission(t2.getMissionRequired(), id, 1.0f);
                                            }
                                            else {
                                                final MissionPerformed mperf = mp.getMission(t2.getMissionRequired());
                                                if (mperf == null) {
                                                    MissionPerformed.startNewMission(t2.getMissionRequired(), id, 1.0f);
                                                }
                                            }
                                            p.modifyKarma((int)karmaGained);
                                            if (!p.isPaying()) {
                                                continue;
                                            }
                                            p.setScenarioKarma((int)(p.getScenarioKarma() + karmaGained));
                                            if (!Servers.localServer.EPIC) {
                                                continue;
                                            }
                                            final WcEpicKarmaCommand wcek = new WcEpicKarmaCommand(WurmId.getNextWCCommandId(), new long[] { p.getWurmId() }, new int[] { p.getScenarioKarma() }, em.getEpicEntityId());
                                            wcek.sendToLoginServer();
                                        }
                                        catch (NoSuchPlayerException ex2) {}
                                    }
                                }
                            }
                        }
                        MissionTriggers.activateTriggers(player, this.getTemplate().getTemplateId(), 491, this.getWurmId(), 1);
                        activatedTrigger = true;
                    }
                    catch (NoSuchPlayerException ex3) {}
                }
                for (final Map.Entry<Long, Long> entry : this.attackers.entrySet()) {
                    final long attackerId = entry.getKey();
                    final long attackTime = entry.getValue();
                    if (now - attackTime < 600000L) {
                        if (WurmId.getType(attackerId) == 0) {
                            pvp = true;
                            if (this.isPlayer()) {
                                if (Players.getInstance().isOverKilling(attackerId, this.getWurmId())) {
                                    continue;
                                }
                            }
                            try {
                                final Player player2 = Players.getInstance().getPlayer(attackerId);
                                if (!this.isDuelOrSpar(player2)) {
                                    kskill = player2.getFightingSkill().getRealKnowledge();
                                    lSkillReceivers.put(player2, new Double(kskill));
                                    sumskill += kskill;
                                }
                                if (!this.isPlayer() && !this.isSpiritGuard() && !this.isKingdomGuard() && player2.isPlayer() && !player2.isDead()) {
                                    player2.checkCoinAward(this.attackers.size() * (this.isBred() ? 20 : (this.isDomestic() ? 50 : 100)));
                                }
                                if (!this.isChampion() || !player2.isPlayer() || (this.getKingdomId() == player2.getKingdomId() && !player2.isEnemyOnChaos(this))) {
                                    continue;
                                }
                                player2.addTitle(Titles.Title.ChampSlayer);
                                if (!player2.isChampion()) {
                                    continue;
                                }
                                player2.modifyChampionPoints(30);
                                Servers.localServer.createChampTwit(player2.getName() + " slays " + this.getName() + " and gains 30 champion points");
                            }
                            catch (NoSuchPlayerException ex4) {}
                        }
                        else {
                            try {
                                final Creature c = Creatures.getInstance().getCreature(attackerId);
                                if (c.isDominated()) {
                                    kskill = c.getFightingSkill().getKnowledge();
                                    lSkillReceivers.put(c, new Double(kskill));
                                    sumskill += kskill;
                                }
                                else {
                                    if ((!c.isSpiritGuard() && !c.isKingdomGuard()) || this.isPlayer()) {
                                        continue;
                                    }
                                    wasHelped = true;
                                }
                            }
                            catch (NoSuchCreatureException ex5) {}
                        }
                    }
                }
            }
            kskill = this.getFightingSkill().getRealKnowledge();
            this.getFightingSkill().touch();
            if (this.isPlayer() && kskill <= 10.0) {
                kskill = 0.0;
            }
            if (!this.isPlayer()) {
                kskill = this.getBaseCombatRating();
                kskill += this.getBonusCombatRating();
                if (kskill > 2.0) {
                    if (!this.isReborn() && !this.isUndead()) {
                        kskill *= 5.0;
                    }
                    else if (this.getTemplate().getTemplateId() == 69) {
                        kskill *= 0.20000000298023224;
                    }
                }
            }
            else {
                this.getFightingSkill().setKnowledge(Math.max(1.0, this.getFightingSkill().getKnowledge() - 0.25), false);
            }
            if (kskill > 0.0) {
                if (!this.isSpiritGuard() && !this.isKingdomGuard() && !this.isWarGuard()) {
                    final HashSet<Creature> lootReceivers = new HashSet<Creature>();
                    for (final Map.Entry<Creature, Double> entry2 : lSkillReceivers.entrySet()) {
                        final Creature p2 = entry2.getKey();
                        final Double psk = entry2.getValue();
                        final double pskill = psk;
                        final double percentSkillGained = pskill / sumskill;
                        final double diff = kskill - pskill;
                        double lMod = 0.20000000298023224;
                        if (diff > 1.0) {
                            lMod = Math.sqrt(diff);
                        }
                        else if (diff < -1.0) {
                            lMod = kskill / pskill;
                        }
                        if (!this.isPlayer()) {
                            lMod /= (Servers.localServer.isChallengeServer() ? 2.0 : 7.0);
                            if (pskill > 70.0) {
                                final double tomax = 100.0 - pskill;
                                final double modifier = tomax / (Servers.localServer.isChallengeServer() ? 30.0f : 500.0f);
                                lMod *= modifier;
                            }
                            if (wasHelped) {
                                lMod *= 0.10000000149011612;
                            }
                        }
                        else if (pskill > 50.0 && kskill < 20.0) {
                            lMod = 0.0;
                        }
                        else if (this.getKingdomId() == p2.getKingdomId()) {
                            lMod = 0.0;
                        }
                        if (kskill <= 0.0) {
                            lMod = 0.0;
                        }
                        final double skillGained = percentSkillGained * lMod * 0.25 * ItemBonus.getKillEfficiencyBonus(p2);
                        if (skillGained > 0.0) {
                            p2.getFightingSkill().touch();
                            if (p2.isPaying() || pskill < 20.0) {
                                if (pskill + skillGained > 100.0) {
                                    p2.getFightingSkill().setKnowledge(pskill + (100.0 - pskill) / 100.0, false);
                                }
                                else {
                                    p2.getFightingSkill().setKnowledge(pskill + skillGained, false);
                                }
                                p2.getFightingSkill().maybeSetMinimum();
                            }
                            p2.getFightingSkill().checkInitialTitle();
                        }
                        if (!this.isPlayer()) {
                            if (p2.isPlayer()) {
                                p2.achievement(522);
                                if (p2.isUndead()) {
                                    final PlayerInfo saveFile = ((Player)p2).getSaveFile();
                                    ++saveFile.undeadKills;
                                    ((Player)p2).getSaveFile().setUndeadData();
                                    p2.achievement(335);
                                }
                                if (this.isUnique()) {
                                    HistoryManager.addHistory(p2.getName(), "slayed " + this.getName());
                                }
                                final int tid = this.getTemplate().getTemplateId();
                                try {
                                    if (CreatureTemplate.isDragon(tid)) {
                                        ((Player)p2).addTitle(Titles.Title.DragonSlayer);
                                    }
                                    else if (tid == 11 || tid == 27) {
                                        ((Player)p2).addTitle(Titles.Title.TrollSlayer);
                                    }
                                    else if (tid == 20 || tid == 22) {
                                        ((Player)p2).addTitle(Titles.Title.GiantSlayer);
                                    }
                                    else if (this.isUnique()) {
                                        ((Player)p2).addTitle(Titles.Title.UniqueSlayer);
                                    }
                                }
                                catch (Exception ex) {
                                    Creature.logger.log(Level.WARNING, this.getName() + " and " + p2.getName() + ":" + ex.getMessage());
                                }
                                switch (this.status.modtype) {
                                    case 1: {
                                        p2.achievement(253);
                                        break;
                                    }
                                    case 2: {
                                        p2.achievement(254);
                                        break;
                                    }
                                    case 3: {
                                        p2.achievement(255);
                                        break;
                                    }
                                    case 4: {
                                        p2.achievement(256);
                                        break;
                                    }
                                    case 5: {
                                        p2.achievement(257);
                                        break;
                                    }
                                    case 6: {
                                        p2.achievement(258);
                                        break;
                                    }
                                    case 7: {
                                        p2.achievement(259);
                                        break;
                                    }
                                    case 8: {
                                        p2.achievement(260);
                                        break;
                                    }
                                    case 9: {
                                        p2.achievement(261);
                                        break;
                                    }
                                    case 10: {
                                        p2.achievement(262);
                                        break;
                                    }
                                    case 11: {
                                        p2.achievement(263);
                                        break;
                                    }
                                    case 99: {
                                        p2.achievement(264);
                                        break;
                                    }
                                }
                                if (tid == 58) {
                                    p2.achievement(225);
                                }
                                else if (tid == 21 || tid == 118) {
                                    p2.achievement(228);
                                }
                                else if (tid == 25) {
                                    p2.achievement(231);
                                }
                                else if (tid == 11) {
                                    p2.achievement(235);
                                }
                                else if (tid == 10) {
                                    p2.achievement(237);
                                }
                                else if (tid == 54) {
                                    p2.achievement(239);
                                }
                                else if (tid == 56) {
                                    p2.achievement(243);
                                }
                                else if (tid == 57) {
                                    p2.achievement(244);
                                }
                                else if (tid == 55) {
                                    p2.achievement(265);
                                }
                                else if (tid == 43) {
                                    p2.achievement(268);
                                }
                                else if (tid == 42 || tid == 12) {
                                    p2.achievement(269);
                                }
                                else if (CreatureTemplate.isFullyGrownDragon(tid)) {
                                    p2.achievement(270);
                                }
                                else if (CreatureTemplate.isDragonHatchling(tid)) {
                                    p2.achievement(271);
                                }
                                else if (tid == 20) {
                                    p2.achievement(272);
                                }
                                else if (tid == 23) {
                                    p2.achievement(273);
                                }
                                else if (tid == 27) {
                                    p2.achievement(274);
                                }
                                else if (tid == 68) {
                                    p2.achievement(276);
                                }
                                else if (tid == 70) {
                                    p2.achievement(277);
                                }
                                else if (tid == 71) {
                                    p2.achievement(278);
                                }
                                else if (tid == 72) {
                                    p2.achievement(279);
                                }
                                else if (tid == 73) {
                                    p2.achievement(280);
                                }
                                else if (tid == 74) {
                                    p2.achievement(281);
                                }
                                else if (tid == 75) {
                                    p2.achievement(282);
                                }
                                else if (tid == 76) {
                                    p2.achievement(283);
                                }
                                else if (tid == 77) {
                                    p2.achievement(284);
                                }
                                else if (tid == 78) {
                                    p2.achievement(285);
                                }
                                else if (tid == 79) {
                                    p2.achievement(286);
                                }
                                else if (tid == 80) {
                                    p2.achievement(287);
                                }
                                else if (tid == 81) {
                                    p2.achievement(288);
                                }
                                else if (tid == 82) {
                                    p2.achievement(289);
                                }
                                else if (tid == 83 || tid == 117) {
                                    p2.achievement(291);
                                }
                                else if (tid == 84) {
                                    p2.achievement(290);
                                }
                                else if (tid == 85) {
                                    p2.achievement(292);
                                }
                                else if (tid == 59) {
                                    p2.achievement(313);
                                }
                                else if (tid == 15) {
                                    p2.achievement(314);
                                }
                                else if (tid == 14) {
                                    p2.achievement(315);
                                }
                                else if (tid == 13) {
                                    p2.achievement(316);
                                }
                                else if (tid == 22) {
                                    p2.achievement(307);
                                }
                                else if (tid == 26) {
                                    p2.achievement(308);
                                }
                                else if (tid == 64 || tid == 65) {
                                    p2.achievement(309);
                                }
                                else if (tid == 49 || tid == 3 || tid == 50) {
                                    p2.achievement(310);
                                }
                                else if (tid == 44) {
                                    p2.achievement(311);
                                }
                                else if (tid == 51) {
                                    p2.achievement(312);
                                }
                                else if (tid == 106) {
                                    p2.achievement(378);
                                }
                                else if (tid == 107) {
                                    p2.achievement(379);
                                }
                                else if (tid == 108) {
                                    p2.achievement(380);
                                }
                                else if (tid == 109) {
                                    p2.achievement(381);
                                }
                                if (this.isDefendKingdom() && !this.isFriendlyKingdom(p2.getKingdomId())) {
                                    p2.achievement(275);
                                }
                                if (this.isReborn()) {
                                    p2.achievement(248);
                                }
                                if (this.isUnique()) {
                                    p2.achievement(589);
                                }
                            }
                        }
                        else if (this.isKing() && p2.isPlayer() && p2.getKingdomId() != this.getKingdomId()) {
                            ((Player)p2).addTitle(Titles.Title.Kingslayer);
                            HistoryManager.addHistory(p2.getName(), "slayed " + this.getName());
                        }
                        if (this.isPlayer() && p2.isPlayer() && !this.isUndead()) {
                            if (p2.isUndead()) {
                                final PlayerInfo saveFile2 = ((Player)p2).getSaveFile();
                                ++saveFile2.undeadPlayerKills;
                                ((Player)p2).getSaveFile().setUndeadData();
                                p2.achievement(339);
                            }
                            Creature.logger.log(Level.INFO, p2.getName() + " killed " + this.getName() + " as champ=" + p2.isChampion() + ". Diff=" + diff + " mod=" + lMod + " skillGained=" + skillGained + " pskill=" + pskill + " kskill=" + kskill);
                            if (skillGained > 0.0) {
                                p2.achievement(8);
                                final Item weapon = p2.getPrimWeapon();
                                if (weapon != null) {
                                    if (weapon.isWeaponBow()) {
                                        p2.achievement(11);
                                    }
                                    else if (weapon.isWeaponSword()) {
                                        p2.achievement(14);
                                    }
                                    else if (weapon.isWeaponCrush()) {
                                        p2.achievement(17);
                                    }
                                    else if (weapon.isWeaponAxe()) {
                                        p2.achievement(20);
                                    }
                                    else if (weapon.isWeaponKnife()) {
                                        p2.achievement(25);
                                    }
                                    if (weapon.getTemplateId() == 314) {
                                        p2.achievement(27);
                                    }
                                    else if (weapon.getTemplateId() == 567) {
                                        p2.achievement(29);
                                    }
                                    else if (weapon.getTemplateId() == 20) {
                                        p2.achievement(30);
                                    }
                                }
                                final Item[] bodyItems = p2.getBody().getAllItems();
                                int clothArmourFound = 0;
                                int dragonPiecesFound = 0;
                                for (final Item i : bodyItems) {
                                    if (i.isArmour()) {
                                        if (i.isCloth()) {
                                            ++clothArmourFound;
                                        }
                                        else if (i.isDragonArmour() && (i.getTemplateId() == 476 || i.getTemplateId() == 475)) {
                                            ++dragonPiecesFound;
                                        }
                                    }
                                }
                                if (clothArmourFound >= 8) {
                                    p2.achievement(31);
                                }
                                if (dragonPiecesFound >= 2) {
                                    p2.achievement(32);
                                }
                                if (Creature.pantLess.contains(this.getWurmId())) {
                                    this.achievement(33);
                                }
                            }
                        }
                        if (this.isPlayer() && kskill > 40.0 && lMod > 0.0 && p2.isChampion()) {
                            final PlayerKills pk = Players.getInstance().getPlayerKillsFor(p2.getWurmId());
                            if (System.currentTimeMillis() - pk.getLastKill(this.getWurmId()) <= 86400000L || pk.getNumKills(this.getWurmId()) >= 10L) {
                                continue;
                            }
                            p2.modifyChampionPoints(1);
                            Servers.localServer.createChampTwit(p2.getName() + " slays " + this.getName() + " and gains 1 champion point because of difficulty");
                        }
                    }
                    this.getTemplate().getLootTable().ifPresent(t -> t.awardAll(this, lootReceivers));
                }
                else {
                    for (final Map.Entry<Creature, Double> entry3 : lSkillReceivers.entrySet()) {
                        final Creature p3 = entry3.getKey();
                        if (p3.isPlayer() && !this.isFriendlyKingdom(p3.getKingdomId()) && this.isSpiritGuard()) {
                            p3.achievement(267);
                        }
                    }
                }
            }
            else {
                for (final Map.Entry<Creature, Double> entry3 : lSkillReceivers.entrySet()) {
                    final Creature p3 = entry3.getKey();
                    if (p3.isPlayer() && !this.isFriendlyKingdom(p3.getKingdomId()) && this.isSpiritGuard()) {
                        p3.achievement(267);
                    }
                }
            }
        }
        else if (!this.isUndead() && this.attackers != null && this.attackers.size() > 0) {
            final ArrayList<Long> possibleTriggerOwners2 = new ArrayList<Long>();
            for (final long j : this.attackers.keySet()) {
                if (WurmId.getType(j) == 0 && (!this.isPlayer() || !Players.getInstance().isOverKilling(j, this.getWurmId()))) {
                    possibleTriggerOwners2.add(j);
                }
            }
            if (!possibleTriggerOwners2.isEmpty()) {
                try {
                    final Player player3 = Players.getInstance().getPlayer(possibleTriggerOwners2.get(Server.rand.nextInt(possibleTriggerOwners2.size())));
                    MissionTriggers.activateTriggers(player3, this.getTemplate().getTemplateId(), 491, this.getWurmId(), 1);
                    final MissionTrigger[] missionTriggersWith2;
                    final MissionTrigger[] trigs2 = missionTriggersWith2 = MissionTriggers.getMissionTriggersWith(this.getTemplate().getTemplateId(), 491, this.getWurmId());
                    for (final MissionTrigger t3 : missionTriggersWith2) {
                        final EpicMission em2 = EpicServerStatus.getEpicMissionForMission(t3.getMissionRequired());
                        if (em2 != null) {
                            final EpicMissionEnum missionEnum2 = EpicMissionEnum.getMissionForType(em2.getMissionType());
                            if (missionEnum2 != null && EpicMissionEnum.isMissionKarmaGivenOnKill(missionEnum2)) {
                                final float karmaSplit2 = missionEnum2.getKarmaBonusDiffMult() * em2.getDifficulty();
                                float karmaGained2 = karmaSplit2 / EpicServerStatus.getNumberRequired(em2.getDifficulty(), missionEnum2);
                                karmaGained2 = (float)Math.ceil(karmaGained2 / possibleTriggerOwners2.size());
                                for (final long id2 : possibleTriggerOwners2) {
                                    try {
                                        final Player p4 = Players.getInstance().getPlayer(id2);
                                        if (Deities.getFavoredKingdom(em2.getEpicEntityId()) != p4.getKingdomTemplateId() && Servers.localServer.EPIC) {
                                            continue;
                                        }
                                        MissionPerformer mp2 = MissionPerformed.getMissionPerformer(id2);
                                        if (mp2 == null) {
                                            mp2 = MissionPerformed.startNewMission(t3.getMissionRequired(), id2, 1.0f);
                                        }
                                        else {
                                            final MissionPerformed mperf2 = mp2.getMission(t3.getMissionRequired());
                                            if (mperf2 == null) {
                                                MissionPerformed.startNewMission(t3.getMissionRequired(), id2, 1.0f);
                                            }
                                        }
                                        p4.modifyKarma((int)karmaGained2);
                                        if (!p4.isPaying()) {
                                            continue;
                                        }
                                        p4.setScenarioKarma((int)(p4.getScenarioKarma() + karmaGained2));
                                        if (!Servers.localServer.EPIC) {
                                            continue;
                                        }
                                        final WcEpicKarmaCommand wcek2 = new WcEpicKarmaCommand(WurmId.getNextWCCommandId(), new long[] { p4.getWurmId() }, new int[] { p4.getScenarioKarma() }, em2.getEpicEntityId());
                                        wcek2.sendToLoginServer();
                                    }
                                    catch (NoSuchPlayerException ex6) {}
                                }
                            }
                        }
                    }
                    activatedTrigger = true;
                }
                catch (NoSuchPlayerException ex7) {}
            }
            for (final Map.Entry<Long, Long> entry4 : this.attackers.entrySet()) {
                final long attackerId2 = entry4.getKey();
                if (WurmId.getType(attackerId2) == 0) {
                    pvp = true;
                    try {
                        final Player player4 = Players.getInstance().getPlayer(attackerId2);
                        if (this.isFriendlyKingdom(player4.getKingdomId()) || !this.isKingdomGuard()) {
                            continue;
                        }
                        player4.achievement(266);
                    }
                    catch (NoSuchPlayerException ex8) {}
                }
            }
        }
        Creature.pantLess.remove(this.getWurmId());
        return pvp || (lSkillReceivers != null && lSkillReceivers.size() > 0);
    }
    
    @Nullable
    public Creature getTarget() {
        Creature toReturn = null;
        if (this.target != -10L) {
            try {
                toReturn = Server.getInstance().getCreature(this.target);
            }
            catch (NoSuchCreatureException nsc) {
                this.setTarget(-10L, true);
            }
            catch (NoSuchPlayerException nsp) {
                this.setTarget(-10L, true);
            }
        }
        return toReturn;
    }
    
    public void setDeathEffects(final boolean freeDeath, final int dtilex, final int dtiley) {
        boolean respawn = false;
        this.removeWoundMod();
        this.modifyFightSkill(dtilex, dtiley);
        if (this.isSpiritGuard() && this.citizenVillage != null) {
            respawn = true;
        }
        else if (this.isKingdomGuard() || (this.isNpc() && this.isRespawn())) {
            respawn = true;
        }
        if (respawn) {
            this.setDestroyed();
            if (this.name.endsWith("traitor")) {
                try {
                    this.setName(this.getNameWithoutPrefixes());
                }
                catch (Exception ex) {
                    Creature.logger.log(Level.WARNING, this.getName() + ", " + this.getWurmId() + ": failed to remove traitor name.");
                }
            }
            try {
                this.status.setDead(true);
            }
            catch (IOException ioex) {
                Creature.logger.log(Level.WARNING, this.getName() + ", " + this.getWurmId() + ": Set dead manually.");
            }
            if (this.isSpiritGuard()) {
                final Village vil = this.citizenVillage;
                if (vil != null) {
                    vil.deleteGuard(this, false);
                    vil.plan.returnGuard(this);
                }
                else {
                    this.destroy();
                }
            }
            else if (this.isKingdomGuard()) {
                final GuardTower tower = Kingdoms.getTower(this);
                if (tower != null) {
                    try {
                        tower.returnGuard(this);
                    }
                    catch (IOException iox) {
                        Creature.logger.log(Level.WARNING, iox.getMessage(), iox);
                    }
                }
                else {
                    Creature.logger.log(Level.INFO, this.getName() + ", " + this.getWurmId() + " without tower, destroying.");
                    this.destroy();
                }
            }
            else {
                this.respawnCounter = 600;
            }
        }
        else {
            this.destroy();
        }
        this.getStatus().setStunned(0.0f, false);
        this.trimAttackers(true);
    }
    
    public void respawn() {
        if (this.getVisionArea() == null) {
            try {
                if (!this.isNpc()) {
                    if (this.skills.getSkill(10052).getKnowledge(0.0) > this.template.getSkills().getSkill(10052).getKnowledge(0.0) * 2.0 || 100.0 - this.skills.getSkill(10052).getKnowledge(0.0) < 30.0 || this.skills.getSkill(10052).getKnowledge(0.0) < this.template.getSkills().getSkill(10052).getKnowledge(0.0) / 2.0) {
                        this.skills.delete();
                        this.skills.clone(this.template.getSkills().getSkills());
                        this.skills.save();
                        this.getStatus().age = 0;
                    }
                    else if (this.getStatus().age >= this.template.getMaxAge() - 1) {
                        this.getStatus().age = 0;
                    }
                }
                this.status.setDead(false);
                this.pollCounter = 0;
                this.lastPolled = 0;
                this.setDisease((byte)0);
                this.getStatus().removeWounds();
                this.getStatus().modifyStamina(65535.0f);
                this.getStatus().refresh(0.5f, false);
                this.createVisionArea();
            }
            catch (Exception ex) {
                Creature.logger.log(Level.WARNING, this.getName() + ":" + ex.getMessage(), ex);
            }
        }
        else {
            Creature.logger.log(Level.WARNING, this.getName() + " already has a visionarea.", new Exception());
        }
        Server.getInstance().broadCastAction(this.getNameWithGenus() + " has arrived.", this, 10);
    }
    
    public boolean hasColoredChat() {
        return false;
    }
    
    public int getCustomGreenChat() {
        return 140;
    }
    
    public int getCustomRedChat() {
        return 255;
    }
    
    public int getCustomBlueChat() {
        return 0;
    }
    
    public final boolean isFaithful() {
        return this.faithful;
    }
    
    public boolean isFighting() {
        return this.opponent != null;
    }
    
    public MovementScheme getMovementScheme() {
        return this.movementScheme;
    }
    
    public boolean isOnGround() {
        return this.movementScheme.onGround;
    }
    
    public void pollStamina() {
        final int n = 0;
        final int staminaPollCounter = this.staminaPollCounter - 1;
        this.staminaPollCounter = staminaPollCounter;
        this.staminaPollCounter = Math.max(n, staminaPollCounter);
        if (this.staminaPollCounter == 0) {
            if (!this.isUndead() && WurmId.getType(this.id) == 0) {
                int hungMod = 4;
                int thirstMod = (int)(5.0f * ItemBonus.getReplenishBonus(this));
                if (this.getSpellEffects() != null && this.getSpellEffects().getSpellEffect((byte)74) != null) {
                    hungMod = 2;
                    thirstMod = 2;
                }
                hungMod *= (int)ItemBonus.getReplenishBonus(this);
                boolean reduceHunger = true;
                if (this.getDeity() != null && this.getDeity().number == 4 && this.isOnSurface()) {
                    final int tile = Server.surfaceMesh.getTile(this.getTileX(), this.getTileY());
                    if (Tiles.getTile(Tiles.decodeType(tile)).isMycelium()) {
                        reduceHunger = false;
                    }
                }
                int hunger;
                if (reduceHunger) {
                    this.status.decreaseCCFPValues();
                    hunger = this.status.modifyHunger((int)(hungMod * (2.0f - this.status.getNutritionlevel())), 1.0f);
                }
                else {
                    hunger = this.status.modifyHunger(-4, 0.99f);
                }
                final int thirst = this.status.modifyThirst(thirstMod);
                float hungpercent = 1.0f;
                if (hunger > 45000) {
                    hungpercent = Math.max(1.0f, 65535 - hunger) / 20535.0f;
                    hungpercent *= hungpercent;
                }
                float thirstpercent = Math.max(65535 - thirst, 1.0f) / 65535.0f;
                thirstpercent *= thirstpercent * thirstpercent;
                if (this.status.hasNormalRegen() && !this.isFighting()) {
                    float toModify = 0.6f;
                    if (this.isStealth()) {
                        toModify = 0.06f;
                    }
                    toModify = toModify * hungpercent * thirstpercent;
                    double staminaModifier = this.status.getModifierValuesFor(1);
                    if (this.getDeity() != null && this.getDeity().isStaminaBonus() && this.getFaith() >= 20.0f && this.getFavor() >= 10.0f) {
                        staminaModifier += 0.25;
                    }
                    if (this.hasSpiritStamina) {
                        staminaModifier *= 1.1;
                    }
                    if (this.hasSleepBonus()) {
                        toModify = Math.max(0.006f, toModify * (float)(1.0 + staminaModifier) * 3.0f);
                    }
                    else {
                        toModify = Math.max(0.004f, toModify * (float)(1.0 + staminaModifier));
                    }
                    if (this.hasSpellEffect((byte)95)) {
                        toModify *= 0.5f;
                    }
                    if ((this.getPower() == 0 && this.getVehicle() == -10L && this.getPositionZ() + this.getAltOffZ() < -1.45) || this.isUsingLastGasp()) {
                        toModify = 0.0f;
                    }
                    else {
                        this.status.modifyStamina2(toModify);
                    }
                }
                this.status.setNormalRegen(true);
            }
            else {
                if (this.isNeedFood()) {
                    if (Server.rand.nextInt(600) == 0) {
                        if (this.hasTrait(14) || this.isPregnant()) {
                            this.status.modifyHunger(1500, 1.0f);
                        }
                        else if (!this.isCarnivore()) {
                            this.status.modifyHunger(700, 1.0f);
                        }
                        else {
                            this.status.modifyHunger(150, 1.0f);
                        }
                    }
                }
                else {
                    this.status.modifyHunger(-1, 0.5f);
                }
                if ((this.isRegenerating() || this.isUnique()) && Server.rand.nextInt(10) == 0) {
                    this.healTick();
                }
                if (Server.rand.nextInt(100) == 0) {
                    if (!this.isFighting() || this.isUnique()) {
                        this.status.resetCreatureStamina();
                    }
                    if (!this.isSwimming() && !this.isUnique() && !this.isSubmerged() && this.getPositionZ() + this.getAltOffZ() <= -1.25 && this.getVehicle() == -10L && this.hitchedTo == null && !this.isRidden() && this.getLeader() == null && !Tiles.isSolidCave(Tiles.decodeType(this.getCurrentTileNum()))) {
                        this.addWoundOfType(null, (byte)7, 2, false, 1.0f, false, 4000.0f + Server.rand.nextFloat() * 3000.0f, 0.0f, 0.0f, false, false);
                    }
                }
                this.status.setNormalRegen(true);
            }
        }
    }
    
    public void sendDeityEffectBonuses() {
    }
    
    public void sendRemoveDeityEffectBonus(final int effectNumber) {
    }
    
    public void sendAddDeityEffectBonus(final int effectNumber) {
    }
    
    public final boolean checkPregnancy(final boolean insta) {
        final Offspring offspring = Offspring.getOffspring(this.getWurmId());
        if (offspring != null && (!offspring.isChecked() || insta) && (Server.rand.nextInt(4) == 0 || insta)) {
            float creatureRatio = 10.0f;
            if (this.getCurrentVillage() != null) {
                creatureRatio = this.getCurrentVillage().getCreatureRatio();
            }
            if (((this.status.hunger > 60000 && this.status.fat <= 2) || (creatureRatio < Village.OPTIMUMCRETRATIO && Server.rand.nextInt(Math.max((int)(creatureRatio / 2.0f), 1)) == 0)) && Server.rand.nextInt(3) == 0) {
                Offspring.deleteSettings(this.getWurmId());
                this.getCommunicator().sendAlertServerMessage("You suddenly bleed immensely and lose your unborn child due to malnourishment!");
                Server.getInstance().broadCastAction(this.getNameWithGenus() + " bleeds immensely due to miscarriage.", this, 5);
                if (Server.rand.nextInt(5) == 0) {
                    this.die(false, "Miscarriage");
                }
                return false;
            }
            if (offspring.decreaseDaysLeft()) {
                try {
                    try {
                        int cid = this.template.getChildTemplateId();
                        if (cid <= 0) {
                            cid = this.template.getTemplateId();
                        }
                        final CreatureTemplate temp = CreatureTemplateFactory.getInstance().getTemplate(cid);
                        String newname = temp.getName();
                        final byte sex = temp.keepSex ? temp.getSex() : ((byte)Server.rand.nextInt(2));
                        if (this.isHorse()) {
                            if (Server.rand.nextBoolean()) {
                                newname = Offspring.generateGenericName();
                            }
                            else if (sex == 1) {
                                newname = Offspring.generateFemaleName();
                            }
                            else {
                                newname = Offspring.generateMaleName();
                            }
                            newname = LoginHandler.raiseFirstLetter(newname);
                        }
                        if (this.isUnicorn()) {
                            if (Server.rand.nextBoolean()) {
                                newname = Offspring.generateGenericName();
                            }
                            else if (sex == 1) {
                                newname = Offspring.generateFemaleUnicornName();
                            }
                            else {
                                newname = Offspring.generateMaleUnicornName();
                            }
                            newname = LoginHandler.raiseFirstLetter(newname);
                        }
                        boolean zombie = false;
                        if (cid == 66) {
                            zombie = true;
                            if (sex == 1) {
                                newname = LoginHandler.raiseFirstLetter("Daughter of " + this.name);
                            }
                            else {
                                newname = LoginHandler.raiseFirstLetter("Son of " + this.name);
                            }
                            if (this.getKingdomTemplateId() != 3) {
                                cid = 25;
                                zombie = false;
                            }
                        }
                        final Creature newCreature = doNew(cid, true, this.getPosX(), this.getPosY(), Server.rand.nextFloat() * 360.0f, this.getLayer(), newname, sex, (byte)(this.isAggHuman() ? this.getKingdomId() : 0), (byte)(Server.rand.nextBoolean() ? this.getStatus().modtype : 0), zombie, (byte)1);
                        this.getCommunicator().sendAlertServerMessage("You give birth to " + newCreature.getName() + "!");
                        newCreature.getStatus().setTraitBits(offspring.getTraits());
                        newCreature.getStatus().setInheritance(offspring.getTraits(), offspring.getMother(), offspring.getFather());
                        newCreature.getStatus().saveCreatureName(newname);
                        if (zombie) {
                            if (this.getPet() != null) {
                                this.getCommunicator().sendNormalServerMessage(this.getPet().getNameWithGenus() + " stops following you.");
                                if (this.getPet().getLeader() == this) {
                                    this.getPet().setLeader(null);
                                }
                                this.getPet().setDominator(-10L);
                                this.setPet(-10L);
                            }
                            newCreature.setDominator(this.getWurmId());
                            newCreature.setLoyalty(100.0f);
                            this.setPet(newCreature.getWurmId());
                            newCreature.getSkills().delete();
                            newCreature.getSkills().clone(this.skills.getSkills());
                            final Skill[] skills;
                            final Skill[] cskills = skills = newCreature.getSkills().getSkills();
                            for (final Skill lCskill : skills) {
                                lCskill.setKnowledge(Math.min(40.0, lCskill.getKnowledge() * 0.5), false);
                            }
                            newCreature.getSkills().save();
                        }
                        newCreature.refreshVisible();
                        Server.getInstance().broadCastAction(this.getNameWithGenus() + " gives birth to " + newCreature.getNameWithGenus() + "!", this, 5);
                        return true;
                    }
                    catch (NoSuchCreatureTemplateException nst) {
                        Creature.logger.log(Level.WARNING, this.getName() + " gives birth to nonexistant template:" + this.template.getChildTemplateId());
                    }
                }
                catch (Exception ex) {
                    Creature.logger.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
        }
        return false;
    }
    
    private long getTraits() {
        return this.status.traits;
    }
    
    public void mate(final Creature father, @Nullable final Creature breeder) {
        boolean inbred = false;
        if (father.getFather() == this.getFather() || father.getMother() == this.getMother() || father.getWurmId() == this.getFather() || father.getMother() == this.getWurmId()) {
            inbred = true;
        }
        new Offspring(this.getWurmId(), father.getWurmId(), (breeder == null) ? Traits.calcNewTraits(inbred, this.getTraits(), father.getTraits()) : Traits.calcNewTraits(breeder.getAnimalHusbandrySkillValue(), inbred, this.getTraits(), father.getTraits()), (byte)(this.template.daysOfPregnancy + Server.rand.nextInt(5)), false);
        Creature.logger.log(Level.INFO, this.getName() + " gender=" + this.getSex() + " just got pregnant with " + father.getName() + " gender=" + father.getSex() + ".");
    }
    
    public boolean isBred() {
        return this.hasTrait(63);
    }
    
    static boolean isInbred(final Creature maleCreature, final Creature femaleCreature) {
        return maleCreature.getFather() == femaleCreature.getFather() || maleCreature.getMother() == femaleCreature.getMother() || maleCreature.getWurmId() == femaleCreature.getFather() || maleCreature.getMother() == femaleCreature.getWurmId();
    }
    
    public boolean isPregnant() {
        return this.getOffspring() != null;
    }
    
    public Offspring getOffspring() {
        return Offspring.getOffspring(this.getWurmId());
    }
    
    private void healTick() {
        if (this.status.damage > 0) {
            try {
                final Wound[] w = this.getBody().getWounds().getWounds();
                if (w.length > 0) {
                    w[0].modifySeverity(-300);
                }
            }
            catch (Exception ex) {
                Creature.logger.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }
    
    public void wearItems() {
        final Item inventory = this.getInventory();
        final Body body = this.getBody();
        final Set<Item> invitems = inventory.getItems();
        final Item[] array;
        final Item[] invarr = array = invitems.toArray(new Item[invitems.size()]);
        for (final Item lElement : array) {
            Label_0550: {
                Label_0316: {
                    if (lElement.isWeapon()) {
                        if (this.isPlayer()) {
                            if (lElement.getTemplateId() == 7 || lElement.isWeaponKnife()) {
                                break Label_0316;
                            }
                        }
                        try {
                            final byte rslot = (byte)(this.isPlayer() ? 38 : 14);
                            Item bodyPart = body.getBodyPart(rslot);
                            if (bodyPart.testInsertItem(lElement)) {
                                final Item parent = lElement.getParent();
                                parent.dropItem(lElement.getWurmId(), false);
                                bodyPart.insertItem(lElement);
                            }
                            else {
                                final byte lslot = (byte)(this.isPlayer() ? 37 : 13);
                                bodyPart = body.getBodyPart(lslot);
                                if (bodyPart.testInsertItem(lElement)) {
                                    final Item parent2 = lElement.getParent();
                                    parent2.dropItem(lElement.getWurmId(), false);
                                    bodyPart.insertItem(lElement);
                                }
                            }
                        }
                        catch (NoSuchItemException nsi) {
                            Creature.logger.log(Level.WARNING, this.getName() + " " + nsi.getMessage(), nsi);
                        }
                        catch (NoSpaceException nsp) {
                            Creature.logger.log(Level.WARNING, this.getName() + " " + nsp.getMessage(), nsp);
                        }
                        break Label_0550;
                    }
                }
                if (lElement.isShield()) {
                    try {
                        final Item bodyPart2 = body.getBodyPart(44);
                        bodyPart2.insertItem(lElement);
                    }
                    catch (NoSpaceException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    final byte[] bodySpaces;
                    final byte[] places = bodySpaces = lElement.getBodySpaces();
                    for (final byte lPlace : bodySpaces) {
                        try {
                            final Item bodyPart3 = body.getBodyPart(lPlace);
                            if (bodyPart3.testInsertItem(lElement)) {
                                final Item parent3 = lElement.getParent();
                                parent3.dropItem(lElement.getWurmId(), false);
                                bodyPart3.insertItem(lElement);
                                break;
                            }
                        }
                        catch (NoSpaceException nsp2) {
                            if (!Servers.localServer.testServer && lPlace != 28) {
                                Creature.logger.log(Level.WARNING, this.getName() + ":" + nsp2.getMessage(), nsp2);
                            }
                        }
                        catch (NoSuchItemException nsi2) {
                            Creature.logger.log(Level.WARNING, this.getName() + ":" + nsi2.getMessage(), nsi2);
                        }
                    }
                }
            }
        }
    }
    
    public float getStaminaMod() {
        final int hunger = this.status.getHunger();
        final int thirst = this.status.getThirst();
        float newhungpercent = 1.0f;
        if (hunger > 45000) {
            newhungpercent = Math.max(1.0f, 65535 - hunger) / 20535.0f;
            newhungpercent *= newhungpercent;
        }
        float thirstpercent = Math.max(65535 - thirst, 1.0f) / 65535.0f;
        thirstpercent *= thirstpercent * thirstpercent;
        return 1.0f - newhungpercent * thirstpercent;
    }
    
    public Skills getSkills() {
        return this.skills;
    }
    
    public double getSoulStrengthVal() {
        return this.getSoulStrength().getKnowledge(0.0);
    }
    
    public Skill getClimbingSkill() {
        try {
            return this.skills.getSkill(10073);
        }
        catch (NoSuchSkillException nss) {
            return this.skills.learn(10073, 1.0f);
        }
    }
    
    public double getLockPickingSkillVal() {
        try {
            return this.skills.getSkill(10076).getKnowledge(0.0);
        }
        catch (NoSuchSkillException nss) {
            return 1.0;
        }
    }
    
    public double getLockSmithingSkill() {
        try {
            return this.skills.getSkill(10034).getKnowledge(0.0);
        }
        catch (NoSuchSkillException nss) {
            return 1.0;
        }
    }
    
    public double getStrengthSkill() {
        try {
            if (this.isPlayer()) {
                return this.skills.getSkill(102).getKnowledge(0.0);
            }
            return this.skills.getSkill(102).getKnowledge();
        }
        catch (NoSuchSkillException nss) {
            return 1.0;
        }
    }
    
    public Skill getStealSkill() {
        try {
            return this.skills.getSkill(10075);
        }
        catch (NoSuchSkillException nss) {
            return this.skills.learn(10075, 1.0f);
        }
    }
    
    public Skill getStaminaSkill() {
        try {
            return this.skills.getSkill(103);
        }
        catch (NoSuchSkillException nss) {
            return this.skills.learn(103, 1.0f);
        }
    }
    
    public final double getAnimalHusbandrySkillValue() {
        try {
            return this.skills.getSkill(10085).getKnowledge(0.0);
        }
        catch (NoSuchSkillException nss) {
            return this.skills.learn(10085, 1.0f).getKnowledge(0.0);
        }
    }
    
    public double getBodyControl() {
        try {
            return this.skills.getSkill(104).getKnowledge(0.0);
        }
        catch (NoSuchSkillException nss) {
            return this.skills.learn(104, 1.0f).getKnowledge(0.0);
        }
    }
    
    public Skill getBodyControlSkill() {
        try {
            return this.skills.getSkill(104);
        }
        catch (NoSuchSkillException nss) {
            return this.skills.learn(104, 1.0f);
        }
    }
    
    public Skill getFightingSkill() {
        if (!this.isPlayer()) {
            return this.getWeaponLessFightingSkill();
        }
        try {
            return this.skills.getSkill(1023);
        }
        catch (NoSuchSkillException nss) {
            return this.skills.learn(1023, 1.0f);
        }
    }
    
    public Skill getWeaponLessFightingSkill() {
        try {
            return this.skills.getSkill(10052);
        }
        catch (NoSuchSkillException nss) {
            try {
                return this.skills.learn(10052, (float)this.template.getSkills().getSkill(10052).getKnowledge(0.0));
            }
            catch (NoSuchSkillException nss2) {
                Creature.logger.log(Level.WARNING, "Template for " + this.getName() + " has no weaponless skill?");
                return this.skills.learn(10052, 20.0f);
            }
            catch (Exception ex) {
                Creature.logger.log(Level.WARNING, ex.getMessage() + " template for " + this.getName() + " has skills?");
                return this.skills.learn(10052, 20.0f);
            }
        }
    }
    
    public byte getAttitude(final Creature aTarget) {
        if (this.opponent == aTarget) {
            return 2;
        }
        if (aTarget.isNpc() && this.isNpc() && aTarget.getKingdomId() == this.getKingdomId()) {
            return 1;
        }
        if (this.isDominated()) {
            if (this.getDominator() != null) {
                if (this.getDominator() == aTarget) {
                    return 1;
                }
                if (this.getDominator() == aTarget.getDominator()) {
                    return 1;
                }
                return aTarget.getAttitude(this.getDominator());
            }
            else if (this.getLoyalty() > 0.0f && (aTarget.getReputation() >= 0 || aTarget.getKingdomTemplateId() == 3) && this.isFriendlyKingdom(aTarget.getKingdomId())) {
                return 0;
            }
        }
        if (aTarget.isDominated()) {
            final Creature lDominator = aTarget.getDominator();
            if (lDominator != null) {
                if (lDominator == this) {
                    return 1;
                }
                if (!aTarget.isHorse() || !aTarget.isRidden()) {
                    return this.getAttitude(lDominator);
                }
                if (this.isHungry() && this.isCarnivore()) {
                    if (Server.rand.nextInt(5) == 0) {
                        for (final Long riderLong : aTarget.getRiders()) {
                            try {
                                final Creature rider = Server.getInstance().getCreature(riderLong);
                                if (this.getAttitude(rider) == 2) {
                                    return 2;
                                }
                                continue;
                            }
                            catch (Exception ex) {
                                Creature.logger.log(Level.WARNING, ex.getMessage());
                            }
                        }
                    }
                    return 0;
                }
            }
            if (this.isFriendlyKingdom(aTarget.getKingdomId()) && aTarget.getLoyalty() > 0.0f) {
                return 0;
            }
        }
        if (this.getPet() != null && aTarget == this.getPet()) {
            return 1;
        }
        if (this.isInvulnerable()) {
            return 0;
        }
        if (aTarget.isInvulnerable()) {
            return 0;
        }
        if (!this.isPlayer() && aTarget.getCultist() != null) {
            if (aTarget.getCultist().hasFearEffect()) {
                return 0;
            }
            if (aTarget.getCultist().hasLoveEffect()) {
                return 1;
            }
        }
        if (this.isReborn() && !aTarget.equals(this.getTarget()) && !aTarget.equals(this.opponent) && aTarget.getKingdomId() == this.getKingdomId()) {
            return 0;
        }
        if (this.onlyAttacksPlayers() && !aTarget.isPlayer()) {
            return 0;
        }
        if (!this.isPlayer() && aTarget.onlyAttacksPlayers()) {
            return 0;
        }
        if (Servers.isThisAChaosServer() && this.getCitizenVillage() != null && this.getCitizenVillage().isEnemy(aTarget)) {
            return 2;
        }
        if (this.isAggHuman()) {
            if (aTarget instanceof Player) {
                boolean atta = true;
                if (this.isAnimal() && aTarget.getDeity() != null && aTarget.getDeity().isBefriendCreature() && aTarget.getFaith() > 60.0f && aTarget.getFavor() >= 30.0f) {
                    atta = false;
                }
                if (this.isMonster() && !this.isUnique() && aTarget.getDeity() != null && aTarget.getDeity().isBefriendMonster() && aTarget.getFaith() > 60.0f && aTarget.getFavor() >= 30.0f) {
                    atta = false;
                }
                if (this.getLoyalty() > 0.0f && (aTarget.getReputation() >= 0 || aTarget.getKingdomTemplateId() == 3) && this.isFriendlyKingdom(aTarget.getKingdomId())) {
                    atta = false;
                }
                if (atta) {
                    return 2;
                }
            }
            else if ((aTarget.isSpiritGuard() && aTarget.getCitizenVillage() == null) || aTarget.isKingdomGuard()) {
                if (this.getLoyalty() <= 0.0f && !this.isUnique() && (!this.isHorse() || !this.isRidden())) {
                    return 2;
                }
            }
            else if (aTarget.isRidden()) {
                if (this.isHungry() && this.isCarnivore() && Server.rand.nextInt(5) == 0) {
                    for (final Long riderLong2 : aTarget.getRiders()) {
                        try {
                            final Creature rider2 = Server.getInstance().getCreature(riderLong2);
                            if (this.getAttitude(rider2) == 2) {
                                return 2;
                            }
                            continue;
                        }
                        catch (Exception ex2) {
                            Creature.logger.log(Level.WARNING, ex2.getMessage());
                        }
                    }
                }
                return 0;
            }
        }
        else {
            if (aTarget.getKingdomId() != 0 && !this.isFriendlyKingdom(aTarget.getKingdomId()) && (this.isDefendKingdom() || (this.isAggWhitie() && aTarget.getKingdomTemplateId() != 3))) {
                return 2;
            }
            if (this.isSpiritGuard()) {
                if (this.citizenVillage != null) {
                    if (aTarget instanceof Player) {
                        if (this.citizenVillage.isEnemy(aTarget.citizenVillage)) {
                            return 2;
                        }
                        if (this.citizenVillage.getReputation(aTarget) <= -30) {
                            return 2;
                        }
                        if (this.citizenVillage.isEnemy(aTarget)) {
                            return 2;
                        }
                        if (this.citizenVillage.isAlly(aTarget)) {
                            return 1;
                        }
                        if (this.citizenVillage.isCitizen(aTarget)) {
                            return 1;
                        }
                        if (!this.isFriendlyKingdom(aTarget.getKingdomId())) {
                            return 2;
                        }
                        return 0;
                    }
                    else if (aTarget.getKingdomId() != 0) {
                        if (!this.isFriendlyKingdom(this.getKingdomId())) {
                            return 2;
                        }
                        return 0;
                    }
                    else if (aTarget.isRidden()) {
                        for (final Long riderLong2 : aTarget.getRiders()) {
                            try {
                                final Creature rider2 = Server.getInstance().getCreature(riderLong2);
                                if (!this.isFriendlyKingdom(rider2.getKingdomId())) {
                                    return 2;
                                }
                                continue;
                            }
                            catch (Exception ex2) {
                                Creature.logger.log(Level.WARNING, ex2.getMessage());
                            }
                        }
                        return 0;
                    }
                }
            }
            else if (this.isKingdomGuard()) {
                if (aTarget.getKingdomId() != 0) {
                    if (!this.isFriendlyKingdom(aTarget.getKingdomId())) {
                        return 2;
                    }
                    if (aTarget.getKingdomTemplateId() != 3 && aTarget.getReputation() <= -100) {
                        return 2;
                    }
                    if (aTarget.isPlayer()) {
                        final Village lVill = Villages.getVillageWithPerimeterAt(this.getTileX(), this.getTileY(), true);
                        if (lVill != null && lVill.kingdom == this.getKingdomId() && lVill.isEnemy(aTarget)) {
                            return 2;
                        }
                    }
                }
                else if (aTarget.isAggHuman() && !aTarget.isUnique() && aTarget.getCurrentKingdom() == this.getKingdomId() && aTarget.getLoyalty() <= 0.0f && !aTarget.isRidden()) {
                    return 2;
                }
                if (aTarget.isRidden()) {
                    for (final Long riderLong2 : aTarget.getRiders()) {
                        try {
                            final Creature rider2 = Server.getInstance().getCreature(riderLong2);
                            if (this.getAttitude(rider2) == 2) {
                                return 2;
                            }
                            continue;
                        }
                        catch (Exception ex2) {
                            Creature.logger.log(Level.WARNING, ex2.getMessage());
                        }
                    }
                }
            }
        }
        if (this.isCarnivore() && aTarget.isPrey() && Server.rand.nextInt(10) == 0 && this.canEat() && aTarget.getCurrentVillage() == null && aTarget.getHitched() == null) {
            return 2;
        }
        return 0;
    }
    
    public final byte getCurrentKingdom() {
        return Zones.getKingdom(this.getTileX(), this.getTileY());
    }
    
    public boolean isFriendlyKingdom(final byte targetKingdom) {
        if (this.getKingdomId() == 0 || targetKingdom == 0) {
            return false;
        }
        if (this.getKingdomId() == targetKingdom) {
            return true;
        }
        final Kingdom myKingd = Kingdoms.getKingdom(this.getKingdomId());
        return myKingd != null && myKingd.isAllied(targetKingdom);
    }
    
    public Possessions getPossessions() {
        return this.possessions;
    }
    
    public Item getInventory() {
        if (this.possessions != null) {
            return this.possessions.getInventory();
        }
        Creature.logger.warning("Posessions was null for " + this.id);
        return null;
    }
    
    public Optional<Item> getInventoryOptional() {
        if (this.possessions != null) {
            return Optional.ofNullable(this.possessions.getInventory());
        }
        Creature.logger.warning("Posessions was null for " + this.id);
        return Optional.empty();
    }
    
    public static final Item createItem(final int templateId, final float qualityLevel) throws Exception {
        final Item item = ItemFactory.createItem(templateId, qualityLevel, (byte)0, (byte)0, null);
        return item;
    }
    
    @Override
    public void save() throws IOException {
        this.possessions.save();
        this.status.save();
        this.skills.save();
    }
    
    public void savePosition(final int zoneid) throws IOException {
        this.status.savePosition(this.id, false, zoneid, false);
    }
    
    public boolean isGuest() {
        return this.guest;
    }
    
    public void setGuest(final boolean g) {
        this.guest = g;
    }
    
    public CreatureTemplate getTemplate() {
        return this.template;
    }
    
    public void refreshAttitudes() {
        if (this.visionArea != null) {
            this.visionArea.refreshAttitudes();
        }
        if (this.currentTile != null) {
            this.currentTile.checkChangedAttitude(this);
        }
    }
    
    public static Creature doNew(final int templateid, final byte ctype, final float aPosX, final float aPosY, final float aRot, final int layer, final String name, final byte gender) throws Exception {
        return doNew(templateid, true, aPosX, aPosY, aRot, layer, name, gender, (byte)0, ctype, false);
    }
    
    public static Creature doNew(final int templateid, final float aPosX, final float aPosY, final float aRot, final int layer, final String name, final byte gender) throws Exception {
        return doNew(templateid, aPosX, aPosY, aRot, layer, name, gender, (byte)0);
    }
    
    public static Creature doNew(final int templateid, final float aPosX, final float aPosY, final float aRot, final int layer, final String name, final byte gender, final byte kingdom) throws Exception {
        return doNew(templateid, true, aPosX, aPosY, aRot, layer, name, gender, kingdom, (byte)0, false);
    }
    
    public static Creature doNew(final int templateid, final boolean createPossessions, final float aPosX, final float aPosY, final float aRot, final int layer, final String name, final byte gender, final byte kingdom, final byte ctype, final boolean reborn) throws Exception {
        return doNew(templateid, createPossessions, aPosX, aPosY, aRot, layer, name, gender, kingdom, ctype, reborn, (byte)0);
    }
    
    public static Creature doNew(final int templateid, final boolean createPossessions, final float aPosX, final float aPosY, final float aRot, final int layer, final String name, final byte gender, final byte kingdom, final byte ctype, final boolean reborn, final byte age) throws Exception {
        return doNew(templateid, createPossessions, aPosX, aPosY, aRot, layer, name, gender, kingdom, ctype, reborn, age, 0);
    }
    
    public static Creature doNew(final int templateid, final boolean createPossessions, final float aPosX, final float aPosY, final float aRot, final int layer, final String name, final byte gender, byte kingdom, final byte ctype, final boolean reborn, final byte age, final int floorLevel) throws Exception {
        final Creature toReturn = (!reborn && (templateid == 1 || templateid == 113)) ? new Npc(CreatureTemplateFactory.getInstance().getTemplate(templateid)) : new Creature(CreatureTemplateFactory.getInstance().getTemplate(templateid));
        long wid = WurmId.getNextCreatureId();
        try {
            while (Creatures.getInstance().getCreature(wid) != null) {
                wid = WurmId.getNextCreatureId();
            }
        }
        catch (Exception ex) {}
        toReturn.setWurmId(wid, aPosX, aPosY, normalizeAngle(aRot), layer);
        if (name.length() > 0) {
            toReturn.setName(name);
        }
        if (toReturn.getTemplate().isRoyalAspiration()) {
            if (toReturn.getTemplate().getTemplateId() == 62) {
                kingdom = 1;
            }
            else if (toReturn.getTemplate().getTemplateId() == 63) {
                kingdom = 3;
            }
        }
        if (reborn) {
            toReturn.getStatus().reborn = true;
        }
        if (floorLevel > 0) {
            toReturn.pushToFloorLevel(floorLevel);
        }
        else {
            toReturn.setPositionZ(toReturn.calculatePosZ());
        }
        if (age <= 0) {
            toReturn.getStatus().age = (int)(1.0f + Server.rand.nextFloat() * Math.min(48, toReturn.getTemplate().getMaxAge()));
        }
        else {
            toReturn.getStatus().age = age;
        }
        if (toReturn.isGhost() || toReturn.isKingdomGuard() || reborn) {
            toReturn.getStatus().age = 12;
        }
        if (ctype != 0) {
            toReturn.getStatus().modtype = ctype;
        }
        if (toReturn.isUnique()) {
            toReturn.getStatus().age = 12 + (int)(Server.rand.nextFloat() * (toReturn.getTemplate().getMaxAge() - 12));
        }
        toReturn.getStatus().kingdom = kingdom;
        if (Kingdoms.getKingdom(kingdom) != null && Kingdoms.getKingdom(kingdom).getTemplate() == 3) {
            toReturn.setAlignment(-50.0f);
            toReturn.setDeity(Deities.getDeity(4));
            toReturn.setFaith(1.0f);
        }
        toReturn.setSex(gender, true);
        Creatures.getInstance().addCreature(toReturn, false, false);
        toReturn.loadSkills();
        toReturn.createPossessions();
        toReturn.getBody().createBodyParts();
        if (!toReturn.isAnimal() && createPossessions) {
            createBasicItems(toReturn);
            toReturn.wearItems();
        }
        if ((toReturn.isHorse() || toReturn.getTemplate().isBlackOrWhite) && Server.rand.nextInt(10) == 0) {
            setRandomColor(toReturn);
        }
        Creatures.getInstance().sendToWorld(toReturn);
        toReturn.createVisionArea();
        toReturn.save();
        if (reborn) {
            toReturn.getStatus().setReborn(true);
        }
        if (ctype != 0) {
            toReturn.getStatus().setType(ctype);
        }
        toReturn.getStatus().setKingdom(kingdom);
        if (kingdom == 3) {
            toReturn.setAlignment(-50.0f);
            toReturn.setDeity(Deities.getDeity(4));
            toReturn.setFaith(1.0f);
        }
        if (templateid != 119) {
            Server.getInstance().broadCastAction(toReturn.getNameWithGenus() + " has arrived.", toReturn, 10);
        }
        if (toReturn.isUnique()) {
            Server.getInstance().broadCastSafe("Rumours of " + toReturn.getName() + " are starting to spread.");
            Servers.localServer.spawnedUnique();
            Creature.logger.log(Level.INFO, "Unique " + toReturn.getName() + " spawned @ " + toReturn.getTileX() + ", " + toReturn.getTileY() + ", wurmID = " + toReturn.getWurmId());
        }
        if (toReturn.getTemplate().getCreatureAI() != null) {
            toReturn.getTemplate().getCreatureAI().creatureCreated(toReturn);
        }
        return toReturn;
    }
    
    public float getSecondsPlayed() {
        return 1.0f;
    }
    
    public static void createBasicItems(final Creature toReturn) {
        try {
            final Item inventory = toReturn.getInventory();
            if (toReturn.getTemplate().getTemplateId() == 11) {
                final Item club = createItem(314, 45.0f);
                inventory.insertItem(club);
                final Item paper = getRareRecipe("Da Wife", 1250, 1251, 1252, 1253);
                if (paper != null) {
                    inventory.insertItem(paper);
                }
            }
            else if (toReturn.getTemplate().getTemplateId() == 23) {
                final Item paper2 = getRareRecipe("Granny Gobin", 1255, 1256, 1257, 1258);
                if (paper2 != null) {
                    inventory.insertItem(paper2);
                }
            }
            else if (toReturn.getTemplate().getTemplateId() == 75) {
                final Item swo = createItem(81, 85.0f);
                final ItemSpellEffects effs = new ItemSpellEffects(swo.getWurmId());
                effs.addSpellEffect(new SpellEffect(swo.getWurmId(), (byte)33, 90.0f, 20000000));
                inventory.insertItem(swo);
                final Item helmOne = createItem(285, 75.0f);
                final Item helmTwo = createItem(285, 75.0f);
                helmOne.setMaterial((byte)9);
                helmTwo.setMaterial((byte)9);
                inventory.insertItem(helmOne);
                inventory.insertItem(helmTwo);
            }
            else if (toReturn.isUnique()) {
                if (toReturn.getTemplate().getTemplateId() == 26) {
                    final Item sword = createItem(80, 45.0f);
                    inventory.insertItem(sword);
                    final Item shield = createItem(4, 45.0f);
                    inventory.insertItem(shield);
                    final Item goboHat = createItem(1014, 55.0f);
                    inventory.insertItem(goboHat);
                }
                else if (toReturn.getTemplate().getTemplateId() == 27) {
                    final Item club = createItem(314, 65.0f);
                    inventory.insertItem(club);
                    final Item trollCrown = createItem(1015, 70.0f);
                    inventory.insertItem(trollCrown);
                }
                else if (toReturn.getTemplate().getTemplateId() == 22 || toReturn.getTemplate().getTemplateId() == 20) {
                    final Item club = createItem(314, 65.0f);
                    inventory.insertItem(club);
                }
                else if (!CreatureTemplate.isDragonHatchling(toReturn.getTemplate().getTemplateId())) {
                    if (CreatureTemplate.isFullyGrownDragon(toReturn.getTemplate().getTemplateId())) {}
                }
            }
        }
        catch (Exception ex) {
            Creature.logger.log(Level.INFO, "Failed to create items for creature.", ex);
        }
    }
    
    public Item getPrimWeapon() {
        return this.getPrimWeapon(false);
    }
    
    public Item getPrimWeapon(final boolean onlyBodyPart) {
        Item primWeapon = null;
        if (this.isAnimal()) {
            try {
                if (this.getHandDamage() > 0.0f) {
                    return this.getEquippedWeapon((byte)14);
                }
                if (this.getKickDamage() > 0.0f) {
                    return this.getEquippedWeapon((byte)34);
                }
                if (this.getHeadButtDamage() > 0.0f) {
                    return this.getEquippedWeapon((byte)1);
                }
                if (this.getBiteDamage() > 0.0f) {
                    return this.getEquippedWeapon((byte)29);
                }
                if (this.getBreathDamage() > 0.0f) {
                    return this.getEquippedWeapon((byte)2);
                }
            }
            catch (NoSpaceException nsp) {
                Creature.logger.log(Level.WARNING, this.getName() + nsp.getMessage(), nsp);
            }
        }
        else {
            try {
                final byte slot = (byte)(this.isPlayer() ? 38 : 14);
                primWeapon = this.getEquippedWeapon(slot, true);
            }
            catch (NoSpaceException nsp) {
                Creature.logger.log(Level.WARNING, nsp.getMessage(), nsp);
            }
        }
        if (primWeapon == null) {
            try {
                final byte slot = (byte)(this.isPlayer() ? 37 : 13);
                primWeapon = this.getEquippedWeapon(slot, true);
                if (!primWeapon.isTwoHanded()) {
                    primWeapon = null;
                }
                else if (this.getShield() != null) {
                    primWeapon = null;
                }
            }
            catch (NoSpaceException nsp) {
                Creature.logger.log(Level.WARNING, nsp.getMessage(), nsp);
            }
        }
        return primWeapon;
    }
    
    public Item getLefthandWeapon() {
        try {
            final byte slot = (byte)(this.isPlayer() ? 37 : 13);
            final Set<Item> wornItems = this.status.getBody().getBodyPart(slot).getItems();
            if (wornItems != null) {
                for (final Item item : wornItems) {
                    if (!item.isArmour() && !item.isBodyPartAttached() && item.getDamagePercent() > 0) {
                        return item;
                    }
                }
            }
        }
        catch (NoSpaceException nsp) {
            Creature.logger.log(Level.WARNING, nsp.getMessage(), nsp);
        }
        return null;
    }
    
    public Item getLefthandItem() {
        try {
            final byte slot = (byte)(this.isPlayer() ? 37 : 13);
            final Set<Item> wornItems = this.status.getBody().getBodyPart(slot).getItems();
            if (wornItems != null) {
                for (final Item item : wornItems) {
                    if (!item.isArmour() && !item.isBodyPartAttached()) {
                        return item;
                    }
                }
            }
        }
        catch (NoSpaceException nsp) {
            Creature.logger.log(Level.WARNING, nsp.getMessage(), nsp);
        }
        return null;
    }
    
    public Item getRighthandItem() {
        try {
            final byte slot = (byte)(this.isPlayer() ? 38 : 14);
            final Set<Item> wornItems = this.status.getBody().getBodyPart(slot).getItems();
            if (wornItems != null) {
                for (final Item item : wornItems) {
                    if (!item.isArmour() && !item.isBodyPartAttached()) {
                        return item;
                    }
                }
            }
        }
        catch (NoSpaceException nsp) {
            Creature.logger.log(Level.WARNING, nsp.getMessage(), nsp);
        }
        return null;
    }
    
    public Item getRighthandWeapon() {
        try {
            final byte slot = (byte)(this.isPlayer() ? 38 : 14);
            final Set<Item> wornItems = this.status.getBody().getBodyPart(slot).getItems();
            if (wornItems != null) {
                for (final Item item : wornItems) {
                    if (!item.isArmour() && !item.isBodyPartAttached() && item.getDamagePercent() > 0) {
                        return item;
                    }
                }
            }
        }
        catch (NoSpaceException nsp) {
            Creature.logger.log(Level.WARNING, nsp.getMessage(), nsp);
        }
        return null;
    }
    
    public Item getWornBelt() {
        try {
            final byte slot = (byte)(this.isPlayer() ? 43 : 34);
            final Set<Item> wornItems = this.status.getBody().getBodyPart(slot).getItems();
            if (wornItems != null) {
                for (final Item item : wornItems) {
                    if (item.isBelt()) {
                        return item;
                    }
                }
            }
        }
        catch (NoSpaceException nsp) {
            Creature.logger.log(Level.WARNING, nsp.getMessage(), nsp);
        }
        return null;
    }
    
    public Item[] getSecondaryWeapons() {
        final Set<Item> toReturn = new HashSet<Item>();
        if (this.getBiteDamage() > 0.0f) {
            try {
                toReturn.add(this.getEquippedWeapon((byte)29));
            }
            catch (NoSpaceException nsp) {
                Creature.logger.log(Level.WARNING, this.getName() + " no face.");
            }
        }
        if (this.getHeadButtDamage() > 0.0f) {
            try {
                toReturn.add(this.getEquippedWeapon((byte)1));
            }
            catch (NoSpaceException nsp) {
                Creature.logger.log(Level.WARNING, this.getName() + " no head.");
            }
        }
        if (this.getKickDamage() > 0.0f) {
            try {
                if (this.isAnimal() || this.isMonster()) {
                    toReturn.add(this.getEquippedWeapon((byte)34));
                }
                else {
                    try {
                        this.getArmour((byte)34);
                    }
                    catch (NoArmourException nsp2) {
                        if (this.getCarryingCapacityLeft() > 40000) {
                            toReturn.add(this.getEquippedWeapon((byte)34));
                        }
                    }
                }
            }
            catch (NoSpaceException nsp) {
                Creature.logger.log(Level.WARNING, this.getName() + " no legs.");
            }
        }
        if (this.getBreathDamage() > 0.0f) {
            try {
                toReturn.add(this.getEquippedWeapon((byte)2));
            }
            catch (NoSpaceException nsp) {
                Creature.logger.log(Level.WARNING, this.getName() + " no torso.");
            }
        }
        if (this.getShield() == null) {
            try {
                if (this.getPrimWeapon() == null || !this.getPrimWeapon().isTwoHanded()) {
                    if (this.isPlayer()) {
                        toReturn.add(this.getEquippedWeapon((byte)37, false));
                    }
                    else {
                        toReturn.add(this.getEquippedWeapon((byte)13, false));
                    }
                }
            }
            catch (NoSpaceException nsp) {
                Creature.logger.log(Level.WARNING, this.getName() + " - no arm. This may be possible later but not now." + nsp.getMessage(), nsp);
            }
        }
        if (!toReturn.isEmpty()) {
            return toReturn.toArray(new Item[toReturn.size()]);
        }
        return Creature.emptyItems;
    }
    
    public Item getShield() {
        Item shield = null;
        try {
            final byte slot = (byte)(this.isPlayer() ? 44 : 3);
            shield = this.getEquippedItem(slot);
            if (shield != null && !shield.isShield()) {
                shield = null;
            }
        }
        catch (NoSpaceException nsp) {
            Creature.logger.log(Level.WARNING, nsp.getMessage(), nsp);
        }
        return shield;
    }
    
    public float getSpeed() {
        if (this.getCreatureAIData() != null) {
            return this.getCreatureAIData().getSpeed();
        }
        return this.template.getSpeed();
    }
    
    public int calculateSize() {
        final int centimetersHigh = this.getBody().getCentimetersHigh();
        final int centimetersLong = this.getBody().getCentimetersLong();
        final int centimetersWide = this.getBody().getCentimetersWide();
        int size = 3;
        if (centimetersHigh > 400 || centimetersLong > 400 || centimetersWide > 400) {
            size = 5;
        }
        else if (centimetersHigh > 200 || centimetersLong > 200 || centimetersWide > 200) {
            size = 4;
        }
        else if (centimetersHigh > 100 || centimetersLong > 100 || centimetersWide > 100) {
            size = 3;
        }
        else if (centimetersHigh > 50 || centimetersLong > 50 || centimetersWide > 50) {
            size = 2;
        }
        else {
            size = 1;
        }
        return size;
    }
    
    public void say(final String message) {
        if (this.currentTile != null) {
            this.currentTile.broadCastMessage(new Message(this, (byte)0, ":Local", "<" + this.getName() + "> " + message));
        }
    }
    
    public void say(final String message, final boolean emote) {
        if (this.currentTile != null) {
            if (!emote) {
                this.say(message);
            }
            else {
                this.currentTile.broadCastMessage(new Message(this, (byte)6, ":Local", this.getName() + " " + message));
            }
        }
    }
    
    public void sendEquipment(final Creature receiver) {
        if (receiver.addItemWatched(this.getBody().getBodyItem())) {
            receiver.getCommunicator().sendOpenInventoryWindow(this.getBody().getBodyItem().getWurmId(), this.getName());
            this.getBody().getBodyItem().addWatcher(this.getBody().getBodyItem().getWurmId(), receiver);
            final Wounds w = this.getBody().getWounds();
            if (w != null) {
                final Wound[] wounds2;
                final Wound[] wounds = wounds2 = w.getWounds();
                for (final Wound lWound : wounds2) {
                    try {
                        final Item bodypart = this.getBody().getBodyPartForWound(lWound);
                        receiver.getCommunicator().sendAddWound(lWound, bodypart);
                    }
                    catch (NoSpaceException nsp) {
                        Creature.logger.log(Level.INFO, nsp.getMessage(), nsp);
                    }
                }
            }
        }
        if (receiver.getPower() >= 2 && receiver.addItemWatched(this.getInventory())) {
            receiver.getCommunicator().sendOpenInventoryWindow(this.getInventory().getWurmId(), this.getName() + " inventory");
            this.getInventory().addWatcher(this.getInventory().getWurmId(), receiver);
        }
    }
    
    public final void startUsingPath() {
        if (this.setTargetNOID) {
            this.setTarget(-10L, true);
            this.setTargetNOID = false;
        }
        if (this.creatureToBlinkTo != null) {
            if (!this.creatureToBlinkTo.isDead()) {
                Creature.logger.log(Level.INFO, this.getName() + " at " + this.getTileX() + "," + this.getTileY() + " " + this.getLayer() + "  blingking to " + this.creatureToBlinkTo.getTileX() + "," + this.creatureToBlinkTo.getTileY() + "," + this.creatureToBlinkTo.getLayer());
                this.blinkTo(this.creatureToBlinkTo.getTileX(), this.creatureToBlinkTo.getTileY(), this.creatureToBlinkTo.getLayer(), this.creatureToBlinkTo.getFloorLevel());
                this.status.setPath(null);
                this.setPathing(this.receivedPath = false, true);
            }
            this.creatureToBlinkTo = null;
        }
        if (this.receivedPath) {
            this.setPathing(this.receivedPath = false, false);
            if (this.status.getPath() != null) {
                this.sendToLoggers("received path to " + this.status.getPath().getTargetTile().getTileX() + "," + this.status.getPath().getTargetTile().getTileY(), (byte)2);
                if (this.status.getPath().getSize() >= 4) {
                    this.pathRecalcLength = this.status.getPath().getSize() / 2;
                }
                else {
                    this.pathRecalcLength = 0;
                }
                this.status.setMoving(true);
                if (this.moveAlongPath() || this.isTeleporting()) {
                    this.status.setPath(null);
                    this.status.setMoving(false);
                }
            }
        }
    }
    
    protected void hunt() {
        if (!this.isPathing()) {
            Path path = null;
            boolean findPath = false;
            if (Server.rand.nextInt(2 * Math.max(1, this.template.getAggressivity())) == 0) {
                this.setTargetNOID = true;
                return;
            }
            if (this.isAnimal() || this.isDominated()) {
                path = this.status.getPath();
                if (path == null) {
                    findPath = true;
                }
            }
            else {
                findPath = true;
            }
            if (findPath) {
                this.startPathing(10);
            }
            else if (path == null) {
                this.startPathing(100);
            }
        }
    }
    
    public void setAlertSeconds(final int seconds) {
        this.guardSecondsLeft = (byte)seconds;
    }
    
    public byte getAlertSeconds() {
        return this.guardSecondsLeft;
    }
    
    public void callGuards() {
        if (this.guardSecondsLeft > 0) {
            this.getCommunicator().sendNormalServerMessage("You already called the guards. Wait a few seconds.", (byte)3);
            return;
        }
        this.guardSecondsLeft = 10;
        if (this.getVisionArea() != null) {
            if (this.isOnSurface()) {
                if (this.getVisionArea().getSurface() != null) {
                    this.getVisionArea().getSurface().callGuards();
                }
            }
            else if (this.getVisionArea().getUnderGround() != null) {
                this.getVisionArea().getUnderGround().callGuards();
            }
        }
    }
    
    public final boolean isPathing() {
        return this.isPathing;
    }
    
    public final void setPathing(final boolean pathing, final boolean removeFromPathing) {
        this.isPathing = pathing;
        if (removeFromPathing) {
            if (this.isHuman() || this.isGhost() || this.isUnique()) {
                Creature.pathFinderNPC.removeTarget(this);
            }
            else if (this.isAggHuman()) {
                Creature.pathFinderAgg.removeTarget(this);
            }
            else {
                Creature.pathFinder.removeTarget(this);
            }
        }
    }
    
    public final void startPathingToTile(final PathTile p) {
        if (this.creatureToBlinkTo == null && (this.targetPathTile = p) != null) {
            this.sendToLoggers("heading to specific " + p.getTileX() + "," + p.getTileY(), (byte)2);
            this.setPathing(true, false);
            if (this.isHuman() || this.isGhost() || this.isUnique()) {
                Creature.pathFinderNPC.addTarget(this, p);
            }
            else if (this.isAggHuman()) {
                Creature.pathFinderAgg.addTarget(this, p);
            }
            else {
                Creature.pathFinder.addTarget(this, p);
            }
        }
    }
    
    public final void startPathing(final int seed) {
        if (this.creatureToBlinkTo == null) {
            final PathTile p = this.getMoveTarget(seed);
            if (p != null) {
                this.startPathingToTile(p);
            }
        }
    }
    
    public final void checkMove() throws NoSuchCreatureException, NoSuchPlayerException {
        if (this.hitchedTo != null) {
            return;
        }
        if (this.isSentinel()) {
            return;
        }
        if (this.isHorse() || this.isUnicorn()) {
            final Item torsoItem = this.getWornItem((byte)2);
            if (torsoItem != null && (torsoItem.isSaddleLarge() || torsoItem.isSaddleNormal())) {
                return;
            }
        }
        if (this.isDominated()) {
            if (this.hasOrders()) {
                if (this.target == -10L) {
                    if (this.status.getPath() == null) {
                        if (!this.isPathing()) {
                            this.startPathing(0);
                        }
                    }
                    else if (this.moveAlongPath() || this.isTeleporting()) {
                        this.status.setPath(null);
                        this.status.setMoving(false);
                        if (this.isSpy()) {
                            final Creature linkedToc = this.getCreatureLinkedTo();
                            if (this.isWithinSpyDist(linkedToc)) {
                                this.turnTowardsCreature(linkedToc);
                                for (final Npc npc : Creatures.getInstance().getNpcs()) {
                                    if (!npc.isDead() && this.isSpyFriend(npc) && npc.isWithinDistanceTo(this, 400.0f) && npc.longTarget == null) {
                                        npc.longTarget = new LongTarget(linkedToc.getTileX(), linkedToc.getTileY(), 0, linkedToc.isOnSurface(), linkedToc.getFloorLevel(), npc);
                                        if (!npc.isWithinDistanceTo(linkedToc, 100.0f)) {
                                            final int seed = Server.rand.nextInt(5);
                                            String mess = "Think I'll go hunt for " + linkedToc.getName() + " a bit...";
                                            switch (seed) {
                                                case 0: {
                                                    mess = linkedToc.getName() + " is in trouble now!";
                                                    break;
                                                }
                                                case 1: {
                                                    mess = "Going to check out what " + linkedToc.getName() + " is doing.";
                                                    break;
                                                }
                                                case 2: {
                                                    mess = "Heading to slay " + linkedToc.getName() + ".";
                                                    break;
                                                }
                                                case 3: {
                                                    mess = "Going to get me the scalp of " + linkedToc.getName() + " today.";
                                                    break;
                                                }
                                                case 4: {
                                                    mess = "Poor " + linkedToc.getName() + " won't know what hit " + linkedToc.getHimHerItString() + ".";
                                                    break;
                                                }
                                                default: {
                                                    mess = "Think I'll go hunt for " + linkedToc.getName() + " a bit...";
                                                    break;
                                                }
                                            }
                                            final VolaTile tile = npc.getCurrentTile();
                                            if (tile != null) {
                                                final Message m = new Message(npc, (byte)0, ":Local", "<" + npc.getName() + "> " + mess);
                                                tile.broadCastMessage(m);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else if (this.status.getPath() != null) {
                    if (this.moveAlongPath() || this.isTeleporting()) {
                        this.status.setPath(null);
                        this.status.setMoving(false);
                    }
                }
                else {
                    this.hunt();
                }
            }
        }
        else if (this.leader == null && !this.shouldStandStill && !this.status.isUnconscious() && this.status.getStunned() == 0.0f) {
            if (this.isMoveGlobal()) {
                if (this.status.getPath() != null) {
                    if (this.moveAlongPath() || this.isTeleporting()) {
                        this.status.setPath(null);
                        this.status.setMoving(false);
                    }
                }
                else if (this.isHunter() && this.target != -10L && this.fleeCounter <= 0) {
                    this.hunt();
                }
                else {
                    if (Server.rand.nextInt(100) == 0) {
                        final PathTile targ = this.getPersonalTargetTile();
                        if (targ != null && !this.isPathing) {
                            this.startPathingToTile(targ);
                        }
                    }
                    if (this.status.moving) {
                        if (Server.rand.nextInt(100) < 5) {
                            this.status.setMoving(false);
                        }
                    }
                    else {
                        int mod = 1;
                        int max = 2000;
                        if ((this.isCareful() && this.getStatus().damage > 10000) || this.loggerCreature1 > 0L) {
                            mod = 19;
                        }
                        else if (this.isBred() || this.isBranded() || this.isCaredFor()) {
                            max = 20000;
                        }
                        else if (this.isNpc() && !this.isAggHuman() && this.getCitizenVillage() != null) {
                            max = 200 + (int)(this.getWurmId() % 100L) * 3;
                        }
                        if (Server.rand.nextInt(Math.max(1, max - this.template.getMoveRate() * mod)) < 5 || this.shouldFlee()) {
                            this.status.setMoving(true);
                        }
                        else if (Features.Feature.CREATURE_MOVEMENT_CHANGES.isEnabled() && (Server.rand.nextInt(Math.max(1, 1000 - this.template.getMoveRate())) < 5 || this.loggerCreature1 > 0L)) {
                            for (final Fence f : this.getCurrentTile().getAllFences()) {
                                if (f.isHorizontal() && Math.abs(f.getPositionY() - this.getPosY()) < 1.25f) {
                                    this.takeSimpleStep();
                                    break;
                                }
                                if (!f.isHorizontal() && Math.abs(f.getPositionX() - this.getPosX()) < 1.25f) {
                                    this.takeSimpleStep();
                                    break;
                                }
                            }
                        }
                    }
                    if (this.status.moving && !this.isTeleporting()) {
                        this.takeSimpleStep();
                    }
                }
            }
            else if (this.status.getPath() == null) {
                if (!this.isTeleporting()) {
                    if (!this.isPathing()) {
                        if (this.target == -10L || this.shouldFlee()) {
                            int mod = 1;
                            final int max = 2000;
                            if (this.isCareful() && this.getStatus().damage > 10000) {
                                mod = 19;
                            }
                            if (this.loggerCreature1 > 0L) {
                                mod = 19;
                            }
                            int seed2 = Server.rand.nextInt(Math.max(2, max - this.template.getMoveRate() * mod));
                            if (this.getPositionZ() < 0.0f) {
                                seed2 -= 100;
                            }
                            if (seed2 < 8 || (this.isSpiritGuard() && this.citizenVillage != this.currentVillage) || this.shouldFlee()) {
                                this.startPathing(seed2);
                            }
                        }
                        else {
                            this.hunt();
                        }
                        if (Features.Feature.CREATURE_MOVEMENT_CHANGES.isEnabled()) {
                            if (Server.rand.nextInt(Math.max(1, 1000 - this.template.getMoveRate())) < 5 || this.loggerCreature1 > 0L) {
                                final float xMod = this.getPosX() % 4.0f;
                                final float yMod = this.getPosY() % 4.0f;
                                if (xMod > 3.5f || xMod < 0.5f || yMod > 3.5f || yMod < 0.5f) {
                                    this.takeSimpleStep();
                                }
                            }
                            if (this.shouldFlee() && this.getPathfindCounter() > 10 && this.targetPathTile != null && (this.getTileX() != this.targetPathTile.getTileX() || this.getTileY() != this.targetPathTile.getTileY())) {
                                if (this.getPathfindCounter() % 50 == 0 && Server.rand.nextFloat() < 0.05f) {
                                    this.turnTowardsTile((short)this.targetPathTile.getTileX(), (short)this.targetPathTile.getTileY());
                                }
                                this.takeSimpleStep();
                            }
                        }
                    }
                    else {
                        this.sendToLoggers("still pathing");
                    }
                }
                else {
                    this.status.setPath(null);
                    this.status.setMoving(false);
                }
            }
            else if (this.moveAlongPath() || this.isTeleporting()) {
                this.status.setPath(null);
                this.status.setMoving(false);
            }
        }
    }
    
    public float getMoveModifier(final int tile) {
        final short height = Tiles.decodeHeight(tile);
        if (height < 2) {
            return 0.5f * this.status.getMovementTypeModifier();
        }
        return Tiles.getTile(Tiles.decodeType(tile)).speed * this.status.getMovementTypeModifier();
    }
    
    public boolean mayManageGuards() {
        return this.citizenVillage != null && this.citizenVillage.isActionAllowed((short)67, this);
    }
    
    public boolean isMoving() {
        return this.status.isMoving();
    }
    
    public static final float normalizeAngle(final float angle) {
        return MovementChecker.normalizeAngle(angle);
    }
    
    public final void checkBridgeMove(final VolaTile oldTile, final VolaTile newtile, final float diffZ) {
        if (this.getBridgeId() == -10L && newtile.getStructure() != null) {
            final BridgePart[] bridgeParts = newtile.getBridgeParts();
            if (bridgeParts != null) {
                for (final BridgePart bp : bridgeParts) {
                    if (bp.isFinished()) {
                        boolean enter = false;
                        final float nz = Zones.calculatePosZ(this.getPosX(), this.getPosY(), newtile, this.isOnSurface(), this.isFloating(), this.getPositionZ(), this, bp.getStructureId());
                        final float newDiff = Math.abs(nz - this.getPositionZ());
                        final float maxDiff = 1.3f;
                        if (oldTile != null) {
                            if (bp.getDir() == 0 || bp.getDir() == 4) {
                                if (oldTile.getTileY() == newtile.getTileY() && newDiff < 1.3f && bp.hasAnExit()) {
                                    enter = true;
                                }
                            }
                            else if (oldTile.getTileX() == newtile.getTileX() && newDiff < 1.3f && bp.hasAnExit()) {
                                enter = true;
                            }
                        }
                        else {
                            enter = (newDiff < 1.3f);
                        }
                        if (enter) {
                            this.setBridgeId(bp.getStructureId());
                            final float newDiffZ = nz - this.getPositionZ();
                            this.setPositionZ(nz);
                            this.moved(0.0f, 0.0f, newDiffZ, 0, 0);
                            break;
                        }
                    }
                }
            }
        }
        else if (this.getBridgeId() != -10L) {
            boolean leave = true;
            if (oldTile != null) {
                final BridgePart[] bridgeParts2 = oldTile.getBridgeParts();
                if (bridgeParts2 != null) {
                    for (final BridgePart bp2 : bridgeParts2) {
                        if (bp2.isFinished()) {
                            if (bp2.getDir() == 0 || bp2.getDir() == 4) {
                                if (oldTile.getTileX() != newtile.getTileX()) {
                                    leave = false;
                                }
                            }
                            else if (oldTile.getTileY() != newtile.getTileY()) {
                                leave = false;
                            }
                        }
                    }
                }
            }
            if (leave) {
                if (newtile.getStructure() == null || newtile.getStructure().getWurmId() != this.getBridgeId()) {
                    this.setBridgeId(-10L);
                }
                else {
                    final BridgePart[] bridgeParts2 = newtile.getBridgeParts();
                    boolean foundBridge = false;
                    for (final BridgePart bp3 : bridgeParts2) {
                        foundBridge = true;
                        if (!bp3.isFinished()) {
                            this.setBridgeId(-10L);
                            return;
                        }
                    }
                    if (foundBridge) {
                        for (final BridgePart bp3 : bridgeParts2) {
                            if (bp3.isFinished() && bp3.hasAnExit()) {
                                this.setBridgeId(bp3.getStructureId());
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public boolean moveAlongPath() {
        final long start = System.nanoTime();
        try {
            Path path = null;
            int mvs = 2;
            if (this.target != -10L) {
                mvs = 3;
            }
            if (this.getSize() >= 5) {
                mvs += 3;
            }
            for (int x = 0; x < mvs; ++x) {
                path = this.status.getPath();
                if (path != null && !path.isEmpty()) {
                    PathTile next = path.getFirst();
                    if (next.getTileX() == this.getCurrentTile().tilex && next.getTileY() == this.getCurrentTile().tiley) {
                        boolean canRemove = true;
                        if (next.hasSpecificPos()) {
                            final float diffX = this.status.getPositionX() - next.getPosX();
                            final float diffY = this.status.getPositionY() - next.getPosY();
                            final double totalDist = Math.sqrt(diffX * diffX + diffY * diffY);
                            final float lMod = this.getMoveModifier((this.isOnSurface() ? Server.surfaceMesh : Server.caveMesh).getTile((int)this.status.getPositionX() >> 2, (int)this.status.getPositionY() >> 2));
                            if (totalDist > this.getSpeed() * lMod) {
                                canRemove = false;
                            }
                        }
                        if (canRemove) {
                            path.removeFirst();
                            if (this.getTarget() != null && this.getTarget().getTileX() == this.getTileX() && this.getTarget().getTileY() == this.getTileY() && this.getTarget().getFloorLevel() != this.getFloorLevel()) {
                                if (this.isSpiritGuard()) {
                                    this.pushToFloorLevel(this.getTarget().getFloorLevel());
                                }
                                else if (this.canOpenDoors()) {
                                    final Floor[] floors2;
                                    final Floor[] floors = floors2 = this.getCurrentTile().getFloors(Math.min(this.getFloorLevel(), this.getTarget().getFloorLevel()) * 30, Math.max(this.getFloorLevel(), this.getTarget().getFloorLevel()) * 30);
                                    for (final Floor f : floors2) {
                                        if (this.getTarget().getFloorLevel() > this.getFloorLevel()) {
                                            if (f.getFloorLevel() == this.getFloorLevel() + 1 && (f.isOpening() || f.isAPlan())) {
                                                this.pushToFloorLevel(f.getFloorLevel());
                                                break;
                                            }
                                        }
                                        else if (f.getFloorLevel() == this.getFloorLevel() && (f.isOpening() || f.isAPlan())) {
                                            this.pushToFloorLevel(f.getFloorLevel() - 1);
                                            break;
                                        }
                                    }
                                }
                            }
                            if (path.isEmpty()) {
                                return true;
                            }
                            next = path.getFirst();
                        }
                    }
                    float lPosX = this.status.getPositionX();
                    float lPosY = this.status.getPositionY();
                    float lPosZ = this.status.getPositionZ();
                    float lRotation = this.status.getRotation();
                    final double lNewRotation = next.hasSpecificPos() ? Math.atan2(next.getPosY() - lPosY, next.getPosX() - lPosX) : Math.atan2((next.getTileY() << 2) + 2 - lPosY, (next.getTileX() << 2) + 2 - lPosX);
                    lRotation = (float)(lNewRotation * 57.29577951308232) + 90.0f;
                    final int lOldTileX = (int)lPosX >> 2;
                    final int lOldTileY = (int)lPosY >> 2;
                    MeshIO lMesh;
                    if (this.isOnSurface()) {
                        lMesh = Server.surfaceMesh;
                    }
                    else {
                        lMesh = Server.caveMesh;
                    }
                    final float lMod2 = this.getMoveModifier(lMesh.getTile(lOldTileX, lOldTileY));
                    final float lXPosMod = (float)Math.sin(lRotation * 0.017453292f) * this.getSpeed() * lMod2;
                    final float lYPosMod = -(float)Math.cos(lRotation * 0.017453292f) * this.getSpeed() * lMod2;
                    final int lNewTileX = (int)(lPosX + lXPosMod) >> 2;
                    final int lNewTileY = (int)(lPosY + lYPosMod) >> 2;
                    final int lDiffTileX = lNewTileX - lOldTileX;
                    final int lDiffTileY = lNewTileY - lOldTileY;
                    if (Math.abs(lDiffTileX) > 1 || Math.abs(lDiffTileY) > 1) {
                        Creature.logger.log(Level.WARNING, this.getName() + "," + this.getWurmId() + " diffTileX=" + lDiffTileX + ", y=" + lDiffTileY);
                    }
                    if (lDiffTileX != 0 || lDiffTileY != 0) {
                        if (!this.isOnSurface() && Tiles.isSolidCave(Tiles.decodeType(lMesh.getTile(lNewTileX, lNewTileY)))) {
                            this.rotateRandom(lRotation, 45);
                            try {
                                this.takeSimpleStep();
                            }
                            catch (NoSuchPlayerException ex) {}
                            catch (NoSuchCreatureException ex2) {}
                            return true;
                        }
                        if (!this.isGhost()) {
                            final BlockingResult result = Blocking.getBlockerBetween(this, this.getPosX(), this.getPosY(), lPosX + lXPosMod, lPosY + lYPosMod, this.getPositionZ(), this.getPositionZ(), this.isOnSurface(), this.isOnSurface(), false, 6, true, -10L, this.getBridgeId(), this.getBridgeId(), this.followsGround());
                            if (result != null) {
                                boolean foundDoor = false;
                                for (final Blocker blocker : result.getBlockerArray()) {
                                    if (blocker.isDoor() && !blocker.canBeOpenedBy(this, false)) {
                                        foundDoor = true;
                                    }
                                }
                                if (!foundDoor) {
                                    path.clear();
                                    return true;
                                }
                            }
                        }
                        if (!next.hasSpecificPos() && next.getTileX() == lNewTileX && next.getTileY() == lNewTileY) {
                            path.removeFirst();
                        }
                        Creature.movesx += lDiffTileX;
                        Creature.movesy += lDiffTileY;
                    }
                    final float oldPosX = lPosX;
                    final float oldPosY = lPosY;
                    final float oldPosZ = lPosZ;
                    final int oldDeciZ = (int)(lPosZ * 10.0f);
                    lPosX += lXPosMod;
                    lPosY += lYPosMod;
                    if (lPosX >= Zones.worldTileSizeX - 1 << 2 || lPosX < 0.0f || lPosY < 0.0f || lPosY >= Zones.worldTileSizeY - 1 << 2) {
                        this.destroy();
                        return true;
                    }
                    lPosZ = this.calculatePosZ();
                    final int newDeciZ = (int)(lPosZ * 10.0f);
                    if (lPosZ < -0.5) {
                        if (this.isSubmerged()) {
                            if (this.isFloating() && newDeciZ > this.template.offZ * 10.0f) {
                                this.rotateRandom(lRotation, 100);
                                if (this.target != -10L) {
                                    this.setTarget(-10L, true);
                                }
                                return true;
                            }
                            if (!this.isFloating() && lPosZ > -5.0f && oldDeciZ < newDeciZ) {
                                this.rotateRandom(lRotation, 100);
                                if (this.target != -10L) {
                                    this.setTarget(-10L, true);
                                }
                                return true;
                            }
                            if (lPosZ < -5.0f) {
                                if (x == 3) {
                                    if (this.isFloating()) {
                                        lPosZ = this.template.offZ;
                                    }
                                    else {
                                        final float newdiff = Math.max(-1.0f, Math.min(1.0f, (float)Server.rand.nextGaussian()));
                                        final float newPosZ = lPosZ = Math.max(lPosZ, Math.min(-5.0f, this.getPositionZ() + newdiff));
                                    }
                                }
                            }
                            else if (x == 3 && this.isFloating()) {
                                lPosZ = this.template.offZ;
                            }
                        }
                        else {
                            lPosZ = Math.max(-1.25f, lPosZ);
                            if (this.isFloating()) {
                                lPosZ = Math.max(this.template.offZ, lPosZ);
                            }
                        }
                    }
                    this.status.setPositionX(lPosX);
                    this.status.setPositionY(lPosY);
                    this.status.setPositionZ(lPosZ);
                    this.status.setRotation(lRotation);
                    this.moved(lPosX - oldPosX, lPosY - oldPosY, lPosZ - oldPosZ, lDiffTileX, lDiffTileY);
                }
            }
            return path == null || (this.pathRecalcLength > 0 && path.getSize() <= this.pathRecalcLength) || path.isEmpty();
        }
        finally {}
    }
    
    protected boolean startDestroyingWall(final Wall wall) {
        try {
            BehaviourDispatcher.action(this, this.communicator, this.getEquippedWeapon((byte)14).getWurmId(), wall.getId(), (short)180);
        }
        catch (FailedException fe) {
            return true;
        }
        catch (NoSuchBehaviourException nsb) {
            Creature.logger.log(Level.WARNING, nsb.getMessage(), nsb);
            return true;
        }
        catch (NoSuchCreatureException nsc) {
            Creature.logger.log(Level.WARNING, nsc.getMessage(), nsc);
            return true;
        }
        catch (NoSuchItemException nsi) {
            Creature.logger.log(Level.WARNING, nsi.getMessage(), nsi);
            return true;
        }
        catch (NoSuchPlayerException nsp) {
            Creature.logger.log(Level.WARNING, nsp.getMessage(), nsp);
            return true;
        }
        catch (NoSuchWallException nsw) {
            Creature.logger.log(Level.WARNING, nsw.getMessage(), nsw);
            return true;
        }
        catch (NoSpaceException nsp2) {
            Creature.logger.log(Level.WARNING, nsp2.getMessage(), nsp2);
            return true;
        }
        return false;
    }
    
    public void followLeader() {
        final float iposx = this.leader.getStatus().getPositionX();
        final float iposy = this.leader.getStatus().getPositionY();
        final float diffx = iposx - this.status.getPositionX();
        final float diffy = iposy - this.status.getPositionY();
        final int diff = (int)Math.max(Math.abs(diffx), Math.abs(diffy));
        if (diffx < 0.0f && this.status.getPositionX() < 10.0f) {
            return;
        }
        if (diffy < 0.0f && this.status.getPositionY() < 10.0f) {
            return;
        }
        if (diffy > 0.0f && this.status.getPositionY() > Zones.worldMeterSizeY - 10.0f) {
            return;
        }
        if (diffx > 0.0f && this.status.getPositionX() > Zones.worldMeterSizeX - 10.0f) {
            return;
        }
        if (diff > 35) {
            Creature.logger.log(Level.INFO, this.leader.getName() + " moved " + diff + "diffx=" + diffx + ", diffy=" + diffy);
            this.setLeader(null);
        }
        else if (diffx > 4.0f || diffy > 4.0f || diffx < -4.0f || diffy < -4.0f) {
            final float lPosX = this.status.getPositionX();
            final float lPosY = this.status.getPositionY();
            final float lPosZ = this.status.getPositionZ();
            final int lOldTileX = (int)lPosX >> 2;
            final int lOldTileY = (int)lPosY >> 2;
            double lNewrot = Math.atan2(iposy - lPosY, iposx - lPosX);
            lNewrot = lNewrot * 57.29577951308232 + 90.0;
            if (lNewrot > 360.0) {
                lNewrot -= 360.0;
            }
            if (lNewrot < 0.0) {
                lNewrot += 360.0;
            }
            float movex = 0.0f;
            float movey = 0.0f;
            if (diffx < -4.0f) {
                movex = diffx + 4.0f;
            }
            else if (diffx > 4.0f) {
                movex = diffx - 4.0f;
            }
            if (diffy < -4.0f) {
                movey = diffy + 4.0f;
            }
            else if (diffy > 4.0f) {
                movey = diffy - 4.0f;
            }
            final float lXPosMod = (float)Math.sin(lNewrot * 0.01745329238474369) * Math.abs(movex + Server.rand.nextFloat());
            final float lYPosMod = -(float)Math.cos(lNewrot * 0.01745329238474369) * Math.abs(movey + Server.rand.nextFloat());
            float newPosX = lPosX + lXPosMod;
            float newPosY = lPosY + lYPosMod;
            final int lNewTileX = (int)newPosX >> 2;
            final int lNewTileY = (int)newPosY >> 2;
            final int lDiffTileX = lNewTileX - lOldTileX;
            final int lDiffTileY = lNewTileY - lOldTileY;
            if ((lDiffTileX != 0 || lDiffTileY != 0) && !this.isGhost() && this.leader.getBridgeId() < 0L && this.getBridgeId() < 0L) {
                final BlockingResult result = Blocking.getBlockerBetween(this, lPosX, lPosY, newPosX, newPosY, this.getPositionZ(), this.leader.getPositionZ(), this.isOnSurface(), this.isOnSurface(), false, 2, -1L, this.getBridgeId(), this.getBridgeId(), this.followsGround());
                if (result != null) {
                    final Blocker first = result.getFirstBlocker();
                    if (!first.isDoor()) {
                        this.leader.sendToLoggers("Your floor level " + this.leader.getFloorLevel() + ", creature: " + this.getFloorLevel());
                        this.setLeader(null);
                        return;
                    }
                    if (!first.canBeOpenedBy(this.leader, false) && !first.canBeOpenedBy(this, false)) {
                        this.leader.sendToLoggers("Your floor level " + this.leader.getFloorLevel() + ", creature: " + this.getFloorLevel());
                        this.setLeader(null);
                        return;
                    }
                }
            }
            if (!this.leader.isOnSurface() && !this.isOnSurface() && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile((int)newPosX >> 2, (int)newPosY >> 2)))) {
                newPosX = iposx;
                newPosY = iposy;
            }
            float newPosZ = this.calculatePosZ();
            if (!this.isSwimming() && newPosZ < -0.71 && newPosZ < lPosZ) {
                this.setLeader(null);
                this.status.setPositionZ(newPosZ);
            }
            else {
                newPosZ = Math.max(-1.25f, newPosZ);
                if (this.isFloating()) {
                    newPosZ = Math.max(this.template.offZ, newPosZ);
                }
                this.setRotation((float)lNewrot);
                final int tilex = (int)lPosX >> 2;
                final int tiley = (int)lPosY >> 2;
                final int newtilex = (int)newPosX >> 2;
                final int newtiley = (int)newPosY >> 2;
                this.status.setPositionX(newPosX);
                this.status.setPositionY(newPosY);
                this.status.setPositionZ(newPosZ);
                this.moved(newPosX - lPosX, newPosY - lPosY, newPosZ - lPosZ, newtilex - tilex, newtiley - tiley);
            }
        }
    }
    
    public void sendAttitudeChange() {
        if (this.currentTile != null) {
            this.currentTile.checkChangedAttitude(this);
        }
    }
    
    public final void takeSimpleStep() throws NoSuchCreatureException, NoSuchPlayerException {
        final long start = 0L;
        try {
            int mvs = 2;
            if (this.target != -10L) {
                mvs = 3;
            }
            if (this.getSize() >= 5) {
                mvs += 3;
            }
            for (int x = 0; x < mvs; ++x) {
                float lPosX = this.status.getPositionX();
                float lPosY = this.status.getPositionY();
                float lPosZ = this.status.getPositionZ();
                float lRotation = this.status.getRotation();
                final float oldPosX = lPosX;
                final float oldPosY = lPosY;
                final float oldPosZ = lPosZ;
                final int oldDeciZ = (int)(lPosZ * 10.0f);
                final int lOldTileX = (int)lPosX >> 2;
                final int lOldTileY = (int)lPosY >> 2;
                if (this.target == -10L) {
                    if (this.isOnSurface()) {
                        final int rand = Server.rand.nextInt(100);
                        if (rand < 10) {
                            final float lXPosMod = (float)Math.sin(lRotation * 0.017453292f) * 12.0f;
                            final float lYPosMod = -(float)Math.cos(lRotation * 0.017453292f) * 12.0f;
                            final int lNewTileX = Zones.safeTileX((int)(lPosX + lXPosMod) >> 2);
                            final int lNewTileY = Zones.safeTileY((int)(lPosY + lYPosMod) >> 2);
                            final int tile = Zones.getTileIntForTile(lNewTileX, lNewTileY, this.getLayer());
                            if (this.isTargetTileTooHigh(lNewTileX, lNewTileY, tile, lPosZ < 0.0f)) {
                                final short[] lLowestNode = this.getLowestTileCorner((short)lOldTileX, (short)lOldTileY);
                                this.turnTowardsTile(lLowestNode[0], lLowestNode[1]);
                            }
                        }
                        else if (rand < 12) {
                            this.rotateRandom(lRotation, 100);
                        }
                        else if (rand < 15) {
                            lRotation = normalizeAngle(lRotation + Server.rand.nextInt(100));
                        }
                    }
                    else {
                        final int rand = Server.rand.nextInt(100);
                        if (rand < 2) {
                            this.rotateRandom(lRotation, 100);
                        }
                        else if (rand < 5) {
                            lRotation = normalizeAngle(lRotation + Server.rand.nextInt(100));
                        }
                    }
                }
                else {
                    this.turnTowardsCreature(this.getTarget());
                }
                lRotation = normalizeAngle(lRotation);
                float lMoveModifier;
                if (!this.isOnSurface()) {
                    lMoveModifier = this.getMoveModifier(Server.caveMesh.getTile(lOldTileX, lOldTileY));
                }
                else {
                    lMoveModifier = this.getMoveModifier(Server.surfaceMesh.getTile(lOldTileX, lOldTileY));
                }
                final float lXPosMod = (float)Math.sin(lRotation * 0.017453292f) * this.getSpeed() * lMoveModifier;
                final float lYPosMod = -(float)Math.cos(lRotation * 0.017453292f) * this.getSpeed() * lMoveModifier;
                final int lNewTileX = (int)(lPosX + lXPosMod) >> 2;
                final int lNewTileY = (int)(lPosY + lYPosMod) >> 2;
                final int lDiffTileX = lNewTileX - lOldTileX;
                final int lDiffTileY = lNewTileY - lOldTileY;
                if ((lDiffTileX != 0 || lDiffTileY != 0) && !this.isGhost()) {
                    if (!this.isOnSurface()) {
                        if (Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(lOldTileX, lOldTileY)))) {
                            Creature.logger.log(Level.INFO, this.getName() + " is in rock at takesimplestep. Dying.");
                            this.die(false, "Suffocated in Rock");
                            return;
                        }
                        if (Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(lNewTileX, lNewTileY)))) {
                            if (this.currentTile.isTransition) {
                                this.sendToLoggers(lPosZ + " setting to surface then moving.");
                                if (!Tiles.isMineDoor(Tiles.decodeType(Server.caveMesh.getTile(this.getTileX(), this.getTileY()))) || MineDoorPermission.getPermission(this.getTileX(), this.getTileY()).mayPass(this)) {
                                    this.setLayer(0, true);
                                }
                                else {
                                    this.rotateRandom(lRotation, 45);
                                }
                                return;
                            }
                            this.rotateRandom(lRotation, 45);
                            return;
                        }
                    }
                    else if (Tiles.Tile.TILE_LAVA.id == Tiles.decodeType(Server.surfaceMesh.getTile(lNewTileX, lNewTileY))) {
                        this.rotateRandom(lRotation, 45);
                        return;
                    }
                    BlockingResult result;
                    if (Features.Feature.CREATURE_MOVEMENT_CHANGES.isEnabled()) {
                        result = Blocking.getBlockerBetween(this, lPosX, lPosY, lPosX + lXPosMod, lPosY + lYPosMod, this.getPositionZ(), this.getPositionZ(), this.isOnSurface(), this.isOnSurface(), false, 6, -1L, this.getBridgeId(), this.getBridgeId(), this.followsGround());
                    }
                    else {
                        result = Blocking.getBlockerBetween(this, lPosX, lPosY, lPosX + lXPosMod, lPosY + lYPosMod, this.getPositionZ(), this.getPositionZ(), this.isOnSurface(), this.isOnSurface(), false, 6, -1L, this.getBridgeId(), this.getBridgeId(), this.followsGround());
                    }
                    if (result != null) {
                        final Blocker first = result.getFirstBlocker();
                        if (this.isKingdomGuard() || this.isSpiritGuard()) {
                            if (!first.isDoor()) {
                                this.rotateRandom(lRotation, 100);
                                return;
                            }
                        }
                        else {
                            if (Features.Feature.CREATURE_MOVEMENT_CHANGES.isEnabled()) {
                                this.turnTowardsTile((short)this.getTileX(), (short)this.getTileY());
                                this.rotateRandom(this.status.getRotation(), 45);
                                x = 0;
                                this.getStatus().setMoving(false);
                                continue;
                            }
                            this.rotateRandom(lRotation, 100);
                            return;
                        }
                    }
                    final VolaTile t = Zones.getOrCreateTile(lNewTileX, lNewTileY, this.isOnSurface());
                    final VolaTile myt = this.getCurrentTile();
                    if ((t.isGuarded() && myt != null && !myt.isGuarded()) || (this.isAnimal() && t.hasFire())) {
                        this.rotateRandom(lRotation, 100);
                        return;
                    }
                }
                lPosX += lXPosMod;
                lPosY += lYPosMod;
                if (lPosX >= Zones.worldTileSizeX - 1 << 2 || lPosX < 0.0f || lPosY < 0.0f || lPosY >= Zones.worldTileSizeY - 1 << 2) {
                    this.destroy();
                    return;
                }
                if (this.getFloorLevel() == 0) {
                    try {
                        lPosZ = Zones.calculateHeight(lPosX, lPosY, this.isOnSurface());
                    }
                    catch (NoSuchZoneException nsz) {
                        Creature.logger.log(Level.WARNING, this.name + " moved out of zone.");
                    }
                    if (this.isFloating()) {
                        lPosZ = Math.max(this.template.offZ, lPosZ);
                    }
                    final int newDeciZ = (int)(lPosZ * 10.0f);
                    if (lPosZ < 0.5) {
                        if (this.isSubmerged()) {
                            if (this.isFloating() && newDeciZ > this.template.offZ * 10.0f) {
                                this.rotateRandom(lRotation, 100);
                                if (this.target != -10L) {
                                    this.setTarget(-10L, true);
                                }
                                return;
                            }
                            if (!this.isFloating() && lPosZ > -5.0f && oldDeciZ < newDeciZ) {
                                this.rotateRandom(lRotation, 100);
                                if (this.target != -10L) {
                                    this.setTarget(-10L, true);
                                }
                                return;
                            }
                            if (lPosZ < -5.0f) {
                                if (x == 3) {
                                    if (this.isFloating()) {
                                        lPosZ = this.template.offZ;
                                    }
                                    else {
                                        final float newdiff = Math.max(-1.0f, Math.min(1.0f, (float)Server.rand.nextGaussian()));
                                        final float newPosZ = lPosZ = Math.max(lPosZ, Math.min(-5.0f, this.getPositionZ() + newdiff));
                                    }
                                }
                            }
                            else if (x == 3 && this.isFloating()) {
                                lPosZ = this.template.offZ;
                            }
                        }
                        if ((lPosZ > -2.0f || oldDeciZ <= -20) && (oldDeciZ < 0 || this.target != -10L) && this.isSwimming()) {
                            lPosZ = Math.max(-1.25f, lPosZ);
                            if (this.isFloating()) {
                                lPosZ = Math.max(this.template.offZ, lPosZ);
                            }
                        }
                        else if (lPosZ < -0.5 && !this.isSubmerged()) {
                            this.rotateRandom(lRotation, 100);
                            if (this.target != -10L) {
                                this.setTarget(-10L, true);
                            }
                            return;
                        }
                    }
                    else if (this.isSubmerged() && oldDeciZ < newDeciZ) {
                        this.rotateRandom(lRotation, 100);
                        if (this.target != -10L) {
                            this.setTarget(-10L, true);
                        }
                        return;
                    }
                }
                this.status.setPositionX(lPosX);
                this.status.setPositionY(lPosY);
                if (Structure.isGroundFloorAtPosition(lPosX, lPosY, this.isOnSurface())) {
                    this.status.setPositionZ(lPosZ + 0.25f);
                }
                else {
                    this.status.setPositionZ(lPosZ);
                }
                this.status.setRotation(lRotation);
                this.moved(lPosX - oldPosX, lPosY - oldPosY, lPosZ - oldPosZ, lDiffTileX, lDiffTileY);
            }
        }
        finally {}
    }
    
    public void rotateRandom(float aRot, final int degrees) {
        aRot -= degrees;
        aRot += Server.rand.nextInt(degrees * 2);
        aRot = normalizeAngle(aRot);
        this.status.setRotation(aRot);
        this.moved(0.0f, 0.0f, 0.0f, 0, 0);
    }
    
    public int getAttackDistance() {
        return this.template.getSize();
    }
    
    public void moved(final float diffX, final float diffY, final float diffZ, final int aDiffTileX, final int aDiffTileY) {
        if (!this.isDead()) {
            try {
                if (this.isPlayer() || this.isWagoner()) {
                    this.movementScheme.move(diffX, diffY, diffZ);
                    if ((this.isWagoner() || this.hasLink()) && this.getVisionArea() != null) {
                        try {
                            this.getVisionArea().move(aDiffTileX, aDiffTileY);
                        }
                        catch (IOException iox) {
                            return;
                        }
                    }
                    try {
                        this.getCurrentTile().creatureMoved(this.id, diffX, diffY, diffZ, aDiffTileX, aDiffTileY);
                    }
                    catch (NoSuchPlayerException ex2) {}
                    catch (NoSuchCreatureException ex3) {}
                    if (this.hasLink() && this.getVisionArea() != null) {
                        this.getVisionArea().linkZones(aDiffTileX, aDiffTileY);
                    }
                }
                else {
                    try {
                        this.getVisionArea().move(aDiffTileX, aDiffTileY);
                    }
                    catch (IOException iox) {
                        return;
                    }
                    try {
                        this.getCurrentTile().creatureMoved(this.id, diffX, diffY, diffZ, aDiffTileX, aDiffTileY);
                    }
                    catch (NoSuchPlayerException ex4) {}
                    catch (NoSuchCreatureException ex5) {}
                    this.getVisionArea().linkZones(aDiffTileX, aDiffTileY);
                }
            }
            catch (NullPointerException ex) {
                try {
                    if (!this.isPlayer()) {
                        this.createVisionArea();
                    }
                    return;
                }
                catch (Exception ex6) {}
            }
            Label_0391: {
                if (diffX == 0.0f) {
                    if (diffY == 0.0f) {
                        break Label_0391;
                    }
                }
                try {
                    if (this.isPlayer() && this.actions.getCurrentAction().isInterruptedAtMove()) {
                        boolean stop = true;
                        if (this.actions.getCurrentAction().getNumber() == 136) {
                            this.getCommunicator().sendToggle(3, false);
                        }
                        else if ((this.actions.getCurrentAction().getNumber() == 329 || this.actions.getCurrentAction().getNumber() == 162 || this.actions.getCurrentAction().getNumber() == 160) && this.getVehicle() != -10L) {
                            stop = false;
                        }
                        if (stop) {
                            this.communicator.sendSafeServerMessage("You must not move while doing that.");
                            this.stopCurrentAction();
                        }
                    }
                }
                catch (NoSuchActionException ex7) {}
                if ((aDiffTileX != 0 || aDiffTileY != 0) && this.musicPlayer != null) {
                    this.musicPlayer.moveTile(this.getCurrentTileNum(), this.getPositionZ() <= 0.0f);
                }
            }
            if (this.status.isTrading()) {
                final Trade trade = this.status.getTrade();
                Creature lOpponent = null;
                if (trade.creatureOne == this) {
                    lOpponent = trade.creatureTwo;
                }
                else {
                    lOpponent = trade.creatureOne;
                }
                if (rangeTo(this, lOpponent) > 6) {
                    trade.end(this, false);
                }
            }
        }
    }
    
    public void stopFighting() {
        if (this.actions != null) {
            this.actions.removeAttacks(this);
        }
    }
    
    public void turnTowardsCreature(final Creature targ) {
        if (targ != null) {
            final double lNewrot = Math.atan2(targ.getStatus().getPositionY() - this.getStatus().getPositionY(), targ.getStatus().getPositionX() - this.getStatus().getPositionX());
            this.setRotation((float)(lNewrot * 57.29577951308232) + 90.0f);
            if (this.isSubmerged()) {
                try {
                    final float currFloor = Zones.calculateHeight(this.getPosX(), this.getPosY(), this.isOnSurface());
                    final float maxHeight = this.isFloating() ? this.template.offZ : Math.min(targ.getPositionZ(), Math.max(-5.0f, currFloor));
                    final float oldHeight = this.getPositionZ();
                    final int diffCentiZ = (int)((maxHeight - oldHeight) * 100.0f);
                    this.moved(0.0f, 0.0f, diffCentiZ, 0, 0);
                    return;
                }
                catch (NoSuchZoneException ex) {}
            }
            this.moved(0.0f, 0.0f, 0.0f, 0, 0);
        }
    }
    
    public void turnTowardsPoint(final float posX, final float posY) {
        final double lNewrot = Math.atan2(posY - this.getStatus().getPositionY(), posX - this.getStatus().getPositionX());
        this.setRotation((float)(lNewrot * 57.29577951308232) + 90.0f);
        this.moved(0.0f, 0.0f, 0.0f, 0, 0);
    }
    
    public void turnTowardsTile(final short tilex, final short tiley) {
        final double lNewrot = Math.atan2((tiley << 2) + 2 - this.getStatus().getPositionY(), (tilex << 2) + 2 - this.getStatus().getPositionX());
        this.setRotation((float)(lNewrot * 57.29577951308232) + 90.0f);
        this.moved(0.0f, 0.0f, 0.0f, 0, 0);
    }
    
    @Override
    public long getWurmId() {
        return this.id;
    }
    
    @Override
    public int getTemplateId() {
        return this.getTemplate().getTemplateId();
    }
    
    public String getNameWithoutPrefixes() {
        return this.name;
    }
    
    public String getNameWithoutFatStatus() {
        if (this.getStatus() != null) {
            return this.getStatus().getAgeString() + " " + this.getStatus().getTypeString() + this.name;
        }
        return "Unknown";
    }
    
    public String getName() {
        String fullName = this.name;
        if (this.isWagoner()) {
            return fullName;
        }
        if (this.isAnimal() || this.isMonster()) {
            if (this.name.toLowerCase().compareTo(this.template.getName().toLowerCase()) == 0) {
                fullName = this.getPrefixes() + this.name.toLowerCase();
            }
            else {
                fullName = this.getPrefixes() + StringUtilities.raiseFirstLetterOnly(this.name);
            }
        }
        if (this.petName.length() > 0) {
            return fullName + " '" + this.petName + "'";
        }
        return fullName;
    }
    
    public String getNamePossessive() {
        final String toReturn = this.getName();
        if (toReturn.endsWith("s")) {
            return toReturn + "'";
        }
        return toReturn + "'s";
    }
    
    public String getPrefixes() {
        if (this.isUnique()) {
            return "The " + this.getStatus().getAgeString() + " " + this.getStatus().getFatString() + this.getStatus().getTypeString();
        }
        return this.getStatus().getAgeString() + " " + this.getStatus().getFatString() + this.getStatus().getTypeString();
    }
    
    public void setName(final String _name) {
        this.name = _name;
    }
    
    public void setPetName(final String aPetName) {
        if (aPetName == null) {
            this.petName = "";
        }
        else {
            this.petName = aPetName.substring(0, Math.min(19, aPetName.length()));
        }
    }
    
    public String getColourName() {
        return this.template.getColourName(this.status);
    }
    
    public String getColourName(final int trait) {
        return this.template.getTemplateColourName(trait);
    }
    
    public CreatureStatus getStatus() {
        return this.status;
    }
    
    public VisionArea getVisionArea() {
        return this.visionArea;
    }
    
    public void trainSkill(final String sname) throws Exception {
        final Skill skill = this.skills.getSkill(sname);
        String message = this.getName() + " trains some " + sname + ", but learns nothing new.";
        final double knowledge = skill.getKnowledge(0.0);
        skill.skillCheck(50.0, 0.0, false, 3600.0f);
        if (skill.getKnowledge(0.0) > knowledge) {
            message = this.getName() + " trains some  " + sname + " and now have skill " + skill.getKnowledge(0.0);
        }
        Creature.logger.log(Level.INFO, message);
    }
    
    public void setSkill(final int skill, final float val) {
        try {
            final Skill sktomod = this.skills.getSkill(skill);
            sktomod.setKnowledge(val, false);
        }
        catch (NoSuchSkillException nss) {
            this.skills.learn(skill, val);
        }
    }
    
    public void sendSkills() {
        try {
            this.loadAffinities();
            final Map<Integer, Skill> skilltree = this.skills.getSkillTree();
            for (final Integer number : skilltree.keySet()) {
                try {
                    final Skill skill = skilltree.get(number);
                    final int[] needed = skill.getDependencies();
                    int parentSkillId = 0;
                    if (needed.length > 0) {
                        parentSkillId = needed[0];
                    }
                    if (parentSkillId != 0) {
                        final int parentType = SkillSystem.getTypeFor(parentSkillId);
                        if (parentType == 0) {
                            parentSkillId = Integer.MAX_VALUE;
                        }
                    }
                    else if (skill.getType() == 1) {
                        parentSkillId = 2147483646;
                    }
                    else {
                        parentSkillId = Integer.MAX_VALUE;
                    }
                    this.getCommunicator().sendAddSkill(number, parentSkillId, skill.getName(), (float)skill.getRealKnowledge(), (float)skill.getMinimumValue(), skill.affinity);
                }
                catch (NullPointerException np) {
                    Creature.logger.log(Level.WARNING, "Inconsistency: " + this.getName() + " forgetting skill with number " + (int)number, np);
                }
            }
        }
        catch (Exception ex2) {
            Creature.logger.log(Level.WARNING, "Failed to load and create skills for creature with name " + this.name + ":" + ex2.getMessage(), ex2);
        }
    }
    
    public void loadSkills() throws Exception {
        if (this.skills == null) {
            Creature.logger.log(Level.WARNING, "Skills object is null in creature " + this.name);
        }
        try {
            if (!this.isPlayer()) {
                if (this.skills.getId() != -10L) {
                    this.skills.initializeSkills();
                }
            }
            else if (!this.guest) {
                this.getCommunicator().sendAddSkill(2147483646, 0, "Characteristics", 0.0f, 0.0f, 0);
                this.getCommunicator().sendAddSkill(2147483643, 0, "Religion", 0.0f, 0.0f, 0);
                this.getCommunicator().sendAddSkill(Integer.MAX_VALUE, 0, "Skills", 0.0f, 0.0f, 0);
                this.skills.load();
            }
            boolean created = false;
            if (this.skills.isTemplate() || this.skills.getSkills().length == 0) {
                final Skills newSkills = SkillsFactory.createSkills(this.id);
                newSkills.clone(this.skills.getSkills());
                this.skills = newSkills;
                created = true;
                if (!this.guest) {
                    this.skills.save();
                }
                this.skills.addTempSkills();
            }
            if (created) {
                if (this.isUndead()) {
                    this.skills.learn(1023, 30.0f);
                    this.skills.learn(10052, 50.0f);
                    this.skills.getSkill(102).setKnowledge(25.0, false);
                    this.skills.getSkill(103).setKnowledge(25.0, false);
                }
                if (Servers.localServer.testServer && Servers.localServer.entryServer && WurmId.getType(this.id) == 0) {
                    int level = 20;
                    this.skills.learn(1023, level);
                    this.skills.learn(10025, level);
                    this.skills.learn(10006, level);
                    this.skills.learn(10023, level);
                    this.skills.learn(10022, level);
                    this.skills.learn(10020, level);
                    this.skills.learn(10021, level);
                    this.skills.learn(10019, level);
                    this.skills.learn(10001, level);
                    this.skills.learn(10024, level);
                    this.skills.learn(10005, level);
                    this.skills.learn(10027, level);
                    this.skills.learn(10028, level);
                    this.skills.learn(10026, level);
                    this.skills.learn(10064, level);
                    this.skills.learn(10061, level);
                    this.skills.learn(10062, level);
                    this.skills.learn(10063, level);
                    this.skills.learn(1002, level / 2.0f);
                    this.skills.learn(1003, level / 2.0f);
                    this.skills.learn(10056, level);
                    this.skills.getSkill(104).setKnowledge(23.0, false);
                    this.skills.getSkill(1).setKnowledge(3.0, false);
                    this.skills.getSkill(102).setKnowledge(23.0, false);
                    this.skills.getSkill(103).setKnowledge(23.0, false);
                    this.skills.learn(10053, level);
                    this.skills.learn(10054, level);
                    level = (int)(Server.rand.nextFloat() * 100.0f);
                    this.skills.learn(1030, level);
                    this.skills.learn(10081, level);
                    this.skills.learn(10079, level);
                    this.skills.learn(10080, level);
                }
            }
            this.setMoveLimits();
        }
        catch (Exception ex2) {
            Creature.logger.log(Level.WARNING, "Failed to load and create skills for creature with name " + this.name + ":" + ex2.getMessage(), ex2);
        }
    }
    
    public void addStructureTile(final VolaTile toAdd, final byte structureType) {
        if (this.structure == null) {
            this.structure = Structures.createStructure(structureType, this.name + "'s planned structure", WurmId.getNextPlanId(), toAdd.tilex, toAdd.tiley, this.isOnSurface());
            this.status.setBuildingId(this.structure.getWurmId());
        }
        else {
            try {
                this.structure.addBuildTile(toAdd, false);
                if (structureType == 0) {
                    this.structure.clearAllWallsAndMakeWallsForStructureBorder(toAdd);
                }
            }
            catch (NoSuchZoneException nsz) {
                this.getCommunicator().sendNormalServerMessage("You can't build there.", (byte)3);
            }
        }
    }
    
    public long getBuildingId() {
        return this.status.buildingId;
    }
    
    public String getUndeadModelName() {
        if (this.getUndeadType() == 1) {
            if (this.status.sex == 0) {
                return "model.creature.humanoid.human.player.zombie.male" + WurmCalendar.getSpecialMapping(true);
            }
            if (this.status.sex == 1) {
                return "model.creature.humanoid.human.player.zombie.female" + WurmCalendar.getSpecialMapping(true);
            }
            return "model.creature.humanoid.human.player.zombie" + WurmCalendar.getSpecialMapping(true);
        }
        else {
            if (this.getUndeadType() == 2) {
                return "model.creature.humanoid.human.skeleton" + WurmCalendar.getSpecialMapping(true);
            }
            if (this.getUndeadType() == 3) {
                return "model.creature.humanoid.human.spirit.shadow" + WurmCalendar.getSpecialMapping(true);
            }
            return this.getModelName();
        }
    }
    
    public String getModelName() {
        if (this.isReborn()) {
            if (this.status.sex == 0) {
                return this.template.getModelName() + ".zombie.male" + WurmCalendar.getSpecialMapping(true);
            }
            if (this.status.sex == 1) {
                return this.template.getModelName() + ".zombie.female" + WurmCalendar.getSpecialMapping(true);
            }
            return this.template.getModelName() + ".zombie" + WurmCalendar.getSpecialMapping(true);
        }
        else {
            if (this.template.isHorse || this.template.isColoured) {
                final String col = this.template.getModelColourName(this.status);
                final StringBuilder s = new StringBuilder();
                s.append(this.template.getModelName());
                s.append('.');
                s.append(col.toLowerCase());
                if (this.status.sex == 0) {
                    s.append(".male");
                }
                if (this.status.sex == 1) {
                    s.append(".female");
                }
                if (this.status.disease > 0) {
                    s.append(".diseased");
                }
                s.append(WurmCalendar.getSpecialMapping(true));
                return s.toString();
            }
            if (this.template.isBlackOrWhite) {
                final StringBuilder s2 = new StringBuilder();
                s2.append(this.template.getModelName());
                if (this.status.sex == 0) {
                    s2.append(".male");
                }
                if (this.status.sex == 1) {
                    s2.append(".female");
                }
                if (!this.hasTrait(15) && !this.hasTrait(16) && !this.hasTrait(18) && !this.hasTrait(24) && !this.hasTrait(25) && !this.hasTrait(23) && !this.hasTrait(30) && !this.hasTrait(31) && !this.hasTrait(32) && !this.hasTrait(33)) {
                    if (!this.hasTrait(34)) {
                        if (this.hasTrait(17)) {
                            s2.append(".black");
                        }
                    }
                }
                if (this.status.disease > 0) {
                    s2.append(".diseased");
                }
                s2.append(WurmCalendar.getSpecialMapping(true));
                return s2.toString();
            }
            if (this.template.getTemplateId() == 119) {
                final StringBuilder s2 = new StringBuilder();
                final FishAI.FishAIData faid = (FishAI.FishAIData)this.getCreatureAIData();
                final FishEnums.FishData fd = faid.getFishData();
                s2.append(fd.getModelName());
                return s2.toString();
            }
            final StringBuilder s2 = new StringBuilder();
            s2.append(this.template.getModelName());
            if (this.status.sex == 0) {
                s2.append(".male");
            }
            if (this.status.sex == 1) {
                s2.append(".female");
            }
            if (this.getKingdomId() != 0) {
                s2.append('.');
                final Kingdom kingdomt = Kingdoms.getKingdom(this.getKingdomId());
                if (kingdomt.getTemplate() != this.getKingdomId()) {
                    s2.append(Kingdoms.getSuffixFor(kingdomt.getTemplate()));
                }
                s2.append(Kingdoms.getSuffixFor(this.getKingdomId()));
                if (this.status.disease > 0) {
                    s2.append("diseased.");
                }
            }
            else {
                s2.append('.');
                if (this.status.disease > 0) {
                    s2.append("diseased.");
                }
            }
            s2.append(WurmCalendar.getSpecialMapping(false));
            return s2.toString();
        }
    }
    
    public String getHitSound() {
        return this.template.getHitSound(this.getSex());
    }
    
    public String getDeathSound() {
        return this.template.getDeathSound(this.getSex());
    }
    
    public final boolean hasNoServerSound() {
        return this.template.noServerSounds();
    }
    
    public void setStructure(@Nullable final Structure struct) {
        if (struct == null) {
            this.status.setBuildingId(-10L);
        }
        this.structure = struct;
    }
    
    public float getNoticeChance() {
        if (this.template.getTemplateId() == 29 || this.template.getTemplateId() == 28 || this.template.getTemplateId() == 4) {
            return 0.2f;
        }
        if (this.template.getTemplateId() == 5) {
            return 0.3f;
        }
        if (this.template.getTemplateId() == 31 || this.template.getTemplateId() == 30 || this.template.getTemplateId() == 6) {
            return 0.4f;
        }
        if (this.template.getTemplateId() == 7) {
            return 0.6f;
        }
        if (this.template.getTemplateId() == 33 || this.template.getTemplateId() == 32 || this.template.getTemplateId() == 8) {
            return 0.65f;
        }
        return 1.0f;
    }
    
    public Structure getStructure() throws NoSuchStructureException {
        if (this.structure == null) {
            throw new NoSuchStructureException("This creature has no structure");
        }
        return this.structure;
    }
    
    public boolean hasLink() {
        return false;
    }
    
    public short getCentimetersLong() {
        return this.status.getBody().getCentimetersLong();
    }
    
    public short getCentimetersHigh() {
        return this.status.getBody().getCentimetersHigh();
    }
    
    public short getCentimetersWide() {
        return this.status.getBody().getCentimetersWide();
    }
    
    public void setCentimetersLong(final short centimetersLong) {
        this.status.getBody().setCentimetersLong(centimetersLong);
    }
    
    public void setCentimetersHigh(final short centimetersHigh) {
        this.status.getBody().setCentimetersHigh(centimetersHigh);
    }
    
    public void setCentimetersWide(final short centimetersWide) {
        this.status.getBody().setCentimetersWide(centimetersWide);
    }
    
    public float getWeight() {
        return this.status.getBody().getWeight(this.getStatus().fat);
    }
    
    public int getSize() {
        return this.template.getSize();
    }
    
    public boolean isClimber() {
        return this.template.climber;
    }
    
    public boolean addItemWatched(final Item watched) {
        return true;
    }
    
    public boolean removeItemWatched(final Item watched) {
        return true;
    }
    
    public boolean isItemWatched(final Item watched) {
        return true;
    }
    
    public boolean isPaying() {
        return true;
    }
    
    public boolean isReallyPaying() {
        return true;
    }
    
    public int getPower() {
        return 0;
    }
    
    public void dropItem(final Item item) {
        final long parentId = item.getParentId();
        item.setPosXY(this.getPosX(), this.getPosY());
        if (parentId != -10L) {
            try {
                final Item parent = Items.getItem(parentId);
                parent.dropItem(item.getWurmId(), false);
            }
            catch (Exception ex) {
                Creature.logger.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        final int tilex = this.getTileX();
        final int tiley = this.getTileY();
        try {
            final Zone zone = Zones.getZone(tilex, tiley, this.isOnSurface());
            VolaTile t = zone.getOrCreateTile(tilex, tiley);
            if (t != null) {
                t.addItem(item, false, false);
            }
            else {
                final int x = Server.rand.nextInt(Zones.worldTileSizeX);
                final int y = Server.rand.nextInt(Zones.worldTileSizeY);
                t = Zones.getOrCreateTile(x, y, true);
                t.addItem(item, false, false);
            }
        }
        catch (NoSuchZoneException nsz) {
            Creature.logger.log(Level.WARNING, nsz.getMessage(), nsz);
        }
    }
    
    public void setDestroyed() {
        if (this.decisions != null) {
            this.decisions.clearOrders();
            this.decisions = null;
        }
        this.getStatus().setPath(null);
        try {
            this.savePosition(-10);
        }
        catch (IOException iox) {
            Creature.logger.log(Level.WARNING, iox.getMessage(), iox);
        }
        this.damageCounter = 0;
        this.status.dead = true;
        this.setLeader(null);
        if (this.followers != null) {
            this.stopLeading();
        }
        if (this.isTrading()) {
            this.getTrade().end(this, true);
        }
        this.setTarget(-10L, true);
        this.destroyVisionArea();
        if (this.isVehicle()) {
            Vehicles.destroyVehicle(this.getWurmId());
        }
    }
    
    public void destroy() {
        if (this.isDominated()) {
            this.setDominator(-10L);
        }
        this.getCurrentTile().deleteCreature(this);
        this.setDestroyed();
        if (this.getSpellEffects() != null) {
            this.getSpellEffects().destroy(false);
        }
        try {
            this.skills.delete();
        }
        catch (Exception ex) {
            Creature.logger.log(Level.INFO, "Error when deleting creature skills: " + ex.getMessage(), ex);
        }
        try {
            final Item[] items = this.possessions.getInventory().getAllItems(true);
            for (int x = 0; x < items.length; ++x) {
                if (!items[x].isUnique()) {
                    Items.destroyItem(items[x].getWurmId());
                }
                else {
                    this.dropItem(items[x]);
                }
            }
            Items.destroyItem(this.possessions.getInventory().getWurmId());
        }
        catch (Exception e) {
            Creature.logger.log(Level.INFO, "Error when decaying items: " + e.getMessage(), e);
        }
        try {
            final Item[] items = this.getBody().getBodyItem().getAllItems(true);
            for (int x = 0; x < items.length; ++x) {
                if (!items[x].isUnique()) {
                    Items.destroyItem(items[x].getWurmId());
                }
                else {
                    this.dropItem(items[x]);
                }
            }
            Items.destroyItem(this.getBody().getBodyItem().getWurmId());
        }
        catch (Exception e) {
            Creature.logger.log(Level.INFO, "Error when decaying body items: " + e.getMessage(), e);
        }
        if (this.citizenVillage != null) {
            final Village vill = this.citizenVillage;
            final Guard[] guards2;
            final Guard[] guards = guards2 = this.citizenVillage.getGuards();
            for (final Guard lGuard : guards2) {
                if (lGuard.getCreature() == this) {
                    vill.deleteGuard(this, false);
                    if (this.isSpiritGuard()) {
                        vill.plan.destroyGuard(this);
                    }
                }
            }
            final Wagoner[] wagoners2;
            final Wagoner[] wagoners = wagoners2 = vill.getWagoners();
            for (final Wagoner wagoner : wagoners2) {
                if (wagoner.getWurmId() == this.getWurmId()) {
                    vill.deleteWagoner(this);
                }
            }
        }
        if (this.isNpcTrader() && Economy.getEconomy().getShop(this, true) != null) {
            if (Economy.getEconomy().getShop(this, true).getMoney() > 0L) {
                Economy.getEconomy().getKingsShop().setMoney(Economy.getEconomy().getKingsShop().getMoney() + Economy.getEconomy().getShop(this, true).getMoney());
            }
            Economy.deleteShop(this.id);
        }
        if (this.isKingdomGuard()) {
            final GuardTower tower = Kingdoms.getTower(this);
            if (tower != null) {
                try {
                    tower.destroyGuard(this);
                }
                catch (IOException iox) {
                    Creature.logger.log(Level.WARNING, iox.getMessage(), iox);
                }
            }
        }
        Creatures.getInstance().permanentlyDelete(this);
    }
    
    public boolean isBreakFence() {
        return this.template.isBreakFence();
    }
    
    public boolean isCareful() {
        return this.template.isCareful();
    }
    
    public final void attackTower() {
        if (this.isOnSurface() && !this.isFriendlyKingdom(this.getCurrentKingdom())) {
            for (int x = Zones.safeTileX(this.getTileX() - 3); x < Zones.safeTileX(this.getTileX() + 3); ++x) {
                for (int y = Zones.safeTileY(this.getTileY() - 3); y < Zones.safeTileY(this.getTileY() + 3); ++y) {
                    final VolaTile t = Zones.getTileOrNull(x, y, this.isOnSurface());
                    if (t != null) {
                        final Item[] items2;
                        final Item[] items = items2 = t.getItems();
                        for (final Item i : items2) {
                            if (i.isGuardTower() && !this.isFriendlyKingdom(i.getKingdom())) {
                                final GuardTower tower = Kingdoms.getTower(i);
                                if (i.getCurrentQualityLevel() > 50.0f) {
                                    if (tower != null) {
                                        tower.sendAttackWarning();
                                    }
                                    this.turnTowardsTile((short)i.getTileX(), (short)i.getTileY());
                                    this.playAnimation("fight_strike", false);
                                    Server.getInstance().broadCastAction(this.getName() + " attacks the " + i.getName() + ".", this, 5);
                                    i.setDamage(i.getDamage() + (float)(this.getStrengthSkill() / 1000.0));
                                    if (Server.rand.nextInt(300) == 0) {
                                        if (Server.rand.nextBoolean()) {
                                            GuardTower.spawnCommander(i, i.getKingdom());
                                        }
                                        for (int n = 0; n < 2 + Server.rand.nextInt(4); ++n) {
                                            GuardTower.spawnSoldier(i, i.getKingdom());
                                        }
                                    }
                                }
                                else if (!Servers.localServer.HOMESERVER && Server.rand.nextInt(300) == 0 && tower != null && !tower.hasLiveGuards()) {
                                    Server.getInstance().broadCastAction(this.getName() + " conquers the " + tower.getName() + "!", this, 5);
                                    Server.getInstance().broadCastSafe(this.getName() + " conquers " + tower.getName() + ".");
                                    Kingdoms.convertTowersWithin(i.getTileX() - 10, i.getTileY() - 10, i.getTileX() + 10, i.getTileY() + 10, this.getKingdomId());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void breakout() {
        if (!this.isDominated() && (((this.isCaveDweller() || this.isBreakFence()) && this.status.hunger >= 60000) || this.isUnique()) && !this.isSubmerged() && Server.rand.nextInt(100) == 0) {
            final Village breakoutVillage = Zones.getVillage(this.getTileX(), this.getTileY(), this.isOnSurface());
            if (breakoutVillage != null && breakoutVillage.isPermanent) {
                return;
            }
            if (this.isBreakFence() && this.currentTile != null) {
                if (this.currentTile.getStructure() != null) {
                    final Wall[] walls = this.currentTile.getWallsForLevel(this.getFloorLevel());
                    if (walls.length > 0) {
                        final Wall tobreak = walls[Server.rand.nextInt(walls.length)];
                        if (!tobreak.isIndestructible()) {
                            Server.getInstance().broadCastAction("The " + this.getName() + " smashes the " + tobreak.getName() + ".", this, 5);
                            if (this.isUnique()) {
                                tobreak.setDamage(tobreak.getDamage() + 100.0f);
                            }
                            else {
                                tobreak.setDamage(tobreak.getDamage() + (float)this.getStrengthSkill() / 10.0f * tobreak.getDamageModifier());
                            }
                        }
                    }
                }
                final boolean onSurface = true;
                if ((this.isOnSurface() || this.currentTile.isTransition) && this.isUnique()) {
                    VolaTile t = Zones.getTileOrNull(this.getTileX() + 1, this.getTileY(), true);
                    if (t != null) {
                        final Wall[] walls2 = t.getWallsForLevel(Math.max(0, this.getFloorLevel()));
                        if (walls2.length > 0) {
                            for (final Wall tobreak2 : walls2) {
                                if (!tobreak2.isIndestructible()) {
                                    if (tobreak2.getTileX() == this.getTileX() + 1 && !tobreak2.isHorizontal()) {
                                        Server.getInstance().broadCastAction("The " + this.getName() + " smashes the " + tobreak2.getName() + ".", this, 5);
                                        if (this.isUnique()) {
                                            tobreak2.setDamage(tobreak2.getDamage() + 100.0f);
                                        }
                                        else {
                                            tobreak2.setDamage(tobreak2.getDamage() + (float)this.getStrengthSkill() / 10.0f * tobreak2.getDamageModifier());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    t = Zones.getTileOrNull(this.getTileX() - 1, this.getTileY(), true);
                    if (t != null) {
                        final Wall[] walls2 = t.getWallsForLevel(Math.max(0, this.getFloorLevel()));
                        if (walls2.length > 0) {
                            for (final Wall tobreak2 : walls2) {
                                if (!tobreak2.isIndestructible()) {
                                    if (tobreak2.getTileX() == this.getTileX() && !tobreak2.isHorizontal()) {
                                        Server.getInstance().broadCastAction("The " + this.getName() + " smashes the " + tobreak2.getName() + ".", this, 5);
                                        if (this.isUnique()) {
                                            tobreak2.setDamage(tobreak2.getDamage() + 100.0f);
                                        }
                                        else {
                                            tobreak2.setDamage(tobreak2.getDamage() + (float)this.getStrengthSkill() / 10.0f * tobreak2.getDamageModifier());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    t = Zones.getTileOrNull(this.getTileX(), this.getTileY() - 1, true);
                    if (t != null) {
                        final Wall[] walls2 = t.getWallsForLevel(Math.max(0, this.getFloorLevel()));
                        if (walls2.length > 0) {
                            for (final Wall tobreak2 : walls2) {
                                if (!tobreak2.isIndestructible()) {
                                    if (tobreak2.getTileY() == this.getTileY() && tobreak2.isHorizontal()) {
                                        Server.getInstance().broadCastAction("The " + this.getName() + " smashes the " + tobreak2.getName() + ".", this, 5);
                                        if (this.isUnique()) {
                                            tobreak2.setDamage(tobreak2.getDamage() + 100.0f);
                                        }
                                        else {
                                            tobreak2.setDamage(tobreak2.getDamage() + (float)this.getStrengthSkill() / 10.0f * tobreak2.getDamageModifier());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    t = Zones.getTileOrNull(this.getTileX(), this.getTileY() + 1, true);
                    if (t != null) {
                        final Wall[] walls2 = t.getWallsForLevel(Math.max(0, this.getFloorLevel()));
                        if (walls2.length > 0) {
                            for (final Wall tobreak2 : walls2) {
                                if (!tobreak2.isIndestructible()) {
                                    if (tobreak2.getTileY() == this.getTileY() + 1 && tobreak2.isHorizontal()) {
                                        Server.getInstance().broadCastAction("The " + this.getName() + " smashes the " + tobreak2.getName() + ".", this, 5);
                                        if (this.isUnique()) {
                                            tobreak2.setDamage(tobreak2.getDamage() + 100.0f);
                                        }
                                        else {
                                            tobreak2.setDamage(tobreak2.getDamage() + (float)this.getStrengthSkill() / 10.0f * tobreak2.getDamageModifier());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Fence[] fences = this.currentTile.getFencesForLevel(this.currentTile.isTransition ? 0 : this.getFloorLevel());
                boolean onlyHoriz = false;
                boolean onlyVert = false;
                if (fences == null) {
                    if (this.isOnSurface()) {
                        if (fences == null) {
                            final VolaTile t2 = Zones.getTileOrNull(this.currentTile.getTileX() + 1, this.currentTile.getTileY(), true);
                            if (t2 != null) {
                                fences = t2.getFencesForLevel(this.getFloorLevel());
                                onlyVert = true;
                            }
                        }
                        if (fences == null) {
                            final VolaTile t2 = Zones.getTileOrNull(this.currentTile.getTileX(), this.currentTile.getTileY() + 1, true);
                            if (t2 != null) {
                                fences = t2.getFencesForLevel(this.getFloorLevel());
                                onlyHoriz = true;
                            }
                        }
                    }
                    if (this.currentTile.isTransition) {
                        if (!this.isOnSurface()) {
                            VolaTile t2 = Zones.getTileOrNull(this.currentTile.getTileX(), this.currentTile.getTileY(), true);
                            if (t2 != null) {
                                fences = t2.getFencesForLevel(Math.max(0, this.getFloorLevel()));
                            }
                            if (fences == null) {
                                t2 = Zones.getTileOrNull(this.currentTile.getTileX() + 1, this.currentTile.getTileY(), true);
                                if (t2 != null) {
                                    fences = t2.getFencesForLevel(Math.max(0, this.getFloorLevel()));
                                    onlyVert = true;
                                }
                            }
                            if (fences == null) {
                                t2 = Zones.getTileOrNull(this.currentTile.getTileX(), this.currentTile.getTileY() + 1, true);
                                if (t2 != null) {
                                    fences = t2.getFencesForLevel(Math.max(0, this.getFloorLevel()));
                                    onlyHoriz = true;
                                }
                            }
                        }
                        if (this.getFloorLevel() <= 0 && Tiles.isMineDoor(Tiles.decodeType(Zones.getTileIntForTile(this.currentTile.tilex, this.currentTile.tiley, 0)))) {
                            int currQl = Server.getWorldResource(this.currentTile.tilex, this.currentTile.tiley);
                            final int damage = 1000;
                            currQl = Math.max(0, currQl - 1000);
                            Server.setWorldResource(this.currentTile.tilex, this.currentTile.tiley, currQl);
                            try {
                                MethodsStructure.sendDestroySound(this, this.getBody().getBodyPart(13), Tiles.decodeType(Server.surfaceMesh.getTile(this.currentTile.tilex, this.currentTile.tiley)) == 25);
                            }
                            catch (Exception ex) {
                                Creature.logger.log(Level.INFO, this.getName() + ex.getMessage());
                            }
                            if (currQl == 0) {
                                TileEvent.log(this.currentTile.tilex, this.currentTile.tiley, 0, this.getWurmId(), 174);
                                TileEvent.log(this.currentTile.tilex, this.currentTile.tiley, -1, this.getWurmId(), 174);
                                if (Tiles.decodeType(Server.caveMesh.getTile(this.currentTile.tilex, this.currentTile.tiley)) == Tiles.Tile.TILE_CAVE_EXIT.id) {
                                    Server.setSurfaceTile(this.currentTile.tilex, this.currentTile.tiley, Tiles.decodeHeight(Server.surfaceMesh.getTile(this.currentTile.tilex, this.currentTile.tiley)), Tiles.Tile.TILE_HOLE.id, (byte)0);
                                }
                                else {
                                    Server.setSurfaceTile(this.currentTile.tilex, this.currentTile.tiley, Tiles.decodeHeight(Server.surfaceMesh.getTile(this.currentTile.tilex, this.currentTile.tiley)), Tiles.Tile.TILE_ROCK.id, (byte)0);
                                }
                                Players.getInstance().sendChangedTile(this.currentTile.tilex, this.currentTile.tiley, true, true);
                                MineDoorPermission.deleteMineDoor(this.currentTile.tilex, this.currentTile.tiley);
                                Server.getInstance().broadCastAction(this.getName() + " damages a door and the last parts fall down with a crash.", this, 5);
                            }
                            else {
                                Server.getInstance().broadCastAction(this.getName() + " damages the door.", this, 5);
                            }
                        }
                    }
                }
                if (fences != null) {
                    for (final Fence f : fences) {
                        if (!f.isIndestructible()) {
                            if (f.isHorizontal()) {
                                if (!onlyVert) {
                                    Server.getInstance().broadCastAction("The " + this.getName() + " smashes the " + f.getName() + ".", this, 5);
                                    if (this.isUnique()) {
                                        f.setDamage(f.getDamage() + Server.rand.nextInt(100));
                                    }
                                    else {
                                        if (f.getVillage() != null) {
                                            f.getVillage().addTarget(this);
                                        }
                                        f.setDamage(f.getDamage() + (float)this.getStrengthSkill() / 10.0f * f.getDamageModifier());
                                    }
                                }
                            }
                            else if (!onlyHoriz) {
                                Server.getInstance().broadCastAction("The " + this.getName() + " smashes the " + f.getName() + ".", this, 5);
                                if (this.isUnique()) {
                                    f.setDamage(f.getDamage() + Server.rand.nextInt(100));
                                }
                                else {
                                    if (f.getVillage() != null) {
                                        f.getVillage().addTarget(this);
                                    }
                                    f.setDamage(f.getDamage() + (float)this.getStrengthSkill() / 10.0f * f.getDamageModifier());
                                }
                            }
                        }
                    }
                }
            }
            if (this.isUnique() && !this.isOnSurface() && Server.rand.nextInt(500) == 0) {
                final boolean breakReinforcement = this.isUnique();
                int tx = Zones.safeTileX(this.getTileX() - 1);
                int ty = Zones.safeTileY(this.getTileY());
                int t3 = Zones.getTileIntForTile(tx, ty, 0);
                if (Tiles.isMineDoor(Tiles.decodeType(t3))) {
                    int currQl = Server.getWorldResource(tx, ty);
                    try {
                        MethodsStructure.sendDestroySound(this, this.getBody().getBodyPart(13), Tiles.decodeType(Server.surfaceMesh.getTile(tx, ty)) == 25);
                        currQl = Math.max(0, currQl - 1000);
                        Server.setWorldResource(tx, ty, currQl);
                        if (currQl == 0) {
                            TileEvent.log(tx, ty, 0, this.getWurmId(), 174);
                            TileEvent.log(tx, ty, -1, this.getWurmId(), 174);
                            if (Tiles.decodeType(Server.caveMesh.getTile(tx, ty)) == Tiles.Tile.TILE_CAVE_EXIT.id) {
                                Server.setSurfaceTile(tx, ty, Tiles.decodeHeight(Server.surfaceMesh.getTile(tx, ty)), Tiles.Tile.TILE_HOLE.id, (byte)0);
                            }
                            else {
                                Server.setSurfaceTile(tx, ty, Tiles.decodeHeight(Server.surfaceMesh.getTile(tx, ty)), Tiles.Tile.TILE_ROCK.id, (byte)0);
                            }
                            Players.getInstance().sendChangedTile(tx, ty, true, true);
                            MineDoorPermission.deleteMineDoor(tx, ty);
                            Server.getInstance().broadCastAction(this.getNameWithGenus() + " damages a door and the last parts fall down with a crash.", this, 5);
                        }
                    }
                    catch (Exception ex2) {
                        Creature.logger.log(Level.WARNING, ex2.getMessage());
                    }
                }
                t3 = Zones.getTileIntForTile(tx, ty, -1);
                if (breakReinforcement && Tiles.decodeType(t3) == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id) {
                    Server.caveMesh.setTile(tx, ty, Tiles.encode(Tiles.decodeHeight(t3), Tiles.Tile.TILE_CAVE_WALL.id, Tiles.decodeData(t3)));
                    Players.getInstance().sendChangedTile(tx, ty, false, true);
                }
                if (Tiles.decodeType(t3) == Tiles.Tile.TILE_CAVE_WALL.id) {
                    final Village v = Zones.getVillage(tx, ty, true);
                    if (v == null || this.isOnPvPServer() || this.isUnique()) {
                        TileRockBehaviour.createInsideTunnel(tx, ty, t3, this, 145 + Server.rand.nextInt(3), 2, false, null);
                        if (v != null) {
                            v.addTarget(this);
                        }
                    }
                }
                tx = Zones.safeTileX(this.getTileX());
                ty = Zones.safeTileY(this.getTileY() - 1);
                t3 = Zones.getTileIntForTile(tx, ty, -1);
                if (breakReinforcement && Tiles.decodeType(t3) == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id) {
                    Server.caveMesh.setTile(tx, ty, Tiles.encode(Tiles.decodeHeight(t3), Tiles.Tile.TILE_CAVE_WALL.id, Tiles.decodeData(t3)));
                    Players.getInstance().sendChangedTile(tx, ty, false, true);
                }
                if (Tiles.decodeType(t3) == Tiles.Tile.TILE_CAVE_WALL.id) {
                    final Village v = Zones.getVillage(tx, ty, true);
                    if (v == null || this.isOnPvPServer() || this.isUnique()) {
                        TileRockBehaviour.createInsideTunnel(tx, ty, t3, this, 145 + Server.rand.nextInt(3), 3, false, null);
                        if (v != null) {
                            v.addTarget(this);
                        }
                    }
                }
                tx = Zones.safeTileX(this.getTileX() + 1);
                ty = Zones.safeTileY(this.getTileY());
                t3 = Zones.getTileIntForTile(tx, ty, -1);
                if (breakReinforcement && Tiles.decodeType(t3) == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id) {
                    Server.caveMesh.setTile(tx, ty, Tiles.encode(Tiles.decodeHeight(t3), Tiles.Tile.TILE_CAVE_WALL.id, Tiles.decodeData(t3)));
                    Players.getInstance().sendChangedTile(tx, ty, false, true);
                }
                if (Tiles.decodeType(t3) == Tiles.Tile.TILE_CAVE_WALL.id) {
                    final Village v = Zones.getVillage(tx, ty, true);
                    if (v == null || this.isOnPvPServer() || this.isUnique()) {
                        TileRockBehaviour.createInsideTunnel(tx, ty, t3, this, 145 + Server.rand.nextInt(3), 4, false, null);
                        if (v != null) {
                            v.addTarget(this);
                        }
                    }
                }
                tx = Zones.safeTileX(this.getTileX());
                ty = Zones.safeTileY(this.getTileY() + 1);
                t3 = Zones.getTileIntForTile(tx, ty, -1);
                if (breakReinforcement && Tiles.decodeType(t3) == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id) {
                    Server.caveMesh.setTile(tx, ty, Tiles.encode(Tiles.decodeHeight(t3), Tiles.Tile.TILE_CAVE_WALL.id, Tiles.decodeData(t3)));
                    Players.getInstance().sendChangedTile(tx, ty, false, true);
                }
                if (Tiles.decodeType(t3) == Tiles.Tile.TILE_CAVE_WALL.id) {
                    final Village v = Zones.getVillage(tx, ty, true);
                    if (v == null || this.isOnPvPServer() || this.isUnique()) {
                        TileRockBehaviour.createInsideTunnel(tx, ty, t3, this, 145 + Server.rand.nextInt(3), 5, false, null);
                        if (v != null) {
                            v.addTarget(this);
                        }
                    }
                }
            }
        }
    }
    
    public int getMaxHuntDistance() {
        if (this.isDominated()) {
            return 20;
        }
        return this.template.getMaxHuntDistance();
    }
    
    public Path findPath(final int targetX, final int targetY, @Nullable final PathFinder pathfinder) throws NoPathException {
        Path path = null;
        final PathFinder pf = (pathfinder != null) ? pathfinder : new PathFinder();
        this.setPathfindcounter(this.getPathfindCounter() + 1);
        if (this.getPathfindCounter() < 10 || this.target != -10L || this.getPower() > 0) {
            if (this.isSpiritGuard() && this.citizenVillage != null) {
                if (this.target == -10L) {
                    if (this.isWithinTileDistanceTo(targetX, targetY, (int)(this.status.getPositionZ() + this.getAltOffZ()) >> 2, this.getMaxHuntDistance())) {
                        path = pf.findPath(this, this.getTileX(), this.getTileY(), targetX, targetY, this.isOnSurface(), 10);
                    }
                }
                else {
                    try {
                        path = pf.findPath(this, this.getTileX(), this.getTileY(), targetX, targetY, this.isOnSurface(), 10);
                    }
                    catch (NoPathException nsp) {
                        if (this.currentVillage == this.citizenVillage) {
                            if (targetX < this.citizenVillage.getStartX() - 5 || targetX > this.citizenVillage.getEndX() + 5 || targetY < this.citizenVillage.getStartY() - 5 || targetY > this.citizenVillage.getEndY() + 5) {
                                this.setTargetNOID = true;
                            }
                            else if (this.getTarget() != null) {
                                this.creatureToBlinkTo = this.getTarget();
                                return null;
                            }
                        }
                        else if (this.getTarget() != null) {
                            this.creatureToBlinkTo = this.getTarget();
                            return null;
                        }
                    }
                }
            }
            else if (this.isWithinTileDistanceTo(targetX, targetY, (int)this.status.getPositionZ() >> 2, Math.max(this.getMaxHuntDistance(), this.template.getVision()))) {
                path = pf.findPath(this, this.getTileX(), this.getTileY(), targetX, targetY, this.isOnSurface(), 5);
            }
            else if (this.isUnique() || this.isKingdomGuard() || this.isDominated() || this.template.isTowerBasher()) {
                if (this.target == -10L) {
                    path = pf.findPath(this, this.getTileX(), this.getTileY(), targetX, targetY, this.isOnSurface(), 5);
                }
                else {
                    this.setTargetNOID = true;
                }
            }
            if (path != null) {
                this.setPathfindcounter(0);
            }
            return path;
        }
        throw new NoPathException("No pathing now");
    }
    
    public boolean isSentinel() {
        return this.template.isSentinel();
    }
    
    public boolean isNpc() {
        return false;
    }
    
    public boolean isTrader() {
        return !this.isReborn() && (this.template.getTemplateId() != 1 || this.isPlayer()) && this.template.isTrader();
    }
    
    public boolean canEat() {
        return this.getStatus().canEat();
    }
    
    public boolean isHungry() {
        return this.getStatus().isHungry();
    }
    
    public boolean isNeedFood() {
        return this.template.isNeedFood();
    }
    
    public boolean isMoveRandom() {
        return this.template.isMoveRandom();
    }
    
    public boolean isSwimming() {
        return this.template.isSwimming();
    }
    
    public boolean isAnimal() {
        return this.template.isAnimal();
    }
    
    public boolean isHuman() {
        return this.template.isHuman();
    }
    
    public boolean isRegenerating() {
        return this.template.isRegenerating() || this.isUndead();
    }
    
    public boolean isDragon() {
        return this.template.isDragon();
    }
    
    public boolean isTypeFleeing() {
        return this.isSpy() || this.template.isFleeing();
    }
    
    public boolean isMonster() {
        return this.template.isMonster();
    }
    
    public boolean isInvulnerable() {
        return this.template.isInvulnerable();
    }
    
    public boolean isNpcTrader() {
        return this.template.isNpcTrader();
    }
    
    public boolean isAggHuman() {
        return this.isReborn() || this.template.isAggHuman();
    }
    
    public boolean isMoveLocal() {
        return this.template.isMoveLocal() && this.status.modtype != 99;
    }
    
    public boolean isMoveGlobal() {
        boolean shouldMove = false;
        if (Features.Feature.CREATURE_MOVEMENT_CHANGES.isEnabled() && this.getCurrentTile().getVillage() != null && (this.isBred() || this.isBranded() || this.isCaredFor()) && this.target == -10L) {
            shouldMove = true;
        }
        return this.template.isMoveGlobal() || this.status.modtype == 99 || shouldMove;
    }
    
    public boolean shouldFlee() {
        if (!Features.Feature.CREATURE_MOVEMENT_CHANGES.isEnabled()) {
            return !this.getStatus().isChampion() && this.fleeCounter > 0;
        }
        if (this.getCurrentTile().getVillage() != null && (this.isBred() || this.isBranded() || this.isCaredFor())) {
            return false;
        }
        if (this.getStatus().isChampion()) {
            return false;
        }
        if (this.fleeCounter > 0) {
            final Long[] creatures;
            final Long[] visibleCreatures = creatures = this.getVisionArea().getSurface().getCreatures();
            for (final Long lCret : creatures) {
                try {
                    final Creature cret = Server.getInstance().getCreature(lCret);
                    if ((cret.getPower() == 0 || Servers.localServer.testServer) && (cret.isPlayer() || cret.isAggHuman() || cret.isCarnivore() || cret.isMonster() || cret.isHunter())) {
                        float modifier = 1.0f;
                        if (this.getCurrentTile().getVillage() != null && cret.isPlayer()) {
                            modifier = 2.0f;
                        }
                        this.sendToLoggers("checking if should flee from " + cret.getName() + ": " + (cret.getBaseCombatRating() - Math.abs(cret.getPos2f().distance(this.getPos2f()) / 4.0f)) + " vs " + this.getBaseCombatRating() * modifier);
                        if (cret.getBaseCombatRating() - Math.abs(cret.getPos2f().distance(this.getPos2f()) / 2.0f) > this.getBaseCombatRating() * modifier) {
                            return true;
                        }
                    }
                }
                catch (NoSuchPlayerException ex) {}
                catch (NoSuchCreatureException ex2) {}
            }
        }
        return false;
    }
    
    public boolean isGrazer() {
        return this.template.isGrazer();
    }
    
    public boolean isHerd() {
        return this.template.isHerd();
    }
    
    public boolean isHunter() {
        return this.template.isHunter();
    }
    
    public boolean isMilkable() {
        return this.template.isMilkable() && this.getSex() == 1 && this.getStatus().age >= 3;
    }
    
    public boolean isReborn() {
        return this.getStatus().reborn;
    }
    
    public boolean isDominatable(final Creature aDominator) {
        return (this.getLeader() == null || this.getLeader() == aDominator) && !this.isRidden() && this.hitchedTo == null && this.template.isDominatable();
    }
    
    public final int getAggressivity() {
        return this.template.getAggressivity();
    }
    
    final byte getCombatDamageType() {
        return this.template.getCombatDamageType();
    }
    
    final float getBreathDamage() {
        if (this.isUndead()) {
            return 10.0f;
        }
        if (this.isReborn()) {
            return Math.max(3.0f, this.template.getBreathDamage());
        }
        return this.template.getBreathDamage();
    }
    
    public float getHandDamage() {
        if (this.isUndead()) {
            return 5.0f;
        }
        if (this.isReborn()) {
            return Math.max(3.0f, this.template.getHandDamage());
        }
        return this.template.getHandDamage();
    }
    
    public float getBiteDamage() {
        if (this.isUndead()) {
            return 8.0f;
        }
        if (this.isReborn()) {
            return Math.max(5.0f, this.template.getBiteDamage());
        }
        return this.template.getBiteDamage();
    }
    
    public float getKickDamage() {
        if (this.isReborn()) {
            return Math.max(2.0f, this.template.getKickDamage());
        }
        return this.template.getKickDamage();
    }
    
    public float getHeadButtDamage() {
        if (this.isReborn()) {
            return Math.max(4.0f, this.template.getKickDamage());
        }
        return this.template.getHeadButtDamage();
    }
    
    public Logger getLogger() {
        return null;
    }
    
    public boolean isUnique() {
        return this.template.isUnique();
    }
    
    public boolean isKingdomGuard() {
        return this.template.isKingdomGuard();
    }
    
    public boolean isGuard() {
        return this.isKingdomGuard() || this.isSpiritGuard() || this.isWarGuard();
    }
    
    public boolean isGhost() {
        return this.template.isGhost();
    }
    
    public boolean unDead() {
        return this.template.isUndead();
    }
    
    public final boolean onlyAttacksPlayers() {
        return this.template.onlyAttacksPlayers();
    }
    
    public boolean isSpiritGuard() {
        return this.template.isSpiritGuard();
    }
    
    public boolean isZombieSummoned() {
        return this.template.getTemplateId() == 69;
    }
    
    public boolean isBartender() {
        return this.template.isBartender();
    }
    
    public boolean isDefendKingdom() {
        return this.template.isDefendKingdom();
    }
    
    public boolean isNotFemale() {
        return this.getSex() != 1;
    }
    
    public boolean isAggWhitie() {
        return this.template.isAggWhitie() || this.isReborn();
    }
    
    public boolean isHerbivore() {
        return this.template.isHerbivore();
    }
    
    public boolean isCarnivore() {
        return this.template.isCarnivore();
    }
    
    public boolean isOmnivore() {
        return this.template.isOmnivore();
    }
    
    public boolean isCaveDweller() {
        return this.template.isCaveDweller();
    }
    
    public boolean isSubmerged() {
        return this.template.isSubmerged();
    }
    
    public boolean isEggLayer() {
        return this.template.isEggLayer();
    }
    
    public int getEggTemplateId() {
        return this.template.getEggTemplateId();
    }
    
    public int getMaxGroupAttackSize() {
        if (this.isUnique()) {
            return 100;
        }
        final float mod = this.getStatus().getBattleRatingTypeModifier();
        return (int)Math.max(this.template.getMaxGroupAttackSize(), this.template.getMaxGroupAttackSize() * mod);
    }
    
    public int getGroupSize() {
        int nums = 0;
        for (int x = Math.max(0, this.getCurrentTile().getTileX() - 3); x < Math.min(this.getCurrentTile().getTileX() + 3, Zones.worldTileSizeX - 1); ++x) {
            for (int y = Math.max(0, this.getCurrentTile().getTileY() - 3); y < Math.min(this.getCurrentTile().getTileY() + 3, Zones.worldTileSizeY - 1); ++y) {
                final VolaTile t = Zones.getTileOrNull(x, y, this.isOnSurface());
                if (t != null && t.getCreatures().length > 0) {
                    final Creature[] creatures;
                    final Creature[] xret = creatures = t.getCreatures();
                    for (final Creature lElement : creatures) {
                        if (lElement.getTemplate().getTemplateId() == this.template.getTemplateId() || lElement.getTemplate().getTemplateId() == this.template.getLeaderTemplateId()) {
                            ++nums;
                        }
                    }
                }
            }
        }
        return nums;
    }
    
    public final TilePos getAdjacentTilePos(final TilePos pos) {
        switch (Server.rand.nextInt(8)) {
            case 0: {
                return pos.East();
            }
            case 1: {
                return pos.South();
            }
            case 2: {
                return pos.West();
            }
            case 3: {
                return pos.North();
            }
            case 4: {
                return pos.NorthEast();
            }
            case 5: {
                return pos.NorthWest();
            }
            case 6: {
                return pos.SouthWest();
            }
            case 7: {
                return pos.SouthEast();
            }
            default: {
                return pos;
            }
        }
    }
    
    public void checkEggLaying() {
        if (this.isEggLayer()) {
            if (this.template.getTemplateId() == 53) {
                if (Server.rand.nextInt(7200) == 0) {
                    if (WurmCalendar.isAfterEaster()) {
                        this.destroy();
                        Server.getInstance().broadCastAction(this.getNameWithGenus() + " suddenly vanishes down into a hole!", this, 10);
                    }
                    else {
                        try {
                            final Item egg = ItemFactory.createItem(466, 50.0f, null);
                            egg.putItemInfrontof(this);
                            Server.getInstance().broadCastAction(this.getNameWithGenus() + " throws something in the air!", this, 10);
                        }
                        catch (Exception ex) {
                            Creature.logger.log(Level.WARNING, ex.getMessage(), ex);
                        }
                    }
                }
            }
            else if (this.status.getSex() == 1 && this.isNeedFood() && !this.canEat() && (Items.mayLayEggs() || this.isUnique()) && Server.rand.nextInt(20000 * (this.isUnique() ? 1000 : 1)) == 0 && this.isOnSurface()) {
                final byte type = Tiles.decodeType(Server.surfaceMesh.getTile(this.getCurrentTile().tilex, this.getCurrentTile().tiley));
                if (type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_FIELD.id || type == Tiles.Tile.TILE_FIELD2.id || type == Tiles.Tile.TILE_DIRT.id || type == Tiles.Tile.TILE_DIRT_PACKED.id) {
                    int templateId = 464;
                    if (this.template.getSize() > 4) {
                        templateId = 465;
                    }
                    try {
                        final Item egg2 = ItemFactory.createItem(templateId, 99.0f, this.getPosX(), this.getPosY(), this.status.getRotation(), this.isOnSurface(), (byte)0, this.getStatus().getBridgeId(), null);
                        if (templateId == 465 || Server.rand.nextInt(5) == 0) {
                            egg2.setData1(this.template.getEggTemplateId());
                        }
                    }
                    catch (NoSuchTemplateException nst) {
                        Creature.logger.log(Level.WARNING, nst.getMessage(), nst);
                    }
                    catch (FailedException fe) {
                        Creature.logger.log(Level.WARNING, fe.getMessage(), fe);
                    }
                    this.status.hunger = 60000;
                }
            }
        }
    }
    
    public boolean isNoSkillFor(final Creature attacker) {
        if ((this.isKingdomGuard() || this.isSpiritGuard() || this.isZombieSummoned() || (this.isPlayer() && attacker.isPlayer()) || this.isWarGuard()) && this.isFriendlyKingdom(attacker.getKingdomId())) {
            return true;
        }
        if (this.isPlayer() && attacker.isPlayer()) {
            if (Players.getInstance().isOverKilling(attacker.getWurmId(), this.getWurmId())) {
                return true;
            }
            if (((Player)this).getSaveFile().getIpaddress().equals(((Player)attacker).getSaveFile().getIpaddress())) {
                return true;
            }
        }
        return false;
    }
    
    public int[] forageForFood(final VolaTile currTile) {
        final int[] toReturn = { -1, -1 };
        if (this.canEat() && this.isNeedFood()) {
            for (int x = -2; x <= 2; ++x) {
                for (int y = -2; y <= 2; ++y) {
                    final VolaTile t = Zones.getTileOrNull(Zones.safeTileX(currTile.getTileX() + x), Zones.safeTileY(currTile.getTileY() + y), this.isOnSurface());
                    if (t != null) {
                        final Item[] items;
                        final Item[] its = items = t.getItems();
                        for (final Item lIt : items) {
                            if (lIt.isEdibleBy(this) && Server.rand.nextInt(10) == 0) {
                                this.sendToLoggers("Found " + lIt.getName());
                                toReturn[0] = Zones.safeTileX(currTile.getTileX() + x);
                                toReturn[1] = Zones.safeTileY(currTile.getTileY() + y);
                                return toReturn;
                            }
                        }
                    }
                    if (this.isGrazer() && this.canEat() && Server.rand.nextInt(9) == 0) {
                        final byte type = Zones.getTextureForTile(Zones.safeTileX(currTile.getTileX() + x), Zones.safeTileY(currTile.getTileY() + y), this.getLayer());
                        if (type == Tiles.Tile.TILE_GRASS.id || type == Tiles.Tile.TILE_FIELD.id || type == Tiles.Tile.TILE_FIELD2.id || type == Tiles.Tile.TILE_STEPPE.id || type == Tiles.Tile.TILE_ENCHANTED_GRASS.id) {
                            this.sendToLoggers("Found grass or field");
                            toReturn[0] = Zones.safeTileX(currTile.getTileX() + x);
                            toReturn[1] = Zones.safeTileY(currTile.getTileY() + y);
                            return toReturn;
                        }
                    }
                }
            }
        }
        return toReturn;
    }
    
    public void blinkTo(final int tilex, final int tiley, final int layer, final int floorLevel) {
        this.getCurrentTile().deleteCreatureQuick(this);
        try {
            this.setPositionX((tilex << 2) + 2);
            this.setPositionY((tiley << 2) + 2);
            this.setLayer(Math.min(0, layer), false);
            if (floorLevel > 0) {
                this.pushToFloorLevel(floorLevel);
            }
            else {
                this.setPositionZ(Zones.calculateHeight(this.getStatus().getPositionX(), this.getStatus().getPositionY(), this.isOnSurface()));
            }
            final Zone z = Zones.getZone(tilex, tiley, layer >= 0);
            z.addCreature(this.getWurmId());
        }
        catch (Exception ex) {
            Creature.logger.log(Level.WARNING, this.getName() + " - " + tilex + ", " + tiley + ", " + layer + ", " + floorLevel + ": " + ex.getMessage(), ex);
        }
    }
    
    public final boolean isBeachDweller() {
        return this.template.isBeachDweller();
    }
    
    public final boolean isWoolProducer() {
        return this.template.isWoolProducer();
    }
    
    public boolean isTargetTileTooHigh(final int targetX, final int targetY, final int currentTileNum, final boolean swimming) {
        if (this.getFloorLevel() > 0) {
            return false;
        }
        if (this.isFlying()) {
            return false;
        }
        final short currheight = Tiles.decodeHeight(currentTileNum);
        final short[] lSteepness = getTileSteepness(targetX, targetY, this.isOnSurface());
        if (swimming && lSteepness[0] < -200 && currheight > lSteepness[0] && !this.isFloating()) {
            return true;
        }
        if (this.isBeachDweller()) {
            if (currheight > 20 && lSteepness[0] > currheight) {
                return true;
            }
            if (currheight < 0 && lSteepness[0] > 0 && !WurmCalendar.isNight()) {
                return true;
            }
        }
        if (this.isOnSurface()) {
            final VolaTile stile = Zones.getTileOrNull(targetX, targetY, this.isOnSurface());
            if (stile != null && stile.getStructure() != null && stile.getStructure().isTypeBridge()) {
                if (stile.getStructure().isHorizontal()) {
                    if (stile.getStructure().getMaxX() == stile.getTileX() || stile.getStructure().getMinX() == stile.getTileX()) {
                        return false;
                    }
                }
                else if (stile.getStructure().getMaxY() == stile.getTileY() || stile.getStructure().getMinY() == stile.getTileY()) {
                    return false;
                }
            }
        }
        if (currheight < 500) {
            return false;
        }
        if (!swimming && lSteepness[0] - currheight > 60.0 * Math.max(1.0, getTileRange(this, targetX, targetY)) && lSteepness[1] > 20) {
            if (Creatures.getInstance().isLog()) {
                Creature.logger.log(Level.INFO, this.getName() + " Skipping moving up since avg steep=" + lSteepness[0] + "=" + (lSteepness[0] - currheight) + ">" + 60.0 * Math.max(1.0, getTileRange(this, targetX, targetY)) + " at " + targetX + "," + targetY + " from " + this.getTileX() + ", " + this.getTileY());
            }
            return true;
        }
        if (!swimming && currheight - lSteepness[0] > 60.0 * Math.max(1.0, getTileRange(this, targetX, targetY)) && lSteepness[1] > 20) {
            if (Creatures.getInstance().isLog()) {
                Creature.logger.log(Level.INFO, this.getName() + " Skipping moving down since avg steep=" + lSteepness[0] + "=" + (lSteepness[0] - currheight) + ">" + 60.0 * Math.max(1.0, getTileRange(this, targetX, targetY)) + " at " + targetX + "," + targetY + " from " + this.getTileX() + ", " + this.getTileY());
            }
            return true;
        }
        return false;
    }
    
    public final long getBridgeId() {
        if (this.getStatus().getPosition() != null) {
            return this.getStatus().getPosition().getBridgeId();
        }
        return -10L;
    }
    
    public final boolean isWarGuard() {
        return this.template.isWarGuard();
    }
    
    public PathTile getMoveTarget(final int seed) {
        if (this.getStatus() == null) {
            return null;
        }
        final long now = System.currentTimeMillis();
        final float lPosX = this.status.getPositionX();
        final float lPosY = this.status.getPositionY();
        boolean hasTarget = false;
        int tilePosX = (int)lPosX >> 2;
        int tilePosY = (int)lPosY >> 2;
        final int tx = tilePosX;
        final int ty = tilePosY;
        try {
            if (this.target == -10L || (this.fleeCounter > 0 && this.target == -10L)) {
                boolean flee = false;
                if (this.isDominated() && this.fleeCounter <= 0) {
                    if (this.hasOrders()) {
                        final Order order = this.getFirstOrder();
                        if (order.isTile()) {
                            boolean swimming = false;
                            final int ctile = this.isOnSurface() ? Server.surfaceMesh.getTile(tx, ty) : Server.caveMesh.getTile(tx, ty);
                            if (Tiles.decodeHeight(ctile) <= 0) {
                                swimming = true;
                            }
                            final int tile = Zones.getTileIntForTile(order.getTileX(), order.getTileY(), this.getLayer());
                            if (!Tiles.isSolidCave(Tiles.decodeType(tile)) && (Tiles.decodeHeight(tile) > 0 || swimming)) {
                                if (this.isOnSurface()) {
                                    if (!this.isTargetTileTooHigh(order.getTileX(), order.getTileY(), tile, swimming)) {
                                        hasTarget = true;
                                        tilePosX = order.getTileX();
                                        tilePosY = order.getTileY();
                                    }
                                }
                                else {
                                    hasTarget = true;
                                    tilePosX = order.getTileX();
                                    tilePosY = order.getTileY();
                                }
                            }
                        }
                        else if (order.isCreature()) {
                            final Creature lTarget = order.getCreature();
                            if (lTarget != null) {
                                if (lTarget.isDead()) {
                                    this.removeOrder(order);
                                }
                                else {
                                    hasTarget = true;
                                    tilePosX = lTarget.getCurrentTile().tilex;
                                    tilePosY = lTarget.getCurrentTile().tiley;
                                }
                            }
                        }
                    }
                }
                else if (this.isTypeFleeing() || this.shouldFlee()) {
                    if (Features.Feature.CREATURE_MOVEMENT_CHANGES.isEnabled()) {
                        if (this.isOnSurface() && this.getVisionArea() != null && this.getVisionArea().getSurface() != null) {
                            final int heatmapSize = this.template.getVision() * 2 + 1;
                            final float[][] rangeHeatmap = new float[heatmapSize][heatmapSize];
                            for (int i = 0; i < heatmapSize; ++i) {
                                for (int j = 0; j < heatmapSize; ++j) {
                                    rangeHeatmap[i][j] = -100.0f;
                                }
                            }
                            final Long[] creatures;
                            final Long[] visibleCreatures = creatures = this.getVisionArea().getSurface().getCreatures();
                            for (final Long lCret : creatures) {
                                try {
                                    final Creature cret = Server.getInstance().getCreature(lCret);
                                    float tileModifier = 0.0f;
                                    final int diffX = (int)(cret.getPosX() - this.getPosX()) >> 2;
                                    final int diffY = (int)(cret.getPosY() - this.getPosY()) >> 2;
                                    for (int k = 0; k < heatmapSize; ++k) {
                                        for (int l = 0; l < heatmapSize; ++l) {
                                            final int deltaX = Math.abs(this.template.getVision() + diffX - k);
                                            final int deltaY = Math.abs(this.template.getVision() + diffY - l);
                                            if ((cret.getPower() == 0 || Servers.localServer.testServer) && (cret.isPlayer() || cret.isAggHuman() || cret.isCarnivore() || cret.isMonster() || cret.isHunter())) {
                                                tileModifier = cret.getBaseCombatRating();
                                                if (cret.isBred() || cret.isBranded() || cret.isCaredFor()) {
                                                    tileModifier /= 3.0f;
                                                }
                                                if (cret.isDominated()) {
                                                    tileModifier /= 3.0f;
                                                }
                                                tileModifier -= Math.max(deltaX, deltaY);
                                            }
                                            else {
                                                tileModifier = 1.0f;
                                            }
                                            final float[] array = rangeHeatmap[k];
                                            final int n2 = l;
                                            array[n2] += tileModifier;
                                        }
                                    }
                                }
                                catch (NoSuchPlayerException ex) {}
                                catch (NoSuchCreatureException ex2) {}
                            }
                            float currentVal = rangeHeatmap[this.template.getVision()][this.template.getVision()];
                            int currentValCount = 1;
                            final int currentTileHeight = Tiles.decodeHeight(Server.surfaceMesh.getTile(Zones.safeTileX(this.getTileX()), Zones.safeTileY(this.getTileY())));
                            for (int y = 0; y < heatmapSize; ++y) {
                                for (int x = 0; x < heatmapSize; ++x) {
                                    final int tileHeight = Tiles.decodeHeight(Server.surfaceMesh.getTile(Zones.safeTileX(this.getTileX() + x - this.template.getVision()), Zones.safeTileY(this.getTileY() + y - this.template.getVision())));
                                    if (!this.isSubmerged() && tileHeight < 0) {
                                        if (!this.isSwimming()) {
                                            final float[] array2 = rangeHeatmap[x];
                                            final int n3 = y;
                                            array2[n3] += 100 + -tileHeight;
                                        }
                                        else {
                                            final float[] array3 = rangeHeatmap[x];
                                            final int n4 = y;
                                            array3[n4] += -tileHeight;
                                        }
                                    }
                                    else if (tileHeight > 0) {
                                        final float[] array4 = rangeHeatmap[x];
                                        final int n5 = y;
                                        array4[n5] += Math.abs(currentTileHeight - tileHeight) / 15;
                                    }
                                    final float testVal = rangeHeatmap[x][y];
                                    if (testVal == currentVal) {
                                        ++currentValCount;
                                    }
                                    else if (testVal < currentVal) {
                                        currentValCount = 1;
                                        currentVal = testVal;
                                    }
                                }
                            }
                            for (int y = 0; y < heatmapSize && !flee; ++y) {
                                for (int x = 0; x < heatmapSize && !flee; ++x) {
                                    if (currentVal == rangeHeatmap[x][y] && Server.rand.nextInt((int)Math.max(1.0f, currentValCount * 0.75f)) == 0) {
                                        tilePosX = tx + x - this.template.getVision();
                                        tilePosY = ty + y - this.template.getVision();
                                        flee = true;
                                    }
                                }
                            }
                            if (!flee) {
                                return null;
                            }
                        }
                    }
                    else if (this.isOnSurface()) {
                        if (Server.rand.nextBoolean()) {
                            if (this.getCurrentTile() != null && this.getCurrentTile().getVillage() != null) {
                                final Long[] creatures2;
                                final Long[] crets = creatures2 = this.getVisionArea().getSurface().getCreatures();
                                for (final Long lCret2 : creatures2) {
                                    try {
                                        final Creature cret2 = Server.getInstance().getCreature(lCret2);
                                        if (cret2.getPower() == 0 && (cret2.isPlayer() || cret2.isAggHuman() || cret2.isCarnivore() || cret2.isMonster())) {
                                            if (cret2.getPosX() > this.getPosX()) {
                                                tilePosX -= Server.rand.nextInt(6);
                                            }
                                            else {
                                                tilePosX += Server.rand.nextInt(6);
                                            }
                                            if (cret2.getPosY() > this.getPosY()) {
                                                tilePosY -= Server.rand.nextInt(6);
                                            }
                                            else {
                                                tilePosY += Server.rand.nextInt(6);
                                            }
                                            flee = true;
                                            break;
                                        }
                                    }
                                    catch (Exception ex3) {}
                                }
                            }
                        }
                        else {
                            for (final Player p : Players.getInstance().getPlayers()) {
                                if ((p.getPower() == 0 || Servers.localServer.testServer) && p.getVisionArea() != null && p.getVisionArea().getSurface() != null && p.getVisionArea().getSurface().containsCreature(this)) {
                                    if (p.getPosX() > this.getPosX()) {
                                        tilePosX -= Server.rand.nextInt(6);
                                    }
                                    else {
                                        tilePosX += Server.rand.nextInt(6);
                                    }
                                    if (p.getPosY() > this.getPosY()) {
                                        tilePosY -= Server.rand.nextInt(6);
                                    }
                                    else {
                                        tilePosY += Server.rand.nextInt(6);
                                    }
                                    flee = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (this.isSpy()) {
                        final int[] empty = { -1, -1 };
                        final int[] newarr = this.getSpySpot(empty);
                        if (newarr[0] > 0 && newarr[1] > 0) {
                            flee = true;
                            tilePosX = newarr[0];
                            tilePosY = newarr[1];
                        }
                    }
                }
                if (this.isMoveLocal() && !flee && !hasTarget) {
                    final VolaTile currTile = this.getCurrentTile();
                    if (this.isUnique() && Server.rand.nextInt(10) == 0) {
                        final Den den = Dens.getDen(this.template.getTemplateId());
                        if (den != null && (den.getTilex() != tx || den.getTiley() != ty)) {
                            tilePosX = den.getTilex();
                            tilePosY = den.getTiley();
                        }
                    }
                    else if (currTile != null) {
                        int rand = Server.rand.nextInt(9);
                        int tpx = currTile.getTileX() + 4 - rand;
                        rand = Server.rand.nextInt(9);
                        int tpy = currTile.getTileY() + 4 - rand;
                        Creature.totx += currTile.getTileX() - tpx;
                        Creature.toty += currTile.getTileY() - tpy;
                        final int[] foodSpot = this.forageForFood(currTile);
                        boolean abort = false;
                        if (Server.rand.nextBoolean()) {
                            if (foodSpot[0] != -1) {
                                tpx = foodSpot[0];
                                tpy = foodSpot[1];
                            }
                            else if (this.template.isTowerBasher() && Servers.localServer.PVPSERVER) {
                                final GuardTower closestTower = Kingdoms.getClosestEnemyTower(this.getTileX(), this.getTileY(), true, this);
                                if (closestTower != null) {
                                    tilePosX = closestTower.getTower().getTileX();
                                    tilePosY = closestTower.getTower().getTileY();
                                    abort = true;
                                }
                            }
                            else if (this.isWarGuard()) {
                                tilePosX = Zones.safeTileX(tpx);
                                tilePosY = Zones.safeTileY(tpy);
                                if (!this.isOnSurface()) {
                                    int[] tiles = { tilePosX, tilePosY };
                                    if (this.getCurrentTile().isTransition) {
                                        this.setLayer(0, true);
                                    }
                                    else {
                                        tiles = this.findRandomCaveExit(tiles);
                                        if (tiles[0] != tilePosX && tiles[1] != tilePosY) {
                                            tilePosX = tiles[0];
                                            tilePosY = tiles[1];
                                            abort = true;
                                        }
                                        else {
                                            this.setLayer(0, true);
                                        }
                                    }
                                }
                                else {
                                    GuardTower gt = this.getGuardTower();
                                    if (gt == null) {
                                        gt = Kingdoms.getClosestTower(this.getTileX(), this.getTileY(), true);
                                    }
                                    boolean towerFound = false;
                                    if (gt != null && gt.getKingdom() == this.getKingdomId()) {
                                        towerFound = true;
                                    }
                                    final Item wtarget = Kingdoms.getClosestWarTarget(tx, ty, this);
                                    if (wtarget != null && (!towerFound || getTileRange(this, wtarget.getTileX(), wtarget.getTileY()) < getTileRange(this, gt.getTower().getTileX(), gt.getTower().getTileY())) && !this.isWithinTileDistanceTo(wtarget.getTileX(), wtarget.getTileY(), wtarget.getFloorLevel(), 15)) {
                                        rand = Server.rand.nextInt(9);
                                        tilePosX = Zones.safeTileX(wtarget.getTileX() + 4 - rand);
                                        rand = Server.rand.nextInt(9);
                                        tilePosY = Zones.safeTileY(wtarget.getTileY() + 4 - rand);
                                        this.setTarget(-10L, true);
                                        this.sendToLoggers("No target. Heading to my camp at " + tilePosX + "," + tilePosY);
                                        abort = true;
                                    }
                                    if (!abort && towerFound && !this.isWithinTileDistanceTo(gt.getTower().getTileX(), gt.getTower().getTileY(), gt.getTower().getFloorLevel(), 15)) {
                                        rand = Server.rand.nextInt(9);
                                        tilePosX = Zones.safeTileX(gt.getTower().getTileX() + 4 - rand);
                                        rand = Server.rand.nextInt(9);
                                        tilePosY = Zones.safeTileY(gt.getTower().getTileY() + 4 - rand);
                                        this.setTarget(-10L, true);
                                        this.sendToLoggers("No target. Heading to my tower at " + tilePosX + "," + tilePosY);
                                        abort = true;
                                    }
                                }
                            }
                        }
                        tpx = Zones.safeTileX(tpx);
                        tpy = Zones.safeTileY(tpy);
                        if (!abort) {
                            final VolaTile t = Zones.getOrCreateTile(tpx, tpy, this.isOnSurface());
                            final VolaTile myt = this.getCurrentTile();
                            if (!t.isGuarded() || (myt != null && myt.isGuarded() && !t.hasFire())) {
                                boolean swimming2 = false;
                                final int ctile2 = this.isOnSurface() ? Server.surfaceMesh.getTile(tx, ty) : Server.caveMesh.getTile(tx, ty);
                                if (Tiles.decodeHeight(ctile2) <= 0) {
                                    swimming2 = true;
                                }
                                final int tile2 = Zones.getTileIntForTile(tpx, tpy, this.getLayer());
                                if (!Tiles.isSolidCave(Tiles.decodeType(tile2)) && (Tiles.decodeHeight(tile2) > 0 || swimming2)) {
                                    if (this.isOnSurface()) {
                                        boolean stepOnBridge = false;
                                        if (Server.rand.nextInt(5) == 0) {
                                            for (final VolaTile stile : this.currentTile.getThisAndSurroundingTiles(1)) {
                                                if (stile.getStructure() != null && stile.getStructure().isTypeBridge()) {
                                                    if (stile.getStructure().isHorizontal()) {
                                                        if ((stile.getStructure().getMaxX() == stile.getTileX() || stile.getStructure().getMinX() == stile.getTileX()) && this.getTileY() == stile.getTileY()) {
                                                            tilePosX = stile.getTileX();
                                                            tilePosY = stile.getTileY();
                                                            stepOnBridge = true;
                                                            break;
                                                        }
                                                        continue;
                                                    }
                                                    else {
                                                        if ((stile.getStructure().getMaxY() == stile.getTileY() || stile.getStructure().getMinY() == stile.getTileY()) && this.getTileX() == stile.getTileX()) {
                                                            tilePosX = stile.getTileX();
                                                            tilePosY = stile.getTileY();
                                                            stepOnBridge = true;
                                                            break;
                                                        }
                                                        continue;
                                                    }
                                                }
                                            }
                                        }
                                        if (!stepOnBridge && !this.isTargetTileTooHigh(tpx, tpy, tile2, swimming2) && (t == null || t.getCreatures().length < 3)) {
                                            tilePosX = tpx;
                                            tilePosY = tpy;
                                        }
                                    }
                                    else if (t == null || t.getCreatures().length < 3) {
                                        tilePosX = tpx;
                                        tilePosY = tpy;
                                    }
                                }
                            }
                        }
                    }
                }
                else if (this.isSpiritGuard() && !hasTarget) {
                    if (this.citizenVillage != null) {
                        int[] tiles2 = { tilePosX, tilePosY };
                        if (!this.isOnSurface()) {
                            if (this.getCurrentTile().isTransition) {
                                this.setLayer(0, true);
                            }
                            else {
                                tiles2 = this.findRandomCaveExit(tiles2);
                                tilePosX = tiles2[0];
                                tilePosY = tiles2[1];
                                if (tilePosX != tx && tilePosY != ty) {}
                            }
                        }
                        else {
                            final int x2 = this.citizenVillage.startx + Server.rand.nextInt(this.citizenVillage.endx - this.citizenVillage.startx);
                            final int y2 = this.citizenVillage.starty + Server.rand.nextInt(this.citizenVillage.endy - this.citizenVillage.starty);
                            final VolaTile t2 = Zones.getTileOrNull(x2, y2, this.isOnSurface());
                            if (t2 != null) {
                                if (t2.getStructure() == null) {
                                    tilePosX = x2;
                                    tilePosY = y2;
                                }
                            }
                            else {
                                tilePosX = x2;
                                tilePosY = y2;
                            }
                        }
                    }
                    else {
                        VolaTile currTile = this.getCurrentTile();
                        if (currTile != null) {
                            int rand = Server.rand.nextInt(5);
                            int tpx = currTile.getTileX() + 2 - rand;
                            rand = Server.rand.nextInt(5);
                            int tpy = currTile.getTileY() + 2 - rand;
                            final VolaTile t3 = Zones.getTileOrNull(tilePosX, tilePosY, this.isOnSurface());
                            tpx = Zones.safeTileX(tpx);
                            tpy = Zones.safeTileY(tpy);
                            if (t3 == null) {
                                tilePosX = tpx;
                                tilePosY = tpy;
                            }
                        }
                        else if (!this.isDead()) {
                            currTile = Zones.getOrCreateTile(tilePosX, tilePosY, this.isOnSurface());
                            Creature.logger.log(Level.WARNING, this.getName() + " stuck on no tile at " + this.getTileX() + "," + this.getTileY() + "," + this.isOnSurface());
                        }
                    }
                }
                else if (this.isKingdomGuard() && !hasTarget) {
                    int[] tiles2 = { tilePosX, tilePosY };
                    if (!this.isOnSurface()) {
                        tiles2 = this.findRandomCaveExit(tiles2);
                        tilePosX = tiles2[0];
                        tilePosY = tiles2[1];
                        if (tilePosX != tx && tilePosY != ty) {
                            hasTarget = true;
                        }
                    }
                    if (!hasTarget && Server.rand.nextInt(40) == 0) {
                        final GuardTower gt2 = Kingdoms.getTower(this);
                        if (gt2 != null) {
                            final int tpx = gt2.getTower().getTileX();
                            final int tpy = gt2.getTower().getTileY();
                            tilePosX = tpx;
                            tilePosY = tpy;
                            hasTarget = true;
                        }
                    }
                    if (!hasTarget) {
                        final VolaTile currTile2 = this.getCurrentTile();
                        int rand2 = Server.rand.nextInt(5);
                        final int tpx2 = Zones.safeTileX(currTile2.getTileX() + 2 - rand2);
                        rand2 = Server.rand.nextInt(5);
                        final int tpy2 = Zones.safeTileY(currTile2.getTileY() + 2 - rand2);
                        final VolaTile t4 = Zones.getOrCreateTile(tpx2, tpy2, this.isOnSurface());
                        if ((t4.getKingdom() == this.getKingdomId() || currTile2.getKingdom() != this.getKingdomId()) && t4.getStructure() == null) {
                            tilePosX = tpx2;
                            tilePosY = tpy2;
                        }
                    }
                }
                if (!this.isCaveDweller() && !this.isOnSurface() && this.getCurrentTile().isTransition && tilePosX == tx && tilePosY == ty && (!Tiles.isMineDoor(Tiles.decodeType(Server.surfaceMesh.getTile(tx, ty))) || MineDoorPermission.getPermission(tx, ty).mayPass(this))) {
                    this.setLayer(0, true);
                }
            }
            else if (this.target != -10L) {
                final Creature targ = this.getTarget();
                if (targ != null) {
                    if (targ.getCultist() != null && targ.getCultist().hasFearEffect()) {
                        this.setTarget(-10L, true);
                    }
                    final VolaTile currTile = targ.getCurrentTile();
                    if (currTile != null) {
                        tilePosX = currTile.tilex;
                        tilePosY = currTile.tiley;
                        if (seed == 100) {
                            tilePosX = currTile.tilex - 1 + Server.rand.nextInt(3);
                            tilePosY = currTile.tiley - 1 + Server.rand.nextInt(3);
                        }
                        if (this.isSpellCaster() || this.isSummoner()) {
                            tilePosX = (Server.rand.nextBoolean() ? (currTile.tilex - (Server.rand.nextBoolean() ? 0 : 5)) : (currTile.tilex + (Server.rand.nextBoolean() ? 0 : 5)));
                            tilePosY = (Server.rand.nextBoolean() ? (currTile.tiley - (Server.rand.nextBoolean() ? 0 : 5)) : (currTile.tiley + (Server.rand.nextBoolean() ? 0 : 5)));
                        }
                        final int targGroup = targ.getGroupSize();
                        final int myGroup = this.getGroupSize();
                        Label_7929: {
                            if (this.isOnSurface() != currTile.isOnSurface()) {
                                boolean changeLayer = false;
                                if (this.getCurrentTile().isTransition) {
                                    changeLayer = true;
                                }
                                if (this.isSpiritGuard()) {
                                    if (this.currentVillage == this.citizenVillage) {
                                        if (this.citizenVillage != null) {
                                            if (currTile.getTileX() >= this.citizenVillage.getStartX() - 5 && currTile.getTileX() <= this.citizenVillage.getEndX() + 5 && currTile.getTileY() >= this.citizenVillage.getStartY() - 5 && currTile.getTileY() <= this.citizenVillage.getEndY() + 5) {
                                                this.blinkTo(tilePosX, tilePosY, targ.getLayer(), targ.getFloorLevel());
                                                return null;
                                            }
                                            if (this.citizenVillage.isOnSurface() == this.isOnSurface()) {
                                                try {
                                                    changeLayer = false;
                                                    tilePosX = this.citizenVillage.getToken().getTileX();
                                                    tilePosY = this.citizenVillage.getToken().getTileY();
                                                }
                                                catch (NoSuchItemException nsi) {
                                                    Creature.logger.log(Level.WARNING, this.getName() + " no token for village " + this.citizenVillage);
                                                }
                                            }
                                            this.setTarget(-10L, true);
                                        }
                                        else {
                                            this.setTarget(-10L, true);
                                        }
                                    }
                                    else if (this.citizenVillage != null) {
                                        if (currTile.getTileX() >= this.citizenVillage.getStartX() - 5 && currTile.getTileX() <= this.citizenVillage.getEndX() + 5 && currTile.getTileY() >= this.citizenVillage.getStartY() - 5 && currTile.getTileY() <= this.citizenVillage.getEndY() + 5) {
                                            this.blinkTo(tilePosX, tilePosY, targ.getLayer(), 0);
                                            return null;
                                        }
                                        if (this.citizenVillage.isOnSurface() == this.isOnSurface()) {
                                            try {
                                                tilePosX = this.citizenVillage.getToken().getTileX();
                                                tilePosY = this.citizenVillage.getToken().getTileY();
                                                changeLayer = false;
                                            }
                                            catch (NoSuchItemException nsi) {
                                                Creature.logger.log(Level.WARNING, this.getName() + " no token for village " + this.citizenVillage);
                                            }
                                        }
                                        else if (!changeLayer) {
                                            int[] tiles3 = { tilePosX, tilePosY };
                                            if (this.isOnSurface()) {
                                                tiles3 = this.findRandomCaveEntrance(tiles3);
                                            }
                                            else {
                                                tiles3 = this.findRandomCaveExit(tiles3);
                                            }
                                            tilePosX = tiles3[0];
                                            tilePosY = tiles3[1];
                                        }
                                        this.setTarget(-10L, true);
                                    }
                                    else {
                                        this.setTarget(-10L, true);
                                    }
                                }
                                else if (this.isUnique()) {
                                    final Den den2 = Dens.getDen(this.template.getTemplateId());
                                    if (den2 != null) {
                                        tilePosX = den2.getTilex();
                                        tilePosY = den2.getTiley();
                                        if (!changeLayer) {
                                            int[] tiles4 = { tilePosX, tilePosY };
                                            if (!this.isOnSurface()) {
                                                tiles4 = this.findRandomCaveExit(tiles4);
                                            }
                                            tilePosX = tiles4[0];
                                            tilePosY = tiles4[1];
                                        }
                                        this.setTarget(-10L, true);
                                    }
                                    else if (!this.isOnSurface() && !changeLayer) {
                                        int[] tiles4 = { tilePosX, tilePosY };
                                        tiles4 = this.findRandomCaveExit(tiles4);
                                        tilePosX = tiles4[0];
                                        tilePosY = tiles4[1];
                                    }
                                }
                                else if (this.isKingdomGuard()) {
                                    if (this.getCurrentKingdom() == this.getKingdomId()) {
                                        if (this.isWithinTileDistanceTo(currTile.getTileX(), currTile.getTileY(), (int)targ.getPositionZ(), this.template.getMaxHuntDistance())) {
                                            if (!changeLayer) {
                                                int[] tiles3 = { tilePosX, tilePosY };
                                                if (this.isOnSurface()) {
                                                    tiles3 = this.findRandomCaveEntrance(tiles3);
                                                }
                                                else {
                                                    tiles3 = this.findRandomCaveExit(tiles3);
                                                }
                                                tilePosX = tiles3[0];
                                                tilePosY = tiles3[1];
                                            }
                                        }
                                        else {
                                            this.setTarget(-10L, true);
                                        }
                                    }
                                    else {
                                        changeLayer = false;
                                        this.setTarget(-10L, true);
                                    }
                                }
                                else if (this.getSize() > 3) {
                                    changeLayer = false;
                                    this.setTarget(-10L, true);
                                }
                                else {
                                    final VolaTile t3 = this.getCurrentTile();
                                    if ((this.isAggHuman() || this.isHunter() || this.isDominated()) && (!currTile.isGuarded() || (t3 != null && t3.isGuarded())) && this.isWithinTileDistanceTo(currTile.getTileX(), currTile.getTileY(), (int)targ.getPositionZ(), this.template.getMaxHuntDistance())) {
                                        if (!changeLayer) {
                                            int[] tiles4 = { tilePosX, tilePosY };
                                            if (this.isOnSurface()) {
                                                tiles4 = this.findRandomCaveEntrance(tiles4);
                                            }
                                            else {
                                                tiles4 = this.findRandomCaveExit(tiles4);
                                            }
                                            tilePosX = tiles4[0];
                                            tilePosY = tiles4[1];
                                        }
                                    }
                                    else {
                                        this.setTarget(-10L, true);
                                    }
                                }
                                if (changeLayer && (!Tiles.isMineDoor(Tiles.decodeType(Server.surfaceMesh.getTile(tx, ty))) || MineDoorPermission.getPermission(tx, ty).mayPass(this))) {
                                    this.setLayer(this.isOnSurface() ? -1 : 0, true);
                                }
                            }
                            else if (this.isSpiritGuard()) {
                                if (this.currentVillage == this.citizenVillage) {
                                    if (this.citizenVillage != null) {
                                        tilePosX = currTile.getTileX();
                                        tilePosY = currTile.getTileY();
                                        if (targ.getCultist() != null && targ.getCultist().hasFearEffect()) {
                                            tilePosX = this.citizenVillage.getStartX() - 5 + Server.rand.nextInt(this.citizenVillage.getDiameterX() + 10);
                                            tilePosY = this.citizenVillage.getStartY() - 5 + Server.rand.nextInt(this.citizenVillage.getDiameterY() + 10);
                                        }
                                        else {
                                            if (currTile.getTileX() >= this.citizenVillage.getStartX() - 5 && currTile.getTileX() <= this.citizenVillage.getEndX() + 5 && currTile.getTileY() >= this.citizenVillage.getStartY() - 5) {
                                                if (currTile.getTileY() <= this.citizenVillage.getEndY() + 5) {
                                                    this.citizenVillage.cryForHelp(this, false);
                                                    break Label_7929;
                                                }
                                            }
                                            try {
                                                tilePosX = this.citizenVillage.getToken().getTileX();
                                                tilePosY = this.citizenVillage.getToken().getTileY();
                                            }
                                            catch (NoSuchItemException nsi2) {
                                                Creature.logger.log(Level.WARNING, this.getName() + " no token for village " + this.citizenVillage);
                                            }
                                            this.setTarget(-10L, true);
                                        }
                                    }
                                    else if (this.isWithinTileDistanceTo(currTile.getTileX(), currTile.getTileY(), (int)targ.getPositionZ(), this.template.getMaxHuntDistance())) {
                                        Creature.logger.log(Level.WARNING, "Why does this happen to a " + this.getName() + " at " + this.getCurrentTile().tilex + ", " + this.getCurrentTile().tiley);
                                        tilePosX = currTile.getTileX();
                                        tilePosY = currTile.getTileY();
                                    }
                                    else {
                                        this.setTarget(-10L, true);
                                    }
                                }
                                else if (this.citizenVillage != null) {
                                    tilePosX = currTile.getTileX();
                                    tilePosY = currTile.getTileY();
                                    if (currTile.getTileX() >= this.citizenVillage.getStartX() - 5 && currTile.getTileX() <= this.citizenVillage.getEndX() + 5 && currTile.getTileY() >= this.citizenVillage.getStartY() - 5) {
                                        if (currTile.getTileY() <= this.citizenVillage.getEndY() + 5) {
                                            this.citizenVillage.cryForHelp(this, true);
                                            break Label_7929;
                                        }
                                    }
                                    try {
                                        tilePosX = this.citizenVillage.getToken().getTileX();
                                        tilePosY = this.citizenVillage.getToken().getTileY();
                                    }
                                    catch (NoSuchItemException nsi2) {
                                        Creature.logger.log(Level.WARNING, this.getName() + " no token for village " + this.citizenVillage);
                                    }
                                    this.setTarget(-10L, true);
                                }
                                else {
                                    if (this.isWithinTileDistanceTo(currTile.getTileX(), currTile.getTileY(), (int)targ.getPositionZ(), this.template.getMaxHuntDistance())) {
                                        if (Server.rand.nextInt(100) != 0) {
                                            tilePosX = currTile.getTileX();
                                            tilePosY = currTile.getTileY();
                                        }
                                        else {
                                            this.setTarget(-10L, true);
                                        }
                                    }
                                    else {
                                        this.setTarget(-10L, true);
                                    }
                                    Creature.logger.log(Level.WARNING, this.getName() + " no citizen village.");
                                }
                            }
                            else if (this.isUnique()) {
                                final Den den3 = Dens.getDen(this.template.getTemplateId());
                                if (den3 != null) {
                                    if (Math.abs(currTile.getTileX() - den3.getTilex()) > this.template.getVision() || Math.abs(currTile.getTileY() - den3.getTiley()) > this.template.getVision()) {
                                        if (Server.rand.nextInt(10) == 0) {
                                            if (!this.isFighting()) {
                                                this.setTarget(-10L, true);
                                                tilePosX = den3.getTilex();
                                                tilePosY = den3.getTiley();
                                            }
                                        }
                                        else if (this.isWithinTileDistanceTo(currTile.getTileX(), currTile.getTileY(), (int)targ.getPositionZ(), this.template.getMaxHuntDistance())) {
                                            tilePosX = currTile.getTileX();
                                            tilePosY = currTile.getTileY();
                                            if (this.getSize() < 5 && targ.getBridgeId() != -10L && this.getBridgeId() < 0L) {
                                                final int[] tiles3 = this.findBestBridgeEntrance(targ.getTileX(), targ.getTileY(), targ.getLayer(), targ.getBridgeId());
                                                if (tiles3[0] > 0) {
                                                    tilePosX = tiles3[0];
                                                    tilePosY = tiles3[1];
                                                    if (this.getTileX() == tilePosX && this.getTileY() == tilePosY) {
                                                        tilePosX = currTile.tilex;
                                                        tilePosY = currTile.tiley;
                                                    }
                                                }
                                            }
                                            else if (this.getBridgeId() != targ.getBridgeId()) {
                                                final int[] tiles3 = this.findBestBridgeEntrance(targ.getTileX(), targ.getTileY(), targ.getLayer(), this.getBridgeId());
                                                if (tiles3[0] > 0) {
                                                    tilePosX = tiles3[0];
                                                    tilePosY = tiles3[1];
                                                    if (this.getTileX() == tilePosX && this.getTileY() == tilePosY) {
                                                        tilePosX = currTile.tilex;
                                                        tilePosY = currTile.tiley;
                                                    }
                                                }
                                            }
                                            if (seed == 100) {
                                                tilePosX = currTile.tilex - 1 + Server.rand.nextInt(3);
                                                tilePosY = currTile.tiley - 1 + Server.rand.nextInt(3);
                                            }
                                        }
                                        else if (!this.isFighting()) {
                                            this.setTarget(-10L, true);
                                        }
                                    }
                                }
                                else if (this.isWithinTileDistanceTo(currTile.getTileX(), currTile.getTileY(), (int)targ.getPositionZ(), this.template.getMaxHuntDistance())) {
                                    if (seed == 100) {
                                        tilePosX = currTile.tilex - 1 + Server.rand.nextInt(3);
                                        tilePosY = currTile.tiley - 1 + Server.rand.nextInt(3);
                                    }
                                    else {
                                        tilePosX = currTile.getTileX();
                                        tilePosY = currTile.getTileY();
                                        if (this.getSize() < 5 && targ.getBridgeId() != -10L && this.getBridgeId() < 0L) {
                                            final int[] tiles3 = this.findBestBridgeEntrance(targ.getTileX(), targ.getTileY(), targ.getLayer(), targ.getBridgeId());
                                            if (tiles3[0] > 0) {
                                                tilePosX = tiles3[0];
                                                tilePosY = tiles3[1];
                                                if (this.getTileX() == tilePosX && this.getTileY() == tilePosY) {
                                                    tilePosX = currTile.tilex;
                                                    tilePosY = currTile.tiley;
                                                }
                                            }
                                        }
                                        else if (this.getBridgeId() != targ.getBridgeId()) {
                                            final int[] tiles3 = this.findBestBridgeEntrance(targ.getTileX(), targ.getTileY(), targ.getLayer(), this.getBridgeId());
                                            if (tiles3[0] > 0) {
                                                tilePosX = tiles3[0];
                                                tilePosY = tiles3[1];
                                                if (this.getTileX() == tilePosX && this.getTileY() == tilePosY) {
                                                    tilePosX = currTile.tilex;
                                                    tilePosY = currTile.tiley;
                                                }
                                            }
                                        }
                                    }
                                }
                                else if (!this.isFighting()) {
                                    this.setTarget(-10L, true);
                                }
                            }
                            else if (this.isKingdomGuard()) {
                                if (this.getCurrentKingdom() == this.getKingdomId()) {
                                    if (this.isWithinTileDistanceTo(currTile.getTileX(), currTile.getTileY(), (int)targ.getPositionZ(), this.template.getMaxHuntDistance())) {
                                        final GuardTower gt3 = Kingdoms.getTower(this);
                                        if (gt3 != null) {
                                            final int tpx3 = gt3.getTower().getTileX();
                                            final int tpy3 = gt3.getTower().getTileY();
                                            if (targGroup < myGroup * this.getMaxGroupAttackSize() && targ.isWithinTileDistanceTo(tpx3, tpy3, (int)gt3.getTower().getPosZ(), 50)) {
                                                if (targ.getCultist() != null && targ.getCultist().hasFearEffect()) {
                                                    if (Server.rand.nextBoolean()) {
                                                        tilePosX = Math.max(currTile.getTileX() + 10, this.getTileX());
                                                    }
                                                    else {
                                                        tilePosX = Math.min(currTile.getTileX() - 10, this.getTileX());
                                                    }
                                                    if (Server.rand.nextBoolean()) {
                                                        tilePosX = Math.max(currTile.getTileY() + 10, this.getTileY());
                                                    }
                                                    else {
                                                        tilePosX = Math.min(currTile.getTileY() - 10, this.getTileY());
                                                    }
                                                }
                                                else if (seed == 100) {
                                                    tilePosX = currTile.tilex - 1 + Server.rand.nextInt(3);
                                                    tilePosY = currTile.tiley - 1 + Server.rand.nextInt(3);
                                                }
                                                else {
                                                    tilePosX = currTile.getTileX();
                                                    tilePosY = currTile.getTileY();
                                                    if (targ.getBridgeId() != -10L && this.getBridgeId() < 0L) {
                                                        final int[] tiles = this.findBestBridgeEntrance(targ.getTileX(), targ.getTileY(), targ.getLayer(), targ.getBridgeId());
                                                        if (tiles[0] > 0) {
                                                            tilePosX = tiles[0];
                                                            tilePosY = tiles[1];
                                                            if (this.getTileX() == tilePosX && this.getTileY() == tilePosY) {
                                                                tilePosX = currTile.tilex;
                                                                tilePosY = currTile.tiley;
                                                            }
                                                        }
                                                    }
                                                    else if (this.getBridgeId() != targ.getBridgeId()) {
                                                        final int[] tiles = this.findBestBridgeEntrance(targ.getTileX(), targ.getTileY(), targ.getLayer(), this.getBridgeId());
                                                        if (tiles[0] > 0) {
                                                            tilePosX = tiles[0];
                                                            tilePosY = tiles[1];
                                                            if (this.getTileX() == tilePosX && this.getTileY() == tilePosY) {
                                                                tilePosX = currTile.tilex;
                                                                tilePosY = currTile.tiley;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            else {
                                                tilePosX = tpx3;
                                                tilePosY = tpy3;
                                                this.setTarget(-10L, true);
                                            }
                                        }
                                        else if (seed == 100) {
                                            tilePosX = currTile.tilex - 1 + Server.rand.nextInt(3);
                                            tilePosY = currTile.tiley - 1 + Server.rand.nextInt(3);
                                        }
                                        else {
                                            tilePosX = currTile.getTileX();
                                            tilePosY = currTile.getTileY();
                                        }
                                    }
                                    else {
                                        this.setTarget(-10L, true);
                                    }
                                }
                                else {
                                    this.setTarget(-10L, true);
                                }
                            }
                            else if (targ.getCultist() != null && targ.getCultist().hasFearEffect()) {
                                if (Server.rand.nextBoolean()) {
                                    tilePosX = Math.max(currTile.getTileX() + 10, this.getTileX());
                                }
                                else {
                                    tilePosX = Math.min(currTile.getTileX() - 10, this.getTileX());
                                }
                                if (Server.rand.nextBoolean()) {
                                    tilePosX = Math.max(currTile.getTileY() + 10, this.getTileY());
                                }
                                else {
                                    tilePosX = Math.min(currTile.getTileY() - 10, this.getTileY());
                                }
                            }
                            else {
                                boolean abort2 = false;
                                boolean towerFound2 = false;
                                if (this.isWarGuard()) {
                                    final GuardTower gt4 = Kingdoms.getClosestTower(this.getTileX(), this.getTileY(), true);
                                    if (gt4 != null && gt4.getKingdom() == this.getKingdomId()) {
                                        towerFound2 = true;
                                    }
                                    final Item wtarget2 = Kingdoms.getClosestWarTarget(tx, ty, this);
                                    if (wtarget2 != null && (!towerFound2 || getTileRange(this, wtarget2.getTileX(), wtarget2.getTileY()) < getTileRange(this, gt4.getTower().getTileX(), gt4.getTower().getTileY())) && !this.isWithinTileDistanceTo(wtarget2.getTileX(), wtarget2.getTileY(), wtarget2.getFloorLevel(), 15)) {
                                        int rand3 = Server.rand.nextInt(9);
                                        tilePosX = Zones.safeTileX(wtarget2.getTileX() + 4 - rand3);
                                        rand3 = Server.rand.nextInt(9);
                                        tilePosY = Zones.safeTileY(wtarget2.getTileY() + 4 - rand3);
                                        abort2 = true;
                                        this.setTarget(-10L, true);
                                        this.sendToLoggers("Heading to my camp at " + tilePosX + "," + tilePosY);
                                    }
                                    if (!abort2 && towerFound2 && !this.isWithinTileDistanceTo(gt4.getTower().getTileX(), gt4.getTower().getTileY(), gt4.getTower().getFloorLevel(), 15)) {
                                        int rand3 = Server.rand.nextInt(9);
                                        tilePosX = Zones.safeTileX(gt4.getTower().getTileX() + 4 - rand3);
                                        rand3 = Server.rand.nextInt(9);
                                        tilePosY = Zones.safeTileY(gt4.getTower().getTileY() + 4 - rand3);
                                        abort2 = true;
                                        this.setTarget(-10L, true);
                                        this.sendToLoggers("Heading to my tower at " + tilePosX + "," + tilePosY);
                                    }
                                }
                                if (!abort2) {
                                    final VolaTile t4 = this.getCurrentTile();
                                    if (targGroup <= myGroup * this.getMaxGroupAttackSize() && (this.isAggHuman() || this.isHunter()) && (!currTile.isGuarded() || (t4 != null && t4.isGuarded()))) {
                                        if (this.isWithinTileDistanceTo(currTile.getTileX(), currTile.getTileY(), (int)targ.getPositionZ(), this.template.getMaxHuntDistance())) {
                                            if (targ.getKingdomId() != 0 && !this.isFriendlyKingdom(targ.getKingdomId()) && (this.isDefendKingdom() || (this.isAggWhitie() && targ.getKingdomTemplateId() != 3))) {
                                                if (!this.isFighting()) {
                                                    if (seed == 100) {
                                                        tilePosX = currTile.tilex - 1 + Server.rand.nextInt(3);
                                                        tilePosY = currTile.tiley - 1 + Server.rand.nextInt(3);
                                                    }
                                                    else {
                                                        tilePosX = currTile.getTileX();
                                                        tilePosY = currTile.getTileY();
                                                        this.setTarget(targ.getWurmId(), false);
                                                    }
                                                }
                                            }
                                            else if (this.isSubmerged()) {
                                                try {
                                                    final float z = Zones.calculateHeight(targ.getPosX(), targ.getPosY(), targ.isOnSurface());
                                                    if (z < -5.0f) {
                                                        if (seed == 100) {
                                                            tilePosX = currTile.tilex - 1 + Server.rand.nextInt(3);
                                                            tilePosY = currTile.tiley - 1 + Server.rand.nextInt(3);
                                                        }
                                                        else {
                                                            tilePosX = currTile.getTileX();
                                                            tilePosY = currTile.getTileY();
                                                        }
                                                    }
                                                    else {
                                                        int[] tiles5 = { tilePosX, tilePosY };
                                                        if (this.isOnSurface()) {
                                                            tiles5 = this.findRandomDeepSpot(tiles5);
                                                        }
                                                        tilePosX = tiles5[0];
                                                        tilePosY = tiles5[1];
                                                        this.setTarget(-10L, true);
                                                    }
                                                }
                                                catch (NoSuchZoneException nsz) {
                                                    this.setTarget(-10L, true);
                                                }
                                            }
                                            else if (seed == 100) {
                                                tilePosX = currTile.tilex - 1 + Server.rand.nextInt(3);
                                                tilePosY = currTile.tiley - 1 + Server.rand.nextInt(3);
                                            }
                                            else {
                                                tilePosX = currTile.getTileX();
                                                tilePosY = currTile.getTileY();
                                                if (this.getSize() < 5 && targ.getBridgeId() != -10L && this.getBridgeId() < 0L) {
                                                    final int[] tiles = this.findBestBridgeEntrance(targ.getTileX(), targ.getTileY(), targ.getLayer(), targ.getBridgeId());
                                                    if (tiles[0] > 0) {
                                                        tilePosX = tiles[0];
                                                        tilePosY = tiles[1];
                                                        if (this.getTileX() == tilePosX && this.getTileY() == tilePosY) {
                                                            tilePosX = currTile.tilex;
                                                            tilePosY = currTile.tiley;
                                                        }
                                                    }
                                                }
                                                else if (this.getBridgeId() != targ.getBridgeId()) {
                                                    final int[] tiles = this.findBestBridgeEntrance(targ.getTileX(), targ.getTileY(), targ.getLayer(), this.getBridgeId());
                                                    if (tiles[0] > 0) {
                                                        tilePosX = tiles[0];
                                                        tilePosY = tiles[1];
                                                        if (this.getTileX() == tilePosX && this.getTileY() == tilePosY) {
                                                            tilePosX = currTile.tilex;
                                                            tilePosY = currTile.tiley;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        else if (!this.isFighting()) {
                                            this.setTarget(-10L, true);
                                        }
                                    }
                                    else if (!this.isFighting()) {
                                        this.setTarget(-10L, true);
                                    }
                                }
                            }
                        }
                    }
                    else {
                        this.setTarget(-10L, true);
                    }
                }
                else {
                    this.setTarget(-10L, true);
                }
            }
            if (tilePosX == tx && tilePosY == ty) {
                return null;
            }
            tilePosX = Zones.safeTileX(tilePosX);
            tilePosY = Zones.safeTileY(tilePosY);
            if (!this.isOnSurface()) {
                final int tile3 = Server.caveMesh.getTile(tilePosX, tilePosY);
                if (!Tiles.isSolidCave(Tiles.decodeType(tile3)) && (Tiles.decodeHeight(tile3) > -this.getHalfHeightDecimeters() || this.isSwimming() || this.isSubmerged())) {
                    return new PathTile(tilePosX, tilePosY, tile3, this.isOnSurface(), this.getFloorLevel());
                }
            }
            else {
                final int tile3 = Server.surfaceMesh.getTile(tilePosX, tilePosY);
                if (Tiles.decodeHeight(tile3) > -this.getHalfHeightDecimeters() || this.isSwimming() || this.isSubmerged()) {
                    return new PathTile(tilePosX, tilePosY, tile3, this.isOnSurface(), this.getFloorLevel());
                }
            }
            this.setTarget(-10L, true);
            if (this.isDominated() && this.hasOrders()) {
                this.removeOrder(this.getFirstOrder());
            }
            return null;
        }
        catch (ArrayIndexOutOfBoundsException iao) {
            Creature.logger.log(Level.WARNING, this.getName() + " " + tilePosX + ", " + tilePosY + iao.getMessage(), iao);
            return null;
        }
    }
    
    public final boolean isBridgeBlockingAttack(final Creature attacker, final boolean justChecking) {
        return this.isInvulnerable() || attacker.isInvulnerable() || (this.getPositionZ() + this.getAltOffZ() < 0.0f && attacker.getBridgeId() > 0L) || (attacker.getPositionZ() + this.getAltOffZ() < 0.0f && this.getBridgeId() > 0L) || (!justChecking && this.getFloorLevel() != attacker.getFloorLevel() && this.getBridgeId() != attacker.getBridgeId() && this.getSize() < 5 && attacker.getSize() < 5);
    }
    
    public final PathTile getPersonalTargetTile() {
        final float lPosX = this.status.getPositionX();
        final float lPosY = this.status.getPositionY();
        int tilePosX = (int)lPosX >> 2;
        int tilePosY = (int)lPosY >> 2;
        final int tx = tilePosX;
        final int ty = tilePosY;
        final VolaTile currTile = this.getCurrentTile();
        if (currTile != null) {
            final int[] foodSpot = this.forageForFood(currTile);
            if (foodSpot[0] != -1) {
                tilePosX = foodSpot[0];
                tilePosY = foodSpot[1];
            }
            else if (this.template.isTowerBasher() && Servers.localServer.PVPSERVER) {
                final GuardTower closestTower = Kingdoms.getClosestEnemyTower(this.getTileX(), this.getTileY(), true, this);
                if (closestTower != null) {
                    tilePosX = closestTower.getTower().getTileX();
                    tilePosY = closestTower.getTower().getTileY();
                }
            }
        }
        if (tilePosX == tx && tilePosY == ty) {
            return null;
        }
        tilePosX = Zones.safeTileX(tilePosX);
        tilePosY = Zones.safeTileY(tilePosY);
        if (!this.isOnSurface()) {
            final int tile = Server.caveMesh.getTile(tilePosX, tilePosY);
            if (!Tiles.isSolidCave(Tiles.decodeType(tile)) && (Tiles.decodeHeight(tile) > -this.getHalfHeightDecimeters() || this.isSwimming() || this.isSubmerged())) {
                return new PathTile(tilePosX, tilePosY, tile, this.isOnSurface(), -1);
            }
        }
        else {
            final int tile = Server.surfaceMesh.getTile(tilePosX, tilePosY);
            if (Tiles.decodeHeight(tile) > -this.getHalfHeightDecimeters() || this.isSwimming() || this.isSubmerged()) {
                return new PathTile(tilePosX, tilePosY, tile, this.isOnSurface(), 0);
            }
        }
        return null;
    }
    
    public final int getHalfHeightDecimeters() {
        return this.getCentimetersHigh() / 20;
    }
    
    public int[] findRandomCaveExit(final int[] tiles) {
        int startx = Math.max(0, this.currentTile.tilex - 20);
        int endx = Math.min(Zones.worldTileSizeX - 1, this.currentTile.tilex + 20);
        int starty = Math.max(0, this.currentTile.tiley - 20);
        int endy = Math.min(Zones.worldTileSizeY - 1, this.currentTile.tiley + 20);
        if (this.citizenVillage != null && Server.rand.nextInt(2) == 0) {
            startx = Math.max(0, this.citizenVillage.getStartX() - 5);
            endx = Math.min(Zones.worldTileSizeX - 1, this.citizenVillage.getEndX() + 5);
            starty = Math.max(0, this.citizenVillage.getStartY() - 5);
            endy = Math.min(Zones.worldTileSizeY - 1, this.citizenVillage.getEndY() + 5);
            final int x = this.citizenVillage.startx + Server.rand.nextInt(this.citizenVillage.endx - this.citizenVillage.startx);
            final int y = this.citizenVillage.starty + Server.rand.nextInt(this.citizenVillage.endy - this.citizenVillage.starty);
            if (!Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(x, y)))) {
                tiles[0] = x;
                tiles[1] = y;
                this.setPathfindcounter(0);
            }
        }
        int rand = Server.rand.nextInt(endx - startx);
        startx += rand;
        rand = Server.rand.nextInt(endy - starty);
        starty += rand;
        for (int x2 = startx; x2 < endx; ++x2) {
            for (int y2 = starty; y2 < endy; ++y2) {
                if (Tiles.decodeType(Server.caveMesh.getTile(x2, y2)) == Tiles.Tile.TILE_CAVE_EXIT.id) {
                    tiles[0] = x2;
                    tiles[1] = y2;
                    this.setPathfindcounter(0);
                    return tiles;
                }
            }
        }
        return tiles;
    }
    
    public int[] findRandomDeepSpot(final int[] tiles) {
        int startx = Zones.safeTileX(this.currentTile.tilex - 50);
        final int endx = Zones.safeTileX(this.currentTile.tilex + 50);
        int starty = Zones.safeTileY(this.currentTile.tiley - 50);
        final int endy = Zones.safeTileY(this.currentTile.tiley + 50);
        int rand = Server.rand.nextInt(endx - startx);
        startx += rand;
        rand = Server.rand.nextInt(endy - starty);
        starty += rand;
        for (int x = startx; x < Math.min(endx, startx + 10); ++x) {
            for (int y = starty; y < Math.min(endy, starty + 10); ++y) {
                if (Tiles.decodeHeight(Server.surfaceMesh.getTile(x, y)) < -50.0f) {
                    tiles[0] = x;
                    tiles[1] = y;
                    return tiles;
                }
            }
        }
        return tiles;
    }
    
    public final boolean isSpyTarget(final Creature c) {
        return !c.isDead() && c.getPower() <= 0 && ((this.getTemplate().getTemplateId() == 84 && c.getKingdomId() != 3) || (this.getTemplate().getTemplateId() == 12 && c.getKingdomId() != 1) || (this.getTemplate().getTemplateId() == 10 && c.getKingdomId() != 2));
    }
    
    public final boolean isSpyFriend(final Creature c) {
        return !c.isAggHuman() && c.getCitizenVillage() != null && ((this.getTemplate().getTemplateId() == 84 && c.getKingdomId() == 3) || (this.getTemplate().getTemplateId() == 12 && c.getKingdomId() == 1) || (this.getTemplate().getTemplateId() == 10 && c.getKingdomId() == 2));
    }
    
    public final boolean isWithinSpyDist(final Creature c) {
        return c != null && c.isWithinTileDistanceTo(this.getTileX(), this.getTileY(), 100, 40);
    }
    
    public int[] getSpySpot(final int[] suggested) {
        if (this.isSpy()) {
            Creature linkedToc = this.getCreatureLinkedTo();
            if (linkedToc == null || !linkedToc.isDead() || !this.isWithinSpyDist(linkedToc)) {
                this.linkedTo = -10L;
                for (final Player player : Players.getInstance().getPlayers()) {
                    if (this.isSpyTarget(player) && !player.isDead() && this.isWithinSpyDist(player)) {
                        linkedToc = player;
                        this.setLinkedTo(player.getWurmId(), false);
                        break;
                    }
                }
            }
            if (linkedToc != null) {
                int targX = linkedToc.getTileX() + 15 + Server.rand.nextInt(6);
                if (this.getTileX() < linkedToc.getTileX()) {
                    targX = linkedToc.getTileX() - 15 - Server.rand.nextInt(6);
                }
                int targY = linkedToc.getTileY() + 15 + Server.rand.nextInt(6);
                if (this.getTileY() < linkedToc.getTileY()) {
                    targX = linkedToc.getTileY() - 15 - Server.rand.nextInt(6);
                }
                targX = Zones.safeTileX(targX);
                targY = Zones.safeTileX(targY);
                return new int[] { targX, targY };
            }
        }
        return suggested;
    }
    
    public int[] findRandomCaveEntrance(final int[] tiles) {
        int startx = Math.max(0, this.currentTile.tilex - 20);
        int endx = Math.min(Zones.worldTileSizeX - 1, this.currentTile.tilex + 20);
        int starty = Math.max(0, this.currentTile.tiley - 20);
        int endy = Math.min(Zones.worldTileSizeY - 1, this.currentTile.tiley + 20);
        if (this.citizenVillage != null) {
            startx = Math.max(0, this.citizenVillage.getStartX() - 5);
            endx = Math.min(Zones.worldTileSizeX - 1, this.citizenVillage.getEndX() + 5);
            starty = Math.max(0, this.citizenVillage.getStartY() - 5);
            endy = Math.min(Zones.worldTileSizeY - 1, this.citizenVillage.getEndY() + 5);
        }
        int rand = Server.rand.nextInt(endx - startx);
        startx += rand;
        rand = Server.rand.nextInt(endy - starty);
        starty += rand;
        final boolean passMineDoors = this.isKingdomGuard() || this.isGhost() || this.isUnique();
        for (int x = startx; x < Math.min(endx, startx + 10); ++x) {
            for (int y = starty; y < Math.min(endy, starty + 10); ++y) {
                if (Tiles.decodeType(Server.surfaceMesh.getTile(x, y)) == Tiles.Tile.TILE_HOLE.id || (passMineDoors && Tiles.isMineDoor(Tiles.decodeType(Server.surfaceMesh.getTile(x, y))))) {
                    tiles[0] = x;
                    tiles[1] = y;
                    return tiles;
                }
            }
        }
        return tiles;
    }
    
    public int[] findBestBridgeEntrance(final int tilex, final int tiley, final int layer, final long bridgeId) {
        final VolaTile t = Zones.getTileOrNull(tilex, tiley, layer >= 0);
        if (t != null && t.getStructure() != null && t.getStructure().getWurmId() == bridgeId) {
            return t.getStructure().findBestBridgeEntrance(this, tilex, tiley, layer, bridgeId, this.pathfindcounter);
        }
        return Structure.noEntrance;
    }
    
    public void setAbilityTitle(final int newTitle) {
    }
    
    public int getAbilityTitleVal() {
        return this.template.abilityTitle;
    }
    
    public String getAbilityTitle() {
        if (this.template.abilityTitle > -1) {
            return Abilities.getAbilityString(this.template.abilityTitle) + " ";
        }
        return "";
    }
    
    public boolean isLogged() {
        return false;
    }
    
    public float getFaith() {
        return this.template.getFaith();
    }
    
    public Skill getChannelingSkill() {
        Skill channeling = null;
        try {
            channeling = this.skills.getSkill(10067);
        }
        catch (NoSuchSkillException nss) {
            if (this.getFaith() >= 10.0f) {
                channeling = this.skills.learn(10067, 1.0f);
            }
        }
        return channeling;
    }
    
    public Skill getMindLogical() {
        Skill toReturn = null;
        try {
            toReturn = this.getSkills().getSkill(100);
        }
        catch (NoSuchSkillException nss) {
            toReturn = this.getSkills().learn(100, 1.0f);
        }
        return toReturn;
    }
    
    public Skill getMindSpeed() {
        Skill toReturn = null;
        try {
            toReturn = this.getSkills().getSkill(101);
        }
        catch (NoSuchSkillException nss) {
            toReturn = this.getSkills().learn(101, 1.0f);
        }
        return toReturn;
    }
    
    public Skill getSoulDepth() {
        Skill toReturn = null;
        try {
            toReturn = this.getSkills().getSkill(106);
        }
        catch (NoSuchSkillException nss) {
            toReturn = this.getSkills().learn(106, 1.0f);
        }
        return toReturn;
    }
    
    public Skill getBreedingSkill() {
        Skill toReturn;
        try {
            toReturn = this.getSkills().getSkill(10085);
        }
        catch (NoSuchSkillException nss) {
            toReturn = this.getSkills().learn(10085, 1.0f);
        }
        return toReturn;
    }
    
    public Skill getSoulStrength() {
        Skill toReturn = null;
        try {
            toReturn = this.getSkills().getSkill(105);
        }
        catch (NoSuchSkillException nss) {
            toReturn = this.getSkills().learn(105, 1.0f);
        }
        return toReturn;
    }
    
    public Skill getBodyStrength() {
        Skill toReturn = null;
        try {
            toReturn = this.getSkills().getSkill(102);
        }
        catch (NoSuchSkillException nss) {
            toReturn = this.getSkills().learn(102, 1.0f);
        }
        return toReturn;
    }
    
    public Deity getDeity() {
        return this.template.getDeity();
    }
    
    public void modifyFaith(final float modifier) {
    }
    
    public boolean isActionFaithful(final Action action) {
        return this.getDeity() == null || !this.faithful || this.getDeity().isActionFaithful(action);
    }
    
    public void performActionOkey(final Action action) {
        if (this.getDeity() != null && !this.getDeity().performActionOkey(this, action)) {
            this.getCommunicator().sendNormalServerMessage(this.getDeity().name + " noticed you!");
        }
    }
    
    public void setFaith(final float faith) throws IOException {
    }
    
    public void setDeity(@Nullable final Deity deity) throws IOException {
    }
    
    public boolean checkLoyaltyProgram() {
        return false;
    }
    
    public boolean maybeModifyAlignment(final float modification) {
        return false;
    }
    
    public void setAlignment(final float align) throws IOException {
    }
    
    public void setPriest(final boolean priest) {
    }
    
    public boolean isPriest() {
        return this.isSpellCaster() || this.isSummoner();
    }
    
    public float getAlignment() {
        return this.template.getAlignment();
    }
    
    public float getFavor() {
        if (this.isSpellCaster() || this.isSummoner()) {
            return this.creatureFavor;
        }
        return this.template.getFaith();
    }
    
    public float getFavorLinked() {
        return this.template.getFaith();
    }
    
    public void setFavor(final float favor) throws IOException {
        if (this.isSpellCaster() || this.isSummoner()) {
            this.creatureFavor = favor;
        }
    }
    
    public void depleteFavor(final float favorToRemove, final boolean combatSpell) throws IOException {
        if (this.isSpellCaster() || this.isSummoner()) {
            this.setFavor(this.getFavor() - favorToRemove);
        }
    }
    
    public boolean mayChangeDeity(final int targetDeity) {
        return true;
    }
    
    public void setChangedDeity() throws IOException {
    }
    
    public boolean isNewbie() {
        return false;
    }
    
    public boolean maySteal() {
        return true;
    }
    
    public boolean isAtWarWith(final Creature creature) {
        return this.citizenVillage != null && creature.citizenVillage != null && this.citizenVillage.isEnemy(creature.citizenVillage);
    }
    
    public boolean isChampion() {
        return false;
    }
    
    public void setRealDeath(final byte realdeathcounter) throws IOException {
    }
    
    public boolean modifyChampionPoints(final int championPointsModifier) {
        return false;
    }
    
    public int getFatigueLeft() {
        return 20000;
    }
    
    public void decreaseFatigue() {
    }
    
    public boolean checkPrayerFaith() {
        return false;
    }
    
    public boolean isAlive() {
        return !this.status.dead;
    }
    
    public boolean isDead() {
        return this.status.dead;
    }
    
    public byte getKingdomId() {
        if (!Servers.isThisAPvpServer()) {
            final Village bVill = this.getBrandVillage();
            if (bVill != null) {
                return bVill.kingdom;
            }
        }
        return this.status.kingdom;
    }
    
    public byte getKingdomTemplateId() {
        final Kingdom k = Kingdoms.getKingdom(this.getKingdomId());
        if (k != null) {
            return k.getTemplate();
        }
        return 0;
    }
    
    public int getReputation() {
        return this.template.getReputation();
    }
    
    public void setReputation(final int reputation) {
    }
    
    public void playAnthem() {
        if (this.musicPlayer != null) {
            if (this.getKingdomTemplateId() == 3) {
                this.musicPlayer.checkMUSIC_ANTHEMHOTS_SND();
            }
            if (this.getKingdomId() == 1) {
                this.musicPlayer.checkMUSIC_ANTHEMJENN_SND();
            }
            if (this.getKingdomId() == 2) {
                this.musicPlayer.checkMUSIC_ANTHEMMOLREHAN_SND();
            }
        }
    }
    
    public boolean isTransferring() {
        return false;
    }
    
    public boolean isOnCurrentServer() {
        return true;
    }
    
    public boolean setKingdomId(final byte kingdom) throws IOException {
        return this.setKingdomId(kingdom, false, true);
    }
    
    public boolean setKingdomId(final byte kingdom, final boolean forced) throws IOException {
        return this.setKingdomId(kingdom, forced, true);
    }
    
    public boolean setKingdomId(final byte kingdom, final boolean forced, final boolean setTimeStamp) throws IOException {
        return this.setKingdomId(kingdom, forced, setTimeStamp, true);
    }
    
    public boolean setKingdomId(final byte kingdom, final boolean forced, final boolean setTimeStamp, final boolean online) throws IOException {
        boolean sendUpdate = false;
        if (this.getKingdomId() != kingdom) {
            if (this.isKing()) {
                this.getCommunicator().sendNormalServerMessage("You are the king, and may not change kingdom!");
                return false;
            }
            final Village v = this.getCitizenVillage();
            if (!forced && v != null && v.getMayor().getId() == this.getWurmId()) {
                try {
                    this.getCommunicator().sendNormalServerMessage("You are the mayor of " + v.getName() + ", and may not change kingdom!");
                    return false;
                }
                catch (Exception ex) {
                    return false;
                }
            }
            if (Kingdoms.getKingdomTemplateFor(this.getKingdomId()) == 3 && Kingdoms.getKingdomTemplateFor(kingdom) != 3) {
                if (this.getDeity() != null && this.getDeity().number == 4) {
                    this.setDeity(null);
                    this.setFaith(0.0f);
                    this.setAlignment(Math.max(1.0f, this.getAlignment()));
                }
            }
            else if (Kingdoms.getKingdomTemplateFor(kingdom) == 3 && Kingdoms.getKingdomTemplateFor(this.getKingdomId()) != 3 && (this.getDeity() == null || this.getDeity().number == 1 || this.getDeity().number == 2 || this.getDeity().number == 3)) {
                this.setDeity(Deities.getDeity(4));
                this.setAlignment(Math.min(this.getAlignment(), -50.0f));
                this.setFaith(1.0f);
            }
            if (this.getKingdomId() != 0 && !forced) {
                if (this.citizenVillage != null) {
                    this.citizenVillage.removeCitizen(this);
                }
                if (kingdom != 0 && Servers.localServer.PVPSERVER) {
                    this.increaseChangedKingdom(setTimeStamp);
                }
                sendUpdate = true;
            }
            this.clearRoyalty();
            this.setTeam(null, true);
            if (this.isPlayer() && this.getCommunicator() != null && this.hasLink() && Servers.localServer.PVPSERVER && !Servers.localServer.testServer) {
                try {
                    final KingdomIp kip = KingdomIp.getKIP(this.getCommunicator().getConnection().getIp(), this.getKingdomId());
                    if (kip != null) {
                        kip.logon(kingdom);
                    }
                }
                catch (Exception iox) {
                    Creature.logger.log(Level.INFO, this.getName() + " " + iox.getMessage());
                }
            }
            this.status.setKingdom(kingdom);
            if (this.isPlayer()) {
                if (Servers.localServer.isChallengeOrEpicServer() || Servers.isThisAChaosServer() || Servers.localServer.PVPSERVER) {
                    if (this.getCommunicator().getConnection() != null) {
                        try {
                            if (this.getCommunicator().getConnection().getIp() != null) {
                                final KingdomIp kip = KingdomIp.getKIP(this.getCommunicator().getConnection().getIp());
                                if (kip != null) {
                                    kip.setKingdom(kingdom);
                                }
                            }
                        }
                        catch (NullPointerException ex2) {}
                    }
                    if ((Server.getInstance().isPS() && Servers.localServer.PVPSERVER) || Servers.isThisAChaosServer()) {
                        ((Player)this).getSaveFile().setChaosKingdom(kingdom);
                    }
                }
                Players.getInstance().registerNewKingdom(this);
                this.setVotedKing(false);
            }
            this.playAnthem();
            Creatures.getInstance().setCreatureDead(this);
            this.setTarget(-10L, true);
            if (sendUpdate && online) {
                this.refreshVisible();
            }
            if (this.citizenVillage != null) {
                if (!forced) {
                    this.citizenVillage.removeCitizen(this);
                }
                else if (this.citizenVillage.getMayor().wurmId == this.getWurmId()) {
                    this.citizenVillage.convertToKingdom(kingdom, true, setTimeStamp);
                }
            }
        }
        return true;
    }
    
    public void setVotedKing(final boolean voted) {
    }
    
    public boolean hasVotedKing() {
        return true;
    }
    
    public void clearRoyalty() {
    }
    
    public void checkForEnemies() {
        this.checkForEnemies(false);
    }
    
    public void checkForEnemies(boolean overrideRandomChance) {
        if ((this.isWarGuard() || this.isKingdomGuard()) && this.guardTower != null && this.guardTower.getKingdom() == this.getKingdomId() && System.currentTimeMillis() - this.guardTower.getLastSentWarning() < 180000L) {
            overrideRandomChance = true;
        }
        if ((overrideRandomChance || Server.rand.nextInt((this.isKingdomGuard() || this.isWarGuard()) ? 20 : 100) == 0) && this.getVisionArea() != null) {
            try {
                if (this.isOnSurface()) {
                    this.getVisionArea().getSurface().checkForEnemies();
                }
                else {
                    this.getVisionArea().getUnderGround().checkForEnemies();
                }
            }
            catch (Exception ep) {
                Creature.logger.log(Level.WARNING, ep.getMessage(), ep);
            }
        }
    }
    
    public boolean sendTransfer(final Server senderServer, final String targetIp, final int targetPort, final String serverpass, final int targetServerId, final int tilex, final int tiley, final boolean surfaced, final boolean toOrFromEpic, final byte targetKingdomId) {
        Creature.logger.log(Level.WARNING, "Sendtransfer called in creature", new Exception());
        return false;
    }
    
    public void increaseChangedKingdom(final boolean setTimeStamp) throws IOException {
    }
    
    public boolean mayChangeKingdom(final Creature converter) {
        return false;
    }
    
    public boolean isOfCustomKingdom() {
        final Kingdom k = Kingdoms.getKingdom(this.getKingdomId());
        return k != null && k.isCustomKingdom();
    }
    
    public void punishSkills(final double aMod, final boolean pvp) {
        if (this.getCultist() != null && this.getCultist().isNoDecay()) {
            return;
        }
        try {
            final Skill bodyStr = this.skills.getSkill(102);
            bodyStr.setKnowledge(bodyStr.getKnowledge() - 0.009999999776482582, false);
            final Skill body = this.skills.getSkill(1);
            body.setKnowledge(body.getKnowledge() - 0.009999999776482582, false);
        }
        catch (NoSuchSkillException nss) {
            this.skills.learn(102, 1.0f);
            Creature.logger.log(Level.WARNING, this.getName() + " learnt body strength.");
        }
        if (!pvp) {
            final Skill[] sk = this.skills.getSkills();
            int nums = 0;
            for (final Skill lElement : sk) {
                if ((lElement.getType() == 4 || lElement.getType() == 2) && lElement.getNumber() != 1023 && Server.rand.nextInt(10) == 0 && lElement.getKnowledge(0.0) > 2.0 && lElement.getKnowledge(0.0) < 99.0) {
                    lElement.setKnowledge(Math.max(1.0, lElement.getKnowledge() - aMod), false);
                    if (++nums > 4) {
                        break;
                    }
                }
            }
        }
    }
    
    public long getMoney() {
        return 0L;
    }
    
    public boolean addMoney(final long moneyToAdd) throws IOException {
        return false;
    }
    
    public boolean chargeMoney(final long moneyToCharge) throws IOException {
        return false;
    }
    
    public boolean hasCustomColor() {
        return this.getPower() > 0 || this.hasCustomKingdom() || this.status.hasCustomColor() || this.template.getColorRed() != 255 || this.template.getColorGreen() != 255 || this.template.getColorBlue() != 255;
    }
    
    public boolean hasCustomKingdom() {
        return this.getKingdomId() > 4 || this.getKingdomId() < 0;
    }
    
    public byte getColorRed() {
        if (this.status.hasCustomColor()) {
            return this.status.getColorRed();
        }
        return (byte)this.template.getColorRed();
    }
    
    public byte getColorGreen() {
        if (this.status.hasCustomColor()) {
            return this.status.getColorGreen();
        }
        return (byte)this.template.getColorGreen();
    }
    
    public byte getColorBlue() {
        if (this.status.hasCustomColor()) {
            return this.status.getColorBlue();
        }
        return (byte)this.template.getColorBlue();
    }
    
    public boolean hasCustomSize() {
        return this.status.getSizeMod() != 1.0f || this.template.getSizeModX() != 64 || this.template.getSizeModY() != 64 || this.template.getSizeModZ() != 64;
    }
    
    public byte getSizeModX() {
        return (byte)Math.min(255.0f, this.template.getSizeModX() * this.status.getSizeMod());
    }
    
    public byte getSizeModY() {
        return (byte)Math.min(255.0f, this.template.getSizeModY() * this.status.getSizeMod());
    }
    
    public byte getSizeModZ() {
        return (byte)Math.min(255.0f, this.template.getSizeModZ() * this.status.getSizeMod());
    }
    
    public void setMoney(final long newMoney) throws IOException {
    }
    
    public void setClimbing(final boolean climbing) throws IOException {
    }
    
    public boolean isClimbing() {
        return true;
    }
    
    public boolean acceptsInvitations() {
        return false;
    }
    
    public Cultist getCultist() {
        return null;
    }
    
    public static short[] getTileSteepness(final int tilex, final int tiley, final boolean surfaced) {
        short highest = -100;
        short lowest = 32000;
        for (int x = 0; x <= 1; ++x) {
            for (int y = 0; y <= 1; ++y) {
                if (tilex + x < Zones.worldTileSizeX && tiley + y < Zones.worldTileSizeY) {
                    short height = 0;
                    if (surfaced) {
                        height = Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex + x, tiley + y));
                    }
                    else {
                        height = Tiles.decodeHeight(Server.caveMesh.getTile(tilex + x, tiley + y));
                    }
                    if (height > highest) {
                        highest = height;
                    }
                    if (height < lowest) {
                        lowest = height;
                    }
                }
            }
        }
        final int med = (highest + lowest) / 2;
        return new short[] { (short)med, (short)(highest - lowest) };
    }
    
    public short[] getLowestTileCorner(final short tilex, final short tiley) {
        short lowestX = tilex;
        short lowestY = tiley;
        short lowest = 32000;
        for (int x = 0; x <= 1; ++x) {
            for (int y = 0; y <= 1; ++y) {
                if (tilex + x < Zones.worldTileSizeX && tiley + y < Zones.worldTileSizeY) {
                    final short height = Tiles.decodeHeight(Server.surfaceMesh.getTile(tilex + x, tiley + y));
                    if (height < lowest) {
                        lowest = height;
                        lowestX = (short)(tilex + x);
                        lowestY = (short)(tiley + y);
                    }
                }
            }
        }
        return new short[] { lowestX, lowestY };
    }
    
    public void setSecondTitle(final Titles.Title title) {
    }
    
    public void setTitle(final Titles.Title title) {
    }
    
    public Titles.Title getSecondTitle() {
        return null;
    }
    
    public Titles.Title getTitle() {
        return null;
    }
    
    public String getTitleString() {
        String suff = "";
        if (this.getTitle() != null) {
            if (this.getTitle().isRoyalTitle()) {
                if (this.getAppointments() != 0L || this.isAppointed()) {
                    suff += this.getKingdomTitle();
                }
            }
            else {
                suff += this.getTitle().getName(this.isNotFemale());
            }
        }
        if (Features.Feature.COMPOUND_TITLES.isEnabled() && this.getSecondTitle() != null) {
            if (this.getTitle() != null) {
                suff += " ";
            }
            if (this.getSecondTitle().isRoyalTitle()) {
                if (this.getAppointments() != 0L || this.isAppointed()) {
                    suff += this.getKingdomTitle();
                }
            }
            else {
                suff += this.getSecondTitle().getName(this.isNotFemale());
            }
        }
        return suff;
    }
    
    public String getKingdomTitle() {
        return "";
    }
    
    public void setFinestAppointment() {
    }
    
    public float getSpellDamageProtectBonus() {
        return this.getBonusForSpellEffect((byte)19);
    }
    
    public float getDetectDangerBonus() {
        if (this.getKingdomTemplateId() == 3) {
            return 50.0f + ItemBonus.getDetectionBonus(this);
        }
        final SpellEffects eff = this.getSpellEffects();
        if (eff != null) {
            final SpellEffect effbon = eff.getSpellEffect((byte)21);
            if (effbon != null) {
                return effbon.power + ItemBonus.getDetectionBonus(this);
            }
        }
        return ItemBonus.getDetectionBonus(this);
    }
    
    public float getBonusForSpellEffect(final byte enchantment) {
        final SpellEffects eff = this.getSpellEffects();
        if (eff != null) {
            final SpellEffect skillgain = eff.getSpellEffect(enchantment);
            if (skillgain != null) {
                return skillgain.power;
            }
        }
        return 0.0f;
    }
    
    public float getNoLocateItemBonus(final boolean reducePower) {
        final Item[] bodyItems = this.getBody().getContainersAndWornItems();
        float maxBonus = 0.0f;
        Item maxItem = null;
        for (int x = 0; x < bodyItems.length; ++x) {
            if ((bodyItems[x].isEnchantableJewelry() || bodyItems[x].isArtifact()) && bodyItems[x].getNolocateBonus() > maxBonus) {
                maxBonus = bodyItems[x].getNolocateBonus();
                maxItem = bodyItems[x];
            }
        }
        if (maxItem != null) {
            maxBonus = (maxBonus + maxItem.getCurrentQualityLevel()) / 2.0f;
            ItemSpellEffects effs = maxItem.getSpellEffects();
            if (effs == null) {
                effs = new ItemSpellEffects(maxItem.getWurmId());
            }
            final SpellEffect eff = effs.getSpellEffect((byte)29);
            if (eff != null && reducePower) {
                eff.setPower(eff.power - 0.2f);
            }
        }
        return maxBonus;
    }
    
    public int getNumberOfShopItems() {
        final Set<Item> ite = this.getInventory().getItems();
        int nums = 0;
        for (final Item i : ite) {
            if (!i.isCoin()) {
                ++nums;
            }
        }
        return nums;
    }
    
    public final void addNewbieBuffs() {
        if (this.getPlayingTime() < 86400000L) {
            final SpellEffects effs = this.createSpellEffects();
            SpellEffect eff = effs.getSpellEffect((byte)74);
            if (eff == null) {
                this.getCommunicator().sendSafeServerMessage("You require less food and drink as a new player.");
                eff = new SpellEffect(this.getWurmId(), (byte)74, 100.0f, (int)((86400000L - this.getPlayingTime()) / 1000L), (byte)1, (byte)0, true);
                effs.addSpellEffect(eff);
            }
            SpellEffect range = effs.getSpellEffect((byte)73);
            if (range == null) {
                this.getCommunicator().sendSafeServerMessage("Creatures and monsters are less aggressive to new players.");
                range = new SpellEffect(this.getWurmId(), (byte)73, 100.0f, (int)((86400000L - this.getPlayingTime()) / 1000L), (byte)1, (byte)0, true);
                effs.addSpellEffect(range);
            }
            SpellEffect health = effs.getSpellEffect((byte)75);
            if (health == null) {
                this.getCommunicator().sendSafeServerMessage("You regenerate health faster as a new player.");
                health = new SpellEffect(this.getWurmId(), (byte)75, 100.0f, (int)((86400000L - this.getPlayingTime()) / 1000L), (byte)1, (byte)0, true);
                effs.addSpellEffect(health);
            }
        }
    }
    
    public SpellEffects getSpellEffects() {
        return this.getStatus().spellEffects;
    }
    
    public void sendUpdateSpellEffect(final SpellEffect effect) {
        final SpellEffectsEnum spellEffect = SpellEffectsEnum.getEnumByName(effect.getName());
        if (spellEffect != SpellEffectsEnum.NONE) {
            this.getCommunicator().sendAddSpellEffect(effect.id, spellEffect, effect.timeleft, effect.power);
        }
        else {
            this.getCommunicator().sendAddSpellEffect(effect.id, effect.getName(), effect.type, effect.getSpellEffectType(), effect.getSpellInfluenceType(), effect.timeleft, effect.power);
        }
    }
    
    public void sendAddSpellEffect(final SpellEffect effect) {
        final SpellEffectsEnum spellEffect = SpellEffectsEnum.getEnumByName(effect.getName());
        if (spellEffect != SpellEffectsEnum.NONE) {
            this.getCommunicator().sendAddSpellEffect(effect.id, spellEffect, effect.timeleft, effect.power);
        }
        else {
            this.getCommunicator().sendAddSpellEffect(effect.id, effect.getName(), effect.type, effect.getSpellEffectType(), effect.getSpellInfluenceType(), effect.timeleft, effect.power);
        }
        if (effect.type == 23) {
            this.getCombatHandler().addDodgeModifier(Creature.willowMod);
        }
        else if (effect.type == 39) {
            this.getMovementScheme().setChargeMoveMod(true);
        }
    }
    
    public void removeSpellEffect(final SpellEffect effect) {
        final SpellEffectsEnum spellEffect = SpellEffectsEnum.getEnumByName(effect.getName());
        if (spellEffect != SpellEffectsEnum.NONE) {
            this.getCommunicator().sendRemoveSpellEffect(effect.id, spellEffect);
        }
        else {
            this.getCommunicator().sendRemoveSpellEffect(effect.id, null);
        }
        this.getCommunicator().sendNormalServerMessage("You are no longer affected by " + effect.getName() + ".");
        if (effect.type == 23) {
            this.getCombatHandler().removeDodgeModifier(Creature.willowMod);
        }
        else if (effect.type == 39) {
            this.getMovementScheme().setChargeMoveMod(false);
        }
        else if (effect.type == 64) {
            this.setVisible(false);
            this.refreshAttitudes();
            this.setVisible(true);
        }
        else if (effect.type == 72) {
            this.setModelName("Human");
        }
    }
    
    public final void removeIllusion() {
        if (this.getSpellEffects() != null) {
            final SpellEffect ill = this.getSpellEffects().getSpellEffect((byte)72);
            if (ill != null) {
                this.getSpellEffects().removeSpellEffect(ill);
            }
        }
    }
    
    public void sendRemovePhantasms() {
    }
    
    public SpellEffects createSpellEffects() {
        if (this.getStatus().spellEffects == null) {
            this.getStatus().spellEffects = new SpellEffects(this.getWurmId());
        }
        return this.getStatus().spellEffects;
    }
    
    @Deprecated
    public boolean dispelSpellEffect(final double power) {
        boolean toret = false;
        if (this.getMovementScheme().setWebArmourMod(false, 0.0f)) {
            this.getMovementScheme().setWebArmourMod(false, 0.0f);
            toret = true;
        }
        if (this.getSpellEffects() != null) {
            final SpellEffect[] speffs = this.getSpellEffects().getEffects();
            for (int x = 0; x < speffs.length; ++x) {
                if (speffs[x].type != 64 && speffs[x].type != 74 && speffs[x].type != 73 && speffs[x].type != 75 && Server.rand.nextInt(Math.max(1, (int)speffs[x].power)) < power) {
                    this.getSpellEffects().removeSpellEffect(speffs[x]);
                    if (speffs[x].type == 22 && this.getCurrentTile() != null) {
                        this.getCurrentTile().setNewRarityShader(this);
                    }
                    return true;
                }
            }
        }
        return toret;
    }
    
    public byte getFarwalkerSeconds() {
        return 0;
    }
    
    protected void setFarwalkerSeconds(final byte seconds) {
    }
    
    public void activeFarwalkerAmulet(final Item amulet) {
    }
    
    public Creature getDominator() {
        if (this.dominator == -10L) {
            return null;
        }
        try {
            return Server.getInstance().getCreature(this.dominator);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public Item getWornItem(final byte bodyPart) {
        try {
            return this.getEquippedItem(bodyPart);
        }
        catch (NoSpaceException nsp) {
            return null;
        }
    }
    
    public boolean hasBridle() {
        if (this.isHorse() || this.isUnicorn()) {
            final Item neckItem = this.getWornItem((byte)17);
            if (neckItem != null) {
                return neckItem.isBridle();
            }
        }
        return false;
    }
    
    private float calcHorseShoeBonus(final boolean mounting) {
        float bonus = 0.0f;
        float leftFootB = 0.0f;
        float rightFootB = 0.0f;
        float leftHandB = 0.0f;
        float rightHandB = 0.0f;
        try {
            final Item leftFoot = this.getEquippedItem((byte)15);
            if (leftFoot != null) {
                leftFootB += Math.max(10.0f, leftFoot.getCurrentQualityLevel()) / 2000.0f;
                leftFootB += leftFoot.getSpellSpeedBonus() / 2000.0f;
                leftFootB += leftFoot.getRarity() * 0.03f;
                if (!mounting && !this.ignoreSaddleDamage) {
                    leftFoot.setDamage(leftFoot.getDamage() + 0.001f);
                }
            }
        }
        catch (NoSpaceException nsp) {
            Creature.logger.log(Level.WARNING, this.getName() + " No left foot.");
        }
        try {
            final Item rightFoot = this.getEquippedItem((byte)16);
            if (rightFoot != null) {
                rightFootB += Math.max(10.0f, rightFoot.getCurrentQualityLevel()) / 2000.0f;
                rightFootB += rightFoot.getSpellSpeedBonus() / 2000.0f;
                rightFootB += rightFoot.getRarity() * 0.03f;
                if (!mounting && !this.ignoreSaddleDamage) {
                    rightFoot.setDamage(rightFoot.getDamage() + 0.001f);
                }
            }
        }
        catch (NoSpaceException nsp) {
            Creature.logger.log(Level.WARNING, this.getName() + " No left foot.");
        }
        try {
            final Item rightHand = this.getEquippedItem((byte)14);
            if (rightHand != null) {
                rightHandB += Math.max(10.0f, rightHand.getCurrentQualityLevel()) / 2000.0f;
                rightHandB += rightHand.getSpellSpeedBonus() / 2000.0f;
                rightHandB += rightHand.getRarity() * 0.03f;
                if (!mounting && !this.ignoreSaddleDamage) {
                    rightHand.setDamage(rightHand.getDamage() + 0.001f);
                }
            }
        }
        catch (NoSpaceException nsp) {
            Creature.logger.log(Level.WARNING, this.getName() + " No left foot.");
        }
        try {
            final Item leftHand = this.getEquippedItem((byte)13);
            if (leftHand != null) {
                leftHandB += Math.max(10.0f, leftHand.getCurrentQualityLevel()) / 2000.0f;
                leftHandB += leftHand.getSpellSpeedBonus() / 2000.0f;
                leftHandB += leftHand.getRarity() * 0.03f;
                if (!mounting && !this.ignoreSaddleDamage) {
                    leftHand.setDamage(leftHand.getDamage() + 0.001f);
                }
            }
        }
        catch (NoSpaceException nsp) {
            Creature.logger.log(Level.WARNING, this.getName() + " No left foot.");
        }
        bonus += leftHandB;
        bonus += rightHandB;
        bonus += leftFootB;
        bonus += rightFootB;
        return bonus;
    }
    
    public boolean hasHands() {
        return this.template.hasHands;
    }
    
    public boolean isDominated() {
        return this.dominator > 0L;
    }
    
    public boolean setDominator(final long newdominator) {
        if (newdominator == -10L) {
            if (this.decisions != null) {
                this.decisions.clearOrders();
                this.decisions = null;
            }
            try {
                this.setKingdomId((byte)0);
            }
            catch (IOException iox) {
                Creature.logger.log(Level.WARNING, iox.getMessage(), iox);
            }
            this.setLoyalty(0.0f);
            this.setLeader(null);
        }
        if (newdominator != this.dominator) {
            this.dominator = newdominator;
            this.getStatus().setDominator(this.dominator);
            this.sendAttitudeChange();
            return true;
        }
        return false;
    }
    
    public boolean hasPet() {
        return false;
    }
    
    public boolean isOnFire() {
        return this.template.isOnFire();
    }
    
    public byte getFireRadius() {
        return this.template.getFireRadius();
    }
    
    public int getPaintMode() {
        return this.template.getPaintMode();
    }
    
    public boolean addOrder(final Order order) {
        if (this.decisions == null) {
            this.decisions = new DecisionStack();
        }
        return this.decisions.addOrder(order);
    }
    
    public void clearOrders() {
        if (this.decisions != null) {
            this.decisions.clearOrders();
        }
        this.getStatus().setPath(null);
        this.getStatus().setMoving(false);
        this.setTarget(-10L, true);
    }
    
    public Order getFirstOrder() {
        if (this.decisions != null) {
            return this.decisions.getFirst();
        }
        return null;
    }
    
    public void removeOrder(final Order order) {
        if (this.decisions != null) {
            this.decisions.removeOrder(order);
        }
    }
    
    public boolean hasOrders() {
        return this.decisions != null && this.decisions.hasOrders();
    }
    
    public boolean mayReceiveOrder() {
        if (this.decisions != null) {
            return this.decisions.mayReceiveOrders();
        }
        if (this.isDominated()) {
            this.decisions = new DecisionStack();
            return true;
        }
        return false;
    }
    
    public void setPet(final long petId) {
    }
    
    public Creature getPet() {
        return null;
    }
    
    public void modifyLoyalty(final float modifier) {
        if (this.getStatus().modifyLoyalty(modifier)) {
            if (this.getDominator() != null) {
                this.getDominator().getCommunicator().sendAlertServerMessage(this.getNameWithGenus() + " is tame no more.", (byte)2);
                this.getDominator().setPet(-10L);
            }
            this.setDominator(-10L);
        }
    }
    
    public void setLoyalty(final float loyalty) {
        this.getStatus().setLoyalty(loyalty);
    }
    
    public float getLoyalty() {
        return this.getStatus().loyalty;
    }
    
    public ArmourTemplate.ArmourType getArmourType() {
        return this.template.getArmourType();
    }
    
    public boolean isFrozen() {
        return false;
    }
    
    public void toggleFrozen(final Creature freezer) {
    }
    
    protected void setLastVehicle(final long _lastvehicle, final byte _seatType) {
        this.status.setVehicle(_lastvehicle, _seatType);
    }
    
    public void setVehicle(final long vehicle, final boolean teleport, final byte _seatType) {
        this.setVehicle(vehicle, teleport, _seatType, -1, -1);
    }
    
    public void setVehicle(final long _vehicle, final boolean teleport, final byte _seatType, int tilex, int tiley) {
        if (_vehicle == -10L) {
            if (this.vehicle != -10L) {
                this.removeIllusion();
                if (this.getVisionArea() != null) {
                    if (this.getVisionArea().getSurface() != null) {
                        this.getVisionArea().getSurface().clearMovementForCreature(this.vehicle);
                    }
                    if (this.getVisionArea().getUnderGround() != null) {
                        this.getVisionArea().getUnderGround().clearMovementForCreature(this.vehicle);
                    }
                }
                if (WurmId.getType(this.vehicle) == 1) {
                    this.setLastVehicle(-10L, (byte)(-1));
                    try {
                        final Creature lVehicle = Server.getInstance().getCreature(this.vehicle);
                        lVehicle.removeRider(this.getWurmId());
                        if (teleport) {
                            final Structure struct = this.getActualTileVehicle().getStructure();
                            if (struct != null && !struct.mayPass(this)) {
                                try {
                                    final float newposx = lVehicle.getPosX();
                                    final float newposy = lVehicle.getPosY();
                                    tilex = (int)newposx / 4;
                                    tiley = (int)newposy / 4;
                                }
                                catch (Exception ex) {
                                    Creature.logger.log(Level.WARNING, ex.getMessage(), ex);
                                }
                            }
                            if (!this.isOnSurface() && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(tilex, tiley)))) {
                                try {
                                    final float newposx = lVehicle.getPosX();
                                    final float newposy = lVehicle.getPosY();
                                    tilex = (int)newposx / 4;
                                    tiley = (int)newposy / 4;
                                }
                                catch (Exception ex) {
                                    Creature.logger.log(Level.WARNING, ex.getMessage(), ex);
                                }
                            }
                        }
                    }
                    catch (NoSuchCreatureException nsi) {
                        Creature.logger.log(Level.WARNING, this.getName() + " " + nsi.getMessage(), nsi);
                    }
                    catch (NoSuchPlayerException nsp) {
                        Creature.logger.log(Level.WARNING, this.getName() + " " + nsp.getMessage(), nsp);
                    }
                }
                else {
                    try {
                        final Item ivehic = Items.getItem(this.vehicle);
                        boolean atTransferBorder = false;
                        if (this.getTileX() < 20 || this.getTileX() > Zones.worldTileSizeX - 20 || this.getTileY() < 20 || this.getTileY() > Zones.worldTileSizeX - 20) {
                            atTransferBorder = true;
                        }
                        if (!ivehic.isBoat() || (!this.isTransferring() && !atTransferBorder)) {
                            this.setLastVehicle(-10L, (byte)(-1));
                            if (teleport) {
                                final Structure struct2 = this.getActualTileVehicle().getStructure();
                                if (struct2 != null && struct2.isTypeHouse() && !struct2.mayPass(this)) {
                                    try {
                                        final Creature dragger = Items.getDragger(ivehic);
                                        final float newposx2 = (dragger == null) ? ivehic.getPosX() : dragger.getPosX();
                                        final float newposy2 = (dragger == null) ? ivehic.getPosY() : dragger.getPosY();
                                        tilex = (int)newposx2 / 4;
                                        tiley = (int)newposy2 / 4;
                                    }
                                    catch (Exception ex2) {
                                        Creature.logger.log(Level.WARNING, ex2.getMessage(), ex2);
                                    }
                                }
                                if (struct2 != null && struct2.isTypeBridge()) {
                                    try {
                                        final Creature dragger = Items.getDragger(ivehic);
                                        final float newposx2 = (dragger == null) ? ivehic.getPosX() : dragger.getPosX();
                                        final float newposy2 = (dragger == null) ? ivehic.getPosY() : dragger.getPosY();
                                        tilex = (int)newposx2 / 4;
                                        tiley = (int)newposy2 / 4;
                                    }
                                    catch (Exception ex2) {
                                        Creature.logger.log(Level.WARNING, ex2.getMessage(), ex2);
                                    }
                                }
                                if (!this.isOnSurface() && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(tilex, tiley)))) {
                                    try {
                                        final float newposx3 = ivehic.getPosX();
                                        final float newposy3 = ivehic.getPosY();
                                        tilex = (int)newposx3 / 4;
                                        tiley = (int)newposy3 / 4;
                                    }
                                    catch (Exception ex2) {
                                        Creature.logger.log(Level.WARNING, ex2.getMessage(), ex2);
                                    }
                                }
                            }
                        }
                    }
                    catch (NoSuchItemException nsi4) {
                        this.setLastVehicle(-10L, (byte)(-1));
                    }
                }
            }
            this.getMovementScheme().offZ = 0.0f;
        }
        this.vehicle = _vehicle;
        this.seatType = _seatType;
        if (!this.isPlayer()) {
            this.setLastVehicle(_vehicle, _seatType);
        }
        if (this.vehicle != -10L) {
            this.removeIllusion();
            final Vehicle vehic = Vehicles.getVehicleForId(this.vehicle);
            if (vehic != null) {
                this.clearDestination();
                this.setFarwalkerSeconds((byte)0);
                this.getMovementScheme().setFarwalkerMoveMod(false);
                this.movementScheme.setEncumbered(false);
                this.movementScheme.setBaseModifier(1.0f);
                this.setStealth(false);
                float offx = 0.0f;
                float offy = 0.0f;
                for (int x = 0; x < vehic.seats.length; ++x) {
                    if (vehic.seats[x].occupant == this.getWurmId()) {
                        offx = vehic.seats[x].offx;
                        offy = vehic.seats[x].offy;
                        break;
                    }
                }
                if (vehic.creature) {
                    try {
                        final Creature lVehicle2 = Server.getInstance().getCreature(this.vehicle);
                        final float r = (-lVehicle2.getStatus().getRotation() + 180.0f) * 3.1415927f / 180.0f;
                        final float s = (float)Math.sin(r);
                        final float c = (float)Math.cos(r);
                        final float xo = s * -offx - c * -offy;
                        final float yo = c * -offx + s * -offy;
                        final float newposx4 = lVehicle2.getPosX() + xo;
                        final float newposy4 = lVehicle2.getPosY() + yo;
                        this.getMovementScheme().setVehicleRotation(lVehicle2.getStatus().getRotation());
                        this.getStatus().setRotation(lVehicle2.getStatus().getRotation());
                        this.setBridgeId(lVehicle2.getBridgeId());
                        this.setTeleportPoints(newposx4, newposy4, lVehicle2.getLayer(), lVehicle2.getFloorLevel());
                        if (this.getVisionArea() != null && (int)newposx4 >> 2 == this.getTileX() && (int)newposy4 >> 2 == this.getTileY()) {
                            this.embark(newposx4, newposy4, this.getPositionZ(), this.getStatus().getRotation(), this.teleportLayer, "Embarking " + vehic.name, null, lVehicle2, vehic);
                        }
                        else if (!this.getCommunicator().stillLoggingIn()) {
                            final int tx = this.getTileX();
                            final int ty = this.getTileY();
                            final int nx = (int)newposx4 >> 2;
                            final int ny = (int)newposy4 >> 2;
                            try {
                                if (this.hasLink() && this.getVisionArea() != null) {
                                    this.getVisionArea().move(nx - tx, ny - ty);
                                    this.embark(newposx4, newposy4, this.getPositionZ(), this.getStatus().getRotation(), this.teleportLayer, "Embarking " + vehic.name, null, lVehicle2, vehic);
                                    this.getVisionArea().linkZones(nx - tx, ny - ty);
                                }
                            }
                            catch (IOException ex4) {
                                this.startTeleporting(true);
                                lVehicle2.setLeader(null);
                                lVehicle2.addRider(this.getWurmId());
                                this.sendMountData();
                                if (this.isVehicleCommander()) {
                                    this.getCommunicator().sendTeleport(true, false, vehic.commandType);
                                }
                                else {
                                    this.getCommunicator().sendTeleport(false, false, (byte)0);
                                }
                            }
                        }
                        else {
                            this.startTeleporting(true);
                            lVehicle2.setLeader(null);
                            lVehicle2.addRider(this.getWurmId());
                            this.sendMountData();
                            if (this.isVehicleCommander()) {
                                this.getCommunicator().sendTeleport(true, false, vehic.commandType);
                            }
                            else {
                                this.getCommunicator().sendTeleport(false, false, (byte)0);
                            }
                        }
                    }
                    catch (NoSuchCreatureException nsi2) {
                        Creature.logger.log(Level.WARNING, this.getName() + " " + nsi2.getMessage(), nsi2);
                    }
                    catch (NoSuchPlayerException nsp2) {
                        Creature.logger.log(Level.WARNING, this.getName() + " " + nsp2.getMessage(), nsp2);
                    }
                }
                else {
                    try {
                        final Item lVehicle3 = Items.getItem(vehic.wurmid);
                        final float r = (-lVehicle3.getRotation() + 180.0f) * 3.1415927f / 180.0f;
                        final float s = (float)Math.sin(r);
                        final float c = (float)Math.cos(r);
                        final float xo = s * -offx - c * -offy;
                        final float yo = c * -offx + s * -offy;
                        final float newposx4 = lVehicle3.getPosX() + xo;
                        final float newposy4 = lVehicle3.getPosY() + yo;
                        this.getMovementScheme().setVehicleRotation(lVehicle3.getRotation());
                        this.getStatus().setRotation(lVehicle3.getRotation());
                        this.setBridgeId(lVehicle3.getBridgeId());
                        if (this.getVisionArea() != null && (int)newposx4 >> 2 == this.getTileX() && (int)newposy4 >> 2 == this.getTileY()) {
                            this.embark(newposx4, newposy4, this.getPositionZ(), this.getStatus().getRotation(), this.teleportLayer, "Embarking " + vehic.name, lVehicle3, null, vehic);
                        }
                        else {
                            this.setTeleportPoints(newposx4, newposy4, lVehicle3.isOnSurface() ? 0 : -1, lVehicle3.getFloorLevel());
                            if (this.isVehicleCommander()) {
                                if (lVehicle3.getKingdom() != this.getKingdomId()) {
                                    Server.getInstance().broadCastAction(LoginHandler.raiseFirstLetter(lVehicle3.getName()) + " is now the property of " + Kingdoms.getNameFor(this.getKingdomId()) + "!", this, 10);
                                    final String message = StringUtil.format("You declare the %s the property of %s.", lVehicle3.getName(), Kingdoms.getNameFor(this.getKingdomId()));
                                    this.getCommunicator().sendNormalServerMessage(message);
                                    lVehicle3.setLastOwnerId(this.getWurmId());
                                }
                                else if (Servers.isThisAChaosServer()) {
                                    final Village v = Villages.getVillageForCreature(lVehicle3.getLastOwnerId());
                                    if (v == null || v.isEnemy(this.getCitizenVillage())) {
                                        String vehname = this.getName();
                                        if (this.getCitizenVillage() != null) {
                                            vehname = this.getCitizenVillage().getName();
                                        }
                                        Server.getInstance().broadCastAction(LoginHandler.raiseFirstLetter(lVehicle3.getName()) + " is now the property of " + vehname + "!", this, 10);
                                        final String message2 = StringUtil.format("You declare the %s the property of %s.", lVehicle3.getName(), vehname);
                                        this.getCommunicator().sendNormalServerMessage(message2);
                                        lVehicle3.setLastOwnerId(this.getWurmId());
                                    }
                                }
                                lVehicle3.setAuxData(this.getKingdomId());
                                this.setEmbarkTeleportVehicle(newposx4, newposy4, vehic, lVehicle3);
                            }
                            else {
                                this.setEmbarkTeleportVehicle(newposx4, newposy4, vehic, lVehicle3);
                            }
                        }
                    }
                    catch (NoSuchItemException nsi3) {
                        Creature.logger.log(Level.WARNING, this.getName() + " " + nsi3.getMessage(), nsi3);
                    }
                }
            }
        }
        else if (teleport) {
            if (tilex < 0 && tiley < 0 && !this.isOnSurface() && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile((int)(this.getStatus().getPositionX() / 4.0f), (int)(this.getStatus().getPositionX() / 4.0f))))) {
                try {
                    if (WurmId.getType(this.vehicle) == 1) {
                        final Creature lVehicle = Server.getInstance().getCreature(this.vehicle);
                        final float newposx5 = lVehicle.getPosX();
                        final float newposy5 = lVehicle.getPosY();
                        tilex = (int)newposx5 / 4;
                        tiley = (int)newposy5 / 4;
                    }
                    else {
                        final Item ivehic = Items.getItem(this.vehicle);
                        final float newposx5 = ivehic.getPosX();
                        final float newposy5 = ivehic.getPosY();
                        tilex = (int)newposx5 / 4;
                        tiley = (int)newposy5 / 4;
                    }
                }
                catch (Exception ex3) {
                    Creature.logger.log(Level.WARNING, ex3.getMessage(), ex3);
                }
            }
            if (tilex > -1 || tiley > -1) {
                final int ntx = tilex - this.getTileX();
                final int nty = tiley - this.getTileY();
                float posz = this.getStatus().getPositionZ();
                posz = Zones.calculatePosZ(this.getPosX(), this.getPosY(), this.getCurrentTile(), this.isOnSurface(), false, posz, this, this.getBridgeId());
                try {
                    if (this.hasLink() && this.getVisionArea() != null) {
                        this.getVisionArea().move(ntx, nty);
                        this.intraTeleport(tilex * 4 + 2, tiley * 4 + 2, posz, this.getStatus().getRotation(), this.getLayer(), "left vehicle");
                        this.getVisionArea().linkZones(ntx, nty);
                    }
                }
                catch (IOException ex5) {
                    this.setTeleportPoints((short)tilex, (short)tiley, this.getLayer(), 0);
                    this.startTeleporting(false);
                    this.getCommunicator().sendTeleport(false, true, (byte)0);
                }
            }
            else {
                final Structure struct3 = (this.getCurrentTile() != null) ? this.getCurrentTile().getStructure() : Structures.getStructureForTile(this.getTileX(), this.getTileY(), this.isOnSurface());
                if (struct3 == null || struct3.mayPass(this)) {
                    float posz2 = this.getStatus().getPositionZ();
                    posz2 = Zones.calculatePosZ(this.getPosX(), this.getPosY(), this.getCurrentTile(), this.isOnSurface(), false, posz2, this, this.getBridgeId());
                    this.intraTeleport(this.getStatus().getPositionX(), this.getStatus().getPositionY(), posz2, this.getStatus().getRotation(), this.getLayer(), "left vehicle");
                }
            }
            this.getMovementScheme().addWindImpact((byte)0);
            this.calcBaseMoveMod();
            this.getMovementScheme().commandingBoat = false;
            this.getCurrentTile().sendAttachCreature(this.getWurmId(), -1L, 0.0f, 0.0f, 0.0f, 0);
        }
        else {
            if (!this.getMovementScheme().isIntraTeleporting()) {
                this.getMovementScheme().addWindImpact((byte)0);
                this.calcBaseMoveMod();
                this.getMovementScheme().setMooredMod(false);
                this.getMovementScheme().commandingBoat = false;
            }
            this.getCurrentTile().sendAttachCreature(this.getWurmId(), -1L, 0.0f, 0.0f, 0.0f, 0);
        }
    }
    
    public void intraTeleport(float posx, float posy, final float posz, final float aRot, int layer, final String reason) {
        if (reason.contains("in rock")) {
            posx = this.getMovementScheme().xOld;
            posy = this.getMovementScheme().yOld;
        }
        ++this.teleports;
        if (this.isDead()) {
            return;
        }
        posx = Math.max(0.0f, Math.min(posx, Zones.worldMeterSizeX - 1.0f));
        posy = Math.max(0.0f, Math.min(posy, Zones.worldMeterSizeY - 1.0f));
        final VolaTile t = this.getCurrentTile();
        if (t != null) {
            t.deleteCreatureQuick(this);
        }
        else {
            Creature.logger.log(Level.INFO, this.getName() + " no current tile when intrateleporting.");
        }
        this.getStatus().setPositionX(posx);
        this.getStatus().setPositionY(posy);
        this.getStatus().setPositionZ(posz);
        this.getStatus().setRotation(aRot);
        if (layer == 0 && Zones.getTextureForTile((int)posx >> 2, (int)posy >> 2, layer) == Tiles.Tile.TILE_HOLE.id) {
            layer = -1;
        }
        boolean visionAreaInitialized = false;
        if (this.getVisionArea() != null) {
            visionAreaInitialized = this.getVisionArea().isInitialized();
        }
        if (!reason.contains("Embarking") && !reason.contains("left vehicle")) {
            Creature.logger.log(Level.INFO, this.getName() + " intrateleport to " + posx + "," + posy + ", " + posz + ", layer " + layer + " currentTile:null=" + (t == null) + " reason=" + reason + " hasVisionArea=" + (this.getVisionArea() != null) + ", initialized=" + visionAreaInitialized + " vehicle=" + this.vehicle, new Exception());
            if (this.getPower() >= 3) {
                this.getCommunicator().sendAlertServerMessage("IntraTeleporting " + reason);
            }
        }
        this.getMovementScheme().setPosition(posx, posy, posz, aRot, layer);
        this.putInWorld();
        this.getMovementScheme().haltSpeedModifier();
        this.getCommunicator().setReady(false);
        this.getMovementScheme().setMooredMod(false);
        this.addCarriedWeight(0);
        try {
            this.sendActionControl("", false, 0);
            this.actions.stopCurrentAction(false);
        }
        catch (NoSuchActionException ex) {}
        this.getMovementScheme().commandingBoat = false;
        this.getMovementScheme().addWindImpact((byte)0);
        this.getCommunicator().sendTeleport(true);
        this.disembark(false);
        this.getMovementScheme().addIntraTeleport(this.getTeleportCounter());
    }
    
    public Vector3f getActualPosVehicle() {
        final Vector3f toReturn = new Vector3f(this.getPosX(), this.getPosY(), this.getPositionZ());
        if (this.vehicle != -10L) {
            final Vehicle vehic = Vehicles.getVehicleForId(this.vehicle);
            if (vehic != null) {
                float offx = 0.0f;
                float offy = 0.0f;
                for (int x = 0; x < vehic.seats.length; ++x) {
                    if (vehic.seats[x].occupant == this.getWurmId()) {
                        offx = vehic.seats[x].offx;
                        offy = vehic.seats[x].offy;
                        break;
                    }
                }
                if (vehic.creature) {
                    try {
                        final Creature lVehicle = Server.getInstance().getCreature(this.vehicle);
                        final float r = (-lVehicle.getStatus().getRotation() + 180.0f) * 3.1415927f / 180.0f;
                        final float s = (float)Math.sin(r);
                        final float c = (float)Math.cos(r);
                        final float xo = s * -offx - c * -offy;
                        final float yo = c * -offx + s * -offy;
                        final float newposx = lVehicle.getPosX() + xo;
                        final float newposy = lVehicle.getPosY() + yo;
                        toReturn.setX(newposx);
                        toReturn.setY(newposy);
                    }
                    catch (NoSuchCreatureException | NoSuchPlayerException ex) {}
                }
                else {
                    try {
                        final Item lVehicle2 = Items.getItem(vehic.wurmid);
                        final float r = (-lVehicle2.getRotation() + 180.0f) * 3.1415927f / 180.0f;
                        final float s = (float)Math.sin(r);
                        final float c = (float)Math.cos(r);
                        final float xo = s * -offx - c * -offy;
                        final float yo = c * -offx + s * -offy;
                        final float newposx = lVehicle2.getPosX() + xo;
                        final float newposy = lVehicle2.getPosY() + yo;
                        toReturn.setX(newposx);
                        toReturn.setY(newposy);
                    }
                    catch (NoSuchItemException ex2) {}
                }
            }
        }
        return toReturn;
    }
    
    protected VolaTile getActualTileVehicle() {
        final Vector3f v = this.getActualPosVehicle();
        final int nx = (int)v.x >> 2;
        final int ny = (int)v.y >> 2;
        return Zones.getOrCreateTile(nx, ny, this.isOnSurface());
    }
    
    protected void setEmbarkTeleportVehicle(final float newposx, final float newposy, final Vehicle vehic, final Item lVehicle) {
        if (!this.getCommunicator().stillLoggingIn()) {
            final int tx = this.getTileX();
            final int ty = this.getTileY();
            final int nx = (int)newposx >> 2;
            final int ny = (int)newposy >> 2;
            try {
                if ((this.hasLink() || this.isWagoner()) && this.getVisionArea() != null) {
                    this.getVisionArea().move(nx - tx, ny - ty);
                    this.embark(newposx, newposy, this.getPositionZ(), this.getStatus().getRotation(), this.teleportLayer, "Embarking " + vehic.name, lVehicle, null, vehic);
                    this.getVisionArea().linkZones(nx - tx, ny - ty);
                }
            }
            catch (IOException ex) {
                this.startTeleporting(true);
                this.sendMountData();
                this.getCommunicator().sendTeleport(true, false, vehic.commandType);
            }
        }
        else {
            this.startTeleporting(true);
            this.sendMountData();
            if (this.isVehicleCommander()) {
                this.getCommunicator().sendTeleport(true, false, vehic.commandType);
            }
            else {
                this.getCommunicator().sendTeleport(false, false, (byte)0);
            }
        }
    }
    
    private void embark(final float posx, final float posy, final float posz, final float aRot, int layer, final String reason, @Nullable final Item lVehicle, final Creature cVehicle, final Vehicle vehic) {
        if (!this.isVehicleCommander()) {
            this.stopLeading();
        }
        final VolaTile t = this.getCurrentTile();
        if (t != null) {
            t.deleteCreatureQuick(this);
        }
        else {
            Creature.logger.log(Level.INFO, this.getName() + " no current tile when intrateleporting.");
        }
        this.getStatus().setPositionX(posx);
        this.getStatus().setPositionY(posy);
        this.getStatus().setPositionZ(posz);
        this.getStatus().setRotation(aRot);
        if (layer == 0 && Zones.getTextureForTile((int)posx >> 2, (int)posy >> 2, layer) == Tiles.Tile.TILE_HOLE.id) {
            layer = -1;
        }
        boolean setOffZ = false;
        if (this.mountAction != null) {
            setOffZ = true;
        }
        if (setOffZ) {
            if (lVehicle != null) {
                final float targetZ = lVehicle.getPosZ();
                this.status.setPositionZ(targetZ + this.mountAction.getOffZ());
            }
            else if (cVehicle != null) {
                final float cretZ = cVehicle.getStatus().getPositionZ();
                this.status.setPositionZ(cretZ + this.mountAction.getOffZ());
            }
            this.getMovementScheme().offZ = this.mountAction.getOffZ();
        }
        this.getMovementScheme().setPosition(posx, posy, this.status.getPositionZ(), this.status.getRotation(), this.getLayer());
        this.putInWorld();
        this.getMovementScheme().haltSpeedModifier();
        this.getCommunicator().setReady(false);
        if (this.status.isTrading()) {
            this.status.getTrade().end(this, false);
        }
        if (this.movementScheme.draggedItem != null) {
            MethodsItems.stopDragging(this, this.movementScheme.draggedItem);
        }
        try {
            this.sendActionControl("", false, 0);
            this.actions.stopCurrentAction(false);
        }
        catch (NoSuchActionException ex) {}
        this._enterVehicle = true;
        if (cVehicle != null) {
            cVehicle.setLeader(null);
            cVehicle.addRider(this.getWurmId());
        }
        this.sendMountData();
        if (this.isVehicleCommander()) {
            if (lVehicle != null) {
                if (lVehicle.getKingdom() != this.getKingdomId()) {
                    Server.getInstance().broadCastAction(LoginHandler.raiseFirstLetter(lVehicle.getName()) + " is now the property of " + Kingdoms.getNameFor(this.getKingdomId()) + "!", this, 10);
                    final String message = StringUtil.format("You declare the %s the property of %s.", lVehicle.getName(), Kingdoms.getNameFor(this.getKingdomId()));
                    this.getCommunicator().sendNormalServerMessage(message);
                    lVehicle.setLastOwnerId(this.getWurmId());
                }
                else if (Servers.isThisAChaosServer()) {
                    final Village v = Villages.getVillageForCreature(lVehicle.getLastOwnerId());
                    if (v == null || v.isEnemy(this.getCitizenVillage())) {
                        String vehname = this.getName();
                        if (this.getCitizenVillage() != null) {
                            vehname = this.getCitizenVillage().getName();
                        }
                        Server.getInstance().broadCastAction(LoginHandler.raiseFirstLetter(lVehicle.getName()) + " is now the property of " + vehname + "!", this, 10);
                        final String message2 = StringUtil.format("You declare the %s the property of %s.", lVehicle.getName(), vehname);
                        this.getCommunicator().sendNormalServerMessage(message2);
                        lVehicle.setLastOwnerId(this.getWurmId());
                    }
                }
                lVehicle.setAuxData(this.getKingdomId());
            }
            this.getCommunicator().sendTeleport(true, false, vehic.commandType);
        }
        else {
            this.getCommunicator().sendTeleport(true, false, (byte)0);
        }
        this.getMovementScheme().addIntraTeleport(this.getTeleportCounter());
    }
    
    public void disembark(final boolean teleport) {
        this.disembark(teleport, -1, -1);
    }
    
    public void disembark(final boolean teleport, final int tilex, final int tiley) {
        if (this.vehicle > -10L) {
            final Vehicle vehic = Vehicles.getVehicleForId(this.vehicle);
            if (vehic != null) {
                if (vehic.pilotId == this.getWurmId()) {
                    this.setVehicleCommander(false);
                    vehic.pilotId = -10L;
                    this.getCommunicator().setVehicleController(-1L, -1L, 0.0f, 0.0f, 0.0f, -2000.0f, 2000.0f, 2000.0f, 0.0f, 0);
                    try {
                        final Item item = Items.getItem(this.vehicle);
                        item.savePosition();
                    }
                    catch (Exception ex) {}
                }
                else if (vehic.pilotId != -10L) {
                    try {
                        final Item item = Items.getItem(this.vehicle);
                        item.savePosition();
                        final Creature pilot = Server.getInstance().getCreature(vehic.pilotId);
                        if (!vehic.creature && item.isBoat()) {
                            pilot.getMovementScheme().addMountSpeed(vehic.calculateNewBoatSpeed(true));
                        }
                        else if (vehic.creature) {
                            vehic.updateDraggedSpeed(true);
                        }
                    }
                    catch (WurmServerException ex2) {}
                    catch (Exception ex3) {}
                }
                final String vehicName = Vehicle.getVehicleName(vehic);
                if (vehic.isChair()) {
                    this.getCommunicator().sendNormalServerMessage(StringUtil.format("You get up from the %s.", vehicName));
                    Server.getInstance().broadCastAction(StringUtil.format("%s gets up from the %s.", this.getName(), vehicName), this, 5);
                }
                else {
                    this.getCommunicator().sendNormalServerMessage(StringUtil.format("You leave the %s.", vehicName));
                    Server.getInstance().broadCastAction(StringUtil.format("%s leaves the %s.", this.getName(), vehicName), this, 5);
                }
                this.setVehicle(-10L, teleport, (byte)(-1), tilex, tiley);
                int found = 0;
                for (int x = 0; x < vehic.seats.length; ++x) {
                    if (vehic.seats[x].occupant == this.getWurmId()) {
                        vehic.seats[x].occupant = -10L;
                        ++found;
                    }
                }
                if (found > 1) {
                    Creature.logger.log(Level.INFO, StringUtil.format("%s was occupying %d seats on %s.", this.getName(), found, vehicName));
                }
            }
            else {
                this.setVehicle(-10L, teleport, (byte)(-1), tilex, tiley);
            }
        }
    }
    
    public int getTeleportCounter() {
        return 0;
    }
    
    public long getVehicle() {
        return this.vehicle;
    }
    
    public byte getSeatType() {
        return this.seatType;
    }
    
    public Vehicle getMountVehicle() {
        return Vehicles.getVehicleForId(this.getWurmId());
    }
    
    public boolean isVehicleCommander() {
        return this.isVehicleCommander;
    }
    
    public double getVillageSkillModifier() {
        return 0.0;
    }
    
    public void setVillageSkillModifier(final double newModifier) {
    }
    
    public String getEmotePrefix() {
        return this.template.getName();
    }
    
    public void playAnimation(final String animationName, final boolean looping) {
        if (this.currentTile != null) {
            this.currentTile.sendAnimation(this, animationName, looping, -10L);
        }
    }
    
    public void playAnimation(final String animationName, final boolean looping, final long aTarget) {
        if (this.currentTile != null) {
            this.currentTile.sendAnimation(this, animationName, looping, aTarget);
        }
    }
    
    public void sendStance(final byte stance) {
        if (this.currentTile != null) {
            this.currentTile.sendStance(this, stance);
        }
    }
    
    public void sendDamage(final float damPercent) {
        if (this.currentTile != null) {
            this.currentTile.sendCreatureDamage(this, damPercent);
        }
    }
    
    public void sendFishingLine(final float posX, final float posY, final byte floatType) {
        if (this.currentTile != null) {
            this.currentTile.sendFishingLine(this, posX, posY, floatType);
        }
    }
    
    public void sendFishHooked(final byte fishType, final long fishId) {
        if (this.currentTile != null) {
            this.currentTile.sendFishHooked(this, fishType, fishId);
        }
    }
    
    public void sendFishingStopped() {
        if (this.currentTile != null) {
            this.currentTile.sendFishingStopped(this);
        }
    }
    
    public void sendSpearStrike(final float posX, final float posY) {
        if (this.currentTile != null) {
            this.currentTile.sendSpearStrike(this, posX, posY);
        }
    }
    
    public void checkTheftWarnQuestion() {
    }
    
    public void setTheftWarned(final boolean warned) {
    }
    
    public void checkChallengeWarnQuestion() {
    }
    
    public void setChallengeWarned(final boolean warned) {
    }
    
    public void addEnemyPresense() {
    }
    
    public void removeEnemyPresense() {
    }
    
    public int getEnemyPresense() {
        return 0;
    }
    
    public boolean mayMute() {
        return false;
    }
    
    public boolean hasNoReimbursement() {
        return true;
    }
    
    public boolean isDeathProtected() {
        return false;
    }
    
    public void setDeathProtected(final boolean _deathProtected) {
    }
    
    public long mayChangeVillageInMillis() {
        return 0L;
    }
    
    public boolean hasGlow() {
        return this.getPower() > 0 || this.template.isGlowing();
    }
    
    public void loadAffinities() {
    }
    
    public void increaseAffinity(final int skillnumber, final int value) {
    }
    
    public void decreaseAffinity(final int skillnumber, final int value) {
    }
    
    public boolean mayOpportunityAttack() {
        return !this.isStunned() && this.opportunityAttackCounter <= 0 && this.getCombatHandler().getOpportunityAttacks() < this.getFightingSkill().getKnowledge(0.0) / 10.0;
    }
    
    public boolean opportunityAttack(final Creature creature) {
        if (creature.isInvulnerable()) {
            return false;
        }
        if (!creature.isVisibleTo(this)) {
            return false;
        }
        if (this.isPlayer() && creature.isPlayer() && !Servers.isThisAPvpServer() && !this.isDuelOrSpar(creature)) {
            return false;
        }
        if ((this.isFighting() || creature.getWurmId() == this.target) && (!this.isPlayer() || !creature.isPlayer())) {
            if (this.isBridgeBlockingAttack(creature, false)) {
                return false;
            }
            if (this.mayOpportunityAttack() && this.getLayer() == creature.getLayer() && this.getMindSpeed().skillCheck(this.getCombatHandler().getOpportunityAttacks() * 10, 0.0, false, 1.0f) > 0.0) {
                if (this.opponent == null) {
                    this.setOpponent(creature);
                }
                return this.getCombatHandler().attack(creature, 10, true, 2.0f, null);
            }
        }
        return false;
    }
    
    public boolean isSparring(final Creature _opponent) {
        return false;
    }
    
    public boolean isDuelling(final Creature _opponent) {
        return false;
    }
    
    public boolean isDuelOrSpar(final Creature _opponent) {
        return false;
    }
    
    public void setChangedTileCounter() {
    }
    
    public boolean isStealth() {
        return this.status.stealth;
    }
    
    public void setStealth(final boolean stealth) {
        if (this.status.setStealth(stealth)) {
            if (stealth) {
                this.stealthBreakers = new HashSet<Long>();
                if (this.isPlayer()) {
                    this.getCommunicator().sendNormalServerMessage("You attempt to hide from others.", (byte)4);
                }
                this.movementScheme.setStealthMod(true);
            }
            else {
                if (this.stealthBreakers != null) {
                    this.stealthBreakers.clear();
                }
                this.getCommunicator().sendNormalServerMessage("You no longer hide.", (byte)4);
                this.movementScheme.setStealthMod(false);
            }
            this.checkInvisDetection();
        }
    }
    
    public void checkInvisDetection() {
        if (this.getBody().getBodyItem() != null) {
            this.getCurrentTile().checkVisibility(this, !this.isVisible() || this.isStealth());
        }
    }
    
    public boolean visibilityCheck(final Creature watcher, final float difficultyModifier) {
        if (!this.isVisible()) {
            return this.getPower() > 0 && this.getPower() <= watcher.getPower();
        }
        if (!this.isStealth()) {
            return true;
        }
        if (this.getPower() > 0 && this.getPower() <= watcher.getPower()) {
            return true;
        }
        if (this.getPower() < watcher.getPower()) {
            return true;
        }
        if (watcher.isUnique()) {
            return true;
        }
        if (this.stealthBreakers != null && this.stealthBreakers.contains(watcher.getWurmId())) {
            return true;
        }
        final int distModifier = (int)Math.max(Math.abs(watcher.getPosX() - this.getPosX()), Math.abs(watcher.getPosY() - this.getPosY()));
        if (watcher.getCurrentTile() == this.getCurrentTile() || watcher.isDetectInvis() || Server.rand.nextInt((int)(100.0f + difficultyModifier + distModifier)) < watcher.getDetectDangerBonus() / 5.0f || watcher.getMindLogical().skillCheck(this.getBodyControl() + difficultyModifier + distModifier, 0.0, true, 1.0f) > 0.0) {
            if (this.stealthBreakers == null) {
                this.stealthBreakers = new HashSet<Long>();
            }
            this.stealthBreakers.add(watcher.getWurmId());
            return true;
        }
        return false;
    }
    
    public boolean isDetectInvis() {
        return this.template.isDetectInvis() || this.status.detectInvisCounter > 0;
    }
    
    public boolean isVisibleTo(final Creature watcher) {
        return this.isVisibleTo(watcher, false);
    }
    
    public boolean isVisibleTo(final Creature watcher, final boolean ignoreStealth) {
        if (!this.isVisible()) {
            return this.getPower() > 0 && this.getPower() <= watcher.getPower();
        }
        return !this.isStealth() || ignoreStealth || (this.getPower() > 0 && this.getPower() <= watcher.getPower()) || this.getPower() < watcher.getPower() || (watcher.isUnique() || watcher.isDetectInvis()) || (this.stealthBreakers != null && this.stealthBreakers.contains(watcher.getWurmId()));
    }
    
    public void addVisionModifier(final DoubleValueModifier modifier) {
        if (this.visionModifiers == null) {
            this.visionModifiers = new HashSet<DoubleValueModifier>();
        }
        this.visionModifiers.add(modifier);
    }
    
    public void removeVisionModifier(final DoubleValueModifier modifier) {
        if (this.visionModifiers != null) {
            this.visionModifiers.remove(modifier);
        }
    }
    
    public double getVisionMod() {
        if (this.visionModifiers == null) {
            return 0.0;
        }
        double doubleModifier = 0.0;
        for (final DoubleValueModifier lDoubleValueModifier : this.visionModifiers) {
            doubleModifier += lDoubleValueModifier.getModifier();
        }
        return doubleModifier;
    }
    
    public int[] getCombatMoves() {
        return this.template.getCombatMoves();
    }
    
    public boolean isGuide() {
        return this.template.isTutorial();
    }
    
    public int getTutorialLevel() {
        return 9999;
    }
    
    public void setTutorialLevel(final int newLevel) {
    }
    
    public boolean skippedTutorial() {
        return false;
    }
    
    public String getCurrentMissionInstruction() {
        return "";
    }
    
    public void missionFinished(final boolean reward, final boolean sendpopup) {
    }
    
    public boolean isNoSkillgain() {
        return this.template.isNoSkillgain() || this.isBred() || !this.isPaying();
    }
    
    public boolean isAutofight() {
        return false;
    }
    
    public final float getDamageModifier(final boolean pvp, final boolean spell) {
        double strength;
        if (!spell) {
            strength = this.getStrengthSkill();
        }
        else {
            strength = this.getSoulStrengthVal();
        }
        float damMod = (float)(120.0 - strength) / 100.0f;
        if (this.isPlayer() && pvp && Servers.localServer.PVPSERVER) {
            damMod = (float)(1.0 - 0.15 * Math.log(Math.max(20.0, strength) * 0.800000011920929 - 15.0));
            damMod = Math.max(Math.min(damMod, 1.0f), 0.2f);
        }
        if (this.hasSpellEffect((byte)96)) {
            damMod *= 1.1f;
        }
        if (this.getCultist() != null) {
            final float percent = this.getCultist().getHalfDamagePercentage();
            if (percent > 0.0f) {
                if (this.isChampion()) {
                    final float red = 1.0f - 0.1f * percent;
                    damMod *= red;
                }
                else {
                    final float red = 1.0f - 0.3f * percent;
                    damMod *= red;
                }
            }
        }
        return damMod;
    }
    
    @Override
    public String toString() {
        return "Creature [id: " + this.id + ", name: " + this.name + ", Tile: " + this.currentTile + ", Template: " + this.template + ", Status: " + this.status + ']';
    }
    
    public void sendToLoggers(final String tolog) {
        this.sendToLoggers(tolog, (byte)2);
    }
    
    public void sendToLoggers(final String tolog, final byte restrictedToPower) {
        if (this.loggerCreature1 != -10L) {
            try {
                final Creature receiver = Server.getInstance().getCreature(this.loggerCreature1);
                receiver.getCommunicator().sendLogMessage(this.getName() + " [" + tolog + "]");
            }
            catch (Exception ex) {
                this.loggerCreature1 = -10L;
            }
        }
        if (this.loggerCreature2 != -10L) {
            try {
                final Creature receiver = Server.getInstance().getCreature(this.loggerCreature2);
                receiver.getCommunicator().sendLogMessage(this.getName() + " [" + tolog + "]");
            }
            catch (Exception ex) {
                this.loggerCreature2 = -10L;
            }
        }
    }
    
    public long getAppointments() {
        return 0L;
    }
    
    public void addAppointment(final int aid) {
    }
    
    public void removeAppointment(final int aid) {
    }
    
    public boolean isFloating() {
        return this.template.isFloating();
    }
    
    public boolean hasAppointment(final int aid) {
        return false;
    }
    
    public boolean isKing() {
        return false;
    }
    
    public final boolean isEligibleForKingdomBonus() {
        if (this.hasCustomKingdom()) {
            final King king = King.getKing(this.getKingdomId());
            return king != null && king.currentLand > 2.0f;
        }
        return true;
    }
    
    public String getAppointmentTitles() {
        return "";
    }
    
    public boolean isRoyalAnnouncer() {
        return King.isOfficial(1510, this.getWurmId(), this.getKingdomId());
    }
    
    public boolean isRoyalChef() {
        return King.isOfficial(1509, this.getWurmId(), this.getKingdomId());
    }
    
    public boolean isRoyalPriest() {
        return King.isOfficial(1506, this.getWurmId(), this.getKingdomId());
    }
    
    public boolean isRoyalSmith() {
        return King.isOfficial(1503, this.getWurmId(), this.getKingdomId());
    }
    
    public boolean isRoyalExecutioner() {
        return King.isOfficial(1508, this.getWurmId(), this.getKingdomId());
    }
    
    public boolean isEconomicAdvisor() {
        return King.isOfficial(1505, this.getWurmId(), this.getKingdomId());
    }
    
    public boolean isInformationOfficer() {
        return King.isOfficial(1500, this.getWurmId(), this.getKingdomId());
    }
    
    public String getAnnounceString() {
        return this.getName() + '!';
    }
    
    public boolean isAppointed() {
        return false;
    }
    
    public boolean isArcheryMode() {
        return false;
    }
    
    public MusicPlayer getMusicPlayer() {
        return this.musicPlayer;
    }
    
    public int getPushCounter() {
        return 200;
    }
    
    public void setPushCounter(final int val) {
    }
    
    public Seat getSeat() {
        return null;
    }
    
    public void setMountAction(@Nullable final MountAction act) {
        this.mountAction = act;
    }
    
    public void activePotion(final Item potion) {
    }
    
    public byte getCRCounterBonus() {
        return 0;
    }
    
    public boolean isNoAttackVehicles() {
        return !this.template.attacksVehicles;
    }
    
    public int getMaxNumActions() {
        return 10;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + (int)(this.id ^ this.id >>> 32);
        result = 31 * result + (this.isPlayer() ? 1231 : 1237);
        return result;
    }
    
    public void setCheated(final String reason) {
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Creature)) {
            return false;
        }
        final Creature other = (Creature)obj;
        return this.id == other.id && this.isPlayer() == other.isPlayer();
    }
    
    public boolean seesPlayerAssistantWindow() {
        return false;
    }
    
    public void setHitched(@Nullable final Vehicle _hitched, final boolean loading) {
        this.hitchedTo = _hitched;
        if (this.hitchedTo != null) {
            this.clearOrders();
            this.seatType = 2;
            if (!loading) {
                this.getStatus().setVehicle(this.hitchedTo.wurmid, this.seatType);
            }
        }
        else {
            this.seatType = -1;
            this.getStatus().setVehicle(-10L, this.seatType);
        }
    }
    
    public Vehicle getHitched() {
        return this.hitchedTo;
    }
    
    public boolean isPlayerAssistant() {
        return false;
    }
    
    public boolean isVehicle() {
        return this.template.isVehicle;
    }
    
    public Set<Long> getRiders() {
        return this.riders;
    }
    
    public boolean isRidden() {
        return this.riders != null && this.riders.size() > 0;
    }
    
    public boolean isRiddenBy(final long wurmid) {
        return this.riders != null && this.riders.contains(wurmid);
    }
    
    public void addRider(final long newrider) {
        if (this.riders == null) {
            this.riders = new HashSet<Long>();
        }
        this.riders.add(newrider);
    }
    
    public void removeRider(final long lostrider) {
        if (this.riders == null) {
            this.riders = new HashSet<Long>();
        }
        this.riders.remove(lostrider);
    }
    
    protected void forceMountSpeedChange() {
        this.mountPollCounter = 0;
        this.pollMount();
    }
    
    private void pollMount() {
        if (this.isRidden()) {
            if (this.mountPollCounter <= 0 || Server.rand.nextInt(100) == 0) {
                final Vehicle vehic = Vehicles.getVehicleForId(this.getWurmId());
                if (vehic != null) {
                    try {
                        final Creature rider = Server.getInstance().getCreature(vehic.getPilotSeat().occupant);
                        byte val = vehic.calculateNewMountSpeed(this, false);
                        if (this.switchv) {
                            --val;
                        }
                        this.switchv = !this.switchv;
                        rider.getMovementScheme().addMountSpeed(val);
                    }
                    catch (NoSuchCreatureException ex) {}
                    catch (NoSuchPlayerException ex2) {}
                }
                this.mountPollCounter = 20;
            }
            else {
                --this.mountPollCounter;
            }
        }
    }
    
    public boolean mayChangeSpeed() {
        return this.mountPollCounter <= 0;
    }
    
    public float getMountSpeedPercent(final boolean mounting) {
        float factor = 0.5f;
        if (this.getStatus().getHunger() < 45000) {
            factor += 0.2f;
        }
        if (this.getStatus().getHunger() < 10000) {
            factor += 0.1f;
        }
        if (this.getStatus().damage < 10000) {
            factor += 0.1f;
        }
        else if (this.getStatus().damage > 20000) {
            factor -= 0.5f;
        }
        else if (this.getStatus().damage > 45000) {
            factor -= 0.7f;
        }
        if (this.isHorse() || this.isUnicorn()) {
            final float hbonus = this.calcHorseShoeBonus(mounting);
            this.sendToLoggers("Horse shoe bonus " + hbonus + " so factor from " + factor + " to " + (factor + hbonus));
            factor += hbonus;
        }
        final float tperc = this.getTraitMovePercent(mounting);
        this.sendToLoggers("Trait move percent= " + tperc + " so factor from " + factor + " to " + (factor + tperc));
        factor += tperc;
        if (this.getBonusForSpellEffect((byte)22) > 0.0f) {
            factor -= 0.2f * (this.getBonusForSpellEffect((byte)22) / 100.0f);
        }
        if (this.isRidden()) {
            final Item torsoItem = this.getWornItem((byte)2);
            if (torsoItem != null && (torsoItem.isSaddleLarge() || torsoItem.isSaddleNormal())) {
                factor += Math.max(10.0f, torsoItem.getCurrentQualityLevel()) / 1000.0f;
                factor += torsoItem.getRarity() * 0.03f;
                factor += torsoItem.getSpellSpeedBonus() / 2000.0f;
                if (!mounting && !this.ignoreSaddleDamage) {
                    torsoItem.setDamage(torsoItem.getDamage() + 0.001f);
                }
                this.ignoreSaddleDamage = false;
            }
            this.sendToLoggers("After saddle move percent= " + factor);
            factor *= this.getMovementScheme().getSpeedModifier();
            this.sendToLoggers("After speedModifier " + this.getMovementScheme().getSpeedModifier() + " move percent= " + factor);
        }
        return factor;
    }
    
    private int getCarriedMountWeight() {
        int currWeight = this.getCarriedWeight();
        final int bagsWeight = this.getSaddleBagsCarriedWeight();
        currWeight -= bagsWeight;
        if (this.isRidden()) {
            for (final Long lLong : this.riders) {
                try {
                    final Creature _rider = Server.getInstance().getCreature(lLong);
                    currWeight += Math.max(30000, _rider.getStatus().fat * 1000);
                    currWeight += _rider.getCarriedWeight();
                }
                catch (NoSuchCreatureException ex) {}
                catch (NoSuchPlayerException ex2) {}
            }
        }
        return currWeight;
    }
    
    public boolean hasTraits() {
        return this.status.traits != 0L;
    }
    
    public boolean hasTrait(final int traitbit) {
        return this.status.traits != 0L && this.status.isTraitBitSet(traitbit);
    }
    
    public boolean hasAbility(final int abilityBit) {
        return false;
    }
    
    public boolean hasFlag(final int flagBit) {
        return false;
    }
    
    public void setFlag(final int number, final boolean value) {
    }
    
    public void setAbility(final int number, final boolean value) {
    }
    
    public void setTagItem(final long itemId, final String itemName) {
    }
    
    public String getTaggedItemName() {
        return "";
    }
    
    public long getTaggedItemId() {
        return -10L;
    }
    
    public boolean removeRandomNegativeTrait() {
        return this.status.traits != 0L && this.status.removeRandomNegativeTrait();
    }
    
    private float getTraitMovePercent(final boolean mounting) {
        float traitMod = 0.0f;
        Creature r = null;
        boolean moving = false;
        if (this.isRidden() && this.getMountVehicle() != null) {
            try {
                r = Server.getInstance().getCreature(this.getMountVehicle().pilotId);
                moving = r.isMoving();
            }
            catch (NoSuchCreatureException ex) {}
            catch (NoSuchPlayerException ex2) {}
        }
        final int cweight = this.getCarriedMountWeight();
        if (!mounting && this.status.traits != 0L) {
            final Skill sstrength = this.getSoulStrength();
            if (this.status.isTraitBitSet(1) && (!this.isHorse() || sstrength.skillCheck(20.0, 0.0, !moving, 1.0f) > 0.0)) {
                traitMod += 0.1f;
            }
            if (!this.status.isTraitBitSet(15) && !this.status.isTraitBitSet(16) && !this.status.isTraitBitSet(17) && !this.status.isTraitBitSet(18) && !this.status.isTraitBitSet(24) && !this.status.isTraitBitSet(25) && this.status.isTraitBitSet(23) && (!this.isHorse() || sstrength.skillCheck(20.0, 0.0, !moving, 1.0f) > 0.0)) {
                traitMod += 0.025f;
            }
            if (this.status.isTraitBitSet(4) && (!this.isHorse() || sstrength.skillCheck(20.0, 0.0, !moving, 1.0f) > 0.0)) {
                traitMod += 0.2f;
            }
            if (this.status.isTraitBitSet(8) && (!this.isHorse() || sstrength.skillCheck(20.0, 0.0, !moving, 1.0f) < 0.0)) {
                traitMod -= 0.1f;
            }
            if (this.status.isTraitBitSet(9) && (!this.isHorse() || sstrength.skillCheck(20.0, 0.0, !moving, 1.0f) < 0.0)) {
                traitMod -= 0.3f;
            }
            if (this.status.isTraitBitSet(6) && (!this.isHorse() || sstrength.skillCheck(20.0, 0.0, !moving, 1.0f) > 0.0)) {
                traitMod += 0.1f;
            }
            float wmod = 0.0f;
            if (this.status.isTraitBitSet(3) && (!this.isHorse() || sstrength.skillCheck(20.0, 0.0, !moving, 1.0f) > 0.0)) {
                wmod += 10000.0f;
            }
            if (this.status.isTraitBitSet(5) && (!this.isHorse() || sstrength.skillCheck(20.0, 0.0, !moving, 1.0f) > 0.0)) {
                wmod += 20000.0f;
            }
            if (this.status.isTraitBitSet(11) && (!this.isHorse() || sstrength.skillCheck(20.0, 0.0, !moving, 1.0f) < 0.0)) {
                wmod -= 30000.0f;
            }
            if (this.status.isTraitBitSet(6) && (!this.isHorse() || sstrength.skillCheck(20.0, 0.0, !moving, 1.0f) > 0.0)) {
                wmod += 10000.0f;
            }
            if (cweight > this.getStrengthSkill() * 5000.0 + wmod) {
                traitMod -= (float)(0.15 * (cweight - this.getStrengthSkill() * 5000.0 - wmod) / 50000.0);
            }
        }
        else if (cweight > this.getStrengthSkill() * 5000.0) {
            traitMod -= (float)(0.15 * (cweight - this.getStrengthSkill() * 5000.0) / 50000.0);
        }
        return traitMod;
    }
    
    public boolean isHorse() {
        return this.template.isHorse;
    }
    
    public boolean isUnicorn() {
        return this.template.isUnicorn();
    }
    
    public boolean cantRideUntame() {
        return this.template.cantRideUntamed();
    }
    
    public static void setRandomColor(final Creature creature) {
        if (Server.rand.nextInt(3) == 0) {
            creature.getStatus().setTraitBit(15, true);
        }
        else if (Server.rand.nextInt(3) == 0) {
            creature.getStatus().setTraitBit(16, true);
        }
        else if (Server.rand.nextInt(3) == 0) {
            creature.getStatus().setTraitBit(17, true);
        }
        else if (Server.rand.nextInt(3) == 0) {
            creature.getStatus().setTraitBit(18, true);
        }
        else if (Server.rand.nextInt(6) == 0) {
            creature.getStatus().setTraitBit(24, true);
        }
        else if (Server.rand.nextInt(12) == 0) {
            creature.getStatus().setTraitBit(25, true);
        }
        else if (Server.rand.nextInt(24) == 0) {
            creature.getStatus().setTraitBit(23, true);
        }
        else if (creature.getTemplate().maxColourCount > 8) {
            if (Server.rand.nextInt(6) == 0) {
                creature.getStatus().setTraitBit(30, true);
            }
            else if (Server.rand.nextInt(6) == 0) {
                creature.getStatus().setTraitBit(31, true);
            }
            else if (Server.rand.nextInt(6) == 0) {
                creature.getStatus().setTraitBit(32, true);
            }
            else if (Server.rand.nextInt(6) == 0) {
                creature.getStatus().setTraitBit(33, true);
            }
            else if (Server.rand.nextInt(6) == 0) {
                creature.getStatus().setTraitBit(34, true);
            }
        }
    }
    
    public boolean mayMate(final Creature potentialMate) {
        if (this.isDead() || potentialMate.isDead()) {
            return false;
        }
        if (potentialMate.getTemplate().getMateTemplateId() == this.template.getTemplateId() || (this.template.getTemplateId() == 96 && potentialMate.getTemplate().getTemplateId() == 96)) {
            if (this.template.getAdultFemaleTemplateId() != -1 || this.template.getAdultMaleTemplateId() != -1) {
                return false;
            }
            if (potentialMate.getSex() != this.getSex() && potentialMate.getWurmId() != this.getWurmId()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean checkBreedingPossibility() {
        final Creature[] crets = this.getCurrentTile().getCreatures();
        if (!this.isKingdomGuard() && !this.isGhost() && !this.isHuman() && crets.length > 0 && this.mayMate(crets[0]) && !crets[0].isPregnant() && !this.isPregnant()) {
            try {
                BehaviourDispatcher.action(this, this.getCommunicator(), -1L, crets[0].getWurmId(), (short)379);
                return true;
            }
            catch (Exception ex) {
                return false;
            }
        }
        return false;
    }
    
    public boolean isInTheMoodToBreed(final boolean forced) {
        return this.getStatus().getHunger() <= 10000 && this.template.getAdultFemaleTemplateId() == -1 && this.template.getAdultMaleTemplateId() == -1 && this.getStatus().age > 3 && (this.breedCounter == 0 || (forced && !this.forcedBreed));
    }
    
    public int getBreedCounter() {
        return this.breedCounter;
    }
    
    public void resetBreedCounter() {
        this.forcedBreed = true;
        this.breedCounter = (Servers.isThisAPvpServer() ? 900 : 2000) + Server.rand.nextInt(Math.max(1000, 100 * Math.abs(20 - this.getStatus().age)));
    }
    
    public long getMother() {
        return this.status.mother;
    }
    
    public long getFather() {
        return this.status.father;
    }
    
    public int getMeditateX() {
        return 0;
    }
    
    public int getMeditateY() {
        return 0;
    }
    
    public void setMeditateX(final int tilex) {
    }
    
    public void setMeditateY(final int tiley) {
    }
    
    public void setDisease(final byte newDisease) {
        boolean changed = false;
        if (this.getStatus().disease > 0 && newDisease <= 0) {
            if (this.getPower() < 2) {
                this.setVisible(false);
            }
            changed = true;
            this.getCommunicator().sendSafeServerMessage("You feel a lot better now as your disease is gone.", (byte)2);
            if (this.isPlayer()) {
                this.getCommunicator().sendRemoveSpellEffect(SpellEffectsEnum.DISEASE);
            }
        }
        else if (this.isPlayer() && newDisease > 0) {
            this.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.DISEASE, 100000, this.getStatus().disease);
            this.achievement(173);
        }
        if (this.getStatus().disease == 0 && newDisease == 1) {
            if (this.isUnique() || this.isKingdomGuard() || this.isGhost() || this.status.modtype == 11) {
                return;
            }
            if (this.getPower() < 2) {
                this.setVisible(false);
            }
            changed = true;
            this.getCommunicator().sendAlertServerMessage("You scratch yourself. What did you catch now?", (byte)2);
            this.achievement(568);
        }
        this.getStatus().setDisease(newDisease);
        if (changed && this.getPower() < 2) {
            this.setVisible(true);
        }
    }
    
    public byte getDisease() {
        return this.getStatus().disease;
    }
    
    public long getLastGroomed() {
        return this.getStatus().lastGroomed;
    }
    
    public void setLastGroomed(final long newLastGroomed) {
        this.getStatus().setLastGroomed(newLastGroomed);
    }
    
    public boolean canBeGroomed() {
        return System.currentTimeMillis() - this.getLastGroomed() > 3600000L;
    }
    
    public boolean isDomestic() {
        return this.template.domestic;
    }
    
    public void setLastKingdom() {
    }
    
    public void addLink(final Creature creature) {
    }
    
    public boolean isLinked() {
        return this.linkedTo != -10L;
    }
    
    public Creature getCreatureLinkedTo() {
        try {
            return Server.getInstance().getCreature(this.linkedTo);
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public void removeLink(final long wurmid) {
    }
    
    public int getNumLinks() {
        return 0;
    }
    
    public Creature[] getLinks() {
        return Creature.emptyCreatures;
    }
    
    public void clearLinks() {
    }
    
    public void setLinkedTo(final long wid, final boolean linkback) {
        this.linkedTo = wid;
    }
    
    public void disableLink() {
        this.setLinkedTo(-10L, true);
    }
    
    public void setFatigue(final int fatigueToAdd) {
    }
    
    public boolean isMissionairy() {
        return true;
    }
    
    public long getLastChangedPriestType() {
        return 0L;
    }
    
    public void setPriestType(final byte type) {
    }
    
    public void setPrayerSeconds(final int prayerSeconds) {
    }
    
    public long getLastChangedJoat() {
        return 0L;
    }
    
    public void resetJoat() {
    }
    
    public Team getTeam() {
        return null;
    }
    
    public void setTeam(@Nullable final Team newTeam, final boolean sendRemove) {
    }
    
    public boolean isTeamLeader() {
        return false;
    }
    
    public boolean mayInviteTeam() {
        return false;
    }
    
    public void setMayInviteTeam(final boolean mayInvite) {
    }
    
    public void sendSystemMessage(final String message) {
    }
    
    public void sendHelpMessage(final String message) {
    }
    
    public void makeEmoteSound() {
    }
    
    public void poisonChanged(final boolean hadPoison, final Wound w) {
        if (hadPoison) {
            if (!this.isPoisoned()) {
                this.getCommunicator().sendRemoveSpellEffect(w.getWurmId(), SpellEffectsEnum.POISON);
                this.hasSentPoison = false;
            }
        }
        else if (!this.hasSentPoison) {
            this.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.POISON.createId(w.getWurmId()), SpellEffectsEnum.POISON, 100000, w.getPoisonSeverity());
            this.hasSentPoison = true;
        }
    }
    
    public final void sendAllPoisonEffect() {
        final Wounds w = this.getBody().getWounds();
        if (w != null && w.getWounds() != null) {
            final Wound[] warr = w.getWounds();
            for (int a = 0; a < warr.length; ++a) {
                if (warr[a].isPoison()) {
                    this.getCommunicator().sendAddSpellEffect(SpellEffectsEnum.POISON.createId(warr[a].getWurmId()), SpellEffectsEnum.POISON, 100000, warr[a].getPoisonSeverity());
                    this.hasSentPoison = true;
                }
            }
        }
    }
    
    public final boolean isPoisoned() {
        final Wounds w = this.getBody().getWounds();
        if (w != null && w.getWounds() != null) {
            final Wound[] warr = w.getWounds();
            for (int a = 0; a < warr.length; ++a) {
                if (warr[a].isPoison()) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean mayEmote() {
        return false;
    }
    
    public boolean hasSkillGain() {
        return true;
    }
    
    public boolean setHasSkillGain(final boolean hasSkillGain) {
        return true;
    }
    
    public long getChampTimeStamp() {
        return 0L;
    }
    
    public void becomeChamp() {
    }
    
    public void revertChamp() {
    }
    
    public long getLastChangedCluster() {
        return 0L;
    }
    
    public void setLastChangedCluster() {
    }
    
    public boolean isInTheNorthWest() {
        return this.getTileX() < Zones.worldTileSizeX / 3 && this.getTileY() < Zones.worldTileSizeY / 3;
    }
    
    public boolean isInTheNorth() {
        return this.getTileY() < Zones.worldTileSizeX / 3;
    }
    
    public boolean isInTheNorthEast() {
        return this.getTileX() > Zones.worldTileSizeX - Zones.worldTileSizeX / 3 && this.getTileY() < Zones.worldTileSizeY / 3;
    }
    
    public boolean isInTheEast() {
        return this.getTileX() > Zones.worldTileSizeX - Zones.worldTileSizeX / 3;
    }
    
    public boolean isInTheSouthEast() {
        return this.getTileX() > Zones.worldTileSizeX - Zones.worldTileSizeX / 3 && this.getTileY() > Zones.worldTileSizeY - Zones.worldTileSizeY / 3;
    }
    
    public boolean isInTheSouth() {
        return this.getTileY() > Zones.worldTileSizeY - Zones.worldTileSizeY / 3;
    }
    
    public boolean isInTheSouthWest() {
        return this.getTileX() < Zones.worldTileSizeX / 3 && this.getTileY() > Zones.worldTileSizeY - Zones.worldTileSizeY / 3;
    }
    
    public boolean isInTheWest() {
        return this.getTileX() < Zones.worldTileSizeX / 3;
    }
    
    public int getGlobalMapPlacement() {
        if (this.isInTheNorthWest()) {
            return 7;
        }
        if (this.isInTheNorthEast()) {
            return 1;
        }
        if (this.isInTheSouthEast()) {
            return 3;
        }
        if (this.isInTheSouthWest()) {
            return 5;
        }
        if (this.isInTheNorth()) {
            return 0;
        }
        if (this.isInTheEast()) {
            return 2;
        }
        if (this.isInTheSouth()) {
            return 4;
        }
        if (this.isInTheWest()) {
            return 6;
        }
        return -1;
    }
    
    public boolean mayDestroy(final Item item) {
        if (item.isDestroyable(this.getWurmId())) {
            return true;
        }
        if (item.isOwnerDestroyable() && !item.isLocked()) {
            final Village village = Zones.getVillage(item.getTilePos(), item.isOnSurface());
            if (village != null) {
                return village.isActionAllowed((short)83, this);
            }
            if (!item.isUnfinished()) {
                return true;
            }
            if (item.getRealTemplate() != null && item.getRealTemplate().isKingdomMarker() && this.getKingdomId() != item.getAuxData()) {
                return true;
            }
        }
        if (item.isEnchantedTurret()) {
            final VolaTile t = Zones.getTileOrNull(item.getTileX(), item.getTileY(), item.isOnSurface());
            if (t != null && t.getVillage() != null && t.getVillage().isPermanent && t.getVillage().kingdom == item.getKingdom()) {
                return false;
            }
        }
        return false;
    }
    
    public boolean isCaredFor() {
        return !this.isUnique() && !this.onlyAttacksPlayers() && Creatures.getInstance().isCreatureProtected(this.getWurmId());
    }
    
    public long getCareTakerId() {
        return Creatures.getInstance().getCreatureProtectorFor(this.getWurmId());
    }
    
    public boolean isCaredFor(final Player player) {
        return this.getCareTakerId() == player.getWurmId();
    }
    
    public boolean isBrandedBy(final int villageId) {
        final Village bVill = this.getBrandVillage();
        return bVill != null && bVill.getId() == villageId;
    }
    
    public boolean isBranded() {
        final Village bVill = this.getBrandVillage();
        return bVill != null;
    }
    
    public boolean isOnDeed() {
        final Village bVill = this.getBrandVillage();
        if (bVill == null) {
            return false;
        }
        final Village pVill = Villages.getVillage(this.getTileX(), this.getTileY(), true);
        return pVill != null && bVill.getId() == pVill.getId();
    }
    
    public boolean isHitched() {
        return this.getHitched() != null;
    }
    
    public int getNumberOfPossibleCreatureTakenCareOf() {
        return 0;
    }
    
    public final void setHasSpiritStamina(final boolean hasStaminaGain) {
        this.hasSpiritStamina = hasStaminaGain;
    }
    
    public void setHasSpiritFavorgain(final boolean hasFavorGain) {
    }
    
    public void setHasSpiritFervor(final boolean hasSpiritFervor) {
    }
    
    public boolean mayUseLastGasp() {
        return false;
    }
    
    public void useLastGasp() {
    }
    
    public boolean isUsingLastGasp() {
        return false;
    }
    
    public final float addToWeaponUsed(final Item weapon, final float time) {
        Float ftime = this.weaponsUsed.get(weapon);
        if (ftime == null) {
            ftime = time;
        }
        else {
            ftime += time;
        }
        this.weaponsUsed.put(weapon, ftime);
        return ftime;
    }
    
    public final UsedAttackData addToAttackUsed(final AttackAction act, final float time, final int rounds) {
        UsedAttackData data = this.attackUsed.get(act);
        if (data == null) {
            data = new UsedAttackData(time, rounds);
        }
        else {
            data.setTime(data.getTime() + time);
            data.setRounds(data.getRounds() + rounds);
        }
        this.attackUsed.put(act, data);
        return data;
    }
    
    public final void updateAttacksUsed(final float time) {
        for (final AttackAction key : this.attackUsed.keySet()) {
            final UsedAttackData data = this.attackUsed.get(key);
            if (data != null) {
                data.update(data.getTime() - time);
            }
        }
    }
    
    public final UsedAttackData getUsedAttackData(final AttackAction act) {
        return this.attackUsed.get(act);
    }
    
    public final float deductFromWeaponUsed(final Item weapon, final float swingTime) {
        Float ftime = this.weaponsUsed.get(weapon);
        if (ftime == null) {
            ftime = swingTime;
        }
        while (ftime >= swingTime) {
            ftime -= swingTime;
        }
        this.weaponsUsed.put(weapon, ftime);
        return ftime;
    }
    
    public final void resetWeaponsUsed() {
        this.weaponsUsed.clear();
    }
    
    public final void resetAttackUsed() {
        this.attackUsed.clear();
        if (this.combatHandler != null) {
            this.combatHandler.resetSecAttacks();
        }
    }
    
    public byte getFightlevel() {
        if (this.fightlevel < 0) {
            this.fightlevel = 0;
        }
        if (this.fightlevel > 5) {
            this.fightlevel = 5;
        }
        return this.fightlevel;
    }
    
    public String getFightlevelString() {
        int fl = this.getFightlevel();
        if (fl < 0) {
            fl = 0;
        }
        if (fl >= 5) {
            fl = 5;
        }
        return Attack.focusStrings[fl];
    }
    
    public void increaseFightlevel(final int delta) {
        this.fightlevel += (byte)delta;
        if (this.fightlevel > 5) {
            this.fightlevel = 5;
        }
        if (this.fightlevel < 0) {
            this.fightlevel = 0;
        }
    }
    
    public void setKickedOffBoat(final boolean kicked) {
    }
    
    public boolean wasKickedOffBoat() {
        return false;
    }
    
    public boolean isOnPermaReputationGrounds() {
        return this.currentVillage != null && this.currentVillage.getReputationObject(this.getWurmId()) != null && this.currentVillage.getReputationObject(this.getWurmId()).isPermanent();
    }
    
    public boolean hasFingerEffect() {
        return false;
    }
    
    public void setHasFingerEffect(final boolean eff) {
    }
    
    public void sendHasFingerEffect() {
    }
    
    public boolean hasFingerOfFoBonus() {
        return false;
    }
    
    public void setHasCrownEffect(final boolean eff) {
    }
    
    public void sendHasCrownEffect() {
    }
    
    public void setCrownInfluence(final int influence) {
    }
    
    public boolean hasCrownInfluence() {
        return false;
    }
    
    public int getEpicServerId() {
        return -1;
    }
    
    public byte getEpicServerKingdom() {
        return 0;
    }
    
    public final boolean attackingIntoIllegalDuellingRing(final int targetX, final int targetY, final boolean surfaced) {
        if (surfaced) {
            final Item ring1 = Zones.isWithinDuelRing(this.getCurrentTile().getTileX(), this.getCurrentTile().getTileY(), this.getCurrentTile().isOnSurface());
            final Item ring2 = Zones.isWithinDuelRing(targetX, targetY, surfaced);
            if (ring2 != ring1) {
                return true;
            }
        }
        return false;
    }
    
    public final boolean hasSpellEffect(final byte spellEffect) {
        return this.getSpellEffects() != null && this.getSpellEffects().getSpellEffect(spellEffect) != null;
    }
    
    public final void reduceStoneSkin() {
        if (this.getSpellEffects() != null) {
            final SpellEffect sk = this.getSpellEffects().getSpellEffect((byte)68);
            if (sk != null) {
                if (sk.getPower() > 34.0f) {
                    sk.setPower(sk.getPower() - 34.0f);
                }
                else {
                    this.getSpellEffects().removeSpellEffect(sk);
                }
            }
        }
    }
    
    public final void removeTrueStrike() {
        if (this.getSpellEffects() != null) {
            final SpellEffect sk = this.getSpellEffects().getSpellEffect((byte)67);
            if (sk != null) {
                this.getSpellEffects().removeSpellEffect(sk);
            }
        }
    }
    
    public final boolean addWoundOfType(@Nullable final Creature attacker, final byte woundType, int pos, final boolean randomizePos, float armourMod, final boolean calculateArmour, double damage, final float infection, final float poison, final boolean noMinimumDamage, final boolean spell) {
        if ((woundType == 8 || woundType == 4 || woundType == 10) && this.getCultist() != null && this.getCultist().hasNoElementalDamage()) {
            return false;
        }
        if (this.hasSpellEffect((byte)69)) {
            damage *= 0.800000011920929;
        }
        try {
            if (randomizePos) {
                pos = this.getBody().getRandomWoundPos();
            }
            Label_0254: {
                if (calculateArmour) {
                    armourMod = this.getArmourMod();
                    if (armourMod != 1.0f && !this.isVehicle()) {
                        if (!this.isKingdomGuard()) {
                            break Label_0254;
                        }
                    }
                    try {
                        final byte protectionSlot = ArmourTemplate.getArmourPosition((byte)pos);
                        final Item armour = this.getArmour(protectionSlot);
                        if (!this.isKingdomGuard()) {
                            armourMod = ArmourTemplate.calculateDR(armour, woundType);
                        }
                        else {
                            armourMod *= ArmourTemplate.calculateDR(armour, woundType);
                        }
                        armour.setDamage((float)(armour.getDamage() + damage * armourMod / 30000.0 * armour.getDamageModifier() * ArmourTemplate.getArmourDamageModFor(armour, woundType)));
                        if (this.getBonusForSpellEffect((byte)22) > 0.0f) {
                            if (armourMod >= 1.0f) {
                                armourMod = 0.2f + (1.0f - this.getBonusForSpellEffect((byte)22) / 100.0f) * 0.6f;
                            }
                            else {
                                armourMod = Math.min(armourMod, 0.2f + (1.0f - this.getBonusForSpellEffect((byte)22) / 100.0f) * 0.6f);
                            }
                        }
                    }
                    catch (NoArmourException ex) {}
                }
            }
            if (pos == 1 || pos == 29) {
                damage *= ItemBonus.getFaceDamReductionBonus(this);
            }
            damage *= Wound.getResistModifier(attacker, this, woundType);
            if (woundType == 8) {
                return CombatEngine.addColdWound(attacker, this, pos, damage, armourMod, infection, poison, noMinimumDamage, spell);
            }
            if (woundType == 7) {
                return CombatEngine.addDrownWound(attacker, this, pos, damage, armourMod, infection, poison, noMinimumDamage, spell);
            }
            if (woundType == 9) {
                return CombatEngine.addInternalWound(attacker, this, pos, damage, armourMod, infection, poison, noMinimumDamage, spell);
            }
            if (woundType == 10) {
                return CombatEngine.addAcidWound(attacker, this, pos, damage, armourMod, infection, poison, noMinimumDamage, spell);
            }
            if (woundType == 4) {
                return CombatEngine.addFireWound(attacker, this, pos, damage, armourMod, infection, poison, noMinimumDamage, spell);
            }
            if (woundType == 6) {
                return CombatEngine.addRotWound(attacker, this, pos, damage, armourMod, infection, poison, noMinimumDamage, spell);
            }
            if (woundType == 5) {
                return CombatEngine.addWound(attacker, this, woundType, pos, damage, armourMod, "poison", null, infection, poison, false, true, noMinimumDamage, spell);
            }
            return CombatEngine.addWound(attacker, this, woundType, pos, damage, armourMod, "hit", null, infection, poison, false, true, noMinimumDamage, spell);
        }
        catch (NoSpaceException nsp) {
            Creature.logger.log(Level.WARNING, this.getName() + " no armour space on loc " + pos);
        }
        catch (Exception e) {
            Creature.logger.log(Level.WARNING, e.getMessage(), e);
        }
        return false;
    }
    
    public float addSpellResistance(final short spellId) {
        return 1.0f;
    }
    
    public SpellResistance getSpellResistance(final short spellId) {
        return null;
    }
    
    public final boolean isInPvPZone() {
        return !this.isInNonPvPZone && this.isInPvPZone;
    }
    
    public final boolean isOnPvPServer() {
        return !this.isInNonPvPZone && (Servers.localServer.PVPSERVER || this.isInPvPZone || this.isInDuelRing);
    }
    
    public short getHotaWins() {
        return 0;
    }
    
    public void setHotaWins(final short wins) {
    }
    
    public void setVehicleCommander(final boolean isCommander) {
        this.isVehicleCommander = isCommander;
    }
    
    public long getFace() {
        return 0L;
    }
    
    public byte getRarity() {
        return 0;
    }
    
    public byte getRarityShader() {
        if (this.getBonusForSpellEffect((byte)22) > 70.0f) {
            return 2;
        }
        if (this.getBonusForSpellEffect((byte)22) > 0.0f) {
            return 1;
        }
        return 0;
    }
    
    public void achievement(final int achievementId) {
    }
    
    public void addTitle(final Titles.Title title) {
    }
    
    public void removeTitle(final Titles.Title title) {
    }
    
    public void achievement(final int achievementId, final int counterModifier) {
    }
    
    protected void addTileMovedDragging() {
    }
    
    protected void addTileMovedRiding() {
    }
    
    protected void addTileMoved() {
    }
    
    protected void addTileMovedDriving() {
    }
    
    protected void addTileMovedPassenger() {
    }
    
    public int getKarma() {
        if (this.isSpellCaster() || this.isSummoner()) {
            return 10000;
        }
        return 0;
    }
    
    public void setKarma(final int newKarma) {
    }
    
    public void modifyKarma(final int points) {
    }
    
    public long getTimeToSummonCorpse() {
        return 0L;
    }
    
    public boolean maySummonCorpse() {
        return false;
    }
    
    public final void pushToFloorLevel(final int floorLevel) {
        try {
            if (!this.isPlayer()) {
                final float oldposz = this.getPositionZ();
                final float newPosz = Zones.calculateHeight(this.getPosX(), this.getPosY(), this.isOnSurface()) + floorLevel * 3 + 0.25f;
                final float diffz = newPosz - oldposz;
                this.getStatus().setPositionZ(newPosz, true);
                if (this.currentTile != null && this.getVisionArea() != null) {
                    this.moved(0.0f, 0.0f, diffz, 0, 0);
                }
            }
        }
        catch (NoSuchZoneException ex) {}
    }
    
    public final float calculatePosZ() {
        return Zones.calculatePosZ(this.getPosX(), this.getPosY(), this.getCurrentTile(), this.isOnSurface(), this.isFloating(), this.getPositionZ(), this, this.getBridgeId());
    }
    
    public final boolean canOpenDoors() {
        return this.template.canOpenDoors();
    }
    
    public final int getFloorLevel(final boolean ignoreVehicleOffset) {
        try {
            float vehicleOffsetToRemove = 0.0f;
            if (ignoreVehicleOffset) {
                final long vehicleId = this.getVehicle();
                if (vehicleId != -10L) {
                    final Vehicle vehicle = Vehicles.getVehicleForId(vehicleId);
                    if (vehicle == null) {
                        Creature.logger.log(Level.WARNING, "Unknown vehicle for id: " + vehicleId + " resulting in possinly incorrect floor level!");
                    }
                    else {
                        final Seat seat = vehicle.getSeatFor(this.id);
                        if (seat == null) {
                            Creature.logger.log(Level.WARNING, "Unable to find the seat the player: " + this.id + " supposedly is on, Vehicle id: " + vehicleId + ". Resulting in possibly incorrect floor level calculation.");
                        }
                        else {
                            vehicleOffsetToRemove = Math.max(this.getAltOffZ(), seat.offz);
                        }
                    }
                }
            }
            final float playerPosZ = this.getPositionZ() + this.getAltOffZ();
            final float groundHeight = Math.max(0.0f, Zones.calculateHeight(this.getPosX(), this.getPosY(), this.isOnSurface()));
            final float posZ = Math.max(0.0f, (playerPosZ - groundHeight - vehicleOffsetToRemove + 0.5f) * 10.0f);
            return (int)posZ / 30;
        }
        catch (NoSuchZoneException snz) {
            return 0;
        }
    }
    
    public final int getFloorLevel() {
        return this.getFloorLevel(false);
    }
    
    public boolean fireTileLog() {
        return false;
    }
    
    public void sendActionControl(final String actionString, final boolean start, final int timeLeft) {
    }
    
    public byte getBlood() {
        return 0;
    }
    
    public Shop getShop() {
        return Economy.getEconomy().getShop(this);
    }
    
    public void setScenarioKarma(final int newKarma) {
    }
    
    public int getScenarioKarma() {
        return 0;
    }
    
    public boolean knowsKarmaSpell(final int karmaSpellActionNum) {
        return this.isSpellCaster() || this.isSummoner();
    }
    
    public float getFireResistance() {
        return this.template.fireResistance;
    }
    
    public boolean checkCoinAward(final int chance) {
        return false;
    }
    
    public float getColdResistance() {
        return this.template.coldResistance;
    }
    
    public float getDiseaseResistance() {
        return this.template.diseaseResistance;
    }
    
    public float getPhysicalResistance() {
        return this.template.physicalResistance;
    }
    
    public float getPierceResistance() {
        return this.template.pierceResistance;
    }
    
    public float getSlashResistance() {
        return this.template.slashResistance;
    }
    
    public float getCrushResistance() {
        return this.template.crushResistance;
    }
    
    public float getBiteResistance() {
        return this.template.biteResistance;
    }
    
    public float getPoisonResistance() {
        return this.template.poisonResistance;
    }
    
    public float getWaterResistance() {
        return this.template.waterResistance;
    }
    
    public float getAcidResistance() {
        return this.template.acidResistance;
    }
    
    public float getInternalResistance() {
        return this.template.internalResistance;
    }
    
    public float getFireVulnerability() {
        return this.template.fireVulnerability;
    }
    
    public float getColdVulnerability() {
        return this.template.coldVulnerability;
    }
    
    public float getDiseaseVulnerability() {
        return this.template.diseaseVulnerability;
    }
    
    public float getPhysicalVulnerability() {
        return this.template.physicalVulnerability;
    }
    
    public float getPierceVulnerability() {
        return this.template.pierceVulnerability;
    }
    
    public float getSlashVulnerability() {
        return this.template.slashVulnerability;
    }
    
    public float getCrushVulnerability() {
        return this.template.crushVulnerability;
    }
    
    public float getBiteVulnerability() {
        return this.template.biteVulnerability;
    }
    
    public float getPoisonVulnerability() {
        return this.template.poisonVulnerability;
    }
    
    public float getWaterVulnerability() {
        return this.template.waterVulnerability;
    }
    
    public float getAcidVulnerability() {
        return this.template.acidVulnerability;
    }
    
    public float getInternalVulnerability() {
        return this.template.internalVulnerability;
    }
    
    public boolean hasAnyAbility() {
        return false;
    }
    
    public static final Set<MovementEntity> getIllusionsFor(final long wurmid) {
        return Creature.illusions.get(wurmid);
    }
    
    public static final long getWurmIdForIllusion(final long illusionId) {
        for (final Set<MovementEntity> set : Creature.illusions.values()) {
            for (final MovementEntity entity : set) {
                if (entity.getWurmid() == illusionId) {
                    return entity.getCreatorId();
                }
            }
        }
        return -10L;
    }
    
    public void addIllusion(final MovementEntity entity) {
        Set<MovementEntity> entities = Creature.illusions.get(this.getWurmId());
        if (entities == null) {
            entities = new HashSet<MovementEntity>();
            Creature.illusions.put(this.getWurmId(), entities);
        }
        entities.add(entity);
    }
    
    public boolean isUndead() {
        return false;
    }
    
    public byte getUndeadType() {
        return 0;
    }
    
    public String getUndeadTitle() {
        return "";
    }
    
    public final void setBridgeId(final long bid) {
        this.setBridgeId(bid, true);
    }
    
    public final void setBridgeId(final long bid, final boolean sendToSelf) {
        this.status.getPosition().setBridgeId(bid);
        if (this.getMovementScheme() != null) {
            this.getMovementScheme().setBridgeId(bid);
        }
        if (this.getCurrentTile() != null) {
            this.getCurrentTile().sendSetBridgeId(this, bid, sendToSelf);
        }
    }
    
    public long getMoneyEarnedBySellingLastHour() {
        return 0L;
    }
    
    public void addMoneyEarnedBySellingLastHour(final long money) {
    }
    
    public void setModelName(final String newModelName) {
    }
    
    public final void calcBattleCampBonus() {
        Item closest = null;
        for (final FocusZone fz : FocusZone.getZonesAt(this.getTileX(), this.getTileY())) {
            if (fz.isBattleCamp()) {
                for (final Item wartarget : Items.getWarTargets()) {
                    if (closest == null || getRange(this, wartarget.getPosX(), wartarget.getPosY()) < getRange(this, closest.getPosX(), closest.getPosY())) {
                        closest = wartarget;
                    }
                }
            }
        }
        if (closest != null) {
            this.isInOwnBattleCamp = (closest.getKingdom() == this.getKingdomId());
        }
        this.isInOwnBattleCamp = false;
        Creature.logger.log(Level.INFO, this.getName() + " set battle camp bonus to " + this.isInOwnBattleCamp);
    }
    
    public final boolean hasBattleCampBonus() {
        return this.isInOwnBattleCamp;
    }
    
    public boolean isVisibleToPlayers() {
        return this.visibleToPlayers;
    }
    
    public void setVisibleToPlayers(final boolean aVisibleToPlayers) {
        this.visibleToPlayers = aVisibleToPlayers;
    }
    
    public boolean isDoLavaDamage() {
        return this.doLavaDamage;
    }
    
    public void setDoLavaDamage(final boolean aDoLavaDamage) {
        this.doLavaDamage = aDoLavaDamage;
    }
    
    public final boolean doLavaDamage() {
        this.setDoLavaDamage(false);
        if (!this.isInvulnerable() && !this.isGhost() && !this.isUnique() && (this.getDeity() == null || !this.getDeity().isMountainGod() || this.getFaith() < 35.0f) && this.getFarwalkerSeconds() <= 0) {
            Wound wound = null;
            boolean dead = false;
            try {
                final byte pos = this.getBody().getRandomWoundPos((byte)10);
                if (Server.rand.nextInt(10) <= 6 && this.getBody().getWounds() != null) {
                    wound = this.getBody().getWounds().getWoundAtLocation(pos);
                    if (wound != null) {
                        dead = wound.modifySeverity((int)(5000.0f + Server.rand.nextInt(5000) * (100.0f - this.getSpellDamageProtectBonus()) / 100.0f));
                        wound.setBandaged(false);
                        this.setWounded();
                    }
                }
                if (wound == null && !this.isGhost() && !this.isUnique() && !this.isKingdomGuard()) {
                    dead = this.addWoundOfType(null, (byte)4, pos, false, 1.0f, true, 5000.0f + Server.rand.nextInt(5000) * (100.0f - this.getSpellDamageProtectBonus()) / 100.0f, 0.0f, 0.0f, false, false);
                }
                this.getCommunicator().sendAlertServerMessage("You are burnt by lava!");
                if (dead) {
                    this.achievement(142);
                    return true;
                }
            }
            catch (Exception ex) {
                Creature.logger.log(Level.WARNING, this.getName() + " " + ex.getMessage(), ex);
            }
        }
        return false;
    }
    
    public boolean isDoAreaDamage() {
        return this.doAreaDamage;
    }
    
    public void setDoAreaEffect(final boolean aDoAreaDamage) {
        this.doAreaDamage = aDoAreaDamage;
    }
    
    public byte getPathfindCounter() {
        return this.pathfindcounter;
    }
    
    public void setPathfindcounter(final int i) {
        this.pathfindcounter = (byte)i;
    }
    
    public int getHugeMoveCounter() {
        return this.hugeMoveCounter;
    }
    
    public void setHugeMoveCounter(final int aHugeMoveCounter) {
        this.hugeMoveCounter = Math.max(0, aHugeMoveCounter);
    }
    
    public void setArmourLimitingFactor(final float factor, final boolean initializing) {
    }
    
    public float getArmourLimitingFactor() {
        return 0.0f;
    }
    
    public void recalcLimitingFactor(final Item currentItem) {
    }
    
    public final float getAltOffZ() {
        if (this.getVehicle() != -10L) {
            final Vehicle vehic = Vehicles.getVehicleForId(this.getVehicle());
            if (vehic != null) {
                final Seat s = vehic.getSeatFor(this.getWurmId());
                if (s != null) {
                    return s.getAltOffz();
                }
            }
        }
        return 0.0f;
    }
    
    public final boolean followsGround() {
        return this.getBridgeId() == -10L && (!this.isPlayer() || this.getMovementScheme().onGround) && this.getFloorLevel() == 0;
    }
    
    public final boolean isWagoner() {
        return this.template.getTemplateId() == 114;
    }
    
    public final boolean isFish() {
        return this.template.getTemplateId() == 119;
    }
    
    @Nullable
    public final Wagoner getWagoner() {
        if (this.isWagoner()) {
            return Wagoner.getWagoner(this.id);
        }
        return null;
    }
    
    @Override
    public String getTypeName() {
        return this.getTemplate().getName();
    }
    
    @Override
    public String getObjectName() {
        if (this.isWagoner()) {
            return this.getName();
        }
        return this.petName;
    }
    
    @Override
    public boolean setObjectName(final String aNewName, final Creature aCreature) {
        this.setVisible(false);
        this.setPetName(aNewName);
        this.setVisible(true);
        this.status.setChanged(true);
        return true;
    }
    
    @Override
    public boolean isActualOwner(final long playerId) {
        return false;
    }
    
    @Override
    public boolean isOwner(final Creature creature) {
        return this.isOwner(creature.getWurmId());
    }
    
    @Override
    public boolean isOwner(final long playerId) {
        if (this.isWagoner()) {
            final Wagoner wagoner = this.getWagoner();
            return wagoner != null && wagoner.getOwnerId() == playerId;
        }
        final Village bVill = this.getBrandVillage();
        return bVill != null && bVill.isMayor(playerId);
    }
    
    @Override
    public boolean canChangeOwner(final Creature creature) {
        return false;
    }
    
    @Override
    public boolean canChangeName(final Creature creature) {
        if (this.isWagoner()) {
            return false;
        }
        if (creature.getPower() > 1) {
            return true;
        }
        final Village bVill = this.getBrandVillage();
        return bVill != null && bVill.isMayor(creature);
    }
    
    @Override
    public boolean setNewOwner(final long playerId) {
        if (!this.isWagoner()) {
            return false;
        }
        final Wagoner wagoner = this.getWagoner();
        if (wagoner != null) {
            wagoner.setOwnerId(playerId);
            return true;
        }
        return false;
    }
    
    @Override
    public String getOwnerName() {
        return "";
    }
    
    @Override
    public String getWarning() {
        if (this.isWagoner()) {
            return "";
        }
        final Village bVill = this.getBrandVillage();
        if (bVill == null) {
            return "NEEDS TO BE BRANDED FOR PERMISSIONS TO WORK";
        }
        return "";
    }
    
    @Override
    public PermissionsPlayerList getPermissionsPlayerList() {
        return AnimalSettings.getPermissionsPlayerList(this.getWurmId());
    }
    
    @Override
    public boolean isManaged() {
        return true;
    }
    
    @Override
    public boolean isManageEnabled(final Player player) {
        return false;
    }
    
    @Override
    public void setIsManaged(final boolean newIsManaged, final Player player) {
    }
    
    @Override
    public String mayManageText(final Player player) {
        if (this.isWagoner()) {
            return "";
        }
        final Village bVill = this.getBrandVillage();
        if (bVill != null) {
            return "Settlement \"" + bVill.getName() + "\" may manage";
        }
        return "";
    }
    
    @Override
    public String mayManageHover(final Player aPlayer) {
        return "";
    }
    
    @Override
    public String messageOnTick() {
        return "";
    }
    
    @Override
    public String questionOnTick() {
        return "";
    }
    
    @Override
    public String messageUnTick() {
        return "";
    }
    
    @Override
    public String questionUnTick() {
        return "";
    }
    
    @Override
    public String getSettlementName() {
        String sName = "";
        final Village bVill = this.isWagoner() ? this.citizenVillage : this.getBrandVillage();
        if (bVill != null) {
            sName = bVill.getName();
        }
        if (sName.length() > 0) {
            return "Citizens of \"" + sName + "\"";
        }
        return "";
    }
    
    @Override
    public String getAllianceName() {
        String aName = "";
        final Village bVill = this.isWagoner() ? this.citizenVillage : this.getBrandVillage();
        if (bVill != null) {
            aName = bVill.getAllianceName();
        }
        if (aName.length() > 0) {
            return "Alliance of \"" + aName + "\"";
        }
        return "";
    }
    
    @Override
    public String getKingdomName() {
        return "";
    }
    
    @Override
    public boolean canAllowEveryone() {
        return true;
    }
    
    @Override
    public String getRolePermissionName() {
        final Village bVill = this.getBrandVillage();
        if (bVill != null) {
            return "Brand Permission of \"" + bVill.getName() + "\"";
        }
        return "";
    }
    
    @Override
    public boolean isCitizen(final Creature creature) {
        final Village bVill = this.isWagoner() ? this.citizenVillage : this.getBrandVillage();
        return bVill != null && bVill.isCitizen(creature);
    }
    
    @Override
    public boolean isAllied(final Creature creature) {
        final Village bVill = this.isWagoner() ? this.citizenVillage : this.getBrandVillage();
        return bVill != null && bVill.isAlly(creature);
    }
    
    @Override
    public boolean isSameKingdom(final Creature creature) {
        return false;
    }
    
    @Override
    public void addGuest(final long guestId, final int aSettings) {
        AnimalSettings.addPlayer(this.getWurmId(), guestId, aSettings);
    }
    
    @Override
    public void removeGuest(final long guestId) {
        AnimalSettings.removePlayer(this.getWurmId(), guestId);
    }
    
    @Override
    public void addDefaultCitizenPermissions() {
        if (!this.getPermissionsPlayerList().exists(-30L)) {
            final int value = AnimalSettings.Animal1Permissions.COMMANDER.getValue();
            this.addNewGuest(-30L, value);
        }
    }
    
    @Override
    public boolean isGuest(final Creature creature) {
        return this.isGuest(creature.getWurmId());
    }
    
    @Override
    public boolean isGuest(final long playerId) {
        return AnimalSettings.isGuest(this, playerId);
    }
    
    @Override
    public int getMaxAllowed() {
        return AnimalSettings.getMaxAllowed();
    }
    
    public void addNewGuest(final long guestId, final int aSettings) {
        AnimalSettings.addPlayer(this.getWurmId(), guestId, aSettings);
    }
    
    public Village getBrandVillage() {
        final Brand brand = Creatures.getInstance().getBrand(this.getWurmId());
        if (brand != null) {
            try {
                final Village villageBrand = Villages.getVillage((int)brand.getBrandId());
                return villageBrand;
            }
            catch (NoSuchVillageException nsv) {
                brand.deleteBrand();
            }
        }
        return null;
    }
    
    @Override
    public final boolean canHavePermissions() {
        return (this.isWagoner() && Features.Feature.WAGONER.isEnabled()) || this.getBrandVillage() != null;
    }
    
    public final boolean mayLead(final Creature creature) {
        if (this.mayCommand(creature)) {
            return true;
        }
        if (AnimalSettings.isExcluded(this, creature)) {
            return false;
        }
        final Village bvill = this.getBrandVillage();
        if (bvill != null) {
            final VillageRole vr = bvill.getRoleFor(creature);
            return vr.mayLead();
        }
        final Village cvill = this.getCurrentVillage();
        if (cvill != null) {
            final VillageRole vr2 = cvill.getRoleFor(creature);
            return vr2.mayLead();
        }
        return true;
    }
    
    @Override
    public final boolean mayShowPermissions(final Creature creature) {
        return this.canHavePermissions() && this.mayManage(creature);
    }
    
    public final boolean canManage(final Creature creature) {
        if (this.isWagoner()) {
            final Wagoner wagoner = this.getWagoner();
            if (wagoner != null) {
                if (wagoner.getOwnerId() == creature.getWurmId()) {
                    return true;
                }
                if (creature.getCitizenVillage() != null && creature.getCitizenVillage() == this.citizenVillage && creature.getCitizenVillage().isMayor(creature)) {
                    return true;
                }
            }
        }
        if (AnimalSettings.isExcluded(this, creature)) {
            return false;
        }
        final Village vill = this.getBrandVillage();
        return AnimalSettings.canManage(this, creature, vill) || (creature.getCitizenVillage() != null && vill != null && vill.isCitizen(creature) && vill.isActionAllowed((short)663, creature));
    }
    
    public final boolean mayManage(final Creature creature) {
        return (creature.getPower() > 1 && !this.isPlayer()) || this.canManage(creature);
    }
    
    public final boolean maySeeHistory(final Creature creature) {
        if (this.isWagoner()) {
            final Wagoner wagoner = this.getWagoner();
            if (wagoner != null) {
                if (wagoner.getOwnerId() == creature.getWurmId()) {
                    return true;
                }
                if (creature.getCitizenVillage() != null && creature.getCitizenVillage() == this.citizenVillage && creature.getCitizenVillage().isMayor(creature)) {
                    return true;
                }
            }
        }
        if (creature.getPower() > 1 && !this.isPlayer()) {
            return true;
        }
        final Village bVill = this.getBrandVillage();
        return bVill != null && bVill.isMayor(creature);
    }
    
    public final boolean mayCommand(final Creature creature) {
        return !AnimalSettings.isExcluded(this, creature) && AnimalSettings.mayCommand(this, creature, this.getBrandVillage());
    }
    
    public final boolean mayPassenger(final Creature creature) {
        return !AnimalSettings.isExcluded(this, creature) && AnimalSettings.mayPassenger(this, creature, this.getBrandVillage());
    }
    
    public final boolean mayAccessHold(final Creature creature) {
        return !AnimalSettings.isExcluded(this, creature) && AnimalSettings.mayAccessHold(this, creature, this.getBrandVillage());
    }
    
    public final boolean mayUse(final Creature creature) {
        return !AnimalSettings.isExcluded(this, creature) && AnimalSettings.mayUse(this, creature, this.getBrandVillage());
    }
    
    public final boolean publicMayUse(final Creature creature) {
        return !AnimalSettings.isExcluded(this, creature) && AnimalSettings.publicMayUse(this);
    }
    
    public ServerEntry getDestination() {
        return this.destination;
    }
    
    public void setDestination(final ServerEntry destination) {
        if (destination != null && !destination.isChallengeOrEpicServer() && !destination.LOGINSERVER && destination != Servers.localServer) {
            this.destination = destination;
        }
    }
    
    public void clearDestination() {
        this.destination = null;
    }
    
    public int getVillageId() {
        if (this.getCitizenVillage() != null) {
            return this.getCitizenVillage().getId();
        }
        return 0;
    }
    
    private static Item getRareRecipe(final String sig, final int commonRecipeId, final int rareRecipeId, final int supremeRecipeId, final int fantasticRecipeId) {
        final int rno = Server.rand.nextInt(Servers.isThisATestServer() ? 100 : 1000);
        if (rno < 100) {
            int recipeId = -10;
            if (rno == 0 && fantasticRecipeId != -10) {
                recipeId = fantasticRecipeId;
            }
            else if (rno < 6 && supremeRecipeId != -10) {
                recipeId = supremeRecipeId;
            }
            else if (rno < 31 && rareRecipeId != -10) {
                recipeId = rareRecipeId;
            }
            else if (rno >= 50 && commonRecipeId != -10) {
                recipeId = commonRecipeId;
            }
            if (recipeId == -10) {
                return null;
            }
            final Recipe recipe = Recipes.getRecipeById((short)recipeId);
            if (recipe == null) {
                return null;
            }
            final int pp = Server.rand.nextBoolean() ? 1272 : 748;
            final int itq = 20 + Server.rand.nextInt(50);
            try {
                final Item newItem = ItemFactory.createItem(pp, itq, (byte)0, recipe.getLootableRarity(), null);
                newItem.setInscription(recipe, sig, 1550103);
                return newItem;
            }
            catch (FailedException e) {
                Creature.logger.log(Level.WARNING, e.getMessage(), e);
            }
            catch (NoSuchTemplateException e2) {
                Creature.logger.log(Level.WARNING, e2.getMessage(), e2);
            }
        }
        return null;
    }
    
    public short getDamageCounter() {
        return this.damageCounter;
    }
    
    public void setDamageCounter(final short damageCounter) {
        this.damageCounter = damageCounter;
    }
    
    public List<Route> getHighwayPath() {
        return null;
    }
    
    public void setHighwayPath(final String newDestination, final List<Route> newPath) {
    }
    
    public String getHighwayPathDestination() {
        return "";
    }
    
    public long getLastWaystoneChecked() {
        return this.lastWaystoneChecked;
    }
    
    public void setLastWaystoneChecked(final long waystone) {
        this.lastWaystoneChecked = waystone;
        final Wagoner wagoner = this.getWagoner();
        if (this.isWagoner() && wagoner != null) {
            wagoner.setLastWaystoneId(waystone);
        }
    }
    
    public boolean embarkOn(final long wurmId, final byte type) {
        try {
            final Item item = Items.getItem(wurmId);
            final Vehicle vehicle = Vehicles.getVehicle(item);
            if (vehicle != null) {
                final Seat[] seats = vehicle.getSeats();
                for (int x = 0; x < seats.length; ++x) {
                    if (seats[x].getType() == type && !seats[x].isOccupied()) {
                        seats[x].occupy(vehicle, this);
                        if (type == 0) {
                            vehicle.pilotId = this.getWurmId();
                        }
                        this.setVehicleCommander(type == 0);
                        final MountAction m = new MountAction(null, item, vehicle, x, type == 0, vehicle.seats[x].offz);
                        this.setMountAction(m);
                        this.setVehicle(item.getWurmId(), true, type);
                        return true;
                    }
                }
            }
        }
        catch (NoSuchItemException e) {
            Creature.logger.log(Level.WARNING, e.getMessage(), e);
        }
        return false;
    }
    
    public ArrayList<Effect> getEffects() {
        return this.effects;
    }
    
    public void addEffect(final Effect e) {
        if (e == null) {
            return;
        }
        if (this.effects == null) {
            this.effects = new ArrayList<Effect>();
        }
        this.effects.add(e);
    }
    
    public void removeEffect(final Effect e) {
        if (this.effects == null || e == null) {
            return;
        }
        this.effects.remove(e);
        if (this.effects.isEmpty()) {
            this.effects = null;
        }
    }
    
    public void updateEffects() {
        if (this.effects == null) {
            return;
        }
        for (final Effect e : this.effects) {
            e.setPosXYZ(this.getPosX(), this.getPosY(), this.getPositionZ(), false);
        }
    }
    
    public boolean isPlacingItem() {
        return this.isPlacingItem;
    }
    
    public void setPlacingItem(final boolean placingItem) {
        if (!(this.isPlacingItem = placingItem)) {
            this.setPlacementItem(null);
        }
    }
    
    public void setPlacingItem(final boolean placingItem, final Item placementItem) {
        this.isPlacingItem = placingItem;
        this.setPlacementItem(placementItem);
    }
    
    public Item getPlacementItem() {
        return this.placementItem;
    }
    
    public void setPlacementItem(final Item placementItem) {
        this.placementItem = placementItem;
        if (placementItem == null) {
            this.pendingPlacement = null;
        }
    }
    
    public void setPendingPlacement(final float xPos, final float yPos, final float zPos, final float rot) {
        if (this.placementItem != null) {
            this.pendingPlacement = new float[] { this.placementItem.getPosX(), this.placementItem.getPosY(), this.placementItem.getPosZ(), this.placementItem.getRotation(), xPos, yPos, zPos, (Math.abs(rot - this.placementItem.getRotation()) > 180.0f) ? (rot - 360.0f) : rot };
        }
        else {
            this.pendingPlacement = null;
        }
    }
    
    public float[] getPendingPlacement() {
        return this.pendingPlacement;
    }
    
    public boolean canUseWithEquipment() {
        for (final Item subjectItem : this.getBody().getContainersAndWornItems()) {
            if (subjectItem.isCreatureWearableOnly()) {
                if (subjectItem.isSaddleLarge()) {
                    if (this.getSize() <= 4) {
                        return false;
                    }
                    if (this.isKingdomGuard()) {
                        return false;
                    }
                }
                else if (subjectItem.isSaddleNormal()) {
                    if (this.getSize() > 4) {
                        return false;
                    }
                    if (this.isKingdomGuard()) {
                        return false;
                    }
                }
                else if (subjectItem.isHorseShoe()) {
                    if (!this.isHorse() && (!this.isUnicorn() || (subjectItem.getMaterial() != 7 && subjectItem.getMaterial() != 8 && subjectItem.getMaterial() != 96))) {
                        return false;
                    }
                }
                else if (subjectItem.isBarding() && !this.isHorse()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public HashMap<Integer, SpellResist> getSpellResistances() {
        return this.spellResistances;
    }
    
    @Override
    public boolean isItem() {
        return false;
    }
    
    public void setGuardTower(final GuardTower guardTower) {
        this.guardTower = guardTower;
    }
    
    public GuardTower getGuardTower() {
        return this.guardTower;
    }
    
    static {
        emptyItems = new Item[0];
        willowMod = new DoubleValueModifier(-0.15000000596046448);
        logger = Logger.getLogger(Creature.class.getName());
        emptyCreatures = new Creature[0];
        LOG_ELAPSED_TIME_THRESHOLD = Constants.lagThreshold;
        pantLess = new HashSet<Long>();
        illusions = new ConcurrentHashMap<Long, Set<MovementEntity>>();
        Creature.pathFinder = new CreaturePathFinder();
        Creature.pathFinderAgg = new CreaturePathFinderAgg();
        Creature.pathFinderNPC = new CreaturePathFinderNPC();
        Creature.pathFinder.startRunning();
        Creature.pathFinderAgg.startRunning();
        Creature.pathFinderNPC.startRunning();
        Creature.firstCreature = -10L;
        Creature.pollChecksPer = 301;
        Creature.totx = 0;
        Creature.toty = 0;
        Creature.movesx = 0;
        Creature.movesy = 0;
    }
}
