// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.server.items;

import com.wurmonline.server.spells.SpellEffect;
import com.wurmonline.server.spells.Spell;
import com.wurmonline.server.spells.EnchantUtil;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.villages.DeadVillage;
import com.wurmonline.server.players.Player;
import com.wurmonline.shared.util.MaterialUtilities;
import com.wurmonline.server.Server;
import java.util.ArrayList;
import java.util.HashMap;

public class FragmentUtilities
{
    private static final int DIFFRANGE_TRASH = 0;
    private static final int DIFFRANGE_0_15 = 1;
    private static final int DIFFRANGE_15_30 = 2;
    private static final int DIFFRANGE_30_40 = 3;
    private static final int DIFFRANGE_40_50 = 4;
    private static final int DIFFRANGE_50_60 = 5;
    private static final int DIFFRANGE_60_70 = 6;
    private static final int DIFFRANGE_70_80 = 7;
    private static final int DIFFRANGE_80_90 = 8;
    private static final int DIFFRANGE_90_100 = 9;
    private static int[] diffTrash;
    private static int[] diff0_15;
    private static int[] diff15_30;
    private static int[] diff30_40;
    private static int[] diff40_50;
    private static int[] diff50_60;
    private static int[] diff60_70;
    private static int[] diff70_80;
    private static int[] anniversaryGifts;
    private static int[] justStatues;
    private static HashMap<Integer, ArrayList<Integer>> fragmentLists;
    static final byte CLASS_WEAPON = 1;
    static final byte CLASS_ARMOUR = 2;
    static final byte CLASS_TOOL = 3;
    static final byte CLASS_CONTAINER = 4;
    static final byte CLASS_VEHICLE = 5;
    static final byte CLASS_ALL = 6;
    
    public static Fragment getRandomFragmentForSkill(final double skill, final boolean trashPossible) {
        if (skill < 0.0) {
            return null;
        }
        int maxRange = 1;
        if (skill >= 15.0 && skill < 30.0) {
            maxRange = 2;
        }
        else if (skill >= 30.0) {
            maxRange = (int)Math.min(9.0, Math.floor(skill / 10.0) - 1.0);
        }
        int thisRange = Server.rand.nextInt(maxRange + 1);
        if (trashPossible && Server.rand.nextInt(3) != 0) {
            thisRange = Math.max(0, thisRange - 3);
        }
        boolean bumpMaterial = false;
        if (thisRange == 8) {
            thisRange = 5;
            bumpMaterial = true;
        }
        else if (thisRange == 9) {
            thisRange = 6;
            bumpMaterial = true;
        }
        int itemId = -1;
        byte materialId = -1;
        final ArrayList<Integer> possibleItems = FragmentUtilities.fragmentLists.get(thisRange);
        if (possibleItems != null) {
            itemId = possibleItems.get(Server.rand.nextInt(possibleItems.size()));
        }
        if (itemId == -1) {
            return null;
        }
        final ItemTemplate item = ItemTemplateFactory.getInstance().getTemplateOrNull(itemId);
        if (item == null) {
            return null;
        }
        materialId = item.getMaterial();
        if (item.isMetal() && !item.isOre && !item.isMetalLump()) {
            materialId = 93;
        }
        if (item.isMetal() && !MaterialUtilities.isMetal(materialId)) {
            materialId = 93;
        }
        else if (item.isWood() && !MaterialUtilities.isWood(materialId)) {
            materialId = 14;
        }
        if (bumpMaterial && item.isMetal() && materialId == 93) {
            materialId = 94;
        }
        return new Fragment(itemId, materialId);
    }
    
