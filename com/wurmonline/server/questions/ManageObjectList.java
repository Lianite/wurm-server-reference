// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.questions;

import com.wurmonline.server.creatures.MineDoorSettings;
import com.wurmonline.server.structures.DoorSettings;
import com.wurmonline.server.items.ItemSettings;
import com.wurmonline.server.structures.StructureSettings;
import com.wurmonline.server.creatures.AnimalSettings;
import com.wurmonline.server.players.Permissions;
import com.wurmonline.server.creatures.Delivery;
import java.util.Set;
import com.wurmonline.server.villages.Village;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import com.wurmonline.server.creatures.Brand;
import java.util.Iterator;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.structures.Door;
import com.wurmonline.server.behaviours.Vehicle;
import com.wurmonline.server.zones.Zones;
import com.wurmonline.server.endgames.EndGameItems;
import com.wurmonline.server.creatures.DbCreatureStatus;
import com.wurmonline.server.behaviours.MethodsCreatures;
import com.wurmonline.server.Servers;
import com.wurmonline.server.creatures.MineDoorPermission;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Items;
import com.wurmonline.server.structures.FenceGate;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import java.util.logging.Level;
import com.wurmonline.server.behaviours.Vehicles;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.WurmId;
import com.wurmonline.server.players.PlayerInfoFactory;
import com.wurmonline.server.LoginHandler;
import java.util.Properties;
import com.wurmonline.server.structures.Structure;
import com.wurmonline.server.creatures.Wagoner;
import com.wurmonline.server.structures.NoSuchStructureException;
import com.wurmonline.server.structures.Structures;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.PermissionsPlayerList;
import com.wurmonline.server.players.Player;
import java.util.logging.Logger;

public class ManageObjectList extends Question
{
    private static final Logger logger;
    private static final String red = "color=\"255,127,127\"";
    private static final String green = "color=\"127,255,127\"";
    private static final String orange = "color=\"255,177,40\"";
    private final Player player;
    private final Type objectType;
    private final boolean fromList;
    private final int sortBy;
    private String searchName;
    private final boolean includeAll;
    private PermissionsPlayerList.ISettings[] objects;
    private boolean showingQueue;
    private boolean inQueue;
    private boolean waitAccept;
    private boolean inProgress;
    private boolean delivered;
    private boolean rejected;
    private boolean cancelled;
    
    public ManageObjectList(final Creature aResponder, final Type aType) {
        this(aResponder, aType, -10L, false, 1, "", true);
    }
    
    public ManageObjectList(final Creature aResponder, final Type aType, final long parent, final boolean wasFromList, final int aSortBy, final String searchFor, final boolean aIncludeAll) {
        super(aResponder, getTitle(aResponder, aType, parent, wasFromList, searchFor), getQuestion(aResponder, aType, parent, wasFromList, searchFor), 121, parent);
        this.objects = null;
        this.showingQueue = false;
        this.inQueue = true;
        this.waitAccept = false;
        this.inProgress = false;
        this.delivered = false;
        this.rejected = false;
        this.cancelled = false;
        this.player = (Player)this.getResponder();
        this.fromList = wasFromList;
        this.objectType = aType;
        this.sortBy = aSortBy;
        this.searchName = searchFor;
        this.includeAll = aIncludeAll;
    }
    
    public ManageObjectList(final Creature aResponder, final Type aType, final long parent, final boolean wasFromList, final int aSortBy, final String searchFor, final boolean aIncludeAll, final boolean inqueue, final boolean waitaccept, final boolean inprogress, final boolean delivered, final boolean rejected, final boolean cancelled) {
        super(aResponder, getTitle(aResponder, aType, parent, wasFromList, searchFor), getQuestion(aResponder, aType, parent, wasFromList, searchFor), 121, parent);
        this.objects = null;
        this.showingQueue = false;
        this.inQueue = true;
        this.waitAccept = false;
        this.inProgress = false;
        this.delivered = false;
        this.rejected = false;
        this.cancelled = false;
        this.player = (Player)this.getResponder();
        this.fromList = wasFromList;
        this.objectType = aType;
        this.sortBy = aSortBy;
        this.searchName = searchFor;
        this.includeAll = aIncludeAll;
        this.showingQueue = (aType == Type.WAGONER);
        this.inQueue = inqueue;
        this.waitAccept = waitaccept;
        this.inProgress = inprogress;
        this.delivered = delivered;
        this.rejected = rejected;
        this.cancelled = cancelled;
    }
    
    private static String getTitle(final Creature aResponder, final Type aType, final long parent, final boolean wasFromList, final String lookingFor) {
        if (aType == Type.DOOR) {
            try {
                final Structure structure = Structures.getStructure(parent);
                return structure.getName() + "'s List of doors";
            }
            catch (NoSuchStructureException e) {
                return aResponder.getName() + "'s List of " + aType.getTitle() + "s";
            }
        }
        if (aType == Type.SEARCH) {
            return "Player Search";
        }
        if (aType == Type.REPLY) {
            return "Search Result for " + lookingFor;
        }
        if (aType == Type.SMALL_CART || aType == Type.LARGE_CART || aType == Type.WAGON || aType == Type.SHIP_CARRIER || aType == Type.CREATURE_CARRIER) {
            return aResponder.getName() + "'s List of Small Carts, Large Carts, Wagons and Carriers";
        }
        if (aType == Type.WAGONER && wasFromList) {
            final Wagoner wagoner = Wagoner.getWagoner(parent);
            return "Wagoners " + wagoner.getName() + "'s Queue";
        }
        return aResponder.getName() + "'s List of " + aType.getTitle() + "s";
    }
    
    private static String getQuestion(final Creature aResponder, final Type aType, final long parent, final boolean wasFromList, final String lookingFor) {
        if (aType == Type.DOOR) {
            try {
                final Structure structure = Structures.getStructure(parent);
                return "Manage List of Doors for " + structure.getName();
            }
            catch (NoSuchStructureException e) {
                return "Manage Your List of " + aType.getTitle() + "s";
            }
        }
        if (aType == Type.SEARCH) {
            return "Player Search";
        }
        if (aType == Type.REPLY) {
            return "Search Result for " + lookingFor;
        }
        if (aType == Type.SMALL_CART || aType == Type.LARGE_CART || aType == Type.WAGON || aType == Type.SHIP_CARRIER || aType == Type.CREATURE_CARRIER) {
            return "Manage Your List of Small Carts, Large Carts, Wagons and Carriers";
        }
        if (aType == Type.WAGONER && wasFromList) {
            final Wagoner wagoner = Wagoner.getWagoner(parent);
            return "Viewing " + wagoner.getName() + "'s Queue";
        }
        return "Manage Your List of " + aType.getTitle() + "s";
    }
    
