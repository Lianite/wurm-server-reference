// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.wurmonline.server.villages.Citizen;
import com.wurmonline.server.villages.Village;
import java.util.List;
import com.wurmonline.server.players.Friend;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.Color;
import com.wurmonline.server.utils.BMLBuilder;
import com.wurmonline.server.players.Permissions;
import com.wurmonline.server.players.PermissionsByPlayer;
import com.wurmonline.server.GeneralUtilities;
import com.wurmonline.server.items.ItemSettings;
import java.util.BitSet;
import com.wurmonline.server.structures.StructureSettings;
import java.io.IOException;
import java.util.logging.Level;
import com.wurmonline.server.players.PermissionsHistories;
import java.util.LinkedList;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.LoginHandler;
import java.util.Properties;
import edu.umd.cs.findbugs.annotations.Nullable;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.PermissionsPlayerList;
import com.wurmonline.server.players.Player;
import java.util.logging.Logger;

public class ManagePermissions extends Question
{
    private static final Logger logger;
    private static final String legalNameChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String legalDescriptionChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'- 1234567890.,+/!()";
    private Player player;
    private ManageObjectList.Type objectType;
    private PermissionsPlayerList.ISettings object;
    private boolean hasBackButton;
    private boolean parentHasBack;
    private ManageObjectList.Type parentType;
    private String error;
    private boolean hadManage;
    
    public ManagePermissions(final Creature aResponder, final ManageObjectList.Type aObjectType, final PermissionsPlayerList.ISettings anObject, final boolean canGoBack, final long parent, final boolean parentCanGoBack, @Nullable final ManageObjectList.Type aParentType, final String errorText) {
        super(aResponder, "Managing " + anObject.getObjectName(), "Manage Permissions", 119, parent);
        this.hadManage = true;
        this.player = (Player)aResponder;
        this.objectType = aObjectType;
        this.object = anObject;
        this.hasBackButton = canGoBack;
        this.parentHasBack = parentCanGoBack;
        this.parentType = aParentType;
        this.error = errorText;
    }
    
