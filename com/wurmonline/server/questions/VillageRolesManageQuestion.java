// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import com.wurmonline.server.Server;
import java.util.Arrays;
import com.wurmonline.server.Features;
import com.wurmonline.server.Servers;
import com.wurmonline.server.villages.Citizen;
import java.util.Iterator;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.VillageRole;
import com.wurmonline.server.villages.DbVillageRole;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.villages.NoSuchVillageException;
import com.wurmonline.server.villages.Villages;
import java.util.regex.Pattern;
import com.wurmonline.server.villages.NoSuchRoleException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.Properties;
import com.wurmonline.server.creatures.Creature;
import java.util.logging.Logger;
import com.wurmonline.server.TimeConstants;
import com.wurmonline.server.villages.VillageStatus;

public final class VillageRolesManageQuestion extends Question implements VillageStatus, TimeConstants
{
    private static final Logger logger;
    private static final int MAX_ROLES = 50;
    private static final String nameDesc = "label{text=\"Name of the role\"}";
    private static final String buildDesc = "label{text=\"Allows ability to plan, color and continue on buildings and fences.\"}";
    private static final String cutDesc = "label{text=\"Allows cutting down young trees, picking sprouts and planting new trees.\"}";
    private static final String cutOldDesc = "label{text=\"Allows cutting down old trees.\"}";
    private static final String destroyDesc = "label{text=\"Allows destroying of building and fence plans as well as finished buildings and plans. Also allows lockpicking of structures and removing lamp posts and signs.\"}";
    private static final String farmDesc = "label{text=\"Allows sowing, farming and harvesting farms. Also milking creatures.\"}";
    private static final String guardDesc = "label{text=\"Allows hiring guards to protect the settlement and uphold these rules.\"}";
    private static final String inviteDesc = "label{text=\"Allows ability to invite non-citizens to join the settlement.\"}";
    private static final String manageDesc = "label{text=\"Allows adding to and modifying these roles via this interface, as well as changing settings and setting reputations and disbanding the settlement.\"}";
    private static final String mineDesc = "label{text=\"Allows mining in general.\"}";
    private static final String digDesc = "label{text=\"Allows ability to dig, pack, pave, level and flatten.\"}";
    private static final String expandDesc = "label{text=\"Allows ability to expand the size of the settlement.\"}";
    private static final String passDesc = "label{text=\"Allows passing through all gates, locked or not.\"}";
    private static final String lockDesc = "label{text=\"Allows ability to unlock and relock gates inside the settlement.\"}";
    private static final String atkDesc = "label{text=\"Allows ability to attack citizens and allies of the settlement.\"}";
    private static final String atknonDesc = "label{text=\"Allows ability to attack non citizens.\"}";
    private static final String fishDesc = "label{text=\"Allows ability to fish on deed.\"}";
    private static final String pullDesc = "label{text=\"Allows ability to push, pull, turn items. Note that pull and push may require pickup permission as well\"}";
    private static final String dipDesc = "label{text=\"Allows ability to form and break alliances with other settlements, as well as declare war and make peace.\"}";
    private static final String mapDesc = "label{text=\"Allows ability to add and remove village annotations on the map.\"}";
    private static final String leadDesc = "label{text=\"Allows leading creatures.\"}";
    private static final String pickupDesc = "label{text=\"Allows ability to pickup item on deed.\"}";
    private static final String tameDesc = "label{text=\"Allows ability to Tame on deed.\"}";
    private static final String loadDesc = "label{text=\"Allows ability to Load on deed (needs Pickup as well).\"}";
    private static final String butcherDesc = "label{text=\"Allows ability to Butcher on deed.\"}";
    private static final String attachDesc = "label{text=\"Allows ability to Attach Locks on deed.\"}";
    private static final String pickLocksDesc = "label{text=\"Allows lockpicking of structures.\"}";
    private static final String deedDesc = "label{text=\"Make the role apply to a certain settlement only. Useful in alliances.\"}";
    private static final String deleteDesc = "label{text=\"If you check that box the role will disappear.\"}";
    private int roleId;
    private final String gmBack;
    private final int gmRowsPerPage;
    private final byte gmCurrentPage;
    
