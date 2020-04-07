// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.behaviours;

import java.util.Random;
import com.wurmonline.server.Servers;
import com.wurmonline.server.Point;
import com.wurmonline.server.zones.WaterType;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.items.ItemTemplateFactory;
import com.wurmonline.server.items.ItemTemplate;
import java.util.HashMap;
import javax.annotation.Nullable;
import com.wurmonline.server.items.Item;
import java.util.Map;
import com.wurmonline.server.WurmCalendar;
import com.wurmonline.server.zones.NoSuchZoneException;
import java.util.logging.Level;
import com.wurmonline.server.zones.Zones;
import java.util.logging.Logger;

public class FishEnums
{
    private static final Logger logger;
    private static final byte testTypeId = -1;
    public static final int MIN_DEPTH_SPECIAL_FISH = -100;
    
    static int getWaterDepth(final float posx, final float posy, final boolean isOnSurface) {
        try {
            return (int)(-Zones.calculateHeight(posx, posy, isOnSurface) * 10.0f);
        }
        catch (NoSuchZoneException e) {
            FishEnums.logger.log(Level.WARNING, e.getMessage(), e);
            return 5;
        }
    }
    
    static {
        logger = Logger.getLogger(FishEnums.class.getName());
    }
    
    public enum FeedHeight
    {
        NONE((byte)0), 
        TOP((byte)1), 
        BOTTOM((byte)2), 
        ANY((byte)3), 
        TIME((byte)4);
        
        private final byte typeId;
        
        private FeedHeight(final byte id) {
            this.typeId = id;
        }
        
        public int getTypeId() {
            return this.typeId;
        }
    }
    
    public enum TimeOfDay
    {
        MORNING((byte)0), 
        AFTERNOON((byte)1), 
        EVENING((byte)2), 
        NIGHT((byte)3);
        
        private final byte typeId;
        
        private TimeOfDay(final byte id) {
            this.typeId = id;
        }
        
        public int getTypeId() {
            return this.typeId;
        }
        
        public static final TimeOfDay getTimeOfDay() {
            if (WurmCalendar.getHour() < 4 || (WurmCalendar.getHour() == 4 && WurmCalendar.getMinute() <= 30)) {
                return TimeOfDay.NIGHT;
            }
            if (WurmCalendar.getHour() < 10 || (WurmCalendar.getHour() == 10 && WurmCalendar.getMinute() <= 30)) {
                return TimeOfDay.MORNING;
            }
            if (WurmCalendar.getHour() < 16 || (WurmCalendar.getHour() == 16 && WurmCalendar.getMinute() <= 30)) {
                return TimeOfDay.AFTERNOON;
            }
            if (WurmCalendar.getHour() < 22 || (WurmCalendar.getHour() == 22 && WurmCalendar.getMinute() <= 30)) {
                return TimeOfDay.EVENING;
            }
            return TimeOfDay.NIGHT;
        }
    }
    
    public enum ReelType
    {
        NONE((byte)0, 0, 1347), 
        LIGHT((byte)1, 1372, 1348), 
        MEDIUM((byte)2, 1373, 1349), 
        DEEP_WATER((byte)3, 1374, 1350), 
        PROFESSIONAL((byte)4, 1375, 1351);
        
        private final byte typeId;
        private final int templateId;
        private final int associatedLine;
        private static final ReelType[] types;
        private static final Map<Integer, ReelType> byTemplateId;
        
        private ReelType(final byte id, final int templateId, final int associatedLine) {
            this.typeId = id;
            this.templateId = templateId;
            this.associatedLine = associatedLine;
        }
        
        public byte getTypeId() {
            return this.typeId;
        }
        
        public int getTemplateId() {
            return this.templateId;
        }
        
        public int getAssociatedLineTemplateId() {
            return this.associatedLine;
        }
        
        public static final int getLength() {
            return ReelType.types.length;
        }
        
        public static ReelType fromInt(final int id) {
            if (id >= getLength()) {
                return ReelType.types[0];
            }
            return ReelType.types[id & 0xFF];
        }
        
        public static ReelType fromItem(@Nullable final Item reel) {
            if (reel == null) {
                return ReelType.NONE;
            }
            final ReelType reelType = ReelType.byTemplateId.get(reel.getTemplateId());
            if (reelType == null) {
                return ReelType.NONE;
            }
            return reelType;
        }
        
        static {
            byTemplateId = new HashMap<Integer, ReelType>();
            types = values();
            for (final ReelType rt : ReelType.types) {
                ReelType.byTemplateId.put(rt.getTemplateId(), rt);
            }
        }
    }
    
    public enum FloatType
    {
        NONE((byte)0, 0), 
        FEATHER((byte)1, 1352), 
        TWIG((byte)2, 1353), 
        MOSS((byte)3, 1354), 
        BARK((byte)4, 1355);
        
        private final byte typeId;
        private final int templateId;
        private static final FloatType[] types;
        private static final Map<Integer, FloatType> byTemplateId;
        
        private FloatType(final byte id, final int templateId) {
            this.typeId = id;
            this.templateId = templateId;
        }
        
        public byte getTypeId() {
            return this.typeId;
        }
        
        public int getTemplateId() {
            return this.templateId;
        }
        
        public static final int getLength() {
            return FloatType.types.length;
        }
        
        public static FloatType fromInt(final int id) {
            if (id >= getLength()) {
                return FloatType.types[0];
            }
            return FloatType.types[id & 0xFF];
        }
        
        public static FloatType fromItem(@Nullable final Item afloat) {
            if (afloat == null) {
                return FloatType.NONE;
            }
            final FloatType floatType = FloatType.byTemplateId.get(afloat.getTemplateId());
            if (floatType == null) {
                return FloatType.NONE;
            }
            return floatType;
        }
        
        static {
            byTemplateId = new HashMap<Integer, FloatType>();
            types = values();
            for (final FloatType ft : FloatType.types) {
                FloatType.byTemplateId.put(ft.getTemplateId(), ft);
            }
        }
    }
    
