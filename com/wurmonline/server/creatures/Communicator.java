// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.creatures;

import java.util.Hashtable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.NoSuchElementException;
import com.wurmonline.shared.constants.ValreiConstants;
import com.wurmonline.server.epic.ValreiFightHistory;
import com.wurmonline.server.epic.ValreiFightHistoryManager;
import com.wurmonline.server.webinterface.WcSetPower;
import com.wurmonline.server.Point4f;
import com.wurmonline.server.behaviours.FishEnums;
import com.wurmonline.server.players.PlayerJournal;
import com.wurmonline.server.zones.Den;
import com.wurmonline.server.zones.Dens;
import com.wurmonline.server.villages.RecruitmentAd;
import com.wurmonline.server.epic.CollectedValreiItem;
import com.wurmonline.server.epic.ValreiMapData;
import java.util.Collection;
import javax.annotation.Nullable;
import com.wurmonline.server.support.TicketAction;
import com.wurmonline.server.items.IngredientGroup;
import com.wurmonline.server.items.Ingredient;
import com.wurmonline.server.players.AchievementTemplate;
import com.wurmonline.math.Vector3f;
import com.wurmonline.server.behaviours.CaveWallBehaviour;
import java.util.Optional;
import com.wurmonline.server.behaviours.MethodsStructure;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.behaviours.BuildAllMaterials;
import com.wurmonline.server.behaviours.BuildStageMaterials;
import com.wurmonline.server.behaviours.BuildMaterial;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.shared.constants.StructureConstantsEnum;
import com.wurmonline.server.items.CreationWindowMethods;
import com.wurmonline.server.structures.BridgePartEnum;
import com.wurmonline.shared.constants.BridgeConstants;
import com.wurmonline.server.structures.RoofFloorEnum;
import com.wurmonline.shared.constants.WallConstants;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.sounds.Sound;
import com.wurmonline.server.structures.FenceGate;
import com.wurmonline.server.structures.Door;
import com.wurmonline.shared.constants.StructureTypeEnum;
import com.wurmonline.server.structures.Wall;
import com.wurmonline.server.structures.BridgePart;
import com.wurmonline.server.structures.Floor;
import com.wurmonline.server.structures.Fence;
import java.math.BigInteger;
import com.wurmonline.server.highways.HighwayPos;
import com.wurmonline.server.highways.ClosestVillage;
import com.wurmonline.server.highways.Route;
import com.wurmonline.server.highways.Node;
import com.wurmonline.server.highways.MethodsHighways;
import com.wurmonline.server.highways.Routes;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.zones.Water;
import com.wurmonline.shared.util.ItemTypeUtilites;
import com.wurmonline.server.items.WurmColor;
import com.wurmonline.shared.util.StringUtilities;
import com.wurmonline.server.utils.CreatureLineSegment;
import com.wurmonline.server.questions.NewsInfo;
import com.wurmonline.server.questions.WurmInfo;
import com.wurmonline.server.questions.WurmInfo2;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.spells.SpellEffect;
import com.wurmonline.server.behaviours.Terraforming;
import com.wurmonline.server.tutorial.Mission;
import com.wurmonline.server.tutorial.Missions;
import com.wurmonline.server.epic.EpicServerStatus;
import com.wurmonline.server.epic.HexMap;
import com.wurmonline.server.epic.EpicXmlWriter;
import java.util.HashMap;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.questions.AlertServerMessageQuestion;
import com.wurmonline.server.statistics.ChallengeSummary;
import com.wurmonline.server.players.Cults;
import com.wurmonline.shared.constants.PlayerOnlineStatus;
import com.wurmonline.server.players.Achievement;
import com.wurmonline.server.players.Achievements;
import com.wurmonline.server.questions.PersonalGoalsListQuestion;
import com.wurmonline.server.items.WurmMail;
import com.wurmonline.server.behaviours.CreatureBehaviour;
import com.wurmonline.server.behaviours.CargoTransportationMethods;
import com.wurmonline.server.items.DbItem;
import com.wurmonline.server.items.CreationEntryCreator;
import com.wurmonline.server.webinterface.WcResetCommand;
import com.wurmonline.server.webinterface.WcOpenEpicPortal;
import com.wurmonline.server.skills.Affinity;
import com.wurmonline.server.webinterface.WcGetHeroes;
import com.wurmonline.server.zones.TilePoller;
import java.util.List;
import com.wurmonline.shared.util.MulticolorLineSegment;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.Affinities;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.server.skills.SkillSystem;
import com.wurmonline.server.questions.RedeemQuestion;
import com.wurmonline.server.webinterface.WcRefreshCommand;
import com.wurmonline.server.behaviours.Fish;
import com.wurmonline.server.items.ItemTemplateFactory;
import java.util.ArrayList;
import com.wurmonline.server.questions.GMForceSpawnRiftLootQuestion;
import com.wurmonline.server.zones.ErrorChecks;
import com.wurmonline.server.players.KingdomIp;
import com.wurmonline.server.support.Trello;
import com.wurmonline.server.endgames.EndGameItems;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import com.wurmonline.shared.exceptions.WurmServerException;
import com.wurmonline.server.villages.VillageRecruitee;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.questions.VillageTeleportQuestion;
import java.awt.Color;
import com.wurmonline.server.behaviours.MethodsFishing;
import com.wurmonline.server.questions.SkillProgressQuestion;
import com.wurmonline.server.zones.ZonesUtility;
import com.wurmonline.server.questions.InGameVoteQuestion;
import com.wurmonline.server.questions.SuicideQuestion;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.questions.ChangeEmailQuestion;
import com.wurmonline.server.players.Titles;
import com.wurmonline.server.kingdom.King;
import com.wurmonline.server.questions.TitleQuestion;
import com.wurmonline.server.questions.TitleCompoundQuestion;
import com.wurmonline.server.questions.SimplePopup;
import com.wurmonline.server.behaviours.MethodsCreatures;
import com.wurmonline.server.villages.VillageRole;
import com.wurmonline.server.villages.Citizen;
import com.wurmonline.server.players.WurmRecord;
import java.util.Map;
import com.wurmonline.server.questions.TransferQuestion;
import com.wurmonline.server.webinterface.WcDemotion;
import com.wurmonline.server.webinterface.WcGlobalModeration;
import com.wurmonline.server.players.Ban;
import com.wurmonline.server.players.SteamIdBan;
import com.wurmonline.server.steam.SteamId;
import com.wurmonline.server.players.PlayerState;
import com.wurmonline.server.webinterface.WcAddFriend;
import com.wurmonline.server.combat.CombatMove;
import java.sql.Date;
import com.wurmonline.server.villages.RecruitmentAds;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.items.Recipe;
import com.wurmonline.server.items.RecipesByPlayer;
import com.wurmonline.server.items.Recipes;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.WurmHarvestables;
import com.wurmonline.common.BuildProperties;
import com.wurmonline.server.players.Friend;
import java.util.regex.Matcher;
import com.wurmonline.server.kingdom.Kingdom;
import com.wurmonline.server.Team;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.webinterface.WcCAHelpGroupMessage;
import com.wurmonline.server.webinterface.WcGVHelpMessage;
import com.wurmonline.server.webinterface.WcTradeChannel;
import java.util.regex.Pattern;
import com.wurmonline.server.webinterface.WCGmMessage;
import com.wurmonline.server.webinterface.WcMgmtMessage;
import com.wurmonline.server.webinterface.WcKingdomChat;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.villages.PvPAlliance;
import com.wurmonline.server.ServerTweaksHandler;
import com.wurmonline.server.behaviours.Methods;
import com.wurmonline.server.deities.Deities;
import com.wurmonline.server.players.PendingAccount;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.structures.NoSuchWallException;
import com.wurmonline.server.behaviours.NoSuchBehaviourException;
import com.wurmonline.server.behaviours.BehaviourDispatcher;
import com.wurmonline.server.economy.Change;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.banks.Bank;
import com.wurmonline.server.items.TradingWindow;
import com.wurmonline.server.items.Trade;
import com.wurmonline.server.questions.RemoveItemQuestion;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.MethodsItems;
import com.wurmonline.server.utils.StringUtil;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.banks.Banks;
import com.wurmonline.server.items.NotOwnedException;
import com.wurmonline.server.tutorial.PlayerTutorial;
import com.wurmonline.server.questions.TeamManagementQuestion;
import com.wurmonline.server.questions.VillageJoinQuestion;
import java.util.StringTokenizer;
import com.wurmonline.server.support.Ticket;
import com.wurmonline.server.support.Tickets;
import com.wurmonline.server.players.MapAnnotation;
import com.wurmonline.server.questions.PortalQuestion;
import com.wurmonline.server.structures.PlanBridgeMethods;
import com.wurmonline.server.Point;
import com.wurmonline.server.questions.Question;
import com.wurmonline.server.questions.NoSuchQuestionException;
import com.wurmonline.server.questions.Questions;
import java.util.Properties;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.LoginHandler;
import java.io.UnsupportedEncodingException;
import com.wurmonline.server.EigcClient;
import com.wurmonline.server.Eigc;
import java.util.logging.Level;
import com.wurmonline.server.Constants;
import com.wurmonline.server.Features;
import java.util.HashSet;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.wurmonline.server.LoginServerWebConnection;
import java.util.LinkedList;
import com.wurmonline.server.intra.PlayerTransfer;
import com.wurmonline.server.behaviours.Seat;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import com.wurmonline.server.behaviours.Vehicle;
import com.wurmonline.server.behaviours.Vehicles;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.Message;
import com.wurmonline.server.Players;
import com.wurmonline.server.Servers;
import com.wurmonline.server.webinterface.WcGlobalIgnore;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.zones.Zone;
import com.wurmonline.server.structures.BlockingResult;
import com.wurmonline.server.zones.NoSuchZoneException;
import com.wurmonline.server.structures.Blocking;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.mesh.Tiles;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.wurmonline.server.Server;
import com.wurmonline.server.items.Item;
import java.util.Set;
import java.text.DecimalFormat;
import java.text.DateFormat;
import com.wurmonline.server.ServerEntry;
import java.util.logging.Logger;
import com.wurmonline.communication.SocketConnection;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.economy.MonetaryConstants;
import com.wurmonline.shared.constants.CounterTypes;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.shared.constants.ObjectTypeConstants;
import com.wurmonline.shared.constants.StructureConstants;
import com.wurmonline.shared.constants.ProtoConstants;
import com.wurmonline.communication.SimpleConnectionListener;

public abstract class Communicator implements SimpleConnectionListener, ProtoConstants, StructureConstants, ObjectTypeConstants, TimeConstants, MiscConstants, ItemTypes, CounterTypes, MonetaryConstants
{
    public Player player;
    private boolean ready;
    private static boolean gchatdisabled;
    protected boolean justLoggedIn;
    private SocketConnection connection;
    private static boolean acceptsBoatTransfers;
    private static final Logger logger;
    private static final Logger chatlogger;
    private MovementScheme ticker;
    private int messagesReceived;
    private static final int firstBorder;
    private static final int secondBorder;
    private static final int finalBorder;
    private static final int serverBorderStart = 0;
    private static final int serverBorderEnd;
    private static final float maxMapSize;
    private static final String DARK_SHADOW_STRING = "You see dark shadows moving beneath the waves.";
    private static final String ENORMOUS_SHARK_STRING = "An enourmous shark scouts you from below.";
    private static final String IMPOS_CURRENT_STRING = "The currents work against you and you get nowhere.";
    private static final String FINAL_CURRENT_STRING = "You head out to sea.";
    private static final String UNAVAILABLE_SERVER_STRING = "The waves are too high to keep going in that direction right now. (The server is unavailable)";
    private static final String HIC = "*hic*";
    private static final String BURP = "*burp*";
    private static final String HIC2 = "*HIC*";
    private static final String BURP2 = "*BURP*";
    private static final String HRR = "HRR";
    private static final String HSS = "HSS";
    private static final String UUH = "UUH";
    private static final String GHH = "GHH*";
    protected static final byte[] local;
    protected static final byte[] event;
    protected static final byte[] system;
    protected static final byte[] help;
    protected static final byte[] team;
    protected static final byte[] friends;
    protected static final byte[] village;
    protected static final byte[] alliance;
    protected static final byte[] combat;
    private static final byte[] deaths;
    private static final byte[] logs;
    private long timeMod;
    public static final String GM = "GM";
    protected static final byte[] gms;
    public static final String MGMT = "MGMT";
    private static final byte[] mgmt;
    private static final byte[] pas;
    private long lastToggledPA;
    private ServerEntry lastTargetServerChecked;
    private boolean shouldSendTradeAgree;
    private boolean tradeAgreeToSend;
    private static byte[] myIp;
    private final byte[] reportedIp;
    private boolean setCheatedIp;
    private int ipPointer;
    private final DateFormat df;
    private long newSeed;
    private int newSeedPointer;
    private boolean invulnerable;
    private static final DecimalFormat twoDecimals;
    private PlayerMove currentmove;
    private int availableMoves;
    private int newHeightOffset;
    private boolean changeHeightImmediately;
    private boolean setWeather;
    private int moves;
    private boolean receivedTicks;
    private int lastLayer;
    private long[] subjectIds;
    private Set<Item> coins;
    private Item subjectItem;
    private boolean shouldSendWeather;
    private static int numcommands;
    private static int prevcommand;
    private static int lastcommand;
    private static long commandAction;
    private int commandsThisSecond;
    private static String commandMessage;
    private byte wroteip;
    private static final float woundMultiplier = 0.0015259022f;
    private static final int emptyRock;
    private byte[] passengerData;
    private byte[] vehicleData;
    public long lastChangedEmail;
    protected static final String CHARSET_ENCODING_FOR_COMMS = "UTF-8";
    private static final String THE = "The ";
    private float lastX;
    private float lastY;
    private float lastZ;
    private int lastCounts;
    private int numberSpeedModsSent;
    private static boolean hasCreated;
    private int spamSeconds;
    private static final long snipeTime = 900000L;
    private static int muteMsgRed;
    private static int muteMsgGreen;
    private static int muteMsgBlue;
    private boolean receivedBridgeChange;
    private long newBridgeId;
    public String macAddr;
    private boolean sentFirstWarning;
    private boolean sentSecondWarning;
    long lastTicked;
    
    public final void resetCounters() {
        final float diffMod = (System.currentTimeMillis() - this.lastTicked) / 1000.0f;
        this.lastTicked = System.currentTimeMillis();
        this.setAvailableMoves((int)(25.0f * diffMod));
        this.messagesReceived = 0;
        if (this.commandsThisSecond > 50) {
            logWarn(this.player.getName() + " number of commands sent:" + this.commandsThisSecond);
        }
        if (this.numberSpeedModsSent > 10) {
            logWarn(this.player.getName() + " number of speedmods sent:" + this.numberSpeedModsSent);
        }
        this.numberSpeedModsSent = 0;
        if (this.commandsThisSecond > 10) {
            ++this.spamSeconds;
        }
        else {
            --this.spamSeconds;
        }
        if (this.spamSeconds >= 3 && this.player != null) {
            logInfo(this.player.getName() + " SPAMMING " + this.commandsThisSecond + ". This is second " + this.spamSeconds + ". Disconnecting.");
            this.player.logoutIn(5, "Spamming the server.");
        }
        this.commandsThisSecond = 0;
        Communicator.numcommands = 0;
        Communicator.lastcommand = 0;
        Communicator.prevcommand = 0;
        Communicator.commandAction = 0L;
        Communicator.commandMessage = "";
    }
    
    public void tickSecond() {
        this.resetCounters();
        if (this.shouldSendTradeAgree) {
            this.reallySendTradeAgree();
        }
        this.checkSendWeather();
        this.player.getMovementScheme().setErrors(0);
    }
    
    public Communicator(final Player aPlayer, final SocketConnection conn) {
        this.ready = false;
        this.justLoggedIn = true;
        this.ticker = null;
        this.messagesReceived = 0;
        this.timeMod = 0L;
        this.lastToggledPA = 0L;
        this.shouldSendTradeAgree = false;
        this.tradeAgreeToSend = false;
        this.reportedIp = new byte[] { 0, 0, 0, 0 };
        this.setCheatedIp = false;
        this.df = DateFormat.getTimeInstance();
        this.newSeed = (Server.rand.nextInt() & Integer.MAX_VALUE);
        this.newSeedPointer = 0;
        this.invulnerable = true;
        this.currentmove = null;
        this.availableMoves = 0;
        this.newHeightOffset = -10000;
        this.changeHeightImmediately = false;
        this.setWeather = false;
        this.moves = 0;
        this.receivedTicks = false;
        this.lastLayer = 0;
        this.subjectIds = null;
        this.coins = null;
        this.subjectItem = null;
        this.shouldSendWeather = false;
        this.commandsThisSecond = 0;
        this.wroteip = 0;
        this.lastChangedEmail = 0L;
        this.lastCounts = 0;
        this.numberSpeedModsSent = 0;
        this.spamSeconds = 0;
        this.receivedBridgeChange = false;
        this.newBridgeId = -10L;
        this.macAddr = "Not reported";
        this.lastTicked = System.currentTimeMillis();
        this.player = aPlayer;
        this.connection = conn;
        this.ticker = aPlayer.getMovementScheme();
        if (aPlayer.isFighting()) {
            this.invulnerable = false;
        }
    }
    
    Communicator() {
        this.ready = false;
        this.justLoggedIn = true;
        this.ticker = null;
        this.messagesReceived = 0;
        this.timeMod = 0L;
        this.lastToggledPA = 0L;
        this.shouldSendTradeAgree = false;
        this.tradeAgreeToSend = false;
        this.reportedIp = new byte[] { 0, 0, 0, 0 };
        this.setCheatedIp = false;
        this.df = DateFormat.getTimeInstance();
        this.newSeed = (Server.rand.nextInt() & Integer.MAX_VALUE);
        this.newSeedPointer = 0;
        this.invulnerable = true;
        this.currentmove = null;
        this.availableMoves = 0;
        this.newHeightOffset = -10000;
        this.changeHeightImmediately = false;
        this.setWeather = false;
        this.moves = 0;
        this.receivedTicks = false;
        this.lastLayer = 0;
        this.subjectIds = null;
        this.coins = null;
        this.subjectItem = null;
        this.shouldSendWeather = false;
        this.commandsThisSecond = 0;
        this.wroteip = 0;
        this.lastChangedEmail = 0L;
        this.lastCounts = 0;
        this.numberSpeedModsSent = 0;
        this.spamSeconds = 0;
        this.receivedBridgeChange = false;
        this.newBridgeId = -10L;
        this.macAddr = "Not reported";
        this.lastTicked = System.currentTimeMillis();
        this.player = null;
        this.connection = null;
    }
    
    public SocketConnection getConnection() {
        return this.connection;
    }
    
    public final void resetConnection() {
        this.connection = null;
    }
    
    public final void resetTicker() {
        this.ticker = null;
    }
    
    public void setReady(final boolean aReady) {
        this.ready = aReady;
    }
    
    public void disconnect() {
        if (this.connection != null) {
            try {
                this.connection.disconnect();
                this.connection.closeChannel();
                if (this.player != null) {
                    this.player.setLink(false);
                }
                this.ticker = null;
                if (this.currentmove != null) {
                    this.currentmove.clear(false, this.ticker, this.player, null);
                }
                this.currentmove = null;
            }
            catch (Exception ex) {
                logInfo("Problem while disconnecting player: " + this.player + " - " + ex.getMessage(), ex);
            }
        }
    }
    
    protected ByteBuffer getBuffer() {
        return this.connection.getBuffer();
    }
    
    protected void flushConnectionOrAddToQueue(final ByteBuffer aByteBuffer) throws IOException {
        this.connection.flush();
    }
    
    private void movePlayer() {
        if (this.currentmove == null) {
            --this.moves;
            return;
        }
        --this.moves;
        this.currentmove.setHandled(true);
        if (!this.player.isDead()) {
            try {
                if (!this.player.isTeleporting() && !this.player.getMovementScheme().isIntraTeleporting() && !this.player.isTransferring()) {
                    final float oldPosX = this.player.getStatus().getPositionX();
                    final float oldPosY = this.player.getStatus().getPositionY();
                    final float oldPosZ = this.player.getStatus().getPositionZ();
                    final float oldRot = this.player.getStatus().getRotation();
                    if ((this.player.vehicle == -10L || this.player.isVehicleCommander()) && (oldPosX != this.currentmove.getNewPosX() || oldPosY != this.currentmove.getNewPosY() || oldPosZ != this.currentmove.getNewPosZ())) {
                        if (this.currentmove.getNewPosX() <= 0.0f || this.currentmove.getNewPosY() >= Communicator.maxMapSize || this.currentmove.getNewPosY() <= 0.0f || this.currentmove.getNewPosX() >= Communicator.maxMapSize) {
                            this.player.getStatus().getPosition().setBridgeId(-10L);
                            this.player.getMovementScheme().setBridgeId(-10L);
                            this.player.intraTeleport(oldPosX, oldPosY, oldPosZ, oldRot, this.lastLayer, "out of bounds: " + this.currentmove.getNewPosX() + ',' + this.currentmove.getNewPosY() + " vehicle=" + this.player.vehicle + " commander=" + this.player.isVehicleCommander());
                            this.player.setKickedOffBoat(true);
                            return;
                        }
                        final int oldTileX = (int)oldPosX >> 2;
                        final int oldTileY = (int)oldPosY >> 2;
                        final int newTileX = (int)this.currentmove.getNewPosX() >> 2;
                        final int newTileY = (int)this.currentmove.getNewPosY() >> 2;
                        final int diffTileX = newTileX - oldTileX;
                        final int diffTileY = newTileY - oldTileY;
                        if (diffTileX != 0 || diffTileY != 0) {
                            if ((Math.abs(diffTileX) > 1 || Math.abs(diffTileY) > 1) && !this.justLoggedIn) {
                                this.player.intraTeleport(oldPosX, oldPosY, oldPosZ, oldRot, this.lastLayer, "many tiles - x:" + diffTileX + ", y:" + diffTileY + " nposx=" + this.currentmove.getNewPosX() + ", nposy=" + this.currentmove.getNewPosY());
                                return;
                            }
                            boolean nextToSurface = this.lastLayer < 0;
                            if (nextToSurface) {
                                nextToSurface = false;
                                if (this.player.getMovementScheme().getTextureForTile(oldTileX, oldTileY, this.lastLayer, -10L) == Tiles.Tile.TILE_CAVE_EXIT.id && Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.getTile(diffTileX + oldTileX, diffTileY + oldTileY)))) {
                                    nextToSurface = true;
                                    if (this.currentmove.getLayer() >= 0 && this.player.getLayer() < 0 && this.lastLayer < 0) {
                                        this.player.intraTeleport(oldPosX, oldPosY, oldPosZ, oldRot, this.lastLayer, "illegal exit");
                                        return;
                                    }
                                }
                            }
                            if (nextToSurface) {
                                final BlockingResult result = Blocking.getBlockerBetween(this.getPlayer(), oldPosX, oldPosY, this.currentmove.getNewPosX(), this.currentmove.getNewPosY(), this.player.getPositionZ(), Zones.calculateHeight(this.currentmove.getNewPosX(), this.currentmove.getNewPosY(), true), true, true, false, 6, -1L, this.player.getBridgeId(), this.player.getBridgeId(), true);
                                if (result != null) {
                                    this.player.intraTeleport(oldPosX, oldPosY, oldPosZ, oldRot, this.lastLayer, "blocked by " + result.getFirstBlocker().getName());
                                    return;
                                }
                            }
                            if (!this.player.getMovementScheme().isFlying() || this.player.getPower() <= 1) {
                                final BlockingResult result = Blocking.getBlockerBetween(this.getPlayer(), 2 + oldTileX * 4, 2 + oldTileY * 4, this.currentmove.getNewPosX(), this.currentmove.getNewPosY(), this.player.getPositionZ(), this.player.getPositionZ(), this.player.isOnSurface(), this.player.isOnSurface(), false, 6, -1L, this.player.getBridgeId(), this.player.getBridgeId(), this.player.followsGround());
                                if (result != null) {
                                    this.player.intraTeleport(oldPosX, oldPosY, oldPosZ, oldRot, this.lastLayer, "blocked by " + result.getFirstBlocker().getName());
                                    return;
                                }
                            }
                            if (this.checkLegalTileMove(newTileX, newTileY, diffTileX, diffTileY)) {
                                if (!this.player.isDead()) {
                                    if (this.player.getVehicle() == -10L) {
                                        this.player.transferCounter = 30;
                                    }
                                    if (!this.player.isTransferring()) {
                                        this.player.intraTeleport(oldPosX, oldPosY, oldPosZ, oldRot, this.lastLayer, "at transfer border");
                                        this.player.setKickedOffBoat(true);
                                    }
                                }
                                return;
                            }
                        }
                        final float diffX = this.currentmove.getNewPosX() - oldPosX;
                        final float diffY = this.currentmove.getNewPosY() - oldPosY;
                        final float diffZ = this.currentmove.getNewPosZ() - oldPosZ;
                        this.player.getStatus().setRotation(this.currentmove.getNewRot());
                        if (Math.abs(diffZ) > 10.0f) {
                            if (diffX != 0.0f || diffY != 0.0f) {
                                if (diffTileX == 0 && diffTileY == 0) {
                                    final boolean setvisible = this.player.isVisible();
                                    this.player.setVisible(false);
                                    this.player.getStatus().setPositionX(this.currentmove.getNewPosX());
                                    this.player.getStatus().setPositionY(this.currentmove.getNewPosY());
                                    this.player.getStatus().setPositionZ(this.currentmove.getNewPosZ());
                                    if (setvisible) {
                                        this.player.setVisible(true);
                                    }
                                    return;
                                }
                                this.player.destroyVisionArea();
                                try {
                                    final Zone z = Zones.getZone(this.player.getTileX(), this.player.getTileY(), this.player.isOnSurface());
                                    z.deleteCreature(this.player, true);
                                }
                                catch (NoSuchZoneException nsz) {
                                    this.sendAlertServerMessage("You are out of bounds. Disconnecting in 5.");
                                    this.player.logoutIn(5, "You were out of bounds.");
                                    return;
                                }
                                this.player.getStatus().setPositionX(this.currentmove.getNewPosX());
                                this.player.getStatus().setPositionY(this.currentmove.getNewPosY());
                                this.player.getStatus().setPositionZ(this.currentmove.getNewPosZ());
                                this.player.createVisionArea();
                                this.player.getVisionArea().getSurface().initialize();
                                this.player.getVisionArea().getUnderGround().initialize();
                                try {
                                    final Zone z = Zones.getZone(this.player.getTileX(), this.player.getTileY(), this.player.isOnSurface());
                                    z.addCreature(this.player.getWurmId());
                                    return;
                                }
                                catch (NoSuchZoneException nsz) {
                                    this.sendAlertServerMessage("You are out of bounds. Disconnecting in 5.");
                                    this.player.logoutIn(5, "You were out of bounds.");
                                    return;
                                }
                            }
                            final boolean setvisible = this.player.isVisible();
                            this.player.setVisible(false);
                            this.player.getStatus().setPositionX(this.currentmove.getNewPosX());
                            this.player.getStatus().setPositionY(this.currentmove.getNewPosY());
                            this.player.getStatus().setPositionZ(this.currentmove.getNewPosZ());
                            if (setvisible) {
                                this.player.setVisible(true);
                            }
                        }
                        else {
                            this.player.getStatus().setPositionX(this.currentmove.getNewPosX());
                            this.player.getStatus().setPositionY(this.currentmove.getNewPosY());
                            this.player.getStatus().setPositionZ(this.currentmove.getNewPosZ());
                            this.player.moved(diffX, diffY, diffZ, diffTileX, diffTileY);
                        }
                    }
                    else if (this.currentmove.getNewRot() != oldRot) {
                        this.player.getStatus().setRotation(this.currentmove.getNewRot());
                        this.player.moved(0.0f, 0.0f, 0.0f, 0, 0);
                    }
                }
            }
            catch (Exception ex) {}
        }
    }
    
    private boolean checkMoveChanges() {
        return CommuincatorMoveChangeChecker.checkMoveChanges(this.currentmove, this.ticker, this.player, null);
    }
    
    public static void attemptMuting(final Player player, final PlayerInfo pinf, final boolean global, final boolean ignoring) {
        boolean mute = false;
        if (player.isPaying() && System.currentTimeMillis() > player.getSaveFile().nextAvailableMute) {
            int hours = 1;
            if (System.currentTimeMillis() - player.getSaveFile().nextAvailableMute < 1800000L) {
                hours = 2;
            }
            if (System.currentTimeMillis() - player.getSaveFile().nextAvailableMute < 900000L) {
                hours = 3;
            }
            player.getSaveFile().nextAvailableMute = System.currentTimeMillis() + hours * 900000L + 900000L;
            mute = true;
            if (!global) {
                attemptMuting(player.getKingdomId(), pinf);
            }
        }
        if (global) {
            final WcGlobalIgnore wgi = new WcGlobalIgnore(WurmId.getNextWCCommandId(), player.getWurmId(), player.getName(), pinf.wurmId, pinf.getName(), false, mute, false, ignoring, false, player.getKingdomId());
            if (Servers.isThisLoginServer()) {
                wgi.sendToServer(pinf.getCurrentServer());
            }
            else {
                wgi.sendToLoginServer();
            }
        }
    }
    
    public static void attemptMuting(final byte ignorerKingdom, final PlayerInfo pinf) {
        try {
            final Player online = Players.getInstance().getPlayer(pinf.wurmId);
            if (online.getKingdomId() == ignorerKingdom && online.getPower() <= 1 && !online.mayMute() && !pinf.isMute() && (online.isActiveInChat() || online.isActiveInLocalChat())) {
                if (System.currentTimeMillis() - pinf.startedReceivingMutes > 180000L) {
                    pinf.startedReceivingMutes = System.currentTimeMillis();
                    pinf.mutesReceived = 1;
                }
                else {
                    ++pinf.mutesReceived;
                }
                final int sameKingdom = Players.getInstance().getOnlinePlayersFromKingdom(ignorerKingdom);
                final int required = Math.max(5, (sameKingdom - 1) / 10);
                if (pinf.mutesReceived > required) {
                    final Message mess = new Message(null, (byte)3, ":Event", "Hi! You have received a one hour automated mute because a lot of people decided to ignore you at the same time. This system is new and under development so please don't take it too seriously.");
                    mess.setReceiver(online.getWurmId());
                    Server.getInstance().addMessage(mess);
                    online.mute(true, "One hour automated system mute by public opinion.", System.currentTimeMillis() + 3600000L);
                    final Message mess2 = new Message(null, (byte)3, ":Event", "We understand that it may be frustrating and the reasons may not be that you misbehaved. That this system is used or abused for various reasons is expected at this point as people figure how it works.");
                    mess2.setReceiver(online.getWurmId());
                    Server.getInstance().addMessage(mess2);
                    final Message mess3 = new Message(null, (byte)3, ":Event", "Eventually we hope that the novelty of this system should go away and people will start ignoring only when they have good reason to. If we are wrong we will have to remove this feature.");
                    mess3.setReceiver(online.getWurmId());
                    Server.getInstance().addMessage(mess3);
                    Players.addMgmtMessage("Opinion", "mutes " + pinf.getName() + " for 1 hour.");
                    final Message messM = new Message(online, (byte)9, "MGMT", "<" + online.getName() + "> is muted 1 hour by public opinion.", Communicator.muteMsgRed, Communicator.muteMsgGreen, Communicator.muteMsgBlue);
                    Server.getInstance().addMessage(messM);
                }
                else if (pinf.mutesReceived >= required * 0.75) {
                    Players.addMgmtMessage("Opinion", "has reached 75% for muting " + pinf.getName() + " for 1 hour.");
                    final Message mess = new Message(online, (byte)9, "MGMT", "<" + online.getName() + "> has reached 75% of being muted 1 hour by public opinion.", Communicator.muteMsgRed, Communicator.muteMsgGreen, Communicator.muteMsgBlue);
                    Server.getInstance().addMessage(mess);
                }
            }
        }
        catch (NoSuchPlayerException ex) {}
    }
    
    public boolean pollNextMove() {
        if (this.ticker == null) {
            return false;
        }
        if (this.currentmove != null) {
            this.checkMoveChanges();
            if ((this.currentmove.getBm() & 0xF) > 0 && this.invulnerable) {
                this.setInvulnerable(false);
                this.sendNormalServerMessage("You are no longer invulnerable.");
            }
            boolean moved = false;
            if (!this.currentmove.isHandled()) {
                moved = true;
                try {
                    this.lastLayer = this.player.getLayer();
                    if (this.player.vehicle == -10L || this.player.isVehicleCommander()) {
                        if (this.player.isVehicleCommander()) {
                            float maxDepth = -2500.0f;
                            float maxHeight = 2500.0f;
                            float maxDiff = 2000.0f;
                            final Vehicle vehic = Vehicles.getVehicleForId(this.player.getVehicle());
                            if (vehic != null) {
                                maxDepth = vehic.maxDepth;
                                maxHeight = vehic.maxHeight;
                                maxDiff = vehic.maxHeightDiff;
                            }
                            this.ticker.movestep(maxDiff, this.currentmove.getNewPosX(), this.currentmove.getNewPosZ(), this.currentmove.getNewPosY(), maxDepth, maxHeight, this.currentmove.getNewRot(), this.currentmove.getBm(), this.currentmove.getLayer());
                        }
                        else {
                            this.ticker.movestep(2000.0f, this.currentmove.getNewPosX(), this.currentmove.getNewPosZ(), this.currentmove.getNewPosY(), -2500.0f, 2500.0f, this.currentmove.getNewRot(), this.currentmove.getBm(), this.currentmove.getLayer());
                        }
                        if (!this.ticker.isAborted()) {
                            this.movePlayer();
                        }
                    }
                    else {
                        this.movePlayer();
                    }
                }
                catch (Exception e) {
                    if (this.currentmove != null) {
                        this.currentmove.setHandled(true);
                    }
                    --this.moves;
                    float lOldPosX = this.player.getStatus().getPositionX();
                    float lOldPosY = this.player.getStatus().getPositionY();
                    final float lOldPosZ = this.player.getStatus().getPositionZ();
                    final float lOldRot = this.player.getStatus().getRotation();
                    boolean iteleport = true;
                    if (lOldPosX < 0.0f) {
                        lOldPosX = 2.0f;
                        iteleport = false;
                    }
                    if (lOldPosY < 0.0f) {
                        lOldPosY = 2.0f;
                        iteleport = false;
                    }
                    if (lOldPosY > Zones.worldMeterSizeY) {
                        lOldPosY = Zones.worldMeterSizeY - 2.0f;
                        iteleport = false;
                    }
                    if (lOldPosX > Zones.worldMeterSizeX) {
                        lOldPosX = Zones.worldMeterSizeX - 2.0f;
                        iteleport = false;
                    }
                    CommuincatorMoveChangeChecker.checkWeather(this.currentmove, this.ticker);
                    CommuincatorMoveChangeChecker.checkWindMod(this.currentmove, this.player, Communicator.logger);
                    CommuincatorMoveChangeChecker.checkMountSpeed(this.currentmove, this.player, Communicator.logger);
                    CommuincatorMoveChangeChecker.checkHeightOffsetChanged(this.currentmove, this.player);
                    if (iteleport) {
                        this.player.intraTeleport(lOldPosX, lOldPosY, lOldPosZ, lOldRot, this.lastLayer, "failed move");
                    }
                    else {
                        this.player.setTeleportPoints(lOldPosX, lOldPosY, this.lastLayer, 0);
                        this.player.startTeleporting();
                        CommuincatorMoveChangeChecker.checkClimb(this.currentmove, this.ticker);
                        this.sendNormalServerMessage("You feel a slight tingle in your spine.");
                        this.sendTeleport(false);
                    }
                    return false;
                }
            }
            if (this.currentmove != null) {
                if (this.currentmove.getSameMoves() > 0) {
                    moved = true;
                    if (this.player.vehicle == -10L || this.player.isVehicleCommander()) {
                        if (this.player.isVehicleCommander()) {
                            float maxDepth = -2500.0f;
                            float maxHeight = 2500.0f;
                            float maxDiff = 2000.0f;
                            final Vehicle vehic = Vehicles.getVehicleForId(this.player.getVehicle());
                            if (vehic != null) {
                                maxDepth = vehic.maxDepth;
                                maxHeight = vehic.maxHeight;
                                maxDiff = vehic.maxHeightDiff;
                            }
                            for (int x = 0; x < this.currentmove.getSameMoves(); ++x) {
                                --this.moves;
                                this.ticker.movestep(maxDiff, this.currentmove.getNewPosX(), this.currentmove.getNewPosZ(), this.currentmove.getNewPosY(), maxDepth, maxHeight, this.currentmove.getNewRot(), this.currentmove.getBm(), this.currentmove.getLayer());
                                if (this.currentmove == null) {
                                    return true;
                                }
                            }
                        }
                        else {
                            for (int x2 = 0; x2 < this.currentmove.getSameMoves(); ++x2) {
                                --this.moves;
                                this.ticker.movestep(2000.0f, this.currentmove.getNewPosX(), this.currentmove.getNewPosZ(), this.currentmove.getNewPosY(), -2500.0f, 2500.0f, this.currentmove.getNewRot(), this.currentmove.getBm(), this.currentmove.getLayer());
                                if (this.currentmove == null) {
                                    return true;
                                }
                            }
                        }
                    }
                    this.currentmove.resetSameMoves();
                    this.currentmove.setHandled(true);
                }
                this.setNextMove();
            }
            return moved;
        }
        this.checkMoveChanges();
        return false;
    }
    
    private boolean isVillageInviteMessage(final String message) {
        final String[] parts = message.split(" ");
        return parts.length > 0 && (parts[0].equalsIgnoreCase("/vinvite") || parts[0].equalsIgnoreCase("/villageinvite"));
    }
    
    private boolean isChangingServerNorth(final int aNewTileX, final int aNewTileY) {
        if (aNewTileY < 40 && aNewTileX >= 0 && aNewTileX <= Communicator.serverBorderEnd) {
            final ServerEntry entry = Servers.getDestinationFor(this.player);
            if (entry != Servers.localServer) {
                this.lastTargetServerChecked = entry;
                return true;
            }
            if (Servers.localServer.serverNorth != null && Servers.localServer.serverNorth.isAvailable(this.player.getPower(), this.player.isReallyPaying())) {
                if (!Servers.mayEnterServer(this.player, Servers.localServer.serverNorth)) {
                    return false;
                }
                this.lastTargetServerChecked = Servers.localServer.serverNorth;
                return true;
            }
        }
        return false;
    }
    
    private boolean isChangingServerSouth(final int aNewTileX, final int aNewTileY) {
        if (aNewTileY > Communicator.firstBorder && aNewTileX >= 0 && aNewTileX <= Communicator.serverBorderEnd) {
            final ServerEntry entry = Servers.getDestinationFor(this.player);
            if (entry != Servers.localServer) {
                this.lastTargetServerChecked = entry;
                return true;
            }
            if (Servers.localServer.serverSouth != null && Servers.localServer.serverSouth.isAvailable(this.player.getPower(), this.player.isReallyPaying())) {
                if (!Servers.mayEnterServer(this.player, Servers.localServer.serverSouth)) {
                    return false;
                }
                this.lastTargetServerChecked = Servers.localServer.serverSouth;
                return true;
            }
        }
        return false;
    }
    
    private boolean isChangingServerWest(final int aNewTileX, final int aNewTileY) {
        if (aNewTileX < 40 && aNewTileY >= 0 && aNewTileY <= Communicator.serverBorderEnd) {
            final ServerEntry entry = Servers.getDestinationFor(this.player);
            if (entry != Servers.localServer) {
                this.lastTargetServerChecked = entry;
                return true;
            }
            if (Servers.localServer.serverWest != null && Servers.localServer.serverWest.isAvailable(this.player.getPower(), this.player.isReallyPaying())) {
                if (!Servers.mayEnterServer(this.player, Servers.localServer.serverWest)) {
                    return false;
                }
                this.lastTargetServerChecked = Servers.localServer.serverWest;
                return true;
            }
        }
        return false;
    }
    
    private boolean isChangingServerEast(final int aNewTileX, final int aNewTileY) {
        if (aNewTileX > Communicator.firstBorder && aNewTileY >= 0 && aNewTileY <= Communicator.serverBorderEnd) {
            final ServerEntry entry = Servers.getDestinationFor(this.player);
            if (entry != Servers.localServer) {
                this.lastTargetServerChecked = entry;
                return true;
            }
            if (Servers.localServer.serverEast != null && Servers.localServer.serverEast.isAvailable(this.player.getPower(), this.player.isReallyPaying())) {
                if (!Servers.mayEnterServer(this.player, Servers.localServer.serverEast)) {
                    return false;
                }
                this.lastTargetServerChecked = Servers.localServer.serverEast;
                return true;
            }
        }
        return false;
    }
    
    private boolean mayLeaveWithVehicle() {
        if (!Servers.localServer.PVPSERVER) {
            final Vehicle vehic = Vehicles.getVehicleForId(this.player.getVehicle());
            if (vehic != null) {
                try {
                    final Item ivehic = Items.getItem(this.player.getVehicle());
                    if (ivehic.getLockId() == -10L) {
                        return true;
                    }
                    if (!ivehic.isGuest(this.player) || !ivehic.mayCommand(this.player)) {
                        this.player.getCommunicator().sendNormalServerMessage("You may not leave the server with this boat. You need to be explicitly specified in the boat's permissions.");
                        return false;
                    }
                    for (final Seat seat : vehic.getSeats()) {
                        if (seat.isOccupied() && seat.type == 1 && !ivehic.isGuest(seat.getOccupant())) {
                            try {
                                final Creature c = Server.getInstance().getCreature(seat.occupant);
                                if (!ivehic.mayPassenger(c)) {
                                    this.player.getCommunicator().sendNormalServerMessage("You may not leave the server with this boat as one of your passengers will not have passenger permission on new server.");
                                    return false;
                                }
                            }
                            catch (NoSuchCreatureException ex) {}
                            catch (NoSuchPlayerException ex2) {}
                        }
                    }
                }
                catch (NoSuchItemException ex3) {}
            }
        }
        return true;
    }
    
    private boolean isChangingServer(final int aNewTileX, final int aNewTileY) {
        return !this.player.wasKickedOffBoat() && this.mayLeaveWithVehicle() && (this.isChangingServerNorth(aNewTileX, aNewTileY) || this.isChangingServerEast(aNewTileX, aNewTileY) || this.isChangingServerWest(aNewTileX, aNewTileY) || this.isChangingServerSouth(aNewTileX, aNewTileY));
    }
    
    private boolean checkVehicle(final int aNewTileX, final int aNewTileY, final boolean leaving) {
        boolean ok = true;
        if (this.player.isVehicleCommander()) {
            final Vehicle vehic = Vehicles.getVehicleForId(this.player.getVehicle());
            if (vehic != null) {
                ServerEntry targetserver = Servers.localServer;
                if (vehic.hasDestinationSet()) {
                    targetserver = vehic.getDestinationServer();
                }
                else if (this.isChangingServerNorth(aNewTileX, aNewTileY)) {
                    targetserver = Servers.localServer.serverNorth;
                }
                else if (this.isChangingServerEast(aNewTileX, aNewTileY)) {
                    targetserver = Servers.localServer.serverEast;
                }
                else if (this.isChangingServerSouth(aNewTileX, aNewTileY)) {
                    targetserver = Servers.localServer.serverSouth;
                }
                else if (this.isChangingServerWest(aNewTileX, aNewTileY)) {
                    targetserver = Servers.localServer.serverWest;
                }
                if (!Servers.mayEnterServer(this.player, targetserver)) {
                    if (leaving) {
                        this.player.getCommunicator().sendAlertServerMessage("You may not enter that server now.");
                        this.player.disembark(true);
                        this.player.setKickedOffBoat(true);
                    }
                    return false;
                }
                if (!Servers.isThisAnEpicOrChallengeServer() && targetserver.PVPSERVER && vehic.hasDestinationSet() && vehic.isPvPBlocking()) {
                    this.player.getCommunicator().sendAlertServerMessage("You or a passenger has PvP travel blocked. This option may be toggled in the profile.", (byte)2);
                    if (leaving) {
                        this.player.disembark(true);
                        this.player.setKickedOffBoat(true);
                    }
                    return false;
                }
                if (!targetserver.PVPSERVER && targetserver.HOMESERVER && (!Servers.localServer.EPIC || Server.getInstance().isPS())) {
                    vehic.alertPassengersOfKingdom(targetserver, true);
                }
                else if (targetserver.PVPSERVER && !targetserver.HOMESERVER && (!Servers.localServer.EPIC || Server.getInstance().isPS())) {
                    vehic.alertPassengersOfKingdom(targetserver, true);
                    vehic.alertAllPassengersOfEnemies(targetserver);
                }
                if (!vehic.creature && !Servers.localServer.PVPSERVER && !vehic.checkPassengerPermissions(this.player)) {
                    if (leaving) {
                        this.player.disembark(true);
                        this.player.setKickedOffBoat(true);
                    }
                    return false;
                }
                if (vehic.seats != null) {
                    for (int x = 0; x < vehic.seats.length; ++x) {
                        if (vehic.seats[x] != null && vehic.seats[x].occupant > 0L && vehic.seats[x].occupant != this.player.getWurmId()) {
                            try {
                                final Creature c = Server.getInstance().getCreature(vehic.seats[x].occupant);
                                if (c.isPlayer()) {
                                    if (!leaving) {
                                        PlayerTransfer.willItemsTransfer((Player)c, false, targetserver.id);
                                    }
                                    if (!Servers.mayEnterServer(c, targetserver)) {
                                        if (!leaving) {
                                            c.getCommunicator().sendAlertServerMessage("You can not leave in that direction and will end up in the water!");
                                            this.sendAlertServerMessage(c.getName() + " may not leave in that direction and will end up in the water!");
                                        }
                                        else {
                                            c.disembark(true);
                                            c.setKickedOffBoat(true);
                                        }
                                    }
                                }
                            }
                            catch (Exception ex) {}
                        }
                    }
                }
                if (!vehic.creature) {
                    try {
                        final Item ivehic = Items.getItem(vehic.wurmid);
                        final Item[] items = ivehic.getAllItems(true);
                        for (int x2 = 0; x2 < items.length; ++x2) {
                            if (!items[x2].willLeaveServer(false, false, this.player.getPower() > 0)) {
                                this.sendAlertServerMessage("The " + items[x2].getName() + " in the " + ivehic.getName() + " will not leave the server.");
                            }
                        }
                    }
                    catch (NoSuchItemException ex2) {}
                }
            }
            else {
                ok = false;
            }
        }
        return ok;
    }
    
    private String createDraggedTransferData(final ServerEntry targetserver, final Item dragged, final int aNewTileX, final int aNewTileY, final LinkedList<Creature> passengers) {
        final Item[] allItems;
        final Item[] items = allItems = dragged.getAllItems(true);
        for (final Item item : allItems) {
            if (!item.willLeaveServer(true, false, this.player.getPower() > 0)) {
                this.sendAlertServerMessage("The " + item.getName() + " in the " + dragged.getName() + " will not leave the server.");
            }
        }
        final LoginServerWebConnection lsw = new LoginServerWebConnection(targetserver.id);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
        try {
            final DataOutputStream dos2 = new DataOutputStream(bos2);
            final Vehicle vehic = Vehicles.getVehicleForId(dragged.getWurmId());
            if (vehic != null) {
                dos2.writeInt(passengers.size());
                for (final Creature c : passengers) {
                    dos2.writeLong(c.getWurmId());
                    dos2.writeInt(vehic.getSeatPosForPassenger(c.getWurmId()));
                }
            }
            else {
                dos2.writeInt(0);
            }
            dos2.flush();
            dos2.close();
            this.passengerData = bos2.toByteArray();
            final DataOutputStream dos3 = new DataOutputStream(bos);
            dos3.writeInt(items.length + 1);
            PlayerTransfer.sendItem(dragged, dos3, false);
            for (int x = 0; x < items.length; ++x) {
                PlayerTransfer.sendItem(items[x], dos3, false);
            }
            dos3.flush();
            dos3.close();
            this.vehicleData = bos.toByteArray();
            final String result = lsw.sendVehicle(this.passengerData, this.vehicleData, this.player.getWurmId(), dragged.getWurmId(), targetserver.id, aNewTileX, aNewTileY, 0, dragged.getRotation());
            if (result.isEmpty()) {
                for (final Item item2 : items) {
                    if (!item2.isTransferred()) {
                        Items.destroyItem(item2.getWurmId(), false, true);
                    }
                }
                Items.destroyItem(dragged.getWurmId());
                this.sendSafeServerMessage("You drift away with the " + dragged.getName() + '.');
                if (!dragged.isTransferred()) {
                    Items.destroyItem(dragged.getWurmId(), false, true);
                }
            }
            return result;
        }
        catch (IOException iox) {
            return "Error: " + iox.getMessage();
        }
    }
    
    private String createVehicleTransferData(final ServerEntry targetserver, final int aNewTileX, final int aNewTileY, final LinkedList<Creature> passengers) {
        final Vehicle vehic = Vehicles.getVehicleForId(this.player.getVehicle());
        if (vehic != null) {
            if (!vehic.creature) {
                try {
                    final Item ivehic = Items.getItem(vehic.wurmid);
                    final Item[] items = ivehic.getAllItems(true);
                    for (int x = 0; x < items.length; ++x) {
                        if (!items[x].willLeaveServer(true, false, this.player.getPower() > 0)) {
                            this.sendAlertServerMessage("The " + items[x].getName() + " in the " + ivehic.getName() + " will not leave the server.");
                        }
                    }
                    boolean anchored = false;
                    Item anchor = null;
                    Item keepnet = null;
                    Item boatLock = null;
                    if (ivehic.isBoat()) {
                        if (ivehic.getData() != -1L) {
                            try {
                                anchor = Items.getItem(ivehic.getData());
                                anchor.setTransferred(false);
                                anchored = true;
                            }
                            catch (NoSuchItemException ex2) {}
                        }
                        if (ivehic.getExtra() != -1L) {
                            try {
                                keepnet = Items.getItem(ivehic.getExtra());
                                keepnet.setTransferred(false);
                                logInfo(this.player.getName() + " has a keepnet.");
                            }
                            catch (NoSuchItemException ex3) {}
                        }
                        if (ivehic.getLockId() != -10L) {
                            try {
                                boatLock = Items.getItem(ivehic.getLockId());
                                boatLock.setTransferred(false);
                            }
                            catch (NoSuchItemException ex4) {}
                        }
                    }
                    final LoginServerWebConnection lsw = new LoginServerWebConnection(targetserver.id);
                    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    final ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
                    try {
                        final DataOutputStream dos2 = new DataOutputStream(bos2);
                        ivehic.setLastOwnerId(this.player.getWurmId());
                        dos2.writeInt(passengers.size());
                        for (final Creature c : passengers) {
                            dos2.writeLong(c.getWurmId());
                            dos2.writeInt(vehic.getSeatPosForPassenger(c.getWurmId()));
                        }
                        dos2.flush();
                        dos2.close();
                        this.passengerData = bos2.toByteArray();
                        final DataOutputStream dos3 = new DataOutputStream(bos);
                        final Set<Item> itemsToSend = new HashSet<Item>();
                        itemsToSend.add(ivehic);
                        if (anchored) {
                            itemsToSend.add(anchor);
                        }
                        if (keepnet != null) {
                            itemsToSend.add(keepnet);
                        }
                        if (boatLock != null) {
                            itemsToSend.add(boatLock);
                        }
                        for (int x2 = 0; x2 < items.length; ++x2) {
                            if (!items[x2].isTransferred() && !items[x2].isArtifact()) {
                                itemsToSend.add(items[x2]);
                            }
                        }
                        dos3.writeInt(itemsToSend.size());
                        for (final Item item : itemsToSend) {
                            PlayerTransfer.sendItem(item, dos3, false);
                        }
                        dos3.flush();
                        dos3.close();
                        this.vehicleData = bos.toByteArray();
                        itemsToSend.clear();
                        final String result = lsw.sendVehicle(this.passengerData, this.vehicleData, this.player.getWurmId(), ivehic.getWurmId(), targetserver.id, aNewTileX, aNewTileY, 0, ivehic.getRotation());
                        logInfo(this.player.getName() + " RECEIVED RESULT " + result);
                        if (result.isEmpty()) {
                            for (int x3 = 0; x3 < items.length; ++x3) {
                                if (!items[x3].isTransferred() && !items[x3].isArtifact()) {
                                    Items.destroyItem(items[x3].getWurmId(), false, true);
                                }
                            }
                            this.sendSafeServerMessage("You drift away on the " + ivehic.getName() + '.');
                            if (!ivehic.isTransferred()) {
                                Items.destroyItem(ivehic.getWurmId(), false, true);
                            }
                        }
                        return result;
                    }
                    catch (IOException iox) {
                        return "Error: " + iox.getMessage();
                    }
                }
                catch (NoSuchItemException ex) {
                    return "Error: " + ex.getMessage();
                }
            }
            return "Creatures don't transfer";
        }
        return "This shouldn't happen, really";
    }
    
    private boolean checkLegalTileMove(final int aNewTileX, final int aNewTileY, final int aDiffTileX, final int aDiffTileY) {
        if ((aNewTileX > 20 && aNewTileX < 30) || (aNewTileY > 20 && aNewTileY < 30) || (aNewTileY > Communicator.firstBorder && aNewTileY < Communicator.secondBorder) || (aNewTileX > Communicator.firstBorder && aNewTileX < Communicator.secondBorder)) {
            boolean send = false;
            if ((aNewTileX > 20 && aNewTileX < 30) || (aNewTileY > 20 && aNewTileY < 30)) {
                if (aDiffTileX < 0 || aDiffTileY < 0) {
                    send = true;
                }
            }
            else if (((aNewTileY > Communicator.firstBorder && aNewTileY < Communicator.secondBorder) || (aNewTileX > Communicator.firstBorder && aNewTileX < Communicator.secondBorder)) && (aDiffTileX > 0 || aDiffTileY > 0)) {
                send = true;
            }
            if (send && (!this.sentFirstWarning || Server.rand.nextInt(20) == 0)) {
                this.sentFirstWarning = true;
                this.sentSecondWarning = false;
                this.player.clearDestination();
                final boolean mayTakeVehicle = this.mayLeaveWithVehicle();
                if (this.isChangingServer(aNewTileX, aNewTileY)) {
                    if (mayTakeVehicle) {
                        this.sendAlertServerMessage("If you keep going in this direction you will end up on " + this.lastTargetServerChecked.getName() + ".");
                        if (this.player.isVehicleCommander() && this.player.getVehicle() != -10L) {
                            final Vehicle boat = Vehicles.getVehicleForId(this.player.getVehicle());
                            boat.notifyAllPassengers("The " + Vehicle.getVehicleName(boat) + " will soon leave this island, and take you to " + this.lastTargetServerChecked.getName() + ".", false, true);
                        }
                    }
                    PlayerTransfer.willItemsTransfer(this.player, false, this.lastTargetServerChecked.getId());
                    this.checkVehicle(aNewTileX, aNewTileY, false);
                }
                else {
                    if (this.player.getVehicle() == -10L) {
                        this.player.sendPopup("Turn around!", "You see dark shadows moving beneath the waves.");
                    }
                    this.sendAlertServerMessage("You see dark shadows moving beneath the waves.");
                }
            }
        }
        else if ((aNewTileX > 1 && aNewTileX <= 20) || (aNewTileY > 1 && aNewTileY <= 20) || (aNewTileY >= Communicator.secondBorder && aNewTileY < Communicator.finalBorder) || (aNewTileX >= Communicator.secondBorder && aNewTileX < Communicator.finalBorder)) {
            this.player.setKickedOffBoat(false);
            boolean send = false;
            if ((aNewTileX > 1 && aNewTileX <= 20) || (aNewTileY > 1 && aNewTileY <= 20)) {
                if (aDiffTileX < 0 || aDiffTileY < 0) {
                    send = true;
                }
            }
            else if (((aNewTileY >= Communicator.secondBorder && aNewTileY < Communicator.finalBorder) || (aNewTileX >= Communicator.secondBorder && aNewTileX < Communicator.finalBorder)) && (aDiffTileX > 0 || aDiffTileY > 0)) {
                send = true;
            }
            final boolean mayTakeVehicle = this.mayLeaveWithVehicle();
            if (aNewTileY == 10 && aDiffTileY < 0) {
                final boolean avail = this.isChangingServerNorth(aNewTileX, aNewTileY) && mayTakeVehicle;
                this.sendAvailServer((byte)0, avail);
            }
            if (aNewTileX == 10 && aDiffTileX < 0) {
                final boolean avail = this.isChangingServerWest(aNewTileX, aNewTileY) && mayTakeVehicle;
                this.sendAvailServer((byte)6, avail);
            }
            if (aNewTileX == Communicator.serverBorderEnd - 10 && aDiffTileX > 0) {
                final boolean avail = this.isChangingServerEast(aNewTileX, aNewTileY) && mayTakeVehicle;
                this.sendAvailServer((byte)2, avail);
            }
            if (aNewTileY == Communicator.serverBorderEnd - 10 && aDiffTileY > 0) {
                final boolean avail = this.isChangingServerSouth(aNewTileX, aNewTileY) && mayTakeVehicle;
                this.sendAvailServer((byte)4, avail);
            }
            if (aNewTileY == 12 && aDiffTileY > 0) {
                this.sendAvailServer((byte)0, true);
            }
            if (aNewTileX == 12 && aDiffTileX > 0) {
                this.sendAvailServer((byte)6, true);
            }
            if (aNewTileX == Communicator.serverBorderEnd - 12 && aDiffTileX < 0) {
                this.sendAvailServer((byte)2, true);
            }
            if (aNewTileY == Communicator.serverBorderEnd - 12 && aDiffTileY < 0) {
                this.sendAvailServer((byte)4, true);
            }
            if (send && (!this.sentSecondWarning || Server.rand.nextInt(10) == 0)) {
                this.sentSecondWarning = true;
                this.sentFirstWarning = false;
                if (this.isChangingServer(aNewTileX, aNewTileY)) {
                    if (mayTakeVehicle) {
                        this.sendAlertServerMessage("You will soon leave this island, and end up on " + this.lastTargetServerChecked.getName() + ".");
                        if (this.player.isVehicleCommander() && this.player.getVehicle() != -10L) {
                            final Vehicle boat = Vehicles.getVehicleForId(this.player.getVehicle());
                            boat.notifyAllPassengers("The " + Vehicle.getVehicleName(boat) + " will soon leave this island, and take you to " + this.lastTargetServerChecked.getName() + ".", false, true);
                        }
                    }
                    PlayerTransfer.willItemsTransfer(this.player, false, 0);
                    this.checkVehicle(aNewTileX, aNewTileY, false);
                }
                else if (this.player.getPositionZ() < 0.0f || this.player.getVehicle() != -10L) {
                    if (this.player.getVehicle() == -10L) {
                        this.player.sendPopup("Turn around!", "An enourmous shark scouts you from below.");
                    }
                    else {
                        this.sendAlertServerMessage("An enourmous shark scouts you from below.");
                    }
                }
            }
        }
        else if (aNewTileX <= 1 || aNewTileY <= 1 || aNewTileY >= Communicator.finalBorder || aNewTileX >= Communicator.finalBorder) {
            if (!this.player.isDead()) {
                if (this.isChangingServer(aNewTileX, aNewTileY)) {
                    if (!this.player.isTransferring()) {
                        boolean ok = true;
                        this.player.getMovementScheme().haltSpeedModifier();
                        ok = this.checkVehicle(aNewTileX, aNewTileY, true);
                        if (ok) {
                            ServerEntry targetserver = Servers.localServer;
                            if ((Features.Feature.BOAT_DESTINATION.isEnabled() || this.player.getPower() >= 2) && this.player.isVehicleCommander()) {
                                final Vehicle boat = Vehicles.getVehicleForId(this.player.getVehicle());
                                if (boat != null) {
                                    if (boat.hasDestinationSet() && (!Servers.localServer.PVPSERVER || (Servers.localServer.PVPSERVER && boat.getPlotCourseCooldowns() <= 0L))) {
                                        targetserver = boat.getDestinationServer();
                                    }
                                }
                                else if (this.player.getDestination() != null) {
                                    targetserver = this.player.getDestination();
                                }
                            }
                            int targetTileX = aNewTileX;
                            int targetTileY = aNewTileY;
                            if (this.isChangingServerNorth(aNewTileX, aNewTileY)) {
                                if (targetserver == Servers.localServer) {
                                    targetserver = Servers.localServer.serverNorth;
                                }
                                targetTileY = (1 << targetserver.meshSize) - 40;
                                if (targetserver.meshSize < Constants.meshSize) {
                                    final int diff = Constants.meshSize - targetserver.meshSize;
                                    if (diff > 1) {
                                        targetTileX /= 4;
                                    }
                                    else {
                                        targetTileX /= 2;
                                    }
                                }
                                else if (targetserver.meshSize > Constants.meshSize) {
                                    final int diff = targetserver.meshSize - Constants.meshSize;
                                    if (diff > 1) {
                                        targetTileX *= 4;
                                    }
                                    else {
                                        targetTileX *= 2;
                                    }
                                }
                                final int maxLimit = (1 << targetserver.meshSize) - 40;
                                targetTileX = Math.min(targetTileX, maxLimit);
                                targetTileX = Math.max(targetTileX, 40);
                            }
                            else if (this.isChangingServerEast(aNewTileX, aNewTileY)) {
                                if (targetserver == Servers.localServer) {
                                    targetserver = Servers.localServer.serverEast;
                                }
                                targetTileX = 40;
                                if (targetserver.meshSize < Constants.meshSize) {
                                    final int diff = Constants.meshSize - targetserver.meshSize;
                                    if (diff > 1) {
                                        targetTileY /= 4;
                                    }
                                    else {
                                        targetTileY /= 2;
                                    }
                                }
                                else if (targetserver.meshSize > Constants.meshSize) {
                                    final int diff = targetserver.meshSize - Constants.meshSize;
                                    if (diff > 1) {
                                        targetTileY *= 4;
                                    }
                                    else {
                                        targetTileY *= 2;
                                    }
                                }
                                final int maxLimit = (1 << targetserver.meshSize) - 40;
                                targetTileY = Math.min(targetTileY, maxLimit);
                                targetTileY = Math.max(targetTileY, 40);
                            }
                            else if (this.isChangingServerSouth(aNewTileX, aNewTileY)) {
                                if (targetserver == Servers.localServer) {
                                    targetserver = Servers.localServer.serverSouth;
                                }
                                targetTileY = 40;
                                if (targetserver.meshSize < Constants.meshSize) {
                                    final int diff = Constants.meshSize - targetserver.meshSize;
                                    if (diff > 1) {
                                        targetTileX /= 4;
                                    }
                                    else {
                                        targetTileX /= 2;
                                    }
                                }
                                else if (targetserver.meshSize > Constants.meshSize) {
                                    final int diff = targetserver.meshSize - Constants.meshSize;
                                    if (diff > 1) {
                                        targetTileX *= 4;
                                    }
                                    else {
                                        targetTileX *= 2;
                                    }
                                }
                                final int maxLimit = (1 << targetserver.meshSize) - 40;
                                targetTileX = Math.min(targetTileX, maxLimit);
                                targetTileX = Math.max(targetTileX, 40);
                            }
                            else if (this.isChangingServerWest(aNewTileX, aNewTileY)) {
                                if (targetserver == Servers.localServer) {
                                    targetserver = Servers.localServer.serverWest;
                                }
                                targetTileX = (1 << targetserver.meshSize) - 40;
                                if (targetserver.meshSize < Constants.meshSize) {
                                    final int diff = Constants.meshSize - targetserver.meshSize;
                                    if (diff > 1) {
                                        targetTileY /= 4;
                                    }
                                    else {
                                        targetTileY /= 2;
                                    }
                                }
                                else if (targetserver.meshSize > Constants.meshSize) {
                                    final int diff = targetserver.meshSize - Constants.meshSize;
                                    if (diff > 1) {
                                        targetTileY *= 4;
                                    }
                                    else {
                                        targetTileY *= 2;
                                    }
                                }
                                final int maxLimit = (1 << targetserver.meshSize) - 40;
                                targetTileY = Math.min(targetTileY, maxLimit);
                                targetTileY = Math.max(targetTileY, 40);
                            }
                            if (targetserver.isAvailable(this.player.getPower(), this.player.isReallyPaying())) {
                                this.sendAlertServerMessage("You head out to sea.");
                                if (this.player.isVehicleCommander() && this.player.getVehicle() != -10L) {
                                    this.player.setDestination(targetserver);
                                    final Vehicle vessel = Vehicles.getVehicleForId(this.player.getVehicle());
                                    if (vessel.hasDestinationSet()) {
                                        Server.getInstance().broadCastAction("The " + Vehicle.getVehicleName(vessel) + " sails off on a course to " + vessel.getDestinationServer().getName() + ".", this.player, 6);
                                    }
                                }
                                final LinkedList<Creature> passengers = new LinkedList<Creature>();
                                if (this.player.isVehicleCommander()) {
                                    final Vehicle vehic = Vehicles.getVehicleForId(this.player.getVehicle());
                                    if (vehic != null && vehic.seats != null) {
                                        for (int x = 0; x < vehic.seats.length; ++x) {
                                            if (vehic.seats[x] != null && vehic.seats[x].occupant > 0L) {
                                                try {
                                                    final Creature c = Server.getInstance().getCreature(vehic.seats[x].occupant);
                                                    if (Servers.mayEnterServer(c, targetserver)) {
                                                        c.setDestination(targetserver);
                                                        passengers.add(c);
                                                    }
                                                    else {
                                                        c.disembark(true);
                                                        c.setKickedOffBoat(true);
                                                    }
                                                }
                                                catch (Exception ex) {
                                                    logWarn(ex.getMessage(), ex);
                                                }
                                            }
                                        }
                                    }
                                    if (this.createVehicleTransferData(targetserver, targetTileX, targetTileY, passengers).isEmpty()) {
                                        for (final Creature c : passengers) {
                                            if (c.getSecondsPlayed() > 900.0f) {
                                                c.achievement(68);
                                            }
                                            c.sendTransfer(Server.getInstance(), targetserver.INTRASERVERADDRESS, Integer.parseInt(targetserver.INTRASERVERPORT), targetserver.INTRASERVERPASSWORD, targetserver.id, targetTileX, targetTileY, true, false, (byte)0);
                                        }
                                    }
                                }
                                else if (this.player.getDraggedItem() != null) {
                                    final Vehicle vehic = Vehicles.getVehicleForId(this.player.getDraggedItem().getWurmId());
                                    if (vehic != null && vehic.seats != null) {
                                        for (int x = 0; x < vehic.seats.length; ++x) {
                                            if (vehic.seats[x] != null && vehic.seats[x].occupant > 0L) {
                                                try {
                                                    final Creature c = Server.getInstance().getCreature(vehic.seats[x].occupant);
                                                    if (Servers.mayEnterServer(c, targetserver)) {
                                                        passengers.add(c);
                                                    }
                                                    else {
                                                        c.getCommunicator().sendAlertServerMessage("Suddenly, strong winds and waves throw you overboard!");
                                                        c.disembark(true);
                                                    }
                                                }
                                                catch (Exception ex2) {}
                                            }
                                        }
                                    }
                                    if (this.createDraggedTransferData(targetserver, this.player.getDraggedItem(), targetTileX, targetTileY, passengers).isEmpty()) {
                                        this.player.sendTransfer(Server.getInstance(), targetserver.INTRASERVERADDRESS, Integer.parseInt(targetserver.INTRASERVERPORT), targetserver.INTRASERVERPASSWORD, targetserver.id, targetTileX, targetTileY, true, false, (byte)0);
                                        final Iterator<Creature> it = passengers.iterator();
                                        while (it.hasNext()) {
                                            it.next().sendTransfer(Server.getInstance(), targetserver.INTRASERVERADDRESS, Integer.parseInt(targetserver.INTRASERVERPORT), targetserver.INTRASERVERPASSWORD, targetserver.id, targetTileX, targetTileY, true, false, (byte)0);
                                        }
                                    }
                                }
                                else {
                                    this.player.sendTransfer(Server.getInstance(), targetserver.INTRASERVERADDRESS, Integer.parseInt(targetserver.INTRASERVERPORT), targetserver.INTRASERVERPASSWORD, targetserver.id, targetTileX, targetTileY, true, false, (byte)0);
                                }
                            }
                            else {
                                this.player.getCommunicator().sendNormalServerMessage("The waves are too high to keep going in that direction right now. (The server is unavailable)");
                                if (this.player.getVehicle() != -10L) {
                                    this.sendAlertServerMessage("The winds die down suddenly and the boat stops moving.");
                                }
                                else {
                                    this.sendAlertServerMessage("You swim against the waves and may do nothing else for a while.");
                                }
                            }
                        }
                        else {
                            this.player.getMovementScheme().resumeSpeedModifier();
                        }
                    }
                }
                else if (this.player.getVehicle() != -10L) {
                    this.sendAlertServerMessage("Suddenly, strong winds and waves throw you overboard!");
                }
                else {
                    this.sendAlertServerMessage("The currents work against you and you get nowhere.");
                }
            }
            return true;
        }
        return false;
    }
    
    public void setInvulnerable(final boolean invuln) {
        this.invulnerable = invuln;
        this.player.refreshAttitudes();
    }
    
    private boolean canResetInactivity(final byte cmd) {
        switch (cmd) {
            case -32:
            case -24:
            case -23:
            case 36:
            case 43:
            case 51:
            case 62:
            case 79:
            case 97:
            case 99:
            case 106:
            case 120:
            case 126: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public void reallyHandle(final int num, final ByteBuffer byteBuffer) {
        if (!this.player.hasLink()) {
            byteBuffer.clear();
            return;
        }
        final byte cmd = byteBuffer.get();
        try {
            if (this.canResetInactivity(cmd)) {
                this.player.resetInactivity(false);
            }
            ++Communicator.numcommands;
            switch (cmd) {
                case -29:
                case 9:
                case 29:
                case 32:
                case 36:
                case 42:
                case 43:
                case 99: {
                    break;
                }
                default: {
                    Communicator.lastcommand = cmd;
                    ++this.commandsThisSecond;
                    if (this.commandsThisSecond > 10) {}
                    break;
                }
            }
            if (Communicator.prevcommand != Communicator.lastcommand) {
                Communicator.prevcommand = Communicator.lastcommand;
            }
            this.player.receivedCmd(cmd);
            switch (cmd) {
                case 9: {
                    this.reallyHandle_CMD_NOT_MOVE_CREATURE();
                    break;
                }
                case 32: {
                    this.reallyHandle_CMD_SPEEDMODIFIER(byteBuffer);
                    break;
                }
                case -29:
                case 36: {
                    this.reallyHandle_CMD_MOVE_CREATURE(byteBuffer, cmd);
                    break;
                }
                case 31: {
                    this.reallyHandle_CMD_STATE(byteBuffer);
                    break;
                }
                case -16: {
                    this.reallyHandle_CMD_EMPTY(byteBuffer);
                    break;
                }
                case 126: {
                    this.reallyHandle_CMD_REQUEST_ACTIONS(byteBuffer);
                    break;
                }
                case -23: {
                    this.reallyHandle_CMD_REQUEST_SELECT(byteBuffer);
                    break;
                }
                case -46: {
                    this.reallyHandle_CMD_ITEM_CREATION_LIST(byteBuffer);
                    break;
                }
                case 97: {
                    this.reallyHandle_CMD_ACTION(byteBuffer);
                    break;
                }
                case 29: {
                    this.reallyHandle_CMD_MORE_ITEMS(byteBuffer);
                    break;
                }
                case 62: {
                    this.reallyHandle_CMD_TOGGLE_SWITCH(byteBuffer);
                    break;
                }
                case 99: {
                    this.reallyHandle_CMD_MESSAGE(byteBuffer);
                    break;
                }
                case -32: {
                    this.reallyHandle_CMD_EQUIP_ITEM(byteBuffer);
                    break;
                }
                case 43: {
                    this.reallyHandle_CMD_MOVE_INVENTORY(byteBuffer);
                    break;
                }
                case 120: {
                    this.reallyHandle_CMD_CLOSE_INVENTORY_WINDOW(byteBuffer);
                    break;
                }
                case 15: {
                    this.reallyHandle_CMD_FORM_RESPONSE(byteBuffer);
                    break;
                }
                case 106: {
                    this.reallyHandle_CMD_BML_FORM(byteBuffer);
                    break;
                }
                case 79: {
                    this.reallyHandle_CMD_CLIMB(byteBuffer);
                    break;
                }
                case 46: {
                    this.reallyHandle_CMD_WEATHER_UPDATE();
                    break;
                }
                case 117: {
                    this.reallyHandle_CMD_WINDIMPACT(byteBuffer);
                    break;
                }
                case 60: {
                    this.reallyHandle_CMD_MOUNTSPEED(byteBuffer);
                    break;
                }
                case 59: {
                    this.reallyHandle_CMD_AVAILABLE_SERVER(byteBuffer);
                    break;
                }
                case 41: {
                    this.reallyHandle_CMD_ERROR(byteBuffer);
                    break;
                }
                case 16: {
                    this.reallyHandle_CMD_FATAL_ERROR(byteBuffer);
                    break;
                }
                case 8: {
                    this.player.setLink(false);
                    break;
                }
                case 4: {
                    logInfo(this.player.getName() + " sent client quit");
                    this.player.setLink(false);
                    break;
                }
                case -21: {
                    this.reallyHandle_CMD_SET_BRIDGE(byteBuffer);
                    break;
                }
                case 51: {
                    this.reallyHandle_CMD_TELEPORT(byteBuffer);
                    break;
                }
                case -15: {
                    this.reallyHandle_CMD_LOGIN(byteBuffer);
                    break;
                }
                case 42: {
                    this.reallyHandle_CMD_SET_TRADE_AGREE(byteBuffer);
                    break;
                }
                case 121: {
                    if (this.player.isTrading()) {
                        this.player.getTrade().end(this.player, true);
                        break;
                    }
                    break;
                }
                case 52: {
                    this.reallyHandle_CMD_NEW_FACE(byteBuffer);
                    break;
                }
                case -8:
                case -3:
                case -2:
                case -1:
                case 104:
                case 123: {
                    this.reallyHandle_EIGC(cmd, byteBuffer);
                    break;
                }
                case 57: {
                    this.reallyHandle_CMD_TAB_CLOSED(byteBuffer);
                    break;
                }
                case -22: {
                    this.reallyHandle_CMD_TAB_SELECTED(byteBuffer);
                    break;
                }
                case -34: {
                    this.reallyHandle_CMD_TICKET_ADD(byteBuffer);
                    break;
                }
                case -35: {
                    this.reallyHandle_CMD_CREATION_WINDOW(byteBuffer);
                    break;
                }
                case -43: {
                    this.reallyHandle_CMD_MAP_ANNOTATIONS(byteBuffer);
                    break;
                }
                case -42: {
                    this.reallyHandle_CMD_SEND_WINDOW_TYPE_DATA(byteBuffer);
                    break;
                }
                case -20: {
                    this.reallyHandle_CMD_SERVERPORTAL(byteBuffer);
                    break;
                }
                case -6: {
                    this.reallyHandle_CMD_VERIFY_CLIENT_VERSION(byteBuffer);
                    break;
                }
                case -25: {
                    this.reallyHandle_CMD_BRIDGE(byteBuffer);
                    break;
                }
                case -24: {
                    this.player.getMovementScheme().setFlying(byteBuffer.get() == 1 && this.player.getPower() > 0);
                    break;
                }
                case -26: {
                    this.reallyHandle_CMD_PERMISSIONS(byteBuffer);
                    break;
                }
                case -55: {
                    this.reallyHandle_CMD_COOKBOOK(byteBuffer);
                    break;
                }
                case -62: {
                    this.reallyHandle_CMD_VALREIFIGHT(byteBuffer);
                    break;
                }
                case -63: {
                    this.reallyHandle_CMD_PLACE_ITEM(byteBuffer);
                    break;
                }
                case -66: {
                    this.reallyHandle_CMD_OPENCLOSE_WINDOW(byteBuffer);
                    break;
                }
                case -64: {
                    this.reallyHandle_CMD_FISH(byteBuffer);
                    break;
                }
                case -39: {
                    this.reallyHandle_CMD_PERSONAL_GOAL_LIST(byteBuffer);
                    break;
                }
            }
        }
        catch (NoSuchItemException nsi) {
            logInfo("Unable to find dragged item", nsi);
        }
        catch (Exception ex2) {
            logInfo(this.player.getName() + "- communication failed: ", ex2);
        }
    }
    
    private void reallyHandle_CMD_SPEEDMODIFIER(final ByteBuffer byteBuffer) {
        boolean mountSpeedChanged = byteBuffer.get() != 0;
        final short mountSpeed = byteBuffer.getShort();
        if (mountSpeedChanged && !this.player.getMovementScheme().removeMountSpeed(mountSpeed) && mountSpeed != 0) {
            Communicator.logger.log(Level.WARNING, this.player.getName() + " failed to locate ms=" + mountSpeed);
            mountSpeedChanged = false;
        }
        boolean windImpactChanged = byteBuffer.get() != 0;
        final byte windImpact = byteBuffer.get();
        if (windImpactChanged && !this.player.getMovementScheme().removeWindMod(windImpact) && windImpact != 0) {
            Communicator.logger.log(Level.WARNING, this.player.getName() + " failed to locate wind impact=" + windImpact);
            windImpactChanged = false;
        }
        boolean moveSpeedChanged = byteBuffer.get() != 0;
        final float moveSpeed = byteBuffer.getFloat();
        if (moveSpeedChanged && !this.player.getMovementScheme().removeSpeedMod(moveSpeed) && moveSpeed != 0.0f) {
            Communicator.logger.log(Level.WARNING, this.player.getName() + " failed to locate movespeed=" + moveSpeed);
            moveSpeedChanged = false;
        }
        final boolean weatherReceived = byteBuffer.get() != 0;
        final boolean climbChanged = byteBuffer.get() != 0;
        final boolean isClimbing = byteBuffer.get() != 0;
        if (climbChanged) {
            if (!isClimbing) {
                this.player.sentClimbing = 0L;
            }
            if (this.player.getVehicle() != -10L && isClimbing) {
                this.sendClimb(false);
                this.sendNormalServerMessage("You can not climb now.");
                this.sendToggle(0, false);
                return;
            }
            if (!this.player.isStunned()) {
                if (this.currentmove != null && this.ready) {
                    this.currentmove.getLast().setToggleClimb(true);
                    this.currentmove.getLast().setClimbing(isClimbing);
                }
                else {
                    this.ticker.setServerClimbing(isClimbing);
                }
            }
            else {
                this.sendClimb(false);
                this.sendNormalServerMessage("You can not climb right now.");
                this.sendToggle(0, false);
            }
        }
        if (this.currentmove != null && this.ready) {
            if (mountSpeedChanged) {
                this.currentmove.getLast().setNewMountSpeed(mountSpeed);
            }
            if (windImpactChanged) {
                this.currentmove.getLast().setNewWindMod(windImpact);
            }
            if (moveSpeedChanged) {
                this.currentmove.getLast().setNewSpeedMod(moveSpeed);
            }
            if (weatherReceived) {
                this.currentmove.getLast().setWeatherChange(true);
            }
        }
        else {
            if (!this.stillLoggingIn() && this.ticker != null) {
                Communicator.logger.log(Level.INFO, this.player.getName() + " Changing without currentmove at " + this.ticker.getX() + "," + this.ticker.getY() + " last=" + this.ticker.xOld + "," + this.ticker.yOld + ": windImpactChanged=" + windImpactChanged + "(" + windImpact + "), mountSpeedChanged=" + mountSpeedChanged + "(" + mountSpeed + "), moveSpeedChanged=" + moveSpeedChanged + " (" + moveSpeed + "), weatherReceived=" + weatherReceived);
            }
            if (this.currentmove != null) {
                if (this.checkMoveChanges()) {
                    Communicator.logger.log(Level.WARNING, "Move changes for not ready handled.");
                }
                this.currentmove.clear(true, this.ticker, this.player, null);
            }
            this.moves = 0;
            this.currentmove = null;
            if (mountSpeedChanged) {
                this.ticker.setMountSpeed(mountSpeed);
            }
            if (windImpactChanged) {
                this.ticker.setWindMod(windImpact);
            }
            if (moveSpeedChanged) {
                this.ticker.setSpeedModifier(moveSpeed);
            }
            if (weatherReceived) {
                this.ticker.diffWindX = Server.getWeather().getXWind();
                this.ticker.diffWindY = Server.getWeather().getYWind();
                this.ticker.setWindRotation(Server.getWeather().getWindRotation());
                this.ticker.setWindStrength(Server.getWeather().getWindPower());
            }
        }
    }
    
    private void reallyHandle_CMD_WINDIMPACT(final ByteBuffer byteBuffer) {
        Communicator.logger.log(Level.WARNING, this.player.getName() + " Not used!");
        final byte nn = byteBuffer.get();
        this.player.getMovementScheme().removeWindMod(nn);
        if (this.currentmove != null && this.ready) {
            this.currentmove.getLast().setNewWindMod(nn);
        }
        else {
            this.ticker.setWindMod(nn);
        }
    }
    
    private void reallyHandle_CMD_MOUNTSPEED(final ByteBuffer byteBuffer) {
        Communicator.logger.log(Level.WARNING, this.player.getName() + " Not used!");
        final short newMountSpeed = byteBuffer.getShort();
        this.player.getMovementScheme().removeMountSpeed(newMountSpeed);
        if (this.currentmove != null && this.ready) {
            this.currentmove.getLast().setNewMountSpeed(newMountSpeed);
        }
        else {
            this.player.getMovementScheme().setMountSpeed(newMountSpeed);
        }
    }
    
    private void reallyHandle_CMD_WEATHER_UPDATE() {
        Communicator.logger.log(Level.WARNING, this.player.getName() + " Not used!");
        if (this.currentmove != null && this.ready) {
            this.currentmove.getLast().setWeatherChange(true);
        }
        else {
            this.ticker.diffWindX = Server.getWeather().getXWind();
            this.ticker.diffWindY = Server.getWeather().getYWind();
            this.ticker.setWindRotation(Server.getWeather().getWindRotation());
            this.ticker.setWindStrength(Server.getWeather().getWindPower());
        }
    }
    
    private void reallyHandle_CMD_CLIMB(final ByteBuffer byteBuffer) {
        Communicator.logger.log(Level.WARNING, this.player.getName() + " Not used!");
        final boolean climbing = byteBuffer.get() != 0;
        if (!climbing) {
            this.player.sentClimbing = 0L;
        }
        if (this.player.getVehicle() != -10L && climbing) {
            this.sendClimb(false);
            this.sendNormalServerMessage("You can not climb now.");
            this.sendToggle(0, false);
            return;
        }
        if (!this.player.isStunned()) {
            if (this.currentmove != null && this.ready) {
                this.currentmove.getLast().setToggleClimb(true);
                this.currentmove.getLast().setClimbing(climbing);
            }
            else {
                this.ticker.setServerClimbing(climbing);
            }
        }
        else {
            this.sendClimb(false);
            this.sendNormalServerMessage("You can not climb right now.");
            this.sendToggle(0, false);
        }
    }
    
    private void reallyHandle_CMD_CREATION_WINDOW(final ByteBuffer byteBuffer) {
        final byte subCommand = byteBuffer.get();
        if (subCommand == 2) {
            this.player.setCreationWindowOpen(false);
        }
    }
    
    private void reallyHandle_EIGC(final byte cmd, final ByteBuffer byteBuffer) throws UnsupportedEncodingException {
        switch (cmd) {
            case -1: {
                final int length = byteBuffer.get() & 0xFF;
                final byte[] tempStringArr = new byte[length];
                byteBuffer.get(tempStringArr);
                final String name = new String(tempStringArr, "UTF-8");
                this.respondEigcName(name);
                break;
            }
            case -2: {
                final int length = byteBuffer.get() & 0xFF;
                final byte[] tempStringArr = new byte[length];
                byteBuffer.get(tempStringArr);
                final String name = new String(tempStringArr, "UTF-8");
                this.respondEigcPlayerName(name);
                break;
            }
            case -3: {
                this.respondEigcLogin(this.player.getName());
                break;
            }
            case -8: {
                final EigcClient client = Eigc.getClientForPlayer(this.player.getName());
                if (client != null) {
                    this.setEigcServiceState(client.getServiceBundle());
                    break;
                }
                break;
            }
            case 104: {
                int length2 = byteBuffer.get() & 0xFF;
                byte[] tempStringArr2 = new byte[length2];
                byteBuffer.get(tempStringArr2);
                final String inviterName = new String(tempStringArr2, "UTF-8");
                length2 = (byteBuffer.get() & 0xFF);
                tempStringArr2 = new byte[length2];
                byteBuffer.get(tempStringArr2);
                final String invitedName = new String(tempStringArr2, "UTF-8");
                final int teamId = byteBuffer.getInt();
                this.respondEigcTeamInvite(teamId, inviterName, invitedName);
                break;
            }
            case 123: {
                final int length2 = byteBuffer.get() & 0xFF;
                final byte[] tempStringArr2 = new byte[length2];
                byteBuffer.get(tempStringArr2);
                final String mutedName = new String(tempStringArr2, "UTF-8");
                final int teamId2 = byteBuffer.getInt();
                final byte muteState = byteBuffer.get();
                this.respondEigcTeamMute(mutedName, teamId2, muteState);
                break;
            }
        }
    }
    
    private void reallyHandle_CMD_FATAL_ERROR(final ByteBuffer byteBuffer) {
        final int length = byteBuffer.get() & 0xFF;
        final byte[] tempStringArr = new byte[length];
        byteBuffer.get(tempStringArr);
    }
    
    private void reallyHandle_CMD_MORE_ITEMS(final ByteBuffer byteBuffer) {
        final long inventoryId = byteBuffer.getLong();
        final long itemid = byteBuffer.getLong();
        try {
            final Item item = Items.getItem(itemid);
            item.sendContainedItems(inventoryId, this.player);
        }
        catch (NoSuchItemException ex) {}
    }
    
    private void reallyHandle_CMD_EMPTY(final ByteBuffer byteBuffer) {
        final int fps = byteBuffer.getInt();
        final int tripCounter = byteBuffer.getInt();
        if (tripCounter <= 10 || fps > 15) {}
    }
    
    private void reallyHandle_CMD_STATE(final ByteBuffer byteBuffer) {
        this.player.gotHash = true;
    }
    
    private void reallyHandle_CMD_PERMISSIONS(final ByteBuffer byteBuffer) throws Exception {
        final byte subCommand = byteBuffer.get();
        switch (subCommand) {
            case 1: {
                final String playerName = LoginHandler.raiseFirstLetter(readByteString(byteBuffer));
                final long pId = PlayerInfoFactory.getWurmId(playerName);
                if (pId == -10L) {
                    this.sendPermissionsAddedManually(playerName, -10L);
                    break;
                }
                final byte kingdom = Players.getInstance().getKingdomForPlayer(pId);
                if (kingdom == this.player.getKingdomId() || kingdom == 0) {
                    this.sendPermissionsAddedManually(playerName, pId);
                }
                else {
                    this.sendNormalServerMessage("You may not permit the enemy " + playerName + " to have any permissions.");
                    this.sendPermissionsAddedManually(playerName, -10L);
                }
                break;
            }
            case 2: {
                final int questionId = byteBuffer.getInt();
                final Properties answers = new Properties();
                final String objectName = readByteString(byteBuffer);
                ((Hashtable<String, String>)answers).put("object", objectName);
                String ownerName = readByteString(byteBuffer);
                ownerName = LoginHandler.raiseFirstLetter(ownerName);
                ((Hashtable<String, String>)answers).put("owner", ownerName);
                ((Hashtable<String, String>)answers).put("manage", "" + (byteBuffer.get() == 1));
                final int rows = byteBuffer.getShort() & 0xFFFF;
                ((Hashtable<String, String>)answers).put("rows", "" + rows);
                if (rows > 0) {
                    final int cols = byteBuffer.get() & 0xFF;
                    ((Hashtable<String, String>)answers).put("cols", "" + cols);
                    for (int row = 0; row < rows; ++row) {
                        final long aId = byteBuffer.getLong();
                        ((Hashtable<String, String>)answers).put("r" + row, "" + aId);
                        for (int col = 0; col < cols; ++col) {
                            final byte flag = byteBuffer.get();
                            ((Hashtable<String, String>)answers).put("r" + row + "c" + col, "" + (flag == 1));
                        }
                    }
                }
                try {
                    final Question question = Questions.getQuestion(questionId);
                    question.answer(answers);
                    Questions.removeQuestion(question);
                }
                catch (NoSuchQuestionException nsq) {
                    this.sendSafeServerMessage("You answered an old question. It has timed out over 15 minutes or been replaced by a more recent.");
                }
                break;
            }
            case 3: {
                final int qId = byteBuffer.getInt();
                final Properties ans = new Properties();
                ((Hashtable<String, String>)ans).put("back", "true");
                try {
                    final Question question2 = Questions.getQuestion(qId);
                    question2.answer(ans);
                    Questions.removeQuestion(question2);
                }
                catch (NoSuchQuestionException nsq2) {
                    this.sendSafeServerMessage("You answered an old question. It has timed out over 15 minutes or been replaced by a more recent.");
                }
                break;
            }
        }
    }
    
    private void reallyHandle_CMD_BRIDGE(final ByteBuffer byteBuffer) throws UnsupportedEncodingException {
        final int startX = byteBuffer.getInt();
        final int startY = byteBuffer.getInt();
        final int startH = byteBuffer.getInt();
        final Point start = new Point(startX, startY, startH);
        final int endX = byteBuffer.getInt();
        final int endY = byteBuffer.getInt();
        final int endH = byteBuffer.getInt();
        final Point end = new Point(endX, endY, endH);
        final byte[] tempStringArr = new byte[byteBuffer.get() & 0xFF];
        byteBuffer.get(tempStringArr);
        final String bridgeName = new String(tempStringArr, "UTF-8");
        final byte dir = byteBuffer.get();
        final byte bridgeType = byteBuffer.get();
        final byte[] tempStringArr2 = new byte[byteBuffer.get() & 0xFF];
        byteBuffer.get(tempStringArr2);
        final String bridgePlan = new String(tempStringArr2, "UTF-8");
        PlanBridgeMethods.planBridge(this.player, dir, bridgeType, false, bridgePlan, 0, start, end, bridgeName);
    }
    
    private void reallyHandle_CMD_VERIFY_CLIENT_VERSION(final ByteBuffer byteBuffer) throws IOException {
        this.player.clearRespondTo();
    }
    
    private void reallyHandle_CMD_SERVERPORTAL(final ByteBuffer byteBuffer) {
        Item portal = null;
        for (final Item item : Items.getAllItems()) {
            if (item.getTemplateId() == 855) {
                portal = item;
                break;
            }
        }
        final int server = byteBuffer.getInt();
        final PortalQuestion pq = new PortalQuestion(this.player, "Select allegiance", "Please select kingdom", portal);
        pq.sendQuestion2(server);
    }
    
    private void reallyHandle_CMD_SEND_WINDOW_TYPE_DATA(final ByteBuffer byteBuffer) {
        final byte windowType = byteBuffer.get();
        switch (windowType) {
            case 0: {
                this.handleRecruitWindowDataMessage(byteBuffer);
                break;
            }
            default: {
                logInfo(this.player.getName() + ": Trying to send data from unknown window: " + windowType);
                break;
            }
        }
    }
    
    private void reallyHandle_CMD_MAP_ANNOTATIONS(final ByteBuffer byteBuffer) throws UnsupportedEncodingException {
        final byte cmdType = byteBuffer.get();
        if (cmdType == 0) {
            final byte type = byteBuffer.get();
            final int nameLength = byteBuffer.getShort();
            final byte[] nameBytes = new byte[nameLength];
            byteBuffer.get(nameBytes);
            final String name = new String(nameBytes, "UTF-8");
            final int serverNameLength = byteBuffer.getShort();
            final byte[] serverBytes = new byte[serverNameLength];
            byteBuffer.get(serverBytes);
            final String server = new String(serverBytes, "UTF-8");
            final int x = byteBuffer.getInt();
            final int y = byteBuffer.getInt();
            final byte icon = byteBuffer.get();
            this.player.createNewMapPOI(name, type, x, y, server, icon);
        }
        else if (cmdType == 1) {
            final long id = byteBuffer.getLong();
            final byte type2 = byteBuffer.get();
            final MapAnnotation anno = this.player.getAnnotation(id, type2);
            if (anno != null) {
                this.player.removeMapPOI(anno);
            }
        }
        else if (cmdType == 2) {
            this.sendMapAnnotationPermissions();
        }
        else {
            logWarn("Unknown annotation command sent: " + cmdType);
        }
    }
    
    private void reallyHandle_CMD_TICKET_ADD(final ByteBuffer byteBuffer) throws UnsupportedEncodingException {
        final byte category = byteBuffer.get();
        final byte[] tempStringArr = new byte[byteBuffer.get() & 0xFF];
        byteBuffer.get(tempStringArr);
        final String message = new String(tempStringArr, "UTF-8");
        if (Tickets.checkForFlooding(this.player.getWurmId())) {
            this.sendNormalServerMessage("Sorry but your support call cannot be taken right now as you already have some in the queue.");
        }
        else {
            Tickets.addTicket(new Ticket(this.player.getWurmId(), this.player.getName(), category, message), false);
        }
    }
    
    private void reallyHandle_CMD_TAB_SELECTED(final ByteBuffer byteBuffer) throws UnsupportedEncodingException {
        final byte[] tempStringArr = new byte[byteBuffer.get() & 0xFF];
        byteBuffer.get(tempStringArr);
        final String title = new String(tempStringArr, "UTF-8");
        if (title.equalsIgnoreCase(":Support")) {
            Tickets.acknowledgeTicketUpdatesFor(this.player);
        }
    }
    
    private void reallyHandle_CMD_TAB_CLOSED(final ByteBuffer byteBuffer) throws UnsupportedEncodingException {
        final byte[] tempStringArr = new byte[byteBuffer.get() & 0xFF];
        byteBuffer.get(tempStringArr);
        final String title = new String(tempStringArr, "UTF-8");
        if (title.startsWith("PM: ")) {
            final StringTokenizer tokens = new StringTokenizer(title);
            tokens.nextToken();
            String targetName = "Unknown";
            if (tokens.hasMoreTokens()) {
                targetName = tokens.nextToken().trim();
            }
            this.player.closePM(targetName);
        }
    }
    
    private void reallyHandle_CMD_NEW_FACE(final ByteBuffer byteBuffer) {
        final long face = byteBuffer.getLong();
        final long itemId = byteBuffer.getLong();
        try {
            final Item i = Items.getItem(itemId);
            if (!i.deleted && i.getOwnerId() == this.player.getWurmId()) {
                this.player.getSaveFile().setFace(face);
                if (this.player.getCurrentTile() != null) {
                    this.player.getCurrentTile().setNewFace(this.player);
                }
                this.sendSafeServerMessage("The mirror disintegrates into nothing as the last of its magic is used.");
                Items.destroyItem(itemId);
            }
        }
        catch (NoSuchItemException nso) {
            Communicator.logger.warning("No such mirror item " + itemId + " for " + this.player.getName());
        }
        catch (IOException ex) {
            Communicator.logger.warning("Unable to set new face for " + this.player.getName() + ": " + ex.getMessage());
        }
    }
    
    private void reallyHandle_CMD_SET_TRADE_AGREE(final ByteBuffer byteBuffer) {
        final int agree = byteBuffer.get();
        final int id = byteBuffer.getInt();
        if (!this.player.isDead()) {
            if (this.player.isTrading()) {
                this.player.getTrade().setSatisfied(this.player, agree != 0, id);
                if (agree != 0 && this.invulnerable) {
                    this.setInvulnerable(false);
                    this.sendNormalServerMessage("You are no longer invulnerable.");
                }
            }
        }
        else if (this.player.isTrading()) {
            this.player.getTrade().end(this.player, true);
        }
    }
    
    private void reallyHandle_CMD_LOGIN(final ByteBuffer byteBuffer) throws UnsupportedEncodingException {
        int length = byteBuffer.get() & 0xFF;
        byte[] tempStringArr = new byte[length];
        byteBuffer.get(tempStringArr);
        final String name = new String(tempStringArr, "UTF-8");
        length = (byteBuffer.get() & 0xFF);
        tempStringArr = new byte[length];
        byteBuffer.get(tempStringArr);
        logInfo("Player with name " + name + " tries to login although he is already logged in.");
    }
    
    private void reallyHandle_CMD_TELEPORT(final ByteBuffer byteBuffer) {
        final int counter = byteBuffer.getInt();
        this.moves = 0;
        this.receivedTicks = false;
        if (this.player.isLogged()) {
            logInfo(this.player.getName() + " is teleporting counter at " + this.player.getTeleportCounter() + " received " + counter + " justlogged on:=" + this.justLoggedIn);
        }
        if (counter != this.player.getTeleportCounter()) {
            if (this.player.getMovementScheme().isIntraTeleporting()) {
                this.player.getMovementScheme().removeIntraTeleport(counter);
                if (this.player.isLogged()) {
                    logInfo("No longer intrateleporting but still teleporting.");
                }
            }
            if (this.justLoggedIn) {
                this.player.teleport(this.player.getVisionArea() == null);
                if (this.player.isLogged()) {
                    logInfo(this.player.getName() + " just logged on. Probably on vehicle. Teleport.");
                }
            }
            return;
        }
        if (this.player.isTeleporting()) {
            if (this.player.getMovementScheme().isIntraTeleporting()) {
                if (this.player.isLogged()) {
                    logInfo(this.player.getName() + " is intraporting.");
                }
                if (this.player.getMovementScheme().removeIntraTeleport(counter)) {
                    if (this.player.isLogged()) {
                        logInfo(this.player.getName() + " removed intraport for " + counter);
                    }
                    if (this.player.transferCounter <= 0) {
                        if (this.player.isLogged()) {
                            logInfo(this.player.getName() + " setting ready");
                        }
                        this.setReady(true);
                        this.player.getMovementScheme().resumeSpeedModifier();
                    }
                    else if (this.player.isLogged()) {
                        logInfo(this.player.getName() + " transfercounter= " + this.player.transferCounter);
                    }
                    if (this.player.getVehicle() == -10L) {
                        if (this.player.isLogged()) {
                            logInfo(this.player.getName() + " vehicle=" + this.player.getVehicle());
                        }
                        this.player.getMovementScheme().setMooredMod(false);
                        this.player.getMovementScheme().addWindImpact((byte)0);
                        this.player.calcBaseMoveMod();
                    }
                    this.player.setTeleporting(false);
                }
                else {
                    logWarn("Counter is correct. Was intrateleporting but there still seems to remain some teleport.");
                }
            }
            else if (this.player.isWithinTeleportTime()) {
                if (this.justLoggedIn) {
                    this.justLoggedIn = false;
                    this.setReady(true);
                    this.player.setTeleporting(false);
                    if (this.player.isLogged()) {
                        logInfo(this.player.getName() + " just logged on");
                    }
                }
                else {
                    this.player.teleport();
                    if (this.player.isLogged()) {
                        logInfo(this.player.getName() + " teleported");
                    }
                }
            }
            else {
                this.sendAlertServerMessage("The time for the teleport was exceeded. You may try again.");
                this.player.cancelTeleport();
            }
        }
        else if (this.justLoggedIn) {
            this.justLoggedIn = false;
            this.setReady(true);
            this.player.setTeleporting(false);
            if (this.player.isLogged()) {
                logInfo(this.player.getName() + " just logged on. End logged.");
            }
        }
    }
    
    private void reallyHandle_CMD_SET_BRIDGE(final ByteBuffer byteBuffer) {
        this.newBridgeId = byteBuffer.getLong();
        if (this.currentmove == null) {
            this.newBridgeId = -10L;
            this.receivedBridgeChange = false;
        }
        else {
            this.receivedBridgeChange = true;
        }
    }
    
    private void reallyHandle_CMD_ERROR(final ByteBuffer byteBuffer) throws UnsupportedEncodingException {
        final int length = byteBuffer.getShort() & 0xFFFF;
        final byte[] tempStringArr = new byte[length];
        byteBuffer.get(tempStringArr);
        if (byteBuffer.hasRemaining()) {
            final int length2 = byteBuffer.getShort() & 0xFFFF;
            final byte[] tempStringArr2 = new byte[length2];
            byteBuffer.get(tempStringArr2);
            this.macAddr = new String(tempStringArr2, "UTF-8");
        }
    }
    
    private void reallyHandle_CMD_AVAILABLE_SERVER(final ByteBuffer byteBuffer) {
        final byte direction = byteBuffer.get();
        final boolean avail = byteBuffer.get() > 0;
        if (direction == 0) {
            this.ticker.serverNorthAvailable = avail;
        }
        else if (direction == 4) {
            this.ticker.serverSouthAvailable = avail;
        }
        else if (direction == 6) {
            this.ticker.serverWestAvailable = avail;
        }
        else if (direction == 2) {
            this.ticker.serverEastAvailable = avail;
        }
    }
    
    private void reallyHandle_CMD_BML_FORM(final ByteBuffer byteBuffer) throws UnsupportedEncodingException {
        final byte verifier = byteBuffer.get();
        byte[] tempStringArr = new byte[byteBuffer.get() & 0xFF];
        byteBuffer.get(tempStringArr);
        final String buttonId = new String(tempStringArr, "UTF-8");
        final int keysize = byteBuffer.getShort() & 0xFFFF;
        final Properties answers = new Properties();
        for (int x = 0; x < keysize; ++x) {
            tempStringArr = new byte[byteBuffer.get() & 0xFF];
            byteBuffer.get(tempStringArr);
            final String key = new String(tempStringArr, "UTF-8");
            tempStringArr = new byte[byteBuffer.getShort()];
            byteBuffer.get(tempStringArr);
            final String value = new String(tempStringArr, "UTF-8");
            ((Hashtable<String, String>)answers).put(key, value);
        }
        if (!buttonId.equals("submit")) {
            ((Hashtable<String, String>)answers).put(buttonId, "true");
        }
        final String ids = answers.getProperty("id");
        final String tutId = answers.getProperty("tutorialid");
        if (ids != null && !ids.isEmpty()) {
            final int id = Integer.parseInt(ids);
            try {
                final Question question = Questions.getQuestion(id);
                if (question.getResponder() == this.player || (question instanceof VillageJoinQuestion && ((VillageJoinQuestion)question).getInvited().getWurmId() == this.player.getWurmId()) || (question instanceof TeamManagementQuestion && ((TeamManagementQuestion)question).getInvited().getWurmId() == this.player.getWurmId())) {
                    if (!buttonId.isEmpty()) {
                        question.answer(answers);
                    }
                    Questions.removeQuestion(question);
                }
            }
            catch (NoSuchQuestionException nsq) {
                this.sendSafeServerMessage("You answered an old question. It has timed out over 15 minutes or been replaced by a more recent.");
            }
        }
        else if (tutId != null && !tutId.isEmpty() && PlayerTutorial.doesTutorialExist(this.player.getWurmId())) {
            PlayerTutorial.getTutorialForPlayer(this.player.getWurmId(), false).updateReceived(answers);
        }
    }
    
    private void reallyHandle_CMD_FORM_RESPONSE(final ByteBuffer byteBuffer) throws UnsupportedEncodingException {
        final int keysize = byteBuffer.getShort() & 0xFFFF;
        final Properties answers = new Properties();
        for (int x = 0; x < keysize; ++x) {
            byte[] tempStringArr = new byte[byteBuffer.get() & 0xFF];
            byteBuffer.get(tempStringArr);
            final String key = new String(tempStringArr, "UTF-8");
            tempStringArr = new byte[byteBuffer.getShort()];
            byteBuffer.get(tempStringArr);
            final String value = new String(tempStringArr, "UTF-8");
            ((Hashtable<String, String>)answers).put(key, value);
        }
        final String ids = answers.getProperty("id");
        final int id = Integer.parseInt(ids);
        try {
            final Question question = Questions.getQuestion(id);
            if (question.getResponder() == this.player) {
                question.answer(answers);
                Communicator.commandAction = question.getType();
            }
        }
        catch (NoSuchQuestionException nsq) {
            this.sendSafeServerMessage("You answered an old question. It has timed out after 15 minutes or has been replaced by a more recent one.");
        }
    }
    
    private void reallyHandle_CMD_CLOSE_INVENTORY_WINDOW(final ByteBuffer byteBuffer) {
        final long targetId = byteBuffer.getLong();
        if (WurmId.getType(targetId) == 13) {
            this.player.closeBank();
        }
        else {
            try {
                final Item item = Items.getItem(targetId);
                if (item.getOwnerId() != this.player.getWurmId()) {
                    if (item.getParentId() == -10L) {
                        item.removeWatcher(this.player, true);
                    }
                    else {
                        boolean found = false;
                        try {
                            final Creature[] crets = item.getParent().getWatchers();
                            for (int x = 0; x < crets.length; ++x) {
                                if (crets[x].getWurmId() == this.player.getWurmId()) {
                                    found = true;
                                }
                            }
                        }
                        catch (NoSuchItemException ex) {}
                        catch (NoSuchCreatureException ex2) {}
                        if (!found) {
                            item.removeWatcher(this.player, true);
                        }
                    }
                }
                this.sendCloseInventoryWindow(targetId);
            }
            catch (NoSuchItemException nsi) {
                logInfo("No container to close with id " + targetId);
            }
        }
    }
    
    private void reallyHandle_CMD_MOVE_INVENTORY(final ByteBuffer byteBuffer) throws NoSuchItemException, IOException {
        final long now = System.currentTimeMillis();
        final int nums = byteBuffer.getShort() & 0xFFFF;
        this.subjectIds = new long[nums];
        for (int x = 0; x < nums; ++x) {
            this.subjectIds[x] = byteBuffer.getLong();
        }
        long targetId = Communicator.commandAction = byteBuffer.getLong();
        if (targetId == -1L) {
            targetId = this.player.getInventory().getWurmId();
        }
        if (WurmId.getType(targetId) == 8 || WurmId.getType(targetId) == 32) {
            return;
        }
        if (this.player.isUndead()) {
            return;
        }
        this.coins = null;
        this.subjectItem = null;
        boolean checkedParent = false;
        try {
            final Item target = Items.getItem(targetId);
            if (target.getTemplateId() == 1392) {
                targetId = target.getParentId();
            }
        }
        catch (NoSuchItemException ex) {}
        int maxLength = 6;
        int moved = 0;
        for (int s = 0; s < nums; ++s) {
            if (s >= 100) {
                this.sendNormalServerMessage("You may only move max 100 items at a time.");
                this.handleCoins(this.coins);
                break;
            }
            if (WurmId.getType(this.subjectIds[s]) == 8 || WurmId.getType(this.subjectIds[s]) == 32) {
                this.handleCoins(this.coins);
                return;
            }
            this.subjectItem = Items.getItem(this.subjectIds[s]);
            if (this.subjectItem.getTemplateId() == 1311) {
                this.sendNormalServerMessage("You can only load or unload this item.");
                return;
            }
            boolean held = false;
            final long subjectOwnerId = this.subjectItem.getOwnerId();
            if (subjectOwnerId == this.player.getWurmId()) {
                held = true;
            }
            if ((this.subjectItem.isBodyPart() && this.subjectItem.getAuxData() != 100) || this.subjectItem.getTemplateId() == 0 || this.subjectItem.getTemplateId() == 1392) {
                this.sendNormalServerMessage("You cannot move that.");
                this.handleCoins(this.coins);
                return;
            }
            if (this.subjectItem.isBeingWorkedOn()) {
                long lOwner;
                try {
                    lOwner = this.subjectItem.getOwner();
                }
                catch (NotOwnedException e) {
                    lOwner = -1L;
                }
                if (lOwner != -1L) {
                    this.sendNormalServerMessage("You are working with that item.");
                }
                else {
                    this.sendNormalServerMessage("That item is already busy");
                }
                this.handleCoins(this.coins);
                return;
            }
            if (this.subjectItem.isOutsideOnly() || this.subjectItem.isComponentItem()) {
                this.sendNormalServerMessage("You cannot move that.");
                this.handleCoins(this.coins);
                return;
            }
            if (this.subjectItem.mailed) {
                this.sendNormalServerMessage("You cannot reach that now. It is in the mail.");
                return;
            }
            if (targetId < 5L) {
                if (!this.player.isTrading()) {
                    this.sendAlertServerMessage("You are not trading.");
                    return;
                }
                final Trade trade = this.player.getTrade();
                if (this.subjectItem.isLiquid()) {
                    return;
                }
                if (this.subjectItem.getTemplateId() == 300 && this.subjectItem.getData() == trade.creatureTwo.getWurmId()) {
                    return;
                }
                if (this.player.getPower() == 0 && trade.creatureTwo.getPower() == 0 && (this.subjectItem.isVillageDeed() || this.subjectItem.isHomesteadDeed())) {
                    if (targetId == 4L && !this.player.isReallyPaying() && (this.subjectItem.getData2() > 0 || Servers.localServer.EPIC)) {
                        this.sendNormalServerMessage("You must be a premium player in order to receive settlement forms and deeds.");
                        return;
                    }
                    if (this.subjectItem.getData2() > 0) {
                        if (trade.creatureTwo != this.player && this.subjectItem.getData2() != trade.creatureTwo.getVillageId() && trade.creatureTwo.isPlayer() && trade.creatureTwo.mayChangeVillageInMillis() > 0L) {
                            this.sendNormalServerMessage(trade.creatureTwo.getName() + " may not change village until " + Server.getTimeFor(trade.creatureTwo.mayChangeVillageInMillis()) + " has elapsed.");
                            return;
                        }
                        if (trade.creatureOne != this.player && this.subjectItem.getData2() != trade.creatureOne.getVillageId() && trade.creatureOne.isPlayer() && trade.creatureOne.mayChangeVillageInMillis() > 0L) {
                            this.sendNormalServerMessage(trade.creatureOne.getName() + " may not change village until " + Server.getTimeFor(trade.creatureOne.mayChangeVillageInMillis()) + " has elapsed.");
                            return;
                        }
                    }
                }
                final TradingWindow window = trade.getTradingWindow(targetId);
                if (this.subjectItem.isTraded()) {
                    final TradingWindow currWin = this.subjectItem.getTradeWindow();
                    if (currWin.mayMoveItemToWindow(this.subjectItem, this.player, targetId)) {
                        currWin.removeItem(this.subjectItem);
                        window.startReceivingItems();
                        window.addItem(this.subjectItem);
                        window.stopReceivingItems();
                    }
                }
                else {
                    if (!held) {
                        return;
                    }
                    if (window.mayAddFromInventory(this.player, this.subjectItem)) {
                        window.startReceivingItems();
                        window.addItem(this.subjectItem);
                        window.stopReceivingItems();
                    }
                }
            }
            else {
                final Communicator playerComm = this.player.getCommunicator();
                if (WurmId.getType(targetId) == 13) {
                    if (this.player.isTrading()) {
                        this.sendNormalServerMessage("You are trading and may not perform bank actions.");
                        return;
                    }
                    if (this.player.isDead()) {
                        this.sendNormalServerMessage("You cannot reach that now.");
                        this.handleCoins(this.coins);
                        return;
                    }
                    final Bank bank = Banks.getBank(this.player.getWurmId());
                    if (!bank.open) {
                        logWarn(this.player.getName() + " tried to drag to bank while it is closed.");
                        this.handleCoins(this.coins);
                        return;
                    }
                    if (this.subjectItem.isBanked()) {
                        return;
                    }
                    if (this.subjectItem.isBulkContainer() || this.subjectItem.isBulkItem()) {
                        playerComm.sendNormalServerMessage("You may not bank the " + this.subjectItem.getName() + ".");
                        continue;
                    }
                    if (!this.subjectItem.isAlwaysBankable() && (this.subjectItem.isNoDrop() || this.subjectItem.isArtifact() || this.subjectItem.isBodyPart() || this.subjectItem.isTemporary() || this.subjectItem.isLiquid() || this.subjectItem.isNoBank())) {
                        playerComm.sendNormalServerMessage("You may not bank the " + this.subjectItem.getName() + ".");
                        continue;
                    }
                    if (this.subjectItem.isLocked()) {
                        playerComm.sendNormalServerMessage("You may not bank locked items.");
                        continue;
                    }
                    if (this.subjectItem.getOwnerId() != this.player.getWurmId()) {
                        playerComm.sendNormalServerMessage("You must possess the " + this.subjectItem.getName() + " in order to bank it.");
                        continue;
                    }
                    if (this.subjectItem.isHollow() && !this.subjectItem.isEmpty(true)) {
                        boolean skip = false;
                        for (final Item item : this.subjectItem.getAllItems(true)) {
                            if (item.getTemplateId() != 1392) {
                                skip = true;
                            }
                        }
                        if (skip) {
                            playerComm.sendNormalServerMessage("You may only bank empty containers.");
                            continue;
                        }
                    }
                    if (this.subjectItem.isCoin()) {
                        if (this.coins == null) {
                            this.coins = new HashSet<Item>();
                        }
                        this.coins.add(this.subjectItem);
                        continue;
                    }
                    if (!bank.addItem(this.subjectItem)) {
                        playerComm.sendNormalServerMessage("The bank is full. Remove something first.");
                        this.handleCoins(this.coins);
                        return;
                    }
                    if (this.subjectItem.isFood()) {
                        final float damageToSet = this.subjectItem.getDamage() + 5.0f + Server.rand.nextFloat() * 10.0f;
                        this.subjectItem.setDamage(damageToSet);
                        this.sendNormalServerMessage("The " + this.subjectItem.getName() + " gets dirty.");
                        if (damageToSet >= 100.0f) {
                            continue;
                        }
                    }
                    try {
                        final Item parent = this.subjectItem.getParent();
                        parent.dropItem(this.subjectItem.getWurmId(), true);
                        this.sendAddToInventory(this.subjectItem, bank.id, 0L, -1);
                        continue;
                    }
                    catch (NoSuchItemException nsi2) {
                        logWarn(this.subjectItem.getName() + " had no parent when banking?");
                        playerComm.sendNormalServerMessage(this.subjectItem.getName() + " does not belong to you. Report this as an error.");
                        return;
                    }
                }
                if (this.subjectItem.isBanked()) {
                    if (this.subjectItem.isCoin()) {
                        return;
                    }
                    if (this.subjectItem.lastOwner != this.player.getWurmId()) {
                        return;
                    }
                    if (this.player.isDead()) {
                        this.sendNormalServerMessage("You are trading and may not perform bank actions.");
                        return;
                    }
                    if (this.player.isTrading()) {
                        this.sendNormalServerMessage("You cannot reach that now.");
                        return;
                    }
                    final Bank bank = Banks.getBank(this.player.getWurmId());
                    if (bank == null) {
                        return;
                    }
                    if (!bank.open) {
                        logWarn(this.player.getName() + " tried to drag from bank while it is closed.");
                        return;
                    }
                    if (WurmId.getType(targetId) != 2 && WurmId.getType(targetId) != 6 && WurmId.getType(targetId) != 19 && WurmId.getType(targetId) != 20) {
                        playerComm.sendNormalServerMessage("You may not move bank items there.");
                        return;
                    }
                    try {
                        final Item target2 = Items.getItem(targetId);
                        final Item inventory = this.player.getInventory();
                        if (target2.getTemplateId() == 0) {
                            this.sendRemoveFromInventory(this.subjectItem, bank.id);
                            bank.removeItem(this.subjectItem);
                            this.subjectItem.removeWatcher(this.player, false);
                            target2.insertItem(this.subjectItem);
                            if (!this.subjectItem.isRiftLoot()) {
                                this.subjectItem.setLastMaintained(WurmCalendar.currentTime);
                            }
                        }
                        else {
                            if (target2.getOwnerId() != this.player.getWurmId()) {
                                return;
                            }
                            if (target2.isBulkContainer() && !target2.isCrate() && target2.getTopParent() != target2.getWurmId()) {
                                this.sendNormalServerMessage(StringUtil.format("You are not allowed to do that! The %s must be on the ground.", target2.getName()));
                                return;
                            }
                            if (target2.isTent() && target2.getTopParent() != target2.getWurmId()) {
                                this.sendNormalServerMessage(StringUtil.format("You are not allowed to do that! The %s must be on the ground.", target2.getName()));
                                return;
                            }
                            this.tryInsertBankItem(this.subjectItem, target2, bank, inventory);
                        }
                    }
                    catch (NoSuchItemException nsi2) {
                        logWarn(this.player.getName() + "- removing from bank but no such item: " + targetId);
                    }
                }
                else {
                    final boolean sameVehicle = this.subjectItem.getTopParent() == this.player.getVehicle();
                    try {
                        if (!checkedParent) {
                            final Item parent = Items.getItem(this.subjectItem.getTopParent());
                            if (parent.isVehicle() && !parent.isTent()) {
                                maxLength = parent.getSizeZ() / 100;
                            }
                            if (parent.getOwnerId() != this.player.getWurmId() && parent.getBridgeId() != this.player.getBridgeId()) {
                                this.sendNormalServerMessage("You need to be on the same bridge as " + parent.getName() + ".");
                                return;
                            }
                            checkedParent = true;
                        }
                    }
                    catch (NoSuchItemException ex2) {}
                    if (!held && !sameVehicle && !this.player.isWithinDistanceTo(this.subjectItem, maxLength)) {
                        final Item item2 = Items.getItem(targetId);
                        if (!item2.isTraded() || !this.subjectItem.isTraded() || item2.mailed || this.subjectItem.mailed) {
                            this.sendNormalServerMessage("You are not allowed to do that.");
                        }
                        else {
                            final TradingWindow window2 = item2.getTradeWindow();
                            if (this.subjectItem.isTraded()) {
                                final TradingWindow currWin2 = this.subjectItem.getTradeWindow();
                                if (currWin2.mayMoveItemToWindow(this.subjectItem, this.player, window2.getWurmId())) {
                                    currWin2.removeItem(this.subjectItem);
                                    window2.startReceivingItems();
                                    window2.addItem(this.subjectItem);
                                    window2.stopReceivingItems();
                                }
                            }
                        }
                    }
                    else {
                        try {
                            if (!held && this.player.isDead()) {
                                this.sendNormalServerMessage("You cannot reach that now.");
                                return;
                            }
                            Label_9297: {
                                if (WurmId.getType(targetId) != 2 && WurmId.getType(targetId) != 6 && WurmId.getType(targetId) != 19) {
                                    if (WurmId.getType(targetId) != 20) {
                                        break Label_9297;
                                    }
                                }
                                try {
                                    Item targetItem = Items.getItem(targetId);
                                    final Item parent2 = Items.getItem(targetItem.getTopParent());
                                    final Item subjectParent = this.subjectItem.getParentOrNull();
                                    Label_2775: {
                                        if (this.subjectItem.isBulkContainer()) {
                                            if (subjectParent == null || !subjectParent.isInventory() || parent2.getTemplateId() != 1315) {
                                                if (subjectParent == null || subjectParent.getTemplateId() != 1315 || !parent2.isInventory()) {
                                                    if (this.subjectItem.isCrate()) {
                                                        if (parent2.getTemplateId() == 1309 || parent2.getTemplateId() == 1312) {
                                                            break Label_2775;
                                                        }
                                                        if (parent2.isVehicle()) {
                                                            break Label_2775;
                                                        }
                                                    }
                                                    if ((subjectParent == null || (!subjectParent.isVehicle() && subjectParent.getTemplateId() != 1312) || (!parent2.isVehicle() && parent2.getTemplateId() != 1312)) && parent2.getTemplateId() != 853 && parent2.getTemplateId() != 1410) {
                                                        this.sendNormalServerMessage("You can only drop the " + this.subjectItem.getName() + ".");
                                                        return;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (parent2.getTemplateId() == 1342 && !parent2.isPlanted() && parent2.getData() == -1L) {
                                        this.sendNormalServerMessage("The " + parent2.getName() + " needs to be planted or attached to a boat, for the " + this.subjectItem.getName() + " to go into it.");
                                        return;
                                    }
                                    if (!parent2.isHollow()) {
                                        this.sendNormalServerMessage("There is nowhere in the " + parent2.getName() + " to put the " + this.subjectItem.getName() + ".");
                                        return;
                                    }
                                    float maxDist = 6.0f;
                                    if (parent2.isVehicle()) {
                                        final Vehicle vehicle = Vehicles.getVehicle(parent2);
                                        if (vehicle != null) {
                                            maxDist = Math.max(maxDist, vehicle.getMaxAllowedLoadDistance());
                                        }
                                    }
                                    if (!this.player.isWithinDistanceTo(parent2, maxDist)) {
                                        this.sendNormalServerMessage("You're too far away from the " + parent2.getName() + ".");
                                        return;
                                    }
                                    if (parent2.getOwnerId() != this.player.getWurmId() && parent2.getBridgeId() != this.player.getBridgeId()) {
                                        this.sendNormalServerMessage("You need to be on the same bridge as " + parent2.getName() + ".");
                                        return;
                                    }
                                    Item newTarget = targetItem;
                                    if (!targetItem.isHollow() && !targetItem.isBulkContainer()) {
                                        boolean end = false;
                                        do {
                                            newTarget = newTarget.getParentOrNull();
                                            if (newTarget != null && newTarget.getWurmId() != targetItem.getWurmId()) {
                                                if (!newTarget.isHollow() && !newTarget.isBulkContainer()) {
                                                    continue;
                                                }
                                                targetItem = newTarget;
                                                end = true;
                                            }
                                            else {
                                                end = true;
                                            }
                                        } while (!end);
                                    }
                                    if (targetItem.getTemplate().hasViewableSubItems() && !targetItem.getTemplate().isContainerWithSubItems()) {
                                        this.sendNormalServerMessage("You'll need to place the " + this.subjectItem.getName() + " on the " + targetItem.getName() + ".");
                                        return;
                                    }
                                    if (targetItem.getOwnerId() > 0L && (targetItem.isBulkItem() || targetItem.isBulkContainer())) {
                                        this.sendNormalServerMessage("You are not allowed to do that.");
                                        return;
                                    }
                                    if (targetItem.mailed) {
                                        this.sendNormalServerMessage("You cannot reach that now. It is in the mail.");
                                        return;
                                    }
                                    if (targetItem.isLockable()) {
                                        final long lockid = targetItem.getLockId();
                                        if (lockid != -10L) {
                                            try {
                                                final Item lock = Items.getItem(lockid);
                                                if (!this.player.hasKeyForLock(lock) && !targetItem.mayAccessHold(this.player)) {
                                                    boolean mayUseItem = false;
                                                    if (targetItem.isDraggable() || targetItem.getTemplateId() == 850) {
                                                        mayUseItem = MethodsItems.mayUseInventoryOfVehicle(this.player, targetItem);
                                                    }
                                                    if (!mayUseItem) {
                                                        this.sendSafeServerMessage("That item is locked.");
                                                        return;
                                                    }
                                                }
                                            }
                                            catch (NoSuchItemException nsi) {
                                                logWarn(targetId + " is locked but lock " + lockid + " can not be found.", nsi);
                                                this.sendAlertServerMessage("The target item is locked but the lock could not be found. Rejecting move request.");
                                                return;
                                            }
                                        }
                                    }
                                    if (targetItem.isTraded()) {
                                        if (targetItem.getOwnerId() == this.player.getWurmId()) {
                                            final TradingWindow window3 = targetItem.getTradeWindow();
                                            if (this.subjectItem.isTraded()) {
                                                final TradingWindow currWin3 = this.subjectItem.getTradeWindow();
                                                if (currWin3.mayMoveItemToWindow(this.subjectItem, this.player, window3.getWurmId())) {
                                                    currWin3.removeItem(this.subjectItem);
                                                    window3.startReceivingItems();
                                                    window3.addItem(this.subjectItem);
                                                    window3.stopReceivingItems();
                                                }
                                            }
                                            else {
                                                if (subjectOwnerId == -10L) {
                                                    this.sendNormalServerMessage("You are not allowed to do that, since the " + targetItem.getName() + " is being traded.");
                                                    return;
                                                }
                                                if (window3.mayAddFromInventory(this.player, this.subjectItem)) {
                                                    window3.startReceivingItems();
                                                    window3.addItem(this.subjectItem);
                                                    window3.stopReceivingItems();
                                                }
                                            }
                                        }
                                        else if (!held) {
                                            final TradingWindow window3 = targetItem.getTradeWindow();
                                            if (this.subjectItem.isTraded()) {
                                                final TradingWindow currWin3 = this.subjectItem.getTradeWindow();
                                                if (currWin3.mayMoveItemToWindow(this.subjectItem, this.player, window3.getWurmId())) {
                                                    currWin3.removeItem(this.subjectItem);
                                                    window3.startReceivingItems();
                                                    window3.addItem(this.subjectItem);
                                                    window3.stopReceivingItems();
                                                }
                                            }
                                        }
                                    }
                                    else if (this.subjectItem.isTraded()) {
                                        final TradingWindow currWin4 = this.subjectItem.getTradeWindow();
                                        if (this.mayMoveToInventory(this.subjectItem)) {
                                            currWin4.removeItem(this.subjectItem);
                                        }
                                    }
                                    else if (subjectOwnerId == -10L) {
                                        final long topparentid = this.subjectItem.getTopParent();
                                        if (topparentid != -10L && WurmId.getType(topparentid) != 6) {
                                            boolean mayUseVehicle = true;
                                            try {
                                                final Item topparent = Items.getItem(topparentid);
                                                if (topparent.isDraggable()) {
                                                    mayUseVehicle = MethodsItems.mayUseInventoryOfVehicle(this.player, topparent);
                                                }
                                                if (!mayUseVehicle && this.subjectItem.lastOwner != this.player.getWurmId() && ((topparent.isVehicle() && topparent.getLockId() != -10L) || Items.isItemDragged(topparent)) && this.player.getDraggedItem() != topparent) {
                                                    this.sendNormalServerMessage("The " + topparent.getName() + " is being watched too closely. You cannot take items from it.");
                                                    return;
                                                }
                                            }
                                            catch (NoSuchItemException ex3) {}
                                        }
                                        final boolean noOwner = targetItem.getOwnerId() == -10L;
                                        final boolean targetIsPileOrVehicle = targetItem.isTopParentPile() || targetItem.isVehicle();
                                        final Item topParent = targetItem.getTopParentOrNull();
                                        final boolean topParentIsContainer = topParent != null && (topParent.isVehicle() || topParent.isHollow());
                                        final boolean subjectIsNotBulkable = !this.subjectItem.isBulk() && !this.subjectItem.isBulkItem();
                                        final boolean targetIsNotBulkItemOrContainer = !targetItem.isBulkItem() && !targetItem.isBulkContainer();
                                        final boolean targetIsNotVehicleOrContainer = !targetItem.isVehicle() && !targetItem.isHollow();
                                        if ((noOwner && !targetIsPileOrVehicle && !topParentIsContainer && subjectIsNotBulkable) || (targetIsNotBulkItemOrContainer && targetIsNotVehicleOrContainer)) {
                                            this.sendNormalServerMessage("You are not allowed to do that.");
                                            return;
                                        }
                                        if (targetItem.getOwnerId() == -10L && targetItem.isTopParentPile() && this.subjectItem.isBulkItem()) {
                                            this.sendNormalServerMessage("You are not allowed to do that.");
                                            return;
                                        }
                                        if (targetItem.isTraded()) {
                                            this.sendNormalServerMessage("You are not allowed to do that, since the " + targetItem.getName() + " is being traded.");
                                            return;
                                        }
                                        if (this.subjectItem.getTopParent() != this.player.getVehicle() && !this.player.isWithinTileDistanceTo((int)this.subjectItem.getPosX() >> 2, (int)this.subjectItem.getPosY() >> 2, (int)this.subjectItem.getPosZ() >> 2, 3)) {
                                            this.sendNormalServerMessage("You cannot reach the " + this.subjectItem.getName() + " now.");
                                            return;
                                        }
                                        if (!MethodsItems.isLootableBy(this.player, this.subjectItem)) {
                                            this.sendNormalServerMessage("You may not loot that item.");
                                            return;
                                        }
                                        if (MethodsItems.checkIfStealing(this.subjectItem, this.player, null)) {
                                            final int tilex = (int)this.subjectItem.getPosX() >> 2;
                                            final int tiley = (int)this.subjectItem.getPosY() >> 2;
                                            final Village vil = Zones.getVillage(tilex, tiley, this.player.isOnSurface());
                                            if (this.player.isLegal() && vil != null) {
                                                this.sendNormalServerMessage("That would be illegal here. You can check the settlement token for the local laws.");
                                                return;
                                            }
                                            if (this.player.getDeity() != null && !this.player.getDeity().isLibila() && this.player.faithful) {
                                                this.sendNormalServerMessage("Your deity would never allow stealing.");
                                                return;
                                            }
                                            this.sendNormalServerMessage("Right-click and select Steal instead.");
                                        }
                                        else if (this.subjectItem.isBulkItem()) {
                                            if (targetItem.isNoPut() || targetItem.getTemplateId() == 272) {
                                                this.sendNormalServerMessage("You are not allowed to do that.");
                                                return;
                                            }
                                            final Item targetParent = targetItem.getParentOrNull();
                                            if (targetItem.isContainerLiquid()) {
                                                for (final Item contained : targetItem.getItems()) {
                                                    if (contained.isLiquid() && MethodsItems.wouldDestroyLiquid(targetItem, contained, this.subjectItem)) {
                                                        this.sendNormalServerMessage("That would destroy the liquid.");
                                                        return;
                                                    }
                                                }
                                            }
                                            if (targetItem.getTopParent() != targetItem.getWurmId()) {
                                                if (targetParent != null) {
                                                    if (targetParent.isNoPut()) {
                                                        this.sendNormalServerMessage("You are not allowed to do that.");
                                                        return;
                                                    }
                                                }
                                                else {
                                                    logInfo(targetItem.getName() + ": " + targetItem.getWurmId() + " no parent " + targetItem.getParentId());
                                                }
                                            }
                                            if (!targetItem.isVehicle() && !targetItem.isHollow() && !targetItem.isTent() && targetItem.getTopParent() != targetItem.getWurmId()) {
                                                if (targetItem.getTemplateId() == 1432) {
                                                    this.sendNormalServerMessage("You can't put that there.");
                                                    return;
                                                }
                                                if (targetItem.getTemplateId() == 1436) {
                                                    this.sendNormalServerMessage("You can't put that there.");
                                                    return;
                                                }
                                                if (targetItem.getTemplateId() == 1434 && !this.subjectItem.getRealTemplate().isSeed()) {
                                                    this.sendNormalServerMessage("You can only put seeds into the feeder.");
                                                    return;
                                                }
                                                if (targetItem.getTemplateId() == 1435 && this.subjectItem.getRealTemplateId() != 128) {
                                                    this.sendNormalServerMessage("You can only put water into the drinker.");
                                                    return;
                                                }
                                                final RemoveItemQuestion riq = new RemoveItemQuestion(this.player, this.subjectItem.getWurmId());
                                                riq.sendQuestion();
                                            }
                                            else {
                                                if (targetItem.isTent()) {
                                                    this.sendNormalServerMessage("The tent doesn't work that way.");
                                                    return;
                                                }
                                                if (targetItem.getTemplateId() == 1023 && !this.subjectItem.getRealTemplate().isUnfired()) {
                                                    this.sendNormalServerMessage("Only unfired clay items can be put into a kiln.");
                                                    return;
                                                }
                                                if (targetItem.getTemplateId() == 1028 && !this.subjectItem.getRealTemplate().isOre) {
                                                    this.sendNormalServerMessage("Only ore can be put into a smelter.");
                                                    return;
                                                }
                                                if (targetItem.isShelf() && targetParent.getParentId() != -10L) {
                                                    this.sendNormalServerMessage("You cannot put items on the " + targetItem.getName() + " whilst the " + targetParent.getName() + " is not on the ground.");
                                                    return;
                                                }
                                                if (targetItem.getTemplateId() == 1278 && this.subjectItem.getRealTemplateId() != 1276) {
                                                    this.sendNormalServerMessage("Only snowballs can be put into an icebox.");
                                                    return;
                                                }
                                                if (targetItem.getTemplateId() == 1309 && !this.subjectItem.isCrate()) {
                                                    this.sendNormalServerMessage("Only crates fit in the wagoner container.");
                                                    return;
                                                }
                                                if (targetItem.getTemplateId() == 1108 && this.subjectItem.getRealTemplateId() != 768) {
                                                    this.sendNormalServerMessage("Only wine barrels can be put on that rack.");
                                                    return;
                                                }
                                                if (targetItem.getTemplateId() == 1109 && this.subjectItem.getRealTemplateId() != 189) {
                                                    this.sendNormalServerMessage("Only small barrels can be put into that rack.");
                                                    return;
                                                }
                                                if (targetItem.getTemplateId() == 1279 && !this.subjectItem.getRealTemplate().canLarder() && (!this.subjectItem.getRealTemplate().usesFoodState() || this.subjectItem.getAuxData() != 0)) {
                                                    this.sendNormalServerMessage("Only procesed food items can be put onto a food shelf.");
                                                    return;
                                                }
                                                if (targetItem.containsIngredientsOnly() && !this.subjectItem.getRealTemplate().isFood() && !this.subjectItem.getRealTemplate().isLiquid() && !this.subjectItem.getRealTemplate().isRecipeItem()) {
                                                    this.sendNormalServerMessage("Only ingredients that are used to make food can be put onto " + targetItem.getNameWithGenus() + ".");
                                                    return;
                                                }
                                                if (targetItem.isAlmanacContainer() && !this.subjectItem.isHarvestReport()) {
                                                    this.sendNormalServerMessage("Only harvest reports can be put in " + targetItem.getTemplate().getNameWithGenus() + ".");
                                                    return;
                                                }
                                                if (targetItem.getTemplateId() == 1312 && !this.subjectItem.isCrate()) {
                                                    this.sendNormalServerMessage("Only crates can be put into that rack.");
                                                    return;
                                                }
                                                if (targetItem.getTemplateId() == 1315 && (this.subjectItem.getTemplateId() != 662 || !this.subjectItem.isEmpty(false))) {
                                                    this.sendNormalServerMessage("Only empty bsb can be put into that rack.");
                                                    return;
                                                }
                                                if (targetItem.getTemplateId() == 1284) {
                                                    if (parent2 != null && parent2.getTemplateId() == 1178 && parent2.getParentId() != -10L) {
                                                        this.sendNormalServerMessage("You can only put liquids into the boiler when the still is not on the ground.");
                                                        return;
                                                    }
                                                }
                                                else if (targetItem.getTemplateId() == 725) {
                                                    if (!this.subjectItem.isWeaponPolearm()) {
                                                        this.sendNormalServerMessage("The " + this.subjectItem.getName() + " doesn't fit.");
                                                        return;
                                                    }
                                                }
                                                else if (targetItem.getTemplateId() == 724) {
                                                    if (!this.subjectItem.isWeapon() || this.subjectItem.isWeaponPolearm()) {
                                                        this.sendNormalServerMessage("The " + this.subjectItem.getName() + " doesn't fit.");
                                                        return;
                                                    }
                                                }
                                                else if (targetItem.getTemplateId() == 758) {
                                                    if (!this.subjectItem.isWeaponBow() && !this.subjectItem.isBowUnstringed()) {
                                                        this.sendNormalServerMessage("The " + this.subjectItem.getName() + " doesn't fit.");
                                                        return;
                                                    }
                                                }
                                                else if (targetItem.getTemplateId() == 759) {
                                                    if (!this.subjectItem.isArmour()) {
                                                        this.sendNormalServerMessage("The " + this.subjectItem.getName() + " doesn't fit.");
                                                        return;
                                                    }
                                                }
                                                else if (targetItem.getTemplateId() == 892 && !this.subjectItem.isArmour() && this.subjectItem.getTemplateId() != 831) {
                                                    this.sendNormalServerMessage("The " + this.subjectItem.getName() + " doesn't fit.");
                                                    return;
                                                }
                                                final Item thisTopParent = this.subjectItem.getTopParentOrNull();
                                                if (thisTopParent != null && (thisTopParent.getTemplateId() == 853 || thisTopParent.getTemplateId() == 1410)) {
                                                    this.sendNormalServerMessage("That needs to be on the ground to reach it.");
                                                    return;
                                                }
                                                if (subjectParent == null || subjectParent.getWurmId() != targetItem.getWurmId()) {
                                                    if (targetItem.getTemplateId() == 1432) {
                                                        this.sendNormalServerMessage("You can't put that there.");
                                                        return;
                                                    }
                                                    if (targetItem.getTemplateId() == 1436) {
                                                        this.sendNormalServerMessage("You can't put that there.");
                                                        return;
                                                    }
                                                    if (targetItem.getTemplateId() == 1434 && !this.subjectItem.getRealTemplate().isSeed()) {
                                                        this.sendNormalServerMessage("You can only put seeds into the feeder.");
                                                        return;
                                                    }
                                                    if (targetItem.getTemplateId() == 1435 && this.subjectItem.getRealTemplateId() != 128) {
                                                        this.sendNormalServerMessage("You can only put water into the drinker.");
                                                        return;
                                                    }
                                                    final RemoveItemQuestion riq2 = new RemoveItemQuestion(this.player, this.subjectItem.getWurmId(), targetItem.getWurmId());
                                                    riq2.sendQuestion();
                                                }
                                            }
                                            return;
                                        }
                                        else {
                                            if (this.subjectItem.isCoin() && this.subjectItem.getParentId() == -10L) {
                                                return;
                                            }
                                            if ((subjectParent == null || !subjectParent.isVehicle() || !targetItem.isVehicle()) && this.subjectItem.isNoTake() && (subjectParent == null || subjectParent.isNoTake())) {
                                                this.sendNormalServerMessage("The " + this.subjectItem.getName() + " can not be picked up.");
                                                return;
                                            }
                                            if (this.subjectItem.isHollow() && this.subjectItem.getTemplate().getInitialContainers() == null) {
                                                for (final Item i : this.subjectItem.getAllItems(true)) {
                                                    if (i.isNoTake() && i.getTemplateId() != 669 && !i.isComponentItem() && i.getTemplateId() != 1403) {
                                                        this.sendNormalServerMessage("The " + this.subjectItem.getName() + " contains a no-take item.");
                                                        return;
                                                    }
                                                }
                                            }
                                            if (this.subjectItem.getTopParentOrNull() != null && (this.subjectItem.getTopParentOrNull().getTemplateId() == 853 || this.subjectItem.getTopParentOrNull().getTemplateId() == 1410)) {
                                                this.sendNormalServerMessage("You need to use the unload action to do this.");
                                                return;
                                            }
                                            if (!this.equipCreatureCheck(targetItem)) {
                                                this.sendNormalServerMessage("You are not allowed to do that.");
                                                return;
                                            }
                                            if (!this.creatureWearableRestrictions(targetItem)) {
                                                return;
                                            }
                                            final boolean lastMove = s == nums - 1;
                                            final Item subjectTopParent = this.subjectItem.getTopParentOrNull();
                                            for (final Item item3 : subjectTopParent.getAllItems(true)) {
                                                if (item3.getTemplateId() == 1436 && !item3.isEmpty(true)) {
                                                    if (this.subjectItem.getParent().getTemplateId() == 1434) {
                                                        this.sendNormalServerMessage("The chickens refuse to let you take the food, perhaps you should remove them first.");
                                                        return;
                                                    }
                                                    if (this.subjectItem.getParent().getTemplateId() == 1435) {
                                                        this.sendNormalServerMessage("The chickens refuse to let you take the water, perhaps you should remove them first.");
                                                        return;
                                                    }
                                                }
                                            }
                                            if (this.subjectItem.moveToItem(this.player, targetItem.getWurmId(), lastMove)) {
                                                ++moved;
                                                this.sendUpdateInventoryItem(this.subjectItem);
                                                if (subjectTopParent != null && lastMove && subjectTopParent.getTemplateId() != 177) {
                                                    subjectTopParent.updateModelNameOnGroundItem();
                                                }
                                                if ((targetItem.getTemplateId() == 1312 || targetItem.getTemplateId() == 1315 || targetItem.getTemplateId() == 1309) && lastMove) {
                                                    targetItem.updateModelNameOnGroundItem();
                                                }
                                            }
                                        }
                                    }
                                    else if (held) {
                                        if (!this.subjectItem.canBeDropped(true)) {
                                            if (targetItem.getOwnerId() == -10L || targetItem.getOwnerId() != this.player.getWurmId()) {
                                                this.sendSafeServerMessage("You are not allowed to drop that.");
                                            }
                                            else if (this.subjectItem.isBulkContainer() && targetItem.getTemplateId() != 1315) {
                                                this.sendSafeServerMessage("You can't move that around.");
                                            }
                                            else if (this.subjectItem.moveToItem(this.player, targetId, s == nums - 1)) {
                                                ++moved;
                                                Item itemParent = null;
                                                try {
                                                    itemParent = this.subjectItem.getParent();
                                                }
                                                catch (NoSuchItemException ex4) {}
                                                for (final Creature c : (itemParent != null) ? itemParent.getWatchers() : this.subjectItem.getWatchers()) {
                                                    if (c.isPlayer()) {
                                                        c.getCommunicator().sendUpdateInventoryItem(this.subjectItem);
                                                    }
                                                }
                                            }
                                        }
                                        else if (targetItem.getOwnerId() != -10L && targetItem.getOwnerId() != this.player.getWurmId()) {
                                            if ((this.subjectItem.isHollow() && this.subjectItem.getTemplateId() != 621 && targetItem.getTemplateId() != 621 && !targetItem.isHollow()) || (this.subjectItem.isLiquid() && !targetItem.isContainerLiquid())) {
                                                this.sendSafeServerMessage("You are not allowed to equip that.");
                                            }
                                            else {
                                                try {
                                                    final Creature owner = Server.getInstance().getCreature(targetItem.getOwnerId());
                                                    if (owner.isDead()) {
                                                        return;
                                                    }
                                                    Label_7611: {
                                                        if (this.equipCreatureCheck(targetItem)) {
                                                            if (!this.creatureWearableRestrictions(targetItem)) {
                                                                return;
                                                            }
                                                            Label_7009: {
                                                                if (targetItem.getTemplateId() == 621 && targetItem.isHollow()) {
                                                                    if (owner.getDominator() != this.player) {
                                                                        if (!owner.mayAccessHold(this.player)) {
                                                                            break Label_7009;
                                                                        }
                                                                    }
                                                                    try {
                                                                        if (targetItem.testInsertItem(this.subjectItem)) {
                                                                            this.subjectItem.getParent().dropItem(this.subjectItem.getWurmId(), false);
                                                                            targetItem.insertItem(this.subjectItem);
                                                                            if (!this.subjectItem.isRiftLoot()) {
                                                                                this.subjectItem.setLastMaintained(WurmCalendar.currentTime);
                                                                            }
                                                                        }
                                                                        else {
                                                                            playerComm.sendNormalServerMessage(this.subjectItem.getNameWithGenus() + " won't fit in " + targetItem.getNameWithGenus() + ".");
                                                                        }
                                                                    }
                                                                    catch (NoSuchItemException nsi3) {
                                                                        logWarn(this.player.getName() + " moving " + this.subjectItem.getName() + " to " + targetItem.getName());
                                                                    }
                                                                    break Label_7611;
                                                                }
                                                            }
                                                            if (owner.isKingdomGuard() || owner.getDominator() == this.player || owner.mayAccessHold(this.player)) {
                                                                if (this.subjectItem.isUnfinished()) {
                                                                    this.sendNormalServerMessage(owner.getName() + " gives the unfinished item back with a shrug.");
                                                                    return;
                                                                }
                                                                if (targetItem.testInsertItem(this.subjectItem)) {
                                                                    if (this.subjectItem.moveToItem(this.player, targetItem.getWurmId(), s == nums - 1)) {
                                                                        ++moved;
                                                                        this.sendUpdateInventoryItem(this.subjectItem);
                                                                    }
                                                                }
                                                                else if (targetItem.getTemplateId() != 16) {
                                                                    try {
                                                                        final Item targetParent2 = targetItem.getParent();
                                                                        if (targetParent2.testInsertItem(this.subjectItem)) {
                                                                            boolean found = false;
                                                                            if (targetItem.isBodyPart()) {
                                                                                found = false;
                                                                                for (int x2 = 0; x2 < this.subjectItem.getBodySpaces().length; ++x2) {
                                                                                    if (this.subjectItem.getBodySpaces()[x2] == targetParent2.getPlace()) {
                                                                                        found = true;
                                                                                    }
                                                                                }
                                                                                if (!found && owner.hasHands() && (targetParent2.getPlace() == 13 || targetParent2.getPlace() == 14)) {
                                                                                    found = true;
                                                                                }
                                                                            }
                                                                            if (found) {
                                                                                try {
                                                                                    this.subjectItem.getParent().dropItem(this.subjectItem.getWurmId(), false);
                                                                                    targetParent2.insertItem(this.subjectItem);
                                                                                    if (!this.subjectItem.isRiftLoot()) {
                                                                                        this.subjectItem.setLastMaintained(WurmCalendar.currentTime);
                                                                                    }
                                                                                }
                                                                                catch (NoSuchItemException nsi4) {
                                                                                    logWarn(this.player.getName() + " moving " + this.subjectItem.getName() + " to " + targetItem.getName());
                                                                                }
                                                                            }
                                                                            else {
                                                                                playerComm.sendNormalServerMessage(this.subjectItem.getNameWithGenus() + " won't fit in " + targetItem.getNameWithGenus() + '.');
                                                                            }
                                                                        }
                                                                        else {
                                                                            playerComm.sendNormalServerMessage(this.subjectItem.getNameWithGenus() + " won't fit in " + targetItem.getNameWithGenus() + '.');
                                                                        }
                                                                    }
                                                                    catch (NoSuchItemException nsi3) {
                                                                        logWarn(this.player.getName() + " moving " + this.subjectItem.getName() + " to " + targetItem.getName());
                                                                    }
                                                                }
                                                            }
                                                            else {
                                                                this.sendNormalServerMessage(owner.getName() + " will not accept that.");
                                                            }
                                                        }
                                                        else if (!targetItem.mayCreatureInsertItem()) {
                                                            this.sendNormalServerMessage("The " + targetItem.getName() + " contains too many items.");
                                                        }
                                                        else {
                                                            this.sendNormalServerMessage("You are not allowed to do that.");
                                                        }
                                                    }
                                                }
                                                catch (NoSuchCreatureException nsc) {
                                                    logWarn(this.player.getName() + " trying to move item to nonexistant creature.");
                                                }
                                                catch (NoSuchPlayerException nsc2) {
                                                    logWarn(this.player.getName() + " trying to move item to nonexistant player.");
                                                }
                                            }
                                        }
                                        else {
                                            if (targetItem.getTopParent() != this.player.getVehicle() && targetItem.getWurmId() != this.player.getStatus().getInventoryId() && !this.player.isWithinTileDistanceTo((int)targetItem.getPosX() >> 2, (int)targetItem.getPosY() >> 2, (int)targetItem.getPosZ() >> 2, 3)) {
                                                this.sendNormalServerMessage("You cannot reach the " + targetItem.getName() + " now.");
                                                return;
                                            }
                                            if (this.subjectItem.isBulkContainer() && targetItem.getTemplateId() != 1315) {
                                                this.sendSafeServerMessage("You can't move that around.");
                                            }
                                            final Item topParent2 = this.subjectItem.getTopParentOrNull();
                                            final boolean lastMove2 = s == nums - 1;
                                            if (this.subjectItem.moveToItem(this.player, targetItem.getWurmId(), lastMove2)) {
                                                ++moved;
                                                this.sendUpdateInventoryItem(this.subjectItem);
                                                if (topParent2 != null && lastMove2 && topParent2.getTemplateId() != 177) {
                                                    topParent2.updateModelNameOnGroundItem();
                                                }
                                            }
                                            if ((targetItem.getTemplateId() == 1312 || targetItem.getTemplateId() == 1315) && lastMove2 && moved > 0) {
                                                targetItem.updateModelNameOnGroundItem();
                                            }
                                        }
                                    }
                                    else {
                                        try {
                                            final Creature owner = Server.getInstance().getCreature(subjectOwnerId);
                                            if (owner.isDead()) {
                                                return;
                                            }
                                            boolean ok = owner.isKingdomGuard() && owner.getKingdomId() == this.player.getKingdomId();
                                            if (!Servers.isThisAPvpServer() && owner.isBranded()) {
                                                ok = (ok || owner.mayAccessHold(this.player));
                                            }
                                            else {
                                                ok = (ok || owner.getDominator() == this.player);
                                            }
                                            if (ok && targetItem.getOwnerId() != this.player.getWurmId()) {
                                                try {
                                                    final Creature targetOwner = Server.getInstance().getCreature(targetItem.getOwner());
                                                    if (targetOwner.isKingdomGuard() && targetOwner.getKingdomId() != this.player.getKingdomId()) {
                                                        ok = false;
                                                    }
                                                    else if (!Servers.isThisAPvpServer() && targetOwner.isBranded()) {
                                                        ok = targetOwner.mayAccessHold(this.player);
                                                    }
                                                    else {
                                                        ok = (targetOwner.getDominator() == this.player);
                                                    }
                                                }
                                                catch (NotOwnedException ex5) {}
                                            }
                                            if ((ok || (owner.isReborn() && owner.isHuman())) && targetItem.mayCreatureInsertItem()) {
                                                if (!this.equipCreatureCheck(targetItem)) {
                                                    this.sendNormalServerMessage("You are not allowed to do that.");
                                                    return;
                                                }
                                                if (!this.creatureWearableRestrictions(targetItem)) {
                                                    return;
                                                }
                                                if (this.subjectItem.moveToItem(this.player, targetItem.getWurmId(), s == nums - 1)) {
                                                    ++moved;
                                                    this.sendUpdateInventoryItem(this.subjectItem);
                                                }
                                                else {
                                                    final long itemOwnerId = targetItem.getOwnerId();
                                                    if (ok && (itemOwnerId == -10L || itemOwnerId == this.player.getWurmId() || itemOwnerId == subjectOwnerId)) {
                                                        if (itemOwnerId == -10L) {
                                                            if (targetItem.isBulkItem() || targetItem.isBulkContainer()) {
                                                                this.sendNormalServerMessage("You are not allowed to do that.");
                                                                return;
                                                            }
                                                            if (targetItem.isLocked()) {
                                                                playerComm.sendNormalServerMessage("The " + targetItem.getName() + " is locked.");
                                                                return;
                                                            }
                                                        }
                                                        if (targetItem.testInsertItem(this.subjectItem)) {
                                                            boolean found2 = false;
                                                            if (targetItem.isBodyPart()) {
                                                                found2 = false;
                                                                for (int x3 = 0; x3 < this.subjectItem.getBodySpaces().length; ++x3) {
                                                                    if (this.subjectItem.getBodySpaces()[x3] == targetItem.getPlace()) {
                                                                        found2 = true;
                                                                    }
                                                                }
                                                                if (!found2 && (targetItem.getPlace() == 13 || targetItem.getPlace() == 14)) {
                                                                    found2 = true;
                                                                }
                                                            }
                                                            if (found2) {
                                                                if (this.subjectItem.isLiquid() && !targetItem.isContainerLiquid()) {
                                                                    playerComm.sendNormalServerMessage("The " + this.subjectItem.getName() + " wont fit in " + targetItem.getNameWithGenus() + '.');
                                                                }
                                                                else {
                                                                    try {
                                                                        this.subjectItem.getParent().dropItem(this.subjectItem.getWurmId(), false);
                                                                        targetItem.insertItem(this.subjectItem);
                                                                        if (!this.subjectItem.isRiftLoot()) {
                                                                            this.subjectItem.setLastMaintained(WurmCalendar.currentTime);
                                                                        }
                                                                    }
                                                                    catch (NoSuchItemException nsi5) {
                                                                        logWarn(this.player.getName() + " moving " + this.subjectItem.getName() + " to " + targetItem.getName());
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        else if (targetItem.getTemplateId() == 621) {
                                                            playerComm.sendNormalServerMessage(this.subjectItem.getNameWithGenus() + " wont fit in " + targetItem.getNameWithGenus() + '.');
                                                        }
                                                        else if (targetItem.getTemplateId() != 0 && targetItem.getTemplateId() != 16) {
                                                            try {
                                                                final Item targetParent3 = targetItem.getParent();
                                                                if (targetParent3.testInsertItem(this.subjectItem)) {
                                                                    boolean found3 = false;
                                                                    if (targetItem.isBodyPart()) {
                                                                        found3 = false;
                                                                        for (int x4 = 0; x4 < this.subjectItem.getBodySpaces().length; ++x4) {
                                                                            if (this.subjectItem.getBodySpaces()[x4] == targetParent3.getPlace()) {
                                                                                found3 = true;
                                                                            }
                                                                        }
                                                                        if (!found3 && (targetParent3.getPlace() == 13 || targetParent3.getPlace() == 14)) {
                                                                            found3 = true;
                                                                        }
                                                                    }
                                                                    if (found3) {
                                                                        try {
                                                                            this.subjectItem.getParent().dropItem(this.subjectItem.getWurmId(), false);
                                                                            targetParent3.insertItem(this.subjectItem);
                                                                            if (!this.subjectItem.isRiftLoot()) {
                                                                                this.subjectItem.setLastMaintained(WurmCalendar.currentTime);
                                                                            }
                                                                        }
                                                                        catch (NoSuchItemException nsi6) {
                                                                            logWarn(this.player.getName() + " moving " + this.subjectItem.getName() + " to " + targetItem.getName());
                                                                        }
                                                                    }
                                                                    else {
                                                                        playerComm.sendNormalServerMessage(this.subjectItem.getNameWithGenus() + " wont fit in " + targetItem.getNameWithGenus() + '.');
                                                                    }
                                                                }
                                                                else {
                                                                    playerComm.sendNormalServerMessage(this.subjectItem.getNameWithGenus() + " wont fit in " + targetItem.getNameWithGenus() + '.');
                                                                }
                                                            }
                                                            catch (NoSuchItemException nsi7) {
                                                                logWarn(this.player.getName() + " moving " + this.subjectItem.getName() + " to " + targetItem.getName());
                                                            }
                                                        }
                                                    }
                                                    else {
                                                        this.sendNormalServerMessage("You can't reach that.");
                                                    }
                                                }
                                            }
                                            else if (!targetItem.mayCreatureInsertItem()) {
                                                this.sendNormalServerMessage("The " + targetItem.getName() + " contains too many items.");
                                            }
                                            else {
                                                this.sendNormalServerMessage("You can't reach that.");
                                            }
                                        }
                                        catch (NoSuchCreatureException nsc) {
                                            logWarn(this.player.getName() + " trying to move item from nonexistant creature.");
                                        }
                                        catch (NoSuchPlayerException nsc2) {
                                            logWarn(this.player.getName() + " trying to move item from nonexistant player.");
                                        }
                                    }
                                }
                                catch (NoSuchItemException nsi2) {
                                    this.sendNormalServerMessage(nsi2.getMessage());
                                    logWarn(this.player.getName() + "- moving item - no such target item: " + targetId);
                                }
                            }
                        }
                        catch (NoSuchCreatureException cnf) {
                            logWarn(this.player.getName() + "- " + cnf.getMessage(), cnf);
                        }
                        catch (NoSuchPlayerException nsp) {
                            logWarn(this.player.getName() + "- " + nsp.getMessage(), nsp);
                        }
                    }
                }
            }
        }
        this.handleCoins(this.coins);
        if (System.currentTimeMillis() - now > 1000L) {
            logInfo("Moving items took " + (System.currentTimeMillis() - now) + " ms for " + this.player.getName() + ". target=" + Communicator.commandAction + ", coins=" + (this.coins != null));
        }
    }
    
    private boolean equipCreatureCheck(final Item targetItem) {
        if (targetItem.getOwnerId() == -10L) {
            return true;
        }
        boolean ok = false;
        try {
            final Creature owner = Server.getInstance().getCreature(targetItem.getOwnerId());
            if (owner.isPlayer()) {
                return true;
            }
            if (owner.isDead()) {
                return false;
            }
            final Vehicle vehicle = Vehicles.getVehicle(owner);
            if (!ok && owner.isKingdomGuard() && owner.getKingdomId() == this.player.getKingdomId()) {
                ok = true;
            }
            if (targetItem.getTemplateId() == 621 || targetItem.isSaddleBags() || targetItem.isInside(1333, 1334)) {
                if (!Servers.isThisAPvpServer() && owner.isBranded()) {
                    ok = (ok || owner.mayAccessHold(this.player));
                }
                else {
                    ok = (ok || owner.getDominator() == this.player);
                }
                if (ok) {
                    ok = targetItem.mayCreatureInsertItem();
                }
            }
            if (this.subjectItem.isCreatureWearableOnly() && owner.isVehicle() && vehicle.getCanHaveEquipment()) {
                if (!Servers.isThisAPvpServer() && owner.isBranded()) {
                    ok = (ok || owner.mayAccessHold(this.player));
                }
                else {
                    ok = (ok || owner.getDominator() == this.player);
                }
            }
        }
        catch (NoSuchCreatureException nsc) {
            logWarn(this.player.getName() + " trying to move item to nonexistant creature.");
        }
        catch (NoSuchPlayerException nsc2) {
            logWarn(this.player.getName() + " trying to move item to nonexistant player.");
        }
        return ok;
    }
    
    private final boolean creatureWearableRestrictions(final Item targetItem) {
        if (targetItem.getOwnerId() == -10L) {
            return true;
        }
        if (targetItem.isSaddleBags() || targetItem.isInside(1333, 1334)) {
            return true;
        }
        try {
            final Creature owner = Server.getInstance().getCreature(targetItem.getOwnerId());
            if (owner.isPlayer()) {
                return true;
            }
            if (this.player.getVehicle() != -10L) {
                this.sendNormalServerMessage("You need to be standing on the ground to do that.");
                return false;
            }
            if (!this.subjectItem.isCreatureWearableOnly()) {
                return true;
            }
            if (this.subjectItem.getTemperature() > 400) {
                this.sendNormalServerMessage("The " + owner.getName() + " rears as the " + this.subjectItem.getName() + " is too warm.");
                return false;
            }
            if (this.subjectItem.isSaddleLarge()) {
                if (owner.getSize() <= 4) {
                    this.sendNormalServerMessage("The " + owner.getName() + " needs to use a normal saddle.");
                    return false;
                }
                if (owner.isKingdomGuard()) {
                    this.sendNormalServerMessage("The " + owner.getName() + " can not use that.");
                    return false;
                }
            }
            else if (this.subjectItem.isSaddleNormal()) {
                if (owner.getSize() > 4) {
                    this.sendNormalServerMessage("The " + owner.getName() + " needs to use a larger saddle.");
                    return false;
                }
                if (owner.isKingdomGuard()) {
                    this.sendNormalServerMessage("The " + owner.getName() + " can not use that.");
                    return false;
                }
            }
            else if (this.subjectItem.isHorseShoe()) {
                if (!owner.isHorse() && (!owner.isUnicorn() || (this.subjectItem.getMaterial() != 7 && this.subjectItem.getMaterial() != 8 && this.subjectItem.getMaterial() != 96))) {
                    this.sendNormalServerMessage("The " + owner.getName() + " can not use that.");
                    return false;
                }
            }
            else if (this.subjectItem.isBarding() && !owner.isHorse()) {
                this.sendNormalServerMessage("The " + owner.getName() + " can not use that.");
                return false;
            }
        }
        catch (NoSuchCreatureException nsc) {
            logWarn(this.player.getName() + " trying to move item to nonexistant creature.");
            return false;
        }
        catch (NoSuchPlayerException nsc2) {
            logWarn(this.player.getName() + " trying to move item to nonexistant player.");
            return false;
        }
        return true;
    }
    
    private final void handleCoins(final Set<Item> coins) throws IOException {
        if (coins != null) {
            int val = 0;
            for (final Item c : coins) {
                if (c.isCoin()) {
                    val += Economy.getValueFor(c.getTemplateId());
                }
            }
            if (this.player.addMoney(val)) {
                for (final Item c : coins) {
                    if (c.isCoin()) {
                        try {
                            final Item parent = c.getParent();
                            parent.dropItem(c.getWurmId(), false);
                            c.setOwnerId(this.player.getWurmId());
                            Economy.getEconomy().returnCoin(c, "Banked");
                        }
                        catch (NoSuchItemException nsi) {
                            logWarn(c.getName() + " had no parent when banking?");
                            this.player.getCommunicator().sendNormalServerMessage(c.getName() + " does not belong to you. Report this as an error.");
                            coins.clear();
                            return;
                        }
                    }
                }
                final Change c2 = new Change(val);
                this.sendNormalServerMessage("Deposited " + c2.getChangeString() + '.');
                final Change change = Economy.getEconomy().getChangeFor(this.player.getMoney());
                this.sendNormalServerMessage("You now have " + change.getChangeString() + " in the bank.");
                this.sendNormalServerMessage("If this amount is incorrect, please wait a while since the information may not immediately be updated.");
                logInfo(this.player.getName() + " deposited " + c2.getChangeString() + " and now has " + change.getChangeString());
            }
            else {
                this.sendNormalServerMessage("Your bank is not available at the moment. Please try later.");
            }
            coins.clear();
        }
    }
    
    private void reallyHandle_CMD_EQUIP_ITEM(final ByteBuffer byteBuffer) throws NoSuchPlayerException, NoSuchCreatureException, NoSuchBehaviourException, NoSuchWallException, FailedException {
        final int nums = byteBuffer.getShort() & 0xFFFF;
        final long[] items = new long[nums];
        for (int i = 0; i < nums; ++i) {
            items[i] = byteBuffer.getLong();
        }
        for (int i = 0; i < nums; ++i) {
            try {
                final Item item = Items.getItem(items[i]);
                final Item parent = item.getParentOrNull();
                if (parent != null) {
                    if (parent.isBodyPart()) {
                        BehaviourDispatcher.action(this.player, this, -1L, item.getWurmId(), (short)585);
                    }
                    else {
                        BehaviourDispatcher.action(this.player, this, -1L, item.getWurmId(), (short)582);
                    }
                }
            }
            catch (NoSuchItemException ex) {}
        }
    }
    
    private void reallyHandle_CMD_MESSAGE(final ByteBuffer byteBuffer) throws Exception {
        byte[] tempStringArr = new byte[byteBuffer.get() & 0xFF];
        byteBuffer.get(tempStringArr);
        String message = new String(tempStringArr, "UTF-8");
        tempStringArr = new byte[byteBuffer.get() & 0xFF];
        byteBuffer.get(tempStringArr);
        final String title = new String(tempStringArr, "UTF-8");
        Communicator.commandMessage = message;
        if (++this.messagesReceived < 2 || !this.player.isFullyLoaded() || this.player.getSecondsPlayed() < 60.0f) {
            final int power = this.player.getPower();
            if (message.charAt(0) == '#' && this.player.getPower() == 1) {
                if (message.startsWith("#invis")) {
                    this.handleHashMessageInvisible(power);
                }
                else if (message.equals("#help")) {
                    this.handleHashMessageHelp(power);
                }
                else if (message.startsWith("#chat")) {
                    this.handleHashMessageChatColour(message, power);
                }
                else if (message.startsWith("#showclientinfo")) {
                    this.handleHashMessageClientInfo(message, power);
                }
                else {
                    this.sendSafeServerMessage("Unknown command: " + message);
                }
            }
            else if (message.charAt(0) == '#' && (this.player.getPower() > 1 || this.player.mayMute())) {
                if (message.startsWith("#sdown")) {
                    this.handleHashMessageShutdown(power);
                }
                else if (message.startsWith("#allowall")) {
                    this.handleHashMessageAllowAll(power);
                }
                else if (message.startsWith("#kick")) {
                    this.handleHashMessageKick(message, power);
                }
                else if (message.startsWith("#showcreaturelist")) {
                    this.handleShowCreatureList(message, power);
                }
                else if (message.startsWith("#setreputation")) {
                    this.handleHashMessageSetReputation(message, power);
                }
                else if (message.startsWith("#toggleglobal")) {
                    this.handleHashMessageToggleGlobalChat(power);
                }
                else if (message.startsWith("#who")) {
                    this.handleHashMessageWho(message, power);
                }
                else if (message.startsWith("#getips")) {
                    this.handleHashMessageGetIps(power);
                }
                else if (message.startsWith("#getip")) {
                    this.handleHashMessageGetIP(message, power);
                }
                else if (message.startsWith("#offline")) {
                    this.handleHashMessageOffline(power);
                }
                else if (message.startsWith("#calcCreatures")) {
                    this.handleHashMessageCalcCreatures(power);
                }
                else if (message.startsWith("#alerts")) {
                    this.handleHashMessageAlerts(power);
                }
                else if (message.startsWith("#ban")) {
                    this.handleHashMessageBan(message, power);
                }
                else if (message.startsWith("#locateitem")) {
                    this.handleHashMessageLocateItem(message, power);
                }
                else if (message.startsWith("#wartargets")) {
                    this.handleHashMessageWarTargets(power);
                }
                else if (message.startsWith("#highscore")) {
                    this.handleHashMessageHighscore(message, power);
                }
                else if (message.startsWith("#meditation")) {
                    this.handleHashMessageMeditation(power);
                }
                else if (message.equals("#createportals")) {
                    this.handleHashMessageCreatePortals(power);
                }
                else if (message.startsWith("#warn")) {
                    this.handleHashMessageWarn(message, power);
                }
                else if (message.startsWith("#resetwarnings")) {
                    this.handleHashMessageResetWarnings(message, power);
                }
                else if (message.startsWith("#getwarnings")) {
                    this.handleHashMessageGetWarnings(message, power);
                }
                else if (message.startsWith("#pardonip")) {
                    this.handleHashMessagePardonIp(message, power);
                }
                else if (message.startsWith("#pardonsteamid")) {
                    this.handleHashMessagePardonSteamId(message, power);
                }
                else if (message.startsWith("#pardon")) {
                    this.handleHashMessagePardon(message, power);
                }
                else if (message.startsWith("#devtalk")) {
                    this.handleHashMessageDevTalk(message, power);
                }
                else if (message.startsWith("#artist")) {
                    this.handleHashMessageToggleArtist(message, power);
                }
                else if (message.startsWith("#invis")) {
                    this.handleHashMessageInvisible(power);
                }
                else if (message.startsWith("#checkeigc")) {
                    this.handleHashMessageCheckEigc(power);
                }
                else if (message.startsWith("#showbans")) {
                    this.handleHashMessageShowBans(power);
                }
                else if (message.startsWith("#locatehorse")) {
                    this.handleHashMessageLocateHorses(message, power);
                }
                else if (message.startsWith("#locateavatars")) {
                    this.handleHashMessageLocateAvatars(message, power);
                }
                else if (message.startsWith("#locate")) {
                    this.handleHashMessageLocate(message, power);
                }
                else if (message.startsWith("#boats")) {
                    this.handleHashMessageBoats(power);
                }
                else if (message.startsWith("#buildinfo") && !Server.getInstance().isPS()) {
                    this.handleHashMessageBuildInfo();
                }
                else if (message.startsWith("#getAchievementData")) {
                    this.handleHashMessageGetAchievementData(message, power);
                }
                else if (message.startsWith("#showPersonalGoals")) {
                    this.handleHashMessageShowPersonalGoals(message, power);
                }
                else if (message.startsWith("#creaturepos")) {
                    this.handleHashMessageCreaturePos(power);
                }
                else if (message.startsWith("#loadItem")) {
                    this.handleHashMessageLoadItemFromHell(message, power);
                }
                else if (message.equals("#enableboats")) {
                    this.handleHashMessageEnableBoats(power);
                }
                else if (message.equals("#slate")) {
                    this.handleHashMessageSlate(power);
                }
                else if (message.startsWith("#startx")) {
                    this.handleHashMessageStartX(message, power);
                }
                else if (message.startsWith("#starty")) {
                    this.handleHashMessageStartY(message, power);
                }
                else if (message.startsWith("#gm ")) {
                    this.handleHashMessageGm(message, power);
                }
                else if (message.startsWith("#resetplayer")) {
                    this.handleHashMessageResetPlayer(message, power);
                }
                else if (message.startsWith("#toggleEpic")) {
                    this.handleHashMessageToggleEpic(power);
                }
                else if (message.startsWith("#xmaslight")) {
                    this.handleHashMessageXmasLight(power);
                }
                else if (message.startsWith("#noxmas")) {
                    this.handleHashMessageNoXmas(power);
                }
                else if (message.startsWith("#mac")) {
                    this.handleHashMessageMac(message, power);
                }
                else if (message.equals("#resetGuards")) {
                    this.handleHashMessageResetGuards(power);
                }
                else if (message.startsWith("#startlibx")) {
                    this.handleHashMessageStartLibX(message, power);
                }
                else if (message.startsWith("#addfakemo")) {
                    this.handleHashMessageAddFakeMoney(message, power);
                }
                else if (message.startsWith("#startliby")) {
                    this.handleHashMessageStartLibY(message, power);
                }
                else if (message.startsWith("#setstartmolrehan")) {
                    this.handleHashMessageSetStartMolRehan(power);
                }
                else if (message.startsWith("#startmolx")) {
                    this.handleHashMessageStartMolX(message, power);
                }
                else if (message.startsWith("#startmoly")) {
                    this.handleHashMessageStartMolY(message, power);
                }
                else if (message.startsWith("#plimit")) {
                    this.handleHashMessagePLimit(message, power);
                }
                else if (message.startsWith("#itempos")) {
                    this.handleHashMessageItemPosition(message, power);
                }
                else if (message.startsWith("#mutewarn")) {
                    this.handleHashMessageMuteWarn(message, power);
                }
                else if (message.startsWith("#checkAff")) {
                    this.handleHashMessageCheckAffinity(message, power);
                }
                else if (message.startsWith("#mute")) {
                    this.handleHashMessageMute(message, power);
                }
                else if (message.startsWith("#unmute")) {
                    this.handleHashMessageUnmute(message, power);
                }
                else if (message.startsWith("#showmuters")) {
                    this.handleHashMessageShowMuters(power);
                }
                else if (message.startsWith("#showdevtalkers")) {
                    this.handleHashMessageShowDevTalkers(power);
                }
                else if (message.startsWith("#showcas")) {
                    this.handleHashMessageShowCas(power);
                }
                else if (message.startsWith("#showhero")) {
                    this.handleHashMessageShowHeros(message, power);
                }
                else if (message.startsWith("#showmuted")) {
                    this.handleHashMessageShowMuted(power);
                }
                else if (message.startsWith("#printranks")) {
                    this.handleHashMessagePrintRanks(power);
                }
                else if (message.startsWith("#setmuter")) {
                    this.handleHashMessageSetMuter(message, power);
                }
                else if (message.startsWith("#toggleca")) {
                    this.handleHashMessageToggleCA(message);
                }
                else if (message.startsWith("#changepassword")) {
                    this.handleHashMessageChangePassword(message, power);
                }
                else if (message.startsWith("#changeemail")) {
                    this.handleHashMessageChangeEmail(message, power);
                }
                else if (message.startsWith("#testemail")) {
                    this.handleMessageSendTestLetter(message);
                }
                else if (message.equals("#invuln")) {
                    this.handleHashMessageInvulnerable(power);
                }
                else if (message.startsWith("#chat")) {
                    this.handleHashMessageChatColour(message, power);
                }
                else if (message.equals("#uniques")) {
                    this.handleHashMessageUniques(power);
                }
                else if (message.startsWith("#rename")) {
                    this.handleHashMessageRename(message, power);
                }
                else if (message.equals("#help")) {
                    this.handleHashMessageHelp(power);
                }
                else if (message.toLowerCase().startsWith("#testcolors")) {
                    this.handleHashMessageTestColors(message, power);
                }
                else if (message.equals("#testAffinity")) {
                    this.handleHashMessageTestAffinity(power);
                }
                else if (message.startsWith("#testFragments")) {
                    this.handleHashMessageTestFragments(message, power);
                }
                else if (message.startsWith("#b ") || message.startsWith("#br ") || message.startsWith("#broadcast ")) {
                    this.handleHashMessageBroadcast(message, power);
                }
                else if (message.startsWith("#a ") || message.startsWith("#ann ") || message.startsWith("#announce ")) {
                    this.handleHashMessageAnnounce(message, power);
                }
                else if (message.startsWith("#respawn")) {
                    this.handleHashMessageRespawn(message, power);
                }
                else if (message.startsWith("#redeem")) {
                    this.handleHashMessageRedeem(power);
                }
                else if (message.startsWith("#addtitle")) {
                    this.handleHashMessageAddTitle(message, power);
                }
                else if (message.startsWith("#removetitle")) {
                    this.handleHashMessageRemoveTitle(message, power);
                }
                else if (message.startsWith("#reload ")) {
                    this.handleHashMessageReload(message, power);
                }
                else if (message.startsWith("#playerstatuses")) {
                    this.handleHashMessagePlayerStatuses(power);
                }
                else if (message.startsWith("#testweb")) {
                    this.handleHashMessageTestWeb(message, power);
                }
                else if (message.startsWith("#worth")) {
                    this.handleHashMessageWorth(message, power);
                }
                else if (message.startsWith("#findboat")) {
                    this.handleHashMessageFindBoat(message, power);
                }
                else if (message.startsWith("#forceRiftLoot")) {
                    this.handleHashMessageForceRiftLoot(message, power);
                }
                else if (message.startsWith("#findfish") && power == 5) {
                    this.handleHashMessageFindFish(message, true);
                }
                else if (message.startsWith("#findoldfish") && power == 5 && Servers.isThisATestServer()) {
                    this.handleHashMessageFindFish(message, false);
                }
                else if (message.startsWith("#timemod")) {
                    this.handleHashMessageTimeMod(message, power);
                }
                else if (message.startsWith("#addmoney")) {
                    this.handleHashMessageAddMoney(message, power);
                }
                else if (message.startsWith("#addreimb")) {
                    this.handleHashMessageAddReimbursement(message, power);
                }
                else if (message.startsWith("#lagstatus")) {
                    this.handleHashMessageLagStatus(power);
                }
                else if (message.startsWith("#vespeed")) {
                    this.handleHashMessageVeSpeed(message, power);
                }
                else if (message.startsWith("#onfire")) {
                    this.handleHashMessageOnFire(power);
                }
                else if (message.startsWith("#checkItems")) {
                    this.handleHashMessageCheckItems(message, power);
                }
                else if (message.startsWith("#reloadItems")) {
                    this.handleHashMessageReloadItems(message, power);
                }
                else if (message.startsWith("#checkCreatures")) {
                    this.handleHashMessageCheckCreatures(message, power);
                }
                else if (message.startsWith("#checkZones")) {
                    this.handleHashMessageCheckZones(power);
                }
                else if (message.startsWith("#resendw")) {
                    PendingAccount.resendMails("wurmonline.com");
                }
                else if (message.startsWith("#resendgmail")) {
                    PendingAccount.resendMails("gmail.com");
                }
                else if (message.startsWith("#resend")) {
                    final StringTokenizer tokens = new StringTokenizer(message);
                    tokens.nextToken();
                    String next = null;
                    if (tokens.hasMoreTokens()) {
                        next = tokens.nextToken();
                    }
                    PendingAccount.resendMails(next);
                }
                else if (message.startsWith("#kips")) {
                    this.handleHashMessageBannedIps(power);
                }
                else if (message.startsWith("#overrideshop")) {
                    this.handleHashMessageOverrideShop(message, power);
                }
                else if (message.startsWith("#setserver")) {
                    this.handleHashMessageSetServer(message, power);
                }
                else if (message.startsWith("#readlog")) {
                    this.handleHashMessageReadLog(message, power);
                }
                else if (message.startsWith("#togglemounts")) {
                    this.handleHashMessageToggleMounts(power);
                }
                else if (message.startsWith("#now")) {
                    this.handleHashMessageNow();
                }
                else if (message.startsWith("#date")) {
                    this.handleHashMessageRealTime(message);
                }
                else if (message.startsWith("#wurmdate")) {
                    this.handleHashMessageWurmDate(message);
                }
                else if (message.startsWith("#harvest") && this.player.getPower() > 2) {
                    this.handleHashMessageHarvest(message);
                }
                else if (!message.startsWith("#respondingto ") || !this.handleHashMessageRespond(message)) {
                    if (message.startsWith("#logTilePoll")) {
                        this.handleHashMessageTilePollLog(this.player.getPower());
                    }
                    else if (message.startsWith("#watch ")) {
                        this.handleHashMessageWatchPlayer(message);
                    }
                    else if (message.startsWith("#tthrow")) {
                        this.testThrow();
                    }
                    else if (message.startsWith("#tdeitycount")) {
                        Deities.calculateFaiths();
                        for (final Deity d : Deities.getDeities()) {
                            this.sendNormalServerMessage(d.getName() + " kingdom=" + Deities.getFavoredKingdom(d.getNumber()));
                        }
                    }
                    else if (message.equals("#cpollog")) {
                        Creatures.getInstance().togglePollTaskLog();
                        Creature.getPF().toggleLog();
                        Creature.getPFA().toggleLog();
                        Creature.getPFNPC().toggleLog();
                    }
                    else if (message.startsWith("#newmission")) {
                        this.handleHashMessageNeMission(this.player.getPower(), message);
                    }
                    else if (message.startsWith("#togglemission")) {
                        this.handleHashMessageToggleEnableMission(this.player.getPower(), message);
                    }
                    else if (message.startsWith("#dumpxml")) {
                        this.handleHashMessageDumpXml(this.player.getPower());
                    }
                    else if (message.startsWith("#toggleqa")) {
                        this.handleHashMessageToggleQA(power, message);
                    }
                    else if (message.startsWith("#isqa")) {
                        this.handleHashMessageIsQA(power, message);
                    }
                    else if (message.startsWith("#maxcreatures")) {
                        this.handleHashMessageMaxCreatures(power, message);
                    }
                    else if (message.startsWith("#changemodel")) {
                        this.handleHashMessageChangeModel(power, message);
                    }
                    else if (message.startsWith("#gmlight")) {
                        this.handleHashMessageGMLight(power, message);
                    }
                    else if (message.startsWith("#dumpcreatures")) {
                        this.handleCreatureDump(power);
                    }
                    else if (message.startsWith("#dumpmarkers")) {
                        this.handleMarkerDump(power);
                    }
                    else if (message.startsWith("#dumproutes")) {
                        this.handleRouteDump(power);
                    }
                    else if (message.startsWith("#dumpsurfacewater")) {
                        this.handleWaterDump(power, true);
                    }
                    else if (message.startsWith("#dumpcavewater")) {
                        this.handleWaterDump(power, false);
                    }
                    else if (message.startsWith("#dumpfishspots")) {
                        this.handleDumpFishSpots(power);
                    }
                    else if (message.startsWith("#showfishspots")) {
                        this.handleShowFishSpots(power);
                    }
                    else if (message.startsWith("#savemapdump")) {
                        this.handleMapDump(power);
                    }
                    else if (message.startsWith("#testchallenge")) {
                        this.handleMessageTestChallengeShutdown();
                    }
                    else if (message.startsWith("#toggleflag")) {
                        this.handleHashMessageToggleFlag(power, message);
                    }
                    else if (message.startsWith("#triggerAchievement")) {
                        this.handleHashMessageTriggerAchievement(power, message);
                    }
                    else if (message.toLowerCase().startsWith("#online")) {
                        this.handleHashMessageOnline(power, message);
                    }
                    else if (message.startsWith("#flattenRock") && power >= 4) {
                        this.handleHashMessageFlattenRock(power, message);
                    }
                    else if (message.startsWith("#flattenDirt") && power >= 4) {
                        this.handleHashMessageFlattenDirt(power, message);
                    }
                    else if (message.startsWith("#createInvestigates") && power >= 4) {
                        Zones.createInvestigatables();
                        this.sendNormalServerMessage("Ok. Created.");
                    }
                    else if (message.startsWith("#generateDeadVillage") && power == 5) {
                        this.handleHashMessageGenerateDeadVillage(power, message);
                    }
                    else if (message.startsWith("#addregalia")) {
                        if (power == 5) {
                            this.handleHashMessageAddRegalia(message, this.player.getPower());
                        }
                    }
                    else if (message.startsWith("#elevate")) {
                        this.handleHashMessageElevate(this.player.getPower(), message);
                    }
                    else if (message.startsWith("#give")) {
                        if (power == 5) {
                            this.handleHashMessageGive(message);
                        }
                    }
                    else if (message.startsWith("#clearappointments") && power == 5) {
                        this.handleHashMessageClearAppointments(message);
                    }
                    else if (message.startsWith("#resetappointments") && power == 5) {
                        this.handleHashMessageResetAppointments(message);
                    }
                    else if (message.startsWith("#listwildhives") && Servers.isThisATestServer()) {
                        this.handleHashMessageListWildHives(message);
                    }
                    else if (message.startsWith("#removewildhives") && Servers.isThisATestServer()) {
                        this.handleHashMessageRemoveWildHives();
                    }
                    else if (message.startsWith("#removeknownrecipes") && power == 5) {
                        this.handleHashMessageRemoveKnownRecipes(message);
                    }
                    else if (message.startsWith("#removenamedrecipe") && power == 5) {
                        this.handleHashMessageRemoveNamedRecipe(message);
                    }
                    else if (message.startsWith("#setwindpower") && power == 5 && Servers.isThisATestServer()) {
                        this.handleHashMessageWindPower(message);
                    }
                    else if (message.startsWith("#showme") && power >= 2) {
                        this.handleHashMessageShowMe(message);
                    }
                    else if (message.startsWith("#hideme") && power >= 2) {
                        this.handleHashMessageHideMe(message);
                    }
                    else if (message.startsWith("#testspecial") && power == 5) {
                        this.handleHashMessageTestSpecial(message);
                    }
                    else if (message.startsWith("#setpower") && power >= 4) {
                        this.handleHashMessageSetPower(message);
                    }
                    else if (message.startsWith("#setAffinityChance") && power == 5) {
                        this.handleHashMessageSetAffinityChance(message);
                    }
                    else if (message.equalsIgnoreCase("#getAffinityStats") && power == 5) {
                        this.handleHashMessageGetAffinityStats();
                    }
                    else if (message.startsWith("#listdens") && power >= 4) {
                        this.handleHashMessageListDens();
                    }
                    else if (message.startsWith("#removeden") && power >= 4) {
                        this.handleHashMessageRemoveDen(message);
                    }
                    else if (message.startsWith("#testTut")) {
                        PlayerTutorial.testTutorialCommand(this.player, message);
                    }
                    else if (message.startsWith("#getStoredCreatures") && power >= 4) {
                        this.handleGetStoredCreatures(message);
                    }
                    else if (message.startsWith("#testfish ") && power >= 5) {
                        this.handleTestFish(message);
                    }
                    else if (message.startsWith("#destroyAllCreatures") && power == 5) {
                        if (Servers.isThisATestServer()) {
                            this.handleDestroyAllCreatures();
                        }
                        else {
                            this.sendNormalServerMessage("This command can only be ran on a test server.");
                        }
                    }
                    else if (message.startsWith("#journalinfo") && power >= 4) {
                        this.handleHashMessageJournalInfo(message);
                    }
                    else {
                        this.sendSafeServerMessage("Unknown command: " + message);
                    }
                }
            }
            else if (message.startsWith("#mute")) {
                this.sendSafeServerMessage("You don't have that type of power: " + message);
            }
            else if (message.charAt(0) == '/' && !message.startsWith("/me ")) {
                if (message.startsWith("/tea")) {
                    this.handleMessageTeamChat(message);
                }
                else if (message.startsWith("/testTut")) {
                    PlayerTutorial.testTutorialCommand(this.player, message);
                }
                else {
                    if (message.startsWith("/t ") || message.startsWith("/tell ") || message.startsWith("/te")) {
                        this.handlePMs(message);
                        return;
                    }
                    if (message.startsWith("/v ") || message.startsWith("/vi ") || message.startsWith("/village ")) {
                        this.handleMessageVillageChat(message);
                    }
                    else if (this.isVillageInviteMessage(message)) {
                        this.handleVillageInviteMessage(message);
                    }
                    else if (message.startsWith("/recruit ")) {
                        this.handleRecruitMessage(message);
                    }
                    else if (message.startsWith("/unrecruit ")) {
                        this.handleUnRecruiteMessage(message);
                    }
                    else if (message.equalsIgnoreCase("/listrecruits")) {
                        this.handleListRecruites();
                    }
                    else if (message.startsWith("/join ")) {
                        this.handleJoinVillageMessage(message);
                    }
                    else if (message.equals("/vteleport")) {
                        this.handleVillageTeleportMessage(message);
                    }
                    else if (message.equals("/poll")) {
                        this.handleInGameVoting();
                    }
                    else if (message.startsWith("/all") || message.startsWith("/alliance ")) {
                        this.handleMessageAllianceChat(message);
                    }
                    else if (message.startsWith("/afk")) {
                        this.handleMessageAFK(message);
                    }
                    else if (message.startsWith("/addfriend")) {
                        this.handleMessageAddFriend(message);
                    }
                    else if (message.startsWith("/tweet ")) {
                        this.handleMessageTweet(message);
                    }
                    else if (message.startsWith("/support")) {
                        this.handleMessageSupportChat(message);
                    }
                    else if (message.startsWith("/dev")) {
                        this.sendNormalServerMessage("Use /support instead.");
                    }
                    else if (message.startsWith("/fl")) {
                        this.handleMessageFightLevel();
                    }
                    else if (message.startsWith("/suicide")) {
                        this.handleMessageSuicide();
                    }
                    else if (message.startsWith("/ks")) {
                        this.sendNormalServerMessage("In future please use /gshout instead.");
                    }
                    else if (message.startsWith("/gs")) {
                        this.handleMessageKingdomChat(message);
                    }
                    else if (message.startsWith("/s ") || message.startsWith("/shout") || message.startsWith("/sh ")) {
                        this.handleMessageShout(message);
                    }
                    else if (message.startsWith("/gm ")) {
                        this.handleMessageGameMasterChat(message);
                    }
                    else if (message.startsWith("/openchat ")) {
                        this.handleMessageOpenChat(message);
                    }
                    else if (message.equals("/fatigue")) {
                        this.handleMessageFatigue();
                    }
                    else if (message.equals("/time")) {
                        this.sendNormalServerMessage(WurmCalendar.getTime());
                        if (Servers.localServer.isChallengeServer()) {
                            this.sendNormalServerMessage("This Challenge ends in " + Server.getTimeFor(Servers.localServer.getChallengeEnds() - System.currentTimeMillis()) + " and the server is reset.");
                        }
                    }
                    else if (message.equals("/playtime")) {
                        this.handleMessageTimePlayed();
                    }
                    else if (message.equals("/who")) {
                        this.handleMessageWho();
                    }
                    else if (message.startsWith("/kchat")) {
                        this.sendNormalServerMessage("Please toggle using your Profile.");
                    }
                    else if (message.startsWith("/gchat")) {
                        this.sendNormalServerMessage("Please toggle using your Profile.");
                    }
                    else if (message.equals("/respawn")) {
                        this.handleMessageRespawn();
                    }
                    else if (message.equals("/attackers")) {
                        this.handleMessageAttackers();
                    }
                    else if (message.startsWith("/weat")) {
                        this.handleMessageWeather();
                    }
                    else if (message.equals("/stuck")) {
                        this.handleMessageStuck();
                    }
                    else if (message.equals("/converts")) {
                        this.handleMessageConverts();
                    }
                    else if (message.equals("/lives")) {
                        this.handleMessageLives();
                    }
                    else if (message.equals("/title")) {
                        this.handleMessageTitle();
                    }
                    else if (message.equals("/sleep")) {
                        this.handleMessageSleepBonus();
                    }
                    else if (message.equals("/titles")) {
                        this.handleMessageTitleSelect();
                    }
                    else if (message.startsWith("/uptime")) {
                        this.handleMessageUptime();
                    }
                    else if (message.startsWith("/vote ")) {
                        this.handleMessageVote(message);
                    }
                    else if (message.equals("/help") || message.equals("/?")) {
                        this.handleMessageHelp();
                    }
                    else if (message.startsWith("/mission")) {
                        this.handleMessageMissionInformation();
                    }
                    else if (message.startsWith("/fsleep")) {
                        this.handleMessageToggleSleep();
                    }
                    else if (message.startsWith("/kingdoms")) {
                        this.sendZones();
                    }
                    else if (message.startsWith("/challenge")) {
                        this.handleMessageChallengeKing();
                    }
                    else if (message.startsWith("/revoke")) {
                        this.handleMessageRevokeVillage(message);
                    }
                    else if (message.startsWith("/remove ")) {
                        this.handleMessageRemoveFriend(message);
                    }
                    else if (message.startsWith("/random")) {
                        this.handleMessageRandomNumber(message);
                    }
                    else if (message.startsWith("/part")) {
                        this.handleHashMessageParticipation(message);
                    }
                    else if (message.startsWith("/rift")) {
                        this.handleHashMessageRiftParticipation(message, power);
                    }
                    else if (message.startsWith("/release corpse")) {
                        this.handleMessageReleaseCorpses();
                    }
                    else if (message.startsWith("/champs")) {
                        this.handleMessageChampRanks();
                    }
                    else if (message.startsWith("/ranks")) {
                        this.handleMessageBattleRanks();
                    }
                    else if (message.startsWith("/maxranks")) {
                        this.handleMessageMaxBattleRanks();
                    }
                    else if (message.startsWith("/rank")) {
                        this.handleMessageBattleRank();
                    }
                    else if (message.startsWith("/lotime")) {
                        this.handleMessageLogoutTime();
                    }
                    else if (message.startsWith("/reputation")) {
                        this.handleMessageReputation();
                    }
                    else if (message.startsWith("/invitations")) {
                        this.handleMessageToggleInvitations();
                    }
                    else if (message.startsWith("/password")) {
                        this.handleMessageChangePassword(message);
                    }
                    else if (message.startsWith("/ignor")) {
                        this.handleMessageIgnore(message);
                    }
                    else if (message.startsWith("/snipe")) {
                        this.handleMessageSnipe(message);
                    }
                    else if (message.equals("/stopcaring")) {
                        this.handleMessageStopCaring();
                    }
                    else if (message.equals("/caringfor")) {
                        this.handleMessageShowCaringFor();
                    }
                    else if (message.equals("/vc") || message.equals("/voicechat")) {
                        Methods.sendVoiceChatQuestion(this.player);
                    }
                    else if (message.equals("/transfer")) {
                        this.handleMessageTransfer();
                    }
                    else if (message.toLowerCase().equals("/ca")) {
                        this.sendNormalServerMessage("In future, Please toggle using your Profile.");
                        this.handleMessageToggleCommunityAssistant();
                    }
                    else if (message.startsWith("/warnings")) {
                        this.handleMessageWarnings();
                    }
                    else if (message.startsWith("/sign")) {
                        this.handleMessageSignInOut(message);
                    }
                    else if (message.startsWith("/tinvite")) {
                        this.handleMessageTeamInvite(message);
                    }
                    else if (Servers.isThisATestServer() && message.startsWith("/rarity ")) {
                        this.handleMessageRarity(message);
                    }
                    else if (message.startsWith("/mykingdoms")) {
                        this.handleMessageMyKingdoms();
                    }
                    else if (ServerTweaksHandler.isTweakCommand(message)) {
                        ServerTweaksHandler.handleTweakCommand(message, this.player);
                    }
                    else if (message.startsWith("/toggleccfp")) {
                        this.handleHashMessageToggleCCFP(power, message);
                    }
                    else if (message.startsWith("/resetccfp") && Servers.isThisATestServer()) {
                        this.handleMessageClearCCFP();
                    }
                    else if (message.startsWith("/resetfood") && Servers.isThisATestServer()) {
                        this.handleMessageClearFood();
                    }
                    else if (message.startsWith("/resetthirst") && Servers.isThisATestServer()) {
                        this.handleMessageClearThirst();
                    }
                    else if (message.startsWith("/almanac")) {
                        this.handleMessageAlmanac(message);
                    }
                    else if (message.equalsIgnoreCase("/tutorial")) {
                        PlayerTutorial.startTutorialCommand(this.player, message);
                    }
                    else if (message.equalsIgnoreCase("/skipTutorial")) {
                        PlayerTutorial.skipTutorialCommand(this.player, message);
                    }
                    else {
                        this.sendSafeServerMessage("Unknown command: " + message);
                    }
                }
            }
            else if (!this.player.isMute()) {
                final boolean emote = message.startsWith("/me ");
                Message mess = null;
                String emSend = null;
                if (emote) {
                    if (!this.player.isDead()) {
                        emSend = this.player.getName() + ' ';
                        emSend += message.substring(4, message.length());
                    }
                    else {
                        this.sendNormalServerMessage("Nobody can see you now.");
                    }
                }
                if (title.equals(":Local")) {
                    if (!this.player.isDead()) {
                        if (this.invulnerable) {
                            this.sendAlertServerMessage("You may not use local chat until you have moved and lost invulnerability.");
                            return;
                        }
                        if (emote) {
                            mess = new Message(this.player, (byte)6, ":Local", emSend);
                        }
                        else {
                            message = this.drunkGarble(message);
                            mess = new Message(this.player, (byte)0, ":Local", "<" + this.player.getName() + "> " + message);
                        }
                        if (this.player.isStealth()) {
                            this.player.setStealth(false);
                        }
                        final VolaTile tile = this.player.getCurrentTile();
                        if (tile != null) {
                            tile.broadCastMessage(mess);
                        }
                        if (!emote && (message.equals("help") || message.contains("guard!") || message.contains("guards!") || message.contains("help guard") || message.contains("help guards"))) {
                            this.player.callGuards();
                        }
                        this.player.chattedLocal();
                    }
                    else if (!this.player.isUndead()) {
                        this.sendNormalServerMessage("Nobody can hear you now.");
                    }
                }
                else if (title.equals("Team")) {
                    if (!this.player.isDead()) {
                        if (this.invulnerable) {
                            this.sendAlertServerMessage("You may not use team chat until you have moved and lost invulnerability.");
                            return;
                        }
                        if (emote) {
                            mess = new Message(this.player, (byte)6, "Team", emSend);
                            final Team g = this.player.getTeam();
                            if (g != null) {
                                g.sendTeamMessage(this.player, mess);
                            }
                            else {
                                this.sendNormalServerMessage("You are not part of a team.");
                            }
                        }
                        else {
                            message = this.drunkGarble(message);
                            final Team g = this.player.getTeam();
                            if (g != null) {
                                g.sendTeamMessage(this.player, message);
                            }
                            else {
                                this.sendNormalServerMessage("You are not part of a team.");
                            }
                        }
                    }
                    else {
                        this.sendNormalServerMessage("Nobody can hear you now.");
                    }
                }
                else if (title.equals("Alliance")) {
                    if (!this.player.isDead()) {
                        final Village lVillage = this.player.getCitizenVillage();
                        if (lVillage == null || lVillage.getAllianceNumber() == 0) {
                            this.sendNormalServerMessage("You are not part of an alliance.");
                        }
                        else {
                            final PvPAlliance pvpAlliance = PvPAlliance.getPvPAlliance(lVillage.getAllianceNumber());
                            if (pvpAlliance != null) {
                                if (emote) {
                                    mess = new Message(this.player, (byte)6, "Alliance", emSend);
                                }
                                else {
                                    message = this.drunkGarble(message);
                                    mess = new Message(this.player, (byte)15, "Alliance", "<" + this.player.getName() + "> " + message);
                                }
                                pvpAlliance.broadCastMessage(mess);
                            }
                        }
                    }
                    else {
                        this.sendNormalServerMessage("Nobody can hear you now.");
                    }
                }
                else if (title.equals("Village")) {
                    if (!this.player.isDead()) {
                        final Village lVillage = this.player.getCitizenVillage();
                        if (lVillage == null) {
                            this.sendNormalServerMessage("You are not the citizen of a village or homestead.");
                        }
                        else {
                            if (emote) {
                                mess = new Message(this.player, (byte)6, "Village", emSend);
                            }
                            else {
                                message = this.drunkGarble(message);
                                mess = new Message(this.player, (byte)3, "Village", "<" + this.player.getName() + "> " + message);
                            }
                            lVillage.broadCastMessage(mess, lVillage.twitChat());
                        }
                    }
                    else {
                        this.sendNormalServerMessage("Nobody can hear you now.");
                    }
                }
                else if (Kingdoms.isKingdomChat(title)) {
                    final Kingdom k = Kingdoms.getKingdomWithChatTitle(title);
                    if (!this.player.isDead()) {
                        if (emote) {
                            this.sendNormalServerMessage("You can not emote in that window.");
                        }
                        else {
                            if (this.player.getPower() <= 0 && k != null && this.player.getKingdomId() != k.getId()) {
                                this.sendAlertServerMessage("You are not part of " + k.getName() + '.');
                                return;
                            }
                            if (this.invulnerable) {
                                this.sendAlertServerMessage("You may not use kingdom chat until you have moved and lost invulnerability.");
                                return;
                            }
                            if (this.player.isKingdomChat()) {
                                message = this.drunkGarble(message);
                                mess = new Message(this.player, (byte)10, title, "<" + this.player.getName() + "> " + message);
                                Communicator.chatlogger.log(Level.INFO, "SH-" + mess.getMessage());
                                final Player[] playarr = Players.getInstance().getPlayers();
                                if (k != null) {
                                    final byte windowKingdom = k.getId();
                                    if (this.player.getKingdomId() == windowKingdom || this.player.getPower() > 0) {
                                        for (int x = 0; x < playarr.length; ++x) {
                                            if (!playarr[x].getCommunicator().invulnerable && playarr[x].isKingdomChat() && !playarr[x].isIgnored(this.player.getWurmId()) && (windowKingdom == playarr[x].getKingdomId() || playarr[x].getPower() > 0)) {
                                                playarr[x].getCommunicator().sendMessage(mess);
                                            }
                                        }
                                    }
                                    this.player.chatted();
                                }
                            }
                            else {
                                this.sendNormalServerMessage("You must toggle Kingdom chat on with /kchat to be able to participate.");
                            }
                        }
                    }
                    else {
                        this.sendNormalServerMessage("Nobody can hear you now.");
                    }
                }
                else if (Kingdoms.isGlobalKingdomChat(title)) {
                    if (Communicator.gchatdisabled) {
                        this.sendNormalServerMessage("Global chat is currently disabled on this server.");
                        return;
                    }
                    final Kingdom k = Kingdoms.getKingdomWithChatTitle(title.replace("GL-", ""));
                    if (!this.player.isDead()) {
                        if (emote) {
                            this.sendNormalServerMessage("You can not emote in that window.");
                        }
                        else {
                            if (this.player.getPower() <= 0 && k != null && this.player.getKingdomId() != k.getId()) {
                                this.sendAlertServerMessage("You are not part of " + k.getName() + '.');
                                return;
                            }
                            if (Servers.localServer.entryServer && !this.player.isReallyPaying() && !this.player.mayMute()) {
                                this.sendNormalServerMessage("You may not use global kingdom chat as a non-premium until you use a portal.");
                                return;
                            }
                            if (this.invulnerable) {
                                this.sendAlertServerMessage("You may not use kingdom chat until you have moved and lost invulnerability.");
                                return;
                            }
                            if (this.player.isGlobalChat()) {
                                message = this.drunkGarble(message);
                                mess = new Message(this.player, (byte)10, title, "<" + this.player.getName() + "> " + message);
                                Communicator.chatlogger.log(Level.INFO, "KSH-" + mess.getMessage());
                                final Player[] playarr = Players.getInstance().getPlayers();
                                if (k != null) {
                                    final byte windowKingdom = k.getId();
                                    if (this.player.getKingdomId() == windowKingdom || this.player.getPower() > 0) {
                                        for (int x = 0; x < playarr.length; ++x) {
                                            if (!playarr[x].getCommunicator().invulnerable && playarr[x].isGlobalChat() && !playarr[x].isIgnored(this.player.getWurmId()) && (windowKingdom == playarr[x].getKingdomId() || playarr[x].getPower() > 0)) {
                                                playarr[x].getCommunicator().sendMessage(mess);
                                            }
                                        }
                                    }
                                    if (message.trim().length() > 1) {
                                        final WcKingdomChat wc = new WcKingdomChat(WurmId.getNextWCCommandId(), this.player.getWurmId(), this.player.getName(), message, false, this.player.getKingdomId(), this.player.hasColoredChat() ? this.player.getCustomRedChat() : -1, this.player.hasColoredChat() ? this.player.getCustomGreenChat() : -1, this.player.hasColoredChat() ? this.player.getCustomBlueChat() : -1);
                                        if (!Servers.isThisLoginServer()) {
                                            wc.sendToLoginServer();
                                        }
                                        else {
                                            wc.sendFromLoginServer();
                                        }
                                    }
                                    this.player.chatted();
                                }
                            }
                            else {
                                this.sendNormalServerMessage("You must toggle global Kingdom chat on with /gchat to be able to participate.");
                            }
                        }
                    }
                    else {
                        this.sendNormalServerMessage("Nobody can hear you now.");
                    }
                }
                else if (title.equals("MGMT")) {
                    if (emote) {
                        message = emSend;
                    }
                    if (message != null && !message.trim().isEmpty()) {
                        Players.getInstance().sendMgmtMessage(this.player, this.player.getName(), message, emote, false, this.getPowerColourRed(), this.getPowerColourGreen(), this.getPowerColourBlue());
                        final WcMgmtMessage wc2 = new WcMgmtMessage(WurmId.getNextWCCommandId(), this.player.getName(), "(" + Servers.localServer.getAbbreviation() + ") " + message, emote, false, this.getPowerColourRed(), this.getPowerColourGreen(), this.getPowerColourBlue());
                        if (!Servers.isThisLoginServer()) {
                            wc2.sendToLoginServer();
                        }
                        else {
                            wc2.sendFromLoginServer();
                        }
                    }
                }
                else if (title.equals("GM")) {
                    if (emote) {
                        message = emSend;
                    }
                    if (message != null && !message.trim().isEmpty()) {
                        Players.getInstance().sendGmMessage(this.player, this.player.getName(), message, emote, this.getPowerColourRed(), this.getPowerColourGreen(), this.getPowerColourBlue());
                        final WCGmMessage wc3 = new WCGmMessage(WurmId.getNextWCCommandId(), this.player.getName(), "(" + Servers.localServer.getAbbreviation() + ") " + message, emote, this.getPowerColourRed(), this.getPowerColourGreen(), this.getPowerColourBlue());
                        if (!Servers.isThisLoginServer()) {
                            wc3.sendToLoginServer();
                        }
                        else {
                            wc3.sendFromLoginServer();
                        }
                    }
                }
                else if (title.equals("Trade")) {
                    if (!this.player.isDead()) {
                        if (emote) {
                            this.sendNormalServerMessage("You can not emote in that window.");
                        }
                        else {
                            if (Servers.localServer.entryServer && !this.player.isReallyPaying() && !this.player.mayMute()) {
                                this.sendNormalServerMessage("You may not use global Trade chat as a non-premium until you use a portal.");
                                return;
                            }
                            if (this.invulnerable) {
                                this.sendAlertServerMessage("You may not use Trade chat until you have moved and lost invulnerability.");
                                return;
                            }
                            if (!this.player.hasColoredChat && this.player.getPower() <= 1 && !message.toUpperCase().startsWith("WTS ") && !message.toUpperCase().startsWith("WTT ") && !message.toUpperCase().startsWith("WTB ") && !message.toUpperCase().startsWith("PC ") && !message.toUpperCase().startsWith("@")) {
                                this.sendAlertServerMessage("Only messages starting with WTB, WTS, WTT, PC or @ are allowed.");
                                return;
                            }
                            if (!this.player.hasColoredChat && this.player.hasFlag(1)) {
                                this.sendAlertServerMessage("You must have PMs enabled to use this channel.");
                                return;
                            }
                            if (!this.player.hasColoredChat && !this.player.hasFlag(3)) {
                                this.sendNormalServerMessage("As you dont have cross server PMs enabled, message is restricted to this server only.");
                            }
                            if (this.player.isTradeChannel()) {
                                message = this.drunkGarble(message);
                                mess = new Message(this.player, (byte)18, title, "<" + this.player.getName() + "> (" + Servers.localServer.getAbbreviation() + ") " + message, this.player.hasColoredChat() ? this.player.getCustomRedChat() : -1, this.player.hasColoredChat() ? this.player.getCustomGreenChat() : -1, this.player.hasColoredChat() ? this.player.getCustomBlueChat() : -1);
                                Communicator.chatlogger.log(Level.INFO, "TD-" + mess.getMessage());
                                final Player[] playarr2 = Players.getInstance().getPlayers();
                                final byte windowKingdom2 = this.player.getKingdomId();
                                for (int x2 = 0; x2 < playarr2.length; ++x2) {
                                    if (!playarr2[x2].getCommunicator().invulnerable && playarr2[x2].isTradeChannel() && !playarr2[x2].isIgnored(this.player.getWurmId()) && (windowKingdom2 == playarr2[x2].getKingdomId() || playarr2[x2].getPower() > 0)) {
                                        boolean tell = true;
                                        if (message.startsWith("@")) {
                                            final String patternString = "@" + playarr2[x2].getName().toLowerCase() + "\\b";
                                            final Pattern pattern = Pattern.compile(patternString);
                                            final Matcher matcher = pattern.matcher(message.toLowerCase());
                                            tell = (matcher.find() || this.player.getWurmId() == playarr2[x2].getWurmId());
                                        }
                                        if (!this.player.hasColoredChat) {
                                            final String patternString = "\\b" + playarr2[x2].getName().toLowerCase() + "\\b";
                                            final Pattern pattern = Pattern.compile(patternString);
                                            final Matcher matcher = pattern.matcher(message.toLowerCase());
                                            if (matcher.find()) {
                                                mess.setColorR(100);
                                                mess.setColorG(170);
                                                mess.setColorB(255);
                                            }
                                            else {
                                                mess.setColorR(150);
                                                mess.setColorG(230);
                                                mess.setColorB(255);
                                            }
                                        }
                                        if (tell) {
                                            playarr2[x2].getCommunicator().sendMessage(mess);
                                        }
                                    }
                                }
                                if (this.player.hasFlag(3)) {
                                    final WcTradeChannel wc4 = new WcTradeChannel(WurmId.getNextWCCommandId(), this.player.getWurmId(), this.player.getName(), "(" + Servers.localServer.getAbbreviation() + ") " + message, this.player.getKingdomId(), this.player.hasColoredChat() ? this.player.getCustomRedChat() : -1, this.player.hasColoredChat() ? this.player.getCustomGreenChat() : -1, this.player.hasColoredChat() ? this.player.getCustomBlueChat() : -1);
                                    if (!Servers.isThisLoginServer()) {
                                        wc4.sendToLoginServer();
                                    }
                                    else {
                                        wc4.sendFromLoginServer();
                                    }
                                }
                                this.player.chatted();
                            }
                            else {
                                this.sendNormalServerMessage("You must toggle Trade channel on from your profile.");
                            }
                        }
                    }
                    else {
                        this.sendNormalServerMessage("Nobody can hear you now.");
                    }
                }
                else if (title.equals("CA HELP")) {
                    if (Servers.localServer.EPIC) {
                        this.sendNormalServerMessage("This channel is only available when NOT on EPIC servers.");
                    }
                    else if (Servers.isThisLoginServer() && !Server.getInstance().isPS()) {
                        if (emote) {
                            mess = new Message(this.player, (byte)6, "GV HELP", emSend);
                        }
                        else {
                            mess = new Message(this.player, (byte)12, "GV HELP", "<" + this.player.getName() + "> " + message);
                        }
                        final WcGVHelpMessage wchgm = new WcGVHelpMessage(this.player.getName(), message, emote, mess.getRed(), mess.getGreen(), mess.getBlue());
                        for (final ServerEntry se : Servers.getAllServers()) {
                            if (se.getId() != Servers.getLocalServerId()) {
                                wchgm.sendToServer(se.getId());
                            }
                        }
                    }
                    if (this.player.seesPlayerAssistantWindow()) {
                        if (emote) {
                            mess = new Message(this.player, (byte)6, "CA HELP", emSend);
                        }
                        else {
                            mess = new Message(this.player, (byte)12, "CA HELP", "<" + this.player.getName() + "> " + message);
                        }
                        final byte localCAHelpGroup = Servers.localServer.getCAHelpGroup();
                        if (localCAHelpGroup != -1) {
                            if (Servers.isThisLoginServer()) {
                                final WcCAHelpGroupMessage wchgm2 = new WcCAHelpGroupMessage(localCAHelpGroup, this.player.getKingdomId(), this.player.getName(), emote ? emSend : ("(" + Servers.localServer.getAbbreviation() + ") " + message), emote, mess.getRed(), mess.getGreen(), mess.getBlue());
                                for (final ServerEntry se2 : Servers.getAllServers()) {
                                    if (se2.getId() != Servers.getLocalServerId() && se2.getCAHelpGroup() == localCAHelpGroup) {
                                        wchgm2.sendToServer(se2.getId());
                                    }
                                }
                            }
                            else {
                                final WcCAHelpGroupMessage wchgm2 = new WcCAHelpGroupMessage(localCAHelpGroup, this.player.getKingdomId(), this.player.getName(), emote ? emSend : ("(" + Servers.localServer.getAbbreviation() + ") " + message), emote, mess.getRed(), mess.getGreen(), mess.getBlue());
                                wchgm2.sendToLoginServer();
                            }
                        }
                        Players.getInstance().sendPaMessage(mess);
                    }
                    else {
                        this.sendNormalServerMessage("You must toggle the CA window using the command /ca first.");
                    }
                }
                else if (title.equals("GV HELP")) {
                    if (Servers.isThisLoginServer()) {
                        this.sendNormalServerMessage("Please use the CA Help window on this server.");
                    }
                    else {
                        if (emote) {
                            mess = new Message(this.player, (byte)6, "CA HELP", emSend);
                        }
                        else {
                            mess = new Message(this.player, (byte)12, "CA HELP", "<" + this.player.getName() + "> " + message);
                        }
                        final WcCAHelpGroupMessage wchgm3 = new WcCAHelpGroupMessage((byte)(-1), (byte)4, this.player.getName(), emote ? emSend : message, emote, mess.getRed(), mess.getGreen(), mess.getBlue());
                        wchgm3.sendToLoginServer();
                        if (emote) {
                            mess = new Message(this.player, (byte)6, "GV HELP", emSend);
                        }
                        else {
                            mess = new Message(this.player, (byte)12, "GV HELP", "<" + this.player.getName() + "> " + message);
                        }
                        Players.getInstance().sendGVMessage(mess);
                        final WcGVHelpMessage wgvhm = new WcGVHelpMessage(this.player.getName(), message, emote, mess.getRed(), mess.getGreen(), mess.getBlue());
                        for (final ServerEntry se2 : Servers.getAllServers()) {
                            if (se2.getId() != Servers.getLocalServerId() && se2.getId() != Servers.getLoginServerId()) {
                                wgvhm.sendToServer(se2.getId());
                            }
                        }
                    }
                }
                else if (title.equals("JK HELP") || title.equals("MR HELP") || title.equals("HOTS HELP")) {
                    if (!Servers.localServer.EPIC) {
                        this.sendNormalServerMessage("This channel is only available when on EPIC servers.");
                    }
                    else if (this.player.seesPlayerAssistantWindow()) {
                        byte kingdomId = this.player.getKingdomId();
                        if (this.player.getPower() > 1) {
                            if (title.equals("JK HELP")) {
                                kingdomId = 1;
                            }
                            else if (title.equals("MR HELP")) {
                                kingdomId = 2;
                            }
                            else if (title.equals("HOTS HELP")) {
                                kingdomId = 3;
                            }
                        }
                        if (emote) {
                            mess = new Message(this.player, (byte)6, title, emSend);
                        }
                        else {
                            mess = new Message(this.player, (byte)12, title, "<" + this.player.getName() + "> " + message);
                        }
                        final byte localCAHelpGroup2 = Servers.localServer.getCAHelpGroup();
                        if (localCAHelpGroup2 != -1) {
                            if (Servers.isThisLoginServer()) {
                                final WcCAHelpGroupMessage wchgm4 = new WcCAHelpGroupMessage(localCAHelpGroup2, kingdomId, this.player.getName(), emote ? emSend : message, emote, mess.getRed(), mess.getGreen(), mess.getBlue());
                                for (final ServerEntry se3 : Servers.getAllServers()) {
                                    if (se3.getId() != Servers.getLocalServerId() && se3.getCAHelpGroup() == localCAHelpGroup2) {
                                        wchgm4.sendToServer(se3.getId());
                                    }
                                }
                            }
                            else {
                                final WcCAHelpGroupMessage wchgm4 = new WcCAHelpGroupMessage(localCAHelpGroup2, kingdomId, this.player.getName(), emote ? emSend : message, emote, mess.getRed(), mess.getGreen(), mess.getBlue());
                                wchgm4.sendToLoginServer();
                            }
                        }
                        Players.getInstance().sendCaMessage(kingdomId, mess);
                    }
                    else {
                        this.sendNormalServerMessage("You must toggle the CA window using the command /ca first.");
                    }
                }
                else if (title.equals("Friends")) {
                    if (!this.player.isDead()) {
                        final Friend[] lFriends = this.player.getFriends();
                        if (emote) {
                            mess = new Message(this.player, (byte)6, "Friends", emSend);
                        }
                        else {
                            message = this.drunkGarble(message);
                            mess = new Message(this.player, (byte)4, "Friends", "<" + this.player.getName() + "> " + message);
                        }
                        for (int a = 0; a < lFriends.length; ++a) {
                            try {
                                final Player friend = Players.getInstance().getPlayer(lFriends[a].getFriendId());
                                friend.getCommunicator().sendMessage(mess);
                            }
                            catch (NoSuchPlayerException ex) {}
                        }
                    }
                    else {
                        this.sendNormalServerMessage("Nobody can hear you now.");
                    }
                }
                else if (title.startsWith("PM: ")) {
                    if (!this.player.isDead() && !this.player.isMute()) {
                        final StringTokenizer tokens2 = new StringTokenizer(title);
                        tokens2.nextToken();
                        String targetName = "Unknown";
                        if (tokens2.hasMoreTokens()) {
                            targetName = tokens2.nextToken().trim();
                        }
                        if (emote) {
                            this.player.sendPM(targetName, emSend, true, false);
                        }
                        else {
                            this.player.sendPM(targetName, this.drunkGarble(message), false, false);
                        }
                    }
                }
            }
        }
    }
    
    private void handleHashMessageBuildInfo() {
        this.sendBuildInfo("server");
        this.sendBuildInfo("common");
    }
    
    private void sendBuildInfo(final String module) {
        try {
            final BuildProperties info = BuildProperties.getPropertiesFor("/com/wurmonline/" + module.toLowerCase() + "/build.properties");
            this.sendNormalServerMessage(module + " branch: " + info.getGitBranch());
            this.sendNormalServerMessage(module + " build SHA: " + info.getGitSha1Short());
            this.sendNormalServerMessage(module + " built on: " + info.getBuildTimeString());
            this.sendNormalServerMessage(module + " version: " + info.getVersion());
        }
        catch (IOException e) {
            this.sendNormalServerMessage(module + " info not available.");
        }
    }
    
    private void handleMessageMyKingdoms() {
        Kingdom epicKingdom = Kingdoms.getKingdom(this.getPlayer().getSaveFile().epicKingdom);
        final Kingdom chaosKingdom = Kingdoms.getKingdom(this.getPlayer().getSaveFile().getChaosKingdom());
        final Kingdom kingdom = Kingdoms.getKingdom(this.getPlayer().getKingdomId());
        if (Servers.localServer.isChallengeOrEpicServer()) {
            epicKingdom = kingdom;
        }
        this.sendNormalServerMessage("Your current kingdom in these lands is " + kingdom.getName() + ".");
        if (Server.getInstance().isPS()) {
            this.sendNormalServerMessage("Your PvP kingdom is " + chaosKingdom.getName() + ".");
        }
        else {
            this.sendNormalServerMessage("Your kingdom on Chaos is " + chaosKingdom.getName() + ".");
        }
        this.sendNormalServerMessage("Your kingdom on Epic is " + epicKingdom.getName() + ".");
    }
    
    private void handleMessageClearCCFP() {
        if (Servers.isThisATestServer()) {
            this.player.getStatus().clearCCFPValues();
        }
    }
    
    private void handleMessageClearFood() {
        if (Servers.isThisATestServer()) {
            this.player.getStatus().clearHunger();
        }
    }
    
    private void handleMessageClearThirst() {
        if (Servers.isThisATestServer()) {
            this.player.getStatus().clearThirst();
        }
    }
    
    private void handleMessageAlmanac(final String message) {
        final Item[] reports = Methods.getBestReports(this.player, null);
        if (reports.length == 0) {
            this.sendSafeServerMessage("No reports found!");
        }
        else {
            this.sendSafeServerMessage("Currently Harvestable (from your almanac): ");
            int found = 0;
            for (final Item report : reports) {
                final WurmHarvestables.Harvestable harvestable = report.getHarvestable();
                if (harvestable != null && harvestable.isHarvestable()) {
                    this.sendSafeServerMessage(harvestable.getHarvestableWithDates());
                    ++found;
                }
            }
            if (found == 0) {
                this.sendSafeServerMessage("Nothing in your almanac is currently harvestable!");
            }
        }
    }
    
    private void reallyHandle_CMD_TOGGLE_SWITCH(final ByteBuffer byteBuffer) throws IOException {
        final int toggle = byteBuffer.get() & 0xFF;
        final int value = byteBuffer.get() & 0xFF;
        if (toggle == 0) {
            if (this.player.isClimbing()) {
                if (value == 0) {
                    this.player.setClimbing(false);
                    PlayerTutorial.firePlayerTrigger(this.player.getWurmId(), PlayerTutorial.PlayerTrigger.DISABLED_CLIMBING);
                }
            }
            else if (value == 1 || value == 2) {
                if (this.player.getVehicle() != -10L) {
                    this.sendNormalServerMessage("You can not climb now.");
                    this.sendToggle(0, false);
                    return;
                }
                if (this.player.getStatus().getStamina() > 500 || this.player.isUsingLastGasp()) {
                    this.player.setClimbing(true);
                    PlayerTutorial.firePlayerTrigger(this.player.getWurmId(), PlayerTutorial.PlayerTrigger.ENABLED_CLIMBING);
                }
                else {
                    this.sendNormalServerMessage("You are too exhausted to climb now.");
                    this.sendToggle(0, false);
                }
            }
        }
        else if (toggle == 5) {
            final float lastx = byteBuffer.getFloat();
            final float lasty = byteBuffer.getFloat();
            final float lastz = byteBuffer.getFloat();
            final float dampen = byteBuffer.getFloat();
            final float fallMod = byteBuffer.getFloat();
            if (this.ready) {
                if (lastx != this.lastX || lasty != this.lastY || lastz != this.lastZ) {
                    this.player.setCheated("Not same moves!");
                    logWarn(this.player.getName() + " not the same move at verification! lastX=" + this.lastX + " vs " + lastx + ", lastY=" + this.lastY + " vs " + lasty + ", lastZ=" + this.lastZ + " vs " + lastz);
                }
                this.lastCounts = 0;
                if (fallMod != this.ticker.getFallMod()) {
                    this.player.setCheated("Hacked Fall mod");
                    logWarn(this.player.getName() + " hacked the fall mod to " + fallMod + " from the standard " + this.ticker.getFallMod());
                }
                if (dampen != this.ticker.getMoveMod()) {
                    this.player.setCheated("Hacked Move mod");
                    logWarn(this.player.getName() + " hacked the move mod to " + dampen + " from the standard " + this.ticker.getMoveMod());
                }
            }
        }
        else if (toggle == 1) {
            if (this.player.faithful) {
                if (value == 0) {
                    this.player.setFaithMode(false);
                }
            }
            else if (value == 1 || value == 2) {
                this.player.setFaithMode(true);
            }
            this.player.getStatus().sendStateString();
        }
        else if (toggle == 2) {
            if (this.player.isLegal()) {
                if (value == 0) {
                    this.player.setLegal(false);
                }
            }
            else if (value == 1 || value == 2) {
                this.player.setLegal(true);
            }
        }
        else if (toggle == 3) {
            if (this.player.isStealth()) {
                if (value == 0) {
                    this.player.setStealth(false);
                }
            }
            else if (value == 1 || value == 2) {
                if (this.player.isFighting()) {
                    this.sendToggle(3, false);
                    this.sendNormalServerMessage("You can't hide now.", (byte)3);
                }
                else {
                    try {
                        if (this.player.getCurrentAction() != null) {
                            this.sendToggle(3, false);
                            this.sendNormalServerMessage("You can't hide now.");
                        }
                        else {
                            this.player.startPersonalAction((short)136, -1L, this.player.getWurmId());
                        }
                    }
                    catch (NoSuchActionException nsa) {
                        this.player.startPersonalAction((short)136, -1L, this.player.getWurmId());
                    }
                }
            }
        }
        else if (toggle == 4) {
            if (this.player.isAutofight()) {
                if (value == 0) {
                    this.player.setAutofight(false);
                }
            }
            else if (value == 1 || value == 2) {
                this.player.setAutofight(true);
            }
        }
        else if (toggle == 100) {
            if (this.player.isArcheryMode()) {
                if (value == 0) {
                    this.player.setArcheryMode(false);
                }
            }
            else if (value == 1 || value == 2) {
                this.player.setArcheryMode(true);
            }
        }
        else if (toggle == 6) {
            this.handleMessageToggleSleep();
        }
    }
    
    private void reallyHandle_CMD_ACTION(final ByteBuffer byteBuffer) {
        if (this.invulnerable) {
            this.setInvulnerable(false);
            this.sendNormalServerMessage("You are no longer invulnerable.");
        }
        final int nums = byteBuffer.getShort() & 0xFFFF;
        final long subject = byteBuffer.getLong();
        final long[] targets = new long[nums];
        for (int s = 0; s < nums; ++s) {
            targets[s] = byteBuffer.getLong();
        }
        short action = byteBuffer.getShort();
        if (nums > 1 && Actions.isMultipleItemAction(action, targets)) {
            Communicator.commandAction = WurmId.getType(targets[0]);
            if (!this.player.isDead() && !this.player.isTransferring()) {
                try {
                    if (Actions.isDefaultTerraformingAction(action)) {
                        action = Actions.getDefaultActionForTarget(subject, targets[0]);
                    }
                    BehaviourDispatcher.action(this.player, this, subject, targets, action);
                }
                catch (FailedException fe) {
                    this.sendNormalServerMessage(fe.getMessage());
                }
                catch (NoSuchCreatureException nsc) {
                    logInfo(this.player.getName() + "- action dispatching failed. " + nsc);
                }
                catch (NoSuchPlayerException nsp) {
                    logInfo(this.player.getName() + "- action dispatching failed. " + nsp);
                }
                catch (RuntimeException rex) {
                    logInfo(this.player.getName() + "- action dispatching failed", rex);
                }
                catch (NoSuchBehaviourException nsb) {
                    logInfo(this.player.getName() + "- action dispatching failed " + nsb.getMessage(), nsb);
                }
                catch (NoSuchItemException nsw) {
                    if (this.player.getPower() >= 2) {
                        this.player.getCommunicator().sendNormalServerMessage(nsw.getMessage());
                    }
                }
                catch (Exception ex) {
                    logInfo(this.player.getName() + "- action dispatching failed", ex);
                }
            }
        }
        else {
            for (int x = 0; x < nums; ++x) {
                if (!this.player.isDead() && !this.player.isTransferring()) {
                    try {
                        Communicator.commandAction = WurmId.getType(targets[x]);
                        if (Actions.isDefaultTerraformingAction(action)) {
                            action = Actions.getDefaultActionForTarget(subject, targets[x]);
                        }
                        BehaviourDispatcher.action(this.player, this, subject, targets[x], action);
                    }
                    catch (FailedException fe2) {
                        if (!fe2.getMessage().equals("This is a placeholder message.")) {
                            this.sendNormalServerMessage(fe2.getMessage(), (byte)3);
                        }
                    }
                    catch (NoSuchCreatureException nsc2) {
                        logInfo(this.player.getName() + "- action dispatching failed. " + nsc2);
                    }
                    catch (NoSuchPlayerException nsp2) {
                        logInfo(this.player.getName() + "- action dispatching failed. " + nsp2);
                    }
                    catch (RuntimeException rex2) {
                        logInfo(this.player.getName() + "- action dispatching failed", rex2);
                    }
                    catch (NoSuchBehaviourException nsb2) {
                        logInfo(this.player.getName() + "- action dispatching failed " + nsb2.getMessage(), nsb2);
                    }
                    catch (NoSuchWallException nsw2) {
                        logInfo(this.player.getName() + "- action dispatching failed " + nsw2.getMessage(), nsw2);
                    }
                    catch (NoSuchItemException nsw3) {
                        if (this.player.getPower() >= 2) {
                            this.player.getCommunicator().sendNormalServerMessage(nsw3.getMessage());
                        }
                    }
                    catch (Exception ex2) {
                        logInfo(this.player.getName() + "- action dispatching failed", ex2);
                    }
                }
            }
        }
    }
    
    private void reallyHandle_CMD_ITEM_CREATION_LIST(final ByteBuffer byteBuffer) {
        final byte subCommand = byteBuffer.get();
        if (this.commandsThisSecond > 10) {
            Communicator.logger.log(Level.INFO, "Subcommand for item_creation_list=" + subCommand);
        }
        if (subCommand == 0) {
            final long source = byteBuffer.getLong();
            final long target = byteBuffer.getLong();
            final int sType = WurmId.getType(source);
            final int tType = WurmId.getType(target);
            if ((sType == 2 || sType == 19 || source == -10L) && (tType == 2 || tType == 19 || target == -10L)) {
                this.sendPartialCreationList(source, target);
            }
            else if (sType == 12 || tType == 12) {
                if (sType == 12 && tType == 2) {
                    this.sendTileBorderCreationList(source, target);
                }
                else if (sType == 2 && tType == 12) {
                    this.sendTileBorderCreationList(target, source);
                }
            }
            else if (sType == 7 || tType == 7) {
                this.sendFenceCreationList(source, target);
            }
            else if (sType == 5 || tType == 5) {
                if (sType == 5 && (tType == 2 || target == -10L)) {
                    this.sendWallCreationList(source, target);
                }
                else if ((sType == 2 || source == -10L) && tType == 5) {
                    this.sendWallCreationList(target, source);
                }
            }
            else if (sType == 23 || tType == 23) {
                if (sType == 23 && (tType == 2 || target == -10L)) {
                    this.sendRoofFloorCreationList(source, target);
                }
                else if ((sType == 2 || source == -10L) && tType == 23) {
                    this.sendRoofFloorCreationList(target, source);
                }
            }
            else if (sType == 28 || tType == 28) {
                if (sType == 28 && (tType == 2 || tType == 19 || target == -10L)) {
                    this.sendBridgePartCreationList(source, target);
                }
                else if ((sType == 2 || sType == 19 || source == -10L) && tType == 28) {
                    this.sendBridgePartCreationList(target, source);
                }
            }
            else if (sType == 17 || tType == 17) {
                if (sType == 17 && (tType == 2 || target == -10L)) {
                    this.sendCaveCreationList(source, target);
                }
                else if ((sType == 2 || source == -10L) && tType == 17) {
                    this.sendCaveCreationList(target, source);
                }
            }
        }
        else if (subCommand == 3) {
            this.sendCreationsList();
        }
    }
    
    private void reallyHandle_CMD_REQUEST_SELECT(final ByteBuffer byteBuffer) {
        if (this.invulnerable) {
            this.setInvulnerable(false);
            this.sendNormalServerMessage("You are no longer invulnerable.");
        }
        final byte requestId = byteBuffer.get();
        final long target = byteBuffer.getLong();
        final long subject = byteBuffer.getLong();
        Communicator.commandAction = WurmId.getType(target);
        if (this.commandsThisSecond > 10) {
            Communicator.logger.log(Level.INFO, "Subcommand for reallyHandle_CMD_REQUEST_SELECT=" + Communicator.commandAction);
        }
        if (!this.player.isDead() && !this.player.isTransferring()) {
            try {
                BehaviourDispatcher.requestSelectionActions(this.player, this, requestId, subject, target);
            }
            catch (NoSuchItemException ex) {
                logInfo(this.player.getName() + "- action request failed. No such item. " + ex);
            }
            catch (NoSuchPlayerException nsp) {
                logInfo(this.player.getName() + "- action request failed. No such player. " + nsp);
            }
            catch (NoSuchCreatureException nsc) {
                logInfo(this.player.getName() + "- action request failed. No such creature. " + nsc);
            }
            catch (Exception ex2) {
                logWarn(this.player.getName() + "- action request failed.", ex2);
            }
        }
    }
    
    private void reallyHandle_CMD_REQUEST_ACTIONS(final ByteBuffer byteBuffer) {
        if (this.invulnerable) {
            this.setInvulnerable(false);
            this.sendNormalServerMessage("You are no longer invulnerable.");
        }
        final byte requestId = byteBuffer.get();
        final long target = byteBuffer.getLong();
        final long subject = byteBuffer.getLong();
        Communicator.commandAction = WurmId.getType(target);
        if (!this.player.isDead() && !this.player.isTransferring()) {
            try {
                BehaviourDispatcher.requestActions(this.player, this, requestId, subject, target);
            }
            catch (NoSuchItemException ex) {
                logInfo(this.player.getName() + "- action request failed. No such item. " + ex);
            }
            catch (NoSuchPlayerException nsp) {
                logInfo(this.player.getName() + "- action request failed. No such player. " + nsp);
            }
            catch (NoSuchCreatureException nsc) {
                logInfo(this.player.getName() + "- action request failed. No such creature. " + nsc);
            }
            catch (Exception ex2) {
                logWarn(this.player.getName() + "- action request failed.", ex2);
            }
        }
    }
    
    private final boolean setNextMove() {
        if (this.currentmove.getNext() != null && this.currentmove != this.currentmove.getNext()) {
            if (this.currentmove.isHandled()) {
                final PlayerMove last = this.currentmove;
                this.currentmove = this.currentmove.getNext();
                last.setNext(null);
            }
            return true;
        }
        return false;
    }
    
    private void reallyHandle_CMD_MOVE_CREATURE(final ByteBuffer byteBuffer, final byte cmd) {
        if (this.ready) {
            ++this.lastCounts;
            if (this.lastCounts % 10000 == 0) {
                logWarn(this.player.getName() + " last counts=" + this.lastCounts);
            }
        }
        if (this.ready) {
            final PlayerMove next = new PlayerMove();
            if (this.currentmove != null) {
                this.currentmove.getLast().setNext(next);
            }
            if (this.player.vehicle != -10L && !this.player.isVehicleCommander()) {
                this.lastX = byteBuffer.getFloat();
                this.lastY = byteBuffer.getFloat();
                this.lastZ = byteBuffer.getFloat();
            }
            else {
                next.setNewPosX(byteBuffer.getFloat());
                next.setNewPosY(byteBuffer.getFloat());
                next.setNewPosZ(byteBuffer.getFloat());
                this.lastX = next.getNewPosX();
                this.lastY = next.getNewPosY();
                this.lastZ = next.getNewPosZ();
            }
            next.setNewRot(byteBuffer.getFloat());
            next.setBm(byteBuffer.get());
            next.setLayer(byteBuffer.get());
            if (cmd == -29) {
                final byte data = byteBuffer.get();
                next.setOnFloor((data & 0x1) != 0x0);
                final boolean sentBridge = (data & 0x2) != 0x0;
                final boolean falling = (data & 0x4) != 0x0;
                next.setIsFalling(falling);
                if (sentBridge) {
                    next.setNewBridgeId(byteBuffer.getLong());
                }
                if ((data & 0x8) != 0x0) {
                    next.setNewHeightOffset(byteBuffer.getInt());
                    if ((data & 0x10) != 0x0) {
                        next.setChangeHeightImmediately(false);
                    }
                    else {
                        next.setChangeHeightImmediately(true);
                    }
                }
            }
            if (this.receivedBridgeChange) {
                next.setNewBridgeId(this.newBridgeId);
                this.receivedBridgeChange = false;
            }
            if (this.currentmove == null) {
                this.currentmove = next;
            }
            ++this.moves;
            this.ticker.started = true;
            if (!this.receivedTicks) {
                this.receivedTicks = true;
            }
            if (this.moves > 2500) {
                logWarn(this.player.getName() + " 2500 moves. Disconnecting.");
                this.player.setLink(false);
            }
        }
        else {
            byteBuffer.getFloat();
            byteBuffer.getFloat();
            byteBuffer.getFloat();
            byteBuffer.getFloat();
            final byte crapbm = byteBuffer.get();
            byteBuffer.get();
            if (cmd == -29) {
                final byte data = byteBuffer.get();
                final boolean sentBridge = (data & 0x2) != 0x0;
                if (sentBridge) {
                    byteBuffer.getLong();
                }
            }
        }
    }
    
    private void reallyHandle_CMD_NOT_MOVE_CREATURE() {
        if (this.ticker != null && this.ready && this.currentmove != null) {
            ++this.moves;
            this.currentmove.getLast().incrementSameMoves();
            if (this.newHeightOffset != -10000) {
                this.currentmove.getLast().setNewHeightOffset(this.newHeightOffset);
                this.currentmove.getLast().setChangeHeightImmediately(this.changeHeightImmediately);
                this.newHeightOffset = -10000;
            }
            if (this.receivedBridgeChange) {
                this.currentmove.setNewBridgeId(this.newBridgeId);
                this.receivedBridgeChange = false;
            }
        }
    }
    
    private void reallyHandle_CMD_VALREIFIGHT(final ByteBuffer byteBuffer) throws Exception {
        final byte subCommand = byteBuffer.get();
        switch (subCommand) {
            case 0: {
                final int requestedPage = byteBuffer.getInt();
                this.sendValreiFightList(requestedPage);
                break;
            }
            case 1: {
                final long fightId = byteBuffer.getLong();
                this.sendValreiFightDetails(fightId);
                break;
            }
        }
    }
    
    private void reallyHandle_CMD_COOKBOOK(final ByteBuffer byteBuffer) throws Exception {
        final byte subCommand = byteBuffer.get();
        switch (subCommand) {
            case 0: {
                this.player.setIsViewingCookbook();
                if (this.player.getPower() == 5) {
                    this.sendRecipeNameList(Recipes.getAllRecipes());
                    break;
                }
                this.sendRecipeNameList(RecipesByPlayer.getKnownRecipesFor(this.player.getWurmId()));
                break;
            }
            case 1: {
                final short recipeId = byteBuffer.getShort();
                if (this.player.getPower() == 5) {
                    this.sendCookbookRecipe(Recipes.getRecipeById(recipeId));
                    break;
                }
                this.sendCookbookRecipe(RecipesByPlayer.getRecipe(this.player.getWurmId(), recipeId));
                break;
            }
            case 2: {
                final short recipeId = byteBuffer.getShort();
                final boolean isFavourite = byteBuffer.get() != 0;
                RecipesByPlayer.setIsFavourite(this.player.getWurmId(), recipeId, isFavourite);
                break;
            }
            case 3: {
                final short recipeId = byteBuffer.getShort();
                final String notes = readByteString(byteBuffer);
                RecipesByPlayer.setNotes(this.player.getWurmId(), recipeId, notes);
                break;
            }
            case 4: {
                if (this.player.isWritingRecipe()) {
                    this.sendNormalServerMessage("You are currently writing a recipe so cannot change to another.");
                    break;
                }
                final short recipeId = byteBuffer.getShort();
                final Recipe recipe = RecipesByPlayer.getRecipe(this.player.getWurmId(), recipeId);
                this.player.setViewingRecipe(recipe);
                break;
            }
        }
    }
    
    private int getPowerColourRed() {
        switch (this.player.getPower()) {
            case 5: {
                return 255;
            }
            case 4: {
                return 128;
            }
            case 3: {
                return 187;
            }
            case 2: {
                return 20;
            }
            case 1: {
                return 255;
            }
            default: {
                if (this.player.mayHearDevTalk()) {
                    return 255;
                }
                if (this.player.mayMute()) {
                    return 100;
                }
                return -1;
            }
        }
    }
    
    private int getPowerColourGreen() {
        switch (this.player.getPower()) {
            case 5: {
                return 242;
            }
            case 4: {
                return 250;
            }
            case 3: {
                return 1;
            }
            case 2: {
                return 200;
            }
            case 1: {
                return 175;
            }
            default: {
                if (this.player.mayHearDevTalk()) {
                    return 128;
                }
                if (this.player.mayMute()) {
                    return 220;
                }
                return -1;
            }
        }
    }
    
    private int getPowerColourBlue() {
        switch (this.player.getPower()) {
            case 5: {
                return 1;
            }
            case 4: {
                return 20;
            }
            case 3: {
                return 187;
            }
            case 2: {
                return 255;
            }
            case 1: {
                return 200;
            }
            default: {
                if (this.player.mayHearDevTalk()) {
                    return 1;
                }
                if (this.player.mayMute()) {
                    return 200;
                }
                return -1;
            }
        }
    }
    
    private void handleRecruitWindowDataMessage(final ByteBuffer bb) {
        final short length = bb.getShort();
        final byte[] byteDesc = new byte[length];
        bb.get(byteDesc);
        try {
            final String description = new String(byteDesc, "UTF-8");
            final Village playerVillage = Villages.getVillageForCreature(this.player);
            if (playerVillage != null) {
                try {
                    if (!RecruitmentAds.containsAdForVillage(playerVillage.getId())) {
                        RecruitmentAds.create(playerVillage.getId(), description, this.player.getWurmId(), this.player.getKingdomId());
                    }
                    else {
                        RecruitmentAds.update(playerVillage.getId(), description, this.player.getWurmId(), new Date(System.currentTimeMillis()), this.player.getKingdomId());
                    }
                }
                catch (IOException ioe) {
                    logWarn("Unable to create recruitment ad.", ioe);
                }
            }
            else {
                this.sendNormalServerMessage("You don't belong to a village so you can't post a recruitment ad.");
            }
        }
        catch (UnsupportedEncodingException enc) {
            logWarn("Unsupported encoding in recruit window data: UTF-8", enc);
        }
    }
    
    private void testThrow() {
        final CombatMove thrsow = CombatMove.getCombatMove(6);
        thrsow.perform(this.player);
    }
    
    private void handlePMs(final String message) {
        if (!this.player.isDead() && !this.player.isMute()) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String targetName = "Unknown";
            if (tokens.hasMoreTokens()) {
                targetName = tokens.nextToken().trim();
            }
            String _message = "";
            if (tokens.hasMoreTokens()) {
                _message = tokens.nextToken();
            }
            while (tokens.hasMoreTokens()) {
                _message = _message + ' ' + tokens.nextToken();
            }
            _message = this.drunkGarble(_message);
            this.player.sendPM(targetName, _message, false, false);
        }
        else {
            this.sendNormalServerMessage("Nobody can hear you now.");
        }
    }
    
    private void handleMessageAFK(final String message) {
        if (this.player.isAFK()) {
            this.player.setAFK(false);
            this.sendNormalServerMessage("You are no longer AFK.");
        }
        else {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String newAFKMessage = "";
            while (tokens.hasMoreTokens()) {
                newAFKMessage = newAFKMessage + tokens.nextToken().trim() + ' ';
            }
            if (!newAFKMessage.isEmpty()) {
                this.player.setAFKMessage(newAFKMessage);
            }
            this.player.setAFK(true);
            this.sendNormalServerMessage("You are now AFK (with message:" + this.player.getAFKMessage() + ").");
        }
    }
    
    private void handleMessageAddFriend(final String message) {
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        String friendsName = "";
        String category = "Other";
        if (tokens.hasMoreTokens()) {
            friendsName = tokens.nextToken().trim();
        }
        if (tokens.hasMoreTokens()) {
            category = tokens.nextToken().trim();
        }
        if (!friendsName.isEmpty()) {
            this.addFriend(friendsName, category);
        }
        else {
            this.sendNormalServerMessage("Use /addfriend <name> <category>.");
        }
    }
    
    public void addFriend(final String name, final String category) {
        Friend.Category cat = Friend.Category.Other;
        cat = Friend.Category.catFromName(category);
        if (cat == null) {
            this.sendNormalServerMessage("Unknown category, should be one of Trusted, Friends, Contacts or Other.");
            return;
        }
        final String friendsName = LoginHandler.raiseFirstLetter(name);
        if (friendsName.equalsIgnoreCase(this.player.getName())) {
            this.sendNormalServerMessage("We will always be friends, you know too much.");
            return;
        }
        final String wffn = this.player.waitingForFriend();
        if (!wffn.isEmpty() && !wffn.equalsIgnoreCase(friendsName)) {
            if (this.player.askingFriend()) {
                this.sendNormalServerMessage("You are still waiting for " + wffn + '.');
            }
            else {
                this.sendNormalServerMessage("You need to reply to " + wffn + " first.");
            }
            return;
        }
        final PlayerState pstate = PlayerInfoFactory.getPlayerState(friendsName);
        if (pstate == null) {
            this.sendNormalServerMessage("Unknown player " + friendsName + '.');
            return;
        }
        if (this.player.isFriend(pstate.getPlayerId())) {
            this.sendNormalServerMessage("You are already friends with " + friendsName + '.');
            return;
        }
        if (this.player.isIgnored(pstate.getPlayerId())) {
            this.sendNormalServerMessage("You are ignoring " + friendsName + " and therefore cannot send friends request.");
            return;
        }
        if (this.player.hasFlag(1)) {
            this.sendNormalServerMessage("You have to enable PMs to be able to send friend requests.");
            return;
        }
        if (wffn.equalsIgnoreCase(friendsName) && this.player.askingFriend()) {
            this.sendNormalServerMessage("You are still waiting for " + wffn + '.');
            return;
        }
        if (pstate.getServerId() == Servers.getLocalServerId()) {
            try {
                byte reply = 7;
                final Player p = Players.getInstance().getPlayer(friendsName);
                if (wffn.isEmpty()) {
                    if (this.player.getKingdomId() != p.getKingdomId() && !this.player.hasFlag(2)) {
                        this.sendNormalServerMessage(friendsName + " is not the same kingdom. You need to enable cross kingdom PMs to be able to send friend requests to other kingdoms.");
                        return;
                    }
                    this.player.setAskFriend(friendsName, cat);
                    this.sendNormalServerMessage("You have sent a request to " + friendsName + '.');
                    reply = p.remoteAddFriend(this.player.getName(), this.player.getKingdomId(), (byte)0, false, this.player.hasFlag(2));
                }
                else {
                    this.player.setAddFriendTimout(30, cat);
                    reply = p.remoteAddFriend(this.player.getName(), this.player.getKingdomId(), (byte)6, false, this.player.hasFlag(2));
                }
                if (reply != 7) {
                    this.player.remoteAddFriend(friendsName, p.getKingdomId(), reply, false, p.hasFlag(2));
                }
                return;
            }
            catch (NoSuchPlayerException e) {
                this.sendSafeServerMessage(friendsName + " is not currently available, please try again later.");
                return;
            }
        }
        byte reply = 0;
        if (!wffn.isEmpty()) {
            this.player.setAddFriendTimout(30, cat);
            reply = 6;
        }
        else {
            this.sendNormalServerMessage("You have sent a request to " + friendsName + '.');
            this.player.setAskFriend(friendsName, cat);
        }
        final WcAddFriend waf = new WcAddFriend(this.player.getName(), this.player.getKingdomId(), friendsName, reply, this.player.hasFlag(2));
        if (Servers.isThisLoginServer()) {
            final byte where = waf.sendToPlayerServer(friendsName);
            if (where == 1) {
                this.sendSafeServerMessage(friendsName + " is unknown.");
            }
        }
        else {
            waf.sendToLoginServer();
        }
    }
    
    private void handleHashMessageSetReputation(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String pname = "Unknown";
            String rep = "0";
            int newreputation = 0;
            if (tokens.hasMoreTokens()) {
                try {
                    pname = tokens.nextToken().trim();
                    if (!tokens.hasMoreTokens()) {
                        this.sendSafeServerMessage("Usage: #setreputation <name> <new reputation>");
                        return;
                    }
                    rep = tokens.nextToken();
                    try {
                        newreputation = Integer.parseInt(rep);
                    }
                    catch (NumberFormatException nse) {
                        this.sendSafeServerMessage("Failed to parse " + rep + " to a valid number.");
                        return;
                    }
                    pname = LoginHandler.raiseFirstLetter(pname);
                    final Player p = Players.getInstance().getPlayer(pname);
                    if (this.player.getLogger() != null) {
                        this.player.getLogger().log(Level.INFO, this.player.getName() + " setting reputation of " + pname + " to " + newreputation);
                    }
                    if (p.getPower() < power) {
                        if (p.getReputation() > newreputation) {
                            p.getCommunicator().sendAlertServerMessage("Your reputation has been lowered by " + this.player.getName() + '!');
                        }
                        else {
                            p.getCommunicator().sendSafeServerMessage("Your reputation has been increased by " + this.player.getName() + '!');
                        }
                        p.setReputation(newreputation);
                        this.sendNormalServerMessage("The reputation of " + p.getName() + " now is " + p.getReputation() + '.');
                    }
                    else {
                        this.sendAlertServerMessage("You cannot change " + p.getName() + "'s reputation.");
                    }
                }
                catch (NoSuchPlayerException nsp) {
                    this.sendSafeServerMessage("No player found with the name " + pname);
                }
            }
        }
    }
    
    private void banSteamIdImplementation(final String message, final int power) {
        final String usage = "Usage: #bansteam <player/steamId> <days> <reason>";
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        final String identifier = tokens.hasMoreTokens() ? tokens.nextToken() : "";
        final int days = tokens.hasMoreTokens() ? Integer.parseInt(tokens.nextToken()) : 0;
        final String reason = tokens.hasMoreTokens() ? tokens.nextToken("") : "";
        if (identifier.isEmpty() || days == 0 || reason.isEmpty()) {
            this.sendAlertServerMessage("Usage: #bansteam <player/steamId> <days> <reason>");
            return;
        }
        Player ply = Players.getInstance().getPlayerOrNull(identifier);
        SteamId toBan = null;
        if (ply == null) {
            toBan = SteamId.fromAnyString(identifier);
            for (final Player p : Players.getInstance().getPlayerMap().values()) {
                if (p.getSteamId().equals(SteamId.fromAnyString(identifier))) {
                    ply = p;
                    break;
                }
            }
        }
        else {
            toBan = ply.getSteamId();
        }
        if (ply != null && ply.getPower() >= power) {
            this.sendAlertServerMessage("You cannot ban someone of equal or greater power and they've been informed.");
            if (ply.hasLink() && ply.getCommunicator() != null) {
                ply.getCommunicator().sendAlertServerMessage(this.player.getName() + " tried to ban your SteamId.");
            }
            if (this.player.getLogger() != null) {
                this.player.getLogger().log(Level.INFO, this.player.getName() + " tried to steamid ban " + ply.getName() + " (" + toBan + ") for " + days + " days. Reason: " + reason);
            }
            return;
        }
        Players.getInstance().addBan(new SteamIdBan(toBan, reason, System.currentTimeMillis() + days * 86400000L));
        if (this.player.getLogger() != null) {
            this.player.getLogger().log(Level.INFO, this.player.getName() + " banned " + toBan + " for " + days + " days. Reason: " + reason);
        }
        this.sendSafeServerMessage("You ban " + toBan + " for " + days + " days.");
        logInfo(this.player.getName() + " bans steamid " + toBan + " for " + days + " days. Reason " + reason);
        if (ply.hasLink() && ply.getCommunicator() != null) {
            ply.getCommunicator().sendAlertServerMessage("You have been banned for " + days + " days.");
        }
    }
    
    private void banIpImplementation(final String message, final int power) {
        boolean ok = true;
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        String ip = "";
        String reason = "";
        long expiry = 0L;
        int days = 0;
        if (tokens.hasMoreTokens() && ok) {
            try {
                ip = tokens.nextToken().trim();
                if (ip.charAt(0) != '/') {
                    ip = '/' + ip;
                }
            }
            catch (Exception ex) {
                ok = false;
            }
        }
        else {
            ok = false;
        }
        final int dots = ip.indexOf(42);
        if (dots > 0 && dots < 5) {
            this.sendAlertServerMessage("Failed to ban the ip. The ip address must be at least 5 characters long.");
            return;
        }
        final Player[] players = Players.getInstance().getPlayers();
        for (int x = 0; x < players.length; ++x) {
            if (players[x].hasLink()) {
                boolean ban = players[x].getCommunicator().getConnection().getIp().equals(ip);
                if (!ban && dots > 0) {
                    ban = players[x].getCommunicator().getConnection().getIp().startsWith(ip.substring(0, dots));
                }
                if (ban) {
                    if (players[x].getPower() < power) {
                        Players.getInstance().logoutPlayer(players[x]);
                    }
                    else {
                        ok = false;
                        this.sendNormalServerMessage("You cannot kick " + players[x].getName() + '!');
                        players[x].getCommunicator().sendAlertServerMessage(this.player.getName() + " tried to kick you from the game and ban your ip.");
                    }
                }
            }
        }
        if (tokens.hasMoreTokens() && ok) {
            try {
                days = Integer.parseInt(tokens.nextToken().trim());
                expiry = System.currentTimeMillis() + days * 86400000L;
            }
            catch (Exception ex2) {
                ok = false;
            }
        }
        else {
            ok = false;
        }
        if (tokens.hasMoreTokens() && ok) {
            try {
                while (tokens.hasMoreTokens()) {
                    reason = reason + tokens.nextToken() + ' ';
                }
                if (reason.length() < 4) {
                    this.sendAlertServerMessage("The reason " + reason + " seems a bit short. Please explain a bit more.");
                    return;
                }
            }
            catch (Exception ex2) {
                ok = false;
            }
        }
        else {
            ok = false;
        }
        if (!ok) {
            this.sendAlertServerMessage("Failed to ban the ip. Make sure you use the syntax #ban <ip> <days> <reason> like this: #ban 10.0.0.0 2 broke the rules.");
            return;
        }
        Players.getInstance().addBannedIp(ip, reason, expiry);
        if (this.player.getLogger() != null) {
            this.player.getLogger().log(Level.INFO, this.player.getName() + " banned " + ip + " for " + days + " days. Reason: " + reason);
        }
        this.sendSafeServerMessage("You ban " + ip + " for " + days + " days. The server won't accept connections from " + ip + " anymore.");
        logInfo(this.player.getName() + " bans ipaddress " + ip + " for " + days + " days. Reason " + reason);
        if (!message.startsWith("#baniphere")) {
            try {
                final LoginServerWebConnection c = new LoginServerWebConnection();
                this.sendSafeServerMessage(c.addBannedIp(ip, reason, days));
            }
            catch (Exception e) {
                this.sendAlertServerMessage("Failed to ban on login server:" + e.getMessage());
                logInfo(this.player.getName() + " banning ip on login server failed: " + e.getMessage(), e);
            }
        }
    }
    
    private void banPlayerImplementation(final String message, final int power) {
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        String bname = "";
        final StringBuilder reason = new StringBuilder("");
        long expiry = 0L;
        int days = 0;
        Label_0131: {
            if (tokens.hasMoreTokens()) {
                Label_0139: {
                    try {
                        bname = tokens.nextToken().trim();
                        bname = LoginHandler.raiseFirstLetter(bname);
                        this.player.getLogger().log(Level.INFO, "Trying to ban player " + bname + '.');
                        break Label_0139;
                    }
                    catch (Exception ex) {
                        this.sendAlertServerMessage("Failed to ban the player. Make sure you use the syntax #ban <name> <days> <reason> like this: #ban Griefer 2 broke the rules. Error=" + ex.getMessage());
                        return;
                    }
                    break Label_0131;
                }
                Label_0218: {
                    if (tokens.hasMoreTokens()) {
                        Label_0226: {
                            try {
                                days = Integer.parseInt(tokens.nextToken().trim());
                                expiry = System.currentTimeMillis() + days * 86400000L;
                                if (days == 0) {
                                    this.sendAlertServerMessage("Failed to ban the player - too few days. Make sure you use the syntax #ban <name> <days> <reason> like this: #ban Griefer 2 broke the rules.");
                                    return;
                                }
                                break Label_0226;
                            }
                            catch (Exception ex) {
                                this.sendAlertServerMessage("Failed to ban the player. Make sure you use the syntax #ban <name> <days> <reason> like this: #ban Griefer 2 broke the rules. Error=" + ex.getMessage());
                                return;
                            }
                            break Label_0218;
                        }
                        Label_0333: {
                            if (tokens.hasMoreTokens()) {
                                Label_0341: {
                                    try {
                                        while (tokens.hasMoreTokens()) {
                                            reason.append(tokens.nextToken()).append(' ');
                                        }
                                        if (reason.length() < 4) {
                                            this.sendAlertServerMessage("The reason " + (Object)reason + " seems a bit short. Please explain a bit more.");
                                            return;
                                        }
                                        break Label_0341;
                                    }
                                    catch (Exception ex) {
                                        this.sendAlertServerMessage("Failed to ban the player. Make sure you use the syntax #ban <name> <days> <reason> like this: #ban Griefer 2 broke the rules. Error: " + ex.getMessage());
                                        return;
                                    }
                                    break Label_0333;
                                    try {
                                        final Player toBan = Players.getInstance().getPlayer(bname);
                                        if (toBan.getPower() >= power) {
                                            this.sendNormalServerMessage("You cannot ban " + bname + '!');
                                            toBan.getCommunicator().sendNormalServerMessage(this.player.getName() + " tried to ban you from the game!");
                                            return;
                                        }
                                        if (!toBan.hasLink()) {
                                            throw new NoSuchPlayerException("Just went offline.");
                                        }
                                        final String bip = toBan.getCommunicator().getConnection().getIp();
                                        toBan.getCommunicator().sendAlertServerMessage("You have been banned for " + days + " days and thrown out from the game.");
                                        try {
                                            this.sendSafeServerMessage("You ban and kick " + bname + ". The server won't accept connections from " + bip + " anymore.");
                                            logInfo(this.player.getName() + " bans player " + bname + " and ipaddress " + bip + " for " + days + " days.");
                                            toBan.ban(reason.toString(), expiry);
                                            final WcGlobalModeration wcgm = new WcGlobalModeration(WurmId.getNextWCCommandId(), this.player.getName(), (byte)this.player.getPower(), false, false, false, true, false, 0, days, bname, reason.toString());
                                            if (Servers.localServer.LOGINSERVER) {
                                                wcgm.sendFromLoginServer();
                                            }
                                            else {
                                                wcgm.sendToLoginServer();
                                            }
                                            if (!message.startsWith("#banhere")) {
                                                try {
                                                    final LoginServerWebConnection c = new LoginServerWebConnection();
                                                    this.sendSafeServerMessage(c.ban(bname, reason.toString(), days));
                                                }
                                                catch (Exception e) {
                                                    this.sendAlertServerMessage("Failed to ban on login server:" + e.getMessage());
                                                    logInfo(this.player.getName() + " banning " + bname + " on login server failed: " + e.getMessage(), e);
                                                }
                                                try {
                                                    final LoginServerWebConnection c = new LoginServerWebConnection();
                                                    this.sendSafeServerMessage(c.addBannedIp(bip, "[" + bname + "] " + (Object)reason, days));
                                                }
                                                catch (Exception e) {
                                                    this.sendAlertServerMessage("Failed to ban on login server:" + e.getMessage());
                                                    logInfo(this.player.getName() + " banning ip on login server failed: " + e.getMessage(), e);
                                                }
                                            }
                                            if (this.player.getLogger() != null) {
                                                this.player.getLogger().log(Level.INFO, this.player.getName() + " banned " + bname + " for " + days + " days. Reason: " + (Object)reason);
                                            }
                                        }
                                        catch (Exception ex2) {
                                            logWarn("Error: " + this.player.getName() + " tries to ban player " + bname + " and ipaddress " + bip + " - " + ex2.getMessage(), ex2);
                                            this.sendAlertServerMessage("You try to ban and kick " + bname + ", but an error seems to have occured on the server.");
                                        }
                                    }
                                    catch (NoSuchPlayerException nsp) {
                                        final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(bname);
                                        try {
                                            pinf.load();
                                            pinf.setBanned(true, reason.toString(), expiry);
                                            final String bip2 = pinf.getIpaddress();
                                            Players.getInstance().addBannedIp(bip2, "[" + bname + "] " + (Object)reason, expiry);
                                            if (!message.startsWith("#banhere")) {
                                                try {
                                                    final LoginServerWebConnection c = new LoginServerWebConnection();
                                                    this.sendSafeServerMessage(c.ban(bname, reason.toString(), days));
                                                }
                                                catch (Exception e) {
                                                    this.sendAlertServerMessage("Failed to ban on login server:" + e.getMessage());
                                                    logInfo(this.player.getName() + " banning " + bname + " on login server failed: " + e.getMessage(), e);
                                                }
                                                try {
                                                    final LoginServerWebConnection c = new LoginServerWebConnection();
                                                    this.sendSafeServerMessage(c.addBannedIp(bip2, "[" + bname + "] " + (Object)reason, days));
                                                }
                                                catch (Exception e) {
                                                    this.sendAlertServerMessage("Failed to ban on login server:" + e.getMessage());
                                                    logInfo(this.player.getName() + " banning ip on login server failed: " + e.getMessage(), e);
                                                }
                                            }
                                            if (this.player.getLogger() != null) {
                                                this.player.getLogger().log(Level.INFO, this.player.getName() + " banned " + bname + " for " + days + " days. Reason: " + (Object)reason);
                                            }
                                            this.sendSafeServerMessage("You ban " + bname + " for " + days + " days. The server won't accept connections from " + bip2 + " anymore.");
                                            logInfo(this.player.getName() + " bans player " + bname + " and ipaddress " + bip2 + " for " + days + " days. Reason " + (Object)reason);
                                        }
                                        catch (IOException iox) {
                                            this.sendSafeServerMessage("Failed to locate player with the name " + bname + '.' + ". Banning globally.");
                                            final WcGlobalModeration wcgm2 = new WcGlobalModeration(WurmId.getNextWCCommandId(), this.player.getName(), (byte)this.player.getPower(), false, false, false, true, false, 0, days, bname, reason.toString());
                                            if (Servers.localServer.LOGINSERVER) {
                                                wcgm2.sendFromLoginServer();
                                                return;
                                            }
                                            wcgm2.sendToLoginServer();
                                        }
                                    }
                                }
                                return;
                            }
                        }
                        this.sendAlertServerMessage("Failed to ban the player. Make sure you use the syntax #ban <name> <days> <reason> like this: #ban Griefer 2 broke the rules.");
                        return;
                    }
                }
                this.sendAlertServerMessage("Failed to ban the player. Make sure you use the syntax #ban <name> <days> <reason> like this: #ban Griefer 2 broke the rules.");
                return;
            }
        }
        this.sendAlertServerMessage("Failed to ban the player. Make sure you use the syntax #ban <name> <days> <reason> like this: #ban Griefer 2 broke the rules.");
    }
    
    private void handleHashMessageBan(final String message, final int power) {
        if (power < 2) {
            return;
        }
        if (message.startsWith("#banip")) {
            this.banIpImplementation(message, power);
        }
        else if (message.startsWith("#bansteam")) {
            this.banSteamIdImplementation(message, power);
        }
        else {
            this.banPlayerImplementation(message, power);
        }
    }
    
    private void handleHashMessageDevTalk(final String message, final int power) throws IOException {
        if (power >= 3) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String bname = "";
            try {
                bname = tokens.nextToken().trim();
                bname = LoginHandler.raiseFirstLetter(bname);
            }
            catch (Exception ex) {
                this.sendNormalServerMessage("Syntax: #devtalk <name>. Error=" + ex.getMessage());
                return;
            }
            PlayerInfo pinf = null;
            Player p = null;
            try {
                p = Players.getInstance().getPlayer(bname);
                pinf = p.getSaveFile();
            }
            catch (NoSuchPlayerException nsp) {
                pinf = PlayerInfoFactory.createPlayerInfo(bname);
                pinf.load();
            }
            if (pinf.wurmId > 0L) {
                if (pinf.mayHearDevTalk) {
                    pinf.setDevTalk(false);
                    this.sendSafeServerMessage(bname + " may no longer hear devtalk.");
                    if (p != null) {
                        p.getCommunicator().sendNormalServerMessage("You may no longer hear the deities chit-chat.");
                    }
                    if (this.player.getLogger() != null) {
                        this.player.getLogger().log(Level.INFO, this.player.getName() + " removes " + bname + " from devtalk.");
                    }
                    final WcDemotion wc = new WcDemotion(WurmId.getNextWCCommandId(), this.player.getWurmId(), pinf.wurmId, (short)3);
                    if (Servers.localServer.LOGINSERVER) {
                        wc.sendFromLoginServer();
                    }
                    else {
                        wc.sendToLoginServer();
                    }
                }
                else {
                    pinf.setDevTalk(true);
                    this.sendSafeServerMessage(bname + " may now hear devtalk.");
                    if (p != null) {
                        p.getCommunicator().sendNormalServerMessage("You may now listen to the wise words of the gods.");
                    }
                    if (this.player.getLogger() != null) {
                        this.player.getLogger().log(Level.INFO, this.player.getName() + " adds " + bname + " to devtalk.");
                    }
                }
            }
            else {
                this.sendSafeServerMessage("No player with the name " + bname + " exists on this server.");
            }
        }
    }
    
    private void handleHashMessageToggleArtist(final String message, final int power) throws IOException {
        if (power >= 3) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String bname = "";
            boolean ok = true;
            try {
                bname = tokens.nextToken().trim();
                bname = LoginHandler.raiseFirstLetter(bname);
            }
            catch (Exception ex) {
                this.sendNormalServerMessage("Syntax: #artist <name> <sound (true or false)> <graphics (true or false)> . Error=" + ex.getMessage());
                return;
            }
            PlayerInfo pinf = null;
            Player p = null;
            try {
                p = Players.getInstance().getPlayer(bname);
                pinf = p.getSaveFile();
            }
            catch (NoSuchPlayerException nsp) {
                pinf = PlayerInfoFactory.createPlayerInfo(bname);
                pinf.load();
            }
            if (pinf.wurmId <= 0L) {
                this.sendSafeServerMessage("No player with the name " + bname + " exists on this server.");
                return;
            }
            if (tokens.hasMoreTokens()) {
                final String sound = tokens.nextToken();
                final boolean s = sound.equals("true");
                if (tokens.hasMoreTokens()) {
                    final String graphics = tokens.nextToken();
                    final boolean g = graphics.equals("true");
                    if (g || s) {
                        Players.addArtist(pinf.wurmId, s, g);
                        this.sendSafeServerMessage(bname + " is now an artist sound=" + s + ", graphics=" + g + ".");
                        if (p != null) {
                            p.getCommunicator().sendNormalServerMessage("You are now artist sound=" + s + ", graphics=" + g + ".");
                        }
                        if (this.player.getLogger() != null) {
                            this.player.getLogger().log(Level.INFO, this.player.getName() + " sets " + bname + " artist " + s + "," + g + ".");
                        }
                    }
                    else {
                        Players.deleteArtist(pinf.wurmId);
                        if (p != null) {
                            p.getCommunicator().sendNormalServerMessage("You no longer have artist privileges.");
                        }
                        this.sendSafeServerMessage(bname + " no longer has artist privileges.");
                        if (this.player.getLogger() != null) {
                            this.player.getLogger().log(Level.INFO, this.player.getName() + " sets " + bname + " artist " + bname + " sound=" + s + ", graphics=" + g + ".");
                        }
                    }
                }
                else {
                    ok = false;
                }
            }
            else {
                ok = false;
            }
            if (!ok) {
                this.sendNormalServerMessage("Syntax: #artist <name> <sound (true or false)> <graphics (true or false)>.");
            }
        }
    }
    
    private void handleHashMessageChatColour(final String message, final int power) {
        if (power >= 1 || this.player.mayMute()) {
            if (this.player.hasColoredChat) {
                this.player.hasColoredChat = false;
                this.player.customBlueChat = 0;
                this.player.customGreenChat = 140;
                this.player.customRedChat = 255;
                this.sendNormalServerMessage("You now chat with the colours of the common man and your chat colours have been reset.");
            }
            else {
                this.player.hasColoredChat = true;
                final StringTokenizer tokens = new StringTokenizer(message);
                tokens.nextToken();
                if (tokens.hasMoreTokens()) {
                    final String nt = tokens.nextToken();
                    if (nt.contains(",")) {
                        final String[] colours = nt.split(",");
                        if (colours.length != 3) {
                            this.sendNormalServerMessage("The value " + nt + " could not split into rgb values.");
                            return;
                        }
                        final int ired = Integer.parseInt(colours[0]);
                        final int cred = Math.max(1, Math.min(255, ired));
                        final int igreen = Integer.parseInt(colours[1]);
                        final int cgreen = Math.max(1, Math.min(255, igreen));
                        final int iblue = Integer.parseInt(colours[2]);
                        final int cblue = Math.max(1, Math.min(255, iblue));
                        this.player.customRedChat = cred;
                        this.player.customGreenChat = cgreen;
                        this.player.customBlueChat = cblue;
                    }
                    else {
                        try {
                            final int newColor = Integer.parseInt(nt);
                            this.player.customRedChat = (newColor >> 16 & 0xFF);
                            this.player.customGreenChat = (newColor >> 8 & 0xFF);
                            this.player.customBlueChat = (newColor & 0xFF);
                        }
                        catch (NumberFormatException nfe) {
                            this.sendNormalServerMessage("The value " + nt + " could not be parsed into an integer.");
                            return;
                        }
                    }
                }
                this.sendNormalServerMessage("You now chat with some fancy colour.");
            }
        }
    }
    
    private void handleMessageWarnings() {
        final long lastWarned = this.player.getSaveFile().getLastWarned();
        final String wst = this.player.getSaveFile().getWarningStats(lastWarned);
        this.sendNormalServerMessage(wst);
    }
    
    private void handleMessageToggleCommunityAssistant() {
        if (System.currentTimeMillis() - this.lastToggledPA > 30000L || this.player.getPower() > 1) {
            this.lastToggledPA = System.currentTimeMillis();
            if (this.player.seesPlayerAssistantWindow()) {
                this.player.togglePlayerAssistantWindow(false);
                this.sendNormalServerMessage("You will no longer receive CA messages.");
            }
            else if (Servers.localServer.HOMESERVER || Servers.localServer.EPIC) {
                Players.getInstance().sendPAWindow(this.player);
                this.player.togglePlayerAssistantWindow(true);
                this.sendNormalServerMessage("You will now receive CA messages.");
            }
            else {
                this.sendNormalServerMessage("Community assistants are not available on this server. Please ask the other players instead or use /support if you need help from the game administrators.");
            }
        }
        else {
            this.sendNormalServerMessage("You can only toggle this twice per minute.");
        }
    }
    
    private void handleMessageTransfer() {
        if (this.player.hasFlag(74)) {
            this.sendNormalServerMessage("You do not currently have a free transfer of faith available.");
            return;
        }
        if (this.player.isChampion()) {
            this.sendNormalServerMessage("Champions cannot convert faith with this command.");
            return;
        }
        if (this.player.getDeity() != null) {
            final TransferQuestion tq = new TransferQuestion(this.player, "Free transfer to a new faith", "Do you wish to receive a free transfer?");
            tq.sendQuestion();
            return;
        }
        this.sendNormalServerMessage("You do not currently have a deity to transfer away from.");
    }
    
    private void handleMessageStopCaring() {
        final int nums = Creatures.getInstance().setNoCreaturesProtectedBy(this.player.getWurmId());
        if (nums == 0) {
            this.sendNormalServerMessage("You didn't care specially for any creature.");
        }
        if (nums == 1) {
            this.sendNormalServerMessage("You no longer care for the one creature you used to.");
        }
        else {
            this.sendNormalServerMessage("You no longer care for any of the " + nums + " creatures you used to.");
        }
    }
    
    private void handleMessageShowCaringFor() {
        final Creature[] creatures = Creatures.getInstance().getProtectedCreaturesFor(this.player.getWurmId());
        if (creatures.length > 0) {
            final StringBuilder buf = new StringBuilder();
            buf.append("You are caring for:");
            boolean first = true;
            for (final Creature creature : creatures) {
                if (first) {
                    first = false;
                }
                else {
                    buf.append(",");
                }
                buf.append("  " + creature.getNameWithoutPrefixes());
            }
            this.sendNormalServerMessage(buf.toString());
        }
        else {
            this.sendNormalServerMessage("You are not caring for any creatures.");
        }
    }
    
    private void handleMessageSnipe(final String message) throws IOException {
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        String pname = "x";
        if (tokens.hasMoreTokens()) {
            pname = tokens.nextToken().trim();
            pname = LoginHandler.raiseFirstLetter(pname);
        }
        if (!pname.equals("x")) {
            boolean muteGlobal = false;
            final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(pname);
            try {
                pinf.load();
                if (pinf.getCurrentServer() == Servers.localServer.id) {
                    attemptMuting(this.player, pinf, false, false);
                }
                else {
                    muteGlobal = true;
                }
            }
            catch (IOException iox) {
                muteGlobal = true;
            }
            if (muteGlobal) {
                this.sendNormalServerMessage("Trying to snipe " + pname + " globally.");
                attemptMuting(this.player, pinf, true, false);
            }
        }
    }
    
    private void handleMessageIgnore(final String message) throws IOException {
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        String pname = "x";
        if (tokens.hasMoreTokens()) {
            pname = tokens.nextToken().trim();
            pname = LoginHandler.raiseFirstLetter(pname);
        }
        if (!pname.equals("x")) {
            final PlayerState pstate = PlayerInfoFactory.getPlayerState(pname);
            if (pstate == null) {
                this.sendNormalServerMessage("Unknown player " + pname + '.');
                return;
            }
            final long pid = pstate.getPlayerId();
            boolean muteGlobal = false;
            final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(pname);
            try {
                if (this.player.isIgnored(pid)) {
                    if (pstate.getServerId() == Servers.localServer.id) {
                        if (this.player.removeIgnored(pid)) {
                            this.sendNormalServerMessage("You no longer ignore " + pname + '.');
                        }
                        else {
                            this.sendNormalServerMessage("The server failed to execute your remove ignore request.");
                        }
                    }
                    else {
                        final WcGlobalIgnore wgi = new WcGlobalIgnore(WurmId.getNextWCCommandId(), this.player.getWurmId(), this.player.getName(), pid, pname, false, false, false, false, true, this.player.getKingdomId());
                        if (Servers.isThisLoginServer()) {
                            wgi.sendToServer(pstate.getServerId());
                        }
                        else {
                            wgi.sendToLoginServer();
                        }
                    }
                }
                else if (pstate.getServerId() == Servers.localServer.id) {
                    pinf.load();
                    if (pinf.getPower() > 1 || pinf.mayMute) {
                        this.sendNormalServerMessage("You may not ignore " + pname + '.');
                    }
                    else if (this.player.addIgnored(pid)) {
                        this.sendNormalServerMessage("You now ignore " + pname + '.');
                        attemptMuting(this.player, pinf, false, true);
                    }
                    else {
                        this.sendNormalServerMessage("The server failed to execute your ignore request.");
                    }
                }
                else {
                    muteGlobal = true;
                }
            }
            catch (IOException iox) {
                muteGlobal = true;
            }
            if (muteGlobal) {
                attemptMuting(this.player, pinf, true, true);
            }
        }
        else {
            final long[] ids = this.player.getIgnored();
            String nlist = "You are ignoring ";
            for (int x = 0; x < ids.length; ++x) {
                final PlayerState pstate2 = PlayerInfoFactory.getPlayerState(ids[x]);
                String next = "unknown?";
                if (pstate2 != null) {
                    next = pstate2.getPlayerName();
                }
                if (x == ids.length - 1) {
                    nlist += next;
                }
                else if (x == ids.length - 2) {
                    nlist = nlist + next + " and ";
                }
                else {
                    nlist = nlist + next + ", ";
                }
                if (next.equals("unknown?")) {
                    this.player.removeIgnored(ids[x]);
                    this.sendNormalServerMessage("Deleted player detected, removing them from your ignored players list. Please execute command again to confirm they are gone.");
                }
            }
            if (ids.length == 0) {
                nlist += "nobody";
            }
            nlist += '.';
            this.sendNormalServerMessage(nlist);
        }
    }
    
    private void handleMessageChangePassword(final String message) throws Exception {
        final StringTokenizer tokens = new StringTokenizer(message);
        if (tokens.countTokens() == 3) {
            tokens.nextToken();
            String oldpassword = "Unknown";
            oldpassword = tokens.nextToken().trim();
            String newpassword = "Unknown2";
            newpassword = tokens.nextToken().trim();
            if (LoginHandler.hashPassword(oldpassword, LoginHandler.encrypt(LoginHandler.raiseFirstLetter(this.player.getSaveFile().getName()))).equals(this.player.getSaveFile().getPassword())) {
                if (newpassword.length() >= 6 && newpassword.length() <= 20) {
                    final LoginServerWebConnection lsw = new LoginServerWebConnection();
                    this.sendNormalServerMessage(lsw.changePassword(this.player.getName(), this.player.getName(), newpassword, this.player.getPower()));
                }
                else {
                    this.sendNormalServerMessage("The password must be between 6 and 20 characters long.");
                }
            }
            else {
                this.sendNormalServerMessage("The old password you submitted is not correct.");
            }
        }
        else {
            this.sendNormalServerMessage("The syntax to change the password is '/password <oldpassword> <newpassword>', nothing else.");
        }
    }
    
    private void handleMessageToggleInvitations() {
        if (this.player.acceptsInvitations()) {
            this.player.acceptsInvitations = false;
            this.sendNormalServerMessage("You will no longer accept invitations to change kingdom or religion.");
        }
        else {
            if (this.player.isUndead()) {
                this.sendNormalServerMessage("Urrr. Urrr.. Nooo..");
                return;
            }
            this.player.acceptsInvitations = true;
            this.sendNormalServerMessage("You will now accept invitations to change kingdom and religion.");
        }
    }
    
    private void handleMessageReputation() {
        final int rep = this.player.getReputation();
        this.sendNormalServerMessage("Your reputation is " + rep + '.');
        if (this.player.getKingdomTemplateId() != 3) {
            if (rep > 20) {
                this.sendNormalServerMessage("You raise no suspicion.");
            }
            else if (rep >= 0) {
                this.sendNormalServerMessage("You are close to becoming an outlaw, which would mean that other players can kill you on sight.");
            }
            else if (rep > -80) {
                this.sendNormalServerMessage("You are an outlaw, and may be killed on sight by other players.");
            }
            else if (rep >= -100) {
                this.sendNormalServerMessage("You are an outlaw, and may be killed on sight by other players. Soon the kingdom guards will start to attack you.");
            }
            else if (rep > -180) {
                this.sendNormalServerMessage("You are an outlaw. Other players and the kingdom guards will attack you on sight.");
            }
            else if (rep >= -200) {
                this.sendNormalServerMessage("You are an outlaw. Other players and kingdom guards may attack you on sight.");
                this.sendAlertServerMessage("You are very close to joining the Horde of The Summoned!");
            }
        }
    }
    
    private void handleMessageLogoutTime() {
        this.sendSafeServerMessage("You would leave the world in " + this.player.getSecondsToLogout() + " seconds if you disconnected now.");
    }
    
    private void handleMessageBattleRank() {
        this.sendNormalServerMessage("Your current battle rank is " + this.player.getRank() + '.');
    }
    
    private void handleMessageMaxBattleRanks() {
        this.sendNormalServerMessage("Historic Battle Masters:");
        final Map<String, Integer> map = Players.getMaxBattleRanks(10);
        for (final Map.Entry<String, Integer> entry : map.entrySet()) {
            this.sendNormalServerMessage(entry.getKey() + ' ' + (int)entry.getValue());
        }
    }
    
    private void handleMessageBattleRanks() {
        this.sendNormalServerMessage("Current Battle Masters:");
        final Map<String, Integer> map = Players.getBattleRanks(10);
        for (final Map.Entry<String, Integer> entry : map.entrySet()) {
            this.sendNormalServerMessage(entry.getKey() + ' ' + (int)entry.getValue());
        }
    }
    
    private void handleMessageChampRanks() {
        this.sendSafeServerMessage("Current Eternal Records:");
        final WurmRecord[] championRecords;
        final WurmRecord[] allRecs = championRecords = PlayerInfoFactory.getChampionRecords();
        for (final WurmRecord record : championRecords) {
            if (record.isCurrent()) {
                this.sendNormalServerMessage(record.getHolder() + ' ' + record.getValue());
            }
        }
        this.sendSafeServerMessage("Older Eternal Records:");
        for (final WurmRecord record : allRecs) {
            if (!record.isCurrent()) {
                this.sendNormalServerMessage(record.getHolder() + ' ' + record.getValue());
            }
        }
    }
    
    private void handleMessageReleaseCorpses() {
        Zones.releaseAllCorpsesFor(this.player);
        this.sendNormalServerMessage("Your corpses may now be looted by anyone.");
    }
    
    private void handleMessageTestChallengeShutdown() {
        Players.getInstance().setChallengeStep(Players.getInstance().getChallengeStep() + 1);
        if (Players.getInstance().getChallengeStep() > 4) {
            Players.getInstance().setChallengeStep(0);
        }
    }
    
    private void handleMessageRandomNumber(final String message) {
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        int rand = 100;
        if (tokens.hasMoreTokens()) {
            final String numo = tokens.nextToken().trim();
            try {
                rand = Integer.parseInt(numo);
            }
            catch (NumberFormatException ex) {}
        }
        rand = Math.min(10000000, rand);
        rand = Math.max(2, rand);
        this.player.getCurrentTile().broadCast(this.player.getName() + " rolls " + (Server.rand.nextInt(rand) + 1) + "/" + rand + ".");
    }
    
    private void handleMessageRemoveFriend(final String message) throws IOException {
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        if (!tokens.hasMoreTokens()) {
            this.sendAlertServerMessage("You must specify a player to remove!");
        }
        else {
            String friendName = tokens.nextToken().trim();
            friendName = LoginHandler.raiseFirstLetter(friendName);
            final long friendWurmId = this.player.removeFriend(friendName);
            this.player.removeMeFromFriendsList(friendWurmId, friendName);
        }
    }
    
    private void handleMessageRevokeVillage(final String message) {
        if (message.equals("/revoke")) {
            this.sendNormalServerMessage("As a safety measure you have to type your village name as part of the command: revoke <villagename>");
        }
        else if (this.player.citizenVillage != null) {
            String vname = message.substring("/revoke ".length(), message.length());
            vname = vname.toLowerCase();
            if (vname.equals(this.player.citizenVillage.getName().toLowerCase())) {
                final Citizen cz = this.player.citizenVillage.getCitizen(this.player.getWurmId());
                final VillageRole role = cz.getRole();
                if (role.getStatus() == 2) {
                    this.sendNormalServerMessage("You cannot revoke the citizenship of the mayor this way. You have to give away the village deed.");
                }
                else {
                    this.player.citizenVillage.removeCitizen(cz);
                }
            }
            else {
                this.sendNormalServerMessage("You may not revoke your citizenship in the village of '" + vname + "' since you are a citizen of '" + this.player.citizenVillage.getName().toLowerCase() + "'.");
            }
        }
        else {
            this.sendNormalServerMessage("You are not citizen of a village and therefor may not revoke your citizenship in it.");
        }
    }
    
    private void handleMessageChallengeKing() {
        if (this.player.isKing()) {
            MethodsCreatures.sendChallengeKingQuestion(this.player);
        }
    }
    
    private void handleMessageToggleSleep() {
        if (this.player.getSaveFile().getSleepLeft() <= 0) {
            this.sendNormalServerMessage("You do not have any sleep bonus. You can gain some by eating a sleep powder or sleeping in a bed.");
        }
        else {
            if (this.player.getSaveFile().frozenSleep && System.currentTimeMillis() - this.player.getSaveFile().lastToggledFSleep < 300000L) {
                this.sendNormalServerMessage("You need to wait " + Server.getTimeFor(this.player.getSaveFile().lastToggledFSleep + 300000L - System.currentTimeMillis()) + " until you can toggle sleep bonus again.");
            }
            else if (this.player.getSaveFile().frozenSleep) {
                this.player.getSaveFile().lastToggledFSleep = System.currentTimeMillis();
                this.player.getSaveFile().frozenSleep = false;
                this.sendNormalServerMessage("You start using your sleep bonus.");
                if (this.player.isSBIdleOffEnabled()) {
                    this.sendNormalServerMessage("Your sleep bonus will auto-freeze after " + Server.getTimeFor(600000L) + " of inactivity.");
                }
                this.player.resetInactivity(true);
            }
            else {
                this.player.getSaveFile().frozenSleep = true;
                this.sendNormalServerMessage("You refrain from using your sleep bonus.");
            }
            this.sendSleepInfo();
        }
    }
    
    private void handleMessageMissionInformation() {
        this.sendNormalServerMessage("Current instruction:");
        final SimplePopup popup = new SimplePopup(this.player, "Current mission", this.player.getCurrentMissionInstruction());
        popup.sendQuestion();
    }
    
    private void handleMessageHelp() {
        this.sendHelp();
        if (this.player.getPower() >= 1) {
            this.sendDeityHelp(this.player.getPower());
        }
    }
    
    private void handleMessageVote(final String message) {
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        String pname = "Unknown";
        pname = tokens.nextToken().trim();
        pname = LoginHandler.raiseFirstLetter(pname);
        try {
            final Village vill = this.player.getCitizenVillage();
            if (vill != null) {
                vill.vote(this.player, pname);
            }
            else {
                this.sendSafeServerMessage("You are not citizen of a village, hence you cannot vote!");
            }
        }
        catch (IOException iox) {
            this.sendSafeServerMessage("Failed to vote for " + pname + ". An error occured on the server.");
            logWarn(iox.getMessage(), iox);
        }
        catch (NoSuchPlayerException nsp) {
            this.sendSafeServerMessage("Failed to vote for " + pname + ". Please verify that the name is correct.");
        }
    }
    
    private void handleMessageUptime() {
        this.sendNormalServerMessage("The server has been up " + Server.getTimeFor(System.currentTimeMillis() - Server.getStartTime()) + '.');
    }
    
    private void handleMessageTitleSelect() {
        if (Features.Feature.COMPOUND_TITLES.isEnabled()) {
            final TitleCompoundQuestion tcq = new TitleCompoundQuestion(this.player, "Select your titles", "Which titles do you want to use?", this.player.getWurmId());
            tcq.sendQuestion();
        }
        else {
            final TitleQuestion tq = new TitleQuestion(this.player, "Select your title", "Which title do you want to use?", this.player.getWurmId());
            tq.sendQuestion();
        }
    }
    
    private void handleMessageSleepBonus() {
        if (!this.player.hasSleepBonus()) {
            if (this.player.getSaveFile().sleep > 0 && this.player.getSaveFile().frozenSleep) {
                this.sendNormalServerMessage("You have " + Server.getTimeFor(this.player.getSaveFile().sleep * 1000) + " left of your sleep bonus, which is frozen.");
            }
            else {
                this.sendNormalServerMessage("You have no bonus from sleep.");
            }
        }
        else {
            this.sendNormalServerMessage("You have " + Server.getTimeFor(this.player.getSaveFile().sleep * 1000) + " left of your sleep bonus.");
        }
    }
    
    private void handleMessageTitle() {
        String suff = "";
        if (this.player.isKing()) {
            suff = suff + " [" + King.getRulerTitle(this.player.getSex() == 0, this.player.getKingdomId()) + ']';
        }
        final Titles.Title tempTitle = this.player.getTitle();
        if (tempTitle != null) {
            if (tempTitle.isRoyalTitle()) {
                suff = suff + " [" + this.player.getKingdomTitle() + ']';
            }
            else {
                suff = suff + " [" + tempTitle.getName(this.player.isNotFemale()) + ']';
            }
        }
        if (Features.Feature.COMPOUND_TITLES.isEnabled()) {
            final Titles.Title tempSecondTitle = this.player.getSecondTitle();
            if (tempSecondTitle != null) {
                if (tempSecondTitle.isRoyalTitle()) {
                    suff = suff + " [" + this.player.getKingdomTitle() + ']';
                }
                else {
                    suff = suff + " [" + tempSecondTitle.getName(this.player.isNotFemale()) + ']';
                }
            }
        }
        if (this.player.isChampion()) {
            suff = suff + " [Champion of " + this.player.getDeity().name + ']';
        }
        if (this.player.getTitle() == null) {
            this.sendNormalServerMessage("You are currently using no title.");
        }
        else {
            this.sendNormalServerMessage("You are currently using the titles " + suff + '.');
        }
    }
    
    private void handleMessageLives() {
        if (this.player.isChampion()) {
            if (this.player.getSaveFile().realdeath - 1 == 0) {
                this.sendAlertServerMessage("The next time you return from the underworld you will no longer be a Champion.");
            }
            else {
                this.sendNormalServerMessage("You may visit the underworld as a Champion " + this.player.getSaveFile().realdeath + " times more.");
            }
            if (System.currentTimeMillis() - this.player.getSaveFile().championTimeStamp < 14515200000L) {
                final String timefor = Server.getTimeFor(14515200000L + this.player.getChampTimeStamp() - System.currentTimeMillis());
                this.sendNormalServerMessage("You will stay champion for " + timefor + ".");
            }
            else {
                this.sendNormalServerMessage("You may become champion whenever you meet the prerequisites.");
            }
        }
        else {
            this.sendNormalServerMessage("You are not the Champion of a deity.");
        }
    }
    
    private void handleMessageConverts() {
        this.sendNormalServerMessage("You have changed kingdom " + this.player.getSaveFile().getChangedKingdom() + " times.");
        if (Servers.localServer.isChallengeServer() && this.player.getPower() <= 0) {
            this.sendNormalServerMessage("You may not change kingdom on this server.");
            return;
        }
        if (System.currentTimeMillis() - this.player.getSaveFile().lastChangedKindom < this.player.getChangeKingdomLimit()) {
            final String timefor = Server.getTimeFor(this.player.getChangeKingdomLimit() + this.player.getSaveFile().lastChangedKindom - System.currentTimeMillis());
            this.sendNormalServerMessage("You may change kingdom in " + timefor + ".");
        }
        else {
            this.sendNormalServerMessage("You may change kingdom now.");
        }
    }
    
    private void handleMessageGoMolRehan() {
        this.sendNormalServerMessage("This command is no longer active.");
    }
    
    private void handleMessageChangeEmail(final String message) throws IOException {
        if (System.currentTimeMillis() - this.lastChangedEmail > 30000L || Servers.localServer.testServer) {
            final ChangeEmailQuestion ceq = new ChangeEmailQuestion(this.player);
            ceq.sendQuestion();
            this.lastChangedEmail = System.currentTimeMillis();
        }
        else {
            this.sendNormalServerMessage("You may only change email twice per minute. Please wait a while.");
        }
    }
    
    private void handleMessageStuck() throws NoSuchZoneException {
        if (this.player.isDead()) {
            this.sendNormalServerMessage("You are dead.");
        }
        else if (this.player.isTeleporting() || this.player.getMovementScheme().isIntraTeleporting()) {
            this.sendNormalServerMessage("You are teleporting and cannot use this command.");
        }
        else if (this.player.stuckCounter > 0) {
            this.sendNormalServerMessage("Try again in " + this.player.stuckCounter + " second/s.");
        }
        else if (this.player.getVehicle() != -10L) {
            this.sendNormalServerMessage("You must disembark first.");
        }
        else if (this.player.getLayer() < 0) {
            this.sendNormalServerMessage("Stuck does not work below ground. You need to use /suicide instead.");
        }
        else if (this.player.isFighting()) {
            this.sendNormalServerMessage("You cannot do this while you're in combat!");
        }
        else {
            final int tilex = this.player.getCurrentTile().tilex;
            final int tiley = this.player.getCurrentTile().tiley;
            try {
                final float tHeight = Zones.calculateHeight(this.player.getPosX(), this.player.getPosY(), this.player.isOnSurface());
                if (this.player.getPositionZ() - 0.5f > tHeight) {
                    this.sendNormalServerMessage("You need to be firmly on the ground.");
                    return;
                }
            }
            catch (Exception ex) {
                this.sendNormalServerMessage("You are in a bad place. You need to use /suicide instead.");
                return;
            }
            final short[] heights = Creature.getTileSteepness(tilex, tiley, this.player.isOnSurface());
            if (heights[1] > 50) {
                this.sendNormalServerMessage("This area is too steep to jump in. Try relogging and worst case use /suicide instead.");
            }
            else {
                boolean ok = true;
                for (int x = 0; x < 10; ++x) {
                    final float posx = tilex * 4 + 1 + Server.rand.nextInt(2);
                    final float posy = tiley * 4 + 1 + Server.rand.nextInt(2);
                    final VolaTile t = Zones.getTileOrNull((int)posx >> 2, (int)posy >> 2, this.player.isOnSurface());
                    if (t != null) {
                        final Structure struct = t.getStructure();
                        if (struct != null && struct.isFinished()) {
                            ok = false;
                            final Item[] keys2;
                            final Item[] keys = keys2 = this.player.getKeys();
                            for (final Item lKey : keys2) {
                                if (lKey.getWurmId() == struct.getWritId()) {
                                    ok = true;
                                }
                            }
                            if (struct.mayPass(this.player)) {
                                ok = true;
                            }
                        }
                    }
                    if (ok) {
                        this.player.intraTeleport(posx, posy, Zones.calculateHeight(posx, posy, this.player.isOnSurface()), this.player.getStatus().getRotation(), this.player.getLayer(), "Stuck");
                        this.player.stuckCounter = 30;
                        break;
                    }
                }
                if (!ok) {
                    this.sendNormalServerMessage("Failed to locate a proper place to teleport to. You may have to use /suicide or relog instead.");
                }
            }
        }
    }
    
    private void handleMessageWeather() {
        this.sendNormalServerMessage(Server.getWeather().getWeatherString(this.player.getPower() >= 3));
    }
    
    private void handleMessageAttackers() {
        final long[] attids = this.player.getLatestAttackers();
        this.sendNormalServerMessage("Latest attackers:");
        if (attids.length == 0) {
            this.sendNormalServerMessage("None.");
        }
        else {
            for (int x = 0; x < attids.length; ++x) {
                try {
                    final Creature cret = Server.getInstance().getCreature(attids[x]);
                    this.sendNormalServerMessage(cret.getName());
                }
                catch (NoSuchPlayerException ex) {}
                catch (NoSuchCreatureException ex2) {}
            }
        }
    }
    
    private void handleMessageRespawn() {
        if (this.player.isDead()) {
            this.player.sendSpawnQuestion();
        }
        else {
            this.sendNormalServerMessage("You are not dead yet.");
        }
    }
    
    private void handleMessageWho() {
        LoginHandler.sendWho(this.player, false);
    }
    
    private void handleMessageSendTestLetter(final String mess) {
        final StringTokenizer t = new StringTokenizer(mess);
        t.nextToken();
        if (t.hasMoreTokens()) {
            final String num = t.nextToken();
            try {
                final int number = Integer.parseInt(num);
                if (number == 1) {
                    PlayerInfoFactory.sendDeleteLetter(this.player.getSaveFile());
                }
                if (number == 2) {
                    PlayerInfoFactory.sendDeletePreventLetter(this.player.getSaveFile());
                }
                if (number == 3) {
                    PlayerInfoFactory.sendPremiumWarningLetter(this.player.getSaveFile());
                }
            }
            catch (Exception ex) {
                this.sendAlertServerMessage(ex.getMessage());
            }
        }
    }
    
    private void handleMessageTimePlayed() {
        final Item inventory = this.player.getInventory();
        if (inventory != null) {
            this.sendNormalServerMessage("You entered through the portal to Wurm on " + WurmCalendar.getDateFor(inventory.creationDate) + " That's " + Server.getTimeFor(System.currentTimeMillis() - this.player.getSaveFile().creationDate) + " ago.");
        }
        final long timePlayed = this.player.getPlayingTime();
        final short days = (short)(timePlayed / 86400000L);
        final long millsoverdays = timePlayed % 86400000L;
        final short hours = (short)(millsoverdays / 3600000L);
        final long minsmillis = millsoverdays % 3600000L;
        final short minutes = (short)(minsmillis / 60000L);
        this.sendNormalServerMessage("You have played " + days + " days, " + hours + " hours and " + minutes + " minutes.");
        if (this.player.getSaveFile().awards != null) {
            this.sendNormalServerMessage("You have been premium a total of " + this.player.getSaveFile().awards.getMonthsPaidEver() + " months until Dec 2013.");
            this.sendNormalServerMessage("You have been premium a total of " + this.player.getSaveFile().awards.getMonthsPaidSinceReset() + " months since Dec 2013.");
            this.sendNormalServerMessage("You have been premium consecutively for " + this.player.getSaveFile().awards.getMonthsPaidInARow() + " months since Dec 2013.");
        }
        if (this.player.isReallyPaying()) {
            this.sendNormalServerMessage("You have premium time until " + WurmCalendar.formatGmt(this.player.getPaymentExpire()));
        }
        else {
            this.sendNormalServerMessage("You have not paid for premium time.");
        }
    }
    
    private void handleMessageFatigue() {
        int time = this.player.getFatigueLeft();
        time *= 1000;
        final String t = Server.getTimeFor(time);
        this.sendNormalServerMessage("You have " + t + " left.");
    }
    
    private void handleMessageSignInOut(final String message) {
        if (this.player.canSignIn()) {
            final StringTokenizer tokens = new StringTokenizer(message);
            final String inout = tokens.nextToken();
            String msg = "";
            while (tokens.hasMoreTokens()) {
                msg = msg + ' ' + tokens.nextToken();
            }
            if (inout.equals("/signin")) {
                if (this.player.isSignedIn()) {
                    this.sendNormalServerMessage("You are already signed in.");
                }
                else {
                    this.signIn(msg);
                    this.sendNormalServerMessage("You are now signed in.");
                }
            }
            else if (inout.equals("/signout")) {
                if (!this.player.isSignedIn()) {
                    this.sendNormalServerMessage("You are already signed out.");
                }
                else {
                    this.signOut(msg);
                    this.sendNormalServerMessage("You are now signed out.");
                }
            }
            else {
                this.sendNormalServerMessage("unknown command " + inout);
            }
        }
    }
    
    public void signIn(final String aMsg) {
        this.signInOut(true, "Signing in. " + aMsg);
    }
    
    public void signOut(final String aMsg) {
        this.signInOut(false, "Signing out. " + aMsg);
    }
    
    private void signInOut(final boolean inOut, final String msg) {
        if (inOut && this.player.isSignedIn()) {
            if (this.player.getPower() < 2) {
                this.sendMGMTMessage(msg, true);
            }
        }
        else if (this.player.isTransferring()) {
            if (this.player.getPower() < 2) {
                this.sendMGMTMessage(msg, true);
            }
        }
        else {
            this.player.setSignedIn(inOut);
            if (this.player.mayHearDevTalk()) {
                this.sendGMMessage(msg, true);
            }
            if (this.player.getPower() < 2) {
                this.sendMGMTMessage(msg, true);
            }
        }
    }
    
    private void handleMessageGameMasterChat(final String message) {
        if (!this.player.isMute()) {
            if (this.player.mayHearMgmtTalk() && this.player.mayMute()) {
                final StringTokenizer tokens = new StringTokenizer(message);
                tokens.nextToken();
                String toSend = "";
                if (tokens.hasMoreTokens()) {
                    toSend = tokens.nextToken();
                }
                while (tokens.hasMoreTokens()) {
                    toSend = toSend + ' ' + tokens.nextToken();
                }
                final Message mess = this.sendGMMessage(toSend, false);
                if (!this.player.mayHearDevTalk()) {
                    this.sendMessage(mess);
                }
            }
            else {
                this.sendNormalServerMessage("You may not use that channel.");
            }
        }
    }
    
    private Message sendGMMessage(final String msg, final boolean addPlayerType) {
        final Message mess = new Message(this.player, (byte)11, "GM", "<" + this.getPlayerName(addPlayerType) + "> " + msg);
        Server.getInstance().addMessage(mess);
        Players.addGmMessage(this.getPlayerName(addPlayerType), msg);
        if (msg.trim().length() > 1) {
            final WCGmMessage wc = new WCGmMessage(WurmId.getNextWCCommandId(), this.getPlayerName(addPlayerType), "(" + Servers.localServer.getAbbreviation() + ") " + msg, false);
            if (Servers.localServer.LOGINSERVER) {
                wc.sendFromLoginServer();
            }
            else {
                wc.sendToLoginServer();
            }
        }
        return mess;
    }
    
    private String getPlayerName(final boolean addPlayerType) {
        if (!addPlayerType) {
            return this.player.getName();
        }
        if (this.player.getPower() == 2) {
            return "GM " + this.player.getName();
        }
        if (this.player.getPower() == 4) {
            return "ARCH " + this.player.getName();
        }
        if (this.player.getPower() > 2) {
            return "GOD " + this.player.getName();
        }
        if (this.player.mayMute()) {
            return "CM " + this.player.getName();
        }
        if (this.player.isPlayerAssistant()) {
            return "CA " + this.player.getName();
        }
        return this.player.getName();
    }
    
    private void handleMessageShout(final String message) {
        if (!this.player.isMute()) {
            if (this.player.isKingdomChat()) {
                if (this.invulnerable) {
                    this.sendAlertServerMessage("You may not use kingdom chat until you have moved and lost invulnerability.");
                }
                else {
                    final StringTokenizer tokens = new StringTokenizer(message);
                    tokens.nextToken();
                    String toSend = "";
                    if (tokens.hasMoreTokens()) {
                        toSend = tokens.nextToken();
                    }
                    while (tokens.hasMoreTokens()) {
                        toSend = toSend + ' ' + tokens.nextToken();
                    }
                    toSend = this.drunkGarble(toSend);
                    this.sendToLocalKingdomChat(toSend);
                }
            }
            else if (!this.player.isUndead()) {
                this.sendNormalServerMessage("You must toggle Kingdom chat on your profile.");
            }
        }
    }
    
    public void sendToLocalKingdomChat(final String message) {
        final Message mess = new Message(this.player, (byte)10, Kingdoms.getChatNameFor(this.player.getKingdomId()), "<" + this.player.getName() + "> " + message);
        if (message.trim().length() > 1) {
            Server.getInstance().addMessage(mess);
            this.player.chatted();
        }
        else {
            this.sendMessage(mess);
        }
    }
    
    private void handleMessageKingdomChat(final String message) {
        if (Communicator.gchatdisabled) {
            this.sendNormalServerMessage("Global chat is currently disabled on this server.");
        }
        else if (!this.player.isMute()) {
            if (this.player.isGlobalChat()) {
                if (Servers.localServer.entryServer && !this.player.isReallyPaying() && !this.player.mayMute()) {
                    this.sendNormalServerMessage("You may not use global kingdom chat as a non-premium until you use a portal.");
                }
                else if (this.invulnerable) {
                    this.sendAlertServerMessage("You may not use global kingdom chat until you have moved and lost invulnerability.");
                }
                else {
                    final StringTokenizer tokens = new StringTokenizer(message);
                    tokens.nextToken();
                    String toSend = "";
                    if (tokens.hasMoreTokens()) {
                        toSend = tokens.nextToken();
                    }
                    while (tokens.hasMoreTokens()) {
                        toSend = toSend + ' ' + tokens.nextToken();
                    }
                    toSend = this.drunkGarble(toSend);
                    this.sendToGlobalKingdomChat(toSend);
                    this.player.chatted();
                }
            }
            else if (!this.player.isUndead()) {
                this.sendNormalServerMessage("You must toggle global Kingdom chat on your profile.");
            }
        }
    }
    
    public void sendToGlobalKingdomChat(final String message) {
        final Message mess = new Message(this.player, (byte)16, "GL-" + Kingdoms.getChatNameFor(this.player.getKingdomId()), "<" + this.player.getName() + "> " + message);
        mess.setSenderKingdom(this.player.getKingdomId());
        if (message.trim().length() > 1) {
            Server.getInstance().addMessage(mess);
            final WcKingdomChat wc = new WcKingdomChat(WurmId.getNextWCCommandId(), this.player.getWurmId(), this.player.getName(), message, false, this.player.getKingdomId(), this.player.hasColoredChat() ? this.player.getCustomRedChat() : -1, this.player.hasColoredChat() ? this.player.getCustomGreenChat() : -1, this.player.hasColoredChat() ? this.player.getCustomBlueChat() : -1);
            if (Servers.localServer.LOGINSERVER) {
                wc.sendFromLoginServer();
            }
            else {
                wc.sendToLoginServer();
            }
        }
        else {
            this.sendMessage(mess);
        }
    }
    
    public void sendToTradeChannel(final String message) {
        final Message mess = new Message(this.player, (byte)18, "Trade", "<" + this.player.getName() + "> " + message);
        mess.setSenderKingdom(this.player.getKingdomId());
        this.sendMessage(mess);
    }
    
    private void handleMessageOpenChat(final String message) {
        if (!this.player.isMute()) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String chat = "";
            if (tokens.hasMoreTokens()) {
                chat = tokens.nextToken();
            }
            if (chat.toLowerCase().startsWith("k")) {
                if (this.player.isKingdomChat()) {
                    this.sendToLocalKingdomChat("");
                }
                else {
                    this.sendNormalServerMessage("Need to enable kingdom chat in profile before opening it.");
                }
            }
            else if (chat.toLowerCase().startsWith("g")) {
                if (this.player.isGlobalChat()) {
                    this.sendToGlobalKingdomChat("");
                }
                else {
                    this.sendNormalServerMessage("Need to enable global kingdom chat in profile before opening it.");
                }
            }
            else if (chat.toLowerCase().startsWith("t")) {
                if (this.player.isTradeChannel()) {
                    this.sendToTradeChannel("");
                }
                else {
                    this.sendNormalServerMessage("Need to enable Trade channel in profile before opening it.");
                }
            }
            else {
                this.sendNormalServerMessage("Unknown chat channel. Use k, g or t as parameter.");
            }
            this.player.chatted();
        }
    }
    
    private void handleMessageSuicide() {
        if (System.currentTimeMillis() - this.player.lastSuicide < 60000L * (this.player.isUndead() ? 10 : 3)) {
            this.sendNormalServerMessage("You suicided recently. You have to wait a few minutes between each attempt.");
        }
        else if (this.player.isDead()) {
            this.sendNormalServerMessage("You are already dead. Use /respawn if you want a respawn window.");
        }
        else if (this.player.isTeleporting()) {
            this.sendAlertServerMessage("You are too confused to kill yourself right now.");
        }
        else if (this.player.getBattle() != null) {
            this.sendAlertServerMessage("You are too full of adrenaline from the battle to kill yourself right now.");
        }
        else if (this.player.getSaveFile().realdeath > 2) {
            this.sendAlertServerMessage("You cannot force yourself to suicide this time.");
        }
        else {
            final SuicideQuestion s = new SuicideQuestion(this.player, "Suicidal?", "Do you wish to commit suicide?", this.player.getWurmId());
            s.sendQuestion();
        }
    }
    
    private void handleMessageFightLevel() {
        if (this.player.fightlevel > 0) {
            this.sendCombatNormalMessage(this.player.getFightlevelString());
        }
        else {
            this.sendNormalServerMessage("You are not focused on combat.");
        }
    }
    
    private void handleMessageSupportChat(final String message) {
        if (!this.player.isMute()) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String toSend = "";
            if (tokens.hasMoreTokens()) {
                toSend = tokens.nextToken();
            }
            while (tokens.hasMoreTokens()) {
                toSend = toSend + ' ' + tokens.nextToken();
            }
            final Message mess = this.sendMGMTMessage(toSend, false);
            if (!this.player.mayHearMgmtTalk()) {
                this.sendMessage(mess);
            }
        }
    }
    
    private Message sendMGMTMessage(final String msg, final boolean addPlayerType) {
        final Message mess = new Message(this.player, (byte)9, "MGMT", "<" + this.getPlayerName(addPlayerType) + "> " + msg);
        Server.getInstance().addMessage(mess);
        return mess;
    }
    
    public void remindToSignIn() {
        final String msg = "You can Sign In using /signin with an optional message which will be appended to the default of Signing in.";
        final Message mess = new Message(this.player, (byte)0, ":Local", "<System> You can Sign In using /signin with an optional message which will be appended to the default of Signing in.");
        mess.setColorR(250);
        mess.setColorG(150);
        mess.setColorB(250);
        this.sendMessage(mess);
    }
    
    private void handleMessageTweet(final String message) {
        if (!this.player.isDead()) {
            final Village lVillage = this.player.getCitizenVillage();
            if (lVillage == null) {
                this.sendNormalServerMessage("You are not the citizen of a village or homestead.");
            }
            else {
                final StringTokenizer tokens = new StringTokenizer(message);
                tokens.nextToken();
                String toSend = "";
                if (tokens.hasMoreTokens()) {
                    toSend = tokens.nextToken();
                    while (tokens.hasMoreTokens()) {
                        toSend = toSend + ' ' + tokens.nextToken();
                    }
                    toSend = this.drunkGarble(toSend);
                    lVillage.broadCastMessage(new Message(this.player, (byte)3, "Village", "<" + this.player.getName() + "> " + toSend), true);
                }
                else {
                    this.sendNormalServerMessage("You need to /tweet something!");
                }
            }
        }
        else {
            this.sendNormalServerMessage("Nobody can hear you now.");
        }
    }
    
    private void handleMessageAllianceChat(final String message) {
        if (!this.player.isDead()) {
            if (this.player.isMute()) {
                this.sendNormalServerMessage("You are muted.");
            }
            else {
                final Village lVillage = this.player.getCitizenVillage();
                if (lVillage == null || lVillage.getAllianceNumber() == 0) {
                    this.sendNormalServerMessage("You are not in an alliance.");
                }
                else {
                    final PvPAlliance pvpAlliance = PvPAlliance.getPvPAlliance(lVillage.getAllianceNumber());
                    if (pvpAlliance != null) {
                        final StringTokenizer tokens = new StringTokenizer(message);
                        tokens.nextToken();
                        String toSend = "";
                        if (tokens.hasMoreTokens()) {
                            toSend = tokens.nextToken();
                        }
                        while (tokens.hasMoreTokens()) {
                            toSend = toSend + ' ' + tokens.nextToken();
                        }
                        toSend = this.drunkGarble(toSend);
                        pvpAlliance.broadCastMessage(new Message(this.player, (byte)15, "Alliance", "<" + this.player.getName() + "> " + toSend));
                    }
                }
            }
        }
        else {
            this.sendNormalServerMessage("Nobody can hear you now.");
        }
    }
    
    private void handleInGameVoting() {
        final InGameVoteQuestion igvq = new InGameVoteQuestion(this.player);
        igvq.sendQuestion();
    }
    
    private void handleCreatureDump(final int power) {
        if (power == 5) {
            final Thread thread = new Thread("saveCreatureDistributionAsImg-Thread") {
                @Override
                public void run() {
                    ZonesUtility.saveCreatureDistributionAsImg(Server.surfaceMesh);
                }
            };
            thread.start();
        }
    }
    
    private void handleMarkerDump(final int power) {
        if (power == 5) {
            final Thread thread = new Thread("saveMapWithMarkersAsImg-Thread") {
                @Override
                public void run() {
                    ZonesUtility.saveMapWithMarkersAsImg();
                }
            };
            thread.start();
        }
    }
    
    private void handleRouteDump(final int power) {
        if (power == 5) {
            final Thread thread = new Thread("saveRoutesAsImg-Thread") {
                @Override
                public void run() {
                    ZonesUtility.saveRoutesAsImg();
                }
            };
            thread.start();
        }
    }
    
    private void handleWaterDump(final int power, final boolean onSurface) {
        if (power == 5) {
            final Thread thread = new Thread("saveWaterAsImg-Thread") {
                @Override
                public void run() {
                    ZonesUtility.saveWaterTypesAsImg(onSurface);
                }
            };
            thread.start();
        }
    }
    
    private void handleSkillProgress(final int power) {
        if (power > 1) {
            final SkillProgressQuestion spq = new SkillProgressQuestion(this.player);
            spq.sendQuestion();
        }
    }
    
    private void handleDumpFishSpots(final int power) {
        if (power == 5) {
            final Thread thread = new Thread("saveFishSpotsAsImg-Thread") {
                @Override
                public void run() {
                    ZonesUtility.saveFishSpots(Server.surfaceMesh);
                }
            };
            thread.start();
        }
    }
    
    private void handleShowFishSpots(final int power) {
        if (power == 5 && this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-64));
                bb.put((byte)45);
                for (byte season = 0; season < 4; ++season) {
                    final Color bgColour = MethodsFishing.getBgColour(season);
                    bb.put((byte)bgColour.getRed());
                    bb.put((byte)bgColour.getGreen());
                    bb.put((byte)bgColour.getBlue());
                    final Point offset = MethodsFishing.getSeasonOffset(season);
                    bb.putInt(offset.getX());
                    bb.putInt(offset.getY());
                    final Point[] spots = MethodsFishing.getSpecialSpots(0, 0, season);
                    bb.put((byte)spots.length);
                    for (final Point spot : spots) {
                        bb.putInt(spot.getX());
                        bb.putInt(spot.getY());
                        final Color fishColour = MethodsFishing.getFishColour(spot.getH());
                        bb.put((byte)fishColour.getRed());
                        bb.put((byte)fishColour.getGreen());
                        bb.put((byte)fishColour.getBlue());
                    }
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " could not send data for showing fish spots due to " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private void handleMapDump(final int power) {
        if (power == 5) {
            final Thread thread = new Thread("saveMapDumpAsImg-Thread") {
                @Override
                public void run() {
                    ZonesUtility.saveMapDump(Server.surfaceMesh);
                }
            };
            thread.start();
        }
    }
    
    private void handleVillageTeleportMessage(final String message) {
        if (!this.player.isDead()) {
            if (!this.player.canUseFreeVillageTeleport()) {
                this.sendNormalServerMessage("You have no free teleports left.");
                return;
            }
            final Village invitingVillage = this.player.getCitizenVillage();
            if (invitingVillage == null) {
                this.sendNormalServerMessage("You are not part of a village.");
                return;
            }
            final VillageTeleportQuestion vtq = new VillageTeleportQuestion(this.player);
            vtq.sendQuestion();
        }
    }
    
    private void handleJoinVillageMessage(final String message) {
        if (this.player.isDead()) {
            this.sendNormalServerMessage("You need to be alive to join a village.");
            return;
        }
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        if (!tokens.hasMoreTokens()) {
            this.sendNormalServerMessage("The message must be in either of the following formats:");
            this.sendNormalServerMessage("/join village <villagename>");
            this.sendNormalServerMessage("/join player <playername>");
            return;
        }
        final String type = tokens.nextToken();
        if (!type.toLowerCase().equals("village") && !type.toLowerCase().equals("player")) {
            this.sendNormalServerMessage("The message must be in either of the following formats:");
            this.sendNormalServerMessage("/join village <villagename>");
            this.sendNormalServerMessage("/join player <playername>");
            return;
        }
        if (!tokens.hasMoreTokens()) {
            this.sendNormalServerMessage("You must specify which village or player you want to join.");
            return;
        }
        String toJoin = tokens.nextToken();
        final boolean joinByPlayer = type.toLowerCase().equals("player");
        if (joinByPlayer) {
            final PlayerInfo info = PlayerInfoFactory.getPlayerInfoWithName(toJoin);
            if (info == null) {
                this.sendNormalServerMessage("There is no player by that name.");
                return;
            }
            final Village vill = Villages.getVillageForCreature(info.getPlayerId());
            if (vill == null) {
                this.sendNormalServerMessage("Unable to find a village for a player with that name: " + toJoin);
                return;
            }
            vill.joinVillage(this.player);
        }
        else {
            while (tokens.hasMoreTokens()) {
                toJoin = toJoin + " " + tokens.nextToken();
            }
            try {
                final Village vill2 = Villages.getVillage(toJoin);
                vill2.joinVillage(this.player);
            }
            catch (NoSuchVillageException e) {
                logWarn(e.getMessage(), e);
                this.sendNormalServerMessage("Unable to find a village with that name: " + toJoin);
            }
        }
    }
    
    private void handleListRecruites() {
        if (this.player.isDead()) {
            this.sendNormalServerMessage("You need to be alive to show the recruit list");
            return;
        }
        final Village vill = this.player.getCitizenVillage();
        if (vill == null) {
            this.sendNormalServerMessage("You are not a member of any village");
            return;
        }
        final Citizen citizen = vill.getCitizen(this.player.getWurmId());
        if (citizen == null) {
            this.sendNormalServerMessage("You are not a citizen of a village.");
            return;
        }
        final VillageRecruitee[] recruits = vill.getRecruitees();
        if (recruits.length == 0) {
            this.sendNormalServerMessage("You have no recruits on the list.");
            return;
        }
        for (int i = 0; i < recruits.length; ++i) {
            this.sendNormalServerMessage(recruits[i].getRecruiteeName());
        }
    }
    
    private void handleUnRecruiteMessage(final String message) {
        if (this.player.isDead()) {
            this.sendNormalServerMessage("You need to be alive to alter the recruit list");
            return;
        }
        final Village vill = this.player.getCitizenVillage();
        if (vill == null) {
            this.sendNormalServerMessage("You are not a member of any village");
            return;
        }
        final Citizen citizen = vill.getCitizen(this.player.getWurmId());
        if (citizen == null) {
            this.sendNormalServerMessage("You are not a citizen of a village.");
            return;
        }
        if (!citizen.getRole().mayInviteCitizens()) {
            this.sendNormalServerMessage("You are not allowed to invite or uninvite players to your village.");
            return;
        }
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        if (!tokens.hasMoreTokens()) {
            this.sendNormalServerMessage("You must include the name of the player you want to remove from the list.");
            return;
        }
        final String name = tokens.nextToken();
        long pid = -10L;
        pid = Players.getInstance().getWurmIdByPlayerName(name);
        if (pid == -1L) {
            this.sendNormalServerMessage("Unable to find a player by that name: " + name);
            return;
        }
        if (vill.removeRecruitee(pid)) {
            this.sendNormalServerMessage("Removed " + name + " from the recruiting list.");
        }
    }
    
    private void handleRecruitMessage(final String message) {
        if (this.player.isDead()) {
            this.sendNormalServerMessage("You need to be alive to alter the recruit list");
            return;
        }
        final Village vill = this.player.getCitizenVillage();
        if (vill == null) {
            this.sendNormalServerMessage("You are not a member of any village");
            return;
        }
        final Citizen citizen = vill.getCitizen(this.player.getWurmId());
        if (citizen == null) {
            this.sendNormalServerMessage("You are not a citizen of a village.");
            return;
        }
        if (!citizen.getRole().mayInviteCitizens()) {
            this.sendNormalServerMessage("You are not allowed to invite players to your village.");
            return;
        }
        if (!Servers.localServer.challengeServer && this.player.getSaveFile().getPaymentExpire() <= 0L && citizen.getRole().getStatus() == 2) {
            this.sendNormalServerMessage("You are not allowed to invite players to your settlement. Since you were never a premium player your account may be deleted and the settlement may end up without mayor.");
            return;
        }
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        if (!tokens.hasMoreTokens()) {
            this.sendNormalServerMessage("You must include the name of the player you want to add to the list.");
            return;
        }
        final String name = tokens.nextToken();
        long pid = -10L;
        pid = Players.getInstance().getWurmIdByPlayerName(name);
        if (pid == -1L) {
            this.sendNormalServerMessage("Unable to find a player by that name: " + name);
            return;
        }
        if (Players.getInstance().getKingdomForPlayer(pid) != this.player.getKingdomId()) {
            this.sendNormalServerMessage(name + " is not the same kingdom as you.");
            return;
        }
        if (vill.addVillageRecruitee(name, pid)) {
            this.sendNormalServerMessage("Added player " + name + " to the list.");
        }
    }
    
    private void handleVillageInviteMessage(final String message) {
        if (!this.player.isDead()) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            if (!tokens.hasMoreTokens()) {
                final String[] parts = message.split(" ");
                if (parts.length > 0) {
                    final String rm = StringUtil.format("You must include the name of the player you want to invite, %s <name>.", parts[0]);
                    this.sendNormalServerMessage(rm);
                }
                else {
                    final String m = "You must include the name of the player you want to invite, /vinvite <name>.";
                    this.sendNormalServerMessage("You must include the name of the player you want to invite, /vinvite <name>.");
                }
                return;
            }
            final String toInvite = tokens.nextToken();
            final Village invitingVillage = this.player.getCitizenVillage();
            if (invitingVillage == null) {
                this.sendNormalServerMessage("You are not part of a village.");
                return;
            }
            final Citizen citizen = invitingVillage.getCitizen(this.player.getWurmId());
            if (citizen == null) {
                this.sendNormalServerMessage("You are not a citizen of a village.");
                return;
            }
            if (!citizen.getRole().mayInviteCitizens()) {
                this.sendNormalServerMessage("You are not allowed to invite players to your village.");
                return;
            }
            if (!Servers.localServer.challengeServer && ((this.player.getSaveFile().getPaymentExpire() <= 0L && citizen.getRole().getStatus() == 2) || (Servers.localServer.entryServer && !Server.getInstance().isPS()))) {
                this.sendNormalServerMessage("You are not allowed to invite players to your settlement. Since you were never a premium player your account may be deleted and the settlement may end up without mayor.");
                return;
            }
            try {
                final Player target = Players.getInstance().getPlayer(toInvite);
                if (this.player.getKingdomId() == target.getKingdomId()) {
                    final Village targetVillage = Villages.getVillageForCreature(target);
                    if (targetVillage == null) {
                        if (!target.isGuest()) {
                            if (target.isPlayer() && target.mayChangeVillageInMillis() > 0L) {
                                this.sendNormalServerMessage(target.getName() + " may not change village until " + Server.getTimeFor(target.mayChangeVillageInMillis()) + " has elapsed.");
                            }
                            else {
                                Methods.sendJoinVillageQuestion(this.player, target);
                            }
                        }
                        else {
                            this.sendAlertServerMessage("You just tried to invite a guest. This should not be possible and has been logged.");
                            logWarn(this.player.getName() + " has managed to invite a guest. This should not be possible, so cheating is involved.");
                        }
                    }
                    else {
                        this.sendNormalServerMessage(target.getName() + " is already part of a village.");
                    }
                }
                else {
                    logWarn(this.player.getName() + " tried to invite ENEMY " + target.getName() + " as villager!");
                }
            }
            catch (NoSuchPlayerException nsp) {
                this.sendNormalServerMessage("Unable to find a player with the name: " + toInvite);
            }
        }
        else {
            this.sendNormalServerMessage("You can not invite someone when you are dead.");
        }
    }
    
    private void handleMessageVillageChat(final String message) {
        if (!this.player.isDead()) {
            final Village lVillage = this.player.getCitizenVillage();
            if (lVillage == null) {
                this.sendNormalServerMessage("You are not the citizen of a village or homestead.");
            }
            else {
                final StringTokenizer tokens = new StringTokenizer(message);
                tokens.nextToken();
                String toSend = "";
                if (tokens.hasMoreTokens()) {
                    toSend = tokens.nextToken();
                }
                while (tokens.hasMoreTokens()) {
                    toSend = toSend + ' ' + tokens.nextToken();
                }
                toSend = this.drunkGarble(toSend);
                lVillage.broadCastMessage(new Message(this.player, (byte)3, "Village", "<" + this.player.getName() + "> " + toSend), lVillage.twitChat());
            }
        }
        else {
            this.sendNormalServerMessage("Nobody can hear you now.");
        }
    }
    
    private void handleMessageTeamChat(final String message) {
        if (!this.player.isDead()) {
            if (this.invulnerable) {
                this.sendAlertServerMessage("You may not use team chat until you have moved and lost invulnerability.");
            }
            else {
                final StringTokenizer tokens = new StringTokenizer(message);
                tokens.nextToken();
                String toSend = "";
                if (tokens.hasMoreTokens()) {
                    toSend = tokens.nextToken();
                }
                while (tokens.hasMoreTokens()) {
                    toSend = toSend + ' ' + tokens.nextToken();
                }
                toSend = this.drunkGarble(toSend);
                final Team g = this.player.getTeam();
                if (g != null) {
                    g.sendTeamMessage(this.player, toSend);
                }
                else {
                    this.sendNormalServerMessage("You are not part of a team.");
                }
            }
        }
    }
    
    private void handleMessageRarity(final String message) {
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        if (tokens.hasMoreTokens()) {
            final String param = LoginHandler.raiseFirstLetter(tokens.nextToken());
            if (param.startsWith("1") || param.startsWith("R") || param.startsWith("r")) {
                this.player.setNextActionRarity((byte)1);
            }
            else if (param.startsWith("2") || param.startsWith("S") || param.startsWith("s")) {
                this.player.setNextActionRarity((byte)2);
            }
            else if (param.startsWith("3") || param.startsWith("F") || param.startsWith("f")) {
                this.player.setNextActionRarity((byte)3);
            }
            else {
                this.sendNormalServerMessage("Value is a number between 1 and 3 or one of the words rare, supreme or fantastic (first character is used)");
                this.sendNormalServerMessage("and it is used to make the next action rarity that value.");
            }
        }
        else {
            this.sendNormalServerMessage("Usage: /rarity value.");
            this.sendNormalServerMessage("Where value is used to make the next action rarity that value.");
        }
    }
    
    private void handleMessageTeamInvite(final String message) {
        final String[] words = message.split(" ");
        if (words.length != 2) {
            this.sendNormalServerMessage("The correct usage is: /tinvite <name>");
            return;
        }
        final String targetName = LoginHandler.raiseFirstLetter(words[1]);
        if (this.player.getTeam() == null) {
            this.sendNormalServerMessage("You are not part of a team");
            return;
        }
        if (!this.player.mayInviteTeam()) {
            this.sendNormalServerMessage("You do not have the permission to invite other players to the team.");
            return;
        }
        if (this.player.getName().equalsIgnoreCase(targetName)) {
            this.sendNormalServerMessage("You cannot invite yourself");
            return;
        }
        final Player targetPlayer = Players.getInstance().getPlayerOrNull(targetName);
        if (targetPlayer == null) {
            this.sendNormalServerMessage("Unable to find a player by that name.");
            return;
        }
        if (targetPlayer.getTeam() != null && this.player.getTeam().equals(targetPlayer.getTeam())) {
            this.sendNormalServerMessage(targetName + " is already part of your team!");
            return;
        }
        if (targetPlayer.getKingdomId() != this.player.getKingdomId() || !targetPlayer.acceptsInvitations()) {
            this.sendNormalServerMessage(targetName + " needs to type /invitations first.");
            return;
        }
        try {
            final TeamManagementQuestion question = new TeamManagementQuestion(this.player, "Expanding the team", "Inviting " + targetPlayer.getName(), false, targetPlayer.getWurmId(), false, false);
            question.sendQuestion();
        }
        catch (NoSuchPlayerException | NoSuchCreatureException ex2) {
            final WurmServerException ex;
            final WurmServerException e = ex;
            this.sendNormalServerMessage(targetName + " needs to type /invitations first.");
        }
    }
    
    private void handleHashMessageGetIP(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String kname = "";
            if (tokens.hasMoreTokens()) {
                kname = LoginHandler.raiseFirstLetter(tokens.nextToken());
                Players.getInstance().sendIpsToPlayer(this.player, kname);
            }
            else {
                this.sendNormalServerMessage("Specify a playername please.");
            }
        }
    }
    
    private void handleHashMessageRename(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String oldname = "Unknown";
            if (tokens.hasMoreTokens()) {
                oldname = tokens.nextToken().trim();
                oldname = LoginHandler.raiseFirstLetter(oldname);
                String newname = "Unknown";
                if (tokens.hasMoreTokens()) {
                    newname = tokens.nextToken().trim();
                    String password = "Unknown";
                    if (tokens.hasMoreTokens()) {
                        password = tokens.nextToken().trim();
                        if (password.length() >= 6) {
                            try {
                                if (newname.length() > 2) {
                                    if (Deities.isNameOkay(newname)) {
                                        if (Players.getInstance().doesPlayerNameExist(newname)) {
                                            this.sendNormalServerMessage("The name " + newname + " is already in use.");
                                        }
                                        else {
                                            newname = LoginHandler.raiseFirstLetter(newname);
                                            final LoginServerWebConnection lsw = new LoginServerWebConnection();
                                            final String toReturn = lsw.renamePlayer(oldname, newname, password, this.player.getPower());
                                            this.sendNormalServerMessage("You try to change the name of " + oldname + " to " + newname + " and set the password to '" + password + "'.");
                                            this.sendNormalServerMessage("The result is:");
                                            this.sendNormalServerMessage(toReturn);
                                            Communicator.logger.info(this.player.getName() + " changed the name of " + oldname + " to " + newname + '.');
                                            if (this.player.getLogger() != null) {
                                                this.player.getLogger().log(Level.INFO, this.player.getName() + " changed the name of " + oldname + " to " + newname + '.');
                                            }
                                            try {
                                                final Player p = Players.getInstance().getPlayer(newname);
                                                p.refreshVisible();
                                            }
                                            catch (NoSuchPlayerException ex2) {}
                                        }
                                    }
                                    else {
                                        this.sendNormalServerMessage("The name  " + newname + " is illegal.");
                                    }
                                }
                                else {
                                    this.sendNormalServerMessage("The name  must contain at least 3 letters.");
                                }
                            }
                            catch (Exception ex) {
                                logWarn(this.player.getName() + " failed to change the name of " + oldname + " to " + newname + ": " + ex.getMessage(), ex);
                                this.sendNormalServerMessage("You FAILED to change the name of " + oldname + " to " + newname + ' ' + ex.getMessage());
                            }
                        }
                        else {
                            this.sendNormalServerMessage("Please choose a password longer than 5 letters.");
                        }
                    }
                    else {
                        this.sendNormalServerMessage("Syntax: #rename <oldname> <newname> <newpassword>");
                    }
                }
                else {
                    this.sendNormalServerMessage("Syntax: #rename <oldname> <newname> <newpassword>");
                }
            }
            else {
                this.sendNormalServerMessage("Syntax: #rename <oldname> <newname> <newpassword>");
            }
        }
    }
    
    private void handleHashMessageReadLog(final String message, final int power) {
        if (power >= 5) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            int start = 50;
            try {
                final String name = tokens.nextToken().trim();
                if (tokens.hasMoreTokens()) {
                    final String am = tokens.nextToken().trim();
                    start = Math.abs(Integer.parseInt(am));
                }
                final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(name);
                if (pinf != null && pinf.loaded) {
                    if (pinf.getPower() == 5) {
                        this.sendAlertServerMessage("Peekaboo!");
                    }
                    else {
                        try {
                            final FileReader fr = new FileReader(new File(pinf.getName() + ".log"));
                            final BufferedReader br = new BufferedReader(fr);
                            final LinkedList<String> lines = new LinkedList<String>();
                            for (String tmp = br.readLine(); tmp != null; tmp = br.readLine()) {
                                lines.add(tmp);
                            }
                            br.close();
                            if (!lines.isEmpty()) {
                                start = lines.size() - start;
                                if (start < 0) {
                                    start = 0;
                                }
                                int end = start + 50;
                                if (end > lines.size()) {
                                    end = lines.size();
                                }
                                this.sendSafeServerMessage("Reading entry " + start + " to " + end);
                                for (int x = start; x < end; ++x) {
                                    this.sendNormalServerMessage(lines.get(x));
                                }
                            }
                            else {
                                this.sendNormalServerMessage("The log is empty");
                            }
                        }
                        catch (IOException iox) {
                            this.sendAlertServerMessage("An error occurred: " + iox.getMessage());
                        }
                    }
                }
                else {
                    this.sendAlertServerMessage("No such Player");
                }
            }
            catch (Exception ex) {
                this.sendAlertServerMessage("Make sure of the syntax #readlog playername <no of lines from the end>:" + ex.getMessage());
            }
        }
    }
    
    private void handleHashMessageAddRegalia(final String message, final int power) {
        if (power >= 5) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String pname = "Unknown";
            if (tokens.hasMoreTokens()) {
                pname = tokens.nextToken().trim();
                try {
                    pname = LoginHandler.raiseFirstLetter(pname);
                    final Player p = Players.getInstance().getPlayer(pname);
                    if (p.isKing()) {
                        Methods.rewardRegalia(p);
                        this.sendNormalServerMessage("You bestow the royal regalia on " + p.getName() + ".");
                        p.getCommunicator().sendSafeServerMessage("You receive the royal regalia!");
                        this.player.getLogger().log(Level.INFO, "Added regalia to " + p.getName());
                    }
                    else {
                        this.sendNormalServerMessage(p.getName() + " is not king.");
                    }
                }
                catch (NoSuchPlayerException nsp) {
                    this.sendNormalServerMessage("The player " + pname + " could not be found.");
                }
            }
            else {
                this.sendNormalServerMessage("Syntax: #addregalia <playername>.");
            }
        }
    }
    
    private void handleHashMessageAddTitle(final String message, final int power) {
        if (power >= 3) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String pname = "Unknown";
            if (tokens.hasMoreTokens()) {
                pname = tokens.nextToken().trim();
                try {
                    pname = LoginHandler.raiseFirstLetter(pname);
                    final Player p = Players.getInstance().getPlayer(pname);
                    final Titles.Title title = this.getTitle(tokens, p.getSex(), 218);
                    if (title != null) {
                        p.addTitle(title);
                        this.sendNormalServerMessage("You bestow the title " + title.getName(p.getSex() == 0) + " on " + p.getName() + ".");
                    }
                    else {
                        this.sendNormalServerMessage("The value could not be parsed into an title.");
                    }
                }
                catch (NoSuchPlayerException nsp) {
                    this.sendNormalServerMessage("The player " + pname + " could not be found.");
                }
            }
            else {
                this.sendNormalServerMessage("Syntax: #addtitle <playername> [<title|titleid>].");
            }
        }
    }
    
    private void handleHashMessageParticipation(final String message) {
    }
    
    private void handleHashMessageRiftParticipation(final String message, final int power) {
    }
    
    private void handleHashMessageRemoveTitle(final String message, final int power) {
        if (power >= 3) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String pname = "Unknown";
            if (tokens.hasMoreTokens()) {
                pname = tokens.nextToken().trim();
                try {
                    pname = LoginHandler.raiseFirstLetter(pname);
                    final Player p = Players.getInstance().getPlayer(pname);
                    final Titles.Title title = this.getTitle(tokens, p.getSex(), 191);
                    if (title != null) {
                        p.removeTitle(title);
                        if (p.getTitle() == title) {
                            p.setTitle(null);
                        }
                        this.sendNormalServerMessage("You remove the title " + title.getName(p.getSex() == 0) + " from " + p.getName() + ".");
                    }
                    else {
                        this.sendNormalServerMessage("The value could not be parsed into an title.");
                    }
                }
                catch (NoSuchPlayerException nsp) {
                    this.sendNormalServerMessage("The player " + pname + " could not be found.");
                }
            }
            else {
                this.sendNormalServerMessage("Syntax: #removetitle <playername> [<title|titleid>].");
            }
        }
    }
    
    private Titles.Title getTitle(final StringTokenizer tokens, final byte sex, final int defaultTitleId) {
        int newtitle = 191;
        String titleString = "";
        if (tokens.hasMoreTokens()) {
            titleString += tokens.nextToken();
            while (tokens.hasMoreTokens()) {
                titleString = titleString + ' ' + tokens.nextToken();
            }
            Titles.Title title = null;
            try {
                newtitle = Integer.parseInt(titleString);
                title = Titles.Title.getTitle(newtitle);
            }
            catch (NumberFormatException nfe) {
                title = Titles.Title.getTitle(titleString, sex == 0);
            }
            return title;
        }
        return Titles.Title.getTitle(defaultTitleId);
    }
    
    private void handleHashMessageRespawn(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String pname = "Unknown";
            if (tokens.hasMoreTokens()) {
                pname = tokens.nextToken().trim();
                pname = LoginHandler.raiseFirstLetter(pname);
                Player p = null;
                try {
                    p = Players.getInstance().getPlayer(pname);
                    if (p.isDead()) {
                        p.spawn((byte)127);
                        this.sendNormalServerMessage("You revive " + pname + '.');
                    }
                    else {
                        this.sendNormalServerMessage(pname + " is not dead yet.");
                    }
                }
                catch (NoSuchPlayerException nsp) {
                    this.sendNormalServerMessage(pname + " is not online.");
                }
            }
            else {
                this.sendNormalServerMessage("Syntax: #respawn <playername>");
            }
        }
    }
    
    private void handleHashMessageLocateItem(final String message, final int power) {
        if (power >= 5) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            int templateId = -1;
            if (tokens.hasMoreTokens()) {
                try {
                    final String tid = tokens.nextToken().trim();
                    templateId = Integer.parseInt(tid);
                    this.sendNormalServerMessage(EndGameItems.locateEndGameItem(templateId, this.player));
                }
                catch (Exception ex) {
                    logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                }
            }
        }
    }
    
    private void handleHashMessageResetWarnings(final String message, final int power) throws IOException {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String bname = "";
            try {
                bname = tokens.nextToken().trim();
                bname = LoginHandler.raiseFirstLetter(bname);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            PlayerInfo pinf = null;
            Player play = null;
            try {
                play = Players.getInstance().getPlayer(bname);
                pinf = play.getSaveFile();
            }
            catch (NoSuchPlayerException nsp) {
                pinf = PlayerInfoFactory.createPlayerInfo(bname);
                pinf.load();
            }
            try {
                pinf.resetWarnings();
                this.sendSafeServerMessage("You have officially removed the warnings for " + bname + '.');
                if (play != null) {
                    play.getCommunicator().sendSafeServerMessage("Your warnings have just been officially removed.");
                }
                if (this.player.getLogger() != null) {
                    this.player.getLogger().log(Level.INFO, this.player.getName() + " removes warnings for " + bname + '.');
                }
            }
            catch (IOException iox) {
                logInfo(this.player.getName() + " fails to reset warnings for " + bname + '.', iox);
            }
        }
    }
    
    private void handleHashMessageStartLibX(final String message, final int power) {
        if (power >= 3) {
            if (Servers.localServer.HOMESERVER) {
                this.sendNormalServerMessage("Use #startx instead for home servers.");
            }
            else {
                final StringTokenizer tokens = new StringTokenizer(message);
                tokens.nextToken();
                int startX = Servers.localServer.SPAWNPOINTLIBX;
                try {
                    final String stx = tokens.nextToken().trim();
                    logInfo("Setting start tile x for HOTS to " + stx + '.');
                    this.sendNormalServerMessage("Setting start tile x for HOTS to " + stx + '.');
                    startX = Integer.parseInt(stx);
                    Servers.localServer.SPAWNPOINTLIBX = startX;
                }
                catch (Exception ex) {}
            }
        }
    }
    
    private void handleHashMessageStartLibY(final String message, final int power) {
        if (power >= 3) {
            if (Servers.localServer.HOMESERVER) {
                this.sendNormalServerMessage("Use #starty instead for home servers.");
            }
            else {
                final StringTokenizer tokens = new StringTokenizer(message);
                tokens.nextToken();
                int startY = Servers.localServer.SPAWNPOINTLIBY;
                try {
                    final String sty = tokens.nextToken().trim();
                    logInfo("Setting start tile y for HOTS to " + sty + '.');
                    this.sendNormalServerMessage("Setting start tile y for HOTS to " + sty + '.');
                    startY = Integer.parseInt(sty);
                    Servers.localServer.SPAWNPOINTLIBY = startY;
                }
                catch (Exception ex) {}
            }
        }
    }
    
    private void handleHashMessageSetStartMolRehan(final int power) {
        if (power >= 3) {
            if (Servers.localServer.HOMESERVER) {
                this.sendNormalServerMessage("Unused on home.");
            }
            else {
                Server.setMolRehanX((short)this.player.getCurrentTile().tilex);
                Server.setMolRehanY((short)this.player.getCurrentTile().tiley);
                this.sendNormalServerMessage("Teleportpoints set to " + Server.getMolRehanX() + ',' + Server.getMolRehanY());
            }
        }
    }
    
    private void handleHashMessageStartMolX(final String message, final int power) {
        if (power >= 3) {
            if (Servers.localServer.HOMESERVER) {
                this.sendNormalServerMessage("Use #startx instead for home servers.");
            }
            else {
                final StringTokenizer tokens = new StringTokenizer(message);
                tokens.nextToken();
                int startX = Servers.localServer.SPAWNPOINTMOLX;
                try {
                    final String stx = tokens.nextToken().trim();
                    logInfo("Setting start tile x for MolRehan to " + stx + '.');
                    this.sendNormalServerMessage("Setting start tile x for MolRehan to " + stx + '.');
                    startX = Integer.parseInt(stx);
                    Servers.localServer.SPAWNPOINTMOLX = startX;
                }
                catch (Exception ex) {}
            }
        }
    }
    
    private void handleHashMessageStartMolY(final String message, final int power) {
        if (power >= 3) {
            if (Servers.localServer.HOMESERVER) {
                this.sendNormalServerMessage("Use #starty instead for home servers.");
            }
            else {
                final StringTokenizer tokens = new StringTokenizer(message);
                tokens.nextToken();
                int startY = Servers.localServer.SPAWNPOINTMOLY;
                try {
                    final String sty = tokens.nextToken().trim();
                    logInfo("Setting start tile y for MolRehan to " + sty + '.');
                    this.sendNormalServerMessage("Setting start tile y for MolRehan to " + sty + '.');
                    startY = Integer.parseInt(sty);
                    Servers.localServer.SPAWNPOINTMOLY = startY;
                }
                catch (Exception ex) {}
            }
        }
    }
    
    private void handleHashMessageMute(final String message, final int power) {
        if (power >= 2 || this.player.mayMute()) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String bname = "";
            String reason = "";
            long expiry = 0L;
            int hours = 0;
            boolean ok = true;
            if (tokens.hasMoreTokens()) {
                try {
                    bname = tokens.nextToken().trim();
                    bname = LoginHandler.raiseFirstLetter(bname);
                    if (this.player.getLogger() != null) {
                        this.player.getLogger().log(Level.INFO, "Trying to mute player " + bname + '.');
                    }
                }
                catch (Exception ex) {
                    ok = false;
                }
            }
            else {
                ok = false;
            }
            if (tokens.hasMoreTokens()) {
                try {
                    hours = Integer.parseInt(tokens.nextToken().trim());
                    expiry = System.currentTimeMillis() + hours * 3600000L;
                    if (hours == 0) {
                        ok = false;
                    }
                }
                catch (Exception ex) {
                    ok = false;
                }
            }
            else {
                ok = false;
            }
            if (tokens.hasMoreTokens()) {
                try {
                    while (tokens.hasMoreTokens()) {
                        reason = reason + tokens.nextToken() + ' ';
                    }
                    if (reason.length() < 4) {
                        this.sendAlertServerMessage("The reason " + reason + " seems a bit short. Please explain a bit more.");
                        ok = false;
                    }
                }
                catch (Exception ex) {
                    ok = false;
                }
            }
            else {
                ok = false;
            }
            if (!ok) {
                this.sendAlertServerMessage("Failed to mute the player. Make sure you use the syntax #mute <name> <hours> <reason> like this: #mute Griefer 2 bad language.");
            }
            else {
                Player p = null;
                try {
                    p = Players.getInstance().getPlayer(bname);
                    if (p.getPower() < this.player.getPower() || (this.player.mayMute() && p.getPower() == this.player.getPower())) {
                        p.mute(true, reason, expiry);
                        Players.addMgmtMessage(this.player.getName(), "mutes " + bname + " for " + hours + " hours. Reason: " + reason);
                        final Message mess = new Message(this.player, (byte)9, "MGMT", "<" + this.player.getName() + "> mutes " + bname + " for " + hours + " hours. Reason: " + reason, Communicator.muteMsgRed, Communicator.muteMsgGreen, Communicator.muteMsgBlue);
                        Server.getInstance().addMessage(mess);
                        this.sendNormalServerMessage("You have muted " + p.getName() + " for " + hours + " hours.");
                        p.getCommunicator().sendAlertServerMessage("You have been muted by " + this.player.getName() + " for " + hours + " hours and cannot shout anymore. Reason: " + reason);
                        if (this.player.getLogger() != null) {
                            this.player.getLogger().log(Level.INFO, this.player.getName() + " muted " + bname + " for " + hours + " hours. Reason: " + reason);
                        }
                        logInfo(this.player.getName() + " muted " + bname + ", reason " + reason);
                        final WcGlobalModeration wcgm = new WcGlobalModeration(WurmId.getNextWCCommandId(), this.player.getName(), (byte)this.player.getPower(), true, false, false, false, false, hours, 0, bname, reason);
                        if (Servers.localServer.LOGINSERVER) {
                            wcgm.sendFromLoginServer();
                            Trello.addMessage(this.player.getName(), bname, reason, hours);
                        }
                        else {
                            wcgm.sendToLoginServer();
                        }
                    }
                    else {
                        this.sendNormalServerMessage("You are too weak to mute " + p.getName() + '!');
                    }
                }
                catch (NoSuchPlayerException nsp) {
                    this.sendNormalServerMessage("No player online found on server with the name " + bname + ". So muting globally.");
                    final WcGlobalModeration wcgm = new WcGlobalModeration(WurmId.getNextWCCommandId(), this.player.getName(), (byte)this.player.getPower(), true, false, false, false, false, hours, 0, bname, reason);
                    if (Servers.localServer.LOGINSERVER) {
                        wcgm.sendFromLoginServer();
                        Trello.addMessage(this.player.getName(), bname, reason, hours);
                    }
                    else {
                        wcgm.sendToLoginServer();
                    }
                    Players.addMgmtMessage(this.player.getName(), "mutes " + bname + " for " + hours + " hours. Reason: " + reason);
                    final Message mess2 = new Message(this.player, (byte)9, "MGMT", "<" + this.player.getName() + "> mutes " + bname + " for " + hours + " hours. Reason: " + reason, Communicator.muteMsgRed, Communicator.muteMsgGreen, Communicator.muteMsgBlue);
                    Server.getInstance().addMessage(mess2);
                }
            }
        }
    }
    
    private void handleHashMessageToggleMounts(final int power) {
        if (power >= 3) {
            Constants.enabledMounts = !Constants.enabledMounts;
            if (Constants.enabledMounts) {
                this.sendSafeServerMessage("Okay, mounts, driving and horse spawning is now effective.");
            }
            else {
                this.sendSafeServerMessage("Okay, mounts, driving and horse spawning no longer work. You may still drive and ride if you boarded.");
            }
        }
    }
    
    private void handleHashMessageNow() {
        this.sendSafeServerMessage("Now=" + System.currentTimeMillis());
    }
    
    private void handleHashMessageWurmDate(final String message) {
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        if (tokens.hasMoreTokens()) {
            final String ttime = tokens.nextToken();
            try {
                final long wurmtime = Long.parseLong(ttime);
                this.sendNormalServerMessage("That Wurm Date is " + WurmCalendar.getDateFor(wurmtime));
                final long wurmcurrent = WurmCalendar.currentTime;
                final long wurmdiff = wurmcurrent - wurmtime;
                if (wurmdiff < 0L) {
                    this.sendNormalServerMessage("Approx real time until that date: " + Server.getTimeFor(-wurmdiff / 8L));
                }
                else {
                    this.sendNormalServerMessage("Approx real time since that date: " + Server.getTimeFor(wurmdiff / 8L));
                }
            }
            catch (Exception ex) {
                this.sendNormalServerMessage("Not a proper number.");
            }
        }
    }
    
    private void handleHashMessageHarvest(final String message) {
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        this.sendNormalServerMessage("Note: Secs=" + WurmCalendar.getCurrentTime());
        if (!tokens.hasMoreTokens()) {
            this.sendNormalServerMessage("Needs a param. Range is 1-" + WurmHarvestables.getMaxHarvestId() + ".");
        }
        else {
            final String harvest = tokens.nextToken();
            try {
                final int eventId = Integer.parseInt(harvest);
                if (eventId < 1 || eventId > WurmHarvestables.getMaxHarvestId()) {
                    this.sendNormalServerMessage("Not a proper harvest number. Range is 1-" + WurmHarvestables.getMaxHarvestId() + ".");
                }
                else if (!tokens.hasMoreTokens()) {
                    final WurmHarvestables.Harvestable harvestable = WurmHarvestables.getHarvestable(eventId);
                    if (harvestable != null) {
                        this.sendNormalServerMessage(harvestable.getHarvestEvent());
                    }
                }
                else {
                    final String ttime = tokens.nextToken();
                    try {
                        final long newHarvestTime = Long.parseLong(ttime);
                        if (newHarvestTime < WurmCalendar.getCurrentTime() - 4838400L) {
                            this.sendNormalServerMessage("cannot change as new data is over a wurm month ago...");
                        }
                        else if (newHarvestTime > WurmCalendar.getCurrentTime() + 29030400L) {
                            this.sendNormalServerMessage("cannot change as new data is over a wurm year in furure...");
                        }
                        else {
                            final WurmHarvestables.Harvestable harvestable2 = WurmHarvestables.getHarvestable(eventId);
                            if (harvestable2 != null) {
                                harvestable2.setSeasonStart(newHarvestTime, true);
                                this.sendNormalServerMessage(harvestable2.getHarvestEvent());
                            }
                        }
                    }
                    catch (Exception ex) {
                        this.sendNormalServerMessage("Not a proper wurm time.");
                    }
                }
            }
            catch (Exception ex2) {
                this.sendNormalServerMessage("Not a proper harvest number.");
            }
        }
    }
    
    private void handleHashMessageRealTime(final String message) {
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        if (tokens.hasMoreTokens()) {
            final String ttime = tokens.nextToken();
            try {
                final long l = Long.parseLong(ttime);
                this.sendNormalServerMessage("That is " + new java.util.Date(l));
                if (l > System.currentTimeMillis()) {
                    this.sendNormalServerMessage("Time until that date: " + Server.getTimeFor(l - System.currentTimeMillis()));
                }
                else {
                    this.sendNormalServerMessage("Time since that date: " + Server.getTimeFor(System.currentTimeMillis() - l));
                }
            }
            catch (Exception ex) {
                this.sendNormalServerMessage("Not a proper number.");
            }
        }
    }
    
    private boolean handleHashMessageRespond(final String message) {
        if (this.player.mayHearDevTalk() || this.player.mayHearMgmtTalk()) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            if (tokens.hasMoreTokens()) {
                final String tabName = tokens.nextToken().trim();
                if (tokens.hasMoreTokens()) {
                    final String whom = tokens.nextToken().trim();
                    if (tabName.equalsIgnoreCase("GM") && this.player.mayHearDevTalk()) {
                        this.player.respondGMTab(whom, "");
                        return true;
                    }
                    if (tabName.equalsIgnoreCase("MGMT")) {
                        this.player.respondMGMTTab(whom, "");
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private void handleHashMessageSetServer(final String message, final int power) {
        if (power >= 2) {
            try {
                final StringTokenizer tokens = new StringTokenizer(message);
                tokens.nextToken();
                String pname = "Unknown";
                pname = tokens.nextToken().trim();
                pname = LoginHandler.raiseFirstLetter(pname);
                final PlayerInfo info = PlayerInfoFactory.createPlayerInfo(pname);
                if (info != null) {
                    try {
                        info.load();
                        if (tokens.hasMoreTokens()) {
                            final int number = Integer.parseInt(tokens.nextToken());
                            info.setCurrentServer(number);
                            this.sendSafeServerMessage("Okay, set the current server of " + pname + " to " + number);
                            this.sendSafeServerMessage("Make sure to do this BOTH on the login server and the actual current server!");
                        }
                        else {
                            this.sendAlertServerMessage("Make sure of the syntax #setserver playername servernumber");
                        }
                    }
                    catch (IOException iox) {
                        this.sendAlertServerMessage("Failure when loading " + pname + ". Try setting to the previous server.");
                    }
                }
                else {
                    this.sendSafeServerMessage(pname + " does not exist on this server.");
                }
            }
            catch (Exception ex) {
                this.sendAlertServerMessage("Make sure of the syntax #setserver playername servernumber");
            }
        }
    }
    
    private void handleHashMessageOverrideShop(final String message, final int power) {
        if (power >= 3) {
            try {
                final StringTokenizer tokens = new StringTokenizer(message);
                tokens.nextToken();
                String pname = "Unknown";
                pname = tokens.nextToken().trim();
                pname = LoginHandler.raiseFirstLetter(pname);
                final PlayerInfo info = PlayerInfoFactory.createPlayerInfo(pname);
                if (info != null) {
                    info.setOverRideShop(Boolean.parseBoolean(tokens.nextToken()));
                    this.sendSafeServerMessage("Okay, set the flag of " + pname + " to " + info.overRideShop);
                }
                else {
                    this.sendSafeServerMessage(pname + " does not exist on this server.");
                }
            }
            catch (Exception ex) {
                this.sendAlertServerMessage("Make sure of the syntax #overrideshop playername <true|false>:" + ex.getMessage());
            }
        }
    }
    
    private void handleHashMessageBannedIps(final int power) {
        if (power >= 2) {
            final KingdomIp[] allKips;
            final KingdomIp[] kips = allKips = KingdomIp.getAllKips();
            for (final KingdomIp kip : allKips) {
                final long last = kip.getLastLogout();
                if (last == 0L) {
                    this.sendNormalServerMessage(kip.getIpAddress() + " " + Kingdoms.getNameFor(kip.getKingdom()) + " time=Still logged on");
                }
                else {
                    this.sendNormalServerMessage(kip.getIpAddress() + " " + Kingdoms.getNameFor(kip.getKingdom()) + " time=" + Server.getTimeFor(System.currentTimeMillis() - last));
                }
            }
        }
    }
    
    private void handleHashMessageCheckZones(final int power) {
        if (power >= 5) {
            ErrorChecks.checkZones(this.player);
        }
    }
    
    private void handleHashMessageCheckCreatures(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String toCheck = "";
            if (tokens.hasMoreTokens()) {
                toCheck = tokens.nextToken().trim();
            }
            ErrorChecks.checkCreatures(this.player, toCheck);
        }
    }
    
    private void handleHashMessageCheckItems(final String message, final int power) {
        if (power >= 5) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String toCheck = "";
            if (tokens.hasMoreTokens()) {
                toCheck = tokens.nextToken().trim();
            }
            ErrorChecks.checkItems(this.player, toCheck);
        }
    }
    
    private void handleHashMessageReloadItems(final String message, final int power) {
        if (power >= 4) {
            if (this.player.getLogger() != null) {
                this.player.getLogger().info(message);
            }
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            if (tokens.hasMoreTokens()) {
                try {
                    final long wurmid = Long.parseLong(tokens.nextToken().trim());
                    if (Items.reloadAllSubItems(this.player, wurmid)) {
                        this.sendNormalServerMessage("Finished reloading all items for " + wurmid + ".");
                    }
                }
                catch (NumberFormatException e) {
                    this.sendNormalServerMessage("Usage: #reloadItems <wurmId> - checks for all subitems of the given itemId and forces them back into that item.");
                }
            }
            else {
                this.sendNormalServerMessage("Usage: #reloadItems <wurmId> - checks for all subitems of the given itemId and forces them back into that item.");
            }
        }
    }
    
    private void handleHashMessageOnFire(final int power) {
        if (power >= 2) {
            if (this.player.isOnFire()) {
                this.sendSafeServerMessage("You are no longer on fire.");
                this.player.isOnFire = false;
            }
            else {
                this.sendSafeServerMessage("You are now on fire!");
                this.player.isOnFire = true;
            }
        }
    }
    
    private void handleHashMessageVeSpeed(final String message, final int power) {
        if (power >= 5) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            if (tokens.hasMoreTokens()) {
                final String speed = tokens.nextToken().trim();
                try {
                    final short speedb = Short.parseShort(speed);
                    if (this.player.isVehicleCommander()) {
                        this.player.getMovementScheme().addMountSpeed(speedb);
                        this.sendSafeServerMessage("Vehicle speed now " + speed);
                    }
                    else {
                        this.sendSafeServerMessage("Not commanding.");
                    }
                }
                catch (Exception ex) {}
            }
        }
    }
    
    private void handleHashMessageLagStatus(final int power) {
        if (power >= 5) {
            final int x = (int)((System.currentTimeMillis() - Server.getStartTime()) / 1000L);
            final int y = Server.getSecondsUptime();
            this.sendSafeServerMessage("The server has been up " + x + " seconds, and ticked " + y + " seconds (" + x / y * 100.0f + "%).");
        }
    }
    
    private void handleHashMessageAddReimbursement(final String message, final int power) {
        if (power >= 5) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String pname = "Unknown";
            pname = tokens.nextToken().trim();
            pname = LoginHandler.raiseFirstLetter(pname);
            String smonths = "0";
            boolean ok = false;
            if (tokens.hasMoreTokens()) {
                smonths = tokens.nextToken().trim();
                String sdays = "0";
                if (tokens.hasMoreTokens()) {
                    sdays = tokens.nextToken().trim();
                    String ssilvers = "0";
                    if (tokens.hasMoreTokens()) {
                        ssilvers = tokens.nextToken().trim();
                        String detail = "PI" + System.currentTimeMillis();
                        if (tokens.hasMoreTokens()) {
                            detail = tokens.nextToken().trim();
                            boolean det = false;
                            if (detail.toLowerCase().equals("true")) {
                                det = true;
                            }
                            try {
                                final int months = Integer.parseInt(smonths);
                                final int days = Integer.parseInt(sdays);
                                final int silvers = Integer.parseInt(ssilvers);
                                if (months >= 0 && days >= 0 && silvers >= 0 && (months != 0 || days != 0 || silvers != 0 || det)) {
                                    final LoginServerWebConnection lsw = new LoginServerWebConnection();
                                    this.sendSafeServerMessage(lsw.addReimb(this.player.getName(), pname, months, silvers, days, det));
                                    ok = true;
                                }
                            }
                            catch (Exception ex) {
                                this.sendAlertServerMessage("Error: " + ex.getMessage());
                                this.sendAlertServerMessage("Syntax: #addreimb name months days silvers setbok");
                            }
                        }
                    }
                }
            }
            if (!ok) {
                this.sendAlertServerMessage("Make sure #addreimb name months days silvers setbok:" + message);
            }
        }
    }
    
    private void handleHashMessageAddMoney(final String message, final int power) {
        if (power >= 5 || (!Servers.localServer.testServer && power >= 3)) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String pname = "Unknown";
            pname = tokens.nextToken().trim();
            pname = LoginHandler.raiseFirstLetter(pname);
            String smonths = "0";
            boolean ok = false;
            if (tokens.hasMoreTokens()) {
                smonths = tokens.nextToken().trim();
                String sdays = "0";
                if (tokens.hasMoreTokens()) {
                    sdays = tokens.nextToken().trim();
                    String ssilvers = "0";
                    if (tokens.hasMoreTokens()) {
                        ssilvers = tokens.nextToken().trim();
                        String detail = "PI" + System.currentTimeMillis();
                        if (tokens.hasMoreTokens()) {
                            detail = tokens.nextToken().trim();
                            while (tokens.hasMoreTokens()) {
                                detail = detail + ' ' + tokens.nextToken();
                            }
                            try {
                                final int months = Integer.parseInt(smonths);
                                final int days = Integer.parseInt(sdays);
                                final int silvers = Integer.parseInt(ssilvers);
                                if (months >= 0 && days >= 0 && (months != 0 || days != 0)) {
                                    final LoginServerWebConnection lsw = new LoginServerWebConnection();
                                    if (lsw.addPlayingTime(this.player, pname, months, days, detail)) {
                                        ok = true;
                                        this.sendSafeServerMessage("Added " + months + " months, and " + days + " days to " + pname + " with detail " + detail + '.');
                                        this.player.getLogger().log(Level.INFO, "Added " + months + " months, and " + days + " days to " + pname + " with detail " + detail + '.');
                                    }
                                    else {
                                        this.sendAlertServerMessage("Failed to add playing time.");
                                    }
                                }
                                if (silvers > 0) {
                                    final LoginServerWebConnection lsw = new LoginServerWebConnection();
                                    if (lsw.addMoney(this.player, pname, silvers * 10000, detail)) {
                                        ok = true;
                                        this.sendSafeServerMessage("Added " + silvers + " to " + pname + " with detail " + detail + '.');
                                        this.player.getLogger().log(Level.INFO, "Added " + silvers + " to " + pname + " with detail " + detail + '.');
                                    }
                                    else {
                                        this.sendAlertServerMessage("Failed to add money.");
                                    }
                                }
                            }
                            catch (Exception ex) {
                                this.sendAlertServerMessage("Error: " + ex.getMessage());
                                this.sendAlertServerMessage("Syntax: #addmoney name months days silvers detail");
                            }
                        }
                    }
                }
            }
            if (!ok) {
                this.sendAlertServerMessage("Make sure #addmoney name months days silvers detail:" + message);
            }
        }
    }
    
    private void handleHashMessageTimeMod(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String hours = "";
            if (tokens.hasMoreTokens()) {
                hours = tokens.nextToken().trim();
                try {
                    final int t = Integer.parseInt(hours);
                    if (t > -100 && t < 100) {
                        this.timeMod = t * 60;
                        this.sendAlertServerMessage("Your time modificator of " + t + " should be effective within a few minutes.");
                    }
                    else {
                        this.sendAlertServerMessage("Please use a time mod between -100 and 100.");
                    }
                }
                catch (Exception ex) {}
            }
            else {
                this.sendAlertServerMessage("Syntax #timemod <hours>");
            }
        }
    }
    
    private void handleHashMessageFindBoat(final String message, final int power) throws NoSuchItemException {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            if (tokens.hasMoreTokens()) {
                String pname = "Unknown";
                pname = tokens.nextToken().trim();
                if (pname.length() < 1) {
                    this.sendNormalServerMessage("Too short search string.");
                }
                else {
                    final Set<Item> boats = Items.getItemsWithDesc(pname, true);
                    for (final Item boat : boats) {
                        this.sendNormalServerMessage(boat.getDescription() + ", " + boat.getName() + " (id:" + boat.getWurmId() + ") at tile " + boat.getTileX() + ", " + boat.getTileY());
                    }
                }
            }
        }
    }
    
    private void handleHashMessageForceRiftLoot(final String message, final int power) {
        if (power >= 5) {
            final GMForceSpawnRiftLootQuestion gfsrlq = new GMForceSpawnRiftLootQuestion(this.player);
            gfsrlq.sendQuestion();
        }
    }
    
    private void handleHashMessageFindFish(final String message, final boolean newFish) {
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        Point[] points;
        if (newFish) {
            final ArrayList<Point> allPoints = new ArrayList<Point>();
            final int season = WurmCalendar.getSeasonNumber();
            points = MethodsFishing.getSpecialSpots(this.player.getTileX(), this.player.getTileY(), season);
            this.sendAlertServerMessage("New fishing spots");
            for (int i = 0; i < 2048; i += 128) {
                for (int j = 0; j < 2048; j += 128) {
                    for (final Point p : MethodsFishing.getSpecialSpots(i, j, season)) {
                        allPoints.add(p);
                    }
                }
            }
            for (final Point p2 : allPoints) {
                final String fishName = ItemTemplateFactory.getInstance().getTemplateName(p2.getH());
                this.sendNormalServerMessage(fishName + " @ " + p2.getX() + "," + p2.getY());
            }
        }
        else {
            points = Fish.getRareSpots(this.player.getTileX(), this.player.getTileY());
            this.sendAlertServerMessage("Old fishing spots");
        }
        for (final Point point : points) {
            final String fishName = ItemTemplateFactory.getInstance().getTemplateName(point.getH());
            this.sendNormalServerMessage(fishName + " @ " + point.getX() + "," + point.getY());
        }
        if (points.length == 0) {
            this.sendNormalServerMessage("No rare fish places found");
        }
    }
    
    private void handleHashMessageWorth(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            if (tokens.hasMoreTokens()) {
                String pname = "Unknown";
                pname = tokens.nextToken().trim();
                pname = LoginHandler.raiseFirstLetter(pname);
                try {
                    final Player p = Players.getInstance().getPlayer(pname);
                    this.sendNormalServerMessage(p.getName() + " is worth " + p.getRoyalLevels() + " royal levels.");
                    this.player.getLogger().log(Level.INFO, "Checked royal levels for " + pname);
                }
                catch (NoSuchPlayerException nsp) {
                    this.sendAlertServerMessage("No player with that name online.");
                }
            }
            else {
                this.sendAlertServerMessage("Provide a name");
            }
        }
    }
    
    private void handleHashMessageTestWeb(final String message, final int power) {
        if (power >= 5) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            if (tokens.hasMoreTokens()) {
                String pname = "Unknown";
                pname = tokens.nextToken().trim();
                pname = LoginHandler.raiseFirstLetter(pname);
                final LoginServerWebConnection lsw = new LoginServerWebConnection();
                lsw.testAdding(pname);
            }
            else {
                this.sendAlertServerMessage("Provide a name");
            }
        }
    }
    
    private void handleHashMessagePlayerStatuses(final int power) {
        if (power >= 5) {
            Players.getInstance().sendPlayerStatus(this.player);
        }
    }
    
    private void handleHashMessageReload(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String pname = "Unknown";
            if (tokens.hasMoreTokens()) {
                pname = tokens.nextToken().trim();
                try {
                    final long id = Long.parseLong(pname);
                    if (WurmId.getType(id) == 1) {
                        try {
                            final Creature c = Creatures.getInstance().getCreature(id);
                            this.sendNormalServerMessage("Retrieved the creature.");
                            if (c.getCurrentTile() != null) {
                                c.setNewTile(null, 0.0f, true);
                                this.sendNormalServerMessage("Set the creature to null tile.");
                            }
                            else {
                                this.sendNormalServerMessage("The creature had null tile.");
                            }
                            Zones.getZone(c.getTileX(), c.getTileY(), c.isOnSurface()).addCreature(id);
                            this.sendNormalServerMessage("Added creature to world again.");
                        }
                        catch (NoSuchCreatureException nsc2) {
                            try {
                                Creatures.getInstance().loadOfflineCreature(id);
                                this.sendNormalServerMessage("Load the creature from offline.");
                                final Creature c2 = Creatures.getInstance().getCreature(id);
                                if (c2.getCurrentTile() != null) {
                                    c2.setNewTile(null, 0.0f, true);
                                    this.sendNormalServerMessage("Set the creature to null tile.");
                                }
                                else {
                                    this.sendNormalServerMessage("The creature had null tile.");
                                }
                                Zones.getZone(c2.getTileX(), c2.getTileY(), c2.isOnSurface()).addCreature(id);
                                this.sendNormalServerMessage("Added creature to world again.");
                            }
                            catch (NoSuchCreatureException nsc1) {
                                this.sendNormalServerMessage("Failed to load it from offline.");
                                this.sendNormalServerMessage(nsc1.getMessage());
                            }
                        }
                        catch (Exception ex) {
                            this.sendNormalServerMessage(ex.getMessage());
                        }
                    }
                }
                catch (Exception ex2) {
                    pname = LoginHandler.raiseFirstLetter(pname);
                    final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(pname);
                    if (pinf != null) {
                        pinf.loaded = false;
                        try {
                            pinf.load();
                            this.sendNormalServerMessage("Reloaded " + pname + '.');
                            final WcRefreshCommand wcr = new WcRefreshCommand(WurmId.getNextWCCommandId(), pname);
                            wcr.sendToLoginServer();
                        }
                        catch (IOException iuox) {
                            this.sendNormalServerMessage("Exception when reloading " + pname + '.');
                        }
                    }
                    else {
                        this.sendNormalServerMessage(pname + " could not be found.");
                    }
                }
            }
            else {
                this.sendNormalServerMessage("Syntax: #reload <creatureId or playername>");
            }
        }
    }
    
    private void handleHashMessageRedeem(final int power) {
        if (power >= 3) {
            final RedeemQuestion rq = new RedeemQuestion(this.player, "Redeem inventory", "Who do you wish to redeem items from?");
            rq.sendQuestion();
        }
    }
    
    private void handleHashMessageAnnounce(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String toSend = "";
            if (tokens.hasMoreTokens()) {
                toSend = tokens.nextToken();
            }
            while (tokens.hasMoreTokens()) {
                toSend = toSend + ' ' + tokens.nextToken();
            }
            final Message mess = new Message(this.player, (byte)1, ":Event", "<" + this.player.getName() + "> " + toSend);
            Server.getInstance().addMessage(mess);
        }
    }
    
    private void handleHashMessageBroadcast(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String toSend = "";
            if (tokens.hasMoreTokens()) {
                toSend = tokens.nextToken();
            }
            while (tokens.hasMoreTokens()) {
                toSend = toSend + ' ' + tokens.nextToken();
            }
            final Message mess = new Message(this.player, (byte)5, ":Event", "<" + this.player.getName() + "> " + toSend);
            Server.getInstance().addMessage(mess);
        }
    }
    
    private void handleHashMessageTestAffinity(final int power) {
        if (power >= 3 && Servers.localServer.testServer) {
            int nextaff = 1;
            Skill skill = null;
            final int sknum = SkillSystem.getRandomSkillNum();
            try {
                skill = this.player.getSkills().getSkill(sknum);
                nextaff = skill.affinity + 1;
            }
            catch (NoSuchSkillException ex) {}
            catch (Exception ex2) {}
            Affinities.setAffinity(this.player.getWurmId(), sknum, nextaff, false);
            if (nextaff > 1) {
                this.player.setAffString("You realize that your affinity for " + SkillSystem.getNameFor(sknum).toLowerCase() + " has grown stronger.");
            }
            else {
                this.player.setAffString("You realize that you have developed an affinity for " + SkillSystem.getNameFor(sknum).toLowerCase() + ".");
            }
        }
    }
    
    private void handleHashMessageTestFragments(final String message, final int power) {
        if (power >= 5 && Servers.localServer.testServer) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            int identifyPower = -1;
            if (tokens.hasMoreTokens()) {
                identifyPower = Integer.parseInt(tokens.nextToken());
            }
            for (int i = 0; i < 3; ++i) {
                try {
                    final Item it = ItemFactory.createItem(1307, 90.0f, "System");
                    it.setRealTemplate(8);
                    it.setAuxData((byte)1);
                    if (identifyPower > 0) {
                        it.setData1(1);
                        it.setData2(identifyPower);
                    }
                    this.player.getInventory().insertItem(it);
                }
                catch (FailedException ex) {}
                catch (NoSuchTemplateException ex2) {}
            }
        }
    }
    
    private void handleHashMessageTestColors(final String message, final int power) {
        if (power >= 3 && Servers.isThisATestServer()) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String toSend = "";
            final List<MulticolorLineSegment> msla = new ArrayList<MulticolorLineSegment>();
            final MulticolorLineSegment msl = new MulticolorLineSegment(this.player.getName(), (byte)3);
            msla.add(msl);
            while (tokens.hasMoreTokens()) {
                toSend = tokens.nextToken();
                msla.add(new MulticolorLineSegment(toSend + " ", (byte)Server.rand.nextInt(15)));
            }
            this.sendColoredMessage("Testing colors", msla);
        }
    }
    
    private void handleHashMessageHelp(final int power) {
        if (power >= 1 || this.player.mayMute() || this.player.mayAppointPlayerAssistant()) {
            this.sendDeityHelp(power);
        }
    }
    
    private void handleHashMessageUniques(final int power) {
        if (power >= 5) {
            final Creature[] crets = Creatures.getInstance().getCreatures();
            int nums = 0;
            for (int x = 0; x < crets.length; ++x) {
                if (crets[x].isUnique()) {
                    ++nums;
                    this.sendNormalServerMessage(crets[x].getName() + " at " + crets[x].getTileX() + ", " + crets[x].getTileY() + " z=" + crets[x].getPositionZ());
                }
            }
            this.sendNormalServerMessage("Found " + nums + " creatures.");
        }
    }
    
    private void handleHashMessageTilePollLog(final int power) {
        if (power >= 5) {
            TilePoller.logTilePolling = !TilePoller.logTilePolling;
            TilePoller.sentTilePollMessages = 0;
            this.sendNormalServerMessage("TilePollLogging is now " + TilePoller.logTilePolling);
        }
    }
    
    private void handleHashMessageInvulnerable(final int power) {
        if (power >= 3) {
            if (this.player.GMINVULN) {
                this.player.GMINVULN = false;
                this.sendNormalServerMessage("You are now no longer invulnerable.");
            }
            else {
                this.player.GMINVULN = true;
                this.sendNormalServerMessage("You are now invulnerable again.");
            }
        }
    }
    
    private void handleHashMessageChangeEmail(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String pname = "Unknown";
            if (tokens.hasMoreTokens()) {
                pname = tokens.nextToken().trim();
                pname = LoginHandler.raiseFirstLetter(pname);
                String email = "Unknown";
                if (tokens.hasMoreTokens()) {
                    email = tokens.nextToken().trim();
                    this.sendNormalServerMessage("You try to change the email of " + pname + " to '" + email + "' - result:");
                    final LoginServerWebConnection lsw = new LoginServerWebConnection();
                    this.sendNormalServerMessage(lsw.changeEmail(this.player.getName(), pname, email, null, this.player.getPower(), this.player.getSaveFile().pwQuestion, this.player.getSaveFile().pwAnswer));
                    if (this.player.getLogger() != null) {
                        this.player.getLogger().log(Level.INFO, this.player.getName() + " changed the email of " + pname + " to " + email);
                    }
                }
                else {
                    this.sendNormalServerMessage("Syntax: #changeemail <name> <newemail>");
                }
            }
            else {
                this.sendNormalServerMessage("Syntax: #changeemail <name> <newemail>");
            }
        }
    }
    
    private void handleHashMessageChangePassword(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String pname = "Unknown";
            if (tokens.hasMoreTokens()) {
                pname = tokens.nextToken().trim();
                pname = LoginHandler.raiseFirstLetter(pname);
                String password = "Unknown";
                if (tokens.hasMoreTokens()) {
                    password = tokens.nextToken().trim();
                    if (password.length() >= 6) {
                        this.sendNormalServerMessage("You try to change the password of " + pname + " to '" + password + "' - result:");
                        final LoginServerWebConnection lsw = new LoginServerWebConnection();
                        this.sendNormalServerMessage(lsw.changePassword(this.player.getName(), pname, password, this.player.getPower()));
                        if (this.player.getLogger() != null) {
                            this.player.getLogger().log(Level.INFO, this.player.getName() + " changed the password of " + pname);
                        }
                    }
                    else {
                        this.sendNormalServerMessage("The password needs at least 6 characters.");
                    }
                }
                else {
                    this.sendNormalServerMessage("Syntax: #changepassword <name> <newpassword>");
                }
            }
            else {
                this.sendNormalServerMessage("Syntax: #changepassword <name> <newpassword>");
            }
        }
    }
    
    private void handleHashMessageToggleCA(final String message) {
        if (this.player.mayAppointPlayerAssistant()) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String pname = "Unknown";
            pname = tokens.nextToken().trim();
            Players.appointCA(this.player, pname);
        }
    }
    
    private void handleHashMessageSetMuter(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String pname = "Unknown";
            pname = tokens.nextToken().trim();
            Players.appointCM(this.player, pname);
        }
    }
    
    private void handleHashMessagePrintRanks(final int power) {
        if (power >= 5) {
            Players.printRanks();
        }
    }
    
    private void handleHashMessageShowMuted(final int power) {
        if (power >= 2 || this.player.mayMute()) {
            final Ban[] bips = Players.getInstance().getPlayersMuted();
            if (bips.length > 0) {
                this.sendNormalServerMessage("PLAYERS MUTED:");
                for (int x = 0; x < bips.length; ++x) {
                    final long daytime = Math.max(0L, bips[x].getExpiry() - System.currentTimeMillis());
                    this.sendNormalServerMessage(bips[x].getIdentifier() + ", " + Server.getTimeFor(daytime) + ", " + bips[x].getReason());
                }
            }
            else {
                this.sendNormalServerMessage("NO PLAYERS MUTED.");
            }
        }
    }
    
    private void handleHashMessageShowMuters(final int power) {
        if (power >= 2 || this.player.mayMute()) {
            final String[] muters = Players.getInstance().getMuters();
            if (muters.length > 0) {
                this.sendNormalServerMessage("These people may mute other players except the gms:");
                for (int x = 0; x < muters.length; ++x) {
                    this.sendNormalServerMessage(muters[x]);
                }
            }
            else {
                this.sendNormalServerMessage("Only the gms may mute other players.");
            }
        }
    }
    
    private void handleHashMessageShowDevTalkers(final int power) {
        if (power >= 2 || this.player.mayHearDevTalk()) {
            final String[] devTalkers = Players.getInstance().getDevTalkers();
            if (devTalkers.length > 0) {
                this.sendNormalServerMessage("These people may hear GM Tab:");
                for (int x = 0; x < devTalkers.length; ++x) {
                    this.sendNormalServerMessage(devTalkers[x]);
                }
            }
            else {
                this.sendNormalServerMessage("Noone can hear GM Tab?");
            }
        }
    }
    
    private void handleHashMessageShowCas(final int power) {
        if (power >= 2 || this.player.mayMute()) {
            final String[] cas = Players.getInstance().getCAs();
            if (cas.length > 0) {
                this.sendNormalServerMessage("These people are CAs:");
                for (int x = 0; x < cas.length; ++x) {
                    this.sendNormalServerMessage(cas[x]);
                }
            }
            else {
                this.sendNormalServerMessage("No CAs?");
            }
        }
    }
    
    private void handleHashMessageShowHeros(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            byte checkForPower = 1;
            if (tokens.hasMoreTokens()) {
                try {
                    checkForPower = Byte.parseByte(tokens.nextToken().trim());
                }
                catch (Exception ex) {}
            }
            if (checkForPower < 0 || checkForPower > 5) {
                this.sendSafeServerMessage("Power must be between 0 and 5 inclusive.");
            }
            else if (power >= checkForPower) {
                this.sendSafeServerMessage(WcGetHeroes.getHeroes(checkForPower));
                final WcGetHeroes wcg = new WcGetHeroes(this.player.getWurmId(), checkForPower);
                wcg.sendFromLoginServer();
            }
            else {
                this.sendSafeServerMessage("You are not allowed to check for that power.");
            }
        }
    }
    
    private void handleHashMessageUnmute(final String message, final int power) {
        if (power >= 2 || this.player.mayMute()) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String pname = "Unknown";
            pname = tokens.nextToken().trim();
            pname = LoginHandler.raiseFirstLetter(pname);
            Player p = null;
            try {
                try {
                    final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(pname);
                    pinf.load();
                    if (pinf.wurmId > 0L) {
                        pinf.setMuted(false, "", 0L);
                        this.sendNormalServerMessage("You have given " + pname + " the voice back.");
                    }
                    else {
                        this.sendNormalServerMessage("No player found with the name " + pname);
                    }
                }
                catch (IOException ex) {}
                p = Players.getInstance().getPlayer(pname);
                if (p != null) {
                    p.getCommunicator().sendAlertServerMessage("You have been given your voice back and can shout again.");
                }
            }
            catch (NoSuchPlayerException ex2) {}
            this.sendNormalServerMessage("Unmuting globally.");
            final WcGlobalModeration wcgm = new WcGlobalModeration(WurmId.getNextWCCommandId(), this.player.getName(), (byte)this.player.getPower(), false, true, false, false, false, 0, 0, pname, "");
            if (Servers.localServer.LOGINSERVER) {
                wcgm.sendFromLoginServer();
                Trello.addMessage(this.player.getName(), pname, "", 0);
            }
            else {
                wcgm.sendToLoginServer();
            }
            Players.addMgmtMessage(this.player.getName(), "unmutes " + pname);
            final Message mess = new Message(this.player, (byte)9, "MGMT", "<" + this.player.getName() + "> unmutes " + pname, Communicator.muteMsgRed, Communicator.muteMsgGreen, Communicator.muteMsgBlue);
            Server.getInstance().addMessage(mess);
        }
    }
    
    private void handleHashMessageCheckAffinity(final String message, final int power) {
        if (power >= 5) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String pname = "Unknown";
            try {
                pname = tokens.nextToken().trim();
                pname = LoginHandler.raiseFirstLetter(pname);
                final Player p = Players.getInstance().getPlayer(pname);
                final Affinity[] affs = Affinities.getAffinities(p.getWurmId());
                if (affs.length == 0) {
                    this.sendNormalServerMessage(p.getName() + " has no affinity.");
                }
                else {
                    for (int x = 0; x < affs.length; ++x) {
                        this.sendNormalServerMessage(SkillSystem.getNameFor(affs[x].skillNumber) + ':' + affs[x].number);
                    }
                }
            }
            catch (NoSuchPlayerException nsp) {
                this.sendSafeServerMessage("No player found with the name " + pname);
            }
        }
    }
    
    private void handleHashMessageMuteWarn(final String message, final int power) {
        if (power >= 2 || this.player.mayMute()) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String pname = "Unknown";
            pname = tokens.nextToken().trim();
            pname = LoginHandler.raiseFirstLetter(pname);
            final StringBuilder reason = new StringBuilder();
            while (tokens.hasMoreTokens()) {
                reason.append(tokens.nextToken());
                reason.append(' ');
            }
            final String reas = reason.toString();
            try {
                final Player p = Players.getInstance().getPlayer(pname);
                if (this.player.getLogger() != null) {
                    this.player.getLogger().log(Level.INFO, this.player.getName() + " mutewarns " + pname + " (" + reas + ")");
                }
                Players.addMgmtMessage(this.player.getName(), "mutewarns " + pname + " (" + reas + ")");
                final Message mess = new Message(this.player, (byte)9, "MGMT", "<" + this.player.getName() + "> mutewarns " + pname + " (" + reas + ")", Communicator.muteMsgRed, Communicator.muteMsgGreen, Communicator.muteMsgBlue);
                final WcGlobalModeration wcgm = new WcGlobalModeration(WurmId.getNextWCCommandId(), this.player.getName(), (byte)this.player.getPower(), false, false, true, false, false, 0, 0, pname, reas);
                if (Servers.localServer.LOGINSERVER) {
                    wcgm.sendFromLoginServer();
                    Trello.addMessage(this.player.getName(), pname, reas, 0);
                }
                else {
                    wcgm.sendToLoginServer();
                }
                Server.getInstance().addMessage(mess);
                if (p.getPower() <= power) {
                    p.getCommunicator().sendAlertServerMessage(this.player.getName() + " issues a warning that you may be muted. Be silent for a while and try to understand why or change the subject of your conversation please.");
                    if (!reas.isEmpty()) {
                        p.getCommunicator().sendAlertServerMessage("The reason for this is '" + reas + "'");
                    }
                    this.player.getCommunicator().sendSafeServerMessage("You warn " + p.getName() + " that " + p.getHeSheItString() + " may be muted.");
                    this.player.getCommunicator().sendSafeServerMessage("The reason you gave was '" + reas + "'.");
                }
                else {
                    this.sendNormalServerMessage("You threaten " + pname + " with muting!");
                    p.getCommunicator().sendNormalServerMessage(this.player.getName() + " tried to threaten you with muting!");
                    if (!reas.isEmpty()) {
                        p.getCommunicator().sendNormalServerMessage("The formal reason for this is '" + reas + "'");
                    }
                }
            }
            catch (NoSuchPlayerException nsp) {
                this.sendSafeServerMessage("No player found with the name " + pname + ". Warning globally.");
                Players.addMgmtMessage(this.player.getName(), "mutewarns " + pname + " (" + reas + ")");
                final Message mess = new Message(this.player, (byte)9, "MGMT", "<" + this.player.getName() + "> mutewarns " + pname + " (" + reas + ")", Communicator.muteMsgRed, Communicator.muteMsgGreen, Communicator.muteMsgBlue);
                Server.getInstance().addMessage(mess);
                final WcGlobalModeration wcgm = new WcGlobalModeration(WurmId.getNextWCCommandId(), this.player.getName(), (byte)this.player.getPower(), false, false, true, false, false, 0, 0, pname, reas);
                if (Servers.localServer.LOGINSERVER) {
                    wcgm.sendFromLoginServer();
                    Trello.addMessage(this.player.getName(), pname, reas, 0);
                }
                else {
                    wcgm.sendToLoginServer();
                }
                Players.addMgmtMessage(this.player.getName(), "mutewarns " + pname + " (" + reas + ")");
            }
        }
    }
    
    private void handleHashMessageItemPosition(final String message, final int power) {
        if (power >= 3) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            long wurmid = -1L;
            if (tokens.hasMoreTokens()) {
                try {
                    final String wid = tokens.nextToken().trim();
                    wurmid = Long.parseLong(wid);
                    final Item checked = Items.getItem(wurmid);
                    long owner = checked.getOwnerId();
                    if (owner == -10L) {
                        this.sendNormalServerMessage(checked.getName() + " is at " + ((int)checked.getPosX() >> 2) + ',' + ((int)checked.getPosY() >> 2) + " surfaced=" + checked.isOnSurface() + '.');
                        try {
                            owner = checked.lastOwner;
                            if (owner != -10L) {
                                final Creature c = Server.getInstance().getCreature(owner);
                                this.sendNormalServerMessage(checked.getName() + " was dropped by " + c.getName() + '.');
                            }
                        }
                        catch (NoSuchPlayerException nsp) {
                            try {
                                final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(Players.getInstance().getNameFor(owner));
                                pinf.load();
                                this.sendNormalServerMessage(checked.getName() + " was dropped by " + pinf.getName() + '.');
                            }
                            catch (NoSuchPlayerException nsp2) {
                                this.sendNormalServerMessage(checked.getName() + " was dropped by unknown player or creature.");
                            }
                            catch (IOException iox) {
                                this.sendNormalServerMessage("An IO error occured when trying to retrieve the last owner for this object.");
                            }
                        }
                        catch (NoSuchCreatureException nsc) {
                            this.sendNormalServerMessage(checked.getName() + " is carried by an unknown, maybe dead creature.");
                        }
                    }
                    else {
                        try {
                            final Creature c = Server.getInstance().getCreature(owner);
                            this.sendNormalServerMessage(checked.getName() + " is carried by " + c.getName() + " at " + c.getTileX() + ',' + c.getTileY() + " surfaced=" + c.isOnSurface() + '.');
                        }
                        catch (NoSuchPlayerException nsp) {
                            try {
                                final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(Players.getInstance().getNameFor(owner));
                                pinf.load();
                                this.sendNormalServerMessage(checked.getName() + " is carried by " + pinf.getName() + '.');
                            }
                            catch (NoSuchPlayerException nsp2) {
                                this.sendNormalServerMessage(checked.getName() + " is carried by unknown player with id " + owner + ". This is weird.");
                            }
                            catch (IOException iox) {
                                this.sendNormalServerMessage("An IO error occured when trying to retrieve the owner for this object.");
                            }
                        }
                        catch (NoSuchCreatureException nsc) {
                            this.sendNormalServerMessage(checked.getName() + " is carried by an unknown, maybe dead creature.");
                        }
                    }
                }
                catch (NoSuchItemException ex) {
                    this.sendNormalServerMessage(wurmid + " could not be found, probably deleted.");
                }
            }
            else {
                this.sendNormalServerMessage("Syntax: #itempos <id>");
            }
        }
    }
    
    private void handleHashMessagePLimit(final String message, final int power) {
        if (power >= 2) {
            if (!Servers.localServer.playerLimitOverridable) {
                this.sendNormalServerMessage("The player limit was set through command line so it can't be overriden from within the game.");
                return;
            }
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            int limit = Servers.localServer.pLimit;
            if (tokens.hasMoreTokens()) {
                try {
                    final String limp = tokens.nextToken().trim();
                    logInfo(this.player.getName() + " setting playerlimit " + limp + '.');
                    this.sendNormalServerMessage("The old limit was " + limit + " - new will be " + limp + " " + '.');
                    limit = Integer.parseInt(limp);
                    Servers.localServer.pLimit = limit;
                    Servers.localServer.saveNewGui(Servers.localServer.id);
                }
                catch (Exception ex) {
                    this.sendNormalServerMessage("You need to provide a number..");
                }
            }
            else {
                this.sendNormalServerMessage("The limit is " + limit + '.');
            }
        }
    }
    
    private void handleHashMessageAddFakeMoney(final String message, final int power) {
        if (power >= 3 && Servers.localServer.testServer) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            long amount = 0L;
            try {
                final String am = tokens.nextToken().trim();
                amount = Math.abs(Long.parseLong(am));
                final Change c = Economy.getEconomy().getChangeFor(amount);
                this.sendNormalServerMessage("Creating amount " + c.getChangeString());
                final Item[] coinsArr = Economy.getEconomy().getCoinsFor(amount);
                long owner = -10L;
                long parent = -10L;
                if (tokens.hasMoreTokens()) {
                    final String own = tokens.nextToken().trim();
                    owner = Long.parseLong(own);
                    if (tokens.hasMoreTokens()) {
                        final String par = tokens.nextToken().trim();
                        parent = Long.parseLong(par);
                    }
                }
                for (int x = 0; x < coinsArr.length; ++x) {
                    if (owner > -10L) {
                        coinsArr[x].setOwnerId(owner);
                    }
                    if (parent > -10L) {
                        coinsArr[x].setParentId(parent, this.player.isOnSurface());
                    }
                    this.sendAddToInventory(coinsArr[x], -1L, -1L, coinsArr[x].getValue());
                }
            }
            catch (Exception ex) {
                this.sendNormalServerMessage("Fail: " + ex.getMessage());
            }
        }
    }
    
    private void handleHashMessageResetGuards(final int power) {
        if (power >= 5) {
            final int nums = Creatures.getInstance().resetGuardSkills();
            this.player.getCommunicator().sendNormalServerMessage("Reset " + nums + " guards skills.");
        }
    }
    
    private void handleHashMessageNoXmas(final int power) {
        if (power >= 5) {
            Zones.removeChristmasEffect(this.player);
        }
    }
    
    private void handleHashMessageXmasLight(final int power) {
        if (power >= 5) {
            try {
                final Item temp = ItemFactory.createItem(344, 1.0f, this.player.getPosX(), this.player.getPosY(), 180.0f, true, (byte)0, -10L, null);
                Zones.sendChristmasEffect(this.player, temp);
            }
            catch (Exception ex22) {
                this.sendAlertServerMessage("Failed to create temp marker: " + ex22.getMessage());
            }
        }
    }
    
    private void handleHashMessageToggleEpic(final int power) {
        if (power >= 2) {
            PortalQuestion.epicPortalsEnabled = !PortalQuestion.epicPortalsEnabled;
            final WcOpenEpicPortal wccom = new WcOpenEpicPortal(WurmId.getNextWCCommandId(), PortalQuestion.epicPortalsEnabled);
            if (Servers.localServer.LOGINSERVER) {
                wccom.sendFromLoginServer();
            }
            else {
                wccom.sendToLoginServer();
            }
            this.sendNormalServerMessage("Okay, portals enabled=" + PortalQuestion.epicPortalsEnabled + ".");
        }
    }
    
    private void handleHashMessageMac(final String message, final int power) {
        if (power > 1) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String name = "";
            if (tokens.hasMoreTokens()) {
                name = tokens.nextToken();
            }
            if (name != null && !name.isEmpty()) {
                try {
                    final Player p = Players.getInstance().getPlayer(LoginHandler.raiseFirstLetter(name));
                    this.sendNormalServerMessage(p.getName() + " mac address=" + p.getCommunicator().macAddr);
                }
                catch (NoSuchPlayerException ex) {
                    this.sendNormalServerMessage("No such player: " + name);
                }
            }
        }
    }
    
    private void handleHashMessageResetPlayer(final String message, final int power) {
        if (power >= 3) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String name = "";
            if (tokens.hasMoreTokens()) {
                name = tokens.nextToken();
            }
            final PlayerInfo p = PlayerInfoFactory.createPlayerInfo(name);
            try {
                p.load();
                if (p.wurmId > 0L) {
                    Players.getInstance().resetPlayer(p.wurmId);
                    final WcResetCommand wccom = new WcResetCommand(WurmId.getNextWCCommandId(), p.wurmId);
                    wccom.sendToLoginServer();
                    this.sendNormalServerMessage("Okay, reset " + p.getName() + ". Skills are at max 20 and no realdeath anymore.");
                    try {
                        final Player pla = Players.getInstance().getPlayer(p.wurmId);
                        pla.getCommunicator().sendAlertServerMessage("Your skills were reset to max 20 and you are no longer a champion.");
                    }
                    catch (NoSuchPlayerException ex) {}
                }
            }
            catch (IOException ex2) {}
        }
    }
    
    private void handleHashMessageGm(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String toSend = "";
            if (tokens.hasMoreTokens()) {
                toSend = tokens.nextToken();
            }
            while (tokens.hasMoreTokens()) {
                toSend = toSend + ' ' + tokens.nextToken();
            }
            if (toSend.trim().length() > 1) {
                final Message mess = new Message(this.player, (byte)11, "GM", "<" + this.player.getName() + "> " + toSend);
                Players.addGmMessage(this.player.getName(), toSend);
                Server.getInstance().addMessage(mess);
                final WCGmMessage wccom = new WCGmMessage(WurmId.getNextWCCommandId(), this.player.getName(), "(" + Servers.localServer.getAbbreviation() + ") " + toSend, false);
                if (Servers.localServer.LOGINSERVER) {
                    wccom.sendFromLoginServer();
                }
                else {
                    wccom.sendToLoginServer();
                }
            }
        }
    }
    
    private void handleHashMessageStartY(final String message, final int power) {
        if (power >= 3) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            int startY = Servers.localServer.SPAWNPOINTJENNY;
            try {
                final String sty = tokens.nextToken().trim();
                logInfo("Setting start tile y to " + sty + '.');
                this.sendNormalServerMessage("Setting start tile y to " + sty + '.');
                startY = Integer.parseInt(sty);
                Servers.localServer.SPAWNPOINTJENNY = startY;
                Servers.localServer.updateSpawns();
            }
            catch (Exception ex) {}
        }
    }
    
    private void handleHashMessageStartX(final String message, final int power) {
        if (power >= 3) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            int startX = Servers.localServer.SPAWNPOINTJENNX;
            try {
                final String stx = tokens.nextToken().trim();
                logInfo("Setting start tile x to " + stx + '.');
                this.sendNormalServerMessage("Setting start tile x to " + stx + '.');
                startX = Integer.parseInt(stx);
                Servers.localServer.SPAWNPOINTJENNX = startX;
                Servers.localServer.updateSpawns();
            }
            catch (Exception ex) {}
        }
    }
    
    private void handleHashMessageSlate(final int power) {
        if (power >= 5) {
            for (int x = 200; x < Zones.worldTileSizeX - 200; ++x) {
                for (int y = 200; y < Zones.worldTileSizeY - 200; ++y) {
                    final int tile = Server.caveMesh.getTile(x, y);
                    if (Tiles.decodeType(tile) == Tiles.Tile.TILE_CAVE_WALL_MARBLE.id) {
                        this.sendNormalServerMessage("Marble at " + x + "," + y);
                    }
                    else if (Tiles.decodeType(tile) == Tiles.Tile.TILE_CAVE_WALL_SLATE.id) {
                        this.sendNormalServerMessage("Slate at " + x + "," + y);
                    }
                }
            }
        }
    }
    
    private void handleHashMessageEnableBoats(final int power) {
        if (power >= 5) {
            CreationEntryCreator.createBoatEntries();
        }
    }
    
    private void handleHashMessageLoadItemFromHell(final String message, final int power) throws Exception {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            long itemid = 0L;
            try {
                final String id = tokens.nextToken().trim();
                if (this.player.getLogger() != null) {
                    this.player.getLogger().log(Level.INFO, this.player.getName() + " trying to load item ." + id + '.');
                }
                itemid = Long.parseLong(id);
            }
            catch (Exception ex) {}
            Item toLoad = null;
            try {
                toLoad = Items.getItem(itemid);
            }
            catch (NoSuchItemException nsi) {
                try {
                    toLoad = new DbItem(itemid);
                    Items.putItem(toLoad);
                    this.player.getCommunicator().sendNormalServerMessage("Tried to load non-loaded item." + itemid + '.');
                }
                catch (NoSuchItemException nsii) {
                    try {
                        toLoad = new DbItem(itemid, true);
                        Items.putItem(toLoad);
                        this.player.getCommunicator().sendNormalServerMessage("Tried to load frozen item." + itemid + '.');
                    }
                    catch (NoSuchItemException nsi2) {
                        toLoad = null;
                    }
                }
            }
            if (toLoad == null) {
                try {
                    toLoad = new DbItem(itemid);
                    Items.putItem(toLoad);
                    this.player.getCommunicator().sendNormalServerMessage("Tried to load non-loaded item." + itemid + '.');
                }
                catch (NoSuchItemException nsi) {
                    try {
                        toLoad = new DbItem(itemid, true);
                        Items.putItem(toLoad);
                        this.player.getCommunicator().sendNormalServerMessage("Tried to load frozen item." + itemid + '.');
                    }
                    catch (NoSuchItemException nsi3) {
                        toLoad = null;
                    }
                }
            }
            if (toLoad != null) {
                if (toLoad.getTemplateId() == 1310) {
                    this.sendNormalServerMessage("Stored creature detected, summoning creature instead.");
                    if (toLoad.getParent() != null) {
                        toLoad.getParent().setName("creature cage [Empty]");
                        toLoad.getParent().setAuxData((byte)0);
                        CargoTransportationMethods.updateItemModel(toLoad.getParent());
                    }
                    final Creature getCreature = Creatures.getInstance().getCreature(toLoad.getData());
                    final Creatures cstat = Creatures.getInstance();
                    getCreature.getStatus().setDead(false);
                    cstat.removeCreature(getCreature);
                    cstat.addCreature(getCreature, false);
                    getCreature.putInWorld();
                    CreatureBehaviour.blinkTo(getCreature, this.player.getPosX(), this.player.getPosY(), this.player.getLayer(), this.player.getPositionZ(), this.player.getBridgeId(), this.player.getFloorLevel());
                    getCreature.save();
                    getCreature.savePosition(toLoad.getZoneId());
                    Items.destroyItem(toLoad.getWurmId());
                }
                if (toLoad.isRoadMarker()) {
                    toLoad.setWhatHappened(" loaded from hell by " + this.player.getName());
                }
                toLoad.setIsPlanted(false);
                toLoad.setTransferred(false);
                toLoad.setBanked(false);
                if (!WurmMail.isItemInMail(itemid)) {
                    toLoad.setMailed(false);
                }
                if (toLoad.getZoneId() >= 0) {
                    try {
                        final Zone z = Zones.getZone((int)toLoad.getPosX() >> 2, (int)toLoad.getPosY() >> 2, toLoad.isOnSurface());
                        z.removeItem(toLoad);
                        this.sendNormalServerMessage(toLoad.getName() + " (" + itemid + ") was removed from " + ((int)toLoad.getPosX() >> 2) + ',' + ((int)toLoad.getPosY() >> 2) + ", surf=" + toLoad.isOnSurface());
                    }
                    catch (NoSuchZoneException nsz) {
                        this.sendNormalServerMessage(toLoad.getName() + " (" + itemid + ") was not on " + ((int)toLoad.getPosX() >> 2) + ',' + ((int)toLoad.getPosY() >> 2) + ", surf=" + toLoad.isOnSurface());
                    }
                }
                try {
                    final Item parent = toLoad.getParent();
                    parent.dropItem(itemid, true);
                    this.sendNormalServerMessage(toLoad.getName() + " (" + itemid + ") was removed from " + parent.getName() + '.');
                }
                catch (NoSuchItemException ex2) {}
                if (this.player.getLogger() != null) {
                    this.player.getLogger().log(Level.INFO, this.player.getName() + " loaded item " + toLoad.getName() + "( " + itemid + ").");
                }
                this.player.getInventory().insertItem(toLoad);
            }
            else {
                this.sendNormalServerMessage("Item was null?");
            }
        }
    }
    
    private void handleHashMessageShowPersonalGoals(final String message, final int power) {
        if (power < 4) {
            return;
        }
        if (this.player.getLogger() != null) {
            this.player.getLogger().info("Show personal goals: " + message);
        }
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        try {
            final String name = tokens.nextToken().trim();
            final PlayerInfo player = PlayerInfoFactory.getPlayerInfoWithName(name);
            if (player == null) {
                this.sendNormalServerMessage("No player named \"" + name + "\" found online or offline on this server. Check your spelling. If the spelling is correct, have they been on this server before?");
                return;
            }
            new PersonalGoalsListQuestion(this.player, player.wurmId).sendQuestion();
        }
        catch (Exception ex) {
            this.sendNormalServerMessage("Something went wrong with the input.");
            this.sendNormalServerMessage(ex.toString());
        }
    }
    
    private void handleHashMessageGetAchievementData(final String message, final int power) {
        if (power < 2) {
            return;
        }
        if (this.player.getLogger() != null) {
            this.player.getLogger().info("Get achievement data: " + message);
        }
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        try {
            final String name = tokens.nextToken().trim();
            final PlayerInfo player = PlayerInfoFactory.getPlayerInfoWithName(name);
            if (player == null) {
                this.sendNormalServerMessage("No player named \"" + name + "\" found online or offline on this server. Check your spelling. If the spelling is correct, have they been on this server before?");
                return;
            }
            final Achievement[] achs = Achievements.getAchievements(player.wurmId);
            final int target = Integer.parseInt(tokens.nextToken());
            for (final Achievement a : achs) {
                if (a.getAchievement() == target) {
                    this.sendNormalServerMessage(name + " has " + a.getCounter() + " of the achievement " + a.getTemplate().getName() + "(" + target + ")");
                    return;
                }
            }
            this.sendNormalServerMessage(name + " seemingly has none of the achievement with the number " + target);
        }
        catch (Exception ex) {
            this.sendNormalServerMessage("Something went wrong with the input.");
            this.sendNormalServerMessage(ex.toString());
        }
    }
    
    private void handleHashMessageCreaturePos(final int power) {
        if (power >= 3) {
            CreaturePos.logCreaturePos = !CreaturePos.logCreaturePos;
            this.sendNormalServerMessage("Creature pos logging enabled=" + CreaturePos.logCreaturePos);
        }
    }
    
    private void handleHashMessageBoats(final int power) {
        if (power >= 3) {
            Communicator.acceptsBoatTransfers = !Communicator.acceptsBoatTransfers;
            this.sendNormalServerMessage("Boat transfers enabled=" + Communicator.acceptsBoatTransfers);
        }
    }
    
    private void handleHashMessageLocate(final String message, final int power) {
        if (power >= 5) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            try {
                final String name = tokens.nextToken().trim();
                this.player.getLogger().log(Level.INFO, "Trying to locate " + name + '.');
                final Creature[] crets = Creatures.getInstance().getCreaturesWithName(name);
                for (int x = 0; x < crets.length && x < 100; ++x) {
                    this.sendNormalServerMessage(crets[x].getName() + ':' + crets[x].getCurrentTile().tilex + ", " + crets[x].getCurrentTile().tiley);
                }
            }
            catch (Exception ex) {}
        }
    }
    
    private void handleHashMessageLocateHorses(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            try {
                final String name = tokens.nextToken().trim();
                if (name.length() < 5 || name.equalsIgnoreCase("horse")) {
                    this.sendNormalServerMessage("Name must be longer than 5 and not be 'horse'!");
                }
                else {
                    this.player.getLogger().log(Level.INFO, "Trying to locate horse " + name + '.');
                    final Creature[] crets = Creatures.getInstance().getHorsesWithName(name);
                    if (crets.length == 0) {
                        this.sendNormalServerMessage("No horse found with name containing '" + name + "'");
                    }
                    else {
                        for (int x = 0; x < crets.length && x < 100; ++x) {
                            this.sendNormalServerMessage(crets[x].getName() + ':' + crets[x].getCurrentTile().tilex + ", " + crets[x].getCurrentTile().tiley + ", " + crets[x].getCurrentTile().isOnSurface());
                        }
                        this.sendNormalServerMessage("--- End of list ---");
                    }
                }
            }
            catch (Exception ex) {}
        }
    }
    
    private void handleHashMessageLocateAvatars(final String message, final int power) {
        this.player.getLogger().log(Level.INFO, "Trying to locate all the avatars on teh current server.");
        if (power >= 2) {
            final Creature[] avatars = Creatures.getInstance().getCreaturesWithName("avatar");
            if (avatars.length == 0) {
                this.sendNormalServerMessage("No avatars were found on this server");
                return;
            }
            for (final Creature avatar : avatars) {
                this.sendNormalServerMessage(StringUtil.format("Name:%s, ID:%d, x:%d, y:%d", avatar.getName(), avatar.getWurmId(), avatar.getCurrentTile().tilex, avatar.getCurrentTile().tiley));
            }
        }
    }
    
    private void handleHashMessageShowBans(final int power) {
        if (power >= 2) {
            Ban[] bips = Players.getInstance().getBans();
            if (bips.length > 0) {
                this.sendNormalServerMessage("IPS BANNED LOCALLY:");
                for (int x = 0; x < bips.length; ++x) {
                    final long daytime = bips[x].getExpiry() - System.currentTimeMillis();
                    this.sendNormalServerMessage(bips[x].getIdentifier() + ", " + Server.getTimeFor(daytime) + ", " + bips[x].getReason());
                }
            }
            else {
                this.sendNormalServerMessage("NO IPS BANNED LOCALLY.");
            }
            try {
                final LoginServerWebConnection c = new LoginServerWebConnection();
                bips = c.getIpsBanned();
                if (bips.length > 0) {
                    this.sendNormalServerMessage("IPS BANNED ON LOGINSERVER:");
                    for (int x2 = 0; x2 < bips.length; ++x2) {
                        final long daytime2 = bips[x2].getExpiry() - System.currentTimeMillis();
                        this.sendNormalServerMessage(bips[x2].getIdentifier() + ", " + Server.getTimeFor(daytime2) + ", " + bips[x2].getReason());
                    }
                }
                else {
                    this.sendNormalServerMessage("NO IPS BANNED ON LOGIN SERVER.");
                }
            }
            catch (Exception e) {
                this.sendAlertServerMessage("Failed to get banned ips from login server:" + e.getMessage());
                logInfo(this.player.getName() + " retrieving banned ips from login server failed: " + e.getMessage(), e);
            }
            bips = Players.getInstance().getPlayersBanned();
            if (bips.length > 0) {
                this.sendNormalServerMessage("PLAYERS BANNED LOCALLY:");
                for (int x = 0; x < bips.length; ++x) {
                    final long daytime = bips[x].getExpiry() - System.currentTimeMillis();
                    this.sendNormalServerMessage(bips[x].getIdentifier() + ", " + Server.getTimeFor(daytime) + ", " + bips[x].getReason());
                }
            }
            else {
                this.sendNormalServerMessage("NO PLAYERS BANNED LOCALLY.");
            }
            try {
                final LoginServerWebConnection c = new LoginServerWebConnection();
                bips = c.getPlayersBanned();
                if (bips.length > 0) {
                    this.sendNormalServerMessage("PLAYERS BANNED ON LOGINSERVER:");
                    for (int x2 = 0; x2 < bips.length; ++x2) {
                        final long daytime2 = bips[x2].getExpiry() - System.currentTimeMillis();
                        this.sendNormalServerMessage(bips[x2].getIdentifier() + ", " + Server.getTimeFor(daytime2) + ", " + bips[x2].getReason());
                    }
                }
                else {
                    this.sendNormalServerMessage("NO PLAYERS BANNED ON LOGIN SERVER.");
                }
            }
            catch (Exception e) {
                this.sendAlertServerMessage("Failed to get banned players from login server:" + e.getMessage());
                logInfo(this.player.getName() + " retrieving banned players from login server failed: " + e.getMessage(), e);
            }
        }
    }
    
    private void handleHashMessageCheckEigc(final int power) {
        if (power >= 2) {
            this.sendSafeServerMessage("The following eigc clients exist:");
            Eigc.sendAllClientInfo(this);
        }
    }
    
    private void handleHashMessageInvisible(final int power) {
        if (power >= 1) {
            if (this.player.getStatus().visible) {
                this.player.setVisible(false);
                this.sendSafeServerMessage("You are now invisible. Only gms can see you. Some actions and emotes may still be visible though.");
            }
            else {
                this.player.setVisible(true);
                this.sendSafeServerMessage("You are now visible again.");
            }
        }
    }
    
    private void handleHashMessagePardon(final String message, final int power) throws IOException {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String bname = "";
            try {
                bname = tokens.nextToken().trim();
                bname = LoginHandler.raiseFirstLetter(bname);
                logInfo("Trying to pardon player " + bname + '.');
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            PlayerInfo pinf = null;
            try {
                final Player p = Players.getInstance().getPlayer(bname);
                pinf = p.getSaveFile();
            }
            catch (NoSuchPlayerException nsp) {
                pinf = PlayerInfoFactory.createPlayerInfo(bname);
                pinf.load();
            }
            if (pinf.isBanned()) {
                pinf.setBanned(false, "", 0L);
                final String bip = pinf.getIpaddress();
                Players.getInstance().removeBan(bip);
                this.sendSafeServerMessage("You have gratiously pardoned " + bname + " and the ipaddress " + bip);
                logInfo(this.player.getName() + " pardons player " + bname + " and ipaddress " + bip + '.');
                if (this.player.getLogger() != null) {
                    this.player.getLogger().log(Level.INFO, this.player.getName() + " pardons " + bname);
                }
                if (!message.startsWith("#pardonhere")) {
                    try {
                        final LoginServerWebConnection c = new LoginServerWebConnection();
                        this.sendSafeServerMessage(c.removeBannedIp(bip));
                    }
                    catch (Exception e) {
                        this.sendAlertServerMessage("Failed to remove ip ban on login server:" + e.getMessage());
                        logInfo(this.player.getName() + " removing ip ban " + bip + " on login server failed: " + e.getMessage(), e);
                    }
                    try {
                        final LoginServerWebConnection c = new LoginServerWebConnection();
                        this.sendSafeServerMessage(c.pardonban(bname));
                    }
                    catch (Exception e) {
                        this.sendAlertServerMessage("Failed to pardon on login server:" + e.getMessage());
                        logInfo(this.player.getName() + " pardoning " + bname + "on login server failed: " + e.getMessage(), e);
                    }
                }
            }
        }
    }
    
    private void handleHashMessagePardonSteamId(final String message, final int power) {
        if (power < 2) {
            return;
        }
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        final SteamId identifier = tokens.hasMoreTokens() ? SteamId.fromAnyString(tokens.nextToken()) : null;
        if (identifier == null) {
            this.sendAlertServerMessage("Usage: #pardonsteamid <steamid>");
            return;
        }
        Ban ban = Players.getInstance().getBannedIp(identifier.toString());
        if (ban == null) {
            ban = new SteamIdBan(identifier, "", 0L);
        }
        if (Players.getInstance().removeBan(ban)) {
            this.sendSafeServerMessage("You have pardoned " + identifier);
        }
        else {
            this.sendAlertServerMessage("That id is either not banned or an error occurred. Check logs.");
        }
    }
    
    private void handleHashMessagePardonIp(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String ip = "";
            try {
                ip = tokens.nextToken().trim();
                logInfo("Trying to pardon ip " + ip + '.');
            }
            catch (Exception ex) {}
            Ban bip = Players.getInstance().getBannedIp(ip);
            if (bip != null) {
                if (Players.getInstance().removeBan(ip)) {
                    this.sendSafeServerMessage("You have gratiously pardoned the ipaddress " + ip);
                    logInfo(this.player.getName() + " pardons ipaddress " + ip + '.');
                    if (this.player.getLogger() != null) {
                        this.player.getLogger().log(Level.INFO, this.player.getName() + " pardons " + ip);
                    }
                }
                else {
                    this.sendAlertServerMessage("Failed to unban ip " + ip + '.');
                }
                if (!message.startsWith("#pardoniphere")) {
                    try {
                        final LoginServerWebConnection c = new LoginServerWebConnection();
                        this.sendSafeServerMessage(c.removeBannedIp(ip));
                    }
                    catch (Exception e) {
                        this.sendAlertServerMessage("Failed to remove ip ban on login server:" + e.getMessage());
                        logInfo(this.player.getName() + " removing ip ban " + bip + " on login server failed: " + e.getMessage(), e);
                    }
                }
            }
            else {
                bip = Players.getInstance().getBannedIp("/" + ip);
                if (bip != null) {
                    if (Players.getInstance().removeBan("/" + ip)) {
                        this.sendSafeServerMessage("You have gratiously pardoned the ipaddress " + ip);
                        logInfo(this.player.getName() + " pardons ipaddress " + ip + '.');
                        if (this.player.getLogger() != null) {
                            this.player.getLogger().log(Level.INFO, this.player.getName() + " pardons " + ip);
                        }
                    }
                    else {
                        this.sendAlertServerMessage("Failed to unban ip /" + ip + '.');
                    }
                }
                if (!message.startsWith("#pardoniphere")) {
                    try {
                        final LoginServerWebConnection c = new LoginServerWebConnection();
                        this.sendSafeServerMessage(c.removeBannedIp("/" + ip));
                    }
                    catch (Exception e) {
                        this.sendAlertServerMessage("Failed to remove ip ban on login server:" + e.getMessage());
                        logInfo(this.player.getName() + " removing ip ban " + bip + " on login server failed: " + e.getMessage(), e);
                    }
                }
            }
        }
    }
    
    private void handleHashMessageGetWarnings(final String message, final int power) throws IOException {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String bname = "";
            try {
                bname = tokens.nextToken().trim();
                bname = LoginHandler.raiseFirstLetter(bname);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            PlayerInfo pinf = null;
            Player play = null;
            try {
                play = Players.getInstance().getPlayer(bname);
                pinf = play.getSaveFile();
            }
            catch (NoSuchPlayerException nsp) {
                pinf = PlayerInfoFactory.createPlayerInfo(bname);
                pinf.load();
            }
            final long lastWarned = pinf.getLastWarned();
            final String wst = pinf.getWarningStats(lastWarned);
            this.sendSafeServerMessage(wst);
        }
    }
    
    private void handleHashMessageWarn(final String message, final int power) throws IOException {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String bname = "";
            try {
                bname = tokens.nextToken().trim();
                bname = LoginHandler.raiseFirstLetter(bname);
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " warning " + bname + " " + ex.getMessage());
            }
            PlayerInfo pinf = null;
            Player play = null;
            try {
                play = Players.getInstance().getPlayer(bname);
                pinf = play.getSaveFile();
            }
            catch (NoSuchPlayerException nsp) {
                pinf = PlayerInfoFactory.createPlayerInfo(bname);
                pinf.load();
            }
            final long lastWarned = pinf.getLastWarned();
            try {
                pinf.warn();
                final String wst = pinf.getWarningStats(lastWarned);
                this.sendSafeServerMessage("You have officially warned " + bname + ". " + wst);
                if (play != null) {
                    play.getCommunicator().sendAlertServerMessage("You have just received an official warning. Too many of these will get you banned from the game.", (byte)1);
                }
                if (this.player.getLogger() != null) {
                    this.player.getLogger().log(Level.INFO, this.player.getName() + " warns " + bname + '.');
                }
                final WcGlobalModeration wcgm = new WcGlobalModeration(WurmId.getNextWCCommandId(), this.player.getName(), (byte)this.player.getPower(), false, false, false, false, true, 0, 0, pinf.getName(), "You have just received an official warning. Too many of these will get you banned from the game.");
                if (Servers.localServer.LOGINSERVER) {
                    wcgm.sendFromLoginServer();
                }
                else {
                    wcgm.sendToLoginServer();
                }
            }
            catch (IOException iox) {
                logInfo(this.player.getName() + " fails to warn " + bname + '.', iox);
                this.sendSafeServerMessage("A server error occured. The warning was probably not registered.");
            }
        }
    }
    
    private void handleHashMessageClientInfo(final String message, final int power) throws IOException {
        if (power >= 1) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String bname = "";
            try {
                bname = tokens.nextToken().trim();
                bname = LoginHandler.raiseFirstLetter(bname);
            }
            catch (Exception ex) {
                this.sendSafeServerMessage("Usage: #showclientinfo [player]");
                return;
            }
            Player play = null;
            try {
                play = Players.getInstance().getPlayer(bname);
            }
            catch (NoSuchPlayerException nsp) {
                logInfo(this.player.getName() + " failed to check client info of " + bname);
                this.sendSafeServerMessage("Player " + bname + " not found.");
                return;
            }
            this.sendSafeServerMessage("Player: " + bname);
            this.sendSafeServerMessage("Client version: " + play.getClientVersion());
            this.sendSafeServerMessage("Client system: " + play.getClientSystem());
        }
    }
    
    private void handleHashMessageStaffClientInfo(final int power) throws IOException {
        if (power >= 4) {
            for (final Player p : Players.getInstance().getPlayers()) {
                if (p.getPower() > 0 || p.mayMute() || p.isPlayerAssistant()) {
                    this.sendSafeServerMessage("Player: " + p.getName() + ", Client Version: " + p.getClientVersion());
                }
            }
        }
    }
    
    private void handleHashMessageCreatePortals(final int power) {
        if (power >= 5) {
            this.createFreedomPortals();
        }
    }
    
    private void handleHashMessageTradeCheat(final int power) {
        if (power >= 2) {
            if (this.player.getLogger() != null) {
                this.player.getLogger().info("Toggling Trade Cheat");
            }
            Server.allowTradeCheat = !Server.allowTradeCheat;
            this.sendNormalServerMessage("Server allows tradecheats=" + Server.allowTradeCheat);
        }
    }
    
    private void handleHashMessageTriggerAchievement(final int power, final String message) {
        if (power >= 5) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            try {
                String targetName = tokens.nextToken().trim();
                final int achievementNum = Integer.parseInt(tokens.nextToken());
                targetName = LoginHandler.raiseFirstLetter(targetName);
                final Player p = Players.getInstance().getPlayerOrNull(targetName);
                if (p != null) {
                    if (this.player.getLogger() != null) {
                        this.player.getLogger().info("Triggering achievement " + achievementNum + " for player " + p.getName());
                    }
                    Achievements.triggerAchievement(p.getWurmId(), achievementNum);
                    this.sendSafeServerMessage("Achievement " + achievementNum + " successfully triggered for player + " + p.getName() + ".");
                }
                else {
                    this.sendSafeServerMessage("Unable to find player " + targetName + ".");
                }
            }
            catch (Exception ex) {
                this.sendSafeServerMessage("Usage: #triggerAchievement [player] [achievementNum]");
            }
        }
    }
    
    private void handleHashMessageOnline(final int power, final String message) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            try {
                final String targetName = tokens.nextToken().trim();
                final PlayerState ps = PlayerInfoFactory.getPlayerState(targetName);
                if (ps == null) {
                    this.sendNormalServerMessage("Unable to find player " + targetName + ".");
                }
                else if (ps.getState() == PlayerOnlineStatus.ONLINE) {
                    this.sendNormalServerMessage(targetName + " is online. Current server is " + ps.getServerName() + ".");
                }
                else {
                    final long totalTime = System.currentTimeMillis() - ps.getLastLogout();
                    final long second = totalTime / 1000L % 60L;
                    final long minute = totalTime / 60000L % 60L;
                    final long hour = totalTime / 3600000L % 24L;
                    final long day = totalTime / 86400000L;
                    final String time = String.format("%dd %02d:%02d:%02d", day, hour, minute, second);
                    this.sendNormalServerMessage(targetName + " is offline. Last known server is " + ps.getServerName() + ". Last logout time is " + time + " ago.");
                }
            }
            catch (Exception ex) {
                this.sendSafeServerMessage("Usage: #online [playerName]");
            }
        }
    }
    
    private void handleHashMessageMeditation(final int power) {
        if (power >= 5) {
            int found = 0;
            for (int nums = 0; nums < 1000; ++nums) {
                try {
                    final int tx = Zones.safeTileX(Server.rand.nextInt(this.player.getCurrentTile().tilex - 100 + Server.rand.nextInt(200)));
                    final int ty = Zones.safeTileY(Server.rand.nextInt(this.player.getCurrentTile().tiley - 100 + Server.rand.nextInt(200)));
                    final float height = Zones.calculateHeight((tx << 2) + 2, (ty << 2) + 2, true) * 10.0f;
                    final byte path = Cults.getPathFor(tx, ty, 0, (int)height);
                    if (path != 0) {
                        this.sendNormalServerMessage(found++ + " - Tile " + tx + "," + ty + ":" + Cults.getPathNameFor(path));
                    }
                }
                catch (NoSuchZoneException ex) {}
            }
        }
    }
    
    private void handleHashMessageHighscore(final String message, final int power) {
        if (power >= 3) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String hstype = "";
            try {
                hstype = tokens.nextToken();
                ChallengeSummary.createHighScorePage(Integer.parseInt(hstype));
            }
            catch (Exception ex) {
                this.sendNormalServerMessage(message + " does not compute! " + ex.getMessage());
            }
        }
    }
    
    private void handleHashMessageWarTargets(final int power) {
        if (power >= 5) {
            for (final Item i : Items.getWarTargets()) {
                this.sendNormalServerMessage(i.getName() + " at " + i.getTileX() + "," + i.getTileY());
            }
        }
    }
    
    private void handleHashMessageAlerts(final int power) {
        if (power >= 2) {
            final AlertServerMessageQuestion asm = new AlertServerMessageQuestion(this.player, "Changing alert messages", "What should the server alerts be?", -10L);
            asm.sendQuestion();
        }
    }
    
    private void handleHashMessageCalcCreatures(final int power) {
        if (power >= 2) {
            Zones.calcCreatures(this.player);
        }
    }
    
    private void handleHashMessageOffline(final int power) {
        if (power >= 2) {
            Creatures.getInstance().sendOfflineCreatures(this, power >= 2);
        }
    }
    
    private void handleHashMessageGetIps(final int power) {
        if (power >= 2) {
            Players.getInstance().sendIpsToPlayer(this.player);
        }
    }
    
    private void handleHashMessageWho(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String kname = "";
            try {
                kname = tokens.nextToken().toLowerCase();
                if (kname.equals("j")) {
                    this.sendWho((byte)1);
                }
                else if (kname.equals("h")) {
                    this.sendWho((byte)3);
                }
                else if (kname.equals("m")) {
                    this.sendWho((byte)2);
                }
            }
            catch (Exception ex) {
                this.sendNormalServerMessage("Use '#who J' to retrieve players online from Jenn-Kellon, H for HOTS, M for Mol-Rehan.");
            }
        }
    }
    
    private void handleHashMessageToggleGlobalChat(final int power) {
        if (power >= 2) {
            if (Servers.localServer.isChallengeServer()) {
                this.sendNormalServerMessage("You can not enable global chat on this server.");
                return;
            }
            if (Communicator.gchatdisabled) {
                Communicator.gchatdisabled = false;
                this.sendNormalServerMessage("Global chat is enabled.");
                Players.addGmMessage(this.player.getName(), "Enabled global chat");
            }
            else {
                Communicator.gchatdisabled = true;
                this.sendNormalServerMessage("Global chat is disabled.");
                Players.addGmMessage(this.player.getName(), "Disabled global chat");
            }
        }
    }
    
    private void handleHashMessageSoundSpam(final int power) {
        if (power >= 2) {
            for (int t = Server.rand.nextInt(20), a = 0; a < t; ++a) {
                final int x = Server.rand.nextInt(100);
                final int y = Server.rand.nextInt(100);
                String soundName = "sound.ambient.day.crickets";
                final int n = Server.rand.nextInt(20);
                if (n == 1) {
                    soundName = "sound.ambient.night.crickets";
                }
                else if (n == 2) {
                    soundName = "sound.ambient.night.crickets";
                }
                else if (n == 3) {
                    soundName = "sound.forest.creak.loud";
                }
                else if (n == 4) {
                    soundName = "sound.forest.leafrustle";
                }
                else if (n == 5) {
                    soundName = "sound.ambient.rain.heavy";
                }
                else if (n == 6) {
                    soundName = "sound.arrow.aim";
                }
                else if (n == 7) {
                    soundName = "sound.arrow.shot";
                }
                else if (n == 8) {
                    soundName = "sound.arrow.stuck.ground";
                }
                else if (n == 9) {
                    soundName = "sound.arrow.miss";
                }
                else if (n == 10) {
                    soundName = "sound.birdsong.bird5";
                }
                else if (n == 11) {
                    soundName = "sound.birdsong.bird1";
                }
                else if (n == 12) {
                    soundName = "sound.birdsong.bird7";
                }
                else if (n == 13) {
                    soundName = "sound.birdsong.bird8";
                }
                else if (n == 14) {
                    soundName = "sound.work.carpentry.carvingknife";
                }
                else if (n == 15) {
                    soundName = "sound.birdsong.crows";
                }
                else if (n == 16) {
                    soundName = "sound.work.carpentry.rasp";
                }
                else if (n == 17) {
                    soundName = "sound.death.bear";
                }
                else if (n == 18) {
                    soundName = "sound.death.dragon";
                }
                else if (n == 19) {
                    soundName = "sound.work.smithing.metal";
                }
                SoundPlayer.playSound(soundName, this.player.getCurrentTile().tilex - 50 + x, this.player.getCurrentTile().tiley - 50 + y, true, this.player.getPositionZ() + this.player.getAltOffZ());
            }
        }
    }
    
    private void showCreatureList() {
        final Map<Integer, Integer> crets = Creatures.getInstance().getCreatureTypeList();
        for (final Integer key : crets.keySet()) {
            try {
                final CreatureTemplate template = CreatureTemplateFactory.getInstance().getTemplate(key);
                final int maxCreatures = Servers.localServer.maxCreatures;
                final String messageStr = "Creature: %s Count: %d Current %%: %.2f Max %%: %.2f%s";
                final int currentCount = crets.get(key);
                final float currentPercentage = currentCount / maxCreatures * 100.0f;
                final float maxPercentage = template.getMaxPercentOfCreatures() * 100.0f;
                final int popCap = template.getMaxPopulationOfCreatures();
                final String popMessage = template.usesMaxPopulation() ? StringUtil.format(" Population limit: %d.", popCap) : ".";
                final String newMessage = StringUtil.format("Creature: %s Count: %d Current %%: %.2f Max %%: %.2f%s", template.getName(), currentCount, currentPercentage, maxPercentage, popMessage);
                this.sendNormalServerMessage(newMessage);
            }
            catch (NoSuchCreatureTemplateException e) {
                logWarn(e.getMessage(), e);
            }
        }
    }
    
    private void handleShowCreatureList(final String message, final int power) {
        if (power < 4) {
            return;
        }
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        if (!tokens.hasMoreTokens()) {
            this.showCreatureList();
        }
        else {
            final String token = tokens.nextToken();
            if (token.equals("corrupt")) {
                final Creature[] creatures = Creatures.getInstance().getCreatures();
                final Map<Integer, Integer> corruptedByType = new HashMap<Integer, Integer>();
                for (final Creature cret : creatures) {
                    if (cret.hasTrait(22)) {
                        final Integer key = cret.getTemplate().getTemplateId();
                        if (corruptedByType.containsKey(key)) {
                            final Integer val = corruptedByType.get(key);
                            final Integer nVal = val + 1;
                            corruptedByType.put(key, nVal);
                        }
                        else {
                            corruptedByType.put(key, 1);
                        }
                    }
                }
                if (corruptedByType.isEmpty()) {
                    this.sendNormalServerMessage("No corrupt animals on the server.");
                    return;
                }
                for (final Integer key2 : corruptedByType.keySet()) {
                    try {
                        final CreatureTemplate template = CreatureTemplateFactory.getInstance().getTemplate(key2);
                        final String messageStr = "Creature: %s Corrupted count: %d";
                        final Integer val2 = corruptedByType.get(key2);
                        this.sendNormalServerMessage(StringUtil.format("Creature: %s Corrupted count: %d", template.getName(), val2));
                    }
                    catch (NoSuchCreatureTemplateException e) {
                        logWarn(e.getMessage(), e);
                    }
                }
            }
        }
    }
    
    private void handleHashMessageKick(final String message, final int power) {
        if (power >= 2) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String pname = "Unknown";
            try {
                pname = tokens.nextToken().trim();
                pname = LoginHandler.raiseFirstLetter(pname);
                final Player p = Players.getInstance().getPlayer(pname);
                if (this.player.getLogger() != null) {
                    this.player.getLogger().log(Level.INFO, this.player.getName() + " kicking " + pname);
                }
                if (p.getPower() < power || p == this.player) {
                    if (p.hasLink()) {
                        p.getCommunicator().sendShutDown("Disconnected by GM.", true);
                        p.setSecondsToLogout(5);
                    }
                    else {
                        Players.getInstance().logoutPlayer(p);
                    }
                }
                else {
                    this.sendNormalServerMessage("You cannot kick " + pname + '!');
                    p.getCommunicator().sendNormalServerMessage(this.player.getName() + " tried to kick you from the game!");
                }
            }
            catch (NoSuchPlayerException nsp) {
                this.sendSafeServerMessage("No player found with the name " + pname);
            }
        }
    }
    
    private void handleHashMessageAllowAll(final int power) {
        if (power >= 3) {
            Server.getInstance().broadCastAlert("The server is now open for connections.");
            Constants.maintaining = false;
        }
    }
    
    private void handleHashMessageShutdown(final int power) {
        if (power >= 3) {
            Server.getInstance().broadCastAlert("The server is put in maintenance mode. Please log off immediately.");
            Constants.maintaining = true;
        }
    }
    
    private void handleHashMessageDumpXml(final int power) {
        if (power < 3) {
            return;
        }
        if (Servers.localServer.LOGINSERVER) {
            EpicXmlWriter.dumpEntities(Server.getEpicMap());
            this.player.getCommunicator().sendNormalServerMessage("Xml dumped.");
        }
        else {
            this.player.getCommunicator().sendNormalServerMessage("Do this on the login server...");
        }
    }
    
    private void handleHashMessageElevate(final int power, final String message) {
        if (power <= 3) {
            return;
        }
        if (Servers.localServer.LOGINSERVER) {
            final StringTokenizer tokenizer = new StringTokenizer(message);
            tokenizer.nextToken();
            if (!tokenizer.hasMoreTokens()) {
                this.sendSafeServerMessage("The name of the deity to elevate must be included.");
                return;
            }
            final String name = tokenizer.nextToken();
            if (HexMap.VALREI.elevateDemigod(1L, name)) {
                this.sendSafeServerMessage("Done!");
            }
            else {
                this.sendSafeServerMessage("Failed..");
            }
        }
        else {
            this.player.getCommunicator().sendNormalServerMessage("Do this on the login server...");
        }
    }
    
    private void handleHashMessageNeMission(final int power, final String message) {
        if (power < 3) {
            return;
        }
        final StringTokenizer tokenizer = new StringTokenizer(message);
        tokenizer.nextToken();
        if (!tokenizer.hasMoreTokens()) {
            this.sendSafeServerMessage("The name of the deity to generate a mission for must be included.");
            return;
        }
        final String rawEntityName = tokenizer.nextToken();
        int number;
        String entityName;
        try {
            number = Integer.parseInt(rawEntityName);
            entityName = Deities.getDeityName(number);
        }
        catch (Exception e) {
            entityName = rawEntityName.substring(0, 1).toUpperCase() + rawEntityName.substring(1).toLowerCase();
            number = Deities.getEntityNumber(entityName);
        }
        boolean found = false;
        if (number > 0) {
            final EpicServerStatus es = new EpicServerStatus();
            int time = 259200;
            if (!Servers.localServer.EPIC) {
                time = 604800;
            }
            if (EpicServerStatus.getCurrentScenario() == null) {
                EpicServerStatus.loadLocalEntries();
            }
            if (EpicServerStatus.getCurrentScenario() != null) {
                es.generateNewMissionForEpicEntity(number, entityName, -1, time, EpicServerStatus.getCurrentScenario().getScenarioName(), EpicServerStatus.getCurrentScenario().getScenarioNumber(), EpicServerStatus.getCurrentScenario().getScenarioQuest(), true);
            }
            else {
                this.player.getCommunicator().sendNormalServerMessage("Failed to find and use the current scenario. Making something up...");
                es.generateNewMissionForEpicEntity(number, entityName, 0, 259200, "The secret scenario", 1, "The secret quest", true);
            }
            found = true;
        }
        if (!found) {
            this.player.getCommunicator().sendNormalServerMessage("Failed to locate deity called " + rawEntityName + "...");
        }
        else {
            this.player.getCommunicator().sendSafeServerMessage("Generated new mission for " + entityName + ".");
        }
    }
    
    private void handleHashMessageToggleEnableMission(final int power, final String message) {
        if (power < 3) {
            return;
        }
        final StringTokenizer tokenizer = new StringTokenizer(message);
        tokenizer.nextToken();
        if (!tokenizer.hasMoreTokens()) {
            this.sendSafeServerMessage("The name of the mission to toggle on/off must be included.");
            return;
        }
        final String missionName = tokenizer.nextToken();
        boolean found = false;
        for (final Mission mission : Missions.getAllMissions()) {
            if (mission.getName().equalsIgnoreCase(missionName)) {
                mission.setInactive(!mission.isInactive());
                this.player.getCommunicator().sendSafeServerMessage("Mission " + missionName + " active=" + !mission.isInactive());
                found = true;
                break;
            }
        }
        if (!found) {
            this.player.getCommunicator().sendNormalServerMessage("Failed to locate mission with that name...");
        }
    }
    
    private void handleHashMessageToggleFlag(final int power, final String message) {
        if (power < 3) {
            return;
        }
        if (!this.player.hasLink()) {
            return;
        }
        if (!Servers.localServer.LOGINSERVER) {
            this.sendSafeServerMessage("This command needs to be used on the login server.");
            return;
        }
        final StringTokenizer tokenizer = new StringTokenizer(message);
        tokenizer.nextToken();
        if (!tokenizer.hasMoreTokens()) {
            this.sendSafeServerMessage("The name of the player to toggle flag for for must be included.");
            return;
        }
        final String playerName = tokenizer.nextToken();
        if (!tokenizer.hasMoreTokens()) {
            this.sendSafeServerMessage("The flag number to toggle must be included.");
            return;
        }
        final String flagString = tokenizer.nextToken();
        int flagNumber = -1;
        try {
            flagNumber = Integer.parseInt(flagString);
        }
        catch (Exception ex) {
            this.sendSafeServerMessage("The flag number " + flagString + " doesn't seem to be a number.");
            return;
        }
        if ((flagNumber >= 64 && flagNumber > 82) || (flagNumber < 64 && flagNumber > 63) || flagNumber < 0) {
            this.sendSafeServerMessage("The flag number " + flagString + " is not a valid flag.");
            return;
        }
        final PlayerInfo target = PlayerInfoFactory.getPlayerInfoWithName(playerName);
        if (target != null) {
            target.setFlag(flagNumber, !target.isFlagSet(flagNumber));
            final String toggleMessage = StringUtil.format("Toggled local flag: %s for: %s.", Boolean.toString(target.isFlagSet(flagNumber)), playerName);
            this.sendSafeServerMessage(toggleMessage);
            if (Servers.localServer.id != target.getCurrentServer()) {
                final LoginServerWebConnection lsw = new LoginServerWebConnection(target.getCurrentServer());
                if (lsw.setPlayerFlag(target.wurmId, flagNumber, target.isFlagSet(flagNumber))) {
                    this.sendSafeServerMessage("Also set the flag on server " + target.getCurrentServer() + " to " + target.isFlagSet(flagNumber));
                }
                else {
                    this.sendSafeServerMessage("Failed to set the flag on server " + target.getCurrentServer());
                }
            }
        }
    }
    
    private void handleHashMessageToggleCCFP(final int power, final String message) {
        if (!this.player.hasLink()) {
            return;
        }
        this.player.setFlag(52, !this.player.hasFlag(52));
        this.sendSafeServerMessage("Visibility of the CCFP Bar switched " + (this.player.hasFlag(52) ? "off." : "on."));
        this.player.getStatus().sendHunger();
    }
    
    private void handleHashMessageFlattenRock(final int power, final String message) {
        if (power < 3) {
            return;
        }
        if (!this.player.hasLink()) {
            return;
        }
        final StringTokenizer tokenizer = new StringTokenizer(message);
        tokenizer.nextToken();
        if (!tokenizer.hasMoreTokens()) {
            this.sendSafeServerMessage("You must have at least the size of the area included. (MISSING N/E/S/W)");
            return;
        }
        int North;
        try {
            North = Integer.parseInt(tokenizer.nextToken());
            if (North < 0) {
                this.sendSafeServerMessage("The NORTH value may not be negative!");
                return;
            }
        }
        catch (NumberFormatException nfe) {
            this.sendSafeServerMessage("The NORTH value must be an integer!");
            return;
        }
        if (!tokenizer.hasMoreTokens()) {
            this.sendSafeServerMessage("You must have at least the size of the area included. (MISSING E/S/W)");
            return;
        }
        int East;
        try {
            East = Integer.parseInt(tokenizer.nextToken());
            if (East < 0) {
                this.sendSafeServerMessage("The EAST value may not be negative!");
                return;
            }
        }
        catch (NumberFormatException nfe) {
            this.sendSafeServerMessage("The EAST value must be an integer!");
            return;
        }
        if (!tokenizer.hasMoreTokens()) {
            this.sendSafeServerMessage("You must have at least the size of the area included. (MISSING S/W");
            return;
        }
        int South;
        try {
            South = Integer.parseInt(tokenizer.nextToken());
            if (South < 0) {
                this.sendSafeServerMessage("The SOUTH value may not be negative!");
                return;
            }
        }
        catch (NumberFormatException nfe) {
            this.sendSafeServerMessage("The SOUTH value must be an integer!");
            return;
        }
        if (!tokenizer.hasMoreTokens()) {
            this.sendSafeServerMessage("You must have at least the size of the area included. (MISSING W)");
            return;
        }
        int West;
        try {
            West = Integer.parseInt(tokenizer.nextToken());
            if (West < 0) {
                this.sendSafeServerMessage("The WEST value may not be negative!");
                return;
            }
        }
        catch (NumberFormatException nfe) {
            this.sendSafeServerMessage("The WEST value must be an integer!");
            return;
        }
        int extra;
        if (!tokenizer.hasMoreTokens()) {
            extra = 0;
        }
        else {
            try {
                extra = Integer.parseInt(tokenizer.nextToken());
            }
            catch (NumberFormatException nfe) {
                this.sendSafeServerMessage("The Extra slopes value must be an integer!");
                return;
            }
        }
        final float minDirt = 0.0f;
        final int startX = this.player.getTileX() - West;
        final int startY = this.player.getTileY() - North;
        final int endX = this.player.getTileX() + East;
        final int endY = this.player.getTileY() + South;
        Terraforming.flattenImmediately(this.player, startX, endX, startY, endY, minDirt, extra, true);
    }
    
    private void handleHashMessageFlattenDirt(final int power, final String message) {
        if (power < 3) {
            return;
        }
        if (!this.player.hasLink()) {
            return;
        }
        final StringTokenizer tokenizer = new StringTokenizer(message);
        tokenizer.nextToken();
        if (!tokenizer.hasMoreTokens()) {
            this.sendSafeServerMessage("You must have at least the size of the area included. (MISSING N/E/S/W)");
            return;
        }
        int North;
        try {
            North = Integer.parseInt(tokenizer.nextToken());
            if (North < 0) {
                this.sendSafeServerMessage("The NORTH value may not be negative!");
                return;
            }
        }
        catch (NumberFormatException nfe) {
            this.sendSafeServerMessage("The NORTH value must be an integer!");
            return;
        }
        if (!tokenizer.hasMoreTokens()) {
            this.sendSafeServerMessage("You must have at least the size of the area included. (MISSING E/S/W)");
            return;
        }
        int East;
        try {
            East = Integer.parseInt(tokenizer.nextToken());
            if (East < 0) {
                this.sendSafeServerMessage("The EAST value may not be negative!");
                return;
            }
        }
        catch (NumberFormatException nfe) {
            this.sendSafeServerMessage("The EAST value must be an integer!");
            return;
        }
        if (!tokenizer.hasMoreTokens()) {
            this.sendSafeServerMessage("You must have at least the size of the area included. (MISSING S/W");
            return;
        }
        int South;
        try {
            South = Integer.parseInt(tokenizer.nextToken());
            if (South < 0) {
                this.sendSafeServerMessage("The SOUTH value may not be negative!");
                return;
            }
        }
        catch (NumberFormatException nfe) {
            this.sendSafeServerMessage("The SOUTH value must be an integer!");
            return;
        }
        if (!tokenizer.hasMoreTokens()) {
            this.sendSafeServerMessage("You must have at least the size of the area included. (MISSING W)");
            return;
        }
        int West;
        try {
            West = Integer.parseInt(tokenizer.nextToken());
            if (West < 0) {
                this.sendSafeServerMessage("The WEST value may not be negative!");
                return;
            }
        }
        catch (NumberFormatException nfe) {
            this.sendSafeServerMessage("The WEST value must be an integer!");
            return;
        }
        int extra;
        if (!tokenizer.hasMoreTokens()) {
            extra = 0;
        }
        else {
            try {
                extra = Integer.parseInt(tokenizer.nextToken());
            }
            catch (NumberFormatException nfe) {
                this.sendSafeServerMessage("The Extra slopes value must be an integer!");
                return;
            }
        }
        float minDirt;
        if (!tokenizer.hasMoreTokens()) {
            minDirt = 1.0f;
        }
        else {
            try {
                final int temp = Integer.parseInt(tokenizer.nextToken());
                minDirt = temp / 10.0f;
            }
            catch (NumberFormatException nfe) {
                this.sendSafeServerMessage("The Minimum Dirt value must be an integer!");
                return;
            }
        }
        final int startX = this.player.getTileX() - West;
        final int startY = this.player.getTileY() - North;
        final int endX = this.player.getTileX() + East;
        final int endY = this.player.getTileY() + South;
        Terraforming.flattenImmediately(this.player, startX, endX, startY, endY, minDirt, extra, false);
    }
    
    private void handleHashMessageGenerateDeadVillage(final int power, final String message) {
        if (power < 5) {
            return;
        }
        if (this.player.hasLink()) {
            if (this.player.getLogger() != null) {
                this.player.getLogger().info(message);
            }
            final String[] splits = message.split(" ");
            int num = 1;
            if (splits.length > 1) {
                try {
                    num = Integer.parseInt(splits[1]);
                }
                catch (NumberFormatException nfe) {
                    this.sendSafeServerMessage("Usage: #generateDeadVillage [num]");
                    return;
                }
            }
            try {
                for (int i = 0; i < num; ++i) {
                    Villages.generateDeadVillage(this.player, num <= 5);
                }
            }
            catch (IOException e) {
                this.sendSafeServerMessage("Failed to generate dead village because of an error: " + e.getMessage());
                return;
            }
            this.sendSafeServerMessage("Alright, successfully completed generation for " + num + " dead villages.");
        }
    }
    
    private void handleHashMessagePayGms(final int power, final String message) {
        if (power < 3) {
            return;
        }
        if (!this.player.hasLink()) {
            return;
        }
        if (!Servers.localServer.LOGINSERVER) {
            this.sendSafeServerMessage("This command needs to be used on the login server.");
            return;
        }
        for (final PlayerInfo target : PlayerInfoFactory.getPlayerInfos()) {
            if (target.isFlagSet(48)) {
                final String payMessage = StringUtil.format("Paying 10s to %s.", target.getName());
                this.sendSafeServerMessage(payMessage);
                final LoginServerWebConnection lsw = new LoginServerWebConnection();
                if (lsw.addMoney(this.player, target.getName(), 100000L, "GMPay " + target.wurmId + " " + System.currentTimeMillis())) {
                    this.sendSafeServerMessage("Properly added.");
                }
            }
        }
    }
    
    private void handleHashMessageToggleQA(final int power, final String message) {
        if (power < 3) {
            return;
        }
        if (!this.player.hasLink()) {
            return;
        }
        final StringTokenizer tokenizer = new StringTokenizer(message);
        tokenizer.nextToken();
        if (!tokenizer.hasMoreTokens()) {
            this.sendSafeServerMessage("The name of the player to toggle QA for must be included.");
            return;
        }
        final String playerName = tokenizer.nextToken();
        try {
            final Player target = Players.getInstance().getPlayer(playerName);
            if (target.hasLink()) {
                final Logger pLogger = this.player.getLogger();
                if (pLogger == null) {
                    this.sendSafeServerMessage("Unable to log this event so aborting.");
                    return;
                }
                target.setFlag(26, !target.isQAAccount());
                final String toggleMessage = StringUtil.format("Toggled QA status: %s for: %s.", Boolean.toString(target.isQAAccount()), playerName);
                this.sendSafeServerMessage(toggleMessage);
                pLogger.log(Level.INFO, toggleMessage);
            }
            else {
                this.toggleQAInInfo(power, playerName);
            }
        }
        catch (NoSuchPlayerException nsp) {
            this.toggleQAInInfo(power, playerName);
        }
    }
    
    private void toggleQAInInfo(final int power, final String playerName) {
        if (power < 3) {
            return;
        }
        if (!this.player.hasLink()) {
            return;
        }
        final Logger pLogger = this.player.getLogger();
        if (pLogger == null) {
            this.sendSafeServerMessage("Unable to log this event so aborting.");
            return;
        }
        try {
            final PlayerInfo info = PlayerInfoFactory.getPlayerInfoWithName(playerName);
            if (info != null) {
                try {
                    info.load();
                }
                catch (IOException ie) {
                    final String warning = "Unable to load player data for %s.";
                    this.sendSafeServerMessage(StringUtil.format("Unable to load player data for %s.", playerName));
                    logWarn("Failed to load PlayerInfo when toggling QA flag.", ie);
                    return;
                }
                info.setFlag(26, !info.isQAAccount());
                info.save();
                final String toggleMessage = StringUtil.format("Toggled QA status: %s for: %s.", Boolean.toString(info.isQAAccount()), playerName);
                this.sendSafeServerMessage(toggleMessage);
                pLogger.log(Level.INFO, toggleMessage);
            }
            else {
                final String warning2 = "Unable to find player data for: %s.";
                this.sendSafeServerMessage(StringUtil.format("Unable to find player data for: %s.", playerName));
            }
        }
        catch (IOException ie2) {
            final String warning2 = "Failed to save flag for offline player %s.";
            this.sendSafeServerMessage(StringUtil.format("Failed to save flag for offline player %s.", playerName));
            logWarn("Failed to save PlayerInfo when toggling QA flag.", ie2);
        }
    }
    
    private void handleHashMessageMaxCreatures(final int power, final String message) {
        if (power < 3) {
            return;
        }
        if (!this.player.hasLink()) {
            return;
        }
        final StringTokenizer tokenizer = new StringTokenizer(message);
        tokenizer.nextToken();
        if (!tokenizer.hasMoreTokens()) {
            this.sendSafeServerMessage("The number of max creatures is " + Servers.localServer.maxCreatures + ". Current number of creatures is " + Creatures.getInstance().getNumberOfCreatures() + " (agg creatures:" + Creatures.getInstance().getNumberOfAgg() + ").");
            return;
        }
        final String newVal = tokenizer.nextToken();
        try {
            final int newcreatures = Integer.parseInt(newVal);
            Servers.localServer.maxCreatures = newcreatures;
            this.sendSafeServerMessage("The new number of max creatures is " + Servers.localServer.maxCreatures);
            Servers.localServer.saveNewGui(Servers.localServer.id);
        }
        catch (NumberFormatException nfe) {
            this.sendAlertServerMessage("The number of max creatures can not be " + newVal + ". Provide a valid whole number.");
        }
    }
    
    private void handleHashMessageChangeModel(final int power, final String message) {
        if (power < 1) {
            return;
        }
        final StringTokenizer tokenizer = new StringTokenizer(message);
        tokenizer.nextToken();
        if (!tokenizer.hasMoreTokens()) {
            if (power < 2) {
                this.sendSafeServerMessage("Usage: #changemodel <model>");
            }
            else {
                this.sendSafeServerMessage("Usage: #changemodel <model> (player) (duration)");
                this.sendSafeServerMessage("Specifying a player and duration puts an illusion on that player.");
            }
            this.sendSafeServerMessage("Use '#changemodel list' to see a list of models. This list is very long. Not all items have models.");
            return;
        }
        final String modelName = tokenizer.nextToken();
        if (modelName.equals("list")) {
            this.sendSafeServerMessage("Creatures:");
            StringBuilder buf = new StringBuilder();
            for (final CreatureTemplate t : CreatureTemplateFactory.getInstance().getTemplates()) {
                if (buf.length() > 120) {
                    this.sendSafeServerMessage(buf.toString().substring(0, buf.length() - 1));
                    buf = new StringBuilder();
                }
                buf.append(t.getName().replace(" ", "_")).append(" ");
            }
            this.sendSafeServerMessage(buf.toString());
            buf = new StringBuilder();
            this.sendSafeServerMessage("Items:");
            for (final ItemTemplate t2 : ItemTemplateFactory.getInstance().getTemplates()) {
                if (buf.length() > 120) {
                    this.sendNormalServerMessage(buf.toString());
                    buf = new StringBuilder();
                }
                buf.append(t2.getName().replace(" ", "_")).append(" ");
            }
            this.sendSafeServerMessage(buf.toString().substring(0, buf.length() - 1));
            return;
        }
        if (modelName.equals("gmdark") && power > 1) {
            this.player.setModelName("model.creature.gmdark");
            this.sendSafeServerMessage("This isn't even your final form, is it?");
            return;
        }
        if (modelName.equals("gmnormal") || modelName.equals("normal")) {
            this.player.setModelName("Human");
            this.sendSafeServerMessage("You return to normal.");
            return;
        }
        String model = modelName;
        if (!modelName.contains(".")) {
            model = CreatureTemplateFactory.getInstance().getModelNameOrNull(modelName.replace("_", " "));
            if (model == null) {
                model = ItemTemplateFactory.getInstance().getModelNameOrNull(modelName.replace("_", " "));
            }
            if (model == null) {
                this.sendSafeServerMessage("Model not found: " + modelName);
                return;
            }
        }
        if (power >= 2 && tokenizer.hasMoreTokens()) {
            if (Servers.isThisAPvpServer() && power < 4) {
                this.sendSafeServerMessage("You cannot use this command on PvP server.");
                return;
            }
            final String playerName = tokenizer.nextToken();
            final Player targetPlayer = Players.getInstance().getPlayerOrNull(playerName);
            if (targetPlayer == null) {
                this.sendSafeServerMessage("Player not found: " + playerName);
                return;
            }
            if (targetPlayer.getPower() >= this.player.getPower()) {
                targetPlayer.getCommunicator().sendAlertServerMessage(this.player.getName() + " is attempting to change your model to " + model, (byte)1);
            }
            final String timeString = tokenizer.nextToken();
            final int time = Integer.parseInt(timeString);
            if (time <= 0 || time > 3600) {
                this.sendSafeServerMessage("Duration must be between 1 and 3600 seconds.");
                return;
            }
            if (targetPlayer.getSpellEffects() != null) {
                targetPlayer.getSpellEffects().addSpellEffect(new SpellEffect(targetPlayer.getWurmId(), (byte)72, 100.0f, time, (byte)9, (byte)0, true));
                targetPlayer.setModelName(model);
                this.sendSafeServerMessage("You bestow an illusion upon " + targetPlayer.getName() + " for " + time + " seconds.");
                targetPlayer.getCommunicator().sendSafeServerMessage("You feel quite different.", (byte)2);
            }
        }
        else {
            this.player.setModelName(model);
        }
    }
    
    private void handleHashMessageGMLight(final int power, final String message) {
        if (power <= 1) {
            return;
        }
        this.player.toggleGMLight();
    }
    
    private void handleHashMessageIsQA(final int power, final String message) {
        if (power < 3) {
            return;
        }
        if (!this.player.hasLink()) {
            return;
        }
        final StringTokenizer tokenizer = new StringTokenizer(message);
        tokenizer.nextToken();
        if (!tokenizer.hasMoreTokens()) {
            this.sendSafeServerMessage("The name of the player to check QA status for must be included.");
            return;
        }
        final String playerName = tokenizer.nextToken();
        try {
            final Player target = Players.getInstance().getPlayer(playerName);
            final String returnMessage = "%s QA status: %s";
            this.sendSafeServerMessage(StringUtil.format("%s QA status: %s", playerName, Boolean.toString(target.isQAAccount())));
        }
        catch (NoSuchPlayerException nsp) {
            this.isQAInfo(power, playerName);
        }
    }
    
    private void isQAInfo(final int power, final String playerName) {
        if (power < 3) {
            return;
        }
        if (!this.player.hasLink()) {
            return;
        }
        final PlayerInfo info = PlayerInfoFactory.getPlayerInfoWithName(playerName);
        if (info != null) {
            try {
                info.load();
                final String returnMessage = "%s QA status: %s";
                this.sendSafeServerMessage(StringUtil.format("%s QA status: %s", playerName, Boolean.toString(info.isQAAccount())));
                return;
            }
            catch (IOException ie) {
                final String warning = "Unable to load player data for %s.";
                this.sendSafeServerMessage(StringUtil.format("Unable to load player data for %s.", playerName));
                logWarn("Failed to load PlayerInfo when checking QA flag.", ie);
                return;
            }
        }
        final String warning2 = "Unable to find player data for: %s.";
        this.sendSafeServerMessage(StringUtil.format("Unable to find player data for: %s.", playerName));
    }
    
    private void handleHashMessageWatchPlayer(final String message) {
        if (!this.player.isDead() && !this.player.isMute()) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String targetName = "";
            long tId = -10L;
            if (!tokens.hasMoreTokens()) {
                this.sendSafeServerMessage("Need a player's name to watch.");
                return;
            }
            targetName = tokens.nextToken().trim();
            targetName = LoginHandler.raiseFirstLetter(targetName);
            tId = PlayerInfoFactory.getWurmId(targetName);
            if (tId == -10L) {
                this.sendSafeServerMessage("Unknown player " + targetName + ".");
                return;
            }
            String reason = "";
            if (tokens.hasMoreTokens()) {
                reason = tokens.nextToken();
            }
            while (tokens.hasMoreTokens()) {
                reason = reason + ' ' + tokens.nextToken();
            }
            if (reason.isEmpty()) {
                this.sendSafeServerMessage("Needs a reason to watch player " + targetName + ".");
                return;
            }
            this.createWatchTicket(tId, targetName, reason);
        }
    }
    
    private void createWatchTicket(final long playerId, final String playerName, final String reason) {
        final Ticket ticket = new Ticket(Tickets.getNextTicketNo(), System.currentTimeMillis(), playerId, playerName, (byte)11, Servers.getLocalServerId(), true, 0L, (byte)5, (byte)2, "", reason, true, (short)0, true);
        ticket.save();
        Tickets.addTicket(ticket, false);
        Tickets.addTicketToSend(ticket);
        ticket.sendTicketGlobal();
    }
    
    private void handleHashMessageResetAppointments(final String message) {
        final String[] words = message.split(" ");
        if (words.length != 2) {
            this.sendNormalServerMessage("Usage: #resetappointments <kingdomId>");
            return;
        }
        final byte kId = Byte.parseByte(words[1]);
        if (kId == 0) {
            this.sendNormalServerMessage("Usage: #resetappointments <kingdomId>");
            return;
        }
        King.getCurrentAppointments(kId).resetAppointments(kId);
        this.sendNormalServerMessage("Appointments reset.");
    }
    
    private void handleHashMessageClearAppointments(final String message) {
        final String[] words = message.split(" ");
        if (words.length != 2) {
            this.sendNormalServerMessage("Usage: #clearappointments <player>");
            return;
        }
        try {
            final Player p = Players.getInstance().getPlayer(words[1]);
            p.getSaveFile().appointments = 0L;
            this.sendNormalServerMessage("Appointments cleared.");
        }
        catch (NoSuchPlayerException nsp) {
            this.sendNormalServerMessage("That player is not online.");
        }
    }
    
    private void handleHashMessageGive(final String message) {
        final String[] words = message.split(" ");
        if (words.length < 2 || words.length > 4) {
            this.sendNormalServerMessage("Correct usage is: #give <ID> [ql] [amount]");
            return;
        }
        final int[] itemData = { 0, 50, 1 };
        for (int i = 0; i < words.length - 1; ++i) {
            try {
                itemData[i] = Integer.parseInt(words[i + 1]);
            }
            catch (NumberFormatException e2) {
                this.sendNormalServerMessage("You can only use numbers for the ID, ql or amount");
                return;
            }
        }
        itemData[0] = Math.max(itemData[0], 0);
        itemData[1] = Math.max(itemData[1], 1);
        itemData[1] = Math.min(itemData[1], 99);
        itemData[2] = Math.max(itemData[2], 1);
        itemData[2] = Math.min(itemData[2], 99);
        Item item = null;
        try {
            for (int j = 0; j < itemData[2]; ++j) {
                item = ItemFactory.createItem(itemData[0], itemData[1], this.player.getName());
                this.player.getInventory().insertItem(item);
            }
        }
        catch (NoSuchTemplateException | FailedException ex2) {
            final WurmServerException ex;
            final WurmServerException e = ex;
            this.sendNormalServerMessage("This item id does not exist!");
            return;
        }
        this.sendNormalServerMessage("You magically create " + itemData[2] + " " + item.getName() + " with a quality of " + itemData[1]);
    }
    
    public final boolean stillLoggingIn() {
        return this.justLoggedIn;
    }
    
    private void respondEigcTeamMute(final String muted, final int teamId, final byte muteState) {
        try {
            final Player p = Players.getInstance().getPlayer(LoginHandler.raiseFirstLetter(muted));
            if (p != null) {
                final int soundSourceId = Math.abs(generateSoundSourceId(p.getWurmId()));
                p.getCommunicator().sendEigcMute(soundSourceId, teamId, muteState);
                if (muteState == 0) {
                    p.getCommunicator().sendSafeServerMessage("Voice team chat enabled.");
                    this.player.getCommunicator().sendSafeServerMessage(muted + " now has enabled voice team chat.");
                }
                else {
                    p.getCommunicator().sendSafeServerMessage("Voice team chat disabled.");
                    this.player.getCommunicator().sendSafeServerMessage(muted + " had voice team chat disabled.");
                }
            }
        }
        catch (NoSuchPlayerException nsp) {
            this.sendAlertServerMessage("No such player: " + muted);
        }
    }
    
    public void sendEigcMute(final int soundSourceId, final int teamId, final byte muteState) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)123);
                bb.putInt(Math.abs(soundSourceId));
                bb.putInt(teamId);
                bb.put(muteState);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " could not send team invite" + teamId + " due to : " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private void respondEigcTeamInvite(final int teamId, final String inviter, final String invited) {
        try {
            final Player p = Players.getInstance().getPlayer(LoginHandler.raiseFirstLetter(invited));
            if (p != null) {
                if (p.getKingdomId() == this.player.getKingdomId()) {
                    final EigcClient client = Eigc.getClientWithId(p.getEigcId());
                    if (client != null) {
                        p.getCommunicator().sendTeamInvite(teamId, inviter);
                        p.getCommunicator().sendSafeServerMessage("You have been invited by " + inviter + " to join his voice chat team.");
                        this.player.getCommunicator().sendSafeServerMessage("You invite " + invited + " to join your voice chat team.");
                    }
                    else {
                        this.sendAlertServerMessage("No such player: " + invited);
                    }
                }
                else {
                    this.sendAlertServerMessage("No such player: " + invited);
                }
            }
            else {
                this.sendAlertServerMessage("No such player: " + invited);
            }
        }
        catch (NoSuchPlayerException nsp) {
            this.sendAlertServerMessage("No such player: " + invited);
        }
    }
    
    public void sendTeamInvite(final int teamId, final String inviter) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)104);
                bb.putInt(teamId);
                final byte[] inviterStringArr = inviter.getBytes("UTF-8");
                bb.put((byte)inviterStringArr.length);
                bb.put(inviterStringArr);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " could not send team invite" + teamId + " due to : " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private void respondEigcName(final String playerNameAsked) {
        if (this.player != null && this.player.hasLink()) {
            String eigcName = "garbage";
            try {
                final Player p = Players.getInstance().getPlayer(LoginHandler.raiseFirstLetter(playerNameAsked));
                if (p != null) {
                    if (p.getKingdomId() == this.player.getKingdomId()) {
                        final EigcClient client = Eigc.getClientForPlayer(playerNameAsked);
                        if (client != null) {
                            eigcName = client.getClientId();
                        }
                        else {
                            this.sendAlertServerMessage(playerNameAsked + " has no active chat client.");
                        }
                    }
                    else {
                        this.sendAlertServerMessage("No such player: " + playerNameAsked);
                    }
                }
                else {
                    this.sendAlertServerMessage("No such player: " + playerNameAsked);
                }
            }
            catch (NoSuchPlayerException nsp) {
                this.sendAlertServerMessage("No such player: " + playerNameAsked);
            }
            try {
                final byte[] tempStringArr = eigcName.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-1));
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                this.connection.flush();
                logInfo(this.player.getName() + " responded eigc name " + eigcName + " to : " + this.player.getName());
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " could not send eigc name " + eigcName + " due to : " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private void respondEigcPlayerName(final String eigcIdAsked) {
        if (this.player != null && this.player.hasLink()) {
            final EigcClient client = Eigc.getClientWithId(eigcIdAsked);
            if (client != null) {
                final String pname = client.getPlayerName();
                if (!pname.isEmpty()) {
                    try {
                        final byte[] tempStringArr = pname.getBytes("UTF-8");
                        final ByteBuffer bb = this.connection.getBuffer();
                        bb.put((byte)(-2));
                        bb.put((byte)tempStringArr.length);
                        bb.put(tempStringArr);
                        this.connection.flush();
                    }
                    catch (Exception ex) {
                        logInfo(this.player.getName() + " could not send eigc name " + pname + " due to : " + ex.getMessage(), ex);
                        this.player.setLink(false);
                    }
                }
                else {
                    this.sendAlertServerMessage("No player is using that chat id.");
                }
            }
            else {
                this.sendAlertServerMessage("No chat client with that chat id.");
            }
        }
    }
    
    private void respondEigcLogin(final String eigcIdAsked) {
        if (this.player != null && this.player.hasLink()) {
            if (Constants.isEigcEnabled) {
                this.player.setEigcClientId(Eigc.addPlayer(this.player.getName()));
                final EigcClient client = Eigc.getClientWithId(this.player.getEigcId());
                if (client != null) {
                    final String password = client.getPassword();
                    final int soundSourceId = Math.abs(generateSoundSourceId(this.player.getWurmId()));
                    logInfo(this.player.getName() + " sending eigc client id " + client.getClientId() + ", password " + password + " soundSource " + soundSourceId);
                    try {
                        final byte[] tempStringArr = client.getClientId().getBytes("UTF-8");
                        final ByteBuffer bb = this.connection.getBuffer();
                        bb.put((byte)(-3));
                        bb.put((byte)tempStringArr.length);
                        bb.put(tempStringArr);
                        final byte[] pwStringArr = password.getBytes("UTF-8");
                        bb.put((byte)pwStringArr.length);
                        bb.put(pwStringArr);
                        bb.putInt(soundSourceId);
                        final byte[] srvIdStringArr = Eigc.URL_PROXIMITY.getBytes("UTF-8");
                        bb.put((byte)srvIdStringArr.length);
                        bb.put(srvIdStringArr);
                        final byte[] domainStringArr = Eigc.URL_SIP_REGISTRAR.getBytes("UTF-8");
                        bb.put((byte)domainStringArr.length);
                        bb.put(domainStringArr);
                        final byte[] sbgURIStringArr = Eigc.URL_SIP_PROXY.getBytes("UTF-8");
                        bb.put((byte)sbgURIStringArr.length);
                        bb.put(sbgURIStringArr);
                        this.connection.flush();
                        this.setEigcServiceState(client.getServiceBundle());
                    }
                    catch (Exception ex) {
                        logInfo(this.player.getName() + " could not login for name " + this.player.getName() + " due to : " + ex.getMessage(), ex);
                        this.player.setLink(false);
                    }
                }
                else {
                    this.sendNormalServerMessage("Failed to allocate a voice client. The server may be provisioning new accounts. You may try logging in again soon.");
                }
            }
            else {
                this.sendNormalServerMessage("Ingame voice chat is not enabled on this server.");
            }
        }
    }
    
    public final void updateEigcInfo(final EigcClient client) {
        if (this.player != null && this.player.hasLink()) {
            if (Constants.isEigcEnabled) {
                if (client != null) {
                    final String password = client.getPassword();
                    final int soundSourceId = generateSoundSourceId(this.player.getWurmId());
                    try {
                        final byte[] tempStringArr = client.getClientId().getBytes("UTF-8");
                        final ByteBuffer bb = this.connection.getBuffer();
                        bb.put((byte)(-3));
                        bb.put((byte)tempStringArr.length);
                        bb.put(tempStringArr);
                        final byte[] pwStringArr = password.getBytes("UTF-8");
                        bb.put((byte)pwStringArr.length);
                        bb.put(pwStringArr);
                        bb.putInt(soundSourceId);
                        final byte[] srvIdStringArr = Eigc.URL_PROXIMITY.getBytes("UTF-8");
                        bb.put((byte)srvIdStringArr.length);
                        bb.put(srvIdStringArr);
                        final byte[] domainStringArr = Eigc.URL_SIP_REGISTRAR.getBytes("UTF-8");
                        bb.put((byte)domainStringArr.length);
                        bb.put(domainStringArr);
                        final byte[] sbgURIStringArr = Eigc.URL_SIP_PROXY.getBytes("UTF-8");
                        bb.put((byte)sbgURIStringArr.length);
                        bb.put(sbgURIStringArr);
                        this.connection.flush();
                        this.setEigcServiceState(client.getServiceBundle());
                    }
                    catch (Exception ex) {
                        logInfo(this.player.getName() + " could not login for name " + this.player.getName() + " due to : " + ex.getMessage(), ex);
                        this.player.setLink(false);
                    }
                }
                else {
                    this.sendNormalServerMessage("Failed to allocate a voice client. The server may be provisioning new accounts. You may try logging in again soon.");
                }
            }
            else {
                this.sendNormalServerMessage("Ingame voice chat is not enabled on this server.");
            }
        }
    }
    
    public void setEigcServiceState(final String serviceString) {
        if (this.player != null && this.player.hasLink()) {
            byte serviceState = 0;
            if (serviceString.contains("proximity")) {
                ++serviceState;
            }
            if (serviceString.contains("team")) {
                serviceState += 2;
            }
            if (serviceString.contains("p2p")) {
                serviceState += 8;
            }
            if (serviceString.contains("lecture")) {
                serviceState += 4;
            }
            if (serviceString.contains("hifi")) {
                serviceState += 16;
            }
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-8));
                bb.put(serviceState);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private String drunkGarble(final String string) {
        String garbledString = string;
        if (this.player.isUndead()) {
            final StringTokenizer st = new StringTokenizer(string);
            while (st.hasMoreTokens()) {
                final String s = st.nextToken();
                if (Server.rand.nextBoolean()) {
                    if (string.length() <= 6) {
                        continue;
                    }
                    final int start = Server.rand.nextInt(s.length() - 6);
                    if (Server.rand.nextBoolean()) {
                        garbledString = s.substring(0, start) + "HRR";
                    }
                    else {
                        garbledString = "HSS" + s.substring(start + 6, s.length());
                    }
                }
                else {
                    if (string.length() <= 5) {
                        continue;
                    }
                    final int start = Server.rand.nextInt(s.length() - 5);
                    if (Server.rand.nextBoolean()) {
                        garbledString = "UUH" + s.substring(start + 5, s.length());
                    }
                    else {
                        garbledString = s.substring(0, start) + "GHH*";
                    }
                }
            }
        }
        if (this.player.getAlcohol() > 50.0f && Server.rand.nextInt(Math.max(2, 10 - (int)(this.player.getAlcohol() / 10.0f))) == 0) {
            if (Server.rand.nextBoolean()) {
                if (string.length() > 6) {
                    final int start2 = Server.rand.nextInt(string.length() - 6);
                    if (Server.rand.nextInt(2) == 0) {
                        garbledString = string.substring(0, start2) + "*burp*" + string.substring(start2 + 6, string.length());
                    }
                    else {
                        garbledString = string.substring(0, start2) + "*BURP*" + string.substring(start2 + 6, string.length());
                    }
                }
            }
            else if (string.length() > 5) {
                final int start2 = Server.rand.nextInt(string.length() - 5);
                if (Server.rand.nextInt(2) == 0) {
                    garbledString = string.substring(0, start2) + "*hic*" + string.substring(start2 + 5, string.length());
                }
                else {
                    garbledString = string.substring(0, start2) + "*HIC*" + string.substring(start2 + 5, string.length());
                }
            }
        }
        return garbledString;
    }
    
    private void tryInsertBankItem(final Item aSubjectItem, final Item target, final Bank bank, final Item inventory) {
        if (target.isNoPut()) {
            return;
        }
        try {
            if (target.isBodyPart()) {
                boolean found = false;
                for (int x = 0; x < target.getBodySpaces().length; ++x) {
                    if (target.getBodySpaces()[x] == target.getPlace()) {
                        found = true;
                    }
                }
                if (!found && target.getPlace() != 13 && target.getPlace() != 14) {
                    return;
                }
            }
            Item targetParent = null;
            targetParent = Items.getItem(target.getParentId());
            if (targetParent != null && targetParent.isNoPut()) {
                return;
            }
            Item topParent = null;
            try {
                topParent = Items.getItem(target.getTopParent());
                if (topParent.isNoPut()) {
                    return;
                }
            }
            catch (NoSuchItemException nsi) {
                logWarn(nsi.getMessage(), nsi);
            }
            if (target.isLockable() && target.getLockId() != -10L) {
                try {
                    final Item lock = Items.getItem(target.getLockId());
                    final long[] keyIds = lock.getKeyIds();
                    for (int x2 = 0; x2 < keyIds.length; ++x2) {
                        if (aSubjectItem.getWurmId() == keyIds[x2]) {
                            return;
                        }
                    }
                }
                catch (NoSuchItemException nsi) {
                    logWarn(target.getWurmId() + ": item has a set lock but the lock does not exist?:" + target.getLockId(), nsi);
                }
            }
            if (targetParent != null && targetParent.isLockable() && targetParent.getLockId() != -10L) {
                try {
                    final Item lock = Items.getItem(targetParent.getLockId());
                    final long[] keyIds = lock.getKeyIds();
                    for (int x2 = 0; x2 < keyIds.length; ++x2) {
                        if (aSubjectItem.getWurmId() == keyIds[x2]) {
                            return;
                        }
                    }
                }
                catch (NoSuchItemException nsi) {
                    logWarn(targetParent.getWurmId() + ": item has a set lock but the lock does not exist?:" + targetParent.getLockId(), nsi);
                }
            }
            if (target.testInsertItem(aSubjectItem)) {
                this.sendRemoveFromInventory(aSubjectItem, bank.id);
                bank.removeItem(aSubjectItem);
                aSubjectItem.removeWatcher(this.player, false);
                target.insertItem(aSubjectItem);
                aSubjectItem.setLastMaintained(WurmCalendar.currentTime);
                return;
            }
            final Item cont = Items.getItem(target.getParentId());
            if (!cont.isBodyPart()) {
                if (cont.testInsertItem(aSubjectItem)) {
                    this.sendRemoveFromInventory(aSubjectItem, bank.id);
                    bank.removeItem(aSubjectItem);
                    aSubjectItem.removeWatcher(this.player, false);
                    cont.insertItem(aSubjectItem);
                    aSubjectItem.setLastMaintained(WurmCalendar.currentTime);
                    return;
                }
                this.player.getCommunicator().sendNormalServerMessage("The " + aSubjectItem.getName() + " will not fit in the " + cont.getName() + '.');
            }
        }
        catch (NoSuchItemException nsi2) {
            logWarn(nsi2.getMessage(), nsi2);
        }
    }
    
    private void sendHelp() {
        this.sendHelpMessage("Current available commands:");
        this.sendHelpMessage("/addfriend <friendsname> <category> - add someone to your friends list remotely.");
        this.sendHelpMessage("/afk [<message>] - toggles Away-From-Keyboard mode, with optional message.");
        this.sendHelpMessage("/alliance <message> - alliance chat.");
        this.sendHelpMessage("/almanac - shows the harvestables using the reports in almanac(s) in your inventory.");
        this.sendHelpMessage("/attackers - shows who you have been fighting the last five minutes.");
        this.sendHelpMessage("/ca - toggles messages to the community assistant window");
        this.sendHelpMessage("/caringfor - shows list of creatures you are caring for.");
        if (this.player.isKing()) {
            this.sendHelpMessage("/challenge - sends you a popup where you can answer challenges to your sovereignty.");
        }
        this.sendHelpMessage("/champs - shows the Champion Eternal Records.");
        this.sendHelpMessage("/clear - clears the current tab.");
        this.sendHelpMessage("/clear <tabName> - clears the specified tab on event side (e.g. /clear combat).");
        this.sendHelpMessage("/converts - shows the number of times you can change kingdom");
        this.sendHelpMessage("/fatigue - displays how much time you have left to perform fatiguing tasks.");
        this.sendHelpMessage("/fl - shows your current combat focus level");
        this.sendHelpMessage("/fsleep - freezes or thaws the consumption of sleep bonus. May be toggled every 5 minutes. This toggle is reset every server restart or you change server.");
        this.sendHelpMessage("/ignore <player> - makes you unable to hear that player. It also adds to mute vote if used by many people at the same time.");
        this.sendHelpMessage("/ignore - shows ignore list");
        this.sendHelpMessage("/invitations - allows you to receive an invitation from another player to join their kingdom or religion.");
        this.sendHelpMessage("/kingdoms - displays kingdom influence on this server.");
        if (this.player.isChampion()) {
            this.sendHelpMessage("/lives - shows the number of respawns you have left");
        }
        this.sendHelpMessage("/lotime - shows how long until you leave the game if you lose link");
        this.sendHelpMessage("/me <emote> - replaces '/me ' with your name and sends the rest to players in the vicinity.");
        this.sendHelpMessage("/mission - displays the last instructions received");
        this.sendHelpMessage("/openchat <channel> - channel is one of [k]ingdom, [g]lobal kingdom or [t]rade.");
        this.sendHelpMessage("/password <oldpassword> <newpassword> - changes your password.");
        this.sendHelpMessage("/playtime - shows information about the time you have played.");
        this.sendHelpMessage("/poll - In Game poll.");
        this.sendHelpMessage("/reputation - shows your current reputation. Reputation is affected by attacking other players and stealing.");
        this.sendHelpMessage("/random <number> - broadcasts a random number up to max <number> a few tiles.");
        this.sendHelpMessage("/rank - shows your current battle rank.");
        this.sendHelpMessage("/ranks - shows top battle ranks.");
        this.sendHelpMessage("/refer - A premium account player may give away free silver coins or playing time once.");
        this.sendHelpMessage("/release corpse - normally people from your kingdom may not loot your corpse. If you issue this command they may.");
        this.sendHelpMessage("/remove <person> - removes person from your friends list");
        this.sendHelpMessage("/respawn - sends a dialogue offering you to respawn when you are dead.");
        this.sendHelpMessage("/revoke <villagename> - removes you as a citizen from the village.");
        this.sendHelpMessage("/shout <message> - kingdom chat");
        if (this.player.canSignIn()) {
            this.sendHelpMessage("/signin [<message>] - signs you in.");
            this.sendHelpMessage("/signout [<message>] - signs you out.");
        }
        this.sendHelpMessage("/sleep - shows how long you have left of sleep bonus.");
        this.sendHelpMessage("/snipe <person> - (premium only) will mute the player if enough people issue this command at roughly the same time. You have only one snipe per time period.");
        this.sendHelpMessage("/stopcaring - frees all the animal husbandry slots for caring.");
        this.sendHelpMessage("/stuck - helps you getting out from trees your are stuck in.");
        this.sendHelpMessage("/suicide - kills you. You will lose some skill!");
        this.sendHelpMessage("/support <message> - opens up a support ticket window so you can add extra details before sending.");
        this.sendHelpMessage("/team <message> - team chat");
        this.sendHelpMessage("/tell <person> <message> - tells someone something ingame.");
        this.sendHelpMessage("/time - shows current game time.");
        this.sendHelpMessage("/tinvite <person> - invites a player to your team.");
        this.sendHelpMessage("/title - displays the title you are currently using.");
        this.sendHelpMessage("/titles - gives the option to select an active title among your available titles.");
        this.sendHelpMessage("/toggleccfp - toggle visibility of the ccfp bar.");
        this.sendHelpMessage("/tutorial - start the in-game tutorial.");
        this.sendHelpMessage("/tweet - sends your tweet to the village twitter if enabled.");
        this.sendHelpMessage("/uptime - shows the time since the last reboot");
        this.sendHelpMessage("/village <message> - village chat");
        this.sendHelpMessage("/vinvite <name> - Sends a village invite to the named player.");
        this.sendHelpMessage("/villageinvite <name> - Sends a village invite to the named player.");
        this.sendHelpMessage("/vteleport - Allows you to use your one free village teleport.");
        this.sendHelpMessage("/vote <citizen> - vote for a citizen to become mayor");
        this.sendHelpMessage("/warnings - shows information about your official moderation warnings.");
        this.sendHelpMessage("/weather - Gives information about wind direction and speed");
        this.sendHelpMessage("/who - shows logged on people");
        this.sendHelpMessage("/recruit <playername> - Adds a player to your village recruit list.");
        this.sendHelpMessage("/unrecruit <playername> - Removes a player from your village recruit list.");
        this.sendHelpMessage("/listrecruits - Show your village recruitment list.");
        this.sendHelpMessage("/join player <playername> - Attempts to join the village of the player, must be on the village recruitment list.");
        this.sendHelpMessage("/join village <villagename> - Attempts to join the village, must be on the village recruitment list.");
        this.sendHelpMessage("/mykingdoms - Displays the kingdoms you are currently affiliated with on Chaos and Epic");
        ServerTweaksHandler.sendHelp(this.player);
        this.sendHelpMessage("/help or /?- this message");
    }
    
    private void sendChangelog() {
        if (Servers.isThisATestServer() && (Servers.getLocalServerId() == 3001 || Servers.getLocalServerId() == 3002 || Servers.getLocalServerId() == 63505)) {
            final WurmInfo2 info = new WurmInfo2(this.player);
            info.sendQuestion();
        }
    }
    
    private void sendInfo() {
        if (Servers.isThisATestServer() && (Servers.getLocalServerId() == 3001 || Servers.getLocalServerId() == 3002 || Servers.getLocalServerId() == 63505)) {
            final WurmInfo info = new WurmInfo(this.player);
            info.sendQuestion();
        }
    }
    
    private void sendNews() {
        if (Servers.isThisATestServer() && (Servers.getLocalServerId() == 3001 || Servers.getLocalServerId() == 3002 || Servers.getLocalServerId() == 63505)) {
            final NewsInfo info = new NewsInfo(this.player);
            info.sendQuestion();
        }
    }
    
    private void sendDeityHelp(final int power) {
        this.sendHelpMessage("Current available commands:");
        if (this.player.mayMute() || power >= 1) {
            this.sendHelpMessage("#chat <int color> - colors your chat so that players understand that it is formal. The color is optional and you'll get orange otherwise, also second parmeter can be r.g.b values.");
            if (power == 1) {
                this.sendHelpMessage("#invis - toggles invisibility");
            }
        }
        if (this.player.mayMute() || power >= 2) {
            this.sendHelpMessage("#mute <playername> <hours> <reason> - the player cannot communicate except with tell.");
            this.sendHelpMessage("#unmute <playername> - pardons a mute.");
            this.sendHelpMessage("#mutewarn <playername> (reason) - sends a warning that a player may be muted. The reason is optional.");
            this.sendHelpMessage("#showmuters - displays a list of the people who can mute apart from the gms.");
            this.sendHelpMessage("#showmuted - displays a list of the people who are muted.");
            this.sendHelpMessage("#showcas - displays a list of the ca.");
        }
        if (power >= 2 || this.player.mayHearDevTalk()) {
            this.sendHelpMessage("#showdevtalkers - displays a list of the people who can see the GM Tab.");
        }
        if (power >= 2) {
            this.sendHelpMessage("#alerts - lets you change periodic messages from the server.");
            this.sendHelpMessage("#announce - announces a blue system wide message.");
            this.sendHelpMessage("#ban <playername> <days> <reason> - bans the player and the ipaddress. You must provide the number of days and a reason.");
            this.sendHelpMessage("#banhere|#baniphere|#pardonhere|pardoniphere <playername> - bancontrol for the current server only");
            this.sendHelpMessage("#banip <ipaddress> <days> <reason> - bans the ipaddress and kicks anyone from it. You must provide the number of days and a reason.");
            this.sendHelpMessage("#bansteamid <steamid/player> <days> <reason> - bans the steamId");
            this.sendHelpMessage("#kips - displays kingdom IP addresses and time since last logout.");
            this.sendHelpMessage("#broadcast - broadcasts a system wide message.");
            this.sendHelpMessage("#calcCreatures - Calculates number of creatures on surface, in caves, are visible, and offline, Use with care - lag prone.");
            this.sendHelpMessage("#changeemail <playername> <newemail> - changes the email of a single player character.");
            this.sendHelpMessage("#changemodel <model> - change character model (gmdark or gmnormal)");
            this.sendHelpMessage("#changepassword <playername> <newpassword> - changes the password of a player.");
            this.sendHelpMessage("#checkCreatures - error checks the positions of creatures. Will return dislocated guards for instance. May provide a name like 'templar' to check only those. Use with care - lag prone and may cause instant spawns.");
            this.sendHelpMessage("#checkclients [<name> [true]]- sends a message to all clients that they should relaunch if they run an old client version.");
            this.sendHelpMessage("             if <name> is specified then just send a message to the specified player to get a list of loaded classes.");
            this.sendHelpMessage("             if [true] is specified then the list of loaded classes is NOT filted upon its return.");
            this.sendHelpMessage("#findboat <name> - lets you find a boat with part of the name in it. May be processor heavy so if you notice lag, use with care!");
            this.sendHelpMessage("#getAchievementData <playername> <achievement id>");
            this.sendHelpMessage("#getip <playername> - displays the players ip address and any other accounts from the same address.");
            this.sendHelpMessage("#getips - displays the current players with ip addresses.");
            this.sendHelpMessage("#getwarnings <playername> - displays info about the player's warnings.");
            this.sendHelpMessage("#gm - send a GM message to login server.");
            this.sendHelpMessage("#gmlight - togles personal light on/off when you are invisible.");
            this.sendHelpMessage("#hideme [GM] - hides (GM) name in MGMT and GM Tab list, if GM is specified then just the GM tab is done.");
            this.sendHelpMessage("#invis - toggles invisibility");
            this.sendHelpMessage("#kick <playername> - kicks the player");
            this.sendHelpMessage("#loadItem <long id> - loads item with id, (removing from the owner).");
            this.sendHelpMessage("#locateavatars - locates all avatars on the server and tells you where they are. It could cause lag, so use sparingly.");
            this.sendHelpMessage("#locatehorse <string> - return the location of horses whose name contains the supplied argument string.");
            this.sendHelpMessage("#offline - shows offline creatures with location.");
            this.sendHelpMessage("#onfire - toggles player fire.");
            this.sendHelpMessage("#online <playername> - returns the current status and server of the given player");
            this.sendHelpMessage("#pardon <playername> - pardons the player and the ipaddress");
            this.sendHelpMessage("#pardonip <ipaddress> - pardons the ipaddress");
            this.sendHelpMessage("#pardonsteamid <steamid> - pardons the steamid");
            this.sendHelpMessage("#plimit <new number> - the number when the server no longer accepts free players. It will always let premiums in though.");
            this.sendHelpMessage("#reload <creatureId or playername> - reload a player or creature when bugged.");
            this.sendHelpMessage("#rename <oldname> <newname> <password> - renames the player. The player must be LOGGED OFF.");
            this.sendHelpMessage("#resetwarnings <playername> - resets the players warnings to 0.");
            this.sendHelpMessage("#respawn <playername> - respawns a dead player at the start.");
            this.sendHelpMessage("#setmuter <name> - gives or removes the ability to a normal player to mute other players.");
            this.sendHelpMessage("#setreputation <playername> <new reputation> - sets the reputation of a player.");
            this.sendHelpMessage("#setserver <playername> <serverid> - tells this server that the player is on the server with the number specified.");
            this.sendHelpMessage("#showbans - displays current bans");
            this.sendHelpMessage("#showheros [power] - displays a list of the people with power (defaults to Hero).");
            if (power < 3) {
                this.sendHelpMessage("#showme - shows (GM) name in MGMT Tab list.");
            }
            this.sendHelpMessage("#soundspam - spams area around you with random sounds for testing.");
            this.sendHelpMessage("#timemod <hours> - modifies your current time with the number of hours. Can be negative.");
            this.sendHelpMessage("#toggleEpic - toggles epic portals");
            this.sendHelpMessage("#toggleglobal - toggles global chat.");
            this.sendHelpMessage("#warn <playername> - the player receives an official warning.");
            this.sendHelpMessage("#watch <playername> <description> - creates a 'watch' ticket.");
            this.sendHelpMessage("#who [J|H|M] - sends a list of players online from Jenn-kellon, HOTS, or Mol-Rehan respectively");
            this.sendHelpMessage("#worth <name> - helps debug royal level kills on pvp servers.");
        }
        if (power >= 3) {
            this.sendHelpMessage("#addmoney <name months days silvers detail> - adds prem or silver to a players account. Detail needs to be any unique string like paypal id or 'reimburseXox22332'. Example: #addmoney rolf 0 2 2 add2days2silverrolf");
            this.sendHelpMessage("#addtitle <name> [<title id>] - adds the default title Clairvoyant to bug reporters. title id is optional.");
            this.sendHelpMessage("#allowall - opens the server for new connections. (leaves maintenance mode).");
            this.sendHelpMessage("#artist <name> <sound> <graphics> - toggle artist privileges. example:  #artist mrb false true.");
            this.sendHelpMessage("#creaturepos - toggles creature position logging.");
            this.sendHelpMessage("#devtalk <name> - toggles the ability to a normal player to hear the gm chat.");
            this.sendHelpMessage("#dumpxml - generates a new epic xml on the login server.");
            this.sendHelpMessage("#invuln - toggles invulnerability mode.");
            this.sendHelpMessage("#itempos <id> - checks the position of an item.");
            this.sendHelpMessage("#maxcreatures <newvalue> - sets the number of max creature to a new value.");
            this.sendHelpMessage("#newmission <deityname> - generates a new epic mission for the provided deity.");
            this.sendHelpMessage("#overrideshop <name> <true|false> - if set to true the player may use the shop even though he has had previous payment reversals.");
            this.sendHelpMessage("#readlog - Reads and displays the player's log from the server filesystem. TODO. ");
            this.sendHelpMessage("#redeem - functionality to retrieve items from banned players.");
            this.sendHelpMessage("#registermail - registers player email in list.");
            this.sendHelpMessage("#removetitle <name> [<title id>] - removes the default title Community Assistant. title id is optional.");
            this.sendHelpMessage("#resetplayer <name> - resets the players skills and faith to max 20. Also removes champion/realdeath.");
            this.sendHelpMessage("#sdown - displays a message that the server is shutting down and rejects new connections. Does not shut down (enters maintenance mode).");
            this.sendHelpMessage("#startx <number> - sets the tile X where new players (Jenn-Kellon, or for home servers all players) start to the number given.");
            this.sendHelpMessage("#starty <number> - sets the tile Y where new players (Jenn-Kellon, or for home servers all players) start to the number given.");
            this.sendHelpMessage("#togglemission <missionname> - enables or disables the mission with the name supplied.");
            this.sendHelpMessage("#togglemounts - enables or disables riding, driving and horse spawning.");
            this.sendHelpMessage("#toggleqa <name> - toggles the QA status on or off for the account.");
            this.sendHelpMessage("#toggleflag <name> <flagid> - toggles the flag on or off for the account.");
            this.sendHelpMessage("#isqa <name> - Checks if account has QA status.");
            this.sendHelpMessage("#getStoredCreatures - List the WurmID and names of stored creatures on this server.");
            if (Servers.localServer.testServer) {
                this.sendHelpMessage("#addfakemo value <owner> <parent> - adds fake money to your inventory with the optionally provided owner and parent item.");
            }
            else {
                this.sendHelpMessage("#addmoney name months days silvers detail - adds to a players account. Detail is the paypal transaction id.");
            }
        }
        if (power >= 4) {
            this.sendHelpMessage("#flattenRock <N>, <E>, <S>, <W>, [Extra Distance Below] ");
            this.sendHelpMessage("              - Custom sized flatten zone. Flattens to rock instead of dirt.");
            this.sendHelpMessage("#flattenDirt <N>, <E>, <S>, <W>, [Extra Distance Below], [Min Dirt Distance to Rock]");
            this.sendHelpMessage("              - Pretty self-explanatory.");
            this.sendHelpMessage("#setpower <player> <power>, sets the power of a player. 0=Normal, 1=Hero, 2=Demigod, 3=High-god, 4=Arch, 5=Dev.");
            this.sendHelpMessage("#listdens, shows a list of all unique dens.");
            this.sendHelpMessage("#removeden <templateID>, removes a unique den with a certain template ID");
            this.sendHelpMessage("#reloadItems <wurmId> - checks for all subitems of the given itemId and forces them back into that item.");
            this.sendHelpMessage("#showPersonalGoals <playerName> - Shows the current (and old) personal goals of the player");
        }
        if (power >= 5) {
            this.sendHelpMessage("#addreimb name months days silvers setbok - adds to a players reimbursement pool.");
            this.sendHelpMessage("#addregalia name - adds kingdom regalia.");
            this.sendHelpMessage("#checkItems - error checks the positions of items. Use with care - lag prone.");
            this.sendHelpMessage("#checkZones - Error checks the surface and cave zones. TODO. ");
            this.sendHelpMessage("#createportals - create Freedom Portals. TODO. ");
            this.sendHelpMessage("#enableboats - create boat entriess. TODO. ");
            this.sendHelpMessage("#findfish - lists the close special fish spots.");
            if (Servers.isThisATestServer()) {
                this.sendHelpMessage("#findoldfish - lists the old close rare fish spots.");
            }
            this.sendHelpMessage("#lagstatus - gives the count in real seconds from startup versus the number of seconds ticked.");
            this.sendHelpMessage("#locate <string> - return the location of creature with name containing the supplied argument string.");
            this.sendHelpMessage("#locateitem <templateid> - return the location of the end game item with that id.");
            this.sendHelpMessage("#noxmas - remove Christmas light effect from player. ");
            this.sendHelpMessage("#playerstatuses - shows how long until a player should leave the world, or if they should be logged off already.");
            this.sendHelpMessage("#removeknownrecipes [player] [recipeId]");
            this.sendHelpMessage("           - removes all known recieps from specified player (if just player specified)");
            this.sendHelpMessage("             OR the specified recipe from all players (if just recipe specified)");
            this.sendHelpMessage("             OR the specified recipe from the specified player (if both specified).");
            this.sendHelpMessage("#removeNamedRecipe [player or recipeId] [\"remove\"]");
            this.sendHelpMessage("           - show recipe/player from named list ");
            this.sendHelpMessage("             OR remove entry from named list (if 2nd param is \"remove\").");
            this.sendHelpMessage("#uniques - shows all unique creatures. Caution prone to lag. TODO");
            this.sendHelpMessage("#vespeed <speed> - sets vehicle speed (0-255).");
            this.sendHelpMessage("#date <date> - show the date in real date.");
            this.sendHelpMessage("#wurmdate <wurmdate> - show the date in wurm calendar format.");
            this.sendHelpMessage("#xmaslight - create Christmas light effect at current location.");
            this.sendHelpMessage("#dumpcavewater - creates a png image of the cave water types (waterCaveTypes.png)");
            this.sendHelpMessage("#dumpcreatures - creates a png image of the map with creatures on it.");
            this.sendHelpMessage("#dumpmarkers - creates a png image of the map for markers.");
            this.sendHelpMessage("#dumproutes - creates a png image of the map for routes.");
            this.sendHelpMessage("#dumpsurfacewater - creates a png image of the surface water types (waterSurfaceTypes.png)");
            this.sendHelpMessage("#give <id> [ql] [amount] - gives you an item with the specified parameters. Use id 176 for ebony wand.");
            this.sendHelpMessage("#generateDeadVillage [number] - generates a number of dead villages for archaeology purposes. A number more than 5 will not return info about the villages, just if it was successful or not.");
        }
        if (this.player.mayAppointPlayerAssistant()) {
            this.sendHelpMessage("#toggleca <name> - toggles the community assistant flag for the named player.");
        }
        this.sendHelpMessage("#help - this message");
    }
    
    private void createFreedomPortals() {
        if (Communicator.hasCreated) {
            this.sendNormalServerMessage("Already done.");
            return;
        }
        Communicator.hasCreated = true;
        int x = 0;
        int y = 0;
        while (x < Zones.worldTileSizeX) {
            x += Server.rand.nextInt(200);
            y = 0;
            while (y < Zones.worldTileSizeY) {
                y += Server.rand.nextInt(200);
                try {
                    final Item portal = ItemFactory.createItem(637, Server.rand.nextFloat() * 100.0f, x << 2, y << 2, Server.rand.nextFloat() * 360.0f, true, (byte)0, -10L, null);
                    portal.setData1(5);
                    this.sendNormalServerMessage(x + "," + y);
                }
                catch (Exception ex) {
                    logInfo(ex.getMessage(), ex);
                }
            }
        }
    }
    
    private boolean mayMoveToInventory(final Item item) {
        boolean toReturn = false;
        try {
            if (item.getParent() != null && item.getParent().isTraded()) {
                return false;
            }
        }
        catch (NoSuchItemException ex) {}
        if (item.getOwnerId() == this.player.getWurmId()) {
            toReturn = true;
        }
        return toReturn;
    }
    
    private void sendZones() {
        Zones.calculateZones(false);
        final Kingdom[] allKingdoms;
        final Kingdom[] kingdoms = allKingdoms = Kingdoms.getAllKingdoms();
        for (final Kingdom kingdom : allKingdoms) {
            if (kingdom.existsHere()) {
                this.sendNormalServerMessage("Percent controlled by " + kingdom.getName() + ": " + Communicator.twoDecimals.format(Zones.getPercentLandForKingdom(kingdom.getId())));
            }
        }
    }
    
    public void sendMessage(final Message message) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final byte[] tempStringArr = message.getMessage().getBytes("UTF-8");
            final byte[] window = message.getWindow().getBytes();
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)99);
            bb.put((byte)window.length);
            bb.put(window);
            bb.put((byte)message.getRed());
            bb.put((byte)message.getGreen());
            bb.put((byte)message.getBlue());
            bb.putShort((short)tempStringArr.length);
            bb.put(tempStringArr);
            bb.put((byte)0);
            this.connection.flush();
        }
        catch (Exception ex) {
            logInfo(this.player.getName() + " could not send a message '" + message + "' due to : " + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void sendMgmtMessage(final long time, final String sender, final String message) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final String fd = this.df.format(new java.util.Date(time));
            final byte[] tempStringArr = (fd + " <" + sender + "> " + message).getBytes("UTF-8");
            final byte[] window = Communicator.mgmt;
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)99);
            bb.put((byte)window.length);
            bb.put(window);
            bb.put((byte)(-56));
            bb.put((byte)(-56));
            bb.put((byte)(-56));
            bb.putShort((short)tempStringArr.length);
            bb.put(tempStringArr);
            bb.put((byte)0);
            this.connection.flush();
        }
        catch (Exception ex) {
            logInfo(this.player.getName() + " could not send a MGMT message '" + message + "' due to : " + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void sendGmMessage(final long time, final String sender, final String message) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final String fd = this.df.format(new java.util.Date(time));
            final byte[] tempStringArr = (fd + " <" + sender + "> " + message).getBytes("UTF-8");
            byte[] window = Communicator.gms;
            if (message.contains(" movement too ")) {
                window = "Warn".getBytes();
            }
            if (message.startsWith("Debug:")) {
                window = "Debug".getBytes();
            }
            if (sender.equals("Roads")) {
                window = "Roads".getBytes();
            }
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)99);
            bb.put((byte)window.length);
            bb.put(window);
            bb.put((byte)(-56));
            bb.put((byte)(-56));
            bb.put((byte)(-56));
            bb.putShort((short)tempStringArr.length);
            bb.put(tempStringArr);
            bb.put((byte)0);
            this.connection.flush();
        }
        catch (Exception ex) {
            logInfo(this.player.getName() + " could not send a GM message '" + message + "' due to : " + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void sendAllianceMessage(final String message) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final byte[] tempStringArr = message.getBytes("UTF-8");
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)99);
            bb.put((byte)Communicator.alliance.length);
            bb.put(Communicator.alliance);
            bb.put((byte)(-1));
            bb.put((byte)(-1));
            bb.put((byte)(-1));
            bb.putShort((short)tempStringArr.length);
            bb.put(tempStringArr);
            bb.put((byte)0);
            this.connection.flush();
        }
        catch (Exception ex) {
            logInfo(this.player.getName() + " could not send a message '" + message + "' due to : " + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void sendClearWindowMessage(final String window) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = window.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-65));
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " could not send a clear window message '" + window + "' due to : " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendColoredMessageCombat(final List<MulticolorLineSegment> segments) {
        this.sendColoredMessageCombat(segments, (byte)0);
    }
    
    public void sendColoredMessageCombat(final List<MulticolorLineSegment> segments, final byte onScreenType) {
        this.sendColoredMessage(Communicator.combat, segments, onScreenType);
    }
    
    public void sendColoredMessageEvent(final List<MulticolorLineSegment> segments) {
        this.sendColoredMessageEvent(segments, (byte)0);
    }
    
    public void sendColoredMessageEvent(final List<MulticolorLineSegment> segments, final byte onScreenType) {
        this.sendColoredMessage(Communicator.event, segments, onScreenType);
    }
    
    public void sendColoredMessage(final String title, final List<MulticolorLineSegment> segments) {
        this.sendColoredMessage(title, segments, (byte)0);
    }
    
    public void sendColoredMessage(final String title, final List<MulticolorLineSegment> segments, final byte onScreenType) {
        try {
            final byte[] titleStringArr = title.getBytes("UTF-8");
            this.sendColoredMessage(titleStringArr, segments, onScreenType);
        }
        catch (UnsupportedEncodingException e) {
            logInfo(this.player.getName() + " could not send a multicolor message due to : " + e.getMessage(), e);
            this.player.setLink(false);
        }
    }
    
    public void sendColoredMessage(final byte[] tabName, final List<MulticolorLineSegment> segments, final byte onScreenType) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)45);
            bb.put((byte)tabName.length);
            bb.put(tabName);
            bb.putShort((short)segments.size());
            boolean firstSegment = true;
            for (final MulticolorLineSegment segment : segments) {
                String text = segment.getText();
                byte color = segment.getColor();
                if (segment instanceof CreatureLineSegment) {
                    final CreatureLineSegment cretSegment = (CreatureLineSegment)segment;
                    text = cretSegment.getText(this.player);
                    color = cretSegment.getColor(this.player);
                }
                if (firstSegment) {
                    text = StringUtilities.raiseFirstLetterOnly(text);
                    firstSegment = false;
                }
                final byte[] tempStringArr = text.getBytes("UTF-8");
                bb.putShort((short)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put(color);
            }
            bb.put(onScreenType);
            this.connection.flush();
        }
        catch (Exception ex) {
            logInfo(this.player.getName() + " could not send a multicolor message due to : " + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void sendSystemMessage(final String message) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = message.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)99);
                bb.put((byte)Communicator.system.length);
                bb.put(Communicator.system);
                bb.put((byte)102);
                bb.put((byte)(-72));
                bb.put((byte)120);
                bb.putShort((short)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put((byte)0);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " could not send a message '" + message + "' due to : " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendHelpMessage(final String message) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = message.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)99);
                bb.put((byte)Communicator.help.length);
                bb.put(Communicator.help);
                bb.put((byte)102);
                bb.put((byte)(-72));
                bb.put((byte)120);
                bb.putShort((short)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put((byte)0);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " could not send a message '" + message + "' due to : " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendSafeServerMessage(final String message) {
        this.sendServerMessage(message, 102, 184, 120);
    }
    
    public void sendSafeServerMessage(final String message, final byte messageType) {
        this.sendServerMessage(message, 102, 184, 120, messageType);
    }
    
    public void sendNormalServerMessage(final String message) {
        this.sendServerMessage(message, 255, 255, 255);
    }
    
    public void sendNormalServerMessage(final String message, final byte messageType) {
        this.sendServerMessage(message, 255, 255, 255, messageType);
    }
    
    public void sendServerMessage(final String message, final int r, final int g, final int b) {
        this.sendServerMessage(message, r, g, b, (byte)0);
    }
    
    public void sendServerMessage(final String message, final int r, final int g, final int b, final byte messageType) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = StringUtilities.raiseFirstLetterOnly(message).getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)99);
                bb.put((byte)Communicator.event.length);
                bb.put(Communicator.event);
                bb.put((byte)r);
                bb.put((byte)g);
                bb.put((byte)b);
                bb.putShort((short)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put(messageType);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " could not send a message '" + message + "' due to : " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendCombatNormalMessage(final String message) {
        this.sendCombatNormalMessage(message, (byte)0);
    }
    
    public void sendCombatNormalMessage(final String message, final byte messageType) {
        this.sendCombatServerMessage(message, (byte)(-1), (byte)(-1), (byte)(-1), messageType);
    }
    
    public void sendCombatAlertMessage(final String message) {
        this.sendCombatServerMessage(message, (byte)(-1), (byte)(-106), (byte)10, (byte)0);
    }
    
    public void sendCombatSafeMessage(final String message) {
        this.sendCombatServerMessage(message, (byte)102, (byte)(-72), (byte)120, (byte)0);
    }
    
    public void sendCombatServerMessage(final String message, final byte r, final byte g, final byte b) {
        this.sendCombatServerMessage(message, r, g, b, (byte)0);
    }
    
    public void sendCombatServerMessage(final String message, final byte r, final byte g, final byte b, final byte messageType) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = StringUtilities.raiseFirstLetterOnly(message).getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)99);
                bb.put((byte)Communicator.combat.length);
                bb.put(Communicator.combat);
                bb.put(r);
                bb.put(g);
                bb.put(b);
                bb.putShort((short)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put(messageType);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendDeathServerMessage(final String message, final byte r, final byte g, final byte b) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = StringUtilities.raiseFirstLetterOnly(message).getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)99);
                bb.put((byte)Communicator.deaths.length);
                bb.put(Communicator.deaths);
                bb.put(r);
                bb.put(g);
                bb.put(b);
                bb.putShort((short)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put((byte)0);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAlertServerMessage(final String message) {
        this.sendAlertServerMessage(message, (byte)0);
    }
    
    public void sendAlertServerMessage(final String message, final byte messageType) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = StringUtilities.raiseFirstLetterOnly(message).getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)99);
                bb.put((byte)Communicator.event.length);
                bb.put(Communicator.event);
                bb.put((byte)(-1));
                bb.put((byte)(-106));
                bb.put((byte)10);
                bb.putShort((short)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put(messageType);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ": " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendLogMessage(final String message) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = message.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)99);
                bb.put((byte)Communicator.logs.length);
                bb.put(Communicator.logs);
                bb.put((byte)(-1));
                bb.put((byte)(-1));
                bb.put((byte)(-1));
                bb.putShort((short)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put((byte)0);
                this.connection.flush();
            }
            catch (Exception e) {
                logInfo(this.player.getName() + ": " + e.getMessage(), e);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddToInventory(final Item item, final long inventoryWindow, final long rootid, final int price) {
        if (this.player != null && this.player.hasLink()) {
            try {
                if (item.isPlacedOnParent() && item.getTopParentOrNull() != null && item.getTopParentOrNull().getTemplate().isContainerWithSubItems() && item.getTopParentOrNull().getOwnerId() == -10L && this.player.getPower() < 1) {
                    return;
                }
                boolean calcCorrectBulkWeight = item.isCrate();
                if (item.isBulkItem()) {
                    final Item parent = item.getParentOrNull();
                    if (parent != null && parent.isCrate()) {
                        calcCorrectBulkWeight = true;
                    }
                }
                final int weight = item.getFullWeight(calcCorrectBulkWeight);
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)76);
                bb.putLong(inventoryWindow);
                long parentId = 0L;
                if (item.isBanked()) {
                    parentId = inventoryWindow;
                }
                else if (rootid != 0L && item.getParentId() > 0L) {
                    parentId = item.getParentId();
                }
                bb.putLong(parentId);
                bb.putLong(item.getWurmId());
                bb.putShort(item.getImageNumber());
                byte[] tempStringArr = item.getName().getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                tempStringArr = item.getHoverText().getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                if (item.descIsName()) {
                    tempStringArr = "".getBytes("UTF-8");
                }
                else {
                    tempStringArr = item.getDescription().getBytes("UTF-8");
                }
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putFloat(item.getQualityLevel());
                bb.putFloat(item.getDamage());
                bb.putInt(weight);
                bb.put((byte)((item.color != -1) ? 1 : 0));
                if (item.color != -1) {
                    bb.put((byte)WurmColor.getColorRed(item.color));
                    bb.put((byte)WurmColor.getColorGreen(item.color));
                    bb.put((byte)WurmColor.getColorBlue(item.color));
                }
                bb.put((byte)((price >= 0) ? 1 : 0));
                if (price >= 0) {
                    bb.putInt(price);
                }
                int templateId = -10;
                if (item.isRepairable() && item.creationState == 0) {
                    if (!item.isNewbieItem() && !item.isChallengeNewbieItem()) {
                        templateId = MethodsItems.getImproveTemplateId(item);
                    }
                }
                else if (item.creationState != 0) {
                    templateId = MethodsItems.getItemForImprovement(MethodsItems.getImproveMaterial(item), item.creationState);
                }
                if (item.getTemplateId() == 1307 && item.getData1() <= 0) {
                    if (item.getAuxData() >= 65) {
                        templateId = 441;
                    }
                    else {
                        templateId = 97;
                    }
                }
                if (templateId != -10L) {
                    try {
                        final ItemTemplate temp = ItemTemplateFactory.getInstance().getTemplate(templateId);
                        bb.put((byte)1);
                        bb.putShort(temp.imageNumber);
                    }
                    catch (NoSuchTemplateException nst) {
                        bb.put((byte)0);
                    }
                }
                else {
                    bb.put((byte)0);
                }
                final boolean isContainer = item.isHollow() && !item.isSealedByPlayer();
                final boolean toolbeltIgnoreContents = item.getTemplateId() == 1346 || item.getTemplateId() == 1344;
                bb.putShort(ItemTypeUtilites.calcProfile(false, item.isBodyPartAttached(), isContainer, item.isNoDrop(), item.isTwoHanded(), item.isInventoryGroup(), item.doesShowSlopes(), toolbeltIgnoreContents));
                bb.put(item.getMaterial());
                bb.put(item.getTemperatureState(item.getTemperature()));
                bb.put(item.getRarity());
                bb.put(item.getAuxData());
                this.connection.flush();
                if (item.isHollow() && item.getItemCount() > 0 && !item.isSealedByPlayer() && (!item.isEmpty(false) || !item.isNoPut()) && item.isViewableBy(this.player)) {
                    this.sendHasMoreItems(inventoryWindow, item.getWurmId());
                }
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
        if (item.isDye() || item.getTemplateId() == 1392) {
            this.sendUpdateInventoryItemColor(item);
        }
    }
    
    public void sendUpdateInventoryItem(final Item item, final long inventoryWindow, final int price) {
        if (this.player != null && this.player.hasLink()) {
            try {
                boolean calcCorrectBulkWeight = item.isCrate();
                if (item.isBulkItem()) {
                    final Item parent = item.getParentOrNull();
                    if (parent != null && parent.isCrate()) {
                        calcCorrectBulkWeight = true;
                    }
                }
                byte[] tempStringArr = item.getName().getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)68);
                bb.putLong(inventoryWindow);
                bb.putLong(item.getWurmId());
                long parentId = -1L;
                if (item.getParentId() > 0L) {
                    parentId = item.getParentId();
                }
                else if (item.getOwnerId() != this.player.getWurmId()) {
                    parentId = item.getWurmId();
                }
                bb.putLong(parentId);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                if (item.descIsName()) {
                    tempStringArr = "".getBytes("UTF-8");
                }
                else {
                    tempStringArr = item.getDescription().getBytes("UTF-8");
                }
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putFloat(item.getQualityLevel());
                bb.putFloat(item.getDamage());
                final int weight = item.getFullWeight(calcCorrectBulkWeight);
                bb.putInt(weight);
                bb.put((byte)((item.color != -1) ? 1 : 0));
                if (item.color != -1) {
                    bb.put((byte)WurmColor.getColorRed(item.color));
                    bb.put((byte)WurmColor.getColorGreen(item.color));
                    bb.put((byte)WurmColor.getColorBlue(item.color));
                }
                bb.put((byte)((price >= 0) ? 1 : 0));
                if (price >= 0) {
                    bb.putInt(price);
                }
                int templateId = -10;
                if (item.isRepairable() && item.creationState == 0) {
                    if (!item.isNewbieItem() && !item.isChallengeNewbieItem()) {
                        templateId = MethodsItems.getImproveTemplateId(item);
                    }
                }
                else if (item.creationState != 0) {
                    templateId = MethodsItems.getItemForImprovement(MethodsItems.getImproveMaterial(item), item.creationState);
                }
                if (item.getTemplateId() == 1307 && item.getData1() <= 0) {
                    if (item.getAuxData() >= 65) {
                        templateId = 441;
                    }
                    else {
                        templateId = 97;
                    }
                }
                if (templateId != -10L) {
                    try {
                        final ItemTemplate temp = ItemTemplateFactory.getInstance().getTemplate(templateId);
                        bb.put((byte)1);
                        bb.putShort(temp.imageNumber);
                    }
                    catch (NoSuchTemplateException nst) {
                        bb.put((byte)0);
                    }
                }
                else {
                    bb.put((byte)0);
                }
                bb.put(item.getTemperatureState(item.getTemperature()));
                bb.put(item.getRarity());
                bb.put(item.getMaterial());
                bb.putShort(item.getImageNumber());
                this.connection.flush();
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
        if (item.isDye() || item.getTemplateId() == 1392) {
            this.sendUpdateInventoryItemColor(item);
        }
    }
    
    public void sendUpdateInventoryItem(final Item item) {
        long inventoryWindow = item.getTopParent();
        if (item.isInside(1333, 1334)) {
            final Item parentBags = item.getFirstParent(1333, 1334);
            this.sendUpdateInventoryItem(item, parentBags.getWurmId(), -1);
        }
        final Item parentWindow = item.recursiveParentCheck();
        if (parentWindow != null && parentWindow != item) {
            this.sendUpdateInventoryItem(item, parentWindow.getWurmId(), -1);
        }
        if (this.player == null) {
            logWarn("Player is null ", new Exception());
        }
        if (item.getOwnerId() == this.player.getWurmId()) {
            inventoryWindow = -1L;
        }
        this.sendUpdateInventoryItem(item, inventoryWindow, -1);
        if (item.isTraded()) {
            item.getTradeWindow().updateItem(item);
        }
    }
    
    public void sendUpdateKingdomId() {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)(-37));
            bb.put(this.player.getKingdomId());
            this.connection.flush();
        }
        catch (Exception ex) {
            this.player.setLink(false);
        }
    }
    
    public void sendUpdateInventoryItemData(final Item item) {
    }
    
    public void sendUpdateInventoryItemParent(final Item item) {
    }
    
    public void sendUpdateInventoryItemCustomName(final Item item) {
    }
    
    public void sendUpdateInventoryItemColor(final Item item) {
        if (this.player != null && this.player.hasLink()) {
            try {
                long inventoryWindow = item.getTopParent();
                if (item.getOwnerId() == this.player.getWurmId()) {
                    inventoryWindow = -1L;
                }
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-7));
                bb.putLong(inventoryWindow);
                bb.putLong(item.getWurmId());
                bb.put((byte)4);
                if (item.getTemplateId() == 1392) {
                    bb.put((byte)127);
                    bb.put((byte)127);
                    bb.put((byte)127);
                }
                else {
                    bb.put((byte)WurmColor.getColorRed(item.getColor()));
                    bb.put((byte)WurmColor.getColorGreen(item.getColor()));
                    bb.put((byte)WurmColor.getColorBlue(item.getColor()));
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendUpdateInventoryItemType(final Item item) {
    }
    
    public void sendUpdateInventoryItemPrice(final Item item) {
    }
    
    public void sendUpdateInventoryItemTemperature(final Item item) {
        if (this.player != null && this.player.hasLink()) {
            try {
                long inventoryWindow = item.getTopParent();
                if (item.getOwnerId() == this.player.getWurmId()) {
                    inventoryWindow = -1L;
                }
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-7));
                bb.putLong(inventoryWindow);
                bb.putLong(item.getWurmId());
                bb.put((byte)7);
                final byte tempState = item.getTemperatureState(item.getTemperature());
                bb.put(tempState);
                final byte[] name = item.getName().getBytes("UTF-8");
                bb.putShort((short)name.length);
                bb.put(name);
                this.connection.flush();
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
        if (item.isTraded()) {
            item.getTradeWindow().updateItem(item);
        }
    }
    
    public void sendRemoveFromInventory(final Item item, final long inventoryWindow) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-10));
                bb.putLong(inventoryWindow);
                bb.putLong(item.getWurmId());
                this.connection.flush();
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveFromInventory(final Item item) {
        if (this.player != null) {
            long inventoryWindow = item.getTopParent();
            if (item.getOwnerId() == this.player.getWurmId()) {
                inventoryWindow = -1L;
            }
            if (item.isInside(1333, 1334)) {
                final Item parentBags = item.getFirstParent(1333, 1334);
                this.sendRemoveFromInventory(item, parentBags.getWurmId());
            }
            final Item parentWindow = item.recursiveParentCheck();
            if (parentWindow != null && parentWindow != item) {
                this.sendRemoveFromInventory(item, parentWindow.getWurmId());
            }
            this.sendRemoveFromInventory(item, inventoryWindow);
        }
    }
    
    private void sendWho(final byte kingdom) {
        final Player[] players = Players.getInstance().getPlayers();
        this.player.getCommunicator().sendNormalServerMessage("Players in " + Kingdoms.getNameFor(kingdom) + ':');
        for (int x = 0; x < players.length; ++x) {
            if (players[x].getKingdomId() == kingdom) {
                this.player.getCommunicator().sendNormalServerMessage(players[x].getName());
            }
        }
        for (final ServerEntry entry : Servers.getAllServers()) {
            if (entry.id != Servers.localServer.id) {
                this.player.getCommunicator().sendNormalServerMessage(entry.name + ": " + entry.currentPlayers + "/" + entry.pLimit);
            }
        }
    }
    
    public void sendRemoveMapAnnotation(final long id, final byte type, final String server) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-43));
                bb.put((byte)1);
                bb.putLong(id);
                bb.put(type);
                final byte[] serverBytes = server.getBytes("UTF-8");
                bb.putShort((short)serverBytes.length);
                bb.put(serverBytes);
                this.connection.flush();
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    public void sendClearMapAnnotationsOfType(final byte type) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-43));
                bb.put((byte)3);
                bb.put(type);
                this.connection.flush();
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    private void sendMapAnnotationPermissions() {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-43));
                bb.put((byte)2);
                final boolean isMayor = this.player.isAllowedToEditVillageMap();
                final boolean isAllianceMayor = this.player.isAllowedToEditAllianceMap();
                bb.put((byte)(isMayor ? 1 : 0));
                bb.put((byte)(isAllianceMayor ? 1 : 0));
                this.connection.flush();
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    public void sendMapAnnotations(final MapAnnotation[] annotations) {
        if (annotations == null) {
            return;
        }
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-43));
                bb.put((byte)0);
                bb.putShort((short)annotations.length);
                for (int i = 0; i < annotations.length; ++i) {
                    bb.putLong(annotations[i].getId());
                    bb.put(annotations[i].getType());
                    final byte[] serverBytes = annotations[i].getServer().getBytes("UTF-8");
                    bb.putShort((short)serverBytes.length);
                    bb.put(serverBytes);
                    bb.putShort((short)annotations[i].getXPos());
                    bb.putShort((short)annotations[i].getYPos());
                    final byte[] nameBytes = annotations[i].getName().getBytes("UTF-8");
                    bb.putShort((short)nameBytes.length);
                    bb.put(nameBytes);
                    bb.put(annotations[i].getIcon());
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    public void sendMapInfo() {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                String nameToUse = Servers.localServer.mapname;
                if (nameToUse == null || nameToUse.isEmpty()) {
                    nameToUse = Servers.localServer.getName();
                }
                final byte[] tempStringArr = nameToUse.getBytes("UTF-8");
                bb.put((byte)(-45));
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put((byte)(Servers.localServer.isChallengeServer() ? 2 : (Servers.localServer.EPIC ? 1 : 0)));
                this.connection.flush();
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    public void sendOpenInventoryWindow(final long inventoryWindow, final String title) {
        if (this.player != null && this.player.hasLink()) {
            try {
                if (inventoryWindow == 5390755858690L) {
                    try {
                        if (this.player.getPower() <= 0) {
                            final Item container = Items.getItem(inventoryWindow);
                            if (container.getAuxData() != 23) {
                                String d = "";
                                if (this.player.getDeity() != null) {
                                    d = this.player.getDeity().getName();
                                }
                                final Item key = ItemFactory.createItem(794, 99.0f, d);
                                if (this.player.getDeity() != null) {
                                    key.setAuxData((byte)this.player.getDeity().getNumber());
                                }
                                key.setData1(577);
                                container.insertItem(key, true);
                                container.setAuxData((byte)23);
                            }
                        }
                    }
                    catch (Exception ex2) {}
                }
                final ByteBuffer bb = this.connection.getBuffer();
                final byte[] tempStringArr = title.getBytes("UTF-8");
                bb.put((byte)116);
                bb.putLong(inventoryWindow);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendOpenWindowByTypeID(final byte id) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-44));
                bb.put(id);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendOpenManageRecruitWindowWithData(final String description) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-42));
                bb.put((byte)0);
                final byte[] descriptionBytes = description.getBytes("UTF-8");
                bb.putShort((short)descriptionBytes.length);
                bb.put(descriptionBytes);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendOpenInventoryContainer(final long containerId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)58);
                bb.putLong(containerId);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
            }
        }
    }
    
    public boolean sendCloseInventoryWindow(final long inventoryWindow) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)120);
                bb.putLong(inventoryWindow);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
        try {
            return this.player.removeItemWatched(Items.getItem(inventoryWindow));
        }
        catch (NoSuchItemException nsi) {
            return true;
        }
    }
    
    public static int generateSoundSourceId(final long playerId) {
        return (int)(playerId & -1L);
    }
    
    public void sendNewCreature(final long id, final String name, final String model, final float x, final float y, final float z, final long onBridge, final float rot, final byte layer, final boolean onGround, final boolean floating, final boolean isSolid, final byte kingdomId, final long face, final byte blood, final boolean isUndead, final boolean isCopy, final byte modtype) {
        this.sendNewCreature(id, name, "", model, x, y, z, onBridge, rot, layer, onGround, floating, isSolid, kingdomId, face, blood, isUndead, isCopy, modtype);
    }
    
    public void sendNewCreature(final long id, final String name, final String hoverText, final String model, final float x, final float y, final float z, final long onBridge, float rot, final byte layer, final boolean onGround, final boolean floating, final boolean isHoverable, final byte kingdomId, final long face, final byte blood, final boolean isUndead, final boolean isCopy, final byte modtype) {
        if (this.player != null && this.player.hasLink()) {
            try {
                byte[] tempStringArr = model.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)108);
                bb.putLong(id);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put((byte)(isHoverable ? 1 : 0));
                bb.putFloat(y);
                bb.putFloat(x);
                bb.putLong(onBridge);
                if (rot > 10000.0f) {
                    rot %= 360.0f;
                }
                else if (rot < 0.0f) {
                    rot = Math.abs(rot % 360.0f);
                }
                bb.putFloat(rot);
                if (onGround) {
                    if (Structure.isGroundFloorAtPosition(x, y, layer == 0)) {
                        bb.putFloat(z + 0.1f);
                    }
                    else {
                        bb.putFloat(-3000.0f);
                    }
                }
                else {
                    bb.putFloat(z);
                }
                tempStringArr = name.getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                tempStringArr = hoverText.getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                if (floating) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                bb.put(layer);
                if ((WurmId.getType(id) == 0 || isCopy) && !isUndead) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                bb.put((byte)0);
                bb.put(kingdomId);
                bb.putLong(face);
                if ((WurmId.getType(id) == 0 || isCopy) && !isUndead) {
                    bb.putInt(Math.abs(generateSoundSourceId(id)));
                }
                bb.put(blood);
                bb.put(modtype);
                bb.put((byte)0);
                this.connection.flush();
                if (this.player.getVehicle() == id) {
                    this.player.getMovementScheme().resumeSpeedModifier();
                }
            }
            catch (NullPointerException np) {
                logWarn(this.player.getName() + ':' + np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    public void sendMoveCreature(final long id, final float x, final float y, final int rot, final boolean keepMoving) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)36);
                bb.putLong(id);
                bb.putFloat(y);
                bb.putFloat(x);
                bb.put((byte)rot);
                this.connection.flush();
            }
            catch (NullPointerException np) {
                logWarn(this.player.getName() + ':' + " creatureId: " + id + ' ' + np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    public void sendMoveCreatureAndSetZ(final long id, final float x, final float y, final float z, final int rot) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)72);
                bb.putLong(id);
                bb.putFloat(z);
                bb.putFloat(x);
                bb.put((byte)rot);
                bb.putFloat(y);
                this.connection.flush();
            }
            catch (NullPointerException np) {
                logWarn(this.player.getName() + ':' + " creatureId: " + id + ' ' + np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    public void sendCreatureChangedLayer(final long wurmid, final byte newlayer) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)30);
                bb.putLong(wurmid);
                bb.put(newlayer);
                this.connection.flush();
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    public void sendDeleteCreature(final long id) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)14);
                bb.putLong(id);
                this.connection.flush();
            }
            catch (NullPointerException np) {
                logWarn(this.player.getName() + ':' + " creatureId: " + id + ' ' + np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    public void sendTileStripFar(final short xStart, final short yStart, final int width, final int height) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)103);
                bb.putShort(xStart);
                bb.putShort(yStart);
                bb.putShort((short)width);
                bb.putShort((short)height);
                for (int x = 0; x < width; ++x) {
                    for (int y = 0; y < height; ++y) {
                        int xx = (xStart + x) * 16;
                        int yy = (yStart + y) * 16;
                        if (xx < 0 || xx >= 1 << Constants.meshSize || yy < 0 || yy >= 1 << Constants.meshSize) {
                            xx = 0;
                            yy = 0;
                        }
                        bb.putShort(Tiles.decodeHeight(Server.surfaceMesh.data[xx | yy << Constants.meshSize]));
                    }
                }
                for (int x = 0; x < width; ++x) {
                    final int ms = Constants.meshSize - 4;
                    for (int y2 = 0; y2 < height; ++y2) {
                        int xx2 = xStart + x;
                        int yy2 = yStart + y2;
                        if (xx2 < 0 || xx2 >= 1 << ms || yy2 < 0 || yy2 >= 1 << ms) {
                            xx2 = 0;
                            yy2 = 0;
                        }
                        bb.put(Server.surfaceMesh.getDistantTerrainTypes()[xx2 | yy2 << ms]);
                    }
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    public void sendTileDoor(final short tilex, final short tiley, final boolean openHole) throws IOException {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)73);
                bb.put((byte)(Features.Feature.SURFACEWATER.isEnabled() ? 1 : 0));
                bb.put((byte)(this.player.isSendExtraBytes() ? 1 : 0));
                bb.putShort(tiley);
                bb.putShort((short)1);
                bb.putShort((short)1);
                bb.putShort(tilex);
                if (openHole) {
                    bb.putInt(Tiles.encode(Tiles.decodeHeight(Server.surfaceMesh.data[tilex | tiley << Constants.meshSize]), Tiles.Tile.TILE_HOLE.id, (byte)0));
                }
                else {
                    bb.putInt(Server.surfaceMesh.data[tilex | tiley << Constants.meshSize]);
                }
                if (Features.Feature.SURFACEWATER.isEnabled()) {
                    bb.putShort((short)Water.getSurfaceWater(tilex, tiley));
                }
                if (this.player.isSendExtraBytes()) {
                    bb.put(Server.getClientSurfaceFlags(tilex, tiley));
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
                throw new IOException(this.player.getName() + ':' + ex.getMessage());
            }
        }
    }
    
    public void sendTileStrip(final short xStart, final short yStart, final int width, final int height) throws IOException {
        if (this.player != null && this.player.hasLink()) {
            if (width < 1 || height < 1) {
                logInfo(this.player.getName() + ':' + " Width=" + width + ", Height=" + height, new Exception("Bad CMD_TILESTRIP params."));
            }
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)73);
                bb.put((byte)(Features.Feature.SURFACEWATER.isEnabled() ? 1 : 0));
                bb.put((byte)(this.player.isSendExtraBytes() ? 1 : 0));
                bb.putShort(yStart);
                bb.putShort((short)width);
                bb.putShort((short)height);
                bb.putShort(xStart);
                for (int x = 0; x < width; ++x) {
                    for (int y = 0; y < height; ++y) {
                        int tempTileX = xStart + x;
                        int tempTileY = yStart + y;
                        if (tempTileX < 0 || tempTileX >= 1 << Constants.meshSize || tempTileY < 0 || tempTileY >= 1 << Constants.meshSize) {
                            tempTileX = 0;
                            tempTileY = 0;
                        }
                        bb.putInt(Server.surfaceMesh.data[tempTileX | tempTileY << Constants.meshSize]);
                        if (Features.Feature.SURFACEWATER.isEnabled()) {
                            bb.putShort((short)Water.getSurfaceWater(tempTileX, tempTileY));
                        }
                        if (this.player.isSendExtraBytes()) {
                            bb.put(Server.getClientSurfaceFlags(tempTileX, tempTileY));
                        }
                    }
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                this.player.setLink(false);
                throw new IOException(this.player.getName() + ':' + ex.getMessage());
            }
        }
    }
    
    public void sendCaveStrip(final short xStart, final short yStart, final int width, final int height) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)102);
                bb.put((byte)(Features.Feature.CAVEWATER.isEnabled() ? 1 : 0));
                bb.put((byte)(this.player.isSendExtraBytes() ? 1 : 0));
                bb.putShort(xStart);
                bb.putShort(yStart);
                bb.putShort((short)width);
                bb.putShort((short)height);
                final boolean onSurface = this.player.isOnSurface();
                for (int x = 0; x < width; ++x) {
                    for (int y = 0; y < height; ++y) {
                        int xx = xStart + x;
                        int yy = yStart + y;
                        if (xx < 0 || xx >= Zones.worldTileSizeX || yy < 0 || yy >= Zones.worldTileSizeY) {
                            bb.putInt(Communicator.emptyRock);
                            xx = 0;
                            yy = 0;
                        }
                        else if (!onSurface) {
                            bb.putInt(Server.caveMesh.data[xx | yy << Constants.meshSize]);
                        }
                        else if (Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.data[xx | yy << Constants.meshSize]))) {
                            bb.putInt(this.getDummyWall(xx, yy));
                        }
                        else {
                            bb.putInt(Server.caveMesh.data[xx | yy << Constants.meshSize]);
                        }
                        if (Features.Feature.CAVEWATER.isEnabled()) {
                            bb.putShort((short)Water.getCaveWater(xx, yy));
                        }
                        if (this.player.isSendExtraBytes()) {
                            bb.put(Server.getClientCaveFlags(xx, yy));
                        }
                    }
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    private int getDummyWall(final int tilex, final int tiley) {
        return Tiles.encode(Tiles.decodeHeight(Server.caveMesh.data[tilex | tiley << Constants.meshSize]), Tiles.Tile.TILE_CAVE_WALL.id, Tiles.decodeData(Server.caveMesh.data[tilex | tiley << Constants.meshSize]));
    }
    
    private boolean isCaveWallHidden(final int tilex, final int tiley) {
        return this.isCaveWallSolid(tilex, tiley) && this.isCaveWallSolid(tilex, tiley - 1) && this.isCaveWallSolid(tilex + 1, tiley) && this.isCaveWallSolid(tilex, tiley + 1) && this.isCaveWallSolid(tilex - 1, tiley);
    }
    
    private boolean isCaveWallSolid(final int tilex, final int tiley) {
        return tilex < 0 || tilex >= Zones.worldTileSizeX || tiley < 0 || tiley >= Zones.worldTileSizeY || Tiles.isSolidCave(Tiles.decodeType(Server.caveMesh.data[tilex | tiley << Constants.meshSize]));
    }
    
    public void sendUpdateSelectBar(final long toUpdate, final boolean keepSelection) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer buffer = this.connection.getBuffer();
                buffer.put((byte)(-23));
                if (!keepSelection) {
                    buffer.put((byte)1);
                }
                else {
                    buffer.put((byte)2);
                }
                buffer.putLong(toUpdate);
                this.connection.flush();
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAvailableSelectBarActions(final byte requestId, final List<ActionEntry> availableActions) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-23));
                bb.put((byte)0);
                bb.put(requestId);
                bb.put((byte)availableActions.size());
                for (final ActionEntry entry : availableActions) {
                    bb.putShort(entry.getNumber());
                    if (entry.isQuickSkillLess()) {
                        bb.put((byte)1);
                    }
                    else {
                        bb.put((byte)0);
                    }
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAvailableActions(final byte requestId, final List<ActionEntry> availableActions, final String helpstring) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)20);
                bb.put(requestId);
                bb.put((byte)availableActions.size());
                for (final ActionEntry entry : availableActions) {
                    bb.putShort(entry.getNumber());
                    final String actionString = entry.getActionString();
                    final byte[] tempStringArr = actionString.getBytes("UTF-8");
                    bb.put((byte)tempStringArr.length);
                    bb.put(tempStringArr);
                    if (entry.isQuickSkillLess()) {
                        bb.put((byte)1);
                    }
                    else {
                        bb.put((byte)0);
                    }
                }
                final byte[] tempStringArr2 = helpstring.getBytes("UTF-8");
                bb.put((byte)tempStringArr2.length);
                bb.put(tempStringArr2);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendItem(final Item item, final long creatureId, final boolean onGroundLevel) {
        if (this.player != null && this.player.hasLink() && item.getTemplateId() != 520) {
            try {
                final long id = item.getWurmId();
                final boolean insidePlaceableContainer = item.isInsidePlaceableContainer();
                final ByteBuffer bb = this.connection.getBuffer();
                if (creatureId <= 0L) {
                    bb.put((byte)(-9));
                }
                else {
                    bb.put((byte)75);
                    bb.putLong(creatureId);
                }
                bb.putLong(id);
                if (insidePlaceableContainer) {
                    bb.putFloat(item.getPosXRaw());
                    bb.putFloat(item.getPosYRaw());
                }
                else {
                    bb.putFloat(item.getPosX());
                    bb.putFloat(item.getPosY());
                }
                bb.putFloat(item.getRotation());
                if (insidePlaceableContainer) {
                    bb.putFloat(item.getPosZRaw());
                }
                else if (item.isFloating() && item.getPosZ() <= 0.0f) {
                    if (item.getCurrentQualityLevel() < 10.0f) {
                        bb.putFloat(-3000.0f);
                    }
                    else {
                        bb.putFloat(0.0f);
                    }
                }
                else if (item.isArtifact()) {
                    bb.putFloat(item.getPosZ() + 0.05f);
                }
                else if (item.getTemplate().hovers() && item.getPosZ() > 0.0f) {
                    bb.putFloat(item.getPosZ());
                }
                else if (item.getFloorLevel() > 0 || !onGroundLevel || item.onBridge() != -10L) {
                    bb.putFloat(item.getPosZ());
                }
                else {
                    bb.putFloat(-3000.0f);
                }
                byte[] tempStringArr = item.getName().getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                tempStringArr = item.getHoverText().getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                tempStringArr = item.getModelName().getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put((byte)(item.isOnSurface() ? 0 : -1));
                bb.put(item.getMaterial());
                if (item.descIsName()) {
                    tempStringArr = "".getBytes("UTF-8");
                }
                else {
                    tempStringArr = item.getDescription().getBytes("UTF-8");
                }
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putShort(item.getImageNumber());
                if (item.getTemplateId() == 177) {
                    bb.put((byte)0);
                }
                else {
                    bb.put((byte)1);
                    bb.putFloat(item.getQualityLevel());
                    bb.putFloat(item.getDamage());
                }
                bb.putFloat(item.getSizeMod());
                bb.putLong(item.onBridge());
                bb.put(item.getRarity());
                bb.put((byte)(insidePlaceableContainer ? 2 : ((item.getTemplate().hasViewableSubItems() && item.getTemplateId() != 1437) ? 1 : 0)));
                if (insidePlaceableContainer) {
                    bb.putLong(item.getParentId());
                }
                if (item.hasExtraData()) {
                    bb.put((byte)1);
                    bb.putInt(item.getExtra1());
                    bb.putInt(item.getExtra2());
                }
                else {
                    bb.put((byte)0);
                }
                this.connection.flush();
                if (Features.Feature.HIGHWAYS.isEnabled() && item.getTemplateId() == 1112) {
                    this.sendWaystoneData(item);
                }
            }
            catch (Exception ex) {
                logWarn("Failed to send item: " + this.player.getName() + ':' + item.getWurmId() + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendChangeModelName(final Item item) {
        if (this.player != null && this.player.hasLink()) {
            if (Features.Feature.HIGHWAYS.isEnabled() && item.getTemplateId() == 1112) {
                this.sendWaystoneData(item);
                return;
            }
            try {
                final long id = item.getWurmId();
                final byte[] temp = item.getModelName().getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-48));
                bb.putLong(id);
                bb.put((byte)temp.length);
                bb.put(temp);
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn("Failed to change model for item: " + this.player.getName() + ':' + item.getWurmId() + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendWaystoneData(final Item waystone) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final Node node = Routes.getNode(waystone.getWurmId());
                if (node != null) {
                    final byte dirs = waystone.getAuxData();
                    final byte count = (byte)MethodsHighways.numberOfSetBits(dirs);
                    final ByteBuffer bb = this.connection.getBuffer();
                    bb.put((byte)(-56));
                    bb.putLong(waystone.getWurmId());
                    bb.put(count);
                    this.sendWaystoneDirInfo(bb, node, dirs, (byte)1);
                    this.sendWaystoneDirInfo(bb, node, dirs, (byte)2);
                    this.sendWaystoneDirInfo(bb, node, dirs, (byte)4);
                    this.sendWaystoneDirInfo(bb, node, dirs, (byte)8);
                    this.sendWaystoneDirInfo(bb, node, dirs, (byte)16);
                    this.sendWaystoneDirInfo(bb, node, dirs, (byte)32);
                    this.sendWaystoneDirInfo(bb, node, dirs, (byte)64);
                    this.sendWaystoneDirInfo(bb, node, dirs, (byte)(-128));
                    this.connection.flush();
                }
            }
            catch (Exception ex) {
                logWarn("Failed to send waystone data: " + this.player.getName() + ':' + waystone.getWurmId() + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private void sendWaystoneDirInfo(final ByteBuffer bb, final Node node, final byte dirs, final byte dir) throws Exception {
        if (MethodsHighways.hasLink(dirs, dir)) {
            final byte cd = MethodsHighways.convertLink(dir);
            byte colour = 0;
            short pathDistance = -1;
            final Route route = node.getRoute(dir);
            if (route == null) {
                colour = 1;
            }
            else {
                pathDistance = route.isOnHighwayPath(this.player);
                colour = 3;
                if (pathDistance > -1) {
                    colour |= 0x10;
                }
            }
            String village = "";
            short distance = 0;
            if (pathDistance > -1) {
                village = this.player.getHighwayPathDestination();
                distance = pathDistance;
            }
            else {
                final ClosestVillage closestVillage = node.getClosestVillage(dir);
                if (closestVillage != null) {
                    village = closestVillage.getName();
                    distance = closestVillage.getDistance();
                }
            }
            bb.put(cd);
            bb.put(colour);
            sendByteStringLength(village, bb);
            bb.putShort(distance);
        }
    }
    
    public boolean sendShowLinks(final boolean markerType, final HighwayPos currentHighwayPos, final byte[] glows) {
        if (this.player != null && this.player.hasLink() && Features.Feature.HIGHWAYS.isEnabled()) {
            final HighwayPos westHighwayPos = MethodsHighways.getNewHighwayPosLinked(currentHighwayPos, (byte)64);
            final HighwayPos northwestHighwayPos = MethodsHighways.getNewHighwayPosLinked(currentHighwayPos, (byte)(-128));
            final HighwayPos northHighwayPos = MethodsHighways.getNewHighwayPosLinked(currentHighwayPos, (byte)1);
            if (westHighwayPos == null || northwestHighwayPos == null || northHighwayPos == null) {
                return false;
            }
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-57));
                bb.put((byte)0);
                bb.put((byte)4);
                this.showHighwayPos(bb, currentHighwayPos);
                this.showHighwayPos(bb, westHighwayPos);
                this.showHighwayPos(bb, northwestHighwayPos);
                this.showHighwayPos(bb, northHighwayPos);
                for (int x = 0; x < glows.length; ++x) {
                    bb.put(glows[x]);
                }
                this.connection.flush();
                return true;
            }
            catch (Exception ex) {
                logWarn("Failed to send the link information: " + this.player.getName() + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
        return false;
    }
    
    public boolean sendShowProtection(final boolean markerType, final HighwayPos currentHighwayPos, final HighwayPos[] protectedHPs) {
        if (this.player != null && this.player.hasLink() && Features.Feature.HIGHWAYS.isEnabled()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-57));
                bb.put((byte)2);
                bb.put((byte)(protectedHPs.length + 1));
                this.showHighwayPos(bb, currentHighwayPos);
                for (int x = 0; x < protectedHPs.length; ++x) {
                    this.showHighwayPos(bb, protectedHPs[x]);
                }
                this.connection.flush();
                return true;
            }
            catch (Exception ex) {
                logWarn("Failed to send the protection information: " + this.player.getName() + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
        return false;
    }
    
    private void showHighwayPos(final ByteBuffer bb, final HighwayPos highwayPos) {
        bb.putShort((short)highwayPos.getTilex());
        bb.putShort((short)highwayPos.getTiley());
        bb.put((byte)(highwayPos.isOnSurface() ? 1 : 0));
        bb.putLong(highwayPos.getBridgeId());
        bb.put((byte)highwayPos.getFloorLevel());
    }
    
    public void sendHideLinks() {
        if (this.player != null && this.player.hasLink() && Features.Feature.HIGHWAYS.isEnabled()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-57));
                bb.put((byte)1);
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn("Failed to hide the link and protection: " + this.player.getName() + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRename(final Item item, final String newName, final String newModelName) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final long id = item.getWurmId();
                byte[] tempStringArr = newName.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)44);
                bb.putLong(id);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                tempStringArr = newModelName.getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put(item.getMaterial());
                if (item.descIsName()) {
                    tempStringArr = "".getBytes("UTF-8");
                }
                else {
                    tempStringArr = item.getDescription().getBytes("UTF-8");
                }
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putShort(item.getImageNumber());
                bb.put(item.getRarity());
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn("Failed to rename item: " + this.player.getName() + ':' + item.getWurmId() + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveItem(final Item item) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)10);
                bb.putLong(item.getWurmId());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddSkill(final int id, final int parentSkillId, final String name, final float value, final float maxValue, final int affinities) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = name.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)124);
                bb.putLong(BigInteger.valueOf(parentSkillId).shiftLeft(32).longValue() + 18L);
                bb.putLong(BigInteger.valueOf(id).shiftLeft(32).longValue() + 18L);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putFloat(value);
                bb.putFloat(maxValue);
                if (this.player.getPaymentExpire() > 0L) {
                    bb.put((byte)affinities);
                }
                else {
                    bb.put((byte)0);
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " skillId: " + id + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendUpdateSkill(final int id, final float value, final int affinities) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)66);
                bb.putLong((id << 32) + 18L);
                bb.putFloat(value);
                if (this.player.getPaymentExpire() > 0L) {
                    bb.put((byte)affinities);
                }
                else {
                    bb.put((byte)0);
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " skillId: " + id + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendActionControl(final long creatureId, final String actionString, final boolean start, final int timeLeft) {
        if (this.player != null && this.player.hasLink()) {
            try {
                byte[] tempStringArr;
                if (start) {
                    tempStringArr = actionString.getBytes("UTF-8");
                }
                else {
                    tempStringArr = "".getBytes("UTF-8");
                }
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-12));
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putShort((short)Math.min(timeLeft, 65535));
                bb.putLong(creatureId);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " creatureId: " + creatureId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddEffect(final long id, final short type, final float x, final float y, final float z, final byte layer) {
        this.sendAddEffect(id, -10L, type, x, y, z, layer, null, -1.0f, 0.0f);
    }
    
    public void sendAddEffect(final long id, final short type, final float x, final float y, final float z, final byte layer, final String effectString, final float timeout, final float rotationOffset) {
        this.sendAddEffect(id, -10L, type, x, y, z, layer, effectString, timeout, rotationOffset);
    }
    
    public void sendAddEffect(final long id, final long target, final short type, final float x, final float y, final float z, final byte layer, final String effectString, final float timeout, final float rotationOffset) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)64);
                bb.putLong(id);
                bb.putShort(type);
                bb.putFloat(x);
                bb.putFloat(y);
                bb.putFloat(z);
                bb.put(layer);
                if (type == 27 && effectString != null) {
                    final byte[] tempStringArr = effectString.getBytes("UTF-8");
                    bb.put((byte)tempStringArr.length);
                    bb.put(tempStringArr);
                    bb.putFloat(timeout);
                    bb.putFloat(rotationOffset);
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " effectId: " + id + " targetId: " + target + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddComplexEffect(final long id, final long target, final short type, final float x, final float y, final float z, final byte layer, final float radiusMeters, final float lengthMeters, final int direction, final byte kingdomTemplateId, final byte epicEntityId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)95);
                bb.putLong(id);
                bb.putLong(target);
                bb.putShort(type);
                bb.putFloat(x);
                bb.putFloat(y);
                bb.putFloat(z);
                bb.put(layer);
                bb.putFloat(radiusMeters);
                bb.putFloat(lengthMeters);
                bb.putInt(direction);
                bb.put(kingdomTemplateId);
                bb.put(epicEntityId);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " effectId: " + id + " targetId: " + target + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveEffect(final long id) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)37);
                bb.putLong(id);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " effectId: " + id + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    protected void sendStamina(int stamina, final int damage) {
        if (this.player != null && this.player.hasLink() && !this.player.isTransferring()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)90);
                stamina = (int)((stamina & 0xFFFE) | (this.newSeed >> this.newSeedPointer++ & 0x1L));
                bb.putShort((short)stamina);
                bb.putShort((short)damage);
                this.connection.flush();
                if (this.newSeedPointer == 32) {
                    this.connection.encryptRandom.setSeed(this.newSeed & -1L);
                    this.connection.changeProtocol(this.newSeed);
                    this.newSeedPointer = 0;
                    this.newSeed = (Server.rand.nextInt() & Integer.MAX_VALUE);
                }
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " stamina: " + stamina + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    protected void sendThirst(final int thirst) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)105);
                bb.putShort((short)thirst);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " thirst: " + thirst + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendHunger(final int hunger, final float nutrition, final float calories, final float carbs, final float fats, final float proteins) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)61);
                bb.putShort((short)hunger);
                bb.put((byte)(nutrition * 100.0f));
                if (!this.player.hasFlag(52)) {
                    bb.put((byte)calories);
                    bb.put((byte)carbs);
                    bb.put((byte)fats);
                    bb.put((byte)proteins);
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " hunger: " + hunger + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    protected void sendWeight(final byte weight) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)5);
                bb.put(weight);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    protected void sendSpeedModifier(final float speedModifier) {
        if (this.player != null && this.player.hasLink()) {
            try {
                ++this.numberSpeedModsSent;
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)32);
                bb.putFloat(speedModifier);
                this.connection.flush();
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    protected void sendTimeLeft(final short tenthOfSeconds) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)87);
                bb.putShort(tenthOfSeconds);
                this.connection.flush();
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    public void sendSingleBuildMarker(final long structureId, final int tilex, final int tiley, final byte layer) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)96);
                bb.putLong(structureId);
                bb.put(layer);
                bb.put((byte)1);
                bb.putShort((short)tilex);
                bb.putShort((short)tiley);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " structureId: " + structureId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendMultipleBuildMarkers(final long structureId, final VolaTile[] tiles, final byte layer) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)96);
                bb.putLong(structureId);
                bb.put(layer);
                bb.put((byte)tiles.length);
                for (int x = 0; x < tiles.length; ++x) {
                    bb.putShort((short)tiles[x].getTileX());
                    bb.putShort((short)tiles[x].getTileY());
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " structureId: " + structureId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddStructure(final String name, final short centerTilex, final short centerTiley, final long structureId, final byte structureType, final byte layer) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)112);
                bb.putLong(structureId);
                bb.put(structureType);
                final byte[] tempStringArr = name.getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putShort(centerTiley);
                bb.putShort(centerTilex);
                bb.put(layer);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " structureId: " + structureId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveStructure(final long structureId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)48);
                bb.putLong(structureId);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " structureId: " + structureId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    protected void sendUpdateFence(final Fence fence) {
        this.sendRemoveFence(fence);
        this.sendAddFence(fence);
    }
    
    public void sendRemoveFloor(final long structureId, final Floor floor) {
        if (this.player == null) {
            return;
        }
        if (!this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)77);
            bb.putLong(structureId);
            bb.putShort((short)floor.getTileX());
            bb.putShort((short)floor.getTileY());
            bb.putShort((short)floor.getHeightOffset());
            bb.put(floor.getLayer());
            bb.put(floor.getType().getCode());
            this.connection.flush();
        }
        catch (Exception ex) {
            logInfo(this.player.getName() + ':' + " structureId: " + structureId + ' ' + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void sendAddFloor(final long structureId, final Floor floor) {
        if (this.player == null) {
            return;
        }
        if (!this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)82);
            bb.putLong(structureId);
            bb.putShort((short)floor.getTileX());
            bb.putShort((short)floor.getTileY());
            bb.putShort((short)floor.getHeightOffset());
            bb.put(floor.getType().getCode());
            bb.put(floor.getMaterial().getCode());
            bb.put(floor.getFloorState().getCode());
            bb.put(floor.getLayer());
            bb.put(floor.getDir());
            this.connection.flush();
        }
        catch (Exception ex) {
            logInfo(this.player.getName() + ':' + " structureId: " + structureId + ' ' + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void sendRemoveBridgePart(final long structureId, final BridgePart bridgePart) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-25));
                bb.put((byte)4);
                bb.putLong(structureId);
                bb.putShort((short)bridgePart.getTileX());
                bb.putShort((short)bridgePart.getTileY());
                bb.putShort((short)bridgePart.getRealHeight());
                bb.put(bridgePart.getLayer());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " structureId: " + structureId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddBridgePart(final long structureId, final BridgePart bridgePart) {
        if (this.player == null) {
            return;
        }
        if (!this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)(-25));
            bb.put((byte)3);
            bb.putLong(structureId);
            bb.putShort((short)bridgePart.getTileX());
            bb.putShort((short)bridgePart.getTileY());
            bb.putShort((short)bridgePart.getRealHeight());
            bb.put(bridgePart.getType().getCode());
            bb.put(bridgePart.getMaterial().getCode());
            bb.put(bridgePart.getBridgePartState().getCode());
            bb.put(bridgePart.getDir());
            bb.put(bridgePart.getSlope());
            bb.put(bridgePart.getRoadType());
            bb.put(bridgePart.getLayer());
            this.connection.flush();
        }
        catch (Exception ex) {
            logInfo(this.player.getName() + ':' + " structureId: " + structureId + ' ' + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void sendAddWall(final long structureId, final Wall wall) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)49);
                bb.putLong(structureId);
                bb.putShort((short)Math.min(wall.getStartY(), wall.getEndY()));
                bb.putShort((short)Math.min(wall.getStartX(), wall.getEndX()));
                if (wall.isHorizontal()) {
                    bb.put((byte)0);
                }
                else {
                    bb.put((byte)1);
                }
                if (wall.isFinished()) {
                    bb.put(wall.getType().value);
                }
                else {
                    bb.put(StructureTypeEnum.PLAN.value);
                }
                final byte[] tempStringArr = wall.getMaterialString().getBytes();
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put((byte)((wall.getColor() != -1) ? 1 : 0));
                if (wall.getColor() != -1) {
                    bb.put((byte)WurmColor.getColorRed(wall.getColor()));
                    bb.put((byte)WurmColor.getColorGreen(wall.getColor()));
                    bb.put((byte)WurmColor.getColorBlue(wall.getColor()));
                }
                bb.putShort((short)wall.getHeight());
                bb.put(wall.getLayer());
                bb.put((byte)(wall.getWallOrientationFlag() ? 1 : 0));
                final Door door = wall.getDoor();
                if (door != null && !door.getName().isEmpty()) {
                    sendByteStringLength(door.getName(), bb);
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " structureId: " + structureId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendPassable(final boolean passable, final Door door) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final Wall wall = door.getWall();
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)125);
                bb.putLong(door.getStructureId());
                bb.putShort((short)Math.min(wall.getStartX(), wall.getEndX()));
                bb.putShort((short)Math.min(wall.getStartY(), wall.getEndY()));
                if (passable) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                if (wall.isHorizontal()) {
                    bb.put((byte)0);
                }
                else {
                    bb.put((byte)1);
                }
                bb.putShort((short)(door.getFloorLevel() * 30));
                bb.put(door.getLayer());
                this.connection.flush();
            }
            catch (NoSuchWallException nsw) {}
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendOpenDoor(final Door door) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final Wall wall = door.getWall();
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)122);
                bb.putLong(door.getStructureId());
                bb.putShort((short)Math.min(wall.getStartX(), wall.getEndX()));
                bb.putShort((short)Math.min(wall.getStartY(), wall.getEndY()));
                if (wall.isHorizontal()) {
                    bb.put((byte)0);
                }
                else {
                    bb.put((byte)1);
                }
                bb.putShort((short)(door.getFloorLevel() * 30));
                bb.put(door.getLayer());
                bb.put((byte)(door.canBeOpenedBy(this.getPlayer(), false) ? 1 : 0));
                this.connection.flush();
            }
            catch (NoSuchWallException nsw) {}
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendCloseDoor(final Door door) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final Wall wall = door.getWall();
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)127);
                bb.putLong(door.getStructureId());
                bb.putShort((short)Math.min(wall.getStartX(), wall.getEndX()));
                bb.putShort((short)Math.min(wall.getStartY(), wall.getEndY()));
                if (wall.isHorizontal()) {
                    bb.put((byte)0);
                }
                else {
                    bb.put((byte)1);
                }
                bb.putShort((short)(door.getFloorLevel() * 30));
                bb.put(door.getLayer());
                this.connection.flush();
            }
            catch (NoSuchWallException nsw) {}
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendStartMoving() {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-28));
                this.connection.flush();
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    public void sendBml(final int width, final int height, final boolean resizeable, final boolean closeable, final String content, final int r, final int g, final int b, final String title) {
        this.sendBml((short)(-10), width, height, 0.5f, 0.5f, resizeable, closeable, content, r, g, b, title);
    }
    
    public void sendBml(final int width, final int height, final float xLoc, final float yLoc, final boolean resizeable, final boolean closeable, final String content, final int r, final int g, final int b, final String title) {
        this.sendBml((short)(-10), width, height, xLoc, yLoc, resizeable, closeable, content, r, g, b, title);
    }
    
    public void sendBml(final short id, final int width, final int height, final float xLoc, final float yLoc, final boolean resizeable, final boolean closeable, final String content, final int r, final int g, final int b, final String title) {
        if (this.player != null && this.player.hasLink()) {
            try {
                byte[] tempStringArr = title.getBytes("UTF-8");
                ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)106);
                bb.put((byte)1);
                bb.putShort(id);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putShort((short)width);
                bb.putShort((short)height);
                bb.putFloat(xLoc);
                bb.putFloat(yLoc);
                tempStringArr = content.getBytes("UTF-8");
                bb.put((byte)(resizeable ? 1 : 0));
                bb.put((byte)(closeable ? 1 : 0));
                bb.put((byte)r);
                bb.put((byte)g);
                bb.put((byte)b);
                final int gap = 20;
                final int maxCap = bb.capacity() - 20;
                int maxParts = 1;
                if (bb.remaining() - 20 >= tempStringArr.length) {
                    bb.put((byte)maxParts);
                    bb.putShort((short)tempStringArr.length);
                    bb.put(tempStringArr);
                    this.connection.flush();
                }
                else {
                    int pLen = bb.remaining() - 20;
                    int left = tempStringArr.length - pLen;
                    maxParts = 2;
                    while (true) {
                        left -= maxCap;
                        if (left < 0) {
                            break;
                        }
                        ++maxParts;
                    }
                    int pStart = 0;
                    bb.put((byte)maxParts);
                    bb.putShort((short)pLen);
                    bb.put(tempStringArr, pStart, pLen);
                    this.connection.flush();
                    for (int pNo = 2; pNo <= maxParts; ++pNo) {
                        pStart += pLen;
                        pLen = Math.min(maxCap, tempStringArr.length - pStart);
                        bb = this.connection.getBuffer();
                        bb.put((byte)106);
                        bb.put((byte)pNo);
                        bb.putShort((short)pLen);
                        bb.put(tempStringArr, pStart, pLen);
                        this.connection.flush();
                    }
                }
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendChangeStructureName(final long structureId, final String newName) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = newName.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)47);
                bb.put((byte)0);
                bb.putLong(structureId);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " structureId: " + structureId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendChangeModelName(final long creatureId, final String newName) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = newName.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)47);
                bb.put((byte)3);
                bb.putLong(creatureId);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " modelname: " + newName + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendUseBinoculars() {
        this.sendClientFeature((byte)1, true);
    }
    
    public void sendStopUseBinoculars() {
        this.sendClientFeature((byte)1, false);
    }
    
    public void sendToggle(final int toggle, final boolean set) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)62);
                bb.put((byte)toggle);
                bb.put((byte)(set ? 1 : 0));
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo("Problem sending toggle (" + toggle + ',' + set + ") to " + this.player.getName() + " due to :" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendBridgeId(final long creatureId, final long bridgeId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-21));
                bb.putLong(creatureId);
                bb.putLong(bridgeId);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " problem sending bridge Id due to :" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendTeleport(final boolean aLocal) {
        this.sendTeleport(aLocal, true, (byte)0);
    }
    
    public void sendTeleport(final boolean aLocal, final boolean disembark, final byte startCommandingType) {
        if (this.player != null && this.player.hasLink()) {
            this.player.setTeleportCounter(this.player.getTeleportCounter() + 1);
            this.player.setTeleporting(true);
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)51);
                bb.putFloat(this.player.getStatus().getPositionX());
                bb.putFloat(this.player.getStatus().getPositionY());
                bb.putFloat(this.player.getStatus().getPositionZ());
                bb.putFloat(this.player.getStatus().getRotation());
                bb.put((byte)(aLocal ? 1 : 0));
                bb.put((byte)(this.player.isOnSurface() ? 0 : -1));
                bb.put((byte)(disembark ? 1 : 0));
                bb.put(startCommandingType);
                bb.putInt(this.player.getTeleportCounter());
                this.connection.flush();
                this.currentmove = null;
                this.moves = 1;
                this.receivedTicks = false;
                this.sendWeather();
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    public void sendStartTrading(final Creature opponent) {
        final Trade trade = this.player.getTrade();
        if (trade != null && this.player != null && this.player.hasLink()) {
            try {
                final String name = opponent.getName();
                final byte[] tempStringArr = name.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)119);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                if (trade.creatureOne == this.player) {
                    bb.putLong(1L);
                    bb.putLong(2L);
                    bb.putLong(3L);
                    bb.putLong(4L);
                }
                else {
                    bb.putLong(2L);
                    bb.putLong(1L);
                    bb.putLong(4L);
                    bb.putLong(3L);
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendCloseTradeWindow() {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)121);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private void reallySendTradeAgree() {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)42);
                bb.put((byte)(this.tradeAgreeToSend ? 1 : 0));
                this.connection.flush();
                this.shouldSendTradeAgree = false;
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendTradeAgree(final Creature agreer, final boolean agree) {
        boolean me = false;
        if (this.player.equals(agreer)) {
            me = true;
        }
        if (me && agree) {
            return;
        }
        this.shouldSendTradeAgree = true;
        this.tradeAgreeToSend = agree;
    }
    
    public void sendTradeChanged(final int id) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)91);
                bb.putInt(id);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddFence(final Fence fence) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)12);
                bb.putShort((short)fence.getTileX());
                bb.putShort((short)fence.getTileY());
                bb.put(fence.getDir().getCode());
                bb.putShort((short)fence.getType().ordinal());
                bb.put((byte)(fence.isFinished() ? 1 : 0));
                bb.put((byte)((fence.getColor() != -1) ? 1 : 0));
                if (fence.getColor() != -1) {
                    bb.put((byte)WurmColor.getColorRed(fence.getColor()));
                    bb.put((byte)WurmColor.getColorGreen(fence.getColor()));
                    bb.put((byte)WurmColor.getColorBlue(fence.getColor()));
                }
                bb.putShort((short)fence.getHeightOffset());
                bb.put(fence.getLayer());
                final FenceGate gate = FenceGate.getFenceGate(fence.getId());
                if (gate != null && !gate.getName().isEmpty()) {
                    sendByteStringLength(gate.getName(), bb);
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " adding fence: " + fence + " :" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveFence(final Fence fence) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)13);
                bb.putShort((short)fence.getTileX());
                bb.putShort((short)fence.getTileY());
                bb.put(fence.getDir().getCode());
                bb.putShort((short)fence.getHeightOffset());
                bb.put(fence.getLayer());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " problem removing fence: " + fence + " due to :" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendOpenFence(final Fence fence, final boolean passable, final boolean changePassable) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)83);
                bb.putShort((short)fence.getTileX());
                bb.putShort((short)fence.getTileY());
                bb.put(fence.getDir().getCode());
                bb.put((byte)1);
                if (changePassable) {
                    bb.put((byte)(passable ? 1 : 0));
                }
                else {
                    bb.put((byte)2);
                }
                bb.putShort((short)fence.getHeightOffset());
                bb.put(fence.getLayer());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " problem opening fence: " + fence + " due to :" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendCloseFence(final Fence fence, final boolean passable, final boolean changePassable) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)83);
                bb.putShort((short)fence.getTileX());
                bb.putShort((short)fence.getTileY());
                bb.put(fence.getDir().getCode());
                bb.put((byte)0);
                if (changePassable) {
                    bb.put((byte)(passable ? 1 : 0));
                }
                else {
                    bb.put((byte)2);
                }
                bb.putShort((short)fence.getHeightOffset());
                bb.put(fence.getLayer());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " problem closing fence: " + fence + " due to :" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendOpenMineDoor(final MineDoorPermission door) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-60));
                bb.putShort((short)door.getTileX());
                bb.putShort((short)door.getTileY());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " problem opening mine door: " + door + " due to :" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendCloseMineDoor(final MineDoorPermission door) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-61));
                bb.putShort((short)door.getTileX());
                bb.putShort((short)door.getTileY());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " problem closing mine door: " + door + " due to :" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddMineDoor(final MineDoorPermission door) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-58));
                bb.putShort((short)door.getTileX());
                bb.putShort((short)door.getTileY());
                bb.put(Tiles.decodeType(Server.surfaceMesh.getTile(door.getTileX(), door.getTileY())));
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " problem adding mine door: " + door + " due to :" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveMineDoor(final MineDoorPermission door) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-59));
                bb.putShort((short)door.getTileX());
                bb.putShort((short)door.getTileY());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " problem removing mine door: " + door + " due to :" + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendSound(final Sound sound) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final String name = sound.getName();
                final byte[] tempStringArr = name.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)86);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putFloat(sound.getPosX());
                bb.putFloat(sound.getPosY());
                bb.putFloat(sound.getPosZ());
                bb.putFloat(sound.getPitch());
                bb.putFloat(sound.getVolume());
                bb.putFloat(sound.getPriority());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendMusic(final Sound sound) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final String name = sound.getName();
                final byte[] tempStringArr = name.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)115);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putFloat(sound.getPosX());
                bb.putFloat(sound.getPosY());
                bb.putFloat(sound.getPosZ());
                bb.putFloat(sound.getPitch());
                bb.putFloat(sound.getVolume());
                bb.putFloat(sound.getPriority());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    protected void sendStatus(final String status) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = status.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-18));
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddWound(final Wound wound, final Item bodyPart) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)76);
                if (this.player == wound.getCreature()) {
                    bb.putLong(-1L);
                }
                else {
                    final Item body = wound.getCreature().getBody().getBodyItem();
                    bb.putLong(body.getWurmId());
                }
                final long parentId = bodyPart.getWurmId();
                bb.putLong(parentId);
                bb.putLong(wound.getWurmId());
                bb.putShort((short)wound.getWoundIconId());
                byte[] tempStringArr = wound.getName().getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                tempStringArr = "".getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                tempStringArr = wound.getDescription().getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putFloat(100.0f);
                bb.putFloat(wound.getSeverity() * 0.0015259022f);
                bb.putInt(0);
                bb.put((byte)0);
                bb.put((byte)0);
                bb.put((byte)0);
                bb.putShort(ItemTypeUtilites.calcProfile(true, false, false, false, false, false, false, false));
                bb.put((byte)0);
                bb.put((byte)0);
                bb.put((byte)0);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + wound.getWoundString(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveWound(final Wound wound) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-10));
                if (this.player == wound.getCreature()) {
                    bb.putLong(-1L);
                }
                else {
                    final Item body = wound.getCreature().getBody().getBodyItem();
                    bb.putLong(body.getWurmId());
                }
                bb.putLong(wound.getWurmId());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendUpdateWound(final Wound wound, final Item bodyPart) {
        if (this.player != null && this.player.hasLink()) {
            try {
                byte[] tempStringArr = wound.getName().getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)68);
                if (this.player == wound.getCreature()) {
                    bb.putLong(-1L);
                }
                else {
                    final Item body = wound.getCreature().getBody().getBodyItem();
                    bb.putLong(body.getWurmId());
                }
                bb.putLong(wound.getWurmId());
                final long parentId = bodyPart.getWurmId();
                bb.putLong(parentId);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                tempStringArr = wound.getDescription().getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putFloat(100.0f);
                bb.putFloat(wound.getSeverity() * 0.0015259022f);
                bb.putInt(0);
                bb.put((byte)0);
                bb.put((byte)0);
                bb.put((byte)0);
                bb.put((byte)0);
                bb.put((byte)0);
                bb.put((byte)0);
                bb.putShort((short)wound.getWoundIconId());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendSelfToLocal() {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = this.player.getName().getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-13));
                bb.put((byte)Communicator.local.length);
                bb.put(Communicator.local);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putLong(this.player.getWurmId());
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
            this.changeAttitude(this.player.getWurmId(), (byte)((this.player.getCitizenVillage() != null) ? 1 : 7));
        }
    }
    
    public void sendAddFriend(final String name, final long wurmid) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = name.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-13));
                bb.put((byte)Communicator.friends.length);
                bb.put(Communicator.friends);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putLong(wurmid);
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn(this.player.getName() + ':' + " wurmId " + wurmid + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveFriend(final String name) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = name.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)114);
                bb.put((byte)Communicator.friends.length);
                bb.put(Communicator.friends);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddAlly(final String name, final long wurmid) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = name.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-13));
                bb.put((byte)Communicator.alliance.length);
                bb.put(Communicator.alliance);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putLong(wurmid);
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn(this.player.getName() + ':' + " wurmId " + wurmid + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveAlly(final String name) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = name.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)114);
                bb.put((byte)Communicator.alliance.length);
                bb.put(Communicator.alliance);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddVillager(final String name, final long wurmid) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = name.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-13));
                bb.put((byte)Communicator.village.length);
                bb.put(Communicator.village);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putLong(wurmid);
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn(this.player.getName() + ':' + " wurmId " + wurmid + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveVillager(final String name) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = name.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)114);
                bb.put((byte)Communicator.village.length);
                bb.put(Communicator.village);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddLocal(final String name, final long wurmid) {
        if (this.player != null && this.player.hasLink() && !this.player.isUndead()) {
            try {
                final byte[] tempStringArr = name.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-13));
                bb.put((byte)Communicator.local.length);
                bb.put(Communicator.local);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putLong(wurmid);
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn(this.player.getName() + ':' + " wurmId " + wurmid + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveLocal(final String name) {
        if (this.player != null && this.player.hasLink() && !this.player.isUndead()) {
            try {
                if (!Servers.localServer.isChaosServer() || this.player.isPaying()) {
                    final byte[] tempStringArr = name.getBytes("UTF-8");
                    final ByteBuffer bb = this.connection.getBuffer();
                    bb.put((byte)114);
                    bb.put((byte)Communicator.local.length);
                    bb.put(Communicator.local);
                    bb.put((byte)tempStringArr.length);
                    bb.put(tempStringArr);
                    this.connection.flush();
                }
            }
            catch (Exception ex) {
                logWarn(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddTeam(final String name, final long wurmid) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = name.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-13));
                bb.put((byte)Communicator.team.length);
                bb.put(Communicator.team);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putLong(wurmid);
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn(this.player.getName() + ':' + " wurmId " + wurmid + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveTeam(final String name) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = name.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)114);
                bb.put((byte)Communicator.team.length);
                bb.put(Communicator.team);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddGm(final String name, final long wurmid) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = name.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-13));
                bb.put((byte)Communicator.gms.length);
                bb.put(Communicator.gms);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putLong(wurmid);
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn(this.player.getName() + ':' + " wurmId " + wurmid + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddPa(final String name, final long wurmid) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = name.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-13));
                bb.put((byte)Communicator.pas.length);
                bb.put(Communicator.pas);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.putLong(wurmid);
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn(this.player.getName() + ':' + " wurmId " + wurmid + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveGm(final String name) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final byte[] tempStringArr = name.getBytes("UTF-8");
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)114);
            bb.put((byte)Communicator.gms.length);
            bb.put(Communicator.gms);
            bb.put((byte)tempStringArr.length);
            bb.put(tempStringArr);
            this.connection.flush();
        }
        catch (Exception ex) {
            logWarn(this.player.getName() + ':' + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void sendRemoveMgmt(final String name) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final byte[] tempStringArr = name.getBytes("UTF-8");
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)114);
            bb.put((byte)Communicator.mgmt.length);
            bb.put(Communicator.mgmt);
            bb.put((byte)tempStringArr.length);
            bb.put(tempStringArr);
            this.connection.flush();
        }
        catch (Exception ex) {
            logWarn(this.player.getName() + ':' + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void sendAddMgmt(final String name, final long wurmid) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final byte[] tempStringArr = name.getBytes("UTF-8");
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)(-13));
            bb.put((byte)Communicator.mgmt.length);
            bb.put(Communicator.mgmt);
            bb.put((byte)tempStringArr.length);
            bb.put(tempStringArr);
            bb.putLong(wurmid);
            this.connection.flush();
        }
        catch (Exception ex) {
            logWarn(this.player.getName() + ':' + " wurmId " + wurmid + ' ' + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void sendRemovePa(final String name) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final byte[] tempStringArr = name.getBytes("UTF-8");
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)114);
            bb.put((byte)Communicator.pas.length);
            bb.put(Communicator.pas);
            bb.put((byte)tempStringArr.length);
            bb.put(tempStringArr);
            this.connection.flush();
        }
        catch (Exception ex) {
            logWarn(this.player.getName() + ':' + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void changeAttitude(final long creatureId, final byte status) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)6);
            bb.putLong(creatureId);
            bb.put(status);
            this.connection.flush();
        }
        catch (Exception ex) {
            logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void sendWeather() {
        this.shouldSendWeather = true;
        if (this.player != null && this.player.hasLink() && !this.connection.isWriting()) {
            this.checkSendWeather();
        }
    }
    
    public void sendSpecificWeather(final float fogLevel) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)46);
            bb.putFloat(Server.getWeather().getCloudiness());
            bb.putFloat(fogLevel);
            bb.putFloat(Server.getWeather().getRain());
            bb.putFloat(Server.getWeather().getWindRotation());
            bb.putFloat(Server.getWeather().getWindPower());
            this.connection.flush();
        }
        catch (Exception ex) {
            logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void checkSendWeather() {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        if (this.shouldSendWeather && !this.player.isInFogZone) {
            try {
                this.shouldSendWeather = false;
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)46);
                bb.putFloat(Server.getWeather().getCloudiness());
                bb.putFloat(Server.getWeather().getFog());
                bb.putFloat(Server.getWeather().getRain());
                bb.putFloat(Server.getWeather().getWindRotation());
                bb.putFloat(Server.getWeather().getWindPower());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendDead() {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)65);
            this.connection.flush();
        }
        catch (Exception ex) {
            logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void sendClimb(final boolean climbing) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)79);
            bb.put((byte)(climbing ? 1 : 0));
            this.connection.flush();
        }
        catch (Exception ex) {
            logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void sendReconnect(final String ip, final int port, final String session) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)23);
            byte[] tempStringArr = ip.getBytes("UTF-8");
            bb.put((byte)tempStringArr.length);
            bb.put(tempStringArr);
            bb.putInt(port);
            tempStringArr = session.getBytes("UTF-8");
            bb.put((byte)tempStringArr.length);
            bb.put(tempStringArr);
            this.connection.flush();
        }
        catch (Exception ex) {
            logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    protected void sendHasMoreItems(final long inventoryId, final long wurmid) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)29);
            bb.putLong(inventoryId);
            bb.putLong(wurmid);
            this.connection.flush();
        }
        catch (Exception ex) {
            this.player.setLink(false);
        }
    }
    
    public void sendIsEmpty(final long inventoryId, final long wurmid) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)(-16));
            bb.putLong(inventoryId);
            bb.putLong(wurmid);
            this.connection.flush();
        }
        catch (Exception ex) {
            this.player.setLink(false);
        }
    }
    
    protected void sendCompass(final Item item) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)(-30));
            bb.put((byte)0);
            if (item == null) {
                bb.put((byte)0);
            }
            else {
                float qualityLevel = item.getCurrentQualityLevel();
                if (item.getSpellSpeedBonus() != 0.0f) {
                    final float ench = item.getSpellSpeedBonus() / 100.0f;
                    final float diff = Math.min(20.0f, 100.0f - qualityLevel);
                    qualityLevel += diff * ench;
                }
                bb.put((byte)Math.max(1.0f, qualityLevel));
            }
            this.connection.flush();
        }
        catch (Exception ex) {
            logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    protected void sendToolbelt(final Item item) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)(-30));
            bb.put((byte)2);
            if (item == null) {
                bb.put((byte)0);
            }
            else {
                bb.put((byte)Math.max(1.0f, item.getCurrentQualityLevel() + item.getRarity() * 10));
            }
            this.connection.flush();
        }
        catch (Exception ex) {
            this.player.setLink(false);
        }
    }
    
    private void sendClientFeature(final byte feature, final boolean on) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)(-30));
            bb.put(feature);
            if (on) {
                bb.put((byte)1);
            }
            else {
                bb.put((byte)0);
            }
            this.connection.flush();
        }
        catch (Exception ex) {
            logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void sendServerTime() {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)107);
                bb.putLong(System.currentTimeMillis());
                bb.putLong(WurmCalendar.currentTime + this.timeMod);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAttachEffect(final long targetId, final byte effectType, final byte data0, final byte data1, final byte data2, final byte dimension) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)109);
                bb.putLong(targetId);
                bb.put(effectType);
                bb.put(data0);
                bb.put(data1);
                bb.put(data2);
                bb.put(dimension);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveEffect(final long targetId, final byte effectType) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)18);
                bb.putLong(targetId);
                bb.put(effectType);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendWieldItem(final long creatureId, final byte slot, final String modelname, final byte rarity, final int colorRed, final int colorGreen, final int colorBlue, final int secondaryColorRed, final int secondaryColorGreen, final int secondaryColorBlue) {
        if (this.player != null && this.player.hasLink()) {
            try {
                if (creatureId == -1L || WurmId.getType(creatureId) == 0) {
                    final ByteBuffer bb = this.connection.getBuffer();
                    bb.put((byte)101);
                    bb.putLong(creatureId);
                    bb.put(slot);
                    final byte[] tempStringArr = modelname.getBytes("UTF-8");
                    bb.putShort((short)tempStringArr.length);
                    bb.put(tempStringArr);
                    bb.put(rarity);
                    bb.putFloat(colorRed);
                    bb.putFloat(colorGreen);
                    bb.putFloat(colorBlue);
                    bb.putFloat(secondaryColorRed);
                    bb.putFloat(secondaryColorGreen);
                    bb.putFloat(secondaryColorBlue);
                    this.connection.flush();
                }
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendUseItem(final long creatureId, final String modelname, final byte rarity, final int colorRed, final int colorGreen, final int colorBlue, final int secondaryColorRed, final int secondaryColorGreen, final int secondaryColorBlue) {
        if (this.player != null && this.player.hasLink()) {
            try {
                if (creatureId == -1L || WurmId.getType(creatureId) == 0) {
                    final ByteBuffer bb = this.connection.getBuffer();
                    bb.put((byte)110);
                    bb.putLong(creatureId);
                    final byte[] tempStringArr = modelname.getBytes("UTF-8");
                    bb.putShort((short)tempStringArr.length);
                    bb.put(tempStringArr);
                    bb.put(rarity);
                    bb.putFloat(colorRed);
                    bb.putFloat(colorGreen);
                    bb.putFloat(colorBlue);
                    bb.putFloat(secondaryColorRed);
                    bb.putFloat(secondaryColorGreen);
                    bb.putFloat(secondaryColorBlue);
                    this.connection.flush();
                }
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddToCreationWindow(final Item target) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-46));
                bb.put((byte)1);
                final byte[] nameBytes = target.getName().getBytes("UTF-8");
                bb.putShort((short)nameBytes.length);
                bb.put(nameBytes);
                bb.putLong(target.getWurmId());
                bb.putFloat(target.getQualityLevel());
                bb.putFloat(target.getDamage());
                bb.putFloat(target.getWeightGrams() / 1000.0f);
                bb.putShort(target.getImageNumber());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddWallToCreationWindow(final Wall wall, final long toReplace) {
        if (this.player != null && this.player.hasLink()) {
            try {
                if (wall.isFinished()) {
                    return;
                }
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-46));
                bb.put((byte)10);
                bb.putLong(toReplace);
                final String name = (wall.getType() == StructureTypeEnum.PLAN) ? wall.getName() : ("Unfinished " + wall.getName());
                final byte[] nameBytes = name.getBytes("UTF-8");
                bb.putShort((short)nameBytes.length);
                bb.put(nameBytes);
                bb.putLong(wall.getId());
                bb.putFloat(wall.getQualityLevel());
                bb.putFloat(wall.getDamage());
                bb.putShort((short)60);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddFenceToCreationWindow(final Fence fence, final long toReplace) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-46));
                bb.put((byte)8);
                bb.putLong(toReplace);
                final byte[] nameBytes = WallConstants.getName(fence.getType()).getBytes("UTF-8");
                bb.putShort((short)nameBytes.length);
                bb.put(nameBytes);
                bb.putLong(fence.getId());
                bb.putFloat(fence.getQualityLevel());
                bb.putFloat(fence.getDamage());
                bb.putShort((short)60);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddFloorRoofToCreationWindow(final Floor floor, final long toReplace) {
        if (this.player != null && this.player.hasLink()) {
            if (floor.getFloorState() == FloorState.COMPLETED) {
                return;
            }
            try {
                String name = StringUtil.format("%s %s", floor.getName(), "plan");
                if (floor.getFloorState() == FloorState.BUILDING) {
                    final RoofFloorEnum en = RoofFloorEnum.getByFloorType(floor);
                    if (en == RoofFloorEnum.UNKNOWN) {
                        return;
                    }
                    name = StringUtil.format("%s %s", "Unfinished", StringUtil.toLowerCase(en.getName()));
                }
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-46));
                bb.put((byte)11);
                bb.putLong(toReplace);
                final byte[] nameBytes = name.getBytes("UTF-8");
                bb.putShort((short)nameBytes.length);
                bb.put(nameBytes);
                bb.putLong(floor.getId());
                bb.putFloat(floor.getQualityLevel());
                bb.putFloat(floor.getDamage());
                bb.putShort((short)60);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddTileBorderToCreationWindow(final long tileBorder) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-46));
                bb.put((byte)7);
                final byte[] name = "Tile Border".getBytes("UTF-8");
                bb.put((byte)name.length);
                bb.put(name);
                bb.putLong(tileBorder);
                bb.putShort((short)60);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddBridgePartToCreationWindow(final BridgePart bridgePart, final long toReplace) {
        if (this.player != null && this.player.hasLink()) {
            if (bridgePart.getBridgePartState() == BridgeConstants.BridgeState.COMPLETED) {
                return;
            }
            try {
                final String name = bridgePart.getFullName();
                final byte[] nameBytes = name.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-46));
                bb.put((byte)12);
                bb.putLong(toReplace);
                bb.putShort((short)nameBytes.length);
                bb.put(nameBytes);
                bb.putLong(bridgePart.getId());
                bb.putFloat(bridgePart.getQualityLevel());
                bb.putFloat(bridgePart.getDamage());
                bb.putShort(BridgePartEnum.getByBridgePartType(bridgePart).getIcon());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveFromCreationWindow(final long itemId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-46));
                bb.put((byte)2);
                bb.putLong(itemId);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendTileBorderCreationList(final long borderId, final long toolId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final Item tool = Items.getItem(toolId);
                if (tool.getTemplateId() == 62 || tool.getTemplateId() == 63 || tool.getTemplateId() == 493) {
                    if (CreationWindowMethods.createFenceListBuffer(this.connection, borderId)) {
                        this.connection.flush();
                    }
                }
                else if (Fence.getFlowerbedType(tool.getTemplateId()) != StructureConstantsEnum.FENCE_PLAN_WOODEN) {
                    if (CreationWindowMethods.createFlowerbedBuffer(this.connection, tool, borderId, this.player)) {
                        this.connection.flush();
                    }
                }
                else if (tool.getTemplateId() == 266 && CreationWindowMethods.createHedgeCreationBuffer(this.connection, tool, borderId, this.player)) {
                    this.connection.flush();
                }
            }
            catch (NoSuchItemException nsi) {
                logWarn("Unable to find item with id: " + toolId, nsi);
            }
            catch (UnsupportedEncodingException uee) {
                logWarn("Unhandled encoding", uee);
            }
            catch (IOException ioe) {
                logWarn("IO exception when sending fence creation list", ioe);
            }
        }
    }
    
    public final void sendFinishedFenceAction(final long id) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-46));
                bb.put((byte)9);
                bb.putLong(id);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public final void sendFinishedWallAction(final long id) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-46));
                bb.put((byte)9);
                bb.putLong(id);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public final void sendFinishedRoofFloorAction(final long id) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-46));
                bb.put((byte)9);
                bb.putLong(id);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private void sendRoofFloorCreationList(final long targetId, final long toolId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final Floor floor = RoofFloorEnum.getFloorOrRoofFromId(targetId);
                if (floor == null || floor.isFinished()) {
                    return;
                }
                final Structure structure = Structures.getStructure(floor.getStructureId());
                if (!structure.isFinalized()) {
                    return;
                }
                if (floor.getFloorState() == FloorState.PLANNING) {
                    if (toolId == -10L) {
                        return;
                    }
                    final Item tool = Items.getItem(toolId);
                    List<RoofFloorEnum> list;
                    if (floor.getType() != FloorType.ROOF) {
                        list = RoofFloorEnum.getFloorByToolAndType(tool, floor.getType());
                    }
                    else {
                        list = RoofFloorEnum.getRoofsByTool(tool);
                    }
                    final String catName = LoginHandler.raiseFirstLetter(StringUtil.format("%s%s", floor.getType().getName(), "s"));
                    final ByteBuffer bb = this.connection.getBuffer();
                    bb.put((byte)(-46));
                    bb.put((byte)0);
                    bb.put((byte)0);
                    bb.putShort((short)1);
                    final byte[] cateBytes = catName.getBytes("UTF-8");
                    bb.put((byte)cateBytes.length);
                    bb.put(cateBytes);
                    bb.putShort((short)list.size());
                    for (final RoofFloorEnum en : list) {
                        final boolean canBuild = RoofFloorEnum.canBuildFloorRoof(floor, en, this.player);
                        final String name = en.getName();
                        final byte[] namebytes = name.getBytes("UTF-8");
                        bb.put((byte)namebytes.length);
                        bb.put(namebytes);
                        bb.putShort(en.getIcon());
                        final short chance = (short)(canBuild ? 100 : 0);
                        bb.putShort(chance);
                        bb.putShort(en.getActionId());
                    }
                    this.connection.flush();
                }
                else if (floor.getFloorState() == FloorState.BUILDING) {
                    final RoofFloorEnum en2 = RoofFloorEnum.getByFloorType(floor);
                    if (en2 == RoofFloorEnum.UNKNOWN) {
                        return;
                    }
                    Item tool2 = null;
                    boolean hasValidTool = false;
                    if (toolId != -10L) {
                        try {
                            tool2 = Items.getItem(toolId);
                            hasValidTool = en2.isValidTool(tool2);
                        }
                        catch (NoSuchItemException ex2) {}
                    }
                    final String neededItemsCat = "Item(s) needed in inventory";
                    final ByteBuffer bb2 = this.connection.getBuffer();
                    bb2.put((byte)(-46));
                    bb2.put((byte)0);
                    bb2.put((byte)1);
                    bb2.putShort((short)(hasValidTool ? 1 : 2));
                    if (!hasValidTool) {
                        final String toolsCat = "Needed tool in crafting window";
                        final byte[] toolByte = "Needed tool in crafting window".getBytes("UTF-8");
                        bb2.put((byte)toolByte.length);
                        bb2.put(toolByte);
                        final int[] tools = RoofFloorEnum.getValidToolsForMaterial(en2.getMaterial());
                        bb2.putShort((short)tools.length);
                        for (final int tid : tools) {
                            final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(tid);
                            final String name2 = getFenceMaterialName(template);
                            final byte[] namebytes2 = name2.getBytes("UTF-8");
                            bb2.put((byte)namebytes2.length);
                            bb2.put(namebytes2);
                            bb2.putShort(template.getImageNumber());
                            final short chance2 = 1;
                            bb2.putShort((short)1);
                            bb2.putShort((short)169);
                        }
                    }
                    final byte[] catBytes = "Item(s) needed in inventory".getBytes("UTF-8");
                    bb2.put((byte)catBytes.length);
                    bb2.put(catBytes);
                    final List<BuildMaterial> needed = RoofFloorEnum.getMaterialsNeeded(floor);
                    bb2.putShort((short)needed.size());
                    for (int i = 0; i < needed.size(); ++i) {
                        final BuildMaterial mat = needed.get(i);
                        final ItemTemplate template2 = ItemTemplateFactory.getInstance().getTemplate(mat.getTemplateId());
                        final String name3 = getFenceMaterialName(template2);
                        final byte[] namebytes3 = name3.getBytes("UTF-8");
                        bb2.put((byte)namebytes3.length);
                        bb2.put(namebytes3);
                        bb2.putShort(template2.getImageNumber());
                        final short chance3 = (short)mat.getNeededQuantity();
                        bb2.putShort(chance3);
                        bb2.putShort(en2.getActionId());
                    }
                    this.connection.flush();
                }
            }
            catch (NoSuchItemException ex3) {}
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private void sendBridgePartCreationList(final long targetId, final long toolId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final BridgePart bridgePart = BridgePartEnum.getBridgePartFromId(targetId);
                if (bridgePart == null || bridgePart.isFinished()) {
                    return;
                }
                final Structure structure = Structures.getStructure(bridgePart.getStructureId());
                if (!structure.isFinalized()) {
                    return;
                }
                final BridgePartEnum en = BridgePartEnum.getByBridgePartType(bridgePart);
                if (en == BridgePartEnum.UNKNOWN) {
                    return;
                }
                Item tool = null;
                boolean hasValidTool = false;
                if (toolId != -10L) {
                    try {
                        tool = Items.getItem(toolId);
                        hasValidTool = en.isValidTool(tool);
                    }
                    catch (NoSuchItemException ex2) {}
                }
                final BuildAllMaterials needed = BridgePartEnum.getMaterialsNeeded(bridgePart);
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-46));
                bb.put((byte)0);
                bb.put((byte)1);
                final short cats = (short)(needed.getStageCount() + 1);
                bb.putShort(cats);
                if (!hasValidTool) {
                    final String toolsCat = "Needed tool in crafting window";
                    final byte[] toolByte = "Needed tool in crafting window".getBytes("UTF-8");
                    bb.put((byte)toolByte.length);
                    bb.put(toolByte);
                    final int[] tools = BridgePartEnum.getValidToolsForMaterial(en.getMaterial());
                    bb.putShort((short)tools.length);
                    for (final int tid : tools) {
                        final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(tid);
                        final String name = getFenceMaterialName(template);
                        final byte[] namebytes = name.getBytes("UTF-8");
                        bb.put((byte)namebytes.length);
                        bb.put(namebytes);
                        bb.putShort(template.getImageNumber());
                        final short chance = 1;
                        bb.putShort((short)1);
                        bb.putShort((short)169);
                    }
                }
                else {
                    final String neededItemsCat = "Item(s) needed in inventory";
                    final byte[] catBytes = "Item(s) needed in inventory".getBytes("UTF-8");
                    bb.put((byte)catBytes.length);
                    bb.put(catBytes);
                    final List<BuildMaterial> wanted = needed.getCurrentRequiredMaterials();
                    bb.putShort((short)wanted.size());
                    for (final BuildMaterial mat : wanted) {
                        final ItemTemplate template2 = ItemTemplateFactory.getInstance().getTemplate(mat.getTemplateId());
                        final String name2 = getFenceMaterialName(template2);
                        final byte[] namebytes2 = name2.getBytes("UTF-8");
                        bb.put((byte)namebytes2.length);
                        bb.put(namebytes2);
                        bb.putShort(template2.getImageNumber());
                        final short chance2 = 1;
                        bb.putShort((short)1);
                        bb.putShort((short)169);
                    }
                }
                for (final BuildStageMaterials bsm : needed.getBuildStageMaterials()) {
                    final byte[] catBytes2 = bsm.getStageName().getBytes("UTF-8");
                    bb.put((byte)catBytes2.length);
                    bb.put(catBytes2);
                    bb.putShort((short)bsm.getRequiredMaterials().size());
                    for (final BuildMaterial mat : bsm.getRequiredMaterials()) {
                        final ItemTemplate template2 = ItemTemplateFactory.getInstance().getTemplate(mat.getTemplateId());
                        final String name2 = getFenceMaterialName(template2);
                        final byte[] namebytes2 = name2.getBytes("UTF-8");
                        bb.put((byte)namebytes2.length);
                        bb.put(namebytes2);
                        bb.putShort(template2.getImageNumber());
                        final short chance2 = (short)mat.getNeededQuantity();
                        bb.putShort(chance2);
                        bb.putShort((short)169);
                    }
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private void sendWallCreationList(final long wallId, final long toolId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final Wall wall = Wall.getWall(wallId);
                if (wall == null || wall.isFinished()) {
                    return;
                }
                final Structure structure = Structures.getStructure(wall.getStructureId());
                if (!structure.isFinalized()) {
                    return;
                }
                if (wall.getType() == StructureTypeEnum.PLAN) {
                    if (CreationWindowMethods.createWallPlanBuffer(this.connection, structure, wall, this.player, toolId)) {
                        this.connection.flush();
                    }
                }
                else if (CreationWindowMethods.createWallBuildingBuffer(this.connection, wall, this.player, toolId)) {
                    this.connection.flush();
                }
            }
            catch (NoSuchStructureException nse) {
                logWarn("No structure found for wall.", nse);
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private void sendFenceCreationList(final long fenceId, final long toolId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final Fence fence = Fence.getFence(fenceId);
                if (fence == null) {
                    return;
                }
                boolean hasCorrectTool = false;
                if (toolId != -10L) {
                    try {
                        final Item tool = Items.getItem(toolId);
                        if (MethodsStructure.isCorrectToolForBuilding(this.player, tool.getTemplateId())) {
                            hasCorrectTool = true;
                        }
                    }
                    catch (NoSuchItemException ex2) {}
                }
                final String category = "Total materials needed";
                final String toolCat = "Needed tool in crafting window";
                final String currStage = "Item(s) needed in inventory";
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-46));
                bb.put((byte)0);
                bb.put((byte)1);
                bb.putShort((short)(hasCorrectTool ? 2 : 3));
                if (!hasCorrectTool) {
                    final byte[] toolByte = "Needed tool in crafting window".getBytes("UTF-8");
                    bb.put((byte)toolByte.length);
                    bb.put(toolByte);
                    final int[] correctTools = MethodsStructure.getCorrectToolsForBuildingFences();
                    bb.putShort((short)correctTools.length);
                    for (int i = 0; i < correctTools.length; ++i) {
                        final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(correctTools[i]);
                        final String name = getFenceMaterialName(template);
                        final byte[] namebytes = name.getBytes("UTF-8");
                        bb.put((byte)namebytes.length);
                        bb.put(namebytes);
                        bb.putShort(template.getImageNumber());
                        final short chance = 1;
                        bb.putShort((short)1);
                        bb.putShort((short)170);
                    }
                }
                final byte[] curr = "Item(s) needed in inventory".getBytes("UTF-8");
                bb.put((byte)curr.length);
                bb.put(curr);
                final int[] currNeeds = Fence.getItemTemplatesNeededForFence(fence);
                if (currNeeds.length == 1 && currNeeds[0] == -1) {
                    bb.putShort((short)0);
                }
                else {
                    bb.putShort((short)currNeeds.length);
                    for (int i = 0; i < currNeeds.length; ++i) {
                        final ItemTemplate template = ItemTemplateFactory.getInstance().getTemplate(currNeeds[i]);
                        final String name = getFenceMaterialName(template);
                        final byte[] namebytes = name.getBytes("UTF-8");
                        bb.put((byte)namebytes.length);
                        bb.put(namebytes);
                        bb.putShort(template.getImageNumber());
                        final short chance = 1;
                        bb.putShort((short)1);
                        bb.putShort((short)170);
                    }
                }
                final byte[] catBytes = "Total materials needed".getBytes("UTF-8");
                bb.put((byte)catBytes.length);
                bb.put(catBytes);
                final int[] needed = Fence.getConstructionMaterialsNeededTotal(fence);
                if (needed.length == 1 && needed[0] == -1) {
                    bb.putShort((short)0);
                }
                else {
                    bb.putShort((short)(needed.length / 2));
                    for (int j = 0; j < needed.length; j += 2) {
                        final ItemTemplate template2 = ItemTemplateFactory.getInstance().getTemplate(needed[j]);
                        final String name2 = getFenceMaterialName(template2);
                        final byte[] namebytes2 = name2.getBytes("UTF-8");
                        bb.put((byte)namebytes2.length);
                        bb.put(namebytes2);
                        bb.putShort(template2.getImageNumber());
                        final short chance2 = (short)needed[j + 1];
                        bb.putShort(chance2);
                        bb.putShort((short)170);
                    }
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private static String getFenceMaterialName(final ItemTemplate template) {
        if (template.getTemplateId() == 218) {
            return "small iron " + template.getName();
        }
        if (template.getTemplateId() == 217) {
            return "large iron " + template.getName();
        }
        return template.getName();
    }
    
    public void sendPartialCreationList(final long sourceId, final long targetId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final Optional<Item> optSource = Items.getItemOptional(sourceId);
                if (!optSource.isPresent()) {
                    return;
                }
                final Item source = optSource.get();
                Item target = null;
                if (targetId != -10L) {
                    final Optional<Item> optTarget = Items.getItemOptional(targetId);
                    if (!optTarget.isPresent()) {
                        return;
                    }
                    target = optTarget.get();
                }
                if (targetId != -10L) {
                    if (!CreationWindowMethods.createCreationListBuffer(this.connection, source, target, this.player)) {
                        return;
                    }
                }
                else {
                    if (!source.isUnfinished()) {
                        return;
                    }
                    if (!CreationWindowMethods.createUnfinishedCreationListBuffer(this.connection, source, this.player)) {
                        return;
                    }
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendStopUseItem(final long creatureId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                if (creatureId == -1L || WurmId.getType(creatureId) == 0) {
                    final ByteBuffer bb = this.connection.getBuffer();
                    bb.put((byte)71);
                    bb.putLong(creatureId);
                    this.connection.flush();
                }
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddCaveWallToCreationWindow(final long caveWallId, final byte type, final long toReplace) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final Tiles.Tile theTile = Tiles.getTile(type);
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-46));
                bb.put((byte)10);
                bb.putLong(toReplace);
                final String name = theTile.getName();
                final byte[] nameBytes = name.getBytes("UTF-8");
                bb.putShort((short)nameBytes.length);
                bb.put(nameBytes);
                bb.putLong(caveWallId);
                bb.putFloat(-1.0f);
                bb.putFloat(-1.0f);
                bb.putShort((short)60);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private void sendCaveCreationList(final long caveTileId, final long toolId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final int tilex = Tiles.decodeTileX(caveTileId);
                final int tiley = Tiles.decodeTileY(caveTileId);
                final int tile = Server.caveMesh.getTile(tilex, tiley);
                final byte type = Tiles.decodeType(tile);
                if (type != Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id && !CaveWallBehaviour.isPartlyClad(type)) {
                    return;
                }
                if (type == Tiles.Tile.TILE_CAVE_WALL_REINFORCED.id) {
                    if (CreationWindowMethods.createCaveReinforcedBuffer(this.connection, this.player, toolId)) {
                        this.connection.flush();
                    }
                }
                else if (CreationWindowMethods.createCaveCladdingBuffer(this.connection, tilex, tiley, tile, type, this.player, toolId)) {
                    this.connection.flush();
                }
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRepaint(final long wurmid, final byte r, final byte g, final byte b, final byte alpha, final byte paintType) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)92);
                bb.putLong(wurmid);
                bb.put(r);
                bb.put(g);
                bb.put(b);
                bb.put(alpha);
                bb.put(paintType);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " wurmId " + wurmid + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendResize(final long wurmid, final byte xscaleMod, final byte yscaleMod, final byte zscaleMod) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)74);
                bb.putLong(wurmid);
                bb.put(xscaleMod);
                bb.put(yscaleMod);
                bb.put(zscaleMod);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " wurmId " + wurmid + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendNewMovingItem(final long id, final String name, final String model, final float x, final float y, final float z, final long onBridge, final float rot, final byte layer, final boolean onGround, final boolean floating, final boolean isHoverable, final byte material, final byte rarity) {
        this.sendNewMovingItem(id, name, "", model, x, y, z, onBridge, rot, layer, onGround, floating, isHoverable, material, rarity);
    }
    
    public void sendNewMovingItem(final long id, final String name, final String hoverText, final String model, final float x, final float y, final float z, final long onBridge, final float rot, final byte layer, final boolean onGround, final boolean floating, final boolean isHoverable, final byte material, final byte rarity) {
        if (this.player != null && this.player.hasLink()) {
            try {
                byte[] tempStringArr = model.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)108);
                bb.putLong(id);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put((byte)(isHoverable ? 1 : 0));
                bb.putFloat(y);
                bb.putFloat(x);
                bb.putLong(onBridge);
                bb.putFloat(rot);
                if (onGround) {
                    if (Structure.isGroundFloorAtPosition(x, y, layer == 0)) {
                        bb.putFloat(z + 0.1f);
                    }
                    else {
                        bb.putFloat(-3000.0f);
                    }
                }
                else {
                    bb.putFloat(z);
                }
                tempStringArr = name.getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                tempStringArr = hoverText.getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                if (floating) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                bb.put(layer);
                bb.put((byte)2);
                bb.put(material);
                bb.put((byte)0);
                bb.putLong(0L);
                bb.put((byte)0);
                bb.put((byte)0);
                bb.put(rarity);
                this.connection.flush();
                if (this.player.getVehicle() == id) {
                    this.player.getMovementScheme().resumeSpeedModifier();
                }
            }
            catch (NullPointerException np) {
                logWarn(this.player.getName() + ':' + " itemId: " + id + ' ' + np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                logWarn(this.player.getName() + ':' + " itemId: " + id + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendMoveMovingItem(final long id, final float x, final float y, final int rot) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)36);
                bb.putLong(id);
                bb.putFloat(y);
                bb.putFloat(x);
                bb.put((byte)rot);
                this.connection.flush();
            }
            catch (NullPointerException np) {
                logWarn(this.player.getName() + ':' + " itemId: " + id + ' ' + np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " itemId: " + id + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendMoveMovingItemAndSetZ(final long id, final float x, final float y, final int rot) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)36);
                bb.putLong(id);
                bb.putFloat(y);
                bb.putFloat(x);
                bb.put((byte)rot);
                this.connection.flush();
            }
            catch (NullPointerException np) {
                logWarn(this.player.getName() + ':' + " itemId: " + id + ' ' + np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " itemId: " + id + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendMoveMovingItemAndSetZ(final long id, final float x, final float y, final float z, final int rot) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)72);
                bb.putLong(id);
                bb.putFloat(z);
                bb.putFloat(x);
                bb.put((byte)rot);
                bb.putFloat(y);
                this.connection.flush();
            }
            catch (NullPointerException np) {
                logWarn(this.player.getName() + ':' + " itemId: " + id + ' ' + np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " itemId: " + id + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendMovingItemChangedLayer(final long wurmid, final byte newlayer) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)30);
                bb.putLong(wurmid);
                bb.put(newlayer);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " wurmId " + wurmid + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendNewFace(final long wurmid, final long newFace) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)52);
                bb.putLong(wurmid);
                bb.putLong(newFace);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " wurmId " + wurmid + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendCustomizeFace(final long oldFace, final long itemId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)2);
                bb.putLong(oldFace);
                bb.putLong(itemId);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " itemId: " + itemId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendDeleteMovingItem(final long id) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)14);
                bb.putLong(id);
                this.connection.flush();
            }
            catch (NullPointerException np) {
                logWarn(this.player.getName() + ':' + " itemId: " + id + ' ' + np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " itemId: " + id + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendShutDown(final String reason, final boolean requested) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)4);
                final byte[] tempStringArr = reason.getBytes("UTF-8");
                bb.putShort((short)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put((byte)(requested ? 1 : 0));
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void attachCreature(final long source, final long target, final float offx, final float offy, final float offz, final int seatId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)111);
                bb.putLong(source);
                bb.putLong(target);
                bb.putFloat(offx);
                bb.putFloat(offy);
                bb.putFloat(offz);
                bb.put((byte)seatId);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " sourceId: " + source + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void setVehicleController(final long playerId, final long targetId, final float offx, final float offy, final float offz, final float maxDepth, final float maxHeight, final float maxHeightDiff, final float vehicleRotation, final int seatId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)63);
                bb.putLong(playerId);
                bb.putLong(targetId);
                bb.putFloat(offx);
                bb.putFloat(offy);
                bb.putFloat(offz);
                bb.putFloat(maxDepth);
                bb.putFloat(maxHeight);
                bb.putFloat(maxHeightDiff);
                bb.putFloat(vehicleRotation);
                bb.put((byte)seatId);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " targetId: " + targetId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAnimation(final long creatureId, final String animationName, final boolean looping, final boolean freezeAtFinish) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)24);
                bb.putLong(creatureId);
                final byte[] tempStringArr = animationName.getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                if (looping) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                if (freezeAtFinish) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " creatureId: " + creatureId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAnimation(final long creatureId, final String animationName, final boolean looping, final boolean freezeAtFinish, final long targetId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)88);
                bb.putLong(creatureId);
                final byte[] tempStringArr = animationName.getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                if (looping) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                if (freezeAtFinish) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                bb.putLong(targetId);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " creatureId: " + creatureId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendStance(final long creatureId, final byte stance) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)85);
                bb.putLong(creatureId);
                bb.put(stance);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " creatureId: " + creatureId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendCombatOptions(final byte[] options, final short tenthsOfSeconds) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)98);
                bb.put((byte)options.length);
                bb.put(options);
                bb.putShort(tenthsOfSeconds);
                this.connection.flush();
            }
            catch (NullPointerException np) {
                logWarn(this.player.getName() + ':' + np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    protected void sendCombatStatus(final float distanceToTarget, final float footing, final byte stance) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-14));
                bb.putFloat(distanceToTarget);
                bb.putFloat(footing);
                bb.put(stance);
                this.connection.flush();
            }
            catch (NullPointerException np) {
                logWarn(this.player.getName() + ':' + np.getMessage(), np);
                this.player.setLink(false);
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    protected void sendStunned(final boolean stunned) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)28);
                if (stunned) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendSpecialMove(final short move, final String movename) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final byte[] tempStringArr = movename.getBytes("UTF-8");
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-17));
                bb.putShort(move);
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendToggleShield(final boolean on) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-17));
                bb.putShort((short)105);
                if (on) {
                    bb.put((byte)1);
                }
                else {
                    bb.put((byte)0);
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    protected void sendTarget(final long id) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)25);
                bb.putLong(id);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " id " + id + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    protected void sendFightStyle(final byte style) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)26);
                bb.put(style);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void setCreatureDamage(final long wurmid, final float damagePercent) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)11);
                bb.putLong(wurmid);
                bb.putFloat(damagePercent);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " wurmId " + wurmid + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendWindImpact(final byte windimpact) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)117);
                bb.put(windimpact);
                this.connection.flush();
                if (this.player.getSecondsPlayed() > 120.0f && !this.player.isTeleporting() && this.player.transferCounter == 0) {
                    this.player.sentWind = System.currentTimeMillis();
                }
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendMountSpeed(final short mountSpeed) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)60);
                bb.putShort(mountSpeed);
                this.connection.flush();
                if (this.player.getSecondsPlayed() > 120.0f && !this.player.isTeleporting() && this.player.transferCounter == 0) {
                    this.player.sentMountSpeed = System.currentTimeMillis();
                }
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRotate(final long itemId, final float rotation) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)67);
                bb.putLong(itemId);
                bb.putFloat(rotation);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAck(final float xpos, final float ypos) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)3);
                bb.putFloat(xpos);
                bb.putFloat(ypos);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddSpellEffect(final SpellEffectsEnum effect, final int duration, final float power) {
        this.sendAddStatusEffect(effect, duration);
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)7);
                bb.putLong(effect.getId());
                final byte[] tempStringArr = effect.getName().getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put((byte)0);
                bb.put(effect.getEffectType());
                bb.put(effect.getInfluence());
                bb.putInt(duration);
                bb.putFloat(power);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " id " + effect.getId() + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddSpellEffect(final long id, final SpellEffectsEnum effect, final int duration, final float power) {
        this.sendAddStatusEffect(id, effect, duration);
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)7);
                bb.putLong(id);
                final byte[] tempStringArr = effect.getName().getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put((byte)0);
                bb.put(effect.getEffectType());
                bb.put(effect.getInfluence());
                bb.putInt(duration);
                bb.putFloat(power);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " id " + effect.getId() + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddSpellEffect(final long id, final String name, final byte type, final byte effectType, final byte influence, final int duration, final float power) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)7);
                bb.putLong(id);
                final byte[] tempStringArr = name.getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put(type);
                bb.put(effectType);
                bb.put(influence);
                bb.putInt(duration);
                bb.putFloat(power);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " id " + id + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddStatusEffect(final long id, final SpellEffectsEnum effect, final int duration, final String name) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-47));
                bb.put((byte)0);
                bb.putLong(id);
                bb.putInt(effect.getTypeId());
                bb.putInt(effect.isSendDuration() ? duration : -1);
                sendByteStringLength(name, bb);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " id " + effect.getId() + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddStatusEffect(final long id, final SpellEffectsEnum effect, final int duration) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-47));
                bb.put((byte)0);
                bb.putLong(id);
                bb.putInt(effect.getTypeId());
                bb.putInt(effect.isSendDuration() ? duration : -1);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " id " + effect.getId() + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddStatusEffect(final SpellEffectsEnum effect, final int duration, final String name) {
        if (!effect.isSendToBuffBar()) {
            return;
        }
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-47));
                bb.put((byte)0);
                bb.putLong(effect.getId());
                bb.putInt(effect.getTypeId());
                bb.putInt(effect.isSendDuration() ? duration : -1);
                sendByteStringLength(name, bb);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " id " + effect.getId() + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddStatusEffect(final SpellEffectsEnum effect, final int duration) {
        if (!effect.isSendToBuffBar()) {
            return;
        }
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-47));
                bb.put((byte)0);
                bb.putLong(effect.getId());
                bb.putInt(effect.getTypeId());
                bb.putInt(effect.isSendDuration() ? duration : -1);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " id " + effect.getId() + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveFromStatusEffectBar(final long id) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-47));
                bb.put((byte)1);
                bb.putLong(id);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " id " + id + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private void sendAvailServer(final byte direction, final boolean avail) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)59);
                bb.put(direction);
                bb.put((byte)(avail ? 1 : 0));
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveSpellEffect(final long id, final SpellEffectsEnum spellEffect) {
        if (spellEffect != null && spellEffect.isSendToBuffBar()) {
            this.sendRemoveFromStatusEffectBar(id);
        }
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)17);
                bb.putLong(id);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " id " + id + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveSpellEffect(final SpellEffectsEnum effect) {
        if (effect == null) {
            return;
        }
        if (effect.isSendToBuffBar()) {
            this.sendRemoveFromStatusEffectBar(effect.getId());
        }
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)17);
                bb.putLong(effect.getId());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " id " + effect.getId() + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendFocusLevel(final long targetId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)50);
                bb.put(this.player.getFightlevel());
                bb.putLong(targetId);
                final byte[] descStringArr = this.player.getFightlevelString().getBytes("UTF-8");
                bb.put((byte)descStringArr.length);
                bb.put(descStringArr);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAddAreaSpellEffect(final int tilex, final int tiley, final int layer, final byte type, final int floorLevel, final int heightOffset, final boolean loop) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-4));
                bb.putShort((short)tilex);
                bb.putShort((short)tiley);
                bb.put((byte)layer);
                bb.put(type);
                bb.putShort((short)heightOffset);
                bb.put((byte)(loop ? 1 : 0));
                bb.put((byte)floorLevel);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveAreaSpellEffect(final int tilex, final int tiley, final int layer) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-5));
                bb.putShort((short)tilex);
                bb.putShort((short)tiley);
                bb.put((byte)layer);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendMissionState(final long wurmId, final String name, final String description, final String creator, final float state, final long start, final long endDate, final long expires, final boolean restartable, final byte difficulty, final String rewards) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)33);
                bb.putLong(wurmId);
                final byte[] nameStringArr = name.getBytes("UTF-8");
                bb.put((byte)nameStringArr.length);
                bb.put(nameStringArr);
                final byte[] descStringArr = description.getBytes("UTF-8");
                bb.putShort((short)descStringArr.length);
                bb.put(descStringArr);
                final byte[] creStringArr = creator.getBytes("UTF-8");
                bb.put((byte)creStringArr.length);
                bb.put(creStringArr);
                bb.putLong(start);
                bb.putLong(endDate);
                bb.putLong(expires);
                bb.put((byte)(restartable ? 1 : 0));
                bb.putFloat(state);
                bb.put(difficulty);
                final byte[] rewardsStringArr = rewards.getBytes("UTF-8");
                bb.putShort((short)rewardsStringArr.length);
                bb.put(rewardsStringArr);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " wurmId " + wurmId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveMissionState(final long wurmId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)113);
                bb.putLong(wurmId);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " wurmId " + wurmId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendDamageState(final long wurmid, final byte damage) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)22);
                bb.putLong(wurmid);
                bb.put(damage);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " wurmId " + wurmid + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendNewProjectile(final long id, final byte type, final String modelName, final String name, final byte material, final Vector3f startingPosition, final Vector3f startingVelocity, final Vector3f endingPosition, final float rotation, final boolean surface) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)35);
                bb.put((byte)(-1));
                bb.put(type);
                final byte[] modelStringArr = modelName.getBytes("UTF-8");
                bb.put((byte)modelStringArr.length);
                bb.put(modelStringArr);
                final byte[] nStringArr = name.getBytes("UTF-8");
                bb.put((byte)nStringArr.length);
                bb.put(nStringArr);
                bb.putLong(id);
                bb.put(material);
                bb.putFloat(startingPosition.x);
                bb.putFloat(startingPosition.y);
                bb.putFloat(startingPosition.z);
                bb.putFloat(startingVelocity.x);
                bb.putFloat(startingVelocity.y);
                bb.putFloat(startingVelocity.z);
                bb.putFloat(endingPosition.x);
                bb.putFloat(endingPosition.y);
                bb.putFloat(endingPosition.z);
                bb.putFloat(rotation);
                bb.put((byte)(surface ? 0 : -1));
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " id " + id + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendProjectile(final long id, final byte type, final String modelName, final String name, final byte material, final float startX, final float startY, final float startH, final float rot, final byte layer, final float endX, final float endY, final float endH, final long sourceId, final long targetId, final float projectedSecondsInAir, final float actualSecondsInAir) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)35);
                bb.put(type);
                final byte[] modelStringArr = modelName.getBytes("UTF-8");
                bb.put((byte)modelStringArr.length);
                bb.put(modelStringArr);
                final byte[] nStringArr = name.getBytes("UTF-8");
                bb.put((byte)nStringArr.length);
                bb.put(nStringArr);
                bb.putLong(id);
                bb.put(material);
                bb.putFloat(startX);
                bb.putFloat(startY);
                bb.putFloat(startH);
                bb.putFloat(rot);
                bb.put(layer);
                bb.putFloat(endX);
                bb.putFloat(endY);
                bb.putFloat(endH);
                bb.putLong(sourceId);
                bb.putLong(targetId);
                bb.putFloat(projectedSecondsInAir);
                bb.putFloat(actualSecondsInAir);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " id " + id + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendPlonk(final short plonkId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-49));
                bb.putShort(plonkId);
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn("Failed to send plonk: " + plonkId + " to player: " + this.player.getName(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendWallDamageState(final long houseId, final long wurmid, final byte damage) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)34);
                bb.putLong(houseId);
                bb.putLong(wurmid);
                bb.put(damage);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " wurmId " + wurmid + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendItemTemplateList(final int itemId, final String modelName) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)78);
                bb.putInt(itemId);
                final byte[] modelNameStringArr = modelName.getBytes("UTF-8");
                bb.put((byte)modelNameStringArr.length);
                bb.put(modelNameStringArr);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " itemId " + itemId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendHorseWear(final long wurmId, final int itemId, final byte material, final byte slot, final byte aux_data) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-31));
                bb.putLong(wurmId);
                bb.putInt(itemId);
                bb.put(material);
                bb.put(slot);
                bb.put(aux_data);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " wurmId " + wurmId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveHorseWear(final long wurmId, final int itemId, final byte slot) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)55);
                bb.putLong(wurmId);
                bb.putInt(itemId);
                bb.put(slot);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " wurmId " + wurmId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendWearItem(final long wurmId, final int itemId, final byte bodyPart, final float red, final float green, final float blue, final float secondaryRed, final float secondaryGreen, final float secondaryBlue, final byte material, final byte rarity) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)21);
                bb.putLong(wurmId);
                bb.putInt(itemId);
                bb.put(bodyPart);
                bb.put((byte)1);
                bb.putFloat(red);
                bb.putFloat(green);
                bb.putFloat(blue);
                bb.putFloat(secondaryRed);
                bb.putFloat(secondaryGreen);
                bb.putFloat(secondaryBlue);
                bb.put(material);
                bb.put(rarity);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " wurmId " + wurmId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRemoveWearItem(final long wurmId, final byte bodyPart) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)27);
                bb.putLong(wurmId);
                bb.put(bodyPart);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " wurmId " + wurmId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendHasTarget(final long wurmId, final boolean hasTarget) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)53);
                bb.putLong(wurmId);
                bb.put((byte)(hasTarget ? 1 : 0));
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " wurmId " + wurmId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public final Player getPlayer() {
        return this.player;
    }
    
    public final void setPlayer(final Player aPlayer) {
        this.player = aPlayer;
    }
    
    public final PlayerMove getCurrentmove() {
        return this.currentmove;
    }
    
    public final void setAvailableMoves(final int aAvailableMoves) {
        this.availableMoves = aAvailableMoves;
    }
    
    public final int getMoves() {
        return this.moves;
    }
    
    protected final void setMoves(final int aMoves) {
        this.moves = aMoves;
    }
    
    final boolean hasReceivedTicks() {
        return this.receivedTicks;
    }
    
    public static int getNumcommands() {
        return Communicator.numcommands;
    }
    
    public static int getPrevcommand() {
        return Communicator.prevcommand;
    }
    
    public static int getLastcommand() {
        return Communicator.lastcommand;
    }
    
    public static long getCommandAction() {
        return Communicator.commandAction;
    }
    
    public static String getCommandMessage() {
        return Communicator.commandMessage;
    }
    
    public final boolean isInvulnerable() {
        return this.invulnerable;
    }
    
    @Override
    public String toString() {
        return "Communicator [Player: " + this.player + ", Conn: " + this.connection + ']';
    }
    
    public void sendRemoveWall(final long structureId, final Wall wall) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)54);
                bb.putLong(structureId);
                bb.putShort((short)Math.min(wall.getStartX(), wall.getEndX()));
                bb.putShort((short)Math.min(wall.getStartY(), wall.getEndY()));
                bb.putShort((short)wall.getHeight());
                if (wall.isHorizontal()) {
                    bb.put((byte)0);
                }
                else {
                    bb.put((byte)1);
                }
                bb.put(wall.getLayer());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " structureId: " + structureId + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void setGroundOffset(final int offset, final boolean immediately) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)94);
                bb.putInt(offset);
                bb.put((byte)(immediately ? 1 : 0));
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " offset " + offset + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAchievementList(final Achievement[] achievements) {
        if (this.player != null && this.player.hasLink()) {
            try {
                int visible = 0;
                for (int x = 0; x < achievements.length; ++x) {
                    if (!achievements[x].isInVisible()) {
                        ++visible;
                    }
                }
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)100);
                bb.putInt(visible);
                for (int x2 = 0; x2 < achievements.length; ++x2) {
                    if (!achievements[x2].isInVisible()) {
                        final AchievementTemplate template = achievements[x2].getTemplate();
                        bb.putInt(achievements[x2].getAchievement());
                        final byte[] aNameStringArr = template.getName().getBytes("UTF-8");
                        bb.put((byte)aNameStringArr.length);
                        bb.put(aNameStringArr);
                        String adesc = template.getDescription();
                        adesc = adesc.replace("COUNTER", "[" + achievements[x2].getCounter() + "]");
                        final byte[] aDescStringArr = adesc.getBytes("UTF-8");
                        bb.put((byte)aDescStringArr.length);
                        bb.put(aDescStringArr);
                        bb.put(template.getType());
                        bb.putLong(achievements[x2].getDateAchieved().getTime());
                        bb.putInt(achievements[x2].getCounter());
                    }
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendAchievement(final Achievement achievement, final boolean isNew) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)38);
                bb.put((byte)(isNew ? 1 : 0));
                bb.put((byte)(achievement.isPlaySoundOnUpdate() ? 1 : 0));
                bb.putInt(achievement.getAchievement());
                final AchievementTemplate template = achievement.getTemplate();
                final byte[] aNameStringArr = template.getName().getBytes("UTF-8");
                bb.put((byte)aNameStringArr.length);
                bb.put(aNameStringArr);
                final String adesc = template.getDescription();
                adesc.replace("COUNTER", "[" + achievement.getCounter() + "]");
                final byte[] aDescStringArr = adesc.getBytes("UTF-8");
                bb.put((byte)aDescStringArr.length);
                bb.put(aDescStringArr);
                bb.put(template.getType());
                bb.putLong(achievement.getDateAchieved().getTime());
                bb.putInt(achievement.getCounter());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " achievement: " + achievement + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private void sendRecipeNameList(final Recipe[] recipes) {
        if (this.player != null && this.player.hasLink()) {
            int chunk = 1;
            try {
                ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-55));
                bb.put((byte)0);
                bb.put((byte)chunk);
                bb.putShort((short)recipes.length);
                for (Recipe recipe : recipes) {
                    final Recipe localRecipe = recipe;
                    if (this.player.getPower() == 5) {
                        final Recipe playerRecipe = RecipesByPlayer.getPlayerKnownRecipeOrNull(this.player.getWurmId(), recipe.getRecipeId());
                        if (playerRecipe != null) {
                            recipe = playerRecipe;
                        }
                    }
                    final Ingredient i = recipe.getResultItem();
                    if (i == null) {
                        Communicator.logger.warning("Result item missing for " + recipe.getName() + " (" + recipe.getRecipeId() + ")");
                    }
                    else {
                        bb.putShort(recipe.getRecipeId());
                        sendByteStringLength(recipe.getName(), bb);
                        if (this.player.getPower() == 5) {
                            bb.put((byte)1);
                        }
                        else {
                            bb.put((byte)(RecipesByPlayer.isKnownRecipe(this.player.getWurmId(), recipe.getRecipeId()) ? 1 : 0));
                        }
                        bb.put(recipe.getRecipeColourCode(this.player.getWurmId()));
                        bb.putShort(i.getIcon());
                        bb.put((byte)(recipe.isLootable() ? 1 : 0));
                        bb.put((byte)(RecipesByPlayer.isFavourite(this.player.getWurmId(), recipe.getRecipeId()) ? 1 : 0));
                        sendByteStringLength(RecipesByPlayer.getNotes(this.player.getWurmId(), recipe.getRecipeId()), bb);
                    }
                }
                for (Recipe recipe : recipes) {
                    final Recipe localRecipe = recipe;
                    if (this.player.getPower() == 5) {
                        final Recipe playerRecipe = RecipesByPlayer.getPlayerKnownRecipeOrNull(this.player.getWurmId(), recipe.getRecipeId());
                        if (playerRecipe != null) {
                            recipe = playerRecipe;
                        }
                    }
                    final ByteBuffer bbc = ByteBuffer.allocate(65534);
                    this.sendCookbookRecipe(recipe, bbc);
                    bbc.flip();
                    final byte[] bytes = new byte[bbc.remaining()];
                    bbc.get(bytes);
                    if (bb.remaining() - 10 <= bytes.length) {
                        this.connection.flush();
                        bb = this.connection.getBuffer();
                        bb.put((byte)(-55));
                        bb.put((byte)0);
                        bb.put((byte)(++chunk));
                    }
                    bb.put(bytes);
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendCookbookRecipe(final Recipe recipe) {
        if (this.player != null && this.player.hasLink() && recipe != null) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-55));
                bb.put((byte)1);
                this.sendCookbookRecipe(recipe, bb);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendCookbookRecipe(final Recipe recipe, final ByteBuffer bb) throws Exception {
        bb.putShort(recipe.getRecipeId());
        sendByteStringLength(recipe.getName(), bb);
        bb.put(recipe.getRecipeColourCode(this.player.getWurmId()));
        bb.putShort(recipe.getResultItem().getIcon());
        bb.put((byte)(recipe.isLootable() ? 1 : 0));
        bb.put((byte)(RecipesByPlayer.isFavourite(this.player.getWurmId(), recipe.getRecipeId()) ? 1 : 0));
        sendByteStringLength(RecipesByPlayer.getNotes(this.player.getWurmId(), recipe.getRecipeId()), bb);
        sendByteStringLength(recipe.getSkillName(), bb);
        if (!recipe.hasCooker()) {
            bb.put((byte)0);
        }
        else {
            final Set<ItemTemplate> cookers = recipe.getCookerTemplates();
            bb.put((byte)cookers.size());
            for (final ItemTemplate cooker : cookers) {
                sendByteStringLength(cooker.getName(), bb);
                bb.putShort(cooker.getImageNumber());
            }
        }
        if (!recipe.hasContainer()) {
            bb.put((byte)0);
        }
        else {
            final Set<ItemTemplate> containers = recipe.getContainerTemplates();
            bb.put((byte)containers.size());
            for (final ItemTemplate container : containers) {
                sendByteStringLength(container.getName(), bb);
                bb.putShort(container.getImageNumber());
            }
        }
        if (!recipe.hasActiveItem()) {
            bb.put((byte)0);
        }
        else {
            if (recipe.getActiveItem().getTemplate().isTool()) {
                bb.put((byte)2);
            }
            else {
                bb.put((byte)1);
            }
            this.sendCookbookIngredient(recipe, recipe.getActiveItem(), bb);
        }
        if (!recipe.hasTargetItem()) {
            bb.put((byte)0);
        }
        else {
            bb.put((byte)1);
            this.sendCookbookIngredient(recipe, recipe.getTargetItem(), bb);
        }
        final IngredientGroup[] groups = recipe.getGroups();
        bb.put((byte)groups.length);
        for (final IngredientGroup ig : groups) {
            bb.put(ig.getGroupType());
            final Ingredient[] ingredients = ig.getIngredients();
            bb.put((byte)ingredients.length);
            for (final Ingredient ingredient : ingredients) {
                this.sendCookbookIngredient(recipe, ingredient, bb);
            }
        }
    }
    
    private void sendCookbookIngredient(final Recipe recipe, final Ingredient ingredient, final ByteBuffer bb) throws Exception {
        byte colourCode = 0;
        boolean linkKnown = false;
        final String linkNotes = "";
        final String name = Recipes.getIngredientName(ingredient);
        final Recipe[] recipes = Recipes.getRecipesByResult(ingredient);
        bb.putShort(ingredient.getTemplate().getImageNumber());
        if (recipes.length > 0) {
            if (recipes.length == 1) {
                sendByteStringLength(name, bb);
                bb.put((byte)1);
            }
            else if (name.equals("any fish") || name.equals("any veg") || name.equals("fish fillet")) {
                sendByteStringLength(name, bb);
                bb.put((byte)(recipes.length + 1));
                bb.putShort((short)(-1));
                sendByteStringLength("raw " + name, bb);
                bb.put((byte)4);
                bb.put((byte)0);
                bb.put((byte)0);
                sendByteStringLength(linkNotes, bb);
            }
            else {
                if (name.startsWith("any ")) {
                    sendByteStringLength(name, bb);
                }
                else {
                    sendByteStringLength("any " + name, bb);
                }
                bb.put((byte)recipes.length);
            }
            for (int x = 0; x < recipes.length; ++x) {
                bb.putShort(recipes[x].getRecipeId());
                if (recipes.length == 1) {
                    sendByteStringLength(name, bb);
                }
                else {
                    sendByteStringLength(recipes[x].getName(), bb);
                }
                colourCode = recipes[x].getRecipeColourCode(this.player.getWurmId());
                bb.put(colourCode);
                linkKnown = (this.player.getPower() == 5 || (colourCode & 0x10) == 0x0);
                bb.put((byte)(linkKnown ? 1 : 0));
                bb.put((byte)0);
                sendByteStringLength(linkNotes, bb);
            }
        }
        else {
            sendByteStringLength(name, bb);
            bb.put((byte)0);
            if (name.contains("any ")) {
                bb.put((byte)4);
            }
            else {
                bb.put((byte)0);
            }
        }
    }
    
    private void sendPermissionsAddedManually(final String playerName, final long pid) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-26));
                bb.put((byte)1);
                bb.putLong(pid);
                sendByteStringLength(playerName, bb);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public final void sendPermissionsApplyChangesFailed(final int questionId, final String reason) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-26));
                bb.put((byte)2);
                bb.putInt(questionId);
                sendByteStringLength(reason, bb);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public final void sendHidePermissions() {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-26));
                bb.put((byte)4);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public final void sendShowPermissions(final int questionId, final boolean hasBackButton, final String objectType, final String objectName, final String ownerName, final boolean isOwner, final boolean canChangeOwner, final boolean isManaged, final boolean isManageEnabled, final String mayManageText, final String mayManageHover, final String warningText, final String messageOnTick, final String questionOnTick, final String messageUnTick, final String questionUnTick, final String allowAlliesText, final String allowCitizensText, final String allowKingdomText, final String allowEveryoneText, final String allowRolePermissionText, final String[] header1, final String[] header2, final String[] hover, final String[] trustedNames, final long[] trustedIds, final String[] friendNames, final long[] friendIds, final String mySettlement, final String[] citizenNames, final long[] citizenIds, final String[] permittedNames, final long[] permittedIds, final boolean[][] allowed) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-26));
                bb.put((byte)0);
                bb.putInt(questionId);
                byte flags = 0;
                if (hasBackButton) {
                    ++flags;
                }
                if (isOwner) {
                    flags += 2;
                }
                if (canChangeOwner) {
                    flags += 4;
                }
                if (isManaged) {
                    flags += 8;
                }
                if (isManageEnabled) {
                    flags += 16;
                }
                bb.put(flags);
                sendByteStringLength(objectType, bb);
                sendByteStringLength(objectName, bb);
                sendByteStringLength(ownerName, bb);
                sendByteStringLength(mayManageText, bb);
                sendByteStringLength(mayManageHover, bb);
                sendByteStringLength(warningText, bb);
                sendByteStringLength(messageOnTick, bb);
                sendByteStringLength(questionOnTick, bb);
                sendByteStringLength(messageUnTick, bb);
                sendByteStringLength(questionUnTick, bb);
                sendByteStringLength(allowAlliesText, bb);
                sendByteStringLength(allowCitizensText, bb);
                sendByteStringLength(allowKingdomText, bb);
                sendByteStringLength(allowEveryoneText, bb);
                sendByteStringLength(allowRolePermissionText, bb);
                bb.putInt(header1.length);
                for (int x = 0; x < header1.length; ++x) {
                    sendByteStringLength(header1[x], bb);
                    sendByteStringLength(header2[x], bb);
                    sendByteStringLength(hover[x], bb);
                }
                bb.putInt(trustedNames.length);
                for (int x = 0; x < trustedNames.length; ++x) {
                    sendByteStringLength(trustedNames[x], bb);
                    bb.putLong(trustedIds[x]);
                }
                bb.putInt(friendNames.length);
                for (int x = 0; x < friendNames.length; ++x) {
                    sendByteStringLength(friendNames[x], bb);
                    bb.putLong(friendIds[x]);
                }
                bb.putInt(citizenNames.length);
                if (citizenNames.length > 0) {
                    sendByteStringLength(mySettlement, bb);
                }
                for (int x = 0; x < citizenNames.length; ++x) {
                    sendByteStringLength(citizenNames[x], bb);
                    bb.putLong(citizenIds[x]);
                }
                bb.putInt(permittedNames.length);
                for (int x = 0; x < permittedNames.length; ++x) {
                    sendByteStringLength(permittedNames[x], bb);
                    bb.putLong(permittedIds[x]);
                    for (int y = 0; y < header1.length; ++y) {
                        bb.put((byte)(allowed[x][y] ? 1 : 0));
                    }
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendPersonalGoalsList(final Map<AchievementTemplate, Boolean> goals) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-39));
                bb.put((byte)0);
                bb.putInt(goals.size());
                for (final Map.Entry<AchievementTemplate, Boolean> goal : goals.entrySet()) {
                    bb.putInt(goal.getKey().getNumber());
                    String name = goal.getKey().getName();
                    if (goal.getKey().isInvisible()) {
                        name = "Personal goal";
                    }
                    final byte[] aNameStringArr = name.getBytes("UTF-8");
                    bb.put((byte)aNameStringArr.length);
                    bb.put(aNameStringArr);
                    final String adesc = goal.getKey().getRequirement();
                    final byte[] aDescStringArr = adesc.getBytes("UTF-8");
                    bb.put((byte)aDescStringArr.length);
                    bb.put(aDescStringArr);
                    bb.put(goal.getKey().getType());
                    bb.put((byte)(((boolean)goal.getValue()) ? 1 : 0));
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void updatePersonalGoal(final Achievement achievement, final boolean finished) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-40));
                bb.putInt(achievement.getAchievement());
                bb.put((byte)(finished ? 1 : 0));
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " achievement: " + achievement + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public final void sendShowPersonalGoalWindow(final boolean show) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-41));
                bb.put((byte)(show ? 1 : 0));
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " show personal goals: ", ex);
                this.player.setLink(false);
            }
        }
    }
    
    public final void sendNewKingdom(final byte kingdom, final String kingdomName, final String suffix) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)39);
                bb.put(kingdom);
                final byte[] kingdomNameStringArr = kingdomName.getBytes("UTF-8");
                bb.put((byte)kingdomNameStringArr.length);
                bb.put(kingdomNameStringArr);
                final byte[] suffixStringArr = suffix.getBytes("UTF-8");
                bb.put((byte)suffixStringArr.length);
                bb.put(suffixStringArr);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + " kingdomName " + kingdomName + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public final void sendAllKingdoms() {
        if (this.player != null && this.player.hasLink()) {
            try {
                final Kingdom[] allKingdom = Kingdoms.getAllKingdoms();
                final int numberOfKindoms = allKingdom.length;
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)40);
                bb.putInt(numberOfKindoms);
                for (int i = 0; i < allKingdom.length; ++i) {
                    bb.put(allKingdom[i].getId());
                    final byte[] kingdomNameStringArr = allKingdom[i].getName().getBytes("UTF-8");
                    bb.put((byte)kingdomNameStringArr.length);
                    bb.put(kingdomNameStringArr);
                    final byte[] suffixStringArr = allKingdom[i].getSuffix().getBytes("UTF-8");
                    bb.put((byte)suffixStringArr.length);
                    bb.put(suffixStringArr);
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public final void sendOwnTitles() {
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)(-33));
            if (this.player.getTitle() != null || (Features.Feature.COMPOUND_TITLES.isEnabled() && this.player.getSecondTitle() != null)) {
                sendShortStringLength(this.player.getTitleString(), bb);
            }
            else {
                sendShortStringLength("", bb);
            }
            if (this.player.getCultist() != null) {
                sendShortStringLength(this.player.getCultist().getCultistTitle(), bb);
            }
            else {
                sendShortStringLength("", bb);
            }
            this.connection.flush();
        }
        catch (Exception ex) {
            logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public final void sendSleepInfo() {
        try {
            final int sleepBonus = this.player.getSaveFile().getSleepLeft();
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)1);
            bb.put((byte)(this.player.getSaveFile().frozenSleep ? 0 : 1));
            bb.putInt(sleepBonus);
            this.connection.flush();
        }
        catch (Exception ex) {
            logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void sendClearFriendsList() {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)89);
                bb.put(PlayerOnlineStatus.DELETE_ME.getId());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " could not send empty friends list due to : " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendFriend(final PlayerState playerState) {
        this.sendFriend(playerState, false, "");
    }
    
    public void sendFriend(final PlayerState playerState, final String note) {
        this.sendFriend(playerState, true, note);
    }
    
    private void sendFriend(final PlayerState playerState, final boolean withNote, final String note) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)89);
                bb.put(playerState.getState().getId());
                sendByteStringLength(playerState.getPlayerName(), bb);
                bb.putLong(playerState.getWhenStateChanged());
                sendByteStringLength(playerState.getServerName(), bb);
                if (withNote) {
                    sendByteStringLength(note, bb);
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn(this.player.getName() + ':' + " wurmId " + this.player.getWurmId() + ' ' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendRarityEvent() {
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)56);
            this.connection.flush();
            this.sendServerMessage("You have a moment of inspiration...", 255, 200, 20);
        }
        catch (Exception ex) {
            logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
            this.player.setLink(false);
        }
    }
    
    public void sendClearTickets() {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-34));
                bb.putInt(0);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " could not send empty ticket list due to : " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendTicket(final Ticket ticket) {
        this.sendTickets(new Ticket[] { ticket });
    }
    
    public void sendTickets(final Ticket[] tickets) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-34));
                bb.putInt(tickets.length);
                for (int x = 0; x < tickets.length; ++x) {
                    final Ticket ticket = tickets[x];
                    bb.putLong(ticket.getWurmId());
                    bb.put(ticket.getTicketGroup(this.player));
                    sendByteStringLength(ticket.getTicketSummary(this.player), bb);
                    bb.put(ticket.getColourCode(this.player));
                    sendShortStringLength(ticket.getShortDescription(), bb);
                    final TicketAction[] ticketActions = ticket.getTicketActions(this.player);
                    bb.put((byte)ticketActions.length);
                    for (final TicketAction ticketAction : ticketActions) {
                        bb.putInt(ticketAction.getActionNo());
                        sendByteStringLength(ticketAction.getLine(this.player), bb);
                        sendByteStringLength(ticketAction.getNotePlus(this.player), bb);
                    }
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " could not send tickets due to : " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendTicket(final Ticket ticket, @Nullable final TicketAction ticketAction) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-34));
                bb.putInt(1);
                bb.putLong(ticket.getWurmId());
                bb.put(ticket.getTicketGroup(this.player));
                sendByteStringLength(ticket.getTicketSummary(this.player), bb);
                bb.put(ticket.getColourCode(this.player));
                sendShortStringLength(ticket.getDescription(), bb);
                if (ticketAction == null) {
                    bb.put((byte)0);
                }
                else {
                    bb.put((byte)1);
                    bb.putInt(ticketAction.getActionNo());
                    sendByteStringLength(ticketAction.getLine(this.player), bb);
                    sendByteStringLength(ticketAction.getNotePlus(this.player), bb);
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " could not send tickets due to : " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void removeTicket(final Ticket ticket) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-36));
                bb.putInt(1);
                bb.putLong(ticket.getWurmId());
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " could not remove ticket due to : " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendWater(final int tilex, final int tiley, final int layer, final int height) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-19));
                bb.putShort((short)tilex);
                bb.putShort((short)tiley);
                bb.put((byte)layer);
                bb.putShort((short)height);
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + " could not send water due to : " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendCreationsList() {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            if (!CreationWindowMethods.sendAllCraftingRecipes(this.connection, this.player)) {
                return;
            }
        }
        catch (Exception ex) {
            final String errorMessage = StringUtil.format("Failed to send creations list to %s.", this.player.getName());
            logInfo(errorMessage, ex);
            this.player.setLink(false);
        }
    }
    
    public void sendActionResult(final boolean success) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)(-46));
            bb.put((byte)5);
            bb.put((byte)(success ? 1 : 0));
            this.connection.flush();
        }
        catch (Exception ex) {
            final String errorMessage = StringUtil.format("Failed to send action result to %s.", this.player.getName());
            logInfo(errorMessage, ex);
            this.player.setLink(false);
        }
    }
    
    public void sendUpdateGroundItem(final Item item) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)(-46));
            bb.put((byte)6);
            bb.putLong(item.getWurmId());
            bb.putFloat(item.getQualityLevel());
            bb.putFloat(item.getWeightGrams() / 1000.0f);
            bb.putFloat(item.getDamage());
            this.connection.flush();
        }
        catch (Exception ex) {
            final String errorMessage = StringUtil.format("Failed to send update to %s.", this.player.getName());
            logInfo(errorMessage, ex);
            this.player.setLink(false);
        }
    }
    
    public void updateCreatureRarity(final long creatureId, final byte rarity) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)(-54));
            bb.putLong(creatureId);
            bb.put(rarity);
            this.connection.flush();
        }
        catch (Exception ex) {
            final String errorMessage = StringUtil.format("Failed to send rarity to %s.", this.player.getName());
            logInfo(errorMessage, ex);
            this.player.setLink(false);
        }
    }
    
    public void sendShowDeedPlan(final int qId, final String deedName, final int tokenX, final int tokenY, final int startX, final int startY, final int endX, final int endY, final int perimSize) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)(-51));
            bb.put((byte)0);
            bb.putInt(qId);
            sendByteStringLength(deedName, bb);
            bb.putInt(tokenX);
            bb.putInt(tokenY);
            bb.putInt(startX);
            bb.putInt(startY);
            bb.putInt(endX);
            bb.putInt(endY);
            bb.putInt(perimSize);
            this.connection.flush();
        }
        catch (Exception ex) {
            final String errorMessage = StringUtil.format("Failed to send show deed plan to %s.", this.player.getName());
            logInfo(errorMessage, ex);
            this.player.setLink(false);
        }
    }
    
    static String readByteString(final ByteBuffer bb) throws Exception {
        final int len = bb.get() & 0xFF;
        final byte[] bytes = new byte[len];
        bb.get(bytes);
        return new String(bytes, "UTF-8");
    }
    
    static String readShortString(final ByteBuffer bb) throws Exception {
        final int len = bb.getShort();
        final byte[] bytes = new byte[len];
        bb.get(bytes);
        return new String(bytes, "UTF-8");
    }
    
    static void sendByteStringLength(final String toSend, final ByteBuffer bb) throws Exception {
        final byte[] toSendStringArr = toSend.getBytes("UTF-8");
        bb.put((byte)toSendStringArr.length);
        bb.put(toSendStringArr);
    }
    
    static void sendShortStringLength(final String toSend, final ByteBuffer bb) throws Exception {
        final byte[] toSendStringArr = toSend.getBytes("UTF-8");
        bb.putShort((short)toSendStringArr.length);
        bb.put(toSendStringArr);
    }
    
    private static void flushSocketConnectionNoErrors(final SocketConnection aConnection) {
        if (aConnection == null || !aConnection.isWriting()) {
            return;
        }
        try {
            aConnection.flush();
        }
        catch (Exception ex) {}
    }
    
    public void sendValreiMapDataList(final Collection<ValreiMapData> datas) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        if (datas != null && !datas.isEmpty()) {
            for (final ValreiMapData d : datas) {
                this.sendValreiMapData(d);
            }
        }
    }
    
    public void sendValreiMapDataList(final List<ValreiMapData> datas) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        if (datas != null && !datas.isEmpty()) {
            for (final ValreiMapData d : datas) {
                this.sendValreiMapData(d);
            }
        }
    }
    
    public void sendValreiMapDataTimeUpdate(final ValreiMapData data) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)(-50));
            bb.put((byte)4);
            bb.putLong(data.getEntityId());
            bb.putLong(data.getTimeRemaining());
            this.connection.flush();
        }
        catch (Exception ex) {
            logWarn("Failed sending ValreiMapData time update for  " + data.getName(), ex);
            this.player.setLink(false);
        }
    }
    
    private void createValreiItemMessage(final ValreiMapData data, final ByteBuffer bb) throws UnsupportedEncodingException {
        bb.put((byte)1);
        bb.putLong(data.getEntityId());
        bb.putInt(data.getType());
        bb.putInt(data.getHexId());
        final String name = data.getName();
        final byte[] nameBytes = name.getBytes("UTF-8");
        bb.putShort((short)nameBytes.length);
        bb.put(nameBytes);
    }
    
    private void createValreiDemigodMessage(final ValreiMapData data, final ByteBuffer bb) throws UnsupportedEncodingException {
        bb.put((byte)2);
        bb.putLong(data.getEntityId());
        bb.putInt(data.getType());
        bb.putInt(data.getHexId());
        bb.putFloat(data.getBodyStr());
        bb.putFloat(data.getBodySta());
        bb.putFloat(data.getBodyCon());
        bb.putFloat(data.getMindLog());
        bb.putFloat(data.getMindSpe());
        bb.putFloat(data.getSoulStr());
        bb.putFloat(data.getSoulDep());
        final List<CollectedValreiItem> coll = data.getCarried();
        int count = 0;
        if (coll != null) {
            count = coll.size();
        }
        bb.putInt(count);
        for (int i = 0; i < count; ++i) {
            final CollectedValreiItem collItem = coll.get(i);
            final byte[] nameBytes = collItem.getName().getBytes("UTF-8");
            bb.putShort((short)nameBytes.length);
            bb.put(nameBytes);
            bb.putInt(collItem.getType());
        }
        final String name = data.getName();
        final byte[] nameBytes2 = name.getBytes("UTF-8");
        bb.putShort((short)nameBytes2.length);
        bb.put(nameBytes2);
    }
    
    private void createValreiAllyMessage(final ValreiMapData data, final ByteBuffer bb) throws UnsupportedEncodingException {
        bb.put((byte)3);
        bb.putLong(data.getEntityId());
        bb.putInt(data.getType());
        bb.putInt(data.getHexId());
        bb.putFloat(data.getBodyStr());
        bb.putFloat(data.getBodySta());
        bb.putFloat(data.getBodyCon());
        bb.putFloat(data.getMindLog());
        bb.putFloat(data.getMindSpe());
        bb.putFloat(data.getSoulStr());
        bb.putFloat(data.getSoulDep());
        final List<CollectedValreiItem> coll = data.getCarried();
        int count = 0;
        if (coll != null) {
            count = coll.size();
        }
        bb.putInt(count);
        for (int i = 0; i < count; ++i) {
            final CollectedValreiItem collItem = coll.get(i);
            final byte[] nameBytes = collItem.getName().getBytes("UTF-8");
            bb.putShort((short)nameBytes.length);
            bb.put(nameBytes);
            bb.putInt(collItem.getType());
        }
    }
    
    private void createValreiStandardMessage(final ValreiMapData data, final ByteBuffer bb) throws UnsupportedEncodingException {
        bb.put((byte)0);
        bb.putLong(data.getEntityId());
        bb.putInt(data.getType());
        bb.putInt(data.getHexId());
        bb.putFloat(data.getBodyStr());
        bb.putFloat(data.getBodySta());
        bb.putFloat(data.getBodyCon());
        bb.putFloat(data.getMindLog());
        bb.putFloat(data.getMindSpe());
        bb.putFloat(data.getSoulStr());
        bb.putFloat(data.getSoulDep());
        bb.putLong(data.getTimeRemaining());
        bb.putInt(data.getTargetHexId());
        final List<CollectedValreiItem> coll = data.getCarried();
        int count = 0;
        if (coll != null) {
            count = coll.size();
        }
        bb.putInt(count);
        for (int i = 0; i < count; ++i) {
            final CollectedValreiItem collItem = coll.get(i);
            final byte[] nameBytes = collItem.getName().getBytes("UTF-8");
            bb.putShort((short)nameBytes.length);
            bb.put(nameBytes);
            bb.putInt(collItem.getType());
        }
        if (data.isCustomGod()) {
            final String name = data.getName();
            final byte[] nameBytes2 = name.getBytes("UTF-8");
            bb.putShort((short)nameBytes2.length);
            bb.put(nameBytes2);
        }
    }
    
    public void sendValreiMapData(final ValreiMapData data) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)(-50));
            if (data.isItem()) {
                this.createValreiItemMessage(data, bb);
            }
            else if (data.isDemiGod()) {
                this.createValreiDemigodMessage(data, bb);
            }
            else if (data.isAlly() || data.isSentinel()) {
                this.createValreiAllyMessage(data, bb);
            }
            else {
                this.createValreiStandardMessage(data, bb);
            }
            this.connection.flush();
        }
        catch (Exception ex) {
            logWarn("Failed sending ValreiMapData. ", ex);
            this.player.setLink(false);
        }
    }
    
    public void sendViableVillageRecruitmentAds() {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final RecruitmentAd[] ads = RecruitmentAds.getKingdomAds(this.player.getKingdomId());
            if (ads == null) {
                return;
            }
            for (final RecruitmentAd ad : ads) {
                try {
                    final Village adVillage = Villages.getVillage(ad.getVillageId());
                    final PlayerInfo contact = PlayerInfoFactory.getPlayerInfoWithWurmId(ad.getContactId());
                    if (contact == null) {
                        throw new NoSuchPlayerException("No player with id: " + ad.getContactId());
                    }
                    final boolean isOnline = PlayerInfoFactory.isPlayerOnline(ad.getContactId());
                    final byte[] vName = adVillage.getName().getBytes("UTF-8");
                    final byte[] pName = contact.getName().getBytes("UTF-8");
                    final byte[] description = ad.getDescription().getBytes("UTF-8");
                    final ByteBuffer bb = this.connection.getBuffer();
                    bb.put((byte)(-42));
                    bb.put((byte)1);
                    bb.putInt(ad.getVillageId());
                    bb.putShort((short)vName.length);
                    bb.put(vName);
                    bb.putShort((short)pName.length);
                    bb.put(pName);
                    bb.put((byte)(isOnline ? 1 : 0));
                    bb.putShort((short)description.length);
                    bb.put(description);
                    this.connection.flush();
                }
                catch (NoSuchVillageException nsv) {
                    logWarn("No village for ad with village id: " + ad.getVillageId() + " :" + nsv.getMessage(), nsv);
                }
                catch (NoSuchPlayerException nsp) {
                    logWarn("No contact for ad with village id: " + ad.getVillageId() + " :" + nsp.getMessage(), nsp);
                }
            }
        }
        catch (Exception ex) {
            logWarn("Failed sending village recruitment ads. ", ex);
            this.player.setLink(false);
        }
    }
    
    public void sendPortalQuestion() {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)(-20));
            this.connection.flush();
        }
        catch (Exception ex) {
            final String errorMessage = StringUtil.format("Failed to send portal command to %s.", this.player.getName());
            logInfo(errorMessage, ex);
            this.player.setLink(false);
        }
    }
    
    public void sendOpenPlanWindow(final String planName, final byte planDirection, final byte planLength, final byte planWidth, final Point start, final Point end, final byte planMaterial, final String planCustom) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)(-25));
            bb.put((byte)1);
            sendByteStringLength(planName, bb);
            bb.put(planDirection);
            bb.put(planLength);
            bb.put(planWidth);
            bb.putInt(start.getX());
            bb.putInt(start.getY());
            bb.putInt(start.getH());
            bb.putInt(end.getX());
            bb.putInt(end.getY());
            bb.putInt(end.getH());
            bb.put(planMaterial);
            sendByteStringLength(planCustom, bb);
            this.connection.flush();
        }
        catch (Exception ex) {
            final String errorMessage = StringUtil.format("Failed to send open bridge plan window to %s.", this.player.getName());
            logInfo(errorMessage, ex);
            this.player.setLink(false);
        }
    }
    
    public void sendClosePlanWindow() {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)(-25));
            bb.put((byte)2);
            this.connection.flush();
        }
        catch (Exception ex) {
            final String errorMessage = StringUtil.format("Failed to send close bridge plan window to %s.", this.player.getName());
            logInfo(errorMessage, ex);
            this.player.setLink(false);
        }
    }
    
    public void sendTargetStatus(final long targetId, final byte statusType, final float statusLevel) {
        if (this.player == null || !this.player.hasLink()) {
            return;
        }
        try {
            final ByteBuffer bb = this.connection.getBuffer();
            bb.put((byte)(-27));
            bb.putLong(targetId);
            bb.put(statusType);
            bb.putFloat(statusLevel);
            this.connection.flush();
        }
        catch (Exception ex) {
            final String errorMessage = StringUtil.format("Failed to send conquer status command to %s.", this.player.getName());
            logInfo(errorMessage, ex);
            this.player.setLink(false);
        }
    }
    
    private static void logInfo(final String msg) {
        Communicator.logger.info(msg);
    }
    
    private static void logInfo(final String msg, final Throwable thrown) {
        Communicator.logger.log(Level.INFO, msg, thrown);
    }
    
    private static void logWarn(final String msg) {
        Communicator.logger.warning(msg);
    }
    
    private static void logWarn(final String msg, final Throwable thrown) {
        Communicator.logger.log(Level.WARNING, msg, thrown);
    }
    
    public int getAvailableMoves() {
        return this.availableMoves;
    }
    
    private void handleHashMessageListWildHives(final String message) {
        final StringTokenizer tokens = new StringTokenizer(message);
        tokens.nextToken();
        if (tokens.hasMoreTokens()) {
            this.sendNormalServerMessage("List of hives that have two queens!.");
            int wildcount = 0;
            final Item[] hives3;
            final Item[] wildhives = hives3 = Zones.getHives(1239);
            for (final Item hive : hives3) {
                if (hive.hasTwoQueens()) {
                    this.sendNormalServerMessage("Wild hive @ " + hive.getTileX() + "," + hive.getTileY() + " has two queens.");
                    ++wildcount;
                }
            }
            int domesticcount = 0;
            final Item[] hives4;
            final Item[] hives = hives4 = Zones.getHives(1175);
            for (final Item hive2 : hives4) {
                if (hive2.hasTwoQueens()) {
                    this.sendNormalServerMessage("Domestic hive @ " + hive2.getTileX() + "," + hive2.getTileY() + " has two queens.");
                    ++domesticcount;
                }
            }
            this.sendNormalServerMessage("Found " + wildcount + " wild hives and " + domesticcount + " domestic hives with two queens.");
        }
        else {
            this.sendNormalServerMessage("List of wild hives and any honey in them!.");
            int totalHoney = 0;
            int totalwax = 0;
            final Item[] hives5;
            final Item[] hives2 = hives5 = Zones.getHives(1239);
            for (final Item hive3 : hives5) {
                int honey = 0;
                int wax = 0;
                final Item[] itemsAsArray;
                final Item[] items = itemsAsArray = hive3.getItemsAsArray();
                for (final Item item : itemsAsArray) {
                    if (item.getTemplateId() == 70) {
                        honey += item.getWeightGrams();
                    }
                    else if (item.getTemplateId() == 1254) {
                        wax += item.getWeightGrams();
                    }
                }
                this.sendNormalServerMessage("Wild hive @ " + hive3.getTileX() + "," + hive3.getTileY() + " honey:" + honey + " and " + wax + " beeswax and has " + hive3.getAuxData() + " queens.");
                totalHoney += honey;
                totalwax += wax;
            }
            this.sendNormalServerMessage("Found " + hives2.length + " wild hives and " + totalHoney + " total honey and " + totalwax + " total wax.");
        }
    }
    
    private void handleHashMessageRemoveWildHives() {
        final Item[] hives2;
        final Item[] hives = hives2 = Zones.getHives(1239);
        for (final Item hive : hives2) {
            this.sendNormalServerMessage("Removing Wild Hive @ " + hive.getTileX() + "," + hive.getTileY());
            Server.setCheckHive(hive.getTileX(), hive.getTileY(), false);
            final Item[] itemsAsArray;
            final Item[] items = itemsAsArray = hive.getItemsAsArray();
            for (final Item item : itemsAsArray) {
                Items.destroyItem(item.getWurmId());
            }
            Items.destroyItem(hive.getWurmId());
        }
        this.sendNormalServerMessage("Removed " + hives.length + " wild hives.");
        if (Servers.isThisATestServer()) {
            Players.getInstance().sendGmMessage(null, "System", "Debug: Removed " + hives.length + " wild hives.", false);
        }
    }
    
    private void handleHashMessageRemoveKnownRecipes(final String message) {
        if (this.player.hasLink()) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String targetName = "";
            long tId = -10L;
            if (tokens.hasMoreTokens()) {
                targetName = tokens.nextToken().trim();
                try {
                    final short recipeId = Short.parseShort(targetName);
                    final Recipe recipe = Recipes.getRecipeById(recipeId);
                    if (recipe == null) {
                        this.sendNormalServerMessage("Recipe " + recipeId + " not found!");
                    }
                    else if (!recipe.isKnown()) {
                        this.sendNormalServerMessage("Recipe " + recipeId + " is known to all and thus cannot be remoed!");
                    }
                    else {
                        RecipesByPlayer.deleteRecipesByNumber(recipeId);
                        this.sendSafeServerMessage("Removed recipe " + recipeId + " from everyone!");
                        if (Servers.isThisATestServer()) {
                            Players.getInstance().sendGmMessage(null, "System", "Debug: Removed known recipe of " + recipeId + " from all.", false);
                        }
                        this.sendNormalServerMessage("Note: players will have to relog for cookbook to be updated.");
                    }
                    return;
                }
                catch (NumberFormatException ex) {
                    targetName = LoginHandler.raiseFirstLetter(targetName);
                    tId = PlayerInfoFactory.getWurmId(targetName);
                    if (tId == -10L) {
                        this.sendSafeServerMessage("Unknown player " + targetName + ".");
                        return;
                    }
                }
            }
            if (tId != -10L) {
                if (tokens.hasMoreTokens()) {
                    final String srecipe = tokens.nextToken().trim();
                    try {
                        final short recipeId2 = Short.parseShort(srecipe);
                        final Recipe recipe2 = Recipes.getRecipeById(recipeId2);
                        if (recipe2 == null) {
                            this.sendNormalServerMessage("Recipe " + recipeId2 + " not found!");
                            return;
                        }
                        if (recipe2.isKnown()) {
                            this.sendNormalServerMessage("Recipe " + recipeId2 + " is known to all and thus cannot be removed!");
                            return;
                        }
                        RecipesByPlayer.removeRecipeForPlayer(tId, recipeId2);
                        this.sendSafeServerMessage("Removed recipe " + recipeId2 + " from " + targetName + ".");
                        if (Servers.isThisATestServer()) {
                            Players.getInstance().sendGmMessage(null, "System", "Debug: Removed recipe " + recipeId2 + " from " + targetName + ".", false);
                        }
                        this.sendNormalServerMessage("Note: player will have to relog for cookbook to be updated.");
                        try {
                            final Player p = Players.getInstance().getPlayer(targetName);
                            p.getCommunicator().sendNormalServerMessage("Please relog so your cookbook can be updated!", (byte)2);
                        }
                        catch (NoSuchPlayerException ex2) {}
                    }
                    catch (NumberFormatException nfe) {
                        this.sendNormalServerMessage("Failed to parse the recipe id!");
                    }
                }
                else {
                    RecipesByPlayer.deleteRecipesForPlayer(tId);
                    this.sendSafeServerMessage("Removed known recipes for " + targetName + ".");
                    if (Servers.isThisATestServer()) {
                        Players.getInstance().sendGmMessage(null, "System", "Debug: Removed known recipes for " + targetName + ".", false);
                    }
                    this.sendNormalServerMessage("Note: players will have to relog for cookbook to be updated.");
                }
            }
            else {
                this.sendNormalServerMessage("usage: #removeknownrecipes [player] [recipeId]");
                this.sendNormalServerMessage("    removes all known recieps from specified player ");
                this.sendNormalServerMessage("    OR the specified recipe from all players.");
                this.sendNormalServerMessage("    OR the specified recipe from the specified player.");
            }
        }
    }
    
    private void handleHashMessageRemoveNamedRecipe(final String message) {
        if (this.player.hasLink()) {
            if (this.player.getLogger() != null) {
                this.player.getLogger().info(message);
            }
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String targetName = "";
            short recipeId = -1;
            long tId = -10L;
            Recipe recipe = null;
            String namer = null;
            if (tokens.hasMoreTokens()) {
                targetName = tokens.nextToken().trim();
                try {
                    recipeId = Short.parseShort(targetName);
                    recipe = Recipes.getRecipeById(recipeId);
                    if (recipe == null) {
                        this.sendSafeServerMessage("Recipe " + recipeId + " not found!");
                        return;
                    }
                    namer = Recipes.getRecipeNamer(recipeId);
                    if (namer == null) {
                        this.sendSafeServerMessage("Recipe (" + recipeId + ") has no namer.");
                        return;
                    }
                }
                catch (NumberFormatException nfe) {
                    namer = LoginHandler.raiseFirstLetter(targetName);
                    tId = PlayerInfoFactory.getWurmId(namer);
                    if (tId == -10L) {
                        this.sendSafeServerMessage("Unknown player " + targetName + ".");
                        return;
                    }
                    recipeId = Recipes.getNamedRecipe(namer);
                    if (recipeId == 0) {
                        this.sendSafeServerMessage("Player (" + namer + ") has no named recipes.");
                        return;
                    }
                    recipe = Recipes.getRecipeById(recipeId);
                    if (recipe == null) {
                        this.sendSafeServerMessage("Recipe " + recipeId + " not found!");
                        return;
                    }
                }
                String remove = "";
                if (tokens.hasMoreTokens()) {
                    remove = tokens.nextToken().trim();
                }
                if (remove.equalsIgnoreCase("remove")) {
                    Recipes.removeRecipeNamer(recipeId);
                    this.sendNormalServerMessage("Removed namer (" + namer + ") from recipe (" + recipeId + ").");
                    if (Servers.isThisATestServer()) {
                        Players.getInstance().sendGmMessage(null, "System", "Debug: Removed named recipe " + recipeId + " from player " + namer + ".", false);
                    }
                    this.sendNormalServerMessage("Note: player will have to relog for cookbook to be updated.");
                    try {
                        final Player p = Players.getInstance().getPlayer(targetName);
                        p.getCommunicator().sendNormalServerMessage("Please relog so your cookbook can be updated!", (byte)2);
                    }
                    catch (NoSuchPlayerException ex) {}
                }
                else {
                    this.sendNormalServerMessage("Player (" + namer + ") has " + recipe.getName() + " (" + recipeId + ") named after them.");
                }
            }
            else {
                this.sendNormalServerMessage("usage: #removeNamedRecipe [player or recipeId] [\"remove\"]");
                this.sendNormalServerMessage("    show recipe/player from named list ");
                this.sendNormalServerMessage("    OR remove entry from named list (if 2nd param is \"remove\").");
            }
        }
    }
    
    private void handleHashMessageWindPower(final String message) {
        if (this.player.hasLink()) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            String newPower = "";
            if (tokens.hasMoreTokens()) {
                newPower = tokens.nextToken();
            }
            try {
                final float pow = Float.parseFloat(newPower);
                if (pow < 0.0f || pow > 1.0f) {
                    this.sendNormalServerMessage("Usage: #setwindpower [-0.5 to 0.5]");
                    return;
                }
                Server.getWeather().setWindOnly(Server.getWeather().getWindRotation(), pow, Server.getWeather().getWindDir());
                this.sendWeather();
                this.sendNormalServerMessage("New wind power successfully set to " + newPower + ".");
            }
            catch (NumberFormatException e) {
                this.sendNormalServerMessage("Usage: #setwindpower [-0.5 to 0.5]");
            }
        }
    }
    
    private void handleHashMessageShowMe(final String message) {
        if (this.player.hasLink()) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            final String tabName = tokens.hasMoreTokens() ? tokens.nextToken() : "";
            final boolean justGM = tabName.equalsIgnoreCase("GM");
            if (justGM) {
                this.sendNormalServerMessage("You are now visible to all in GM Tab");
            }
            else {
                this.sendNormalServerMessage("You are now visible to all in GM and MGMT Tabs");
            }
            Players.getInstance().sendToTabs(this.player, true, justGM);
        }
    }
    
    private void handleHashMessageHideMe(final String message) {
        if (this.player.hasLink()) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            final String tabName = tokens.hasMoreTokens() ? tokens.nextToken() : "";
            final boolean justGM = tabName.equalsIgnoreCase("GM");
            if (justGM) {
                this.sendNormalServerMessage("You are now invisible to all of lower power than you are in GM Tab.");
            }
            else {
                this.sendNormalServerMessage("You are now invisible to all of lower power than you are in GM Tab and all in MGMT Tab.");
            }
            Players.getInstance().sendToTabs(this.player, false, justGM);
        }
    }
    
    private void handleHashMessageSetAffinityChance(final String message) {
        if (this.player.hasLink()) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            if (tokens.hasMoreTokens()) {
                final int oldChance = Player.newAffinityChance;
                int newChance = Integer.parseInt(tokens.nextToken());
                newChance = (Player.newAffinityChance = Math.max(Math.min(10000, newChance), 100));
                this.sendNormalServerMessage("Affinity chance now set to 1 / " + newChance + " from 1 / " + oldChance + ".");
                Skill.affinityDebug.log(Level.INFO, this.getPlayer() + " changed affinity chance to 1/" + newChance + " from 1/" + oldChance + ".");
            }
        }
    }
    
    private void handleHashMessageGetAffinityStats() {
        if (this.player.hasLink()) {
            this.sendNormalServerMessage("Total of " + Skill.getTotalAffinityChecks() + " affinity checks since last restart with a total of " + Skill.getTotalAffinitiesGiven() + " affinities given since last restart. Current affinity chance is 1 / " + Player.newAffinityChance + ".");
        }
    }
    
    private void handleHashMessageTestSpecial(final String message) {
        if (Servers.isThisATestServer() && this.player != null && this.player.hasLink() && this.player.getPower() == 5) {
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            if (!tokens.hasMoreTokens()) {
                this.sendSafeServerMessage("Command usage is: #testspecial <special> where special is one of: xmas, easter, wurm, halloween or none");
                return;
            }
            final String special = tokens.nextToken();
            if (special.equals("xmas") || WurmCalendar.isTestChristmas()) {
                this.sendServerMessage("There is a delay before santas arrive / depart, please wait!", 255, 177, 40);
            }
            WurmCalendar.toggleSpecial(special);
            this.sendServerMessage("You will been to relog to see the new mappings!", 255, 177, 40);
        }
    }
    
    private void handleHashMessageListDens() {
        final Logger playerLogger = this.player.getLogger();
        if (playerLogger != null) {
            playerLogger.log(Level.INFO, "Player attempted to use the #listdens command to see all the unique dens");
        }
        final Map<Integer, Den> dens = Dens.getDens();
        if (dens.size() == 0) {
            this.sendSafeServerMessage("There are no unique dens on this server.");
            return;
        }
        this.sendSafeServerMessage("The following dens have been found:");
        final String basicInfo = "%d: At x=%d, y=%d, belonging to template ID: %d. ";
        final String templateInfo = "Template ID is a %s. ";
        final String aliveInfo = "Creature is %salive. ";
        final String wurmIdInfo = "Wurmid: %d";
        final String templateNotFoundInfo = "This template ID is unknown.";
        final String multipleCreaturesInfo = "Multiple living creatures with this template ID were found.";
        int i = 1;
        for (final Den d : dens.values()) {
            final StringBuilder sb = new StringBuilder(String.format("%d: At x=%d, y=%d, belonging to template ID: %d. ", i, d.getTilex(), d.getTiley(), d.getTemplateId()));
            try {
                final CreatureTemplate template = CreatureTemplateFactory.getInstance().getTemplate(d.getTemplateId());
                sb.append(String.format("Template ID is a %s. ", template.getName()));
                final Creature creature = Creatures.getInstance().getUniqueCreatureWithTemplate(d.getTemplateId());
                if (creature != null) {
                    sb.append(String.format("Creature is %salive. ", ""));
                    sb.append(String.format("Wurmid: %d", creature.getWurmId()));
                }
                else {
                    sb.append(String.format("Creature is %salive. ", "not "));
                }
            }
            catch (NoSuchCreatureTemplateException ex) {
                sb.append("This template ID is unknown.");
            }
            catch (UnsupportedOperationException ex2) {
                sb.append("Multiple living creatures with this template ID were found.");
            }
            this.sendSafeServerMessage(sb.toString());
            ++i;
        }
    }
    
    private void handleHashMessageRemoveDen(final String message) {
        final StringTokenizer tokens = new StringTokenizer(message);
        if (tokens.countTokens() != 2) {
            this.sendSafeServerMessage("Command usage is: #removeden <templateID>");
            return;
        }
        tokens.nextToken();
        final String requestedTemplateId = tokens.nextToken();
        final Logger playerLogger = this.player.getLogger();
        if (playerLogger != null) {
            playerLogger.log(Level.INFO, "Player attempted to delete a unique den with ID: " + requestedTemplateId);
        }
        int templateId;
        try {
            templateId = Integer.parseInt(requestedTemplateId);
        }
        catch (NumberFormatException ex) {
            this.sendSafeServerMessage("The template id: " + requestedTemplateId + " is not a valid number.");
            return;
        }
        Dens.deleteDen(templateId);
        this.sendSafeServerMessage("Den with template ID: " + templateId + " has been deleted.");
    }
    
    private void handleHashMessageJournalInfo(final String message) {
        final StringTokenizer tokens = new StringTokenizer(message);
        if (tokens.countTokens() != 2) {
            this.sendSafeServerMessage("Command usage is: #journalinfo <name>");
            return;
        }
        tokens.nextToken();
        final String name = tokens.nextToken();
        PlayerJournal.sendJournalInfoBML(this.player, PlayerInfoFactory.getPlayerInfoWithName(name).wurmId);
    }
    
    private void handleTestFish(final String message) {
        if (Servers.isThisATestServer() && this.player != null && this.player.hasLink() && this.player.getPower() == 5) {
            final StringTokenizer tokens = new StringTokenizer(message);
            if (tokens.countTokens() < 2) {
                this.sendSafeServerMessage("Command usage is: #testfish <fishname> [ql]");
                return;
            }
            tokens.nextToken();
            final String fishName = tokens.nextToken().toLowerCase();
            final FishEnums.FishData fd = FishEnums.FishData.fromName(fishName);
            if (fd == null) {
                this.sendSafeServerMessage("Unknown fish name " + fishName + ".");
                return;
            }
            float ql = 100.0f;
            if (tokens.hasMoreTokens()) {
                final String str = tokens.nextToken();
                try {
                    final float newql = Float.parseFloat(str);
                    ql = Math.max(Math.min(newql, 100.0f), 1.0f);
                }
                catch (NumberFormatException ex) {
                    this.sendSafeServerMessage("The ql (" + str + ") is not a valid number, so using 100ql.");
                }
            }
            final float dist = 1.5f;
            final Point4f mid = MethodsFishing.calcSpot(this.player.getPosX(), this.player.getPosY(), this.player.getStatus().getRotation(), 1.5f);
            final float angle = Creature.normalizeAngle(this.player.getStatus().getRotation() - 60.0f);
            final Point4f start = MethodsFishing.calcSpot(mid.getPosX(), mid.getPosY(), angle, 10.0f);
            final Point4f end = MethodsFishing.calcSpot(start.getPosX(), start.getPosY(), start.getRot(), 12.0f);
            final Creature fish = MethodsFishing.makeFishCreature(this.player, start, fishName, ql, (byte)fd.getTypeId(), 1.0f, end);
            if (fish == null) {
                this.sendSafeServerMessage("Cannot make that fish? " + fishName + ".");
                return;
            }
            MethodsFishing.addMarker(this.player, fish.getName() + " start", start);
            MethodsFishing.addMarker(this.player, fish.getName() + " end", end);
            this.sendSafeServerMessage(fishName + " starts its journey.");
        }
    }
    
    private Pair<String, Byte> validatePower(final String requestedPower, final int ownPower) {
        String error = "";
        byte newPower = 0;
        try {
            newPower = Byte.parseByte(requestedPower);
            if (newPower < 0 || newPower > 5) {
                error = "Could not set power. Power: " + requestedPower + " is not recognized. Only use numbers between 0 and 5";
            }
            else if (newPower > ownPower) {
                error = "You can't set powers above your level";
            }
        }
        catch (NumberFormatException e2) {
            error = "Could not set power. Power: " + requestedPower + " is not recognized. Only use numbers between 0 and 5";
        }
        catch (Exception e) {
            error = "Could not set power. Unknown exception: " + e.getMessage();
        }
        return new Pair<String, Byte>(error, newPower);
    }
    
    private String getPowerName(final int power) {
        String powString = "normal adventurer";
        if (power == 1) {
            powString = "hero";
        }
        else if (power == 2) {
            powString = "demigod";
        }
        else if (power == 3) {
            powString = "high god";
        }
        else if (power == 4) {
            powString = "arch angel";
        }
        else if (power == 5) {
            powString = "implementor";
        }
        return powString;
    }
    
    private void handleHashMessageSetPower(final String message) {
        final Logger playerLogger = this.player.getLogger();
        final StringTokenizer tokens = new StringTokenizer(message);
        if (tokens.countTokens() != 3) {
            this.sendSafeServerMessage("Command usage is: #setpower <player> <power>, where the power is a number between 0 to 5");
            return;
        }
        tokens.nextToken();
        final String playerName = LoginHandler.raiseFirstLetter(tokens.nextToken());
        final String requestedPower = tokens.nextToken();
        final Pair<String, Byte> errorAndPower = this.validatePower(requestedPower, this.player.getPower());
        if (!errorAndPower.getKey().equals("")) {
            this.sendSafeServerMessage(errorAndPower.getKey());
            return;
        }
        final byte newPower = errorAndPower.getValue();
        boolean crossServer = false;
        int destServer = -1;
        if (playerLogger != null) {
            playerLogger.log(Level.INFO, "Tried to set the power of " + playerName + " to level" + newPower);
        }
        boolean isLoggedIn = true;
        Player p = null;
        try {
            p = Players.getInstance().getPlayer(playerName);
        }
        catch (NoSuchPlayerException ex) {
            isLoggedIn = false;
        }
        Label_0442: {
            if (isLoggedIn) {
                if (p.getPower() > this.player.getPower()) {
                    this.sendSafeServerMessage("They are more powerful than you. You cannot set their power.");
                    return;
                }
                try {
                    p.setPower(newPower);
                    break Label_0442;
                }
                catch (IOException e) {
                    this.sendSafeServerMessage("Could not set power. Error with saving the new power.");
                    if (e.getMessage() != null && !e.getMessage().equals("")) {
                        this.sendSafeServerMessage("Exception: " + e.getMessage());
                    }
                    return;
                }
            }
            try {
                final PlayerInfo pinf = PlayerInfoFactory.createPlayerInfo(playerName);
                final PlayerState pstate = PlayerInfoFactory.getPlayerState(playerName);
                if (pstate == null) {
                    this.sendSafeServerMessage("Player: " + playerName + " could not be found");
                    return;
                }
                if (pstate.getServerId() != Servers.localServer.id) {
                    this.sendSafeServerMessage("User is not on your server. Trying set status globally...");
                    crossServer = true;
                    destServer = pstate.getServerId();
                }
                else {
                    pinf.load();
                    if (pinf.getPower() > this.player.getPower()) {
                        this.sendSafeServerMessage("You can't set powers above your level");
                        return;
                    }
                    pinf.setPower(newPower);
                    pinf.save();
                }
            }
            catch (IOException e) {
                this.sendSafeServerMessage("Could not set power. Error with loading or saving data for player: " + playerName + ". Player might never have visited this server.");
                return;
            }
        }
        if (crossServer) {
            final WcSetPower wsp = new WcSetPower(playerName, newPower, this.getPlayerName(false), this.player.getPower(), "");
            wsp.sendToServer(destServer);
            return;
        }
        final String powString = this.getPowerName(newPower);
        if (p != null && p.hasLink()) {
            p.getCommunicator().sendSafeServerMessage("Your status has been set by " + this.getPlayerName(false) + " to " + powString + "!");
        }
        this.sendSafeServerMessage("You set the power of " + playerName + " to the status of " + powString + ".");
    }
    
    public void sendValreiFightList(final int listPage) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                final ArrayList<ValreiFightHistory> fights = ValreiFightHistoryManager.getInstance().get10Fights(listPage);
                final int totalPages = ValreiFightHistoryManager.getInstance().getNumberOfFights() / 10;
                bb.put((byte)(-62));
                bb.put((byte)0);
                bb.putInt(totalPages);
                bb.putInt(listPage);
                if (fights == null) {
                    bb.put((byte)0);
                }
                else {
                    bb.put((byte)fights.size());
                    for (final ValreiFightHistory f : fights) {
                        bb.putLong(f.getFightId());
                        sendShortStringLength(f.getPreviewString(), bb);
                    }
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendValreiFightDetails(final long fightId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                final ValreiFightHistory fight = ValreiFightHistoryManager.getInstance().getFight(fightId);
                final int totalActions = fight.getTotalActions() + 1;
                final HashMap<Long, ValreiFightHistory.ValreiFighter> fighters = fight.getFighters();
                bb.put((byte)(-62));
                bb.put((byte)1);
                bb.putLong(fight.getFightId());
                bb.putLong(fight.getFightTime());
                final long fighter1 = ValreiConstants.getFightActionActor(fight.getFightAction(0));
                final long fighter2 = ValreiConstants.getFightActionActor(fight.getFightAction(1));
                bb.putLong(fighter1);
                sendByteStringLength(fighters.get(fighter1).getName(), bb);
                bb.putLong(fighter2);
                sendByteStringLength(fighters.get(fighter2).getName(), bb);
                bb.putInt(totalActions);
                for (int i = 0; i < totalActions; ++i) {
                    final ValreiConstants.ValreiFightAction tempAction = fight.getFightAction(i);
                    bb.putShort(tempAction.getActionId());
                    bb.put((byte)tempAction.getActionData().length);
                    bb.put(tempAction.getActionData());
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logInfo(this.player.getName() + ':' + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendCancelPlacingItem() {
        if (this.player != null && this.player.hasLink()) {
            this.player.setPlacingItem(false);
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-63));
                bb.putLong(-10L);
                this.connection.flush();
            }
            catch (Exception ex) {
                this.player.setLink(false);
            }
        }
    }
    
    public void sendPlaceItem(final Item item) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final long id = item.getWurmId();
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-63));
                bb.putLong(id);
                byte[] tempStringArr = item.getName().getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                tempStringArr = item.getModelName().getBytes("UTF-8");
                bb.put((byte)tempStringArr.length);
                bb.put(tempStringArr);
                bb.put(item.getMaterial());
                bb.putFloat(item.getSizeMod());
                bb.put(item.getRarity());
                this.connection.flush();
                if (Communicator.logger.isLoggable(Level.FINER)) {
                    Communicator.logger.finer(this.player.getName() + " sent item to place " + item.getName() + " - " + item.getWurmId());
                }
            }
            catch (Exception ex) {
                logWarn("Failed to send item to place: " + this.player.getName() + ':' + item.getWurmId() + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private void reallyHandle_CMD_PLACE_ITEM(final ByteBuffer bb) throws Exception {
        final long itemId = bb.getLong();
        final long parentId = bb.getLong();
        final float xPos = bb.getFloat();
        final float yPos = bb.getFloat();
        final float zPos = bb.getFloat();
        final float rotation = bb.getFloat();
        MethodsItems.handlePlaceItem(this.player, itemId, parentId, xPos, yPos, zPos, rotation);
    }
    
    private void reallyHandle_CMD_OPENCLOSE_WINDOW(final ByteBuffer bb) throws Exception {
        final boolean opened = bb.get() == 0;
        final short windowId = bb.getShort();
        switch (windowId) {
            case 20: {
                PlayerTutorial.firePlayerTrigger(this.player.getWurmId(), opened ? PlayerTutorial.PlayerTrigger.ENABLED_INVENTORY : PlayerTutorial.PlayerTrigger.DISABLED_INVENTORY);
                break;
            }
            case 40: {
                if (opened) {
                    PlayerTutorial.firePlayerTrigger(this.player.getWurmId(), PlayerTutorial.PlayerTrigger.ENABLED_CREATION);
                    break;
                }
                break;
            }
            case 26: {
                if (opened) {
                    PlayerTutorial.firePlayerTrigger(this.player.getWurmId(), PlayerTutorial.PlayerTrigger.ENABLED_CHARACTER);
                    break;
                }
                break;
            }
        }
    }
    
    private void reallyHandle_CMD_PERSONAL_GOAL_LIST(final ByteBuffer bb) throws Exception {
        final byte cmdType = bb.get();
        switch (cmdType) {
            case 5: {
                final byte parentId = bb.get();
                final byte tutorialId = bb.get();
                if (parentId >= 0) {
                    PlayerTutorial.startNewTutorial(this.player, PlayerTutorial.TutorialType.values()[parentId], tutorialId);
                    break;
                }
                PlayerTutorial.startNewTutorial(this.player, PlayerTutorial.TutorialType.values()[tutorialId], 0);
                break;
            }
        }
    }
    
    public void sendOpenWindow(final short windowId, final boolean blink) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-66));
                bb.put((byte)0);
                bb.putShort(windowId);
                bb.put((byte)(blink ? 1 : 0));
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn("Failed to send open window: " + this.player.getName() + ":" + windowId + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendCloseWindow(final short windowId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-66));
                bb.put((byte)1);
                bb.putShort(windowId);
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn("Failed to send close window: " + this.player.getName() + ":" + windowId + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendToggleQuickbarBtn(final short buttonId, final boolean enabled) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-66));
                bb.put((byte)(enabled ? 4 : 3));
                bb.putShort(buttonId);
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn("Failed to send toggle quickbar btn: " + this.player.getName() + ":" + buttonId + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendToggleAllQuickbarBtns(final boolean enabled) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-66));
                bb.put((byte)(enabled ? 6 : 5));
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn("Failed to send toggle all quickbar btn: " + this.player.getName() + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private void reallyHandle_CMD_FISH(final ByteBuffer bb) throws Exception {
        final byte subCommand = bb.get();
        if (this.player.getPower() >= 5 && Servers.isThisATestServer()) {
            this.sendNormalServerMessage("CMD_FISH<-" + MethodsFishing.fromCommand(subCommand));
        }
        switch (subCommand) {
            case 9: {
                final float posX = bb.getFloat();
                final float posY = bb.getFloat();
                MethodsFishing.fromClient(this.player, subCommand, posX, posY);
                break;
            }
            case 10:
            case 11: {
                MethodsFishing.fromClient(this.player, subCommand, -1.0f, -1.0f);
                break;
            }
            case 27: {
                MethodsFishing.fromClient(this.player, subCommand, -1.0f, -1.0f);
                break;
            }
            case 26: {
                final float posX = bb.getFloat();
                final float posY = bb.getFloat();
                MethodsFishing.fromClient(this.player, subCommand, posX, posY);
                break;
            }
            default: {
                logWarn("Bad sub command for CMD_FISH: " + this.player.getName() + ':' + subCommand + ", ");
                break;
            }
        }
    }
    
    public void sendFishCasted(final long playerId, final float posX, final float posY, final byte floatType) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-64));
                bb.put((byte)9);
                bb.putLong(playerId);
                bb.putFloat(posX);
                bb.putFloat(posY);
                bb.put(floatType);
                this.connection.flush();
                if (this.player.getPower() >= 5 && Servers.isThisATestServer()) {
                    this.sendNormalServerMessage("CMD_FISH->" + MethodsFishing.fromCommand((byte)9));
                }
            }
            catch (Exception ex) {
                logWarn("Failed to send rod casted for " + playerId + " : " + this.player.getName() + ':' + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendSpearStrike(final long playerId, final float posX, final float posY) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-64));
                bb.put((byte)26);
                bb.putLong(playerId);
                bb.putFloat(posX);
                bb.putFloat(posY);
                this.connection.flush();
                if (this.player.getPower() >= 5 && Servers.isThisATestServer()) {
                    this.sendNormalServerMessage("CMD_FISH->" + MethodsFishing.fromCommand((byte)26));
                }
            }
            catch (Exception ex) {
                logWarn("Failed to send rod casted for " + playerId + " : " + this.player.getName() + ':' + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendFishStart(final float minRadius, final float maxRadius, final byte rodType, final byte rodMaterial, final byte reelType, final byte reelMaterial, final byte floatType, final byte baitType) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-64));
                bb.put((byte)0);
                bb.putFloat(minRadius);
                bb.putFloat(maxRadius);
                bb.put(rodType);
                bb.put(rodMaterial);
                bb.put(reelType);
                bb.put(reelMaterial);
                bb.put(floatType);
                bb.put(baitType);
                bb.put((byte)0);
                this.connection.flush();
                if (this.player.getPower() >= 5 && Servers.isThisATestServer()) {
                    this.sendNormalServerMessage("CMD_FISH->" + MethodsFishing.fromCommand((byte)0));
                }
            }
            catch (Exception ex) {
                logWarn("Failed to send start rod fishing: " + this.player.getName() + ':' + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendFishBite(final byte fishType, final long fishId, final long playerId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-64));
                bb.put((byte)1);
                bb.putLong(playerId);
                bb.put(fishType);
                bb.putLong(fishId);
                this.connection.flush();
                if (this.player.getPower() >= 5 && Servers.isThisATestServer()) {
                    this.sendNormalServerMessage("CMD_FISH->" + MethodsFishing.fromCommand((byte)1));
                }
            }
            catch (Exception ex) {
                logWarn("Failed to send fish bite: " + this.player.getName() + ':' + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendSpearHit(final byte fishType, final long fishId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-64));
                bb.put((byte)22);
                bb.put(fishType);
                bb.putLong(fishId);
                this.connection.flush();
                if (this.player.getPower() >= 5 && Servers.isThisATestServer()) {
                    this.sendNormalServerMessage("CMD_FISH->" + MethodsFishing.fromCommand((byte)22));
                }
            }
            catch (Exception ex) {
                logWarn("Failed to send spear hit: " + this.player.getName() + ':' + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendFishSubCommand(final byte subCommand, final long playerId) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-64));
                bb.put(subCommand);
                bb.putLong(playerId);
                this.connection.flush();
                if (this.player.getPower() >= 5 && Servers.isThisATestServer()) {
                    this.sendNormalServerMessage("CMD_FISH->" + MethodsFishing.fromCommand(subCommand));
                }
            }
            catch (Exception ex) {
                logWarn("Failed to send fish sub command: " + this.player.getName() + ':' + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendSpearStart() {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-64));
                bb.put((byte)20);
                bb.put((byte)0);
                this.connection.flush();
                if (this.player.getPower() >= 5 && Servers.isThisATestServer()) {
                    this.sendNormalServerMessage("CMD_FISH->" + MethodsFishing.fromCommand((byte)20));
                }
            }
            catch (Exception ex) {
                logWarn("Failed to send start spear fishing: " + this.player.getName() + ':' + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendSpearStrike(final float posX, final float posY) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-64));
                bb.put((byte)26);
                bb.putFloat(posX);
                bb.putFloat(posY);
                this.connection.flush();
                if (this.player.getPower() >= 5 && Servers.isThisATestServer()) {
                    this.sendNormalServerMessage("CMD_FISH->" + MethodsFishing.fromCommand((byte)26));
                }
            }
            catch (Exception ex) {
                logWarn("Failed to send spear strike: " + this.player.getName() + ':' + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendPersonalJournalTier(final byte tierId, final String tierName, final String tierHover, final boolean tierCompleted, final int[] achievementIds, final boolean[] achievementsCompleted) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-39));
                bb.put((byte)1);
                bb.put(tierId);
                bb.put((byte)(tierCompleted ? 1 : 0));
                sendShortStringLength(tierName, bb);
                sendShortStringLength(tierHover, bb);
                bb.putInt(achievementIds.length);
                for (int i = 0; i < achievementIds.length; ++i) {
                    final AchievementTemplate temp = Achievement.getTemplate(achievementIds[i]);
                    if (temp == null) {
                        Communicator.logger.warning("AchievementTemplate for ID# " + achievementIds[i] + " is null");
                    }
                    else {
                        bb.putInt(temp.getNumber());
                        sendShortStringLength(temp.getName(), bb);
                        final boolean completed = achievementsCompleted[i] || tierCompleted;
                        sendShortStringLength(completed ? temp.getDescription() : temp.getRequirement(), bb);
                        bb.put((byte)(completed ? -1 : ((byte)(temp.getProgressFor(this.player.getWurmId()) * 100.0f))));
                    }
                }
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn("Failed to send journal tier addition: " + this.player.getName() + ':' + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendPersonalJournalTierUpdate(final byte tierId, final boolean tierCompleted, final String tierHover) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-39));
                bb.put((byte)2);
                bb.put(tierId);
                bb.put((byte)(tierCompleted ? 1 : 0));
                sendShortStringLength(tierHover, bb);
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn("Failed to send journal tier update: " + this.player.getName() + ':' + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendPersonalJournalAchvUpdate(final byte tierId, final int achievementId, final boolean achievementCompleted) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final AchievementTemplate temp = Achievement.getTemplate(achievementId);
                if (temp == null) {
                    return;
                }
                final byte progress = (byte)(temp.getProgressFor(this.player.getWurmId()) * 100.0f);
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-39));
                bb.put((byte)3);
                bb.putInt(achievementId);
                sendShortStringLength(temp.getName(), bb);
                sendShortStringLength(achievementCompleted ? temp.getDescription() : temp.getRequirement(), bb);
                bb.put((byte)((achievementCompleted || progress >= 100) ? -1 : progress));
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn("Failed to send journal tier update: " + this.player.getName() + ':' + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    public void sendPersonalJournalTutorial(final byte parentId, final byte tutorialId, final String tutName) {
        if (this.player != null && this.player.hasLink()) {
            try {
                final ByteBuffer bb = this.connection.getBuffer();
                bb.put((byte)(-39));
                bb.put((byte)4);
                bb.put(parentId);
                bb.put(tutorialId);
                sendShortStringLength(tutName, bb);
                this.connection.flush();
            }
            catch (Exception ex) {
                logWarn("Failed to send journal tutorial: " + this.player.getName() + ':' + ", " + ex.getMessage(), ex);
                this.player.setLink(false);
            }
        }
    }
    
    private void handleGetStoredCreatures(final String message) {
        final String msgHelp = "Examples  #getStoredCreatures <Creature Actual Name (E.g. Dalehard or Bison)>. #getStoredCreatures <Creature Template Name(E.g. Foal or Bull)>. #getStoredCreatures * this is a wildcard and will display all creatures.";
        try {
            if (this.player.getLogger() != null) {
                this.player.getLogger().info(message);
            }
            final StringTokenizer tokens = new StringTokenizer(message);
            tokens.nextToken();
            final String name = tokens.nextToken().trim();
            if (name.equals("help")) {
                this.sendNormalServerMessage(msgHelp);
            }
            if (name.equals("")) {
                this.sendNormalServerMessage(msgHelp);
            }
            for (final Item item : Items.getAllItems()) {
                if (item.getData() != -10L) {
                    if (item.getTemplateId() == 1310) {
                        if (item.isInside(1311)) {
                            try {
                                final Creature getCreature = Creatures.getInstance().getCreature(item.getData());
                                final String templateNameWithoutSpace = getCreature.getTemplate().getName().trim().replaceAll("\\s", "");
                                final String fm = "Found " + getCreature.getName() + " Color = " + getCreature.getColourName() + ", WurmID for stored creature item = " + item.getWurmId() + ", Parent WurmID = " + item.getParent().getWurmId() + ", Parent Name = " + item.getParent().getName() + ", Parent Type = " + item.getParent().getTemplate().getName() + ", TileX = " + item.getTileX() + ", TileY = " + item.getTileY();
                                if (templateNameWithoutSpace.equals(name)) {
                                    this.sendNormalServerMessage(fm);
                                }
                                else if (getCreature.getNameWithoutPrefixes().equals(name)) {
                                    this.sendNormalServerMessage(fm);
                                }
                                else if (name.equals("*")) {
                                    this.sendNormalServerMessage(fm);
                                }
                            }
                            catch (NoSuchCreatureException | NoSuchItemException ex3) {
                                final WurmServerException ex2;
                                final WurmServerException nsce = ex2;
                                final String msg = nsce.getMessage() + item.getTileX() + ", " + item.getTileY();
                                Communicator.logger.log(Level.WARNING, msg);
                            }
                        }
                        else {
                            final String msg2 = "Found Stored Creature outside of cage. " + item.getTileX() + ", " + item.getTileY();
                            Communicator.logger.log(Level.WARNING, msg2);
                            this.sendNormalServerMessage(msg2);
                        }
                    }
                }
                else {
                    final String msg2 = "Found Stored Creature with NOID. " + item.getTileX() + ", " + item.getTileY();
                    Communicator.logger.log(Level.WARNING, msg2);
                    this.sendNormalServerMessage(msg2);
                }
            }
        }
        catch (NoSuchElementException ex) {
            this.sendNormalServerMessage(msgHelp);
        }
    }
    
    private void handleDestroyAllCreatures() {
        if (!Servers.isThisATestServer()) {
            this.sendNormalServerMessage("This command can only be ran on a test server.");
            return;
        }
        if (this.player.getPower() < 5) {
            this.sendNormalServerMessage("You lack the required power level to execute this command.");
            return;
        }
        try {
            int count = 0;
            for (final Creature cret : Creatures.getInstance().getCreatures()) {
                if (!cret.isPlayer()) {
                    cret.destroy();
                    ++count;
                }
            }
            this.sendNormalServerMessage("Destroyed " + count + " creatures.");
        }
        catch (NoSuchElementException ex) {
            ex.printStackTrace();
        }
    }
    
    static {
        Communicator.gchatdisabled = Servers.localServer.isChallengeServer();
        Communicator.acceptsBoatTransfers = true;
        logger = Logger.getLogger(Communicator.class.getName());
        chatlogger = Logger.getLogger("Chat");
        firstBorder = (1 << Constants.meshSize) - 40;
        secondBorder = (1 << Constants.meshSize) - 20;
        finalBorder = (1 << Constants.meshSize) - 2;
        serverBorderEnd = 1 << Constants.meshSize;
        maxMapSize = ((1 << Constants.meshSize) - 1) * 4;
        local = ":Local".getBytes();
        event = ":Event".getBytes();
        system = ":System".getBytes();
        help = ":Help".getBytes();
        team = "Team".getBytes();
        friends = ":Friends".getBytes();
        village = "Village".getBytes();
        alliance = "Alliance".getBytes();
        combat = ":Combat".getBytes();
        deaths = ":Deaths".getBytes();
        logs = ":Logs".getBytes();
        gms = "GM".getBytes();
        mgmt = "MGMT".getBytes();
        pas = "CA HELP".getBytes();
        Communicator.myIp = Server.getInstance().getExternalIp();
        twoDecimals = new DecimalFormat("##0.00");
        Communicator.numcommands = 0;
        Communicator.prevcommand = 0;
        Communicator.lastcommand = 0;
        Communicator.commandAction = 0L;
        Communicator.commandMessage = "";
        emptyRock = Tiles.encode((short)(-100), Tiles.Tile.TILE_CAVE_WALL.id, (byte)0);
        Communicator.hasCreated = false;
        Communicator.muteMsgRed = 255;
        Communicator.muteMsgGreen = 201;
        Communicator.muteMsgBlue = 14;
    }
}
