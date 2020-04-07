// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.villages.Citizen;
import java.util.Iterator;
import com.wurmonline.server.villages.AllianceWar;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.villages.WarDeclaration;
import java.util.Arrays;
import com.wurmonline.server.villages.PvPAlliance;
import com.wurmonline.server.zones.FocusZone;
import com.wurmonline.server.villages.NoSuchRoleException;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.economy.Change;
import com.wurmonline.server.Servers;
import com.wurmonline.server.Server;
import com.wurmonline.server.kingdom.Kingdoms;
import com.wurmonline.server.NoSuchItemException;
import java.util.logging.Level;
import com.wurmonline.server.villages.Villages;
import com.wurmonline.server.Items;
import com.wurmonline.server.villages.NoSuchVillageException;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.villages.VillageRole;
import java.text.NumberFormat;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.villages.VillageStatus;

public final class VillageInfo extends Question implements VillageStatus, TimeConstants
{
    private static final Logger logger;
    private static final NumberFormat nf;
    private VillageRole playerRole;
    
    public VillageInfo(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        super(aResponder, aTitle, aQuestion, 14, aTarget);
        this.playerRole = null;
        VillageInfo.nf.setMaximumFractionDigits(6);
    }
    
    public VillageInfo(final Creature aResponder, final VillageRole vRole) {
        super(aResponder, "", "", 14, -10L);
        this.playerRole = null;
        this.playerRole = vRole;
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        if (Boolean.parseBoolean(this.getAnswer().getProperty("showPlayerRole"))) {
            final VillageInfo vi = new VillageInfo(this.getResponder(), this.playerRole);
            vi.sendQuestion();
        }
    }
    