    public enum HookType
    {
        NONE((byte)0, 0), 
        WOOD((byte)1, 1356), 
        METAL((byte)2, 1357), 
        BONE((byte)3, 1358);
        
        private final byte typeId;
        private final int templateId;
        private static final HookType[] types;
        private static final Map<Integer, HookType> byTemplateId;
        
        private HookType(final byte id, final int templateId) {
            this.typeId = id;
            this.templateId = templateId;
        }
        
        public byte getTypeId() {
            return this.typeId;
        }
        
        public int getTemplateId() {
            return this.templateId;
        }
        
        public static final int getLength() {
            return HookType.types.length;
        }
        
        public static HookType fromInt(final int id) {
            if (id >= getLength()) {
                return HookType.types[0];
            }
            return HookType.types[id & 0xFF];
        }
        
        public static HookType fromItem(@Nullable final Item hook) {
            if (hook == null) {
                return HookType.NONE;
            }
            for (final HookType ft : HookType.types) {
                if (ft.getTemplateId() == hook.getTemplateId()) {
                    return ft;
                }
            }
            return HookType.NONE;
        }
        
        static {
            byTemplateId = new HashMap<Integer, HookType>();
            types = values();
            for (final HookType ht : HookType.types) {
                HookType.byTemplateId.put(ht.getTemplateId(), ht);
            }
        }
    }
    
    public enum BaitType
    {
        NONE((byte)0, -1, 1.0f), 
        FLY((byte)1, 1359, 0.1f), 
        CHEESE((byte)2, 1360, 1.5f), 
        DOUGH((byte)3, 1361, 2.0f), 
        WURM((byte)4, 1362, 1.0f), 
        SARDINE((byte)5, 1337, 2.2f), 
        ROACH((byte)6, 162, 2.8f), 
        PERCH((byte)7, 163, 3.0f), 
        MINNOW((byte)8, 1338, 2.5f), 
        FISH_BAIT((byte)9, 1363, 0.2f), 
        GRUB((byte)10, 1364, 0.5f), 
        WHEAT((byte)11, 1365, 0.2f), 
        CORN((byte)12, 1366, 0.1f);
        
        private final byte typeId;
        private final int templateId;
        private final float crumbles;
        private static final BaitType[] types;
        private static final Map<Integer, BaitType> byTemplateId;
        
        private BaitType(final byte id, final int templateId, final float crumbles) {
            this.typeId = id;
            this.templateId = templateId;
            this.crumbles = crumbles;
        }
        
        public byte getTypeId() {
            return this.typeId;
        }
        
        public int getTemplateId() {
            return this.templateId;
        }
        
        public float getCrumbleFactor() {
            return this.crumbles;
        }
        
        public static final int getLength() {
            return BaitType.types.length;
        }
        
        public static BaitType fromInt(final int id) {
            if (id >= getLength()) {
                return BaitType.types[0];
            }
            return BaitType.types[id & 0xFF];
        }
        
        public static BaitType fromItem(@Nullable final Item bait) {
            if (bait == null) {
                return BaitType.NONE;
            }
            final BaitType baitType = BaitType.byTemplateId.get(bait.getTemplateId());
            if (baitType == null) {
                return BaitType.NONE;
            }
            return baitType;
        }
        
        static {
            byTemplateId = new HashMap<Integer, BaitType>();
            types = values();
            for (final BaitType bt : BaitType.types) {
                BaitType.byTemplateId.put(bt.getTemplateId(), bt);
            }
        }
    }
    
