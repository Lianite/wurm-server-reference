// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.server.behaviours.NoSuchBehaviourException;
import com.wurmonline.server.behaviours.Behaviours;
import com.wurmonline.server.behaviours.Behaviour;
import com.wurmonline.server.Servers;
import com.wurmonline.server.skills.NoSuchSkillException;
import com.wurmonline.shared.util.StringUtilities;
import com.wurmonline.shared.util.MaterialUtilities;
import com.wurmonline.server.deities.Deities;
import java.util.logging.Level;
import java.util.Arrays;
import java.util.ArrayList;
import com.wurmonline.server.deities.Deity;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.ItemMaterials;
import com.wurmonline.server.MiscConstants;

public final class ItemTemplate implements MiscConstants, ItemMaterials, ItemTypes, ItemSizes, Comparable<ItemTemplate>
{
    public long damUpdates;
    public long maintUpdates;
    boolean isDuelRing;
    private final String name;
    private final short behaviourType;
    public final short imageNumber;
    private final String itemDescriptionLong;
    private final String itemDescriptionSuperb;
    private final String itemDescriptionNormal;
    private final String itemDescriptionBad;
    private final String itemDescriptionRotten;
    private String concatName;
    final int templateId;
    private final long decayTime;
    public float priceHalfSize;
    private final int centimetersX;
    private final int centimetersY;
    private final int centimetersZ;
    private boolean usesSpecifiedContainerSizes;
    private int containerCentimetersX;
    private int containerCentimetersY;
    private int containerCentimetersZ;
    private boolean calcNutritionValues;
    private short calories;
    private short carbs;
    private short fats;
    private short proteins;
    private int grows;
    private int crushsTo;
    private int pickSeeds;
    private int alcoholStrength;
    private boolean canBeCookingOil;
    private boolean useRealTemplateIcon;
    private boolean canBeRawWrapped;
    private boolean canBePapyrusWrapped;
    private boolean canBeClothWrapped;
    private boolean surfaceonly;
    private int dyePrimaryAmountRequired;
    private int dyeSecondaryAmountRequired;
    private String secondaryItemName;
    private final int volume;
    private int containerVolume;
    private final int primarySkill;
    private final byte[] bodySpaces;
    private final int combatDamage;
    private final String modelName;
    private final float difficulty;
    private static final Logger logger;
    private final int weight;
    private final byte material;
    private final int value;
    private final boolean isPurchased;
    private final String plural;
    private final int size;
    private boolean isSharp;
    boolean hollow;
    boolean weaponslash;
    boolean shield;
    boolean armour;
    boolean food;
    boolean magic;
    boolean magicContainer;
    boolean fieldtool;
    boolean bodypart;
    boolean inventory;
    boolean inventoryGroup;
    boolean unstableRift;
    boolean miningtool;
    boolean carpentrytool;
    boolean smithingtool;
    boolean weaponpierce;
    boolean weaponcrush;
    boolean weaponaxe;
    boolean weaponsword;
    boolean weaponPolearm;
    boolean weaponknife;
    boolean weaponmisc;
    boolean rechargeable;
    boolean bow;
    public boolean bowUnstringed;
    boolean diggingtool;
    boolean seed;
    boolean liquid;
    boolean melting;
    boolean meat;
    boolean sign;
    boolean fence;
    boolean streetlamp;
    boolean vegetable;
    boolean wood;
    private boolean metal;
    boolean stone;
    boolean notrade;
    boolean visibleDecay;
    boolean leather;
    boolean cloth;
    boolean paper;
    boolean pottery;
    boolean notake;
    boolean light;
    boolean containerliquid;
    boolean liquidinflammable;
    boolean weaponmelee;
    boolean fish;
    boolean weapon;
    boolean tool;
    boolean lock;
    boolean indestructible;
    boolean key;
    boolean nodrop;
    boolean repairable;
    boolean lockable;
    boolean temporary;
    boolean combine;
    private boolean canHaveInscription;
    boolean hasdata;
    boolean hasExtraData;
    boolean viewableSubItems;
    boolean isContainerWithSubItems;
    boolean outsideonly;
    boolean coin;
    boolean turnable;
    boolean decoration;
    boolean fullprice;
    boolean norename;
    private boolean nonutrition;
    private boolean lownutrition;
    private boolean mediumnutrition;
    private boolean goodnutrition;
    private boolean highnutrition;
    public boolean isDish;
    private boolean isFoodMaker;
    boolean herb;
    boolean spice;
    boolean fruit;
    boolean poison;
    boolean draggable;
    boolean villagedeed;
    boolean farwalkerItem;
    boolean homesteaddeed;
    boolean alwayspoll;
    boolean protectionTower;
    boolean floating;
    boolean isButcheredItem;
    boolean isNoPut;
    boolean isLeadCreature;
    boolean isLeadMultipleCreatures;
    boolean isFire;
    private boolean isCarpet;
    boolean domainItem;
    boolean useOnGroundOnly;
    boolean holyItem;
    boolean hugeAltar;
    public boolean artifact;
    public boolean unique;
    boolean destroysHugeAltar;
    boolean passFullData;
    boolean isForm;
    boolean descIsExam;
    boolean isServerBound;
    boolean isTwohanded;
    boolean kingdomMarker;
    boolean destroyable;
    boolean priceAffectedByMaterial;
    private boolean liquidCooking;
    boolean positiveDecay;
    boolean drinkable;
    public boolean isColor;
    boolean colorable;
    boolean gem;
    boolean egg;
    boolean newbieItem;
    boolean challengeNewbieItem;
    boolean isTileAligned;
    boolean isDragonArmour;
    boolean isCompass;
    boolean isToolbelt;
    boolean isBelt;
    boolean oilConsuming;
    boolean candleHolder;
    boolean flickeringLight;
    boolean namedCreator;
    boolean onePerTile;
    boolean fourPerTile;
    boolean tenPerTile;
    Deity deity;
    public String sizeString;
    int alchemyType;
    boolean healing;
    boolean bed;
    boolean insideOnly;
    boolean nobank;
    boolean isRecycled;
    public boolean alwaysLoaded;
    boolean brightLight;
    private boolean isVehicle;
    public boolean isChair;
    public boolean isCart;
    boolean isVehicleDragged;
    boolean isMovingItem;
    boolean isFlower;
    boolean isNaturePlantable;
    boolean isImproveItem;
    boolean isDeathProtection;
    public boolean isRoyal;
    public boolean isNoMove;
    public boolean isWind;
    public boolean isDredgingTool;
    public boolean isMineDoor;
    public boolean isNoSellBack;
    public boolean isSpringFilled;
    public boolean destroyOnDecay;
    public boolean isServerPortal;
    public boolean isTrap;
    public boolean isDisarmTrap;
    public boolean nonDeedable;
    boolean plantedFlowerpot;
    boolean ownerDestroyable;
    boolean wearableByCreaturesOnly;
    boolean puppet;
    boolean overrideNonEnchantable;
    boolean isMeditation;
    boolean isTransmutable;
    boolean bulkContainer;
    boolean bulk;
    boolean missions;
    boolean notMissions;
    boolean combineCold;
    boolean spawnsTrees;
    boolean killsTrees;
    boolean isKingdomFlag;
    boolean useMaterialAndKingdom;
    boolean isFlag;
    boolean isCrude;
    boolean minable;
    boolean isEnchantableJewelry;
    boolean isEpicTargetItem;
    boolean isEpicPortal;
    private boolean isMassProduction;
    boolean noWorkParent;
    boolean alwaysBankable;
    int improveItem;
    public boolean alwaysLit;
    public boolean isMushroom;
    public boolean isWarTarget;
    public boolean isSourceSpring;
    public boolean isSource;
    public boolean isColorComponent;
    boolean isTutorialItem;
    private boolean isEquipmentSlot;
    public boolean isOre;
    public boolean isShard;
    boolean isAbility;
    boolean isAltar;
    public boolean isBag;
    public boolean isQuiver;
    private boolean isMagicStaff;
    private boolean isTent;
    private boolean improveUsesTypeAsMaterial;
    private boolean noDiscard;
    private boolean instaDiscard;
    private boolean isTransportable;
    private boolean isWarmachine;
    private boolean hideAddToCreationWindow;
    private boolean isBrazier;
    private boolean isSmearable;
    private boolean isItemSpawn;
    private boolean noImprove;
    private boolean isTapestry;
    boolean isUnfinishedNoTake;
    boolean isMilk;
    boolean isCheese;
    boolean isOwnerTurnable;
    boolean isOwnerMoveable;
    boolean isUnfired;
    boolean isPlantable;
    boolean isPlantOneAWeek;
    boolean isRiftItem;
    private boolean isHitchTarget;
    private boolean isRiftAltar;
    private boolean isRiftLoot;
    private boolean hasItemBonus;
    private boolean isBracelet;
    private boolean isPotable;
    private boolean canBeGrownInPot;
    private boolean isAlcohol;
    private boolean isCrushable;
    private boolean hasSeeds;
    private boolean isCooker;
    private boolean isFoodGroup;
    private int inFoodGroup;
    private boolean isCookingTool;
    private boolean isRecipeItem;
    private boolean isNoCreate;
    private boolean usesFoodState;
    private boolean canBeFermented;
    private boolean canBeDistilled;
    private boolean canBeSealed;
    private boolean hovers;
    private boolean foodBonusHot;
    private boolean foodBonusCold;
    private boolean isHarvestable;
    private int harvestTo;
    private boolean isRune;
    private boolean canBePegged;
    private boolean decayOnDeed;
    private InitialContainer[] initialContainers;
    private boolean canShowRaw;
    private boolean cannotBeSpellTarget;
    private boolean isTrellis;
    private boolean containsIngredientsOnly;
    private boolean usesRealTemplate;
    private boolean canLarder;
    private boolean isInsulated;
    private boolean isGuardTower;
    private boolean isComponentItem;
    private boolean parentMustBeOnGround;
    private int maxItemCount;
    private int maxItemWeight;
    private boolean isRoadMarker;
    private boolean isPaveable;
    private boolean isCavePaveable;
    public boolean decorationWhenPlanted;
    private int fragmentAmount;
    boolean descIsName;
    boolean isNotRuneable;
    private boolean showsSlopes;
    private boolean supportsSecondryColor;
    private boolean createsWithLock;
    private boolean isFishingReel;
    private boolean isFishingLine;
    private boolean isFishingFloat;
    private boolean isFishingHook;
    private boolean isFishingBait;
    private ArrayList<ContainerRestriction> containerRestrictions;
    boolean isPluralName;
    