    public static Item createVillageCache(final Player performer, final Item archReport, final DeadVillage vill, final Skill archSkill) {
        if (!archReport.getAuxBit(0) || !archReport.getAuxBit(1) || !archReport.getAuxBit(2) || !archReport.getAuxBit(3)) {
            return null;
        }
        try {
            final Item cache = ItemFactory.createItem(1422, archReport.getCurrentQualityLevel(), vill.getFounderName());
            cache.setName(vill.getDeedName());
            final int statueCount = (int)Math.min(6.0, (archSkill.getKnowledge(0.0) + archReport.getCurrentQualityLevel()) / 28.0);
            final int goodCount = (int)Math.min(6.0, (archSkill.getKnowledge(0.0) + archReport.getCurrentQualityLevel()) / 28.0);
            final float dvModifier = Math.min(2.0f, 0.25f + vill.getTimeSinceDisband() / 120.0f + vill.getTotalAge() / 60.0f);
            int totalGiven = 0;
            for (int i = 0; i < statueCount * dvModifier; ++i) {
                final double power = archSkill.skillCheck(i * 5, archReport, 0.0, false, 1.0f);
                final Item statueFrag = ItemFactory.createItem(1307, (float)Math.min(100.0, Math.max(1.0, power)), vill.getFounderName());
                statueFrag.setRealTemplate(FragmentUtilities.justStatues[Server.rand.nextInt(FragmentUtilities.justStatues.length)]);
                statueFrag.setLastOwnerId(performer.getWurmId());
                if (statueFrag.isMetal()) {
                    if (Server.rand.nextInt(500) == 0) {
                        statueFrag.setMaterial((byte)95);
                    }
                    else if (Server.rand.nextInt(50) == 0) {
                        statueFrag.setMaterial((byte)94);
                    }
                    else {
                        statueFrag.setMaterial((byte)93);
                    }
                }
                cache.insertItem(statueFrag, true);
                ++totalGiven;
            }
            if (archSkill.getKnowledge(0.0) > 50.0) {
                for (int i = 0; i < goodCount * dvModifier; ++i) {
                    final double power = archSkill.skillCheck(i * 10, archReport, 0.0, false, 1.0f);
                    final Item randomFrag = ItemFactory.createItem(1307, (float)Math.min(100.0, Math.max(1.0, power)), vill.getFounderName());
                    int[] list = FragmentUtilities.diff50_60;
                    if (power > 50.0) {
                        list = FragmentUtilities.diff70_80;
                    }
                    else if (power > 30.0) {
                        list = FragmentUtilities.diff60_70;
                    }
                    randomFrag.setRealTemplate(list[Server.rand.nextInt(list.length)]);
                    randomFrag.setLastOwnerId(performer.getWurmId());
                    randomFrag.setMaterial(randomFrag.getRealTemplate().getMaterial());
                    if (randomFrag.isMetal() && !randomFrag.getTemplate().isOre && !randomFrag.getTemplate().isMetalLump()) {
                        if (Server.rand.nextInt(500) == 0) {
                            randomFrag.setMaterial((byte)95);
                        }
                        else if (Server.rand.nextInt(50) == 0) {
                            randomFrag.setMaterial((byte)94);
                        }
                        else {
                            randomFrag.setMaterial((byte)93);
                        }
                    }
                    cache.insertItem(randomFrag, true);
                    ++totalGiven;
                }
            }
            for (int i = totalGiven; i < 10; ++i) {
                final double power = archSkill.skillCheck(i * 5, archReport, 0.0, false, 1.0f);
                final Item randomFrag = ItemFactory.createItem(1307, (float)Math.min(100.0, Math.max(1.0, power)), vill.getFounderName());
                int[] list = FragmentUtilities.diff15_30;
                if (power > 50.0) {
                    list = FragmentUtilities.diff40_50;
                }
                else if (power > 20.0) {
                    list = FragmentUtilities.diff30_40;
                }
                randomFrag.setRealTemplate(list[Server.rand.nextInt(list.length)]);
                randomFrag.setLastOwnerId(performer.getWurmId());
                randomFrag.setMaterial(randomFrag.getRealTemplate().getMaterial());
                if (randomFrag.isMetal() && !randomFrag.getTemplate().isOre && !randomFrag.getTemplate().isMetalLump()) {
                    if (Server.rand.nextInt(500) == 0) {
                        randomFrag.setMaterial((byte)95);
                    }
                    else if (Server.rand.nextInt(50) == 0) {
                        randomFrag.setMaterial((byte)94);
                    }
                    else {
                        randomFrag.setMaterial((byte)93);
                    }
                }
                cache.insertItem(randomFrag, true);
            }
            final Item tokenMini = ItemFactory.createItem(1423, (float)((archSkill.getKnowledge(0.0) + archReport.getCurrentQualityLevel()) / 2.0), vill.getFounderName());
            final double tokenPower = archSkill.skillCheck(50.0, archReport, 0.0, false, 1.0f);
            if (tokenPower > 80.0) {
                tokenMini.setMaterial(getMetalMoonMaterial(100));
            }
            else if (tokenPower > 60.0) {
                tokenMini.setMaterial(getMetalAlloyMaterial(100));
            }
            else if (tokenPower > 30.0) {
                tokenMini.setMaterial(getMetalBaseMaterial((int)tokenPower));
            }
            tokenMini.setName(vill.getDeedName());
            tokenMini.setData(vill.getDeedId());
            tokenMini.setAuxData((byte)((archReport.getAuxData() & 0xFF) >>> 4));
            tokenMini.setAuxBit(7, true);
            tokenMini.setLastOwnerId(performer.getWurmId());
            cache.insertItem(tokenMini, true);
            return cache;
        }
        catch (FailedException | NoSuchTemplateException ex) {
            return null;
        }
    }
    