    @Override
    public void answer(final Properties aAnswers) {
        this.setAnswer(aAnswers);
        final String sback = aAnswers.getProperty("back");
        if (sback != null && sback.equals("true")) {
            final ManageObjectList mol = new ManageObjectList(this.player, this.parentType, this.target, this.parentHasBack, 1, "", true);
            mol.sendQuestion();
            return;
        }
        final String sclose = aAnswers.getProperty("close");
        if (sclose != null && sclose.equals("true")) {
            return;
        }
        if (this.object.isItem() && this.object.getTemplateId() == 272) {
            final ManagePermissions mp = new ManagePermissions(this.player, this.objectType, this.object, this.hasBackButton, this.target, this.parentHasBack, this.parentType, "Cannot modify corpse permissions.");
            mp.sendQuestion();
            return;
        }
        final boolean canChangeName = this.object.canChangeName(this.player);
        final String newObjectName = aAnswers.getProperty("object");
        final String newOwnerName = aAnswers.getProperty("owner");
        final String newOwner = (newOwnerName != null) ? LoginHandler.raiseFirstLetter(newOwnerName) : "";
        final boolean manage = Boolean.parseBoolean(aAnswers.getProperty("manage"));
        boolean manageChanged = false;
        if (canChangeName && !newObjectName.equalsIgnoreCase(this.object.getObjectName()) && containsIllegalCharacters("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'- 1234567890.,+/!()", newObjectName)) {
            final ManagePermissions mp2 = new ManagePermissions(this.player, this.objectType, this.object, this.hasBackButton, this.target, this.parentHasBack, this.parentType, "Illegal new object name '" + newObjectName + "'.");
            mp2.sendQuestion();
            return;
        }
        long ownerId = -10L;
        if (newOwner.length() != 0 && !newOwner.equalsIgnoreCase(this.object.getOwnerName())) {
            if (containsIllegalCharacters("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", newOwner)) {
                final String msg = "Illegal new owners name '" + newOwner + "'.";
                if (this.object.mayShowPermissions(this.player)) {
                    final ManagePermissions mp3 = new ManagePermissions(this.player, this.objectType, this.object, this.hasBackButton, this.target, this.parentHasBack, this.parentType, msg);
                    mp3.sendQuestion();
                }
                else {
                    this.player.getCommunicator().sendNormalServerMessage(msg);
                }
                return;
            }
            if (!this.object.canChangeOwner(this.getResponder())) {
                final String msg = "Not allowed to change owner.";
                if (this.object.mayShowPermissions(this.player)) {
                    final ManagePermissions mp3 = new ManagePermissions(this.player, this.objectType, this.object, this.hasBackButton, this.target, this.parentHasBack, this.parentType, "Not allowed to change owner.");
                    mp3.sendQuestion();
                }
                else {
                    this.player.getCommunicator().sendNormalServerMessage("Not allowed to change owner.");
                }
                return;
            }
            ownerId = PlayerInfoFactory.getWurmId(newOwnerName);
            if (ownerId == -10L) {
                final String msg = "Cannot find new owner '" + newOwnerName + "'.";
                if (this.object.mayShowPermissions(this.player)) {
                    final ManagePermissions mp3 = new ManagePermissions(this.player, this.objectType, this.object, this.hasBackButton, this.target, this.parentHasBack, this.parentType, msg);
                    mp3.sendQuestion();
                }
                else {
                    this.player.getCommunicator().sendNormalServerMessage(msg);
                }
                return;
            }
        }
        final LinkedList<String> changes = new LinkedList<String>();
        final LinkedList<String> removed = new LinkedList<String>();
        final LinkedList<String> added = new LinkedList<String>();
        final LinkedList<String> updated = new LinkedList<String>();
        if (!this.object.mayShowPermissions(this.player)) {
            if (newObjectName != null && !newObjectName.equalsIgnoreCase(this.object.getObjectName())) {
                if (!this.object.setObjectName(newObjectName, this.player)) {
                    this.player.getCommunicator().sendNormalServerMessage("Problem changing name.");
                    return;
                }
                changes.add("Name");
            }
            if (ownerId != -10L) {
                if (!this.object.setNewOwner(ownerId)) {
                    if (!changes.isEmpty()) {
                        PermissionsHistories.addHistoryEntry(this.object.getWurmId(), System.currentTimeMillis(), this.player.getWurmId(), this.player.getName(), "Changed " + changes.getFirst());
                    }
                    this.player.getCommunicator().sendNormalServerMessage("Problem changing name.");
                    return;
                }
                changes.add("Owner to '" + newOwner + "'");
            }
            if (this.objectType == ManageObjectList.Type.DOOR && ownerId == -10L && this.object.getWarning().length() == 0 && this.object.isManaged() != manage) {
                this.object.setIsManaged(manage, this.player);
                manageChanged = true;
            }
            try {
                this.object.save();
            }
            catch (IOException e) {
                ManagePermissions.logger.log(Level.WARNING, e.getMessage(), e);
            }
            if (!changes.isEmpty() || manageChanged) {
                final StringBuilder buf = new StringBuilder();
                if (!changes.isEmpty()) {
                    buf.append("Changed " + String.join(", ", changes));
                }
                if (manageChanged) {
                    if (buf.length() > 0) {
                        buf.append(", ");
                    }
                    if (manage) {
                        buf.append("Ticked Controlled by Flag");
                    }
                    else {
                        buf.append("UnTicked Controlled by Flag");
                    }
                }
                PermissionsHistories.addHistoryEntry(this.object.getWurmId(), System.currentTimeMillis(), this.player.getWurmId(), this.player.getName(), buf.toString());
                this.player.getCommunicator().sendNormalServerMessage("You " + buf.toString());
                final ManagePermissions mp4 = new ManagePermissions(this.player, this.objectType, this.object, this.hasBackButton, this.target, this.parentHasBack, this.parentType, "");
                mp4.sendQuestion();
            }
            return;
        }
        final int rows = Integer.parseInt(aAnswers.getProperty("rows"));
        if (rows > this.object.getMaxAllowed()) {
            final ManagePermissions mp4 = new ManagePermissions(this.player, this.objectType, this.object, this.hasBackButton, this.target, this.parentHasBack, this.parentType, "Too many allowed entries, Max is " + this.object.getMaxAllowed() + ".");
            mp4.sendQuestion();
            return;
        }
        final PermissionsPlayerList ppl = this.object.getPermissionsPlayerList();
        final long[] pIds = new long[rows];
        final int[] settings = new int[rows];
        final int excludeBit = StructureSettings.StructurePermissions.EXCLUDE.getBit();
        if (rows > 0) {
            final int cols = Integer.parseInt(aAnswers.getProperty("cols"));
            final Permissions.IPermission[] values = this.objectType.getEnumValues();
            final int[] bits = new int[cols];
            for (int x = 0; x < cols; ++x) {
                bits[x] = values[x].getBit();
            }
            for (int row = 0; row < rows; ++row) {
                pIds[row] = Long.parseLong(aAnswers.getProperty("r" + row));
                final BitSet bitset = new BitSet(32);
                boolean otherThanExclude = false;
                for (int col = 0; col < cols; ++col) {
                    final boolean flag = Boolean.parseBoolean(aAnswers.getProperty("r" + row + "c" + col));
                    bitset.set(bits[col], flag);
                    if (bits[col] != excludeBit && flag) {
                        otherThanExclude = true;
                    }
                }
                boolean wasExcluded = false;
                if (ppl.exists(pIds[row])) {
                    wasExcluded = ppl.hasPermission(pIds[row], excludeBit);
                }
                if (wasExcluded && otherThanExclude) {
                    bitset.clear(excludeBit);
                }
                else if (bitset.get(excludeBit)) {
                    bitset.clear();
                    bitset.set(excludeBit);
                }
                if (pIds[row] == -30L || pIds[row] == -20L || pIds[row] == -40L || pIds[row] == -50L) {
                    if (bitset.get(StructureSettings.StructurePermissions.MANAGE.getBit())) {
                        bitset.clear(StructureSettings.StructurePermissions.MANAGE.getBit());
                    }
                }
                else {
                    if (bitset.get(StructureSettings.StructurePermissions.MANAGE.getBit())) {
                        for (int x2 = 1; x2 < cols; ++x2) {
                            if (bits[x2] != excludeBit) {
                                bitset.set(bits[x2], true);
                            }
                        }
                    }
                    if (this.object.isItem() && this.object.getTemplateId() == 1271) {
                        if (bitset.get(ItemSettings.MessageBoardPermissions.MANAGE_NOTICES.getBit())) {
                            for (int x2 = 2; x2 < cols; ++x2) {
                                if (bits[x2] != excludeBit) {
                                    bitset.set(bits[x2], true);
                                }
                            }
                        }
                        else if (bitset.get(ItemSettings.MessageBoardPermissions.MAY_POST_NOTICES.getBit())) {
                            bitset.set(ItemSettings.MessageBoardPermissions.ACCESS_HOLD.getBit(), true);
                        }
                        else if (bitset.get(ItemSettings.MessageBoardPermissions.MAY_ADD_PMS.getBit())) {
                            bitset.set(ItemSettings.MessageBoardPermissions.ACCESS_HOLD.getBit(), true);
                        }
                    }
                }
                settings[row] = GeneralUtilities.getIntSettingsFrom(bitset);
            }
        }
        if (canChangeName && !newObjectName.equalsIgnoreCase(this.object.getObjectName())) {
            if (!this.object.setObjectName(newObjectName, this.player)) {
                final ManagePermissions mp5 = new ManagePermissions(this.player, this.objectType, this.object, this.hasBackButton, this.target, this.parentHasBack, this.parentType, "Problem changing name.");
                mp5.sendQuestion();
                return;
            }
            changes.add("Name");
        }
        if (ownerId != -10L) {
            if (!this.object.setNewOwner(ownerId)) {
                if (!changes.isEmpty()) {
                    PermissionsHistories.addHistoryEntry(this.object.getWurmId(), System.currentTimeMillis(), this.player.getWurmId(), this.player.getName(), "Changed " + changes.getFirst());
                }
                final ManagePermissions mp5 = new ManagePermissions(this.player, this.objectType, this.object, this.hasBackButton, this.target, this.parentHasBack, this.parentType, "Problem changing owner.");
                mp5.sendQuestion();
                return;
            }
            changes.add("Owner to '" + newOwner + "'");
        }
        if (ownerId == -10L && this.object.isManaged() != manage) {
            this.object.setIsManaged(manage, this.player);
            manageChanged = true;
        }
        try {
            this.object.save();
        }
        catch (IOException e2) {
            ManagePermissions.logger.log(Level.WARNING, e2.getMessage(), e2);
        }
        if (ownerId == -10L && !manageChanged) {
            for (final PermissionsByPlayer pbp : ppl.getPermissionsByPlayer()) {
                boolean found = false;
                for (final long pId : pIds) {
                    if (pbp.getPlayerId() == pId) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    removed.add(pbp.getName());
                    this.object.removeGuest(pbp.getPlayerId());
                }
            }
            final Permissions.IPermission[] values2 = this.objectType.getEnumValues();
            final String[] title = new String[values2.length];
            final int[] bits = new int[values2.length];
            for (int x = 0; x < values2.length; ++x) {
                title[x] = values2[x].getHeader1() + " " + values2[x].getHeader2();
                bits[x] = values2[x].getBit();
            }
            for (int x = 0; x < pIds.length; ++x) {
                int oldSettings = 0;
                if (ppl.exists(pIds[x])) {
                    oldSettings = ppl.getPermissionsFor(pIds[x]).getPermissions();
                }
                final LinkedList<String> perms = new LinkedList<String>();
                for (int y = 0; y < 32; ++y) {
                    final boolean oldBit = (oldSettings >>> y & 0x1) == 0x1;
                    final boolean newBit = (settings[x] >>> y & 0x1) == 0x1;
                    if (oldBit != newBit) {
                        int bit = -1;
                        for (int j = 0; j < values2.length; ++j) {
                            if (bits[j] == y) {
                                bit = j;
                                break;
                            }
                        }
                        if (bit != -1) {
                            if (oldBit) {
                                perms.add("-" + title[bit]);
                            }
                            else {
                                perms.add("+" + title[bit]);
                            }
                        }
                        else if (oldBit) {
                            perms.add("-Bad Bit");
                        }
                        else {
                            perms.add("+Bad Bit");
                        }
                    }
                }
                final String fields = "(" + String.join(", ", perms) + ")";
                if (ppl.exists(pIds[x])) {
                    if (oldSettings != settings[x]) {
                        this.object.addGuest(pIds[x], settings[x]);
                        updated.add(PermissionsByPlayer.getPlayerOrGroupName(pIds[x]) + fields);
                    }
                }
                else if (settings[x] != 0) {
                    this.object.addGuest(pIds[x], settings[x]);
                    added.add(PermissionsByPlayer.getPlayerOrGroupName(pIds[x]) + fields);
                }
            }
        }
        if (this.hadManage && !this.object.mayShowPermissions(this.player)) {
            this.player.getCommunicator().sendHidePermissions();
        }
        final StringBuilder buf2 = new StringBuilder();
        if (!changes.isEmpty()) {
            buf2.append("Changed " + String.join(", ", changes));
        }
        if (manageChanged) {
            if (buf2.length() > 0) {
                buf2.append(", ");
            }
            if (this.objectType == ManageObjectList.Type.DOOR) {
                if (manage) {
                    buf2.append("Ticked Controlled by Flag");
                }
                else {
                    buf2.append("UnTicked Controlled by Flag");
                }
            }
            else if (manage) {
                buf2.append("Ticked Manage Flag");
            }
            else {
                buf2.append("UnTicked Manage Flag");
            }
        }
        if (!added.isEmpty()) {
            if (buf2.length() > 0) {
                buf2.append(", ");
            }
            buf2.append("Added " + String.join(", ", added));
        }
        if (!removed.isEmpty()) {
            if (buf2.length() > 0) {
                buf2.append(", ");
            }
            buf2.append("Removed " + String.join(", ", removed));
        }
        if (!updated.isEmpty()) {
            if (buf2.length() > 0) {
                buf2.append(", ");
            }
            buf2.append("Updated " + String.join(", ", updated));
        }
        if (buf2.length() > 0) {
            String historyevent = buf2.toString();
            if (buf2.length() > 255) {
                historyevent = historyevent.substring(0, 250) + " ...";
            }
            PermissionsHistories.addHistoryEntry(this.object.getWurmId(), System.currentTimeMillis(), this.player.getWurmId(), this.player.getName(), historyevent);
            this.player.getCommunicator().sendNormalServerMessage("You " + historyevent);
        }
        final ManagePermissions mp6 = new ManagePermissions(this.player, this.objectType, this.object, this.hasBackButton, this.target, this.parentHasBack, this.parentType, "");
        mp6.sendQuestion();
    }
    