    ItemTemplate(final int aTemplateId, final int aSize, final String aName, final String aPlural, final String aItemDescriptionSuperb, final String aItemDescriptionNormal, final String aItemDescriptionBad, final String aItemDescriptionRotten, final String aItemDescriptionLong, final short[] aItemTypes, final short aImageNumber, final short aBehaviourType, final int aCombatDamage, final long aDecayTime, final int aCentimetersX, final int aCentimetersY, final int aCentimetersZ, final int aPrimarySkill, final byte[] aBodySpaces, final String aModelName, final float aDifficulty, final int aWeight, final byte aMaterial, final int aValue, final boolean aIsPurchased) {
        this.damUpdates = 0L;
        this.maintUpdates = 0L;
        this.isDuelRing = false;
        this.priceHalfSize = 100.0f;
        this.calcNutritionValues = true;
        this.calories = 1000;
        this.carbs = 150;
        this.fats = 40;
        this.proteins = 25;
        this.grows = 0;
        this.crushsTo = 0;
        this.pickSeeds = 0;
        this.alcoholStrength = 0;
        this.canBeCookingOil = false;
        this.useRealTemplateIcon = false;
        this.canBeRawWrapped = false;
        this.canBePapyrusWrapped = false;
        this.canBeClothWrapped = false;
        this.surfaceonly = false;
        this.dyePrimaryAmountRequired = 0;
        this.dyeSecondaryAmountRequired = 0;
        this.secondaryItemName = "";
        this.hollow = false;
        this.weaponslash = false;
        this.shield = false;
        this.armour = false;
        this.food = false;
        this.magic = false;
        this.magicContainer = false;
        this.fieldtool = false;
        this.bodypart = false;
        this.inventory = false;
        this.inventoryGroup = false;
        this.unstableRift = false;
        this.miningtool = false;
        this.carpentrytool = false;
        this.smithingtool = false;
        this.weaponpierce = false;
        this.weaponcrush = false;
        this.weaponaxe = false;
        this.weaponsword = false;
        this.weaponPolearm = false;
        this.weaponknife = false;
        this.weaponmisc = false;
        this.rechargeable = false;
        this.bow = false;
        this.bowUnstringed = false;
        this.diggingtool = false;
        this.seed = false;
        this.liquid = false;
        this.melting = false;
        this.meat = false;
        this.sign = false;
        this.fence = false;
        this.streetlamp = false;
        this.vegetable = false;
        this.wood = false;
        this.metal = false;
        this.stone = false;
        this.notrade = false;
        this.visibleDecay = false;
        this.leather = false;
        this.cloth = false;
        this.paper = false;
        this.pottery = false;
        this.notake = false;
        this.light = false;
        this.containerliquid = false;
        this.liquidinflammable = false;
        this.weaponmelee = false;
        this.fish = false;
        this.weapon = false;
        this.tool = false;
        this.lock = false;
        this.indestructible = false;
        this.key = false;
        this.nodrop = false;
        this.repairable = false;
        this.lockable = false;
        this.temporary = false;
        this.combine = false;
        this.canHaveInscription = false;
        this.hasdata = false;
        this.hasExtraData = false;
        this.viewableSubItems = false;
        this.isContainerWithSubItems = false;
        this.outsideonly = false;
        this.coin = false;
        this.turnable = false;
        this.decoration = false;
        this.fullprice = false;
        this.norename = false;
        this.nonutrition = false;
        this.lownutrition = false;
        this.mediumnutrition = false;
        this.goodnutrition = false;
        this.highnutrition = false;
        this.isDish = false;
        this.isFoodMaker = false;
        this.herb = false;
        this.spice = false;
        this.fruit = false;
        this.poison = false;
        this.draggable = false;
        this.villagedeed = false;
        this.farwalkerItem = false;
        this.homesteaddeed = false;
        this.alwayspoll = false;
        this.protectionTower = false;
        this.floating = false;
        this.isButcheredItem = false;
        this.isNoPut = false;
        this.isLeadCreature = false;
        this.isLeadMultipleCreatures = false;
        this.isFire = false;
        this.isCarpet = false;
        this.domainItem = false;
        this.useOnGroundOnly = false;
        this.holyItem = false;
        this.hugeAltar = false;
        this.artifact = false;
        this.unique = false;
        this.destroysHugeAltar = false;
        this.passFullData = false;
        this.isForm = false;
        this.descIsExam = false;
        this.isServerBound = false;
        this.isTwohanded = false;
        this.kingdomMarker = false;
        this.destroyable = false;
        this.priceAffectedByMaterial = false;
        this.liquidCooking = false;
        this.positiveDecay = false;
        this.drinkable = false;
        this.isColor = false;
        this.colorable = false;
        this.gem = false;
        this.egg = false;
        this.newbieItem = false;
        this.challengeNewbieItem = false;
        this.isTileAligned = false;
        this.isDragonArmour = false;
        this.isCompass = false;
        this.isToolbelt = false;
        this.isBelt = false;
        this.oilConsuming = false;
        this.candleHolder = false;
        this.flickeringLight = false;
        this.namedCreator = false;
        this.onePerTile = false;
        this.fourPerTile = false;
        this.tenPerTile = false;
        this.deity = null;
        this.sizeString = "";
        this.alchemyType = 0;
        this.healing = false;
        this.bed = false;
        this.insideOnly = false;
        this.nobank = false;
        this.isRecycled = false;
        this.alwaysLoaded = false;
        this.brightLight = false;
        this.isVehicle = false;
        this.isChair = false;
        this.isCart = false;
        this.isVehicleDragged = false;
        this.isMovingItem = false;
        this.isFlower = false;
        this.isNaturePlantable = false;
        this.isImproveItem = false;
        this.isDeathProtection = false;
        this.isRoyal = false;
        this.isNoMove = false;
        this.isWind = false;
        this.isDredgingTool = false;
        this.isMineDoor = false;
        this.isNoSellBack = false;
        this.isSpringFilled = false;
        this.destroyOnDecay = false;
        this.isServerPortal = false;
        this.isTrap = false;
        this.isDisarmTrap = false;
        this.nonDeedable = false;
        this.plantedFlowerpot = false;
        this.ownerDestroyable = false;
        this.wearableByCreaturesOnly = false;
        this.puppet = false;
        this.overrideNonEnchantable = false;
        this.isMeditation = false;
        this.isTransmutable = false;
        this.bulkContainer = false;
        this.bulk = false;
        this.missions = false;
        this.notMissions = false;
        this.combineCold = false;
        this.spawnsTrees = false;
        this.killsTrees = false;
        this.isKingdomFlag = false;
        this.useMaterialAndKingdom = false;
        this.isFlag = false;
        this.isCrude = false;
        this.minable = false;
        this.isEnchantableJewelry = false;
        this.isEpicTargetItem = false;
        this.isEpicPortal = false;
        this.isMassProduction = false;
        this.noWorkParent = false;
        this.alwaysBankable = false;
        this.improveItem = -1;
        this.alwaysLit = false;
        this.isMushroom = false;
        this.isWarTarget = false;
        this.isSourceSpring = false;
        this.isSource = false;
        this.isColorComponent = false;
        this.isTutorialItem = false;
        this.isEquipmentSlot = false;
        this.isOre = false;
        this.isShard = false;
        this.isAbility = false;
        this.isAltar = false;
        this.isBag = false;
        this.isQuiver = false;
        this.isMagicStaff = false;
        this.isTent = false;
        this.improveUsesTypeAsMaterial = false;
        this.isTransportable = false;
        this.isWarmachine = false;
        this.hideAddToCreationWindow = false;
        this.isBrazier = false;
        this.isSmearable = false;
        this.isItemSpawn = false;
        this.noImprove = false;
        this.isTapestry = false;
        this.isUnfinishedNoTake = false;
        this.isMilk = false;
        this.isCheese = false;
        this.isOwnerTurnable = false;
        this.isOwnerMoveable = false;
        this.isUnfired = false;
        this.isPlantable = false;
        this.isPlantOneAWeek = false;
        this.isRiftItem = false;
        this.isHitchTarget = false;
        this.isRiftAltar = false;
        this.isRiftLoot = false;
        this.hasItemBonus = false;
        this.isBracelet = false;
        this.isPotable = false;
        this.canBeGrownInPot = false;
        this.isAlcohol = false;
        this.isCrushable = false;
        this.hasSeeds = false;
        this.isCooker = false;
        this.isFoodGroup = false;
        this.inFoodGroup = 0;
        this.isCookingTool = false;
        this.isRecipeItem = false;
        this.isNoCreate = false;
        this.usesFoodState = false;
        this.canBeFermented = false;
        this.canBeDistilled = false;
        this.canBeSealed = false;
        this.hovers = false;
        this.foodBonusHot = false;
        this.foodBonusCold = false;
        this.isHarvestable = false;
        this.harvestTo = 0;
        this.isRune = false;
        this.canBePegged = false;
        this.decayOnDeed = false;
        this.initialContainers = null;
        this.canShowRaw = false;
        this.cannotBeSpellTarget = false;
        this.isTrellis = false;
        this.containsIngredientsOnly = false;
        this.usesRealTemplate = false;
        this.canLarder = false;
        this.isInsulated = false;
        this.isGuardTower = false;
        this.isComponentItem = false;
        this.parentMustBeOnGround = false;
        this.maxItemCount = -1;
        this.maxItemWeight = -1;
        this.isRoadMarker = false;
        this.isPaveable = false;
        this.isCavePaveable = false;
        this.decorationWhenPlanted = false;
        this.fragmentAmount = 3;
        this.descIsName = false;
        this.isNotRuneable = false;
        this.showsSlopes = false;
        this.supportsSecondryColor = false;
        this.createsWithLock = false;
        this.isFishingReel = false;
        this.isFishingLine = false;
        this.isFishingFloat = false;
        this.isFishingHook = false;
        this.isFishingBait = false;
        this.containerRestrictions = null;
        this.isPluralName = false;
        this.templateId = aTemplateId;
        this.name = aName;
        this.plural = aPlural;
        this.itemDescriptionSuperb = aItemDescriptionSuperb;
        this.itemDescriptionNormal = aItemDescriptionNormal;
        this.itemDescriptionBad = aItemDescriptionBad;
        this.itemDescriptionRotten = aItemDescriptionRotten;
        this.itemDescriptionLong = aItemDescriptionLong;
        this.imageNumber = aImageNumber;
        this.behaviourType = aBehaviourType;
        this.combatDamage = aCombatDamage;
        this.decayTime = aDecayTime;
        final int[] sizes = { aCentimetersX, aCentimetersY, aCentimetersZ };
        Arrays.sort(sizes);
        this.centimetersX = sizes[0];
        this.centimetersY = sizes[1];
        this.centimetersZ = sizes[2];
        this.volume = aCentimetersX * aCentimetersY * aCentimetersZ;
        this.primarySkill = aPrimarySkill;
        this.bodySpaces = aBodySpaces;
        this.concatName = aName.trim().toLowerCase().replaceAll(" ", "") + ".";
        if (aModelName == null) {
            this.modelName = "UNSET";
            ItemTemplate.logger.log(Level.WARNING, "Modelname was null for template with id=" + this.templateId);
        }
        else {
            this.modelName = aModelName;
        }
        this.difficulty = aDifficulty;
        this.weight = aWeight;
        this.material = aMaterial;
        this.value = aValue;
        this.isPurchased = aIsPurchased;
        this.size = aSize;
        this.usesSpecifiedContainerSizes = false;
        this.setSizeString();
        this.assignTypes(aItemTypes);
        this.assignTemplateTypes();
        this.checkHolyItem();
        if (this.weight > 2000) {
            this.setFragmentAmount(Math.max(3, this.weight / 750));
        }
    }
    