    public static int getDifficultyForItem(final int itemId, final int materialId) {
        for (final int fragment : FragmentUtilities.diff0_15) {
            if (fragment == itemId) {
                return 5;
            }
        }
        for (final int fragment : FragmentUtilities.diff15_30) {
            if (fragment == itemId) {
                return 15;
            }
        }
        for (final int fragment : FragmentUtilities.diff30_40) {
            if (fragment == itemId) {
                return 25;
            }
        }
        for (final int fragment : FragmentUtilities.diff40_50) {
            if (fragment == itemId) {
                return 35;
            }
        }
        final int[] diff50_60 = FragmentUtilities.diff50_60;
        final int length5 = diff50_60.length;
        int n = 0;
        while (n < length5) {
            final int fragment = diff50_60[n];
            if (fragment == itemId) {
                if (materialId == 94 || materialId == 9) {
                    return 75;
                }
                return 45;
            }
            else {
                ++n;
            }
        }
        final int[] diff60_70 = FragmentUtilities.diff60_70;
        final int length6 = diff60_70.length;
        int n2 = 0;
        while (n2 < length6) {
            final int fragment = diff60_70[n2];
            if (fragment == itemId) {
                if (materialId == 94 || materialId == 9) {
                    return 85;
                }
                return 55;
            }
            else {
                ++n2;
            }
        }
        final int[] diff70_80 = FragmentUtilities.diff70_80;
        for (int length7 = diff70_80.length, n3 = 0; n3 < length7; ++n3) {
            final int fragment = diff70_80[n3];
            if (fragment == itemId) {
                return 65;
            }
        }
        return 10;
    }
    
    public static byte getMetalBaseMaterial(final int identifyLevel) {
        switch (Server.rand.nextInt(Math.max(6, 75 - identifyLevel))) {
            case 0: {
                return 7;
            }
            case 1: {
                return 8;
            }
            case 2: {
                return 10;
            }
            case 3: {
                return 13;
            }
            case 4: {
                return 34;
            }
            case 5: {
                return 12;
            }
            default: {
                return 11;
            }
        }
    }
    
    public static byte getMetalAlloyMaterial(final int identifyLevel) {
        switch (Server.rand.nextInt(Math.max(4, 75 - identifyLevel))) {
            case 0: {
                return 30;
            }
            case 1: {
                return 31;
            }
            case 2: {
                return 96;
            }
            default: {
                return 9;
            }
        }
    }
    
    public static byte getMetalMoonMaterial(final int identifyLevel) {
        switch (Server.rand.nextInt(Math.max(10, 90 - identifyLevel))) {
            case 0: {
                return 67;
            }
            case 1:
            case 2: {
                return 56;
            }
            default: {
                return 57;
            }
        }
    }
    