    @Override
    public void sendQuestion() {
        if (this.playerRole != null) {
            VillageRolesManageQuestion.roleShow(this.getResponder(), this.getId(), null, this.playerRole, "");
            return;
        }
        try {
            Village village;
            if (this.target == -10L) {
                village = this.getResponder().getCitizenVillage();
                if (village == null) {
                    throw new NoSuchVillageException("You are not a citizen of any village (on this server).");
                }
            }
            else {
                final Item deed = Items.getItem(this.target);
                final int villageId = deed.getData2();
                village = Villages.getVillage(villageId);
            }
            if (village.getMayor() != null && village.getMayor().getId() == this.getResponder().getWurmId()) {
                try {
                    final Item deed = Items.getItem(village.getDeedId());
                    if (deed.getOwnerId() < 0L) {
                        VillageInfo.logger.log(Level.INFO, this.getResponder().getName() + " retrieving and inserting deed " + village.getDeedId() + " for " + village.getName() + ".");
                        deed.setTransferred(false);
                        deed.setMailed(false);
                        this.getResponder().getInventory().insertItem(deed);
                        this.getResponder().getCommunicator().sendNormalServerMessage("You have retrieved your settlement deed.");
                    }
                }
                catch (NoSuchItemException iox) {
                    VillageInfo.logger.log(Level.WARNING, "No deed available for " + village.getName() + ". Creating new. Exception was " + iox.getMessage(), iox);
                    village.replaceNoDeed(this.getResponder());
                    this.getResponder().getCommunicator().sendNormalServerMessage("You have received a new settlement deed.");
                }
            }
            final StringBuilder buf = new StringBuilder();
            buf.append(this.getBmlHeader());
            buf.append("header{text=\"" + village.getName() + "\"}");
            buf.append("text{type=\"italic\";text=\"" + village.getMotto() + "\"};text{text=\"\"}");
            if (village.isCapital()) {
                buf.append("text{type=\"bold\";text=\"Welcome to the capital of " + Kingdoms.getNameFor(village.kingdom) + "!\"};text{text=\"\"}");
            }
            if (village.isDisbanding()) {
                final long timeleft = village.getDisbanding() - System.currentTimeMillis();
                final String times = Server.getTimeFor(timeleft);
                buf.append("text{type=\"bold\";text=\"This settlement is disbanding\"}");
                if (timeleft > 0L) {
                    buf.append("text{type=\"bold\";text=\"Eta: " + times + ".\"};text{text=\"\"}");
                }
                else {
                    buf.append("text{type=\"bold\";text=\"Eta: any minute now.\"};text{text=\"\"}");
                }
            }
            if (village.isCitizen(this.getResponder()) || this.getResponder().getPower() >= 2) {
                buf.append("text{text=\"The size of " + village.getName() + " is " + village.getDiameterX() + " by " + village.getDiameterY() + ".\"}");
                buf.append("text{text=\"The perimeter is " + (5 + village.getPerimeterSize()) + " and it has " + village.plan.getNumHiredGuards() + " guards hired.\"}");
                if (Servers.localServer.testServer) {
                    buf.append("text{text='[TEST] Number of current guards in guardPlan: " + village.getGuards().length + "'}");
                }
                final long money = village.plan.getMoneyLeft();
                final Change ca = new Change(money);
                if (Servers.localServer.isUpkeep()) {
                    buf.append("text{text=\"The settlement has " + ca.getChangeString() + " in its coffers.\"}");
                    if (this.getResponder().getPower() >= 2) {
                        VillageInfo.logger.log(Level.INFO, this.getResponder().getName() + " checking " + village.getName() + " financial info.");
                        this.getResponder().getLogger().log(Level.INFO, this.getResponder().getName() + " checking " + village.getName() + " financial info.");
                        final long moneyTick = (long)village.plan.calculateUpkeep(false);
                        final Change cu = new Change(moneyTick);
                        buf.append("text{text=\"Every tick (~8 mins) will drain " + cu.getChangeString() + ".\"}");
                    }
                    final long monthly = village.plan.getMonthlyCost();
                    final Change c = new Change(monthly);
                    buf.append("text{text=\"The monthly cost is " + c.getChangeString() + ".\"}");
                    final long timeleft2 = village.plan.getTimeLeft();
                    buf.append("text{text=\"The upkeep will last approximately " + Server.getTimeFor(timeleft2) + " more.\"}");
                }
                buf.append("text{text=\"\"}");
                buf.append("text{text=\"The settlement is granted the following faith bonuses:\"}");
                buf.append("text{text=\"War (" + VillageInfo.nf.format(village.getFaithWarValue()) + ") damage: " + VillageInfo.nf.format(village.getFaithWarBonus()) + "% CR: " + VillageInfo.nf.format(village.getFaithWarBonus()) + "%, Healing (" + VillageInfo.nf.format(village.getFaithHealValue()) + "): " + VillageInfo.nf.format(village.getFaithHealBonus()) + "%, Enchanting (" + VillageInfo.nf.format(village.getFaithCreateValue()) + "): " + VillageInfo.nf.format(village.getFaithCreateBonus()) + "%, Rarity window: " + (int)Math.min(10.0f, village.getFaithCreateValue()) + " bonus seconds\"}");
                buf.append("text{text=\"These bonuses will decrease by 15% per day.\"}");
                buf.append("text{text=\"\"}");
                final float ratio = village.getCreatureRatio();
                buf.append("text{text=\"The tile per creature ratio of this deed is " + ratio + ". Optimal is " + Village.OPTIMUMCRETRATIO + " or more.");
                if (ratio < Village.OPTIMUMCRETRATIO) {
                    buf.append(" This means that you will see more disease and miscarriage.");
                }
                else {
                    buf.append(" This is a good figure.");
                }
                final int brandedCreatures = Creatures.getInstance().getBranded(village.getId()).length;
                if (brandedCreatures > 1) {
                    buf.append(String.format(" There are %d creatures currently branded.", brandedCreatures));
                }
                else if (brandedCreatures == 1) {
                    buf.append(String.format(" There is %d creature currently branded.", brandedCreatures));
                }
                buf.append("\"};text{text=\"\"}");
                if (village.isDemocracy()) {
                    buf.append("text{text=\"" + village.getName() + " is a democracy. This means your citizenship cannot be revoked by any city officials such as the mayor. \"}");
                }
                else {
                    buf.append("text{text=\"" + village.getName() + " is a non-democracy. This means your citizenship can be revoked by any city officials such as the mayor. \"}");
                }
                buf.append("");
                buf.append("text{text=\"\"}");
            }
            String visitor = "Visitor";
            this.playerRole = village.getRoleForPlayer(this.getResponder().getWurmId());
            if (this.playerRole != null) {
                if (village.isCitizen(this.getResponder())) {
                    visitor = this.playerRole.getName() + " of " + village.getName();
                }
                else {
                    visitor = "Individual (" + this.playerRole.getName() + ") role";
                }
            }
            else {
                try {
                    if (this.getResponder().getCitizenVillage() == null) {
                        visitor = "visitor";
                        this.playerRole = village.getRoleForStatus((byte)1);
                    }
                    else {
                        this.playerRole = village.getRoleForVillage(this.getResponder().getCitizenVillage().getId());
                        if (this.playerRole != null) {
                            visitor = "Citizen of " + this.getResponder().getCitizenVillage().getName();
                        }
                        else if (this.getResponder().getCitizenVillage().isAlly(village)) {
                            visitor = "Ally";
                            this.playerRole = village.getRoleForStatus((byte)5);
                        }
                        else {
                            visitor = "visitor";
                            this.playerRole = village.getRoleForStatus((byte)1);
                        }
                    }
                }
                catch (NoSuchRoleException e) {
                    VillageInfo.logger.log(Level.WARNING, e.getMessage(), e);
                    visitor = "problem";
                }
            }
            buf.append("harray{button{text=\"Show role for " + visitor + "\";id=\"showPlayerRole\"}}");
            buf.append("text{text=\"\"}");
            if (FocusZone.getHotaZone() != null) {
                buf.append("text{text=\"" + village.getName() + " has won the Hunt of the Ancients " + village.getHotaWins() + " times.\"}");
                if (Servers.localServer.getNextHota() == Long.MAX_VALUE) {
                    buf.append("text{text=\"The Hunt of the Ancients is afoot!\"}");
                }
                else {
                    final long timeLeft = Servers.localServer.getNextHota() - System.currentTimeMillis();
                    buf.append("text{text=\"The next Hunt of the Ancients is in " + Server.getTimeFor(timeLeft) + ".\"}");
                }
            }
            final Village[] allies = village.getAllies();
            if (allies.length > 0) {
                final PvPAlliance alliance = PvPAlliance.getPvPAlliance(village.getAllianceNumber());
                if (alliance != null) {
                    final Village capital = alliance.getAllianceCapital();
                    buf.append("text{text=\"We are in the " + alliance.getName() + ". ");
                    buf.append("The capital is " + capital.getName() + ".\"}");
                    if (FocusZone.getHotaZone() != null) {
                        buf.append("text{text=\"" + alliance.getName() + " has won the Hunt of the Ancients " + alliance.getNumberOfWins() + " times.\"}");
                    }
                }
                buf.append("label{text=\"The alliance consists of: \"};text{text=\"");
                Arrays.sort(allies);
                for (int x = 0; x < allies.length; ++x) {
                    if (x == allies.length - 1) {
                        buf.append(allies[x].getName());
                    }
                    else if (x == allies.length - 2) {
                        buf.append(allies[x].getName() + " and ");
                    }
                    else {
                        buf.append(allies[x].getName() + ", ");
                    }
                }
                buf.append(".\"}");
                buf.append("text{text=\"\"}");
                if (alliance != null) {
                    final AllianceWar[] wars = alliance.getWars();
                    if (wars.length > 0) {
                        buf.append("text{type=\"bold\";text=\"We are at war with the following alliances: \"};text{text=\"");
                        for (int x2 = 0; x2 < wars.length; ++x2) {
                            PvPAlliance enemy = null;
                            if (wars[x2].hasEnded()) {
                                wars[x2].delete();
                            }
                            else {
                                if (wars[x2].getAggressor() != alliance.getId()) {
                                    enemy = PvPAlliance.getPvPAlliance(wars[x2].getAggressor());
                                }
                                else {
                                    enemy = PvPAlliance.getPvPAlliance(wars[x2].getDefender());
                                }
                                if (enemy != null) {
                                    if (x2 == wars.length - 1) {
                                        buf.append(enemy.getName());
                                    }
                                    else if (x2 == wars.length - 2) {
                                        buf.append(enemy.getName() + " and ");
                                    }
                                    else {
                                        buf.append(enemy.getName() + ", ");
                                    }
                                }
                            }
                        }
                        buf.append(".\"}");
                    }
                }
            }
            if (Servers.localServer.HOMESERVER && Servers.localServer.EPIC) {
                buf.append("text{type=\"bold\";text=\"Our notoriety is " + village.getVillageReputation() + ".\"};");
                if (village.getVillageReputation() >= 50) {
                    buf.append("text{text=\" Over 50 - other settlements may declare war on us. \"};text{text=\"\"}");
                }
                else {
                    buf.append("text{text=\" Below 50 - other settlements may not declare war on us. \"};text{text=\"\"}");
                }
            }
            final Village[] enemies = village.getEnemies();
            if (enemies.length > 0) {
                buf.append("text{type=\"bold\";text=\"We are at war with the following settlements: \"};text{text=\"");
                Arrays.sort(enemies);
                for (int x = 0; x < enemies.length; ++x) {
                    if (x == enemies.length - 1) {
                        buf.append(enemies[x].getName());
                    }
                    else if (x == enemies.length - 2) {
                        buf.append(enemies[x].getName() + " and ");
                    }
                    else {
                        buf.append(enemies[x].getName() + ", ");
                    }
                }
                buf.append(".\"}");
                buf.append("text{text=\"\"}");
            }
            if (village.warDeclarations != null) {
                buf.append("label{text=\"The current settlement war declarations are: \"}");
                for (final WarDeclaration declaration : village.warDeclarations.values()) {
                    buf.append("text{text=\"" + declaration.receiver.getName() + " must answer the challenge from " + declaration.declarer.getName() + " within " + Server.getTimeFor(declaration.time + 86400000L - System.currentTimeMillis()) + ".\"}");
                }
                buf.append("text{text=\"\"}");
            }
            if (village.getSkillModifier() == 0.0) {
                buf.append("text{text=\"This settlement has no acquired knowledge.\"}");
            }
            else {
                buf.append("text{text=\"This settlement has acquired knowledge that increases the productivity bonus of its citizens by " + village.getSkillModifier() + "%.\"}");
            }
            buf.append("text{text=\"\"}");
            final Citizen mayor = village.getMayor();
            if (mayor != null) {
                buf.append("text{type=\"italic\";text=\"" + mayor.getName() + ", " + mayor.getRole().getName() + ", " + village.getName() + "\"};text{text=\"\"}");
            }
            else {
                buf.append("text{type=\"italic\";text=\"The Citizens, " + village.getName() + "\"};text{text=\"\"}");
            }
            buf.append(this.createAnswerButton2());
            this.getResponder().getCommunicator().sendBml(500, 400, true, true, buf.toString(), 200, 200, 200, this.title);
        }
        catch (NoSuchItemException nsi) {
            VillageInfo.logger.log(Level.WARNING, this.getResponder().getName() + " tried to get info for null token with id " + this.target, nsi);
            this.getResponder().getCommunicator().sendNormalServerMessage("Failed to locate the settlement for that request. Please contact administration.");
        }
        catch (NoSuchVillageException nsv) {
            VillageInfo.logger.log(Level.WARNING, this.getResponder().getName() + " tried to get info for null settlement for token with id " + this.target);
            this.getResponder().getCommunicator().sendNormalServerMessage("Failed to locate the settlement for that request. Please contact administration.");
        }
    }
    
    static {
        logger = Logger.getLogger(VillageInfo.class.getName());
        nf = NumberFormat.getInstance();
    }
}
