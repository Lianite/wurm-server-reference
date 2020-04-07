// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.intra;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import com.wurmonline.server.players.Achievement;
import com.wurmonline.server.players.AchievementTemplate;
import java.util.HashSet;
import com.wurmonline.server.players.Achievements;
import com.wurmonline.server.NoSuchPlayerException;
import com.wurmonline.server.players.MapAnnotation;
import com.wurmonline.server.skills.Affinity;
import com.wurmonline.server.players.Titles;
import com.wurmonline.server.skills.Affinities;
import java.util.Map;
import com.wurmonline.server.spells.Cooldowns;
import com.wurmonline.server.items.Puppet;
import com.wurmonline.server.players.Cultist;
import javax.annotation.Nullable;
import com.wurmonline.server.players.Friend;
import java.io.Closeable;
import com.wurmonline.shared.util.IoUtilities;
import com.wurmonline.server.items.RecipesByPlayer;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.creatures.CreatureStatus;
import java.io.UnsupportedEncodingException;
import com.wurmonline.server.items.InscriptionData;
import com.wurmonline.server.players.PermissionsByPlayer;
import java.util.Iterator;
import java.util.Set;
import com.wurmonline.server.spells.SpellEffect;
import com.wurmonline.server.items.ItemSpellEffects;
import com.wurmonline.server.effects.Effect;
import com.wurmonline.server.players.PlayerInfo;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.ItemMealData;
import com.wurmonline.server.items.ItemSettings;
import com.wurmonline.server.items.ItemRequirement;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.Players;
import com.wurmonline.server.creatures.CreatureDataStream;
import com.wurmonline.server.creatures.Creatures;
import java.io.DataOutputStream;
import com.wurmonline.server.ServerEntry;
import com.wurmonline.server.bodys.Wound;
import java.io.IOException;
import com.wurmonline.server.Items;
import java.util.logging.Level;
import com.wurmonline.server.LoginServerWebConnection;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.Servers;
import com.wurmonline.server.Server;
import com.wurmonline.server.players.Player;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.MiscConstants;

public final class PlayerTransfer extends IntraCommand implements MiscConstants, TimeConstants
{
    private static final Logger logger;
    private final Player player;
    private boolean done;
    private byte[] data;
    private final int posX;
    private final int posY;
    private final boolean surfaced;
    public boolean copiedToLoginServer;
    private long loginServerVersion;
    private final int targetServerId;
    public static final int maxItems = 200;
    private static final int standardBodyInventoryItems = 12;
    private final boolean toOrFromEpic;
    private final byte targetKingdomId;
    
    public PlayerTransfer(final Server server, final Player wurmplayer, final String ip, final int port, final String serverpass, final int _targetServerId, final int posx, final int posy, final boolean aSurfaced, final boolean toOrFromEpicCluster, final byte targetKingdom) throws Exception {
        this.done = false;
        this.copiedToLoginServer = false;
        this.loginServerVersion = -10L;
        this.player = wurmplayer;
        this.toOrFromEpic = ((toOrFromEpicCluster && !Servers.isThisLoginServer()) || _targetServerId == 20);
        this.posX = posx;
        this.posY = posy;
        this.surfaced = aSurfaced;
        this.targetServerId = _targetServerId;
        if (Servers.isThisLoginServer()) {
            this.copiedToLoginServer = true;
        }
        this.targetKingdomId = targetKingdom;
        wurmplayer.setIsTransferring(true);
    }
    
    public static final boolean willItemsTransfer(final Player player, final boolean setTransferFlag, final int targetServer) {
        return willItemsTransfer(player, setTransferFlag, targetServer, false);
    }
    
    public static final boolean willItemsTransfer(final Player player, final boolean setTransferFlag, final int targetServer, final boolean changingCluster) {
        int numitems = 0;
        int stayBehind = 0;
        Item[] items = player.getInventory().getAllItems(true);
        boolean ok = true;
        for (int x = 0; x < items.length; ++x) {
            if (!items[x].willLeaveServer(setTransferFlag, changingCluster, player.getPower() > 0)) {
                ++stayBehind;
                ok = false;
                if (items[x].isArtifact() && setTransferFlag) {
                    player.getCommunicator().sendAlertServerMessage("The " + items[x].getName() + " disappears!");
                }
                else {
                    player.getCommunicator().sendAlertServerMessage("The " + items[x].getName() + " will not leave the server.");
                }
            }
            else {
                ++numitems;
                if (player.getPower() == 0 && changingCluster && numitems - stayBehind - 12 > 200 && !items[x].isInventory()) {
                    if (setTransferFlag) {
                        items[x].setTransferred(true);
                    }
                    player.getCommunicator().sendAlertServerMessage("The " + items[x].getName() + " stays behind.");
                }
            }
        }
        items = player.getBody().getBodyItem().getAllItems(true);
        for (int x = 0; x < items.length; ++x) {
            if (!items[x].isBodyPartAttached()) {
                if (!items[x].willLeaveServer(setTransferFlag, changingCluster, player.getPower() > 0)) {
                    ++stayBehind;
                    ok = false;
                    if (items[x].isArtifact() && setTransferFlag) {
                        player.getCommunicator().sendAlertServerMessage("The " + items[x].getName() + " disappears!");
                    }
                    else {
                        player.getCommunicator().sendAlertServerMessage("The " + items[x].getName() + " will not leave the server.");
                    }
                }
                else {
                    ++numitems;
                    if (player.getPower() == 0 && changingCluster && numitems - stayBehind - 12 > 200 && !items[x].isBodyPartAttached()) {
                        if (setTransferFlag) {
                            items[x].setTransferred(true);
                        }
                        player.getCommunicator().sendAlertServerMessage("The " + items[x].getName() + " stays behind.");
                    }
                }
            }
        }
        return ok;
    }
    