    public static byte getRandomWoodMaterial(final int identifyLevel) {
        switch (Server.rand.nextInt(Math.max(25, 75 - identifyLevel))) {
            case 0: {
                return 42;
            }
            case 1: {
                return 14;
            }
            case 2: {
                return 91;
            }
            case 3: {
                return 50;
            }
            case 4: {
                return 39;
            }
            case 5: {
                return 45;
            }
            case 6: {
                return 63;
            }
            case 7: {
                return 65;
            }
            case 8: {
                return 49;
            }
            case 9: {
                return 71;
            }
            case 10: {
                return 46;
            }
            case 11: {
                return 43;
            }
            case 12: {
                return 66;
            }
            case 13: {
                return 92;
            }
            case 14: {
                return 41;
            }
            case 15: {
                return 38;
            }
            case 16: {
                return 51;
            }
            case 17: {
                return 44;
            }
            case 18: {
                return 88;
            }
            case 19: {
                return 37;
            }
            case 20: {
                return 90;
            }
            case 21: {
                return 47;
            }
            case 22: {
                return 48;
            }
            case 23: {
                return 64;
            }
            case 24: {
                return 40;
            }
            default: {
                return 14;
            }
        }
    }
    
    public static int getRandomAnniversaryGift() {
        return FragmentUtilities.anniversaryGifts[Server.rand.nextInt(FragmentUtilities.anniversaryGifts.length)];
    }
    
    public static int getRandomEnchantNumber(final int weight) {
        if (weight < 50) {
            return 0;
        }
        final int[] vals = new int[8];
        for (int i = 0; i < 8; ++i) {
            vals[i] = Server.rand.nextInt(1000);
        }
        int closest = vals[0];
        final int weightedVal = (weight - 50) * 20;
        for (int j = 0; j < 8; ++j) {
            if (Math.abs(weightedVal - vals[j]) < Math.abs(weightedVal - closest)) {
                closest = vals[j];
            }
        }
        return Math.min(5, Math.max(1, Math.round(closest / 200.0f)));
    }
    
    public static void addRandomEnchantment(final Item toEnchant, final int enchLevel, final float power) {
        byte itemClass = 6;
        if (toEnchant.isWeapon()) {
            itemClass = 1;
        }
        else if (toEnchant.isArmour()) {
            itemClass = 2;
        }
        else if (toEnchant.isTool()) {
            itemClass = 3;
        }
        else if (toEnchant.isHollow()) {
            itemClass = 4;
        }
        else if (toEnchant.isVehicle()) {
            itemClass = 5;
        }
        final FragmentEnchantment f = FragmentEnchantment.getRandomEnchantment(itemClass, enchLevel);
        if (f == null) {
            return;
        }
        final byte enchantment = f.getEnchantment();
        if (enchantment <= -51) {
            if (!RuneUtilities.canApplyRuneTo(enchantment, toEnchant)) {
                return;
            }
        }
        else {
            if (EnchantUtil.hasNegatingEffect(toEnchant, enchantment) != null) {
                return;
            }
            if (!Spell.mayBeEnchanted(toEnchant)) {
                return;
            }
        }
        ItemSpellEffects effs = toEnchant.getSpellEffects();
        if (effs == null) {
            effs = new ItemSpellEffects(toEnchant.getWurmId());
        }
        SpellEffect e = effs.getSpellEffect(enchantment);
        if (e == null) {
            e = new SpellEffect(toEnchant.getWurmId(), enchantment, power, 20000000);
            effs.addSpellEffect(e);
        }
        else {
            if (power > e.getPower() + power / 5.0f) {
                e.setPower(power);
            }
            else {
                e.setPower(e.getPower() + power / 5.0f);
            }
            if (enchantment != 45 && e.getPower() > 104.0f) {
                e.setPower(104.0f);
            }
        }
    }
    
    private static void addFragment(final int itemId, final int range) {
        ArrayList<Integer> fragments = FragmentUtilities.fragmentLists.get(range);
        if (fragments == null) {
            fragments = new ArrayList<Integer>();
            FragmentUtilities.fragmentLists.put(range, fragments);
        }
        fragments.add(itemId);
    }
    
