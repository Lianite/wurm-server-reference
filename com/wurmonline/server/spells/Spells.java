// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.spells;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;
import com.wurmonline.shared.constants.AttitudeConstants;

public final class Spells implements AttitudeConstants
{
    private static final Logger logger;
    private static final Map<Integer, Spell> spells;
    private static final Map<Integer, Spell> creatureSpells;
    private static final Map<Integer, Spell> itemSpells;
    private static final Map<Integer, Spell> woundSpells;
    private static final Map<Byte, Spell> enchantments;
    private static final Map<Integer, Spell> tileSpells;
    public static Spell SPELL_AURA_SHARED_PAIN;
    public static Spell SPELL_BEARPAWS;
    public static Spell SPELL_BLAZE;
    public static Spell SPELL_BLESS;
    public static Spell SPELL_BLESSINGS_OF_THE_DARK;
    public static Spell SPELL_BLOODTHIRST;
    public static Spell SPELL_BREAK_ALTAR;
    public static Spell SPELL_CHARM_ANIMAL;
    public static Spell SPELL_CIRCLE_OF_CUNNING;
    public static Spell SPELL_CLEANSE;
    public static Spell SPELL_CORROSION;
    public static Spell SPELL_CORRUPT;
    public static Spell SPELL_COURIER;
    public static Spell SPELL_CURE_LIGHT;
    public static Spell SPELL_CURE_MEDIUM;
    public static Spell SPELL_CURE_SERIOUS;
    public static Spell SPELL_DARK_MESSENGER;
    public static Spell SPELL_DEMISE_ANIMAL;
    public static Spell SPELL_DEMISE_HUMAN;
    public static Spell SPELL_DEMISE_LEGENDARY;
    public static Spell SPELL_DEMISE_MONSTER;
    public static Spell SPELL_DIRT;
    public static Spell SPELL_DISINTEGRATE;
    public static Spell SPELL_DISPEL;
    public static Spell SPELL_DOMINATE;
    public static Spell SPELL_DRAIN_HEALTH;
    public static Spell SPELL_DRAIN_STAMINA;
    public static Spell SPELL_ESSENCE_DRAIN;
    public static Spell SPELL_EXCEL;
    public static Spell SPELL_FIREHEART;
    public static Spell SPELL_FIRE_PILLAR;
    public static Spell SPELL_FLAMING_AURA;
    public static Spell SPELL_FOCUSED_WILL;
    public static Spell SPELL_FOREST_GIANT_STRENGTH;
    public static Spell SPELL_FRANTIC_CHARGE;
    public static Spell SPELL_FROSTBRAND;
    public static Spell SPELL_FUNGUS_TRAP;
    public static Spell SPELL_GENESIS;
    public static Spell SPELL_GLACIAL;
    public static Spell SPELL_GOAT_SHAPE;
    public static Spell SPELL_HEAL;
    public static Spell SPELL_HELL_STRENGTH;
    public static Spell SPELL_HOLY_CROP;
    public static Spell SPELL_HUMID_DRIZZLE;
    public static Spell SPELL_HYPOTHERMIA;
    public static Spell SPELL_ICE_PILLAR;
    public static Spell SPELL_INFERNO;
    public static Spell SPELL_LAND_OF_THE_DEAD;
    public static Spell SPELL_LIFE_TRANSFER;
    public static Spell SPELL_LIGHT_OF_FO;
    public static Spell SPELL_LIGHT_TOKEN;
    public static Spell SPELL_LOCATE_ARTIFACT;
    public static Spell SPELL_LOCATE_SOUL;
    public static Spell SPELL_LURKER_IN_THE_DARK;
    public static Spell SPELL_LURKER_IN_THE_DEEP;
    public static Spell SPELL_LURKER_IN_THE_WOODS;
    public static Spell SPELL_MASS_STAMINA;
    public static Spell SPELL_MEND;
    public static Spell SPELL_MIND_STEALER;
    public static Spell SPELL_MOLE_SENSES;
    public static Spell SPELL_MORNING_FOG;
    public static Spell SPELL_NIMBLENESS;
    public static Spell SPELL_NOLOCATE;
    public static Spell SPELL_OAKSHELL;
    public static Spell SPELL_OPULENCE;
    public static Spell SPELL_PAIN_RAIN;
    public static Spell SPELL_PHANTASMS;
    public static Spell SPELL_PROTECT_ACID;
    public static Spell SPELL_PROTECT_FIRE;
    public static Spell SPELL_PROTECT_FROST;
    public static Spell SPELL_PROTECT_POISON;
    public static Spell SPELL_PURGE;
    public static Spell SPELL_REBIRTH;
    public static Spell SPELL_REFRESH;
    public static Spell SPELL_REVEAL_CREATURES;
    public static Spell SPELL_REVEAL_SETTLEMENTS;
    public static Spell SPELL_RITE_OF_DEATH;
    public static Spell SPELL_RITE_OF_SPRING;
    public static Spell SPELL_RITUAL_OF_THE_SUN;
    public static Spell SPELL_ROTTING_GUT;
    public static Spell SPELL_ROTTING_TOUCH;
    public static Spell SPELL_SCORN_OF_LIBILA;
    public static Spell SPELL_SHARD_OF_ICE;
    public static Spell SPELL_SIXTH_SENSE;
    public static Spell SPELL_SMITE;
    public static Spell SPELL_STRONGWALL;
    public static Spell SPELL_SUMMON_SOUL;
    public static Spell SPELL_SUNDER;
    public static Spell SPELL_TANGLEWEAVE;
    public static Spell SPELL_TENTACLES;
    public static Spell SPELL_TORNADO;
    public static Spell SPELL_TOXIN;
    public static Spell SPELL_TRUEHIT;
    public static Spell SPELL_VENOM;
    public static Spell SPELL_VESSEL;
    public static Spell SPELL_WARD;
    public static Spell SPELL_WEAKNESS;
    public static Spell SPELL_WEB_ARMOUR;
    public static Spell SPELL_WILD_GROWTH;
    public static Spell SPELL_WILLOWSPINE;
    public static Spell SPELL_WIND_OF_AGES;
    public static Spell SPELL_WISDOM_OF_VYNORA;
    public static Spell SPELL_WORM_BRAINS;
    public static Spell SPELL_WRATH_OF_MAGRANON;
    public static Spell SPELL_ZOMBIE_INFESTATION;
    public static Spell SPELL_CONTINUUM;
    public static Spell SPELL_DISEASE;
    public static Spell SPELL_FIREBALL;
    public static Spell SPELL_FORECAST;
    public static Spell SPELL_INCINERATE;
    public static Spell SPELL_KARMA_BOLT;
    public static Spell SPELL_KARMA_MISSILE;
    public static Spell SPELL_KARMA_SLOW;
    public static Spell SPELL_LIGHTNING;
    public static Spell SPELL_MIRRORED_SELF;
    public static Spell SPELL_RUST_MONSTER;
    public static Spell SPELL_SPROUT_TREES;
    public static Spell SPELL_STONESKIN;
    public static Spell SPELL_SUMMON;
    public static Spell SPELL_SUMMON_SKELETON;
    public static Spell SPELL_SUMMON_WORG;
    public static Spell SPELL_SUMMON_WRAITH;
    public static Spell SPELL_TRUESTRIKE;
    public static Spell SPELL_WALL_OF_FIRE;
    public static Spell SPELL_WALL_OF_ICE;
    public static Spell SPELL_WALL_OF_STONE;
    