    @Override
    public boolean poll() {
        if (this.data != null) {
            if (!this.copiedToLoginServer) {
                if (!Servers.isThisLoginServer()) {
                    if (this.targetServerId == Servers.loginServer.id) {
                        this.copiedToLoginServer = true;
                    }
                    else {
                        final long time = System.nanoTime();
                        if (new LoginServerWebConnection().transferPlayer(this.player, this.player.getName(), this.posX, this.posY, this.surfaced, this.data)) {
                            this.copiedToLoginServer = true;
                            PlayerTransfer.logger.log(Level.INFO, "Copy to login server for " + this.player.getName() + " took " + (System.nanoTime() - time) / 1000000.0f + " millis.");
                        }
                        else {
                            PlayerTransfer.logger.log(Level.INFO, "Failed copy to login server for " + this.player.getName() + " took " + (System.nanoTime() - time) / 1000000.0f + " millis.");
                            this.player.getCommunicator().sendAlertServerMessage("You can not transfer right now.");
                            this.done = true;
                        }
                    }
                }
                else {
                    this.copiedToLoginServer = true;
                }
            }
            if (this.copiedToLoginServer) {
                final ServerEntry entry = Servers.getServerWithId(this.targetServerId);
                if (entry != null) {
                    try {
                        if (this.player.getDraggedItem() != null) {
                            Items.stopDragging(this.player.getDraggedItem());
                        }
                        final long time2 = System.currentTimeMillis();
                        if (new LoginServerWebConnection(entry.id).transferPlayer(this.player, this.player.getName(), this.posX, this.posY, this.surfaced, this.data)) {
                            PlayerTransfer.logger.log(Level.INFO, "Copy to target server for " + entry.getName() + " (" + entry.id + ") " + this.player.getName() + " took " + (System.currentTimeMillis() - time2) + " ms.");
                            this.player.getSaveFile().setCurrentServer(this.targetServerId);
                            try {
                                this.player.getSaveFile().save();
                            }
                            catch (IOException iox) {
                                PlayerTransfer.logger.log(Level.WARNING, "Failed to set target server=" + this.targetServerId + " for " + this.player.getName() + ".", iox);
                            }
                            this.player.getCommunicator().sendReconnect(entry.EXTERNALIP, Integer.parseInt(entry.EXTERNALPORT), this.player.getSaveFile().getPassword());
                            PlayerTransfer.logger.log(Level.INFO, "Command executed. Player redirected.");
                            this.player.logoutIn(10, "Redirected");
                        }
                        else {
                            new LoginServerWebConnection(Servers.loginServer.id).setCurrentServer(this.player.getName(), Servers.getLocalServerId());
                            PlayerTransfer.logger.log(Level.INFO, "Failed copy to target server for " + this.player.getName() + " took " + (System.currentTimeMillis() - time2) + " ms.");
                            this.player.getCommunicator().sendAlertServerMessage("You can not transfer right now.");
                        }
                    }
                    catch (Exception ex) {
                        new LoginServerWebConnection(Servers.loginServer.id).setCurrentServer(this.player.getName(), Servers.getLocalServerId());
                        this.player.getCommunicator().sendAlertServerMessage("An error occurred. You can not transfer right now.");
                        PlayerTransfer.logger.log(Level.WARNING, "Command executed. Failed to transfer player:" + ex.getMessage(), ex);
                    }
                }
                this.done = true;
            }
        }
        if (this.data == null) {
            Wound[] wounds = new Wound[0];
            if (!this.toOrFromEpic && this.player.getBody().getWounds() != null) {
                wounds = this.player.getBody().getWounds().getWounds();
            }
            try {
                if (this.toOrFromEpic) {
                    this.data = createPlayerData(this.player.getSaveFile(), this.player.getStatus(), this.player.getSkills().getSkillsNoTemp(), this.targetServerId, this.targetKingdomId, System.currentTimeMillis());
                }
                else {
                    this.data = createPlayerData(wounds, this.player.getSaveFile(), this.player.getStatus(), this.player.getAllItems(), this.player.getSkills().getSkillsNoTemp(), this.player.getDraggedItem(), this.targetServerId, System.currentTimeMillis(), this.targetKingdomId);
                }
            }
            catch (IOException iox2) {
                this.done = true;
            }
        }
        return this.done;
    }
    
    @Override
    public void reschedule(final IntraClient aClient) {
        this.timeOutAt = System.currentTimeMillis() + aClient.retryInSeconds * 1000;
    }
    
    @Override
    public void remove(final IntraClient aClient) {
        this.timeOutAt = System.currentTimeMillis();
        this.done = true;
    }
    