    static {
        FragmentUtilities.diffTrash = new int[] { 776, 786, 1122, 1121, 1123, 132, 38, 39, 43, 41, 40, 207, 42, 785, 785, 146, 688, 23, 454, 561, 551 };
        FragmentUtilities.diff0_15 = new int[] { 46, 47, 49, 220, 48, 453 };
        FragmentUtilities.diff15_30 = new int[] { 1011, 685, 687, 690, 45, 44, 223, 205, 221, 1411, 784, 778, 217, 218, 188, 451, 1408, 1407, 1416 };
        FragmentUtilities.diff30_40 = new int[] { 77, 813, 1161, 76, 78, 1020, 523, 127, 154, 389, 125, 126, 124, 123, 395, 270, 121, 269, 494, 452, 1406, 1421, 1418 };
        FragmentUtilities.diff40_50 = new int[] { 1022, 1172, 1169, 1165, 1252, 1323, 1324, 1405, 708, 88, 91, 89, 293, 295, 294, 148, 147, 149, 1417, 1420, 1419, 1430 };
        FragmentUtilities.diff50_60 = new int[] { 62, 20, 97, 388, 93, 8, 25, 7, 27, 24, 493, 394, 268, 267, 1325, 1330, 1415 };
        FragmentUtilities.diff60_70 = new int[] { 21, 80, 81, 87, 90, 3, 706, 290, 292, 291, 274, 279, 278, 275, 276, 277, 1328, 1327, 1329, 1326, 710 };
        FragmentUtilities.diff70_80 = new int[] { 976, 973, 978, 975, 974, 280, 284, 281, 282, 283, 83, 86, 287, 286 };
        FragmentUtilities.anniversaryGifts = new int[] { 791, 738, 967, 1306, 1321, 1100, 1297, 972, 1032, 844, 700, 1334, 997 };
        FragmentUtilities.justStatues = new int[] { 1408, 1407, 1416, 1406, 1421, 1418, 1323, 1324, 1405, 1417, 1420, 1419, 1325, 1330, 1415, 1328, 1327, 1329, 1326, 1430 };
        FragmentUtilities.fragmentLists = new HashMap<Integer, ArrayList<Integer>>();
        for (final int fragment : FragmentUtilities.diffTrash) {
            addFragment(fragment, 0);
        }
        for (final int fragment : FragmentUtilities.diff0_15) {
            addFragment(fragment, 1);
        }
        for (final int fragment : FragmentUtilities.diff15_30) {
            addFragment(fragment, 2);
        }
        for (final int fragment : FragmentUtilities.diff30_40) {
            addFragment(fragment, 3);
        }
        for (final int fragment : FragmentUtilities.diff40_50) {
            addFragment(fragment, 4);
        }
        for (final int fragment : FragmentUtilities.diff50_60) {
            addFragment(fragment, 5);
        }
        for (final int fragment : FragmentUtilities.diff60_70) {
            addFragment(fragment, 6);
        }
        for (final int fragment : FragmentUtilities.diff70_80) {
            addFragment(fragment, 7);
        }
    }
    
    public static class Fragment
    {
        private int itemId;
        private int itemMaterial;
        
        Fragment(final int itemId, final int itemMaterial) {
            this.itemId = itemId;
            this.itemMaterial = itemMaterial;
        }
        
        public int getItemId() {
            return this.itemId;
        }
        
        public int getMaterial() {
            return this.itemMaterial;
        }
    }
    