    public enum FishData
    {
        NONE((byte)0, "unknown", 0, true, FeedHeight.NONE, 0, 0, false, 0, 0, "model.creature.fish", 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, new byte[0], new int[] { 1, 1, 1, 1 }, new int[0], new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }), 
        ROACH((byte)1, "roach", 162, true, FeedHeight.BOTTOM, 0, 30, false, 1, 30, "model.creature.fish.roach", 1.5f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, new byte[] { 2, 3, 7 }, new int[] { 4, 4, 3, 1 }, new int[] { 705, 1343, 1344, 1372 }, new int[] { 8, 5, 1, 5, 8, 1, 0, 0, 1, 5, 10, 5, 5 }), 
        PERCH((byte)2, "perch", 163, true, FeedHeight.ANY, 0, 40, false, 1, 40, "model.creature.fish.perch", 1.5f, 1.0f, 2.0f, 2.0f, 2.0f, 2.0f, new byte[] { 2, 3, 7 }, new int[] { 4, 3, 3, 2 }, new int[] { 705, 1343, 1344, 1372 }, new int[] { 8, 5, 5, 10, 5, 5, 1, 0, 5, 5, 8, 5, 5 }), 
        TROUT((byte)3, "brook trout", 165, true, FeedHeight.BOTTOM, 60, 150, false, 2, 50, "model.creature.fish.trout", 1.0f, 1.0f, 4.0f, 4.0f, 4.0f, 4.0f, new byte[] { 2, 3 }, new int[] { 4, 2, 4, 2 }, new int[] { 705, 1373, 1374 }, new int[] { 1, 10, 1, 5, 5, 5, 5, 5, 5, 1, 5, 1, 5 }), 
        PIKE((byte)4, "pike", 157, true, FeedHeight.BOTTOM, 10, 100, false, 4, 50, "model.creature.fish.pike", 0.74f, 1.0f, 8.0f, 10.0f, 5.0f, 2.0f, new byte[] { 3 }, new int[] { 3, 2, 4, 4 }, new int[] { 1372, 1373 }, new int[] { 1, 5, 1, 1, 1, 5, 5, 5, 5, 1, 1, 10, 1 }), 
        CATFISH((byte)5, "catfish", 160, true, FeedHeight.BOTTOM, 20, 100, false, 4, 50, "model.creature.fish.catfish", 0.85f, 1.0f, 20.0f, 22.0f, 13.0f, 5.0f, new byte[] { 3, 4 }, new int[] { 4, 4, 3, 2 }, new int[] { 705, 1372, 1373, 1374 }, new int[] { 1, 5, 5, 5, 5, 5, 7, 5, 5, 10, 5, 5, 5 }), 
        SNOOK((byte)6, "snook", 161, true, FeedHeight.TIME, 10, 250, false, 5, 50, "model.creature.fish.snook", 0.85f, 1.0f, 15.0f, 17.0f, 14.0f, 8.0f, new byte[] { 4 }, new int[] { 4, 2, 3, 4 }, new int[] { 1372, 1373, 1374 }, new int[] { 0, 1, 1, 1, 1, 1, 1, 5, 5, 5, 1, 1, 10 }), 
        HERRING((byte)7, "herring", 159, true, FeedHeight.TIME, 10, 150, false, 1, 50, "model.creature.fish.herring", 2.35f, 1.0f, 10.0f, 15.0f, 10.0f, 7.0f, new byte[] { 3, 4 }, new int[] { 2, 3, 4, 2 }, new int[] { 1372, 1373 }, new int[] { 1, 5, 5, 5, 1, 5, 5, 1, 1, 1, 10, 1, 1 }), 
        CARP((byte)8, "carp", 164, true, FeedHeight.ANY, 5, 200, false, 3, 50, "model.creature.fish.carp", 0.5f, 1.0f, 13.0f, 18.0f, 11.0f, 8.0f, new byte[] { 2, 3 }, new int[] { 4, 4, 3, 3 }, new int[] { 705, 1344, 1372, 1373, 1374, 1375 }, new int[] { 10, 5, 5, 5, 5, 1, 1, 1, 1, 5, 5, 5, 5 }), 
        BASS((byte)9, "smallmouth bass", 158, true, FeedHeight.BOTTOM, 0, 60, false, 2, 50, "model.creature.fish.bass", 1.03f, 1.0f, 15.0f, 21.0f, 14.0f, 11.0f, new byte[] { 3, 4 }, new int[] { 2, 2, 4, 4 }, new int[] { 1344, 1372, 1373 }, new int[] { 1, 1, 1, 1, 10, 1, 1, 1, 1, 5, 1, 1, 1 }), 
        SALMON((byte)10, "salmon", 1335, true, FeedHeight.TIME, 0, 75, false, 3, 50, "model.creature.fish.salmon", 1.0f, 1.0f, 25.0f, 30.0f, 30.0f, 15.0f, new byte[] { 3, 4 }, new int[] { 4, 1, 4, 2 }, new int[] { 705, 1372, 1373 }, new int[] { 1, 10, 1, 1, 5, 5, 5, 5, 5, 1, 5, 1, 1 }), 
        OCTOPUS((byte)11, "octopus", 572, true, FeedHeight.ANY, 200, 800, true, 3, 50, "model.creature.fish.octopus.black", 1.0f, 1.0f, 30.0f, 40.0f, 45.0f, 14.0f, new byte[] { 4 }, new int[] { 2, 4, 1, 4 }, new int[] { 1374, 1375 }, new int[] { 0, 0, 10, 1, 1, 5, 1, 1, 1, 5, 1, 1, 1 }), 
        MARLIN((byte)12, "marlin", 569, true, FeedHeight.TOP, 250, 1000, true, 6, 50, "model.creature.fish.marlin", 0.343f, 1.0f, 50.0f, 50.0f, 45.0f, 18.0f, new byte[] { 4 }, new int[] { 4, 2, 4, 3 }, new int[] { 1375 }, new int[] { 0, 0, 1, 1, 1, 5, 5, 5, 10, 0, 1, 1, 1 }), 
        BLUESHARK((byte)13, "blue shark", 570, true, FeedHeight.ANY, 250, 1000, true, 5, 50, "model.creature.fish.blueshark", 1.0f, 1.0f, 45.0f, 50.0f, 45.0f, 14.0f, new byte[] { 4 }, new int[] { 4, 3, 4, 2 }, new int[] { 1375 }, new int[] { 0, 0, 1, 1, 1, 10, 5, 5, 5, 0, 1, 1, 1 }), 
        DORADO((byte)14, "dorado", 574, true, FeedHeight.TOP, 150, 600, true, 4, 50, "model.creature.fish.dorado", 1.0f, 1.0f, 30.0f, 50.0f, 45.0f, 13.0f, new byte[] { 4 }, new int[] { 4, 2, 4, 3 }, new int[] { 1374, 1375 }, new int[] { 0, 1, 1, 1, 10, 5, 5, 5, 5, 0, 5, 1, 1 }), 
        SAILFISH((byte)15, "sailfish", 573, true, FeedHeight.TOP, 200, 800, true, 4, 50, "model.creature.fish.sailfish", 1.0f, 1.0f, 40.0f, 50.0f, 45.0f, 15.0f, new byte[] { 4 }, new int[] { 4, 2, 4, 3 }, new int[] { 1375 }, new int[] { 0, 0, 0, 1, 1, 5, 5, 5, 5, 0, 10, 0, 0 }), 
        WHITESHARK((byte)16, "white shark", 571, true, FeedHeight.ANY, 150, 1000, true, 5, 50, "model.creature.fish.whiteshark", 1.0f, 1.0f, 42.0f, 50.0f, 45.0f, 14.0f, new byte[] { 4 }, new int[] { 4, 3, 4, 2 }, new int[] { 1375 }, new int[] { 0, 0, 1, 1, 1, 5, 8, 10, 5, 0, 1, 1, 1 }), 
        TUNA((byte)17, "tuna", 575, true, FeedHeight.TOP, 150, 600, true, 2, 50, "model.creature.fish.tuna", 1.0f, 1.0f, 40.0f, 50.0f, 45.0f, 20.0f, new byte[] { 4 }, new int[] { 4, 2, 4, 3 }, new int[] { 1374, 1375 }, new int[] { 0, 1, 1, 5, 1, 5, 10, 5, 5, 0, 1, 0, 1 }), 
        MINNOW((byte)18, "minnow", 1338, false, FeedHeight.ANY, 0, 20, false, 1, 10, "model.creature.fish.minnow", 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, new byte[] { 2, 3, 7 }, new int[] { 4, 2, 4, 1 }, new int[] { 1344, 1343, 1372 }, new int[] { 5, 10, 1, 1, 1, 0, 0, 0, 0, 1, 5, 1, 1 }), 
        LOACH((byte)19, "loach", 1339, false, FeedHeight.ANY, 10, 50, false, 2, 40, "model.creature.fish.loach", 1.0f, 1.0f, 5.0f, 7.0f, 3.0f, 2.0f, new byte[] { 2, 3 }, new int[] { 1, 4, 2, 3 }, new int[] { 705, 1344, 1372, 1373 }, new int[] { 5, 5, 1, 1, 5, 1, 0, 0, 1, 5, 1, 1, 10 }), 
        WURMFISH((byte)20, "wurmfish", 1340, false, FeedHeight.BOTTOM, 40, 1000, false, 3, 50, "model.creature.fish.wurmfish", 1.0f, 1.0f, 21.0f, 33.0f, 11.0f, 4.0f, new byte[] { 2, 3 }, new int[] { 2, 3, 1, 4 }, new int[] { 705, 1344, 1372, 1373 }, new int[] { 1, 5, 1, 1, 5, 5, 5, 1, 5, 5, 1, 10, 1 }), 
        SARDINE((byte)21, "sardine", 1337, true, FeedHeight.NONE, 0, 20, false, 1, 10, "model.creature.fish.sardine", 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, new byte[] { 2, 1, 3, 7 }, new int[] { 4, 3, 4, 2 }, new int[] { 1343 }, new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }), 
        CLAM((byte)22, "clam", 1394, true, FeedHeight.ANY, 0, 0, false, 1, 0, "model.creature.fish.clam", 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, new byte[0], new int[] { 1, 1, 1, 1 }, new int[0], new int[] { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 });
        
        private final byte typeId;
        private String name;
        private final int templateId;
        private ItemTemplate template;
        private final boolean onSurface;
        private final FeedHeight feedHeight;
        private final int minDepth;
        private final int maxDepth;
        private final boolean isSpecialFish;
        private final int damageMod;
        private final int minWeight;
        private final String modelName;
        private final float scale;
        private final float baseSpeed;
        private final float bodyStrength;
        private final float bodyStamina;
        private final float bodyControl;
        private final float mindSpeed;
        private boolean inWater;
        private boolean inPond;
        private boolean inLake;
        private boolean inSea;
        private boolean inShallows;
        private boolean useFishingPole;
        private boolean useFishingNet;
        private boolean useSpear;
        private boolean useReelBasic;
        private boolean useReelFine;
        private boolean useReelWater;
        private boolean useReelProfessional;
        private final int[] feeds;
        private final int[] baits;
        private static final FishData[] types;
        private static final Map<Integer, FishData> byTemplateId;
        
        private FishData(final byte typeId, final String name, final int templateId, final boolean onSurface, final FeedHeight feedHeight, final int minDepth, final int maxDepth, final boolean specialFish, final int damageMod, final int minWeight, final String modelName, final float scale, final float baseSpeed, final float bodyStrength, final float bodyStamina, final float bodyControl, final float mindSpeed, final byte[] depths, final int[] feeds, final int[] reels, final int[] baits) {
            this.template = null;
            this.inWater = false;
            this.inPond = false;
            this.inLake = false;
            this.inSea = false;
            this.inShallows = false;
            this.useFishingNet = false;
            this.useSpear = false;
            this.useReelBasic = false;
            this.useReelFine = false;
            this.useReelWater = false;
            this.useReelProfessional = false;
            this.typeId = typeId;
            this.name = name;
            this.templateId = templateId;
            this.onSurface = onSurface;
            this.feedHeight = feedHeight;
            this.minDepth = minDepth;
            this.maxDepth = maxDepth;
            this.isSpecialFish = specialFish;
            this.damageMod = damageMod;
            this.minWeight = minWeight;
            this.modelName = modelName;
            this.scale = scale;
            this.baseSpeed = baseSpeed;
            this.bodyStrength = bodyStrength;
            this.bodyStamina = bodyStamina;
            this.bodyControl = bodyControl;
            this.mindSpeed = mindSpeed;
            this.assignDepths(depths);
            this.feeds = feeds;
            if (typeId > 0) {
                this.assignReels(reels);
            }
            this.baits = baits;
        }
        
        public int getTypeId() {
            return this.typeId;
        }
        
        public String getName() {
            return this.name;
        }
        
        public int getTemplateId() {
            return this.templateId;
        }
        
        public boolean inCave() {
            return !this.onSurface;
        }
        
        public boolean onSurface() {
            return this.onSurface;
        }
        
        public boolean inWater() {
            return this.inWater;
        }
        
        public boolean inPond() {
            return this.inPond;
        }
        
        public boolean inLake() {
            return this.inLake;
        }
        
        public boolean inSea() {
            return this.inSea;
        }
        
        public boolean inShallows() {
            return this.inShallows;
        }
        
        public FeedHeight getFeedHeight() {
            return this.feedHeight;
        }
        
        public int getMinDepth() {
            return this.minDepth;
        }
        
        public int getMaxDepth() {
            return this.maxDepth;
        }
        
        public String getModelName() {
            return this.modelName;
        }
        
        public float getScaleMod() {
            return 1.0f / this.scale;
        }
        
        public float getBaseSpeed() {
            return this.baseSpeed;
        }
        
        public boolean isSpecialFish() {
            return this.isSpecialFish;
        }
        
        public float getBodyStrength() {
            return this.bodyStrength;
        }
        
        public float getBodyStamina() {
            return this.bodyStamina;
        }
        
        public float getBodyControl() {
            return this.bodyControl;
        }
        
        public float getMindSpeed() {
            return this.mindSpeed;
        }
        
        public int getDamageMod() {
            return this.damageMod;
        }
        
        public int getMinWeight() {
            return this.minWeight;
        }
        
        public boolean useFishingPole() {
            return this.useFishingPole;
        }
        
        public boolean useFishingNet() {
            return this.useFishingNet;
        }
        
        public boolean useSpear() {
            return this.useSpear;
        }
        
        public boolean useReelBasic() {
            return this.useReelBasic;
        }
        
        public boolean useReelFine() {
            return this.useReelFine;
        }
        
        public boolean useReelWater() {
            return this.useReelWater;
        }
        
        public boolean useReelProfessional() {
            return this.useReelProfessional;
        }
        
        public int[] feeds() {
            return this.feeds;
        }
        
        public int[] baits() {
            return this.baits;
        }
        
        private void assignDepths(final byte[] depths) {
            for (final int depth : depths) {
                switch (depth) {
                    case 1: {
                        this.inWater = true;
                        break;
                    }
                    case 2: {
                        this.inPond = true;
                        break;
                    }
                    case 3: {
                        this.inLake = true;
                        break;
                    }
                    case 4: {
                        this.inSea = true;
                        break;
                    }
                    case 7: {
                        this.inShallows = true;
                        break;
                    }
                }
            }
        }
        
        private void assignReels(final int[] reels) {
            for (final int reel : reels) {
                switch (reel) {
                    case 1343: {
                        this.useFishingNet = true;
                        break;
                    }
                    case 705: {
                        this.useSpear = true;
                        break;
                    }
                    case 1344: {
                        this.useFishingPole = true;
                        break;
                    }
                    case 1372: {
                        this.useReelBasic = true;
                        break;
                    }
                    case 1373: {
                        this.useReelFine = true;
                        break;
                    }
                    case 1374: {
                        this.useReelWater = true;
                        break;
                    }
                    case 1375: {
                        this.useReelProfessional = true;
                        break;
                    }
                }
            }
        }
        
        @Nullable
        public ItemTemplate getTemplate() {
            if (this.templateId == 0) {
                return null;
            }
            if (this.template != null) {
                return this.template;
            }
            try {
                this.template = ItemTemplateFactory.getInstance().getTemplate(this.templateId);
            }
            catch (NoSuchTemplateException ex) {}
            return this.template;
        }
        
        public float getTemplateDifficulty() {
            if (this.getTemplate() != null) {
                return this.template.getDifficulty();
            }
            return 100.0f;
        }
        
        private float addDifficultyDepth(final float posx, final float posy, final boolean isOnSurface) {
            final int tilex = (int)posx >> 2;
            final int tiley = (int)posy >> 2;
            final byte waterType = WaterType.getWaterType(tilex, tiley, isOnSurface);
            float extraWaterTypeDifficulty = 0.0f;
            switch (waterType) {
                case 1:
                case 2: {
                    if (!this.inPond()) {
                        extraWaterTypeDifficulty = 10.0f;
                        break;
                    }
                    break;
                }
                case 3: {
                    if (!this.inLake()) {
                        extraWaterTypeDifficulty = 15.0f;
                        break;
                    }
                    break;
                }
                case 4: {
                    if (!this.inSea()) {
                        extraWaterTypeDifficulty = 20.0f;
                        break;
                    }
                    break;
                }
                case 5:
                case 6: {
                    if (!this.inShallows()) {
                        extraWaterTypeDifficulty = 10.0f;
                        break;
                    }
                    break;
                }
            }
            final int waterDepth = FishEnums.getWaterDepth(posx, posy, isOnSurface);
            int heightDiff = 0;
            if (waterDepth < this.minDepth) {
                heightDiff = Math.min(Math.abs(this.minDepth - waterDepth), 250);
                return extraWaterTypeDifficulty + Math.min(heightDiff / 10.0f, 1.0f);
            }
            if (waterDepth > this.maxDepth) {
                heightDiff = Math.min(Math.abs(waterDepth - this.maxDepth), 250);
                return extraWaterTypeDifficulty + Math.min(heightDiff / 10.0f, 1.0f);
            }
            return extraWaterTypeDifficulty;
        }
        
        private float addDifficultyFeeding(final Item fishingFloat) {
            if (fishingFloat == null) {
                return 10.0f;
            }
            Label_0187: {
                switch (this.getFeedHeight()) {
                    case TOP: {
                        switch (fishingFloat.getTemplateId()) {
                            case 1352: {
                                return -5.0f;
                            }
                            case 1354: {
                                return 8.0f;
                            }
                            case 1353: {
                                return 0.0f;
                            }
                            case 1355: {
                                return 8.0f;
                            }
                            default: {
                                break Label_0187;
                            }
                        }
                        break;
                    }
                    case BOTTOM: {
                        switch (fishingFloat.getTemplateId()) {
                            case 1352: {
                                return 8.0f;
                            }
                            case 1354: {
                                return -5.0f;
                            }
                            case 1353: {
                                return 0.0f;
                            }
                            case 1355: {
                                return 8.0f;
                            }
                            default: {
                                break Label_0187;
                            }
                        }
                        break;
                    }
                    case ANY: {
                        switch (fishingFloat.getTemplateId()) {
                            case 1352: {
                                return 8.0f;
                            }
                            case 1354: {
                                return 8.0f;
                            }
                            case 1353: {
                                return -5.0f;
                            }
                            case 1355: {
                                return 0.0f;
                            }
                            default: {
                                break Label_0187;
                            }
                        }
                        break;
                    }
                    case TIME: {
                        final TimeOfDay tod = TimeOfDay.getTimeOfDay();
                        Label_0367: {
                            switch (tod) {
                                case MORNING: {
                                    switch (fishingFloat.getTemplateId()) {
                                        case 1352: {
                                            return -5.0f;
                                        }
                                        case 1354: {
                                            return 8.0f;
                                        }
                                        case 1353: {
                                            return 0.0f;
                                        }
                                        case 1355: {
                                            return -5.0f;
                                        }
                                        default: {
                                            break Label_0367;
                                        }
                                    }
                                    break;
                                }
                                case AFTERNOON: {
                                    switch (fishingFloat.getTemplateId()) {
                                        case 1352: {
                                            return 8.0f;
                                        }
                                        case 1354: {
                                            return 8.0f;
                                        }
                                        case 1353: {
                                            return -5.0f;
                                        }
                                        case 1355: {
                                            return -5.0f;
                                        }
                                        default: {
                                            break Label_0367;
                                        }
                                    }
                                    break;
                                }
                                case EVENING: {
                                    switch (fishingFloat.getTemplateId()) {
                                        case 1352: {
                                            return 8.0f;
                                        }
                                        case 1354: {
                                            return -5.0f;
                                        }
                                        case 1353: {
                                            return 0.0f;
                                        }
                                        case 1355: {
                                            return -5.0f;
                                        }
                                        default: {
                                            break Label_0367;
                                        }
                                    }
                                    break;
                                }
                                case NIGHT: {
                                    switch (fishingFloat.getTemplateId()) {
                                        case 1352: {
                                            return 8.0f;
                                        }
                                        case 1354: {
                                            return 8.0f;
                                        }
                                        case 1353: {
                                            return -5.0f;
                                        }
                                        case 1355: {
                                            return -5.0f;
                                        }
                                        default: {
                                            break Label_0187;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
            return 15.0f;
        }
        
        private float addDifficultyTimeOfDay() {
            final TimeOfDay tod = TimeOfDay.getTimeOfDay();
            final int feed = this.feeds[tod.typeId];
            final int diff = (4 - feed) * 5;
            return diff;
        }
        
        private float addDifficultyRod(final Item rod, final Item reel, final Item line, final Item hook) {
            if (rod.getTemplateId() == 1343) {
                if (this.useFishingNet) {
                    return 0.0f;
                }
                return 1000.0f;
            }
            else if (rod.getTemplateId() == 705 || rod.getTemplateId() == 707) {
                if (this.useSpear) {
                    return (rod.getTemplateId() == 705) ? 2.0f : 0.0f;
                }
                return 1000.0f;
            }
            else {
                if (line == null || hook == null) {
                    return 1000.0f;
                }
                float diff = 1000.0f;
                if (rod.getTemplateId() == 1344) {
                    if (this.useFishingPole) {
                        diff = -10.0f;
                    }
                    else if (this.useReelBasic) {
                        diff = 5.0f;
                    }
                    else if (this.useReelFine) {
                        diff = 10.0f;
                    }
                    else {
                        diff = 30.0f;
                    }
                }
                else {
                    switch (reel.getTemplateId()) {
                        case 1372: {
                            if (this.useReelBasic) {
                                diff = -10.0f;
                                break;
                            }
                            if (this.useFishingPole) {
                                diff = 5.0f;
                                break;
                            }
                            if (this.useReelFine) {
                                diff = 10.0f;
                                break;
                            }
                            diff = 30.0f;
                            break;
                        }
                        case 1373: {
                            if (this.useReelFine) {
                                diff = -10.0f;
                                break;
                            }
                            if (this.useReelBasic) {
                                diff = 5.0f;
                                break;
                            }
                            if (this.useReelWater) {
                                diff = 10.0f;
                                break;
                            }
                            diff = 30.0f;
                            break;
                        }
                        case 1374: {
                            if (this.useReelWater) {
                                diff = -10.0f;
                                break;
                            }
                            if (this.useReelFine) {
                                diff = 5.0f;
                                break;
                            }
                            if (this.useReelProfessional) {
                                diff = 10.0f;
                                break;
                            }
                            if (this.useReelBasic) {
                                diff = 15.0f;
                                break;
                            }
                            diff = 30.0f;
                            break;
                        }
                        case 1375: {
                            if (this.useReelProfessional) {
                                diff = -10.0f;
                                break;
                            }
                            if (this.useReelWater) {
                                diff = 5.0f;
                                break;
                            }
                            if (this.useReelFine) {
                                diff = 15.0f;
                                break;
                            }
                            diff = 50.0f;
                            break;
                        }
                    }
                }
                if (diff > 0.0f) {
                    switch (hook.getTemplateId()) {
                        case 1358: {
                            diff *= 1.1f;
                            break;
                        }
                        case 1356: {
                            diff *= 1.2f;
                            break;
                        }
                    }
                }
                return diff;
            }
        }
        
        private float addDifficultyBait(final Item bait) {
            final byte baitId = BaitType.fromItem(bait).getTypeId();
            return 10.0f - this.baits[baitId];
        }
        
        public float getDifficulty(final float skill, final float posX, final float posY, final boolean onSurface, final Item rod, final Item reel, final Item line, final Item fishingFloat, final Item hook, final Item bait) {
            if (this.getTypeId() == FishData.CLAM.getTypeId()) {
                return skill - 10.0f;
            }
            float difficulty = this.getTemplateDifficulty();
            difficulty += this.addDifficultyDepth(posX, posY, onSurface);
            difficulty += this.addDifficultyFeeding(fishingFloat);
            difficulty += this.addDifficultyTimeOfDay();
            difficulty += this.addDifficultyRod(rod, reel, line, hook);
            difficulty += this.addDifficultyBait(bait);
            difficulty = Math.min(Math.max(difficulty, -50.0f), 100.0f);
            return difficulty;
        }
        
        private float getChanceDefault(final float skill) {
            float diff = 0.0f;
            if (this.getTemplate() != null) {
                diff = this.template.getDifficulty();
            }
            if (diff > 0.0f) {
                final float flip = 110.0f - diff;
                final float smd = skill - diff;
                final double rad = Math.toRadians(smd);
                final float sin = (float)Math.sin(rad);
                final float mult = 1.0f + sin;
                return flip * mult;
            }
            return 50.0f;
        }
        
        private float multChanceDepth(final float posx, final float posy, final boolean isOnSurface) {
            final int tilex = (int)posx >> 2;
            final int tiley = (int)posy >> 2;
            final byte waterType = WaterType.getWaterType(tilex, tiley, isOnSurface);
            switch (waterType) {
                case 1:
                case 2: {
                    if (!this.inPond()) {
                        return 0.0f;
                    }
                    break;
                }
                case 3: {
                    if (!this.inLake()) {
                        return 0.0f;
                    }
                    break;
                }
                case 4: {
                    if (!this.inSea()) {
                        return 0.0f;
                    }
                    break;
                }
                case 6: {
                    if (!this.inLake() && !this.inShallows()) {
                        return 0.0f;
                    }
                    break;
                }
                case 5: {
                    if (!this.inSea() && !this.inShallows()) {
                        return 0.0f;
                    }
                    break;
                }
            }
            final int waterDepth = FishEnums.getWaterDepth(posx, posy, isOnSurface);
            int heightDiff = 0;
            if (waterDepth < this.minDepth) {
                heightDiff = Math.min(Math.abs(this.minDepth - waterDepth), 250);
                return 1.0f - Math.min(heightDiff / 300.0f, 1.0f);
            }
            if (waterDepth > this.maxDepth) {
                heightDiff = Math.min(Math.abs(waterDepth - this.maxDepth), 250);
                return 1.0f - Math.min(heightDiff / 500.0f, 1.0f);
            }
            return 1.0f;
        }
        
        private float multChanceFeeding(final Item fishingFloat) {
            if (fishingFloat == null) {
                return 0.5f;
            }
            Label_0187: {
                switch (this.getFeedHeight()) {
                    case TOP: {
                        switch (fishingFloat.getTemplateId()) {
                            case 1352: {
                                return 1.2f;
                            }
                            case 1354: {
                                return 0.8f;
                            }
                            case 1353: {
                                return 1.0f;
                            }
                            case 1355: {
                                return 0.8f;
                            }
                            default: {
                                break Label_0187;
                            }
                        }
                        break;
                    }
                    case BOTTOM: {
                        switch (fishingFloat.getTemplateId()) {
                            case 1352: {
                                return 0.8f;
                            }
                            case 1354: {
                                return 1.2f;
                            }
                            case 1353: {
                                return 1.0f;
                            }
                            case 1355: {
                                return 0.8f;
                            }
                            default: {
                                break Label_0187;
                            }
                        }
                        break;
                    }
                    case ANY: {
                        switch (fishingFloat.getTemplateId()) {
                            case 1352: {
                                return 0.8f;
                            }
                            case 1354: {
                                return 0.8f;
                            }
                            case 1353: {
                                return 1.2f;
                            }
                            case 1355: {
                                return 1.0f;
                            }
                            default: {
                                break Label_0187;
                            }
                        }
                        break;
                    }
                    case TIME: {
                        final TimeOfDay tod = TimeOfDay.getTimeOfDay();
                        Label_0367: {
                            switch (tod) {
                                case MORNING: {
                                    switch (fishingFloat.getTemplateId()) {
                                        case 1352: {
                                            return 1.2f;
                                        }
                                        case 1354: {
                                            return 0.8f;
                                        }
                                        case 1353: {
                                            return 1.0f;
                                        }
                                        case 1355: {
                                            return 1.2f;
                                        }
                                        default: {
                                            break Label_0367;
                                        }
                                    }
                                    break;
                                }
                                case AFTERNOON: {
                                    switch (fishingFloat.getTemplateId()) {
                                        case 1352: {
                                            return 0.8f;
                                        }
                                        case 1354: {
                                            return 0.8f;
                                        }
                                        case 1353: {
                                            return 1.2f;
                                        }
                                        case 1355: {
                                            return 1.2f;
                                        }
                                        default: {
                                            break Label_0367;
                                        }
                                    }
                                    break;
                                }
                                case EVENING: {
                                    switch (fishingFloat.getTemplateId()) {
                                        case 1352: {
                                            return 0.8f;
                                        }
                                        case 1354: {
                                            return 1.2f;
                                        }
                                        case 1353: {
                                            return 1.0f;
                                        }
                                        case 1355: {
                                            return 1.2f;
                                        }
                                        default: {
                                            break Label_0367;
                                        }
                                    }
                                    break;
                                }
                                case NIGHT: {
                                    switch (fishingFloat.getTemplateId()) {
                                        case 1352: {
                                            return 0.8f;
                                        }
                                        case 1354: {
                                            return 0.8f;
                                        }
                                        case 1353: {
                                            return 1.2f;
                                        }
                                        case 1355: {
                                            return 1.2f;
                                        }
                                        default: {
                                            break Label_0187;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                }
            }
            return 0.5f;
        }
        
        private float multChanceTimeOfDay() {
            final TimeOfDay tod = TimeOfDay.getTimeOfDay();
            final int feed = this.feeds[tod.typeId];
            final int chance = 1 + (feed - 4) / 8;
            return chance;
        }
        
        private float multChanceRod(final Item rod, final Item reel, final Item line, final Item hook, final Item bait) {
            if (rod.getTemplateId() == 1343) {
                if (this.useFishingNet) {
                    return 1.0f;
                }
                return 0.0f;
            }
            else if (rod.getTemplateId() == 705 || rod.getTemplateId() == 707) {
                if (this.useSpear) {
                    return (rod.getTemplateId() == 705) ? 0.8f : 1.0f;
                }
                return 0.0f;
            }
            else {
                if (line == null || hook == null) {
                    return 0.0f;
                }
                float chance = 0.0f;
                if (rod.getTemplateId() == 1344) {
                    if (this.useFishingPole) {
                        chance = 1.0f;
                    }
                    else if (this.useReelBasic) {
                        chance = 0.75f;
                    }
                    else if (this.useReelFine) {
                        chance = 0.5f;
                    }
                    else {
                        chance = 0.1f;
                    }
                }
                else {
                    switch (reel.getTemplateId()) {
                        case 1372: {
                            if (this.useReelBasic) {
                                chance = 1.0f;
                                break;
                            }
                            if (this.useFishingPole) {
                                chance = 0.75f;
                                break;
                            }
                            if (this.useReelFine) {
                                chance = 0.75f;
                                break;
                            }
                            if (this.useReelWater) {
                                chance = 0.25f;
                                break;
                            }
                            chance = 0.1f;
                            break;
                        }
                        case 1373: {
                            if (this.useReelFine) {
                                chance = 1.0f;
                                break;
                            }
                            if (this.useReelBasic) {
                                chance = 0.75f;
                                break;
                            }
                            if (this.useFishingPole) {
                                chance = 0.25f;
                                break;
                            }
                            if (this.useReelWater) {
                                chance = 0.5f;
                                break;
                            }
                            chance = 0.1f;
                            break;
                        }
                        case 1374: {
                            if (this.useReelWater) {
                                chance = 1.0f;
                                break;
                            }
                            if (this.useReelFine) {
                                chance = 0.75f;
                                break;
                            }
                            if (this.useReelProfessional) {
                                chance = 0.5f;
                                break;
                            }
                            chance = 0.1f;
                            break;
                        }
                        case 1375: {
                            if (this.useReelProfessional) {
                                chance = 1.0f;
                                break;
                            }
                            if (this.useReelWater) {
                                chance = 0.85f;
                                break;
                            }
                            if (this.useReelFine) {
                                chance = 0.45f;
                                break;
                            }
                            chance = 0.1f;
                            break;
                        }
                    }
                }
                if (chance > 0.0f) {
                    switch (hook.getTemplateId()) {
                        case 1358: {
                            chance *= 0.9f;
                            break;
                        }
                        case 1356: {
                            chance *= 0.8f;
                            break;
                        }
                    }
                }
                return chance * this.multChanceBait(bait);
            }
        }
        
        private float multChanceBait(final Item bait) {
            final byte baitId = BaitType.fromItem(bait).getTypeId();
            if (this.typeId == -1) {
                System.out.println(this.name + "(bait):" + baitId + " " + this.baits[baitId]);
            }
            switch (this.baits[baitId]) {
                case 0: {
                    return 0.0f;
                }
                case 1: {
                    return 0.8f;
                }
                case 2: {
                    return 0.82f;
                }
                case 3: {
                    return 0.84f;
                }
                case 4: {
                    return 0.86f;
                }
                case 5: {
                    return 0.88f;
                }
                case 6: {
                    return 0.9f;
                }
                case 7: {
                    return 0.925f;
                }
                case 8: {
                    return 0.95f;
                }
                case 9: {
                    return 0.975f;
                }
                case 10: {
                    return 1.0f;
                }
                default: {
                    return 0.0f;
                }
            }
        }
        
        public Point getSpecialSpot(final int zoneX, final int zoneY, final int season) {
            final Random r = new Random(this.getTypeId() * 5 + Servers.localServer.id * 100 + season);
            final int rx = zoneX * 128 + 5 + r.nextInt(118);
            final int ry = zoneY * 128 + 5 + r.nextInt(118);
            return new Point(rx, ry, this.getTemplateId());
        }
        
        public float getChance(final float skill, final Item rod, final Item reel, final Item line, final Item fishingFloat, final Item hook, final Item bait, final float posX, final float posY, final boolean onSurface) {
            if (this.onSurface() != onSurface) {
                return 0.0f;
            }
            float chance = this.getChanceDefault(skill);
            if (this.typeId == -1) {
                System.out.println(this.name + "(default):" + chance);
            }
            chance *= this.multChanceDepth(posX, posY, onSurface);
            if (this.typeId == -1) {
                System.out.println(this.name + "(depth):" + chance);
            }
            chance *= this.multChanceFeeding(fishingFloat);
            if (this.typeId == -1) {
                System.out.println(this.name + "(feed):" + chance);
            }
            chance *= this.multChanceTimeOfDay();
            if (this.typeId == -1) {
                System.out.println(this.name + "(time):" + chance);
            }
            chance *= this.multChanceRod(rod, reel, line, hook, bait);
            if (this.typeId == -1) {
                System.out.println(this.name + "(rod):" + chance + " " + (bait == null));
            }
            if (this.isSpecialFish && chance > 0.0f) {
                final int tilex = (int)posX >> 2;
                final int tiley = (int)posY >> 2;
                final int season = WurmCalendar.getSeasonNumber();
                final int zoneX = tilex / 128;
                final int zoneY = tiley / 128;
                final Point specialSpot = this.getSpecialSpot(zoneX, zoneY, season);
                final int farAwayX = Math.abs(specialSpot.getX() - tilex);
                final int farAwayY = Math.abs(specialSpot.getY() - tiley);
                final int farAway = Math.max(farAwayX, farAwayY);
                final float rt2 = 15.0f;
                float nc = 0.0f;
                float ht = 0.0f;
                try {
                    ht = Zones.calculateHeight(posX, posY, onSurface) * 10.0f;
                }
                catch (NoSuchZoneException ex) {}
                if (farAway <= 15.0f && ht < -100.0f) {
                    final float dpt = 6.0f;
                    final double rad = Math.toRadians(farAway * 6.0f);
                    nc = (float)Math.cos(rad);
                }
                chance *= nc;
                if (this.typeId == -1) {
                    System.out.println(this.name + "(special):" + farAway + " " + chance + " " + nc);
                }
            }
            return chance;
        }
        
        public static final int getLength() {
            return FishData.types.length;
        }
        
        public static FishData fromInt(final int id) {
            if (id >= getLength()) {
                return FishData.types[0];
            }
            return FishData.types[id & 0xFF];
        }
        
        @Nullable
        public static FishData fromName(final String name) {
            for (final FishData fd : FishData.types) {
                if (fd.getName().equalsIgnoreCase(name)) {
                    return fd;
                }
            }
            return null;
        }
        
        public static FishData fromItem(@Nullable final Item fish) {
            if (fish == null) {
                return FishData.NONE;
            }
            final FishData fishData = FishData.byTemplateId.get(fish.getTemplateId());
            if (fishData == null) {
                return FishData.NONE;
            }
            return fishData;
        }
        
        static {
            byTemplateId = new HashMap<Integer, FishData>();
            types = values();
            for (final FishData fd : FishData.types) {
                FishData.byTemplateId.put(fd.getTemplateId(), fd);
            }
        }
    }
}