    private void assignTemplateTypes() {
        if (this.templateId == 527 || this.templateId == 525 || this.templateId == 524 || this.templateId == 509) {
            this.farwalkerItem = true;
        }
        if (this.templateId == 516 || this.templateId == 102) {
            this.isBelt = true;
        }
        if (this.templateId == 664 || this.templateId == 665) {
            this.magicContainer = true;
        }
        if (this.templateId == 578 || this.templateId == 579 || this.templateId == 999) {
            this.isKingdomFlag = true;
            this.isFlag = true;
        }
        else if (this.templateId == 577 || this.templateId == 487) {
            this.isFlag = true;
        }
        else if (this.templateId == 726 || this.templateId == 728 || this.templateId == 727) {
            this.isDuelRing = true;
            this.nonDeedable = true;
        }
        else if (this.templateId == 712 || this.templateId == 714 || this.templateId == 713 || this.templateId == 715 || this.templateId == 716 || this.templateId == 717) {
            this.isEpicTargetItem = true;
            this.onePerTile = true;
        }
        else if (this.templateId == 732 || this.templateId == 733) {
            this.isEpicPortal = true;
            this.onePerTile = true;
        }
        else if (this.templateId == 969 || this.templateId == 970 || this.templateId == 971) {
            this.isItemSpawn = true;
        }
        if (this.templateId == 931) {
            this.fence = true;
        }
        if (this.templateId == 228 || this.templateId == 844 || this.templateId == 729) {
            this.candleHolder = true;
        }
        if (this.templateId >= 322 && this.templateId <= 328) {
            this.isAltar = true;
        }
        if (this.templateId == 1 || this.templateId == 443 || this.templateId == 2) {
            this.isBag = true;
        }
        if (this.templateId == 462) {
            this.isQuiver = true;
        }
        if (this.templateId == 824) {
            this.inventoryGroup = true;
        }
        if (this.templateId == 939) {
            this.protectionTower = true;
        }
        if (this.templateId == 1026) {
            this.unstableRift = true;
        }
        if (this.templateId >= 1033 && this.templateId <= 1048) {
            this.isRiftItem = true;
        }
    }
    
    public final boolean isItemSpawn() {
        return this.isItemSpawn;
    }
    
    public final boolean isUnstableRift() {
        return this.unstableRift;
    }
    
    public boolean isEpicTargetItem() {
        return this.isEpicTargetItem;
    }
    
    public void checkHolyItem() {
        final Deity[] deities = Deities.getDeities();
        for (int x = 0; x < deities.length; ++x) {
            if (deities[x].holyItem == this.templateId) {
                this.deity = deities[x];
                this.holyItem = true;
            }
        }
    }
    
    public String getConcatName() {
        return this.concatName;
    }
    
    public int getValue() {
        return this.value;
    }
    