    public VillageRolesManageQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget) {
        this(aResponder, aTitle, aQuestion, aTarget, -10, "", 50, (byte)0);
    }
    
    public VillageRolesManageQuestion(final Creature aResponder, final String aTitle, final String aQuestion, final long aTarget, final int aRoleId, final String aGMBack, final int rowsPerPage, final byte iPage) {
        super(aResponder, aTitle, aQuestion, 105, aTarget);
        this.roleId = aRoleId;
        this.gmBack = aGMBack;
        this.gmCurrentPage = iPage;
        this.gmRowsPerPage = rowsPerPage;
    }
    
    @Override
    public void answer(final Properties answers) {
        this.setAnswer(answers);
        final Village village = this.getVillage();
        if (village == null) {
            return;
        }
        for (final String key : this.getAnswer().stringPropertyNames()) {
            if (key.equals("close")) {
                return;
            }
            if (key.equals("reset")) {
                village.resetRoles();
                this.getResponder().getCommunicator().sendNormalServerMessage("All roles were reset. Any other changes you tried to make were discarded.");
                return;
            }
            if (key.startsWith("change")) {
                final int id = Integer.parseInt(key.substring(6));
                try {
                    final VillageRole vr = village.getRole(id);
                    final String newName = this.getAnswer().getProperty("name");
                    if (QuestionParser.containsIllegalCharacters(newName)) {
                        this.getResponder().getCommunicator().sendSafeServerMessage("The role " + newName + " contains illegal letters. Please select another name.");
                    }
                    else if (newName.length() < 3) {
                        this.getResponder().getCommunicator().sendSafeServerMessage("The role " + newName + " contains less than 3 letters. Please select another name.");
                    }
                    else {
                        vr.setName(newName);
                    }
                }
                catch (IOException iox) {
                    VillageRolesManageQuestion.logger.log(Level.WARNING, "Failed to change mayor role " + id + ":" + iox.getMessage(), iox);
                    this.getResponder().getCommunicator().sendSafeServerMessage("Failed to change the name of the mayor role. Please contact administration.");
                }
                catch (NoSuchRoleException nsre) {
                    VillageRolesManageQuestion.logger.log(Level.WARNING, nsre.getMessage(), nsre);
                    this.getResponder().getCommunicator().sendSafeServerMessage("Failed to find the mayor role. Please contact administration.");
                }
            }
            else if (key.equals("add")) {
                final int id = -1;
                final VillageRolesManageQuestion vrmq = new VillageRolesManageQuestion(this.getResponder(), this.getTitle(), this.getQuestion(), this.getTarget(), -1, this.gmBack, this.gmRowsPerPage, this.gmCurrentPage);
                vrmq.sendQuestion();
            }
            else if (key.startsWith("edit")) {
                final int id = Integer.parseInt(key.substring(4));
                final VillageRolesManageQuestion vrmq = new VillageRolesManageQuestion(this.getResponder(), this.getTitle(), this.getQuestion(), this.getTarget(), id, this.gmBack, this.gmRowsPerPage, this.gmCurrentPage);
                vrmq.sendQuestion();
            }
            else if (key.startsWith("show")) {
                final int id = Integer.parseInt(key.substring(4));
                final VillageRolesManageQuestion vrmq = new VillageRolesManageQuestion(this.getResponder(), this.getTitle(), this.getQuestion(), this.getTarget(), id, this.gmBack, this.gmRowsPerPage, this.gmCurrentPage);
                vrmq.sendQuestion();
            }
            else if (key.equals("GMTool")) {
                byte qType = 0;
                byte qSubType = 0;
                long wId = -10L;
                String sSearch = "";
                String sBack = "";
                final String[] backs = this.gmBack.split(Pattern.quote("|"));
                if (backs.length <= 0) {
                    return;
                }
                final StringBuilder buf = new StringBuilder();
                if (backs.length > 1) {
                    buf.append(backs[0]);
                    for (int s = 1; s < backs.length - 1; ++s) {
                        buf.append("|" + backs[s]);
                    }
                }
                final String[] lparts = backs[backs.length - 1].split(",");
                qType = Byte.parseByte(lparts[0]);
                qSubType = Byte.parseByte(lparts[1]);
                wId = Long.parseLong(lparts[2]);
                if (lparts.length > 3) {
                    sSearch = lparts[3];
                }
                sBack = buf.toString();
                final GmTool gt = new GmTool(this.getResponder(), qType, qSubType, wId, sSearch, sBack, this.gmRowsPerPage, this.gmCurrentPage);
                gt.sendQuestion();
            }
            else if (key.equals("save")) {
                final int oldRoleId = this.roleId;
                final String roleName = this.getAnswer().getProperty("roleName");
                if (QuestionParser.containsIllegalCharacters(roleName)) {
                    this.getResponder().getCommunicator().sendSafeServerMessage("The role " + roleName + " contains illegal letters. Please select another name.");
                    return;
                }
                if (roleName.length() < 3) {
                    this.getResponder().getCommunicator().sendSafeServerMessage("The role " + roleName + " contains less than 3 letters. Please select another name.");
                    return;
                }
                if (this.roleId == -1) {
                    final String externalSettlement = this.getAnswer().getProperty("externalSettlement");
                    final String individualPlayer = this.getAnswer().getProperty("individualPlayer");
                    int villageAppliedTo = 0;
                    if (externalSettlement != null && externalSettlement.length() > 0) {
                        if (externalSettlement.equalsIgnoreCase(this.getResponder().getCitizenVillage().getName())) {
                            this.getResponder().getCommunicator().sendAlertServerMessage("Use normal roles instead of the village applied option for your own settlement.");
                            return;
                        }
                        try {
                            final Village vill = Villages.getVillage(externalSettlement);
                            villageAppliedTo = vill.getId();
                        }
                        catch (NoSuchVillageException nsv) {
                            this.getResponder().getCommunicator().sendAlertServerMessage("No village found with the name \"" + externalSettlement + "\".");
                            return;
                        }
                    }
                    long playerAppliedTo = -10L;
                    if (individualPlayer != null && individualPlayer.length() > 0) {
                        if (villageAppliedTo > 0) {
                            this.getResponder().getCommunicator().sendAlertServerMessage("Cannot have external village and individual player in same role. So defaulting to specified village.");
                            return;
                        }
                        final long pid = PlayerInfoFactory.getWurmId(individualPlayer);
                        if (pid == -10L) {
                            this.getResponder().getCommunicator().sendAlertServerMessage("Player \"" + individualPlayer + "\" not found.");
                            return;
                        }
                        if (village.isCitizen(pid)) {
                            this.getResponder().getCommunicator().sendAlertServerMessage("Use normal roles instead of the individual player option for your own settlement citizens.");
                            return;
                        }
                        playerAppliedTo = pid;
                    }
                    try {
                        final DbVillageRole newrole = new DbVillageRole(village.getId(), roleName, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, (byte)0, villageAppliedTo, false, false, false, false, false, false, false, false, false, playerAppliedTo, 0, 0, 0);
                        village.addRole(newrole);
                        this.roleId = newrole.id;
                        this.getResponder().getCommunicator().sendSafeServerMessage("The role \"" + roleName + "\" has been created.");
                    }
                    catch (IOException iox2) {
                        VillageRolesManageQuestion.logger.log(Level.WARNING, "Failed to create role \"" + roleName + "\":" + iox2.getMessage(), iox2);
                        this.getResponder().getCommunicator().sendSafeServerMessage("Failed to create the role \"" + roleName + "\". Please contact administration.");
                        return;
                    }
                }
                try {
                    final VillageRole vr2 = village.getRole(this.roleId);
                    final String oldName = vr2.getName();
                    final boolean aCitizen = vr2.getStatus() == 3 || (vr2.getStatus() == 0 && vr2.getVillageAppliedTo() == 0 && vr2.getPlayerAppliedTo() == -10L);
                    if (!oldName.equals(roleName)) {
                        vr2.setName(roleName);
                    }
                    vr2.setCanBrand(Boolean.parseBoolean(this.getAnswer().getProperty("brand")) && aCitizen);
                    vr2.setCanBreed(Boolean.parseBoolean(this.getAnswer().getProperty("breed")));
                    vr2.setCanButcher(Boolean.parseBoolean(this.getAnswer().getProperty("butcher")));
                    vr2.setCanGroom(Boolean.parseBoolean(this.getAnswer().getProperty("groom")));
                    vr2.setCanLead(Boolean.parseBoolean(this.getAnswer().getProperty("lead")));
                    vr2.setCanMilkShear(Boolean.parseBoolean(this.getAnswer().getProperty("milk")));
                    vr2.setCanSacrifice(Boolean.parseBoolean(this.getAnswer().getProperty("sacrifice")));
                    vr2.setCanTame(Boolean.parseBoolean(this.getAnswer().getProperty("tame")));
                    vr2.setCanBuild(Boolean.parseBoolean(this.getAnswer().getProperty("build")));
                    if (vr2.getStatus() != 1) {
                        vr2.setCanDestroyAnyBuilding(Boolean.parseBoolean(this.getAnswer().getProperty("destroyAnyBuildings")));
                    }
                    vr2.setCanDestroyFence(Boolean.parseBoolean(this.getAnswer().getProperty("destroyFence")));
                    vr2.setCanDestroyItems(Boolean.parseBoolean(this.getAnswer().getProperty("destroyIteme")));
                    vr2.setCanPickLocks(Boolean.parseBoolean(this.getAnswer().getProperty("pickLocks")) && aCitizen);
                    vr2.setCanPlanBuildings(Boolean.parseBoolean(this.getAnswer().getProperty("planBuildings")));
                    vr2.setCanCultivate(Boolean.parseBoolean(this.getAnswer().getProperty("cultivate")));
                    vr2.setCanDigResource(Boolean.parseBoolean(this.getAnswer().getProperty("digResource")));
                    vr2.setCanPack(Boolean.parseBoolean(this.getAnswer().getProperty("pack")));
                    vr2.setCanTerraform(Boolean.parseBoolean(this.getAnswer().getProperty("terraform")));
                    vr2.setCanHarvestFields(Boolean.parseBoolean(this.getAnswer().getProperty("harvest")));
                    vr2.setCanSowFields(Boolean.parseBoolean(this.getAnswer().getProperty("sow")));
                    vr2.setCanTendFields(Boolean.parseBoolean(this.getAnswer().getProperty("tend")));
                    vr2.setCanChopDownAllTrees(Boolean.parseBoolean(this.getAnswer().getProperty("chopAllTrees")));
                    vr2.setCanChopDownOldTrees(Boolean.parseBoolean(this.getAnswer().getProperty("chopOldTrees")));
                    vr2.setCanCutGrass(Boolean.parseBoolean(this.getAnswer().getProperty("cutGrass")));
                    vr2.setCanHarvestFruit(Boolean.parseBoolean(this.getAnswer().getProperty("harvestFruit")));
                    vr2.setCanMakeLawn(Boolean.parseBoolean(this.getAnswer().getProperty("makeLawn")));
                    vr2.setCanPickSprouts(Boolean.parseBoolean(this.getAnswer().getProperty("pickSprouts")));
                    vr2.setCanPlantFlowers(Boolean.parseBoolean(this.getAnswer().getProperty("plantFlowers")));
                    vr2.setCanPlantSprouts(Boolean.parseBoolean(this.getAnswer().getProperty("plantSprouts")));
                    vr2.setCanPrune(Boolean.parseBoolean(this.getAnswer().getProperty("prune")));
                    vr2.setCanAttackCitizens(Boolean.parseBoolean(this.getAnswer().getProperty("attackCitizens")));
                    vr2.setCanAttackNonCitizens(Boolean.parseBoolean(this.getAnswer().getProperty("attackNonCitizens")));
                    vr2.setCanCastDeitySpells(Boolean.parseBoolean(this.getAnswer().getProperty("deitySpells")));
                    vr2.setCanCastSorcerySpells(Boolean.parseBoolean(this.getAnswer().getProperty("sorcerySpells")));
                    vr2.setCanForageBotanize(Boolean.parseBoolean(this.getAnswer().getProperty("forage")));
                    vr2.setCanPassGates(Boolean.parseBoolean(this.getAnswer().getProperty("passGates")));
                    vr2.setCanPlaceMerchants(Boolean.parseBoolean(this.getAnswer().getProperty("placeMerchants")));
                    vr2.setCanPave(Boolean.parseBoolean(this.getAnswer().getProperty("pave")));
                    vr2.setCanUseMeditationAbility(Boolean.parseBoolean(this.getAnswer().getProperty("meditationAbilities")));
                    vr2.setCanAttachLocks(Boolean.parseBoolean(this.getAnswer().getProperty("attachLocks")) && aCitizen);
                    vr2.setCanDrop(Boolean.parseBoolean(this.getAnswer().getProperty("drop")));
                    vr2.setCanImproveRepair(Boolean.parseBoolean(this.getAnswer().getProperty("improve")));
                    vr2.setCanLoad(Boolean.parseBoolean(this.getAnswer().getProperty("load")));
                    vr2.setCanPickup(Boolean.parseBoolean(this.getAnswer().getProperty("pickup")));
                    vr2.setCanPickupPlanted(Boolean.parseBoolean(this.getAnswer().getProperty("pickupPlanted")));
                    vr2.setCanPlantItem(Boolean.parseBoolean(this.getAnswer().getProperty("plantItem")));
                    vr2.setCanPullPushTurn(Boolean.parseBoolean(this.getAnswer().getProperty("pullPushTurn")));
                    vr2.setCanUnload(Boolean.parseBoolean(this.getAnswer().getProperty("unload")));
                    vr2.setCanMineFloor(Boolean.parseBoolean(this.getAnswer().getProperty("mineFloor")));
                    vr2.setCanMineIron(Boolean.parseBoolean(this.getAnswer().getProperty("mineIron")));
                    vr2.setCanMineOther(Boolean.parseBoolean(this.getAnswer().getProperty("mineOther")));
                    vr2.setCanMineRock(Boolean.parseBoolean(this.getAnswer().getProperty("mineRock")));
                    vr2.setCanSurface(Boolean.parseBoolean(this.getAnswer().getProperty("surface")));
                    vr2.setCanTunnel(Boolean.parseBoolean(this.getAnswer().getProperty("tunnel")));
                    vr2.setCanReinforce(Boolean.parseBoolean(this.getAnswer().getProperty("reinforce")));
                    if (aCitizen) {
                        vr2.SetCanPerformActionsOnAlliedDeeds(Boolean.parseBoolean(this.getAnswer().getProperty("helpAllied")));
                        vr2.setCanDiplomat(Boolean.parseBoolean(this.getAnswer().getProperty("diplomat")));
                        vr2.setCanInviteCitizens(Boolean.parseBoolean(this.getAnswer().getProperty("inviteCitizens")));
                        vr2.setCanManageAllowedObjects(Boolean.parseBoolean(this.getAnswer().getProperty("manageAllowedObjects")));
                        vr2.setCanManageCitizenRoles(Boolean.parseBoolean(this.getAnswer().getProperty("manageCitizenRoles")));
                        vr2.setCanManageGuards(Boolean.parseBoolean(this.getAnswer().getProperty("manageGuards")));
                        vr2.setCanManageMap(Boolean.parseBoolean(this.getAnswer().getProperty("manageMap")));
                        vr2.setCanManageReputations(Boolean.parseBoolean(this.getAnswer().getProperty("manageReputations")));
                        vr2.setCanManageRoles(Boolean.parseBoolean(this.getAnswer().getProperty("manageRoles")));
                        vr2.setCanManageSettings(Boolean.parseBoolean(this.getAnswer().getProperty("manageSettings")));
                        vr2.setCanConfigureTwitter(Boolean.parseBoolean(this.getAnswer().getProperty("twitter")));
                        vr2.setCanResizeSettlement(Boolean.parseBoolean(this.getAnswer().getProperty("resize")));
                    }
                    vr2.save();
                    if (oldRoleId == -1) {
                        continue;
                    }
                    this.getResponder().getCommunicator().sendSafeServerMessage("The role \"" + roleName + "\" has been updated to your specifications.");
                }
                catch (NoSuchRoleException nsre2) {
                    VillageRolesManageQuestion.logger.log(Level.WARNING, nsre2.getMessage(), nsre2);
                    this.getResponder().getCommunicator().sendSafeServerMessage("Failed to find the role. Make sure it still exists.");
                }
                catch (IOException ioe) {
                    VillageRolesManageQuestion.logger.log(Level.WARNING, ioe.getMessage(), ioe);
                    this.getResponder().getCommunicator().sendSafeServerMessage("Failed to save the role.");
                }
            }
            else if (key.equals("back")) {
                final VillageRolesManageQuestion vrmq2 = new VillageRolesManageQuestion(this.getResponder(), this.getTitle(), this.getQuestion(), this.getTarget(), -10, this.gmBack, this.gmRowsPerPage, this.gmCurrentPage);
                vrmq2.sendQuestion();
            }
            else {
                if (key.equals("remove")) {
                    try {
                        final VillageRole vr3 = village.getRole(this.roleId);
                        if (vr3.getStatus() == 3) {
                            this.getResponder().getCommunicator().sendNormalServerMessage("There must always exist a citizen role. You cannot delete it.");
                        }
                        else {
                            final String name = vr3.getName();
                            village.removeRole(vr3);
                            vr3.delete();
                            this.getResponder().getCommunicator().sendNormalServerMessage("You have removed the role '" + name + "'.");
                        }
                    }
                    catch (NoSuchRoleException nsre3) {
                        VillageRolesManageQuestion.logger.log(Level.WARNING, nsre3.getMessage(), nsre3);
                    }
                    catch (IOException iox3) {
                        VillageRolesManageQuestion.logger.log(Level.WARNING, "Failed to change role " + this.roleId + ":" + iox3.getMessage(), iox3);
                        this.getResponder().getCommunicator().sendSafeServerMessage("Failed to change the role. Please contact administration.");
                    }
                    return;
                }
                continue;
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        final Village village = this.getVillage();
        if (village == null) {
            return;
        }
        if (this.roleId != -10) {
            this.roleShow(village, this.roleId);
        }
        else {
            this.roleList(village);
        }
    }
    
    private void roleShow(final Village village, final int rid) {
        VillageRole citRole = null;
        if (this.gmBack.length() == 0) {
            final Citizen cit = village.getCitizen(this.getResponder().getWurmId());
            if (cit == null) {
                this.getResponder().getCommunicator().sendNormalServerMessage("You are not a citizen here");
                return;
            }
            citRole = cit.getRole();
        }
        VillageRole villRole = null;
        try {
            if (rid != -1) {
                villRole = village.getRole(rid);
            }
        }
        catch (NoSuchRoleException nsre) {
            VillageRolesManageQuestion.logger.log(Level.WARNING, nsre.getMessage(), nsre);
            this.getResponder().getCommunicator().sendSafeServerMessage("Failed to find the role. Please contact administration.");
            return;
        }
        roleShow(this.getResponder(), this.getId(), citRole, villRole, this.gmBack);
    }
    
    public static void roleShow(final Creature performer, final int qId, final VillageRole citRole, final VillageRole villRole, final String gmBack) {
        String myRole1 = " ";
        String myRole2 = " ";
        if (citRole == null) {
            myRole1 = "";
            myRole2 = "INFO: Readonly";
        }
        else if (villRole == null) {
            myRole1 = "INFO: You can only add the permissions";
            myRole2 = " to a new role if your role has them.";
        }
        else if (villRole.getId() == citRole.getId()) {
            myRole1 = "WARNING: This is your own role!";
            myRole2 = "You can only disable permissions.";
        }
        else {
            myRole1 = "INFO: You can only change the permissions";
            myRole2 = " that your role has.";
        }
        boolean iCanBrand = false;
        boolean iCanBreed = false;
        boolean iCanButcher = false;
        boolean iCanGroom = false;
        boolean iCanLead = false;
        boolean iCanMilkShear = false;
        boolean iCanSacrifice = false;
        boolean iCanTame = false;
        boolean iCanBuild = false;
        boolean iCanDestroyFence = false;
        boolean iCanDestroyItems = false;
        boolean iCanPickLocks = false;
        boolean iCanPlanBuildings = false;
        boolean iCanCultivate = false;
        boolean iCanDigResource = false;
        boolean iCanPack = false;
        boolean iCanTerraform = false;
        boolean iCanHarvestFields = false;
        boolean iCanSowFields = false;
        boolean iCanTendFields = false;
        boolean iCanChopAllTrees = false;
        boolean iCanChopOldTrees = false;
        boolean iCanCutGrass = false;
        boolean iCanHarvestFruit = false;
        boolean iCanMakeLawn = false;
        boolean iCanPickSprouts = false;
        boolean iCanPlantFlowers = false;
        boolean iCanPlantSprouts = false;
        boolean iCanPrune = false;
        boolean iCanAttackCitizens = false;
        boolean iCanAttackNonCitizens = false;
        boolean iCanCastDeitySpells = false;
        boolean iCanCastSorcerySpells = false;
        boolean iCanForageBotanize = false;
        boolean iCanPassGates = false;
        boolean iCanPlaceMerchants = false;
        boolean iCanPave = false;
        boolean iCanUseMeditationAbilities = false;
        boolean iCanAttachLocks = false;
        boolean iCanDrop = false;
        boolean iCanImproveRepair = false;
        boolean iCanLoad = false;
        boolean iCanPickup = false;
        boolean iCanPickupPlanted = false;
        boolean iCanPullPushTurn = false;
        boolean iCanUnload = false;
        boolean iCanPlantItem = false;
        boolean iCanMineFloor = false;
        boolean iCanMineIron = false;
        boolean iCanMineOther = false;
        boolean iCanMineRock = false;
        boolean iCanSurface = false;
        boolean iCanTunnel = false;
        boolean iCanReinforce = false;
        boolean iCanHelpAllied = false;
        boolean iCanDiplomat = false;
        boolean iCanDestroyAnyBuilding = false;
        boolean iCanInviteCitizens = false;
        boolean iCanManageAllowedObjects = false;
        boolean iCanManageCitizenRoles = false;
        boolean iCanManageGuards = false;
        boolean iCanManageMap = false;
        boolean iCanManageReputations = false;
        boolean iCanManageRoles = false;
        boolean iCanManageSettings = false;
        boolean iCanConfigureTwitter = false;
        boolean iCanResizeSettlement = false;
        if (citRole != null) {
            iCanBrand = citRole.mayBrand();
            iCanBreed = citRole.mayBreed();
            iCanButcher = citRole.mayButcher();
            iCanGroom = citRole.mayGroom();
            iCanLead = citRole.mayLead();
            iCanMilkShear = citRole.mayMilkAndShear();
            iCanSacrifice = citRole.maySacrifice();
            iCanTame = citRole.mayTame();
            iCanBuild = citRole.mayBuild();
            iCanDestroyAnyBuilding = citRole.mayDestroyAnyBuilding();
            iCanDestroyFence = citRole.mayDestroyFences();
            iCanDestroyItems = citRole.mayDestroyItems();
            iCanPickLocks = citRole.mayPickLocks();
            iCanPlanBuildings = citRole.mayPlanBuildings();
            iCanCultivate = citRole.mayCultivate();
            iCanDigResource = citRole.mayDigResources();
            iCanPack = citRole.mayPack();
            iCanTerraform = citRole.mayTerraform();
            iCanHarvestFields = citRole.mayHarvestFields();
            iCanSowFields = citRole.maySowFields();
            iCanTendFields = citRole.mayTendFields();
            iCanChopAllTrees = citRole.mayChopDownAllTrees();
            iCanChopOldTrees = citRole.mayChopDownOldTrees();
            iCanCutGrass = citRole.mayCutGrass();
            iCanHarvestFruit = citRole.mayHarvestFruit();
            iCanMakeLawn = citRole.mayMakeLawn();
            iCanPickSprouts = citRole.mayPickSprouts();
            iCanPlantFlowers = citRole.mayPlantFlowers();
            iCanPlantSprouts = citRole.mayPlantSprouts();
            iCanPrune = citRole.mayPrune();
            iCanAttackCitizens = citRole.mayAttackCitizens();
            iCanAttackNonCitizens = citRole.mayAttackNonCitizens();
            iCanCastDeitySpells = citRole.mayCastDeitySpells();
            iCanCastSorcerySpells = citRole.mayCastSorcerySpells();
            iCanForageBotanize = citRole.mayForageAndBotanize();
            iCanPassGates = citRole.mayPassGates();
            iCanPlaceMerchants = citRole.mayPlaceMerchants();
            iCanPave = citRole.mayPave();
            iCanUseMeditationAbilities = citRole.mayUseMeditationAbilities();
            iCanAttachLocks = citRole.mayAttachLock();
            iCanDrop = citRole.mayDrop();
            iCanImproveRepair = citRole.mayImproveAndRepair();
            iCanLoad = citRole.mayLoad();
            iCanPickup = citRole.mayPickup();
            iCanPickupPlanted = citRole.mayPickupPlanted();
            iCanPullPushTurn = citRole.mayPushPullTurn();
            iCanUnload = citRole.mayUnload();
            iCanPlantItem = citRole.mayPlantItem();
            iCanMineFloor = citRole.mayMineFloor();
            iCanMineIron = citRole.mayMineIronVeins();
            iCanMineOther = citRole.mayMineOtherVeins();
            iCanMineRock = citRole.mayMineRock();
            iCanSurface = citRole.mayMineSurface();
            iCanTunnel = citRole.mayTunnel();
            iCanReinforce = citRole.mayReinforce();
            iCanHelpAllied = citRole.mayPerformActionsOnAlliedDeeds();
            iCanDiplomat = citRole.isDiplomat();
            iCanInviteCitizens = citRole.mayInviteCitizens();
            iCanManageAllowedObjects = citRole.mayManageAllowedObjects();
            iCanManageCitizenRoles = citRole.mayManageCitizenRoles();
            iCanManageGuards = citRole.mayManageGuards();
            iCanManageMap = citRole.mayManageMap();
            iCanManageReputations = citRole.mayManageReputations();
            iCanManageRoles = citRole.mayManageRoles();
            iCanManageSettings = citRole.mayManageSettings();
            iCanConfigureTwitter = citRole.mayConfigureTwitter();
            iCanResizeSettlement = citRole.mayResizeSettlement();
        }
        String roleDesc = "new role";
        String roleName = "";
        String defaultRole = "";
        byte role = 0;
        boolean hasExternalSettlement = true;
        boolean hasIndividualPlayer = true;
        String externalSettlement = "";
        String individualPlayer = "";
        boolean brand = false;
        boolean breed = false;
        boolean butcher = false;
        boolean groom = false;
        boolean lead = false;
        boolean milkShear = false;
        boolean sacrifice = false;
        boolean tame = false;
        boolean build = false;
        boolean destroyFence = false;
        boolean destroyItems = false;
        boolean pickLocks = false;
        boolean planBuildings = false;
        boolean cultivate = false;
        boolean digResource = false;
        boolean pack = false;
        boolean terraform = false;
        boolean harvestFields = false;
        boolean sowFields = false;
        boolean tendFields = false;
        boolean chopAllTrees = false;
        boolean chopOldTrees = false;
        boolean cutGrass = false;
        boolean harvestFruit = false;
        boolean makeLawn = false;
        boolean pickSprouts = false;
        boolean plantFlowers = false;
        boolean plantSprouts = false;
        boolean prune = false;
        boolean attackCitizens = false;
        boolean attackNonCitizens = false;
        boolean deitySpells = false;
        boolean sorcerySpells = false;
        boolean forageBotanize = false;
        boolean passGates = false;
        boolean placeMerchants = false;
        boolean pave = false;
        boolean meditationAbilities = false;
        boolean attachLocks = false;
        boolean drop = false;
        boolean improveRepair = false;
        boolean load = false;
        boolean pickup = false;
        boolean pickupPlanted = false;
        boolean pullPushTurn = false;
        boolean unload = false;
        boolean plantItem = false;
        boolean mineFloor = false;
        boolean mineIron = false;
        boolean mineOther = false;
        boolean mineRock = false;
        boolean surface = false;
        boolean tunnel = false;
        boolean reinforce = false;
        boolean helpAllied = false;
        boolean diplomat = false;
        boolean destroyAnyBuilding = false;
        boolean inviteCitizens = false;
        boolean manageCitizenRoles = false;
        boolean manageAllowedObjects = false;
        boolean manageGuards = false;
        boolean manageMap = false;
        boolean manageReputations = false;
        boolean manageRoles = false;
        boolean manageSettings = false;
        boolean configureTwitter = false;
        boolean resizeSettlement = false;
        if (villRole != null) {
            roleName = villRole.getName();
            roleDesc = "role '" + roleName + "'";
            role = villRole.getStatus();
            switch (role) {
                case 3: {
                    defaultRole = "Citizen";
                    break;
                }
                case 2: {
                    defaultRole = "Mayor";
                    break;
                }
                case 4: {
                    defaultRole = "Guard";
                    break;
                }
                case 6: {
                    defaultRole = "Wagoner";
                    break;
                }
                case 5: {
                    defaultRole = "Ally";
                    break;
                }
                case 1: {
                    defaultRole = "Non-Citizen";
                    iCanDestroyAnyBuilding = false;
                    break;
                }
            }
            hasExternalSettlement = false;
            externalSettlement = "n/a";
            if (role == 0 && villRole.getVillageAppliedTo() != 0) {
                try {
                    final Village external = Villages.getVillage(villRole.getVillageAppliedTo());
                    externalSettlement = external.getName();
                    hasExternalSettlement = true;
                }
                catch (NoSuchVillageException e) {
                    externalSettlement = "Missing Settlement";
                }
            }
            if (role == 0 && villRole.getPlayerAppliedTo() != -10L) {
                individualPlayer = PlayerInfoFactory.getPlayerName(villRole.getPlayerAppliedTo());
                hasIndividualPlayer = true;
            }
            else {
                hasIndividualPlayer = false;
                individualPlayer = "n/a";
            }
            brand = villRole.mayBrand();
            breed = villRole.mayBreed();
            butcher = villRole.mayButcher();
            groom = villRole.mayGroom();
            lead = villRole.mayLead();
            milkShear = villRole.mayMilkAndShear();
            sacrifice = villRole.maySacrifice();
            tame = villRole.mayTame();
            build = villRole.mayBuild();
            destroyAnyBuilding = villRole.mayDestroyAnyBuilding();
            destroyFence = villRole.mayDestroyFences();
            destroyItems = villRole.mayDestroyItems();
            pickLocks = villRole.mayPickLocks();
            planBuildings = villRole.mayPlanBuildings();
            cultivate = villRole.mayCultivate();
            digResource = villRole.mayDigResources();
            pack = villRole.mayPack();
            terraform = villRole.mayTerraform();
            harvestFields = villRole.mayHarvestFields();
            sowFields = villRole.maySowFields();
            tendFields = villRole.mayTendFields();
            chopAllTrees = villRole.mayChopDownAllTrees();
            chopOldTrees = villRole.mayChopDownOldTrees();
            cutGrass = villRole.mayCutGrass();
            harvestFruit = villRole.mayHarvestFruit();
            makeLawn = villRole.mayMakeLawn();
            pickSprouts = villRole.mayPickSprouts();
            plantFlowers = villRole.mayPlantFlowers();
            plantSprouts = villRole.mayPlantSprouts();
            prune = villRole.mayPrune();
            attackCitizens = villRole.mayAttackCitizens();
            attackNonCitizens = villRole.mayAttackNonCitizens();
            deitySpells = villRole.mayCastDeitySpells();
            sorcerySpells = villRole.mayCastSorcerySpells();
            forageBotanize = villRole.mayForageAndBotanize();
            passGates = villRole.mayPassGates();
            placeMerchants = villRole.mayPlaceMerchants();
            pave = villRole.mayPave();
            meditationAbilities = villRole.mayUseMeditationAbilities();
            attachLocks = villRole.mayAttachLock();
            drop = villRole.mayDrop();
            improveRepair = villRole.mayImproveAndRepair();
            load = villRole.mayLoad();
            pickup = villRole.mayPickup();
            pickupPlanted = villRole.mayPickupPlanted();
            pullPushTurn = villRole.mayPushPullTurn();
            unload = villRole.mayUnload();
            plantItem = villRole.mayPlantItem();
            mineFloor = villRole.mayMineFloor();
            mineIron = villRole.mayMineIronVeins();
            mineOther = villRole.mayMineOtherVeins();
            mineRock = villRole.mayMineRock();
            surface = villRole.mayMineSurface();
            tunnel = villRole.mayTunnel();
            reinforce = villRole.mayReinforce();
            helpAllied = villRole.mayPerformActionsOnAlliedDeeds();
            diplomat = villRole.isDiplomat();
            inviteCitizens = villRole.mayInviteCitizens();
            manageAllowedObjects = villRole.mayManageAllowedObjects();
            manageCitizenRoles = villRole.mayManageCitizenRoles();
            manageGuards = villRole.mayManageGuards();
            manageMap = villRole.mayManageMap();
            manageReputations = villRole.mayManageReputations();
            manageRoles = villRole.mayManageRoles();
            manageSettings = villRole.mayManageSettings();
            configureTwitter = villRole.mayConfigureTwitter();
            resizeSettlement = villRole.mayResizeSettlement();
            if ((role == 0 && villRole.getVillageAppliedTo() != 0) || role == 1 || role == 5 || role == 4 || role == 6) {
                iCanPickLocks = false;
                iCanAttachLocks = false;
                iCanBrand = false;
            }
        }
        String destroyAllPopup = "";
        if (!Servers.localServer.PVPSERVER) {
            destroyAllPopup = ";confirm=\"The Destroy Any Buildings option enables theft\";question=\"Are sure you want this " + roleDesc + " to be able to destroy buildings since it enables theft?\"";
        }
        final String citOnlyColour = ";color=\"127,127,255\"";
        final String popupColour = ";color=\"255,127,127\"";
        final boolean citTypeRole = role == 2 || role == 3 || (role == 0 && !hasExternalSettlement && !hasIndividualPlayer) || villRole == null;
        final StringBuilder buf = new StringBuilder();
        buf.append("border{border{null;null;varray{rescale=\"true\";table{rows=\"2\";cols=\"3\";label{type='bold';text=\"Role Name \"};" + ((citRole != null) ? "input{maxchars=\"20\";id=\"roleName\";" : "label{") + "text=\"" + roleName + "\"}label{text=\"" + myRole1 + "\"}");
        if (hasExternalSettlement) {
            buf.append("label{type=\"bold\";text=\"Settlement \"};" + ((externalSettlement.length() == 0) ? "input{maxchars=\"40\";id=\"externalSettlement\";" : "label{") + "text=\"" + externalSettlement + "\"}");
        }
        else {
            buf.append("label{text=\" \"}label{text=\" \"}");
        }
        buf.append("label{text=\"" + myRole2 + "\"}");
        if (hasIndividualPlayer) {
            buf.append("label{type=\"bold\";text=\"Player \"};" + ((individualPlayer.length() == 0) ? "input{maxchars=\"40\";id=\"individualPlayer\";" : "label{") + "text=\"" + individualPlayer + "\"}");
        }
        else {
            buf.append("label{text=\" \"}label{text=\" \"}");
        }
        if (citRole != null && villRole != null && role == 0) {
            buf.append("harray{label{text=\"  \"};button{text=\"Remove role\";id=\"remove\";confirm=\"You are about to remove this role.\";question=\"Are you sure you want to do that?\"}}");
        }
        else if (defaultRole.length() > 0) {
            buf.append("label{type=\"bolditalic\";text=\"This is the default " + defaultRole + " role.\"}");
        }
        else {
            buf.append("label{text=\" \"}");
        }
        buf.append("};");
        buf.append("};varray{right{harray{label{text=\" \"};" + ((citRole != null || gmBack.length() > 0) ? "button{text=\"Back\";id=\"back\"};" : "") + "label{text=\" \"};button{text=\"Close\";id=\"close\"}}}label{text=\" \"}right{harray{label{text=\" \"};" + ((citRole != null) ? "button{text=\"Save\";id=\"save\"}" : "") + "}}};null;");
        buf.append("}null;scroll{vertical=\"true\";horizontal=\"false\";varray{rescale=\"true\";passthrough{id=\"id\";text=\"" + qId + "\"}");
        final String blank = "image{src=\"img.gui.bridge.blank\";size=\"" + (Servers.isThisAPvpServer() ? " 240" : "220") + ",2\";text=\"\"}";
        buf.append("label{type=\"bold\";text=\"Animals\"}");
        buf.append("table{rows=\"3\";cols=\"3\";");
        buf.append(permission("brand", brand, "Brand", iCanBrand, "Allows branding of animals on deed - Only for Citizen type roles.", ";color=\"127,127,255\"", "never"));
        buf.append(permission("breed", breed, "Breed", iCanBreed, "Allows breeding of animals on deed.", "", "always"));
        buf.append(permission("butcher", butcher, "Butcher", iCanButcher, "Allows butchering on deed.", "", "always"));
        buf.append(permission("groom", groom, "Groom", iCanGroom, "Allows grooming of animals on deed.", "", "always"));
        buf.append(permission("lead", lead, "Lead", iCanLead, "Allows leading of animals on deed.", "", "always"));
        buf.append(permission("milk", milkShear, "Milk / Shear", iCanMilkShear, "Allows milking of cows and shearing of sheep.", "", "always"));
        buf.append(permission("sacrifice", sacrifice, "Sacrifice", iCanSacrifice, "Allows sacrificing of animals on deed.", "", "always"));
        buf.append(permission("tame", tame, "Tame / Charm / Dominate", iCanTame, "Allows taming of animals on deed. Charming and Domination of animals requires this and 'Cast Deity Spells' privilege.", "", "always"));
        buf.append("label{text=\"\"};");
        buf.append(blank + blank + blank);
        buf.append("}");
        buf.append("label{type=\"bold\";text=\"Construction\"}");
        buf.append("table{rows=\"3\";cols=\"3\";");
        buf.append(permission("build", build, "Build", iCanBuild, "Allows ability to color and continue on buildings and build fences and place mine doors.", "", "noGuards"));
        buf.append(permission("destroyAnyBuildings", destroyAnyBuilding, "Destroy Any Buildings", iCanDestroyAnyBuilding, "Allows demolition of any building, not available for non-citizen role.", ";color=\"255,127,127\"" + destroyAllPopup, "noGuards"));
        buf.append(permission("destroyFence", destroyFence, "Destroy Fences", iCanDestroyFence, "Allows destruction of fences and gates.", "", "noGuards"));
        buf.append(permission("destroyIteme", destroyItems, "Destroy Items", iCanDestroyItems, "Allows destruction of items.", "", "noGuards"));
        buf.append(permission("pickLocks", pickLocks, "Pick Locks", iCanPickLocks, "Allows lockpicking of buildings, items and gates. Also requires Pickup permission to lockpick items. - Only for Citizen type roles.", ";color=\"127,127,255\"", "noGuards"));
        buf.append(permission("planBuildings", planBuildings, "Plan Buildings", iCanPlanBuildings, "Allows planning of buildings.", "", "noGuards"));
        buf.append(blank + blank + blank);
        buf.append("}");
        buf.append("label{type=\"bold\";text=\"Digging\"}");
        buf.append("table{rows=\"3\";cols=\"3\";");
        buf.append(permission("cultivate", cultivate, "Cultivate", iCanCultivate, "Allows cultivating of packed tiles.", "", "noGuards"));
        buf.append(permission("digResource", digResource, "Dig Clay / Tar / Peat / Moss", iCanDigResource, "Allows digging of resource tiles.", "", "always"));
        buf.append(permission("pack", pack, "Pack", iCanPack, "Allows packing of tiles.", "", "noGuards"));
        buf.append(permission("terraform", terraform, "Terraform", iCanTerraform, "Allows terraforming (digging, levelling and flattening).", "", "both"));
        buf.append("label{text=\"\"};");
        buf.append("label{text=\"\"};");
        buf.append(blank + blank + blank);
        buf.append("}");
        buf.append("label{type=\"bold\";text=\"Farming\"}");
        buf.append("table{rows=\"3\";cols=\"3\";");
        buf.append(permission("harvest", harvestFields, "Harvest Fields", iCanHarvestFields, "Allows harvesting of fields.", "", "always"));
        buf.append(permission("sow", sowFields, "Sow Fields", iCanSowFields, "Allows sowing of fields.", "", "always"));
        buf.append(permission("tend", tendFields, "Tend Fields", iCanTendFields, "Allows tending of fields.", "", "always"));
        buf.append(blank + blank + blank);
        buf.append("}");
        buf.append("label{type=\"bold\";text=\"Forestry / Gardening\"}");
        buf.append("table{rows=\"3\";cols=\"3\";");
        buf.append(permission("chopAllTrees", chopAllTrees, "Chop Down Trees and Bushes", iCanChopAllTrees, "Allows chopping down of all trees and bushes except the very old and overaged trees.", "", "always"));
        buf.append(permission("chopOldTrees", chopOldTrees, "Chop Down V.Old Trees", iCanChopOldTrees, "Allows chopping down of Very Old and Overaged trees. Also allows 'Wild Growth' spell on hedges, but that requires 'Cast Deity Spells' privilege as well.", "", "always"));
        buf.append(permission("cutGrass", cutGrass, "Cut Grass", iCanCutGrass, "Allows cutting of grass.", "", "always"));
        buf.append(permission("harvestFruit", harvestFruit, "Harvest Fruit", iCanHarvestFruit, "Allows harvesting from trees and bushes.", "", "always"));
        buf.append(permission("makeLawn", makeLawn, "Make Lawn", iCanMakeLawn, "Allows ability to make lawn.", "", "always"));
        buf.append(permission("pickSprouts", pickSprouts, "Pick Sprouts", iCanPickSprouts, "Allows picking of sprouts from tress and bushes and also allows picking of flowers.", "", "always"));
        buf.append(permission("plantFlowers", plantFlowers, "Plant Flowers", iCanPlantFlowers, "Allows planting of flowers, mixed grass and thatch.", "", "always"));
        buf.append(permission("plantSprouts", plantSprouts, "Plant Sprouts", iCanPlantSprouts, "Allows planting of sprouts including hedges.", "", "always"));
        buf.append(permission("prune", prune, "Prune", iCanPrune, "Allows pruning of trees and bushes including hedges.", "", "always"));
        buf.append(blank + blank + blank);
        buf.append("}");
        buf.append("label{type=\"bold\";text=\"General\"}");
        buf.append("table{rows=\"3\";cols=\"3\";");
        buf.append(permission("attackCitizens", attackCitizens, "Attack Citizens", iCanAttackCitizens, "Allows attacking of citizens and allies of the settlement.", "", "always"));
        buf.append(permission("attackNonCitizens", attackNonCitizens, "Attack Non Citizens", iCanAttackNonCitizens, "Allows attacking of non citizens which includes animals.", "", "always"));
        buf.append(permission("deitySpells", deitySpells, "Cast Deity Spells", iCanCastDeitySpells, "Allows use of deity spells on deed.", "", "always"));
        buf.append(permission("sorcerySpells", sorcerySpells, "Cast Sorcery Spells", iCanCastSorcerySpells, "Allows use of sorcery spells on deed.", "", "always"));
        buf.append(permission("forage", forageBotanize, "Forage / Botanize / Investigate", iCanForageBotanize, "Allows foraging, botanizing and investigating on deed.", "", "always"));
        buf.append(permission("passGates", passGates, "May Pass Gates", iCanPassGates, "Allows passing of gates that don't have any permissions set.", "", "never"));
        if (Features.Feature.WAGONER.isEnabled()) {
            buf.append(permission("placeMerchants", placeMerchants, "May Place NPCs", iCanPlaceMerchants, "Allows placing of Merchants, Traders and Wagoners (citizen only).", "", "never"));
        }
        else {
            buf.append(permission("placeMerchants", placeMerchants, "May Place Merchants", iCanPlaceMerchants, "Allows placing of Merchants and Traders.", "", "never"));
        }
        buf.append(permission("pave", pave, "Pave", iCanPave, "Allows adding or removing of paving on deed.", "", "noGuards"));
        buf.append(permission("meditationAbilities", meditationAbilities, "Use Meditation Abilities", iCanUseMeditationAbilities, "Allows use of meditation abilities on deed.", "", "always"));
        buf.append(blank + blank + blank);
        buf.append("}");
        buf.append("label{type=\"bold\";text=\"Item Management\"}");
        buf.append("table{rows=\"3\";cols=\"3\";");
        buf.append(permission("attachLocks", attachLocks, "Attach Locks", iCanAttachLocks, "Allows attaching locks to items on deed - Only for Citizen type roles.", ";color=\"127,127,255\"", "noGuards"));
        buf.append(permission("drop", drop, "Drop", iCanDrop, "Allows dropping of items on deed.", "", "always"));
        buf.append(permission("improve", improveRepair, "Improve / Repair", iCanImproveRepair, "Allows improving and repairing of items on deed.", "", "always"));
        buf.append(permission("load", load, "Load", iCanLoad, "Allows loading of items on deed, also requires 'Pickup' permission, or 'Pickup Planted' permission.", "", "noGuards"));
        buf.append(permission("pickup", pickup, "Pickup", iCanPickup, "Allows picking up of items on deed.", "", "always"));
        buf.append(permission("pickupPlanted", pickupPlanted, "Pickup Planted Items", iCanPickupPlanted, "Allows picking up of planted items on deed, also requires 'Pickup' permission.", "", "noGuards"));
        buf.append(permission("plantItem", plantItem, "Plant Item", iCanPlantItem, "Allows planting and securing of items on deed.", "", "noGuards"));
        buf.append(permission("pullPushTurn", pullPushTurn, "Pull / Push / Turn", iCanPullPushTurn, "Allows pull, push and turning of items on deed.", "", "noGuards"));
        buf.append(permission("unload", unload, "UnLoad", iCanUnload, "Allows unloading of items on deed.", "", "noGuards"));
        buf.append(blank + blank + blank);
        buf.append("}");
        buf.append("label{type=\"bold\";text=\"Mining\"}");
        buf.append("table{rows=\"3\";cols=\"3\";");
        buf.append(permission("reinforce", reinforce, "Add/Remove Reinforcement", iCanReinforce, "Allows adding and removing of reinforced walls (needs Mine Rock) and floors (needs Mine Floor) on deed. Also used to control Disintegrate.", "", "never"));
        buf.append(permission("mineFloor", mineFloor, "Mine Floor", iCanMineFloor, "Allows mining of floors and ceilings, including levelling and flattening in caves on deed.", "", "noGuards"));
        buf.append(permission("mineIron", mineIron, "Mine Iron Veins", iCanMineIron, "Allows mining of Iron veins on deed.", "", "noGuards"));
        buf.append(permission("mineOther", mineOther, "Mine Other Veins", iCanMineOther, "Allows mining of other veins on deed.", "", "noGuards"));
        buf.append(permission("mineRock", mineRock, "Mine Rock", iCanMineRock, "Allows mining of rock in caves on deed.", "", "noGuards"));
        buf.append(permission("surface", surface, "Surface Mining", iCanSurface, "Allows surface mining on deed.", "", "noGuards"));
        buf.append(permission("tunnel", tunnel, "Tunnel", iCanTunnel, "Allows making tunnel entrances on deed.", "", "noGuards"));
        buf.append("label{text=\"\"}");
        buf.append("label{text=\"\"}");
        buf.append(blank + blank + blank);
        buf.append("}");
        if (citTypeRole) {
            buf.append("harray{label{;color=\"127,127,255\"type=\"bold\";text=\"Settlement Management\"}");
            if (villRole == null) {
                buf.append("label{;color=\"127,127,255\"text=\" - Only for Citizen type roles.\"}");
            }
            buf.append("}");
            buf.append("table{rows=\"3\";cols=\"3\";");
            buf.append(permission("helpAllied", helpAllied, "Allow Actions on Allied Deeds", iCanHelpAllied, "Allows Actions on allied deeds using their ally role, if not set then player will use their non-citizen role.", ";color=\"127,127,255\"", ""));
            buf.append(permission("inviteCitizens", inviteCitizens, "Invite Citizens", iCanInviteCitizens, "Allows ability to invite non-citizens to join the settlement including using recruitment boards.", ";color=\"127,127,255\"", ""));
            buf.append(permission("manageAllowedObjects", manageAllowedObjects, "Manage Allowed Objects", iCanManageAllowedObjects, "Allows ability to manage allowed objects, that's buildings, gates and mine doors that have the 'Settlement May Manage' flag set.", ";color=\"127,127,255\"", ""));
            buf.append(permission("manageCitizenRoles", manageCitizenRoles, "Manage Citizen Roles", iCanManageCitizenRoles, "Allows ability to assign roles to citizens.", ";color=\"127,127,255\"", ""));
            buf.append(permission("manageGuards", manageGuards, "Manage Guards", iCanManageGuards, "Allows hiring guards to protect the settlement and uphold these rules.", ";color=\"127,127,255\"", ""));
            buf.append(permission("manageMap", manageMap, "Manage Map", iCanManageMap, "Allows ability to add and remove village annotations on the map. Updating alliance annotations requires the manage politics permission as well.", ";color=\"127,127,255\"", ""));
            buf.append(permission("diplomat", diplomat, "Manage Politics", iCanDiplomat, "Allows ability to form and break alliances with other settlements, as well as declare war and make peace.", ";color=\"127,127,255\"", ""));
            buf.append(permission("manageReputations", manageReputations, "Manage Reputations", iCanManageReputations, "Allows ability to manage reputations.", ";color=\"127,127,255\"", ""));
            buf.append(permission("manageRoles", manageRoles, "Manage Roles", iCanManageRoles, "Allows managing of permissions in any role in which you have the corresponding permission in your role.", ";color=\"127,127,255\"", ""));
            buf.append(permission("manageSettings", manageSettings, "Manage Settings", iCanManageSettings, "Allows changing settlement settings.", ";color=\"127,127,255\"", ""));
            buf.append(permission("twitter", configureTwitter, "May Configure Twitter", iCanConfigureTwitter, "Allows ability to configure twitter settings.", ";color=\"127,127,255\"", ""));
            buf.append(permission("resize", resizeSettlement, "Resize Settlement", iCanResizeSettlement, "Allows ability to change the size of the settlement.", ";color=\"127,127,255\"", ""));
            buf.append(blank + blank + blank);
            buf.append("}");
        }
        buf.append("}};null;null;null;null;}");
        performer.getCommunicator().sendBml(560, 400, true, true, buf.toString(), 200, 200, 200, "Role Management");
    }
    
    private static String permission(final String sid, final boolean selected, final String text, final boolean enabled, final String hover, final String extra, final String enemyIcon) {
        String icon = "";
        if (Servers.isThisAPvpServer() && enemyIcon.length() != 0) {
            switch (enemyIcon) {
                case "always": {
                    icon = "image{src=\"img.gui.circle.green\";size=\"16,16\";text=\"Should always be doable by enemy.\"}";
                    break;
                }
                case "never": {
                    icon = "image{src=\"img.gui.circle.red\";size=\"16,16\";text=\"Should never be doable by enemy.\"}";
                    break;
                }
                case "both": {
                    icon = "image{src=\"img.gui.circle.both\";size=\"16,16\";text=\"Should only be doable by enemy if guards are dead. Except dig which is always doable.\"}";
                    break;
                }
                case "noGuards": {
                    icon = "image{src=\"img.gui.circle.yellow\";size=\"16,16\";text=\"Should only be doable by enemy if guards are dead.\"}";
                    break;
                }
            }
        }
        if (enabled) {
            return "harray{checkbox{id=\"" + sid + "\";selected=\"" + selected + "\";text=\"" + text + "  \"" + extra + ";hover=\"" + hover + "\"};" + icon + "};";
        }
        return "harray{image{src=\"img.gui." + (selected ? "vsmall" : "xsmall") + "\";size=\"16,16\";text=\"" + hover + "\"}label{text=\"" + text + "\"" + extra + ";hover=\"" + hover + "\"};" + icon + "};";
    }
    
    public void roleList(final Village village) {
        final VillageRole[] roles = village.getRoles();
        Arrays.sort(roles);
        final StringBuilder buf = new StringBuilder();
        if (this.gmBack.length() == 0) {
            String buttons = "button{text=\"Close\";id=\"close\"}";
            if (roles.length < 50) {
                buttons = "button{text=\"Add role\";id=\"add\"};label{text=\" \"};" + buttons;
            }
            buf.append("border{border{null;null;label{type='bold';text=\"Manage Roles for " + village.getName() + "\"};harray{" + buttons + "};harray{button{text=\"Reset all roles\";id=\"reset\";confirm=\"You are about to reset all the roles.\";question=\"Are you sure you want to do that?\"};label{text=\"Everyone except the mayor will have the Citizen role.\"}}}null;scroll{vertical=\"true\";horizontal=\"false\";varray{rescale=\"true\";passthrough{id=\"id\";text=\"" + this.getId() + "\"}");
            if (village.isDisbanding()) {
                final long timeleft = village.getDisbanding() - System.currentTimeMillis();
                final String times = Server.getTimeFor(timeleft);
                buf.append("text{type=\"bold\";text=\"This settlement is disbanding\"}");
                if (timeleft > 0L) {
                    buf.append("text{type=\"bold\";text=\"Eta: " + times + ".\"};text{text=\"\"};");
                }
                else {
                    buf.append("text{type=\"bold\";text=\"Eta:  any minute now.\"};text{text=\"\"};");
                }
            }
            buf.append("text{text=\"\"}");
            buf.append("table{rows=\"3\";cols=\"3\";");
            boolean hasGuardRole = false;
            boolean hasWagonerRole = false;
            for (int x = 0; x < roles.length; ++x) {
                if (roles[x].getStatus() == 2) {
                    buf.append("harray{button{text=\"Change Name\";id=\"change" + roles[x].getId() + "\"};label{text=\" \"}}");
                    buf.append("input{maxchars=\"20\";id=\"name\";text=\"" + roles[x].getName() + "\";onenter=\"change" + roles[x].getId() + "\"};label{text=\"Default Mayor Role\"}");
                }
                if (roles[x].getStatus() == 4) {
                    hasGuardRole = true;
                }
                if (roles[x].getStatus() == 6) {
                    hasWagonerRole = true;
                }
            }
            if (hasGuardRole) {
                buf.append("label{text=\" \"}");
                buf.append("label{text=\"Guard\"}");
                buf.append("label{text=\"Default Guard Role\"}");
            }
            for (int x = 0; x < roles.length; ++x) {
                if (roles[x].getStatus() != 2 && roles[x].getStatus() != 4 && roles[x].getStatus() != 6) {
                    buf.append("right{harray{button{text=\"Edit\";id=\"edit" + roles[x].getId() + "\"};label{text=\" \"}}}");
                    buf.append("label{text=\"" + roles[x].getName() + "\"}");
                    switch (roles[x].getStatus()) {
                        case 1: {
                            buf.append("label{text=\"Default Non-Citizen Role\"}");
                            break;
                        }
                        case 3: {
                            buf.append("label{text=\"Default Citizen Role\"}");
                            break;
                        }
                        case 5: {
                            buf.append("label{text=\"Default Allied Role\"}");
                            break;
                        }
                        default: {
                            if (roles[x].getVillageAppliedTo() > 0) {
                                buf.append("label{text=\"External Village\"}");
                                break;
                            }
                            if (roles[x].getPlayerAppliedTo() != -10L) {
                                buf.append("label{text=\"Individual Player\"}");
                                break;
                            }
                            buf.append("label{text=\"\"}");
                            break;
                        }
                    }
                }
            }
            if (hasWagonerRole) {
                buf.append("label{text=\" \"}");
                buf.append("label{text=\"Wagoner\"}");
                buf.append("label{text=\"Default Wagoner Role\"}");
            }
            buf.append("}");
            buf.append("text{text=\"\"}");
            buf.append("text{text=\"If the checkbox is checked, it means the role WILL BE able to perform the task.\"}");
            if (Servers.isThisAPvpServer()) {
                buf.append("text{type=\"bold\";text=\"Note that you will need at least 1 guard to enforce these rules on deed!\"}");
            }
            buf.append("text{text=\"If you want to change the name, simply type another name there.\"}");
            buf.append("text{text=\" \"}");
            buf.append("text{type=\"bold\";text=\"Note! The role name may contain the following letters: \"}");
            buf.append("text{text=\"a-z,A-Z,', and -\"}text{text=\"\"}");
            buf.append("text{text=\"The non-citizens role means those who are not citizens. Normally they shouldn't be able to do anything at all.\"}");
            buf.append("text{text=\"The citizen role is the default role people get when they become citizens.\"}");
            buf.append("text{text=\"\"}");
            buf.append("}};null;null;null;null;}");
            this.getResponder().getCommunicator().sendBml(450, 400, true, true, buf.toString(), 200, 200, 200, this.title);
        }
        else {
            buf.append("border{border{null;null;label{type='bold';text=\"Show Roles for " + village.getName() + "\"};harray{button{text=\"Close\";id=\"close\"}};harray{button{text=\"Back to GM Tool\";id=\"GMTool\"}};}null;scroll{vertical=\"true\";horizontal=\"false\";varray{rescale=\"true\";passthrough{id=\"id\";text=\"" + this.getId() + "\"}");
            if (village.isDisbanding()) {
                final long timeleft2 = village.getDisbanding() - System.currentTimeMillis();
                final String times2 = Server.getTimeFor(timeleft2);
                buf.append("text{type=\"bold\";text=\"This settlement is disbanding\"}");
                if (timeleft2 > 0L) {
                    buf.append("text{type=\"bold\";text=\"Eta: " + times2 + ".\"};text{text=\"\"};");
                }
                else {
                    buf.append("text{type=\"bold\";text=\"Eta:  any minute now.\"};text{text=\"\"};");
                }
            }
            buf.append("text{text=\"\"}");
            buf.append("table{rows=\"3\";cols=\"3\";");
            int guardRoleId = -1;
            int wagonerRoleId = -1;
            for (int x2 = 0; x2 < roles.length; ++x2) {
                if (roles[x2].getStatus() == 2) {
                    buf.append("harray{button{text=\"Show\";id=\"show" + roles[x2].getId() + "\"};label{text=\" \"}}");
                    buf.append("label{text=\"" + roles[x2].getName() + "\"}label{text=\"Default Mayor Role\"}");
                }
                if (roles[x2].getStatus() == 4) {
                    guardRoleId = roles[x2].getId();
                }
                if (roles[x2].getStatus() == 6) {
                    wagonerRoleId = roles[x2].getId();
                }
            }
            if (guardRoleId > -1) {
                buf.append("harray{button{text=\"Show\";id=\"show" + guardRoleId + "\"};label{text=\" \"}}");
                buf.append("label{text=\"Guard\"}label{text=\"Default Guard Role\"}");
            }
            for (int x2 = 0; x2 < roles.length; ++x2) {
                if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4 && roles[x2].getStatus() != 6) {
                    buf.append("right{harray{button{text=\"Show\";id=\"show" + roles[x2].getId() + "\"};label{text=\" \"}}}");
                    buf.append("label{text=\"" + roles[x2].getName() + "\"}");
                    switch (roles[x2].getStatus()) {
                        case 1: {
                            buf.append("label{text=\"Default Non-Citizen Role\"}");
                            break;
                        }
                        case 3: {
                            buf.append("label{text=\"Default Citizen Role\"}");
                            break;
                        }
                        case 5: {
                            buf.append("label{text=\"Default Allied Role\"}");
                            break;
                        }
                        default: {
                            if (roles[x2].getVillageAppliedTo() > 0) {
                                buf.append("label{text=\"External Village\"}");
                                break;
                            }
                            if (roles[x2].getPlayerAppliedTo() != -10L) {
                                buf.append("label{text=\"Individual Player\"}");
                                break;
                            }
                            buf.append("label{text=\"\"}");
                            break;
                        }
                    }
                }
            }
            if (wagonerRoleId > -1) {
                buf.append("harray{button{text=\"Show\";id=\"show" + wagonerRoleId + "\"};label{text=\" \"}}");
                buf.append("label{text=\"Wagoner\"}label{text=\"Default Wagoner Role\"}");
            }
            buf.append("}");
            buf.append("text{text=\"\"}");
            buf.append("}};null;null;null;null;}");
            this.getResponder().getCommunicator().sendBml(250, 300, true, true, buf.toString(), 200, 200, 200, this.title);
        }
    }
    
    public void roleMatrix(final Village village) {
        final VillageRole[] roles = village.getRoles();
        Arrays.sort(roles);
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getBmlHeaderWithScroll());
        if (village.isDisbanding()) {
            final long timeleft = village.getDisbanding() - System.currentTimeMillis();
            final String times = Server.getTimeFor(timeleft);
            buf.append("text{type=\"bold\";text=\"This settlement is disbanding\"}");
            if (timeleft > 0L) {
                buf.append("text{type=\"bold\";text=\"Eta: " + times + ".\"};text{text=\"\"};");
            }
            else {
                buf.append("text{type=\"bold\";text=\"Eta:  any minute now.\"};text{text=\"\"};");
            }
        }
        buf.append("text{type=\"bold\";text=\"Set the permissions for the different roles.\"}");
        buf.append("text{text=\"If the checkbox is checked, it means the role WILL BE able to perform the task.\"}");
        if (Servers.isThisAPvpServer()) {
            buf.append("text{type=\"bold\";text=\"Note that you will need at least 1 guard to enforce these rules on deed!\"}");
        }
        buf.append("text{text=\"If you want to change the name, simply type another name there.\"}text{text=\" \"}");
        buf.append("text{type=\"bold\";text=\"Note! The role name may contain the following letters: \"}");
        buf.append("text{text=\"a-z,A-Z,', and -\"}text{text=\"\"}");
        buf.append("text{text=\"The non-citizens role means those who are not citizens. Normally they shouldn't be able to do anything at all.\"}");
        buf.append("text{text=\"The citizen role is the default role people get when they become citizens.\"}");
        buf.append("checkbox{id=\"resetall\";selected=\"false\";text=\"Check this box to reset all roles. Everyone except the mayor will have the Citizen role. Any other changes made below will be discarded.\"};text{text=\"\"}");
        boolean hasGuardRole = false;
        for (int x = 0; x < roles.length; ++x) {
            if (roles[x].getStatus() == 2) {
                buf.append("harray{label{text=\"Change mayor role name here \"};input{maxchars=\"20\";id=\"" + roles[x].getId() + "name\"; text=\"" + roles[x].getName() + "\"}}");
            }
            if (roles[x].getStatus() == 4) {
                hasGuardRole = true;
            }
        }
        if (roles.length < 50) {
            buf.append("text{text=\"If you want to create a new role, just change the name 'newrole' and set the appropriate values.\"}");
        }
        else {
            buf.append("text{text=\"You have reached the maximum number of roles for your settlement, which is 50. Delete one if you need more.\"}");
        }
        buf.append("text{text=\"\"}");
        final int cols = roles.length + ((roles.length < 50) ? 1 : 0) + (hasGuardRole ? 0 : 1);
        buf.append("table{rows=\"21\";cols=\"" + cols + "\";");
        buf.append("label{maxchars=\"20\";text=\"Role name\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("harray{input{maxchars=\"20\";id=\"" + roles[x2].getId() + "name\"; text=\"" + roles[x2].getName() + "\"};label{text=\" \"}}");
            }
        }
        if (roles.length < 50) {
            buf.append("input{maxchars=\"20\";id=\"newname\";text=\"newrole\"}");
        }
        buf.append("label{text=\"Name of the role\"}");
        buf.append("label{text=\"Build\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "build\"" + (roles[x2].mayBuild() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newbuild\"}");
        }
        buf.append("label{text=\"Allows ability to plan, color and continue on buildings and fences.\"}");
        buf.append("label{text=\"Forestry\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "cut\"" + (roles[x2].mayCuttrees() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newcut\"}");
        }
        buf.append("label{text=\"Allows cutting down young trees, picking sprouts and planting new trees.\"}");
        buf.append("label{text=\"Cut old trees\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "cutold\"" + (roles[x2].mayCutOldTrees() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newcutold\"}");
        }
        buf.append("label{text=\"Allows cutting down old trees.\"}");
        buf.append("label{text=\"Destroy buildings\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "dest\"" + (roles[x2].mayDestroy() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newdest\"}");
        }
        buf.append("label{text=\"Allows destroying of building and fence plans as well as finished buildings and plans. Also allows lockpicking of structures and removing lamp posts and signs.\"}");
        buf.append("label{text=\"Farm\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "farm\"" + (roles[x2].mayFarm() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newfarm\"}");
        }
        buf.append("label{text=\"Allows sowing, farming and harvesting farms. Also milking creatures.\"}");
        buf.append("label{text=\"Hire guards\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "hire\"" + (roles[x2].mayHire() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newhire\"}");
        }
        buf.append("label{text=\"Allows hiring guards to protect the settlement and uphold these rules.\"}");
        buf.append("label{text=\"Citizens\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "inv\"" + (roles[x2].mayInviteCitizens() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newinv\"}");
        }
        buf.append("label{text=\"Allows ability to invite non-citizens to join the settlement.\"}");
        buf.append("label{text=\"Manage roles\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "man\"" + (roles[x2].mayManageRoles() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newman\"}");
        }
        buf.append("label{text=\"Allows adding to and modifying these roles via this interface, as well as changing settings and setting reputations and disbanding the settlement.\"}");
        buf.append("label{text=\"Mine\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "mine\"" + (roles[x2].mayMine() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newmine\"}");
        }
        buf.append("label{text=\"Allows mining in general.\"}");
        buf.append("label{text=\"Terraform\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "terra\"" + (roles[x2].mayTerraform() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newterra\"}");
        }
        buf.append("label{text=\"Allows ability to dig, pack, pave, level and flatten.\"}");
        buf.append("label{text=\"Expand\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "expa\"" + (roles[x2].mayExpand() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newexpa\"}");
        }
        buf.append("label{text=\"Allows ability to expand the size of the settlement.\"}");
        buf.append("label{text=\"Pass gates\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "pass\"" + ";selected='true'" + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newpass\"}");
        }
        buf.append("label{text=\"Allows passing through all gates, locked or not.\"}");
        buf.append("label{text=\"Lock gates\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "lock\"" + (roles[x2].mayLockFences() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newlock\"}");
        }
        buf.append("label{text=\"Allows ability to unlock and relock gates inside the settlement.\"}");
        buf.append("label{text=\"Attack citizens\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "attcitiz\"" + (roles[x2].mayAttackCitizens() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newattcitiz\"}");
        }
        buf.append("label{text=\"Allows ability to attack citizens and allies of the settlement.\"}");
        buf.append("label{text=\"Attack non-citizens\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "attnoncitiz\"" + (roles[x2].mayAttackNonCitizens() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newattnoncitiz\"}");
        }
        buf.append("label{text=\"Allows ability to attack non citizens.\"}");
        buf.append("label{text=\"Fish\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "fish\"" + (roles[x2].mayFish() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newfish\"}");
        }
        buf.append("label{text=\"Allows ability to fish on deed.\"}");
        buf.append("label{text=\"Push,Pull,Turn\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "pushpullturn\"" + (roles[x2].mayPushPullTurn() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newpushpullturn\"}");
        }
        buf.append("label{text=\"Allows ability to push, pull, turn items. Note that pull and push may require pickup permission as well\"}");
        buf.append("label{text=\"Diplomat\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                if (roles[x2].getStatus() != 1) {
                    buf.append("checkbox{id=\"" + roles[x2].getId() + "diplomat\"" + (roles[x2].isDiplomat() ? ";selected='true'" : "") + "}");
                }
                else {
                    buf.append("label{text=\"N/A\"}");
                }
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newdiplomat\"}");
        }
        buf.append("label{text=\"Allows ability to form and break alliances with other settlements, as well as declare war and make peace.\"}");
        buf.append("label{text=\"Map\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                if (roles[x2].getStatus() == 3 || roles[x2].getStatus() == 0) {
                    buf.append("checkbox{id=\"" + roles[x2].getId() + "map\"" + (roles[x2].mayUpdateMap() ? ";selected='true'" : "") + "}");
                }
                else {
                    buf.append("label{text=\"N/A\"}");
                }
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newmayupdatemap\"}");
        }
        buf.append("label{text=\"Allows ability to add and remove village annotations on the map.\"}");
        buf.append("label{text=\"Lead\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "lead\"" + (roles[x2].mayLead() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newmayLead\"}");
        }
        buf.append("label{text=\"Allows leading creatures.\"}");
        buf.append("label{text=\"Pickup\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "pickup\"" + (roles[x2].mayPickup() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newmayPickup\"}");
        }
        buf.append("label{text=\"Allows ability to pickup item on deed.\"}");
        buf.append("label{text=\"Tame\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "tame\"" + (roles[x2].mayTame() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newmayTame\"}");
        }
        buf.append("label{text=\"Allows ability to Tame on deed.\"}");
        buf.append("label{text=\"Load\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "load\"" + (roles[x2].mayLoad() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newmayLoad\"}");
        }
        buf.append("label{text=\"Allows ability to Load on deed (needs Pickup as well).\"}");
        buf.append("label{text=\"Butcher\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "butcher\"" + (roles[x2].mayButcher() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newmayButcher\"}");
        }
        buf.append("label{text=\"Allows ability to Butcher on deed.\"}");
        buf.append("label{text=\"Attach Lock\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "attachLock\"" + (roles[x2].mayAttachLock() ? ";selected='true'" : "") + "}");
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newmayAttachLock\"}");
        }
        buf.append("label{text=\"Allows ability to Attach Locks on deed.\"}");
        buf.append("label{text=\"Pick Locks\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                if (roles[x2].getStatus() != 1) {
                    buf.append("checkbox{id=\"" + roles[x2].getId() + "pickLocks\"" + (roles[x2].mayPickLocks() ? ";selected='true'" : "") + "}");
                }
                else {
                    buf.append("label{text=\"N/A\"}");
                }
            }
        }
        if (roles.length < 50) {
            buf.append("checkbox{id=\"newmayPickLocks\"}");
        }
        buf.append("label{text=\"Allows lockpicking of structures.\"}");
        buf.append("label{text=\"Settlement\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() == 0) {
                final int villId = roles[x2].getVillageAppliedTo();
                try {
                    final Village villapplied = Villages.getVillage(villId);
                    buf.append("harray{input{maxchars=\"40\";id=\"" + roles[x2].getId() + "Applied\"; text=\"" + villapplied.getName() + "\"};label{text=\" \"}}");
                }
                catch (NoSuchVillageException nsv) {
                    buf.append("label{text=\"N/A\"}");
                }
            }
            else if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("label{text=\"N/A\"}");
            }
        }
        if (roles.length < 50) {
            buf.append("input{maxchars=\"40\";id=\"newappliedTo\";text=\"\"}");
        }
        buf.append("label{text=\"Make the role apply to a certain settlement only. Useful in alliances.\"}");
        buf.append("label{text=\"DELETE\"};");
        for (int x2 = 0; x2 < roles.length; ++x2) {
            if (roles[x2].getStatus() == 0) {
                buf.append("checkbox{id=\"" + roles[x2].getId() + "delete\"selected=\"false\"}");
            }
            else if (roles[x2].getStatus() != 2 && roles[x2].getStatus() != 4) {
                buf.append("label{text=\"N/A\"}");
            }
        }
        if (roles.length < 50) {
            buf.append("label{text=\" \"}");
        }
        buf.append("label{text=\"If you check that box the role will disappear.\"}");
        buf.append("}");
        buf.append(this.createAnswerButton3());
        this.getResponder().getCommunicator().sendBml(700, 400, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    public Village getVillage() {
        try {
            Village village = null;
            if (this.gmBack.length() > 0) {
                try {
                    village = Villages.getVillage((int)this.target);
                }
                catch (NoSuchVillageException e) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("Cannot find the village ... most odd!.");
                }
                return village;
            }
            if (this.target == -10L) {
                village = this.getResponder().getCitizenVillage();
                if (village == null) {
                    this.getResponder().getCommunicator().sendNormalServerMessage("You are not a citizen of any village (on this server).");
                }
                return village;
            }
            final Item deed = Items.getItem(this.target);
            final int villageId = deed.getData2();
            return Villages.getVillage(villageId);
        }
        catch (NoSuchItemException nsi) {
            VillageRolesManageQuestion.logger.log(Level.WARNING, "Failed to locate village deed with id " + this.target, nsi);
            this.getResponder().getCommunicator().sendNormalServerMessage("Failed to locate the deed item for that request. Please contact administration.");
            return null;
        }
        catch (NoSuchVillageException nsv) {
            VillageRolesManageQuestion.logger.log(Level.WARNING, "Failed to locate village for deed with id " + this.target, nsv);
            this.getResponder().getCommunicator().sendNormalServerMessage("Failed to locate the village for that request. Please contact administration.");
            return null;
        }
    }
    
    static {
        logger = Logger.getLogger(VillageRolesManageQuestion.class.getName());
    }
}