    @Override
    public void commandExecuted(final IntraClient aClient) {
        this.done = true;
        if (this.copiedToLoginServer) {
            final ServerEntry entry = Servers.getServerWithId(this.targetServerId);
            if (entry != null) {
                try {
                    if (this.player.getDraggedItem() != null) {
                        Items.stopDragging(this.player.getDraggedItem());
                    }
                    if (this.player.lastKingdom != 0) {
                        this.player.getStatus().setKingdom(this.player.lastKingdom);
                    }
                    if (Servers.isThisLoginServer()) {
                        this.player.getSaveFile().currentServer = this.targetServerId;
                        this.player.getSaveFile().lastServer = this.player.getSaveFile().currentServer;
                        try {
                            this.player.getSaveFile().save();
                            this.player.getCommunicator().sendReconnect(entry.EXTERNALIP, Integer.parseInt(entry.EXTERNALPORT), this.player.getSaveFile().getPassword());
                            PlayerTransfer.logger2.log(Level.INFO, "PLT Command " + PlayerTransfer.num + " executed. Player redirected.");
                            this.player.logoutIn(10, "Redirected");
                        }
                        catch (IOException iox) {
                            this.player.getCommunicator().sendAlertServerMessage("Failed to save your data. Not redirecting to the new server.");
                            PlayerTransfer.logger.log(Level.WARNING, iox.getMessage(), iox);
                        }
                    }
                    else {
                        this.player.getCommunicator().sendReconnect(entry.EXTERNALIP, Integer.parseInt(entry.EXTERNALPORT), this.player.getSaveFile().getPassword());
                        this.player.logoutIn(10, "Redirected");
                    }
                    PlayerTransfer.logger2.log(Level.INFO, "PLT Command executed " + PlayerTransfer.num);
                }
                catch (Exception ex) {
                    PlayerTransfer.logger.log(Level.WARNING, "Command executed. Failed to transfer player:" + ex.getMessage(), ex);
                }
            }
        }
        else {
            this.copiedToLoginServer = true;
            PlayerTransfer.logger2.log(Level.INFO, "Command executed. Player copied to login server.");
        }
    }
    
    @Override
    public void commandFailed(final IntraClient aClient) {
        PlayerTransfer.logger2.log(Level.INFO, "Command failed. " + PlayerTransfer.num, new Exception());
    }
    
    @Override
    public void dataReceived(final IntraClient aClient) {
        try {
            aClient.executePlayerTransferRequest(this.posX, this.posY, this.surfaced);
        }
        catch (IOException iox) {
            this.commandFailed(aClient);
        }
    }
    