    public static void addSpell(final Spell spell) {
        Spells.spells.put(spell.getNumber(), spell);
        if (spell.isTargetCreature()) {
            Spells.creatureSpells.put(spell.getNumber(), spell);
        }
        if (spell.isTargetAnyItem()) {
            Spells.itemSpells.put(spell.getNumber(), spell);
        }
        if (spell.isTargetWound()) {
            Spells.woundSpells.put(spell.getNumber(), spell);
        }
        if (spell.isTargetTile()) {
            Spells.tileSpells.put(spell.getNumber(), spell);
        }
        if (spell.getEnchantment() != 0) {
            Spells.enchantments.put(spell.getEnchantment(), spell);
        }
    }
    
    public static final Spell getSpell(final int number) {
        return Spells.spells.get(number);
    }
    
    public static final Spell[] getAllSpells() {
        return Spells.spells.values().toArray(new Spell[Spells.spells.size()]);
    }
    
    public static final Spell getEnchantment(final byte num) {
        return Spells.enchantments.get(num);
    }
    
    public static final Spell[] getSpellsTargettingItems() {
        final Set<Spell> toReturn = new HashSet<Spell>();
        final Iterator<Integer> it = Spells.itemSpells.keySet().iterator();
        while (it.hasNext()) {
            toReturn.add(Spells.itemSpells.get(it.next()));
        }
        return toReturn.toArray(new Spell[toReturn.size()]);
    }
    