    @Override
    public void answer(final Properties aAnswer) {
        this.setAnswer(aAnswer);
        final boolean managePermissions = this.getBooleanProp("permissions");
        final boolean manageDoors = this.getBooleanProp("doors");
        final boolean back = this.getBooleanProp("back");
        final boolean close = this.getBooleanProp("close");
        final boolean search = this.getBooleanProp("search");
        final boolean remall = this.getBooleanProp("remall");
        final boolean findAnimal = this.getBooleanProp("find");
        final boolean inc = this.getBooleanProp("inc");
        final boolean exc = this.getBooleanProp("exc");
        final boolean queue = this.getBooleanProp("queue");
        final boolean viewDelivery = this.getBooleanProp("delivery");
        if (close) {
            return;
        }
        if (inc) {
            final ManageObjectList mol = new ManageObjectList(this.player, this.objectType, this.target, this.fromList, this.sortBy, this.searchName, true);
            mol.sendQuestion();
            return;
        }
        if (exc) {
            final ManageObjectList mol = new ManageObjectList(this.player, this.objectType, this.target, this.fromList, this.sortBy, this.searchName, false);
            mol.sendQuestion();
            return;
        }
        if (back) {
            if (this.objectType == Type.DOOR) {
                final ManageObjectList mol = new ManageObjectList(this.player, Type.BUILDING, this.target, false, 1, "", this.includeAll);
                mol.sendQuestion();
                return;
            }
            if (this.objectType == Type.WAGONER) {
                final ManageObjectList mol = new ManageObjectList(this.player, Type.WAGONER, this.target, false, 1, "", this.includeAll);
                mol.sendQuestion();
                return;
            }
            if (this.objectType == Type.DELIVERY) {
                final ManageObjectList mol = new ManageObjectList(this.player, Type.WAGONER, this.target, false, 1, "", this.includeAll);
                mol.sendQuestion2();
                return;
            }
            if (this.objectType == Type.REPLY) {
                final ManageObjectList mol = new ManageObjectList(this.player, Type.SEARCH);
                mol.sendQuestion();
                return;
            }
        }
        if (search) {
            final String who = aAnswer.getProperty("who");
            final String lookingFor = LoginHandler.raiseFirstLetter(who);
            final long lookId = PlayerInfoFactory.getWurmId(lookingFor);
            final ManageObjectList mol2 = new ManageObjectList(this.player, Type.REPLY, lookId, true, 1, lookingFor, this.includeAll);
            mol2.sendQuestion();
            return;
        }
        if (remall) {
            for (final PermissionsPlayerList.ISettings is : this.objects) {
                if (!is.isActualOwner(this.target)) {
                    is.removeGuest(this.target);
                    this.player.getCommunicator().sendNormalServerMessage("You removed " + this.searchName + " from " + is.getTypeName() + " (" + is.getObjectName() + ")");
                }
            }
        }
        if (managePermissions || manageDoors || findAnimal || queue || viewDelivery) {
            final String sel = aAnswer.getProperty("sel");
            final long selId = Long.parseLong(sel);
            if (selId == -10L) {
                this.player.getCommunicator().sendNormalServerMessage("You decide to do nothing.");
                return;
            }
            if (managePermissions) {
                final int ct = WurmId.getType(selId);
                if (ct == 1) {
                    try {
                        final Creature creature = Creatures.getInstance().getCreature(selId);
                        if (creature.isWagoner()) {
                            final ManagePermissions mp = new ManagePermissions(this.player, Type.WAGONER, creature, true, this.target, false, this.objectType, "");
                            mp.sendQuestion();
                        }
                        else {
                            final Vehicle vehicle = Vehicles.getVehicle(creature);
                            if (vehicle == null) {
                                final ManagePermissions mp2 = new ManagePermissions(this.player, Type.ANIMAL0, creature, true, this.target, false, this.objectType, "");
                                mp2.sendQuestion();
                            }
                            else if (vehicle.isUnmountable()) {
                                final ManagePermissions mp2 = new ManagePermissions(this.player, Type.ANIMAL0, creature, true, this.target, false, this.objectType, "");
                                mp2.sendQuestion();
                            }
                            else if (vehicle.getMaxPassengers() == 0) {
                                final ManagePermissions mp2 = new ManagePermissions(this.player, Type.ANIMAL1, creature, true, this.target, false, this.objectType, "");
                                mp2.sendQuestion();
                            }
                            else {
                                final ManagePermissions mp2 = new ManagePermissions(this.player, Type.ANIMAL2, creature, true, this.target, false, this.objectType, "");
                                mp2.sendQuestion();
                            }
                        }
                    }
                    catch (NoSuchCreatureException nsce) {
                        this.player.getCommunicator().sendNormalServerMessage("Cannot find animal, it was here a minute ago!");
                        ManageObjectList.logger.log(Level.WARNING, "Cannot find animal, it was here a minute ago! Id:" + selId, nsce);
                    }
                }
                else if (ct == 4) {
                    try {
                        final PermissionsPlayerList.ISettings theItem = Structures.getStructure(selId);
                        final ManagePermissions mp = new ManagePermissions(this.player, Type.BUILDING, theItem, true, this.target, false, this.objectType, "");
                        mp.sendQuestion();
                    }
                    catch (NoSuchStructureException nsse) {
                        this.player.getCommunicator().sendNormalServerMessage("Cannot find structure, it was here a minute ago!");
                        ManageObjectList.logger.log(Level.WARNING, "Cannot find structure, it was here a minute ago! Id:" + selId, nsse);
                    }
                }
                else if (ct == 5) {
                    try {
                        final Structure structure = Structures.getStructure(this.target);
                        for (final Door door : structure.getAllDoors()) {
                            if (door.getWurmId() == selId) {
                                final ManagePermissions mp3 = new ManagePermissions(this.player, Type.DOOR, door, true, this.target, this.fromList, this.objectType, "");
                                mp3.sendQuestion();
                                return;
                            }
                        }
                        this.player.getCommunicator().sendNormalServerMessage("Cannot find door, it was here a minute ago!");
                        ManageObjectList.logger.log(Level.WARNING, "Cannot find door, it was here a minute ago! Id:" + selId);
                    }
                    catch (NoSuchStructureException nsse) {
                        this.player.getCommunicator().sendNormalServerMessage("Cannot find structure, it was here a minute ago!");
                        ManageObjectList.logger.log(Level.WARNING, "Cannot find structure, it was here a minute ago! Id:" + selId, nsse);
                    }
                }
                else if (ct == 7) {
                    final FenceGate gate = FenceGate.getFenceGate(selId);
                    if (gate != null) {
                        final ManagePermissions mp = new ManagePermissions(this.player, Type.GATE, gate, true, this.target, this.fromList, this.objectType, "");
                        mp.sendQuestion();
                    }
                    else {
                        this.player.getCommunicator().sendNormalServerMessage("Cannot find gate, it was here a minute ago!");
                    }
                }
                else if (ct == 2) {
                    try {
                        final Item item = Items.getItem(selId);
                        Type itemType = Type.SHIP;
                        if (item.getTemplateId() == 186) {
                            itemType = Type.SMALL_CART;
                        }
                        else if (item.getTemplateId() == 539) {
                            itemType = Type.LARGE_CART;
                        }
                        else if (item.getTemplateId() == 850) {
                            itemType = Type.WAGON;
                        }
                        else if (item.getTemplateId() == 853) {
                            itemType = Type.SHIP_CARRIER;
                        }
                        else if (item.getTemplateId() == 1410) {
                            itemType = Type.CREATURE_CARRIER;
                        }
                        final ManagePermissions mp2 = new ManagePermissions(this.player, itemType, item, true, this.target, this.fromList, this.objectType, "");
                        mp2.sendQuestion();
                    }
                    catch (NoSuchItemException e) {
                        this.player.getCommunicator().sendNormalServerMessage("Cannot find vehicle, it was here a minute ago!");
                    }
                }
                else if (ct == 3) {
                    final MineDoorPermission mineDoor = MineDoorPermission.getPermission(selId);
                    if (mineDoor != null) {
                        final ManagePermissions mp = new ManagePermissions(this.player, Type.MINEDOOR, mineDoor, true, this.target, this.fromList, this.objectType, "");
                        mp.sendQuestion();
                    }
                    else {
                        this.player.getCommunicator().sendNormalServerMessage("Cannot find minedoor, it was here a minute ago!");
                    }
                }
                else {
                    this.player.getCommunicator().sendNormalServerMessage("Unknown object!");
                }
                return;
            }
            if (manageDoors) {
                final ManageObjectList mol3 = new ManageObjectList(this.player, Type.DOOR, selId, true, 1, "", this.includeAll);
                mol3.sendQuestion();
            }
            else if (findAnimal && !Servers.isThisAPvpServer()) {
                try {
                    final Creature creature2 = Creatures.getInstance().getCreature(selId);
                    final int centerx = creature2.getTileX();
                    final int centery = creature2.getTileY();
                    final int dx = Math.abs(centerx - this.player.getTileX());
                    final int dy = Math.abs(centery - this.player.getTileY());
                    final int mindist = (int)Math.sqrt(dx * dx + dy * dy);
                    final int dir = MethodsCreatures.getDir(this.player, centerx, centery);
                    if (DbCreatureStatus.getIsLoaded(creature2.getWurmId()) == 0) {
                        final String direction = MethodsCreatures.getLocationStringFor(this.player.getStatus().getRotation(), dir, "you");
                        final String toReturn = EndGameItems.getDistanceString(mindist, creature2.getName(), direction, false);
                        this.player.getCommunicator().sendNormalServerMessage(toReturn);
                    }
                    else {
                        this.player.getCommunicator().sendNormalServerMessage("This creature is loaded in a cage, or on another server.");
                    }
                }
                catch (NoSuchCreatureException nsce2) {
                    this.player.getCommunicator().sendNormalServerMessage("Cannot find animal, it was here a minute ago!");
                    ManageObjectList.logger.log(Level.WARNING, "Cannot find animal, it was here a minute ago! Id:" + selId, nsce2);
                }
            }
            else {
                if (queue && this.objectType == Type.WAGONER) {
                    this.inQueue = this.getBooleanProp("inqueue");
                    this.waitAccept = this.getBooleanProp("waitaccept");
                    this.inProgress = this.getBooleanProp("inprogress");
                    this.delivered = this.getBooleanProp("delivered");
                    this.rejected = this.getBooleanProp("rejected");
                    this.cancelled = this.getBooleanProp("cancelled");
                    final ManageObjectList mol3 = new ManageObjectList(this.player, Type.WAGONER, selId, true, 1, "", this.includeAll, this.inQueue, this.waitAccept, this.inProgress, this.delivered, this.rejected, this.cancelled);
                    mol3.sendQuestion2();
                    return;
                }
                this.inQueue = this.getBooleanProp("inqueue");
                this.waitAccept = this.getBooleanProp("waitaccept");
                this.inProgress = this.getBooleanProp("inprogress");
                this.delivered = this.getBooleanProp("delivered");
                this.rejected = this.getBooleanProp("rejected");
                this.cancelled = this.getBooleanProp("cancelled");
                final ManageObjectList mol3 = new ManageObjectList(this.player, Type.DELIVERY, selId, true, 1, "", this.includeAll, this.inQueue, this.waitAccept, this.inProgress, this.delivered, this.rejected, this.cancelled);
                mol3.sendQuestion3();
                return;
            }
        }
        for (final String key : this.getAnswer().stringPropertyNames()) {
            if (key.startsWith("sort")) {
                final String sid = key.substring(4);
                final int newSort = Integer.parseInt(sid);
                if (this.showingQueue) {
                    final ManageObjectList mol2 = new ManageObjectList(this.player, this.objectType, this.target, this.fromList, newSort, this.searchName, this.includeAll, this.inQueue, this.waitAccept, this.inProgress, this.delivered, this.rejected, this.cancelled);
                    mol2.sendQuestion2();
                    return;
                }
                if (this.objectType == Type.DELIVERY) {
                    final ManageObjectList mol2 = new ManageObjectList(this.player, this.objectType, this.target, this.fromList, newSort, this.searchName, this.includeAll, this.inQueue, this.waitAccept, this.inProgress, this.delivered, this.rejected, this.cancelled);
                    mol2.sendQuestion3();
                    return;
                }
                final ManageObjectList mol2 = new ManageObjectList(this.player, this.objectType, this.target, this.fromList, newSort, this.searchName, this.includeAll);
                mol2.sendQuestion();
                return;
            }
        }
        if (this.objectType == Type.BUILDING) {
            for (final String key : this.getAnswer().stringPropertyNames()) {
                if (key.startsWith("demolish")) {
                    final String sid = key.substring(8);
                    final long id = Long.parseLong(sid);
                    try {
                        final Structure structure2 = Structures.getStructure(id);
                        if (structure2.isOnSurface()) {
                            Zones.flash(structure2.getCenterX(), structure2.getCenterY(), false);
                        }
                        structure2.totallyDestroy();
                    }
                    catch (NoSuchStructureException nsse2) {
                        this.player.getCommunicator().sendNormalServerMessage("Cannot find structure, it was here a minute ago!");
                        ManageObjectList.logger.log(Level.WARNING, "Cannot find structure, it was here a minute ago! Id:" + id, nsse2);
                    }
                }
            }
        }
        else if (this.objectType == Type.WAGONER) {
            for (final String key : this.getAnswer().stringPropertyNames()) {
                if (key.startsWith("dismiss")) {
                    final String sid = key.substring(7);
                    final long id = Long.parseLong(sid);
                    try {
                        final Creature creature3 = Creatures.getInstance().getCreature(id);
                        final Wagoner wagoner = creature3.getWagoner();
                        if (wagoner.getVillageId() == -1) {
                            this.player.getCommunicator().sendNormalServerMessage("Wagoner is already dismissing!");
                        }
                        else {
                            final WagonerDismissQuestion wdq = new WagonerDismissQuestion(this.getResponder(), wagoner);
                            wdq.sendQuestion();
                        }
                    }
                    catch (NoSuchCreatureException nsse3) {
                        this.player.getCommunicator().sendNormalServerMessage("Cannot find wagoner, it was here a minute ago!");
                        ManageObjectList.logger.log(Level.WARNING, "Cannot find wagoner, it was here a minute ago! Id:" + id, nsse3);
                    }
                }
            }
        }
        else if (this.objectType == Type.ANIMAL0 || this.objectType == Type.ANIMAL1 || this.objectType == Type.ANIMAL2) {
            for (final String key : this.getAnswer().stringPropertyNames()) {
                if (key.startsWith("uncarefor")) {
                    final String sid = key.substring(9);
                    final long id = Long.parseLong(sid);
                    try {
                        final int tc = Creatures.getInstance().getNumberOfCreaturesProtectedBy(this.player.getWurmId());
                        final int max = this.player.getNumberOfPossibleCreatureTakenCareOf();
                        final Creature animal = Creatures.getInstance().getCreature(id);
                        if (animal.getCareTakerId() == this.player.getWurmId()) {
                            Creatures.getInstance().setCreatureProtected(animal, -10L, false);
                            this.player.getCommunicator().sendNormalServerMessage("You let " + animal.getName() + " go in order to care for other creatures. You may care for " + (max - tc + 1) + " more creatures.");
                        }
                        else {
                            this.player.getCommunicator().sendNormalServerMessage("You are not caring for this animal!");
                        }
                    }
                    catch (NoSuchCreatureException nsce3) {
                        ManageObjectList.logger.log(Level.WARNING, nsce3.getMessage(), nsce3);
                    }
                }
                if (key.startsWith("unbrand")) {
                    final String sid = key.substring(7);
                    final long id = Long.parseLong(sid);
                    try {
                        final Creature animal2 = Creatures.getInstance().getCreature(id);
                        final Brand brand = Creatures.getInstance().getBrand(animal2.getWurmId());
                        if (brand != null) {
                            if (animal2.getBrandVillage() == this.player.getCitizenVillage()) {
                                if (this.player.getCitizenVillage().isActionAllowed((short)484, this.player)) {
                                    brand.deleteBrand();
                                    if (animal2.getVisionArea() != null) {
                                        animal2.getVisionArea().broadCastUpdateSelectBar(animal2.getWurmId());
                                    }
                                }
                                else {
                                    this.player.getCommunicator().sendNormalServerMessage("You need to have deed permission to remove a brand.");
                                }
                            }
                            else {
                                this.player.getCommunicator().sendNormalServerMessage("You need to be in same village as the brand on the animal.");
                            }
                        }
                        else {
                            this.player.getCommunicator().sendNormalServerMessage("That animal is not branded.");
                        }
                    }
                    catch (NoSuchCreatureException nsce3) {
                        ManageObjectList.logger.log(Level.WARNING, nsce3.getMessage(), nsce3);
                    }
                }
                if (key.startsWith("untame")) {
                    final String sid = key.substring(6);
                    final long id = Long.parseLong(sid);
                    try {
                        final Creature animal2 = Creatures.getInstance().getCreature(id);
                        if (animal2.getDominator() == this.player) {
                            if (DbCreatureStatus.getIsLoaded(animal2.getWurmId()) == 1) {
                                this.player.getCommunicator().sendNormalServerMessage("This animal is caged, remove it first.", (byte)3);
                                return;
                            }
                            final Creature pet = this.player.getPet();
                            if (animal2.cantRideUntame()) {
                                assert pet != null;
                                final Vehicle cret = Vehicles.getVehicleForId(pet.getWurmId());
                                if (cret != null) {
                                    cret.kickAll();
                                }
                            }
                            animal2.setDominator(-10L);
                            this.player.setPet(-10L);
                            this.player.getCommunicator().sendNormalServerMessage("You no longer have this animal tamed!");
                        }
                        else {
                            this.player.getCommunicator().sendNormalServerMessage("You do not have this animal tamed!");
                        }
                    }
                    catch (NoSuchCreatureException nsce3) {
                        ManageObjectList.logger.log(Level.WARNING, nsce3.getMessage(), nsce3);
                    }
                }
            }
        }
    }
    