    public static final void sendItem(final Item item, final DataOutputStream dos, final boolean dragged) throws UnsupportedEncodingException, IOException {
        try {
            if (item.getTemplateId() == 1310) {
                dos.writeBoolean(true);
                final long animalId = item.getData();
                final Creature animal = Creatures.getInstance().getCreature(animalId);
                if (!CreatureDataStream.validateCreature(animal)) {
                    dos.writeBoolean(false);
                    return;
                }
                CreatureDataStream.toStream(animal, dos);
                if (animal.getDominator() != null) {
                    for (final Player player : Players.getInstance().getPlayers()) {
                        if (player.getPet() == animal) {
                            player.setPet(-10L);
                        }
                    }
                    animal.setDominator(-10L);
                }
                if (animal.isDominated()) {
                    final PlayerInfo pinf = PlayerInfoFactory.getPlayerInfoWithWurmId(animal.dominator);
                    if (pinf != null) {
                        animal.setDominator(pinf.pet = -10L);
                    }
                }
            }
            else {
                dos.writeBoolean(false);
            }
        }
        catch (NoSuchCreatureException e) {
            PlayerTransfer.logger.log(Level.WARNING, "No creature found!!", e.getMessage());
            dos.writeBoolean(false);
        }
        catch (IOException e2) {
            PlayerTransfer.logger.log(Level.WARNING, e2.getMessage());
        }
        dos.writeBoolean(item.getLocked());
        dos.writeLong(item.getLockId());
        if (item.getLockId() != -10L) {
            if (item.isHollow()) {
                try {
                    final Item lock = Items.getItem(item.getLockId());
                    dos.writeBoolean(true);
                    sendItem(lock, dos, dragged);
                }
                catch (NoSuchItemException ex) {
                    dos.writeBoolean(false);
                }
            }
            else {
                dos.writeBoolean(false);
            }
        }
        dos.writeLong(item.getWurmId());
        dos.writeBoolean(dragged);
        final Effect[] effects = item.getEffects();
        dos.writeInt(effects.length);
        for (int e3 = 0; e3 < effects.length; ++e3) {
            dos.writeShort(effects[e3].getType());
            dos.writeLong(effects[e3].getStartTime());
        }
        final ItemSpellEffects effs = item.getSpellEffects();
        if (effs != null) {
            final SpellEffect[] sparr = effs.getEffects();
            dos.writeInt(sparr.length);
            for (int x = 0; x < sparr.length; ++x) {
                dos.writeLong(sparr[x].id);
                dos.writeByte(sparr[x].type);
                dos.writeFloat(sparr[x].power);
                dos.writeInt(sparr[x].timeleft);
            }
        }
        else {
            dos.writeInt(0);
        }
        final long[] keys = item.getKeyIds();
        dos.writeInt(keys.length);
        for (int k = 0; k < keys.length; ++k) {
            dos.writeLong(keys[k]);
        }
        dos.writeLong(item.lastOwner);
        if (item.isFarwalkerItem()) {
            dos.writeInt(-1);
            dos.writeInt(-1);
            dos.writeInt(-1);
            dos.writeInt(-1);
        }
        else {
            dos.writeInt(item.getData1());
            dos.writeInt(item.getData2());
            dos.writeInt(item.getExtra1());
            dos.writeInt(item.getExtra2());
        }
        dos.writeUTF(item.getActualName());
        dos.writeUTF(item.getDescription());
        dos.writeLong(item.getOwnerId());
        dos.writeLong(item.getParentId());
        dos.writeLong(item.lastMaintained);
        dos.writeFloat(item.getQualityLevel());
        dos.writeFloat(item.getDamage());
        dos.writeFloat(item.getOriginalQualityLevel());
        dos.writeInt(item.getTemplateId());
        dos.writeInt(item.getWeightGrams(false));
        dos.writeShort(item.getPlace());
        dos.writeInt(item.getSizeX(false));
        dos.writeInt(item.getSizeY(false));
        dos.writeInt(item.getSizeZ(false));
        if (item.getBless() != null) {
            dos.writeInt(item.getBless().number);
        }
        else {
            dos.writeInt(0);
        }
        dos.writeByte(item.enchantment);
        dos.writeByte(item.getMaterial());
        dos.writeInt(item.getPrice());
        dos.writeShort(item.getTemperature());
        dos.writeBoolean(item.isBanked());
        dos.writeByte(item.getAuxData());
        dos.writeLong(item.creationDate);
        dos.writeByte(item.creationState);
        dos.writeInt(item.realTemplate);
        boolean hasMoreItems = false;
        if (item.isUnfinished() && item.getTemplateId() == 179) {
            final Set<ItemRequirement> doneSet = ItemRequirement.getRequirements(item.getWurmId());
            if (doneSet != null) {
                final int nums = doneSet.size();
                if (nums > 0) {
                    hasMoreItems = true;
                    dos.writeBoolean(true);
                    dos.writeInt(nums);
                    for (final ItemRequirement next : doneSet) {
                        dos.writeInt(next.getTemplateId());
                        dos.writeInt(next.getNumsDone());
                    }
                }
            }
        }
        if (!hasMoreItems) {
            dos.writeBoolean(false);
        }
        dos.writeBoolean(item.wornAsArmour);
        dos.writeBoolean(item.female);
        dos.writeBoolean(item.mailed);
        dos.writeByte(item.getMailTimes());
        dos.writeByte(item.getRarity());
        dos.writeLong(item.getBridgeId());
        dos.writeInt(item.getSettings().getPermissions());
        final PermissionsByPlayer[] pbps = ItemSettings.getPermissionsPlayerList(item.getWurmId()).getPermissionsByPlayer();
        dos.writeInt(pbps.length);
        for (final PermissionsByPlayer pbp : pbps) {
            dos.writeLong(pbp.getPlayerId());
            dos.writeInt(pbp.getSettings());
        }
        final boolean hasInscription = item.canHaveInscription() && item.getInscription() != null && item.getInscription().hasBeenInscribed();
        dos.writeBoolean(hasInscription);
        if (hasInscription) {
            final InscriptionData id = item.getInscription();
            if (id.getInscription() == null) {
                id.setInscription("");
                PlayerTransfer.logger.log(Level.WARNING, "Inscription was null for " + item.getWurmId());
            }
            dos.writeUTF(id.getInscription());
            if (id.getInscriber() == null) {
                PlayerTransfer.logger.log(Level.WARNING, "Inscriber was null for " + item.getWurmId());
                id.setInscriber("unknown");
            }
            dos.writeUTF(id.getInscriber());
        }
        dos.writeInt(item.color);
        dos.writeInt(item.color2);
        dos.writeUTF(item.creator);
        final ItemMealData imd = ItemMealData.getItemMealData(item.getWurmId());
        if (imd == null) {
            dos.writeBoolean(false);
        }
        else {
            dos.writeBoolean(true);
            dos.writeShort(imd.getCalories());
            dos.writeShort(imd.getCarbs());
            dos.writeShort(imd.getFats());
            dos.writeShort(imd.getProteins());
            dos.writeByte(imd.getBonus());
            dos.writeByte(imd.getStages());
            dos.writeByte(imd.getIngredients());
            dos.writeShort(imd.getRecipeId());
        }
    }
    
    private static void sendSpellEffects(final CreatureStatus status, final DataOutputStream dos) throws IOException {
        if (status.spellEffects == null) {
            dos.writeInt(0);
        }
        else {
            final SpellEffect[] sparr = status.spellEffects.getEffects();
            dos.writeInt(sparr.length);
            for (int x = 0; x < sparr.length; ++x) {
                dos.writeLong(sparr[x].id);
                dos.writeByte(sparr[x].type);
                dos.writeFloat(sparr[x].power);
                dos.writeInt(sparr[x].timeleft);
            }
        }
    }
    