    public enum FragmentEnchantment
    {
        FLAMEAURA((byte)14, new float[] { 0.1f, 0.2f, 0.4f, 0.4f, 0.2f }, new byte[] { 1 }), 
        FROSTBRAND((byte)33, new float[] { 0.1f, 0.2f, 0.5f, 0.3f, 0.2f }, new byte[] { 1 }), 
        BLOODTHIRST((byte)45, new float[] { 0.2f, 0.4f, 0.2f, 0.1f, 0.0f }, new byte[] { 1 }), 
        ROTTINGTOUCH((byte)18, new float[] { 0.0f, 0.0f, 0.2f, 0.4f, 0.4f }, new byte[] { 1 }), 
        NIMBLENESS((byte)32, new float[] { 0.0f, 0.0f, 0.0f, 0.1f, 0.3f }, new byte[] { 1 }), 
        LIFETRANSFER((byte)26, new float[] { 0.0f, 0.0f, 0.0f, 0.1f, 0.3f }, new byte[] { 1 }), 
        MINDSTEALER((byte)31, new float[] { 0.0f, 0.0f, 0.05f, 0.2f, 0.4f }, new byte[] { 1 }), 
        AURASHAREDPAIN((byte)17, new float[] { 0.0f, 0.1f, 0.3f, 0.2f, 0.1f }, new byte[] { 2 }), 
        WEBARMOUR((byte)46, new float[] { 0.0f, 0.0f, 0.2f, 0.3f, 0.2f }, new byte[] { 2 }), 
        WINDOFAGES((byte)16, new float[] { 0.1f, 0.2f, 0.4f, 0.2f, 0.1f }, new byte[] { 1, 3 }), 
        CIRCLEOFCUNNING((byte)13, new float[] { 0.05f, 0.15f, 0.3f, 0.4f, 0.2f }, new byte[] { 1, 3 }), 
        BOTD((byte)47, new float[] { 0.0f, 0.05f, 0.2f, 0.4f, 0.2f }, new byte[] { 1, 3 }), 
        MAGBRASS((byte)(-128), new float[] { 0.05f, 0.1f, 0.05f, 0.025f, 0.0f }, new byte[] { 6 }), 
        MAGBRONZE((byte)(-127), new float[] { 0.1f, 0.125f, 0.05f, 0.0f, 0.0f }, new byte[] { 3 }), 
        MAGADAMANTINE((byte)(-125), new float[] { 0.05f, 0.1f, 0.05f, 0.025f, 0.0f }, new byte[] { 5 }), 
        MAGGLIMMERSTEEL((byte)(-124), new float[] { 0.1f, 0.05f, 0.0f, 0.0f, 0.0f }, new byte[] { 3 }), 
        MAGGOLD((byte)(-123), new float[] { 0.05f, 0.05f, 0.025f, 0.0f, 0.0f }, new byte[] { 6 }), 
        MAGSILVER((byte)(-122), new float[] { 0.05f, 0.1f, 0.05f, 0.025f, 0.0f }, new byte[] { 4 }), 
        MAGSTEEL((byte)(-121), new float[] { 0.05f, 0.025f, 0.0f, 0.0f, 0.0f }, new byte[] { 5 }), 
        MAGCOPPER((byte)(-120), new float[] { 0.1f, 0.15f, 0.05f, 0.0f, 0.0f }, new byte[] { 6 }), 
        MAGLEAD((byte)(-118), new float[] { 0.05f, 0.1f, 0.05f, 0.025f, 0.0f }, new byte[] { 6 }), 
        MAGZINC((byte)(-117), new float[] { 0.05f, 0.025f, 0.0f, 0.0f, 0.0f }, new byte[] { 5 }), 
        MAGSERYLL((byte)(-116), new float[] { 0.025f, 0.05f, 0.1f, 0.125f, 0.075f }, new byte[] { 6 }), 
        FOBRASS((byte)(-115), new float[] { 0.05f, 0.05f, 0.025f, 0.0f, 0.0f }, new byte[] { 3 }), 
        FOBRONZE((byte)(-114), new float[] { 0.05f, 0.1f, 0.125f, 0.05f, 0.025f }, new byte[] { 5 }), 
        FOTIN((byte)(-113), new float[] { 0.1f, 0.125f, 0.05f, 0.0f, 0.0f }, new byte[] { 6 }), 
        FOADAMANTINE((byte)(-112), new float[] { 0.05f, 0.1f, 0.05f, 0.025f, 0.0f }, new byte[] { 6 }), 
        FOGLIMMERSTEEL((byte)(-111), new float[] { 0.025f, 0.0f, 0.0f, 0.0f, 0.0f }, new byte[] { 3 }), 
        FOGOLD((byte)(-110), new float[] { 0.05f, 0.05f, 0.025f, 0.0f, 0.0f }, new byte[] { 3 }), 
        FOSILVER((byte)(-109), new float[] { 0.025f, 0.0f, 0.0f, 0.0f, 0.0f }, new byte[] { 3 }), 
        FOSTEEL((byte)(-108), new float[] { 0.1f, 0.125f, 0.05f, 0.025f, 0.0f }, new byte[] { 6 }), 
        FOLEAD((byte)(-105), new float[] { 0.025f, 0.05f, 0.025f, 0.0f, 0.0f }, new byte[] { 3 }), 
        FOSERYLL((byte)(-103), new float[] { 0.025f, 0.05f, 0.1f, 0.125f, 0.075f }, new byte[] { 6 }), 
        VYNBRASS((byte)(-102), new float[] { 0.05f, 0.1f, 0.05f, 0.025f, 0.0f }, new byte[] { 6 }), 
        VYNBRONZE((byte)(-101), new float[] { 0.05f, 0.1f, 0.125f, 0.05f, 0.025f }, new byte[] { 5 }), 
        VYNTIN((byte)(-100), new float[] { 0.025f, 0.05f, 0.1f, 0.1f, 0.05f }, new byte[] { 6 }), 
        VYNADAMANTINE((byte)(-99), new float[] { 0.05f, 0.1f, 0.05f, 0.025f, 0.0f }, new byte[] { 5 }), 
        VYNGLIMMERSTEEL((byte)(-98), new float[] { 0.025f, 0.05f, 0.1f, 0.05f, 0.0f }, new byte[] { 3 }), 
        VYNSILVER((byte)(-96), new float[] { 0.025f, 0.05f, 0.1f, 0.05f, 0.025f }, new byte[] { 4 }), 
        VYNSTEEL((byte)(-95), new float[] { 0.025f, 0.05f, 0.1f, 0.05f, 0.0f }, new byte[] { 6 }), 
        VYNCOPPER((byte)(-94), new float[] { 0.05f, 0.1f, 0.125f, 0.05f, 0.0f }, new byte[] { 6 }), 
        VYNLEAD((byte)(-92), new float[] { 0.1f, 0.05f, 0.025f, 0.0f, 0.0f }, new byte[] { 4 }), 
        VYNSERYLL((byte)(-90), new float[] { 0.025f, 0.05f, 0.1f, 0.125f, 0.075f }, new byte[] { 6 }), 
        LIBBRASS((byte)(-89), new float[] { 0.05f, 0.025f, 0.0f, 0.0f, 0.0f }, new byte[] { 3 }), 
        LIBBRONZE((byte)(-88), new float[] { 0.025f, 0.05f, 0.025f, 0.0f, 0.0f }, new byte[] { 3 }), 
        LIBTIN((byte)(-87), new float[] { 0.025f, 0.05f, 0.1f, 0.05f, 0.025f }, new byte[] { 6 }), 
        LIBADAMANTINE((byte)(-86), new float[] { 0.025f, 0.05f, 0.1f, 0.05f, 0.0f }, new byte[] { 6 }), 
        LIBGLIMMERSTEEL((byte)(-85), new float[] { 0.025f, 0.05f, 0.1f, 0.05f, 0.0f }, new byte[] { 3 }), 
        LIBGOLD((byte)(-84), new float[] { 0.025f, 0.05f, 0.025f, 0.0f, 0.0f }, new byte[] { 3 }), 
        LIBSILVER((byte)(-83), new float[] { 0.025f, 0.05f, 0.1f, 0.05f, 0.025f }, new byte[] { 4 }), 
        LIBSTEEL((byte)(-82), new float[] { 0.025f, 0.05f, 0.025f, 0.0f, 0.0f }, new byte[] { 5 }), 
        LIBLEAD((byte)(-79), new float[] { 0.025f, 0.05f, 0.025f, 0.0f, 0.0f }, new byte[] { 3 }), 
        LIBZINC((byte)(-78), new float[] { 0.05f, 0.1f, 0.05f, 0.025f, 0.0f }, new byte[] { 6 }), 
        LIBSERYLL((byte)(-77), new float[] { 0.025f, 0.05f, 0.1f, 0.125f, 0.075f }, new byte[] { 6 }), 
        JACKALBRASS((byte)(-76), new float[] { 0.025f, 0.01f, 0.0f, 0.0f, 0.0f }, new byte[] { 6 }), 
        JACKALGLIMMERSTEEL((byte)(-72), new float[] { 0.025f, 0.01f, 0.0f, 0.0f, 0.0f }, new byte[] { 3 }), 
        JACKALGOLD((byte)(-71), new float[] { 0.025f, 0.05f, 0.025f, 0.0f, 0.0f }, new byte[] { 6 }), 
        JACKALSILVER((byte)(-70), new float[] { 0.025f, 0.05f, 0.025f, 0.0f, 0.0f }, new byte[] { 4 }), 
        JACKALSTEEL((byte)(-69), new float[] { 0.025f, 0.05f, 0.1f, 0.05f, 0.01f }, new byte[] { 6 }), 
        JACKALCOPPER((byte)(-68), new float[] { 0.05f, 0.1f, 0.125f, 0.05f, 0.0f }, new byte[] { 4 }), 
        JACKALIRON((byte)(-67), new float[] { 0.025f, 0.01f, 0.0f, 0.0f, 0.0f }, new byte[] { 3 }), 
        JACKALLEAD((byte)(-66), new float[] { 0.025f, 0.0f, 0.0f, 0.0f, 0.0f }, new byte[] { 6 }), 
        JACKALZINC((byte)(-65), new float[] { 0.025f, 0.05f, 0.025f, 0.0f, 0.0f }, new byte[] { 5 }), 
        JACKALSERYLL((byte)(-64), new float[] { 0.025f, 0.05f, 0.1f, 0.125f, 0.075f }, new byte[] { 6 }), 
        UNKBRASS((byte)(-63), new float[] { 0.0f, 0.01f, 0.025f, 0.05f, 0.1f }, new byte[] { 6 }), 
        UNKBRONZE((byte)(-62), new float[] { 0.0f, 0.01f, 0.025f, 0.05f, 0.1f }, new byte[] { 6 }), 
        UNKTIN((byte)(-61), new float[] { 0.0f, 0.01f, 0.025f, 0.05f, 0.1f }, new byte[] { 6 }), 
        UNKADAMANTINE((byte)(-60), new float[] { 0.0f, 0.01f, 0.025f, 0.05f, 0.1f }, new byte[] { 6 }), 
        UNKGLIMMERSTEEL((byte)(-59), new float[] { 0.0f, 0.01f, 0.025f, 0.05f, 0.1f }, new byte[] { 3 }), 
        UNKGOLD((byte)(-58), new float[] { 0.0f, 0.01f, 0.025f, 0.05f, 0.1f }, new byte[] { 6 }), 
        UNKSILVER((byte)(-57), new float[] { 0.0f, 0.01f, 0.025f, 0.05f, 0.1f }, new byte[] { 4 }), 
        UNKSTEEL((byte)(-56), new float[] { 0.0f, 0.01f, 0.025f, 0.05f, 0.1f }, new byte[] { 6 }), 
        UNKCOPPER((byte)(-55), new float[] { 0.0f, 0.01f, 0.025f, 0.05f, 0.1f }, new byte[] { 6 }), 
        UNKIRON((byte)(-54), new float[] { 0.0f, 0.01f, 0.025f, 0.05f, 0.1f }, new byte[] { 6 }), 
        UNKLEAD((byte)(-53), new float[] { 0.0f, 0.01f, 0.025f, 0.05f, 0.1f }, new byte[] { 3 }), 
        UNKZINC((byte)(-52), new float[] { 0.0f, 0.01f, 0.025f, 0.05f, 0.1f }, new byte[] { 6 }), 
        UNKSERYLL((byte)(-51), new float[] { 0.0f, 0.0f, 0.025f, 0.075f, 0.15f }, new byte[] { 6 });
        
        private final byte enchantment;
        private final byte[] itemClass;
        private final float[] levelChances;
        
        private FragmentEnchantment(final byte enchantment, final float... levelChances, final byte[] itemClass) {
            this.enchantment = enchantment;
            this.itemClass = itemClass;
            this.levelChances = levelChances;
        }
        
        byte getEnchantment() {
            return this.enchantment;
        }
        
        static FragmentEnchantment getRandomEnchantment(final byte itemClass, final int level) {
            float totalChance = 0.0f;
            for (final FragmentEnchantment f : values()) {
                for (final byte b : f.itemClass) {
                    if (b == itemClass || b == 6) {
                        totalChance += f.levelChances[level];
                    }
                }
            }
            final float winningVal = Server.rand.nextFloat() * totalChance;
            float thisVal = 0.0f;
            for (final FragmentEnchantment f2 : values()) {
                for (final byte b2 : f2.itemClass) {
                    if (b2 == itemClass || b2 == 6) {
                        thisVal += f2.levelChances[level];
                        if (winningVal < thisVal) {
                            return f2;
                        }
                    }
                }
            }
            return null;
        }
    }
}