    @Override
    public void sendQuestion() {
        int width = 300;
        final StringBuilder buf = new StringBuilder();
        final String closeBtn = "harray{" + (((this.objectType == Type.DOOR || this.objectType == Type.REPLY) && this.fromList) ? "button{text=\"Back\";id=\"back\"};" : "") + "label{text=\" \"};button{text=\"Close\";id=\"close\"};label{text=\" \"}};";
        buf.append("border{border{size=\"20,20\";null;null;label{type='bold';text=\"" + this.question + "\"};" + closeBtn + "null;}null;scroll{vertical=\"true\";horizontal=\"false\";varray{rescale=\"true\";passthrough{id=\"id\";text=\"" + this.getId() + "\"}");
        String extraButton = "";
        if (this.objectType == Type.SEARCH) {
            buf.append("text{text=\"Allow searching for all objects that the player has permissions and you can manage.\"}");
            buf.append("harray{label{text=\"Look For Player \"}input{id=\"who\"}}");
            buf.append("text{text=\"\"}");
            buf.append("harray{button{text=\"Search\";id=\"search\";default=\"true\"}};");
            buf.append("}};null;null;}");
            this.getResponder().getCommunicator().sendBml(300, 160, true, true, buf.toString(), 200, 200, 200, this.title);
            return;
        }
        if (this.objectType == Type.REPLY) {
            if (this.target == -10L) {
                buf.append("label{text=\"No such Player\"}");
                buf.append("}};null;null;}");
                this.getResponder().getCommunicator().sendBml(300, 150, true, true, buf.toString(), 200, 200, 200, this.title);
                return;
            }
            final Village vill = this.getResponder().getCitizenVillage();
            final int vid = (vill != null && vill.getRoleFor(this.getResponder()).mayManageAllowedObjects()) ? vill.getId() : -1;
            final Set<PermissionsPlayerList.ISettings> result = new HashSet<PermissionsPlayerList.ISettings>();
            this.objects = Creatures.getManagedAnimalsFor(this.player, vid, true);
            for (final PermissionsPlayerList.ISettings is : this.objects) {
                if (is.isGuest(this.target)) {
                    result.add(is);
                }
            }
            this.objects = Structures.getManagedBuildingsFor(this.player, vid, true);
            for (final PermissionsPlayerList.ISettings is : this.objects) {
                if (is.isGuest(this.target)) {
                    result.add(is);
                }
            }
            this.objects = FenceGate.getManagedGatesFor(this.player, vid, true);
            for (final PermissionsPlayerList.ISettings is : this.objects) {
                if (is.isGuest(this.target)) {
                    result.add(is);
                }
            }
            this.objects = Items.getManagedCartsFor(this.player, true);
            for (final PermissionsPlayerList.ISettings is : this.objects) {
                if (is.isGuest(this.target)) {
                    result.add(is);
                }
            }
            this.objects = MineDoorPermission.getManagedMineDoorsFor(this.player, vid, true);
            for (final PermissionsPlayerList.ISettings is : this.objects) {
                if (is.isGuest(this.target)) {
                    result.add(is);
                }
            }
            this.objects = Items.getManagedShipsFor(this.player, true);
            for (final PermissionsPlayerList.ISettings is : this.objects) {
                if (is.isGuest(this.target)) {
                    result.add(is);
                }
            }
            this.objects = result.toArray(new PermissionsPlayerList.ISettings[result.size()]);
            buf.append("text{text=\"List of objects that player '" + this.searchName + "' has permissions for that you may manage.\"}");
            final int absSortBy = Math.abs(this.sortBy);
            final int upDown = Integer.signum(this.sortBy);
            buf.append("table{rows=\"1\";cols=\"5\";label{text=\"\"};" + this.colHeader("Name", 1, this.sortBy) + this.colHeader("Type", 2, this.sortBy) + this.colHeader("Owner?", 3, this.sortBy) + "label{type=\"bold\";text=\"\"};");
            switch (absSortBy) {
                case 1: {
                    Arrays.sort(this.objects, new Comparator<PermissionsPlayerList.ISettings>() {
                        @Override
                        public int compare(final PermissionsPlayerList.ISettings param1, final PermissionsPlayerList.ISettings param2) {
                            return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                        }
                    });
                    break;
                }
                case 2: {
                    Arrays.sort(this.objects, new Comparator<PermissionsPlayerList.ISettings>() {
                        @Override
                        public int compare(final PermissionsPlayerList.ISettings param1, final PermissionsPlayerList.ISettings param2) {
                            return param1.getTypeName().compareTo(param2.getTypeName()) * upDown;
                        }
                    });
                    break;
                }
                case 3: {
                    Arrays.sort(this.objects, new Comparator<PermissionsPlayerList.ISettings>() {
                        @Override
                        public int compare(final PermissionsPlayerList.ISettings param1, final PermissionsPlayerList.ISettings param2) {
                            if (param1.isActualOwner(ManageObjectList.this.target) == param2.isActualOwner(ManageObjectList.this.target)) {
                                return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                            }
                            if (param1.isActualOwner(ManageObjectList.this.target)) {
                                return -1 * upDown;
                            }
                            return 1 * upDown;
                        }
                    });
                    break;
                }
            }
            for (final PermissionsPlayerList.ISettings object : this.objects) {
                buf.append("radio{group=\"sel\";id=\"" + object.getWurmId() + "\";text=\"\"}label{text=\"" + object.getObjectName() + "\"};label{text=\"" + object.getTypeName() + "\"};label{" + this.showBoolean(object.isActualOwner(this.target)) + "};label{text=\"\"}");
            }
            buf.append("}");
            if (result.size() > 0) {
                extraButton = ";label{text=\"  \"};button{text=\"Remove all Permissions\";id=\"remall\"}";
            }
        }
        else if (this.objectType == Type.ANIMAL0 || this.objectType == Type.ANIMAL1 || this.objectType == Type.ANIMAL2) {
            buf.append(this.makeAnimalList());
            if (!Servers.isThisAPvpServer()) {
                extraButton = ";label{text=\"  \"};button{text=\"Give direction to\";id=\"find\"}";
            }
            width = 550;
        }
        else if (this.objectType == Type.BUILDING) {
            buf.append(this.makeBuildingList());
            extraButton = ";label{text=\"  \"};button{text=\"Manage All Doors\";id=\"doors\"}";
            width = 500;
        }
        else if (this.objectType == Type.LARGE_CART || this.objectType == Type.SMALL_CART || this.objectType == Type.WAGON || this.objectType == Type.SHIP_CARRIER || this.objectType == Type.CREATURE_CARRIER) {
            buf.append(this.makeLandVehicleList());
            width = 500;
        }
        else if (this.objectType == Type.DOOR) {
            buf.append(this.makeDoorList());
            width = 500;
        }
        else if (this.objectType == Type.GATE) {
            buf.append(this.makeGateList());
            width = 600;
        }
        else if (this.objectType == Type.MINEDOOR) {
            buf.append(this.makeMineDoorList());
            width = 400;
        }
        else if (this.objectType == Type.SHIP) {
            buf.append(this.makeShipList());
            width = 500;
        }
        else if (this.objectType == Type.WAGONER) {
            buf.append(this.makeWagonerList());
            width = 600;
        }
        buf.append("radio{group=\"sel\";id=\"-10\";selected=\"true\";text=\"None\"}");
        buf.append("text{text=\"\"}");
        buf.append("harray{button{text=\"Manage Permissions\";id=\"permissions\"}" + extraButton + "};");
        if (this.objectType == Type.WAGONER) {
            buf.append("text{text=\"\"}");
            buf.append("harray{button{text=\"View Deliveries\";id=\"queue\"};label{text=\" filter by \"};checkbox{id=\"inqueue\";text=\"In queue  \"" + (this.inQueue ? ";selected=\"true\"" : "") + "};checkbox{id=\"waitaccept\";text=\"Waiting for accept  \"" + (this.waitAccept ? ";selected=\"true\"" : "") + "};checkbox{id=\"inprogress\";text=\"In Progress  \"" + (this.inProgress ? ";selected=\"true\"" : "") + "};checkbox{id=\"delivered\";text=\"Delivered  \"" + (this.delivered ? ";selected=\"true\"" : "") + "};checkbox{id=\"rejected\";text=\"Rejected  \"" + (this.rejected ? ";selected=\"true\"" : "") + "};checkbox{id=\"cancelled\";text=\"Cancelled  \"" + (this.cancelled ? ";selected=\"true\"" : "") + "};};");
        }
        buf.append("}};null;null;}");
        this.getResponder().getCommunicator().sendBml(width, 400, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    private void sendQuestion2() {
        final StringBuilder buf = new StringBuilder();
        final String closeBtn = "harray{button{text=\"Back\";id=\"back\"};label{text=\" \"};button{text=\"Close\";id=\"close\"};label{text=\" \"}};";
        buf.append("border{border{size=\"20,20\";null;null;label{type='bold';text=\"" + this.question + "\"};" + "harray{button{text=\"Back\";id=\"back\"};label{text=\" \"};button{text=\"Close\";id=\"close\"};label{text=\" \"}};" + "null;}null;scroll{vertical=\"true\";horizontal=\"false\";varray{rescale=\"true\";passthrough{id=\"id\";text=\"" + this.getId() + "\"}");
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        final Delivery[] deliveries = Delivery.getDeliveriesFor(this.target, this.inQueue, this.waitAccept, this.inProgress, this.rejected, this.delivered);
        buf.append("table{rows=\"1\";cols=\"6\";label{text=\"\"};" + this.colHeader("id", 1, this.sortBy) + this.colHeader("Sender", 2, this.sortBy) + this.colHeader("Receiver", 3, this.sortBy) + this.colHeader("State", 4, this.sortBy) + "label{text=\"\"};");
        switch (absSortBy) {
            case 1: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        final long value1 = param1.getDeliveryId();
                        final long value2 = param2.getDeliveryId();
                        if (value1 == value2) {
                            return 0;
                        }
                        if (value1 < value2) {
                            return -1 * upDown;
                        }
                        return 1 * upDown;
                    }
                });
                break;
            }
            case 2: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        return param1.getSenderName().compareTo(param2.getSenderName()) * upDown;
                    }
                });
                break;
            }
            case 3: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        return param1.getReceiverName().compareTo(param2.getReceiverName()) * upDown;
                    }
                });
                break;
            }
            case 4: {
                Arrays.sort(deliveries, new Comparator<Delivery>() {
                    @Override
                    public int compare(final Delivery param1, final Delivery param2) {
                        return param1.getStateName().compareTo(param2.getStateName()) * upDown;
                    }
                });
                break;
            }
        }
        for (final Delivery delivery : deliveries) {
            buf.append("radio{group=\"sel\";id=\"" + delivery.getDeliveryId() + "\";text=\"\"}label{text=\"" + delivery.getDeliveryId() + "\"};label{text=\"" + delivery.getSenderName() + "\"};label{text=\"" + delivery.getReceiverName() + "\"};label{text=\"" + delivery.getStateName() + "\"};label{text=\"  \"};");
        }
        buf.append("}");
        buf.append("radio{group=\"sel\";id=\"-10\";selected=\"true\";text=\"None\"}");
        buf.append("text{text=\"\"}");
        buf.append("harray{button{text=\"View Delivery\";id=\"delivery\"};};");
        buf.append("}};null;null;}");
        this.getResponder().getCommunicator().sendBml(400, 400, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    private void sendQuestion3() {
        final StringBuilder buf = new StringBuilder();
        final String closeBtn = "harray{button{text=\"Back\";id=\"back\"};label{text=\" \"};button{text=\"Close\";id=\"close\"};label{text=\" \"}};";
        buf.append("border{border{size=\"20,20\";null;null;label{type='bold';text=\"" + this.question + "\"};" + "harray{button{text=\"Back\";id=\"back\"};label{text=\" \"};button{text=\"Close\";id=\"close\"};label{text=\" \"}};" + "null;}null;scroll{vertical=\"true\";horizontal=\"false\";varray{rescale=\"true\";passthrough{id=\"id\";text=\"" + this.getId() + "\"}");
        final Delivery delivery = Delivery.getDelivery(this.target);
        buf.append("table{rows=\"1\";cols=\"4\";");
        buf.append("label{text=\"\"};label{text=\"Id\"};label{text=\"\"};label{text=\"" + delivery.getDeliveryId() + "\"};");
        buf.append("label{text=\"\"};label{text=\"Sender\"};label{text=\"\"};label{text=\"" + delivery.getSenderName() + "\"};");
        buf.append("label{text=\"\"};label{text=\"Receiver\"};label{text=\"\"};label{text=\"" + delivery.getReceiverName() + "\"};");
        buf.append("label{text=\"\"};label{text=\"State\"};label{text=\"\"};label{text=\"" + delivery.getStateName() + "\"};");
        buf.append("label{text=\"\"};label{text=\"Delivery setup\"};label{text=\"@\"};label{text=\"" + delivery.getStringWaitingForAccept() + "\"};");
        String reason = "Accepted";
        switch (delivery.getState()) {
            case 6:
            case 11: {
                reason = "Timed Out";
                break;
            }
            case 5:
            case 8: {
                reason = "Rejected";
                break;
            }
            case 9:
            case 10: {
                reason = "Cancelled";
                break;
            }
        }
        buf.append("label{text=\"\"};label{text=\"" + reason + "\"};label{text=\"@\"};label{text=\"" + delivery.getStringAcceptedOrRejected() + "\"};");
        buf.append("label{text=\"\"};label{text=\"Delivery started\"};label{text=\"@\"};label{text=\"" + delivery.getStringDeliveryStarted() + "\"};");
        buf.append("label{text=\"\"};label{text=\"Crates picked up\"};label{text=\"@\"};label{text=\"" + delivery.getStringPickedUp() + "\"};");
        buf.append("label{text=\"\"};label{text=\"Crates delivered\"};label{text=\"@\"};label{text=\"" + delivery.getStringDelivered() + "\"};");
        buf.append("}");
        buf.append("}};null;null;}");
        this.getResponder().getCommunicator().sendBml(400, 400, true, true, buf.toString(), 200, 200, 200, this.title);
    }
    
    private String makeAnimalList() {
        final StringBuilder buf = new StringBuilder();
        final Village vill = this.getResponder().getCitizenVillage();
        final int vid = (vill != null && vill.getRoleFor(this.getResponder()).mayManageAllowedObjects()) ? vill.getId() : -1;
        buf.append("text{text=\"As well as the list containing any animals that you care for, and any tamed animals you have. It also includes any animals that are branded to your village that have 'Settlement may manage' Permission set to your village so long as you have the 'Manage Allowed Objects' settlement permission.\"}");
        buf.append("text{text=\"\"}");
        final Creature[] animals = Creatures.getManagedAnimalsFor(this.player, vid, this.includeAll);
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        buf.append("table{rows=\"1\";cols=\"8\";label{text=\"\"};" + this.colHeader("Name", 1, this.sortBy) + this.colHeader("Animal Type", 2, this.sortBy) + this.colHeader("On Deed?", 3, this.sortBy) + this.colHeader("Hitched?", 4, this.sortBy) + this.colHeader("Cared For?", 5, this.sortBy) + this.colHeader("Branded?", 6, this.sortBy) + this.colHeader("Tamed?", 7, this.sortBy));
        switch (absSortBy) {
            case 1: {
                Arrays.sort(animals, new Comparator<Creature>() {
                    @Override
                    public int compare(final Creature param1, final Creature param2) {
                        return param1.getName().compareTo(param2.getName()) * upDown;
                    }
                });
                break;
            }
            case 2: {
                Arrays.sort(animals, new Comparator<Creature>() {
                    @Override
                    public int compare(final Creature param1, final Creature param2) {
                        return param1.getTypeName().compareTo(param2.getTypeName()) * upDown;
                    }
                });
                break;
            }
            case 3: {
                Arrays.sort(animals, new Comparator<Creature>() {
                    @Override
                    public int compare(final Creature param1, final Creature param2) {
                        if (param1.isOnDeed() == param2.isOnDeed()) {
                            return param1.getName().compareTo(param2.getName()) * upDown;
                        }
                        if (param1.isOnDeed()) {
                            return 1 * upDown;
                        }
                        return -1 * upDown;
                    }
                });
                break;
            }
            case 4: {
                Arrays.sort(animals, new Comparator<Creature>() {
                    @Override
                    public int compare(final Creature param1, final Creature param2) {
                        if (param1.isHitched() == param2.isHitched()) {
                            return param1.getName().compareTo(param2.getName()) * upDown;
                        }
                        if (param1.isHitched()) {
                            return 1 * upDown;
                        }
                        return -1 * upDown;
                    }
                });
                break;
            }
            case 5: {
                Arrays.sort(animals, new Comparator<Creature>() {
                    @Override
                    public int compare(final Creature param1, final Creature param2) {
                        if (param1.isCaredFor(ManageObjectList.this.player) == param2.isCaredFor(ManageObjectList.this.player)) {
                            return param1.getName().compareTo(param2.getName()) * upDown;
                        }
                        if (param1.isCaredFor(ManageObjectList.this.player)) {
                            return 1 * upDown;
                        }
                        return -1 * upDown;
                    }
                });
                break;
            }
            case 6: {
                Arrays.sort(animals, new Comparator<Creature>() {
                    @Override
                    public int compare(final Creature param1, final Creature param2) {
                        if (param1.isBrandedBy(vid) == param2.isBrandedBy(vid)) {
                            return param1.getName().compareTo(param2.getName()) * upDown;
                        }
                        if (param1.isBrandedBy(vid)) {
                            return 1 * upDown;
                        }
                        return -1 * upDown;
                    }
                });
                break;
            }
            case 7: {
                Arrays.sort(animals, new Comparator<Creature>() {
                    @Override
                    public int compare(final Creature param1, final Creature param2) {
                        if (param1.isDominated() == param2.isDominated()) {
                            return param1.getName().compareTo(param2.getName()) * upDown;
                        }
                        if (param1.isDominated()) {
                            return 1 * upDown;
                        }
                        return -1 * upDown;
                    }
                });
                break;
            }
        }
        for (final Creature animal : animals) {
            buf.append((animal.canHavePermissions() ? ("radio{group=\"sel\";id=\"" + animal.getWurmId() + "\";text=\"\"}") : "label{text=\"\"};") + "label{text=\"" + animal.getName() + "\"};label{text=\"" + animal.getTypeName() + "\"};" + (animal.isBranded() ? ("label{" + this.showBoolean(animal.isOnDeed()) + "};") : "label{text=\"not branded\"};") + "label{" + this.showBoolean(animal.isHitched()) + "};");
            if (animal.isCaredFor(this.player)) {
                buf.append(this.unCareForButton(animal));
            }
            else {
                buf.append("label{" + this.showBoolean(animal.getCareTakerId() != -10L) + "};");
            }
            if (animal.isBranded() && animal.getBrandVillage() == this.player.getCitizenVillage() && this.player.getCitizenVillage().isActionAllowed((short)484, this.player)) {
                buf.append(this.unBrandButton(animal));
            }
            else {
                buf.append("label{" + this.showBoolean(animal.isBranded()) + "};");
            }
            if (animal.isDominated() && animal.getDominator() == this.player) {
                buf.append(this.unTameButton(animal));
            }
            else {
                buf.append("label{" + this.showBoolean(animal.isDominated()) + "};");
            }
        }
        buf.append("}");
        return buf.toString();
    }
    
    private String unCareForButton(final Creature animal) {
        final StringBuilder buf = new StringBuilder();
        buf.append("harray{button{text=\"Un-Care For\";id=\"uncarefor" + animal.getWurmId() + "\";confirm=\"You are about to un care for " + animal.getName() + ".\";question=\"Do you really want to do that?\"}label{text=\" \"}}");
        return buf.toString();
    }
    
    private String unBrandButton(final Creature animal) {
        final StringBuilder buf = new StringBuilder();
        buf.append("harray{button{text=\"Un-Brand\";id=\"unbrand" + animal.getWurmId() + "\";confirm=\"You are about to remove the brand from " + animal.getName() + ".\";question=\"Do you really want to do that?\"}label{text=\" \"}}");
        return buf.toString();
    }
    
    private String unTameButton(final Creature animal) {
        final StringBuilder buf = new StringBuilder();
        buf.append("harray{button{text=\"Un-Tame\";id=\"untame" + animal.getWurmId() + "\";confirm=\"You are about to un tame " + animal.getName() + ".\";question=\"Do you really want to do that?\"}label{text=\" \"}}");
        return buf.toString();
    }
    
    private String makeBuildingList() {
        final StringBuilder buf = new StringBuilder();
        buf.append("text{text=\"List includes any buildings that you are the owner of plus any buildings in your settlment that have 'Settlement may manage' Permission set to your village so long as you have the 'Manage Allowed Objects' settlement permission.\"}");
        buf.append("text{text=\"\"}");
        final Village vill = this.getResponder().getCitizenVillage();
        final int vid = (vill != null && vill.getRoleFor(this.getResponder()).mayManageAllowedObjects()) ? vill.getId() : -1;
        final Structure[] structures = Structures.getManagedBuildingsFor(this.player, vid, this.includeAll);
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        buf.append("table{rows=\"1\";cols=\"7\";label{text=\"\"};" + this.colHeader("Name", 1, this.sortBy) + this.colHeader("Owner?", 2, this.sortBy) + this.colHeader("Doors have locks?", 3, this.sortBy) + this.colHeader("On Deed?", 4, this.sortBy) + this.colHeader("Deed Managed?", 5, this.sortBy) + "label{type=\"bold\";text=\"\"};");
        switch (absSortBy) {
            case 1: {
                Arrays.sort(structures, new Comparator<Structure>() {
                    @Override
                    public int compare(final Structure param1, final Structure param2) {
                        return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                    }
                });
                break;
            }
            case 2: {
                Arrays.sort(structures, new Comparator<Structure>() {
                    @Override
                    public int compare(final Structure param1, final Structure param2) {
                        if (param1.isActualOwner(ManageObjectList.this.player.getWurmId()) == param2.isActualOwner(ManageObjectList.this.player.getWurmId())) {
                            return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                        }
                        if (param1.isActualOwner(ManageObjectList.this.player.getWurmId())) {
                            return -1 * upDown;
                        }
                        return 1 * upDown;
                    }
                });
                break;
            }
            case 3: {
                Arrays.sort(structures, new Comparator<Structure>() {
                    @Override
                    public int compare(final Structure param1, final Structure param2) {
                        final int value1 = (param1.getAllDoors().length == 0) ? 0 : (param1.isLockable() ? 1 : 2);
                        final int value2 = (param2.getAllDoors().length == 0) ? 0 : (param2.isLockable() ? 1 : 2);
                        if (value1 == value2) {
                            return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                        }
                        if (value1 < value2) {
                            return 1 * upDown;
                        }
                        return -1 * upDown;
                    }
                });
                break;
            }
            case 4: {
                Arrays.sort(structures, new Comparator<Structure>() {
                    @Override
                    public int compare(final Structure param1, final Structure param2) {
                        final int value1 = (param1.getVillage() != null) ? param1.getVillage().getId() : 0;
                        final int value2 = (param2.getVillage() != null) ? param2.getVillage().getId() : 0;
                        if (value1 == value2) {
                            return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                        }
                        if (value1 < value2) {
                            return 1 * upDown;
                        }
                        return -1 * upDown;
                    }
                });
                break;
            }
            case 5: {
                Arrays.sort(structures, new Comparator<Structure>() {
                    @Override
                    public int compare(final Structure param1, final Structure param2) {
                        if (param1.isManaged() == param2.isManaged()) {
                            return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                        }
                        if (param1.isManaged()) {
                            return -1 * upDown;
                        }
                        return 1 * upDown;
                    }
                });
                break;
            }
        }
        for (final Structure structure : structures) {
            buf.append((structure.canHavePermissions() ? ("radio{group=\"sel\";id=\"" + structure.getWurmId() + "\";text=\"\"}") : "label{text=\"\"};") + "label{text=\"" + structure.getObjectName() + "\"};label{" + this.showBoolean(structure.isActualOwner(this.player.getWurmId())) + "};");
            if (structure.getAllDoors().length == 0) {
                buf.append("label{color=\"255,177,40\"text=\"No lockable doors.\"};");
            }
            else if (structure.isLockable()) {
                buf.append("label{color=\"127,255,127\"text=\"true\"};");
            }
            else {
                buf.append("label{color=\"255,127,127\"text=\"Not all doors have locks.\"};");
            }
            buf.append("label{" + this.showBoolean(structure.getVillage() != null) + "};");
            buf.append("label{" + this.showBoolean(structure.isManaged()) + "};");
            if (structure.isOwner(this.player.getWurmId())) {
                buf.append("harray{label{text=\" \"};button{text=\"Demolish\";id=\"demolish" + structure.getWurmId() + "\";confirm=\"You are about to blow up the building '" + structure.getObjectName() + "'.\";question=\"Do you really want to do that?\"}label{text=\" \"}}");
            }
            else {
                buf.append("label{text=\" \"}");
            }
        }
        buf.append("}");
        return buf.toString();
    }
    
    private String makeDoorList() {
        final StringBuilder buf = new StringBuilder();
        try {
            final Structure structure = Structures.getStructure(this.target);
            buf.append("text{text=\"List includes all doors in this building if you are the owner, or any doors in this building that have the 'Building may manage' Permission so long as you have the 'Manage Permissions' building permission.\"}");
            buf.append("text{text=\"Note: Owner of the Door is the Owner of the bulding.\"}");
            buf.append("text{text=\"\"}");
            buf.append("text{type=\"bold\";text=\"List of Doors that you may manage in this building.\"}");
            if (this.includeAll) {
                buf.append(this.extraButton("Exclude Doors without locks", "exc"));
            }
            else {
                buf.append(this.extraButton("Include Doors without locks", "inc"));
            }
            final Door[] doors = structure.getAllDoors(this.includeAll);
            final int absSortBy = Math.abs(this.sortBy);
            final int upDown = Integer.signum(this.sortBy);
            buf.append("table{rows=\"1\";cols=\"7\";label{text=\"\"};" + this.colHeader("Name", 1, this.sortBy) + this.colHeader("Door Type", 2, this.sortBy) + this.colHeader("Level", 3, this.sortBy) + this.colHeader("Has Lock?", 4, this.sortBy) + this.colHeader("Locked?", 5, this.sortBy) + this.colHeader("Building Managed?", 6, this.sortBy));
            Arrays.sort(doors, new Comparator<Door>() {
                @Override
                public int compare(final Door param1, final Door param2) {
                    if (param1.getFloorLevel() == param2.getFloorLevel()) {
                        final int comp = param1.getTypeName().compareTo(param2.getTypeName());
                        if (comp == 0) {
                            return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                        }
                        return comp * upDown;
                    }
                    else {
                        if (param1.getFloorLevel() < param2.getFloorLevel()) {
                            return 1 * upDown;
                        }
                        return -1 * upDown;
                    }
                }
            });
            switch (absSortBy) {
                case 1: {
                    Arrays.sort(doors, new Comparator<Door>() {
                        @Override
                        public int compare(final Door param1, final Door param2) {
                            return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                        }
                    });
                    break;
                }
                case 2: {
                    Arrays.sort(doors, new Comparator<Door>() {
                        @Override
                        public int compare(final Door param1, final Door param2) {
                            return param1.getTypeName().compareTo(param2.getTypeName()) * upDown;
                        }
                    });
                    break;
                }
                case 3: {
                    Arrays.sort(doors, new Comparator<Door>() {
                        @Override
                        public int compare(final Door param1, final Door param2) {
                            if (param1.getFloorLevel() == param2.getFloorLevel()) {
                                return 0;
                            }
                            if (param1.getFloorLevel() < param2.getFloorLevel()) {
                                return 1 * upDown;
                            }
                            return -1 * upDown;
                        }
                    });
                    break;
                }
                case 4: {
                    Arrays.sort(doors, new Comparator<Door>() {
                        @Override
                        public int compare(final Door param1, final Door param2) {
                            if (param1.hasLock() == param2.hasLock()) {
                                return 0;
                            }
                            if (param1.hasLock()) {
                                return -1 * upDown;
                            }
                            return 1 * upDown;
                        }
                    });
                    break;
                }
                case 5: {
                    Arrays.sort(doors, new Comparator<Door>() {
                        @Override
                        public int compare(final Door param1, final Door param2) {
                            if (param1.isLocked() == param2.isLocked()) {
                                return 0;
                            }
                            if (param1.isLocked()) {
                                return -1 * upDown;
                            }
                            return 1 * upDown;
                        }
                    });
                    break;
                }
                case 6: {
                    Arrays.sort(doors, new Comparator<Door>() {
                        @Override
                        public int compare(final Door param1, final Door param2) {
                            if (param1.isManaged() == param2.isManaged()) {
                                return 0;
                            }
                            if (param1.isManaged()) {
                                return -1 * upDown;
                            }
                            return 1 * upDown;
                        }
                    });
                    break;
                }
            }
            for (final Door door : doors) {
                buf.append((door.canHavePermissions() ? ("radio{group=\"sel\";id=\"" + door.getWurmId() + "\";text=\"\"}") : "label{text=\"\"}") + "label{text=\"" + door.getObjectName() + "\"};label{text=\"" + door.getTypeName() + "\"};label{text=\"" + door.getFloorLevel() + "\"};label{" + this.showBoolean(door.hasLock()) + "};label{" + this.showBoolean(door.isLocked()) + "};label{" + this.showBoolean(door.isManaged()) + "};");
            }
            buf.append("}");
        }
        catch (NoSuchStructureException nsse) {
            ManageObjectList.logger.log(Level.WARNING, "Cannot find structure, it was here a minute ago! Id:" + this.target, nsse);
            buf.append("text{text=\"Cannot find structure, it was here a minute ago!\"}");
        }
        return buf.toString();
    }
    
    private String makeGateList() {
        final StringBuilder buf = new StringBuilder();
        buf.append("text{text=\"As well as the list containing any gates that you are the owner of their lock it also includes any gate that have 'Settlement may manage' Permission set to your village so long as you have the 'Manage Allowed Objects' settlement permission.\"}");
        final Village vill = this.getResponder().getCitizenVillage();
        if (vill != null && vill.isMayor(this.player)) {
            buf.append("text{text=\"As you are a mayor, the list will have all gates on your deed.\"}");
        }
        buf.append("text{text=\"\"}");
        if (this.includeAll) {
            buf.append(this.extraButton("Exclude Gates without locks", "exc"));
        }
        else {
            buf.append(this.extraButton("Include Gates without locks", "inc"));
        }
        final int vid = (vill != null && vill.getRoleFor(this.getResponder()).mayManageAllowedObjects()) ? vill.getId() : -1;
        final FenceGate[] gates = FenceGate.getManagedGatesFor(this.player, vid, this.includeAll);
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        buf.append("table{rows=\"1\";cols=\"9\";label{text=\"\"};" + this.colHeader("Name", 1, this.sortBy) + this.colHeader("Gate Type", 2, this.sortBy) + this.colHeader("Level", 3, this.sortBy) + this.colHeader("Has Lock?", 4, this.sortBy) + this.colHeader("Locked?", 5, this.sortBy) + this.colHeader("Owner?", 6, this.sortBy) + this.colHeader("On Deed?", 7, this.sortBy) + this.colHeader("Deed Managed?", 8, this.sortBy));
        Arrays.sort(gates, new Comparator<FenceGate>() {
            @Override
            public int compare(final FenceGate param1, final FenceGate param2) {
                if (param1.getFloorLevel() == param2.getFloorLevel()) {
                    final int comp = param1.getTypeName().compareTo(param2.getTypeName());
                    if (comp == 0) {
                        return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                    }
                    return comp * upDown;
                }
                else {
                    if (param1.getFloorLevel() < param2.getFloorLevel()) {
                        return 1 * upDown;
                    }
                    return -1 * upDown;
                }
            }
        });
        switch (absSortBy) {
            case 1: {
                Arrays.sort(gates, new Comparator<FenceGate>() {
                    @Override
                    public int compare(final FenceGate param1, final FenceGate param2) {
                        return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                    }
                });
                break;
            }
            case 2: {
                Arrays.sort(gates, new Comparator<FenceGate>() {
                    @Override
                    public int compare(final FenceGate param1, final FenceGate param2) {
                        return param1.getTypeName().compareTo(param2.getTypeName()) * upDown;
                    }
                });
                break;
            }
            case 3: {
                Arrays.sort(gates, new Comparator<FenceGate>() {
                    @Override
                    public int compare(final FenceGate param1, final FenceGate param2) {
                        if (param1.getFloorLevel() == param2.getFloorLevel()) {
                            return 0;
                        }
                        if (param1.getFloorLevel() < param2.getFloorLevel()) {
                            return 1 * upDown;
                        }
                        return -1 * upDown;
                    }
                });
                break;
            }
            case 4: {
                Arrays.sort(gates, new Comparator<FenceGate>() {
                    @Override
                    public int compare(final FenceGate param1, final FenceGate param2) {
                        if (param1.hasLock() == param2.hasLock()) {
                            return 0;
                        }
                        if (param1.hasLock()) {
                            return -1 * upDown;
                        }
                        return 1 * upDown;
                    }
                });
                break;
            }
            case 5: {
                Arrays.sort(gates, new Comparator<FenceGate>() {
                    @Override
                    public int compare(final FenceGate param1, final FenceGate param2) {
                        if (param1.isLocked() == param2.isLocked()) {
                            return 0;
                        }
                        if (param1.isLocked()) {
                            return -1 * upDown;
                        }
                        return 1 * upDown;
                    }
                });
                break;
            }
            case 6: {
                Arrays.sort(gates, new Comparator<FenceGate>() {
                    @Override
                    public int compare(final FenceGate param1, final FenceGate param2) {
                        if (param1.isActualOwner(ManageObjectList.this.player.getWurmId()) == param2.isActualOwner(ManageObjectList.this.player.getWurmId())) {
                            return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                        }
                        if (param1.isActualOwner(ManageObjectList.this.player.getWurmId())) {
                            return -1 * upDown;
                        }
                        return 1 * upDown;
                    }
                });
                break;
            }
            case 7: {
                Arrays.sort(gates, new Comparator<FenceGate>() {
                    @Override
                    public int compare(final FenceGate param1, final FenceGate param2) {
                        final int value1 = (param1.getVillage() != null) ? param1.getVillage().getId() : 0;
                        final int value2 = (param2.getVillage() != null) ? param2.getVillage().getId() : 0;
                        if (value1 == value2) {
                            return 0;
                        }
                        if (value1 < value2) {
                            return -1 * upDown;
                        }
                        return 1 * upDown;
                    }
                });
                break;
            }
            case 8: {
                Arrays.sort(gates, new Comparator<FenceGate>() {
                    @Override
                    public int compare(final FenceGate param1, final FenceGate param2) {
                        if (param1.isManaged() == param2.isManaged()) {
                            return 0;
                        }
                        if (param1.isManaged()) {
                            return -1 * upDown;
                        }
                        return 1 * upDown;
                    }
                });
                break;
            }
        }
        for (final FenceGate gate : gates) {
            buf.append((gate.canHavePermissions() ? ("radio{group=\"sel\";id=\"" + gate.getWurmId() + "\";text=\"\"}") : "label{text=\"\"}") + "label{text=\"" + gate.getObjectName() + "\"};label{text=\"" + gate.getTypeName() + "\"};label{text=\"" + gate.getFloorLevel() + "\"};label{" + this.showBoolean(gate.hasLock()) + "};label{" + this.showBoolean(gate.isLocked()) + "};label{" + this.showBoolean(gate.isActualOwner(this.player.getWurmId())) + "};label{" + this.showBoolean(gate.getVillage() != null) + "};label{" + this.showBoolean(gate.isManaged()) + "};");
        }
        buf.append("}");
        return buf.toString();
    }
    
    private String makeLandVehicleList() {
        final StringBuilder buf = new StringBuilder();
        buf.append("text{text=\"List contains the Small Carts, Large Carts, Wagons and Carriers that you can manage.\"}");
        buf.append("text{text=\"\"}");
        buf.append("text{type=\"bold\";text=\"List of Small Carts, Large Carts, Wagons and Carriers that you may manage.\"}");
        if (this.includeAll) {
            buf.append(this.extraButton("Exclude Vehicles without locks", "exc"));
        }
        else {
            buf.append(this.extraButton("Include Vehicles without locks", "inc"));
        }
        final Item[] items = Items.getManagedCartsFor(this.player, this.includeAll);
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        buf.append("table{rows=\"1\";cols=\"6\";label{text=\"\"};" + this.colHeader("Name", 1, this.sortBy) + this.colHeader("Type", 2, this.sortBy) + this.colHeader("Owner?", 3, this.sortBy) + this.colHeader("Locked?", 4, this.sortBy) + "label{type=\"bold\";text=\"\"};");
        switch (absSortBy) {
            case 1: {
                Arrays.sort(items, new Comparator<Item>() {
                    @Override
                    public int compare(final Item param1, final Item param2) {
                        return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                    }
                });
                break;
            }
            case 2: {
                Arrays.sort(items, new Comparator<Item>() {
                    @Override
                    public int compare(final Item param1, final Item param2) {
                        return param1.getTypeName().compareTo(param2.getTypeName()) * upDown;
                    }
                });
                break;
            }
            case 3: {
                Arrays.sort(items, new Comparator<Item>() {
                    @Override
                    public int compare(final Item param1, final Item param2) {
                        if (param1.isActualOwner(ManageObjectList.this.player.getWurmId()) == param2.isActualOwner(ManageObjectList.this.player.getWurmId())) {
                            return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                        }
                        if (param1.isActualOwner(ManageObjectList.this.player.getWurmId())) {
                            return -1 * upDown;
                        }
                        return 1 * upDown;
                    }
                });
                break;
            }
            case 4: {
                Arrays.sort(items, new Comparator<Item>() {
                    @Override
                    public int compare(final Item param1, final Item param2) {
                        if (param1.isLocked() == param2.isLocked()) {
                            return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                        }
                        if (param1.isLocked()) {
                            return -1 * upDown;
                        }
                        return 1 * upDown;
                    }
                });
                break;
            }
        }
        for (final Item item : items) {
            buf.append((item.canHavePermissions() ? ("radio{group=\"sel\";id=\"" + item.getWurmId() + "\";text=\"\"}") : "label{text=\"\"}") + "label{text=\"" + item.getObjectName() + "\"};" + addRariryColour(item, item.getTypeName()) + "label{" + this.showBoolean(item.isActualOwner(this.player.getWurmId())) + "};label{" + this.showBoolean(item.isLocked()) + "};label{text=\"\"};");
        }
        buf.append("}");
        return buf.toString();
    }
    
    private String makeMineDoorList() {
        final Village vill = this.getResponder().getCitizenVillage();
        final StringBuilder buf = new StringBuilder();
        buf.append("text{text=\"As well as the list containing any mine doors that you are the owner of it also includes any mine doors that have 'Settlement may manage' Permission set to your village so long as you have the 'Manage Allowed Objects' settlement permission.\"}");
        if (vill != null && vill.isMayor(this.player)) {
            buf.append("text{text=\"As you are a mayor, the list will have all minedoors on your deed.\"}");
        }
        buf.append("text{text=\"\"}");
        final int vid = (vill != null && vill.getRoleFor(this.getResponder()).mayManageAllowedObjects()) ? vill.getId() : -1;
        final MineDoorPermission[] mineDoors = MineDoorPermission.getManagedMineDoorsFor(this.player, vid, this.includeAll);
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        buf.append("table{rows=\"1\";cols=\"7\";label{text=\"\"};" + this.colHeader("Name", 1, this.sortBy) + this.colHeader("Door Type", 2, this.sortBy) + this.colHeader("Owner?", 3, this.sortBy) + this.colHeader("On Deed?", 4, this.sortBy) + this.colHeader("Deed Managed?", 5, this.sortBy) + "label{type=\"bold\";text=\"\"};");
        switch (absSortBy) {
            case 1: {
                Arrays.sort(mineDoors, new Comparator<MineDoorPermission>() {
                    @Override
                    public int compare(final MineDoorPermission param1, final MineDoorPermission param2) {
                        return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                    }
                });
                break;
            }
            case 2: {
                Arrays.sort(mineDoors, new Comparator<MineDoorPermission>() {
                    @Override
                    public int compare(final MineDoorPermission param1, final MineDoorPermission param2) {
                        return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                    }
                });
                break;
            }
            case 3: {
                Arrays.sort(mineDoors, new Comparator<MineDoorPermission>() {
                    @Override
                    public int compare(final MineDoorPermission param1, final MineDoorPermission param2) {
                        if (param1.isActualOwner(ManageObjectList.this.player.getWurmId()) == param2.isActualOwner(ManageObjectList.this.player.getWurmId())) {
                            return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                        }
                        if (param1.isActualOwner(ManageObjectList.this.player.getWurmId())) {
                            return -1 * upDown;
                        }
                        return 1 * upDown;
                    }
                });
                break;
            }
            case 4: {
                Arrays.sort(mineDoors, new Comparator<MineDoorPermission>() {
                    @Override
                    public int compare(final MineDoorPermission param1, final MineDoorPermission param2) {
                        final int value1 = (param1.getVillage() != null) ? param1.getVillage().getId() : 0;
                        final int value2 = (param2.getVillage() != null) ? param2.getVillage().getId() : 0;
                        if (value1 == value2) {
                            return 0;
                        }
                        if (value1 < value2) {
                            return -1 * upDown;
                        }
                        return 1 * upDown;
                    }
                });
                break;
            }
            case 5: {
                Arrays.sort(mineDoors, new Comparator<MineDoorPermission>() {
                    @Override
                    public int compare(final MineDoorPermission param1, final MineDoorPermission param2) {
                        if (param1.isManaged() == param2.isManaged()) {
                            return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                        }
                        if (param1.isManaged()) {
                            return -1 * upDown;
                        }
                        return 1 * upDown;
                    }
                });
                break;
            }
        }
        for (final MineDoorPermission mineDoor : mineDoors) {
            buf.append((mineDoor.canHavePermissions() ? ("radio{group=\"sel\";id=\"" + mineDoor.getWurmId() + "\";text=\"\"}") : "label{text=\"\"}") + "label{text=\"" + mineDoor.getObjectName() + "\"};label{text=\"" + mineDoor.getTypeName() + "\"};label{" + this.showBoolean(mineDoor.isActualOwner(this.player.getWurmId())) + "};label{" + this.showBoolean(mineDoor.getVillage() != null) + "};label{" + this.showBoolean(mineDoor.isManaged()) + "};label{text=\" \"}");
        }
        buf.append("}");
        return buf.toString();
    }
    
    private String makeShipList() {
        final StringBuilder buf = new StringBuilder();
        buf.append("text{text=\"List contains the Ships that you can manage.\"}");
        buf.append("text{text=\"\"}");
        buf.append("text{type=\"bold\";text=\"Will have List of Ships that you may manage.\"}");
        if (this.includeAll) {
            buf.append(this.extraButton("Exclude ships without locks", "exc"));
        }
        else {
            buf.append(this.extraButton("Include ships without locks", "inc"));
        }
        final Item[] items = Items.getManagedShipsFor(this.player, this.includeAll);
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        buf.append("table{rows=\"1\";cols=\"6\";label{text=\"\"};" + this.colHeader("Name", 1, this.sortBy) + this.colHeader("Type", 2, this.sortBy) + this.colHeader("Owner?", 3, this.sortBy) + this.colHeader("Locked?", 4, this.sortBy) + "label{type=\"bold\";text=\"\"};");
        switch (absSortBy) {
            case 1: {
                Arrays.sort(items, new Comparator<Item>() {
                    @Override
                    public int compare(final Item param1, final Item param2) {
                        return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                    }
                });
                break;
            }
            case 2: {
                Arrays.sort(items, new Comparator<Item>() {
                    @Override
                    public int compare(final Item param1, final Item param2) {
                        return param1.getTypeName().compareTo(param2.getTypeName()) * upDown;
                    }
                });
                break;
            }
            case 3: {
                Arrays.sort(items, new Comparator<Item>() {
                    @Override
                    public int compare(final Item param1, final Item param2) {
                        if (param1.isActualOwner(ManageObjectList.this.player.getWurmId()) == param2.isActualOwner(ManageObjectList.this.player.getWurmId())) {
                            return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                        }
                        if (param1.isActualOwner(ManageObjectList.this.player.getWurmId())) {
                            return -1 * upDown;
                        }
                        return 1 * upDown;
                    }
                });
                break;
            }
            case 4: {
                Arrays.sort(items, new Comparator<Item>() {
                    @Override
                    public int compare(final Item param1, final Item param2) {
                        if (param1.isLocked() == param2.isLocked()) {
                            return param1.getObjectName().compareTo(param2.getObjectName()) * upDown;
                        }
                        if (param1.isLocked()) {
                            return -1 * upDown;
                        }
                        return 1 * upDown;
                    }
                });
                break;
            }
        }
        for (final Item item : items) {
            buf.append((item.canHavePermissions() ? ("radio{group=\"sel\";id=\"" + item.getWurmId() + "\";text=\"\"}") : "label{text=\"\"}") + "label{text=\"" + item.getObjectName() + "\"};" + addRariryColour(item, item.getTypeName()) + "label{" + this.showBoolean(item.isActualOwner(this.player.getWurmId())) + "};label{" + this.showBoolean(item.isLocked()) + "};label{text=\"\"};");
        }
        buf.append("}");
        return buf.toString();
    }
    
    private String makeWagonerList() {
        final StringBuilder buf = new StringBuilder();
        final int vid = -1;
        final Creature[] animals = Creatures.getManagedWagonersFor(this.player, -1);
        final int absSortBy = Math.abs(this.sortBy);
        final int upDown = Integer.signum(this.sortBy);
        buf.append("table{rows=\"1\";cols=\"6\";label{text=\"\"};" + this.colHeader("Name", 1, this.sortBy) + this.colHeader("State", 2, this.sortBy) + this.colHeader("Queue", 3, this.sortBy) + "label{text=\"\"};label{text=\"\"};");
        switch (absSortBy) {
            case 1: {
                Arrays.sort(animals, new Comparator<Creature>() {
                    @Override
                    public int compare(final Creature param1, final Creature param2) {
                        return param1.getName().compareTo(param2.getName()) * upDown;
                    }
                });
                break;
            }
            case 2: {
                Arrays.sort(animals, new Comparator<Creature>() {
                    @Override
                    public int compare(final Creature param1, final Creature param2) {
                        return param1.getWagoner().getStateName().compareTo(param2.getWagoner().getStateName()) * upDown;
                    }
                });
                break;
            }
            case 3: {
                Arrays.sort(animals, new Comparator<Creature>() {
                    @Override
                    public int compare(final Creature param1, final Creature param2) {
                        final int value1 = param1.getWagoner().getQueueLength();
                        final int value2 = param2.getWagoner().getQueueLength();
                        if (value1 == value2) {
                            return 0;
                        }
                        if (value1 < value2) {
                            return -1 * upDown;
                        }
                        return 1 * upDown;
                    }
                });
                break;
            }
        }
        for (final Creature animal : animals) {
            final Wagoner wagoner = animal.getWagoner();
            final int queueLength = Delivery.getQueueLength(wagoner.getWurmId());
            buf.append((animal.canHavePermissions() ? ("radio{group=\"sel\";id=\"" + animal.getWurmId() + "\";text=\"\"}") : "label{text=\"\"};") + "label{text=\"" + animal.getName() + "\"};label{text=\"" + wagoner.getStateName() + "\"};" + ((queueLength == 0) ? "label{text=\"empty\"};" : ("label{text=\"" + queueLength + "\"};")) + "label{text=\"  \"};");
            if (animal.mayManage(this.getResponder())) {
                if (wagoner.getVillageId() == -1) {
                    buf.append("label{\"Dismissing.\"};");
                }
                else {
                    buf.append(this.dismissButton(animal));
                }
            }
            else {
                buf.append("label{\"\"};");
            }
        }
        buf.append("}");
        return buf.toString();
    }
    
    private String dismissButton(final Creature animal) {
        final StringBuilder buf = new StringBuilder();
        buf.append("harray{button{text=\"Dismiss\";id=\"dismiss" + animal.getWurmId() + "\";}label{text=\" \"}}");
        return buf.toString();
    }
    
    private String extraButton(final String txt, final String id) {
        final StringBuilder buf = new StringBuilder();
        buf.append("harray{label{text=\"Filter list:\"};button{text=\"" + txt + "\";id=\"" + id + "\"}};");
        return buf.toString();
    }
    
    public static String addRariryColour(final Item item, final String name) {
        final StringBuilder buf = new StringBuilder();
        if (item.getRarity() == 1) {
            buf.append("label{color=\"66,153,225\";text=\"rare " + name + "\"};");
        }
        else if (item.getRarity() == 2) {
            buf.append("label{color=\"0,255,255\";text=\"supreme " + name + "\"};");
        }
        else if (item.getRarity() == 3) {
            buf.append("label{color=\"255,0,255\";text=\"fantastic " + name + "\"};");
        }
        else {
            buf.append("label{text=\"" + name + "\"};");
        }
        return buf.toString();
    }
    
    private String showBoolean(final boolean flag) {
        final StringBuilder buf = new StringBuilder();
        if (flag) {
            buf.append("color=\"127,255,127\"");
        }
        else {
            buf.append("color=\"255,127,127\"");
        }
        buf.append("text=\"" + flag + "\"");
        return buf.toString();
    }
    
    static {
        logger = Logger.getLogger(ManageObjectList.class.getName());
    }
    
    public enum Type
    {
        ANIMAL0("Animal", (Permissions.IPermission[])AnimalSettings.Animal0Permissions.values()), 
        ANIMAL1("Animal", (Permissions.IPermission[])AnimalSettings.Animal1Permissions.values()), 
        ANIMAL2("Animal", (Permissions.IPermission[])AnimalSettings.Animal2Permissions.values()), 
        WAGONER("Wagoner", (Permissions.IPermission[])AnimalSettings.WagonerPermissions.values()), 
        DELIVERY("Wagoner", (Permissions.IPermission[])AnimalSettings.WagonerPermissions.values()), 
        BUILDING("Building", (Permissions.IPermission[])StructureSettings.StructurePermissions.values()), 
        LARGE_CART("Large Cart", (Permissions.IPermission[])ItemSettings.VehiclePermissions.values()), 
        DOOR("Door", (Permissions.IPermission[])DoorSettings.DoorPermissions.values()), 
        GATE("Gate", (Permissions.IPermission[])DoorSettings.GatePermissions.values()), 
        MINEDOOR("Minedoor", (Permissions.IPermission[])MineDoorSettings.MinedoorPermissions.values()), 
        SHIP("Ship", (Permissions.IPermission[])ItemSettings.VehiclePermissions.values()), 
        WAGON("Wagon", (Permissions.IPermission[])ItemSettings.WagonPermissions.values()), 
        SHIP_CARRIER("Ship Carrier", (Permissions.IPermission[])ItemSettings.ShipTransporterPermissions.values()), 
        CREATURE_CARRIER("Creature Carrier", (Permissions.IPermission[])ItemSettings.CreatureTransporterPermissions.values()), 
        SMALL_CART("Small Cart", (Permissions.IPermission[])ItemSettings.SmallCartPermissions.values()), 
        ITEM("Item", (Permissions.IPermission[])ItemSettings.ItemPermissions.values()), 
        BED("Bed", (Permissions.IPermission[])ItemSettings.BedPermissions.values()), 
        MESSAGE_BOARD("Village Message Board", (Permissions.IPermission[])ItemSettings.MessageBoardPermissions.values()), 
        CORPSE("Corpse", (Permissions.IPermission[])ItemSettings.CorpsePermissions.values()), 
        SEARCH("Search", (Permissions.IPermission[])null), 
        REPLY("Reply", (Permissions.IPermission[])null);
        
        private final String title;
        private final Permissions.IPermission[] enumValues;
        
        private Type(final String aTitle, final Permissions.IPermission[] values) {
            this.title = aTitle;
            this.enumValues = values;
        }
        
        public String getTitle() {
            return this.title;
        }
        
        public Permissions.IPermission[] getEnumValues() {
            return this.enumValues;
        }
    }
}