    public static final Spell[] getSpellsEnchantingItems() {
        final Set<Spell> toReturn = new HashSet<Spell>();
        final Iterator<Integer> it = Spells.itemSpells.keySet().iterator();
        while (it.hasNext()) {
            final Spell spell = Spells.itemSpells.get(it.next());
            if (spell.getEnchantment() > 0) {
                toReturn.add(spell);
            }
        }
        return toReturn.toArray(new Spell[toReturn.size()]);
    }
    
    static {
        logger = Logger.getLogger(Spells.class.getName());
        spells = new HashMap<Integer, Spell>();
        creatureSpells = new HashMap<Integer, Spell>();
        itemSpells = new HashMap<Integer, Spell>();
        woundSpells = new HashMap<Integer, Spell>();
        enchantments = new HashMap<Byte, Spell>();
        tileSpells = new HashMap<Integer, Spell>();
        Spells.SPELL_AURA_SHARED_PAIN = new SharedPain();
        Spells.SPELL_BEARPAWS = new Bearpaw();
        Spells.SPELL_BLAZE = new Blaze();
        Spells.SPELL_BLESS = new Bless();
        Spells.SPELL_BLESSINGS_OF_THE_DARK = new BlessingDark();
        Spells.SPELL_BLOODTHIRST = new Bloodthirst();
        Spells.SPELL_BREAK_ALTAR = new BreakAltar();
        Spells.SPELL_CHARM_ANIMAL = new CharmAnimal();
        Spells.SPELL_CIRCLE_OF_CUNNING = new CircleOfCunning();
        Spells.SPELL_CLEANSE = new Cleanse();
        Spells.SPELL_CORROSION = new Corrosion();
        Spells.SPELL_CORRUPT = new Corrupt();
        Spells.SPELL_COURIER = new Courier();
        Spells.SPELL_CURE_LIGHT = new CureLight();
        Spells.SPELL_CURE_MEDIUM = new CureMedium();
        Spells.SPELL_CURE_SERIOUS = new CureSerious();
        Spells.SPELL_DARK_MESSENGER = new DarkMessenger();
        Spells.SPELL_DEMISE_ANIMAL = new DemiseAnimal();
        Spells.SPELL_DEMISE_HUMAN = new DemiseHuman();
        Spells.SPELL_DEMISE_LEGENDARY = new DemiseLegendary();
        Spells.SPELL_DEMISE_MONSTER = new DemiseMonster();
        Spells.SPELL_DIRT = new Dirt();
        Spells.SPELL_DISINTEGRATE = new Disintegrate();
        Spells.SPELL_DISPEL = new Dispel();
        Spells.SPELL_DOMINATE = new Dominate();
        Spells.SPELL_DRAIN_HEALTH = new DrainHealth();
        Spells.SPELL_DRAIN_STAMINA = new DrainStamina();
        Spells.SPELL_ESSENCE_DRAIN = new EssenceDrain();
        Spells.SPELL_EXCEL = new Excel();
        Spells.SPELL_FIREHEART = new FireHeart();
        Spells.SPELL_FIRE_PILLAR = new FirePillar();
        Spells.SPELL_FLAMING_AURA = new FlamingAura();
        Spells.SPELL_FOCUSED_WILL = new FocusedWill();
        Spells.SPELL_FOREST_GIANT_STRENGTH = new ForestGiant();
        Spells.SPELL_FRANTIC_CHARGE = new FranticCharge();
        Spells.SPELL_FROSTBRAND = new Frostbrand();
        Spells.SPELL_FUNGUS_TRAP = new FungusTrap();
        Spells.SPELL_GENESIS = new Genesis();
        Spells.SPELL_GLACIAL = new Glacial();
        Spells.SPELL_GOAT_SHAPE = new GoatShape();
        Spells.SPELL_HEAL = new Heal();
        Spells.SPELL_HELL_STRENGTH = new Hellstrength();
        Spells.SPELL_HOLY_CROP = new HolyCrop();
        Spells.SPELL_HUMID_DRIZZLE = new HumidDrizzle();
        Spells.SPELL_HYPOTHERMIA = new Hypothermia();
        Spells.SPELL_ICE_PILLAR = new IcePillar();
        Spells.SPELL_INFERNO = new Inferno();
        Spells.SPELL_LAND_OF_THE_DEAD = new LandOfTheDead();
        Spells.SPELL_LIFE_TRANSFER = new LifeTransfer();
        Spells.SPELL_LIGHT_OF_FO = new LightOfFo();
        Spells.SPELL_LIGHT_TOKEN = new LightToken();
        Spells.SPELL_LOCATE_ARTIFACT = new LocateArtifact();
        Spells.SPELL_LOCATE_SOUL = new LocatePlayer();
        Spells.SPELL_LURKER_IN_THE_DARK = new LurkerDark();
        Spells.SPELL_LURKER_IN_THE_DEEP = new LurkerDeep();
        Spells.SPELL_LURKER_IN_THE_WOODS = new LurkerWoods();
        Spells.SPELL_MASS_STAMINA = new MassStamina();
        Spells.SPELL_MEND = new Mend();
        Spells.SPELL_MIND_STEALER = new MindStealer();
        Spells.SPELL_MOLE_SENSES = new MoleSenses();
        Spells.SPELL_MORNING_FOG = new MorningFog();
        Spells.SPELL_NIMBLENESS = new Nimbleness();
        Spells.SPELL_NOLOCATE = new Nolocate();
        Spells.SPELL_OAKSHELL = new OakShell();
        Spells.SPELL_OPULENCE = new Opulence();
        Spells.SPELL_PAIN_RAIN = new PainRain();
        Spells.SPELL_PHANTASMS = new Phantasms();
        Spells.SPELL_PROTECT_ACID = new ProtectionAcid();
        Spells.SPELL_PROTECT_FIRE = new ProtectionFire();
        Spells.SPELL_PROTECT_FROST = new ProtectionFrost();
        Spells.SPELL_PROTECT_POISON = new ProtectionPoison();
        Spells.SPELL_PURGE = new Purge();
        Spells.SPELL_REBIRTH = new Rebirth();
        Spells.SPELL_REFRESH = new Refresh();
        Spells.SPELL_REVEAL_CREATURES = new RevealCreatures();
        Spells.SPELL_REVEAL_SETTLEMENTS = new RevealSettlements();
        Spells.SPELL_RITE_OF_DEATH = new RiteDeath();
        Spells.SPELL_RITE_OF_SPRING = new RiteSpring();
        Spells.SPELL_RITUAL_OF_THE_SUN = new RitualSun();
        Spells.SPELL_ROTTING_GUT = new RottingGut();
        Spells.SPELL_ROTTING_TOUCH = new RottingTouch();
        Spells.SPELL_SCORN_OF_LIBILA = new ScornOfLibila();
        Spells.SPELL_SHARD_OF_ICE = new ShardOfIce();
        Spells.SPELL_SIXTH_SENSE = new SixthSense();
        Spells.SPELL_SMITE = new Smite();
        Spells.SPELL_STRONGWALL = new StrongWall();
        Spells.SPELL_SUMMON_SOUL = new SummonSoul();
        Spells.SPELL_SUNDER = new Sunder();
        Spells.SPELL_TANGLEWEAVE = new TangleWeave();
        Spells.SPELL_TENTACLES = new DeepTentacles();
        Spells.SPELL_TORNADO = new Tornado();
        Spells.SPELL_TOXIN = new Toxin();
        Spells.SPELL_TRUEHIT = new TrueHit();
        Spells.SPELL_VENOM = new Venom();
        Spells.SPELL_VESSEL = new Vessle();
        Spells.SPELL_WARD = new Ward();
        Spells.SPELL_WEAKNESS = new Weakness();
        Spells.SPELL_WEB_ARMOUR = new WebArmour();
        Spells.SPELL_WILD_GROWTH = new WildGrowth();
        Spells.SPELL_WILLOWSPINE = new WillowSpine();
        Spells.SPELL_WIND_OF_AGES = new WindOfAges();
        Spells.SPELL_WISDOM_OF_VYNORA = new WisdomVynora();
        Spells.SPELL_WORM_BRAINS = new WormBrains();
        Spells.SPELL_WRATH_OF_MAGRANON = new WrathMagranon();
        Spells.SPELL_ZOMBIE_INFESTATION = new ZombieInfestation();
        Spells.SPELL_CONTINUUM = new Continuum();
        Spells.SPELL_DISEASE = new Disease();
        Spells.SPELL_FIREBALL = new Fireball();
        Spells.SPELL_FORECAST = new Forecast();
        Spells.SPELL_INCINERATE = new Incinerate();
        Spells.SPELL_KARMA_BOLT = new KarmaBolt();
        Spells.SPELL_KARMA_MISSILE = new KarmaMissile();
        Spells.SPELL_KARMA_SLOW = new KarmaSlow();
        Spells.SPELL_LIGHTNING = new Lightning();
        Spells.SPELL_MIRRORED_SELF = new MirroredSelf();
        Spells.SPELL_RUST_MONSTER = new RustMonster();
        Spells.SPELL_SPROUT_TREES = new SproutTrees();
        Spells.SPELL_STONESKIN = new StoneSkin();
        Spells.SPELL_SUMMON = new Summon();
        Spells.SPELL_SUMMON_SKELETON = new SummonSkeleton();
        Spells.SPELL_SUMMON_WORG = new SummonWorg();
        Spells.SPELL_SUMMON_WRAITH = new SummonWraith();
        Spells.SPELL_TRUESTRIKE = new Truestrike();
        Spells.SPELL_WALL_OF_FIRE = new WallOfFire();
        Spells.SPELL_WALL_OF_ICE = new WallOfIce();
        Spells.SPELL_WALL_OF_STONE = new WallOfStone();
        addSpell(Spells.SPELL_AURA_SHARED_PAIN);
        addSpell(Spells.SPELL_BEARPAWS);
        addSpell(Spells.SPELL_BLAZE);
        addSpell(Spells.SPELL_BLESS);
        addSpell(Spells.SPELL_BLESSINGS_OF_THE_DARK);
        addSpell(Spells.SPELL_BLOODTHIRST);
        addSpell(Spells.SPELL_BREAK_ALTAR);
        addSpell(Spells.SPELL_CHARM_ANIMAL);
        addSpell(Spells.SPELL_CIRCLE_OF_CUNNING);
        addSpell(Spells.SPELL_CLEANSE);
        addSpell(Spells.SPELL_CORROSION);
        addSpell(Spells.SPELL_CORRUPT);
        addSpell(Spells.SPELL_COURIER);
        addSpell(Spells.SPELL_CURE_LIGHT);
        addSpell(Spells.SPELL_CURE_MEDIUM);
        addSpell(Spells.SPELL_CURE_SERIOUS);
        addSpell(Spells.SPELL_DARK_MESSENGER);
        addSpell(Spells.SPELL_DEMISE_ANIMAL);
        addSpell(Spells.SPELL_DEMISE_HUMAN);
        addSpell(Spells.SPELL_DEMISE_LEGENDARY);
        addSpell(Spells.SPELL_DEMISE_MONSTER);
        addSpell(Spells.SPELL_DIRT);
        addSpell(Spells.SPELL_DISINTEGRATE);
        addSpell(Spells.SPELL_DISPEL);
        addSpell(Spells.SPELL_DOMINATE);
        addSpell(Spells.SPELL_DRAIN_HEALTH);
        addSpell(Spells.SPELL_DRAIN_STAMINA);
        addSpell(Spells.SPELL_ESSENCE_DRAIN);
        addSpell(Spells.SPELL_EXCEL);
        addSpell(Spells.SPELL_FIREHEART);
        addSpell(Spells.SPELL_FIRE_PILLAR);
        addSpell(Spells.SPELL_FLAMING_AURA);
        addSpell(Spells.SPELL_FOCUSED_WILL);
        addSpell(Spells.SPELL_FOREST_GIANT_STRENGTH);
        addSpell(Spells.SPELL_FRANTIC_CHARGE);
        addSpell(Spells.SPELL_FROSTBRAND);
        addSpell(Spells.SPELL_FUNGUS_TRAP);
        addSpell(Spells.SPELL_GENESIS);
        addSpell(Spells.SPELL_GLACIAL);
        addSpell(Spells.SPELL_GOAT_SHAPE);
        addSpell(Spells.SPELL_HEAL);
        addSpell(Spells.SPELL_HELL_STRENGTH);
        addSpell(Spells.SPELL_HOLY_CROP);
        addSpell(Spells.SPELL_HUMID_DRIZZLE);
        addSpell(Spells.SPELL_HYPOTHERMIA);
        addSpell(Spells.SPELL_ICE_PILLAR);
        addSpell(Spells.SPELL_INFERNO);
        addSpell(Spells.SPELL_LAND_OF_THE_DEAD);
        addSpell(Spells.SPELL_LIFE_TRANSFER);
        addSpell(Spells.SPELL_LIGHT_OF_FO);
        addSpell(Spells.SPELL_LIGHT_TOKEN);
        addSpell(Spells.SPELL_LOCATE_ARTIFACT);
        addSpell(Spells.SPELL_LOCATE_SOUL);
        addSpell(Spells.SPELL_LURKER_IN_THE_DARK);
        addSpell(Spells.SPELL_LURKER_IN_THE_DEEP);
        addSpell(Spells.SPELL_LURKER_IN_THE_WOODS);
        addSpell(Spells.SPELL_MASS_STAMINA);
        addSpell(Spells.SPELL_MEND);
        addSpell(Spells.SPELL_MIND_STEALER);
        addSpell(Spells.SPELL_MOLE_SENSES);
        addSpell(Spells.SPELL_MORNING_FOG);
        addSpell(Spells.SPELL_NIMBLENESS);
        addSpell(Spells.SPELL_NOLOCATE);
        addSpell(Spells.SPELL_OAKSHELL);
        addSpell(Spells.SPELL_OPULENCE);
        addSpell(Spells.SPELL_PAIN_RAIN);
        addSpell(Spells.SPELL_PHANTASMS);
        addSpell(Spells.SPELL_PROTECT_ACID);
        addSpell(Spells.SPELL_PROTECT_FIRE);
        addSpell(Spells.SPELL_PROTECT_FROST);
        addSpell(Spells.SPELL_PROTECT_POISON);
        addSpell(Spells.SPELL_PURGE);
        addSpell(Spells.SPELL_REBIRTH);
        addSpell(Spells.SPELL_REFRESH);
        addSpell(Spells.SPELL_REVEAL_CREATURES);
        addSpell(Spells.SPELL_REVEAL_SETTLEMENTS);
        addSpell(Spells.SPELL_RITE_OF_DEATH);
        addSpell(Spells.SPELL_RITE_OF_SPRING);
        addSpell(Spells.SPELL_RITUAL_OF_THE_SUN);
        addSpell(Spells.SPELL_ROTTING_GUT);
        addSpell(Spells.SPELL_ROTTING_TOUCH);
        addSpell(Spells.SPELL_SCORN_OF_LIBILA);
        addSpell(Spells.SPELL_SHARD_OF_ICE);
        addSpell(Spells.SPELL_SIXTH_SENSE);
        addSpell(Spells.SPELL_SMITE);
        addSpell(Spells.SPELL_STRONGWALL);
        addSpell(Spells.SPELL_SUMMON_SOUL);
        addSpell(Spells.SPELL_SUNDER);
        addSpell(Spells.SPELL_TANGLEWEAVE);
        addSpell(Spells.SPELL_TENTACLES);
        addSpell(Spells.SPELL_TORNADO);
        addSpell(Spells.SPELL_TOXIN);
        addSpell(Spells.SPELL_TRUEHIT);
        addSpell(Spells.SPELL_VENOM);
        addSpell(Spells.SPELL_VESSEL);
        addSpell(Spells.SPELL_WARD);
        addSpell(Spells.SPELL_WEAKNESS);
        addSpell(Spells.SPELL_WEB_ARMOUR);
        addSpell(Spells.SPELL_WILD_GROWTH);
        addSpell(Spells.SPELL_WILLOWSPINE);
        addSpell(Spells.SPELL_WIND_OF_AGES);
        addSpell(Spells.SPELL_WISDOM_OF_VYNORA);
        addSpell(Spells.SPELL_WORM_BRAINS);
        addSpell(Spells.SPELL_WRATH_OF_MAGRANON);
        addSpell(Spells.SPELL_ZOMBIE_INFESTATION);
        addSpell(Spells.SPELL_CONTINUUM);
        addSpell(Spells.SPELL_DISEASE);
        addSpell(Spells.SPELL_FIREBALL);
        addSpell(Spells.SPELL_FORECAST);
        addSpell(Spells.SPELL_INCINERATE);
        addSpell(Spells.SPELL_KARMA_BOLT);
        addSpell(Spells.SPELL_KARMA_MISSILE);
        addSpell(Spells.SPELL_KARMA_SLOW);
        addSpell(Spells.SPELL_LIGHTNING);
        addSpell(Spells.SPELL_MIRRORED_SELF);
        addSpell(Spells.SPELL_RUST_MONSTER);
        addSpell(Spells.SPELL_SPROUT_TREES);
        addSpell(Spells.SPELL_STONESKIN);
        addSpell(Spells.SPELL_SUMMON);
        addSpell(Spells.SPELL_SUMMON_SKELETON);
        addSpell(Spells.SPELL_SUMMON_WORG);
        addSpell(Spells.SPELL_SUMMON_WRAITH);
        addSpell(Spells.SPELL_TRUESTRIKE);
        addSpell(Spells.SPELL_WALL_OF_FIRE);
        addSpell(Spells.SPELL_WALL_OF_ICE);
        addSpell(Spells.SPELL_WALL_OF_STONE);
    }
}