    @Override
    public void sendQuestion() {
        final String objectName = this.object.getObjectName();
        final boolean canChangeName = this.object.canChangeName(this.player);
        final String ownerName = (this.player.getPower() > 1) ? this.object.getOwnerName() : "";
        final boolean canChangeOwner = this.object.canChangeOwner(this.player);
        final String warningText = this.object.getWarning();
        final boolean isManaged = this.object.isManaged();
        final boolean isManageEnabled = this.object.isManageEnabled(this.player);
        final String mayManageText = this.object.mayManageText(this.player);
        final String mayManageHover = this.object.mayManageHover(this.player);
        final String messageOnTick = this.object.messageOnTick();
        final String questionOnTick = this.object.questionOnTick();
        final String messageUnTick = this.object.messageUnTick();
        final String questionUnTick = this.object.questionUnTick();
        if (!this.object.mayShowPermissions(this.player)) {
            if (canChangeOwner || canChangeName || (isManageEnabled && this.objectType == ManageObjectList.Type.DOOR)) {
                this.hadManage = false;
                final String oldOwnerName = (this.player.getPower() > 1) ? ("(" + this.object.getOwnerName() + ") ") : "";
                String door = "";
                if (this.objectType == ManageObjectList.Type.DOOR) {
                    door = "checkbox{id=\"manage\";text=\"" + mayManageText + "\";hover=\"" + mayManageHover + "\";confirm=\"" + messageOnTick + "\";question=\"" + questionOnTick + "\";unconfirm=\"" + messageUnTick + "\";unquestion=\"" + questionUnTick + "\";selected=\"" + isManaged + "\"}";
                }
                final StringBuilder buf = new StringBuilder();
                buf.append("border{border{size=\"20,20\";null;null;label{type='bold';text=\"" + this.question + "\"};harray{" + (this.hasBackButton ? "button{text=\"Back\";id=\"back\"};label{text=\" \"};" : "") + "button{text=\"Close\";id=\"close\"};label{text=\" \"}};null;};harray{passthrough{id=\"id\";text=\"" + this.getId() + "\"};label{text=\"\"}};varray{label{text=\"You are not allowed to manage permissions.\"};harray{label{text=\"Name:\"};" + (canChangeName ? ("input{id=\"object\";text=\"" + objectName + "\"};") : ("label{text=\"" + objectName + "\"};")) + "}" + (canChangeOwner ? ("harray{label{text=\"Change owner " + oldOwnerName + "to \"};input{id=\"owner\";text=\"\"}};") : ((this.objectType == ManageObjectList.Type.DOOR && this.object.getWarning().length() == 0) ? door : "label{text=\" \"};")) + "};varray{label{text=\"\"};label{text=\"\"};harray{button{text=\"Apply Changes\";id=\"save\"};label{text=\" \"}}};" + ((warningText.length() > 0) ? ("center{label{type=\"bold\";color=\"240,40,40\";text=\"" + warningText + "\"}};") : "null;") + "}");
                final BMLBuilder completePanel = BMLBuilder.createBMLBorderPanel(BMLBuilder.createBMLBorderPanel(null, null, BMLBuilder.createGenericBuilder().addLabel(this.question, null, BMLBuilder.TextType.BOLD, null), BMLBuilder.createHorizArrayNode(false).addString(this.hasBackButton ? BMLBuilder.createButton("back", "Back") : BMLBuilder.createLabel(" ")).addButton("close", "Close").addLabel(" "), null, 20, 20), BMLBuilder.createHorizArrayNode(false).addPassthrough("id", Integer.toString(this.getId())).addLabel(""), BMLBuilder.createVertArrayNode(false).addLabel("You are not allowed to manage permissions.").addString(BMLBuilder.createHorizArrayNode(false).addLabel("Name:").addString(canChangeName ? BMLBuilder.createInput("object", objectName) : BMLBuilder.createLabel(objectName)).toString()).addString(canChangeOwner ? BMLBuilder.createHorizArrayNode(false).addLabel("Change owner " + oldOwnerName + "to ").addInput("owner", "", 0, 0).toString() : ((this.objectType == ManageObjectList.Type.DOOR && this.object.getWarning().length() == 0) ? BMLBuilder.createGenericBuilder().addCheckbox("manage", mayManageText, questionOnTick, messageOnTick, questionUnTick, messageUnTick, mayManageHover, isManaged, true, null).toString() : BMLBuilder.createLabel(" "))), BMLBuilder.createVertArrayNode(false).addLabel("").addLabel("").addString(BMLBuilder.createHorizArrayNode(false).addButton("save", "Apply Changes").addLabel(" ").toString()), (warningText.length() > 0) ? BMLBuilder.createCenteredNode(BMLBuilder.createGenericBuilder().addLabel(warningText, null, BMLBuilder.TextType.BOLD, Color.RED)) : null);
                final int ht = (warningText.length() > 0) ? 150 : 125;
                this.getResponder().getCommunicator().sendBml(320, ht, true, true, buf.toString(), 200, 200, 200, this.objectType.getTitle() + " - Manage Permissions");
            }
        }
        else {
            if (this.error.length() > 0) {
                this.player.getCommunicator().sendPermissionsApplyChangesFailed(this.getId(), this.error);
                return;
            }
            final String mySettlement = (this.player.getCitizenVillage() != null) ? "Citizens of my deed" : "";
            final String allowAlliesText = this.object.getAllianceName();
            final String allowCitizensText = this.object.getSettlementName();
            final String allowKingdomText = this.object.getKingdomName();
            final String allowEveryoneText = this.object.canAllowEveryone() ? "\"Everyone\"" : "";
            final String allowRolePermissionText = this.object.getRolePermissionName();
            final Permissions.IPermission[] values = this.objectType.getEnumValues();
            final String[] header1 = new String[values.length];
            final String[] header2 = new String[values.length];
            final String[] hover = new String[values.length];
            final int[] bits = new int[values.length];
            for (int x = 0; x < values.length; ++x) {
                header1[x] = values[x].getHeader1();
                header2[x] = values[x].getHeader2();
                hover[x] = values[x].getHover();
                bits[x] = values[x].getBit();
            }
            final PermissionsPlayerList allowedList = this.object.getPermissionsPlayerList();
            final String[] permittedNames = new String[allowedList.size()];
            final long[] permittedIds = new long[allowedList.size()];
            final boolean[][] allowed = new boolean[allowedList.size()][header1.length];
            int count = 0;
            final PermissionsByPlayer[] pbpList = allowedList.getPermissionsByPlayer();
            Arrays.sort(pbpList);
            for (final PermissionsByPlayer pbp : pbpList) {
                final long playerId = pbp.getPlayerId();
                permittedNames[count] = pbp.getName();
                permittedIds[count] = playerId;
                for (int bit = 0; bit < bits.length; ++bit) {
                    allowed[count][bit] = pbp.hasPermission(bits[bit]);
                }
                ++count;
            }
            final Friend[] friendsList = this.player.getFriends();
            Arrays.sort(friendsList);
            final List<Long> trusted = new ArrayList<Long>();
            final List<Long> friends = new ArrayList<Long>();
            final List<Long> citizens = new ArrayList<Long>();
            for (count = 0; count < friendsList.length; ++count) {
                if (friendsList[count].getCategory().getCatId() == Friend.Category.Trusted.getCatId() && !allowedList.exists(friendsList[count].getFriendId())) {
                    trusted.add(friendsList[count].getFriendId());
                }
                if (friendsList[count].getCategory().getCatId() == Friend.Category.Friends.getCatId() && !allowedList.exists(friendsList[count].getFriendId())) {
                    friends.add(friendsList[count].getFriendId());
                }
            }
            final playerIdName[] trustedIdNames = new playerIdName[trusted.size()];
            for (count = 0; count < trusted.size(); ++count) {
                trustedIdNames[count] = new playerIdName(trusted.get(count));
            }
            Arrays.sort(trustedIdNames);
            final long[] trustedIds = new long[trusted.size()];
            final String[] trustedNames = new String[trusted.size()];
            for (count = 0; count < trusted.size(); ++count) {
                trustedIds[count] = trustedIdNames[count].getWurmId();
                trustedNames[count] = trustedIdNames[count].getName();
            }
            final playerIdName[] friendIdNames = new playerIdName[friends.size()];
            for (count = 0; count < friends.size(); ++count) {
                friendIdNames[count] = new playerIdName(friends.get(count));
            }
            Arrays.sort(friendIdNames);
            final long[] friendIds = new long[friends.size()];
            final String[] friendNames = new String[friends.size()];
            for (count = 0; count < friends.size(); ++count) {
                friendIds[count] = friendIdNames[count].getWurmId();
                friendNames[count] = friendIdNames[count].getName();
            }
            final Village village = this.player.getCitizenVillage();
            if (village != null) {
                for (final Citizen c : village.getCitizens()) {
                    if (!allowedList.exists(c.wurmId) && c.isPlayer()) {
                        citizens.add(c.wurmId);
                    }
                }
            }
            final playerIdName[] citizenIdNames = new playerIdName[citizens.size()];
            for (count = 0; count < citizens.size(); ++count) {
                citizenIdNames[count] = new playerIdName(citizens.get(count));
            }
            Arrays.sort(citizenIdNames);
            final long[] citizenIds = new long[citizens.size()];
            final String[] citizenNames = new String[citizens.size()];
            for (count = 0; count < citizens.size(); ++count) {
                citizenIds[count] = citizenIdNames[count].getWurmId();
                citizenNames[count] = citizenIdNames[count].getName();
            }
            this.player.getCommunicator().sendShowPermissions(this.getId(), this.hasBackButton, this.objectType.getTitle(), objectName, ownerName, canChangeName, canChangeOwner, isManaged, isManageEnabled, mayManageText, mayManageHover, warningText, messageOnTick, questionOnTick, messageUnTick, questionUnTick, allowAlliesText, allowCitizensText, allowKingdomText, allowEveryoneText, allowRolePermissionText, header1, header2, hover, trustedNames, trustedIds, friendNames, friendIds, mySettlement, citizenNames, citizenIds, permittedNames, permittedIds, allowed);
        }
    }
    
    public static final boolean containsIllegalCharacters(final String legalChars, final String name) {
        final char[] charArray;
        final char[] chars = charArray = name.toCharArray();
        for (final char lC : charArray) {
            if (legalChars.indexOf(lC) < 0) {
                return true;
            }
        }
        return false;
    }
    
    static {
        logger = Logger.getLogger(ManagePermissions.class.getName());
    }
    
    private class playerIdName implements Comparable<playerIdName>
    {
        final long id;
        final String name;
        
        playerIdName(final long aId) {
            this.id = aId;
            this.name = PermissionsByPlayer.getPlayerOrGroupName(this.id);
        }
        
        public long getWurmId() {
            return this.id;
        }
        
        public String getName() {
            return this.name;
        }
        
        @Override
        public int compareTo(final playerIdName pin) {
            return this.getName().compareTo(pin.getName());
        }
    }
}