    private void setSizeString() {
        if (this.size == 1) {
            this.sizeString = "tiny ";
        }
        else if (this.size == 2) {
            this.sizeString = "small ";
        }
        else if (this.size == 4) {
            this.sizeString = "large ";
        }
        else if (this.size == 5) {
            this.sizeString = "huge ";
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isNamePlural() {
        return this.isPluralName;
    }
    
    public String getProspectName() {
        if (this.getTemplateId() == 1238) {
            return this.getName();
        }
        return MaterialUtilities.getMaterialString(this.getMaterial());
    }
    
    public boolean isPurchased() {
        return this.isPurchased;
    }
    
    public String getNameWithGenus() {
        return StringUtilities.addGenus(this.name, this.isPluralName);
    }
    
    public byte getMaterial() {
        return this.material;
    }
    
    public String getPlural() {
        return this.plural;
    }
    
    public boolean hasPrimarySkill() {
        return this.primarySkill != -10L;
    }
    
    public int getPrimarySkill() throws NoSuchSkillException {
        if (this.primarySkill == -10L) {
            throw new NoSuchSkillException("No skill needed for item " + this.name);
        }
        return this.primarySkill;
    }
    
    public String getDescriptionSuperb() {
        return this.itemDescriptionSuperb;
    }
    
    public float getDifficulty() {
        return this.difficulty;
    }
    
    public String getModelName() {
        return this.modelName;
    }
    
    public short getImageNumber() {
        return this.imageNumber;
    }
    
    public String getDescriptionNormal() {
        return this.itemDescriptionNormal;
    }
    
    public String getDescriptionBad() {
        return this.itemDescriptionBad;
    }
    
    public String getDescriptionRotten() {
        return this.itemDescriptionRotten;
    }
    
    public String getDescriptionLong() {
        return this.itemDescriptionLong;
    }
    
    public int getDamagePercent() {
        return this.combatDamage;
    }
    
    public final boolean isMassProduction() {
        return this.isMassProduction;
    }
    
    public final boolean isMushroom() {
        return this.isMushroom;
    }
    
    public final boolean hideAddToCreationWindow() {
        return this.hideAddToCreationWindow;
    }
    
    public void assignTypes(final short[] types) {
        for (int x = 0; x < types.length; ++x) {
            switch (types[x]) {
                case 1: {
                    this.hollow = true;
                    break;
                }
                case 2: {
                    this.weapon = true;
                    this.weaponslash = true;
                    break;
                }
                case 3: {
                    this.shield = true;
                    break;
                }
                case 4: {
                    this.armour = true;
                    break;
                }
                case 5: {
                    this.food = true;
                    this.canLarder = true;
                    break;
                }
                case 6: {
                    this.magic = true;
                    break;
                }
                case 7: {
                    this.fieldtool = true;
                    break;
                }
                case 8: {
                    this.bodypart = true;
                    this.temporary = true;
                    break;
                }
                case 9: {
                    this.inventory = true;
                    break;
                }
                case 10: {
                    this.miningtool = true;
                    break;
                }
                case 11: {
                    this.carpentrytool = true;
                    break;
                }
                case 12: {
                    this.smithingtool = true;
                    break;
                }
                case 13: {
                    this.weapon = true;
                    this.weaponpierce = true;
                    break;
                }
                case 14: {
                    this.weapon = true;
                    this.weaponcrush = true;
                    break;
                }
                case 15: {
                    this.weapon = true;
                    this.weaponaxe = true;
                    break;
                }
                case 16: {
                    this.weapon = true;
                    this.weaponsword = true;
                    break;
                }
                case 154: {
                    this.weapon = true;
                    this.weaponPolearm = true;
                    break;
                }
                case 17: {
                    this.weapon = true;
                    this.weaponknife = true;
                    break;
                }
                case 18: {
                    this.weapon = true;
                    this.weaponmisc = true;
                    break;
                }
                case 19: {
                    this.diggingtool = true;
                    break;
                }
                case 20: {
                    this.seed = true;
                    break;
                }
                case 21: {
                    this.wood = true;
                    break;
                }
                case 22: {
                    this.metal = true;
                    break;
                }
                case 23: {
                    this.leather = true;
                    break;
                }
                case 24: {
                    this.cloth = true;
                    break;
                }
                case 25: {
                    this.stone = true;
                    break;
                }
                case 26: {
                    this.liquid = true;
                    break;
                }
                case 27: {
                    this.melting = true;
                    break;
                }
                case 28: {
                    this.meat = true;
                    break;
                }
                case 142: {
                    this.sign = true;
                    break;
                }
                case 143: {
                    this.streetlamp = true;
                    break;
                }
                case 29: {
                    this.vegetable = true;
                    break;
                }
                case 30: {
                    this.pottery = true;
                    break;
                }
                case 31: {
                    this.notake = true;
                    break;
                }
                case 32: {
                    this.light = true;
                    break;
                }
                case 33: {
                    this.containerliquid = true;
                    break;
                }
                case 34: {
                    this.liquidinflammable = true;
                    break;
                }
                case 35: {
                    this.weapon = true;
                    this.weaponmelee = true;
                    break;
                }
                case 36: {
                    this.fish = true;
                    break;
                }
                case 37: {
                    this.weapon = true;
                    break;
                }
                case 38: {
                    this.tool = true;
                    break;
                }
                case 39: {
                    this.lock = true;
                    break;
                }
                case 40: {
                    this.indestructible = true;
                    this.isNotRuneable = true;
                    break;
                }
                case 41: {
                    this.key = true;
                    break;
                }
                case 42: {
                    this.nodrop = true;
                    break;
                }
                case 44: {
                    this.repairable = true;
                    break;
                }
                case 45: {
                    this.temporary = true;
                    break;
                }
                case 46: {
                    this.combine = true;
                    break;
                }
                case 47: {
                    this.lockable = true;
                    break;
                }
                case 159: {
                    this.canHaveInscription = true;
                    break;
                }
                case 48: {
                    this.hasdata = true;
                    break;
                }
                case 49: {
                    this.outsideonly = true;
                    break;
                }
                case 50: {
                    this.coin = true;
                    this.fullprice = true;
                    break;
                }
                case 51: {
                    this.turnable = true;
                    break;
                }
                case 52: {
                    this.decoration = true;
                    break;
                }
                case 53: {
                    this.fullprice = true;
                    break;
                }
                case 54: {
                    this.norename = true;
                    break;
                }
                case 137: {
                    this.nonutrition = true;
                    break;
                }
                case 55: {
                    this.lownutrition = true;
                    break;
                }
                case 74: {
                    this.mediumnutrition = true;
                    break;
                }
                case 75: {
                    this.goodnutrition = true;
                    break;
                }
                case 76: {
                    this.highnutrition = true;
                    break;
                }
                case 77: {
                    this.isFoodMaker = true;
                    break;
                }
                case 56: {
                    this.draggable = true;
                    break;
                }
                case 57: {
                    this.villagedeed = true;
                    break;
                }
                case 58: {
                    this.homesteaddeed = true;
                    break;
                }
                case 59: {
                    this.alwayspoll = true;
                    break;
                }
                case 60: {
                    this.floating = true;
                    break;
                }
                case 61: {
                    this.notrade = true;
                    break;
                }
                case 62: {
                    this.hasdata = true;
                    this.isButcheredItem = true;
                    break;
                }
                case 63: {
                    this.isNoPut = true;
                    break;
                }
                case 64: {
                    this.isLeadCreature = true;
                    break;
                }
                case 198: {
                    this.isLeadMultipleCreatures = true;
                    break;
                }
                case 65: {
                    this.isFire = true;
                    break;
                }
                case 66: {
                    this.domainItem = true;
                    break;
                }
                case 67: {
                    this.useOnGroundOnly = true;
                    break;
                }
                case 68: {
                    this.hugeAltar = true;
                    this.nonDeedable = true;
                    break;
                }
                case 69: {
                    this.artifact = true;
                    this.alwaysLoaded = true;
                    this.isServerBound = true;
                    break;
                }
                case 70: {
                    this.unique = true;
                    this.alwaysLoaded = true;
                    break;
                }
                case 71: {
                    this.destroysHugeAltar = true;
                    break;
                }
                case 72: {
                    this.passFullData = true;
                    break;
                }
                case 73: {
                    this.isForm = true;
                    break;
                }
                case 78: {
                    this.herb = true;
                    break;
                }
                case 205: {
                    this.spice = true;
                    break;
                }
                case 79: {
                    this.poison = true;
                    break;
                }
                case 80: {
                    this.fruit = true;
                    break;
                }
                case 81: {
                    this.descIsExam = true;
                    break;
                }
                case 82: {
                    this.isDish = true;
                    this.namedCreator = true;
                    this.food = true;
                    this.canLarder = true;
                    break;
                }
                case 83: {
                    this.isServerBound = true;
                    break;
                }
                case 84: {
                    this.isTwohanded = true;
                    break;
                }
                case 85: {
                    this.kingdomMarker = true;
                    break;
                }
                case 86: {
                    this.destroyable = true;
                    break;
                }
                case 87: {
                    this.priceAffectedByMaterial = true;
                    break;
                }
                case 88: {
                    this.liquidCooking = true;
                    break;
                }
                case 89: {
                    this.positiveDecay = true;
                    break;
                }
                case 90: {
                    this.drinkable = true;
                    break;
                }
                case 91: {
                    this.isColor = true;
                    break;
                }
                case 92: {
                    this.colorable = true;
                    break;
                }
                case 93: {
                    this.gem = true;
                    break;
                }
                case 94: {
                    this.bow = true;
                    break;
                }
                case 95: {
                    this.bowUnstringed = true;
                    break;
                }
                case 96: {
                    this.egg = true;
                    break;
                }
                case 97: {
                    this.newbieItem = true;
                    break;
                }
                case 189: {
                    this.challengeNewbieItem = true;
                    break;
                }
                case 98: {
                    this.isTileAligned = true;
                    break;
                }
                case 99: {
                    this.isDragonArmour = true;
                    this.setImproveItem();
                    break;
                }
                case 100: {
                    this.isCompass = true;
                    break;
                }
                case 121: {
                    this.isToolbelt = true;
                    break;
                }
                case 101: {
                    this.oilConsuming = true;
                    break;
                }
                case 102: {
                    this.healing = true;
                    this.alchemyType = 1;
                    break;
                }
                case 103: {
                    this.healing = true;
                    this.alchemyType = 2;
                    break;
                }
                case 104: {
                    this.healing = true;
                    this.alchemyType = 3;
                    break;
                }
                case 105: {
                    this.healing = true;
                    this.alchemyType = 4;
                    break;
                }
                case 106: {
                    this.healing = true;
                    this.alchemyType = 5;
                    break;
                }
                case 108: {
                    this.namedCreator = true;
                    break;
                }
                case 109: {
                    this.onePerTile = true;
                    break;
                }
                case 167: {
                    this.fourPerTile = true;
                    break;
                }
                case 166: {
                    this.tenPerTile = true;
                    break;
                }
                case 110: {
                    this.bed = true;
                    break;
                }
                case 111: {
                    this.insideOnly = true;
                    break;
                }
                case 112: {
                    this.nobank = true;
                    break;
                }
                case 155: {
                    this.alwaysBankable = true;
                    this.nobank = false;
                    break;
                }
                case 113: {
                    this.isRecycled = true;
                    this.nobank = true;
                    break;
                }
                case 114: {
                    this.alwaysLoaded = true;
                    break;
                }
                case 115: {
                    this.flickeringLight = true;
                    break;
                }
                case 116: {
                    this.brightLight = true;
                    this.light = true;
                    break;
                }
                case 117: {
                    this.isVehicle = true;
                    break;
                }
                case 197: {
                    this.isChair = true;
                    break;
                }
                case 134: {
                    this.isVehicleDragged = true;
                    break;
                }
                case 193: {
                    this.isCart = true;
                    break;
                }
                case 118: {
                    this.isFlower = true;
                    this.isNaturePlantable = true;
                    break;
                }
                case 186: {
                    this.isNaturePlantable = true;
                    break;
                }
                case 119: {
                    this.isImproveItem = true;
                    break;
                }
                case 120: {
                    this.isDeathProtection = true;
                    break;
                }
                case 122: {
                    this.isRoyal = true;
                    this.isServerBound = true;
                    this.alwaysLoaded = true;
                    break;
                }
                case 123: {
                    this.isNoMove = true;
                    break;
                }
                case 124: {
                    this.isWind = true;
                    this.alwayspoll = true;
                    break;
                }
                case 125: {
                    this.isDredgingTool = true;
                    break;
                }
                case 126: {
                    this.isMineDoor = true;
                    break;
                }
                case 127: {
                    this.isNoSellBack = true;
                    break;
                }
                case 128: {
                    this.isSpringFilled = true;
                    break;
                }
                case 129: {
                    this.destroyOnDecay = true;
                    break;
                }
                case 130: {
                    this.rechargeable = true;
                    break;
                }
                case 131: {
                    this.isServerPortal = true;
                    break;
                }
                case 132: {
                    this.isTrap = true;
                    break;
                }
                case 133: {
                    this.isDisarmTrap = true;
                    break;
                }
                case 135: {
                    this.ownerDestroyable = true;
                    break;
                }
                case 136: {
                    this.wearableByCreaturesOnly = true;
                    break;
                }
                case 138: {
                    this.puppet = true;
                    break;
                }
                case 139: {
                    this.overrideNonEnchantable = true;
                    break;
                }
                case 140: {
                    this.isMeditation = true;
                    break;
                }
                case 141: {
                    this.isTransmutable = true;
                    break;
                }
                case 144: {
                    this.visibleDecay = true;
                    break;
                }
                case 145: {
                    this.bulkContainer = true;
                    break;
                }
                case 146: {
                    this.bulk = true;
                    this.isNotRuneable = true;
                    break;
                }
                case 147: {
                    this.missions = true;
                    break;
                }
                case 157: {
                    this.notMissions = true;
                    break;
                }
                case 148: {
                    this.combineCold = true;
                    break;
                }
                case 149: {
                    this.spawnsTrees = true;
                    break;
                }
                case 150: {
                    this.killsTrees = true;
                    break;
                }
                case 151: {
                    this.isCrude = true;
                    break;
                }
                case 152: {
                    this.minable = true;
                    break;
                }
                case 153: {
                    this.isEnchantableJewelry = true;
                    break;
                }
                case 156: {
                    this.alwaysLit = true;
                    break;
                }
                case 158: {
                    this.isMassProduction = true;
                    break;
                }
                case 160: {
                    this.noWorkParent = true;
                    break;
                }
                case 161: {
                    this.isWarTarget = true;
                    break;
                }
                case 162: {
                    this.isSourceSpring = true;
                    break;
                }
                case 163: {
                    this.isSource = true;
                    break;
                }
                case 164: {
                    this.isColorComponent = true;
                    break;
                }
                case 165: {
                    if (Servers.localServer.entryServer) {
                        this.isTutorialItem = true;
                        break;
                    }
                    break;
                }
                case 170: {
                    this.isEquipmentSlot = true;
                    break;
                }
                case 168: {
                    this.isAbility = true;
                    break;
                }
                case 169: {
                    this.plantedFlowerpot = true;
                    break;
                }
                case 172: {
                    this.isMagicStaff = true;
                    break;
                }
                case 173: {
                    this.improveUsesTypeAsMaterial = true;
                    break;
                }
                case 174: {
                    this.noDiscard = true;
                    break;
                }
                case 175: {
                    this.instaDiscard = true;
                    break;
                }
                case 176: {
                    this.isTransportable = true;
                    break;
                }
                case 177: {
                    this.isWarmachine = true;
                    break;
                }
                case 178: {
                    this.hideAddToCreationWindow = true;
                    break;
                }
                case 179: {
                    this.isBrazier = true;
                    break;
                }
                case 180: {
                    this.usesSpecifiedContainerSizes = true;
                    break;
                }
                case 181: {
                    this.setTent(true);
                    break;
                }
                case 182: {
                    this.useMaterialAndKingdom = true;
                    break;
                }
                case 183: {
                    this.setSmearable(true);
                    break;
                }
                case 184: {
                    this.isCarpet = true;
                    break;
                }
                case 191: {
                    this.isMilk = true;
                    break;
                }
                case 192: {
                    this.isCheese = true;
                    break;
                }
                case 187: {
                    this.noImprove = true;
                    break;
                }
                case 188: {
                    this.isTapestry = true;
                    break;
                }
                case 190: {
                    this.isUnfinishedNoTake = true;
                    break;
                }
                case 194: {
                    this.isOwnerTurnable = true;
                    break;
                }
                case 195: {
                    this.isOwnerMoveable = true;
                    break;
                }
                case 196: {
                    this.isUnfired = true;
                    break;
                }
                case 199: {
                    this.isPlantable = true;
                    break;
                }
                case 200: {
                    this.isPlantable = true;
                    this.isPlantOneAWeek = true;
                    break;
                }
                case 201: {
                    this.isHitchTarget = true;
                    break;
                }
                case 206: {
                    this.isPotable = true;
                    break;
                }
                case 221: {
                    this.canBeGrownInPot = true;
                    break;
                }
                case 209: {
                    this.isCooker = true;
                    break;
                }
                case 208: {
                    this.isFoodGroup = true;
                    break;
                }
                case 210: {
                    this.isCookingTool = true;
                    break;
                }
                case 211: {
                    this.isRecipeItem = true;
                    break;
                }
                case 207: {
                    this.isNoCreate = true;
                    break;
                }
                case 212: {
                    this.usesFoodState = true;
                    break;
                }
                case 213: {
                    this.canBeFermented = true;
                    break;
                }
                case 214: {
                    this.canBeDistilled = true;
                    break;
                }
                case 215: {
                    this.canBeSealed = true;
                    break;
                }
                case 236: {
                    this.canBePegged = true;
                    break;
                }
                case 217: {
                    this.canBeCookingOil = true;
                    break;
                }
                case 216: {
                    this.useRealTemplateIcon = true;
                    break;
                }
                case 218: {
                    this.hovers = true;
                    break;
                }
                case 219: {
                    this.foodBonusHot = true;
                    break;
                }
                case 220: {
                    this.foodBonusCold = true;
                    break;
                }
                case 223: {
                    this.canBeRawWrapped = true;
                    break;
                }
                case 222: {
                    this.canBePapyrusWrapped = true;
                    this.usesFoodState = true;
                    break;
                }
                case 224: {
                    this.canBeClothWrapped = true;
                    break;
                }
                case 225: {
                    this.surfaceonly = true;
                    break;
                }
                case 226: {
                    this.isMushroom = true;
                    break;
                }
                case 228: {
                    this.canShowRaw = true;
                    break;
                }
                case 229: {
                    this.cannotBeSpellTarget = true;
                    break;
                }
                case 230: {
                    this.isTrellis = true;
                    break;
                }
                case 231: {
                    this.containsIngredientsOnly = true;
                    break;
                }
                case 232: {
                    this.isComponentItem = true;
                    break;
                }
                case 240: {
                    this.parentMustBeOnGround = true;
                    break;
                }
                case 233: {
                    this.usesRealTemplate = true;
                    break;
                }
                case 234: {
                    this.canLarder = true;
                    break;
                }
                case 235: {
                    this.isRune = true;
                    this.isNotRuneable = true;
                    break;
                }
                case 237: {
                    this.decayOnDeed = true;
                    break;
                }
                case 238: {
                    this.isInsulated = true;
                    break;
                }
                case 239: {
                    this.isGuardTower = true;
                    break;
                }
                case 241: {
                    this.isRoadMarker = true;
                    break;
                }
                case 242: {
                    this.isPaveable = true;
                    break;
                }
                case 243: {
                    this.isCavePaveable = true;
                    break;
                }
                case 244: {
                    this.isPlantable = true;
                    this.decorationWhenPlanted = true;
                    break;
                }
                case 245: {
                    this.descIsName = true;
                    break;
                }
                case 246: {
                    this.isNotRuneable = true;
                    break;
                }
                case 247: {
                    this.showsSlopes = true;
                    break;
                }
                case 248: {
                    this.isPluralName = true;
                    break;
                }
                case 249: {
                    this.supportsSecondryColor = true;
                    break;
                }
                case 250: {
                    this.isFishingReel = true;
                    break;
                }
                case 251: {
                    this.isFishingLine = true;
                    break;
                }
                case 252: {
                    this.isFishingFloat = true;
                    break;
                }
                case 253: {
                    this.isFishingHook = true;
                    break;
                }
                case 254: {
                    this.isFishingBait = true;
                    break;
                }
                case 255: {
                    this.hasExtraData = true;
                    break;
                }
                case 256: {
                    this.viewableSubItems = true;
                    break;
                }
                case 259: {
                    this.viewableSubItems = true;
                    this.isContainerWithSubItems = true;
                    break;
                }
                case 257: {
                    this.createsWithLock = true;
                    break;
                }
                case 258: {
                    this.isBracelet = true;
                    break;
                }
                default: {
                    if (ItemTemplate.logger.isLoggable(Level.FINE)) {
                        ItemTemplate.logger.fine("Cannot assign type for: " + types[x]);
                        break;
                    }
                    break;
                }
            }
        }
        this.isMovingItem = (this.isVehicle || this.draggable);
        this.setIsSharp();
        this.setIsOre();
        this.setIsShard();
    }
    
    private void setIsSharp() {
        if (this.isWeaponSlash()) {
            this.isSharp = true;
        }
        switch (this.getTemplateId()) {
            case 8:
            case 25:
            case 93:
            case 121:
            case 125:
            case 126:
            case 258:
            case 267:
            case 268:
            case 269:
            case 270: {
                this.isSharp = true;
                break;
            }
            default: {
                this.isSharp = false;
                break;
            }
        }
    }
    
    private void setIsOre() {
        switch (this.getTemplateId()) {
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 207:
            case 693:
            case 697: {
                this.isOre = true;
                break;
            }
            default: {
                this.isOre = false;
                break;
            }
        }
    }
    
    private void setIsShard() {
        switch (this.getTemplateId()) {
            case 146:
            case 770:
            case 785:
            case 1116:
            case 1238: {
                this.isShard = true;
                break;
            }
            default: {
                this.isShard = false;
                break;
            }
        }
    }
    
    private void setImproveItem() {
        if (this.isDragonArmour) {
            this.improveItem = 371;
            if (this.getTemplateId() >= 474) {
                this.improveItem = 372;
            }
        }
    }
    
    public int getImproveItem() {
        return this.improveItem;
    }
    
    public long getDecayTime() {
        return this.decayTime;
    }
    
    public int getSizeX() {
        return this.centimetersX;
    }
    
    public final int getContainerSizeX() {
        if (this.usesSpecifiedContainerSizes) {
            return this.containerCentimetersX;
        }
        return this.getSizeX();
    }
    
    public int getSizeY() {
        return this.centimetersY;
    }
    
    public final int getContainerSizeY() {
        if (this.usesSpecifiedContainerSizes) {
            return this.containerCentimetersY;
        }
        return this.getSizeY();
    }
    
    public int getSizeZ() {
        return this.centimetersZ;
    }
    
    public final int getContainerSizeZ() {
        if (this.usesSpecifiedContainerSizes) {
            return this.containerCentimetersZ;
        }
        return this.getSizeZ();
    }
    
    public ItemTemplate setContainerSize(final int x, final int y, final int z) {
        final int[] sizes = { x, y, z };
        Arrays.sort(sizes);
        this.containerCentimetersX = sizes[0];
        this.containerCentimetersY = sizes[1];
        this.containerCentimetersZ = sizes[2];
        this.containerVolume = x * y * z;
        return this;
    }
    
    public final int getContainerVolume() {
        if (this.usesSpecifiedContainerSizes) {
            return this.containerVolume;
        }
        return this.getVolume();
    }
    
    public final boolean usesSpecifiedContainerSizes() {
        return this.usesSpecifiedContainerSizes;
    }
    
    public final ItemTemplate setDyeAmountGrams(final int dyeOverrideAmount) {
        this.dyePrimaryAmountRequired = dyeOverrideAmount;
        return this;
    }
    
    public final ItemTemplate setDyeAmountGrams(final int dyeOverridePrimary, final int dyeOverrideSecondary) {
        this.dyePrimaryAmountRequired = dyeOverridePrimary;
        this.dyeSecondaryAmountRequired = dyeOverrideSecondary;
        return this;
    }
    
    public final int getDyePrimaryAmountGrams() {
        return this.dyePrimaryAmountRequired;
    }
    
    public final int getDyeSecondaryAmountGrams() {
        return this.dyeSecondaryAmountRequired;
    }
    
    public final ItemTemplate setSecondryItem(final String secondryItemName, final int dyeOverrideAmount) {
        this.secondaryItemName = secondryItemName;
        this.dyeSecondaryAmountRequired = dyeOverrideAmount;
        return this;
    }
    
    public final ItemTemplate setSecondryItem(final String secondaryItemName) {
        this.secondaryItemName = secondaryItemName;
        this.dyeSecondaryAmountRequired = 0;
        return this;
    }
    
    public final String getSecondryItemName() {
        return this.secondaryItemName;
    }
    
    public ItemTemplate setNutritionValues(final int calories, final int carbs, final int fats, final int proteins) {
        this.calcNutritionValues = false;
        this.calories = (short)calories;
        this.carbs = (short)carbs;
        this.fats = (short)fats;
        this.proteins = (short)proteins;
        return this;
    }
    
    ItemTemplate setAlcoholStrength(final int newAlcoholStrength) {
        this.alcoholStrength = newAlcoholStrength;
        this.isAlcohol = true;
        return this;
    }
    
    ItemTemplate setGrows(final int growsTemplateId) {
        this.grows = growsTemplateId;
        return this;
    }
    
    public int getGrows() {
        if (this.grows == 0) {
            return this.templateId;
        }
        return this.grows;
    }
    
    ItemTemplate setCrushsTo(final int toTemplateId) {
        this.crushsTo = toTemplateId;
        this.isCrushable = true;
        return this;
    }
    
    public int getCrushsTo() {
        return this.crushsTo;
    }
    
    ItemTemplate setHarvestsTo(final int toTemplateId) {
        this.harvestTo = toTemplateId;
        this.isHarvestable = true;
        return this;
    }
    
    public boolean isHarvestable() {
        return this.isHarvestable;
    }
    
    public int getHarvestsTo() {
        return this.harvestTo;
    }
    
    ItemTemplate setPickSeeds(final int seedTemplateId) {
        this.pickSeeds = seedTemplateId;
        this.hasSeeds = true;
        return this;
    }
    
    public int getPickSeeds() {
        return this.pickSeeds;
    }
    
    ItemTemplate setFoodGroup(final int foodGroupTemplateId) {
        this.inFoodGroup = foodGroupTemplateId;
        return this;
    }
    
    public int getFoodGroup() {
        if (this.inFoodGroup > 0) {
            return this.inFoodGroup;
        }
        return this.getTemplateId();
    }
    
    ItemTemplate addContainerRestriction(final boolean onlyOneOf, final int... itemTemplateId) {
        if (this.containerRestrictions == null) {
            this.containerRestrictions = new ArrayList<ContainerRestriction>();
        }
        this.containerRestrictions.add(new ContainerRestriction(onlyOneOf, itemTemplateId));
        return this;
    }
    
    ItemTemplate addContainerRestriction(final boolean onlyOneOf, final String emptySlotName, final int... itemTemplateId) {
        if (this.containerRestrictions == null) {
            this.containerRestrictions = new ArrayList<ContainerRestriction>();
        }
        this.containerRestrictions.add(new ContainerRestriction(onlyOneOf, emptySlotName, itemTemplateId));
        return this;
    }
    
    public ArrayList<ContainerRestriction> getContainerRestrictions() {
        return this.containerRestrictions;
    }
    
    ItemTemplate setInitialContainers(final InitialContainer[] containers) {
        this.initialContainers = containers;
        return this;
    }
    
    public InitialContainer[] getInitialContainers() {
        return this.initialContainers;
    }
    
    public boolean calcNutritionValues() {
        return this.calcNutritionValues;
    }
    
    public short getCalories() {
        return this.calories;
    }
    
    public short getCarbs() {
        return this.carbs;
    }
    
    public short getFats() {
        return this.fats;
    }
    
    public short getProteins() {
        return this.proteins;
    }
    
    public int getVolume() {
        return this.volume;
    }
    
    public byte[] getBodySpaces() {
        return this.bodySpaces;
    }
    
    public int getTemplateId() {
        return this.templateId;
    }
    
    public short getBehaviourType() {
        return this.behaviourType;
    }
    
    public Behaviour getBehaviour() throws NoSuchBehaviourException {
        return Behaviours.getInstance().getBehaviour(this.behaviourType);
    }
    
    public int getWeightGrams() {
        return this.weight;
    }
    
    public final boolean isVehicle() {
        return this.isVehicle;
    }
    
    public final boolean isAlwaysBankable() {
        return this.alwaysBankable;
    }
    
    public boolean isHollow() {
        return this.hollow;
    }
    
    public boolean isWeaponSlash() {
        return this.weaponslash;
    }
    
    public boolean isShield() {
        return this.shield;
    }
    
    public boolean isCrude() {
        return this.isCrude;
    }
    
    public final boolean isCarpet() {
        return this.isCarpet;
    }
    
    public boolean isArmour() {
        return this.armour;
    }
    
    public boolean isBracelet() {
        return this.isBracelet;
    }
    
    public boolean isFood() {
        return this.food;
    }
    
    public boolean isFruit() {
        return this.fruit;
    }
    
    public boolean isMagic() {
        return this.magic;
    }
    
    public boolean isFieldTool() {
        return this.fieldtool;
    }
    
    public boolean isBodyPart() {
        return this.bodypart;
    }
    
    public boolean isBoat() {
        return this.isVehicle() && this.isFloating();
    }
    
    public boolean isPuppet() {
        return this.puppet;
    }
    
    public boolean isInventory() {
        return this.inventory;
    }
    
    public boolean isInventoryGroup() {
        return this.inventoryGroup;
    }
    
    public boolean isImproveUsingTypeAsMaterial() {
        return this.improveUsesTypeAsMaterial;
    }
    
    public boolean isMiningtool() {
        return this.miningtool;
    }
    
    public boolean isCarpentryTool() {
        return this.carpentrytool;
    }
    
    public boolean isSmithingTool() {
        return this.smithingtool;
    }
    
    public boolean isWeaponPierce() {
        return this.weaponpierce;
    }
    
    public boolean isWeaponCrush() {
        return this.weaponcrush;
    }
    
    public boolean isWeaponAxe() {
        return this.weaponaxe;
    }
    
    public boolean isWeaponSword() {
        return this.weaponsword;
    }
    
    public boolean isWeaponPolearm() {
        return this.weaponPolearm;
    }
    
    public boolean isWeaponKnife() {
        return this.weaponknife;
    }
    
    public boolean isWeaponMisc() {
        return this.weaponmisc;
    }
    
    public boolean isRechargeable() {
        return this.rechargeable;
    }
    
    public boolean isDiggingtool() {
        return this.diggingtool;
    }
    
    public boolean isSeed() {
        return this.seed;
    }
    
    public boolean isLiquid() {
        return this.liquid;
    }
    
    public boolean isLiquidCooking() {
        return this.liquidCooking;
    }
    
    public boolean isMelting() {
        return this.melting;
    }
    
    public boolean isMeat() {
        return this.meat;
    }
    
    public boolean isVegetable() {
        return this.vegetable;
    }
    
    public boolean isWood() {
        return this.wood;
    }
    
    public boolean isStone() {
        return this.stone;
    }
    
    public boolean isMetal() {
        return this.metal;
    }
    
    public boolean isNoTrade() {
        return this.notrade;
    }
    
    public boolean isLeather() {
        return this.leather;
    }
    
    public boolean isCloth() {
        return this.cloth;
    }
    
    public boolean isPottery() {
        return this.pottery;
    }
    
    public boolean isPlantedFlowerpot() {
        return this.plantedFlowerpot;
    }
    
    public boolean isNoTake() {
        return this.notake;
    }
    
    public boolean isNoImprove() {
        return this.noImprove;
    }
    
    public boolean isFlower() {
        return this.isFlower;
    }
    
    public boolean isLight() {
        return this.light;
    }
    
    public boolean isContainerLiquid() {
        return this.containerliquid;
    }
    
    public boolean isLiquidInflammable() {
        return this.liquidinflammable;
    }
    
    public boolean isWeaponMelee() {
        return this.weaponmelee;
    }
    
    public boolean isFish() {
        return this.fish;
    }
    
    public boolean isWeapon() {
        return this.weapon;
    }
    
    public boolean isTool() {
        return this.tool;
    }
    
    public boolean isLock() {
        return this.lock;
    }
    
    public boolean isIndestructible() {
        return this.indestructible;
    }
    
    public boolean isKey() {
        return this.key;
    }
    
    public boolean isNoDrop() {
        return this.nodrop;
    }
    
    public boolean isRepairable() {
        return this.repairable;
    }
    
    public boolean isTemporary() {
        return this.temporary;
    }
    
    public boolean isCombine() {
        return this.combine;
    }
    
    public boolean isLockable() {
        return this.lockable;
    }
    
    public boolean canHaveInscription() {
        return this.canHaveInscription;
    }
    
    public boolean hasData() {
        return this.hasdata;
    }
    
    public boolean hasExtraData() {
        return this.hasExtraData;
    }
    
    public boolean hasViewableSubItems() {
        return this.viewableSubItems;
    }
    
    public boolean isContainerWithSubItems() {
        return this.isContainerWithSubItems;
    }
    
    public boolean isOutsideOnly() {
        return this.outsideonly;
    }
    
    public boolean isSurfaceOnly() {
        return this.surfaceonly;
    }
    
    public boolean isCoin() {
        return this.coin;
    }
    
    public boolean isTurnable() {
        return this.turnable;
    }
    
    public final boolean isTapestry() {
        return this.isTapestry;
    }
    
    public final boolean isTransportable() {
        return this.isTransportable;
    }
    
    public boolean isDecoration() {
        return this.decoration;
    }
    
    public boolean decayOnDeed() {
        return this.decayOnDeed;
    }
    
    public boolean isFullprice() {
        return this.fullprice;
    }
    
    public boolean isNoRename() {
        return this.norename;
    }
    
    public boolean isTutorialItem() {
        return this.isTutorialItem;
    }
    
    public boolean isLownutrition() {
        return this.lownutrition;
    }
    
    public boolean isDraggable() {
        return this.draggable;
    }
    
    public boolean isVillageDeed() {
        return this.villagedeed;
    }
    
    public final boolean isBarding() {
        switch (this.getTemplateId()) {
            case 702:
            case 703:
            case 704: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public final boolean isRope() {
        return this.getTemplateId() == 319 || this.getTemplateId() == 1029;
    }
    
    public boolean isHomesteadDeed() {
        return this.homesteaddeed;
    }
    
    public boolean isAlwaysPoll() {
        return this.alwayspoll;
    }
    
    public boolean isMissionItem() {
        return !this.notMissions && !this.isRiftItem && !this.isCrude && (this.bulk || this.newbieItem || this.missions);
    }
    
    public boolean isCombineCold() {
        return this.combineCold;
    }
    
    public final boolean isBulk() {
        return this.bulk;
    }
    
    public final boolean isUnfired() {
        return this.isUnfired;
    }
    
    public boolean isFloating() {
        return this.floating;
    }
    
    public boolean isButcheredItem() {
        return this.isButcheredItem;
    }
    
    public boolean isNoPut() {
        return this.isNoPut;
    }
    
    public boolean isLeadCreature() {
        return this.isLeadCreature;
    }
    
    public boolean isLeadMultipleCreatures() {
        return this.isLeadMultipleCreatures;
    }
    
    public final boolean isKingdomMarker() {
        return this.kingdomMarker;
    }
    
    public boolean isFire() {
        return this.isFire;
    }
    
    public boolean isDomainItem() {
        return this.domainItem;
    }
    
    public boolean isCreatureWearableOnly() {
        return this.wearableByCreaturesOnly;
    }
    
    public boolean isUseOnGroundOnly() {
        return this.useOnGroundOnly;
    }
    
    public boolean isHolyItem() {
        return this.holyItem;
    }
    
    public final boolean isBrazier() {
        return this.isBrazier;
    }
    
    public final boolean isPlantable() {
        return this.isPlantable;
    }
    
    public final boolean isPlantOneAWeeek() {
        return this.isPlantOneAWeek;
    }
    
    public String toPipeString() {
        final StringBuilder lBuilder = new StringBuilder();
        lBuilder.append(this.templateId);
        lBuilder.append('|').append(this.size);
        lBuilder.append('|').append(this.name);
        lBuilder.append('|').append(this.plural);
        lBuilder.append('|').append(this.itemDescriptionSuperb);
        lBuilder.append('|').append(this.itemDescriptionNormal);
        lBuilder.append('|').append(this.itemDescriptionBad);
        lBuilder.append('|').append(this.itemDescriptionRotten);
        lBuilder.append('|').append(this.itemDescriptionLong);
        lBuilder.append('|').append(this.imageNumber);
        lBuilder.append('|').append(this.behaviourType);
        lBuilder.append('|').append(this.combatDamage);
        lBuilder.append('|').append(this.decayTime);
        lBuilder.append('|').append(this.centimetersX);
        lBuilder.append('|').append(this.centimetersY);
        lBuilder.append('|').append(this.centimetersZ);
        lBuilder.append('|').append(this.primarySkill);
        lBuilder.append('|').append(Arrays.toString(this.bodySpaces));
        lBuilder.append('|').append(this.modelName);
        lBuilder.append('|').append(this.difficulty);
        lBuilder.append('|').append(this.weight);
        lBuilder.append('|').append(Byte.toString(this.material));
        lBuilder.append('|').append(this.value);
        lBuilder.append('|').append(this.isPurchased);
        lBuilder.append('|').append(-1);
        return lBuilder.toString();
    }
    
    @Override
    public String toString() {
        final StringBuilder lBuilder = new StringBuilder();
        lBuilder.append("ItemTemplate[");
        lBuilder.append("ID: ").append(this.templateId);
        lBuilder.append(", size: ").append(this.size);
        lBuilder.append(", name: ").append(this.name);
        lBuilder.append(", plural: ").append(this.plural);
        lBuilder.append(", itemDescriptionSuperb: ").append(this.itemDescriptionSuperb);
        lBuilder.append(", itemDescriptionNormal: ").append(this.itemDescriptionNormal);
        lBuilder.append(", itemDescriptionBad: ").append(this.itemDescriptionBad);
        lBuilder.append(", itemDescriptionRotten: ").append(this.itemDescriptionRotten);
        lBuilder.append(", itemDescriptionLong: ").append(this.itemDescriptionLong);
        lBuilder.append(", imageNumber: ").append(this.imageNumber);
        lBuilder.append(", behaviourType: ").append(this.behaviourType);
        lBuilder.append(", combatDamage: ").append(this.combatDamage);
        lBuilder.append(", decayTime: ").append(this.decayTime);
        lBuilder.append(", centimetersX: ").append(this.centimetersX);
        lBuilder.append(", centimetersY: ").append(this.centimetersY);
        lBuilder.append(", centimetersZ: ").append(this.centimetersZ);
        lBuilder.append(", primarySkill: ").append(this.primarySkill);
        lBuilder.append(", bodySpaces: ").append(Arrays.toString(this.bodySpaces));
        lBuilder.append(", modelName: ").append(this.modelName);
        lBuilder.append(", difficulty: ").append(this.difficulty);
        lBuilder.append(", weight: ").append(this.weight);
        lBuilder.append(", material: ").append(Byte.toString(this.material));
        lBuilder.append(", value: ").append(this.value);
        lBuilder.append(", isPurchased: ").append(this.isPurchased);
        lBuilder.append(", armourType: ").append(-1);
        lBuilder.append("]");
        return lBuilder.toString();
    }
    
    @Override
    public int compareTo(final ItemTemplate aItemTemplate) {
        return this.getName().compareTo(aItemTemplate.getName());
    }
    
    public boolean isSharp() {
        return this.isSharp;
    }
    
    public boolean isEquipmentSlot() {
        return this.isEquipmentSlot;
    }
    
    public boolean isMagicStaff() {
        return this.isMagicStaff;
    }
    
    public boolean isInstaDiscard() {
        return this.instaDiscard;
    }
    
    public void setInstaDiscard(final boolean aInstaDiscard) {
        this.instaDiscard = aInstaDiscard;
    }
    
    public boolean isNoDiscard() {
        return this.noDiscard;
    }
    
    public final boolean isWarmachine() {
        return this.isWarmachine;
    }
    
    public void setNoDiscard(final boolean aNoDiscard) {
        this.noDiscard = aNoDiscard;
    }
    
    public boolean isTent() {
        return this.isTent;
    }
    
    public void setTent(final boolean aIsTent) {
        this.isTent = aIsTent;
    }
    
    public boolean isSmearable() {
        return this.isSmearable;
    }
    
    public void setSmearable(final boolean aIsSmearable) {
        this.isSmearable = aIsSmearable;
    }
    
    public boolean isHitchTarget() {
        return this.isHitchTarget;
    }
    
    public boolean isRiftAltar() {
        return this.isRiftAltar;
    }
    
    public void setRiftAltar(final boolean isRiftAltar) {
        this.isRiftAltar = isRiftAltar;
    }
    
    public boolean isRiftItem() {
        return this.isRiftItem;
    }
    
    public boolean isRiftStoneDeco() {
        return this.getTemplateId() == 1033 || this.getTemplateId() == 1034 || this.getTemplateId() == 1035 || this.getTemplateId() == 1036;
    }
    
    public boolean isRiftPlantDeco() {
        return this.getTemplateId() == 1041 || this.getTemplateId() == 1042 || this.getTemplateId() == 1043 || this.getTemplateId() == 1044;
    }
    
    public boolean isRiftCrystalDeco() {
        return this.getTemplateId() == 1037 || this.getTemplateId() == 1038 || this.getTemplateId() == 1039 || this.getTemplateId() == 1040;
    }
    
    public boolean isRiftLoot() {
        return this.isRiftLoot;
    }
    
    public void setRiftLoot(final boolean isRiftLoot) {
        this.isRiftLoot = isRiftLoot;
    }
    
    public boolean isHasItemBonus() {
        return this.hasItemBonus;
    }
    
    public void setHasItemBonus(final boolean hasItemBonus) {
        this.hasItemBonus = hasItemBonus;
    }
    
    public boolean isPotable() {
        return this.isPotable;
    }
    
    public boolean canBeGrownInPot() {
        return this.canBeGrownInPot;
    }
    
    public boolean usesFoodState() {
        return this.usesFoodState;
    }
    
    public boolean canBeFermented() {
        return this.canBeFermented;
    }
    
    public boolean canBeDistilled() {
        return this.canBeDistilled;
    }
    
    public boolean canBeSealed() {
        return this.canBeSealed;
    }
    
    public boolean canBePegged() {
        return this.canBePegged;
    }
    
    public boolean hovers() {
        return this.hovers;
    }
    
    public boolean hasFoodBonusWhenHot() {
        return this.foodBonusHot;
    }
    
    public boolean hasFoodBonusWhenCold() {
        return this.foodBonusCold;
    }
    
    public boolean canShowRaw() {
        return this.canShowRaw;
    }
    
    public boolean cannotBeSpellTarget() {
        return this.cannotBeSpellTarget;
    }
    
    public boolean isTrellis() {
        return this.isTrellis;
    }
    
    public boolean containsIngredientsOnly() {
        return this.containsIngredientsOnly;
    }
    
    public boolean isShelf() {
        return this.isComponentItem && this.parentMustBeOnGround;
    }
    
    public boolean usesRealTemplate() {
        return this.usesRealTemplate;
    }
    
    public boolean isCooker() {
        return this.isCooker;
    }
    
    public boolean isFoodGroup() {
        return this.isFoodGroup;
    }
    
    public boolean isCookingTool() {
        return this.isCookingTool;
    }
    
    public boolean isRecipeItem() {
        return this.isRecipeItem;
    }
    
    public boolean isNoCreate() {
        return this.isNoCreate;
    }
    
    public boolean isAlcohol() {
        return this.isAlcohol;
    }
    
    public boolean isCrushable() {
        return this.isCrushable;
    }
    
    public boolean hasSeeds() {
        return this.hasSeeds;
    }
    
    public boolean isHerb() {
        return this.herb;
    }
    
    public boolean isRoadMarker() {
        return this.isRoadMarker;
    }
    
    public boolean isPaveable() {
        return this.isPaveable;
    }
    
    public boolean isCavePaveable() {
        return this.isCavePaveable;
    }
    
    public boolean canBeCookingOil() {
        return this.canBeCookingOil;
    }
    
    public boolean canBePapyrusWrapped() {
        return this.canBePapyrusWrapped;
    }
    
    public boolean canBeRawWrapped() {
        return this.canBeRawWrapped;
    }
    
    public boolean canBeClothWrapped() {
        return this.canBeClothWrapped;
    }
    
    public boolean useRealTemplateIcon() {
        return this.useRealTemplateIcon;
    }
    
    public boolean isMilk() {
        return this.isMilk;
    }
    
    public boolean isSpice() {
        return this.spice;
    }
    
    public final boolean isNoNutrition() {
        return this.nonutrition;
    }
    
    public final boolean isLowNutrition() {
        return this.lownutrition;
    }
    
    public final boolean isMediumNutrition() {
        return this.mediumnutrition;
    }
    
    public final boolean isHighNutrition() {
        return this.highnutrition;
    }
    
    public final boolean isGoodNutrition() {
        return this.goodnutrition;
    }
    
    public boolean isFoodMaker() {
        return this.isFoodMaker;
    }
    
    public int getAlcoholStrength() {
        return this.alcoholStrength;
    }
    
    public boolean canLarder() {
        return this.canLarder;
    }
    
    public boolean isRune() {
        return this.isRune;
    }
    
    public boolean isInsulated() {
        return this.isInsulated;
    }
    
    public boolean isGuardTower() {
        return this.isGuardTower;
    }
    
    public boolean isParentMustBeOnGround() {
        return this.parentMustBeOnGround;
    }
    
    public boolean isComponentItem() {
        return this.isComponentItem;
    }
    
    public boolean isAlmanacContainer() {
        return this.templateId == 1127 || this.templateId == 1128;
    }
    
    public ItemTemplate setMaxItemCount(final int count) {
        this.maxItemCount = count;
        return this;
    }
    
    public ItemTemplate setMaxItemWeight(final int grams) {
        this.maxItemWeight = grams;
        return this;
    }
    
    public int getMaxItemCount() {
        return this.maxItemCount;
    }
    
    public int getMaxItemWeight() {
        return this.maxItemWeight;
    }
    
    public ItemTemplate setFragmentAmount(final int count) {
        this.fragmentAmount = Math.min(127, count);
        return this;
    }
    
    public int getFragmentAmount() {
        return this.fragmentAmount;
    }
    
    public boolean isSaddleBags() {
        return this.templateId == 1333 || this.templateId == 1334;
    }
    
    public boolean doesShowSlopes() {
        return this.showsSlopes;
    }
    
    public boolean supportsSecondryColor() {
        return this.supportsSecondryColor;
    }
    
    public boolean doesCreateWithLock() {
        return this.createsWithLock;
    }
    
    public boolean isStatue() {
        return this.templateId == 402 || this.templateId == 399 || this.templateId == 400 || this.templateId == 1330 || this.templateId == 1323 || this.templateId == 1328 || this.templateId == 403 || this.templateId == 1325 || this.templateId == 811 || this.templateId == 742 || this.templateId == 1329 || this.templateId == 1327 || this.templateId == 398 || this.templateId == 401 || this.templateId == 1405 || this.templateId == 1407 || this.templateId == 1406 || this.templateId == 1408 || this.templateId == 1324 || this.templateId == 1326 || this.templateId == 1415 || this.templateId == 1416 || this.templateId == 1417 || this.templateId == 1418 || this.templateId == 1419 || this.templateId == 1420 || this.templateId == 1421 || this.templateId == 1430;
    }
    
    public boolean isMask() {
        return this.templateId == 977 || this.templateId == 973 || this.templateId == 978 || this.templateId == 1099 || this.templateId == 975 || this.templateId == 974 || this.templateId == 976 || this.templateId == 1321 || this.templateId == 1306;
    }
    
    public boolean isStorageRack() {
        return this.templateId == 1111 || this.templateId == 1315 || this.templateId == 1312 || this.templateId == 1110 || this.templateId == 1109 || this.templateId == 1108 || this.templateId == 1316 || this.templateId == 724 || this.templateId == 758 || this.templateId == 725;
    }
    
    public boolean isFishingReel() {
        return this.isFishingReel;
    }
    
    public boolean isFishingLine() {
        return this.isFishingLine;
    }
    
    public boolean isFishingFloat() {
        return this.isFishingFloat;
    }
    
    public boolean isFishingHook() {
        return this.isFishingHook;
    }
    
    public boolean isFishingBait() {
        return this.isFishingBait;
    }
    
    public boolean isMetalLump() {
        return this.templateId == 694 || this.templateId == 221 || this.templateId == 223 || this.templateId == 47 || this.templateId == 698 || this.templateId == 44 || this.templateId == 46 || this.templateId == 49 || this.templateId == 837 || this.templateId == 45 || this.templateId == 205 || this.templateId == 220 || this.templateId == 48 || this.templateId == 1411;
    }
    
    static {
        logger = Logger.getLogger(ItemTemplate.class.getName());
    }
}