    public static byte[] createPlayerData(final PlayerInfo pinf, final CreatureStatus status, final Skill[] skills, final int targServId, final byte targetKingdomId, final long clientTimeDifference) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            final DataOutputStream dos = new DataOutputStream(bos);
            dos.writeBoolean(true);
            dos.writeLong(pinf.getPlayerId());
            dos.writeUTF(pinf.getName());
            dos.writeUTF(pinf.getPassword());
            dos.writeUTF("");
            dos.writeUTF(pinf.emailAddress);
            dos.writeLong(0L);
            dos.writeByte((byte)pinf.getPower());
            dos.writeLong(pinf.money);
            dos.writeLong(pinf.getPaymentExpire());
            final long[] ignored = pinf.getIgnored();
            dos.writeInt(ignored.length);
            for (int x = 0; x < ignored.length; ++x) {
                dos.writeLong(ignored[x]);
            }
            if (!pinf.hasLoadedFriends()) {
                pinf.loadFriends(pinf.getPlayerId());
            }
            final Friend[] friends = pinf.getFriends();
            dos.writeInt(friends.length);
            for (int x2 = 0; x2 < friends.length; ++x2) {
                dos.writeLong(friends[x2].getFriendId());
                dos.writeByte(friends[x2].getCatId());
            }
            if (pinf.lastLogin > 0L) {
                dos.writeLong(pinf.playingTime + System.currentTimeMillis() - pinf.lastLogin);
            }
            else {
                dos.writeLong(pinf.playingTime);
            }
            dos.writeLong(pinf.creationDate);
            dos.writeLong(pinf.lastWarned);
            dos.writeByte(targetKingdomId);
            dos.writeBoolean(pinf.isBanned());
            dos.writeLong(pinf.banexpiry);
            if (pinf.banreason == null) {
                dos.writeUTF("");
            }
            else {
                dos.writeUTF(pinf.banreason);
            }
            dos.writeBoolean(pinf.isMute());
            dos.writeShort(pinf.muteTimes);
            dos.writeLong(pinf.muteexpiry);
            dos.writeUTF(pinf.mutereason);
            dos.writeBoolean(pinf.mayMute);
            dos.writeBoolean(pinf.overRideShop);
            dos.writeBoolean(pinf.isReimbursed());
            dos.writeInt(pinf.warnings);
            dos.writeBoolean(pinf.mayHearDevTalk);
            dos.writeUTF((pinf.getIpaddress() != null) ? pinf.getIpaddress() : "");
            dos.writeLong(pinf.version);
            dos.writeLong(pinf.referrer);
            dos.writeUTF(pinf.pwQuestion);
            dos.writeUTF(pinf.pwAnswer);
            dos.writeBoolean(pinf.logging);
            dos.writeBoolean(pinf.seesPlayerAssistantWindow());
            dos.writeBoolean(pinf.isPlayerAssistant());
            dos.writeBoolean(pinf.mayAppointPlayerAssistant());
            dos.writeLong(pinf.face);
            dos.writeByte(pinf.getBlood());
            dos.writeLong(pinf.flags);
            dos.writeLong(pinf.flags2);
            dos.write(pinf.getChaosKingdom());
            dos.write(pinf.undeadType);
            dos.writeInt(pinf.undeadKills);
            dos.writeInt(pinf.undeadPlayerKills);
            dos.writeInt(pinf.undeadPlayerSeconds);
            dos.writeLong(pinf.getLastResetEarningsCounter());
            dos.writeLong(pinf.getMoneyEarnedBySellingLastHour());
            dos.writeLong(pinf.getMoneyEarnedBySellingEver());
            if (pinf.awards != null) {
                dos.writeBoolean(true);
                dos.writeInt(pinf.awards.getDaysPrem());
                dos.writeLong(pinf.awards.getLastTickedDay());
                dos.writeInt(pinf.awards.getMonthsPaidEver());
                dos.writeInt(pinf.awards.getMonthsPaidInARow());
                dos.writeInt(pinf.awards.getMonthsPaidSinceReset());
                dos.writeInt(pinf.awards.getSilversPaidEver());
                dos.writeInt(pinf.awards.getCurrentLoyalty());
                dos.writeInt(pinf.awards.getTotalLoyalty());
            }
            else {
                dos.writeBoolean(false);
            }
            dos.writeByte(status.getSex());
            if (Servers.localServer.entryServer) {
                dos.writeInt(targServId);
                dos.writeByte(0);
            }
            else {
                dos.writeInt(Servers.localServer.id);
                dos.writeByte(status.kingdom);
            }
            dos.writeInt(skills.length);
            for (int s = 0; s < skills.length; ++s) {
                double actualKnowledge = skills[s].getKnowledge();
                double actualMin = skills[s].minimum;
                if (pinf.realdeath > 0) {
                    switch (skills[s].getNumber()) {
                        case 10066:
                        case 10067:
                        case 10068: {
                            if (skills[s].getNumber() == 10067) {
                                actualKnowledge = Math.max(pinf.champChanneling, actualKnowledge - 50.0);
                            }
                            else {
                                actualKnowledge = Math.max(10.0, actualKnowledge - 50.0);
                            }
                            actualMin = Math.max(actualKnowledge, actualMin - 50.0);
                            break;
                        }
                        case 100:
                        case 101:
                        case 102:
                        case 103:
                        case 104:
                        case 105:
                        case 106: {
                            actualKnowledge -= 6.0;
                            actualMin -= 6.0;
                            break;
                        }
                    }
                }
                dos.writeLong(skills[s].id);
                dos.writeInt(skills[s].getNumber());
                dos.writeDouble(actualKnowledge);
                dos.writeDouble(actualMin);
                dos.writeLong(skills[s].lastUsed);
            }
            sendAchievements(pinf.getPlayerId(), dos);
            RecipesByPlayer.packRecipes(dos, pinf.getPlayerId());
            sendPMList(pinf, dos);
            dos.flush();
            IoUtilities.closeClosable(dos);
        }
        catch (Exception ex) {
            PlayerTransfer.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        final byte[] barr = bos.toByteArray();
        return barr;
    }
    
    public static byte[] createPlayerData(final Wound[] wounds, final PlayerInfo pinf, final CreatureStatus status, final Item[] items, final Skill[] skills, @Nullable final Item draggedItem, final int targServId, final long clientTimeDifference, final byte targetKingdom) throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            final DataOutputStream dos = new DataOutputStream(bos);
            dos.writeBoolean(false);
            dos.writeLong(pinf.getPlayerId());
            dos.writeInt(wounds.length);
            for (int w = 0; w < wounds.length; ++w) {
                dos.writeLong(wounds[w].getWurmId());
                dos.writeByte(wounds[w].getType());
                dos.writeByte(wounds[w].getLocation());
                dos.writeFloat(wounds[w].getSeverity());
                dos.writeFloat(wounds[w].getPoisonSeverity());
                dos.writeFloat(wounds[w].getInfectionSeverity());
                dos.writeBoolean(wounds[w].isBandaged());
                dos.writeLong(wounds[w].getLastPolled());
                dos.writeByte(wounds[w].getHealEff());
            }
            dos.writeUTF(pinf.getName());
            dos.writeUTF(pinf.getPassword());
            dos.writeUTF("");
            dos.writeUTF(pinf.emailAddress);
            dos.writeLong(0L);
            dos.writeByte((byte)pinf.getPower());
            if (pinf.getDeity() != null) {
                dos.writeByte((byte)pinf.getDeity().number);
            }
            else {
                dos.writeByte(0);
            }
            dos.writeFloat(pinf.getAlignment());
            dos.writeFloat(pinf.getFaith());
            dos.writeFloat(pinf.getFavor());
            if (pinf.getGod() != null) {
                dos.writeByte((byte)pinf.getGod().number);
            }
            else {
                dos.writeByte(0);
            }
            dos.writeByte(pinf.realdeath);
            dos.writeLong(pinf.lastChangedDeity);
            dos.writeInt(pinf.fatigueSecsLeft);
            dos.writeInt(pinf.fatigueSecsToday);
            dos.writeInt(pinf.fatigueSecsYesterday);
            dos.writeLong(pinf.lastFatigue);
            dos.writeLong(pinf.lastWarned);
            dos.writeLong(pinf.lastCheated);
            dos.writeLong(pinf.plantedSign);
            if (pinf.lastLogin > 0L) {
                dos.writeLong(pinf.playingTime + System.currentTimeMillis() - pinf.lastLogin);
            }
            else {
                dos.writeLong(pinf.playingTime);
            }
            dos.writeLong(pinf.creationDate);
            if (Servers.localServer.entryServer && pinf.getPower() <= 0) {
                dos.writeByte(targetKingdom);
            }
            else {
                dos.writeByte(status.kingdom);
            }
            dos.writeBoolean(pinf.votedKing);
            dos.writeInt(pinf.getRank());
            dos.writeInt(pinf.getMaxRank());
            dos.writeLong(pinf.lastModifiedRank);
            dos.writeBoolean(pinf.isBanned());
            dos.writeLong(pinf.banexpiry);
            if (pinf.banreason == null) {
                dos.writeUTF("");
            }
            else {
                dos.writeUTF(pinf.banreason);
            }
            dos.writeShort(pinf.muteTimes);
            dos.writeBoolean(pinf.isReimbursed());
            dos.writeInt(pinf.warnings);
            dos.writeBoolean(pinf.mayHearDevTalk);
            dos.writeLong(pinf.getPaymentExpire());
            final long[] ignored = pinf.getIgnored();
            dos.writeInt(ignored.length);
            for (int x = 0; x < ignored.length; ++x) {
                dos.writeLong(ignored[x]);
            }
            if (!pinf.hasLoadedFriends()) {
                pinf.loadFriends(pinf.getPlayerId());
            }
            final Friend[] friends = pinf.getFriends();
            dos.writeInt(friends.length);
            for (int x2 = 0; x2 < friends.length; ++x2) {
                dos.writeLong(friends[x2].getFriendId());
                dos.writeByte(friends[x2].getCatId());
            }
            dos.writeUTF((pinf.getIpaddress() != null) ? pinf.getIpaddress() : "");
            dos.writeLong(pinf.version);
            dos.writeBoolean(pinf.dead);
            dos.writeBoolean(pinf.isMute());
            dos.writeLong(pinf.lastFaith);
            dos.writeByte(pinf.numFaith);
            dos.writeLong(pinf.money);
            dos.writeBoolean(pinf.climbing);
            dos.writeByte((byte)pinf.getChangedKingdom());
            dos.writeLong(pinf.face);
            dos.writeByte(pinf.getBlood());
            dos.writeLong(pinf.flags);
            dos.writeLong(pinf.flags2);
            dos.writeLong(pinf.abilities);
            dos.writeInt(pinf.scenarioKarma);
            dos.writeInt(pinf.abilityTitle);
            dos.write(pinf.getChaosKingdom());
            dos.write(pinf.undeadType);
            dos.writeInt(pinf.undeadKills);
            dos.writeInt(pinf.undeadPlayerKills);
            dos.writeInt(pinf.undeadPlayerSeconds);
            dos.writeLong(pinf.getLastResetEarningsCounter());
            dos.writeLong(pinf.getMoneyEarnedBySellingLastHour());
            dos.writeLong(pinf.getMoneyEarnedBySellingEver());
            if (pinf.awards != null) {
                dos.writeBoolean(true);
                dos.writeInt(pinf.awards.getDaysPrem());
                dos.writeLong(pinf.awards.getLastTickedDay());
                dos.writeInt(pinf.awards.getMonthsPaidEver());
                dos.writeInt(pinf.awards.getMonthsPaidInARow());
                dos.writeInt(pinf.awards.getMonthsPaidSinceReset());
                dos.writeInt(pinf.awards.getSilversPaidEver());
                dos.writeInt(pinf.awards.getCurrentLoyalty());
                dos.writeInt(pinf.awards.getTotalLoyalty());
            }
            else {
                dos.writeBoolean(false);
            }
            dos.writeShort(pinf.getHotaWins());
            dos.writeBoolean(pinf.hasFreeTransfer);
            dos.writeInt(pinf.reputation);
            dos.writeLong(pinf.lastPolledReputation);
            dos.writeLong(pinf.pet);
            dos.writeLong(pinf.nicotineAddiction);
            dos.writeLong(pinf.alcoholAddiction);
            dos.writeFloat(pinf.nicotine);
            dos.writeFloat(pinf.alcohol);
            dos.writeBoolean(pinf.logging);
            if (pinf.title != null) {
                dos.writeInt(pinf.title.id);
            }
            else {
                dos.writeInt(0);
            }
            if (pinf.secondTitle != null) {
                dos.writeInt(pinf.secondTitle.id);
            }
            else {
                dos.writeInt(0);
            }
            final Titles.Title[] titles = pinf.getTitles();
            dos.writeInt(titles.length);
            for (int x3 = 0; x3 < titles.length; ++x3) {
                dos.writeInt(titles[x3].id);
            }
            dos.writeLong(pinf.muteexpiry);
            dos.writeUTF(pinf.mutereason);
            dos.writeBoolean(pinf.mayMute);
            dos.writeBoolean(pinf.overRideShop);
            dos.writeInt(targServId);
            dos.writeInt(pinf.currentServer);
            dos.writeLong(pinf.referrer);
            dos.writeUTF(pinf.pwQuestion);
            dos.writeUTF(pinf.pwAnswer);
            dos.writeBoolean(pinf.isPriest);
            if (pinf.isPriest) {
                dos.writeByte(pinf.priestType);
                dos.writeLong(pinf.lastChangedPriestType);
            }
            dos.writeLong(pinf.bed);
            dos.writeInt(pinf.sleep);
            dos.writeBoolean(pinf.isTheftWarned);
            dos.writeBoolean(pinf.noReimbursementLeft);
            dos.writeBoolean(pinf.deathProtected);
            dos.writeByte(pinf.fightmode);
            dos.writeLong(pinf.nextAffinity);
            dos.writeInt(pinf.tutorialLevel);
            dos.writeBoolean(pinf.autoFighting);
            dos.writeLong(pinf.appointments);
            dos.writeBoolean(pinf.seesPlayerAssistantWindow());
            dos.writeBoolean(pinf.isPlayerAssistant());
            dos.writeBoolean(pinf.mayAppointPlayerAssistant());
            dos.writeLong(pinf.lastChangedKindom);
            dos.writeLong(pinf.championTimeStamp);
            dos.writeShort(pinf.championPoints);
            dos.writeFloat(pinf.champChanneling);
            if (Servers.localServer.entryServer) {
                dos.writeByte(0);
                dos.writeInt(targServId);
            }
            else {
                dos.write(pinf.epicKingdom);
                dos.writeInt(pinf.epicServerId);
            }
            dos.writeInt(pinf.getKarma());
            dos.writeInt(pinf.getMaxKarma());
            dos.writeInt(pinf.getTotalKarma());
            dos.writeUTF(status.getTemplate().getName());
            dos.writeShort(status.getBody().getCentimetersHigh());
            dos.writeShort(status.getBody().getCentimetersLong());
            dos.writeShort(status.getBody().getCentimetersWide());
            dos.writeFloat(status.getRotation());
            dos.writeLong(status.getBodyId());
            dos.writeLong(status.getBuildingId());
            dos.writeInt(status.damage);
            dos.writeInt(status.getHunger());
            dos.writeInt((int)status.getStunned());
            dos.writeInt(status.getThirst());
            dos.writeInt(status.getStamina());
            dos.writeFloat(status.getNutritionlevel());
            dos.writeByte(status.getSex());
            dos.writeLong(status.getInventoryId());
            dos.writeBoolean(status.isOnSurface());
            dos.writeBoolean(status.isUnconscious());
            dos.writeInt(status.age);
            dos.writeLong(status.lastPolledAge);
            dos.writeByte(status.fat);
            dos.writeShort((short)status.getDetectInvisCounter());
            dos.write(status.disease);
            dos.writeFloat(status.getCalories());
            dos.writeFloat(status.getCarbs());
            dos.writeFloat(status.getFats());
            dos.writeFloat(status.getProteins());
            final Cultist cultist = Cultist.getCultist(pinf.getPlayerId());
            if (cultist != null) {
                dos.writeBoolean(true);
                dos.writeByte(cultist.getLevel());
                dos.writeByte(cultist.getPath());
                dos.writeLong(cultist.getLastMeditated());
                dos.writeLong(cultist.getLastReceivedLevel());
                dos.writeLong(cultist.getLastAppointedLevel());
                dos.writeLong(cultist.getCooldown1());
                dos.writeLong(cultist.getCooldown2());
                dos.writeLong(cultist.getCooldown3());
                dos.writeLong(cultist.getCooldown4());
                dos.writeLong(cultist.getCooldown5());
                dos.writeLong(cultist.getCooldown6());
                dos.writeLong(cultist.getCooldown7());
                dos.writeByte(cultist.getSkillgainCount());
            }
            else {
                dos.writeBoolean(false);
            }
            dos.writeLong(pinf.getLastChangedPath());
            dos.writeLong(Puppet.getLastPuppeteered(pinf.getPlayerId()));
            final Cooldowns cd = Cooldowns.getCooldownsFor(pinf.getPlayerId(), false);
            if (cd != null) {
                dos.writeInt(cd.cooldowns.size());
                if (cd.cooldowns.size() > 0) {
                    for (final Map.Entry<Integer, Long> ent : cd.cooldowns.entrySet()) {
                        dos.writeInt(ent.getKey());
                        dos.writeLong(ent.getValue());
                    }
                }
            }
            else {
                dos.writeInt(0);
            }
            int numItems = 0;
            for (int x4 = 0; x4 < items.length; ++x4) {
                if (!items[x4].transferred && !items[x4].isBodyPart() && !items[x4].isTemporary()) {
                    ++numItems;
                }
            }
            dos.writeInt(numItems);
            for (int x4 = 0; x4 < items.length; ++x4) {
                if (!items[x4].transferred && !items[x4].isBodyPart() && !items[x4].isTemporary()) {
                    sendItem(items[x4], dos, false);
                }
            }
            dos.writeInt(skills.length);
            for (int s = 0; s < skills.length; ++s) {
                dos.writeLong(skills[s].id);
                dos.writeInt(skills[s].getNumber());
                dos.writeDouble(skills[s].getKnowledge());
                dos.writeDouble(skills[s].minimum);
                dos.writeLong(skills[s].lastUsed);
            }
            final Affinity[] affs = Affinities.getAffinities(pinf.getPlayerId());
            dos.writeInt(affs.length);
            for (int xa = 0; xa < affs.length; ++xa) {
                dos.writeInt(affs[xa].skillNumber);
                dos.write((byte)affs[xa].number);
            }
            sendSpellEffects(status, dos);
            sendAchievements(pinf.getPlayerId(), dos);
            RecipesByPlayer.packRecipes(dos, pinf.getPlayerId());
            sendPMList(pinf, dos);
            sendPrivateMapAnnotations(pinf.getPlayerId(), dos);
            dos.flush();
            IoUtilities.closeClosable(dos);
        }
        catch (Exception ex) {
            PlayerTransfer.logger.log(Level.WARNING, ex.getMessage(), ex);
        }
        final byte[] barr = bos.toByteArray();
        return barr;
    }
    
    private static final void sendPrivateMapAnnotations(final long playerId, final DataOutputStream dos) throws IOException {
        try {
            final Player player = Players.getInstance().getPlayer(playerId);
            final Set<MapAnnotation> annos = player.getPrivateMapAnnotations();
            if (annos.size() > 0) {
                dos.writeBoolean(true);
                dos.writeInt(annos.size());
                for (final MapAnnotation anno : annos) {
                    dos.writeLong(anno.getId());
                    dos.writeByte(anno.getType());
                    dos.writeUTF(anno.getName());
                    dos.writeUTF(anno.getServer());
                    dos.writeLong(anno.getPosition());
                    dos.writeLong(anno.getOwnerId());
                    dos.writeByte(anno.getIcon());
                }
            }
            else {
                dos.writeBoolean(false);
            }
        }
        catch (NoSuchPlayerException nsp) {
            PlayerTransfer.logger.log(Level.WARNING, "Unable to find the player during transfer, no map annotations will be sent. " + nsp.getMessage(), nsp);
            dos.writeBoolean(false);
        }
    }
    
    private static final void sendAchievements(final long wurmid, final DataOutputStream dos) throws IOException {
        final Achievement[] myAchievements = Achievements.getAchievements(wurmid);
        final Set<AchievementTemplate> templatesToSend = new HashSet<AchievementTemplate>();
        for (int x = 0; x < myAchievements.length; ++x) {
            if (myAchievements[x].getTemplate().getType() == 2) {
                templatesToSend.add(myAchievements[x].getTemplate());
            }
        }
        dos.writeInt(templatesToSend.size());
        for (final AchievementTemplate template : templatesToSend) {
            dos.writeInt(template.getNumber());
            dos.writeUTF(template.getName());
            dos.writeUTF(template.getDescription());
            dos.writeUTF(template.getCreator());
        }
        dos.writeInt(myAchievements.length);
        for (int x = 0; x < myAchievements.length; ++x) {
            dos.writeInt(myAchievements[x].getAchievement());
            dos.writeInt(myAchievements[x].getCounter());
            dos.writeLong(myAchievements[x].getDateAchieved().getTime());
        }
    }
    
    private static final void sendPMList(final PlayerInfo pinf, final DataOutputStream dos) throws IOException {
        final ConcurrentHashMap<String, Long> targetPMIds = pinf.getAllTargetPMIds();
        final int theCount = targetPMIds.size();
        dos.writeInt(theCount);
        for (final Map.Entry<String, Long> x : targetPMIds.entrySet()) {
            dos.writeUTF(x.getKey());
            dos.writeLong(x.getValue());
        }
        final long sessionFlags = pinf.getSessionFlags();
        dos.writeLong(sessionFlags);
    }
    
    @Override
    public void receivingData(final ByteBuffer buffer) {
        this.loginServerVersion = buffer.getLong();
    }
    
    static {
        logger = Logger.getLogger(PlayerTransfer.class.getName());
    }
}
